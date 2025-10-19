package objects.users.jobs.adventure.thief;

import constants.GameConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.fields.ForceAtom;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.SecondAtom;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.item.Item;
import objects.item.MapleInventoryType;
import objects.users.skills.ExtraSkillInfo;
import objects.users.skills.SecondAtomData;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.AttackPair;
import objects.utils.Randomizer;

public class NightLord extends DefaultThief {
   public long lastShowdownChallengeTime = 0L;
   public int normalThrowingStar = 0;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      super.prepareAttack(attack, effect, opcode);
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
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
      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ForceAtomOnOff) != null
         && attack.skillID != 4100012
         && attack.skillID != 4120019
         && attack.skillID != 80001770
         && attack.skillID != 25101009
         && attack.skillID != 400041061
         && attack.skillID != 400041079
         && (
            this.getPlayer().getJob() >= 400 && this.getPlayer().getJob() <= 412
               || this.getPlayer().getJob() >= 1400 && this.getPlayer().getJob() <= 1412
               || GameConstants.isPhantom(this.getPlayer().getJob())
         )) {
         Item ipp = this.getPlayer().getInventory(MapleInventoryType.USE).getItem((short)(attack.slot & 255));
         int consumeItemID = attack.consumeItemID;
         if (ipp != null && consumeItemID == 0) {
            consumeItemID = ipp.getItemId();
            this.normalThrowingStar = consumeItemID;
         }

         if (attack.bulletCashItemPos > 0 && this.getPlayer().getInventory(MapleInventoryType.CASH).getItem(attack.bulletCashItemPos) != null) {
            consumeItemID = this.getPlayer().getInventory(MapleInventoryType.CASH).getItem(attack.bulletCashItemPos).getItemId();
         }

         int cashThrowingStar = this.getPlayer().getOneInfoQuestInteger(27038, "bullet");
         if (GameConstants.isThrowingStar(consumeItemID) && cashThrowingStar > 0 && consumeItemID != cashThrowingStar) {
            consumeItemID = cashThrowingStar;
         }

         if (consumeItemID != 0) {
            int skillID = 4100011;
            if (this.getPlayer().getSkillLevel(4120018) > 0) {
               skillID = 4120018;
            }

            SecondaryStatEffect eff = SkillFactory.getSkill(skillID).getEffect(this.getPlayer().getSkillLevel(skillID));
            if (skillID == 4100011) {
               eff = SkillFactory.getSkill(skillID).getEffect(1);
            }

            if (monster.getMarkOfAssassin()) {
               this.launchExtraAttack(eff, monster, consumeItemID, skillID);
            } else if (eff != null && eff.getMonsterStati().size() > 0 && skill != null && eff.makeChanceResult()) {
               for (Entry<MobTemporaryStatFlag, Integer> z : eff.getMonsterStati().entrySet()) {
                  monster.applyStatus(
                     this.getPlayer(), new MobTemporaryStatEffect(z.getKey(), z.getValue(), skillID, null, false), eff.isPoison(), eff.getDuration(), true, eff
                  );
               }

               if (monster.getHp() - totalDamage < 0L) {
                  this.launchExtraAttack(eff, monster, consumeItemID, skillID);
               }
            }
         }
      }

      if (totalDamage > 0L) {
         if (attack.skillID == 4141001) {
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.QuadrupleThrow, 4141000, Integer.MAX_VALUE, 0);
         }

         if (attack.skillID == 4141000) {
            int value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.QuadrupleThrow, 0) + 1;
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.QuadrupleThrow, 4141000, Integer.MAX_VALUE, value);
         }

         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.BleedingToxin) != null) {
            SecondaryStatEffect effx = this.getPlayer().getBuffedEffect(SecondaryStatFlag.BleedingToxin);
            if (effx != null) {
               MobTemporaryStatEffect monsterStatusEffect = new MobTemporaryStatEffect(MobTemporaryStatFlag.BURNED, 1, effx.getSourceId(), null, false);
               monster.applyStatus(this.getPlayer(), monsterStatusEffect, true, 1000L, true, effx);
            }
         }

         if (this.getPlayer().skillisCooling(4101015) && monster.getMarkOfAssassin()) {
            this.getPlayer().changeCooldown(4101015, -1000L);
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   private void launchExtraAttack(SecondaryStatEffect eff, MapleMonster monster, int consumeItemID, int skillID) {
      int bulletCount = eff.getBulletCount();
      List<MapleMapObject> objs = this.getPlayer()
         .getMap()
         .getMapObjectsInRange(this.getPlayer().getTruePosition(), 500000.0, List.of(MapleMapObjectType.MONSTER));
      List<Integer> monsters = new ArrayList<>();
      int objsSize = objs.size();
      if (objsSize > 0) {
         for (int i = 0; i < bulletCount; i++) {
            int rand = Randomizer.rand(0, objs.size() - 1);
            if (objsSize < bulletCount) {
               if (i < objs.size()) {
                  monsters.add(objs.get(i).getObjectId());
               }
            } else {
               monsters.add(objs.get(rand).getObjectId());
               objs.remove(rand);
            }
         }
      }

      monsters.removeIf(m -> this.getPlayer().getMap().getMonsterByOid(m).getStats().isFriendly());
      if (monsters.size() > 0) {
         ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
         if (monsters.size() == 1) {
            for (int ix = 0; ix < bulletCount; ix++) {
               info.initMarkOfThief(monster.getPosition(), consumeItemID);
               ForceAtom forceAtom = new ForceAtom(
                  info, skillID + 1, this.getPlayer().getId(), true, true, monsters.get(0), ForceAtom.AtomType.MARK_OF_THIEF, monsters, 1
               );
               this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
            }
         } else {
            info.initMarkOfThief(monster.getPosition(), consumeItemID);
            ForceAtom forceAtom = new ForceAtom(
               info, skillID + 1, this.getPlayer().getId(), true, true, monsters.get(0), ForceAtom.AtomType.MARK_OF_THIEF, monsters, bulletCount
            );
            this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(forceAtom));
         }
      }
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (!attack.affectedSpawnPos.isEmpty()
         && this.getPlayer().getJob() == 412
         && attack.skillID != 400041079
         && attack.skillID != 400041062
         && attack.skillID != 400041061
         && attack.skillID != 400041018) {
         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.ThrowBlasting) != null) {
            if (SkillFactory.getSkill(400041061).getSkillList().contains(attack.skillID)) {
               Point pos = attack.affectedSpawnPos.get(Randomizer.nextInt(attack.affectedSpawnPos.size()));
               this.sendThrowBlasting(pos, (attack.display & 32768) != 0);
            }
         } else {
            int slv = this.getPlayer().getTotalSkillLevel(400041061);
            if (slv > 0) {
               Skill sk = SkillFactory.getSkill(400041061);
               SecondaryStatEffect eff = sk.getEffect(slv);
               if (eff != null && this.getPlayer().getCooldownLimit(400041062) <= 0L && sk.getSkillList().contains(attack.skillID)) {
                  Point pos = attack.affectedSpawnPos.get(Randomizer.nextInt(attack.affectedSpawnPos.size()));
                  this.getPlayer().sendRegisterExtraSkill(pos, (attack.display & 32768) != 0, 400041061);
                  this.getPlayer().send(CField.skillCooldown(400041062, eff.getSubTime() / 1000));
                  this.getPlayer().addCooldown(400041062, System.currentTimeMillis(), eff.getSubTime() / 1000);
               }
            }
         }
      }

      if (attack.skillID == 4121017) {
         List<SecondAtom.Atom> atoms = new ArrayList<>();
         Skill skill_ = SkillFactory.getSkill(4121020);
         SecondAtomData data = skill_.getSecondAtomData();
         if (!attack.allDamage.isEmpty() && this.getPlayer().checkInterval(this.lastShowdownChallengeTime, effect.getS2() * 1000)) {
            for (SecondAtomData.atom atom : data.getAtoms()) {
               this.lastShowdownChallengeTime = System.currentTimeMillis();
               SecondAtom.Atom a = new SecondAtom.Atom(
                  this.getPlayer().getMap(),
                  this.getPlayer().getId(),
                  4121020,
                  ForceAtom.SN.getAndAdd(1),
                  SecondAtom.SecondAtomType.ShowdownChallenge,
                  0,
                  null,
                  attack.attackPosition
               );
               a.setEnableDelay(atom.getEnableDelay());
               a.setExpire(atom.getExpire());
               if ((attack.display & 32768) != 0) {
                  a.setAngle(-atom.getRotate());
                  a.setPos(new Point(attack.attackPosition.x - atom.getPos().x, attack.attackPosition.y + atom.getPos().y));
               } else {
                  a.setAngle(atom.getRotate());
                  a.setPos(new Point(attack.attackPosition.x + atom.getPos().x, attack.attackPosition.y + atom.getPos().y));
               }

               a.setRotate(atom.getRotate());
               int targetID = atoms.size() < attack.allDamage.size()
                  ? attack.allDamage.get(atoms.size()).objectid
                  : attack.allDamage.get(Randomizer.nextInt(attack.allDamage.size())).objectid;
               a.setPlayerID(this.getPlayer().getId());
               a.setTargetObjectID(targetID);
               a.setCreateDelay(930);
               a.setRange(30);
               a.setAttackableCount(1);
               a.setSkillID(4121020);
               this.getPlayer().addSecondAtom(a);
               atoms.add(a);
            }

            SecondAtom secondAtom = new SecondAtom(this.getPlayer().getId(), 4121020, atoms);
            this.getPlayer().getMap().createSecondAtom(secondAtom);
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   public void sendThrowBlasting(Point position, boolean isLeft) {
      List<ExtraSkillInfo> extraSkills = new ArrayList<>();
      Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.ThrowBlasting);
      if (value != null) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(400041061);
         if (eff != null) {
            int rand = Math.min(value, Randomizer.rand(eff.getS(), eff.getW()));
            extraSkills.add(new ExtraSkillInfo(400041079, 0));
            this.getPlayer().send(CField.getRegisterExtraSkill(400041061, position.x, position.y, isLeft, extraSkills, rand));
            value = value - rand;
            if (value <= 0) {
               this.getPlayer().temporaryStatReset(SecondaryStatFlag.ThrowBlasting);
            } else {
               SecondaryStatManager statManager = new SecondaryStatManager(this.getPlayer().getClient(), this.getPlayer().getSecondaryStat());
               statManager.changeStatValue(SecondaryStatFlag.ThrowBlasting, 400041061, value);
               statManager.temporaryStatSet();
            }
         }
      }
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      if (this.getActiveSkillID() == 4141502) {
         int time = SkillFactory.getSkill(this.getActiveSkillID()).getEffect(this.getActiveSkillLevel()).getTime() * 1000;
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.LifeAndDeath, 4141502, time, 1);
      }

      if (this.getActiveSkillID() == 4121015) {
         AffectedArea area = this.getPlayer().getMap().getAffectedAreaBySkillId(4121015, this.getPlayer().getId());
         if (area != null) {
            this.getPlayer().getMap().broadcastMessage(CField.removeAffectedArea(area.getObjectId(), 4121015, false));
            this.getPlayer().getMap().removeMapObject(area);
         }
      }

      super.beforeActiveSkill(packet);
   }
}
