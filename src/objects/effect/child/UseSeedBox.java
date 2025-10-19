package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;

public class UseSeedBox implements Effect {
   public static final int header = EffectHeader.UseSeedBox.getValue();
   private final int playerID;
   private final boolean isEquip;
   private final boolean isInBag;
   private final int itemID;
   private final int itemCount;

   public UseSeedBox(int playerID, boolean isEquip, int itemID, int itemCount, boolean isInBag) {
      this.playerID = playerID;
      this.isEquip = isEquip;
      this.itemID = itemID;
      this.itemCount = itemCount;
      this.isInBag = isInBag;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.write(this.isEquip);
      packet.write(true);
      packet.writeInt(this.itemID);
      packet.writeInt(this.itemCount);
      packet.write(this.isInBag);
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
