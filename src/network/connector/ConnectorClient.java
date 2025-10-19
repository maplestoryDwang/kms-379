package network.connector;

import database.DBConnection;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.connector.panel.ConnectorPanel;
import network.game.GameServer;
import objects.users.MapleCharacter;
import objects.utils.FileoutputUtil;
import objects.utils.Timer;

public class ConnectorClient {
   public static final AttributeKey<ConnectorClient> CLIENTKEY = AttributeKey.valueOf("connectorclient_netty");
   private final Channel session;
   private final TundraAes m_send;
   private final TundraAes m_recv;
   private String id;
   private String pw;
   private String connecterkey;
   private String hardid;
   private String mac;
   private String charname;
   private String sid;
   private String spw;
   private final List<String> ingamechar = new ArrayList<>();
   private MapleCharacter chr;
   private MapleCharacter secondchr;
   private final transient Lock mutex = new ReentrantLock(true);
   private int pingv;
   private int accountid;
   private int saccountid;
   private long pingtime;
   private long chattime;
   private transient ScheduledFuture<?> SkillTask;
   private boolean asd = false;
   private int bcd;
   private static byte[] sShiftKey = new byte[]{
      -97, -109, 26, 101, 57, -95, -106, 76, 19, -74, -89, 34, 33, -101, 119, -121, -26, 99, 69, -128, -109, 78, 69, -104, 11, 52, -4, 108, 48, 29, 100, 126
   };
   private String auth;

   public ConnectorClient(Channel socket, TundraAes send, TundraAes recv) {
      this.session = socket;
      this.m_send = send;
      this.m_recv = recv;
   }

   public void send(byte[] data) {
      if (data != null) {
         byte[] temp = new byte[data.length];
         System.arraycopy(data, 0, temp, 0, data.length);
         this.session.writeAndFlush(temp);
      }
   }

   public final MapleCharacter getSearchChar() {
      for (GameServer cs : GameServer.getAllInstances()) {
         for (MapleCharacter player : cs.getPlayerStorage().getAllCharacters()) {
            if (player.getClient().getAccountName().equals(this.id)) {
               return player;
            }
         }
      }

      return null;
   }

   public final void addInGameChar(String s) {
      this.ingamechar.add(s);
   }

   public final void removeInGameChar(String s) {
      this.ingamechar.remove(s);
   }

   public final List<String> getIngameChar() {
      return this.ingamechar;
   }

   public final void setChatTime(long a) {
      this.chattime = a;
   }

   public long getChatTime() {
      return this.chattime;
   }

   public final void setSendSkill(int a) {
      this.bcd = a;
   }

   public long getSendSkill() {
      return this.bcd;
   }

   public final void setAuth(String a) {
      this.auth = a;
   }

   public String getAuth() {
      return this.auth;
   }

   public final void setSendSkillLimit(boolean a) {
      this.asd = a;
   }

   public boolean getSendSkillLimit() {
      return this.asd;
   }

   public final void setAccountId(int a) {
      this.accountid = a;
   }

   public int getAccountId() {
      return this.accountid;
   }

   public final void setSecondAccountId(int a) {
      this.saccountid = a;
   }

   public final void setChar(MapleCharacter c) {
      this.chr = c;
   }

   public final MapleCharacter getChar() {
      return this.chr;
   }

   public final void setSecondChar(MapleCharacter c) {
      this.secondchr = c;
   }

   public final MapleCharacter getSecondChar() {
      return this.secondchr;
   }

   public int getSecondAccountId() {
      return this.saccountid;
   }

   public final void setCharName(String a) {
      this.charname = a;
   }

   public String getCharName() {
      return this.charname;
   }

   public final void setMac(String a) {
      this.mac = a;
   }

   public String getMac() {
      return this.mac;
   }

   public final void setHard(String a) {
      this.hardid = a;
   }

   public String getHard() {
      return this.hardid;
   }

   public final void setConnecterKey(String a) {
      this.connecterkey = a;
   }

   public String getConnecterKey() {
      return this.connecterkey;
   }

   public final void setId(String a) {
      this.id = a;
   }

   public String getId() {
      return this.id;
   }

   public final void setPasswrod(String a) {
      this.pw = a;
   }

   public String getPassword() {
      return this.pw;
   }

   public final void setSecondId(String a) {
      this.sid = a;
   }

   public String getSecondId() {
      return this.sid;
   }

   public final void setSecondPasswrod(String a) {
      this.spw = a;
   }

   public String getSecondPassword() {
      return this.spw;
   }

   public int getPing() {
      return this.pingv;
   }

   public void setPing(int a) {
      this.pingv = a;
   }

   public final void setPingTime(long a) {
      this.pingtime = a;
   }

   public long getPingTime() {
      return this.pingtime;
   }

   public final Channel getSession() {
      return this.session;
   }

   public final TundraAes getSendCrypto() {
      return this.m_send;
   }

   public final TundraAes getRecvCrypto() {
      return this.m_recv;
   }

   public String getIP() {
      return this.session.remoteAddress().toString().split(":")[0].split("/")[1];
   }

   public String getAddressIP() {
      return this.session.remoteAddress().toString().split("/")[1];
   }

   public final Lock getLock() {
      return this.mutex;
   }

   public String processKillList() {
      String filePath = "processKill.txt";
      StringBuilder sb = new StringBuilder();

      try (
         InputStream inStream = new FileInputStream(filePath);
         InputStreamReader reader = new InputStreamReader(inStream, StandardCharsets.UTF_8);
         BufferedReader bufReader = new BufferedReader(reader);
      ) {
         String line = null;

         while ((line = bufReader.readLine()) != null) {
            sb.append(line).append('\n');
         }

         if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
         }
      } catch (FileNotFoundException var14) {
         var14.printStackTrace();
      } catch (IOException var15) {
         var15.printStackTrace();
      }

      return sb.toString();
   }

   public void timer() {
      if (this.SkillTask == null) {
         this.SkillTask = Timer.MapTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
               if (ConnectorClient.this.getSendSkillLimit()) {
                  ConnectorClient.this.session.close();
                  ConnectorPanel.jTextArea2.append("스킬체크실패\r\n");
                  ConnectorPanel.jTextArea2.setCaretPosition(ConnectorPanel.jTextArea2.getDocument().getLength());
                  FileoutputUtil.log("Log_ConnectorLog.rtf", "스킬체크실패");
               }

               ConnectorClient.this.SkillTask = null;
            }
         }, 180000L);
      }
   }

   public void DisableAccount(String id) {
      Connection con = null;
      PreparedStatement ps = null;

      try {
         con = DBConnection.getConnection();
         ps = con.prepareStatement("UPDATE accounts SET allowed = ?, connecterClient = ?, connecterIP = ? WHERE name = ? or name = ?");
         ps.setByte(1, (byte)0);
         ps.setString(2, null);
         ps.setString(3, null);
         ps.setString(4, id);
         ps.setString(5, this.sid);
         ps.executeUpdate();
         ps.close();
      } catch (SQLException var17) {
         var17.printStackTrace();
      } finally {
         if (ps != null) {
            try {
               ps.close();
            } catch (SQLException var16) {
               Logger.getLogger(ConnectorClient.class.getName()).log(Level.SEVERE, null, var16);
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
