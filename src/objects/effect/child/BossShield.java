package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class BossShield implements Effect {
   public static final int header = EffectHeader.HitBossShield.getValue();
   private final int playerID;
   private final int itemID;
   private final int itemCount;

   public BossShield(int playerID, int itemID, int itemCount) {
      this.playerID = playerID;
      this.itemID = itemID;
      this.itemCount = itemCount;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.writeInt(this.itemID);
      packet.writeInt(this.itemCount);
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
