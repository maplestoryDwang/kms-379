package objects.fields.obstacle;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;
import network.encode.PacketEncoder;

public class ObstacleAtom {
   private int atomType;
   private int key;
   private Point startPos;
   private Point endPos;
   private int hitBoxRange;
   private int trueDamR;
   private int mobDamR;
   private int createDelay;
   private int height;
   private int vPerSec;
   private int maxP;
   private int length;
   private int angle;
   private int diseaseSkillID;
   private int diseaseSkillLevel;
   private ObstacleDiagonalInfo obstacleDiagonalInfo;
   public static AtomicInteger SN = new AtomicInteger(0);

   public ObstacleAtom() {
   }

   public ObstacleAtom(int atomType, Point startPos, Point endPos, int createDelay) {
      this.atomType = atomType;
      this.startPos = startPos;
      this.endPos = endPos;
      this.hitBoxRange = 0;
      this.trueDamR = 0;
      this.mobDamR = 0;
      this.createDelay = createDelay;
      this.height = 0;
      this.vPerSec = 0;
      this.maxP = 0;
      this.length = (int)startPos.distance(endPos) - 5;
      this.angle = calculateAngle(startPos, endPos);
   }

   public static void setInfo(ObstacleAtom atom) {
      int atomType = atom.atomType;
      switch (atomType) {
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
            atom.hitBoxRange = 25;
            atom.mobDamR = 0;
            return;
         case 6:
         case 7:
            atom.hitBoxRange = 45;
            atom.mobDamR = 0;
            return;
         case 8:
            atom.hitBoxRange = 65;
            atom.mobDamR = 0;
            return;
         case 22:
            atom.hitBoxRange = 100;
            atom.trueDamR = 100;
            atom.mobDamR = 0;
            return;
         case 23:
            atom.hitBoxRange = 60;
            atom.trueDamR = 50;
            atom.mobDamR = 0;
            return;
         case 24:
            atom.hitBoxRange = 25;
            atom.trueDamR = 30;
            atom.mobDamR = 0;
            return;
         case 25:
            atom.hitBoxRange = 100;
            atom.trueDamR = 100;
            atom.mobDamR = 0;
            return;
         case 26:
            atom.hitBoxRange = 60;
            atom.trueDamR = 50;
            atom.mobDamR = 0;
            return;
         case 27:
            atom.hitBoxRange = 25;
            atom.trueDamR = 30;
            atom.mobDamR = 0;
            return;
         default:
            switch (atomType) {
               case 48:
                  atom.hitBoxRange = 36;
                  atom.mobDamR = 0;
                  atom.vPerSec = 800;
                  atom.maxP = 1;
                  return;
               case 49:
                  atom.hitBoxRange = 51;
                  atom.mobDamR = 0;
                  atom.vPerSec = 600;
                  atom.maxP = 1;
                  return;
               case 50:
                  atom.hitBoxRange = 65;
                  atom.mobDamR = 0;
                  atom.vPerSec = 400;
                  atom.maxP = 1;
                  return;
               case 51:
                  atom.hitBoxRange = 110;
                  atom.mobDamR = 0;
                  atom.vPerSec = 300;
                  atom.maxP = 1;
                  return;
               case 52:
                  atom.hitBoxRange = 155;
                  atom.mobDamR = 0;
                  atom.vPerSec = 200;
                  atom.maxP = 1;
                  return;
               case 53:
               case 54:
               case 55:
               case 56:
               case 57:
               default:
                  break;
               case 58:
                  atom.hitBoxRange = 50;
                  atom.trueDamR = 90;
                  return;
               case 59:
                  atom.hitBoxRange = 40;
                  atom.trueDamR = 90;
                  return;
               case 60:
               case 61:
                  atom.hitBoxRange = 25;
                  atom.trueDamR = 5;
                  atom.mobDamR = 0;
                  return;
            }
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
      }
   }

   public static int calculateAngle(Point origin, Point destination) {
      int num;
      try {
         int x = destination.x - origin.x;
         int y = destination.y - origin.y;
         if (y != 0) {
            int degree = (int)(180.0 * Math.atan2(x, y) / 3.14159265358979);
            degree /= 2;
            if (degree < 0) {
               degree += 360;
            }

            num = (360 - degree) % 360;
         } else {
            num = 0;
         }
      } catch (Exception var6) {
         num = 0;
      }

      return num;
   }

   public void encode(PacketEncoder packet, boolean single) {
      packet.writeInt(this.getAtomType());
      packet.writeInt(this.getKey());
      packet.writeInt(this.getStartPos().x);
      packet.writeInt(this.getStartPos().y);
      packet.writeInt(this.getEndPos().x);
      packet.writeInt(this.getEndPos().y);
      packet.writeInt(this.getHitBoxRange());
      packet.write(0);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(this.getTrueDamR());
      packet.writeInt(this.getMobDamR());
      packet.writeInt(this.getCreateDelay());
      packet.writeInt(this.getHeight());
      packet.writeInt(this.getvPerSec());
      packet.writeInt(this.getMaxP());
      packet.writeInt(this.getLength());
      if (single) {
         packet.writeDouble(0.0);
         packet.writeInt(0);
      }

      packet.writeInt(this.getAngle());
   }

   public int getAtomType() {
      return this.atomType;
   }

   public void setAtomType(int atomType) {
      this.atomType = atomType;
   }

   public int getKey() {
      return this.key;
   }

   public void setKey(int key) {
      this.key = key;
   }

   public Point getStartPos() {
      return this.startPos;
   }

   public void setStartPos(Point startPos) {
      this.startPos = startPos;
   }

   public Point getEndPos() {
      return this.endPos;
   }

   public void setEndPos(Point endPos) {
      this.endPos = endPos;
   }

   public int getHitBoxRange() {
      return this.hitBoxRange;
   }

   public void setHitBoxRange(int hitBoxRange) {
      this.hitBoxRange = hitBoxRange;
   }

   public int getTrueDamR() {
      return this.trueDamR;
   }

   public void setTrueDamR(int trueDamR) {
      this.trueDamR = trueDamR;
   }

   public int getMobDamR() {
      return this.mobDamR;
   }

   public void setMobDamR(int mobDamR) {
      this.mobDamR = mobDamR;
   }

   public int getCreateDelay() {
      return this.createDelay;
   }

   public void setCreateDelay(int createDelay) {
      this.createDelay = createDelay;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public int getvPerSec() {
      return this.vPerSec;
   }

   public void setvPerSec(int vPerSec) {
      this.vPerSec = vPerSec;
   }

   public int getMaxP() {
      return this.maxP;
   }

   public void setMaxP(int maxP) {
      this.maxP = maxP;
   }

   public int getLength() {
      return this.length;
   }

   public void setLength(int length) {
      this.length = length;
   }

   public int getAngle() {
      return this.angle;
   }

   public void setAngle(int angle) {
      this.angle = angle;
   }

   public ObstacleDiagonalInfo getObstacleDiagonalInfo() {
      return this.obstacleDiagonalInfo;
   }

   public void setObstacleDiagonalInfo(ObstacleDiagonalInfo obstacleDiagonalInfo) {
      this.obstacleDiagonalInfo = obstacleDiagonalInfo;
   }

   public int getDiseaseSkillID() {
      return this.diseaseSkillID;
   }

   public void setDiseaseSkillID(int diseaseSkillID) {
      this.diseaseSkillID = diseaseSkillID;
   }

   public int getDiseaseSkillLevel() {
      return this.diseaseSkillLevel;
   }

   public void setDiseaseSkillLevel(int diseaseSkillLevel) {
      this.diseaseSkillLevel = diseaseSkillLevel;
   }
}
