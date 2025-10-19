package objects.users.jobs.resistance;

import java.awt.Point;
import java.util.Collections;
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
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.item.MapleInventoryType;
import objects.summoned.Summoned;
import objects.users.jobs.Archer;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AttackPair;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class WildHunter extends Archer {
   private boolean jaguarActive = false;
   private long lastBelifTime = 0L;

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
      if (monster.getBuff(MobTemporaryStatFlag.JAGUAR_BLEEDING) != null) {
         Skill skill_ = SkillFactory.getSkill(attack.skillID);
         if (skill_ != null) {
            List<Integer> activeWeaponType = skill_.getFinalAttack();
            int weaponIdx = this.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11).getItemId() / 10000 % 100;
            long count = activeWeaponType.stream().filter(t -> t == weaponIdx).count();
            if (count > 0L) {
               MobTemporaryStatEffect e = monster.getBuff(MobTemporaryStatFlag.JAGUAR_BLEEDING);
               if (e != null) {
                  Integer value = e.getX();
                  int x = 0;
                  if (value != null) {
                     x = value;
                  }

                  this.getPlayer().send(CField.userBonusAttackRequest(33000036, false, Collections.singletonList(new Pair<>(monster.getObjectId(), 126)), x));
               }
            }
         }
      }

      if (this.getPlayer().getTotalSkillLevel(33141500) > 0) {
         List<Integer> list = SkillFactory.getSkill(33141501).getSkillList();
         if (list.contains(attack.skillID) && this.getPlayer().hasBuffBySkillID(33141503) && System.currentTimeMillis() - this.lastBelifTime > 2000L) {
            this.getPlayer().send(CField.userBonusAttackRequest(33141501, true, Collections.emptyList()));
         }
      }

      super.onAttack(monster, boss, attackPair, skill, totalDamage, attack, effect, opcode);
   }

   @Override
   public void afterAttack(
      boolean boss, AttackInfo attack, long totalDamage, SecondaryStatEffect effect, Skill skill, int multiKill, long totalExp, RecvPacketOpcode opcode
   ) {
      if (this.getPlayer().getJob() >= 3300 && this.getPlayer().getJob() <= 3312 && !this.isJaguarActive()) {
         this.getPlayer().send(jaguarActive(true));
         this.setJaguarActive(true);
      }

      if (attack.skillID == 400031032) {
         Integer value = this.getPlayer().getBuffedValue(SecondaryStatFlag.WildGrenade);
         int v = 0;
         if (value != null) {
            v = value;
         }

         if (v > 0) {
            this.getPlayer().temporaryStatSet(400031032, Integer.MAX_VALUE, SecondaryStatFlag.WildGrenade, v - 1);
         }
      }

      if (attack.skillID == 33121214) {
         this.getPlayer().addMP(-effect.getMPCon());
      }

      if (attack.skillID == 33141000) {
         Integer cnt = this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.WildVulcanAdvStack, 0);
         if (this.getPlayer().getBuffedEffect(SecondaryStatFlag.WildVulcanAdv) == null) {
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.WildVulcanAdvStack, 33141001, Integer.MAX_VALUE, Math.min(cnt + 1, 50));
         }
      }

      super.afterAttack(boss, attack, totalDamage, effect, skill, multiKill, totalExp, opcode);
   }

   @Override
   public void onActiveSkill(Skill skill, SecondaryStatEffect effect, PacketDecoder packet) {
      switch (this.getActiveSkillID()) {
         case 33001007:
         case 33001008:
         case 33001009:
         case 33001010:
         case 33001011:
         case 33001012:
         case 33001013:
         case 33001014:
         case 33001015:
            this.getPlayer().temporaryStatResetBySkillID(33001001);
            effect.applyTo(this.getPlayer(), true);
            break;
         case 33001016:
         case 33101115:
         case 33111015:
         case 33121017:
         case 33121255:
            this.getPlayer().send(CField.getJaguarSkill(this.getActiveSkillID()));
            effect.applyTo(this.getPlayer(), true);
            break;
         case 33001025: {
            Point pos = packet.readPos();
            boolean facingLeft = packet.readByte() == 1;
            List<MapleMonster> mobs = this.getPlayer()
               .getMap()
               .getMobsInRect(pos, effect.getLt().x, effect.getLt().y, effect.getRb().x, effect.getRb().y, facingLeft);
            Summoned jaguar = this.getPlayer().getSummonJaguar();
            if (jaguar == null) {
               return;
            }

            mobs.forEach(
               m -> {
                  if (m != null && m.isAlive()) {
                     boolean boss = m.getStats().isBoss();
                     int time = effect.getDuration();
                     if (boss) {
                        time /= 2;
                     }

                     Map<MobTemporaryStatFlag, MobTemporaryStatEffect> stats = new HashMap<>();
                     stats.put(
                        MobTemporaryStatFlag.DODGE_BODY_ATTACK, new MobTemporaryStatEffect(MobTemporaryStatFlag.DODGE_BODY_ATTACK, 1, 33001025, null, false)
                     );
                     stats.put(
                        MobTemporaryStatFlag.JAGUAR_PROVOKE,
                        new MobTemporaryStatEffect(MobTemporaryStatFlag.JAGUAR_PROVOKE, jaguar.getObjectId(), 33001025, null, false)
                     );
                     m.applyMonsterBuff(stats, 33001025, time, null, Collections.EMPTY_LIST);
                  }
               }
            );
            this.getPlayer().send(CField.getJaguarSkill(this.getActiveSkillID()));
            effect.applyTo(this.getPlayer(), true);
            break;
         }
         case 33111013:
         case 33121016: {
            AffectedArea area = this.getPlayer().getMap().getAffectedAreaBySkillId(this.getActiveSkillID(), this.getPlayer().getId());
            if (area != null) {
               this.getPlayer().temporaryStatResetBySkillID(this.getActiveSkillID());
               this.getPlayer().getMap().broadcastMessage(CField.removeAffectedArea(area.getObjectId(), this.getActiveSkillID(), false));
               this.getPlayer().getMap().removeMapObject(area);
            }

            Point pos = packet.readPos();
            packet.skip(4);
            byte facingLeft = packet.readByte();
            effect.applyTo(this.getPlayer(), pos, facingLeft);
            break;
         }
         case 33141503:
            this.getPlayer().temporaryStatSet(SecondaryStatFlag.NaturesBelief, this.getActiveSkillID(), effect.getDuration(), 1);
            break;
         case 400031005:
         case 500061007: {
            Point pos = packet.readPos();
            packet.skip(4);
            byte flip = packet.readByte();
            this.getPlayer().onJaguarStorm(effect, pos, this.getActiveSkillLevel(), flip);
            effect.applyTo(this.getPlayer(), true);
            break;
         }
         case 400031012: {
            this.getPlayer().temporaryStatSet(400031012, effect.getS() * 1000, SecondaryStatFlag.NotDamaged, 1);
            Point pos = packet.readPos();
            Rect rect = new Rect(pos, effect.getLt(), effect.getRb(), false);
            AffectedArea area = new AffectedArea(rect, this.getPlayer(), effect, pos, System.currentTimeMillis() + effect.getV() * 1000);
            this.getPlayer().getMap().spawnMist(area);
            effect.applyTo(this.getPlayer(), true);
            break;
         }
         default:
            super.onActiveSkill(skill, effect, packet);
      }
   }

   @Override
   public void updatePerSecond() {
      super.updatePerSecond();
      if (this.isJaguarActive()
         && this.getPlayer().getAntiMacro() != null
         && System.currentTimeMillis() - this.getPlayer().getAntiMacro().getLastAttackTime() >= 10000L) {
         this.getPlayer().send(jaguarActive(false));
         this.setJaguarActive(false);
      }
   }

   public boolean isJaguarActive() {
      return this.jaguarActive;
   }

   public void setJaguarActive(boolean jaguarActive) {
      this.jaguarActive = jaguarActive;
   }

   public void setJaguarBleeding(SecondaryStatEffect eff, SecondaryStatEffect e, MapleMonster mob) {
      boolean success = false;
      if (mob.getBuff(MobTemporaryStatFlag.JAGUAR_BLEEDING) != null) {
         MobTemporaryStatEffect e2 = mob.getBuff(MobTemporaryStatFlag.JAGUAR_BLEEDING);
         if (e.makeChanceResult() && e2 != null) {
            Integer value = e2.getX();
            int x = 0;
            if (value != null) {
               x = value;
            }

            if (x < eff.getY()) {
               mob.applyStatus(
                  this.getPlayer(),
                  new MobTemporaryStatEffect(MobTemporaryStatFlag.JAGUAR_BLEEDING, x + 1, 33000036, null, false),
                  false,
                  eff.getDuration(),
                  false,
                  eff
               );
               success = true;
            }
         }
      } else {
         mob.applyStatus(
            this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.JAGUAR_BLEEDING, 1, 33000036, null, false), false, eff.getDuration(), false, eff
         );
         success = true;
      }

      if (success && this.getPlayer().getBuffedValueDefault(SecondaryStatFlag.WildVulcanAdvStack, 0) >= 50) {
         SecondaryStatEffect effect = SkillFactory.getSkill(33141001).getEffect(this.getPlayer().getTotalSkillLevel(33141001));
         int dur = effect.getS() * 1000;
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.WildVulcanAdv, 33141001, dur, 1);
         this.getPlayer().temporaryStatSet(SecondaryStatFlag.WildVulcanAdvStack, 33141001, Integer.MAX_VALUE, 0);
      }
   }

   public void activeAnotherBite(SecondaryStatEffect eff, MapleMonster mob, int attackSkillID) {
      SecondaryStatEffect e = null;
      if (attackSkillID != 0) {
         e = SkillFactory.getSkill(attackSkillID).getEffect(this.getPlayer().getTotalSkillLevel(attackSkillID));
      }

      switch (attackSkillID) {
         case 33001016:
            this.setJaguarBleeding(eff, e, mob);
            int hp = (int)(this.getPlayer().getStat().getCurrentMaxHp(this.getPlayer()) * 0.01 * e.getQ());
            this.getPlayer().addHP(hp);
            break;
         case 33101115:
            if (Randomizer.isSuccess(e.getSubProp())) {
               mob.applyStatus(
                  this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.STUN, 1, attackSkillID, null, false), false, e.getDuration(), true, e
               );
            }

            this.setJaguarBleeding(eff, e, mob);
            break;
         case 33111015:
            this.setJaguarBleeding(eff, e, mob);
            break;
         case 33121017:
            if (Randomizer.isSuccess(e.getSubProp())) {
               if (mob.checkResistSkill(MobTemporaryStatFlag.FREEZE)) {
                  mob.applyStatus(
                     this.getPlayer(), new MobTemporaryStatEffect(MobTemporaryStatFlag.FREEZE, 1, attackSkillID, null, false), false, e.getDuration(), false, e
                  );
                  mob.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + 90000L, this.getPlayer(), 33121017);
               } else {
                  this.getPlayer()
                     .send(
                        MobPacket.monsterResist(
                           mob, this.getPlayer(), (int)((System.currentTimeMillis() - mob.getResistSkill(MobTemporaryStatFlag.FREEZE)) / 1000L), attackSkillID
                        )
                     );
               }
            }

            this.setJaguarBleeding(eff, e, mob);
            break;
         default:
            SecondaryStatEffect eff2 = this.getPlayer().getSkillLevelData(33001007);
            this.setJaguarBleeding(eff, eff2, mob);
      }
   }

   public static byte[] jaguarActive(boolean active) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.JAGUAR_ACTIVE.getValue());
      packet.write(active);
      return packet.getPacket();
   }

   @Override
   public void beforeActiveSkill(PacketDecoder packet) {
      if (this.getActiveSkillID() == 33111007) {
         this.getPlayer().temporaryStatSet(33111007, 2000, SecondaryStatFlag.indiePartialNotDamaged, 1);
      }

      super.beforeActiveSkill(packet);
   }
}
