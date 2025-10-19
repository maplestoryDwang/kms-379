package objects.context.party;

public enum CancelJoinRequestType {
   CancelJoinRequest(6);

   private int type;

   private CancelJoinRequestType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
