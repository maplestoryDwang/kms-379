package objects.fields.child.karrotte.guardian;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.encode.PacketEncoder;

public class SphereOfOdium extends GuardianEntry {
   public static List<Point> movePath = new ArrayList<>();
   private int unk4;
   private byte unk5;
   private int unk6;
   private int unk7;
   private int unk8;

   public SphereOfOdium(int index, Point position, byte unk, GuardianType type, int refMobID) {
      super(index, position, unk, type, refMobID);
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getUnk4());
      packet.write(this.getUnk5());
      packet.writeInt(this.getUnk7());
      packet.writeInt(this.getUnk8());
      packet.writeInt(this.getRefMobID());
      packet.writeInt(this.getType().getType());
      packet.writeInt(this.getDeactiveHitCount());
      packet.writeInt(movePath.size());
      movePath.forEach(m -> {
         packet.writeInt(m.x);
         packet.writeInt(m.y);
      });
   }

   public int getUnk4() {
      return this.unk4;
   }

   public void setUnk4(int unk4) {
      this.unk4 = unk4;
   }

   public byte getUnk5() {
      return this.unk5;
   }

   public void setUnk5(byte unk5) {
      this.unk5 = unk5;
   }

   public int getUnk6() {
      return this.unk6;
   }

   public void setUnk6(int unk6) {
      this.unk6 = unk6;
   }

   public int getUnk7() {
      return this.unk7;
   }

   public void setUnk7(int unk7) {
      this.unk7 = unk7;
   }

   public int getUnk8() {
      return this.unk8;
   }

   public void setUnk8(int unk8) {
      this.unk8 = unk8;
   }

   static {
      movePath.add(new Point(1690, 389));
      movePath.add(new Point(1509, -553));
      movePath.add(new Point(1209, 389));
      movePath.add(new Point(909, -553));
      movePath.add(new Point(609, 389));
      movePath.add(new Point(309, -553));
      movePath.add(new Point(9, 389));
      movePath.add(new Point(-309, -553));
      movePath.add(new Point(-609, 389));
   }
}
