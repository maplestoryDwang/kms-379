package objects.fields.child.will;

import java.util.concurrent.atomic.AtomicInteger;
import network.encode.PacketEncoder;

public class SpiderWeb {
   private static AtomicInteger SN = new AtomicInteger(10000);
   private int objectID = SN.getAndAdd(1);
   private int pattern;
   private int x1;
   private int y1;

   public SpiderWeb(int pattern, int x1, int y1) {
      this.pattern = pattern;
      this.x1 = x1;
      this.y1 = y1;
   }

   public int getPattern() {
      return this.pattern;
   }

   public void setPattern(int pattern) {
      this.pattern = pattern;
   }

   public int getX1() {
      return this.x1;
   }

   public void setX1(int x1) {
      this.x1 = x1;
   }

   public int getY1() {
      return this.y1;
   }

   public void setY1(int y1) {
      this.y1 = y1;
   }

   public int getObjectID() {
      return this.objectID;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getObjectID());
      packet.writeInt(this.getPattern());
      packet.writeInt(this.getX1());
      packet.writeInt(this.getY1());
      switch (this.getPattern()) {
         case 0:
            packet.writeInt(100);
            packet.writeInt(100);
            break;
         case 1:
            packet.writeInt(160);
            packet.writeInt(160);
            break;
         case 2:
            packet.writeInt(270);
            packet.writeInt(270);
            break;
         default:
            packet.writeInt(0);
            packet.writeInt(0);
      }
   }
}
