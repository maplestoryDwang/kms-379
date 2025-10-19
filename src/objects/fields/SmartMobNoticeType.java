package objects.fields;

public enum SmartMobNoticeType {
   Normal(0),
   Aggro(1),
   Warning(2);

   private int type;

   private SmartMobNoticeType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
