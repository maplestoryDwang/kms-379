package objects.fields.gameobject.lifes.mobskills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class FieldCommand {
   public int idleTime = 0;
   public int repeat = 0;
   public List<FieldCommand.Sequence> seq = new ArrayList<>();

   public FieldCommand(MapleData root) {
      this.idleTime = MapleDataTool.getInt("idleTime", root, 0);
      this.repeat = MapleDataTool.getInt("repeat", root, 0);
      MapleData sq = root.getChildByPath("seq");

      for (MapleData data : sq.getChildren()) {
         this.seq.add(new FieldCommand.Sequence(data));
      }
   }

   public class Sequence {
      public int delay = 0;
      public int mobCount = 0;
      public int pickCount = 0;
      public Map<Integer, Integer> targets = new HashMap<>();

      public Sequence(MapleData root) {
         this.delay = MapleDataTool.getInt("delay", root, 0);
         this.mobCount = MapleDataTool.getInt("mobCount", root, 0);
         this.pickCount = MapleDataTool.getInt("pickCount", root, 0);
         MapleData tg = root.getChildByPath("target");

         for (MapleData data : tg.getChildren()) {
            this.targets.put(Integer.parseInt(data.getName()), MapleDataTool.getInt(data, 0));
         }
      }
   }
}
