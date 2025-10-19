package objects.fields.child.minigame.yutgame;

import network.encode.PacketEncoder;

public class YutGameYutInfo {
   private int aPigCount = 0;
   private int aDogCount = 0;
   private int aSheepCount = 0;
   private int aCowCount = 0;
   private int aHorseCount = 0;
   private int aReturnAPig = 0;

   public int getaPigCount() {
      return this.aPigCount;
   }

   public void setaPigCount(int aPigCount) {
      this.aPigCount = aPigCount;
   }

   public int getaDogCount() {
      return this.aDogCount;
   }

   public void setaDogCount(int aDogCount) {
      this.aDogCount = aDogCount;
   }

   public int getaSheepCount() {
      return this.aSheepCount;
   }

   public void setaSheepCount(int aSheepCount) {
      this.aSheepCount = aSheepCount;
   }

   public int getaCowCount() {
      return this.aCowCount;
   }

   public void setaCowCount(int aCowCount) {
      this.aCowCount = aCowCount;
   }

   public int getaHorseCount() {
      return this.aHorseCount;
   }

   public void setaHorseCount(int aHorseCount) {
      this.aHorseCount = aHorseCount;
   }

   public int getaReturnAPig() {
      return this.aReturnAPig;
   }

   public void setaReturnAPig(int aReturnAPig) {
      this.aReturnAPig = aReturnAPig;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getaPigCount());
      packet.writeInt(this.getaDogCount());
      packet.writeInt(this.getaSheepCount());
      packet.writeInt(this.getaCowCount());
      packet.writeInt(this.getaHorseCount());
      packet.writeInt(this.getaReturnAPig());
   }

   public boolean impossibleMove() {
      return this.getaPigCount() == 0
         && this.getaDogCount() == 0
         && this.getaSheepCount() == 0
         && this.getaCowCount() == 0
         && this.getaHorseCount() == 0
         && this.getaReturnAPig() == 0;
   }

   public void incrementYutCount(int index) {
      switch (index) {
         case 0:
            this.aPigCount++;
            break;
         case 1:
            this.aDogCount++;
            break;
         case 2:
            this.aSheepCount++;
            break;
         case 3:
            this.aCowCount++;
            break;
         case 4:
            this.aHorseCount++;
            break;
         case 5:
            this.aReturnAPig++;
      }
   }

   public void decrementYutCount(int index) {
      switch (index) {
         case 0:
            this.aPigCount--;
            break;
         case 1:
            this.aDogCount--;
            break;
         case 2:
            this.aSheepCount--;
            break;
         case 3:
            this.aCowCount--;
            break;
         case 4:
            this.aHorseCount--;
            break;
         case 5:
            this.aReturnAPig--;
      }
   }
}
