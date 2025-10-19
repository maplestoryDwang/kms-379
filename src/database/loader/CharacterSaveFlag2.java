package database.loader;

public enum CharacterSaveFlag2 {
   INTENSE_POWER_CRYSTAL(1),
   MANNEQUIN(2),
   MANNEQUIN_SLOT_MAX(4),
   BLACK_LIST(8),
   SKILL_ALARM(16),
   LINKSKILL_PRESET(32),
   ACHIEVEMENT(64);

   private int flag;

   private CharacterSaveFlag2(int flag) {
      this.flag = flag;
   }

   public int getFlag() {
      return this.flag;
   }
}
