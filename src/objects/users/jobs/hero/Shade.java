package objects.users.jobs.hero;

import constants.GameConstants;
import io.netty.util.internal.ThreadLocalRandom;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import network.models.MobPacket;
import objects.fields.ForceAtom;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.EliteType;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.MoveAbility;
import objects.item.Item;
import objects.users.MapleCoolDownValueHolder;
import objects.users.jobs.Pirate;
import objects.users.skills.RandomSkillInfo;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class Shade extends Pirate {
   public long lastElementalFocusTime = 0L;
   public long randomSkillID = 0L;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (GameConstants.isEunWol(GameConstants.getSkillRootFromSkill(attack.skillID))) {
         if (this.getPlayer().getTotalSkillLevel(500061006) > 0) {
            this.checkForRandomSkill(500061006);
         } else {
            this.checkForRandomSkill(400051010);
         }
      }

      if (attack.targets > 0
            && this.getPlayer().getBuffedValue(SecondaryStatFlag.HiddenHyperLinkMaximization) != null) {
         SecondaryStatEffect e = this.getPlayer().getSkillLevelData(25121133);
         if (e != null && e.makeChanceResult()) {
            Item drop = new Item(2434851, (short) 0, (short) 1, 0);
            MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(attack.allDamage.get(0).objectid);
            if (mob != null) {
               Point pos = mob.getPosition();
               this.getPlayer().getMap().spawnItemDrop(this.getPlayer(), this.getPlayer(), drop, pos, false, false);
            }
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
         RecvPacketOpcode opcode) {
      if (attack.skillID == 25101003 || attack.skillID == 25101004 || attack.skillID == 25111004) {
         SecondaryStatEffect eff = SkillFactory.getSkill(25100011).getEffect(attack.skillLevel);
         if (eff != null) {
            monster.applyStatus(
                  this.getPlayer(),
                  new MobTemporaryStatEffect(MobTemporaryStatFlag.HIDDEN_DEBUFF, 1, 25100011, null, false), false,
                  eff.getDuration(), true, eff);
         }
      }

      if (totalDamage > 0L) {
         int slv = 0;
         if ((slv = this.getPlayer().getTotalSkillLevel(25110210)) > 0) {
            SecondaryStatEffect eff = SkillFactory.getSkill(25110210).getEffect(slv);
            if (eff != null) {
               Map<MobTemporaryStatFlag, MobTemporaryStatEffect> mse = new HashMap<>();
               mse.put(MobTemporaryStatFlag.ACC,
                     new MobTemporaryStatEffect(MobTemporaryStatFlag.ACC, -eff.getY(), 25110210, null, false));
               mse.put(MobTemporaryStatFlag.EVA,
                     new MobTemporaryStatEffect(MobTemporaryStatFlag.EVA, -eff.getZ(), 25110210, null, false));
               mse.put(
                     MobTemporaryStatFlag.ADD_DAM_SKILL_2, new MobTemporaryStatEffect(
                           MobTemporaryStatFlag.ADD_DAM_SKILL_2, eff.getX(), 25110210, null, false));
               monster.applyMonsterBuff(mse, 25110210, eff.getDuration(), null, Collections.EMPTY_LIST);
            }
         }

         if (attack.skillID == 25120003) {
            if (effect.makeChanceResult()) {
               monster.applyStatus(
                     this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.STUN, 1, 25120003, null, false),
                     false, effect.getDuration(), true, effect);
            }
         } else if (attack.skillID == 25121007) {
            if (monster.getStats().getMoveAbility() == MoveAbility.Stop.getType()
                  || monster.getEliteMobType() == EliteType.Boss) {
               monster.applyStatus(
                     this.getPlayer(),
                     new MobTemporaryStatEffect(MobTemporaryStatFlag.ADD_DAM_SKILL, effect.getZ(), 25121007, null,
                           false),
                     false,
                     effect.getDuration(),
                     false,
                     effect);
            } else if (monster.getBuff(MobTemporaryStatFlag.SEPERATE_SOUL_C) == null
                  && monster.checkResistSkill(MobTemporaryStatFlag.SEPERATE_SOUL_P)) {
               Point pos = monster.getPosition();
               int templateID = monster.getId();
               MapleMonster mob = MapleLifeFactory.getMonster(templateID);
               int objectID = this.getPlayer().getMap().spawnMonster(mob, pos, -1);
               MapleMonster m = this.getPlayer().getMap().getMonsterByOid(objectID);
               if (m != null) {
                  m.setSeperateSoulDummy(this.getPlayer(), monster, attack.skillID, attack.skillLevel, effect);
                  monster.setSeperateSoulOriginal(this.getPlayer(), attack.skillID, objectID, effect);
                  monster.addResistSkill(MobTemporaryStatFlag.SEPERATE_SOUL_P, System.currentTimeMillis() + 90000L,
                        this.getPlayer(), attack.skillID);
               }
            }
         }

         if (attack.skillID == 25111206) {
            if (monster.checkResistSkill(MobTemporaryStatFlag.FREEZE)) {
               monster.applyStatus(
                     this.getPlayer(),
                     new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, skill.getId(), null, false),
                     false,
                     effect.getDuration(),
                     false,
                     effect);
               monster.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + 90000L,
                     this.getPlayer(), attack.skillID);
            } else {
               this.getPlayer()
                     .send(
                           MobPacket.monsterResist(
                                 monster,
                                 this.getPlayer(),
                                 (int) ((monster.getResistSkill(MobTemporaryStatFlag.FREEZE)
                                       - System.currentTimeMillis()) / 1000L),
                                 attack.skillID));
            }
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
         boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill,
         long totalExp, RecvPacketOpcode opcode) {
      if (attack.targets > 0 && attack.skillID == 400051079) {
         SecondaryStatEffect e = this.getPlayer().getSkillLevelData(400051078);
         if (e != null) {
            int skillID_ = 25101009;
            int skillLevel = this.getPlayer().getSkillLevel(25120110);
            if (skillLevel > 0) {
               skillID_ = 25120110;
            } else {
               skillLevel = Math.max(1, this.getPlayer().getSkillLevel(25100009));
               skillID_ = 25100009;
            }

            ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
            info.initFoxGhost(skillID_);
            if (skillID_ == 25100009) {
               skillID_ = 25100010;
            } else if (skillID_ == 25120110) {
               skillID_ = 25120115;
            }

            if (this.getPlayer().hasBuffBySkillID(25141506)) {
               ForceAtom forceAtom = new ForceAtom(
                     info,
                     25141505,
                     this.getPlayer().getId(),
                     false,
                     true,
                     this.getPlayer().getId(),
                     ForceAtom.AtomType.FOX_GHOST_UPGRADE,
                     Collections.EMPTY_LIST,
                     e.getV());
               this.getPlayer().send(CField.getCreateForceAtom(forceAtom));
            } else {
               ForceAtom forceAtom = new ForceAtom(
                     info,
                     skillID_,
                     this.getPlayer().getId(),
                     false,
                     true,
                     this.getPlayer().getId(),
                     ForceAtom.AtomType.FOX_GHOST,
                     Collections.EMPTY_LIST,
                     e.getV());
               this.getPlayer().send(CField.getCreateForceAtom(forceAtom));
            }
         }
      }

      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ForceAtomOnOff) != null
            && skill.getId() != 25100009
            && skill.getId() != 25120110
            && skill.getId() != 25141505) {
         if (this.getPlayer().hasBuffBySkillID(25141506)) {
            SecondaryStatEffect eff = SkillFactory.getSkill(25141505)
                  .getEffect(this.getPlayer().getTotalSkillLevel(25141505));
            int prop = eff.getU();
            if (ThreadLocalRandom.current().nextInt(100) < prop) {
               int bulletCount = eff.getBulletCount();
               List<MapleMonster> monsters = this.getPlayer().getMap()
                     .getMobsInRange(this.getPlayer().getTruePosition(), 640000.0, bulletCount, true);
               List<Integer> mobs = new ArrayList<>();

               for (MapleMonster monster : monsters) {
                  mobs.add(monster.getObjectId());
               }

               ForceAtom.AtomInfo infox = new ForceAtom.AtomInfo();
               infox.initFoxGhostUpgrade();
               ForceAtom forceAtom = new ForceAtom(
                     infox, 25141505, this.getPlayer().getId(), false, true, this.getPlayer().getId(),
                     ForceAtom.AtomType.FOX_GHOST_UPGRADE, mobs, 1);
               this.getPlayer().getMap().addForceAtom(forceAtom.getKey(), forceAtom);
               this.getPlayer().send(CField.getCreateForceAtom(forceAtom));
            }
         } else {
            int skillLevelx = this.getPlayer().getSkillLevel(25120110);
            int skillID_x;
            if (skillLevelx > 0) {
               skillID_x = 25120110;
            } else {
               skillLevelx = Math.max(1, this.getPlayer().getSkillLevel(25100009));
               skillID_x = 25100009;
            }

            SecondaryStatEffect eff = SkillFactory.getSkill(skillID_x).getEffect(skillLevelx);
            boolean prop = eff.makeChanceResult();
            if (prop) {
               int bulletCount = eff.getBulletCount();
               List<MapleMonster> monsters = this.getPlayer().getMap()
                     .getMobsInRange(this.getPlayer().getTruePosition(), 640000.0, bulletCount, true);
               List<Integer> mobs = new ArrayList<>();

               for (MapleMonster monster : monsters) {
                  mobs.add(monster.getObjectId());
               }

               ForceAtom.AtomInfo infox = new ForceAtom.AtomInfo();
               infox.initFoxGhost(skillID_x);
               if (skillID_x == 25100009) {
                  skillID_x = 25100010;
               } else {
                  skillID_x = 25120115;
               }

               ForceAtom forceAtom = new ForceAtom(
                     infox, skillID_x, this.getPlayer().getId(), false, true, this.getPlayer().getId(),
                     ForceAtom.AtomType.FOX_GHOST, mobs, 1);
               this.getPlayer().getMap().addForceAtom(forceAtom.getKey(), forceAtom);
               this.getPlayer().send(CField.getCreateForceAtom(forceAtom));
            }
         }
      }

      if (this.getPlayer().getSkillLevel(20050285) > 0 && attack.targets > 0) {
         SecondaryStatEffect eff = SkillFactory.getSkill(20050285).getEffect(this.getPlayer().getSkillLevel(20050285));
         if (eff != null) {
            int delta = (int) (this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * eff.getX());
            this.getPlayer().addHP(delta);
         }
      }

      if (attack.skillID == 25141503 && this.getPlayer().hasBuffBySkillID(25141506)
            && this.getPlayer().getRemainCooltime(400051043) <= 0L) {
         SecondaryStatEffect eff = SkillFactory.getSkill(25141504)
               .getEffect(this.getPlayer().getTotalSkillLevel(25141504));
         int cooltime = eff.getCoolTime();
         this.getPlayer().send(CField.skillCooldown(400051043, cooltime));
         this.getPlayer().addCooldown(400051043, System.currentTimeMillis(), cooltime);
         this.getPlayer().send(CField.userBonusAttackRequest(25141504, true, Collections.emptyList()));

         for (int i = 0; i < 6; i++) {
            ForceAtom.AtomInfo infox = new ForceAtom.AtomInfo();
            infox.initFoxGhostUpgrade();
            ForceAtom forceAtom = new ForceAtom(
                  infox,
                  25141505,
                  this.getPlayer().getId(),
                  false,
                  true,
                  this.getPlayer().getId(),
                  ForceAtom.AtomType.FOX_GHOST_UPGRADE,
                  Collections.emptyList(),
                  1);
            this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      if (this.getActiveSkillPrepareID() == 25121030) {
         SecondaryStatEffect eff = SkillFactory.getSkill(25121030)
               .getEffect(this.getPlayer().getTotalSkillLevel(25121030));
         if (eff != null) {
            this.getPlayer().temporaryStatSet(25121030, eff.getDuration(), SecondaryStatFlag.indiePartialNotDamaged, 1);
         }
      }

      if (this.getActiveSkillPrepareID() == 400051078) {
         SecondaryStatEffect eff = SkillFactory.getSkill(400051078)
               .getEffect(this.getPlayer().getTotalSkillLevel(400051078));
         if (eff != null) {
            this.getPlayer().temporaryStatSet(400051078, Integer.MAX_VALUE, SecondaryStatFlag.indieKeyDownMoving,
                  eff.getW());
         }
      }

      super.activeSkillPrepare(packet);
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      if (this.getActiveSkillID() == 400051010 || this.getActiveSkillID() == 500061006) {
         List<MapleCoolDownValueHolder> skillId = new ArrayList<>();

         for (MapleCoolDownValueHolder h : this.getPlayer().getCooldowns()) {
            if (h.skillId / 10000 != 2514 && h.skillId < 400000000) {
               skillId.add(h);
            }
         }

         this.getPlayer().getCooldowns().removeAll(skillId);
         skillId.forEach(skill -> this.getPlayer().send(CField.skillCooldown(skill.skillId, 0)));
      }

      super.beforeActiveSkill(packet);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 25100002:
            byte mobCount = packet.readByte();

            for (int i = 0; i < mobCount; i++) {
               MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(packet.readInt());
               if (mob != null && effect.makeChanceResult()) {
                  mob.applyStatus(
                        this.getPlayer(),
                        new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, effect.getX(), this.getActiveSkillID(),
                              null, false),
                        false,
                        effect.getDuration(),
                        true,
                        effect);
               }
            }
            break;
         case 25100009:
            mobCount = packet.readByte();
            List<Integer> list = new ArrayList<>();

            for (int i = 0; i < mobCount; i++) {
               list.add(packet.readInt());
            }

            ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
            if (this.getPlayer().hasBuffBySkillID(25141506)) {
               info.initFoxGhostUpgrade();
               ForceAtom forceAtom = new ForceAtom(
                     info, 25141505, this.getPlayer().getId(), false, true, this.getPlayer().getId(),
                     ForceAtom.AtomType.FOX_GHOST_UPGRADE, list, 1);
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            } else {
               info.initFoxGhost(25100010);
               ForceAtom forceAtom = new ForceAtom(
                     info, 25100010, this.getPlayer().getId(), false, true, this.getPlayer().getId(),
                     ForceAtom.AtomType.FOX_GHOST, list, 1);
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            }
            break;
         case 25120110:
            mobCount = packet.readByte();
            list = new ArrayList<>();

            for (int i = 0; i < mobCount; i++) {
               list.add(packet.readInt());
            }

            info = new ForceAtom.AtomInfo();
            if (this.getPlayer().hasBuffBySkillID(25141506)) {
               info.initFoxGhostUpgrade();
               ForceAtom forceAtom = new ForceAtom(
                     info, 25141505, this.getPlayer().getId(), false, true, this.getPlayer().getId(),
                     ForceAtom.AtomType.FOX_GHOST_UPGRADE, list, 1);
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            } else {
               info.initFoxGhost(25120115);
               ForceAtom forceAtom = new ForceAtom(
                     info, 25120115, this.getPlayer().getId(), false, true, this.getPlayer().getId(),
                     ForceAtom.AtomType.FOX_GHOST, list, 1);
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            }
            break;
         case 25141506:
            SecondaryStatEffect eff = SkillFactory.getSkill(25141506)
                  .getEffect(this.getPlayer().getTotalSkillLevel(25141506));
            Map<SecondaryStatFlag, Integer> statups = new EnumMap<>(SecondaryStatFlag.class);
            statups.put(SecondaryStatFlag.indieSummon, 1);
            statups.put(SecondaryStatFlag.indiePMDR, eff.getIndiePMdR());
            this.getPlayer().temporaryStatSet(25141506, eff.getLevel(), eff.getDuration(), statups);
            break;
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 400051078) {
         this.getPlayer().temporaryStatReset(SecondaryStatFlag.indieKeyDownMoving);
      } else if (this.getActiveSkillID() == 25121030) {
         this.getPlayer().temporaryStatResetBySkillID(25121030);
      } else if (this.randomSkillID == this.getActiveSkillID()) {
         this.randomSkillID = 0L;
      }

      if (this.getActiveSkillID() == 25111005) {
         SecondaryStatEffect effect = this.getPlayer().getBuffedEffect(SecondaryStatFlag.ProfessionalAgent);
         if (effect != null) {
            int reason = this.getPlayer().getSecondaryStatReason(SecondaryStatFlag.ProfessionalAgent);
            this.getPlayer().temporaryStatResetBySkillID(SecondaryStatFlag.ProfessionalAgent, reason);
         }
      }

      super.activeSkillCancel();
   }

   @Override
   public void afterActiveSkill() {
      if (this.randomSkillID == this.getActiveSkillID()) {
         this.randomSkillID = 0L;
      }
   }

   public void checkForRandomSkill(int reasonID) {
      SecondaryStatEffect effect = this.getPlayer().getBuffedEffect(SecondaryStatFlag.indiePMDR, reasonID);
      if (effect != null && effect.getSourceId() == reasonID
            && this.getPlayer().checkInterval(this.lastElementalFocusTime, effect.getX() * 1000)) {
         int maxWeight = 0;
         Skill skill = SkillFactory.getSkill(effect.getSourceId());
         if (skill != null) {
            List<RandomSkillInfo> random = skill.getRandomSkillInfo();
            Collections.shuffle(random);

            for (RandomSkillInfo rsi : random) {
               maxWeight += rsi.getProb();
            }

            int rand = Randomizer.rand(1, maxWeight);
            int v = 0;
            RandomSkillInfo pickSkill = null;

            for (RandomSkillInfo rsi : random) {
               v += rsi.getProb();
               if (rand <= v) {
                  pickSkill = rsi;
                  break;
               }
            }

            if (pickSkill != null) {
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.RANDOM_SKILL.getValue());
               packet.writeInt(pickSkill.getSkillList().size());
               pickSkill.getSkillList()
                     .forEach(
                           p -> {
                              this.randomSkillID = p.left.intValue();
                              packet.writeInt(p.left);
                              packet.writeInt(p.right);
                              if (p.left == 25111012 || p.left == 25121055) {
                                 SecondaryStatEffect e = this.getPlayer().getSkillLevelData(p.left);
                                 if (e != null) {
                                    Rect rect = new Rect(this.getPlayer().getTruePosition(), e.getLt(), e.getRb(),
                                          false);
                                    AffectedArea area = new AffectedArea(
                                          rect, this.getPlayer(), e, this.getPlayer().getTruePosition(),
                                          System.currentTimeMillis() + e.getDuration());
                                    this.getPlayer().getMap().spawnMist(area);
                                 }
                              }
                           });
               this.getPlayer().send(packet.getPacket());
               PacketEncoder packet2 = new PacketEncoder();
               packet2.writeShort(SendPacketOpcode.RANDOM_SKILL_REPEAT_COOLTIME.getValue());
               packet2.writeInt(this.getPlayer().getId());
               packet2.writeInt(reasonID);
               packet2.writeInt(effect.getX());
               this.getPlayer().getMap().broadcastMessage(packet2.getPacket());
            }
         }

         this.lastElementalFocusTime = System.currentTimeMillis();
      }
   }
}
