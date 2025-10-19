package objects.users.achievement.caching.mission.submission.checkvalue;

import java.util.ArrayList;
import java.util.List;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class QuestChangeInfo {
   public static List<Integer> allQuestList = new ArrayList<>();
   private int questID;
   private int questStateMin;
   private int questStateMax;

   public QuestChangeInfo(MapleData root) {
      this.setQuestID(MapleDataTool.getInt("quest_id", root, 0));
      if (!allQuestList.contains(this.getQuestID())) {
         allQuestList.add(this.questID);
      }

      MapleData questState = root.getChildByPath("quest_state");
      if (questState != null) {
         this.setQuestStateMin(MapleDataTool.getInt("min", questState, 0));
         this.setQuestStateMax(MapleDataTool.getInt("max", questState, 0));
      }
   }

   public boolean check(int questID, int questState) {
      return this.questID != questID ? false : this.questStateMin == 0 || questState >= this.questStateMin && questState <= this.questStateMax;
   }

   public int getQuestID() {
      return this.questID;
   }

   public void setQuestID(int questID) {
      this.questID = questID;
   }

   public int getQuestStateMin() {
      return this.questStateMin;
   }

   public void setQuestStateMin(int questStateMin) {
      this.questStateMin = questStateMin;
   }

   public int getQuestStateMax() {
      return this.questStateMax;
   }

   public void setQuestStateMax(int questStateMax) {
      this.questStateMax = questStateMax;
   }
}
