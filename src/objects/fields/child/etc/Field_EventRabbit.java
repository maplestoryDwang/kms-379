package objects.fields.child.etc;

import constants.ServerConstants;
import java.util.ArrayList;
import java.util.List;
import network.game.GameServer;
import network.models.CField;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public class Field_EventRabbit extends Field {
   private long rabbitSpawnedTime = 0L;
   private List<Integer> rewardGetUsers = new ArrayList<>();

   public Field_EventRabbit(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.rabbitSpawnedTime = 0L;
      this.rewardGetUsers.clear();
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.getRabbitSpawnedTime() != 0L && System.currentTimeMillis() - this.rabbitSpawnedTime >= 900000L) {
         for (GameServer gameServer : GameServer.getAllInstances()) {
            for (MapleCharacter player : gameServer.getPlayerStorage().getAllCharacters()) {
               if (player != null) {
                  player.removeEventRabbitPortal();
               }
            }
         }

         for (MapleCharacter playerx : this.getCharactersThreadsafe()) {
            if (playerx != null && playerx.getMapId() == this.getId()) {
               playerx.dropMessage(5, "제한 시간 내에 월묘를 처치하지 못하여 보상을 획득하지 못했습니다.");
               playerx.warp(ServerConstants.TownMap);
            }
         }

         this.resetFully(false);
         this.setRabbitSpawnedTime(0L);
      }
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      long delta = System.currentTimeMillis() - this.rabbitSpawnedTime;
      long remain = 900000L - delta;
      player.send(CField.getStopwatch((int)remain));
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      if (mob.getId() == 9500006 || mob.getId() == 9500007) {
         for (GameServer gameServer : GameServer.getAllInstances()) {
            for (MapleCharacter player : gameServer.getPlayerStorage().getAllCharacters()) {
               if (player != null) {
                  player.removeEventRabbitPortal();
               }
            }
         }

         for (MapleCharacter playerx : this.getCharactersThreadsafe()) {
            if (playerx != null && !this.rewardGetUsers.contains(playerx.getId()) && playerx.getMapId() == this.getId()) {
               int quantity = 40;
               playerx.dropMessage(5, "월묘를 처치하여 단감 코인 " + quantity + "개를 획득했습니다.");
               playerx.gainItem(4310221, quantity, false, 0L, "추석 이벤트로 획득");
               playerx.warp(ServerConstants.TownMap);
               playerx.send(CField.addPopupSay(9062000, 5000, "월묘를 처치하여 #b단감 코인 " + quantity + "개#k를 획득했습니다.", ""));
               this.rewardGetUsers.add(playerx.getId());
            }
         }

         this.setRabbitSpawnedTime(0L);
      }
   }

   public long getRabbitSpawnedTime() {
      return this.rabbitSpawnedTime;
   }

   public void setRabbitSpawnedTime(long rabbitSpawnedTime) {
      this.rabbitSpawnedTime = rabbitSpawnedTime;
   }
}
