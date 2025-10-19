package objects.context.party;

public enum PartyOperation {
   Disband(PartyType.WithdrawParty),
   KickParty(PartyType.WithdrawParty),
   Withdraw(PartyType.WithdrawParty),
   Join(PartyType.JoinMember),
   SilentUpdate(PartyType.PartyDataUpdate),
   LogOnOff(PartyType.PartyDataUpdate),
   ChangeLeader(PartyType.ChangeLeader),
   ChangeLeaderDisconnect(PartyType.ChangeLeader),
   PartySetting(PartyType.PartySetting);

   private PartyType type;

   private PartyOperation(PartyType type) {
      this.type = type;
   }

   public PartyType getType() {
      return this.type;
   }
}
