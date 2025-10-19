package objects.fields.child.union;

public class UnionRankData {
   private int attackerCount;
   private int coinStackMax;
   private int level;
   private int rank;
   private int tier;

   public UnionRankData(int attackerCount, int coinStackMax, int level, int rank, int tier) {
      this.attackerCount = attackerCount;
      this.coinStackMax = coinStackMax;
      this.level = level;
      this.tier = tier;
   }

   public int getAttackerCount() {
      return this.attackerCount;
   }

   public void setAttackerCount(int attackerCount) {
      this.attackerCount = attackerCount;
   }

   public int getCoinStackMax() {
      return this.coinStackMax;
   }

   public void setCoinStackMax(int coinStackMax) {
      this.coinStackMax = coinStackMax;
   }

   public int getLevel() {
      return this.level;
   }

   public void setLevel(int level) {
      this.level = level;
   }

   public int getRank() {
      return this.rank;
   }

   public void setRank(int rank) {
      this.rank = rank;
   }

   public int getTier() {
      return this.tier;
   }

   public void setTier(int tier) {
      this.tier = tier;
   }
}
