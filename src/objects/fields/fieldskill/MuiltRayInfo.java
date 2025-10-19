package objects.fields.fieldskill;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class MuiltRayInfo {
   private int skillID;
   private int X;
   private int Y;
   private int createDelayMin;
   private int createDelayMax;
   private int attackDelay;
   private int time;
   private int subTime;
   private int attackCountLimit;
   private int attackTryCountLimit;
   private int w;
   private int refMobID;
   private int refMobAttack;
   private int countMin;
   private int countMax;
   private int gapY;
   private String name;

   public MuiltRayInfo(MapleData data) {
      this.setX(MapleDataTool.getInt("x", data, 0));
      this.setY(MapleDataTool.getInt("y", data, 0));
      this.setW(MapleDataTool.getInt("w", data, 0));
      this.setRefMobID(MapleDataTool.getInt("refMobID", data, 0));
      this.setRefMobAttack(MapleDataTool.getInt("refMobAttack", data, 0));
      this.setCountMin(MapleDataTool.getInt("countMin", data, 0));
      this.setCountMax(MapleDataTool.getInt("countMax", data, 0));
      this.setGapY(MapleDataTool.getInt("gapY", data, 0));
      this.setCreateDelayMin(MapleDataTool.getInt("createDelayMin", data, 0));
      this.setCreateDelayMax(MapleDataTool.getInt("createDelayMax", data, 0));
      this.setAttackDelay(MapleDataTool.getInt("attackDelay", data, 0));
      this.setTime(MapleDataTool.getInt("time", data, 0));
      this.setSubTime(MapleDataTool.getInt("subTime", data, 0));
      this.setAttackCountLimit(MapleDataTool.getInt("attackCountLimit", data, 0));
      this.setAttackTryCountLimit(MapleDataTool.getInt("attackTryCountLimit", data, 0));
   }

   public int getX() {
      return this.X;
   }

   public void setX(int getX) {
      this.X = getX;
   }

   public int getY() {
      return this.Y;
   }

   public void setY(int getY) {
      this.Y = getY;
   }

   public int getCreateDelayMin() {
      return this.createDelayMin;
   }

   public void setCreateDelayMin(int createDelayMin) {
      this.createDelayMin = createDelayMin;
   }

   public int getCreateDelayMax() {
      return this.createDelayMax;
   }

   public void setCreateDelayMax(int createDelayMax) {
      this.createDelayMax = createDelayMax;
   }

   public int getAttackDelay() {
      return this.attackDelay;
   }

   public void setAttackDelay(int attackDelay) {
      this.attackDelay = attackDelay;
   }

   public int getTime() {
      return this.time;
   }

   public void setTime(int time) {
      this.time = time;
   }

   public int getSubTime() {
      return this.subTime;
   }

   public void setSubTime(int subTime) {
      this.subTime = subTime;
   }

   public int getAttackCountLimit() {
      return this.attackCountLimit;
   }

   public void setAttackCountLimit(int attackCountLimit) {
      this.attackCountLimit = attackCountLimit;
   }

   public int getAttackTryCountLimit() {
      return this.attackTryCountLimit;
   }

   public void setAttackTryCountLimit(int attackTryCountLimit) {
      this.attackTryCountLimit = attackTryCountLimit;
   }

   public int getW() {
      return this.w;
   }

   public void setW(int w) {
      this.w = w;
   }

   public int getRefMobID() {
      return this.refMobID;
   }

   public void setRefMobID(int refMobID) {
      this.refMobID = refMobID;
   }

   public int getRefMobAttack() {
      return this.refMobAttack;
   }

   public void setRefMobAttack(int refMobAttack) {
      this.refMobAttack = refMobAttack;
   }

   public int getCountMin() {
      return this.countMin;
   }

   public void setCountMin(int countMin) {
      this.countMin = countMin;
   }

   public int getCountMax() {
      return this.countMax;
   }

   public void setCountMax(int countMax) {
      this.countMax = countMax;
   }

   public int getGapY() {
      return this.gapY;
   }

   public void setGapY(int gapY) {
      this.gapY = gapY;
   }
}
