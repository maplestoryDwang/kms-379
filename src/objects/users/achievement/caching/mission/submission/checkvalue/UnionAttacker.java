package objects.users.achievement.caching.mission.submission.checkvalue;

import java.util.ArrayList;
import java.util.List;
import objects.fields.child.union.MapleUnion;
import objects.fields.child.union.MapleUnionEntry;
import objects.users.MapleCharacter;
import objects.users.achievement.AchievementConditionType;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class UnionAttacker {
   private AchievementConditionType condition;
   private List<Integer> characterJobCodeList;

   public UnionAttacker(MapleData root) {
      this.setCondition(AchievementConditionType.getType(MapleDataTool.getString("condition", root, "or")));
      this.setCharacterJobCodeList(new ArrayList<>());
      MapleData values = root.getChildByPath("values");
      if (values != null) {
         int index = 0;

         while (true) {
            MapleData data = values.getChildByPath(String.valueOf(index++));
            if (data == null) {
               break;
            }

            this.getCharacterJobCodeList().add(MapleDataTool.getInt("union_attacker_job", data, 0));
         }
      }
   }

   public boolean check(MapleCharacter player) {
      MapleUnion union = player.getMapleUnion();
      if (union == null) {
         return false;
      } else if (union.activeRaiders.isEmpty()) {
         return false;
      } else {
         switch (this.condition) {
            case or:
               for (MapleUnionEntry entryx : union.activeRaiders) {
                  if (this.characterJobCodeList.contains(entryx.job)) {
                     return true;
                  }
               }

               return false;
            case and:
               for (MapleUnionEntry entry : union.activeRaiders) {
                  if (!this.characterJobCodeList.contains(entry.job)) {
                     return false;
                  }
               }

               return true;
            default:
               return false;
         }
      }
   }

   public AchievementConditionType getCondition() {
      return this.condition;
   }

   public void setCondition(AchievementConditionType condition) {
      this.condition = condition;
   }

   public List<Integer> getCharacterJobCodeList() {
      return this.characterJobCodeList;
   }

   public void setCharacterJobCodeList(List<Integer> characterJobCodeList) {
      this.characterJobCodeList = characterJobCodeList;
   }
}
