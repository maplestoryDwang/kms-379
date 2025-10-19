package network.center.praise;

public class PraiseDonationMesoLog {
   private String name = "";
   private int accountID = 0;
   private long meso = 0L;
   private long time = 0L;

   public PraiseDonationMesoLog(String name, int accountID, long meso, long time) {
      this.setName(name);
      this.setAccountID(accountID);
      this.setMeso(meso);
      this.setTime(time);
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getAccountID() {
      return this.accountID;
   }

   public void setAccountID(int accountID) {
      this.accountID = accountID;
   }

   public long getMeso() {
      return this.meso;
   }

   public void setMeso(long meso) {
      this.meso = meso;
   }

   public long getTime() {
      return this.time;
   }

   public void setTime(long time) {
      this.time = time;
   }
}
