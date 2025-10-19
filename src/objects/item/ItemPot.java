package objects.item;

import java.io.Serializable;
import network.encode.PacketEncoder;
import network.models.PacketHelper;

public class ItemPot implements Serializable {
   private byte level;
   private byte lastState;
   private byte maximumIncLevel;
   private int lifeID;
   private int satiety;
   private int friendly;
   private int remainAbleFriendly;
   private int remainFriendlyTime;
   private int maximumIncSatiety;
   private long dateLastEatTime;
   private long dateLastSleepStartTime;
   private long dateLastDecSatietyTime;
   private long dateConsumedLastTime;

   public ItemPot(int lifeID) {
      this.lifeID = lifeID;
   }

   public void encode(PacketEncoder o) {
      o.writeInt(this.lifeID);
      o.write(this.level);
      o.write(this.lastState);
      o.writeInt(this.satiety);
      o.writeInt(this.friendly);
      o.writeInt(this.remainAbleFriendly);
      o.writeInt(this.remainFriendlyTime);
      o.writeLong(0L);
      o.write(this.maximumIncLevel);
      o.writeInt(this.maximumIncSatiety);
      o.writeLong(PacketHelper.getTime(this.dateLastEatTime));
      o.writeLong(PacketHelper.getTime(this.dateLastSleepStartTime));
      o.writeLong(PacketHelper.getTime(this.dateLastDecSatietyTime));
      o.writeLong(PacketHelper.getTime(this.dateConsumedLastTime));
   }

   public byte getLevel() {
      return this.level;
   }

   public void setLevel(byte level) {
      this.level = level;
   }

   public byte getLastState() {
      return this.lastState;
   }

   public void setLastState(byte lastState) {
      this.lastState = lastState;
   }

   public byte getMaximumIncLevel() {
      return this.maximumIncLevel;
   }

   public void setMaximumIncLevel(int maximumIncLevel) {
      this.maximumIncLevel = (byte)maximumIncLevel;
   }

   public int getLifeID() {
      return this.lifeID;
   }

   public void setLifeID(int lifeID) {
      this.lifeID = lifeID;
   }

   public int getSatiety() {
      return this.satiety;
   }

   public void setSatiety(int satiety) {
      this.satiety = satiety;
   }

   public int getFriendly() {
      return this.friendly;
   }

   public void setFriendly(int friendly) {
      this.friendly = friendly;
   }

   public int getRemainAbleFriendly() {
      return this.remainAbleFriendly;
   }

   public void setRemainAbleFriendly(int remainAbleFriendly) {
      this.remainAbleFriendly = remainAbleFriendly;
   }

   public int getRemainFriendlyTime() {
      return this.remainFriendlyTime;
   }

   public void setRemainFriendlyTime(int remainFriendlyTime) {
      this.remainFriendlyTime = remainFriendlyTime;
   }

   public int getMaximumIncSatiety() {
      return this.maximumIncSatiety;
   }

   public void setMaximumIncSatiety(int maximumIncSatiety) {
      this.maximumIncSatiety = maximumIncSatiety;
   }

   public long getDateLastEatTime() {
      return this.dateLastEatTime;
   }

   public void setDateLastEatTime(long dateLastEatTime) {
      this.dateLastEatTime = dateLastEatTime;
   }

   public long getDateLastSleepStartTime() {
      return this.dateLastSleepStartTime;
   }

   public void setDateLastSleepStartTime(long dateLastSleepStartTime) {
      this.dateLastSleepStartTime = dateLastSleepStartTime;
   }

   public long getDateLastDecSatietyTime() {
      return this.dateLastDecSatietyTime;
   }

   public void setDateLastDecSatietyTime(long dateLastDecSatietyTime) {
      this.dateLastDecSatietyTime = dateLastDecSatietyTime;
   }

   public long getDateConsumedLastTime() {
      return this.dateConsumedLastTime;
   }

   public void setDateConsumedLastTime(long dateConsumedLastTime) {
      this.dateConsumedLastTime = dateConsumedLastTime;
   }
}
