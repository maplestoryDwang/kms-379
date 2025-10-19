package objects.users.stats;

import java.util.Arrays;
import network.encode.PacketEncoder;
import objects.users.MapleCharacter;
import objects.users.skills.IndieTemporaryStatEntry;

public class Flag992 {
   long[] flags = new long[31];

   public Flag992() {
      Arrays.fill(this.flags, 0L);
   }

   public Flag992(long[] flags) {
      for (int i = 0; i < flags.length; i++) {
         this.flags[i] = flags[i];
      }
   }

   public void setFlag(SecondaryStatFlag flag) {
      if ((this.flags[flag.getPosition() - 1] & flag.getValue()) == 0L) {
         this.flags[flag.getPosition() - 1] |= flag.getValue();
      }
   }

   public void removeFlag(SecondaryStatFlag flag) {
      if ((this.flags[flag.getPosition() - 1] & flag.getValue()) != 0L) {
         this.flags[flag.getPosition() - 1] -= flag.getValue();
      }
   }

   public long[] getFlags() {
      return this.flags;
   }

   public void encode(PacketEncoder packet) {
      for (int i = this.flags.length; i >= 1; i--) {
         packet.writeInt(this.flags[i - 1]);
      }
   }

   public boolean check(SecondaryStatFlag flag) {
      return (this.flags[flag.getPosition() - 1] & flag.getValue()) != 0L;
   }

   public boolean check(Flag992 flag) {
      for (int i = this.flags.length; i >= 1; i--) {
         if ((this.flags[i - 1] & flag.flags[i - 1]) != 0L) {
            return true;
         }
      }

      return false;
   }

   public Flag992 FilterForRemote(Flag992 flag) {
      Flag992 ret = new Flag992();

      for (int i = this.flags.length; i >= 1; i--) {
         if ((this.flags[i - 1] & flag.flags[i - 1]) != 0L) {
            ret.flags[i - 1] = this.flags[i - 1] & flag.flags[i - 1];
         }
      }

      return ret;
   }

   public boolean hasBuff() {
      for (int i = this.flags.length; i >= 1; i--) {
         if (this.flags[i - 1] != 0L) {
            return true;
         }
      }

      return false;
   }

   public boolean hasBuffBySkillID(MapleCharacter player, int skillID) {
      for (int pos = 0; pos < 31; pos++) {
         for (int j = 0; j < 32; j++) {
            int vl = 1 << j;
            if ((this.flags[pos] & vl) != 0L) {
               String name = null;

               for (SecondaryStatFlag ssf : SecondaryStatFlag.values()) {
                  if (ssf.getBit() == 31 - j + 32 * (30 - pos)) {
                     if (ssf.isIndie()) {
                        IndieTemporaryStatEntry entry = player.getIndieTemporaryStat(ssf, skillID);
                        if (entry != null) {
                           return true;
                        }
                     } else if (player.getSecondaryStatReason(ssf) == skillID) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }
}
