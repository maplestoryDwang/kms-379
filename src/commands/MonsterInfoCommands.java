package commands;

import java.util.Arrays;
import objects.fields.Field;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleClient;

public class MonsterInfoCommands implements Command {
   @Override
   public void execute(MapleClient c, String[] splitted) throws Exception {
      Field map = c.getPlayer().getMap();
      double range = Double.POSITIVE_INFINITY;
      if (splitted.length > 1) {
         int irange = Integer.parseInt(splitted[1]);
         if (splitted.length <= 2) {
            range = irange * irange;
         } else {
            map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[2]));
         }
      }

      if (splitted[0].equals("킬올")) {
         for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
            MapleMonster mob = (MapleMonster)monstermo;
            map.killMonster(mob, c.getPlayer(), false, false, (byte)1);
         }
      } else if (splitted[0].equals("킬올드롭")) {
         for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
            MapleMonster mob = (MapleMonster)monstermo;
            map.killMonster(mob, c.getPlayer(), true, false, (byte)1);
         }
      } else if (splitted[0].equals("킬올노스폰")) {
         map.killAllMonsters(false);
      } else if (splitted[0].equals("몬스터소환개체")) {
         for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
            MapleMonster mob = (MapleMonster)monstermo;
            c.getPlayer().dropMessage(6, "몬스터 " + mob.toString());
         }
      } else if (splitted[0].equals("딸피")) {
         for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
            MapleMonster mob = (MapleMonster)monstermo;
            mob.setHp(10L);
         }
      }
   }

   @Override
   public CommandDefinition[] getDefinition() {
      return new CommandDefinition[]{
         new CommandDefinition("킬올", "(<범위>) (<맵ID>)", "모든 몬스터를 죽입니다. 맵 ID가 정의될 경우, 범위값은 무시됩니다.", 5),
         new CommandDefinition("킬올노스폰", "(<범위>) (<맵ID>)", "모든 몬스터를 죽이되, 해당 몬스터는 다른몬스터로 부활하지 않습니다.", 5),
         new CommandDefinition("킬올드롭", "(<범위>) (<맵ID>)", "모든 몬스터를 죽이되, 해당 몬스터는 아이템도 드롭하게 됩니다.", 5),
         new CommandDefinition("몬스터소환개체", "", "현재 맵의 모든 몬스터를 출력합니다.", 5),
         new CommandDefinition("딸피", "", "현재 맵의 모든 몬스터의 체력을 10으로 깎습니다.", 5)
      };
   }
}
