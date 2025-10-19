package objects.fields.child.fritto;

import constants.QuestExConstants;
import network.models.CField;
import objects.fields.Field;
import objects.users.MapleCharacter;

public class Field_StealDragonsEgg extends Field {
   private MapleCharacter player = null;
   private long endGameTime = 0L;
   private boolean endGame = false;

   public Field_StealDragonsEgg(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      if (this.player != null) {
         if (!this.endGame && this.endGameTime <= System.currentTimeMillis()) {
            this.player.send(CField.showEffect("killing/timeout"));
            this.player.setRegisterTransferField(993000601);
            this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
            this.endGame = true;
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.endGameTime = 0L;
      this.player = null;
      this.endGame = false;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      this.resetFully(false);
      super.onEnter(player);
      this.player = player;
      player.updateOneInfo(26022, "gameType", "2");
      player.updateOneInfo(15142, "Stage", "0");
      this.gameStart();
   }

   @Override
   public void onLeave(MapleCharacter player) {
      this.resetFully(false);
      player.setEnterRandomPortal(false);
      player.setRandomPortal(null);
      player.checkHasteQuestComplete(QuestExConstants.HasteEventRandomPortal.getQuestID());
      player.checkHiddenMissionComplete(QuestExConstants.SuddenMKRandomPortal.getQuestID());
   }

   public void gameStart() {
      this.player.send(CField.getStopwatch(30000));
      this.player.send(CField.environmentChange("PoloFritto/msg2", 20, 263));
      this.player.send(CField.startMapEffect("쉿! 이 둥지의 꼭대기에는 드래곤의 알이 숨겨져 있어. 꼭대기로 가는 길을 잘 찾아보라구!", 5120160, true, 10));
      this.endGameTime = System.currentTimeMillis() + 30000L;
   }
}
