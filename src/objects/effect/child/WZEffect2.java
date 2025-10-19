package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class WZEffect2 implements Effect {
   public static final int header = EffectHeader.WZEffect2.getValue();
   private final int playerID;
   private final int posX;
   private final int posY;
   private final String data;

   public WZEffect2(int playerID, int posX, int posY, String data) {
      this.playerID = playerID;
      this.posX = posX;
      this.posY = posY;
      this.data = data;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeMapleAsciiString(this.data);
      packet.writeInt(this.posX);
      packet.writeInt(this.posY);
   }

   @Override
   public byte[] encodeForLocal() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_ON_EFFECT.getValue());
      packet.write(header);
      this.encode(packet);
      return packet.getPacket();
   }

   @Override
   public byte[] encodeForRemote() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_ON_EFFECT_REMOTE.getValue());
      packet.writeInt(this.playerID);
      packet.write(header);
      this.encode(packet);
      return packet.getPacket();
   }
}
