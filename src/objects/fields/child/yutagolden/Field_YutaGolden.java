package objects.fields.child.yutagolden;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.Spawns;
import objects.users.MapleCharacter;
import objects.utils.Randomizer;

public class Field_YutaGolden extends Field {
   private long lastSpawnTime = 0L;

   public Field_YutaGolden(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(false);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      super.onMobKilled(mob);
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
      super.onMobChangeHP(mob);
   }

   @Override
   public void onPlayerDead(MapleCharacter player) {
      super.onPlayerDead(player);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.lastSpawnTime == 0L) {
         for (MapleCharacter chr : this.getCharacters()) {
            int mobsize = 0;

            for (MapleMonster mob : this.getAllMonster()) {
               if (mob.getFrozenLinkSerialNumber() == chr.getFrozenLinkSerialNumber()) {
                  mobsize++;
               }
            }

            if (chr.getFrozenLinkSerialNumber() == 0L) {
               chr.setFrozenLinkSerialNumber(System.currentTimeMillis() + Randomizer.rand(100000, 500000));
            }

            List<Spawns> randomSpawn = new ArrayList<>(this.monsterSpawn);
            Collections.shuffle(randomSpawn);

            while (mobsize < 19) {
               MapleMonster mobx = MapleLifeFactory.getMonster(9010188);
               mobx.changeLevel(chr.getLevel());
               mobx.setPosition(randomSpawn.get(mobsize).getPosition());
               this.spawnMonsterOnFrozenLink(mobx, mobx.getPosition(), chr.getFrozenLinkSerialNumber());
               mobsize++;
            }
         }

         this.lastSpawnTime = System.currentTimeMillis();
      } else if (this.lastSpawnTime + 5000L <= System.currentTimeMillis()) {
         for (MapleCharacter chr : this.getCharacters()) {
            int mobsize = 0;

            for (MapleMonster mobx : this.getAllMonster()) {
               if (mobx.getFrozenLinkSerialNumber() == chr.getFrozenLinkSerialNumber()) {
                  mobsize++;
               }
            }

            if (chr.getFrozenLinkSerialNumber() == 0L) {
               chr.setFrozenLinkSerialNumber(System.currentTimeMillis() + Randomizer.rand(100000, 500000));
            }

            List<Spawns> randomSpawn = new ArrayList<>(this.monsterSpawn);
            Collections.shuffle(randomSpawn);

            while (mobsize < randomSpawn.size()) {
               MapleMonster mobxx = MapleLifeFactory.getMonster(9010188);
               mobxx.changeLevel(chr.getLevel());
               mobxx.setPosition(randomSpawn.get(Randomizer.rand(0, randomSpawn.size() - 1)).getPosition());
               this.spawnMonsterOnFrozenLink(mobxx, mobxx.getPosition(), chr.getFrozenLinkSerialNumber());
               mobsize++;
            }
         }

         this.lastSpawnTime = System.currentTimeMillis();
      }
   }

   @Override
   public void onCompleteFieldCommand() {
      super.onCompleteFieldCommand();
   }
}
