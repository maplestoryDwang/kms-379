package network.game.processors.inventory;

import constants.GameConstants;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.models.CWvsContext;
import network.models.PacketHelper;
import objects.item.Item;
import objects.item.ItemPot;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.users.MapleCharacter;
import objects.utils.Pair;

public class ItemPotHandler {
   public static final byte Normal = 1;
   public static final byte Hunger = 2;
   public static final byte Sleep = 3;
   public static final byte Sick = 4;
   public static final byte Complete = 5;
   public static final int Remove = 1;
   public static final int Summon = 2;
   public static final int Level = 4;
   public static final int LastState = 8;
   public static final int Satiety = 16;
   public static final int Friendly = 32;
   public static final int RemainAbleFriendly = 64;
   public static final int RemainFriendlyTime = 128;
   public static final int MaximumIncLevel = 256;
   public static final int MaximumIncSatiety = 512;
   public static final int DateLastEatTime = 1024;
   public static final int DateLastSleepStartTime = 2048;
   public static final int DateLastDecSatietyTime = 4096;
   public static final int DateConsumedLastTime = 8192;

   public static void usePot(PacketDecoder o, MapleCharacter chr) {
      int itemID = o.readInt();
      short pos = o.readShort();
      if (chr != null) {
         chr.send(CWvsContext.enableActions(chr));
         Item item = chr.getInventory(MapleInventoryType.USE).getItem(pos);
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         Pair<Integer, Integer> pot = ii.getPot(itemID);
         if (item == null || item.getItemId() != itemID || pot == null) {
            return;
         }

         for (int i = 0; i < chr.getItemPots().length; i++) {
            if (chr.getItemPots()[i] == null) {
               MapleInventoryManipulator.removeFromSlot(chr.getClient(), MapleInventoryType.USE, pos, (short)1, false);
               ItemPot itemPot = new ItemPot(pot.left);
               itemPot.setLevel((byte)1);
               itemPot.setLastState((byte)1);
               itemPot.setRemainAbleFriendly(1);
               itemPot.setRemainFriendlyTime(10);
               itemPot.setMaximumIncLevel(1);
               itemPot.setDateLastEatTime(System.currentTimeMillis());
               itemPot.setDateLastSleepStartTime(System.currentTimeMillis() - 86400000L);
               itemPot.setDateLastDecSatietyTime(System.currentTimeMillis());
               itemPot.setDateConsumedLastTime(System.currentTimeMillis());
               chr.getItemPots()[i] = itemPot;
               chr.send(updatePot(true, i, 2, itemPot));
               break;
            }
         }
      }
   }

   public static void clearPot(PacketDecoder o, MapleCharacter chr) {
      int potPos = o.readInt();
      ItemPot itemPot = chr.getItemPots()[potPos - 1];
      if (itemPot != null) {
         chr.getItemPots()[potPos - 1] = null;
         chr.send(updatePot(false, potPos - 1, 1, null));
         chr.send(CWvsContext.enableActions(chr));
      }
   }

   public static void feedPot(PacketDecoder o, MapleCharacter chr) {
      int itemID = o.readInt();
      MapleInventoryType invType = GameConstants.getInventoryType(itemID);
      Item item = chr.getInventory(invType).getItem((short)o.readInt());
      int potPos = o.readInt();
      ItemPot itemPot = chr.getItemPots()[potPos - 1];
      if (itemPot != null) {
         MapleInventoryManipulator.removeFromSlot(chr.getClient(), invType, item.getPosition(), (short)1, false);
         chr.send(updatePot(false, potPos - 1, 8, itemPot));
         chr.send(CWvsContext.enableActions(chr));
      }
   }

   public static byte[] updatePot(boolean openUI, int slot, int flag, ItemPot pot) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.ITEM_POT.getValue());
      o.write(openUI);
      o.writeInt(slot + 1);
      o.writeInt(flag);
      if ((flag & 2) != 0) {
         pot.encode(o);
         o.writeInt(100);
         o.writeInt(1260);
         o.writeInt(1000);
         o.writeInt(1);
         o.writeInt(10);
         o.write(false);
      }

      if ((flag & 4) != 0) {
         o.write(pot.getLevel());
         o.writeInt(0);
         o.writeInt(0);
         o.writeInt(0);
      }

      if ((flag & 8) != 0) {
         o.write(pot.getLastState());
      }

      if ((flag & 16) != 0) {
         o.writeInt(pot.getSatiety());
      }

      if ((flag & 32) != 0) {
         o.writeInt(pot.getFriendly());
      }

      if ((flag & 64) != 0) {
         o.writeInt(pot.getRemainAbleFriendly());
      }

      if ((flag & 128) != 0) {
         o.writeInt(pot.getRemainFriendlyTime());
      }

      if ((flag & 256) != 0) {
         o.write(pot.getMaximumIncLevel());
      }

      if ((flag & 512) != 0) {
         o.writeInt(pot.getMaximumIncSatiety());
      }

      if ((flag & 1024) != 0) {
         o.writeLong(PacketHelper.getTime(pot.getDateLastEatTime()));
      }

      if ((flag & 2048) != 0) {
         o.writeLong(PacketHelper.getTime(pot.getDateLastSleepStartTime()));
      }

      if ((flag & 4096) != 0) {
         o.writeLong(PacketHelper.getTime(pot.getDateLastDecSatietyTime()));
      }

      if ((flag & 8192) != 0) {
         o.writeLong(PacketHelper.getTime(pot.getDateConsumedLastTime()));
         o.write(false);
      }

      return o.getPacket();
   }
}
