package network.game.processors;

import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import scripting.newscripting.ScriptManager;

public class StepUpHandler {
   public static void RunScript(PacketDecoder slea, MapleClient c) {
      MapleCharacter user = c.getPlayer();
      if (user != null) {
         if (user.getScriptThread() == null) {
            int step = slea.readInt();
            ScriptManager.runScript(c, "StepUp_" + step, MapleLifeFactory.getNPC(9001197));
         }
      }
   }

   public static void ArrivedCharacter(PacketDecoder slea, MapleClient c) {
      MapleCharacter user = c.getPlayer();
      if (user != null) {
         if (user.getOneInfoQuestInteger(501524, "state") > 1) {
            user.updateOneInfo(501524, "state", "0");
            user.updateOneInfo(501524, "step", String.valueOf(user.getOneInfoQuestInteger(501524, "step") + 1));
            PacketEncoder tq = new PacketEncoder();
            tq.writeShort(SendPacketOpcode.STEP_UP_RESET_CHARACTER.getValue());
            tq.write(1);
            user.send(tq.getPacket());
         }
      }
   }
}
