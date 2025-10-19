package objects.fields.obstacle;

import network.encode.PacketEncoder;

public class ObstacleDiagonalInfo {
   private int effect;
   private int minAngle;
   private int maxAngle;
   private int createDuration;
   private int height;

   public ObstacleDiagonalInfo() {
   }

   public ObstacleDiagonalInfo(int effect, int minAngle, int maxAngle, int createDuration, int height) {
      this.effect = effect;
      this.minAngle = minAngle;
      this.maxAngle = maxAngle;
      this.createDuration = createDuration;
      this.height = height;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getEffect());
      packet.writeInt(this.getMinAngle());
      packet.writeInt(this.getMaxAngle());
      packet.writeInt(this.getCreateDuration());
      packet.writeInt(this.getHeight());
   }

   public int getEffect() {
      return this.effect;
   }

   public void setEffect(int effect) {
      this.effect = effect;
   }

   public int getMinAngle() {
      return this.minAngle;
   }

   public void setMinAngle(int minAngle) {
      this.minAngle = minAngle;
   }

   public int getMaxAngle() {
      return this.maxAngle;
   }

   public void setMaxAngle(int maxAngle) {
      this.maxAngle = maxAngle;
   }

   public int getCreateDuration() {
      return this.createDuration;
   }

   public void setCreateDuration(int createDuration) {
      this.createDuration = createDuration;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int height) {
      this.height = height;
   }
}
