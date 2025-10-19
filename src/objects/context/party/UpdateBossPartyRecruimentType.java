package objects.context.party;

public enum UpdateBossPartyRecruimentType {
   CreateBossPartyRecruimentList(0),
   CancelBossPartyRecruiment(1),
   ChangeSetting(2);

   private int type;

   private UpdateBossPartyRecruimentType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
