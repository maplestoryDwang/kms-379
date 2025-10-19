package objects.fields.child.blackmage;

import java.awt.Point;
import network.encode.PacketEncoder;

public class BlackMageLaser {
   private Point position;
   private int angle;
   private int createDelay;

   public BlackMageLaser(Point position, int angle, int createDelay) {
      this.position = position;
      this.angle = angle;
      this.createDelay = createDelay;
   }

   public Point getPosition() {
      return this.position;
   }

   public void setPosition(Point position) {
      this.position = position;
   }

   public int getAngle() {
      return this.angle;
   }

   public void setAngle(int angle) {
      this.angle = angle;
   }

   public int getCreateDelay() {
      return this.createDelay;
   }

   public void setCreateDelay(int createDelay) {
      this.createDelay = createDelay;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.position.x);
      packet.writeInt(this.position.y);
      packet.writeInt(this.angle);
      packet.writeInt(this.createDelay);
   }
}
