package objects.fields.child.minigame.battlereverse;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.PacketHelper;
import objects.users.MapleCharacter;
import objects.utils.Pair;

public class BattleReversePacket {
   public static byte[] OpenBattleReverseUI(BattleReverseGameInfo gameInfo, MapleCharacter chr) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      packet.writeInt(20);
      packet.write(19);
      packet.write(2);
      packet.write(1);
      int team = gameInfo.getTeamByChr(chr);
      int otherteam = team == 1 ? 0 : 1;
      MapleCharacter left = gameInfo.getCharacter(otherteam);
      packet.write(0);
      PacketHelper.addCharLook(packet, left, false, false);
      packet.writeMapleAsciiString(left.getName());
      packet.writeShort(left.getJob());
      packet.writeInt(0);
      MapleCharacter right = gameInfo.getCharacter(team);
      packet.write(1);
      PacketHelper.addCharLook(packet, right, false, false);
      packet.writeMapleAsciiString(right.getName());
      packet.writeShort(right.getJob());
      packet.writeInt(0);
      packet.write(255);
      return packet.getPacket();
   }

   public static byte[] StartBattleReverse(BattleReverseGameInfo gameInfo, MapleCharacter chr) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      packet.writeInt(96);
      packet.writeInt(0);
      packet.writeInt(10000);
      packet.writeInt(15);
      EncodeBoard(packet, gameInfo, chr);
      return packet.getPacket();
   }

   public static byte[] EndBattleReverse(int result) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      packet.writeInt(189);
      packet.writeInt(result);
      return packet.getPacket();
   }

   public static byte[] StartBattleReverseStone(BattleReverseGameInfo gameInfo, int team, MapleCharacter chr) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      packet.writeInt(185);
      packet.write(0);
      boolean chipreverse;
      if (chr.getMiniGameTeam() == 1) {
         chipreverse = true;
      } else {
         chipreverse = false;
      }

      byte[][] board = gameInfo.getBoard();
      int putx = 0;
      int puty = 0;
      boolean find = false;

      for (int x = 0; x < 8 && !find; x++) {
         for (int y = 0; y < 8 && !find; y++) {
            if (team == 1 && board[x][y] == 0) {
               putx = x;
               puty = y;
               find = true;
            }

            if (team == 0 && board[x][y] == 1) {
               putx = x;
               puty = y;
               find = true;
            }
         }
      }

      if (team == 0) {
         packet.writeInt(putx);
         packet.writeInt(puty);
         packet.writeInt(chipreverse ? 0 : 1);
      } else {
         packet.writeInt(putx);
         packet.writeInt(puty);
         packet.writeInt(chipreverse ? 1 : 0);
      }

      packet.write(1);
      EncodeBoard(packet, gameInfo, chr);
      packet.writeInt(gameInfo.getTeamHP(team == 0 ? 0 : 1));
      return packet.getPacket();
   }

   public static byte[] PutBattleReverseStone(BattleReverseGameInfo gameInfo, Point stone, int team, MapleCharacter chr) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
      packet.writeInt(185);
      packet.write(0);
      boolean chipreverse;
      if (chr.getMiniGameTeam() == 1) {
         chipreverse = true;
      } else {
         chipreverse = false;
      }

      if (team == 0) {
         packet.writeInt(stone.x);
         packet.writeInt(stone.y);
         packet.writeInt(chipreverse ? 1 : 0);
      } else {
         packet.writeInt(stone.x);
         packet.writeInt(stone.y);
         packet.writeInt(chipreverse ? 0 : 1);
      }

      packet.write(1);
      EncodeBoard(packet, gameInfo, chr);
      packet.writeInt(gameInfo.getTeamHP(team == 0 ? 0 : 1));
      return packet.getPacket();
   }

   private static void EncodeBoard(PacketEncoder packet, BattleReverseGameInfo gameInfo, MapleCharacter chr) {
      byte[][] board = gameInfo.getBoard();
      List<Pair<Point, Byte>> chips = new ArrayList<>();

      for (int x = 0; x < 8; x++) {
         for (int y = 0; y < 8; y++) {
            if (board[x][y] != -1) {
               chips.add(new Pair<>(new Point(x, y), board[x][y]));
            }
         }
      }

      boolean chipreverse;
      if (chr.getMiniGameTeam() == 1) {
         chipreverse = true;
      } else {
         chipreverse = false;
      }

      int count = chips.size();
      packet.writeInt(count);

      for (Pair<Point, Byte> chip : chips) {
         Point xy = chip.left;
         Byte chiptype = chip.right;
         packet.writeInt(xy.x);
         packet.writeInt(xy.y);
         if (chipreverse) {
            if (chiptype == 0) {
               packet.writeInt(1);
            } else if (chiptype == 1) {
               packet.writeInt(0);
            } else {
               packet.writeInt(chiptype);
            }
         } else {
            packet.writeInt(chiptype);
         }
      }
   }

   public class BattleReverseGameEndType {
      public static final int LOSE = 0;
      public static final int WIN = 1;
      public static final int DRAW = 2;
   }

   private class MiniGameAction {
      private static final int StartBattleReverse = 96;
      private static final int PutBattleReverseStone = 185;
      private static final int EndBattleReverse = 189;
   }

   private class PlayerInterAction {
      private static final int OpenBattleReverse = 20;
   }
}
