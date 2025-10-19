package objects.fields;

import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public class MarriageEvent extends FieldEvent {
   public MarriageEvent(Field map, long expireTime) {
      super(map, expireTime);
   }

   @Override
   public void onStart() {
   }

   @Override
   public void onEnd() {
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
   }

   @Override
   public void onMobLeave(MapleMonster mob) {
   }

   @Override
   public void onUserEnter(MapleCharacter player) {
   }

   @Override
   public void onUserLeave(MapleCharacter player) {
   }
}
