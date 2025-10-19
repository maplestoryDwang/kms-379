package objects.fields;

public class FieldCommandContext {
   public long nextPickTime = 0L;
   public int pickCount = 0;
   public int repeatCount = 0;
   public int sequence = 0;
   public long startTime = 0L;

   public FieldCommandContext(long nextPickTime, int pickCount, int repeatCount, int sequence, long startTime) {
      this.nextPickTime = nextPickTime;
      this.pickCount = pickCount;
      this.repeatCount = repeatCount;
      this.sequence = sequence;
      this.startTime = startTime;
   }
}
