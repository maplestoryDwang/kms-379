package objects.fields.gameobject.lifes;

public class EliteMonsterStats {
   public int hpRate;
   public int expMesoRate;

   public EliteMonsterStats(EliteGrade grade, int level) {
      if (level < 100) {
         if (grade == EliteGrade.Yellow) {
            this.hpRate = 22;
            this.expMesoRate = 11;
         } else if (grade == EliteGrade.Orange) {
            this.hpRate = 30;
            this.expMesoRate = 15;
         } else if (grade == EliteGrade.Red) {
            this.hpRate = 39;
            this.expMesoRate = 19;
         }
      } else if (level < 200) {
         if (grade == EliteGrade.Yellow) {
            this.hpRate = 30;
            this.expMesoRate = 15;
         } else if (grade == EliteGrade.Orange) {
            this.hpRate = 46;
            this.expMesoRate = 23;
         } else if (grade == EliteGrade.Red) {
            this.hpRate = 60;
            this.expMesoRate = 30;
         }
      } else if (level >= 200) {
         if (grade == EliteGrade.Yellow) {
            this.hpRate = 50;
            this.expMesoRate = 19;
         } else if (grade == EliteGrade.Orange) {
            this.hpRate = 75;
            this.expMesoRate = 27;
         } else if (grade == EliteGrade.Red) {
            this.hpRate = 100;
            this.expMesoRate = 40;
         }
      }
   }
}
