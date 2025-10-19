package objects.fields;

import constants.DailyEventType;
import constants.GameConstants;
import constants.PlayerNPCConstants;
import constants.QuestExConstants;
import constants.ServerConstants;
import database.DBConfig;
import database.DBConnection;
import io.netty.util.internal.ThreadLocalRandom;
import java.awt.Point;
import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import logging.LoggingManager;
import logging.entry.BossLog;
import logging.entry.BossLogType;
import network.SendPacketOpcode;
import network.center.Center;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import network.models.FontColorType;
import network.models.FontType;
import network.models.MobPacket;
import network.models.PetPacket;
import objects.androids.Android;
import objects.context.expedition.ExpeditionType;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.effect.child.TextEffect;
import objects.fields.child.demian.Field_Demian;
import objects.fields.child.dojang.Field_Dojang;
import objects.fields.child.dreambreaker.Field_DreamBreaker;
import objects.fields.child.etc.DamageMeasurementRank;
import objects.fields.child.horntail.Field_Horntail;
import objects.fields.child.moonbridge.Field_FerociousBattlefield;
import objects.fields.child.muto.HungryMuto;
import objects.fields.child.papulatus.Field_Papulatus;
import objects.fields.child.pollo.Field_BountyHunting;
import objects.fields.child.pollo.Field_MidnightMonsterHunting;
import objects.fields.child.pollo.Field_StormwingArea;
import objects.fields.child.will.Field_WillBattle;
import objects.fields.child.will.SpiderWeb;
import objects.fields.events.MapleEvent;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.instance.HardBlackHeavenBoss;
import objects.fields.fieldset.instance.HardDemianBoss;
import objects.fields.fieldset.instance.HardLucidBoss;
import objects.fields.fieldset.instance.HardWillBoss;
import objects.fields.fieldset.instance.HellBlackHeavenBoss;
import objects.fields.fieldset.instance.HellDemianBoss;
import objects.fields.fieldset.instance.HellDunkelBoss;
import objects.fields.fieldset.instance.HellLucidBoss;
import objects.fields.fieldset.instance.HellWillBoss;
import objects.fields.fieldset.instance.NormalJinHillahBoss;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.Drop;
import objects.fields.gameobject.Extractor;
import objects.fields.gameobject.FieldAttackObj;
import objects.fields.gameobject.OpenGate;
import objects.fields.gameobject.Reactor;
import objects.fields.gameobject.RuneStone;
import objects.fields.gameobject.TownPortal;
import objects.fields.gameobject.lifes.AbstractLoadedMapleLife;
import objects.fields.gameobject.lifes.ChangeableStats;
import objects.fields.gameobject.lifes.EliteGrade;
import objects.fields.gameobject.lifes.EliteType;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MapleMonsterInformationProvider;
import objects.fields.gameobject.lifes.MapleNPC;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.MobZoneInfo;
import objects.fields.gameobject.lifes.MonsterDropEntry;
import objects.fields.gameobject.lifes.MonsterGlobalDropEntry;
import objects.fields.gameobject.lifes.OverrideMonsterStats;
import objects.fields.gameobject.lifes.PlayerNPC;
import objects.fields.gameobject.lifes.SpawnPoint;
import objects.fields.gameobject.lifes.SpawnPointAreaBoss;
import objects.fields.gameobject.lifes.Spawns;
import objects.fields.gameobject.lifes.mobskills.FieldCommand;
import objects.fields.gameobject.lifes.mobskills.MobSkill;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.fields.gameobject.lifes.mobskills.MobSkillID;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.fields.gameobject.lifes.mobskills.MobSkillStat;
import objects.fields.obstacle.ObstacleAtom;
import objects.fields.obstacle.ObstacleAtomAction;
import objects.fields.obstacle.ObstacleAtomCreateType;
import objects.fields.obstacle.ObstacleAtomCreator;
import objects.fields.obstacle.ObstacleAtomCreatorEntry;
import objects.item.Equip;
import objects.item.IntensePowerCrystalData;
import objects.item.Item;
import objects.item.MapleInventoryIdentifier;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.item.MaplePet;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MapleStat;
import objects.users.achievement.AchievementFactory;
import objects.users.jobs.koc.FlameWizard;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStat;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.CurrentTime;
import objects.utils.FileoutputUtil;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;
import objects.utils.StringUtil;
import objects.utils.Timer;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.newscripting.ScriptManager;

public class Field {
   private FieldSetInstance fsi;
   private final Map<MapleMapObjectType, LinkedHashMap<Integer, MapleMapObject>> mapobjects;
   private final Map<MapleMapObjectType, ReentrantReadWriteLock> mapobjectlocks;
   private final CopyOnWriteArrayList<MapleCharacter> characters = new CopyOnWriteArrayList<>();
   private int runningOid = 500000;
   private static AtomicInteger runningOid_NPC = new AtomicInteger(1000000000);
   private final Lock runningOidLock = new ReentrantLock();
   protected final List<Spawns> monsterSpawn = new ArrayList<>();
   private final AtomicInteger spawnedMonstersOnMap = new AtomicInteger(0);
   private final Map<Integer, Portal> portals = new HashMap<>();
   private MapleFootholdTree footholds = null;
   private float monsterRate;
   private float recoveryRate;
   private MapleMapEffect mapEffect;
   private MapleCharacter hasteBoosterTarget = null;
   private boolean useHasteBooster;
   private long useHasteBoosterTime = 0L;
   private int fieldType = 0;
   private byte channel;
   private boolean vrlimit = false;
   private short decHP = 0;
   private short createMobInterval = 9000;
   private short top = 0;
   private short bottom = 0;
   private short left = 0;
   private short right = 0;
   private int starforce = 0;
   private int authenticforce = 0;
   private int arcaneforce = 0;
   private int consumeItemCoolTime = 0;
   private int protectItem = 0;
   private int decHPInterval = 10000;
   private int mapid;
   private int returnMapId;
   private int timeLimit;
   private int fieldLimit;
   private int maxRegularSpawn = 0;
   private int fixedMob;
   private int forcedReturnMap = 999999999;
   private int instanceid = -1;
   private int lvForceMove = 0;
   private int lvLimit = 0;
   private int permanentWeather = 0;
   private int partyBonusRate = 0;
   private String mode = "";
   private boolean town;
   private boolean clock;
   private boolean personalShop;
   private boolean everlast = false;
   private boolean dropsDisabled = false;
   private boolean gDropsDisabled = false;
   private boolean soaring = false;
   private boolean squadTimer = false;
   private boolean isSpawns = true;
   private boolean checkStates = true;
   private Point reviveCurFieldOfNoTransferPoint = null;
   private boolean reviveCurFieldOfNoTransfer = false;
   private String mapName;
   private String streetName;
   private String onUserEnter;
   private String onFirstUserEnter;
   private String speedRunLeader = "";
   private List<Integer> dced = new ArrayList<>();
   private ScheduledFuture<?> squadSchedule;
   private ScheduledFuture<?> catchstart = null;
   private long speedRunStart = 0L;
   private long lastSpawnTime = 0L;
   private long lastHurtTime = 0L;
   private long lastEliteMob = 0L;
   private int eliteLevel = 0;
   private EliteState eliteState = EliteState.Normal;
   private int remainEliteMobSpawn = 0;
   private long lastSpawnRuneTime = 0L;
   private int eliteBossCurseLevel = 0;
   private long lastUpdateEliteBossCurseTime = 0L;
   private boolean spawnEventMob = false;
   private int remainEventMobSpawn = 0;
   private MapleNodes nodes;
   private MapleSquad.MapleSquadType squad;
   private Map<String, Integer> environment = new LinkedHashMap<>();
   private int mobCapacityMin;
   private int mobCapacityMax;
   private int breakTimeFieldLevel = 10;
   private long lastActiveFieldTime = 0L;
   private int breakTimeFieldExpRate = 0;
   private int breakTimeFieldLevelExp = 0;
   private ScheduledFuture<?> breakTimeFieldTask = null;
   private long lastRespawnTime = 0L;
   private long lastUpdateFieldCatcherTime = 0L;
   private int fieldCatcherIndex = 0;
   private List<ObstacleAtomCreator> obstacleAtomCreators = new ArrayList<>();
   private List<ObstacleAtomAction> obstacleAtomActions = new ArrayList<>();
   private Map<Integer, ForceAtom> forceAtoms = new HashMap<>();
   private Map<Integer, SecondAtom.Atom> secondAtoms = new ConcurrentHashMap<>();
   private boolean activeDebuffObjs = true;
   private int debuffObjInterval = 4000;
   private Map<Integer, DebuffObj> debuffObjs = new HashMap<>();
   private FieldCommand currentFieldCommand = null;
   private FieldCommandContext fieldCommandContext = null;
   private boolean mobGen = true;
   private List<Integer> mobGenExcept = new ArrayList<>();
   private List<ObstacleAtom> obstacleAtoms = new CopyOnWriteArrayList<>();
   private boolean spawnSpiderWeb = true;
   private List<SpiderWeb> spiderWebs = new ArrayList<>();
   public Map<Integer, FallingCatcher> fallingCatcher = new HashMap<>();
   private FieldEvent fieldEvent;
   private boolean firstSpawn = true;
   private long lastCheckMacroTime = 0L;
   private Rect mBR;
   private int incMobGen = 0;
   private int reduceMobGenTime = 0;
   private long lastSpawnedSpecialJaguarTime = 0L;
   private int stormWingCount = 0;
   private boolean checkLinkMobRevive = false;
   public List<MapleMonster> mobZoneMobs = new ArrayList<>();
   public MobZoneInfo currentMobZone = null;
   private Map<String, Boolean> enabledObject = new HashMap<>();
   private Map<Integer, Long> nextRemoveMonster = new HashMap<>();
   private List<DynamicObject> dynamicObjects = new ArrayList<>();
   private ReentrantReadWriteLock transformLock = new ReentrantReadWriteLock();
   public static AtomicInteger bounceAttackSN = new AtomicInteger(0);
   private List<FieldMonsterSpawner> monsterSpawners;
   List<Pair<String, Long>> musicList = Collections.synchronizedList(new ArrayList<>());
   private long nextMusicTime = 0L;
   private Map<Integer, Integer> wreckages = new HashMap<>();
   private Point[] spawnPoints = new Point[] { new Point(-330, -434), new Point(-510, -86), new Point(-570, -494),
         new Point(30, -393) };

   public int getHeight() {
      return this.getTop() - this.getBottom();
   }

   public int getWidth() {
      return this.getRight() - this.getLeft();
   }

   public Field(int mapid, int channel, int returnMapId, float monsterRate) {
      this.mapid = mapid;
      this.channel = (byte) channel;
      this.returnMapId = returnMapId;
      if (this.returnMapId == 999999999) {
         this.returnMapId = mapid;
      }

      if (GameConstants.getPartyPlay(mapid) > 0) {
         this.monsterRate = (monsterRate - 1.0F) * 2.5F + 1.0F;
      } else {
         this.monsterRate = monsterRate;
      }

      EnumMap<MapleMapObjectType, LinkedHashMap<Integer, MapleMapObject>> objsMap = new EnumMap<>(
            MapleMapObjectType.class);
      EnumMap<MapleMapObjectType, ReentrantReadWriteLock> objlockmap = new EnumMap<>(MapleMapObjectType.class);

      for (MapleMapObjectType type : MapleMapObjectType.values()) {
         objsMap.put(type, new LinkedHashMap<>());
         objlockmap.put(type, new ReentrantReadWriteLock());
      }

      this.mapobjects = Collections.unmodifiableMap(objsMap);
      this.mapobjectlocks = Collections.unmodifiableMap(objlockmap);
   }

   public final void setSpawns(boolean fm) {
      this.isSpawns = fm;
   }

   public final boolean getSpawns() {
      return this.isSpawns;
   }

   public final void setFixedMob(int fm) {
      this.fixedMob = fm;
   }

   public final void setForceMove(int fm) {
      this.lvForceMove = fm;
   }

   public final int getForceMove() {
      return this.lvForceMove;
   }

   public final void setLevelLimit(int fm) {
      this.lvLimit = fm;
   }

   public final int getLevelLimit() {
      return this.lvLimit;
   }

   public final void setReturnMapId(int rmi) {
      this.returnMapId = rmi;
   }

   public final void setSoaring(boolean b) {
      this.soaring = b;
   }

   public final boolean canSoar() {
      return this.soaring;
   }

   public final void toggleDrops() {
      this.dropsDisabled = !this.dropsDisabled;
   }

   public final void setDrops(boolean b) {
      this.dropsDisabled = b;
   }

   public final void toggleGDrops() {
      this.gDropsDisabled = !this.gDropsDisabled;
   }

   public final int getId() {
      return this.mapid;
   }

   public final Field getReturnMap() {
      return GameServer.getInstance(this.channel).getMapFactory().getMap(this.returnMapId);
   }

   public final int getReturnMapId() {
      return this.returnMapId;
   }

   public final int getForcedReturnId() {
      return this.forcedReturnMap;
   }

   public final Field getForcedReturnMap() {
      return GameServer.getInstance(this.channel).getMapFactory().getMap(this.forcedReturnMap);
   }

   public final void setForcedReturnMap(int map) {
      this.forcedReturnMap = map;
   }

   public final float getRecoveryRate() {
      return this.recoveryRate;
   }

   public final void setRecoveryRate(float recoveryRate) {
      this.recoveryRate = recoveryRate;
   }

   public final int getFieldLimit() {
      return this.fieldLimit;
   }

   public final void setFieldLimit(int fieldLimit) {
      this.fieldLimit = fieldLimit;
   }

   public String getMode() {
      return this.mode;
   }

   public final void setMode(String mode) {
      this.mode = mode;
   }

   public final int getFieldType() {
      return this.fieldType;
   }

   public final void setFieldType(int fieldType) {
      this.fieldType = fieldType;
   }

   public final void setCreateMobInterval(short createMobInterval) {
      this.createMobInterval = createMobInterval;
   }

   public final void setTimeLimit(int timeLimit) {
      this.timeLimit = timeLimit;
   }

   public final void setMapName(String mapName) {
      this.mapName = mapName;
   }

   public final String getMapName() {
      return this.mapName;
   }

   public final String getStreetName() {
      return this.streetName;
   }

   public final void setFirstUserEnter(String onFirstUserEnter) {
      this.onFirstUserEnter = onFirstUserEnter;
   }

   public final void setUserEnter(String onUserEnter) {
      this.onUserEnter = onUserEnter;
   }

   public final String getFirstUserEnter() {
      return this.onFirstUserEnter;
   }

   public final String getUserEnter() {
      return this.onUserEnter;
   }

   public final boolean hasClock() {
      return this.clock;
   }

   public final void setClock(boolean hasClock) {
      this.clock = hasClock;
   }

   public final boolean isTown() {
      return this.town;
   }

   public final void setTown(boolean town) {
      this.town = town;
   }

   public final boolean allowPersonalShop() {
      return this.personalShop;
   }

   public final void setPersonalShop(boolean personalShop) {
      this.personalShop = personalShop;
   }

   public final void setStreetName(String streetName) {
      this.streetName = streetName;
   }

   public final void setEverlast(boolean everlast) {
      this.everlast = everlast;
   }

   public final boolean getEverlast() {
      return this.everlast;
   }

   public final int getHPDec() {
      return this.decHP;
   }

   public final void setHPDec(int delta) {
      if (delta > 0 || this.mapid == 749040100) {
         this.lastHurtTime = System.currentTimeMillis();
      }

      this.decHP = (short) delta;
   }

   public final int getHPDecInterval() {
      return this.decHPInterval;
   }

   public final void setHPDecInterval(int delta) {
      this.decHPInterval = delta;
   }

   public final int getHPDecProtect() {
      return this.protectItem;
   }

   public final void setHPDecProtect(int delta) {
      this.protectItem = delta;
   }

   public final int getCurrentPartyId() {
      for (MapleCharacter chr : this.characters) {
         if (chr.getParty() != null) {
            return chr.getParty().getId();
         }
      }

      return -1;
   }

   public void setFieldSetInstance(FieldSetInstance fsi) {
      this.fsi = fsi;
   }

   public FieldSetInstance getFieldSetInstance() {
      return this.fsi;
   }

   public final void addMapObject(MapleMapObject mapobject) {
      int newOid;
      if (mapobject instanceof MapleNPC) {
         newOid = runningOid_NPC.incrementAndGet();
         GameObjects.add(newOid, (MapleNPC) mapobject);
      } else {
         this.runningOidLock.lock();

         try {
            newOid = ++this.runningOid;
         } finally {
            this.runningOidLock.unlock();
         }
      }

      mapobject.setObjectId(newOid);
      this.mapobjectlocks.get(mapobject.getType()).writeLock().lock();

      try {
         this.mapobjects.get(mapobject.getType()).put(newOid, mapobject);
      } finally {
         this.mapobjectlocks.get(mapobject.getType()).writeLock().unlock();
      }
   }

   protected void spawnAndAddRangedMapObject(MapleMapObject mapobject, Field.DelayedPacketCreation packetbakery) {
      this.spawnMapObject(mapobject, packetbakery);
   }

   private int spawnMapObject(MapleMapObject mapobject, Field.DelayedPacketCreation packetbakery) {
      this.addMapObject(mapobject);

      for (MapleCharacter chr : this.characters) {
         if (chr != null && !chr.isClone()) {
            if (mapobject.getType() == MapleMapObjectType.ITEM) {
               Drop drop = (Drop) mapobject;
               if (drop.isIndividual()) {
                  if (drop.getOwner() == chr.getId()) {
                     packetbakery.sendPackets(chr.getClient());
                  }
               } else {
                  packetbakery.sendPackets(chr.getClient());
               }
            } else if (mapobject.getType() == MapleMapObjectType.MONSTER) {
               MapleMonster mob = (MapleMonster) mapobject;
               if (mob.getOwner() > 0 && chr.getId() == mob.getOwner()) {
                  packetbakery.sendPackets(chr.getClient());
               } else if (mob.getOwner() == 0) {
                  packetbakery.sendPackets(chr.getClient());
               }
            } else {
               packetbakery.sendPackets(chr.getClient());
            }

            chr.addVisibleMapObject(mapobject);
         }
      }

      if (mapobject.getType() == MapleMapObjectType.WRECKAGE) {
         Wreckage wr = (Wreckage) mapobject;
         int count = this.wreckages.getOrDefault(wr.getOwnerID(), 0) + 1;
         this.wreckages.put(wr.getOwnerID(), count);
      }

      return mapobject.getObjectId();
   }

   public final void removeMonsterBySelfDestruct(MapleMonster monster) {
      this.spawnedMonstersOnMap.decrementAndGet();
      this.removeMapObject(monster);
   }

   public final void removeMapObject(MapleMapObject obj) {
      this.mapobjectlocks.get(obj.getType()).writeLock().lock();

      try {
         this.mapobjects.get(obj.getType()).remove(obj.getObjectId());
         if (obj.getType() == MapleMapObjectType.WRECKAGE) {
            Wreckage wr = (Wreckage) obj;
            int count = this.wreckages.getOrDefault(wr.getOwnerID(), 0) - 1;
            if (count < 0) {
               count = 0;
            }

            this.wreckages.put(wr.getOwnerID(), count);
         }
      } finally {
         this.mapobjectlocks.get(obj.getType()).writeLock().unlock();
      }
   }

   public final Point calcPointBelow(Point initial) {
      MapleFoothold fh = this.footholds.findBelow(initial);
      if (fh == null) {
         return null;
      } else {
         int dropY = fh.getY1();
         if (!fh.isWall() && fh.getY1() != fh.getY2()) {
            double s1 = Math.abs(fh.getY2() - fh.getY1());
            double s2 = Math.abs(fh.getX2() - fh.getX1());
            if (fh.getY2() < fh.getY1()) {
               dropY = fh.getY1() - (int) (Math.cos(Math.atan(s2 / s1))
                     * (Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2))));
            } else {
               dropY = fh.getY1() + (int) (Math.cos(Math.atan(s2 / s1))
                     * (Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2))));
            }
         }

         return new Point(initial.x, dropY);
      }
   }

   public final Point calcDropPos(Point initial, Point fallback) {
      Point ret = this.calcPointBelow(new Point(initial.x, initial.y - 50));
      return ret == null ? fallback : ret;
   }

   private void dropExpCard(MapleCharacter attacker, MapleMonster mob) {
      boolean check = true;
      double eliteRate = 1.0;
      if (check) {
         long exp = mob.getMobExp();
         if (exp > 0L) {
            Integer holySymbol = attacker.getBuffedValue(SecondaryStatFlag.HolySymbol);
            if (attacker.hasDisease(SecondaryStatFlag.Curse)) {
               exp /= 2L;
            }

            double rate = GameServer.getInstance(this.getChannel()).getExpRate();
            if (Center.sunShineStorage.bloomFlower
                  && (Center.sunShineStorage.randomBuff == 1 || Center.sunShineStorage.randomBuff == 2
                        || Center.sunShineStorage.randomBuff == 3)) {
               rate *= 1.5;
            }

            if (ServerConstants.useExpRateByLevel) {
               for (int next = 0; next < ServerConstants.expRateChangeLevels.length; next++) {
                  if (next == ServerConstants.expRateChangeLevels.length - 1) {
                     if (attacker.getLevel() >= ServerConstants.expRateChangeLevels[next]) {
                        rate *= ServerConstants.expRateByLevels[next];
                     }
                     break;
                  }

                  if (attacker.getLevel() >= ServerConstants.expRateChangeLevels[next]
                        && attacker.getLevel() < ServerConstants.expRateChangeLevels[next + 1]) {
                     rate *= ServerConstants.expRateByLevels[next];
                     break;
                  }
               }
            }

            if (GameConstants.isYetiPinkBean(attacker.getJob())) {
               rate = 30.0;
            }

            double expRate = attacker.getLevel() < 10 ? GameConstants.getExpRate_Below10(attacker.getJob()) : rate;
            exp = (long) (exp * attacker.getEXPMod() * expRate);
            int addExpRate = (int) (attacker.getStat().plusExpRate * (exp * 0.01));
            exp = (long) (exp * (attacker.getStat().expBuff / 100.0));
            int addExp = (int) (attacker.getStat().plusExp * 0.01 * (exp * 0.01));
            if (mob.getStats().getLevel() >= 101 && mob.getStats().getLevel() <= 200
                  && attacker.getStat().expGuild > 0) {
               addExp += (int) (exp * 0.01 * attacker.getStat().expGuild);
            }

            addExp += addExpRate;
            exp = (long) (exp * eliteRate);
            if (ServerConstants.currentHottimeRate > 1.0) {
               exp = (long) (exp * ServerConstants.currentHottimeRate);
            }

            long Equipment_Bonus_EXP = (long) (exp / 100.0 * attacker.getStat().equipmentBonusExp);
            if (attacker.getStat().equippedFairy > 0 && attacker.getFairyExp() > 0) {
               Equipment_Bonus_EXP += (long) (exp / 100.0 * attacker.getFairyExp());
            }

            if (attacker.getRoadRingExpBoost() > 0) {
               Equipment_Bonus_EXP += (long) (exp / 100.0 * attacker.getRoadRingExpBoost());
            }

            if (attacker.getGuildBonusExpBoost() > 0) {
               Equipment_Bonus_EXP += (long) (exp / 100.0 * attacker.getGuildBonusExpBoost());
            }

            if (attacker.getDonatorBonusExpBoost() > 0) {
               Equipment_Bonus_EXP += (long) (exp / 100.0 * attacker.getDonatorBonusExpBoost());
            }

            long BuffBonusEXP = 0L;
            if (holySymbol != null) {
               BuffBonusEXP += (int) (exp / 100.0 * holySymbol.intValue());
            }

            Integer dice = attacker.getBuffedValue(SecondaryStatFlag.Dice);
            if (dice != null) {
               int[] diceStat = (int[]) attacker.getJobField("diceStatData");
               int diceBonusExpRate = diceStat[16];
               if (diceBonusExpRate > 0) {
                  BuffBonusEXP += (long) (exp / 100.0 * diceBonusExpRate);
               }
            }

            long Field_Bonus_EXP = 0L;
            if (this.getBreakTimeFieldExpRate() > 0) {
               Field_Bonus_EXP = (long) (exp * (this.getBreakTimeFieldExpRate() * 0.01));
            }

            long Event_Bonus_EXP = 0L;
            if (ServerConstants.dailyEventType == DailyEventType.ExpRateFever) {
               Event_Bonus_EXP = (long) (exp / 100.0 * (DBConfig.isGanglim ? 50 : 20));
            }

            if (ServerConstants.dailyEventType == DailyEventType.ExpRateFever_) {
               Event_Bonus_EXP = (long) (exp / 100.0 * 100.0);
            }

            exp += addExp;
            MobTemporaryStatEffect ms = mob.getBuff(MobTemporaryStatFlag.SHOWDOWN);
            if (ms != null) {
               exp += (long) (exp * (ms.getX().intValue() / 100.0));
            }

            if (mob.getBuff(MobTemporaryStatFlag.SEPERATE_SOUL_P) != null) {
               exp += exp;
            }

            exp += Equipment_Bonus_EXP + Event_Bonus_EXP + Field_Bonus_EXP + BuffBonusEXP;
            Item idrop = new Item(2436496, (short) 0, (short) 1, 0);
            Point pos = new Point(mob.getTruePosition());
            byte d = 1;
            pos.x += d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2));
            this.spawnMobDrop(idrop, this.calcDropPos(pos, mob.getTruePosition()), mob, attacker, (byte) 0, 0, exp);
         }
      }
   }

   private void dropFromMonster(MapleCharacter chr, MapleMonster mob, boolean instanced) {
      this.dropFromMonster(chr, mob, instanced, true);
   }

   private void dropFromMonster(MapleCharacter chr, MapleMonster mob, boolean instanced, boolean mesoDrop) {
      chr.getStat().getLock().writeLock().lock();

      try {
         if (chr.getBossMode() == 1) {
            return;
         }

         if (mob == null
               || chr == null
               || GameServer.getInstance(this.channel) == null
               || this.dropsDisabled
               || mob.dropsDisabled()
               || chr.getPyramidSubway() != null) {
            return;
         }

         if (this.getId() == 993014200) {
            return;
         }

         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         byte droptype = (byte) (mob.getStats().isExplosiveReward() ? 3
               : (mob.getStats().isFfaLoot() ? 2 : (chr.getParty() != null ? 1 : 0)));
         int mobpos = mob.getTruePosition().x;
         double cmServerrate = GameServer.getInstance(this.channel).getMesoRate();
         double chServerrate = GameServer.getInstance(this.channel).getDropRate();
         double caServerrate = GameServer.getInstance(this.channel).getCashRate();
         if (Center.sunShineStorage.bloomFlower) {
            if (Center.sunShineStorage.randomBuff == 2) {
               if (!mob.getStats().isBoss()) {
                  chServerrate *= 1.5;
               }
            } else if (Center.sunShineStorage.randomBuff == 3 || Center.sunShineStorage.randomBuff == 4) {
               cmServerrate *= 1.5;
            } else if (Center.sunShineStorage.randomBuff == 5) {
               cmServerrate *= 1.5;
               if (!mob.getStats().isBoss()) {
                  chServerrate *= 1.5;
               }
            }
         }

         if (ServerConstants.dailyEventType == DailyEventType.DropRateFever) {
            chServerrate += DBConfig.isGanglim ? 0.5 : 0.2;
         }

         Item idrop = null;
         byte d = 1;
         Point pos = new Point(0, mob.getTruePosition().y);
         double showdown = 100.0;
         MobTemporaryStatEffect mse = mob.getBuff(MobTemporaryStatFlag.SHOWDOWN);
         if (mse != null) {
            showdown += mse.getX().intValue();
         }

         List<MonsterDropEntry> customs = new ArrayList<>();
         if (DBConfig.isGanglim) {
            if (mob.getId() == 9833971) {
               customs.add(new MonsterDropEntry(4310237, 1000000, 1, 3, 0));
               customs.add(new MonsterDropEntry(4310237, 100000, 1, 3, 0));
               customs.add(new MonsterDropEntry(4310237, 100000, 1, 3, 0));
               customs.add(new MonsterDropEntry(4310266, 1000000, 1, 1, 0));
               customs.add(new MonsterDropEntry(4310266, 100000, 1, 1, 0));
            }

            if (this.getId() >= 410000500 && this.getId() <= 410002000
                  || this.getId() >= 410003000 && this.getId() <= 410004000
                  || this.getId() >= 410007000 && this.getId() <= 410008000) {
               customs.add(new MonsterDropEntry(2633616, 1000, 1, 1, 0));
            }
         }

         if (this.getId() == 921170050 || this.getId() == 921170100) {
            HungryMuto muto = chr.getHungryMuto();
            if (muto == null) {
               return;
            }

            int itemID = 0;
            int baseMobID = 8642000;
            int baseItemID = 2435856;
            itemID = baseItemID + (mob.getId() - baseMobID);
            if (Randomizer.nextInt(100) <= muto.getDropRate(itemID)) {
               idrop = new Item(itemID, (short) 0, (short) 1, 0);
               pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
               this.spawnMobDrop(idrop, this.calcDropPos(pos, mob.getTruePosition()), mob, chr, (byte) 0, 0);
            }

            return;
         }

         if (!DBConfig.isGanglim && mob.getEliteMobGrade() != EliteGrade.None) {
            idrop = new Item(2432407, (short) 0, (short) 1, 0);
            Date d1 = new Date();
            Date d2 = new Date(d1.getYear(), d1.getMonth(), d1.getDate(), 23, 59, 59);
            idrop.setExpiration(d2.getTime());
            pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
            this.spawnMobDrop(idrop, this.calcDropPos(pos, mob.getTruePosition()), mob, chr, (byte) 0, 0);
         }

         MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
         List<MonsterDropEntry> derp = mi.retrieveDrop(mob.getId());
         List<MonsterDropEntry> dropEntry;
         if (derp != null) {
            dropEntry = new ArrayList<>(derp);
         } else {
            dropEntry = new ArrayList<>();
         }

         if (DBConfig.isGanglim && !customs.isEmpty()) {
            dropEntry.addAll(customs);
         }

         IntensePowerCrystalData data = GameConstants.getIntensePowerCrystalData(mob.getId());
         if (data != null) {
            for (MapleCharacter player : this.getCharactersThreadsafe()) {
               if (player.getParty() != null && player.getBossMode() == 0) {
                  player.dropIntensePowerCrystal(mob);
               }
            }
         }

         if (DBConfig.isGanglim && GameConstants.isHellInstance(this.getFieldSetInstance())) {
            List<MonsterDropEntry> hellDropMultiplierEntry = new ArrayList<>();
            List<Integer> dropAddReward = new ArrayList<>();
            if (this.getFieldSetInstance() instanceof HellBlackHeavenBoss) {
               int[] rewards = new int[] {
                     4001843, 2433593, 1012632, 2048717, 2630291, 2048717, 2711003, 2711004, 4310229, 4031227, 4310308,
                     4001877, 4310237, 4310266
               };

               for (int reward : rewards) {
                  dropAddReward.add(reward);
               }
            }

            if (this.getFieldSetInstance() instanceof HellDemianBoss) {
               int[] rewards = new int[] {
                     4001869, 2435369, 2630291, 1662088, 1099015, 1022278, 2048717, 2711003, 2711004, 4310229, 4031227,
                     4310308, 4310237, 4310266
               };

               for (int reward : rewards) {
                  dropAddReward.add(reward);
               }
            }

            if (this.getFieldSetInstance() instanceof HellLucidBoss) {
               int[] rewards = new int[] {
                     4001879, 1143029, 1662111, 1132308, 2630782, 2048717, 2711003, 2711004, 4310229, 2436039, 4031227,
                     4310308, 4310266, 2430894
               };

               for (int reward : rewards) {
                  dropAddReward.add(reward);
               }
            }

            if (this.getFieldSetInstance() instanceof HellWillBoss) {
               int[] rewards = new int[] { 4310308, 2048717, 4310237, 4310266, 2438396, 4001890, 2438412, 2438411,
                     2630782, 2430945, 1162080, 1162081, 1162082 };

               for (int reward : rewards) {
                  dropAddReward.add(reward);
               }
            }

            if (this.getFieldSetInstance() instanceof HellWillBoss) {
               int[] rewards = new int[] { 4310308, 2048717, 4310237, 4310266, 4009005, 4031466, 2439568, 1032316 };

               for (int reward : rewards) {
                  dropAddReward.add(reward);
               }
            }

            if (!dropAddReward.isEmpty()) {
               for (MonsterDropEntry de : dropEntry) {
                  if (dropAddReward.contains(de.itemId)) {
                     hellDropMultiplierEntry.add(de);
                  }
               }
            }

            if (!hellDropMultiplierEntry.isEmpty()) {
               for (MonsterDropEntry hde : hellDropMultiplierEntry) {
                  MonsterDropEntry hellAddDrop = new MonsterDropEntry(hde.itemId, hde.chance * 2, hde.Minimum,
                        hde.Maximum, hde.questid);
                  dropEntry.add(hellAddDrop);
               }
            }
         }

         Collections.shuffle(dropEntry);
         if (chr.isEquippedSoulWeapon()) {
            dropEntry.add(new MonsterDropEntry(4001536, 1000000, 1, Randomizer.rand(1, 3), 0));
         }

         if (DBConfig.isGanglim && chr.getLevel() >= 260 && this.getFieldSetInstance() == null) {
            dropEntry.add(new MonsterDropEntry(4009547, 2000, 1, 1, 0));
            dropEntry.add(new MonsterDropEntry(2636420, 4000, 1, 1, 0));
            dropEntry.add(new MonsterDropEntry(2636422, 100, 1, 1, 0));
         }

         if (!DBConfig.isGanglim) {
            if (mob.getId() == 8800002
                  || mob.getId() == 8840007
                  || mob.getId() == 8840000
                  || mob.getId() == 8840014
                  || mob.getId() == 8910100
                  || mob.getId() == 8900103
                  || mob.getId() == 8920106
                  || mob.getId() == 8930100
                  || mob.getId() == 8820001) {
               dropEntry.add(new MonsterDropEntry(2512292, 5000, 1, 1, 0));
               dropEntry.add(new MonsterDropEntry(2512293, 5000, 1, 1, 0));
            }

            if (GameConstants.isHellInstance(this.getFieldSetInstance())) {
               if (mob.getId() == 8950110) {
                  dropEntry.add(new MonsterDropEntry(2644200, 880, 1, 1, 0));
               }

               if (mob.getId() == 8950112) {
                  dropEntry.add(new MonsterDropEntry(2644201, 880, 1, 1, 0));
               }

               if (mob.getId() == 8880177) {
                  dropEntry.add(new MonsterDropEntry(2644202, 880, 1, 1, 0));
               }
            }
         }

         boolean mesoDropped = false;
         chServerrate *= ServerConstants.dropFeverRate;
         List<Integer> filterItems = new ArrayList<>();
         boolean dropSingleMode = !chr.isMultiMode();
         boolean dropCube = false;

         label2184: for (MonsterDropEntry dex : dropEntry) {
            double curse = 1.0;
            if (this.getEliteBossCurseLevel() > 0) {
               int r = GameConstants.getCursedRunesRate(this.getEliteBossCurseLevel());
               curse -= r * 0.01;
            }

            List<MapleCharacter> characterList = new ArrayList<>();
            if (!dex.individual && (!DBConfig.isGanglim
                  || !mob.getStats().isBoss() && dex.itemId != 2434851 && mob.getScale() <= 100)) {
               characterList.add(chr);
            } else {
               characterList.addAll(chr.getMap().getCharacters());
            }

            Iterator cchr = characterList.iterator();

            while (true) {
               MapleCharacter cchrx;
               while (true) {
                  if (!cchr.hasNext()) {
                     continue label2184;
                  }

                  cchrx = (MapleCharacter) cchr.next();
                  if (cchrx.getBuffedValue(SecondaryStatFlag.RuneBlocked) != null) {
                     curse = 1.0;
                  }

                  int max = 300;
                  if (DBConfig.isGanglim) {
                     max = mob.getStats().isBoss() ? 300 : 400;
                  }

                  double dropBuff = Math.min((double) max, cchrx.getStat().dropBuff);
                  double extraBuff = cchrx.getStat().extraDropBuff - 100.0;
                  double dropRatef = dropBuff + extraBuff;
                  int chance = (int) (dex.chance
                        * chServerrate
                        * (this.isAffectedByDropBuff(dex.itemId)
                              ? cchrx.getDropMod() * (dropRatef / 100.0) * (showdown / 100.0)
                              : 1.0)
                        * curse);
                  if (Center.sunShineStorage.bloomFlower
                        && (Center.sunShineStorage.randomBuff == 1 || Center.sunShineStorage.randomBuff == 4)
                        && GameConstants.isArcaneSymbol(dex.itemId)) {
                     chance = (int) (chance * 1.5);
                  }

                  if (Randomizer.nextInt(999999) < chance
                        && (!mesoDropped || droptype == 3 || dex.itemId != 0)
                        && (dex.questid <= 0 || cchrx.getQuestStatus(dex.questid) == 1)
                        && (dex.questid <= 0 || cchrx.needQuestItem(dex.questid, dex.itemId))) {
                     if (dex.itemId != 4036454) {
                        break;
                     }

                     if (this.getFieldSetInstance() == null
                           || this.getFieldSetInstance() instanceof HellLucidBoss
                           || this.getFieldSetInstance() instanceof HellDemianBoss
                           || this.getFieldSetInstance() instanceof HellBlackHeavenBoss
                           || this.getFieldSetInstance() instanceof HellWillBoss
                           || this.getFieldSetInstance() instanceof HellDunkelBoss) {
                        dex.Minimum = 1;
                        dex.Maximum = 2;
                        break;
                     }
                  }
               }

               if (!DBConfig.isGanglim && (dex.itemId == 5062009 || dex.itemId == 5062503)) {
                  if (this.getFieldSetInstance() == null
                        || !(this.getFieldSetInstance() instanceof HellLucidBoss)
                              && !(this.getFieldSetInstance() instanceof HellDemianBoss)
                              && !(this.getFieldSetInstance() instanceof HellBlackHeavenBoss)
                              && !(this.getFieldSetInstance() instanceof HellWillBoss)
                              && !(this.getFieldSetInstance() instanceof HellDunkelBoss)) {
                     continue;
                  }

                  if (!DBConfig.isGanglim
                        && (this.getFieldSetInstance() instanceof HellLucidBoss
                              || this.getFieldSetInstance() instanceof HellDemianBoss
                              || this.getFieldSetInstance() instanceof HellBlackHeavenBoss
                              || this.getFieldSetInstance() instanceof HellWillBoss
                              || this.getFieldSetInstance() instanceof HellDunkelBoss)) {
                     dropCube = true;
                     continue;
                  }
               }

               if (DBConfig.isGanglim
                     || !chr.isMultiMode()
                     || dex.itemId != 5062500
                     || this.getFieldSetInstance() == null
                     || !(this.getFieldSetInstance() instanceof HellLucidBoss)
                           && !(this.getFieldSetInstance() instanceof HellDemianBoss)
                           && !(this.getFieldSetInstance() instanceof HellBlackHeavenBoss)
                           && !(this.getFieldSetInstance() instanceof HellWillBoss)
                           && !(this.getFieldSetInstance() instanceof HellDunkelBoss)) {
                  if ((dex.itemId >= 2000000
                        || this.getFieldSetInstance() == null
                        || !(this.getFieldSetInstance() instanceof HellLucidBoss)
                              && !(this.getFieldSetInstance() instanceof HellDemianBoss)
                              && !(this.getFieldSetInstance() instanceof HellBlackHeavenBoss)
                              && !(this.getFieldSetInstance() instanceof HellWillBoss)
                              && !(this.getFieldSetInstance() instanceof HellDunkelBoss))
                        && (dex.itemId / 10000 != 238 || mob.getStats().isBoss())
                        && (!GameConstants.isArcaneWeaponArmor(dex.itemId) || !Randomizer.isSuccess(70))) {
                     if (droptype == 3) {
                        pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
                     } else {
                        pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
                     }

                     if (dex.itemId == 0 && mesoDrop && !DBConfig.isGanglim) {
                        int mesos = Randomizer.nextInt(1 + Math.abs(dex.Maximum - dex.Minimum)) + dex.Minimum;
                        if (mesos > 0) {
                           double mesoBuff = ServerConstants.currentMesoHottimeRate;
                           mesoBuff *= ServerConstants.mesoFeverRate;
                           if (ServerConstants.dailyEventType == DailyEventType.MesoRateFever) {
                              mesoBuff += 0.2;
                           }

                           this.spawnMobMesoDrop(
                                 (int) (mesos * (Math.min(480.0, cchrx.getStat().mesoBuff) / 100.0) * cchrx.getDropMod()
                                       * mesoBuff * cmServerrate),
                                 this.calcDropPos(pos, mob.getTruePosition()),
                                 mob,
                                 cchrx,
                                 false,
                                 droptype);
                           mesoDropped = true;
                        }
                     } else {
                        if (GameConstants.isArcaneWeaponArmor(dex.itemId)) {
                           int itemID = 2633914 + Randomizer.rand(0, 1);
                           idrop = new Item(itemID, (short) 0, (short) 1, 0);
                        } else if (GameConstants.getInventoryType(dex.itemId) != MapleInventoryType.EQUIP) {
                           int range = Math.abs(dex.Maximum - dex.Minimum);
                           int itemID = dex.itemId;
                           idrop = new Item(itemID, (short) 0,
                                 (short) (dex.Maximum != 1
                                       ? Randomizer.nextInt(range <= 0 ? 1 : range) + 1 + dex.Minimum
                                       : 1),
                                 0);
                        } else {
                           idrop = ii.randomizeStats((Equip) ii.getEquipById(dex.itemId));
                           if (GameConstants.isArcaneSymbol(idrop.getItemId())) {
                              Equip equip = (Equip) idrop;
                              if (equip.getArcLevel() < 1) {
                                 equip.setArc(30);
                                 equip.setArcLevel(1);
                                 equip.setArcEXP(1);
                                 if ((cchrx.getJob() < 100 || cchrx.getJob() >= 200)
                                       && cchrx.getJob() != 512
                                       && cchrx.getJob() != 1512
                                       && cchrx.getJob() != 2512
                                       && (cchrx.getJob() < 1100 || cchrx.getJob() >= 1200)
                                       && !GameConstants.isAran(cchrx.getJob())
                                       && !GameConstants.isBlaster(cchrx.getJob())
                                       && !GameConstants.isDemonSlayer(cchrx.getJob())
                                       && !GameConstants.isMichael(cchrx.getJob())
                                       && !GameConstants.isKaiser(cchrx.getJob())
                                       && !GameConstants.isZero(cchrx.getJob())
                                       && !GameConstants.isArk(cchrx.getJob())
                                       && !GameConstants.isAdele(cchrx.getJob())) {
                                    if ((cchrx.getJob() < 200 || cchrx.getJob() >= 300)
                                          && !GameConstants.isFlameWizard(cchrx.getJob())
                                          && !GameConstants.isEvan(cchrx.getJob())
                                          && !GameConstants.isLuminous(cchrx.getJob())
                                          && (cchrx.getJob() < 3200 || cchrx.getJob() >= 3300)
                                          && !GameConstants.isKinesis(cchrx.getJob())
                                          && !GameConstants.isIllium(cchrx.getJob())
                                          && !GameConstants.isLara(cchrx.getJob())) {
                                       if (!GameConstants.isKain(cchrx.getJob())
                                             && (cchrx.getJob() < 300 || cchrx.getJob() >= 400)
                                             && cchrx.getJob() != 522
                                             && cchrx.getJob() != 532
                                             && !GameConstants.isMechanic(cchrx.getJob())
                                             && !GameConstants.isAngelicBuster(cchrx.getJob())
                                             && (cchrx.getJob() < 1300 || cchrx.getJob() >= 1400)
                                             && !GameConstants.isMercedes(cchrx.getJob())
                                             && (cchrx.getJob() < 3300 || cchrx.getJob() >= 3400)) {
                                          if ((cchrx.getJob() < 400 || cchrx.getJob() >= 500)
                                                && (cchrx.getJob() < 1400 || cchrx.getJob() >= 1500)
                                                && !GameConstants.isPhantom(cchrx.getJob())
                                                && !GameConstants.isKadena(cchrx.getJob())
                                                && !GameConstants.isHoyoung(cchrx.getJob())
                                                && !GameConstants.isKhali(cchrx.getJob())) {
                                             if (GameConstants.isDemonAvenger(cchrx.getJob())) {
                                                equip.setHp((short) 630);
                                             } else if (GameConstants.isXenon(cchrx.getJob())) {
                                                equip.setStr((short) 144);
                                                equip.setDex((short) 144);
                                                equip.setLuk((short) 144);
                                             }
                                          } else {
                                             equip.setLuk((short) 300);
                                          }
                                       } else {
                                          equip.setDex((short) 300);
                                       }
                                    } else {
                                       equip.setInt((short) 300);
                                    }
                                 } else {
                                    equip.setStr((short) 300);
                                 }
                              }
                           }

                           if (GameConstants.isAuthenticSymbol(idrop.getItemId())) {
                              Equip equip = (Equip) idrop;
                              if (equip.getArcLevel() < 1) {
                                 equip.setArc(10);
                                 equip.setArcLevel(1);
                                 equip.setArcEXP(1);
                                 if ((cchrx.getJob() < 100 || cchrx.getJob() >= 200)
                                       && cchrx.getJob() != 512
                                       && cchrx.getJob() != 1512
                                       && cchrx.getJob() != 2512
                                       && (cchrx.getJob() < 1100 || cchrx.getJob() >= 1200)
                                       && !GameConstants.isAran(cchrx.getJob())
                                       && !GameConstants.isBlaster(cchrx.getJob())
                                       && !GameConstants.isDemonSlayer(cchrx.getJob())
                                       && !GameConstants.isMichael(cchrx.getJob())
                                       && !GameConstants.isKaiser(cchrx.getJob())
                                       && !GameConstants.isZero(cchrx.getJob())
                                       && !GameConstants.isArk(cchrx.getJob())
                                       && !GameConstants.isAdele(cchrx.getJob())) {
                                    if ((cchrx.getJob() < 200 || cchrx.getJob() >= 300)
                                          && !GameConstants.isFlameWizard(cchrx.getJob())
                                          && !GameConstants.isEvan(cchrx.getJob())
                                          && !GameConstants.isLuminous(cchrx.getJob())
                                          && (cchrx.getJob() < 3200 || cchrx.getJob() >= 3300)
                                          && !GameConstants.isKinesis(cchrx.getJob())
                                          && !GameConstants.isIllium(cchrx.getJob())
                                          && !GameConstants.isLara(cchrx.getJob())) {
                                       if (!GameConstants.isKain(cchrx.getJob())
                                             && (cchrx.getJob() < 300 || cchrx.getJob() >= 400)
                                             && cchrx.getJob() != 522
                                             && cchrx.getJob() != 532
                                             && !GameConstants.isMechanic(cchrx.getJob())
                                             && !GameConstants.isAngelicBuster(cchrx.getJob())
                                             && (cchrx.getJob() < 1300 || cchrx.getJob() >= 1400)
                                             && !GameConstants.isMercedes(cchrx.getJob())
                                             && (cchrx.getJob() < 3300 || cchrx.getJob() >= 3400)) {
                                          if ((cchrx.getJob() < 400 || cchrx.getJob() >= 500)
                                                && (cchrx.getJob() < 1400 || cchrx.getJob() >= 1500)
                                                && !GameConstants.isPhantom(cchrx.getJob())
                                                && !GameConstants.isKadena(cchrx.getJob())
                                                && !GameConstants.isHoyoung(cchrx.getJob())
                                                && !GameConstants.isKhali(cchrx.getJob())) {
                                             if (GameConstants.isDemonAvenger(cchrx.getJob())) {
                                                equip.setHp((short) 1050);
                                             } else if (GameConstants.isXenon(cchrx.getJob())) {
                                                equip.setStr((short) 240);
                                                equip.setDex((short) 240);
                                                equip.setLuk((short) 240);
                                             }
                                          } else {
                                             equip.setLuk((short) 500);
                                          }
                                       } else {
                                          equip.setDex((short) 500);
                                       }
                                    } else {
                                       equip.setInt((short) 500);
                                    }
                                 } else {
                                    equip.setStr((short) 500);
                                 }
                              }
                           }
                        }

                        boolean filterFind = false;

                        for (Integer id : filterItems) {
                           if (id == idrop.getItemId()) {
                              filterFind = true;
                              break;
                           }
                        }

                        if (filterFind) {
                           continue;
                        }

                        if (idrop.getItemId() == 2633914 || idrop.getItemId() == 2633915) {
                           filterItems.add(idrop.getItemId());
                        }

                        if (idrop.getUniqueId() < 0L && ii.isCash(idrop.getItemId())) {
                           idrop.setUniqueId(MapleInventoryIdentifier.getInstance());
                        }

                        idrop.setGMLog(
                              CurrentTime.getAllCurrentTime() + "에 " + cchrx.getName() + "이(가) " + mob.getId()
                                    + " 몬스터로부터 얻은 아이템 (맵ID : " + this.mapid + ")");
                        this.spawnMobDrop(idrop, this.calcDropPos(pos, mob.getTruePosition()), mob, cchrx, droptype,
                              dex.questid, dex.individual);
                     }

                     d++;
                  }
               } else {
                  dropCube = true;
               }
            }
         }

         List<MonsterGlobalDropEntry> globalEntry = new ArrayList<>(mi.getGlobalDrop());
         Collections.shuffle(globalEntry);
         int cashz = (int) ((mob.getStats().isBoss() && mob.getStats().getHPDisplayType() == 0 ? 20 : 1)
               * caServerrate);
         int cashModifier = (int) (mob.getStats().isBoss()
               ? (mob.getStats().isPartyBonus() ? mob.getMobExp() / 1000L : 0L)
               : mob.getMobExp() / 1000L + mob.getMobMaxHp() / 20000L);

         for (MonsterGlobalDropEntry dex : globalEntry) {
            List<MapleCharacter> characterList = new ArrayList<>();
            if (dex.individual) {
               characterList.addAll(chr.getMap().getCharacters());
            } else {
               characterList.add(chr);
            }

            for (MapleCharacter cchrx : characterList) {
               if (Randomizer.nextInt(999999) < dex.chance
                     && (dex.continent < 0
                           || dex.continent < 10 && this.mapid / 100000000 == dex.continent
                           || dex.continent < 100 && this.mapid / 10000000 == dex.continent
                           || dex.continent < 1000 && this.mapid / 1000000 == dex.continent)) {
                  if (droptype == 3) {
                     pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
                  } else {
                     pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
                  }

                  if ((dex.questid <= 0 || cchrx.getQuestStatus(dex.questid) == 1)
                        && (dex.questid <= 0 || cchrx.needQuestItem(dex.questid, dex.itemId))) {
                     if (dex.itemId == 0) {
                        int min = 0;
                        int maxx = 0;
                        short mobLevel = mob.getStats().getLevel();
                        if (mobLevel < 11) {
                           min = 50;
                           maxx = 90;
                        } else if (mobLevel < 21) {
                           min = 100;
                           maxx = 200;
                        } else if (mobLevel < 31) {
                           min = 300;
                           maxx = 400;
                        } else if (mobLevel < 41) {
                           min = 500;
                           maxx = 700;
                        } else if (mobLevel < 51) {
                           min = 800;
                           maxx = 1000;
                        } else if (mobLevel < 61) {
                           min = 1200;
                           maxx = 1500;
                        } else if (mobLevel < 71) {
                           min = 1600;
                           maxx = 1800;
                        } else if (mobLevel < 81) {
                           min = 2000;
                           maxx = 2400;
                        } else if (mobLevel < 91) {
                           min = 2200;
                           maxx = 2600;
                        } else if (mobLevel < 101) {
                           min = 2200;
                           maxx = 2600;
                        } else if (mobLevel < 111) {
                           min = 2400;
                           maxx = 2800;
                        } else if (mobLevel < 121) {
                           min = 2600;
                           maxx = 3000;
                        } else if (mobLevel < 131) {
                           min = 3000;
                           maxx = 3500;
                        } else if (mobLevel < 141) {
                           min = 3300;
                           maxx = 3900;
                        } else if (mobLevel < 151) {
                           min = 3800;
                           maxx = 4200;
                        } else if (mobLevel < 161) {
                           min = 4500;
                           maxx = 5200;
                        } else if (mobLevel < 171) {
                           min = 5000;
                           maxx = 5800;
                        } else if (mobLevel < 181) {
                           min = 5500;
                           maxx = 6200;
                        } else if (mobLevel < 191) {
                           min = 6000;
                           maxx = 6800;
                        } else if (mobLevel < 201) {
                           min = 6500;
                           maxx = 7300;
                        } else if (mobLevel < 211) {
                           min = 7000;
                           maxx = 7800;
                        } else if (mobLevel < 221) {
                           min = 7600;
                           maxx = 8200;
                        } else if (mobLevel < 231) {
                           min = 8500;
                           maxx = 9200;
                        } else if (mobLevel < 241) {
                           min = 9600;
                           maxx = 11000;
                        } else if (mobLevel < 251) {
                           min = 11500;
                           maxx = 12500;
                        } else if (mobLevel < 261) {
                           min = 12500;
                           maxx = 13500;
                        } else if (mobLevel < 271) {
                           min = 13500;
                           maxx = 14500;
                        } else if (mobLevel < 301) {
                           min = 14500;
                           maxx = 15500;
                        }

                        int mesos = Randomizer.nextInt(1 + Math.abs(maxx - min)) + min;
                        if (mesos > 0) {
                           double mesoBuff = ServerConstants.currentMesoHottimeRate;
                           mesoBuff *= ServerConstants.mesoFeverRate;
                           if (ServerConstants.dailyEventType == DailyEventType.MesoRateFever) {
                              mesoBuff += DBConfig.isGanglim ? 1.0 : 0.2;
                           }

                           this.spawnMobMesoDrop(
                                 (int) (mesos * (Math.min(480.0, cchrx.getStat().mesoBuff) / 100.0) * cchrx.getDropMod()
                                       * mesoBuff * cmServerrate),
                                 this.calcDropPos(pos, mob.getTruePosition()),
                                 mob,
                                 cchrx,
                                 false,
                                 droptype);
                           mesoDropped = true;
                        }
                     } else if (!this.gDropsDisabled) {
                        if (GameConstants.getInventoryType(dex.itemId) == MapleInventoryType.EQUIP) {
                           idrop = ii.randomizeStats((Equip) ii.getEquipById(dex.itemId));
                        } else {
                           idrop = new Item(
                                 dex.itemId, (short) 0,
                                 (short) (dex.Maximum != 1 ? Randomizer.nextInt(dex.Maximum - dex.Minimum) + dex.Minimum
                                       : 1),
                                 0);
                        }

                        if (idrop.getUniqueId() < 0L && ii.isCash(idrop.getItemId())) {
                           idrop.setUniqueId(MapleInventoryIdentifier.getInstance());
                        }

                        idrop.setGMLog(
                              CurrentTime.getAllCurrentTime()
                                    + "에 "
                                    + cchrx.getName()
                                    + "이(가) "
                                    + mob.getId()
                                    + " 몬스터로부터 얻은 아이템 (맵ID : "
                                    + this.mapid
                                    + ") (글로벌 드롭)");
                        this.spawnMobDrop(idrop, this.calcDropPos(pos, mob.getTruePosition()), mob, cchrx, droptype,
                              dex.questid, dex.individual);
                        d++;
                     }
                  }
               }
            }
         }

         if (DBConfig.isGanglim) {
            if (mob.getId() == 8950110
                  || mob.getId() == 8950112
                  || mob.getId() == 8950115
                  || mob.getId() == 8880177
                  || mob.getId() == 8950121
                  || mob.getId() == 8880518
                  || mob.getId() == 8880614) {
               for (MapleCharacter cchrxx : new ArrayList<>(chr.getMap().getCharacters())) {
                  int random = ThreadLocalRandom.current().nextInt(100);
                  int dropitemid = -1;
                  int dropitemqty = -1;
                  if (mob.getId() == 8950121 || mob.getId() == 8880518 || mob.getId() == 8880614) {
                     if (random < 20) {
                        dropitemid = 2439995;
                        dropitemqty = 1;
                     } else if (random < 25) {
                        dropitemid = 2439996;
                        dropitemqty = 1;
                     }
                  }

                  if (GameConstants.isHellInstance(this.getFieldSetInstance())
                        && (mob.getId() == 8950110 || mob.getId() == 8950112 || mob.getId() == 8880177
                              || mob.getId() == 8950115)) {
                     if (random < 50) {
                        dropitemid = 2439995;
                        dropitemqty = 10;
                     } else {
                        dropitemid = 2439996;
                        dropitemqty = 5;
                     }
                  }

                  if (dropitemid != -1) {
                     idrop = new Item(dropitemid, (short) 0, (short) dropitemqty, 0);
                     if (idrop.getUniqueId() < 0L && ii.isCash(idrop.getItemId())) {
                        idrop.setUniqueId(MapleInventoryIdentifier.getInstance());
                     }

                     idrop.setGMLog(
                           CurrentTime.getAllCurrentTime()
                                 + "에 "
                                 + cchrxx.getName()
                                 + "이(가) "
                                 + mob.getId()
                                 + " 몬스터로부터 얻은 아이템 (맵ID : "
                                 + this.mapid
                                 + ") (보스 개인 드롭)");
                     this.spawnMobDrop(idrop, this.calcDropPos(pos, mob.getTruePosition()), mob, cchrxx, droptype, 0,
                           true);
                  }
               }
            }

            if (GameConstants.isHellInstance(this.getFieldSetInstance())
                  && (mob.getId() == 8950110 || mob.getId() == 8950112 || mob.getId() == 8880177
                        || mob.getId() == 8950115 || mob.getId() == 8950119)) {
               int mapID = 0;
               if (mob.getMap() != null) {
                  mapID = mob.getMap().getId();
               } else if (chr.getMap() != null) {
                  mapID = chr.getMapId();
               }

               int partySize = chr.getPartyMembersSameMap() == null ? 1 : chr.getPartyMembersSameMap().size();

               for (MapleCharacter partyMember : chr.getPartyMembers()) {
                  if (partyMember.getMapId() == mapID) {
                     int successRate = 55;
                     switch (partySize) {
                        case 1:
                           successRate = 55;
                           break;
                        case 2:
                           if (partyMember.isLeader()) {
                              successRate = 50;
                           } else {
                              successRate = 40;
                           }
                           break;
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                           if (partyMember.isLeader()) {
                              successRate = 50;
                           } else {
                              successRate = 30;
                           }
                     }

                     if (Randomizer.isSuccess(successRate)) {
                        int blueball = 4031228;
                        int qty = 1;
                        if (mob.getId() == 8950115 || mob.getId() == 8950119) {
                           qty = 2;
                        }

                        if (partyMember.getInventory(GameConstants.getInventoryType(blueball)).getNumFreeSlot() < 1) {
                           partyMember.dropMessage(-22, "인벤토리 빈공간이 부족하여 헬 보스 보상 푸른 구슬을 얻지 못하였습니다.");
                        } else {
                           MapleInventoryManipulator.addById(partyMember.getClient(), blueball, (short) qty,
                                 "헬모드 보상으로 획득한 아이템");
                           partyMember.dropMessage(-22, "헬 보스 보상으로 푸른 구슬을 " + qty + "개 획득하였습니다.");
                        }
                     } else {
                        partyMember.dropMessage(-22, "헬 보스 보상으로 푸른 구슬을 획득하는데 실패하였습니다.");
                     }
                  }
               }
            }

            if (GameConstants.isHellInstance(this.getFieldSetInstance())) {
               for (MapleCharacter cchrxx : new ArrayList<>(chr.getMap().getCharacters())) {
                  int randomx = ThreadLocalRandom.current().nextInt(100);
                  int dropitemidx = -1;
                  int dropitemqtyx = 1;
                  if (GameConstants.isHellInstance(this.getFieldSetInstance()) && randomx < 5) {
                     if (mob.getId() == 8950110) {
                        dropitemidx = 2644201;
                     }

                     if (mob.getId() == 8950112) {
                        dropitemidx = 2644202;
                     }

                     if (mob.getId() == 8880177) {
                        dropitemidx = 2644200;
                     }
                  }

                  if (dropitemidx != -1) {
                     idrop = new Item(dropitemidx, (short) 0, (short) dropitemqtyx, 0);
                     if (idrop.getUniqueId() < 0L && ii.isCash(idrop.getItemId())) {
                        idrop.setUniqueId(MapleInventoryIdentifier.getInstance());
                     }

                     idrop.setGMLog(
                           CurrentTime.getAllCurrentTime()
                                 + "에 "
                                 + cchrxx.getName()
                                 + "이(가) "
                                 + mob.getId()
                                 + " 몬스터로부터 얻은 아이템 (맵ID : "
                                 + this.mapid
                                 + ") (보스 개인 드롭)");
                     this.spawnMobDrop(idrop, this.calcDropPos(pos, mob.getTruePosition()), mob, cchrxx, droptype, 0,
                           true);
                  }
               }
            }
         }

         if (!DBConfig.isGanglim) {
            if (chr.isMultiMode()) {
               for (MapleCharacter partyMemberx : chr.getPartyMembers()) {
                  partyMemberx.addTogetherPointByBoss(mob.getId());
               }
            }

            if (dropCube
                  && (mob.getId() == 8950110 || mob.getId() == 8950112 || mob.getId() == 8950115
                        || mob.getId() == 8880177)
                  && this.getFieldSetInstance() != null
                  && (this.getFieldSetInstance() instanceof HellLucidBoss
                        || this.getFieldSetInstance() instanceof HellDemianBoss
                        || this.getFieldSetInstance() instanceof HellBlackHeavenBoss
                        || this.getFieldSetInstance() instanceof HellWillBoss
                        || this.getFieldSetInstance() instanceof HellDunkelBoss)) {
               for (MapleCharacter cchrxx : new ArrayList<>(chr.getMap().getCharacters())) {
                  int dropitemidxx = 2434560;
                  int dropitemqtyxx = ThreadLocalRandom.current().nextInt(15) + 20;
                  idrop = new Item(dropitemidxx, (short) 0, (short) dropitemqtyxx, 0);
                  if (idrop.getUniqueId() < 0L && ii.isCash(idrop.getItemId())) {
                     idrop.setUniqueId(MapleInventoryIdentifier.getInstance());
                  }

                  idrop.setGMLog(
                        CurrentTime.getAllCurrentTime() + "에 " + cchrxx.getName() + "이(가) " + mob.getId()
                              + " 몬스터로부터 얻은 아이템 (맵ID : " + this.mapid + ") (보스 개인 드롭)");
                  this.spawnMobDrop(idrop, this.calcDropPos(pos, mob.getTruePosition()), mob, cchrxx, droptype, 0,
                        true);
                  dropitemidxx = 2439259;
                  dropitemqtyxx = ThreadLocalRandom.current().nextInt(15) + 10;
                  idrop = new Item(dropitemidxx, (short) 0, (short) dropitemqtyxx, 0);
                  if (idrop.getUniqueId() < 0L && ii.isCash(idrop.getItemId())) {
                     idrop.setUniqueId(MapleInventoryIdentifier.getInstance());
                  }

                  idrop.setGMLog(
                        CurrentTime.getAllCurrentTime() + "에 " + cchrxx.getName() + "이(가) " + mob.getId()
                              + " 몬스터로부터 얻은 아이템 (맵ID : " + this.mapid + ") (보스 개인 드롭)");
                  this.spawnMobDrop(idrop, this.calcDropPos(pos, mob.getTruePosition()), mob, cchrxx, droptype, 0,
                        true);
               }
            }

            if (!dropSingleMode) {
               if (mob.getId() != 8950110 && mob.getId() != 8950112 && mob.getId() != 8950115
                     && mob.getId() != 8880177) {
                  if (mob.getId() == 8880725
                        || mob.getId() == 8950117
                        || mob.getId() == 8950119
                        || mob.getId() == 8950121
                        || mob.getId() == 8880518
                        || mob.getId() == 8880614) {
                     for (MapleCharacter cchrxx : new ArrayList<>(chr.getMap().getCharacters())) {
                        int dropitemidxxx = 2631879;
                        int dropitemqtyxxx = ThreadLocalRandom.current().nextInt(15) + 15;
                        idrop = new Item(dropitemidxxx, (short) 0, (short) dropitemqtyxxx, 0);
                        if (idrop.getUniqueId() < 0L && ii.isCash(idrop.getItemId())) {
                           idrop.setUniqueId(MapleInventoryIdentifier.getInstance());
                        }

                        idrop.setGMLog(
                              CurrentTime.getAllCurrentTime()
                                    + "에 "
                                    + cchrxx.getName()
                                    + "이(가) "
                                    + mob.getId()
                                    + " 몬스터로부터 얻은 아이템 (맵ID : "
                                    + this.mapid
                                    + ") (보스 개인 드롭)");
                        this.spawnMobDrop(idrop, this.calcDropPos(pos, mob.getTruePosition()), mob, cchrxx, droptype, 0,
                              true);
                     }
                  }
               } else if (this.getFieldSetInstance() != null
                     && (this.getFieldSetInstance() instanceof HardLucidBoss
                           || this.getFieldSetInstance() instanceof HardDemianBoss
                           || this.getFieldSetInstance() instanceof HardBlackHeavenBoss
                           || this.getFieldSetInstance() instanceof HardWillBoss)) {
                  for (MapleCharacter cchrxx : new ArrayList<>(chr.getMap().getCharacters())) {
                     int dropitemidxxx = 2631879;
                     int dropitemqtyxxx = ThreadLocalRandom.current().nextInt(15) + 15;
                     idrop = new Item(dropitemidxxx, (short) 0, (short) dropitemqtyxxx, 0);
                     if (idrop.getUniqueId() < 0L && ii.isCash(idrop.getItemId())) {
                        idrop.setUniqueId(MapleInventoryIdentifier.getInstance());
                     }

                     idrop.setGMLog(
                           CurrentTime.getAllCurrentTime()
                                 + "에 "
                                 + cchrxx.getName()
                                 + "이(가) "
                                 + mob.getId()
                                 + " 몬스터로부터 얻은 아이템 (맵ID : "
                                 + this.mapid
                                 + ") (보스 개인 드롭)");
                     this.spawnMobDrop(idrop, this.calcDropPos(pos, mob.getTruePosition()), mob, cchrxx, droptype, 0,
                           true);
                  }
               }
            }

            if (mob.getId() == 8950110
                  || mob.getId() == 8950112
                  || mob.getId() == 8950115
                  || mob.getId() == 8880177
                  || mob.getId() == 8950117
                  || mob.getId() == 8950119
                  || mob.getId() == 8950121
                  || mob.getId() == 8880518
                  || mob.getId() == 8880614) {
               for (MapleCharacter cchrxx : new ArrayList<>(chr.getMap().getCharacters())) {
                  int randomxx = ThreadLocalRandom.current().nextInt(100);
                  int dropitemidxxx = -1;
                  int dropitemqtyxxx = -1;
                  if (randomxx < 30) {
                     dropitemidxxx = 2439995;
                     dropitemqtyxxx = 1;
                  } else if (randomxx < 40) {
                     dropitemidxxx = 2439996;
                     dropitemqtyxxx = 1;
                  }

                  if (dropitemidxxx != -1) {
                     idrop = new Item(dropitemidxxx, (short) 0, (short) dropitemqtyxxx, 0);
                     if (idrop.getUniqueId() < 0L && ii.isCash(idrop.getItemId())) {
                        idrop.setUniqueId(MapleInventoryIdentifier.getInstance());
                     }

                     idrop.setGMLog(
                           CurrentTime.getAllCurrentTime()
                                 + "에 "
                                 + cchrxx.getName()
                                 + "이(가) "
                                 + mob.getId()
                                 + " 몬스터로부터 얻은 아이템 (맵ID : "
                                 + this.mapid
                                 + ") (보스 개인 드롭)");
                     this.spawnMobDrop(idrop, this.calcDropPos(pos, mob.getTruePosition()), mob, cchrxx, droptype, 0,
                           true);
                  }
               }
            }
         }

         this.customDrop(mob, droptype, pos, mobpos, d, chr, cmServerrate, idrop, ii);
      } finally {
         chr.getStat().getLock().writeLock().unlock();
      }
   }

   private void customDrop(
         MapleMonster mob, int droptype, Point pos, int mobpos, int d, MapleCharacter chr, double cmServerrate,
         Item idrop, MapleItemInformationProvider ii) {
      if (mob.getId() == 8870000) {
         for (int i = 0; i < 3; i++) {
            int mesos = Randomizer.nextInt(10000) + 10000;
            if (mesos > 0) {
               double mesoBuff = ServerConstants.currentMesoHottimeRate;
               if (ServerConstants.dailyEventType == DailyEventType.MesoRateFever) {
                  mesoBuff += DBConfig.isGanglim ? 1.0 : 0.2;
               }

               if (droptype == 3) {
                  pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
               } else {
                  pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
               }

               this.spawnMobMesoDrop(
                     (int) (mesos * (Math.min(480.0, chr.getStat().mesoBuff) / 100.0) * chr.getDropMod() * mesoBuff
                           * cmServerrate),
                     this.calcDropPos(pos, mob.getTruePosition()),
                     mob,
                     chr,
                     false,
                     (byte) 0);
               d++;
            }
         }

         List<Integer> armors = new ArrayList<>(
               Arrays.asList(
                     1004214,
                     1004215,
                     1004216,
                     1004217,
                     1004218,
                     1082593,
                     1082594,
                     1082595,
                     1082596,
                     1082597,
                     1052784,
                     1052785,
                     1052786,
                     1052787,
                     1052788,
                     1072952,
                     1072953,
                     1072954,
                     1072955,
                     1072956));
         if (droptype == 3) {
            pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
         } else {
            pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
         }

         Item var18 = ii.randomizeStats_Necro((Equip) ii.getEquipById(armors.get(Randomizer.nextInt(armors.size()))));
         this.spawnMobDrop(var18, this.calcDropPos(pos, mob.getTruePosition()), mob, chr, (byte) 0, 0);
         d++;
         boolean chanceDoubleDrop = Randomizer.nextBoolean();
         if (chanceDoubleDrop) {
            if (droptype == 3) {
               pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
            } else {
               pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
            }

            Item var19 = ii
                  .randomizeStats_Necro((Equip) ii.getEquipById(armors.get(Randomizer.nextInt(armors.size()))));
            this.spawnMobDrop(var19, this.calcDropPos(pos, mob.getTruePosition()), mob, chr, (byte) 0, 0);
            d++;
         }

         for (int ix = 0; ix < 3; ix++) {
            int mesos = Randomizer.nextInt(10000) + 10000;
            if (mesos > 0) {
               double mesoBuffx = ServerConstants.currentMesoHottimeRate;
               if (ServerConstants.dailyEventType == DailyEventType.MesoRateFever) {
                  mesoBuffx += DBConfig.isGanglim ? 1.0 : 0.2;
               }

               if (droptype == 3) {
                  pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
               } else {
                  pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
               }

               this.spawnMobMesoDrop(
                     (int) (mesos * (Math.min(480.0, chr.getStat().mesoBuff) / 100.0) * chr.getDropMod() * mesoBuffx
                           * cmServerrate),
                     this.calcDropPos(pos, mob.getTruePosition()),
                     mob,
                     chr,
                     false,
                     (byte) 0);
               d++;
            }
         }

         boolean weaponDrop = Randomizer.nextBoolean();
         if (weaponDrop) {
            if (droptype == 3) {
               pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
            } else {
               pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
            }

            List<Integer> weapons = new ArrayList<>(
                  Arrays.asList(
                        1212035,
                        1213011,
                        1214011,
                        1222035,
                        1232035,
                        1242131,
                        1262013,
                        1272010,
                        1282010,
                        1292011,
                        1302213,
                        1312112,
                        1322151,
                        1332187,
                        1332188,
                        1342066,
                        1362061,
                        1372132,
                        1382159,
                        1402143,
                        1412100,
                        1422103,
                        1432133,
                        1442171,
                        1452163,
                        1462153,
                        1472175,
                        1482136,
                        1492136,
                        1522067,
                        1532071,
                        1582013,
                        1592011));
            Item var20 = ii
                  .randomizeStats_Necro((Equip) ii.getEquipById(weapons.get(Randomizer.nextInt(weapons.size()))));
            this.spawnMobDrop(var20, this.calcDropPos(pos, mob.getTruePosition()), mob, chr, (byte) 0, 0);
            d++;
         }

         for (int ixx = 0; ixx < 3; ixx++) {
            int mesos = Randomizer.nextInt(10000) + 10000;
            if (mesos > 0) {
               double mesoBuffxx = ServerConstants.currentMesoHottimeRate;
               if (ServerConstants.dailyEventType == DailyEventType.MesoRateFever) {
                  mesoBuffxx += DBConfig.isGanglim ? 1.0 : 0.2;
               }

               if (droptype == 3) {
                  pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
               } else {
                  pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
               }

               this.spawnMobMesoDrop(
                     (int) (mesos * (Math.min(480.0, chr.getStat().mesoBuff) / 100.0) * chr.getDropMod() * mesoBuffxx
                           * cmServerrate),
                     this.calcDropPos(pos, mob.getTruePosition()),
                     mob,
                     chr,
                     false,
                     (byte) 0);
               d++;
            }
         }
      }

      if (mob.getId() == 8840007 || mob.getId() == 8840000) {
         for (int ixxx = 0; ixxx < 3; ixxx++) {
            int mesos = Randomizer.nextInt(10000) + 10000;
            if (mesos > 0) {
               double mesoBuffxxx = ServerConstants.currentMesoHottimeRate;
               if (ServerConstants.dailyEventType == DailyEventType.MesoRateFever) {
                  mesoBuffxxx += DBConfig.isGanglim ? 1.0 : 0.2;
               }

               if (droptype == 3) {
                  pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
               } else {
                  pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
               }

               this.spawnMobMesoDrop(
                     (int) (mesos * (Math.min(480.0, chr.getStat().mesoBuff) / 100.0) * chr.getDropMod() * mesoBuffxxx
                           * cmServerrate),
                     this.calcDropPos(pos, mob.getTruePosition()),
                     mob,
                     chr,
                     false,
                     (byte) 0);
               d++;
            }
         }

         List<Integer> armorsx = new ArrayList<>(
               Arrays.asList(
                     1003154,
                     1003155,
                     1003156,
                     1003157,
                     1003158,
                     1102262,
                     1102263,
                     1102264,
                     1102265,
                     1102266,
                     1082285,
                     1082286,
                     1082287,
                     1082288,
                     1082289,
                     1052299,
                     1052300,
                     1052301,
                     1052302,
                     1052303,
                     1072471,
                     1072472,
                     1072473,
                     1072474,
                     1072475,
                     1132094,
                     1132096,
                     1132098,
                     1132106,
                     1132108));
         if (droptype == 3) {
            pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
         } else {
            pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
         }

         Item var21 = ii.randomizeStats_Necro((Equip) ii.getEquipById(armorsx.get(Randomizer.nextInt(armorsx.size()))));
         this.spawnMobDrop(var21, this.calcDropPos(pos, mob.getTruePosition()), mob, chr, (byte) 0, 0);
         d++;
         boolean chanceDoubleDropx = Randomizer.nextBoolean();
         if (chanceDoubleDropx) {
            if (droptype == 3) {
               pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
            } else {
               pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
            }

            Item var22 = ii
                  .randomizeStats_Necro((Equip) ii.getEquipById(armorsx.get(Randomizer.nextInt(armorsx.size()))));
            this.spawnMobDrop(var22, this.calcDropPos(pos, mob.getTruePosition()), mob, chr, (byte) 0, 0);
            d++;
         }

         for (int ixxxx = 0; ixxxx < 3; ixxxx++) {
            int mesos = Randomizer.nextInt(10000) + 10000;
            if (mesos > 0) {
               double mesoBuffxxxx = ServerConstants.currentMesoHottimeRate;
               if (ServerConstants.dailyEventType == DailyEventType.MesoRateFever) {
                  mesoBuffxxxx += DBConfig.isGanglim ? 1.0 : 0.2;
               }

               if (droptype == 3) {
                  pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
               } else {
                  pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
               }

               this.spawnMobMesoDrop(
                     (int) (mesos * (Math.min(480.0, chr.getStat().mesoBuff) / 100.0) * chr.getDropMod() * mesoBuffxxxx
                           * cmServerrate),
                     this.calcDropPos(pos, mob.getTruePosition()),
                     mob,
                     chr,
                     false,
                     (byte) 0);
               d++;
            }
         }

         boolean weaponDrop = Randomizer.nextBoolean();
         if (weaponDrop) {
            if (droptype == 3) {
               pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
            } else {
               pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
            }

            List<Integer> weapons = new ArrayList<>(
                  Arrays.asList(
                        1212015,
                        1222015,
                        1232015,
                        1242015,
                        1302149,
                        1302193,
                        1312095,
                        1312099,
                        1322135,
                        1322139,
                        1332126,
                        1332170,
                        1362020,
                        1372080,
                        1372119,
                        1382102,
                        1382145,
                        1402091,
                        1402131,
                        1432084,
                        1432119,
                        1442113,
                        1442156,
                        1452107,
                        1452149,
                        1462094,
                        1462139,
                        1472118,
                        1472161,
                        1482080,
                        1482122,
                        1492081,
                        1492122,
                        1522019,
                        1522056,
                        1532019,
                        1532060));
            Item var23 = ii
                  .randomizeStats_Necro((Equip) ii.getEquipById(weapons.get(Randomizer.nextInt(weapons.size()))));
            this.spawnMobDrop(var23, this.calcDropPos(pos, mob.getTruePosition()), mob, chr, (byte) 0, 0);
            d++;
         }

         for (int ixxxxx = 0; ixxxxx < 3; ixxxxx++) {
            int mesos = Randomizer.nextInt(10000) + 10000;
            if (mesos > 0) {
               double mesoBuffxxxxx = ServerConstants.currentMesoHottimeRate;
               if (ServerConstants.dailyEventType == DailyEventType.MesoRateFever) {
                  mesoBuffxxxxx += DBConfig.isGanglim ? 1.0 : 0.2;
               }

               if (droptype == 3) {
                  pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
               } else {
                  pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
               }

               this.spawnMobMesoDrop(
                     (int) (mesos * (Math.min(480.0, chr.getStat().mesoBuff) / 100.0) * chr.getDropMod() * mesoBuffxxxxx
                           * cmServerrate),
                     this.calcDropPos(pos, mob.getTruePosition()),
                     mob,
                     chr,
                     false,
                     (byte) 0);
               d++;
            }
         }
      }

      if (mob.getId() == 8840014) {
         for (int ixxxxxx = 0; ixxxxxx < 3; ixxxxxx++) {
            int mesos = Randomizer.nextInt(10000) + 10000;
            if (mesos > 0) {
               double mesoBuffxxxxxx = ServerConstants.currentMesoHottimeRate;
               if (ServerConstants.dailyEventType == DailyEventType.MesoRateFever) {
                  mesoBuffxxxxxx += DBConfig.isGanglim ? 1.0 : 0.2;
               }

               if (droptype == 3) {
                  pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
               } else {
                  pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
               }

               this.spawnMobMesoDrop(
                     (int) (mesos * (Math.min(480.0, chr.getStat().mesoBuff) / 100.0) * chr.getDropMod()
                           * mesoBuffxxxxxx * cmServerrate),
                     this.calcDropPos(pos, mob.getTruePosition()),
                     mob,
                     chr,
                     false,
                     (byte) 0);
               d++;
            }
         }

         List<Integer> armorsxx = new ArrayList<>(
               Arrays.asList(
                     1004234,
                     1004235,
                     1004236,
                     1004237,
                     1004238,
                     1102713,
                     1102714,
                     1102715,
                     1102716,
                     1102717,
                     1082613,
                     1082614,
                     1082615,
                     1082616,
                     1082617,
                     1052804,
                     1052805,
                     1052806,
                     1052807,
                     1052808,
                     1072972,
                     1072973,
                     1072974,
                     1072975,
                     1072976));
         if (droptype == 3) {
            pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
         } else {
            pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
         }

         Item var24 = ii
               .randomizeStats_RoyalVonLeon((Equip) ii.getEquipById(armorsxx.get(Randomizer.nextInt(armorsxx.size()))));
         this.spawnMobDrop(var24, this.calcDropPos(pos, mob.getTruePosition()), mob, chr, (byte) 0, 0);
         d++;
         boolean chanceDoubleDropxx = Randomizer.nextBoolean();
         if (chanceDoubleDropxx) {
            if (droptype == 3) {
               pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
            } else {
               pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
            }

            Item var25 = ii.randomizeStats_RoyalVonLeon(
                  (Equip) ii.getEquipById(armorsxx.get(Randomizer.nextInt(armorsxx.size()))));
            this.spawnMobDrop(var25, this.calcDropPos(pos, mob.getTruePosition()), mob, chr, (byte) 0, 0);
            d++;
         }

         for (int ixxxxxxx = 0; ixxxxxxx < 3; ixxxxxxx++) {
            int mesos = Randomizer.nextInt(10000) + 10000;
            if (mesos > 0) {
               double mesoBuffxxxxxxx = ServerConstants.currentMesoHottimeRate;
               if (ServerConstants.dailyEventType == DailyEventType.MesoRateFever) {
                  mesoBuffxxxxxxx += DBConfig.isGanglim ? 1.0 : 0.2;
               }

               if (droptype == 3) {
                  pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
               } else {
                  pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
               }

               this.spawnMobMesoDrop(
                     (int) (mesos * (Math.min(480.0, chr.getStat().mesoBuff) / 100.0) * chr.getDropMod()
                           * mesoBuffxxxxxxx * cmServerrate),
                     this.calcDropPos(pos, mob.getTruePosition()),
                     mob,
                     chr,
                     false,
                     (byte) 0);
               d++;
            }
         }

         boolean weaponDrop = Randomizer.nextBoolean();
         if (weaponDrop) {
            if (droptype == 3) {
               pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
            } else {
               pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
            }

            List<Integer> weapons = new ArrayList<>(
                  Arrays.asList(
                        1212102,
                        1213013,
                        1214013,
                        1222096,
                        1232096,
                        1242103,
                        1242130,
                        1262014,
                        1272012,
                        1282012,
                        1292013,
                        1302316,
                        1312186,
                        1322237,
                        1332261,
                        1362122,
                        1372208,
                        1382246,
                        1402237,
                        1412179,
                        1422186,
                        1432201,
                        1442255,
                        1452239,
                        1462226,
                        1472248,
                        1482203,
                        1522125,
                        1532131,
                        1582014,
                        1592013));
            Item var26 = ii.randomizeStats_RoyalVonLeon(
                  (Equip) ii.getEquipById(weapons.get(Randomizer.nextInt(weapons.size()))));
            this.spawnMobDrop(var26, this.calcDropPos(pos, mob.getTruePosition()), mob, chr, (byte) 0, 0);
            d++;
         }

         for (int ixxxxxxxx = 0; ixxxxxxxx < 3; ixxxxxxxx++) {
            int mesos = Randomizer.nextInt(10000) + 10000;
            if (mesos > 0) {
               double mesoBuffxxxxxxxx = ServerConstants.currentMesoHottimeRate;
               if (ServerConstants.dailyEventType == DailyEventType.MesoRateFever) {
                  mesoBuffxxxxxxxx += DBConfig.isGanglim ? 1.0 : 0.2;
               }

               if (droptype == 3) {
                  pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
               } else {
                  pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
               }

               this.spawnMobMesoDrop(
                     (int) (mesos * (Math.min(480.0, chr.getStat().mesoBuff) / 100.0) * chr.getDropMod()
                           * mesoBuffxxxxxxxx * cmServerrate),
                     this.calcDropPos(pos, mob.getTruePosition()),
                     mob,
                     chr,
                     false,
                     (byte) 0);
               d++;
            }
         }
      }

      if (mob.getId() == 8820212) {
         for (int ixxxxxxxxx = 0; ixxxxxxxxx < 3; ixxxxxxxxx++) {
            int mesos = Randomizer.nextInt(10000) + 10000;
            if (mesos > 0) {
               double mesoBuffxxxxxxxxx = ServerConstants.currentMesoHottimeRate;
               if (ServerConstants.dailyEventType == DailyEventType.MesoRateFever) {
                  mesoBuffxxxxxxxxx += DBConfig.isGanglim ? 1.0 : 0.2;
               }

               if (droptype == 3) {
                  pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
               } else {
                  pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
               }

               this.spawnMobMesoDrop(
                     (int) (mesos * (Math.min(480.0, chr.getStat().mesoBuff) / 100.0) * chr.getDropMod()
                           * mesoBuffxxxxxxxxx * cmServerrate),
                     this.calcDropPos(pos, mob.getTruePosition()),
                     mob,
                     chr,
                     false,
                     (byte) droptype);
               d++;
            }
         }

         List<Integer> armorsxxx = new ArrayList<>(
               Arrays.asList(
                     1004229,
                     1004230,
                     1004231,
                     1004232,
                     1004233,
                     1102718,
                     1102719,
                     1102720,
                     1102721,
                     1102722,
                     1082608,
                     1082609,
                     1082610,
                     1082611,
                     1082612,
                     1052799,
                     1052800,
                     1052801,
                     1052802,
                     1052803,
                     1072967,
                     1072968,
                     1072969,
                     1072970,
                     1072971));

         for (int ixxxxxxxxxx = 0; ixxxxxxxxxx < 2; ixxxxxxxxxx++) {
            if (droptype == 3) {
               pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
            } else {
               pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
            }

            Item var27 = ii.randomizeStats_Fensalir(
                  (Equip) ii.getEquipById(armorsxxx.get(Randomizer.nextInt(armorsxxx.size()))));
            this.spawnMobDrop(var27, this.calcDropPos(pos, mob.getTruePosition()), mob, chr, (byte) droptype, 0);
            d++;
         }

         boolean chanceDoubleDropxxx = Randomizer.nextBoolean();
         if (chanceDoubleDropxxx) {
            if (droptype == 3) {
               pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
            } else {
               pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
            }

            Item var28 = ii.randomizeStats_Fensalir(
                  (Equip) ii.getEquipById(armorsxxx.get(Randomizer.nextInt(armorsxxx.size()))));
            this.spawnMobDrop(var28, this.calcDropPos(pos, mob.getTruePosition()), mob, chr, (byte) droptype, 0);
            d++;
         }

         for (int ixxxxxxxxxx = 0; ixxxxxxxxxx < 3; ixxxxxxxxxx++) {
            int mesos = Randomizer.nextInt(10000) + 10000;
            if (mesos > 0) {
               double mesoBuffxxxxxxxxxx = ServerConstants.currentMesoHottimeRate;
               if (ServerConstants.dailyEventType == DailyEventType.MesoRateFever) {
                  mesoBuffxxxxxxxxxx += DBConfig.isGanglim ? 1.0 : 0.2;
               }

               if (droptype == 3) {
                  pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
               } else {
                  pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
               }

               this.spawnMobMesoDrop(
                     (int) (mesos * (Math.min(480.0, chr.getStat().mesoBuff) / 100.0) * chr.getDropMod()
                           * mesoBuffxxxxxxxxxx * cmServerrate),
                     this.calcDropPos(pos, mob.getTruePosition()),
                     mob,
                     chr,
                     false,
                     (byte) droptype);
               d++;
            }
         }

         for (int ixxxxxxxxxxx = 0; ixxxxxxxxxxx < 2; ixxxxxxxxxxx++) {
            if (droptype == 3) {
               pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
            } else {
               pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
            }

            List<Integer> weapons = new ArrayList<>(
                  Arrays.asList(
                        1212101,
                        1213014,
                        1214014,
                        1222095,
                        1232095,
                        1242102,
                        1242133,
                        1262011,
                        1272013,
                        1282013,
                        1292014,
                        1302315,
                        1312185,
                        1322236,
                        1332260,
                        1342100,
                        1362121,
                        1372207,
                        1382245,
                        1402236,
                        1412164,
                        1422171,
                        1432200,
                        1442254,
                        1452238,
                        1462225,
                        1472247,
                        1482202,
                        1492212,
                        1522124,
                        1532130,
                        1582011,
                        1592016));
            Item var29 = ii
                  .randomizeStats_Fensalir((Equip) ii.getEquipById(weapons.get(Randomizer.nextInt(weapons.size()))));
            this.spawnMobDrop(var29, this.calcDropPos(pos, mob.getTruePosition()), mob, chr, (byte) droptype, 0);
            d++;
         }

         for (int ixxxxxxxxxxx = 0; ixxxxxxxxxxx < 3; ixxxxxxxxxxx++) {
            int mesos = Randomizer.nextInt(10000) + 10000;
            if (mesos > 0) {
               double mesoBuffxxxxxxxxxxx = ServerConstants.currentMesoHottimeRate;
               if (ServerConstants.dailyEventType == DailyEventType.MesoRateFever) {
                  mesoBuffxxxxxxxxxxx += DBConfig.isGanglim ? 1.0 : 0.2;
               }

               if (droptype == 3) {
                  pos.x = mobpos + (d % 2 == 0 ? 40 * (d + 1) / 2 : -(40 * (d / 2)));
               } else {
                  pos.x = mobpos + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
               }

               this.spawnMobMesoDrop(
                     (int) (mesos * (Math.min(480.0, chr.getStat().mesoBuff) / 100.0) * chr.getDropMod()
                           * mesoBuffxxxxxxxxxxx * cmServerrate),
                     this.calcDropPos(pos, mob.getTruePosition()),
                     mob,
                     chr,
                     false,
                     (byte) droptype);
               d++;
            }
         }
      }
   }

   private boolean isAffectedByDropBuff(int itemId) {
      switch (itemId) {
         case 2632363:
            return false;
         default:
            return true;
      }
   }

   public void removeMonster(MapleMonster monster) {
      this.removeMonster(monster, -1);
   }

   public void removeMonster(MapleMonster monster, int animation) {
      if (monster != null) {
         this.spawnedMonstersOnMap.decrementAndGet();
         if (monster.getController() != null) {
            monster.getController().send(MobPacket.stopControllingMonster(monster.getObjectId()));
         }

         this.broadcastMessage(MobPacket.killMonster(monster.getObjectId(), animation));
         this.removeMapObject(monster);
         monster.killed();
      }
   }

   public final boolean checkAllKill() {
      for (MapleMapObject obj : this.getAllMonstersThreadsafe()) {
         MapleMonster mob = (MapleMonster) obj;
         if (mob.getSponge() != null && mob.isAlive() && mob.getId() != 8810018 && mob.getId() != 8880166
               && mob.getId() != 8810122 || mob.getSponge() == null) {
            return false;
         }
      }

      return true;
   }

   public final void giveBuff(MapleMonster monster) {
      if (monster.getBuffToGive() > -1) {
         int buffid = monster.getBuffToGive();
         SecondaryStatEffect var3 = MapleItemInformationProvider.getInstance().getItemEffect(buffid);
      }
   }

   public void bossClearQex(MapleCharacter p, int qexID, String qexKey) {
      if (!DBConfig.isGanglim) {
         EventInstanceManager eim = p.getEventInstance();
         if (eim != null) {
            List<Integer> partyPlayerList = eim.getPartyPlayerList();
            if (partyPlayerList != null && !partyPlayerList.isEmpty()) {
               for (Integer playerID : partyPlayerList) {
                  boolean find = false;

                  for (GameServer gs : GameServer.getAllInstances()) {
                     MapleCharacter player = gs.getPlayerStorage().getCharacterById(playerID);
                     if (player != null) {
                        player.updateOneInfo(qexID, qexKey,
                              String.valueOf(player.getOneInfoQuestInteger(qexID, qexKey) + 1));
                        find = true;
                        break;
                     }
                  }

                  if (!find) {
                     DBConnection db = new DBConnection();

                     try (Connection con = DBConnection.getConnection()) {
                        PreparedStatement ps = con.prepareStatement(
                              "SELECT `customData` FROM questinfo WHERE characterid = ? and quest = ?");
                        ps.setInt(1, playerID);
                        ps.setInt(2, qexID);
                        ResultSet rs = ps.executeQuery();
                        boolean f = false;

                        while (rs.next()) {
                           f = true;
                           String value = rs.getString("customData");
                           String[] v = value.split(";");
                           StringBuilder sb = new StringBuilder();
                           int i = 1;
                           boolean a = false;
                           sb.append(qexKey);
                           sb.append("=");
                           sb.append("1");
                           sb.append(";");

                           for (String v_ : v) {
                              String[] cd = v_.split("=");
                              if (!cd[0].equals(qexKey)) {
                                 sb.append(cd[0]);
                                 sb.append("=");
                                 if (cd.length > 1) {
                                    sb.append(cd[1]);
                                 }

                                 if (v.length > i++) {
                                    sb.append(";");
                                 }
                              } else {
                                 a = true;
                              }
                           }

                           PreparedStatement ps2 = con.prepareStatement(
                                 "UPDATE questinfo SET customData = ? WHERE characterid = ? and quest = ?");
                           ps2.setString(1, sb.toString());
                           ps2.setInt(2, playerID);
                           ps2.setInt(3, qexID);
                           ps2.executeUpdate();
                           ps2.close();
                        }

                        if (!f) {
                           PreparedStatement ps2 = con.prepareStatement(
                                 "INSERT INTO questinfo (characterid, quest, customData, date) VALUES (?, ?, ?, ?)");
                           ps2.setInt(1, playerID);
                           ps2.setInt(2, qexID);
                           ps2.setString(3, qexKey + "=1");
                           SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                           String time = sdf.format(Calendar.getInstance().getTime());
                           ps2.setString(4, time);
                           ps2.executeQuery();
                           ps2.close();
                        }

                        rs.close();
                        ps.close();
                     } catch (SQLException var30) {
                        var30.printStackTrace();
                     }
                  }
               }
            }
         }

         if (eim == null && this.getFieldSetInstance() != null) {
            List<Integer> eimm = this.getFieldSetInstance().userList;
            if (eimm != null) {
               List<Integer> partyPlayerList = this.getFieldSetInstance().userList;
               if (partyPlayerList != null && !partyPlayerList.isEmpty()) {
                  for (Integer playerID : partyPlayerList) {
                     boolean find = false;

                     for (GameServer gsx : GameServer.getAllInstances()) {
                        MapleCharacter player = gsx.getPlayerStorage().getCharacterById(playerID);
                        if (player != null) {
                           player.updateOneInfo(qexID, qexKey,
                                 String.valueOf(player.getOneInfoQuestInteger(qexID, qexKey) + 1));
                           find = true;
                           break;
                        }
                     }

                     if (!find) {
                        DBConnection db = new DBConnection();

                        try (Connection con = DBConnection.getConnection()) {
                           PreparedStatement ps = con.prepareStatement(
                                 "SELECT `customData` FROM questinfo WHERE characterid = ? and quest = ?");
                           ps.setInt(1, playerID);
                           ps.setInt(2, qexID);
                           ResultSet rs = ps.executeQuery();
                           boolean f = false;

                           while (rs.next()) {
                              f = true;
                              String value = rs.getString("customData");
                              String[] v = value.split(";");
                              StringBuilder sb = new StringBuilder();
                              int i = 1;
                              boolean a = false;
                              sb.append(qexKey);
                              sb.append("=");
                              sb.append("1");
                              sb.append(";");

                              for (String v_x : v) {
                                 String[] cd = v_x.split("=");
                                 if (!cd[0].equals(qexKey)) {
                                    sb.append(cd[0]);
                                    sb.append("=");
                                    if (cd.length > 1) {
                                       sb.append(cd[1]);
                                    }

                                    if (v.length > i++) {
                                       sb.append(";");
                                    }
                                 } else {
                                    a = true;
                                 }
                              }

                              PreparedStatement ps2 = con.prepareStatement(
                                    "UPDATE questinfo SET customData = ? WHERE characterid = ? and quest = ?");
                              ps2.setString(1, sb.toString());
                              ps2.setInt(2, playerID);
                              ps2.setInt(3, qexID);
                              ps2.executeUpdate();
                              ps2.close();
                           }

                           if (!f) {
                              PreparedStatement ps2 = con.prepareStatement(
                                    "INSERT INTO questinfo (characterid, quest, customData, date) VALUES (?, ?, ?, ?)");
                              ps2.setInt(1, playerID);
                              ps2.setInt(2, qexID);
                              ps2.setString(3, qexKey + "=1");
                              SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                              String time = sdf.format(Calendar.getInstance().getTime());
                              ps2.setString(4, time);
                              ps2.executeQuery();
                              ps2.close();
                           }

                           rs.close();
                           ps.close();
                        } catch (SQLException var28) {
                           var28.printStackTrace();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void bossClear(int mobID, int qexID, String qexKey) {
      boolean set = false;

      for (MapleCharacter p : this.getCharactersThreadsafe()) {
         if (p.getParty() != null) {
            if (DBConfig.isGanglim) {
               p.addGuildContributionByBoss(mobID);
            } else if (!qexKey.contains("_single") && !qexKey.contains("_multi")) {
               p.addGuildContributionByBoss(mobID);
            }

            if (!set) {
               this.bossClearQex(p, qexID, qexKey);
               set = true;
            }
         }
      }
   }

   public void killMonster(MapleMonster monster) {
      this.killMonster(monster, false);
   }

   public void killMonster(MapleMonster monster, boolean removeAfter) {
      if (monster != null) {
         monster.setHp(0L);
         if (!removeAfter) {
            this.removeMonster(monster, 1);
         }

         if (monster.getLinkCID() <= 0) {
            monster.spawnRevives(this, removeAfter);
         }

         this.removeMapObject(monster);
         monster.killed();
      }
   }

   public final void killMonster(MapleMonster monster, MapleCharacter chr, boolean withDrops, boolean second,
         byte animation) {
      this.killMonster(monster, chr, withDrops, second, animation, 0);
   }

   public final void killMonster(MapleMonster monster, MapleCharacter chr, boolean withDrops, boolean second,
         byte animation, boolean selfDestruct) {
      this.killMonster(monster, chr, withDrops, second, animation, 0, selfDestruct);
   }

   public final void killMonster(MapleMonster monster, MapleCharacter chr, boolean withDrops, boolean second,
         byte animation, int lastSkill) {
      this.killMonster(monster, chr, withDrops, second, animation, lastSkill, false);
   }

   public final void killMonster(
         MapleMonster monster, MapleCharacter chr, boolean withDrops, boolean second, byte animation, int lastSkill,
         boolean selfDestruct) {
      if (monster.getBuff(MobTemporaryStatFlag.SEPERATE_SOUL_C) != null) {
         this.removeMonster(monster);
      } else {
         if (GameConstants.isFlameWizard(chr.getJob()) && (lastSkill < 400021042 || lastSkill > 400021045)
               && monster != null) {
            if (chr.getPlayerJob() instanceof FlameWizard) {
               FlameWizard fw = (FlameWizard) chr.getPlayerJob();
               fw.increaseDischarge();
            } else if (chr.getClient().isGm()) {
               chr.dropMessage(5, "플레임위자드가 아닌 JobField??");
            }
         }

         if (chr.getSummonsSize() > 0) {
            for (Summoned summon : chr.getSummons()) {
               if (summon != null && summon.getSkill() == 400001064 && lastSkill != 400001064) {
                  Integer fountain = (Integer) chr.getJobField("erdaFountainStack");
                  if (fountain == null) {
                     fountain = 0;
                  }

                  fountain = fountain + 1;
                  chr.setJobField("erdaFountainStack", fountain);
                  if (fountain >= 12) {
                     PacketEncoder packet = new PacketEncoder();
                     packet.writeShort(SendPacketOpcode.SUMMON_KILL_STACK.getValue());
                     packet.writeInt(chr.getId());
                     packet.writeInt(summon.getObjectId());
                     packet.writeInt(0);
                     packet.write(1);
                     chr.send(packet.getPacket());
                     chr.setJobField("erdaFountainStack", 0);
                  } else {
                     PacketEncoder packet = new PacketEncoder();
                     packet.writeShort(SendPacketOpcode.SUMMON_KILL_STACK.getValue());
                     packet.writeInt(chr.getId());
                     packet.writeInt(summon.getObjectId());
                     packet.writeInt(fountain);
                     packet.write(0);
                     chr.send(packet.getPacket());
                  }
               }
            }
         }

         if (this.getFieldMonsterSpawners() != null) {
            for (FieldMonsterSpawner fms : this.getFieldMonsterSpawners()) {
               if (fms.getSpawnMonster() == monster.getId()) {
                  fms.setLastSpawnTime(System.currentTimeMillis());
                  break;
               }
            }
         }

         boolean drop = withDrops;
         if (this instanceof Field_Demian && this.getFieldSetInstance() != null) {
            Field_Demian demian = (Field_Demian) this;
            if (demian.phase == 1) {
               return;
            }
         }

         try {
            this.onMobKilled(monster);
            chr.checkSpecialCoreSkills("killCount", monster.getObjectId(), null);
         } catch (Exception var31) {
            System.out.println("killMonster Check3");
            var31.printStackTrace();
            FileoutputUtil.log("Log_Damage_Except.rtf",
                  "Error executing on Damage. (playerName : " + chr.getName() + ") " + var31);
         }

         if (this instanceof Field_FerociousBattlefield) {
            Field_FerociousBattlefield f = (Field_FerociousBattlefield) this;
            if (monster.getId() == 8644653 || monster.getId() == 8644654 || monster.getId() == 8644656
                  || monster.getId() == 8644657) {
               f.addDuskGauge(chr, -30.0);
            }
         }

         if (this.getId() >= 993000130 && this.getId() <= 993000149) {
            drop = false;
         }

         if (monster.getId() == 8880156 || monster.getId() == 8880167 || monster.getId() == 8880177) {
            if (!chr.isCanAttackLucidRewardMob()) {
               drop = false;
            } else {
               for (MapleCharacter player : this.getCharactersThreadsafe()) {
                  if (player != null) {
                     player.setCanAttackLucidRewardMob(false);
                  }
               }
            }
         }

         if (monster.getId() == 8880518) {
            if (!chr.isCanAttackBMRewardMob()) {
               drop = false;
            } else {
               for (MapleCharacter playerx : this.getCharactersThreadsafe()) {
                  if (playerx != null) {
                     playerx.updateOneInfo(2000018, "clear", "1");
                     playerx.setCanAttackBMRewardMob(false);
                  }

                  playerx.setClock(300);
                  playerx.setRegisterTransferField(ServerConstants.TownMap);
                  playerx.setRegisterTransferFieldTime(System.currentTimeMillis() + 300000L);
               }
            }
         }

         if (monster.getId() == 8840000) {
            chr.addGuildContributionByBoss(monster.getId());
         } else if (monster.getId() == 8840014) {
            if (chr.hasBuffBySkillID(80002635)
                  && chr.getQuestStatus(2000019) == 1
                  && chr.getBossMode() == 0
                  && chr.getOneInfoQuestInteger(2000019, "clear") != 1) {
               chr.updateOneInfo(2000019, "clear", "1");
               chr.send(CField.addPopupSay(9062000, 3000, "#b[사자왕 반 레온의 흔적]#k 퀘스트를 클리어 할 수 있습니다.", ""));
            }
         } else if (monster.getId() == 8860000) {
            if (chr.getBossMode() == 0
                  && chr.hasBuffBySkillID(80002635)
                  && chr.getQuestStatus(2000020) == 1
                  && chr.getOneInfoQuestInteger(2000020, "clear") != 1) {
               chr.updateOneInfo(2000020, "clear", "1");
               chr.send(CField.addPopupSay(9062000, 3000, "#b[시간의 대신관 아카이럼의 흔적]#k 퀘스트를 클리어 할 수 있습니다.", ""));
            }

            chr.addGuildContributionByBoss(monster.getId());
         } else if (monster.getId() == 8880000) {
            if (chr.getBossMode() == 0
                  && chr.hasBuffBySkillID(80002635)
                  && chr.getQuestStatus(2000021) == 1
                  && chr.getOneInfoQuestInteger(2000021, "clear") != 1) {
               if (DBConfig.isGanglim) {
                  chr.updateOneInfo(2000021, "clear", "1");
                  chr.send(CField.addPopupSay(9062000, 3000, "#b[폭군 매그너스의 흔적]#k 퀘스트를 클리어 할 수 있습니다.", ""));
               } else if (chr.haveItem(4036460) && !chr.isMultiMode()) {
                  chr.updateOneInfo(2000021, "clear", "1");
                  chr.send(CField.addPopupSay(9062000, 3000, "#b[폭군 매그너스의 흔적]#k 퀘스트를 클리어 할 수 있습니다.", ""));
               }
            }

            this.bossClear(monster.getId(), 1234569, "hard_magnus");
         } else if (monster.getId() == 8950002) {
            if (chr.getBossMode() == 0
                  && chr.hasBuffBySkillID(80002636)
                  && chr.getQuestStatus(2000022) == 1
                  && chr.getOneInfoQuestInteger(2000022, "clear") != 1) {
               chr.updateOneInfo(2000022, "clear", "1");
               chr.send(CField.addPopupSay(9062000, 3000, "#b[윙 마스터 스우의 흔적]#k 퀘스트를 클리어 할 수 있습니다.", ""));
            }
         } else if (monster.getId() == 8880101) {
            if (chr.getBossMode() == 0 && chr.getQuestStatus(2000023) == 1 && chr.isStartBMQuest6()
                  && chr.getOneInfoQuestInteger(2000023, "clear") != 1) {
               chr.updateOneInfo(2000023, "clear", "1");
               chr.send(CField.addPopupSay(9062000, 3000, "#b[파멸의 검 데미안의 흔적]#k 퀘스트를 클리어 할 수 있습니다.", ""));
            }
         } else if (monster.getId() == 8880302) {
            if (chr.getBossMode() == 0 && chr.getQuestStatus(2000024) == 1 && chr.isStartBMQuest7()
                  && chr.getOneInfoQuestInteger(2000024, "clear") != 1) {
               chr.updateOneInfo(2000024, "clear", "1");
               chr.send(CField.addPopupSay(9062000, 3000, "#b[거미의 왕 윌의 흔적]#k 퀘스트를 클리어 할 수 있습니다.", ""));
            }
         } else if (monster.getId() == 8880153) {
            if (chr.getBossMode() == 0 && chr.getQuestStatus(2000025) == 1
                  && chr.getOneInfoQuestInteger(2000025, "clear") != 1) {
               chr.updateOneInfo(2000025, "clear", "1");
               chr.send(CField.addPopupSay(9062000, 3000, "#b[악몽의 주인 루시드의 흔적]#k 퀘스트를 클리어 할 수 있습니다.", ""));
            }
         } else if (monster.getId() == 8880410
               && chr.getBossMode() == 0
               && chr.getQuestStatus(2000026) == 1
               && chr.isStartBMQuest9()
               && chr.getOneInfoQuestInteger(2000026, "clear") != 1) {
            chr.updateOneInfo(2000026, "clear", "1");
            chr.send(CField.addPopupSay(9062000, 3000, "#b[붉은 마녀 진 힐라의 흔적]#k 퀘스트를 클리어 할 수 있습니다.", ""));
         }

         if (monster.getId() == 8910001) {
            EventInstanceManager eim = chr.getEventInstance();
            if (eim != null) {
               eim.doTimerGaugeEndAction(true, chr);
            }
         }

         if (chr.getLevel() >= 33 && (this.getId() < 954000000 || this.getId() > 954080500)) {
            int level = chr.getLevel();
            int mobLevel = monster.getStats().getLevel();
            int delta = level - mobLevel;
            boolean check = true;

            try {
               if (!check) {
                  drop = false;
               } else if (!DBConfig.isGanglim && Math.abs(delta) > 20 && level < 275) {
                  if (!monster.getStats().isBoss()) {
                     drop = false;
                     if (chr.getMobPenaltyAnnounceTime() == 0L
                           || System.currentTimeMillis() - chr.getMobPenaltyAnnounceTime() >= 7000L) {
                        chr.send(
                              CField.UIPacket.sendBigScriptProgressMessage(
                                    "레벨 범위를 벗어난 몬스터를 사냥 시 경험치와 메소 획득량이 크게 감소합니다.", FontType.NanumGothic,
                                    FontColorType.Yellow));
                        chr.setMobPenaltyAnnounceTime(System.currentTimeMillis());
                     }
                  }
               } else {
                  if (this.getFieldSetInstance() == null && chr.getEventInstance() == null
                        && chr.getRandomPortal() == null && Randomizer.rand(0, 10000) <= 4) {
                     List<Spawns> spawns = this.getClosestSpawns(chr.getTruePosition(), 50);
                     if (spawns != null && !spawns.isEmpty()) {
                        Collections.shuffle(spawns);
                        Spawns spawn = spawns.stream().findAny().orElse(null);
                        if (spawn != null) {
                           Point pos = spawn.getPosition();
                           int type = Randomizer.isSuccess(20) ? 3 : 2;
                           int gameType = type == 3 ? 8 : Randomizer.rand(0, 7);
                           RandomPortalType portalType = RandomPortalType.get(type);
                           RandomPortalGameType portalGameType = RandomPortalGameType.get(gameType);
                           RandomPortal portal = new RandomPortal(portalType, Randomizer.rand(1000000, 9999999), pos,
                                 chr.getId(), portalGameType);
                           int totalLevel = 0;
                           long totalExp = 0L;
                           long totalHp = 0L;
                           int c = 0;

                           for (Spawns s : this.getClosestSpawns(chr.getTruePosition(), 30)) {
                              int mobTemplateID = s.getMonster().getId();
                              MapleMonster mob = MapleLifeFactory.getMonster(mobTemplateID);
                              totalLevel += mob.getStats().getLevel();
                              totalExp += mob.getStats().getExp();
                              totalHp += mob.getStats().getMaxHp();
                              c++;
                           }

                           totalLevel /= c;
                           totalExp /= c;
                           totalHp /= c;
                           portal.setMobAvgHp(totalHp);
                           portal.setMobAvgExp(totalExp);
                           portal.setMobAvgLevel(totalLevel);
                           chr.updateOneInfo(26022, "exp", String.valueOf(totalExp));
                           chr.updateOneInfo(26022, "map", String.valueOf(chr.getMapId()));
                           if (type == 2) {
                              chr.send(CWvsContext.getScriptProgressMessage("현상금 사냥꾼의 포탈이 등장했습니다!"));
                           } else {
                              chr.send(CWvsContext.getScriptProgressMessage("불꽃늑대의 소굴로 향하는 포탈이 등장했습니다!"));
                           }

                           chr.setRandomPortal(portal);
                           chr.setRandomPortalSpawnedTime(System.currentTimeMillis());
                           chr.send(CField.randomPortalCreated(portal));
                        }
                     }
                  }

                  String value = chr.getOneInfoQuest(16700, "count");
                  if (value != null && !value.isEmpty()) {
                     int count = Integer.parseInt(value) + 1;
                     if (count <= 300) {
                        chr.updateInfoQuest(16700, "count=" + count + ";day=" + chr.getDailyGift().getDailyDay()
                              + ";date=" + CurrentTime.getCurrentTime2());
                     }
                  } else {
                     chr.updateInfoQuest(16700, "count=1;day=0;date=" + CurrentTime.getCurrentTime2());
                  }

                  if (DBConfig.isGanglim) {
                     int count = chr.getOneInfoQuestInteger(100722, "kill_count");
                     if (++count >= 100) {
                        chr.gainStackEventGauge(0, 1, false);
                        chr.updateOneInfo(100722, "kill_count", "0");
                     } else {
                        chr.updateOneInfo(100722, "kill_count", String.valueOf(count));
                     }
                  } else if (this.getId() != 993014200 && this.getId() != 993058200) {
                     int count = chr.getOneInfoQuestInteger(100722, "kill_count");
                     if (++count >= 100) {
                        chr.gainStackEventGauge(0, 1, false);
                        chr.updateOneInfo(100722, "kill_count", "0");
                     } else {
                        chr.updateOneInfo(100722, "kill_count", String.valueOf(count));
                     }
                  }

                  if (!DBConfig.isGanglim) {
                     int questID = QuestExConstants.JinQuestExAccount.getQuestID();
                     String questKey = "DailyLevelMob";
                     chr.updateOneInfo(questID, questKey,
                           String.valueOf(chr.getOneInfoQuestInteger(questID, questKey) + 1));
                  }
               }
            } catch (Exception var32) {
               System.out.println("killMonster Check2");
               var32.printStackTrace();
               FileoutputUtil.log("Log_Damage_Except.rtf",
                     "Error executing on Damage. (playerName : " + chr.getName() + ") " + var32);
            }
         }

         if (monster.getId() == 9833905) {
            chr.gainStackEventGauge(0, 20, false);
            chr.updateOneInfo(100722, "total", String.valueOf(chr.getOneInfoQuestInteger(100722, "total") + 1));
            int prop = 10;
            int total = chr.getOneInfoQuestInteger(100722, "total");
            byte var44;
            if (total <= 5) {
               var44 = 15;
            } else if (total <= 10) {
               var44 = 25;
            } else if (total <= 15) {
               var44 = 35;
            } else if (total <= 20) {
               var44 = 45;
            } else {
               var44 = 55;
            }

            if (Randomizer.isSuccess(var44)) {
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.MAGIC_BELL_FIELD_ATTACK.getValue());
               packet.writeInt(0);
               packet.writeInt(9833905);
               chr.send(packet.getPacket());
               chr.gainStackEventGauge(0, 20, false);
               chr.send(CField.addPopupSay(9062474, 2000, "마법의 종이 울리는 소리를 듣고\r\n눈꽃 순록들이 달려오고 있어.", ""));
               chr.send(CField.addPopupSay(9062474, 2000, "고마워.\r\n네 덕분에 눈꽃 순록들이 바깥세상을 달릴 수 있었어.", ""));
            }
         }

         if (GameConstants.getMParkMonsterExp(monster.getId()) > 0) {
            chr.setmParkExp(chr.getmParkExp() + (int) (GameConstants.getMParkMonsterExp(monster.getId()) * 1.5));
            NumberFormat nf = NumberFormat.getInstance();
            chr.send(CField.startMapEffect("경험치 보상 " + nf.format(chr.getmParkExp()) + " 누적!", 5120162, true));
         }

         if (this instanceof Field_BountyHunting) {
            Field_BountyHunting f = (Field_BountyHunting) this;
            if (monster.getId() == 9833004) {
               f.setFailGame(true);
            }
         }

         if (this instanceof Field_StormwingArea) {
            Field_StormwingArea f = (Field_StormwingArea) this;
            if (monster.getId() == 9832001 && f.getStormWingCount() < 5) {
               chr.send(CField.environmentChange("PoloFritto/bonus", 19));
               chr.send(CField.environmentChange("Romio/discovery", 5, 100));
               f.setStormWingCount(f.getStormWingCount() + 1);
               f.addBonusTime();
               if (f.getStormWingCount() >= 5) {
                  chr.send(CField.startMapEffect("스톰윙을 모두 처치하였네! 이제 몰려든 몬스터들을 최대한 잡아보자고!", 5120159, true, 5));
               } else {
                  chr.send(CField.startMapEffect("스톰윙을 처치하였군. 조금 더 오래 머물 수 있겠어!", 5120159, true, 5));
               }
            }
         }

         if (this instanceof Field_MidnightMonsterHunting) {
            Field_MidnightMonsterHunting f = (Field_MidnightMonsterHunting) this;
            if (!f.isClearGame()) {
               f.setRemainCount(f.getRemainCount() - 1);
               if (f.getRemainCount() <= 0) {
                  chr.send(CWvsContext.getScriptProgressMessage("축하드립니다! 최대 사냥 가능 마리 수를 달성하였습니다. 잠시 후 퇴장맵으로 이동합니다."));
                  f.setClearGame(true);
               }
            }
         }

         if ((this.getId() < 993000130 || this.getId() > 993000149)
               && !DBConfig.isGanglim
               && !this.isTown()
               && !FieldLimitType.NO_EXP_DECREASE.check(this.getFieldLimit())
               && this.getId() != 993026800) {
            int delta = chr.getLevel() - monster.getStats().getLevel();
            if (delta >= -20 && delta <= 20 || chr.getLevel() >= 275) {
               if (monster.getEliteMobType() == EliteType.None && this.getEliteState() == EliteState.Normal
                     && this.getRemainEliteMobSpawn() >= 50) {
                  if (Randomizer.isSuccess(20)) {
                     if (this.getEliteLevel() >= 20) {
                        EliteBossEvent event = new EliteBossEvent(
                              this, System.currentTimeMillis() + 1800000L, chr.getClient().getChannelServer(),
                              chr.getTruePosition());
                        this.setFieldEvent(event);
                     } else {
                        EliteMobEvent event = new EliteMobEvent(this, System.currentTimeMillis() + 600000L,
                              chr.getPosition(), 1);
                        this.setFieldEvent(event);
                     }
                  }

                  this.setRemainEliteMobSpawn(0);
               }

               this.setRemainEliteMobSpawn(this.getRemainEliteMobSpawn() + 1);
            }
         }

         if (GameConstants.isAngelicBuster(chr.getJob()) && chr.getTotalSkillLevel(65100005) > 0) {
            chr.invokeJobMethod("addSoulRechargeKillMob");
         }

         if (drop && (monster.getId() < 8800003 || monster.getId() > 8800010)
               && (monster.getId() < 8800103 || monster.getId() > 8800110)) {
            int delta = 1;
            if (ServerConstants.dailyEventType == DailyEventType.StarForceDiscount) {
               delta = 2;
            }

            chr.gainKillPoint(delta);
         }

         EventInstanceManager eim = chr.getEventInstance();
         if (eim != null && monster.getStats().isBoss()) {
            for (Entry<String, ScheduledFuture<?>> schedule : eim.getAllEventSchedule().entrySet()) {
               if (schedule.getValue() != null) {
                  schedule.getValue().cancel(true);
                  eim.removeEventSchedule(schedule.getKey());
               }
            }
         }

         if (DBConfig.isGanglim && monster.getId() == 9833971) {
            int genMobKillCount = chr.getKeyValue(100857, "genMobKillCount");
            if (genMobKillCount < 5) {
               chr.setKeyValue(100857, "genMobKillCount", ++genMobKillCount + "");
               if (genMobKillCount == 5) {
                  chr.finishGiftShowX3(null, false);
               }
            }
         }

         this.broadcastMessage(
               MobPacket.killMonster(monster.getObjectId(), monster.getId() == 9300166 ? 4 : animation));
         if (!selfDestruct) {
            this.spawnedMonstersOnMap.decrementAndGet();
            this.removeMapObject(monster);
         }

         monster.killed();
         MapleSquad sqd = this.getSquadByMap();
         boolean instanced = sqd != null || monster.getEventInstance() != null || this.getEMByMap() != null;
         int dropOwner = monster.killBy(chr, lastSkill);
         IntensePowerCrystalData data = GameConstants.getIntensePowerCrystalData(monster.getId());
         if (data != null) {
            for (MapleCharacter playerx : this.getCharactersThreadsafe()) {
               if (playerx.getParty() != null && playerx.getBossMode() == 0) {
                  playerx.dropIntensePowerCrystal(monster);
               }
            }
         }

         if (this.hasteBoosterTarget == null && monster.getFrozenLinkSerialNumber() > 0L && this.getId() != 450009400
               && this.getId() != 450009450) {
            chr.addFrozenLinkMobCount(-1);
            if (chr.getFrozenLinkMobCount() <= 0 && chr.isStartedFrozenLink()) {
               this.killAllMonstersFL(true, chr.getFrozenLinkSerialNumber());
               chr.cancelFrozenLinkTask();
               Field target = chr.getClient().getChannelServer().getMapFactory().getMap(993014200);
               chr.changeMap(target, target.getPortal(0));
            }
         }

         if (monster.getBuffToGive() > -1) {
            int buffid = monster.getBuffToGive();
            SecondaryStatEffect var83 = MapleItemInformationProvider.getInstance().getItemEffect(buffid);
         }

         int mobid = monster.getId();
         ExpeditionType type = null;
         if (mobid == 9400266 && this.mapid == 802000111) {
            this.doShrine(true, eim);
         } else if (mobid == 9400265 && this.mapid == 802000211) {
            this.doShrine(true, eim);
         } else if (mobid == 9400270 && this.mapid == 802000411) {
            this.doShrine(true, eim);
         } else if (mobid == 9400273 && this.mapid == 802000611) {
            this.doShrine(true, eim);
         } else if (mobid == 9400294 && this.mapid == 802000711) {
            this.doShrine(true, eim);
         } else if (mobid == 9400296 && this.mapid == 802000803) {
            this.doShrine(true, eim);
         } else if (mobid == 9400289 && this.mapid == 802000821) {
            this.doShrine(true, eim);
         } else if (mobid == 8830000 && this.mapid == 105100300) {
            if (this.speedRunStart > 0L) {
               type = ExpeditionType.Normal_Balrog;
            }
         } else if ((mobid == 9420544 || mobid == 9420549)
               && this.mapid == 551030200
               && monster.getEventInstance() != null
               && monster.getEventInstance().getName().contains(this.getEMByMap().getName())) {
            this.doShrine(this.getAllReactor().isEmpty(), eim);
         } else if (mobid == 8900000 && withDrops) {
            this.bossClear(8900000, 1234569, "chaos_pierre_clear");
         } else if (mobid == 8910000 && withDrops) {
            this.bossClear(8910000, 1234569, "chaos_banban_clear");
         } else if (mobid == 8930000 && withDrops) {
            this.bossClear(8930000, 1234569, "chaos_velum_clear");
         } else if (mobid == 8850011 && withDrops) {
            chr.addGuildContributionByBoss(8850011);
         } else if (mobid == 8820210 && withDrops) {
            chr.addGuildContributionByBoss(8820210);
         } else if (mobid == 8950109 && this.mapid == 350060950 || mobid == 8950110 && this.mapid == 350060650) {
            if (chr.getBossMode() == 0) {
               if (chr.getParty() != null) {
                  chr.getParty().getPartyMember().getPartyMemberList().forEach(chr_ -> {
                     MapleCharacter cx = chr.getClient().getChannelServer().getPlayerStorage()
                           .getCharacterById(chr_.getId());
                     if (cx != null && cx.getMapId() == this.getId()) {
                        int quantity = 0;
                        int rand = Randomizer.nextInt(100);
                        byte var6x;
                        if (rand <= 20) {
                           var6x = 1;
                        } else if (rand <= 95) {
                           var6x = 2;
                        } else {
                           var6x = 3;
                        }

                        cx.gainItem(4001843, var6x, false, -1L, "스우 격파로 얻은 아이템");
                     }
                  });
               }

               if (DBConfig.isGanglim) {
                  this.bossClear(mobid, 1234569, "swoo_clear");
               } else {
                  boolean single = !chr.isMultiMode();
                  this.bossClear(mobid, 1234569, "swoo_clear");
                  if (this.getFieldSetInstance() == null || this.getFieldSetInstance() != null
                        && !(this.getFieldSetInstance() instanceof HellBlackHeavenBoss)) {
                     this.bossClear(mobid, 1234569, "swoo_clear" + (single ? "_single" : "_multi"));
                  }
               }

               if (mobid == 8950109) {
                  this.bossClearQex(chr, 1234569, "normal_swoo_clear");

                  for (PartyMemberEntry mpc : new ArrayList<>(chr.getParty().getPartyMember().getPartyMemberList())) {
                     StringBuilder sb = new StringBuilder("보스 노말 스우 격파");
                     MapleCharacter playerxx = this.getCharacterById(mpc.getId());
                     if (playerxx != null) {
                        LoggingManager.putLog(new BossLog(playerxx, BossLogType.ClearLog.getType(), sb));
                     }
                  }
               } else if (mobid == 8950110) {
                  this.bossClearQex(chr, 1234569, "hard_swoo_clear");
                  String list = "";
                  List<String> names = new ArrayList<>();
                  boolean check = false;

                  for (PartyMemberEntry mpcx : new ArrayList<>(chr.getParty().getPartyMember().getPartyMemberList())) {
                     names.add(mpcx.getName());
                  }

                  list = String.join(",", names);

                  for (PartyMemberEntry mpcx : new ArrayList<>(chr.getParty().getPartyMember().getPartyMemberList())) {
                     boolean hell = false;
                     if (this.getFieldSetInstance() != null
                           && this.getFieldSetInstance() instanceof HellBlackHeavenBoss) {
                        MapleCharacter p = this.getCharacterById(mpcx.getId());
                        if (p != null) {
                           String keyValue = "hell_swoo_point";
                           p.updateOneInfo(787777, keyValue,
                                 String.valueOf(p.getOneInfoQuestInteger(787777, keyValue) + 3));
                           if (!check) {
                              this.bossClearQex(p, 1234569, "hell_swoo_clear");
                              check = true;
                           }
                        }

                        hell = true;
                     }

                     StringBuilder sb = new StringBuilder("보스 " + (hell ? "헬" : "하드") + " 스우 격파 (" + list + ")");
                     MapleCharacter playerxx = this.getCharacterById(mpcx.getId());
                     if (playerxx != null) {
                        LoggingManager.putLog(new BossLog(playerxx, BossLogType.ClearLog.getType(), sb));
                     }
                  }

                  if (!DBConfig.isGanglim) {
                     if (this.getFieldSetInstance() != null
                           && this.getFieldSetInstance() instanceof HellBlackHeavenBoss) {
                        Center.Broadcast.broadcastMessage(
                              CField.chatMsg(
                                    DBConfig.isGanglim ? 8 : 22,
                                    "[보스격파] [CH."
                                          + (this.getChannel() == 2 ? "20세 이상"
                                                : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                                          + "] '"
                                          + chr.getParty().getPartyMember().getLeader().getName()
                                          + "' 파티("
                                          + list
                                          + ")가 [헬 스우]를 격파하였습니다."));
                     } else {
                        Center.Broadcast.broadcastMessage(
                              CField.chatMsg(
                                    DBConfig.isGanglim ? 8 : 22,
                                    "[보스격파] [CH."
                                          + (this.getChannel() == 2 ? "20세 이상"
                                                : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                                          + "] '"
                                          + chr.getParty().getPartyMember().getLeader().getName()
                                          + "' 파티("
                                          + list
                                          + ")가 [하드 스우]를 격파하였습니다."));
                     }
                  }
               }

               this.doShrine(true, eim);
            }
         } else if (mobid == 8950002
               && this.mapid == 350060600
               && DBConfig.isGanglim
               && this.getFieldSetInstance() != null
               && this.getFieldSetInstance() instanceof HellBlackHeavenBoss) {
            if (chr.getParty() != null) {
               String list = "";
               List<String> names = new ArrayList<>();
               chr.getParty().getPartyMember().getPartyMemberList().forEach(chr_ -> {
                  MapleCharacter cx = chr.getClient().getChannelServer().getPlayerStorage()
                        .getCharacterById(chr_.getId());
                  if (cx != null) {
                     cx.updateOneInfo(1234569, "hell_swoo_clear",
                           String.valueOf(chr.getOneInfoQuestInteger(1234569, "hell_swoo_clear") + 1));
                     names.add(cx.getName());
                  } else if (DBConfig.isGanglim) {
                     this.updateOfflineBossLimit(chr_.getId(), 1234569, "hell_swoo_clear", "1");
                  }
               });
               list = String.join(",", names);
               if (names.size() > 0) {
                  Center.Broadcast.broadcastMessage(
                        CField.chatMsg(
                              22,
                              "[보스격파] [CH."
                                    + (this.getChannel() == 2 ? "20세 이상"
                                          : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                                    + "] '"
                                    + chr.getParty().getPartyMember().getLeader().getName()
                                    + "' 파티("
                                    + list
                                    + ")가 [헬 스우]를 격파하였습니다."));
               }
            }
         } else if (mobid >= 8800003 && mobid <= 8800010) {
            boolean makeZakReal = true;
            Collection<MapleMonster> monsters = this.getAllMonstersThreadsafe();

            for (MapleMonster mons : monsters) {
               if (mons.getId() >= 8800003 && mons.getId() <= 8800010) {
                  makeZakReal = false;
                  break;
               }
            }

            if (makeZakReal) {
               for (MapleMapObject object : monsters) {
                  MapleMonster monsx = (MapleMonster) object;
                  if (monsx.getId() == 8800000) {
                     Point pos = monsx.getTruePosition();
                     this.killAllMonsters(true);
                     this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800000), pos);
                     break;
                  }
               }
            }
         } else if (mobid >= 8800103 && mobid <= 8800110) {
            boolean makeZakReal = true;
            Collection<MapleMonster> monsters = this.getAllMonstersThreadsafe();

            for (MapleMonster monsx : monsters) {
               if (monsx.getId() >= 8800103 && monsx.getId() <= 8800110) {
                  makeZakReal = false;
                  break;
               }
            }

            if (makeZakReal) {
               for (MapleMonster monsxx : monsters) {
                  if (monsxx.getId() == 8800100) {
                     Point pos = monsxx.getTruePosition();
                     this.killAllMonsters(true);
                     this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800100), pos);
                     break;
                  }
               }
            }
         } else if (mobid == 8820008) {
            for (MapleMonster mmo : this.getAllMonstersThreadsafe()) {
               if (mmo.getLinkOid() != monster.getObjectId()) {
                  this.killMonster(mmo, chr, false, false, animation);
               }
            }
         } else if (mobid / 100000 == 98 && chr.getMapId() / 10000000 == 95
               && this.getAllMonstersThreadsafe().size() == 0) {
            switch (chr.getMapId() % 1000 / 100) {
               case 0:
               case 1:
               case 2:
               case 3:
               case 4:
                  chr.getClient().getSession().writeAndFlush(CField.MapEff("monsterPark/clear"));
                  break;
               case 5:
                  if (chr.getMapId() / 1000000 == 952) {
                     chr.getClient().getSession().writeAndFlush(CField.MapEff("monsterPark/clearF"));
                  } else {
                     chr.getClient().getSession().writeAndFlush(CField.MapEff("monsterPark/clear"));
                  }
                  break;
               case 6:
                  chr.getClient().getSession().writeAndFlush(CField.MapEff("monsterPark/clearF"));
            }
         }

         if (type != null && this.speedRunLeader.length() > 0) {
            long endTime = System.currentTimeMillis();
            String time = StringUtil.getReadableMillis(this.speedRunStart, endTime);
            this.broadcastMessage(CWvsContext.serverNotice(5,
                  this.speedRunLeader + "'s squad has taken " + time + " to defeat " + type.name() + "!"));
            this.getRankAndAdd(this.speedRunLeader, time, type, endTime - this.speedRunStart,
                  sqd == null ? null : sqd.getMembers());
            this.endSpeedRun();
         }

         if (withDrops) {
            MapleCharacter dropOwnerr = null;
            if (dropOwner <= 0) {
               dropOwnerr = chr;
            } else {
               dropOwnerr = this.getCharacterById(dropOwner);
               if (dropOwnerr == null) {
                  dropOwnerr = chr;
               }
            }

            this.dropFromMonster(dropOwnerr, monster, instanced, drop);
         }

         this.giveBuff(monster);
      }
   }

   public List<Reactor> getAllReactor() {
      return this.getAllReactorsThreadsafe();
   }

   public List<Reactor> getAllReactorsThreadsafe() {
      ArrayList<Reactor> ret = new ArrayList<>();
      this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();

      try {
         for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            ret.add((Reactor) mmo);
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
      }

      return ret;
   }

   public List<Summoned> getAllSummonsThreadsafe() {
      ArrayList<Summoned> ret = new ArrayList<>();
      this.mapobjectlocks.get(MapleMapObjectType.SUMMON).readLock().lock();

      try {
         for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.SUMMON).values()) {
            if (mmo instanceof Summoned) {
               ret.add((Summoned) mmo);
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.SUMMON).readLock().unlock();
      }

      return ret;
   }

   public final List<Wreckage> getWreckageInRange(Point from, double rangeSq,
         List<MapleMapObjectType> MapObject_types) {
      List<Wreckage> ret = new LinkedList<>();
      this.mapobjectlocks.get(MapleMapObjectType.WRECKAGE).readLock().lock();

      try {
         for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.WRECKAGE).values()) {
            if (mmo instanceof Wreckage) {
               ret.add((Wreckage) mmo);
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.WRECKAGE).readLock().unlock();
      }

      return ret;
   }

   public final List<MapleDynamicFoothold> getDynamicFoodholdInRange(Point from, double rangeSq,
         List<MapleMapObjectType> MapObject_types) {
      List<MapleDynamicFoothold> ret = new LinkedList<>();
      this.mapobjectlocks.get(MapleMapObjectType.DYNAMIC_FOOTHOLD).readLock().lock();

      try {
         for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.DYNAMIC_FOOTHOLD).values()) {
            if (mmo instanceof MapleDynamicFoothold) {
               ret.add((MapleDynamicFoothold) mmo);
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.DYNAMIC_FOOTHOLD).readLock().unlock();
      }

      return ret;
   }

   public void setDynamicObjectVisible(String name, boolean visible) {
      List<MapleDynamicFoothold> list = this.getAllDynamicFoodhold();

      for (MapleDynamicFoothold foothold : new ArrayList<>(list)) {
         List<MapleDynamicFoothold.DynamicFoothold> l = foothold.footholds
               .stream()
               .filter(df -> name == null || df.getFootholdName().equals(name))
               .collect(Collectors.toList());
         l.forEach(df -> df.setCurState(visible ? 1 : 0));
         this.broadcastMessage(CField.syncDynamicFoothold(foothold));
      }
   }

   public final List<MapleDynamicFoothold> getAllDynamicFoodhold() {
      return this.getDynamicFoodholdInRange(new Point(0, 0), Double.POSITIVE_INFINITY,
            Arrays.asList(MapleMapObjectType.DYNAMIC_FOOTHOLD));
   }

   public final List<Wreckage> getAllWreakage() {
      return this.getWreckageInRange(new Point(0, 0), Double.POSITIVE_INFINITY,
            Arrays.asList(MapleMapObjectType.WRECKAGE));
   }

   public final List<RuneStone> getAllRune() {
      return this.getRuneInRange(new Point(0, 0), Double.POSITIVE_INFINITY,
            Arrays.asList(MapleMapObjectType.RUNE_STONE));
   }

   public final List<RuneStone> getRuneInRange(Point from, double rangeSq, List<MapleMapObjectType> MapObject_types) {
      List<MapleMapObject> mapobject = this.getMapObjectsInRange(from, rangeSq);
      List<RuneStone> runes = new ArrayList<>();

      for (int i = 0; i < mapobject.size(); i++) {
         if (mapobject.get(i).getType() == MapleMapObjectType.RUNE_STONE) {
            runes.add((RuneStone) mapobject.get(i));
         }
      }

      return runes;
   }

   public List<MapleMapObject> getAllDoor() {
      return this.getAllDoorsThreadsafe();
   }

   public List<MapleMapObject> getAllDoorsThreadsafe() {
      ArrayList<MapleMapObject> ret = new ArrayList<>();
      this.mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().lock();

      try {
         for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.DOOR).values()) {
            if (mmo instanceof TownPortal) {
               ret.add(mmo);
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().unlock();
      }

      return ret;
   }

   public List<MapleMapObject> getAllMechDoorsThreadsafe() {
      ArrayList<MapleMapObject> ret = new ArrayList<>();
      this.mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().lock();

      try {
         for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.DOOR).values()) {
            if (mmo instanceof OpenGate) {
               ret.add(mmo);
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().unlock();
      }

      return ret;
   }

   public List<MapleMapObject> getCustomChairsThreadsafe() {
      ArrayList<MapleMapObject> ret = new ArrayList<>();
      this.mapobjectlocks.get(MapleMapObjectType.CUSTOM_CHAIR).readLock().lock();

      try {
         for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.CUSTOM_CHAIR).values()) {
            if (mmo instanceof CustomChair) {
               ret.add(mmo);
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.CUSTOM_CHAIR).readLock().unlock();
      }

      return ret;
   }

   public void addCustomChair(final CustomChair chair) {
      this.spawnAndAddRangedMapObject(chair, new Field.DelayedPacketCreation() {
         @Override
         public final void sendPackets(MapleClient c) {
            chair.sendSpawnData(c);
         }
      });
   }

   public void removeCustomChair(MapleCharacter player, CustomChair chair) {
      this.broadcastMessage(CField.customChairResult(player, false, true, true, chair));
      this.removeMapObject(chair);
   }

   public CustomChair getCustomChairByOwner(int ownerID) {
      for (MapleMapObject obj : this.getCustomChairsThreadsafe()) {
         CustomChair chair = (CustomChair) obj;
         if (chair.getOwner() != null && chair.getOwner().getId() == ownerID) {
            return chair;
         }
      }

      return null;
   }

   public List<MapleMapObject> getAllMerchant() {
      return this.getAllHiredMerchantsThreadsafe();
   }

   public List<MapleMapObject> getAllHiredMerchantsThreadsafe() {
      ArrayList<MapleMapObject> ret = new ArrayList<>();
      this.mapobjectlocks.get(MapleMapObjectType.HIRED_MERCHANT).readLock().lock();

      try {
         for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.HIRED_MERCHANT).values()) {
            ret.add(mmo);
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.HIRED_MERCHANT).readLock().unlock();
      }

      return ret;
   }

   public List<MapleMonster> getAllMonster() {
      return this.getAllMonstersThreadsafe();
   }

   public List<MapleMonster> getAllMonstersThreadsafe() {
      ArrayList<MapleMonster> ret = new ArrayList<>();
      this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();

      try {
         for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.MONSTER).values()) {
            ret.add((MapleMonster) mmo);
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
      }

      return ret;
   }

   public List<Grenade> getAllGrenadesThreadsafe() {
      ArrayList<Grenade> ret = new ArrayList<>();
      this.mapobjectlocks.get(MapleMapObjectType.GRENADE).readLock().lock();

      try {
         for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.GRENADE).values()) {
            ret.add((Grenade) mmo);
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.GRENADE).readLock().unlock();
      }

      return ret;
   }

   public List<Integer> getAllUniqueMonsters() {
      ArrayList<Integer> ret = new ArrayList<>();
      this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();

      try {
         for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.MONSTER).values()) {
            int theId = ((MapleMonster) mmo).getId();
            if (!ret.contains(theId)) {
               ret.add(theId);
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
      }

      return ret;
   }

   public final void killAllMonsters(boolean animate) {
      this.killAllMonsters(animate, null);
   }

   public final void killAllMonsters(boolean animate, MapleCharacter player) {
      for (MapleMonster monster : this.getAllMonstersThreadsafe()) {
         if (this instanceof Field_Horntail) {
            this.onMobKilled(monster);
         }

         if (monster.getId() != 8810122 || this.mapid != 240060201) {
            if ((monster.getId() == 8810018 && this.mapid == 240060200
                  || monster.getId() == 8810214 && this.mapid == 240060300) && player != null) {
               if (monster.getStats().isBoss()) {
                  if (player.getParty() != null) {
                     player.getParty().getPartyMember().getPartyMemberList().forEach(p -> {
                        MapleCharacter chr = player.getClient().getChannelServer().getPlayerStorage()
                              .getCharacterById(p.getId());
                        if (chr != null) {
                           chr.setMobCount(monster, 1);
                        }
                     });
                  } else {
                     player.setMobCount(monster, 1);
                  }
               } else {
                  player.setMobCount(monster, 1);
               }

               player.addGuildContributionByBoss(8810018);
               this.doShrine(true, player.getEventInstance());
               this.giveBuff(monster);
               MapleSquad sqd = this.getSquadByMap();
               boolean instanced = sqd != null || monster.getEventInstance() != null || this.getEMByMap() != null;
               this.dropFromMonster(player, monster, instanced);
            }
         } else if (player != null) {
            this.doShrine(true, player.getEventInstance());
            this.giveBuff(monster);
            MapleSquad sqd = this.getSquadByMap();
            boolean instanced = sqd != null || monster.getEventInstance() != null || this.getEMByMap() != null;
            this.dropFromMonster(player, monster, instanced);
            player.addGuildContributionByBoss(monster.getId());
         }

         this.spawnedMonstersOnMap.decrementAndGet();
         monster.setHp(0L);
         if (monster.getEventInstance() != null) {
            monster.getEventInstance().resetMobs();
         }

         this.broadcastMessage(MobPacket.killMonster(monster.getObjectId(), animate ? 1 : 0));
         this.removeMapObject(monster);
         monster.killed();
      }
   }

   public final void killAllMonstersFL(boolean animate, long serialNumber) {
      for (MapleMapObject monstermo : this.getAllMonstersThreadsafe()) {
         MapleMonster monster = (MapleMonster) monstermo;
         if (monster.getFrozenLinkSerialNumber() == serialNumber) {
            this.spawnedMonstersOnMap.decrementAndGet();
            this.removeMapObject(monstermo);
            this.broadcastMessageFL(MobPacket.killMonster(monster.getObjectId(), animate ? 1 : 0), serialNumber);
            monster.killed();
         }
      }
   }

   public final void killAllMonster(int monsId) {
      for (MapleMonster mmo : this.getAllMonstersThreadsafe()) {
         if (mmo.getId() == monsId) {
            this.spawnedMonstersOnMap.decrementAndGet();
            this.removeMapObject(mmo);
            this.broadcastMessage(MobPacket.killMonster(mmo.getObjectId(), 1));
            mmo.killed();
         }
      }
   }

   public final void killMonster(int monsId) {
      for (MapleMapObject mmo : this.getAllMonstersThreadsafe()) {
         if (((MapleMonster) mmo).getId() == monsId) {
            this.spawnedMonstersOnMap.decrementAndGet();
            this.removeMapObject(mmo);
            this.broadcastMessage(MobPacket.killMonster(mmo.getObjectId(), 1));
            ((MapleMonster) mmo).killed();
            break;
         }
      }
   }

   private String MapDebug_Log() {
      StringBuilder sb = new StringBuilder("Defeat time : ");
      sb.append(FileoutputUtil.CurrentReadable_Time());
      sb.append(" | Mapid : ").append(this.mapid);
      sb.append(" Users [").append(this.characters.size()).append("] | ");

      for (MapleCharacter mc : this.characters) {
         sb.append(mc.getName()).append(", ");
      }

      return sb.toString();
   }

   public final void limitReactor(int rid, int num) {
      List<Reactor> toDestroy = new ArrayList<>();
      Map<Integer, Integer> contained = new LinkedHashMap<>();
      this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();

      try {
         for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            Reactor mr = (Reactor) obj;
            if (contained.containsKey(mr.getReactorId())) {
               if (contained.get(mr.getReactorId()) >= num) {
                  toDestroy.add(mr);
               } else {
                  contained.put(mr.getReactorId(), contained.get(mr.getReactorId()) + 1);
               }
            } else {
               contained.put(mr.getReactorId(), 1);
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
      }

      for (Reactor mr : toDestroy) {
         this.destroyReactor(mr.getObjectId());
      }
   }

   public final void destroyReactors(int first, int last) {
      List<Reactor> toDestroy = new ArrayList<>();
      this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();

      try {
         for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            Reactor mr = (Reactor) obj;
            if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
               toDestroy.add(mr);
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
      }

      for (Reactor mr : toDestroy) {
         this.destroyReactor(mr.getObjectId());
      }
   }

   public final void destroyReactor(int oid) {
      final Reactor reactor = this.getReactorByOid(oid);
      if (reactor != null) {
         this.broadcastMessage(CField.destroyReactor(reactor));
         reactor.setAlive(false);
         this.removeMapObject(reactor);
         reactor.setTimerActive(false);
         if (reactor.getDelay() > 0) {
            Timer.MapTimer.getInstance().schedule(new Runnable() {
               @Override
               public final void run() {
                  Field.this.respawnReactor(reactor);
               }
            }, reactor.getDelay());
         }
      }
   }

   public final void reloadReactors() {
      List<Reactor> toSpawn = new ArrayList<>();
      this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();

      try {
         for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            Reactor reactor = (Reactor) obj;
            this.broadcastMessage(CField.destroyReactor(reactor));
            reactor.setAlive(false);
            reactor.setTimerActive(false);
            toSpawn.add(reactor);
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
      }

      for (Reactor r : toSpawn) {
         this.removeMapObject(r);
         if (!r.isCustom()) {
            this.respawnReactor(r);
         }
      }
   }

   public final void resetReactors() {
      this.setReactorState((byte) 0);
   }

   public final void setReactorState() {
      this.setReactorState((byte) 1);
   }

   public final void setReactorState(String name, byte state) {
      this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();

      try {
         for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            Reactor reactor = (Reactor) obj;
            if (reactor.getName().equals(name)) {
               reactor.forceHitReactor(state);
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
      }
   }

   public final void setReactorState(byte state) {
      this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();

      try {
         for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            ((Reactor) obj).forceHitReactor(state);
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
      }
   }

   public final void setReactorDelay(int state) {
      this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();

      try {
         for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            ((Reactor) obj).setDelay(state);
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
      }
   }

   public final void shuffleReactors() {
      this.shuffleReactors(0, 9999999);
   }

   public final void shuffleReactors(int first, int last) {
      List<Point> points = new ArrayList<>();
      this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();

      try {
         for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            Reactor mr = (Reactor) obj;
            if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
               points.add(mr.getPosition());
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
      }

      Collections.shuffle(points);
      this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();

      try {
         for (MapleMapObject objx : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            Reactor mr = (Reactor) objx;
            if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
               mr.setPosition(points.remove(points.size() - 1));
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
      }
   }

   public final void updateMonsterController(MapleMonster monster) {
      if (monster.getId() == 9833004
            || monster.isAlive() && monster.getLinkCID() <= 0 && !monster.getStats().isEscort()) {
         if (monster.getFrozenLinkSerialNumber() <= 0L) {
            if (monster.getController() != null) {
               if (monster.getController().getMap() == this
                     && !(monster.getController().getTruePosition().distanceSq(monster.getTruePosition()) > monster
                           .getRange())) {
                  return;
               }

               monster.getController().stopControllingMonster(monster);
            }

            int mincontrolled = -1;
            MapleCharacter newController = null;

            for (MapleCharacter chr : this.characters) {
               if (chr != null
                     && !chr.isHidden()
                     && !chr.isClone()
                     && (chr.getControlledSize() < mincontrolled || mincontrolled == -1)
                     && chr.getTruePosition().distanceSq(monster.getTruePosition()) <= monster.getRange()) {
                  mincontrolled = chr.getControlledSize();
                  newController = chr;
               }
            }

            if (newController != null) {
               if (DBConfig.isGanglim ? !monster.isFirstAttack() : !monster.getStats().isBoss()) {
                  newController.controlMonster(monster, false);
               } else {
                  newController.controlMonster(monster, true);
                  monster.setControllerHasAggro(true);
               }
            }
         }
      }
   }

   public final MapleMapObject getMapObject(int oid, MapleMapObjectType type) {
      this.mapobjectlocks.get(type).readLock().lock();

      MapleMapObject var3;
      try {
         var3 = this.mapobjects.get(type).get(oid);
      } finally {
         this.mapobjectlocks.get(type).readLock().unlock();
      }

      return var3;
   }

   public final boolean containsNPC(int npcid) {
      this.mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();

      boolean var4;
      try {
         Iterator<MapleMapObject> itr = this.mapobjects.get(MapleMapObjectType.NPC).values().iterator();

         MapleNPC n;
         do {
            if (!itr.hasNext()) {
               return false;
            }

            n = (MapleNPC) itr.next();
         } while (n.getId() != npcid);

         var4 = true;
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
      }

      return var4;
   }

   public Grenade getGrenadeById(int id) {
      this.mapobjectlocks.get(MapleMapObjectType.GRENADE).readLock().lock();

      Grenade var4;
      try {
         Iterator<MapleMapObject> itr = this.mapobjects.get(MapleMapObjectType.GRENADE).values().iterator();

         Grenade grenade;
         do {
            if (!itr.hasNext()) {
               return null;
            }

            grenade = (Grenade) itr.next();
         } while (grenade.getId() != id);

         var4 = grenade;
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.GRENADE).readLock().unlock();
      }

      return var4;
   }

   public MapleNPC getNPCById(int id) {
      this.mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();

      MapleNPC var4;
      try {
         Iterator<MapleMapObject> itr = this.mapobjects.get(MapleMapObjectType.NPC).values().iterator();

         MapleNPC n;
         do {
            if (!itr.hasNext()) {
               return null;
            }

            n = (MapleNPC) itr.next();
         } while (n.getId() != id);

         var4 = n;
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
      }

      return var4;
   }

   public MapleMonster getMonsterById(int id) {
      this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();

      MapleMonster var8;
      try {
         MapleMonster ret = null;

         for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.MONSTER).values()) {
            MapleMonster n = (MapleMonster) obj;
            if (n.getId() == id) {
               ret = n;
               break;
            }
         }

         var8 = ret;
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
      }

      return var8;
   }

   public AffectedArea getMistBySkillId(int id) {
      this.mapobjectlocks.get(MapleMapObjectType.MIST).readLock().lock();

      AffectedArea var8;
      try {
         AffectedArea ret = null;

         for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.MIST).values()) {
            AffectedArea n = (AffectedArea) obj;
            if (n.getSource() != null && n.getSource().getSourceId() == id) {
               ret = n;
               break;
            }
         }

         var8 = ret;
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.MIST).readLock().unlock();
      }

      return var8;
   }

   public List<AffectedArea> getAffectedAreasBySkillID(int skillID) {
      List<AffectedArea> ret = new ArrayList<>();
      this.mapobjectlocks.get(MapleMapObjectType.MIST).readLock().lock();

      Object var8;
      try {
         for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.MIST).values()) {
            AffectedArea n = (AffectedArea) obj;
            if (n != null) {
               if (n.getSource() != null) {
                  if (n.getSource().getSourceId() == skillID) {
                     ret.add(n);
                  }
               } else if (n.getMobSkill() != null && n.getMobSkill().getSkillId() == skillID) {
                  ret.add(n);
               }
            }
         }

         var8 = ret;
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.MIST).readLock().unlock();
      }

      return (List<AffectedArea>) var8;
   }

   public List<AffectedArea> getAffectedAreasBySkillId(int skillID, int ownerID) {
      List<AffectedArea> ret = new ArrayList<>();
      this.mapobjectlocks.get(MapleMapObjectType.MIST).readLock().lock();

      try {
         for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.MIST).values()) {
            AffectedArea n = (AffectedArea) obj;
            if (n != null && n.getSource() != null && n.getSource().getSourceId() == skillID
                  && n.getOwnerId() == ownerID) {
               ret.add(n);
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.MIST).readLock().unlock();
      }

      return ret;
   }

   public AffectedArea getAffectedAreaByMobSkill(MobSkillInfo info) {
      AffectedArea ret = null;
      this.mapobjectlocks.get(MapleMapObjectType.MIST).readLock().lock();

      try {
         for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.MIST).values()) {
            AffectedArea n = (AffectedArea) obj;
            if (n != null && n.getSource() != null && n.getMobSkill() == info) {
               ret = n;
               break;
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.MIST).readLock().unlock();
      }

      return ret;
   }

   public AffectedArea getAffectedAreaBySkillId(int skillID, int ownerID) {
      AffectedArea ret = null;
      this.mapobjectlocks.get(MapleMapObjectType.MIST).readLock().lock();

      try {
         for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.MIST).values()) {
            AffectedArea n = (AffectedArea) obj;
            if (n != null && n.getSource() != null && n.getSource().getSourceId() == skillID
                  && n.getOwnerId() == ownerID) {
               ret = n;
               break;
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.MIST).readLock().unlock();
      }

      return ret;
   }

   public int getAffectedAreaSize(int id, int ownerID) {
      int size = 0;
      this.mapobjectlocks.get(MapleMapObjectType.MIST).readLock().lock();

      try {
         for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.MIST).values()) {
            AffectedArea n = (AffectedArea) obj;
            if (n != null) {
               try {
                  if (n.getSource().getSourceId() == id && n.getOwnerId() == ownerID) {
                     size++;
                  }
               } catch (Exception var10) {
               }
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.MIST).readLock().unlock();
      }

      return size;
   }

   public int countMonsterById(int id) {
      this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();

      int var8;
      try {
         int ret = 0;

         for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.MONSTER).values()) {
            MapleMonster n = (MapleMonster) obj;
            if (n.getId() == id) {
               ret++;
            }
         }

         var8 = ret;
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
      }

      return var8;
   }

   public Reactor getReactorById(int id) {
      this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();

      Reactor var8;
      try {
         Reactor ret = null;

         for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            Reactor n = (Reactor) obj;
            if (n.getReactorId() == id) {
               ret = n;
               break;
            }
         }

         var8 = ret;
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
      }

      return var8;
   }

   public final MapleMonster getMonsterByOid(int oid) {
      MapleMapObject mmo = this.getMapObject(oid, MapleMapObjectType.MONSTER);
      return mmo == null ? null : (MapleMonster) mmo;
   }

   public final MapleNPC getNPCByOid(int oid) {
      MapleMapObject mmo = this.getMapObject(oid, MapleMapObjectType.NPC);
      return mmo == null ? null : (MapleNPC) mmo;
   }

   public final Reactor getReactorByOid(int oid) {
      MapleMapObject mmo = this.getMapObject(oid, MapleMapObjectType.REACTOR);
      return mmo == null ? null : (Reactor) mmo;
   }

   public final Reactor getReactorByName(String name) {
      this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();

      Reactor var5;
      try {
         Iterator var2 = this.mapobjects.get(MapleMapObjectType.REACTOR).values().iterator();

         Reactor mr;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            MapleMapObject obj = (MapleMapObject) var2.next();
            mr = (Reactor) obj;
         } while (!mr.getName().equalsIgnoreCase(name));

         var5 = mr;
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
      }

      return var5;
   }

   public final void spawnGrenade(MapleCharacter player, Grenade grenade) {
      this.addMapObject(grenade);
      this.broadcastMessage(player, CField.throwGrenade(player.getId(), grenade), false);
   }

   public final void removeGrenade(MapleCharacter player, Grenade grenade) {
      this.broadcastMessage(player, CField.destoryGrenade(player.getId(), grenade.getId()), false);
      this.removeMapObject(grenade);
   }

   public final int spawnNpc(int id, Point pos) {
      return this.spawnNpc(id, pos, 0, -1, 0);
   }

   public final int spawnNpc(int id, Point pos, int f, int fh, final int localUserID) {
      final MapleNPC npc = MapleLifeFactory.getNPC(id);
      npc.setPosition(pos);
      npc.setCy(pos.y);
      npc.setRx0(pos.x - 50);
      npc.setRx1(pos.x + 50);
      if (fh < 0) {
         npc.setFh(this.getFootholds().findBelow(pos).getId());
      } else {
         npc.setFh(fh);
      }

      npc.setF(f);
      npc.setCustom(true);
      if (localUserID > 0) {
         npc.setLocalUserID(localUserID);
      }

      this.spawnAndAddRangedMapObject(npc, new Field.DelayedPacketCreation() {
         @Override
         public void sendPackets(MapleClient c) {
            if (localUserID == 0 || c.getPlayer().getId() == localUserID) {
               npc.sendSpawnData(c);
            }
         }
      });
      return npc.getObjectId();
   }

   public final void removeNpcByOid(int oid) {
      this.mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().lock();

      try {
         Iterator<MapleMapObject> itr = this.mapobjects.get(MapleMapObjectType.NPC).values().iterator();

         while (itr.hasNext()) {
            MapleNPC npc = (MapleNPC) itr.next();
            if (npc.isCustom() && npc.getObjectId() == oid) {
               this.broadcastMessage(CField.NPCPacket.removeNPCController(npc.getObjectId()));
               this.broadcastMessage(CField.NPCPacket.removeNPC(npc.getObjectId()));
               itr.remove();
               GameObjects.remove(npc.getObjectId());
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().unlock();
      }
   }

   public final void removeNpcByLocalUserID(int localuserid) {
      this.mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().lock();

      try {
         Iterator<MapleMapObject> itr = this.mapobjects.get(MapleMapObjectType.NPC).values().iterator();

         while (itr.hasNext()) {
            MapleNPC npc = (MapleNPC) itr.next();
            if (npc.isCustom() && npc.getLocalUserID() == localuserid) {
               this.broadcastMessage(CField.NPCPacket.removeNPCController(npc.getObjectId()));
               this.broadcastMessage(CField.NPCPacket.removeNPC(npc.getObjectId()));
               itr.remove();
               GameObjects.remove(npc.getObjectId());
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().unlock();
      }
   }

   public final void removeNpc(int npcid) {
      this.mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().lock();

      try {
         Iterator<MapleMapObject> itr = this.mapobjects.get(MapleMapObjectType.NPC).values().iterator();

         while (itr.hasNext()) {
            MapleNPC npc = (MapleNPC) itr.next();
            if (npc.isCustom() && (npcid == -1 || npc.getId() == npcid)) {
               this.broadcastMessage(CField.NPCPacket.removeNPCController(npc.getObjectId()));
               this.broadcastMessage(CField.NPCPacket.removeNPC(npc.getObjectId()));
               itr.remove();
               GameObjects.remove(npc.getObjectId());
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().unlock();
      }
   }

   public final void hideNpc(int npcid) {
      this.mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();

      try {
         for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.NPC).values()) {
            MapleNPC npc = (MapleNPC) obj;
            if (npcid == -1 || npc.getId() == npcid) {
               this.broadcastMessage(CField.NPCPacket.removeNPCController(npc.getObjectId()));
               this.broadcastMessage(CField.NPCPacket.removeNPC(npc.getObjectId()));
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
      }
   }

   public final void spawnReactorOnGroundBelow(Reactor mob, Point pos) {
      mob.setPosition(pos);
      mob.setCustom(true);
      this.spawnReactor(mob);
   }

   public final void spawnMonster_sSack(MapleMonster mob, Point pos, int spawnType) {
      this.spawnMonster_sSack(mob, pos, spawnType, false, false);
   }

   public final int spawnMonster(MapleMonster monster, Point pos, int spawnType) {
      if (this.getId() == 15) {
         return 0;
      } else {
         monster.setPosition(this.calcPointBelow(new Point(pos.x, pos.y - 1)));
         monster.setMap(this);
         this.checkRemoveAfter(monster);
         this.spawnedMonstersOnMap.incrementAndGet();
         return this.spawnMapObject(monster,
               c -> c.getSession().writeAndFlush(MobPacket.spawnMonster(monster, spawnType, 0)));
      }
   }

   public final int spawnMonsterSponge(final MapleMonster monster, Point pos, final int spongeOid) {
      if (this.getId() == 15) {
         return 0;
      } else {
         monster.setPosition(this.calcPointBelow(new Point(pos.x, pos.y - 1)));
         monster.setMap(this);
         this.checkRemoveAfter(monster);
         this.spawnedMonstersOnMap.incrementAndGet();
         return this.spawnMapObject(monster, new Field.DelayedPacketCreation() {
            @Override
            public final void sendPackets(MapleClient c) {
               c.getSession().writeAndFlush(MobPacket.spawnMonster(monster, -3, spongeOid));
            }
         });
      }
   }

   public final void spawnMonster_sSack(MapleMonster mob, Point pos, int spawnType, boolean overwrite,
         boolean forcedPos) {
      if (this.getId() != 15) {
         if (forcedPos) {
            mob.setPosition(new Point(pos.x, pos.y - 1));
         } else {
            Point pos_ = this.calcPointBelow(new Point(pos.x, pos.y - 1));
            mob.setPosition(pos_ == null ? new Point(pos.x, pos.y - 1) : pos_);
         }

         if (mob.getId() >= 8920000 && mob.getId() <= 8920003) {
            if (mob.getLastSkillUsed().isEmpty()) {
               MobSkillInfo msi = MobSkillFactory.getMobSkill(201, 51);
               mob.setLastSkillUsed(msi, System.currentTimeMillis(), 30000L);
            }
         } else if (mob.getId() >= 8920100 && mob.getId() <= 8920103 && mob.getLastSkillUsed().isEmpty()) {
            MobSkillInfo msi = MobSkillFactory.getMobSkill(201, 55);
            mob.setLastSkillUsed(msi, System.currentTimeMillis(), 30000L);
         }

         this.spawnMonster(mob, spawnType, overwrite);
      }
   }

   public final void spawnMonsterOnGroundBelow(MapleMonster mob, Point pos, boolean overwrite) {
      this.spawnMonster_sSack(mob, pos, -2, overwrite, false);
   }

   public final void spawnMonsterOnGroundBelow(MapleMonster mob, Point pos) {
      this.spawnMonster_sSack(mob, pos, -2);
   }

   public final void spawnMonsterOnGroundBelow(MapleMonster mob, Point pos, byte spawnType) {
      this.spawnMonster_sSack(mob, pos, spawnType);
   }

   public final void spawnMonsterOnGroundBelow(MapleMonster mob, Point pos, byte spawnType, boolean overwrite,
         boolean forcedPos) {
      this.spawnMonster_sSack(mob, pos, spawnType, overwrite, forcedPos);
   }

   public final void spawnMonsterOnGroundBelow(MapleMonster mob, Point pos, byte spawnType, boolean overwrite) {
      this.spawnMonster_sSack(mob, pos, spawnType, overwrite, false);
   }

   public final int spawnMonsterWithEffectBelow(MapleMonster mob, Point pos, int effect) {
      Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
      return this.spawnMonsterWithEffect(mob, effect, spos);
   }

   public final void spawnZakum(int x, int y) {
      Point pos = new Point(x, y);
      Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
      int[] zakpart = new int[] { 8800003, 8800004, 8800005, 8800006, 8800007, 8800008, 8800009, 8800010, 8800002 };
      boolean isMulti = false;

      try {
         if (!DBConfig.isGanglim) {
            if (this.getCharactersSize() > 1) {
               isMulti = true;
            }

            for (MapleCharacter chr : this.getCharacters()) {
               if (chr.isMultiMode()) {
                  isMulti = true;
                  break;
               }
            }
         }
      } catch (Exception var13) {
      }

      for (int i : zakpart) {
         MapleMonster part = MapleLifeFactory.getMonster(i);
         part.setPosition(spos);
         ChangeableStats cs = new ChangeableStats(part.getStats());
         if (i == 8800002) {
            cs.hp = 7000000L;
            part.getStats().setHp(7000000L);
            part.getStats().setMaxHp(7000000L);
         } else {
            cs.hp = 700000L;
            part.getStats().setHp(700000L);
            part.getStats().setMaxHp(700000L);
         }

         if (isMulti) {
            cs.hp *= 3L;
            part.getStats().setHp(part.getStats().getHp() * 3L);
            part.getStats().setMaxHp(part.getStats().getMaxHp() * 3L);
         }

         part.setOverrideStats(cs);
         this.spawnMonster(part, -2);
      }

      if (this.squadSchedule != null) {
         this.cancelSquadSchedule(false);
      }
   }

   public final void spawnChaosZakum(int x, int y) {
      Point pos = new Point(x, y);
      Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
      int[] zakpart = new int[] { 8800103, 8800104, 8800105, 8800106, 8800107, 8800108, 8800109, 8800110, 8800102 };
      boolean isMulti = false;

      try {
         if (!DBConfig.isGanglim) {
            if (this.getCharactersSize() > 1) {
               isMulti = true;
            }

            for (MapleCharacter chr : this.getCharacters()) {
               if (chr.isMultiMode()) {
                  isMulti = true;
                  break;
               }
            }
         }
      } catch (Exception var13) {
      }

      for (int i : zakpart) {
         MapleMonster part = MapleLifeFactory.getMonster(i);
         part.setPosition(spos);
         ChangeableStats cs = new ChangeableStats(part.getStats());
         if (i == 8800102) {
            cs.hp = 84000000000L;
            part.getStats().setHp(84000000000L);
            part.getStats().setMaxHp(84000000000L);
         } else {
            cs.hp = 10500000000L;
            part.getStats().setHp(10500000000L);
            part.getStats().setMaxHp(10500000000L);
         }

         if (isMulti) {
            cs.hp *= 3L;
            part.getStats().setHp(part.getStats().getHp() * 3L);
            part.getStats().setMaxHp(part.getStats().getMaxHp() * 3L);
         }

         part.setOverrideStats(cs);
         this.spawnMonster(part, -2);
      }

      if (this.squadSchedule != null) {
         this.cancelSquadSchedule(false);
      }
   }

   public final void spawnFakeMonsterOnGroundBelow(MapleMonster mob, Point pos) {
      Point spos = this.calcPointBelow(new Point(pos.x, pos.y - 1));
      spos.y--;
      mob.setPosition(spos);
      this.spawnFakeMonster(mob);
   }

   private void checkRemoveAfter(MapleMonster monster) {
      int ra = monster.getStats().getRemoveAfter();
      int action = monster.getStats().getSelfD();
      if (ra > 0 || action != -1 && action != 2 && action != 3 && action != 1 && monster.getLinkCID() <= 0) {
         monster.registerKill(ra * 1000L);
      }
   }

   public final void spawnRevives(MapleMonster monster, int oid, int mobid, boolean fromMap) {
      MapleMonster srcmob = MapleLifeFactory.getMonster(mobid);
      int dieDelay = srcmob.getStats().getDieDelay();
      Timer.MapTimer.getInstance().schedule(() -> {
         monster.setMap(this);
         this.checkRemoveAfter(monster);
         monster.setLinkOid(oid);
         if ((monster.getId() < 9833070 || monster.getId() > 9833074)
               && monster.getMap() instanceof Field_DreamBreaker) {
            Field_DreamBreaker f = (Field_DreamBreaker) monster.getMap();
            long hp = GameConstants.getDreamBreakerHP(f.getStage());
            monster.setHp(hp);
            monster.getStats().setHp(hp);
         }

         if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellLucidBoss) {
            long hp = monster.getStats().getMaxHp();
            ChangeableStats cs = new ChangeableStats(monster.getStats());
            if (DBConfig.isGanglim) {
               cs.hp = hp * 20L;
               monster.getStats().setMaxHp(hp * 20L);
               monster.getStats().setHp(hp * 20L);
               monster.setMaxHp(hp * 20L);
               monster.setHp(hp * 20L);
               monster.setOverrideStats(cs);
            } else {
               cs.hp = hp * 2L * 23L;
               monster.getStats().setMaxHp(hp * 2L * 23L);
               monster.getStats().setHp(hp * 2L * 23L);
               monster.setMaxHp(hp * 2L * 23L);
               monster.setHp(hp * 2L * 23L);
               monster.setOverrideStats(cs);
            }
         }

         int spawnType = -1;
         int createDelay = 0;
         switch (monster.getId()) {
            case 8880161:
            case 8880171:
            case 8880181:
            case 8880183:
            case 8880187:
            case 8880189:
            case 8880193:
            case 8880195:
               spawnType = -3;
               createDelay = oid;
            default:
               monster.getMap().onMobEnter(monster);
               int finalSpawnType = spawnType;
               int finalCreateDelay = createDelay;
               this.spawnAndAddRangedMapObject(monster, c -> c.getSession()
                     .writeAndFlush(MobPacket.spawnMonster(monster, finalSpawnType, finalCreateDelay)));
               this.updateMonsterController(monster);
               this.spawnedMonstersOnMap.incrementAndGet();
               switch (monster.getId()) {
                  case 8810010:
                  case 8810011:
                  case 8810012:
                  case 8810013:
                  case 8810014:
                  case 8810015:
                  case 8810016:
                  case 8810017:
                  case 8810118:
                  case 8810122:
                     monster.setSponge(monster);
                  default:
                     if (fromMap) {
                        this.broadcastMessage(MobPacket.killMonster(oid, 2));
                     }
               }
         }
      }, dieDelay);
   }

   public final void spawnMonster(MapleMonster monster, int spawnType) {
      this.spawnMonster(monster, spawnType, false);
   }

   public final void createSecondAtom(SecondAtom secondAtom) {
      for (SecondAtom.Atom atom : secondAtom.getAtoms()) {
         this.secondAtoms.put(atom.getKey(), atom);
      }

      this.broadcastMessage(CField.createSecondAtom(secondAtom));
   }

   public final SecondAtom.Atom getSecondAtom(int key) {
      for (Entry<Integer, SecondAtom.Atom> entry : this.secondAtoms.entrySet()) {
         if (entry.getValue().getKey() == key) {
            SecondAtom.Atom atom = entry.getValue();
            if (atom != null) {
               return atom;
            }
         }
      }

      return null;
   }

   public final void removeSecondAtom(int key) {
      List<SecondAtom.Atom> removes = new ArrayList<>();

      for (Entry<Integer, SecondAtom.Atom> entry : this.secondAtoms.entrySet()) {
         if (entry.getValue().getKey() == key) {
            SecondAtom.Atom atom = entry.getValue();
            if (atom != null) {
               removes.add(atom);
            }

            this.broadcastMessage(CField.removeSecondAtom(atom.getPlayerID(), key));
         }
      }

      for (SecondAtom.Atom a : removes) {
         this.secondAtoms.remove(a.getKey());
      }
   }

   public final Map<Integer, SecondAtom.Atom> getSecondAtoms() {
      return new HashMap<>(new HashMap<>(this.secondAtoms));
   }

   public final void removeSecondAtom(SecondAtom.Atom atom) {
      this.secondAtoms.remove(atom.getKey());
   }

   public final void createMonsterWithDelay(final MapleMonster monster, Point pos, final int createEffect,
         final int createDelay) {
      if (this.getId() != 15) {
         monster.setPosition(this.calcPointBelow(new Point(pos.x, pos.y - 1)));
         monster.setMap(this);
         this.checkRemoveAfter(monster);
         this.spawnAndAddRangedMapObject(monster, new Field.DelayedPacketCreation() {
            @Override
            public final void sendPackets(MapleClient c) {
               c.getSession().writeAndFlush(MobPacket.spawnMonster(monster, createEffect, createDelay));
            }
         });
         this.updateMonsterController(monster);
         this.spawnedMonstersOnMap.incrementAndGet();
         this.onMobEnter(monster);
      }
   }

   public final void spawnMonster(MapleMonster monster, int spawnType, boolean overwrite) {
      this.spawnMonster(monster, spawnType, overwrite, true);
   }

   public final void spawnMonster(MapleMonster monster, int spawnType, boolean overwrite, boolean useController) {
      monster.setMap(this);
      this.checkRemoveAfter(monster);
      if (monster.getId() == 8930000 || monster.getId() == 8930100) {
         monster.addAttackBlocked(7);
         if (monster.getId() == 8930000) {
            monster.addAttackBlocked(8);
            monster.addAttackBlocked(9);
            monster.addAttackBlocked(10);
            monster.addAttackBlocked(11);
            monster.addAttackBlocked(12);
            monster.addAttackBlocked(13);
            monster.addAttackBlocked(14);
            monster.addAttackBlocked(15);
            monster.addSkillFilter(0);
         } else {
            monster.addAttackBlocked(9);
            monster.addAttackBlocked(10);
            monster.addAttackBlocked(11);
         }

         monster.broadcastAttackBlocked();
      }

      this.spawnAndAddRangedMapObject(
            monster,
            c -> c.getSession()
                  .writeAndFlush(
                        MobPacket.spawnMonster(
                              monster,
                              monster.getStats().getSummonType() > 1 && monster.getStats().getSummonType() != 27
                                    && !overwrite
                                          ? monster.getStats().getSummonType()
                                          : spawnType,
                              0)));
      if (useController) {
         this.updateMonsterController(monster);
      }

      this.spawnedMonstersOnMap.incrementAndGet();
      this.onMobEnter(monster);
   }

   public final int spawnMonsterWithEffect(MapleMonster monster, int effect, Point pos) {
      try {
         monster.setMap(this);
         monster.setPosition(pos);
         this.spawnAndAddRangedMapObject(monster,
               c -> c.getSession().writeAndFlush(MobPacket.spawnMonster(monster, effect, 0)));
         this.updateMonsterController(monster);
         this.spawnedMonstersOnMap.incrementAndGet();
         return monster.getObjectId();
      } catch (Exception var5) {
         return -1;
      }
   }

   public final void spawnFakeMonster(final MapleMonster monster) {
      monster.setMap(this);
      monster.setFake(true);
      this.spawnAndAddRangedMapObject(monster, new Field.DelayedPacketCreation() {
         @Override
         public final void sendPackets(MapleClient c) {
            c.getSession().writeAndFlush(MobPacket.spawnMonster(monster, -4, 0));
         }
      });
      this.updateMonsterController(monster);
      this.spawnedMonstersOnMap.incrementAndGet();
   }

   public final void spawnRune(final RuneStone rune) {
      rune.setMap(this);
      this.spawnAndAddRangedMapObject(rune, new Field.DelayedPacketCreation() {
         @Override
         public void sendPackets(MapleClient c) {
            c.getSession().writeAndFlush(CField.spawnRune(rune, true));
            c.getSession().writeAndFlush(CField.spawnRune(rune, false));
         }
      });
   }

   public final void spawnReactor(final Reactor reactor) {
      reactor.setMap(this);
      this.spawnAndAddRangedMapObject(reactor, new Field.DelayedPacketCreation() {
         @Override
         public final void sendPackets(MapleClient c) {
            c.getSession().writeAndFlush(CField.spawnReactor(reactor));
         }
      });
   }

   private void respawnReactor(Reactor reactor) {
      reactor.setState((byte) 0);
      reactor.setAlive(true);
      this.spawnReactor(reactor);
   }

   public final void spawnWreckage(final Wreckage wreackage) {
      this.spawnAndAddRangedMapObject(wreackage, new Field.DelayedPacketCreation() {
         @Override
         public final void sendPackets(MapleClient c) {
            wreackage.sendSpawnData(c);
         }
      });
   }

   public final int getWrekageCount(int ownerID) {
      return this.wreckages.getOrDefault(ownerID, 0);
   }

   public final void spawnDynamicFoothold(final MapleDynamicFoothold foodhold) {
      this.spawnAndAddRangedMapObject(foodhold, new Field.DelayedPacketCreation() {
         @Override
         public void sendPackets(MapleClient c) {
            foodhold.sendSpawnData(c);
         }
      });
   }

   public final void spawnDoor(final TownPortal door) {
      this.spawnAndAddRangedMapObject(door, new Field.DelayedPacketCreation() {
         @Override
         public final void sendPackets(MapleClient c) {
            door.sendSpawnData(c);
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         }
      });
   }

   public final void spawnMechDoor(final OpenGate door) {
      this.spawnAndAddRangedMapObject(door, new Field.DelayedPacketCreation() {
         @Override
         public final void sendPackets(MapleClient c) {
            c.getSession().writeAndFlush(CField.spawnMechDoor(door, true));
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         }
      });
   }

   public final void spawnSummon(final Summoned summon) {
      summon.updateMap(this);
      this.spawnAndAddRangedMapObject(summon, new Field.DelayedPacketCreation() {
         @Override
         public void sendPackets(MapleClient c) {
            if (summon != null && c.getPlayer() != null
                  && (!summon.isChangedMap() || summon.getOwnerId() == c.getPlayer().getId())) {
               c.getSession().writeAndFlush(CField.SummonPacket.spawnSummon(summon, true));
            }
         }
      });
   }

   public final void spawnSummon(Summoned summon, int duration) {
      this.spawnSummon(summon, duration, true, true);
   }

   public final void spawnSummon(Summoned summon, int duration, boolean showBuffIcon, boolean canIncSummonTime) {
      summon.updateMap(this);
      int dur = duration;
      switch (summon.getSkill()) {
         case 35111001:
         case 35111009:
         case 35111010:
         case 35121003:
         case 152101000:
         case 164121011:
            dur = Integer.MAX_VALUE;
            break;
         case 152111007:
            dur = 15000;
            break;
         case 400001039:
         case 400001040:
         case 400001041:
            dur = 50000;
            break;
         case 400001059:
            dur = 51000;
            break;
         case 400021047:
            dur = 40000;
      }

      if (showBuffIcon) {
         switch (summon.getSkill()) {
            case 33001007:
            case 33001008:
            case 33001009:
            case 33001010:
            case 33001011:
            case 33001012:
            case 33001013:
            case 33001014:
            case 33001015:
               dur = Integer.MAX_VALUE;
         }
      }

      int time = dur;
      summon.setSummonRemoveTime(System.currentTimeMillis() + dur);
      this.spawnAndAddRangedMapObject(
            summon,
            c -> {
               if (summon != null && c.getPlayer() != null) {
                  if (canIncSummonTime && summon.getSkill() != 1301013 && summon.getSkill() != 2311011) {
                     Skill s = SkillFactory.getSkill(summon.getSkill());
                     if (s != null && !s.isNotIncBuffDuration() && !GameConstants.is5thSkill(summon.getSkill())) {
                        MapleCharacter player = summon.getOwner();
                        int t = time;
                        int skillID = summon.getSkill();
                        if ((summon.getMoveAbility() != SummonMoveAbility.STATIONARY || skillID == 5221022)
                              && player != null) {
                           t = (int) (time + time * 0.01 * player.getStat().summonTimeR);
                        }

                        if (skillID == 35111002 && player.getTotalSkillLevel(35120044) > 0) {
                           SecondaryStatEffect effect = SkillFactory.getSkill(35120044)
                                 .getEffect(player.getTotalSkillLevel(35120044));
                           if (effect != null) {
                              t += effect.getY() * 1000;
                           }
                        }

                        if (skillID == 35111008 && player.getTotalSkillLevel(35120048) > 0) {
                           SecondaryStatEffect effect = SkillFactory.getSkill(35120048)
                                 .getEffect(player.getTotalSkillLevel(35120048));
                           if (effect != null) {
                              t += effect.getTime() * 1000;
                           }
                        }

                        if (skillID == 35101012 || skillID == 35111002 || skillID == 35111008 || skillID == 35120002
                              || skillID == 35121009) {
                           int slv = 0;
                           if ((slv = player.getTotalSkillLevel(35120001)) > 0) {
                              SecondaryStatEffect e = SkillFactory.getSkill(35120001).getEffect(slv);
                              if (e != null) {
                                 t = (int) (t + time * (e.getY() * 0.01));
                              }
                           }
                        }

                        if (!DBConfig.isHosting && player.isGM()) {
                           player.dropMessage(5, "소환수 지속 시간 : " + t + "m/s");
                        }

                        summon.setSummonRemoveTime(System.currentTimeMillis() + t);
                     }
                  }

                  int skillId = summon.getSkill();
                  if (skillId == 5211019) {
                     skillId = 5210015;
                  }

                  if (skillId >= 2341501 && skillId <= 2341503) {
                     int diff = skillId == 2341501 ? 0 : (skillId == 2341502 ? -250 : 250);
                     skillId = 2341502;
                     Point pos = summon.getPosition();
                     pos.x += diff;
                     summon.setPosition(pos);
                  }

                  if (c.getPlayer().getId() == summon.getOwnerId() && !c.getPlayer().hasBuffBySkillID(skillId)
                        && skillId != 154110010) {
                     c.getPlayer()
                           .temporaryStatSet(
                                 SecondaryStatFlag.indieSummon,
                                 skillId,
                                 (int) (summon.getSummonRemoveTime() - System.currentTimeMillis()),
                                 summon.getSkillLevel(),
                                 1,
                                 false,
                                 showBuffIcon,
                                 false);
                  }

                  c.getSession().writeAndFlush(CField.SummonPacket.spawnSummon(summon, true));
               }
            });
      if (summon.getSkill() == 400021068
            || summon.getSkill() == 500061012
            || summon.getSkill() == 400021047
            || summon.getSkill() == 131002015
            || summon.getSkill() == 162101012) {
         Timer.MapTimer.getInstance().schedule(() -> {
            this.broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
            if (summon.getOwner() != null) {
               summon.getOwner().removeVisibleMapObject(summon);
               summon.getOwner().removeSummon(summon);
            }

            this.removeMapObject(summon);
         }, duration);
      }
   }

   public final void spawnExtractor(final Extractor ex) {
      this.spawnAndAddRangedMapObject(ex, new Field.DelayedPacketCreation() {
         @Override
         public void sendPackets(MapleClient c) {
            ex.sendSpawnData(c);
         }
      });
   }

   public final void spawnMist(AffectedArea mist) {
      this.spawnAndAddRangedMapObject(mist, mist::sendSpawnData);
      Timer.MapTimer tMan = Timer.MapTimer.getInstance();
      switch (mist.getElement()) {
         default:
            ScheduledFuture<?> poisonSchedule = null;
            mist.setPoisonSchedule(poisonSchedule);
      }
   }

   public final void disappearingItemDrop(MapleMapObject dropper, MapleCharacter owner, Item item, Point pos) {
      Point droppos = this.calcDropPos(pos, pos);
      Drop drop = new Drop(item, droppos, dropper, owner, (byte) 1, false);
      this.broadcastMessage(CField.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 3),
            drop.getTruePosition());
   }

   public final void spawnMesoDrop(int meso, Point position, MapleMapObject dropper, MapleCharacter owner,
         boolean playerDrop, byte droptype) {
      this.spawnMesoDrop(meso, position, dropper, owner, playerDrop, droptype, (byte) 0);
   }

   public final void spawnMesoDrop(
         int meso, Point position, final MapleMapObject dropper, MapleCharacter owner, boolean playerDrop,
         byte droptype, byte explosiveType) {
      final Point droppos = this.calcDropPos(position, position);
      final Drop mdrop = new Drop(meso, droppos, dropper, owner, droptype, playerDrop);
      mdrop.setExplosiveDrop(explosiveType);
      if (explosiveType > 0) {
         mdrop.setDropTime(System.currentTimeMillis());
      }

      this.spawnAndAddRangedMapObject(mdrop, new Field.DelayedPacketCreation() {
         @Override
         public void sendPackets(MapleClient c) {
            c.getSession()
                  .writeAndFlush(CField.dropItemFromMapObject(mdrop, dropper.getTruePosition(), droppos, (byte) 1));
         }
      });
      if (!this.everlast) {
         mdrop.registerExpire(120000L);
         if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000L);
         }
      }
   }

   public final void spawnMobMesoDrop(int meso, final Point position, final MapleMapObject dropper,
         MapleCharacter owner, boolean playerDrop, byte droptype) {
      int fixBossMeso = meso;
      if (!DBConfig.isGanglim && GameConstants.isJinBossMesoFixMap(this.getId())) {
         fixBossMeso = (int) (meso * 0.6);
      }

      final Drop mdrop = new Drop(fixBossMeso, position, dropper, owner, droptype, playerDrop);
      this.spawnAndAddRangedMapObject(mdrop, new Field.DelayedPacketCreation() {
         @Override
         public void sendPackets(MapleClient c) {
            c.getSession()
                  .writeAndFlush(CField.dropItemFromMapObject(mdrop, dropper.getTruePosition(), position, (byte) 1));
         }
      });
      mdrop.registerExpire(120000L);
      if (droptype == 0 || droptype == 1) {
         mdrop.registerFFA(30000L);
      }
   }

   public final void spawnMobDrop(Item idrop, Point dropPos, MapleMonster mob, MapleCharacter chr, byte droptype,
         int questid) {
      this.spawnMobDrop(idrop, dropPos, mob, chr, droptype, questid, 0L);
   }

   public final void spawnMobDrop(Item idrop, Point dropPos, MapleMonster mob, MapleCharacter chr, byte droptype,
         int questid, boolean privateDrop) {
      this.spawnMobDrop(idrop, dropPos, mob, chr, droptype, questid, 0L, false, privateDrop);
   }

   public final void spawnMobDrop(Item idrop, Point dropPos, MapleMonster mob, MapleCharacter chr, byte droptype,
         int questid, long exp) {
      this.spawnMobDrop(idrop, dropPos, mob, chr, droptype, questid, exp, false, false);
   }

   public final void spawnMobDrop(
         Item idrop, Point dropPos, MapleMonster mob, MapleCharacter chr, byte droptype, int questid, long exp,
         boolean exclusive, boolean individual) {
      if (chr.getBossMode() != 1 || exclusive) {
         if (this.getId() != 450010950
               && (this.getFieldSetInstance() == null || !(this.getFieldSetInstance() instanceof NormalJinHillahBoss))
               || idrop.getItemId() != 2633915 && idrop.getItemId() != 2633914) {
            Drop mdrop = new Drop(idrop, dropPos, mob, chr, droptype, false, questid, individual);
            if (mob.getStats().isBoss() && !exclusive) {
               mdrop.setBossDrop(true);
            }

            if (mob.getId() == 8220028) {
               mdrop.setDropSpeed(150);
               mdrop.setDropSlopeAngle(Randomizer.rand(50, 200));
               mdrop.setDropMotionType(2);
               mdrop.setCollisionPickUp(true);
            } else if (mob.getId() == 8220027) {
               mdrop.setCollisionPickUp(true);
            } else if (idrop.getItemId() == 2633343) {
               mdrop.setCollisionPickUp(true);
            }

            if (idrop.getItemId() == 2436496) {
               mdrop.setOwnerID(chr.getId());
               mdrop.setExp(exp);
            } else if (exp > 0L) {
               mdrop.setExp(exp);
               mdrop.setCollisionPickUp(true);
            }

            if (mdrop.getItemId() == 4001536) {
               mdrop.setObjectId(++this.runningOid);
               chr.send(CField.dropItemFromMapObject(mdrop, mdrop.getPosition(), dropPos, (byte) 1));
               chr.send(CField.dropItemFromMapObject(mdrop, mdrop.getPosition(), dropPos, (byte) 0));
               chr.send(CField.removeItemFromMap(mdrop.getObjectId(), 2, chr.getId()));
               int soulCount = chr.getSoulCount();
               chr.setSoulCount((short) (soulCount + 1));
               chr.temporaryStatSet(
                     SecondaryStatFlag.SoulMP,
                     chr.getEquippedSoulSkill(),
                     Integer.MAX_VALUE,
                     chr.getTotalSkillLevel(chr.getEquippedSoulSkill()),
                     chr.getSoulCount(),
                     false,
                     true,
                     false);
               chr.checkSoulState(false);
            } else {
               this.spawnAndAddRangedMapObject(mdrop, c -> {
                  if (c != null && c.getPlayer() != null && (questid <= 0 || c.getPlayer().getQuestStatus(questid) == 1)
                        && mob != null && dropPos != null) {
                     if (DBConfig.isGanglim && mob.getStats().isBoss()) {
                        if (c.getPlayer().getId() == chr.getId()) {
                           c.getSession().writeAndFlush(
                                 CField.dropItemFromMapObject(mdrop, mob.getPosition(), dropPos, (byte) 1));
                        }
                     } else {
                        c.getSession()
                              .writeAndFlush(CField.dropItemFromMapObject(mdrop, mob.getPosition(), dropPos, (byte) 1));
                     }
                  }
               });
            }

            if (exp <= 0L && mdrop.getItemId() != 2437606 && mdrop.getItemId() != 2437607
                  && (mdrop.getItemId() < 2437659 || mdrop.getItemId() > 2437664)) {
               if (mob.getId() != 8220028 && mob.getId() != 8220027) {
                  mdrop.registerExpire(120000L);
               } else {
                  mdrop.registerExpire(10000L);
               }
            } else {
               mdrop.registerExpire(60000L);
            }

            if (droptype == 0 || droptype == 1) {
               mdrop.registerFFA(30000L);
            }

            this.activateItemReactors(mdrop, chr.getClient());
         }
      }
   }

   public final void spawnRandDrop() {
      if (this.mapid == 910000000 && this.channel == 1) {
         this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();

         label46: {
            try {
               Iterator var1 = this.mapobjects.get(MapleMapObjectType.ITEM).values().iterator();

               MapleMapObject o;
               do {
                  if (!var1.hasNext()) {
                     break label46;
                  }

                  o = (MapleMapObject) var1.next();
               } while (!((Drop) o).isRandDrop());
            } finally {
               this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
            }

            return;
         }

         Timer.MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               Point pos = new Point(Randomizer.nextInt(800) + 531, -806);
               int theItem = Randomizer.nextInt(1000);
               int itemid = 0;
               if (theItem < 950) {
                  itemid = GameConstants.normalDrops[Randomizer.nextInt(GameConstants.normalDrops.length)];
               } else if (theItem < 990) {
                  itemid = GameConstants.rareDrops[Randomizer.nextInt(GameConstants.rareDrops.length)];
               } else {
                  itemid = GameConstants.superDrops[Randomizer.nextInt(GameConstants.superDrops.length)];
               }

               Field.this.spawnAutoDrop(itemid, pos);
            }
         }, 20000L);
      }
   }

   public final void spawnAutoDrop(int itemid, final Point pos) {
      Item idrop = null;
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) {
         idrop = ii.randomizeStats((Equip) ii.getEquipById(itemid));
      } else {
         idrop = new Item(itemid, (short) 0, (short) 1, 0);
      }

      idrop.setGMLog("Dropped from auto  on " + this.mapid);
      final Drop mdrop = new Drop(pos, idrop);
      this.spawnAndAddRangedMapObject(mdrop, new Field.DelayedPacketCreation() {
         @Override
         public void sendPackets(MapleClient c) {
            c.getSession().writeAndFlush(CField.dropItemFromMapObject(mdrop, pos, pos, (byte) 1));
         }
      });
      this.broadcastMessage(CField.dropItemFromMapObject(mdrop, pos, pos, (byte) 0));
      if (itemid / 10000 != 291) {
         mdrop.registerExpire(120000L);
      }
   }

   public final void spawnIndividualItemDrop(MapleMapObject dropper, MapleCharacter owner, Item item, Point pos,
         boolean ffaDrop) {
      Point droppos = this.calcDropPos(pos, pos);
      Equip equip = null;
      if (item.getType() == 1) {
         equip = (Equip) item;
      }

      Drop drop = new Drop(item, droppos, dropper, owner, (byte) 2, false, equip);
      if (item.getItemId() == 2434851 || item.getItemId() == 2633626) {
         drop.setCollisionPickUp(true);
      }

      drop.setIndividual(true);
      this.addMapObject(drop);
      owner.send(CField.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 1));
      drop.registerExpire(120000L);
   }

   public final synchronized void spawnIndividualItemDropPos(MapleMapObject dropper, MapleCharacter owner, Item item,
         Point pos, boolean playerDrop) {
      Equip equip = null;
      if (item.getType() == 1) {
         equip = (Equip) item;
      }

      Drop drop = new Drop(item, pos, dropper, owner, (byte) 2, playerDrop, equip);
      if (item.getItemId() == 2434851 || item.getItemId() == 2633626) {
         drop.setCollisionPickUp(true);
         if (item.getItemId() == 2633626) {
            drop.setSpecialType(1);
         }
      }

      drop.setIndividual(true);
      this.addMapObject(drop);
      if (item.getItemId() == 2633626) {
         owner.send(CField.dropItemFromMapObject(drop, pos, pos, (byte) 0));
      } else {
         owner.send(CField.dropItemFromMapObject(drop, pos, pos, (byte) 1));
      }
   }

   public final void spawnItemDrop(final MapleMapObject dropper, MapleCharacter owner, Item item, Point pos,
         boolean ffaDrop, boolean playerDrop) {
      final Point droppos = this.calcDropPos(pos, pos);
      Equip equip = null;
      if (item.getType() == 1) {
         equip = (Equip) item;
      }

      final Drop drop = new Drop(item, droppos, dropper, owner, (byte) 2, playerDrop, equip);
      if (item.getItemId() == 2434851) {
         drop.setCollisionPickUp(true);
      }

      this.spawnAndAddRangedMapObject(drop, new Field.DelayedPacketCreation() {
         @Override
         public void sendPackets(MapleClient c) {
            c.getSession()
                  .writeAndFlush(CField.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 1));
         }
      });
      this.broadcastMessage(CField.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 0));
      if (!this.everlast) {
         drop.registerExpire(120000L);
         this.activateItemReactors(drop, owner.getClient());
      }
   }

   public final void spawnItemDrop(MapleMapObject dropper, MapleCharacter owner, final Drop drop, final Point dropFrom,
         Point dropTo) {
      final Point droppos = this.calcDropPos(dropTo, dropTo);
      this.spawnAndAddRangedMapObject(drop, new Field.DelayedPacketCreation() {
         @Override
         public void sendPackets(MapleClient c) {
            c.getSession().writeAndFlush(CField.dropItemFromMapObject(drop, dropFrom, droppos, (byte) 1));
         }
      });
      this.broadcastMessage(CField.dropItemFromMapObject(drop, dropFrom, droppos, (byte) 0));
      if (!this.everlast) {
         drop.registerExpire(120000L);
         this.activateItemReactors(drop, owner.getClient());
      }
   }

   private void activateItemReactors(Drop drop, MapleClient c) {
      Item item = drop.getItem();
      this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();

      try {
         for (MapleMapObject o : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            Reactor react = (Reactor) o;
            if (react.getReactorType() == 100
                  && item.getItemId() == GameConstants.getCustomReactItem(react.getReactorId(),
                        react.getReactItem().getLeft())
                  && react.getReactItem().getRight() == item.getQuantity()
                  && react.getArea().contains(drop.getTruePosition())
                  && !react.isTimerActive()) {
               Timer.MapTimer.getInstance().schedule(new Field.ActivateItemReactor(drop, react, c), 5000L);
               react.setTimerActive(true);
               break;
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
      }
   }

   public int getItemsSize() {
      return this.mapobjects.get(MapleMapObjectType.ITEM).size();
   }

   public int getExtractorSize() {
      return this.mapobjects.get(MapleMapObjectType.EXTRACTOR).size();
   }

   public int getMobsSize() {
      return this.mapobjects.get(MapleMapObjectType.MONSTER).size();
   }

   public int getMobSizeWithLock() {
      this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();

      int var1;
      try {
         var1 = this.mapobjects.get(MapleMapObjectType.MONSTER).size();
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
      }

      return var1;
   }

   public int getMobsSize(int mobid) {
      this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();

      int var2;
      try {
         if (mobid != 0) {
            return (int) this.mapobjects
                  .get(MapleMapObjectType.MONSTER)
                  .values()
                  .stream()
                  .map(MapleMonster.class::cast)
                  .map(AbstractLoadedMapleLife::getId)
                  .filter(id -> id == mobid)
                  .count();
         }

         var2 = this.mapobjects.get(MapleMapObjectType.MONSTER).size();
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
      }

      return var2;
   }

   public List<Drop> getAllItems() {
      return this.getAllItemsThreadsafe();
   }

   public List<Drop> getAllItemsThreadsafe() {
      ArrayList<Drop> ret = new ArrayList<>();
      this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();

      try {
         for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.ITEM).values()) {
            ret.add((Drop) mmo);
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
      }

      return ret;
   }

   public Point getPointOfItem(int itemid) {
      this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();

      Point var5;
      try {
         Iterator var2 = this.mapobjects.get(MapleMapObjectType.ITEM).values().iterator();

         Drop mm;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            MapleMapObject mmo = (MapleMapObject) var2.next();
            mm = (Drop) mmo;
         } while (mm.getItem() == null || mm.getItem().getItemId() != itemid);

         var5 = mm.getPosition();
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
      }

      return var5;
   }

   public List<AffectedArea> getAllMistsThreadsafe() {
      ArrayList<AffectedArea> ret = new ArrayList<>();
      this.mapobjectlocks.get(MapleMapObjectType.MIST).readLock().lock();

      try {
         for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.MIST).values()) {
            ret.add((AffectedArea) mmo);
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.MIST).readLock().unlock();
      }

      return ret;
   }

   public final void returnEverLastItem(MapleCharacter chr) {
      for (MapleMapObject o : this.getAllItemsThreadsafe()) {
         Drop item = (Drop) o;
         if (item.getOwner() == chr.getId()) {
            item.setPickedUp(true);
            this.broadcastMessage(CField.removeItemFromMap(item.getObjectId(), 2, chr.getId()), item.getTruePosition());
            if (item.getMeso() > 0) {
               chr.gainMeso(item.getMeso(), false);
            } else {
               MapleInventoryManipulator.addFromDrop(chr.getClient(), item.getItem(), false);
            }

            this.removeMapObject(item);
         }
      }

      this.spawnRandDrop();
   }

   public final void talkMonster(String msg, int itemId, int objectid) {
      if (itemId > 0) {
         this.startMapEffect(msg, itemId, false);
      }

      this.broadcastMessage(MobPacket.talkMonster(objectid, itemId, msg));
      this.broadcastMessage(MobPacket.removeTalkMonster(objectid));
   }

   public final void startMapEffect(String msg, int itemId) {
      this.startMapEffect(msg, itemId, false);
   }

   public final void startMapEffect(String msg, int itemId, boolean jukebox) {
      this.startMapEffect(msg, itemId, jukebox, 0);
   }

   public final void startMapEffect(String msg, int itemId, boolean jukebox, int unk) {
      if (this.mapEffect != null) {
         this.broadcastMessage(this.mapEffect.makeDestroyData());
         this.mapEffect = null;
      }

      this.mapEffect = new MapleMapEffect(msg, itemId);
      this.mapEffect.setUnk(unk);
      this.mapEffect.setJukebox(jukebox);
      this.broadcastMessage(this.mapEffect.makeStartData());
      Timer.MapTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (Field.this.mapEffect != null) {
               Field.this.broadcastMessage(Field.this.mapEffect.makeDestroyData());
               Field.this.mapEffect = null;
            }
         }
      }, jukebox ? 300000L : 30000L);
   }

   public final void startMapEffect(String msg, int itemId, int time) {
      if (this.mapEffect != null) {
         this.broadcastMessage(this.mapEffect.makeDestroyData());
         this.mapEffect = null;
      }

      this.mapEffect = new MapleMapEffect(msg, itemId);
      this.broadcastMessage(this.mapEffect.makeStartData());
      Timer.MapTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (Field.this.mapEffect != null) {
               Field.this.broadcastMessage(Field.this.mapEffect.makeDestroyData());
               Field.this.mapEffect = null;
            }
         }
      }, time);
   }

   public final void startExtendedMapEffect(final String msg, final int itemId) {
      this.broadcastMessage(CField.startMapEffect(msg, itemId, true));
      Timer.MapTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            Field.this.broadcastMessage(CField.removeMapEffect());
            Field.this.broadcastMessage(CField.startMapEffect(msg, itemId, false));
         }
      }, 60000L);
   }

   public final void startSimpleMapEffect(String msg, int itemId) {
      this.broadcastMessage(CField.startMapEffect(msg, itemId, true));
   }

   public final void startJukebox(String msg, int itemId) {
      this.startMapEffect(msg, itemId, true);
   }

   public final void addTest() {
      this.characters.add(null);
   }

   public final void calcIncMobGen(MapleCharacter chr, boolean show) {
      this.incMobGen = 0;
      if (!DBConfig.isGanglim) {
         Item item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -21);
         if (item != null
               && (item.getItemId() >= 1142095 && item.getItemId() <= 1142099
                     || item.getItemId() == 1142329
                     || item.getItemId() >= 1142442 && item.getItemId() <= 1142444
                     || item.getItemId() == 1142569
                     || item.getItemId() >= 1142242 && item.getItemId() <= 1142247
                     || item.getItemId() == 1142107
                     || item.getItemId() == 1142108
                     || item.getItemId() == 1142109
                     || item.getItemId() == 1142110)) {
            int inc = 0;
            int expInc = 0;
            int reduceCool = 0;
            int skillID = 0;
            int ignoreMobPdpR = 0;
            switch (item.getItemId()) {
               case 1142095:
                  inc = 10;
                  skillID = 80003170;
                  break;
               case 1142096:
               case 1142097:
                  inc = 10;
                  int var28 = 20;
                  skillID = 80003171;
                  break;
               case 1142098:
                  inc = 20;
                  int var27 = 25;
                  int var41 = 10;
                  skillID = 80003172;
                  break;
               case 1142099:
                  inc = 25;
                  int var26 = 25;
                  int var40 = 20;
                  skillID = 80003173;
                  break;
               case 1142107:
                  inc = 50;
                  int var25 = 40;
                  int var39 = 35;
                  skillID = 80003203;
                  ignoreMobPdpR = 50;
                  break;
               case 1142108:
                  inc = 50;
                  int var24 = 45;
                  int var38 = 35;
                  skillID = 80003204;
                  ignoreMobPdpR = 50;
                  break;
               case 1142109:
                  inc = 50;
                  int var23 = 50;
                  int var37 = 40;
                  skillID = 80003205;
                  ignoreMobPdpR = 60;
                  break;
               case 1142110:
                  inc = 50;
                  int var22 = 55;
                  int var36 = 40;
                  skillID = 80003206;
                  ignoreMobPdpR = 60;
                  break;
               case 1142242:
               case 1142243:
               case 1142244:
               case 1142245:
               case 1142246:
               case 1142247:
                  inc = 60;
                  skillID = 80003207;
                  ignoreMobPdpR = 70;
                  break;
               case 1142329:
                  inc = 30;
                  int var21 = 25;
                  int var35 = 20;
                  skillID = 80003174;
                  break;
               case 1142442:
                  inc = 35;
                  int var20 = 30;
                  int var34 = 25;
                  skillID = 80003175;
                  break;
               case 1142443:
                  inc = 40;
                  int var19 = 30;
                  int var33 = 25;
                  skillID = 80003176;
                  break;
               case 1142444:
                  inc = 45;
                  int var18 = 35;
                  int var32 = 30;
                  skillID = 80003177;
                  break;
               case 1142569:
                  inc = 50;
                  int var17 = 35;
                  int var31 = 30;
                  skillID = 80003178;
            }

            if (inc > 0) {
               if (this.incMobGen < inc) {
                  this.incMobGen = inc;
               }

               if (skillID > 0) {
                  int[] donatorBuffs = new int[] {
                        80003170, 80003171, 80003172, 80003173, 80003174, 80003175, 80003176, 80003177, 80003178,
                        80003203, 80003204, 80003205, 80003206, 80003207
                  };

                  for (int buff : donatorBuffs) {
                     chr.temporaryStatResetBySkillID(buff);
                  }

                  if (ignoreMobPdpR > 0) {
                     chr.temporaryStatSet(skillID, Integer.MAX_VALUE, SecondaryStatFlag.indieIgnoreMobPdpR,
                           ignoreMobPdpR);
                  } else {
                     chr.temporaryStatSet(skillID, Integer.MAX_VALUE, SecondaryStatFlag.indieEmpty, 1);
                  }
               }
            }
         }
      } else {
         Item item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -21);
         if (item != null && item.getItemId() >= 1142093 && item.getItemId() <= 1142099) {
            int reduce = 0;
            int i;
            switch (item.getItemId()) {
               case 1142097:
               case 1142098:
               case 1142099:
                  reduce = 1000;
               default:
                  if (reduce > 0 && reduce < this.reduceMobGenTime) {
                     this.reduceMobGenTime = reduce;
                  }

                  i = 80004003;
            }

            while (i <= 80004009) {
               chr.temporaryStatResetBySkillID(i);
               i++;
            }

            i = 80004003 + (item.getItemId() - 1142093);
            chr.temporaryStatSet(SecondaryStatFlag.indieSummon, i, Integer.MAX_VALUE, 1);
         }

         int inc = 0;
         if (DBConfig.isGanglim && chr.getStat().incExtraMobGegen > 0) {
            inc = chr.getStat().incExtraMobGegen;
         }

         if (inc > 0 && this.incMobGen < inc) {
            this.incMobGen = inc;
         }
      }
   }

   private void spawnRankerNpc() {
      if (!DBConfig.isGanglim) {
         PlayerNPC npcc = DamageMeasurementRank.numberOneNPC;
         if (npcc != null && this.getId() == ServerConstants.TownMap) {
            MapleNPC npc = this.getNPCById(9901001);
            if (npc != null) {
               PlayerNPC pn = (PlayerNPC) npc;
               if (pn.getCharId() != npcc.getCharId()) {
                  this.mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().lock();

                  try {
                     Iterator<MapleMapObject> itr = this.mapobjects.get(MapleMapObjectType.NPC).values().iterator();

                     while (itr.hasNext()) {
                        MapleNPC mn = (MapleNPC) itr.next();
                        if (mn.getId() == 9901001) {
                           this.broadcastMessage(CField.NPCPacket.removeNPCController(npc.getObjectId()));
                           this.broadcastMessage(CField.NPCPacket.removeNPC(npc.getObjectId()));
                           itr.remove();
                           GameObjects.remove(npc.getObjectId());
                        }
                     }
                  } finally {
                     this.mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().unlock();
                  }

                  MapleCharacter npc2 = MapleCharacter.loadCharFromDBFake(npcc.getCharId(), true);
                  PlayerNPC var45 = new PlayerNPC(npc2, 9901001, ServerConstants.TownMap);
                  Center.RankerCustomText = "";
                  this.addMapObject(var45);
               }
            } else {
               MapleCharacter player = MapleCharacter.loadCharFromDBFake(npcc.getCharId(), true);
               PlayerNPC n = new PlayerNPC(player, 9901001, ServerConstants.TownMap);
               Center.RankerCustomText = "";
               this.addMapObject(n);
            }
         }
      } else {
         PlayerNPC npc1 = DamageMeasurementRank.numberOneNPC;
         if (npc1 != null && this.getId() == PlayerNPCConstants.DamageRank1_Map) {
            MapleNPC npc = this.getNPCById(9901001);
            if (npc != null) {
               PlayerNPC pn = (PlayerNPC) npc;
               if (pn.getCharId() != npc1.getCharId()) {
                  this.mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().lock();

                  try {
                     Iterator<MapleMapObject> itr = this.mapobjects.get(MapleMapObjectType.NPC).values().iterator();

                     while (itr.hasNext()) {
                        MapleNPC mn = (MapleNPC) itr.next();
                        if (mn.getId() == 9901001) {
                           this.broadcastMessage(CField.NPCPacket.removeNPCController(npc.getObjectId()));
                           this.broadcastMessage(CField.NPCPacket.removeNPC(npc.getObjectId()));
                           itr.remove();
                           GameObjects.remove(npc.getObjectId());
                        }
                     }
                  } finally {
                     this.mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().unlock();
                  }

                  PlayerNPC n = new PlayerNPC(MapleCharacter.loadCharFromDBFake(npc1.getCharId(), true), 9901001,
                        PlayerNPCConstants.DamageRank1_Map);
                  n.setCoords(PlayerNPCConstants.DamageRank1_X, PlayerNPCConstants.DamageRank1_Y, 0,
                        PlayerNPCConstants.DamageRank1_FH);
                  Center.RankerCustomText = "";
                  this.addMapObject(n);
               }
            } else {
               PlayerNPC n = new PlayerNPC(MapleCharacter.loadCharFromDBFake(npc1.getCharId(), true), 9901001,
                     PlayerNPCConstants.DamageRank1_Map);
               n.setCoords(PlayerNPCConstants.DamageRank1_X, PlayerNPCConstants.DamageRank1_Y, 0,
                     PlayerNPCConstants.DamageRank1_FH);
               Center.RankerCustomText = "";
               this.addMapObject(n);
            }
         }

         PlayerNPC npc2 = DamageMeasurementRank.numberTwoNPC;
         if (npc2 != null && this.getId() == PlayerNPCConstants.DamageRank2_Map) {
            MapleNPC npc = this.getNPCById(9901003);
            if (npc != null) {
               PlayerNPC pn = (PlayerNPC) npc;
               if (pn.getCharId() != npc2.getCharId()) {
                  this.mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().lock();

                  try {
                     Iterator<MapleMapObject> itr = this.mapobjects.get(MapleMapObjectType.NPC).values().iterator();

                     while (itr.hasNext()) {
                        MapleNPC mn = (MapleNPC) itr.next();
                        if (mn.getId() == 9901003) {
                           this.broadcastMessage(CField.NPCPacket.removeNPCController(npc.getObjectId()));
                           this.broadcastMessage(CField.NPCPacket.removeNPC(npc.getObjectId()));
                           itr.remove();
                           GameObjects.remove(npc.getObjectId());
                        }
                     }
                  } finally {
                     this.mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().unlock();
                  }

                  PlayerNPC n = new PlayerNPC(MapleCharacter.loadCharFromDBFake(npc2.getCharId(), true), 9901003,
                        PlayerNPCConstants.DamageRank2_Map);
                  n.setCoords(PlayerNPCConstants.DamageRank2_X, PlayerNPCConstants.DamageRank2_Y, 0,
                        PlayerNPCConstants.DamageRank2_FH);
                  Center.Ranker2CustomText = "";
                  this.addMapObject(n);
               }
            } else {
               PlayerNPC n = new PlayerNPC(MapleCharacter.loadCharFromDBFake(npc2.getCharId(), true), 9901003,
                     PlayerNPCConstants.DamageRank2_Map);
               n.setCoords(PlayerNPCConstants.DamageRank2_X, PlayerNPCConstants.DamageRank2_Y, 0,
                     PlayerNPCConstants.DamageRank2_FH);
               Center.Ranker2CustomText = "";
               this.addMapObject(n);
            }
         }

         PlayerNPC npc3 = DamageMeasurementRank.numberThreeNPC;
         if (npc3 != null && this.getId() == PlayerNPCConstants.DamageRank3_Map) {
            MapleNPC npc = this.getNPCById(9901004);
            if (npc != null) {
               PlayerNPC pn = (PlayerNPC) npc;
               if (pn.getCharId() != npc3.getCharId()) {
                  this.mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().lock();

                  try {
                     Iterator<MapleMapObject> itr = this.mapobjects.get(MapleMapObjectType.NPC).values().iterator();

                     while (itr.hasNext()) {
                        MapleNPC mn = (MapleNPC) itr.next();
                        if (mn.getId() == 9901004) {
                           this.broadcastMessage(CField.NPCPacket.removeNPCController(npc.getObjectId()));
                           this.broadcastMessage(CField.NPCPacket.removeNPC(npc.getObjectId()));
                           itr.remove();
                           GameObjects.remove(npc.getObjectId());
                        }
                     }
                  } finally {
                     this.mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().unlock();
                  }

                  PlayerNPC n = new PlayerNPC(MapleCharacter.loadCharFromDBFake(npc3.getCharId(), true), 9901004,
                        PlayerNPCConstants.DamageRank3_Map);
                  n.setCoords(PlayerNPCConstants.DamageRank3_X, PlayerNPCConstants.DamageRank3_Y, 0,
                        PlayerNPCConstants.DamageRank3_FH);
                  Center.Ranker3CustomText = "";
                  this.addMapObject(n);
               }
            } else {
               PlayerNPC n = new PlayerNPC(MapleCharacter.loadCharFromDBFake(npc3.getCharId(), true), 9901004,
                     PlayerNPCConstants.DamageRank3_Map);
               n.setCoords(PlayerNPCConstants.DamageRank3_X, PlayerNPCConstants.DamageRank3_Y, 0,
                     PlayerNPCConstants.DamageRank3_FH);
               Center.Ranker3CustomText = "";
               this.addMapObject(n);
            }
         }
      }
   }

   public void addPlayer(MapleCharacter chr) {
      if (chr != null) {
         if (this.getId() == ServerConstants.TownMap) {
            try {
               MapleCharacter checkChr = this.getCharacterById(chr.getId());
               if (checkChr != null) {
                  this.characters.remove(checkChr);
               }
            } catch (Exception var27) {
            }
         }

         try {
            this.mapobjectlocks.get(MapleMapObjectType.PLAYER).writeLock().lock();

            try {
               this.mapobjects.get(MapleMapObjectType.PLAYER).put(chr.getObjectId(), chr);
            } finally {
               this.mapobjectlocks.get(MapleMapObjectType.PLAYER).writeLock().unlock();
            }

            this.characters.add(chr);
            chr.setChangeTime();
            if (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -27) != null
                  && chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -28) != null) {
               if (chr.getAndroid() == null) {
                  chr.setAndroid(chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -27).getAndroid());
               } else {
                  chr.updateAndroid();
               }
            }

            this.spawnRankerNpc();
            if (GameConstants.isTeamMap(this.mapid) && !chr.inPVP()) {
               chr.setTeam(this.getAndSwitchTeam() ? 0 : 1);
            }

            if (!this.onFirstUserEnter.equals("") && this.getCharactersSize() == 1) {
               if (chr.isGM()) {
                  System.out.println("[onFirstUserEnter] " + this.onFirstUserEnter);
                  chr.dropMessage(5, "[onFirstUserEnter] " + this.onFirstUserEnter);
               }

               if (ScriptManager.get()._scripts.get(this.onFirstUserEnter) != null) {
                  ScriptManager.runScript(chr.getClient(), this.onFirstUserEnter, null, null);
               } else {
                  MapScriptMethods.startScript_FirstUser(chr.getClient(), this.onFirstUserEnter);
               }
            }

            try {
               if (!GameConstants.isIllium(chr.getJob())) {
                  Integer value = (Integer) chr.getTempKeyValue("NewFlyingValue");
                  Integer reason = (Integer) chr.getTempKeyValue("NewFlyingReason");
                  Long till = (Long) chr.getTempKeyValue("NewFlyingTime");
                  if (reason != null && till != null && value != null) {
                     int duration = (int) (till - System.currentTimeMillis());
                     if (reason != 400001007 && reason != 400001052 && reason != 80001242) {
                        chr.temporaryStatSet(SecondaryStatFlag.NewFlying, reason, duration, value);
                     }

                     chr.removeTempKeyValue("NewFlyingValue");
                     chr.removeTempKeyValue("NewFlyingReason");
                     chr.removeTempKeyValue("NewFlyingTime");
                  }
               }
            } catch (Exception var25) {
            }

            if (!this.onUserEnter.equals("")) {
               if (chr.isGM()) {
                  System.out.println("[onUserEnter] " + this.onUserEnter);
                  chr.dropMessage(5, "[onUserEnter] " + this.onUserEnter);
               }

               MapScriptMethods.startScript_User(chr.getClient(), this.onUserEnter);
            }

            byte[] packet = null;

            try {
               packet = CField.spawnPlayerMapobject(chr);
               if (!chr.isHidden()) {
                  this.broadcastMessage(chr, packet, false);
               } else {
                  this.broadcastGMMessage(chr, packet, false);
               }
            } catch (NullPointerException var24) {
               FileoutputUtil.outputFileError("Log_Packet_Except.rtf", var24);
            }

            if (GameConstants.isPhantom(chr.getJob())) {
               chr.getClient().getSession().writeAndFlush(CField.updateCardStack(chr.getCardStack()));
            }

            this.calcIncMobGen(chr, true);
            if (!chr.isClone()) {
               try {
                  this.sendObjectPlacement(chr);
               } catch (Exception var23) {
                  FileoutputUtil.log("Log_Player_Except.rtf",
                        "[오류] 필드 addPlayer 함수 실행 중 sendObjectPlacement 오류 발생 : " + var23.toString());
                  System.out.println("AddPlayer Err");
                  var23.printStackTrace();
               }

               if (packet != null) {
                  chr.getClient().getSession().writeAndFlush(packet);
               }

               if (GameConstants.isTeamMap(this.mapid) && !chr.inPVP()) {
                  chr.getClient().getSession().writeAndFlush(CField.showEquipEffect(chr.getTeam()));
               }

               if (chr.getBuffedValue(SecondaryStatFlag.GuidedArrow) != null) {
                  ForceAtom.AtomInfo info = new ForceAtom.AtomInfo();
                  ForceAtom forceAtom = new ForceAtom(
                        info, 400031000, chr.getId(), false, true, chr.getId(), ForceAtom.AtomType.GUIDED_ARROW,
                        Collections.singletonList(0), 1);
                  this.broadcastMessage(CField.getCreateForceAtom(forceAtom));
               }

               if (GameConstants.isAdele(chr.getJob()) && chr.getBuffedValue(SecondaryStatFlag.Creation) != null) {
                  chr.tryCreateEtherCreation(chr.getTruePosition(), false);
               }

               switch (this.mapid) {
                  case 109090300:
                     chr.getClient().getSession().writeAndFlush(CField.showEquipEffect(chr.isCatching ? 1 : 0));
                     break;
                  case 450002011:
                  case 450002012:
                  case 450002013:
                  case 450002014:
                  case 450002015:
                  case 450002021:
                  case 450002200:
                  case 450002201:
                  case 450002301:
                  case 921170050:
                  case 921170100:
                  case 954080200:
                  case 954080300:
                  case 993000868:
                  case 993000873:
                  case 993000874:
                  case 993000875:
                  case 993000877:
                     chr.getClient().getSession()
                           .writeAndFlush(CField.momentAreaOnOffAll(Collections.singletonList("swim01")));
                     break;
                  case 689000000:
                  case 689000010:
                     chr.getClient().getSession().writeAndFlush(CField.getCaptureFlags(this));
                     break;
                  case 809000101:
                  case 809000201:
                     chr.getClient().getSession().writeAndFlush(CField.showEquipEffect());
               }
            }

            if (objects.users.achievement.caching.mission.submission.checkvalue.Field.allFieldIDList
                  .contains(this.mapid)) {
               AchievementFactory.checkField(chr);
            }

            try {
               for (int i = 0; i < 3; i++) {
                  MaplePet pet = chr.getPet(i);
                  if (pet != null) {
                     Item item = chr.getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition());
                     if (item != null && item.getPet() != null) {
                        pet.setPos(chr.getPosition());
                        chr.getClient().getSession()
                              .writeAndFlush(PetPacket.updatePet(chr, pet, item, false, chr.getPetLoot()));
                        this.broadcastMessage(PetPacket.showPet(chr, item.getPet(), false, false, 0));
                     } else {
                        chr.unequipPet(pet, false, false, 0);
                        chr.send(CWvsContext.enableActions(chr));
                     }
                  }
               }
            } catch (Exception var28) {
               System.out.println("AddPlayer 22 Err");
               var28.printStackTrace();
            }

            if (chr.getOneInfoQuestInteger(190823, "grade") > 0) {
               chr.send(CField.onZodiacInfo());
               chr.send(CField.onZodiacRankInfo(chr.getId(), chr.getOneInfoQuestInteger(190823, "grade")));
            }

            String showmotion = chr.getOneInfoQuest(27042, "use");
            int motionvalue = 0;
            if (!showmotion.equals("")) {
               try {
                  motionvalue = Integer.parseInt(showmotion);
               } catch (Exception var22) {
                  motionvalue = 0;
               }
            }

            this.broadcastMessage(CWvsContext.updateWeaponMotion(chr.getId(), motionvalue));
            String showmedal = chr.getOneInfoQuest(101149, "1007");
            this.broadcastMessage(CWvsContext.updateShowMedal(chr.getId(), showmedal.equals("0") ? 0 : 1));
            String showitemeffect = chr.getOneInfoQuest(101149, "1009");
            this.broadcastMessage(CWvsContext.updateShowItemEffect(chr.getId(), showitemeffect.equals("0") ? 0 : 1));
            String souleffect = chr.getOneInfoQuest(26535, "effect");
            this.broadcastMessage(CField.setSoulEffect(chr, (byte) (souleffect.equals("0") ? 0 : 1)));
            chr.checkEquippedMasterLabel();
            Android android = chr.getAndroid();
            if (android != null) {
               android.setPos(chr.getPosition());
               this.broadcastMessage(CField.spawnAndroid(chr, android));
            }

            Party party = chr.getParty();
            if (party != null && !chr.isClone()) {
               chr.silentPartyUpdate();
               chr.updatePartyMemberHP();
               chr.receivePartyMemberHP();
            }

            if (!chr.isClone()) {
               new ArrayList<>(chr.getSummons()).forEach(summon -> {
                  if (summon != null && summon.getSkill() != 154110010) {
                     long duration = summon.getSummonRemoveTime() - System.currentTimeMillis();
                     if (duration > 0L) {
                        summon.setPosition(chr.getTruePosition());
                        chr.addVisibleMapObject(summon);
                        this.spawnSummon(summon, (int) duration);
                     }
                  }
               });
            }

            int breakTimeFieldLevel = this.checkBreakTimeField();
            if (breakTimeFieldLevel != -1) {
               this.startBreakTimeFieldTask();
               chr.send(CField.breakTimeFieldEnter());
               if (breakTimeFieldLevel > 0) {
                  TextEffect e = new TextEffect(
                        chr.getId(), "#fn나눔고딕 ExtraBold##fs26#    버닝 " + breakTimeFieldLevel + "단계 : 경험치 "
                              + breakTimeFieldLevel * 10 + "% 추가지급!!    ");
                  chr.send(e.encodeForLocal());
               }
            }

            if (this.mapEffect != null) {
               this.mapEffect.sendStartData(chr.getClient());
            }

            if (DBConfig.isGanglim) {
            }

            if (this.timeLimit > 0 && this.getForcedReturnMap() != null && !chr.isClone()) {
               chr.startMapTimeLimitTask(this.timeLimit, this.getForcedReturnMap());
            }

            if (chr.getKeyValue2("add_bdr") > 0) {
               chr.giveBossDamage();
            }

            if (chr.getKeyValue2("add_critical_dam") > 0) {
               chr.giveCriticalDamage();
            }

            if (chr.getBuffedValue(SecondaryStatFlag.RideVehicle) != null
                  && !GameConstants.isResist(chr.getJob())
                  && FieldLimitType.Mount.check(this.fieldLimit)) {
               chr.temporaryStatReset(SecondaryStatFlag.RideVehicle);
            }

            if (chr.getJob() == 3200 || chr.getJob() >= 3210 && chr.getJob() <= 3212) {
               chr.resetDarkLightning();
            }

            chr.giveDonatorBuff();
            this.onEnter(chr);
            if (!chr.isClone()) {
               EventInstanceManager eim = chr.getEventInstance();
               if (eim != null && eim.isTimerStarted() && !chr.isClone() && this.mapid != ServerConstants.TownMap) {
                  chr.send(CField.getClock((int) (chr.getEventInstance().getTimeLeft() / 1000L)));
               }

               if (this.hasClock()) {
                  Calendar cal = Calendar.getInstance();
                  chr.getClient().getSession()
                        .writeAndFlush(CField.getClockTime(cal.get(11), cal.get(12), cal.get(13)));
               }

               if (chr.getEventInstance() != null) {
                  chr.getEventInstance().onMapLoad(chr);
               }

               MapleEvent.mapLoad(chr, this.channel);
               if (this.getSquadBegin() != null && this.getSquadBegin().getTimeLeft() > 0L
                     && this.getSquadBegin().getStatus() == 1) {
                  chr.getClient().getSession()
                        .writeAndFlush(CField.getClock((int) (this.getSquadBegin().getTimeLeft() / 1000L)));
               }

               if (this.mapid / 1000 != 105100 && this.mapid / 100 != 8020003 && this.mapid / 100 != 8020008
                     && this.mapid != 271040100) {
                  MapleSquad sqd = this.getSquadByMap();
                  EventManager em = this.getEMByMap();
                  if (!this.squadTimer
                        && sqd != null
                        && chr.getName().equals(sqd.getLeaderName())
                        && em != null
                        && em.getProperty("leader") != null
                        && em.getProperty("leader").equals("true")
                        && this.checkStates) {
                     this.doShrine(false, null);
                     this.squadTimer = true;
                  }
               }

               if (this.getNumMonsters() > 0
                     && (this.mapid == 280030001
                           || this.mapid == 240060201
                           || this.mapid == 280030000
                           || this.mapid == 240060200
                           || this.mapid == 220080001
                           || this.mapid == 541020800
                           || this.mapid == 541010100)) {
                  String music = "Bgm09/TimeAttack";
                  switch (this.mapid) {
                     case 240060200:
                     case 240060201:
                        music = "Bgm14/HonTale";
                        break;
                     case 280030000:
                     case 280030001:
                        music = "Bgm06/FinalFight";
                  }

                  chr.getClient().getSession().writeAndFlush(CField.musicChange(music));
               }

               if (this.mapid != 914000000 && this.mapid != 927000000) {
                  if ((this.mapid != 105100300 || chr.getLevel() < 91)
                        && (this.mapid == 140090000 || this.mapid == 105100301 || this.mapid == 105100401
                              || this.mapid == 105100100)) {
                     chr.getClient().getSession().writeAndFlush(CWvsContext.temporaryStats_Reset());
                  }
               } else {
                  chr.getClient().getSession().writeAndFlush(CWvsContext.temporaryStats_Aran());
               }
            }

            if (GameConstants.isEvan(chr.getJob()) && chr.getJob() >= 2200) {
               if (chr.getDragon() == null) {
                  chr.makeDragon();
               } else {
                  chr.getDragon().setPosition(chr.getPosition());
               }

               if (chr.getDragon() != null) {
                  this.broadcastMessage(CField.spawnDragon(chr.getDragon()));
               }
            }

            if (this.permanentWeather > 0) {
               chr.getClient().getSession().writeAndFlush(CField.startMapEffect("", this.permanentWeather, false));
            }

            if (this.getPlatforms().size() > 0) {
               chr.getClient().getSession().writeAndFlush(CField.getMovingPlatforms(this));
            }

            if (this.environment.size() > 0) {
               chr.getClient().getSession().writeAndFlush(CField.getUpdateEnvironment(this));
            }

            switch (this.mapid) {
               case 993033400:
               default:
                  chr.refreshGiftShowX3();
            }
         } catch (Exception var29) {
            System.out.println("[오류] 필드 addPlayer 함수 실행중 오류 발생 (캐릭터 이름: " + chr.getName() + " ) : " + var29.toString());
            var29.printStackTrace();
         }
      }
   }

   public int getNumItems() {
      this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();

      int var1;
      try {
         var1 = this.mapobjects.get(MapleMapObjectType.ITEM).size();
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
      }

      return var1;
   }

   public int getNumMonsters() {
      this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();

      int var1;
      try {
         var1 = this.mapobjects.get(MapleMapObjectType.MONSTER).size();
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
      }

      return var1;
   }

   public void doShrine(final boolean spawned, EventInstanceManager eim) {
      if (eim != null && eim.hasEventTimer()) {
         eim.restartEventTimer(20000L);
      }

      if (this.squadSchedule != null) {
         this.cancelSquadSchedule(true);
      }

      MapleSquad sqd = this.getSquadByMap();
      if (sqd != null) {
         final int mode = this.mapid == 280030000 ? 1
               : (this.mapid == 280030001 ? 2 : (this.mapid != 240060200 && this.mapid != 240060201 ? 0 : 3));
         EventManager em = this.getEMByMap();
         if (sqd != null && em != null && this.getCharactersSize() > 0) {
            final String leaderName = sqd.getLeaderName();
            final String state = em.getProperty("state");
            Field returnMapa = this.getForcedReturnMap();
            if (returnMapa == null || returnMapa.getId() == this.mapid) {
               returnMapa = this.getReturnMap();
            }

            if (mode == 1 || mode == 2) {
               this.broadcastMessage(CField.showChaosZakumShrine(spawned, 5));
            } else if (mode == 3) {
               this.broadcastMessage(CField.showChaosHorntailShrine(spawned, 5));
            } else {
               this.broadcastMessage(CField.showHorntailShrine(spawned, 5));
            }

            if (spawned) {
               this.broadcastMessage(CField.getClock(300));
            }
            final Field returnMapa2 = returnMapa;
            Runnable run;
            if (spawned) {
               run = new Runnable() {
                  @Override
                  public void run() {
                     MapleSquad sqnow = Field.this.getSquadByMap();
                     if (Field.this.getCharactersSize() > 0
                           && sqnow != null
                           && sqnow.getStatus() == 2
                           && sqnow.getLeaderName().equals(leaderName)
                           && Field.this.getEMByMap().getProperty("state").equals(state)) {
                        byte[] packet;
                        if (mode != 1 && mode != 2) {
                           packet = CField.showHorntailShrine(spawned, 0);
                        } else {
                           packet = CField.showChaosZakumShrine(spawned, 0);
                        }

                        for (MapleCharacter chr : Field.this.getCharactersThreadsafe()) {
                           chr.getClient().getSession().writeAndFlush(packet);
                           chr.changeMap(returnMapa2, returnMapa2.getPortal(0));
                        }

                        Field.this.checkStates("");
                        Field.this.resetFully();
                     }
                  }
               };
            } else {
               final List<MapleMonster> monsterz = this.getAllMonstersThreadsafe();
               final List<Integer> monsteridz = new ArrayList<>();

               for (MapleMapObject m : monsterz) {
                  monsteridz.add(m.getObjectId());
               }

               run = new Runnable() {
                  @Override
                  public void run() {
                     MapleSquad sqnow = Field.this.getSquadByMap();
                     if (Field.this.getCharactersSize() > 0
                           && Field.this.getNumMonsters() == monsterz.size()
                           && sqnow != null
                           && sqnow.getStatus() == 2
                           && sqnow.getLeaderName().equals(leaderName)
                           && Field.this.getEMByMap().getProperty("state").equals(state)) {
                        boolean passed = monsterz.isEmpty();

                        for (MapleMapObject m : Field.this.getAllMonstersThreadsafe()) {
                           for (int i : monsteridz) {
                              if (m.getObjectId() == i) {
                                 passed = true;
                                 break;
                              }
                           }

                           if (passed) {
                              break;
                           }
                        }

                        if (passed) {
                           byte[] packet;
                           if (mode != 1 && mode != 2) {
                              packet = CField.showHorntailShrine(spawned, 0);
                           } else {
                              packet = CField.showChaosZakumShrine(spawned, 0);
                           }

                           for (MapleCharacter chr : Field.this.getCharactersThreadsafe()) {
                              chr.getClient().getSession().writeAndFlush(packet);
                              chr.changeMap(returnMapa2, returnMapa2.getPortal(0));
                           }

                           Field.this.checkStates("");
                           Field.this.resetFully();
                        }
                     }
                  }
               };
            }

            this.squadSchedule = Timer.MapTimer.getInstance().schedule(run, 300000L);
         }
      }
   }

   public final MapleSquad getSquadByMap() {
      MapleSquad.MapleSquadType zz = null;
      switch (this.mapid) {
         case 105100300:
         case 105100400:
            zz = MapleSquad.MapleSquadType.bossbalrog;
            break;
         case 211070100:
         case 211070101:
         case 211070110:
            zz = MapleSquad.MapleSquadType.vonleon;
            break;
         case 240060200:
            zz = MapleSquad.MapleSquadType.horntail;
            break;
         case 240060201:
            zz = MapleSquad.MapleSquadType.chaosht;
            break;
         case 270050100:
            zz = MapleSquad.MapleSquadType.pinkbean;
            break;
         case 271040100:
            zz = MapleSquad.MapleSquadType.cygnus;
            break;
         case 280030000:
            zz = MapleSquad.MapleSquadType.zak;
            break;
         case 280030001:
            zz = MapleSquad.MapleSquadType.chaoszak;
            break;
         case 551030200:
            zz = MapleSquad.MapleSquadType.scartar;
            break;
         case 802000111:
            zz = MapleSquad.MapleSquadType.nmm_squad;
            break;
         case 802000211:
            zz = MapleSquad.MapleSquadType.vergamot;
            break;
         case 802000311:
            zz = MapleSquad.MapleSquadType.tokyo_2095;
            break;
         case 802000411:
            zz = MapleSquad.MapleSquadType.dunas;
            break;
         case 802000611:
            zz = MapleSquad.MapleSquadType.nibergen_squad;
            break;
         case 802000711:
            zz = MapleSquad.MapleSquadType.dunas2;
            break;
         case 802000801:
         case 802000802:
         case 802000803:
            zz = MapleSquad.MapleSquadType.core_blaze;
            break;
         case 802000821:
         case 802000823:
            zz = MapleSquad.MapleSquadType.aufheben;
            break;
         default:
            return null;
      }

      return GameServer.getInstance(this.channel).getMapleSquad(zz);
   }

   public final MapleSquad getSquadBegin() {
      return this.squad != null ? GameServer.getInstance(this.channel).getMapleSquad(this.squad) : null;
   }

   public final EventManager getEMByMap() {
      String em = null;
      switch (this.mapid) {
         case 105100300:
            em = "BossBalrog_NORMAL";
            break;
         case 105100400:
            em = "BossBalrog_EASY";
            break;
         case 211070100:
         case 211070101:
         case 211070110:
            em = "VonLeonBattle";
            break;
         case 240060200:
            em = "HorntailBattle";
            break;
         case 240060201:
            em = "ChaosHorntail";
            break;
         case 270050100:
            em = "PinkBeanBattle";
            break;
         case 271040100:
            em = "CygnusBattle";
            break;
         case 280030000:
            em = "ZakumBattle";
            break;
         case 280030001:
            em = "ChaosZakum";
            break;
         case 551030200:
            em = "ScarTarBattle";
            break;
         case 802000111:
            em = "NamelessMagicMonster";
            break;
         case 802000211:
            em = "Vergamot";
            break;
         case 802000311:
            em = "2095_tokyo";
            break;
         case 802000411:
            em = "Dunas";
            break;
         case 802000611:
            em = "Nibergen";
            break;
         case 802000711:
            em = "Dunas2";
            break;
         case 802000801:
         case 802000802:
         case 802000803:
            em = "CoreBlaze";
            break;
         case 802000821:
         case 802000823:
            em = "Aufhaven";
            break;
         default:
            return null;
      }

      return GameServer.getInstance(this.channel).getEventSM().getEventManager(em);
   }

   public final void resetIncMobGen(MapleCharacter chr) {
      boolean f = false;

      for (MapleCharacter p : this.getCharactersThreadsafe()) {
         if (p.getId() != chr.getId()) {
            Item item = p.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -21);
            if (item != null) {
               if (DBConfig.isGanglim) {
                  if (item.getItemId() >= 1142097 && item.getItemId() <= 1142099) {
                     int reduce = 0;
                     switch (item.getItemId()) {
                        case 1142095:
                        case 1142096:
                        case 1142097:
                           reduce = 1000;
                     }

                     if (reduce > 0) {
                        if (this.reduceMobGenTime >= reduce) {
                           this.reduceMobGenTime = reduce;
                        }

                        f = true;
                     }
                  }
               } else if (item.getItemId() >= 1142095 && item.getItemId() <= 1142099
                     || item.getItemId() == 1142329
                     || item.getItemId() >= 1142442 && item.getItemId() <= 1142444
                     || item.getItemId() == 1142569) {
                  int inc = 0;
                  switch (item.getItemId()) {
                     case 1142095:
                     case 1142096:
                     case 1142097:
                        inc = 10;
                        break;
                     case 1142098:
                        inc = 20;
                        break;
                     case 1142099:
                        inc = 25;
                        break;
                     case 1142329:
                        inc = 30;
                        break;
                     case 1142442:
                        inc = 35;
                        break;
                     case 1142443:
                        inc = 40;
                        break;
                     case 1142444:
                        inc = 45;
                        break;
                     case 1142569:
                        inc = 50;
                  }

                  if (inc > 0) {
                     if (this.incMobGen >= inc) {
                        this.incMobGen = inc;
                     }

                     f = true;
                  }
               }
            }
         }
      }

      if (!f) {
         this.incMobGen = 0;
      }
   }

   public final void removePlayer(MapleCharacter chr) {
      if (chr != null) {
         try {
            this.onLeave(chr);
         } catch (Exception var11) {
            System.out.println(
                  "[오류] 필드 removePlayer 함수 실행중 오류 발생 Point 1 (캐릭터 이름: " + chr.getName() + " ) : " + var11.toString());
            var11.printStackTrace();
         }

         if (this.characters.remove(chr)) {
            try {
               this.getAllFieldAttackObj().stream().filter(objx -> objx.getPlayerID() == chr.getId())
                     .collect(Collectors.toList()).forEach(objx -> {
                        if (objx != null) {
                           this.removeMapObject(objx);
                        }
                     });
            } catch (Exception var10) {
               System.out.println("[오류] 필드 removePlayer 함수 실행중 오류 발생 Point 2 (캐릭터 이름: " + chr.getName() + " ) : "
                     + var10.toString());
               var10.printStackTrace();
            }

            try {
               if (this.everlast) {
                  this.returnEverLastItem(chr);
               }

               this.removeMapObject(chr);

               for (SecondAtom.Atom atom : new ArrayList<>(chr.getSecondAtoms())) {
                  if (atom != null) {
                     chr.removeSecondAtom(atom.getKey());
                     this.removeSecondAtom(atom);
                     this.broadcastMessage(CField.removeSecondAtom(chr.getId(), atom.getKey()));
                  }
               }
            } catch (Exception var16) {
               System.out.println("[오류] 필드 removePlayer 함수 실행중 오류 발생 Point 3 (캐릭터 이름: " + chr.getName() + " ) : "
                     + var16.toString());
               var16.printStackTrace();
            }

            try {
               int holyWaterCount = 0;

               for (AffectedArea area : new ArrayList<>(this.getAllMistsThreadsafe())) {
                  if (area != null && area.getOwner() != null && area.getOwner().getId() == chr.getId()) {
                     if (area.getSourceSkillID() == 2321015 && area.getEndTime() > System.currentTimeMillis()) {
                        holyWaterCount++;
                     }

                     this.broadcastMessage(
                           CField.removeAffectedArea(area.getObjectId(), area.getSourceSkillID(), false));
                     this.removeMapObject(area);
                  }
               }

               if (holyWaterCount != 0) {
                  Integer holyWaterValue = chr.getBuffedValueDefault(SecondaryStatFlag.HolyWater, 0);
                  chr.temporaryStatSet(SecondaryStatFlag.HolyWater, 0, Integer.MAX_VALUE,
                        holyWaterValue + holyWaterCount / 2);
               }
            } catch (Exception var15) {
               System.out.println("[오류] 필드 removePlayer 함수 실행중 오류 발생 Point 4 (캐릭터 이름: " + chr.getName() + " ) : "
                     + var15.toString());
               var15.printStackTrace();
            }

            try {
               chr.checkFollow();
               chr.removeExtractor();
               this.removeNpcByLocalUserID(chr.getId());
               this.broadcastMessage(CField.removePlayerFromMap(chr.getId()));
            } catch (Exception var9) {
               System.out.println(
                     "[오류] 필드 removePlayer 함수 실행중 오류 발생 Point 5 (캐릭터 이름: " + chr.getName() + " ) : " + var9.toString());
               var9.printStackTrace();
            }

            try {
               if (!GameConstants.isIllium(chr.getJob()) && chr.getBuffedValue(SecondaryStatFlag.NewFlying) != null) {
                  SecondaryStat stat = chr.getSecondaryStat();
                  chr.setTempKeyValue("NewFlyingReason", stat.NewFlyingReason);
                  chr.setTempKeyValue("NewFlyingTime", stat.NewFlyingTill);
                  chr.setTempKeyValue("NewFlyingValue", stat.NewFlyingValue);
                  chr.temporaryStatReset(SecondaryStatFlag.NewFlying);
               }
            } catch (Exception var8) {
               System.out.println(
                     "[오류] 필드 removePlayer 함수 실행중 오류 발생 Point 6 (캐릭터 이름: " + chr.getName() + " ) : " + var8.toString());
               var8.printStackTrace();
            }

            try {
               this.resetIncMobGen(chr);
               if (this.hasteBoosterTarget == chr) {
                  this.hasteBoosterTarget = null;
                  this.useHasteBoosterTime = 0L;
                  this.killAllMonster(9833710);
               }
            } catch (Exception var7) {
               System.out.println(
                     "[오류] 필드 removePlayer 함수 실행중 오류 발생 Point 7 (캐릭터 이름: " + chr.getName() + " ) : " + var7.toString());
               var7.printStackTrace();
            }

            try {
               List<Summoned> ss = chr.getSummons();

               for (Summoned summon : new ArrayList<>(ss)) {
                  if (summon != null) {
                     if (summon.getSkill() >= 33001007 && summon.getSkill() <= 33001015
                           || summon.getMoveAbility() != SummonMoveAbility.STATIONARY
                                 && summon.getMoveAbility() != SummonMoveAbility.CIRCLE_STATIONARY
                                 && summon.getMoveAbility() != SummonMoveAbility.WALK_STATIONARY) {
                        this.broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
                        this.removeMapObject(summon);
                        summon.setChangedMap(true);
                     } else {
                        chr.dispelSkill(summon.getSkill());
                     }
                  }
               }
            } catch (Exception var14) {
               System.out.println("[오류] 필드 removePlayer 함수 실행중 오류 발생 Point 7 (캐릭터 이름: " + chr.getName() + " ) : "
                     + var14.toString());
               var14.printStackTrace();
            }

            try {
               AchievementFactory.resetFieldLeave(chr);
               AchievementFactory.checkFieldLeave(chr);
            } catch (NullPointerException var6) {
               var6.printStackTrace();
            }

            try {
               if (chr.getChair() / 1000 == 3016) {
                  chr.setChairEmotion(0);
                  if (chr.getCustomChair() != null && chr.getCustomChair().getOwner().getId() == chr.getId()) {
                     CustomChair chair = chr.getMap().getCustomChairByOwner(chr.getId());
                     chair.getPlayers().forEach(p -> {
                        p.setChair(0);
                        p.getMap().broadcastMessage(CField.cancelChair(-1, p));
                        p.getMap().broadcastMessage(p, CField.showChair(p, 0, "", 0L), false);
                        p.setCustomChair(null);
                     });
                     if (chair != null) {
                        chr.getMap().removeCustomChair(chr, chair);
                     }
                  } else {
                     CustomChair chair = chr.getCustomChair();
                     chair.removePlayer(chr);
                     chr.getMap().broadcastMessage(CField.customChairResult(chr, false, true, false, chair));
                  }
               }
            } catch (Exception var13) {
               System.out.println("[오류] 필드 removePlayer 함수 실행중 오류 발생 Point 8 (캐릭터 이름: " + chr.getName() + " ) : "
                     + var13.toString());
               var13.printStackTrace();
            }

            try {
               if (this.getCharactersSize() == 0) {
                  this.cancelBreakTimeFieldTask();

                  for (DynamicObject obj : this.dynamicObjects) {
                     obj.reset();
                  }
               }
            } catch (Exception var12) {
               System.out.println("[오류] 필드 removePlayer 함수 실행중 오류 발생 Point 9 (캐릭터 이름: " + chr.getName() + " ) : "
                     + var12.toString());
               var12.printStackTrace();
            }

            try {
               if (!chr.isClone()) {
                  this.checkStates(chr.getName());
                  if (this.mapid == 109020001) {
                     chr.canTalk(true);
                  }

                  if (chr.getFrozenLinkSerialNumber() > 0L) {
                     this.killAllMonstersFL(false, chr.getFrozenLinkSerialNumber());
                     chr.cancelFrozenLinkTask();
                  }

                  chr.leaveMap(this);
               }
            } catch (Exception var5) {
               System.out.println("[오류] 필드 removePlayer 함수 실행중 오류 발생 Point 10 (캐릭터 이름: " + chr.getName() + " ) : "
                     + var5.toString());
               var5.printStackTrace();
            }
         }
      }
   }

   public void broadcastMessageFL(byte[] packet, long serialNumber) {
      for (MapleCharacter chr : this.characters) {
         if (chr != null && chr.getFrozenLinkSerialNumber() == serialNumber) {
            chr.getClient().getSession().writeAndFlush(packet);
         }
      }
   }

   public final void broadcastMessage(byte[] packet) {
      this.broadcastMessage(null, packet, Double.POSITIVE_INFINITY, null);
   }

   public final void broadcastMessage(MapleCharacter source, byte[] packet, boolean repeatToSource) {
      this.broadcastMessage(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getTruePosition());
   }

   public final void broadcastMessage(byte[] packet, Point rangedFrom) {
      this.broadcastMessage(null, packet, GameConstants.maxViewRangeSq(), rangedFrom);
   }

   public final void broadcastMessage(MapleCharacter source, byte[] packet, Point rangedFrom) {
      this.broadcastMessage(source, packet, GameConstants.maxViewRangeSq(), rangedFrom);
   }

   public void broadcastMessage(MapleCharacter source, byte[] packet, double rangeSq, Point rangedFrom) {
      for (MapleCharacter chr : this.characters) {
         if (chr != null && chr != source) {
            if (rangeSq < Double.POSITIVE_INFINITY) {
               if (rangedFrom.distanceSq(chr.getTruePosition()) <= rangeSq) {
                  chr.getClient().getSession().writeAndFlush(packet);
               }
            } else {
               chr.getClient().getSession().writeAndFlush(packet);
            }
         }
      }
   }

   private void sendObjectPlacement(MapleCharacter c) {
      if (c != null) {
         if (!c.isClone()) {
            try {
               List<MapleMapObject> objects = this.getMapObjectsInRange(c.getTruePosition(), c.getRange(),
                     GameConstants.rangedMapobjectTypes);
               List<MapleMapObject> needRemoveObjects = new ArrayList<>();
               Iterator var4 = objects.iterator();

               while (true) {
                  MapleMapObject o;
                  while (true) {
                     while (true) {
                        if (!var4.hasNext()) {
                           if (!needRemoveObjects.isEmpty()) {
                              needRemoveObjects.stream().forEach(obj -> {
                                 try {
                                    this.removeMapObject(obj);
                                 } catch (Exception var3x) {
                                 }
                              });
                           }

                           return;
                        }

                        o = (MapleMapObject) var4.next();

                        try {
                           if (o.getType() != MapleMapObjectType.REACTOR || ((Reactor) o).isAlive()) {
                              break;
                           }
                        } catch (Exception var14) {
                           FileoutputUtil.log("Log_Player_Except.rtf", "sendObjectPlacement 오류 발생 리엑터");
                           break;
                        }
                     }

                     try {
                        if (o.getType() != MapleMapObjectType.MONSTER
                              || ((MapleMonster) o).getFrozenLinkSerialNumber() <= 0L) {
                           break;
                        }
                     } catch (Exception var15) {
                        FileoutputUtil.log("Log_Player_Except.rtf", "sendObjectPlacement 오류 발생 몬스터");
                        break;
                     }
                  }

                  if (o.getType() == MapleMapObjectType.WRECKAGE) {
                     Wreckage wr = (Wreckage) o;
                     int ownerID = wr.getOwnerID();
                     long endTime = wr.getEndTime();
                     long curTime = System.currentTimeMillis();
                     if (this.getCharacterById(ownerID) == null || endTime <= curTime) {
                        needRemoveObjects.add(c);
                        continue;
                     }
                  }

                  try {
                     o.sendSpawnData(c.getClient());
                  } catch (Exception var13) {
                     FileoutputUtil.log("Log_Player_Except.rtf",
                           "sendObjectPlacement 오류 발생 sendSpawnData OBJType : " + o.getType().toString());
                  }

                  try {
                     c.addVisibleMapObject(o);
                  } catch (Exception var12) {
                     FileoutputUtil.log("Log_Player_Except.rtf", "sendObjectPlacement 오류 발생 addVisibleMapObject");
                  }
               }
            } catch (Exception var16) {
               FileoutputUtil.log("Log_Player_Except.rtf", "sendObjectPlacement 오류 발생");
            }
         }
      }
   }

   public final List<Portal> getPortalsInRange(Point from, double rangeSq) {
      List<Portal> ret = new ArrayList<>();

      for (Portal type : this.portals.values()) {
         if (from.distanceSq(type.getPosition()) <= rangeSq && type.getTargetMapId() != this.mapid
               && type.getTargetMapId() != 999999999) {
            ret.add(type);
         }
      }

      return ret;
   }

   public final List<MapleMapObject> getMapObjectsInRange(Point from, double rangeSq) {
      List<MapleMapObject> ret = new ArrayList<>();

      for (MapleMapObjectType type : MapleMapObjectType.values()) {
         this.mapobjectlocks.get(type).readLock().lock();

         try {
            for (MapleMapObject mmo : this.mapobjects.get(type).values()) {
               if (from.distanceSq(mmo.getTruePosition()) <= rangeSq) {
                  ret.add(mmo);
               }
            }
         } finally {
            this.mapobjectlocks.get(type).readLock().unlock();
         }
      }

      return ret;
   }

   public List<MapleMonster> getMobsInRange(Point from, double rangeSq, int size, boolean isOrderByMaxHP) {
      List<MapleMonster> ret = new ArrayList<>();
      List<MapleMonster> finalList = new ArrayList<>();

      for (MapleMapObject obj : this.getMapObjectsInRange(from, rangeSq, List.of(MapleMapObjectType.MONSTER))) {
         if (obj.getType() == MapleMapObjectType.MONSTER) {
            ret.add((MapleMonster) obj);
         }
      }

      if (ret.isEmpty()) {
         return ret;
      } else {
         if (isOrderByMaxHP) {
            ret.sort(Comparator.comparingLong(MapleMonster::getMobMaxHp).reversed());
         }

         while (size > ret.size()) {
            finalList.addAll(ret);
            size -= ret.size();
         }

         if (isOrderByMaxHP) {
            finalList.addAll(ret.subList(0, size));
         } else {
            int begin = Randomizer.nextInt(ret.size() - size + 1);
            finalList.addAll(ret.subList(begin, begin + size));
         }

         if (finalList.size() != size) {
         }

         return finalList;
      }
   }

   public final List<MapleCharacter> getCharactersInRange(Point from, double rangeSq) {
      List<MapleCharacter> ret = new ArrayList<>();

      for (MapleCharacter m : this.getCharactersThreadsafe()) {
         if (from.distanceSq(m.getTruePosition()) <= rangeSq) {
            ret.add(m);
         }
      }

      return ret;
   }

   public final List<MapleMapObject> getMapObjectsInRange(Point from, double rangeSq,
         List<MapleMapObjectType> MapObject_types) {
      List<MapleMapObject> ret = new ArrayList<>();

      for (MapleMapObjectType type : MapObject_types) {
         this.mapobjectlocks.get(type).readLock().lock();

         try {
            for (MapleMapObject mmo : this.mapobjects.get(type).values()) {
               if (from.distanceSq(mmo.getTruePosition()) <= rangeSq) {
                  ret.add(mmo);
               }
            }
         } finally {
            this.mapobjectlocks.get(type).readLock().unlock();
         }
      }

      return ret;
   }

   public final List<MapleMapObject> getMapObjectsInRect(Rectangle box, List<MapleMapObjectType> MapObject_types) {
      List<MapleMapObject> ret = new ArrayList<>();

      for (MapleMapObjectType type : MapObject_types) {
         this.mapobjectlocks.get(type).readLock().lock();

         try {
            for (MapleMapObject mmo : this.mapobjects.get(type).values()) {
               if (box.contains(mmo.getTruePosition())) {
                  ret.add(mmo);
               }
            }
         } finally {
            this.mapobjectlocks.get(type).readLock().unlock();
         }
      }

      return ret;
   }

   public final List<MapleCharacter> getCharactersIntersect(Rectangle box) {
      List<MapleCharacter> ret = new ArrayList<>();

      for (MapleCharacter chr : this.characters) {
         if (chr.getBounds().intersects(box)) {
            ret.add(chr);
         }
      }

      return ret;
   }

   public final List<MapleCharacter> getPlayersInRectAndInList(Rectangle box, List<MapleCharacter> chrList) {
      List<MapleCharacter> character = new LinkedList<>();

      for (MapleCharacter a : this.characters) {
         if (chrList.contains(a) && box.contains(a.getTruePosition())) {
            character.add(a);
         }
      }

      return character;
   }

   public final void addPortal(Portal myPortal) {
      this.portals.put(myPortal.getId(), myPortal);
   }

   public final Portal getPortal(String portalname) {
      for (Portal port : this.portals.values()) {
         if (port.getName().equals(portalname)) {
            return port;
         }
      }

      return null;
   }

   public final Portal getPortal(int portalid) {
      return this.portals.get(portalid);
   }

   public final List<Portal> getPortalSP() {
      List<Portal> res = new LinkedList<>();

      for (Portal port : this.portals.values()) {
         if (port.getName().equals("sp")) {
            res.add(port);
         }
      }

      return res;
   }

   public final void resetPortals() {
      for (Portal port : this.portals.values()) {
         port.setPortalState(true);
      }
   }

   public final void setFootholds(MapleFootholdTree footholds) {
      this.footholds = footholds;
   }

   public final MapleFootholdTree getFootholds() {
      return this.footholds;
   }

   public final int getNumSpawnPoints() {
      return this.monsterSpawn.size();
   }

   public Rect calculateMBR() {
      if (this.mBR != null) {
         return this.mBR;
      } else {
         int MBRLeft = Integer.MAX_VALUE;
         int MBRRight = Integer.MIN_VALUE;
         int MBRTop = Integer.MAX_VALUE;
         int MBRBottom = Integer.MIN_VALUE;
         int VRLeft = this.getLeft();
         int VRRight = this.getRight();
         int VRTop = this.getTop();
         int VRBottom = this.getBottom();
         boolean VRLimit = this.isVrlimit();
         if (!this.footholds.getFootholds().isEmpty()) {
            for (MapleFoothold foothold : this.footholds.getFootholds()) {
               if (foothold != null) {
                  MBRLeft = Math.min(MBRLeft, foothold.getX1() + 30);
                  MBRRight = Math.max(MBRRight, foothold.getX2() - 30);
                  MBRTop = Math.min(MBRTop, foothold.getY1() - 300);
                  MBRBottom = Math.max(MBRBottom, foothold.getY2() + 10);
               }
            }
         }

         if (VRLimit) {
            if (VRLeft != 0 && MBRLeft < VRLeft + 20) {
               MBRLeft = VRLeft + 20;
            }

            if (VRRight != 0 && MBRRight > VRRight - 20) {
               MBRRight = VRRight - 20;
            }

            if (VRTop != 0 && MBRTop < VRTop + 65) {
               MBRTop = VRTop + 65;
            }

            if (VRBottom != 0 && MBRBottom > VRBottom) {
               MBRBottom = VRBottom;
            }
         }

         this.mBR = new Rect(MBRLeft, MBRTop, MBRRight, MBRBottom);
         return this.mBR;
      }
   }

   public final void setMaxRegularSpawn(int maxRegularSpawn) {
      this.maxRegularSpawn = maxRegularSpawn;
   }

   public final void loadMonsterRate(boolean first) {
      int spawnSize = this.monsterSpawn.size();
      Rect mbr = this.calculateMBR();
      int x = mbr.getRight() - mbr.getLeft();
      if (x < 800) {
         x = 800;
      }

      int y = mbr.getBottom() - mbr.getTop() - 450;
      if (y < 600) {
         y = 600;
      }

      double minMob = (double) this.monsterRate * x * y * 7.8125E-6;
      if (minMob < 1.0) {
         minMob = 1.0;
      }

      int min = (int) minMob;
      if (min > 40) {
         min = 40;
      }

      this.mobCapacityMin = min;
      this.mobCapacityMax = min * 2;
      if (this.mobCapacityMin > this.mobCapacityMax) {
         this.mobCapacityMin = this.mobCapacityMax;
      }

      int ctrlHeapSize = this.getCharactersSize();
      if (ctrlHeapSize <= this.mobCapacityMin / 2) {
         this.maxRegularSpawn = this.mobCapacityMin;
      } else {
         this.maxRegularSpawn = ctrlHeapSize >= 2 * this.mobCapacityMin
               ? this.mobCapacityMax
               : this.mobCapacityMin + (this.mobCapacityMax - this.mobCapacityMin)
                     * (2 * ctrlHeapSize - this.mobCapacityMin) / (3 * this.mobCapacityMin);
      }

      if (this.maxRegularSpawn > spawnSize) {
         this.maxRegularSpawn = spawnSize;
      }

      Collection<Spawns> newSpawn = new LinkedList<>();
      Collection<Spawns> newBossSpawn = new LinkedList<>();

      for (Spawns s : this.monsterSpawn) {
         if (s.getCarnivalTeam() < 2) {
            if (s.getMonster().getStats().isBoss()) {
               newBossSpawn.add(s);
            } else {
               newSpawn.add(s);
            }
         }
      }

      this.monsterSpawn.clear();
      this.monsterSpawn.addAll(newBossSpawn);
      this.monsterSpawn.addAll(newSpawn);
      if (first && spawnSize > 0) {
         this.lastSpawnTime = System.currentTimeMillis();
         if (GameConstants.isForceRespawn(this.mapid)) {
            this.createMobInterval = 15000;
         }

         this.respawn(false);
      }
   }

   public final SpawnPoint addMonsterSpawn(MapleMonster monster, int mobTime, byte carnivalTeam, String msg) {
      Point newpos = this.calcPointBelow(monster.getPosition());
      newpos.y--;
      SpawnPoint sp = new SpawnPoint(monster, newpos, mobTime, carnivalTeam, msg);
      if (carnivalTeam > -1) {
         this.monsterSpawn.add(0, sp);
      } else {
         this.monsterSpawn.add(sp);
      }

      return sp;
   }

   public List<Spawns> getMonsterSpawn() {
      return this.monsterSpawn;
   }

   public final void resetMonsterSpawn() {
      this.monsterSpawn.clear();
   }

   public final void loadDynamicObjects(MapleData data) {
      for (int i = 0; i <= 7; i++) {
         MapleData objs = data.getChildByPath(i + "/obj");
         if (objs != null) {
            for (MapleData obj : objs) {
               int dynamic = MapleDataTool.getInt("dynamic", obj, 0);
               if (dynamic != 0) {
                  String tags = MapleDataTool.getString("tags", obj, "");
                  String name = MapleDataTool.getString("name", obj, "");
                  int x = MapleDataTool.getInt("x", obj, 0);
                  int y = MapleDataTool.getInt("y", obj, 0);
                  DynamicObject object = new DynamicObject(tags, name, x, y, this.dynamicObjects.size());
                  int snCount = MapleDataTool.getInt("SN_count", obj, 0);

                  for (int a = 0; a < snCount; a++) {
                     int sn = MapleDataTool.getInt("SN" + a, obj, 0);
                     object.getFootholdSN().add(sn);
                  }

                  this.dynamicObjects.add(object);
               }
            }
         }
      }

      this.dynamicObjects.sort(Comparator.comparing(DynamicObject::getName));
   }

   public void setDynamicObjectWaiting(int id, int duration, int posX, int posY,
         List<DynamicObject.CollisionDisease> collisionDiseases) {
      if (this.dynamicObjects.size() > id && id >= 0) {
         DynamicObject obj = null;

         for (DynamicObject ob : this.dynamicObjects) {
            if (ob.getIndex() == id) {
               obj = ob;
               break;
            }
         }

         if (obj != null) {
            obj.setWaiting(true);
            obj.setWaitingDuration(duration);
            obj.setPosX(posX);
            obj.setPosY(posY);
            obj.getUserCollisionDisease().addAll(collisionDiseases);
         }
      }
   }

   public List<DynamicObject> pickDynamicObjectRandomly(int count) {
      List<DynamicObject> ret = new ArrayList<>();

      for (int i = 0; i < count; i++) {
         DynamicObject dynamicObject = this.dynamicObjects.get(Randomizer.rand(0, this.dynamicObjects.size() - 1));
         if (!ret.stream().anyMatch(a -> Math.abs(a.getPosX() - dynamicObject.getPosX()) < 50)) {
            ret.add(dynamicObject);
         }
      }

      return ret;
   }

   public final void addAreaMonsterSpawn(MapleMonster monster, Point pos1, Point pos2, Point pos3, int mobTime,
         String msg, boolean shouldSpawn) {
      pos1 = this.calcPointBelow(pos1);
      pos2 = this.calcPointBelow(pos2);
      pos3 = this.calcPointBelow(pos3);
      if (pos1 != null) {
         pos1.y--;
      }

      if (pos2 != null) {
         pos2.y--;
      }

      if (pos3 != null) {
         pos3.y--;
      }

      if (pos1 == null && pos2 == null && pos3 == null) {
         System.out.println("WARNING: mapid " + this.mapid + ", monster " + monster.getId() + " could not be spawned.");
      } else {
         if (pos1 != null) {
            if (pos2 == null) {
               pos2 = new Point(pos1);
            }

            if (pos3 == null) {
               pos3 = new Point(pos1);
            }
         } else if (pos2 != null) {
            if (pos1 == null) {
               pos1 = new Point(pos2);
            }

            if (pos3 == null) {
               pos3 = new Point(pos2);
            }
         } else if (pos3 != null) {
            if (pos1 == null) {
               pos1 = new Point(pos3);
            }

            if (pos2 == null) {
               pos2 = new Point(pos3);
            }
         }

         this.monsterSpawn.add(new SpawnPointAreaBoss(monster, pos1, pos2, pos3, mobTime, msg, shouldSpawn));
      }
   }

   public final List<MapleCharacter> getCharacters() {
      return this.getCharactersThreadsafe();
   }

   public final List<MapleCharacter> getCharactersThreadsafe() {
      List<MapleCharacter> chars = new ArrayList<>();

      for (MapleCharacter mc : this.characters) {
         if (mc != null && (mc.getClient().getSession().isOpen() || mc.getClient().getSession().isActive())) {
            chars.add(mc);
         }
      }

      return chars;
   }

   public final MapleCharacter getCharacterByName(String id) {
      for (MapleCharacter mc : this.characters) {
         if (mc != null && mc.getName().equalsIgnoreCase(id)) {
            return mc;
         }
      }

      return null;
   }

   public final MapleCharacter getCharacterById(int id) {
      for (MapleCharacter mc : this.characters) {
         if (mc != null && mc.getId() == id) {
            return mc;
         }
      }

      return null;
   }

   public final void updateMapObjectVisibility(MapleCharacter chr, MapleMapObject mo) {
      if (chr != null && !chr.isClone()) {
         if (!chr.isMapObjectVisible(mo)) {
            if (mo.getType() == MapleMapObjectType.MIST
                  || mo.getType() == MapleMapObjectType.EXTRACTOR
                  || mo.getType() == MapleMapObjectType.SUMMON
                  || mo.getType() == MapleMapObjectType.RUNE_STONE
                  || mo instanceof OpenGate
                  || mo.getTruePosition().distanceSq(chr.getTruePosition()) <= mo.getRange()) {
               chr.addVisibleMapObject(mo);
               mo.sendSpawnData(chr.getClient());
            }
         } else if (!(mo instanceof OpenGate)
               && mo.getType() != MapleMapObjectType.MIST
               && mo.getType() != MapleMapObjectType.EXTRACTOR
               && mo.getType() != MapleMapObjectType.SUMMON
               && mo.getType() != MapleMapObjectType.RUNE_STONE
               && mo.getTruePosition().distanceSq(chr.getTruePosition()) > mo.getRange()) {
            chr.removeVisibleMapObject(mo);
            mo.sendDestroyData(chr.getClient());
         } else if (mo.getType() == MapleMapObjectType.MONSTER
               && chr.getPosition().distanceSq(mo.getPosition()) <= GameConstants.maxViewRangeSq()) {
            this.updateMonsterController((MapleMonster) mo);
         }
      }
   }

   public void moveMonster(MapleMonster monster, Point reportedPos) {
      monster.setPosition(reportedPos);

      for (MapleCharacter mc : this.characters) {
         if (mc != null) {
            this.updateMapObjectVisibility(mc, monster);
         }
      }
   }

   public void movePlayer(MapleCharacter player, Point newPosition) {
      player.setPosition(newPosition);
      if (!player.isClone()) {
         try {
            Collection<MapleMapObject> visibleObjects = player.getAndWriteLockVisibleMapObjects();

            for (MapleMapObject mo : new ArrayList<>(visibleObjects)) {
               if (mo != null && this.getMapObject(mo.getObjectId(), mo.getType()) == mo) {
                  this.updateMapObjectVisibility(player, mo);
               } else if (mo != null) {
                  visibleObjects.remove(mo);
               }
            }

            for (MapleMapObject mox : this.getMapObjectsInRange(player.getTruePosition(), player.getRange())) {
               if ((mox.getType() != MapleMapObjectType.MONSTER
                     || ((MapleMonster) mox).getFrozenLinkSerialNumber() <= 0L)
                     && mox != null
                     && !visibleObjects.contains(mox)
                     && (mox.getType() != MapleMapObjectType.PLAYER
                           || ((MapleCharacter) mox).getId() != player.getId())) {
                  mox.sendSpawnData(player.getClient());
                  visibleObjects.add(mox);
               }
            }
         } finally {
            player.unlockWriteVisibleMapObjects();
         }
      }
   }

   public Portal findClosestSpawnpoint(Point from) {
      Portal closest = this.getPortal(0);
      double shortestDistance = Double.POSITIVE_INFINITY;

      for (Portal portal : this.portals.values()) {
         double distance = portal.getPosition().distanceSq(from);
         if (portal.getType() >= 0 && portal.getType() <= 2 && distance < shortestDistance
               && portal.getTargetMapId() == 999999999) {
            closest = portal;
            shortestDistance = distance;
         }
      }

      return closest;
   }

   public Portal findClosestPortal(Point from) {
      Portal closest = this.getPortal(0);
      double shortestDistance = Double.POSITIVE_INFINITY;

      for (Portal portal : this.portals.values()) {
         double distance = portal.getPosition().distanceSq(from);
         if (distance < shortestDistance) {
            closest = portal;
            shortestDistance = distance;
         }
      }

      return closest;
   }

   public String spawnDebug() {
      StringBuilder sb = new StringBuilder("Mobs in map : ");
      sb.append(this.getMobsSize());
      sb.append(" spawnedMonstersOnMap: ");
      sb.append(this.spawnedMonstersOnMap);
      sb.append(" spawnpoints: ");
      sb.append(this.monsterSpawn.size());
      sb.append(" maxRegularSpawn: ");
      sb.append(this.maxRegularSpawn);
      sb.append(" actual monsters: ");
      sb.append(this.getNumMonsters());
      sb.append(" monster rate: ");
      sb.append(this.monsterRate);
      sb.append(" fixed: ");
      sb.append(this.fixedMob);
      return sb.toString();
   }

   public int characterSize() {
      return this.characters.size();
   }

   public final int getMapObjectSize() {
      return this.mapobjects.size() + this.getCharactersSize() - this.characters.size();
   }

   public final int getCharactersSize() {
      int ret = 0;

      for (MapleCharacter chr : this.characters) {
         if (chr != null && !chr.isClone() && chr.getNotMovingCount() < 5
               && (chr.getClient().getSession().isOpen() || chr.getClient().getSession().isActive())) {
            ret++;
         }
      }

      return ret;
   }

   public Collection<Portal> getPortals() {
      return Collections.unmodifiableCollection(this.portals.values());
   }

   public int getSpawnedMonstersOnMap() {
      return this.spawnedMonstersOnMap.get();
   }

   public int getStormWingCount() {
      return this.stormWingCount;
   }

   public void setStormWingCount(int stormWingCount) {
      this.stormWingCount = stormWingCount;
   }

   public boolean isActiveDebuffObjs() {
      return this.activeDebuffObjs;
   }

   public void setActiveDebuffObjs(boolean activeDebuffObjs) {
      this.activeDebuffObjs = activeDebuffObjs;
   }

   public Map<Integer, DebuffObj> getDebuffObjs() {
      return new HashMap<>(this.debuffObjs);
   }

   public void registerDebuffObj(int key, int dataType, String tag, String effName, int activeRate) {
      this.debuffObjs.put(key, new DebuffObj(key, dataType, tag, effName, activeRate));
   }

   public int getDebuffObjInterval() {
      return this.debuffObjInterval;
   }

   public void setDebuffObjInterval(int debuffObjInterval) {
      this.debuffObjInterval = debuffObjInterval;
   }

   public void onDebuffObjCollision(MapleCharacter player, int key, int dataType) {
      DebuffObj debuffObj = null;
      if ((debuffObj = this.debuffObjs.get(key)) != null) {
         String effectName = debuffObj.getEffectName();
         if (effectName != null
               && effectName.equals("sleepGas")
               && player.getBuffedValue(SecondaryStatFlag.NotDamaged) == null
               && player.getBuffedValue(SecondaryStatFlag.indiePartialNotDamaged) == null) {
            MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(MobSkillID.GIVE_ME_HEAL.getVal(), 1);
            player.giveDebuff(SecondaryStatFlag.GiveMeHeal, mobSkillInfo.getX(), 0, mobSkillInfo.getDuration(),
                  MobSkillID.GIVE_ME_HEAL.getVal(), 1);
         }
      }
   }

   public boolean isCheckLinkMobRevive() {
      return this.checkLinkMobRevive;
   }

   public void setCheckLinkMobRevive(boolean checkLinkMobRevive) {
      this.checkLinkMobRevive = checkLinkMobRevive;
   }

   public int getFieldCatcherIndex() {
      return this.fieldCatcherIndex;
   }

   public void setFieldCatcherIndex(int fieldCatcherIndex) {
      this.fieldCatcherIndex = fieldCatcherIndex;
   }

   public boolean isSpawnSpiderWeb() {
      return this.spawnSpiderWeb;
   }

   public void setSpawnSpiderWeb(boolean spawnSpiderWeb) {
      this.spawnSpiderWeb = spawnSpiderWeb;
   }

   public List<Spawns> getClosestSpawns(Point pt, int count) {
      List<Spawns> spawn = new ArrayList<>(this.monsterSpawn);
      return spawn.stream().sorted((a, b) -> (int) (a.getPosition().distance(pt) - b.getPosition().distance(pt)))
            .limit(count).collect(Collectors.toList());
   }

   public void respawn(boolean force) {
      this.respawn(force, System.currentTimeMillis());
   }

   public void respawn(boolean force, long now) {
      if (this.getId() != 921174100 && this.getId() != 262030300 && this.getId() != 910010000) {
         if (this.getId() != 993033200) {
            if (force || this.isMobGen()) {
               this.lastSpawnTime = now;
               if (force) {
                  int numShouldSpawn = this.monsterSpawn.size() - this.spawnedMonstersOnMap.get();
                  if (numShouldSpawn > 0) {
                     int spawned = 0;

                     for (Spawns spawnPoint : this.monsterSpawn) {
                        spawnPoint.spawnMonster(this);
                        if (++spawned >= numShouldSpawn) {
                           break;
                        }
                     }
                  }
               } else {
                  int debugValue = 0;

                  try {
                     if (this.spawnedMonstersOnMap.get() < 0) {
                        this.spawnedMonstersOnMap.set(0);
                     }

                     int def = this.maxRegularSpawn;
                     debugValue = 1;
                     if (GameConstants.FatigueMapList.contains(this.getId())) {
                        def *= 2;
                     }

                     if (!(this instanceof Field_Papulatus) && this.getId() != 310070400 && this.getId() != 230040420) {
                        if (DBConfig.isGanglim) {
                           def *= 2;
                        }

                        if (this.incMobGen > 0) {
                           def = (int) (def + def * 0.01 * this.incMobGen);
                        }

                        if (ServerConstants.dailyEventType == DailyEventType.MobGenFever && !DBConfig.isGanglim) {
                           def = (int) (def + this.maxRegularSpawn * 0.01 * 20.0);
                        }

                        if (DBConfig.isGanglim) {
                           def = (int) (def + this.maxRegularSpawn * 0.01 * 70.0);
                        }
                     }

                     debugValue = 2;
                     int spawnMobSize = this.spawnedMonstersOnMap.get();
                     int numShouldSpawn = (GameConstants.isForceRespawn(this.mapid) ? this.monsterSpawn.size() : def)
                           - spawnMobSize;
                     debugValue = 3;
                     int spawned = 0;
                     int tryCount = 0;

                     while (numShouldSpawn > spawned && tryCount++ <= 300) {
                        debugValue = 4;
                        List<Spawns> randomSpawn = new ArrayList<>(this.monsterSpawn);
                        Collections.shuffle(randomSpawn);
                        Spawns spawnPointx = randomSpawn.stream().findAny().orElse(null);
                        if (spawnPointx != null) {
                           debugValue = 5;
                           if ((this.isSpawns || spawnPointx.getMobTime() <= 0)
                                 && (tryCount >= 100 || this
                                       .getMobsInRect(spawnPointx.getPosition(), -150, -100, 150, 100).size() <= 0)) {
                              int cooltime = 5000;
                              if (DBConfig.isGanglim) {
                                 cooltime = 1000;
                              }

                              if (DBConfig.isGanglim && this.reduceMobGenTime > 0) {
                                 cooltime -= this.reduceMobGenTime;
                              }

                              cooltime = Math.max(0, cooltime);
                              if (this.getLastRespawnTime() == 0L
                                    || System.currentTimeMillis() - this.getLastRespawnTime() >= cooltime
                                    || GameConstants.isForceRespawn(this.mapid)) {
                                 debugValue = 6;
                                 if (tryCount > 200 || spawnPointx.shouldSpawn(now, 1)) {
                                    boolean check = false;

                                    for (int mid : this.mobGenExcept) {
                                       if (mid == spawnPointx.getMonster().getId()) {
                                          check = true;
                                       }
                                    }

                                    if (!check) {
                                       debugValue = 7;
                                       MapleMonster life = spawnPointx.spawnMonster(this);
                                       spawned++;
                                       if (this.hasteBoosterTarget != null) {
                                          debugValue = 8;
                                          MapleMonster booster = MapleLifeFactory.getMonster(9833710);
                                          booster.setOverrideStats(new OverrideMonsterStats(life.getMobMaxHp(),
                                                life.getMobMaxMp(), life.getMobExp()));
                                          this.spawnMonsterOnFrozenLink(booster, spawnPointx.getPosition(),
                                                this.hasteBoosterTarget.getFrozenLinkSerialNumber());
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }

                     if (spawned != 0) {
                        this.setLastRespawnTime(System.currentTimeMillis());
                     }
                  } catch (Exception var16) {
                     System.out.println("[오류] 몬스터 리젠중 오류가 발생함 : " + var16.toString() + " FieldId : " + this.getId()
                           + " DebugStep : " + debugValue);
                     var16.printStackTrace();
                  }
               }
            }
         }
      }
   }

   public String getSnowballPortal() {
      int[] teamss = new int[2];

      for (MapleCharacter chr : this.characters) {
         if (chr.getTruePosition().y > -80) {
            teamss[0]++;
         } else {
            teamss[1]++;
         }
      }

      return teamss[0] > teamss[1] ? "st01" : "st00";
   }

   public boolean isDisconnected(int id) {
      return this.dced.contains(id);
   }

   public void addDisconnected(int id) {
      this.dced.add(id);
   }

   public void resetDisconnected() {
      this.dced.clear();
   }

   public void startSpeedRun() {
      MapleSquad squad = this.getSquadByMap();
      if (squad != null) {
         for (MapleCharacter chr : this.characters) {
            if (chr.getName().equals(squad.getLeaderName()) && !chr.isIntern()) {
               this.startSpeedRun(chr.getName());
               return;
            }
         }
      }
   }

   public void startSpeedRun(String leader) {
      this.speedRunStart = System.currentTimeMillis();
      this.speedRunLeader = leader;
   }

   public void endSpeedRun() {
      this.speedRunStart = 0L;
      this.speedRunLeader = "";
   }

   public void getRankAndAdd(String leader, String time, ExpeditionType type, long timz, Collection<String> squad) {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         long lastTime = SpeedRunner.getSpeedRunData(type) == null ? 0L : SpeedRunner.getSpeedRunData(type).right;
         StringBuilder rett = new StringBuilder();
         if (squad != null) {
            for (String chr : squad) {
               rett.append(chr);
               rett.append(",");
            }
         }

         String z = rett.toString();
         if (squad != null) {
            z = z.substring(0, z.length() - 1);
         }

         PreparedStatement ps = con.prepareStatement(
               "INSERT INTO speedruns(`type`, `leader`, `timestring`, `time`, `members`) VALUES (?,?,?,?,?)");
         ps.setString(1, type.name());
         ps.setString(2, leader);
         ps.setString(3, time);
         ps.setLong(4, timz);
         ps.setString(5, z);
         ps.executeUpdate();
         ps.close();
         if (lastTime == 0L) {
            SpeedRunner.addSpeedRunData(
                  type, SpeedRunner.addSpeedRunData(new StringBuilder(SpeedRunner.getPreamble(type)), new HashMap<>(),
                        z, leader, 1, time),
                  timz);
         } else {
            SpeedRunner.removeSpeedRunData(type);
            SpeedRunner.loadSpeedRunData(type);
         }
      } catch (Exception var16) {
         System.out.println("SpeedRunner Err");
         var16.printStackTrace();
      }
   }

   public long getSpeedRunStart() {
      return this.speedRunStart;
   }

   public final void disconnectAll() {
      for (MapleCharacter chr : this.getCharactersThreadsafe()) {
         if (!chr.isGM()) {
            chr.getClient().disconnect(false);
            chr.getClient().getSession().close();
            System.out.println("팅겼다고인마");
         }
      }
   }

   public List<MapleNPC> getAllNPCs() {
      return this.getAllNPCsThreadsafe();
   }

   public List<MapleNPC> getAllNPCsThreadsafe() {
      ArrayList<MapleNPC> ret = new ArrayList<>();
      this.mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();

      try {
         for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.NPC).values()) {
            ret.add((MapleNPC) mmo);
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
      }

      return ret;
   }

   public final void resetNPCs() {
      this.removeNpc(-1);
   }

   public final void resetPQ(int level) {
      this.resetFully();

      for (MapleMonster mons : this.getAllMonstersThreadsafe()) {
         mons.changeLevel(level, true);
      }

      this.resetSpawnLevel(level);
   }

   public final void resetSpawnLevel(int level) {
      for (Spawns spawn : this.monsterSpawn) {
         if (spawn instanceof SpawnPoint) {
            ((SpawnPoint) spawn).setLevel(level);
         }
      }
   }

   public final void resetMist() {
      for (AffectedArea mist : new LinkedList<>(this.getAllMistsThreadsafe())) {
         this.removeMapObject(mist);
      }
   }

   public void resetFully() {
      this.resetFully(true);
   }

   public void resetFully(boolean respawn) {
      this.killAllMonsters(false);
      this.reloadReactors();
      this.removeDrops();
      this.resetNPCs();
      this.resetFieldAttackObj();
      this.resetSpawns();
      this.resetDisconnected();
      this.endSpeedRun();
      this.cancelSquadSchedule(true);
      this.resetPortals();
      this.resetMist();
      this.environment.clear();
      this.stormWingCount = 0;
      this.obstacleAtomCreators.clear();
      this.fieldCommandContext = null;
      this.currentFieldCommand = null;
      if (this.getFieldSetInstance() != null) {
         this.setFieldSetInstance(null);
      }

      if (respawn) {
         this.respawn(true);
      }
   }

   public final void cancelSquadSchedule(boolean interrupt) {
      this.squadTimer = false;
      this.checkStates = true;
      if (this.squadSchedule != null) {
         this.squadSchedule.cancel(interrupt);
         this.squadSchedule = null;
      }
   }

   public final void removeDrops() {
      for (Drop i : this.getAllItemsThreadsafe()) {
         i.expire(this);
      }
   }

   public final void removeDropsIndividual(MapleCharacter chr) {
      for (Drop i : this.getAllItemsThreadsafe()) {
         if (i.isIndividual() && i.getCharacter_ownerid() == chr.getId()) {
            i.expire(this);
         }
      }
   }

   public final void resetAllSpawnPoint(int mobid, int mobTime) {
      Collection<Spawns> sss = new LinkedList<>(this.monsterSpawn);
      this.resetFully();
      this.monsterSpawn.clear();

      for (Spawns s : sss) {
         MapleMonster newMons = MapleLifeFactory.getMonster(mobid);
         newMons.setF(s.getF());
         newMons.setFh(s.getFh());
         newMons.setPosition(s.getPosition());
         this.addMonsterSpawn(newMons, mobTime, (byte) -1, null);
      }

      this.loadMonsterRate(true);
   }

   public final void resetFieldAttackObj() {
      try {
         this.getAllFieldAttackObj().stream().forEach(obj -> this.removeMapObject(obj));
      } catch (Exception var2) {
      }
   }

   public final void resetSpawns() {
      boolean changed = false;
      Iterator<Spawns> sss = this.monsterSpawn.iterator();

      while (sss.hasNext()) {
         if (sss.next().getCarnivalId() > -1) {
            sss.remove();
            changed = true;
         }
      }

      this.setSpawns(true);
      if (changed) {
         this.loadMonsterRate(true);
      }
   }

   public final boolean makeCarnivalSpawn(int team, MapleMonster newMons, int num) {
      MapleNodes.MonsterPoint ret = null;

      for (MapleNodes.MonsterPoint mp : this.nodes.getMonsterPoints()) {
         if (mp.team == team || mp.team == -1) {
            Point newpos = this.calcPointBelow(new Point(mp.x, mp.y));
            newpos.y--;
            boolean found = false;

            for (Spawns s : this.monsterSpawn) {
               if (s.getCarnivalId() > -1
                     && (mp.team == -1 || s.getCarnivalTeam() == mp.team)
                     && s.getPosition().x == newpos.x
                     && s.getPosition().y == newpos.y) {
                  found = true;
                  break;
               }
            }

            if (!found) {
               ret = mp;
               break;
            }
         }
      }

      if (ret != null) {
         newMons.setCy(ret.cy);
         newMons.setF(0);
         newMons.setFh(ret.fh);
         newMons.setRx0(ret.x + 50);
         newMons.setRx1(ret.x - 50);
         newMons.setPosition(new Point(ret.x, ret.y));
         newMons.setHide(false);
         SpawnPoint sp = this.addMonsterSpawn(newMons, 1, (byte) team, null);
         sp.setCarnival(num);
      }

      return ret != null;
   }

   public final void blockAllPortal() {
      for (Portal p : this.portals.values()) {
         p.setPortalState(false);
      }
   }

   public boolean getAndSwitchTeam() {
      return this.getCharactersSize() % 2 != 0;
   }

   public void setSquad(MapleSquad.MapleSquadType s) {
      this.squad = s;
   }

   public int getChannel() {
      return this.channel;
   }

   public int getConsumeItemCoolTime() {
      return this.consumeItemCoolTime;
   }

   public void setConsumeItemCoolTime(int ciit) {
      this.consumeItemCoolTime = ciit;
   }

   public void setPermanentWeather(int pw) {
      this.permanentWeather = pw;
   }

   public int getPermanentWeather() {
      return this.permanentWeather;
   }

   public void checkStates(String chr) {
      if (this.checkStates) {
         MapleSquad sqd = this.getSquadByMap();
         EventManager em = this.getEMByMap();
         int size = this.getCharactersSize();
         if (sqd != null && sqd.getStatus() == 2) {
            sqd.removeMember(chr);
            if (em != null) {
               if (sqd.getLeaderName().equalsIgnoreCase(chr)) {
                  em.setProperty("leader", "false");
               }

               if (chr.equals("") || size == 0) {
                  em.setProperty("state", "0");
                  em.setProperty("leader", "true");
                  this.cancelSquadSchedule(!chr.equals(""));
                  sqd.clear();
                  sqd.copy();
               }
            }
         }

         if (em != null && em.getProperty("state") != null && (sqd == null || sqd.getStatus() == 2) && size == 0) {
            em.setProperty("state", "0");
            if (em.getProperty("leader") != null) {
               em.setProperty("leader", "true");
            }
         }

         if (this.speedRunStart > 0L && size == 0) {
            this.endSpeedRun();
         }
      }
   }

   public void setCheckStates(boolean b) {
      this.checkStates = b;
   }

   public void setNodes(MapleNodes mn) {
      this.nodes = mn;
   }

   public final List<MapleNodes.MaplePlatform> getPlatforms() {
      return this.nodes.getPlatforms();
   }

   public Collection<MapleNodes.MapleNodeInfo> getNodes() {
      return this.nodes.getNodes();
   }

   public MapleNodes.MapleNodeInfo getNode(int index) {
      return this.nodes.getNode(index);
   }

   public boolean isLastNode(int index) {
      return this.nodes.isLastNode(index);
   }

   public final List<Rectangle> getAreas() {
      return this.nodes.getAreas();
   }

   public final Rectangle getArea(int index) {
      return this.nodes.getArea(index);
   }

   public final void changeEnvironment(String ms, int type) {
      this.broadcastMessage(CField.environmentChange(ms, type));
   }

   public final void toggleEnvironment(String ms) {
      if (this.environment.containsKey(ms)) {
         this.moveEnvironment(ms, this.environment.get(ms) == 1 ? 2 : 1);
      } else {
         this.moveEnvironment(ms, 1);
      }
   }

   public final void moveEnvironment(String ms, int type) {
      this.broadcastMessage(CField.environmentMove(ms, type));
      this.environment.put(ms, type);
   }

   public final Map<String, Integer> getEnvironment() {
      return this.environment;
   }

   public final int getNumPlayersInArea(int index) {
      return this.getNumPlayersInRect(this.getArea(index));
   }

   public final int getNumPlayersInRect(Rectangle rect) {
      int ret = 0;
      Iterator<MapleCharacter> ltr = this.characters.iterator();

      while (ltr.hasNext()) {
         if (rect.contains(ltr.next().getTruePosition())) {
            ret++;
         }
      }

      return ret;
   }

   public final int getNumPlayersItemsInArea(int index) {
      return this.getNumPlayersItemsInRect(this.getArea(index));
   }

   public final int getNumPlayersItemsInRect(Rectangle rect) {
      int ret = this.getNumPlayersInRect(rect);
      this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();

      try {
         for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.ITEM).values()) {
            if (rect.contains(mmo.getTruePosition())) {
               ret++;
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
      }

      return ret;
   }

   public void broadcastGMMessage(MapleCharacter source, byte[] packet, boolean repeatToSource) {
      this.broadcastGMMessage(repeatToSource ? null : source, packet);
   }

   private void broadcastGMMessage(MapleCharacter source, byte[] packet) {
      if (source == null) {
         for (MapleCharacter chr : this.characters) {
            if (chr.isStaff()) {
               chr.getClient().getSession().writeAndFlush(packet);
            }
         }
      } else {
         for (MapleCharacter chrx : this.characters) {
            if (chrx != source && chrx.getGMLevel() >= source.getGMLevel()) {
               chrx.getClient().getSession().writeAndFlush(packet);
            }
         }
      }
   }

   public final List<Pair<Integer, Integer>> getMobsToSpawn() {
      return this.nodes.getMobsToSpawn();
   }

   public final List<Integer> getSkillIds() {
      return this.nodes.getSkillIds();
   }

   public final boolean canSpawn(long now) {
      return this.lastSpawnTime > 0L && this.lastSpawnTime + this.createMobInterval < now;
   }

   public final boolean canHurt(long now) {
      if (this.lastHurtTime > 0L && this.lastHurtTime + this.decHPInterval < now) {
         this.lastHurtTime = now;
         return true;
      } else {
         return false;
      }
   }

   public final void resetShammos(final MapleClient c) {
      this.killAllMonsters(true);
      this.broadcastMessage(CWvsContext.serverNotice(5,
            "A player has moved too far from Shammos. Shammos is going back to the start."));
      Timer.EtcTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (c.getPlayer() != null) {
               c.getPlayer().changeMap(Field.this, Field.this.getPortal(0));
               if (Field.this.getCharactersThreadsafe().size() > 1) {
                  MapScriptMethods.startScript_FirstUser(c, "shammos_Fenter");
               }
            }
         }
      }, 500L);
   }

   public int getInstanceId() {
      return this.instanceid;
   }

   public void setInstanceId(int ii) {
      this.instanceid = ii;
   }

   public int getPartyBonusRate() {
      return this.partyBonusRate;
   }

   public void setPartyBonusRate(int ii) {
      this.partyBonusRate = ii;
   }

   public short getTop() {
      return this.top;
   }

   public short getBottom() {
      return this.bottom;
   }

   public short getLeft() {
      return this.left;
   }

   public short getRight() {
      return this.right;
   }

   public void setTop(int ii) {
      this.top = (short) ii;
   }

   public void setBottom(int ii) {
      this.bottom = (short) ii;
   }

   public void setLeft(int ii) {
      this.left = (short) ii;
   }

   public void setRight(int ii) {
      this.right = (short) ii;
   }

   public int getNeedStarForce() {
      return this.starforce;
   }

   public int getNeedAuthenticForce() {
      return this.authenticforce;
   }

   public int getNeedArcaneForce() {
      return this.arcaneforce;
   }

   public void setNeedStarForce(int need) {
      this.starforce = need;
   }

   public void setNeedAuthenticForce(int need) {
      this.authenticforce = need;
   }

   public void setNeedArcaneForce(int need) {
      this.arcaneforce = need;
   }

   public List<Pair<Point, Integer>> getGuardians() {
      return this.nodes.getGuardians();
   }

   public MapleNodes.DirectionInfo getDirectionInfo(int i) {
      return this.nodes.getDirection(i);
   }

   public Collection<MapleCharacter> getNearestPvpChar(Point attacker, double maxRange, double maxHeight,
         boolean isLeft, Collection<MapleCharacter> chr) {
      Collection<MapleCharacter> character = new LinkedList<>();

      for (MapleCharacter a : this.characters) {
         if (chr.contains(a.getClient().getPlayer())) {
            Point attackedPlayer = a.getPosition();
            Portal Port = a.getMap().findClosestSpawnpoint(a.getPosition());
            Point nearestPort = Port.getPosition();
            double safeDis = attackedPlayer.distance(nearestPort);
            double distanceX = attacker.distance(attackedPlayer.getX(), attackedPlayer.getY());
            if (isLeft) {
               if (attacker.x < attackedPlayer.x
                     && distanceX < maxRange
                     && distanceX > 1.0
                     && attackedPlayer.y >= attacker.y - maxHeight
                     && attackedPlayer.y <= attacker.y + maxHeight) {
                  character.add(a);
               }
            } else if (attacker.x > attackedPlayer.x
                  && distanceX < maxRange
                  && distanceX > 1.0
                  && attackedPlayer.y >= attacker.y - maxHeight
                  && attackedPlayer.y <= attacker.y + maxHeight) {
               character.add(a);
            }
         }
      }

      return character;
   }

   public void startCatch() {
      if (this.catchstart == null) {
         this.broadcastMessage(CField.getClock(180));
         this.catchstart = Timer.MapTimer.getInstance()
               .schedule(
                     new Runnable() {
                        @Override
                        public void run() {
                           Field.this.broadcastMessage(CWvsContext.serverNotice(1,
                                 "[술래잡기 알림]\r\n제한시간 2분이 지나 양이 승리하였습니다!\r\n모든 분들은 게임 보상맵으로 이동됩니다."));

                           for (MapleCharacter chr : Field.this.getCharacters()) {
                              chr.getStat().setHp(chr.getStat().getCurrentMaxHp(chr), chr);
                              chr.updateSingleStat(MapleStat.HP, chr.getStat().getCurrentMaxHp(chr));
                              if (chr.isCatching) {
                                 chr.changeMap(
                                       chr.getClient().getChannelServer().getMapFactory().getMap(910040005),
                                       chr.getClient().getChannelServer().getMapFactory().getMap(910040005)
                                             .getPortalSP().get(0));
                                 chr.isWolfShipWin = false;
                              } else {
                                 chr.changeMap(
                                       chr.getClient().getChannelServer().getMapFactory().getMap(910040004),
                                       chr.getClient().getChannelServer().getMapFactory().getMap(910040004)
                                             .getPortalSP().get(0));
                                 chr.isWolfShipWin = true;
                              }
                           }

                           Field.this.stopCatch();
                        }
                     },
                     180000L);
      }
   }

   public void stopCatch() {
      if (this.catchstart != null) {
         this.catchstart.cancel(true);
         this.catchstart = null;
      }
   }

   public int getSummonCount(MapleCharacter chr, int skill) {
      int count = 0;
      if (GameConstants.isEvan(chr.getJob())) {
         return 0;
      } else {
         for (MapleMapObject o : chr.getMap().getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY,
               Arrays.asList(MapleMapObjectType.SUMMON))) {
            if (o.getType() == MapleMapObjectType.SUMMON && ((Summoned) o).getOwner() == chr
                  && ((Summoned) o).getSkill() == skill) {
               count++;
            }
         }

         return count;
      }
   }

   public List<Summoned> getSummonedInRect(int playerID, int skillID, Point basePosition, int ltX, int ltY, int rbX,
         int rbY) {
      List<Summoned> ret = new ArrayList<>();

      for (MapleMapObject o : this.getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY,
            Arrays.asList(MapleMapObjectType.SUMMON))) {
         if (o.getType() == MapleMapObjectType.SUMMON) {
            Summoned s = (Summoned) o;
            if (s.getOwnerId() == playerID && s.getSkill() == skillID) {
               Point position = s.getPosition();
               int x = position.x;
               int y = position.y;
               if (inRect(x, y, basePosition.x, basePosition.y, ltX, ltY, rbX, rbY)) {
                  ret.add(s);
               }
            }
         }
      }

      return ret;
   }

   public List<Wreckage> getWreckageInRect(Point basePosition, int ltX, int ltY, int rbX, int rbY, int playerID) {
      List<Wreckage> ret = new ArrayList<>();

      for (Wreckage wreckage : new ArrayList<>(this.getAllWreakage())) {
         if (wreckage != null && wreckage.getOwner().getId() == playerID) {
            Point position = wreckage.getPosition();
            int x = position.x;
            int y = position.y;
            if (inRect(x, y, basePosition.x, basePosition.y, ltX, ltY, rbX, rbY)) {
               ret.add(wreckage);
            }
         }
      }

      return ret;
   }

   public List<MapleMonster> getMobsInRect(Point basePosition, int ltX, int ltY, int rbX, int rbY) {
      List<MapleMonster> mobs = new ArrayList<>();

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         if (mob != null) {
            Point position = mob.getPosition();
            int x = position.x;
            int y = position.y;
            if (mob.getId() == 8880153 || inRect(x, y, basePosition.x, basePosition.y, ltX, ltY, rbX, rbY)) {
               mobs.add(mob);
            }
         }
      }

      return mobs;
   }

   public List<MapleMonster> getMobsInRect(Point basePosition, int ltX, int ltY, int rbX, int rbY, boolean faceLeft) {
      return this.getMobsInRect(basePosition, ltX, ltY, rbX, rbY, faceLeft, 0);
   }

   public List<MapleMonster> getMobsInRect(Point basePosition, int ltX, int ltY, int rbX, int rbY, boolean faceLeft,
         int ydiff) {
      List<MapleMonster> mobs = new ArrayList<>();

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         if (!GameConstants.isUnvisibleMob(mob.getId())) {
            if (mob.getId() == 8880153) {
               mobs.add(mob);
            } else if (!mob.getStats().isFriendly()) {
               Point position = mob.getPosition();
               int x = position.x;
               int y = position.y;
               if (mob.getId() == 8644650 || mob.getId() == 8644655 || mob.getId() == 8644658
                     || mob.getId() == 8644659) {
                  y -= 443;
                  if (ydiff != 0) {
                     y += ydiff;
                  }
               }

               if (mob.getId() == 8880700
                     || mob.getId() == 8880711
                     || mob.getId() == 8880722
                     || mob.getId() == 8880723
                     || mob.getId() == 8880727
                     || mob.getId() == 8880728) {
                  rbX += 200;
                  rbY += 200;
               }

               if (!faceLeft) {
                  int temp = ltX;
                  ltX = -rbX;
                  rbX = -temp;
               }

               if (inRect(x, y, basePosition.x, basePosition.y, ltX, ltY, rbX, rbY)) {
                  mobs.add(mob);
               }
            }
         }
      }

      return mobs;
   }

   public List<MapleCharacter> getPlayerInRect(Point basePosition, int ltX, int ltY, int rbX, int rbY) {
      List<MapleCharacter> players = new ArrayList<>();

      for (MapleCharacter player : this.getCharactersThreadsafe()) {
         if (player != null) {
            Point position = player.getPosition();
            int x = position.x;
            int y = position.y;
            if (inRect(x, y, basePosition.x, basePosition.y, ltX, ltY, rbX, rbY)) {
               players.add(player);
            }
         }
      }

      return players;
   }

   public List<MapleMonster> getMobsInRect(int ltX, int ltY, int rbX, int rbY) {
      List<MapleMonster> mobs = new ArrayList<>();

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         Point position = mob.getPosition();
         int x = position.x;
         int y = position.y;
         if (inRect(x, y, ltX, ltY, rbX, rbY)) {
            mobs.add(mob);
         }
      }

      return mobs;
   }

   public static boolean inRect(int objectX, int objectY, int baseX, int baseY, int ltX, int ltY, int rbX, int rbY) {
      return objectX >= baseX + ltX && objectX <= baseX + rbX && objectY >= baseY + ltY && objectY <= baseY + rbY;
   }

   public static boolean inRect(int objectX, int objectY, int ltX, int ltY, int rbX, int rbY) {
      return objectX >= ltX && objectY >= ltY && objectX <= rbX && objectY <= ltX;
   }

   public static boolean inRect(MapleMapObject target, MapleMapObject base, Point lt, Point rb) {
      return inRect(target.getPosition().x, target.getPosition().y, target.getPosition().x, target.getPosition().y,
            lt.x, lt.y, rb.x, rb.y);
   }

   public int getFrozenMonsterSize(long serialNumber) {
      int ret = 0;
      this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();

      try {
         for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.MONSTER).values()) {
            MapleMonster mob = (MapleMonster) mmo;
            if (mob.getFrozenLinkSerialNumber() == serialNumber) {
               ret++;
            }
         }
      } finally {
         this.mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
      }

      return ret;
   }

   public final void updateMonsterControllerOnFrozenLink(MapleMonster monster, long serialNumber) {
      if (monster.isAlive() && monster.getLinkCID() <= 0 && !monster.getStats().isEscort()) {
         if (monster.getController() != null) {
            if (monster.getController().getMap() == this
                  && !(monster.getController().getTruePosition().distanceSq(monster.getTruePosition()) > monster
                        .getRange())) {
               return;
            }

            monster.getController().stopControllingMonster(monster);
         }

         int mincontrolled = -1;
         MapleCharacter newController = null;

         for (MapleCharacter chr : this.characters) {
            if ((chr.getControlledSize() < mincontrolled || mincontrolled == -1)
                  && chr.getFrozenLinkSerialNumber() == serialNumber) {
               mincontrolled = chr.getControlledSize();
               newController = chr;
            }
         }

         if (newController != null) {
            if (monster.isFirstAttack()) {
               newController.controlMonster(monster, true);
               monster.setControllerHasAggro(true);
            } else {
               newController.controlMonster(monster, false);
            }
         }
      }
   }

   public final void spawnMonsterOnFrozenLink(MapleMonster monster, Point position, long serialNumber) {
      if (monster != null) {
         monster.setMap(this);
         monster.setPosition(position);
         monster.setFrozenLinkSerialNumber(serialNumber);
         this.addMapObject(monster);
         this.broadcastMessageFL(MobPacket.spawnMonster(monster, -2, 0), serialNumber);
         this.updateMonsterControllerOnFrozenLink(monster, serialNumber);
         this.spawnedMonstersOnMap.incrementAndGet();
      }
   }

   public void removeFieldAttackObj(FieldAttackObj obj) {
      if (obj != null) {
         this.broadcastMessage(CField.fieldAttackObj_Remove(obj.getObjectId()));
         this.removeMapObject(obj);
      }
   }

   public List<FieldAttackObj> getAllFieldAttackObj() {
      ArrayList<FieldAttackObj> ret = new ArrayList<>();

      for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.FIELD_ATTACK_OBJ).values()) {
         ret.add((FieldAttackObj) mmo);
      }

      return ret;
   }

   public final void spawnFieldAttackObj(final FieldAttackObj obj) {
      this.spawnAndAddRangedMapObject(obj, new Field.DelayedPacketCreation() {
         @Override
         public void sendPackets(MapleClient c) {
            Field.this.broadcastMessage(CField.fieldAttackObj_Create(obj));
            c.getSession().writeAndFlush(CField.fieldAttackObj_SetAttack(obj.getObjectId()));
         }
      });
   }

   public int getBreakTimeFieldLevel() {
      return this.breakTimeFieldLevel;
   }

   public void setBreakTimeFieldLevel(int breakTimeFieldLevel) {
      this.breakTimeFieldLevel = breakTimeFieldLevel;
   }

   public long getLastActiveFieldTime() {
      return this.lastActiveFieldTime;
   }

   public void setLastActiveFieldTime(long lastActiveFieldTime) {
      this.lastActiveFieldTime = lastActiveFieldTime;
   }

   public int checkBreakTimeField() {
      boolean check = false;

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         if (!mob.getStats().isBoss() && mob.getStats().getLevel() >= 100) {
            check = true;
            break;
         }
      }

      if (!check) {
         return -1;
      } else if (this.lastActiveFieldTime == 0L) {
         this.setBreakTimeFieldExpRate(this.breakTimeFieldLevel * 10);
         return this.breakTimeFieldLevel;
      } else if (this.getCharactersThreadsafe().size() == 1) {
         int time = (int) (System.currentTimeMillis() - this.lastActiveFieldTime);
         this.breakTimeFieldLevelExp -= time;
         this.breakTimeFieldLevelExp = Math.max(0, this.breakTimeFieldLevelExp);
         time = Math.max(0, time);
         int level = Math.min(10, this.breakTimeFieldLevel + time / 600000);
         this.setBreakTimeFieldLevel(level);
         this.setBreakTimeFieldExpRate(level * 10);
         return level;
      } else {
         return this.breakTimeFieldLevel;
      }
   }

   public int getBreakTimeFieldExpRate() {
      return this.breakTimeFieldExpRate;
   }

   public void setBreakTimeFieldExpRate(int breakTimeFieldExpRate) {
      this.breakTimeFieldExpRate = breakTimeFieldExpRate;
   }

   public void startBreakTimeFieldTask() {
      if (this.breakTimeFieldTask == null) {
         this.breakTimeFieldTask = Timer.MapTimer.getInstance()
               .register(
                     new Runnable() {
                        @Override
                        public void run() {
                           if (Field.this.breakTimeFieldLevel > 0) {
                              Field.this.breakTimeFieldLevelExp += 1000;
                              if (Field.this.breakTimeFieldLevelExp >= 600000) {
                                 if (Field.this.breakTimeFieldLevel == 1) {
                                    Field.this.breakTimeFieldLevel = 0;
                                    Field.this.breakTimeFieldExpRate = 0;
                                    TextEffect e = new TextEffect(-1, "#fn나눔고딕 ExtraBold##fs26#    버닝 소멸!!    ");
                                    Field.this.broadcastMessage(e.encodeForLocal());
                                    Field.this.cancelBreakTimeFieldTask();
                                 } else {
                                    Field.this.breakTimeFieldLevel--;
                                    Field.this.breakTimeFieldExpRate = Field.this.breakTimeFieldLevel * 10;
                                    TextEffect e = new TextEffect(
                                          -1,
                                          "#fn나눔고딕 ExtraBold##fs26#    버닝 "
                                                + Field.this.breakTimeFieldLevel
                                                + "단계 : 경험치 "
                                                + Field.this.breakTimeFieldLevel * 10
                                                + "%로 감소!!    ");
                                    Field.this.broadcastMessage(e.encodeForLocal());
                                 }

                                 Field.this.breakTimeFieldLevelExp = 0;
                              }
                           } else {
                              Field.this.cancelBreakTimeFieldTask();
                           }
                        }
                     },
                     1000L);
      }
   }

   public void cancelBreakTimeFieldTask() {
      if (this.breakTimeFieldTask != null) {
         this.breakTimeFieldTask.cancel(true);
         this.breakTimeFieldTask = null;
         this.setLastActiveFieldTime(System.currentTimeMillis());
      }
   }

   public boolean isVrlimit() {
      return this.vrlimit;
   }

   public void setVrlimit(boolean vrlimit) {
      this.vrlimit = vrlimit;
   }

   public long getLastRespawnTime() {
      return this.lastRespawnTime;
   }

   public void setLastRespawnTime(long lastRespawnTime) {
      this.lastRespawnTime = lastRespawnTime;
   }

   public long getLastUpdateFieldCatcherTime() {
      return this.lastUpdateFieldCatcherTime;
   }

   public void setLastUpdateFieldCatcherTime(long lastUpdateFieldCatcherTime) {
      this.lastUpdateFieldCatcherTime = lastUpdateFieldCatcherTime;
   }

   public void updateFieldFallingCatcher(long now) {
      if (this.getFieldType() == FieldType.Bellum.getType()) {
         boolean chaos = this.getId() % 1000 / 100 == 8;
         if (this.getLastUpdateFieldCatcherTime() == 0L
               || System.currentTimeMillis() - this.getLastUpdateFieldCatcherTime() >= 7000L) {
            MapleMonster mob = null;
            int[] mobs = new int[] { 8930100, 8930000 };

            for (int id : mobs) {
               mob = this.getMonsterById(id);
               if (mob != null) {
                  break;
               }
            }

            if (mob != null) {
               List<Point> pts = new ArrayList<>();
               Point pt = mob.getPosition();

               for (int i = -7; i < 7; i++) {
                  Point pt2 = new Point(pt.x, pt.y);
                  pt2.x = pt.x + i * (chaos ? 250 : 350);
                  this.calculateMBR();
                  if (pt2.x >= this.mBR.getLeft() && pt2.x <= this.mBR.getRight()) {
                     pts.add(pt2);
                  }
               }

               int idx = Randomizer.nextInt(3);
               FallingCatcher fallingCather = new FallingCatcher(123, 44, 0);
               this.fallingCatcher.put(idx, fallingCather);
               this.broadcastMessage(CField.sendCreateFallingCatcher("DropStone", idx, pts));
            }

            this.lastUpdateFieldCatcherTime = System.currentTimeMillis();
         }
      } else if (this.getFieldType() == FieldType.Pierre.getType()) {
         boolean chaos = this.getId() % 1000 / 100 == 6;
         int[] mobs = new int[] { 8900000, 8900001, 8900002, 8900100, 8900101, 8900102 };
         int count = 0;
         MapleMonster mob = null;

         for (int idx : mobs) {
            MapleMonster mob_ = this.getMonsterById(idx);
            if (mob_ != null && mob_.getId() == idx) {
               count++;
               mob = mob_;
            }
         }

         if (mob != null) {
            int interval = 10000;
            if (this.getFieldCatcherIndex() == 1 && count > 1) {
               interval = 1000;
            }

            if (this.getLastUpdateFieldCatcherTime() == 0L
                  || System.currentTimeMillis() - this.getLastUpdateFieldCatcherTime() >= interval) {
               List<Point> pts = new ArrayList<>();
               Point pt = mob.getPosition();

               for (int ix = -6; ix < 5; ix++) {
                  if (Randomizer.isSuccess(80)) {
                     Point pt2 = new Point(pt.x, pt.y);
                     pt2.x = pt.x + ix * (chaos ? 200 : 300);
                     this.calculateMBR();
                     if (pt2.x >= this.mBR.getLeft() && pt2.x <= this.mBR.getRight()) {
                        pts.add(pt2);
                     }
                  }
               }

               int idxx = Randomizer.nextInt(3);
               FallingCatcher fallingCather = new FallingCatcher(174, 3, 0);
               this.fallingCatcher.put(idxx, fallingCather);
               this.broadcastMessage(CField.sendCreateFallingCatcher("CapEffect", idxx, pts));
               if (this.getFieldCatcherIndex() > 0) {
                  this.setFieldCatcherIndex(0);
               } else {
                  this.setFieldCatcherIndex(1);
               }

               this.lastUpdateFieldCatcherTime = System.currentTimeMillis();
            }
         }
      }
   }

   public void onCatchDebuffCollision(MapleCharacter player, int idx) {
      FallingCatcher catcher = this.fallingCatcher.get(idx);
      if (catcher != null && catcher.getSkillID() > 0 && catcher.getSkillLevel() > 0) {
         boolean ignore = false;
         if (player.getBuffedValue(SecondaryStatFlag.DarkSight) != null) {
            ignore = true;
         }

         if (player.getBuffedValue(SecondaryStatFlag.GrandCrossSize) != null) {
            ignore = true;
         }

         if (!ignore) {
            MobSkillInfo mobSkill = MobSkillFactory.getMobSkill(catcher.getSkillID(), catcher.getSkillLevel());
            if (mobSkill != null) {
               MobSkillID msi = MobSkillID.getMobSkillIDByValue(catcher.getSkillID());
               SecondaryStatFlag disease = SecondaryStatFlag.getBySkill(catcher.getSkillID());
               switch (msi) {
                  case STUN:
                     player.giveDebuff(disease, 1, 0, (int) mobSkill.getDuration(), mobSkill.getSkillId(),
                           mobSkill.getSkillLevel());
                     break;
                  case LAPIDIFICATION:
                     player.giveDebuff(disease, mobSkill.getX(), mobSkill.getY(), (int) mobSkill.getDuration(),
                           mobSkill.getSkillId(), mobSkill.getSkillLevel());
               }
            }
         }
      }
   }

   public void checkMonsterTransform() {
      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         mob.checkTransform();
      }

      if (this.isCheckLinkMobRevive()) {
         for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
            mob.checkLinkMobRevive();
         }

         this.setCheckLinkMobRevive(false);
      }
   }

   public void registerObstacleAtom(
         ObstacleAtomCreateType createType, int atomType, int minCount, int maxCount, int duration, int interval,
         Consumer<ObstacleAtom>... option) {
      this.registerObstacleAtom(createType, atomType, minCount, maxCount, duration, interval, true, option);
   }

   public void registerObstacleAtom(
         ObstacleAtomCreateType createType,
         int atomType,
         int minCount,
         int maxCount,
         int duration,
         int interval,
         boolean overWrite,
         Consumer<ObstacleAtom>... option) {
      ObstacleAtomCreator creator = new ObstacleAtomCreator(minCount, maxCount);
      creator.createType = createType;
      creator.atomType = atomType;
      creator.obstacleAtomDuration = duration;
      creator.createInterval = interval;

      for (ObstacleAtomCreator oac : new ArrayList<>(this.obstacleAtomCreators)) {
         if (oac.atomType == atomType && oac.createType == createType && overWrite) {
            this.obstacleAtomCreators.remove(oac);
         }
      }

      for (Consumer<ObstacleAtom> op : option) {
         creator.options.add(op);
      }

      this.obstacleAtomCreators.add(creator);
   }

   public void clearObstacleAtomCreators() {
      this.obstacleAtomCreators.clear();
   }

   public void addObstacleAtomAction(ObstacleAtomAction action) {
      this.obstacleAtomActions.add(action);
   }

   public void broadcastMobPhaseChange(int objectID, int phase, int rampage) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_PHASE_CHANGE.getValue());
      packet.writeInt(objectID);
      packet.writeInt(phase);
      packet.write(rampage);
      this.broadcastMessage(packet.getPacket());
   }

   public void broadcastMobZoneChange(int objectID, int mobZoneDataType) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_ZONE_CHANGE.getValue());
      packet.writeInt(objectID);
      packet.writeInt(mobZoneDataType);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendBuffObjON(List<DebuffObj> debuffObjs) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.DEBUFF_OBJ_ON.getValue());

      for (DebuffObj obj : debuffObjs) {
         packet.write(1);
         obj.encode(packet);
      }

      packet.write(0);
      this.broadcastMessage(packet.getPacket());
   }

   public void fieldUpdatePerSeconds() {
      try {
         if (this.musicList.size() > 1) {
            long currentTime = System.currentTimeMillis();
            long musicTime = (Long) this.musicList.get(0).right;
            if (currentTime >= musicTime) {
               this.musicList.remove(0);
            }
         }
      } catch (Exception var6) {
      }

      List<ObstacleAtomCreatorEntry> creatorEntries = new ArrayList<>();
      new ArrayList<>(this.obstacleAtomCreators)
            .forEach(
                  atom -> {
                     if (atom.lastCreateObstacleAtomTime + atom.createInterval <= System.currentTimeMillis()
                           && (atom.beginCreateObstacleAtomTime == 0L || atom.beginCreateObstacleAtomTime
                                 + atom.obstacleAtomDuration >= System.currentTimeMillis())) {
                        Set<ObstacleAtom> set = new HashSet<>();

                        for (int i = 0; i < Randomizer.rand(atom.minCount, atom.maxCount); i++) {
                           ObstacleAtom oa = new ObstacleAtom();

                           for (Consumer c : atom.options) {
                              c.accept(oa);
                           }

                           atom.lastCreateObstacleAtomTime = System.currentTimeMillis();
                           if (atom.beginCreateObstacleAtomTime == 0L) {
                              atom.beginCreateObstacleAtomTime = System.currentTimeMillis();
                           }

                           oa.setKey(Randomizer.rand(0, 99999999));
                           oa.setAtomType(atom.atomType);
                           this.addObstacleAtom(oa);
                           set.add(oa);
                        }

                        creatorEntries.add(new ObstacleAtomCreatorEntry(atom.createType, null, null, set));
                     }

                     if (atom.beginCreateObstacleAtomTime + atom.obstacleAtomDuration <= System.currentTimeMillis()) {
                        this.obstacleAtomCreators.remove(atom);
                     }
                  });
      creatorEntries.forEach(entryx -> this
            .broadcastMessage(CField.createObstacle(entryx.createType, entryx.oiri, entryx.ori, entryx.obstacleAtom)));
      this.onUpdateFieldCommand();
      this.onUpdateDynamicObjectShow(System.currentTimeMillis());
      new ArrayList<>(this.obstacleAtomActions).forEach(atom -> {
         if (atom.getStartTime() != 0L && System.currentTimeMillis() - atom.getStartTime() >= atom.getDuration()) {
            this.obstacleAtomActions.remove(atom);
            Timer.MapTimer.getInstance().schedule(() -> {
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.OBSTACLE_ATOM_CLEAR.getValue());
               this.broadcastMessage(packet.getPacket());
            }, 10000L);
         } else {
            if (atom.getStartTime() == 0L || System.currentTimeMillis() - atom.getStartTime() >= atom.getInterval()) {
               if (atom.getStartTime() == 0L) {
                  atom.setStartTime(System.currentTimeMillis());
               }

               atom.getRunnable().run();
            }
         }
      });
      if (this.isActiveDebuffObjs() && !this.getDebuffObjs().isEmpty()) {
         List<DebuffObj> list = new ArrayList<>();

         for (DebuffObj debuffObj : this.getDebuffObjs().values()) {
            if (debuffObj.getLastActive() == 0L
                  || System.currentTimeMillis() - debuffObj.getLastActive() >= this.getDebuffObjInterval()) {
               debuffObj.setLastActive(System.currentTimeMillis());
               if (Randomizer.isSuccess(debuffObj.getActiveRate())) {
                  list.add(debuffObj);
               }
            }
         }

         if (!list.isEmpty()) {
            this.sendBuffObjON(list);
         }
      }

      if (this.hasteBoosterTarget != null && System.currentTimeMillis() >= this.useHasteBoosterTime) {
         if (this.useHasteBooster) {
            this.useHasteBooster = false;
            this.hasteBoosterTarget = null;
            this.useHasteBoosterTime = 0L;
            this.killAllMonster(9833710);
            this.hasteBoosterTarget.send(CField.playSound("Sound/SoundEff.img/HasteBooster/finish", 100));
            this.hasteBoosterTarget.send(CField.showHasteEffect(false));
         } else {
            this.useHasteBooster = true;
            this.useHasteBoosterTime = System.currentTimeMillis() + 90000L;
            this.hasteBoosterTarget.send(CField.playSound("Sound/SoundEff.img/HasteBooster/start", 100));
            this.hasteBoosterTarget.send(CField.showHasteEffect(true));
         }
      }

      new ArrayList<>(this.getAllSummonsThreadsafe()).forEach(summon -> {
         if (summon.getSkill() == 35111008 || summon.getSkill() == 35120002) {
            new ArrayList<>(this.getAllMonstersThreadsafe()).forEach(m -> {
               if (m != null && m.isAlive() && !m.getStats().isBoss()) {
                  if (summon.getOwner() != null) {
                     SecondaryStatEffect eff = SkillFactory.getSkill(summon.getSkill())
                           .getEffect(summon.getSkillLevel());
                     if (eff != null) {
                        Map<MobTemporaryStatFlag, MobTemporaryStatEffect> stats = new HashMap<>();
                        stats.put(MobTemporaryStatFlag.PDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.PDR,
                              eff.getY(), eff.getSourceId(), null, false));
                        stats.put(MobTemporaryStatFlag.MDR, new MobTemporaryStatEffect(MobTemporaryStatFlag.MDR,
                              eff.getY(), eff.getSourceId(), null, false));
                        m.applyMonsterBuff(stats, eff.getSourceId(), 5000L, null, Collections.EMPTY_LIST);
                     }
                  }
               }
            });
         }
      });
      if (this.getId() == 931000500) {
         if (this.getLastSpawnedSpecialJaguarTime() == 0L) {
            this.setLastSpawnedSpecialJaguarTime(System.currentTimeMillis());
         }

         if (System.currentTimeMillis() - this.getLastSpawnedSpecialJaguarTime() >= 7200000L) {
            int rand = 9304005 + Randomizer.rand(0, 3);
            Point pos = this.spawnPoints[Randomizer.rand(0, this.spawnPoints.length - 1)];
            this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(rand), pos);
            this.setLastSpawnedSpecialJaguarTime(System.currentTimeMillis());
         }
      }

      if (this.getFieldMonsterSpawners() != null && this.getFieldMonsterSpawners().size() > 0) {
         for (FieldMonsterSpawner spawner : this.getFieldMonsterSpawners()) {
            if (spawner.check(this)) {
               spawner.setLastSpawnTime(System.currentTimeMillis());
               Point pos = Randomizer.next(spawner.getSpawnPositions());
               int retry = 0;

               while (pos == null && retry++ < 5) {
                  pos = Randomizer.next(spawner.getSpawnPositions());
               }

               if (pos != null) {
                  this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(spawner.getSpawnMonster()), pos);
                  if (spawner.getSpawnDesc() != null && !spawner.getSpawnDesc().equals("")) {
                     this.broadcastMessage(CWvsContext.serverNotice(6, spawner.getSpawnDesc()));
                  }
               }
            }
         }
      }

      for (Entry<Integer, Long> entry : new HashMap<>(this.nextRemoveMonster).entrySet()) {
         if (entry.getValue() <= System.currentTimeMillis()) {
            MapleMonster mob = this.getMonsterByOid(entry.getKey());
            if (mob != null) {
               this.removeMonsterBySelfDestruct(mob);
               this.removeMonster(mob, 1);
            }

            this.nextRemoveMonster.remove(entry.getKey());
         }
      }
   }

   public void setObjectDisabled(String tag, boolean checkPreWord) {
      if (!checkPreWord) {
         this.enabledObject.remove(tag);
      } else {
         for (String k : this.enabledObject.keySet()) {
            if (k.startsWith(tag)) {
               this.enabledObject.remove(k);
            }
         }
      }

      this.broadcastObjectDisable(tag, checkPreWord);
   }

   public void setObjectEnable(String tag) {
      this.enabledObject.put(tag, true);
      this.broadcastObjectEnable(tag);
   }

   public boolean isObjectEnable(String tag) {
      return this.enabledObject.containsKey(tag) ? this.enabledObject.get(tag) : false;
   }

   private void broadcastObjectDisable(String tag, boolean checkPreWord) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      packet.write(3);
      packet.writeMapleAsciiString(tag);
      packet.write(checkPreWord);
      this.broadcastMessage(packet.getPacket());
   }

   private void broadcastObjectEnable(String tag) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_EFFECT.getValue());
      packet.write(2);
      packet.writeMapleAsciiString(tag);
      this.broadcastMessage(packet.getPacket());
   }

   public void addForceAtom(int key, ForceAtom atom) {
      this.forceAtoms.put(key, atom);
   }

   public void removeForceAtom(int key) {
      this.forceAtoms.remove(key);
   }

   public ForceAtom getForceAtom(int key) {
      return this.forceAtoms.get(key);
   }

   public List<ForceAtom> getForceAtoms() {
      List<ForceAtom> ret = new ArrayList<>();

      for (Entry<Integer, ForceAtom> a : new HashMap<>(this.forceAtoms).entrySet()) {
         ret.add(a.getValue());
      }

      return ret;
   }

   public FieldEvent getFieldEvent() {
      return this.fieldEvent;
   }

   public int getRemainEliteMobSpawn() {
      return this.remainEliteMobSpawn;
   }

   public void setRemainEliteMobSpawn(int remainEliteMobSpawn) {
      this.remainEliteMobSpawn = remainEliteMobSpawn;
   }

   public int getEliteLevel() {
      return this.eliteLevel;
   }

   public void setEliteLevel(int eliteLevel) {
      this.eliteLevel = eliteLevel;
   }

   public EliteState getEliteState() {
      return this.eliteState;
   }

   public void setEliteState(EliteState eliteState) {
      this.eliteState = eliteState;
   }

   public void setFieldEvent(FieldEvent event) {
      FieldEvent before = this.getFieldEvent();
      if (before != null) {
         before.onEnd();
      }

      this.fieldEvent = event;
      if (event != null) {
         event.onStart();
      }
   }

   public boolean isMobGen() {
      return this.mobGen;
   }

   public void setMobGen(boolean mobGen) {
      this.mobGen = mobGen;
   }

   public void setMobGen(boolean mobGen, Integer id) {
      if (mobGen) {
         this.mobGenExcept.remove(id);
      } else {
         boolean check = false;

         for (int mid : this.mobGenExcept) {
            if (mid == id) {
               check = true;
            }
         }

         if (!check) {
            this.mobGenExcept.add(id);
         }
      }
   }

   protected void updateOfflineBossLimit(int playerId, int questId, String key, String value) {
      DBConnection db = new DBConnection();

      try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con
               .prepareStatement("SELECT `customData` FROM questinfo WHERE characterid = ? and quest = ?");
         ps.setInt(1, playerId);
         ps.setInt(2, questId);
         ResultSet rs = ps.executeQuery();
         boolean f = false;

         while (rs.next()) {
            f = true;
            String customData = rs.getString("customData");
            String[] v = customData.split(";");
            StringBuilder sb = new StringBuilder();
            int i = 1;
            int count = 0;
            boolean a = false;

            for (String v_ : v) {
               String[] cd = v_.split("=");
               if (!cd[0].equals(key)) {
                  sb.append(v_);
                  if (!v_.equals(v[v.length - 1])) {
                     sb.append(";");
                  }
               } else {
                  a = true;
                  if (key.equals("count")) {
                     count = Integer.parseInt(cd[1].replace(";", ""));
                     sb.append(key).append("=").append(count + 1);
                  } else {
                     sb.append(key).append("=").append(value);
                  }

                  if (!v_.equals(v[v.length - 1])) {
                     sb.append(";");
                  }
               }
            }

            PreparedStatement ps2 = con
                  .prepareStatement("UPDATE questinfo SET customData = ? WHERE characterid = ? and quest = ?");
            ps2.setString(1, sb.toString());
            ps2.setInt(2, playerId);
            ps2.setInt(3, questId);
            ps2.executeUpdate();
            ps2.close();
         }

         if (!f) {
            PreparedStatement ps2 = con
                  .prepareStatement("INSERT INTO questinfo (characterid, quest, customData, date) VALUES (?, ?, ?, ?)");
            ps2.setInt(1, playerId);
            ps2.setInt(2, questId);
            ps2.setString(3, key + "=" + value + ";");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String time = sdf.format(Calendar.getInstance().getTime());
            ps2.setString(4, time);
            ps2.executeQuery();
            ps2.close();
         }

         rs.close();
         ps.close();
      } catch (SQLException var23) {
         var23.printStackTrace();
      }
   }

   public void onUserHit(MapleCharacter player, int mobTemplateID, int skillID, int skillLevel, int attackIndex) {
   }

   public void onEnter(MapleCharacter player) {
      FieldEvent event = this.getFieldEvent();
      if (event != null) {
         event.onUserEnter(player);
      }

      FieldSetInstance fsi = this.getFieldSetInstance();
      if (fsi != null) {
         fsi.userEnter(player);
      }
   }

   public void onLeave(MapleCharacter player) {
      FieldEvent event = this.getFieldEvent();
      if (event != null) {
         event.onUserLeave(player);
      }
   }

   public void onLeaveFieldSet(MapleCharacter player, Field to) {
      FieldSetInstance fsi = this.getFieldSetInstance();
      if (fsi != null) {
         fsi.userLeave(player, to);
      }
   }

   public void onDisconnected(MapleCharacter user) {
      FieldSetInstance fsi = this.getFieldSetInstance();
      if (fsi != null) {
         fsi.userDisconnected(user);
      }
   }

   public void onMobChangeHP(MapleMonster mob) {
      FieldSetInstance fsi = this.getFieldSetInstance();
      if (fsi != null) {
         fsi.mobChangeHP(mob);
      }
   }

   public void onMobKilled(MapleMonster mob) {
      FieldEvent event = this.getFieldEvent();
      if (event != null) {
         event.onMobLeave(mob);
      }

      FieldSetInstance fsi = this.getFieldSetInstance();
      if (fsi != null) {
         fsi.mobDead(mob);
      }
   }

   public void onMobEnter(MapleMonster mob) {
      FieldEvent event = this.getFieldEvent();
      if (event != null) {
         event.onMobEnter(mob);
      }

      mob.doPassiveSkill();
   }

   public void onMobSkill(MapleMonster mob, int skillID, int skillLevel) {
      MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(skillID, skillLevel);
      if (mobSkillInfo != null) {
         if (mob.getController() == null) {
            return;
         }

         String lua = null;
         if ((lua = mobSkillInfo.getMobSkillStats(MobSkillStat.lua)) != null && !lua.isEmpty()
               && ScriptManager.get()._scripts.get(lua) != null) {
            MapleNPC npc = MapleLifeFactory.getNPC(9010000);
            ScriptManager.runScript(mob.getController().getClient(), lua, npc, null);
         }
      }
   }

   public void onPlayerDead(MapleCharacter player) {
      FieldSetInstance fsi = this.getFieldSetInstance();
      if (fsi != null) {
         fsi.userDead(player);
      }
   }

   public void onLeftParty(MapleCharacter user) {
      FieldSetInstance fsi = this.getFieldSetInstance();
      if (fsi != null) {
         fsi.userLeftParty(user);
      }
   }

   public void onDisbandParty() {
      FieldSetInstance fsi = this.getFieldSetInstance();
      if (fsi != null) {
         fsi.userDisbandParty();
      }
   }

   public void handleFreeze(MapleMonster mob) {
   }

   public void handleMobHP(MapleMonster mob) {
   }

   public void onUpdateFieldCommand() {
      long now = System.currentTimeMillis();
      if (this.currentFieldCommand != null) {
         if (this.fieldCommandContext != null) {
            if (this.fieldCommandContext.startTime <= now) {
               if (this.fieldCommandContext.nextPickTime <= now) {
                  FieldCommand.Sequence seq = this.currentFieldCommand.seq.get(this.fieldCommandContext.sequence);
                  this.onProcessFieldCommand(seq);
                  FieldCommandContext context = this.fieldCommandContext;
                  context.pickCount++;
                  if (context.pickCount >= seq.pickCount) {
                     context.sequence++;
                     context.pickCount = 0;
                     if (this.currentFieldCommand.seq.size() <= this.fieldCommandContext.sequence) {
                        this.fieldCommandContext.sequence = 0;
                        this.fieldCommandContext.repeatCount++;
                        if (this.currentFieldCommand.repeat <= this.fieldCommandContext.repeatCount) {
                           this.resetFieldCommand();
                           this.onCompleteFieldCommand();
                           return;
                        }

                        this.fieldCommandContext.nextPickTime = System.currentTimeMillis()
                              + this.currentFieldCommand.idleTime;
                        return;
                     }
                  }

                  this.fieldCommandContext.nextPickTime = System.currentTimeMillis() + seq.delay;
               }
            }
         }
      }
   }

   public void onProcessFieldCommand(FieldCommand.Sequence sequence) {
      List<MapleMonster> mobs = this.getAllMonstersThreadsafe().stream()
            .filter(m -> sequence.targets.containsKey(m.getId())).collect(Collectors.toList());
      Collections.shuffle(mobs);
      mobs.stream().limit(sequence.mobCount).forEach(m -> {
         int skillIdx = sequence.targets.get(m.getId());
         this.broadcastMessage(MobPacket.mobForcedSkillAction(m.getObjectId(), skillIdx, false));
         int delay = sequence.delay;
         MobSkill ms = m.getSkills().get(skillIdx);
         if (ms != null) {
            MobSkillInfo msi = MobSkillFactory.getMobSkill(ms.getMobSkillID(), ms.getLevel());
            if (msi != null) {
               if (delay == 0) {
                  msi.applyEffect(m.getController(), m, ms, true, false, new Point(0, 0));
               } else {
                  delay -= 300;
                  this.broadcastMessage(MobPacket.mobSkillDelay(m.getObjectId(), delay, ms.getMobSkillID(),
                        ms.getLevel(), 0, Collections.EMPTY_LIST));
               }
            }
         }
      });
   }

   public void onCompleteFieldCommand() {
   }

   public void setFieldCommand(FieldCommand fieldCommand) {
      this.currentFieldCommand = fieldCommand;
      this.fieldCommandContext = new FieldCommandContext(0L, 0, 0, 0,
            System.currentTimeMillis() + fieldCommand.idleTime);
   }

   public void resetFieldCommand() {
      this.currentFieldCommand = null;
      this.fieldCommandContext = null;
   }

   public void sendSmartMobNotice(SmartMobNoticeType noticeType, int mobTemplateID, SmartMobMsgType msgType,
         int actionID, String message) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SMART_MOB_NOTICE_MSG.getValue());
      packet.writeInt(noticeType.getType());
      packet.writeInt(mobTemplateID);
      packet.writeInt(msgType.getType());
      packet.writeInt(actionID);
      packet.writeMapleAsciiString(message);
      this.broadcastMessage(packet.getPacket());
   }

   public long getLastSpawnRuneTime() {
      return this.lastSpawnRuneTime;
   }

   public void setLastSpawnRuneTime(long lastSpawnRuneTime) {
      this.lastSpawnRuneTime = lastSpawnRuneTime;
   }

   public int getEliteBossCurseLevel() {
      return this.eliteBossCurseLevel;
   }

   public void setEliteBossCurseLevel(int eliteBossCurseLevel) {
      this.eliteBossCurseLevel = eliteBossCurseLevel;
   }

   public long getLastUpdateEliteBossCurseTime() {
      return this.lastUpdateEliteBossCurseTime;
   }

   public void setLastUpdateEliteBossCurseTime(long lastUpdateEliteBossCurseTime) {
      this.lastUpdateEliteBossCurseTime = lastUpdateEliteBossCurseTime;
   }

   public boolean isSpawnEventMob() {
      return this.spawnEventMob;
   }

   public void setSpawnEventMob(boolean spawnEventMob) {
      this.spawnEventMob = spawnEventMob;
   }

   public int getRemainEventMobSpawn() {
      return this.remainEventMobSpawn;
   }

   public void setRemainEventMobSpawn(int remainEventMobSpawn) {
      this.remainEventMobSpawn = remainEventMobSpawn;
   }

   public List<Point> getRandomPositions(int count, List<MapleFoothold> fhs) {
      List<Point> points = new ArrayList<>();

      for (int i = 0; i < count; i++) {
         MapleFoothold fh = fhs.get(Randomizer.nextInt(fhs.size()));
         int x = Randomizer.rand(fh.getX1(), fh.getX2());
         int minY = Math.min(fh.getY1(), fh.getY2());
         int maxY = Math.max(fh.getY1(), fh.getY2());
         double f = (double) (x - fh.getX1()) / (fh.getX2() - fh.getX1());
         int y = (int) (minY + (maxY - minY) * f);
         points.add(new Point(x, y));
      }

      return points;
   }

   public List<Point> getFootholdRandomly(int count, Rect rect) {
      List<Point> points = new ArrayList<>();
      Rect mbr = this.calculateMBR();
      Rect range = Rect.Intersect(rect, mbr);
      int length = 2 * count;
      int x1 = range.getX1();
      int y1 = range.getY1();
      int x2 = range.getX2();
      int y2 = range.getY2();
      int dx = (x2 - x1 + 1) / length;
      int[] uniques = new int[length];
      int i = 0;

      while (i < length) {
         uniques[i] = i++;
      }

      i = 0;

      for (int ix = 0; ix < uniques.length * 30; ix++) {
         int r = Randomizer.rand(0, uniques.length - 1);
         i = uniques[0];
         uniques[0] = uniques[r];
         uniques[r] = i;
      }

      if (length > 0 && dx > 0) {
         for (int ix = 0; ix < length; ix++) {
            int rnd = Randomizer.nextInt(dx);
            int x = x1 + rnd + dx * uniques[ix];
            List<Point> footholdRange = this.getFootholdWithin(x, y1, y2);
            int size = footholdRange.size();
            if (size != 0) {
               int index = Randomizer.nextInt(size);
               points.add(footholdRange.get(index));
               if (points.size() == count) {
                  break;
               }
            }
         }
      }

      return points;
   }

   public List<Point> getFootholdWithin(int x, int y1, int y2) {
      List<Point> points = new ArrayList<>();

      for (MapleFoothold fh : this.getFootholds().getFootholds()) {
         if (fh != null && fh.getX1() < fh.getX2() && fh.getX1() <= x && x <= fh.getX2()) {
            double n = fh.getX2() - fh.getX1();
            double m = (fh.getY2() - fh.getY1()) / n;
            int fx = (int) (m * (x - fh.getX1()) + fh.getY1());
            if (y1 <= fx && fx <= y2) {
               points.add(new Point(x, fx));
            }
         }
      }

      return points;
   }

   public boolean isCanSummonSubMob(int skillID, int skillLevel) {
      return true;
   }

   public void setSummonSubMob(long time) {
   }

   public Point getReviveCurFieldOfNoTransferPoint() {
      return this.reviveCurFieldOfNoTransferPoint;
   }

   public void setReviveCurFieldOfNoTransferPoint(Point reviveCurFieldOfNoTransferPoint) {
      this.reviveCurFieldOfNoTransferPoint = reviveCurFieldOfNoTransferPoint;
   }

   public boolean isReviveCurFieldOfNoTransfer() {
      return this.reviveCurFieldOfNoTransfer;
   }

   public void setReviveCurFieldOfNoTransfer(boolean reviveCurFieldOfNoTransfer) {
      this.reviveCurFieldOfNoTransfer = reviveCurFieldOfNoTransfer;
   }

   public void addObstacleAtom(ObstacleAtom atom) {
      this.obstacleAtoms.add(atom);
   }

   public void removeObstacleAtom(ObstacleAtom atom) {
      this.obstacleAtoms.remove(atom);
   }

   public ObstacleAtom getObstacleAtom(int key) {
      for (ObstacleAtom atom : new ArrayList<>(this.obstacleAtoms)) {
         if (atom.getKey() == key) {
            return atom;
         }
      }

      return null;
   }

   public void addSpiderWeb(SpiderWeb web) {
      this.spiderWebs.add(web);
      if (this instanceof Field_WillBattle) {
         Field_WillBattle f = (Field_WillBattle) this;
         f.sendWillSpiderWeb(null, web, true);
      }
   }

   public void removeSpiderWeb(SpiderWeb web) {
      this.spiderWebs.remove(web);
      if (this instanceof Field_WillBattle) {
         Field_WillBattle f = (Field_WillBattle) this;
         f.setNextCreateWebTime(f.getNextCreateWebTime() + 1000L);
         f.sendWillSpiderWeb(null, web, false);
      }
   }

   public List<SpiderWeb> getSpiderWebs() {
      return this.spiderWebs;
   }

   public SpiderWeb getSpiderWeb(int objectID) {
      for (SpiderWeb web : new ArrayList<>(this.getSpiderWebs())) {
         if (web.getObjectID() == objectID) {
            return web;
         }
      }

      return null;
   }

   public void clearSpiderWeb() {
      this.spiderWebs.clear();
   }

   public long getLastCheckMacroTime() {
      return this.lastCheckMacroTime;
   }

   public void setLastCheckMacroTime(long lastCheckMacroTime) {
      this.lastCheckMacroTime = lastCheckMacroTime;
   }

   public long getLastSpawnedSpecialJaguarTime() {
      return this.lastSpawnedSpecialJaguarTime;
   }

   public void setLastSpawnedSpecialJaguarTime(long lastSpawnedSpecialJaguarTime) {
      this.lastSpawnedSpecialJaguarTime = lastSpawnedSpecialJaguarTime;
   }

   public boolean checkDojangClear() {
      if (this instanceof Field_Dojang) {
         Field_Dojang f = (Field_Dojang) this;
         return f.checkClear();
      } else {
         return false;
      }
   }

   public void checkDojangStopClockTime() {
      if (this instanceof Field_Dojang) {
         Field_Dojang f = (Field_Dojang) this;
         f.checkStopClockTime();
      }
   }

   public void updateDojangRanking() {
      if (this instanceof Field_Dojang) {
         Field_Dojang f = (Field_Dojang) this;
         f.updateRanking();
      }
   }

   public void addNextRemoveMonster(int objectID, long removeTime) {
      this.nextRemoveMonster.put(objectID, removeTime);
   }

   private void onUpdateDynamicObjectShow(long now) {
      boolean change = false;

      for (DynamicObject object : this.dynamicObjects) {
         if (object.getWaitingDuration() > 0 && object.getBeginShowTime() != 0L
               && object.getBeginShowTime() + object.getWaitingDuration() <= now) {
            object.reset();
            change = true;
         }
      }

      if (change) {
         this.broadcastMessage(this.makeDynamicObjectSyncPacket());
      }
   }

   public List<DynamicObject> getDynamicObjects() {
      return this.dynamicObjects;
   }

   public DynamicObject getDynamicObject(String name) {
      return this.dynamicObjects.stream().filter(object -> object.getName().equals(name)).findFirst().orElse(null);
   }

   public byte[] makeDynamicObjectSyncPacket() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SYNC_DYNAMIC_FOOTHOLD.getValue());
      packet.writeInt(this.dynamicObjects.size());
      new ArrayList<>(this.dynamicObjects).forEach(obj -> obj.encode(packet));
      return packet.getPacket();
   }

   public void onDynamicObjectCollision(MapleCharacter player, int id, boolean userCollide) {
      if (this.dynamicObjects.size() > id && id >= 0) {
         DynamicObject obj = null;

         for (DynamicObject ob : this.dynamicObjects) {
            if (ob.getIndex() == id) {
               obj = ob;
               break;
            }
         }

         if (obj.isWaiting()) {
            obj.setWaiting(false);
            if (!userCollide) {
               obj.setCurState(1);
               obj.setBeginShowTime(System.currentTimeMillis());
               this.broadcastMessage(this.makeDynamicObjectSyncPacket());
               return;
            }

            obj.reset();

            for (DynamicObject.CollisionDisease disease : obj.getUserCollisionDisease()) {
               if (player.getBuffedValue(SecondaryStatFlag.StormGuard) == null
                     && player.getBuffedValue(SecondaryStatFlag.BlessingArmor) == null
                     && player.getBuffedValue(SecondaryStatFlag.HolyMagicShell) == null
                     && player.getBuffedValue(SecondaryStatFlag.DarkSight) == null) {
                  player.giveDebuff(
                        SecondaryStatFlag.getBySkill(disease.getDiseaseSkillID()),
                        MobSkillFactory.getMobSkill(disease.getDiseaseSkillID(), disease.getDiseaseSkillLevel()));
               }
            }
         }
      }
   }

   public void startHasteBooster(MapleCharacter target) {
      target.send(CField.UIPacket.closeUI(1251));
      if (this.hasteBoosterTarget != null) {
         target.dropMessage(5, "이미 누군가가 헤이스트 부스터를 사용 중입니다.");
      } else if (this.eliteState != EliteState.Normal) {
         target.dropMessage(5, "엘리트 보스가 소환되어 있습니다.");
      } else if (!this.isTown() && this.getMonsterSpawn().size() > 0) {
         this.hasteBoosterTarget = target;
         this.useHasteBoosterTime = System.currentTimeMillis() + 3000L;
         target.send(CField.showEffect("Effect/EventEffect.img/HasteBooster/startEff"));
         target.send(CField.playSound("Sound/SoundEff.img/HasteBooster/count", 100));
         target.setFrozenLinkSerialNumber(this.useHasteBoosterTime);
         int count = Integer.parseInt(target.getOneInfo(QuestExConstants.HasteEvent.getQuestID(), "booster")) - 1;
         target.updateOneInfo(QuestExConstants.HasteEvent.getQuestID(), "booster", count + "");
      } else {
         target.dropMessage(5, "헤이스트 부스터를 사용할 수 없는 맵입니다.");
      }
   }

   public List<FieldMonsterSpawner> getFieldMonsterSpawners() {
      return this.monsterSpawners;
   }

   public void addFieldMonsterSpawner(FieldMonsterSpawner spawner) {
      if (this.monsterSpawners == null) {
         this.monsterSpawners = new ArrayList<>();
      }

      this.monsterSpawners.add(spawner);
   }

   public void clearCurrentPhase(Party party) {
      for (PartyMemberEntry e : party.getPartyMember().getPartyMemberList()) {
         for (GameServer gameServer : GameServer.getAllInstances()) {
            MapleCharacter player = gameServer.getPlayerStorage().getCharacterById(e.getId());
            if (player != null) {
               player.setCurrentBossPhase(0);
            }
         }
      }
   }

   public int getMusicSize() {
      return this.musicList.size();
   }

   public void addMusicList(String music) {
      if (this.nextMusicTime < System.currentTimeMillis()) {
         this.nextMusicTime = System.currentTimeMillis() + 300000L;
      } else {
         this.nextMusicTime += 300000L;
      }

      this.musicList.add(new Pair<>(music, this.nextMusicTime));
   }

   public List<String> getMusicList() {
      List<String> list = new ArrayList<>();

      for (Pair<String, Long> pair : this.musicList) {
         list.add(pair.left);
      }

      return list;
   }

   public List<String> getMusicTimeList() {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
      List<String> list = new ArrayList<>();

      for (Pair<String, Long> pair : this.musicList) {
         Date date = new Date();
         date.setTime(pair.right);
         String dateString = simpleDateFormat.format(date);
         list.add(dateString);
      }

      return list;
   }

   public String getFirstMusic() {
      return this.musicList.size() > 0 ? (String) this.musicList.get(0).left : null;
   }

   public void clearMusicList() {
      this.musicList.clear();
      this.nextMusicTime = System.currentTimeMillis();
   }

   public void gettest() {
   }

   private class ActivateItemReactor implements Runnable {
      private Drop mapitem;
      private Reactor reactor;
      private MapleClient c;

      public ActivateItemReactor(Drop mapitem, Reactor reactor, MapleClient c) {
         this.mapitem = mapitem;
         this.reactor = reactor;
         this.c = c;
      }

      @Override
      public void run() {
         if (this.mapitem != null
               && this.mapitem == Field.this.getMapObject(this.mapitem.getObjectId(), this.mapitem.getType())
               && !this.mapitem.isPickedUp()) {
            this.mapitem.expire(Field.this);
            this.reactor.hitReactor(this.c);
            this.reactor.setTimerActive(false);
            if (this.reactor.getDelay() > 0) {
               Timer.MapTimer.getInstance().schedule(new Runnable() {
                  @Override
                  public void run() {
                     ActivateItemReactor.this.reactor.forceHitReactor((byte) 0);
                  }
               }, this.reactor.getDelay());
            }
         } else {
            this.reactor.setTimerActive(false);
         }
      }
   }

   protected interface DelayedPacketCreation {
      void sendPackets(MapleClient var1);
   }
}
