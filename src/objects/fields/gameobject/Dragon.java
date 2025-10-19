package objects.fields.gameobject;

import network.models.CField;
import objects.fields.AnimatedMapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.users.MapleCharacter;
import objects.users.MapleClient;

public class Dragon extends AnimatedMapleMapObject {
   private int owner;
   private int jobid;

   public Dragon(MapleCharacter owner) {
      this.owner = owner.getId();
      this.jobid = owner.getJob();
      if (this.jobid >= 2200 && this.jobid <= 2218) {
         this.setPosition(owner.getTruePosition());
         this.setStance(4);
      } else {
         throw new RuntimeException("Trying to create a dragon for a non-Evan");
      }
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      client.getSession().writeAndFlush(CField.spawnDragon(this));
   }

   @Override
   public void sendDestroyData(MapleClient client) {
      client.getSession().writeAndFlush(CField.removeDragon(this.owner));
   }

   public int getOwner() {
      return this.owner;
   }

   public int getJobId() {
      return this.jobid;
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.SUMMON;
   }
}
