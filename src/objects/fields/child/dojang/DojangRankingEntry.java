package objects.fields.child.dojang;

import java.sql.ResultSet;
import java.sql.SQLException;
import network.encode.PacketEncoder;
import objects.users.MapleCharacter;
import objects.users.looks.AvatarLook;

public class DojangRankingEntry {
   private int type = 0;
   private int rank = 0;
   private int job = 0;
   private int level = 1;
   private int point = 1099;
   private int accountID = 0;
   private String name = "Extreme";
   private byte[] packedAvatarLook = null;

   public DojangRankingEntry() {
   }

   public DojangRankingEntry(int type, int rank, int job, int level, int point, String name, byte[] packedAvatarLook, int accountID) {
      this.type = type;
      this.rank = rank;
      this.job = job;
      this.level = level;
      this.point = point;
      this.name = name;
      this.packedAvatarLook = packedAvatarLook;
      this.accountID = accountID;
   }

   public void loadFromDB(ResultSet rs) {
      try {
         this.setType(rs.getInt("type"));
         this.setRank(rs.getInt("rank"));
         this.setJob(rs.getInt("job"));
         this.setLevel(rs.getInt("level"));
         this.setPoint(rs.getInt("point"));
         this.setName(rs.getString("name"));
         this.setPackedAvatarLook(rs.getBytes("packed_avatar_look"));
      } catch (SQLException var3) {
         var3.printStackTrace();
      }
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getJob());
      packet.writeInt(this.getLevel());
      packet.writeInt(this.getPoint());
      packet.writeInt(this.getRank());
      packet.writeMapleAsciiString(this.getName());
      if (this.getRank() <= 3) {
         if (this.packedAvatarLook == null) {
            packet.write(0);
         } else {
            packet.write(1);
            packet.encodeBuffer(this.packedAvatarLook);
         }
      } else {
         packet.write(0);
      }
   }

   public void encodeTest(PacketEncoder packet, MapleCharacter chr) {
      packet.writeInt(this.getJob());
      packet.writeInt(this.getLevel());
      packet.writeInt(this.getPoint());
      packet.writeInt(this.getRank());
      packet.writeMapleAsciiString(this.getName());
      if (this.getRank() <= 3) {
         if (this.packedAvatarLook == null) {
            packet.write(0);
         } else {
            packet.write(1);
            MapleCharacter temp = chr.cloneLooks();
            AvatarLook avatar = new AvatarLook(temp, false, false);
            byte[] packedAvatarLook = avatar.packedTo();
            packet.encodeBuffer(packedAvatarLook);
         }
      } else {
         packet.write(0);
      }
   }

   public int getRank() {
      return this.rank;
   }

   public void setRank(int rank) {
      this.rank = rank;
   }

   public int getJob() {
      return this.job;
   }

   public void setJob(int job) {
      this.job = job;
   }

   public int getLevel() {
      return this.level;
   }

   public void setLevel(int level) {
      this.level = level;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public byte[] getPackedAvatarLook() {
      return this.packedAvatarLook;
   }

   public void setPackedAvatarLook(byte[] packedAvatarLook) {
      this.packedAvatarLook = packedAvatarLook;
   }

   public int getPoint() {
      return this.point;
   }

   public void setPoint(int point) {
      this.point = point;
   }

   public int getType() {
      return this.type;
   }

   public void setType(int type) {
      this.type = type;
   }

   public int getAccountID() {
      return this.accountID;
   }

   public void setAccountID(int accountID) {
      this.accountID = accountID;
   }
}
