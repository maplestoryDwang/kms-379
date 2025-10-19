package objects.utils;

import java.awt.Point;
import java.util.List;

public class AttackPair {
   public int objectid;
   private boolean objIsBoss = false;
   public int refImgId;
   public Point point;
   public List<Pair<Long, Boolean>> attack;
   public int mobDelay;

   public AttackPair(int objectid, List<Pair<Long, Boolean>> attack) {
      this.objectid = objectid;
      this.attack = attack;
   }

   public AttackPair(int objectid, Point point, List<Pair<Long, Boolean>> attack) {
      this.objectid = objectid;
      this.point = point;
      this.attack = attack;
   }

   public void setMobDelay(int delay) {
      this.mobDelay = delay;
   }

   public void setRefImgId(int refImgId) {
      this.refImgId = refImgId;
   }

   public void setIsBoss(boolean boss) {
      this.objIsBoss = boss;
   }

   public boolean isBoss() {
      return this.objIsBoss;
   }
}
