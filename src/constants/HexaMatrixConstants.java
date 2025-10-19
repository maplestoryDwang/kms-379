package constants;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import objects.utils.Pair;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class HexaMatrixConstants {
   private static int[] originSkillUpgradeSolErdaCost = new int[]{1, 1, 1, 2, 2, 2, 3, 3, 10, 3, 3, 4, 4, 4, 4, 4, 4, 5, 15, 5, 5, 5, 5, 5, 6, 6, 6, 7, 20};
   private static int[] originSkillUpgradeSolErdaPieceCost = new int[]{
      30, 35, 40, 45, 50, 55, 60, 65, 200, 80, 90, 100, 110, 120, 130, 140, 150, 160, 350, 170, 180, 190, 200, 210, 220, 230, 240, 250, 500
   };
   private static int[] masteryCoreUpgradeSolErdaCost = new int[]{1, 1, 1, 1, 1, 1, 2, 2, 5, 2, 2, 2, 2, 2, 2, 2, 2, 3, 8, 3, 3, 3, 3, 3, 3, 3, 3, 4, 10};
   private static int[] masteryCoreUpgradeSolErdaPieceCost = new int[]{
      15, 18, 20, 23, 25, 28, 30, 33, 100, 40, 45, 50, 55, 60, 65, 70, 75, 80, 175, 85, 90, 95, 100, 105, 110, 115, 120, 125, 250
   };
   private static int[] enforceCoreUpgradeSolErdaCost = new int[]{1, 1, 1, 2, 2, 2, 3, 3, 8, 3, 3, 3, 3, 3, 3, 3, 3, 4, 12, 4, 4, 4, 4, 4, 5, 5, 5, 6, 15};
   private static int[] enforceCoreUpgradeSolErdaPieceCost = new int[]{
      23, 27, 30, 34, 38, 42, 45, 49, 150, 60, 68, 75, 83, 90, 98, 105, 113, 120, 263, 128, 135, 143, 150, 158, 165, 173, 180, 188, 375
   };
   private static int[] publicCoreUpgradeSolErdaCost = new int[]{1, 1, 1, 2, 2, 2, 3, 3, 10, 3, 3, 4, 4, 4, 4, 4, 4, 5, 15, 5, 5, 5, 5, 5, 6, 6, 6, 7, 20};
   private static int[] publicCoreUpgradeSolErdaPieceCost = new int[]{
      30, 35, 40, 45, 50, 55, 60, 65, 200, 80, 90, 100, 110, 120, 130, 140, 150, 160, 350, 170, 180, 190, 200, 210, 220, 230, 240, 250, 500
   };
   private static int[] hexaStatUpgradeSolErdaCost = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   private static int[] hexaStatUpgradeSolErdaPieceCost = new int[]{10, 10, 10, 20, 20, 20, 20, 30, 30, 50, 50};
   private static double[] hexaStatUpgradeWeight = new double[]{0.35, 0.35, 0.35, 0.2, 0.2, 0.2, 0.2, 0.15, 0.1, 0.05};
   public static int solErdaStrengthMax = 1000;
   public static int solErdaMax = 20;
   public static final Map<Integer, List<Integer>> sixthJobSkillCore = new HashMap<>();
   public static final Map<Integer, List<Integer>> sixthJobEnforceCore = new HashMap<>();
   public static final Map<Integer, List<Integer>> sixthJobMasteryCore = new HashMap<>();
   public static final Map<Integer, List<Integer>> sixthJobCommonCore = new HashMap<>();
   public static final Map<Integer, List<Integer>> linkedCoreSkill = new HashMap<>();
   public static final Map<Integer, List<Pair<Integer, Integer>>> reqForActivation = new HashMap<>();
   private static final Map<Integer, String> coreName = new HashMap<>();
   private static final Map<Integer, String> coreDesc = new HashMap<>();
   private static final Map<HexaMatrixConstants.HexaStatOption, Map<Integer, Double>> mainHexaStatValue = new EnumMap<>(
      HexaMatrixConstants.HexaStatOption.class
   );
   private static final Map<HexaMatrixConstants.HexaStatOption, Map<Integer, Double>> subHexaStatValue = new EnumMap<>(HexaMatrixConstants.HexaStatOption.class);

   public static int getHexaStatCoreIdByIndex(int index) {
      return 50000000;
   }

   public static int getHexaStatIndexByCoreId(int coreid) {
      return 0;
   }

   public static void init() {
      String wzPath = System.getProperty("net.sf.odinms.wzpath");
      MapleDataProvider etcWz = MapleDataProviderFactory.getDataProvider(new File(wzPath + "/Etc.wz"));
      MapleData hexaCore = etcWz.getData("HexaCore.img");
      MapleData solErda = hexaCore.getChildByPath("solErda");
      solErdaStrengthMax = MapleDataTool.getInt("gaugeMax", solErda, 1000);
      solErdaMax = MapleDataTool.getInt("slotMax", solErda, 20);
      MapleData hexaSkill = hexaCore.getChildByPath("hexaSkill");
      MapleData coreData = hexaSkill.getChildByPath("coreData");

      for (MapleData core : coreData.getChildren()) {
         try {
            int coreId = Integer.parseInt(core.getName());
            String name = MapleDataTool.getString("name", core, "NULL");
            String desc = MapleDataTool.getString("desc", core, "NULL");
            List<Integer> connectSkill = new ArrayList<>();

            for (MapleData connect : core.getChildByPath("connectSkill").getChildren()) {
               int link = MapleDataTool.getInt(connect);
               connectSkill.add(link);
            }

            List<Pair<Integer, Integer>> reqForActivationList = new ArrayList<>();
            if (core.getChildByPath("reqForActivation") != null) {
               for (MapleData req : core.getChildByPath("reqForActivation").getChildren()) {
                  int skillId = Integer.parseInt(req.getName());
                  int level = MapleDataTool.getInt(req);
                  reqForActivationList.add(new Pair<>(skillId, level));
               }
            }

            reqForActivation.put(coreId, reqForActivationList);
            linkedCoreSkill.put(coreId, connectSkill);
            coreName.put(coreId, name);
            coreDesc.put(coreId, desc);
         } catch (Exception var24) {
            System.out.println("6차 코어 정보 파싱 오류");
            var24.printStackTrace();
         }
      }

      MapleData jobCore = hexaSkill.getChildByPath("jobCore");

      for (MapleData jobData : jobCore.getChildren()) {
         try {
            int jobId = Integer.parseInt(jobData.getName());
            MapleData mastery = jobData.getChildByPath("mastery");
            List<Integer> mList = new ArrayList<>();

            for (MapleData mSkill : mastery.getChildren()) {
               int coreId = MapleDataTool.getInt(mSkill);
               mList.add(coreId);
            }

            sixthJobMasteryCore.put(jobId, mList);
            List<Integer> eList = new ArrayList<>();
            MapleData enforce = jobData.getChildByPath("enforce");

            for (MapleData eSkill : enforce.getChildren()) {
               int coreId = MapleDataTool.getInt(eSkill);
               eList.add(coreId);
            }

            sixthJobEnforceCore.put(jobId, eList);
            List<Integer> sList = new ArrayList<>();
            MapleData skill = jobData.getChildByPath("skill");

            for (MapleData sSkill : skill.getChildren()) {
               int coreId = MapleDataTool.getInt(sSkill);
               sList.add(coreId);
            }

            sixthJobSkillCore.put(jobId, sList);
            List<Integer> cList = new ArrayList<>();
            MapleData common = jobData.getChildByPath("common");

            for (MapleData cSkill : common.getChildren()) {
               int coreId = MapleDataTool.getInt(cSkill);
               cList.add(coreId);
            }

            sixthJobCommonCore.put(jobId, cList);
         } catch (Exception var23) {
            System.out.println("6차 직업 코어 파싱 오류");
            var23.printStackTrace();
         }
      }

      MapleData hexaStat = hexaCore.getChildByPath("hexaStat");
      MapleData mainType = hexaStat.getChildByPath("stat/main/type");

      for (MapleData typeData : mainType.getChildren()) {
         try {
            HexaMatrixConstants.HexaStatOption type = HexaMatrixConstants.HexaStatOption.findByValue(Integer.parseInt(typeData.getName()));
            if (type == null) {
               System.out.println("정의되지 않은 헥사스텟" + typeData.getName());
            } else {
               Map<Integer, Double> tempMap = new HashMap<>();

               for (MapleData levelData : typeData.getChildByPath("level").getChildren()) {
                  int level = Integer.parseInt(levelData.getName());
                  int stat = MapleDataTool.getInt(type.getWzValueName(), levelData);
                  double result = stat;
                  if (type.getWzValueName().endsWith("PerM")) {
                     result = stat / 100.0;
                  }

                  tempMap.put(level, result);
               }

               mainHexaStatValue.put(type, tempMap);
            }
         } catch (Exception var22) {
            System.out.println("헥사 스텟 메인 옵션 파싱 오류");
            var22.printStackTrace();
         }
      }

      MapleData subType = hexaStat.getChildByPath("stat/additional/type");

      for (MapleData typeData : subType.getChildren()) {
         try {
            HexaMatrixConstants.HexaStatOption type = HexaMatrixConstants.HexaStatOption.findByValue(Integer.parseInt(typeData.getName()));
            if (type == null) {
               System.out.println("정의되지 않은 헥사스텟" + typeData.getName());
            } else {
               Map<Integer, Double> tempMap = new HashMap<>();

               for (MapleData levelData : typeData.getChildByPath("level").getChildren()) {
                  int level = Integer.parseInt(levelData.getName());
                  int stat = MapleDataTool.getInt(type.getWzValueName(), levelData);
                  double result = stat;
                  if (type.getWzValueName().endsWith("PerM")) {
                     result = stat / 100.0;
                  }

                  tempMap.put(level, result);
               }

               subHexaStatValue.put(type, tempMap);
            }
         } catch (Exception var21) {
            System.out.println("헥사 스텟 서브 옵션 파싱 오류");
            var21.printStackTrace();
         }
      }

      System.out.println("Hexa Matrix 정보 로드 완료");
   }

   public static HexaMatrixConstants.HexaMatrixFlag[] getSkillFlags() {
      return new HexaMatrixConstants.HexaMatrixFlag[]{
         HexaMatrixConstants.HexaMatrixFlag.SKILL_CORE,
         HexaMatrixConstants.HexaMatrixFlag.MASTERY_CORE,
         HexaMatrixConstants.HexaMatrixFlag.ENFORCE_CORE,
         HexaMatrixConstants.HexaMatrixFlag.COMMON_CORE
      };
   }

   public static int getNeedSolErdaToOpenHexaSkill(HexaMatrixConstants.HexaMatrixFlag flag) {
      int need = 5;
      switch (flag) {
         case SKILL_CORE:
            need = 5;
            break;
         case MASTERY_CORE:
            need = 3;
            break;
         case ENFORCE_CORE:
            need = 4;
            break;
         case COMMON_CORE:
            need = 5;
            break;
         case HEXA_STAT:
            need = 5;
      }

      return need;
   }

   public static int getNeedSolErdaPieceToOpenHexaSkill(HexaMatrixConstants.HexaMatrixFlag flag) {
      int need = 100;
      switch (flag) {
         case SKILL_CORE:
            need = 100;
            break;
         case MASTERY_CORE:
            need = 50;
            break;
         case ENFORCE_CORE:
            need = 75;
            break;
         case COMMON_CORE:
            need = 100;
            break;
         case HEXA_STAT:
            need = 10;
      }

      return need;
   }

   public static int getNeedSolErdaToUpgradeHexaSkill(HexaMatrixConstants.HexaMatrixFlag flag, int level) {
      int[] needs = null;
      switch (flag) {
         case SKILL_CORE:
            needs = originSkillUpgradeSolErdaCost;
            break;
         case MASTERY_CORE:
            needs = masteryCoreUpgradeSolErdaCost;
            break;
         case ENFORCE_CORE:
            needs = enforceCoreUpgradeSolErdaCost;
            break;
         case COMMON_CORE:
            needs = publicCoreUpgradeSolErdaCost;
            break;
         case HEXA_STAT:
            needs = hexaStatUpgradeSolErdaCost;
            level++;
      }

      return needs != null && needs.length >= level ? needs[level - 1] : 20;
   }

   public static int getNeedSolErdaPieceToUpgradeHexaSkill(HexaMatrixConstants.HexaMatrixFlag flag, int level) {
      int[] needs = null;
      switch (flag) {
         case SKILL_CORE:
            needs = originSkillUpgradeSolErdaPieceCost;
            break;
         case MASTERY_CORE:
            needs = masteryCoreUpgradeSolErdaPieceCost;
            break;
         case ENFORCE_CORE:
            needs = enforceCoreUpgradeSolErdaPieceCost;
            break;
         case COMMON_CORE:
            needs = publicCoreUpgradeSolErdaPieceCost;
            break;
         case HEXA_STAT:
            needs = hexaStatUpgradeSolErdaPieceCost;
            level++;
      }

      return needs != null && needs.length >= level ? needs[level - 1] : 20;
   }

   public static int getHexaSkillMasterLevel(HexaMatrixConstants.HexaMatrixFlag flag) {
      int[] needs = null;
      switch (flag) {
         case SKILL_CORE:
            needs = originSkillUpgradeSolErdaPieceCost;
            break;
         case MASTERY_CORE:
            needs = masteryCoreUpgradeSolErdaPieceCost;
            break;
         case ENFORCE_CORE:
            needs = enforceCoreUpgradeSolErdaPieceCost;
            break;
         case COMMON_CORE:
            needs = publicCoreUpgradeSolErdaPieceCost;
            break;
         case HEXA_STAT:
            needs = hexaStatUpgradeSolErdaPieceCost;
      }

      return needs == null ? 30 : needs.length;
   }

   public static int getHexaStatMasterLevel() {
      return hexaStatUpgradeWeight.length;
   }

   public static double getHexaStatWeight(int level) {
      return level >= hexaStatUpgradeWeight.length ? 0.0 : hexaStatUpgradeWeight[level];
   }

   public static double getHexaStatMainValue(HexaMatrixConstants.HexaStatOption opt, int level) {
      if (mainHexaStatValue.get(opt) == null) {
         return 0.0;
      } else {
         return mainHexaStatValue.get(opt).get(level) == null ? 0.0 : mainHexaStatValue.get(opt).get(level);
      }
   }

   public static double getHexaStatSubValue(HexaMatrixConstants.HexaStatOption opt, int level) {
      if (subHexaStatValue.get(opt) == null) {
         return 0.0;
      } else {
         return subHexaStatValue.get(opt).get(level) == null ? 0.0 : subHexaStatValue.get(opt).get(level);
      }
   }

   public static Map<Integer, String> searchCoreName(String search) {
      Map<Integer, String> result = new HashMap<>();

      for (Entry<Integer, String> entry : coreName.entrySet()) {
         if (entry.getValue().contains(search)) {
            String name = entry.getValue();
            String desc = coreDesc.get(entry.getKey());
            result.put(entry.getKey(), name + " : " + desc);
         }
      }

      return result;
   }

   public static int getNeedSolErdaPieceToUpgradeMainHexaStat(int level) {
      switch (level) {
         case 0:
         case 1:
         case 2:
            return 10;
         case 3:
         case 4:
         case 5:
         case 6:
            return 20;
         case 7:
         case 8:
            return 30;
         case 9:
            return 50;
         default:
            return 50;
      }
   }

   public static List<Integer> getAllJobCores(int job) {
      List<Integer> list = new ArrayList<>();
      if (sixthJobSkillCore.get(job) != null) {
         list.addAll(sixthJobSkillCore.get(job));
      }

      if (sixthJobEnforceCore.get(job) != null) {
         list.addAll(sixthJobEnforceCore.get(job));
      }

      if (sixthJobMasteryCore.get(job) != null) {
         list.addAll(sixthJobMasteryCore.get(job));
      }

      if (sixthJobCommonCore.get(job) != null) {
         list.addAll(sixthJobCommonCore.get(job));
      }

      return list;
   }

   public static List<Integer> getOriginSkillCores(int job) {
      List<Integer> list = new ArrayList<>();
      if (sixthJobSkillCore.get(job) != null) {
         list.addAll(sixthJobSkillCore.get(job));
      }

      return list;
   }

   public static int searchCoreIdBySkill(int skillId) {
      for (Entry<Integer, List<Integer>> entry : linkedCoreSkill.entrySet()) {
         if (entry.getValue().contains(skillId)) {
            return entry.getKey();
         }
      }

      return 0;
   }

   public static boolean isAllowCooldownSkill(int jobId, int skillId) {
      List<Integer> coreList = new ArrayList<>();
      if (sixthJobEnforceCore.get(jobId) != null) {
         coreList.addAll(sixthJobEnforceCore.get(jobId));
      }

      if (sixthJobMasteryCore.containsKey(jobId)) {
         coreList.addAll(sixthJobMasteryCore.get(jobId));
      }

      for (int core : coreList) {
         List<Integer> coreSkillList = linkedCoreSkill.get(core);
         if (coreSkillList != null) {
            for (int skill : coreSkillList) {
               if (skill == skillId) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static enum HexaMatrixFlag {
      SKILL_CORE(0),
      MASTERY_CORE(1),
      ENFORCE_CORE(2),
      COMMON_CORE(3),
      HEXA_STAT(-1);

      int flag;

      private HexaMatrixFlag(int flag) {
         this.flag = flag;
      }

      public int getFlag() {
         return this.flag;
      }
   }

   public static enum HexaMatrixMsg {
      Reslut(0),
      UnknownError(1),
      UserInfoError(2),
      PlayerInfoError(3),
      NotEnoughSolErda(4),
      NotEnoughMeso(5),
      GainMesoError(6),
      SkillcConditionError(7),
      StatChangeError(8),
      SecondPassWordError(9);

      int type;

      private HexaMatrixMsg(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }
   }

   public static enum HexaStatOption {
      CRITICAL_DAMAGE(0, "cdPerM"),
      BOSS_DAMAGE(1, "bdRPerM"),
      IGNORE_DEFENSE(2, "ignoreMobpdpRPerM"),
      INCREASE_DAMAGE(3, "damRPerM"),
      INCREASE_PAD(4, "padX"),
      INCREASE_MAD(5, "madX"),
      INCREASE_MAINSTAT(6, "indieStat");

      int type;
      String wzValueName;

      private HexaStatOption(int type, String wzValueName) {
         this.type = type;
         this.wzValueName = wzValueName;
      }

      public int getType() {
         return this.type;
      }

      public String getWzValueName() {
         return this.wzValueName;
      }

      public static HexaMatrixConstants.HexaStatOption findByValue(int type) {
         for (HexaMatrixConstants.HexaStatOption opt : values()) {
            if (opt.getType() == type) {
               return opt;
            }
         }

         return null;
      }
   }
}
