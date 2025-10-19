package objects.effect;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;

public class NormalEffect implements Effect {
   public int header;
   private final int playerID;

   public NormalEffect(int playerID, EffectHeader header) {
      this.playerID = playerID;
      this.header = header.getValue();
   }

   @Override
   public void encode(PacketEncoder packet) {
   }

   @Override
   public byte[] encodeForLocal() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_ON_EFFECT.getValue());
      packet.write(this.header);
      this.encode(packet);
      return packet.getPacket();
   }

   @Override
   public byte[] encodeForRemote() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_ON_EFFECT_REMOTE.getValue());
      packet.writeInt(this.playerID);
      packet.write(this.header);
      this.encode(packet);
      return packet.getPacket();
   }
}
