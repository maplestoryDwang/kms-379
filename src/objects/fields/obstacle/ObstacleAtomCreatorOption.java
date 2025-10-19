package objects.fields.obstacle;

import java.awt.Point;
import java.util.function.Consumer;
import objects.fields.Field;
import objects.utils.Randomizer;

public class ObstacleAtomCreatorOption {
   public static Consumer<ObstacleAtom> SetCreateDelay(int min, int max) {
      return oa -> oa.setCreateDelay(Randomizer.rand(min, max));
   }

   public static Consumer<ObstacleAtom> SetMaxP(int min, int max) {
      return oa -> oa.setMaxP(Randomizer.rand(min, max));
   }

   public static Consumer<ObstacleAtom> SetVperSec(int min, int max) {
      return oa -> oa.setvPerSec(Randomizer.rand(min, max));
   }

   public static Consumer<ObstacleAtom> SetTrueDamR(int trueDamR) {
      return oa -> oa.setTrueDamR(trueDamR);
   }

   public static Consumer<ObstacleAtom> SetHitBoxRange(int hitBoxRange) {
      return oa -> oa.setHitBoxRange(hitBoxRange);
   }

   public static Consumer<ObstacleAtom> SetCollisionDisease(int skillID, int skillLevel) {
      return oa -> {
         oa.setDiseaseSkillID(skillID);
         oa.setDiseaseSkillLevel(skillLevel);
      };
   }

   public static Consumer<ObstacleAtom> SetDirectionHorizontalToFootHold(int minX, int maxX, int startY, int endY, Field map) {
      return oa -> {
         int x = Randomizer.rand(minX, maxX);
         oa.setStartPos(new Point(x, startY));
         Point endPos = new Point(x, endY);
         endPos = map.calcDropPos(endPos, endPos);
         oa.setEndPos(endPos);
         oa.setLength(endY - startY);
      };
   }

   public static Consumer<ObstacleAtom> Position_Horizontal(int minX, int maxX, int startY, int endY, Field map) {
      return oa -> {
         int x = Randomizer.rand(minX, maxX);
         oa.setStartPos(new Point(x, startY));
         Point endPos = new Point(x, endY);
         oa.setEndPos(map.calcDropPos(endPos, endPos));
         oa.setLength(endY - startY);
      };
   }
}
