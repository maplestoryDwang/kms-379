package objects.context.waitqueue;

public enum WaitQueueType {
   Start(10),
   Success(11),
   Error(12),
   Done(13),
   Cancel(14),
   CancelByAnother(15),
   CancelByTime(16),
   UnknownServer(17),
   Ask(18),
   End(19),
   UserCount(20),
   CanceledUserCount(21),
   AcceptButTimeOut(22),
   FieldSetFull(23),
   ServerIsBusy(24),
   Refresh(25);

   private int type;

   private WaitQueueType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
