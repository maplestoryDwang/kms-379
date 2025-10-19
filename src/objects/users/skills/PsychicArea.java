package objects.users.skills;

import network.encode.PacketEncoder;

public class PsychicArea {
   private int action;
   private int speed;
   private int key;
   private int v6;
   private int skillId;
   private short skillLevel;
   private int durationTime;
   private boolean isLeft_second;
   private short skeletonFilePathIdx;
   private short skeletonAniIdx;
   private short skeletonLoop;
   private int posStartX;
   private int posStartY;

   public void encode(PacketEncoder packet) {
      packet.write(1);
      packet.writeInt(this.action);
      packet.writeInt(this.speed);
      packet.writeInt(this.v6);
      packet.writeInt(this.skillId);
      packet.writeShort(this.skillLevel);
      packet.writeInt(this.key);
      packet.writeInt(this.durationTime);
      packet.write(this.isLeft_second ? 1 : 0);
      packet.writeShort(this.skeletonFilePathIdx);
      packet.writeShort(this.skeletonAniIdx);
      packet.writeShort(this.skeletonLoop);
      packet.writeInt(this.posStartX);
      packet.writeInt(this.posStartY);
   }

   public void setAction(int action) {
      this.action = action;
   }

   public void setSpeed(int speed) {
      this.speed = speed;
   }

   public void setKey(int key) {
      this.key = key;
   }

   public void setV6(int v6) {
      this.v6 = v6;
   }

   public void setSkillId(int skillId) {
      this.skillId = skillId;
   }

   public int getSkillId() {
      return this.skillId;
   }

   public void setSkillLevel(short skillLevel) {
      this.skillLevel = skillLevel;
   }

   public void setDurationTIme(int durationTIme) {
      this.durationTime = durationTIme;
   }

   public void setIsLeft_second(boolean isLeft_second) {
      this.isLeft_second = isLeft_second;
   }

   public void setSkeletonFilePathIdx(short skeletonFilePathIdx) {
      this.skeletonFilePathIdx = skeletonFilePathIdx;
   }

   public void setSkeletonAniIdx(short skeletonAniIdx) {
      this.skeletonAniIdx = skeletonAniIdx;
   }

   public void setSkeletonLoop(short skeletonLoop) {
      this.skeletonLoop = skeletonLoop;
   }

   public void setPosStartX(int posStartX) {
      this.posStartX = posStartX;
   }

   public void setPosStartY(int posStartY) {
      this.posStartY = posStartY;
   }

   public int getKey() {
      return this.key;
   }
}
