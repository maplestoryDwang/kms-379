package objects.users.enchant;

import constants.GameConstants;
import database.DBConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleItemInformationProvider;
import objects.utils.Randomizer;

public class BonusStat {
   public static double Rate_From_Normal = 2.7;
   public static double Rate_From_EliteBoss = 2.5;
   public static double Rate_LevelledRebirthFlame = 2.5;
   public static double Rate_ChanceTime = 2.3;
   public static double Rate_PowerfulRebirthFlame = 2.3;
   public static double Rate_EternalRebirthFlame = 2.0;
   public static double Rate_BlackRebirthFlame = 1.405;

   public static boolean test(Item item, int lines, int level, int... source_) {
      Equip equip = (Equip)item;
      if (equip == null) {
         return false;
      } else {
         Map<ExItemType, Integer> source = new HashMap<>();
         int index = 0;

         while (source.size() < lines) {
            ExItemType type = ExItemType.getItemType(source_[index++]);
            source.put(type, level);
         }

         setExItemOptions(equip, source);
         return true;
      }
   }

   public static boolean resetBonusStat(Item item, BonusStatPlaceType placeType) {
      return resetBonusStat(item, placeType, false);
   }

   public static boolean resetBonusStat(Item item, BonusStatPlaceType placeType, boolean fromDrop) {
      Equip equip = (Equip)item;
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (ii != null && equip != null) {
         if (equip.getItemId() == 1113306) {
            setKarmaOption(equip, placeType, fromDrop);
            return true;
         } else if (!GameConstants.isRing(equip.getItemId())
            && equip.getItemId() / 1000 != 1092
            && equip.getItemId() / 1000 != 1342
            && equip.getItemId() / 1000 != 1712
            && equip.getItemId() / 1000 != 1713
            && equip.getItemId() / 1000 != 1152
            && equip.getItemId() / 1000 != 1142
            && equip.getItemId() / 1000 != 1143
            && equip.getItemId() / 1000 != 1149
            && equip.getItemId() / 1000 != 1672
            && !GameConstants.isSecondaryWeapon(equip.getItemId())
            && equip.getItemId() / 1000 != 1190
            && equip.getItemId() / 1000 != 1191
            && equip.getItemId() / 1000 != 1182
            && equip.getItemId() / 1000 != 1662
            && equip.getItemId() / 1000 != 1802
            && equip.getItemId() / 1000 != 1902
            && equip.getItemId() / 1000 != 1098
            && equip.getItemId() / 1000 != 1099) {
            Map<ExItemType, Integer> source = new HashMap<>();
            int lines = getExItemLines(equip.getExGradeOption());
            boolean bossReward = ii.isBossReward(item.getItemId());
            if (lines == 0) {
               if (!bossReward && !DBConfig.isGanglim) {
                  lines = Randomizer.rand(1, 4);
               } else {
                  lines = 4;
               }
            }

            while (source.size() < lines) {
               ExItemType type = ExItemType.getRandom();
               if (!source.containsKey(type) && GameConstants.IsEligibleExOptionPart(item.getItemId(), ii.getReqLevel(item.getItemId()), type)) {
                  int num2 = randomizeLevel(placeType, bossReward);
                  source.put(type, num2);
               }
            }

            setExItemOptions(equip, source);
            setKarmaOption(equip, placeType, fromDrop);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static void setKarmaOption(Equip equip, BonusStatPlaceType placeType, boolean fromDrop) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (equip != null && ii != null) {
         if (equip.getKarmaCount() < 0) {
            if (!ii.isAccountShared(equip.getItemId())) {
               if (!ii.isTradeBlocked(equip.getItemId()) && !ii.isEquipTradeBlocked(equip.getItemId())) {
                  if (!fromDrop) {
                     equip.setFlag(equip.getFlag() | ItemFlag.POSSIBLE_TRADING.getValue());
                     equip.setKarmaCount((byte)(placeType.type > BonusStatPlaceType.LevelledRebirthFlame.type ? 10 : 5));
                  }
               } else if (ii.isPKarmaEnabled(equip.getItemId())) {
                  if (ii.isPitchedBossset(equip.getItemId())) {
                     equip.setKarmaCount((byte)5);
                  } else {
                     equip.setKarmaCount((byte)10);
                  }
               }
            }
         }
      }
   }

   public static void setExItemOptions(Equip equip, Map<ExItemType, Integer> options) {
      long num1 = 0L;
      int num2 = 0;

      for (Entry<ExItemType, Integer> option : options.entrySet()) {
         int num3 = option.getKey().getType() * 10 + option.getValue();
         num1 += num3 * (long)Math.pow(1000.0, num2++);
      }

      equip.setExGradeOption(num1);
   }

   public static int getExItemLines(long exGradeOption) {
      int num = 0;

      for (int i = 0; i < 4; i++) {
         if (exGradeOption % 1000L > 0L) {
            num++;
         }

         exGradeOption /= 1000L;
      }

      return num;
   }

   public static int randomizeLevel(BonusStatPlaceType type, boolean bossReward) {
      int min = 0;
      int max = 0;
      double rate = 1.0;
      switch (type) {
         case FromNormal:
            rate = Rate_From_Normal;
            if (bossReward) {
               min = 3;
               max = 7;
            } else {
               min = 1;
               max = 5;
            }
            break;
         case FromEliteBoss:
            rate = Rate_From_EliteBoss;
            if (bossReward) {
               return 0;
            }

            min = 3;
            max = 7;
            break;
         case LevelledRebirthFlame:
            rate = Rate_LevelledRebirthFlame;
            if (bossReward) {
               min = 3;
               max = 6;
            } else {
               min = 1;
               max = 4;
            }
            break;
         case PowerfulRebirthFlame:
            rate = Rate_PowerfulRebirthFlame;
            if (bossReward) {
               min = 3;
               max = 6;
            } else {
               min = 1;
               max = 4;
               if (DBConfig.isGanglim) {
                  max = 6;
               }
            }
            break;
         case EternalRebirthFlame:
            rate = Rate_EternalRebirthFlame;
            if (bossReward) {
               min = 4;
               max = 7;
            } else {
               min = 2;
               if (DBConfig.isGanglim) {
                  min = 3;
               }

               max = 5;
               if (DBConfig.isGanglim) {
                  max = 7;
               }
            }
            break;
         case ChanceTime:
            rate = Rate_ChanceTime;
            if (bossReward) {
               min = 3;
               max = 7;
            } else {
               min = 1;
               max = 5;
            }
            break;
         case BlackRebirthFlame:
            if (DBConfig.isGanglim) {
               rate = Rate_BlackRebirthFlame;
               min = 4;
               max = 7;
            } else {
               rate = Rate_EternalRebirthFlame;
               if (bossReward) {
                  min = 4;
                  max = 7;
               } else {
                  min = 2;
                  max = 5;
               }
            }
      }

      if (DBConfig.isGanglim) {
         return type == BonusStatPlaceType.BlackRebirthFlame && Randomizer.nextBoolean() ? Randomizer.rand(max - 1, max) : Randomizer.rand(min, max);
      } else {
         Map<Integer, Integer> source = new HashMap<>();

         for (int x = min; x <= max; x++) {
            source.put(x, f(rate, x, min, max));
         }

         int sum = 0;

         for (Integer a : source.values()) {
            sum += a;
         }

         int num = Randomizer.nextInt(sum);

         for (Entry<Integer, Integer> s : source.entrySet()) {
            num -= s.getValue();
            if (num <= 0) {
               return s.getKey();
            }
         }

         return -1;
      }
   }

   public static int f(double rate, int x, int min, int max) {
      return (int)(Math.pow(rate, -x + max - min) * 1000.0);
   }

   public static Map<ExItemType, Integer> getExItemOptions(Equip equip) {
      Map<ExItemType, Integer> ret = new HashMap<>();
      long exGradeOption = equip.getExGradeOption();

      for (int index = 0; index < 5; index++) {
         long num1 = exGradeOption % 1000L;
         if (num1 > 0L) {
            int num2 = (int)(num1 % 10L);
            ExItemType exItemType = ExItemType.getItemType((int)(num1 / 10L));
            ret.put(exItemType, num2);
         }

         exGradeOption /= 1000L;
      }

      return ret;
   }

   public static Map<ExItemType, Integer> getExItemOptionsBlack(Equip equip) {
      Map<ExItemType, Integer> ret = new HashMap<>();
      long exGradeOption = equip.getExGradeOption();

      for (int index = 0; index < 5; index++) {
         long num1 = exGradeOption % 1000L;
         if (num1 > 0L) {
            int num2 = (int)(num1 % 10L);
            ExItemType exItemType = ExItemType.getItemType((int)(num1 / 10L));
            if (exItemType.getType() >= 4 && exItemType.getType() <= 9) {
               ret.putIfAbsent(exItemType, 0);
               switch (exItemType.getType()) {
                  case 4:
                     ret.putIfAbsent(ExItemType.Str, 0);
                     ret.putIfAbsent(ExItemType.Dex, 0);
                     ret.put(ExItemType.Str, ret.get(ExItemType.Str) + num2);
                     ret.put(ExItemType.Dex, ret.get(ExItemType.Dex) + num2);
                     break;
                  case 5:
                     ret.putIfAbsent(ExItemType.Str, 0);
                     ret.putIfAbsent(ExItemType.Int, 0);
                     ret.put(ExItemType.Str, ret.get(ExItemType.Str) + num2);
                     ret.put(ExItemType.Int, ret.get(ExItemType.Int) + num2);
                     break;
                  case 6:
                     ret.putIfAbsent(ExItemType.Str, 0);
                     ret.putIfAbsent(ExItemType.Luk, 0);
                     ret.put(ExItemType.Str, ret.get(ExItemType.Str) + num2);
                     ret.put(ExItemType.Luk, ret.get(ExItemType.Luk) + num2);
                     break;
                  case 7:
                     ret.putIfAbsent(ExItemType.Dex, 0);
                     ret.putIfAbsent(ExItemType.Int, 0);
                     ret.put(ExItemType.Dex, ret.get(ExItemType.Dex) + num2);
                     ret.put(ExItemType.Int, ret.get(ExItemType.Int) + num2);
                     break;
                  case 8:
                     ret.putIfAbsent(ExItemType.Dex, 0);
                     ret.putIfAbsent(ExItemType.Luk, 0);
                     ret.put(ExItemType.Dex, ret.get(ExItemType.Dex) + num2);
                     ret.put(ExItemType.Luk, ret.get(ExItemType.Luk) + num2);
                     break;
                  case 9:
                     ret.putIfAbsent(ExItemType.Int, 0);
                     ret.putIfAbsent(ExItemType.Luk, 0);
                     ret.put(ExItemType.Int, ret.get(ExItemType.Int) + num2);
                     ret.put(ExItemType.Luk, ret.get(ExItemType.Luk) + num2);
               }

               ret.put(exItemType, ret.get(exItemType) + num2);
            } else {
               ret.putIfAbsent(exItemType, 0);
               ret.put(exItemType, ret.get(exItemType) + num2);
            }
         }

         exGradeOption /= 1000L;
      }

      return ret;
   }

   public static int getBonusStat(Equip equip, ExItemType type, int level) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      boolean bossReward = ii.isBossReward(equip.getItemId());
      int num1 = 1 + ii.getReqLevel(equip.getItemId()) / 20;
      int num2 = 1 + ii.getReqLevel(equip.getItemId()) / 40;
      switch (type) {
         case Str:
         case Dex:
         case Int:
         case Luk:
            return level * num1;
         case StrDex:
         case StrInt:
         case StrLuk:
         case DexInt:
         case DexLuk:
         case IntLuk:
            return level * num2;
         case MaxHP:
         case MaxMP:
            return Math.max(1, ii.getReqLevel(equip.getItemId())) * level * 3;
         case ReqLevel:
            return level * 5;
         case Pdd:
         case Mdd:
            return level * num2;
         case Pad:
         case Mad:
            if (!GameConstants.isWeapon(equip.getItemId())) {
               return level;
            }

            double num3;
            if (bossReward) {
               switch (level) {
                  case 3:
                     num3 = 3.0;
                     break;
                  case 4:
                     num3 = 4.4;
                     break;
                  case 5:
                     num3 = 6.05;
                     break;
                  case 6:
                     num3 = 8.0;
                     break;
                  case 7:
                     num3 = 10.25;
                     break;
                  default:
                     num3 = 0.0;
               }
            } else {
               switch (level) {
                  case 1:
                     num3 = 1.0;
                     break;
                  case 2:
                     num3 = 2.2;
                     break;
                  case 3:
                     num3 = 3.65;
                     break;
                  case 4:
                     num3 = 5.35;
                     break;
                  case 5:
                     num3 = 7.3;
                     break;
                  case 6:
                     num3 = 8.8;
                     break;
                  case 7:
                     num3 = 10.25;
                     break;
                  default:
                     num3 = 0.0;
               }
            }

            return (int)Math.ceil((type == ExItemType.Pad ? ii.getPad(equip.getItemId()) : ii.getMad(equip.getItemId())) * num2 * num3 / 100.0);
         case Speed:
         case Jump:
         case DamR:
         case StatR:
         case IMdR:
            return level;
         case BdR:
            return level * 2;
         default:
            System.out.println("[오류] 계산할 수 없는 추가옵션 " + type + " 입니다.");
            return 0;
      }
   }
}
