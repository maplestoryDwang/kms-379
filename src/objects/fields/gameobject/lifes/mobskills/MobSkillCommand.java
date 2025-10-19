package objects.fields.gameobject.lifes.mobskills;

public class MobSkillCommand {
   private int skillCommand;
   private int skillCommandLevel;
   private int attackCommand;

   public MobSkillCommand(int skillCommand, int skillCommandLevel, int attackCommand) {
      this.skillCommand = skillCommand;
      this.skillCommandLevel = skillCommandLevel;
      this.attackCommand = attackCommand;
   }

   public int getSkillCommand() {
      return this.skillCommand;
   }

   public void setSkillCommand(int skillCommand) {
      this.skillCommand = skillCommand;
   }

   public int getSkillCommandLevel() {
      return this.skillCommandLevel;
   }

   public void setSkillCommandLevel(int skillCommandLevel) {
      this.skillCommandLevel = skillCommandLevel;
   }

   public int getAttackCommand() {
      return this.attackCommand;
   }

   public void setAttackCommand(int attackCommand) {
      this.attackCommand = attackCommand;
   }
}
