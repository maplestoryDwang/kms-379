package objects.context.guild;

import java.io.Serializable;
import network.center.Center;
import network.encode.PacketEncoder;
import objects.users.MapleCharacter;

public class GuildCharacter implements Serializable {
   public static final long serialVersionUID = 2058609046116597760L;
   private byte channel = -1;
   private byte guildrank;
   private byte allianceRank;
   private short level;
   private int id;
   private int jobid;
   private int guildid;
   private int guildContribution;
   private int todayContribution;
   private long createDate;
   private long lastLoggedinDate;
   private long todayLoggedinDate;
   private boolean online;
   private String name;
   private int lastAttendanceDate;

   public GuildCharacter(MapleCharacter c) {
      this.name = c.getName();
      this.level = c.getLevel();
      this.id = c.getId();
      this.channel = (byte)c.getClient().getChannel();
      this.jobid = c.getJob();
      this.guildrank = c.getGuildRank();
      this.guildid = c.getGuildId();
      this.guildContribution = c.getGuildContribution();
      this.allianceRank = c.getAllianceRank();
      this.lastAttendanceDate = c.getLastAttendacneDate();
      this.createDate = c.getCreateDate();
      this.todayContribution = c.getTodayContribution();
      this.lastLoggedinDate = c.getLastLoggedinDate();
      this.todayLoggedinDate = c.getTodayLoggedinDate();
      this.online = true;
   }

   public GuildCharacter(
      int id,
      short lv,
      String name,
      byte channel,
      int job,
      byte rank,
      int guildContribution,
      byte allianceRank,
      int gid,
      long createDate,
      int todayContribution,
      long lastLoggedinDate,
      long todayLoggedinDate,
      boolean on
   ) {
      this.level = lv;
      this.id = id;
      this.name = name;
      if (on) {
         this.channel = channel;
      }

      this.jobid = job;
      this.online = on;
      this.guildrank = rank;
      this.allianceRank = allianceRank;
      this.guildContribution = guildContribution;
      this.createDate = createDate;
      this.todayContribution = todayContribution;
      this.lastLoggedinDate = lastLoggedinDate;
      this.todayLoggedinDate = todayLoggedinDate;
      this.guildid = gid;
   }

   public int getLevel() {
      return this.level;
   }

   public void setLevel(short l) {
      this.level = l;
   }

   public int getId() {
      return this.id;
   }

   public void setChannel(byte ch) {
      this.channel = ch;
   }

   public int getChannel() {
      return this.channel;
   }

   public int getJobId() {
      return this.jobid;
   }

   public void setJobId(int job) {
      this.jobid = job;
   }

   public int getGuildId() {
      return this.guildid;
   }

   public void setGuildId(int gid) {
      this.guildid = gid;
   }

   public void setGuildRank(byte rank) {
      this.guildrank = rank;
   }

   public byte getGuildRank() {
      return this.guildrank;
   }

   public void addGuildContribution(int c) {
      this.guildContribution += c;
      Guild guild = Center.Guild.getGuild(this.getGuildId());
      if (guild != null) {
         guild.setHonorEXP(guild.getHonorEXP() + c);
         guild.gainGP((int)(c * 0.3));
         guild.setLevel(guild.calculateLevel());
         Center.Guild.setGuildAndRank(this.getId(), this.getGuildId(), this.getGuildRank(), this.getGuildContribution(), this.getAllianceRank());
         PacketEncoder packet = new PacketEncoder();
         GuildPacket.AddIGPLog log = new GuildPacket.AddIGPLog(this.getGuildId(), this.getId(), this.guildContribution, c);
         log.encode(packet);
         guild.broadcast(packet.getPacket());
         packet = new PacketEncoder();
         GuildPacket.UpdateGuildPoint gp = new GuildPacket.UpdateGuildPoint(guild);
         gp.encode(packet);
         guild.broadcast(packet.getPacket());
      }
   }

   public void setGuildContribution(int c) {
      this.guildContribution = c;
   }

   public int getGuildContribution() {
      return this.guildContribution;
   }

   public boolean isOnline() {
      return this.online;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setOnline(boolean f) {
      if (f) {
         long curTime = System.currentTimeMillis();
         if (curTime > this.lastLoggedinDate) {
            this.lastLoggedinDate = curTime;
         }
      }

      this.online = f;
   }

   public void setAllianceRank(byte rank) {
      this.allianceRank = rank;
   }

   public byte getAllianceRank() {
      return this.allianceRank;
   }

   public int getLastAttendanceDate() {
      return this.lastAttendanceDate;
   }

   public void setLastAttendanceDate(int lastAttendanceDate) {
      this.lastAttendanceDate = lastAttendanceDate;
   }

   public long getCreateDate() {
      return this.createDate;
   }

   public int getTodayContribution() {
      return this.todayContribution;
   }

   public long getLastLoggedInDate() {
      return this.lastLoggedinDate;
   }

   public long getTodayLoggedInDate() {
      return this.todayLoggedinDate;
   }
}
