package objects.context.guild;

import constants.GameConstants;
import database.DBConfig;
import database.DBConnection;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.sql.rowset.serial.SerialBlob;
import network.center.Center;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import network.models.GuildContents;
import network.models.PacketHelper;
import objects.context.ReportLogEntry;
import objects.context.guild.alliance.Alliance;
import objects.item.Item;
import objects.users.MapleCharacter;
import objects.users.MapleCharacterUtil;
import objects.users.MapleClient;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.utils.Timer;

public class Guild implements Serializable {
   public static final long serialVersionUID = 6322150443228168192L;
   private final List<GuildCharacter> members = new CopyOnWriteArrayList<>();
   private final List<GuildCharacter> requests = new ArrayList<>();
   private final Map<Integer, GuildSkill> guildSkills = new HashMap<>();
   private List<GuildContentsLog> contentsLogs = new ArrayList<>();
   private final String[] rankTitles = new String[]{"", "", "", "", "", "", "", "", "", ""};
   private final int[] rankPermission = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   private String name;
   private String notice;
   private int id;
   private int gp;
   private int logo;
   private int logoColor;
   private int leader;
   private int capacity;
   private int logoBG;
   private int logoBGColor;
   private int signature;
   private int level;
   private int connectTimeFlag;
   private int activityFlag;
   private int ageGroupFlag;
   private int honorEXP;
   private int noblessSkillPoint;
   private byte[] customEmblem;
   private boolean allowJoinRequest;
   private boolean bDirty = true;
   private boolean proper = true;
   private int allianceid = 0;
   private int invitedid = 0;
   private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
   private boolean init = false;
   private boolean changed_skills = false;
   private boolean change_guildLog = false;
   private final Map<Integer, Guild.JoinRequester> requesters = new ConcurrentHashMap<>();
   public ScheduledFuture<?> nobleA = null;

   public Guild(int guildid) {
      DBConnection db = new DBConnection();

      try {
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM guilds WHERE guildid = ?");
            ps.setInt(1, guildid);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
               this.id = guildid;
               this.name = rs.getString("name");
               this.gp = rs.getInt("GP");
               this.logo = rs.getInt("logo");
               this.logoColor = rs.getInt("logoColor");
               this.logoBG = rs.getInt("logoBG");
               this.logoBGColor = rs.getInt("logoBGColor");
               this.customEmblem = rs.getBytes("customEmblem");
               this.capacity = rs.getInt("capacity");
               this.rankTitles[0] = rs.getString("rank1title");
               this.rankTitles[1] = rs.getString("rank2title");
               this.rankTitles[2] = rs.getString("rank3title");
               this.rankTitles[3] = rs.getString("rank4title");
               this.rankTitles[4] = rs.getString("rank5title");
               this.rankTitles[5] = rs.getString("rank6title");
               this.rankTitles[6] = rs.getString("rank7title");
               this.rankTitles[7] = rs.getString("rank8title");
               this.rankTitles[8] = rs.getString("rank9title");
               this.rankTitles[9] = rs.getString("rank10title");
               this.rankPermission[0] = rs.getInt("rank1permission");
               this.rankPermission[1] = rs.getInt("rank2permission");
               this.rankPermission[2] = rs.getInt("rank3permission");
               this.rankPermission[3] = rs.getInt("rank4permission");
               this.rankPermission[4] = rs.getInt("rank5permission");
               this.rankPermission[5] = rs.getInt("rank6permission");
               this.rankPermission[6] = rs.getInt("rank7permission");
               this.rankPermission[7] = rs.getInt("rank8permission");
               this.rankPermission[8] = rs.getInt("rank9permission");
               this.rankPermission[9] = rs.getInt("rank10permission");
               this.leader = rs.getInt("leader");
               this.notice = rs.getString("notice");
               this.signature = rs.getInt("signature");
               this.allianceid = rs.getInt("alliance");
               this.allowJoinRequest = rs.getInt("allowJoinRequest") != 0;
               this.connectTimeFlag = rs.getInt("connectTimeFlag");
               this.activityFlag = rs.getInt("activityFlag");
               this.ageGroupFlag = rs.getInt("ageGroupFlag");
               this.honorEXP = rs.getInt("honorEXP");
               this.noblessSkillPoint = rs.getInt("noblessSkillPoint");
               Blob custom = rs.getBlob("customEmblem");
               if (custom != null) {
                  this.customEmblem = custom.getBytes(1L, (int)custom.length());
               }

               rs.close();
               ps.close();
               Alliance alliance = Center.Alliance.getAlliance(this.allianceid);
               if (alliance == null) {
                  this.allianceid = 0;
               }

               ps = con.prepareStatement(
                  "SELECT id, name, level, job, guildrank, guildContribution, alliancerank, createdate, todayContribution, last_loggedin_date, today_loggedin_date FROM characters WHERE guildid = ? ORDER BY guildrank ASC, name ASC",
                  1007
               );
               ps.setInt(1, guildid);
               rs = ps.executeQuery();
               if (!rs.first()) {
                  System.err.println(this.id + "번 길드에 길드원이 아무도 없어 길드가 자동으로 해체됩니다.");
                  rs.close();
                  ps.close();
                  this.writeToDB(true);
                  this.proper = false;
                  return;
               }

               boolean leaderCheck = false;
               byte gFix = 0;
               byte aFix = 0;

               do {
                  int cid = rs.getInt("id");
                  byte gRank = rs.getByte("guildrank");
                  byte aRank = rs.getByte("alliancerank");
                  if (cid == this.leader) {
                     leaderCheck = true;
                     if (gRank != 1) {
                        gRank = 1;
                        gFix = 1;
                     }

                     if (alliance != null) {
                        if (alliance.getLeaderId() == cid && aRank != 1) {
                           aRank = 1;
                           aFix = 1;
                        } else if (alliance.getLeaderId() != cid && aRank != 2) {
                           aRank = 2;
                           aFix = 2;
                        }
                     }
                  } else {
                     if (gRank == 1) {
                        gRank = 2;
                        gFix = 2;
                     }

                     if (aRank < 3) {
                        aRank = 3;
                        aFix = 3;
                     }
                  }

                  this.members
                     .add(
                        new GuildCharacter(
                           cid,
                           rs.getShort("level"),
                           rs.getString("name"),
                           (byte)-1,
                           rs.getInt("job"),
                           gRank,
                           rs.getInt("guildContribution"),
                           aRank,
                           guildid,
                           rs.getTimestamp("createdate").getTime(),
                           rs.getInt("todayContribution"),
                           rs.getTimestamp("last_loggedin_date").getTime(),
                           rs.getTimestamp("today_loggedin_date").getTime(),
                           false
                        )
                     );
               } while (rs.next());

               rs.close();
               ps.close();
               if (!leaderCheck) {
                  System.err.println("Leader " + this.leader + " isn't in guild " + this.id + ".  Impossible... guild is disbanding.");
                  this.writeToDB(true);
                  this.proper = false;
                  return;
               }

               if (gFix > 0) {
                  ps = con.prepareStatement("UPDATE characters SET guildrank = ? WHERE id = ?");
                  ps.setByte(1, gFix);
                  ps.setInt(2, this.leader);
                  ps.executeUpdate();
                  ps.close();
               }

               if (aFix > 0) {
                  ps = con.prepareStatement("UPDATE characters SET alliancerank = ? WHERE id = ?");
                  ps.setByte(1, aFix);
                  ps.setInt(2, this.leader);
                  ps.executeUpdate();
                  ps.close();
               }

               ps = con.prepareStatement("SELECT * FROM guild_request_member WHERE guild_id = ?");
               ps.setInt(1, guildid);
               rs = ps.executeQuery();

               while (rs.next()) {
                  Guild.JoinRequester r = new Guild.JoinRequester(
                     rs.getInt("id"), rs.getInt("job"), rs.getInt("level"), rs.getString("name"), rs.getString("introduce")
                  );
                  this.requesters.put(rs.getInt("id"), r);
               }

               ps = con.prepareStatement("SELECT * FROM guildskills WHERE guildid = ?");
               ps.setInt(1, guildid);
               rs = ps.executeQuery();

               while (rs.next()) {
                  int sid = rs.getInt("skillid");
                  if (sid < 91000000) {
                     rs.close();
                     ps.close();
                     System.err.println("Skill " + sid + " is in guild " + this.id + ".  Impossible... guild is disbanding.");
                     this.writeToDB(true);
                     this.proper = false;
                     return;
                  }

                  this.guildSkills.put(sid, new GuildSkill(sid, rs.getInt("level"), rs.getLong("timestamp"), rs.getString("purchaser"), ""));
               }

               ps = con.prepareStatement("SELECT * FROM guildcontents WHERE guildid = ?");
               ps.setInt(1, guildid);
               rs = ps.executeQuery();

               while (rs.next()) {
                  int characterid = rs.getInt("characterid");
                  int type = rs.getInt("type");
                  int lastweekpoint = rs.getInt("lastweekpoint");
                  long lastweektime = rs.getLong("lastweektime");
                  int thisweekpoint = rs.getInt("thisweekpoint");
                  long thisweektime = rs.getLong("thisweektime");
                  this.contentsLogs.add(new GuildContentsLog(characterid, type, lastweekpoint, lastweektime, thisweekpoint, thisweektime));
               }

               rs.close();
               ps.close();
               this.level = this.calculateLevel();
               return;
            }

            rs.close();
            ps.close();
            this.id = -1;
         }
      } catch (SQLException var21) {
         System.err.println("unable to read guild information from sql");
         var21.printStackTrace();
      }
   }

   public boolean isProper() {
      return this.proper;
   }

   public final void writeToDB(boolean bDisband) {
      try (Connection con = DBConnection.getConnection()) {
         if (bDisband) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM guildcontents WHERE guildid = ?");
            ps.setInt(1, this.id);
            ps.execute();
            ps.close();
            ps = con.prepareStatement("DELETE FROM guildskills WHERE guildid = ?");
            ps.setInt(1, this.id);
            ps.execute();
            ps.close();
            ps = con.prepareStatement("DELETE FROM guilds WHERE guildid = ?");
            ps.setInt(1, this.id);
            ps.executeUpdate();
            ps.close();
            if (this.allianceid > 0) {
               Alliance alliance = Center.Alliance.getAlliance(this.allianceid);
               if (alliance != null) {
                  alliance.removeGuild(this.id, false);
               }
            }

            PacketEncoder p = new PacketEncoder();
            GuildPacket.DisbandGuild disbandGuild = new GuildPacket.DisbandGuild(this.id);
            disbandGuild.encode(p);
            this.broadcast(p.getPacket());
         } else {
            StringBuilder buf = new StringBuilder("UPDATE guilds SET GP = ?, logo = ?, logoColor = ?, logoBG = ?, logoBGColor = ?, ");

            for (int i = 1; i < 6; i++) {
               buf.append("rank").append(i).append("title = ?, ");
            }

            buf.append(
               "capacity = ?, notice = ?, alliance = ?, leader = ?, connectTimeFlag = ?, activityFlag = ?, ageGroupFlag = ?, allowJoinRequest = ?, rank1permission = ?, rank2permission = ?, rank3permission = ?, rank4permission = ?, rank5permission = ?, honorEXP = ?, customEmblem = ?, noblessSkillPoint = ? WHERE guildid = ?"
            );
            PreparedStatement ps = con.prepareStatement(buf.toString());
            ps.setInt(1, this.gp);
            ps.setInt(2, this.logo);
            ps.setInt(3, this.logoColor);
            ps.setInt(4, this.logoBG);
            ps.setInt(5, this.logoBGColor);
            ps.setString(6, this.rankTitles[0]);
            ps.setString(7, this.rankTitles[1]);
            ps.setString(8, this.rankTitles[2]);
            ps.setString(9, this.rankTitles[3]);
            ps.setString(10, this.rankTitles[4]);
            ps.setInt(11, this.capacity);
            ps.setString(12, this.notice);
            ps.setInt(13, this.allianceid);
            ps.setInt(14, this.leader);
            ps.setInt(15, this.connectTimeFlag);
            ps.setInt(16, this.activityFlag);
            ps.setInt(17, this.ageGroupFlag);
            ps.setInt(18, this.allowJoinRequest ? 1 : 0);
            ps.setInt(19, this.rankPermission[0]);
            ps.setInt(20, this.rankPermission[1]);
            ps.setInt(21, this.rankPermission[2]);
            ps.setInt(22, this.rankPermission[3]);
            ps.setInt(23, this.rankPermission[4]);
            ps.setInt(24, this.honorEXP);
            Blob blob = null;
            if (this.customEmblem != null) {
               blob = new SerialBlob(this.customEmblem);
            }

            ps.setBlob(25, blob);
            ps.setInt(26, this.noblessSkillPoint);
            ps.setInt(27, this.id);
            ps.executeUpdate();
            ps.close();
            if (this.changed_skills) {
               ps = con.prepareStatement("DELETE FROM guildskills WHERE guildid = ?");
               ps.setInt(1, this.id);
               ps.execute();
               ps.close();
               ps = con.prepareStatement("INSERT INTO guildskills(`guildid`, `skillid`, `level`, `timestamp`, `purchaser`) VALUES(?, ?, ?, ?, ?)");
               ps.setInt(1, this.id);

               for (GuildSkill i : this.guildSkills.values()) {
                  ps.setInt(2, i.skillID);
                  ps.setByte(3, (byte)i.level);
                  ps.setLong(4, i.timestamp);
                  ps.setString(5, i.purchaser);
                  ps.execute();
               }

               ps.close();
            }

            this.changed_skills = false;
            if (this.change_guildLog) {
               ps = con.prepareStatement("DELETE FROM guildcontents WHERE guildid = ?");
               ps.setInt(1, this.id);
               ps.execute();
               ps.close();
               ps = con.prepareStatement(
                  "INSERT INTO guildcontents(`guildid`, `characterid`, `type`, `lastweekpoint`, `lastweektime`, `thisweekpoint`, `thisweektime`) VALUES(?, ?, ?, ?, ?, ?, ?)"
               );
               ps.setInt(1, this.id);

               for (GuildContentsLog i : this.contentsLogs) {
                  ps.setInt(2, i.getCharacterid());
                  ps.setInt(3, i.getType());
                  ps.setInt(4, i.getLastweekpoint());
                  ps.setLong(5, i.getLastweektime());
                  ps.setInt(6, i.getThisweekpoint());
                  ps.setLong(7, i.getThisweektime());
                  ps.execute();
               }

               ps.close();
               this.change_guildLog = false;
            }
         }
      } catch (SQLException var10) {
         System.err.println("Error saving guild to SQL");
         var10.printStackTrace();
      }
   }

   public final int getId() {
      return this.id;
   }

   public final int getLeaderId() {
      return this.leader;
   }

   public final MapleCharacter getLeader(MapleClient c) {
      return c.getChannelServer().getPlayerStorage().getCharacterById(this.leader);
   }

   public final int getGP() {
      return this.gp;
   }

   public final int getLogo() {
      return this.logo;
   }

   public final void setLogo(int l) {
      this.logo = l;
   }

   public final int getLogoColor() {
      return this.logoColor;
   }

   public final void setLogoColor(int c) {
      this.logoColor = c;
   }

   public final int getLogoBG() {
      return this.logoBG;
   }

   public final void setLogoBG(int bg) {
      this.logoBG = bg;
   }

   public final int getLogoBGColor() {
      return this.logoBGColor;
   }

   public final void setLogoBGColor(int c) {
      this.logoBGColor = c;
   }

   public final String getNotice() {
      return this.notice == null ? "" : this.notice;
   }

   public final String getName() {
      return this.name;
   }

   public final int getCapacity() {
      return this.capacity;
   }

   public final int getSignature() {
      return this.signature;
   }

   public final void broadcast(byte[] packet) {
      this.broadcast(packet, -1, Guild.BCOp.NONE);
   }

   public final void broadcast(byte[] packet, int exception) {
      this.broadcast(packet, exception, Guild.BCOp.NONE);
   }

   public final void broadcast(byte[] packet, int exceptionId, Guild.BCOp bcop) {
      this.lock.writeLock().lock();

      try {
         this.buildNotifications();
      } finally {
         this.lock.writeLock().unlock();
      }

      this.lock.readLock().lock();

      try {
         for (GuildCharacter mgc : this.members) {
            if (bcop == Guild.BCOp.DISBAND) {
               if (mgc.isOnline()) {
                  Center.Guild.setGuildAndRank(mgc.getId(), 0, 5, 0, 5);
               } else {
                  setOfflineGuildStatus(0, (byte)5, 0, (byte)5, mgc.getId());
               }
            } else if (mgc.isOnline() && mgc.getId() != exceptionId) {
               if (bcop != Guild.BCOp.EMBELMCHANGE && bcop != Guild.BCOp.CUSTOMEMBLEMCHANGE) {
                  Center.Broadcast.sendGuildPacket(mgc.getId(), packet, exceptionId, this.id);
               } else {
                  Center.Guild.changeEmblem(this.id, mgc.getId(), this, bcop == Guild.BCOp.CUSTOMEMBLEMCHANGE, packet, 0);
               }
            }
         }
      } finally {
         this.lock.readLock().unlock();
      }
   }

   private final void buildNotifications() {
      if (this.bDirty) {
         List<Integer> mem = new LinkedList<>();

         for (GuildCharacter mgc : this.members) {
            if (mgc.isOnline()) {
               if (!mem.contains(mgc.getId()) && mgc.getGuildId() == this.id) {
                  mem.add(mgc.getId());
               } else {
                  this.members.remove(mgc);
               }
            }
         }

         this.bDirty = false;
      }
   }

   public final void setOnline(int cid, String chrName, boolean online, int channel) {
      this.setOnline(cid, chrName, online, channel, false);
   }

   public final void setOnline(int cid, String chrName, boolean online, int channel, boolean forceBroadcast) {
      this.setOnline(cid, chrName, online, channel, forceBroadcast, true);
   }

   public final void setOnline(int cid, String chrName, boolean online, int channel, boolean forceBroadcast, boolean show) {
      try {
         boolean bBroadcast = true;
         GuildCharacter gc = null;

         for (GuildCharacter mgc : this.members) {
            if (mgc.getGuildId() == this.id && mgc.getId() == cid) {
               if (mgc.isOnline() == online && !forceBroadcast) {
                  bBroadcast = false;
               }

               if (!mgc.getName().equals(chrName)) {
                  mgc.setName(chrName);
               }

               mgc.setOnline(online);
               mgc.setChannel((byte)channel);
               gc = mgc;
               break;
            }
         }

         if (bBroadcast) {
            PacketEncoder packet = new PacketEncoder();
            GuildPacket.MemberLogOnOff onOff = new GuildPacket.MemberLogOnOff(
               this.getId(),
               cid,
               online,
               PacketHelper.getKoreanTimestamp(System.currentTimeMillis()),
               gc != null ? gc.getJobId() : 0,
               gc != null ? gc.getLevel() : 0,
               show
            );
            onOff.encode(packet);
            if (this.allianceid > 0) {
               Center.Alliance.sendGuild(packet.getPacket(), -1, this.allianceid);
            } else {
               this.broadcast(packet.getPacket());
            }
         }

         this.bDirty = true;
         this.init = true;
      } catch (Exception var11) {
         System.out.println("[오류] MapleGuild setOnline함수 실행 중 오류 발생! " + var11.toString());
         var11.printStackTrace();
      }
   }

   public final void guildChat(MapleCharacter chr, int cid, String msg, Item item, String itemName, int achievementID, long achievementTime) {
      this.broadcast(CField.multiChat(chr, msg, 2, item, itemName, achievementID, achievementTime, new ReportLogEntry(chr.getName(), msg, chr.getId())), cid);
   }

   public final void allianceChat(MapleCharacter chr, int cid, String msg, Item item, String itemName, int achievementID, long achievementTime) {
      this.broadcast(CField.multiChat(chr, msg, 3, item, itemName, achievementID, achievementTime, new ReportLogEntry(chr.getName(), msg, chr.getId())), cid);
   }

   public final String getRankTitle(int rank) {
      return this.rankTitles[rank - 1];
   }

   public int getAllianceId() {
      return this.allianceid;
   }

   public int getInvitedId() {
      return this.invitedid;
   }

   public void setInvitedId(int iid) {
      this.invitedid = iid;
   }

   public void setAllianceId(int a) {
      this.allianceid = a;
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("UPDATE guilds SET alliance = ? WHERE guildid = ?");
         ps.setInt(1, a);
         ps.setInt(2, this.id);
         ps.execute();
         ps.close();
      } catch (SQLException var8) {
         System.err.println("Saving allianceid ERROR" + var8);
      }
   }

   public static final boolean checkAvailableName(String name) {
      return Center.Guild.getGuildByName(name) == null;
   }

   public static final int createGuild(int leaderId, String name) {
      if (name.length() > 12) {
         return 0;
      } else {
         DBConnection db = new DBConnection();

         try {
            int var7;
            try (Connection con = DBConnection.getConnection()) {
               PreparedStatement ps = con.prepareStatement("SELECT guildid FROM guilds WHERE name = ?");
               ps.setString(1, name);
               ResultSet rs = ps.executeQuery();
               if (rs.first()) {
                  rs.close();
                  ps.close();
                  return 0;
               }

               rs.close();
               ps.close();
               ps = con.prepareStatement(
                  "INSERT INTO guilds (`leader`, `name`, `signature`, `alliance`, `rank1title`, `rank2title`, `rank3title`, `rank4title`, `rank5title`) VALUES (?, ?, ?, 0, ?, ?, ?, ?, ?)",
                  1
               );
               ps.setInt(1, leaderId);
               ps.setString(2, name);
               ps.setInt(3, (int)(System.currentTimeMillis() / 1000L));
               ps.setString(4, "마스터");
               ps.setString(5, "부마스터");
               ps.setString(6, "길드원");
               ps.setString(7, "길드원");
               ps.setString(8, "길드원");
               ps.executeUpdate();
               rs = ps.getGeneratedKeys();
               int ret = 0;
               if (rs.next()) {
                  ret = rs.getInt(1);
               }

               rs.close();
               ps.close();
               var7 = ret;
            }

            return var7;
         } catch (SQLException var10) {
            System.err.println("SQL THROW");
            var10.printStackTrace();
            return 0;
         }
      }
   }

   public final int addGuildMember(GuildCharacter mgc) {
      this.lock.writeLock().lock();

      try {
         if (this.members.size() >= this.capacity) {
            return 0;
         }

         for (int i = this.members.size() - 1; i >= 0; i--) {
            if (this.members.get(i).getGuildRank() < 5 || this.members.get(i).getName().compareTo(mgc.getName()) < 0) {
               this.members.add(i + 1, mgc);
               this.bDirty = true;
               break;
            }
         }
      } finally {
         this.lock.writeLock().unlock();
      }

      this.gainGP(500, true, mgc.getId());
      PacketEncoder var6 = new PacketEncoder();
      GuildPacket.JoinMember joinMember = new GuildPacket.JoinMember(mgc);
      joinMember.encode(var6);
      this.broadcast(var6.getPacket());
      if (this.allianceid > 0) {
         Center.Alliance.sendGuild(var6.getPacket(), -1, this.allianceid);
      }

      return 1;
   }

   public final void leaveGuild(GuildCharacter mgc) {
      this.lock.writeLock().lock();

      try {
         for (GuildCharacter mgcc : this.members) {
            if (mgcc.getId() == mgc.getId()) {
               PacketEncoder p = new PacketEncoder();
               GuildPacket.WithdrawGuildResult result = new GuildPacket.WithdrawGuildResult(mgc.getGuildId(), mgc.getId(), mgc.getName());
               result.encode(p);
               this.broadcast(p.getPacket());
               this.bDirty = true;
               this.setHonorEXP(this.getHonorEXP() - (mgcc.getGuildContribution() > 0 ? mgcc.getGuildContribution() : 0));
               this.setLevel(this.calculateLevel());
               this.members.remove(mgcc);
               if (mgc.isOnline()) {
                  Center.Guild.setGuildAndRank(mgcc.getId(), 0, 5, 0, 5);
               } else {
                  setOfflineGuildStatus(0, (byte)5, 0, (byte)5, mgcc.getId());
               }

               p = new PacketEncoder();
               GuildPacket.UpdateGuildPoint gp = new GuildPacket.UpdateGuildPoint(this);
               gp.encode(p);
               this.broadcast(p.getPacket());
               break;
            }
         }
      } finally {
         this.lock.writeLock().unlock();
      }

      if (this.allianceid > 0) {
         PacketEncoder p = new PacketEncoder();
         GuildPacket.WithdrawGuildResult result = new GuildPacket.WithdrawGuildResult(mgc.getGuildId(), mgc.getId(), mgc.getName());
         result.encode(p);
         Center.Alliance.sendGuild(p.getPacket(), -1, this.allianceid);
      }
   }

   public final void expelMember(GuildCharacter initiator, String name, int cid) {
      this.lock.writeLock().lock();

      try {
         for (GuildCharacter mgc : this.members) {
            if (mgc.getId() == cid && initiator.getGuildRank() < mgc.getGuildRank()) {
               PacketEncoder p = new PacketEncoder();
               GuildPacket.KickMember kickMember = new GuildPacket.KickMember(this.getId(), cid, name);
               kickMember.encode(p);
               this.broadcast(p.getPacket());
               this.bDirty = true;
               this.setHonorEXP(this.getHonorEXP() - (mgc.getGuildContribution() > 0 ? mgc.getGuildContribution() : 50));
               this.setLevel(this.calculateLevel());
               if (mgc.isOnline()) {
                  Center.Guild.setGuildAndRank(cid, 0, 5, 0, 5);
               } else {
                  MapleCharacterUtil.sendNote(mgc.getName(), initiator.getName(), "당신은 길드에서 추방되었습니다.", 0);
                  setOfflineGuildStatus(0, (byte)5, 0, (byte)5, cid);
               }

               this.members.remove(mgc);
               p = new PacketEncoder();
               GuildPacket.UpdateGuildPoint point = new GuildPacket.UpdateGuildPoint(this);
               point.encode(p);
               this.broadcast(p.getPacket());
               break;
            }
         }
      } finally {
         this.lock.writeLock().unlock();
      }

      if (this.allianceid > 0) {
         PacketEncoder p = new PacketEncoder();
         GuildPacket.KickMember kickMember = new GuildPacket.KickMember(this.getId(), cid, name);
         kickMember.encode(p);
         Center.Alliance.sendGuild(p.getPacket(), -1, this.allianceid);
      }
   }

   public final void changeARank() {
      this.changeARank(false);
   }

   public final void changeARank(boolean leader) {
      if (this.allianceid > 0) {
         for (GuildCharacter mgc : this.members) {
            byte newRank = 3;
            if (this.leader == mgc.getId()) {
               newRank = (byte)(leader ? 1 : 2);
            }

            if (mgc.isOnline()) {
               Center.Guild.setGuildAndRank(mgc.getId(), this.id, mgc.getGuildRank(), mgc.getGuildContribution(), newRank);
            } else {
               setOfflineGuildStatus(this.id, mgc.getGuildRank(), mgc.getGuildContribution(), newRank, mgc.getId());
            }

            mgc.setAllianceRank(newRank);
         }

         Center.Alliance.sendGuild(this.allianceid);
      }
   }

   public final void changeARank(int newRank) {
      if (this.allianceid > 0) {
         for (GuildCharacter mgc : this.members) {
            if (mgc.isOnline()) {
               Center.Guild.setGuildAndRank(mgc.getId(), this.id, mgc.getGuildRank(), mgc.getGuildContribution(), newRank);
            } else {
               setOfflineGuildStatus(this.id, mgc.getGuildRank(), mgc.getGuildContribution(), (byte)newRank, mgc.getId());
            }

            mgc.setAllianceRank((byte)newRank);
         }

         Center.Alliance.sendGuild(this.allianceid);
      }
   }

   public final boolean changeARank(int cid, int newRank) {
      if (this.allianceid <= 0) {
         return false;
      } else {
         for (GuildCharacter mgc : this.members) {
            if (cid == mgc.getId()) {
               if (mgc.isOnline()) {
                  Center.Guild.setGuildAndRank(cid, this.id, mgc.getGuildRank(), mgc.getGuildContribution(), newRank);
               } else {
                  setOfflineGuildStatus(this.id, mgc.getGuildRank(), mgc.getGuildContribution(), (byte)newRank, cid);
               }

               mgc.setAllianceRank((byte)newRank);
               PacketEncoder p = new PacketEncoder();
               GuildPacket.ChangeAllianceRank rank = new GuildPacket.ChangeAllianceRank(this.allianceid, this.getId(), cid, (byte)newRank);
               rank.encode(p);
               Center.Alliance.sendGuild(p.getPacket(), -1, this.allianceid);
               return true;
            }
         }

         return false;
      }
   }

   public final void changeGuildLeader(int cid) {
      if (this.changeRank(cid, 1) && this.changeRank(this.leader, 2)) {
         if (this.allianceid > 0) {
            int aRank = this.getMGC(this.leader).getAllianceRank();
            int lead = this.leader;
            if (aRank == 1) {
               Center.Alliance.changeAllianceLeader(this.allianceid, cid, true);
            } else {
               this.changeARank(cid, aRank);
            }

            this.changeARank(lead, 3);
         }

         PacketEncoder p = new PacketEncoder();
         GuildPacket.ChangeLeader changeLeader = new GuildPacket.ChangeLeader(this.id, this.leader, cid, (byte)0, (byte)1);
         changeLeader.encode(p);
         this.broadcast(p.getPacket());
         this.leader = cid;
         DBConnection db = new DBConnection();

         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE guilds SET leader = ? WHERE guildid = ?");
            ps.setInt(1, cid);
            ps.setInt(2, this.id);
            ps.execute();
            ps.close();
         } catch (SQLException var10) {
            System.err.println("Saving leaderid ERROR" + var10);
         }
      }
   }

   public final boolean changeRank(int cid, int newRank) {
      for (GuildCharacter mgc : this.members) {
         if (cid == mgc.getId()) {
            if (mgc.isOnline()) {
               Center.Guild.setGuildAndRank(cid, this.id, newRank, mgc.getGuildContribution(), mgc.getAllianceRank());
            } else {
               setOfflineGuildStatus(this.id, (byte)newRank, mgc.getGuildContribution(), mgc.getAllianceRank(), cid);
            }

            mgc.setGuildRank((byte)newRank);
            PacketEncoder packet = new PacketEncoder();
            GuildPacket.ChangeRank changeRank = new GuildPacket.ChangeRank(this.getId(), cid, (byte)newRank);
            changeRank.encode(packet);
            this.broadcast(packet.getPacket());
            return true;
         }
      }

      return false;
   }

   public final void attendanceCheck(int playerID, int date) {
      PacketEncoder p = new PacketEncoder();
      GuildPacket.Attendance attendance = new GuildPacket.Attendance(this.getId(), playerID, date);
      attendance.encode(p);
      this.broadcast(p.getPacket());

      for (GuildCharacter mgc : this.members) {
         if (playerID == mgc.getId()) {
            mgc.setLastAttendanceDate(date);
         }
      }
   }

   public final void setGuildNotice(String notice, int playerID) {
      this.notice = notice;
      PacketEncoder p = new PacketEncoder();
      GuildPacket.UpdateNotice updateNotice = new GuildPacket.UpdateNotice(this.getId(), playerID, notice);
      updateNotice.encode(p);
      this.broadcast(p.getPacket());
   }

   public final void setJoinSetting(int playerID, boolean allowJoinRequest, int connectTimeFlag, int activityFlag, int ageGroupFlag) {
      this.allowJoinRequest = allowJoinRequest;
      this.connectTimeFlag = connectTimeFlag;
      this.activityFlag = activityFlag;
      this.ageGroupFlag = ageGroupFlag;
      PacketEncoder packet = new PacketEncoder();
      GuildPacket.EditJoinSetting setting = new GuildPacket.EditJoinSetting(this.id, playerID, allowJoinRequest, connectTimeFlag, activityFlag, ageGroupFlag);
      setting.encode(packet);
      this.broadcast(packet.getPacket());
   }

   public final void memberLevelJobUpdate(GuildCharacter mgc) {
      for (GuildCharacter member : this.members) {
         if (member.getId() == mgc.getId()) {
            int old_level = member.getLevel();
            int old_job = member.getJobId();
            member.setJobId(mgc.getJobId());
            member.setLevel((short)mgc.getLevel());
            if (mgc.getLevel() > old_level) {
               this.gainGP((mgc.getLevel() - old_level) * mgc.getLevel(), false, mgc.getId());
            }

            if (mgc.getLevel() >= 200 && !DBConfig.isGanglim && old_level != mgc.getLevel() && !DBConfig.isGanglim) {
               this.broadcast(CWvsContext.sendLevelup(false, mgc.getLevel(), mgc.getName()), mgc.getId());
            }

            if (old_job != mgc.getJobId() && !DBConfig.isGanglim) {
               this.broadcast(CWvsContext.sendJobup(false, mgc.getJobId(), mgc.getName()), mgc.getId());
            }

            this.broadcast(CWvsContext.GuildPacket.guildMemberLevelJobUpdate(mgc));
            if (this.allianceid > 0) {
               Center.Alliance.sendGuild(CWvsContext.AlliancePacket.updateAlliance(mgc, this.allianceid), this.id, this.allianceid);
            }
            break;
         }
      }
   }

   public final void removeRankTitleRole(int playerID, int index) {
      String name = this.rankTitles[index - 1];
      this.rankTitles[index - 1] = "";
      this.rankPermission[index - 1] = 0;
      this.broadcast(CWvsContext.GuildPacket.removeRankTitleRole(this.id, playerID, index, name, this.rankTitles, this.rankPermission));
   }

   public final void changeRankTitleRole(boolean add, int playerID, int index, String newName, int newRole) {
      this.rankTitles[index - 1] = newName;
      this.rankPermission[index - 1] = newRole;
      PacketEncoder packet = new PacketEncoder();
      GuildPacket.ChangeRankRole role = new GuildPacket.ChangeRankRole(
         add ? GuildRequestResultType.Result.AddRank : GuildRequestResultType.Result.ChangeRankRole,
         this.getId(),
         playerID,
         this.rankTitles,
         this.rankPermission
      );
      role.encode(packet);
      this.broadcast(packet.getPacket());
   }

   public final void disbandGuild() {
      this.writeToDB(true);
      this.broadcast(null, -1, Guild.BCOp.DISBAND);
   }

   public final boolean setGuildEmblem(short bg, byte bgcolor, short logo, byte logocolor, Guild.BCOp bcop, byte[] imageData) {
      int reqGP = bcop == Guild.BCOp.CUSTOMEMBLEMCHANGE ? 225000 : 150000;
      if (this.gp < reqGP) {
         return false;
      } else {
         this.logoBG = bg;
         this.logoBGColor = bgcolor;
         this.logo = logo;
         this.logoColor = logocolor;
         if (bcop == Guild.BCOp.CUSTOMEMBLEMCHANGE) {
            this.setCustomEmblem(imageData);
         }

         if (bcop == Guild.BCOp.EMBELMCHANGE) {
            this.setCustomEmblem(null);
         }

         this.broadcast(imageData, -1, bcop);
         this.gainGP(-reqGP);
         DBConnection db = new DBConnection();

         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
               "UPDATE guilds SET logo = ?, logoColor = ?, logoBG = ?, logoBGColor = ?, customEmblem = ? WHERE guildid = ?"
            );
            ps.setInt(1, logo);
            ps.setInt(2, this.logoColor);
            ps.setInt(3, this.logoBG);
            ps.setInt(4, this.logoBGColor);
            ps.setBytes(5, this.customEmblem);
            ps.setInt(6, this.id);
            ps.execute();
            ps.close();
         } catch (SQLException var14) {
            System.err.println("Saving guild logo / BG colo ERROR");
            var14.printStackTrace();
         }

         return true;
      }
   }

   public final GuildCharacter getMGC(int cid) {
      for (GuildCharacter mgc : this.members) {
         if (mgc.getId() == cid) {
            return mgc;
         }
      }

      return null;
   }

   public final boolean increaseCapacity(boolean trueMax) {
      if (this.capacity >= 200 || this.capacity + 5 > 200) {
         return false;
      } else if (trueMax && this.gp < 25000) {
         return false;
      } else if (trueMax && this.gp - 25000 < GameConstants.getGuildExpNeededForLevel(this.getLevel() - 1)) {
         return false;
      } else {
         this.capacity += 5;
         PacketEncoder p = new PacketEncoder();
         GuildPacket.ChangeCapacity changeCapacity = new GuildPacket.ChangeCapacity(this.id, (byte)this.capacity);
         changeCapacity.encode(p);
         this.broadcast(p.getPacket());
         DBConnection db = new DBConnection();

         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE guilds SET capacity = ? WHERE guildid = ?");
            ps.setInt(1, this.capacity);
            ps.setInt(2, this.id);
            ps.execute();
            ps.close();
         } catch (SQLException var10) {
            System.err.println("Saving guild capacity ERROR");
            var10.printStackTrace();
         }

         return true;
      }
   }

   public final void gainGP(int amount) {
      this.gainGP(amount, true, -1);
   }

   public final void gainGP(int amount, boolean broadcast) {
      this.gainGP(amount, broadcast, -1);
   }

   public final void gainGP(int amount, boolean broadcast, int cid) {
      if (amount != 0) {
         if (amount + this.gp < 0) {
            amount = -this.gp;
         }

         this.gp += amount;
         PacketEncoder p = new PacketEncoder();
         GuildPacket.UpdateGuildPoint point = new GuildPacket.UpdateGuildPoint(this);
         point.encode(p);
         this.broadcast(p.getPacket());
         if (broadcast) {
            this.broadcast(CWvsContext.InfoPacket.getGPMsg(amount));
         }
      }
   }

   public Collection<GuildSkill> getSkills() {
      return this.guildSkills.values();
   }

   public int getSkillLevel(int sid) {
      return !this.guildSkills.containsKey(sid) ? 0 : this.guildSkills.get(sid).level;
   }

   public boolean resetGuildSkill() {
      Collection<GuildSkill> skill = this.getSkills();
      if (skill != null) {
         for (GuildSkill s : skill) {
            if (s.level > 0) {
               s.level = 0;
               this.broadcast(CWvsContext.GuildPacket.guildSkillPurchased(this.id, s.skillID, s.level, s.timestamp, this.name, this.name));
            }
         }

         for (GuildCharacter mgc : this.getMembers()) {
            if (mgc != null && mgc.isOnline()) {
               MapleCharacter player = GameServer.getInstance(mgc.getChannel()).getPlayerStorage().getCharacterById(mgc.getId());
               if (player != null) {
                  player.getStat().recalcLocalStats(player);
               }
            }
         }

         this.changed_skills = true;
         return true;
      } else {
         return false;
      }
   }

   public boolean purchaseSkill(int skill, int levelUp, String name, int cid) {
      SecondaryStatEffect skillid = SkillFactory.getSkill(skill).getEffect(this.getSkillLevel(skill) + levelUp);
      if (skillid.getReqGuildLevel() <= this.getLevel() && skillid.getLevel() > this.getSkillLevel(skill)) {
         GuildSkill ourSkill = this.guildSkills.get(skill);
         if (ourSkill == null) {
            ourSkill = new GuildSkill(skill, skillid.getLevel(), 0L, name, name);
            this.guildSkills.put(skill, ourSkill);
         } else {
            ourSkill.level = skillid.getLevel();
            ourSkill.purchaser = name;
            ourSkill.activator = name;
         }

         if (skillid.getPeriod() <= 0) {
            ourSkill.timestamp = -1L;
         } else {
            ourSkill.timestamp = System.currentTimeMillis() + skillid.getPeriod() * 60000L;
         }

         this.changed_skills = true;
         this.gainGP(1000, true, cid);
         PacketEncoder p = new PacketEncoder();
         GuildPacket.PurchaseGuildSkill s = new GuildPacket.PurchaseGuildSkill(
            this.id, skill, 0, (short)ourSkill.level, PacketHelper.getTime(ourSkill.timestamp), name, name
         );
         s.encode(p);
         this.broadcast(p.getPacket());

         for (GuildCharacter mgc : this.getMembers()) {
            if (mgc != null && mgc.isOnline()) {
               MapleCharacter player = GameServer.getInstance(mgc.getChannel()).getPlayerStorage().getCharacterById(mgc.getId());
               if (player != null) {
                  player.getStat().recalcLocalStats(player);
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean purchaseNobleSkill(int skill, String name, int cid, int incLevel) {
      SecondaryStatEffect skillid = SkillFactory.getSkill(skill).getEffect(this.getSkillLevel(skill) + incLevel);
      if (skillid.getLevel() <= this.getSkillLevel(skill)) {
         return false;
      } else {
         GuildSkill ourSkill = this.guildSkills.get(skill);
         if (ourSkill == null) {
            ourSkill = new GuildSkill(skill, skillid.getLevel(), 0L, name, name);
            this.guildSkills.put(skill, ourSkill);
         } else {
            ourSkill.level = skillid.getLevel();
            ourSkill.purchaser = name;
            ourSkill.activator = name;
         }

         if (skillid.getPeriod() <= 0) {
            ourSkill.timestamp = -1L;
         } else {
            ourSkill.timestamp = System.currentTimeMillis() + skillid.getPeriod() * 60000L;
         }

         this.changed_skills = true;
         this.gainGP(1000 * incLevel, true, cid);
         this.broadcast(CWvsContext.GuildPacket.guildSkillPurchased(this.id, skill, ourSkill.level, ourSkill.timestamp, name, name));

         for (GuildCharacter mgc : this.getMembers()) {
            if (mgc != null && mgc.isOnline()) {
               MapleCharacter player = GameServer.getInstance(mgc.getChannel()).getPlayerStorage().getCharacterById(mgc.getId());
               if (player != null) {
                  player.getStat().recalcLocalStats(player);
               }
            }
         }

         return true;
      }
   }

   public int getLevel() {
      return this.level;
   }

   public final int calculateLevel() {
      for (int i = 1; i < 30; i++) {
         if (this.honorEXP < GameConstants.getGuildExpNeededForLevel(i)) {
            return i;
         }
      }

      return 30;
   }

   public final void encodeMemberData(PacketEncoder mplew) {
      mplew.writeShort(this.members.size());

      for (GuildCharacter mgc : this.members) {
         mplew.writeInt(mgc.getId());
         mplew.writeMapleAsciiString(mgc.getName());
         mplew.writeInt(mgc.getJobId());
         mplew.writeInt(mgc.getLevel());
         mplew.writeInt(mgc.getGuildRank());
         mplew.writeInt(mgc.getAllianceRank());
         mplew.writeLong(PacketHelper.getTime(mgc.getLastLoggedInDate() - 432000000L));
         mplew.write(mgc.isOnline() ? 1 : 0);
         mplew.writeLong(PacketHelper.getTime(mgc.getTodayLoggedInDate()));
         mplew.writeInt(mgc.getGuildContribution());
         mplew.writeInt(mgc.getTodayContribution());
         mplew.writeLong(PacketHelper.getTime(-2L));
         mplew.writeInt(mgc.getLastAttendanceDate());
         mplew.writeLong(PacketHelper.getTime(mgc.getCreateDate()));
      }
   }

   public static final GuildResponse sendInvite(MapleClient c, String targetName) {
      MapleCharacter mc = c.getChannelServer().getPlayerStorage().getCharacterByName(targetName);
      if (mc == null) {
         return GuildResponse.NOT_IN_CHANNEL;
      } else if (mc.getGuildId() > 0) {
         return GuildResponse.ALREADY_IN_GUILD;
      } else {
         Guild gs = Center.Guild.getGuild(c.getPlayer().getGuildId());
         if (gs == null) {
            return null;
         } else {
            GuildPacket.sendGuildPacket(
               mc,
               new GuildPacket.InviteGuild(
                  c.getPlayer().getGuildId(),
                  gs.getName(),
                  c.getPlayer().getId(),
                  c.getPlayer().getName(),
                  c.getPlayer().getLevel(),
                  c.getPlayer().getJob(),
                  0,
                  0
               )
            );
            return null;
         }
      }
   }

   public Collection<GuildCharacter> getMembers() {
      return Collections.unmodifiableCollection(this.members);
   }

   public int getAvgLevel() {
      int totalLevel = 0;

      for (GuildCharacter mgc : this.members) {
         totalLevel += mgc.getLevel();
      }

      return totalLevel / this.members.size();
   }

   public final boolean isInit() {
      return this.init;
   }

   public boolean hasSkill(int id) {
      return this.guildSkills.containsKey(id);
   }

   public static void setOfflineGuildStatus(int guildid, byte guildrank, int contribution, byte alliancerank, int cid) {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("UPDATE characters SET guildid = ?, guildrank = ?, guildContribution = ?, alliancerank = ? WHERE id = ?");
         ps.setInt(1, guildid);
         ps.setInt(2, guildrank);
         ps.setInt(3, contribution);
         ps.setInt(4, alliancerank);
         ps.setInt(5, cid);
         ps.executeUpdate();
         ps.close();
      } catch (SQLException var11) {
         System.out.println("SQLException: " + var11.getLocalizedMessage());
         var11.printStackTrace();
      }
   }

   public String getLeaderName() {
      for (GuildCharacter gc : this.members) {
         if (gc.getId() == this.leader) {
            return gc.getName();
         }
      }

      return "메이플스토리";
   }

   public void addRequest(GuildCharacter mgc) {
      this.requests.add(mgc);
   }

   public Map<Integer, Guild.JoinRequester> getJoinRequesters() {
      return new HashMap<>(this.requesters);
   }

   public void insertJoinRequester(MapleCharacter p, String introduce) {
      Guild.JoinRequester r = new Guild.JoinRequester(p.getId(), p.getJob(), p.getLevel(), p.getName(), introduce);
      if (p == null) {
         throw new RuntimeException("없는 유저가 가입 신청하려고 했습니다.");
      } else {
         r.insert();
         this.requesters.put(p.getId(), r);
         p.updateOneInfo(26015, "name", this.getName());
         p.updateOneInfo(26015, "guild", String.valueOf(this.id));
         p.updateOneInfo(26015, "remove_time", String.valueOf(System.currentTimeMillis()));
         PacketEncoder packet = new PacketEncoder();
         GuildPacket.InsertJoinRequester joinRequester = new GuildPacket.InsertJoinRequester(this.getId(), p.getId(), r);
         joinRequester.encode(packet);
         this.broadcast(packet.getPacket());
      }
   }

   public void removeJoinRequester(int playerId, boolean joined) {
      MapleCharacter p = null;

      for (GameServer cs : GameServer.getAllInstances()) {
         p = cs.getPlayerStorage().getCharacterById(playerId);
         if (p != null) {
            break;
         }
      }

      if (p != null) {
         p.updateOneInfo(26015, "name", "");
         p.updateOneInfo(26015, "guild", "");
         p.updateOneInfo(26015, "remove_time", String.valueOf(System.currentTimeMillis()));
         if (!joined) {
            PacketEncoder packet = new PacketEncoder();
            GuildPacket.DeleteJoinRequester r = new GuildPacket.DeleteJoinRequester(playerId, this.getId());
            r.encode(packet);
            p.send(packet.getPacket());
         }
      } else {
         DBConnection db = new DBConnection();

         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM `questinfo` where characterid = " + playerId + " and quest = 26015");
            ps.executeUpdate();
            ps.close();
         } catch (SQLException var10) {
            System.err.println(var10);
         }
      }

      PacketEncoder packet = new PacketEncoder();
      GuildPacket.DeleteJoinRequester r = new GuildPacket.DeleteJoinRequester(playerId, this.getId());
      r.encode(packet);
      this.broadcast(packet.getPacket());
      if (!this.requesters.isEmpty() && this.requesters.containsKey(playerId)) {
         Guild.JoinRequester rq = this.requesters.get(playerId);
         if (rq != null) {
            rq.delete();
            this.requesters.remove(playerId);
         }
      }
   }

   public static final int[] getGuildPointTable() {
      return new int[]{
         0,
         15000,
         60000,
         135000,
         240000,
         375000,
         540000,
         735000,
         960000,
         1215000,
         1500000,
         1815000,
         2160000,
         2535000,
         2940000,
         3375000,
         3840000,
         4335000,
         4860000,
         5415000,
         6000000,
         6615000,
         7235424,
         7935000,
         8640000,
         12528000,
         18165600,
         26340120,
         38193170,
         68747700
      };
   }

   public int getConnectTimeFlag() {
      return this.connectTimeFlag;
   }

   public void setConnectTimeFlag(int connectTimeFlag) {
      this.connectTimeFlag = connectTimeFlag;
   }

   public int getActivityFlag() {
      return this.activityFlag;
   }

   public void setActivityFlag(int activityFlag) {
      this.activityFlag = activityFlag;
   }

   public int getAgeGroupFlag() {
      return this.ageGroupFlag;
   }

   public void setAgeGroupFlag(int ageGroupFlag) {
      this.ageGroupFlag = ageGroupFlag;
   }

   public boolean isAllowJoinRequest() {
      return this.allowJoinRequest;
   }

   public void setAllowJoinRequest(boolean allowJoinRequest) {
      this.allowJoinRequest = allowJoinRequest;
   }

   public boolean isProcessRequester(int playerID) {
      return new HashMap<>(this.getJoinRequesters()).containsKey(playerID);
   }

   public int getRankPermission(int index) {
      return this.rankPermission[index];
   }

   public void setRankPermission(int index, int permission) {
      this.rankPermission[index] = permission;
   }

   public int getHonorEXP() {
      return this.honorEXP;
   }

   public void setHonorEXP(int honorEXP) {
      this.honorEXP = honorEXP;
   }

   public byte[] getCustomEmblem() {
      return this.customEmblem;
   }

   public void setCustomEmblem(byte[] customEmblem) {
      this.customEmblem = customEmblem;
   }

   public int getNoblessSkillPoint() {
      return this.noblessSkillPoint;
   }

   public void setNoblessSkillPoint(int noblessSkillPoint) {
      this.noblessSkillPoint = noblessSkillPoint;
   }

   public void setLevel(int level) {
      if (this.level <= level) {
         this.level = level;
      }
   }

   public List<GuildContentsLog> getGuildContentsLastWeekLogsByType(GuildContentsType type) {
      List<GuildContentsLog> ret = new ArrayList<>();

      for (GuildContentsLog flag : this.contentsLogs) {
         if (flag.getType() == type.getType() && flag.getLastweekpoint() > 0) {
            ret.add(flag);
         }
      }

      return ret;
   }

   public List<GuildContentsLog> getGuildContentsThisWeekLogsByType(GuildContentsType type) {
      List<GuildContentsLog> ret = new ArrayList<>();

      for (GuildContentsLog flag : this.contentsLogs) {
         if (flag.getType() == type.getType() && flag.getThisweekpoint() > 0) {
            ret.add(flag);
         }
      }

      return ret;
   }

   public int getGuildContentsLastWeekTotalScoreByType(GuildContentsType type) {
      int ret = 0;

      for (GuildContentsLog flag : this.contentsLogs) {
         if (flag.getType() == type.getType() && flag.getLastweekpoint() > 0) {
            ret += flag.getLastweekpoint();
         }
      }

      return type.getType() == 3 ? Math.min(200, ret) : ret;
   }

   public int getGuildContentsThisWeekTotalScoreByType(GuildContentsType type) {
      int ret = 0;

      for (GuildContentsLog flag : this.contentsLogs) {
         if (flag.getType() == type.getType() && flag.getThisweekpoint() > 0) {
            ret += flag.getThisweekpoint();
         }
      }

      return type.getType() == 3 ? Math.min(200, ret) : ret;
   }

   public long getGuildContentsThisWeekLastUpdateTimeByType(GuildContentsType type) {
      long ret = 0L;

      for (GuildContentsLog flag : this.contentsLogs) {
         if (flag.getType() == type.getType() && flag.getThisweektime() > 0L && flag.getThisweektime() > ret) {
            ret = flag.getThisweektime();
         }
      }

      return ret;
   }

   public int getPointLogByType(GuildContentsType type, MapleCharacter chr) {
      boolean find = false;
      GuildContentsLog log = null;

      for (GuildContentsLog flag : this.contentsLogs) {
         if (flag.getType() == type.getType() && flag.getCharacterid() == chr.getId()) {
            log = flag;
            find = true;
            break;
         }
      }

      return find ? log.getThisweekpoint() : 0;
   }

   public void setPointLog(GuildContentsType type, MapleCharacter chr, int point) {
      boolean find = false;
      GuildContentsLog log = null;

      for (GuildContentsLog flag : this.contentsLogs) {
         if (flag.getType() == type.getType() && flag.getCharacterid() == chr.getId()) {
            log = flag;
            find = true;
         }
      }

      if (find) {
         Date curDate = new Date();
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(curDate);
         boolean checkSunday = false;
         if (curDate.getDay() == 0) {
            checkSunday = true;
         }

         calendar.set(7, 2);
         calendar.set(10, 0);
         calendar.set(12, 0);
         calendar.set(13, 0);
         long thisWeekStartTime = calendar.getTime().getTime();
         long sundayLastWeekStartTime = thisWeekStartTime - 604800000L;
         if ((checkSunday || log.getThisweektime() >= thisWeekStartTime) && (!checkSunday || log.getThisweektime() >= sundayLastWeekStartTime)) {
            if (log.getThisweekpoint() < point) {
               this.change_guildLog = true;
               log.setThisweekpoint(point);
               log.setThisweektime(System.currentTimeMillis());
               this.broadcast(GuildContents.loadGuildLog(this));
            }
         } else {
            this.change_guildLog = true;
            log.setLastweektime(log.getThisweektime());
            log.setLastweekpoint(log.getThisweekpoint());
            log.setThisweekpoint(point);
            log.setThisweektime(System.currentTimeMillis());
            this.broadcast(GuildContents.loadGuildLog(this));
         }
      } else {
         this.change_guildLog = true;
         this.contentsLogs.add(new GuildContentsLog(chr.getId(), type.getType(), 0, 0L, point, System.currentTimeMillis()));
         this.broadcast(GuildContents.loadGuildLog(this));
      }
   }

   public synchronized void nobleSPAdjustment() {
      if (this.nobleA == null) {
         Calendar calz = Calendar.getInstance(TimeZone.getTimeZone("KST"), Locale.KOREAN);
         int day = calz.getTime().getDay();
         int hour = calz.getTime().getHours();
         int minute = calz.getTime().getMinutes();
         if (day == 0 && hour == 23 && minute >= 30) {
            this.nobleA = Timer.EventTimer.getInstance().schedule(new Runnable() {
               @Override
               public void run() {
                  int rewardNoblePoint = 0;
                  int totalFlagScore = 0;
                  int totalCulvertScore = 0;
                  int totalWeekMissionsScore = 0;

                  for (GuildContentsLog log : Guild.this.contentsLogs) {
                     switch (log.getType()) {
                        case 0:
                           totalFlagScore += log.getThisweekpoint();
                        case 1:
                        default:
                           break;
                        case 2:
                           totalCulvertScore += log.getThisweekpoint();
                           break;
                        case 3:
                           totalWeekMissionsScore += log.getThisweekpoint();
                     }

                     log.setLastweekpoint(log.getThisweekpoint());
                     log.setLastweektime(log.getLastweektime());
                     log.setThisweekpoint(0);
                     log.setThisweektime(0L);
                  }

                  for (GuildSkill i : Guild.this.guildSkills.values()) {
                     if (i.skillID == 91001022 || i.skillID == 91001023 || i.skillID == 91001024 || i.skillID == 91001025) {
                        i.level = 0;
                        i.purchaser = "";
                        Guild.this.changed_skills = true;
                        Guild.this.broadcast(CWvsContext.GuildPacket.guildSkillPurchased(Guild.this.id, i.skillID, 0, i.timestamp, "", ""));
                     }
                  }

                  rewardNoblePoint += Math.min(10, totalFlagScore / 1000);
                  rewardNoblePoint += Math.min(40, totalCulvertScore / 10000);
                  rewardNoblePoint += Math.min(10, Math.min(200, totalWeekMissionsScore) / 20);
                  Guild.this.setNoblessSkillPoint(rewardNoblePoint);
                  Guild.this.change_guildLog = true;
                  Guild.this.broadcast(GuildContents.loadGuildLog(Guild.this));
                  Guild.this.broadcast(CWvsContext.serverNotice(5, "길드 컨텐츠 참여 현황 및 노블 포인트가 정산되었습니다."));
                  Guild.this.nobleA = null;
               }
            }, 2400000L);
         }
      }
   }

   public void nobleSPAdjustmentF() {
      int rewardNoblePoint = 0;
      int totalFlagScore = 0;
      int totalCulvertScore = 0;
      int totalWeekMissionsScore = 0;

      for (GuildContentsLog log : this.contentsLogs) {
         switch (log.getType()) {
            case 0:
               totalFlagScore += log.getThisweekpoint();
            case 1:
            default:
               break;
            case 2:
               totalCulvertScore += log.getThisweekpoint();
               break;
            case 3:
               totalWeekMissionsScore += log.getThisweekpoint();
         }

         log.setLastweekpoint(log.getThisweekpoint());
         log.setLastweektime(log.getLastweektime());
         log.setThisweekpoint(0);
         log.setThisweektime(0L);
      }

      for (GuildSkill i : this.guildSkills.values()) {
         if (i.skillID == 91001022 || i.skillID == 91001023 || i.skillID == 91001024 || i.skillID == 91001025) {
            i.level = 0;
            i.purchaser = "";
            this.changed_skills = true;
            this.broadcast(CWvsContext.GuildPacket.guildSkillPurchased(this.id, i.skillID, 0, i.timestamp, "", ""));
         }
      }

      rewardNoblePoint += Math.min(25, totalFlagScore / 1000);
      rewardNoblePoint += Math.min(25, totalCulvertScore / 10000);
      rewardNoblePoint += Math.min(10, Math.min(200, totalWeekMissionsScore) / 20);
      this.setNoblessSkillPoint(rewardNoblePoint);
      this.change_guildLog = true;
      this.broadcast(GuildContents.loadGuildLog(this));
      this.broadcast(CWvsContext.serverNotice(5, "길드 컨텐츠 참여 현황 및 노블 포인트가 정산되었습니다."));
   }

   public void encode(PacketEncoder packet, boolean visit) {
      Alliance alliance = Center.Alliance.getAlliance(this.getAllianceId());
      if (this.getAllianceId() != 0 && alliance != null && !visit) {
         alliance.encode(packet);
      } else {
         this.encodeGuildInformation(packet);
      }
   }

   public void encodeGuildInformation(PacketEncoder packet) {
      packet.writeInt(this.getId());
      packet.writeMapleAsciiString(this.getName());

      for (int i = 1; i <= 10; i++) {
         packet.writeMapleAsciiString(this.getRankTitle(i));
         packet.writeInt(this.getRankPermission(i - 1));
      }

      packet.writeShort(this.getJoinRequesters().size());

      for (Entry<Integer, Guild.JoinRequester> entry : this.getJoinRequesters().entrySet()) {
         entry.getValue().encode(packet);
      }

      this.encodeMemberData(packet);
      packet.writeInt(this.getCapacity());
      packet.writeShort(this.getLogoBG());
      packet.write(this.getLogoBGColor());
      packet.writeShort(this.getLogo());
      packet.write(this.getLogoColor());
      packet.writeMapleAsciiString(this.getNotice());
      packet.writeInt(this.getHonorEXP());
      packet.writeInt(this.getAllianceId());
      packet.write(this.getLevel());
      packet.writeInt(this.getGP());
      packet.writeInt(0);
      packet.writeInt(this.getCurrentDateYesterday());
      packet.write(this.isAllowJoinRequest());
      packet.writeLong(PacketHelper.getTime(-2L));
      packet.writeInt(this.getConnectTimeFlag());
      packet.writeInt(this.getActivityFlag());
      packet.writeInt(this.getAgeGroupFlag());
      packet.writeShort(this.getSkills().size());

      for (GuildSkill i : this.getSkills()) {
         packet.writeInt(i.skillID);
         packet.writeShort(i.level);
         packet.writeLong(PacketHelper.getTime(i.timestamp));
         packet.writeMapleAsciiString(i.purchaser);
         packet.writeMapleAsciiString(i.activator);
      }

      if (this.getLogoColor() > 0) {
         packet.write(-1);
         packet.writeInt(0);
      } else {
         byte[] customEmblem = this.getCustomEmblem();
         packet.write(customEmblem == null ? -1 : 1);
         packet.writeInt(customEmblem == null ? 0 : customEmblem.length);
         if (customEmblem != null && customEmblem.length > 0) {
            packet.encodeBuffer(customEmblem);
         }
      }

      packet.writeInt(0);
   }

   public int getCurrentDateYesterday() {
      Calendar ocal = Calendar.getInstance();
      String years = ocal.get(1) + "";
      String months = ocal.get(2) + 1 + "";
      String days = ocal.get(5) + "";
      int month = ocal.get(2) + 1;
      int day = ocal.get(5) - 1;
      if (month < 10) {
         months = "0" + month;
      } else {
         months = month + "";
      }

      if (day < 10) {
         days = "0" + day;
      } else {
         days = day + "";
      }

      return Integer.parseInt(years + months + days);
   }

   public static class AttendanceData {
      private int attendanceCount = 0;
      private int point = 0;

      public AttendanceData(int unk1, int unk2) {
         this.setAttendanceCount(unk1);
         this.setPoint(unk2);
      }

      public void encode(PacketEncoder packet) {
         packet.writeInt(this.attendanceCount);
         packet.writeInt(this.point);
      }

      public int getAttendanceCount() {
         return this.attendanceCount;
      }

      public void setAttendanceCount(int attendanceCount) {
         this.attendanceCount = attendanceCount;
      }

      public int getPoint() {
         return this.point;
      }

      public void setPoint(int point) {
         this.point = point;
      }
   }

   public static enum BCOp {
      NONE,
      DISBAND,
      EMBELMCHANGE,
      CUSTOMEMBLEMCHANGE;
   }

   public class JoinRequester {
      private int cid;
      private int job;
      private int level;
      private String name;
      private String introduce;

      public JoinRequester(int id, int job, int level, String name, String introduce) {
         this.cid = id;
         this.job = job;
         this.level = level;
         this.name = name;
         this.introduce = introduce;
      }

      public void insert() {
         DBConnection db = new DBConnection();

         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
               "INSERT INTO `guild_request_member` (`id`, `guild_id`, `job`, `level`, `name`, `introduce`, `request_date`) VALUES (?, ?, ?, ?, ?, ?, NOW())"
            );
            ps.setInt(1, this.cid);
            ps.setInt(2, Guild.this.id);
            ps.setInt(3, this.job);
            ps.setInt(4, this.level);
            ps.setString(5, this.name);
            ps.setString(6, this.introduce);
            ps.executeUpdate();
            ps.close();
         } catch (SQLException var7) {
            System.err.println(var7);
         }
      }

      public void delete() {
         DBConnection db = new DBConnection();

         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM `guild_request_member` where id = " + this.cid);
            ps.executeUpdate();
            ps.close();
         } catch (SQLException var7) {
            System.err.println(var7);
         }
      }

      public void encode(PacketEncoder packet) {
         packet.writeInt(this.cid);
         packet.writeMapleAsciiString(this.name);
         packet.writeInt(this.job);
         packet.writeInt(this.level);
         packet.write(0);
         packet.writeMapleAsciiString(this.introduce);
      }

      public int getPlayerID() {
         return this.cid;
      }

      public String getIntroduce() {
         return this.introduce;
      }
   }

   public static class RecruitmentGuildData {
      int guildID;
      byte guildLevel;
      String guildName;
      String leaderName;
      int memberCount;
      int avgLevel;
      boolean joinRequest;
      long unkTime;
      boolean allowJointRequest;
      String notice;
      int connectTimeFlag;
      int activityFlag;
      int ageGroupFlag;
      boolean specialRecruiment;

      public RecruitmentGuildData(Guild guild, int playerID, boolean specialRecruiment) {
         this.guildID = guild.getId();
         this.guildLevel = (byte)guild.getLevel();
         this.guildName = guild.getName();
         this.leaderName = guild.getLeaderName();
         this.memberCount = guild.getMembers().size();
         this.avgLevel = guild.getAvgLevel();
         this.joinRequest = guild.isProcessRequester(playerID);
         this.unkTime = PacketHelper.getKoreanTimestamp(System.currentTimeMillis() + 600000L);
         this.allowJointRequest = guild.isAllowJoinRequest();
         this.notice = guild.getNotice();
         this.connectTimeFlag = guild.getConnectTimeFlag();
         this.activityFlag = guild.getActivityFlag();
         this.ageGroupFlag = guild.getAgeGroupFlag();
         this.specialRecruiment = specialRecruiment;
      }

      public void encode(PacketEncoder packet) {
         packet.writeInt(this.guildID);
         packet.write(this.guildLevel);
         packet.writeMapleAsciiString(this.guildName);
         packet.writeMapleAsciiString(this.leaderName);
         packet.writeShort(this.memberCount);
         packet.writeShort(this.avgLevel);
         packet.write(this.joinRequest ? 0 : 1);
         packet.writeLong(this.unkTime);
         packet.write(this.allowJointRequest);
         packet.writeMapleAsciiString(this.notice);
         packet.writeInt(this.connectTimeFlag);
         packet.writeInt(this.activityFlag);
         packet.writeInt(this.ageGroupFlag);
         packet.write(this.specialRecruiment);
      }
   }
}
