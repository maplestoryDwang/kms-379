package objects.fields.gameobject;

import java.awt.Point;
import network.models.CField;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.users.MapleCharacter;
import objects.users.MapleClient;

public class OpenGate extends MapleMapObject {
   private int owner;
   private int partyid;
   private int id;

   public OpenGate(MapleCharacter owner, Point pos, int id) {
      this.owner = owner.getId();
      this.partyid = owner.getParty() == null ? 0 : owner.getParty().getId();
      this.setPosition(pos);
      this.id = id;
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      client.getSession().writeAndFlush(CField.spawnMechDoor(this, false));
   }

   @Override
   public void sendDestroyData(MapleClient client) {
      client.getSession().writeAndFlush(CField.removeMechDoor(this, false));
   }

   public int getOwnerId() {
      return this.owner;
   }

   public int getPartyId() {
      return this.partyid;
   }

   public int getId() {
      return this.id;
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.DOOR;
   }
}
