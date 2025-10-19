package objects.fields.gameobject.lifes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import network.encode.PacketEncoder;
import network.models.PacketHelper;
import objects.users.skills.IndieTemporaryStatEntry;
import objects.users.stats.CTS;
import objects.utils.CFlagOperator;
import objects.utils.HexTool;

public class MobTemporaryStat {
   public static void main(String[] args) {
      h("10 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
   }

   public static void h(String hex) {
      byte[] bytes = HexTool.getByteArrayFromHexString(hex);
      ByteBuffer b = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);

      for (int i = 0; i < 4; i++) {
         int dat = b.getInt();

         for (int j = 0; j < 32; j++) {
            if ((dat & 1) != 0) {
               String name = "UNKNOWN";
               int ps = 4 - i;
               int vl = 1 << j;

               for (MobTemporaryStatFlag ssf : MobTemporaryStatFlag.values()) {
                  if (ssf.getPosition() == ps && ssf.getValue() == (vl & 4294967295L)) {
                     name = ssf.name();
                     break;
                  }
               }

               System.out.printf("pos: %d, bit: %d, value: 0x%02X [%s]\n", ps, 31 - j + 32 * i, vl, name);
            }

            dat >>>= 1;
         }
      }
   }

   public static void encodeStatReset(PacketEncoder mplew, MapleMonster mob, Map<MobTemporaryStatFlag, MobTemporaryStatEffect> tempStat, int playerID) {
      PacketHelper.writeMobMask(mplew, tempStat.keySet());
      if (tempStat != null && !tempStat.isEmpty()) {
         Map<MobTemporaryStatFlag, MobTemporaryStatEffect> stat = null;
         if ((stat = CFlagOperator.hasMobTemporaryStat(tempStat, MobTemporaryStatFlag.BURNED)) != null) {
            mplew.writeInt(0);
            mplew.writeInt(stat.size());
            stat.forEach((key, value) -> {
               mplew.writeInt(value.getFromID());
               if (value.isMonsterSkill()) {
                  mplew.writeShort(value.getMobSkill().getSkillId());
                  mplew.writeShort(value.getMobSkill().getSkillLevel());
               } else if (value.getSkillID() > 0) {
                  mplew.writeInt(value.getSkillID());
               }

               mplew.writeInt(0);
            });
         }

         boolean pmCounter = false;
         MobTemporaryStatFlag stats = null;
         if (CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.P_COUNTER.getBit()) != null
            || CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.M_COUNTER.getBit()) != null) {
            pmCounter = true;
         }

         int length = tempStat.size() - (pmCounter ? 1 : 0);
         mplew.write(length);
         boolean hasMovementAffectedStat = false;

         for (MobTemporaryStatFlag flag : tempStat.keySet()) {
            if (is_movement_affected_stat(flag)) {
               hasMovementAffectedStat = true;
            }
         }

         mplew.write(hasMovementAffectedStat);
         mplew.writeZeroBytes(7);
      }
   }

   public static <E extends CTS> void encodeIndieTempStat(MapleMonster monster, Map<E, MobTemporaryStatEffect> statups, PacketEncoder mplew) {
      statups.entrySet().stream().sorted(Comparator.comparingInt(a -> a.getKey().getBit())).forEach(e -> {
         if (e.getKey().isIndie()) {
            List<IndieTemporaryStatEntry> entrys = monster.getIndieTemporaryStats((MobTemporaryStatFlag)e.getKey());
            if (entrys != null) {
               mplew.writeInt(entrys.size());

               for (IndieTemporaryStatEntry entry : entrys) {
                  mplew.writeInt(entry.getSkillID() != 400021003 && entry.getSkillID() != 72000046 && entry.getSkillID() != 72000047 ? entry.getSkillID() : 0);
                  mplew.writeInt(entry.getStatValue());
                  mplew.writeInt((int)entry.getStartTime() % 1000000000);
                  mplew.writeInt(0);
                  mplew.writeInt(entry.getDuration());
                  mplew.writeInt(0);
                  mplew.writeInt(0);
               }
            } else {
               mplew.writeInt(0);
            }
         }
      });
   }

   public static void encodeStat(
      int skillID, PacketEncoder packet, Map<MobTemporaryStatFlag, MobTemporaryStatEffect> tempStat, MapleMonster mob, int delay, boolean process
   ) {
      PacketHelper.writeMobMask(packet, tempStat.keySet());
      if (tempStat != null && !tempStat.isEmpty()) {
         encodeIndieTempStat(mob, tempStat, packet);
         MobTemporaryStatFlag flag = null;
         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.PAD.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.PDR.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.MAD.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.MDR.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.ACC.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.EVA.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.SPEED.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.STUN.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.FREEZE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_14.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.POISON.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.SEAL.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.DARKNESS.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.POWER_UP.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.MAGIC_UP.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.P_GUARD_UP.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.M_GUARD_UP.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.P_IMMUNE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.M_IMMUNE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.WEB.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.HARD_SKIN.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.NINJA_AMBUSH.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.VENOM.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.BLIND.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.SEAL_SKILL.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.DAZZLE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         boolean pmCounter = false;
         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.P_COUNTER.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
            pmCounter = true;
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.M_COUNTER.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
            pmCounter = true;
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.RISE_BY_TOSS.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.BODY_PRESSURE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.WEAKNESS.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.SHOWDOWN.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_37.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.MAGIC_CRASH.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.WEAKNESS2.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.ADD_DAM_PARTY.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.HIT_CRI_DAM_R.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.FATALITY.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.LIFTING.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.DEADLY_CHARGE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.SMITE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.ADD_DAM_SKILL.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.INCIZING.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.DODGE_BODY_ATTACK.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.DEBUFF_HEALING.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.ADD_DAM_SKILL_2.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.BODY_ATTACK.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.TEMP_MOVE_AVILITY.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.FIX_DAM_R_BUFF.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.ELEMENT_DARKNESS.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.GHOST_DISPOSITION.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.AREA_INSTALL_BY_HIT.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.B_MAGE_DEBUFF.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.JAGUAR_PROVOKE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.JAGUAR_BLEEDING.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.PINKBEAN_FLOWER_POT.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.BATTLE_PVP_HELENA_MARK.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.PSYCHIC_LOCK.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.PSYCHIC_LOCK_COOLTIME.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.PSYCHIC_GROUND_MARK.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.POWER_IMMUNE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.MULTI_PMDR.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.ELEMENT_RESET_BY_SUMMON.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.BAHAMUT_LIGHT_ELEM_ADD_DAM.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.CURSE_MARK.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.BOSS_PROP_PLUS.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.MULTI_DAM_SKILL.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.RW_LIFT_PRESS.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.RW_CHOPPING_HAMMER.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_74.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.MULTI_PMDR2.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.TIME_CURSE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.ALARM_MODE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_78.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_79.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_80.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.HIDDEN_DEBUFF.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.ANCIENT_CURSE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.PINPOINT_PIERCE_DEBUFF.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.TRANSFORMATION.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.SUCTION_BOTTLE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.TIME_BOMB.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.ADD_EFFECT.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_88.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.THESEED_STACK.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_90.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_91.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_92.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_93.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_94.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_95.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_96.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.HILLAH_STACK.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.INVINCIBLE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.EXPLOSION.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.HANG_OVER.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.CASTING.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_102.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_103.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            encodeMobSkillInfo(effect, mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.PDR.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(effect.getX());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.MDR.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(effect.getX());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.SPEED.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.write(effect == null ? 0 : effect.getValue());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.FREEZE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(effect.getX());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_14.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(effect.getX());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.P_COUNTER.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(effect.getX());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.M_COUNTER.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(effect.getX());
         }

         if (CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.P_COUNTER.getBit()) != null
            || CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.M_COUNTER.getBit()) != null) {
            packet.writeInt(0);
            packet.write(0);
            packet.writeInt(0);
         }

         if (CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.ADD_DAM_PARTY.getBit()) != null) {
            packet.writeInt(mob.getAddDamPartyFrom());
            packet.writeInt(mob.getAddDamPartyPartyID());
            packet.writeInt(mob.getAddDamPartyC());
         }

         if (CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.FATALITY.getBit()) != null) {
            packet.writeInt(mob.getFatalityFrom());
            packet.writeInt(mob.getFatalityPartyID());
            packet.writeInt(mob.getFatalityPartyValue());
            packet.writeInt(0);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.GHOST_DISPOSITION.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(1);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.ANCIENT_CURSE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(effect.getX());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.ELEMENT_DARKNESS.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(0);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.DEADLY_CHARGE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(effect.getX());
            packet.writeInt(0);
         }

         if (CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.INCIZING.getBit()) != null) {
            packet.writeInt(mob.getIncizingFrom());
            packet.writeInt(mob.getIncizingPartyValue());
            packet.writeInt(mob.getIncizingPartyID());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.B_MAGE_DEBUFF.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(effect.getX());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.BATTLE_PVP_HELENA_MARK.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(effect.getX());
         }

         if (CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.MULTI_PMDR.getBit()) != null) {
            packet.writeInt(mob.getMultiPMDRC());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.ELEMENT_RESET_BY_SUMMON.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(mob.getFoxFlameDebuffer());
            if (effect.getSkillID() == 12120024) {
               packet.writeInt(0);
               packet.writeInt(100);
               packet.writeInt(1);
            } else {
               packet.writeInt(0);
               packet.writeInt(0);
               packet.writeInt(0);
            }
         }

         if (CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.BAHAMUT_LIGHT_ELEM_ADD_DAM.getBit()) != null) {
            packet.writeInt(mob.getBahamutLightElemAddDamP());
            packet.writeInt(mob.getBahamutLightElemAddDamC());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.MULTI_DAM_SKILL.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(effect.getX());
         }

         if (CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.CURSE_MARK.getBit()) != null) {
            packet.writeInt(mob.getCurseMarkAddDamPMdr());
            packet.writeInt(mob.getCurseMarkAddDamX());
            packet.writeInt(mob.getCurseMarkAddDamPassiveReason());
         }

         if (CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.TIME_CURSE.getBit()) != null) {
            packet.writeInt(mob.getTimeCurseX());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_78.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(effect.getX());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.POISON.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(effect.getX());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.NINJA_AMBUSH.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(effect.getX());
         }

         if (CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.PINPOINT_PIERCE_DEBUFF.getBit()) != null) {
            packet.writeInt(mob.getPinpointPierceDebuffX());
         }

         if (CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.SUCTION_BOTTLE.getBit()) != null) {
            packet.writeInt(mob.getSuctionBottlePlayerID());
         }

         if (CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.TIME_BOMB.getBit()) != null) {
            packet.writeInt(0);
         }

         if (CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_93.getBit()) != null) {
            packet.writeInt(0);
            packet.writeInt(0);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.EXPLOSION.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.writeInt(effect.getX());
         }

         MobTemporaryStatFlag value = null;
         if (CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.BURNED.getBit()) != null) {
            encodeBurnedInfo(mob, packet);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.BALOG_DISABLE.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.write(effect.getX());
            packet.write(0);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.EXCHANGE_ATTACK.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            packet.write(effect.getX());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.ADD_BUFF_STAT.getBit())) != null) {
            MobTemporaryStatEffect effect = mob.getBuff(flag);
            int f = 0;
            if (effect != null) {
               f = effect.getValue();
            }

            packet.write(f);
            if (f != 0) {
               packet.writeInt(mob.getMobStatusAddPad());
               packet.writeInt(mob.getMobStatusAddMad());
               packet.writeInt(mob.getMobStatusAddPdd());
               packet.writeInt(mob.getMobStatusAddMdd());
            }
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.LINK_TEAM.getBit())) != null) {
            MobTemporaryStatEffect effectx = mob.getBuff(flag);
            packet.writeMapleAsciiString("");
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_116.getBit())) != null) {
            MobTemporaryStatEffect effectx = mob.getBuff(flag);
            packet.writeInt(effectx.getX());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_117.getBit())) != null) {
            MobTemporaryStatEffect effectx = mob.getBuff(flag);
            packet.writeLong(effectx.getX().intValue());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_118.getBit())) != null) {
            MobTemporaryStatEffect effectx = mob.getBuff(flag);
            packet.writeInt(effectx.getX());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_119.getBit())) != null) {
            MobTemporaryStatEffect effectx = mob.getBuff(flag);
            packet.writeInt(effectx.getX());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_121.getBit())) != null) {
            MobTemporaryStatEffect effectx = mob.getBuff(flag);
            packet.writeInt(effectx.getX());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_122.getBit())) != null) {
            MobTemporaryStatEffect effectx = mob.getBuff(flag);
            packet.writeInt(effectx.getX());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_120.getBit())) != null) {
            MobTemporaryStatEffect effectx = mob.getBuff(flag);
            packet.writeShort(effectx.getX());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.SOUL_EXPLOSTION.getBit())) != null) {
            MobTemporaryStatEffect effectx = mob.getBuff(flag);
            packet.writeInt(effectx.getX());
            packet.writeInt(0);
            packet.writeInt(effectx.getW());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.SEPERATE_SOUL_P.getBit())) != null) {
            MobTemporaryStatEffect effectx = mob.getBuff(flag);
            packet.writeInt(effectx.getX());
            packet.writeInt(effectx.getSkillID());
            packet.writeShort(effectx.getDuration());
            packet.writeInt(mob.getSeperateSoulPW());
            packet.writeInt(mob.getSeperateSoulPU());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.SEPERATE_SOUL_C.getBit())) != null) {
            MobTemporaryStatEffect effectx = mob.getBuff(flag);
            packet.writeInt(effectx.getX());
            packet.writeInt(effectx.getSkillID());
            packet.writeShort(effectx.getDuration());
            packet.writeInt(mob.getSeperateSoulCW());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.TRUE_SIGHT.getBit())) != null) {
            MobTemporaryStatEffect effectx = mob.getBuff(flag);
            packet.writeInt(effectx.getX());
            packet.writeInt(effectx.getSkillID());
            packet.writeInt(effectx.getDuration());
            packet.writeInt(effectx.getC());
            packet.writeInt(effectx.getP());
            packet.writeInt(effectx.getU());
            packet.writeInt(effectx.getW());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.LASER.getBit())) != null) {
            MobTemporaryStatEffect effectx = mob.getBuff(flag);
            packet.writeInt(effectx.getN());
            packet.writeShort(effectx.getMobSkill().getSkillId());
            packet.writeShort(effectx.getMobSkill().getSkillLevel());
            packet.writeInt((int)System.currentTimeMillis());
            packet.writeInt(effectx.getW());
            packet.writeInt(effectx.getU());
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_115.getBit())) != null) {
            MobTemporaryStatEffect effectx = mob.getBuff(flag);
            packet.writeInt(effectx.getX());
            packet.writeInt(0);
            packet.writeInt(0);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_123.getBit())) != null) {
            MobTemporaryStatEffect effectx = mob.getBuff(flag);
            packet.writeInt(effectx.getX());
            packet.writeInt(0);
         }

         if ((flag = CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.UNK_124.getBit())) != null) {
            MobTemporaryStatEffect effectx = mob.getBuff(flag);
            packet.writeInt(0);
            packet.writeInt(0);
            packet.writeInt(0);
         }

         if (CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.INDIE_MDR.getBit()) != null) {
            packet.writeInt(mob.getIndieMdrFrom());
            packet.writeInt(mob.getIndieMdrStack());
            packet.writeInt(0);
         }

         if (process) {
            if (CFlagOperator.getMobBuffStat(tempStat, MobTemporaryStatFlag.ELEMENT_RESET_BY_SUMMON.getBit()) != null) {
               packet.writeShort(0);
               packet.write(mob.getFoxMischiefStack());
               return;
            }

            packet.writeShort(delay);
            int length = tempStat.size() - (pmCounter ? 1 : 0);
            packet.write(length);
            boolean hasMovementAffectedStat = false;

            for (MobTemporaryStatFlag fx : tempStat.keySet()) {
               if (is_movement_affected_stat(fx)) {
                  hasMovementAffectedStat = true;
               }
            }

            packet.write(hasMovementAffectedStat);
         }
      }
   }

   public static boolean is_movement_affected_stat(MobTemporaryStatFlag stat) {
      return stat == MobTemporaryStatFlag.STUN || stat == MobTemporaryStatFlag.SPEED || stat == MobTemporaryStatFlag.FREEZE;
   }

   public static void encodeMobSkillInfo(MobTemporaryStatEffect effect, MapleMonster mob, PacketEncoder mplew) {
      mplew.writeInt(effect.getX() != null ? effect.getX() : 0);
      if (effect.isMonsterSkill()) {
         mplew.writeShort(effect.getMobSkill().getSkillId());
         mplew.writeShort(effect.getMobSkill().getSkillLevel());
      } else if (effect.getSkillID() > 0) {
         int skillID = effect.getSkillID();
         if (skillID == 152120013 || skillID == 152110009 || skillID == 152000007) {
            skillID = 152000010;
         }

         mplew.writeInt(skillID);
      } else {
         mplew.writeInt(0);
      }

      mplew.writeShort((short)(effect.getDuration() / 500));
   }

   public static void encodeBurnedInfo(MapleMonster mob, PacketEncoder mplew) {
      List<MobTemporaryStatEffect> poisons = mob.getAllBurned();
      mplew.write(poisons.size());
      int superPos = 0;

      for (MobTemporaryStatEffect poison : poisons) {
         mplew.writeInt(poison.getFromID());
         mplew.writeInt(poison.getSkillID());
         mplew.writeLong(poison.getPoisonDamage());
         mplew.writeInt(poison.getInterval());
         mplew.writeInt(0);
         mplew.writeInt(poison.getDotTickIdx());
         mplew.writeInt(poison.getDotAnimation());
         mplew.writeInt(poison.getDotCount());
         mplew.writeInt(superPos++);
         mplew.writeInt(poison.getAttackDelay());
         mplew.writeInt(0);
         mplew.writeInt(poison.getDotTickIdx());
         mplew.writeInt(poison.getDotTickDamR());
         mplew.writeInt(poison.getMaxDotTickDamR());
      }
   }
}
