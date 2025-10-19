package objects.fields.gameobject;

import java.awt.Point;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import network.models.CField;
import objects.fields.Field;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.item.Equip;
import objects.item.Item;
import objects.users.MapleCharacter;
import objects.users.MapleClient;

public class Drop extends MapleMapObject {
   protected Item item;
   protected MapleMapObject dropper;
   protected int character_ownerid;
   protected int meso = 0;
   protected int questid = -1;
   protected byte type;
   private int dropMotionType;
   private int dropSpeed;
   private int dropSlopeAngle;
   private int dropDelay = 0;
   private boolean individual = false;
   private boolean bossDropEquip = false;
   protected boolean pickedUp = false;
   protected boolean playerDrop;
   protected boolean randDrop = false;
   protected boolean collisionPickUp = false;
   private boolean bossDrop = false;
   protected long nextExpiry = 0L;
   protected long nextFFA = 0L;
   protected long exp = 0L;
   protected byte explosiveDrop = 0;
   protected Equip equip;
   private int specialType = 0;
   private int unk1 = 0;
   private int ownerID = 0;
   private long droptime = 0L;
   private ReentrantLock lock = new ReentrantLock();

   public Drop(Item item, Point position, MapleMapObject dropper, MapleCharacter owner, byte type, boolean playerDrop) {
      this.setPosition(position);
      this.item = item;
      this.dropper = dropper;
      this.character_ownerid = owner.getId();
      this.type = type;
      this.playerDrop = playerDrop;
   }

   public Drop(Item item, Point position, MapleMapObject dropper, MapleCharacter owner, byte type, boolean playerDrop, Equip equip) {
      this.setPosition(position);
      this.item = item;
      this.dropper = dropper;
      this.character_ownerid = owner.getId();
      this.type = type;
      this.playerDrop = playerDrop;
      this.equip = equip;
   }

   public Drop(Item item, Point position, MapleMapObject dropper, MapleCharacter owner, byte type, boolean playerDrop, int questid, boolean individual) {
      this.setPosition(position);
      this.item = item;
      this.dropper = dropper;
      this.character_ownerid = owner.getId();
      this.type = type;
      this.playerDrop = playerDrop;
      this.questid = questid;
      this.equip = this.equip;
      this.individual = individual;
   }

   public Drop(int meso, Point position, MapleMapObject dropper, MapleCharacter owner, byte type, boolean playerDrop) {
      this.setPosition(position);
      this.item = null;
      this.dropper = dropper;
      this.character_ownerid = owner.getId();
      this.meso = meso;
      this.type = type;
      this.playerDrop = playerDrop;
   }

   public Drop(Point position, Item item) {
      this.setPosition(position);
      this.item = item;
      this.character_ownerid = 0;
      this.type = 2;
      this.playerDrop = false;
      this.randDrop = true;
   }

   public Drop(Item item, Point position) {
      this.setPosition(position);
      this.item = item;
      this.character_ownerid = 0;
      this.type = 2;
      this.playerDrop = true;
   }

   public final Item getItem() {
      return this.item;
   }

   public void setItem(Item z) {
      this.item = z;
   }

   public final int getQuest() {
      return this.questid;
   }

   public final int getItemId() {
      return this.getMeso() > 0 ? this.meso : this.item.getItemId();
   }

   public final MapleMapObject getDropper() {
      return this.dropper;
   }

   public final int getOwner() {
      return this.character_ownerid;
   }

   public final int getMeso() {
      return this.meso;
   }

   public final boolean isPlayerDrop() {
      return this.playerDrop;
   }

   public final boolean isPickedUp() {
      return this.pickedUp;
   }

   public void setPickedUp(boolean pickedUp) {
      this.pickedUp = pickedUp;
   }

   public byte getOwnType() {
      return this.type;
   }

   public void setDropType(byte z) {
      this.type = z;
   }

   public final boolean isRandDrop() {
      return this.randDrop;
   }

   @Override
   public final MapleMapObjectType getType() {
      return MapleMapObjectType.ITEM;
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      if ((this.questid <= 0 || client.getPlayer().getQuestStatus(this.questid) == 1)
         && (!this.individual || this.character_ownerid == client.getPlayer().getId())) {
         client.getSession().writeAndFlush(CField.dropItemFromMapObject(this, null, this.getTruePosition(), (byte)2));
      }
   }

   @Override
   public void sendDestroyData(MapleClient client) {
      client.getSession().writeAndFlush(CField.removeItemFromMap(this.getObjectId(), 1, 0));
   }

   public Lock getLock() {
      return this.lock;
   }

   public void registerExpire(long time) {
      this.nextExpiry = System.currentTimeMillis() + time;
   }

   public void registerFFA(long time) {
      this.nextFFA = System.currentTimeMillis() + time;
   }

   public boolean shouldExpire(long now) {
      return !this.pickedUp && this.nextExpiry > 0L && this.nextExpiry < now;
   }

   public boolean shouldFFA(long now) {
      return !this.pickedUp && this.type < 2 && this.nextFFA > 0L && this.nextFFA < now;
   }

   public boolean hasFFA() {
      return this.nextFFA > 0L;
   }

   public void expire(Field map) {
      this.pickedUp = true;
      map.broadcastMessage(CField.removeItemFromMap(this.getObjectId(), 0, 0));
      map.removeMapObject(this);
      if (this.randDrop) {
         map.spawnRandDrop();
      }
   }

   public final Equip getEquip() {
      return this.equip;
   }

   public void setExp(long exp) {
      this.exp = exp;
   }

   public long getExp() {
      return this.exp;
   }

   public boolean isCollisionPickUp() {
      return this.collisionPickUp;
   }

   public void setCollisionPickUp(boolean collisionPickUp) {
      this.collisionPickUp = collisionPickUp;
   }

   public byte getExplosiveDrop() {
      return this.explosiveDrop;
   }

   public void setExplosiveDrop(byte explosiveDrop) {
      this.explosiveDrop = explosiveDrop;
   }

   public int getDropMotionType() {
      return this.dropMotionType;
   }

   public void setDropMotionType(int dropMotionType) {
      this.dropMotionType = dropMotionType;
   }

   public int getDropSpeed() {
      return this.dropSpeed;
   }

   public void setDropSpeed(int dropSpeed) {
      this.dropSpeed = dropSpeed;
   }

   public int getDropSlopeAngle() {
      return this.dropSlopeAngle;
   }

   public void setDropSlopeAngle(int dropSlopeAngle) {
      this.dropSlopeAngle = dropSlopeAngle;
   }

   public int getDropDelay() {
      return this.dropDelay;
   }

   public void setDropDelay(int dropDelay) {
      this.dropDelay = dropDelay;
   }

   public boolean isIndividual() {
      return this.individual;
   }

   public void setIndividual(boolean individual) {
      this.individual = individual;
   }

   public boolean isBossDrop() {
      return this.bossDrop;
   }

   public void setBossDrop(boolean bossDrop) {
      this.bossDrop = bossDrop;
   }

   public boolean isBossDropEquip() {
      return this.bossDropEquip;
   }

   public void setBossDropEquip(boolean bossDropEquip) {
      this.bossDropEquip = bossDropEquip;
   }

   public int getCharacter_ownerid() {
      return this.character_ownerid;
   }

   public int getSpecialType() {
      return this.specialType;
   }

   public void setSpecialType(int specialType) {
      this.specialType = specialType;
   }

   public int getOwnerID() {
      return this.ownerID;
   }

   public void setOwnerID(int ownerID) {
      this.ownerID = ownerID;
   }

   public int getUnk1() {
      return this.unk1;
   }

   public void setUnk1(int unk1) {
      this.unk1 = unk1;
   }

   public long getDropTime() {
      return this.droptime;
   }

   public void setDropTime(long time) {
      this.droptime = time;
   }
}
