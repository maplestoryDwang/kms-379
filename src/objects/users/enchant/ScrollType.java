package objects.users.enchant;

public enum ScrollType {
   UPGRADE(0),
   INNOCENT(1),
   WHITE(2),
   GOLDENHAMMER(3);

   final int type;

   private ScrollType(int type) {
      this.type = type;
   }
}
