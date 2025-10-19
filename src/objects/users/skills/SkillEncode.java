package objects.users.skills;

import constants.GameConstants;
import database.loader.CharacterSaveFlag2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.models.PacketHelper;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.stats.HyperStat;

public class SkillEncode {
   private final MapleCharacter chr;
   public static final int NORMAL_SKILL = 1;
   public static final int EXPANSION_SKILL = 2;
   public static final int INHERITING_SKILL = 3;

   public SkillEncode(MapleCharacter chr) {
      this.chr = chr;
   }

   public void encodeSkills(PacketEncoder e, Map<Integer, SkillEncode.SkillEncodeEntry> skillData) {
      e.writeShort(skillData.size());
      this.encodeNormalSkills(
         e, skillData.entrySet().stream().filter(d -> d.getValue().skillType == 1).collect(Collectors.toMap(Entry::getKey, Entry::getValue))
      );
      this.encodeExpansionSkills(
         e, skillData.entrySet().stream().filter(d -> d.getValue().skillType == 2).collect(Collectors.toMap(Entry::getKey, Entry::getValue))
      );
      this.encodeInheritingSkills(
         e, skillData.entrySet().stream().filter(d -> d.getValue().skillType == 3).collect(Collectors.toMap(Entry::getKey, Entry::getValue))
      );
   }

   public void encodeNormalSkills(PacketEncoder e, Map<Integer, SkillEncode.SkillEncodeEntry> skillData) {
      for (Entry<Integer, SkillEncode.SkillEncodeEntry> skill : skillData.entrySet()) {
         e.writeInt(skill.getKey());
         skill.getValue().encode(skill.getKey(), e);
      }
   }

   public void encodeExpansionSkills(PacketEncoder e, Map<Integer, SkillEncode.SkillEncodeEntry> skillData) {
      for (Entry<Integer, SkillEncode.SkillEncodeEntry> skill : skillData.entrySet()) {
         e.writeInt(skill.getKey());
         skill.getValue().encode(skill.getKey(), e);
      }
   }

   public void encodeInheritingSkills(PacketEncoder e, Map<Integer, SkillEncode.SkillEncodeEntry> skillData) {
      for (Entry<Integer, SkillEncode.SkillEncodeEntry> skill : skillData.entrySet()) {
         e.writeInt(skill.getKey());
         skill.getValue().encodeInheritingSkills(e);
      }
   }

   public void encodeLinkLevels(PacketEncoder e, Map<Integer, Short> skillData) {
      e.writeShort(skillData.size());

      for (Entry<Integer, Short> skill : skillData.entrySet()) {
         e.writeInt(skill.getKey());
         e.writeShort(skill.getValue());
      }
   }

   public void encodeLinkSkills(PacketEncoder e) {
      e.writeInt(this.chr.getLinkSkill().getLinkSkills().size());

      for (LinkSkillEntry s : this.chr.getLinkSkill().getLinkSkills()) {
         e.writeInt(s.getLinkingPlayerID());
         e.writeInt(s.getLinkedPlayerID());
         e.writeInt(s.getRealSkillID());
         e.writeShort(s.getSkillLevel());
         e.writeLong(PacketHelper.getTime(-2L));
         if (SkillFactory.getSkill(s.getSkillID()) != null) {
            e.writeInt(SkillFactory.getSkill(s.getSkillID()).getType() == 51 ? 1 : 0);
         } else {
            e.writeInt(0);
         }
      }
   }

   public Map<Integer, Short> getLinkLevelData() {
      Map<Integer, Short> skillMap = new HashMap<>();
      this.chr.getLinkSkill().getLinkSkills().forEach(skill -> {
         if (skill.getLinkingPlayerID() != this.chr.getId() && skill.getLinkedPlayerID() == this.chr.getId()) {
            skillMap.put(skill.getRealSkillID(), skill.getSkillLevel());
         }
      });
      return skillMap;
   }

   public Map<Integer, SkillEncode.SkillEncodeEntry> getSkillData() {
      Map<Integer, SkillEncode.SkillEncodeEntry> skillMap = new HashMap<>();
      this.chr.getSkills().forEach((k, v) -> {
         if (!skillMap.containsKey(k.getId()) && getLinkSkill(this.chr.getJob()) != k.getId() && !GameConstants.isLinkSkill(k.getId())) {
            skillMap.put(k.getId(), new SkillEncode.SkillEncodeEntry(1, v.skillevel, v.masterlevel, -1, v.expiration));
         }
      });
      Map<Integer, Integer> expansionSkillMap = new HashMap<>();
      LinkSkill linkSkill = this.chr.getLinkSkill();
      if (linkSkill != null) {
         linkSkill.getLinkSkills().forEach(skill -> {
            int ordinarySkillID = getStackedLinkSkill(skill.getSkillID());
            if (skill.getLinkingPlayerID() != this.chr.getId() && skill.getLinkedPlayerID() == this.chr.getId() && ordinarySkillID != skill.getSkillID()) {
               if (expansionSkillMap.containsKey(ordinarySkillID)) {
                  expansionSkillMap.put(ordinarySkillID, expansionSkillMap.get(ordinarySkillID) + skill.getSkillLevel());
               } else {
                  expansionSkillMap.put(ordinarySkillID, Integer.valueOf(skill.getSkillLevel()));
               }
            }
         });
         expansionSkillMap.forEach((k, v) -> {
            if (!skillMap.containsKey(k)) {
               skillMap.put(k, new SkillEncode.SkillEncodeEntry(2, v, v, -1, -1L));
            }
         });
         linkSkill.getLinkSkills()
            .forEach(
               skill -> {
                  if (skill.getLinkingPlayerID() == this.chr.getId()) {
                     skillMap.put(
                        skill.getSkillID(),
                        new SkillEncode.SkillEncodeEntry(3, skill.getSkillLevel(), skill.getSkillLevel(), skill.getLinkedPlayerID(), skill.getLinkedTime())
                     );
                  } else if (skill.getLinkedPlayerID() == this.chr.getId()) {
                     skillMap.put(
                        skill.getRealSkillID(),
                        new SkillEncode.SkillEncodeEntry(3, skill.getSkillLevel(), skill.getSkillLevel(), skill.getLinkingPlayerID(), skill.getLinkedTime())
                     );
                  }
               }
            );
      }

      return skillMap;
   }

   public static void encode(PacketEncoder o, MapleCharacter player) {
      SkillEncode e = new SkillEncode(player);
      Map<Integer, SkillEncode.SkillEncodeEntry> skillData = e.getSkillData();
      Map<Integer, Short> linkLevelData = e.getLinkLevelData();
      o.write(true);
      e.encodeSkills(o, skillData);
      e.encodeLinkLevels(o, linkLevelData);
      e.encodeLinkSkills(o);
      HyperStat hStat = player.getHyperStat();
      if (hStat == null) {
         hStat = new HyperStat(player, 0);
         player.setHyperStat(hStat);
      }

      hStat.encode(o);
   }

   public static int getStackedLinkSkill(int skillID) {
      int realSkillID = skillID;
      if (skillID >= 80000066 && skillID <= 80000070) {
         realSkillID = 80000055;
      } else if ((skillID < 80000333 || skillID > 80000335) && skillID != 80000378) {
         if (skillID >= 80002759 && skillID <= 80002761) {
            realSkillID = 80002758;
         } else if (skillID >= 80002763 && skillID <= 80002765) {
            realSkillID = 80002762;
         } else if (skillID >= 80002767 && skillID <= 80002769) {
            realSkillID = 80002766;
         } else if (skillID >= 80002771 && skillID <= 80002773) {
            realSkillID = 80002770;
         } else if ((skillID < 80002775 || skillID > 80002776) && skillID != 80000000) {
            if (skillID >= 252 && skillID <= 254) {
               realSkillID = 80002758;
            } else if (skillID >= 255 && skillID <= 257) {
               realSkillID = 80002762;
            } else if (skillID >= 258 && skillID <= 260) {
               realSkillID = 80002766;
            } else if (skillID >= 261 && skillID <= 263) {
               realSkillID = 80002770;
            } else if ((skillID < 264 || skillID > 265) && skillID != 110) {
               if (skillID >= 10000255 && skillID <= 10000259) {
                  realSkillID = 80000055;
               } else if (skillID >= 30000074 && skillID <= 30000077) {
                  realSkillID = 80000329;
               }
            } else {
               realSkillID = 80002774;
            }
         } else {
            realSkillID = 80002774;
         }
      } else {
         realSkillID = 80000329;
      }

      return realSkillID;
   }

   public static int getLinkSkill(int job) {
      if (GameConstants.isHoyoung(job)) {
         return 160000001;
      } else if (GameConstants.isArk(job)) {
         return 150010241;
      } else if (GameConstants.isIllium(job)) {
         return 150000017;
      } else if (GameConstants.isAdele(job)) {
         return 150020241;
      } else if (GameConstants.isZero(job)) {
         return 100000271;
      } else if (GameConstants.isLara(job)) {
         return 160010001;
      } else if (GameConstants.isKhali(job)) {
         return 150030241;
      } else {
         switch (job) {
            case 110:
            case 111:
            case 112:
               return 252;
            case 120:
            case 121:
            case 122:
               return 253;
            case 130:
            case 131:
            case 132:
               return 254;
            case 210:
            case 211:
            case 212:
               return 255;
            case 220:
            case 221:
            case 222:
               return 256;
            case 230:
            case 231:
            case 232:
               return 257;
            case 310:
            case 311:
            case 312:
               return 258;
            case 320:
            case 321:
            case 322:
               return 259;
            case 330:
            case 331:
            case 332:
               return 260;
            case 410:
            case 411:
            case 412:
               return 261;
            case 420:
            case 421:
            case 422:
               return 262;
            case 430:
            case 431:
            case 432:
            case 433:
            case 434:
               return 263;
            case 501:
            case 530:
            case 531:
            case 532:
               return 110;
            case 510:
            case 511:
            case 512:
               return 264;
            case 520:
            case 521:
            case 522:
               return 265;
            case 1100:
            case 1110:
            case 1111:
            case 1112:
               return 10000255;
            case 1200:
            case 1210:
            case 1211:
            case 1212:
               return 10000256;
            case 1300:
            case 1310:
            case 1311:
            case 1312:
               return 10000257;
            case 1400:
            case 1410:
            case 1411:
            case 1412:
               return 10000258;
            case 1500:
            case 1510:
            case 1511:
            case 1512:
               return 10000259;
            case 2100:
            case 2110:
            case 2111:
            case 2112:
               return 20000297;
            case 2200:
            case 2210:
            case 2211:
            case 2212:
            case 2213:
            case 2214:
            case 2215:
            case 2216:
            case 2217:
            case 2218:
               return 20010294;
            case 2300:
            case 2310:
            case 2311:
            case 2312:
               return 20021110;
            case 2400:
            case 2410:
            case 2411:
            case 2412:
               return 20030204;
            case 2500:
            case 2510:
            case 2511:
            case 2512:
               return 20050286;
            case 2700:
            case 2710:
            case 2711:
            case 2712:
               return 20040218;
            case 3100:
            case 3110:
            case 3111:
            case 3112:
               return 30010112;
            case 3101:
            case 3120:
            case 3121:
            case 3122:
               return 30010241;
            case 3200:
            case 3210:
            case 3211:
            case 3212:
               return 30000074;
            case 3300:
            case 3310:
            case 3311:
            case 3312:
               return 30000075;
            case 3500:
            case 3510:
            case 3511:
            case 3512:
               return 30000076;
            case 3600:
            case 3610:
            case 3611:
            case 3612:
               return 30020233;
            case 3700:
            case 3710:
            case 3711:
            case 3712:
               return 30000077;
            case 5100:
            case 5110:
            case 5111:
            case 5112:
               return 50001214;
            case 6100:
            case 6110:
            case 6111:
            case 6112:
               return 60000222;
            case 6300:
            case 6310:
            case 6311:
            case 6312:
               return 60030241;
            case 6400:
            case 6410:
            case 6411:
            case 6412:
               return 60020218;
            case 6500:
            case 6510:
            case 6511:
            case 6512:
               return 60011219;
            case 10112:
               return 100000271;
            case 14200:
            case 14210:
            case 14211:
            case 14212:
               return 140000292;
            default:
               return -1;
         }
      }
   }

   public static List<Integer> getAdventureAnotherJobLink(int job) {
      List<Integer> ret = new ArrayList<>();
      switch (job) {
         case 110:
         case 111:
         case 112:
            ret.add(253);
            ret.add(254);
            return ret;
         case 120:
         case 121:
         case 122:
            ret.add(252);
            ret.add(254);
            return ret;
         case 130:
         case 131:
         case 132:
            ret.add(252);
            ret.add(253);
            return ret;
         case 210:
         case 211:
         case 212:
            ret.add(256);
            ret.add(257);
            return ret;
         case 220:
         case 221:
         case 222:
            ret.add(255);
            ret.add(257);
            return ret;
         case 230:
         case 231:
         case 232:
            ret.add(255);
            ret.add(256);
            return ret;
         case 310:
         case 311:
         case 312:
            ret.add(259);
            ret.add(260);
            return ret;
         case 320:
         case 321:
         case 322:
            ret.add(258);
            ret.add(260);
            return ret;
         case 330:
         case 331:
         case 332:
            ret.add(258);
            ret.add(259);
            return ret;
         case 410:
         case 411:
         case 412:
            ret.add(262);
            ret.add(263);
            return ret;
         case 420:
         case 421:
         case 422:
            ret.add(261);
            ret.add(263);
            return ret;
         case 430:
         case 431:
         case 432:
         case 433:
         case 434:
            ret.add(261);
            ret.add(262);
            return ret;
         case 501:
         case 530:
         case 531:
         case 532:
            ret.add(264);
            ret.add(265);
            return ret;
         case 510:
         case 511:
         case 512:
            ret.add(110);
            ret.add(265);
            return ret;
         case 520:
         case 521:
         case 522:
            ret.add(110);
            ret.add(264);
            return ret;
         default:
            return ret;
      }
   }

   public static void skillAlarm(PacketDecoder slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr != null && chr.getSkillAlarm() != null) {
         SkillAlarm alarm = chr.getSkillAlarm();
         byte[] array = slea.getByteArray();
         int type = slea.readInt();
         switch (type) {
            case 0:
            case 1: {
               int skillID = slea.readInt();
               int index = slea.readInt();
               boolean set = slea.readByte() != 0;
               alarm.getSkillList()[index] = skillID;
               alarm.getOnOffCheck()[index] = set;
               break;
            }
            case 2: {
               int index = slea.readInt();
               alarm.getSkillList()[index] = 0;
               break;
            }
            case 3:
               int firstSkillID = slea.readInt();
               int firstPos = slea.readInt();
               int secondSkillID = slea.readInt();
               int secondPos = slea.readInt();
               alarm.getSkillList()[secondPos] = firstSkillID;
               alarm.getSkillList()[firstPos] = secondSkillID;
               break;
            default:
               return;
         }

         chr.setSaveFlag2(chr.getSaveFlag2() | CharacterSaveFlag2.SKILL_ALARM.getFlag());
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.SKILL_ALARM.getValue());

         for (int x = 2; x < array.length; x++) {
            packet.write(array[x]);
         }

         chr.send(packet.getPacket());
      }
   }

   public static boolean is_stealable_skill(int a1) {
      boolean v5;
      if (a1 > 3120022) {
         if (a1 > 4341006) {
            if (a1 > 5121016) {
               if (a1 - 5201005 <= 1 || a1 == 5201011 || a1 == 5221022) {
                  return false;
               }

               v5 = a1 == 32121010;
            } else {
               if (a1 == 5121016) {
                  return false;
               }

               int v14 = a1 - 5001005;
               if (v14 == 0) {
                  return false;
               }

               int v15 = v14 - 9997;
               if (v15 == 0) {
                  return false;
               }

               int v16 = v15 - 6;
               if (v16 == 0) {
                  return false;
               }

               v5 = v16 == 99994;
            }
         } else {
            if (a1 == 4341006) {
               return false;
            }

            if (a1 > 4211003) {
               int v11 = a1 - 4211006;
               if (0 == v11) {
                  return false;
               }

               int v12 = v11 - 2;
               if (0 == v12) {
                  return false;
               }

               int v13 = v12 - 9998;
               if (0 == v13) {
                  return false;
               }

               v5 = v13 == 109996;
            } else {
               if (a1 == 4211003) {
                  return false;
               }

               int v8 = a1 - 3220006;
               if (0 == v8) {
                  return false;
               }

               int v9 = v8 - 781005;
               if (0 == v9) {
                  return false;
               }

               int v10 = v9 - 2;
               if (0 == v10) {
                  return false;
               }

               v5 = v10 == 109989;
            }
         }
      } else {
         if (a1 == 3120022) {
            return false;
         }

         if (a1 > 1321024) {
            int v6 = a1 - 2321001;
            if (Math.abs(v6) <= 53) {
               long v7 = 9007199254741001L;
               if ((v7 & v6) != 0L) {
                  return false;
               }
            }

            if (a1 == 2121004 || a1 == 2201009 || a1 == 2221004 || a1 == 3101009 || a1 == 3111011) {
               return false;
            }

            v5 = a1 == 3120007;
         } else {
            if (a1 == 1321024) {
               return false;
            }

            if (a1 > 1221015) {
               if (a1 == 1301014 || a1 == 1310013 || a1 == 1311019) {
                  return false;
               }

               v5 = a1 == 1321015;
            } else {
               if (a1 == 1221015) {
                  return false;
               }

               int v2 = a1 - 1111003;
               if (0 == v2) {
                  return false;
               }

               int v3 = v2 - 10005;
               if (0 == v3) {
                  return false;
               }

               int v4 = v3 - 2;
               if (0 == v4) {
                  return false;
               }

               v5 = v4 == 5;
            }
         }
      }

      return v5 ? false : is_finally_stealable_skill(a1);
   }

   public static boolean is_finally_stealable_skill(int a1) {
      if (is_stealable_hyper_skill(a1)) {
         return true;
      } else if (SkillFactory.getSkill(a1) != null && SkillFactory.getSkill(a1).isHyper()) {
         return false;
      } else {
         int v4 = GameConstants.get_skill_root_from_skill(a1);
         return Integer.toUnsignedLong(v4 - 40000) > 5L
            && is_active_skill(a1)
            && !is_beginner_job(v4)
            && v4 / 1000 == 0
            && !is_phantom_skill(a1)
            && get_job_category(v4) != 9
            && !is_normal_maplehero_skill(a1)
            && !is_heros_will_skill(a1)
            && !is_moving_skill(a1)
            && !is_teleport_mastery_skill(a1)
            && (!is_SummonOnSkill(a1) || is_normal_pupet_skill(a1))
            && !is_dummy_skill(a1)
            && !is_ancient_force_skill(a1)
            && !is_enchant_force_skill(a1);
      }
   }

   public static boolean is_active_skill(int a1) {
      int v1 = a1 / 1000 % 10;
      return v1 != 0 && v1 != 9;
   }

   public static boolean is_beginner_job(int a1) {
      if (Math.abs(a1 - 13000) <= 1
         || a1 == 5000
         || Math.abs(a1 - 2001) <= 4
         || Math.abs(a1 - 3001) <= 1
         || Math.abs(a1 - 6000) <= 3
         || a1 == 14000
         || Math.abs(a1 - 15000) <= 2
         || a1 == 16001) {
         return true;
      } else if (Math.abs(a1 - 40000) <= 5) {
         return false;
      } else {
         boolean result;
         if (a1 == 1000 * (a1 / 1000)) {
            result = true;
         } else {
            result = Math.abs(a1 - 800000) < 100;
         }

         return result;
      }
   }

   public static int get_job_category(int a1) {
      if (a1 == 10000900) {
         return -1;
      } else {
         int v2 = Math.abs(a1 % 1000 / 100);
         if (a1 / 100 == 27) {
            return 2;
         } else if (a1 / 100 == 36) {
            return 4;
         } else if (a1 / 100 != 37) {
            return a1 / 100 != 140 && a1 / 100 != 142 ? v2 : 2;
         } else {
            return 1;
         }
      }
   }

   public static boolean is_phantom_skill(int a1) {
      int v1 = a1 / 10000;
      if (a1 / 10000 == 8000) {
         v1 = a1 / 100;
      }

      return Math.abs(v1 - 2400) < 100 || v1 == 2003;
   }

   public static boolean is_stealable_hyper_skill(int a1) {
      return a1 == 3121054
         || a1 == 2121054
         || a1 == 1121054
         || Math.abs(a1 - 1221054) <= 1
         || a1 == 1321054
         || a1 == 2221054
         || a1 == 2321054
         || a1 == 4221054
         || a1 == 3221054
         || a1 == 3321034
         || a1 == 4121054
         || a1 == 5121054
         || a1 == 5221054;
   }

   public static boolean is_normal_maplehero_skill(int a1) {
      int v2 = a1 / 10000;
      if (v2 == 8000) {
         v2 = a1 / 100;
      }

      return 0 == v2 / 1000
         && !is_beginner_job(v2)
         && (
            a1 == 3321023
               || a1 == 2221000
               || a1 == 1121000
               || a1 == 1221000
               || a1 == 1321000
               || a1 == 2121000
               || a1 == 2321000
               || a1 == 3121000
               || a1 == 3221000
               || a1 == 5221000
               || a1 == 4121000
               || a1 == 4221000
               || a1 == 4341000
               || a1 == 5121000
               || a1 == 5321005
               || a1 == 31221008
               || a1 == 51121005
         );
   }

   public static boolean is_heros_will_skill(int a1) {
      return a1 == 24121009
         || a1 == 4341008
         || a1 == 2321009
         || a1 == 1121011
         || a1 == 1221012
         || a1 == 1321010
         || a1 == 2121008
         || a1 == 2221008
         || a1 == 3121009
         || a1 == 3221008
         || a1 == 3321024
         || a1 == 4121009
         || a1 == 4221008
         || a1 == 13121015
         || a1 == 5121008
         || a1 == 5221010
         || a1 == 5321006
         || a1 == 11121015
         || a1 == 12121015
         || a1 == 14121015
         || a1 == 15121015
         || a1 == 21121008
         || a1 == 22171069
         || a1 == 23121008
         || a1 == 61121220
         || a1 == 33121008
         || a1 == 25121211
         || a1 == 27121010
         || a1 == 31121012
         || a1 == 31221015
         || a1 == 32121008
         || a1 == 35121008
         || a1 == 36121009
         || a1 == 37121007
         || a1 == 51121015
         || a1 == 61121015
         || a1 == 151121006
         || a1 == 154121006
         || a1 == 63121010
         || a1 == 64121005
         || a1 == 65121010
         || a1 == 100001284
         || a1 == 142121007
         || a1 == 152121010
         || a1 == 155121009
         || a1 == 162121024
         || a1 == 164121010
         || a1 == 400001009;
   }

   public static boolean is_moving_skill(int a1) {
      boolean result;
      if (a1 != 31101002
         && a1 != 5100016
         && Math.abs(a1 - 4101015) > 1
         && a1 != 3311002
         && a1 != 3321006
         && a1 != 4211016
         && a1 != 5001005
         && a1 != 12101025
         && a1 != 13001021
         && a1 != 20001295
         && a1 != 25101205
         && a1 != 30001078
         && a1 != 80001886
         && a1 != 37110004
         && a1 != 37100002
         && a1 != 37101001
         && a1 != 37111003
         && Math.abs(a1 - 63001002) > 3
         && a1 != 100001266
         && a1 != 142111010
         && a1 != 151111004
         && a1 != 400051004) {
         result = Math.abs(SkillFactory.getSkill(a1).getType() - 40) <= 2;
      } else {
         result = true;
      }

      return result;
   }

   public static boolean is_teleport_mastery_skill(int a1) {
      return a1 == 2111007 || a1 == 2211007 || a1 == 2311007 || a1 == 12111007;
   }

   public static boolean is_SummonOnSkill(int a1) {
      return SkillFactory.getSkill(a1) == null ? false : SkillFactory.getSkill(a1).getType() == 33 || GameConstants.findProcessType(a1, 7);
   }

   public static boolean is_normal_pupet_skill(int a1) {
      int v2 = a1 / 10000;
      if (v2 == 8000) {
         v2 = a1 / 100;
      }

      boolean result;
      if (v2 / 1000 == 0 && !is_beginner_job(v2)) {
         result = a1 == 3221014;
      } else {
         result = false;
      }

      return result;
   }

   public static boolean is_dummy_skill(int a1) {
      return SkillFactory.getSkill(a1) != null && SkillFactory.getSkill(a1).getType() == 98;
   }

   public static boolean is_ancient_force_skill(int a1) {
      boolean v1;
      if (a1 > 400031034) {
         int v2 = a1 - 400031036;
         if (0 == v2) {
            return true;
         }

         v1 = v2 == 31;
      } else {
         if (a1 == 400031034 || a1 == 3301008 || Math.abs(a1 - 3311009) <= 1) {
            return true;
         }

         v1 = a1 == 3321012;
      }

      return v1;
   }

   public static boolean is_enchant_force_skill(int a1) {
      return Math.abs(a1 - 3321014) <= 1 || a1 == 3321035 || a1 == 400031037 || a1 == 400031057;
   }

   public static class SkillEncodeEntry {
      private final int skillType;
      private final int skillLevel;
      private final int masterLevel;
      private final int targetID;
      private final long skillExpiration;

      public SkillEncodeEntry(int skillType, int skillLevel, int masterLevel, int targetID, long skillExpiration) {
         this.skillType = skillType;
         this.skillLevel = skillLevel;
         this.masterLevel = masterLevel;
         this.targetID = targetID;
         this.skillExpiration = skillExpiration;
      }

      public void encode(int skillID, PacketEncoder e) {
         e.writeInt(this.skillLevel);
         e.writeLong(PacketHelper.getTime(this.skillExpiration));
         if (this.skillType == 1 && GameConstants.isSkillNeedMasterLevel(skillID)) {
            e.writeInt(this.masterLevel);
         }
      }

      public void encodeInheritingSkills(PacketEncoder e) {
         e.writeInt(this.targetID);
         e.writeLong(PacketHelper.getTime(this.skillExpiration));
      }
   }
}
