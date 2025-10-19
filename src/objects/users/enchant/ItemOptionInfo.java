package objects.users.enchant;

import constants.DailyEventType;
import constants.GameConstants;
import constants.ServerConstants;
import database.DBConfig;
import database.DBConnection;
import database.DBEventManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import objects.context.SpecialSunday;
import objects.item.MapleInventoryIdentifier;
import objects.item.MapleItemInformationProvider;
import objects.utils.Randomizer;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;

public class ItemOptionInfo {
   public static Map<Integer, ItemOption> options = new HashMap<>();
   public static Map<Integer, Map<Integer, List<ItemOption>>> optionsSorted = new HashMap<>();

   public static void loadItemInfo() {
      String WZpath = System.getProperty("net.sf.odinms.wzpath");
      MapleDataProvider prov = MapleDataProviderFactory.getDataProvider(new File(WZpath + "/Item.wz"));
      MapleData data = prov.getData("ItemOption.img");
      Map<String, Integer> types = new HashMap<>();

      for (MapleData dat : data) {
         ItemOption option = new ItemOption(dat, types);
         ItemOptionInfo.options.put(option.id, option);
      }

      for (ItemOption option : ItemOptionInfo.options.values()) {
         int rarity = option.id / 10000;
         Map<Integer, List<ItemOption>> options = optionsSorted.get(rarity);
         if (options == null || options.isEmpty()) {
            options = new HashMap<>();
            optionsSorted.put(rarity, options);
         }

         List<ItemOption> list = options.get(option.optionType);
         if (list == null || list.isEmpty()) {
            list = new ArrayList<>();
            options.put(option.optionType, list);
         }

         list.add(option);
      }
   }

   public static ItemOption getItemOption(int optionID) {
      ItemOption option = options.get(optionID);
      return option != null ? option : null;
   }

   public static ItemOptionLevelData getItemOptionLevelData(int itemID, int optionID) {
      if (itemID != 0 && optionID != 0) {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         return getItemOptionLevelDataByRLevel(ii.getReqLevel(itemID), optionID);
      } else {
         return null;
      }
   }

   public static ItemOptionLevelData getItemOptionLevelDataByRLevel(int rLevel, int optionID) {
      ItemOption option = getItemOption(optionID);
      if (option == null) {
         return null;
      } else {
         int reqLevel = (rLevel - 1) / 10 + 1;
         return option.level[in(0, reqLevel, 19)];
      }
   }

   public static int in(int min, int v, int max) {
      int ret = v;
      if (v > max) {
         ret = max;
      }

      if (ret < min) {
         ret = min;
      }

      return ret;
   }

   public static int getItemGrade(int curGrade, int optPos, GradeRandomOption opt) {
      boolean fever = ServerConstants.dailyEventType == DailyEventType.CubeFever;
      ItemOptionPercentageInfo.ItemGradePercentageInfo info = ItemOptionPercentageInfo.getItemGradePercentageInfo(opt, curGrade);
      if (info == null) {
         return curGrade;
      } else {
         int randomValue = Randomizer.nextInt(ItemOptionPercentageInfo.ItemGradePercentageInfo.getRandomValue());
         if (fever) {
            randomValue = (int)(randomValue / 1.05);
         }

         if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeCubeUpFever) {
            randomValue /= 2;
         }

         if (ItemOptionPercentageInfo.ItemGradePercentageInfo.getPercentageInfo(0) == 0) {
            return curGrade;
         } else if (curGrade >= 4) {
            return curGrade;
         } else {
            return randomValue >= ItemOptionPercentageInfo.ItemGradePercentageInfo.getPercentageInfo(optPos) ? curGrade : curGrade + 1;
         }
      }
   }

   public static void main(String[] args) {
      DBConnection.init();
      DBEventManager.init(4);
      MapleInventoryIdentifier.getInstance();
      MapleItemInformationProvider.getInstance().runEtc();
      MapleItemInformationProvider.getInstance().runItems();
      loadItemInfo();

      for (int x = 0; x < 20; x++) {
         System.out.println(x + 1 + "회차 결과 (등급 : 레전드리) (사용 아이템 등급 : 장인의 큐브)");
         int level = 4;

         for (int i = 0; i < 3; i++) {
            getItemOption(1592035, level, Collections.EMPTY_LIST, GradeRandomOption.Master);
         }
      }
   }

   public static int getItemOption(int itemID, int grade, List<Integer> beforeOptions, GradeRandomOption option) {
      List<Integer> optionTypes = getOptionTypes(itemID);
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      Map<Integer, List<ItemOption>> options = optionsSorted.get(grade);
      if (options != null && !options.isEmpty()) {
         List<ItemOption> allPoptions = new ArrayList<>();

         for (int opt : optionTypes) {
            List<ItemOption> list = options.get(opt);
            if (list != null && !list.isEmpty()) {
               for (ItemOption op : list) {
                  if (option != GradeRandomOption.Additional && option != GradeRandomOption.OccultAdditional && option != GradeRandomOption.AmazingAdditional) {
                     if (!isAdditional(op.id)) {
                        allPoptions.add(op);
                     }
                  } else if (isAdditional(op.id)) {
                     allPoptions.add(op);
                  }
               }
            }
         }

         allPoptions = allPoptions.stream()
            .filter(a -> getCustomMaxLine(itemID, a, optionTypes) > 0)
            .filter(a -> a.reqLevel <= ii.getReqLevel(itemID))
            .filter(a -> ItemOptionPercentageInfo.getItemOptionPercentageInfo(option, a.id) > 0)
            .collect(Collectors.toList());
         if (DBConfig.isGanglim) {
            List<Integer> removeList = List.of(
               31001, 31002, 31003, 31004, 32091, 32092, 32093, 32094, 32661, 40081, 42059, 42116, 42650, 42656, 42661, 32058, 42058
            );
            allPoptions = allPoptions.stream()
               .filter(
                  a -> a.id != 42601
                     && !isAdditinalDamageCheck(a.id)
                     && !isAdditionalStatValue(a.id)
                     && !isAdditionalDamage(a.id)
                     && a.id != 32071
                     && a.id != 40091
                     && a.id != 40092
                     && (a.id < 42091 || a.id > 42096)
               )
               .filter(a -> !removeList.contains(a.id))
               .collect(Collectors.toList());
         }

         List<Integer> removeList = List.of(30053, 30054, 40053, 40054, 32055, 32056, 42055, 42056, 32013, 32014, 42013, 42014);
         allPoptions = allPoptions.stream().filter(a -> !removeList.contains(a.id)).collect(Collectors.toList());
         int max = 0;

         for (ItemOption optx : allPoptions) {
            max += ItemOptionPercentageInfo.getItemOptionPercentageInfo(option, optx.id);
         }

         int rand = Randomizer.rand(0, max - 1);
         int v = 0;

         for (ItemOption optx : allPoptions) {
            v += ItemOptionPercentageInfo.getItemOptionPercentageInfo(option, optx.id);
            if (rand <= v) {
               return optx.id;
            }
         }

         return 0;
      } else {
         System.out.println(grade + " 레어리티의 아이템 옵션이 로드되지 않았습니다.");
         return 0;
      }
   }

   public static boolean isAdditinalDamageCheck(int potential) {
      switch (potential) {
         case 12070:
         case 12071:
         case 22070:
         case 22071:
         case 32070:
         case 32071:
         case 42070:
         case 42071:
            return true;
         default:
            return false;
      }
   }

   public static boolean isAdditionalDamage(int potential) {
      switch (potential) {
         case 32052:
         case 32054:
         case 42052:
         case 42054:
            return true;
         default:
            return false;
      }
   }

   public static boolean isAdditionalStatValue(int potential) {
      switch (potential) {
         case 22057:
         case 22058:
         case 22059:
         case 22060:
         case 22087:
         case 32059:
         case 32060:
         case 32061:
         case 32062:
         case 32087:
         case 42063:
         case 42064:
         case 42065:
         case 42066:
         case 42087:
            return true;
         default:
            return false;
      }
   }

   public static int getCustomMaxLine(int itemID, ItemOption option, List<Integer> beforeOptions) {
      if (GameConstants.isEmblem(itemID)) {
         int rmax = checkMaxLines(itemID, 10, option, x -> x.incDAMr <= 0 ? false : x.boss, beforeOptions, 0);
         if (rmax != -1) {
            return 0;
         }
      }

      int max = checkMaxLines(itemID, 10, option, x -> x.ignoreTargetDEF > 0, beforeOptions, 2);
      if (max != -1) {
         return max;
      } else {
         max = checkMaxLines(itemID, 10, option, x -> x.incDAMr <= 0 ? false : x.boss, beforeOptions, 2);
         return max != -1 ? max : 9;
      }
   }

   private static int checkMaxLines(int itemID, int optionType, ItemOption option, ItemOptionInfo.Function func, List<Integer> beforeOptions, int max) {
      int ret = 0;
      ItemOptionLevelData levelData = getItemOptionLevelData(itemID, option.id);
      if (levelData == null) {
         return -1;
      } else if ((option.optionType == optionType || optionType < 0) && func.check(levelData)) {
         int count = max;

         for (int opt : beforeOptions) {
            ItemOptionLevelData beforeOpt = getItemOptionLevelData(itemID, opt);
            if (beforeOpt != null && func.check(beforeOpt)) {
               count--;
            }
         }

         return Math.max(0, count);
      } else {
         return -1;
      }
   }

   public static int getItemGradePA() {
      return (Randomizer.nextInt() & 3) == 3 ? 3 : 2;
   }

   public static List<Integer> getOptionTypes(int itemID) {
      List<Integer> ret = new ArrayList<>();
      ret.add(0);
      if (GameConstants.isWeapon(itemID) || GameConstants.isSubWeapon(itemID) || GameConstants.isEmblem(itemID) || GameConstants.isShield(itemID)) {
         ret.add(10);
      } else if (GameConstants.isAndroidHeart(itemID) && DBConfig.isGanglim) {
         ret.add(10);
      } else {
         ret.add(11);
      }

      if (!GameConstants.isBetaWeapon(itemID)
         && (
            GameConstants.isCap(itemID)
               || GameConstants.isRing(itemID)
               || GameConstants.isGlove(itemID)
               || GameConstants.isShoes(itemID)
               || GameConstants.isShieldBodyPart(itemID)
               || GameConstants.isCoat(itemID)
               || !GameConstants.isLongcoat(itemID)
               || GameConstants.isPants(itemID)
               || GameConstants.isCape(itemID)
               || GameConstants.isBelt(itemID)
               || GameConstants.isShoulder(itemID)
         )) {
         ret.add(20);
      } else {
         ret.add(21);
      }

      if (!GameConstants.isPendant(itemID)
         && !GameConstants.isRing(itemID)
         && !GameConstants.isFaceAccessory(itemID)
         && !GameConstants.isEyeAccessory(itemID)
         && !GameConstants.isEarAccessory(itemID)) {
         ret.add(41);
      } else {
         ret.add(40);
      }

      if (GameConstants.isCap(itemID)) {
         ret.add(51);
      }

      if (GameConstants.isCoat(itemID) || GameConstants.isLongcoat(itemID)) {
         ret.add(52);
      }

      if (GameConstants.isLongcoat(itemID) || GameConstants.isPants(itemID)) {
         ret.add(53);
      }

      if (GameConstants.isGlove(itemID)) {
         ret.add(54);
      }

      if (GameConstants.isShoes(itemID)) {
         ret.add(55);
      }

      if (GameConstants.isEmblem(itemID)) {
         ret.add(56);
      }

      return ret;
   }

   public static boolean isAdditional(int optionID) {
      return optionID / 1000 % 10 == 2;
   }

   public static boolean isBossIncDamR(int optionID) {
      return optionID / 100 % 10 == 6;
   }

   public interface Function {
      boolean check(ItemOptionLevelData var1);
   }
}
