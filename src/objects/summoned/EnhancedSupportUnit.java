package objects.summoned;

import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;

public class EnhancedSupportUnit extends SummonedAura {
   @Override
   public void affectToMob(int skillId, SecondaryStatEffect effect, MapleMonster mob) {
   }

   @Override
   public void affectToUser(int skillId, SecondaryStatEffect effect, MapleCharacter player) {
      player.temporaryStatSet(skillId, 5000, SecondaryStatFlag.indiePMDR, effect.getZ() + this.z);
   }
}
