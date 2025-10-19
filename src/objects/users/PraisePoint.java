package objects.users;

public class PraisePoint {
   private int totalPoint = 0;
   private int point = 0;
   private String name = "";

   public PraisePoint(int totalPoint, int point) {
      this.setTotalPoint(totalPoint);
      this.setPoint(point);
   }

   public int getTotalPoint() {
      return this.totalPoint;
   }

   public void setTotalPoint(int totalPoint) {
      this.totalPoint = totalPoint;
   }

   public int getPoint() {
      return this.point;
   }

   public void setPoint(int point) {
      this.point = point;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
