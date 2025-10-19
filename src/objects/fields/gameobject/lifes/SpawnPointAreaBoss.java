package objects.fields.gameobject.lifes;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicBoolean;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.utils.Randomizer;

public class SpawnPointAreaBoss extends Spawns {
   private MapleMonster monster;
   private Point pos1;
   private Point pos2;
   private Point pos3;
   private long nextPossibleSpawn;
   private int mobTime;
   private int fh;
   private int f;
   private int id;
   private AtomicBoolean spawned = new AtomicBoolean(false);
   private String msg;

   public SpawnPointAreaBoss(MapleMonster monster, Point pos1, Point pos2, Point pos3, int mobTime, String msg, boolean shouldSpawn) {
      this.monster = monster;
      this.id = monster.getId();
      this.fh = monster.getFh();
      this.f = monster.getF();
      this.pos1 = pos1;
      this.pos2 = pos2;
      this.pos3 = pos3;
      this.mobTime = mobTime < 0 ? -1 : mobTime * 1000;
      this.msg = msg;
      this.nextPossibleSpawn = System.currentTimeMillis() + 3500L + (shouldSpawn ? 0 : this.mobTime);
   }

   @Override
   public final int getF() {
      return this.f;
   }

   @Override
   public final int getFh() {
      return this.fh;
   }

   @Override
   public final MapleMonster getMonster() {
      return this.monster;
   }

   @Override
   public final byte getCarnivalTeam() {
      return -1;
   }

   @Override
   public final int getCarnivalId() {
      return -1;
   }

   @Override
   public final boolean shouldSpawn(long time, int mobRate) {
      return this.mobTime >= 0 && !this.spawned.get() ? this.nextPossibleSpawn <= time : false;
   }

   @Override
   public final Point getPosition() {
      int rand = Randomizer.nextInt(3);
      return rand == 0 ? this.pos1 : (rand == 1 ? this.pos2 : this.pos3);
   }

   @Override
   public final MapleMonster spawnMonster(Field map) {
      Point pos = this.getPosition();
      MapleMonster mob = new MapleMonster(this.monster.getId(), this.monster.getStats());
      if (this.monster.getChangedStats() != null) {
         mob.setOverrideStats(this.monster.getChangedStats());
      }

      mob.setPosition(pos);
      mob.setCy(pos.y);
      mob.setRx0(pos.x - 50);
      mob.setRx1(pos.x + 50);
      mob.setFh(this.fh);
      mob.setF(this.f);
      this.spawned.set(true);
      mob.addListener(new MonsterListener() {
         @Override
         public void monsterKilled() {
            SpawnPointAreaBoss.this.nextPossibleSpawn = System.currentTimeMillis() + 3500L;
            if (SpawnPointAreaBoss.this.mobTime > 0) {
               SpawnPointAreaBoss.this.nextPossibleSpawn = SpawnPointAreaBoss.this.nextPossibleSpawn + SpawnPointAreaBoss.this.mobTime;
            }

            SpawnPointAreaBoss.this.spawned.set(false);
         }
      });
      map.spawnMonster(mob, -2);
      if (this.msg != null) {
         map.broadcastMessage(CWvsContext.serverNotice(6, this.msg));
      }

      return mob;
   }

   @Override
   public final int getMobTime() {
      return this.mobTime;
   }

   @Override
   public final void setNextPossibleSpawn(long time) {
      this.nextPossibleSpawn = time;
   }
}
