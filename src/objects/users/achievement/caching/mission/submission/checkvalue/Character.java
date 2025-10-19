package objects.users.achievement.caching.mission.submission.checkvalue;

import java.util.ArrayList;
import java.util.List;
import objects.users.MapleCharacter;
import objects.users.achievement.AchievementConditionType;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class Character {
   private AchievementConditionType condition;
   private List<Integer> jobCodeList;
   private int minLevel;
   private int maxLevel;
   private int characterBaseHpMin;
   private int characterBaseHpMax;

   public Character(MapleData root) {
      this.setCondition(AchievementConditionType.getType(MapleDataTool.getString("condition", root, "and")));
      this.setJobCodeList(new ArrayList<>());
      MapleData values = root.getChildByPath("values");
      if (values != null) {
         int index = 0;

         while (true) {
            MapleData d = values.getChildByPath(String.valueOf(index++));
            if (d == null) {
               break;
            }

            this.getJobCodeList().add(MapleDataTool.getInt("character_jobcode", d, 0));
         }
      } else {
         int value = MapleDataTool.getInt("character_jobcode", root, -1);
         if (value != -1) {
            this.getJobCodeList().add(value);
         }
      }

      MapleData characterLevel = root.getChildByPath("character_level");
      if (characterLevel != null) {
         this.setMinLevel(MapleDataTool.getInt("min", characterLevel, 0));
         this.setMaxLevel(MapleDataTool.getInt("max", characterLevel, 0));
      }

      MapleData baseHp = root.getChildByPath("character_base_hp");
      if (baseHp != null) {
         this.setCharacterBaseHpMin(MapleDataTool.getInt("min", baseHp, 0));
         this.setCharacterBaseHpMax(MapleDataTool.getInt("max", baseHp, 0));
      }
   }

   public boolean check(MapleCharacter player) {
      boolean check = true;
      switch (this.condition) {
         case or:
            check = false;
            if ((this.minLevel != 0 || this.maxLevel != 0) && player.getLevel() >= this.minLevel && player.getLevel() <= this.maxLevel) {
               check = true;
            }

            if (!this.getJobCodeList().isEmpty()) {
               Integer job = Integer.valueOf(player.getJob());
               if (this.getJobCodeList().contains(job)) {
                  check = true;
               }
            }

            if (this.getCharacterBaseHpMin() > 0
               && this.getCharacterBaseHpMin() <= player.getStat().getHp()
               && this.getCharacterBaseHpMax() >= player.getStat().getHp()) {
               check = true;
            }
            break;
         case and:
         default:
            if ((this.minLevel != 0 || this.maxLevel != 0) && (player.getLevel() < this.minLevel || player.getLevel() > this.maxLevel)) {
               check = false;
            }

            if (this.getCharacterBaseHpMin() > 0 && this.getCharacterBaseHpMin() > player.getStat().getHp()) {
               check = false;
            }

            if (this.getCharacterBaseHpMax() > 0 && this.getCharacterBaseHpMax() < player.getStat().getHp()) {
               check = false;
            }

            if (!this.getJobCodeList().isEmpty()) {
               Integer job = Integer.valueOf(player.getJob());
               if (!this.getJobCodeList().contains(job)) {
                  check = false;
               }
            }
      }

      return check;
   }

   public AchievementConditionType getCondition() {
      return this.condition;
   }

   public void setCondition(AchievementConditionType condition) {
      this.condition = condition;
   }

   public List<Integer> getJobCodeList() {
      return this.jobCodeList;
   }

   public void setJobCodeList(List<Integer> jobCodeList) {
      this.jobCodeList = jobCodeList;
   }

   public int getMinLevel() {
      return this.minLevel;
   }

   public void setMinLevel(int minLevel) {
      this.minLevel = minLevel;
   }

   public int getMaxLevel() {
      return this.maxLevel;
   }

   public void setMaxLevel(int maxLevel) {
      this.maxLevel = maxLevel;
   }

   public int getCharacterBaseHpMin() {
      return this.characterBaseHpMin;
   }

   public void setCharacterBaseHpMin(int characterBaseHpMin) {
      this.characterBaseHpMin = characterBaseHpMin;
   }

   public int getCharacterBaseHpMax() {
      return this.characterBaseHpMax;
   }

   public void setCharacterBaseHpMax(int characterBaseHpMax) {
      this.characterBaseHpMax = characterBaseHpMax;
   }
}
