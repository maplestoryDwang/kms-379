package objects.users.potential;

public class CharacterPotentialHolder {
   private int skillId = 0;
   private byte skillLevel = 0;
   private byte maxLevel = 0;
   private byte rank = 0;
   private boolean locked = false;

   public CharacterPotentialHolder(int skillId, byte skillLevel, byte maxLevel, byte rank, boolean locked) {
      this.skillId = skillId;
      this.skillLevel = skillLevel;
      this.maxLevel = maxLevel;
      this.rank = rank;
      this.locked = locked;
   }

   public int getSkillId() {
      return this.skillId;
   }

   public byte getSkillLevel() {
      return this.skillLevel;
   }

   public byte getMaxLevel() {
      return this.maxLevel;
   }

   public byte getRank() {
      return this.rank;
   }

   public boolean isLocked() {
      return this.locked;
   }
}
