package objects.fields.child.rimen;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;
import objects.users.MapleCharacter;
import objects.utils.Pair;
import objects.utils.Randomizer;

public class DunkelEliteBossEntry {
   private int type;
   private int unk;
   private int unk2;
   private int ballCreateDelay;
   private int unk3;
   private int unk4;
   private int bossObjectID;
   private int controllerID;
   private int unk5;
   private int unk6;
   private int unk7;
   private int unk8;
   private int unk9;
   private int unk11;
   private boolean unk10;
   private boolean faceLeft;
   private Point unkPos;
   private Point unkPos2;
   private Point createPos;
   private List<Point> affectedAreaPos = new ArrayList<>();
   private List<Pair<Point, Integer>> forcedCreatePos = new ArrayList<>();

   public static DunkelEliteBossEntry initEntry(int type, int bossObjectID, MapleCharacter target, boolean firstSet) {
      DunkelEliteBossEntry ret = new DunkelEliteBossEntry();
      ret.setType(type);
      if (type == 0) {
         Point createPos = null;
         if (firstSet) {
            createPos = new Point(target.getTruePosition().x, target.getTruePosition().y);
         } else if (target.getTruePosition().x > 400) {
            createPos = new Point(target.getTruePosition().x + 200, target.getTruePosition().y);
         } else {
            createPos = new Point(target.getTruePosition().x - 200, target.getTruePosition().y);
         }

         ret.setUnk(1);
         ret.setUnk2(2800);
         ret.setBallCreateDelay(1440);
         ret.setUnk3(1);
         ret.setUnk4(3);
         ret.setBossObjectID(bossObjectID);
         ret.setControllerID(target.getId());
         ret.setUnkPos(new Point(-280, -220));
         ret.setUnkPos2(new Point(10, 10));
         if (createPos.x > 400) {
            ret.setFaceLeft(true);
         }

         ret.setCreatePos(createPos);
      } else if (type == 1) {
         ret.setUnk(2);
         ret.setUnk2(3000);
         ret.setBallCreateDelay(1620);
         ret.setUnk3(1);
         ret.setUnk4(4);
         ret.setBossObjectID(bossObjectID);
         ret.setControllerID(target.getId());
         ret.setUnk5(300);
         ret.setUnk7(1200);
         ret.setUnk8(65);
         ret.setUnk9(100);
         ret.setUnkPos(new Point(-100, -75));
         ret.setUnkPos2(new Point(0, 0));
         ret.setCreatePos(new Point(0, 0));
         List<Pair<Point, Integer>> list = new ArrayList<>();
         if (Randomizer.isSuccess(50)) {
            list.add(new Pair<>(new Point(740, -5), 1));
            list.add(new Pair<>(new Point(-695, -5), 0));
         } else {
            list.add(new Pair<>(new Point(-695, -5), 0));
            list.add(new Pair<>(new Point(740, -5), 1));
         }

         ret.setForcedCreatePos(list);
      } else if (type == 2) {
         ret.setUnk(2);
         ret.setUnk2(3000);
         ret.setBallCreateDelay(1800);
         ret.setUnk3(1);
         ret.setUnk4(5);
         ret.setBossObjectID(bossObjectID);
         ret.setControllerID(target.getId());
         ret.setUnk5(0);
         ret.setUnk6(1);
         ret.setUnk7(1600);
         ret.setUnk8(35);
         ret.setUnk9(600);
         ret.setUnkPos(new Point(-45, -20));
         ret.setUnkPos2(new Point(0, 0));
         ret.setCreatePos(new Point(0, 0));
         List<Pair<Point, Integer>> list = new ArrayList<>();
         if (Randomizer.isSuccess(50)) {
            list.add(new Pair<>(new Point(740, -5), 1));
            list.add(new Pair<>(new Point(-695, -5), 0));
         } else {
            list.add(new Pair<>(new Point(-695, -5), 0));
            list.add(new Pair<>(new Point(740, -5), 1));
         }

         ret.setForcedCreatePos(list);
      } else if (type == 3) {
         Point createPosx = null;
         if (firstSet) {
            createPosx = new Point(target.getTruePosition().x, target.getTruePosition().y);
         } else if (target.getTruePosition().x > 400) {
            createPosx = new Point(target.getTruePosition().x + 200, target.getTruePosition().y);
         } else {
            createPosx = new Point(target.getTruePosition().x - 200, target.getTruePosition().y);
         }

         ret.setUnk(1);
         ret.setUnk2(2900);
         ret.setBallCreateDelay(1500);
         ret.setUnk3(1);
         ret.setUnk4(6);
         ret.setBossObjectID(bossObjectID);
         ret.setControllerID(target.getId());
         if (target.getTruePosition().x > 400) {
            ret.setFaceLeft(true);
            if (firstSet) {
               createPosx.x = target.getTruePosition().x + 200;
            }
         }

         ret.setUnkPos(new Point(-620, -135));
         ret.setUnkPos2(new Point(50, 5));
         ret.setCreatePos(createPosx);
      } else if (type == 4) {
         ret.setUnk(3);
         ret.setUnk2(3300);
         ret.setBallCreateDelay(1710);
         ret.setUnk3(5);
         ret.setUnk4(7);
         ret.setBossObjectID(bossObjectID);
         ret.setControllerID(target.getId());
         ret.setUnk10(true);
         ret.setUnkPos(new Point(-40, -80));
         ret.setUnkPos2(new Point(40, 0));
         ret.setCreatePos(new Point(0, 0));
         List<Point> affectedAreas = new ArrayList<>();

         for (int i = 0; i < 5; i++) {
            affectedAreas.add(new Point(Randomizer.rand(-700, 700), 29));
         }

         ret.setAffectedAreaPos(affectedAreas);
      } else if (type == 5) {
         ret.setUnk(1);
         ret.setUnk2(4800);
         ret.setBallCreateDelay(3630);
         ret.setUnk3(1);
         ret.setUnk4(8);
         ret.setBossObjectID(bossObjectID);
         ret.setControllerID(target.getId());
         ret.setUnk11(1);
         ret.setUnk10(true);
         ret.setUnkPos(new Point(-290, -420));
         ret.setUnkPos2(new Point(270, 25));
         ret.setCreatePos(new Point(Randomizer.rand(-600, 600), 29));
         if (ret.getCreatePos().x < 0) {
            ret.setFaceLeft(true);
         }
      } else if (type == 6) {
         ret.setUnk(3);
         ret.setUnk2(3000);
         ret.setBallCreateDelay(2160);
         ret.setUnk3(7);
         ret.setUnk4(11);
         ret.setBossObjectID(bossObjectID);
         ret.setControllerID(target.getId());
         ret.setUnk11(1);
         ret.setUnk10(true);
         ret.setFaceLeft(true);
         ret.setUnkPos(new Point(-50, -170));
         ret.setUnkPos2(new Point(50, 5));
         ret.setCreatePos(new Point(0, 0));
         List<Point> affectedAreas = new ArrayList<>();

         for (int i = 0; i < 7; i++) {
            affectedAreas.add(new Point(Randomizer.rand(-700, 700), 29));
         }

         ret.setAffectedAreaPos(affectedAreas);
      } else if (type == 7) {
         ret.setUnk(1);
         ret.setUnk2(4800);
         ret.setBallCreateDelay(3630);
         ret.setUnk3(1);
         ret.setUnk4(8);
         ret.setBossObjectID(bossObjectID);
         ret.setControllerID(target.getId());
         ret.setUnk11(1);
         ret.setUnk10(true);
         ret.setUnkPos(new Point(-290, -420));
         ret.setUnkPos2(new Point(270, 25));
         ret.setCreatePos(new Point(Randomizer.rand(-700, 700), 29));
         if (ret.getCreatePos().x > 400) {
            ret.setFaceLeft(true);
         }
      } else if (type == 8) {
         Point createPosxx = null;
         if (firstSet) {
            createPosxx = new Point(target.getTruePosition().x, target.getTruePosition().y);
         } else if (target.getTruePosition().x > 400) {
            createPosxx = new Point(target.getTruePosition().x + 200, target.getTruePosition().y);
         } else {
            createPosxx = new Point(target.getTruePosition().x - 200, target.getTruePosition().y);
         }

         ret.setUnk(1);
         ret.setUnk2(2800);
         ret.setBallCreateDelay(840);
         ret.setUnk3(1);
         ret.setUnk4(12);
         ret.setBossObjectID(bossObjectID);
         ret.setControllerID(target.getId());
         ret.setUnkPos(new Point(-360, -155));
         ret.setUnkPos2(new Point(10, 10));
         if (createPosxx.x > 400) {
            ret.setFaceLeft(true);
            if (firstSet) {
               createPosxx.x = target.getTruePosition().x + 200;
            }
         }

         ret.setCreatePos(createPosxx);
      } else if (type == 9) {
         Point createPosxxx = null;
         if (firstSet) {
            createPosxxx = new Point(target.getTruePosition().x, target.getTruePosition().y);
         } else if (target.getTruePosition().x > 400) {
            createPosxxx = new Point(target.getTruePosition().x + 200, target.getTruePosition().y);
         } else {
            createPosxxx = new Point(target.getTruePosition().x - 200, target.getTruePosition().y);
         }

         ret.setUnk(1);
         ret.setUnk2(2700);
         ret.setBallCreateDelay(840);
         ret.setUnk3(1);
         ret.setUnk4(10);
         ret.setBossObjectID(bossObjectID);
         ret.setControllerID(target.getId());
         ret.setUnkPos(new Point(-350, -155));
         ret.setUnkPos2(new Point(10, 10));
         if (createPosxxx.x > 400) {
            ret.setFaceLeft(true);
            if (firstSet) {
               createPosxxx.x = target.getTruePosition().x + 200;
            }
         }

         ret.setCreatePos(createPosxxx);
      } else {
         ret = null;
      }

      return ret;
   }

   public int getType() {
      return this.type;
   }

   public void setType(int type) {
      this.type = type;
   }

   public int getUnk() {
      return this.unk;
   }

   public void setUnk(int unk) {
      this.unk = unk;
   }

   public int getUnk2() {
      return this.unk2;
   }

   public void setUnk2(int unk2) {
      this.unk2 = unk2;
   }

   public int getBallCreateDelay() {
      return this.ballCreateDelay;
   }

   public void setBallCreateDelay(int ballCreateDelay) {
      this.ballCreateDelay = ballCreateDelay;
   }

   public int getUnk3() {
      return this.unk3;
   }

   public void setUnk3(int unk3) {
      this.unk3 = unk3;
   }

   public int getUnk4() {
      return this.unk4;
   }

   public void setUnk4(int unk4) {
      this.unk4 = unk4;
   }

   public int getBossObjectID() {
      return this.bossObjectID;
   }

   public void setBossObjectID(int bossObjectID) {
      this.bossObjectID = bossObjectID;
   }

   public int getControllerID() {
      return this.controllerID;
   }

   public void setControllerID(int controllerID) {
      this.controllerID = controllerID;
   }

   public int getUnk5() {
      return this.unk5;
   }

   public void setUnk5(int unk5) {
      this.unk5 = unk5;
   }

   public int getUnk6() {
      return this.unk6;
   }

   public void setUnk6(int unk6) {
      this.unk6 = unk6;
   }

   public int getUnk7() {
      return this.unk7;
   }

   public void setUnk7(int unk7) {
      this.unk7 = unk7;
   }

   public int getUnk8() {
      return this.unk8;
   }

   public void setUnk8(int unk8) {
      this.unk8 = unk8;
   }

   public int getUnk9() {
      return this.unk9;
   }

   public void setUnk9(int unk9) {
      this.unk9 = unk9;
   }

   public boolean isUnk10() {
      return this.unk10;
   }

   public void setUnk10(boolean unk10) {
      this.unk10 = unk10;
   }

   public boolean isFaceLeft() {
      return this.faceLeft;
   }

   public void setFaceLeft(boolean faceLeft) {
      this.faceLeft = faceLeft;
   }

   public Point getUnkPos() {
      return this.unkPos;
   }

   public void setUnkPos(Point unkPos) {
      this.unkPos = unkPos;
   }

   public Point getUnkPos2() {
      return this.unkPos2;
   }

   public void setUnkPos2(Point unkPos2) {
      this.unkPos2 = unkPos2;
   }

   public Point getCreatePos() {
      return this.createPos;
   }

   public void setCreatePos(Point createPos) {
      this.createPos = createPos;
   }

   public List<Point> getAffectedAreaPos() {
      return this.affectedAreaPos;
   }

   public void setAffectedAreaPos(List<Point> affectedAreaPos) {
      this.affectedAreaPos = affectedAreaPos;
   }

   public List<Pair<Point, Integer>> getForcedCreatePos() {
      return this.forcedCreatePos;
   }

   public void setForcedCreatePos(List<Pair<Point, Integer>> forcedCreatePos) {
      this.forcedCreatePos = forcedCreatePos;
   }

   public void encode(PacketEncoder packet) {
      packet.writeShort(this.getType());
      packet.writeShort(this.getUnk());
      packet.writeInt(this.getUnk2());
      packet.writeInt(this.getBallCreateDelay());
      packet.writeInt(this.getUnk3());
      packet.writeInt(this.getUnk4());
      packet.writeInt(this.getBossObjectID());
      packet.writeInt(this.getControllerID());
      packet.writeInt(0);
      packet.writeInt(this.getUnk5());
      packet.writeInt(this.getUnk6());
      packet.writeInt(this.getUnk7());
      packet.writeShort(this.getUnk8());
      packet.writeShort(this.getUnk9());
      packet.writeShort(this.getUnk11());
      packet.write(this.isUnk10());
      packet.write(this.isFaceLeft());
      packet.writeInt(this.getUnkPos().x);
      packet.writeInt(this.getUnkPos().y);
      packet.writeInt(this.getUnkPos2().x);
      packet.writeInt(this.getUnkPos2().y);
      packet.writeInt(this.getCreatePos().x);
      packet.writeInt(this.getCreatePos().y);
      packet.writeShort(this.getAffectedAreaPos().size());

      for (Point pos : this.getAffectedAreaPos()) {
         packet.writeInt(pos.x);
         packet.writeInt(pos.y);
      }

      packet.writeShort(this.getForcedCreatePos().size());

      for (Pair<Point, Integer> pair : this.getForcedCreatePos()) {
         packet.writeInt(pair.left.x);
         packet.writeInt(pair.left.y);
         packet.writeInt(pair.right);
      }
   }

   public int getUnk11() {
      return this.unk11;
   }

   public void setUnk11(int unk11) {
      this.unk11 = unk11;
   }
}
