package objects.context.party;

public enum PartyRequestType {
   Unknown(-1),
   CreateParty(1),
   WithdrawParty(2),
   Invite(3),
   AcceptInvite(4),
   KickParty(5),
   ChangeLeader(6),
   AllowPartyRequest(7),
   ChangePartySetting(12),
   RegisterBossPartyRecruiment(15),
   RegisterBossPartyRecruimentInParty(16),
   CancelBossPartyRecruiment(18),
   ChangeBossPartyRecruimentSetting(19),
   JoinRequestFromBossPartyRecruiment(20),
   CancelJoinRequestFromBossPartyRecruiment(22),
   ProcessJoinRequestFromBossPartyRecruiment(23),
   BossPartyRecruimentList(24),
   OpenPartyRecruiment(25),
   PartyResult(41);

   private int type;

   private PartyRequestType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static PartyRequestType getType(int type) {
      for (PartyRequestType t : values()) {
         if (t.getType() == type) {
            return t;
         }
      }

      return null;
   }
}
