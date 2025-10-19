package commands;

import database.DBConfig;
import database.DBConnection;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import network.models.CField;
import objects.fields.child.dreambreaker.DreamBreakerRank;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MapleNPC;
import objects.fields.gameobject.lifes.PlayerNPC;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import scripting.NPCScriptManager;

public class NPCSpawningCommands implements Command {
   @Override
   public void execute(MapleClient c, String[] splitted) throws Exception, IllegalCommandSyntaxException {
      if (splitted[0].equals("엔피시")) {
         int npcId = Integer.parseInt(splitted[1]);
         MapleNPC npc = MapleLifeFactory.getNPC(npcId);
         if (npc != null && !npc.getName().equals("MISSINGNO")) {
            npc.setPosition(c.getPlayer().getPosition());
            npc.setCy(c.getPlayer().getPosition().y);
            npc.setRx0(c.getPlayer().getPosition().x - 50);
            npc.setRx1(c.getPlayer().getPosition().x + 50);
            npc.setFh(c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
            npc.setCustom(true);
            c.getPlayer().getMap().addMapObject(npc);
            c.getPlayer().getMap().broadcastMessage(CField.NPCPacket.spawnNPC(npc, true));
         } else {
            c.getPlayer().dropMessage(6, "WZ데이터에 없는 엔피시코드를 입력하였습니다.");
         }
      } else if (splitted[0].equals("미니게임")) {
         if (DBConfig.isGanglim) {
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            NPCScriptManager.getInstance().start(c, 9062462);
         }
      } else if (splitted[0].equals("엔피시삭제")) {
         c.getPlayer().getMap().resetNPCs();
      } else if (splitted[0].equals("현재위치")) {
         Point pos = c.getPlayer().getPosition();
         c.getPlayer()
            .dropMessage(
               6,
               "CY: "
                  + (pos.y + 2)
                  + " | RX0: "
                  + (pos.x - 50)
                  + " | RX1: "
                  + (pos.x + 50)
                  + " | FH: "
                  + c.getPlayer().getMap().getFootholds().findBelow(pos).getId()
            );
      } else if (splitted[0].equals("드브엔피시리셋")) {
         try {
            c.getPlayer().dropMessage(6, "드림브레이커 랭킹 엔피시를 다시 불러옵니다.");
            List<String> rankers = new ArrayList<>();

            for (int i = 1; i <= 5; i++) {
               String name = DreamBreakerRank.getRanker(i);
               if (name.isEmpty()) {
                  break;
               }

               rankers.add(name);
            }

            if (!rankers.isEmpty()) {
            }

            c.getPlayer().dropMessage(6, "완료!");
         } catch (Exception var18) {
            c.getPlayer().dropMessage(6, "플레이어엔피시를 제작하는데 실패하였습니다. : " + var18.getMessage());
            var18.printStackTrace();
         }
      } else if (splitted[0].equals("플레이어엔피시")) {
         try {
            c.getPlayer().dropMessage(6, "플레이어엔피시를 제작중입니다.");
            MapleCharacter chhr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chhr == null) {
               c.getPlayer().dropMessage(6, splitted[1] + "님은 온라인이 아니거나, 존재하지 않는 닉네임입니다. 상태를 확인하고 다시 시도해주세요.");
            }

            PlayerNPC npc = new PlayerNPC(chhr, Integer.parseInt(splitted[2]), c.getPlayer().getMap(), c.getPlayer());
            npc.addToServer();
            c.getPlayer().dropMessage(6, "완료!");
         } catch (Exception var12) {
            c.getPlayer().dropMessage(6, "플레이어엔피시를 제작하는데 실패하였습니다. : " + var12.getMessage());
            var12.printStackTrace();
         }
      } else if (splitted[0].equals("고정엔피시")) {
         int npcId = Integer.parseInt(splitted[1]);
         MapleNPC npc = MapleLifeFactory.getNPC(npcId);
         if (npc == null || npc.getName().equals("MISSINGNO")) {
            c.getPlayer().dropMessage(6, "WZ에 존재하지 않는 NPC를 입력했습니다.");
            return;
         }

         npc.setPosition(c.getPlayer().getPosition());
         npc.setCy(c.getPlayer().getPosition().y);
         npc.setRx0(c.getPlayer().getPosition().x - 50);
         npc.setRx1(c.getPlayer().getPosition().x + 50);
         npc.setFh(c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
         c.getPlayer().getMap().addMapObject(npc);
         c.getPlayer().getMap().broadcastMessage(CField.NPCPacket.spawnNPC(npc, true));

         try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
               "INSERT INTO `spawn`(`lifeid`, `rx0`, `rx1`, `cy`, `fh`, `type`, `dir`, `mapid`, `mobTime`) VALUES (? ,? ,? ,? ,? ,? ,? ,? ,?)"
            );
         ) {
            ps.setInt(1, npcId);
            ps.setInt(2, c.getPlayer().getPosition().x);
            ps.setInt(3, c.getPlayer().getPosition().x + 100);
            ps.setInt(4, c.getPlayer().getPosition().y);
            ps.setInt(5, c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
            ps.setString(6, "n");
            ps.setInt(7, c.getPlayer().getFacingDirection() == 1 ? 0 : 1);
            ps.setInt(8, c.getPlayer().getMapId());
            ps.setInt(9, 0);
            ps.executeUpdate();
         } catch (Exception var17) {
            System.err.println("[오류] 엔피시를 고정 등록하는데 실패했습니다.");
            var17.printStackTrace();
         }
      } else if (splitted[0].equals("고정몹")) {
         int mobId = Integer.parseInt(splitted[1]);
         MapleMonster mob = MapleLifeFactory.getMonster(mobId);
         c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());

         try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO `spawn`(`lifeid`, `rx0`, `rx1`, `cy`, `fh`, `type`, `dir`, `mapid`, `mobTime`) VALUES (? ,? ,? ,? ,? ,? ,? ,? ,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, mobId);
            ps.setInt(2, c.getPlayer().getPosition().x - 50);
            ps.setInt(3, c.getPlayer().getPosition().x + 50);
            ps.setInt(4, c.getPlayer().getPosition().y);
            ps.setInt(5, c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
            ps.setString(6, "m");
            ps.setInt(7, c.getPlayer().getFacingDirection() == 1 ? 0 : 1);
            ps.setInt(8, c.getPlayer().getMapId());
            ps.setInt(9, 0);
            ps.executeUpdate();
            ps.close();
         } catch (Exception var14) {
            System.err.println("[오류] 엔피시를 고정 등록하는데 실패했습니다.");
            var14.printStackTrace();
         }
      }
   }

   @Override
   public CommandDefinition[] getDefinition() {
      return new CommandDefinition[]{
         new CommandDefinition("미니게임", "", "미니게임 NPC를 소환합니다", 5),
         new CommandDefinition("엔피시", "<엔피시ID>", "현재 위치에 해당 ID의 엔피시를 소환합니다.", 5),
         new CommandDefinition("엔피시삭제", "", "현재 맵에서 명령어로 소환된 모든 NPC를 제거합니다.", 5),
         new CommandDefinition("플레이어엔피시", "<스크립트ID>", "현재 맵에 현재 플레이어를 엔피시로 등록합니다.", 5),
         new CommandDefinition("드브엔피시리셋", "", "드림브레이커 엔피시를 리로드합니다.", 5),
         new CommandDefinition("고정엔피시", "<엔피시ID>", "현재 맵의 현재 위치에 해당 엔피시를 고정으로 등록합니다.", 5),
         new CommandDefinition("고정몹", "<몹ID>", "현재 맵의 현재 위치에 해당 몬스터를 고정으로 등록합니다.", 5),
         new CommandDefinition("현재위치", "", "현재 X,Y 값 등 좌표를 출력합니다.", 2)
      };
   }
}
