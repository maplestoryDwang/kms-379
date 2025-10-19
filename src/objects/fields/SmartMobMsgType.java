package objects.fields;

public enum SmartMobMsgType {
   None(0),
   Attack(1),
   Skill(2),
   SwitchController(3),
   UNK(4),
   Field(5);

   private int type;

   private SmartMobMsgType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
