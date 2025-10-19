package network.game.processors;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.decode.PacketDecoder;
import objects.fields.AnimatedMapleMapObject;
import objects.movepath.AbsoluteLifeMovement;
import objects.movepath.AranMovement;
import objects.movepath.ChairMovement;
import objects.movepath.ChangeEquipSpecialAwesome;
import objects.movepath.LifeMovement;
import objects.movepath.LifeMovementFragment;
import objects.movepath.MPA_INFO;
import objects.movepath.RelativeLifeMovement;
import objects.movepath.SunknownMovement;
import objects.movepath.TunknownMovement;
import objects.movepath.UnknownMovement;
import objects.movepath.UnknownMovement2;
import objects.movepath.UunknownMovement;
import objects.users.MapleCharacter;

public class MovementParse {
   public static List<LifeMovementFragment> parseMovement(PacketDecoder lea) {
      return parseMovement(lea, null);
   }

   public static List<LifeMovementFragment> parseMovement(PacketDecoder rh, MapleCharacter chr) {
      List<LifeMovementFragment> res = new ArrayList<>();
      int numCommands = rh.readShort();

      for (int i = 0; i < numCommands; i++) {
         short command = rh.readByte();
         short nAttr = 0;
         switch (command) {
            case 0:
            case 8:
            case 15:
            case 17:
            case 19:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 91:
            case 105: {
               short xpos = rh.readShort();
               short ypos = rh.readShort();
               short xwobble = rh.readShort();
               short ywobble = rh.readShort();
               short fh = rh.readShort();
               if (command == 15 || command == 17) {
                  nAttr = rh.readShort();
               }

               short xoffset = rh.readShort();
               short yoffset = rh.readShort();
               short unk = rh.readShort();
               byte newstate = rh.readByte();
               short duration = rh.readShort();
               byte ForcedStop_CS = rh.readByte();
               AbsoluteLifeMovement alm = new AbsoluteLifeMovement(command, new Point(xpos, ypos), duration, newstate, fh);
               alm.setnAttr(nAttr);
               alm.setUnk(unk);
               alm.setPixelsPerSecond(new Point(xwobble, ywobble));
               alm.setOffset(new Point(xoffset, yoffset));
               alm.setForcedStop_CS(ForcedStop_CS);
               res.add(alm);
               break;
            }
            case 1:
            case 2:
            case 18:
            case 21:
            case 22:
            case 24:
            case 60:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 96: {
               short xmod = rh.readShort();
               short ymod = rh.readShort();
               if (command == 21 || command == 22) {
                  nAttr = rh.readShort();
               }

               short n1 = 0;
               short n2 = 0;
               if (command == 60) {
                  n1 = rh.readShort();
                  n2 = rh.readShort();
               }

               byte newstate = rh.readByte();
               short duration = rh.readShort();
               byte ForcedStop = rh.readByte();
               RelativeLifeMovement rlm = new RelativeLifeMovement(command, new Point(xmod, ymod), duration, newstate);
               rlm.setAttr(nAttr);
               rlm.setForcedStop(ForcedStop);
               rlm.setN1(n1);
               rlm.setN2(n2);
               res.add(rlm);
               break;
            }
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            case 11:
            case 13:
            case 26:
            case 27:
            case 52:
            case 53:
            case 54:
            case 80:
            case 81:
            case 82:
            case 84:
            case 86: {
               short xpos = rh.readShort();
               short ypos = rh.readShort();
               short fh = rh.readShort();
               int unk = rh.readInt();
               byte newstate = rh.readByte();
               short duration = rh.readShort();
               byte ForcedStop_CS = rh.readByte();
               ChairMovement cm = new ChairMovement(command, new Point(xpos, ypos), duration, newstate, fh, unk);
               cm.setForcedStop_CS(ForcedStop_CS);
               res.add(cm);
               break;
            }
            case 12:
               res.add(new ChangeEquipSpecialAwesome(command, rh.readByte()));
               break;
            case 14:
            case 16: {
               short xpos = rh.readShort();
               short ypos = rh.readShort();
               nAttr = rh.readShort();
               byte newstate = rh.readByte();
               short duration = rh.readShort();
               byte force = rh.readByte();
               SunknownMovement sum = new SunknownMovement(command, new Point(xpos, ypos), duration, newstate);
               sum.setAttr(nAttr);
               sum.setForce(force);
               res.add(sum);
               break;
            }
            case 20:
            case 25:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 50:
            case 51:
            case 55:
            case 57:
            case 58:
            case 59:
            case 61:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 83:
            case 85:
            case 87:
            case 88:
            case 89:
            case 90:
            case 92:
            case 93:
            case 94:
            case 95:
            case 97:
            case 98:
            case 101:
            case 102:
            case 103:
            case 104:
            default:
               if (command != 77 && command != 79) {
                  byte newstatex = rh.readByte();
                  short durationx = rh.readShort();
                  byte ForcedStop = rh.readByte();
                  AranMovement am = new AranMovement(command, new Point(0, 0), durationx, newstatex);
                  am.setForcedStop(ForcedStop);
                  res.add(am);
               } else {
                  MPA_INFO p = new MPA_INFO(rh.readShort(), rh.readShort(), rh.readShort(), rh.readShort(), rh.readShort(), rh.readShort(), rh.readShort());
                  res.add(p);
               }
               break;
            case 23:
            case 99:
            case 100: {
               short xpos = rh.readShort();
               short ypos = rh.readShort();
               short xoffset = rh.readShort();
               short yoffset = rh.readShort();
               byte newstate = rh.readByte();
               short duration = rh.readShort();
               byte force = rh.readByte();
               TunknownMovement tum = new TunknownMovement(command, new Point(xpos, ypos), duration, newstate);
               tum.setOffset(new Point(xoffset, yoffset));
               tum.setForce(force);
               res.add(tum);
               break;
            }
            case 28:
            case 29:
            case 41: {
               int attr = rh.readInt();
               byte newstate = rh.readByte();
               short duration = rh.readShort();
               byte force = rh.readByte();
               UunknownMovement uum = new UunknownMovement(command, attr, newstate, duration, force);
               uum.setForcedStop(force);
               res.add(uum);
               break;
            }
            case 49:
               res.add(
                  new UnknownMovement2(
                     command, rh.readShort(), rh.readShort(), rh.readShort(), rh.readShort(), rh.readShort(), rh.readByte(), rh.readShort(), rh.readByte()
                  )
               );
               break;
            case 56:
            case 68: {
               short xpos = rh.readShort();
               short ypos = rh.readShort();
               short xwobble = rh.readShort();
               short ywobble = rh.readShort();
               short fh = rh.readShort();
               byte newstate = rh.readByte();
               short duration = rh.readShort();
               byte force = rh.readByte();
               UnknownMovement um = new UnknownMovement(command, new Point(xpos, ypos), duration, newstate, fh, force);
               um.setPixelsPerSecond(new Point(xwobble, ywobble));
               res.add(um);
            }
         }
      }

      return numCommands != res.size() ? null : res;
   }

   public static void updatePosition(List<LifeMovementFragment> movement, AnimatedMapleMapObject target, int yoffset) {
      if (movement != null) {
         for (LifeMovementFragment move : movement) {
            if (move instanceof LifeMovement) {
               if (move instanceof AbsoluteLifeMovement) {
                  Point position = ((LifeMovement)move).getPosition();
                  position.y += yoffset;
                  target.setPosition(position);
               }

               if (!(move instanceof MPA_INFO)) {
                  target.setStance(((LifeMovement)move).getNewstate());
               }
            }
         }
      }
   }
}
