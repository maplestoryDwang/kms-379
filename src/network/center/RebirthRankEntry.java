package network.center;

import constants.JobConstants;
import network.encode.PacketEncoder;

public class RebirthRankEntry {
   private String playerName = "";
   private int playerLevel = 0;
   private int playerId = 0;
   private String jobName = "";
   private int rebirthCount = 0;
   private int superRebirthCount = 0;
   private int totalUnion = 0;

   public RebirthRankEntry(String playerName, int playerLevel, int job, int rebirthCount, int superRebirthCount, int totalUnion) {
      this.setPlayerName(playerName);
      this.setPlayerLevel(playerLevel);
      this.setRebirthCount(rebirthCount);
      this.setSuperRebirthCount(superRebirthCount);
      this.setTotalUnion(totalUnion);
      this.jobName = JobConstants.getPlayerJobs(job);
   }

   public String getPlayerName() {
      return this.playerName;
   }

   public void setPlayerName(String playerName) {
      this.playerName = playerName;
   }

   public int getPlayerLevel() {
      return this.playerLevel;
   }

   public void setPlayerLevel(int playerLevel) {
      this.playerLevel = playerLevel;
   }

   public int getRebirthCount() {
      return this.rebirthCount;
   }

   public void setRebirthCount(int rebirthCount) {
      this.rebirthCount = rebirthCount;
   }

   public int getSuperRebirthCount() {
      return this.superRebirthCount;
   }

   public void setSuperRebirthCount(int superRebirthCount) {
      this.superRebirthCount = superRebirthCount;
   }

   public int getTotalUnion() {
      return this.totalUnion;
   }

   public void setTotalUnion(int totalUnion) {
      this.totalUnion = totalUnion;
   }

   public int getPlayerId() {
      return this.playerId;
   }

   public void setPlayerId(int playerId) {
      this.playerId = playerId;
   }

   public void encode(PacketEncoder packet) {
      packet.writeMapleAsciiString(this.playerName);
      packet.writeMapleAsciiString("직업 : " + this.jobName);
      packet.writeMapleAsciiString("환생포인트 : " + this.rebirthCount);
      packet.writeMapleAsciiString("레벨 : " + this.playerLevel);
      packet.writeMapleAsciiString("유니온 : " + this.totalUnion);
      packet.writeMapleAsciiString("각성 : " + this.superRebirthCount + "회");
   }
}
