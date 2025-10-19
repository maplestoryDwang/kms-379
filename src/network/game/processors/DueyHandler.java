package network.game.processors;

import constants.GameConstants;
import database.DBConnection;
import database.loader.ItemLoader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import network.center.Center;
import network.decode.PacketDecoder;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import objects.MapleDueyActions;
import objects.item.Item;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.users.MapleCharacter;
import objects.users.MapleCharacterUtil;
import objects.users.MapleClient;
import objects.users.enchant.ItemFlag;
import objects.utils.Pair;

public class DueyHandler {
   public static final void DueyOperation(PacketDecoder slea, MapleClient c) {
      byte operation = slea.readByte();
      switch (operation) {
         case 1:
            String secondPassword = slea.readMapleAsciiString();
            if (c.CheckSecondPassword(secondPassword)) {
               slea.skip(4);
               int conv = c.getPlayer().getConversation();
               if (conv == 2) {
                  List<MapleDueyActions> list1 = new ArrayList<>();
                  List<MapleDueyActions> list2 = new ArrayList<>();

                  for (MapleDueyActions dp : loadItems(c.getPlayer())) {
                     if (dp.isExpire()) {
                        list2.add(dp);
                     } else {
                        list1.add(dp);
                     }
                  }

                  c.getSession().writeAndFlush(CField.sendDuey((byte) 10, list1, list2));

                  for (MapleDueyActions dpx : list2) {
                     removeItemFromDB(dpx.getPackageId(), c.getPlayer().getId());
                  }
               }
            } else {
               c.getSession().writeAndFlush(CField.checkFailedDuey());
            }
            break;
         case 2:
         case 4:
         case 7:
         default:
            System.out.println("Unhandled Duey operation : " + slea.toString());
            break;
         case 3:
            if (c.getPlayer().getConversation() != 2) {
               return;
            }

            byte inventId = slea.readByte();
            short itemPos = slea.readShort();
            short amount = slea.readShort();
            int mesos = slea.readInt();
            String recipient = slea.readMapleAsciiString();
            boolean quickdelivery = slea.readByte() > 0;
            String letter = "";
            int qq = 0;
            if (quickdelivery) {
               if (!c.getPlayer().haveItem(5330000, 1) && !c.getPlayer().haveItem(5330001, 1)) {
                  return;
               }

               letter = slea.readMapleAsciiString();
               qq = slea.readInt();
            }

            long finalcost = mesos + GameConstants.getTaxAmount(mesos) + (quickdelivery ? 0 : 5000);
            if (mesos >= 0 && mesos <= 100000000 && c.getPlayer().getMeso() >= finalcost) {
               int accid = MapleCharacterUtil.getAccByName(recipient);
               int cid = MapleCharacterUtil.getIdByName(recipient);
               if (accid == -1) {
                  c.getSession().writeAndFlush(CField.sendDuey((byte) 14, null, null));
                  break;
               }

               if (accid != c.getAccID()) {
                  boolean recipientOn = false;
                  MapleClient rClient = null;
                  int channel = Center.Find.findChannel(recipient);
                  if (channel > -1) {
                     recipientOn = true;
                     GameServer rcserv = GameServer.getInstance(channel);
                     rClient = rcserv.getPlayerStorage().getCharacterByName(recipient).getClient();
                  }

                  if (inventId > 0) {
                     MapleInventoryType inv = MapleInventoryType.getByType(inventId);
                     Item item = c.getPlayer().getInventory(inv).getItem(itemPos);
                     if (item == null) {
                        c.getSession().writeAndFlush(CField.sendDuey((byte) 17, null, null));
                        return;
                     }

                     for (MapleDueyActions mda : loadItems(c.getPlayer())) {
                        if (mda.getItem() != null) {
                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           if (ii.isPickupRestricted(mda.getItem().getItemId())
                                 && mda.getItem().getItemId() == item.getItemId()) {
                              c.getSession().writeAndFlush(CField.sendDuey((byte) 18, null, null));
                              return;
                           }
                        }
                     }

                     int flag = item.getFlag();
                     if (ItemFlag.POSSIBLE_TRADING.check(flag) || ItemFlag.PROTECTED.check(flag)) {
                        c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     if ((GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId()))
                           && item.getQuantity() == 0) {
                        amount = 0;
                     }

                     if (c.getPlayer().getItemQuantity(item.getItemId(), false) >= amount) {
                        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                        if (!ii.isDropRestricted(item.getItemId()) && !ii.isAccountShared(item.getItemId())) {
                           Item toSend = item.copy();
                           if (!GameConstants.isThrowingStar(toSend.getItemId())
                                 && !GameConstants.isBullet(toSend.getItemId())) {
                              toSend.setQuantity(amount);
                           }

                           if (addItemToDB(toSend, mesos, c.getPlayer().getName(), cid, recipientOn, letter, qq,
                                 quickdelivery)) {
                              if (!GameConstants.isThrowingStar(toSend.getItemId())
                                    && !GameConstants.isBullet(toSend.getItemId())) {
                                 MapleInventoryManipulator.removeFromSlot(c, inv, (byte) itemPos, amount, true, false);
                              } else {
                                 MapleInventoryManipulator.removeFromSlot(c, inv, (byte) itemPos, toSend.getQuantity(),
                                       true, false);
                              }

                              if (quickdelivery) {
                                 if (c.getPlayer().haveItem(5330001, 1)) {
                                    MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5330001, 1, false,
                                          false);
                                 } else if (c.getPlayer().haveItem(5330000, 1)) {
                                    MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5330000, 1, false,
                                          false);
                                 }
                              }

                              c.getPlayer().gainMeso(-finalcost, false);
                              c.getSession().writeAndFlush(CField.sendDuey((byte) 19, null, null));
                           } else {
                              c.getSession().writeAndFlush(CField.sendDuey((byte) 17, null, null));
                           }
                        } else {
                           c.getSession().writeAndFlush(CField.sendDuey((byte) 17, null, null));
                        }
                     } else {
                        c.getSession().writeAndFlush(CField.sendDuey((byte) 17, null, null));
                     }
                  } else if (addMesoToDB(mesos, c.getPlayer().getName(), cid, recipientOn, letter, quickdelivery)) {
                     c.getPlayer().gainMeso(-finalcost, false);
                     if (quickdelivery) {
                        if (c.getPlayer().haveItem(5330001, 1)) {
                           MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5330001, 1, false, false);
                        } else if (c.getPlayer().haveItem(5330000, 1)) {
                           MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5330000, 1, false, false);
                        }
                     }

                     c.getSession().writeAndFlush(CField.sendDuey((byte) 19, null, null));
                  } else {
                     c.getSession().writeAndFlush(CField.sendDuey((byte) 17, null, null));
                  }

                  if (recipientOn && rClient != null && quickdelivery) {
                     rClient.getSession().writeAndFlush(CField.receiveParcel(c.getPlayer().getName(), quickdelivery));
                  }
                  break;
               }

               c.getSession().writeAndFlush(CField.sendDuey((byte) 15, null, null));
               break;
            }

            c.getSession().writeAndFlush(CField.sendDuey((byte) 12, null, null));
            break;
         case 5: {
            if (c.getPlayer().getConversation() != 2) {
               return;
            }

            int packageid = slea.readInt();
            MapleDueyActions dpx = loadSingleItem(packageid, c.getPlayer().getId());
            if (dpx == null) {
               return;
            }

            if (dpx.isExpire() || !dpx.canReceive()) {
               return;
            }

            if (dpx.getItem() != null
                  && !MapleInventoryManipulator.checkSpace(c, dpx.getItem().getItemId(), dpx.getItem().getQuantity(),
                        dpx.getItem().getOwner())) {
               c.getSession().writeAndFlush(CField.sendDuey((byte) 16, null, null));
               return;
            }

            if (dpx.getMesos() < 0 || dpx.getMesos() + c.getPlayer().getMeso() < 0L) {
               c.getSession().writeAndFlush(CField.sendDuey((byte) 17, null, null));
               return;
            }

            if (dpx.getItem() != null
                  && c.getPlayer().haveItem(dpx.getItem().getItemId(), 1, true, true)
                  && MapleItemInformationProvider.getInstance().isPickupRestricted(dpx.getItem().getItemId())) {
               c.getSession().writeAndFlush(CField.sendDuey((byte) 18, null, null));
               return;
            }

            removeItemFromDB(packageid, c.getPlayer().getId());
            if (dpx.getItem() != null) {
               MapleInventoryManipulator.addFromDrop(c, dpx.getItem(), false);
            }

            if (dpx.getMesos() != 0) {
               c.getPlayer().gainMeso(dpx.getMesos(), false);
            }

            c.getSession().writeAndFlush(CField.removeItemFromDuey(false, packageid));
         }
            break;
         case 6: {
            if (c.getPlayer().getConversation() != 2) {
               return;
            }

            int packageid = slea.readInt();
            removeItemFromDB(packageid, c.getPlayer().getId());
            c.getSession().writeAndFlush(CField.removeItemFromDuey(true, packageid));
         }
            break;
         case 8:
            c.getPlayer().setConversation(0);
      }
   }

   private static final boolean addMesoToDB(int mesos, String sName, int recipientID, boolean isOn, String content,
         boolean quick) {
      DBConnection db = new DBConnection();

      try {
         boolean var9;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                  "INSERT INTO dueypackages (RecieverId, SenderName, Mesos, TimeStamp, Checked, Type, `Quick`, content) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, recipientID);
            ps.setString(2, sName);
            ps.setInt(3, mesos);
            ps.setLong(4, System.currentTimeMillis());
            ps.setInt(5, isOn ? 0 : 1);
            ps.setInt(6, 3);
            ps.setInt(7, quick ? 1 : 0);
            ps.setString(8, content);
            ps.executeUpdate();
            ps.close();
            var9 = true;
         }

         return var9;
      } catch (SQLException var12) {
         var12.printStackTrace();
         return false;
      }
   }

   private static final boolean addItemToDB(Item item, int mesos, String sName, int recipientID, boolean isOn,
         String content, int qq, boolean Quick) {
      DBConnection db = new DBConnection();

      try {
         boolean var12;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                  "INSERT INTO dueypackages (RecieverId, SenderName, Mesos, TimeStamp, Checked, Type, content, `Quick`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                  1);
            ps.setInt(1, recipientID);
            ps.setString(2, sName);
            ps.setInt(3, mesos);
            ps.setLong(4, System.currentTimeMillis());
            ps.setInt(5, isOn ? 0 : 1);
            ps.setInt(6, item.getType());
            ps.setString(7, content);
            ps.setInt(8, Quick ? 1 : 0);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
               ItemLoader.DUEY.saveItems(
                     Collections.singletonList(new Pair<>(item, GameConstants.getInventoryType(item.getItemId()))), con,
                     rs.getInt(1));
            }

            rs.close();
            ps.close();
            var12 = true;
         }

         return var12;
      } catch (SQLException var15) {
         var15.printStackTrace();
         return false;
      }
   }

   public static final List<MapleDueyActions> loadItems(MapleCharacter chr) {
      List<MapleDueyActions> packages = new LinkedList<>();
      DBConnection db = new DBConnection();

      try {
         Object var10;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM dueypackages WHERE RecieverId = ?");
            ps.setInt(1, chr.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
               MapleDueyActions dueypack = getItemByPID(rs.getInt("packageid"));
               dueypack.setSender(rs.getString("SenderName"));
               dueypack.setMesos(rs.getInt("Mesos"));
               dueypack.setSentTime(rs.getLong("TimeStamp"));
               dueypack.setContent(rs.getString("content"));
               dueypack.setQuick(rs.getInt("Quick") > 0);
               packages.add(dueypack);
            }

            rs.close();
            ps.close();
            var10 = packages;
         }

         return (List<MapleDueyActions>) var10;
      } catch (SQLException var9) {
         var9.printStackTrace();
         return null;
      }
   }

   public static final MapleDueyActions loadSingleItem(int packageid, int charid) {
      List<MapleDueyActions> packages = new LinkedList<>();
      DBConnection db = new DBConnection();

      try {
         MapleDueyActions dueypack;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con
                  .prepareStatement("SELECT * FROM dueypackages WHERE PackageId = ? and RecieverId = ?");
            ps.setInt(1, packageid);
            ps.setInt(2, charid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
               dueypack = getItemByPID(packageid);
               dueypack.setSender(rs.getString("SenderName"));
               dueypack.setMesos(rs.getInt("Mesos"));
               dueypack.setSentTime(rs.getLong("TimeStamp"));
               dueypack.setContent(rs.getString("content"));
               dueypack.setQuick(rs.getInt("Quick") > 0);
               packages.add(dueypack);
               return dueypack;
            }

            rs.close();
            ps.close();
            dueypack = null;
         }

         return dueypack;
      } catch (SQLException var11) {
         return null;
      }
   }

   public static final void reciveMsg(MapleClient c, int recipientId) {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("UPDATE dueypackages SET Checked = 0 where RecieverId = ?");
         ps.setInt(1, recipientId);
         ps.executeUpdate();
         ps.close();
      } catch (SQLException var8) {
         var8.printStackTrace();
      }
   }

   private static final void removeItemFromDB(int packageid, int charid) {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("DELETE FROM dueypackages WHERE PackageId = ? and RecieverId = ?");
         ps.setInt(1, packageid);
         ps.setInt(2, charid);
         ps.executeUpdate();
         ps.close();
      } catch (SQLException var8) {
         var8.printStackTrace();
      }
   }

   private static final MapleDueyActions getItemByPID(int packageid) {
      try {
         Map<Long, Pair<Item, MapleInventoryType>> iter = ItemLoader.DUEY.loadItems(false, packageid, 0);
         if (iter != null && iter.size() > 0) {
            Iterator var2 = iter.values().iterator();
            if (var2.hasNext()) {
               Pair<Item, MapleInventoryType> i = (Pair<Item, MapleInventoryType>) var2.next();
               return new MapleDueyActions(packageid, i.getLeft());
            }
         }
      } catch (Exception var4) {
         System.out.println("듀이 아이템 오류 발생");
         var4.printStackTrace();
      }

      return new MapleDueyActions(packageid);
   }
}
