package objects.users;

import database.loader.CharacterSaveFlag;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.shop.MapleShop;
import objects.shop.MapleShopItem;

public class BuyLimit {
   private List<BuyLimitEntry> buyLimits = new ArrayList<>();

   public int getBuyableCount(MapleShop shop, int limitCount) {
      return limitCount;
   }

   public void addBuyLimit(BuyLimitEntry entry) {
      this.buyLimits.add(entry);
   }

   public void setLimit(MapleCharacter player, MapleShop shop, MapleShopItem item, int slot) {
      BuyLimitEntry e = null;

      for (BuyLimitEntry entry : new ArrayList<>(this.getBuyLimits())) {
         if (entry.getShopID() == shop.getId() && entry.getItemIndex() == slot) {
            entry.setBuyCount(entry.getBuyCount() + 1);
            entry.setBuyTime(System.currentTimeMillis());
            e = entry;
            break;
         }
      }

      if (e == null) {
         e = new BuyLimitEntry(shop.getId(), slot, item.getItemId(), 1, System.currentTimeMillis());
         this.getBuyLimits().add(e);
      }

      BuyLimitEntry copy = new BuyLimitEntry(shop.getNpcId(), e.getItemIndex(), e.getItemID(), e.getBuyCount(), e.getBuyTime());
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_BUY_LIMIT_COUNT.getValue());
      packet.write(0);
      copy.encode(packet);
      player.send(packet.getPacket());
      player.setSaveFlag(player.getSaveFlag() | CharacterSaveFlag.NPC_SHOP_BUY_LIMIT.getFlag());
   }

   public List<BuyLimitEntry> getBuyLimits() {
      return this.buyLimits;
   }

   public void checkBuyLimit(MapleCharacter player, int shopID, int npcID) {
      List<Integer> reset = new ArrayList<>();

      for (BuyLimitEntry entry : new ArrayList<>(this.buyLimits)) {
         if (entry.getShopID() == shopID) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String now = sdf.format(System.currentTimeMillis());
            String buyTime = sdf.format(entry.getBuyTime());
            String[] dates = now.split("-");
            String[] dates2 = buyTime.split("-");
            int day = Integer.parseInt(dates[2]);
            int buyTimeDay = Integer.parseInt(dates2[2]);
            if (day != buyTimeDay) {
               entry.setBuyCount(0);
               entry.setBuyTime(System.currentTimeMillis());
               reset.add(entry.getItemIndex());
               player.setSaveFlag(player.getSaveFlag() | CharacterSaveFlag.NPC_SHOP_BUY_LIMIT.getFlag());
            } else {
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.SET_BUY_LIMIT_COUNT.getValue());
               packet.write(0);
               BuyLimitEntry copy = new BuyLimitEntry(npcID, entry.getItemIndex(), entry.getItemID(), entry.getBuyCount(), entry.getBuyTime());
               copy.encode(packet);
               player.send(packet.getPacket());
            }
         }
      }

      if (!reset.isEmpty()) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.RESET_BUY_LIMIT_COUNT.getValue());
         packet.writeInt(npcID);
         packet.writeInt(reset.size());

         for (int idx : reset) {
            packet.writeShort(idx);
         }

         player.send(packet.getPacket());
      }
   }

   public void setBuyLimits(List<BuyLimitEntry> buyLimits) {
      this.buyLimits = buyLimits;
   }
}
