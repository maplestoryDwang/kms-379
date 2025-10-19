package objects.item.rewards;

public class RandomRewards {
   private int itemID;
   private int maleItemID;
   private int femaleItemID;
   private int weight;
   private int bonusWeight;
   private int itemQuantity;
   private boolean announce;

   public RandomRewards(int itemID, int weight, int maleItemID, int femaleItemID, int bonusWeight, boolean announce) {
      this.itemID = itemID;
      this.weight = weight;
      this.maleItemID = maleItemID;
      this.femaleItemID = femaleItemID;
      this.bonusWeight = bonusWeight;
      this.announce = announce;
   }

   public RandomRewards(int itemID, int weight, int maleItemID, int bonusWeight, boolean announce) {
      this.itemID = itemID;
      this.weight = weight;
      this.maleItemID = maleItemID;
      this.femaleItemID = maleItemID;
      this.bonusWeight = bonusWeight;
      this.announce = announce;
      this.itemQuantity = 1;
   }

   public RandomRewards(int itemID, int weight, int maleItemID, int femaleItemID, int bonusWeight, int itemQuantity, boolean announce) {
      this.itemID = itemID;
      this.weight = weight;
      this.maleItemID = maleItemID;
      this.femaleItemID = femaleItemID;
      this.bonusWeight = bonusWeight;
      this.announce = announce;
      this.itemQuantity = itemQuantity;
   }

   public int getItemID() {
      return this.itemID;
   }

   public void setItemID(int itemID) {
      this.itemID = itemID;
   }

   public int getWeight() {
      return this.weight;
   }

   public void setWeight(int weight) {
      this.weight = weight;
   }

   public int getBonusWeight() {
      return this.bonusWeight;
   }

   public void setBonusWeight(int bonusWeight) {
      this.bonusWeight = bonusWeight;
   }

   public int getMaleItemID() {
      return this.maleItemID;
   }

   public void setMaleItemID(int maleItemID) {
      this.maleItemID = maleItemID;
   }

   public int getFemaleItemID() {
      return this.femaleItemID;
   }

   public void setFemaleItemID(int femaleItemID) {
      this.femaleItemID = femaleItemID;
   }

   public boolean isAnnounce() {
      return this.announce;
   }

   public void setAnnounce(boolean announce) {
      this.announce = announce;
   }

   public int getItemQuantity() {
      return this.itemQuantity;
   }

   public void setItemQuantity(int itemQuantity) {
      this.itemQuantity = itemQuantity;
   }
}
