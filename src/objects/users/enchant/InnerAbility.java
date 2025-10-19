package objects.users.enchant;

import database.DBConfig;
import database.loader.CharacterSaveFlag;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import objects.item.Item;
import objects.users.MapleCharacter;
import objects.users.achievement.AchievementFactory;
import objects.users.potential.CharacterPotentialHolder;
import objects.utils.Pair;
import objects.utils.Properties;
import objects.utils.Randomizer;
import objects.utils.Table;
import objects.utils.Triple;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class InnerAbility {
   public static HashMap<Integer, InnerAbility.InnerAbilityInfo> innerAbilityInfos = new HashMap<>();
   public static List<Triple<Integer, Integer, Integer>> LegendaryAbility = new ArrayList<>();
   public static List<Triple<Integer, Integer, Integer>> UniqueAbility = new ArrayList<>();
   public static List<Triple<Integer, Integer, Integer>> EpicAbility = new ArrayList<>();
   public static List<Triple<Integer, Integer, Integer>> RareAbility = new ArrayList<>();
   public static HashMap<String, List<Integer>> OptionRequirePointAbility = new HashMap<>();
   private static int gradeUpRare2Epic = 0;
   private static int gradeUpEpic2Unique = 0;
   private static int gradeUpUnique2Legendary = 0;
   private static int UniqueSecondAbilityEpic = 0;
   private static int UniqueThirdAbilityEpic = 0;
   private static int legendarySecondAbilityUnique = 0;
   private static int legendarySecondAbilityEpic = 0;
   private static int legendaryThirdAbilityUnique = 0;
   private static int legendaryThirdAbilityEpic = 0;
   private static int gradeUpEpic2UniqueCirculator = 0;
   private static int gradeUpUnique2LegendaryCirculator = 0;
   private static int UniqueSecondAbilityEpicCirculator = 0;
   private static int UniqueThirdAbilityEpicCirculator = 0;
   private static int legendarySecondAbilityUniqueCirculator = 0;
   private static int legendarySecondAbilityEpicCirculator = 0;
   private static int legendaryThirdAbilityUniqueCirculator = 0;
   private static int legendaryThirdAbilityEpicCirculator = 0;

   public static void loadInnerAbilityPercentage() {
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim" : "data/Jin", "InnerAbilityPercentage.data");
      gradeUpRare2Epic = Integer.parseInt(table.getProperty("gradeUpRare2Epic"));
      gradeUpEpic2Unique = Integer.parseInt(table.getProperty("gradeUpEpic2Unique"));
      gradeUpUnique2Legendary = Integer.parseInt(table.getProperty("gradeUpUnique2Legendary"));
      UniqueSecondAbilityEpic = Integer.parseInt(table.getProperty("UniqueSecondAbilityEpic"));
      UniqueThirdAbilityEpic = Integer.parseInt(table.getProperty("UniqueThirdAbilityEpic"));
      legendarySecondAbilityUnique = Integer.parseInt(table.getProperty("legendarySecondAbilityUnique"));
      legendarySecondAbilityEpic = Integer.parseInt(table.getProperty("legendarySecondAbilityEpic"));
      legendaryThirdAbilityUnique = Integer.parseInt(table.getProperty("legendaryThirdAbilityUnique"));
      legendaryThirdAbilityEpic = Integer.parseInt(table.getProperty("legendaryThirdAbilityEpic"));
      gradeUpEpic2UniqueCirculator = Integer.parseInt(table.getProperty("gradeUpEpic2UniqueCirculator"));
      gradeUpUnique2LegendaryCirculator = Integer.parseInt(table.getProperty("gradeUpUnique2LegendaryCirculator"));
      UniqueSecondAbilityEpicCirculator = Integer.parseInt(table.getProperty("UniqueSecondAbilityEpicCirculator"));
      UniqueThirdAbilityEpicCirculator = Integer.parseInt(table.getProperty("UniqueThirdAbilityEpicCirculator"));
      legendarySecondAbilityUniqueCirculator = Integer.parseInt(table.getProperty("legendarySecondAbilityUniqueCirculator"));
      legendarySecondAbilityEpicCirculator = Integer.parseInt(table.getProperty("legendarySecondAbilityEpicCirculator"));
      legendaryThirdAbilityUniqueCirculator = Integer.parseInt(table.getProperty("legendaryThirdAbilityUniqueCirculator"));
      legendaryThirdAbilityEpicCirculator = Integer.parseInt(table.getProperty("legendaryThirdAbilityEpicCirculator"));
   }

   public static void loadingInnerAbility() {
      MapleData OptionRequirePoint = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz"))
         .getData("InnerAbility.img")
         .getChildByPath("OptionRequirePoint");

      for (MapleData data : OptionRequirePoint.getChildren()) {
         OptionRequirePointAbility.putIfAbsent(data.getName(), new ArrayList<>());

         for (MapleData data0 : data.getChildren()) {
            OptionRequirePointAbility.get(data.getName()).add(MapleDataTool.getInt(data0));
         }
      }

      MapleData data00 = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Skill.wz"))
         .getData("7000.img")
         .getChildByPath("skill");

      for (MapleData data01 : data00.getChildren()) {
         MapleData data02 = data01.getChildByPath("common");
         InnerAbility.InnerAbilityInfo iai = null;
         int maxLevel = MapleDataTool.getInt(data02.getChildByPath("maxLevel"), 0);
         List<String> options = new ArrayList<>();

         for (MapleData data03 : data02.getChildren()) {
            if (!data03.getName().equals("maxLevel")) {
               options.add(data03.getName());
            }
         }

         iai = new InnerAbility.InnerAbilityInfo(maxLevel, options);
         innerAbilityInfos.put(Integer.parseInt(data01.getName()), iai);
      }

      for (Pair<Triple<Integer, Integer, Integer>, Integer> In : LegendaryAbility()) {
         if (innerAbilityInfos.get(In.left.left) != null) {
            LegendaryAbility.add(In.left);
         }
      }

      for (Pair<Triple<Integer, Integer, Integer>, Integer> Inx : UniqueAbility()) {
         if (innerAbilityInfos.get(Inx.left.left) != null) {
            UniqueAbility.add(Inx.left);
         }
      }

      for (Pair<Triple<Integer, Integer, Integer>, Integer> Inxx : EpicAbility()) {
         if (innerAbilityInfos.get(Inxx.left.left) != null) {
            EpicAbility.add(Inxx.left);
         }
      }

      for (Pair<Triple<Integer, Integer, Integer>, Integer> Inxxx : RareAbility()) {
         if (innerAbilityInfos.get(Inxxx.left.left) != null) {
            RareAbility.add(Inxxx.left);
         }
      }

      Collections.shuffle(LegendaryAbility);
      Collections.shuffle(UniqueAbility);
      Collections.shuffle(EpicAbility);
      Collections.shuffle(RareAbility);
      loadInnerAbilityPercentage();
   }

   public static List<Pair<Triple<Integer, Integer, Integer>, Integer>> LegendaryAbility() {
      List<Pair<Triple<Integer, Integer, Integer>, Integer>> LegendaryAbility = new ArrayList<>();
      LegendaryAbility.add(new Pair<>(new Triple<>(70000000, 35, 40), 400));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000001, 35, 40), 400));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000002, 35, 40), 400));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000003, 35, 40), 400));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000006, 35, 40), 178));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000008, 35, 40), 178));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000009, 35, 40), 178));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000012, 25, 30), 222));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000013, 25, 30), 222));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000014, 25, 30), 44));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000015, 35, 40), 178));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000016, 5, 10), 44));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000021, 35, 40), 222));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000022, 35, 40), 222));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000023, 35, 40), 222));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000024, 35, 40), 222));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000025, 5, 10), 222));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000026, 5, 10), 222));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000029, 15, 20), 222));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000027, 15, 20), 178));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000028, 15, 20), 178));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000035, 15, 20), 222));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000036, 35, 40), 178));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000039, 35, 40), 178));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000041, 15, 20), 178));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000045, 15, 20), 178));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000046, 5, 10), 71));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000047, 5, 10), 71));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000048, 35, 40), 89));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000049, 35, 40), 178));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000050, 35, 40), 178));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000051, 35, 40), 311));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000052, 35, 40), 311));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000053, 35, 40), 311));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000054, 35, 40), 311));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000055, 35, 40), 311));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000056, 35, 40), 311));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000057, 35, 40), 311));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000058, 35, 40), 311));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000059, 35, 40), 311));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000060, 35, 40), 311));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000061, 35, 40), 311));
      LegendaryAbility.add(new Pair<>(new Triple<>(70000062, 35, 40), 311));
      return LegendaryAbility;
   }

   public static List<Pair<Triple<Integer, Integer, Integer>, Integer>> UniqueAbility() {
      List<Pair<Triple<Integer, Integer, Integer>, Integer>> UniqueAbility = new ArrayList<>();
      UniqueAbility.add(new Pair<>(new Triple<>(70000000, 25, 30), 388));
      UniqueAbility.add(new Pair<>(new Triple<>(70000001, 25, 30), 388));
      UniqueAbility.add(new Pair<>(new Triple<>(70000002, 25, 30), 388));
      UniqueAbility.add(new Pair<>(new Triple<>(70000003, 25, 30), 388));
      UniqueAbility.add(new Pair<>(new Triple<>(70000006, 25, 30), 216));
      UniqueAbility.add(new Pair<>(new Triple<>(70000008, 25, 30), 216));
      UniqueAbility.add(new Pair<>(new Triple<>(70000009, 25, 30), 216));
      UniqueAbility.add(new Pair<>(new Triple<>(70000010, 25, 30), 216));
      UniqueAbility.add(new Pair<>(new Triple<>(70000011, 25, 30), 216));
      UniqueAbility.add(new Pair<>(new Triple<>(70000012, 15, 20), 129));
      UniqueAbility.add(new Pair<>(new Triple<>(70000013, 15, 20), 129));
      UniqueAbility.add(new Pair<>(new Triple<>(70000014, 15, 20), 43));
      UniqueAbility.add(new Pair<>(new Triple<>(70000015, 25, 30), 172));
      UniqueAbility.add(new Pair<>(new Triple<>(70000021, 25, 30), 259));
      UniqueAbility.add(new Pair<>(new Triple<>(70000022, 25, 30), 259));
      UniqueAbility.add(new Pair<>(new Triple<>(70000023, 25, 30), 259));
      UniqueAbility.add(new Pair<>(new Triple<>(70000024, 25, 30), 259));
      UniqueAbility.add(new Pair<>(new Triple<>(70000029, 5, 10), 172));
      UniqueAbility.add(new Pair<>(new Triple<>(70000027, 5, 10), 172));
      UniqueAbility.add(new Pair<>(new Triple<>(70000028, 5, 10), 172));
      UniqueAbility.add(new Pair<>(new Triple<>(70000035, 5, 10), 86));
      UniqueAbility.add(new Pair<>(new Triple<>(70000036, 25, 30), 172));
      UniqueAbility.add(new Pair<>(new Triple<>(70000039, 25, 30), 172));
      UniqueAbility.add(new Pair<>(new Triple<>(70000041, 5, 10), 172));
      UniqueAbility.add(new Pair<>(new Triple<>(70000045, 5, 10), 172));
      UniqueAbility.add(new Pair<>(new Triple<>(70000048, 25, 30), 86));
      UniqueAbility.add(new Pair<>(new Triple<>(70000049, 25, 30), 172));
      UniqueAbility.add(new Pair<>(new Triple<>(70000050, 25, 30), 172));
      UniqueAbility.add(new Pair<>(new Triple<>(70000051, 25, 30), 345));
      UniqueAbility.add(new Pair<>(new Triple<>(70000052, 25, 30), 345));
      UniqueAbility.add(new Pair<>(new Triple<>(70000053, 25, 30), 345));
      UniqueAbility.add(new Pair<>(new Triple<>(70000054, 25, 30), 345));
      UniqueAbility.add(new Pair<>(new Triple<>(70000055, 25, 30), 345));
      UniqueAbility.add(new Pair<>(new Triple<>(70000056, 25, 30), 345));
      UniqueAbility.add(new Pair<>(new Triple<>(70000057, 25, 30), 345));
      UniqueAbility.add(new Pair<>(new Triple<>(70000058, 25, 30), 345));
      UniqueAbility.add(new Pair<>(new Triple<>(70000059, 25, 30), 345));
      UniqueAbility.add(new Pair<>(new Triple<>(70000060, 25, 30), 345));
      UniqueAbility.add(new Pair<>(new Triple<>(70000061, 25, 30), 345));
      UniqueAbility.add(new Pair<>(new Triple<>(70000062, 25, 30), 345));
      return UniqueAbility;
   }

   public static List<Pair<Triple<Integer, Integer, Integer>, Integer>> EpicAbility() {
      List<Pair<Triple<Integer, Integer, Integer>, Integer>> EpicAbility = new ArrayList<>();
      EpicAbility.add(new Pair<>(new Triple<>(70000000, 15, 20), 385));
      EpicAbility.add(new Pair<>(new Triple<>(70000001, 15, 20), 385));
      EpicAbility.add(new Pair<>(new Triple<>(70000002, 15, 20), 385));
      EpicAbility.add(new Pair<>(new Triple<>(70000003, 15, 20), 385));
      EpicAbility.add(new Pair<>(new Triple<>(70000006, 15, 20), 299));
      EpicAbility.add(new Pair<>(new Triple<>(70000008, 15, 20), 257));
      EpicAbility.add(new Pair<>(new Triple<>(70000009, 15, 20), 257));
      EpicAbility.add(new Pair<>(new Triple<>(70000010, 15, 20), 299));
      EpicAbility.add(new Pair<>(new Triple<>(70000011, 15, 20), 257));
      EpicAbility.add(new Pair<>(new Triple<>(70000012, 5, 10), 171));
      EpicAbility.add(new Pair<>(new Triple<>(70000013, 5, 10), 171));
      EpicAbility.add(new Pair<>(new Triple<>(70000015, 15, 20), 257));
      EpicAbility.add(new Pair<>(new Triple<>(70000021, 15, 20), 257));
      EpicAbility.add(new Pair<>(new Triple<>(70000022, 15, 20), 257));
      EpicAbility.add(new Pair<>(new Triple<>(70000023, 15, 20), 257));
      EpicAbility.add(new Pair<>(new Triple<>(70000024, 15, 20), 257));
      EpicAbility.add(new Pair<>(new Triple<>(70000036, 15, 20), 257));
      EpicAbility.add(new Pair<>(new Triple<>(70000039, 15, 20), 257));
      EpicAbility.add(new Pair<>(new Triple<>(70000048, 15, 20), 128));
      EpicAbility.add(new Pair<>(new Triple<>(70000049, 15, 20), 257));
      EpicAbility.add(new Pair<>(new Triple<>(70000050, 15, 20), 257));
      EpicAbility.add(new Pair<>(new Triple<>(70000051, 15, 20), 359));
      EpicAbility.add(new Pair<>(new Triple<>(70000052, 15, 20), 359));
      EpicAbility.add(new Pair<>(new Triple<>(70000053, 15, 20), 359));
      EpicAbility.add(new Pair<>(new Triple<>(70000054, 15, 20), 359));
      EpicAbility.add(new Pair<>(new Triple<>(70000055, 15, 20), 359));
      EpicAbility.add(new Pair<>(new Triple<>(70000056, 15, 20), 359));
      EpicAbility.add(new Pair<>(new Triple<>(70000057, 15, 20), 359));
      EpicAbility.add(new Pair<>(new Triple<>(70000058, 15, 20), 359));
      EpicAbility.add(new Pair<>(new Triple<>(70000059, 15, 20), 359));
      EpicAbility.add(new Pair<>(new Triple<>(70000060, 15, 20), 359));
      EpicAbility.add(new Pair<>(new Triple<>(70000061, 15, 20), 359));
      EpicAbility.add(new Pair<>(new Triple<>(70000062, 15, 20), 359));
      return EpicAbility;
   }

   public static List<Pair<Triple<Integer, Integer, Integer>, Integer>> RareAbility() {
      List<Pair<Triple<Integer, Integer, Integer>, Integer>> RareAbility = new ArrayList<>();
      RareAbility.add(new Pair<>(new Triple<>(70000000, 5, 10), 351));
      RareAbility.add(new Pair<>(new Triple<>(70000001, 5, 10), 351));
      RareAbility.add(new Pair<>(new Triple<>(70000002, 5, 10), 351));
      RareAbility.add(new Pair<>(new Triple<>(70000003, 5, 10), 351));
      RareAbility.add(new Pair<>(new Triple<>(70000006, 5, 10), 335));
      RareAbility.add(new Pair<>(new Triple<>(70000008, 5, 10), 335));
      RareAbility.add(new Pair<>(new Triple<>(70000009, 5, 10), 335));
      RareAbility.add(new Pair<>(new Triple<>(70000010, 5, 10), 335));
      RareAbility.add(new Pair<>(new Triple<>(70000011, 5, 10), 312));
      RareAbility.add(new Pair<>(new Triple<>(70000015, 5, 10), 312));
      RareAbility.add(new Pair<>(new Triple<>(70000021, 5, 10), 234));
      RareAbility.add(new Pair<>(new Triple<>(70000022, 5, 10), 234));
      RareAbility.add(new Pair<>(new Triple<>(70000023, 5, 10), 234));
      RareAbility.add(new Pair<>(new Triple<>(70000024, 5, 10), 234));
      RareAbility.add(new Pair<>(new Triple<>(70000036, 5, 10), 273));
      RareAbility.add(new Pair<>(new Triple<>(70000039, 5, 10), 273));
      RareAbility.add(new Pair<>(new Triple<>(70000048, 5, 10), 312));
      RareAbility.add(new Pair<>(new Triple<>(70000049, 5, 10), 312));
      RareAbility.add(new Pair<>(new Triple<>(70000050, 5, 10), 312));
      RareAbility.add(new Pair<>(new Triple<>(70000051, 5, 10), 351));
      RareAbility.add(new Pair<>(new Triple<>(70000052, 5, 10), 351));
      RareAbility.add(new Pair<>(new Triple<>(70000053, 5, 10), 351));
      RareAbility.add(new Pair<>(new Triple<>(70000054, 5, 10), 351));
      RareAbility.add(new Pair<>(new Triple<>(70000055, 5, 10), 351));
      RareAbility.add(new Pair<>(new Triple<>(70000056, 5, 10), 351));
      RareAbility.add(new Pair<>(new Triple<>(70000057, 5, 10), 351));
      RareAbility.add(new Pair<>(new Triple<>(70000058, 5, 10), 351));
      RareAbility.add(new Pair<>(new Triple<>(70000059, 5, 10), 351));
      RareAbility.add(new Pair<>(new Triple<>(70000060, 5, 10), 351));
      RareAbility.add(new Pair<>(new Triple<>(70000061, 5, 10), 351));
      RareAbility.add(new Pair<>(new Triple<>(70000062, 5, 10), 351));
      return RareAbility;
   }

   public static boolean DuplicateCheck(List<Integer> ability, int checkAbility) {
      boolean dup = false;
      List<Pair<Integer, Integer>> duplic = new ArrayList<>(
         Arrays.asList(
            new Pair<>(70000000, 70000017),
            new Pair<>(70000001, 70000018),
            new Pair<>(70000002, 70000019),
            new Pair<>(70000003, 70000020),
            new Pair<>(70000004, 70000010),
            new Pair<>(70000005, 70000011),
            new Pair<>(70000006, 70000007),
            new Pair<>(70000014, 70000043),
            new Pair<>(70000015, 70000044),
            new Pair<>(70000025, 70000040),
            new Pair<>(70000026, 70000042),
            new Pair<>(70000027, 70000031),
            new Pair<>(70000028, 70000032),
            new Pair<>(70000029, 70000030),
            new Pair<>(70000033, 70000050)
         )
      );

      for (Integer ab : ability) {
         if (ab.equals(checkAbility)) {
            dup = true;
            break;
         }

         Iterator var6 = duplic.iterator();

         while (true) {
            if (var6.hasNext()) {
               Pair<Integer, Integer> d = (Pair<Integer, Integer>)var6.next();
               if (ab.equals(d.getLeft()) && d.getRight().equals(checkAbility)) {
                  dup = true;
               } else {
                  if (!ab.equals(d.getRight()) || !d.getLeft().equals(checkAbility)) {
                     continue;
                  }

                  dup = true;
               }
            }

            if (dup) {
               return dup;
            }
            break;
         }
      }

      return dup;
   }

   public static void gachaAbility(MapleCharacter User, List<Integer> gradeLockLines) {
      List<Triple<Integer, Integer, Integer>> abilitys = new ArrayList<>();
      int rank = User.getInnerSkills().get(0).getRank();
      List<Integer> dup = new ArrayList<>();
      InnerAbility.GenerateAbility generateAbility = null;
      switch (rank) {
         case 0:
            generateAbility = GenerateAbilityRare();
            if (Randomizer.nextInt(100000) < gradeUpRare2Epic) {
               rank = 1;
               generateAbility = GenerateAbilityEpic();
            }
            break;
         case 1:
            generateAbility = GenerateAbilityEpic();
            int randomGradex = Randomizer.nextInt(100000);
            if (randomGradex < gradeUpEpic2Unique) {
               rank = 2;
               generateAbility = GenerateAbilityUnique(UniqueSecondAbilityEpic, UniqueThirdAbilityEpic);
            }
            break;
         case 2:
            generateAbility = GenerateAbilityUnique(UniqueSecondAbilityEpic, UniqueThirdAbilityEpic);
            int randomGrade = Randomizer.nextInt(100000);
            if (randomGrade < gradeUpUnique2Legendary) {
               rank = 3;
               generateAbility = GenerateAbilityLegendary(
                  legendarySecondAbilityUnique, legendarySecondAbilityEpic, legendaryThirdAbilityUnique, legendaryThirdAbilityEpic
               );
            }
            break;
         case 3:
            generateAbility = GenerateAbilityLegendary(
               legendarySecondAbilityUnique, legendarySecondAbilityEpic, legendaryThirdAbilityUnique, legendaryThirdAbilityEpic
            );
      }

      if (gradeLockLines.size() > 0) {
         for (Integer g : gradeLockLines) {
            CharacterPotentialHolder h = User.getInnerSkills().get(g - 1);
            dup.add(h.getSkillId());
         }
      }

      for (int i = 0; i < 3; i++) {
         if (gradeLockLines.contains(i + 1)) {
            CharacterPotentialHolder inner = User.getInnerSkills().get(i);
            abilitys.add(new Triple<>(inner.getSkillId(), Integer.valueOf(inner.getSkillLevel()), Integer.valueOf(inner.getRank())));
            dup.add(inner.getSkillId());
         } else if (i == 0) {
            while (DuplicateCheck(dup, generateAbility.abi.getLeft())) {
               Triple<Integer, Integer, Integer> abi = null;
               switch (rank) {
                  case 0:
                     abi = RareAbility.get(Randomizer.nextInt(RareAbility.size()));
                     break;
                  case 1:
                     abi = EpicAbility.get(Randomizer.nextInt(EpicAbility.size()));
                     break;
                  case 2:
                     abi = UniqueAbility.get(Randomizer.nextInt(UniqueAbility.size()));
                     break;
                  case 3:
                     abi = LegendaryAbility.get(Randomizer.nextInt(LegendaryAbility.size()));
               }

               generateAbility.setAbi(abi);
            }

            if (DBConfig.isGanglim) {
               abilitys.add(new Triple<>(generateAbility.abi.getLeft(), generateAbility.abi.getRight(), rank));
            } else {
               abilitys.add(new Triple<>(generateAbility.abi.getLeft(), Randomizer.rand(generateAbility.abi.getMid(), generateAbility.abi.getRight()), rank));
            }

            dup.add(generateAbility.abi.getLeft());
         } else if (i == 1) {
            generateAbility.setAbi(generateAbility.secondAbility.get(Randomizer.nextInt(generateAbility.secondAbility.size())));

            while (DuplicateCheck(dup, generateAbility.abi.getLeft())) {
               generateAbility.setAbi(generateAbility.secondAbility.get(Randomizer.nextInt(generateAbility.secondAbility.size())));
            }

            if (DBConfig.isGanglim) {
               abilitys.add(new Triple<>(generateAbility.abi.getLeft(), generateAbility.abi.getRight(), generateAbility.secondRank));
            } else {
               abilitys.add(
                  new Triple<>(
                     generateAbility.abi.getLeft(), Randomizer.rand(generateAbility.abi.getMid(), generateAbility.abi.getRight()), generateAbility.secondRank
                  )
               );
            }

            dup.add(generateAbility.abi.getLeft());
         } else if (i == 2) {
            generateAbility.setAbi(generateAbility.thirdAbility.get(Randomizer.nextInt(generateAbility.thirdAbility.size())));

            while (DuplicateCheck(dup, generateAbility.abi.getLeft())) {
               generateAbility.setAbi(generateAbility.thirdAbility.get(Randomizer.nextInt(generateAbility.thirdAbility.size())));
            }

            if (DBConfig.isGanglim) {
               abilitys.add(new Triple<>(generateAbility.abi.getLeft(), generateAbility.abi.getRight(), generateAbility.thirdRank));
            } else {
               abilitys.add(
                  new Triple<>(
                     generateAbility.abi.getLeft(), Randomizer.rand(generateAbility.abi.getMid(), generateAbility.abi.getRight()), generateAbility.thirdRank
                  )
               );
            }
         }
      }

      User.getInnerSkills().clear();
      List<CharacterPotentialHolder> holders = new ArrayList<>();

      for (Triple<Integer, Integer, Integer> ability : abilitys) {
         holders.add(
            new CharacterPotentialHolder(
               ability.getLeft(),
               (byte)ability.getMid().intValue(),
               (byte)innerAbilityInfos.get(ability.getLeft()).maxLevel,
               (byte)ability.getRight().intValue(),
               false
            )
         );
      }

      for (CharacterPotentialHolder cph : holders) {
         User.getInnerSkills().add(cph);
      }

      User.setSaveFlag(User.getSaveFlag() | CharacterSaveFlag.INNER_SKILL.getFlag());

      for (int ix = 0; ix < holders.size(); ix++) {
         User.send(CField.updateInnerPotential((byte)(ix + 1), holders.get(ix).getSkillId(), holders.get(ix).getSkillLevel(), holders.get(ix).getRank()));
         AchievementFactory.checkAbility(User, holders.get(ix).getSkillId(), holders.get(ix).getSkillLevel(), holders.get(ix).getRank(), 0);
      }

      if (User.isQuestStarted(501547)) {
         if (User.getOneInfoQuestInteger(501547, "value") < 1) {
            User.updateOneInfo(501547, "value", "1");
         }

         if (User.getOneInfoQuestInteger(501524, "state") < 2) {
            User.updateOneInfo(501524, "state", "2");
         }
      }

      User.send(CWvsContext.showPopupMessage("어빌리티 재설정에 성공 하였습니다."));
      User.getStat().recalcLocalStats(User);
   }

   public static void gachaAbilityMiracle(MapleCharacter User, Item item) {
      List<Triple<Integer, Integer, Integer>> abilitys = new ArrayList<>();
      int rank = User.getInnerSkills().get(0).getRank();
      List<Integer> dup = new ArrayList<>();
      InnerAbility.GenerateAbility generateAbility = null;
      switch (rank) {
         case 1:
            generateAbility = GenerateAbilityEpic();
            int randomGradex = Randomizer.nextInt(100000);
            if (randomGradex < gradeUpEpic2UniqueCirculator) {
               rank = 2;
               generateAbility = GenerateAbilityUnique(UniqueSecondAbilityEpicCirculator, UniqueThirdAbilityEpicCirculator);
            }
            break;
         case 2:
            generateAbility = GenerateAbilityUnique(UniqueSecondAbilityEpicCirculator, UniqueThirdAbilityEpicCirculator);
            int randomGrade = Randomizer.nextInt(100000);
            if (randomGrade < gradeUpUnique2LegendaryCirculator) {
               rank = 3;
               generateAbility = GenerateAbilityLegendary(
                  legendarySecondAbilityUniqueCirculator,
                  legendarySecondAbilityEpicCirculator,
                  legendaryThirdAbilityUniqueCirculator,
                  legendaryThirdAbilityEpicCirculator
               );
            }
            break;
         case 3:
            generateAbility = GenerateAbilityLegendary(
               legendarySecondAbilityUniqueCirculator,
               legendarySecondAbilityEpicCirculator,
               legendaryThirdAbilityUniqueCirculator,
               legendaryThirdAbilityEpicCirculator
            );
      }

      for (int i = 0; i < 3; i++) {
         if (i == 0) {
            abilitys.add(new Triple<>(generateAbility.abi.getLeft(), generateAbility.abi.getRight(), rank));
            dup.add(generateAbility.abi.getLeft());
         } else if (i == 1) {
            generateAbility.setAbi(generateAbility.secondAbility.get(Randomizer.nextInt(generateAbility.secondAbility.size())));

            while (DuplicateCheck(dup, generateAbility.abi.getLeft())) {
               generateAbility.setAbi(generateAbility.secondAbility.get(Randomizer.nextInt(generateAbility.secondAbility.size())));
            }

            abilitys.add(new Triple<>(generateAbility.abi.getLeft(), generateAbility.abi.getRight(), generateAbility.secondRank));
            dup.add(generateAbility.abi.getLeft());
         } else if (i == 2) {
            generateAbility.setAbi(generateAbility.thirdAbility.get(Randomizer.nextInt(generateAbility.thirdAbility.size())));

            while (DuplicateCheck(dup, generateAbility.abi.getLeft())) {
               generateAbility.setAbi(generateAbility.thirdAbility.get(Randomizer.nextInt(generateAbility.thirdAbility.size())));
            }

            abilitys.add(new Triple<>(generateAbility.abi.getLeft(), generateAbility.abi.getRight(), generateAbility.thirdRank));
         }
      }

      List<CharacterPotentialHolder> holders = new ArrayList<>();

      for (Triple<Integer, Integer, Integer> ability : abilitys) {
         holders.add(
            new CharacterPotentialHolder(
               ability.getLeft(),
               (byte)ability.getMid().intValue(),
               (byte)innerAbilityInfos.get(ability.getLeft()).maxLevel,
               (byte)ability.getRight().intValue(),
               false
            )
         );
         AchievementFactory.checkAbility(User, ability.getLeft(), ability.getMid(), ability.getRight(), 0);
      }

      if (User.isQuestStarted(501547)) {
         if (User.getOneInfoQuestInteger(501547, "value") < 1) {
            User.updateOneInfo(501547, "value", "1");
         }

         if (User.getOneInfoQuestInteger(501524, "state") < 2) {
            User.updateOneInfo(501524, "state", "2");
         }
      }

      User.setLastMiracleCirculator(holders);
      User.send(CWvsContext.miracleCirculatorWindow(item.getItemId(), item.getUniqueId(), item.getPosition(), holders));
      User.send(CWvsContext.enableActions(User));
      User.getStat().recalcLocalStats(User);
   }

   public static void gachaAbilityLegendary(MapleCharacter User) {
      List<Triple<Integer, Integer, Integer>> abilitys = new ArrayList<>();
      int rank = 3;
      List<Integer> dup = new ArrayList<>();
      InnerAbility.GenerateAbility generateAbility = null;
      switch (rank) {
         case 0:
            generateAbility = GenerateAbilityRare();
            if (Randomizer.nextInt(100000) < gradeUpRare2Epic) {
               rank = 1;
               generateAbility = GenerateAbilityEpic();
            }
            break;
         case 1:
            generateAbility = GenerateAbilityEpic();
            int randomGradex = Randomizer.nextInt(100000);
            if (randomGradex < gradeUpEpic2Unique) {
               rank = 2;
               generateAbility = GenerateAbilityUnique(UniqueSecondAbilityEpic, UniqueThirdAbilityEpic);
            }
            break;
         case 2:
            generateAbility = GenerateAbilityUnique(UniqueSecondAbilityEpic, UniqueThirdAbilityEpic);
            int randomGrade = Randomizer.nextInt(100000);
            if (randomGrade < gradeUpUnique2Legendary) {
               rank = 3;
               generateAbility = GenerateAbilityLegendary(
                  legendarySecondAbilityUnique, legendarySecondAbilityEpic, legendaryThirdAbilityUnique, legendaryThirdAbilityEpic
               );
            }
            break;
         case 3:
            generateAbility = GenerateAbilityLegendary(
               legendarySecondAbilityUnique, legendarySecondAbilityEpic, legendaryThirdAbilityUnique, legendaryThirdAbilityEpic
            );
      }

      for (int i = 0; i < 3; i++) {
         if (i == 0) {
            while (DuplicateCheck(dup, generateAbility.abi.getLeft())) {
               Triple<Integer, Integer, Integer> abi = null;
               switch (rank) {
                  case 0:
                     abi = RareAbility.get(Randomizer.nextInt(RareAbility.size()));
                     break;
                  case 1:
                     abi = EpicAbility.get(Randomizer.nextInt(EpicAbility.size()));
                     break;
                  case 2:
                     abi = UniqueAbility.get(Randomizer.nextInt(UniqueAbility.size()));
                     break;
                  case 3:
                     abi = LegendaryAbility.get(Randomizer.nextInt(LegendaryAbility.size()));
               }

               generateAbility.setAbi(abi);
            }

            abilitys.add(new Triple<>(generateAbility.abi.getLeft(), Randomizer.rand(generateAbility.abi.getMid(), generateAbility.abi.getRight()), rank));
            dup.add(generateAbility.abi.getLeft());
         } else if (i == 1) {
            generateAbility.setAbi(generateAbility.secondAbility.get(Randomizer.nextInt(generateAbility.secondAbility.size())));

            while (DuplicateCheck(dup, generateAbility.abi.getLeft())) {
               generateAbility.setAbi(generateAbility.secondAbility.get(Randomizer.nextInt(generateAbility.secondAbility.size())));
            }

            abilitys.add(
               new Triple<>(
                  generateAbility.abi.getLeft(), Randomizer.rand(generateAbility.abi.getMid(), generateAbility.abi.getRight()), generateAbility.secondRank
               )
            );
            dup.add(generateAbility.abi.getLeft());
         } else if (i == 2) {
            generateAbility.setAbi(generateAbility.thirdAbility.get(Randomizer.nextInt(generateAbility.thirdAbility.size())));

            while (DuplicateCheck(dup, generateAbility.abi.getLeft())) {
               generateAbility.setAbi(generateAbility.thirdAbility.get(Randomizer.nextInt(generateAbility.thirdAbility.size())));
            }

            abilitys.add(
               new Triple<>(
                  generateAbility.abi.getLeft(), Randomizer.rand(generateAbility.abi.getMid(), generateAbility.abi.getRight()), generateAbility.thirdRank
               )
            );
         }
      }

      User.getInnerSkills().clear();
      List<CharacterPotentialHolder> holders = new ArrayList<>();

      for (Triple<Integer, Integer, Integer> ability : abilitys) {
         holders.add(
            new CharacterPotentialHolder(
               ability.getLeft(),
               (byte)ability.getMid().intValue(),
               (byte)innerAbilityInfos.get(ability.getLeft()).maxLevel,
               (byte)ability.getRight().intValue(),
               false
            )
         );
      }

      for (CharacterPotentialHolder cph : holders) {
         User.getInnerSkills().add(cph);
      }

      User.setSaveFlag(User.getSaveFlag() | CharacterSaveFlag.INNER_SKILL.getFlag());

      for (int ix = 0; ix < holders.size(); ix++) {
         User.send(CField.updateInnerPotential((byte)(ix + 1), holders.get(ix).getSkillId(), holders.get(ix).getSkillLevel(), holders.get(ix).getRank()));
         AchievementFactory.checkAbility(User, holders.get(ix).getSkillId(), holders.get(ix).getSkillLevel(), holders.get(ix).getRank(), 0);
      }

      User.send(CWvsContext.showPopupMessage("어빌리티 재설정에 성공 하였습니다."));
      User.getStat().recalcLocalStats(User);
   }

   public static void gachaAbilityChaos(MapleCharacter User) {
      List<Triple<Integer, Integer, Integer>> abilitys = new ArrayList<>();
      int rank = 0;
      int level = 0;

      for (int i = 0; i < 3; i++) {
         int var7 = User.getInnerSkills().get(i).getRank();
         if (var7 == 3) {
            for (Triple<Integer, Integer, Integer> abil : LegendaryAbility) {
               if (abil.left == User.getInnerSkills().get(i).getSkillId()) {
                  level = Randomizer.rand(abil.mid, abil.right);
                  break;
               }
            }
         } else if (var7 == 2) {
            for (Triple<Integer, Integer, Integer> abilx : UniqueAbility) {
               if (abilx.left == User.getInnerSkills().get(i).getSkillId()) {
                  level = Randomizer.rand(abilx.mid, abilx.right);
                  break;
               }
            }
         } else if (var7 == 1) {
            for (Triple<Integer, Integer, Integer> abilxx : EpicAbility) {
               if (abilxx.left == User.getInnerSkills().get(i).getSkillId()) {
                  level = Randomizer.rand(abilxx.mid, abilxx.right);
                  break;
               }
            }
         } else {
            for (Triple<Integer, Integer, Integer> abilxxx : RareAbility) {
               if (abilxxx.left == User.getInnerSkills().get(i).getSkillId()) {
                  level = Randomizer.rand(abilxxx.mid, abilxxx.right);
                  break;
               }
            }
         }

         abilitys.add(new Triple<>(User.getInnerSkills().get(i).getSkillId(), level, Integer.valueOf(var7)));
      }

      User.getInnerSkills().clear();
      List<CharacterPotentialHolder> holders = new ArrayList<>();

      for (Triple<Integer, Integer, Integer> ability : abilitys) {
         holders.add(
            new CharacterPotentialHolder(
               ability.getLeft(),
               (byte)ability.getMid().intValue(),
               (byte)innerAbilityInfos.get(ability.getLeft()).maxLevel,
               (byte)ability.getRight().intValue(),
               false
            )
         );
      }

      for (CharacterPotentialHolder cph : holders) {
         User.getInnerSkills().add(cph);
      }

      User.setSaveFlag(User.getSaveFlag() | CharacterSaveFlag.INNER_SKILL.getFlag());

      for (int i = 0; i < holders.size(); i++) {
         User.send(CField.updateInnerPotential((byte)(i + 1), holders.get(i).getSkillId(), holders.get(i).getSkillLevel(), holders.get(i).getRank()));
         AchievementFactory.checkAbility(User, holders.get(i).getSkillId(), holders.get(i).getSkillLevel(), holders.get(i).getRank(), 0);
      }

      User.send(CWvsContext.showPopupMessage("어빌리티 재설정에 성공 하였습니다."));
      User.getStat().recalcLocalStats(User);
   }

   public static void gachaAbilityBlack(MapleCharacter User, int itemID, int itemSlot) {
      List<Triple<Integer, Integer, Integer>> abilitys = new ArrayList<>();
      int rank = 0;
      int level = 0;

      for (int i = 0; i < 3; i++) {
         int var9 = User.getInnerSkills().get(i).getRank();
         if (var9 == 3) {
            for (Triple<Integer, Integer, Integer> abil : LegendaryAbility) {
               if (abil.left == User.getInnerSkills().get(i).getSkillId()) {
                  level = Randomizer.rand(abil.mid, abil.right);
                  break;
               }
            }
         } else if (var9 == 2) {
            for (Triple<Integer, Integer, Integer> abilx : UniqueAbility) {
               if (abilx.left == User.getInnerSkills().get(i).getSkillId()) {
                  level = Randomizer.rand(abilx.mid, abilx.right);
                  break;
               }
            }
         } else if (var9 == 1) {
            for (Triple<Integer, Integer, Integer> abilxx : EpicAbility) {
               if (abilxx.left == User.getInnerSkills().get(i).getSkillId()) {
                  level = Randomizer.rand(abilxx.mid, abilxx.right);
                  break;
               }
            }
         } else {
            for (Triple<Integer, Integer, Integer> abilxxx : RareAbility) {
               if (abilxxx.left == User.getInnerSkills().get(i).getSkillId()) {
                  level = Randomizer.rand(abilxxx.mid, abilxxx.right);
                  break;
               }
            }
         }

         abilitys.add(new Triple<>(User.getInnerSkills().get(i).getSkillId(), level, Integer.valueOf(var9)));
      }

      User.blackAbilitys = abilitys;
      User.send(Black_CirculatorPacket(abilitys, itemID, itemSlot));
   }

   public static byte[] Black_CirculatorPacket(List<Triple<Integer, Integer, Integer>> abilitys, int itemID, int itemSlot) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.BLACK_CIRCULATOR_UI.getValue());
      packet.writeInt(3);

      for (int i = 0; i < 3; i++) {
         packet.writeInt((Integer)abilitys.get(i).left);
         packet.write((Integer)abilitys.get(i).mid);
         packet.write(i + 1);
         packet.write((Integer)abilitys.get(i).right);
      }

      packet.writeInt(itemID);
      packet.writeInt(itemSlot);
      return packet.getPacket();
   }

   private static InnerAbility.GenerateAbility GenerateAbilityRare() {
      InnerAbility.GenerateAbility generateAbility = new InnerAbility.GenerateAbility();
      generateAbility.setAbi(RareAbility.get(Randomizer.nextInt(RareAbility.size())));
      generateAbility.setSecondAbility(RareAbility);
      generateAbility.setThirdAbility(RareAbility);
      generateAbility.setSecondRank(0);
      generateAbility.setThirdRank(0);
      return generateAbility;
   }

   private static InnerAbility.GenerateAbility GenerateAbilityEpic() {
      InnerAbility.GenerateAbility generateAbility = new InnerAbility.GenerateAbility();
      generateAbility.setAbi(EpicAbility.get(Randomizer.nextInt(EpicAbility.size())));
      generateAbility.setSecondAbility(RareAbility);
      generateAbility.setThirdAbility(RareAbility);
      generateAbility.setSecondRank(0);
      generateAbility.setThirdRank(0);
      return generateAbility;
   }

   private static InnerAbility.GenerateAbility GenerateAbilityUnique(int UniqueSecondAbilityEpic, int UniqueThirdAbilityEpic) {
      InnerAbility.GenerateAbility generateAbility = new InnerAbility.GenerateAbility();
      generateAbility.setAbi(UniqueAbility.get(Randomizer.nextInt(UniqueAbility.size())));
      int randomSecondAbility = Randomizer.nextInt(100000);
      if (randomSecondAbility < UniqueSecondAbilityEpic) {
         generateAbility.setSecondAbility(EpicAbility);
         generateAbility.setSecondRank(1);
      } else {
         generateAbility.setSecondAbility(RareAbility);
         generateAbility.setSecondRank(0);
      }

      int randomThirdAbility = Randomizer.nextInt(100000);
      if (randomThirdAbility < UniqueThirdAbilityEpic) {
         generateAbility.setThirdAbility(EpicAbility);
         generateAbility.setThirdRank(1);
      } else {
         generateAbility.setThirdAbility(RareAbility);
         generateAbility.setThirdRank(0);
      }

      return generateAbility;
   }

   private static InnerAbility.GenerateAbility GenerateAbilityLegendary(
      int legendarySecondAbilityUnique, int legendarySecondAbilityEpic, int legendaryThirdAbilityUnique, int legendaryThirdAbilityEpic
   ) {
      InnerAbility.GenerateAbility generateAbility = new InnerAbility.GenerateAbility();
      generateAbility.setAbi(LegendaryAbility.get(Randomizer.nextInt(LegendaryAbility.size())));
      int randomSecondAbility = Randomizer.nextInt(100000);
      if (randomSecondAbility >= legendarySecondAbilityUnique && !DBConfig.isGanglim) {
         if (randomSecondAbility < legendarySecondAbilityUnique + legendarySecondAbilityEpic) {
            generateAbility.setSecondAbility(EpicAbility);
            generateAbility.setSecondRank(1);
         } else {
            generateAbility.setSecondAbility(RareAbility);
            generateAbility.setSecondRank(0);
         }
      } else if (DBConfig.isGanglim) {
         generateAbility.setSecondAbility(LegendaryAbility);
         generateAbility.setSecondRank(3);
      } else {
         generateAbility.setSecondAbility(UniqueAbility);
         generateAbility.setSecondRank(2);
      }

      int randomThirdAbility = Randomizer.nextInt(100000);
      if (randomThirdAbility >= legendaryThirdAbilityUnique && !DBConfig.isGanglim) {
         if (randomThirdAbility < legendaryThirdAbilityUnique + legendaryThirdAbilityEpic) {
            generateAbility.setThirdAbility(EpicAbility);
            generateAbility.setThirdRank(1);
         } else {
            generateAbility.setThirdAbility(RareAbility);
            generateAbility.setThirdRank(0);
         }
      } else if (DBConfig.isGanglim) {
         generateAbility.setThirdAbility(LegendaryAbility);
         generateAbility.setThirdRank(3);
      } else {
         generateAbility.setThirdAbility(UniqueAbility);
         generateAbility.setThirdRank(2);
      }

      return generateAbility;
   }

   private static int GenerateMaxLevel(int rank, InnerAbility.GenerateAbility generateAbility) {
      int maxLevel = 0;
      if (rank == 0) {
         for (Pair<Triple<Integer, Integer, Integer>, Integer> rare : RareAbility()) {
            if (rare.getLeft().equals(generateAbility.abi.getLeft())) {
               maxLevel = rare.getLeft().getRight();
               break;
            }
         }
      } else if (rank == 1) {
         for (Pair<Triple<Integer, Integer, Integer>, Integer> rarex : EpicAbility()) {
            if (rarex.getLeft().equals(generateAbility.abi.getLeft())) {
               maxLevel = rarex.getLeft().getRight();
               break;
            }
         }
      } else if (rank == 2) {
         for (Pair<Triple<Integer, Integer, Integer>, Integer> rarexx : UniqueAbility()) {
            if (rarexx.getLeft().equals(generateAbility.abi.getLeft())) {
               maxLevel = rarexx.getLeft().getRight();
               break;
            }
         }
      } else if (rank == 3) {
         for (Pair<Triple<Integer, Integer, Integer>, Integer> rarexxx : LegendaryAbility()) {
            if (rarexxx.getLeft().equals(generateAbility.abi.getLeft())) {
               maxLevel = rarexxx.getLeft().getRight();
               break;
            }
         }
      }

      return maxLevel;
   }

   private static class GenerateAbility {
      Triple<Integer, Integer, Integer> abi = null;
      List<Triple<Integer, Integer, Integer>> secondAbility = new ArrayList<>();
      List<Triple<Integer, Integer, Integer>> thirdAbility = new ArrayList<>();
      int secondRank = 0;
      int thirdRank = 0;

      public GenerateAbility() {
      }

      public GenerateAbility(
         Triple<Integer, Integer, Integer> abi,
         List<Triple<Integer, Integer, Integer>> secondAbility,
         List<Triple<Integer, Integer, Integer>> thirdAbility,
         int secondRank,
         int thirdRank
      ) {
         this.abi = abi;
         this.secondAbility = secondAbility;
         this.thirdAbility = thirdAbility;
         this.secondRank = secondRank;
         this.thirdRank = thirdRank;
      }

      public void setAbi(Triple<Integer, Integer, Integer> abi) {
         this.abi = abi;
      }

      public void setSecondAbility(List<Triple<Integer, Integer, Integer>> secondAbility) {
         this.secondAbility = secondAbility;
      }

      public void setSecondRank(int secondRank) {
         this.secondRank = secondRank;
      }

      public void setThirdAbility(List<Triple<Integer, Integer, Integer>> thirdAbility) {
         this.thirdAbility = thirdAbility;
      }

      public void setThirdRank(int thirdRank) {
         this.thirdRank = thirdRank;
      }
   }

   public static class InnerAbilityInfo {
      private int maxLevel;
      private List<String> options;

      public InnerAbilityInfo(int maxLevel, List<String> options) {
         this.maxLevel = maxLevel;
         this.options = options;
      }

      public int getMaxLevel() {
         return this.maxLevel;
      }

      public void setMaxLevel(int maxLevel) {
         this.maxLevel = maxLevel;
      }

      public List<String> getOptions() {
         return this.options;
      }

      public void setOptions(List<String> options) {
         this.options = options;
      }
   }
}
