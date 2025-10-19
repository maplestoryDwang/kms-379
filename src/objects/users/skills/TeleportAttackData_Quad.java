package objects.users.skills;

import network.decode.PacketDecoder;
import network.encode.PacketEncoder;

public class TeleportAttackData_Quad extends TeleportAttackData {
   public int data1;
   public int direction;
   public int x;
   public int y;
   private int objectID;

   public TeleportAttackData_Quad(PacketDecoder packet) {
      this.decode(packet);
   }

   @Override
   public void decode(PacketDecoder packet) {
      this.data1 = packet.readInt();
      this.x = packet.readInt();
      this.y = packet.readInt();
      this.direction = packet.readInt();
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.data1);
      packet.writeInt(this.x);
      packet.writeInt(this.y);
      packet.writeInt(this.direction);
   }

   public int getObjectID() {
      return this.objectID;
   }

   public void setObjectID(int objectID) {
      this.objectID = objectID;
   }
}
