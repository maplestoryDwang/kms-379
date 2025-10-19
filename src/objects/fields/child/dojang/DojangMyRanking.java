package objects.fields.child.dojang;

import network.encode.PacketEncoder;

public class DojangMyRanking {
   private int point;
   private int rank;
   private int percentage;

   public DojangMyRanking(int point, int rank, int percentage) {
      this.point = point;
      this.rank = rank;
      this.percentage = percentage;
   }

   public int getPoint() {
      return this.point;
   }

   public void setPoint(int point) {
      this.point = point;
   }

   public int getRank() {
      return this.rank;
   }

   public void setRank(int rank) {
      this.rank = rank;
   }

   public int getPercentage() {
      return this.percentage;
   }

   public void setPercentage(int percentage) {
      this.percentage = percentage;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getPoint());
      packet.writeInt(this.getRank());
      packet.writeInt(this.getPercentage());
   }
}
