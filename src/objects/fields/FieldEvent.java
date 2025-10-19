package objects.fields;

import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public abstract class FieldEvent {
   public long expireTime;
   public boolean expired;
   public Field map;

   public abstract void onStart();

   public abstract void onEnd();

   public abstract void onMobEnter(MapleMonster var1);

   public abstract void onMobLeave(MapleMonster var1);

   public abstract void onUserEnter(MapleCharacter var1);

   public abstract void onUserLeave(MapleCharacter var1);

   public FieldEvent(Field map, long expireTime) {
      this.map = map;
      this.expireTime = expireTime;
   }

   public void onUpdatePerSecond(long now) {
      if (this.expireTime <= now) {
         this.onTimeOut();
      }
   }

   public void onTimeOut() {
      this.expired = true;
      this.onEnd();
      this.map.setFieldEvent(null);
   }
}
