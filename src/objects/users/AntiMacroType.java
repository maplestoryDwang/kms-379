package objects.users;

public enum AntiMacroType {
   FromUser(0),
   FromGM(1),
   Auto(2);

   private int type;

   private AntiMacroType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
