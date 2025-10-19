package network.shop;

import database.DBConnection;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.models.CSPacket;
import objects.item.MapleItemInformationProvider;
import objects.utils.ServerProperties;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataProvider;
import objects.wz.provider.MapleDataProviderFactory;
import objects.wz.provider.MapleDataTool;

public class CashItemFactory {
   private static final CashItemFactory instance = new CashItemFactory();
   private static final int[] bestItems = new int[]{10002412, 10002413, 10002414, 10002576, 50200092};
   private final Map<Integer, CashItemInfo> itemStats = new HashMap<>();
   private final Map<Integer, List<Integer>> itemPackage = new HashMap<>();
   private final Map<Integer, CashItemInfo.CashModInfo> itemMods = new HashMap<>();
   private final Map<Integer, List<Integer>> openBox = new HashMap<>();
   private final MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Etc.wz"));

   public static final CashItemFactory getInstance() {
      return instance;
   }

   public void initialize() {
      for (MapleData field : this.data.getData("Commodity.img").getChildren()) {
         int SN = MapleDataTool.getIntConvert("SN", field, 0);
         CashItemInfo stats = new CashItemInfo(
            MapleDataTool.getIntConvert("ItemId", field, 0),
            MapleDataTool.getIntConvert("Count", field, 1),
            MapleDataTool.getIntConvert("Price", field, 0),
            SN,
            MapleDataTool.getIntConvert("Period", field, 0),
            MapleDataTool.getIntConvert("Gender", field, 2),
            MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0
         );
         if (SN > 0) {
            this.itemStats.put(SN, stats);
         }
      }

      MapleData b = this.data.getData("CashPackage.img");

      for (MapleData c : b.getChildren()) {
         if (c.getChildByPath("SN") != null) {
            List<Integer> packageItems = new ArrayList<>();

            for (MapleData d : c.getChildByPath("SN").getChildren()) {
               packageItems.add(MapleDataTool.getIntConvert(d));
            }

            this.itemPackage.put(Integer.parseInt(c.getName()), packageItems);
         }
      }

      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_modified_items");
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {
            CashItemInfo.CashModInfo ret = new CashItemInfo.CashModInfo(
               rs.getInt("serial"),
               rs.getInt("discount_price"),
               rs.getInt("mark"),
               rs.getInt("showup") > 0,
               rs.getInt("itemid"),
               rs.getInt("priority"),
               rs.getInt("package") > 0,
               rs.getInt("period"),
               rs.getInt("gender"),
               rs.getInt("count"),
               rs.getInt("meso"),
               rs.getInt("unk_1"),
               rs.getInt("unk_2"),
               rs.getInt("unk_3"),
               rs.getInt("extra_flags")
            );
            this.itemMods.put(ret.sn, ret);
            if (ret.showUp) {
               CashItemInfo cc = this.itemStats.get(ret.sn);
               if (cc != null) {
                  ret.toCItem(cc);
               }
            }
         }

         rs.close();
         ps.close();
      } catch (Exception var16) {
         System.out.println("CashItemFactory Err");
         var16.printStackTrace();
      }

      String[] sn = ServerProperties.getProperty("SN").replaceAll(" ", "").split(",");
      String[] price = ServerProperties.getProperty("Price").replaceAll(" ", "").split(",");
      String[] onSale = ServerProperties.getProperty("OnSale").replaceAll(" ", "").split(",");

      for (int i = 0; i < sn.length; i++) {
         int sn_ = Integer.parseInt(sn[i]);
         int price_ = Integer.parseInt(price[i]);
         int onSale_ = Integer.parseInt(onSale[i]);
         CashItemInfo cinfo = getInstance().getItem(sn_, true);
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         if (cinfo != null && (cinfo.getId() / 1000000 == 9 || ii.itemExists(cinfo.getId()))) {
            CashItemInfo.CashModInfo info = new CashItemInfo.CashModInfo(sn_, price_, onSale_);
            CSPacket.putModdifiedCommodity(info);
            getInstance().putModInfo(sn_, info);
         }
      }

      System.out.println("캐시샵 판매 아이템 설정 로드가 완료되었습니다.");
   }

   public final CashItemInfo getSimpleItem(int sn) {
      return this.itemStats.get(sn);
   }

   public final CashItemInfo getItem(int sn, boolean force) {
      CashItemInfo stats = this.itemStats.get(sn);
      CashItemInfo.CashModInfo z = this.getModInfo(sn);
      if (z != null && z.showUp) {
         return z.toCItem(stats);
      } else {
         return stats == null ? null : stats;
      }
   }

   public final List<Integer> getPackageItems(int itemId) {
      return this.itemPackage.get(itemId);
   }

   public final CashItemInfo.CashModInfo getModInfo(int sn) {
      return this.itemMods.get(sn);
   }

   public final Collection<CashItemInfo.CashModInfo> getAllModInfo() {
      return this.itemMods.values();
   }

   public final void putModInfo(int sn, CashItemInfo.CashModInfo info) {
      this.itemMods.put(sn, info);
   }

   public final Map<Integer, List<Integer>> getRandomItemInfo() {
      return this.openBox;
   }

   public final int[] getBestItems() {
      return bestItems;
   }
}
