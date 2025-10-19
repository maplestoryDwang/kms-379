package objects.fields.child.blackmage;

import java.awt.Point;
import network.encode.PacketEncoder;

public class BMFootHold {
   private Point position;
   private String footHoldName;

   public BMFootHold(Point position, String footHoldName) {
      this.position = position;
      this.footHoldName = footHoldName;
   }

   public Point getPosition() {
      return this.position;
   }

   public void setPosition(Point position) {
      this.position = position;
   }

   public String getFootHoldName() {
      return this.footHoldName;
   }

   public void setFootHoldName(String footHoldName) {
      this.footHoldName = footHoldName;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.position.x);
      packet.writeInt(this.position.y);
      packet.writeMapleAsciiString(this.footHoldName);
   }
}
