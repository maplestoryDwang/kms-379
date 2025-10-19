package objects.users;

import constants.GameConstants;
import database.DBConfig;
import database.DBConnection;
import database.loader.ItemLoader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import network.models.StoragePacket;
import objects.item.Item;
import objects.item.MapleInventoryType;
import objects.utils.Pair;

public class MapleStorage implements Serializable {
   private static final long serialVersionUID = 9179541993413738569L;
   private int id;
   private int accountId;
   private List<Item> items;
   private long meso;
   private int lastNPC = 0;
   private short slots;
   private boolean changed = false;
   private Map<MapleInventoryType, List<Item>> typeItems = new EnumMap<>(MapleInventoryType.class);

   private MapleStorage(int id, short slots, long meso, int accountId) {
      this.id = id;
      this.slots = DBConfig.isGanglim ? 152 : slots;
      this.items = new CopyOnWriteArrayList<>();
      this.meso = meso;
      this.accountId = accountId;
   }

   public static int create(int id) throws SQLException {
      DBConnection db = new DBConnection();
      Connection con = DBConnection.getConnection();
      PreparedStatement ps = con.prepareStatement("INSERT INTO storages (accountid, slots, meso) VALUES (?, ?, ?)", 1);
      ps.setInt(1, id);
      ps.setInt(2, 4);
      ps.setLong(3, 0L);
      ps.executeUpdate();
      ResultSet rs = ps.getGeneratedKeys();
      if (rs.next()) {
         int storageid = rs.getInt(1);
         ps.close();
         rs.close();
         return storageid;
      } else {
         ps.close();
         rs.close();
         throw new RuntimeException("Inserting char failed.");
      }
   }

   public static MapleStorage loadStorage(int id) {
      MapleStorage ret = null;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM storages WHERE accountid = ?");
         ps.setInt(1, id);
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {
            int storeId = rs.getInt("storageid");
            ret = new MapleStorage(storeId, rs.getShort("slots"), rs.getLong("meso"), id);
            rs.close();
            ps.close();

            for (Pair<Item, MapleInventoryType> mit : ItemLoader.STORAGE.loadItems(false, id, 0).values()) {
               ret.items.add(mit.getLeft());
            }
         } else {
            int storeId = create(id);
            ret = new MapleStorage(storeId, (short)4, 0L, id);
            rs.close();
            ps.close();
         }
      } catch (SQLException var11) {
         System.err.println("Error loading storage" + var11);
      }

      return ret;
   }

   public void saveToDB(Connection con) throws SQLException {
      if (this.changed) {
         PreparedStatement ps = con.prepareStatement("UPDATE storages SET slots = ?, meso = ? WHERE storageid = ?");
         ps.setInt(1, this.slots);
         ps.setLong(2, this.meso);
         ps.setInt(3, this.id);
         ps.executeUpdate();
         ps.close();
         List<Pair<Item, MapleInventoryType>> listing = new ArrayList<>();

         for (Item item : this.items) {
            listing.add(new Pair<>(item, GameConstants.getInventoryType(item.getItemId())));
         }

         ItemLoader.STORAGE.saveItems(listing, con, this.accountId);
         this.changed = false;
      }
   }

   public Item takeOut(short slot) {
      if (slot < this.items.size() && slot >= 0) {
         this.changed = true;
         Item ret = this.items.remove(slot);
         MapleInventoryType type = GameConstants.getInventoryType(ret.getItemId());
         this.typeItems.put(type, this.filterItems(type));
         return ret;
      } else {
         return null;
      }
   }

   public void updateQty(short slot, int qty) {
      if (slot < this.items.size() && slot >= 0) {
         this.changed = true;
         this.items.get(slot).setQuantity((short)qty);
      }
   }

   public void store(Item item) {
      this.changed = true;
      this.items.add(item);
      MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
      this.typeItems.put(type, this.filterItems(type));
   }

   public void arrange() {
      Collections.sort(this.items, new Comparator<Item>() {
         public int compare(Item o1, Item o2) {
            if (o1.getItemId() < o2.getItemId()) {
               return -1;
            } else if (o1.getItemId() == o2.getItemId()) {
               if (o1.getQuantity() > o2.getQuantity()) {
                  return -1;
               } else {
                  return o1.getQuantity() == o2.getQuantity() ? 0 : 1;
               }
            } else {
               return 1;
            }
         }
      });

      for (MapleInventoryType type : MapleInventoryType.values()) {
         this.typeItems.put(type, this.items);
      }
   }

   public List<Item> getItems() {
      return Collections.unmodifiableList(this.items);
   }

   private List<Item> filterItems(MapleInventoryType type) {
      List<Item> ret = new ArrayList<>();

      for (Item item : this.items) {
         if (GameConstants.getInventoryType(item.getItemId()) == type) {
            ret.add(item);
         }
      }

      return ret;
   }

   public short getSlot(MapleInventoryType type, short slot) {
      short ret = 0;
      List<Item> it = this.typeItems.get(type);
      if (it != null && slot < it.size() && slot >= 0) {
         for (Item item : this.items) {
            if (item == it.get(slot)) {
               return ret;
            }

            ret++;
         }

         return -1;
      } else {
         return -1;
      }
   }

   public void sendStorage(MapleClient c, int npcId) {
      this.lastNPC = npcId;
      Collections.sort(this.items, new Comparator<Item>() {
         public int compare(Item o1, Item o2) {
            if (GameConstants.getInventoryType(o1.getItemId()).getType() < GameConstants.getInventoryType(o2.getItemId()).getType()) {
               return -1;
            } else {
               return GameConstants.getInventoryType(o1.getItemId()) == GameConstants.getInventoryType(o2.getItemId()) ? 0 : 1;
            }
         }
      });

      for (MapleInventoryType type : MapleInventoryType.values()) {
         this.typeItems.put(type, this.items);
      }

      c.getSession().writeAndFlush(StoragePacket.getStorage(npcId, this.slots, this.items, this.meso));
   }

   public void update(MapleClient c) {
      c.getSession().writeAndFlush(StoragePacket.arrangeStorage(this.slots, this.items, true));
   }

   public void sendStored(MapleClient c, MapleInventoryType type) {
      c.getSession().writeAndFlush(StoragePacket.storeStorage(this.slots, type, this.typeItems.get(type)));
   }

   public void sendTakenOut(MapleClient c, MapleInventoryType type) {
      c.getSession().writeAndFlush(StoragePacket.takeOutStorage(this.slots, type, this.typeItems.get(type)));
   }

   public long getMeso() {
      return this.meso;
   }

   public Item findById(int itemId) {
      for (Item item : this.items) {
         if (item.getItemId() == itemId) {
            return item;
         }
      }

      return null;
   }

   public void removeById(int itemId) {
      for (Item item : this.items) {
         if (item.getItemId() == itemId) {
            this.changed = true;
            if (this.items.remove(item)) {
            }
         }
      }
   }

   public void setMeso(long meso) {
      if (meso >= 0L) {
         this.changed = true;
         this.meso = meso;
      }
   }

   public void sendMeso(MapleClient c) {
      c.getSession().writeAndFlush(StoragePacket.mesoStorage(this.slots, this.meso));
   }

   public boolean isFull() {
      return this.items.size() >= this.slots;
   }

   public int getSlots() {
      return this.slots;
   }

   public void increaseSlots(short gain) {
      this.changed = true;
      this.slots += gain;
   }

   public void setSlots(short set) {
      this.changed = true;
      this.slots = set;
   }

   public void close() {
      this.typeItems.clear();
   }
}
