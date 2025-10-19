package objects.fields.gameobject.lifes;

import java.awt.Point;
import objects.fields.Field;

public abstract class Spawns {
   public abstract MapleMonster getMonster();

   public abstract byte getCarnivalTeam();

   public abstract boolean shouldSpawn(long var1, int var3);

   public abstract int getCarnivalId();

   public abstract MapleMonster spawnMonster(Field var1);

   public abstract int getMobTime();

   public abstract Point getPosition();

   public abstract int getF();

   public abstract int getFh();

   public abstract void setNextPossibleSpawn(long var1);
}
