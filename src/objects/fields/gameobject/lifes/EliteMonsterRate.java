package objects.fields.gameobject.lifes;

public class EliteMonsterRate {
   private final int grade;
   private final double hpRate;
   private final double expMesoRate;

   public EliteMonsterRate(int grade, double hpRate) {
      this.grade = grade;
      this.hpRate = hpRate;
      this.expMesoRate = hpRate / 2.0;
   }

   public int getGrade() {
      return this.grade;
   }

   public double getHpRate() {
      return this.hpRate;
   }

   public double getExpMesoRate() {
      return this.expMesoRate;
   }
}
