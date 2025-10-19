package objects.users.skills;

public enum HoyoungAttributes {
   None(0),
   Heaven(1),
   Earth(2),
   Human(4);

   private int type;

   private HoyoungAttributes(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
