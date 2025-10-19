package objects.fields.child.karing.Gauge;

public enum KaringGaugeType {
   Mentality(0),
   Season(1);

   private int type;

   private KaringGaugeType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static KaringGauge.karingGaugeType getType(int type) {
      for (KaringGauge.karingGaugeType t : KaringGauge.karingGaugeType.values()) {
         if (t.getType() == type) {
            return t;
         }
      }

      return null;
   }
}
