package network.game.processors;

import java.awt.Point;
import java.util.List;
import network.decode.PacketDecoder;
import network.models.CField;
import network.models.FontColorType;
import network.models.FontType;
import objects.fields.child.minigame.battlereverse.BattleReverseGameInfo;
import objects.fields.child.minigame.battlereverse.BattleReversePacket;
import objects.fields.child.minigame.battlereverse.Field_BattleReverse;
import objects.users.MapleCharacter;
import objects.users.MapleClient;

public class MiniGameActionHandler {
   public static final void MiniGameAction(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      int actionvalue = slea.readInt();
      MiniGameActionHandler.Interaction action = MiniGameActionHandler.Interaction.getByAction(actionvalue);
      if (c != null && chr != null) {
         if (chr.getMap() instanceof Field_BattleReverse) {
            if (action == null) {
               System.out.println("Unhandled interaction action by " + chr.getName() + " : " + actionvalue + ", " + slea.toString());
            } else if (c.getChannelServer().getPlayerStorage().getCharacterById(c.getPlayer().getId()) != null) {
               c.getPlayer().setScrolledPosition((short)0);
               switch (action) {
                  case BATTLEREVERSE_START_GAME:
                     Field_BattleReverse fbrx = (Field_BattleReverse)chr.getMap();
                     int team = fbrx.getBattleReverseGameDlg().getGameInfo().getTeamByChr(chr);
                     if (team == 0) {
                        BattleReverseGameInfo gameInfox = fbrx.getBattleReverseGameDlg().getGameInfo();
                        gameInfox.InitBoard();

                        for (MapleCharacter player : chr.getMap().getCharacters()) {
                           player.send(BattleReversePacket.StartBattleReverse(gameInfox, player));
                        }

                        chr.send(BattleReversePacket.StartBattleReverseStone(gameInfox, 0, chr));
                     }
                     break;
                  case BATTLEREVERSE_PUT_STONE:
                     int x = slea.readInt();
                     int y = slea.readInt();
                     Field_BattleReverse fbr = (Field_BattleReverse)chr.getMap();
                     BattleReverseGameInfo gameInfo = fbr.getBattleReverseGameDlg().getGameInfo();
                     if (!gameInfo.isInit()) {
                        return;
                     }

                     team = gameInfo.getTeamByChr(chr);
                     if (gameInfo.getTurnTeam() != team) {
                        return;
                     }

                     List<Point> canputs = gameInfo.getPuttableList(team);
                     Point check = new Point(x, y);

                     for (Point pos : canputs) {
                        if (pos.equals(check)) {
                           gameInfo.ProcessChips(x, y, (byte)team);
                           MapleCharacter otherplayer = gameInfo.getCharacter(team == 1 ? 0 : 1);
                           chr.send(BattleReversePacket.PutBattleReverseStone(gameInfo, new Point(x, y), team == 1 ? 0 : 1, chr));
                           otherplayer.send(BattleReversePacket.PutBattleReverseStone(gameInfo, new Point(x, y), team == 1 ? 0 : 1, otherplayer));
                           gameInfo.nextTurnTeam();
                           byte[][] board = gameInfo.getBoard();
                           int chipcount = 0;
                           int freechip = 0;

                           for (int i = 0; i < 8; i++) {
                              for (int j = 0; j < 8; j++) {
                                 if (board[i][j] == (team == 0 ? 1 : 0)) {
                                    chipcount++;
                                 } else if (board[i][j] == -1) {
                                    freechip++;
                                 }
                              }
                           }

                           if (freechip == 0) {
                              int team0hp = gameInfo.getTeamHP(0);
                              int team1hp = gameInfo.getTeamHP(1);
                              if (team0hp == team1hp) {
                                 fbr.setWinTeam(2);
                                 fbr.setEndGame(true);
                              } else if (team0hp > team1hp) {
                                 fbr.setWinTeam(0);
                                 fbr.setEndGame(true);
                              } else {
                                 fbr.setWinTeam(1);
                                 fbr.setEndGame(true);
                              }
                           } else if (chipcount == 0) {
                              fbr.setWinTeam(team);
                              fbr.setEndGame(true);
                           } else if (gameInfo.getPuttableList(team == 0 ? 1 : 0).size() == 0) {
                              gameInfo.nextTurnTeam();
                              if (gameInfo.getPuttableList(team).size() == 0) {
                                 int team0hp = gameInfo.getTeamHP(0);
                                 int team1hp = gameInfo.getTeamHP(1);
                                 if (team0hp == team1hp) {
                                    fbr.setWinTeam(2);
                                    fbr.setEndGame(true);
                                 } else if (team0hp > team1hp) {
                                    fbr.setWinTeam(0);
                                    fbr.setEndGame(true);
                                 } else {
                                    fbr.setWinTeam(1);
                                    fbr.setEndGame(true);
                                 }

                                 return;
                              }

                              chr.send(CField.UIPacket.sendBigScriptProgressMessage("상대방이 놓을수 있는 곳이 없어 턴이 넘어옵니다.", FontType.NanumGothic, FontColorType.Yellow));
                              otherplayer.send(
                                 CField.UIPacket.sendBigScriptProgressMessage("놓을수 있는 곳이 없어 턴이 종료됩니다.", FontType.NanumGothic, FontColorType.Yellow)
                              );
                              chr.send(BattleReversePacket.StartBattleReverseStone(gameInfo, gameInfo.getTurnTeam(), chr));
                              otherplayer.send(BattleReversePacket.StartBattleReverseStone(gameInfo, gameInfo.getTurnTeam(), otherplayer));
                           }

                           return;
                        }
                     }
               }
            }
         }
      }
   }

   public static enum Interaction {
      BATTLEREVERSE_START_GAME(96),
      BATTLEREVERSE_PUT_STONE(185);

      public int action;

      private Interaction(int action) {
         this.action = action;
      }

      public static MiniGameActionHandler.Interaction getByAction(int i) {
         for (MiniGameActionHandler.Interaction s : values()) {
            if (s.action == i) {
               return s;
            }
         }

         return null;
      }
   }
}
