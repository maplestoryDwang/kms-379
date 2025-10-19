package objects.context.party;

public enum ProcessJoinRequestType {
   DeclineRequest(4),
   AcceptRequest(5);

   private int type;

   private ProcessJoinRequestType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static ProcessJoinRequestType getType(int type) {
      for (ProcessJoinRequestType t : values()) {
         if (t.getType() == type) {
            return t;
         }
      }

      return null;
   }
}
