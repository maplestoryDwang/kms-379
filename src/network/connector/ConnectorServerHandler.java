package network.connector;

import constants.ServerConstants;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import network.connector.panel.ConnectorPanel;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import objects.users.LoginCrypto;
import objects.utils.FileoutputUtil;
import objects.utils.LoginCryptoLegacy;
import objects.utils.Triple;

public class ConnectorServerHandler {
   public static final void HandlePacket(RecvPacketOpcode header, PacketDecoder slea, ConnectorClient c) throws InterruptedException {
      DefaultTableModel model = null;
      switch (header) {
         case LoginRequest: {
            PacketEncoder mplew = new PacketEncoder();
            String id = slea.readMapleAsciiString2();
            String pw = slea.readMapleAsciiString2();
            String mac = slea.readMapleAsciiString2();
            String key = slea.readMapleAsciiString2();
            int res = login(id, pw, mac, key, c, false);
            mplew.write(SendPacketOpcode.LOGIN.getValue());
            mplew.write((byte)res);
            if (res == 2) {
               c.setId(id);
               c.setPasswrod(pw);
               model = (DefaultTableModel)ConnectorPanel.jTable1.getModel();

               try {
                  if (model.getRowCount() >= 1) {
                     for (int i = model.getRowCount() - 1; i >= 0; i--) {
                        if (model.getValueAt(i, 0).toString().equals(c.getId())) {
                           model.removeRow(i);
                           break;
                        }
                     }
                  }
               } catch (Exception var11) {
                  System.out.println("커넥터 서버 오류 발생");
                  var11.printStackTrace();
               }

               model.addRow(new Object[]{c.getId(), c.getPassword(), c.getSession().remoteAddress(), "", c, c.getCharName()});
               if (ConnectorServer.getInstance().getClientStorage().getLoginClient(id) != null) {
                  ConnectorServer.getInstance().getClientStorage().removeLoginClient(id);
               }

               ConnectorServer.getInstance().getClientStorage().registerClient(c, id);
            }

            c.send(mplew.getPacket());
            break;
         }
         case AccountInfoRequest:
            c.send(PacketCreator.sendCharInfo(c.getCharName(), getCharacterId(c.getCharName())));
            break;
         case Heartbeat:
            c.setPingTime(System.currentTimeMillis());
            break;
         case ProcessDetected: {
            String text = slea.readMapleAsciiString2();
            String key = slea.readMapleAsciiString2();
            Ban(c, text, key);
            ConnectorLog(text, c);
            break;
         }
         case ProcessListResponse:
            model = (DefaultTableModel)ConnectorPanel.jTable3.getModel();
            model.getDataVector().removeAllElements();
            int as = slea.readInt();

            for (int i = 0; i < as; i++) {
               String a = slea.readMapleAsciiString2();
               String b = slea.readMapleAsciiString2();
               model.addRow(new Object[]{a, b});
            }
            break;
         case GameStartRequest:
            String auth = getRandomString(20);
            String id = slea.readMapleAsciiString2();
            String pw = slea.readMapleAsciiString2();
            if (ServerConstants.authlist2.get(id) != null) {
               ServerConstants.authlist.remove(c.getAuth());
               ServerConstants.authlist2.remove(id);
            }

            ServerConstants.authlist.put(auth, new Triple<>(id, pw, auth));
            ServerConstants.authlist2.put(id, new Triple<>(id, pw, auth));
            c.setAuth(auth);
            PacketEncoder mplew = new PacketEncoder();
            mplew.write(SendPacketOpcode.GameStartResponse.getValue());
            mplew.writeMapleAsciiString2(auth);
            c.send(mplew.getPacket());
            break;
         case CharacterListRequest:
            c.send(PacketCreator.SendCharacterList(c.getAccountId()));
            break;
         case SetMainCharacterRequest:
            String name = slea.readMapleAsciiString2();
            setMainCharacter(c.getAccountId(), name);
            c.setCharName(name);
      }
   }

   public static void ConnectorLog(String text, ConnectorClient c) {
      try {
         if (ServerConstants.ConnectorLog) {
            ConnectorPanel.jTextArea2.append(text + "\r\n");
            ConnectorPanel.jTextArea2.setCaretPosition(ConnectorPanel.jTextArea2.getDocument().getLength());
            FileoutputUtil.log("Log_ConnectorLog.rtf", text);
         }
      } catch (NullPointerException var3) {
         var3.printStackTrace();
      }
   }

   public static void AllMessage(byte[] data) {
      for (ConnectorClient c : ConnectorServer.getInstance().getClientStorage().getLoginClients()) {
         c.send(data);
      }
   }

   public static boolean bancheck(String key, String ip, Connection con) {
      PreparedStatement ps = null;
      ResultSet rs = null;

      boolean ex;
      try {
         ps = con.prepareStatement("SELECT * FROM connectorban WHERE `connecterkey` = ? OR `ip` = ?");
         ps.setString(1, key);
         ps.setString(2, "111.111.111.111");
         rs = ps.executeQuery();
         if (!rs.next()) {
            return true;
         }

         ex = false;
      } catch (SQLException var21) {
         var21.printStackTrace();
         return true;
      } finally {
         if (ps != null) {
            try {
               ps.close();
            } catch (SQLException var20) {
               Logger.getLogger(ConnectorServerHandler.class.getName()).log(Level.SEVERE, null, var20);
            }
         }

         if (rs != null) {
            try {
               rs.close();
            } catch (SQLException var19) {
               Logger.getLogger(ConnectorServerHandler.class.getName()).log(Level.SEVERE, null, var19);
            }
         }
      }

      return ex;
   }

   public static int login(String login, String pwd, String mac, String key, ConnectorClient c, boolean checkpw) {
      Connection con = null;
      PreparedStatement ps = null;
      ResultSet rs = null;

      byte charname;
      try {
         con = DBConnection.getConnection();
         ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?");
         ps.setString(1, login);
         rs = ps.executeQuery();
         if (!rs.next()) {
            return 1;
         }

         if (rs.getInt("allowed") == 1) {
            return 4;
         }

         String passhash = rs.getString("password");
         String salt = rs.getString("salt");
         int banned = rs.getInt("banned");
         int id = rs.getInt("id");
         String connecterKey = rs.getString("connecterKey");
         ps.close();
         if (checkpw) {
            if (LoginCryptoLegacy.isLegacyPassword(passhash) && LoginCryptoLegacy.checkPassword(pwd, passhash)) {
               ps = con.prepareStatement("UPDATE accounts SET allowed = ?, macs = ?, connecterKey = ?, connecterClient = ?, connecterIP = ? WHERE name = ?");
               ps.setByte(1, (byte)1);
               ps.setString(2, mac);
               ps.setString(3, key);
               ps.setString(4, c.toString());
               ps.setString(5, c.getIP());
               ps.setString(6, login);
               ps.executeUpdate();
               ps.close();
               c.setAccountId(id);
               String charnamex = getFirstCharacter(c.getAccountId(), con, c.getId());
               c.setCharName(charnamex);
               return 2;
            }

            if (LoginCrypto.checkSaltedSha512Hash(passhash, pwd, salt)) {
               ps = con.prepareStatement("UPDATE accounts SET allowed = ?, macs = ?, connecterKey = ?, connecterClient = ?, connecterIP = ? WHERE name = ?");
               ps.setByte(1, (byte)1);
               ps.setString(2, mac);
               ps.setString(3, key);
               ps.setString(4, c.toString());
               ps.setString(5, c.getIP());
               ps.setString(6, login);
               ps.executeUpdate();
               ps.close();
               c.setAccountId(id);
               String charnamex = getFirstCharacter(c.getAccountId(), con, c.getId());
               c.setCharName(charnamex);
               return 2;
            }

            if (salt == null && LoginCrypto.checkSha1Hash(passhash, pwd)) {
               ps = con.prepareStatement("UPDATE accounts SET allowed = ?, macs = ?, connecterKey = ?, connecterClient = ?, connecterIP = ? WHERE name = ?");
               ps.setByte(1, (byte)1);
               ps.setString(2, mac);
               ps.setString(3, key);
               ps.setString(4, c.toString());
               ps.setString(5, c.getIP());
               ps.setString(6, login);
               ps.executeUpdate();
               ps.close();
               c.setAccountId(id);
               String charnamex = getFirstCharacter(c.getAccountId(), con, c.getId());
               c.setCharName(charnamex);
               return 2;
            }

            if (banned > 0) {
               return 3;
            }

            return 0;
         }

         if (!passhash.equals(pwd)) {
            return 0;
         }

         if (banned <= 0) {
            ps = con.prepareStatement("UPDATE accounts SET allowed = ?, macs = ?, connecterKey = ?, connecterClient = ?, connecterIP = ? WHERE name = ?");
            ps.setByte(1, (byte)1);
            ps.setString(2, mac);
            ps.setString(3, key);
            ps.setString(4, c.toString());
            ps.setString(5, c.getIP());
            ps.setString(6, login);
            ps.executeUpdate();
            c.setAccountId(id);
            c.setConnecterKey(connecterKey);
            String charnamex = getMainCharacter(c.getAccountId(), c.getId());
            c.setCharName(charnamex);
            return 2;
         }

         charname = 3;
      } catch (SQLException var60) {
         var60.printStackTrace();
         return 5;
      } finally {
         if (ps != null) {
            try {
               ps.close();
            } catch (SQLException var59) {
               Logger.getLogger(ConnectorServerHandler.class.getName()).log(Level.SEVERE, null, var59);
            }
         }

         if (rs != null) {
            try {
               rs.close();
            } catch (SQLException var58) {
               Logger.getLogger(ConnectorServerHandler.class.getName()).log(Level.SEVERE, null, var58);
            }
         }

         if (con != null) {
            try {
               con.close();
            } catch (SQLException var57) {
               var57.printStackTrace();
            }
         }
      }

      return charname;
   }

   public static int getCharacterId(String name) {
      Connection con = null;
      PreparedStatement ps = null;
      ResultSet rs = null;

      int e;
      try {
         con = DBConnection.getConnection();
         ps = con.prepareStatement("SELECT id FROM characters WHERE name = ?");
         ps.setString(1, name);
         rs = ps.executeQuery();
         if (!rs.next()) {
            return 0;
         }

         e = rs.getInt("id");
      } catch (SQLException var25) {
         var25.printStackTrace();
         return 0;
      } finally {
         if (ps != null) {
            try {
               ps.close();
            } catch (SQLException var24) {
               Logger.getLogger(ConnectorServerHandler.class.getName()).log(Level.SEVERE, null, var24);
            }
         }

         if (rs != null) {
            try {
               rs.close();
            } catch (SQLException var23) {
               Logger.getLogger(ConnectorServerHandler.class.getName()).log(Level.SEVERE, null, var23);
            }
         }

         if (con != null) {
            try {
               con.close();
            } catch (SQLException var22) {
               var22.printStackTrace();
            }
         }
      }

      return e;
   }

   public static String getMainCharacter(int code, String name) {
      Connection con = null;
      PreparedStatement ps = null;
      ResultSet rs = null;

      String e;
      try {
         con = DBConnection.getConnection();
         ps = con.prepareStatement("SELECT maincharacter FROM accounts WHERE id = ?");
         ps.setInt(1, code);
         rs = ps.executeQuery();
         if (!rs.next()) {
            return name;
         }

         e = rs.getString("maincharacter");
      } catch (SQLException var26) {
         var26.printStackTrace();
         return name;
      } finally {
         if (ps != null) {
            try {
               ps.close();
            } catch (SQLException var25) {
               Logger.getLogger(ConnectorServerHandler.class.getName()).log(Level.SEVERE, null, var25);
            }
         }

         if (rs != null) {
            try {
               rs.close();
            } catch (SQLException var24) {
               var24.printStackTrace();
            }
         }

         if (con != null) {
            try {
               con.close();
            } catch (SQLException var23) {
               var23.printStackTrace();
            }
         }
      }

      return e;
   }

   public static String getFirstCharacter(int code, Connection con, String name) {
      PreparedStatement ps = null;
      ResultSet rs = null;

      String e;
      try {
         ps = con.prepareStatement("SELECT name FROM characters WHERE accountid = ?");
         ps.setInt(1, code);
         rs = ps.executeQuery();
         if (!rs.next()) {
            return name;
         }

         e = rs.getString("name");
      } catch (SQLException var21) {
         var21.printStackTrace();
         return name;
      } finally {
         if (ps != null) {
            try {
               ps.close();
            } catch (SQLException var20) {
               Logger.getLogger(ConnectorServerHandler.class.getName()).log(Level.SEVERE, null, var20);
            }
         }

         if (rs != null) {
            try {
               rs.close();
            } catch (SQLException var19) {
               var19.printStackTrace();
            }
         }
      }

      return e;
   }

   private static String getRandomString(int length) {
      StringBuffer buffer = new StringBuffer();
      Random random = new Random();
      String[] chars = "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,0,1,2,3,4,5,6,7,8,9".split(",");

      for (int i = 0; i < length; i++) {
         buffer.append(chars[random.nextInt(chars.length)]);
      }

      return buffer.toString();
   }

   public static void Ban(ConnectorClient c, String d, String key) {
      Connection con = null;
      PreparedStatement ps = null;
      ResultSet rs = null;

      try {
         con = DBConnection.getConnection();
         ps = con.prepareStatement("SELECT * FROM connectorban WHERE ip = ? or connecterkey = ?");
         ps.setString(1, "111.111.111.111");
         ps.setString(2, key);
         rs = ps.executeQuery();
         if (!rs.next()) {
            ps.close();
            ps = con.prepareStatement("INSERT INTO connectorban (`connecterkey`, `ip`, `comment`) VALUES (?, ?, ?)");
            ps.setString(1, key);
            ps.setString(2, c.getIP());
            ps.setString(3, d);
            ps.execute();
            ps.close();
            ps = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE connecterKey = ? or SessionIP = ?");
            ps.setString(1, "영구 정지 당하셨습니다.");
            ps.setString(2, key);
            ps.setString(3, c.getIP());
            ps.executeUpdate();
            ps.close();
            if (c.getSecondId() != null) {
               ps = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE connecterKey = ? or SessionIP = ?");
               ps.setString(1, "영구 정지 당하셨습니다.");
               ps.setString(2, key);
               ps.setString(3, c.getIP());
               ps.executeUpdate();
               ps.close();
            }

            return;
         }
      } catch (SQLException var26) {
         System.out.println("Ban Error : ");
         var26.printStackTrace();
         return;
      } finally {
         if (ps != null) {
            try {
               ps.close();
            } catch (SQLException var25) {
            }
         }

         if (rs != null) {
            try {
               rs.close();
            } catch (SQLException var24) {
            }
         }

         if (con != null) {
            try {
               con.close();
            } catch (SQLException var23) {
               var23.printStackTrace();
            }
         }
      }
   }

   public static int getMainCharId(ConnectorClient c) {
      Connection con = null;
      PreparedStatement ps = null;
      ResultSet rs = null;

      try {
         con = DBConnection.getConnection();
         ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?");
         ps.setString(1, c.getId());
         rs = ps.executeQuery();
         if (rs.next()) {
            String mainChar = rs.getString("maincharacter");
            rs.close();
            ps.close();
            if (mainChar != null) {
               ps = con.prepareStatement("SELECT * FROM characters WHERE name = ?");
               ps.setString(1, mainChar);
               rs = ps.executeQuery();
               if (rs.next()) {
                  int id = rs.getInt("id");
                  rs.close();
                  ps.close();
                  return id;
               }
            }
         }

         return -1;
      } catch (SQLException var27) {
         System.out.println("Ban Error : ");
         var27.printStackTrace();
         return -1;
      } finally {
         if (ps != null) {
            try {
               ps.close();
            } catch (SQLException var26) {
            }
         }

         if (rs != null) {
            try {
               rs.close();
            } catch (SQLException var25) {
            }
         }

         if (con != null) {
            try {
               con.close();
            } catch (SQLException var24) {
               var24.printStackTrace();
            }
         }
      }
   }

   public static void chats(String text, JTextArea t) {
      t.append(text);
      t.append("\r\n");
      t.setCaretPosition(t.getDocument().getLength());
   }

   public static Map<Integer, String> GetCharacterList(int accountId) {
      Connection con = null;
      PreparedStatement ps = null;
      ResultSet rs = null;
      Map<Integer, String> map = new HashMap<>();

      try {
         con = DBConnection.getConnection();
         ps = con.prepareStatement("SELECT id, name FROM characters WHERE accountid = ? ORDER BY level desc");
         ps.setInt(1, accountId);
         rs = ps.executeQuery();

         while (rs.next()) {
            map.put(rs.getInt("id"), rs.getString("name"));
         }
      } catch (SQLException var22) {
         var22.printStackTrace();
      } finally {
         if (con != null) {
            try {
               con.close();
            } catch (SQLException var21) {
               Logger.getLogger(ConnectorServerHandler.class.getName()).log(Level.SEVERE, null, var21);
            }
         }

         if (rs != null) {
            try {
               rs.close();
            } catch (SQLException var20) {
               Logger.getLogger(ConnectorServerHandler.class.getName()).log(Level.SEVERE, null, var20);
            }
         }

         if (ps != null) {
            try {
               ps.close();
            } catch (SQLException var19) {
               var19.printStackTrace();
            }
         }
      }

      return map;
   }

   public static void setMainCharacter(int code, String name) {
      Connection con = null;
      PreparedStatement ps = null;

      try {
         con = DBConnection.getConnection();
         ps = con.prepareStatement("UPDATE accounts SET maincharacter = ? WHERE id = ?");
         ps.setString(1, name);
         ps.setInt(2, code);
         ps.executeUpdate();
      } catch (SQLException var17) {
         var17.printStackTrace();
      } finally {
         if (ps != null) {
            try {
               ps.close();
            } catch (SQLException var16) {
               Logger.getLogger(ConnectorServerHandler.class.getName()).log(Level.SEVERE, null, var16);
            }
         }

         if (con != null) {
            try {
               con.close();
            } catch (SQLException var15) {
               var15.printStackTrace();
            }
         }
      }
   }
}
