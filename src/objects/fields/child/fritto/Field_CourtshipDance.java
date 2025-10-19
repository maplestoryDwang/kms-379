package objects.fields.child.fritto;

import constants.QuestExConstants;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.fields.Field;
import objects.users.MapleCharacter;

public class Field_CourtshipDance extends Field {
   private long startGameTime = 0L;
   private boolean startGame = false;
   private long endGameTime = 0L;
   private boolean endGame = false;
   private boolean clear = false;
   private boolean displayClear = false;
   private long displayStartGameTime = 0L;
   private boolean displayStartGame = false;
   private MapleCharacter player = null;
   private CourtshipCommand command = null;

   public Field_CourtshipDance(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      if (this.player != null) {
         if (this.clear && !this.displayClear) {
            this.player.send(CField.showEffect("killing/clear"));
            this.player.setRegisterTransferField(993000601);
            this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
            this.displayClear = true;
         } else {
            if (this.endGameTime == 0L) {
               this.endGameTime = System.currentTimeMillis() + 66000L;
            }

            if (!this.endGame && this.endGameTime <= System.currentTimeMillis()) {
               this.player.send(CField.showEffect("killing/timeout"));
               this.player.setRegisterTransferField(993000601);
               this.player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
               this.endGame = true;
            } else {
               if (!this.startGame) {
                  if (this.startGameTime == 0L) {
                     this.player.send(CField.startMapEffect("달걀을 훔치려면 먼저 닭들은 속여야 해! 자, 나를 따라 구애의 춤을 춰!", 5120160, true, 10));
                     this.player.send(CField.environmentChange("PoloFritto/msg3", 20, 263));
                     this.startGameTime = System.currentTimeMillis() + 6000L;
                  }

                  if (this.displayStartGameTime == 0L) {
                     this.displayStartGameTime = System.currentTimeMillis() + 3000L;
                  }

                  if (!this.displayStartGame && this.displayStartGameTime <= System.currentTimeMillis()) {
                     this.broadcastMessage(CField.showEffect("defense/count"));
                     this.broadcastMessage(CField.environmentChange("killing/first/start", 17, 1000));
                     this.displayStartGame = true;
                  }

                  if (this.startGameTime <= System.currentTimeMillis()) {
                     this.command = new CourtshipCommand();
                     this.command.generateCommands();
                     this.player.send(CField.getStopwatch(60000));
                     this.endGameTime = System.currentTimeMillis() + 60000L;
                     this.sendCourtshipState(2);
                     this.sendCourtshipCommand();
                     this.startGame = true;
                  }
               }
            }
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.player = null;
      this.command = null;
      this.endGameTime = 0L;
      this.endGame = false;
      this.startGameTime = 0L;
      this.startGame = false;
      this.displayStartGameTime = 0L;
      this.displayStartGame = false;
      this.displayClear = false;
      this.setClear(false);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      this.resetFully(false);
      super.onEnter(player);
      this.player = player;
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_IN_GAME_DIRECTION_MODE.getValue());
      packet.writeInt(1);
      player.send(packet.getPacket());
      player.send(CField.UIPacket.hideChar(true));
      player.updateOneInfo(26022, "gameType", "3");
      player.updateOneInfo(15143, "success", "0");
   }

   @Override
   public void onLeave(MapleCharacter player) {
      this.resetFully(false);
      player.send(CField.UIPacket.hideChar(false));
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_IN_GAME_DIRECTION_MODE.getValue());
      packet.write(0);
      packet.write(1);
      player.send(packet.getPacket());
      player.setEnterRandomPortal(false);
      player.setRandomPortal(null);
      player.checkHasteQuestComplete(QuestExConstants.HasteEventRandomPortal.getQuestID());
      player.checkHiddenMissionComplete(QuestExConstants.SuddenMKRandomPortal.getQuestID());
   }

   public void sendCourtshipState(int state) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.COURTSHIP_STATE.getValue());
      packet.writeInt(state);
      this.player.send(packet.getPacket());
   }

   public void sendCourtshipCommand() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.COURTSHIP_COMMAND.getValue());
      this.command.encode(packet);
      this.player.send(packet.getPacket());
   }

   public boolean isClear() {
      return this.clear;
   }

   public void setClear(boolean clear) {
      this.clear = clear;
   }

   public void nextStep() {
      int stage = this.player.getOneInfoQuestInteger(15143, "success");
      this.player.updateOneInfo(15143, "success", String.valueOf(++stage));
      if (stage >= 10) {
         this.clear = true;
      }
   }
}
