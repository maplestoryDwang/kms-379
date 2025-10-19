package objects.fields.child.sernium;

import network.encode.PacketEncoder;

public class SerenLaser {
   private int xPos;
   private int yPos;
   private int angleInfo;
   private int angleInfo2;

   public SerenLaser(int xPos, int yPos, int angleInfo, int angleInfo2) {
      this.setxPos(xPos);
      this.setyPos(yPos);
      this.setAngleInfo(angleInfo);
      this.setAngleInfo2(angleInfo2);
   }

   public int getxPos() {
      return this.xPos;
   }

   public void setxPos(int xPos) {
      this.xPos = xPos;
   }

   public int getyPos() {
      return this.yPos;
   }

   public void setyPos(int yPos) {
      this.yPos = yPos;
   }

   public int getAngleInfo() {
      return this.angleInfo;
   }

   public void setAngleInfo(int angleInfo) {
      this.angleInfo = angleInfo;
   }

   public int getAngleInfo2() {
      return this.angleInfo2;
   }

   public void setAngleInfo2(int angleInfo2) {
      this.angleInfo2 = angleInfo2;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getxPos());
      packet.writeInt(this.getyPos());
      packet.writeInt(this.getAngleInfo());
      packet.writeInt(this.getAngleInfo2());
   }
}
