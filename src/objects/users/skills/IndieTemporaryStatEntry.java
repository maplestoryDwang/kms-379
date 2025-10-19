package objects.users.skills;

import network.encode.PacketEncoder;
import objects.users.stats.SecondaryStat;

public class IndieTemporaryStatEntry {
   private int skillID;
   private int skillLevel;
   private int duration;
   private int statValue;
   private long startTime;
   private int fromID;
   private boolean showBuffIcon;

   public IndieTemporaryStatEntry(int skillID, int skillLevel, int duration, int statValue, long startTime, int fromID) {
      this.skillID = skillID;
      this.setSkillLevel(skillLevel);
      this.duration = duration;
      this.statValue = statValue;
      this.startTime = startTime;
      this.setFromID(fromID);
      this.setShowBuffIcon(true);
   }

   public int getSkillID() {
      return this.skillID;
   }

   public void setSkillID(int skillID) {
      this.skillID = skillID;
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   public int getStatValue() {
      return this.statValue;
   }

   public void setStatValue(int statValue) {
      this.statValue = statValue;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public void setStartTime(long startTime) {
      this.startTime = startTime;
   }

   public int getSkillLevel() {
      return this.skillLevel;
   }

   public void setSkillLevel(int skillLevel) {
      this.skillLevel = skillLevel;
   }

   public int getFromID() {
      return this.fromID;
   }

   public void setFromID(int fromID) {
      this.fromID = fromID;
   }

   public void encode(PacketEncoder packet) {
      int skillID = this.getSkillID() != 72000048 && this.getSkillID() != 72000046 && this.getSkillID() != 72000047 ? this.getSkillID() : 0;
      if (skillID / 10000 == 202) {
         skillID *= -1;
      }

      if (SecondaryStat.isNotDisplayBuffIcon(skillID) || !this.isShowBuffIcon()) {
         skillID = 0;
      }

      packet.writeInt(skillID);
      packet.writeInt(this.getStatValue());
      packet.writeInt((int)this.getStartTime() % 1000000000);
      packet.writeInt(0);
      int duration = (int)(this.getDuration() - (System.currentTimeMillis() - this.getStartTime()));
      if (duration >= 2000000000) {
         duration = 0;
      }

      packet.writeInt(duration);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(0);
   }

   public boolean isShowBuffIcon() {
      return this.showBuffIcon;
   }

   public void setShowBuffIcon(boolean showBuffIcon) {
      this.showBuffIcon = showBuffIcon;
   }
}
