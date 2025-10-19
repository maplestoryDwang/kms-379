package objects.fields.obstacle;

public enum ObstacleAtomCreateType {
   NORMAL(0),
   IN_ROW(1),
   TORNADO(2),
   MOB_SKILL(3),
   RADIAL(4),
   DIAGONAL(5);

   private byte value;

   private ObstacleAtomCreateType(int value) {
      this.value = (byte)value;
   }

   public byte getValue() {
      return this.value;
   }
}
