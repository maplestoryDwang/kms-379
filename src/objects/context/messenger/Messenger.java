package objects.context.messenger;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import network.center.Center;
import network.game.GameServer;
import objects.users.MapleCharacter;

public class Messenger implements Serializable {
   private static final long serialVersionUID = 9179541993413738569L;
   private MessengerCharacter[] members = new MessengerCharacter[3];
   private String[] silentLink = new String[3];
   private int id;

   public Messenger(int id, MessengerCharacter chrfor) {
      this.id = id;
      this.addMem(0, chrfor);
   }

   public void addMem(int pos, MessengerCharacter chrfor) {
      if (this.members[pos] == null) {
         this.members[pos] = chrfor;
      }
   }

   public boolean containsMembers(MessengerCharacter member) {
      return this.getPositionByName(member.getName()) < 4;
   }

   public void addMember(MessengerCharacter member) {
      int position = this.getLowestPosition();
      if (position > -1 && position < 4) {
         this.addMem(position, member);
      }
   }

   public void removeMember(MessengerCharacter member) {
      int position = this.getPositionByName(member.getName());
      if (position > -1 && position < 4) {
         this.members[position] = null;
      }
   }

   public void silentRemoveMember(MessengerCharacter member) {
      int position = this.getPositionByName(member.getName());
      if (position > -1 && position < 4) {
         this.members[position] = null;
         this.silentLink[position] = member.getName();
      }
   }

   public void silentAddMember(MessengerCharacter member) {
      for (int i = 0; i < this.silentLink.length; i++) {
         if (this.silentLink[i] != null && this.silentLink[i].equalsIgnoreCase(member.getName())) {
            this.addMem(i, member);
            this.silentLink[i] = null;
            return;
         }
      }
   }

   public void updateMember(MessengerCharacter member) {
      for (int i = 0; i < this.members.length; i++) {
         MessengerCharacter chr = this.members[i];
         if (chr != null && chr.equals(member)) {
            this.members[i] = null;
            this.addMem(i, member);
            return;
         }
      }
   }

   public int getLowestPosition() {
      for (int i = 0; i < this.members.length; i++) {
         if (this.members[i] == null) {
            return i;
         }
      }

      return 4;
   }

   public int getPositionByName(String name) {
      for (int i = 0; i < this.members.length; i++) {
         MessengerCharacter messengerchar = this.members[i];
         if (messengerchar != null && messengerchar.getName().equalsIgnoreCase(name)) {
            return i;
         }
      }

      return 4;
   }

   public int getId() {
      return this.id;
   }

   public void setId(int id) {
      this.id = id;
   }

   @Override
   public int hashCode() {
      return 31 + this.id;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Messenger other = (Messenger)obj;
         return this.id == other.id;
      }
   }

   public Collection<MessengerCharacter> getMembers() {
      return Arrays.asList(this.members);
   }

   public boolean isMonitored() {
      int ch = -1;

      for (MessengerCharacter m : this.members) {
         if (m != null) {
            ch = Center.Find.findChannel(m.getName());
            if (ch != -1) {
               MapleCharacter mc = GameServer.getInstance(ch).getPlayerStorage().getCharacterByName(m.getName());
               if (mc != null && mc.getClient() != null && mc.getClient().isMonitored()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public String getMemberNamesDEBUG() {
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < this.members.length; i++) {
         if (this.members[i] != null) {
            sb.append(this.members[i].getName());
            if (i != this.members.length - 1) {
               sb.append(',');
            }
         }
      }

      return sb.toString();
   }
}
