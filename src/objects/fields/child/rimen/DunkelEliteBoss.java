package objects.fields.child.rimen;

import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;
import objects.users.MapleCharacter;
import objects.utils.Randomizer;

public class DunkelEliteBoss {
   private List<DunkelEliteBossEntry> entrys = new ArrayList<>();
   private int[][] doubleSet = new int[][]{
      {2, 8}, {0, 2}, {8, 9}, {1, 5}, {0, 7}, {1, 3}, {2, 3}, {2, 4}, {5, 8}, {6, 8}, {3, 9}, {1, 7}, {1, 9}, {0, 3}, {0, 4}, {0, 8}, {6, 9}
   };
   private int[][] tripleSet = new int[][]{{0, 2, 6}, {0, 7, 9}, {0, 3, 9}, {1, 2, 5}, {2, 6, 8}, {0, 4, 8}, {1, 7, 9}, {5, 8, 9}, {2, 5, 9}, {0, 1, 2}};

   public void encode(PacketEncoder packet) {
      for (DunkelEliteBossEntry entry : this.entrys) {
         packet.write(1);
         entry.encode(packet);
      }

      packet.write(0);
   }

   public void createEntry(int size, int bossObjectID, MapleCharacter target) {
      int[] set = null;
      if (size == 1) {
         set = new int[]{Randomizer.rand(0, 9)};
      } else if (size == 2) {
         set = this.doubleSet[Randomizer.rand(0, this.doubleSet.length - 1)];
      } else if (size == 3) {
         set = this.tripleSet[Randomizer.rand(0, this.tripleSet.length - 1)];
      }

      for (int type : set) {
         boolean firstSet = true;

         for (DunkelEliteBossEntry e : this.entrys) {
            if (e.getType() == 0 || e.getType() == 3 || e.getType() == 8 || e.getType() == 9) {
               firstSet = false;
            }
         }

         DunkelEliteBossEntry entry = DunkelEliteBossEntry.initEntry(type, bossObjectID, target, firstSet);
         if (entry != null) {
            this.entrys.add(entry);
         }
      }
   }
}
