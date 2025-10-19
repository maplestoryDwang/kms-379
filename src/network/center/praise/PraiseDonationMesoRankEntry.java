package network.center.praise;

public class PraiseDonationMesoRankEntry {
   private String playerName;
   private int accountID;
   private long totalMeso;

   public PraiseDonationMesoRankEntry(String playerName, int accountID, long totalMeso) {
      this.setPlayerName(playerName);
      this.setAccountID(accountID);
      this.setTotalMeso(totalMeso);
   }

   public String getPlayerName() {
      return this.playerName;
   }

   public void setPlayerName(String playerName) {
      this.playerName = playerName;
   }

   public int getAccountID() {
      return this.accountID;
   }

   public void setAccountID(int accountID) {
      this.accountID = accountID;
   }

   public long getTotalMeso() {
      return this.totalMeso;
   }

   public void setTotalMeso(long totalMeso) {
      this.totalMeso = totalMeso;
   }
}
