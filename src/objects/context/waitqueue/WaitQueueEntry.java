package objects.context.waitqueue;

import network.encode.PacketEncoder;

public class WaitQueueEntry {
   private int playerID;
   private int waitingType;
   private int waitingQueueID;
   private WaitQueueType queueType = WaitQueueType.Success;
   private WaitQueueResult queueResult = WaitQueueResult.None;
   private WaitQueueError queueError = WaitQueueError.None;
   private int fieldID;

   public WaitQueueEntry(int playerID, int waitingType, int waitingQueueID, int fieldID) {
      this.playerID = playerID;
      this.waitingType = waitingType;
      this.waitingQueueID = waitingQueueID;
      this.fieldID = fieldID;
   }

   public void makeWaitQueueSuccess(WaitQueueResult result) {
      this.queueResult = result;
   }

   public void makeWaitQueueError(WaitQueueError error) {
      this.queueType = WaitQueueType.Error;
      this.queueError = error;
   }

   public void makeWaitQueueType(WaitQueueType type) {
      this.queueType = type;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getPlayerID());
      packet.write(this.getQueueType().getType());
      packet.write(this.getQueueResult().getType());
      packet.writeInt(this.getWaitingType());
      packet.writeInt(this.getWaitingQueueID());
      packet.writeInt(0);
      packet.writeInt(this.getQueueError().getType());
      packet.writeInt(0);
      packet.writeInt(this.getFieldID());
   }

   public int getPlayerID() {
      return this.playerID;
   }

   public void setPlayerID(int playerID) {
      this.playerID = playerID;
   }

   public int getWaitingType() {
      return this.waitingType;
   }

   public void setWaitingType(int waitingType) {
      this.waitingType = waitingType;
   }

   public int getWaitingQueueID() {
      return this.waitingQueueID;
   }

   public void setWaitingQueueID(int waitingQueueID) {
      this.waitingQueueID = waitingQueueID;
   }

   public WaitQueueType getQueueType() {
      return this.queueType;
   }

   public void setQueueType(WaitQueueType queueType) {
      this.queueType = queueType;
   }

   public WaitQueueResult getQueueResult() {
      return this.queueResult;
   }

   public void setQueueResult(WaitQueueResult queueResult) {
      this.queueResult = queueResult;
   }

   public WaitQueueError getQueueError() {
      return this.queueError;
   }

   public void setQueueError(WaitQueueError queueError) {
      this.queueError = queueError;
   }

   public int getFieldID() {
      return this.fieldID;
   }

   public void setFieldID(int fieldID) {
      this.fieldID = fieldID;
   }
}
