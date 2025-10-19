package objects.fields;

import java.awt.Point;
import java.util.Collections;
import network.models.CField;
import objects.users.MapleCharacter;
import objects.users.MapleClient;

public class Wreckage extends AnimatedMapleMapObject {
   int duration;
   int skillID;
   int index;
   int ownerID;
   long endTime;
   MapleCharacter owner;
   Point position;

   public Wreckage(MapleCharacter owner, int duration, int skillID, int index, Point position) {
      this.owner = owner;
      this.ownerID = owner.getId();
      this.duration = duration;
      this.skillID = skillID;
      this.index = index;
      this.position = position;
      this.setPosition(position);
      this.endTime = System.currentTimeMillis() + duration;
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.WRECKAGE;
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      if (client.getPlayer().getMap().getCharacterById(this.ownerID) != null) {
         client.getSession().writeAndFlush(CField.AddWreckage(this.owner.getId(), this.position, this.duration, this.getObjectId(), this.skillID, this.index));
      }
   }

   @Override
   public void sendDestroyData(MapleClient client) {
      client.getSession().writeAndFlush(CField.DelWreckage(this.owner.getId(), Collections.singletonList(this), false));
   }

   public long getEndTime() {
      return this.endTime;
   }

   public MapleCharacter getOwner() {
      return this.owner;
   }

   @Override
   public Point getPosition() {
      return this.position;
   }

   public int getSkillID() {
      return this.skillID;
   }

   public int getOwnerID() {
      return this.ownerID;
   }

   public final void removeWreckage(Field map, boolean useSkill) {
      map.removeMapObject(this);
      this.owner.removeVisibleMapObject(this);
   }
}
