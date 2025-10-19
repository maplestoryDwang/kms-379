package objects.users.extra;

import database.DBConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import objects.utils.Properties;
import objects.utils.Randomizer;
import objects.utils.Table;

public class ExtraAbilityFactory {
   public static List<ExtraAbilityEntry> rareList = new ArrayList<>();
   public static List<ExtraAbilityEntry> epicList = new ArrayList<>();
   public static List<ExtraAbilityEntry> uniqueList = new ArrayList<>();
   public static List<ExtraAbilityEntry> legendaryList = new ArrayList<>();
   public static List<ExtraAbilityEntry> extraList = new ArrayList<>();

   public static void loadData() {
      rareList.clear();
      epicList.clear();
      uniqueList.clear();
      legendaryList.clear();
      extraList.clear();
      Table rare = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim/Extra" : "data/Jin/Extra", "Rare.data");

      for (Table children : rare.list()) {
         String option = children.getName();
         int optionWeight = Integer.parseInt(children.getProperty("Weight"));
         ExtraAbilityEntry.ValueAndWeight[] values = new ExtraAbilityEntry.ValueAndWeight[children.list().size()];
         int index = 0;

         for (Table data : children.list()) {
            int value = Integer.parseInt(data.getProperty("Value"));
            int weight = Integer.parseInt(data.getProperty("Weight"));
            values[index++] = new ExtraAbilityEntry.ValueAndWeight(value, weight);
         }

         rareList.add(new ExtraAbilityEntry(ExtraAbilityOption.getByName(option), optionWeight, values));
      }

      Table epic = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim/Extra" : "data/Jin/Extra", "Epic.data");

      for (Table children : epic.list()) {
         String option = children.getName();
         int optionWeight = Integer.parseInt(children.getProperty("Weight"));
         ExtraAbilityEntry.ValueAndWeight[] values = new ExtraAbilityEntry.ValueAndWeight[children.list().size()];
         int index = 0;

         for (Table data : children.list()) {
            int value = Integer.parseInt(data.getProperty("Value"));
            int weight = Integer.parseInt(data.getProperty("Weight"));
            values[index++] = new ExtraAbilityEntry.ValueAndWeight(value, weight);
         }

         epicList.add(new ExtraAbilityEntry(ExtraAbilityOption.getByName(option), optionWeight, values));
      }

      Table unique = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim/Extra" : "data/Jin/Extra", "Unique.data");

      for (Table children : unique.list()) {
         String option = children.getName();
         int optionWeight = Integer.parseInt(children.getProperty("Weight"));
         ExtraAbilityEntry.ValueAndWeight[] values = new ExtraAbilityEntry.ValueAndWeight[children.list().size()];
         int index = 0;

         for (Table data : children.list()) {
            int value = Integer.parseInt(data.getProperty("Value"));
            int weight = Integer.parseInt(data.getProperty("Weight"));
            values[index++] = new ExtraAbilityEntry.ValueAndWeight(value, weight);
         }

         uniqueList.add(new ExtraAbilityEntry(ExtraAbilityOption.getByName(option), optionWeight, values));
      }

      Table legendary = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim/Extra" : "data/Jin/Extra", "Legendary.data");

      for (Table children : legendary.list()) {
         String option = children.getName();
         int optionWeight = Integer.parseInt(children.getProperty("Weight"));
         ExtraAbilityEntry.ValueAndWeight[] values = new ExtraAbilityEntry.ValueAndWeight[children.list().size()];
         int index = 0;

         for (Table data : children.list()) {
            int value = Integer.parseInt(data.getProperty("Value"));
            int weight = Integer.parseInt(data.getProperty("Weight"));
            values[index++] = new ExtraAbilityEntry.ValueAndWeight(value, weight);
         }

         legendaryList.add(new ExtraAbilityEntry(ExtraAbilityOption.getByName(option), optionWeight, values));
      }

      if (DBConfig.isGanglim) {
         Table extra = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim/Extra" : "data/Jin/Extra", "Extra.data");

         for (Table children : extra.list()) {
            String option = children.getName();
            int optionWeight = Integer.parseInt(children.getProperty("Weight"));
            ExtraAbilityEntry.ValueAndWeight[] values = new ExtraAbilityEntry.ValueAndWeight[children.list().size()];
            int index = 0;

            for (Table data : children.list()) {
               int value = Integer.parseInt(data.getProperty("Value"));
               int weight = Integer.parseInt(data.getProperty("Weight"));
               values[index++] = new ExtraAbilityEntry.ValueAndWeight(value, weight);
            }

            extraList.add(new ExtraAbilityEntry(ExtraAbilityOption.getByName(option), optionWeight, values));
         }
      }
   }

   public static double getGradeUpRate(ExtraAbilityGrade grade, ExtraAbilityPayType type) {
      if (DBConfig.isGanglim) {
         if (grade == ExtraAbilityGrade.Rare) {
            return 15.0;
         }

         if (grade == ExtraAbilityGrade.Epic) {
            return 3.8;
         }

         if (grade == ExtraAbilityGrade.Unique) {
            return 1.0;
         }
      } else if (grade == ExtraAbilityGrade.Rare) {
         switch (type) {
            case Meso:
               return 5.0;
            case Promotion:
               return 10.0;
            case Donation:
               return 15.0;
         }
      } else if (grade == ExtraAbilityGrade.Epic) {
         switch (type) {
            case Meso:
               return 0.9;
            case Promotion:
               return 2.5;
            case Donation:
               return 3.8;
         }
      } else if (grade == ExtraAbilityGrade.Unique) {
         switch (type) {
            case Meso:
               return 0.0;
            case Promotion:
               return 0.7;
            case Donation:
               return 1.0;
         }
      }

      return 0.0;
   }

   public static int getLuckyLine() {
      int rand = Randomizer.rand(0, 100);
      if (DBConfig.isGanglim) {
         if (rand < 10) {
            return 5;
         }

         if (rand >= 10 && rand < 20) {
            return 4;
         }

         if (rand >= 20 && rand < 35) {
            return 3;
         }

         if (rand >= 35 && rand < 60) {
            return 2;
         }

         if (rand >= 60) {
            return 1;
         }
      } else {
         if (rand < 20) {
            return 3;
         }

         if (rand < 50) {
            return 2;
         }
      }

      return 1;
   }

   public static void main(String[] args) {
      loadData();
      test1();
   }

   public static void test3() {
      int totalCount = 0;

      for (int i = 0; i < 1000; i++) {
         int luckyPoint = 0;
         int count = 0;

         while (true) {
            luckyPoint += 3;
            ExtraAbilityStatEntry[] entry = pickMeUp(ExtraAbilityGrade.Legendary, luckyPoint, ExtraAbilityPayType.Donation);
            boolean isMax = false;
            int maxCount = 0;
            boolean find = true;
            int index = 0;

            for (ExtraAbilityStatEntry e : entry) {
               List<ExtraAbilityEntry> list = null;
               ArrayList<ExtraAbilityEntry> var19;
               if (index == 0) {
                  var19 = new ArrayList<>(legendaryList);
               } else {
                  var19 = new ArrayList<>(uniqueList);
               }

               ExtraAbilityEntry zz = var19.stream().filter(f -> f.getOption() == e.getOption()).findFirst().orElse(null);
               if (zz == null) {
                  List<ExtraAbilityEntry> var20 = new ArrayList<>(legendaryList);
                  zz = var20.stream().filter(f -> f.getOption() == e.getOption()).findFirst().orElse(null);
               }

               isMax = zz.isMaxValue(e.getValue());
               if (isMax) {
                  maxCount++;
               }

               if (index == 0) {
                  if (e.getOption() != ExtraAbilityOption.BossDamageR) {
                     find = false;
                  }
               } else if (e.getOption() != ExtraAbilityOption.AttackR) {
                  find = false;
               }

               index++;
            }

            count++;
            if (maxCount >= 3 && find) {
               for (ExtraAbilityStatEntry e : entry) {
                  System.out.println(e.getOption().name() + " " + e.getValue() + " (isMax : " + isMax + ")");
               }

               totalCount += count;
               break;
            }

            if (luckyPoint >= 30) {
               luckyPoint -= 30;
            }
         }
      }

      System.out.println("총 1000번 시뮬레이션 결과 토탈 " + totalCount + "번 소요, 평균 " + totalCount / 1000 + "회 필요");
   }

   public static void test2() {
      int totalCount = 0;

      for (int i = 0; i < 1000; i++) {
         ExtraAbilityGrade grade = ExtraAbilityGrade.Rare;
         int luckyPoint = 0;
         int count = 0;

         do {
            luckyPoint += 3;
            double rate = getGradeUpRate(grade, ExtraAbilityPayType.Donation);
            if (Randomizer.isSuccess((int)(rate * 10.0), 1000) && grade != ExtraAbilityGrade.Legendary) {
               grade = ExtraAbilityGrade.getGrade(grade.getGradeID() + 1);
            }

            ExtraAbilityStatEntry[] entry = pickMeUp(grade, luckyPoint, ExtraAbilityPayType.Donation);

            for (ExtraAbilityStatEntry e : entry) {
               if (e.getOption() == null) {
                  System.out.println("?");
               }
            }

            if (luckyPoint >= 30) {
               luckyPoint -= 30;
            }

            count++;
         } while (grade != ExtraAbilityGrade.Legendary);

         System.out.println("레전드리 까지 가는 데 총 " + count + "회 소요됨.");
         totalCount += count;
      }

      System.out.println("총 1000번 시뮬레이션 결과 토탈 " + totalCount + "번 소요, 평균 " + totalCount / 1000 + "회 필요");
   }

   public static int checkAllMaxValue(ExtraAbilityStatEntry[] entry) {
      boolean isMax = false;
      int maxCount = 0;
      int findOptionMatchCount = 0;
      int index = 0;
      ExtraAbilityOption lastOption = null;

      for (ExtraAbilityStatEntry e : entry) {
         List<ExtraAbilityEntry> list = null;
         ArrayList<ExtraAbilityEntry> var13;
         if (index == 0) {
            var13 = new ArrayList<>(legendaryList);
         } else {
            var13 = new ArrayList<>(uniqueList);
         }

         ExtraAbilityEntry zz = var13.stream().filter(f -> f.getOption() == e.getOption()).findFirst().orElse(null);
         if (zz == null) {
            List<ExtraAbilityEntry> var14 = new ArrayList<>(legendaryList);
            zz = var14.stream().filter(f -> f.getOption() == e.getOption()).findFirst().orElse(null);
         }

         isMax = zz.isMaxValue(e.getValue());
         if (isMax) {
            maxCount++;
         }

         if (lastOption == null || lastOption == e.getOption()) {
            findOptionMatchCount++;
         }

         lastOption = e.getOption();
         index++;
      }

      if (maxCount >= 3 && findOptionMatchCount >= 3) {
         return 2;
      } else {
         return findOptionMatchCount >= 3 ? 1 : 0;
      }
   }

   public static void test1() {
      int totalCount = 0;

      for (int i = 0; i < 1000; i++) {
         int luckyPoint = 0;
         int count = 0;

         while (true) {
            luckyPoint += 2;
            ExtraAbilityStatEntry[] entry = pickMeUp(ExtraAbilityGrade.Legendary, luckyPoint, ExtraAbilityPayType.Donation);
            boolean isMax = false;
            int maxCount = 0;
            int findOptionMatchCount = 0;
            int index = 0;

            for (ExtraAbilityStatEntry e : entry) {
               List<ExtraAbilityEntry> list = null;
               ArrayList<ExtraAbilityEntry> var19;
               if (index == 0) {
                  var19 = new ArrayList<>(legendaryList);
               } else {
                  var19 = new ArrayList<>(uniqueList);
               }

               ExtraAbilityEntry zz = var19.stream().filter(f -> f.getOption() == e.getOption()).findFirst().orElse(null);
               if (zz == null) {
                  List<ExtraAbilityEntry> var20 = new ArrayList<>(legendaryList);
                  zz = var20.stream().filter(f -> f.getOption() == e.getOption()).findFirst().orElse(null);
               }

               isMax = zz.isMaxValue(e.getValue());
               if (isMax) {
                  maxCount++;
               }

               if (e.getOption() == ExtraAbilityOption.BossDamageR) {
                  findOptionMatchCount++;
               }

               index++;
            }

            count++;
            if (findOptionMatchCount >= 3) {
               for (ExtraAbilityStatEntry e : entry) {
                  System.out.println(e.getOption().name() + " " + e.getValue() + " (isMax : " + isMax + ")");
               }

               totalCount += count;
               System.out.println("총 " + count + "번 만에 올이탈 + 최상옵으로 BossDamageR 뽑힘");
               break;
            }

            if (luckyPoint >= 30) {
               luckyPoint -= 30;
            }
         }
      }

      System.out.println("총 1000번 시뮬레이션 결과 토탈 " + totalCount + "번 소요, 평균 " + totalCount / 1000 + "회 필요");
   }

   public static ExtraAbilityStatEntry[] pickMeUpRoyal(ExtraAbilityGrade grade, int luckyPoint, int mvpGrade, boolean enableExtra) {
      ExtraAbilityStatEntry[] ret = new ExtraAbilityStatEntry[6];
      boolean lucky = luckyPoint >= 30;
      int maxValueCount = 0;
      if (lucky) {
         maxValueCount = getLuckyLine();
      }

      for (int i = 0; i < 6; i++) {
         if (i == 6) {
            maxValueCount = 0;
         }

         int rand = 0;
         if (i == 5 && !enableExtra) {
            ret[i] = new ExtraAbilityStatEntry(ExtraAbilityOption.None, 0);
            break;
         }

         if (i == 4 && mvpGrade < 12) {
            ret[i] = new ExtraAbilityStatEntry(ExtraAbilityOption.None, 0);
         } else if (i == 3 && mvpGrade < 6) {
            ret[i] = new ExtraAbilityStatEntry(ExtraAbilityOption.None, 0);
         } else {
            int gradeID = -1;
            gradeID = grade.getGradeID();
            if (gradeID == -1) {
               System.out.println("엑스트라 어빌리티 설정 중 어떻게 gradeID가 -1?");
               return null;
            }

            ExtraAbilityGrade pickGrade = ExtraAbilityGrade.getGrade(gradeID);
            ExtraAbilityEntry entry = null;
            List<ExtraAbilityEntry> list = null;
            switch (pickGrade) {
               case Rare:
                  list = new ArrayList<>(rareList);
                  break;
               case Epic:
                  list = new ArrayList<>(epicList);
                  break;
               case Unique:
                  list = new ArrayList<>(uniqueList);
                  break;
               case Legendary:
                  list = new ArrayList<>(legendaryList);
            }

            if (i == 5) {
               list = new ArrayList<>(extraList);
            }

            if (list == null) {
               System.out.println("엑스트라 어빌리티 설정 중 어떻게 list가 null?");
               return null;
            }

            Collections.shuffle(list);
            int maxWeight = 0;

            for (ExtraAbilityEntry e : list) {
               maxWeight += e.getWeight();
            }

            int r = Randomizer.rand(1, maxWeight);
            int v = 0;

            for (ExtraAbilityEntry e : list) {
               v += e.getWeight();
               if (rand <= v) {
                  entry = e;
                  break;
               }
            }

            if (entry == null) {
               System.out.println("엑스트라 어빌리티 설정 중 어떻게 entry가 null?");
               return null;
            }

            ret[i] = new ExtraAbilityStatEntry(entry.getOption(), entry.pickValue(maxValueCount > 0));
            maxValueCount--;
         }
      }

      return ret;
   }

   public static ExtraAbilityStatEntry[] pickMeUp(ExtraAbilityGrade grade, int luckyPoint, ExtraAbilityPayType type) {
      ExtraAbilityStatEntry[] ret = new ExtraAbilityStatEntry[3];
      boolean lucky = luckyPoint >= 30;
      int maxValueCount = 0;
      if (lucky) {
         maxValueCount = getLuckyLine();
      }

      for (int i = 0; i < 3; i++) {
         int rand = 0;
         if (i != 0 && grade != ExtraAbilityGrade.Rare) {
            if (i != 1 || type != ExtraAbilityPayType.Donation && type != ExtraAbilityPayType.Promotion) {
               if (i == 2 && type == ExtraAbilityPayType.Donation) {
                  rand = 10;
               }
            } else {
               rand = 30;
            }
         } else {
            rand = 100;
         }

         int gradeID = -1;
         if (Randomizer.isSuccess(rand)) {
            gradeID = grade.getGradeID();
         } else {
            gradeID = grade.getGradeID() - 1;
         }

         if (gradeID == -1) {
            System.out.println("엑스트라 어빌리티 설정 중 어떻게 gradeID가 -1?");
            return null;
         }

         ExtraAbilityGrade pickGrade = ExtraAbilityGrade.getGrade(gradeID);
         ExtraAbilityEntry entry = null;
         List<ExtraAbilityEntry> list = null;
         switch (pickGrade) {
            case Rare:
               list = new ArrayList<>(rareList);
               break;
            case Epic:
               list = new ArrayList<>(epicList);
               break;
            case Unique:
               list = new ArrayList<>(uniqueList);
               break;
            case Legendary:
               list = new ArrayList<>(legendaryList);
         }

         if (list == null) {
            System.out.println("엑스트라 어빌리티 설정 중 어떻게 list가 null?");
            return null;
         }

         Collections.shuffle(list);
         int maxWeight = 0;

         for (ExtraAbilityEntry e : list) {
            maxWeight += e.getWeight();
         }

         int r = Randomizer.rand(1, maxWeight);
         int v = 0;

         for (ExtraAbilityEntry e : list) {
            v += e.getWeight();
            if (rand <= v) {
               entry = e;
               break;
            }
         }

         if (entry == null) {
            System.out.println("엑스트라 어빌리티 설정 중 어떻게 entry가 null?");
            return null;
         }

         ret[i] = new ExtraAbilityStatEntry(entry.getOption(), entry.pickValue(maxValueCount > 0));
         maxValueCount--;
      }

      return ret;
   }
}
