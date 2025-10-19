package objects.context.party;

public enum BossPartyRecruimentType {
   CancelRequestJoin(1),
   UpdateRequestJoinMember(8),
   DisplayBossPartyRecruimentList(10),
   OpenBossPartyRecruiment(11);

   private int type;

   private BossPartyRecruimentType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
