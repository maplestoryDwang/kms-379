package objects.context.party;

public enum BossPartyRecruimentMessageType {
   CompleteJoinRequest(0),
   DeclineJoinRequest(4);

   private int type;

   private BossPartyRecruimentMessageType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
