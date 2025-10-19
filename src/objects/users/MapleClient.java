package objects.users;

import constants.GameConstants;
import constants.ServerConstants;
import database.DBConfig;
import database.DBConnection;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.script.ScriptEngine;
import javax.swing.table.DefaultTableModel;
import logging.LoggingManager;
import logging.entry.ConnectLog;
import logging.entry.ConnectLogType;
import network.auction.AuctionServer;
import network.center.Center;
import network.connector.ConnectorClient;
import network.connector.ConnectorServer;
import network.connector.panel.ConnectorPanel;
import network.game.GameServer;
import network.login.LoginServer;
import network.models.CWvsContext;
import network.netty.ServerType;
import network.shop.CashShopServer;
import objects.context.friend.Friend;
import objects.context.guild.GuildCharacter;
import objects.context.messenger.MessengerCharacter;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.context.party.PartyOperation;
import objects.fields.Field;
import objects.fields.child.minigame.yutgame.Field_MultiYutGame;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.users.skills.SkillEncode;
import objects.utils.AdminClient;
import objects.utils.FileoutputUtil;
import objects.utils.GenerateCertCharacter;
import objects.utils.MD5Tool;
import objects.utils.MapleAESOFB;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Triple;
import scripting.newscripting.ScriptEngineNPC;

public class MapleClient implements Serializable {
   private static final long serialVersionUID = 9179541993413738569L;
   public static final byte LOGIN_NOTLOGGEDIN = 0;
   public static final byte LOGIN_SERVER_TRANSITION = 1;
   public static final byte LOGIN_LOGGEDIN = 2;
   public static final byte CHANGE_CHANNEL = 3;
   public static final String CLIENT_KEY = "CLIENT";
   public static final AttributeKey<MapleClient> CLIENTKEY = AttributeKey.valueOf("mapleclient_netty");
   private transient MapleAESOFB send;
   private transient MapleAESOFB receive;
   private ServerType serverType;
   public static final int DEFAULT_CHARSLOT = 45;
   private Channel session;
   private long serialNumber;
   private boolean auction;
   private MapleCharacter player;
   private int channel = 1;
   private int accId = -1;
   private int world;
   private int birthday;
   private int charslots = 45;
   private boolean loggedIn = false;
   private boolean serverTransition = false;
   private transient Calendar tempban = null;
   private String accountName;
   private boolean receivePing = false;
   private boolean monitored = false;
   private boolean receiving = true;
   private boolean gm;
   private byte greason = 1;
   private byte gender = -1;
   private byte nameChangeEnable = 0;
   public transient short loginAttempt = 0;
   private transient List<Integer> allowedChar = new LinkedList<>();
   private transient List<String> macs = new ArrayList<>();
   private transient Map<String, ScriptEngine> engines = new HashMap<>();
   private transient ScheduledFuture<?> idleTask = null;
   private transient String secondPassword;
   private transient String tempIP = "";
   private final transient Lock mutex = new ReentrantLock(true);
   private final transient Lock npc_mutex = new ReentrantLock();
   private long lastNpcClick = 0L;
   private long chatBlockedTime = 0L;
   private static final Lock login_mutex = new ReentrantLock(true);
   private final Map<Integer, Pair<Short, Short>> charInfo = new LinkedHashMap<>();
   private String cookie = null;
   private List<Integer> charPosition = new LinkedList<>();
   public ScheduledFuture<?> pingSchedule = null;
   private String lastUsedScriptName = "";
   private String lastUsedNewScriptName = "";
   private int crcValue = 0;
   private boolean firstTimeCRC = true;
   private int dwRand = 0;
   private ConnectorClient connectorClient;
   private Map<String, String> keyValues = new HashMap<>();
   private boolean debugPacketSend;
   private long debugPacketSendTime = 0L;
   private long debugPacketStartTime = 0L;
   private long accountChatBan = -1L;
   private long lastUpdateChat = 0L;
   private String phoneNumber = "";
   public boolean setLoggedin = false;

   public MapleClient(Channel session, MapleAESOFB send, MapleAESOFB receive, ServerType serverType) {
      this.send = send;
      this.receive = receive;
      this.session = session;
      this.serverType = serverType;
      this.serialNumber = Randomizer.nextLong();
   }

   public final MapleAESOFB getReceiveCrypto() {
      return this.receive;
   }

   public final MapleAESOFB getSendCrypto() {
      return this.send;
   }

   public final Lock getLock() {
      return this.mutex;
   }

   public final Lock getNPCLock() {
      return this.npc_mutex;
   }

   public MapleCharacter getPlayer() {
      return this.player;
   }

   public void setPlayer(MapleCharacter player) {
      this.player = player;
   }

   public void createdChar(int id) {
      this.allowedChar.add(id);
   }

   public final boolean login_Auth(int id) {
      return this.allowedChar.contains(id);
   }

   public Channel getSession() {
      return this.session;
   }

   public final List<MapleCharacter> loadCharacters(int accountID, int serverId) {
      List<MapleCharacter> chars = new LinkedList<>();

      for (MapleClient.CharNameAndId cni : this.loadCharactersInternal(accountID, serverId)) {
         MapleCharacter chr = MapleCharacter.loadCharFromDB(cni.id, this, false);
         chars.add(chr);
         this.charInfo.put(chr.getId(), new Pair<>(chr.getLevel(), chr.getJob()));
         this.allowedChar.add(chr.getId());
      }

      return chars;
   }

   public boolean canMakeCharacter(int serverId) {
      return this.loadCharactersSize(serverId) < this.getCharacterSlots();
   }

   public List<String> loadCharacterNames(int accountID, int serverId) {
      List<String> chars = new LinkedList<>();

      for (MapleClient.CharNameAndId cni : this.loadCharactersInternal(accountID, serverId)) {
         chars.add(cni.name);
      }

      return chars;
   }

   private List<MapleClient.CharNameAndId> loadCharactersInternal(int accountID, int serverId) {
      List<MapleClient.CharNameAndId> chars = new LinkedList<>();
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT id, name, gm FROM characters WHERE accountid = ? AND world = ?");
         ps.setInt(1, accountID);
         ps.setInt(2, serverId);
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            chars.add(new MapleClient.CharNameAndId(rs.getString("name"), rs.getInt("id")));
            LoginServer.getLoginAuth(rs.getInt("id"));
         }

         rs.close();
         ps.close();
      } catch (SQLException var10) {
         System.err.println("error loading characters internal");
         System.out.println("loadCharactersInternal 함수 실행중 오류발생" + var10.toString());
         var10.printStackTrace();
      }

      return chars;
   }

   private int loadCharactersSize(int serverId) {
      int chars = 0;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT count(*) FROM characters WHERE accountid = ? AND world = ?");
         ps.setInt(1, this.accId);
         ps.setInt(2, serverId);
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {
            chars = rs.getInt(1);
         }

         rs.close();
         ps.close();
      } catch (SQLException var9) {
         System.out.println("loadCharactersSize 함수 실행중 오류발생" + var9.toString());
         var9.printStackTrace();
      }

      return chars;
   }

   public boolean isLoggedIn() {
      return this.loggedIn && this.accId >= 0;
   }

   private Calendar getTempBanCalendar(ResultSet rs) throws SQLException {
      Calendar lTempban = Calendar.getInstance();
      Timestamp time = rs.getTimestamp("tempban");
      if (time == null) {
         lTempban.setTimeInMillis(0L);
         return lTempban;
      } else {
         Calendar today = Calendar.getInstance();
         lTempban.setTimeInMillis(rs.getTimestamp("tempban").getTime());
         if (today.getTimeInMillis() < lTempban.getTimeInMillis()) {
            return lTempban;
         } else {
            lTempban.setTimeInMillis(0L);
            return lTempban;
         }
      }
   }

   public Calendar getTempBanCalendar() {
      return this.tempban;
   }

   public byte getBanReason() {
      return this.greason;
   }

   public boolean hasBannedIP() {
      return false;
   }

   public boolean hasBannedMac(String mac) {
      boolean ret = false;
      mac.trim();
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM macbans WHERE mac = ?");
         ps.setString(1, mac);
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {
            ret = true;
         }

         rs.close();
         ps.close();
      } catch (SQLException var9) {
         System.err.println("Error checking mac bans" + var9);
      }

      return ret;
   }

   public boolean hasBannedSerial(String serial) {
      boolean ret = false;
      serial.trim();
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM serialban WHERE serialNumber = ?");
         ps.setString(1, serial);
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {
            ret = true;
         }

         rs.close();
         ps.close();
      } catch (SQLException var9) {
         System.err.println("Error checking mac bans" + var9);
      }

      return ret;
   }

   public void updateMacAndVolume(String mac, String volume, int accId) {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("UPDATE accounts SET mac = ?, volume = ? WHERE id = ?");
         ps.setString(1, mac);
         ps.setString(2, volume);
         ps.setInt(3, accId);
         ps.executeUpdate();
         ps.close();
      } catch (SQLException var10) {
         System.err.println(var10);
      }
   }

   public void loadMacsIfNescessary() {
      if (this.macs.isEmpty() && this.accId != -1) {
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT macs FROM accounts WHERE id = ?");
            ps.setInt(1, this.accId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
               rs.close();
               ps.close();
               throw new RuntimeException("No valid account associated with this client.");
            }

            if (rs.getString("macs") != null) {
               String[] macData = rs.getString("macs").split(", ");

               for (String mac : macData) {
                  if (!mac.equals("")) {
                     this.macs.add(mac);
                  }
               }
            }

            rs.close();
            ps.close();
         } catch (SQLException var11) {
            System.err.println(var11);
         }
      }
   }

   public void banMacs() {
      this.loadMacsIfNescessary();
      if (this.macs.size() > 0) {
         String[] macBans = new String[this.macs.size()];
         int z = 0;

         for (String mac : this.macs) {
            macBans[z] = mac;
            z++;
         }

         banMacs(macBans);
      }
   }

   public static final void banMacs(String[] macs) {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         List<String> filtered = new LinkedList<>();
         PreparedStatement ps = con.prepareStatement("SELECT filter FROM macfilters");
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            filtered.add(rs.getString("filter"));
         }

         rs.close();
         ps.close();
         boolean already = false;
         ps = con.prepareStatement("SELECT * FROM macbans WHERE `mac` = ?");
         ps.setString(1, macs[0]);
         rs = ps.executeQuery();
         if (rs.next()) {
            already = true;
         }

         rs.close();
         ps.close();
         if (!already) {
            ps = con.prepareStatement("INSERT INTO macbans (mac) VALUES (?)");
            ps.setString(1, macs[0]);
            ps.close();
         }
      } catch (SQLException var9) {
         System.err.println("Error banning MACs" + var9);
      }
   }

   public int finishLogin() {
      synchronized (MapleClient.class) {
         byte state = this.getLoginState();
         if (state > 0) {
            this.loggedIn = false;
            return 7;
         } else {
            this.updateLoginState(2, this.getSessionIPAddress());
            return 0;
         }
      }
   }

   public void clearInformation() {
      this.accountName = null;
      this.accId = -1;
      this.secondPassword = null;
      this.gm = false;
      this.loggedIn = false;
      this.greason = 1;
      this.tempban = null;
      this.gender = -1;
   }

   public int login(String login, String pwd) {
      return this.login(login, pwd, false);
   }

   public int login(String login, String pwd, boolean forced) {
      int loginok = 5;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?");
         ps.setString(1, login);
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {
            int banned = rs.getInt("banned");
            String passhash = rs.getString("password");
            String salt = rs.getString("salt");
            String oldSession = rs.getString("SessionIP");
            String connecterIP = rs.getString("connecterIP");
            int allowed = rs.getByte("allowed");
            this.accountName = login;
            this.accId = rs.getInt("id");
            this.secondPassword = rs.getString("2ndpassword");
            this.gm = rs.getInt("gm") > 0;
            this.greason = rs.getByte("greason");
            this.tempban = this.getTempBanCalendar(rs);
            this.gender = rs.getByte("gender");
            this.nameChangeEnable = rs.getByte("nameChange");
            if (ServerConstants.ConnectorSetting1 && !this.gm) {
               if (allowed != 1) {
                  this.session.write(CWvsContext.serverNotice(1, "전용 접속기로만 로그인할 수 있습니다.(오류코드 : 0x01)"));
                  rs.close();
                  ps.close();
                  return 21;
               }

               if (connecterIP == null) {
                  this.session.write(CWvsContext.serverNotice(1, "전용 접속기로만 로그인할 수 있습니다. (오류코드 : 0x03)"));
                  rs.close();
                  ps.close();
                  return 21;
               }
            }

            if (banned > 0) {
               loginok = 3;
            } else {
               if (banned == -1) {
                  this.unban();
               }

               byte loginstate = this.getLoginState();
               boolean updatePasswordHash = false;
               if (!forced) {
                  if (MD5Tool.generateMD5(pwd).equals(passhash) || !DBConfig.isHosting && pwd.equals("!xptmxm")) {
                     loginok = 0;
                     ConnectorClient cli = ConnectorServer.getInstance().getClientStorage().getLoginClient(login);
                     if (!this.gm && !this.isAllowedClient()) {
                        if (cli != null) {
                           this.setConnectorClient(cli);
                        } else if (ServerConstants.ConnectorSetting) {
                           loginok = 4;
                        }
                     }
                  } else {
                     this.loggedIn = false;
                     loginok = 4;
                  }
               }

               if (loginok == 0 && loginstate > 0) {
                  this.loggedIn = false;
                  loginok = 7;
               }

               if (updatePasswordHash && !forced) {
                  PreparedStatement pss = con.prepareStatement("UPDATE `accounts` SET `password` = ?, `salt` = ? WHERE id = ?");

                  try {
                     String newSalt = LoginCrypto.makeSalt();
                     pss.setString(1, LoginCrypto.makeSaltedSha512Hash(pwd, newSalt));
                     pss.setString(2, newSalt);
                     pss.setInt(3, this.accId);
                     pss.executeUpdate();
                  } finally {
                     pss.close();
                  }
               }
            }
         }

         rs.close();
         ps.close();
         return loginok;
      } catch (SQLException var27) {
         System.err.println("ERROR" + var27);
         return loginok;
      }
   }

   public boolean CheckSecondPassword(String in) {
      return in.equals(this.secondPassword);
   }

   public boolean CheckSecondPassword(int encType, byte[] encryptedPassword) {
      if (encType < 4) {
         for (int i = 0; i < encryptedPassword.length; i++) {
            byte b = encryptedPassword[i];
            byte b2 = (byte)(b << 4 & 240 | b >>> 4 & 15);
            encryptedPassword[i] = b2;
         }

         encType += 4;
      }

      if (this.secondPassword == null) {
         return false;
      } else {
         byte[] decrypted = this.secondPassword.getBytes(Charset.forName("MS949"));
         if (decrypted.length != encryptedPassword.length) {
            return false;
         } else {
            for (int i = 0; i < decrypted.length; i++) {
               switch (encType) {
                  case 4:
                     if (decrypted[i] != (byte)((encryptedPassword[i] & 255) << 1 | (encryptedPassword[i] & 255) >> 7)) {
                        return false;
                     }
                     break;
                  case 5:
                     if (encryptedPassword[i] != decrypted[i]) {
                        return false;
                     }
                     break;
                  case 6:
                     if (encryptedPassword[i] != (byte)(decrypted[i] * 2)) {
                        return false;
                     }
                     break;
                  case 7:
                     int aa = decrypted[i] * 4;
                     if (aa > 255) {
                        int var9;
                        aa += var9 = aa >>> 8;
                     }

                     if (encryptedPassword[i] != (byte)aa) {
                        return false;
                     }
               }
            }

            return true;
         }
      }
   }

   private void unban() {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = 0, banreason = '' WHERE id = ?");
         ps.setInt(1, this.accId);
         ps.executeUpdate();
         ps.close();
      } catch (SQLException var7) {
         System.err.println("Error while unbanning" + var7);
      }
   }

   public int getAccIdByCharId(int charId) {
      DBConnection db = new DBConnection();
      int accid = 0;

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT accountid from characters where id = ?");
         ps.setInt(1, charId);
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {
            accid = rs.getInt(1);
         }

         rs.close();
         ps.close();
      } catch (SQLException var9) {
      }

      return accid;
   }

   public static final byte unban(String charname) {
      DBConnection db = new DBConnection();
      int accid = 0;
      String name = "";
      String sessionIP = "";

      try {
         label58: {
            byte var8;
            try (Connection con = DBConnection.getConnection()) {
               PreparedStatement ps = con.prepareStatement("SELECT accountid from characters where name = ?");
               ps.setString(1, charname);
               ResultSet rs = ps.executeQuery();
               if (rs.next()) {
                  accid = rs.getInt(1);
                  rs.close();
                  ps.close();
                  ps = con.prepareStatement("UPDATE accounts SET tempban = DEFAULT, banned = 0, banreason = '' WHERE id = ?");
                  ps.setInt(1, accid);
                  ps.executeUpdate();
                  ps.close();
                  ps = con.prepareStatement("SELECT name, SessionIP FROM accounts WHERE id = ?");
                  ps.setInt(1, accid);
                  rs = ps.executeQuery();
                  if (rs.next()) {
                     name = rs.getString("name");
                     sessionIP = rs.getString("SessionIP");
                  }

                  rs.close();
                  ps.close();
                  ps = con.prepareStatement("DELETE FROM ipbans WHERE ip = ?");
                  ps.setString(1, sessionIP);
                  ps.executeQuery();
                  break label58;
               }

               rs.close();
               ps.close();
               var8 = -1;
            }

            return var8;
         }
      } catch (SQLException var11) {
         System.err.println("Error while unbanning" + var11);
         return -2;
      }

      MapleCharacter.unSerialBan(name, false);
      return 0;
   }

   public void updateMacs(String macData, String accountName) {
      this.macs.clear();

      for (String mac : macData.split(", ")) {
         this.macs.add(mac);
      }

      DBConnection db = new DBConnection();

      try {
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT id FROM accounts WHERE name = ?");
            ps.setString(1, accountName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
               int accountID = rs.getInt("id");
               rs.close();
               ps.close();
               ps = con.prepareStatement("UPDATE accounts SET macs = ?, mac = ?, volume = ? WHERE id = ?");
               ps.setString(1, macData);
               ps.setString(2, this.macs.get(0));
               ps.setString(3, this.macs.get(1));
               ps.setInt(4, accountID);
               ps.executeUpdate();
               ps.close();
               return;
            }

            rs.close();
            ps.close();
         }
      } catch (SQLException var10) {
         System.err.println("Error saving MACs" + var10);
      }
   }

   public void setAccID(int id) {
      this.accId = id;
   }

   public int getAccID() {
      return this.accId;
   }

   public final String generateCookie() {
      String cookie = "";
      GenerateCertCharacter generator = new GenerateCertCharacter();
      return generator.excuteGenerate();
   }

   public final void updateLoginState(int newstate, String SessionID) {
      this.updateLoginState(newstate, SessionID, null);
   }

   public final void updateLoginState(int newstate, String SessionID, String cookie) {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = ?, SessionIP = ?, lastlogin = CURRENT_TIMESTAMP() WHERE id = ?");
         ps.setInt(1, newstate);
         ps.setString(2, SessionID);
         ps.setInt(3, this.getAccID());
         ps.executeUpdate();
         ps.close();
      } catch (SQLException var10) {
         System.err.println("error updating login state" + var10);
      }

      if (newstate == 0) {
         this.loggedIn = false;
         this.serverTransition = false;
      } else {
         this.serverTransition = newstate == 1 || newstate == 3;
         this.loggedIn = !this.serverTransition;
      }
   }

   public final void updateSecondPassword() {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `2ndpassword` = ? WHERE id = ?");
         ps.setString(1, this.secondPassword);
         ps.setInt(2, this.accId);
         ps.executeUpdate();
         ps.close();
      } catch (SQLException var7) {
         System.err.println("error updating login state" + var7);
      }
   }

   public final int authAccountInfo(String cookie) {
      PreparedStatement ps = null;
      ResultSet rs = null;
      DBConnection db = new DBConnection();

      byte var20;
      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement(
            String.format("SELECT id, name, banned, 2ndpassword, gm, gender %s FROM accounts WHERE cookie = ?", DBConfig.isGanglim ? ", discordid" : "")
         );
         ps.setString(1, cookie);
         rs = ps.executeQuery();
         if (!rs.next() || rs.getInt("banned") > 0) {
            ps.close();
            rs.close();
            this.session.close();
            System.err.println("Account doesn't exist or is banned (cookie : " + cookie + ")");
            return 1;
         }

         this.setAccID(rs.getInt("id"));
         this.setAccountName(rs.getString("name"));
         this.setSecondPassword(rs.getString("2ndpassword"));
         this.setGender(rs.getByte("gender"));
         this.setCookie(cookie);
         this.gm = rs.getBoolean("gm");
         rs.close();
         ps.close();
         var20 = 0;
      } catch (SQLException var15) {
         this.loggedIn = false;
         System.out.println("authAccountInfo 함수 실행중 오류발생" + var15.toString());
         var15.printStackTrace();
         return 3;
      } finally {
         if (ps != null) {
            PreparedStatement var17 = null;
         }

         if (rs != null) {
            ResultSet var18 = null;
         }
      }

      return var20;
   }

   public final byte getLoginState() {
      DBConnection db = new DBConnection();

      try {
         byte var7;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT loggedin, lastlogin, banned, `birthday` + 0 AS `bday` FROM accounts WHERE id = ?");
            ps.setInt(1, this.getAccID());
            ResultSet rs = ps.executeQuery();
            boolean next = rs.next();
            if (!next) {
               ps.close();
               rs.close();
               this.session.close();
               throw new RuntimeException("[오류] 존재하지 않는 계정이 로그인 성공되었습니다.");
            }

            this.birthday = rs.getInt("bday");
            byte state = rs.getByte("loggedin");
            if ((state == 1 || state == 3) && rs.getTimestamp("lastlogin").getTime() + 20000L < System.currentTimeMillis()) {
               state = 0;
               this.updateLoginState(state, this.getSessionIPAddress());
            }

            rs.close();
            ps.close();
            if (state == 2) {
               this.loggedIn = true;
            } else {
               this.loggedIn = false;
            }

            var7 = state;
         }

         return var7;
      } catch (SQLException var10) {
         this.loggedIn = false;
         throw new RuntimeException("error getting login state", var10);
      }
   }

   public final boolean checkBirthDate(int date) {
      return this.birthday == date;
   }

   public final void removalTask(boolean shutdown) {
      try {
         if (this.player.getMap() instanceof Field_MultiYutGame) {
            Field_MultiYutGame f = (Field_MultiYutGame)this.player.getMap();
            f.getYutGameDlg().surrender(this.player, this.player.getMiniGameTeam());
            this.player.updateOneInfo(1234569, "miniGame1_can_time", String.valueOf(System.currentTimeMillis() + 900000L));
         }

         Center.GameWaitQueue.deleteQueue(this.player);
         this.player.cancelAllDebuffs();
         this.player.setSecondaryStat(null);
         if (this.player.getMarriageId() > 0) {
            MapleQuestStatus stat1 = this.player.getQuestNoAdd(MapleQuest.getInstance(160001));
            MapleQuestStatus stat2 = this.player.getQuestNoAdd(MapleQuest.getInstance(160002));
            if (stat1 != null && stat1.getCustomData() != null && (stat1.getCustomData().equals("2_") || stat1.getCustomData().equals("2"))) {
               if (stat2 != null && stat2.getCustomData() != null) {
                  stat2.setCustomData("0");
               }

               stat1.setCustomData("3");
            }
         }

         this.player.changeRemoval(true);
         if (this.player.getEventInstance() != null) {
            this.player.getEventInstance().playerDisconnected(this.player, this.player.getId());
         }

         this.player.setMessenger(null);
         if (this.player.getMap() != null) {
            if (shutdown || this.getChannelServer() != null && this.getChannelServer().isShutdown()) {
               int questID = -1;
               switch (this.player.getMapId()) {
                  case 105100300:
                  case 105100400:
                     questID = 160106;
                     break;
                  case 211070000:
                  case 211070100:
                  case 211070101:
                  case 211070110:
                     questID = 160107;
                     break;
                  case 240060200:
                     questID = 160100;
                     break;
                  case 240060201:
                     questID = 160103;
                     break;
                  case 270050100:
                     questID = 160101;
                     break;
                  case 271040100:
                     questID = 160109;
                     break;
                  case 280030000:
                     questID = 160101;
                     break;
                  case 280030001:
                     questID = 160102;
                     break;
                  case 551030200:
                     questID = 160108;
               }

               if (questID > 0) {
                  this.player.getQuestIfNullAdd(MapleQuest.getInstance(questID)).setCustomData("0");
               }
            } else if (this.player.isAlive()) {
               switch (this.player.getMapId()) {
                  case 220080001:
                  case 541010100:
                  case 541020800:
                     this.player.getMap().addDisconnected(this.player.getId());
               }
            }
         }
      } catch (Throwable var7) {
         System.out.println("[오류] disconnect 함수 실행 중 removalTask에서 오류 발생! (이름 : " + this.player.getName() + ") " + var7.toString());
         var7.printStackTrace();
         FileoutputUtil.outputFileError("Log_AccountStuck.rtf", var7);
      } finally {
         if (this.player != null && this.player.getMap() != null) {
            this.player.getMap().removePlayer(this.player);
         }
      }
   }

   public static final int getAccountIDByCharID(int charID) {
      DBConnection db = new DBConnection();

      try {
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT accountid FROM characters WHERE id = ?");
            ps.setInt(1, charID);
            ResultSet rs = ps.executeQuery();
            boolean next = rs.next();
            if (next) {
               int cid = rs.getInt("accountid");
               rs.close();
               ps.close();
               return cid;
            }

            rs.close();
            ps.close();
         }

         return -1;
      } catch (SQLException var10) {
         throw new RuntimeException("error getting accountIDByCharID", var10);
      }
   }

   public static final int getAccountIDByCharName(String name) {
      DBConnection db = new DBConnection();

      try {
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            boolean next = rs.next();
            if (next) {
               int cid = rs.getInt("accountid");
               rs.close();
               ps.close();
               return cid;
            }

            rs.close();
            ps.close();
         }

         return -1;
      } catch (SQLException var10) {
         throw new RuntimeException("error getting AccountIDByCharName", var10);
      }
   }

   public final void disconnect(boolean shutdown) {
      this.disconnect(shutdown, null);
   }

   public final void disconnect(boolean shutdown, Field m) {
      this.disconnect(shutdown, m, true);
   }

   public final void disconnect(boolean shutdown, Field m, boolean save) {
      Field map = null;
      Party party = null;
      Friend friend = null;
      PartyMemberEntry mpc = null;
      MessengerCharacter mmc = null;
      GuildCharacter mgc = null;
      int messengerID = -1;
      int guildID = -1;
      if (this.player != null && this.getServerType() != ServerType.LOGIN) {
         try {
            ScriptEngineNPC script = this.player.getScriptThread();
            if (script != null) {
               script.end();
            }
         } catch (Exception var71) {
            System.out.println("Disconnect Err");
            var71.printStackTrace();
         }
      }

      this.loadMacsIfNescessary();
      if (this.player != null) {
         map = m != null ? m : this.player.getMap();
         party = this.player.getParty();
         friend = this.player.getBuddylist();
         mpc = new PartyMemberEntry(this.player);
         mmc = new MessengerCharacter(this.player);
         mgc = this.player.getMGC();
         messengerID = this.player.getMessenger() == null ? 0 : this.player.getMessenger().getId();
         guildID = this.player.getGuildId();

         try {
            List<String> macString = this.getMacs();
            StringBuilder sb = new StringBuilder();
            sb.append("접속 해제 (아이피 : ");
            sb.append(this.player.getClient().getSessionIPAddress());
            sb.append(")");
            String getMac = "";
            String getVolume = "";
            if (macString.size() == 2) {
               getMac = macString.get(0);
               getVolume = macString.get(1);
            }

            LoggingManager.putLog(
               new ConnectLog(
                  this.player.getName(),
                  this.getAccountName(),
                  this.player.getId(),
                  this.getAccID(),
                  ConnectLogType.Disconnect.getType(),
                  getMac,
                  getVolume,
                  sb
               )
            );
         } catch (Exception var70) {
            System.out.println("Disconnect 2 Err");
            var70.printStackTrace();
         }
      } else {
         try {
            List<String> macString = this.getMacs();
            StringBuilder sb = new StringBuilder();
            sb.append("접속 해제 (아이피 : ");
            sb.append(this.getSessionIPAddress());
            sb.append(")");
            String getMac = "";
            String getVolume = "";
            if (macString.size() == 2) {
               getMac = macString.get(0);
               getVolume = macString.get(1);
            }

            LoggingManager.putLog(new ConnectLog("", this.getAccountName(), 0, this.getAccID(), ConnectLogType.Disconnect.getType(), getMac, getVolume, sb));
         } catch (Exception var69) {
            System.out.println("Disconnect 3 Err");
            var69.printStackTrace();
         }
      }

      if (this.getServerType() == ServerType.GAME || map != null) {
         if (this.player != null && !shutdown) {
            try {
               if (!this.player.isProcessChangeChannel()) {
                  Center.RemainBuffStorage.processSaveBuff(this.player);
               }
            } catch (Exception var68) {
               FileoutputUtil.log("Log_Disconnect_Except.rtf", "버프저장중 오류 발생");
               FileoutputUtil.outputFileError("Log_Disconnect_Except.rtf", var68);
            }

            try {
               if (!this.player.isProcessChangeChannel() && this.player.getAntiMacro() != null && this.player.getAntiMacro().getCaptcha() != null) {
                  this.player.onFailedAntiMacro(this.player.getAntiMacro(), AntiMacroFailedType.Disconnection);
               }
            } catch (Exception var67) {
               FileoutputUtil.log("Log_Disconnect_Except.rtf", "거짓말탐지기 저장중 오류발생");
               FileoutputUtil.outputFileError("Log_Disconnect_Except.rtf", var67);
            }

            try {
               if (map != null) {
                  this.removalTask(shutdown);
               }
            } catch (Exception var66) {
               System.out.println("[오류] disconnect 함수 실행 중 removalTask에서 오류 발생! (이름 : " + this.player.getName() + ") " + var66.toString());
               var66.printStackTrace();
            }

            if (map != null) {
               try {
                  map.removePlayer(this.player);
                  map.onDisconnected(this.player);
               } catch (Exception var65) {
                  FileoutputUtil.log("Log_Disconnect_Except.rtf", "removePlayer중 오류발생");
                  FileoutputUtil.outputFileError("Log_Disconnect_Except.rtf", var65);
               }
            }

            try {
               this.player.cancelAllTask(true, false);
            } catch (Exception var64) {
               System.out.println("[오류] disconnect 함수 실행 중 cancelAllTask에서 오류 발생! (이름 : " + this.player.getName() + ") " + var64.toString());
               var64.printStackTrace();
            }

            GameServer gs = GameServer.getInstance(map == null ? this.channel : map.getChannel());

            try {
               if (messengerID > 0) {
                  Center.Messenger.leaveMessenger(messengerID, mmc);
               }

               if (party != null) {
                  mpc.setOnline(false);
                  Center.Party.updateParty(party.getId(), PartyOperation.LogOnOff, mpc);
                  if (map != null && party.getLeader().getId() == this.player.getId()) {
                     PartyMemberEntry lchr = null;

                     for (PartyMemberEntry pchr : party.getPartyMemberList()) {
                        if (pchr != null && map.getCharacterById(pchr.getId()) != null && (lchr == null || lchr.getLevel() < pchr.getLevel())) {
                           lchr = pchr;
                        }
                     }

                     if (lchr != null) {
                        Center.Party.updateParty(party.getId(), PartyOperation.ChangeLeaderDisconnect, lchr);
                     }
                  }
               }

               if (friend != null) {
                  if (!this.serverTransition) {
                     Center.Buddy.loggedOff(this.player.getName(), this.player.getId(), this.getAccID(), this.channel, friend.getBuddyIds());
                  } else {
                     Center.Buddy.loggedOn(this.player.getName(), this.player.getId(), this.getAccID(), this.channel, friend.getBuddyIds());
                  }
               }

               if (guildID > 0 && mgc != null) {
                  Center.Guild.setGuildMemberOnline(mgc, false, -1);
               }
            } catch (Exception var76) {
               System.out.println("[오류] disconnect 함수 실행 중 오류 발생! (이름 : " + this.player.getName() + ") " + var76.toString());
               var76.printStackTrace();
            } finally {
               if (gs != null) {
                  gs.removePlayer(this.player);
               }
            }
         }

         try {
            if (this.engines != null) {
               this.engines.clear();
            }
         } catch (Exception var63) {
            FileoutputUtil.log("Log_Disconnect_Except.rtf", "스크립트 엔진 초기화중 오류 발생");
            FileoutputUtil.outputFileError("Log_Disconnect_Except.rtf", var63);
         }

         if (ServerConstants.useAdminClient) {
            try {
               AdminClient.updatePlayerList();
            } catch (Exception var62) {
               FileoutputUtil.log("Log_Disconnect_Except.rtf", "관리기 동접자 출력중 오류 발생");
               FileoutputUtil.outputFileError("Log_Disconnect_Except.rtf", var62);
            }
         }
      }

      if (this.getServerType() == ServerType.AUCTION || this.getServerType() == ServerType.SHOP) {
         if (this.player != null) {
            if (this.getServerType() == ServerType.AUCTION) {
               AuctionServer.getPlayerStorage().deregisterPlayer(this.player);
            } else if (this.getServerType() == ServerType.SHOP) {
               CashShopServer.getPlayerStorage().deregisterPlayer(this.player);
            }

            if (party != null) {
               mpc.setOnline(false);
               Center.Party.updateParty(party.getId(), PartyOperation.LogOnOff, mpc);
            }

            if (friend != null) {
               if (!this.serverTransition) {
                  Center.Buddy.loggedOff(this.player.getName(), this.player.getId(), this.getAccID(), this.channel, friend.getBuddyIds());
               } else {
                  Center.Buddy.loggedOn(this.player.getName(), this.player.getId(), this.getAccID(), this.channel, friend.getBuddyIds());
               }
            }

            if (guildID > 0 && mgc != null) {
               Center.Guild.setGuildMemberOnline(mgc, false, -1);
            }

            this.player.setMessenger(null);
         }

         if (ServerConstants.useAdminClient) {
            AdminClient.updatePlayerList();
         }
      }

      if (this.player != null && this.getServerType() != ServerType.LOGIN) {
         if (this.player.getTrade() != null) {
            try {
               MapleTrade.cancelTrade(this.player.getTrade(), this, this.player);
            } catch (Exception var61) {
               System.err.println("교환 취소중 오류 발생");
               var61.printStackTrace();
            }
         }

         if (save) {
            this.player.saveToDB(true, this.getServerType() == ServerType.SHOP || this.getServerType() == ServerType.AUCTION);
         }
      }

      if (ServerConstants.ConnectorSetting) {
         PreparedStatement ps = null;

         try (Connection con = DBConnection.getConnection()) {
            ps = con.prepareStatement("UPDATE `accounts` SET `allowed` = 0 WHERE `id` = ?");
            ps.setInt(1, this.getAccID());
            ps.executeUpdate();
         } catch (SQLException var74) {
         } finally {
            try {
               if (ps != null) {
                  ps.close();
                  PreparedStatement var82 = null;
               }
            } catch (SQLException var59) {
            }
         }

         try {
            DefaultTableModel model = (DefaultTableModel)ConnectorPanel.jTable1.getModel();
            if (model.getRowCount() >= 1) {
               for (int i = model.getRowCount() - 1; i >= 0; i--) {
                  if (model != null && this.getConnectorClient() != null) {
                     String id = model.getValueAt(i, 0) != null ? model.getValueAt(i, 0).toString() : null;
                     if (id != null && id.equals(this.getConnectorClient().getId())) {
                        String name = model.getValueAt(i, 3) != null ? model.getValueAt(i, 3).toString() : null;
                        if (name != null) {
                           if (this.player != null) {
                              model.setValueAt(name.replace(this.player.getName() + ",", ""), i, 3);
                              ConnectorClient c = ConnectorServer.getInstance().getClientStorage().getClientByName(this.getAccountName());
                              if (c != null) {
                                 c.removeInGameChar(this.player.getName());
                              }
                           }
                           break;
                        }
                     }
                  }
               }
            }
         } catch (Exception var72) {
         }
      }

      if (!this.serverTransition && this.isLoggedIn()) {
         this.updateLoginState(0, this.getSessionIPAddress());
      }

      this.player = null;
   }

   public final String getSessionIPAddress() {
      return this.session.remoteAddress() != null ? this.session.remoteAddress().toString().split(":")[0] : "";
   }

   public final boolean CheckIPAddress() {
      return true;
   }

   public final int getChannel() {
      return this.channel;
   }

   public final GameServer getChannelServer() {
      return GameServer.getInstance(this.channel);
   }

   public final int deleteCharacter(int cid) {
      DBConnection db = new DBConnection();

      try {
         byte var6;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT guildid, guildrank, familyid, name FROM characters WHERE id = ? AND accountid = ?");
            ps.setInt(1, cid);
            ps.setInt(2, this.accId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
               rs.close();
               ps.close();
               return 9;
            }

            if (rs.getInt("guildid") > 0) {
               if (rs.getInt("guildrank") == 1) {
                  rs.close();
                  ps.close();
                  return 18;
               }

               Center.Guild.deleteGuildCharacter(rs.getInt("guildid"), cid);
            }

            rs.close();
            ps.close();
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM characters WHERE id = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM linkskill WHERE linking_cid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mountdata WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM inventoryitems WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM famelog WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM famelog WHERE characterid_to = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM dueypackages WHERE RecieverId = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM wishlist WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM keymap WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM trocklocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM regrocklocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM hyperrocklocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM savedlocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM skills WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mountdata WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM skillmacros WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM trocklocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM queststatus WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM inventoryslot WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM extendedSlots WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM vcores WHERE player_id = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM skill_alarm WHERE character_id = ?", cid);
            var6 = 0;
         }

         return var6;
      } catch (Exception var9) {
         FileoutputUtil.outputFileError("Log_Packet_Except.rtf", var9);
         System.out.println("Char Delete Err");
         var9.printStackTrace();
         return 10;
      }
   }

   public final byte getGender() {
      return this.gender;
   }

   public final void setGender(byte gender) {
      this.gender = gender;
   }

   public final String getSecondPassword() {
      return this.secondPassword;
   }

   public final void setSecondPassword(String secondPassword) {
      this.secondPassword = secondPassword;
   }

   public final String getAccountName() {
      return this.accountName;
   }

   public final void setAccountName(String accountName) {
      this.accountName = accountName;
   }

   public final void setChannel(int channel) {
      this.channel = channel;
   }

   public final int getWorld() {
      return this.world;
   }

   public final void setWorld(int world) {
      this.world = world;
   }

   public final void cancelPingSchedule() {
      if (this.pingSchedule != null) {
         this.pingSchedule.cancel(true);
         this.pingSchedule = null;
      }
   }

   public final void sendPing() {
   }

   public static final String getLogMessage(MapleClient cfor, String message) {
      return getLogMessage(cfor, message);
   }

   public static final String getLogMessage(MapleCharacter cfor, String message) {
      return getLogMessage(cfor == null ? null : cfor.getClient(), message);
   }

   public static final String getLogMessage(MapleCharacter cfor, String message, Object... parms) {
      return getLogMessage(cfor == null ? null : cfor.getClient(), message, parms);
   }

   public static final String getLogMessage(MapleClient cfor, String message, Object... parms) {
      StringBuilder builder = new StringBuilder();
      if (cfor != null) {
         if (cfor.getPlayer() != null) {
            builder.append("<");
            builder.append(MapleCharacterUtil.makeMapleReadable(cfor.getPlayer().getName()));
            builder.append(" (cid: ");
            builder.append(cfor.getPlayer().getId());
            builder.append(")> ");
         }

         if (cfor.getAccountName() != null) {
            builder.append("(Account: ");
            builder.append(cfor.getAccountName());
            builder.append(") ");
         }
      }

      builder.append(message);

      for (Object parm : parms) {
         int start = builder.indexOf("{}");
         builder.replace(start, start + 2, parm.toString());
      }

      return builder.toString();
   }

   public static final int findAccIdForCharacterName(String charName) {
      DBConnection db = new DBConnection();

      try {
         int var6;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            ps.setString(1, charName);
            ResultSet rs = ps.executeQuery();
            int ret = -1;
            if (rs.next()) {
               ret = rs.getInt("accountid");
            }

            rs.close();
            ps.close();
            var6 = ret;
         }

         return var6;
      } catch (SQLException var9) {
         System.err.println("findAccIdForCharacterName SQL error");
         return -1;
      }
   }

   public final List<String> getMacs() {
      return this.macs;
   }

   public final boolean isGm() {
      return this.gm;
   }

   public final void setGm(boolean gm) {
      this.gm = gm;
   }

   public final void setScriptEngine(String name, ScriptEngine e) {
      this.engines.put(name, e);
   }

   public final ScriptEngine getScriptEngine(String name) {
      return this.engines.get(name);
   }

   public final void removeScriptEngine() {
      this.engines.clear();
   }

   public final ScheduledFuture<?> getIdleTask() {
      return this.idleTask;
   }

   public final void setIdleTask(ScheduledFuture<?> idleTask) {
      this.idleTask = idleTask;
   }

   public void updateLinkSkills(int playerID) {
      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT `accountid` FROM characters WHERE `id` = ?");
         ps.setInt(1, playerID);
         ResultSet rs = ps.executeQuery();
         int accountID = -1;

         while (rs.next()) {
            accountID = rs.getInt("accountid");
         }

         rs.close();
         ps.close();
         List<MapleClient.CharNameAndId> dataList = this.loadCharactersInternal(accountID, this.world);
         List<Triple<Integer, Integer, Integer>> jobAndLevelAndID = new ArrayList<>();

         for (MapleClient.CharNameAndId data : dataList) {
            ps = con.prepareStatement("SELECT `job`, `level` FROM characters WHERE `id` = ?");
            ps.setInt(1, data.id);
            rs = ps.executeQuery();
            if (rs.next()) {
               int job = rs.getInt("job");
               int level = rs.getInt("level");
               if (level >= 70) {
                  ps = con.prepareStatement("SELECT * FROM linkskill WHERE `linking_cid` = ?");
                  ps.setInt(1, data.id);
                  rs = ps.executeQuery();
                  if (!rs.next()) {
                     jobAndLevelAndID.add(new Triple<>(job, level, data.id));
                  } else {
                     int skillLv = rs.getInt("skilllevel");
                     if (skillLv != GameConstants.getLinkLevel(level, GameConstants.isZero(job))) {
                        jobAndLevelAndID.add(new Triple<>(job, level, data.id));
                     }
                  }
               }
            }

            rs.close();
            ps.close();
         }

         for (Triple<Integer, Integer, Integer> JLI : jobAndLevelAndID) {
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM linkskill WHERE linking_cid = ?", JLI.right);
            ps = con.prepareStatement("INSERT INTO linkskill VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?)");
            int skillID = SkillEncode.getLinkSkill(JLI.left);
            if (skillID > 0) {
               ps.setInt(1, GameConstants.getDuplicateOfOriginalSkill(skillID));
               ps.setInt(2, skillID);
               ps.setInt(3, JLI.right);
               ps.setInt(4, JLI.right);
               ps.setInt(5, GameConstants.getLinkLevel(JLI.mid, GameConstants.isZero(JLI.left)));
               ps.setLong(6, System.currentTimeMillis());
               ps.setInt(7, accountID);
               ps.executeUpdate();
               ps.close();
            }
         }
      } catch (SQLException var15) {
         var15.printStackTrace();
      }
   }

   public int getCharacterSlots() {
      return 49;
   }

   public boolean gainCharacterSlot() {
      if (this.getCharacterSlots() >= 47) {
         return false;
      } else {
         this.charslots++;
         DBConnection db = new DBConnection();

         try {
            try (Connection con = DBConnection.getConnection()) {
               PreparedStatement ps = con.prepareStatement("UPDATE character_slots SET charslots = ? WHERE worldid = ? AND accid = ?");
               ps.setInt(1, this.charslots);
               ps.setInt(2, 49);
               ps.setInt(3, this.accId);
               ps.executeUpdate();
               ps.close();
            }

            return true;
         } catch (SQLException var7) {
            var7.printStackTrace();
            return false;
         }
      }
   }

   public static final byte unbanIPMacs_(Connection con, String sessionIP, String macs) {
      byte ret = 0;

      try {
         if (sessionIP != null) {
            PreparedStatement psa = con.prepareStatement("DELETE FROM ipbans WHERE ip like ?");
            psa.setString(1, sessionIP);
            psa.execute();
            psa.close();
            ret++;
         }

         if (macs != null) {
            String[] macz = macs.split(", ");
            String mac = macz[0];
            if (!mac.isEmpty()) {
               PreparedStatement psa = con.prepareStatement("DELETE FROM macbans WHERE mac = ?");
               psa.setString(1, mac);
               psa.execute();
               psa.close();
            }

            ret++;
         }
      } catch (SQLException var7) {
      }

      return ret;
   }

   public static final byte unbanIPMacs(int accid, String sessionIP, String macs) {
      DBConnection db = new DBConnection();

      try {
         byte var10;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, accid);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
               rs.close();
               ps.close();
               return -1;
            }

            String sessionIP_ = rs.getString("sessionIP");
            String macs_ = rs.getString("macs");
            rs.close();
            ps.close();
            byte ret = 0;
            ret = unbanIPMacs_(con, sessionIP, macs);
            var10 = ret;
         }

         return var10;
      } catch (SQLException var13) {
         System.err.println("Error while unbanning" + var13);
         return -2;
      }
   }

   public static final byte unHellban(String charname) {
      DBConnection db = new DBConnection();

      try {
         byte var8;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT accountid from characters where name = ?");
            ps.setString(1, charname);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
               rs.close();
               ps.close();
               return -1;
            }

            int accid = rs.getInt(1);
            rs.close();
            ps.close();
            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, accid);
            rs = ps.executeQuery();
            if (!rs.next()) {
               rs.close();
               ps.close();
               return -1;
            }

            String sessionIP = rs.getString("sessionIP");
            String email = rs.getString("email");
            rs.close();
            ps.close();
            ps = con.prepareStatement("UPDATE accounts SET banned = 0, banreason = '' WHERE email = ?" + (sessionIP == null ? "" : " OR sessionIP = ?"));
            ps.setString(1, email);
            if (sessionIP != null) {
               ps.setString(2, sessionIP);
            }

            ps.execute();
            ps.close();
            var8 = 0;
         }

         return var8;
      } catch (SQLException var11) {
         System.err.println("Error while unbanning" + var11);
         return -2;
      }
   }

   public boolean isMonitored() {
      return this.monitored;
   }

   public void setMonitored(boolean m) {
      this.monitored = m;
   }

   public boolean isReceiving() {
      return this.receiving;
   }

   public void setReceiving(boolean m) {
      this.receiving = m;
   }

   public boolean canClickNPC() {
      return this.lastNpcClick + 500L < System.currentTimeMillis();
   }

   public boolean canRunScript() {
      return this.lastNpcClick + 200L < System.currentTimeMillis();
   }

   public void setClickedNPC() {
      this.lastNpcClick = System.currentTimeMillis();
   }

   public void removeClickedNPC() {
      this.lastNpcClick = 0L;
   }

   public final Timestamp getCreated() {
      DBConnection db = new DBConnection();

      try {
         Timestamp var6;
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT createdat FROM accounts WHERE id = ?");
            ps.setInt(1, this.getAccID());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
               rs.close();
               ps.close();
               return null;
            }

            Timestamp ret = rs.getTimestamp("createdat");
            rs.close();
            ps.close();
            var6 = ret;
         }

         return var6;
      } catch (SQLException var9) {
         throw new RuntimeException("error getting create", var9);
      }
   }

   public String getTempIP() {
      return this.tempIP;
   }

   public void setTempIP(String s) {
      this.tempIP = s;
   }

   public final byte getNameChangeEnable() {
      return this.nameChangeEnable;
   }

   public final void setNameChangeEnable(byte nickName) {
      this.nameChangeEnable = nickName;
   }

   public boolean isLocalhost() {
      return ServerConstants.Use_Localhost;
   }

   public String getCookie() {
      return this.cookie;
   }

   public void setCookie(String cookie) {
      this.cookie = cookie;
   }

   public List<Integer> getAndSetCharPosition() {
      if (this.charPosition == null || this.charPosition.isEmpty()) {
         PreparedStatement ps = null;
         ResultSet rs = null;
         DBConnection db = new DBConnection();

         Object var23;
         try (Connection con = DBConnection.getConnection()) {
            ps = con.prepareStatement("SELECT position FROM character_select_list WHERE account_id = ?");
            ps.setInt(1, this.getAccID());
            rs = ps.executeQuery();
            if (rs.next()) {
               String position = rs.getString("position");
               List<Integer> charPosition = new LinkedList<>();
               String[] pos = position.split(",");

               for (String po : pos) {
                  if (!po.isEmpty()) {
                     charPosition.add(Integer.parseInt(po));
                  }
               }

               rs.close();
               ps.close();
               this.charPosition = charPosition;
               return charPosition;
            }

            var23 = null;
         } catch (SQLException var19) {
            var19.printStackTrace();
            return this.charPosition;
         } finally {
            if (rs != null) {
               ResultSet var22 = null;
            }

            if (ps != null) {
               PreparedStatement var21 = null;
            }
         }

         return (List<Integer>)var23;
      } else {
         return this.charPosition;
      }
   }

   public List<Integer> getCharPosition() {
      return this.charPosition;
   }

   public void addCharPosition(int playerID) {
      if (this.charPosition != null) {
         this.charPosition.add(playerID);
      }
   }

   public void setCharPosition(List<Integer> charPosition) {
      this.charPosition = charPosition;
   }

   public void setReceivePing(boolean receivePing) {
      this.receivePing = receivePing;
   }

   public boolean isAuction() {
      return this.auction;
   }

   public void setAuction(boolean auction) {
      this.auction = auction;
   }

   public void setSession(Channel session) {
      this.session = session;
   }

   public ServerType getServerType() {
      return this.serverType;
   }

   public void setServerType(ServerType serverType) {
      this.serverType = serverType;
   }

   public long getSerialNumber() {
      return this.serialNumber;
   }

   public void setSerialNumber(long serialNumber) {
      this.serialNumber = serialNumber;
   }

   public String getLastUsedScriptName() {
      return this.lastUsedScriptName;
   }

   public void setLastUsedScriptName(String lastUsedScriptName) {
      this.lastUsedScriptName = lastUsedScriptName;
   }

   public String getLastUsedNewScriptName() {
      return this.lastUsedNewScriptName;
   }

   public void setLastUsedNewScriptName(String lastUsedNewScriptName) {
      this.lastUsedNewScriptName = lastUsedNewScriptName;
   }

   public int getCrcValue() {
      return this.crcValue;
   }

   public void setCrcValue(int crcValue) {
      this.crcValue = crcValue;
   }

   public boolean isFirstTimeCRC() {
      return this.firstTimeCRC;
   }

   public void setFirstTimeCRC(boolean firstTimeCRC) {
      this.firstTimeCRC = firstTimeCRC;
   }

   public int getDWRand() {
      return this.dwRand;
   }

   public void setDwRand(int dwRand) {
      this.dwRand = dwRand;
   }

   public final boolean isAllowedClient() {
      if (this.accountName == null) {
         return false;
      } else {
         String var1 = this.accountName;
         byte var2 = -1;
         switch (var1.hashCode()) {
            case 92668751:
               if (var1.equals("admin")) {
                  var2 = 0;
               }
            default:
               switch (var2) {
                  case 0:
                     return true;
                  default:
                     return false;
               }
         }
      }
   }

   public void setConnectorClient(ConnectorClient c) {
      this.connectorClient = c;
   }

   public ConnectorClient getConnectorClient() {
      return this.connectorClient;
   }

   public void loadKeyValues() {
      PreparedStatement ps = null;
      ResultSet rs = null;
      this.keyValues.clear();

      try (Connection con = DBConnection.getConnection()) {
         ps = con.prepareStatement("SELECT * FROM acckeyvalue WHERE id = ?");
         ps.setInt(1, this.accId);
         rs = ps.executeQuery();

         while (rs.next()) {
            this.keyValues.put(rs.getString("key"), rs.getString("value"));
         }

         ps.close();
         rs.close();
      } catch (Exception var22) {
         System.out.println("LoadKeyValue Err");
         var22.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
         } catch (SQLException var19) {
            var19.printStackTrace();
         }

         try {
            if (rs != null) {
               rs.close();
            }
         } catch (SQLException var18) {
            var18.printStackTrace();
         }
      }
   }

   public String getKeyValue(String key) {
      return this.keyValues.containsKey(key) ? this.keyValues.get(key) : null;
   }

   public void saveKeyValueToDB(Connection con) {
      try {
         if (this.keyValues != null) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM acckeyvalue WHERE `id` = ?");
            ps.setInt(1, this.accId);
            ps.executeUpdate();
            ps.close();
            PreparedStatement pse = con.prepareStatement("INSERT INTO acckeyvalue (`id`, `key`, `value`) VALUES (?, ?, ?)");
            pse.setInt(1, this.accId);

            for (Entry<String, String> keyValue : this.keyValues.entrySet()) {
               pse.setString(2, keyValue.getKey());
               pse.setString(3, keyValue.getValue());
               pse.execute();
            }

            pse.close();
         }
      } catch (Exception var6) {
         System.out.println("SaveKeyValue Err");
         var6.printStackTrace();
      }
   }

   public Map<String, String> getKeyValues() {
      return this.keyValues;
   }

   public void setKeyValues(Map<String, String> values) {
      this.keyValues = values;
   }

   public void setKeyValue(String key, String value) {
      this.keyValues.put(key, value);
   }

   public void removeKeyValue(String key) {
      this.keyValues.remove(key);
   }

   public boolean getDebugPacketSend() {
      return this.debugPacketSend;
   }

   public void setDebugPacketSend(boolean send) {
      this.debugPacketSend = send;
   }

   public long getDebugPacketSendTime() {
      return this.debugPacketSendTime;
   }

   public long getDebugPacketStartTime() {
      return this.debugPacketStartTime;
   }

   public void setDebugPacketSendTime(long time) {
      this.debugPacketSendTime = time;
      this.debugPacketStartTime = System.currentTimeMillis();
   }

   public boolean isAccountChatBan() {
      if (System.currentTimeMillis() - this.lastUpdateChat > 60000L) {
         this.accountChatBan = -1L;
         this.lastUpdateChat = System.currentTimeMillis();
      }

      if (this.accountChatBan == -1L) {
         try {
            label176: {
               boolean var5;
               try (
                  Connection con = DBConnection.getConnection();
                  PreparedStatement ps = con.prepareStatement("SELECT `chat_ban_time` FROM `accounts` WHERE `id` = ?");
               ) {
                  ps.setInt(1, this.getAccID());

                  try (ResultSet rs = ps.executeQuery()) {
                     if (!rs.next()) {
                        this.accountChatBan = 0L;
                        return false;
                     }

                     Timestamp ts = rs.getTimestamp("chat_ban_time");
                     if (ts != null) {
                        this.accountChatBan = ts.getTime();
                        break label176;
                     }

                     this.accountChatBan = 0L;
                     var5 = false;
                  }
               }

               return var5;
            }
         } catch (Exception var12) {
            System.out.println("ChatbanCheck Err");
            var12.printStackTrace();
            return false;
         }
      }

      if (this.accountChatBan >= System.currentTimeMillis()) {
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
         String info = sdf.format(this.accountChatBan) + " 까지 채팅 이용이 정지된 계정입니다.";
         if (this.getPlayer() != null) {
            this.getPlayer().dropMessage(6, info);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean setAccountChatBanTime(long bantime) {
      try (
         Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `chat_ban_time` = ? WHERE `id` = ?");
      ) {
         Timestamp ts = new Timestamp(bantime);
         ps.setTimestamp(1, ts);
         ps.setInt(2, this.getAccID());
         ps.execute();
      } catch (Exception var11) {
         return false;
      }

      this.accountChatBan = bantime;
      return true;
   }

   public String getPhoneNumber() {
      if (!DBConfig.isGanglim) {
         return "";
      } else if (!this.phoneNumber.equals("")) {
         return this.phoneNumber;
      } else {
         try {
            String var5;
            try (
               Connection con = DBConnection.getConnection();
               PreparedStatement ps = con.prepareStatement("SELECT `phonenumber` FROM `accounts` WHERE `id` = ?");
            ) {
               ps.setInt(1, this.getAccID());

               try (ResultSet rs = ps.executeQuery()) {
                  if (!rs.next()) {
                     return "";
                  }

                  String phone = rs.getString("phonenumber");
                  if (phone != null && !phone.isEmpty()) {
                     this.phoneNumber = phone;
                     return this.phoneNumber;
                  }

                  var5 = "";
               }
            }

            return var5;
         } catch (Exception var12) {
            System.out.println("getPhoneNumber Err");
            var12.printStackTrace();
            return "";
         }
      }
   }

   public boolean isOverseasUser() {
      return this.getPhoneNumber().replace("-", "").equals("01097777777");
   }

   protected static final class CharNameAndId {
      public final String name;
      public final int id;

      public CharNameAndId(String name, int id) {
         this.name = name;
         this.id = id;
      }
   }
}
