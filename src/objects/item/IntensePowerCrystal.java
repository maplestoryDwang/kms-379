package objects.item;

import network.encode.PacketEncoder;

public class IntensePowerCrystal {
   private int playerID;
   private long itemUniqueID;
   private int memberCount;
   private int mobID;
   private long price;
   private long unk;
   private long gainTime;

   public IntensePowerCrystal(int playerID, long itemUniqueID, int memberCount, int mobID, long price, long unk, long gainTime) {
      this.setPlayerID(playerID);
      this.setItemUniqueID(itemUniqueID);
      this.setMemberCount(memberCount);
      this.setMobID(mobID);
      this.setPrice(price);
      this.setUnk(unk);
      this.setGainTime(gainTime);
   }

   public int getPlayerID() {
      return this.playerID;
   }

   public void setPlayerID(int playerID) {
      this.playerID = playerID;
   }

   public long getItemUniqueID() {
      return this.itemUniqueID;
   }

   public void setItemUniqueID(long itemUniqueID) {
      this.itemUniqueID = itemUniqueID;
   }

   public int getMemberCount() {
      return this.memberCount;
   }

   public void setMemberCount(int memberCount) {
      this.memberCount = memberCount;
   }

   public int getMobID() {
      return this.mobID;
   }

   public void setMobID(int mobID) {
      this.mobID = mobID;
   }

   public long getPrice() {
      return this.price;
   }

   public void setPrice(long price) {
      this.price = price;
   }

   public long getUnk() {
      return this.unk;
   }

   public void setUnk(long unk) {
      this.unk = unk;
   }

   public long getGainTime() {
      return this.gainTime;
   }

   public void setGainTime(long gainTime) {
      this.gainTime = gainTime;
   }

   public void encode(PacketEncoder packet) {
      packet.writeLong(this.itemUniqueID);
      packet.writeInt(this.mobID);
      packet.writeInt(this.memberCount);
      packet.writeLong(this.price);
      packet.writeLong(this.unk);
      packet.writeLong(this.gainTime);
   }
}
