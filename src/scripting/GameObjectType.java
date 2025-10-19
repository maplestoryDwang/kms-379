package scripting;

public enum GameObjectType {
   None(0),
   GameObject(1),
   Creature(2),
   User(3),
   Npc(4),
   Mob(5),
   Pet(6),
   Item(7),
   Portal(8),
   Reactor(9),
   Employee(10),
   No(11);

   private int type;

   private GameObjectType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
