package commands;

import network.models.CField;
import objects.fields.Field;
import objects.fields.HundredBingo;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.utils.Timer;

public class FieldCommand implements Command {
   @Override
   public void execute(final MapleClient c, String[] splitted) throws Exception, IllegalCommandSyntaxException {
      if (splitted[0].equals("빙고시작") && splitted.length > 1) {
         String var3 = splitted[1];
         switch (var3) {
            case "0":
               if (c.getPlayer().getMapId() != 922290000) {
                  return;
               }

               c.getPlayer().getMap().broadcastMessage(CField.getClock(30));
               Timer.MapTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     for (MapleCharacter chr : c.getPlayer().getMap().getCharacters()) {
                        if (chr != null) {
                           Field map = c.getChannelServer().getMapFactory().getMap(922290100);
                           chr.changeMap(map, map.getPortal(0));
                        }
                     }
                  }
               }, 30000L);
               Timer.MapTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     if (c.getPlayer().getMapId() == 922290100) {
                        HundredBingo bingo = new HundredBingo(c.getPlayer().getMap().getCharacters());

                        for (MapleCharacter chr : c.getPlayer().getMap().getCharacters()) {
                           if (chr != null) {
                              chr.setBingoGame(bingo);
                              chr.getClient().getSession().writeAndFlush(CField.musicChange("BgmEvent/dolphin_night"));
                              chr.getClient().getSession().writeAndFlush(CField.playSE("multiBingo/start"));
                              chr.getClient().getSession().writeAndFlush(CField.MapEff("Gstar/start"));
                           }
                        }
                     }
                  }
               }, 40000L);
               Timer.MapTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     c.getPlayer().getBingoGame().StartGame();
                  }
               }, 42000L);
               break;
            case "1":
               c.getPlayer().getBingoGame().StopBingo();
         }
      }
   }

   @Override
   public CommandDefinition[] getDefinition() {
      return new CommandDefinition[]{new CommandDefinition("빙고시작", "0 or 1", "빙고 게임을 시작합니다.", 6)};
   }
}
