package objects.users.potential;

import objects.utils.Randomizer;

public class CharacterPotentialSkillLevel {
   private int skillID;
   private CharacterPotentialSkillLevelEntry entry;

   public CharacterPotentialSkillLevel(int skillID, CharacterPotentialSkillLevelEntry entry) {
      this.skillID = skillID;
      this.entry = entry;
   }

   public int getSkillID() {
      return this.skillID;
   }

   public void setSkillID(int skillID) {
      this.skillID = skillID;
   }

   public CharacterPotentialSkillLevelEntry getEntry() {
      return this.entry;
   }

   public void setEntry(CharacterPotentialSkillLevelEntry entry) {
      this.entry = entry;
   }

   public int getSkillLevelForRandom() {
      int[] slvs = this.entry.getSkillLevels();
      return slvs[Randomizer.rand(0, slvs.length - 1)];
   }
}
