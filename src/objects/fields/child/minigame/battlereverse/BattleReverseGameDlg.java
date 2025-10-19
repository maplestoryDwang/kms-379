package objects.fields.child.minigame.battlereverse;

import constants.ServerConstants;
import java.util.List;
import objects.fields.Field;
import objects.users.MapleCharacter;
import objects.utils.FileoutputUtil;

public class BattleReverseGameDlg {
   private BattleReverseGameInfo gameInfo = new BattleReverseGameInfo();
   private Field_BattleReverse field = null;

   public BattleReverseGameDlg(Field field) {
      this.field = (Field_BattleReverse)field;
   }

   public void init(List<MapleCharacter> players) {
      if (players.size() >= 2) {
         int idx = 0;
         BattleReverseGameInfo gameInfo = new BattleReverseGameInfo();

         for (MapleCharacter player : this.field.getCharactersThreadsafe()) {
            if (idx >= 2) {
               player.setRegisterTransferField(15);
               player.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
            } else {
               gameInfo.setTeamByChr(player);
               player.setMiniGameTeam(gameInfo.getTeamByChr(player));
               this.gameInfo = gameInfo;
            }
         }
      } else {
         for (MapleCharacter playerx : this.field.getCharactersThreadsafe()) {
            playerx.dropMessage(5, "게임에 필요한 인원수가 부족하여 마을로 이동됩니다.");
            playerx.setRegisterTransferField(ServerConstants.TownMap);
            playerx.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
         }
      }
   }

   public void updateGameInfo() {
      if (this.gameInfo.getCharacter(0) != null && this.gameInfo.getCharacter(1) != null) {
         for (MapleCharacter chr : this.field.getCharacters()) {
            chr.send(BattleReversePacket.OpenBattleReverseUI(this.gameInfo, chr));
         }
      } else {
         FileoutputUtil.log("Log_MiniGame_Except.rtf", "캐릭터가 null인 상태로 게임시작 시도");

         for (MapleCharacter chr : this.field.getCharacters()) {
            chr.dropMessage(5, "오류가 발생하여 게임이 시작되지 않았습니다. 마을로 이동됩니다.");
            chr.setRegisterTransferField(ServerConstants.TownMap);
            chr.setRegisterTransferFieldTime(System.currentTimeMillis() + 1000L);
         }
      }
   }

   public BattleReverseGameInfo getGameInfo() {
      return this.gameInfo;
   }
}
