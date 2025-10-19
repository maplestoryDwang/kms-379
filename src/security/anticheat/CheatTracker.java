package security.anticheat;

import constants.GameConstants;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import network.center.Center;
import network.models.CWvsContext;
import objects.users.MapleCharacter;
import objects.users.MapleCharacterUtil;
import objects.users.skills.SkillFactory;
import objects.utils.AutobanManager;
import objects.utils.FileoutputUtil;
import objects.utils.StringUtil;
import objects.utils.Timer;

public class CheatTracker {
   private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
   private final Lock rL = this.lock.readLock();
   private final Lock wL = this.lock.writeLock();
   private final Map<CheatingOffense, CheatingOffenseEntry> offenses = new LinkedHashMap<>();
   private WeakReference<MapleCharacter> chr;
   private long lastAttackTime = 0L;
   private int lastAttackTickCount = 0;
   private byte Attack_tickResetCount = 0;
   private long Server_ClientAtkTickDiff = 0L;
   private long lastDamage = 0L;
   private long takingDamageSince;
   private int numSequentialDamage = 0;
   private long lastDamageTakenTime = 0L;
   private byte numZeroDamageTaken = 0;
   private int numSequentialSummonAttack = 0;
   private long summonSummonTime = 0L;
   private int numSameDamage = 0;
   private Point lastMonsterMove;
   private int monsterMoveCount;
   private int attacksWithoutHit = 0;
   private byte dropsPerSecond = 0;
   private long lastDropTime = 0L;
   private byte msgsPerSecond = 0;
   private long lastMsgTime = 0L;
   private ScheduledFuture<?> invalidationTask;
   private int gm_message = 0;
   private int lastTickCount = 0;
   private int tickSame = 0;
   private long lastSmegaTime = 0L;
   private long lastBBSTime = 0L;
   private long lastASmegaTime = 0L;

   public CheatTracker(MapleCharacter chr) {
      this.start(chr);
   }

   public final void checkAttack(int skillId, int tickcount) {
      int AtkDelay = GameConstants.getAttackDelay(skillId, skillId == 0 ? null : SkillFactory.getSkill(skillId));
      if (tickcount - this.lastAttackTickCount < AtkDelay) {
         this.registerOffense(CheatingOffense.FASTATTACK);
      }

      this.lastAttackTime = System.currentTimeMillis();
      if (this.chr.get() != null && this.lastAttackTime - this.chr.get().getChangeTime() > 600000L) {
         this.chr.get().setChangeTime();
      }

      long STime_TC = this.lastAttackTime - tickcount;
      if (this.Server_ClientAtkTickDiff - STime_TC > 1000L) {
         this.registerOffense(CheatingOffense.FASTATTACK2);
      }

      this.Attack_tickResetCount++;
      if (this.Attack_tickResetCount >= (AtkDelay <= 200 ? 1 : 4)) {
         this.Attack_tickResetCount = 0;
         this.Server_ClientAtkTickDiff = STime_TC;
      }

      this.lastAttackTickCount = tickcount;
   }

   public final void checkPVPAttack(int skillId) {
      int AtkDelay = GameConstants.getAttackDelay(skillId, skillId == 0 ? null : SkillFactory.getSkill(skillId));
      long STime_TC = System.currentTimeMillis() - this.lastAttackTime;
      if (STime_TC < AtkDelay) {
         this.registerOffense(CheatingOffense.FASTATTACK);
      }

      this.lastAttackTime = System.currentTimeMillis();
   }

   public final long getLastAttack() {
      return this.lastAttackTime;
   }

   public final void checkTakeDamage(int damage) {
      this.numSequentialDamage++;
      this.lastDamageTakenTime = System.currentTimeMillis();
      if (this.lastDamageTakenTime - this.takingDamageSince / 500L < this.numSequentialDamage) {
         this.registerOffense(CheatingOffense.FAST_TAKE_DAMAGE);
      }

      if (this.lastDamageTakenTime - this.takingDamageSince > 4500L) {
         this.takingDamageSince = this.lastDamageTakenTime;
         this.numSequentialDamage = 0;
      }

      if (damage == 0) {
         this.numZeroDamageTaken++;
         if (this.numZeroDamageTaken >= 35) {
            this.numZeroDamageTaken = 0;
            this.registerOffense(CheatingOffense.HIGH_AVOID);
         }
      } else if (damage != -1) {
         this.numZeroDamageTaken = 0;
      }
   }

   public final void checkSameDamage(Long eachd, double expected) {
      if (eachd > 2000L && this.lastDamage == eachd && this.chr.get() != null && (this.chr.get().getLevel() < 175 || eachd.longValue() > expected * 2.0)) {
         this.numSameDamage++;
         if (this.numSameDamage > 5) {
            this.registerOffense(
               CheatingOffense.SAME_DAMAGE,
               this.numSameDamage
                  + " times, damage "
                  + eachd
                  + ", expected "
                  + expected
                  + " [Level: "
                  + this.chr.get().getLevel()
                  + ", Job: "
                  + this.chr.get().getJob()
                  + "]"
            );
            this.numSameDamage = 0;
         }
      } else {
         this.lastDamage = eachd;
         this.numSameDamage = 0;
      }
   }

   public final void checkMoveMonster(Point pos) {
      if (pos == this.lastMonsterMove) {
         this.monsterMoveCount++;
         if (this.monsterMoveCount > 10) {
            this.registerOffense(CheatingOffense.MOVE_MONSTERS, "Position: " + pos.x + ", " + pos.y);
            this.monsterMoveCount = 0;
         }
      } else {
         this.lastMonsterMove = pos;
         this.monsterMoveCount = 1;
      }
   }

   public final void resetSummonAttack() {
      this.summonSummonTime = System.currentTimeMillis();
      this.numSequentialSummonAttack = 0;
   }

   public final boolean checkSummonAttack() {
      this.numSequentialSummonAttack++;
      if ((System.currentTimeMillis() - this.summonSummonTime) / 1001L < this.numSequentialSummonAttack) {
         this.registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
         return false;
      } else {
         return true;
      }
   }

   public final void checkDrop() {
      this.checkDrop(false);
   }

   public final void checkDrop(boolean dc) {
      if (System.currentTimeMillis() - this.lastDropTime < 1000L) {
         this.dropsPerSecond++;
         if (this.dropsPerSecond >= (dc ? 32 : 16) && this.chr.get() != null && !this.chr.get().isGM()) {
            if (dc) {
               this.chr.get().getClient().getSession().close();
               System.out.println("팅겼다고인마");
            } else {
               this.chr.get().getClient().setMonitored(true);
            }
         }
      } else {
         this.dropsPerSecond = 0;
      }

      this.lastDropTime = System.currentTimeMillis();
   }

   public final void checkMsg() {
      if (System.currentTimeMillis() - this.lastMsgTime < 1000L) {
         this.msgsPerSecond++;
         if (this.msgsPerSecond > 10 && this.chr.get() != null && !this.chr.get().isGM()) {
            this.chr.get().getClient().getSession().close();
            System.out.println("팅겼다고인마");
         }
      } else {
         this.msgsPerSecond = 0;
      }

      this.lastMsgTime = System.currentTimeMillis();
   }

   public final int getAttacksWithoutHit() {
      return this.attacksWithoutHit;
   }

   public final void setAttacksWithoutHit(boolean increase) {
      if (increase) {
         this.attacksWithoutHit++;
      } else {
         this.attacksWithoutHit = 0;
      }
   }

   public final void registerOffense(CheatingOffense offense) {
      this.registerOffense(offense, null);
   }

   public final void registerOffense(CheatingOffense offense, String param) {
      MapleCharacter chrhardref = this.chr.get();
      if (chrhardref != null && offense.isEnabled() && !chrhardref.isClone() && !chrhardref.isGM()) {
         CheatingOffenseEntry entry = null;
         this.rL.lock();

         try {
            entry = this.offenses.get(offense);
         } finally {
            this.rL.unlock();
         }

         if (entry != null && entry.isExpired()) {
            this.expireEntry(entry);
            entry = null;
            this.gm_message = 0;
         }

         if (entry == null) {
            entry = new CheatingOffenseEntry(offense, chrhardref.getId());
         }

         if (param != null) {
            entry.setParam(param);
         }

         entry.incrementCount();
         if (offense.shouldAutoban(entry.getCount())) {
            byte type = offense.getBanType();
            if (type == 1) {
               AutobanManager.getInstance().autoban(chrhardref.getClient(), StringUtil.makeEnumHumanReadable(offense.name()));
            } else if (type == 2) {
               chrhardref.getClient().getSession().close();
               System.out.println("팅겼다고인마");
            }

            this.gm_message = 0;
         } else {
            this.wL.lock();

            try {
               this.offenses.put(offense, entry);
            } finally {
               this.wL.unlock();
            }

            switch (offense) {
               case HIGH_DAMAGE_MAGIC_2:
               case HIGH_DAMAGE_2:
               case ATTACK_FARAWAY_MONSTER:
               case ATTACK_FARAWAY_MONSTER_SUMMON:
               case SAME_DAMAGE:
                  this.gm_message++;
                  if (this.gm_message % 100 == 0) {
                     Center.Broadcast.broadcastGMMessage(
                        CWvsContext.serverNotice(
                           6,
                           "[GM Message] "
                              + MapleCharacterUtil.makeMapleReadable(chrhardref.getName())
                              + " (level "
                              + chrhardref.getLevel()
                              + ") suspected of hacking! "
                              + StringUtil.makeEnumHumanReadable(offense.name())
                              + (param == null ? "" : " - " + param)
                        )
                     );
                  }

                  if (this.gm_message >= 300 && chrhardref.getLevel() < (offense == CheatingOffense.SAME_DAMAGE ? 175 : 150)) {
                     Timestamp created = chrhardref.getClient().getCreated();
                     long time = System.currentTimeMillis();
                     if (created != null) {
                        time = created.getTime();
                     }

                     if (time + 1296000000L >= System.currentTimeMillis()) {
                        AutobanManager.getInstance()
                           .autoban(
                              chrhardref.getClient(),
                              StringUtil.makeEnumHumanReadable(offense.name()) + " over 500 times " + (param == null ? "" : " - " + param)
                           );
                     } else {
                        this.gm_message = 0;
                        Center.Broadcast.broadcastGMMessage(
                           CWvsContext.serverNotice(
                              6,
                              "[GM Message] "
                                 + MapleCharacterUtil.makeMapleReadable(chrhardref.getName())
                                 + " (level "
                                 + chrhardref.getLevel()
                                 + ") suspected of autoban! "
                                 + StringUtil.makeEnumHumanReadable(offense.name())
                                 + (param == null ? "" : " - " + param)
                           )
                        );
                        FileoutputUtil.log(
                           "Log_Hacker.rtf",
                           "[GM Message] "
                              + MapleCharacterUtil.makeMapleReadable(chrhardref.getName())
                              + " (level "
                              + chrhardref.getLevel()
                              + ") suspected of autoban! "
                              + StringUtil.makeEnumHumanReadable(offense.name())
                              + (param == null ? "" : " - " + param)
                        );
                     }
                  }
               default:
                  CheatingOffensePersister.getInstance().persistEntry(entry);
            }
         }
      }
   }

   public boolean canSmega() {
      if (this.lastSmegaTime + 10000L > System.currentTimeMillis() && this.chr.get() != null && !this.chr.get().isGM()) {
         return false;
      } else {
         this.lastSmegaTime = System.currentTimeMillis();
         return true;
      }
   }

   public boolean canAvatarSmega() {
      if (this.lastASmegaTime + 300000L > System.currentTimeMillis() && this.chr.get() != null && !this.chr.get().isGM()) {
         return false;
      } else {
         this.lastASmegaTime = System.currentTimeMillis();
         return true;
      }
   }

   public boolean canBBS() {
      if (this.lastBBSTime + 60000L > System.currentTimeMillis() && this.chr.get() != null && !this.chr.get().isGM()) {
         return false;
      } else {
         this.lastBBSTime = System.currentTimeMillis();
         return true;
      }
   }

   public final void expireEntry(CheatingOffenseEntry coe) {
      this.wL.lock();

      try {
         this.offenses.remove(coe.getOffense());
      } finally {
         this.wL.unlock();
      }
   }

   public final int getPoints() {
      int ret = 0;
      this.rL.lock();

      CheatingOffenseEntry[] offenses_copy;
      try {
         offenses_copy = this.offenses.values().toArray(new CheatingOffenseEntry[this.offenses.size()]);
      } finally {
         this.rL.unlock();
      }

      for (CheatingOffenseEntry entry : offenses_copy) {
         if (entry.isExpired()) {
            this.expireEntry(entry);
         } else {
            ret += entry.getPoints();
         }
      }

      return ret;
   }

   public final Map<CheatingOffense, CheatingOffenseEntry> getOffenses() {
      return Collections.unmodifiableMap(this.offenses);
   }

   public final String getSummary() {
      StringBuilder ret = new StringBuilder();
      List<CheatingOffenseEntry> offenseList = new ArrayList<>();
      this.rL.lock();

      try {
         for (CheatingOffenseEntry entry : this.offenses.values()) {
            if (!entry.isExpired()) {
               offenseList.add(entry);
            }
         }
      } finally {
         this.rL.unlock();
      }

      Collections.sort(offenseList, new Comparator<CheatingOffenseEntry>() {
         public final int compare(CheatingOffenseEntry o1, CheatingOffenseEntry o2) {
            int thisVal = o1.getPoints();
            int anotherVal = o2.getPoints();
            return thisVal < anotherVal ? 1 : (thisVal == anotherVal ? 0 : -1);
         }
      });
      int var8 = Math.min(offenseList.size(), 4);

      for (int var9 = 0; var9 < var8; var9++) {
         ret.append(StringUtil.makeEnumHumanReadable(offenseList.get(var9).getOffense().name()));
         ret.append(": ");
         ret.append(offenseList.get(var9).getCount());
         if (var9 != var8 - 1) {
            ret.append(" ");
         }
      }

      return ret.toString();
   }

   public final void dispose() {
      if (this.invalidationTask != null) {
         this.invalidationTask.cancel(false);
      }

      this.invalidationTask = null;
      this.chr = new WeakReference<>(null);
   }

   public final void start(MapleCharacter chr) {
      this.chr = new WeakReference<>(chr);
      this.invalidationTask = Timer.CheatTimer.getInstance().register(new CheatTracker.InvalidationTask(), 60000L);
      this.takingDamageSince = System.currentTimeMillis();
   }

   private final class InvalidationTask implements Runnable {
      @Override
      public final void run() {
         CheatTracker.this.rL.lock();

         CheatingOffenseEntry[] offenses_copy;
         try {
            offenses_copy = CheatTracker.this.offenses.values().toArray(new CheatingOffenseEntry[CheatTracker.this.offenses.size()]);
         } finally {
            CheatTracker.this.rL.unlock();
         }

         for (CheatingOffenseEntry offense : offenses_copy) {
            if (offense.isExpired()) {
               CheatTracker.this.expireEntry(offense);
            }
         }

         if (CheatTracker.this.chr.get() == null) {
            CheatTracker.this.dispose();
         }
      }
   }
}
