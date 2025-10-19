package objects.contents;

public class SpeedLadder {
   int round;
   byte right;
   byte line;
   byte odd;
   long reward;

   public SpeedLadder(int round, byte right, byte line, byte odd) {
      this.round = round;
      this.right = right;
      this.line = line;
      this.odd = odd;
   }

   public int getRound() {
      return this.round;
   }

   public byte getRight() {
      return this.right;
   }

   public byte getLine() {
      return this.line;
   }

   public byte getOdd() {
      return this.odd;
   }

   public long getReward() {
      return this.reward;
   }

   public void setReward(long reward) {
      this.reward = reward;
   }
}
