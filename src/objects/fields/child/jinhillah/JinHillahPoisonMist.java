package objects.fields.child.jinhillah;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.encode.PacketEncoder;

public class JinHillahPoisonMist {
   private int radius;
   private int scale;
   private Point lt;
   private Point rb;
   private int totalAreaMin;
   private int totalAreaMax;
   public Map<Integer, List<JinHillahPoisonMist.AreaInfo>> poisonInfoMap = new HashMap<>();

   public void setRadius(int radius) {
      this.radius = radius;
   }

   public void setScale(int scale) {
      this.scale = scale;
   }

   public Point getLt() {
      return this.lt;
   }

   public Point getRb() {
      return this.rb;
   }

   public int getAreaMax() {
      return this.totalAreaMax;
   }

   public void setLtRb(Point lt, Point rb) {
      this.lt = lt;
      this.rb = rb;
   }

   public void setTotalArea(int min, int max) {
      this.totalAreaMin = min;
      this.totalAreaMax = max;
   }

   public void addAreaInfo(int phase, int scale, int time, String tile) {
      if (!this.poisonInfoMap.containsKey(phase)) {
         this.poisonInfoMap.put(phase, new ArrayList<>());
      }

      this.poisonInfoMap.get(phase).add(new JinHillahPoisonMist.AreaInfo(scale, time, tile));
   }

   public int getDuration(int phase) {
      return this.poisonInfoMap.get(phase).stream().mapToInt(i -> i.time).sum();
   }

   public void encode(PacketEncoder mplew, int phase) {
      mplew.writeInt(0);
      mplew.writeInt(this.radius);
      System.out.println("진힐라1 " + this.radius);
      mplew.writeInt(this.getDuration(phase));
      mplew.writeInt(this.scale);
      System.out.println("진힐라2 " + this.scale);
      mplew.writeInt(this.scale);
      mplew.writeInt(this.poisonInfoMap.get(phase).size());
      System.out.println("진힐라3 " + this.poisonInfoMap.get(phase).size());
      this.poisonInfoMap.get(phase).forEach(poisonInfo -> poisonInfo.encode(mplew));
   }

   public static class AreaInfo {
      private int scale;
      private int time;
      private String tile;

      public AreaInfo(int scale, int time, String tile) {
         this.scale = scale;
         this.time = time;
         this.tile = tile;
      }

      public void encode(PacketEncoder mplew) {
         mplew.writeInt(this.time);
         System.out.println("진힐라4 " + this.time);
         mplew.writeInt(this.scale);
         System.out.println("진힐라5 " + this.scale);
         mplew.writeMapleAsciiString(this.tile);
         System.out.println("진힐라6 " + this.tile);
      }
   }
}
