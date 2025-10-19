package objects.fields.gameobject.lifes;

import java.awt.Point;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.summoned.Summoned;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;

public class SpawnPoint extends Spawns {
   private MapleMonster monster;
   private Point pos;
   private long nextPossibleSpawn;
   private int mobTime;
   private int carnival = -1;
   private int fh;
   private int f;
   private int id;
   private int level = -1;
   private AtomicInteger spawnedMonsters = new AtomicInteger(0);
   private String msg;
   private byte carnivalTeam;
   private boolean canRespawn = true;

   public SpawnPoint(MapleMonster monster, Point pos, int mobTime, byte carnivalTeam, String msg) {
      this.monster = monster;
      this.pos = pos;
      this.id = monster.getId();
      this.fh = monster.getFh();
      this.f = monster.getF();
      this.mobTime = mobTime < 0 ? -1 : mobTime * 1000;
      this.carnivalTeam = carnivalTeam;
      this.msg = msg;
      this.nextPossibleSpawn = -1L;
   }

   public final void setCarnival(int c) {
      this.carnival = c;
   }

   public final void setLevel(int c) {
      this.level = c;
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
   public final Point getPosition() {
      return this.pos;
   }

   @Override
   public final MapleMonster getMonster() {
      return this.monster;
   }

   @Override
   public final byte getCarnivalTeam() {
      return this.carnivalTeam;
   }

   @Override
   public final int getCarnivalId() {
      return this.carnival;
   }

   @Override
   public final boolean shouldSpawn(long time, int mobRate) {
      return this.mobTime < 0 ? false : this.canRespawn;
   }

   @Override
   public final MapleMonster spawnMonster(final Field map) {
      MapleMonster mob = new MapleMonster(this.monster.getId(), this.monster.getStats());
      if (this.monster.getChangedStats() != null) {
         mob.setOverrideStats(this.monster.getChangedStats());
      }

      mob.setPosition(this.pos);
      mob.setCy(this.pos.y);
      mob.setRx0(this.pos.x - 50);
      mob.setRx1(this.pos.x + 50);
      mob.setFh(this.fh);
      mob.setF(this.f);
      mob.setCarnivalTeam(this.carnivalTeam);
      if (this.level > -1) {
         mob.changeLevel(this.level);
      }

      this.spawnedMonsters.incrementAndGet();
      this.canRespawn = false;
      mob.addListener(new MonsterListener() {
         @Override
         public void monsterKilled() {
            SpawnPoint.this.nextPossibleSpawn = System.currentTimeMillis();
            if (SpawnPoint.this.mobTime <= 0) {
               SpawnPoint.this.mobTime = 5000;
            }

            SpawnPoint.this.nextPossibleSpawn = SpawnPoint.this.nextPossibleSpawn + SpawnPoint.this.mobTime;
            SpawnPoint.this.canRespawn = true;
            if (System.currentTimeMillis() - map.getLastRespawnTime() >= 5000L) {
               map.setLastRespawnTime(System.currentTimeMillis());
            }

            SpawnPoint.this.spawnedMonsters.decrementAndGet();
         }
      });
      map.spawnMonster(mob, -2);

      for (Summoned s : map.getAllSummonsThreadsafe()) {
         if (s.getSkill() == 35111005) {
            SecondaryStatEffect effect = SkillFactory.getSkill(s.getSkill()).getEffect(s.getSkillLevel());

            for (Entry<MobTemporaryStatFlag, Integer> stat : effect.getMonsterStati().entrySet()) {
               mob.applyStatus(
                  s.getOwner(),
                  new MobTemporaryStatEffect(stat.getKey(), stat.getValue(), s.getSkill(), null, false),
                  false,
                  effect.getDuration(),
                  true,
                  effect
               );
            }
            break;
         }
      }

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
