package commands;

import constants.devtempConstants.MapleClientCRC;
import constants.devtempConstants.MapleDailyGift;
import constants.devtempConstants.MapleDimensionalMirror;
import constants.devtempConstants.MapleEventList;
import constants.devtempConstants.MapleFishing;
import constants.devtempConstants.MapleGoldenChariot;
import constants.devtempConstants.MapleMonsterCustomHP;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.center.WeeklyItemManager;
import network.game.GameServer;
import objects.fields.child.etc.DamageMeasurementRank;
import objects.fields.gameobject.lifes.MapleMonsterInformationProvider;
import objects.item.rewards.RoyalStyle;
import objects.shop.MapleShopFactory;
import objects.users.MapleClient;
import objects.users.skills.SkillFactory;
import scripting.PortalScriptManager;
import scripting.ReactorScriptManager;

public class ReloadingCommands implements Command {
   @Override
   public void execute(MapleClient c, String[] splitted) throws Exception, IllegalCommandSyntaxException {
      if (splitted[0].equals("옵코드리셋")) {
         SendPacketOpcode.reloadValues();
         RecvPacketOpcode.reloadValues();
         c.getPlayer().dropMessage(5, "[시스템] 옵코드 리셋이 완료되었습니다.");
      } else if (splitted[0].equals("디버그옵코드")) {
         for (SendPacketOpcode send : SendPacketOpcode.values()) {
            if (send.name().equals(splitted[1])) {
               send.setValue(Short.parseShort(splitted[2]));
               c.getPlayer().dropMessage(5, "[디버그 옵코드] " + send.name() + " : " + send.getValue());
               break;
            }
         }
      } else if (splitted[0].equals("드롭리셋")) {
         MapleMonsterInformationProvider.getInstance().clearDrops();
         ReactorScriptManager.getInstance().clearDrops();
         c.getPlayer().dropMessage(5, "[시스템] 드롭 리셋이 완료되었습니다.");
      } else if (splitted[0].equals("포탈리셋")) {
         PortalScriptManager.getInstance().clearScripts();
         c.getPlayer().dropMessage(5, "[시스템] 포탈 리셋이 완료되었습니다.");
      } else if (splitted[0].equals("상점리셋")) {
         MapleShopFactory.getInstance().clear();
         c.getPlayer().dropMessage(5, "[시스템] 상점 리셋이 완료되었습니다.");
      } else if (splitted[0].equals("이벤트리셋")) {
         for (GameServer instance : GameServer.getAllInstances()) {
            instance.reloadEvents();
         }

         c.getPlayer().dropMessage(5, "[시스템] 이벤트 리셋이 완료되었습니다.");
      } else if (splitted[0].equals("스킬리셋")) {
         SkillFactory.load();
         c.getPlayer().dropMessage(5, "[시스템] 스킬 리셋이 완료되었습니다.");
      } else if (splitted[0].equals("황금상자리셋")) {
         WeeklyItemManager.loadWeeklyItems();
         c.getPlayer().dropMessage(5, "[시스템] 페어리 브로의 황금상자 리셋이 완료되었습니다.");
      } else if (splitted[0].equals("황금상자저장")) {
         WeeklyItemManager.saveWeeklyItems();
         c.getPlayer().dropMessage(5, "[시스템] 페어리 브로의 황금상자가 DB에 저장되었습니다.");
      } else if (splitted[0].equals("골드애플리셋")) {
         RoyalStyle.resetGoldApple();
         c.getPlayer().dropMessage(5, "[시스템] 골드애플 리셋이 완료되었습니다.");
      } else if (splitted[0].equals("체력리로드")) {
         MapleMonsterCustomHP.Load();
         c.getPlayer().dropMessage(5, "[시스템] Excel 체력 리스트가 리로드 되었습니다");
      } else if (splitted[0].equals("데일리아이템리로드")) {
         MapleDailyGift.Load();
         c.getPlayer().dropMessage(5, "[시스템] Excel 데일리아이템 리스트가 리로드 되었습니다");
      } else if (splitted[0].equals("디멘션미러리로드")) {
         MapleDimensionalMirror.Load();
         c.getPlayer().dropMessage(5, "[시스템] Excel 디멘션미러 리스트가 리로드 되었습니다");
      } else if (splitted[0].equals("이벤트리스트리로드")) {
         MapleEventList.Load();
         c.getPlayer().dropMessage(5, "[시스템] Excel 이벤트 리스트가 리로드 되었습니다");
      } else if (splitted[0].equals("낚시리로드")) {
         MapleFishing.Load();
         c.getPlayer().dropMessage(5, "[시스템] Excel 낚시 리스트가 리로드 되었습니다");
      } else if (splitted[0].equals("황금마차리로드")) {
         MapleGoldenChariot.Load();
         c.getPlayer().dropMessage(5, "[시스템] Excel 황금마차 리스트가 리로드 되었습니다");
      } else if (splitted[0].equals("위젯CRC리로드")) {
         MapleClientCRC.Load();
         c.getPlayer().dropMessage(5, "[시스템] Excel WZ CRC 리스트가 리로드 되었습니다");
      } else if (splitted[0].equals("전투력측정리로드")) {
         DamageMeasurementRank.loadRank();
         c.getPlayer().dropMessage(5, "[시스템] 전투력측정 랭킹이 리로드되었습니다.");
      }
   }

   @Override
   public CommandDefinition[] getDefinition() {
      return new CommandDefinition[]{
         new CommandDefinition("옵코드리셋", "", "리시브와 센드 옵코드를 properties에서 다시 불러옵니다.", 6),
         new CommandDefinition("포탈리셋", "", "캐시된 포탈스크립트를 비우고 js를 다시 불러옵니다.", 6),
         new CommandDefinition("드롭리셋", "", "캐시된 드롭 데이터를 비우고 DB에서 다시 불러옵니다.", 6),
         new CommandDefinition("상점리셋", "", "캐시된 상점 데이터를 비우고 DB에서 다시 불러옵니다.", 6),
         new CommandDefinition("이벤트리셋", "", "캐시되고 실행중인 이벤트를 모두 종료하고 js를 다시 불러옵니다.", 6),
         new CommandDefinition("스킬리셋", "", "캐시된 스킬을 비우고 wz에서 다시 불러옵니다.", 6),
         new CommandDefinition("황금상자리셋", "", "캐시된 황금상자 데이터를 비우고 DB에서 다시 불러옵니다.", 6),
         new CommandDefinition("골드애플리셋", "", "골드애플 아이템목록을 다시불러옵니다.", 6),
         new CommandDefinition("황금상자저장", "", "서버에 있는 황금상자 데이터를 DB에 저장합니다.", 6),
         new CommandDefinition("디버그옵코드", "", "", 6),
         new CommandDefinition("체력리로드", "", "", 6),
         new CommandDefinition("데일리아이템리로드", "", "", 6),
         new CommandDefinition("디멘션미러리로드", "", "", 6),
         new CommandDefinition("이벤트리스트리로드", "", "", 6),
         new CommandDefinition("낚시리로드", "", "", 6),
         new CommandDefinition("황금마차리로드", "", "", 6),
         new CommandDefinition("위젯CRC리로드", "", "", 6),
         new CommandDefinition("전투력측정리로드", "", "", 6)
      };
   }
}
