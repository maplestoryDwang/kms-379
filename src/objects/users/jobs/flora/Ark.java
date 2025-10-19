package objects.users.jobs.flora;

import constants.GameConstants;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import network.models.CWvsContext;
import network.models.MobPacket;
import objects.fields.Field;
import objects.fields.ForceAtom;
import objects.fields.ForceAtom_Parallel;
import objects.fields.ForceAtom_Parallel_Bullet;
import objects.fields.ForceAtom_Parallel_Entry;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.Wreckage;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.jobs.Pirate;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Timer;

public class Ark extends Pirate {
   public boolean onArkEndlessBeast;
   private int specterStateCount = 0;
   private int plainSpellBulletCount = 0;
   private int scarletSpellBulletCount = 0;
   private int gustSpellBulletCount = 0;
   private int abyssSpellBulletCount = 0;
   public List<Integer> deviousNightmareCount = new ArrayList<>();
   private int lastArkAttack = 0;
   private ScheduledFuture<?> oldestAbyssTask = null;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      int skillID = GameConstants.getLinkedSkillID(attack.skillID);
      SecondaryStatEffect real = SkillFactory.getSkill(skillID).getEffect(attack.skillLevel);
      if (attack.skillID == 155111306) {
         int hpRCon = real.getHpRCon();
         long con = this.getPlayer().getStat().getCurrentMaxHp() / 100L * hpRCon;
         this.getPlayer().addHP(-con);
      }

      if (attack.skillID == 155120000) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(400051047);
         if (eff != null && this.getPlayer().getCooldownLimit(400051047) == 0L) {
            this.getPlayer().send(CField.userBonusAttackRequest(400051047, true, Collections.EMPTY_LIST));
            this.deviousNightmareCount.clear();
            this.getPlayer().send(CField.skillCooldown(400051047, eff.getCooldown(this.getPlayer())));
            this.getPlayer().addCooldown(400051047, System.currentTimeMillis(), eff.getCooldown(this.getPlayer()));
         }
      }

      if (attack.skillID == 155120001) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(400051048);
         if (eff != null && this.getPlayer().getCooldownLimit(400051048) == 0L) {
            this.getPlayer().send(CField.userBonusAttackRequest(400051048, true, Collections.EMPTY_LIST));
            this.getPlayer().getDreamsEscapeCount().clear();
            this.getPlayer().send(CField.skillCooldown(400051048, eff.getCooldown(this.getPlayer())));
            this.getPlayer().addCooldown(400051048, System.currentTimeMillis(), eff.getCooldown(this.getPlayer()));
         }
      }

      super.prepareAttack(attack, effect, opcode);
   }

   @Override
   public void onAttack(
      MapleMonster monster,
      boolean boss,
      AttackPair attackPair,
      Skill skill,
      long totalDamage,
      AttackInfo attack,
      SecondaryStatEffect effect,
      RecvPacketOpcode opcode
   ) {
      if (totalDamage > 0L) {
         if (attack.skillID == 155100009) {
            int slv = this.getPlayer().getTotalSkillLevel(155111207);
            if (slv > 0) {
               SecondaryStatEffect eff = SkillFactory.getSkill(155111207).getEffect(slv);
               if (eff != null && Randomizer.nextInt(100) < eff.getS()) {
                  int z = eff.getZ();
                  String value = this.getPlayer().getOneInfo(1544, String.valueOf(155111207));
                  int v = 0;
                  if (value != null) {
                     v = Integer.parseInt(value);
                  }

                  if (v != 0) {
                     z = eff.getY();
                  }

                  int wreakageCount = 0;

                  for (Wreckage wreak : this.getPlayer().getMap().getAllWreakage()) {
                     if (wreak.getOwner() != null && wreak.getOwner().getId() == this.getPlayer().getId() && wreak.getSkillID() == 155111207) {
                        wreakageCount++;
                     }
                  }

                  if (wreakageCount < eff.getZ()) {
                     this.getPlayer()
                        .getMap()
                        .spawnWreckage(
                           new Wreckage(this.getPlayer(), eff.getQ() * 1000, 155111207, this.getPlayer().incAndGetWreckageCount(), monster.getTruePosition())
                        );
                  }
               }
            }
         }

         if (attack.skillID == 155121003) {
            SecondaryStatEffect eff = SkillFactory.getSkill(155121004).getEffect(155121102);
            SecondaryStatEffect eff2 = SkillFactory.getSkill(155121102).getEffect(this.getPlayer().getTotalSkillLevel(155121102));
            if (eff != null && eff2 != null) {
               Rectangle rect = eff.calculateBoundingBox(monster.getPosition(), false);
               Point pos = this.getPlayer().getMap().calcDropPos(new Point(rect.x, rect.y - 23), monster.getPosition());
               rect.y = pos.y - 23;
               this.getPlayer().getMap().spawnMist(new AffectedArea(rect, this.getPlayer(), eff, pos, System.currentTimeMillis() + eff2.getQ() * 1000));
            }
         }

         if (attack.skillID == 155121306) {
            SecondaryStatEffect eff = SkillFactory.getSkill(155121006).getEffect(attack.skillLevel);
            SecondaryStatEffect eff2 = SkillFactory.getSkill(155121306).getEffect(this.getPlayer().getTotalSkillLevel(155121306));
            if (eff != null) {
               Rectangle rect = eff.calculateBoundingBox(this.getPlayer().getPosition(), false);
               Point point = this.getPlayer().getPosition();
               point.y -= 341;
               this.getPlayer().getMap().spawnMist(new AffectedArea(rect, this.getPlayer(), eff, point, System.currentTimeMillis() + eff2.getY() * 1000));
               if (monster.checkResistSkill(MobTemporaryStatFlag.FREEZE)) {
                  monster.applyStatus(
                     this.getPlayer(),
                     new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, 155121306, null, false),
                     false,
                     effect.getDuration(),
                     false,
                     effect
                  );
                  monster.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + 90000L, this.getPlayer(), attack.skillID);
               } else {
                  this.getPlayer()
                     .send(
                        MobPacket.monsterResist(
                           monster,
                           this.getPlayer(),
                           (int)((monster.getResistSkill(MobTemporaryStatFlag.FREEZE) - System.currentTimeMillis()) / 1000L),
                           attack.skillID
                        )
                     );
               }
            }
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (attack.skillID == 400051080 && !this.onArkEndlessBeast) {
         this.onArkEndlessBeast = true;
      }

      if (attack.targets > 0 && this.onArkEndlessBeast) {
         Skill endless = SkillFactory.getSkill(400051080);
         if (endless.getSkillList().contains(attack.skillID)) {
            this.getPlayer().send(CField.arkEndlessBeastReduce(600));
         } else if (endless.getSkillList2().contains(attack.skillID)) {
            this.getPlayer().send(CField.arkEndlessBeastReduce(180));
         }
      }

      if (attack.targets > 0) {
         this.handleSpellBullets(1, attack.skillID);
      }

      this.checkArkCancelAttack(attack.skillID, attack.targets);
      switch (attack.skillID) {
         case 155101200:
         case 155101201:
         case 155101202:
            if (this.getPlayer().skillisCooling(400051048) && !this.deviousNightmareCount.contains(0)) {
               this.deviousNightmareCount.add(0);
               this.getPlayer().changeCooldown(400051048, -1000L);
            }
            break;
         case 155101204:
         case 155101214:
            if (this.getPlayer().skillisCooling(400051048) && !this.deviousNightmareCount.contains(1)) {
               this.deviousNightmareCount.add(1);
               this.getPlayer().changeCooldown(400051048, -1000L);
            }
            break;
         case 155111006:
            if (this.getPlayer().skillisCooling(400051048) && !this.deviousNightmareCount.contains(3)) {
               this.deviousNightmareCount.add(3);
               this.getPlayer().changeCooldown(400051048, -1000L);
            }
            break;
         case 155111202:
         case 155111211:
         case 155111212:
            if (this.getPlayer().skillisCooling(400051048) && !this.deviousNightmareCount.contains(2)) {
               this.deviousNightmareCount.add(2);
               this.getPlayer().changeCooldown(400051048, -1000L);
            }
            break;
         case 155121202:
         case 155121215:
            if (this.getPlayer().skillisCooling(400051048) && !this.deviousNightmareCount.contains(4)) {
               this.deviousNightmareCount.add(4);
               this.getPlayer().changeCooldown(400051048, -1000L);
            }
      }

      switch (attack.skillID) {
         case 155101100:
         case 155101112:
         case 155120002:
         case 155121102:
         case 155141004:
         case 155141005:
         case 155141016:
         case 155141017:
            this.getPlayer().sendRegisterExtraSkill(attack.forcedPos, (attack.display & 32768) != 0, attack.skillID, 1);
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ImpendingDeath) != null
         && this.getPlayer().getBuffedValue(SecondaryStatFlag.SpectralForm) != null
         && this.getPlayer().getBuffedEffect(SecondaryStatFlag.indiePartialNotDamaged, 400051334) == null
         && attack.skillID != 400051048
         && attack.skillID != 155100009
         && attack.skillID != 155111207
         && attack.skillID != 155121006
         && attack.skillID != 155121306) {
         SecondaryStatEffect eff = this.getPlayer().getBuffedEffect(SecondaryStatFlag.ImpendingDeath);
         if (eff != null) {
            List<MapleMapObject> mobs_ = this.getPlayer()
               .getMap()
               .getMapObjectsInRange(this.getPlayer().getPosition(), 640000.0, Arrays.asList(MapleMapObjectType.MONSTER));
            List<Integer> mobList = new ArrayList<>();
            int bulletCount = eff.getZ();

            for (MapleMapObject mo : mobs_) {
               MapleMonster mons = (MapleMonster)mo;
               mobList.add(mons.getObjectId());
               if (mobList.size() >= bulletCount) {
                  break;
               }
            }

            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.InfinitySpell) != null) {
               int skillID = 400051036;
               if (this.getPlayer().getTotalSkillLevel(500061013) > 0) {
                  skillID = 500061013;
                  if (this.getPlayer().getTotalSkillLevel(155141001) > 0) {
                     this.getPlayer().temporaryStatSet(SecondaryStatFlag.AwakenAbyss, 155141001, Integer.MAX_VALUE, 300);
                  }
               }

               SecondaryStatEffect e = this.getPlayer().getSkillLevelData(skillID);
               if (e != null) {
                  for (int i = 0; i < e.getX(); i++) {
                     Collections.shuffle(mobs_);
                     MapleMapObject mob_ = mobs_.stream().findAny().orElse(null);
                     if (mob_ != null) {
                        MapleMonster mons = (MapleMonster)mob_;
                        mobList.add(mons.getObjectId());
                     }
                  }
               }
            }

            if (mobList.size() > 0) {
               ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
               info.initImpendingDead();
               ForceAtom forceAtom = new ForceAtom(
                  info, 155100009, this.getPlayer().getId(), false, true, this.getPlayer().getId(), ForceAtom.AtomType.IMPENDING_DEAD, mobList, mobList.size()
               );
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            }
         }
      }

      if (attack.skillID == 155001000 && attack.targets > 0) {
         SecondaryStatEffect eff = SkillFactory.getSkill(155001001).getEffect(this.getPlayer().getSkillLevel(attack.skillID));
         if (eff != null) {
            eff.applyTo(this.getPlayer());
         }
      }

      if (attack.skillID == 155101002 && attack.targets > 0) {
         SecondaryStatEffect eff = SkillFactory.getSkill(155101003).getEffect(this.getPlayer().getSkillLevel(attack.skillID));
         if (eff != null) {
            eff.applyTo(this.getPlayer());
         }
      }

      if (attack.skillID == 155111003 && attack.targets > 0) {
         SecondaryStatEffect eff = SkillFactory.getSkill(155111005).getEffect(this.getPlayer().getSkillLevel(attack.skillID));
         if (eff != null) {
            eff.applyTo(this.getPlayer());
         }

         this.getPlayer().sendRegisterExtraSkillIndex(attack.forcedPos, (attack.display & 32768) != 0, 155111003, 0);
      }

      if (attack.skillID == 155121003 && attack.targets > 0) {
         SecondaryStatEffect eff = SkillFactory.getSkill(155121005).getEffect(this.getPlayer().getSkillLevel(attack.skillID));
         if (eff != null) {
            eff.applyTo(this.getPlayer());
         }
      }

      if (attack.skillID == 155141002 && attack.targets > 0) {
         SecondaryStatEffect eff = SkillFactory.getSkill(155141003).getEffect(this.getPlayer().getSkillLevel(attack.skillID));
         if (eff != null) {
            eff.applyTo(this.getPlayer());
         }
      }

      if (attack.skillID == 155141009 && attack.targets > 0) {
         SecondaryStatEffect eff = SkillFactory.getSkill(155141010).getEffect(this.getPlayer().getSkillLevel(attack.skillID));
         if (eff != null) {
            eff.applyTo(this.getPlayer());
         }
      }

      if (attack.skillID == 155141014 && attack.targets > 0) {
         SecondaryStatEffect eff = SkillFactory.getSkill(155141015).getEffect(this.getPlayer().getSkillLevel(attack.skillID));
         if (eff != null) {
            eff.applyTo(this.getPlayer());
         }
      }

      if (attack.skillID == 155141019 && attack.targets > 0) {
         SecondaryStatEffect eff = SkillFactory.getSkill(155141020).getEffect(this.getPlayer().getSkillLevel(attack.skillID));
         if (eff != null) {
            eff.applyTo(this.getPlayer());
         }
      }

      if (attack.skillID == 155121202 && attack.targets > 0) {
         int delta = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * effect.getW());
         this.getPlayer().addHP(delta, false);
      }

      if (attack.skillID == 155111212 || attack.skillID == 155111211) {
         SecondaryStatEffect eff = SkillFactory.getSkill(155111202).getEffect(attack.skillLevel);
         if (eff != null) {
            if (attack.targets > 0) {
               int delta = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * eff.getW());
               this.getPlayer().addHP(delta);
            }

            int cooldown = eff.getCooldown(this.getPlayer());
            this.getPlayer().giveCoolDowns(155111202, System.currentTimeMillis(), cooldown);
            this.getPlayer().send(CField.skillCooldown(155111202, cooldown));
         }
      }

      if (attack.skillID == 155121306 || attack.skillID == 400051334) {
         if (this.specterStateCount > 0 && this.getPlayer().getBuffedValue(SecondaryStatFlag.SpectralForm) == null) {
            SecondaryStatEffect eff = SkillFactory.getSkill(155101006).getEffect(this.getPlayer().getTotalSkillLevel(155101006));
            if (eff != null) {
               eff.applyTo(this.getPlayer());
            }
         }

         if (attack.skillID == 400051334 && this.getPlayer().getCanNextSpecterStateTime() == 0L) {
            this.getPlayer().setCanNextSpecterStateTime(System.currentTimeMillis() + 30000L);
         }

         effect.applyTo(this.getPlayer());
      }

      if (attack.targets > 0 && this.getPlayer().getTotalSkillLevel(155141001) > 0) {
         List<Integer> list = SkillFactory.getSkill(155141001).getSkillList();
         Integer value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.AwakenAbyss, 0);
         if (list.contains(attack.skillID) && value > 0) {
            List<Pair<Integer, Integer>> objList = new ArrayList<>();

            for (AttackPair dmg : attack.allDamage) {
               int objId = dmg.objectid;
               MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(objId);
               if (mob != null) {
                  objList.add(new Pair<>(mob.getObjectId(), 0));
               }
            }

            this.getPlayer().send(CField.userBonusAttackRequest(155141001, true, objList));
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.AwakenAbyss, 155141001, Integer.MAX_VALUE, Math.max(value - 1, 0));
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      if (this.getActiveSkillPrepareID() == 400051334) {
         SecondaryStatEffect eff = SkillFactory.getSkill(400051334).getEffect(this.getPlayer().getTotalSkillLevel(400051334));
         if (eff != null) {
            this.getPlayer().temporaryStatSet(400051334, eff.getUpdatableTime(), SecondaryStatFlag.indiePartialNotDamaged, 1);
            this.getPlayer().temporaryStatSet(400051035, eff.getDuration(), SecondaryStatFlag.MemoryOfRoot, 1);
         }
      } else if (this.getActiveSkillPrepareID() == 400051080) {
         this.onArkEndlessBeast = true;
         if (this.specterStateCount > 0 && this.getPlayer().getBuffedValue(SecondaryStatFlag.SpectralForm) == null) {
            SecondaryStatEffect eff = SkillFactory.getSkill(155101006).getEffect(this.getPlayer().getTotalSkillLevel(155101006));
            if (eff != null) {
               eff.applyTo(this.getPlayer());
            }
         }
      }

      super.activeSkillPrepare(packet);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 150011074:
            long now = System.currentTimeMillis();
            int r = Randomizer.rand(0, 3);
            int questID = 21770;
            String key = "lp";
            int index = GameConstants.getSkillCommandStateIndex(this.getPlayer().getJob(), this.getActiveSkillID());
            String popUpFlag = this.getPlayer().getOneInfo(questID, key + index);
            if (popUpFlag == null) {
               popUpFlag = "0";
            }

            int effectId = 0;
            switch (r) {
               case 0:
                  effectId = 150011075;
                  break;
               case 1:
                  effectId = 150011076;
                  break;
               case 2:
                  effectId = 150011077;
                  break;
               case 3:
                  effectId = 150011078;
            }

            SecondaryStatEffect eff = SkillFactory.getSkill(effectId).getEffect(1);
            if (eff != null) {
               eff.applyTo(this.getPlayer(), true);
            }

            if (popUpFlag.equals("0")) {
               String lastTime = this.getPlayer().getOneInfoQuest(1546, "150011076");
               long time = System.currentTimeMillis();
               if (lastTime != null && !lastTime.isEmpty()) {
                  time = Long.valueOf(lastTime);
               }

               int diff = (int)(now - time);
               this.contactCaravanPopup(r, diff);
            }

            this.getPlayer().updateOneInfo(1546, "150011076", String.valueOf(now));
            this.getPlayer().updateOneInfo(1546, "contact", String.valueOf(r));
            break;
         case 155001103:
            int mobCount = packet.readByte();
            ForceAtom_Parallel forceAtom = new ForceAtom_Parallel();
            forceAtom.fromID = this.getPlayer().getId();
            forceAtom.skillID = 155001103;
            int count = 0;
            ForceAtom_Parallel_Entry entry = new ForceAtom_Parallel_Entry();

            for (int i = 0; i < this.abyssSpellBulletCount; i++) {
               if (count++ < mobCount) {
                  int mobObjectID = packet.readInt();
                  ForceAtom_Parallel_Bullet bullet = new ForceAtom_Parallel_Bullet();
                  ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                  info.initSpellBullets();
                  bullet.atomInfos.add(info);
                  bullet.targetID = mobObjectID;
                  entry.bullets.add(bullet);
               }
            }

            entry.bulletSkillID = 155121003;
            entry.atomType = ForceAtom.AtomType.ABYSS_SPELL;
            forceAtom.entries.add(entry);
            entry = new ForceAtom_Parallel_Entry();

            for (int ix = 0; ix < this.gustSpellBulletCount; ix++) {
               if (count++ < mobCount) {
                  int mobObjectID = packet.readInt();
                  ForceAtom_Parallel_Bullet bullet = new ForceAtom_Parallel_Bullet();
                  ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                  info.initSpellBullets();
                  bullet.atomInfos.add(info);
                  bullet.targetID = mobObjectID;
                  entry.bullets.add(bullet);
               }
            }

            entry.bulletSkillID = 155111003;
            entry.atomType = ForceAtom.AtomType.GUST_SPELL;
            forceAtom.entries.add(entry);
            entry = new ForceAtom_Parallel_Entry();

            for (int ixx = 0; ixx < this.scarletSpellBulletCount; ixx++) {
               if (count++ < mobCount) {
                  int mobObjectID = packet.readInt();
                  ForceAtom_Parallel_Bullet bullet = new ForceAtom_Parallel_Bullet();
                  ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                  info.initSpellBullets();
                  bullet.atomInfos.add(info);
                  bullet.targetID = mobObjectID;
                  entry.bullets.add(bullet);
               }
            }

            entry.bulletSkillID = 155101002;
            entry.atomType = ForceAtom.AtomType.SCARLET_SPELL;
            forceAtom.entries.add(entry);
            entry = new ForceAtom_Parallel_Entry();

            for (int ixxx = 0; ixxx < this.plainSpellBulletCount; ixxx++) {
               if (count++ < mobCount) {
                  int mobObjectID = packet.readInt();
                  ForceAtom_Parallel_Bullet bullet = new ForceAtom_Parallel_Bullet();
                  ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                  info.initSpellBullets();
                  bullet.atomInfos.add(info);
                  bullet.targetID = mobObjectID;
                  entry.bullets.add(bullet);
               }
            }

            entry.bulletSkillID = 155001000;
            entry.atomType = ForceAtom.AtomType.PLAIN_SPELL;
            forceAtom.entries.add(entry);
            this.clearSpellBullets();
            this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtomParallel(forceAtom));
            break;
         case 155101006:
            if (this.specterStateCount <= 0) {
               return;
            }

            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.SpectralForm) != null) {
               this.getPlayer().temporaryStatResetBySkillID(155101006);
               this.getPlayer().send(CWvsContext.enableActions(this.getPlayer(), this.exclusive));
            } else {
               effect.applyTo(this.getPlayer(), true);
            }
            break;
         case 155111207:
            Field field = this.getPlayer().getMap();
            List<MapleMapObject> mobs = this.getPlayer()
               .getMap()
               .getMapObjectsInRange(this.getPlayer().getPosition(), 320000.0, Arrays.asList(MapleMapObjectType.MONSTER));
            List<Integer> mobList = new LinkedList<>();
            List<Wreckage> wreakageList = new LinkedList<>();
            int i = 0;
            List<Point> position = new ArrayList<>();
            field.getAllWreakage().stream().filter(w -> w.getOwner().getId() == this.getPlayer().getId()).forEach(w -> {
               wreakageList.add(w);
               position.add(w.getPosition());
               w.removeWreckage(field, true);
            });
            this.getPlayer().setWreckageCount(0);
            Collections.sort(wreakageList, (r1, r2) -> r1.getObjectId() - r2.getObjectId());

            for (MapleMapObject mo : mobs) {
               MapleMonster mons = (MapleMonster)mo;
               mobList.add(mons.getObjectId());
               if (++i >= 12) {
                  break;
               }
            }

            field.broadcastMessage(CField.DelWreckage(this.getPlayer().getId(), wreakageList, this.getPlayer().getCooldownLimit(22171095) != 0L));
            ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
            info.initReturningHatred(position);
            ForceAtom forceAtom2 = new ForceAtom(
               info,
               this.getActiveSkillID(),
               this.getPlayer().getId(),
               false,
               true,
               this.getPlayer().getId(),
               ForceAtom.AtomType.RETURNING_HATRED,
               mobList,
               wreakageList.size()
            );
            this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom2));
            effect.applyTo(this.getPlayer(), true);
            break;
         case 155141500:
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.OldestAbyss, 155141500, effect.getDuration(), 1);
            if (this.getPlayer().getTotalSkillLevel(155141001) > 0) {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.AwakenAbyss, 155141001, Integer.MAX_VALUE, 300);
            }

            this.startOldestAbyssTask();
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   private void contactCaravanPopup(int r, int diff) {
      if (r == 0) {
         this.getPlayer()
            .send(
               CField.addPopupSay(
                  3001532,
                  5000,
                  "앗, 오랜만이네. 아, 그렇게 오래는 안되었던가... 그래도 이정도 지났으면 오랜만이라고 하는게 맞는 것 같은데... 얼마 안되었는데 오랜만이라고 했다고 기분 상했으면 어쩌지... 말실수 할 줄 알았으면 내가 받는게 아니었는데, 아침 점괘가 이상하더라니... 전부 내 실수야. 미안해...",
                  ""
               )
            );
         this.getPlayer().dropMessage(5, "니야는 여전한 것 같다. 니야를 달래고 카라반의 상황에 대해 이야기를 나누었다.");
      } else if (r == 1) {
         String timeStr = "";
         int hours = diff / 3600;
         int minutes = diff % 3600;
         minutes /= 60;
         int seconds = minutes % 60;
         if (hours > 0) {
            timeStr = hours + "시간 ";
         }

         if (minutes > 0) {
            timeStr = timeStr + minutes + "분 ";
         }

         if (seconds > 0) {
            timeStr = timeStr + seconds + "초 ";
         }

         if (timeStr.isEmpty()) {
            timeStr = "0 ";
         }

         this.getPlayer()
            .send(
               CField.addPopupSay(
                  3001533,
                  5000,
                  "이전 너와 교신한 후 정확히 "
                     + timeStr
                     + "분 지난 것이다, 치르.\r\n전파가 잡히지 않아서 걱정했는데 전파 장치가 아직 작동하는 것 같아서 다행인 것이다, 치르.\r\n바크바크 녀석은 마음만 느낄 수 있으면 된다는 망발을 했지만 역시 실제로 교신해야 서로 무사한 것을 확인할 수 있다고 본다. 치르.",
                  ""
               )
            );
         this.getPlayer().dropMessage(5, "웨이는 여전히 바크바크와 다투는 것 같다. 전파장치를 좀 더 잘 사용할 수 있는 방법에 대해 이야기를 나누었다.");
      } else if (r == 2) {
         this.getPlayer().send(CField.addPopupSay(3001534, 3000, "헤이! 왓썹 브로. 롱타임노씨이지만 유와 내 하트가 뜨겁게 뛰고있으니 전혀 걱정하지 않았돠아!\r\n말이 나왔으니 오랜만에 내 뮤직을 리슨.", ""));
         this.getPlayer().dropMessage(5, "바크바크는 여전한 것 같다. 바크바크의 노래에 대해 이야기를 나누었다.");
      } else if (r == 3) {
         this.getPlayer()
            .send(
               CField.addPopupSay(
                  3001535, 3000, "앗. 반짝반짝한 걸 준 사람, 잘 있었냐! 나 요즘 밥 많이 먹었더니 이 만큼 컸다! 어제는 넘어지고 울지도 않았어! 헤헤...\r\n(이건 비밀인데 이 근처에서 반짝반짝하는 것도 발견했어!)", ""
               )
            );
         this.getPlayer().dropMessage(5, "마르는 씩씩하게 자라고 있는 것 같다. 마르가 어제 한 놀이에 대해 이야기를 나누었다.");
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 155111306) {
         this.getPlayer().temporaryStatResetBySkillID(155111306);
      }

      super.activeSkillCancel();
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      if (!this.getPlayer().hasBuffBySkillID(155141500) && this.oldestAbyssTask != null) {
         this.cancelOldestAbyssTask();
      }

      if (this.getPlayer().getCanNextSpecterStateTime() != 0L && this.getPlayer().getCanNextSpecterStateTime() <= System.currentTimeMillis()) {
         this.getPlayer().setCanNextSpecterStateTime(0L);
      }

      if (this.getPlayer().getCanNextSpecterStateTime() == 0L || this.getPlayer().getCanNextSpecterStateTime() <= System.currentTimeMillis()) {
         int skillID = 155000007;
         SecondaryStatEffect effect = this.getPlayer().getBuffedEffect(SecondaryStatFlag.SpecterState);
         if (effect == null) {
            effect = SkillFactory.getSkill(skillID).getEffect(this.getPlayer().getTotalSkillLevel(skillID));
         }

         if (effect != null) {
            int value = this.specterStateCount;
            if (this.getPlayer().getBuffedValue(SecondaryStatFlag.SpectralForm) != null) {
               if (this.getPlayer().getCanNextSpecterStateTime() == 0L || this.getPlayer().getCanNextSpecterStateTime() > System.currentTimeMillis()) {
                  value = Math.max(0, value - 23);
                  if (value <= 0) {
                     this.getPlayer().temporaryStatResetBySkillID(155101006);
                     this.getPlayer().setCanNextSpecterStateTime(System.currentTimeMillis() + effect.getZ() * 1000);
                  }
               }
            } else {
               value = Math.min(1000, value + 14);
            }

            this.specterStateCount = value;
            this.getPlayer().temporaryStatSet(skillID, Integer.MAX_VALUE, SecondaryStatFlag.SpecterState, 1);
         }
      }
   }

   public void checkArkCancelAttack(int skillID, int target) {
      if (skillID != this.lastArkAttack) {
         if (this.lastArkAttack == 155001100 || this.lastArkAttack == 155120001 || this.lastArkAttack == 155141000) {
            switch (skillID) {
               case 155101112:
               case 155101114:
               case 155101212:
               case 155101214:
               case 155111111:
               case 155111211:
               case 155111306:
               case 155121041:
               case 155121102:
               case 155121215:
               case 155141006:
               case 155141012:
               case 155141016:
               case 400051334:
                  SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(155120014);
                  if (effect != null) {
                     Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.BattleHolic);
                     int v = 0;
                     if (value != null) {
                        v = value;
                     }

                     v = Math.min(++v, effect.getX());
                     this.getPlayer()
                        .temporaryStatSet(
                           155120014, effect.getDuration() > 5000 ? effect.getDuration() : effect.getDuration() * 4, SecondaryStatFlag.BattleHolic, v
                        );
                  }

                  if (this.getPlayer().getTotalSkillLevel(155141001) > 0 && target > 0) {
                     int value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.AwakenAbyss, 0);
                     this.getPlayer().temporaryStatSet(SecondaryStatFlag.AwakenAbyss, 155141001, Integer.MAX_VALUE, Math.min(value + 10, 300));
                  }
            }
         }

         this.lastArkAttack = skillID;
      }
   }

   public void handleSpellBullets(int delta, int attackSkillID) {
      int skillID = 155001103;
      Skill skill = SkillFactory.getSkill(skillID);
      if (skill != null) {
         int skillLevel = this.getPlayer().getSkillLevel(skillID);
         if (skillLevel > 0) {
            SecondaryStatFlag flag = null;
            int count = 0;
            switch (attackSkillID) {
               case 155001100:
               case 155141000:
                  flag = SecondaryStatFlag.PlainSpellBullets;
                  count = this.plainSpellBulletCount / 2 + this.scarletSpellBulletCount + this.gustSpellBulletCount + this.abyssSpellBulletCount;
                  if (count < 5) {
                     this.plainSpellBulletCount += 2;
                  }
                  break;
               case 155101100:
               case 155101101:
               case 155101112:
               case 155141004:
               case 155141005:
               case 155141006:
                  count = this.scarletSpellBulletCount;
                  if (count < 1) {
                     this.scarletSpellBulletCount = 1;
                  }

                  flag = SecondaryStatFlag.ScarletSpellBullets;
                  if (this.getPlayer().skillisCooling(400051047) && !this.deviousNightmareCount.contains(0)) {
                     this.deviousNightmareCount.add(0);
                     this.getPlayer().changeCooldown(400051047, -1000L);
                  }
                  break;
               case 155111102:
               case 155111111:
               case 155141011:
               case 155141012:
                  count = this.gustSpellBulletCount;
                  if (count < 1) {
                     this.gustSpellBulletCount = 1;
                  }

                  flag = SecondaryStatFlag.GustSpellBullets;
                  if (this.getPlayer().skillisCooling(400051047) && !this.deviousNightmareCount.contains(1)) {
                     this.deviousNightmareCount.add(1);
                     this.getPlayer().changeCooldown(400051047, -1000L);
                  }
                  break;
               case 155121102:
               case 155141016:
                  count = this.abyssSpellBulletCount;
                  if (count < 1) {
                     this.abyssSpellBulletCount = 1;
                  }

                  flag = SecondaryStatFlag.AbysSpellBullets;
                  if (this.getPlayer().skillisCooling(400051047) && !this.deviousNightmareCount.contains(2)) {
                     this.deviousNightmareCount.add(2);
                     this.getPlayer().changeCooldown(400051047, -1000L);
                  }
            }

            if (flag == null) {
               return;
            }

            SecondaryStatEffect effect = SkillFactory.getSkill(attackSkillID).getEffect(this.getPlayer().getTotalSkillLevel(attackSkillID));
            if (effect != null) {
               if (this.getPlayer().getBuffedValue(SecondaryStatFlag.InfinitySpell) != null) {
                  int bulletCount = 0;
                  bulletCount = this.plainSpellBulletCount / 2 + this.scarletSpellBulletCount + this.gustSpellBulletCount + this.abyssSpellBulletCount;
                  this.plainSpellBulletCount += (5 - bulletCount) * 2;
                  if (flag.equals(SecondaryStatFlag.PlainSpellBullets)) {
                     this.getPlayer().temporaryStatSet(this.getPlayer().getJob(), Integer.MAX_VALUE, flag, 1);
                  } else {
                     Map<SecondaryStatFlag, Integer> flags = new HashMap<>();
                     flags.put(SecondaryStatFlag.PlainSpellBullets, 1);
                     flags.put(flag, 1);
                     this.getPlayer().temporaryStatSet(this.getPlayer().getJob(), skillLevel, Integer.MAX_VALUE, flags);
                  }
               } else {
                  this.getPlayer().temporaryStatSet(this.getPlayer().getJob(), Integer.MAX_VALUE, flag, 1);
               }
            }
         }
      }
   }

   public void clearSpellBullets() {
      this.abyssSpellBulletCount = 0;
      this.gustSpellBulletCount = 0;
      this.scarletSpellBulletCount = 0;
      this.plainSpellBulletCount = 0;
      this.getPlayer().temporaryStatSet(this.getPlayer().getJob(), Integer.MAX_VALUE, SecondaryStatFlag.AbysSpellBullets, 1);
      this.getPlayer().temporaryStatSet(this.getPlayer().getJob(), Integer.MAX_VALUE, SecondaryStatFlag.GustSpellBullets, 1);
      this.getPlayer().temporaryStatSet(this.getPlayer().getJob(), Integer.MAX_VALUE, SecondaryStatFlag.ScarletSpellBullets, 1);
      this.getPlayer().temporaryStatSet(this.getPlayer().getJob(), Integer.MAX_VALUE, SecondaryStatFlag.PlainSpellBullets, 1);
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case SpecterState:
            packet.writeInt(this.specterStateCount);
            break;
         case PlainSpellBullets:
            packet.writeInt(this.plainSpellBulletCount);
            packet.writeInt(0);
            break;
         case ScarletSpellBullets:
            packet.writeInt(this.scarletSpellBulletCount);
            packet.writeInt(0);
            break;
         case GustSpellBullets:
            packet.writeInt(this.gustSpellBulletCount);
            packet.writeInt(0);
            break;
         case AbysSpellBullets:
            packet.writeInt(this.abyssSpellBulletCount);
            packet.writeInt(0);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }

   public void cancelOldestAbyssTask() {
      try {
         if (this.oldestAbyssTask == null) {
            return;
         }

         this.oldestAbyssTask.cancel(true);
         this.oldestAbyssTask = null;
      } catch (Exception var2) {
         System.out.println("Ark Err");
         var2.printStackTrace();
      }
   }

   public void startOldestAbyssTask() {
      if (this.oldestAbyssTask != null) {
         this.cancelOldestAbyssTask();
      }

      this.oldestAbyssTask = Timer.BuffTimer.getInstance().register(() -> {
         if (this.getPlayer() != null && this.getPlayer().getMap() != null) {
            if (this.getPlayer().getBuffedEffect(SecondaryStatFlag.OldestAbyss) != null) {
               this.getPlayer().send(CField.userBonusAttackRequest(155141501, true, Collections.emptyList(), 0, 0));
            } else {
               this.cancelOldestAbyssTask();
            }
         } else {
            this.cancelOldestAbyssTask();
         }
      }, 7320L);
   }
}
