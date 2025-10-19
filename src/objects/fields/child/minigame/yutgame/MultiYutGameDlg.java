package objects.fields.child.minigame.yutgame;

import constants.ServerConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import network.encode.PacketEncoder;
import objects.fields.Field;
import objects.users.MapleCharacter;
import objects.utils.Pair;
import objects.utils.Randomizer;

public class MultiYutGameDlg {
   private YutGameResult_GameInfo[] gameInfo = new YutGameResult_GameInfo[2];
   private Field_MultiYutGame field = null;
   private List<YutGameResult_InstallItem> installItems = new ArrayList<>();
   private YutGameResult resultLog = new YutGameResult();

   public MultiYutGameDlg(Field field) {
      this.field = (Field_MultiYutGame)field;
   }

   public void init(List<MapleCharacter> players) {
      if (players.size() < 2) {
         for (MapleCharacter player : this.field.getCharactersThreadsafe()) {
            player.dropMessage(5, "게임에 필요한 인원수가 부족하여 마을로 이동됩니다.");
            player.setRegisterTransferField(ServerConstants.TownMap);
            player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
         }
      } else {
         int idx = 0;

         for (MapleCharacter player : this.field.getCharactersThreadsafe()) {
            if (idx >= 2) {
               player.setRegisterTransferField(15);
               player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
            } else {
               YutGameResult_GameInfo gameInfo = new YutGameResult_GameInfo();
               gameInfo.setPlayerID(player.getId());
               gameInfo.setTeam(idx);
               gameInfo.setCurrentYutInfo(new YutGameYutInfo());
               gameInfo.setSuperItem(new YutGameSuperItem());
               player.setMiniGameTeam(gameInfo.getTeam());
               YutGamePiece[] pieces = new YutGamePiece[YutGameResult_GameInfo.MAX_PIECE_COUNT];

               for (int i = 0; i < YutGameResult_GameInfo.MAX_PIECE_COUNT; i++) {
                  YutGamePiece piece = new YutGamePiece(i, idx);
                  pieces[i] = piece;
               }

               gameInfo.setPieces(pieces);
               this.gameInfo[idx++] = gameInfo;
            }
         }
      }
   }

   public void firstSetSuperItem() {
      if (this.field.getCharactersSize() < 2) {
         for (MapleCharacter player : this.field.getCharactersThreadsafe()) {
            player.dropMessage(5, "게임에 필요한 인원수가 부족하여 마을로 이동됩니다.");
            player.setRegisterTransferField(ServerConstants.TownMap);
            player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
         }
      } else {
         int team0YutItem = Randomizer.rand(0, 7);
         int team0PieceItem = Randomizer.rand(0, 7);
         int team1YutItem = 0;
         int team1PieceItem = 0;

         do {
            team1YutItem = Randomizer.rand(0, 7);
         } while (team0YutItem == team1YutItem);

         do {
            team1PieceItem = Randomizer.rand(0, 7);
         } while (team0PieceItem == team1PieceItem);

         for (YutGameResult_GameInfo info : this.gameInfo) {
            if (info.getTeam() == 0) {
               info.getSuperItem().setYutItem(YutGameSuperItem.YutItemType.getType(team0YutItem));
               info.getSuperItem().setPieceItem(YutGameSuperItem.PieceItemType.getType(team0PieceItem));
               info.setNextTurn(true);
            } else {
               info.getSuperItem().setYutItem(YutGameSuperItem.YutItemType.getType(team1YutItem));
               info.getSuperItem().setPieceItem(YutGameSuperItem.PieceItemType.getType(team1PieceItem));
            }
         }

         this.resultLog.addResult(new YutGameResult_NextTurn(0));

         for (YutGameResult_GameInfo infox : this.gameInfo) {
            this.resultLog.addResult(infox);
         }

         YutGameResult_Action.ActionEntry e = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.Unk, -1);
         this.resultLog.addResult(new YutGameResult_Action(e));
         YutGameResult_Action.ActionEntry entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.GameSet, 0);
         entry.addAdditional(YutGameResult_Action.GameSetType.FirstItemSet.getType());
         entry.addAdditional(team0YutItem);
         entry.addAdditional(team0PieceItem);
         this.resultLog.addResult(new YutGameResult_Action(entry));
         YutGameResult_Action.ActionEntry entry1 = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.GameSet, 1);
         entry1.addAdditional(YutGameResult_Action.GameSetType.FirstItemSet.getType());
         entry1.addAdditional(team1YutItem);
         entry1.addAdditional(team1PieceItem);
         this.resultLog.addResult(new YutGameResult_Action(entry1));
         this.commitResultLog();
      }
   }

   public void commitResultLog() {
      PacketEncoder packet = new PacketEncoder();
      this.resultLog.sortingLog();
      this.resultLog.encode(packet);
      this.field.broadcastMessage(packet.getPacket());
      this.resultLog.clearResult();
   }

   public void updateGameInfo() {
      for (YutGameResult_GameInfo info : this.gameInfo) {
         this.resultLog.addResult(info);
      }

      this.commitResultLog();
   }

   public void nextTurn() {
      this.nextTurn(true);
   }

   public void gameResult(int winTeam) {
      YutGameResult_Action.ActionEntry entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.GameResult, winTeam);
      entry.addAdditional(1);
      this.resultLog.addResult(new YutGameResult_Action(entry));
      entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.GameResult, winTeam ^ 1);
      entry.addAdditional(0);
      this.resultLog.addResult(new YutGameResult_Action(entry));
      this.commitResultLog();
   }

   public void nextTurn(boolean commit) {
      YutGameResult_Action.ActionEntry entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.RemainTurn, -1);
      entry.addAdditional(20000);
      this.field.setNextTimeOutTurn(System.currentTimeMillis() + 20000L);
      this.resultLog.addResult(new YutGameResult_Action(entry));
      if (commit) {
         this.commitResultLog();
      }
   }

   public void doGoHome(YutGameResult_GameInfo gameInfo, int pieceIndex) {
      YutGamePiece piece = gameInfo.getPiece(pieceIndex);
      if (piece != null) {
         List<YutGamePiece> samePos = gameInfo.getPieceByPosition(piece.getPosition());
         if (!samePos.isEmpty()) {
            for (YutGamePiece p : samePos) {
               p.setPosition(0);
               p.setHidden(false);
               p.clearCarryIndex();
            }
         }

         piece.setPosition(0);
         piece.setHidden(false);
         piece.clearCarryIndex();
         this.resultLog.addResult(gameInfo);
         this.nextTurn();
         YutGameResult_Action.ActionEntry entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.NextTurn, gameInfo.getTeam() ^ 1);
         this.resultLog.addResult(new YutGameResult_Action(entry));
         this.resultLog.addResult(new YutGameResult_NextTurn(gameInfo.getTeam() ^ 1));
         this.commitResultLog();
      }
   }

   public YutGameResult_InstallItem getInstallItem(YutGameResult_Action.InstallItemType type) {
      for (YutGameResult_InstallItem installItem : this.installItems) {
         if (installItem.getItemType() == type) {
            return installItem;
         }
      }

      return null;
   }

   public void installItem(YutGameResult_Action.InstallItemType type, int position) {
      YutGameResult_InstallItem item = new YutGameResult_InstallItem(position, type, true);
      this.installItems.add(item);
      YutGameResult_Action.ActionEntry entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.InstallItem, -1);
      entry.addAdditional(position);
      entry.addAdditional(type.getType());
      this.resultLog.addResult(new YutGameResult_Action(entry));
      this.resultLog.addResult(item);
   }

   public void removeInstallItem(int position) {
      YutGameResult_InstallItem item = null;

      for (YutGameResult_InstallItem i : this.installItems) {
         if (i.getPosition() == position) {
            item = i;
            break;
         }
      }

      if (item != null) {
         this.installItems.remove(item);
         item.setCreate(false);
         this.resultLog.addResult(item);
      }
   }

   public void doCarryAndGo(YutGameResult_GameInfo gameInfo, int piece1, int piece2) {
      YutGamePiece piece_1 = gameInfo.getPiece(piece1);
      YutGamePiece piece_2 = gameInfo.getPiece(piece2);
      piece_1.setPosition(piece_2.getPosition());
      piece_1.setHidden(true);
      piece_2.addCarryIndex(piece1);
      this.resultLog.addResult(gameInfo);
      YutGameResult_Action.ActionEntry entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.NextTurn, gameInfo.getTeam());
      this.resultLog.addResult(new YutGameResult_Action(entry));
      this.resultLog.addResult(new YutGameResult_NextTurn(gameInfo.getTeam()));
      this.commitResultLog();
   }

   public void doChangePosition(YutGameResult_GameInfo gameInfo, int piece1, int piece2) {
      YutGameResult_GameInfo info = this.getGameInfo(gameInfo.getTeam() ^ 1);
      YutGamePiece piece_1 = gameInfo.getPiece(piece1);
      if (piece_1 != null) {
         YutGamePiece piece_2 = info.getPiece(piece2);
         if (piece_2 != null) {
            List<YutGamePiece> samePos = gameInfo.getPieceByPosition(piece_1.getPosition());
            int temp = piece_1.getPosition();
            piece_1.setPosition(piece_2.getPosition());

            for (YutGamePiece p : samePos) {
               p.setPosition(piece_2.getPosition());
            }

            samePos = info.getPieceByPosition(piece_2.getPosition());
            piece_2.setPosition(temp);

            for (YutGamePiece p : samePos) {
               p.setPosition(temp);
            }

            this.resultLog.addResult(gameInfo);
            this.resultLog.addResult(info);
            YutGameResult_Action.ActionEntry entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.NextTurn, gameInfo.getTeam());
            this.resultLog.addResult(new YutGameResult_Action(entry));
            this.resultLog.addResult(new YutGameResult_NextTurn(gameInfo.getTeam()));
            this.commitResultLog();
         }
      }
   }

   public void doGoMyFront(YutGameResult_GameInfo gameInfo, int pieceIndex, int position) {
      YutGamePiece piece = gameInfo.getPiece(pieceIndex);
      if (piece != null) {
         if (piece.getPosition() > 0) {
            for (YutGamePiece p : gameInfo.getPieceByPosition(piece.getPosition())) {
               p.setPosition(position);
            }
         }

         piece.setPosition(position);
         this.resultLog.addResult(gameInfo);
         YutGameResult_Action.ActionEntry entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.NextTurn, gameInfo.getTeam() ^ 1);
         this.resultLog.addResult(new YutGameResult_Action(entry));
         this.resultLog.addResult(new YutGameResult_NextTurn(gameInfo.getTeam() ^ 1));
         this.commitResultLog();
      }
   }

   public void doBackHug(YutGameResult_GameInfo gameInfo, int pieceIndex, int position) {
      YutGamePiece piece = gameInfo.getPiece(pieceIndex);
      if (piece != null) {
         if (piece.getPosition() > 0) {
            for (YutGamePiece p : gameInfo.getPieceByPosition(piece.getPosition())) {
               p.setPosition(position);
            }
         }

         piece.setPosition(position);
         this.resultLog.addResult(gameInfo);
         YutGameResult_Action.ActionEntry entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.NextTurn, gameInfo.getTeam());
         this.resultLog.addResult(new YutGameResult_Action(entry));
         this.resultLog.addResult(new YutGameResult_NextTurn(gameInfo.getTeam()));
         this.commitResultLog();
      }
   }

   public void doFinishLineFront(YutGameResult_GameInfo gameInfo, int position) {
      YutGameResult_InstallItem item = this.getInstallItem(YutGameResult_Action.InstallItemType.FinishLineFront);
      if (item == null) {
         this.installItem(YutGameResult_Action.InstallItemType.FinishLineFront, position);
         YutGameResult_Action.ActionEntry entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.NextTurn, gameInfo.getTeam());
         this.resultLog.addResult(new YutGameResult_Action(entry));
         this.resultLog.addResult(new YutGameResult_NextTurn(gameInfo.getTeam()));
         this.commitResultLog();
      }
   }

   public void doInstallBomb(YutGameResult_GameInfo gameInfo, int position) {
      YutGameResult_InstallItem item = this.getInstallItem(YutGameResult_Action.InstallItemType.Bomb);
      if (item == null) {
         this.installItem(YutGameResult_Action.InstallItemType.Bomb, position);
         YutGameResult_Action.ActionEntry entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.NextTurn, gameInfo.getTeam());
         this.resultLog.addResult(new YutGameResult_Action(entry));
         this.resultLog.addResult(new YutGameResult_NextTurn(gameInfo.getTeam()));
         this.commitResultLog();
      }
   }

   public void doReverseCell(YutGameResult_GameInfo gameInfo) {
      List<Integer> pickList = new ArrayList<>();
      YutGameResult_GameInfo info = this.getGameInfo(gameInfo.getTeam() ^ 1);
      if (info != null) {
         List<YutGamePiece> list = new ArrayList<>();
         Arrays.stream(info.getPieces()).forEach(list::add);
         Arrays.stream(gameInfo.getPieces()).forEach(list::add);

         for (YutGamePiece piece : list) {
            if (!piece.isHidden() && piece.getPosition() != 0 && piece.getPosition() != 30) {
               int count = 0;

               while (true) {
                  Integer pick = Randomizer.rand(0, 29);
                  YutGameResult_GameInfo g = this.getGameInfo(piece.getTeam());
                  if (g == null) {
                     break;
                  }

                  boolean f = true;

                  for (Integer p : pickList) {
                     if (p == pick) {
                        f = false;
                        break;
                     }
                  }

                  if (f) {
                     if (piece.getPosition() > 0) {
                        List<YutGamePiece> samePos = g.getPieceByPosition(piece.getPosition());
                        if (!samePos.isEmpty()) {
                           for (YutGamePiece px : samePos) {
                              px.setPosition(pick);
                           }
                        }
                     }

                     piece.setPosition(pick);
                     pickList.add(pick);
                     break;
                  }

                  if (++count >= 1000) {
                     break;
                  }
               }
            }
         }

         this.resultLog.addResult(gameInfo);
         this.resultLog.addResult(info);
         YutGameResult_Action.ActionEntry entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.NextTurn, gameInfo.getTeam());
         this.resultLog.addResult(new YutGameResult_Action(entry));
         this.resultLog.addResult(new YutGameResult_NextTurn(gameInfo.getTeam()));
         this.commitResultLog();
      }
   }

   public void activeItem(YutGameResult_GameInfo gameInfo, int itemType, int itemIndex) {
      if (itemType == 0) {
         gameInfo.getSuperItem().setActiveYutItem(itemIndex);
         gameInfo.getSuperItem().getYutItem().setUsable(false);
         gameInfo.setDisableYutItem(true);
         if (itemIndex == YutGameSuperItem.YutItemType.BackStep.getType()) {
            gameInfo.setActiveBackStep(true);
         }
      } else if (itemType == 1) {
         gameInfo.getSuperItem().setActivePieceItem(itemIndex);
         gameInfo.getSuperItem().getPieceItem().setUsable(false);
         if (itemIndex == YutGameSuperItem.PieceItemType.GoHome.getType()) {
            this.activePieceItem(gameInfo, 0, 2);
         } else if (itemIndex == YutGameSuperItem.PieceItemType.CarryAndGo.getType()) {
            this.activePieceItem(gameInfo, 1, 3);
         } else if (itemIndex == YutGameSuperItem.PieceItemType.ChangePosition.getType() || itemIndex == YutGameSuperItem.PieceItemType.BackHug.getType()) {
            this.activePieceItem(gameInfo, 1, 1);
         } else if (itemIndex == YutGameSuperItem.PieceItemType.GoMyFront.getType()) {
            this.activePieceItem(gameInfo, 1, 2);
         } else if (itemIndex == YutGameSuperItem.PieceItemType.ReverseCell.getType()) {
            this.doReverseCell(gameInfo);
         } else if (itemIndex == YutGameSuperItem.PieceItemType.FinishLineFront.getType() || itemIndex == YutGameSuperItem.PieceItemType.InstallBomb.getType()) {
            this.activePieceItem(gameInfo, 1, 4);
         }
      }

      this.resultLog.addResult(gameInfo);
      YutGameResult_Action.ActionEntry entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.ActiveItem, -1);
      entry.addAdditional(itemType);
      entry.addAdditional(itemIndex);
      this.resultLog.addResult(new YutGameResult_Action(entry));
      this.commitResultLog();
   }

   public void activePieceItem(YutGameResult_GameInfo gameInfo, int unk, int unk2) {
      gameInfo.setActivePieceItem(true);
      gameInfo.setDisableYutItem(true);
      this.nextTurn();
      YutGameResult_NextTurn nt = new YutGameResult_NextTurn(gameInfo.getTeam());
      nt.setUnk(true);
      nt.setUnk2(unk);
      this.resultLog.addResult(nt);
      YutGameResult_Action.ActionEntry entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.Unk2, gameInfo.getTeam());
      entry.addAdditional(unk2);
      this.resultLog.addResult(new YutGameResult_Action(entry));
   }

   public boolean throwYut(YutGameResult_GameInfo gameInfo) {
      boolean moreThrow = false;
      int dice = 0;
      int rand = Randomizer.rand(0, 1000);
      if (rand >= 200 && rand < 500) {
         dice = 1;
      } else if (rand >= 500 && rand < 700) {
         dice = 2;
      } else if (rand >= 700 && rand < 850) {
         dice = 3;
      } else if (rand >= 850 && rand < 900) {
         dice = 4;
      } else if (rand >= 900 && rand < 950) {
         dice = 5;
      } else if (rand >= 950) {
         dice = 6;
      }

      if (gameInfo.getSuperItem().getActiveYutItem() == YutGameSuperItem.YutItemType.APigAndADog.getType()) {
         dice = 0;
         if (Randomizer.isSuccess(50)) {
            dice = 1;
         }
      } else if (gameInfo.getSuperItem().getActiveYutItem() == YutGameSuperItem.YutItemType.UnconditionalASheep.getType()) {
         dice = 2;
      } else if (gameInfo.getSuperItem().getActiveYutItem() == YutGameSuperItem.YutItemType.Reverse.getType()) {
         dice = 5;
      } else if (gameInfo.getSuperItem().getActiveYutItem() == YutGameSuperItem.YutItemType.AHorseOrAPig.getType()) {
         dice = 0;
         if (Randomizer.isSuccess(50)) {
            dice = 4;
         }
      } else if (gameInfo.getSuperItem().getActiveYutItem() == YutGameSuperItem.YutItemType.ACowOrAHorse.getType()) {
         dice = 3;
         if (Randomizer.isSuccess(50)) {
            dice = 4;
         }
      }

      if (gameInfo.getSuperItem().getYutItem().isUsable()) {
         gameInfo.setDisableYutItem(true);
      }

      gameInfo.getCurrentYutInfo().incrementYutCount(dice);
      if (gameInfo.getSuperItem().getActiveYutItem() == YutGameSuperItem.YutItemType.DoubleDouble.getType()) {
         gameInfo.getCurrentYutInfo().incrementYutCount(dice);
      }

      if (gameInfo.getSuperItem().getActiveYutItem() != YutGameSuperItem.YutItemType.NextTurn.getType()
         && gameInfo.getSuperItem().getActiveYutItem() != YutGameSuperItem.YutItemType.BackStep.getType()
         && gameInfo.getSuperItem().getActiveYutItem() != -1) {
         gameInfo.getSuperItem().setActiveYutItem(-1);
      }

      if (dice != 3 && dice != 4) {
         gameInfo.setThrowYut(true);
         gameInfo.setNextTurn(false);
         if (gameInfo.getSuperItem().getActiveYutItem() == YutGameSuperItem.YutItemType.NextTurn.getType()) {
            this.forcedNextTurn(gameInfo, dice);
            gameInfo.getSuperItem().setActiveYutItem(-1);
         }
      } else {
         this.nextTurn(false);
         gameInfo.setNextTurn(true);
         moreThrow = true;
      }

      YutGameResult_Action.ActionEntry entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.GameSet, -1);
      entry.addAdditional(YutGameResult_Action.GameSetType.ThrowYut.getType());
      entry.addAdditional(dice);
      this.resultLog.addResult(new YutGameResult_Action(entry));
      if (gameInfo.getSuperItem().getActiveYutItem() != YutGameSuperItem.YutItemType.NextTurn.getType()) {
         if ((dice != 5 || !gameInfo.isAllPieceInHome()) && (dice != 6 || !gameInfo.getCurrentYutInfo().impossibleMove())) {
            this.resultLog.addResult(gameInfo);
         } else {
            this.forcedNextTurn(gameInfo, dice);
         }
      }

      this.commitResultLog();
      return moreThrow;
   }

   public void forcedNextTurn(YutGameResult_GameInfo gameInfo, int dice) {
      new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.GameSet, -1);

      for (YutGameResult_GameInfo info : this.gameInfo) {
         if (info.getTeam() != gameInfo.getTeam()) {
            info.setNextTurn(true);
            YutGameResult_Action.ActionEntry entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.NextTurn, info.getTeam());
            this.resultLog.addResult(new YutGameResult_Action(entry));
            this.resultLog.addResult(new YutGameResult_NextTurn(info.getTeam()));
         } else if (dice == 5) {
            info.getCurrentYutInfo().decrementYutCount(5);
         }

         if (this.field.isAuto()) {
            this.field.setAuto(false);
         }

         this.resultLog.addResult(info);
      }

      gameInfo.setNextTurn(false);
      if (gameInfo.isDisableYutItem()) {
         gameInfo.setDisableYutItem(false);
      }

      if (gameInfo.isActiveBackStep()) {
         gameInfo.setActiveBackStep(false);
      }
   }

   public boolean movePieceByAuto(YutGameResult_GameInfo gameInfo) {
      List<Integer> yuts = new ArrayList<>();
      if (gameInfo.getCurrentYutInfo().getaPigCount() > 0) {
         yuts.add(0);
      } else if (gameInfo.getCurrentYutInfo().getaDogCount() > 0) {
         yuts.add(1);
      } else if (gameInfo.getCurrentYutInfo().getaSheepCount() > 0) {
         yuts.add(2);
      } else if (gameInfo.getCurrentYutInfo().getaCowCount() > 0) {
         yuts.add(3);
      } else if (gameInfo.getCurrentYutInfo().getaHorseCount() > 0) {
         yuts.add(4);
      } else if (gameInfo.getCurrentYutInfo().getaReturnAPig() > 0) {
         yuts.add(5);
      }

      Integer pickYut = yuts.stream().findAny().orElse(null);
      if (pickYut == null) {
         return false;
      } else {
         YutGamePiece pick = Arrays.stream(gameInfo.getPieces()).filter(p -> p.getPosition() < 30).findAny().orElse(null);
         return this.movePiece(gameInfo, pick.getIndex(), pickYut, this.calcEndPos(pick.getPosition(), pickYut, gameInfo.isActiveBackStep()), true);
      }
   }

   public boolean movePiece(YutGameResult_GameInfo gameInfo, int pieceIndex, int yutIndex, int position) {
      return this.movePiece(gameInfo, pieceIndex, yutIndex, position, false);
   }

   public boolean movePiece(YutGameResult_GameInfo gameInfo, int pieceIndex, int yutIndex, int position, boolean auto) {
      if (position > 30) {
         return false;
      } else {
         if (position == 30) {
         }

         YutGamePiece piece = gameInfo.getPiece(pieceIndex);
         if (piece == null) {
            return false;
         } else {
            int originalPos = piece.getPosition();
            if (!auto && this.calcEndPos(originalPos, yutIndex, gameInfo.isActiveBackStep()) != position) {
               return false;
            } else {
               boolean pieceCatch = false;
               int team = gameInfo.getTeam();
               YutGameResult_GameInfo info = this.gameInfo[team ^ 1];
               List<Integer> catchs = info.checkPieceCatch(position);
               if (!catchs.isEmpty()) {
                  pieceCatch = true;

                  for (int index : catchs) {
                     for (YutGamePiece pi : info.getPieceByPosition(index)) {
                        pi.setPosition(0);
                        pi.setHidden(false);
                        pi.clearCarryIndex();
                     }
                  }

                  this.resultLog.addResult(info);
                  gameInfo.setThrowYut(false);
                  gameInfo.setNextTurn(true);
                  this.nextTurn(false);
               }

               List<YutGamePiece> sameList = gameInfo.getPieceByPosition(position);
               if (!sameList.isEmpty()) {
                  for (YutGamePiece p : sameList) {
                     if (!p.isHidden()) {
                        p.addCarryIndex(piece.getIndex());
                        piece.setHidden(true);
                        if (piece.getPosition() > 0) {
                           for (YutGamePiece p2 : gameInfo.getPieceByPosition(piece.getPosition())) {
                              p.addCarryIndex(p2.getIndex());
                              p2.setPosition(position);
                              p2.setHidden(true);
                              p2.clearCarryIndex();
                           }

                           piece.clearCarryIndex();
                        }
                     }
                  }
               }

               YutGamePiece carryPiece = null;
               if (piece.getPosition() > 0) {
                  sameList = gameInfo.getPieceByPosition(piece.getPosition());
                  if (!sameList.isEmpty()) {
                     for (YutGamePiece px : sameList) {
                        if (!px.isHidden()) {
                           carryPiece = px;
                        }

                        px.setPosition(position);
                     }

                     if (carryPiece == null) {
                        carryPiece = piece;
                     }
                  }
               }

               YutGameResult_Action.ActionEntry entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.GameSet, -1);
               entry.addAdditional(YutGameResult_Action.GameSetType.MovePiece.getType());
               entry.addAdditional(gameInfo.getTeam());
               entry.addAdditional(!sameList.isEmpty() ? (carryPiece != null ? carryPiece.getIndex() : pieceIndex) : pieceIndex);
               List<Integer> path = new ArrayList<>();
               Pair<Integer, Integer> ret = this.generateMovePath(originalPos, yutIndex, path, gameInfo.isActiveBackStep());
               if (ret.left == 1) {
                  if (piece.getPosition() > 0) {
                     sameList = gameInfo.getPieceByPosition(piece.getPosition());
                     if (!sameList.isEmpty()) {
                        for (YutGamePiece px : sameList) {
                           px.setPosition(20);
                        }
                     }
                  }

                  piece.setPosition(20);
                  path.add(20);
               } else if (ret.left == 2) {
                  sameList = gameInfo.getPieceByPosition(piece.getPosition());
                  if (!sameList.isEmpty()) {
                     for (YutGamePiece px : sameList) {
                        px.setPosition(0);
                        px.setHidden(false);
                        px.clearCarryIndex();
                     }
                  }

                  piece.setPosition(0);
                  piece.setHidden(false);
                  piece.clearCarryIndex();
                  path.add(0);
               } else {
                  piece.setPosition(position);
               }

               entry.addAdditional(ret.right > 0 ? 1 : 0);
               entry.addAdditional(pieceCatch ? 7 : -1);
               entry.addAdditional(ret.left);
               entry.addAdditional(path.size() + 1);
               entry.addAdditional(originalPos);

               for (Integer pa : path) {
                  entry.addAdditional(pa);
               }

               entry.addAdditional(0);
               this.resultLog.addResult(new YutGameResult_Action(entry));
               this.resultLog.addResult(gameInfo);
               gameInfo.getCurrentYutInfo().decrementYutCount(yutIndex);
               if (gameInfo.isActiveBackStep()) {
                  gameInfo.setActiveBackStep(false);
               }

               if (gameInfo.getCurrentYutInfo().impossibleMove() && !pieceCatch && gameInfo.isThrowYut()) {
                  if (gameInfo.isDisableYutItem()) {
                     gameInfo.setDisableYutItem(false);
                  }

                  info.setThrowYut(false);
                  info.setNextTurn(true);
                  this.resultLog.addResult(info);
                  entry = new YutGameResult_Action.ActionEntry(YutGameResult_Action.ActionType.NextTurn, info.getTeam());
                  this.resultLog.addResult(new YutGameResult_Action(entry));
                  this.resultLog.addResult(new YutGameResult_NextTurn(info.getTeam()));
               }

               if (gameInfo.allPieceGoalIn()) {
                  this.field.setWinTeam(gameInfo.getTeam());
                  this.field.setEndGame(true);
               }

               this.commitResultLog();
               return pieceCatch;
            }
         }
      }
   }

   public void surrender(MapleCharacter player, int team) {
      this.field.setWinTeam(team ^ 1);
      this.field.setEndGame(true);
      this.field.setSurrender(true);
      player.updateOneInfo(1234569, "miniGame1_can_time", String.valueOf(System.currentTimeMillis() + 900000L));
      player.updateOneInfo(1234569, "miniGame1_result", "2");
   }

   public YutGameResult_GameInfo getGameInfo(int team) {
      return this.gameInfo[team];
   }

   public Pair<Integer, Integer> generateMovePath(int startPos, int yutIndex, List<Integer> entry, boolean backStep) {
      Pair<Integer, Integer> ret = new Pair<>(0, 0);
      int moveCount = yutIndex + 1;
      if (yutIndex == 5) {
         moveCount = 1;
      }

      int pos = startPos;
      int count = 0;
      boolean touchInstallItem = false;

      for (int i = 0; i < moveCount; i++) {
         int delta = 1;
         if (yutIndex == 5 || backStep) {
            delta = -1;
         }

         pos = this.calcEndPos_(startPos, pos, delta, backStep);
         if (pos >= 30) {
            pos = 30;
         }

         if (backStep && pos == 30) {
            pos = 20;
         }

         for (YutGameResult_InstallItem installItem : new ArrayList<>(this.installItems)) {
            count++;
            if (installItem.getPosition() == pos) {
               touchInstallItem = true;
               if (installItem.getItemType() == YutGameResult_Action.InstallItemType.FinishLineFront) {
                  ret.left = 1;
                  ret.right = count;
               } else if (installItem.getItemType() == YutGameResult_Action.InstallItemType.Bomb) {
                  ret.left = 2;
                  ret.right = count;
               }

               this.removeInstallItem(pos);
               break;
            }
         }

         entry.add(pos);
         if (touchInstallItem) {
            break;
         }
      }

      return ret;
   }

   public boolean isDiagonalLine(int pos) {
      switch (pos) {
         case 5:
         case 10:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
            return true;
         case 6:
         case 7:
         case 8:
         case 9:
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
         default:
            return false;
      }
   }

   public int calcEndPos(int startPos, int yutIndex, boolean backStep) {
      int moveCount = yutIndex + 1;
      if (yutIndex == 5) {
         moveCount = 1;
      }

      int pos = startPos;

      for (int i = startPos; i < moveCount + startPos && pos < 30; i++) {
         int delta = 1;
         if (yutIndex == 5 || backStep) {
            delta = -1;
         }

         pos = this.calcEndPos_(startPos, pos, delta, backStep);
         if (backStep && pos == 30) {
            pos = 20;
         }
      }

      return Math.min(30, pos);
   }

   public int calcEndPos_(int startPos, int pos, int delta, boolean backStep) {
      if (pos == 30) {
         return 30;
      } else {
         boolean diagonal = startPos == 5 || startPos == 10 || startPos == 23;
         if (delta == -1) {
            if (pos == 20) {
               return backStep ? 30 : 29;
            } else if (pos == 1) {
               return 20;
            } else if (pos == 21) {
               return 5;
            } else if (pos == 28) {
               return 23;
            } else {
               return pos == 26 ? 10 : pos - 1;
            }
         } else if (delta == 1) {
            if (pos == 5) {
               return diagonal ? 21 : pos + 1;
            } else if (pos == 10) {
               return diagonal ? 26 : pos + 1;
            } else if (pos == 27) {
               return 23;
            } else if (pos == 23) {
               return startPos != 10 && startPos != 26 && startPos != 27 && startPos != 23 ? pos + 1 : 28;
            } else if (pos == 29) {
               return 20;
            } else if (pos == 20) {
               return 30;
            } else {
               return pos == 25 ? 15 : pos + 1;
            }
         } else {
            return -1;
         }
      }
   }
}
