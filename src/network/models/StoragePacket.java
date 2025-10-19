package network.models;

import java.util.Collection;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.item.Item;
import objects.item.MapleInventoryType;

public class StoragePacket {
   public static byte[] getStorage(int npcId, short slots, Collection<Item> items, long meso) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
      mplew.write(StoragePacket.StorageStatus.getStorage.getValue());
      mplew.writeInt(npcId);
      mplew.write((int)slots);
      List<BufferbitFlag> flag = List.of(
         BufferbitFlag.Meso, BufferbitFlag.EQUIP, BufferbitFlag.CONSUME, BufferbitFlag.INSTALL, BufferbitFlag.ETC, BufferbitFlag.CASH
      );
      mplew.flagBuffer(flag);
      mplew.writeLong(meso);
      mplew.write((byte)items.size());

      for (Item item : items) {
         PacketHelper.addItemInfo(mplew, item);
      }

      mplew.write(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] getStorage(byte status) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
      mplew.write(status);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] getStorageFull() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
      mplew.write(StoragePacket.StorageStatus.StorageFull.getValue());
      return mplew.getPacket();
   }

   public static byte[] mesoStorage(short slots, long meso) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
      mplew.write(StoragePacket.StorageStatus.Meso.getValue());
      mplew.write((int)slots);
      mplew.flagBuffer(BufferbitFlag.Meso);
      mplew.writeLong(meso);
      return mplew.getPacket();
   }

   public static byte[] arrangeStorage(short slots, Collection<Item> items, boolean changed) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
      mplew.write(StoragePacket.StorageStatus.Arrange.getValue());
      mplew.write((int)slots);
      List<BufferbitFlag> flag = List.of(BufferbitFlag.EQUIP, BufferbitFlag.CONSUME, BufferbitFlag.INSTALL, BufferbitFlag.ETC, BufferbitFlag.CASH);
      mplew.flagBuffer(flag);
      mplew.write(items.size());

      for (Item item : items) {
         PacketHelper.addItemInfo(mplew, item);
      }

      mplew.write(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] storeStorage(short slots, MapleInventoryType type, Collection<Item> items) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
      mplew.write(StoragePacket.StorageStatus.storeStorage.getValue());
      mplew.write((int)slots);
      BufferbitFlag flag = null;
      switch (type.getType()) {
         case 1:
            flag = BufferbitFlag.EQUIP;
            break;
         case 2:
            flag = BufferbitFlag.CONSUME;
            break;
         case 3:
            flag = BufferbitFlag.INSTALL;
            break;
         case 4:
            flag = BufferbitFlag.ETC;
            break;
         case 5:
            flag = BufferbitFlag.CASH;
      }

      mplew.flagBuffer(flag);
      mplew.write(items.size());

      for (Item item : items) {
         PacketHelper.addItemInfo(mplew, item);
      }

      return mplew.getPacket();
   }

   public static byte[] takeOutStorage(short slots, MapleInventoryType type, Collection<Item> items) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
      mplew.write(StoragePacket.StorageStatus.takeOutStorage.getValue());
      mplew.write((int)slots);
      BufferbitFlag flag = null;
      switch (type.getType()) {
         case 1:
            flag = BufferbitFlag.EQUIP;
            break;
         case 2:
            flag = BufferbitFlag.CONSUME;
            break;
         case 3:
            flag = BufferbitFlag.INSTALL;
            break;
         case 4:
            flag = BufferbitFlag.ETC;
            break;
         case 5:
            flag = BufferbitFlag.CASH;
      }

      mplew.flagBuffer(flag);
      mplew.write(items.size());

      for (Item item : items) {
         PacketHelper.addItemInfo(mplew, item);
      }

      return mplew.getPacket();
   }

   static enum StorageStatus {
      secondPassword(1),
      getStorage(24),
      takeOutStorage(9),
      StorageFull(17),
      Meso(19),
      storeStorage(13),
      Arrange(15);

      private int value;

      private StorageStatus(int flag) {
         this.value = flag;
      }

      public int getValue() {
         return this.value;
      }
   }
}
