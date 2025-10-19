package objects.shop;

import database.DBConfig;
import java.util.HashMap;
import java.util.Map;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleNPC;
import objects.item.MapleItemInformationProvider;
import objects.utils.Properties;
import objects.utils.Table;

public class MapleShopFactory {
   private final Map<Integer, MapleShop> shops = new HashMap<>();
   private final Map<Integer, MapleShop> npcShops = new HashMap<>();
   private static final MapleShopFactory instance = new MapleShopFactory();

   public static MapleShopFactory getInstance() {
      return instance;
   }

   public void clear() {
      this.shops.clear();
      this.npcShops.clear();
   }

   public MapleShop getShop(int shopId) {
      return this.shops.containsKey(shopId) ? this.shops.get(shopId) : this.loadShop(shopId, true);
   }

   public MapleShop getShopForNPC(int npcId) {
      return this.npcShops.containsKey(npcId) ? this.npcShops.get(npcId) : this.loadShop(npcId, false);
   }

   private MapleShop loadShop(int id, boolean isShopId) {
      if (isShopId) {
      }

      MapleShop ret = new MapleShop(id, id);
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim/Shops" : "data/Jin/Shops", id + ".data");
      if (table == null) {
         return null;
      } else {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

         for (Table c : table.list()) {
            MapleNPC npc = MapleLifeFactory.getNPC(c.getID());
            int npcID = 9001000;
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
               npcID = c.getID();
            }

            if (isShopId) {
               ret.setNpcId(npcID);
            }

            for (Table children : c.list()) {
               int itemID = children.getProperty("ItemID", 0);
               if (ii.getItemInformation(itemID) != null) {
                  int idx = Integer.parseInt(children.getName());
                  ret.addItem(
                     new MapleShopItem(
                        idx,
                        (short)1,
                        (short)children.getProperty("Quantity", 1),
                        itemID,
                        children.getProperty("Price", 0),
                        (short)children.getProperty("Position", 0),
                        children.getProperty("ReqItem", 0),
                        children.getProperty("ReqItemQ", 0),
                        (byte)0,
                        children.getProperty("Category", 0),
                        children.getProperty("MinLevel", 0),
                        children.getProperty("Expiration", 0),
                        false,
                        children.getProperty("PointQuestExID", 0),
                        children.getProperty("PointPrice", 0),
                        children.getProperty("BuyLimit", 0),
                        children.getProperty("WorldBuyLimit", 0),
                        children.getProperty("LimitQuestExID", 0),
                        children.getProperty("LimitQuestExKey", ""),
                        children.getProperty("LimitQuestExValue", 0)
                     )
                  );
               }
            }
         }

         this.shops.put(id, ret);
         this.npcShops.put(ret.getNpcId(), ret);
         return ret;
      }
   }
}
