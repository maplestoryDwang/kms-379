package objects.context.party;

public enum PartyResultType {
   Unknown(-1),
   InviteResult(0),
   DeclineInvite(4),
   AcceptInvite(5);

   private int type;

   private PartyResultType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static PartyResultType getType(int type) {
      for (PartyResultType t : values()) {
         if (t.getType() == type) {
            return t;
         }
      }

      return null;
   }
}
