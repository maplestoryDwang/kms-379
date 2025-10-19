package network.center.praise;

import database.DBConnection;
import database.loader.CharacterSaveFlag;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import network.game.GameServer;
import network.models.CField;
import objects.users.MapleCharacter;
import objects.users.PraisePoint;
import scripting.NPCConversationManager;

public class PraisePointRank {
   private static Map<Integer, PraisePoint> ranks = new HashMap<>();

   public static synchronized void loadRanks() {
      ranks.clear();
      PreparedStatement ps = null;
      ResultSet rs = null;
      int rank = 1;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT `account_id`, `total_point` FROM `praise_point` ORDER BY `total_point` DESC");
         rs = ps.executeQuery();

         while (rs.next()) {
            PraisePoint p = new PraisePoint(rs.getInt("total_point"), 0);
            PreparedStatement ps2 = con.prepareStatement("SELECT `name` FROM `characters` WHERE `accountid` = ? ORDER BY `level` DESC");
            ps2.setInt(1, rs.getInt("account_id"));
            ResultSet rs2 = ps2.executeQuery();
            String name = "";
            if (rs2.next()) {
               name = rs2.getString("name");
               p.setName(name);
            }

            rs2.close();
            ps2.close();
            if (!name.isEmpty()) {
               ranks.put(rank++, p);
            }
         }
      } catch (SQLException var20) {
         var20.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var22 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var23 = null;
            }
         } catch (SQLException var17) {
         }
      }

      System.out.println("칭찬 포인트 랭킹 총 " + (rank - 1) + "개가 업데이트 되었습니다.");
   }

   public static boolean doPraise(MapleCharacter from, String toName, NPCConversationManager cm) {
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         int toAccountID = 0;
         int toPlayerID = 0;
         ps = con.prepareStatement("SELECT `accountid`, `id` FROM `characters` WHERE `name` = ?");
         ps.setString(1, toName);

         for (rs = ps.executeQuery(); rs.next(); toPlayerID = rs.getInt("id")) {
            toAccountID = rs.getInt("accountid");
         }

         if (toAccountID == 0) {
            cm.sendNext("해당 캐릭터를 찾을 수 없습니다.");
            return false;
         } else if (toAccountID == from.getAccountID()) {
            cm.sendNext("자신의 캐릭터는 칭찬할 수 없습니다. #e어뷰징 행위#n는 경고 없이 제재될 수 있습니다. 주의해주시기 바랍니다.");
            return false;
         } else if (toAccountID == 106) {
            cm.sendNext("해당 캐릭터는 칭찬할 수 없습니다.");
            return false;
         } else {
            MapleCharacter player = null;

            for (GameServer cs : GameServer.getAllInstances()) {
               for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
                  if (chr.getId() == toPlayerID) {
                     player = chr;
                     break;
                  }
               }
            }

            if (player != null) {
               PraisePoint p = player.getPraisePoint();
               p.setTotalPoint(p.getTotalPoint() + 1000);
               p.setPoint(p.getPoint() + 1000);
               player.setSaveFlag(player.getSaveFlag() | CharacterSaveFlag.PRAISE_POINT.getFlag());
               player.dropMessage(5, from.getName() + "님이 " + player.getName() + "님을 칭찬했습니다. 1,000 칭찬 포인트를 획득했습니다.");
               player.send(
                  CField.addPopupSay(9062000, 5000, "#b" + from.getName() + "#k님이 #b" + player.getName() + "#k님을 칭찬했습니다. #r1,000 칭찬 포인트#k를 획득했습니다.", "")
               );
            } else {
               PreparedStatement ps2 = con.prepareStatement("SELECT `total_point`, `point` FROM `praise_point` WHERE `account_id` = ?");
               ps2.setInt(1, toAccountID);
               ResultSet rs2 = ps2.executeQuery();
               int tp = 0;

               int p;
               for (p = 0; rs2.next(); p = rs2.getInt("point")) {
                  tp = rs2.getInt("total_point");
               }

               tp += 1000;
               p += 1000;
               rs2.close();
               ps2.close();
               ps2 = con.prepareStatement("DELETE FROM `praise_point` WHERE `account_id` = ?");
               ps2.setInt(1, toAccountID);
               ps2.executeUpdate();
               ps2.close();
               ps2 = con.prepareStatement("INSERT INTO `praise_point` (`account_id`, `total_point`, `point`) VALUES (?, ?, ?)");
               ps2.setInt(1, toAccountID);
               ps2.setInt(2, tp);
               ps2.setInt(3, p);
               ps2.executeUpdate();
               ps2.close();
            }

            from.updateOneInfo(3887, "point", String.valueOf(from.getOneInfoQuestInteger(3887, "point") + 500));
            from.dropMessage(5, toName + "님을 칭찬했습니다. 500 칭찬포인트를 획득했습니다.");
            PraisePoint p = from.getPraisePoint();
            if (p != null) {
               p.setPoint(p.getPoint() + 500);
               p.setTotalPoint(p.getTotalPoint() + 500);
               from.setSaveFlag(from.getSaveFlag() | CharacterSaveFlag.PRAISE_POINT.getFlag());
            }

            PreparedStatement ps2 = con.prepareStatement(
               "INSERT INTO `praise_point_log` (`from_name`, `from_account_id`, `to_name`, `to_account_id`, `praise_time`, `connector_key`) VALUES (?, ?, ?, ?, ?, ?)"
            );
            ps2.setString(1, from.getName());
            ps2.setInt(2, from.getAccountID());
            ps2.setString(3, toName);
            ps2.setInt(4, toAccountID);
            ps2.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            String connectorKey = "";
            if (from.getClient().getConnectorClient() != null) {
               connectorKey = from.getClient().getConnectorClient().getConnecterKey();
            }

            ps2.setString(6, connectorKey);
            ps2.executeUpdate();
            ps2.close();
            return true;
         }
      } catch (SQLException var28) {
         var28.printStackTrace();
         return true;
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var30 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var31 = null;
            }
         } catch (SQLException var25) {
         }
      }
   }

   public static void calculateRankerReward() {
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         int rank = 1;
         ps = con.prepareStatement("DELETE FROM `questinfo` WHERE `quest` = ?");
         ps.setInt(1, 1234599);
         ps.executeUpdate();
         ps.close();

         for (GameServer gs : GameServer.getAllInstances()) {
            for (MapleCharacter player : gs.getPlayerStorage().getAllCharacters()) {
               if (player != null) {
                  player.updateOneInfo(1234599, "praise_reward", "");
                  player.updateOneInfo(1234599, "praise_reward_get", "");
               }
            }
         }

         for (Entry<Integer, PraisePoint> entry : new HashMap<>(ranks).entrySet()) {
            PraisePoint point = entry.getValue();
            String name = point.getName();
            int toAccountID = 0;
            int toPlayerID = 0;
            ps = con.prepareStatement("SELECT `accountid`, `id` FROM `characters` WHERE `name` = ?");
            ps.setString(1, name);

            for (rs = ps.executeQuery(); rs.next(); toPlayerID = rs.getInt("id")) {
               toAccountID = rs.getInt("accountid");
            }

            rs.close();
            ps.close();
            MapleCharacter playerx = null;

            for (GameServer cs : GameServer.getAllInstances()) {
               for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
                  if (chr.getId() == toPlayerID) {
                     playerx = chr;
                     break;
                  }
               }
            }

            if (playerx != null) {
               playerx.updateOneInfo(1234599, "praise_reward", String.valueOf(rank));
               playerx.updateOneInfo(1234599, "praise_reward_get", "");
               playerx.dropMessage(5, "[알림] 칭찬 포인트 랭킹 " + rank + "위 보상을 수령해주시기 바랍니다.");
            } else {
               PreparedStatement ps3 = con.prepareStatement("INSERT INTO `questinfo` (`characterid`, `quest`, `customData`, `date`) VALUES (?, ?, ?, ?)");
               ps3.setInt(1, toPlayerID);
               ps3.setInt(2, 1234599);
               ps3.setString(3, "praise_reward=" + rank);
               SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
               String time = sdf.format(Calendar.getInstance().getTime());
               ps3.setString(4, time);
               ps3.executeUpdate();
               ps3.close();
            }

            System.out.println(name + " 캐릭터가 칭찬 포인트 랭킹 " + rank + "위로 보상이 정산되었습니다.");
            if (rank++ >= 10) {
               break;
            }
         }

         ps = con.prepareStatement("UPDATE `praise_point` SET `total_point` = ?");
         ps.setInt(1, 0);
         ps.executeUpdate();

         for (GameServer gs : GameServer.getAllInstances()) {
            for (MapleCharacter p : gs.getPlayerStorage().getAllCharacters()) {
               if (p != null) {
                  PraisePoint point = p.getPraisePoint();
                  point.setTotalPoint(0);
                  p.setSaveFlag(p.getSaveFlag() | CharacterSaveFlag.PRAISE_POINT.getFlag());
               }
            }
         }
      } catch (SQLException var27) {
         var27.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var29 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var30 = null;
            }
         } catch (SQLException var24) {
            var24.printStackTrace();
         }
      }
   }

   public static Map<Integer, PraisePoint> getRanks() {
      return new HashMap<>(ranks);
   }

   public static void setRanks(Map<Integer, PraisePoint> ranks) {
      PraisePointRank.ranks = ranks;
   }
}
