package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class WZEffect implements Effect {
   public static final int header = EffectHeader.WZEffect.getValue();
   private final int playerID;
   private final int rlType;
   private final int posX;
   private final int posY;
   private final String data;

   public WZEffect(int playerID, int rlType, int posX, int posY, String data) {
      this.playerID = playerID;
      this.rlType = rlType;
      this.posX = posX;
      this.posY = posY;
      this.data = data;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.write(this.rlType);
      packet.writeInt(this.posX);
      packet.writeInt(this.posY);
      packet.writeMapleAsciiString(this.data);
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
