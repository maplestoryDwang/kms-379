package objects.fields.child.karrotte.guardian;

import java.awt.Point;
import network.encode.PacketEncoder;
import objects.utils.Rect;

public class FighterPlane extends GuardianEntry {
   public static Rect basicRect = new Rect(-720, -553, 1785, -553);
   private int unk3;
   private int attackDelay;
   private byte direction;
   private Rect rect;
   private long time;

   public FighterPlane(int index, Point position, byte unk, GuardianType type, int refMobID) {
      super(index, position, unk, type, refMobID);
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getRefMobID());
      packet.writeInt(this.getType().getType());
      packet.writeInt(this.getUnk2());
      packet.writeInt(this.getAttackInterval());
      packet.writeInt(this.getAttackDelay());
      packet.writeInt(this.getDeactiveHitCount());
      packet.writeInt(this.getUnk3());
      packet.write(this.getDirection());
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(this.getPosition().x);
      packet.writeInt(this.getPosition().y);
      packet.writeInt(this.getRect().getLeft());
      packet.writeInt(this.getRect().getTop());
      packet.writeInt(this.getRect().getRight());
      packet.writeInt(this.getRect().getBottom());
   }

   public int getUnk3() {
      return this.unk3;
   }

   public void setUnk3(int unk3) {
      this.unk3 = unk3;
   }

   public int getAttackDelay() {
      return this.attackDelay;
   }

   public void setAttackDelay(int attackDelay) {
      this.attackDelay = attackDelay;
   }

   public byte getDirection() {
      return this.direction;
   }

   public void setDirection(byte direction) {
      this.direction = direction;
   }

   public Rect getRect() {
      return this.rect;
   }

   public void setRect(Rect rect) {
      this.rect = rect;
   }

   public long getTime() {
      return this.time;
   }

   public void setTime(long time) {
      this.time = time;
   }
}
