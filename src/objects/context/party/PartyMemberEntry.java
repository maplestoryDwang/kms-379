package objects.context.party;

import java.awt.Point;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import network.encode.PacketEncoder;
import network.models.PacketHelper;
import objects.fields.child.dojang.DojangMyRanking;
import objects.fields.child.dojang.DojangRanking;
import objects.fields.gameobject.TownPortal;
import objects.users.MapleCharacter;
import objects.users.looks.AvatarLook;
import objects.utils.Pair;

public class PartyMemberEntry implements Serializable {
   private static final long serialVersionUID = 6215463252132450750L;
   private String name;
   private int id;
   private int level;
   private int channel;
   private int jobID;
   private int fieldID;
   private int doorTown = 999999999;
   private int doorTarget = 999999999;
   private int doorSkill = 0;
   private Point doorPosition = new Point(0, 0);
   private boolean online;
   private int arcaneForce;
   private int athenticForce;
   private int dojangRank;
   private long dojangRankTime;
   private int totalUnion;
   private byte[] packedAvatar;
   private int bossTier;
   private boolean skipIntro;

   public PartyMemberEntry(MapleCharacter player) {
      this.name = player.getName();
      this.level = player.getLevel();
      this.channel = player.getClient().getChannel();
      this.id = player.getId();
      this.jobID = player.getJob();
      this.fieldID = player.getMapId();
      this.online = true;
      this.bossTier = player.getBossTier();
      this.skipIntro = player.getIsSkipIntro();
      AvatarLook avatar = new AvatarLook(player, false, false);
      this.setPackedAvatar(avatar.packedTo());
      this.arcaneForce = player.getTotalArcane();
      this.athenticForce = player.getTotalAthentic();
      DojangMyRanking rank = DojangRanking.getThisWeekMyRank(2, player.getName());
      Pair<Integer, Integer> pair = DojangRanking.getLastTryDojang(2, player.getName());
      if (rank == null) {
         rank = DojangRanking.getLastWeekMyRank(2, player.getName());
      }

      if (rank != null && pair != null) {
         this.dojangRank = rank.getPoint() / 1000;
         Calendar cal = Calendar.getInstance();
         cal.set(1, pair.left);
         cal.set(3, pair.right);
         this.dojangRankTime = PacketHelper.getKoreanTimestamp(cal.getTime().getTime());
      } else {
         this.dojangRank = 0;
         this.dojangRankTime = PacketHelper.getTime(-2L);
      }

      this.totalUnion = player.getUnionLevel();
      List<TownPortal> doors = player.getDoors();
      if (doors.size() > 0) {
         TownPortal door = doors.get(0);
         this.doorTown = door.getTown().getId();
         this.doorTarget = door.getTarget().getId();
         this.doorSkill = door.getSkill();
         this.doorPosition = door.getTargetPosition();
      } else {
         this.doorPosition = player.getPosition();
      }
   }

   public PartyMemberEntry() {
      this.name = "";
   }

   public int getLevel() {
      return this.level;
   }

   public int getChannel() {
      return this.channel;
   }

   public void setChannel(int channel) {
      this.channel = channel;
   }

   public boolean isOnline() {
      return this.online;
   }

   public void setOnline(boolean online) {
      this.online = online;
   }

   public int getFieldID() {
      return this.fieldID;
   }

   public String getName() {
      return this.name;
   }

   public int getId() {
      return this.id;
   }

   public int getJobId() {
      return this.jobID;
   }

   public int getDoorTown() {
      return this.doorTown;
   }

   public int getDoorTarget() {
      return this.doorTarget;
   }

   public int getDoorSkill() {
      return this.doorSkill;
   }

   public Point getDoorPosition() {
      return this.doorPosition;
   }

   public int getBossTier() {
      return this.bossTier;
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      return 31 * result + (this.name == null ? 0 : this.name.hashCode());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         PartyMemberEntry other = (PartyMemberEntry)obj;
         if (this.name == null) {
            if (other.name != null) {
               return false;
            }
         } else if (!this.name.equals(other.name)) {
            return false;
         }

         return true;
      }
   }

   public int getArcaneForce() {
      return this.arcaneForce;
   }

   public void setArcaneForce(int arcaneForce) {
      this.arcaneForce = arcaneForce;
   }

   public int getAthenticForce() {
      return this.athenticForce;
   }

   public void setAthenticForce(int athenticForce) {
      this.athenticForce = athenticForce;
   }

   public int getDojangRank() {
      return this.dojangRank;
   }

   public void setDojangRank(int dojangRank) {
      this.dojangRank = dojangRank;
   }

   public long getDojangRankTime() {
      return this.dojangRankTime;
   }

   public void setDojangRankTime(long dojangRankTime) {
      this.dojangRankTime = dojangRankTime;
   }

   public int getTotalUnion() {
      return this.totalUnion;
   }

   public void setTotalUnion(int totalUnion) {
      this.totalUnion = totalUnion;
   }

   public byte[] getPackedAvatar() {
      return this.packedAvatar;
   }

   public void setPackedAvatar(byte[] packedAvatar) {
      this.packedAvatar = packedAvatar;
   }

   public boolean isSkipIntro() {
      return this.skipIntro;
   }

   public void setSkipIntro(boolean skipIntro) {
      this.skipIntro = skipIntro;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getId());
      packet.writeMapleAsciiString(this.getName());
      packet.writeInt(this.getJobId());
      packet.writeInt(this.isOnline() ? 1 : 0);
      packet.writeInt(this.getLevel());
      packet.writeInt(this.isOnline() ? this.getChannel() - 1 : -2);
      packet.write(0);
      packet.write(0);
      packet.writeInt(this.getArcaneForce());
      packet.writeInt(this.getAthenticForce());
      packet.writeInt(this.getDojangRank());
      packet.writeLong(this.getDojangRankTime());
      packet.writeInt(this.getTotalUnion());
      packet.encodeBuffer(this.getPackedAvatar());
   }
}
