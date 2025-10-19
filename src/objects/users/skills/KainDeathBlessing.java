package objects.users.skills;

import java.util.ArrayList;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.users.MapleCharacter;

public class KainDeathBlessing {
   private MapleCharacter player = null;
   private List<KainDeathBlessingEntry> entries = new ArrayList<>();

   public KainDeathBlessing(MapleCharacter player) {
      this.player = player;
   }

   public KainDeathBlessingEntry getEntry(int targetObjectID) {
      KainDeathBlessingEntry entry = null;

      for (KainDeathBlessingEntry e : new ArrayList<>(this.entries)) {
         if (e.getTargetObjectID() == targetObjectID) {
            entry = e;
            break;
         }
      }

      return entry;
   }

   public int getTotalEntryCount() {
      return this.entries.size();
   }

   public void addEntry(KainDeathBlessingEntry entry) {
      this.entries.add(entry);
   }

   public void removeEntry(int targetObjectID) {
      KainDeathBlessingEntry entry = this.getEntry(targetObjectID);
      if (entry != null) {
         this.entries.remove(entry);
      }
   }

   public void incrementStack(int targetObjectID) {
      KainDeathBlessingEntry entry = this.getEntry(targetObjectID);
      if (entry == null) {
         entry = new KainDeathBlessingEntry(targetObjectID, 1, 60000);
         this.addEntry(entry);
      } else {
         entry.incrementStack(this.player.hasBuffBySkillID(63141503));
      }

      this.updateDeathBlessing();
   }

   public void decrementStack(int targetObjectID) {
      KainDeathBlessingEntry entry = this.getEntry(targetObjectID);
      if (entry != null) {
         entry.decrementStack();
      }
   }

   public void updateDeathBlessing() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.KAIN_DEATH_BLESSING_RESULT.getValue());
      packet.write(1);
      packet.writeInt(this.entries.size());
      new ArrayList<>(this.entries).forEach(entry -> packet.writeInt(entry.getTargetObjectID()));
      packet.writeInt(15);
      packet.writeInt(20);
      packet.writeInt(90000);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(this.entries.size());
      new ArrayList<>(this.entries).forEach(entry -> entry.encode(packet));
      if (this.player != null) {
         this.player.send(packet.getPacket());
      }
   }
}
