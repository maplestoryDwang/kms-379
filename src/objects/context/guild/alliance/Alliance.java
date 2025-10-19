package objects.context.guild.alliance;

import database.DBConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import network.center.Center;
import network.encode.PacketEncoder;
import network.models.CWvsContext;
import objects.context.guild.Guild;
import objects.context.guild.GuildCharacter;
import objects.context.guild.GuildPacket;

public class Alliance implements Serializable {
   public static final long serialVersionUID = 24081985245L;
   public static final int CHANGE_CAPACITY_COST = 10000000;
   private final int[] guilds = new int[5];
   private int allianceid;
   private int leaderid;
   private int capacity;
   private String name;
   private String notice;
   private String[] ranks = new String[5];

   public Alliance(int id) {
      DBConnection db = new DBConnection();

      try {
         try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM alliances WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.first()) {
               rs.close();
               ps.close();
               this.allianceid = -1;
               return;
            }

            this.allianceid = id;
            this.name = rs.getString("name");
            this.capacity = rs.getInt("capacity");

            for (int i = 1; i < 6; i++) {
               this.guilds[i - 1] = rs.getInt("guild" + i);
               this.ranks[i - 1] = rs.getString("rank" + i);
            }

            this.leaderid = rs.getInt("leaderid");
            this.notice = rs.getString("notice");
            rs.close();
            ps.close();
         }
      } catch (SQLException var9) {
         System.err.println("unable to read guild information from sql");
         var9.printStackTrace();
      }
   }

   public static final Collection<Alliance> loadAll() {
      Collection<Alliance> ret = new ArrayList<>();
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT id FROM alliances");
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
            Alliance g = new Alliance(rs.getInt("id"));
            if (g.getId() > 0) {
               ret.add(g);
            }
         }

         rs.close();
         ps.close();
      } catch (SQLException var8) {
         System.err.println("unable to read guild information from sql");
         var8.printStackTrace();
      }

      return ret;
   }

   public int getGuildCount() {
      int ret = 0;

      for (int i = 0; i < this.capacity; i++) {
         if (this.guilds[i] > 0) {
            ret++;
         }
      }

      return ret;
   }

   public static final int createToDb(int leaderId, String name, int guild1, int guild2) {
      int ret = -1;
      if (name.length() > 12) {
         return ret;
      } else {
         DBConnection db = new DBConnection();

         try {
            int var9;
            try (Connection con = DBConnection.getConnection()) {
               PreparedStatement ps = con.prepareStatement("SELECT id FROM alliances WHERE name = ?");
               ps.setString(1, name);
               ResultSet rs = ps.executeQuery();
               if (!rs.first()) {
                  rs.close();
                  ps.close();
                  ps = con.prepareStatement(
                     "insert into alliances (name, guild1, guild2, leaderid, rank1, rank2, rank3, rank4, rank5) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", 1
                  );
                  ps.setString(1, name);
                  ps.setInt(2, guild1);
                  ps.setInt(3, guild2);
                  ps.setInt(4, leaderId);
                  ps.setString(5, "마스터");
                  ps.setString(6, "부마스터");
                  ps.setString(7, "연합원");
                  ps.setString(8, "연합원");
                  ps.setString(9, "연합원");
                  ps.execute();
                  rs = ps.getGeneratedKeys();
                  if (rs.next()) {
                     ret = rs.getInt(1);
                  }

                  rs.close();
                  ps.close();
                  return ret;
               }

               rs.close();
               ps.close();
               var9 = ret;
            }

            return var9;
         } catch (SQLException var12) {
            System.err.println("SQL THROW");
            var12.printStackTrace();
            return ret;
         }
      }
   }

   public final boolean deleteAlliance() {
      DBConnection db = new DBConnection();

      try {
         try (Connection con = DBConnection.getConnection()) {
            for (int i = 0; i < this.getGuildCount(); i++) {
               PreparedStatement ps = con.prepareStatement("UPDATE characters SET alliancerank = 5 WHERE guildid = ?");
               ps.setInt(1, this.guilds[i]);
               ps.execute();
               ps.close();
            }

            PreparedStatement ps = con.prepareStatement("delete from alliances where id = ?");
            ps.setInt(1, this.allianceid);
            ps.execute();
            ps.close();
         }

         return true;
      } catch (SQLException var7) {
         System.err.println("SQL THROW" + var7);
         return false;
      }
   }

   public final void broadcast(byte[] packet) {
      this.broadcast(packet, -1, Alliance.GAOp.NONE, false);
   }

   public final void broadcast(byte[] packet, int exception) {
      this.broadcast(packet, exception, Alliance.GAOp.NONE, false);
   }

   public final void broadcast(byte[] packet, int exceptionId, Alliance.GAOp op, boolean expelled) {
      if (op == Alliance.GAOp.DISBAND) {
         Center.Alliance.withdrawGuildInAlliance(exceptionId, expelled, this.allianceid);
      } else if (op == Alliance.GAOp.NEWGUILD) {
         Center.Alliance.JoinGuildInAlliance(exceptionId, this.allianceid);
      } else {
         Center.Alliance.sendGuild(packet, exceptionId, this.allianceid);
      }
   }

   public final boolean disband() {
      boolean ret = this.deleteAlliance();
      if (ret) {
         this.broadcast(null, -1, Alliance.GAOp.DISBAND, false);
      }

      return ret;
   }

   public final void saveToDb() {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement(
            "UPDATE alliances set guild1 = ?, guild2 = ?, guild3 = ?, guild4 = ?, guild5 = ?, rank1 = ?, rank2 = ?, rank3 = ?, rank4 = ?, rank5 = ?, capacity = ?, leaderid = ?, notice = ? where id = ?"
         );

         for (int i = 0; i < 5; i++) {
            ps.setInt(i + 1, this.guilds[i] < 0 ? 0 : this.guilds[i]);
            ps.setString(i + 6, this.ranks[i]);
         }

         ps.setInt(11, this.capacity);
         ps.setInt(12, this.leaderid);
         ps.setString(13, this.notice);
         ps.setInt(14, this.allianceid);
         ps.executeUpdate();
         ps.close();
      } catch (SQLException var7) {
         System.err.println("SQL THROW");
         var7.printStackTrace();
      }
   }

   public void setRank(String[] ranks) {
      this.ranks = ranks;
      PacketEncoder p = new PacketEncoder();
      GuildPacket.ChangeAllianceRankRole role = new GuildPacket.ChangeAllianceRankRole(this.getId(), 0, ranks);
      role.encode(p);
      this.broadcast(p.getPacket());
      this.saveToDb();
   }

   public String getRank(int rank) {
      return this.ranks[rank - 1];
   }

   public String[] getRanks() {
      return this.ranks;
   }

   public String getNotice() {
      return this.notice;
   }

   public void setNotice(String newNotice) {
      this.notice = newNotice;
      this.broadcast(CWvsContext.AlliancePacket.changeAllianceNotice(this.allianceid, newNotice));
      this.saveToDb();
   }

   public int getGuildId(int i) {
      return this.guilds[i];
   }

   public int getId() {
      return this.allianceid;
   }

   public String getName() {
      return this.name;
   }

   public int getCapacity() {
      return this.capacity;
   }

   public boolean setCapacity() {
      if (this.capacity >= 5) {
         return false;
      } else {
         this.capacity++;
         this.broadcast(CWvsContext.AlliancePacket.getAllianceUpdate(this));
         this.saveToDb();
         return true;
      }
   }

   public boolean addGuild(int guildid) {
      if (this.getGuildCount() >= this.getCapacity()) {
         return false;
      } else {
         this.guilds[this.getGuildCount()] = guildid;
         this.saveToDb();
         this.broadcast(null, guildid, Alliance.GAOp.NEWGUILD, false);
         return true;
      }
   }

   public boolean removeGuild(int guildid, boolean expelled) {
      return this.removeGuild(guildid, expelled, false);
   }

   public void removeGuild_(int guildID) {
      for (int i = 0; i < this.getGuildCount(); i++) {
         if (this.guilds[i] == guildID) {
            if (i > 0 && i != this.getGuildCount() - 1) {
               for (int x = i + 1; x < this.getGuildCount(); x++) {
                  if (this.guilds[x] > 0) {
                     this.guilds[x - 1] = this.guilds[x];
                     if (x == this.getGuildCount() - 1) {
                        this.guilds[x] = -1;
                     }
                  }
               }
            } else {
               this.guilds[i] = -1;
            }
         }
      }
   }

   public boolean removeGuild(int guildid, boolean expelled, boolean isNull) {
      for (int i = 0; i < this.getGuildCount(); i++) {
         if (this.guilds[i] == guildid) {
            if (i == 0) {
               return this.disband();
            }

            if (!isNull) {
               this.broadcast(null, guildid, Alliance.GAOp.DISBAND, expelled);
            }

            if (i > 0 && i != this.getGuildCount() - 1) {
               for (int x = i + 1; x < this.getGuildCount(); x++) {
                  if (this.guilds[x] > 0) {
                     this.guilds[x - 1] = this.guilds[x];
                     if (x == this.getGuildCount() - 1) {
                        this.guilds[x] = -1;
                     }
                  }
               }
            } else {
               this.guilds[i] = -1;
            }

            if (i > 0) {
               this.saveToDb();
               return true;
            }
         }
      }

      return false;
   }

   public int getLeaderId() {
      return this.leaderid;
   }

   public boolean setLeaderId(int c) {
      return this.setLeaderId(c, false);
   }

   public boolean setLeaderId(int c, boolean sameGuild) {
      if (this.leaderid == c) {
         return false;
      } else {
         if (sameGuild) {
            Guild g_ = Center.Guild.getGuild(this.guilds[0]);
            g_.changeARank(c, 1);
            this.leaderid = c;
         } else {
            int g = -1;
            String leaderName = null;

            for (int i = 0; i < this.getGuildCount(); i++) {
               Guild g_ = Center.Guild.getGuild(this.guilds[i]);
               if (g_ != null) {
                  GuildCharacter newLead = g_.getMGC(c);
                  GuildCharacter oldLead = g_.getMGC(this.leaderid);
                  if (newLead != null && oldLead != null && !sameGuild) {
                     return false;
                  }

                  if (newLead != null && newLead.getGuildRank() == 1 && newLead.getAllianceRank() == 2) {
                     g_.changeARank(c, 1);
                     g = i;
                     leaderName = newLead.getName();
                  }

                  if (oldLead != null && oldLead.getGuildRank() == 1 && oldLead.getAllianceRank() == 1) {
                     g_.changeARank(this.leaderid, 2);
                  }
               }
            }

            if (g == -1) {
               return false;
            }

            int oldGuild = this.guilds[g];
            this.guilds[g] = this.guilds[0];
            this.guilds[0] = oldGuild;
            this.leaderid = c;
         }

         PacketEncoder packet = new PacketEncoder();
         GuildPacket.ChangeAllianceLeader ca = new GuildPacket.ChangeAllianceLeader(this);
         ca.encode(packet);
         this.broadcast(packet.getPacket());
         this.saveToDb();
         return true;
      }
   }

   public boolean changeAllianceRank(int cid, int change) {
      if (this.leaderid != cid && change >= 0 && change <= 1) {
         for (int i = 0; i < this.getGuildCount(); i++) {
            Guild g_ = Center.Guild.getGuild(this.guilds[i]);
            if (g_ != null) {
               GuildCharacter chr = g_.getMGC(cid);
               if (chr != null && chr.getAllianceRank() > 2) {
                  if ((change != 0 || chr.getAllianceRank() < 5) && (change != 1 || chr.getAllianceRank() > 3)) {
                     g_.changeARank(cid, chr.getAllianceRank() + (change == 0 ? 1 : -1));
                     return true;
                  }

                  return false;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getId());
      packet.writeMapleAsciiString(this.getName());

      for (int i = 1; i <= 5; i++) {
         packet.writeMapleAsciiString(this.getRank(i));
      }

      packet.writeInt(0);
      int guildCount = this.getGuildCount();
      Guild[] guilds = new Guild[guildCount];
      packet.write(guildCount);

      for (int i = 0; i < guildCount; i++) {
         guilds[i] = Center.Guild.getGuild(this.getGuildId(i));
         if (guilds[i] == null) {
            packet = new PacketEncoder();
            packet.encodeBuffer(CWvsContext.enableActions(null));
            return;
         }
      }

      for (Guild gg : guilds) {
         gg.encodeGuildInformation(packet);
      }
   }

   private static enum GAOp {
      NONE,
      DISBAND,
      NEWGUILD;
   }
}
