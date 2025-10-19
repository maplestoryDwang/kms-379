package objects.fields.child.minigame.onecard;

import constants.ServerConstants;
import network.models.CField;
import objects.fields.Field;
import objects.users.MapleCharacter;

public class Field_OneCardWait extends Field {
   public Field_OneCardWait(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();

      for (MapleCharacter player : this.getCharactersThreadsafe()) {
         if (player.getNextTransferMinigameField() > 0 && player.getNextTransferMinigameFieldTime() <= System.currentTimeMillis()) {
            boolean find = false;

            for (int i = player.getNextTransferMinigameField(); i < player.getNextTransferMinigameField() + 90; i++) {
               Field f = player.getClient().getChannelServer().getMapFactory().getMap(i);
               if (f != null && f.getCharactersSize() < 4) {
                  find = true;
               }
            }

            if (!find) {
               player.warp(ServerConstants.TownMap);
               player.setNextTransferMinigameField(0);
               player.setNextTransferMinigameFieldTime(0L);
               return;
            }

            for (int ix = player.getNextTransferMinigameField(); ix < player.getNextTransferMinigameField() + 90; ix++) {
               Field f = player.getClient().getChannelServer().getMapFactory().getMap(ix);
               if (f != null && f.getCharactersSize() < 4) {
                  if (f.getCharactersSize() <= 0) {
                     f.resetFully(false);
                  }

                  player.warp(ix);
                  break;
               }
            }

            player.setNextTransferMinigameField(0);
            player.setNextTransferMinigameFieldTime(0L);
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      player.send(CField.getClock(5));
      player.setNextTransferMinigameField(993189500);
      player.setNextTransferMinigameFieldTime(System.currentTimeMillis() + 5000L);
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }
}
