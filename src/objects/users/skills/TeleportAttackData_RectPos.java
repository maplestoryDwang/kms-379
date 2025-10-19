package objects.users.skills;

import java.awt.Point;
import java.awt.Rectangle;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;

public class TeleportAttackData_RectPos extends TeleportAttackData {
   private Rectangle rect;
   private Point pos;
   private int value;

   public TeleportAttackData_RectPos(PacketDecoder packet) {
      this.decode(packet);
   }

   @Override
   public void decode(PacketDecoder packet) {
      this.rect = new Rectangle(packet.readInt(), packet.readInt(), packet.readInt(), packet.readInt());
      this.pos = new Point(packet.readInt(), packet.readInt());
      this.value = packet.readInt();
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.encodeRect(this.rect);
      packet.encodePos(this.pos);
      packet.writeInt(this.value);
   }

   public Rectangle getRect() {
      return this.rect;
   }

   public Point getPos() {
      return this.pos;
   }
}
