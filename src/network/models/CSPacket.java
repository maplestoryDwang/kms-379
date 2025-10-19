package network.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.shop.CashItemFactory;
import network.shop.CashItemInfo;
import network.shop.CashShop;
import objects.context.ReportLogEntry;
import objects.item.Item;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MapleMessage;
import objects.utils.Pair;

public class CSPacket {
   private static final List<CashItemInfo.CashModInfo> modinfo = new ArrayList<>();
   public static final HashMap<Integer, CashItemInfo.CSRand> csrand = new HashMap<>();
   private static final List<CashItemInfo.CSMainBest> mainbest = new ArrayList<>();
   private static final List<CashItemInfo.CSCustomizedCSPackage> ccspack = new ArrayList<>();
   private static final List<Pair<String, String>> searchhelp = new ArrayList<>();

   public static byte[] stageSetCashShop(MapleClient c) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPEN.getValue());
      PacketHelper.addCharacterInfo(mplew, c.getPlayer(), -1L);
      mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
      mplew.write(0);
      mplew.write(0);
      mplew.write(0);
      mplew.write(0);
      mplew.write(0);
      mplew.write(0);
      mplew.writeShort(modinfo.size());
      modinfo.forEach(mi -> addModCashItemInfo(mplew, mi));
      mplew.writeShort(0);
      mplew.writeInt(csrand.size());
      csrand.values().forEach(cr -> addCSRand(mplew, cr));
      mplew.writeInt(mainbest.size());
      mainbest.forEach(cb -> addCSMainBest(mplew, cb));
      mplew.writeInt(ccspack.size());
      ccspack.forEach(cp -> addCSCustomizedCSPackage(mplew, cp));
      mplew.writeInt(searchhelp.size());

      for (Pair<String, String> p : searchhelp) {
         mplew.writeMapleAsciiString(p.getLeft());
         mplew.writeMapleAsciiString(p.getRight());
      }

      mplew.writeShort(0);
      mplew.writeShort(0);
      return mplew.getPacket();
   }

   public static void addModCashItemInfo(PacketEncoder mplew, CashItemInfo.CashModInfo item) {
      long flags = item.flags;
      mplew.writeInt(item.sn);
      mplew.writeLong(flags);
      if ((flags & 1L) != 0L) {
         mplew.writeInt(item.itemid);
      }

      if ((flags & 2L) != 0L) {
         mplew.writeShort(item.count);
      }

      if ((flags & 16L) != 0L) {
         mplew.write(item.priority);
      }

      if ((flags & 4L) != 0L) {
         mplew.writeInt(item.discountPrice);
      }

      if ((flags & 268435456L) != 0L) {
         mplew.writeInt(0);
      }

      if ((flags & 33554432L) != 0L) {
         mplew.writeInt(0);
      }

      if ((flags & 8L) != 0L) {
         mplew.write(item.unk_1 - 1);
      }

      if ((flags & 4294967296L) != 0L) {
         mplew.write(0);
      }

      if ((flags & 32L) != 0L) {
         mplew.writeShort(item.period);
      }

      if ((flags & 131072L) != 0L) {
         mplew.writeShort(0);
      }

      if ((flags & 262144L) != 0L) {
         mplew.writeShort(0);
      }

      if ((flags & 64L) != 0L) {
         mplew.writeInt(0);
      }

      if ((flags & 128L) != 0L) {
         mplew.writeInt(item.meso);
      }

      if ((flags & 256L) != 0L) {
         mplew.write(item.unk_2 - 1);
      }

      if ((flags & 512L) != 0L) {
         mplew.write(item.gender);
      }

      if ((flags & 1024L) != 0L) {
         mplew.write(item.showUp ? 1 : 0);
      }

      if ((flags & 2048L) != 0L) {
         mplew.write(item.mark);
      }

      if ((flags & 4096L) != 0L) {
         mplew.write(item.unk_3 - 1);
      }

      if ((flags & 8192L) != 0L) {
         mplew.writeShort(0);
      }

      if ((flags & 16384L) != 0L) {
         mplew.writeShort(0);
      }

      if ((flags & 32768L) != 0L) {
         mplew.writeShort(0);
      }

      if ((flags & 65536L) != 0L) {
         List<Integer> pack = CashItemFactory.getInstance().getPackageItems(item.sn);
         if (pack == null) {
            mplew.write(0);
         } else {
            mplew.write(pack.size());

            for (int i = 0; i < pack.size(); i++) {
               mplew.writeInt(pack.get(i));
            }
         }
      }

      if ((flags & 524288L) != 0L) {
         mplew.writeInt(0);
      }

      if ((flags & 1048576L) != 0L) {
         mplew.writeInt(0);
      }

      if ((flags & 2097152L) != 0L) {
         mplew.write(0);
      }

      if ((flags & 4194304L) != 0L) {
         mplew.write(0);
      }

      if ((flags & 8388608L) != 0L) {
         mplew.write(0);
         mplew.write(0);
      }

      if ((flags & 16777216L) != 0L) {
         mplew.write(0);
      }

      if ((flags & 67108864L) != 0L) {
         mplew.write(0);
      }

      if ((flags & 134217728L) != 0L) {
         mplew.writeInt(0);
      }

      if ((flags & 536870912L) != 0L) {
         mplew.write(0);
      }

      if ((flags & 1073741824L) != 0L) {
         mplew.writeLong(Double.doubleToLongBits(0.0));
      }

      if ((flags & -2147483648L) != 0L) {
         mplew.write(0);
         mplew.write(0);
      }

      if ((flags & 8589934592L) != 0L) {
         mplew.writeInt(0);
      }

      if ((flags & 17179869184L) != 0L) {
         mplew.writeInt(0);
         mplew.writeInt(0);
      }

      if ((flags & 34359738368L) != 0L) {
         mplew.write(0);
      }

      if ((flags & 68719476736L) != 0L) {
         mplew.write(0);
      }

      if ((flags & 137438953472L) != 0L) {
         mplew.writeInt(0);
      }

      if ((flags & 274877906944L) != 0L) {
         mplew.writeInt(0);
      }

      if ((flags & 549755813888L) != 0L) {
         mplew.write(0);
      }
   }

   public static void addCSRand(PacketEncoder mplew, CashItemInfo.CSRand cr) {
      mplew.writeInt(cr.sn);
      mplew.writeInt(cr.items.length);

      for (int i : cr.items) {
         mplew.writeInt(i);
      }
   }

   public static void addCSMainBest(PacketEncoder mplew, CashItemInfo.CSMainBest cb) {
      mplew.write(cb._class);
      mplew.writeInt(cb.sn);
   }

   public static void addCSCustomizedCSPackage(PacketEncoder mplew, CashItemInfo.CSCustomizedCSPackage cp) {
      mplew.write(cp._class);
      mplew.writeInt(cp.sn);
   }

   public static byte[] showNXMapleTokens(MapleCharacter chr) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_UPDATE.getValue());
      mplew.writeInt(chr.getCSPoints(1));
      mplew.writeInt(chr.getCSPoints(2));
      mplew.writeInt(chr.getCSPoints(4));
      return mplew.getPacket();
   }

   public static byte[] getCSInventory(MapleClient c) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(6);
      mplew.write(0);
      CashShop mci = c.getPlayer().getCashInventory();
      int size = 0;
      mplew.writeShort(mci.getItemsSize());

      for (Item itemz : mci.getInventory()) {
         addCashItemInfo(mplew, itemz, c.getAccID(), 0);
      }

      mplew.writeShort(c.getPlayer().getStorage().getSlots());
      mplew.writeShort(c.getCharacterSlots());
      mplew.writeShort(0);
      mplew.writeShort(2);
      return mplew.getPacket();
   }

   public static byte[] getCSGifts(MapleClient c) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(8);
      List<Pair<Item, String>> mci = c.getPlayer().getCashInventory().loadGifts();
      mplew.writeShort(mci.size());

      for (Pair<Item, String> mcz : mci) {
         mplew.writeLong(mcz.getLeft().getUniqueId());
         mplew.writeInt(mcz.getLeft().getItemId());
         mplew.writeMapleAsciiString_(mcz.getLeft().getGiftFrom(), 13);
         mplew.writeMapleAsciiString_(mcz.getRight(), 73);
      }

      return mplew.getPacket();
   }

   public static byte[] sendWishList(MapleCharacter chr, boolean update) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(update ? 12 : 10);
      int[] list = chr.getWishlist();

      for (int i = 0; i < 12; i++) {
         mplew.writeInt(list[i] != -1 ? list[i] : 0);
      }

      return mplew.getPacket();
   }

   public static byte[] showBoughtCSItem(Item item, int sn, int accid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(14);
      addCashItemInfo(mplew, item, accid, sn);
      mplew.writeZeroBytes(5);
      return mplew.getPacket();
   }

   public static byte[] showBoughtCSPackage(Map<Integer, Item> ccc, int accid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(70);
      mplew.write(ccc.size());

      for (Entry<Integer, Item> sn : ccc.entrySet()) {
         addCashItemInfo(mplew, sn.getValue(), accid, sn.getKey());
      }

      mplew.writeShort(0);
      mplew.writeZeroBytes(5);
      return mplew.getPacket();
   }

   public static byte[] sendGift(int price, int itemid, int quantity, String receiver, boolean packages) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(packages ? 76 : 23);
      mplew.writeMapleAsciiString(receiver);
      mplew.writeInt(itemid);
      mplew.writeShort(quantity);
      if (packages) {
         mplew.writeShort(0);
      }

      mplew.writeInt(price);
      return mplew.getPacket();
   }

   public static byte[] showCouponRedeemedItem(Map<Integer, Item> items, int mesos, int maplePoints, MapleClient c) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(16);
      mplew.write(items.size());

      for (Entry<Integer, Item> item : items.entrySet()) {
         addCashItemInfo(mplew, item.getValue(), c.getAccID(), item.getKey());
      }

      mplew.writeInt(maplePoints);
      mplew.writeInt(0);
      mplew.writeInt(mesos);
      return mplew.getPacket();
   }

   public static byte[] confirmFromCSInventory(Item item, short pos) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(33);
      mplew.write(1);
      mplew.writeShort(pos);
      PacketHelper.addItemInfo(mplew, item);
      mplew.writeInt(0);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] confirmToCSInventory(Item item, int accId, int sn) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(35);
      mplew.write(0);
      addCashItemInfo(mplew, item, accId, sn, false);
      return mplew.getPacket();
   }

   public static byte[] cashItemExpired(long uniqueid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(39);
      mplew.writeLong(uniqueid);
      return mplew.getPacket();
   }

   public static byte[] sendBoughtRings(boolean couple, Item item, int sn, int accid, String receiver) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(couple ? 72 : 82);
      addCashItemInfo(mplew, item, accid, sn);
      mplew.writeMapleAsciiString(receiver);
      mplew.writeInt(item.getItemId());
      mplew.writeShort(1);
      return mplew.getPacket();
   }

   public static byte[] showBoughtCSQuestItem(int price, short quantity, byte position, int itemid) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(78);
      mplew.writeInt(1);
      mplew.writeInt((int)quantity);
      mplew.writeInt(itemid);
      return mplew.getPacket();
   }

   public static byte[] updatePurchaseRecord(int sn, int count) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(97);
      mplew.writeInt(sn);
      mplew.writeInt(count);
      return mplew.getPacket();
   }

   public static byte[] sendRandomBox(long uniqueid, Item item, short pos) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(111);
      mplew.writeLong(uniqueid);
      mplew.writeInt(0);
      PacketHelper.addItemInfo(mplew, item);
      mplew.writeShort(pos);
      mplew.writeInt(0);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] changeNameCheck(String charname, boolean nameUsed) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHANGE_NAME_CHECK.getValue());
      mplew.writeMapleAsciiString(charname);
      mplew.write(nameUsed ? 1 : 0);
      return mplew.getPacket();
   }

   public static byte[] changeNameResponse(int mode, int pic) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHANGE_NAME_RESPONSE.getValue());
      mplew.writeInt(0);
      mplew.write(mode);
      mplew.writeInt(pic);
      return mplew.getPacket();
   }

   public static byte[] playCashSong(int itemid, String name) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PLAY_JUKE_BOX.getValue());
      mplew.writeInt(itemid);
      mplew.writeMapleAsciiString(name);
      return mplew.getPacket();
   }

   public static byte[] ViciousHammer(boolean start, boolean success) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.VICIOUS_HAMMER.getValue());
      mplew.write(0);
      if (start) {
         mplew.write(0);
         mplew.writeInt(1);
      } else {
         mplew.write(2);
         mplew.writeInt(success ? 0 : 1);
      }

      return mplew.getPacket();
   }

   public static byte[] changePetFlag(int uniqueId, boolean added, int flagAdded) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PET_FLAG_CHANGE.getValue());
      mplew.writeLong(uniqueId);
      mplew.write(added ? 1 : 0);
      mplew.writeShort(flagAdded);
      return mplew.getPacket();
   }

   public static byte[] changePetName(MapleCharacter chr, String newname, int slot) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.PET_NAMECHANGE.getValue());
      mplew.writeInt(chr.getId());
      mplew.writeInt(slot);
      mplew.writeMapleAsciiString(newname);
      return mplew.getPacket();
   }

   public static byte[] OnMemoResult(byte act, byte mode) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_NOTES.getValue());
      mplew.write(act);
      if (act == 9) {
         mplew.write(mode);
      }

      return mplew.getPacket();
   }

   public static byte[] updateSentMessage(int index, MapleMessage note) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_NOTES.getValue());
      mplew.write(15);
      mplew.writeInt(note.getUniqueID());
      mplew.write(index);
      mplew.writeInt(note.getFromcid());
      mplew.writeMapleAsciiString(note.getFrom());
      mplew.writeInt(note.getTocid());
      mplew.writeMapleAsciiString(note.getTo());
      mplew.writeMapleAsciiString(note.getMessage());
      mplew.writeLong(PacketHelper.getTime(note.getSentTime()));
      return mplew.getPacket();
   }

   public static byte[] showReceivedMessages(List<MapleMessage> rev) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_NOTES.getValue());
      mplew.write(6);
      mplew.write(rev.size());

      for (MapleMessage s : rev) {
         mplew.writeInt(s.getUniqueID());
         mplew.write(s.isChecked() ? 0 : 1);
         mplew.writeInt(s.getFromcid());
         mplew.writeInt(0);
         mplew.writeMapleAsciiString(s.getFrom());
         mplew.writeMapleAsciiString(s.getMessage());
         mplew.writeLong(PacketHelper.getTime(s.getSentTime()));
         mplew.write(s.isAddPop() ? 1 : 0);
         ReportLogEntry report = new ReportLogEntry(s.getFrom(), s.getMessage(), s.getFromcid());
         report.encode(mplew);
      }

      return mplew.getPacket();
   }

   public static byte[] showSentMessages(List<MapleMessage> sent) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_NOTES.getValue());
      mplew.write(7);
      mplew.write(sent.size());

      for (MapleMessage s : sent) {
         mplew.writeInt(s.getUniqueID());
         mplew.write(s.isChecked() ? 0 : 1);
         mplew.writeInt(s.getFromcid());
         mplew.writeMapleAsciiString(s.getFrom());
         mplew.writeInt(s.getTocid());
         mplew.writeMapleAsciiString(s.getTo());
         mplew.writeMapleAsciiString(s.getMessage());
         mplew.writeLong(PacketHelper.getTime(s.getSentTime()));
      }

      return mplew.getPacket();
   }

   public static byte[] deletedReceivedMessage(int id) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_NOTES.getValue());
      mplew.write(11);
      mplew.writeInt(1);
      mplew.writeInt(id);
      return mplew.getPacket();
   }

   public static byte[] deletedSentMessage(int id) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_NOTES.getValue());
      mplew.write(13);
      mplew.writeInt(1);
      mplew.writeInt(id);
      return mplew.getPacket();
   }

   public static byte[] checkedMessage(int id) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SHOW_NOTES.getValue());
      mplew.write(14);
      mplew.write(2);
      mplew.writeInt(1);
      mplew.writeInt(id);
      return mplew.getPacket();
   }

   public static byte[] useChalkboard(int charid, String msg) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHALKBOARD.getValue());
      mplew.writeInt(charid);
      if (msg != null && msg.length() > 0) {
         mplew.write(1);
         mplew.writeMapleAsciiString(msg);
      } else {
         mplew.write(0);
      }

      return mplew.getPacket();
   }

   public static byte[] OnMapTransferResult(MapleCharacter chr, byte vip, boolean delete) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.TROCK_LOCATIONS.getValue());
      mplew.write(delete ? 2 : 3);
      mplew.write(vip);
      if (vip == 1) {
         int[] map = chr.getRegRocks();

         for (int i = 0; i < 5; i++) {
            mplew.writeInt(map[i]);
         }
      } else if (vip == 2) {
         int[] map = chr.getRocks();

         for (int i = 0; i < 10; i++) {
            mplew.writeInt(map[i]);
         }
      } else if (vip == 3) {
         int[] map = chr.getHyperRocks();

         for (int i = 0; i < 13; i++) {
            mplew.writeInt(map[i]);
         }
      }

      return mplew.getPacket();
   }

   public static final void addCashItemInfo(PacketEncoder mplew, Item item, int accId, int sn) {
      addCashItemInfo(mplew, item, accId, sn, true);
   }

   public static final void addCashItemInfo(PacketEncoder mplew, Item item, int accId, int sn, boolean isFirst) {
      addCashItemInfo(mplew, item, item.getUniqueId(), accId, item.getItemId(), sn, item.getQuantity(), item.getGiftFrom(), item.getExpiration(), isFirst);
   }

   public static final void addCashItemInfo(
      PacketEncoder mplew, Item item, long uniqueid, int accId, int itemid, int sn, int quantity, String sender, long expire
   ) {
      addCashItemInfo(mplew, item, uniqueid, accId, itemid, sn, quantity, sender, expire, true);
   }

   public static final void addCashItemInfo(
      PacketEncoder mplew, Item item, long uniqueid, int accId, int itemid, int sn, int quantity, String sender, long expire, boolean isFirst
   ) {
      mplew.writeLong(uniqueid > 0L ? uniqueid : 0L);
      mplew.writeInt(accId);
      mplew.writeInt(0);
      mplew.writeInt(itemid);
      mplew.writeInt(isFirst ? sn : 0);
      mplew.writeShort(quantity);
      mplew.writeMapleAsciiString_(sender, 13);
      PacketHelper.addExpirationTime(mplew, expire);
      mplew.writeInt(0);
      mplew.writeLong(0L);
      mplew.writeInt(item.getUniqueId());
      mplew.writeInt(367866);
      mplew.write(0);
      mplew.write(1);
      mplew.write(0);
      mplew.write(item != null);
      if (item != null) {
         PacketHelper.addItemInfo(mplew, item);
      }
   }

   public static byte[] sendCSFail(int err) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(22);
      mplew.write(err);
      return mplew.getPacket();
   }

   public static byte[] enableCSUse() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_USE.getValue());
      mplew.write(1);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] sendMesobagFailed(boolean random) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(random ? SendPacketOpcode.RANDOM_MESOBAG_FAILURE.getValue() : SendPacketOpcode.MESOBAG_FAILURE.getValue());
      return mplew.getPacket();
   }

   public static byte[] sendMesobagSuccess(int mesos) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.MESOBAG_SUCCESS.getValue());
      mplew.writeInt(mesos);
      return mplew.getPacket();
   }

   public static byte[] sendRandomMesobagSuccess(int size, int mesos) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.RANDOM_MESOBAG_SUCCESS.getValue());
      mplew.write(size);
      mplew.writeInt(mesos);
      return mplew.getPacket();
   }

   public static byte[] buyCharacterSlot() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(29);
      mplew.writeShort(0);
      return mplew.getPacket();
   }

   public static byte[] buyPendantSlot(short date) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(31);
      mplew.writeShort(0);
      mplew.writeShort(date);
      return mplew.getPacket();
   }

   public static byte[] incSlotCountDone(byte TI, short incCount) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(25);
      mplew.write(TI);
      mplew.writeShort(incCount);
      return mplew.getPacket();
   }

   public static byte[] incTrunkSlotCountDone(short incCount) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(27);
      mplew.writeShort(incCount);
      return mplew.getPacket();
   }

   public static byte[] updateLimitItemQuantity(int a, int b, int count) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
      mplew.write(2);
      mplew.writeInt(a);
      mplew.writeInt(b);
      mplew.writeInt(count);
      return mplew.getPacket();
   }

   public static void putModdifiedCommodity(CashItemInfo.CashModInfo info) {
      modinfo.add(info);
   }
}
