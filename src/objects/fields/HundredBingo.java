package objects.fields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import network.models.CField;
import objects.item.MapleInventoryType;
import objects.users.MapleCharacter;
import objects.utils.Randomizer;
import objects.utils.Timer;

public class HundredBingo {
   Map<MapleCharacter, int[][]> players = new HashMap<>();
   private List<MapleCharacter> rank = new ArrayList<>();
   private List<Integer> hostednumbers = new ArrayList<>();
   private ScheduledFuture<?> BingoTimer = null;
   private int round = 1;

   public HundredBingo(List<MapleCharacter> a1) {
      this.InitGame(a1);
   }

   public void addRank(MapleCharacter a1) {
      if (!this.rank.contains(a1)) {
         this.rank.add(a1);
         a1.getMap().broadcastMessage(CField.BingoAddRank(a1));
      }
   }

   public int[][] getTable(MapleCharacter a1) {
      return this.players.get(a1);
   }

   public void setTable(MapleCharacter a1, int[][] a2) {
      this.players.replace(a1, this.players.get(a1), a2);
   }

   public List<MapleCharacter> getRanking() {
      return this.rank;
   }

   public void StartGame() {
      for (MapleCharacter chr : this.players.keySet()) {
         chr.cancelAllBuffs();
         chr.getClient().getSession().writeAndFlush(CField.BingoGameState(3, this.round));
      }

      this.BingoTimer = Timer.MapTimer.getInstance()
         .register(
            new Runnable() {
               @Override
               public void run() {
                  int temp = HundredBingo.this.players.size();

                  for (MapleCharacter chr : HundredBingo.this.players.keySet()) {
                     if (chr == null) {
                        temp--;
                     }
                  }

                  if (temp <= 0) {
                     HundredBingo.this.StopBingo();
                  } else if (HundredBingo.this.hostednumbers.size() != 75
                     && HundredBingo.this.rank.size() != 30
                     && HundredBingo.this.rank.size() != HundredBingo.this.players.size()) {
                     int number;
                     do {
                        number = Randomizer.rand(1, 76);
                     } while (HundredBingo.this.hostednumbers.contains(number) || number > 75);

                     HundredBingo.this.hostednumbers.add(number);

                     for (MapleCharacter chrx : HundredBingo.this.players.keySet()) {
                        chrx.getClient().getSession().writeAndFlush(CField.BingoHostNumberReady());
                        chrx.getClient().getSession().writeAndFlush(CField.BingoHostNumber(number, 75 - HundredBingo.this.hostednumbers.size()));
                     }
                  } else {
                     HundredBingo.this.StopBingo();
                  }
               }
            },
            5000L
         );
   }

   private void InitGame(List<MapleCharacter> a1) {
      for (MapleCharacter chr : a1) {
         int[][] table = new int[5][5];
         List<Integer> temp = new ArrayList<>();

         for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
               int number;
               do {
                  number = Randomizer.rand(x * 15 + 1, (x + 1) * 15);
               } while (temp.contains(number) || number > (x + 1) * 15);

               temp.add(number);
               table[x][y] = number;
            }
         }

         table[2][2] = 0;
         this.players.put(chr, table);
         chr.getClient().getSession().writeAndFlush(CField.BingoEnterGame(table));
      }
   }

   public void StopBingo() {
      this.BingoTimer.cancel(true);

      for (MapleCharacter chr : this.players.keySet()) {
         chr.getClient().getSession().writeAndFlush(CField.playSE("multiBingo/gameover"));
         if (chr != null) {
            Field map = chr.getClient().getChannelServer().getMapFactory().getMap(922290200);
            chr.changeMap(map, map.getPortal(0));
            chr.setBingoGame(null);
            if (!this.rank.contains(chr) && chr.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() > 0) {
            }
         }
      }

      for (MapleCharacter chrx : this.rank) {
         if (chrx != null) {
            int ranknumber = this.rank.indexOf(chrx) + 1;
            if (ranknumber == 1
               ? chrx.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() >= 10
               : (
                  ranknumber >= 2 && ranknumber <= 10
                     ? chrx.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() >= 5
                     : (
                        ranknumber >= 11 && ranknumber <= 20
                           ? chrx.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() >= 4
                           : chrx.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() >= 3
                     )
               )) {
            }
         }
      }
   }
}
