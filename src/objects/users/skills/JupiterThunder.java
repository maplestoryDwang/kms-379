package objects.users.skills;

import network.encode.PacketEncoder;

public class JupiterThunder {
   private int objectID;
   private int skillID;
   private int x;
   private int y;
   private int delay;
   private int unk2;
   private int unk3;
   private int rlType;
   private int unk5;
   private int shockCount;
   private int subTime;
   private int time;

   public JupiterThunder(int objectID, int skillID, int x, int y, int delay, int unk2, int unk3, int rlType, int unk5, int shockCount, int subTime, int time) {
      this.setObjectID(objectID);
      this.setSkillID(skillID);
      this.setX(x);
      this.setY(y);
      this.setDelay(delay);
      this.setUnk2(unk2);
      this.setUnk3(unk3);
      this.setRLType(rlType);
      this.setUnk5(unk5);
      this.setShockCount(shockCount);
      this.setSubTime(subTime);
      this.setTime(time);
   }

   public int getSkillID() {
      return this.skillID;
   }

   public void setSkillID(int skillID) {
      this.skillID = skillID;
   }

   public int getX() {
      return this.x;
   }

   public void setX(int x) {
      this.x = x;
   }

   public int getY() {
      return this.y;
   }

   public void setY(int y) {
      this.y = y;
   }

   public int getDelay() {
      return this.delay;
   }

   public void setDelay(int unk1) {
      this.delay = unk1;
   }

   public int getUnk2() {
      return this.unk2;
   }

   public void setUnk2(int unk2) {
      this.unk2 = unk2;
   }

   public int getUnk3() {
      return this.unk3;
   }

   public void setUnk3(int unk3) {
      this.unk3 = unk3;
   }

   public int getRLType() {
      return this.rlType;
   }

   public void setRLType(int rlType) {
      this.rlType = rlType;
   }

   public int getUnk5() {
      return this.unk5;
   }

   public void setUnk5(int unk5) {
      this.unk5 = unk5;
   }

   public int getShockCount() {
      return this.shockCount;
   }

   public void setShockCount(int shockCount) {
      this.shockCount = shockCount;
   }

   public int getSubTime() {
      return this.subTime;
   }

   public void setSubTime(int subTime) {
      this.subTime = subTime;
   }

   public int getTime() {
      return this.time;
   }

   public void setTime(int time) {
      this.time = time;
   }

   public int getObjectID() {
      return this.objectID;
   }

   public void setObjectID(int objectID) {
      this.objectID = objectID;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getObjectID());
      packet.writeInt(1);
      packet.write(true);
      packet.writeInt(1);
      packet.writeInt(1);
      packet.writeInt(this.getObjectID());
      packet.writeInt(this.getX());
      packet.writeInt(this.getY());
      packet.writeInt(this.getRLType());
      packet.writeInt(this.getUnk5());
      packet.writeInt(this.getSkillID());
      packet.writeInt(this.getShockCount());
      packet.writeInt(this.getSubTime());
      packet.writeInt(this.getTime());
      packet.writeInt(this.getDelay());
      packet.writeInt(this.getUnk2());
      packet.writeInt(this.getUnk3());
   }
}
