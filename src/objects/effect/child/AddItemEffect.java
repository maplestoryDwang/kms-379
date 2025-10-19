package objects.effect.child;

import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.effect.Effect;
import objects.effect.EffectHeader;
import objects.utils.Triple;

public class AddItemEffect implements Effect {
   public static final int header = EffectHeader.AddItem.getValue();
   private final int playerID;
   private final List<Triple<Integer, Integer, Boolean>> addItemList;

   public AddItemEffect(int playerID, List<Triple<Integer, Integer, Boolean>> itemList) {
      this.playerID = playerID;
      this.addItemList = itemList;
   }

   @Override
   public void encode(PacketEncoder packet) {
      packet.write(this.addItemList.size());

      for (Triple<Integer, Integer, Boolean> itemList : this.addItemList) {
         packet.writeInt(itemList.left);
         packet.writeInt(itemList.mid);
         packet.write(itemList.right);
      }

      packet.writeMapleAsciiString("");
      packet.writeInt(0);
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
