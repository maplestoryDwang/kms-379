package objects.shop;

import constants.GameConstants;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import network.game.GameServer;
import network.models.CWvsContext;
import network.models.PlayerShopPacket;
import objects.fields.MapleMapObjectType;
import objects.item.Item;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleItemInformationProvider;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.enchant.ItemFlag;
import objects.utils.Timer;

public class HiredMerchant extends AbstractPlayerStore {
   public ScheduledFuture<?> schedule;
   private List<String> blacklist;
   private int storeid;
   private long start = System.currentTimeMillis();

   public HiredMerchant(MapleCharacter owner, int itemId, String desc) {
      super(owner, itemId, desc, "", 6);
      this.blacklist = new LinkedList<>();
      this.schedule = Timer.EtcTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (HiredMerchant.this.getMCOwner() != null && HiredMerchant.this.getMCOwner().getPlayerShop() == HiredMerchant.this) {
               HiredMerchant.this.getMCOwner().setPlayerShop(null);
            }

            HiredMerchant.this.removeAllVisitors(-1, -1);
            HiredMerchant.this.closeShop(true, true);
         }
      }, 86400000L);
   }

   @Override
   public byte getShopType() {
      return 1;
   }

   public final void setStoreid(int storeid) {
      this.storeid = storeid;
   }

   public List<MaplePlayerShopItem> searchItem(int itemSearch) {
      List<MaplePlayerShopItem> itemz = new LinkedList<>();

      for (MaplePlayerShopItem item : this.items) {
         if (item.item.getItemId() == itemSearch && item.bundles > 0) {
            itemz.add(item);
         }
      }

      return itemz;
   }

   @Override
   public void buy(MapleClient c, int item, short quantity) {
      MaplePlayerShopItem pItem = this.items.get(item);
      Item shopItem = pItem.item;
      Item newItem = shopItem.copy();
      short perbundle = newItem.getQuantity();
      int theQuantity = pItem.price * quantity;
      newItem.setQuantity((short)(quantity * perbundle));
      int flag = newItem.getFlag();
      if (ItemFlag.KARMA_EQ.check(flag)) {
         newItem.setFlag((short)(flag - ItemFlag.KARMA_EQ.getValue()));
      } else if (ItemFlag.KARMA_USE.check(flag)) {
         newItem.setFlag((short)(flag - ItemFlag.KARMA_USE.getValue()));
      }

      if (MapleInventoryManipulator.checkSpace(c, newItem.getItemId(), newItem.getQuantity(), newItem.getOwner())) {
         int gainmeso = this.getMeso() + theQuantity - GameConstants.EntrustedStoreTax(theQuantity);
         if (gainmeso > 0) {
            this.setMeso(gainmeso);
            pItem.bundles -= quantity;
            MapleInventoryManipulator.addFromDrop(c, newItem, false);
            this.bought.add(new AbstractPlayerStore.BoughtItem(newItem.getItemId(), quantity, theQuantity, c.getPlayer().getName()));
            c.getPlayer().gainMeso(-theQuantity, false);
            this.saveItems();
            MapleCharacter chr = this.getMCOwnerWorld();
            if (chr != null) {
               chr.dropMessage(
                  -5,
                  "Item "
                     + MapleItemInformationProvider.getInstance().getName(newItem.getItemId())
                     + " ("
                     + perbundle
                     + ") x "
                     + quantity
                     + " has sold in the Hired Merchant. Quantity left: "
                     + pItem.bundles
               );
            }
         } else {
            c.getPlayer().dropMessage(1, "The seller has too many mesos.");
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         }
      } else {
         c.getPlayer().dropMessage(1, "Your inventory is full.");
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      }
   }

   @Override
   public void closeShop(boolean saveItems, boolean remove) {
      if (this.schedule != null) {
         this.schedule.cancel(false);
      }

      if (saveItems) {
         this.saveItems();
         this.items.clear();
      }

      if (remove) {
         GameServer.getInstance(this.channel).removeMerchant(this);
         this.getMap().broadcastMessage(PlayerShopPacket.destroyHiredMerchant(this.getOwnerId()));
      }

      this.getMap().removeMapObject(this);
      this.schedule = null;
   }

   public int getTimeLeft() {
      return (int)((System.currentTimeMillis() - this.start) / 1000L);
   }

   public final int getStoreId() {
      return this.storeid;
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.HIRED_MERCHANT;
   }

   @Override
   public void sendDestroyData(MapleClient client) {
      if (this.isAvailable()) {
         client.getSession().writeAndFlush(PlayerShopPacket.destroyHiredMerchant(this.getOwnerId()));
      }
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      if (this.isAvailable()) {
         client.getSession().writeAndFlush(PlayerShopPacket.spawnHiredMerchant(this));
      }
   }

   public final boolean isInBlackList(String bl) {
      return this.blacklist.contains(bl);
   }

   public final void addBlackList(String bl) {
      this.blacklist.add(bl);
   }

   public final void removeBlackList(String bl) {
      this.blacklist.remove(bl);
   }

   public final void sendBlackList(MapleClient c) {
      c.getSession().writeAndFlush(PlayerShopPacket.MerchantBlackListView(this.blacklist));
   }

   public final void sendVisitor(MapleClient c) {
      c.getSession().writeAndFlush(PlayerShopPacket.MerchantVisitorView(this.visitors));
   }
}
