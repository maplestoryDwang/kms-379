package objects.fields.obstacle;

import java.util.Set;

public class ObstacleAtomCreatorEntry {
   public ObstacleAtomCreateType createType;
   public ObstacleInRowInfo oiri = null;
   public ObstacleRadialInfo ori = null;
   public Set<ObstacleAtom> obstacleAtom = null;

   public ObstacleAtomCreatorEntry(ObstacleAtomCreateType createType, ObstacleInRowInfo oiri, ObstacleRadialInfo ori, Set<ObstacleAtom> obstacleAtom) {
      this.createType = createType;
      this.oiri = oiri;
      this.ori = ori;
      this.obstacleAtom = obstacleAtom;
   }
}
