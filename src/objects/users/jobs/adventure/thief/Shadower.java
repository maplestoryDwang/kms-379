package objects.users.jobs.adventure.thief;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import network.RecvPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.processors.AttackInfo;
import network.models.CField;
import objects.effect.child.SkillEffect;
import objects.fields.ForceAtom;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.Drop;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class Shadower extends DefaultThief {
   private int sonicBlowAttackCount = 0;
   private int pickPocketX = 0;
   private int eviscerateDebuffTarget = 0;
   private int mistTimeCheck = 0;

   @Override
   public void prepareAttack(AttackInfo attack, SecondaryStatEffect effect, RecvPacketOpcode opcode) {
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
      if (this.getPlayer().getBuffedValue(SecondaryStatFlag.PickPocket) != null) {
         switch (attack.skillID) {
            case 0:
            case 4001334:
            case 4201005:
            case 4201012:
            case 4211002:
            case 4211004:
            case 4211011:
            case 4221007:
            case 4221010:
            case 4221014:
            case 4221016:
            case 4221017:
            case 4221052:
            case 4241000:
            case 4241001:
            case 4241002:
            case 4241003:
            case 4241004:
            case 400041002:
            case 400041003:
            case 400041004:
            case 400041005:
            case 400041025:
            case 400041026:
            case 400041027:
            case 400041039:
            case 500061025:
            case 500061026:
            case 500061027:
            case 500061028:
               this.handlePickPocket(this.getPlayer(), monster, attackPair, attack.skillID);
         }
      }

      if (totalDamage > 0L && this.getPlayer().getBuffedValue(SecondaryStatFlag.Steal) != null) {
         monster.handleSteal(this.getPlayer());
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if ((attack.skillID == 4221016 || attack.skillID == 4241001 || attack.skillID == 4241002 || attack.skillID == 4241003 || attack.skillID == 4241004)
         && attack.targets > 0) {
         this.getPlayer().temporaryStatReset(SecondaryStatFlag.EnhanceAssassinate);
      }

      if (attack.skillID == 400041069) {
         SecondaryStatEffect eff = this.getPlayer().getSkillLevelData(400041069);
         if (eff != null) {
            this.getPlayer().sendRegisterExtraSkill(attack.forcedPos, (attack.display & 32768) != 0, 400041069);
            this.getPlayer().send(CField.skillCooldown(400041069, eff.getCooldown(this.getPlayer())));
            this.getPlayer().addCooldown(400041069, System.currentTimeMillis(), eff.getCooldown(this.getPlayer()));
         }
      }

      if (attack.skillID == 4241002 || attack.skillID == 4241003 || attack.skillID == 4241004) {
         int duration = effect.getQ() * 1000;
         if (this.getPlayer().getBuffedValue(SecondaryStatFlag.DarkSight) != null) {
            Integer reason = this.getPlayer().getSecondaryStatReason(SecondaryStatFlag.DarkSight);
            if (reason != null && reason != 400001023) {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.DarkSight, 4241005, duration, 1);
            }
         }

         int SixthAssassination = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.SixthAssassination, 0);
         if (SixthAssassination == 0) {
            if (this.getPlayer().getRemainCooltime(4241002) <= 0L) {
               SecondaryStatEffect eff = SkillFactory.getSkill(4241002).getEffect(this.getPlayer().getTotalSkillLevel(4241002));
               int cooldown = eff.getCoolTime();
               this.getPlayer().send(CField.skillCooldown(4241002, cooldown));
               this.getPlayer().addCooldown(4241002, System.currentTimeMillis(), cooldown);
            }
         } else {
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.SixthAssassination, 4241004, Integer.MAX_VALUE, Math.max(SixthAssassination - 1, 0));
         }
      }

      if (attack.skillID >= 400041025
         && attack.skillID <= 400041027
         && this.getPlayer().getBuffedEffect(SecondaryStatFlag.indiePartialNotDamaged, 400041025) == null) {
         this.getPlayer().temporaryStatSet(400041025, 1800, SecondaryStatFlag.indiePartialNotDamaged, 1);
      }

      if (attack.skillID == 400041039) {
         this.sonicBlowAttackCount++;
      }

      if (attack.skillID >= 400041025 && attack.skillID <= 400041027) {
         this.getPlayer().temporaryStatResetBySkillID(SecondaryStatFlag.EviscerateDebuff, 400041025);
      } else {
         this.tryApplyEviscerate(attack.skillID, attack);
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void activeSkillPrepare(PacketDecoder packet) {
      if (this.getActiveSkillPrepareID() == 400041039) {
         this.sonicBlowAttackCount = 0;
         SecondaryStatEffect eff = SkillFactory.getSkill(400041039).getEffect(this.getPlayer().getTotalSkillLevel(400041039));
         if (eff != null) {
            this.getPlayer().temporaryStatSet(400041039, eff.getZ() * 1000, SecondaryStatFlag.indiePartialNotDamaged, 1);
         }
      }

      super.activeSkillPrepare(packet);
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      super.beforeActiveSkill(packet);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 4211006:
         case 4221019:
            List<Point> startPos = new ArrayList<>();
            List<Drop> removes = new ArrayList<>();

            for (Drop item : this.getPlayer().getMap().getAllItemsThreadsafe()) {
               if (item.getExplosiveDrop() > 0 && item.getOwner() == this.getPlayer().getId()) {
                  removes.add(item);
                  startPos.add(item.getPosition());
               }
            }

            List<MapleMapObject> objects = new ArrayList<>();
            objects.addAll(this.getPlayer().getMap().getMapObjectsInRange(this.getPlayer().getPosition(), 320000.0, Arrays.asList(MapleMapObjectType.MONSTER)));
            Collections.shuffle(objects);
            List<Integer> targets = new ArrayList<>();
            int idx = 0;

            for (int i = 0; i < objects.size(); i++) {
               MapleMonster mob_ = (MapleMonster)objects.get(i);
               targets.add(mob_.getObjectId());
               if (idx++ >= removes.size()) {
                  break;
               }
            }

            removes.forEach(ix -> {
               this.getPlayer().getMap().removeMapObject(ix);
               this.getPlayer().getMap().broadcastMessage(CField.removeItemFromMap(ix.getObjectId(), 0, this.getPlayer().getId()));
            });
            ForceAtom.AtomInfo atomInfo = new ForceAtom.AtomInfo();
            atomInfo.initMesoExplosion(startPos);
            ForceAtom atom = new ForceAtom(
               atomInfo,
               this.getActiveSkillID() == 4221019 ? 4220021 : 4210014,
               this.getPlayer().getId(),
               false,
               true,
               this.getPlayer().getId(),
               this.getActiveSkillID() == 4221019 ? ForceAtom.AtomType.BLOOD_MESO_EXPLOSION : ForceAtom.AtomType.MESO_EXPLOSION,
               targets,
               startPos.size()
            );
            this.getPlayer().getMap().broadcastMessage(CField.getCreateForceAtom(atom));
            PacketEncoder p = new PacketEncoder();
            p.write(true);
            SkillEffect e = new SkillEffect(this.getPlayer().getId(), this.getPlayer().getLevel(), this.getActiveSkillID(), this.getActiveSkillLevel(), p);
            this.getPlayer().send(e.encodeForLocal());
            this.getPlayer().getMap().broadcastMessage(this.getPlayer(), e.encodeForRemote(), false);
            effect.applyTo(this.getPlayer(), true);
            if (!this.getPlayer().hasBuffBySkillID(4221020)) {
               this.getPlayer().temporaryStatSet(SecondaryStatFlag.EnhanceAssassinate, 4221020, Integer.MAX_VALUE, 1);
            }
            break;
         case 4221006:
            this.mistTimeCheck = 0;
            if (this.getPlayer().getTotalSkillLevel(4241002) > 0) {
               SecondaryStatEffect eff = SkillFactory.getSkill(4241002).getEffect(this.getPlayer().getTotalSkillLevel(4241002));
               int max = eff.getW();
               int SixthAssassination = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.SixthAssassination, 0);
               this.getPlayer()
                  .temporaryStatSet(SecondaryStatFlag.SixthAssassination, 4241004, Integer.MAX_VALUE, Math.min(max, SixthAssassination + eff.getZ()));
            }
         case 4221052:
            if (this.getPlayer().getTotalSkillLevel(4241002) > 0) {
               SecondaryStatEffect eff = SkillFactory.getSkill(4241002).getEffect(this.getPlayer().getTotalSkillLevel(4241002));
               int max = eff.getW();
               int SixthAssassination = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.SixthAssassination, 0);
               this.getPlayer()
                  .temporaryStatSet(SecondaryStatFlag.SixthAssassination, 4241004, Integer.MAX_VALUE, Math.min(max, SixthAssassination + eff.getW2()));
            }
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      this.updatePickPocketX();
      this.checkSixthAssassinationInMist();
   }

   public void tryApplyEviscerate(int skillID, AttackInfo attackInfo) {
      if (skillID == 4221016 || skillID == 400041039 || skillID == 4241001 || skillID == 4241003) {
         SecondaryStatEffect effect = this.getPlayer().getSkillLevelData(400041025);
         if (effect == null) {
            return;
         }

         if (attackInfo.targets == 0) {
            return;
         }

         AttackPair lastAttack = attackInfo.allDamage.get(attackInfo.allDamage.size() - 1);
         if (lastAttack != null) {
            MapleMonster mob = this.getPlayer().getMap().getMonsterByOid(lastAttack.objectid);
            if (mob != null) {
               Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.EviscerateDebuff);
               int v = 0;
               if (value != null) {
                  v = value;
               }

               if (mob.getObjectId() == this.eviscerateDebuffTarget) {
                  v = Math.min(v + 1, effect.getS2());
               } else {
                  v = 1;
                  this.eviscerateDebuffTarget = mob.getObjectId();
               }

               this.getPlayer().temporaryStatSet(400041025, effect.getDuration(), SecondaryStatFlag.EviscerateDebuff, v);
            }
         }
      }
   }

   public void handlePickPocket(MapleCharacter player, MapleMonster mob, AttackPair oned, int skillID) {
      for (Pair<Long, Boolean> eachde : oned.attack) {
         SecondaryStatEffect effect = SkillFactory.getSkill(4211003).getEffect(player.getTotalSkillLevel(4211003));
         if (effect != null) {
            if (this.getPlayer().getTotalSkillLevel(4221018) > 0) {
               effect = SkillFactory.getSkill(4221018).getEffect(this.getPlayer().getTotalSkillLevel(4221018));
            }

            int y = effect.getY();
            if (player.getTotalSkillLevel(4220045) > 0) {
               SecondaryStatEffect eff = SkillFactory.getSkill(4220045).getEffect(player.getTotalSkillLevel(4220045));
               if (eff != null) {
                  y += eff.getBulletCount();
               }
            }

            if (this.pickPocketX < y && Randomizer.isSuccess(player.getStat().pickRate / (skillID == 4221017 ? 2 : 1))) {
               player.getMap()
                  .spawnMesoDrop(
                     1,
                     new Point((int)(mob.getTruePosition().getX() + Randomizer.nextInt(100) - 50.0), (int)mob.getTruePosition().getY()),
                     mob,
                     player,
                     false,
                     (byte)0,
                     (byte)(this.getPlayer().hasBuffBySkillID(4221018) ? 4 : 1)
                  );
               this.updatePickPocketX();
            }
         }
      }
   }

   public void updatePickPocketX() {
      int count = 0;

      for (Drop item : this.getPlayer().getMap().getAllItemsThreadsafe()) {
         if (item.getExplosiveDrop() > 0 && !item.shouldExpire(System.currentTimeMillis()) && item.getOwner() == this.getPlayer().getId()) {
            count++;
         }
      }

      if (count != this.pickPocketX) {
         SecondaryStatEffect effect = SkillFactory.getSkill(4211003).getEffect(this.getPlayer().getTotalSkillLevel(4211003));
         if (this.getPlayer().getTotalSkillLevel(4221018) > 0) {
            effect = SkillFactory.getSkill(4221018).getEffect(this.getPlayer().getTotalSkillLevel(4221018));
         }

         if (effect != null) {
            int y = effect.getY();
            if (this.getPlayer().getTotalSkillLevel(4220045) > 0) {
               SecondaryStatEffect eff = SkillFactory.getSkill(4220045).getEffect(this.getPlayer().getTotalSkillLevel(4220045));
               if (eff != null) {
                  y += eff.getBulletCount();
               }
            }

            count = Math.min(count, y);
            this.pickPocketX = count;
            this.getPlayer()
               .temporaryStatSet(this.getPlayer().hasBuffBySkillID(4221018) ? 4221018 : 4211003, Integer.MAX_VALUE, SecondaryStatFlag.PickPocket, 1);
         }
      }
   }

   public void checkSixthAssassinationInMist() {
      if (this.getPlayer().getTotalSkillLevel(4241002) > 0) {
         AffectedArea area = this.getPlayer().getMap().getMistBySkillId(4221006);
         if (area != null && area.getOwnerId() == this.getPlayer().getId()) {
            Rect rect = area.getMistRect();
            if (rect != null) {
               Point pos = this.getPlayer().getPosition();
               if (rect.getLeft() <= pos.x && rect.getTop() <= pos.y && rect.getRight() >= pos.x && rect.getBottom() >= pos.y) {
                  this.mistTimeCheck++;
               } else {
                  this.mistTimeCheck = 0;
               }
            } else {
               Rectangle rectangle = area.getBox();
               if (rectangle != null) {
                  if (rectangle.contains(this.getPlayer().getPosition())) {
                     this.mistTimeCheck++;
                  } else {
                     this.mistTimeCheck = 0;
                  }
               }
            }

            SecondaryStatEffect eff = SkillFactory.getSkill(4241002).getEffect(this.getPlayer().getTotalSkillLevel(4241002));
            if (this.mistTimeCheck >= eff.getS2()) {
               int SixthAssassination = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.SixthAssassination, 0);
               int max = eff.getW();
               this.getPlayer()
                  .temporaryStatSet(SecondaryStatFlag.SixthAssassination, 4241004, Integer.MAX_VALUE, Math.min(max, SixthAssassination + eff.getV2()));
               this.mistTimeCheck = 0;
            }
         }
      }
   }

   @Override
   public void encodeForLocal(SecondaryStatFlag flag, PacketEncoder packet) {
      switch (flag) {
         case CriticalGrowing:
            packet.writeInt(0);
            break;
         case PickPocket:
            packet.writeInt(this.pickPocketX);
            break;
         case EviscerateDebuff:
            packet.writeInt(this.eviscerateDebuffTarget);
            break;
         default:
            super.encodeForLocal(flag, packet);
      }
   }

   public void debugPickPocket() {
      int count = 0;

      for (Drop item : this.getPlayer().getMap().getAllItemsThreadsafe()) {
         if (item.getExplosiveDrop() > 0 && item.getOwner() == this.getPlayer().getId()) {
            count++;
         }
      }

      Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.PickPocket);
      this.getPlayer()
         .dropMessage(5, "PickPocketX : " + this.pickPocketX + " // Map Meso Count : " + count + " // Buffed : " + (value == null ? "null" : value));
   }
}
