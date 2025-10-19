package objects.fields.child.karrotte;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.fields.Field;
import objects.fields.child.karrotte.guardian.EyeOfAbyssAttackEntry;
import objects.fields.child.karrotte.guardian.GuardianEntry;
import objects.fields.child.karrotte.guardian.GuardianType;
import objects.fields.child.karrotte.guardian.MysticShot;
import objects.users.MapleCharacter;
import objects.utils.Pair;

public class KalosAction {
   public void encode(PacketEncoder packet) {
      packet.writeShort(SendPacketOpcode.KALOS_ACTION.getValue());
   }

   public void sendPacket(MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      this.encode(packet);
      player.send(packet.getPacket());
   }

   public void broadcastPacket(Field field) {
      PacketEncoder packet = new PacketEncoder();
      this.encode(packet);
      field.broadcastMessage(packet.getPacket());
   }

   public static class AssaultAction extends KalosAction {
      final KalosActionType type = KalosActionType.AssaultAction;

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.type.getType());
      }

      public static class EyeOfAbyss extends KalosAction.AssaultAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.EyeOfTheAbyss;
         objects.fields.child.karrotte.guardian.EyeOfAbyss eyeOfAbyss;

         public EyeOfAbyss(objects.fields.child.karrotte.guardian.EyeOfAbyss eyeOfAbyss) {
            this.eyeOfAbyss = eyeOfAbyss;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            this.eyeOfAbyss.encode(packet);
         }
      }

      public static class EyeOfAbyssAttack extends KalosAction.AssaultAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.EyeOfTheAbyssAttack;
         int actionSN;
         List<EyeOfAbyssAttackEntry> attacks;

         public EyeOfAbyssAttack(int actionSN, List<EyeOfAbyssAttackEntry> attacks) {
            this.actionSN = actionSN;
            this.attacks = attacks;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(this.actionSN);
            packet.writeInt(this.attacks.size());
            this.attacks.forEach(a -> a.encode(packet));
         }
      }

      public static class EyeOfAbyssDoBlind extends KalosAction.AssaultAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.EyeOfTheAbyssDoBlind;
         int actionSN;
         int playerID;

         public EyeOfAbyssDoBlind(int actionSN, int playerID) {
            this.actionSN = actionSN;
            this.playerID = playerID;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(this.actionSN);
            packet.writeInt(this.playerID);
         }
      }

      public static class EyeOfRedemption extends KalosAction.AssaultAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.EyeOfRedemption;
         final GuardianType guardianType = GuardianType.EyeOfRedemption;
         objects.fields.child.karrotte.guardian.EyeOfRedemption eyeOfRedemption;

         public EyeOfRedemption(objects.fields.child.karrotte.guardian.EyeOfRedemption eyeOfRedemption) {
            this.eyeOfRedemption = eyeOfRedemption;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            this.eyeOfRedemption.encode(packet);
         }
      }

      public static class EyeOfRedemptionAttack extends KalosAction.AssaultAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.EyeOfRedemptionAttack;
         int actionSN;
         List<Point> position;

         public EyeOfRedemptionAttack(int actionSN, List<Point> position) {
            this.actionSN = actionSN;
            this.position = position;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(this.actionSN);
            packet.writeInt(this.position.size());
            this.position.forEach(p -> {
               packet.writeInt(p.x);
               packet.writeInt(p.y);
            });
         }
      }

      public static class EyeOfRedemptionDeactive extends KalosAction.AssaultAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.EyeOfRedemptionDeactive;

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
         }
      }

      public static class EyeOfRedemptionStatus extends KalosAction.AssaultAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.EyeOfRedemptionStatus;
         int playerID;
         int remainHit;

         public EyeOfRedemptionStatus(int playerID, int remainHit) {
            this.playerID = playerID;
            this.remainHit = remainHit;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(this.playerID);
            packet.writeInt(this.remainHit);
         }
      }

      public static class EyeOfTheAbyssDeactive extends KalosAction.AssaultAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.EyeOfTheAbyssDeactive;

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
         }
      }

      public static class EyeOfTheAbyssStatus extends KalosAction.AssaultAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.EyeOfTheAbyssStatus;
         int playerID;
         int remainHit;

         public EyeOfTheAbyssStatus(int playerID, int remainHit) {
            this.playerID = playerID;
            this.remainHit = remainHit;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(this.playerID);
            packet.writeInt(this.remainHit);
         }
      }

      public static class FighterPlane extends KalosAction.AssaultAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.FighterPlane;
         int actionSN;
         int unk4;
         objects.fields.child.karrotte.guardian.FighterPlane fighterPlane;

         public FighterPlane(int actionSN, int unk4, objects.fields.child.karrotte.guardian.FighterPlane fighterPlane) {
            this.actionSN = actionSN;
            this.unk4 = unk4;
            this.fighterPlane = fighterPlane;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(this.actionSN);
            packet.writeInt(this.unk4);
            this.fighterPlane.encode(packet);
         }
      }

      public static class FighterPlaneDeactive extends KalosAction.AssaultAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.FighterPlaneDeactive;
         Point pos;

         public FighterPlaneDeactive(Point pos) {
            this.pos = pos;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(this.pos.x);
            packet.writeInt(this.pos.y);
         }
      }

      public static class FighterPlaneStatus extends KalosAction.AssaultAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.FighterPlaneStatus;
         int pos;
         int toRemove;
         int remain;

         public FighterPlaneStatus(int pos, int toRemove, int remain) {
            this.pos = pos;
            this.toRemove = toRemove;
            this.remain = remain;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(this.pos);
            packet.writeInt(this.toRemove);
            packet.writeInt(this.remain);
         }
      }

      public static class SphereOfOdium extends KalosAction.AssaultAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.SphereOfOdium;
         objects.fields.child.karrotte.guardian.SphereOfOdium sphereOfOdium;

         public SphereOfOdium(objects.fields.child.karrotte.guardian.SphereOfOdium sphereOfOdium) {
            this.sphereOfOdium = sphereOfOdium;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            this.sphereOfOdium.encode(packet);
         }
      }

      public static class SphereOfOdiumDeactive extends KalosAction.AssaultAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.SphereOfOdiumDeactive;

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
         }
      }

      public static class SphereOfOdiumStatus extends KalosAction.AssaultAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.SphereOfOdiumStatus;
         int pos;
         int toRemove;
         int remain;

         public SphereOfOdiumStatus(int pos, int toRemove, int remain) {
            this.pos = pos;
            this.toRemove = toRemove;
            this.remain = remain;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(this.pos);
            packet.writeInt(this.toRemove);
            packet.writeInt(this.remain);
         }
      }
   }

   public static class CursePacket extends KalosAction {
      final KalosActionType type = KalosActionType.CurseAction;

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.type.getType());
      }

      public static class CurseState extends KalosAction.InitPacket {
         final KalosActionSubType.Send type = KalosActionSubType.Send.CurseState;
         int partyID;
         List<Pair<Integer, Integer>> party = new ArrayList<>();
         int maxCurse;

         public CurseState(int partyID, List<Pair<Integer, Integer>> party, int maxCurse) {
            this.partyID = partyID;
            this.party = party;
            this.maxCurse = maxCurse;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(this.partyID);
            packet.writeInt(this.party.size());

            for (Pair<Integer, Integer> pair : this.party) {
               packet.writeInt(pair.left);
               packet.writeInt(pair.right);
               packet.writeInt(this.maxCurse);
            }
         }
      }
   }

   public static class GuardianAction extends KalosAction {
      final KalosActionType type = KalosActionType.GuardianAction;

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.type.getType());
      }

      public static class AssaultGuardian extends KalosAction.GuardianAction {
         private GuardianType guardianType;
         private int assaultInterval;

         public AssaultGuardian(GuardianType guardianType, int assaultInterval) {
            this.guardianType = guardianType;
            this.assaultInterval = assaultInterval;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.guardianType.getType());
            packet.writeInt(this.assaultInterval);
         }
      }

      public static class CreateGuardian extends KalosAction.GuardianAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.CreateGuardian;
         List<GuardianEntry> guardians = new ArrayList<>();

         public CreateGuardian(List<GuardianEntry> guardians) {
            this.guardians = guardians;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(60000);
            packet.writeInt(150000);
            packet.writeInt(this.guardians.size());
            this.guardians.forEach(g -> {
               packet.writeInt(g.getIndex());
               packet.writeInt(g.getPosition().x);
               packet.writeInt(g.getPosition().y);
            });
            packet.writeInt(this.guardians.size());
            this.guardians.forEach(g -> {
               packet.writeInt(g.getIndex());
               packet.write(g.getUnk());
            });
         }
      }
   }

   public static class InitPacket extends KalosAction {
      final KalosActionType type = KalosActionType.Init;

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.type.getType());
      }

      public static class CreateLiberationBottom extends KalosAction.InitPacket {
         final KalosActionSubType.Send type = KalosActionSubType.Send.LiberationBottom;
         final int REACTIVE_TIME = 10000;
         final int UNK_TIME = 1780;
         List<Point> points = new ArrayList<>();

         public CreateLiberationBottom(int phase) {
            if (phase == 1) {
               this.points.add(new Point(1690, 534));
               this.points.add(new Point(1209, 534));
               this.points.add(new Point(609, 534));
               this.points.add(new Point(9, 534));
               this.points.add(new Point(-609, 534));
            }

            if (phase == 2) {
               this.points.add(new Point(1809, 510));
               this.points.add(new Point(1209, 510));
               this.points.add(new Point(609, 510));
               this.points.add(new Point(9, 510));
               this.points.add(new Point(-738, 510));
            }
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(10000);
            packet.writeInt(1780);
            packet.writeInt(this.points.size());
            this.points.forEach(p -> {
               packet.writeInt(p.x);
               packet.writeInt(p.y);
            });
         }
      }

      public static class CreateLiberationTop extends KalosAction.InitPacket {
         final KalosActionSubType.Send type = KalosActionSubType.Send.LiberationTop;
         final int REACTIVE_TIME = 10000;
         final int START_POS_X = -800;
         final int POS_Y = -800;
         final int POS_DELTA = 400;
         final int DRONE_SIZE = 7;

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(10000);
            packet.writeInt(7);

            for (int i = 0; i < 7; i++) {
               packet.writeInt(-800 + i * 400);
               packet.writeInt(-800);
            }
         }
      }

      public static class CreateMysticShot extends KalosAction.InitPacket {
         final KalosActionSubType.Send type = KalosActionSubType.Send.CreateMysticShot;
         MysticShot mysticShot;

         public CreateMysticShot(MysticShot mysticShot) {
            this.mysticShot = mysticShot;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            this.mysticShot.encode(packet);
         }
      }

      public static class Gauge extends KalosAction.InitPacket {
         final KalosActionSubType.Send type = KalosActionSubType.Send.KalosWill;
         int chargeDelay;
         int chargeValue;
         int maxValue;

         public Gauge(int chargeDelay, int chargeValue, int maxValue) {
            this.chargeDelay = chargeDelay;
            this.chargeValue = chargeValue;
            this.maxValue = maxValue;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(this.chargeDelay);
            packet.writeInt(this.chargeValue);
            packet.writeInt(this.maxValue);
            packet.writeInt(this.maxValue);
         }
      }

      public static class HitMysticShotBottom extends KalosAction.InitPacket {
         final KalosActionSubType.Send type = KalosActionSubType.Send.HitMysticShotBottom;
         int playerID;
         int objectID;

         public HitMysticShotBottom(int playerID, int objectID) {
            this.playerID = playerID;
            this.objectID = objectID;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(this.playerID);
            packet.writeInt(this.objectID);
         }
      }

      public static class HitMysticShotTop extends KalosAction.InitPacket {
         final KalosActionSubType.Send type = KalosActionSubType.Send.HitMysticShotTop;
         int playerID;
         int objectID;

         public HitMysticShotTop(int playerID, int objectID) {
            this.playerID = playerID;
            this.objectID = objectID;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(this.playerID);
            packet.writeInt(this.objectID);
         }
      }
   }

   public static class Phase2KalosAction extends KalosAction {
      final KalosActionType type = KalosActionType.Phase2KalosAction;

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.type.getType());
      }

      public static class FuryOfTheGuardianEnd extends KalosAction.Phase2KalosAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.FuryOfTheGuardianEnd;

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
         }
      }

      public static class FuryOfTheGuardianSafetyZoneGen extends KalosAction.Phase2KalosAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.FuryOfTheGuardianSafetyZoneGen;

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
         }
      }

      public static class FuryOfTheGuardianSafetyZoneSet extends KalosAction.Phase2KalosAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.FuryOfTheGuardianSafetyZoneSet;
         int refMobID;
         int skillID;
         List<Point> pos = new ArrayList<>();

         public FuryOfTheGuardianSafetyZoneSet(int refMobID, int skillID) {
            this.refMobID = refMobID;
            this.skillID = skillID;
            if (this.pos.isEmpty()) {
               this.pos.add(new Point(900, -224));
               this.pos.add(new Point(1326, 79));
               this.pos.add(new Point(570, 391));
               this.pos.add(new Point(87, -224));
               this.pos.add(new Point(-192, 79));
            }

            Collections.shuffle(this.pos);
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(this.refMobID);
            packet.writeInt(this.skillID);
            packet.writeInt(5);
            packet.writeInt(2610);
            packet.writeInt(2700);
            packet.writeInt(3000);
            packet.writeInt(this.pos.size());

            for (Point pos_ : this.pos) {
               packet.encodePos4Byte(pos_);
            }
         }
      }

      public static class FuryOfTheGuardianStart extends KalosAction.Phase2KalosAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.FuryOfTheGuardianStart;
         int objID;
         int mobID;

         public FuryOfTheGuardianStart(int objID, int mobID) {
            this.objID = objID;
            this.mobID = mobID;
         }

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
            packet.writeInt(this.objID);
            packet.writeMapleAsciiString("Mob/" + this.mobID + ".img/skill2");
            packet.writeMapleAsciiString("Mob/" + this.mobID + ".img/skill3");
            packet.writeMapleAsciiString("Mob/" + this.mobID + ".img/skill7");
            packet.writeMapleAsciiString("Mob/" + this.mobID + ".img/skill8");
            packet.writeMapleAsciiString("");
            packet.writeInt(0);
            packet.writeInt(600);
            packet.writeInt(570);
            packet.writeInt(83);
         }
      }

      public static class FuryOfTheGuardianSuccess extends KalosAction.Phase2KalosAction {
         final KalosActionSubType.Send type = KalosActionSubType.Send.FuryOfTheGuardianSuccess;

         @Override
         public void encode(PacketEncoder packet) {
            super.encode(packet);
            packet.writeInt(this.type.getType());
         }
      }
   }
}
