package objects.users.skills;

import java.util.ArrayList;
import java.util.List;
import objects.utils.Pair;

public class RandomSkillInfo {
   private int prob;
   private List<Pair<Integer, Integer>> skillList = new ArrayList<>();

   public int getProb() {
      return this.prob;
   }

   public void setProb(int prob) {
      this.prob = prob;
   }

   public List<Pair<Integer, Integer>> getSkillList() {
      return this.skillList;
   }

   public void addSkillList(Pair<Integer, Integer> pair) {
      this.skillList.add(pair);
   }
}
