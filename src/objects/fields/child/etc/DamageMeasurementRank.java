package objects.fields.child.etc;

import constants.JobConstants;
import database.DBConfig;
import database.DBConnection;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import objects.fields.gameobject.lifes.PlayerNPC;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Pair;
import objects.utils.Properties;
import objects.utils.Table;

public class DamageMeasurementRank {
   public static Map<Integer, Long> rank = new ConcurrentHashMap<>();
   public static Map<Integer, Long> rewardRank = new ConcurrentHashMap<>();
   public static PlayerNPC numberOneNPC = null;
   public static PlayerNPC numberTwoNPC = null;
   public static PlayerNPC numberThreeNPC = null;
   public static long lastweekBuffTime;
   public static LinkedHashMap<Integer, Pair<Integer, Integer>> lastweekRanks = new LinkedHashMap<>();

   public static void resetRank() {
      rankReward();
      rank.clear();
      rewardRank.clear();
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("DELETE FROM `damage_measurement_rank`");
         ps.executeQuery();
         System.out.println("전투력 측정 랭킹이 초기화되었습니다.");
      } catch (SQLException var17) {
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var19 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var20 = null;
            }
         } catch (SQLException var14) {
         }
      }

      numberOneNPC = null;
   }

   public static void loadRank() {
      rank.clear();
      rewardRank.clear();
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM `damage_measurement_rank`");
         rs = ps.executeQuery();

         while (rs.next()) {
            int playerID = rs.getInt("player_id");
            long damage = rs.getLong("damage");
            rank.put(playerID, damage);
            boolean addRewardRank = true;

            for (Integer ran : lastweekRanks.keySet()) {
               if ((Integer)lastweekRanks.get(ran).left == MapleClient.getAccountIDByCharID(playerID)) {
                  addRewardRank = false;
               }
            }

            if (addRewardRank) {
               rewardRank.put(playerID, damage);
            }
         }

         rank = sortByValue(rank);
         rewardRank = sortByValue(rewardRank);
         System.out.println("전투력 측정 랭킹이 로드되었습니다.");
      } catch (SQLException var21) {
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var23 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var24 = null;
            }
         } catch (SQLException var18) {
         }
      }

      if (numberOneNPC == null) {
         int ranker1 = -1;
         int ranker2 = -1;
         int ranker3 = -1;

         for (Integer a : rank.keySet()) {
            int temprank = a;
            if (temprank > 0 && ranker1 <= 0) {
               ranker1 = a;
               if (!DBConfig.isGanglim) {
                  break;
               }
            } else if (temprank > 0 && ranker2 <= 0) {
               ranker2 = a;
            } else if (temprank > 0 && ranker3 <= 0) {
               ranker3 = a;
               break;
            }
         }

         if (ranker1 > 0) {
            numberOneNPC = new PlayerNPC(MapleCharacter.loadCharFromDBFake(ranker1, true), 9901001, 680000710);
         }

         if (DBConfig.isGanglim) {
            if (ranker2 > 0) {
               numberTwoNPC = new PlayerNPC(MapleCharacter.loadCharFromDBFake(ranker2, true), 9901003, 680000710);
            }

            if (ranker3 > 0) {
               numberThreeNPC = new PlayerNPC(MapleCharacter.loadCharFromDBFake(ranker3, true), 9901004, 680000710);
            }
         }
      }

      saveRank();
   }

   public static void saveRank() {
      PreparedStatement ps = null;
      ResultSet rs = null;

      try (Connection con = DBConnection.getConnection()) {
         for (Entry<Integer, Long> entry : rank.entrySet()) {
            ps = con.prepareStatement("SELECT `level` FROM `characters` WHERE `id` = ?");
            ps.setInt(1, entry.getKey());
            rs = ps.executeQuery();
            boolean find = true;
            if (!rs.next()) {
               find = false;
            }

            ps.close();
            rs.close();
            if (!find) {
               ps = con.prepareStatement("DELETE FROM `damage_measurement_rank` WHERE `player_id` = ?");
               ps.setInt(1, entry.getKey());
               ps.executeUpdate();
            } else {
               ps = con.prepareStatement("SELECT * FROM `damage_measurement_rank` WHERE `player_id` = ?");
               ps.setInt(1, entry.getKey());
               rs = ps.executeQuery();
               if (rs.next()) {
                  ps = con.prepareStatement("UPDATE `damage_measurement_rank` SET `damage` = ? WHERE `player_id` = ?");
                  ps.setLong(1, entry.getValue());
                  ps.setInt(2, entry.getKey());
                  ps.executeUpdate();
                  ps.close();
               } else {
                  saveNewRecord(con, ps, entry.getKey(), entry.getValue());
               }
            }
         }
      } catch (SQLException var18) {
      } finally {
         try {
            if (ps != null) {
               ps.close();
               PreparedStatement var20 = null;
            }

            if (rs != null) {
               rs.close();
               ResultSet var21 = null;
            }
         } catch (SQLException var15) {
         }
      }
   }

   public static void saveNewRecord(Connection con, PreparedStatement ps, Integer playerID, long damage) {
      try {
         ps = con.prepareStatement("INSERT INTO `damage_measurement_rank` (`player_id`, `damage`) VALUES (?, ?)");
         ps.setInt(1, playerID);
         ps.setLong(2, damage);
         ps.executeUpdate();
         ps.close();
      } catch (SQLException var6) {
         var6.printStackTrace();
      }
   }

   public static void removeRecord(int playerID) {
      rank.remove(playerID);
      rank = sortByValue(rank);
      rewardRank.remove(playerID);
      rewardRank = sortByValue(rewardRank);
      saveRank();
   }

   public static void editRecord(int playerID, long damage) {
      for (Integer ran : lastweekRanks.keySet()) {
         if ((Integer)lastweekRanks.get(ran).left == MapleClient.getAccountIDByCharID(playerID)) {
            rank.put(playerID, damage);
            rank = sortByValue(rank);
            saveRank();
            return;
         }
      }

      rank.put(playerID, damage);
      rank = sortByValue(rank);
      rewardRank.put(playerID, damage);
      rewardRank = sortByValue(rewardRank);
      saveRank();
      if (rank.size() > 0) {
         int ranker1 = -1;
         int ranker2 = -1;
         int ranker3 = -1;

         for (Integer a : rank.keySet()) {
            int temprank = a;
            if (temprank > 0 && ranker1 <= 0) {
               ranker1 = a;
               if (!DBConfig.isGanglim) {
                  break;
               }
            } else if (temprank > 0 && ranker2 <= 0) {
               ranker2 = a;
            } else if (temprank > 0 && ranker3 <= 0) {
               ranker3 = a;
               break;
            }
         }

         numberOneNPC = new PlayerNPC(MapleCharacter.loadCharFromDBFake(ranker1, true), 9901001, 680000710);
         if (DBConfig.isGanglim) {
            if (ranker2 > 0) {
               numberTwoNPC = new PlayerNPC(MapleCharacter.loadCharFromDBFake(ranker2, true), 9901003, 680000710);
            }

            if (ranker3 > 0) {
               numberThreeNPC = new PlayerNPC(MapleCharacter.loadCharFromDBFake(ranker3, true), 9901004, 680000710);
            }
         }
      }
   }

   public static long getDamage(Integer playerID) {
      if (!rank.containsKey(playerID)) {
         return 0L;
      } else {
         for (Entry<Integer, Long> info : rank.entrySet()) {
            if (info.getKey().equals(playerID)) {
               return info.getValue();
            }
         }

         return 0L;
      }
   }

   public static int getRank(Integer playerID) {
      int index = 1;
      if (!rank.containsKey(playerID)) {
         return 0;
      } else {
         for (Entry<Integer, Long> info : rank.entrySet()) {
            if (info.getKey().equals(playerID)) {
               break;
            }

            index++;
         }

         return index;
      }
   }

   public static Map<Integer, Long> sortByValue(Map<Integer, Long> wordCounts) {
      return wordCounts.entrySet()
         .stream()
         .sorted(Entry.<Integer, Long>comparingByValue().reversed())
         .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
   }

   public static String getUnit(long damage) {
      int k = 0;
      int m = 0;
      int g = 0;
      int t = 0;
      int p = 0;
      long d = damage;
      p = (int)(damage / 10000000000000000L);
      if (p > 0) {
         d = damage - p * 10000000000000000L;
      }

      t = (int)(d / 1000000000000L);
      if (t > 0) {
         d -= t * 1000000000000L;
      }

      g = (int)(d / 100000000L);
      if (g > 0) {
         d -= g * 100000000L;
      }

      m = (int)(d / 10000L);
      if (m > 0) {
         d -= m * 10000L;
      }

      StringBuilder unit = new StringBuilder();
      if (p > 0) {
         unit.append(p).append("경 ");
      }

      if (t > 0) {
         unit.append(t).append("조 ");
      }

      if (g > 0) {
         unit.append(g).append("억 ");
      }

      return unit.toString();
   }

   public static String getRanks(int limit) {
      StringBuilder ret = new StringBuilder();
      int i = 1;

      for (Entry<Integer, Long> info : rank.entrySet()) {
         if (i < 10) {
            ret.append("#Cgray#00#b#e").append(i).append("위#n#k");
         } else if (i >= 10 && i < 100) {
            ret.append("#Cgray#0#b#e").append(i).append("위#n#k");
         } else {
            ret.append("#Cgray##b#e").append(i).append("위#n#k");
         }

         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT `name`, `level`, `job` FROM `characters` WHERE `id` = ?");
            ps.setInt(1, info.getKey());
            ResultSet rs = ps.executeQuery();

            boolean find;
            for (find = false; rs.next(); find = true) {
               if (DBConfig.isGanglim) {
                  ret.append(" Lv.").append(rs.getInt("level")).append(" ").append(rs.getString("name")).append(" #b");
                  ret.append(JobConstants.getPlayerJobs(rs.getInt("job")));
                  ret.append("#k #e");
               } else {
                  ret.append(" Lv.").append(rs.getInt("level")).append("  ").append(rs.getString("name")).append("   #b전투력#k : #e");
               }

               ret.append(getUnit(info.getValue()));
               ret.append("#n\r\n");
            }

            if (!find) {
               ret.append(" 삭제된 캐릭터\r\n");
            }

            rs.close();
            ps.close();
         } catch (SQLException var11) {
         }

         if (limit <= i++) {
            break;
         }
      }

      return ret.toString();
   }

   public static String getRewardRanks(int limit) {
      StringBuilder ret = new StringBuilder();
      int i = 1;

      for (Entry<Integer, Long> info : rewardRank.entrySet()) {
         if (i < 10) {
            ret.append("#Cgray#00#b#e").append(i).append("위#n#k");
         } else if (i >= 10 && i < 100) {
            ret.append("#Cgray#0#b#e").append(i).append("위#n#k");
         } else {
            ret.append("#Cgray##b#e").append(i).append("위#n#k");
         }

         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT `name`, `level`, `job` FROM `characters` WHERE `id` = ?");
            ps.setInt(1, info.getKey());
            ResultSet rs = ps.executeQuery();

            boolean find;
            for (find = false; rs.next(); find = true) {
               if (DBConfig.isGanglim) {
                  ret.append(" Lv.").append(rs.getInt("level")).append(" ").append(rs.getString("name")).append(" #b");
                  ret.append(JobConstants.getPlayerJobs(rs.getInt("job")));
                  ret.append("#k #e");
               } else {
                  ret.append(" Lv.").append(rs.getInt("level")).append("  ").append(rs.getString("name")).append("   #b전투력#k : #e");
               }

               ret.append(JobConstants.getPlayerJobs(rs.getInt("job"))).append(" ");
               ret.append(getUnit(info.getValue()));
               ret.append("#n\r\n");
            }

            if (!find) {
               ret.append(" 삭제된 캐릭터\r\n");
            }

            rs.close();
            ps.close();
         } catch (SQLException var11) {
         }

         if (limit <= i++) {
            break;
         }
      }

      return ret.toString();
   }

   public static void rankReward() {
      rewardRank = sortByValue(rewardRank);
      LinkedHashMap<Integer, Integer> ranks = new LinkedHashMap<>();

      for (Integer a : rewardRank.keySet()) {
         int accountid = MapleClient.getAccountIDByCharID(a);
         if (accountid != -1 && ranks.get(accountid) == null) {
            ranks.put(accountid, a);
         }
      }

      StringBuilder sb = new StringBuilder();
      Calendar cal = Calendar.getInstance();
      cal.set(7, 2);
      cal.set(10, 0);
      cal.set(12, 0);
      cal.set(13, 0);
      cal.set(14, 0);
      cal.set(11, 0);
      cal.set(5, cal.getTime().getDate() + 7);
      sb.append("lastBuffDay = ").append(cal.getTimeInMillis()).append("\r\n");
      lastweekBuffTime = cal.getTimeInMillis();
      lastweekRanks = new LinkedHashMap<>();
      int rankNumber = 0;

      for (Integer accid : ranks.keySet()) {
         sb.append(rankNumber).append(" = {").append("\r\n");
         sb.append("\t").append("accountid = ").append(accid).append("\r\n");
         sb.append("\t").append("chrid = ").append(ranks.get(accid)).append("\r\n");
         sb.append("}\r\n");
         lastweekRanks.put(++rankNumber, new Pair<>(accid, ranks.get(accid)));
      }

      try {
         OutputStream output = new FileOutputStream(DBConfig.isGanglim ? "data/Ganglim/DamageRank.data" : "data/Jin/DamageRank.data");
         String str = sb.toString();
         byte[] by = str.getBytes();
         output.write(by);
      } catch (Exception var7) {
         System.out.println("DamageRank Err");
         var7.printStackTrace();
      }
   }

   public static void applyDamageRankBuff(MapleCharacter chr) {
      for (int i = 80003189; i <= 80003192; i++) {
         chr.temporaryStatResetBySkillID(i);
      }

      if (System.currentTimeMillis() < lastweekBuffTime) {
         int rrranks = -1;

         for (Integer ran : lastweekRanks.keySet()) {
            if ((Integer)lastweekRanks.get(ran).right == chr.getId()) {
               rrranks = ran;
               break;
            }
         }

         if (rrranks > -1) {
            int size = lastweekRanks.size();
            int rank30per = (int)(size / 100.0 * 30.0);
            int rank70per = (int)(size / 100.0 * 70.0);
            boolean rank1 = rrranks == 1 || rrranks == 2 || rrranks == 3;
            boolean rank30p = rrranks <= rank30per;
            boolean rank70p = rrranks <= rank70per;
            if (rank1) {
               Map<SecondaryStatFlag, Integer> statList = new HashMap<>();
               if (DBConfig.isGanglim) {
                  statList.put(SecondaryStatFlag.indiePAD, 500);
                  statList.put(SecondaryStatFlag.indieMAD, 500);
                  statList.put(SecondaryStatFlag.indieSTR, 1000);
                  statList.put(SecondaryStatFlag.indieDEX, 1000);
                  statList.put(SecondaryStatFlag.indieINT, 1000);
                  statList.put(SecondaryStatFlag.indieLUK, 1000);
               } else {
                  statList.put(SecondaryStatFlag.indiePAD, 150);
                  statList.put(SecondaryStatFlag.indieMAD, 150);
                  statList.put(SecondaryStatFlag.indieSTR, 200);
                  statList.put(SecondaryStatFlag.indieDEX, 200);
                  statList.put(SecondaryStatFlag.indieINT, 200);
                  statList.put(SecondaryStatFlag.indieLUK, 200);
               }

               chr.temporaryStatSet(80003189, 1, Integer.MAX_VALUE, statList);
            } else if (rank30p) {
               Map<SecondaryStatFlag, Integer> statList = new HashMap<>();
               if (DBConfig.isGanglim) {
                  statList.put(SecondaryStatFlag.indiePAD, 200);
                  statList.put(SecondaryStatFlag.indieMAD, 200);
                  statList.put(SecondaryStatFlag.indieSTR, 500);
                  statList.put(SecondaryStatFlag.indieDEX, 500);
                  statList.put(SecondaryStatFlag.indieINT, 500);
                  statList.put(SecondaryStatFlag.indieLUK, 500);
               } else {
                  statList.put(SecondaryStatFlag.indiePAD, 75);
                  statList.put(SecondaryStatFlag.indieMAD, 75);
                  statList.put(SecondaryStatFlag.indieSTR, 50);
                  statList.put(SecondaryStatFlag.indieDEX, 50);
                  statList.put(SecondaryStatFlag.indieINT, 50);
                  statList.put(SecondaryStatFlag.indieLUK, 50);
               }

               chr.temporaryStatSet(80003190, 1, Integer.MAX_VALUE, statList);
            } else if (rank70p) {
               Map<SecondaryStatFlag, Integer> statList = new HashMap<>();
               if (DBConfig.isGanglim) {
                  statList.put(SecondaryStatFlag.indiePAD, 100);
                  statList.put(SecondaryStatFlag.indieMAD, 100);
                  statList.put(SecondaryStatFlag.indieSTR, 300);
                  statList.put(SecondaryStatFlag.indieDEX, 300);
                  statList.put(SecondaryStatFlag.indieINT, 300);
                  statList.put(SecondaryStatFlag.indieLUK, 300);
               } else {
                  statList.put(SecondaryStatFlag.indiePAD, 40);
                  statList.put(SecondaryStatFlag.indieMAD, 40);
                  statList.put(SecondaryStatFlag.indieSTR, 25);
                  statList.put(SecondaryStatFlag.indieDEX, 25);
                  statList.put(SecondaryStatFlag.indieINT, 25);
                  statList.put(SecondaryStatFlag.indieLUK, 25);
               }

               chr.temporaryStatSet(80003191, 1, Integer.MAX_VALUE, statList);
            } else {
               Map<SecondaryStatFlag, Integer> statList = new HashMap<>();
               if (DBConfig.isGanglim) {
                  statList.put(SecondaryStatFlag.indiePAD, 50);
                  statList.put(SecondaryStatFlag.indieMAD, 50);
                  statList.put(SecondaryStatFlag.indieSTR, 150);
                  statList.put(SecondaryStatFlag.indieDEX, 150);
                  statList.put(SecondaryStatFlag.indieINT, 150);
                  statList.put(SecondaryStatFlag.indieLUK, 150);
               } else {
                  statList.put(SecondaryStatFlag.indiePAD, 20);
                  statList.put(SecondaryStatFlag.indieMAD, 20);
                  statList.put(SecondaryStatFlag.indieSTR, 15);
                  statList.put(SecondaryStatFlag.indieDEX, 15);
                  statList.put(SecondaryStatFlag.indieINT, 15);
                  statList.put(SecondaryStatFlag.indieLUK, 15);
               }

               chr.temporaryStatSet(80003192, 1, Integer.MAX_VALUE, statList);
            }
         }
      }
   }

   static {
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim" : "data/Jin", "DamageRank.data");
      if (table != null) {
         lastweekBuffTime = table.getProperty("lastBuffDay", 0L);

         for (Table child : table.list()) {
            int rank = Integer.parseInt(child.getName()) + 1;
            int accid = child.getProperty("accountid", 0);
            int chrid = child.getProperty("chrid", 0);
            lastweekRanks.put(rank, new Pair<>(accid, chrid));
         }
      }
   }
}
