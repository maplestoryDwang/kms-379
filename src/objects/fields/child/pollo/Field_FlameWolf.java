package objects.fields.child.pollo;

import constants.QuestExConstants;
import java.awt.Point;
import network.models.CField;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;

public class Field_FlameWolf extends Field {
   public Field_FlameWolf(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      for (MapleCharacter player : this.getCharactersThreadsafe()) {
         if (player.getEnterFlameWolfTime() != 0L && System.currentTimeMillis() - player.getEnterFlameWolfTime() >= 30000L) {
            player.updateOneInfo(15142, "kill_wolf", "0");
            player.updateOneInfo(15142, "wolf_damage", String.valueOf(player.getFlameWolfTotalDamage()));
            player.setEnterFlameWolfTime(0L);
            player.setRegisterTransferField(993000600);
            player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      this.resetFully(false);
      super.onEnter(player);
      player.send(CField.startMapEffect("불꽃늑대를 처치할 용사가 늘었군. 어서 녀석을 공격해! 머무를 수 있는 시간은 30초 뿐이야!", 5120159, true, 30));
      player.send(CField.getClock(30));
      player.setEnterFlameWolfTime(System.currentTimeMillis());
      if (this.getAllMonstersThreadsafe().size() <= 0) {
         MapleMonster mob = MapleLifeFactory.getMonster(9101078);
         if (mob != null) {
            this.spawnMonsterOnGroundBelow(mob, new Point(51, 352));
         }
      }

      player.updateOneInfo(15142, "gameType", "8");
   }

   @Override
   public void onLeave(MapleCharacter player) {
      player.setEnterRandomPortal(false);
      player.setRandomPortal(null);
      player.setEnterFlameWolfTime(0L);
      player.checkHasteQuestComplete(QuestExConstants.HasteEventFireWolf.getQuestID());
      player.checkHiddenMissionComplete(QuestExConstants.SuddenMKFireWolf.getQuestID());
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      for (MapleCharacter player : this.getCharactersThreadsafe()) {
         player.updateOneInfo(15142, "gameType", "8");
         player.updateOneInfo(15142, "wolf_damage", String.valueOf(player.getFlameWolfTotalDamage()));
         player.updateOneInfo(15142, "kill_wolf", "1");
         player.setEnterFlameWolfTime(0L);
         player.setRegisterTransferField(993000600);
         player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
      }
   }
}
