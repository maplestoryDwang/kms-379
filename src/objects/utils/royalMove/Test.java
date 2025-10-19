package objects.utils.royalMove;

import database.DBConnection;
import database.DBEventManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import objects.item.MapleItemInformationProvider;

public class Test {
   public static void main(String[] args) {
      DBConnection.init();
      DBEventManager.init(5);
      System.setProperty("net.sf.odinms.wzpath", "wz");
      MapleItemInformationProvider.getInstance().runItems();
      String[] var10000 = new String[]{"inventoryequipment", "auctionequipment", "cabinet_equipment"};

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM `inventoryequipment`");
         ResultSet rs = ps.executeQuery();
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

         while (rs.next()) {
            int itemID = rs.getInt("item_id");
            int tuc = ii.getSlots(itemID);
            Test.FuckingHacker fuckingHacker = new Test.FuckingHacker(
               rs.getInt("player_id"),
               rs.getInt("account_id"),
               rs.getInt("item_id"),
               tuc,
               rs.getInt("upgradeslots"),
               rs.getByte("level"),
               rs.getInt("ViciousHammer"),
               rs.getLong("inventoryitemid")
            );
            PreparedStatement ps2 = con.prepareStatement("SELECT `name`, `accountid` FROM `characters` WHERE `id` = ?");
            ps2.setInt(1, fuckingHacker.playerID);
            ResultSet rs2 = ps2.executeQuery();
            int accountID = 0;
            String name = "";
            if (rs2.next()) {
               accountID = rs2.getInt("accountid");
               name = rs2.getString("name");
            }

            int totalTuc = fuckingHacker.tuc + fuckingHacker.vicious;
            if (totalTuc < fuckingHacker.level) {
               System.out
                  .println(
                     "악용 발견! accountID : "
                        + accountID
                        + ", playerID : "
                        + fuckingHacker.playerID
                        + ", 이름 : "
                        + name
                        + ", 아이템ID : "
                        + fuckingHacker.itemID
                        + ", 강화 레벨 : + "
                        + fuckingHacker.level
                        + ", 넘어간 수치 : "
                        + (fuckingHacker.level - totalTuc)
                        + ", 인벤토리ID : "
                        + fuckingHacker.inventoryID
                  );
            }

            rs2.close();
            ps2.close();
         }

         rs.close();
         ps.close();
      } catch (SQLException var16) {
         var16.printStackTrace();
      }
   }

   public static class FuckingHacker {
      int playerID;
      int accountID;
      int itemID;
      int tuc;
      int ruc;
      int level;
      int vicious;
      long inventoryID;

      public FuckingHacker(int playerID, int accountID, int itemID, int tuc, int ruc, int level, int vicious, long inventoryID) {
         this.playerID = playerID;
         this.accountID = accountID;
         this.itemID = itemID;
         this.tuc = tuc;
         this.ruc = ruc;
         this.level = level;
         this.vicious = vicious;
         this.inventoryID = inventoryID;
      }
   }
}
