package objects.users.looks.mannequin;

import network.encode.PacketEncoder;

public class MannequinEntry {
   private int type = 0;
   private int itemID = 0;
   private byte baseColor = -1;
   private byte addColor = 0;
   private byte baseProb = 0;

   public MannequinEntry() {
      this.itemID = 0;
      this.baseColor = -1;
      this.addColor = 0;
      this.baseProb = 0;
   }

   public MannequinEntry(int type, int itemID, byte baseColor, byte addColor, byte baseProb) {
      if (type == MannequinType.FaceRoom.getType()) {
         if (itemID > 100000) {
            if (itemID % 1000 == 0) {
               itemID /= 1000;
               baseColor = -1;
               addColor = 0;
               baseProb = 0;
            } else {
               int mixItemID = itemID;
               itemID /= 1000;
               addColor = (byte)(mixItemID / 100 % 10);
               baseColor = (byte)(itemID / 100 % 10);
               baseProb = (byte)(mixItemID % 100);
            }
         }
      } else if (type == MannequinType.HairRoom.getType() && itemID > 100000) {
         if (itemID % 1000 == 0) {
            itemID /= 1000;
            baseColor = -1;
            addColor = 0;
            baseProb = 0;
         } else {
            int mixItemID = itemID;
            itemID /= 1000;
            addColor = (byte)(mixItemID / 100 % 10);
            baseColor = (byte)(itemID % 10);
            baseProb = (byte)(mixItemID % 100);
         }
      }

      this.type = type;
      this.setItemID(itemID);
      this.setBaseColor(baseColor);
      this.setAddColor(addColor);
      this.setBaseProb(baseProb);
   }

   public int getItemID() {
      return this.itemID;
   }

   public void setItemID(int itemID) {
      this.itemID = itemID;
   }

   public byte getBaseColor() {
      return this.baseColor;
   }

   public void setBaseColor(byte baseColor) {
      this.baseColor = baseColor;
   }

   public byte getAddColor() {
      return this.addColor;
   }

   public void setAddColor(byte addColor) {
      this.addColor = addColor;
   }

   public byte getBaseProb() {
      return this.baseProb;
   }

   public void setBaseProb(byte baseProb) {
      this.baseProb = baseProb;
   }

   public void encode(PacketEncoder packet) {
      packet.write(0);
      if (this.baseColor != -1 && this.baseProb > 0) {
         int mixItemID = this.itemID;
         if (this.type == MannequinType.FaceRoom.getType()) {
            mixItemID = (this.itemID / 1000 * 1000 + this.itemID % 100 + this.baseColor * 100) * 1000 + this.addColor * 100 + this.baseProb;
         } else if (this.type == MannequinType.HairRoom.getType()) {
            mixItemID = this.itemID / 10 * 10 * 1000 + this.baseColor * 1000 + this.addColor * 100 + this.baseProb;
         }

         packet.writeInt(mixItemID);
      } else {
         packet.writeInt(this.itemID);
      }
   }
}
