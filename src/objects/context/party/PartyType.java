package objects.context.party;

public enum PartyType {
   InviteParty(3),
   InviteBreakIn(4),
   RequestJoinParty(7),
   PartyDataUpdate(27),
   CreateParty(28),
   WithdrawParty(30),
   JoinMember(32),
   JoinCompleteMessage(33),
   ChangeLeader(52),
   PartySetting(63),
   UpdateBossPartyRecruiment(65),
   BossPartyRecruimentMessage(70),
   CancelJoinRequest(71),
   BossPartyRecruimentResult(74),
   RequestJoinPartyFromRecruiment(75),
   TownPortal(78);

   private int type;

   private PartyType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
