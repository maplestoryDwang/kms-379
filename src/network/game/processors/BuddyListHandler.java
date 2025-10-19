package network.game.processors;

import constants.devtempConstants.DummyCharacterName;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import network.center.Center;
import network.decode.PacketDecoder;
import network.models.CWvsContext;
import objects.context.friend.Friend;
import objects.context.friend.FriendEntry;
import objects.quest.MapleQuest;
import objects.users.CharacterNameAndId;
import objects.users.MapleCharacter;
import objects.users.MapleCharacterUtil;
import objects.users.MapleClient;

public class BuddyListHandler {
   private static final void nextPendingRequest(MapleClient c) {
      CharacterNameAndId pendingBuddyRequest = c.getPlayer().getBuddylist().pollPendingRequest();
      if (pendingBuddyRequest != null) {
         c.getSession()
            .writeAndFlush(
               CWvsContext.BuddylistPacket.requestBuddylistAdd(
                  pendingBuddyRequest.getId(),
                  pendingBuddyRequest.getAccId(),
                  pendingBuddyRequest.getName(),
                  pendingBuddyRequest.getLevel(),
                  pendingBuddyRequest.getJob(),
                  c,
                  pendingBuddyRequest.getGroupName(),
                  pendingBuddyRequest.getMemo()
               )
            );
      }
   }

   public static final BuddyListHandler.CharacterIdNameBuddyCapacity getCharacterIdAndNameFromDatabase(String name, String groupname, String memo) throws SQLException {
      DBConnection db = new DBConnection();
      Connection con = DBConnection.getConnection();
      PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE name LIKE ?");
      ps.setString(1, name);
      ResultSet rs = ps.executeQuery();
      BuddyListHandler.CharacterIdNameBuddyCapacity ret = null;
      if (rs.next()) {
         ret = new BuddyListHandler.CharacterIdNameBuddyCapacity(
            rs.getInt("id"), rs.getInt("accountid"), name, rs.getInt("level"), rs.getInt("job"), rs.getInt("buddyCapacity"), groupname, memo
         );
      }

      rs.close();
      ps.close();
      con.close();
      return ret;
   }

   public static final void allowBuddyAdd(PacketDecoder slea, MapleClient c) {
      if (slea.readByte() > 0) {
         c.getPlayer().getQuestRemove(MapleQuest.getInstance(122902));
      } else {
         c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(122902));
      }
   }

   public static final void BuddyOperation(PacketDecoder slea, MapleClient c) {
      int mode = slea.readByte();
      Friend buddylist = c.getPlayer().getBuddylist();
      if (mode == 1) {
         String addName = slea.readMapleAsciiString();
         int accid = MapleCharacterUtil.getAccByName(addName);
         String groupName = slea.readMapleAsciiString();
         String memo = slea.readMapleAsciiString();
         byte accountBuddyCheck = slea.readByte();
         String nickName = "";
         if (accountBuddyCheck == 1) {
            nickName = slea.readMapleAsciiString();
         }

         FriendEntry ble = buddylist.get(addName);
         if (addName.length() > 13 || groupName.length() > 16 || nickName.length() > 13 || memo.length() > 260) {
            return;
         }

         if (ble != null && !ble.isVisible()) {
            c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "이미 친구로 등록되어 있습니다."));
            return;
         }

         if (buddylist.isFull()) {
            c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "친구리스트가 꽉 찼습니다."));
            return;
         }

         if (accid == c.getAccID()) {
            c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "자기계정에 있는 캐릭터는 친구추가 하실 수 없습니다."));
            return;
         }

         if (accountBuddyCheck == 0) {
            c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "현재 이 기능은 사용하실 수 없습니다.\r\n계정 통합 친구를 체크해 주세요."));
            return;
         }

         try {
            try (Connection con = DBConnection.getConnection()) {
               BuddyListHandler.CharacterIdNameBuddyCapacity charWithId = null;
               MapleCharacter otherChar = c.getChannelServer().getPlayerStorage().getCharacterByName(addName);
               int channel;
               if (otherChar != null) {
                  channel = c.getChannel();
                  if (!otherChar.isGM() || c.getPlayer().isGM()) {
                     charWithId = new BuddyListHandler.CharacterIdNameBuddyCapacity(
                        otherChar.getId(),
                        otherChar.getAccountID(),
                        otherChar.getName(),
                        otherChar.getLevel(),
                        otherChar.getJob(),
                        otherChar.getBuddylist().getCapacity(),
                        groupName,
                        memo
                     );
                  }
               } else {
                  channel = Center.Find.findChannel(addName);
                  charWithId = getCharacterIdAndNameFromDatabase(addName, groupName, memo);
               }

               if (charWithId == null) {
                  int id = 0;

                  for (String name : new ArrayList<>(DummyCharacterName.dummyList)) {
                     if (name.equals(addName)) {
                        charWithId = new BuddyListHandler.CharacterIdNameBuddyCapacity(100000000 + id, 100000000 + id, addName, 200, 6512, 15, groupName, memo);
                     }

                     id++;
                  }
               }

               if (charWithId != null) {
                  Friend.BuddyAddResult buddyAddResult = null;
                  if (channel != -1) {
                     buddyAddResult = Center.Buddy.requestBuddyAdd(
                        addName,
                        c.getChannel(),
                        c.getPlayer().getId(),
                        c.getAccID(),
                        c.getPlayer().getName(),
                        c.getPlayer().getLevel(),
                        c.getPlayer().getJob(),
                        groupName,
                        memo
                     );
                  } else {
                     PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) as buddyCount FROM buddies WHERE accid = ? AND pending = 0");
                     ps.setInt(1, charWithId.getAccId());
                     ResultSet rs = ps.executeQuery();
                     if (!rs.next()) {
                        ps.close();
                        rs.close();
                        throw new RuntimeException("Result set expected");
                     }

                     int count = rs.getInt("buddyCount");
                     if (count >= charWithId.getBuddyCapacity()) {
                        buddyAddResult = Friend.BuddyAddResult.BUDDYLIST_FULL;
                     }

                     rs.close();
                     ps.close();
                     ps = con.prepareStatement("SELECT pending FROM buddies WHERE accid = ? AND buddyid = ?");
                     ps.setInt(1, charWithId.getAccId());
                     ps.setInt(2, c.getPlayer().getId());
                     rs = ps.executeQuery();
                     if (rs.next()) {
                        buddyAddResult = Friend.BuddyAddResult.ALREADY_ON_LIST;
                     }

                     rs.close();
                     ps.close();
                     ps = con.prepareStatement("SELECT * FROM queststatus WHERE characterid = ?");
                     ps.setInt(1, charWithId.getId());
                     rs = ps.executeQuery();
                     if (rs.next()) {
                        buddyAddResult = Friend.BuddyAddResult.ADD_BLOCKED;
                     }

                     rs.close();
                     ps.close();
                  }

                  if (buddyAddResult == Friend.BuddyAddResult.BUDDYLIST_FULL) {
                     c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "상대 친구목록이 꽉 찼습니다."));
                     return;
                  } else if (buddyAddResult == Friend.BuddyAddResult.ADD_BLOCKED) {
                     c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "상대가 친구 추가 거부 상태입니다."));
                     return;
                  } else {
                     int displayChannel = -1;
                     int otherCid = charWithId.getId();
                     if (buddyAddResult != Friend.BuddyAddResult.ALREADY_ON_LIST) {
                        if (channel == -1) {
                           PreparedStatement psx = con.prepareStatement(
                              "INSERT INTO buddies (`accid`, `buddyid`, `buddyaccid`, `groupname`, `pending`, `memo`) VALUES (?, ?, ?, ?, 1, ?)"
                           );
                           psx.setInt(1, charWithId.getAccId());
                           psx.setInt(2, c.getPlayer().getId());
                           psx.setInt(3, c.getAccID());
                           psx.setString(4, groupName);
                           psx.setString(5, memo);
                           psx.executeUpdate();
                           psx.close();
                        }

                        buddylist.put(
                           new FriendEntry(
                              charWithId.getName(), accid, otherCid, groupName, displayChannel, true, charWithId.getLevel(), charWithId.getJob(), memo
                           )
                        );
                        c.getSession().writeAndFlush(CWvsContext.BuddylistPacket.buddyAddMessage(addName));
                        c.getSession().writeAndFlush(CWvsContext.BuddylistPacket.updateBuddylist(buddylist.getBuddies(), ble, (byte)21));
                        return;
                     }

                     c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "상대방의 친구목록에 신청자의 캐릭터가 등록되어 있습니다."));
                  }
               } else {
                  c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "대상을 발견하지 못했습니다."));
                  return;
               }
            }

            return;
         } catch (SQLException var52) {
            System.err.println("SQLError" + var52);
            var52.printStackTrace();
         }
      } else if (mode != 2) {
         if (mode == 3) {
            try {
               int otherAccId = slea.readInt();
               if (!buddylist.isFull()) {
                  try (Connection con = DBConnection.getConnection()) {
                     int channelx = Center.Find.findAccChannel(otherAccId);
                     int otherChrId = 0;
                     String otherName = null;
                     String groupNamex = "그룹 미지정";
                     String otherMemo = "";
                     int otherLevel = 0;
                     int otherJob = 0;
                     MapleClient otherClient = null;
                     if (Center.getStorage(channelx) != null) {
                        otherClient = Center.getStorage(channelx).getClientById(otherAccId);
                     }

                     MapleCharacter otherCharx = null;
                     if (otherClient != null && otherClient.getPlayer() != null) {
                        otherCharx = otherClient.getPlayer();
                     }

                     if (otherCharx == null) {
                        PreparedStatement psx = con.prepareStatement("SELECT id, name, level, job FROM characters WHERE accountid = ?");
                        psx.setInt(1, otherAccId);
                        ResultSet rsx = psx.executeQuery();
                        if (rsx.next()) {
                           otherChrId = rsx.getInt("id");
                           otherName = rsx.getString("name");
                           otherLevel = rsx.getInt("level");
                           otherJob = rsx.getInt("job");
                        }

                        rsx.close();
                        psx.close();
                        otherMemo = "";
                     } else {
                        otherName = otherCharx.getName();
                        otherChrId = otherCharx.getId();
                        otherLevel = otherCharx.getLevel();
                        otherJob = otherCharx.getJob();
                     }

                     if (otherName != null) {
                        FriendEntry blex = new FriendEntry(otherName, otherAccId, otherChrId, groupNamex, channelx, true, otherLevel, otherJob, otherMemo);
                        buddylist.put(blex);
                        c.getSession().writeAndFlush(CWvsContext.BuddylistPacket.updateBuddylist(buddylist.getBuddies(), blex, (byte)24));
                        if (otherClient != null) {
                           notifyRemoteChannel(c, otherClient.getChannel(), otherChrId, Friend.BuddyOperation.ADDED, otherMemo);
                        } else if (otherCharx != null) {
                           notifyRemoteChannel(c, otherCharx.getClient().getChannel(), otherChrId, Friend.BuddyOperation.ADDED, otherMemo);
                        }
                     }
                  } catch (SQLException var49) {
                     System.err.println("SQL THROW" + var49);
                     var49.printStackTrace();
                  }
               }

               nextPendingRequest(c);
            } catch (Exception var50) {
               System.out.println("친구리스트 오류 발생");
               var50.printStackTrace();
            }
         } else if (mode == 4) {
            int otherAccId = slea.readInt();
            if (otherAccId >= 100000000) {
               PreparedStatement psx = null;

               try (Connection con = DBConnection.getConnection()) {
                  psx = con.prepareStatement("DELETE FROM `buddies` WHERE `accid` = ?");
                  psx.setInt(1, otherAccId);
                  psx.executeUpdate();
               } catch (SQLException var44) {
               } finally {
                  try {
                     if (psx != null) {
                        psx.close();
                        PreparedStatement var62 = null;
                     }
                  } catch (SQLException var38) {
                  }
               }
            }

            FriendEntry blex = buddylist.get(otherAccId);
            String otherMemox = null;
            if (buddylist.containsVisible(otherAccId)) {
               notifyRemoteChannel(c, Center.Find.findAccChannel(otherAccId), blex.getCharacterId(), Friend.BuddyOperation.DELETED, otherMemox);
            }

            buddylist.remove(otherAccId);
            c.getSession().writeAndFlush(CWvsContext.BuddylistPacket.updateBuddylist(buddylist.getBuddies(), blex, (byte)43));
            nextPendingRequest(c);
         } else if (mode == 5) {
            int otherAccIdx = slea.readInt();
            FriendEntry blex = buddylist.get(otherAccIdx);
            String otherMemox = null;
            if (buddylist.containsVisible(otherAccIdx)) {
               notifyRemoteChannel(c, Center.Find.findAccChannel(otherAccIdx), blex.getCharacterId(), Friend.BuddyOperation.DELETED, otherMemox);
            }

            buddylist.remove(otherAccIdx);
            c.getSession().writeAndFlush(CWvsContext.BuddylistPacket.updateBuddylist(buddylist.getBuddies(), blex, (byte)43));
            nextPendingRequest(c);
         } else if (mode == 7) {
            int otherAccIdx = slea.readInt();
            FriendEntry blex = buddylist.get(otherAccIdx);
            String otherMemox = buddylist.get(otherAccIdx).getMemo();
            if (buddylist.containsVisible(otherAccIdx)) {
               notifyRemoteChannel(c, Center.Find.findAccChannel(otherAccIdx), blex.getCharacterId(), Friend.BuddyOperation.DELETED, otherMemox);
            }

            buddylist.remove(otherAccIdx);
            c.getSession().writeAndFlush(CWvsContext.BuddylistPacket.updateBuddylist(buddylist.getBuddies(), blex, (byte)43));
            nextPendingRequest(c);
         } else if (mode == 10) {
            if (c.getPlayer().getMeso() >= 50000L) {
               try (Connection con = DBConnection.getConnection()) {
                  PreparedStatement psx = con.prepareStatement("UPDATE `characters` SET `buddyCapacity` = ? WHERE `accountid` = ?");
                  psx.setInt(1, c.getPlayer().getBuddyCapacity() + 5);
                  psx.setInt(2, c.getAccID());
                  psx.executeQuery();
               } catch (SQLException var47) {
                  var47.printStackTrace();
               }

               c.getPlayer().setBuddyCapacity((byte)(c.getPlayer().getBuddyCapacity() + 5));
               c.getPlayer().gainMeso(-50000L, false);
            }
         } else if (mode == 11) {
            c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "현재 이 기능은 사용하실 수 없습니다."));
         } else if (mode == 12) {
            slea.skip(1);
            int otherCid = slea.readInt();
            slea.skip(4);
            String charname = slea.readMapleAsciiString();
            String memox = slea.readMapleAsciiString();
            FriendEntry entry = null;

            for (FriendEntry e : buddylist.getBuddies()) {
               if (e.getCharacterId() == otherCid) {
                  entry = e;
                  break;
               }
            }

            entry.setMemo(memox);
            c.getSession().writeAndFlush(CWvsContext.BuddylistPacket.updateBuddylist(buddylist.getBuddies(), entry, (byte)24));
         } else if (mode == 13) {
            int otherCid = slea.readInt();
            FriendEntry blex = buddylist.get(otherCid);
            String groupname = slea.readMapleAsciiString();
            FriendEntry blz = buddylist.get(otherCid);
            blz.setGroupName(groupname);
            c.getSession().writeAndFlush(CWvsContext.BuddylistPacket.updateBuddylist(buddylist.getBuddies(), blex, (byte)24));
         } else if (mode == 14) {
            int otherCid = slea.readInt();
            FriendEntry blex = buddylist.get(otherCid);
            String groupname = slea.readMapleAsciiString();
            FriendEntry blz = buddylist.get(otherCid);
            blz.setGroupName(groupname);
            c.getSession().writeAndFlush(CWvsContext.BuddylistPacket.updateBuddylist(buddylist.getBuddies(), blex, (byte)24));
         } else if (mode == 15) {
            Center.Buddy.loggedOff(c.getPlayer().getName(), c.getPlayer().getId(), c.getAccID(), c.getChannel(), buddylist.getBuddyIds());
            c.getPlayer().dropMessage(5, "오프라인 상태로 변경되었습니다.");
         }
      }
   }

   private static final void notifyRemoteChannel(MapleClient c, int remoteChannel, int otherId, Friend.BuddyOperation operation, String memo) {
      MapleCharacter player = c.getPlayer();
      if (remoteChannel > 0) {
         Center.Buddy.buddyChanged(otherId, player.getId(), c.getAccID(), player.getName(), c.getChannel(), operation, player.getLevel(), player.getJob(), memo);
      }
   }

   private static final class CharacterIdNameBuddyCapacity extends CharacterNameAndId {
      private int buddyCapacity;

      public CharacterIdNameBuddyCapacity(int id, int accId, String name, int level, int job, int buddyCapacity, String groupname, String memo) {
         super(id, accId, name, level, job, groupname, memo);
         this.buddyCapacity = buddyCapacity;
      }

      public int getBuddyCapacity() {
         return this.buddyCapacity;
      }
   }
}
