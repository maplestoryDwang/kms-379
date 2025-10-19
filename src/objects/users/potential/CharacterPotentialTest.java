package objects.users.potential;

import database.DBConnection;
import database.DBEventManager;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import objects.users.skills.SkillFactory;
import objects.utils.Randomizer;

public class CharacterPotentialTest {
   public static void main(String[] args) {
      DBConnection.init();
      DBEventManager.init(4);
      SkillFactory.load();
      CharacterPotential.loadSkillLevelTables();

      for (int i = 0; i < 100000; i++) {
         List<CharacterPotentialHolder> newValues = new LinkedList<>();
         int rand = Randomizer.rand(0, 1000);
         int rank = 0;
         if (rand < 8) {
            rank = 3;
         } else if (rand < 100) {
            rank = 2;
         } else if (rand < 200) {
            rank = 1;
         }

         for (int next = 0; next < 3; next++) {
            int r = rank;
            if (next > 0) {
               if (rank > 0) {
                  r = rank - 1;
               }

               if (Randomizer.rand(0, 100) < 2) {
                  r = rank;
               }
            }

            newValues.add(CharacterPotential.getInstance().renewSkill(Collections.EMPTY_LIST, newValues, r, false, true));
         }

         for (CharacterPotentialHolder holder : newValues) {
            int count = 0;

            for (CharacterPotentialHolder h : newValues) {
               if (holder.getSkillId() == h.getSkillId()) {
                  count++;
               }
            }

            if (count > 1) {
               System.out.println("중복 " + holder.getSkillId());
            }
         }
      }
   }
}
