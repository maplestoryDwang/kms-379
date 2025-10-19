package objects.fields.child.slime;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import network.encode.PacketEncoder;
import objects.utils.Pair;
import objects.utils.Randomizer;

public class GuardianAngelSlime {
   public static class CameraWork implements GuardianAngelSlime.SlimePattern {
      public Point pos1;
      public Point pos2;
      public int posXSpeed;
      public int posYSpeed;
      public int duration;

      public CameraWork(Point pos1, Point pos2, int posXSpeed, int posYSpeed, int duration) {
         this.pos1 = pos1;
         this.pos2 = pos2;
         this.posXSpeed = posXSpeed;
         this.posYSpeed = posYSpeed;
         this.duration = duration;
      }

      @Override
      public int getOpcode() {
         return 2;
      }

      @Override
      public void encode(PacketEncoder p) {
         p.encodePos4Byte(this.pos1);
         p.encodePos4Byte(this.pos2);
         p.writeInt(this.posXSpeed);
         p.writeInt(this.posYSpeed);
         p.writeInt(this.duration);
      }
   }

   public static class InitAttack1 implements GuardianAngelSlime.SlimePattern {
      @Override
      public int getOpcode() {
         return 11;
      }

      @Override
      public void encode(PacketEncoder p) {
         p.writeLong(0L);
      }
   }

   public static class InitAttack2 implements GuardianAngelSlime.SlimePattern {
      @Override
      public int getOpcode() {
         return 5;
      }

      @Override
      public void encode(PacketEncoder p) {
         p.writeInt(1);
      }
   }

   public static class InitCrystal implements GuardianAngelSlime.SlimePattern {
      public int state;
      public int state2;
      public int state3;
      public int objectID;
      public int charID;
      public int posX;
      public int posY;

      public InitCrystal(int state, int state2, int state3) {
         this.state = state;
         this.state2 = state2;
         this.state3 = state3;
      }

      @Override
      public int getOpcode() {
         return 4;
      }

      @Override
      public void encode(PacketEncoder p) {
         p.writeInt(this.state);
         if (this.state == 0) {
            p.writeInt(this.state2);
            p.writeInt(this.state3);
         } else if (this.state == 1) {
            p.writeInt(this.state2);
            p.writeInt(this.state3);
            switch (this.state2) {
               case 0:
               case 1:
               case 2:
               case 3:
               case 4:
               case 5:
               default:
                  break;
               case 6:
                  p.writeInt(20);
                  p.writeInt(this.objectID);
                  p.writeInt(2);
                  p.writeInt(this.charID);
                  p.writeInt(Randomizer.nextInt());
                  p.writeInt(this.posX);
                  p.writeInt(this.posY);
                  break;
               case 7:
                  p.writeInt(0);
            }
         }
      }
   }

   public static class InitFootHold implements GuardianAngelSlime.SlimePattern {
      List<Pair<String, Boolean>> footHoldName;

      public InitFootHold(List<Pair<String, Boolean>> footHoldName) {
         this.footHoldName = footHoldName;
      }

      @Override
      public int getOpcode() {
         return 3;
      }

      @Override
      public void encode(PacketEncoder p) {
         p.writeInt(this.footHoldName.size());
         this.footHoldName.forEach(FH -> {
            p.writeMapleAsciiString(FH.left);
            p.write(FH.right);
         });
      }
   }

   public static class InitGuardianWave implements GuardianAngelSlime.SlimePattern {
      public int type;
      public int posX;
      public int posY;
      public int state;

      public InitGuardianWave() {
         this.type = 2;
      }

      public InitGuardianWave(Point pos) {
         this.type = 0;
         this.posX = pos.x;
         this.posY = pos.y;
      }

      public InitGuardianWave(int type) {
         this.type = type;
      }

      @Override
      public int getOpcode() {
         return 1;
      }

      @Override
      public void encode(PacketEncoder p) {
         p.writeInt(this.type);
         switch (this.type) {
            case 0:
               p.writeInt(Randomizer.nextInt());
               p.writeInt(this.posX);
               p.writeInt(this.posY);
               p.writeInt(10);
               p.writeInt(15);
               p.writeInt(0);
               p.writeInt(0);
               p.writeInt(0);
               p.writeInt(-500);
               p.writeInt(20);
               p.writeInt(1000);
               p.writeInt(3);
               p.writeInt(1000);
               p.writeInt(1000);
               p.write(false);
            case 1:
            case 3:
            case 4:
            default:
               break;
            case 2:
               p.writeInt(this.state);
               break;
            case 5:
               p.write(false);
         }
      }
   }

   public static class InitPortal implements GuardianAngelSlime.SlimePattern {
      public int state;
      public List<Object> portalList;

      public InitPortal(List<Object> list) {
         if (!list.isEmpty()) {
            this.portalList = list;
            if (list.get(0) instanceof Point) {
               this.state = 0;
            } else if (list.get(0) instanceof Integer) {
               this.state = 1;
            }
         }
      }

      @Override
      public int getOpcode() {
         return 8;
      }

      @Override
      public void encode(PacketEncoder p) {
         p.writeInt(this.state);
         switch (this.state) {
            case 0:
               p.writeInt(3);
               p.writeInt(this.portalList.size());

               for (Object data : this.portalList) {
                  p.encodePos4Byte((Point)data);
               }

               for (int i = 0; i < this.portalList.size(); i++) {
                  p.writeInt(i < 4 ? 2 : 1);
               }

               for (int i = 0; i < this.portalList.size(); i++) {
                  p.writeInt(i < 4 ? i : 0);
               }

               for (int i = 0; i < this.portalList.size(); i++) {
                  p.write(i % 2 == 1);
               }
               break;
            case 1:
               p.writeInt(this.portalList.size());

               for (int i = 0; i < this.portalList.size(); i++) {
                  p.writeInt(i);
               }
         }
      }
   }

   public static class InitState implements GuardianAngelSlime.SlimePattern {
      public int state;

      public InitState(int state) {
         this.state = state;
      }

      @Override
      public int getOpcode() {
         return 6;
      }

      @Override
      public void encode(PacketEncoder p) {
         p.writeInt(this.state);
      }
   }

   public static class MagmaSlimeInit implements GuardianAngelSlime.SlimePattern {
      public int state;
      public List<GuardianAngelSlime.MagmaSlimeInit.MagmaSlime> slimeList;
      public GuardianAngelSlime.MagmaSlimeInit.MagmaSlime slime;
      public int objectID;
      public int towerObjectID;
      public int charID;
      public int value1;
      public int value2;
      public int count;
      public long value3;
      public Point srcPos;
      public Point destPos;

      public MagmaSlimeInit(int state) {
         this.state = state;
      }

      @Override
      public int getOpcode() {
         return 7;
      }

      @Override
      public void encode(PacketEncoder p) {
         p.writeInt(this.state);
         switch (this.state) {
            case 1:
               p.writeInt(this.slimeList.size());

               for (GuardianAngelSlime.MagmaSlimeInit.MagmaSlime slime : this.slimeList) {
                  p.write(slime != null);
                  if (slime != null) {
                     slime.encode(p);
                  }
               }
               break;
            case 2:
               p.writeInt(this.objectID);
            case 3:
            case 4:
            case 5:
            case 7:
            case 9:
            case 12:
            default:
               break;
            case 6:
               p.writeInt(this.objectID);
               p.writeInt(this.charID);
               p.writeInt(this.value1);
               p.encodePos4Byte(this.srcPos);
               p.encodePos4Byte(this.destPos);
               break;
            case 8:
               p.writeInt(this.objectID);
               p.writeInt(20);
               p.encodePos4Byte(this.srcPos);
               p.encodePos4Byte(this.destPos);
               break;
            case 10:
               p.writeInt(this.objectID);
               p.writeInt(this.value2);
               p.writeLong(this.value3);
               p.writeInt(15);
               p.writeInt(320);
               p.writeInt(11);
               p.writeInt(10);
               p.writeLong(4607182418800017408L);
               break;
            case 11:
               p.writeInt(0);
               p.writeLong(0L);
               p.encodeRect(null);
               break;
            case 13:
               p.writeInt(this.objectID);
               p.writeInt(this.towerObjectID);
               p.writeInt(this.count);
         }
      }

      public static class MagmaSlime {
         public int objectID;
         public int mobID;
         public int FH;
         public Point pos;
         public Rectangle lt;
         public Rectangle lt2;
         public Rectangle center;
         public Rectangle rb;
         public Rectangle center2;

         public MagmaSlime(int objectID, int mobID, Point pos, Rectangle lt, Rectangle lt2, Rectangle center, Rectangle rb, Rectangle center2) {
            this.objectID = objectID;
            this.mobID = mobID;
            this.pos = pos;
            this.lt = lt;
            this.lt2 = lt2;
            this.center = center;
            this.rb = rb;
            this.center2 = center2;
         }

         public void encode(PacketEncoder p) {
            p.writeInt(1);
            p.encodeRect(this.lt);
            p.encodeRect(this.lt2);
            p.encodeRect(this.center);
            p.encodeRect(this.rb);
            p.encodeRect(this.center2);
            p.encodePos4Byte(this.pos);
            p.writeInt(this.objectID);
            p.writeInt(this.FH);
            p.writeInt(1080);
            p.writeInt(0);
            p.writeInt(this.mobID);
            p.writeInt(0);
            p.writeInt(this.mobID);
            p.writeInt(3);
         }
      }
   }

   public static class SetSlimeGuardPortalCount implements GuardianAngelSlime.SlimePattern {
      public int state;
      public int value;

      public SetSlimeGuardPortalCount(int state, int value) {
         this.state = state;
         this.value = value;
      }

      @Override
      public int getOpcode() {
         return 13;
      }

      @Override
      public void encode(PacketEncoder p) {
         p.writeInt(this.state);
         if (this.state == 10) {
            p.writeInt(this.value);
         }
      }
   }

   public static class SlimeGauge implements GuardianAngelSlime.SlimePattern {
      public int before;
      public int after;
      public int total;

      public SlimeGauge(int before, int after, int total) {
         this.before = before;
         this.after = after;
         this.total = total;
      }

      @Override
      public int getOpcode() {
         return 9;
      }

      @Override
      public void encode(PacketEncoder p) {
         p.writeInt(this.before);
         p.writeInt(this.after);
         p.writeInt(this.total);
      }
   }

   public interface SlimePattern {
      int getOpcode();

      void encode(PacketEncoder var1);
   }

   public static class SpawnMonster implements GuardianAngelSlime.SlimePattern {
      public int monsterID;
      public int posX;
      public int posY;

      public SpawnMonster(int monsterID, int posX, int posY) {
         this.monsterID = monsterID;
         this.posX = posX;
         this.posY = posY;
      }

      @Override
      public int getOpcode() {
         return 12;
      }

      @Override
      public void encode(PacketEncoder p) {
         p.writeInt(this.monsterID);
         p.writeInt(this.posX);
         p.writeInt(this.posY);
      }
   }

   public static class UnkSlime implements GuardianAngelSlime.SlimePattern {
      @Override
      public int getOpcode() {
         return 10;
      }

      @Override
      public void encode(PacketEncoder p) {
         p.writeInt(0);
      }
   }
}
