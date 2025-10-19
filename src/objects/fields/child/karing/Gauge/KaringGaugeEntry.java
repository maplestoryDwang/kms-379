package objects.fields.child.karing.Gauge;

public class KaringGaugeEntry {
   public int goongiGauge;
   public int doolGauge;
   public int hondonGauge;
   public int memtality;
   public boolean goongiClear;
   public boolean doolClear;
   public boolean hondonClear;

   public KaringGaugeEntry(int memtalityGauge, int goongiGauge, int doolGauge, int hondonGauge, boolean goongiClear, boolean doolClear, boolean hondonClear) {
      this.memtality = memtalityGauge;
      this.goongiGauge = goongiGauge;
      this.doolGauge = doolGauge;
      this.hondonGauge = hondonGauge;
      this.goongiClear = goongiClear;
      this.doolClear = doolClear;
      this.hondonClear = hondonClear;
   }

   public int getMemtality() {
      return this.memtality;
   }

   public void setMemtality(int gauge) {
      this.memtality = gauge;
   }

   public int getGoongiGauge() {
      return this.goongiGauge;
   }

   public void setGoongiGauge(int gauge) {
      this.goongiGauge = gauge;
   }

   public int getDoolGauge() {
      return this.doolGauge;
   }

   public void setDoolGauge(int Gauge) {
      this.doolGauge = Gauge;
   }

   public int getHondonGauge() {
      return this.hondonGauge;
   }

   public void setHondonGauge(int Gauge) {
      this.hondonGauge = Gauge;
   }

   public boolean isGoongiClear() {
      return this.goongiClear;
   }

   public void setGoongiClear(boolean clear) {
      this.goongiClear = clear;
   }

   public boolean isDoolClear() {
      return this.doolClear;
   }

   public void setDoolClear(boolean clear) {
      this.doolClear = clear;
   }

   public boolean isHondonClear() {
      return this.hondonClear;
   }

   public void setHondonClear(boolean clear) {
      this.hondonClear = clear;
   }
}
