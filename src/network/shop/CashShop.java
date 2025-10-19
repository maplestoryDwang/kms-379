package network.shop;

import constants.GameConstants;
import database.DBConnection;
import database.loader.ItemLoader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import network.models.CSPacket;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryIdentifier;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.item.MaplePet;
import objects.item.MapleRing;
import objects.users.MapleClient;
import objects.utils.FileoutputUtil;
import objects.utils.Pair;

public class CashShop implements Serializable {
   private static final long serialVersionUID = 231541893513373579L;
   private int accountId;
   private int characterId;
   private ItemLoader factory = ItemLoader.CASHSHOP;
   private List<Item> inventory = new ArrayList<>();
   private List<Long> uniqueids = new ArrayList<>();

   public CashShop(int accountId, int characterId, int jobType) throws SQLException {
      this.accountId = accountId;
      this.characterId = characterId;

      for (Pair<Item, MapleInventoryType> item : this.factory.loadItems(false, accountId, 0).values()) {
         this.inventory.add(item.getLeft());
      }
   }

   public int getItemsSize() {
      return this.inventory.size();
   }

   public List<Item> getInventory() {
      return this.inventory;
   }

   public Item findByCashId(int cashId) {
      for (Item item : this.inventory) {
         if (item.getUniqueId() == cashId) {
            return item;
         }
      }

      return null;
   }

   public void checkExpire(MapleClient c) {
      List<Item> toberemove = new ArrayList<>();

      for (Item item : this.inventory) {
         if (item != null && !GameConstants.isPet(item.getItemId()) && item.getExpiration() > 0L && item.getExpiration() < System.currentTimeMillis()) {
            toberemove.add(item);
         }
      }

      if (toberemove.size() > 0) {
         for (Item itemx : toberemove) {
            this.removeFromInventory(itemx);
            c.getSession().writeAndFlush(CSPacket.cashItemExpired(itemx.getUniqueId()));
         }

         toberemove.clear();
      }
   }

   public Item toItem(CashItemInfo cItem) {
      return this.toItem(cItem, MapleInventoryManipulator.getUniqueId(cItem.getId(), null), "");
   }

   public Item toItem(CashItemInfo cItem, String gift) {
      return this.toItem(cItem, MapleInventoryManipulator.getUniqueId(cItem.getId(), null), gift);
   }

   public Item toItem(CashItemInfo cItem, long uniqueid) {
      return this.toItem(cItem, uniqueid, "");
   }

   public Item toItem(CashItemInfo cItem, long uniqueid, String gift) {
      if (uniqueid <= 0L) {
         uniqueid = MapleInventoryIdentifier.getInstance();
      }

      long period = cItem.getPeriod();
      if (period <= 0L && GameConstants.getInventoryType(cItem.getId()) != MapleInventoryType.EQUIP || GameConstants.isPet(cItem.getId())) {
         period = 45L;
      }

      if (cItem.getId() >= 5000100 && cItem.getId() < 5000200) {
         period = 20000L;
      }

      Item ret = null;
      if (GameConstants.getInventoryType(cItem.getId()) == MapleInventoryType.EQUIP) {
         Equip eq = (Equip)MapleItemInformationProvider.getInstance().getEquipById(cItem.getId(), uniqueid);
         if (period > 0L) {
            eq.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
         }

         eq.setGMLog("Cash Shop: " + cItem.getSN() + " on " + FileoutputUtil.CurrentReadable_Date());
         eq.setGiftFrom(gift);
         if (GameConstants.isEffectRing(cItem.getId()) && uniqueid > 0L) {
            MapleRing ring = MapleRing.loadFromDb(uniqueid);
            if (ring != null) {
               eq.setRing(ring);
            }
         }

         ret = eq.copy();
      } else {
         Item item = new Item(cItem.getId(), (short)0, (short)cItem.getCount(), 0, uniqueid);
         if (period > 0L) {
            item.setExpiration(System.currentTimeMillis() + period * 24L * 60L * 60L * 1000L);
         }

         item.setGMLog("Cash Shop: " + cItem.getSN() + " on " + FileoutputUtil.CurrentReadable_Date());
         item.setGiftFrom(gift);
         if (GameConstants.isPet(cItem.getId())) {
            MaplePet pet = MaplePet.createPet(cItem.getId(), uniqueid);
            if (pet != null) {
               item.setPet(pet);
            }
         }

         ret = item.copy();
      }

      return ret;
   }

   public void addToInventory(Item item) {
      this.inventory.add(item);
   }

   public void removeFromInventory(Item item) {
      this.inventory.remove(item);
   }

   public void gift(int recipient, String from, String message, int sn) {
      this.gift(recipient, from, message, sn, 0L);
   }

   public void gift(int recipient, String from, String message, int sn, long uniqueid) {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("INSERT INTO `gifts` VALUES (DEFAULT, ?, ?, ?, ?, ?)");
         ps.setInt(1, recipient);
         ps.setString(2, from);
         ps.setString(3, message);
         ps.setInt(4, sn);
         ps.setLong(5, uniqueid);
         ps.executeUpdate();
         ps.close();
      } catch (SQLException var13) {
         var13.printStackTrace();
      }
   }

   public List<Pair<Item, String>> loadGifts() {
      List<Pair<Item, String>> gifts = new ArrayList<>();
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM `gifts` WHERE `recipient` = ?");
         ps.setInt(1, this.characterId);
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            CashItemInfo cItem = CashItemFactory.getInstance().getItem(rs.getInt("sn"), false);
            if (cItem != null) {
               Item item = this.toItem(cItem, rs.getInt("uniqueid"), rs.getString("from"));
               gifts.add(new Pair<>(item, rs.getString("message")));
               this.uniqueids.add(item.getUniqueId());
               List<Integer> packages = CashItemFactory.getInstance().getPackageItems(cItem.getId());
               if (packages != null && packages.size() > 0) {
                  for (int packageItem : packages) {
                     CashItemInfo pack = CashItemFactory.getInstance().getSimpleItem(packageItem);
                     if (pack != null) {
                        this.addToInventory(this.toItem(pack, rs.getString("from")));
                     }
                  }
               } else {
                  this.addToInventory(item);
               }
            }
         }

         rs.close();
         ps.close();
         ps = con.prepareStatement("DELETE FROM `gifts` WHERE `recipient` = ?");
         ps.setInt(1, this.characterId);
         ps.executeUpdate();
         ps.close();
         this.save();
      } catch (SQLException var14) {
         var14.printStackTrace();
      }

      return gifts;
   }

   public boolean canSendNote(int uniqueid) {
      return this.uniqueids.contains(uniqueid);
   }

   public void sendedNote(int uniqueid) {
      for (int i = 0; i < this.uniqueids.size(); i++) {
         if (this.uniqueids.get(i).intValue() == uniqueid) {
            this.uniqueids.remove(i);
         }
      }
   }

   public void save() throws SQLException {
      List<Pair<Item, MapleInventoryType>> itemsWithType = new ArrayList<>();

      for (Item item : this.inventory) {
         itemsWithType.add(new Pair<>(item, GameConstants.getInventoryType(item.getItemId())));
      }

      this.factory.saveItems(itemsWithType, this.accountId);
   }
}
