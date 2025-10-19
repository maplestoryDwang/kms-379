package objects.fields.child.union;

import java.util.List;

public class UnionCharacterRank {
   private List<Integer> rank;

   public UnionCharacterRank(List<Integer> rank) {
      this.rank = rank;
   }

   public List<Integer> getRank() {
      return this.rank;
   }

   public void setRank(List<Integer> rank) {
      this.rank = rank;
   }

   public int getRank(int level) {
      for (int i = this.rank.size() - 1; i >= 0; i--) {
         if (level >= this.rank.get(i)) {
            return i;
         }
      }

      return -1;
   }
}
