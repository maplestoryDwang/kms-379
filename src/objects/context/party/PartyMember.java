package objects.context.party;

import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;
import network.models.PacketHelper;

public class PartyMember {
   public static int MAX_USER = 6;
   private PartyMemberEntry leader;
   private List<PartyMemberEntry> partyMemberList = new ArrayList<>();
   private List<PartyMemberEntry> registerRequestList = new ArrayList<>();

   public PartyMember(PartyMemberEntry leader) {
      this.setLeader(leader);
      this.partyMemberList.add(leader);
   }

   public void addMember(PartyMemberEntry member) {
      this.partyMemberList.add(member);
   }

   public void removeMember(PartyMemberEntry member) {
      this.partyMemberList.remove(member);
   }

   public void updateMember(PartyMemberEntry member) {
      for (int i = 0; i < this.partyMemberList.size(); i++) {
         PartyMemberEntry chr = this.partyMemberList.get(i);
         if (chr.equals(member)) {
            this.partyMemberList.set(i, member);
         }
      }
   }

   public PartyMemberEntry getMemberById(int id) {
      for (PartyMemberEntry chr : this.partyMemberList) {
         if (chr.getId() == id) {
            return chr;
         }
      }

      return null;
   }

   public PartyMemberEntry getMemberByIndex(int index) {
      return this.partyMemberList.size() <= index ? null : this.partyMemberList.get(index);
   }

   public List<PartyMemberEntry> getPartyMemberList() {
      return new ArrayList<>(this.partyMemberList);
   }

   public PartyMemberEntry getLeader() {
      return this.leader;
   }

   public void setLeader(PartyMemberEntry leader) {
      this.leader = leader;
   }

   public void encode(PacketEncoder packet) {
      for (int i = 0; i < MAX_USER; i++) {
         PartyMemberEntry entry = this.getMemberByIndex(i);
         if (entry == null) {
            packet.writeInt(0);
         } else {
            entry.encode(packet);
         }
      }

      packet.writeInt(this.leader.getId());
      packet.writeInt(this.registerRequestList.size());
      this.registerRequestList.stream().forEach(p -> {
         p.encode(packet);
         packet.writeLong(PacketHelper.getKoreanTimestamp(System.currentTimeMillis()));
      });
   }

   public List<PartyMemberEntry> getRegisterRequestList() {
      return new ArrayList<>(this.registerRequestList);
   }

   public void addRegisterRequestPlayer(PartyMemberEntry player) {
      this.registerRequestList.add(player);
   }

   public void removeRegisterRequestPlayer(int playerID) {
      for (PartyMemberEntry entry : new ArrayList<>(this.registerRequestList)) {
         if (entry.getId() == playerID) {
            this.registerRequestList.remove(entry);
         }
      }
   }

   public void clearRegisterRequestPlayer() {
      this.registerRequestList.clear();
   }
}
