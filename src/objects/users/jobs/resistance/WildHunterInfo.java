package objects.users.jobs.resistance;

import network.encode.PacketEncoder;

public class WildHunterInfo {
   private int index;
   private int ridingType;
   private int[] capturedMob = new int[5];

   public WildHunterInfo() {
      this.index = 0;
   }

   public int getRidingType() {
      return this.ridingType;
   }

   public void setRidingType(int ridingType) {
      this.ridingType = ridingType;
   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public void setCapturedMob(int index, int mobID) {
      this.capturedMob[index] = mobID;
   }

   public void setCapturedMob(int[] capturedMob) {
      this.capturedMob = capturedMob;
   }

   public int getCapturedMob(int index) {
      return this.capturedMob[index];
   }

   public void encode(PacketEncoder packet) {
      packet.write(this.ridingType * 10 + this.getIndex());

      for (int i = 0; i < this.capturedMob.length; i++) {
         packet.writeInt(this.capturedMob[i]);
      }
   }

   public int getNextCapturedPos() {
      for (int i = 0; i < 5; i++) {
         if (this.capturedMob[i] == 0) {
            return i;
         }
      }

      int index = (this.index + 1) % 5;
      this.index = index;
      return index;
   }
}
