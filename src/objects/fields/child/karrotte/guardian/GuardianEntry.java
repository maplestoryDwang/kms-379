package objects.fields.child.karrotte.guardian;

import java.awt.Point;

public class GuardianEntry {
   private int index;
   private Point position;
   private byte unk;
   private int refMobID;
   private boolean assault;
   private GuardianType type;
   private int unk2;
   private int angle;
   private int attackInterval;
   private int deactiveHitCount;
   private int currentHitCount;
   private int actionSN;

   public GuardianEntry(int index, Point position, byte unk, GuardianType type, int refMobID) {
      this.setIndex(index);
      this.setPosition(position);
      this.setUnk(unk);
      this.setType(type);
      this.setRefMobID(refMobID);
      this.setCurrentHitCount(0);
   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public Point getPosition() {
      return this.position;
   }

   public void setPosition(Point position) {
      this.position = position;
   }

   public byte getUnk() {
      return this.unk;
   }

   public void setUnk(byte unk) {
      this.unk = unk;
   }

   public int getUnk2() {
      return this.unk2;
   }

   public void setUnk2(int unk2) {
      this.unk2 = unk2;
   }

   public int getAngle() {
      return this.angle;
   }

   public void setAngle(int angle) {
      this.angle = angle;
   }

   public int getAttackInterval() {
      return this.attackInterval;
   }

   public void setAttackInterval(int attackInterval) {
      this.attackInterval = attackInterval;
   }

   public int getDeactiveHitCount() {
      return this.deactiveHitCount;
   }

   public void setDeactiveHitCount(int deactiveHitCount) {
      this.deactiveHitCount = deactiveHitCount;
   }

   public GuardianType getType() {
      return this.type;
   }

   public void setType(GuardianType type) {
      this.type = type;
   }

   public boolean isAssault() {
      return this.assault;
   }

   public void setAssault(boolean assault) {
      this.assault = assault;
   }

   public int getActionSN() {
      return this.actionSN;
   }

   public void setActionSN(int actionSN) {
      this.actionSN = actionSN;
   }

   public int getRefMobID() {
      return this.refMobID;
   }

   public void setRefMobID(int refMobID) {
      this.refMobID = refMobID;
   }

   public void setCurrentHitCount(int hitCount) {
      this.currentHitCount = hitCount;
   }

   public int getCurrentHitCount() {
      return this.currentHitCount;
   }
}
