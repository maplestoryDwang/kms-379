package objects.fields;

import java.awt.Point;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.users.MapleClient;

public class Grenade extends MapleMapObject {
   private int id;
   private boolean isLeft;
   private int keyDown;
   private int skillId;
   private int bySummonedId;
   private int skillLevel;
   private int ownerId = -1;
   private long startTime = 0L;
   private Point point;

   public Grenade(int id, int keyDown, int skillId, int skillLevel, int bySummonedId, Point point, boolean isLeft) {
      this.id = id;
      this.keyDown = keyDown;
      this.skillId = skillId;
      this.skillLevel = skillLevel;
      this.bySummonedId = bySummonedId;
      this.point = point;
      this.isLeft = isLeft;
      this.setPosition(point);
   }

   public void encode(PacketEncoder packet) {
      packet.writeInt(this.getId());
      packet.writeInt(this.point.x);
      packet.writeInt(this.point.y);
      packet.writeInt(this.keyDown);
      packet.writeInt(this.getSkillId());
      packet.writeInt(this.bySummonedId);
      packet.writeInt(this.skillLevel);
      packet.write(this.isLeft ? 1 : 0);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(0);
      packet.writeInt(0);
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.GRENADE;
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      client.getSession().writeAndFlush(CField.throwGrenade(client.getPlayer().getId(), this));
   }

   @Override
   public void sendDestroyData(MapleClient client) {
      client.getSession().writeAndFlush(CField.destoryGrenade(client.getPlayer().getId(), this.getId()));
   }

   public int getSkillId() {
      return this.skillId;
   }

   public int getId() {
      return this.id;
   }

   public int getOwnerId() {
      return this.ownerId;
   }

   public void setOwnerId(int ownerId) {
      this.ownerId = ownerId;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public void setStartTime(long startTime) {
      this.startTime = startTime;
   }
}
