package objects.fields.obstacle;

import network.encode.PacketEncoder;
import objects.utils.Randomizer;

public class ObstacleRadialInfo {
   private int effect;
   private int rx;
   private int ry;
   private int delay;
   private int height;

   public ObstacleRadialInfo() {
   }

   public ObstacleRadialInfo(int effect, int rx, int ry, int minDelay, int maxDelay, int height) {
      this.effect = effect;
      this.rx = rx;
      this.ry = ry;
      this.delay = Randomizer.rand(minDelay, maxDelay);
      this.height = height;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getEffect());
      packet.writeInt(this.getRx());
      packet.writeInt(this.getRy());
      packet.writeInt(this.getDelay());
      packet.writeInt(this.getHeight());
   }

   public int getEffect() {
      return this.effect;
   }

   public void setEffect(int effect) {
      this.effect = effect;
   }

   public int getRx() {
      return this.rx;
   }

   public void setRx(int rx) {
      this.rx = rx;
   }

   public int getRy() {
      return this.ry;
   }

   public void setRy(int ry) {
      this.ry = ry;
   }

   public int getDelay() {
      return this.delay;
   }

   public void setDelay(int delay) {
      this.delay = delay;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int height) {
      this.height = height;
   }
}
