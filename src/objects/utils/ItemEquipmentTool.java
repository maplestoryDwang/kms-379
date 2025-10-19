package objects.utils;

import database.DBConnection;
import database.DBEventManager;
import database.DBProcessor;
import database.DBSelectionKey;
import database.callback.DBCallback;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemEquipmentTool {
   public static void main(String[] args) {
      DBConnection.init();
      DBEventManager.init(6);
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         System.out.println("[알림] inventoryequipment 작업이 시작됩니다.");
         ps = con.prepareStatement("SELECT * FROM inventoryequipment");
         System.out.println("[알림] inventoryequipment 데이터 수집을 시작합니다. 시간이 다소 소요될 수 있습니다.");
         rs = ps.executeQuery();
         System.out.println("[알림] inventoryequipment 데이터 수집이 완료되었습니다. 작업이 시작됩니다.");
         int count = 0;

         while (rs.next()) {
            PreparedStatement ps2 = con.prepareStatement("SELECT * FROM inventoryitems WHERE `inventoryitemid` = ?");
            ps2.setInt(1, rs.getInt("inventoryitemid"));
            ResultSet rs2 = ps2.executeQuery();

            while (rs2.next()) {
               final int characterID = rs2.getInt("characterid");
               final int accountID = rs2.getInt("accountid");
               final int itemID = rs2.getInt("itemid");
               final int inventoryID = rs.getInt("inventoryitemid");
               DBProcessor db = DBEventManager.getNextProcessor();
               db.addQuery(
                  DBSelectionKey.INSERT_OR_UPDATE,
                  "UPDATE inventoryequipment SET `player_id` = ?, `account_id` = ?, `item_id` = ? WHERE `inventoryitemid` = ?",
                  new DBCallback() {
                     @Override
                     public void execute(PreparedStatement ps) throws SQLException {
                        ps.setInt(1, characterID);
                        ps.setInt(2, accountID);
                        ps.setInt(3, itemID);
                        ps.setInt(4, inventoryID);
                        ps.addBatch();
                     }
                  }
               );
            }

            if (++count % 10000 == 0) {
               System.out.println("[알림] inventoryequipment " + count + "행의 작업이 완료되었습니다.");
            }

            rs2.close();
            ps2.close();
         }

         System.out.println("[알림] auctionequipment 작업이 시작됩니다.");
         ps = con.prepareStatement("SELECT * FROM auctionequipment");
         System.out.println("[알림] auctionequipment 데이터 수집을 시작합니다. 시간이 다소 소요될 수 있습니다.");
         rs = ps.executeQuery();
         System.out.println("[알림] auctionequipment 데이터 수집이 완료되었습니다. 작업이 시작됩니다.");
         count = 0;

         while (rs.next()) {
            PreparedStatement ps2 = con.prepareStatement("SELECT * FROM auctionitems WHERE `inventoryitemid` = ?");
            ps2.setInt(1, rs.getInt("inventoryitemid"));
            ResultSet rs2 = ps2.executeQuery();

            while (rs2.next()) {
               final int characterID = rs2.getInt("characterid");
               final int accountID = rs2.getInt("accountid");
               final int itemID = rs2.getInt("itemid");
               final int inventoryID = rs.getInt("inventoryitemid");
               DBProcessor db = DBEventManager.getNextProcessor();
               db.addQuery(
                  DBSelectionKey.INSERT_OR_UPDATE,
                  "UPDATE auctionequipment SET `player_id` = ?, `account_id` = ?, `item_id` = ? WHERE `inventoryitemid` = ?",
                  new DBCallback() {
                     @Override
                     public void execute(PreparedStatement ps) throws SQLException {
                        ps.setInt(1, characterID);
                        ps.setInt(2, accountID);
                        ps.setInt(3, itemID);
                        ps.setInt(4, inventoryID);
                        ps.addBatch();
                     }
                  }
               );
            }

            if (++count % 10000 == 0) {
               System.out.println("[알림] auctionequipment " + count + "행의 작업이 완료되었습니다.");
            }

            rs2.close();
            ps2.close();
         }

         System.out.println("[알림] cabinet_equipment 작업이 시작됩니다.");
         ps = con.prepareStatement("SELECT * FROM cabinet_equipment");
         System.out.println("[알림] cabinet_equipment 데이터 수집을 시작합니다. 시간이 다소 소요될 수 있습니다.");
         rs = ps.executeQuery();
         System.out.println("[알림] cabinet_equipment 데이터 수집이 완료되었습니다. 작업이 시작됩니다.");
         count = 0;

         while (rs.next()) {
            PreparedStatement ps2 = con.prepareStatement("SELECT * FROM cabinet_items WHERE `inventoryitemid` = ?");
            ps2.setInt(1, rs.getInt("inventoryitemid"));
            ResultSet rs2 = ps2.executeQuery();

            while (rs2.next()) {
               final int characterID = rs2.getInt("characterid");
               final int accountID = rs2.getInt("accountid");
               final int itemID = rs2.getInt("itemid");
               final int inventoryID = rs.getInt("inventoryitemid");
               DBProcessor db = DBEventManager.getNextProcessor();
               db.addQuery(
                  DBSelectionKey.INSERT_OR_UPDATE,
                  "UPDATE cabinet_equipment SET `player_id` = ?, `account_id` = ?, `item_id` = ? WHERE `inventoryitemid` = ?",
                  new DBCallback() {
                     @Override
                     public void execute(PreparedStatement ps) throws SQLException {
                        ps.setInt(1, characterID);
                        ps.setInt(2, accountID);
                        ps.setInt(3, itemID);
                        ps.setInt(4, inventoryID);
                        ps.addBatch();
                     }
                  }
               );
            }

            if (++count % 10000 == 0) {
               System.out.println("[알림] cabinet_equipment " + count + "행의 작업이 완료되었습니다.");
            }

            rs2.close();
            ps2.close();
         }

         System.out.println("[알림] csequipment 작업이 시작됩니다.");
         ps = con.prepareStatement("SELECT * FROM csequipment");
         System.out.println("[알림] csequipment 데이터 수집을 시작합니다. 시간이 다소 소요될 수 있습니다.");
         rs = ps.executeQuery();
         System.out.println("[알림] csequipment 데이터 수집이 완료되었습니다. 작업이 시작됩니다.");
         count = 0;

         while (rs.next()) {
            PreparedStatement ps2 = con.prepareStatement("SELECT * FROM csitems WHERE `inventoryitemid` = ?");
            ps2.setInt(1, rs.getInt("inventoryitemid"));
            ResultSet rs2 = ps2.executeQuery();

            while (rs2.next()) {
               final int characterID = rs2.getInt("characterid");
               final int accountID = rs2.getInt("accountid");
               final int itemID = rs2.getInt("itemid");
               final int inventoryID = rs.getInt("inventoryitemid");
               DBProcessor db = DBEventManager.getNextProcessor();
               db.addQuery(
                  DBSelectionKey.INSERT_OR_UPDATE,
                  "UPDATE csequipment SET `player_id` = ?, `account_id` = ?, `item_id` = ? WHERE `inventoryitemid` = ?",
                  new DBCallback() {
                     @Override
                     public void execute(PreparedStatement ps) throws SQLException {
                        ps.setInt(1, characterID);
                        ps.setInt(2, accountID);
                        ps.setInt(3, itemID);
                        ps.setInt(4, inventoryID);
                        ps.addBatch();
                     }
                  }
               );
            }

            if (++count % 10000 == 0) {
               System.out.println("[알림] csequipment " + count + "행의 작업이 완료되었습니다.");
            }

            rs2.close();
            ps2.close();
         }

         System.out.println("[알림] 작업이 완료되었습니다.");
      } catch (SQLException var15) {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var16 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var17 = null;
            }
         } catch (SQLException var12) {
         }
      }
   }
}
