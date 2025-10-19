package objects.utils;

import constants.AutoHottimeManager;
import constants.HottimeItemManager;
import constants.ServerConstants;
import constants.devtempConstants.MapleFishing;
import java.util.Scanner;
import network.RecvPacketOpcode;
import network.SendPacketOpcode;
import network.ShutdownServer;
import network.center.Center;
import network.discordbot.DiscordBotHandler;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import objects.context.guild.Guild;
import objects.fields.child.etc.DamageMeasurementRank;
import objects.fields.gameobject.lifes.MapleMonsterInformationProvider;
import objects.users.MapleCharacter;
import objects.users.extra.ExtraAbilityFactory;
import scripting.newscripting.ScriptManager;

public class CMDCommand {
   private static CMDCommand instance = new CMDCommand();
   public static String target = "";
   private static int a;

   public static void main() {
      Scanner scan = new Scanner(System.in);

      while (true) {
         System.out.print("Input Command : ");
         target = scan.nextLine();
         String[] command = target.split(" ");
         String var2 = command[0];
         switch (var2) {
            case "낚시리셋":
            case "fr":
               MapleFishing.Load();
               break;
            case "핫타임아이템리셋":
            case "hir":
               HottimeItemManager.loadHottimeItem();
               break;
            case "피버리셋":
            case "feverreset":
               ServerConstants.expFeverRate = 1.0;
               ServerConstants.dropFeverRate = 1.0;
               ServerConstants.mesoFeverRate = 1.0;
               Center.cancelAutoFeverTask();
               AutoHottimeManager.loadAutoHottime();
               Center.registerAutoFever();
               break;
            case "전투력리셋":
               DamageMeasurementRank.resetRank();
               System.out.println("전투력 랭킹이 초기화되었습니다.");
               break;
            case "전투력로드":
               DamageMeasurementRank.loadRank();
               System.out.println("전투력 랭킹을 로드했습니다.");
               break;
            case "노블강제정산":
               for (Guild g : Center.Guild.getGuilds()) {
                  g.nobleSPAdjustmentF();
               }
               break;
            case "블라썸저장":
               if (Center.sunShineStorage.save()) {
                  System.out.println("성공적으로 저장되었습니다.");
               } else {
                  System.out.println("저장 실패 error");
               }
               break;
            case "스크립트리셋":
               ScriptManager.resetScript(null);
               break;
            case "봇테스트":
               DiscordBotHandler.requestSendTelegramWithChatID("봇에서 보내는 테스트입니다. 1", -671926475L);
               DiscordBotHandler.requestSendTelegramWithChatID("봇에서 보내는 테스트입니다. 2", -627738806L);
               DiscordBotHandler.requestSendTelegramWithChatID("봇에서 보내는 테스트입니다. 3", -1001603835720L);
               break;
            case "도움말":
               System.out.println("<  CMD커맨드 도움말 >");
               System.out.println("[명령어 목록] :: \r\n");
               System.out.println("<공지> - 공지사항을 보냅니다.");
               System.out.println("<모두종료> - 서버에 있는 유저들을 모두 종료시킵니다.");
               System.out.println("<고상저장> - 서버에 열려있는 고용상인을 모두 저장합니다.");
               System.out.println("<임명> - 플레이어에게 GM권한 레벨을 부여합니다.");
               System.out.println("<패킷> - 서버에 센드 패킷 스트링을 보냅니다.");
               System.out.println("<OR> - 옵코드를 리로딩합니다.");
               System.out.println("<서버종료> - 서버를 안전하게 저장후 종료합니다.");
               break;
            case "공지":
               for (GameServer ch : GameServer.getAllInstances()) {
                  for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
                     chr.dropMessage(1, "[공지사항]\r\n" + StringUtil.joinStringFrom(command, 1));
                  }
               }
               break;
            case "모두종료":
               for (GameServer ch : GameServer.getAllInstances()) {
                  ch.getPlayerStorage().disconnectAll();
               }

               System.out.println("서버에 있는 유저들을 종료시켰습니다.");
               break;
            case "고상저장":
               for (GameServer ch : GameServer.getAllInstances()) {
                  ch.closeAllMerchant();
               }

               System.out.println("고용상인 저장 완료");
               break;
            case "임명":
               a = 0;

               for (GameServer cserv : GameServer.getAllInstances()) {
                  MapleCharacter player = null;
                  player = cserv.getPlayerStorage().getCharacterByName(command[1]);
                  if (player != null) {
                     byte number = Byte.parseByte(command[2]);
                     player.getClient().getSession().writeAndFlush(CWvsContext.getScriptProgressMessage("해당 플레이어가 GM " + command[2] + "레벨이 되었습니다."));
                     System.out.println(command[1] + " 플레이어를 GM레벨 " + command[2] + "(으)로 설정하였습니다.");
                     player.setGMLevel(number);
                     a = 1;
                  } else if (player == null && a == 0) {
                     System.out.println(command[1] + " 플레이어를 찾지 못하였습니다.");
                     a = 1;
                  }
               }
               break;
            case "패킷":
               PacketEncoder mplew = new PacketEncoder();
               mplew.encodeBuffer(HexTool.getByteArrayFromHexString(StringUtil.joinStringFrom(command, 1)));

               for (GameServer ch : GameServer.getAllInstances()) {
                  for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
                     chr.getClient().getSession().writeAndFlush(mplew.getPacket());
                  }
               }

               System.out.println(StringUtil.joinStringFrom(command, 1));
               break;
            case "서버종료":
               ShutdownServer.getInstance().run();
               ShutdownServer.getInstance().run();
               break;
            case "ear":
               ExtraAbilityFactory.loadData();
               break;
            case "or":
               RecvPacketOpcode.reloadValues();
               SendPacketOpcode.reloadValues();
               System.out.print("옵코드 재설정이 완료되었습니다.");
               break;
            case "enddir":
               for (GameServer cserv : GameServer.getAllInstances()) {
                  cserv.broadcastPacket(CField.UIPacket.IntroLock(false));
               }
               break;
            case "endingamedir":
               for (GameServer cserv : GameServer.getAllInstances()) {
                  cserv.broadcastPacket(CField.DirectionPacket.IntroEnableUI(1));
               }
               break;
            case "드롭리셋":
            case "dr":
               MapleMonsterInformationProvider.getInstance().clearDrops();
         }
      }
   }

   public static String converToDecimalFromHex(String hex) {
      String decimal = "";
      hex = hex.trim();

      for (int i = 0; i < hex.length(); i += 2) {
         String tmp = hex.substring(i, i + 2);
         long val = Long.parseLong(tmp, 16);
         decimal = decimal + val;
         decimal = decimal + ",";
      }

      return decimal;
   }
}
