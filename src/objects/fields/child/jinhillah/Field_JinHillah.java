package objects.fields.child.jinhillah;

import database.DBConfig;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import logging.LoggingManager;
import logging.entry.BossLog;
import logging.entry.BossLogType;
import network.SendPacketOpcode;
import network.center.Center;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.fields.EliteState;
import objects.fields.Field;
import objects.fields.fieldset.instance.HardJinHillahBoss;
import objects.fields.fieldset.instance.HellJinHillahBoss;
import objects.fields.fieldset.instance.NormalJinHillahBoss;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.users.MapleCharacter;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class Field_JinHillah extends Field {
   public static List<List<Rect>> threadRect = new ArrayList<>();
   public static List<Point> threadPos = new ArrayList<>();
   private List<JinHillahThread> jinHillahThread = new ArrayList<>();
   private int candleSize = 0;
   private int activeCandle = 0;
   private long nextFullMapAttackTime = 0L;
   private boolean setFullMapAttackTime = false;
   private int phase = 1;
   private JinHillahAltar altar = null;
   private boolean disabledRedThread = false;
   private long enableRedThreadTime = 0L;
   private boolean firstSet = false;
   private boolean hellMode = false;

   public Field_JinHillah(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.enableRedThreadTime != 0L && this.enableRedThreadTime <= System.currentTimeMillis()) {
         this.disabledRedThread = false;
         this.enableRedThreadTime = 0L;
      }

      if (this.getNextFullMapAttackTime() == 0L) {
         this.setNextFullMapAttackTime(System.currentTimeMillis() + 145000L);
      }

      if (this.getNextFullMapAttackTime() <= System.currentTimeMillis()) {
         MapleMonster boss = this.getMonsterById(8880410);
         if (boss == null) {
            boss = this.getMonsterById(8880405);
         }

         if (boss != null) {
            boss.cancelStatus(MobTemporaryStatFlag.FREEZE);

            for (int i = 0; i < 10; i++) {
               boss.addAttackBlocked(i);
            }

            boss.broadcastAttackBlocked();

            for (int i = 0; i < 5; i++) {
               boss.addSkillFilter(i);
            }
         }

         this.setSetFullMapAttackTime(true);
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.jinHillahThread.clear();
      this.setCandleSize(0);
      this.setActiveCandle(0);
      this.setNextFullMapAttackTime(0L);
      this.firstSet = false;
      this.altar = null;
      this.setPhase(1);
      this.disabledRedThread = false;
      this.enableRedThreadTime = 0L;
      this.setFullMapAttackTime = false;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof NormalJinHillahBoss) {
         NormalJinHillahBoss fs = (NormalJinHillahBoss)this.getFieldSetInstance();
         if (!fs.isPracticeMode() && this.getId() == 450010950 && this.getMonsterById(8950120) == null && player.getCurrentBossPhase() > 0) {
            MapleMonster mob = MapleLifeFactory.getMonster(8950120);
            if (!DBConfig.isGanglim && player.isMultiMode()) {
               long hp = mob.getMobMaxHp();
               long fixedhp = hp * 3L;
               if (fixedhp < 0L) {
                  fixedhp = Long.MAX_VALUE;
               }

               mob.setHp(fixedhp);
               mob.setMaxHp(fixedhp);
            }

            this.spawnMonsterOnGroundBelow(mob, new Point(913, 206));
            this.clearCurrentPhase(player.getParty());
         }
      }

      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HardJinHillahBoss) {
         HardJinHillahBoss fs = (HardJinHillahBoss)this.getFieldSetInstance();
         if (!fs.isPracticeMode() && this.getId() == 450010550 && this.getMonsterById(8950121) == null && player.getCurrentBossPhase() > 0) {
            MapleMonster mob = MapleLifeFactory.getMonster(8950121);
            if (!DBConfig.isGanglim && player.isMultiMode()) {
               long hp = mob.getMobMaxHp();
               long fixedhp = hp * 3L;
               if (fixedhp < 0L) {
                  fixedhp = Long.MAX_VALUE;
               }

               mob.setHp(fixedhp);
               mob.setMaxHp(fixedhp);
            }

            this.spawnMonsterOnGroundBelow(mob, new Point(913, 206));
            this.clearCurrentPhase(player.getParty());
         }
      }

      if (this.getFieldSetInstance() != null
         && this.getFieldSetInstance() instanceof HellJinHillahBoss
         && this.getId() == 450010550
         && this.getMonsterById(8950121) == null) {
         this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8950121), new Point(913, 206));
      }

      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellJinHillahBoss) {
         this.hellMode = true;
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.ELITE_STATE.getValue());
         packet.writeInt(EliteState.EliteBoss.getType());
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeMapleAsciiString("Bgm41/Gravity Core");
         packet.writeMapleAsciiString("Effect/EliteMobEff.img/eliteMonsterEffect2");
         packet.writeMapleAsciiString("Effect/EventEffect.img/gloryWmission/screenEff");
         player.send(packet.getPacket());
      } else {
         this.hellMode = false;
      }

      player.setJinHillahDeathCountOut(false);
      player.initJinHillahDeathCount();
      this.sendJinHillahDeathCount(player);
      if (this.getFieldSetInstance() != null
         && this.getFieldSetInstance() instanceof NormalJinHillahBoss
         && ((NormalJinHillahBoss)this.getFieldSetInstance()).isPracticeMode) {
         player.setBossMode(1);
      }

      if (this.getFieldSetInstance() != null
         && this.getFieldSetInstance() instanceof HardJinHillahBoss
         && ((HardJinHillahBoss)this.getFieldSetInstance()).isPracticeMode) {
         player.setBossMode(1);
      }

      if (!this.firstSet) {
         Party party = player.getParty();
         if (party != null) {
            int totalDeathCount = 0;

            for (PartyMemberEntry pUser : party.getPartyMemberList()) {
               MapleCharacter pChr = player.getMap().getCharacterById(pUser.getId());
               if (pChr != null) {
                  totalDeathCount = (int)(
                     totalDeathCount + pChr.getJinHillahDeathCount().stream().filter(dc -> dc.status == Field_JinHillah.JinHillahDeathCountType.Green).count()
                  );
               }
            }

            this.candleSize = (int)Math.ceil(totalDeathCount / 2.0);
            this.activeCandle = 0;
         }

         this.firstSet = true;
      }

      Party party = player.getParty();
      if (party != null) {
         for (PartyMemberEntry mpc : party.getPartyMemberList()) {
            this.updatePartyMemberDeathCount(player, false);
         }
      }

      this.sendJinHillahSetCandle(player);
      this.sendJinHillahSandglass(player, 147000, 247, 1);
      if (this.getId() != 450010950 && this.getId() != 450010550) {
         SecondaryStatEffect eff = SkillFactory.getSkill(80002543).getEffect(1);
         if (eff != null) {
            eff.applyTo(player);
         }
      } else {
         player.temporaryStatResetBySkillID(80002543);
      }
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
      player.temporaryStatResetBySkillID(80002543);
      player.setNextDebuffIncHPTime(0L);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellJinHillahBoss) {
         this.hellMode = true;
         mob.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + System.currentTimeMillis(), null, 0);
         if (mob.getId() == 8880410 || mob.getId() == 8880405) {
            MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
            e.setCancelTask(5000L);
            mob.applyStatus(e);
         }
      } else {
         this.hellMode = false;
      }

      if (DBConfig.isGanglim
         && (
            this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HardJinHillahBoss
               || this.getFieldSetInstance() instanceof NormalJinHillahBoss
         )
         && (mob.getId() == 8880410 || mob.getId() == 8880405)) {
         MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
         e.setCancelTask(0L);
         mob.applyStatus(e);
      }

      if (mob.getId() == 8880410 || mob.getId() == 8880405) {
         mob.addSkillFilter(0);
         mob.addSkillFilter(1);
         mob.addSkillFilter(2);
         mob.addSkillFilter(4);
         mob.addAttackBlocked(3);
         mob.addAttackBlocked(4);
         mob.addAttackBlocked(6);
         mob.addAttackBlocked(7);
         mob.broadcastAttackBlocked();
      }
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
      if (this.getPhase() == 1 && mob.getHPPercent() <= 75) {
         mob.removeAttackBlocked(3);
         mob.removeAttackBlocked(4);
         mob.removeAttackBlocked(6);
         mob.removeAttackBlocked(7);
         mob.addAttackBlocked(0);
         mob.addAttackBlocked(1);
         mob.addAttackBlocked(5);
         mob.broadcastAttackBlocked();
         this.setPhase(2);
      } else if (this.getPhase() == 2 && mob.getHPPercent() <= 50) {
         mob.removeSkillFilter(0);
         this.setPhase(3);
      } else if (this.getPhase() == 3 && mob.getHPPercent() <= 25) {
         mob.removeSkillFilter(1);
         this.setPhase(4);
      }
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      if (mob.getId() == 8880410 || mob.getId() == 8880405) {
         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            if (player != null) {
               player.temporaryStatResetBySkillID(80002543);
               if (this.getId() == 450010930) {
                  player.setRegisterTransferField(this.getId() + 20);
               } else {
                  player.setRegisterTransferField(this.getId() + 50);
               }

               player.setRegisterTransferFieldTime(System.currentTimeMillis() + 6000L);
            }
         }
      }

      if (mob.getId() == 8950120 || mob.getId() == 8950121) {
         for (MapleMonster m : this.getAllMonstersThreadsafe()) {
            this.removeMonster(m, 1);
         }

         boolean set = false;

         for (MapleCharacter p : this.getCharactersThreadsafe()) {
            if (p.getBossMode() == 1) {
               return;
            }

            if (p.getParty() != null) {
               if (p.getMapId() == this.getId()) {
                  int quantity = 3;
                  p.gainItem(4001894, (short)quantity, false, -1L, "진 힐라 격파로 얻은 아이템");
               }

               if (!set) {
                  if (mob.getId() != 8950120) {
                     String list = "";
                     List<String> names = new ArrayList<>();

                     for (PartyMemberEntry mpc : new ArrayList<>(p.getParty().getPartyMemberList())) {
                        names.add(mpc.getName());
                     }

                     list = String.join(",", names);

                     for (PartyMemberEntry mpc : new ArrayList<>(p.getParty().getPartyMemberList())) {
                        StringBuilder sb = new StringBuilder("보스 하드 진 힐라 격파 (" + list + ")");
                        MapleCharacter playerx = this.getCharacterById(mpc.getId());
                        if (playerx != null) {
                           LoggingManager.putLog(new BossLog(playerx, BossLogType.ClearLog.getType(), sb));
                        }
                     }

                     Center.Broadcast.broadcastMessageCheckQuest(
                        CField.chatMsg(
                           DBConfig.isGanglim ? 8 : 22,
                           "[보스격파] [CH."
                              + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                              + "] '"
                              + p.getParty().getPartyMember().getLeader().getName()
                              + "' 파티("
                              + list
                              + ")가 [하드 진 힐라]를 격파하였습니다."
                        ),
                        "BossMessage"
                     );
                  }

                  if (DBConfig.isGanglim) {
                     this.bossClear(8950121, 1234569, "jinhillah_clear");
                  } else {
                     this.bossClear(8950121, 1234569, "jinhillah_clear");
                     boolean single = this.getCharactersSize() == 1;
                     this.bossClear(8950121, 1234569, "jinhillah_clear_" + (single ? "single" : "multi"));
                  }

                  set = true;
               }
            }
         }
      }
   }

   @Override
   public void onPlayerDead(MapleCharacter player) {
      for (Field_JinHillah.JinHillahDeathCount dc : new ArrayList<>(player.getJinHillahDeathCount())) {
         if (dc.getStatus() == Field_JinHillah.JinHillahDeathCountType.Green) {
            dc.setStatus(Field_JinHillah.JinHillahDeathCountType.Out);
            break;
         }
      }

      this.sendJinHillahDeathCount(player);
   }

   public void sendJinHillahNotice(String message, int time) {
      this.broadcastMessage(CField.sendWeatherEffectNotice(254, time, false, message));
   }

   public void sendJinHillahSetCandle(MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.JINHILLAH_RESULT.getValue());
      packet.writeInt(Field_JinHillah.JinHillahResultType.CandleSet.getType());
      packet.writeInt(this.candleSize);
      packet.write(0);
      player.send(packet.getPacket());
   }

   public void sendJinHillahResetCandle() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.JINHILLAH_RESULT.getValue());
      packet.writeInt(Field_JinHillah.JinHillahResultType.CandleSet.getType());
      packet.writeInt(this.candleSize);
      packet.write(1);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendJinHillahUpdateCandle() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.JINHILLAH_RESULT.getValue());
      packet.writeInt(Field_JinHillah.JinHillahResultType.UpdateCandle.getType());
      packet.writeInt(this.activeCandle);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendJinHillahSandglass(MapleCharacter player, int time, int skillID, int skillLevel) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.JINHILLAH_RESULT.getValue());
      packet.writeInt(Field_JinHillah.JinHillahResultType.Sandglass.getType());
      packet.writeInt(time);
      packet.writeInt(skillID);
      packet.writeInt(skillLevel);
      if (player != null) {
         player.send(packet.getPacket());
      } else {
         this.broadcastMessage(packet.getPacket());
      }
   }

   public void sendJinHillahDeathCount(MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.JINHILLAH_RESULT.getValue());
      packet.writeInt(Field_JinHillah.JinHillahResultType.DeathCount.getType());
      List<Field_JinHillah.JinHillahDeathCount> deathCount = player.getJinHillahDeathCount();
      packet.writeInt(deathCount.size());

      for (Field_JinHillah.JinHillahDeathCount dc : deathCount) {
         dc.encode(packet);
      }

      player.send(packet.getPacket());
   }

   public void createJinHillahAltar(int x, int y, int count) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.JINHILLAH_RESULT.getValue());
      packet.writeInt(Field_JinHillah.JinHillahResultType.Altar.getType());
      this.altar = new JinHillahAltar(new Point(x, y), count);
      this.altar.setMaxKeyInput(count);
      this.altar.encode(packet);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendJinHillahUpdateAltarStatus() {
      if (this.altar != null) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.JINHILLAH_RESULT.getValue());
         packet.writeInt(Field_JinHillah.JinHillahResultType.UpdateAltarStatus.getType());
         packet.writeInt(Math.min(30, this.altar.getMaxKeyInput() - this.altar.getRemainKeyInput()));
         this.broadcastMessage(packet.getPacket());
         this.altar.setRemainKeyInput(this.altar.getRemainKeyInput() - 1);
         if (this.altar.getRemainKeyInput() <= 0) {
            this.altar = null;
            this.sendJinHillahRemoveAltar();
            this.activeCandle = 0;
            this.sendJinHillahResetCandle();

            for (MapleCharacter player : this.getCharactersThreadsafe()) {
               player.resetJinHillahDeathCount();
               player.temporaryStatResetBySkillID(80002543);
               player.setNextDebuffIncHPTime(System.currentTimeMillis() + 5000L);
            }
         }
      }
   }

   public void sendJinHillahRemoveAltar() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.JINHILLAH_RESULT.getValue());
      packet.writeInt(Field_JinHillah.JinHillahResultType.RemoveAltar.getType());
      packet.writeInt(0);
      this.broadcastMessage(packet.getPacket());
   }

   public void updatePartyMemberDeathCount(MapleCharacter player, boolean broadCast) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.JINHILLAH_RESULT.getValue());
      packet.writeInt(Field_JinHillah.JinHillahResultType.PartyDeathCount.getType());
      packet.writeInt(1);
      packet.writeInt(player.getId());
      int deathCount = 5;

      for (Field_JinHillah.JinHillahDeathCount dc : new ArrayList<>(player.getJinHillahDeathCount())) {
         if (dc.getStatus() == Field_JinHillah.JinHillahDeathCountType.Out) {
            deathCount--;
         }
      }

      packet.writeInt(deathCount);
      if (broadCast) {
         this.broadcastMessage(packet.getPacket());
      } else {
         player.send(packet.getPacket());
      }
   }

   public void createJinHillahCreateThread(int mobObjectID, int skillLevel) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.JINHILLAH_CREATE_THREAD.getValue());
      packet.writeInt(mobObjectID);
      List<JinHillahThread> threads = new ArrayList<>();
      List<Point> posList = new ArrayList<>(threadPos);
      Collections.shuffle(posList);

      for (int i = 1; i <= posList.size(); i++) {
         JinHillahThread thread = new JinHillahThread(246, skillLevel, 250 * i, posList.get(i - 1));
         threads.add(thread);
      }

      packet.writeInt(threads.size());

      for (JinHillahThread t : threads) {
         t.encode(packet);
      }

      this.broadcastMessage(packet.getPacket());
   }

   public void jinHillahGrabSoul(int objectID, int cid, Rectangle rect, Point pos, int skillLv) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.JINHILLAH_GRAB_SOUL.getValue());
      o.writeInt(objectID);
      o.writeInt(cid);
      o.writeInt(rect.x);
      o.writeInt(rect.y);
      o.writeInt(rect.width);
      o.writeInt(rect.height);
      o.encodePos(pos);
      o.writeInt(1);
      o.writeInt(246);
      o.writeInt(skillLv);
      o.write(true);
      o.writeInt(1);
      o.encodePos(new Point(280 * Randomizer.rand(-3, 3), -260));
      int z = Randomizer.rand(4, 6);
      o.writeInt(250 * z);
      o.writeInt(0);
      o.writeInt(0);
      List<Rectangle> rectz = new ArrayList<>();

      for (int j = 0; j < z; j++) {
         rectz.add(new Rectangle(Randomizer.rand(-100, 150), -80, Randomizer.rand(10, 15) * 5, 640));
      }

      o.writeInt(rectz.size());

      for (Rectangle recta : rectz) {
         o.encodeRect(recta);
      }

      this.broadcastMessage(o.getPacket());
   }

   public void updateJinHillahCandle() {
      this.activeCandle++;
      this.sendJinHillahUpdateCandle();
      this.checkJinHillahAltar();
   }

   public void checkJinHillahAltar() {
      if (this.activeCandle >= this.candleSize) {
         int x = Randomizer.rand(-700, 700);
         int y = 268;
         this.sendJinHillahNotice("힐라가 접근하여 사라지기 전에 제단에서 채집키를 연타하여 영혼을 회수해야 한다.", 6000);
         int count = Math.min(30, 5 + Math.min(5, this.getCharactersThreadsafe().size()) * 5);
         this.createJinHillahAltar(x, y, count);
      }
   }

   public void resetJinHillahCandle() {
      int count = 0;

      for (MapleCharacter player : this.getCharactersThreadsafe()) {
         if (!player.isJinHillahDeathCountOut()) {
            for (Field_JinHillah.JinHillahDeathCount dc : player.getJinHillahDeathCount()) {
               if (dc.status == Field_JinHillah.JinHillahDeathCountType.Green) {
                  count++;
               }
            }
         }
      }

      if (count > 0) {
         this.candleSize = (int)Math.ceil(count / 2.0);
         this.activeCandle = 0;
         this.sendJinHillahResetCandle();
         this.sendJinHillahUpdateCandle();
      }
   }

   public int getCandleSize() {
      return this.candleSize;
   }

   public void setCandleSize(int candleSize) {
      this.candleSize = candleSize;
   }

   public int getActiveCandle() {
      return this.activeCandle;
   }

   public void setActiveCandle(int activeCandle) {
      this.activeCandle = activeCandle;
   }

   public int getPhase() {
      return this.phase;
   }

   public void setPhase(int phase) {
      this.phase = phase;
   }

   public boolean isDisabledRedThread() {
      return this.disabledRedThread;
   }

   public void setDisabledRedThread(boolean disabledRedThread) {
      this.disabledRedThread = disabledRedThread;
   }

   public long getEnableRedThreadTime() {
      return this.enableRedThreadTime;
   }

   public void setEnableRedThreadTime(long enableRedThreadTime) {
      this.enableRedThreadTime = enableRedThreadTime;
   }

   public long getNextFullMapAttackTime() {
      return this.nextFullMapAttackTime;
   }

   public void setNextFullMapAttackTime(long nextFullMapAttackTime) {
      this.nextFullMapAttackTime = nextFullMapAttackTime;
   }

   public boolean isSetFullMapAttackTime() {
      return this.setFullMapAttackTime;
   }

   public void setSetFullMapAttackTime(boolean setFullMapAttackTime) {
      this.setFullMapAttackTime = setFullMapAttackTime;
   }

   static {
      List<Rect> rects = new ArrayList<>();
      rects.add(new Rect(-75, -80, -25, 560));
      rects.add(new Rect(13, -80, -47, 560));
      rects.add(new Rect(83, -80, 155, 560));
      rects.add(new Rect(83, -80, 155, 560));
      threadRect.add(rects);
      rects = new ArrayList<>();
      rects.add(new Rect(-81, -80, 9, 560));
      rects.add(new Rect(-59, -80, -79, 560));
      rects.add(new Rect(-25, -80, -12, 560));
      rects.add(new Rect(123, -80, 154, 560));
      rects.add(new Rect(138, -80, 84, 560));
      threadRect.add(rects);
      List<Rect> var2 = new ArrayList();
      var2.add(new Rect(-78, -80, -50, 560));
      var2.add(new Rect(-13, -80, -63, 560));
      var2.add(new Rect(42, -80, 123, 560));
      var2.add(new Rect(75, -80, 57, 560));
      var2.add(new Rect(133, -80, 137, 560));
      threadRect.add(var2);
      threadPos = new ArrayList<>();
      threadPos.add(new Point(-840, -260));
      threadPos.add(new Point(-560, -260));
      threadPos.add(new Point(-280, -260));
      threadPos.add(new Point(0, -260));
      threadPos.add(new Point(280, -260));
      threadPos.add(new Point(560, -260));
      threadPos.add(new Point(840, -260));
   }

   public static class JinHillahDeathCount {
      private Field_JinHillah.JinHillahDeathCountType status = Field_JinHillah.JinHillahDeathCountType.Green;

      public void encode(PacketEncoder packet) {
         packet.writeInt(0);
         packet.write(this.getStatus().getType());
      }

      public Field_JinHillah.JinHillahDeathCountType getStatus() {
         return this.status;
      }

      public void setStatus(Field_JinHillah.JinHillahDeathCountType status) {
         this.status = status;
      }
   }

   public static enum JinHillahDeathCountType {
      Red(0),
      Green(1),
      Out(2);

      private int type;

      private JinHillahDeathCountType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }
   }

   public static enum JinHillahResultType {
      CandleSet(0),
      UpdateCandle(1),
      DeathCount(3),
      Sandglass(4),
      Altar(6),
      UpdateAltarStatus(7),
      RemoveAltar(8),
      PartyDeathCount(10);

      private int type;

      private JinHillahResultType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }
   }
}
