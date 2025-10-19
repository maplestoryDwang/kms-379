package network.game;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import objects.utils.Pair;

public class MapleGuildRanking {
   private static final MapleGuildRanking instance = new MapleGuildRanking();
   private final List<MapleGuildRanking.GuildRankingInfo> honorRank = new LinkedList<>();
   private List<MapleGuildRanking.GuildRankingInfo> flagRaceRank = new LinkedList<>();
   private List<MapleGuildRanking.GuildRankingInfo> culvertRank = new LinkedList<>();
   private List<MapleGuildRanking.GuildRankingInfo> weekMissionRank = new LinkedList<>();

   public static MapleGuildRanking getInstance() {
      return instance;
   }

   public void load() {
      this.reload();
   }

   public List<MapleGuildRanking.GuildRankingInfo> getHonorRank() {
      return this.honorRank;
   }

   private void reload() {
      this.honorRank.clear();
      this.flagRaceRank.clear();
      this.culvertRank.clear();
      this.weekMissionRank.clear();

      try {
         Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement("SELECT * FROM guilds ORDER BY `GP` DESC LIMIT 50");
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            MapleGuildRanking.GuildRankingInfo rank = new MapleGuildRanking.GuildRankingInfo(
               rs.getString("name"), rs.getInt("GP"), rs.getInt("guildid"), -2L, new ArrayList<>()
            );
            this.honorRank.add(rank);
         }

         ps.close();
         rs.close();
         ps = con.prepareStatement("SELECT * FROM guildcontents");
         rs = ps.executeQuery();
         Map<Integer, Pair<Integer, Integer>> guildData = new HashMap<>();
         List<MapleGuildRanking.GuildRankingCharacterInfo> userInfo = new ArrayList<>();

         while (rs.next()) {
            int type = rs.getInt("type");
            int guildID = rs.getInt("guildid");
            int score = rs.getInt("lastweekpoint");
            long week = rs.getLong("lastweektime");
            if (guildData.containsKey(guildID)) {
               Pair<Integer, Integer> data = guildData.get(guildID);
               guildData.put(guildID, new Pair<>(type, data.right + score));
            } else {
               guildData.put(guildID, new Pair<>(type, score));
            }

            userInfo.add(new MapleGuildRanking.GuildRankingCharacterInfo(rs.getInt("characterid"), guildID, score, week, type));
         }

         ps.close();
         rs.close();

         for (Entry<Integer, Pair<Integer, Integer>> data : guildData.entrySet()) {
            ps = con.prepareStatement("SELECT `name` FROM guilds WHERE `guildid` = ?");
            ps.setInt(1, data.getKey());
            rs = ps.executeQuery();
            if (rs.next()) {
               String name = rs.getString(1);
               switch ((Integer)data.getValue().left) {
                  case 0:
                     this.flagRaceRank
                        .add(
                           new MapleGuildRanking.GuildRankingInfo(
                              name,
                              (Integer)data.getValue().right,
                              data.getKey(),
                              -2L,
                              userInfo.stream().filter(user -> user.getType() == 0 && user.getGuildID() == data.getKey()).collect(Collectors.toList())
                           )
                        );
                  case 1:
                  default:
                     break;
                  case 2:
                     this.culvertRank
                        .add(
                           new MapleGuildRanking.GuildRankingInfo(
                              name,
                              (Integer)data.getValue().right,
                              data.getKey(),
                              -2L,
                              userInfo.stream().filter(user -> user.getType() == 2 && user.getGuildID() == data.getKey()).collect(Collectors.toList())
                           )
                        );
                     break;
                  case 3:
                     this.weekMissionRank
                        .add(
                           new MapleGuildRanking.GuildRankingInfo(
                              name,
                              (Integer)data.getValue().right,
                              data.getKey(),
                              -2L,
                              userInfo.stream().filter(user -> user.getType() == 3 && user.getGuildID() == data.getKey()).collect(Collectors.toList())
                           )
                        );
               }
            }
         }

         ps.close();
         rs.close();
         con.close();
         this.flagRaceRank.sort((a, b) -> b.score - a.score);
         this.culvertRank.sort((a, b) -> b.score - a.score);
         this.weekMissionRank.sort((a, b) -> b.score - a.score);
      } catch (SQLException var12) {
         System.err.println("Error handling guildRanking");
         var12.printStackTrace();
      }
   }

   public List<MapleGuildRanking.GuildRankingInfo> getCulvertRank() {
      return this.culvertRank;
   }

   public List<MapleGuildRanking.GuildRankingInfo> getFlagRaceRank() {
      return this.flagRaceRank;
   }

   public List<MapleGuildRanking.GuildRankingInfo> getWeekMissionRank() {
      return this.weekMissionRank;
   }

   public static class GuildRankingCharacterInfo {
      private int id;
      private int guildID;
      private int point;
      private int type;
      private long updateTime;

      public GuildRankingCharacterInfo(int id, int guildID, int point, long updateTime, int type) {
         this.id = id;
         this.guildID = guildID;
         this.point = point;
         this.updateTime = updateTime;
         this.type = type;
      }

      public int getId() {
         return this.id;
      }

      public int getGuildID() {
         return this.guildID;
      }

      public int getPoint() {
         return this.point;
      }

      public long getUpdateTime() {
         return this.updateTime;
      }

      public int getType() {
         return this.type;
      }
   }

   public static class GuildRankingInfo {
      private final String guildName;
      private final int score;
      private int guildId;
      private long lastUpdateTime;
      private List<MapleGuildRanking.GuildRankingCharacterInfo> userInfo;

      public GuildRankingInfo(String guildName, int score, int guildId, long lastUpdateTime, List<MapleGuildRanking.GuildRankingCharacterInfo> chrInfo) {
         this.guildName = guildName;
         this.score = score;
         this.guildId = guildId;
         this.lastUpdateTime = lastUpdateTime;
         this.userInfo = chrInfo;
      }

      public String getGuildName() {
         return this.guildName;
      }

      public int getScore() {
         return this.score;
      }

      public int getGuildId() {
         return this.guildId;
      }

      public long getLastUpdateTime() {
         return this.lastUpdateTime;
      }

      public List<MapleGuildRanking.GuildRankingCharacterInfo> getUserInfo() {
         return this.userInfo;
      }
   }
}
