package objects.users;

import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;

public class MapleCabinet {
   private List<MapleCabinetItem> items;
   private int nextIndex;

   public MapleCabinet() {
      this.items = new ArrayList<>();
   }

   public MapleCabinet(List<MapleCabinetItem> items) {
      this.items = items;
      int idx = 0;

      for (MapleCabinetItem item : items) {
         if (item.getIndex() > idx) {
            idx = item.getIndex();
         }
      }

      this.nextIndex = ++idx;
   }

   public void addCabinetItem(MapleCabinetItem mci) {
      this.items.add(mci);
   }

   public MapleCabinetItem getCabinetItem(int index) {
      for (MapleCabinetItem item : new ArrayList<>(this.items)) {
         if (item.getIndex() == index) {
            return item;
         }
      }

      return null;
   }

   public List<MapleCabinetItem> getItems() {
      return this.items;
   }

   public void removeCabinetItem(int index) {
      for (MapleCabinetItem item : new ArrayList<>(this.items)) {
         if (item.getIndex() == index) {
            this.items.remove(item);
            break;
         }
      }
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.items.size());

      for (MapleCabinetItem item : new ArrayList<>(this.items)) {
         item.encode(packet);
      }

      packet.write(0);
   }

   public boolean checkAlert() {
      return !this.items.isEmpty();
   }

   public int getNextIndex() {
      return this.nextIndex++;
   }

   public void setNextIndex(int nextIndex) {
      this.nextIndex = nextIndex;
   }
}
