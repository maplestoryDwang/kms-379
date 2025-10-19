package objects.summoned;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatEffect;

public class EmeraldDust extends SummonedAura {
   @Override
   public void affectToMob(int skillId, SecondaryStatEffect effect, MapleMonster mob) {
      if (!mob.hasSkillBySkillID(effect.getSourceId())) {
         Map<MobTemporaryStatFlag, MobTemporaryStatEffect> mse = new HashMap<>();
         mse.put(MobTemporaryStatFlag.PDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.PDR, effect.getW(), effect.getSourceId(), null, false));
         mse.put(MobTemporaryStatFlag.SPEED, new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, effect.getZ(), effect.getSourceId(), null, false));
         mob.applyMonsterBuff(mse, effect.getSourceId(), effect.getDuration(), null, Collections.EMPTY_LIST);
      }
   }

   @Override
   public void affectToUser(int skillId, SecondaryStatEffect effect, MapleCharacter player) {
   }
}
