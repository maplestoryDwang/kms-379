package objects.users.achievement.caching.info;

import java.util.ArrayList;
import java.util.List;
import objects.users.achievement.AchievementDifficulty;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class Info {
   private String name;
   private String desc;
   private AchievementDifficulty difficulty;
   private int score;
   private int parentId;
   private List<Integer> children = new ArrayList<>();

   public Info(MapleData root) {
      MapleData info = root.getChildByPath("info");
      this.setName(MapleDataTool.getString("name", info, ""));
      this.setDesc(MapleDataTool.getString("desc", info, ""));
      this.setDifficulty(AchievementDifficulty.getDifficulty(MapleDataTool.getString("difficulty", info, "normal")));
      this.setScore(MapleDataTool.getInt("score", info, 0));
      this.setParentId(MapleDataTool.getInt("parentId", info, -1));
      if (info != null) {
         MapleData children = info.getChildByPath("children");
         if (children != null) {
            int index = 0;

            while (true) {
               int value = MapleDataTool.getInt(String.valueOf(index++), children, -1);
               if (value == -1) {
                  break;
               }

               this.children.add(value);
            }
         }
      }
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDesc() {
      return this.desc;
   }

   public void setDesc(String desc) {
      this.desc = desc;
   }

   public AchievementDifficulty getDifficulty() {
      return this.difficulty;
   }

   public void setDifficulty(AchievementDifficulty difficulty) {
      this.difficulty = difficulty;
   }

   public int getScore() {
      return this.score;
   }

   public void setScore(int score) {
      this.score = score;
   }

   public int getParentId() {
      return this.parentId;
   }

   public void setParentId(int parentId) {
      this.parentId = parentId;
   }
}
