package objects.users.jobs.zero;

public class EgoEquipUpgradeCost {
   private int meso;
   private int wp;

   public EgoEquipUpgradeCost(int meso, int wp) {
      this.setMeso(meso);
      this.setWp(wp);
   }

   public int getMeso() {
      return this.meso;
   }

   public void setMeso(int meso) {
      this.meso = meso;
   }

   public int getWp() {
      return this.wp;
   }

   public void setWp(int wp) {
      this.wp = wp;
   }
}
