package objects.fields.child.dojang;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import network.game.GameServer;
import objects.fields.gameobject.lifes.PlayerNPC;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.looks.AvatarLook;
import objects.utils.Pair;

public class DojangRanking {
   public static Map<Integer, List<DojangRankingEntry>> thisWeekRank = new HashMap<>();
   public static Map<Integer, List<DojangRankingEntry>> lastWeekRank = new HashMap<>();
   public static PlayerNPC numberOneNPC = null;

   public static void initRanking() {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(System.currentTimeMillis());
      int currentWeek = cal.get(3);
      int weekYear = cal.getWeekYear();
      int lastYear = weekYear;
      int lastWeek = currentWeek;
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         if (currentWeek == 1) {
            lastWeek = 52;
            lastYear--;
         } else {
            lastWeek--;
         }

         ps = con.prepareStatement(
            "SELECT `type`, `rank`, `job`, `level`, `point`, `name`, `packed_avatar_look`, `week`, `year` FROM `dojang_ranking` WHERE `week` = ? and `year` = ?"
         );
         ps.setInt(1, lastWeek);
         ps.setInt(2, lastYear);
         rs = ps.executeQuery();

         int count;
         for (count = 0; rs.next(); count++) {
            Integer type = rs.getInt("type");
            List<DojangRankingEntry> list = null;
            list = lastWeekRank.get(type);
            if (list == null) {
               list = new ArrayList<>();
               lastWeekRank.put(type, list);
            }

            DojangRankingEntry entry = new DojangRankingEntry();
            entry.loadFromDB(rs);
            PreparedStatement ps2 = con.prepareStatement("SELECT `accountid` FROM `characters` WHERE `name` = ?");
            ps2.setString(1, entry.getName());
            ResultSet rs2 = ps2.executeQuery();
            int accountID = -1;

            while (rs2.next()) {
               accountID = rs2.getInt("accountid");
            }

            rs2.close();
            ps2.close();
            entry.setAccountID(accountID);
            list.add(entry);
         }

         System.out.println("총 " + count + "개의 지난주 무릉 도장 랭킹 데이터를 불러왔습니다.");
         rs.close();
         ps.close();
         ps = con.prepareStatement(
            "SELECT `type`, `rank`, `job`, `level`, `point`, `name`, `packed_avatar_look`, `week`, `year` FROM `dojang_ranking` WHERE `week` = ? and `year` = ?"
         );
         ps.setInt(1, currentWeek);
         ps.setInt(2, weekYear);
         rs = ps.executeQuery();

         for (count = 0; rs.next(); count++) {
            Integer type = rs.getInt("type");
            List<DojangRankingEntry> list = null;
            list = thisWeekRank.get(type);
            if (list == null) {
               list = new ArrayList<>();
               thisWeekRank.put(type, list);
            }

            DojangRankingEntry entry = new DojangRankingEntry();
            entry.loadFromDB(rs);
            PreparedStatement ps2 = con.prepareStatement("SELECT `accountid` FROM `characters` WHERE `name` = ?");
            ps2.setString(1, entry.getName());
            ResultSet rs2 = ps2.executeQuery();
            int accountID = -1;

            while (rs2.next()) {
               accountID = rs2.getInt("accountid");
            }

            rs2.close();
            ps2.close();
            entry.setAccountID(accountID);
            list.add(entry);
         }

         if (thisWeekRank.size() > 0 && numberOneNPC == null && thisWeekRank.get(0) != null) {
            try {
               String playerID = thisWeekRank.get(0).get(0).getName();
               MapleCharacter player = MapleCharacter.loadCharFromDBFakeByName(playerID, true);
               PlayerNPC npc = new PlayerNPC(player, 9901002, 680000710);
               npc.setCoords(-1097, -220, 0, 32);
               numberOneNPC = npc;
            } catch (Exception var27) {
            }
         }

         System.out.println("총 " + count + "개의 금주 무릉 도장 랭킹 데이터를 불러왔습니다.");
         rs.close();
         ps.close();
      } catch (SQLException var29) {
         var29.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var32 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var33 = null;
            }
         } catch (SQLException var25) {
            var25.printStackTrace();
         }
      }

      calculateRankerReward();
   }

   public static void saveRanks() {
      saveRanks(-1, -1);
   }

   public static synchronized void saveRanks(int currentWeek, int weekYear) {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(System.currentTimeMillis());
      if (currentWeek == -1) {
         currentWeek = cal.get(3);
         weekYear = cal.getWeekYear();
      }

      PreparedStatement ps = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("DELETE FROM `dojang_ranking` WHERE `week` = ? and `year` = ?");
         ps.setInt(1, currentWeek);
         ps.setInt(2, weekYear);
         ps.executeQuery();
         ps.close();

         for (Entry<Integer, List<DojangRankingEntry>> entry : new HashMap<>(thisWeekRank).entrySet()) {
            int type = entry.getKey();
            List<DojangRankingEntry> list = entry.getValue();
            ps = con.prepareStatement(
               "INSERT INTO `dojang_ranking` (`type`, `rank`, `job`, `level`, `point`, `name`, `packed_avatar_look`, `week`, `year`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );

            for (DojangRankingEntry e : list) {
               ps.setInt(1, type);
               ps.setInt(2, e.getRank());
               ps.setInt(3, e.getJob());
               ps.setInt(4, e.getLevel());
               ps.setInt(5, e.getPoint());
               ps.setString(6, e.getName());
               ps.setBytes(7, e.getPackedAvatarLook());
               ps.setInt(8, currentWeek);
               ps.setInt(9, weekYear);
               ps.executeUpdate();
            }
         }

         System.out.println("금주 무릉도장 랭킹이 DB에 업데이트 되었습니다.");
      } catch (SQLException var23) {
         var23.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var25 = null;
            }
         } catch (SQLException var20) {
            var20.printStackTrace();
         }
      }
   }

   public static void nextWeekend() {
      lastWeekRank = new HashMap<>(thisWeekRank);
      thisWeekRank.clear();
   }

   public static void calculateRankerReward() {
      PreparedStatement ps = null;
      ResultSet rs = null;
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(System.currentTimeMillis());
      int currentWeek = cal.get(3);
      int weekYear = cal.getWeekYear();
      int lastYear = weekYear;
      int lastWeek;
      if (currentWeek == 1) {
         lastWeek = 52;
         lastYear = weekYear - 1;
      } else {
         lastWeek = currentWeek - 1;
      }

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM `dojang_ranking_calculate`");
         rs = ps.executeQuery();
         boolean find = false;

         while (rs.next()) {
            int lastUpdateWeek = rs.getInt("last_update_week");
            int lastUpdateYear = rs.getInt("last_update_year");
            if (lastUpdateWeek == lastWeek && lastUpdateYear == lastYear) {
               find = true;
            }
         }

         if (!find) {
            ps.close();
            ps = con.prepareStatement("DELETE FROM `questinfo` WHERE `quest` = ?");
            ps.setInt(1, 1234590);
            ps.executeUpdate();
            ps.close();
            rs.close();

            for (GameServer gs : GameServer.getAllInstances()) {
               for (MapleCharacter player : gs.getPlayerStorage().getAllCharacters()) {
                  if (player != null) {
                     int r = player.getOneInfoQuestInteger(1234590, "dojang_reward");
                     if (r > 0) {
                        player.updateOneInfo(1234590, "dojang_reward", "");
                        player.updateOneInfo(1234590, "dojang_reward_get", "");
                     }

                     r = player.getOneInfoQuestInteger(1234590, "dojang_reward_c");
                     if (r > 0) {
                        player.updateOneInfo(1234590, "dojang_reward_c", "");
                        player.updateOneInfo(1234590, "dojang_reward_get_c", "");
                     }

                     player.updateOneInfo(1234590, "dojang_point_get", "");
                  }
               }
            }

            HashMap<Integer, List<DojangRankingEntry>> tempMap = new HashMap<>(lastWeekRank);
            List<DojangRankingEntry> list = tempMap.get(2);
            if (list != null) {
               list = list.stream().sorted((a, b) -> b.getPoint() - a.getPoint()).collect(Collectors.toList());
               int rx = 1;

               for (DojangRankingEntry e : list) {
                  e.setRank(rx++);
                  MapleCharacter playerx = null;

                  for (GameServer gs : GameServer.getAllInstances()) {
                     for (MapleCharacter p : gs.getPlayerStorage().getAllCharacters()) {
                        if (p != null) {
                           int accid = MapleClient.getAccountIDByCharName(p.getName());
                           int entryaccid = MapleClient.getAccountIDByCharName(e.getName());
                           if (e.getName().equals(p.getName()) || accid != -1 && entryaccid != -1 && accid == entryaccid) {
                              playerx = p;
                              break;
                           }
                        }
                     }
                  }

                  if (playerx != null) {
                     System.out.println(e.getName() + " 캐릭터가 무릉 도장 랭킹 " + e.getRank() + "위로 보상이 정산되었습니다.");
                     playerx.dropMessage(5, "[알림] 무릉도장 랭킹 " + e.getRank() + "위 보상을 수령해주시기 바랍니다.");
                     playerx.updateOneInfo(1234590, "dojang_reward", String.valueOf(e.getRank()));
                  } else {
                     PreparedStatement ps2 = con.prepareStatement("SELECT `id` FROM `characters` WHERE `name` = ?");
                     ps2.setString(1, e.getName());
                     ResultSet rs2 = ps2.executeQuery();

                     while (rs2.next()) {
                        int playerID = rs2.getInt("id");
                        PreparedStatement ps3 = con.prepareStatement(
                           "INSERT INTO `questinfo` (`characterid`, `quest`, `customData`, `date`) VALUES (?, ?, ?, ?)"
                        );
                        ps3.setInt(1, playerID);
                        ps3.setInt(2, 1234590);
                        ps3.setString(3, "dojang_reward=" + e.getRank());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        String time = sdf.format(Calendar.getInstance().getTime());
                        ps3.setString(4, time);
                        ps3.executeUpdate();
                        ps3.close();
                        System.out.println(e.getName() + " 캐릭터가 무릉 도장 랭킹 " + e.getRank() + "위로 보상이 정산되었습니다.");
                     }

                     rs2.close();
                     ps2.close();
                  }

                  if (rx >= 6) {
                     break;
                  }
               }
            }

            List<DojangRankingEntry> list2 = tempMap.get(0);
            if (list2 != null) {
               list2 = list2.stream().sorted((a, b) -> b.getPoint() - a.getPoint()).collect(Collectors.toList());
               int rx = 1;

               for (DojangRankingEntry e : list2) {
                  e.setRank(rx++);
                  MapleCharacter playerx = null;

                  for (GameServer gs : GameServer.getAllInstances()) {
                     for (MapleCharacter px : gs.getPlayerStorage().getAllCharacters()) {
                        if (px != null && e.getName().equals(px.getName())) {
                           playerx = px;
                           break;
                        }
                     }
                  }

                  if (playerx != null) {
                     System.out.println(e.getName() + " 캐릭터가 무릉 도장(챌린지) 랭킹 " + e.getRank() + "위로 보상이 정산되었습니다.");
                     playerx.dropMessage(5, "[알림] 무릉도장(챌린지) 랭킹 " + e.getRank() + "위 보상을 수령해주시기 바랍니다.");
                     playerx.updateOneInfo(1234590, "dojang_reward_c", String.valueOf(e.getRank()));
                  } else {
                     PreparedStatement ps2 = con.prepareStatement("SELECT `id` FROM `characters` WHERE `name` = ?");
                     ps2.setString(1, e.getName());
                     ResultSet rs2 = ps2.executeQuery();

                     while (rs2.next()) {
                        int playerID = rs2.getInt("id");
                        PreparedStatement ps3 = con.prepareStatement(
                           "INSERT INTO `questinfo` (`characterid`, `quest`, `customData`, `date`) VALUES (?, ?, ?, ?)"
                        );
                        ps3.setInt(1, playerID);
                        ps3.setInt(2, 1234590);
                        ps3.setString(3, "dojang_reward_c=" + e.getRank());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        String time = sdf.format(Calendar.getInstance().getTime());
                        ps3.setString(4, time);
                        ps3.executeUpdate();
                        ps3.close();
                        System.out.println(e.getName() + " 캐릭터가 무릉 도장(챌린지) 랭킹 " + e.getRank() + "위로 보상이 정산되었습니다.");
                     }

                     rs2.close();
                     ps2.close();
                  }

                  if (rx >= 6) {
                     break;
                  }
               }
            }

            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM `dojang_ranking_calculate`");
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement("INSERT INTO `dojang_ranking_calculate` (`last_update_week`, `last_update_year`) VALUES (?, ?)");
            ps.setInt(1, lastWeek);
            ps.setInt(2, lastYear);
            ps.executeUpdate();
            System.out.println("무릉 도장 상위 랭커 정산이 수행되었습니다.");
         }
      } catch (SQLException var34) {
         var34.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var36 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var37 = null;
            }
         } catch (SQLException var31) {
            var31.printStackTrace();
         }
      }
   }

   public static boolean addAndCalcRank(int score, MapleCharacter player, boolean challenge) {
      int type = -1;
      byte var5;
      if (challenge) {
         var5 = 0;
      } else {
         var5 = 2;
      }

      if (var5 == 0) {
         removeRank(2, player);
         removeRank(player.getJob(), player);
         if (player.getOneInfoQuestInteger(1234590, "dojang_challenge") <= 0) {
            player.updateOneInfo(1234590, "dojang_challenge", "1");
         }
      } else if (var5 == 2) {
         removeRank(0, player);
         if (player.getOneInfoQuestInteger(1234590, "dojang_challenge") > 0) {
            player.updateOneInfo(1234590, "dojang_challenge", "0");
         }
      }

      boolean update = updateRank(var5, player, score);
      if (!challenge && var5 == 2) {
         updateRank(player.getJob(), player, score);
      }

      return update;
   }

   public static void removeRank(int type, MapleCharacter player) {
      List<DojangRankingEntry> list = thisWeekRank.get(type);
      if (list != null && !list.isEmpty()) {
         boolean find = false;
         int accid = MapleClient.getAccountIDByCharName(player.getName());

         for (DojangRankingEntry entry : new ArrayList<>(list)) {
            int entryaccid = MapleClient.getAccountIDByCharName(entry.getName());
            if (entry.getName().equals(player.getName()) || accid != -1 && entryaccid != -1 && accid == entryaccid) {
               list.remove(entry);
               find = true;
            }
         }

         if (find) {
            list = list.stream().sorted((a, b) -> b.getPoint() - a.getPoint()).collect(Collectors.toList());
            int r = 1;

            for (DojangRankingEntry e : list) {
               e.setRank(r++);
            }
         }
      }
   }

   public static boolean updateRank(int type, MapleCharacter player, int score) {
      List<DojangRankingEntry> list = thisWeekRank.get(type);
      DojangRankingEntry entry = new DojangRankingEntry(type, 1, player.getJob(), player.getLevel(), score, player.getName(), null, player.getAccountID());
      AvatarLook avatar = new AvatarLook(player, false, false);
      byte[] packedAvatarLook = avatar.packedTo();
      entry.setPackedAvatarLook(packedAvatarLook);
      if (list == null) {
         List<DojangRankingEntry> l = new ArrayList<>();
         l.add(entry);
         thisWeekRank.put(type, l);
         return false;
      } else {
         boolean update = true;
         int accid = MapleClient.getAccountIDByCharName(player.getName());

         for (DojangRankingEntry e : new ArrayList<>(list)) {
            int entryaccid = MapleClient.getAccountIDByCharName(e.getName());
            if (e.getName().equals(player.getName()) || accid != -1 && entryaccid != -1 && accid == entryaccid) {
               if (e.getPoint() < score) {
                  list.remove(e);
                  update = true;
               } else {
                  update = false;
               }
            }
         }

         if (update) {
            list.add(entry);
            list = list.stream().sorted((a, b) -> b.getPoint() - a.getPoint()).collect(Collectors.toList());
            int r = 1;

            for (DojangRankingEntry ex : list) {
               ex.setRank(r++);
            }

            if (type == 0 && thisWeekRank.size() > 0 && numberOneNPC != null && thisWeekRank.get(0) != null) {
               try {
                  if (numberOneNPC.getCharId() != player.getId()) {
                     String playerID = thisWeekRank.get(0).get(0).getName();
                     MapleCharacter pl = MapleCharacter.loadCharFromDBFakeByName(playerID, true);
                     PlayerNPC npc = new PlayerNPC(pl, 9901002, 680000710);
                     npc.setCoords(-1097, -220, 0, 32);
                     numberOneNPC = npc;
                  }
               } catch (Exception var13) {
               }
            }

            thisWeekRank.put(type, list);
         }

         return update;
      }
   }

   public static Pair<Integer, Integer> getLastTryDojang(int type, String name) {
      List<Pair<Integer, Integer>> rankings = new ArrayList<>();
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT `week`, `year` FROM `dojang_ranking` WHERE `name` = ?");
         ps.setString(1, name);
         rs = ps.executeQuery();

         while (rs.next()) {
            rankings.add(new Pair<>(rs.getInt("year"), rs.getInt("week")));
         }
      } catch (SQLException var20) {
         var20.printStackTrace();
      } finally {
         try {
            if (rs != null) {
               rs.close();
               ResultSet var24 = null;
            }

            if (ps != null) {
               ps.close();
               PreparedStatement var23 = null;
            }
         } catch (SQLException var17) {
            var17.printStackTrace();
         }
      }

      rankings = rankings.stream().sorted((a, b) -> b.right - a.right).collect(Collectors.toList());
      return rankings.isEmpty() ? null : rankings.get(0);
   }

   public static int getLastWeekPercentage(int type, String name) {
      DojangMyRanking ranking = getLastWeekMyRank(type, name);
      return ranking != null && ranking.getRank() != 0 ? ranking.getPercentage() : 100;
   }

   public static DojangMyRanking getLastWeekMyRank(int type, String name) {
      if (lastWeekRank.isEmpty()) {
         return new DojangMyRanking(0, 0, 101);
      } else {
         List<DojangRankingEntry> entry = lastWeekRank.get(type);
         if (entry != null) {
            int accid = MapleClient.getAccountIDByCharName(name);

            for (DojangRankingEntry e : entry) {
               int entryaccid = e.getAccountID();
               if (e.getName().equals(name) || accid != -1 && entryaccid != -1 && accid == entryaccid) {
                  int percent = (int)(e.getRank() / (entry.size() * 0.01));
                  return new DojangMyRanking(e.getPoint(), e.getRank(), percent);
               }
            }
         }

         return new DojangMyRanking(0, 0, 101);
      }
   }

   public static DojangMyRanking getThisWeekMyRank(int type, String name) {
      if (thisWeekRank.isEmpty()) {
         return new DojangMyRanking(0, 0, 101);
      } else {
         List<DojangRankingEntry> entry = thisWeekRank.get(type);
         if (entry != null) {
            int accid = MapleClient.getAccountIDByCharName(name);

            for (DojangRankingEntry e : entry) {
               int entryaccid = e.getAccountID();
               if (e.getName().equals(name) || accid != -1 && entryaccid != -1 && accid == entryaccid) {
                  int percent = (int)(e.getRank() / (entry.size() * 0.01));
                  return new DojangMyRanking(e.getPoint(), e.getRank(), percent);
               }
            }
         }

         return new DojangMyRanking(0, 0, 101);
      }
   }

   public static List<DojangRankingEntry> getThisWeekRank(int type) {
      if (thisWeekRank.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<DojangRankingEntry> list = thisWeekRank.get(type);
         if (list == null) {
            return Collections.emptyList();
         } else {
            List<DojangRankingEntry> ret = new ArrayList<>();
            list = list.stream().sorted((a, b) -> a.getRank() - b.getRank()).collect(Collectors.toList());
            int count = 0;

            for (DojangRankingEntry entry : list) {
               ret.add(entry);
               if (++count >= 100) {
                  break;
               }
            }

            return ret;
         }
      }
   }

   public static List<DojangRankingEntry> getLastWeekRank(int type) {
      if (lastWeekRank.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<DojangRankingEntry> list = lastWeekRank.get(type);
         if (list == null) {
            return Collections.emptyList();
         } else {
            List<DojangRankingEntry> ret = new ArrayList<>();
            list = list.stream().sorted((a, b) -> a.getRank() - b.getRank()).collect(Collectors.toList());
            int count = 0;

            for (DojangRankingEntry entry : list) {
               ret.add(entry);
               if (++count >= 100) {
                  break;
               }
            }

            return ret;
         }
      }
   }
}
