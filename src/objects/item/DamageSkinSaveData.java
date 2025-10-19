package objects.item;

import network.encode.PacketEncoder;

public class DamageSkinSaveData {
   private int damageSkinID;
   private int itemID;
   private boolean notSave;
   private String desc;

   public DamageSkinSaveData(int damageSkinID, int itemID, boolean notSave, String desc) {
      this.damageSkinID = damageSkinID;
      this.itemID = itemID;
      this.notSave = notSave;
      this.desc = desc;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getDamageSkinID());
      packet.writeInt(this.getItemID());
      packet.write(this.isNotSave());
      packet.writeMapleAsciiString(this.getDesc());
      if (this.getItemID() != 1) {
         packet.writeInt(0);
      }
   }

   public int getDamageSkinID() {
      return this.damageSkinID;
   }

   public void setDamageSkinID(int damageSkinID) {
      this.damageSkinID = damageSkinID;
   }

   public int getItemID() {
      return this.itemID;
   }

   public void setItemID(int itemID) {
      this.itemID = itemID;
   }

   public boolean isNotSave() {
      return this.notSave;
   }

   public void setNotSave(boolean notSave) {
      this.notSave = notSave;
   }

   public String getDesc() {
      return this.desc;
   }

   public void setDesc(String desc) {
      this.desc = desc;
   }
}
