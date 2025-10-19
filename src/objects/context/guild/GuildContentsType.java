package objects.context.guild;

public enum GuildContentsType {
   FLAG_RACE(0),
   CULVERT(2),
   WEEK_MISSIONS(3);

   private final int type;

   private GuildContentsType(int val) {
      this.type = val;
   }

   public int getType() {
      return this.type;
   }
}
