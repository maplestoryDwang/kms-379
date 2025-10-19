package commands;

import java.awt.Point;
import java.util.List;
import network.models.CField;
import objects.fields.gameobject.RuneStone;
import objects.fields.gameobject.RuneStoneType;
import objects.users.MapleClient;

public class SpawnObjectCommands implements Command {
   @Override
   public void execute(MapleClient c, String[] splitted) throws Exception, IllegalCommandSyntaxException {
      if (splitted[0].equals("룬")) {
         Point pos = c.getPlayer().getPosition();
         int type = Integer.parseInt(splitted[1]);
         if (type >= 0 && type <= 9) {
            RuneStone rune = new RuneStone(RuneStoneType.get(Integer.parseInt(splitted[1])), 0, pos.x - 50, pos.y, c.getPlayer().getMap());
            List<RuneStone> runes = c.getPlayer().getMap().getAllRune();

            for (int i = 0; i < runes.size(); i++) {
               c.getPlayer().getMap().removeMapObject(runes.get(i));
            }

            c.getPlayer().getMap().addMapObject(rune);
            c.getPlayer().getMap().broadcastMessage(CField.spawnRune(rune, false));
            c.getPlayer().getMap().broadcastMessage(CField.spawnRune(rune, true));
         } else {
            c.getPlayer().dropMessage(5, "0~9까지만 입력 가능합니다.");
         }
      } else if (splitted[0].equals("오브젝트")) {
         c.getPlayer().dropMessage(5, "현재 맵에 있는 오브젝트 수는 " + c.getPlayer().getMap().getMapObjectSize() + "개 입니다.");
      }
   }

   @Override
   public CommandDefinition[] getDefinition() {
      return new CommandDefinition[]{new CommandDefinition("룬", "<룬타입>", "현재 위치에 해당 룬을 생성합니다.", 6), new CommandDefinition("오브젝트", "", "오브젝트 수를 가져옵니다.", 6)};
   }
}
