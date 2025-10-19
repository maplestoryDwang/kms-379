package objects.context.expedition;

import java.util.ArrayList;
import java.util.List;
import network.center.Center;
import objects.context.party.Party;

public class Expedition {
   private List<Integer> parties;
   private ExpeditionType et;
   private int leaderId;
   private int id;

   public Expedition(ExpeditionType ett, int leaderId, int id) {
      this.et = ett;
      this.id = id;
      this.leaderId = leaderId;
      this.parties = new ArrayList<>(ett.maxParty);
   }

   public ExpeditionType getType() {
      return this.et;
   }

   public int getLeader() {
      return this.leaderId;
   }

   public List<Integer> getParties() {
      return this.parties;
   }

   public int getId() {
      return this.id;
   }

   public int getAllMembers() {
      int ret = 0;

      for (int i = 0; i < this.parties.size(); i++) {
         Party pp = Center.Party.getParty(this.parties.get(i));
         if (pp == null) {
            this.parties.remove(i);
         } else {
            ret += pp.getPartyMemberList().size();
         }
      }

      return ret;
   }

   public int getFreeParty() {
      for (int i = 0; i < this.parties.size(); i++) {
         Party pp = Center.Party.getParty(this.parties.get(i));
         if (pp == null) {
            this.parties.remove(i);
         } else if (pp.getPartyMemberList().size() < 6) {
            return pp.getId();
         }
      }

      return this.parties.size() < this.et.maxParty ? 0 : -1;
   }

   public int getIndex(int partyId) {
      for (int i = 0; i < this.parties.size(); i++) {
         if (this.parties.get(i) == partyId) {
            return i;
         }
      }

      return -1;
   }

   public void setLeader(int newLead) {
      this.leaderId = newLead;
   }
}
