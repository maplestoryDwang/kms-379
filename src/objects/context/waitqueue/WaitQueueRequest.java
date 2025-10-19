package objects.context.waitqueue;

public enum WaitQueueRequest {
   Start(0),
   None(1),
   AddUser(2),
   AddUserOnlyOne(3),
   AddUrusParty(4),
   DelUser(5),
   DelUserByParty(6),
   AcceptCompleteUser(7),
   CancelCompleteUser(8),
   End(9);

   private int type;

   private WaitQueueRequest(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static WaitQueueRequest get(int type) {
      for (WaitQueueRequest request : values()) {
         if (request.getType() == type) {
            return request;
         }
      }

      return null;
   }
}
