package network.game;

import constants.ServerConstants;
import database.DBConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import network.center.CheaterData;
import network.login.LoginServer;
import network.models.CWvsContext;
import network.netty.MapleNettyDecoder;
import network.netty.MapleNettyEncoder;
import network.netty.MapleNettyHandler;
import network.netty.ServerType;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.fields.AramiaFireWorks;
import objects.fields.Field;
import objects.fields.MapleMapFactory;
import objects.fields.MapleMapObject;
import objects.fields.MapleSquad;
import objects.fields.events.MapleCoconut;
import objects.fields.events.MapleEvent;
import objects.fields.events.MapleEventType;
import objects.fields.events.MapleFitness;
import objects.fields.events.MapleOla;
import objects.fields.events.MapleOxQuiz;
import objects.fields.events.MapleSnowball;
import objects.fields.events.MapleSurvival;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.childs.ChaosDuskEnter;
import objects.fields.fieldset.childs.ChaosKalosEnter;
import objects.fields.fieldset.childs.ChaosZakumEnter;
import objects.fields.fieldset.childs.CulvertEnter;
import objects.fields.fieldset.childs.EasyKalosEnter;
import objects.fields.fieldset.childs.EasyKaringEnter;
import objects.fields.fieldset.childs.EasyMagnusEnter;
import objects.fields.fieldset.childs.EasyWillEnter;
import objects.fields.fieldset.childs.ErdaSpectrumEnter;
import objects.fields.fieldset.childs.ExtremeKalosEnter;
import objects.fields.fieldset.childs.ExtremeKaringEnter;
import objects.fields.fieldset.childs.FlagRaceEnter;
import objects.fields.fieldset.childs.FlagRaceN1Enter;
import objects.fields.fieldset.childs.FlagRaceN2Enter;
import objects.fields.fieldset.childs.FlagRaceN3Enter;
import objects.fields.fieldset.childs.HardBlackHeavenBossEnter;
import objects.fields.fieldset.childs.HardBlackMageEnter;
import objects.fields.fieldset.childs.HardDemianEnter;
import objects.fields.fieldset.childs.HardDunkelEnter;
import objects.fields.fieldset.childs.HardGuardianSlimeEnter;
import objects.fields.fieldset.childs.HardJinHillahEnter;
import objects.fields.fieldset.childs.HardKaringEnter;
import objects.fields.fieldset.childs.HardLucidEnter;
import objects.fields.fieldset.childs.HardSerenEnter;
import objects.fields.fieldset.childs.HardWillEnter;
import objects.fields.fieldset.childs.HellBlackHeavenBossEnter;
import objects.fields.fieldset.childs.HellDemianEnter;
import objects.fields.fieldset.childs.HellDunkelEnter;
import objects.fields.fieldset.childs.HellJinHillahEnter;
import objects.fields.fieldset.childs.HellLucidEnter;
import objects.fields.fieldset.childs.HellWillEnter;
import objects.fields.fieldset.childs.MitsuhideEnter;
import objects.fields.fieldset.childs.MulungForestEnter;
import objects.fields.fieldset.childs.NormalBlackHeavenBossEnter;
import objects.fields.fieldset.childs.NormalDemianEnter;
import objects.fields.fieldset.childs.NormalDunkelEnter;
import objects.fields.fieldset.childs.NormalDuskEnter;
import objects.fields.fieldset.childs.NormalGuardianSlimeEnter;
import objects.fields.fieldset.childs.NormalJinHillahEnter;
import objects.fields.fieldset.childs.NormalKalosEnter;
import objects.fields.fieldset.childs.NormalKaringEnter;
import objects.fields.fieldset.childs.NormalLucidEnter;
import objects.fields.fieldset.childs.NormalSerenEnter;
import objects.fields.fieldset.childs.NormalWillEnter;
import objects.fields.fieldset.childs.PuzzleMasterEnter;
import objects.fields.fieldset.childs.SpiritSaviorEnter;
import objects.fields.fieldset.childs.TangyoonKitchenEnter;
import objects.fields.fieldset.childs.TenguEnter;
import objects.fields.fieldset.childs.ZakumEnter;
import objects.fields.gameobject.lifes.PlayerNPC;
import objects.shop.HiredMerchant;
import objects.users.MapleCharacter;
import objects.utils.ConcurrentEnumMap;
import objects.utils.ServerProperties;
import scripting.EventScriptManager;

public class GameServer {
   public static long serverStartTime;
   private int cashRate = 3;
   private int traitRate = 3;
   private double expRate;
   private double mesoRate;
   private double dropRate = 3.0;
   private int VanishingJourneySymbolBonusRate;
   private int ChewChewSymbolBonusRate;
   private int LachelnSymbolBonusRate;
   private int ArcanaSymbolBonusRate;
   private int MorassSymbolBonusRate;
   private int EsferaSymbolBonusRate;
   private int Sernium1SymbolBonusRate;
   private int Sernium2SymbolBonusRate;
   private int HotelArcsSymbolBonusRate = 1;
   private short port;
   private int channel;
   private int running_MerchantID = 0;
   private int flags = 0;
   private String serverMessage;
   private String ip;
   private String serverName;
   private boolean shutdown = false;
   private boolean finishedShutdown = false;
   private boolean MegaphoneMuteState = false;
   private boolean adminOnly = false;
   private PlayerStorage players;
   private final MapleMapFactory mapFactory;
   private EventScriptManager eventSM;
   private AramiaFireWorks works = new AramiaFireWorks();
   private static final Map<Integer, GameServer> instances = new HashMap<>();
   private final Map<MapleSquad.MapleSquadType, MapleSquad> mapleSquads = new ConcurrentEnumMap<>(MapleSquad.MapleSquadType.class);
   private final Map<Integer, HiredMerchant> merchants = new HashMap<>();
   private final List<PlayerNPC> playerNPCs = new LinkedList<>();
   private final ReentrantReadWriteLock merchLock = new ReentrantReadWriteLock();
   private int eventmap = -1;
   private final Map<MapleEventType, MapleEvent> events = new EnumMap<>(MapleEventType.class);
   public static ScheduledFuture destroyer = null;
   public static ScheduledFuture feverSchedule = null;
   public static ScheduledFuture goldMaplefeverSchedule = null;
   public Collection<FieldSet> updateFieldSet = new LinkedHashSet<>();
   private static ServerBootstrap bootstrap;

   private GameServer(int channel) {
      this.channel = channel;
      this.mapFactory = new MapleMapFactory(channel);
   }

   public static Set<Integer> getAllInstance() {
      return new HashSet<>(instances.keySet());
   }

   public final void loadEvents() {
      if (this.events.size() == 0) {
         this.events.put(MapleEventType.CokePlay, new MapleCoconut(this.channel, MapleEventType.CokePlay));
         this.events.put(MapleEventType.Coconut, new MapleCoconut(this.channel, MapleEventType.Coconut));
         this.events.put(MapleEventType.Fitness, new MapleFitness(this.channel, MapleEventType.Fitness));
         this.events.put(MapleEventType.OlaOla, new MapleOla(this.channel, MapleEventType.OlaOla));
         this.events.put(MapleEventType.OxQuiz, new MapleOxQuiz(this.channel, MapleEventType.OxQuiz));
         this.events.put(MapleEventType.Snowball, new MapleSnowball(this.channel, MapleEventType.Snowball));
         this.events.put(MapleEventType.Survival, new MapleSurvival(this.channel, MapleEventType.Survival));
      }
   }

   public final void loadFieldSet() {
      if (this.updateFieldSet.size() == 0) {
         this.updateFieldSet.add(new ZakumEnter(this.channel));
         this.updateFieldSet.add(new ChaosZakumEnter(this.channel));
         this.updateFieldSet.add(new EasyMagnusEnter(this.channel));
         this.updateFieldSet.add(new ErdaSpectrumEnter(this.channel));
         this.updateFieldSet.add(new SpiritSaviorEnter(this.channel));
         this.updateFieldSet.add(new TangyoonKitchenEnter(this.channel));
         this.updateFieldSet.add(new PuzzleMasterEnter(this.channel));
         this.updateFieldSet.add(new FlagRaceN1Enter(this.channel));
         this.updateFieldSet.add(new FlagRaceN2Enter(this.channel));
         this.updateFieldSet.add(new FlagRaceN3Enter(this.channel));
         this.updateFieldSet.add(new FlagRaceEnter(this.channel));
         this.updateFieldSet.add(new CulvertEnter(this.channel));
         this.updateFieldSet.add(new MulungForestEnter(this.channel));
         this.updateFieldSet.add(new NormalDemianEnter(this.channel));
         this.updateFieldSet.add(new HardDemianEnter(this.channel));
         this.updateFieldSet.add(new HellDemianEnter(this.channel));
         this.updateFieldSet.add(new NormalBlackHeavenBossEnter(this.channel));
         this.updateFieldSet.add(new HardBlackHeavenBossEnter(this.channel));
         this.updateFieldSet.add(new HellBlackHeavenBossEnter(this.channel));
         this.updateFieldSet.add(new NormalLucidEnter(this.channel));
         this.updateFieldSet.add(new HardLucidEnter(this.channel));
         this.updateFieldSet.add(new EasyWillEnter(this.channel));
         this.updateFieldSet.add(new NormalWillEnter(this.channel));
         this.updateFieldSet.add(new HardWillEnter(this.channel));
         this.updateFieldSet.add(new HellWillEnter(this.channel));
         this.updateFieldSet.add(new NormalJinHillahEnter(this.channel));
         this.updateFieldSet.add(new HardJinHillahEnter(this.channel));
         this.updateFieldSet.add(new HellJinHillahEnter(this.channel));
         this.updateFieldSet.add(new NormalGuardianSlimeEnter(this.channel));
         this.updateFieldSet.add(new HardGuardianSlimeEnter(this.channel));
         this.updateFieldSet.add(new NormalDuskEnter(this.channel));
         this.updateFieldSet.add(new ChaosDuskEnter(this.channel));
         this.updateFieldSet.add(new NormalDunkelEnter(this.channel));
         this.updateFieldSet.add(new HardDunkelEnter(this.channel));
         this.updateFieldSet.add(new HellDunkelEnter(this.channel));
         this.updateFieldSet.add(new HellLucidEnter(this.channel));
         this.updateFieldSet.add(new HardBlackMageEnter(this.channel));
         this.updateFieldSet.add(new NormalSerenEnter(this.channel));
         this.updateFieldSet.add(new HardSerenEnter(this.channel));
         this.updateFieldSet.add(new TenguEnter(this.channel));
         this.updateFieldSet.add(new MitsuhideEnter(this.channel));
         this.updateFieldSet.add(new EasyKalosEnter(this.channel));
         this.updateFieldSet.add(new NormalKalosEnter(this.channel));
         this.updateFieldSet.add(new ChaosKalosEnter(this.channel));
         this.updateFieldSet.add(new ExtremeKalosEnter(this.channel));
         this.updateFieldSet.add(new EasyKaringEnter(this.channel));
         this.updateFieldSet.add(new NormalKaringEnter(this.channel));
         this.updateFieldSet.add(new HardKaringEnter(this.channel));
         this.updateFieldSet.add(new ExtremeKaringEnter(this.channel));
      }
   }

   public final void run_startup_configurations() {
      this.setChannel(this.channel);

      try {
         this.expRate = Integer.parseInt(ServerProperties.getProperty("net.sf.odinms.world.exp"));
         this.mesoRate = Integer.parseInt(ServerProperties.getProperty("net.sf.odinms.world.meso"));
         this.dropRate = Integer.parseInt(ServerProperties.getProperty("net.sf.odinms.world.drop"));
         this.serverMessage = ServerProperties.getProperty("net.sf.odinms.world.serverMessage");
         this.VanishingJourneySymbolBonusRate = Integer.parseInt(ServerProperties.getProperty("VanishingJourneySymbolBonusRate"));
         this.ChewChewSymbolBonusRate = Integer.parseInt(ServerProperties.getProperty("ChewChewSymbolBonusRate"));
         this.LachelnSymbolBonusRate = Integer.parseInt(ServerProperties.getProperty("LachelnSymbolBonusRate"));
         this.ArcanaSymbolBonusRate = Integer.parseInt(ServerProperties.getProperty("ArcanaSymbolBonusRate"));
         this.MorassSymbolBonusRate = Integer.parseInt(ServerProperties.getProperty("MorassSymbolBonusRate"));
         this.EsferaSymbolBonusRate = Integer.parseInt(ServerProperties.getProperty("EsferaSymbolBonusRate"));
         this.Sernium1SymbolBonusRate = Integer.parseInt(ServerProperties.getProperty("Sernium1SymbolBonusRate"));
         this.Sernium2SymbolBonusRate = Integer.parseInt(ServerProperties.getProperty("Sernium2SymbolBonusRate"));
         this.HotelArcsSymbolBonusRate = Integer.parseInt(ServerProperties.getProperty("HotelArcsSymbolBonusRate"));
         this.serverName = ServerProperties.getProperty("net.sf.odinms.login.serverName");
         this.flags = Integer.parseInt(ServerProperties.getProperty("net.sf.odinms.world.flags", "0"));
         this.adminOnly = Boolean.parseBoolean(ServerProperties.getProperty("net.sf.odinms.world.admin", "false"));
         this.port = (short)(!ServerConstants.tempServer && DBConfig.isGanglim ? 5454 + this.channel : 8584 + this.channel);
      } catch (Exception var5) {
         throw new RuntimeException(var5);
      }

      this.eventSM = new EventScriptManager(this, ServerProperties.getProperty("net.sf.odinms.channel.events").split(","));
      this.ip = ServerProperties.getProperty("net.sf.odinms.channel.net.interface") + ":" + this.port;
      this.players = new PlayerStorage(this.channel);
      this.loadEvents();
      this.loadFieldSet();
      EventLoopGroup bossGroup = new NioEventLoopGroup(24);
      EventLoopGroup workerGroup = new NioEventLoopGroup(24);

      try {
         bootstrap = new ServerBootstrap();
         ((ServerBootstrap)((ServerBootstrap)bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class))
               .childHandler(new ChannelInitializer<SocketChannel>() {
                  public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast("decoder", new MapleNettyDecoder());
                     ch.pipeline().addLast("encoder", new MapleNettyEncoder());
                     ch.pipeline().addLast("handler", new MapleNettyHandler(ServerType.GAME, GameServer.this.channel));
                  }
               })
               .option(ChannelOption.SO_BACKLOG, 128))
            .childOption(ChannelOption.SO_SNDBUF, 4194304)
            .childOption(ChannelOption.SO_KEEPALIVE, true);
         ChannelFuture f = bootstrap.bind(this.port).sync();
         System.out.println("채널 " + this.getChannel() + " 서버가 " + this.port + " 포트를 성공적으로 개방했습니다.");
         this.eventSM.init();
      } catch (Exception var4) {
         System.out.println("[오류] 채널서버가 " + this.port + " 포트를 개방하는데 실패했습니다.");
         var4.printStackTrace();
      }

      this.eventSM.init();
   }

   public final void shutdown() {
      if (!this.finishedShutdown) {
         this.broadcastPacket(CWvsContext.serverNotice(0, "현재 채널이 잠시 후 종료됩니다."));
         this.shutdown = true;
         System.out.println(this.channel + "채널 캐릭터를 저장합니다.");
         this.getPlayerStorage().disconnectAll();
         instances.remove(this.channel);
         this.setFinishShutdown();
      }
   }

   public final boolean hasFinishedShutdown() {
      return this.finishedShutdown;
   }

   public final MapleMapFactory getMapFactory() {
      return this.mapFactory;
   }

   public int getPlayerCountInChannel() {
      int count = 0;

      for (Field map : this.getMapFactory().getAllMaps()) {
         for (MapleCharacter chr : map.getCharacters()) {
            if (chr != null && chr.getClient().getSession().isOpen()) {
               count++;
            }
         }
      }

      return count;
   }

   public static final GameServer newInstance(int channel) {
      return new GameServer(channel);
   }

   public static final GameServer getInstance(int channel) {
      return instances.get(channel);
   }

   public final void addPlayer(MapleCharacter chr) {
      this.getPlayerStorage().registerPlayer(chr);
   }

   public final List<MapleCharacter> getPartyMembers(Party party) {
      List<MapleCharacter> partym = new LinkedList<>();

      for (PartyMemberEntry partychar : party.getPartyMemberList()) {
         if (partychar.getChannel() == this.getChannel()) {
            MapleCharacter chr = this.getPlayerStorage().getCharacterByName(partychar.getName());
            if (chr != null) {
               partym.add(chr);
            }
         }
      }

      return partym;
   }

   public final PlayerStorage getPlayerStorage() {
      if (this.players == null) {
         this.players = new PlayerStorage(this.channel);
      }

      return this.players;
   }

   public final void removePlayer(MapleCharacter chr) {
      this.getPlayerStorage().deregisterPlayer(chr);
   }

   public final void removePlayer(int idz, String namez, int accId) {
      this.getPlayerStorage().deregisterPlayer(idz, namez, accId);
   }

   public final String getServerMessage() {
      return this.serverMessage;
   }

   public final void setServerMessage(String newMessage) {
      this.serverMessage = newMessage;
      this.broadcastPacket(CWvsContext.serverMessage(this.serverMessage));
   }

   public final void broadcastPacket(byte[] data) {
      this.getPlayerStorage().broadcastPacket(data);
   }

   public final void broadcastPacketCheckQuest(byte[] data, String questStr) {
      this.getPlayerStorage().broadcastPacketCheckQuest(data, questStr, false);
   }

   public final void broadcastPacketCheckQuest(byte[] data, String questStr, boolean force) {
      this.getPlayerStorage().broadcastPacketCheckQuest(data, questStr, force);
   }

   public final void broadcastPacketLadderGame(byte[] data) {
      this.getPlayerStorage().broadcastPacketLadderGame(data);
   }

   public final void broadcastSmegaPacket(byte[] data) {
      this.getPlayerStorage().broadcastSmegaPacket(data);
   }

   public final void broadcastGMPacket(byte[] data) {
      this.getPlayerStorage().broadcastGMPacket(data);
   }

   public final double getExpRate() {
      return this.expRate;
   }

   public final void setExpRate(double expRate) {
      this.expRate = expRate;
   }

   public final int getCashRate() {
      return this.cashRate;
   }

   public int getVanishingJourneySymbolBonusRate() {
      return this.VanishingJourneySymbolBonusRate;
   }

   public void setVanishingJourneySymbolBonusRate(int vanishingJourneySymbolBonusRate) {
      this.VanishingJourneySymbolBonusRate = vanishingJourneySymbolBonusRate;
   }

   public int getChewChewSymbolBonusRate() {
      return this.ChewChewSymbolBonusRate;
   }

   public void setChewChewSymbolBonusRate(int chewChewSymbolBonusRate) {
      this.ChewChewSymbolBonusRate = chewChewSymbolBonusRate;
   }

   public int getLachelnSymbolBonusRate() {
      return this.LachelnSymbolBonusRate;
   }

   public void setLachelnSymbolBonusRate(int lachelnSymbolBonusRate) {
      this.LachelnSymbolBonusRate = lachelnSymbolBonusRate;
   }

   public int getArcanaSymbolBonusRate() {
      return this.ArcanaSymbolBonusRate;
   }

   public void setArcanaSymbolBonusRate(int arcanaSymbolBonusRate) {
      this.ArcanaSymbolBonusRate = arcanaSymbolBonusRate;
   }

   public int getMorassSymbolBonusRate() {
      return this.MorassSymbolBonusRate;
   }

   public void setMorassSymbolBonusRate(int morassSymbolBonusRate) {
      this.MorassSymbolBonusRate = morassSymbolBonusRate;
   }

   public int getEsferaSymbolBonusRate() {
      return this.EsferaSymbolBonusRate;
   }

   public void setEsferaSymbolBonusRate(int esferaSymbolBonusRate) {
      this.EsferaSymbolBonusRate = esferaSymbolBonusRate;
   }

   public int getSernium1SymbolBonusRate() {
      return this.Sernium1SymbolBonusRate;
   }

   public void setSernium1SymbolBonusRate(int sernium1SymbolBonusRate) {
      this.Sernium1SymbolBonusRate = sernium1SymbolBonusRate;
   }

   public int getSernium2SymbolBonusRate() {
      return this.Sernium2SymbolBonusRate;
   }

   public void setSernium2SymbolBonusRate(int sernium2SymbolBonusRate) {
      this.Sernium2SymbolBonusRate = sernium2SymbolBonusRate;
   }

   public int getHotelArcsSymbolBonusRate() {
      return this.HotelArcsSymbolBonusRate;
   }

   public void setHotelArcsSymbolBonusRate(int hotelArcsSymbolBonusRate) {
      this.HotelArcsSymbolBonusRate = hotelArcsSymbolBonusRate;
   }

   public final int getChannel() {
      return this.channel;
   }

   public final void setChannel(int channel) {
      instances.put(channel, this);
      LoginServer.addChannel(channel);
   }

   public static final ArrayList<GameServer> getAllInstances() {
      return new ArrayList<>(instances.values());
   }

   public final String getIP() {
      return this.ip;
   }

   public final boolean isShutdown() {
      return this.shutdown;
   }

   public final int getLoadedMaps() {
      return this.mapFactory.getLoadedMaps();
   }

   public final EventScriptManager getEventSM() {
      return this.eventSM;
   }

   public final void reloadEvents() {
      this.eventSM.cancel();
      this.eventSM = new EventScriptManager(this, ServerProperties.getProperty("net.sf.odinms.channel.events").split(","));
      this.eventSM.init();
   }

   public final double getMesoRate() {
      return this.mesoRate;
   }

   public final void setMesoRate(double mesoRate) {
      this.mesoRate = mesoRate;
   }

   public final double getDropRate() {
      return this.dropRate;
   }

   public final void setDropRate(double dropRate) {
      this.dropRate = dropRate;
   }

   public static final void startChannelServer() {
      serverStartTime = System.currentTimeMillis();

      for (int i = 0; i < Integer.parseInt(ServerProperties.getProperty("net.sf.odinms.channel.count", "0")); i++) {
         newInstance(i + 1).run_startup_configurations();
      }
   }

   public Map<MapleSquad.MapleSquadType, MapleSquad> getAllSquads() {
      return Collections.unmodifiableMap(this.mapleSquads);
   }

   public final MapleSquad getMapleSquad(String type) {
      return this.getMapleSquad(MapleSquad.MapleSquadType.valueOf(type.toLowerCase()));
   }

   public final MapleSquad getMapleSquad(MapleSquad.MapleSquadType type) {
      return this.mapleSquads.get(type);
   }

   public final boolean addMapleSquad(MapleSquad squad, String type) {
      MapleSquad.MapleSquadType types = MapleSquad.MapleSquadType.valueOf(type.toLowerCase());
      if (types != null && !this.mapleSquads.containsKey(types)) {
         this.mapleSquads.put(types, squad);
         squad.scheduleRemoval();
         return true;
      } else {
         return false;
      }
   }

   public final boolean removeMapleSquad(MapleSquad.MapleSquadType types) {
      if (types != null && this.mapleSquads.containsKey(types)) {
         this.mapleSquads.remove(types);
         return true;
      } else {
         return false;
      }
   }

   public final int closeAllMerchant() {
      int ret = 0;
      this.merchLock.writeLock().lock();

      try {
         for (Iterator<Entry<Integer, HiredMerchant>> merchants_ = this.merchants.entrySet().iterator(); merchants_.hasNext(); ret++) {
            HiredMerchant hm = merchants_.next().getValue();
            hm.closeShop(true, false);
            hm.getMap().removeMapObject(hm);
            merchants_.remove();
         }
      } finally {
         this.merchLock.writeLock().unlock();
      }

      for (int var7 = 910000001; var7 <= 910000022; var7++) {
         for (MapleMapObject mmo : this.mapFactory.getMap(var7).getAllHiredMerchantsThreadsafe()) {
            ((HiredMerchant)mmo).closeShop(true, false);
            ret++;
         }
      }

      return ret;
   }

   public final int addMerchant(HiredMerchant hMerchant) {
      this.merchLock.writeLock().lock();

      int var2;
      try {
         this.running_MerchantID++;
         this.merchants.put(this.running_MerchantID, hMerchant);
         var2 = this.running_MerchantID;
      } finally {
         this.merchLock.writeLock().unlock();
      }

      return var2;
   }

   public final void removeMerchant(HiredMerchant hMerchant) {
      this.merchLock.writeLock().lock();

      try {
         this.merchants.remove(hMerchant.getStoreId());
      } finally {
         this.merchLock.writeLock().unlock();
      }
   }

   public final boolean containsMerchant(int accid, int cid) {
      boolean contains = false;
      this.merchLock.readLock().lock();

      try {
         for (HiredMerchant hm : this.merchants.values()) {
            if (hm.getOwnerAccId() == accid || hm.getOwnerId() == cid) {
               contains = true;
               break;
            }
         }
      } finally {
         this.merchLock.readLock().unlock();
      }

      return contains;
   }

   public final List<HiredMerchant> searchMerchant(int itemSearch) {
      List<HiredMerchant> list = new LinkedList<>();
      this.merchLock.readLock().lock();

      try {
         for (HiredMerchant hm : this.merchants.values()) {
            if (hm.searchItem(itemSearch).size() > 0) {
               list.add(hm);
            }
         }
      } finally {
         this.merchLock.readLock().unlock();
      }

      return list;
   }

   public final void toggleMegaphoneMuteState() {
      this.MegaphoneMuteState = !this.MegaphoneMuteState;
   }

   public final boolean getMegaphoneMuteState() {
      return this.MegaphoneMuteState;
   }

   public int getEvent() {
      return this.eventmap;
   }

   public final void setEvent(int ze) {
      this.eventmap = ze;
   }

   public MapleEvent getEvent(MapleEventType t) {
      return this.events.get(t);
   }

   public final Collection<PlayerNPC> getAllPlayerNPC() {
      return this.playerNPCs;
   }

   public final void addPlayerNPC(PlayerNPC npc) {
      if (!this.playerNPCs.contains(npc)) {
         this.playerNPCs.add(npc);
         this.getMapFactory().getMap(npc.getMapId()).addMapObject(npc);
      }
   }

   public final void removePlayerNPC(PlayerNPC npc) {
      if (this.playerNPCs.contains(npc)) {
         this.playerNPCs.remove(npc);
         this.getMapFactory().getMap(npc.getMapId()).removeMapObject(npc);
      }
   }

   public final String getServerName() {
      return this.serverName;
   }

   public final void setServerName(String sn) {
      this.serverName = sn;
   }

   public final String getTrueServerName() {
      return this.serverName.substring(0, this.serverName.length() - 3);
   }

   public final int getPort() {
      return this.port;
   }

   public static final Set<Integer> getChannelServer() {
      return new HashSet<>(instances.keySet());
   }

   public final void setShutdown() {
      this.shutdown = true;
   }

   public final void setFinishShutdown() {
      this.finishedShutdown = true;
      System.out.println(this.channel + "채널이 종료되었습니다.");
   }

   public final boolean isAdminOnly() {
      return this.adminOnly;
   }

   public static final int getChannelCount() {
      return instances.size();
   }

   public final int getTempFlag() {
      return this.flags;
   }

   public static Map<Integer, Integer> getChannelLoad() {
      Map<Integer, Integer> ret = new HashMap<>();

      for (GameServer cs : instances.values()) {
         ret.put(cs.getChannel(), cs.getConnectedClients());
      }

      return ret;
   }

   public int getConnectedClients() {
      return this.getPlayerStorage().getConnectedClients();
   }

   public List<CheaterData> getCheaters() {
      List<CheaterData> cheaters = this.getPlayerStorage().getCheaters();
      Collections.sort(cheaters);
      return cheaters;
   }

   public List<CheaterData> getReports() {
      List<CheaterData> cheaters = this.getPlayerStorage().getReports();
      Collections.sort(cheaters);
      return cheaters;
   }

   public void broadcastMessage(byte[] message) {
      this.broadcastPacket(message);
   }

   public void broadcastSmega(byte[] message) {
      this.broadcastSmegaPacket(message);
   }

   public void broadcastGMMessage(byte[] message) {
      this.broadcastGMPacket(message);
   }

   public AramiaFireWorks getFireWorks() {
      return this.works;
   }

   public int getTraitRate() {
      return this.traitRate;
   }

   public boolean isMyChannelConnected(String charName) {
      return this.getPlayerStorage().getCharacterByName(charName) != null;
   }

   public static int getOnlineConnections() {
      int count = 0;

      for (GameServer cs : getAllInstances()) {
         for (Field map : cs.getMapFactory().getAllMaps()) {
            for (MapleCharacter chr : map.getCharacters()) {
               if (chr != null && chr.getClient().getSession().isOpen()) {
                  count++;
               }
            }
         }
      }

      return count;
   }

   public FieldSet getFieldSet(String name) {
      FieldSet fieldSet = null;

      for (FieldSet fs : this.updateFieldSet) {
         if (fs.name.equals(name)) {
            fieldSet = fs;
            break;
         }
      }

      return fieldSet;
   }
}
