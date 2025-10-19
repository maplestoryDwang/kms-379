package objects.context.party.boss;

import java.util.concurrent.atomic.AtomicInteger;
import network.encode.PacketEncoder;
import network.models.PacketHelper;
import objects.context.ReportLogEntry;

public class BossPartyRecruiment {
   private AtomicInteger SN = new AtomicInteger(1);
   private int id;
   private int bossType;
   private String leaderName;
   private int leaderID;
   private long endRecruimentTime;
   private BossPartyRecruimentEntry entry;
   private ReportLogEntry reportLogEntry;

   public BossPartyRecruiment() {
   }

   public BossPartyRecruiment(int bossType, String leaderName, int leaderID, BossPartyRecruimentEntry entry) {
      this.id = this.SN.getAndAdd(1);
      entry.setActiveRecruiment(true);
      this.setBossType(bossType);
      this.setLeaderName(leaderName);
      this.setLeaderID(leaderID);
      this.setEndRecruimentTime(System.currentTimeMillis() + 1800000L);
      this.setEntry(entry);
      this.setReportLogEntry(new ReportLogEntry(leaderName, entry.getTitle(), leaderID));
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getEntry() == null ? -1 : this.getBossType());
      if (this.getEntry() != null) {
         this.getEntry().encode(packet);
         packet.writeLong(PacketHelper.getKoreanTimestamp(this.getEndRecruimentTime()));
         this.getReportLogEntry().encode(packet);
      }
   }

   public int getId() {
      return this.id;
   }

   public int getBossType() {
      return this.bossType;
   }

   public BossPartyRecruimentEntry getEntry() {
      return this.entry;
   }

   public void setEntry(BossPartyRecruimentEntry entry) {
      this.entry = entry;
   }

   public void setBossType(int bossType) {
      this.bossType = bossType;
   }

   public String getLeaderName() {
      return this.leaderName;
   }

   public void setLeaderName(String leaderName) {
      this.leaderName = leaderName;
   }

   public int getLeaderID() {
      return this.leaderID;
   }

   public void setLeaderID(int leaderID) {
      this.leaderID = leaderID;
   }

   public long getEndRecruimentTime() {
      return this.endRecruimentTime;
   }

   public void setEndRecruimentTime(long endRecruimentTime) {
      this.endRecruimentTime = endRecruimentTime;
   }

   public ReportLogEntry getReportLogEntry() {
      return this.reportLogEntry;
   }

   public void setReportLogEntry(ReportLogEntry reportLogEntry) {
      this.reportLogEntry = reportLogEntry;
   }
}
