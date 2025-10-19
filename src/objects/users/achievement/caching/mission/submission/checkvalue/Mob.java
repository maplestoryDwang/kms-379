package objects.users.achievement.caching.mission.submission.checkvalue;

import java.util.ArrayList;
import java.util.List;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.achievement.AchievementConditionType;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;
import objects.wz.provider.MapleDataType;

public class Mob {
   public static List<Integer> allMobList = new ArrayList<>();
   private List<Integer> mobList;
   private AchievementConditionType condition;
   private boolean mobIsElite;
   private int mobEliteLv;
   private boolean mobIsOptimum;

   public Mob(MapleData root) {
      this.setMobList(new ArrayList<>());
      MapleData mobList = root.getChildByPath("mob_id");
      if (mobList != null) {
         if (mobList.getType() == MapleDataType.INT) {
            int mobID = MapleDataTool.getInt("mob_id", root, 0);
            this.getMobList().add(mobID);
            if (!allMobList.contains(mobID)) {
               allMobList.add(mobID);
            }

            this.setCondition(AchievementConditionType.or);
         } else if (mobList.getType() == MapleDataType.PROPERTY) {
            int index = 0;

            while (true) {
               int value = MapleDataTool.getInt(String.valueOf(index++), mobList.getChildByPath("values"), -1);
               if (value == -1) {
                  this.setCondition(AchievementConditionType.getType(MapleDataTool.getString("condition", mobList, "or")));
                  break;
               }

               this.getMobList().add(value);
               if (!allMobList.contains(value)) {
                  allMobList.add(value);
               }
            }
         }
      }

      if (this.getCondition() == null) {
         this.setCondition(AchievementConditionType.or);
      }

      this.setMobIsElite(MapleDataTool.getInt("mob_is_elite", root, 0) == 1);
      this.setMobEliteLv(MapleDataTool.getInt("mob_elite_lv", root, 0));
      this.setMobIsOptimum(MapleDataTool.getInt("mob_is_optimum", root, 0) == 1);
   }

   public boolean check(MapleMonster mob, boolean optimum) {
      switch (this.condition) {
         case or:
            if (this.mobList.isEmpty()) {
               if (this.isMobIsOptimum()) {
                  if (!optimum) {
                     return false;
                  }

                  return true;
               }

               if (!this.isMobIsElite()) {
                  return false;
               }

               if (this.getMobEliteLv() != mob.getEliteMobType().getType()) {
                  return false;
               }
            }

            if (!this.mobList.contains(mob.getId())) {
               return false;
            }
         default:
            return true;
      }
   }

   public List<Integer> getMobList() {
      return this.mobList;
   }

   public void setMobList(List<Integer> mobList) {
      this.mobList = mobList;
   }

   public AchievementConditionType getCondition() {
      return this.condition;
   }

   public void setCondition(AchievementConditionType condition) {
      this.condition = condition;
   }

   public boolean isMobIsElite() {
      return this.mobIsElite;
   }

   public void setMobIsElite(boolean mobIsElite) {
      this.mobIsElite = mobIsElite;
   }

   public int getMobEliteLv() {
      return this.mobEliteLv;
   }

   public void setMobEliteLv(int mobEliteLv) {
      this.mobEliteLv = mobEliteLv;
   }

   public boolean isMobIsOptimum() {
      return this.mobIsOptimum;
   }

   public void setMobIsOptimum(boolean mobIsOptimum) {
      this.mobIsOptimum = mobIsOptimum;
   }
}
