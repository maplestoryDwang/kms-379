package objects.context.waitqueue;

public enum WaitQueueError {
   None(0),
   Unknown(1),
   ManyRequest(2),
   AlreadyParty(3),
   AlreadyReq(4),
   InvaildField(5),
   InvalidLevel(6),
   InvalidReq(7),
   InvalidContent(8),
   NotPartyBoss(9),
   TooManyPartyMember(10),
   PartyMemberNotSameChannel(11),
   TeleportLimitMap(12),
   CanAttachAdditionalProcess(13),
   OnlyOneButParty(14),
   OverDailyCount(15),
   OverDailyCountInParty(16),
   AlreadyInWaitingQueue(17);

   private int type;

   private WaitQueueError(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
