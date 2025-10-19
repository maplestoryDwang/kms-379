package objects.fields;

import java.util.ArrayList;
import java.util.List;
import objects.wz.provider.MapleData;

public class StigmaInfo {
   public List<StigmaStackLevel> stackLevel = new ArrayList<>();
   public StigmaStackMax stackMax;
   public List<StigmaStackType> stackType = new ArrayList<>();

   public StigmaInfo(MapleData data) {
      MapleData stackLevel = data.getChildByPath("stackLevel");
      if (stackLevel != null) {
         for (MapleData sl : stackLevel) {
            this.stackLevel.add(new StigmaStackLevel(sl));
         }
      }

      MapleData stackType = data.getChildByPath("stackType");
      if (stackType != null) {
         for (MapleData st : stackType) {
            this.stackType.add(new StigmaStackType(st));
         }
      }

      this.stackMax = new StigmaStackMax(data.getChildByPath("stackMax"));
   }
}
