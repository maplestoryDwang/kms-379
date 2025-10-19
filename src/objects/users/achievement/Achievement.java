package objects.users.achievement;

import database.loader.CharacterSaveFlag2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import network.encode.PacketEncoder;
import network.models.PacketHelper;
import objects.users.MapleCharacter;
import objects.users.achievement.caching.AchievementData;

public class Achievement {
   private int accountID = 0;
   private int playerID = 0;
   private int lastWeekScore = 0;
   private int lastWeekRank = 0;
   private int lastWeekDeltaRank = 0;
   private List<AchievementEntry> achievements = new ArrayList<>();
   private List<AchievementInsigniaEntry> achievementInsignias = new ArrayList<>();

   public Achievement(
      int accountID,
      int playerID,
      int lastWeekScore,
      int lastWeekRank,
      int lastWeekDeltaRank,
      List<AchievementEntry> achievements,
      List<AchievementInsigniaEntry> achievementInsignias
   ) {
      this.setAccountID(accountID);
      this.setPlayerID(playerID);
      this.setLastWeekScore(lastWeekScore);
      this.setLastWeekRank(lastWeekRank);
      this.setLastWeekDeltaRank(lastWeekDeltaRank);
      this.setAchievements(achievements);
      this.setAchievementInsignias(achievementInsignias);
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getAccountID());
      packet.writeInt(this.getPlayerID());
      packet.writeInt(this.getLastWeekScore());
      packet.writeInt(this.getLastWeekRank());
      packet.writeInt(this.getLastWeekDeltaRank());
      packet.writeLong(PacketHelper.getKoreanTimestamp(System.currentTimeMillis()));
      packet.writeInt(this.getAchievements().size());
      new ArrayList<>(this.getAchievements()).forEach(a -> a.encode(packet));
      packet.writeInt(this.getAchievementInsignias().size());
      new ArrayList<>(this.getAchievementInsignias()).forEach(a -> a.encode(packet));
   }

   public boolean checkCompleteAchievement(int achievementID) {
      if (this.getAchievements().isEmpty()) {
         return false;
      } else {
         for (AchievementEntry entry : new ArrayList<>(this.getAchievements())) {
            if (entry != null
               && entry.getAchievementID() == achievementID
               && entry.getMission() == -1
               && entry.getStatus() == AchievementMissionStatus.Complete) {
               return true;
            }
         }

         return false;
      }
   }

   public int getTotalScore() {
      int total = 0;

      for (AchievementEntry entry : new ArrayList<>(this.getAchievements())) {
         if (entry.getMission() == -1 && entry.getStatus() == AchievementMissionStatus.Complete) {
            AchievementData data = null;
            if ((data = AchievementFactory.getAchievementData(entry.getAchievementID())) != null) {
               total += data.getInfo().getScore();
            }
         }
      }

      return total;
   }

   public int getDataFromSubMission(String subMission) {
      int ret = 0;
      if (subMission != null && !subMission.isEmpty()) {
         String[] args = subMission.split("=");
         if (args.length > 1) {
            ret = Integer.parseInt(subMission.split("=")[1]);
         }
      }

      return ret;
   }

   public void removeAllAchievement(MapleCharacter player, int achievementID) {
      for (AchievementEntry entry : new ArrayList<>(this.getAchievements())) {
         if (entry.getAchievementID() == achievementID) {
            this.getAchievements().remove(entry);
         }
      }

      player.setSaveFlag2(player.getSaveFlag2() | CharacterSaveFlag2.ACHIEVEMENT.getFlag());
   }

   public void updateAchievementSingle(MapleCharacter player, AchievementEntry entry) {
      this.updateAchievement(player, Collections.singletonList(entry));
   }

   public void updateAchievement(MapleCharacter player, List<AchievementEntry> entries) {
   }

   public int getAccountID() {
      return this.accountID;
   }

   public void setAccountID(int accountID) {
      this.accountID = accountID;
   }

   public int getPlayerID() {
      return this.playerID;
   }

   public void setPlayerID(int playerID) {
      this.playerID = playerID;
   }

   public int getLastWeekScore() {
      return this.lastWeekScore;
   }

   public void setLastWeekScore(int lastWeekScore) {
      this.lastWeekScore = lastWeekScore;
   }

   public int getLastWeekRank() {
      return this.lastWeekRank;
   }

   public void setLastWeekRank(int lastWeekRank) {
      this.lastWeekRank = lastWeekRank;
   }

   public int getLastWeekDeltaRank() {
      return this.lastWeekDeltaRank;
   }

   public void setLastWeekDeltaRank(int lastWeekDeltaRank) {
      this.lastWeekDeltaRank = lastWeekDeltaRank;
   }

   public List<AchievementEntry> getAchievements() {
      return this.achievements;
   }

   public void setAchievements(List<AchievementEntry> achievements) {
      this.achievements = achievements;
   }

   public List<AchievementInsigniaEntry> getAchievementInsignias() {
      return this.achievementInsignias;
   }

   public void setAchievementInsignias(List<AchievementInsigniaEntry> achievementInsignias) {
      this.achievementInsignias = achievementInsignias;
   }
}
