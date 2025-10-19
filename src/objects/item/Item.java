package objects.item;

import database.DBConfig;
import java.io.Serializable;
import objects.androids.Android;
import objects.users.enchant.ItemFlag;

public class Item implements Comparable<Item>, Serializable {
   private int id;
   private short position;
   private short quantity;
   private int flag;
   private long expiration = -1L;
   private long inventoryitemid;
   private IntensePowerCrystal intensePowerCrystal = null;
   private MaplePet pet = null;
   private Android android = null;
   private long uniqueid;
   private String owner = "";
   private String GameMaster_log = "";
   private String giftFrom = "";
   private int onceTrade = 0;
   private long tempUniqueID;
   private long rebuyPrice = 0L;

   public Item(int id, short position, short quantity, int flag, long uniqueid) {
      this.id = id;
      this.position = position;
      this.quantity = quantity;
      this.flag = flag;
      this.uniqueid = uniqueid;
   }

   public Item(int id, short position, short quantity, int flag) {
      this.id = id;
      this.position = position;
      this.quantity = quantity;
      this.flag = flag;
      this.uniqueid = -1L;
   }

   public Item(int id, byte position, short quantity) {
      this.id = id;
      this.position = position;
      this.quantity = quantity;
      this.uniqueid = -1L;
   }

   public Item copy() {
      Item ret = new Item(this.id, this.position, this.quantity, this.flag, this.uniqueid);
      ret.pet = this.pet;
      ret.android = this.android;
      ret.owner = this.owner;
      ret.GameMaster_log = this.GameMaster_log;
      ret.expiration = this.expiration;
      ret.giftFrom = this.giftFrom;
      ret.onceTrade = this.onceTrade;
      ret.intensePowerCrystal = this.intensePowerCrystal;
      if (ret.intensePowerCrystal != null) {
         ret.intensePowerCrystal.setItemUniqueID(this.getUniqueId());
      }

      return ret;
   }

   public Item copyWithQuantity(short qq) {
      Item ret = new Item(this.id, this.position, qq, this.flag, this.uniqueid);
      ret.pet = this.pet;
      ret.owner = this.owner;
      ret.GameMaster_log = this.GameMaster_log;
      ret.expiration = this.expiration;
      ret.giftFrom = this.giftFrom;
      return ret;
   }

   public final void setPosition(short position) {
      this.position = position;
      if (this.pet != null) {
         this.pet.setInventoryPosition(position);
      }
   }

   public void setQuantity(short quantity) {
      this.quantity = quantity;
   }

   public final int getItemId() {
      return this.id;
   }

   public final int setItemId(int id) {
      this.id = id;
      return id;
   }

   public final short getPosition() {
      return this.position;
   }

   public final int getFlag() {
      return this.flag;
   }

   public final short getQuantity() {
      return this.quantity;
   }

   public byte getType() {
      return 2;
   }

   public final String getOwner() {
      return this.owner;
   }

   public final void setOwner(String owner) {
      this.owner = owner;
   }

   public final void setFlag(int flag) {
      if (DBConfig.isGanglim && this.getItemId() == 1662141 && (flag & ItemFlag.POSSIBLE_TRADING.getValue()) > 0) {
         try {
            throw new Exception();
         } catch (Exception var3) {
            System.out.println("안드로이드 교불 위치 디버그!!");
            var3.printStackTrace();
         }
      }

      this.flag = flag;
   }

   public final long getExpiration() {
      return this.expiration;
   }

   public final void setExpiration(long expire) {
      this.expiration = expire;
   }

   public final String getGMLog() {
      return this.GameMaster_log;
   }

   public void setGMLog(String GameMaster_log) {
      this.GameMaster_log = GameMaster_log;
   }

   public final long getUniqueId() {
      return this.uniqueid;
   }

   public void setUniqueId(long ui) {
      this.uniqueid = ui;
   }

   public final long getInventoryId() {
      return this.inventoryitemid;
   }

   public void setInventoryId(long ui) {
      this.inventoryitemid = ui;
   }

   public final MaplePet getPet() {
      return this.pet;
   }

   public final void setPet(MaplePet pet) {
      this.pet = pet;
      if (pet != null) {
         this.uniqueid = pet.getUniqueId();
      }
   }

   public final Android getAndroid() {
      if (this.getItemId() / 10000 == 166 && this.getUniqueId() > 0L) {
         if (this.android == null) {
            this.android = Android.loadFromDb(this.getItemId(), this.getUniqueId());
         }

         return this.android;
      } else {
         return null;
      }
   }

   public final void setAndroid(Android android) {
      this.android = android;
   }

   public void setGiftFrom(String gf) {
      this.giftFrom = gf;
   }

   public String getGiftFrom() {
      return this.giftFrom;
   }

   public int compareTo(Item other) {
      if (Math.abs(this.position) < Math.abs(other.getPosition())) {
         return -1;
      } else {
         return Math.abs(this.position) == Math.abs(other.getPosition()) ? 0 : 1;
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof Item)) {
         return false;
      } else {
         Item ite = (Item)obj;
         return this.uniqueid == ite.getUniqueId()
            && this.id == ite.getItemId()
            && this.quantity == ite.getQuantity()
            && Math.abs(this.position) == Math.abs(ite.getPosition());
      }
   }

   @Override
   public String toString() {
      return "Item: " + this.id + " quantity: " + this.quantity;
   }

   public int getOnceTrade() {
      return this.onceTrade;
   }

   public void setOnceTrade(int onceTrade) {
      this.onceTrade = onceTrade;
   }

   public long getRebuyPrice() {
      return this.rebuyPrice;
   }

   public void setRebuyPrice(long rebuyPrice) {
      this.rebuyPrice = rebuyPrice;
   }

   public IntensePowerCrystal getIntensePowerCrystal() {
      return this.intensePowerCrystal;
   }

   public void setIntensePowerCrystal(IntensePowerCrystal intensePowerCrystal) {
      this.intensePowerCrystal = intensePowerCrystal;
   }

   public long getTempUniqueID() {
      return this.tempUniqueID;
   }

   public void setTempUniqueID(long tempUniqueID) {
      this.tempUniqueID = tempUniqueID;
   }
}
