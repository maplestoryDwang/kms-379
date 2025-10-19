package objects.effect.child;

import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.PacketHelper;
import objects.effect.Effect;
import objects.effect.EffectHeader;
import objects.item.Item;

public class ItemFloatNotice implements Effect {
   public static final int header = EffectHeader.ItemFloatNotice.getValue();
   private final int playerID;
   private final Item item;

   public ItemFloatNotice(int playerID, Item item) {
      this.playerID = playerID;
      this.item = item;
   }

   @Override
   public void encode(PacketEncoder packet) {
      PacketHelper.addItemInfo(packet, this.item);
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
