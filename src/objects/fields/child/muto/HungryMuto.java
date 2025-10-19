package objects.fields.child.muto;

import java.awt.Point;
import java.util.concurrent.ScheduledFuture;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.context.party.Party;
import objects.users.MapleCharacter;
import objects.utils.Randomizer;
import objects.utils.Timer;

public class HungryMuto {
   private int[][] currentRecipes;
   private int difficulty;
   private int score;
   private int time;
   private int type;
   private int count;
   private long gameStartTime;
   private boolean enhanceMob = false;
   private static Point[] randArea = new Point[]{
      new Point(105, -354),
      new Point(2644, -345),
      new Point(-929, -356),
      new Point(3778, -343),
      new Point(-1127, -764),
      new Point(3935, -841),
      new Point(1434, -791),
      new Point(1405, -1637)
   };
   private ScheduledFuture<?> gameTask = null;

   public int getDropRate(int itemID) {
      switch (itemID) {
         case 2435856:
         case 2435857:
         case 2435858:
         case 2435859:
         case 2435864:
         case 2435865:
         case 2435868:
         case 2435869:
            return 83;
         case 2435860:
         case 2435861:
         case 2435862:
         case 2435863:
            return 66;
         case 2435866:
         case 2435867:
            return 50;
         case 2435870:
         case 2435871:
            return 33;
         case 2435872:
            return 100;
         default:
            return 0;
      }
   }

   public Point getRandArea() {
      return randArea[Randomizer.nextInt(randArea.length)];
   }

   public void setRecipes(int type) {
      int[][] recipes = FoodRecipe.getRecipe(type);
      this.currentRecipes = new int[recipes.length][3];

      for (int i = 0; i < recipes.length; i++) {
         this.currentRecipes[i][0] = recipes[i][0];
         this.currentRecipes[i][1] = recipes[i][1];
         this.currentRecipes[i][2] = 0;
      }
   }

   public int[][] getRecipes() {
      return this.currentRecipes;
   }

   public void updateRecipe(int itemID, int quantity) {
      for (int i = 0; i < this.currentRecipes.length; i++) {
         int itemID_ = this.currentRecipes[i][0];
         if (itemID_ == itemID) {
            this.currentRecipes[i][2] = quantity;
         }
      }
   }

   public boolean checkClear() {
      boolean ret = true;

      for (int i = 0; i < this.currentRecipes.length; i++) {
         if (this.currentRecipes[i][1] > this.currentRecipes[i][2]) {
            ret = false;
            break;
         }
      }

      return ret;
   }

   public void cancelTask() {
      if (this.gameTask != null) {
         this.gameTask.cancel(true);
         this.gameTask = null;
      }
   }

   public boolean addScore() {
      int recipeLength = this.getRecipes().length;
      int delta = 200;
      boolean perfect = false;
      switch (recipeLength) {
         case 3:
            delta = 250;
            break;
         case 4:
            delta = 300;
      }

      if (getBonusTime(129900, this.getRecipes().length) <= this.getTime()) {
         delta += 50;
         perfect = true;
      }

      this.score = Math.min(990, this.score + delta);
      return perfect;
   }

   public int getScore() {
      return this.score;
   }

   public void setScore(int score) {
      this.score = score;
   }

   public boolean isEnhanceMob() {
      return this.enhanceMob;
   }

   public void setEnhanceMob(boolean enhanceMob) {
      this.enhanceMob = enhanceMob;
   }

   public int getTime() {
      return this.time;
   }

   public void setTime(int time) {
      this.time = Math.max(0, time);
      if (this.count >= 10) {
         this.count = 0;
      }

      this.count++;
   }

   public int getCount() {
      return this.count;
   }

   public long getGameStartTime() {
      return this.gameStartTime;
   }

   public void setGameStartTime(long gameStartTime) {
      this.gameStartTime = gameStartTime;
   }

   public void setType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public int getDifficulty() {
      return this.difficulty;
   }

   public void setDifficulty(int difficulty) {
      this.difficulty = difficulty;
   }

   public boolean checkRecipes(FoodType type) {
      return false;
   }

   public HungryMuto(int type) {
      this.type = type;
      int[][] recipes = FoodRecipe.getRecipe(type);
      this.currentRecipes = new int[recipes.length][3];

      for (int i = 0; i < recipes.length; i++) {
         this.currentRecipes[i][0] = recipes[i][0];
         this.currentRecipes[i][1] = recipes[i][1];
         this.currentRecipes[i][2] = 0;
      }

      this.time = 129900;
      this.score = 500;
   }

   public static int getBonusTime(int time, int recipesLength) {
      return (int)(time * getBonusRate(recipesLength));
   }

   public static double getBonusRate(int recipesLength) {
      switch (recipesLength) {
         case 2:
            return 0.5;
         case 3:
            return 0.3;
         case 4:
            return 0.2;
         default:
            return 0.5;
      }
   }

   public void broadcastPacket(Party party, MapleCharacter chr, byte[] packets) {
      if (party != null) {
         party.getPartyMemberList().stream().filter(p -> p.getChannel() == chr.getClient().getChannel()).filter(p -> p.isOnline()).forEach(p -> {
            MapleCharacter player = chr.getClient().getChannelServer().getPlayerStorage().getCharacterById(p.getId());
            if (player != null) {
               player.send(packets);
            }
         });
      }
   }

   public void changeFood(MapleCharacter chr) {
      int type = Randomizer.rand(0, 7);
      if (this.enhanceMob) {
         type = Randomizer.rand(8, FoodType.values().length - 1);
      }

      this.setType(type);
      this.setRecipes(type);
      this.setTime(129900);
      chr.getMap()
         .broadcastMessage(
            new HungryMuto.GameInit(
                  FoodType.getFoodType(type), this.difficulty, this.getTime(), getBonusTime(this.getTime(), this.getRecipes().length), this.getRecipes()
               )
               .encode()
         );
      chr.getMap().broadcastMessage(CField.fieldValue("foodType", String.valueOf(type)));
   }

   public void startGame(final MapleCharacter chr, int difficulty) {
      this.setGameStartTime(System.currentTimeMillis());
      this.setDifficulty(difficulty);
      this.gameTask = Timer.MapTimer.getInstance().register(new Runnable() {
         @Override
         public void run() {
            if (HungryMuto.this.getGameStartTime() != 0L && System.currentTimeMillis() - HungryMuto.this.getGameStartTime() > 600000L) {
               Party party = chr.getParty();
               if (party != null) {
                  party.getPartyMemberList().stream().filter(p -> p.getChannel() == chr.getClient().getChannel()).filter(p -> p.isOnline()).forEach(p -> {
                     MapleCharacter player = chr.getClient().getChannelServer().getPlayerStorage().getCharacterById(p.getId());
                     if (player != null) {
                        player.warp(450002023);
                        player.setHungryMuto(null);
                        player.send(CField.MapEff("Map/Effect3.img/hungryMuto/TimeOver"));
                     }
                  });
               }

               HungryMuto.this.gameTask.cancel(true);
               HungryMuto.this.gameTask = null;
            } else {
               HungryMuto.this.setTime(HungryMuto.this.getTime() - 1000);
               if (HungryMuto.this.getTime() <= 0) {
                  HungryMuto.this.changeFood(chr);
                  HungryMuto.this.broadcastPacket(chr.getParty(), chr, CField.MapEff("Map/Effect3.img/hungryMuto/failed"));
                  HungryMuto.this.broadcastPacket(chr.getParty(), chr, CField.MapEff("Map/Effect3.img/hungryMutoMsg/msg6"));
               }

               HungryMuto.this.broadcastPacket(chr.getParty(), chr, CField.fieldValue("time", String.valueOf(HungryMuto.this.getTime())));
               if (HungryMuto.this.getCount() >= 10) {
                  HungryMuto.this.setScore(HungryMuto.this.getScore() - 10);
                  HungryMuto.this.broadcastPacket(chr.getParty(), chr, CField.fieldValue("score", String.valueOf(HungryMuto.this.getScore())));
                  if (HungryMuto.this.getScore() <= 0) {
                     HungryMuto.this.gameOver(chr, false);
                     return;
                  }
               }
            }
         }
      }, 1000L);
   }

   public void gameOver(MapleCharacter chr, boolean clear) {
      Party party = chr.getParty();
      if (party != null) {
         party.getPartyMemberList().stream().filter(p -> p.getChannel() == chr.getClient().getChannel()).filter(p -> p.isOnline()).forEach(p -> {
            MapleCharacter player = chr.getClient().getChannelServer().getPlayerStorage().getCharacterById(p.getId());
            if (player != null) {
               if (clear) {
                  int clearTime = (int)(System.currentTimeMillis() - this.getGameStartTime());
                  player.setMutoClearTime(clearTime);
                  int rank = 2;
                  if (clearTime < 300000) {
                     rank = 0;
                  } else if (clearTime < 480000) {
                     rank = 1;
                  }

                  if (rank == 0 && player.getQuestStatus(2000005) == 1 && player.getOneInfo(2000005, "clear") == null) {
                     player.updateOneInfo(2000005, "clear", "1");
                  }

                  player.setMutoClearRank(rank);
                  player.setMutoClearDifficulty(this.getDifficulty());
                  player.CountAdd("hungry_muto");
                  player.setMutoHighRecord(rank, this.getDifficulty());
               }

               player.warp(clear ? 450002024 : 450002023);
               player.setHungryMuto(null);
               player.send(CField.MapEff(clear ? "Map/Effect3.img/hungryMuto/Clear" : "Map/Effect2.img/event/gameover"));
            }
         });
      }

      this.gameTask.cancel(true);
      this.gameTask = null;
   }

   public void encodeHeader(PacketEncoder mplew) {
      mplew.writeShort(SendPacketOpcode.FIELD_HUNGRY_MUTO_RESULT.getValue());
      mplew.writeInt(this.type);
   }

   public static class GameInit extends HungryMuto {
      private FoodType foodType;
      private int difficulty;
      private int time;
      private int bonusTime;
      private int[][] recipes;

      public GameInit(FoodType foodType, int difficulty, int time, int bonusTime, int[][] recipes) {
         super(HungryMutoType.HungryMutoResult_GameInit.getType());
         this.foodType = foodType;
         this.difficulty = difficulty;
         this.time = time;
         this.bonusTime = bonusTime;
         this.recipes = recipes;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(this.foodType.getItemID());
         mplew.writeInt(this.foodType.getType());
         mplew.writeInt(this.difficulty);
         mplew.writeInt(this.time);
         mplew.writeInt(this.bonusTime);
         mplew.writeInt(this.recipes.length);

         for (int i = 0; i < this.recipes.length; i++) {
            mplew.writeInt(this.recipes[i][0]);
            mplew.writeInt(this.recipes[i][1]);
            mplew.writeInt(this.recipes[i][2]);
         }

         return mplew.getPacket();
      }
   }

   public static class PickupItemUpdate extends HungryMuto {
      private int playerID;
      private int itemID;
      private int quantity;

      public PickupItemUpdate(int playerID, int itemID, int quantity) {
         super(HungryMutoType.HungryMutoResult_PickupItemUpdate.getType());
         this.playerID = playerID;
         this.itemID = itemID;
         this.quantity = quantity;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(1);
         mplew.writeInt(this.playerID);
         mplew.writeInt(this.itemID);
         mplew.writeInt(this.quantity);
         return mplew.getPacket();
      }
   }

   public static class RecipeUpdate extends HungryMuto {
      private FoodType foodType;
      private int difficulty;
      private int[][] recipes;

      public RecipeUpdate(FoodType foodType, int difficulty, int[][] recipes) {
         super(HungryMutoType.HungryMutoResult_RecipeUpdate.getType());
         this.foodType = foodType;
         this.difficulty = difficulty;
         this.recipes = recipes;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(this.foodType.getItemID());
         mplew.writeInt(this.foodType.getType());
         mplew.writeInt(this.difficulty);
         mplew.writeInt(this.recipes.length);

         for (int i = 0; i < this.recipes.length; i++) {
            mplew.writeInt(this.recipes[i][0]);
            mplew.writeInt(this.recipes[i][1]);
            mplew.writeInt(this.recipes[i][2]);
         }

         return mplew.getPacket();
      }
   }

   public static class TimerSet extends HungryMuto {
      private int time;

      public TimerSet(int time) {
         super(HungryMutoType.HungryMutoResult_TimerSet.getType());
         this.time = time;
      }

      public byte[] encode() {
         PacketEncoder mplew = new PacketEncoder();
         super.encodeHeader(mplew);
         mplew.writeInt(this.time);
         return mplew.getPacket();
      }
   }
}
