package objects.fields.child.union;

import java.awt.Point;
import java.util.List;
import java.util.Map;

public class UnionCharacterSizeInfo {
   private Map<Integer, List<Point>> points;

   public UnionCharacterSizeInfo(Map<Integer, List<Point>> points) {
      this.points = points;
   }

   public Map<Integer, List<Point>> getPoints() {
      return this.points;
   }

   public void setPoints(Map<Integer, List<Point>> points) {
      this.points = points;
   }
}
