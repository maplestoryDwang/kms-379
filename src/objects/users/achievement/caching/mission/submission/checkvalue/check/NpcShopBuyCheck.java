package objects.users.achievement.caching.mission.submission.checkvalue.check;

import objects.users.achievement.caching.mission.submission.checkvalue.CheckValue;
import objects.users.achievement.caching.mission.submission.checkvalue.ItemCheck;
import objects.users.achievement.caching.mission.submission.checkvalue.NpcID;

public class NpcShopBuyCheck extends AchievementMissionCheck {
   int npcID;
   int itemID;

   public NpcShopBuyCheck(int npcID, int itemID) {
      this.npcID = npcID;
      this.itemID = itemID;
   }

   @Override
   public boolean check(CheckValue checkValue) {
      NpcID npcID = checkValue.getNpcID();
      boolean npcIDCheck = true;
      if (npcID != null) {
         npcIDCheck = npcID.check(this.npcID);
      }

      ItemCheck item = checkValue.getItem();
      boolean itemCheck = true;
      if (item != null) {
         itemCheck = item.check(this.itemID);
      }

      return npcIDCheck && itemCheck;
   }
}
