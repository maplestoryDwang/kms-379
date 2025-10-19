package objects.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class StructSetItem {
   public byte completeCount;
   public int setItemID;
   public String setItemName;
   public Map<Integer, StructSetItem.SetItem> items = new LinkedHashMap<>();
   public List<Integer> itemIDs = new ArrayList<>();
   public List<Integer> itemParts = new ArrayList<>();
   public boolean jokerPossible;
   public boolean zeroWeaponJokerPossible;

   public Map<Integer, StructSetItem.SetItem> getItems() {
      return new LinkedHashMap<>(this.items);
   }

   public static class ActiveSkill {
      private Map<Integer, StructSetItem.ActiveSkill.ActiveSkillEntry> activeSkills = new HashMap<>();

      public ActiveSkill(MapleData data) {
         for (MapleData root : data.getChildren()) {
            int id = MapleDataTool.getInt("id", root, 0);
            int level = MapleDataTool.getInt("level", root, 0);
            this.activeSkills.put(Integer.parseInt(root.getName()), new StructSetItem.ActiveSkill.ActiveSkillEntry(id, level));
         }
      }

      public Map<Integer, StructSetItem.ActiveSkill.ActiveSkillEntry> getActiveSkills() {
         return this.activeSkills;
      }

      public static class ActiveSkillEntry {
         private int id;
         private int level;

         public ActiveSkillEntry(int id, int level) {
            this.id = id;
            this.level = level;
         }

         public int getId() {
            return this.id;
         }

         public int getLevel() {
            return this.level;
         }
      }
   }

   public static class SetItem {
      public int incPDD;
      public int incMDD;
      public int incSTR;
      public int incDEX;
      public int incINT;
      public int incLUK;
      public int incACC;
      public int incPAD;
      public int incMAD;
      public int incSpeed;
      public int incMHP;
      public int incMMP;
      public int incMHPr;
      public int incMMPr;
      public int incAllStat;
      public int option1;
      public int option2;
      public int option1Level;
      public int option2Level;
      public StructSetItem.ActiveSkill activeSkill;
   }
}
