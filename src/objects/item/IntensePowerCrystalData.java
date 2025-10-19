package objects.item;

public class IntensePowerCrystalData {
   private int realMonster;
   private int dropMonster;
   private int namingMonster;
   private long meso;

   public IntensePowerCrystalData(int realMonster, int dropMonster, int namingMonster, long meso) {
      this.setRealMonster(realMonster);
      this.setDropMonster(dropMonster);
      this.setNamingMonster(namingMonster);
      this.setMeso(meso);
   }

   public int getRealMonster() {
      return this.realMonster;
   }

   public void setRealMonster(int realMonster) {
      this.realMonster = realMonster;
   }

   public int getDropMonster() {
      return this.dropMonster;
   }

   public void setDropMonster(int dropMonster) {
      this.dropMonster = dropMonster;
   }

   public int getNamingMonster() {
      return this.namingMonster;
   }

   public void setNamingMonster(int namingMonster) {
      this.namingMonster = namingMonster;
   }

   public long getMeso() {
      return this.meso;
   }

   public void setMeso(long meso) {
      this.meso = meso;
   }
}
