package security.anticheat;

public class CheatingOffenseEntry {
   private CheatingOffense offense;
   private int count = 0;
   private int characterid;
   private long lastOffense;
   private String param;
   private int dbid = -1;

   public CheatingOffenseEntry(CheatingOffense offense, int characterid) {
      this.offense = offense;
      this.characterid = characterid;
   }

   public CheatingOffense getOffense() {
      return this.offense;
   }

   public int getCount() {
      return this.count;
   }

   public int getChrfor() {
      return this.characterid;
   }

   public void incrementCount() {
      this.count++;
      this.lastOffense = System.currentTimeMillis();
   }

   public boolean isExpired() {
      return this.lastOffense < System.currentTimeMillis() - this.offense.getValidityDuration();
   }

   public int getPoints() {
      return this.count * this.offense.getPoints();
   }

   public String getParam() {
      return this.param;
   }

   public void setParam(String param) {
      this.param = param;
   }

   public long getLastOffenseTime() {
      return this.lastOffense;
   }

   public int getDbId() {
      return this.dbid;
   }

   public void setDbId(int dbid) {
      this.dbid = dbid;
   }
}
