package objects.context.messenger;

import java.io.Serializable;
import objects.users.MapleCharacter;

public class MessengerCharacter implements Serializable {
   private static final long serialVersionUID = 6215463252132450750L;
   private String name = "";
   private int id = -1;
   private int channel = -1;
   private boolean online = false;

   public MessengerCharacter(MapleCharacter maplechar) {
      this.name = maplechar.getName();
      this.channel = maplechar.getClient().getChannel();
      this.id = maplechar.getId();
      this.online = true;
   }

   public MessengerCharacter() {
   }

   public int getChannel() {
      return this.channel;
   }

   public boolean isOnline() {
      return this.online;
   }

   public void setOnline(boolean online) {
      this.online = online;
   }

   public String getName() {
      return this.name;
   }

   public int getId() {
      return this.id;
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      return 31 * result + (this.name == null ? 0 : this.name.hashCode());
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
         MessengerCharacter other = (MessengerCharacter)obj;
         if (this.name == null) {
            if (other.name != null) {
               return false;
            }
         } else if (!this.name.equals(other.name)) {
            return false;
         }

         return true;
      }
   }
}
