package objects.users.skills;

import network.encode.PacketEncoder;

public class TemporarySkill {
   private byte type;
   private int skillID;
   private byte skillLevel;
   private int continuousTime;
   private int continuationSkillID;
   private int linkSkillID;
   private boolean processKeyAtDisable;
   private int unk1 = 0;
   private int unk2 = 0;
   private int unk3 = 0;

   public TemporarySkill(byte type, int skillID, byte skillLevel, int continuousTime, int continuationSkillID, int linkSkillID) {
      this.type = type;
      this.skillID = skillID;
      this.skillLevel = skillLevel;
      this.continuousTime = continuousTime;
      this.continuationSkillID = continuationSkillID;
      this.linkSkillID = linkSkillID;
      this.processKeyAtDisable = false;
   }

   public byte getType() {
      return this.type;
   }

   public void setType(byte type) {
      this.type = type;
   }

   public int getSkillID() {
      return this.skillID;
   }

   public void setSkillID(int skillID) {
      this.skillID = skillID;
   }

   public byte getSkillLevel() {
      return this.skillLevel;
   }

   public void setSkillLevel(byte skillLevel) {
      this.skillLevel = skillLevel;
   }

   public int getContinuousTime() {
      return this.continuousTime;
   }

   public void setContinuousTime(int continuousTime) {
      this.continuousTime = continuousTime;
   }

   public int getContinuationSkillID() {
      return this.continuationSkillID;
   }

   public void setContinuationSkillID(int continuationSkillID) {
      this.continuationSkillID = continuationSkillID;
   }

   public int getLinkSkillID() {
      return this.linkSkillID;
   }

   public void setLinkSkillID(int linkSkillID) {
      this.linkSkillID = linkSkillID;
   }

   public boolean isProcessKeyAtDisable() {
      return this.processKeyAtDisable;
   }

   public void setProcessKeyAtDisable(boolean processKeyAtDisable) {
      this.processKeyAtDisable = processKeyAtDisable;
   }

   public int getUnk1() {
      return this.unk1;
   }

   public void setUnk1(int unk1) {
      this.unk1 = unk1;
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

   public void encode(PacketEncoder packet) {
      packet.write(this.getType());
      packet.writeInt(this.getSkillID());
      packet.write(this.getSkillLevel());
      packet.writeInt(this.getContinuousTime());
      packet.writeInt(this.getContinuationSkillID());
      packet.writeInt(this.getLinkSkillID());
      packet.writeInt(this.getUnk1());
      packet.writeInt(this.getUnk2());
      packet.writeInt(this.getUnk3());
      packet.write(this.isProcessKeyAtDisable());
      packet.write(false);
   }
}
