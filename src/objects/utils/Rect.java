package objects.utils;

import java.awt.Point;
import java.util.List;
import network.encode.PacketEncoder;

public class Rect {
   private int left;
   private int top;
   private int right;
   private int bottom;

   public Rect(int left, int top, int right, int bottom) {
      this.left = left;
      this.top = top;
      this.right = right;
      this.bottom = bottom;
   }

   public Rect(Point point, Point lt, Point rb, boolean isLeft) {
      int x1 = lt.x * (isLeft ? 1 : -1) + point.x;
      int x2 = rb.x * (isLeft ? 1 : -1) + point.x;
      this.left = Math.min(x1, x2);
      this.top = lt.y + point.y;
      this.right = Math.max(x1, x2);
      this.bottom = rb.y + point.y;
   }

   public static Rect Intersect(Rect a, Rect b) {
      int x1 = Math.max(a.getLeft(), b.getLeft());
      int x2 = Math.min(a.getRight(), b.getRight());
      int y1 = Math.max(a.getTop(), b.getTop());
      int y2 = Math.min(a.getBottom(), b.getBottom());
      return x2 >= x1 && y2 >= y1 ? new Rect(x1, y1, x2, y2) : null;
   }

   public static Rect GetOrDefault(Point lt, Point rb, Rect def) {
      Point point = new Point();
      if (lt == point) {
         point = new Point();
         if (rb == point) {
            return def;
         }
      }

      return new Rect(lt, rb);
   }

   public Point getLt() {
      return new Point(this.getLeft(), this.getTop());
   }

   public Point getRb() {
      return new Point(this.getRight(), this.getBottom());
   }

   public Rect(Point lt, Point rb) {
      this.left = lt.x;
      this.top = lt.y;
      this.right = rb.x;
      this.bottom = rb.y;
   }

   public int getLeft() {
      return this.left;
   }

   public void setLeft(int left) {
      this.left = left;
   }

   public int getTop() {
      return this.top;
   }

   public void setTop(int top) {
      this.top = top;
   }

   public int getRight() {
      return this.right;
   }

   public void setRight(int right) {
      this.right = right;
   }

   public int getBottom() {
      return this.bottom;
   }

   public void setBottom(int bottom) {
      this.bottom = bottom;
   }

   public int getWidth() {
      return Math.abs(this.getLeft() - this.getRight());
   }

   public int getHeight() {
      return Math.abs(this.getTop() - this.getBottom());
   }

   public int getX1() {
      return Math.min(this.getLeft(), this.getRight());
   }

   public int getX2() {
      return Math.max(this.getLeft(), this.getRight());
   }

   public int getY1() {
      return Math.min(this.getTop(), this.getBottom());
   }

   public int getY2() {
      return Math.max(this.getTop(), this.getBottom());
   }

   public Point containsPoint(List<Point> pointList) {
      for (Point point : pointList) {
         if (this.getLeft() <= point.getX() && point.getX() <= this.getRight() && this.getTop() <= point.getY() && point.getY() <= this.getBottom()) {
            return point;
         }
      }

      return null;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getLeft());
      packet.writeInt(this.getTop());
      packet.writeInt(this.getRight());
      packet.writeInt(this.getBottom());
   }
}
