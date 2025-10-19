package constants;

public enum DailyEventType {
   DropRateFever(0),
   MesoRateFever(1),
   ExpRateFever(2),
   MobGenFever(3),
   StarForceDiscount(4),
   CubeFever(5),
   ExpRateFever_(6);

   private int type;

   private DailyEventType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
