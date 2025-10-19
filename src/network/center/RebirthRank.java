package network.center;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.fields.gameobject.lifes.PlayerNPC;
import objects.users.MapleCharacter;

public class RebirthRank {
   static List<RebirthRankEntry> ranks = new ArrayList<>();
   public static PlayerNPC numberOneNPC = null;

   public static void displayRank(MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PARTY_QUEST_RANKING_RESULT.getValue());
      packet.writeInt(1);
      packet.writeInt(0);
      packet.writeInt(2);
      List<RebirthRankEntry> temp = new ArrayList<>(ranks);
      int count = Math.min(50, temp.size());
      packet.writeInt(count);

      for (int i = 0; i < count; i++) {
         packet.writeInt(i + 1);
         int co = 6;
         packet.writeInt(co);
         RebirthRankEntry entry = temp.get(i);
         entry.encode(packet);
      }

      player.send(packet.getPacket());
   }

   public static void loadRanks() {
      ranks.clear();
      PreparedStatement ps = null;
      ResultSet rs = null;
      List<RebirthRankEntry> temp = new ArrayList<>();

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT `id`, `name`, `accountid`, `job`, `level` FROM `characters` WHERE `level` >= 200");
         rs = ps.executeQuery();
         int totalRebirthCount = 0;
         int totalSuperRebirthCount = 0;

         while (rs.next()) {
            int playerID = rs.getInt("id");
            String name = rs.getString("name");
            int accountID = rs.getInt("accountid");
            int job = rs.getInt("job");
            int level = rs.getInt("level");
            PreparedStatement ps3 = con.prepareStatement("SELECT `level`, `job` FROM `characters` WHERE `accountid` = ?");
            ps3.setInt(1, accountID);
            ResultSet rs3 = ps3.executeQuery();
            int unionLevel = 0;
            int count = 0;

            while (rs3.next()) {
               if ((rs3.getInt("job") != 10112 || rs3.getInt("level") >= 200) && rs3.getInt("level") >= 60) {
                  unionLevel += rs3.getInt("level");
                  if (++count >= 42) {
                     break;
                  }
               }
            }

            rs3.close();
            ps3.close();
            PreparedStatement ps2 = con.prepareStatement("SELECT * FROM `questinfo` WHERE `characterid` = ? and `quest` = ?");
            int rebirthCount = 0;
            ps2.setInt(1, playerID);
            ps2.setInt(2, 100711);
            ResultSet rs2 = ps2.executeQuery();

            while (rs2.next()) {
               String value = rs2.getString("customData");
               String[] split = value.split(";");

               for (String v : split) {
                  String[] s = v.split("=");
                  if (s.length > 1 && s[0].equals("total")) {
                     String num = s[1];
                     if (num != null && !num.isEmpty()) {
                        rebirthCount = Integer.parseInt(num);
                        totalRebirthCount += rebirthCount;
                        break;
                     }
                  }
               }
            }

            rs2.close();
            ps2.close();
            ps2 = con.prepareStatement("SELECT * FROM `questinfo` WHERE `characterid` = ? and `quest` = ?");
            ps2.setInt(1, playerID);
            ps2.setInt(2, 100712);
            rs2 = ps2.executeQuery();
            int superRebirthCount = 0;

            while (rs2.next()) {
               String value = rs2.getString("customData");
               String[] split = value.split(";");

               for (String vx : split) {
                  String[] s = vx.split("=");
                  if (s.length > 1 && s[0].equals("point")) {
                     String num = s[1];
                     if (num != null && !num.isEmpty()) {
                        superRebirthCount = Integer.parseInt(num);
                        totalSuperRebirthCount += superRebirthCount;
                        break;
                     }
                  }
               }
            }

            rs2.close();
            ps2.close();
            RebirthRankEntry entry = new RebirthRankEntry(name, level, job, rebirthCount, superRebirthCount, unionLevel);
            entry.setPlayerId(playerID);
            temp.add(entry);
         }

         ranks = temp.stream()
            .sorted((a, b) -> b.getTotalUnion() - a.getTotalUnion())
            .sorted((a, b) -> b.getSuperRebirthCount() - a.getSuperRebirthCount())
            .sorted((a, b) -> b.getRebirthCount() - a.getRebirthCount())
            .collect(Collectors.toList());

         try {
            if (numberOneNPC == null && ranks.size() > 0 && ranks.size() > 0) {
               MapleCharacter player = MapleCharacter.loadCharFromDBFake(ranks.get(0).getPlayerId(), true);
               if (player != null) {
                  PlayerNPC npc = new PlayerNPC(player, 9901001, 993165543);
                  numberOneNPC = npc;
               }
            }
         } catch (Exception var39) {
            System.out.println("환생 랭킹 저장 오류 발생");
            var39.printStackTrace();
         }

         System.out.println("환생 랭킹 집계가 진행되었습니다.");
      } catch (SQLException var41) {
         var41.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var43 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var44 = null;
            }
         } catch (SQLException var37) {
         }
      }
   }
}
