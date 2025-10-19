package objects.shop;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import network.center.Center;
import network.game.GameServer;
import network.models.PlayerShopPacket;
import objects.fields.Field;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.utils.Pair;

public abstract class AbstractPlayerStore extends MapleMapObject implements IMaplePlayerShop {
   protected boolean open = false;
   protected boolean available = false;
   protected String ownerName;
   protected String des;
   protected String pass;
   protected int ownerId;
   protected int owneraccount;
   protected int itemId;
   protected int channel;
   protected int map;
   protected AtomicInteger meso = new AtomicInteger(0);
   protected WeakReference<MapleCharacter>[] chrs;
   protected List<String> visitors = new LinkedList<>();
   protected List<AbstractPlayerStore.BoughtItem> bought = new LinkedList<>();
   protected List<MaplePlayerShopItem> items = new LinkedList<>();

   public AbstractPlayerStore(MapleCharacter owner, int itemId, String desc, String pass, int slots) {
      this.setPosition(owner.getTruePosition());
      this.ownerName = owner.getName();
      this.ownerId = owner.getId();
      this.owneraccount = owner.getAccountID();
      this.itemId = itemId;
      this.des = desc;
      this.pass = pass;
      this.map = owner.getMapId();
      this.channel = owner.getClient().getChannel();
      this.chrs = new WeakReference[slots];

      for (int i = 0; i < this.chrs.length; i++) {
         this.chrs[i] = new WeakReference<>(null);
      }
   }

   @Override
   public int getMaxSize() {
      return this.chrs.length + 1;
   }

   @Override
   public int getSize() {
      return this.getFreeSlot() == -1 ? this.getMaxSize() : this.getFreeSlot();
   }

   @Override
   public void broadcastToVisitors(byte[] packet) {
      this.broadcastToVisitors(packet, true);
   }

   public void broadcastToVisitors(byte[] packet, boolean owner) {
      for (WeakReference<MapleCharacter> chr : this.chrs) {
         if (chr != null && chr.get() != null) {
            chr.get().getClient().getSession().writeAndFlush(packet);
         }
      }

      if (this.getShopType() != 1 && owner && this.getMCOwner() != null) {
         this.getMCOwner().getClient().getSession().writeAndFlush(packet);
      }
   }

   public void broadcastToVisitors(byte[] packet, int exception) {
      for (WeakReference<MapleCharacter> chr : this.chrs) {
         if (chr != null && chr.get() != null && this.getVisitorSlot(chr.get()) != exception) {
            chr.get().getClient().getSession().writeAndFlush(packet);
         }
      }

      if (this.getShopType() != 1 && this.getMCOwner() != null && exception != this.ownerId) {
         this.getMCOwner().getClient().getSession().writeAndFlush(packet);
      }
   }

   @Override
   public int getMeso() {
      return this.meso.get();
   }

   @Override
   public void setMeso(int meso) {
      this.meso.set(meso);
   }

   @Override
   public void setOpen(boolean open) {
      this.open = open;
   }

   @Override
   public boolean isOpen() {
      return this.open;
   }

   public boolean saveItems() {
      return false;
   }

   public MapleCharacter getVisitor(int num) {
      return this.chrs[num].get();
   }

   @Override
   public void update() {
      if (this.isAvailable()) {
         if (this.getShopType() == 1) {
            this.getMap().broadcastMessage(PlayerShopPacket.updateHiredMerchant((HiredMerchant)this));
         } else if (this.getMCOwner() != null) {
            this.getMap().broadcastMessage(PlayerShopPacket.sendPlayerShopBox(this.getMCOwner()));
         }
      }
   }

   @Override
   public void addVisitor(MapleCharacter visitor) {
      int i = this.getFreeSlot();
      if (i > 0) {
         if (this.getShopType() >= 3) {
            this.broadcastToVisitors(PlayerShopPacket.getMiniGameNewVisitor(visitor, i, (MapleMiniGame)this));
         } else {
            this.broadcastToVisitors(PlayerShopPacket.shopVisitorAdd(visitor, i));
         }

         this.chrs[i - 1] = new WeakReference<>(visitor);
         if (!this.isOwner(visitor)) {
            this.visitors.add(visitor.getName());
         }

         if (this.getItemId() >= 4080000 && this.getItemId() <= 4080100) {
            if (i == 1) {
               this.update();
            }
         } else if (i == 3) {
            this.update();
         }
      }
   }

   @Override
   public void removeVisitor(MapleCharacter visitor) {
      byte slot = this.getVisitorSlot(visitor);
      boolean shouldUpdate = this.getFreeSlot() == -1;
      if (slot > 0) {
         this.broadcastToVisitors(PlayerShopPacket.shopVisitorLeave(slot), slot);
         this.chrs[slot - 1] = new WeakReference<>(null);
         if (shouldUpdate) {
            this.update();
         }
      }
   }

   @Override
   public byte getVisitorSlot(MapleCharacter visitor) {
      for (byte i = 0; i < this.chrs.length; i++) {
         if (this.chrs[i] != null && this.chrs[i].get() != null && this.chrs[i].get().getId() == visitor.getId()) {
            return (byte)(i + 1);
         }
      }

      return (byte)(visitor.getId() == this.ownerId ? 0 : -1);
   }

   @Override
   public void removeAllVisitors(int error, int type) {
      for (int i = 0; i < this.chrs.length; i++) {
         MapleCharacter visitor = this.getVisitor(i);
         if (visitor != null) {
            if (type != -1) {
               visitor.getClient().getSession().writeAndFlush(PlayerShopPacket.shopErrorMessage(error, type));
            }

            this.broadcastToVisitors(PlayerShopPacket.shopVisitorLeave(this.getVisitorSlot(visitor)), this.getVisitorSlot(visitor));
            visitor.setPlayerShop(null);
            this.chrs[i] = new WeakReference<>(null);
         }
      }

      this.update();
   }

   @Override
   public String getOwnerName() {
      return this.ownerName;
   }

   @Override
   public int getOwnerId() {
      return this.ownerId;
   }

   @Override
   public int getOwnerAccId() {
      return this.owneraccount;
   }

   @Override
   public String getDescription() {
      return this.des == null ? "" : this.des;
   }

   @Override
   public List<Pair<Byte, MapleCharacter>> getVisitors() {
      List<Pair<Byte, MapleCharacter>> chrz = new LinkedList<>();

      for (byte i = 0; i < this.chrs.length; i++) {
         if (this.chrs[i] != null && this.chrs[i].get() != null) {
            chrz.add(new Pair<>((byte)(i + 1), this.chrs[i].get()));
         }
      }

      return chrz;
   }

   @Override
   public List<MaplePlayerShopItem> getItems() {
      return this.items;
   }

   @Override
   public void addItem(MaplePlayerShopItem item) {
      this.items.add(item);
   }

   @Override
   public boolean removeItem(int item) {
      return false;
   }

   @Override
   public void removeFromSlot(int slot) {
      this.items.remove(slot);
   }

   @Override
   public byte getFreeSlot() {
      for (byte i = 0; i < this.chrs.length; i++) {
         if (this.chrs[i] == null || this.chrs[i].get() == null) {
            return (byte)(i + 1);
         }
      }

      return -1;
   }

   @Override
   public int getItemId() {
      return this.itemId;
   }

   @Override
   public boolean isOwner(MapleCharacter chr) {
      return chr.getId() == this.ownerId && chr.getName().equals(this.ownerName);
   }

   @Override
   public String getPassword() {
      return this.pass == null ? "" : this.pass;
   }

   @Override
   public void sendDestroyData(MapleClient client) {
   }

   @Override
   public void sendSpawnData(MapleClient client) {
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.SHOP;
   }

   public MapleCharacter getMCOwnerWorld() {
      int ourChannel = Center.Find.findChannel(this.ownerId);
      return ourChannel <= 0 ? null : GameServer.getInstance(ourChannel).getPlayerStorage().getCharacterById(this.ownerId);
   }

   public MapleCharacter getMCOwnerChannel() {
      return GameServer.getInstance(this.channel).getPlayerStorage().getCharacterById(this.ownerId);
   }

   public MapleCharacter getMCOwner() {
      return this.getMap().getCharacterById(this.ownerId);
   }

   public Field getMap() {
      return GameServer.getInstance(this.channel).getMapFactory().getMap(this.map);
   }

   @Override
   public int getGameType() {
      if (this.getShopType() == 1) {
         return 6;
      } else if (this.getShopType() == 2) {
         return 5;
      } else if (this.getShopType() == 3) {
         return 1;
      } else {
         return this.getShopType() == 4 ? 2 : 0;
      }
   }

   @Override
   public boolean isAvailable() {
      return this.available;
   }

   @Override
   public void setAvailable(boolean b) {
      this.available = b;
   }

   @Override
   public List<AbstractPlayerStore.BoughtItem> getBoughtItems() {
      return this.bought;
   }

   public static final class BoughtItem {
      public int id;
      public int quantity;
      public int totalPrice;
      public String buyer;

      public BoughtItem(int id, int quantity, int totalPrice, String buyer) {
         this.id = id;
         this.quantity = quantity;
         this.totalPrice = totalPrice;
         this.buyer = buyer;
      }
   }
}
