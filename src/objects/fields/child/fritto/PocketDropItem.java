package objects.fields.child.fritto;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class PocketDropItem {
   private int coolTime;
   private int itemID;
   private int maxSpeed;
   private int minSpeed;
   private int score;
   private int weight;
   private int xSpeed;

   public PocketDropItem(MapleData data) {
      this.coolTime = MapleDataTool.getInt("cooltime", data, 0);
      this.itemID = MapleDataTool.getInt("itemid", data, 0);
      this.maxSpeed = MapleDataTool.getInt("maxspeed", data, 0);
      this.minSpeed = MapleDataTool.getInt("minSpeed", data, 0);
      this.score = MapleDataTool.getInt("score", data, 0);
      this.weight = MapleDataTool.getInt("weight", data, 0);
      this.xSpeed = MapleDataTool.getInt("xspeed", data, 0);
   }

   public int getCoolTime() {
      return this.coolTime;
   }

   public void setCoolTime(int coolTime) {
      this.coolTime = coolTime;
   }

   public int getItemID() {
      return this.itemID;
   }

   public void setItemID(int itemID) {
      this.itemID = itemID;
   }

   public int getMaxSpeed() {
      return this.maxSpeed;
   }

   public void setMaxSpeed(int maxSpeed) {
      this.maxSpeed = maxSpeed;
   }

   public int getMinSpeed() {
      return this.minSpeed;
   }

   public void setMinSpeed(int minSpeed) {
      this.minSpeed = minSpeed;
   }

   public int getScore() {
      return this.score;
   }

   public void setScore(int score) {
      this.score = score;
   }

   public int getWeight() {
      return this.weight;
   }

   public void setWeight(int weight) {
      this.weight = weight;
   }

   public int getxSpeed() {
      return this.xSpeed;
   }

   public void setxSpeed(int xSpeed) {
      this.xSpeed = xSpeed;
   }
}
