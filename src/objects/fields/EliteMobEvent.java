package objects.fields;

import java.awt.Point;
import network.models.CField;
import objects.fields.gameobject.lifes.ChangeableStats;
import objects.fields.gameobject.lifes.EliteGrade;
import objects.fields.gameobject.lifes.EliteMonsterMan;
import objects.fields.gameobject.lifes.EliteMonsterRate;
import objects.fields.gameobject.lifes.EliteType;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.Spawns;
import objects.item.Item;
import objects.users.MapleCharacter;
import objects.utils.Randomizer;

public class EliteMobEvent extends FieldEvent {
   private int summonCount = 0;
   private Point pos;

   public EliteMobEvent(Field map, long expireTime, Point pos, int summonCount) {
      super(map, expireTime);
      this.pos = pos;
      this.summonCount = summonCount;
   }

   @Override
   public void onStart() {
      this.map.setRemainEliteMobSpawn(20);
      boolean spawnRed = false;

      for (Spawns spawns : this.map.getClosestSpawns(this.pos, this.summonCount)) {
         int mobTemplateID = spawns.getMonster().getId();
         EliteGrade eliteGrade = null;
         if (this.summonCount == 1 && this.map.getEliteLevel() > 0 && this.map.getEliteLevel() % 10 == 0) {
            mobTemplateID = 8644631;
            eliteGrade = EliteGrade.Red;
            spawnRed = true;
         }

         MapleMonster mob = MapleLifeFactory.getMonster(mobTemplateID);
         MapleMonster origin = MapleLifeFactory.getMonster(spawns.getMonster().getId());
         mob.setElite(eliteGrade);
         if (spawnRed) {
            int level = origin.getStats().getLevel();
            EliteMonsterRate emr = EliteMonsterMan.getRandomRate(level);
            ChangeableStats cs = new ChangeableStats(origin.getStats());
            cs.hp = (long)(origin.getStats().getMaxHp() * emr.getHpRate());
            cs.level = level;
            mob.getStats().setLevel((short)level);
            mob.setOverrideStats(cs);
         }

         this.map.spawnMonsterOnGroundBelow(mob, spawns.getPosition());
      }

      if (spawnRed) {
         this.map.startMapEffect("어둠을 흩뿌리며 어둠의 전령이 나타납니다.", 5120124, false, 5);
      } else {
         this.map.startMapEffect("어두운 기운과 함께 강력한 몬스터가 출현합니다.", 5120124, false, 5);
      }

      this.map.broadcastMessage(CField.getSpecialMapSound("Field.img/eliteMonster/Regen"));
      this.map.setEliteState(EliteState.EliteMob);
   }

   public static void onEliteMobDead(Field map) {
      map.setEliteState(EliteState.Normal);
      map.setEliteLevel(map.getEliteLevel() + 1);
      if (map.getEliteLevel() < 14) {
         map.startMapEffect("어두운 기운이 사라지지 않아 이곳을 음산하게 만들고 있습니다.", 5120124, false, 5);
      } else {
         map.startMapEffect("이곳이 어두운 기운으로 가득 차 곧 무슨 일이 일어날 듯 합니다.", 5120124, false, 5);
      }

      map.setFieldEvent(null);
   }

   @Override
   public void onEnd() {
      for (MapleMonster mob : this.map.getAllMonstersThreadsafe()) {
         if (mob.getEliteMobType().getType() > EliteType.None.getType()) {
            this.map.removeMonster(mob, 1);
         }
      }
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
   }

   @Override
   public void onMobLeave(MapleMonster mob) {
      if (mob.getEliteMobGrade().getType() > EliteGrade.None.getType()) {
         this.summonCount--;
         if (this.summonCount == 0) {
            onEliteMobDead(this.map);
         }

         int[] itemID = new int[]{2049501, 2048300, 2049400, 2048308, 2049100, 2470003, 2436605, 2049153, 2049004, 2430441, 2048716, 2048717, 5680148, 5520001};
         int[] prop = new int[]{2000, 2000, 2000, 2000, 2000, 2000, 800, 30, 40, 20, 20, 50, 10, 2};
         byte d = 1;
         Point pos = new Point(0, mob.getTruePosition().y);

         for (int i = 0; i < itemID.length; i++) {
            if (Randomizer.nextInt(10000) < prop[i]) {
               int item = itemID[i];
               Item drop = new Item(item, (short)0, (short)1, 0);
               pos.x = mob.getTruePosition().x + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
               this.map.spawnMobDrop(drop, this.map.calcDropPos(pos, mob.getTruePosition()), mob, mob.getController(), (byte)0, 0);
               d++;
            }
         }
      }
   }

   @Override
   public void onUpdatePerSecond(long now) {
      super.onUpdatePerSecond(now);
      if (!this.expired) {
         ;
      }
   }

   @Override
   public void onUserEnter(MapleCharacter player) {
   }

   @Override
   public void onUserLeave(MapleCharacter player) {
   }
}
