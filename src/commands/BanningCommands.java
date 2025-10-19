package commands;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import network.auction.AuctionServer;
import network.game.GameServer;
import network.shop.CashShopServer;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.utils.StringUtil;

public class BanningCommands implements Command {
   @Override
   public void execute(MapleClient c, String[] splitted) throws Exception {
      GameServer cserv = c.getChannelServer();
      if (!splitted[0].equals("밴") && !splitted[0].equals("영구밴")) {
         if (splitted[0].equals("기간밴")) {
            String targetName = splitted[1];
            String reason = splitted[2];
            int numDay = Integer.parseInt(splitted[3]);
            MapleCharacter target = null;
            int accountID = -1;
            String accountName = "";
            String banLogs = "";

            try (Connection con = DBConnection.getConnection()) {
               PreparedStatement ps = con.prepareStatement("SELECT `accountid` FROM characters WHERE `name` = ?");
               ps.setString(1, targetName);
               ResultSet rs = ps.executeQuery();

               while (rs.next()) {
                  accountID = rs.getInt("accountid");
               }

               rs.close();
               ps.close();
               ps = con.prepareStatement("SELECT `name`, `banreason` FROM accounts WHERE `id` = ?");
               ps.setInt(1, accountID);
               rs = ps.executeQuery();

               while (rs.next()) {
                  banLogs = rs.getString("banreason");
                  accountName = rs.getString("name");
               }

               rs.close();
               ps.close();
            } catch (SQLException var27) {
            }

            if (banLogs != null && !banLogs.isEmpty()) {
               banLogs = banLogs + "," + reason + "(기간밴)";
            } else {
               banLogs = reason + "(기간밴)";
            }

            for (MapleCharacter p : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
               if (p != null && p.getName().equals(targetName)) {
                  target = p;
                  break;
               }
            }

            for (MapleCharacter px : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
               if (px != null && px.getName().equals(targetName)) {
                  target = px;
                  break;
               }
            }

            for (GameServer cs : GameServer.getAllInstances()) {
               for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
                  if (chr != null && chr.getName().equals(targetName)) {
                     target = chr;
                     break;
                  }
               }
            }

            if (target == null) {
               try (Connection con = DBConnection.getConnection()) {
                  for (MapleCharacter pxx : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
                     if (pxx != null && pxx.getAccountID() == accountID) {
                        target = pxx;
                        break;
                     }
                  }

                  for (MapleCharacter pxxx : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
                     if (pxxx != null && pxxx.getAccountID() == accountID) {
                        target = pxxx;
                        break;
                     }
                  }

                  for (GameServer cs : GameServer.getAllInstances()) {
                     for (MapleCharacter chrx : cs.getPlayerStorage().getAllCharacters()) {
                        if (chrx != null && chrx.getAccountID() == accountID) {
                           target = chrx;
                           break;
                        }
                     }
                  }

                  if (target != null) {
                     Calendar cal = Calendar.getInstance();
                     cal.add(5, numDay);
                     DateFormat df = DateFormat.getInstance();
                     target.tempban(banLogs, cal, false);
                     c.getPlayer().dropMessage(6, splitted[1] + " 캐릭터가 " + df.format(cal.getTime()) + " 까지 성공적으로 밴 되었습니다.");
                  } else {
                     Calendar cal = Calendar.getInstance();
                     cal.add(5, numDay);
                     DateFormat df = DateFormat.getInstance();
                     PreparedStatement ps = con.prepareStatement("UPDATE accounts SET tempban = ?, banreason = ? WHERE id = ?");
                     Timestamp TS = new Timestamp(cal.getTimeInMillis());
                     ps.setTimestamp(1, TS);
                     ps.setString(2, banLogs);
                     ps.setInt(3, accountID);
                     ps.execute();
                     ps.close();
                     c.getPlayer().dropMessage(6, splitted[1] + " 캐릭터가 " + df.format(cal.getTime()) + " 까지 오프라인 밴처리되었습니다.");
                  }
               } catch (SQLException var25) {
               }
            } else {
               Calendar cal = Calendar.getInstance();
               cal.add(5, numDay);
               DateFormat df = DateFormat.getInstance();
               target.tempban(banLogs, cal, false);
               c.getPlayer().dropMessage(6, splitted[1] + " 캐릭터가 " + df.format(cal.getTime()) + " 까지 성공적으로 밴 되었습니다.");
            }
         } else if (splitted[0].equals("밴풀기")) {
            if (splitted.length < 2) {
               c.getPlayer().dropMessage(6, "밴풀기 <캐릭터이름>");
            } else {
               byte result = MapleClient.unban(splitted[1]);
               if (result == -1) {
                  c.getPlayer().dropMessage(6, splitted[1] + " 캐릭터를 발견하지 못했습니다.");
               } else if (result == -2) {
                  c.getPlayer().dropMessage(6, splitted[1] + " 캐릭터의 밴을 해제하는데 오류가 발생했습니다.");
               } else {
                  c.getPlayer().dropMessage(6, splitted[1] + " 캐릭터가 성공적으로 밴이 해제되었습니다.");
               }
            }
         } else if (splitted[0].equals("접속끊기")) {
            int level = 0;
            MapleCharacter victim;
            if (splitted[1].charAt(0) == '-') {
               level = StringUtil.countCharacters(splitted[1], 'f');
               victim = cserv.getPlayerStorage().getCharacterByName(splitted[2]);
            } else {
               victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            }

            try (Connection con = DBConnection.getConnection()) {
               PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = 0 WHERE id = ?");
               ps.setInt(1, victim.getId());
               ps.executeUpdate();
               ps.close();
            } catch (Exception var23) {
               var23.printStackTrace();
            }

            if (level < 2) {
               victim.getClient().getSession().close();
               System.out.println("팅겼다고인마");
               if (level >= 1) {
                  victim.getClient().disconnect(false);
               }
            } else {
               c.getPlayer().dropMessage(6, "Please use dc -f instead.");
            }
         }
      } else {
         if (splitted.length < 3) {
            return;
         }

         boolean byAdminClient = false;
         boolean hellban = splitted[0].equals("영구밴");
         String targetName = splitted[1];
         StringBuilder sb = new StringBuilder(c.getPlayer().getName());
         sb.append(" banned ").append(splitted[1]).append(": ").append(StringUtil.joinStringFrom(splitted, 2));
         int accountID = -1;
         String banLogs = "";

         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT `accountid` FROM characters WHERE `name` = ?");
            ps.setString(1, targetName);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
               accountID = rs.getInt("accountid");
            }

            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT `banreason` FROM accounts WHERE `id` = ?");
            ps.setInt(1, accountID);
            rs = ps.executeQuery();

            while (rs.next()) {
               banLogs = rs.getString("banreason");
            }

            rs.close();
            ps.close();
         } catch (SQLException var21) {
         }

         if (banLogs != null && !banLogs.isEmpty()) {
            banLogs = banLogs + "," + sb.toString();
         } else {
            banLogs = sb.toString();
         }

         MapleCharacter target = null;

         for (MapleCharacter pxxxx : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
            if (pxxxx != null && pxxxx.getName().equals(targetName)) {
               target = pxxxx;
               break;
            }
         }

         for (MapleCharacter pxxxxx : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
            if (pxxxxx != null && pxxxxx.getName().equals(targetName)) {
               target = pxxxxx;
               break;
            }
         }

         for (GameServer cs : GameServer.getAllInstances()) {
            for (MapleCharacter chrxx : cs.getPlayerStorage().getAllCharacters()) {
               if (chrxx != null && chrxx.getName().equals(targetName)) {
                  target = chrxx;
                  break;
               }
            }
         }

         if (target == null) {
            for (MapleCharacter pxxxxxx : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
               if (pxxxxxx != null && pxxxxxx.getAccountID() == accountID) {
                  target = pxxxxxx;
                  break;
               }
            }

            for (MapleCharacter pxxxxxxx : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
               if (pxxxxxxx != null && pxxxxxxx.getAccountID() == accountID) {
                  target = pxxxxxxx;
                  break;
               }
            }

            for (GameServer cs : GameServer.getAllInstances()) {
               for (MapleCharacter chrxxx : cs.getPlayerStorage().getAllCharacters()) {
                  if (chrxxx != null && chrxxx.getAccountID() == accountID) {
                     target = chrxxx;
                     break;
                  }
               }
            }

            if (target != null) {
               if (target.ban(banLogs, true, false, hellban)) {
                  c.getPlayer().dropMessage(6, targetName + "(이)가 밴처리 되었습니다.");
               } else {
                  c.getPlayer().dropMessage(6, "밴에 실패했습니다.");
               }

               target.serialBan(byAdminClient);
            } else if (MapleCharacter.ban(targetName, banLogs, false, c.getPlayer().isAdmin() ? 250 : c.getPlayer().getGMLevel(), false)) {
               c.getPlayer().dropMessage(6, targetName + " 오프라인 밴 성공.");
            } else {
               c.getPlayer().dropMessage(6, targetName + " 를 밴 하는데 실패했습니다.");
            }
         } else {
            if (target.ban(banLogs, true, false, hellban)) {
               c.getPlayer().dropMessage(6, targetName + "(이)가 밴처리 되었습니다.");
            } else {
               c.getPlayer().dropMessage(6, "밴에 실패했습니다.");
            }

            target.serialBan(byAdminClient);
         }
      }
   }

   @Override
   public CommandDefinition[] getDefinition() {
      return new CommandDefinition[]{
         new CommandDefinition("밴", "<캐릭터이름> <이유>", "해당 ip와 mac주소, 계정을 영구적으로 밴 시킵니다.", 3),
         new CommandDefinition("밴풀기", "<캐릭터이름>", "밴 된 ip와 mac주소, 계정의 밴을 해제합니다.", 3),
         new CommandDefinition("기간밴", "<캐릭터이름> <이유> <밴 될 일수>", "해당 계정을 해당 일 수 동안 밴 시킵니다.", 3),
         new CommandDefinition("접속끊기", "[-f] <캐릭터이름>", "해당 캐릭터를 강제로 접속종료시킵니다. 현접에걸렸다면 -f 옵션을 사용하세요.", 3)
      };
   }
}
