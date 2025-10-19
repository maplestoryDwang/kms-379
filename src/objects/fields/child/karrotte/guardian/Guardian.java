package objects.fields.child.karrotte.guardian;

import java.util.ArrayList;
import java.util.List;

public class Guardian {
   public static final int ASSAULT_CYCLE = 60000;
   public static final int BONUS_ASSAULT_CYCLE = 150000;
   private List<GuardianEntry> guardians = new ArrayList<>();

   public Guardian(List<GuardianEntry> guardians) {
      this.setGuardians(guardians);
   }

   public List<GuardianEntry> getGuardians() {
      return this.guardians;
   }

   public void setGuardians(List<GuardianEntry> guardians) {
      this.guardians = guardians;
   }

   public GuardianEntry findGuardian(GuardianType type) {
      for (GuardianEntry entry : new ArrayList<>(this.guardians)) {
         if (entry.getType() == type) {
            return entry;
         }
      }

      return null;
   }
}
