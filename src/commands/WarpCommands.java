package commands;

import network.center.Center;
import network.game.GameServer;
import objects.fields.Field;
import objects.fields.Portal;
import objects.users.MapleCharacter;
import objects.users.MapleClient;

public class WarpCommands implements Command {
   @Override
   public void execute(MapleClient c, String[] splitted) throws Exception, IllegalCommandSyntaxException {
      GameServer cserv = c.getChannelServer();
      if (splitted[0].equals("워프")) {
         MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
         if (victim == null || c.getPlayer().getGMLevel() < victim.getGMLevel() || victim.inPVP() || c.getPlayer().inPVP()) {
            try {
               victim = c.getPlayer();
               int ch = Center.Find.findChannel(splitted[1]);
               if (ch < 0) {
                  Field target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                  if (target == null) {
                     c.getPlayer().dropMessage(6, "맵이 존재하지 않습니다.");
                  }

                  Portal targetPortal = null;
                  if (splitted.length > 2) {
                     try {
                        targetPortal = target.getPortal(Integer.parseInt(splitted[2]));
                     } catch (IndexOutOfBoundsException var11) {
                        c.getPlayer().dropMessage(5, "Invalid portal selected.");
                     } catch (NumberFormatException var12) {
                     }
                  }

                  if (targetPortal == null) {
                     targetPortal = target.getPortal(0);
                  }

                  c.getPlayer().changeMap(target, targetPortal);
               } else {
                  victim = GameServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
                  c.getPlayer().dropMessage(6, "채널을 변경하여 이동합니다.");
                  if (victim.getMapId() != c.getPlayer().getMapId()) {
                     Field mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
                     c.getPlayer().changeMap(mapp, mapp.findClosestPortal(victim.getTruePosition()));
                  }

                  c.getPlayer().changeChannel(ch);
               }
            } catch (Exception var13) {
               c.getPlayer().dropMessage(6, "캐릭터 이름을 제대로 입력해주세요.");
            }
         } else if (splitted.length == 2) {
            c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestSpawnpoint(victim.getTruePosition()));
         } else {
            Field targetx = GameServer.getInstance(c.getChannel()).getMapFactory().getMap(Integer.parseInt(splitted[2]));
            if (targetx == null) {
               c.getPlayer().dropMessage(6, "맵이 존재하지 않습니다.");
            }

            Portal targetPortalx = null;
            if (splitted.length > 3) {
               try {
                  targetPortalx = targetx.getPortal(Integer.parseInt(splitted[3]));
               } catch (IndexOutOfBoundsException var14) {
                  c.getPlayer().dropMessage(5, "Invalid portal selected.");
               } catch (NumberFormatException var15) {
               }
            }

            if (targetPortalx == null) {
               targetPortalx = targetx.getPortal(0);
            }

            victim.changeMap(targetx, targetPortalx);
         }
      } else if (splitted[0].equals("이리와")) {
         MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
         if (victim != null) {
            if (c.getPlayer().inPVP() || !c.getPlayer().isGM() && (victim.isInBlockedMap() || victim.isGM())) {
               c.getPlayer().dropMessage(5, "잠시후에 다시시도 해주세요.");
            }

            victim.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().findClosestPortal(c.getPlayer().getTruePosition()));
         } else {
            int ch = Center.Find.findChannel(splitted[1]);
            if (ch < 0) {
               c.getPlayer().dropMessage(5, "캐릭터를 찾을 수 없습니다.");
            }

            victim = GameServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null || victim.inPVP() || !c.getPlayer().isGM() && (victim.isInBlockedMap() || victim.isGM())) {
               c.getPlayer().dropMessage(5, "잠시후에 다시시도 해주세요.");
            }

            c.getPlayer().dropMessage(5, "채널을 변경하여 소환합니다.");
            victim.dropMessage(5, "채널을 변경하여 소환되었습니다.");
            if (victim.getMapId() != c.getPlayer().getMapId()) {
               Field mapp = victim.getClient().getChannelServer().getMapFactory().getMap(c.getPlayer().getMapId());
               victim.changeMap(mapp, mapp.findClosestPortal(c.getPlayer().getTruePosition()));
            }

            victim.changeChannel(c.getChannel());
         }
      } else if (splitted[0].equals("맵")) {
         Field targetxx = null;
         if (c.getPlayer().getEventInstance() != null) {
            targetxx = c.getPlayer().getEventInstance().getMapFactory().getMap(Integer.parseInt(splitted[1]));
         } else {
            targetxx = cserv.getMapFactory().getMap(Integer.parseInt(splitted[1]));
         }

         if (targetxx == null) {
            c.getPlayer().dropMessage(5, Integer.parseInt(splitted[1]) + "는 존재하지 않는 맵입니다.");
            return;
         }

         Portal targetPortalxx = null;
         if (splitted.length > 2) {
            try {
               targetPortalxx = targetxx.getPortal(Integer.parseInt(splitted[2]));
            } catch (IndexOutOfBoundsException var9) {
               c.getPlayer().dropMessage(5, "Invalid portal selected.");
            } catch (NumberFormatException var10) {
            }
         }

         if (targetPortalxx == null) {
            targetPortalxx = targetxx.getPortal(0);
         }

         c.getPlayer().changeMap(targetxx, targetPortalxx);
      }
   }

   @Override
   public CommandDefinition[] getDefinition() {
      return new CommandDefinition[]{
         new CommandDefinition("워프", "<플레이어이름> (<타겟맵ID>)", "자신을 해당 플레이어에게 워프합니다. 맵 ID가 입력될 경우 입력한 플레이어를 해당 맵으로 이동시킵니다.", 2),
         new CommandDefinition("이리와", "<플레이어이름>", "해당 플레이어를 자신의 위치로 소환합니다.", 2),
         new CommandDefinition("전체소환", "<전체소환>", "모든유저를 자신의 위치로 소환합니다.", 2),
         new CommandDefinition("맵", "<맵ID>", "해당 맵ID의 맵으로 이동합니다.", 2)
      };
   }
}
