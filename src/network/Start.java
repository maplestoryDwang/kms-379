package network;

import api.DonationRequest;
import constants.AutoHottimeManager;
import constants.DailySettingConstants;
import constants.ForceAtomConstants;
import constants.GameConstants;
import constants.HexaMatrixConstants;
import constants.HottimeItemManager;
import constants.PlayerNPCConstants;
import constants.ServerConstants;
import constants.TimeScheduleEntry;
import constants.devtempConstants.DummyCharacterName;
import constants.devtempConstants.MapleClientCRC;
import constants.devtempConstants.MapleDailyGift;
import constants.devtempConstants.MapleDimensionalMirror;
import constants.devtempConstants.MapleEventList;
import constants.devtempConstants.MapleFishing;
import constants.devtempConstants.MapleGoldenChariot;
import constants.devtempConstants.MapleMonsterCustomHP;
import database.DBConfig;
import database.DBConnection;
import database.DBEventManager;
import database.LogDBConnection;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import network.auction.AuctionHistoryIDManager;
import network.auction.AuctionSearchManager;
import network.auction.AuctionServer;
import network.center.Center;
import network.center.WeeklyItemManager;
import network.center.praise.PraiseDonationMeso;
import network.center.praise.PraiseDonationMesoRank;
import network.center.praise.PraisePointRank;
import network.discordbot.BotServer;
import network.game.GameServer;
import network.game.MapleGuildRanking;
import network.login.LoginInformationProvider;
import network.login.LoginServer;
import network.netty.SessionBlockManager;
import network.shop.CashItemFactory;
import network.shop.CashShopServer;
import objects.contents.ContentsManager;
import objects.context.EventList;
import objects.context.GoldenChariot;
import objects.context.MonsterCollection;
import objects.context.SundayEventList;
import objects.context.party.boss.BossParty;
import objects.fields.child.dojang.DojangRanking;
import objects.fields.child.dreambreaker.DreamBreakerRank;
import objects.fields.child.etc.DamageMeasurementRank;
import objects.fields.child.union.MapleUnionData;
import objects.fields.events.MapleOxQuizFactory;
import objects.fields.fieldset.instance.TangyoonKitchen;
import objects.fields.gameobject.lifes.BossLucid;
import objects.fields.gameobject.lifes.BossPapulatus;
import objects.fields.gameobject.lifes.BossWill;
import objects.fields.gameobject.lifes.EliteMonsterMan;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonsterInformationProvider;
import objects.fields.gameobject.lifes.MobZoneInfo;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.fields.gameobject.lifes.mobskills.SmartMobNoticeData;
import objects.item.MapleInventoryIdentifier;
import objects.item.MapleItemInformationProvider;
import objects.quest.MapleQuest;
import objects.users.achievement.AchievementFactory;
import objects.users.enchant.InnerAbility;
import objects.users.enchant.ItemOptionInfo;
import objects.users.enchant.skilloption.SkillOption;
import objects.users.extra.ExtraAbilityFactory;
import objects.users.potential.CharacterPotential;
import objects.users.skills.SkillFactory;
import objects.users.skills.VCoreData;
import objects.users.skills.VCoreEnforcement;
import objects.users.skills.VMatrixOption;
import objects.utils.AdminClient;
import objects.utils.AutobanManager;
import objects.utils.CMDCommand;
import objects.utils.DebugForm;
import objects.utils.DebugStream;
import objects.utils.ServerProperties;
import objects.utils.StringProvider;
import objects.utils.Timer;
import scripting.ReactorScriptManager;
import scripting.newscripting.ScriptManager;
import scripting.newscripting.ScriptThreadManager;

public class Start {
   public static long startTime = System.currentTimeMillis();
   public static final Start instance = new Start();
   public static AtomicInteger CompletedLoadingThreads = new AtomicInteger(0);
   public static HashMap<Integer, List<String>> nickNames = new HashMap<>();
   public static List<String> names = new ArrayList<>();
   public static boolean bloodyQueenLog = false;
   public static long rebootTime;
   public static HashMap<String, List<String>> bossEnterLog;

   public void run() throws InterruptedException {
      if (!DBConfig.DB_PASSWORD.equals("J2vs@efh6@K6!2")) {
         System.setProperty("net.sf.odinms.wzpath", "wz");
      }

      try {
         ScriptManager.parseScripts();
      } catch (IOException var8) {
      }

      ScriptThreadManager tMan = ScriptThreadManager.getInstance();
      tMan.start();
      tMan.register(tMan.purge(), 300000L);
      DBConnection.init();
      LogDBConnection.init();
      DBEventManager.init(DBConfig.isGanglim ? 80 : 6);
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = 0");
         ps.executeUpdate();
         ps.close();
         ps = con.prepareStatement("UPDATE accounts SET allowed = 0");
         ps.executeUpdate();
         ps.close();
      } catch (SQLException var10) {
         throw new RuntimeException("[EXCEPTION] Please check if the SQL server is active.");
      }

      if (ServerCerficator.serverAuth()) {
         System.err.println("[通知] Dev 03-28");
         Center.init();
         Timer.HeartBeatTimer.getInstance().start();
         Timer.WorldTimer.getInstance().start();
         Timer.EtcTimer.getInstance().start();
         Timer.MapTimer.getInstance().start();
         Timer.CloneTimer.getInstance().start();
         Timer.EventTimer.getInstance().start();
         Timer.BuffTimer.getInstance().start();
         Timer.PingTimer.getInstance().start();
         Timer.ShowTimer.getInstance().start();
         Timer.SaveTimer.getInstance().start();
         Timer.HottimeTimer.getInstance().start();
         ServerConstants.WORLD_UI = ServerProperties.getProperty("net.sf.odinms.login.serverUI");
         ServerConstants.ChangeMapUI = Boolean.parseBoolean(ServerProperties.getProperty("net.sf.odinms.login.ChangeMapUI"));
         MapleMonsterCustomHP.Load();
         Start.AllLoding allLoding = new Start.AllLoding();
         allLoding.start();
         ServerConstants.timeScheduleEntry = new TimeScheduleEntry();
         ServerConstants.timeScheduleEntry.load();
         ServerConstants.loadMirrorDungeon();
         MapleDimensionalMirror.Load();
         SmartMobNoticeData.loadSmartMobNoticeData();
         System.out.println("开始启动独立服务器。");
         System.out.println("开始启动登录服务器。");
         LoginServer.startLoginServer();
         System.out.println("开始启动频道服务器。");
         GameServer.startChannelServer();
         System.out.println("开始启动现金商城服务器。");
         CashShopServer.startCashShopServer();
         System.out.println("开始启动枫叶拍卖服务器。");
         AuctionServer.startAuctionServer();

         try {
            new BotServer().run_startup_configurations();
         } catch (Exception var7) {
            System.out.println("BotServer Startup Err");
            var7.printStackTrace();
         }

         Center.Guild.LoadAllGuild();
         HexaMatrixConstants.init();
         ForceAtomConstants.infoInit();
         catchHair_Face();
         Timer.CheatTimer.getInstance().register(AutobanManager.getInstance(), 10000L);
         Runtime.getRuntime().addShutdownHook(new Thread(new Start.Shutdown()));
         Center.registerRespawn();
         DreamBreakerRank.loadRank();
         DamageMeasurementRank.loadRank();
         ShutdownServer.registerMBean();
         LoginServer.setOn();
         Center.Auction.load();
         AuctionHistoryIDManager.init();
         WeeklyItemManager.loadWeeklyItems();
         DojangRanking.initRanking();
         if (!DBConfig.isGanglim) {
            PraisePointRank.loadRanks();
            PraiseDonationMeso.loadData();
            PraiseDonationMesoRank.loadRanks();
         }

         StringProvider.load();
         Center.registerAutoFever();
         ExtraAbilityFactory.loadData();
         if (DBConfig.isGanglim) {
            MapleFishing.Load();
            PlayerNPCConstants.loadPlayerNPCPos();
         }

         Timer.EtcTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               Center.registerAutoSave();
            }
         }, 20000L);
         Center.registerAutoHottime();
         HottimeItemManager.loadHottimeItem();
         Center.registerHottimeItem();
         Center.registerAutoJuhunHottime();
         Center.autoCCU();
         AutoHottimeManager.loadAutoHottime();
         Center.registerLoggingSave();
         MapleDailyGift.Load();
         MapleDimensionalMirror.Load();
         MapleEventList.Load();
         MapleFishing.Load();
         MapleClientCRC.Load();
         MapleGoldenChariot.Load();
         Center.InstanceFieldMan.init();
         EliteMonsterMan.load();
         AdminClient.main();
         if (DBConfig.isHosting) {
            Center.registerAutoCharge();
         }

         if (!DBConfig.isGanglim) {
            DonationRequest.main();
         }

         if (!DBConfig.isGanglim || !DBConfig.isHosting) {
            EventQueue.invokeLater(() -> new DebugForm().setVisible(true));
         }

         Center.checkRegenThread();
         GameConstants.loadBGM();
         SessionBlockManager.startUpdater(5000L);
         MapleMonsterInformationProvider.getInstance().clearDrops();
         ReactorScriptManager.getInstance().clearDrops();
         Center.sunShineStorage.loadBloomflower();
         Center.sunShineStorage.autoSave();
         DummyCharacterName.Load();
         if (!DBConfig.isGanglim) {
            ContentsManager.JaumQuizGame.LoadJaumGameDB();
         }

         if (!DBConfig.isHosting) {
            DebugStream.activate();
         }

         rebootTime = System.currentTimeMillis();
         bossEnterLog = new HashMap<>();
      }
   }

   public static void main(String[] args) {
      try {
         instance.run();
      } catch (InterruptedException var2) {
      }
   }

   public static void catchHair_Face() {
      File Hair = new File(System.getProperty("net.sf.odinms.wzpath") + "/Character.wz/Hair_000.wz");
      File Hair_ = new File(System.getProperty("net.sf.odinms.wzpath") + "/Character.wz/Hair_001.wz");
      File Face = new File(System.getProperty("net.sf.odinms.wzpath") + "/Character.wz/Face_000.wz");

      for (File file : Hair.listFiles()) {
         ServerConstants.cacheFaceHair = ServerConstants.cacheFaceHair + file.getName();
      }

      for (File file : Hair_.listFiles()) {
         ServerConstants.cacheFaceHair = ServerConstants.cacheFaceHair + file.getName();
      }

      for (File file : Face.listFiles()) {
         ServerConstants.cacheFaceHair = ServerConstants.cacheFaceHair + file.getName();
      }

      System.out.println("头发和整形代码缓存完成。");
   }

   private class AllLoding extends Thread {
      @Override
      public void run() {
         Start.LoadingThread SkillLoader = new Start.LoadingThread(new Runnable() {
            @Override
            public void run() {
               try {
                  SkillFactory.load();
               } catch (Exception var2) {
                  System.out.println("SkillFactory Load Err");
                  var2.printStackTrace();
               }

               CharacterPotential.loadSkillLevelTables();
            }
         }, "技能", this);
         Start.LoadingThread QuestLoader = new Start.LoadingThread(new Runnable() {
            @Override
            public void run() {
               MapleLifeFactory.loadQuestCounts();
               MapleQuest.initQuests();
            }
         }, "任务", this);
         Start.LoadingThread ItemLoader = new Start.LoadingThread(new Runnable() {
            @Override
            public void run() {
               MapleInventoryIdentifier.getInstance();
               MapleItemInformationProvider.getInstance().runEtc();
               MapleItemInformationProvider.getInstance().runItems();
               ItemOptionInfo.loadItemInfo();
               SkillOption.init();
               DailySettingConstants.main(null);
               CashItemFactory.getInstance().initialize();
               AuctionSearchManager.init();
            }
         }, "道具", this);
         Start.LoadingThread GuildRankingLoader = new Start.LoadingThread(new Runnable() {
            @Override
            public void run() {
               MapleGuildRanking.getInstance().load();
            }
         }, "公会排行", this);
         Start.LoadingThread EtcLoader = new Start.LoadingThread(new Runnable() {
            @Override
            public void run() {
               LoginInformationProvider.getInstance();
               RandomRewards.load();
               MapleOxQuizFactory.getInstance();
               BossParty.cachingBossParty();
               InnerAbility.loadingInnerAbility();
               EventList.cachingEventList();
               SundayEventList.cachingSundayEventList();
               GoldenChariot.cachingGCList();
               TangyoonKitchen.loadTangyoonRecipe();
               MonsterCollection.cacheMonsterCollection();
               AchievementFactory.loadData();
            }
         }, "其他", this);
         Start.LoadingThread MonsterLoader = new Start.LoadingThread(new Runnable() {
            @Override
            public void run() {
               MobSkillFactory.getInstance();
               MobZoneInfo.load();
               BossLucid.load();
               BossWill.load();
               BossPapulatus.load();
            }
         }, "怪物", this);
         Start.LoadingThread MatrixLoader = new Start.LoadingThread(new Runnable() {
            @Override
            public void run() {
               VCoreData.LoadVCore();
               VCoreEnforcement.LoadEnforcement();
               VMatrixOption.Load();
            }
         }, "V核心", this);
         Start.LoadingThread UnionLoader = new Start.LoadingThread(new Runnable() {
            @Override
            public void run() {
               MapleUnionData.loadData();
            }
         }, "联盟", this);
         Start.LoadingThread[] LoadingThreads = new Start.LoadingThread[]{
            SkillLoader, QuestLoader, ItemLoader, GuildRankingLoader, EtcLoader, MonsterLoader, MatrixLoader, UnionLoader
         };

         for (Thread t : LoadingThreads) {
            t.start();
         }

         synchronized (this) {
            try {
               this.wait();
            } catch (InterruptedException var18) {
               var18.printStackTrace();
            }
         }

         while (Start.CompletedLoadingThreads.get() != LoadingThreads.length) {
            synchronized (this) {
               try {
                  this.wait();
               } catch (InterruptedException var16) {
                  var16.printStackTrace();
               }
            }
         }

         System.out.println("所有缓存工作已完成。(耗时 " + (System.currentTimeMillis() - Start.startTime) / 1000L + "秒)");
         GameConstants.isOpen = true;
         long before = Runtime.getRuntime().freeMemory();
         System.gc();
         long after = Runtime.getRuntime().freeMemory();
         DecimalFormat df = new DecimalFormat(",###.000");
         System.out.println("已进行垃圾回收，释放了 " + df.format((after - before) / 1000000.0) + " MB 内存。");
         CMDCommand.main();
      }
   }

   private static class LoadingThread extends Thread {
      protected String LoadingThreadName;

      private LoadingThread(Runnable r, String t, Object o) {
         super(new Start.NotifyingRunnable(r, o, t));
         this.LoadingThreadName = t;
      }

      @Override
      public synchronized void start() {
         System.out.println(this.LoadingThreadName + " 加载开始。");
         super.start();
      }
   }

   private static class NotifyingRunnable implements Runnable {
      private String LoadingThreadName;
      private long StartTime;
      private Runnable WrappedRunnable;
      private final Object ToNotify;

      private NotifyingRunnable(Runnable r, Object o, String name) {
         this.WrappedRunnable = r;
         this.ToNotify = o;
         this.LoadingThreadName = name;
      }

      @Override
      public void run() {
         this.StartTime = System.currentTimeMillis();
         this.WrappedRunnable.run();
         System.out
            .println(
               this.LoadingThreadName
                  + " 加载完成。(耗时 "
                  + (System.currentTimeMillis() - this.StartTime) / 1000L
                  + "秒。["
                  + (Start.CompletedLoadingThreads.get() + 1)
                  + "/8])"
            );
         synchronized (this.ToNotify) {
            Start.CompletedLoadingThreads.incrementAndGet();
            this.ToNotify.notify();
         }
      }
   }

   public static class Shutdown implements Runnable {
      @Override
      public void run() {
         ShutdownServer.getInstance().run();
         ShutdownServer.getInstance().run();
      }
   }
}
