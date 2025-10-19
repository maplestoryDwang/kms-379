package objects.fields.obstacle;

import network.encode.PacketEncoder;

public class ObstacleInRowInfo {
   private int effect;
   private boolean leftToRight;
   private int duration;
   private int i;
   private int startX;
   private int endX;

   public ObstacleInRowInfo() {
   }

   public ObstacleInRowInfo(int effect, boolean leftToRight, int duration, int i, int startX, int endX) {
      this.effect = effect;
      this.leftToRight = leftToRight;
      this.duration = duration;
      this.i = i;
      this.startX = startX;
      this.endX = endX;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getEffect());
      packet.write(this.isLeftToRight());
      packet.writeInt(this.getDuration());
      packet.writeInt(this.getI());
      packet.writeInt(this.getStartX());
      packet.writeInt(this.getEndX());
   }

   public int getEffect() {
      return this.effect;
   }

   public void setEffect(int effect) {
      this.effect = effect;
   }

   public boolean isLeftToRight() {
      return this.leftToRight;
   }

   public void setLeftToRight(boolean leftToRight) {
      this.leftToRight = leftToRight;
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   public int getI() {
      return this.i;
   }

   public void setI(int i) {
      this.i = i;
   }

   public int getStartX() {
      return this.startX;
   }

   public void setStartX(int startX) {
      this.startX = startX;
   }

   public int getEndX() {
      return this.endX;
   }

   public void setEndX(int endX) {
      this.endX = endX;
   }
}
