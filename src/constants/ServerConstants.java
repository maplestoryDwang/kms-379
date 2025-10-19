package constants;

import constants.devtempConstants.MapleDimensionalMirror;
import database.DBConfig;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import objects.users.MapleCharacter;
import objects.utils.Pair;
import objects.utils.Properties;
import objects.utils.Table;
import objects.utils.Triple;

public class ServerConstants {
   public static boolean workingSave = false;
   public static boolean workingReboot = false;
   public static AtomicInteger currentSaveCount = new AtomicInteger(0);
   public static AtomicInteger totalSaveCount = new AtomicInteger(0);
   public static final short MAPLE_VERSION = 379;
   public static final byte MAPLE_PATCH = 8;
   public static final byte MAPLE_LOCALE = 1;
   public static boolean Use_Fixed_IV = false;
   public static boolean Use_Localhost = false;
   public static boolean DEBUG_RECEIVE = false;
   public static boolean DEBUG_SEND = false;
   public static boolean INGAME_TEST_SEND_BLOCK = false;
   public static boolean INGAME_TEST_RECV_BLOCK = false;
   public static boolean SET_TEST_DAMAGE = false;
   public static long TEST_DAMAGE = 0L;
   public static boolean DEBUG_DAMAGE = false;
   public static boolean SAVE_SEND_PACKET = false;
   public static final List<Pair<String, String>> dungeonList = new ArrayList<>();
   public static final String remoteIP = "175.207.0.33";
   public static final String[] guildMedalList = new String[]{"길드이름"};
   public static final int[] guildMedalItemID = new int[]{1142656};
   public static final int[] singleMedalItemID = new int[]{1143037};
   public static final int[] donatorMedalItemID = new int[]{1142172};
   public static boolean tempServer = false;
   public static double connectedRate = 1.9;
   public static int StartMap = 180000100;
   public static int TownMap = DBConfig.isGanglim ? 101 : 993210000;
   public static int TownMap2 = 111;
   public static int TownMap3 = 111;
   public static boolean disableBanMap = true;
   public static String serverName = DBConfig.isGanglim ? "강림" : "강림";
   public static byte[] origCRCBytes = null;
   public static List<Integer> fuckNegativeArray = new ArrayList<>();
   public static boolean useTempCharacterName = false;
   public static String tempCharacterName = "임시";
   public static int JuhunFever;
   public static boolean JangGangFever;
   public static String WORLD_UI;
   public static String serverMessage;
   public static boolean ChangeMapUI;
   public static boolean useDailyGift;
   public static MapleDimensionalMirror dimensionalMirror;
   public static DailyEventType dailyEventType;
   public static int goldMapleDropRate;
   public static boolean useExpRateByLevel;
   public static int[] expRateChangeLevels;
   public static int[] expRateByLevels;
   public static double expFeverRate;
   public static double mesoFeverRate;
   public static double dropFeverRate;
   public static boolean useAdminClient;
   public static boolean blockedEnterAuction;
   public static String expHottimeStartTime;
   public static String expHottimeEndTime;
   public static String juhunHottimeStartTime;
   public static String juhunHottimeEndTime;
   public static int cashPlusRate;
   public static boolean royalGuildSave;
   public static long guildSaveTime;
   public static boolean enableCRCBin;
   public static double expHottimeRate;
   public static double mesoHottimeRate;
   public static double currentHottimeRate;
   public static double currentMesoHottimeRate;
   public static boolean useChairFishing;
   public static int chairFishingMapID;
   public static boolean useAdminClientUpgrade;
   public static MapleCharacter top1Ranker;
   public static String cacheFaceHair;
   public static List<Integer> accountShareQuestEx;
   public static int[] monsterCollectionQuestEx;
   public static TimeScheduleEntry timeScheduleEntry;
   public static boolean useCriticalDll;
   public static final Map<String, Triple<String, String, String>> authlist;
   public static final Map<String, Triple<String, String, String>> authlist2;
   public static boolean ConnectorSetting;
   public static boolean ConnectorSetting1;
   public static boolean ConnectorLog;
   public static boolean useAchievement;

   public static boolean isAccountShareQuestEx(int questID) {
      boolean ret = false;

      for (int qid : accountShareQuestEx) {
         if (qid == questID) {
            ret = true;
            break;
         }
      }

      return ret;
   }

   public static boolean isMonsterCollectionQuestEx(int questID) {
      boolean ret = false;

      for (int qid : monsterCollectionQuestEx) {
         if (qid == questID) {
            ret = true;
            break;
         }
      }

      return ret;
   }

   public static final byte[] getGatewayIP() {
      String[] splitted = "175.207.0.33".split("\\.");
      byte[] ret = new byte[4];

      for (int i = 0; i < 4; i++) {
         Integer b = Integer.parseInt(splitted[i]);
         ret[i] = b.byteValue();
      }

      return ret;
   }

   public static final byte Class_Bonus_EXP(int job) {
      switch (job) {
         case 501:
         case 530:
         case 531:
         case 532:
         case 800:
         case 900:
         case 910:
         case 2300:
         case 2310:
         case 2311:
         case 2312:
         case 3100:
         case 3110:
         case 3111:
         case 3112:
            return 10;
         default:
            return 0;
      }
   }

   public static void loadMirrorDungeon() {
      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim" : "data/Jin", "MirrorDungeon.data");
      int count = 0;

      for (Table children : table.list()) {
         String path = children.getProperty("Path");
         String desc = children.getProperty("Desc");
         dungeonList.add(new Pair<>(path, desc));
         count++;
      }

      System.out.println("총 " + count + "개의 트레이드 시스템 데이터를 불러왔습니다.");
   }

   static java.util.Properties getDefaultProperties() throws FileNotFoundException, IOException {
      java.util.Properties props = new java.util.Properties();
      FileInputStream fileInputStream = new FileInputStream(DBConfig.isGanglim ? "settings_ganglim.properties" : "settings_jin.properties");
      props.load(fileInputStream);
      fileInputStream.close();
      return props;
   }

   public static boolean isRoyalFishingMap(int mapid) {
      return mapid == TownMap || mapid == TownMap2 || mapid == TownMap3;
   }

   static {
      if (DBConfig.isGanglim) {
         serverName = "강림";
         connectedRate = 1.8;
      }

      JuhunFever = 0;
      JangGangFever = false;
      WORLD_UI = "";
      serverMessage = "";
      ChangeMapUI = false;
      useDailyGift = true;
      dimensionalMirror = null;
      dailyEventType = null;
      goldMapleDropRate = 1;
      useExpRateByLevel = true;
      expRateChangeLevels = new int[]{1, 200, 210, 220, 230, 240, 250, 260, 270, 280, 285, 290};
      expRateByLevels = new int[]{300, 150, 100, 75, 50, 35, 30, 35, 50, 75, 100, 200};
      expFeverRate = 1.0;
      mesoFeverRate = 1.0;
      dropFeverRate = 1.0;
      useAdminClient = true;
      blockedEnterAuction = false;
      expHottimeStartTime = "20:30";
      expHottimeEndTime = "21:30";
      juhunHottimeStartTime = "20:00";
      juhunHottimeEndTime = "22:00";
      cashPlusRate = 0;
      royalGuildSave = true;
      guildSaveTime = 0L;
      enableCRCBin = !DBConfig.isHosting;
      if (DBConfig.isGanglim) {
         expRateChangeLevels = new int[]{1, 200, 250, 300};
         expRateByLevels = new int[]{500, 100, 50, 1};
         juhunHottimeStartTime = "20:00";
         juhunHottimeEndTime = "22:00";
      }

      if (DBConfig.isGanglim) {
         try {
            java.util.Properties prop = getDefaultProperties();
            cashPlusRate = Integer.parseInt(prop.getProperty("cashPlusRate", "0"));
            StartMap = Integer.parseInt(prop.getProperty("StartMap", "120"));
            TownMap = Integer.parseInt(prop.getProperty("TownMap", "121"));
            TownMap2 = Integer.parseInt(prop.getProperty("TownMap2", "121"));
            TownMap3 = Integer.parseInt(prop.getProperty("TownMap3", "121"));
            enableCRCBin = prop.getProperty("enableCRCBin", "true").equals("true");
         } catch (Exception var13) {
            cashPlusRate = 0;
            StartMap = 120;
            TownMap = 121;
            TownMap2 = 121;
            TownMap3 = 121;
            System.out.println("설정값 불러오기 실패");
            var13.printStackTrace();
         } finally {
            System.out.println("캐시 추가 배율 설정됨 " + cashPlusRate + "%(총 " + (100 + cashPlusRate) + "%)");
            System.out.println("StartMap : " + StartMap);
            System.out.println("TownMap : " + TownMap);
            System.out.println("TownMap2 : " + TownMap2);
            System.out.println("TownMap3 : " + TownMap3);
            System.out.println("enableCRCBin : " + enableCRCBin);
         }
      } else {
         try {
            java.util.Properties prop = getDefaultProperties();
            StartMap = Integer.parseInt(prop.getProperty("StartMap", "100"));
            TownMap = Integer.parseInt(prop.getProperty("TownMap", "100"));
            enableCRCBin = prop.getProperty("enableCRCBin", "true").equals("true");
         } catch (Exception var11) {
            StartMap = 100;
            TownMap = 100;
            System.out.println("설정값 불러오기 실패");
            var11.printStackTrace();
         } finally {
            System.out.println("StartMap : " + StartMap);
            System.out.println("TownMap : " + TownMap);
            System.out.println("enableCRCBin : " + enableCRCBin);
         }
      }

      expHottimeRate = 1.0;
      mesoHottimeRate = 1.5;
      currentHottimeRate = 1.0;
      currentMesoHottimeRate = 1.0;
      useChairFishing = true;
      chairFishingMapID = TownMap;
      useAdminClientUpgrade = true;
      top1Ranker = null;
      cacheFaceHair = "";
      accountShareQuestEx = new ArrayList<Integer>() {
         {
            this.addAll(
               List.of(
                  20,
                  21,
                  22,
                  23,
                  24,
                  100,
                  101,
                  102,
                  103,
                  104,
                  105,
                  106,
                  107,
                  108,
                  110,
                  111,
                  112,
                  120,
                  121,
                  122,
                  130,
                  131,
                  132,
                  133,
                  140,
                  141,
                  142,
                  143,
                  144,
                  145,
                  150,
                  151,
                  160,
                  161,
                  162,
                  170,
                  171,
                  172,
                  180,
                  181,
                  182,
                  183,
                  184,
                  190,
                  191,
                  192,
                  193,
                  200,
                  201,
                  202,
                  203,
                  204,
                  210,
                  211,
                  212,
                  213,
                  214,
                  215,
                  216,
                  1100,
                  1110,
                  1111,
                  1068,
                  1235858,
                  1235859
               )
            );
            this.addAll(
               List.of(
                  18098,
                  18790,
                  1211345,
                  1236000,
                  1236001,
                  1234567,
                  1234568,
                  1234579,
                  1235999,
                  1234699,
                  19770,
                  19771,
                  501045,
                  501046,
                  501053,
                  16180,
                  17015,
                  1234555
               )
            );
            this.addAll(
               List.of(
                  1234599,
                  100000,
                  100001,
                  100002,
                  100003,
                  100004,
                  100005,
                  100006,
                  100007,
                  100100,
                  100101,
                  100200,
                  100201,
                  100300,
                  100301,
                  100302,
                  100400,
                  100401,
                  100402,
                  100403,
                  100404,
                  100500,
                  100600,
                  100601,
                  100700,
                  100701,
                  100800,
                  100801,
                  100802,
                  100803,
                  100900,
                  100901,
                  100902,
                  101000,
                  101001,
                  101002,
                  101003,
                  101100,
                  101101,
                  101102,
                  101103,
                  101104,
                  101105,
                  110000
               )
            );
         }
      };
      accountShareQuestEx.addAll(
         List.of(
            QuestExConstants.NeoEventNormalMob.getQuestID(),
            QuestExConstants.NeoEventEliteMob.getQuestID(),
            QuestExConstants.NeoEventRuneAct.getQuestID(),
            QuestExConstants.NeoEventRandomPortal.getQuestID(),
            QuestExConstants.NeoEventAdventureLog.getQuestID()
         )
      );
      accountShareQuestEx.addAll(
         List.of(
            QuestExConstants.UnionMobInfo.getQuestID(),
            QuestExConstants.UnionCoin.getQuestID(),
            QuestExConstants.UnionPreset.getQuestID(),
            QuestExConstants.UnionRankInfo.getQuestID()
         )
      );
      accountShareQuestEx.addAll(
         List.of(QuestExConstants.IntensePowerCrystal.getQuestID(), QuestExConstants.NeoCoreEvent.getQuestID(), QuestExConstants.KillPoint.getQuestID())
      );
      accountShareQuestEx.add(QuestExConstants.CubeLevelUp.getQuestID());
      if (!DBConfig.isGanglim) {
         accountShareQuestEx.add(QuestExConstants.JinQuestExAccount.getQuestID());
         accountShareQuestEx.add(QuestExConstants.JinRestPointReward.getQuestID());
      }

      monsterCollectionQuestEx = new int[]{
         20,
         21,
         22,
         23,
         24,
         100,
         101,
         102,
         103,
         104,
         105,
         106,
         107,
         108,
         110,
         111,
         112,
         120,
         121,
         122,
         130,
         131,
         132,
         133,
         140,
         141,
         142,
         143,
         144,
         145,
         150,
         151,
         160,
         161,
         162,
         170,
         171,
         172,
         180,
         181,
         182,
         183,
         184,
         190,
         191,
         192,
         193,
         200,
         201,
         202,
         203,
         204,
         210,
         211,
         212,
         213,
         214,
         215,
         216,
         1100,
         1110,
         1111,
         100000,
         100001,
         100002,
         100003,
         100004,
         100005,
         100006,
         100007,
         100100,
         100101,
         100200,
         100201,
         100300,
         100301,
         100302,
         100400,
         100401,
         100402,
         100403,
         100404,
         100500,
         100600,
         100601,
         100700,
         100701,
         100800,
         100801,
         100802,
         100803,
         100900,
         100901,
         100902,
         101000,
         101001,
         101002,
         101003,
         101100,
         101101,
         101102,
         101103,
         101104,
         101105,
         110000
      };
      timeScheduleEntry = null;
      useCriticalDll = false;
      authlist = new ConcurrentHashMap<>();
      authlist2 = new ConcurrentHashMap<>();
      ConnectorSetting = false;
      ConnectorSetting1 = true;
      ConnectorLog = false;
      useAchievement = DBConfig.isHosting ? true : true;
   }

   public static enum CommandType {
      NORMAL(0),
      TRADE(1);

      private int level;

      private CommandType(int level) {
         this.level = level;
      }

      public int getType() {
         return this.level;
      }
   }

   public static enum PlayerGMRank {
      NORMAL('@', 0),
      DONATOR('#', 1),
      SUPERDONATOR('$', 2),
      INTERN('%', 3),
      GM('!', 4),
      SUPERGM('!', 5),
      ADMIN('!', 6);

      private char commandPrefix;
      private int level;

      private PlayerGMRank(char ch, int level) {
         this.commandPrefix = ch;
         this.level = level;
      }

      public char getCommandPrefix() {
         return this.commandPrefix;
      }

      public int getLevel() {
         return this.level;
      }
   }
}
