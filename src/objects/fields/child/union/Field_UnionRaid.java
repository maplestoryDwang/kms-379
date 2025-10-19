package objects.fields.child.union;

import java.awt.Point;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public class Field_UnionRaid extends Field {
   public Field_UnionRaid(int mapID, int channel, int returnMapId, float monsterRate) {
      super(mapID, channel, returnMapId, monsterRate);
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
   }

   @Override
   public void onCompleteFieldCommand() {
      super.onCompleteFieldCommand();
   }

   public void spawnUnionWyvern() {
      for (int mobID = 9833106; mobID <= 9833111; mobID++) {
         int count = 0;

         for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
            if (mob.getId() == mobID) {
               count++;
            }
         }

         int a = 0;

         while (count < 2) {
            count++;
            MapleMonster monster = MapleLifeFactory.getMonster(mobID);
            this.spawnMonsterOnGroundBelow(monster, mobID == 9833111 ? new Point(3077, -282) : new Point(1730, -282));
            if (a++ > 100) {
               break;
            }
         }
      }
   }
}
