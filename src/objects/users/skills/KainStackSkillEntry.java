package objects.users.skills;

import network.encode.PacketEncoder;

public class KainStackSkillEntry {
   private int skillID;
   private int cycle;
   private int maxCharge;
   private int currentStack;

   public KainStackSkillEntry(int skillID, int cycle, int maxCharge) {
      this.setSkillID(skillID);
      this.setCycle(cycle);
      this.setMaxCharge(maxCharge);
      this.setCurrentStack(0);
   }

   public int getSkillID() {
      return this.skillID;
   }

   public void setSkillID(int skillID) {
      this.skillID = skillID;
   }

   public int getCycle() {
      return this.cycle;
   }

   public void setCycle(int cycle) {
      this.cycle = cycle;
   }

   public int getMaxCharge() {
      return this.maxCharge;
   }

   public void setMaxCharge(int maxCharge) {
      this.maxCharge = maxCharge;
   }

   public int getCurrentStack() {
      return this.currentStack;
   }

   public void setCurrentStack(int currentStack) {
      this.currentStack = currentStack;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.skillID);
      packet.writeInt(this.currentStack);
      packet.writeInt(this.maxCharge);
      packet.writeInt(this.cycle);
   }
}
