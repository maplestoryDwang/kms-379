package objects.fields.child.demian;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.encode.PacketEncoder;

public class DemianObjectNodeData {
   public static final Map<Integer, List<DemianObjectNodeData>> datas = new HashMap<>();
   public int collisionType;
   public int duration;
   public int endDelay;
   public boolean hide;
   public int nodeIndex;
   public DemianObjectNodeData.NodeTypes nodeType;
   public int pathIndex;
   public int startDelay;
   public int v;
   public int x;
   public int y;

   public DemianObjectNodeData(
      DemianObjectNodeData.NodeTypes nodeType,
      int pathIndex,
      int nodeIndex,
      int v,
      int startDelay,
      int endDelay,
      int duration,
      boolean hide,
      int collisionType,
      Point pt
   ) {
      this.nodeType = nodeType;
      this.pathIndex = pathIndex;
      this.nodeIndex = nodeIndex;
      this.v = v;
      this.startDelay = startDelay;
      this.endDelay = endDelay;
      this.duration = duration;
      this.hide = hide;
      this.collisionType = collisionType;
      this.x = pt.x;
      this.y = pt.y;
   }

   public void encode(PacketEncoder packet) {
      packet.write(this.nodeType.getTypes());
      packet.writeShort(this.pathIndex);
      packet.writeShort(this.nodeIndex);
      packet.writeShort(this.v);
      packet.writeInt(this.startDelay);
      packet.writeInt(this.endDelay);
      packet.writeInt(this.duration);
      packet.write(this.hide);
      packet.write(this.collisionType);
      packet.writeInt(this.x);
      packet.writeInt(this.y);
   }

   public static DemianObjectNodeData get(int path, int node) {
      List<DemianObjectNodeData> nodes = datas.get(path);
      return nodes != null && !nodes.isEmpty() && node > 0 && node <= nodes.size() ? nodes.get(node) : null;
   }

   static {
      List<DemianObjectNodeData> list = new ArrayList<>();
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 1, 0, 35, 0, 0, 0, false, 0, new Point(100, -100)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 1, 1, 35, 0, 0, 0, false, 0, new Point(340, 100)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 1, 2, 35, 0, 0, 0, false, 0, new Point(890, -200)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 1, 3, 35, 0, 0, 0, false, 0, new Point(1390, 50)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 1, 4, 35, 0, 0, 0, false, 0, new Point(1690, -100)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 1, 5, 35, 0, 0, 0, false, 0, new Point(1540, -300)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 1, 6, 35, 0, 0, 0, false, 0, new Point(1040, -25)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 1, 7, 35, 0, 0, 0, false, 0, new Point(-60, -25)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 1, 8, 35, 0, 0, 0, false, 0, new Point(340, -25)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 1, 9, 35, 0, 0, 0, false, 0, new Point(590, 100)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 1, 10, 35, 0, 0, 0, false, 0, new Point(1090, -200)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.Make, 1, 11, 60, 500, 0, 0, false, 0, new Point(1600, -400)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.Make, 1, 12, 35, 0, 11000, 0, true, 0, new Point(0, 0)));
      datas.put(1, list);
      list = new ArrayList<>();
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 2, 0, 35, 0, 0, 0, false, 0, new Point(895, -300)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 2, 1, 35, 0, 0, 0, false, 0, new Point(1530, -100)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 2, 2, 35, 0, 0, 0, false, 0, new Point(1290, 100)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 2, 3, 35, 0, 0, 0, false, 0, new Point(740, -200)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 2, 4, 35, 0, 0, 0, false, 0, new Point(240, 50)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 2, 5, 35, 0, 0, 0, false, 0, new Point(-60, -100)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 2, 6, 35, 0, 0, 0, false, 0, new Point(90, -300)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 2, 7, 35, 0, 0, 0, false, 0, new Point(590, -25)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 2, 8, 35, 0, 0, 0, false, 0, new Point(1690, -25)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 2, 9, 35, 0, 0, 0, false, 0, new Point(1290, -25)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 2, 10, 35, 0, 0, 0, false, 0, new Point(1040, 100)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 2, 11, 35, 0, 0, 0, false, 0, new Point(540, -200)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.Make, 2, 12, 60, 500, 0, 0, false, 0, new Point(1600, -400)));
      list.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.Make, 2, 13, 35, 0, 11000, 0, true, 0, new Point(1600, -400)));
      datas.put(2, list);
      List<DemianObjectNodeData> var2 = new ArrayList();
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 3, 0, 35, 0, 0, 0, false, 0, new Point(895, -500)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 3, 1, 35, 0, 0, 0, false, 0, new Point(350, 0)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 3, 2, 35, 0, 0, 0, false, 0, new Point(140, -150)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 3, 3, 35, 0, 0, 0, false, 0, new Point(-60, 100)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 3, 4, 35, 0, 0, 0, false, 0, new Point(840, -50)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 3, 5, 35, 0, 0, 0, false, 0, new Point(1140, 100)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 3, 6, 35, 0, 0, 0, false, 0, new Point(1690, -25)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 3, 7, 35, 0, 0, 0, false, 0, new Point(940, -25)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 3, 8, 35, 0, 0, 0, false, 0, new Point(1240, -250)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 3, 9, 35, 0, 0, 0, false, 0, new Point(1590, 100)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 3, 10, 35, 0, 0, 0, false, 0, new Point(1690, -100)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 3, 11, 35, 0, 0, 0, false, 0, new Point(990, 50)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 3, 12, 35, 0, 0, 0, false, 0, new Point(740, -50)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 3, 13, 35, 0, 0, 0, false, 0, new Point(490, 100)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 3, 14, 35, 0, 0, 0, false, 0, new Point(-60, -25)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 3, 15, 35, 0, 0, 0, false, 0, new Point(690, -25)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.Make, 3, 16, 35, 500, 0, 0, false, 0, new Point(690, -25)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 3, 17, 35, 0, 0, 0, false, 0, new Point(895, -300)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.Make, 3, 18, 60, 500, 0, 0, false, 0, new Point(690, -25)));
      var2.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.Make, 3, 19, 35, 0, 11000, 0, true, 0, new Point(0, 0)));
      datas.put(3, var2);
      List<DemianObjectNodeData> var3 = new ArrayList();
      var3.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 4, 0, 35, 0, 0, 0, false, 0, new Point(895, -300)));
      var3.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 4, 1, 35, 0, 0, 0, false, 0, new Point(410, 100)));
      var3.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 4, 2, 35, 0, 0, 0, false, 0, new Point(40, -200)));
      var3.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 4, 3, 35, 0, 0, 0, false, 0, new Point(-60, 100)));
      var3.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 4, 4, 35, 0, 0, 0, false, 0, new Point(360, -150)));
      var3.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 4, 5, 35, 0, 0, 0, false, 0, new Point(810, 50)));
      var3.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 4, 6, 35, 0, 0, 0, false, 0, new Point(1440, -200)));
      var3.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 4, 7, 35, 0, 0, 0, false, 0, new Point(1690, -50)));
      var3.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 4, 8, 35, 0, 0, 0, false, 0, new Point(1540, 100)));
      var3.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 4, 9, 35, 0, 0, 0, false, 0, new Point(840, -150)));
      var3.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.Make, 4, 10, 35, 500, 0, 0, false, 0, new Point(840, -150)));
      var3.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.Make, 4, 11, 35, 500, 0, 0, false, 0, new Point(840, -150)));
      var3.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.Make, 4, 12, 35, 500, 0, 0, false, 0, new Point(840, -150)));
      var3.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 4, 13, 35, 0, 0, 0, false, 0, new Point(895, -300)));
      var3.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.Make, 4, 14, 60, 500, 0, 0, false, 0, new Point(840, -150)));
      var3.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.Make, 4, 15, 35, 0, 11000, 0, true, 0, new Point(0, 0)));
      datas.put(4, var3);
      List<DemianObjectNodeData> var4 = new ArrayList();
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 5, 0, 35, 0, 0, 0, false, 0, new Point(100, 100)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 5, 1, 35, 0, 0, 0, false, 0, new Point(490, -400)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 5, 2, 35, 0, 0, 0, false, 0, new Point(840, 75)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 5, 3, 35, 0, 0, 0, false, 0, new Point(1190, -450)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 5, 4, 35, 0, 0, 0, false, 0, new Point(1500, 50)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 5, 5, 35, 0, 0, 0, false, 0, new Point(1700, -150)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 5, 6, 35, 0, 0, 0, false, 0, new Point(1040, 100)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 5, 7, 35, 0, 0, 0, false, 0, new Point(440, -150)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 5, 8, 35, 0, 0, 0, false, 0, new Point(-70, 50)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 5, 9, 35, 0, 0, 0, false, 0, new Point(140, -300)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 5, 10, 35, 0, 0, 0, false, 0, new Point(1240, 100)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 5, 11, 35, 0, 0, 0, false, 0, new Point(1640, -350)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 5, 12, 35, 0, 0, 0, false, 0, new Point(690, 75)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 5, 13, 35, 0, 0, 0, false, 0, new Point(-60, -25)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 5, 14, 35, 0, 0, 0, false, 0, new Point(895, -25)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 5, 15, 35, 0, 0, 0, false, 0, new Point(1690, -25)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 5, 16, 35, 0, 0, 0, false, 0, new Point(1540, -400)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.Make, 5, 17, 60, 500, 0, 0, false, 0, new Point(800, -200)));
      var4.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.Make, 5, 18, 35, 0, 11000, 0, true, 0, new Point(0, 0)));
      datas.put(5, var4);
      List<DemianObjectNodeData> var5 = new ArrayList();
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 0, 35, 0, 0, 0, false, 0, new Point(895, -300)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 1, 35, 0, 0, 0, false, 0, new Point(1290, 50)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 2, 35, 0, 0, 0, false, 0, new Point(1690, -150)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 3, 35, 0, 0, 0, false, 0, new Point(1640, 50)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 4, 35, 0, 0, 0, false, 0, new Point(1140, -150)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 5, 35, 0, 0, 0, false, 0, new Point(490, 50)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 6, 35, 0, 0, 0, false, 0, new Point(190, -100)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 7, 35, 0, 0, 0, false, 0, new Point(-60, -25)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 8, 35, 0, 0, 0, false, 0, new Point(740, -25)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 9, 35, 0, 0, 0, false, 0, new Point(890, 100)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 10, 35, 0, 0, 0, false, 0, new Point(1490, -200)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 11, 35, 0, 0, 0, false, 0, new Point(1690, -25)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 12, 35, 0, 0, 0, false, 0, new Point(890, -25)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 13, 35, 0, 0, 0, false, 0, new Point(540, -300)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 14, 35, 0, 0, 0, false, 0, new Point(90, 100)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 15, 35, 0, 0, 0, false, 0, new Point(-60, -100)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 16, 35, 0, 0, 0, false, 0, new Point(890, 50)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.Make, 6, 17, 35, 500, 0, 0, false, 0, new Point(890, 50)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.FieldPosition, 6, 18, 35, 0, 0, 0, false, 0, new Point(895, -300)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.Make, 6, 19, 60, 500, 0, 0, false, 0, new Point(890, 50)));
      var5.add(new DemianObjectNodeData(DemianObjectNodeData.NodeTypes.Make, 6, 20, 35, 0, 11000, 0, true, 0, new Point(0, 0)));
      datas.put(6, var5);
   }

   public static enum CollisionTypes {
      All(0),
      Local(1),
      Not(2);

      int types;

      private CollisionTypes(int types) {
         this.types = types;
      }

      public int getType() {
         return this.types;
      }
   }

   public static enum NodeTypes {
      Unknown(0),
      FieldPosition(1),
      Make(2),
      RelPosition(3);

      int types;

      private NodeTypes(int types) {
         this.types = types;
      }

      public int getTypes() {
         return this.types;
      }
   }
}
