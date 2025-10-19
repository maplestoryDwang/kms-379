package objects.fields.child.minigame.space;

import network.encode.PacketEncoder;

public class Mission2SpaceUnk {
   private int unk1;
   private int unk2;

   public Mission2SpaceUnk(int unk1, int unk2) {
      this.setUnk1(unk1);
      this.setUnk2(unk2);
   }

   public int getUnk1() {
      return this.unk1;
   }

   public void setUnk1(int unk1) {
      this.unk1 = unk1;
   }

   public int getUnk2() {
      return this.unk2;
   }

   public void setUnk2(int unk2) {
      this.unk2 = unk2;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.unk1);
      packet.writeInt(this.unk2);
   }
}
