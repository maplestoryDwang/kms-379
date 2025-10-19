package objects.context.guild;

public enum GuildSubType {
   ComeToMe(46),
   InviteAlliance(51);

   int type;

   private GuildSubType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
