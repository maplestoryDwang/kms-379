package objects.summoned;

import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatEffect;

public abstract class SummonedAura {
   public int z = 0;

   public abstract void affectToMob(int var1, SecondaryStatEffect var2, MapleMonster var3);

   public abstract void affectToUser(int var1, SecondaryStatEffect var2, MapleCharacter var3);
}
