package objects.users.skills;

import java.util.ArrayList;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.users.MapleCharacter;

public class KainStackSkill {
   private MapleCharacter player = null;
   private List<KainStackSkillEntry> skills = new ArrayList<>();

   public KainStackSkill(MapleCharacter player) {
      this.player = player;
   }

   public void updatePlayer(MapleCharacter player) {
      this.player = player;
   }

   public boolean hasSkill(int skillID) {
      boolean check = false;

      for (KainStackSkillEntry entry : this.skills) {
         if (entry.getSkillID() == skillID) {
            check = true;
            break;
         }
      }

      return check;
   }

   public void updateStackSkill(KainStackSkillEntry entry) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.KAIN_STACK_SKILL_RESULT.getValue());
      entry.encode(packet);
      this.player.send(packet.getPacket());
   }

   public void addKainStackSkill(int skillID, int maxCharge, int cycle) {
      KainStackSkillEntry entry = new KainStackSkillEntry(skillID, cycle, maxCharge);
      this.skills.add(entry);
      this.updateStackSkill(entry);
   }

   public void incrementStack(int skillID) {
      KainStackSkillEntry entry = null;

      for (KainStackSkillEntry e : this.skills) {
         if (e.getSkillID() == skillID) {
            entry = e;
            break;
         }
      }

      if (entry != null) {
         int maxCharge = entry.getMaxCharge();
         if (maxCharge > entry.getCurrentStack()) {
            boolean useTempCycle = false;
            if (skillID == 63121040) {
               long cooldown = this.player.getRemainCooltime(63121140);
               if (cooldown > 0L) {
                  useTempCycle = true;
               }
            }

            if (!useTempCycle) {
               entry.setCurrentStack(entry.getCurrentStack() + 1);
            }
         }

         this.updateStackSkill(entry);
      }
   }

   public void decrementStack(int skillID) {
      KainStackSkillEntry entry = null;

      for (KainStackSkillEntry e : this.skills) {
         if (e.getSkillID() == skillID) {
            entry = e;
            break;
         }
      }

      if (entry != null) {
         int maxCharge = entry.getMaxCharge();
         if (0 <= entry.getCurrentStack() - 1) {
            entry.setCurrentStack(entry.getCurrentStack() - 1);
         }

         this.updateStackSkill(entry);
      }
   }
}
