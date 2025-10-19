package network.auction;

import constants.GameConstants;
import database.DBConfig;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import network.SendPacketOpcode;
import network.center.Center;
import network.encode.PacketEncoder;
import network.models.PacketHelper;
import objects.item.Item;
import objects.item.MapleItemInformationProvider;
import objects.users.MapleCharacter;

public class Auction {
   private int type;

   public Auction(int type) {
      this.type = type;
   }

   public void encodeHeader(PacketEncoder mplew) {
      mplew.writeShort(SendPacketOpcode.AUCTION_RESULT.getValue());
      mplew.writeInt(this.type);
   }

   public void encodeAuctionItem(PacketEncoder mplew, AuctionItemPackage auctionItem, MapleCharacter player, boolean isItemList) {
      this.encodeAuctionItem(mplew, auctionItem, player, isItemList, false);
   }

   public void encodeAuctionItem(PacketEncoder mplew, AuctionItemPackage auctionItem, MapleCharacter player, boolean isItemList, boolean isMarketPrice) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      Item item = auctionItem.getItem();
      mplew.writeInt(auctionItem.getItem().getInventoryId());
      mplew.writeInt(auctionItem.isBargain() ? 1 : 0);
      boolean isReal = false;
      if (DBConfig.isGanglim) {
         LocalDateTime date = LocalDateTime.of(2022, 4, 26, 0, 0, 0);
         long milliSeconds = Timestamp.valueOf(date).getTime();
         if (auctionItem.getExpiredTime() < milliSeconds) {
            isReal = true;
         }
      }

      int temp = auctionItem.getType(auctionItem.getOwnerId() == player.getId(), isReal);
      int type = isItemList && auctionItem.getOwnerId() == player.getId() ? 1 : (auctionItem.getOwnerId() != player.getId() && temp == 2 ? 3 : temp);
      if (isMarketPrice) {
         type = 3;
      }

      mplew.writeInt(type);
      mplew.writeInt(0);
      if (isMarketPrice) {
         mplew.writeLong(auctionItem.getMesos());
      } else {
         mplew.writeLong(type == 4 ? 0L : 5L);
      }

      mplew.writeLong(-1L);
      boolean isCash = ii.isCash(item.getItemId());
      if (isCash) {
         mplew.writeLong(auctionItem.getMesos());
      } else {
         mplew.writeLong(type == 3 ? auctionItem.getMesos() : auctionItem.getMesos() * item.getQuantity());
      }

      long market_meso = auctionItem.getMesos();
      if (isMarketPrice && !GameConstants.isRechargable(item.getItemId()) && item.getQuantity() > 1) {
         market_meso /= item.getQuantity();
      }

      mplew.writeLong(auctionItem.getMesos());
      mplew.writeLong(Double.doubleToRawLongBits(market_meso));
      long duration = auctionItem.getExpiredTime();
      mplew.writeLong(PacketHelper.getKoreanTimestamp(duration));
      mplew.writeLong(PacketHelper.getKoreanTimestamp(duration));
      mplew.writeLong(type == 0 ? 0L : 2000L);
      mplew.writeInt(item.getItemId() / 1000000);
      mplew.writeInt(item.getItemId() / 1000000 - 1);
      mplew.writeLong(isMarketPrice ? PacketHelper.getKoreanTimestamp(auctionItem.getBuyTime()) : PacketHelper.getTime(-2L));
      if (isMarketPrice) {
         mplew.write(false);
      } else {
         mplew.write(true);
         mplew.writeLong(0L);
         mplew.writeInt(auctionItem.getAccountID());
         mplew.writeInt(auctionItem.getOwnerId());
         mplew.writeMapleAsciiString(auctionItem.getOwnerName());
      }

      PacketHelper.addItemInfo(mplew, item);
   }

   private double log2(double i, double j) {
      return Math.log10(j) / Math.log10(i);
   }

   private long unitPrice(AuctionItemPackage auctionItem) {
      String binaryPrice = Long.toBinaryString(auctionItem.getMesos());
      binaryPrice = binaryPrice.replace("1", "0");
      binaryPrice = "1" + binaryPrice.substring(0, binaryPrice.length() - 1);
      long binaryToLong = Long.parseLong(binaryPrice, 2);
      int exponent = (int)this.log2(2.0, binaryToLong) - 1;
      String remainderBinary = Long.toBinaryString(auctionItem.getMesos() - Long.parseLong(binaryPrice, 2));
      LinkedList<Long> maxBinary = new LinkedList<>();
      long total = Long.parseLong(binaryPrice, 2);
      long value = Long.parseLong(remainderBinary, 2);

      while (total != 1L) {
         total /= 2L;
         maxBinary.add(total);
      }

      total = 0L;

      for (int i = 0; i < remainderBinary.length(); i++) {
         if (remainderBinary.startsWith("1", i)) {
            total += maxBinary.get(i);
         }
      }

      while (total != value) {
         for (int ix = 0; ix < remainderBinary.length(); ix++) {
            if (remainderBinary.startsWith("1", ix)) {
               total += maxBinary.get(ix);
            }
         }

         if (total != value) {
            remainderBinary = "0" + remainderBinary;
            total = 0L;
         }
      }

      while (remainderBinary.length() < 52) {
         remainderBinary = remainderBinary + "0";
      }

      long uPriceH = Long.parseLong(remainderBinary, 2);
      String cBinary = Long.toBinaryString(exponent) + "0000000000000000000000000000000000000000000000000000";
      return 4611686018427387904L | Long.parseLong(cBinary, 2) | uPriceH;
   }

   public static class BuyBundleItemDone extends Auction {
      private int result;
      private long auctionInventoryID;

      public BuyBundleItemDone(int result, long auctionInventoryID) {
         super(AuctionResult.AuctionRes_BuyBundleItemDone.getValue());
         this.result = result;
         this.auctionInventoryID = auctionInventoryID;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(this.result);
         mplew.writeInt(this.auctionInventoryID);
         return mplew.getPacket();
      }
   }

   public static class BuyItem extends Auction {
      private AuctionItemPackage item;
      private MapleCharacter player;
      private boolean isRegisterWish;

      public BuyItem(AuctionItemPackage item, MapleCharacter player, boolean isRegisterWish) {
         super(AuctionResult.AuctionRes_BuyItem.getValue());
         this.item = item;
         this.player = player;
         this.isRegisterWish = isRegisterWish;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(0);
         mplew.writeInt(this.item.getItem().getInventoryId());
         mplew.write(this.isRegisterWish);
         if (this.isRegisterWish) {
            this.encodeAuctionItem(mplew, this.item, this.player, this.isRegisterWish);
         }

         return mplew.getPacket();
      }
   }

   public static class BuyItemDone extends Auction {
      private int result;
      private long auctionInventoryID;

      public BuyItemDone(int result, long auctionInventoryID) {
         super(AuctionResult.AuctionRes_BuyItemDone.getValue());
         this.result = result;
         this.auctionInventoryID = auctionInventoryID;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(this.result);
         mplew.writeInt(this.auctionInventoryID);
         return mplew.getPacket();
      }
   }

   public static class CancelItemDone extends Auction {
      private int auctionInventoryID;

      public CancelItemDone(int auctionInventoryID) {
         super(AuctionResult.AuctionRes_CancelItemDone.getValue());
         this.auctionInventoryID = auctionInventoryID;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(0);
         mplew.writeInt(this.auctionInventoryID);
         return mplew.getPacket();
      }
   }

   public static class MarketPriceList extends Auction {
      private List<AuctionItemPackage> itemList;
      private MapleCharacter player;
      private int result;

      public MarketPriceList(List<AuctionItemPackage> itemList, MapleCharacter player, int result) {
         super(AuctionResult.AuctionRes_MarketPriceList.getValue());
         this.itemList = itemList;
         this.player = player;
         this.result = result;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(this.result);
         mplew.writeInt(0);
         mplew.write(1);
         mplew.write(0);
         mplew.writeInt(Math.min(100, this.itemList.size()));
         AtomicInteger ai = new AtomicInteger(0);

         for (AuctionItemPackage aip : this.itemList) {
            this.encodeAuctionItem(mplew, aip, this.player, true, true);
            if (ai.addAndGet(1) >= 100) {
               break;
            }
         }

         mplew.writeInt(0);
         return mplew.getPacket();
      }
   }

   public static class MyHistory extends Auction {
      private List<AuctionItemPackage> itemList;
      private MapleCharacter player;

      public MyHistory(List<AuctionItemPackage> itemList, MapleCharacter player) {
         super(AuctionResult.AuctionRes_MyHistory.getValue());
         this.itemList = itemList;
         this.player = player;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeLong(0L);
         mplew.writeInt(this.itemList.size());

         for (AuctionItemPackage item : this.itemList) {
            mplew.writeLong(item.getHistoryID());
            mplew.writeInt(item.getItem().getInventoryId());
            mplew.writeInt(item.getAccountID());
            mplew.writeInt(item.getOwnerId());
            mplew.writeInt(item.getItem().getItemId());
            boolean isReal = false;
            if (DBConfig.isGanglim) {
               LocalDateTime date = LocalDateTime.of(2022, 4, 26, 0, 0, 0);
               long milliSeconds = Timestamp.valueOf(date).getTime();
               if (item.getExpiredTime() < milliSeconds) {
                  isReal = true;
               }
            }

            int type = item.getType(item.getOwnerId() == this.player.getId(), isReal);
            mplew.writeInt(type);
            mplew.writeLong(type == 4 ? 0L : item.getMesos());
            mplew.writeLong(PacketHelper.getKoreanTimestamp(item.getBuyTime()));
            mplew.writeLong(0L);
            mplew.writeInt(item.getItem().getQuantity());
            mplew.writeInt(0);
            mplew.write(type == 2 || type == 4);
            if (type != 2 && type != 4) {
               mplew.write(item.getItem() != null);
               if (item.getItem() != null) {
                  PacketHelper.addItemInfo(mplew, item.getItem());
               }
            } else {
               this.encodeAuctionItem(mplew, item, this.player, false);
            }
         }

         return mplew.getPacket();
      }
   }

   public static class MyItemList extends Auction {
      private List<AuctionItemPackage> itemList;
      private MapleCharacter player;

      public MyItemList(List<AuctionItemPackage> itemList, MapleCharacter player) {
         super(AuctionResult.AuctionRes_MyItemList.getValue());
         this.itemList = itemList;
         this.player = player;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(0);
         mplew.writeInt(0);
         mplew.writeInt(this.itemList.size());
         this.itemList.forEach(item -> this.encodeAuctionItem(mplew, item, this.player, false));
         mplew.writeInt(0);
         return mplew.getPacket();
      }
   }

   public static class NotInitialize extends Auction {
      public NotInitialize() {
         super(AuctionResult.AuctionRes_NotInitialize.getValue());
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(0);
         mplew.writeInt(0);
         return mplew.getPacket();
      }
   }

   public static class PaymentReceiptDone extends Auction {
      private int result;
      private long auctionInventoryID;

      public PaymentReceiptDone(int result, long auctionInventoryID) {
         super(AuctionResult.AuctionRes_PaymentReceiptDone.getValue());
         this.result = result;
         this.auctionInventoryID = auctionInventoryID;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(this.result);
         mplew.writeInt(this.auctionInventoryID);
         return mplew.getPacket();
      }
   }

   public static class ReRegisterItemDone extends Auction {
      private int auctionInventoryID;

      public ReRegisterItemDone(int auctionInventoryID) {
         super(AuctionResult.AuctionRes_ReRegisterItemDone.getValue());
         this.auctionInventoryID = auctionInventoryID;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(0);
         mplew.writeInt(this.auctionInventoryID);
         return mplew.getPacket();
      }
   }

   public static class RegisterHistoryItem extends Auction {
      private AuctionItemPackage item;
      private MapleCharacter player;

      public RegisterHistoryItem(AuctionItemPackage item, MapleCharacter player) {
         super(AuctionResult.AuctionRes_RegisterHistoryItem.getValue());
         this.item = item;
         this.player = player;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeLong(0L);
         mplew.writeLong(this.item.getHistoryID());
         mplew.write(true);
         mplew.writeLong(this.item.getHistoryID());
         mplew.writeInt(this.item.getItem().getInventoryId());
         mplew.writeInt(this.item.getAccountID());
         mplew.writeInt(this.player.getId());
         mplew.writeInt(this.item.getItem().getItemId());
         int type = this.item.getType(this.item.getOwnerId() == this.player.getId(), false);
         mplew.writeInt(type);
         mplew.writeLong(type != 2 && type != 4 && type != 7 && type != 8 ? 0L : this.item.getMesos());
         mplew.writeLong(PacketHelper.getKoreanTimestamp(System.currentTimeMillis()) - 864000000000L);
         mplew.writeLong(0L);
         mplew.writeInt(this.item.getItem().getQuantity());
         mplew.writeInt(5);
         mplew.write(type == 2 || type == 4);
         if (type != 2 && type != 4) {
            mplew.write(this.item.getItem() != null);
            if (this.item.getItem() != null) {
               PacketHelper.addItemInfo(mplew, this.item.getItem());
            }
         } else {
            this.encodeAuctionItem(mplew, this.item, this.player, false);
         }

         return mplew.getPacket();
      }
   }

   public static class RegisterItem extends Auction {
      private MapleCharacter owner;
      private AuctionItemPackage item;

      public RegisterItem(AuctionItemPackage item, MapleCharacter owner) {
         super(AuctionResult.AuctionRes_RegisterItem.getValue());
         this.item = item;
         this.owner = owner;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(0);
         mplew.writeInt(this.item.getItem().getInventoryId());
         mplew.write(true);
         this.encodeAuctionItem(mplew, this.item, this.owner, false);
         return mplew.getPacket();
      }
   }

   public static class RegisterItemDone extends Auction {
      private int result;
      private int auctionInventoryID;

      public RegisterItemDone(int result, int auctionInventoryID) {
         super(AuctionResult.AuctionRes_RegisterItemDone.getValue());
         this.result = result;
         this.auctionInventoryID = auctionInventoryID;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(this.result);
         mplew.writeInt(this.auctionInventoryID);
         return mplew.getPacket();
      }
   }

   public static class RegisterSaleItemList extends Auction {
      private List<AuctionItemPackage> itemList;
      private MapleCharacter owner;

      public RegisterSaleItemList(List<AuctionItemPackage> itemList, MapleCharacter owner) {
         super(AuctionResult.AuctionRes_RegisterSaleItemList.getValue());
         this.itemList = itemList;
         this.owner = owner;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(1000);
         mplew.writeInt(0);
         mplew.write(1);
         mplew.write(0);
         mplew.writeInt(Math.min(200, this.itemList.size()));
         AtomicInteger ai = new AtomicInteger(0);

         for (AuctionItemPackage aip : this.itemList) {
            this.encodeAuctionItem(mplew, aip, this.owner, true);
            if (ai.addAndGet(1) >= 200) {
               break;
            }
         }

         mplew.writeInt(0);
         return mplew.getPacket();
      }
   }

   public static class RegisterWishItemDone extends Auction {
      private int result;
      private long auctionInventoryID;

      public RegisterWishItemDone(int result, long auctionInventoryID) {
         super(AuctionResult.AuctionRes_RegisterWishItemDone.getValue());
         this.result = result;
         this.auctionInventoryID = auctionInventoryID;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(this.result);
         mplew.writeInt(this.auctionInventoryID);
         return mplew.getPacket();
      }
   }

   public static class RemoveWishList extends Auction {
      private long auctionInventoryID;

      public RemoveWishList(long auctionInventoryID) {
         super(AuctionResult.AuctionRes_RemoveWishList.getValue());
         this.auctionInventoryID = auctionInventoryID;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(0);
         mplew.writeInt(this.auctionInventoryID);
         return mplew.getPacket();
      }
   }

   public static class ReturnItemDone extends Auction {
      private int result;
      private long auctionInventoryID;

      public ReturnItemDone(int result, long auctionInventoryID) {
         super(AuctionResult.AuctionRes_ReturnItemDone.getValue());
         this.result = result;
         this.auctionInventoryID = auctionInventoryID;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(this.result);
         mplew.writeInt(this.auctionInventoryID);
         return mplew.getPacket();
      }
   }

   public static class SearchResult extends Auction {
      private int result;
      private List<AuctionItemPackage> itemList;
      private MapleCharacter player;

      public SearchResult(List<AuctionItemPackage> itemList, MapleCharacter player, int result) {
         super(AuctionResult.AuctionRes_SearchResult.getValue());
         this.itemList = itemList;
         this.player = player;
         this.result = result;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(this.result);
         mplew.writeInt(0);
         mplew.write(true);
         mplew.write(0);
         mplew.writeInt(Math.min(200, this.itemList.size()));
         AtomicInteger ai = new AtomicInteger(0);

         for (AuctionItemPackage aip : this.itemList) {
            this.encodeAuctionItem(mplew, aip, this.player, true);
            if (ai.addAndGet(1) >= 200) {
               break;
            }
         }

         mplew.writeInt(0);
         return mplew.getPacket();
      }
   }

   public static class UpdateSaleItem extends Auction {
      private AuctionItemPackage item;
      private MapleCharacter player;

      public UpdateSaleItem(AuctionItemPackage item, MapleCharacter player) {
         super(AuctionResult.AuctionRes_UpdateSaleItem.getValue());
         this.item = item;
         this.player = player;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(0);
         mplew.writeInt(this.item.getItem().getInventoryId());
         boolean soldout = this.item.getItem().getQuantity() == 0;
         mplew.write(soldout ? 0 : 1);
         if (!soldout) {
            this.encodeAuctionItem(mplew, this.item, this.player, true);
         }

         return mplew.getPacket();
      }
   }

   public static class WishList extends Auction {
      private List<Integer> wishList;
      private MapleCharacter player;

      public WishList(List<Integer> wishList, MapleCharacter player) {
         super(AuctionResult.AuctionRes_WishList.getValue());
         this.wishList = wishList;
         this.player = player;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(0);
         mplew.writeInt(0);
         List<AuctionItemPackage> list = new ArrayList<>();
         this.wishList.forEach(id -> {
            AuctionItemPackage item = Center.Auction.getItem(id);
            if (item != null) {
               list.add(item);
            }
         });
         mplew.writeInt(list.size());
         list.forEach(item -> this.encodeAuctionItem(mplew, item, this.player, false));
         list.clear();
         mplew.writeInt(0);
         return mplew.getPacket();
      }
   }
}
