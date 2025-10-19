package objects.utils;

import database.DBConnection;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import objects.shop.MapleShop;
import objects.shop.MapleShopItem;

public class ShopsSQLtoDataRoyal {
   public static void main(String[] args) {
      DBConnection.init();
      List<MapleShop> ret = new ArrayList<>();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM shopitems2 ORDER BY shopid, position ASC");
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            MapleShop shop = null;

            for (MapleShop s : ret) {
               if (s.getId() == rs.getInt("shopid")) {
                  shop = s;
                  break;
               }
            }

            PreparedStatement ps2 = con.prepareStatement("SELECT * FROM shops2 WHERE shopid = ?");
            ps2.setInt(1, rs.getInt("shopid"));
            ResultSet rs2 = ps2.executeQuery();
            int coinKey = 0;
            int questEx = 0;
            if (rs2.next()) {
               coinKey = rs2.getInt("coinKey");
               questEx = rs2.getInt("questEx");
            }

            rs2.close();
            ps2.close();
            int price = 0;
            if (coinKey == 0) {
               price = rs.getInt("price");
            }

            int reqItemID = 0;
            int priceQuantity = rs.getInt("pricequantity");
            if (priceQuantity > 0) {
               reqItemID = price;
               price = 0;
            }

            if (shop != null) {
               shop.addItem(
                  new MapleShopItem(
                     rs.getInt("shopitemid"),
                     (short)1,
                     rs.getShort("quantity"),
                     rs.getInt("itemid"),
                     price,
                     (short)rs.getInt("position"),
                     reqItemID,
                     priceQuantity,
                     (byte)0,
                     rs.getInt("Tab"),
                     0,
                     0,
                     false,
                     coinKey,
                     coinKey > 0 ? rs.getInt("price") : 0,
                     0,
                     0,
                     0,
                     "",
                     0
                  )
               );
            } else {
               shop = new MapleShop(rs.getInt("shopid"), rs.getInt("shopid"));
               shop.addItem(
                  new MapleShopItem(
                     rs.getInt("shopitemid"),
                     (short)1,
                     rs.getShort("quantity"),
                     rs.getInt("itemid"),
                     price,
                     (short)rs.getInt("position"),
                     reqItemID,
                     priceQuantity,
                     (byte)0,
                     rs.getInt("Tab"),
                     0,
                     0,
                     false,
                     coinKey,
                     coinKey > 0 ? rs.getInt("price") : 0,
                     0,
                     0,
                     0,
                     "",
                     0
                  )
               );
               ret.add(shop);
            }
         }

         rs.close();
         ps.close();
      } catch (SQLException var18) {
      }

      Table mainTable = new Table("Shops");

      for (MapleShop shop : ret) {
         try {
            Table newTable = new Table(String.valueOf(shop.getId()));
            int position = 0;

            for (MapleShopItem item : shop.getItems()) {
               Table table = new Table(String.valueOf(position));
               table.put("ItemID", String.valueOf(item.getItemId()));
               table.put("Quantity", String.valueOf(item.getQuantity()));
               table.put("Price", String.valueOf(item.getPrice()));
               table.put("Position", String.valueOf(position++));
               table.put("ReqItem", String.valueOf(item.getReqItem()));
               table.put("ReqItemQ", String.valueOf(item.getReqItemQ()));
               table.put("Category", String.valueOf(item.getCategory()));
               table.put("MinLevel", String.valueOf(item.getMinLevel()));
               table.put("Expiration", String.valueOf(item.getExpiration()));
               table.put("PointQuestExID", String.valueOf(item.getPointQuestExID()));
               table.put("PointPrice", String.valueOf(item.getPointPrice()));
               table.put("BuyLimit", String.valueOf(item.getBuyLimit()));
               table.put("WorldBuyLimit", String.valueOf(item.getWorldBuyLimit()));
               table.put("LimitQuestExID", String.valueOf(item.getLimitQuestExID()));
               table.put("LimitQuestExKey", String.valueOf(item.getLimitQuestExKey()));
               table.put("LimitQuestExValue", String.valueOf(item.getLimitQuestExValue()));
               newTable.putChild(table);
            }

            try {
               newTable.save(Paths.get("./data/Ganglim/Shops/" + shop.getId() + ".data"));
            } catch (Exception var14) {
            }
         } catch (NumberFormatException var16) {
            System.err.println("[몹 셋팅 오류] 몹ID는 숫자로만 입력되어야 합니다.");
         }
      }

      try {
         mainTable.save(Paths.get("Shops.data"));
      } catch (Exception var13) {
      }
   }
}
