package objects.fields.gameobject;

import network.models.CField;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.users.MapleCharacter;
import objects.users.MapleClient;

public class Extractor extends MapleMapObject {
   public int owner;
   public int timeLeft;
   public int itemId;
   public int fee;
   public long startTime;
   public String ownerName;

   public Extractor(MapleCharacter owner, int itemId, int fee, int timeLeft) {
      this.owner = owner.getId();
      this.itemId = itemId;
      this.fee = fee;
      this.ownerName = owner.getName();
      this.startTime = System.currentTimeMillis();
      this.timeLeft = timeLeft;
      this.setPosition(owner.getPosition());
   }

   public int getTimeLeft() {
      return this.timeLeft;
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      client.getSession().writeAndFlush(CField.makeExtractor(this.owner, this.ownerName, this.getTruePosition(), this.getTimeLeft(), this.itemId, this.fee));
   }

   @Override
   public void sendDestroyData(MapleClient client) {
      client.getSession().writeAndFlush(CField.removeExtractor(this.owner));
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.EXTRACTOR;
   }
}
