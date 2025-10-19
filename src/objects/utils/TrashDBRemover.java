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

public class TrashDBRemover {
   public static void main(String[] args) {
      DBConnection.init();
      DBEventManager.init(5);
      String arg = args[0];
      if (arg != null && !arg.isEmpty()) {
         DBConnection db = new DBConnection();

         try (final Connection con = DBConnection.getConnection()) {
            if (arg.equals("additional_enhance")) {
               System.out.println("`additional_enhance` 테이블 정리 등록이 시작됩니다.");
               DBProcessor dis = DBEventManager.getNextProcessor();
               dis.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "DELETE FROM additional_enhance WHERE unique_id = ?", new DBCallback() {
                  @Override
                  public void execute(PreparedStatement ps) throws SQLException {
                     PreparedStatement p = con.prepareStatement("SELECT `unique_id` FROM additional_enhance");
                     ResultSet r = p.executeQuery();
                     int count = 0;

                     while (r.next()) {
                        int id = r.getInt("unique_id");
                        PreparedStatement b = con.prepareStatement("SELECT `inventoryitemid` FROM `inventoryitems` WHERE inventoryitemid = ?");
                        b.setInt(1, id);
                        ResultSet rs = b.executeQuery();
                        if (!rs.next()) {
                           ps.setInt(1, id);
                           ps.addBatch();
                           count++;
                           if (count > 0 && count % 10000 == 0) {
                              System.out.println("table = `additional_enhance` " + count + "개 등록");
                           }
                        }

                        rs.close();
                        b.close();
                     }

                     r.close();
                     p.close();
                  }
               });
            }

            if (arg.equals("characters")) {
               System.out.println("`characters` 테이블 정리 등록이 시작됩니다.");
               DBProcessor dis = DBEventManager.getNextProcessor();
               dis.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "DELETE FROM characters WHERE accountid = ?", new DBCallback() {
                  @Override
                  public void execute(PreparedStatement ps) throws SQLException {
                     PreparedStatement p = con.prepareStatement("SELECT `accountid` FROM characters");
                     ResultSet r = p.executeQuery();
                     int count = 0;

                     while (r.next()) {
                        int id = r.getInt("accountid");
                        PreparedStatement b = con.prepareStatement("SELECT `id` FROM `accounts` WHERE id = ?");
                        b.setInt(1, id);
                        ResultSet rs = b.executeQuery();
                        if (!rs.next()) {
                           ps.setInt(1, id);
                           ps.addBatch();
                           count++;
                           if (count > 0 && count % 100 == 0) {
                              System.out.println("table = `characters` " + count + "개 등록");
                           }
                        }

                        b.close();
                        rs.close();
                     }

                     p.close();
                     r.close();
                  }
               });
            }

            if (arg.equals("inventoryitems")) {
               System.out.println("`inventoryitems` 테이블 정리 등록이 시작됩니다.");
               DBProcessor dis = DBEventManager.getNextProcessor();
               dis.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "DELETE FROM inventoryitems WHERE inventoryitemid = ?", new DBCallback() {
                  @Override
                  public void execute(PreparedStatement ps) throws SQLException {
                     PreparedStatement p = con.prepareStatement("SELECT `characterid` FROM inventoryitems");
                     ResultSet r = p.executeQuery();
                     int count = 0;

                     while (r.next()) {
                        int id = r.getInt("characterid");
                        PreparedStatement b = con.prepareStatement("SELECT `id` FROM `characters` WHERE id = ?");
                        b.setInt(1, id);
                        ResultSet rs = b.executeQuery();
                        if (!rs.next()) {
                           ps.setInt(1, id);
                           ps.addBatch();
                           count++;
                           if (count > 0 && count % 100 == 0) {
                              System.out.println("table = `inventoryitems` " + count + "개 등록");
                           }
                        }

                        b.close();
                        rs.close();
                     }

                     p.close();
                     r.close();
                  }
               });
            }

            if (arg.equals("inventoryequipment")) {
               System.out.println("`inventoryequipment` 테이블 정리 등록이 시작됩니다.");
               DBProcessor dis = DBEventManager.getNextProcessor();
               dis.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "DELETE FROM inventoryequipment WHERE inventoryitemid = ?", new DBCallback() {
                  @Override
                  public void execute(PreparedStatement ps) throws SQLException {
                     PreparedStatement p = con.prepareStatement("SELECT `inventoryitemid` FROM inventoryequipment");
                     ResultSet r = p.executeQuery();
                     int count = 0;

                     while (r.next()) {
                        long id = r.getLong("inventoryitemid");
                        PreparedStatement b = con.prepareStatement("SELECT `characterid` FROM `inventoryitems` WHERE inventoryitemid = ?");
                        b.setLong(1, id);
                        ResultSet rs = b.executeQuery();
                        if (!rs.next()) {
                           ps.setLong(1, id);
                           ps.addBatch();
                           count++;
                           if (count > 0 && count % 100 == 0) {
                              System.out.println("table = `inventoryequipment` " + count + "개 등록");
                           }
                        }

                        b.close();
                        rs.close();
                     }

                     p.close();
                     r.close();
                  }
               });
            }

            if (arg.equals("inventoryslot")) {
               System.out.println("`inventoryslot` 테이블 정리 등록이 시작됩니다.");
               DBProcessor dis = DBEventManager.getNextProcessor();
               dis.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "DELETE FROM inventoryslot WHERE characterid = ?", new DBCallback() {
                  @Override
                  public void execute(PreparedStatement ps) throws SQLException {
                     PreparedStatement p = con.prepareStatement("SELECT `characterid` FROM inventoryslot");
                     ResultSet r = p.executeQuery();
                     int count = 0;

                     while (r.next()) {
                        int id = r.getInt("characterid");
                        PreparedStatement b = con.prepareStatement("SELECT `id` FROM `characters` WHERE id = ?");
                        b.setInt(1, id);
                        ResultSet rs = b.executeQuery();
                        if (!rs.next()) {
                           ps.setInt(1, id);
                           ps.addBatch();
                           count++;
                           if (count > 0 && count % 100 == 0) {
                              System.out.println("table = `inventoryslot` " + count + "개 등록");
                           }
                        }

                        b.close();
                        rs.close();
                     }

                     p.close();
                     r.close();
                  }
               });
            }

            if (arg.equals("keymap")) {
               System.out.println("`keymap` 테이블 정리 등록이 시작됩니다.");
               DBProcessor dis = DBEventManager.getNextProcessor();
               dis.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "DELETE FROM keymap WHERE characterid = ?", new DBCallback() {
                  @Override
                  public void execute(PreparedStatement ps) throws SQLException {
                     PreparedStatement p = con.prepareStatement("SELECT `characterid` FROM keymap");
                     ResultSet r = p.executeQuery();
                     int count = 0;

                     while (r.next()) {
                        int id = r.getInt("characterid");
                        PreparedStatement b = con.prepareStatement("SELECT `id` FROM keymap WHERE characterid = ?");
                        b.setInt(1, id);
                        ResultSet rs = b.executeQuery();
                        if (!rs.next()) {
                           ps.setInt(1, id);
                           ps.addBatch();
                           count++;
                           if (count > 0 && count % 100 == 0) {
                              System.out.println("table = `keymap` " + count + "개 등록");
                           }
                        }

                        b.close();
                        rs.close();
                     }

                     p.close();
                     r.close();
                  }
               });
            }

            if (arg.equals("keyvalue")) {
               System.out.println("`keyvalue` 테이블 정리 등록이 시작됩니다.");
               DBProcessor dis = DBEventManager.getNextProcessor();
               dis.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "DELETE FROM keyvalue WHERE cid = ?", new DBCallback() {
                  @Override
                  public void execute(PreparedStatement ps) throws SQLException {
                     PreparedStatement p = con.prepareStatement("SELECT `cid` FROM keyvalue");
                     ResultSet r = p.executeQuery();
                     int count = 0;

                     while (r.next()) {
                        int id = r.getInt("cid");
                        PreparedStatement b = con.prepareStatement("SELECT `level` FROM `characters` WHERE id = ?");
                        b.setInt(1, id);
                        ResultSet rs = b.executeQuery();
                        if (!rs.next()) {
                           ps.setInt(1, id);
                           ps.addBatch();
                           count++;
                           if (count > 0 && count % 100 == 0) {
                              System.out.println("table = `keyvalue` " + count + "개 등록");
                           }
                        }

                        b.close();
                        rs.close();
                     }

                     p.close();
                     r.close();
                  }
               });
            }

            if (arg.equals("keyvalue2")) {
               System.out.println("`keyvalue2` 테이블 정리 등록이 시작됩니다.");
               DBProcessor dis = DBEventManager.getNextProcessor();
               dis.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "DELETE FROM keyvalue2 WHERE cid = ?", new DBCallback() {
                  @Override
                  public void execute(PreparedStatement ps) throws SQLException {
                     PreparedStatement p = con.prepareStatement("SELECT `cid` FROM keyvalue2");
                     ResultSet r = p.executeQuery();
                     int count = 0;

                     while (r.next()) {
                        int id = r.getInt("cid");
                        PreparedStatement b = con.prepareStatement("SELECT `level` FROM `characters` WHERE id = ?");
                        b.setInt(1, id);
                        ResultSet rs = b.executeQuery();
                        if (!rs.next()) {
                           ps.setInt(1, id);
                           ps.addBatch();
                           count++;
                           if (count > 0 && count % 100 == 0) {
                              System.out.println("table = `keyvalue2` " + count + "개 등록");
                           }
                        }

                        b.close();
                        rs.close();
                     }

                     p.close();
                     r.close();
                  }
               });
            }

            if (arg.equals("mountdata")) {
               System.out.println("`mountdata` 테이블 정리 등록이 시작됩니다.");
               DBProcessor dis = DBEventManager.getNextProcessor();
               dis.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "DELETE FROM mountdata WHERE characterid = ?", new DBCallback() {
                  @Override
                  public void execute(PreparedStatement ps) throws SQLException {
                     PreparedStatement p = con.prepareStatement("SELECT `characterid` FROM mountdata");
                     ResultSet r = p.executeQuery();
                     int count = 0;

                     while (r.next()) {
                        int id = r.getInt("characterid");
                        PreparedStatement b = con.prepareStatement("SELECT `level` FROM `characters` WHERE id = ?");
                        b.setInt(1, id);
                        ResultSet rs = b.executeQuery();
                        if (!rs.next()) {
                           ps.setInt(1, id);
                           ps.addBatch();
                           count++;
                           if (count > 0 && count % 100 == 0) {
                              System.out.println("table = `mountdata` " + count + "개 등록");
                           }
                        }

                        b.close();
                        rs.close();
                     }

                     p.close();
                     r.close();
                  }
               });
            }

            if (arg.equals("skills")) {
               System.out.println("`skills` 테이블 정리 등록이 시작됩니다.");
               DBProcessor dis = DBEventManager.getNextProcessor();
               dis.addQuery(DBSelectionKey.INSERT_OR_UPDATE_BATCH, "DELETE FROM skills WHERE characterid = ?", new DBCallback() {
                  @Override
                  public void execute(PreparedStatement ps) throws SQLException {
                     PreparedStatement p = con.prepareStatement("SELECT `characterid` FROM skills");
                     ResultSet r = p.executeQuery();
                     int count = 0;

                     while (r.next()) {
                        int id = r.getInt("characterid");
                        PreparedStatement b = con.prepareStatement("SELECT `level` FROM `characters` WHERE id = ?");
                        b.setInt(1, id);
                        ResultSet rs = b.executeQuery();
                        if (!rs.next()) {
                           ps.setInt(1, id);
                           ps.addBatch();
                           count++;
                           if (count > 0 && count % 100 == 0) {
                              System.out.println("table = `skills` " + count + "개 등록");
                           }
                        }

                        b.close();
                        rs.close();
                     }

                     p.close();
                     r.close();
                  }
               });
            }
         } catch (Throwable var8) {
            System.out.println("TrashDB Err");
            var8.printStackTrace();
         }
      }
   }
}
