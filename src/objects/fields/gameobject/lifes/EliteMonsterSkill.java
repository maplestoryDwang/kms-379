package objects.fields.gameobject.lifes;

public class EliteMonsterSkill {
   private final int skill;
   private final int level;
   private final int type;

   public EliteMonsterSkill(int type, int skill, int level) {
      this.skill = skill;
      this.level = level;
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public int getSkill() {
      return this.skill;
   }

   public int getLevel() {
      return this.level;
   }
}
