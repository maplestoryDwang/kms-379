package constants.devtempConstants;

public enum MapleStatusInfo {
   GAINMESO(0),
   updateQuestMobKills(1),
   PlatformerStageInfo(1),
   itemExpired(2),
   GainEXP(3),
   getSPMsg(4),
   ShowFameGain(5),
   getGPMsg(7),
   getCommitment(8),
   StatusMsg(9),
   brownMessage(11),
   AcceptQuest(11),
   updateInfoQuest(13),
   PandoraBox(13),
   showPetSkills(13),
   updateSuddenQuest(13),
   updateClientInfoQuest(14),
   showItemReplaceMessage(16),
   showTraitGain(19),
   showTraitMaxed(19),
   getBPMsg(20),
   showReturnStone(27),
   dailyGift(31),
   getWpGain(33),
   comboKill(35),
   multiKill(35),
   monstercollertion(39),
   setMobCollectionOnFirst(41),
   setMobOnCollection(42);

   private final int i;

   private MapleStatusInfo(int i) {
      this.i = i;
   }

   public int getType() {
      return this.i;
   }

   public static final MapleStatusInfo getByValue(int value) {
      for (MapleStatusInfo enums : values()) {
         if (enums.i == value) {
            return enums;
         }
      }

      return null;
   }
}
