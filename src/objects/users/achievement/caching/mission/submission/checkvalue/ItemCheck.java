package objects.users.achievement.caching.mission.submission.checkvalue;

import java.util.ArrayList;
import java.util.List;
import objects.item.Equip;
import objects.item.Item;
import objects.users.achievement.AchievementConditionType;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class ItemCheck {
   public static List<Integer> allItemList = new ArrayList<>();
   AchievementConditionType condition;
   int itemID;
   List<Integer> items = new ArrayList<>();
   List<ItemCheck.ItemPotentialGrade> itemPotentialGradeList = new ArrayList<>();
   ItemCheck.ItemPotentialGrade potentialGrade;

   public ItemCheck(MapleData root) {
      this.condition = AchievementConditionType.getType(MapleDataTool.getString("condition", root, "or"));
      if (this.condition != AchievementConditionType.or) {
      }

      MapleData values = root.getChildByPath("values");
      if (values != null) {
         for (MapleData data : values) {
            MapleData itemID = data.getChildByPath("item_id");
            if (itemID != null) {
               int value = MapleDataTool.getInt(itemID, -1);
               this.items.add(value);
               if (value != -1 && !allItemList.contains(value)) {
                  allItemList.add(value);
               }
            }

            MapleData itemPotential = data.getChildByPath("item_potential");
            if (itemPotential != null) {
               MapleData itemPotentialGrade = itemPotential.getChildByPath("item_potential_grade");
               if (itemPotentialGrade != null) {
                  int min = MapleDataTool.getInt(itemPotentialGrade.getChildByPath("min"), 0);
                  int max = MapleDataTool.getInt(itemPotentialGrade.getChildByPath("max"), Integer.MAX_VALUE);
                  this.itemPotentialGradeList.add(new ItemCheck.ItemPotentialGrade(min, max));
               }
            }
         }
      }

      MapleData itemIDx = root.getChildByPath("item_id");
      if (itemIDx != null) {
         this.itemID = MapleDataTool.getInt(itemIDx, -1);
         if (this.itemID != -1 && !allItemList.contains(this.itemID)) {
            allItemList.add(this.itemID);
         }
      }

      MapleData itemPotential = root.getChildByPath("item_potential");
      if (itemPotential != null) {
         MapleData itemPotentialGrade = itemPotential.getChildByPath("item_potential_grade");
         if (itemPotentialGrade != null) {
            int min = MapleDataTool.getInt(itemPotentialGrade.getChildByPath("min"), 0);
            int max = MapleDataTool.getInt(itemPotentialGrade.getChildByPath("max"), Integer.MAX_VALUE);
            this.potentialGrade = new ItemCheck.ItemPotentialGrade(min, max);
         }
      }
   }

   public boolean check(int itemID) {
      switch (this.condition) {
         case or:
            if (this.items.contains(itemID)) {
               return true;
            } else if (this.itemID == itemID) {
               return true;
            }
         default:
            return false;
      }
   }

   public boolean check(List<Item> itemArray) {
      switch (this.condition) {
         case or:
            boolean ret = false;

            for (Item itemA : itemArray) {
               if (this.items.contains(itemA.getItemId())) {
                  ret = true;
               }

               if (this.items.contains(171) && itemA.getItemId() / 10000 == 171 && itemA.getItemId() < 1713000) {
                  return true;
               }

               if (this.itemID == itemA.getItemId()) {
                  ret = true;
               }

               if (this.itemID == 1 && itemA instanceof Equip) {
                  ret = true;
               }
            }

            if (!ret && this.potentialGrade != null) {
               for (Item itemA : itemArray) {
                  if (itemA instanceof Equip) {
                     int grade = ((Equip)itemA).getPotential1() / 10000;
                     if (grade >= this.potentialGrade.getItemPotentialGradeMin() && grade <= this.potentialGrade.getItemPotentialGradeMax()) {
                        ret = true;
                     }
                  }
               }
            }

            return ret;
         case and:
            if (itemArray.isEmpty()) {
               return false;
            } else if (itemArray.size() < 2) {
               return false;
            } else if (this.itemPotentialGradeList.size() < 2) {
               return false;
            } else if (itemArray.get(0) instanceof Equip && itemArray.get(1) instanceof Equip) {
               Equip equip0 = (Equip)itemArray.get(0);
               Equip equip1 = (Equip)itemArray.get(1);
               int equip0grade = equip0.getPotential1() / 10000;
               int equip1grade = equip1.getPotential1() / 10000;
               if (equip0grade >= this.itemPotentialGradeList.get(0).getItemPotentialGradeMin()
                  && equip0grade <= this.itemPotentialGradeList.get(0).getItemPotentialGradeMax()) {
                  if (equip1grade >= this.itemPotentialGradeList.get(1).getItemPotentialGradeMin()
                     && equip1grade <= this.itemPotentialGradeList.get(1).getItemPotentialGradeMax()) {
                     return true;
                  }

                  return false;
               }

               return false;
            }
         default:
            return false;
      }
   }

   private class ItemPotentialGrade {
      private int itemPotentialGradeMin;
      private int itemPotentialGradeMax;

      public ItemPotentialGrade(int itemPotentialGradeMin, int itemPotentialGradeMax) {
         this.itemPotentialGradeMin = itemPotentialGradeMin;
         this.itemPotentialGradeMax = itemPotentialGradeMax;
      }

      public int getItemPotentialGradeMin() {
         return this.itemPotentialGradeMin;
      }

      public int getItemPotentialGradeMax() {
         return this.itemPotentialGradeMax;
      }
   }
}
