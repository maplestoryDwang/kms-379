package objects.item;

public class BlackRebirthFlame {
   private int blackRebirthItemID;
   private Equip beforeItem = null;
   private Equip afterItem = null;

   public BlackRebirthFlame(int blackRebirthItemID, Equip beforeItem, Equip afterItem) {
      this.blackRebirthItemID = blackRebirthItemID;
      this.beforeItem = beforeItem;
      this.afterItem = afterItem;
   }

   public int getBlackRebirthItemID() {
      return this.blackRebirthItemID;
   }

   public Equip getAfterItem() {
      return this.afterItem;
   }

   public Equip getBeforeItem() {
      return this.beforeItem;
   }
}
