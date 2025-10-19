package objects.users;

import constants.GameConstants;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.utils.Pair;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;

public class CalcDamageUtil {
   private static final Map<Integer, List<Pair<CalcDamageUtil.IncType, Integer>>> skillinfo = new HashMap<>();
   private static final Map<Integer, List<Integer>> psdskill = new HashMap<>();

   public static List<Integer> getPsdSkill(int skillid) {
      return psdskill.containsKey(skillid) ? psdskill.get(skillid) : null;
   }

   public static void AddPsdSkill(int skillid, int psdskillid) {
      if (!psdskill.containsKey(skillid)) {
         List<Integer> psdlist = new ArrayList<>();
         psdlist.add(psdskillid);
         psdskill.put(skillid, psdlist);
      } else {
         psdskill.get(skillid).add(psdskillid);
      }
   }

   public static double getJobConstants(int jobid) {
      double constants;
      switch (jobid) {
         case 110:
            constants = 0.1;
            break;
         case 111:
            constants = 0.1;
            break;
         case 112:
            constants = 0.1;
            break;
         case 200:
            constants = 0.2;
            break;
         case 210:
            constants = 0.2;
            break;
         case 211:
            constants = 0.2;
            break;
         case 212:
            constants = 0.2;
            break;
         case 220:
            constants = 0.2;
            break;
         case 221:
            constants = 0.2;
            break;
         case 222:
            constants = 0.2;
            break;
         case 230:
            constants = 0.2;
            break;
         case 231:
            constants = 0.2;
            break;
         case 232:
            constants = 0.2;
            break;
         case 1200:
            constants = 0.2;
            break;
         case 1210:
            constants = 0.2;
            break;
         case 1211:
            constants = 0.2;
            break;
         case 1212:
            constants = 0.2;
            break;
         case 16200:
            constants = 0.2;
            break;
         case 16210:
            constants = 0.2;
            break;
         case 16211:
            constants = 0.2;
            break;
         case 16212:
            constants = 0.2;
            break;
         default:
            constants = 0.0;
      }

      return constants;
   }

   public static long getBaseDamageByWT(MapleCharacter player, int weaponid, int pad, int mad, int skillid) {
      short jobid = player.getJob();
      double jobconst = getJobConstants(jobid);
      if (isBeginnerJob(jobid)) {
         return calcBaseDamage(player.getStat().getTotalStr(), player.getStat().getTotalDex(), 0, pad, jobconst + 1.2);
      } else if (GameConstants.isEvan(jobid) || GameConstants.isBattleMage(jobid)) {
         return calcBaseDamage(player.getStat().getTotalInt(), player.getStat().getTotalLuk(), 0, mad, jobconst + 1.2);
      } else if (GameConstants.isMagician(jobid) && !GameConstants.isLuminous(jobid) && !GameConstants.isKinesis(jobid) && !GameConstants.isIllium(jobid)) {
         return calcBaseDamage(player.getStat().getTotalInt(), player.getStat().getTotalLuk(), 0, mad, jobconst + 1.0);
      } else {
         int wt = weaponid / 10000 % 100;
         if (weaponid / 1000 == 1214 || weaponid / 1000 == 1213) {
            wt = weaponid / 1000 % 1000;
         }

         switch (wt) {
            case 21:
            case 26:
            case 28:
               return calcBaseDamage(player.getStat().getTotalInt(), player.getStat().getTotalLuk(), 0, mad, jobconst + 1.2);
            case 22:
               return calcBaseDamage(player.getStat().getTotalStr(), player.getStat().getTotalDex(), 0, pad, jobconst + 1.7);
            case 24:
               return (long)(
                  (player.getStat().getTotalStr() + player.getStat().getTotalDex() + player.getStat().getTotalLuk()) * 3.5 / 100.0 * ((jobconst + 1.5) * pad)
                     + 0.5
               );
            case 27:
            case 33:
               return calcBaseDamage(player.getStat().getTotalLuk(), player.getStat().getTotalStr(), player.getStat().getTotalDex(), pad, jobconst + 1.3);
            case 29:
               return calcBaseDamage(player.getStat().getTotalLuk(), player.getStat().getTotalDex(), 0, pad, jobconst + 1.3);
            case 30:
               return calcBaseDamage(player.getStat().getTotalStr(), player.getStat().getTotalDex(), 0, pad, jobconst + 1.24);
            case 31:
            case 32:
               return calcBaseDamage(player.getStat().getTotalStr(), player.getStat().getTotalDex(), 0, pad, jobconst + 1.2);
            case 36:
               if (isStealSkill(skillid)) {
                  return calcBaseDamage(player.getStat().getTotalLuk(), player.getStat().getTotalDex(), 0, pad, jobconst + 1.2);
               }

               return calcBaseDamage(player.getStat().getTotalLuk(), player.getStat().getTotalDex(), 0, pad, jobconst + 1.3);
            case 39:
               return calcBaseDamage(player.getStat().getTotalStr(), player.getStat().getTotalDex(), 0, 0, jobconst + 1.43);
            case 40:
            case 41:
            case 42:
            case 57:
               return calcBaseDamage(player.getStat().getTotalStr(), player.getStat().getTotalDex(), 0, pad, jobconst + 1.34);
            case 43:
            case 44:
            case 56:
               return calcBaseDamage(player.getStat().getTotalStr(), player.getStat().getTotalDex(), 0, pad, jobconst + 1.49);
            case 45:
            case 52:
            case 59:
            case 214:
               return calcBaseDamage(player.getStat().getTotalDex(), player.getStat().getTotalStr(), 0, pad, jobconst + 1.3);
            case 46:
               return calcBaseDamage(player.getStat().getTotalDex(), player.getStat().getTotalStr(), 0, pad, jobconst + 1.35);
            case 47:
               return calcBaseDamage(player.getStat().getTotalLuk(), player.getStat().getTotalDex(), 0, pad, jobconst + 1.75);
            case 48:
            case 58:
               return calcBaseDamage(player.getStat().getTotalStr(), player.getStat().getTotalDex(), 0, pad, jobconst + 1.7);
            case 49:
               return calcBaseDamage(player.getStat().getTotalDex(), player.getStat().getTotalStr(), 0, pad, jobconst + 1.5);
            case 53:
               int subStat = player.getStat().getDex();
               int mainStat = player.getStat().getStr();
               if (mainStat >= subStat) {
                  int temp = subStat;
                  subStat = mainStat;
                  mainStat = temp;
               }

               return calcBaseDamage(mainStat, subStat, 0, pad, jobconst + 1.5);
            case 213:
               return calcBaseDamage(player.getStat().getTotalStr(), player.getStat().getTotalDex(), 0, pad, jobconst + 1.3);
            default:
               System.out.println("[Error] 처리되지 않은 무기 발생 WeaponType : " + wt);
               return 0L;
         }
      }
   }

   private static boolean isBeginnerJob(int jobid) {
      boolean isbeginner;
      switch (jobid) {
         case 0:
            isbeginner = true;
            break;
         case 1000:
            isbeginner = true;
            break;
         case 2000:
            isbeginner = true;
            break;
         case 2001:
            isbeginner = true;
            break;
         case 2002:
            isbeginner = true;
            break;
         case 2003:
            isbeginner = true;
            break;
         case 2004:
            isbeginner = true;
            break;
         case 2005:
            isbeginner = true;
            break;
         case 3000:
            isbeginner = true;
            break;
         case 3001:
            isbeginner = true;
            break;
         case 3002:
            isbeginner = true;
            break;
         case 5000:
            isbeginner = true;
            break;
         case 6000:
            isbeginner = true;
            break;
         case 6001:
            isbeginner = true;
            break;
         case 6002:
            isbeginner = true;
            break;
         case 6003:
            isbeginner = true;
            break;
         case 10000:
            isbeginner = true;
            break;
         case 13000:
            isbeginner = true;
            break;
         case 13001:
            isbeginner = true;
            break;
         case 14000:
            isbeginner = true;
            break;
         case 15000:
            isbeginner = true;
            break;
         case 15001:
            isbeginner = true;
            break;
         case 15002:
            isbeginner = true;
            break;
         case 16000:
            isbeginner = true;
            break;
         case 16001:
            isbeginner = true;
            break;
         case 30000:
            isbeginner = true;
            break;
         default:
            isbeginner = false;
      }

      return isbeginner;
   }

   private static long calcBaseDamage(int mainstat, int substat1, int substat2, int damage, double multiplier) {
      return (long)((4 * mainstat + substat1 + substat2) / 100.0 * (damage * multiplier) + 0.5);
   }

   public static long calcDemonAvengerDamage(long currenthp, long maxhp, int str, int pad, double multiplier) {
      double dmg = (str + 2L * currenthp / 7L) / 100.0 * (pad * multiplier) + 0.5;
      return currenthp >= maxhp ? (long)dmg : (long)(dmg + 2L * (maxhp - currenthp) / 7L / 100.0 * (pad * multiplier) * 0.8 + 0.5);
   }

   public static double calcLevelDiffDamage(int playerlevel, int monsterlevel, double damage) {
      int leveldiff = playerlevel - monsterlevel;
      Double multiple;
      switch (leveldiff) {
         case -39:
            multiple = 0.02;
            break;
         case -38:
            multiple = 0.05;
            break;
         case -37:
            multiple = 0.08;
            break;
         case -36:
            multiple = 0.1;
            break;
         case -35:
            multiple = 0.13;
            break;
         case -34:
            multiple = 0.15;
            break;
         case -33:
            multiple = 0.18;
            break;
         case -32:
            multiple = 0.2;
            break;
         case -31:
            multiple = 0.23;
            break;
         case -30:
            multiple = 0.25;
            break;
         case -29:
            multiple = 0.28;
            break;
         case -28:
            multiple = 0.3;
            break;
         case -27:
            multiple = 0.33;
            break;
         case -26:
            multiple = 0.35;
            break;
         case -25:
            multiple = 0.38;
            break;
         case -24:
            multiple = 0.4;
            break;
         case -23:
            multiple = 0.43;
            break;
         case -22:
            multiple = 0.45;
            break;
         case -21:
            multiple = 0.48;
            break;
         case -20:
            multiple = 0.5;
            break;
         case -19:
            multiple = 0.53;
            break;
         case -18:
            multiple = 0.55;
            break;
         case -17:
            multiple = 0.58;
            break;
         case -16:
            multiple = 0.6;
            break;
         case -15:
            multiple = 0.63;
            break;
         case -14:
            multiple = 0.65;
            break;
         case -13:
            multiple = 0.68;
            break;
         case -12:
            multiple = 0.7;
            break;
         case -11:
            multiple = 0.73;
            break;
         case -10:
            multiple = 0.75;
            break;
         case -9:
            multiple = 0.78;
            break;
         case -8:
            multiple = 0.8;
            break;
         case -7:
            multiple = 0.83;
            break;
         case -6:
            multiple = 0.85;
            break;
         case -5:
            multiple = 0.88;
            break;
         case -4:
            multiple = 0.918;
            break;
         case -3:
            multiple = 0.9672;
            break;
         case -2:
            multiple = 1.007;
            break;
         case -1:
            multiple = 1.0584;
            break;
         case 0:
            multiple = 1.1;
            break;
         case 1:
            multiple = 1.12;
            break;
         case 2:
            multiple = 1.14;
            break;
         case 3:
            multiple = 1.16;
            break;
         case 4:
            multiple = 1.18;
            break;
         default:
            multiple = null;
      }

      if (multiple == null) {
         if (leveldiff >= 5) {
            multiple = 1.2;
         } else {
            multiple = 0.0;
         }
      }

      return Math.max(damage * multiple, 1.0);
   }

   public static double calcStarForceDamage(int playerSF, int mapNeedSF, double damage) {
      double diffrate = (double)playerSF / mapNeedSF;
      double multiple = 1.0;
      if (diffrate < 0.1) {
         multiple = 0.0;
      } else if (diffrate < 0.3) {
         multiple = 0.1;
      } else if (diffrate < 0.5) {
         multiple = 0.3;
      } else if (diffrate < 0.7) {
         multiple = 0.5;
      } else if (diffrate < 1.0) {
         multiple = 0.7;
      } else if (diffrate == 1.0) {
         multiple = 1.0;
      } else if (playerSF <= 20) {
         multiple = (100.0 + playerSF) / 100.0;
      } else {
         multiple = 1.2;
      }

      return damage * multiple;
   }

   public static double calcArcaneForceDamage(int playerArc, int mapNeedArc, double damage) {
      double diffrate = (double)playerArc / mapNeedArc;
      double multiple = 1.0;
      if (diffrate == 0.0) {
         multiple = 0.1;
      } else if (diffrate < 0.3) {
         multiple = 0.3;
      } else if (diffrate < 0.5) {
         multiple = 0.6;
      } else if (diffrate < 0.7) {
         multiple = 0.7;
      } else if (diffrate < 1.0) {
         multiple = 0.8;
      } else if (diffrate < 1.1) {
         multiple = 1.0;
      } else if (diffrate < 1.3) {
         multiple = 1.1;
      } else if (diffrate < 1.3) {
         multiple = 1.3;
      } else {
         multiple = 1.5;
      }

      return damage * multiple;
   }

   public static double calcAuthenticForceDamage(int playerAUT, int mapNeedAUT, double damage) {
      int diff = playerAUT - mapNeedAUT;
      double multiple = 1.0;
      if (diff <= -95) {
         multiple = 0.05;
      } else if (diff <= 0) {
         multiple = 1.0 + diff / 100.0;
      } else if (diff <= 50) {
         multiple = 1.0 + diff / 200.0;
      } else {
         multiple = 1.25;
      }

      return damage * multiple;
   }

   public static void LoadSkillForCalcDamage() {
      if (skillinfo.size() == 0) {
         SkillFactory.load();
         MapleDataProvider string = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath", "wz") + "/String.wz"));
         MapleData skill = string.getData("Skill.img");

         for (MapleData test : skill.getChildren()) {
            int skillid = Integer.parseInt(test.getName());
            MapleData name = test.getChildByPath("name");
            String desc = null;
            if (test.getChildByPath("desc") != null) {
               desc = test.getChildByPath("desc").getData().toString();
            }

            String h = null;
            if (test.getChildByPath("h") != null) {
               h = test.getChildByPath("h").getData().toString();
            }

            if (h == null && desc != null && desc.indexOf("마력") != -1 && test.getChildByPath("h1") != null) {
               h = test.getChildByPath("h1").getData().toString();
            }

            if (h != null && h.indexOf("공격력") != -1) {
               switch (skillid) {
                  case 9001003:
                  case 51111008:
                  case 80000049:
                  case 80000116:
                  case 80000186:
                  case 80000545:
                  case 80000602:
                  case 80000624:
                  case 80001034:
                  case 80001035:
                  case 80001036:
                  case 80001041:
                  case 80001042:
                  case 80001043:
                  case 80001470:
                  case 80001471:
                  case 80001472:
                  case 80001473:
                  case 80001646:
                  case 150000079:
                  case 150010079:
                  case 150020079:
                  case 400001024:
                  case 400001046:
                  case 400021099:
                     break;
                  default:
                     if (SkillFactory.getSkill(skillid) != null) {
                        SecondaryStatEffect skeff = SkillFactory.getSkill(skillid).getEffect(SkillFactory.getSkill(skillid).getMaxLevel());
                        if ((h.indexOf("공격력 #padX%") == -1 || skeff.getAttackX() == 0)
                           && (h.indexOf("공격력 #padX") == -1 || skeff.getAttackX() == 0)
                           && (h.indexOf("공격력 #x%") == -1 || skeff.getX() == 0)
                           && (h.indexOf("공격력 #x") == -1 || skeff.getX() == 0)) {
                           if (name != null && name.getData().toString().equals("영웅의 메아리")) {
                              if (skeff.getX() == 0) {
                                 System.out.println("[Error] 영웅의 메아리 : " + skillid + " / " + h);
                              }
                           } else if (name != null && name.getData().toString().equals("익스클루시브 스펠")) {
                              if (skeff.getX() == 0) {
                                 System.out.println("[Error] 익스클루시브 스펠 : " + skillid + " / " + h);
                              }
                           } else if (name != null && name.getData().toString().equals("인탠시브 타임")) {
                              if (skeff.getX() == 0) {
                                 System.out.println("[Error] 인탠시브 타임 : " + skillid + " / " + h);
                              }
                           } else if (name != null && name.getData().toString().equals("쓸만한 어드밴스드 블레스")) {
                              if (skeff.getX() == 0 || skeff.getZ() == 0 || skeff.getIndieMHp() == 0) {
                                 System.out.println("[Error] 쓸만한 어드밴스드 블레스 : " + skillid + " / " + h);
                              }
                           } else if (name != null) {
                              System.out.println("[Info] 처리되지 않은 마력 증가 스킬 발생 : " + skillid + "(" + name.getData().toString() + ") / " + h);
                           } else {
                              System.out.println("[Info] 처리되지 않은 마력 증가 스킬 발생 : " + skillid + " / " + h);
                           }
                        }
                     }
               }
            } else if (h != null && h.indexOf("마력") != -1) {
               switch (skillid) {
                  case 9001003:
                  case 51111008:
                  case 80000049:
                  case 80000116:
                  case 80000186:
                  case 80000545:
                  case 80000602:
                  case 80000624:
                  case 80001034:
                  case 80001035:
                  case 80001036:
                  case 80001041:
                  case 80001042:
                  case 80001043:
                  case 80001470:
                  case 80001471:
                  case 80001472:
                  case 80001473:
                  case 80001646:
                  case 80002364:
                  case 80002365:
                  case 150000079:
                  case 150010079:
                  case 150020079:
                  case 151001001:
                  case 155121102:
                  case 400001024:
                  case 400001037:
                  case 400001046:
                  case 400011109:
                  case 400021073:
                  case 400021099:
                  case 400021105:
                     break;
                  default:
                     if (SkillFactory.getSkill(skillid) != null) {
                        SecondaryStatEffect skeff = SkillFactory.getSkill(skillid).getEffect(SkillFactory.getSkill(skillid).getMaxLevel());
                        if ((h.indexOf("마력 #madX%") == -1 || skeff.getMagicX() == 0)
                           && (h.indexOf("마력 #madX") == -1 || skeff.getMagicX() == 0)
                           && (h.indexOf("마력 #padX%") == -1 || skeff.getAttackX() == 0)
                           && (h.indexOf("마력 #padX") == -1 || skeff.getAttackX() == 0)
                           && (h.indexOf("마력#c #padX%") == -1 || skeff.getAttackX() == 0)
                           && (h.indexOf("마력#c #padX") == -1 || skeff.getAttackX() == 0)
                           && (h.indexOf("마력 #mad%") == -1 || skeff.getMatk() == 0)
                           && (h.indexOf("마력 #mad") == -1 || skeff.getMatk() == 0)
                           && (h.indexOf("마력#indieMad%") == -1 || skeff.getIndieMad() == 0)
                           && (h.indexOf("마력#indieMad") == -1 || skeff.getIndieMad() == 0)
                           && (h.indexOf("마력 #indiePad%") == -1 || skeff.getIndiePad() == 0)
                           && (h.indexOf("마력 #indiePad") == -1 || skeff.getIndiePad() == 0)
                           && (h.indexOf("마력 #indiePAD%") == -1 || skeff.getIndiePad() == 0)
                           && (h.indexOf("마력 #indiePAD") == -1 || skeff.getIndiePad() == 0)
                           && (h.indexOf("마력 #indieMad%") == -1 || skeff.getIndieMad() == 0)
                           && (h.indexOf("마력 #indieMad") == -1 || skeff.getIndieMad() == 0)
                           && (h.indexOf("마력 #c#indieMad#%") == -1 || skeff.getIndieMad() == 0)
                           && (h.indexOf("마력 #c#indieMad#") == -1 || skeff.getIndieMad() == 0)
                           && (h.indexOf("마력 #damage%") == -1 || skeff.getDamage() == 0)
                           && (h.indexOf("마력 #x%") == -1 || skeff.getX() == 0)
                           && (h.indexOf("마력 #x") == -1 || skeff.getX() == 0)
                           && (h.indexOf("마력 #y%") == -1 || skeff.getY() == 0)
                           && (h.indexOf("마력 #y") == -1 || skeff.getY() == 0)
                           && (h.indexOf("마력+#y%") == -1 || skeff.getY() == 0)
                           && (h.indexOf("마력+#y") == -1 || skeff.getY() == 0)
                           && (h.indexOf("마력 #v%") == -1 || skeff.getV() == 0)
                           && (h.indexOf("마력 #v") == -1 || skeff.getV() == 0)
                           && (h.indexOf("마력 #w%") == -1 || skeff.getW() == 0)
                           && (h.indexOf("마력 #w") == -1 || skeff.getW() == 0)
                           && (h.indexOf("마력 #epad%") == -1 || skeff.getEnhancedWatk() == 0)
                           && (h.indexOf("마력 #epad") == -1 || skeff.getEnhancedWatk() == 0)
                           && (h.indexOf("마력 #emad%") == -1 || skeff.getEnhancedMatk() == 0)
                           && (h.indexOf("마력 #emad") == -1 || skeff.getEnhancedMatk() == 0)
                           && (h.indexOf("마력 #indiePadR%") == -1 || skeff.getIndiePadR() == 0)
                           && (h.indexOf("마력 #indieMadR%") == -1 || skeff.getIndieMadR() == 0)
                           && (h.indexOf("마력이 #indieMadR%") == -1 || skeff.getIndieMadR() == 0)) {
                           if (name != null && name.getData().toString().equals("영웅의 메아리")) {
                              if (skeff.getX() == 0) {
                                 System.out.println("[Error] 영웅의 메아리 : " + skillid + " / " + h);
                              }
                           } else if (name != null && name.getData().toString().equals("익스클루시브 스펠")) {
                              if (skeff.getX() == 0) {
                                 System.out.println("[Error] 익스클루시브 스펠 : " + skillid + " / " + h);
                              }
                           } else if (name != null && name.getData().toString().equals("인탠시브 타임")) {
                              if (skeff.getX() == 0) {
                                 System.out.println("[Error] 인탠시브 타임 : " + skillid + " / " + h);
                              }
                           } else if (name != null && name.getData().toString().equals("쓸만한 어드밴스드 블레스")) {
                              if (skeff.getX() == 0 || skeff.getZ() == 0 || skeff.getIndieMHp() == 0) {
                                 System.out.println("[Error] 쓸만한 어드밴스드 블레스 : " + skillid + " / " + h);
                              }
                           } else if (name != null) {
                              System.out.println("[Info] 처리되지 않은 마력 증가 스킬 발생 : " + skillid + "(" + name.getData().toString() + ") / " + h);
                           } else {
                              System.out.println("[Info] 처리되지 않은 마력 증가 스킬 발생 : " + skillid + " / " + h);
                           }
                        }
                     }
               }
            }
         }
      }
   }

   public static boolean isLoaded() {
      return skillinfo.size() != 0;
   }

   public static int getShadowPartnerSkillID(short jobid) {
      int skillid;
      switch (jobid) {
         case 411:
            skillid = 4111002;
            break;
         case 412:
            skillid = 4111002;
            break;
         case 421:
            skillid = 4211008;
            break;
         case 422:
            skillid = 4211008;
            break;
         case 433:
            skillid = 4331002;
            break;
         case 434:
            skillid = 4331002;
            break;
         case 1411:
            skillid = 14111000;
            break;
         case 1412:
            skillid = 14111000;
            break;
         case 1512:
            skillid = 15121004;
            break;
         case 3112:
            skillid = 31121054;
            break;
         case 3611:
            skillid = 36111006;
            break;
         case 3612:
            skillid = 36111006;
            break;
         default:
            skillid = 0;
      }

      return skillid;
   }

   public static boolean isStealSkill(int skillid) {
      return false;
   }

   public static boolean isCritRateAddSkill(int skillid) {
      boolean critadd;
      switch (skillid) {
         case 1220047:
            critadd = true;
            break;
         case 1220057:
            critadd = true;
            break;
         case 2220051:
            critadd = true;
            break;
         case 2221006:
            critadd = true;
            break;
         case 2321008:
            critadd = true;
            break;
         default:
            critadd = false;
      }

      return critadd;
   }

   public static boolean isIgnoreMobPdpRAddSkill(int skillid) {
      switch (skillid) {
         case 1220022:
            return true;
         default:
            return false;
      }
   }

   public static boolean isDashSkill(int skillid) {
      boolean isDash;
      switch (skillid) {
         case 1111012:
            isDash = true;
            break;
         case 1211012:
            isDash = true;
            break;
         case 1311012:
            isDash = true;
            break;
         case 4301004:
            isDash = true;
            break;
         case 4311003:
            isDash = true;
            break;
         case 4321006:
            isDash = true;
            break;
         case 4331000:
            isDash = true;
            break;
         case 5111009:
            isDash = true;
            break;
         case 15111021:
            isDash = true;
            break;
         case 23101001:
            isDash = true;
            break;
         case 23111003:
            isDash = true;
            break;
         case 36111001:
            isDash = true;
            break;
         case 51101006:
            isDash = true;
            break;
         case 80001825:
            isDash = true;
            break;
         case 80001826:
            isDash = true;
            break;
         case 80001910:
            isDash = true;
            break;
         case 80001916:
            isDash = true;
            break;
         default:
            isDash = false;
      }

      return isDash;
   }

   public static double get_rand(long rand, double f0, double f1) {
      double realF1 = f1;
      double realF0 = f0;
      if (f0 > f1) {
         realF1 = f0;
         realF0 = f1;
      }

      return realF1 != realF0 ? realF0 + rand % 10000000L * (realF1 - realF0) / 9999999.0 : realF0;
   }

   public static enum IncType {
      pad,
      mad,
      padR,
      madR,
      mhp,
      mmp,
      mhpR,
      mmpR,
      def,
      defR;
   }
}
