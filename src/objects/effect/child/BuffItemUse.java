package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class BuffItemUse implements Effect {
   public static final int header = EffectHeader.BuffItemUse.getValue();
   private final int playerID;
   private final int itemID;

   public BuffItemUse(int playerID, int itemID) {
      this.playerID = playerID;
      this.itemID = itemID;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.itemID);
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
