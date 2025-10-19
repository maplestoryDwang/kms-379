package objects.context.party;

public enum RequestJoinPartyFromRecruimentType {
   RemoveRequest(7),
   RequestJoinParty(8);

   private int type;

   private RequestJoinPartyFromRecruimentType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
