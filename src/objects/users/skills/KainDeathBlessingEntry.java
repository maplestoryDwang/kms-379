package objects.users.skills;

import network.encode.PacketEncoder;

public class KainDeathBlessingEntry {
   private int targetObjectID;
   private int stack;
   private int unk;
   private int duration;
   private static int MAX_STACK = 15;

   public KainDeathBlessingEntry(int targetObjectID, int stack, int duration) {
      this.setTargetObjectID(targetObjectID);
      this.setStack(stack);
      this.setUnk(0);
      this.setDuration(duration);
   }

   public int getTargetObjectID() {
      return this.targetObjectID;
   }

   public void setTargetObjectID(int targetObjectID) {
      this.targetObjectID = targetObjectID;
   }

   public int getStack() {
      return this.stack;
   }

   public void setStack(int stack) {
      this.stack = stack;
   }

   public void incrementStack(boolean increaseMaxStack) {
      this.stack = Math.min(MAX_STACK + (increaseMaxStack ? 5 : 0), this.stack + 1);
   }

   public void decrementStack() {
      this.stack = Math.max(0, this.stack - 1);
   }

   public int getUnk() {
      return this.unk;
   }

   public void setUnk(int unk) {
      this.unk = unk;
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.targetObjectID);
      packet.writeInt(this.stack);
      packet.writeInt(this.unk);
      packet.writeInt(this.duration);
   }
}
