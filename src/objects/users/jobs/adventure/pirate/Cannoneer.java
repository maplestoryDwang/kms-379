package objects.users.jobs.adventure.pirate;

import constants.ServerConstants;
import java.awt.Point;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.effect.child.DiceRoll;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.item.Item;
import objects.item.MapleInventoryType;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.jobs.Pirate;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.skills.TeleportAttackData_PointWithDirection;
import objects.users.skills.TeleportAttackElement;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class Cannoneer extends Pirate {
   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
      if (attack.skillID == 400051021) {
         this.getPlayer().temporaryStatSet(400051021, 1320, SecondaryStatFlag.indiePartialNotDamaged, 1);
      }

      if (attack.skillID == 400051075 || attack.skillID == 500061030) {
         SecondaryStatEffect poolEffect = SkillFactory.getSkill(attack.skillID - 1).getEffect(attack.skillLevel);
         if ((this.getPlayer().getPoolMakerRemain() == poolEffect.getW() || this.getPlayer().getPoolMakerRemain() == 1)
            && this.getPlayer().getMap().getAffectedAreaSize(attack.skillID + 1, this.getPlayer().getId()) < 2) {
            SecondaryStatEffect supplyEffect = SkillFactory.getSkill(attack.skillID + 1).getEffect(attack.skillLevel);
            if (supplyEffect != null) {
               Point pt = attack.forcedPos;
               Rect rect = new Rect(pt, supplyEffect.getLt(), supplyEffect.getRb(), (attack.display & 32768) > 0);
               this.getPlayer()
                  .getMap()
                  .spawnMist(new AffectedArea(rect, this.getPlayer(), supplyEffect, pt, System.currentTimeMillis() + supplyEffect.getDuration()));
            }
         }

         this.getPlayer().setPoolMakerRemain(this.getPlayer().getPoolMakerRemain() - 1);
         SecondaryStatEffect e = this.getPlayer().getSkillLevelData(attack.skillID);
         if (e != null) {
            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.POOL_MAKER_REQUEST.getValue());
            if (this.getPlayer().getPoolMakerRemain() <= 0) {
               packet.write(0);
            } else {
               packet.write(1);
               packet.writeInt(attack.skillID - 1);
               packet.writeInt(this.getPlayer().getPoolMakerRemain());
               packet.writeInt(this.getPlayer().getCooldownLimit(attack.skillID - 1));
            }

            this.getPlayer().send(packet.getPacket());
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
      if (this.getPlayer().getJob() >= 531 && this.getPlayer().getJob() <= 532) {
         Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.Roulette);
         if (value != null) {
            SecondaryStatEffect eff = SkillFactory.getSkill(5311004).getEffect(this.getPlayer().getTotalSkillLevel(5311004));
            if (value == 3) {
               if (eff != null && Randomizer.nextInt(100) < eff.getW()) {
                  MobTemporaryStatEffect eff2 = new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, eff.getU(), 5311004, null, false);
                  eff2.setDuration(eff.getV() * 1000);
                  eff2.setCancelTask(eff.getV() * 1000);
                  eff2.setValue(0);
                  monster.applyStatus(this.getPlayer(), eff2, false, eff.getV() * 1000, false, eff);
               }
            } else if (value == 4) {
               MobTemporaryStatEffect monsterStatusEffect = new MobTemporaryStatEffect(MobTemporaryStatFlag.BURNED, 1, 5311004, null, false);
               monster.applyStatus(this.getPlayer(), monsterStatusEffect, true, eff.getDOTTime() * 1000, true, eff);
            }
         }

         if (attack.skillID == 5311002 && attack.keydown == 1000) {
            this.getPlayer().temporaryStatSet(5311002, effect.getSubTime(), SecondaryStatFlag.indieCD, effect.getX());
         }

         if (attack.skillID == 5311010) {
            monster.applyStatus(
               this.getPlayer(),
               new MobTemporaryStatEffect(MobTemporaryStatFlag.ADD_DAM_SKILL_2, effect.getZ(), attack.skillID, null, false),
               false,
               effect.getDuration(),
               false,
               effect
            );
         }

         if (attack.skillID == 5311002 && this.getPlayer().getBuffedEffect(SecondaryStatFlag.KeydownTimeIgnore) != null) {
            this.getPlayer().temporaryStatReset(SecondaryStatFlag.KeydownTimeIgnore);
         }

         if (this.getPlayer().getSkillLevel(5311002) > 0) {
            SecondaryStatEffect monkeyWave = SkillFactory.getSkill(5311002).getEffect(this.getPlayer().getSkillLevel(5311002));
            if (attack.targets > 0) {
               if (ServerConstants.useCriticalDll) {
                  boolean sCri = false;

                  for (Boolean cri : attack.dllCritical) {
                     if (cri) {
                        sCri = true;
                        break;
                     }
                  }

                  if (sCri
                     && attack.skillID != 5311002
                     && Randomizer.isSuccess(monkeyWave.getW())
                     && this.getPlayer().getBuffedEffect(SecondaryStatFlag.KeydownTimeIgnore) == null) {
                     this.getPlayer().temporaryStatSet(5310008, 15000, SecondaryStatFlag.KeydownTimeIgnore, 1);
                  }
               } else if (attack.skillID != 5311002
                  && Randomizer.isSuccess(monkeyWave.getW())
                  && this.getPlayer().getBuffedEffect(SecondaryStatFlag.KeydownTimeIgnore) == null) {
                  this.getPlayer().temporaryStatSet(5310008, 15000, SecondaryStatFlag.KeydownTimeIgnore, 1);
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
      if (attack.targets > 0 && attack.skillID == 5321001) {
         long cool = this.getPlayer().getRemainCooltime(5311005);
         int v = (int)(cool * 0.01) * effect.getX();
         this.getPlayer().changeCooldown(5311005, -v);
         cool = this.getPlayer().getRemainCooltime(5320007);
         v = (int)(cool * 0.01) * effect.getX();
         this.getPlayer().changeCooldown(5320007, -v);
      }

      if (attack.targets > 0) {
         Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.Roulette);
         if (value != null && value == 1) {
            Item item = this.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11);
            if (item == null) {
               return;
            }

            int idx = item.getItemId() / 10000 % 100;
            this.getPlayer().send(CField.finalAttackRequest(true, 5310004, skill.getId(), idx, Collections.EMPTY_LIST));
         }

         if (this.getPlayer().getSkillLevel(5311013) > 0) {
            int v = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.MiniCannonBallStack, 0);
            if (v > 0
               && this.getPlayer().getOneInfoQuest(1544, "5311013").equals("1")
               && SkillFactory.getSkill(5311013).getSkillList2().contains(attack.skillID)) {
               this.getPlayer().sendRegisterExtraSkillIndex(new Point(attack.affectedSpawnPos.get(0)), this.getPlayer().isFacingLeft(), 5311013, 1);
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.MiniCannonBallStack, 5311013, Integer.MAX_VALUE, v - 1);
               effect.applyTo(this.getPlayer(), true);
            }
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      if (this.getActiveSkillPrepareID() == 400051024) {
         SecondaryStatEffect eff = SkillFactory.getSkill(400051024).getEffect(this.getPlayer().getTotalSkillLevel(400051024));
         if (eff != null) {
            Map<SecondaryStatFlag, Integer> flags = new HashMap<>();
            flags.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
            flags.put(SecondaryStatFlag.indieFlyAcc, 1);
            this.getPlayer().temporaryStatSet(400051024, this.getPlayer().getTotalSkillLevel(400051024), 750, flags);
         }
      }

      super.activeSkillPrepare(packet);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 5011007: {
            int v = this.getPlayer().getOneInfoQuestInteger(7786, "sw");
            this.getPlayer().updateOneInfo(7786, "sw", String.valueOf(v ^ 1));
            PacketEncoder p = new PacketEncoder();
            p.writeShort(SendPacketOpcode.MONKEY_ON_OFF.getValue());
            p.writeInt(this.getPlayer().getId());
            p.write(v ^ 1);
            this.getPlayer().getMap().broadcastMessage(p.getPacket());
            break;
         }
         case 5311004:
            int value = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.RouletteStack, 0);
            if (value <= 0) {
               return;
            }

            int rand = Randomizer.rand(1, 4);
            DiceRoll roll = new DiceRoll(this.getPlayer().getId(), rand, -1, this.getActiveSkillID(), this.getActiveSkillLevel(), false);
            this.getPlayer().send(roll.encodeForLocal());
            this.getPlayer().getMap().broadcastMessage(this.getPlayer(), roll.encodeForRemote(), false);
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            statups.put(SecondaryStatFlag.Roulette, rand);
            if (rand == 2) {
               statups.put(SecondaryStatFlag.indieCD, effect.getS());
            }

            this.getPlayer().temporaryStatSet(SecondaryStatFlag.RouletteStack, 5311004, Integer.MAX_VALUE, value - 1);
            this.getPlayer().temporaryStatSet(5311004, this.getActiveSkillLevel(), effect.getDuration(effect.getDuration(), this.getPlayer()), statups);
            break;
         case 5311013: {
            for (TeleportAttackElement ex : this.teleportAttackAction.actions) {
               TeleportAttackData_PointWithDirection posx = (TeleportAttackData_PointWithDirection)ex.data;
               this.getPlayer().sendRegisterExtraSkillIndex(new Point(posx.x, posx.y), posx.direction == 1, 5311013, 0);
            }

            int v = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.MiniCannonBallStack, 1);
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.MiniCannonBallStack, 5311013, Integer.MAX_VALUE, v - 1);
            effect.applyTo(this.getPlayer(), true);
            break;
         }
         case 5321004: {
            Point pos = packet.readPos();
            byte flip = packet.readByte();
            pos.x -= 110;

            for (Summoned summon : this.getPlayer().getSummons()) {
               if (summon.getSkill() == 5321004 || summon.getSkill() == 5320011 || summon.getSkill() == 5320045) {
                  this.getPlayer().getMap().broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
                  this.getPlayer().getMap().removeMapObject(summon);
                  this.getPlayer().removeVisibleMapObject(summon);
                  this.getPlayer().removeSummon(summon);
                  this.getPlayer().temporaryStatResetBySkillID(summon.getSkill());
               }
            }

            int max = 2;
            SecondaryStatEffect ex = this.getPlayer().getSkillLevelData(5320045);
            if (ex != null) {
               max += ex.getX();
            }

            int duration = effect.getDuration();
            Summoned s = new Summoned(
               this.getPlayer(), 5321004, this.getActiveSkillLevel(), pos, SummonMoveAbility.STATIONARY, flip, System.currentTimeMillis() + duration
            );
            this.getPlayer().getMap().spawnSummon(s, duration);
            this.getPlayer().addSummon(s);
            pos.x += 110;
            Point p = this.getPlayer().getMap().calcDropPos(pos, this.getPlayer().getTruePosition());
            s = new Summoned(
               this.getPlayer(), 5320011, this.getActiveSkillLevel(), p, SummonMoveAbility.STATIONARY, flip, System.currentTimeMillis() + duration
            );
            this.getPlayer().getMap().spawnSummon(s, duration);
            this.getPlayer().addSummon(s);
            pos.x += 110;
            p = this.getPlayer().getMap().calcDropPos(pos, this.getPlayer().getTruePosition());
            s = new Summoned(
               this.getPlayer(), 5320011, this.getActiveSkillLevel(), p, SummonMoveAbility.STATIONARY, flip, System.currentTimeMillis() + duration
            );
            this.getPlayer().getMap().spawnSummon(s, duration);
            this.getPlayer().addSummon(s);
            effect.applyTo(this.getPlayer(), true);
            break;
         }
         case 5321052: {
            Point pos = packet.readPos();
            byte flip = packet.readByte();
            Summoned summon = new Summoned(
               this.getPlayer(),
               5321052,
               this.getActiveSkillLevel(),
               pos,
               SummonMoveAbility.STATIONARY,
               flip,
               System.currentTimeMillis() + effect.getDuration()
            );
            this.getPlayer().getMap().spawnSummon(summon, effect.getDuration());
            this.getPlayer().addSummon(summon);
            effect.applyTo(this.getPlayer(), true);
            break;
         }
         case 400051025: {
            Point pos = new Point(packet.readInt(), packet.readInt());
            Rect rect = new Rect(pos, effect.getLt(), effect.getRb(), false);
            this.getPlayer()
               .getMap()
               .spawnMist(new AffectedArea(rect, this.getPlayer(), effect, pos, System.currentTimeMillis() + effect.getDuration() + 1000L));
            SecondaryStatEffect e = SkillFactory.getSkill(400051026).getEffect(this.getActiveSkillLevel());
            if (e != null) {
               AffectedArea area = new AffectedArea(
                  e.calculateBoundingBox(pos, false), this.getPlayer(), e, pos, System.currentTimeMillis() + e.getDuration() + 1000L
               );
               area.setStartTime(System.currentTimeMillis() + 1000L);
               this.getPlayer().getMap().spawnMist(area);
            }
            break;
         }
         case 400051038: {
            Point pos = packet.readPos();
            byte flip = packet.readByte();
            Summoned summon = new Summoned(
               this.getPlayer(),
               this.getActiveSkillID(),
               this.getActiveSkillLevel(),
               this.getPlayer().getMap().calcDropPos(pos, this.getPlayer().getTruePosition()),
               SummonMoveAbility.WALK_FOLLOW,
               flip,
               System.currentTimeMillis() + effect.getDuration()
            );
            Summoned summon2 = new Summoned(
               this.getPlayer(),
               400051052,
               this.getActiveSkillLevel(),
               this.getPlayer().getMap().calcDropPos(pos, this.getPlayer().getTruePosition()),
               SummonMoveAbility.WALK_FOLLOW,
               flip,
               System.currentTimeMillis() + effect.getDuration()
            );
            Summoned summon3 = new Summoned(
               this.getPlayer(),
               400051053,
               this.getActiveSkillLevel(),
               this.getPlayer().getMap().calcDropPos(pos, this.getPlayer().getTruePosition()),
               SummonMoveAbility.WALK_FOLLOW,
               flip,
               System.currentTimeMillis() + effect.getDuration()
            );
            this.getPlayer().getMap().spawnSummon(summon, effect.getDuration());
            this.getPlayer().getMap().spawnSummon(summon2, effect.getDuration());
            this.getPlayer().getMap().spawnSummon(summon3, effect.getDuration());
            this.getPlayer().addSummon(summon);
            this.getPlayer().addSummon(summon2);
            this.getPlayer().addSummon(summon3);
            effect.applyTo(this.getPlayer(), true);
            break;
         }
         case 400051074:
         case 500061029: {
            PacketEncoder p = new PacketEncoder();
            p.writeShort(SendPacketOpcode.POOL_MAKER_REQUEST.getValue());
            p.write(1);
            this.getPlayer().setPoolMakerRemain(effect.getW());
            p.writeInt(this.getActiveSkillID());
            System.out.println("스킬아이디 " + this.getActiveSkillID());
            p.writeInt(this.getPlayer().getPoolMakerRemain());
            System.out.println("포격가능한? " + this.getPlayer().getPoolMakerRemain());
            p.writeInt(effect.getCoolTime());
            System.out.println("쿨 " + effect.getCoolTime());
            this.getPlayer().send(p.getPacket());
            effect.applyTo(this.getPlayer());
            break;
         }
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void activeSkillCancel() {
      if (this.getActiveSkillID() == 400051024) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(400051024);
         if (eff != null) {
            this.getPlayer().giveCoolDowns(400051024, System.currentTimeMillis(), eff.getCooldown(this.getPlayer()));
            this.getPlayer().send(CField.skillCooldown(400051024, eff.getCooldown(this.getPlayer())));
         }
      }

      super.activeSkillCancel();
   }
}
