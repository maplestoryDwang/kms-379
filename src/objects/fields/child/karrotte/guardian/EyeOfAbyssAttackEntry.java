package objects.fields.child.karrotte.guardian;

import network.encode.PacketEncoder;

public class EyeOfAbyssAttackEntry {
   private int key;
   private int unk1;
   private int targetID;
   private int unk2;

   public EyeOfAbyssAttackEntry(int key, int unk1, int unkTime, int unk2) {
      this.setKey(key);
      this.setUnk1(unk1);
      this.setTargetID(unkTime);
      this.setUnk2(unk2);
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getKey());
      packet.writeInt(this.getUnk1());
      packet.writeInt(this.getTargetID());
      packet.writeInt(this.getUnk2());
   }

   public int getKey() {
      return this.key;
   }

   public void setKey(int key) {
      this.key = key;
   }

   public int getUnk1() {
      return this.unk1;
   }

   public void setUnk1(int unk1) {
      this.unk1 = unk1;
   }

   public int getTargetID() {
      return this.targetID;
   }

   public void setTargetID(int targetID) {
      this.targetID = targetID;
   }

   public int getUnk2() {
      return this.unk2;
   }

   public void setUnk2(int unk2) {
      this.unk2 = unk2;
   }
}
