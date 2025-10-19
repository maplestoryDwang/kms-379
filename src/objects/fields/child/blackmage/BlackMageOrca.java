package objects.fields.child.blackmage;

import java.awt.Point;
import network.encode.PacketEncoder;

public class BlackMageOrca {
   private int type;
   private int key;
   private Point position;

   public BlackMageOrca(int level, int key, Point position) {
      this.type = level;
      this.key = key;
      this.position = position;
   }

   public int getKey() {
      return this.key;
   }

   public void setKey(int key) {
      this.key = key;
   }

   public Point getPosition() {
      return this.position;
   }

   public void setPosition(Point position) {
      this.position = position;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.key);
      packet.writeInt(this.type);
      packet.writeInt(this.position.x);
      packet.writeInt(this.position.y);
      packet.write(0);
   }
}
