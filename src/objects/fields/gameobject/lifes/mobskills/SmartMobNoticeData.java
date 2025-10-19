package objects.fields.gameobject.lifes.mobskills;

import database.DBConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import objects.utils.Pair;
import objects.utils.Properties;
import objects.utils.Table;

public class SmartMobNoticeData {
   public static Map<Integer, List<Pair<Integer, String>>> skills = new HashMap<>();
   public static Map<Integer, List<Pair<Integer, String>>> attacks = new HashMap<>();
   public static Map<Integer, List<Pair<Integer, String>>> aggros = new HashMap<>();

   public static void loadSmartMobNoticeData() {
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim" : "data/Jin", "SmartMobNoticeData.data");
      int count = 0;

      for (Table children : table.list()) {
         Table skill = children.getChild("Skill");
         List<Pair<Integer, String>> list = new ArrayList<>();

         for (Table child : skill.list()) {
            list.add(new Pair<>(Integer.parseInt(child.getName()), child.getProperty("0")));
         }

         skills.put(Integer.parseInt(children.getName()), list);
         Table attack = children.getChild("Attack");
         list = new ArrayList<>();

         for (Table child : attack.list()) {
            list.add(new Pair<>(Integer.parseInt(child.getName()), child.getProperty("0")));
         }

         attacks.put(Integer.parseInt(children.getName()), list);
         Table aggro = children.getChild("Aggro");
         List<Pair<Integer, String>> var11 = new ArrayList();

         for (Table child : aggro.list()) {
            var11.add(new Pair<>(Integer.parseInt(child.getName()), child.getProperty("0")));
         }

         aggros.put(Integer.parseInt(children.getName()), var11);
         count++;
      }

      System.out.println("총 " + count + "개의 SmartMobNoticeData를 불러왔습니다.");
   }
}
