package objects.fields.gameobject.lifes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class EliteMonsterMan {
   private static final MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Skill.wz"));
   private static final HashMap<Integer, List<EliteMonsterSkill>> eliteMonsterSkills = new HashMap<>();

   public static void load() {
      MapleData eliteMobSkill = data.getData("EliteMobSkill.img");

      for (MapleData grade : eliteMobSkill.getChildren()) {
         List<EliteMonsterSkill> list = eliteMonsterSkills.computeIfAbsent(Integer.parseInt(grade.getName()), EliteMonsterMan::listSupplier);

         for (MapleData index : grade.getChildren()) {
            int skill = MapleDataTool.getInt("skill", index, 0);
            int level = MapleDataTool.getInt("level", index, 0);
            if (skill != 0 && level != 0) {
               int type = Integer.parseInt(index.getName());
               list.add(new EliteMonsterSkill(type, skill, level));
            }
         }
      }
   }

   public static EliteMonsterSkill getByGrade(int grade) {
      List<EliteMonsterSkill> list = eliteMonsterSkills.get(grade);
      return list != null ? Randomizer.next(list) : null;
   }

   public static Pair<EliteMonsterSkill, EliteMonsterSkill> getTwiceByGrade(int grade) {
      List<EliteMonsterSkill> list = eliteMonsterSkills.get(grade);
      if (list != null) {
         new ArrayList<>(list);
         Collections.shuffle(list);
         return new Pair<>(list.get(0), list.get(1));
      } else {
         return null;
      }
   }

   public static EliteMonsterRate getRandomRate(int level) {
      int grade = Randomizer.nextInt(3);
      if (level < 100) {
         switch (grade) {
            case 0:
               return new EliteMonsterRate(grade, 21.0);
            case 1:
               return new EliteMonsterRate(grade, 29.0);
            case 2:
               return new EliteMonsterRate(grade, 38.0);
         }
      } else if (level < 200) {
         switch (grade) {
            case 0:
               return new EliteMonsterRate(grade, 30.0);
            case 1:
               return new EliteMonsterRate(grade, 45.0);
            case 2:
               return new EliteMonsterRate(grade, 60.0);
         }
      } else {
         switch (grade) {
            case 0:
               return new EliteMonsterRate(grade, 35.0);
            case 1:
               return new EliteMonsterRate(grade, 52.5);
            case 2:
               return new EliteMonsterRate(grade, 70.0);
         }
      }

      return null;
   }

   private static List<EliteMonsterSkill> listSupplier(int key) {
      return new ArrayList<>();
   }
}
