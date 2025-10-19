package objects.users;

import network.encode.PacketEncoder;
import network.models.PacketHelper;
import objects.item.Item;

public class MapleCabinetItem {
   private int index;
   private long expiredTime;
   private String title;
   private String desc;
   private Item item;
   private long meso;

   public MapleCabinetItem(int index, long expiredTime, String title, String desc, Item item) {
      this.index = index;
      this.expiredTime = expiredTime;
      this.title = title;
      this.desc = desc;
      this.item = item;
      this.meso = 0L;
   }

   public MapleCabinetItem(int index, long expiredTime, String title, String desc, Item item, long meso) {
      this.index = index;
      this.expiredTime = expiredTime;
      this.title = title;
      this.desc = desc;
      this.item = item;
      this.meso = meso;
   }

   public void encode(PacketEncoder packet) {
      packet.write(1);
      packet.writeInt(1);
      packet.writeInt(this.getIndex());
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeLong(PacketHelper.getTime(this.getExpiredTime()));
      packet.writeMapleAsciiString(this.getTitle());
      packet.writeMapleAsciiString(this.getDesc());
      packet.writeLong(this.getMeso());
      packet.write(this.getItem().getItemId() / 1000000);
      PacketHelper.addItemInfo(packet, this.getItem());
   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public long getExpiredTime() {
      return this.expiredTime;
   }

   public void setExpiredTime(long expiredTime) {
      this.expiredTime = expiredTime;
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getDesc() {
      return this.desc;
   }

   public void setDesc(String desc) {
      this.desc = desc;
   }

   public Item getItem() {
      return this.item;
   }

   public void setItem(Item item) {
      this.item = item;
   }

   public long getMeso() {
      return this.meso;
   }

   public void setMeso(long meso) {
      this.meso = meso;
   }
}
