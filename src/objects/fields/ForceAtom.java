package objects.fields;

import constants.ForceAtomConstants;
import io.netty.util.internal.ThreadLocalRandom;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import network.encode.PacketEncoder;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class ForceAtom {
   private boolean byMob = false;
   private boolean toMob = true;
   private int ownerID;
   private int targetMobObjectID;
   private ForceAtom.AtomType forceAtomType;
   private List<Integer> targetMobs = new LinkedList<>();
   private int skillID;
   private int targetCount;
   private ForceAtom.AtomInfo info;
   private Point position;
   private int attackCount;
   public static AtomicInteger SN = new AtomicInteger(1);

   public int getKey() {
      return this.info.getKey();
   }

   public ForceAtom.AtomInfo getInfo() {
      return this.info;
   }

   public void setByMob(boolean byMob) {
      this.byMob = byMob;
   }

   public void setToMob(boolean toMob) {
      this.toMob = toMob;
   }

   public void setTargetMobObjectID(int targetMobObjectID) {
      this.targetMobObjectID = targetMobObjectID;
   }

   public void setTargetMobs(List<Integer> targetMobs) {
      this.targetMobs = targetMobs;
   }

   public ForceAtom(ForceAtom.AtomInfo info, int ownerID, int targetMobObjectID, ForceAtom.AtomType forceAtomType, int skillID) {
      this.info = info;
      this.ownerID = ownerID;
      this.setForceAtomType(forceAtomType);
      this.skillID = skillID;
   }

   public ForceAtom(ForceAtom.AtomInfo info, int ownerID, int targetMobObjectID, ForceAtom.AtomType forceAtomType, int skillID, int atomCount) {
      this(info, ownerID, targetMobObjectID, forceAtomType, skillID);
      this.targetCount = atomCount;
   }

   public ForceAtom(
      ForceAtom.AtomInfo info,
      int skillID,
      int ownerID,
      boolean byMob,
      boolean toMob,
      int targetMobObjectID,
      ForceAtom.AtomType forceAtomType,
      List<Integer> targetMobs,
      int atomCount
   ) {
      this(info, skillID, ownerID, byMob, toMob, targetMobObjectID, forceAtomType, targetMobs, atomCount, null);
   }

   public ForceAtom(
      ForceAtom.AtomInfo info,
      int skillID,
      int ownerID,
      boolean byMob,
      boolean toMob,
      int targetMobObjectID,
      ForceAtom.AtomType forceAtomType,
      List<Integer> targetMobs,
      int atomCount,
      Point point
   ) {
      this.info = info;
      this.ownerID = ownerID;
      this.byMob = byMob;
      this.toMob = toMob;
      this.targetMobObjectID = targetMobObjectID;
      this.setForceAtomType(forceAtomType);
      this.targetMobs = targetMobs;
      this.skillID = skillID;
      this.targetCount = atomCount;
      this.position = point;
   }

   public void encode(PacketEncoder packet) {
      packet.write(this.byMob ? 1 : 0);
      if (this.byMob) {
         packet.writeInt(this.ownerID);
      }

      packet.writeInt(this.targetMobObjectID);
      packet.writeInt(this.getForceAtomType().getType());
      boolean skipSkillID = false;
      if (this.getForceAtomType().getType() >= 0
         && this.getForceAtomType().getType() != 36
         && this.getForceAtomType().getType() != 37
         && this.getForceAtomType().getType() != 89
         && this.getForceAtomType().getType() != 91) {
         switch (this.getForceAtomType().getType()) {
            case 0:
            case 9:
            case 14:
            case 29:
            case 42:
               skipSkillID = true;
         }

         if (!skipSkillID) {
            packet.write(this.toMob ? 1 : 0);
            switch (this.getForceAtomType().getType()) {
               case 2:
               case 3:
               case 6:
               case 7:
               case 11:
               case 12:
               case 13:
               case 17:
               case 19:
               case 20:
               case 23:
               case 24:
               case 25:
               case 27:
               case 28:
               case 30:
               case 32:
               case 34:
               case 38:
               case 39:
               case 40:
               case 41:
               case 47:
               case 48:
               case 49:
               case 52:
               case 53:
               case 54:
               case 55:
               case 56:
               case 57:
               case 58:
               case 60:
               case 64:
               case 65:
               case 67:
               case 68:
               case 72:
               case 73:
               case 75:
               case 76:
               case 77:
               case 84:
               case 86:
               case 87:
               case 90:
               case 92:
               case 93:
               case 95:
                  packet.writeInt(this.targetMobs.size());

                  for (int objectID : this.targetMobs) {
                     packet.writeInt(objectID);
                  }
                  break;
               case 4:
               case 5:
               case 8:
               case 9:
               case 10:
               case 14:
               case 15:
               case 16:
               case 18:
               case 21:
               case 22:
               case 26:
               case 29:
               case 31:
               case 33:
               case 35:
               case 36:
               case 37:
               case 42:
               case 43:
               case 44:
               case 45:
               case 46:
               case 50:
               case 51:
               case 59:
               case 61:
               case 62:
               case 63:
               case 66:
               case 69:
               case 70:
               case 71:
               case 74:
               case 78:
               case 79:
               case 80:
               case 81:
               case 82:
               case 83:
               case 85:
               case 88:
               case 89:
               case 91:
               case 94:
               default:
                  if (this.targetMobs.isEmpty()) {
                     packet.writeInt(0);
                  } else {
                     packet.writeInt(this.targetMobs.get(0));
                  }
            }
         }
      }

      if (!skipSkillID || this.getForceAtomType().getType() == 29 || this.getForceAtomType().getType() == 42) {
         packet.writeInt(this.skillID);
         if (this.skillID == 400021069) {
            packet.writeInt(this.info.grimReaperIncGuage);
         }
      }

      for (int next = 0; next < this.targetCount; next++) {
         switch (this.getForceAtomType()) {
            case BULLET:
               this.info.initBullet();
               break;
            case MEGIDDO_FLAME:
            case ATOM_REGEN:
            case DAShieldChasing2_1:
               if (this.skillID == 400021045) {
                  this.info.initFlameDischarge();
                  if (this.getForceAtomType() == ForceAtom.AtomType.ATOM_REGEN) {
                     this.info.startDelay = 0;
                     this.info.firstImpact = Randomizer.rand(41, 43);
                     this.info.secondImpact = Randomizer.rand(3, 4);
                  }
               } else if (this.skillID == 31221014 && this.info.startDelay != 50) {
                  this.info.initShieldChasingNoKey();
               } else if (this.skillID == 31241001 && this.info.startDelay != 50) {
                  this.info.initShieldChasingNoKeyVI2();
               }
               break;
            case PIN_POINT_ROCKET:
               this.info.initPinPointRocket();
               break;
            case TRI_FLING:
               this.info.initTriFling(this.info.inc, next);
               break;
            case STORM_BRINGER:
               this.info.initStormBringer();
               break;
            case HOMING_MISILE:
               this.info.initHomingMisile();
               break;
            case TELE_KINESIS:
               this.info.initTeleKinesis();
               break;
            case GUIDED_ARROW:
               this.info.initGuidedArrow();
               break;
            case IDLE_WORM:
               this.info.initIdleWorm();
               break;
            case IMPENDING_DEAD:
               this.info.initImpendingDead();
               break;
            case CARDINAL_DISCHARGE:
               if (this.skillID == 400031054) {
                  this.info.initSilhouetteMirage(next, this.info.startPos);
               } else {
                  this.info.initCardinalDischarge(this.info.inc);
               }
               break;
            case EDITIONAL_DISCHARGE:
               this.info.initAdditionalDischarge(new Point(this.info.forcedTargetX, this.info.forcedTargetY), this.info.inc);
               break;
            case SPLIT_MISTELLE:
               this.info.initSplitMistelle(new Point(this.info.forcedTargetX, this.info.forcedTargetY));
               break;
            case NOIR_CARTE:
               this.info.initNoirCarte(this.skillID);
               break;
            case QUIVER_FULL_BURST_FLAME_ARROW:
               this.info.initQuiverFullBurstFlameArrow(new Point(this.info.forcedTargetX, this.info.forcedTargetY));
               break;
            case EAGIS_SYSTEM:
               this.info.initPinPointRocket();
               break;
            case ENERGY_BURST:
               if (this.skillID == 400021073) {
                  this.info.initJodiacRay(new Point(this.info.forcedTargetX, this.info.forcedTargetY));
               }
               break;
            case BUTTERFLY_DREAM:
               this.info.initButterflyDream(next + 1, new Point(this.info.forcedTargetX, this.info.forcedTargetY));
               break;
            case FOX_GHOST:
               this.info.initFoxGhost(this.skillID);
               break;
            case FOX_GHOST_UPGRADE:
               this.info.initFoxGhostUpgrade();
               break;
            case PHANTOM_REVERSE_CARD:
               this.info.initPhantomReverseCard();
               break;
            case CLONE_ATTACK:
               if (this.skillID == 135002015 || this.skillID == 131003016) {
                  this.info.initPepeAttack();
               }
         }

         packet.write(true);
         this.info.encode(packet, this.position, next);
      }

      packet.write(false);
      switch (this.getForceAtomType().getType()) {
         case 4:
         case 16:
         case 26:
         case 33:
            packet.writeInt(this.info.forcedTargetX);
            packet.writeInt(this.info.forcedTargetY);
            if (this.skillID == 25100010 || this.skillID == 25120115 || this.skillID == 25141505) {
               packet.writeInt(0);
            }

            if (this.skillID == 400041023) {
               packet.writeInt(this.info.atomFlag);
               packet.writeInt(this.info.specialPos1);
               packet.writeInt(this.info.specialPos2);
            }
            break;
         case 7:
            packet.writeInt(this.info.forcedTargetX - 250);
            packet.writeInt(this.info.forcedTargetY - 250);
            packet.writeInt(this.info.forcedTargetX + 250);
            packet.writeInt(this.info.forcedTargetY + 250);
            break;
         case 9:
         case 15:
         case 29:
            packet.writeInt(this.info.left);
            packet.writeInt(this.info.top);
            packet.writeInt(this.info.right);
            packet.writeInt(this.info.bottom);
            if (this.getForceAtomType().getType() == 29) {
               packet.writeInt(this.info.forcedTargetX);
               packet.writeInt(this.info.forcedTargetY);
            }

            if (this.getForceAtomType().getType() == 15) {
               packet.write(this.info.isShadowBite);
            }
            break;
         case 11:
            packet.writeInt(this.info.left);
            packet.writeInt(this.info.top);
            packet.writeInt(this.info.right);
            packet.writeInt(this.info.bottom);
            packet.writeInt(this.info.bulletItemID);
            break;
         case 17:
         case 77:
            packet.writeInt(this.info.arriveDir);
            packet.writeInt(this.info.arriveRange);
            break;
         case 27:
            packet.writeInt(this.info.left);
            packet.writeInt(this.info.top);
            packet.writeInt(this.info.right);
            packet.writeInt(this.info.bottom);
            packet.writeInt(0);
            break;
         case 28:
         case 34:
            packet.writeInt(this.info.left);
            packet.writeInt(this.info.top);
            packet.writeInt(this.info.right);
            packet.writeInt(this.info.bottom);
            packet.writeInt(this.info.duration);
            break;
         case 36:
         case 39:
         case 89:
         case 90:
            packet.writeInt(this.info.maxBound);
            packet.writeInt(this.info.range);
            packet.writeInt(0);
            this.info.boundRange.encode(packet);
            if (this.getForceAtomType().getType() == 36 || this.getForceAtomType().getType() == 89) {
               this.info.endRect.encode(packet);
               packet.writeInt(this.info.useTick);
            }
            break;
         case 37:
         case 91:
            packet.writeInt(0);
            this.info.endRect.encode(packet);
            packet.writeInt(this.info.range);
            packet.writeInt(this.info.useTick);
            break;
         case 42:
            packet.writeInt(this.info.left);
            packet.writeInt(this.info.top);
            packet.writeInt(this.info.right);
            packet.writeInt(this.info.bottom);
            break;
         case 44:
            packet.writeInt(25141503);
            break;
         case 49:
            packet.writeInt(this.info.bulletItemID);
            packet.writeInt(this.info.objectID);
            packet.writeInt(this.info.left);
            packet.writeInt(this.info.top);
            packet.writeInt(this.info.right);
            packet.writeInt(this.info.bottom);
            break;
         case 50:
            packet.writeInt(this.info.forcedTargetX);
            packet.writeInt(this.info.forcedTargetY);
            packet.writeInt(this.info.range);
            break;
         case 57:
         case 58:
         case 86:
         case 87:
            packet.writeInt(this.info.left);
            packet.writeInt(this.info.top);
            packet.writeInt(this.info.right);
            packet.writeInt(this.info.bottom);
            packet.writeInt(2);
            packet.writeInt(this.info.forcedTargetX);
            packet.writeInt(this.info.forcedTargetY);
            break;
         case 67:
            packet.writeInt(this.info.forcedTargetX);
            packet.writeInt(this.info.forcedTargetY);
            packet.writeInt(this.info.flag);
            packet.write(this.info.lastAttackMaxTaget);
         case 5:
         case 6:
         case 8:
         case 10:
         case 12:
         case 13:
         case 14:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 30:
         case 31:
         case 32:
         case 35:
         case 38:
         case 40:
         case 41:
         case 43:
         case 45:
         case 46:
         case 47:
         case 48:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 88:
         default:
            packet.writeInt(this.info.forcedTargetX);
            packet.writeInt(this.info.forcedTargetY);
            switch (this.skillID) {
               case 25100010:
               case 25120115:
               case 25141505:
                  packet.writeInt(0);
                  break;
               case 400011131:
                  packet.writeInt(0);
                  packet.write(0);
                  break;
               case 400041023:
                  packet.writeInt(0);
                  packet.writeInt(0);
                  packet.writeInt(0);
            }
      }
   }

   public ForceAtom.AtomType getForceAtomType() {
      return this.forceAtomType;
   }

   public void setForceAtomType(ForceAtom.AtomType forceAtomType) {
      this.forceAtomType = forceAtomType;
   }

   public int getAttackCount() {
      return this.attackCount;
   }

   public void addAttackCount() {
      this.attackCount++;
   }

   public int getOwnerID() {
      return this.ownerID;
   }

   public int getSkillID() {
      return this.skillID;
   }

   public static class AtomInfo {
      public int key;
      public int inc;
      public int firstImpact;
      public int secondImpact;
      public int angle;
      public int startDelay;
      public int grimReaperIncGuage;
      public int createTime;
      public int maxHitCount;
      public int effectIdx;
      public int endDelay;
      public int left;
      public int top;
      public int right;
      public int bottom;
      public Rect endRect;
      public Rect boundRange;
      public int maxBound;
      public int range;
      public int useTick;
      public int arriveDir;
      public int arriveRange;
      public int forcedTargetX;
      public int forcedTargetY;
      public int atomFlag;
      public int specialPos1;
      public int specialPos2;
      public int bulletItemID;
      public int objectID;
      public List<Point> startPos;
      public int flag;
      public int lastAttackMaxTaget;
      public int duration;
      public int unk;
      public boolean isShadowBite = false;

      public int getKey() {
         return this.key;
      }

      public void encode(PacketEncoder packet, Point position, int next) {
         packet.writeInt(this.key);
         packet.writeInt(0);
         packet.writeInt(this.inc);
         packet.writeInt(this.firstImpact);
         packet.writeInt(this.secondImpact);
         packet.writeInt(this.angle);
         packet.writeInt(this.startDelay);
         if (position != null) {
            packet.writeInt(position.x);
            packet.writeInt(position.y);
         } else {
            packet.writeInt(this.startPos == null ? 0 : this.startPos.get(next).x);
            packet.writeInt(this.startPos == null ? 0 : this.startPos.get(next).y);
         }

         packet.writeInt(this.createTime);
         packet.writeInt(this.maxHitCount);
         packet.writeInt(this.effectIdx);
         packet.writeInt(this.endDelay);
         packet.write(false);
      }

      public void initTeleKinesis() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 0;
         this.firstImpact = 21;
         this.secondImpact = Randomizer.rand(46, 48);
         this.angle = 9;
         this.startDelay = 960;
         this.createTime = (int)System.currentTimeMillis();
         this.maxHitCount = 0;
         this.effectIdx = 0;
      }

      public void initFlameDischarge() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 6;
         this.firstImpact = Randomizer.rand(15, 18);
         this.secondImpact = Randomizer.rand(23, 31);
         this.angle = Randomizer.rand(23, 330);
         this.startDelay = 720;
         this.createTime = (int)System.currentTimeMillis();
         this.maxHitCount = 0;
         this.effectIdx = 0;
      }

      public void initFlameDischargeRegen(int posX, int posY) {
         this.initFlameDischarge();
         this.forcedTargetX = posX;
         this.forcedTargetY = posY;
      }

      public void initFoxGhostRegen(int skillID, int posX, int posY) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = skillID == 25100010 ? 4 : 5;
         this.startDelay = 0;
         this.createTime = (int)System.currentTimeMillis();
         this.firstImpact = Randomizer.rand(40, 42);
         this.secondImpact = 3;
         this.angle = Randomizer.rand(100, 350);
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.forcedTargetX = posX;
         this.forcedTargetY = posY;
      }

      public void initShieldChasing(int key) {
         this.key = key;
         this.inc = 3;
      }

      public void initShieldChasingNoKey() {
         this.firstImpact = Randomizer.rand(1, 20);
         this.secondImpact = Randomizer.rand(20, 50);
         this.angle = Randomizer.rand(50, 200);
         this.startDelay = 660;
         this.createTime = (int)System.currentTimeMillis();
         this.maxHitCount = 0;
         this.effectIdx = 0;
      }

      public void initShieldChasingVI(int key) {
         this.key = key;
         this.inc = 3;
      }

      public void initShieldChasingNoKeyVI() {
         this.firstImpact = Randomizer.rand(15, 20);
         this.secondImpact = Randomizer.rand(20, 35);
         this.angle = Randomizer.rand(30, 70);
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initShieldChasingNoKeyVI2() {
         this.firstImpact = Randomizer.rand(40, 45);
         this.secondImpact = Randomizer.rand(3, 5);
         this.angle = Randomizer.rand(0, 360);
         this.createTime = (int)System.currentTimeMillis();
         this.maxHitCount = 0;
         this.effectIdx = 0;
      }

      public void initShieldChasingRegen(int key, int posX, int posY) {
         this.key = key;
         this.inc = 3;
         this.firstImpact = Randomizer.rand(40, 43);
         this.secondImpact = 3;
         this.angle = Randomizer.rand(36, 205);
         this.startDelay = 50;
         this.createTime = (int)System.currentTimeMillis();
         this.forcedTargetX = posX;
         this.forcedTargetY = posY;
      }

      public void initMegiddoFlame(int key, int inc) {
         this.key = key;
         this.inc = inc;
         this.firstImpact = Randomizer.rand(10, 17);
         this.secondImpact = Randomizer.rand(10, 16);
         this.angle = Randomizer.rand(40, 52);
         this.startDelay = 780;
         this.createTime = (int)System.currentTimeMillis();
         this.maxHitCount = 0;
         this.effectIdx = 0;
      }

      public void initDemonForce(int inc) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = inc;
         this.firstImpact = Randomizer.rand(1, 28);
         this.secondImpact = Randomizer.rand(1, 10);
         this.angle = Randomizer.rand(0, 8);
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initBase() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = Randomizer.rand(1, 3);
         this.firstImpact = Randomizer.rand(1, 28);
         this.secondImpact = Randomizer.rand(1, 10);
         this.angle = Randomizer.rand(0, 8);
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initNoirCarte(int skillId) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = skillId == 24100003 ? 1 : 2;
         this.firstImpact = Randomizer.rand(15, 29);
         this.secondImpact = Randomizer.rand(7, 11);
         this.angle = Randomizer.rand(0, 9);
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initSoulSeeker() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(15, 19);
         this.secondImpact = Randomizer.rand(20, 34);
         this.angle = Randomizer.rand(30, 69);
         this.startDelay = 470;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initSoulSeekerRegen(int posX, int posY) {
         this.initSoulSeeker();
         this.firstImpact = Randomizer.rand(24, 45);
         this.secondImpact = Randomizer.rand(3, 3);
         this.forcedTargetX = posX;
         this.forcedTargetY = posY;
         this.startDelay = 0;
         this.angle = Randomizer.rand(4, 301);
      }

      public void initWillOfSword(boolean transform, boolean isStrike) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = transform ? 4 : 1;
         this.firstImpact = Randomizer.rand(15, 18);
         this.secondImpact = Randomizer.rand(26, 31);
         this.angle = 0;
         this.startDelay = 1000;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
         this.endDelay = isStrike ? 2000 : 0;
      }

      public void initBowMasterMagicArrow(int changeType) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 10 - changeType;
         this.firstImpact = Randomizer.rand(10, 20);
         this.secondImpact = Randomizer.rand(5, 10);
         this.angle = Randomizer.rand(4, 301);
         this.startDelay = Randomizer.rand(20, 48);
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initMarkOfThief(Point position, int itemID) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(42, 43);
         this.secondImpact = Randomizer.rand(3, 4);
         this.angle = Randomizer.rand(1, 364);
         this.startDelay = 200;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
         this.left = position.x - 50;
         this.top = position.y - 100;
         this.right = position.x + 50;
         this.bottom = position.y + 100;
         this.bulletItemID = itemID;
      }

      public void initBullet() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.firstImpact = Randomizer.rand(42, 43);
         this.secondImpact = Randomizer.rand(3, 4);
         this.angle = Randomizer.rand(1, 364);
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initBullet(Point position, int itemID, int objectID) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(42, 43);
         this.secondImpact = Randomizer.rand(3, 4);
         this.angle = Randomizer.rand(1, 364);
         this.startDelay = 200;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
         this.left = position.x - 50;
         this.top = position.y - 100;
         this.right = position.x + 50;
         this.bottom = position.y + 100;
         this.objectID = objectID;
         this.bulletItemID = itemID;
      }

      public void initShadowBat(Point position, int inc) {
         this.initShadowBat(position, 500, inc);
      }

      public void initShadowBat(Point position, int startDelay, int inc) {
         this.key = 1;
         this.inc = inc;
         this.firstImpact = 1;
         this.secondImpact = 5;
         this.angle = Randomizer.rand(30, 80);
         this.startDelay = startDelay;
         this.createTime = (int)System.currentTimeMillis();
         this.left = position.x;
         this.top = position.y;
         this.right = position.x;
         this.bottom = position.y;
         this.maxHitCount = 0;
         this.endDelay = 0;
         this.isShadowBite = startDelay == 2000;
      }

      public void initShadowBatRegen(int key, Point position) {
         this.key = key;
         this.inc = 0;
         this.firstImpact = 5;
         this.secondImpact = 5;
         this.angle = 46;
         this.startDelay = 80;
         this.createTime = (int)System.currentTimeMillis();
         this.right = position.x;
         this.top = position.y;
         this.forcedTargetX = position.x;
         this.forcedTargetY = position.y;
      }

      public void initFoxGhost(int skillID) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = skillID == 25100010 ? 1 : 2;
         this.firstImpact = Randomizer.rand(10, 20);
         this.secondImpact = Randomizer.rand(20, 40);
         this.angle = Randomizer.rand(40, 50);
         this.startDelay = 630;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initFoxGhostUpgrade() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(30, 35);
         this.secondImpact = Randomizer.rand(80, 90);
         this.angle = Randomizer.rand(45, 150);
         this.startDelay = 630;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initPinPointRocket() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 0;
         this.firstImpact = Randomizer.rand(10, 20);
         this.secondImpact = Randomizer.rand(20, 40);
         this.angle = Randomizer.rand(40, 200);
         this.startDelay = Randomizer.rand(500, 2000);
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initHomingMisile() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 2;
         this.firstImpact = 50;
         this.secondImpact = Randomizer.rand(10, 14);
         this.angle = Randomizer.rand(0, 48);
         this.startDelay = Randomizer.rand(285, 525);
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initMesoExplosion(List<Point> pos) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(42, 47);
         this.secondImpact = Randomizer.rand(3, 4);
         this.angle = Randomizer.rand(4, 306);
         this.startDelay = 250;
         this.startPos = pos;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initIdleWorm() {
         this.firstImpact = Randomizer.rand(40, 49);
         this.secondImpact = Randomizer.rand(5, 9);
         this.angle = Randomizer.rand(5, 174);
         this.startDelay = 480;
         this.duration = 8;
         this.left = -650;
         this.top = -450;
         this.right = 650;
         this.bottom = 250;
      }

      public void initDotPunisher(List<Point> position) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(40, 44);
         this.secondImpact = Randomizer.rand(3, 4);
         this.angle = Randomizer.rand(0, 360);
         this.startDelay = 720;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
         this.left = -700;
         this.top = -600;
         this.right = 700;
         this.bottom = 600;
         this.duration = 20;
         this.startPos = position;
      }

      public void initGuidedArrow() {
         this.key = 12345;
         this.inc = 1;
         this.firstImpact = Randomizer.rand(43, 44);
         this.secondImpact = Randomizer.rand(3, 4);
         this.angle = 90;
         this.startDelay = 840;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
         this.left = -500;
         this.top = -350;
         this.right = 500;
         this.bottom = 350;
      }

      public void initTriFling(int inc, int idx) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = inc;
         this.firstImpact = Randomizer.rand(35, 36);
         this.secondImpact = 5;
         this.angle = Randomizer.nextInt(2) * 180;
         this.startDelay = 150 * idx;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initStormBringer() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = Randomizer.rand(1, 3);
         this.firstImpact = 0;
         this.secondImpact = 10;
         this.angle = 180;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initOrbitalFlame(int effect, int direction, int range) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = effect;
         this.firstImpact = 17;
         this.secondImpact = 17;
         this.angle = 90;
         this.createTime = (int)System.currentTimeMillis();
         this.maxHitCount = 8;
         this.effectIdx = 0;
         this.arriveDir = direction;
         this.arriveRange = range;
      }

      public void initIllusionaryShot(boolean faceLeft) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(10, 20);
         this.secondImpact = Randomizer.rand(5, 15);
         this.angle = faceLeft ? 270 : 90;
         this.startDelay = 0;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initBlackJack() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 33;
         this.firstImpact = Randomizer.rand(30, 34);
         this.secondImpact = Randomizer.rand(20, 29);
         this.angle = Randomizer.rand(46, 144);
         this.startDelay = 760;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initBlackJackRegen(int x, int y) {
         this.inc = 74;
         this.startDelay = 0;
         this.firstImpact = Randomizer.rand(55, 59);
         this.secondImpact = Randomizer.rand(8, 10);
         this.angle = Randomizer.rand(48, 147);
         this.forcedTargetX = x;
         this.forcedTargetY = y;
      }

      public void initCraftJabelin(int useTick, Rect boundRange, Rect endRect, int range, int maxBound) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 2;
         this.firstImpact = 50;
         this.secondImpact = 50;
         this.angle = 0;
         this.startDelay = 0;
         this.range = range;
         this.maxBound = maxBound;
         this.boundRange = boundRange;
         this.endRect = endRect;
         this.createTime = (int)System.currentTimeMillis();
         this.useTick = useTick;
      }

      public void initCraftJabelinSplitted(int maxBound, Rect boundRange, Point pos) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.maxBound = maxBound;
         this.inc = 1;
         this.firstImpact = Randomizer.rand(52, 70);
         this.secondImpact = Randomizer.rand(5, 6);
         this.startDelay = 0;
         this.startPos = new ArrayList<>();
         this.startPos.add(pos);
         this.angle = Randomizer.rand(0, 359);
         this.createTime = (int)System.currentTimeMillis();
         this.boundRange = boundRange;
      }

      public void initGloryWingJabelin(int skillID, Rect end, int useTick, int range) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 1;
         if (skillID == 152120001 || skillID == 152141005) {
            this.inc = 2;
         }

         this.firstImpact = 40;
         this.secondImpact = 60;
         this.angle = 0;
         this.startDelay = 0;
         this.endRect = end;
         this.range = range;
         this.createTime = (int)System.currentTimeMillis();
         this.useTick = useTick;
      }

      public void initMagicMissile() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 2;
         this.firstImpact = Randomizer.rand(52, 70);
         this.secondImpact = Randomizer.rand(5, 6);
         this.angle = Randomizer.rand(0, 359);
         this.startDelay = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initHarmonyWingBeatToUser() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(40, 45);
         this.secondImpact = Randomizer.rand(5, 6);
         this.angle = Randomizer.rand(0, 359);
         this.startDelay = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initHarmonyWingBeatToMob() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(40, 45);
         this.secondImpact = Randomizer.rand(5, 6);
         this.angle = Randomizer.rand(0, 359);
         this.startDelay = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initMagicWreckage(List<Point> position) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 1;
         this.firstImpact = 28 + this.key;
         this.secondImpact = Randomizer.rand(3, 4);
         this.angle = Randomizer.rand(309, 342);
         this.startDelay = 0;
         this.startPos = position;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initImpendingDead() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 0;
         this.firstImpact = 1;
         this.secondImpact = Randomizer.rand(6, 8);
         this.angle = 270;
         this.startDelay = Randomizer.rand(85, 115);
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initSpellBullets() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = Randomizer.rand(900, 905);
         this.firstImpact = Randomizer.rand(38, 46);
         this.secondImpact = Randomizer.rand(5, 6);
         this.angle = Randomizer.rand(31, 59);
         this.startDelay = Randomizer.rand(900, 960);
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initReturningHatred(List<Point> position) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 0;
         this.firstImpact = Randomizer.rand(40, 50);
         this.secondImpact = Randomizer.rand(30, 60);
         this.angle = Randomizer.rand(150, 180);
         this.startPos = position;
         this.createTime = (int)System.currentTimeMillis();
         this.maxHitCount = 8;
      }

      public void initCardinalDischarge(int inc) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = inc;
         this.firstImpact = Randomizer.rand(20, 30);
         this.secondImpact = Randomizer.rand(8, 12);
         this.angle = Randomizer.rand(8, 169);
         this.startDelay = 60;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initSilhouetteMirage(int i, List<Point> startPos) {
         this.key = ForceAtom.SN.addAndGet(1);
         this.inc = 3;
         this.firstImpact = Randomizer.rand(22, 26);
         this.secondImpact = Randomizer.rand(7, 9);
         this.angle = Randomizer.rand(165, 168);
         this.startDelay = 90 + i * 210;
         this.startPos = startPos;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initAdditionalDischarge(Point forceTarget, int inc) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = inc;
         this.firstImpact = Randomizer.rand(40, 45);
         this.secondImpact = Randomizer.rand(3, 4);
         this.angle = 0;
         this.startDelay = 60;
         this.createTime = (int)System.currentTimeMillis();
         this.left = -500;
         this.top = -200;
         this.right = 500;
         this.bottom = 200;
         this.forcedTargetX = forceTarget.x;
         this.forcedTargetY = forceTarget.y;
      }

      public void initForsakenRelic(Point forceTarget) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 3;
         this.firstImpact = ThreadLocalRandom.current().nextInt(40, 45);
         this.secondImpact = ThreadLocalRandom.current().nextInt(3, 5);
         this.angle = ThreadLocalRandom.current().nextInt(160, 175);
         this.startDelay = ThreadLocalRandom.current().nextInt(0, 30);
         this.createTime = (int)System.currentTimeMillis();
         this.left = -500;
         this.top = -200;
         this.right = 500;
         this.bottom = 200;
         this.forcedTargetX = forceTarget.x;
         this.forcedTargetY = forceTarget.y;
      }

      public void initSplitMistelle(Point forceTarget) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(23, 29);
         this.secondImpact = Randomizer.rand(3, 4);
         this.angle = Randomizer.rand(10, 320);
         this.left = -650;
         this.top = -450;
         this.right = 650;
         this.top = 250;
         this.startDelay = 120;
         this.createTime = (int)System.currentTimeMillis();
         this.forcedTargetX = forceTarget.x;
         this.forcedTargetY = forceTarget.y;
      }

      public void initQuiverFullBurstFlameArrow(Point pos) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 1;
         this.firstImpact = 35;
         this.secondImpact = Randomizer.rand(4, 5);
         this.angle = 5;
         this.startDelay = Randomizer.rand(30, 50);
         this.forcedTargetX = pos.x;
         this.forcedTargetY = pos.y;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initStormGuardWind(int inc) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = inc;
         this.firstImpact = 15;
         this.secondImpact = 15;
         this.angle = 70;
         this.startDelay = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initEnergyBurst(Point pos) {
         this.key = ForceAtom.SN.addAndGet(1);
         this.inc = Randomizer.rand(1, 3);
         this.firstImpact = Randomizer.rand(25, 45);
         this.secondImpact = Randomizer.rand(5, 10);
         this.angle = Randomizer.rand(4, 301);
         this.startDelay = 0;
         this.left = pos.x + 106;
         this.top = pos.y - 100;
         this.right = pos.x + 441;
         this.bottom = pos.y + 100;
         this.forcedTargetX = pos.x;
         this.forcedTargetY = pos.y;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initJodiacRay(Point pos) {
         this.key = ForceAtom.SN.addAndGet(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(25, 45);
         this.secondImpact = Randomizer.rand(5, 10);
         this.angle = Randomizer.rand(4, 301);
         this.startDelay = 0;
         this.left = pos.x + 106;
         this.top = pos.y - 100;
         this.right = pos.x + 441;
         this.bottom = pos.y + 100;
         this.forcedTargetX = pos.x;
         this.forcedTargetY = pos.y;
         this.maxHitCount = 0;
         this.effectIdx = 4;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initRuneofPurification(Point mobPos, Point pos) {
         this.key = ForceAtom.SN.addAndGet(1);
         this.inc = 8;
         this.firstImpact = 48;
         this.secondImpact = 6;
         this.angle = 51;
         this.startDelay = 235;
         this.left = mobPos.x - 101;
         this.top = mobPos.y - 100;
         this.right = mobPos.x + 101;
         this.bottom = mobPos.y + 100;
         this.forcedTargetX = pos.x;
         this.forcedTargetY = pos.y;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initGrimReaper(Point mobPos, Point pos) {
         this.key = ForceAtom.SN.addAndGet(1);
         this.inc = 4;
         this.firstImpact = Randomizer.rand(35, 49);
         this.secondImpact = Randomizer.rand(5, 6);
         this.angle = Randomizer.rand(30, 69);
         this.startDelay = 1260;
         this.grimReaperIncGuage = 200;
         this.left = mobPos.x - 101;
         this.top = mobPos.y - 100;
         this.right = mobPos.x + 101;
         this.bottom = mobPos.y + 100;
         this.forcedTargetX = pos.x;
         this.forcedTargetY = pos.y;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initGrimReaperBoss(Point mobPos, Point pos) {
         this.key = ForceAtom.SN.addAndGet(1);
         this.inc = 4;
         this.firstImpact = Randomizer.rand(35, 49);
         this.secondImpact = Randomizer.rand(5, 6);
         this.angle = Randomizer.rand(30, 69);
         this.startDelay = 1260;
         this.grimReaperIncGuage = 2000;
         this.left = mobPos.x - 101;
         this.top = mobPos.y - 100;
         this.right = mobPos.x + 101;
         this.bottom = mobPos.y + 100;
         this.forcedTargetX = pos.x;
         this.forcedTargetY = pos.y;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initSuctionBottle() {
         this.key = ForceAtom.SN.addAndGet(1);
         this.inc = 2;
         this.firstImpact = 1;
         this.secondImpact = 5;
         this.angle = 30;
         this.startDelay = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initCloneAttack() {
         this.key = ForceAtom.SN.addAndGet(1);
         this.inc = 1;
         this.firstImpact = 40;
         this.secondImpact = 3;
         this.angle = Randomizer.rand(150, 350);
         this.startDelay = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initPepeAttack() {
         this.key = ForceAtom.SN.addAndGet(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(40, 42);
         this.secondImpact = Randomizer.rand(3, 4);
         this.angle = Randomizer.rand(134, 332);
         this.startDelay = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initPinkBeanAtom() {
         this.key = ForceAtom.SN.addAndGet(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(40, 42);
         this.secondImpact = Randomizer.rand(3, 4);
         this.angle = Randomizer.rand(134, 332);
         this.startDelay = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initButterflyDream(int inc, Point pos) {
         this.key = ForceAtom.SN.addAndGet(1);
         this.inc = inc;
         this.firstImpact = Randomizer.rand(40, 45);
         this.secondImpact = 4;
         this.angle = Randomizer.rand(150, 350);
         this.startDelay = 0;
         this.createTime = (int)System.currentTimeMillis();
         this.forcedTargetX = pos.x;
         this.forcedTargetY = pos.y;
      }

      public void initMicroMissileContainer() {
         this.key = ForceAtom.SN.addAndGet(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(40, 50);
         this.secondImpact = Randomizer.rand(5, 6);
         this.startDelay = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initMightyMjolnir(Point target, int startDelay, int flag, int lastAttackMaxTaget) {
         this.key = ForceAtom.SN.addAndGet(1);
         this.inc = 0;
         this.firstImpact = 1;
         this.secondImpact = Randomizer.rand(25, 28);
         this.angle = Randomizer.rand(70, 80);
         this.startDelay = startDelay;
         this.flag = flag;
         this.createTime = (int)System.currentTimeMillis();
         this.forcedTargetX = target.x;
         this.forcedTargetY = target.y;
         this.lastAttackMaxTaget = lastAttackMaxTaget;
      }

      public void initHeavenEarthHumanAppartion() {
         this.key = ForceAtom.SN.addAndGet(1);
         this.inc = 1;
         this.firstImpact = 35;
         this.secondImpact = 5;
         this.angle = Randomizer.rand(160, 360);
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initPhantomReverseCard() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(15, 29);
         this.secondImpact = Randomizer.rand(7, 11);
         this.angle = Randomizer.rand(0, 9);
         this.startDelay = 0;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initShadowBite(Point position) {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 2;
         this.firstImpact = 42;
         this.secondImpact = 6;
         this.angle = 33;
         this.startDelay = Randomizer.rand(2500, 3000);
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.left = position.x - 50;
         this.top = position.y - 10;
         this.right = position.x + 50;
         this.bottom = position.y + 10;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initCheerballoonRegen2() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(15, 20);
         this.secondImpact = Randomizer.rand(20, 35);
         this.angle = Randomizer.rand(30, 70);
         this.startDelay = 0;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initCheerballoonRegen() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(40, 45);
         this.secondImpact = Randomizer.rand(3, 5);
         this.angle = Randomizer.rand(0, 360);
         this.startDelay = 0;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initCheerballoon() {
         this.key = ForceAtom.SN.getAndAdd(1);
         this.inc = 1;
         this.firstImpact = Randomizer.rand(57, 62);
         this.secondImpact = Randomizer.rand(15, 18);
         this.angle = Randomizer.rand(-25, 25);
         this.startDelay = 0;
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void initForceAtomFromData(int inc, int type) {
         this.inc = inc;
         this.firstImpact = ForceAtomConstants.getFirstImpact(type);
         this.secondImpact = ForceAtomConstants.getSecondImpact(type);
         this.angle = ForceAtomConstants.getAngle(type);
         this.startDelay = ForceAtomConstants.getStartDelay(type);
         this.maxHitCount = 0;
         this.effectIdx = 0;
         this.createTime = (int)System.currentTimeMillis();
      }

      public void setStartDelay(int startDelay) {
         this.startDelay = startDelay;
      }

      public void setAngle(int angle) {
         this.angle = angle;
      }

      public void setForcedTargetX(int forcedTargetX) {
         this.forcedTargetX = forcedTargetX;
      }

      public void setForcedTargetY(int forcedTargetY) {
         this.forcedTargetY = forcedTargetY;
      }
   }

   public static enum AtomType {
      DEMON_FORCE(0),
      NOIR_CARTE(1),
      WILL_OF_SWORD(2),
      MEGIDDO_FLAME(3),
      ATOM_REGEN(4),
      EAGIS_SYSTEM(5),
      PIN_POINT_ROCKET(6),
      TRI_FLING(7),
      STORM_BRINGER(8),
      TIME_FORCE(9),
      BOW_MASTER_MAGIC_ARROW(10),
      MARK_OF_THIEF(11),
      MESO_EXPLOSION(12),
      FOX_GHOST(13),
      UNK_14(14),
      SHADOW_BAT(15),
      SHADOW_BAT_REGEN(16),
      ORBITAL_FLAME(17),
      UNK_18(18),
      HOMING_MISILE(20),
      MULTIPLE_OPTION_M_FL(21),
      TELE_KINESIS(22),
      MAGIC_WRECKAGE(23),
      SUPER_MAGIC_WRECKAGE(24),
      AUTO_SOUL_SEEKER(25),
      AUTO_SOUL_SEEKER_REGEN(26),
      GUIDED_ARROW(27),
      DOT_PUNISHER(28),
      ENERGY_BURST(29),
      MICRO_MISSILE_CONTAINER(30),
      ILLUSIONARY_SHOT(31),
      WILL_OF_SWORD_STRIKE(32),
      BLACK_JACK(33),
      IDLE_WORM(34),
      UNK_35(35),
      CRAFT_JABELIN(36),
      GLORY_WING_JABELIN(37),
      MAGIC_MISSILE(38),
      CRAFT_JABELIN_SPLITTED(39),
      HARMONY_WING_BEAT_TO_USER(40),
      HARMONY_WING_BEAT_TO_MOB(41),
      SHADOW_BITE(42),
      PLAIN_SPELL(43),
      SCARLET_SPELL(44),
      GUST_SPELL(45),
      ABYSS_SPELL(46),
      IMPENDING_DEAD(47),
      RETURNING_HATRED(48),
      BULLET(49),
      QUIVER_FULL_BURST_FLAME_ARROW(50),
      STORM_GUARD_WIND(51),
      BLUE_ARROW(52),
      BLUE_MISSILE(53),
      BLUE_ARROW_BIG(54),
      BLUE_BALL(55),
      CARDINAL_DISCHARGE(56),
      EDITIONAL_DISCHARGE(57),
      SPLIT_MISTELLE(58),
      UNK_59(59),
      CLONE_ATTACK(60),
      BUTTERFLY_DREAM(61),
      UNK_62(62),
      SUCTION_BOTTLE(63),
      UNK_64(64),
      PINKBEAN_ATOM(65),
      UNK_66(66),
      MIGHTY_MJOLNIR(67),
      FOX_GHOST_UPGRADE(68),
      UNK_69(69),
      UNK_70(70),
      UNK_71(71),
      HEAVEN_EARTH_HUMAN_APPARTION(72),
      PHANTOM_REVERSE_CARD(73),
      UNK_74(74),
      BLOOD_MESO_EXPLOSION(75),
      HOMING_MISILE_2(76),
      flameOrbFoxVI2(77),
      unk78(78),
      unk79(79),
      unk80(80),
      unk81(81),
      unk82(82),
      unk83(83),
      DAShieldChasing2_1(84),
      DAShieldChasing2_2(85),
      Additional_Blast_VI(86),
      Forsaken_Relic(87),
      unk88(88),
      CraftJavelinVI(89),
      CraftJavelinVI_Split(90),
      GloryWingJavelin_VI(91),
      GloryWingJavelin_VI_MagicMissile(92),
      GrandFinale_Cheerballoon1(93),
      GrandFinale_Cheerballoon_REGEN(94),
      GrandFinale_Cheerballoon(95);

      int type;

      private AtomType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }
   }
}
