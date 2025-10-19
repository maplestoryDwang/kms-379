package objects.users.skills;

public class DarkLigntningEntry {
   int remainCount;
   long startTime;
   long endTime;

   public DarkLigntningEntry(int remainCount, long startTime) {
      this.remainCount = remainCount;
      this.startTime = startTime;
      this.endTime = startTime + 15000L;
   }

   public int getRemainCount() {
      return this.remainCount;
   }

   public void decRemainCount() {
      this.remainCount--;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public long getEndTime() {
      return this.endTime;
   }

   public void setRemainCount(int remainCount) {
      this.remainCount = remainCount;
   }
}
