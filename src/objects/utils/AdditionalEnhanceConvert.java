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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AdditionalEnhanceConvert {
   public static void main(String[] args) {
      DBConnection.init();
      DBEventManager.init(20);
      DBConnection db = new DBConnection();
      final AtomicInteger count = new AtomicInteger();

      try (final Connection con = DBConnection.getConnection()) {
         final DBProcessor dis = DBEventManager.getNextProcessor();
         dis.addQuery(
            DBSelectionKey.INSERT_OR_UPDATE_BATCH,
            "DELETE FROM additional_enhance WHERE unique_id = ?",
            new DBCallback() {
               @Override
               public void execute(PreparedStatement ps) throws SQLException {
                  PreparedStatement d = con.prepareStatement("SELECT * FROM `additional_enhance`");
                  ResultSet rs = d.executeQuery();

                  while (rs.next()) {
                     final long inventoryID = rs.getLong("unique_id");
                     final int grade = rs.getInt("grade");
                     final int attack = rs.getInt("attack");
                     final int allStat = rs.getInt("allStat");
                     final AtomicBoolean find = new AtomicBoolean(false);
                     PreparedStatement ps2 = con.prepareStatement("SELECT * FROM `auctionequipment` WHERE `inventoryitemid` = ?");
                     ps2.setLong(1, inventoryID);
                     ResultSet rs2 = ps2.executeQuery();

                     while (rs2.next()) {
                        dis.addQuery(
                           DBSelectionKey.INSERT_OR_UPDATE_BATCH,
                           "UPDATE `auctionequipment` SET `sp_grade` = ?, `sp_attack` = ?, `sp_all_stat` = ? WHERE `inventoryitemid` = ?",
                           new DBCallback() {
                              @Override
                              public void execute(PreparedStatement dd) throws SQLException {
                                 dd.setInt(1, grade);
                                 dd.setInt(2, attack);
                                 dd.setInt(3, allStat);
                                 dd.setLong(4, inventoryID);
                                 dd.addBatch();
                                 find.set(true);
                              }
                           }
                        );
                     }

                     rs2.close();
                     ps2.close();
                     if (!find.get()) {
                        ps2 = con.prepareStatement("SELECT * FROM `cabinet_equipment` WHERE `inventoryitemid` = ?");
                        ps2.setLong(1, inventoryID);
                        rs2 = ps2.executeQuery();

                        while (rs2.next()) {
                           dis.addQuery(
                              DBSelectionKey.INSERT_OR_UPDATE_BATCH,
                              "UPDATE `cabinet_equipment` SET `sp_grade` = ?, `sp_attack` = ?, `sp_all_stat` = ? WHERE `inventoryitemid` = ?",
                              new DBCallback() {
                                 @Override
                                 public void execute(PreparedStatement dd) throws SQLException {
                                    dd.setInt(1, grade);
                                    dd.setInt(2, attack);
                                    dd.setInt(3, allStat);
                                    dd.setLong(4, inventoryID);
                                    dd.addBatch();
                                    find.set(true);
                                 }
                              }
                           );
                        }
                     }

                     rs2.close();
                     ps2.close();
                     if (!find.get()) {
                        ps2 = con.prepareStatement("SELECT * FROM `csequipment` WHERE `inventoryitemid` = ?");
                        ps2.setLong(1, inventoryID);
                        rs2 = ps2.executeQuery();

                        while (rs2.next()) {
                           dis.addQuery(
                              DBSelectionKey.INSERT_OR_UPDATE_BATCH,
                              "UPDATE `csequipment` SET `sp_grade` = ?, `sp_attack` = ?, `sp_all_stat` = ? WHERE `inventoryitemid` = ?",
                              new DBCallback() {
                                 @Override
                                 public void execute(PreparedStatement dd) throws SQLException {
                                    dd.setInt(1, grade);
                                    dd.setInt(2, attack);
                                    dd.setInt(3, allStat);
                                    dd.setLong(4, inventoryID);
                                    dd.addBatch();
                                    find.set(true);
                                 }
                              }
                           );
                        }
                     }

                     rs2.close();
                     ps2.close();
                     if (!find.get()) {
                        ps2 = con.prepareStatement("SELECT * FROM `inventoryequipment` WHERE `inventoryitemid` = ?");
                        ps2.setLong(1, inventoryID);
                        rs2 = ps2.executeQuery();

                        while (rs2.next()) {
                           dis.addQuery(
                              DBSelectionKey.INSERT_OR_UPDATE_BATCH,
                              "UPDATE `inventoryequipment` SET `sp_grade` = ?, `sp_attack` = ?, `sp_all_stat` = ? WHERE `inventoryitemid` = ?",
                              new DBCallback() {
                                 @Override
                                 public void execute(PreparedStatement dd) throws SQLException {
                                    dd.setInt(1, grade);
                                    dd.setInt(2, attack);
                                    dd.setInt(3, allStat);
                                    dd.setLong(4, inventoryID);
                                    dd.addBatch();
                                    find.set(true);
                                 }
                              }
                           );
                        }
                     }

                     rs2.close();
                     ps2.close();
                     ps.setLong(1, inventoryID);
                     ps.addBatch();
                     if (count.addAndGet(1) % 1000 == 0) {
                        System.out.println(count.get() + "개의 작업이 수행되었습니다.");
                     }
                  }
               }
            }
         );
      } catch (SQLException var8) {
      }
   }
}
