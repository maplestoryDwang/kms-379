package objects.item;

import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.PacketHelper;
import objects.users.MapleCharacter;

public class InventoryOperation {
   public static byte[] swapPacket(MapleInventoryType type, short src, short dst) {
      PacketEncoder o = new PacketEncoder();
      o.write(InventoryOperation.InventoryOperationType.SWAP.getType());
      o.write(type.getType());
      o.writeShort(src);
      o.writeShort(dst);
      return o.getPacket();
   }

   public static byte[] removePacket(MapleInventoryType type, short src) {
      PacketEncoder o = new PacketEncoder();
      o.write(InventoryOperation.InventoryOperationType.REMOVE.getType());
      o.write(type.getType());
      o.writeShort(src);
      return o.getPacket();
   }

   public static byte[] updatePacket(Item item, short position, MapleInventoryType type, MapleCharacter chr, boolean trade, boolean bagSlot) {
      PacketEncoder o = new PacketEncoder();
      o.write(InventoryOperation.InventoryOperationType.UPDATE.getType());
      o.write(type.getType());
      o.writeShort(position);
      PacketHelper.addItemInfo(o, item, chr);
      return o.getPacket();
   }

   public static byte[] getInventoryOperationPacket(List<byte[]> operations, boolean exlusive) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.INVENTORY_OPERATION.getValue());
      o.write(exlusive);
      o.write(0);
      o.writeInt(operations.size());
      o.write(0);

      for (byte[] arr : operations) {
         o.encodeBuffer(arr);
      }

      o.write(0);
      return o.getPacket();
   }

   static enum InventoryOperationType {
      SWAP(2),
      REMOVE(3),
      UPDATE(5);

      int type;

      private InventoryOperationType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }
   }
}
