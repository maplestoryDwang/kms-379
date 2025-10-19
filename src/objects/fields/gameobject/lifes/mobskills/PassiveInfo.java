package objects.fields.gameobject.lifes.mobskills;

import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class PassiveInfo {
   private int level;
   private int skill;

   public PassiveInfo(MapleData root) {
      this.setLevel(MapleDataTool.getInt("level", root, 0));
      this.setSkill(MapleDataTool.getInt("skill", root, 0));
   }

   public int getLevel() {
      return this.level;
   }

   public void setLevel(int level) {
      this.level = level;
   }

   public int getSkill() {
      return this.skill;
   }

   public void setSkill(int skill) {
      this.skill = skill;
   }
}
