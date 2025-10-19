package objects.shop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import network.models.PlayerShopPacket;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.users.MapleCharacter;
import objects.users.MapleClient;

public class MapleMiniGame extends AbstractPlayerStore {
   private static final int slots = 2;
   private boolean[] exitAfter;
   private boolean[] ready;
   private int[] points;
   private int GameType = 0;
   private int[][] piece = new int[15][15];
   private List<Integer> matchcards = new ArrayList<>();
   int loser = 0;
   int turn = 1;
   int piecetype = 0;
   int firstslot = 0;
   int tie = -1;
   private List<Integer> omokPlayers = new ArrayList<>();

   public MapleMiniGame(MapleCharacter owner, int itemId, String description, String pass, int GameType) {
      super(owner, itemId, description, pass, 1);
      this.GameType = GameType;
      this.points = new int[2];
      this.exitAfter = new boolean[2];
      this.ready = new boolean[2];
      this.reset();
   }

   public void reset() {
      for (int i = 0; i < 2; i++) {
         this.points[i] = 0;
         this.exitAfter[i] = false;
         this.ready[i] = false;
      }
   }

   public void setFirstSlot(int type) {
      this.firstslot = type;
   }

   public int getFirstSlot() {
      return this.firstslot;
   }

   public void setPoints(int slot) {
      this.points[slot]++;
      this.checkWin();
   }

   public int getPoints() {
      int ret = 0;

      for (int i = 0; i < 2; i++) {
         ret += this.points[i];
      }

      return ret;
   }

   public void checkWin() {
      if (this.getPoints() >= this.getMatchesToWin() && !this.isOpen()) {
         int x = 0;
         int highest = 0;
         boolean tie = false;

         for (int i = 0; i < 2; i++) {
            if (this.points[i] > highest) {
               x = i;
               highest = this.points[i];
               tie = false;
            } else if (this.points[i] == highest) {
               tie = true;
            }

            this.points[i] = 0;
         }

         this.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(this, tie ? 1 : 2, x));
         this.setOpen(true);
         this.update();
         this.checkExitAfterGame();
      }
   }

   public int getOwnerPoints(int slot) {
      return this.points[slot];
   }

   public void setPieceType(int type) {
      this.piecetype = type;
   }

   public int getPieceType() {
      return this.piecetype;
   }

   public void setGameType() {
      if (this.GameType == 2) {
         this.matchcards.clear();

         for (int i = 0; i < this.getMatchesToWin(); i++) {
            this.matchcards.add(i);
            this.matchcards.add(i);
         }
      }
   }

   public void shuffleList() {
      if (this.GameType == 2) {
         Collections.shuffle(this.matchcards);
      } else {
         this.piece = new int[15][15];
      }
   }

   public int getCardId(int slot) {
      return this.matchcards.get(slot - 1);
   }

   public int getMatchesToWin() {
      return this.getPieceType() == 0 ? 6 : (this.getPieceType() == 1 ? 10 : 15);
   }

   public void setLoser(int type) {
      this.loser = type;
   }

   public int getLoser() {
      return this.loser;
   }

   public void send(MapleClient c) {
      if (this.getMCOwner() == null) {
         this.closeShop(false, false);
      } else {
         c.getSession().writeAndFlush(PlayerShopPacket.getMiniGame(c, this));
      }
   }

   public void setReady(int slot) {
      this.ready[slot] = !this.ready[slot];
   }

   public boolean isReady(int slot) {
      return this.ready[slot];
   }

   public void setPiece(int move1, int move2, int type, MapleCharacter chr) {
      if (this.piece[move1][move2] == 0 && !this.isOpen()) {
         this.piece[move1][move2] = type;
         this.broadcastToVisitors(PlayerShopPacket.getMiniGameMoveOmok(move1, move2, type));
         boolean found = false;

         for (int y = 0; y < 15; y++) {
            for (int x = 0; x < 15; x++) {
               if (!found && this.searchCombo(x, y, type)) {
                  this.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(this, 2, this.getVisitorSlot(chr)));
                  this.setOpen(true);
                  this.update();
                  this.checkExitAfterGame();
                  found = true;
               }
            }
         }

         this.nextLoser();
      }
   }

   public void nextLoser() {
      this.loser++;
      if (this.loser > 1) {
         this.loser = 0;
      }
   }

   public void exit(MapleCharacter player) {
      if (player != null) {
         player.setPlayerShop(null);
         if (this.isOwner(player)) {
            this.update();
            this.removeAllVisitors(3, 1);
         } else {
            this.removeVisitor(player);
         }
      }
   }

   public boolean isExitAfter(MapleCharacter player) {
      return this.getVisitorSlot(player) > -1 ? this.exitAfter[this.getVisitorSlot(player)] : false;
   }

   public void setExitAfter(MapleCharacter player) {
      if (this.getVisitorSlot(player) > -1) {
         this.exitAfter[this.getVisitorSlot(player)] = !this.exitAfter[this.getVisitorSlot(player)];
      }
   }

   public void checkExitAfterGame() {
      for (int i = 0; i < 2; i++) {
         if (this.exitAfter[i]) {
            this.exitAfter[i] = false;
            (i == 0 ? this.getMCOwner() : this.chrs[i - 1].get()).getClient().getSession().writeAndFlush(PlayerShopPacket.shopErrorMessage(0, i));
            if (i == 0 && this.getShopType() != 1) {
               this.closeShop(false, this.isAvailable());
            } else {
               (i == 0 ? this.getMCOwner() : this.chrs[i - 1].get()).setPlayerShop(null);
               this.removeVisitor(this.chrs[i - 1].get());
            }
         }
      }
   }

   public boolean searchCombo(int x, int y, int type) {
      boolean ret = false;
      if (!ret && x < 11) {
         ret = true;

         for (int i = 0; i < 5; i++) {
            if (this.piece[x + i][y] != type) {
               ret = false;
               break;
            }
         }
      }

      if (!ret && y < 11) {
         ret = true;

         for (int ix = 0; ix < 5; ix++) {
            if (this.piece[x][y + ix] != type) {
               ret = false;
               break;
            }
         }
      }

      if (!ret && x < 11 && y < 11) {
         ret = true;

         for (int ixx = 0; ixx < 5; ixx++) {
            if (this.piece[x + ixx][y + ixx] != type) {
               ret = false;
               break;
            }
         }
      }

      if (!ret && x > 3 && y < 11) {
         ret = true;

         for (int ixxx = 0; ixxx < 5; ixxx++) {
            if (this.piece[x - ixxx][y + ixxx] != type) {
               ret = false;
               break;
            }
         }
      }

      return ret;
   }

   public int getScore(MapleCharacter chr) {
      int score = 2000;
      int wins = this.getWins(chr);
      int ties = this.getTies(chr);
      int losses = this.getLosses(chr);
      if (wins + ties + losses > 0) {
         score += wins * 2;
         score += ties;
         score -= losses * 2;
      }

      return score;
   }

   @Override
   public byte getShopType() {
      return (byte)(this.GameType == 1 ? 3 : 4);
   }

   public int getWins(MapleCharacter chr) {
      return Integer.parseInt(this.getData(chr).split(",")[2]);
   }

   public int getTies(MapleCharacter chr) {
      return Integer.parseInt(this.getData(chr).split(",")[1]);
   }

   public int getLosses(MapleCharacter chr) {
      return Integer.parseInt(this.getData(chr).split(",")[0]);
   }

   public void setPoints(int i, int type) {
      MapleCharacter z;
      if (i == 0) {
         z = this.getMCOwner();
      } else {
         z = this.getVisitor(i - 1);
      }

      if (z != null) {
         String[] data = this.getData(z).split(",");
         data[type] = String.valueOf(Integer.parseInt(data[type]) + 1);
         StringBuilder newData = new StringBuilder();

         for (int s = 0; s < data.length; s++) {
            newData.append(data[s]);
            newData.append(",");
         }

         String newDat = newData.toString();
         z.getQuestIfNullAdd(MapleQuest.getInstance(this.GameType == 1 ? 122200 : 122210)).setCustomData(newDat.substring(0, newDat.length() - 1));
      }
   }

   public String getData(MapleCharacter chr) {
      MapleQuest quest = MapleQuest.getInstance(this.GameType == 1 ? 122200 : 122210);
      MapleQuestStatus record;
      if (chr.getQuestNoAdd(quest) == null) {
         record = chr.getQuestIfNullAdd(quest);
         record.setCustomData("0,0,0");
      } else {
         record = chr.getQuestNoAdd(quest);
         if (record.getCustomData() == null || record.getCustomData().length() < 5 || record.getCustomData().indexOf(",") == -1) {
            record.setCustomData("0,0,0");
         }
      }

      return record.getCustomData();
   }

   public int getRequestedTie() {
      return this.tie;
   }

   public void setRequestedTie(int t) {
      this.tie = t;
   }

   public int getTurn() {
      return this.turn;
   }

   public void setTurn(int t) {
      this.turn = t;
   }

   @Override
   public void closeShop(boolean s, boolean z) {
      this.removeAllVisitors(3, 1);
      if (this.getMCOwner() != null) {
         this.getMCOwner().setPlayerShop(null);
      }

      this.update();
      this.getMap().removeMapObject(this);
   }

   @Override
   public void buy(MapleClient c, int z, short i) {
   }

   public void addOmokPlayers(int id) {
      if (!this.getOmokPlayers().contains(id)) {
         this.omokPlayers.add(id);
      }
   }

   public List<Integer> getOmokPlayers() {
      return this.omokPlayers;
   }

   public void forceFinishGame() {
      for (int playerId : this.getOmokPlayers()) {
         MapleCharacter player = this.getMap().getCharacterById(playerId);
         if (player != null) {
            this.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(this, 2, this.getVisitorSlot(player)));
            this.setOpen(true);
            this.update();
            this.checkExitAfterGame();
         }
      }
   }
}
