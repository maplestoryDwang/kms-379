package objects.context.waitqueue;

public enum WaitQueueResult {
   None(0),
   RegisterWaiting(2),
   DeleteWaiting(5),
   CancelByParty(6),
   CancelByAnother(15),
   CancelByTimeOut(16),
   RequestFailed(18);

   private int type;

   private WaitQueueResult(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
