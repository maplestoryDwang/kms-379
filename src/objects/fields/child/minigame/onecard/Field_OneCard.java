package objects.fields.child.minigame.onecard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import network.decode.PacketDecoder;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.users.MapleCharacter;

public class Field_OneCard extends Field {
   public boolean setGame;
   public boolean endGame;
   public boolean setFirstCard;
   public boolean setUser;
   public boolean clockWiseTurn;
   public int fireSet;
   public long setNextUserTime;
   public long userPossibleTime;
   public long userWarpTime;
   public OneCardGameCardInfo currentCard = null;
   public OneCardPlayer currentPlayer = null;
   public OneCardGameDlg gameDlg = null;
   public OneCardPlayer winnerUser = null;

   public Field_OneCard(int mapID, int channel, int returnMapId, float monsterRate) {
      super(mapID, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.endGame) {
         if (this.userWarpTime < System.currentTimeMillis()) {
            this.warpUser();
            if (this.winnerUser != null) {
               this.gameDlg.getPlayerList().clear();
            }
         }
      } else if (this.setGame) {
         if (this.setUser) {
            if (this.userPossibleTime < System.currentTimeMillis()) {
               this.skipPlayer();
            }
         } else if (!this.setFirstCard) {
            if (this.setNextUserTime < System.currentTimeMillis()) {
               this.setFirstCardDeck();
               this.setCard();
            }
         } else {
            this.setUser = true;
            this.setNextUserAction();
         }
      } else {
         this.gameDlg = new OneCardGameDlg(this);
         this.gameDlg.init(this.getCharactersThreadsafe());
         this.gameDlg.start();
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.setGame = false;
      this.endGame = false;
      this.setFirstCard = false;
      this.clockWiseTurn = false;
      this.setUser = false;
      this.fireSet = 0;
      this.setNextUserTime = System.currentTimeMillis();
      this.userPossibleTime = System.currentTimeMillis();
      this.userWarpTime = System.currentTimeMillis();
      this.currentCard = null;
      this.currentPlayer = null;
      this.gameDlg = null;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }

   public void setFirstCardDeck() {
      this.setNextUserTime = System.currentTimeMillis() + 5000L;
      this.setFirstCard = true;

      for (OneCardPlayer player : this.gameDlg.getPlayerList()) {
         List<OneCardGameCardInfo> cardList = new ArrayList<>();

         for (int i = 0; i < 7; i++) {
            OneCardGameCardInfo card = this.gameDlg.getNextFreeCard();
            if (card != null) {
               card.setOwner(player.getPlayer().getId());
               cardList.add(card);
               player.addCard(card);
            } else {
               System.out.println("CARD 찾는 중 오류 발생.");
            }
         }

         this.broadcastMessage(OneCardPacket.onGetCardResult(player, cardList));
      }
   }

   public void setCard() {
      while (this.currentCard == null || this.currentCard.getNumber() >= 6) {
         this.currentCard = this.gameDlg.getNextFreeCard();
         this.gameDlg.resetDeck();
      }

      if (this.currentCard != null) {
         this.broadcastMessage(OneCardPacket.onPutCardResult(null, this.currentCard));
      } else {
         System.out.println("CARD 세팅 중 오류 발생.");
      }
   }

   public void setNextUserAction() {
      this.userPossibleTime = System.currentTimeMillis() + 15000L;
      this.broadcastMessage(
         OneCardPacket.onUserPossibleAction(this.currentPlayer, 15, this.calcUsableCardInfo(), this.currentPlayer.getDeckInfo().size() < 17, this.clockWiseTurn)
      );
      this.currentPlayer.getPlayer().getClient().getSession().writeAndFlush(CField.playSound("Sound/MiniGame.img/oneCard/myturn"));
      this.currentPlayer.getPlayer().getClient().getSession().writeAndFlush(CWvsContext.enableActions(this.currentPlayer.getPlayer()));
      this.currentPlayer.getPlayer().getClient().getSession().writeAndFlush(OneCardPacket.onShowText("당신의 턴입니다."));
   }

   public void skipPlayer() {
      this.addCardPlayer(this.currentPlayer.getPlayer());
      this.setNextPlayer();
      this.setNextUserAction();
   }

   public void warpUser() {
      for (OneCardPlayer p : this.gameDlg.getIntactPlayerList()) {
         if (p.getPlayer().getMapId() == 993189500) {
            p.getPlayer().warp(993189401);
            this.broadcastMessage(OneCardPacket.leaveResult());
         }
      }

      this.endGame = false;
   }

   public void endGame(OneCardPlayer winner) {
      if (!this.endGame) {
         this.broadcastMessage(OneCardPacket.leaveResult());

         for (OneCardPlayer player : this.gameDlg.getPlayerList()) {
            if (player.getPOS() == winner.getPOS()) {
               winner.getPlayer().updateOneInfo(1234569, "miniGame2_result", "1");
            } else {
               player.getPlayer().updateOneInfo(1234569, "miniGame2_result", "4");
            }
         }

         winner.getPlayer().getClient().getSession().writeAndFlush(OneCardPacket.onShowScreenEffect("Effect/screeneff/victory"));
         this.broadcastMessage(winner.getPlayer(), OneCardPacket.onShowScreenEffect("Effect/screeneff/gameover"), false);
         String winnerName = winner.getPlayer().getName();
         this.broadcastMessage(OneCardPacket.onShowText(winnerName + "님의 승리! 게임이 종료됩니다."));

         for (OneCardPlayer playerx : this.gameDlg.getIntactPlayerList()) {
            if (playerx.getPlayer() != null) {
               playerx.getPlayer().dropMessage(6, winnerName + "님의 승리! 게임이 종료됩니다.");
            }
         }

         this.endGame = true;
         this.winnerUser = winner;
         this.userWarpTime = System.currentTimeMillis() + 5000L;
      }
   }

   public void setAction(MapleCharacter chr, PacketDecoder o) {
      byte type = o.readByte();
      if (this.currentPlayer.getPlayer().getId() != chr.getId()) {
         System.out.println("현재 유저가 아닌 대상이 행동을 시도함.");
      } else if (!this.endGame) {
         switch (type) {
            case 0:
               int objectID = o.readInt();
               OneCardGameCardInfo card = this.gameDlg.getCard(objectID);
               if (card == null || card.getOwner() != chr.getId()) {
                  System.out.println("존재하지 않는 카드를 놓으려고 시도함.");
                  return;
               }

               this.currentPlayer.removeCard(card);
               this.broadcastMessage(OneCardPacket.onPutCardResult(this.currentPlayer, card));
               card.setOwner(-1);
               int size = this.currentPlayer.getDeckInfo().size();
               if (size <= 0) {
                  this.endGame(this.currentPlayer);
               } else if (size == 1) {
                  this.broadcastMessage(CField.playSound("Sound/MiniGame.img/oneCard/lastcard"));
               }

               this.currentCard = card;
               List<Integer> ableColors = new ArrayList<>();
               this.setCardOption();
               switch (card.getNumber()) {
                  case 8:
                     IntStream.range(0, 4).forEach(i -> {
                        if (card.getColor() != i) {
                           ableColors.add(i);
                        }
                     });
                  case 9:
                     break;
                  case 10:
                     this.setNextPlayer();
                     this.setNextPlayer();
                     break;
                  case 11:
                     this.clockWiseTurn = !this.clockWiseTurn;
                     this.setNextPlayer();
                     break;
                  case 12:
                     if (card.getColor() != 3) {
                        this.setNextPlayer();
                     } else {
                        IntStream.range(0, 4).forEach(ableColors::add);
                     }
                     break;
                  default:
                     this.setNextPlayer();
               }

               if (!ableColors.isEmpty()) {
                  chr.getClient().getSession().writeAndFlush(OneCardPacket.onChangeColorRequest(ableColors));
               }

               if (this.gameDlg.getNextFreeCard() == null) {
                  this.resetDeck();
               }

               this.setNextUserAction();
               break;
            case 1:
               this.addCardPlayer(chr);
               this.setNextPlayer();
               this.setNextUserAction();
               break;
            case 2:
               byte color = o.readByte();
               this.broadcastMessage(OneCardPacket.onChangeColorResult(false, color));
               this.currentCard = new OneCardGameCardInfo(this.currentCard.getObjectID(), color, this.currentCard.getNumber());
               this.setNextPlayer();
               this.setNextUserAction();
         }
      }
   }

   public void setCardOption() {
      switch (this.currentCard.getNumber()) {
         case 6:
            this.fireSet += 2;
            this.broadcastMessage(OneCardPacket.onEffectResult(2, 2, this.currentPlayer.getPlayer().getId(), false));
            break;
         case 7:
            this.fireSet += 3;
            this.broadcastMessage(OneCardPacket.onEffectResult(2, 3, this.currentPlayer.getPlayer().getId(), false));
            break;
         case 8:
            this.broadcastMessage(OneCardPacket.onShowText("마법 : 색 바꾸기!"));
            break;
         case 9:
            this.broadcastMessage(OneCardPacket.onShowText("마법 : 한 번 더!"));
            break;
         case 10:
            this.broadcastMessage(OneCardPacket.onShowText("마법 : 점프!"));
            break;
         case 11:
            this.broadcastMessage(OneCardPacket.onShowText("마법 : 거꾸로!"));
            break;
         case 12:
            switch (this.currentCard.getColor()) {
               case 0:
                  this.fireSet += 5;
                  this.broadcastMessage(OneCardPacket.onShowScreenEffect("Effect/screeneff/oz"));
                  this.broadcastMessage(CField.playSound("Sound/MiniGame.img/oneCard/flame_burst"));
                  this.broadcastMessage(OneCardPacket.onEffectResult(2, 5, this.currentPlayer.getPlayer().getId(), false));
                  this.broadcastMessage(OneCardPacket.onShowText("마법 : " + this.currentPlayer.getPlayer().getName() + "님의 공격!"));
                  break;
               case 1:
                  this.fireSet = 0;
                  this.broadcastMessage(OneCardPacket.onShowScreenEffect("Effect/screeneff/michael"));
                  this.broadcastMessage(OneCardPacket.onEffectResult(3, 0, this.currentPlayer.getPlayer().getId(), false));
                  this.broadcastMessage(CField.playSound("Sound/MiniGame.img/oneCard/shield_appear"));
                  break;
               case 2:
                  this.broadcastMessage(OneCardPacket.onShowScreenEffect("Effect/screeneff/hawkeye"));
                  this.addCardPlayers(2, true);
                  break;
               case 3:
                  this.broadcastMessage(OneCardPacket.onShowScreenEffect("Effect/screeneff/irina"));
                  this.removeCardPlayers();
                  break;
               case 4:
                  this.broadcastMessage(OneCardPacket.onShowScreenEffect("Effect/screeneff/icart"));
            }
      }
   }

   public void setEmotion(MapleCharacter chr, PacketDecoder o) {
      o.skip(4);
      int emotionID = o.readInt();
      this.broadcastMessage(OneCardPacket.onEmotion(chr.getId(), emotionID));
      chr.getClient().getSession().writeAndFlush(CWvsContext.enableActions(chr));
   }

   public void playerDead(MapleCharacter chr) {
      for (OneCardPlayer pUser : this.gameDlg.getPlayerList()) {
         if (pUser.getPlayer().getId() == chr.getId()) {
            this.playerDead(pUser, true);
            return;
         }
      }
   }

   public void playerDead(OneCardPlayer player, boolean exit) {
      boolean first = false;
      if (this.gameDlg.getPlayerList().contains(player)) {
         first = true;
         this.broadcastMessage(OneCardPacket.onShowScreenEffect("Effect/screeneff/gameover"));
         player.getPlayer().getClient().getSession().writeAndFlush(CField.playSound("Sound/MiniGame.img/oneCard/gameover"));
         this.broadcastMessage(OneCardPacket.onEffectResult(5, 0, player.getPlayer().getId(), true));
      }

      this.gameDlg.playerDead(player, exit, first);
      if (this.gameDlg.getPlayerList().size() == 1) {
         this.endGame(this.gameDlg.getPlayerList().get(0));
      } else if (player.getPOS() == this.currentPlayer.getPOS()) {
         this.setNextPlayer();
         this.setNextUserAction();
      }
   }

   public void setNextPlayer() {
      OneCardPlayer dummy = null;
      int currentPostion = this.currentPlayer.getPOS();
      int next = 1;
      int count = 0;
      int MAX_USER = 4;
      if (this.gameDlg.getPlayerList().size() == 2) {
         this.currentPlayer = this.gameDlg.getPlayerList().stream().filter(player -> player.getPOS() != this.currentPlayer.getPOS()).findFirst().orElse(null);
      } else {
         while (count < 5) {
            count++;

            for (OneCardPlayer p : this.gameDlg.getPlayerList()) {
               if (this.clockWiseTurn) {
                  if (p.getPOS() == currentPostion + next && !p.getIsDead()) {
                     dummy = p;
                     break;
                  }
               } else if (p.getPOS() == currentPostion - next && !p.getIsDead()) {
                  dummy = p;
                  break;
               }
            }

            if (dummy != null) {
               break;
            }

            if (this.clockWiseTurn && currentPostion + ++next > MAX_USER - 1) {
               next = 0;
               currentPostion = 0;
            } else if (!this.clockWiseTurn && currentPostion - next < 0) {
               next = 0;
               currentPostion = MAX_USER - 1;
            }
         }

         this.currentPlayer = dummy;
         if (this.gameDlg.getPlayerList().size() == 1) {
            this.endGame(this.currentPlayer);
         }

         for (OneCardPlayer winner : this.gameDlg.getPlayerList()) {
            if (winner.getDeckInfo().size() <= 0) {
               this.endGame(winner);
            }
         }

         return;
      }
   }

   public void addCardPlayer(MapleCharacter chr) {
      this.addCardPlayer(this.currentPlayer, 1 + this.fireSet);
      if (this.fireSet > 0) {
         this.broadcastMessage(OneCardPacket.onShowText(chr.getName() + "님이 " + this.fireSet + "의 피해를 입었습니다."));
         this.broadcastMessage(OneCardPacket.onEffectResult(4, 0, chr.getId(), false));
         this.fireSet = 0;
      }
   }

   public void addCardPlayer(OneCardPlayer player, int count) {
      List<OneCardGameCardInfo> cardList = new ArrayList<>();

      for (int i = 0; i < count; i++) {
         OneCardGameCardInfo card;
         for (card = this.gameDlg.getNextFreeCard(); card == null; card = this.gameDlg.getNextFreeCard()) {
            this.gameDlg.resetDeck();
         }

         if (player.getDeckInfo().size() >= 17) {
            this.playerDead(player, false);
            return;
         }

         card.setOwner(player.getPlayer().getId());
         cardList.add(card);
         player.addCard(card);
      }

      if (player.getDeckInfo().size() >= 17) {
         this.playerDead(player, false);
      } else {
         this.broadcastMessage(OneCardPacket.onGetCardResult(player, cardList));
      }
   }

   public void addCardPlayers(int count, boolean isHawkEye) {
      if (isHawkEye) {
         for (OneCardPlayer ocp : this.gameDlg.getPlayerList()) {
            if (ocp != this.currentPlayer) {
               this.addCardPlayer(ocp, count);
            }
         }
      } else {
         this.gameDlg.getPlayerList().forEach(player -> this.addCardPlayer(player, count));
      }
   }

   public void removeCardPlayers() {
      this.gameDlg.getPlayerList().forEach(player -> {
         for (OneCardGameCardInfo card : player.getDeckInfo().stream().filter(c -> c.getColor() == 3).collect(Collectors.toList())) {
            if (card != null) {
               card.setOwner(-1);
               player.removeCard(card);
               this.broadcastMessage(OneCardPacket.onPutCardResult(player, card));
            }
         }

         if (player.getDeckInfo().size() <= 0) {
            this.endGame(player);
         }
      });
   }

   public void resetDeck() {
      this.gameDlg.resetDeck();
      this.broadcastMessage(OneCardPacket.onEffectResult(0, 0, 0, false));
   }

   public List<OneCardGameCardInfo> calcUsableCardInfo() {
      List<OneCardGameCardInfo> deckInfo = new ArrayList<>();
      this.currentPlayer
         .getDeckInfo()
         .forEach(
            card -> {
               if (this.currentCard.getColor() == 4) {
                  deckInfo.add(card);
               } else if (card.getNumber() <= 5) {
                  if ((card.getColor() == this.currentCard.getColor() || card.getNumber() == this.currentCard.getNumber())
                     && (this.currentCard.getNumber() <= 5 || this.currentCard.getNumber() >= 8 && this.currentCard.getNumber() <= 11)
                     && this.fireSet == 0) {
                     deckInfo.add(card);
                  }
               } else if (card.getNumber() <= 7) {
                  if (this.fireSet == 0) {
                     if (card.getColor() == this.currentCard.getColor() || card.getNumber() == this.currentCard.getNumber()) {
                        deckInfo.add(card);
                     }
                  } else if (this.currentCard.getNumber() == 6) {
                     if (card.getNumber() == 6) {
                        deckInfo.add(card);
                     }
                  } else if (this.currentCard.getNumber() == 7
                     && (card.getColor() == this.currentCard.getColor() && card.getNumber() == 6 || card.getNumber() == 7)) {
                     deckInfo.add(card);
                  }
               } else if (card.getNumber() <= 11) {
                  if ((card.getColor() == this.currentCard.getColor() || card.getNumber() == this.currentCard.getNumber()) && this.fireSet == 0) {
                     deckInfo.add(card);
                  }
               } else {
                  switch (card.getColor()) {
                     case 0:
                        if (this.currentCard.getColor() == card.getColor()) {
                           deckInfo.add(card);
                        }
                        break;
                     case 1:
                     case 4:
                        deckInfo.add(card);
                        break;
                     case 2:
                     case 3:
                        if (this.currentCard.getColor() == card.getColor() && this.fireSet == 0) {
                           deckInfo.add(card);
                        }
                  }
               }
            }
         );
      return deckInfo;
   }
}
