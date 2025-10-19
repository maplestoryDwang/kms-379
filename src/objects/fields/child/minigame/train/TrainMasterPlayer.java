package objects.fields.child.minigame.train;

import java.util.ArrayList;
import java.util.List;
import objects.users.MapleCharacter;
import objects.utils.Pair;

public class TrainMasterPlayer {
   private MapleCharacter chr;
   private List<Pair<Integer, Integer>> data = new ArrayList<>();
   private List<List<Pair<Integer, Integer>>> setData = List.of(
      List.of(
         new Pair<>(7, 16),
         new Pair<>(240, 40),
         new Pair<>(1, 10),
         new Pair<>(0, 40),
         new Pair<>(8, 16),
         new Pair<>(1, 40),
         new Pair<>(9, 16),
         new Pair<>(2, 40),
         new Pair<>(10, 16),
         new Pair<>(3, 37),
         new Pair<>(10, 15),
         new Pair<>(4, 37),
         new Pair<>(10, 14),
         new Pair<>(5, 37),
         new Pair<>(10, 13),
         new Pair<>(6, 37),
         new Pair<>(10, 12),
         new Pair<>(7, 37),
         new Pair<>(10, 11),
         new Pair<>(8, 37),
         new Pair<>(10, 10),
         new Pair<>(9, 37),
         new Pair<>(10, 9)
      )
   );

   public TrainMasterPlayer(MapleCharacter chr) {
      this.chr = chr;
   }

   public MapleCharacter getPlayer() {
      return this.chr;
   }

   public void set(int index) {
      this.data.addAll(this.setData.get(index));
   }

   public List<Pair<Integer, Integer>> getData() {
      return this.data;
   }
}
