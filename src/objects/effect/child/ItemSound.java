package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class ItemSound implements Effect {
   public static final int header = EffectHeader.ItemSound.getValue();
   private final int playerID;
   private final int value;
   private final String data;

   public ItemSound(int playerID, int value, String data) {
      this.playerID = playerID;
      this.value = value;
      this.data = data;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.value);
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
