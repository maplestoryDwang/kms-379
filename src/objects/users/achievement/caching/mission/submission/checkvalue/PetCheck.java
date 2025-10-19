package objects.users.achievement.caching.mission.submission.checkvalue;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class PetCheck {
   private int petSatietyMin;
   private int petSatietyMax;

   public PetCheck(MapleData root) {
      MapleData petSatiety = root.getChildByPath("pet_satiety");
      if (petSatiety != null) {
         this.petSatietyMin = MapleDataTool.getInt(petSatiety.getChildByPath("min"), 0);
         this.petSatietyMax = MapleDataTool.getInt(petSatiety.getChildByPath("max"), Integer.MAX_VALUE);
      }
   }

   public boolean check(int s) {
      return s >= this.petSatietyMin && s <= this.petSatietyMax;
   }
}
