package objects.fields.gameobject.lifes;

import constants.GameConstants;
import database.DBConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.utils.Properties;
import objects.utils.Table;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;

public class MapleMonsterInformationProvider {
   private static final MapleMonsterInformationProvider instance = new MapleMonsterInformationProvider();
   private final Map<Integer, ArrayList<MonsterDropEntry>> drops = new HashMap<>();
   private final List<MonsterGlobalDropEntry> globaldrops = new ArrayList<>();
   private static final MapleDataProvider stringDataWZ = MapleDataProviderFactory.getDataProvider(
      new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz")
   );

   public static MapleMonsterInformationProvider getInstance() {
      return instance;
   }

   public List<MonsterGlobalDropEntry> getGlobalDrop() {
      return this.globaldrops;
   }

   public void load() {
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim/Drops/Global" : "data/Jin/Drops/Global", "Global.data");
      int count = 0;

      for (Table children : table.getChild("Global").list()) {
         this.globaldrops
            .add(
               new MonsterGlobalDropEntry(
                  children.getProperty("ItemID", 0),
                  children.getProperty("Chance", 0),
                  children.getProperty("Continent", 0),
                  (byte)children.getProperty("DropType", 0),
                  children.getProperty("Min", 0),
                  children.getProperty("Max", 0),
                  children.getProperty("QuestID", 0),
                  children.getProperty("Individual", 0) > 0
               )
            );
      }

      File dir = new File(DBConfig.isGanglim ? "data/Ganglim/Drops" : "data/Jin/Drops");

      for (File f : dir.listFiles()) {
         if (!f.isDirectory()) {
            ArrayList<MonsterDropEntry> ret = new ArrayList<>();
            table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim/Drops" : "data/Jin/Drops", f.getName());
            if (table != null) {
               int i = Integer.parseInt(f.getName().split(".data")[0]);
               MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(i);
               if (mons != null) {
                  boolean doneMesos = false;
                  if (table.getChild(String.valueOf(i)) == null) {
                     System.out.println("Error DropData : " + i);
                  } else {
                     for (Table children : table.getChild(String.valueOf(i)).list()) {
                        int itemID = children.getProperty("ItemID", 0);
                        int chance = children.getProperty("Chance", 0);
                        if (!DBConfig.isGanglim
                           && GameConstants.getInventoryType(itemID) == MapleInventoryType.EQUIP
                           && !GameConstants.isArcaneSymbol(itemID)
                           && !GameConstants.isAuthenticSymbol(itemID)) {
                           chance *= 10;
                        }

                        if (itemID == 0) {
                           doneMesos = true;
                        }

                        ret.add(
                           new MonsterDropEntry(
                              itemID,
                              chance,
                              children.getProperty("Min", 0),
                              children.getProperty("Max", 0),
                              children.getProperty("QuestID", 0),
                              0,
                              children.getProperty("Individual", 0) > 0
                           )
                        );
                     }

                     if (!doneMesos) {
                        this.addMeso(mons, ret);
                     }

                     this.drops.put(i, ret);
                  }
               }
            }
         }
      }

      if (!DBConfig.isGanglim) {
         this.globaldrops.add(new MonsterGlobalDropEntry(2633343, 20000, -1, (byte)1, 1, 1, 0));
      }
   }

   public ArrayList<MonsterDropEntry> retrieveDrop(int monsterId) {
      return this.drops.get(monsterId);
   }

   public void addExtra() {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      for (Entry<Integer, ArrayList<MonsterDropEntry>> e : this.drops.entrySet()) {
         for (int i = 0; i < e.getValue().size(); i++) {
            if (e.getValue().get(i).itemId != 0 && !ii.itemExists(e.getValue().get(i).itemId)) {
               e.getValue().remove(i);
            }
         }

         MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(e.getKey());
         Integer item = ii.getItemIdByMob(e.getKey());
         if (item != null && item > 0) {
            e.getValue().add(new MonsterDropEntry(item, mons.isBoss() ? 1000000 : 10000, 1, 1, 0));
         }
      }
   }

   public void addMeso(MapleMonsterStats mons, ArrayList<MonsterDropEntry> ret) {
      double divided = mons.getLevel() < 100 ? (mons.getLevel() < 10 ? mons.getLevel() : 10.0) : mons.getLevel() / 10.0;
      int max = mons.isBoss() && !mons.isPartyBonus() ? mons.getLevel() * mons.getLevel() : mons.getLevel() * (int)Math.ceil(mons.getLevel() / divided);

      for (int i = 0; i < mons.dropsMeso(); i++) {
         ret.add(
            new MonsterDropEntry(
               0, mons.isBoss() && !mons.isPartyBonus() ? 1000000 : (mons.isPartyBonus() ? 100000 : 200000), (int)Math.floor(0.66 * max), max, 0
            )
         );
      }
   }

   public void clearDrops() {
      this.drops.clear();
      this.globaldrops.clear();
      this.load();
      this.addExtra();
   }

   public boolean contains(ArrayList<MonsterDropEntry> e, int toAdd) {
      for (MonsterDropEntry f : e) {
         if (f.itemId == toAdd) {
            return true;
         }
      }

      return false;
   }

   public int chanceLogic(int itemId) {
      if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
         return 50000;
      } else if (GameConstants.getInventoryType(itemId) != MapleInventoryType.SETUP && GameConstants.getInventoryType(itemId) != MapleInventoryType.CASH) {
         switch (itemId / 10000) {
            case 204:
            case 207:
            case 229:
            case 233:
               return 500;
            case 401:
            case 402:
               return 5000;
            case 403:
               return 5000;
            default:
               return 20000;
         }
      } else {
         return 500;
      }
   }
}
