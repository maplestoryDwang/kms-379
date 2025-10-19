package objects.fields.gameobject;

public enum AffectedAreaElement {
   Physical(0),
   Ice(1),
   Fire(2),
   Light(3),
   Poison(4),
   Holy(5),
   Dark(6),
   Undead(7),
   Gravity(8),
   Number(9);

   private int type;

   private AffectedAreaElement(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
