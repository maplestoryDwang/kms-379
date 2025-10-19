package objects.fields.child.fritto;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class PocketDrop {
   private int dropDelay = 0;
   private int dropInterval = 0;
   private Point lt = null;
   private Point rb = null;
   private Point start = null;
   private int minDropRange = 0;
   private List<PocketDropItem> dropItems = new ArrayList<>();
   private int maxScore = 0;
   private int playTime = 0;

   public PocketDrop(MapleData data) {
      MapleData drop = data.getChildByPath("drop");
      if (drop != null) {
         this.dropDelay = MapleDataTool.getInt("dropDelay", drop, 0);
         this.dropInterval = MapleDataTool.getInt("dropInterval", drop, 0);
         this.lt = MapleDataTool.getPoint("lt", drop, new Point(0, 0));
         this.rb = MapleDataTool.getPoint("rb", drop, new Point(0, 0));
         this.setStart(MapleDataTool.getPoint("start", drop, new Point(0, 0)));

         for (MapleData root : drop.getChildByPath("dropitem")) {
            this.dropItems.add(new PocketDropItem(root));
         }
      }

      MapleData game = data.getChildByPath("game");
      if (game != null) {
         this.setMaxScore(MapleDataTool.getInt("maxScore", game, 0));
         this.setPlayTime(MapleDataTool.getInt("playTime", game, 0));
      }
   }

   public int getDropDelay() {
      return this.dropDelay;
   }

   public void setDropDelay(int dropDelay) {
      this.dropDelay = dropDelay;
   }

   public int getDropInterval() {
      return this.dropInterval;
   }

   public void setDropInterval(int dropInterval) {
      this.dropInterval = dropInterval;
   }

   public int getMinDropRange() {
      return this.minDropRange;
   }

   public void setMinDropRange(int minDropRange) {
      this.minDropRange = minDropRange;
   }

   public List<PocketDropItem> getDropItems() {
      return this.dropItems;
   }

   public void setDropItems(List<PocketDropItem> dropItems) {
      this.dropItems = dropItems;
   }

   public Point getLt() {
      return this.lt;
   }

   public void setLt(Point lt) {
      this.lt = lt;
   }

   public Point getRb() {
      return this.rb;
   }

   public void setRb(Point rb) {
      this.rb = rb;
   }

   public int getMaxScore() {
      return this.maxScore;
   }

   public void setMaxScore(int maxScore) {
      this.maxScore = maxScore;
   }

   public int getPlayTime() {
      return this.playTime;
   }

   public void setPlayTime(int playTime) {
      this.playTime = playTime;
   }

   public Point getStart() {
      return this.start;
   }

   public void setStart(Point start) {
      this.start = start;
   }
}
