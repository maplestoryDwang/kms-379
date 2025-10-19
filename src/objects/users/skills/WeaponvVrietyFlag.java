package objects.users.skills;

public enum WeaponvVrietyFlag {
   NONE(0),
   SUMMON_CUTTING_SCIMITAR(1),
   SUMMON_SCRATCHING_CLAW(2),
   SUMMON_THROWING_WING_DAGGER(4),
   SUMMON_SHOOTING_SHOTGUN(8),
   SUMMON_SLASHING_KNIFE(16),
   SUMMON_RELEASING_BOMB(32),
   SUMMON_STRIKING_BRICK(64),
   SUMMON_BITING_NEEDLE_BAT(128),
   TAKE_DOWN(255);

   int flag;

   private WeaponvVrietyFlag(int flag) {
      this.flag = flag;
   }

   public int getFlag() {
      return this.flag;
   }

   public static WeaponvVrietyFlag getFlagBySkillID(int skillID) {
      switch (skillID) {
         case 64001002:
         case 64001013:
            return SUMMON_CUTTING_SCIMITAR;
         case 64101001:
            return SUMMON_SCRATCHING_CLAW;
         case 64101002:
         case 64101008:
            return SUMMON_THROWING_WING_DAGGER;
         case 64111002:
            return SUMMON_SHOOTING_SHOTGUN;
         case 64111003:
            return SUMMON_SLASHING_KNIFE;
         case 64111004:
         case 64111012:
            return SUMMON_RELEASING_BOMB;
         case 64121001:
         case 64121012:
         case 64121013:
         case 64121014:
         case 64121015:
         case 64121017:
         case 64121018:
         case 64121019:
            return TAKE_DOWN;
         case 64121003:
         case 64121011:
         case 64121016:
            return SUMMON_BITING_NEEDLE_BAT;
         case 64121021:
         case 64121022:
         case 64121023:
         case 64121024:
            return SUMMON_STRIKING_BRICK;
         default:
            return NONE;
      }
   }
}
