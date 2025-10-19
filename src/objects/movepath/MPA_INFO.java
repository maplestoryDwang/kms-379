package objects.movepath;

import java.awt.Point;
import network.encode.PacketEncoder;

public class MPA_INFO implements LifeMovementFragment {
   private int mpa;
   private int p1;
   private int p2;
   private int p3;
   private int p4;
   private int p5;
   private int p6;

   public MPA_INFO(int nMPA, int nParam1, int nParam2, int nParam3, int nParam4, int nParam5, int nParam6) {
      this.mpa = nMPA;
      this.p1 = nParam1;
      this.p2 = nParam2;
      this.p3 = nParam3;
      this.p4 = nParam4;
      this.p5 = nParam5;
      this.p6 = nParam6;
   }

   @Override
   public void serialize(PacketEncoder packet) {
      packet.writeShort(this.mpa);
      packet.writeShort(this.p1);
      packet.writeShort(this.p2);
      packet.writeShort(this.p3);
      packet.writeShort(this.p4);
      packet.writeShort(this.p5);
      packet.writeShort(this.p6);
   }

   @Override
   public Point getPosition() {
      return new Point(0, 0);
   }
}
