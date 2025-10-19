package objects.users.achievement.caching.mission.submission.checkvalue;

import objects.users.MapleTrait;

public class NCStatType {
   private MapleTrait.MapleTraitType traitType;

   public NCStatType(String ncStatType) {
      if (!ncStatType.isEmpty()) {
         this.traitType = MapleTrait.MapleTraitType.getByName(ncStatType);
      }
   }

   public boolean check(MapleTrait.MapleTraitType traitType) {
      return this.traitType == traitType;
   }
}
