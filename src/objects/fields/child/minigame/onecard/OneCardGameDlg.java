package objects.fields.child.minigame.onecard;

import constants.ServerConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import objects.fields.Field;
import objects.users.MapleCharacter;
import objects.utils.Randomizer;

public class OneCardGameDlg {
   public static final int PUT_CARD = 0;
   public static final int ADD_CARD = 1;
   public static final int SELECT_COLOR = 2;
   public static final int PLAYER_COUNT = 4;
   public static final int SET_FIRST_CARD_COUNT = 7;
   public static final int POSSIBLE_TIME = 15;
   public static final int CAN_GET_CARD_FROM_GRAVES = 17;
   private final Field_OneCard field;
   private final List<OneCardPlayer> playerList = new ArrayList<>();
   private final List<OneCardGameCardInfo> cardList = new ArrayList<>();
   private final List<OneCardPlayer> intactPlayerList = new ArrayList<>();

   public List<OneCardPlayer> getIntactPlayerList() {
      return this.intactPlayerList;
   }

   public OneCardGameDlg(Field field) {
      this.field = (Field_OneCard)field;
   }

   public void init(List<MapleCharacter> players) {
      int position = 0;
      if (players.size() < 4) {
         for (MapleCharacter player : players) {
            if (player.getRegisterTransferFieldTime() == 0L) {
               player.dropMessage(5, "게임에 필요한 인원수가 부족하여 마을로 이동됩니다.");
               player.setRegisterTransferField(ServerConstants.TownMap);
               player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
            }
         }
      } else {
         for (MapleCharacter playerx : players) {
            this.playerList.add(new OneCardPlayer(playerx, position));
            this.intactPlayerList.add(new OneCardPlayer(playerx, position));
            position++;
         }

         for (OneCardPlayer playerx : this.playerList) {
            playerx.getPlayer().send(OneCardPacket.createUI(playerx, this.playerList));
         }
      }
   }

   public void start() {
      if (this.playerList.size() == 0) {
         this.field.endGame = true;
      } else {
         this.setDeck();
         this.field.currentPlayer = this.playerList.get(Randomizer.nextInt(this.playerList.size()));
         this.field.clockWiseTurn = Randomizer.nextBoolean();
         this.field.setNextUserTime = System.currentTimeMillis() + 3000L;
         this.field.setFirstCard = false;
         this.field.setGame = true;
         this.field.fireSet = 0;
         this.field.broadcastMessage(OneCardPacket.onStart(this.playerList));
      }
   }

   public OneCardGameCardInfo getNextFreeCard() {
      return this.cardList.stream().filter(card -> card.getOwner() == 0).findAny().orElse(null);
   }

   public OneCardGameCardInfo getCard(int objectID) {
      return this.cardList.stream().filter(card -> card.getObjectID() == objectID).findAny().orElse(null);
   }

   public void setDeck() {
      for (int color = 0; color <= 3; color++) {
         for (int number = 0; number <= 12; number++) {
            this.cardList.add(new OneCardGameCardInfo(this.cardList.size() + 1, color, number));
         }
      }

      this.cardList.add(new OneCardGameCardInfo(this.cardList.size() + 1, 4, 12));
      Collections.shuffle(this.cardList);
   }

   public List<OneCardPlayer> getPlayerList() {
      return this.playerList;
   }

   public void resetDeck() {
      this.cardList.stream().filter(card -> card.getOwner() == -1).forEach(card -> card.setOwner(0));
      Collections.shuffle(this.cardList);
   }

   public void playerDead(OneCardPlayer player, boolean exit, boolean first) {
      if (first) {
         player.userDead();
         player.getDeckInfo().forEach(card -> card.setOwner(0));
         this.playerList.remove(player);
         if (exit) {
            player.getPlayer().updateOneInfo(1234569, "miniGame2_result", "2");
         } else {
            player.getPlayer().updateOneInfo(1234569, "miniGame2_result", "4");
         }
      }

      if (exit) {
         player.getPlayer().warp(ServerConstants.TownMap);
      }

      if (first) {
         player.getPlayer().getClient().getSession().writeAndFlush(OneCardPacket.onShowScreenEffect("Effect/screeneff/dropout"));
      }
   }
}
