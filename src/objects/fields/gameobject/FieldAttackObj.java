package objects.fields.gameobject;

import java.awt.Point;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.users.MapleClient;

public class FieldAttackObj extends MapleMapObject {
   private int playerID;
   private int reserveCID;
   private byte direction;
   private Point position;
   private long createTime;
   private long endTime;

   public FieldAttackObj(int playerID, int reserveCID, byte direction, Point position, int duration) {
      this.playerID = playerID;
      this.reserveCID = reserveCID;
      this.direction = direction;
      this.position = position;
      this.createTime = System.currentTimeMillis();
      this.endTime = this.createTime + duration;
   }

   public int getPlayerID() {
      return this.playerID;
   }

   public void setPlayerID(int playerID) {
      this.playerID = playerID;
   }

   public int getReserveCID() {
      return this.reserveCID;
   }

   public void setReserveCID(int reserveCID) {
      this.reserveCID = reserveCID;
   }

   public byte getDirection() {
      return this.direction;
   }

   public void setDirection(byte direction) {
      this.direction = direction;
   }

   @Override
   public Point getPosition() {
      return this.position;
   }

   @Override
   public void setPosition(Point position) {
      this.position = position;
   }

   public long getEndTime() {
      return this.endTime;
   }

   public boolean checkRemove(long time) {
      return time >= this.getEndTime();
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getObjectId());
      packet.writeInt(1);
      packet.writeInt(this.getPlayerID());
      packet.writeInt(this.getReserveCID());
      packet.write(0);
      packet.writeInt(this.getPosition().x);
      packet.writeInt(this.getPosition().y);
      packet.write(this.getDirection());
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.FIELD_ATTACK_OBJ;
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      if (!this.checkRemove(System.currentTimeMillis()) && client.getPlayer().getMap().getCharacterById(this.getPlayerID()) != null) {
         client.getSession().writeAndFlush(CField.fieldAttackObj_Create(this));
      }
   }

   @Override
   public void sendDestroyData(MapleClient client) {
      client.getSession().writeAndFlush(CField.fieldAttackObj_Remove(this.getObjectId()));
   }
}
