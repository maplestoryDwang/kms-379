package objects.users.skills;

import network.encode.PacketEncoder;

public class VMatrixSlot {
   private int equippedCore = -1;
   private int index = 0;
   private int released = 0;
   private int slotEnforcement = 0;

   public int getEquippedCore() {
      return this.equippedCore;
   }

   public void setEquippedCore(int equippedCore) {
      this.equippedCore = equippedCore;
   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public int getReleased() {
      return this.released;
   }

   public void setReleased(int released) {
      this.released = released;
   }

   public int getSlotEnforcement() {
      return this.slotEnforcement;
   }

   public void setSlotEnforcement(int slotEnforcement) {
      this.slotEnforcement = slotEnforcement;
   }

   public void encode(PacketEncoder packet, int equippedIdx) {
      packet.writeInt(equippedIdx);
      packet.writeInt(this.getIndex());
      packet.writeInt(this.getSlotEnforcement());
      packet.write(this.getReleased());
   }
}
