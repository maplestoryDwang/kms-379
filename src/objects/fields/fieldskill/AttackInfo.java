package objects.fields.fieldskill;

import java.awt.Point;
import network.encode.PacketEncoder;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;

public class AttackInfo {
   private int attackIndex;
   private int angleMax;
   private int angleMin;
   private int attackDelay;
   private int duration;
   private int interval;
   private Point lt;
   private Point rb;
   private int z;
   private String objName;
   private int set;
   private Point pos;
   private Point position;
   private boolean left;

   public AttackInfo(int attackIndex, MapleData data) {
      this.attackIndex = attackIndex;
      this.setAngleMax(MapleDataTool.getInt("angleMax", data, 0));
      this.setAngleMin(MapleDataTool.getInt("angleMin", data, 0));
      this.setAttackDelay(MapleDataTool.getInt("attackDelay", data, 0));
      this.setDuration(MapleDataTool.getInt("duration", data, 0));
      this.setInterval(MapleDataTool.getInt("interval", data, 0));
      this.setLt(MapleDataTool.getPoint("lt", data, new Point(0, 0)));
      this.setRb(MapleDataTool.getPoint("rb", data, new Point(0, 0)));
      this.setZ(MapleDataTool.getInt("z", data, 0));
      this.setObjName(MapleDataTool.getString("objName", data, ""));
      this.setSet(MapleDataTool.getInt("set", data, 0));
      this.setPos(MapleDataTool.getPoint("pos", data, new Point(0, 0)));
   }

   public int getAngleMax() {
      return this.angleMax;
   }

   public void setAngleMax(int angleMax) {
      this.angleMax = angleMax;
   }

   public int getAngleMin() {
      return this.angleMin;
   }

   public void setAngleMin(int angleMin) {
      this.angleMin = angleMin;
   }

   public int getAttackDelay() {
      return this.attackDelay;
   }

   public void setAttackDelay(int attackDelay) {
      this.attackDelay = attackDelay;
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   public int getInterval() {
      return this.interval;
   }

   public void setInterval(int interval) {
      this.interval = interval;
   }

   public Point getLt() {
      return this.lt;
   }

   public void setLt(Point lt) {
      this.lt = lt;
   }

   public Point getRb() {
      return this.rb;
   }

   public void setRb(Point rb) {
      this.rb = rb;
   }

   public int getZ() {
      return this.z;
   }

   public void setZ(int z) {
      this.z = z;
   }

   public Point getPosition() {
      return this.position;
   }

   public void setPosition(Point position) {
      this.position = position;
   }

   public boolean isLeft() {
      return this.left;
   }

   public void setLeft(boolean left) {
      this.left = left;
   }

   public String getObjName() {
      return this.objName;
   }

   public void setObjName(String objName) {
      this.objName = objName;
   }

   public int getSet() {
      return this.set;
   }

   public void setSet(int set) {
      this.set = set;
   }

   public Point getPos() {
      return this.pos;
   }

   public void setPos(Point pos) {
      this.pos = pos;
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.duration);
      packet.writeInt(this.interval);
      packet.writeInt(this.angleMin);
      packet.writeInt(this.angleMax);
      packet.writeInt(this.attackDelay);
      packet.writeInt(this.z);
      packet.writeInt(this.set);
      packet.writeMapleAsciiString(this.objName);
      packet.writeShort(0);
      packet.writeInt(this.lt.y);
      packet.writeInt(this.rb.y);
      packet.writeInt(this.lt.x);
      packet.writeInt(this.rb.x);
      packet.writeInt(this.getPosition().x);
      packet.writeInt(this.getPosition().y);
      packet.write(this.isLeft());
   }
}
