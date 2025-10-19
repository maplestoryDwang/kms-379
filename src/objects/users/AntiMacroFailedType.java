package objects.users;

public enum AntiMacroFailedType {
   Disconnection(0),
   InputFailed(1),
   Timeout(2);

   private int type;

   private AntiMacroFailedType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
