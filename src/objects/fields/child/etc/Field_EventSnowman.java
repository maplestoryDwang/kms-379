package objects.fields.child.etc;

import constants.ServerConstants;
import java.util.ArrayList;
import java.util.List;
import network.game.GameServer;
import network.models.CField;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public class Field_EventSnowman extends Field {
   private long snowmanSpawnedTime = 0L;
   private List<Integer> rewardGetUsers = new ArrayList<>();

   public Field_EventSnowman(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.snowmanSpawnedTime = 0L;
      this.rewardGetUsers.clear();
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.getSnowmanSpawnedTime() != 0L && System.currentTimeMillis() - this.snowmanSpawnedTime >= 900000L) {
         for (GameServer gameServer : GameServer.getAllInstances()) {
            for (MapleCharacter player : gameServer.getPlayerStorage().getAllCharacters()) {
               if (player != null) {
                  player.removeEventRabbitPortal();
               }
            }
         }

         for (MapleCharacter playerx : this.getCharactersThreadsafe()) {
            if (playerx != null && playerx.getMapId() == this.getId()) {
               playerx.dropMessage(5, "제한 시간 내에 거대 눈사람을 처치하지 못하여 보상을 획득하지 못했습니다.");
               playerx.warp(ServerConstants.TownMap);
            }
         }

         this.resetFully(false);
         this.setSnowmanSpawnedTime(0L);
      }
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      long delta = System.currentTimeMillis() - this.snowmanSpawnedTime;
      long remain = 900000L - delta;
      player.send(CField.getStopwatch((int)remain));
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      if (mob.getId() == 9500319) {
         for (GameServer gameServer : GameServer.getAllInstances()) {
            for (MapleCharacter player : gameServer.getPlayerStorage().getAllCharacters()) {
               if (player != null) {
                  player.removeEventRabbitPortal();
               }
            }
         }

         for (MapleCharacter playerx : this.getCharactersThreadsafe()) {
            if (playerx != null && !this.rewardGetUsers.contains(playerx.getId()) && playerx.getMapId() == this.getId()) {
               playerx.dropMessage(5, "거대 눈사람을 처치하여 메리 크리스마스 선물상자 1개를 획득했습니다.");
               playerx.gainItem(2430033, 1, false, 0L, "크리스마스 이벤트로 획득");
               playerx.setRegisterTransferField(ServerConstants.TownMap);
               playerx.setRegisterTransferFieldTime(System.currentTimeMillis() + 2000L);
               playerx.send(CField.addPopupSay(9062000, 5000, "거대 눈사람을 처치하여 #b메리 크리스마스 선물상자#k를 획득했습니다.", ""));
               this.rewardGetUsers.add(playerx.getId());
            }
         }

         this.setSnowmanSpawnedTime(0L);
      }
   }

   public long getSnowmanSpawnedTime() {
      return this.snowmanSpawnedTime;
   }

   public void setSnowmanSpawnedTime(long snowmanSpawnedTime) {
      this.snowmanSpawnedTime = snowmanSpawnedTime;
   }
}
