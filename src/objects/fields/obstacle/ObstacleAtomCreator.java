package objects.fields.obstacle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ObstacleAtomCreator {
   public int atomType;
   public long beginCreateObstacleAtomTime;
   public long lastCreateObstacleAtomTime;
   public int createInterval;
   public int maxCount;
   public int minCount;
   public ObstacleAtomCreateType createType;
   public int obstacleAtomDuration;
   public List<Consumer<ObstacleAtom>> options;

   public ObstacleAtomCreator() {
      this.lastCreateObstacleAtomTime = System.currentTimeMillis();
      this.options = new ArrayList<>();
   }

   public ObstacleAtomCreator(int min, int max) {
      this.minCount = min;
      this.maxCount = max;
      this.options = new ArrayList<>();
   }
}
