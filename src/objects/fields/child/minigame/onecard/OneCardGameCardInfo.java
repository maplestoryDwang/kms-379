package objects.fields.child.minigame.onecard;

public class OneCardGameCardInfo {
   private int objectID;
   private int color;
   private int number;
   private int owner;

   public OneCardGameCardInfo(int objectID, int color, int number) {
      this.objectID = objectID;
      this.color = color;
      this.number = number;
      this.owner = 0;
   }

   public int getObjectID() {
      return this.objectID;
   }

   public int getColor() {
      return this.color;
   }

   public int getNumber() {
      return this.number;
   }

   public int getOwner() {
      return this.owner;
   }

   public void setOwner(int id) {
      this.owner = id;
   }
}
