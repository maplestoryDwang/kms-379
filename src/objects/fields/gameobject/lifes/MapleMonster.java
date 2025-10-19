package objects.fields.gameobject.lifes;

import constants.DailyEventType;
import constants.GameConstants;
import constants.ServerConstants;
import database.DBConfig;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import network.SendPacketOpcode;
import network.center.Center;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import network.models.FontColorType;
import network.models.FontType;
import network.models.MobPacket;
import objects.context.MonsterCollection;
import objects.context.SpecialSunday;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.effect.child.HPHeal;
import objects.fields.Field;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.child.blackheaven.Field_BlackHeavenBoss;
import objects.fields.child.blackmage.Field_BlackMage;
import objects.fields.child.blackmage.Field_BlackMageBattlePhase4;
import objects.fields.child.demian.Field_Demian;
import objects.fields.child.etc.Field_MMRace;
import objects.fields.child.horntail.Field_Horntail;
import objects.fields.child.papulatus.Field_Papulatus;
import objects.fields.child.sernium.Field_SerenPhase2;
import objects.fields.child.will.Field_WillBattle;
import objects.fields.child.zakum.Field_Zakum;
import objects.fields.gameobject.lifes.mobskills.AffectedOtherSkill;
import objects.fields.gameobject.lifes.mobskills.LinkMobInfo;
import objects.fields.gameobject.lifes.mobskills.MobCastingBarSkill;
import objects.fields.gameobject.lifes.mobskills.MobSkill;
import objects.fields.gameobject.lifes.mobskills.MobSkillCommand;
import objects.fields.gameobject.lifes.mobskills.MobSkillContext;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.fields.gameobject.lifes.mobskills.MobSkillID;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.fields.gameobject.lifes.mobskills.MobSkillStat;
import objects.fields.gameobject.lifes.mobskills.PassiveInfo;
import objects.fields.gameobject.lifes.mobskills.TransInfo;
import objects.fields.gameobject.lifes.mobskills.TransformBackReference;
import objects.item.Item;
import objects.item.MapleItemInformationProvider;
import objects.item.MaplePet;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.achievement.AchievementFactory;
import objects.users.skills.IndieTemporaryStatEntry;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.ConcurrentEnumMap;
import objects.utils.FileoutputUtil;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;
import objects.utils.Timer;
import scripting.EventInstanceManager;
import scripting.EventManager;

public class MapleMonster extends AbstractLoadedMapleLife {
   private MapleMonsterStats stats;
   private ChangeableStats ostats = null;
   private long hp;
   private long maxHp;
   private long nextKill = 0L;
   private long lastDropTime = 0L;
   private int mp;
   private byte carnivalTeam = -1;
   private int createDelay = 0;
   private int owner;
   private Field map;
   private WeakReference<MapleMonster> sponge = new WeakReference<>(null);
   private int linkoid = 0;
   private int lastNode = -1;
   private int highestDamageChar = 0;
   private int linkCID = 0;
   private WeakReference<MapleCharacter> controller = new WeakReference<>(null);
   private boolean fake = false;
   private boolean dropsDisabled = false;
   private boolean controllerHasAggro = false;
   private final Collection<MapleMonster.AttackerEntry> attackers = new ArrayList<>();
   private EventInstanceManager eventInstance;
   private MonsterListener listener = null;
   private byte[] nodepack = null;
   private final ConcurrentEnumMap<MobTemporaryStatFlag, MobTemporaryStatEffect> stati = new ConcurrentEnumMap<>(
         MobTemporaryStatFlag.class);
   private final List<MobTemporaryStatEffect> poisons = new LinkedList<>();
   private final ReentrantReadWriteLock poisonsLock = new ReentrantReadWriteLock();
   private Map<MobSkillInfo, Long> usedSkills;
   private boolean stolen = false;
   private boolean shouldDropItem = false;
   private boolean killed = false;
   private long lastReceivedMovePacket = System.currentTimeMillis();
   private EliteGrade eliteMobGrade = EliteGrade.None;
   private EliteType eliteMobType = EliteType.None;
   private int scale;
   private long canFreezeTime = 0L;
   private long frozenLinkSerialNumber = 0L;
   private List<EliteMonsterSkill> eliteMonsterSkills = new ArrayList<>();
   private List<Integer> attackBlocked = new ArrayList<>();
   private List<Integer> skillFilters = new ArrayList<>();
   private List<Integer> allowedFsmSkills = new ArrayList<>();
   private List<Integer> allowedOnetimeFsmSkills = new ArrayList<>();
   private Map<Integer, List<Integer>> attackPriority = new HashMap<>();
   private List<Point> obstaclePosition = new ArrayList<>();
   private List<Rect> fireAtRandomAttack = new ArrayList<>();
   private List<MapleCharacter> damageShareUsers = new ArrayList<>();
   private boolean nextAttackPossible = false;
   private List<MobSkillContext> msc = new ArrayList<>();
   private int demianDelayedAttackID;
   private List<Pair<Integer, Integer>> relMobZones = new ArrayList<>();
   private int mobZoneDataType = 0;
   private int phase = 0;
   private boolean rampage = false;
   private long chaseEnd = 0L;
   private int targetFromSvr = 0;
   private List<Integer> oneTimeForcedAttack = new ArrayList<>();
   private int castingSkillID = 0;
   private int castingSkillLevel = 0;
   private long castingEnd = 0L;
   private long castingCancelDamage = 0L;
   private MobSkillInfo.CastingActionData castingAction_Success = null;
   private MobSkillInfo.CastingActionData castingAction_Failed = null;
   private MobSkillCommand command = new MobSkillCommand(0, 0, 0);
   private int addDamPartyFrom = 0;
   private int addDamPartyPartyID = 0;
   private int addDamPartyC = 0;
   private int seperateSoulCW = 0;
   private int seperateSoulPW = 0;
   private int seperateSoulPU = 0;
   private int fatalityPartyValue = 0;
   private int fatalityFrom = 0;
   private int fatalityPartyID = 0;
   private int pinpointPierceDebuffX = 0;
   private int suctionBottlePlayerID = 0;
   private int addDamByHealPartyID = 0;
   private int addDamByHealFrom = 0;
   private int addDamByHealC = 0;
   private Map<Integer, Long> resistSkillBySkillId = new HashMap<>();
   private Map<MobTemporaryStatFlag, Long> resistSkill = new HashMap<>();
   private Map<Integer, Long> resistOriginSkillBySkillId = new HashMap<>();
   private Map<MobTemporaryStatFlag, Long> resistOriginSkill = new HashMap<>();
   private Map<MobTemporaryStatFlag, List<IndieTemporaryStatEntry>> indieTemporaryStats;
   private int incizingFrom = 0;
   private int incizingPartyID = 0;
   private int incizingPartyValue = 0;
   private long lastUseSkillTime = 0L;
   private long skillForbid = 0L;
   private boolean setShriekingWallPattern = false;
   private long totalShieldHP = 0L;
   private long shieldHP = 0L;
   private int indieMdrStack = 0;
   private int indieMdrFrom = 0;
   private int blizzardTempestStack = 0;
   private long blizzardTempestEndTime = 0L;
   private int bahamutLightElemAddDamC = 0;
   private int bahamutLightElemAddDamP = 0;
   private int curseMarkAddDamPMdr = 0;
   private int curseMarkAddDamX = 0;
   private int curseMarkAddDamPassiveReason = 0;
   private int timeCurseLevel = 0;
   private int timeCurseX = 0;
   private int multiPMDRC = 0;
   private int mobStatusAddPad = 0;
   private int mobStatusAddMad = 0;
   private int mobStatusAddPdd = 0;
   private int mobStatusAddMdd = 0;
   private TransformBackReference transformBackReference = null;
   private long linkedNextTransformCooltime = 0L;
   private boolean canTransform = false;
   private boolean canRevive = false;
   private boolean vampireState = false;
   private int parentObjectID = 0;
   private String eventName;
   private List<Integer> triggeredOnce = new ArrayList<>();
   private List<Integer> ignoreIntervalSkill = new ArrayList<>();
   private Map<Integer, Integer> skillIndexUsedCount = new HashMap<>();
   private int callOtherSkillID = 0;
   private int callOtherSkillLevel = 0;
   private int foxFlameStack = 0;
   private int foxFlameDebuffer = 0;
   private int foxMischiefStack = 0;
   private long laserStartTime = 0L;
   private long laserControlEndTime = 0L;
   private int laserDirection = 0;
   private int laserAngle = 0;
   private int laserSpeed = 0;
   private int nextLaserValue = 0;
   private int nextLaserDirection = 0;
   private boolean faceLeft = false;
   private boolean isSetAdventurerMark = false;
   private int divineJudgement = 0;
   private long adventurerMarkCancelTime = 0L;
   private List<Integer> dynamicObjects = new ArrayList<>();
   private List<Rect> areaWarnings = new ArrayList<>();
   private boolean selfDestruct = false;
   private boolean blockController;
   private ReentrantReadWriteLock transformLock = new ReentrantReadWriteLock();

   public int getFoxFlameStack() {
      return this.foxFlameStack;
   }

   public void setFoxFlameStack(int foxFlameStack) {
      this.foxFlameStack = foxFlameStack;
   }

   public int getFoxMischiefStack() {
      return this.foxMischiefStack;
   }

   public void setFoxMischiefStack(int foxMischiefStack) {
      this.foxMischiefStack = foxMischiefStack;
   }

   public int getFoxFlameDebuffer() {
      return this.foxFlameDebuffer;
   }

   public void setFoxFlameDebuffer(int chrid) {
      this.foxFlameDebuffer = chrid;
   }

   public MapleMonster(int id, MapleMonsterStats stats) {
      super(id);
      this.eliteMobGrade = EliteGrade.None;
      this.eliteMobType = EliteType.None;
      this.canFreezeTime = 0L;
      this.frozenLinkSerialNumber = 0L;
      this.eliteMonsterSkills = new ArrayList<>();
      this.attackBlocked = new ArrayList<>();
      this.skillFilters = new ArrayList<>();
      this.allowedFsmSkills = new ArrayList<>();
      this.allowedOnetimeFsmSkills = new ArrayList<>();
      this.attackPriority = new HashMap<>();
      this.obstaclePosition = new ArrayList<>();
      this.fireAtRandomAttack = new ArrayList<>();
      this.damageShareUsers = new ArrayList<>();
      this.nextAttackPossible = false;
      this.msc = new ArrayList<>();
      this.relMobZones = new ArrayList<>();
      this.mobZoneDataType = 0;
      this.phase = 0;
      this.rampage = false;
      this.chaseEnd = 0L;
      this.targetFromSvr = 0;
      this.oneTimeForcedAttack = new ArrayList<>();
      this.castingSkillID = 0;
      this.castingSkillLevel = 0;
      this.castingEnd = 0L;
      this.castingCancelDamage = 0L;
      this.castingAction_Success = null;
      this.castingAction_Failed = null;
      this.command = new MobSkillCommand(0, 0, 0);
      this.addDamPartyFrom = 0;
      this.addDamPartyPartyID = 0;
      this.addDamPartyC = 0;
      this.seperateSoulCW = 0;
      this.seperateSoulPW = 0;
      this.seperateSoulPU = 0;
      this.fatalityPartyValue = 0;
      this.fatalityFrom = 0;
      this.fatalityPartyID = 0;
      this.pinpointPierceDebuffX = 0;
      this.suctionBottlePlayerID = 0;
      this.addDamByHealPartyID = 0;
      this.addDamByHealFrom = 0;
      this.addDamByHealC = 0;
      this.resistSkillBySkillId = new HashMap<>();
      this.resistSkill = new HashMap<>();
      this.resistOriginSkillBySkillId = new HashMap<>();
      this.resistOriginSkill = new HashMap<>();
      this.incizingFrom = 0;
      this.incizingPartyID = 0;
      this.incizingPartyValue = 0;
      this.lastUseSkillTime = 0L;
      this.skillForbid = 0L;
      this.setShriekingWallPattern = false;
      this.totalShieldHP = 0L;
      this.shieldHP = 0L;
      this.indieMdrStack = 0;
      this.indieMdrFrom = 0;
      this.blizzardTempestStack = 0;
      this.blizzardTempestEndTime = 0L;
      this.bahamutLightElemAddDamC = 0;
      this.bahamutLightElemAddDamP = 0;
      this.curseMarkAddDamPMdr = 0;
      this.curseMarkAddDamX = 0;
      this.curseMarkAddDamPassiveReason = 0;
      this.timeCurseLevel = 0;
      this.timeCurseX = 0;
      this.multiPMDRC = 0;
      this.mobStatusAddPad = 0;
      this.mobStatusAddMad = 0;
      this.mobStatusAddPdd = 0;
      this.mobStatusAddMdd = 0;
      this.transformBackReference = null;
      this.linkedNextTransformCooltime = 0L;
      this.canTransform = false;
      this.canRevive = false;
      this.vampireState = false;
      this.parentObjectID = 0;
      this.triggeredOnce = new ArrayList<>();
      this.ignoreIntervalSkill = new ArrayList<>();
      this.skillIndexUsedCount = new HashMap<>();
      this.callOtherSkillID = 0;
      this.callOtherSkillLevel = 0;
      this.foxFlameStack = 0;
      this.foxFlameDebuffer = 0;
      this.foxMischiefStack = 0;
      this.laserStartTime = 0L;
      this.laserControlEndTime = 0L;
      this.laserDirection = 0;
      this.laserAngle = 0;
      this.laserSpeed = 0;
      this.nextLaserValue = 0;
      this.nextLaserDirection = 0;
      this.faceLeft = false;
      this.isSetAdventurerMark = false;
      this.divineJudgement = 0;
      this.adventurerMarkCancelTime = 0L;
      this.dynamicObjects = new ArrayList<>();
      this.areaWarnings = new ArrayList<>();
      this.selfDestruct = false;
      this.blockController = false;
      this.transformLock = new ReentrantReadWriteLock();
      this.initWithStats(stats);
      this.makeSkillContext();
   }

   public MapleMonster(MapleMonster monster) {
      super(monster);
      this.eliteMobGrade = EliteGrade.None;
      this.eliteMobType = EliteType.None;
      this.canFreezeTime = 0L;
      this.frozenLinkSerialNumber = 0L;
      this.eliteMonsterSkills = new ArrayList<>();
      this.attackBlocked = new ArrayList<>();
      this.skillFilters = new ArrayList<>();
      this.allowedFsmSkills = new ArrayList<>();
      this.allowedOnetimeFsmSkills = new ArrayList<>();
      this.attackPriority = new HashMap<>();
      this.obstaclePosition = new ArrayList<>();
      this.fireAtRandomAttack = new ArrayList<>();
      this.damageShareUsers = new ArrayList<>();
      this.nextAttackPossible = false;
      this.msc = new ArrayList<>();
      this.relMobZones = new ArrayList<>();
      this.mobZoneDataType = 0;
      this.phase = 0;
      this.rampage = false;
      this.chaseEnd = 0L;
      this.targetFromSvr = 0;
      this.oneTimeForcedAttack = new ArrayList<>();
      this.castingSkillID = 0;
      this.castingSkillLevel = 0;
      this.castingEnd = 0L;
      this.castingCancelDamage = 0L;
      this.castingAction_Success = null;
      this.castingAction_Failed = null;
      this.command = new MobSkillCommand(0, 0, 0);
      this.addDamPartyFrom = 0;
      this.addDamPartyPartyID = 0;
      this.addDamPartyC = 0;
      this.seperateSoulCW = 0;
      this.seperateSoulPW = 0;
      this.seperateSoulPU = 0;
      this.fatalityPartyValue = 0;
      this.fatalityFrom = 0;
      this.fatalityPartyID = 0;
      this.pinpointPierceDebuffX = 0;
      this.suctionBottlePlayerID = 0;
      this.addDamByHealPartyID = 0;
      this.addDamByHealFrom = 0;
      this.addDamByHealC = 0;
      this.resistSkillBySkillId = new HashMap<>();
      this.resistSkill = new HashMap<>();
      this.resistOriginSkillBySkillId = new HashMap<>();
      this.resistOriginSkill = new HashMap<>();
      this.incizingFrom = 0;
      this.incizingPartyID = 0;
      this.incizingPartyValue = 0;
      this.lastUseSkillTime = 0L;
      this.skillForbid = 0L;
      this.setShriekingWallPattern = false;
      this.totalShieldHP = 0L;
      this.shieldHP = 0L;
      this.indieMdrStack = 0;
      this.indieMdrFrom = 0;
      this.blizzardTempestStack = 0;
      this.blizzardTempestEndTime = 0L;
      this.bahamutLightElemAddDamC = 0;
      this.bahamutLightElemAddDamP = 0;
      this.curseMarkAddDamPMdr = 0;
      this.curseMarkAddDamX = 0;
      this.curseMarkAddDamPassiveReason = 0;
      this.timeCurseLevel = 0;
      this.timeCurseX = 0;
      this.multiPMDRC = 0;
      this.mobStatusAddPad = 0;
      this.mobStatusAddMad = 0;
      this.mobStatusAddPdd = 0;
      this.mobStatusAddMdd = 0;
      this.transformBackReference = null;
      this.linkedNextTransformCooltime = 0L;
      this.canTransform = false;
      this.canRevive = false;
      this.vampireState = false;
      this.parentObjectID = 0;
      this.triggeredOnce = new ArrayList<>();
      this.ignoreIntervalSkill = new ArrayList<>();
      this.skillIndexUsedCount = new HashMap<>();
      this.callOtherSkillID = 0;
      this.callOtherSkillLevel = 0;
      this.foxFlameStack = 0;
      this.foxFlameDebuffer = 0;
      this.foxMischiefStack = 0;
      this.laserStartTime = 0L;
      this.laserControlEndTime = 0L;
      this.laserDirection = 0;
      this.laserAngle = 0;
      this.laserSpeed = 0;
      this.nextLaserValue = 0;
      this.nextLaserDirection = 0;
      this.faceLeft = false;
      this.isSetAdventurerMark = false;
      this.divineJudgement = 0;
      this.adventurerMarkCancelTime = 0L;
      this.dynamicObjects = new ArrayList<>();
      this.areaWarnings = new ArrayList<>();
      this.selfDestruct = false;
      this.blockController = false;
      this.transformLock = new ReentrantReadWriteLock();
      this.initWithStats(monster.stats);
   }

   private final void initWithStats(MapleMonsterStats stats) {
      this.setStance(5);
      this.stats = stats;
      this.hp = stats.getHp();
      this.mp = stats.getMp();
      this.maxHp = stats.getMaxHp();
      this.usedSkills = new HashMap<>();
   }

   public final ArrayList<MapleMonster.AttackerEntry> getAttackers() {
      if (this.attackers != null && this.attackers.size() > 0) {
         ArrayList<MapleMonster.AttackerEntry> ret = new ArrayList<>();

         for (MapleMonster.AttackerEntry e : this.attackers) {
            if (e != null) {
               ret.add(e);
            }
         }

         return ret;
      } else {
         return new ArrayList<>();
      }
   }

   public boolean checkTriggeredOnce(Integer idx) {
      if (this.triggeredOnce.contains(idx)) {
         return false;
      } else {
         this.triggeredOnce.add(idx);
         return true;
      }
   }

   public void removeTriggeredOnce(Integer idx) {
      this.triggeredOnce.remove(idx);
   }

   public void addIgnoreIntervalSkill(Integer idx) {
      this.ignoreIntervalSkill.add(idx);
   }

   public boolean checkIgnoreIntervalSkill(Integer idx) {
      return this.ignoreIntervalSkill.contains(idx);
   }

   public void removeIgnoreIntervalSkill(Integer idx) {
      this.ignoreIntervalSkill.remove(idx);
   }

   public void incrementSkillIndexUsed(Integer skillIndex) {
      if (this.skillIndexUsedCount.containsKey(skillIndex)) {
         this.skillIndexUsedCount.put(skillIndex, this.skillIndexUsedCount.get(skillIndex) + 1);
      } else {
         this.skillIndexUsedCount.put(skillIndex, 1);
      }
   }

   public int getSkillIndexUsedCount(Integer skillIndex) {
      Map<Integer, Integer> copy = new HashMap<>(this.skillIndexUsedCount);
      return copy.containsKey(skillIndex) ? copy.get(skillIndex) : 0;
   }

   public long getLastReceivedMovePacket() {
      return this.lastReceivedMovePacket;
   }

   public void receiveMovePacket() {
      this.lastReceivedMovePacket = System.currentTimeMillis();
   }

   public final MapleMonsterStats getStats() {
      return this.stats;
   }

   public final void disableDrops() {
      this.dropsDisabled = true;
   }

   public final boolean dropsDisabled() {
      return this.dropsDisabled;
   }

   public final void setSponge(MapleMonster mob) {
      this.sponge = new WeakReference<>(mob);
      if (this.linkoid <= 0) {
         this.linkoid = mob.getObjectId();
      }
   }

   public final void setMap(Field map) {
      this.map = map;
      this.startDropItemSchedule();
   }

   public final long getHp() {
      return this.hp;
   }

   public final void setHp(long hp) {
      this.hp = hp;
   }

   public final Pair<Long, Long> getHPForDisplay() {
      long chp = this.getHp();
      long mhp = this.getMobMaxHp();
      Pair<Long, Long> display = new Pair<>(chp, mhp);
      if (this.getMobMaxHp() >= 2147483647L) {
         long div = mhp / 2147483647L;
         long div_hp = chp / div;
         long div_mhp = mhp / div;
         display.left = div_hp;
         display.right = div_mhp;
      }

      return display;
   }

   public final ChangeableStats getChangedStats() {
      return this.ostats;
   }

   public final long getMobMaxHp() {
      if (this.ostats != null) {
         return this.ostats.hp;
      } else {
         return this.maxHp > 0L ? this.maxHp : this.stats.getHp();
      }
   }

   public final int getMp() {
      return this.mp;
   }

   public final void setMaxHp(long maxHp) {
      this.maxHp = maxHp;
   }

   public final void setMp(int mp) {
      if (mp < 0) {
         mp = 0;
      }

      this.mp = mp;
   }

   public final int getMobMaxMp() {
      return this.ostats != null ? this.ostats.mp : this.stats.getMp();
   }

   public final long getMobExp() {
      return this.ostats != null ? this.ostats.exp : this.stats.getExp();
   }

   public final void setOverrideStats(OverrideMonsterStats ostats) {
      this.ostats = new ChangeableStats(this.stats, ostats);
      this.hp = ostats.getHp();
      this.mp = ostats.getMp();
   }

   public final void changeCustomStat(ChangeableStats stat) {
      this.ostats = stat;
      this.hp = this.ostats.getHp();
      this.mp = this.ostats.getMp();
   }

   public final void changeLevel(int newLevel) {
      this.changeLevel(newLevel, true);
   }

   public final void changeLevel(int newLevel, boolean pqMob) {
      if (this.stats.isChangeable()) {
         this.ostats = new ChangeableStats(this.stats, newLevel, pqMob);
         this.hp = this.ostats.getHp();
         this.mp = this.ostats.getMp();
      }
   }

   public final MapleMonster getSponge() {
      return this.sponge.get();
   }

   public final void checkMobChangeHP() {
      if (this.getId() == 8930000 || this.getId() == 8930100) {
         if (this.getHPPercent() <= 80) {
            this.removeAttackBlocked(7);
         }

         if (this.getHPPercent() <= 60) {
            if (this.getId() == 8930000) {
               this.removeAttackBlocked(12);
               this.removeAttackBlocked(13);
               this.removeAttackBlocked(14);
               this.removeAttackBlocked(15);
            } else {
               this.removeAttackBlocked(8);
               this.removeAttackBlocked(9);
               this.removeAttackBlocked(10);
               this.removeAttackBlocked(11);
            }
         }

         if (this.getHPPercent() <= 40 && this.getId() == 8930000) {
            this.removeSkillFilter(0);
            this.removeAttackBlocked(8);
            this.removeAttackBlocked(9);
            this.removeAttackBlocked(10);
            this.removeAttackBlocked(11);
         }

         this.broadcastAttackBlocked();
      }
   }

   private boolean isBluePierre() {
      return this.getId() == 8900002 || this.getId() == 8900102;
   }

   private boolean isRedPierre() {
      return this.getId() == 8900001 || this.getId() == 8900101;
   }

   public final void damage(MapleCharacter from, long damage, boolean updateAttackTime) {
      this.damage(from, damage, updateAttackTime, 0);
   }

   public final void damage(final MapleCharacter from, long damage, boolean updateAttackTime, int lastSkill) {
      if (from != null && damage > 0L && this.isAlive()) {
         if (!(from.getMap() instanceof Field_MMRace)) {
            if (!GameConstants.isUnvisibleMob(this.getId())) {
               MapleMonster.AttackerEntry attacker = null;
               if (from.getParty() != null) {
                  attacker = new MapleMonster.PartyAttackerEntry(from.getParty().getId());
               } else {
                  attacker = new MapleMonster.SingleAttackerEntry(from);
               }

               boolean replaced = false;

               for (MapleMonster.AttackerEntry aentry : this.getAttackers()) {
                  if (aentry != null && aentry.equals(attacker)) {
                     attacker = aentry;
                     replaced = true;
                     break;
                  }
               }

               if (!replaced) {
                  this.attackers.add(attacker);
               }

               NumberFormat nf = NumberFormat.getInstance();
               long rDamage = damage;
               if (this.getMap().getId() != 921174100) {
                  if (from.getAddDamage() > 0L) {
                     long addDamage = from.getAddDamage();
                     rDamage = damage + addDamage;
                  }

                  if (from.getAddDamageSin() >= 2) {
                     rDamage *= from.getAddDamageSin();
                  }
               }

               rDamage = Math.max(0L, Math.min(rDamage, this.hp));
               if (ServerConstants.SET_TEST_DAMAGE) {
                  rDamage = ServerConstants.TEST_DAMAGE;
               }

               if (from.isGM()) {
               }

               if ((this.getId() == 8880156 || this.getId() == 8880167 || this.getId() == 8880177)
                     && !from.isCanAttackLucidRewardMob()) {
                  rDamage = 0L;
                  String var26 = from.getName() + "(accountID : " + from.getAccountID()
                        + ")이(가) 루시드 격파 없이 마지막 오르골을 공격함";
               }

               if (this.getId() == 8880518 && !from.isCanAttackBMRewardMob()) {
                  rDamage = 0L;
                  String var27 = from.getName() + "(accountID : " + from.getAccountID()
                        + ")이(가) 검은마법사 격파 없이 창세의 알을 공격함";
               }

               if (from.getBuffedValue(SecondaryStatFlag.CapDebuff) != null) {
                  int reason = from.getSecondaryStat().CapDebuffReason;
                  boolean absorbingAttack = false;
                  if (reason == MobSkillID.CAPDEBUFF_RED.getVal() && this.isRedPierre()) {
                     absorbingAttack = true;
                  }

                  if (reason == MobSkillID.CAPDEBUFF_BLUE.getVal() && this.isBluePierre()) {
                     absorbingAttack = true;
                  }

                  int multiplier = 1;
                  if (this.getId() / 100 == 89000) {
                     multiplier = 2;
                  }

                  if (absorbingAttack) {
                     rDamage = (long) (this.getMobMaxHp() * 0.01);
                     rDamage *= multiplier;
                     if (this.getMobMaxHp() - this.getHp() <= rDamage) {
                        rDamage = this.getMobMaxHp() - this.getHp();
                     }

                     rDamage = -rDamage;
                  }
               }

               attacker.addDamage(from, rDamage, updateAttackTime);
               if (this.getId() == 9101078) {
                  from.setFlameWolfTotalDamage(rDamage);
               }

               if (this.stats.isBoss()) {
                  if (this.isNextAttackPossible()) {
                     this.broadcastAttackBlocked();
                  }

                  this.checkMobChangeHP();
               }

               if (this.getCastingCancelDamage() > 0L) {
                  this.setCastingCancelDamage(Math.max(0L, this.getCastingCancelDamage() - rDamage));
                  if (this.getCastingCancelDamage() <= 0L) {
                     this.stopCastingSkill(false);
                  }
               }

               if (this.stats.getSelfD() != -1) {
                  this.hp = Math.min(this.getMobMaxHp(), this.hp - Math.min(this.getMobMaxHp(), rDamage));
                  if (this.hp > 0L) {
                     if (this.hp < this.stats.getSelfDHp()) {
                        this.map.killMonster(this, from, false, false, this.stats.getSelfD(), lastSkill);
                     } else {
                        for (MapleMonster.AttackerEntry mattacker : this.getAttackers()) {
                           for (MapleMonster.AttackingMapleCharacter cattacker : mattacker.getAttackers()) {
                              if (cattacker.getAttacker().getMap() == from.getMap()) {
                                 cattacker.getAttacker()
                                       .getClient()
                                       .getSession()
                                       .writeAndFlush(MobPacket.showMonsterHP(this.getObjectId(), this.getHPPercent()));
                                 if (System.currentTimeMillis() - cattacker.getLastAttackTime() > 4000L) {
                                    cattacker.setLastAttackTime(System.currentTimeMillis());
                                 }
                              }
                           }
                        }
                     }
                  } else {
                     this.map.killMonster(this, from, true, false, (byte) 1, lastSkill);
                  }
               } else {
                  if (this.sponge.get() != null && this.sponge.get().hp > 0L) {
                     MapleMonster var10000 = this.sponge.get();
                     var10000.hp = var10000.hp - Math.min(this.getMobMaxHp(), rDamage);
                     if (this.sponge.get().hp <= 0L) {
                        this.map.broadcastMessage(
                              MobPacket.showBossHP(this.sponge.get().getId(), -1L, this.sponge.get().getMobMaxHp()));
                        if (this.sponge.get().getMap() instanceof Field_Horntail) {
                           Timer.MapTimer.getInstance().schedule(new Runnable() {
                              @Override
                              public void run() {
                                 MapleMonster.this.map.killAllMonsters(true, from);
                              }
                           }, 3000L);
                        }
                     } else {
                        this.map.broadcastMessage(MobPacket.showBossHP(this.sponge.get()));
                     }
                  }

                  if (this.hp > 0L) {
                     if (this.getId() != 8880303
                           && this.getId() != 8880304
                           && this.getId() != 8880343
                           && this.getId() != 8880344
                           && this.getId() != 8880363
                           && this.getId() != 8880364) {
                        if (this.getId() != 8880301 && this.getId() != 8880341 && this.getId() != 8880361) {
                           if (this.getId() != 8880603
                                 && this.getId() != 8880607
                                 && this.getId() != 8880609
                                 && this.getId() != 8880612
                                 && this.getId() != 8880633
                                 && this.getId() != 8880637
                                 && this.getId() != 8880639
                                 && this.getId() != 8880642) {
                              if (this.getId() != 8880500
                                    && this.getId() != 8880501
                                    && this.getId() != 8880502
                                    && this.getId() != 8880503
                                    && this.getId() != 8880504) {
                                 this.hp = this.hp - Math.min(this.getMobMaxHp(), rDamage);
                              } else if (this.getMap() instanceof Field_BlackMage) {
                                 Field_BlackMage f = (Field_BlackMage) this.getMap();
                                 int mobID = this.getId() != 8880500 && this.getId() != 8880501 ? this.getId()
                                       : 8880505;
                                 MapleMonster m = this.getMap().getMonsterById(mobID);
                                 if (m != null) {
                                    if (m.getShieldHP() > 0L) {
                                       long delta = m.getShieldHP() - rDamage;
                                       if (delta > 0L) {
                                          m.setShieldHP(delta);
                                          f.sendBlackMageShield(m.getObjectId(), m.getShieldPercentage());
                                          if (this.getMap() instanceof Field_BlackMageBattlePhase4) {
                                             long hp = Math.max(0L,
                                                   this.getShieldHP() - from.getTotalDamage4PhaseBoss());
                                             int per = (int) Math.ceil(hp * 100.0 / this.getTotalShieldHP());
                                             f.sendBlackMageShield(from, m.getObjectId(), per);
                                             if (this.getId() == 8880504) {
                                                MapleMonster linked = this.getMap().getMonsterById(8880519);
                                                if (linked == null) {
                                                   linked = MapleLifeFactory.getMonster(8880519);
                                                   this.getMap().spawnMonsterOnGroundBelow(linked, new Point(0, 218));
                                                }

                                                if (linked != null) {
                                                   linked.setShieldHP(m.getShieldHP());
                                                   linked.setTotalShieldHP(m.getTotalShieldHP());
                                                   f.sendBlackMageShield(from, linked.getObjectId(), per);
                                                }
                                             }
                                          }

                                          return;
                                       }

                                       m.setShieldHP(0L);
                                       m.setTotalShieldHP(0L);
                                       f.sendBlackMageShield(m.getObjectId(), 0);

                                       for (MapleCharacter p : this.getMap().getCharactersThreadsafe()) {
                                          p.setTotalDamage4PhaseBoss(0L);
                                       }

                                       if (this.getId() == 8880504) {
                                          MapleMonster linkedx = this.getMap().getMonsterById(8880519);
                                          if (linkedx == null) {
                                             linkedx = MapleLifeFactory.getMonster(8880519);
                                             this.getMap().spawnMonsterOnGroundBelow(linkedx, new Point(0, 218));
                                          }

                                          if (linkedx != null) {
                                             linkedx.setShieldHP(0L);
                                             linkedx.setTotalShieldHP(0L);
                                             f.sendBlackMageShield(m.getObjectId(), 0);
                                          }
                                       }

                                       rDamage += delta;
                                    }

                                    m.setHp(m.getHp() - Math.min(m.getMobMaxHp(), rDamage));
                                    this.map.broadcastMessage(MobPacket.showBossHP(m));
                                    if (this.getId() == 8880504) {
                                       MapleMonster linkedxx = this.getMap().getMonsterById(8880519);
                                       if (linkedxx == null) {
                                          linkedxx = MapleLifeFactory.getMonster(8880519);
                                          this.getMap().spawnMonsterOnGroundBelow(linkedxx, new Point(0, 218));
                                       }

                                       if (linkedxx != null) {
                                          linkedxx.setHp(m.getHp());
                                          linkedxx.setMaxHp(m.getMobMaxHp());
                                          if (linkedxx.ostats != null) {
                                             linkedxx.ostats.hp = m.getMobMaxHp();
                                          }

                                          this.map.broadcastMessage(MobPacket.showBossHP(linkedxx));
                                       }
                                    }

                                    if (m.getHp() <= 0L) {
                                       this.map.killMonster(m, from, true, false, (byte) 1, lastSkill);
                                    }
                                 }

                                 this.getMap().onMobChangeHP(this);
                                 return;
                              }
                           } else if (this.getMap() instanceof Field_SerenPhase2) {
                              Field_SerenPhase2 f = (Field_SerenPhase2) this.getMap();
                              int mobID = 8880602;
                              MapleMonster m = this.getMap().getMonsterById(mobID);
                              if (m == null) {
                                 m = this.getMap().getMonsterById(8880632);
                              }

                              if (m != null) {
                                 if (m.getShieldHP() > 0L) {
                                    long deltax = m.getShieldHP() - rDamage;
                                    if (deltax > 0L) {
                                       m.setShieldHP(deltax);
                                       f.sendSerenShield(this, m);
                                       return;
                                    }

                                    m.setShieldHP(0L);
                                    m.setTotalShieldHP(0L);
                                    f.sendSerenShield(this, m);
                                    rDamage += deltax;
                                 }

                                 m.setHp(m.getHp() - Math.min(m.getMobMaxHp(), rDamage));
                                 this.map.onMobChangeHP(m);
                                 this.map.broadcastMessage(MobPacket.showBossHP(m));
                                 if (m.getHp() <= 0L) {
                                    this.map.killMonster(m, from, true, false, (byte) 1, lastSkill);
                                 }
                              }

                              this.getMap().onMobChangeHP(this);
                              return;
                           }
                        } else if (this.getMap() instanceof Field_WillBattle) {
                           Field_WillBattle fx = (Field_WillBattle) this.getMap();
                           if (fx.getWillHPList().contains(500)) {
                              this.hp = Math.max((long) (this.getMobMaxHp() * 0.001) * 500L, this.hp - rDamage);
                           } else if (fx.getWillHPList().contains(3)) {
                              this.hp = Math.max((long) (this.getMobMaxHp() * 0.001) * 3L, this.hp - rDamage);
                           } else {
                              this.hp = this.hp - Math.min(this.getMobMaxHp(), rDamage);
                           }
                        }
                     } else if (this.getMap() instanceof Field_WillBattle) {
                        Field_WillBattle fx = (Field_WillBattle) this.getMap();
                        if (fx.getWillHPList().contains(666)) {
                           this.hp = Math.max((long) (this.getMobMaxHp() * 0.001) * 666L, this.hp - rDamage);
                        } else if (fx.getWillHPList().contains(333)) {
                           this.hp = Math.max((long) (this.getMobMaxHp() * 0.001) * 333L, this.hp - rDamage);
                        } else if (fx.getWillHPList().contains(3)) {
                           this.hp = Math.max((long) (this.getMobMaxHp() * 0.001) * 3L, this.hp - rDamage);
                        } else {
                           this.hp = this.hp - Math.min(this.getMobMaxHp(), rDamage);
                        }
                     }

                     if (this.getMap() != null) {
                        this.getMap().handleMobHP(this);
                     }

                     if ((this.getId() == 8880302 || this.getId() == 8880342 || this.getId() == 8880362)
                           && from.getBuffedValue(SecondaryStatFlag.WillPoison) != null) {
                        from.setLastWillAttackTime(System.currentTimeMillis());
                     }

                     MobTemporaryStatEffect eff = null;
                     if ((eff = this.getBuff(MobTemporaryStatFlag.SEPERATE_SOUL_P)) != null) {
                        int objectID = eff.getSkillID();
                        MapleMonster mob = this.getMap().getMonsterByOid(objectID);
                        if (mob != null) {
                           mob.setHp(this.hp);
                           this.map.broadcastMessage(MobPacket.showMonsterHP(mob.getObjectId(), mob.getHPPercent()));
                        }

                        if (this.hp <= 0L) {
                           this.getMap().removeMonster(mob);
                        }
                     } else if ((eff = this.getBuff(MobTemporaryStatFlag.SEPERATE_SOUL_C)) != null) {
                        int objectIDx = eff.getSkillID();
                        MapleMonster mobx = this.getMap().getMonsterByOid(objectIDx);
                        if (mobx != null) {
                           mobx.damage(from, damage, true);
                           this.map.broadcastMessage(MobPacket.showMonsterHP(mobx.getObjectId(), mobx.getHPPercent()));
                           if (this.hp <= 0L) {
                              this.getMap().removeMonster(this);
                              return;
                           }
                        }
                     }

                     if ((this.getId() >= 9833101 && this.getId() <= 9833105
                           || this.getId() >= 9833201 && this.getId() <= 9833205) && this.hp <= 0L) {
                        this.hp = 1L;
                     }

                     if (this.eventInstance != null) {
                        this.eventInstance.monsterDamaged(from, this, (int) rDamage);
                     } else {
                        EventInstanceManager em = from.getEventInstance();
                        if (em != null) {
                           em.monsterDamaged(from, this, (int) rDamage);
                        }
                     }

                     switch (this.stats.getHPDisplayType()) {
                        case 0:
                           this.map.broadcastMessage(MobPacket.showBossHP(this), this.getTruePosition());
                           break;
                        case 1:
                           this.map.broadcastMessage(from, MobPacket.damageFriendlyMob(this, damage, true), true);
                           break;
                        case 2:
                           this.map.broadcastMessage(MobPacket.showMonsterHP(this.getObjectId(), this.getHPPercent()));
                           from.mulung_EnergyModify(true);
                           break;
                        case 3:
                           for (MapleMonster.AttackerEntry mattacker : this.getAttackers()) {
                              if (mattacker != null) {
                                 for (MapleMonster.AttackingMapleCharacter cattackerx : mattacker.getAttackers()) {
                                    if (cattackerx != null && cattackerx.getAttacker().getMap() == from.getMap()) {
                                       if (this.getStats() != null && !this.getStats().isBoss()) {
                                          cattackerx.getAttacker()
                                                .getClient()
                                                .getSession()
                                                .writeAndFlush(
                                                      MobPacket.showMonsterHP(this.getObjectId(), this.getHPPercent()));
                                       } else if (System.currentTimeMillis() - cattackerx.getLastAttackTime() > 4000L) {
                                          cattackerx.getAttacker()
                                                .getClient()
                                                .getSession()
                                                .writeAndFlush(
                                                      MobPacket.showMonsterHP(this.getObjectId(), this.getHPPercent()));
                                          cattackerx.setLastAttackTime(System.currentTimeMillis());
                                       }
                                    }
                                 }
                              }
                           }
                     }

                     if (this.getId() == 8880410 || this.getId() == 8880405) {
                        this.map.broadcastMessage(MobPacket.showMonsterHP(this.getObjectId(), this.getHPPercent()));
                     }

                     if (this.map.getId() >= 925070100 && this.map.getId() <= 925078000) {
                        this.map
                              .broadcastMessage(MobPacket.showMonsterHP(this.getObjectId(), this.getHPPercent() * 10));
                     }

                     if (this.hp <= 0L) {
                        if (lastSkill == 400011027) {
                           this.map.broadcastMessage(
                                 MobPacket.mobDieEffectBySkill(this.getObjectId(), 400011027, from.getId()),
                                 this.getTruePosition());
                        }

                        if (this.stats.getHPDisplayType() == 0) {
                           this.map.broadcastMessage(MobPacket.showBossHP(this.getId(), -1L, this.getMobMaxHp()),
                                 this.getTruePosition());
                        }

                        this.map.killMonster(this, from, true, false, (byte) 1, lastSkill);
                        if ((this.map.getId() == 921170050 || this.map.getId() == 921170100)
                              && this.getId() != 9833905) {
                           Timer.MapTimer.getInstance().schedule(new Runnable() {
                              @Override
                              public void run() {
                                 Point pos = MapleMonster.this.getPosition();
                                 switch (MapleMonster.this.getId()) {
                                    case 8642000:
                                    case 8642001:
                                       pos = new Point(105, -354);
                                       break;
                                    case 8642002:
                                    case 8642003:
                                       pos = new Point(2644, -345);
                                       break;
                                    case 8642004:
                                    case 8642005:
                                       pos = new Point(-929, -356);
                                       break;
                                    case 8642006:
                                    case 8642007:
                                       pos = new Point(3778, -343);
                                       break;
                                    case 8642008:
                                    case 8642009:
                                       pos = new Point(-1045, -847);
                                       break;
                                    case 8642010:
                                    case 8642011:
                                       pos = new Point(3935, -841);
                                       break;
                                    case 8642012:
                                    case 8642013:
                                       pos = new Point(1434, -791);
                                       break;
                                    case 8642014:
                                    case 8642015:
                                       pos = new Point(1405, -1637);
                                 }

                                 MapleMonster mob = MapleLifeFactory.getMonster(MapleMonster.this.getId());
                                 if (from.getMap().getId() == MapleMonster.this.map.getId()) {
                                    MapleMonster.this.map.spawnMonsterOnGroundBelow(mob, pos);
                                 }
                              }
                           }, 5000L);
                        }
                     }
                  }
               }

               this.getMap().onMobChangeHP(this);
               this.startDropItemSchedule();
            }
         }
      }
   }

   public int giveComboExpToCharacter(MapleCharacter attacker, int exp) {
      attacker.gainExpMonster(this, exp, false, false, true);
      return exp;
   }

   public int getHPPercent() {
      double multiple = 100.0;
      if (this.getStats() != null && this.getStats().getHpNoticePerNum() != 100) {
         multiple = this.getStats().getHpNoticePerNum();
      }

      return (int) Math.ceil(this.hp * multiple / this.getMobMaxHp());
   }

   public double getHPPercentDouble() {
      double multiple = 100.0;
      if (this.getStats() != null && this.getStats().getHpNoticePerNum() != 100) {
         multiple = this.getStats().getHpNoticePerNum();
      }

      return this.hp * multiple / this.getMobMaxHp();
   }

   public final void heal(long hp, int mp, boolean broadcast) {
      long TotalHP = this.getHp() + hp;
      int TotalMP = this.getMp() + mp;
      if (TotalHP >= this.getMobMaxHp()) {
         this.setHp(this.getMobMaxHp());
      } else {
         this.setHp(TotalHP);
      }

      if (TotalMP >= this.getMp()) {
         this.setMp(this.getMp());
      } else {
         this.setMp(TotalMP);
      }

      this.map.broadcastMessage(MobPacket.healMonster(this.getObjectId(), hp));
      if (this.getStats().getTagColor() != 0) {
         this.map.broadcastMessage(MobPacket.showBossHP(this));
      }

      if (this.sponge.get() != null) {
         this.sponge.get().hp += hp;
      }
   }

   public final void killed() {
      if (this.listener != null) {
         this.listener.monsterKilled();
      }

      this.listener = null;
   }

   private final void giveExpToCharacter(
         MapleCharacter attacker,
         long exp,
         boolean highestDamage,
         int numExpSharers,
         byte pty,
         byte Premium_Bonus_EXP_PERCENT,
         int lastskillID,
         int Buff_Bonus_EXP) {
      if (attacker.getBossMode() != 1) {
         try {
            if (attacker != null && MonsterCollection.mobByName.getOrDefault(this.getStats().getName(), null) != null) {
               MonsterCollection.CollectionMobData cmd = MonsterCollection.mobByName.get(this.getStats().getName());
               int type = cmd.getType();
               int percentage = 0;
               switch (type) {
                  case 0:
                  case 1:
                     percentage = 50;
                     break;
                  case 2:
                     percentage = 5000;
                     break;
                  case 3:
                  case 4:
                  case 5:
                     percentage = 2000;
                     break;
                  case 6:
                     percentage = 0;
               }

               percentage = (int) (percentage + percentage / 100.0 * attacker.getStat().getMonsterCollectionR());
               if (!DBConfig.isGanglim && ServerConstants.dailyEventType == DailyEventType.CubeFever) {
                  percentage = (int) (percentage + 0.2);
               }

               if (SpecialSunday.isActive && new Date().getDay() == 0 && SpecialSunday.activeMcUP) {
                  percentage *= 2;
               }

               boolean typeBool = true;
               if (type == 2) {
                  String eliteName = cmd.getEliteName();

                  for (MobSkill skill : this.getStats().getSkills()) {
                     String elitePrefix = ElitePrefix.getElitePrefixBySkills(skill.getMobSkillID());
                     if (elitePrefix != null && (skill.getMobSkillID() == 211 || skill.getMobSkillID() == 212)) {
                        MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(skill.getMobSkillID(),
                              skill.getLevel());
                        if (mobSkillInfo != null) {
                           for (AffectedOtherSkill dd : mobSkillInfo.getAffectedOtherSkills()) {
                              elitePrefix = ElitePrefix.getElitePrefixBySkills(dd.getAffectedOtherSkillID()) + " "
                                    + elitePrefix;
                           }
                        }

                        if (!eliteName.equals(elitePrefix)) {
                           typeBool = false;
                        }
                        break;
                     }
                  }
               }

               if (Randomizer.nextInt(10000) < percentage && typeBool) {
                  try {
                     if (!MonsterCollection.checkIfMobOnCollection(attacker, cmd)) {
                        MonsterCollection.setMobOnCollection(attacker, cmd);
                     }
                  } catch (Exception var32) {
                     System.out
                           .println(
                                 "몬스터 컬렉션 예외 발생 "
                                       + var32.toString()
                                       + " / 몹 : "
                                       + (this.getStats() != null ? this.getId() + "(" + this.getStats().getName() + ")"
                                             : this.getId() + "")
                                       + " / 맵 : "
                                       + attacker.getMapId());
                     var32.printStackTrace();
                  }
               }
            }

            if (DBConfig.isGanglim) {
               double d = 0.0;
               if (this.getId() == 4110301) {
                  if (attacker.getLevel() < 280) {
                     d = 6.25E-7;
                  } else if (attacker.getLevel() >= 280 && attacker.getLevel() < 295) {
                     d = 1.0E-7;
                  } else if (attacker.getLevel() >= 295 && attacker.getLevel() < 300) {
                     d = 5.0E-8;
                  }

                  exp = (long) (GameConstants.getExpNeededForLevel(attacker.getLevel()) * d);
                  exp = (long) (exp * ServerConstants.expFeverRate);
                  if (attacker.getParty() != null && attacker.getParty().getPartyMemberList().size() > 2) {
                     exp = 0L;
                  }
               } else if (this.getId() == 8120105) {
                  if (attacker.getLevel() < 280) {
                     d = 1.25E-6;
                  } else if (attacker.getLevel() >= 280 && attacker.getLevel() < 295) {
                     d = 2.0E-7;
                  } else if (attacker.getLevel() >= 295 && attacker.getLevel() < 300) {
                     d = 1.0E-7;
                  }

                  exp = (long) (GameConstants.getExpNeededForLevel(attacker.getLevel()) * d);
                  exp = (long) (exp * ServerConstants.expFeverRate);
                  if (attacker.getParty() != null && attacker.getParty().getPartyMemberList().size() > 2) {
                     exp = 0L;
                  }
               } else if (this.getId() >= 9830000 && this.getId() <= 9830018
                     || this.getId() >= 9831000 && this.getId() <= 9831014) {
                  if (attacker.getLevel() < 280) {
                     d = 5.0E-5;
                  } else if (attacker.getLevel() >= 280 && attacker.getLevel() < 295) {
                     d = 8.0E-6;
                  } else if (attacker.getLevel() >= 295 && attacker.getLevel() < 300) {
                     d = 4.0E-6;
                  }

                  exp = (long) (GameConstants.getExpNeededForLevel(attacker.getLevel()) * d);
                  exp = (long) (exp * ServerConstants.expFeverRate);
               }
            }

            double rate = GameServer.getInstance(this.map.getChannel()).getExpRate();
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
            exp = (long) (exp * attacker.getEXPMod());
            exp = (long) (exp * expRate);
            boolean check = true;
            double eliteRate = 1.0;
            if (this.getFrozenLinkSerialNumber() > 0L) {
               exp = (long) (exp * 1.5);
            }

            exp = (long) (exp * eliteRate);
            if (ServerConstants.currentHottimeRate > 1.0) {
               exp = (long) (exp * ServerConstants.currentHottimeRate);
            }

            if (!check) {
               exp = 0L;
            }

            int atkerLevel = attacker.getLevel();
            int msterLevel = this.getStats().getLevel();
            int leveldiff = atkerLevel - msterLevel;
            boolean delta = Math.abs(leveldiff) < 20 || atkerLevel >= 275;
            if (DBConfig.isGanglim) {
               delta = true;
               if (attacker.getLevel() > 300) {
                  double d = 2.5E-5;
                  if (attacker.getLevel() >= 400) {
                     d = 1.0E-5;
                  } else if (attacker.getLevel() >= 450) {
                     d = 5.0E-6;
                  } else if (attacker.getLevel() >= 500) {
                     d = 2.9999999999999997E-6;
                  }

                  exp = (long) (9.223372E18F * d);
                  exp = (long) (exp * ServerConstants.expFeverRate);
                  if (attacker.getParty() != null && attacker.getParty().getPartyMemberList().size() > 1
                        && !this.getStats().isBoss()) {
                     exp = 0L;
                     long currenttime = System.currentTimeMillis();
                     long timediff = currenttime - attacker.getAlertTime();
                     if (timediff >= 10000L) {
                        attacker.setAlertTime(currenttime);
                        attacker.send(
                              CField.UIPacket.sendBigScriptProgressMessage("300레벨 이상 구간에서 파티 사냥 시 경험치 획득량이 크게 감소합니다.",
                                    FontType.NanumGothic, FontColorType.Yellow));
                     }
                  }
               }
            }

            attacker.getStat().getLock().writeLock().lock();

            try {
               if (highestDamage) {
                  if (this.eventInstance != null) {
                     this.eventInstance.monsterKilled(attacker, this);
                  } else {
                     EventInstanceManager em = attacker.getEventInstance();
                     if (em != null) {
                        em.monsterKilled(attacker, this);
                     }
                  }

                  this.highestDamageChar = attacker.getId();
               }

               if (exp > 0L && delta) {
                  attacker.gainExpMonster(this, exp, true, highestDamage, false);
               }
            } finally {
               attacker.getStat().getLock().writeLock().unlock();
            }
         } catch (Exception var33) {
            System.out.println("Monster to Damage Err");
            var33.printStackTrace();
            FileoutputUtil.log("Log_Damage_Except.rtf",
                  "Error executing on Damage. (playerName : " + attacker.getName() + ") " + var33);
         }
      }
   }

   public final int killBy(MapleCharacter killer, int lastSkill) {
      if (this.killed) {
         return 1;
      } else {
         this.killed = true;
         long totalBaseExp = this.getMobExp();
         if (this.getMap().getId() == 993018200) {
            totalBaseExp = (long) (totalBaseExp * 1.5);
         }

         MapleMonster.AttackerEntry highest = null;
         long highdamage = 0L;
         List<MapleMonster.AttackerEntry> list = this.getAttackers();

         for (MapleMonster.AttackerEntry attackEntry : list) {
            if (attackEntry != null && attackEntry.getDamage() > highdamage) {
               highest = attackEntry;
               highdamage = attackEntry.getDamage();
            }
         }

         for (MapleMonster.AttackerEntry attackEntryx : list) {
            if (attackEntryx != null) {
               int baseExp = (int) Math
                     .ceil(totalBaseExp * Math.min(1.0, (double) attackEntryx.getDamage() / this.getMobMaxHp()));
               attackEntryx.killedMob(this.getMap(), baseExp, attackEntryx == highest, lastSkill);
            }
         }

         MapleCharacter controll = this.controller.get();
         if (controll != null) {
            controll.getClient().getSession().writeAndFlush(MobPacket.stopControllingMonster(this.getObjectId()));
            controll.stopControllingMonster(this);
         }

         this.spawnRevives(this.getMap(), false);
         if (this.eventInstance != null) {
            this.eventInstance.unregisterMonster(this);
            this.eventInstance = null;
         }

         if (killer != null && killer.getPyramidSubway() != null) {
            killer.getPyramidSubway().onKill(killer);
         }

         if (killer != null && killer.isBattleRecordOnCalc()) {
            killer.send(CField.setBRMOnUpdateResult());
         }

         this.hp = 0L;
         MapleMonster oldSponge = this.getSponge();
         this.sponge = new WeakReference<>(null);
         if (oldSponge != null && oldSponge.isAlive()) {
            boolean set = true;

            for (MapleMapObject mon : this.map.getAllMonstersThreadsafe()) {
               MapleMonster mons = (MapleMonster) mon;
               if (mons.isAlive()
                     && mons.getObjectId() != oldSponge.getObjectId()
                     && mons.getStats().getLevel() > 1
                     && mons.getObjectId() != this.getObjectId()
                     && (mons.getSponge() == oldSponge || mons.getLinkOid() == oldSponge.getObjectId())) {
                  set = false;
                  break;
               }
            }

            if (set) {
               this.map.killMonster(oldSponge, killer, true, false, (byte) 1);
            }
         }

         this.nodepack = null;
         if (this.stati.size() > 0) {
            List<MobTemporaryStatFlag> statuses = new LinkedList<>(this.stati.keySet());

            for (MobTemporaryStatFlag ms : statuses) {
               this.cancelStatus(ms);
            }

            statuses.clear();
         }

         if (this.poisons.size() > 0) {
            List<MobTemporaryStatEffect> ps = new LinkedList<>();
            this.poisonsLock.readLock().lock();

            try {
               ps.addAll(this.poisons);
            } finally {
               this.poisonsLock.readLock().unlock();
            }

            for (MobTemporaryStatEffect p : ps) {
               this.cancelSingleStatus(p);
            }

            ps.clear();
         }

         this.cancelDropItem();
         int v1 = this.highestDamageChar;
         this.highestDamageChar = 0;
         return v1;
      }
   }

   public final void spawnRevives(Field map, boolean fromMap) {
      List<Integer> toSpawn = this.stats.getRevives();
      if (toSpawn != null && this.getLinkCID() <= 0) {
         MapleMonster spongy = null;
         switch (this.getId()) {
            case 6160003:
            case 8820002:
            case 8820003:
            case 8820004:
            case 8820005:
            case 8820006:
            case 8840000:
            case 8850011:
            case 8880100:
            case 8880101:
            case 8950000:
            case 8950001:
            case 8950002:
            case 9500006:
            case 9500007:
               break;
            case 8810026:
            case 8810130:
            case 8810215:
               List<MapleMonster> mobs = new ArrayList<>();

               for (int ixx : toSpawn) {
                  MapleMonster mob = MapleLifeFactory.getMonster(ixx);
                  mob.setPosition(this.getPosition());
                  if (this.eventInstance != null) {
                     this.eventInstance.registerMonster(mob);
                  }

                  if (this.dropsDisabled()) {
                     mob.disableDrops();
                  }

                  switch (mob.getId()) {
                     case 8810018:
                     case 8810118:
                     case 8810122:
                     case 8810214:
                     case 8820009:
                     case 8820010:
                     case 8820011:
                     case 8820012:
                     case 8820013:
                     case 8820014:
                     case 8820109:
                     case 8820110:
                     case 8820111:
                     case 8820112:
                     case 8820113:
                     case 8820114:
                        spongy = mob;
                        break;
                     default:
                        mobs.add(mob);
                  }
               }

               if (spongy != null && map.getMonsterById(spongy.getId()) == null) {
                  map.spawnMonster(spongy, -2);
                  spongy.setHp(0L);

                  for (MapleMonster ixx : mobs) {
                     map.spawnMonster(ixx, -2);
                     spongy.setHp(spongy.getHp() + ixx.getHp());
                     ixx.setSponge(spongy);
                  }
               }
               break;
            case 8810118:
            case 8810119:
            case 8810120:
            case 8810121:
               for (int ixx : toSpawn) {
                  MapleMonster mobx = MapleLifeFactory.getMonster(ixx);
                  mobx.setPosition(this.getTruePosition());
                  if (this.eventInstance != null) {
                     this.eventInstance.registerMonster(mobx);
                  }

                  if (this.dropsDisabled()) {
                     mobx.disableDrops();
                  }

                  switch (mobx.getId()) {
                     case 8810119:
                     case 8810120:
                     case 8810121:
                     case 8810122:
                        spongy = mobx;
                  }
               }

               if (spongy != null && map.getMonsterById(spongy.getId()) == null) {
                  map.spawnMonster(spongy, -2);

                  for (MapleMonster mon : map.getAllMonstersThreadsafe()) {
                     if (mon.getObjectId() != spongy.getObjectId()
                           && (mon.getSponge() == this || mon.getLinkOid() == this.getObjectId())) {
                        mon.setSponge(spongy);
                     }
                  }
               }
               break;
            case 8820008:
            case 8820108:
               for (int ix : toSpawn) {
                  MapleMonster mob = MapleLifeFactory.getMonster(ix);
                  if (mob != null) {
                     if (this.eventInstance != null) {
                        this.eventInstance.registerMonster(mob);
                     }

                     mob.setPosition(this.getTruePosition());
                     if (this.dropsDisabled()) {
                        mob.disableDrops();
                     }

                     map.spawnMonster(mob, -2);
                     int spongeId = 8820014;
                     if (this.getId() == 8820108) {
                        spongeId = 8820114;
                     }

                     MapleMonster monster = this.getMap().getMonsterById(spongeId);
                     if (monster != null) {
                        mob.setSponge(monster);
                     }
                  }
               }
               break;
            default:
               for (int i : toSpawn) {
                  MapleMonster mob = MapleLifeFactory.getMonster(i);
                  if (mob != null) {
                     if (this.eventInstance != null) {
                        this.eventInstance.registerMonster(mob);
                     }

                     mob.setPosition(this.getTruePosition());
                     mob.setFh(this.getFh());
                     if (this.dropsDisabled()) {
                        mob.disableDrops();
                     }

                     map.spawnRevives(mob, this.getObjectId(), this.getId(), fromMap);
                     if (mob.getId() == 9300216) {
                        map.broadcastMessage(CField.environmentChange("Dojang/clear", 4));
                        map.broadcastMessage(CField.environmentChange("dojang/end/clear", 3));
                     }
                  }
               }
         }
      }
   }

   public final boolean isAlive() {
      return this.hp > 0L;
   }

   public final void setCarnivalTeam(byte team) {
      this.carnivalTeam = team;
   }

   public final byte getCarnivalTeam() {
      return this.carnivalTeam;
   }

   public final MapleCharacter getController() {
      return this.controller == null ? null : this.controller.get();
   }

   public final void setController(MapleCharacter controller) {
      this.controller = new WeakReference<>(controller);
   }

   public final void switchController(MapleCharacter newController, boolean immediateAggro) {
      MapleCharacter controllers = this.getController();
      if (controllers != newController) {
         if (controllers != null) {
            controllers.stopControllingMonster(this);
            controllers.getClient().getSession().writeAndFlush(MobPacket.stopControllingMonster(this.getObjectId()));
            this.sendStatus(controllers.getClient());
         }

         newController.controlMonster(this, immediateAggro);
         this.setController(newController);
         if (immediateAggro) {
            this.setControllerHasAggro(true);
         }
      }
   }

   public final void addListener(MonsterListener listener) {
      this.listener = listener;
   }

   public final boolean isControllerHasAggro() {
      return this.controllerHasAggro;
   }

   public final void setControllerHasAggro(boolean controllerHasAggro) {
      this.controllerHasAggro = controllerHasAggro;
   }

   public final void sendStatus(MapleClient client) {
      if (this.poisons.size() > 0) {
         this.poisonsLock.readLock().lock();

         try {
            client.getSession().writeAndFlush(MobPacket.applyMonsterStatus(0, this, this.poisons));
         } finally {
            this.poisonsLock.readLock().unlock();
         }
      }
   }

   @Override
   public final void sendSpawnData(MapleClient client) {
      if (this.isAlive()) {
         try {
            if (this.getOwner() > 0 && this.getOwner() == client.getPlayer().getId()) {
               client.getSession()
                     .writeAndFlush(MobPacket.spawnMonster(this, this.fake && this.linkCID <= 0 ? -4 : -1, 0));
            } else if (this.getOwner() == 0) {
               client.getSession()
                     .writeAndFlush(MobPacket.spawnMonster(this, this.fake && this.linkCID <= 0 ? -4 : -1, 0));
            }
         } catch (Exception var5) {
            FileoutputUtil.log("Log_Player_Except.rtf", "sendObjectPlacement 중 Monster sendSpawnData 패킷 전송 실패");
            FileoutputUtil.log("Log_Player_Except.rtf", var5.toString());
         }

         try {
            this.sendStatus(client);
         } catch (Exception var4) {
            FileoutputUtil.log("Log_Player_Except.rtf", "sendObjectPlacement 중 Monster sendSpawnData sendStatus 실행 실패");
            FileoutputUtil.log("Log_Player_Except.rtf", var4.toString());
         }

         try {
            if (this.map != null
                  && !this.stats.isEscort()
                  && client.getPlayer() != null
                  && client.getPlayer().getTruePosition().distanceSq(this.getTruePosition()) <= GameConstants
                        .maxViewRangeSq_Half()) {
               this.map.updateMonsterController(this);
            }
         } catch (Exception var3) {
            FileoutputUtil.log("Log_Player_Except.rtf", "sendObjectPlacement 중 Monster sendSpawnData 컨트롤러 업데이트 실패");
            FileoutputUtil.outputFileError("Log_Player_Except.rtf", var3.getCause());
         }
      }
   }

   @Override
   public final void sendDestroyData(MapleClient client) {
      if (this.stats.isEscort() && this.getEventInstance() != null && this.lastNode >= 0) {
         this.map.resetShammos(client);
      } else {
         client.getSession().writeAndFlush(MobPacket.killMonster(this.getObjectId(), 0));
         if (this.getController() != null && client.getPlayer() != null
               && client.getPlayer().getId() == this.getController().getId()) {
            client.getPlayer().stopControllingMonster(this);
         }
      }
   }

   @Override
   public final String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.stats.getName());
      sb.append("(");
      sb.append(this.getId());
      sb.append(") (Level ");
      sb.append(this.stats.getLevel());
      sb.append(") at (X");
      sb.append(this.getTruePosition().x);
      sb.append("/ Y");
      sb.append(this.getTruePosition().y);
      sb.append(") with ");
      sb.append(this.getHp());
      sb.append("/ ");
      sb.append(this.getMobMaxHp());
      sb.append("hp, ");
      sb.append(this.getMp());
      sb.append("/ ");
      sb.append(this.getMobMaxMp());
      sb.append(" mp, oid: ");
      sb.append(this.getObjectId());
      sb.append(", FLS : " + this.getFrozenLinkSerialNumber());
      sb.append(" || Controller : ");
      MapleCharacter chr = this.controller.get();
      sb.append(chr != null ? chr.getName() : "none");
      return sb.toString();
   }

   @Override
   public final MapleMapObjectType getType() {
      return MapleMapObjectType.MONSTER;
   }

   public final EventInstanceManager getEventInstance() {
      return this.eventInstance;
   }

   public final void setEventInstance(EventInstanceManager eventInstance) {
      this.eventInstance = eventInstance;
   }

   public final MapleCharacter getPoisonOwner(int skillID) {
      this.poisonsLock.readLock().lock();

      MapleCharacter var4;
      try {
         Iterator var2 = this.poisons.iterator();

         MobTemporaryStatEffect ps;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            ps = (MobTemporaryStatEffect) var2.next();
         } while (ps == null || ps.getSkillID() != skillID);

         var4 = ps.getPoisonOwner().get();
      } finally {
         this.poisonsLock.readLock().unlock();
      }

      return var4;
   }

   public final int getBurendSizeByPlayerID(int playerID) {
      this.poisonsLock.readLock().lock();
      int count = 0;

      try {
         for (MobTemporaryStatEffect ps : this.poisons) {
            if (ps != null && ps.getFromID() == playerID) {
               count++;
            }
         }
      } finally {
         this.poisonsLock.readLock().unlock();
      }

      return count;
   }

   public final int getBurnedSizeBySkillID(int skillID) {
      this.poisonsLock.readLock().lock();
      int count = 0;

      try {
         for (MobTemporaryStatEffect ps : this.poisons) {
            if (ps != null && ps.getSkillID() == skillID) {
               count++;
            }
         }
      } finally {
         this.poisonsLock.readLock().unlock();
      }

      return count;
   }

   public final int getBurnedSize() {
      this.poisonsLock.readLock().lock();
      int count = 0;

      try {
         for (MobTemporaryStatEffect ps : this.poisons) {
            count++;
         }
      } finally {
         this.poisonsLock.readLock().unlock();
      }

      return count;
   }

   public final int getBurnedSizeBySkillIDWithPlayerID(int skillID, int playerID) {
      this.poisonsLock.readLock().lock();
      int count = 0;

      try {
         for (MobTemporaryStatEffect ps : this.poisons) {
            if (ps != null && ps.getSkillID() == skillID && ps.getFromID() == playerID) {
               count++;
            }
         }
      } finally {
         this.poisonsLock.readLock().unlock();
      }

      return count;
   }

   public final List<MobTemporaryStatEffect> getBurnedEffect(int skillID) {
      List<MobTemporaryStatEffect> ret = new ArrayList<>();
      this.poisonsLock.readLock().lock();

      try {
         for (MobTemporaryStatEffect ps : this.poisons) {
            if (ps != null && ps.getSkillID() == skillID) {
               ret.add(ps);
            }
         }
      } finally {
         this.poisonsLock.readLock().unlock();
      }

      return ret;
   }

   public final int getStatusSourceID(MobTemporaryStatFlag status) {
      if (status != MobTemporaryStatFlag.POISON && status != MobTemporaryStatFlag.BURNED) {
         MobTemporaryStatEffect effect = this.stati.get(status);
         return effect != null ? effect.getSkillID() : -1;
      } else {
         this.poisonsLock.readLock().lock();

         int var4;
         try {
            Iterator effect = this.poisons.iterator();

            MobTemporaryStatEffect ps;
            do {
               if (!effect.hasNext()) {
                  return -1;
               }

               ps = (MobTemporaryStatEffect) effect.next();
            } while (ps == null);

            var4 = ps.getSkillID();
         } finally {
            this.poisonsLock.readLock().unlock();
         }

         return var4;
      }
   }

   public final ElementalEffectiveness getEffectiveness(Element e) {
      return this.stats.getEffectiveness(e);
   }

   public final void applyStatus(MapleCharacter from, MobTemporaryStatEffect status, boolean poison, long duration,
         boolean checkboss, SecondaryStatEffect eff) {
      this.applyStatus(from, status, poison, duration, checkboss, eff, 1);
   }

   public final void applyStatus(
         MapleCharacter from, MobTemporaryStatEffect status, boolean poison, long duration, boolean checkboss,
         SecondaryStatEffect eff, int maxMultiplier) {
      if (from != null && this.isAlive() && this.getLinkCID() <= 0 && this.nextKill <= 0L) {
         if (this.getBuff(MobTemporaryStatFlag.SEPERATE_SOUL_C) == null) {
            Skill skilz = SkillFactory.getSkill(status.getSkillID());
            if (from.getTotalSkillLevel(80002770) > 0 && from.getCooldownLimit(80002770) == 0L) {
               SecondaryStatEffect e = from.getSkillLevelData(80002770);
               from.send(CField.skillCooldown(80002770, e.getCooldown(from)));
               from.addCooldown(80002770, System.currentTimeMillis(), e.getCooldown(from));
               from.temporaryStatSet(80002770, e.getDuration(), SecondaryStatFlag.indieDamR, e.getIndieDamR());
            }

            int statusSkill = status.getSkillID();
            if (duration >= 2000000000L) {
               duration = 5000L;
            }

            MobTemporaryStatFlag stat = status.getStati();
            if (stat != MobTemporaryStatFlag.FREEZE || this.getId() != 8880503 && this.getId() != 8880504
                  && this.getId() != 8644655 && this.getId() != 8644650) {
               if (stat == MobTemporaryStatFlag.FREEZE && this.map != null) {
                  this.map.handleFreeze(this);
               }

               if (this.stats.isBoss()) {
                  if (stat == MobTemporaryStatFlag.STUN) {
                     return;
                  }

                  if (checkboss) {
                     switch (stat) {
                        case SPEED:
                        case NINJA_AMBUSH:
                        case PAD:
                        case POISON:
                        case BURNED:
                        case DARKNESS:
                        case MAGIC_CRASH:
                        case SHOWDOWN:
                           break;
                        default:
                           return;
                     }
                  }

                  if (this.getId() == 8850011 && stat == MobTemporaryStatFlag.MAGIC_CRASH) {
                     return;
                  }
               }

               if (stat != MobTemporaryStatFlag.STUN && stat != MobTemporaryStatFlag.FREEZE
                     || this.getCanFreezeTime() == 0L
                     || this.getCanFreezeTime() >= System.currentTimeMillis()) {
                  if (!this.stats.isFriendly() && !this.isFake()
                        || stat != MobTemporaryStatFlag.STUN
                              && stat != MobTemporaryStatFlag.SPEED
                              && stat != MobTemporaryStatFlag.POISON
                              && stat != MobTemporaryStatFlag.BURNED) {
                     if (stat != MobTemporaryStatFlag.BURNED && stat != MobTemporaryStatFlag.POISON || eff != null) {
                        if (stat == MobTemporaryStatFlag.BURNED) {
                           this.poisonsLock.readLock().lock();

                           try {
                              for (MobTemporaryStatEffect mse : this.poisons) {
                                 if (mse != null
                                       && mse.getSkillID() != 4340012
                                       && (mse.getSkillID() == 14001021
                                             && this.getBurnedSizeBySkillID(14001021) > maxMultiplier
                                             || mse.getSkillID() != 14001021
                                                   && (mse.getSkillID() == eff.getSourceId()
                                                         || mse.getSkillID() == GameConstants
                                                               .getLinkedAranSkill(eff.getSourceId())
                                                         || GameConstants.getLinkedAranSkill(mse.getSkillID()) == eff
                                                               .getSourceId()))) {
                                    return;
                                 }
                              }
                           } finally {
                              this.poisonsLock.readLock().unlock();
                           }
                        }

                        if (this.stati.containsKey(stat)
                              && stat != MobTemporaryStatFlag.BURNED
                              && stat != MobTemporaryStatFlag.SOUL_EXPLOSTION
                              && stat != MobTemporaryStatFlag.ELEMENT_RESET_BY_SUMMON) {
                           this.cancelStatus(stat, true);
                        }

                        if (poison && this.getHp() > 1L && eff != null) {
                           duration = Math.max(duration, (long) (eff.getDOTTime() * 1000));
                        }

                        duration += (int) (duration * 0.01 * from.getStat().dotTime);
                        long aniTime = duration;
                        if (skilz != null) {
                           aniTime = duration + skilz.getAnimationTime();
                        }

                        if (stat != MobTemporaryStatFlag.SOUL_EXPLOSTION) {
                           status.setCancelTask(duration);
                        }

                        status.setDotAnimation((int) duration);
                        status.setDotCount((int) duration / 1000);
                        status.setDotTickDamR(eff.getDotTickDamR());
                        status.setSuperPos(eff.getDotSuperpos());
                        status.setMaxDotTickDamR(eff.getMaxDotTickDamR());
                        if (from.getTotalSkillLevel(12120049) > 0) {
                           SecondaryStatEffect e = SkillFactory.getSkill(12120049)
                                 .getEffect(from.getTotalSkillLevel(12120049));
                           if (e != null) {
                              status.setDotCount(status.getDotCount() * 2);
                              status.setInterval(status.getInterval() / 2);
                           }
                        } else if (this.getBurnedSizeBySkillID(status.getSkillID()) > maxMultiplier) {
                           status.setDotCount(status.getDotCount() * this.getBurnedSizeBySkillID(status.getSkillID()));

                           for (MobTemporaryStatEffect e : this.getBurnedEffect(status.getSkillID())) {
                              if (e != null) {
                                 e.setCancelTask(duration);
                              }
                           }
                        }

                        if (duration != 0L && status.getDuration() == 0) {
                           status.setDuration((int) duration);
                        }

                        if (poison && this.getHp() > 1L) {
                           AtomicLong dam = new AtomicLong(
                                 (long) ((eff.getDOT() + from.getStat().dot
                                       + from.getStat().getDamageIncrease(eff.getSourceId()))
                                       * from.getStat().getCurrentMaxBaseDamage()
                                       / 100.0));
                           if (from.getTotalSkillLevel(12120008) > 0) {
                              SecondaryStatEffect effect = SkillFactory.getSkill(12120008)
                                    .getEffect(from.getTotalSkillLevel(12120008));
                              if (effect != null) {
                                 dam.set(dam.get() + (long) (dam.get() * 0.01) * effect.getY());
                              }
                           }

                           if (from.getTotalSkillLevel(12120050) > 0) {
                              SecondaryStatEffect effect = SkillFactory.getSkill(12120050)
                                    .getEffect(from.getTotalSkillLevel(12120050));
                              if (effect != null) {
                                 dam.set(dam.get() + (long) (dam.get() * 0.01) * effect.getDOT());
                              }
                           }

                           int d = 0;
                           if (from.getTotalSkillLevel(14100026) > 0) {
                              SecondaryStatEffect effect = SkillFactory.getSkill(14100026)
                                    .getEffect(from.getTotalSkillLevel(14100026));
                              if (effect != null) {
                                 d = (int) (d + (long) (dam.get() * 0.01) * effect.getDOT());
                              }
                           }

                           if (from.getTotalSkillLevel(14110028) > 0) {
                              SecondaryStatEffect effect = SkillFactory.getSkill(14110028)
                                    .getEffect(from.getTotalSkillLevel(14110028));
                              if (effect != null) {
                                 d = (int) (d + (long) (dam.get() * 0.01) * effect.getDOT());
                              }
                           }

                           if (from.getTotalSkillLevel(14120007) > 0) {
                              SecondaryStatEffect effect = SkillFactory.getSkill(14120007)
                                    .getEffect(from.getTotalSkillLevel(14120007));
                              if (effect != null) {
                                 d = (int) (d + (long) (dam.get() * 0.01) * effect.getDOT());
                              }
                           }

                           if (d > 0) {
                              dam.set(dam.get() + d);
                           }

                           if (dam.get() > 2147483647L) {
                              status.setX(Integer.MAX_VALUE);
                           } else {
                              status.setX((int) dam.get());
                           }

                           status.setPoisonDamage(dam.get(), from);
                        } else if (statusSkill == 4111003 || statusSkill == 14111001) {
                           status.setValue(status.getStati(), (int) (this.getMobMaxHp() / 50.0 + 0.999));
                           status.setX((int) (this.getMobMaxHp() / 50.0 + 0.999));
                           status.setPoisonDamage(status.getX().intValue(), from);
                        } else if (statusSkill == 4341003) {
                           status.setPoisonDamage(
                                 (int) (eff.getDamage() * from.getStat().getCurrentMaxBaseDamage() / 100.0), from);
                           status.setX((int) (eff.getDamage() * from.getStat().getCurrentMaxBaseDamage() / 100.0));
                        } else if (statusSkill == 4121004 || statusSkill == 4221004) {
                           status.setValue(status.getStati(), Math.min(32767,
                                 (int) (eff.getDamage() * from.getStat().getCurrentMaxBaseDamage() / 100.0)));
                           status.setX(Math.min(32767,
                                 (int) (eff.getDamage() * from.getStat().getCurrentMaxBaseDamage() / 100.0)));
                           int damx = (int) (aniTime / 1000L * status.getX().intValue() / 2L);
                           status.setPoisonDamage(damx, from);
                           if (damx > 0) {
                              if (damx >= this.hp) {
                                 damx = (int) (this.hp - 1L);
                              }

                              this.damage(from, damx, false);
                           }
                        }

                        if (status.getValue() == null) {
                           status.setValue(0);
                        }

                        if (statusSkill == 4121017 && from.getTotalSkillLevel(4120045) > 0) {
                           SecondaryStatEffect ex = SkillFactory.getSkill(4120045)
                                 .getEffect(from.getTotalSkillLevel(4120045));
                           if (ex != null) {
                              status.setX(status.getX() + ex.getX());
                           }
                        }

                        MapleCharacter con = this.getController();
                        if (stat == MobTemporaryStatFlag.BURNED) {
                           this.poisonsLock.writeLock().lock();

                           try {
                              this.poisons.add(status);
                              if (con != null) {
                                 this.map.broadcastMessage(con,
                                       MobPacket.applyMonsterStatus(eff.getSourceId(), this, this.poisons),
                                       this.getTruePosition());
                                 con.getClient().getSession().writeAndFlush(
                                       MobPacket.applyMonsterStatus(eff.getSourceId(), this, this.poisons));
                              } else {
                                 this.map.broadcastMessage(
                                       MobPacket.applyMonsterStatus(eff.getSourceId(), this, this.poisons),
                                       this.getTruePosition());
                              }
                           } finally {
                              this.poisonsLock.writeLock().unlock();
                           }
                        } else {
                           this.stati.put(stat, status);
                           if (stat.isIndie()) {
                              IndieTemporaryStatEntry entry = new IndieTemporaryStatEntry(
                                    status.getSkillID(), 0, (int) duration, status.getX(), System.currentTimeMillis(),
                                    0);
                              if (this.indieTemporaryStats == null) {
                                 this.indieTemporaryStats = new HashMap<>();
                              }

                              if (!this.indieTemporaryStats.isEmpty() && this.indieTemporaryStats.containsKey(stat)) {
                                 boolean exists = false;

                                 for (IndieTemporaryStatEntry ex : this.indieTemporaryStats.get(stat)) {
                                    if (ex.getSkillID() == status.getSkillID()) {
                                       ex.setStatValue(status.getX());
                                       ex.setStartTime(System.currentTimeMillis());
                                       ex.setDuration((int) duration);
                                       exists = true;
                                       break;
                                    }
                                 }

                                 if (!exists) {
                                    List<IndieTemporaryStatEntry> list = this.indieTemporaryStats.get(stat);
                                    entry.setDuration((int) duration);
                                    list.add(entry);
                                 }
                              } else {
                                 List<IndieTemporaryStatEntry> list = new LinkedList<>();
                                 list.add(entry);
                                 this.indieTemporaryStats.put(stat, list);
                              }
                           }

                           if (con != null) {
                              this.map.broadcastMessage(con,
                                    MobPacket.applyMonsterStatus(eff.getSourceId(), this, status),
                                    this.getTruePosition());
                              con.getClient().getSession()
                                    .writeAndFlush(MobPacket.applyMonsterStatus(eff.getSourceId(), this, status));
                           } else {
                              this.map.broadcastMessage(MobPacket.applyMonsterStatus(eff.getSourceId(), this, status),
                                    this.getTruePosition());
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void applyStatus(MobTemporaryStatEffect status) {
      this.applyStatus(status, false);
   }

   public void applyStatus(MobTemporaryStatEffect status, boolean broadcast) {
      if (!this.stati.containsKey(MobTemporaryStatFlag.INVINCIBLE)) {
         if (this.stati.containsKey(status.getStati())
               && (status.getStati() != MobTemporaryStatFlag.SPEED || status.getValue() == null
                     || status.getValue() <= 0)) {
            this.cancelStatus(status.getStati());
         }

         if (status.getValue() == null) {
            status.setValue(0);
         }

         this.stati.put(status.getStati(), status);
         if (broadcast) {
            this.map.broadcastMessage(MobPacket.mobStatSet(status.getSkillID(), this, status));
         }
      }
   }

   public final void dispelSkill(MobSkillInfo skillId) {
      List<MobTemporaryStatFlag> toCancel = new ArrayList<>();

      for (Entry<MobTemporaryStatFlag, MobTemporaryStatEffect> effects : this.stati.entrySet()) {
         MobTemporaryStatEffect mse = effects.getValue();
         if (mse.getMobSkill() != null && mse.getMobSkill().getSkillId() == skillId.getSkillId()) {
            toCancel.add(effects.getKey());
         }
      }

      for (MobTemporaryStatFlag stat : toCancel) {
         this.cancelStatus(stat);
      }
   }

   public final void applyMonsterBuff(
         Map<MobTemporaryStatFlag, MobTemporaryStatEffect> effect, int skillID, long duration, MobSkillInfo skill,
         List<Integer> reflection) {
      if (this.getBuff(MobTemporaryStatFlag.SEPERATE_SOUL_C) == null) {
         for (Entry<MobTemporaryStatFlag, MobTemporaryStatEffect> z : effect.entrySet()) {
            if (!z.getKey().isIndie() && this.stati.containsKey(z.getKey())) {
               this.cancelStatus(z.getKey(), true);
            }

            Integer x = z.getValue().getX();
            if (x == null) {
               x = z.getValue().getValue();
            }

            MobTemporaryStatEffect effectz = new MobTemporaryStatEffect(z.getKey(), x, skillID, skill, skill != null,
                  reflection.size() > 0);
            if (z.getKey() != MobTemporaryStatFlag.SOUL_EXPLOSTION) {
               effectz.setCancelTask(duration);
            }

            effectz.setDuration((int) duration);
            if (z.getValue().getW() != null) {
               effectz.setW(z.getValue().getW());
            }

            if (z.getValue().getC() != null) {
               effectz.setC(z.getValue().getC());
            }

            if (z.getValue().getP() != null) {
               effectz.setP(z.getValue().getP());
            }

            if (z.getValue().getU() != null) {
               effectz.setU(z.getValue().getU());
            }

            if (z.getValue().getValue() == null) {
               effectz.setValue(0);
            }

            this.stati.put(z.getKey(), effectz);
            if (z.getKey().isIndie()) {
               IndieTemporaryStatEntry entry = new IndieTemporaryStatEntry(skillID, 0, (int) duration,
                     z.getValue().getX(), System.currentTimeMillis(), 0);
               if (this.indieTemporaryStats == null) {
                  this.indieTemporaryStats = new HashMap<>();
               }

               if (!this.indieTemporaryStats.isEmpty() && this.indieTemporaryStats.containsKey(z.getKey())) {
                  boolean exists = false;

                  for (IndieTemporaryStatEntry e : this.indieTemporaryStats.get(z.getKey())) {
                     if (e.getSkillID() == skillID) {
                        e.setStatValue(z.getValue().getX());
                        e.setStartTime(System.currentTimeMillis());
                        e.setDuration((int) duration);
                        exists = true;
                        break;
                     }
                  }

                  if (!exists) {
                     List<IndieTemporaryStatEntry> list = this.indieTemporaryStats.get(z.getKey());
                     entry.setDuration((int) duration);
                     list.add(entry);
                  }
               } else {
                  List<IndieTemporaryStatEntry> list = new ArrayList<>();
                  list.add(entry);
                  this.indieTemporaryStats.put(z.getKey(), list);
               }
            }
         }

         MapleCharacter con = this.getController();
         byte[] packet = MobPacket.applyMonsterStatus(skillID, this, effect);
         if (con != null) {
            this.map.broadcastMessage(con, packet, this.getTruePosition());
            con.getClient().getSession().writeAndFlush(packet);
         } else {
            this.map.broadcastMessage(packet, this.getTruePosition());
         }
      }
   }

   public final void setTempEffectiveness(Element e, long milli) {
      this.stats.setEffectiveness(e, ElementalEffectiveness.WEAK);
      Timer.EtcTimer.getInstance().schedule(() -> this.stats.removeEffectiveness(e), milli);
   }

   public final boolean isBuffed(MobTemporaryStatFlag status) {
      return status != MobTemporaryStatFlag.POISON && status != MobTemporaryStatFlag.BURNED
            ? this.stati.containsKey(status)
            : this.poisons.size() > 0 || this.stati.containsKey(status);
   }

   public final MobTemporaryStatEffect getBuff(MobTemporaryStatFlag status) {
      return this.stati.get(status);
   }

   public final MobTemporaryStatEffect getEffectBySkillIDWithPlayerID(int skillID, int playerID) {
      return this.poisons.stream().filter(ts -> ts.getSkillID() == skillID).filter(ts -> ts.getFromID() == playerID)
            .findFirst().orElse(null);
   }

   public final boolean hasSkillBySkillID(int skillID) {
      return this.stati.values().stream().filter(ts -> ts.getSkillID() == skillID).findFirst().orElse(null) != null;
   }

   public final int getStatiSize() {
      return this.stati.size() + (this.poisons.size() > 0 ? 1 : 0);
   }

   public final ArrayList<MobTemporaryStatEffect> getAllBuffs() {
      return new ArrayList<>(this.stati.values());
   }

   public final void setFake(boolean fake) {
      this.fake = fake;
   }

   public final boolean isFake() {
      return this.fake;
   }

   public final Field getMap() {
      return this.map;
   }

   public final List<MobSkill> getSkills() {
      return this.stats.getSkills();
   }

   public final int getSkillIndex(int skillId, int skillLevel) {
      for (MobSkill ms : this.stats.getSkills()) {
         if (ms.getMobSkillID() == skillId && ms.getLevel() == skillLevel) {
            return ms.getIndex();
         }
      }

      return -1;
   }

   public final boolean hasSkill(int skillId, int level) {
      return this.stats.hasSkill(skillId, level);
   }

   public final long getLastSkillUsed(MobSkillInfo mobSkillInfo) {
      return this.usedSkills.containsKey(mobSkillInfo) ? this.usedSkills.get(mobSkillInfo) : 0L;
   }

   public final void setLastSkillUsed(MobSkillInfo mobSkillInfo, long now, long cooltime) {
      if (mobSkillInfo != null && this.usedSkills != null) {
         switch (mobSkillInfo.getSkillId()) {
            case 140:
               this.usedSkills.put(mobSkillInfo, now + cooltime * 2L);
               break;
            case 141:
               this.usedSkills.put(mobSkillInfo, now + cooltime * 2L);
               break;
            default:
               if (mobSkillInfo != null) {
                  this.usedSkills.put(mobSkillInfo, now + cooltime);
               }
         }
      }
   }

   public final Map<MobSkillInfo, Long> getLastSkillUsed() {
      return this.usedSkills;
   }

   public final void copyLastSkillUsed(Map<MobSkillInfo, Long> info) {
      for (Entry<MobSkillInfo, Long> e : info.entrySet()) {
         this.usedSkills.put(e.getKey(), e.getValue());
      }
   }

   public final byte getNoSkills() {
      return this.stats.getNoSkills();
   }

   public final boolean isFirstAttack() {
      return this.stats.isFirstAttack();
   }

   public final int getBuffToGive() {
      return this.stats.getBuffToGive();
   }

   public final List<MobTemporaryStatEffect> getAllBurned() {
      List<MobTemporaryStatEffect> list = new LinkedList<>();
      this.poisonsLock.readLock().lock();

      try {
         for (MobTemporaryStatEffect effect : this.poisons) {
            if (effect.getStati() == MobTemporaryStatFlag.BURNED) {
               list.add(effect);
            }
         }
      } finally {
         this.poisonsLock.readLock().unlock();
      }

      return list;
   }

   public final List<MobTemporaryStatEffect> getAllPoison() {
      List<MobTemporaryStatEffect> list = new LinkedList<>();
      this.poisonsLock.readLock().lock();

      try {
         for (MobTemporaryStatEffect effect : this.poisons) {
            if (effect.getStati() == MobTemporaryStatFlag.POISON || effect.getStati() == MobTemporaryStatFlag.BURNED) {
               list.add(effect);
            }
         }
      } finally {
         this.poisonsLock.readLock().unlock();
      }

      return list;
   }

   public final boolean getMarkOfAssassin() {
      new LinkedList();
      this.poisonsLock.readLock().lock();

      try {
         for (MobTemporaryStatEffect effect : this.poisons) {
            if (effect.getStati() == MobTemporaryStatFlag.BURNED
                  && (effect.getSkillID() == 4120018 || effect.getSkillID() == 4100011)) {
               return true;
            }
         }
      } finally {
         this.poisonsLock.readLock().unlock();
      }

      return false;
   }

   public final void doPoison(MobTemporaryStatEffect status, WeakReference<MapleCharacter> weakChr) {
      if (status.getStati() != MobTemporaryStatFlag.BURNED && status.getStati() != MobTemporaryStatFlag.POISON
            || this.poisons.size() > 0) {
         if (status.getStati() == MobTemporaryStatFlag.BURNED || status.getStati() == MobTemporaryStatFlag.POISON
               || this.stati.containsKey(status.getStati())) {
            if (weakChr != null) {
               long damage = status.getPoisonDamage();
               int dotTickDamR = status.getDotTickDamR();
               if (dotTickDamR > 0) {
                  int d = Math.min(status.getMaxDotTickDamR(), dotTickDamR * status.getDotTickIdx());
                  int dd = (int) (damage * 0.01 * d);
                  damage += dd;
               }

               status.setDotTickIdx(status.getDotTickIdx() + 1);
               boolean shadowWeb = status.getSkillID() == 4111003 || status.getSkillID() == 14111001;
               MapleCharacter chr = weakChr.get();
               boolean cancel = damage <= 0L || chr == null
                     || chr.getMapId() != this.map.getId() && status.getStati() != MobTemporaryStatFlag.BURNED;
               if (damage >= this.hp) {
                  damage = status.getStati() == MobTemporaryStatFlag.BURNED ? this.hp : this.hp - 1L;
                  cancel = !shadowWeb || cancel;
               }

               if (this.getId() == 9500006 || this.getId() == 9500007 || this.getId() == 9500319) {
                  damage = 1000L;
               }

               if (this.getId() == 8880156 || this.getId() == 8880167 || this.getId() == 8880177) {
                  damage = 0L;
               }

               if (this.getId() == 8880518) {
                  damage = 0L;
               }

               if ((!cancel || status.getStati() == MobTemporaryStatFlag.BURNED) && chr != null) {
                  if (status.getSkillID() == 25121006) {
                     SecondaryStatEffect effect = SkillFactory.getSkill(25121006)
                           .getEffect(chr.getSkillLevel(25121006));
                     if (effect != null) {
                        int delta = (int) (damage * 0.01 * effect.getX());
                        chr.addHP(delta, false);
                        HPHeal e = new HPHeal(chr.getId(), delta);
                        chr.send(e.encodeForLocal());
                        chr.getMap().broadcastMessage(chr, e.encodeForRemote(), false);
                     }
                  }

                  if (chr.isBattleRecordOnCalc()) {
                     chr.send(CField.setBRMOnDotDamageInfo(damage, status.getSkillID(), this.getId()));
                  }

                  if (this.getHp() > damage) {
                     long d = Math.min(this.getHp() - 1L, damage);
                     this.damage(chr, d, false);
                     if (shadowWeb) {
                        this.map.broadcastMessage(MobPacket.damageMonster(this.getObjectId(), damage),
                              this.getTruePosition());
                     }
                  }
               }
            }
         }
      }
   }

   public void setElite() {
      this.setElite(null);
   }

   public void setElite(EliteGrade eliteGrade) {
      int level = this.getStats().getLevel();
      EliteMonsterRate emr = EliteMonsterMan.getRandomRate(level);
      ChangeableStats cs = new ChangeableStats(this.getStats());
      EliteMonsterSkill ems = EliteMonsterMan.getByGrade(emr.getGrade());
      this.eliteMobGrade = EliteGrade.get(emr.getGrade());
      if (eliteGrade != null) {
         this.eliteMobGrade = eliteGrade;
      }

      EliteMonsterStats stats = new EliteMonsterStats(this.eliteMobGrade, level);
      cs.hp = this.getStats().getMaxHp() * stats.hpRate;
      cs.exp = cs.exp * stats.expMesoRate;
      cs.speed = 140;
      if (this.getId() == 8644631) {
         cs.hp = (long) (cs.hp * 2.5);
         cs.exp = (long) (cs.exp * 1.2);
      }

      this.setOverrideStats(cs);
      this.eliteMobType = EliteType.Monster;
      this.eliteMonsterSkills.add(ems);
      List<MobSkill> mobSkills = new ArrayList<>();

      for (EliteMonsterSkill ms : this.eliteMonsterSkills) {
         MobSkill mobSkill = new MobSkill();
         mobSkill.setPriority((byte) 0);
         mobSkill.setMobSkillID(ms.getSkill());
         mobSkill.setLevel(ms.getLevel());
         mobSkills.add(mobSkill);
      }

      this.getStats().setSkills(mobSkills);
      this.makeSkillContext();
      this.setScale(200);
   }

   public void copyEliteBoss(MapleMonster mob) {
      this.eliteMobGrade = mob.eliteMobGrade;
      this.eliteMobType = mob.eliteMobType;
      this.eliteMonsterSkills = mob.eliteMonsterSkills;
      this.setScale(mob.getScale());
      List<MobSkill> mobSkills = new ArrayList<>();

      for (EliteMonsterSkill ms : this.eliteMonsterSkills) {
         MobSkill mobSkill = new MobSkill();
         mobSkill.setPriority((byte) 0);
         mobSkill.setMobSkillID(ms.getSkill());
         mobSkill.setLevel(ms.getLevel());
         mobSkills.add(mobSkill);
      }

      this.getStats().setSkills(mobSkills);
      this.makeSkillContext();
      ChangeableStats copy = new ChangeableStats(mob.getStats());
      ChangeableStats cs = new ChangeableStats(this.getStats());
      cs.hp = copy.hp;
      cs.exp = copy.exp;
      cs.speed = copy.speed;
      this.setOverrideStats(cs);
   }

   public void setEliteBoss(long hp, int level) {
      this.eliteMobGrade = EliteGrade.Red;
      this.eliteMobType = EliteType.Boss;
      Pair<EliteMonsterSkill, EliteMonsterSkill> pair = EliteMonsterMan.getTwiceByGrade(2);
      this.eliteMonsterSkills.add(pair.getLeft());
      this.eliteMonsterSkills.add(pair.getRight());
      List<MobSkill> mobSkills = new ArrayList<>();

      for (EliteMonsterSkill ms : this.eliteMonsterSkills) {
         MobSkill mobSkill = new MobSkill();
         mobSkill.setPriority((byte) 0);
         mobSkill.setMobSkillID(ms.getSkill());
         mobSkill.setLevel(ms.getLevel());
         mobSkills.add(mobSkill);
      }

      this.getStats().setSkills(mobSkills);
      this.makeSkillContext();
      this.setScale(100);
      ChangeableStats cs = new ChangeableStats(this.getStats());
      EliteMonsterStats stats = new EliteMonsterStats(this.eliteMobGrade, level);
      cs.hp = hp * stats.hpRate * 8L;
      cs.exp = cs.exp * (stats.expMesoRate * 3);
      cs.speed = 140;
      this.setOverrideStats(cs);
   }

   public EliteGrade getEliteMobGrade() {
      return this.eliteMobGrade;
   }

   public EliteType getEliteMobType() {
      return this.eliteMobType;
   }

   public int getScale() {
      return this.scale;
   }

   public void addEliteMobSkills(PacketEncoder mplew) {
      mplew.writeInt(this.eliteMonsterSkills.size());

      for (EliteMonsterSkill ems : this.eliteMonsterSkills) {
         mplew.writeInt(ems.getSkill());
         mplew.writeInt(ems.getLevel());
      }
   }

   public int getBlizzardTempestStack() {
      return this.blizzardTempestStack;
   }

   public void setBlizzardTempestStack(int blizzardTempestStack) {
      this.blizzardTempestStack = blizzardTempestStack;
   }

   public long getBlizzardTempestEndTime() {
      return this.blizzardTempestEndTime;
   }

   public void setBlizzardTempestEndTime(long blizzardTempestEndTime) {
      this.blizzardTempestEndTime = blizzardTempestEndTime;
   }

   public int getCurseMarkAddDamPMdr() {
      return this.curseMarkAddDamPMdr;
   }

   public void setCurseMarkAddDamPMdr(int curseMarkAddDamPMdr) {
      this.curseMarkAddDamPMdr = curseMarkAddDamPMdr;
   }

   public int getCurseMarkAddDamX() {
      return this.curseMarkAddDamX;
   }

   public void setCurseMarkAddDamX(int curseMarkAddDamX) {
      this.curseMarkAddDamX = curseMarkAddDamX;
   }

   public int getCurseMarkAddDamPassiveReason() {
      return this.curseMarkAddDamPassiveReason;
   }

   public void setCurseMarkAddDamPassiveReason(int curseMarkAddDamPassiveReason) {
      this.curseMarkAddDamPassiveReason = curseMarkAddDamPassiveReason;
   }

   public int getCreateDelay() {
      return this.createDelay;
   }

   public void setCreateDelay(int createDelay) {
      this.createDelay = createDelay;
   }

   public int getTimeCurseX() {
      return this.timeCurseX;
   }

   public void setTimeCurseX(int timeCurseX) {
      this.timeCurseX = timeCurseX;
   }

   public int getTimeCurseLevel() {
      return this.timeCurseLevel;
   }

   public void setTimeCurseLevel(int timeCurseLevel) {
      this.timeCurseLevel = timeCurseLevel;
   }

   public int getParentObjectID() {
      return this.parentObjectID;
   }

   public void setParentObjectID(int parentObjectID) {
      this.parentObjectID = parentObjectID;
   }

   public long getLinkedNextTransformCooltime() {
      return this.linkedNextTransformCooltime;
   }

   public void setLinkedNextTransformCooltime(long linkedNextTransformCooltime) {
      this.linkedNextTransformCooltime = linkedNextTransformCooltime;
   }

   public boolean isCanTransform() {
      return this.canTransform;
   }

   public void setCanTransform(boolean canTransform) {
      this.canTransform = canTransform;
   }

   public boolean isCanRevive() {
      return this.canRevive;
   }

   public void setCanRevive(boolean canRevive) {
      this.canRevive = canRevive;
   }

   public ReentrantReadWriteLock getTransformLock() {
      return this.transformLock;
   }

   public void setTransformLock(ReentrantReadWriteLock transformLock) {
      this.transformLock = transformLock;
   }

   public int getMultiPMDRC() {
      return this.multiPMDRC;
   }

   public void setMultiPMDRC(int multiPMDRC) {
      this.multiPMDRC = multiPMDRC;
   }

   public void setMobStatusAddData(int[] data) {
      this.setMobStatusAddPad(data[0]);
      this.setMobStatusAddMad(data[1]);
      this.setMobStatusAddPdd(data[2]);
      this.setMobStatusAddMdd(data[3]);
   }

   public int getMobStatusAddPad() {
      return this.mobStatusAddPad;
   }

   public void setMobStatusAddPad(int mobStatusAddPad) {
      this.mobStatusAddPad = mobStatusAddPad;
   }

   public int getMobStatusAddMad() {
      return this.mobStatusAddMad;
   }

   public void setMobStatusAddMad(int mobStatusAddMad) {
      this.mobStatusAddMad = mobStatusAddMad;
   }

   public int getMobStatusAddPdd() {
      return this.mobStatusAddPdd;
   }

   public void setMobStatusAddPdd(int mobStatusAddPdd) {
      this.mobStatusAddPdd = mobStatusAddPdd;
   }

   public int getMobStatusAddMdd() {
      return this.mobStatusAddMdd;
   }

   public void setMobStatusAddMdd(int mobStatusAddMdd) {
      this.mobStatusAddMdd = mobStatusAddMdd;
   }

   public boolean isVampireState() {
      return this.vampireState;
   }

   public void setVampireState(boolean vampireState) {
      this.vampireState = vampireState;
   }

   public void setScale(int scale) {
      this.scale = scale;
   }

   public long getLaserControlEndTime() {
      return this.laserControlEndTime;
   }

   public void setLaserControlEndTime(long laserControlEndTime) {
      this.laserControlEndTime = laserControlEndTime;
   }

   public int getLaserDirection() {
      return this.laserDirection;
   }

   public void setLaserDirection(int laserDirection) {
      this.laserDirection = laserDirection;
   }

   public int getLaserAngle() {
      return this.laserAngle;
   }

   public void setLaserAngle(int laserAngle) {
      this.laserAngle = laserAngle;
   }

   public int getLaserSpeed() {
      return this.laserSpeed;
   }

   public void setLaserSpeed(int laserSpeed) {
      this.laserSpeed = laserSpeed;
   }

   public int getNextLaserValue() {
      return this.nextLaserValue;
   }

   public void setNextLaserValue(int nextLaserValue) {
      this.nextLaserValue = nextLaserValue;
   }

   public int getNextLaserDirection() {
      return this.nextLaserDirection;
   }

   public void setNextLaserDirection(int nextLaserDirection) {
      this.nextLaserDirection = nextLaserDirection;
   }

   public boolean isSelfDestruct() {
      return this.selfDestruct;
   }

   public void setSelfDestruct(boolean selfDestruct) {
      this.selfDestruct = selfDestruct;
   }

   public int getCallOtherSkillID() {
      return this.callOtherSkillID;
   }

   public void setCallOtherSkillID(int callOtherSkillID) {
      this.callOtherSkillID = callOtherSkillID;
   }

   public int getCallOtherSkillLevel() {
      return this.callOtherSkillLevel;
   }

   public void setCallOtherSkillLevel(int callOtherSkillLevel) {
      this.callOtherSkillLevel = callOtherSkillLevel;
   }

   public boolean isFaceLeft() {
      return this.faceLeft;
   }

   public void setFaceLeft(boolean faceLeft) {
      this.faceLeft = faceLeft;
   }

   public int getLinkOid() {
      return this.linkoid;
   }

   public void setLinkOid(int lo) {
      this.linkoid = lo;
   }

   public final ConcurrentEnumMap<MobTemporaryStatFlag, MobTemporaryStatEffect> getStati() {
      return this.stati;
   }

   public void addEmpty() {
   }

   public final boolean isStolen() {
      return this.stolen;
   }

   public final void setStolen(boolean s) {
      this.stolen = s;
   }

   public final void handleSteal(MapleCharacter chr) {
      if (!this.stolen && Randomizer.isSuccess(chr.getBuffedEffect(SecondaryStatFlag.Steal).getZ())) {
         Item item = new Item(this.getStats().isBoss() ? 2431850 : 2431835, (short) 1, (short) 1, 0);
         this.map.spawnItemDrop(this, chr, item, this.getTruePosition(), true, false);
         this.stolen = true;
      }
   }

   public final void setLastNode(int lastNode) {
      this.lastNode = lastNode;
   }

   public final int getLastNode() {
      return this.lastNode;
   }

   public final void cancelStatus(List<MobTemporaryStatFlag> sList) {
      Map<MobTemporaryStatFlag, MobTemporaryStatEffect> ts = new HashMap<>();

      for (MobTemporaryStatFlag flag : sList) {
         if (flag != MobTemporaryStatFlag.BURNED && flag != MobTemporaryStatFlag.POISON) {
            MobTemporaryStatEffect mse = this.stati.get(flag);
            ts.put(flag, mse);
         } else {
            for (MobTemporaryStatEffect e : this.poisons) {
               if (e.getStati() == flag) {
                  ts.put(flag, e);
               }
            }
         }
      }

      MapleCharacter con = this.getController();
      if (con != null) {
         this.map.broadcastMessage(con, MobPacket.cancelMonsterStatus(this, ts, con.getId()), this.getTruePosition());
         con.getClient().getSession().writeAndFlush(MobPacket.cancelMonsterStatus(this, ts, con.getId()));
      } else {
         this.map.broadcastMessage(MobPacket.cancelMonsterStatus(this, ts, 0), this.getTruePosition());
      }

      for (MobTemporaryStatFlag flagx : sList) {
         MobTemporaryStatEffect mse = this.stati.get(flagx);
         if (flagx == MobTemporaryStatFlag.BURNED || flagx == MobTemporaryStatFlag.POISON) {
            try {
               this.poisonsLock.readLock().lock();

               for (MobTemporaryStatEffect ex : this.poisons) {
                  ex.cancelPoisonSchedule(this);
               }
            } finally {
               this.poisonsLock.readLock().unlock();
            }
         }

         if (flagx == MobTemporaryStatFlag.MULTI_PMDR) {
            this.setMultiPMDRC(0);
         }

         this.stati.remove(flagx);
         this.poisons.remove(flagx);
         if (flagx.isIndie() && this.indieTemporaryStats != null && this.indieTemporaryStats.size() > 0) {
            IndieTemporaryStatEntry entry = this.getIndieTemporaryStat(flagx, mse.getSkillID());
            if (entry != null) {
               List<IndieTemporaryStatEntry> list = this.indieTemporaryStats.get(flagx);
               if (list != null && list.size() > 0) {
                  list.remove(entry);
               }

               if (list.size() == 0) {
                  this.indieTemporaryStats.remove(flagx);
               }
            }
         }
      }
   }

   public List<IndieTemporaryStatEntry> getIndieTemporaryStats(MobTemporaryStatFlag flag) {
      List<IndieTemporaryStatEntry> list = new LinkedList<>();
      if (this.indieTemporaryStats != null && this.indieTemporaryStats.size() > 0) {
         list = this.indieTemporaryStats.get(flag);
      }

      return list;
   }

   public IndieTemporaryStatEntry getIndieTemporaryStat(MobTemporaryStatFlag flag, int skillID) {
      IndieTemporaryStatEntry ret = null;
      if (this.indieTemporaryStats != null && this.indieTemporaryStats.size() > 0) {
         List<IndieTemporaryStatEntry> list = this.indieTemporaryStats.get(flag);
         if (list != null) {
            for (IndieTemporaryStatEntry e : list) {
               if (e.getSkillID() == skillID) {
                  ret = e;
                  break;
               }
            }
         }
      }

      return ret;
   }

   public final void cancelStatusBySkillIDWithPlayerID(int skillID, int playerID) {
      this.poisons
            .stream()
            .filter(ts -> ts.getSkillID() == skillID)
            .filter(ts -> ts.getFromID() == playerID)
            .collect(Collectors.toList())
            .forEach(ts -> this.cancelSingleStatus(ts));
   }

   public final void cancelStatus(MobTemporaryStatFlag stat) {
      this.cancelStatus(stat, true);
   }

   public final void cancelStatus(MobTemporaryStatFlag stat, boolean send) {
      MobTemporaryStatEffect mse = this.stati.get(stat);
      if (mse != null && this.isAlive()) {
         MapleCharacter con = this.getController();
         Map<MobTemporaryStatFlag, MobTemporaryStatEffect> sList = Collections.singletonMap(stat, null);
         if (stat == MobTemporaryStatFlag.INDIE_MDR) {
            this.setIndieMdrFrom(0);
            this.setIndieMdrStack(0);
         }

         if (stat == MobTemporaryStatFlag.MULTI_PMDR) {
            this.setMultiPMDRC(0);
         }

         if (stat == MobTemporaryStatFlag.ELEMENT_RESET_BY_SUMMON) {
            this.setFoxFlameDebuffer(0);
            this.setFoxFlameStack(0);
            this.setFoxMischiefStack(0);
         }

         if (stat == MobTemporaryStatFlag.TIME_CURSE && this.getMap() instanceof Field_Papulatus) {
            Field_Papulatus f = (Field_Papulatus) this.getMap();
            f.applyTimeCurseByMob(mse.getMobSkill().getSkillLevel());
         }

         if (send) {
            if (con != null) {
               this.map.broadcastMessage(con, MobPacket.cancelMonsterStatus(this, sList, con.getId()),
                     this.getTruePosition());
               con.getClient().getSession().writeAndFlush(MobPacket.cancelMonsterStatus(this, sList, con.getId()));
            } else {
               this.map.broadcastMessage(MobPacket.cancelMonsterStatus(this, sList, 0), this.getTruePosition());
            }
         }

         mse.cancelPoisonSchedule(this);
         this.stati.remove(stat);
         this.poisons.remove(stat);
      }
   }

   public final void cancelSingleStatus(MobTemporaryStatEffect stat) {
      if (stat != null && this.isAlive()) {
         if (stat.getStati() == MobTemporaryStatFlag.SEPERATE_SOUL_P) {
            int objectID = stat.getSkillID();
            MapleMonster mob = this.getMap().getMonsterByOid(objectID);
            if (mob != null) {
               this.map.removeMonster(mob);
            }
         }

         if (stat.getStati() == MobTemporaryStatFlag.DAZZLE) {
            this.map.removeMonster(this);
         }

         if (stat.getStati() != MobTemporaryStatFlag.BURNED) {
            this.cancelStatus(stat.getStati());
         } else {
            if (stat.getStati() == MobTemporaryStatFlag.MULTI_PMDR) {
               this.setMultiPMDRC(0);
            }

            if (stat.getStati() == MobTemporaryStatFlag.TIME_CURSE && this.getMap() instanceof Field_Papulatus) {
               Field_Papulatus f = (Field_Papulatus) this.getMap();
               f.applyTimeCurseByMob(stat.getMobSkill().getSkillLevel());
            }

            if (stat.getSkillID() == 2321001) {
               this.setIndieMdrFrom(0);
               this.setIndieMdrStack(0);
            }

            this.poisonsLock.writeLock().lock();

            try {
               if (this.poisons.contains(stat)) {
                  this.poisons.remove(stat);
                  if (stat.getStati() == MobTemporaryStatFlag.POISON) {
                     this.stati.remove(stat.getStati(), stat);
                  }

                  MapleCharacter con = this.getController();
                  Map<MobTemporaryStatFlag, MobTemporaryStatEffect> sList = Collections.singletonMap(stat.getStati(),
                        stat);
                  if (con != null) {
                     this.map.broadcastMessage(con, MobPacket.cancelMonsterStatus(this, sList, con.getId()),
                           this.getTruePosition());
                     con.getClient().getSession()
                           .writeAndFlush(MobPacket.cancelMonsterStatus(this, sList, con.getId()));
                  } else {
                     this.map.broadcastMessage(MobPacket.cancelMonsterStatus(this, sList, 0), this.getTruePosition());
                  }

                  stat.cancelPoisonSchedule(this);
                  return;
               }
            } finally {
               this.poisonsLock.writeLock().unlock();
            }
         }
      }
   }

   public final void cancelDropItem() {
      this.lastDropTime = 0L;
   }

   public final void startDropItemSchedule() {
      this.cancelDropItem();
      if (this.stats.getDropItemPeriod() > 0 && this.isAlive()) {
         this.shouldDropItem = false;
         this.lastDropTime = System.currentTimeMillis();
      }
   }

   public boolean shouldDrop(long now) {
      return this.lastDropTime > 0L && this.lastDropTime + this.stats.getDropItemPeriod() * 1000 < now;
   }

   public void doDropItem(long now) {
      switch (this.getId()) {
         case 9300061:
            int itemId = 4001101;
            if (this.isAlive() && this.map != null) {
               if (this.shouldDropItem) {
                  this.map.spawnAutoDrop(itemId, this.getTruePosition());
               } else {
                  this.shouldDropItem = true;
               }
            }

            this.lastDropTime = now;
            return;
         default:
            this.cancelDropItem();
      }
   }

   public byte[] getNodePacket() {
      return this.nodepack;
   }

   public void setNodePacket(byte[] np) {
      this.nodepack = np;
   }

   public void registerKill(long next) {
      this.nextKill = System.currentTimeMillis() + next;
   }

   public boolean shouldKill(long now) {
      return this.nextKill > 0L && now > this.nextKill;
   }

   public int getLinkCID() {
      return this.linkCID;
   }

   public void setLinkCID(int lc) {
      this.linkCID = lc;
      if (lc > 0) {
         this.stati.put(MobTemporaryStatFlag.M_COUNTER,
               new MobTemporaryStatEffect(MobTemporaryStatFlag.M_COUNTER, 60000, 30001062, null, false));
      }
   }

   public long getCanFreezeTime() {
      return this.canFreezeTime;
   }

   public void setCanFreezeTime(long canFreezeTime) {
      this.canFreezeTime = canFreezeTime;
   }

   public long getFrozenLinkSerialNumber() {
      return this.frozenLinkSerialNumber;
   }

   public void setFrozenLinkSerialNumber(long frozenLinkSerialNumber) {
      this.frozenLinkSerialNumber = frozenLinkSerialNumber;
   }

   public void startLaserTask() {
   }

   public List<Point> getObstaclePosition() {
      return this.obstaclePosition;
   }

   public void setObstaclePosition(List<Point> obstaclePosition) {
      this.obstaclePosition.clear();

      for (Point pos : obstaclePosition) {
         this.obstaclePosition.add(pos);
      }
   }

   public List<Rect> getFireAtRandomAttack() {
      return this.fireAtRandomAttack;
   }

   public void setFireAtRandomAttack(List<Rect> fireAtRandomAttack) {
      this.fireAtRandomAttack.clear();

      for (Rect pos : fireAtRandomAttack) {
         this.fireAtRandomAttack.add(pos);
      }
   }

   public List<MapleCharacter> getDamageShareUsers() {
      return new ArrayList<>(this.damageShareUsers);
   }

   public void addDamageShareUsers(MapleCharacter player) {
      if (!this.damageShareUsers.contains(player)) {
         this.damageShareUsers.add(player);
      }
   }

   public void removeDamageShareUsers(MapleCharacter player) {
      if (this.damageShareUsers.contains(player)) {
         this.damageShareUsers.remove(player);
      }
   }

   public int getDamageShareUserCount() {
      return this.damageShareUsers == null ? 0 : this.damageShareUsers.size();
   }

   public void clearDamageShareUsers() {
      this.damageShareUsers.clear();
   }

   public void addAttackBlocked(int attackIdx) {
      if (!this.attackBlocked.contains(attackIdx)) {
         this.attackBlocked.add(attackIdx);
      }
   }

   public void removeAttackBlocked(int attackIdx) {
      if (!this.attackBlocked.isEmpty()) {
         int idx = 0;
         boolean find = false;

         for (Integer i : this.attackBlocked) {
            if (i == attackIdx) {
               find = true;
               break;
            }

            idx++;
         }

         if (find) {
            this.attackBlocked.remove(idx);
         }
      }
   }

   public boolean containsAttackBlocked(int attackIdx) {
      return this.attackBlocked.isEmpty() ? false : this.attackBlocked.contains(attackIdx);
   }

   public void broadcastAttackBlocked() {
      this.map.broadcastMessage(MobPacket.sendAttackBlocked(this.getObjectId(), this.attackBlocked));
   }

   public void addOnetimeFsmSkill(int skillIdx) {
      if (!this.allowedOnetimeFsmSkills.contains(skillIdx)) {
         this.allowedOnetimeFsmSkills.add(skillIdx);
      }
   }

   public void removeOnetimeFsmSkill(int skillIdx) {
      if (!this.allowedOnetimeFsmSkills.isEmpty()) {
         int idx = 0;
         boolean find = false;

         for (Integer i : this.allowedOnetimeFsmSkills) {
            if (i == skillIdx) {
               find = true;
               break;
            }

            idx++;
         }

         if (find) {
            this.allowedOnetimeFsmSkills.remove(idx);
         }
      }
   }

   public boolean containsOnetimeFsmSkill(int skillIdx) {
      return this.allowedOnetimeFsmSkills.isEmpty() ? false : this.allowedOnetimeFsmSkills.contains(skillIdx);
   }

   public void addAllowedFsmSkill(int skillIdx) {
      if (!this.allowedFsmSkills.contains(skillIdx)) {
         this.allowedFsmSkills.add(skillIdx);
      }
   }

   public void removeAllowedFsmSkill(int skillIdx) {
      if (!this.allowedFsmSkills.isEmpty()) {
         int idx = 0;
         boolean find = false;

         for (Integer i : this.allowedFsmSkills) {
            if (i == skillIdx) {
               find = true;
               break;
            }

            idx++;
         }

         if (find) {
            this.allowedFsmSkills.remove(idx);
         }
      }
   }

   public boolean containsAllowedFsmSkill(int skillIdx) {
      return this.allowedFsmSkills.isEmpty() ? false : this.allowedFsmSkills.contains(skillIdx);
   }

   public void addSkillFilter(int skillIdx) {
      if (!this.skillFilters.contains(skillIdx)) {
         this.skillFilters.add(skillIdx);
      }
   }

   public void removeSkillFilter(int skillIdx) {
      if (!this.skillFilters.isEmpty()) {
         int idx = 0;
         boolean find = false;

         for (Integer i : this.skillFilters) {
            if (i == skillIdx) {
               find = true;
               break;
            }

            idx++;
         }

         if (find) {
            this.skillFilters.remove(idx);
         }
      }
   }

   public boolean containsSkillFilter(int skillIdx) {
      return this.skillFilters.isEmpty() ? false : this.skillFilters.contains(skillIdx);
   }

   public void addAttackPriority(Integer priority, int attackIdx) {
      List<Integer> l = this.attackPriority.get(priority);
      if (l == null) {
         l = new ArrayList<>();
         this.attackPriority.put(priority, l);
      }

      if (attackIdx != -1) {
         l.add(attackIdx);
      }
   }

   public void removeAttackPriority(Integer priority, Integer attackIdx) {
      List<Integer> l = this.attackPriority.get(priority);
      if (l == null) {
         l = new ArrayList<>();
         this.attackPriority.put(priority, l);
      }

      if (!l.isEmpty()) {
         l.remove(attackIdx);
      }
   }

   public void clearAttackPriority() {
      this.attackPriority.clear();
   }

   public void broadcastAttackPriority() {
      for (MapleCharacter player : this.getMap().getCharactersThreadsafe()) {
         if (player != null) {
            this.sendAttackPriority(player);
         }
      }
   }

   public void sendAttackPriority(MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_ATTACK_PRIORITY.getValue());
      packet.writeInt(this.getObjectId());
      packet.writeInt(this.attackPriority.size());
      this.attackPriority.forEach((key, value) -> {
         packet.writeInt(key);
         packet.writeInt(value.size());
         value.forEach(packet::writeInt);
      });
      player.send(packet.getPacket());
   }

   public boolean isNextAttackPossible() {
      return this.nextAttackPossible;
   }

   public void setNextAttackPossible(boolean nextAttackPossible) {
      this.nextAttackPossible = nextAttackPossible;
   }

   public void makeSkillContext() {
      List<MobSkillContext> ret = new ArrayList<>();
      if (this.stats.getSkills() != null && !this.stats.getSkills().isEmpty()) {
         for (MobSkill s : this.stats.getSkills()) {
            if (s != null) {
               MobSkillContext context = new MobSkillContext();
               context.setSkillID(s.getMobSkillID());
               context.setSkillLevel(s.getLevel());
               context.setSummoned(0);
               ret.add(context);
            }
         }
      }

      this.setMsc(ret);
   }

   public List<MobSkillContext> getMsc() {
      return this.msc;
   }

   public void setMsc(List<MobSkillContext> msc) {
      this.msc = msc;
   }

   public int getDemianDelayedAttackID() {
      return this.demianDelayedAttackID;
   }

   public void setDemianDelayedAttackID(int demianDelayedAttackID) {
      this.demianDelayedAttackID = demianDelayedAttackID;
   }

   public void registerMobZone(int type, int target) {
      this.getRelMobZones().add(new Pair<>(type, target));
      this.sendRegisterMobZone(type, target);
   }

   public void sendRegisterMobZone(int type, int target) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.REGISTER_MOB_ZONE.getValue());
      packet.writeInt(this.getObjectId());
      packet.writeInt(type);
      packet.writeInt(target);
      this.getMap().broadcastMessage(packet.getPacket());
   }

   public List<Pair<Integer, Integer>> getRelMobZones() {
      return this.relMobZones;
   }

   public void removeRelMobZones(int target) {
      this.getRelMobZones().stream().collect(Collectors.toList()).forEach(p -> {
         if (target == p.right) {
            this.relMobZones.remove(p);
         }
      });
   }

   public int getMobZoneDataType() {
      return this.mobZoneDataType;
   }

   public void setMobZoneDataType(int mobZoneDataType) {
      this.mobZoneDataType = mobZoneDataType;
   }

   public int getPhase() {
      return this.phase;
   }

   public void setPhase(int phase) {
      this.phase = phase;
   }

   public long getChaseEnd() {
      return this.chaseEnd;
   }

   public void setChaseEnd(long chaseEnd) {
      this.chaseEnd = chaseEnd;
   }

   public int getTargetFromSvr() {
      return this.targetFromSvr;
   }

   public void setTargetFromSvr(int targetFromSvr) {
      this.targetFromSvr = targetFromSvr;
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SET_NEXT_TARGET_FROM_SVR.getValue());
      packet.writeInt(this.getObjectId());
      packet.writeInt(targetFromSvr);
      this.getMap().broadcastMessage(packet.getPacket());
   }

   public void addOneTimeForcedAttack(int attackCommand) {
      this.oneTimeForcedAttack.add(attackCommand);
   }

   public void removeOneTimeForcedAttack(int attackCommand) {
      this.oneTimeForcedAttack.remove(attackCommand);
   }

   public int removeAndGetOneTimeForcedAttack() {
      if (!this.oneTimeForcedAttack.isEmpty()) {
         int ret = this.oneTimeForcedAttack.get(0);
         this.removeOneTimeForcedAttack(0);
         return ret;
      } else {
         return 0;
      }
   }

   public int getCastingSkillID() {
      return this.castingSkillID;
   }

   public void setCastingSkillID(int castingSkillID) {
      this.castingSkillID = castingSkillID;
   }

   public int getCastingSkillLevel() {
      return this.castingSkillLevel;
   }

   public void setCastingSkillLevel(int castingSkillLevel) {
      this.castingSkillLevel = castingSkillLevel;
   }

   public long getCastingEnd() {
      return this.castingEnd;
   }

   public void setCastingEnd(long castingEnd) {
      this.castingEnd = castingEnd;
   }

   public MobSkillInfo.CastingActionData getCastingAction_Success() {
      return this.castingAction_Success;
   }

   public void setCastingAction_Success(MobSkillInfo.CastingActionData castingAction_Success) {
      this.castingAction_Success = castingAction_Success;
   }

   public MobSkillInfo.CastingActionData getCastingAction_Failed() {
      return this.castingAction_Failed;
   }

   public void setCastingAction_Failed(MobSkillInfo.CastingActionData castingAction_Failed) {
      this.castingAction_Failed = castingAction_Failed;
   }

   public long getCastingCancelDamage() {
      return this.castingCancelDamage;
   }

   public void setCastingCancelDamage(long castingCancelDamage) {
      this.castingCancelDamage = castingCancelDamage;
   }

   public void sendCastingBarStart(int gaugeType, int time, boolean reverseGauge, boolean showUI, boolean unk) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CASTING_BAR_SKILL.getValue());
      packet.writeInt(this.getObjectId());
      packet.write(MobCastingBarSkill.Start.getType());
      packet.writeInt(gaugeType);
      packet.writeInt(time);
      packet.write(reverseGauge);
      packet.write(showUI);
      packet.write(unk);
      this.getMap().broadcastMessage(packet.getPacket());
   }

   public void sendCastingBarEnd_Attack(boolean success, MobMoveAction action, int attackIdx) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CASTING_BAR_SKILL.getValue());
      packet.writeInt(this.getObjectId());
      packet.write(MobCastingBarSkill.End.getType());
      packet.write(success);
      packet.writeInt(action.getType());
      packet.writeInt(attackIdx);
      this.getMap().broadcastMessage(packet.getPacket());
   }

   public void sendCastingBarEnd_Skill(boolean success, MobMoveAction action, int skillID, int skillLevel) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CASTING_BAR_SKILL.getValue());
      packet.writeInt(this.getObjectId());
      packet.write(MobCastingBarSkill.End.getType());
      packet.write(success);
      packet.writeInt(action.getType());
      packet.writeInt(skillID);
      packet.writeInt(skillLevel);
      this.getMap().broadcastMessage(packet.getPacket());
   }

   public void doSkill_CastingBar(int skillID, int skillLevel, long duration, MobSkillInfo.CastingActionData succeed,
         MobSkillInfo.CastingActionData failed) {
      MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(skillID, skillLevel);
      this.setCastingSkillID(skillID);
      this.setCastingSkillLevel(skillLevel);
      this.setCastingEnd(
            System.currentTimeMillis() + Long.parseLong(mobSkillInfo.getMobSkillStats(MobSkillStat.castingTime)));
      this.setCastingAction_Success(succeed);
      this.setCastingAction_Failed(failed);
      this.sendCastingBarStart(skillLevel, Integer.parseInt(mobSkillInfo.getMobSkillStats(MobSkillStat.castingTime)),
            false, false, false);
      if (skillID == 214 && skillLevel == 14) {
         AtomicInteger userCount = new AtomicInteger(0);
         this.getMap().getCharactersThreadsafe().stream().filter(p -> !p.isHidden()).collect(Collectors.toList())
               .forEach(p -> {
                  p.giveDebuff(SecondaryStatFlag.Lapidification, 1, 0, duration, 174, 16);
                  userCount.addAndGet(1);
               });
         long damage = userCount.get()
               * Long.parseLong(mobSkillInfo.getMobSkillStats(MobSkillStat.cancleDamageMultiplier))
               * Long.parseLong(mobSkillInfo.getMobSkillStats(MobSkillStat.cancleDamage));
         if (damage == 0L) {
            damage = 1L;
         }

         this.setCastingCancelDamage(damage);
         Field_Demian map = (Field_Demian) this.getMap();
         map.sendDemianNotice(216, "데미안이 타락한 세계수의 힘을 폭주시키기 전에 큰 피해를 입혀 저지해야 합니다.", -1, 2500);
      }
   }

   public void stopCastingSkill(boolean success) {
      MobSkillInfo.CastingActionData data = !success ? this.castingAction_Failed : this.castingAction_Success;
      if (this.getCastingCancelDamage() > 0L) {
         this.getMap().getCharactersThreadsafe().stream().collect(Collectors.toList()).forEach(p -> p.addHP(-9999999L));
      }

      if (data != null && (data.attackIdx != -1 || data.skillIdx != -1)) {
         if (data.attackIdx != -1) {
            this.sendCastingBarEnd_Attack(success, MobMoveAction.getAction(13 + data.attackIdx), data.attackIdx);
         } else if (data.skillIdx != -1) {
            int skillCommand = this.msc.get(data.skillIdx).getSkillID();
            int skillCommandLevel = this.msc.get(data.skillIdx).getSkillLevel();
            this.command.setSkillCommand(skillCommand);
            this.command.setSkillCommandLevel(skillCommandLevel);
            this.sendCastingBarEnd_Skill(success, MobMoveAction.getAction(30 + data.skillIdx), 0, 0);
         }

         if (Field_Zakum.isReviveZakumArm(this.getId()) && this.getMap() instanceof Field_Zakum) {
            Field_Zakum f = (Field_Zakum) this.getMap();
            int mid = this.getId() - 27;
            MapleMonster mob = f.getZakum();
            if (mob != null) {
               Point pos = mob.getTruePosition();
               MapleMonster m = MapleLifeFactory.getMonster(mid);
               if (m != null) {
                  ChangeableStats cs = new ChangeableStats(m.getStats());
                  cs.hp = 10500000000L;
                  m.getStats().setHp(10500000000L);
                  m.getStats().setMaxHp(10500000000L);
                  m.setOverrideStats(cs);
                  f.spawnMonster(m, pos, 1);
               }
            }
         }

         this.castingAction_Failed = null;
         this.castingAction_Success = null;
         this.castingEnd = 0L;
         this.castingSkillID = 0;
         this.castingSkillLevel = 0;
         this.castingCancelDamage = 0L;
      }
   }

   public MobSkillCommand getCommand() {
      return this.command;
   }

   public void setCommand(MobSkillCommand command) {
      this.command = command;
   }

   public int getAddDamPartyFrom() {
      return this.addDamPartyFrom;
   }

   public void setAddDamPartyFrom(int addDamPartyFrom) {
      this.addDamPartyFrom = addDamPartyFrom;
   }

   public int getAddDamPartyPartyID() {
      return this.addDamPartyPartyID;
   }

   public void setAddDamPartyPartyID(int addDamPartyPartyID) {
      this.addDamPartyPartyID = addDamPartyPartyID;
   }

   public int getAddDamPartyC() {
      return this.addDamPartyC;
   }

   public void setAddDamPartyC(int addDamPartyC) {
      this.addDamPartyC = addDamPartyC;
   }

   public void addResistSkillBySkillID(Integer skillID, long resistTime) {
      this.resistSkillBySkillId.put(skillID, resistTime);
   }

   public long getResistSkillBySkillID(Integer skillID) {
      return this.resistSkillBySkillId.get(skillID);
   }

   public boolean checkResistSkillByID(Integer skillID) {
      AtomicBoolean b = new AtomicBoolean(true);
      List<Integer> removes = new ArrayList<>();

      for (Entry<Integer, Long> resists : this.resistSkillBySkillId.entrySet()) {
         if (Objects.equals(resists.getKey(), skillID)) {
            if (System.currentTimeMillis() < resists.getValue()) {
               b.set(false);
            } else {
               removes.add(resists.getKey());
            }
         }
      }

      new ArrayList<>(removes).forEach(s -> this.resistSkillBySkillId.remove(s));
      return b.get();
   }

   public long getResistSkill(MobTemporaryStatFlag flag) {
      return this.resistSkill.get(flag);
   }

   public void addResistSkill(MobTemporaryStatFlag flag, long resistTime, MapleCharacter player, int skillID) {
      if (player != null) {
         this.getMap().broadcastMessage(MobPacket.monsterResist(this, player, 90, skillID, 1));
      }

      this.resistSkill.put(flag, resistTime);
   }

   public boolean checkResistSkill(MobTemporaryStatFlag flag) {
      AtomicBoolean b = new AtomicBoolean(true);
      List<MobTemporaryStatFlag> removes = new ArrayList<>();
      this.resistSkill.forEach((f, time) -> {
         if (f == flag) {
            if (System.currentTimeMillis() < time) {
               b.set(false);
            } else {
               removes.add(f);
            }
         }
      });
      removes.stream().collect(Collectors.toList()).forEach(f -> this.resistSkill.remove(f));
      return b.get();
   }

   public void addResistOriginSkillBySkillID(Integer skillID, long resistTime) {
      this.resistOriginSkillBySkillId.put(skillID, resistTime);
   }

   public long getResistOriginSkillBySkillID(Integer skillID) {
      return this.resistOriginSkillBySkillId.get(skillID);
   }

   public boolean checkResistOriginSkillByID(Integer skillID) {
      AtomicBoolean b = new AtomicBoolean(true);
      List<Integer> removes = new ArrayList<>();

      for (Entry<Integer, Long> resists : this.resistOriginSkillBySkillId.entrySet()) {
         if (Objects.equals(resists.getKey(), skillID)) {
            if (System.currentTimeMillis() < resists.getValue()) {
               b.set(false);
            } else {
               removes.add(resists.getKey());
            }
         }
      }

      new ArrayList<>(removes).forEach(s -> this.resistOriginSkillBySkillId.remove(s));
      return b.get();
   }

   public long getResistOriginSkill(MobTemporaryStatFlag flag) {
      return this.resistOriginSkill.get(flag);
   }

   public void addResistOriginSkill(MobTemporaryStatFlag flag, long resistTime, MapleCharacter player) {
      if (player != null) {
         this.getMap().broadcastMessage(MobPacket.monsterResistOrigin(this, player, 90));
      }

      this.resistOriginSkill.put(flag, resistTime);
   }

   public boolean checkResistOriginSkill(MobTemporaryStatFlag flag) {
      AtomicBoolean b = new AtomicBoolean(true);
      List<MobTemporaryStatFlag> removes = new ArrayList<>();
      this.resistOriginSkill.forEach((f, time) -> {
         if (f == flag) {
            if (System.currentTimeMillis() < time) {
               b.set(false);
            } else {
               removes.add(f);
            }
         }
      });
      removes.stream().collect(Collectors.toList()).forEach(f -> this.resistOriginSkill.remove(f));
      return b.get();
   }

   public int getSeperateSoulCW() {
      return this.seperateSoulCW;
   }

   public void setSeperateSoulCW(int seperateSoulCW) {
      this.seperateSoulCW = seperateSoulCW;
   }

   public int getSeperateSoulPW() {
      return this.seperateSoulPW;
   }

   public void setSeperateSoulPW(int seperateSoulPW) {
      this.seperateSoulPW = seperateSoulPW;
   }

   public int getSeperateSoulPU() {
      return this.seperateSoulPU;
   }

   public void setSeperateSoulPU(int seperateSoulPU) {
      this.seperateSoulPU = seperateSoulPU;
   }

   public void setSeperateSoulDummy(MapleCharacter player, MapleMonster originalMob, int skillID, int skillLevel,
         SecondaryStatEffect effect) {
      this.setHp(originalMob.getHp());
      this.getStats().setMaxHp(originalMob.getStats().getMaxHp());
      this.setMaxHp(originalMob.getStats().getMaxHp());
      this.setSeperateSoulCW(effect.getY());
      this.applyStatus(
            player,
            new MobTemporaryStatEffect(MobTemporaryStatFlag.SEPERATE_SOUL_C, effect.getX(), originalMob.getObjectId(),
                  null, false),
            false,
            32767L,
            false,
            effect);
   }

   public void setSeperateSoulOriginal(MapleCharacter player, int skillID, int objectID, SecondaryStatEffect effect) {
      this.setSeperateSoulPW(effect.getY());
      this.setSeperateSoulPU(skillID);
      this.applyStatus(
            player,
            new MobTemporaryStatEffect(MobTemporaryStatFlag.SEPERATE_SOUL_P, effect.getX(), objectID, null, false),
            false,
            effect.getDuration(),
            false,
            effect);
   }

   public int getFatalityPartyValue() {
      return this.fatalityPartyValue;
   }

   public void setFatalityPartyValue(int fatalityPartyValue) {
      this.fatalityPartyValue = fatalityPartyValue;
   }

   public int getFatalityFrom() {
      return this.fatalityFrom;
   }

   public void setFatalityFrom(int fatalityFrom) {
      this.fatalityFrom = fatalityFrom;
   }

   public int getFatalityPartyID() {
      return this.fatalityPartyID;
   }

   public void setFatalityPartyID(int fatalityPartyID) {
      this.fatalityPartyID = fatalityPartyID;
   }

   public int getAddDamByHealPartyID() {
      return this.addDamByHealPartyID;
   }

   public void setAddDamByHealPartyID(int addDamByHealPartyID) {
      this.addDamByHealPartyID = addDamByHealPartyID;
   }

   public int getAddDamByHealFrom() {
      return this.addDamByHealFrom;
   }

   public void setAddDamByHealFrom(int addDamByHealFrom) {
      this.addDamByHealFrom = addDamByHealFrom;
   }

   public int getAddDamByHealC() {
      return this.addDamByHealC;
   }

   public void setAddDamByHealC(int addDamByHealC) {
      this.addDamByHealC = addDamByHealC;
   }

   public void onUpdatePerSecond() {
      if (this.getId() == 8220028 && this.getController() != null) {
         int[] itemID = new int[] { 2432391, 2432392, 2432393, 2432394, 2432395, 2432391, 2432392, 2432393, 2432394,
               2432395 };
         int[] prop = new int[] { 40, 10, 40, 10, 10, 50, 20, 50, 20, 10 };
         int[] exp = new int[] { 5, 10, 0, 0, 0, 5, 10, 0, 0, 0 };

         for (int i = 0; i < itemID.length; i++) {
            if (Randomizer.nextInt(100) < prop[i]) {
               long giveExp = 0L;
               if (exp[i] > 0) {
                  List<MapleMonster> mobs = this.getMap().getAllMonstersThreadsafe();
                  Collections.shuffle(mobs);
                  MapleMonster any = mobs.stream().findAny().orElse(null);
                  if (any != null) {
                     giveExp = any.getStats().getExp() * exp[i];
                  }
               }

               int item = itemID[i];
               Item drop = new Item(item, (short) 0, (short) 1, 0);
               this.map.spawnMobDrop(drop, this.getTruePosition(), this, this.getController(), (byte) 0, 0, giveExp);
            }
         }
      }

      if (this.adventurerMarkCancelTime > 0L && this.adventurerMarkCancelTime <= System.currentTimeMillis()) {
         this.adventurerMarkCancelTime = 0L;
         this.setAdventurerMark(false);
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.SET_ADVENTURER_MARK.getValue());
         packet.writeInt(0);
         packet.write(false);
         this.getMap().broadcastMessage(packet.getPacket());
      }

      this.checkLinkMobBuff();
      this.updateLaser();
   }

   public void updateLaser() {
      if (this.getBuff(MobTemporaryStatFlag.LASER) != null) {
         long now = System.currentTimeMillis();
         if (this.laserStartTime == 0L) {
            this.setLaserDirection(1);
            this.setLaserSpeed(1);
            if (this.getMap() instanceof Field_BlackHeavenBoss) {
               Field_BlackHeavenBoss f = (Field_BlackHeavenBoss) this.getMap();
               if (f != null && f.isHellMode()) {
                  this.setNextLaserValue(2);
               }
            }

            this.laserStartTime = now - 1000L;
         }

         if (this.laserStartTime + 1000L <= now) {
            MobTemporaryStatEffect effect = this.getBuff(MobTemporaryStatFlag.LASER);
            if (effect != null) {
               int skillLevel = effect.getMobSkill().getSkillLevel();
               MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(effect.getSkillID(), skillLevel);
               int z = mobSkillInfo.getMobSkillStatsInt(MobSkillStat.z) / 10;
               double gap = (now - this.laserStartTime) / 1000.0;
               this.laserStartTime = now;
               int direction = (this.getLaserDirection() & 1) == 0 ? -1 : 1;
               int delta = (int) (z * this.getLaserSpeed() * gap * direction);
               this.setLaserAngle((360 + this.getLaserAngle() + delta) % 360);
               if (this.getNextLaserValue() == 0) {
                  if (this.getLaserControlEndTime() != 0L && this.getLaserControlEndTime() <= now) {
                     this.setNextLaserDirection((Randomizer.nextInt() & 3) == 3 ? 1 : 0);
                     this.setNextLaserValue(1);
                     this.setLaserControlEndTime(0L);
                  }
               } else if (this.getLaserDirection() == this.getNextLaserDirection()) {
                  if (this.getLaserSpeed() < this.getNextLaserValue()) {
                     this.setLaserSpeed(this.getLaserSpeed() + 1);
                  } else if (this.getLaserSpeed() > this.getNextLaserValue()) {
                     this.setLaserSpeed(this.getLaserSpeed() - 1);
                  } else if (this.getLaserSpeed() == this.getNextLaserValue()) {
                     this.setNextLaserValue(0);
                  }
               } else {
                  this.setLaserSpeed(0);
                  this.setLaserDirection(this.getNextLaserDirection());
               }
            }

            MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.LASER, 1, 223,
                  new MobSkillInfo(223, 5), true);
            e.setValue(this.getLaserSpeed());
            e.setN(this.getLaserSpeed());
            e.setW(this.getLaserDirection());
            e.setU(this.getLaserAngle());
            e.setDuration(5000000);
            this.applyStatus(e);
            this.getMap().broadcastMessage(MobPacket.mobLaserControl(this.getObjectId(), this.getLaserAngle(),
                  this.getLaserSpeed(), this.getLaserDirection()));
         }
      }
   }

   public void tryApplyCurseMark(MapleCharacter player, int attackSkillID) {
      if (GameConstants.isIllium(player.getJob())) {
         int curseMarkSkillID = player.getCurseMarkSkillID();
         if (curseMarkSkillID != 0) {
            int stack = 0;
            SecondaryStatEffect effect = SkillFactory.getSkill(curseMarkSkillID)
                  .getEffect(player.getTotalSkillLevel(curseMarkSkillID));
            if (effect != null) {
               MobTemporaryStatEffect e2 = this.getBuff(MobTemporaryStatFlag.CURSE_MARK);
               if (e2 != null) {
                  stack = e2.getX();
               }

               int max = effect.getX();
               if (stack < max) {
                  stack = Math.min(max, stack + 1);
                  if (attackSkillID == 152121007) {
                     stack = max;
                  }

                  SecondaryStatEffect curseMarkData = SkillFactory.getSkill(152000010).getEffect(stack);
                  if (curseMarkData != null) {
                     MobTemporaryStatEffect eff = new MobTemporaryStatEffect(MobTemporaryStatFlag.CURSE_MARK, stack,
                           curseMarkSkillID, null, false);
                     int totalPMdr = effect.getW() * curseMarkData.getX() + effect.getZ() * curseMarkData.getY()
                           + effect.getY() * curseMarkData.getZ();
                     this.setCurseMarkAddDamPMdr(totalPMdr);
                     this.setCurseMarkAddDamX(stack == max ? 1 : 0);
                     this.setCurseMarkAddDamPassiveReason(curseMarkSkillID);
                     this.applyStatus(player, eff, false, effect.getDuration(), false, effect);
                     this.getMap()
                           .broadcastMessage(MobPacket.mobHitEffectBySkill(this.getObjectId(), curseMarkSkillID));
                  }
               }
            }
         }
      }
   }

   public void broadcastMobPhaseChange() {
      for (MapleCharacter player : this.getMap().getCharactersThreadsafe()) {
         if (player != null) {
            this.sendMobPhaseChange(player);
         }
      }
   }

   public void sendMobPhaseChange(MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_PHASE_CHANGE.getValue());
      packet.writeInt(this.getObjectId());
      packet.writeInt(this.phase);
      packet.write(this.rampage);
      player.send(packet.getPacket());
   }

   public int getPinpointPierceDebuffX() {
      return this.pinpointPierceDebuffX;
   }

   public void setPinpointPierceDebuffX(int pinpointPierceDebuffX) {
      this.pinpointPierceDebuffX = pinpointPierceDebuffX;
   }

   public int getSuctionBottlePlayerID() {
      return this.suctionBottlePlayerID;
   }

   public void setSuctionBottlePlayerID(int suctionBottlePlayerID) {
      this.suctionBottlePlayerID = suctionBottlePlayerID;
   }

   public long getLastUseSkillTime() {
      return this.lastUseSkillTime;
   }

   public void setLastUseSkillTime(long lastUseSkillTime) {
      this.lastUseSkillTime = lastUseSkillTime;
   }

   public long getSkillForbid() {
      return this.skillForbid;
   }

   public void setSkillForbid(long skillForbid) {
      this.skillForbid = skillForbid;
   }

   public boolean isSetShriekingWallPattern() {
      return this.setShriekingWallPattern;
   }

   public void setSetShriekingWallPattern(boolean setShriekingWallPattern) {
      this.setShriekingWallPattern = setShriekingWallPattern;
   }

   public long getTotalShieldHP() {
      return this.totalShieldHP;
   }

   public void setTotalShieldHP(long totalShieldHP) {
      this.totalShieldHP = Math.min(this.getStats().getMaxHp(), totalShieldHP);
   }

   public long getShieldHP() {
      return this.shieldHP;
   }

   public void setShieldHP(long shieldHP) {
      this.shieldHP = Math.min(this.getStats().getMaxHp(), shieldHP);
   }

   public int getShieldPercentage() {
      return (int) Math.ceil(this.getShieldHP() * 100.0 / this.getTotalShieldHP());
   }

   public int getIncizingFrom() {
      return this.incizingFrom;
   }

   public int getIncizingPartyID() {
      return this.incizingPartyID;
   }

   public int getIncizingPartyValue() {
      return this.incizingPartyValue;
   }

   public void setIncizingFrom(int incizingFrom) {
      this.incizingFrom = incizingFrom;
   }

   public void setIncizingPartyID(int incizingPartyID) {
      this.incizingPartyID = incizingPartyID;
   }

   public void setIncizingPartyValue(int incizingPartyValue) {
      this.incizingPartyValue = incizingPartyValue;
   }

   public int getIndieMdrStack() {
      return this.indieMdrStack;
   }

   public void setIndieMdrStack(int indieMdrStack) {
      this.indieMdrStack = indieMdrStack;
   }

   public int getIndieMdrFrom() {
      return this.indieMdrFrom;
   }

   public void setIndieMdrFrom(int indieMdrFrom) {
      this.indieMdrFrom = indieMdrFrom;
   }

   public int getBahamutLightElemAddDamC() {
      return this.bahamutLightElemAddDamC;
   }

   public void setBahamutLightElemAddDamC(int bahamutLightElemAddDamC) {
      this.bahamutLightElemAddDamC = bahamutLightElemAddDamC;
   }

   public int getBahamutLightElemAddDamP() {
      return this.bahamutLightElemAddDamP;
   }

   public void setBahamutLightElemAddDamP(int bahamutLightElemAddDamP) {
      this.bahamutLightElemAddDamP = bahamutLightElemAddDamP;
   }

   public String getEventName() {
      return this.eventName;
   }

   public void setEventName(String eventName) {
      this.eventName = eventName;
   }

   public void checkLinkMobRevive() {
      this.transformLock.writeLock().lock();

      try {
         LinkMobInfo linkMobInfo = this.getStats().getLinkMobInfo();
         if (linkMobInfo != null
               && linkMobInfo.isLinkRevive()
               && linkMobInfo.getHpTriggerOn() >= this.getHPPercent()
               && linkMobInfo.getHpTriggerOff() <= this.getHPPercent()
               && this.getMap().getMonsterById(linkMobInfo.getMob()) == null) {
            int mob = linkMobInfo.getMob();
            Point position = this.getTruePosition();
            MapleMonster newMob = MapleLifeFactory.getMonster(mob);
            if (newMob != null) {
               long hp = (long) (newMob.getStats().getHp() * (linkMobInfo.getReviveHP() * 0.01));
               newMob.setHp(hp);
               newMob.setCanTransform(true);
               newMob.setCanRevive(true);
               newMob.setLinkedNextTransformCooltime(
                     System.currentTimeMillis() + this.getStats().getTransInfo().getTime() * 1000);
               this.getMap().createMonsterWithDelay(newMob, position, -2, this.getStats().getDieDelay() + 300);
            }
         }
      } finally {
         this.transformLock.writeLock().unlock();
      }
   }

   public void checkLinkMobBuff() {
      LinkMobInfo linkMobInfo = this.getStats().getLinkMobInfo();
      if (linkMobInfo != null) {
         if (this.getHp() <= 0L || this.getBuff(MobTemporaryStatFlag.SEPERATE_SOUL_C) != null) {
            return;
         }

         if (this.getHPPercent() >= linkMobInfo.getHpTriggerOff()
               && this.getHPPercent() <= linkMobInfo.getHpTriggerOn()) {
            MapleMonster linkTarget = this.getMap().getMonsterById(linkMobInfo.getMob());
            if (linkTarget != null
                  && linkTarget.getTruePosition().distance(this.getTruePosition()) <= linkMobInfo.getRange()
                  && linkMobInfo.getSkill() > 0) {
               MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(linkMobInfo.getSkill(), linkMobInfo.getLevel());
               if (mobSkillInfo != null) {
                  mobSkillInfo.applyEffect(this.getController(), linkTarget, null, true);
               }
            }
         }
      }
   }

   public void checkTransform() {
      this.transformLock.writeLock().lock();

      try {
         long now = System.currentTimeMillis();
         if (this.getHp() <= 0L || this.getBuff(MobTemporaryStatFlag.SEPERATE_SOUL_C) != null) {
            return;
         }

         TransInfo transInfo = this.getStats().getTransInfo();
         if (this.transformBackReference != null) {
            if (this.transformBackReference.getNextTransformBack() <= now) {
               this.doTransformBack();
            }

            return;
         }

         if (transInfo == null) {
            return;
         }

         if (this.getLinkedNextTransformCooltime() > now) {
            return;
         }

         if (transInfo.getHpTriggerOn() < this.getHPPercent() || transInfo.getHpTriggerOff() > this.getHPPercent()) {
            return;
         }

         if (this.transformBackReference == null || this.transformBackReference.getNextTransformBack() <= now) {
            List<Integer> targets = new ArrayList<>(transInfo.getTargets());
            Collections.shuffle(targets);
            int transTemplateID = targets.stream().findAny().orElse(0);
            if (transTemplateID != 0 && !transInfo.getSkillInfos().isEmpty()) {
               TransInfo.SkillInfo skill = transInfo.getSkillInfos()
                     .get(Randomizer.rand(0, transInfo.getSkillInfos().size() - 1));
               if (skill != null && this.getController() != null) {
                  MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(skill.getSkill(), skill.getLevel());
                  if (mobSkillInfo != null) {
                     mobSkillInfo.applyEffect(this.getController(), this, null, true);
                  }
               }

               TransformBackReference tr = new TransformBackReference(
                     this.getId(),
                     transInfo.isLinkHP(),
                     transInfo.getHpTriggerOn(),
                     transInfo.getHpTriggerOff(),
                     System.currentTimeMillis() + 14000L,
                     System.currentTimeMillis() + 7000L);
               MapleMonster newMob = MapleLifeFactory.getMonster(transTemplateID);
               if (newMob != null) {
                  Point position = this.getTruePosition();
                  if (transInfo.isLinkHP()) {
                     newMob.setHp(this.getHp());
                  }

                  newMob.setTransformBackReference(tr);
                  newMob.setCanTransform(true);
                  newMob.setCanRevive(this.isCanRevive());
                  this.getMap().removeMonster(this, 0);
                  this.getMap().createMonsterWithDelay(newMob, position, -2, this.getStats().getDieDelay() + 300);
                  this.getMap().setCheckLinkMobRevive(true);
                  return;
               }
            }

            return;
         }
      } finally {
         this.transformLock.writeLock().unlock();
      }
   }

   public void doTransformBack() {
      TransformBackReference tbr = this.transformBackReference;
      int parentTemplateID = tbr.getParentTemplateID();
      Point position = this.getTruePosition();
      MapleMonster newMob = MapleLifeFactory.getMonster(parentTemplateID);
      if (newMob != null) {
         if (tbr.isLinkHP()) {
            newMob.setHp(this.getHp());
         }

         newMob.setLinkedNextTransformCooltime(tbr.getNextTransformCooltime());
         newMob.setCanTransform(true);
         newMob.setCanRevive(this.isCanRevive());
         this.getMap().removeMonster(this, 0);
         this.getMap().createMonsterWithDelay(newMob, position, -2, this.getStats().getDieDelay() + 300);
         this.getMap().setCheckLinkMobRevive(true);
      }
   }

   public TransformBackReference getTransformBackReference() {
      return this.transformBackReference;
   }

   public void setTransformBackReference(TransformBackReference tbr) {
      this.transformBackReference = tbr;
   }

   public void doPassiveSkill() {
      List<PassiveInfo> list = this.getStats().getPassiveInfos();
      if (list != null) {
         for (PassiveInfo passiveInfo : list) {
            if (passiveInfo != null && this.getController() != null) {
               MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(passiveInfo.getSkill(), passiveInfo.getLevel());
               if (mobSkillInfo != null) {
                  mobSkillInfo.applyEffect(this.getController(), this, null, true);
               }
            }
         }
      }
   }

   public void applyTimeZone(Field field, int level) {
      EventManager em = GameServer.getInstance(this.map.getChannel()).getEventSM().getEventManager("RootAbyssVonbon");
      if (em != null) {
         EventInstanceManager eim = null;

         for (EventInstanceManager e : em.getInstances()) {
            if (e != null) {
               eim = e;
               break;
            }
         }

         if (eim != null) {
            int time = 0;
            short var8;
            if (level == 1) {
               var8 = 5000;
            } else {
               var8 = -5000;
            }

            eim.addTimer(var8);
         }
      }
   }

   public void onVonbonBreakDownTimeZone() {
      EventManager em = GameServer.getInstance(this.map.getChannel()).getEventSM().getEventManager("RootAbyssVonbon");
      if (em != null) {
         EventInstanceManager eim1 = null;

         for (EventInstanceManager e : em.getInstances()) {
            if (e != null) {
               eim1 = e;
               break;
            }
         }
         final EventInstanceManager eim = eim1;
         if (eim != null) {
            int timerGauge = 20000;
            eim.setFieldSetTimerGauge(
                  timerGauge / 1000,
                  new Consumer<Boolean>() {
                     public void accept(Boolean success) {
                        MapleMonster.this.getMap().setObjectDisabled("Pt", true);
                        if (success) {
                           eim.broadcastPacket(CWvsContext.getScriptProgressMessage("시공간 붕괴 실패! 잠시 후, 원래 세계로 돌아갑니다."));
                           eim.broadcastResetTimerGuage();

                           for (MapleCharacter player : eim.getPlayers()) {
                              if (player.getMapId() == 105200520) {
                                 player.setRegisterTransferField(player.getMapId() - 10);
                                 player.setRegisterTransferFieldTime(System.currentTimeMillis() + 5000L);
                              }
                           }

                           int remain = (int) (eim.getFieldSetTimerGuageEndTime() - System.currentTimeMillis());
                           Map<MobTemporaryStatFlag, MobTemporaryStatEffect> stats = new EnumMap<>(
                                 MobTemporaryStatFlag.class);
                           MobSkillInfo skill = new MobSkillInfo(MobSkillID.BREAKDOWN_TIMEZONE.getVal(), 1);
                           stats.put(
                                 MobTemporaryStatFlag.STUN,
                                 new MobTemporaryStatEffect(MobTemporaryStatFlag.STUN, 1,
                                       MobSkillID.BREAKDOWN_TIMEZONE.getVal(), skill, true));
                           MapleMonster.this.cancelStatus(MobTemporaryStatFlag.INVINCIBLE);
                           MapleMonster.this.applyMonsterBuff(stats, MobSkillID.BREAKDOWN_TIMEZONE.getVal(), remain,
                                 skill, Collections.EMPTY_LIST);
                        } else {
                           PacketEncoder packet = new PacketEncoder();
                           packet.writeShort(SendPacketOpcode.BREAK_DOWN_TIME_ZONE_TIME_OUT.getValue());
                           packet.writeInt(MapleMonster.this.getObjectId());
                           eim.broadcastPacket(packet.getPacket());
                           packet = new PacketEncoder();
                           packet.writeShort(SendPacketOpcode.SET_DEAD.getValue());
                           packet.write(1);
                           packet.writeInt(0);
                           eim.broadcastPacket(packet.getPacket());
                        }
                     }
                  });
            eim.broadcastTimerGauge();
         }
      }
   }

   public void addDynamicObject(int index) {
      this.dynamicObjects.add(index);
   }

   public List<Integer> getDynamicObjects() {
      return this.dynamicObjects;
   }

   public void clearDynamicObjects() {
      this.dynamicObjects.clear();
   }

   public void addAreaWarning(Rect rect) {
      this.areaWarnings.add(rect);
   }

   public List<Rect> getAreaWarnings() {
      return this.areaWarnings;
   }

   public void clearAreaWarnings() {
      this.areaWarnings.clear();
   }

   public boolean isAdventurerMarkSet() {
      return this.isSetAdventurerMark;
   }

   public void setAdventurerMark(boolean isSetAdventurerMark) {
      this.isSetAdventurerMark = isSetAdventurerMark;
   }

   public void setAdventurerMarkCancelTime(long time) {
      this.adventurerMarkCancelTime = time;
   }

   public int getDivineJudgement() {
      return this.divineJudgement;
   }

   public void setDivineJudgement(int a) {
      this.divineJudgement = a;
   }

   public int getOwner() {
      return this.owner;
   }

   public void setOwner(int owner) {
      this.owner = owner;
   }

   public boolean isBlockedController() {
      return this.blockController;
   }

   public void setBlockedController(boolean control) {
      this.blockController = control;
   }

   public interface AttackerEntry {
      List<MapleMonster.AttackingMapleCharacter> getAttackers();

      void addDamage(MapleCharacter var1, long var2, boolean var4);

      long getDamage();

      boolean contains(MapleCharacter var1);

      void killedMob(Field var1, int var2, boolean var3, int var4);
   }

   private static class AttackingMapleCharacter {
      private MapleCharacter attacker;
      private long lastAttackTime;

      public AttackingMapleCharacter(MapleCharacter attacker, long lastAttackTime) {
         this.attacker = attacker;
         this.lastAttackTime = lastAttackTime;
      }

      public final long getLastAttackTime() {
         return this.lastAttackTime;
      }

      public final void setLastAttackTime(long lastAttackTime) {
         this.lastAttackTime = lastAttackTime;
      }

      public final MapleCharacter getAttacker() {
         return this.attacker;
      }
   }

   private static final class ExpMap {
      public final int exp;
      public final byte ptysize;
      public final byte Premium_Bonus_EXP;
      public final int Buff_Bonus_EXP;

      public ExpMap(int exp, byte ptysize, byte Premium_Bonus_EXP, int Buff_Bonus_EXP) {
         this.exp = exp;
         this.ptysize = ptysize;
         this.Premium_Bonus_EXP = Premium_Bonus_EXP;
         this.Buff_Bonus_EXP = Buff_Bonus_EXP;
      }
   }

   public static final class OnePartyAttacker {
      public Party lastKnownParty;
      public long damage;
      public long lastAttackTime;

      public OnePartyAttacker(Party lastKnownParty, long damage) {
         this.lastKnownParty = lastKnownParty;
         this.damage = damage;
         this.lastAttackTime = System.currentTimeMillis();
      }
   }

   private class PartyAttackerEntry implements MapleMonster.AttackerEntry {
      private long totDamage = 0L;
      private final Map<Integer, MapleMonster.OnePartyAttacker> attackers = new HashMap<>(6);
      private int partyid;

      public PartyAttackerEntry(int partyid) {
         this.partyid = partyid;
      }

      @Override
      public List<MapleMonster.AttackingMapleCharacter> getAttackers() {
         List<MapleMonster.AttackingMapleCharacter> ret = new ArrayList<>(this.attackers.size());

         for (Entry<Integer, MapleMonster.OnePartyAttacker> entry : this.attackers.entrySet()) {
            MapleCharacter chr = MapleMonster.this.map.getCharacterById(entry.getKey());
            if (chr != null) {
               ret.add(new MapleMonster.AttackingMapleCharacter(chr, entry.getValue().lastAttackTime));
            }
         }

         return ret;
      }

      private final Map<MapleCharacter, MapleMonster.OnePartyAttacker> resolveAttackers() {
         Map<MapleCharacter, MapleMonster.OnePartyAttacker> ret = new HashMap<>(this.attackers.size());

         for (Entry<Integer, MapleMonster.OnePartyAttacker> aentry : this.attackers.entrySet()) {
            MapleCharacter chr = MapleMonster.this.map.getCharacterById(aentry.getKey());
            if (chr != null) {
               ret.put(chr, aentry.getValue());
            }
         }

         return ret;
      }

      @Override
      public final boolean contains(MapleCharacter chr) {
         return this.attackers.containsKey(chr.getId());
      }

      @Override
      public final long getDamage() {
         return this.totDamage;
      }

      @Override
      public void addDamage(MapleCharacter from, long damage, boolean updateAttackTime) {
         MapleMonster.OnePartyAttacker oldPartyAttacker = this.attackers.get(from.getId());
         if (oldPartyAttacker != null) {
            oldPartyAttacker.damage += damage;
            oldPartyAttacker.lastKnownParty = from.getParty();
            oldPartyAttacker.lastAttackTime = System.currentTimeMillis();
         } else {
            MapleMonster.OnePartyAttacker onePartyAttacker = new MapleMonster.OnePartyAttacker(from.getParty(), damage);
            this.attackers.put(from.getId(), onePartyAttacker);
         }

         this.totDamage += damage;
      }

      @Override
      public final void killedMob(Field map, int baseExp, boolean mostDamage, int lastSkill) {
         MapleCharacter highest = null;
         long highestDamage = 0L;
         int iexp = 0;
         List<MapleCharacter> expApplicable = new ArrayList<>();
         Map<MapleCharacter, MapleMonster.ExpMap> expMap = new HashMap<>(6);
         long lastAttackTime = 0L;
         MapleCharacter lastAttacker = null;

         for (Entry<MapleCharacter, MapleMonster.OnePartyAttacker> attacker : this.resolveAttackers().entrySet()) {
            Party party = attacker.getValue().lastKnownParty;
            double addedPartyLevel = 0.0;
            byte added_partyinc = 0;
            byte Premium_Bonus_EXP = 0;
            int Buff_Bonus_EXP = 0;
            expApplicable.clear();
            int count = 0;

            for (PartyMemberEntry partychar : party.getPartyMemberList()) {
               MapleCharacter pchr = map.getCharacterById(partychar.getId());
               if (pchr != null) {
                  if (pchr.getJob() >= 200
                        && pchr.getJob() <= 232
                        && attacker.getKey().getLevel() - partychar.getLevel() <= 30
                        && attacker.getKey().getLevel() - partychar.getLevel() >= -30) {
                     count++;
                  }

                  if (pchr.isAlive()
                        && (attacker.getKey().getLevel() - partychar.getLevel() <= 5
                              || MapleMonster.this.stats.getLevel() - partychar.getLevel() <= 5)) {
                     expApplicable.add(pchr);
                     addedPartyLevel += pchr.getLevel();
                     if (pchr.getStat().equippedWelcomeBackRing && Premium_Bonus_EXP == 0) {
                        Premium_Bonus_EXP = 80;
                     }

                     if (pchr.getStat().hasPartyBonus && added_partyinc < 4 && map.getPartyBonusRate() <= 0) {
                        added_partyinc++;
                     }
                  }
               }
            }

            if (attacker.getKey().getJob() >= 230 && attacker.getKey().getJob() <= 232) {
               Buff_Bonus_EXP = Math.min(60, (count - 1) * 20);
            }

            long iDamage = attacker.getValue().damage;
            if (iDamage > highestDamage) {
               highest = attacker.getKey();
               highestDamage = iDamage;
            }

            if (lastAttackTime < attacker.getValue().lastAttackTime) {
               lastAttackTime = attacker.getValue().lastAttackTime;
               lastAttacker = attacker.getKey();
            }

            double innerBaseExp = baseExp * ((double) iDamage / this.totDamage);

            for (MapleCharacter expReceiver : expApplicable) {
               iexp = expMap.get(expReceiver) == null ? 0 : expMap.get(expReceiver).exp;
               double levelMod = expReceiver.getLevel() / addedPartyLevel * 0.4;
               iexp += (int) Math
                     .round(((attacker.getKey().getId() == expReceiver.getId() ? 0.6 : 0.0) + levelMod) * innerBaseExp);
               expMap.put(expReceiver, new MapleMonster.ExpMap(iexp, (byte) (expApplicable.size() + added_partyinc),
                     Premium_Bonus_EXP, Buff_Bonus_EXP));
            }
         }

         AchievementFactory.checkMobKill(MapleMonster.this, expApplicable, lastAttacker, highest, lastSkill);

         for (Entry<MapleCharacter, MapleMonster.ExpMap> expReceiver : expMap.entrySet()) {
            MapleMonster.ExpMap expmap = expReceiver.getValue();
            MapleMonster.this.giveExpToCharacter(
                  expReceiver.getKey(),
                  (long) expmap.exp,
                  mostDamage && expReceiver.getKey() == highest,
                  expMap.size(),
                  expmap.ptysize,
                  expmap.Premium_Bonus_EXP,
                  lastSkill,
                  expmap.Buff_Bonus_EXP);
         }
      }

      @Override
      public final int hashCode() {
         int prime = 31;
         int result = 1;
         return 31 * result + this.partyid;
      }

      @Override
      public final boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            MapleMonster.PartyAttackerEntry other = (MapleMonster.PartyAttackerEntry) obj;
            return this.partyid == other.partyid;
         }
      }
   }

   public final class SingleAttackerEntry implements MapleMonster.AttackerEntry {
      private long damage = 0L;
      private int chrid;
      private long lastAttackTime;

      public SingleAttackerEntry(MapleCharacter from) {
         this.chrid = from.getId();
      }

      @Override
      public void addDamage(MapleCharacter from, long damage, boolean updateAttackTime) {
         if (this.chrid == from.getId()) {
            this.damage += damage;
            if (updateAttackTime) {
               this.lastAttackTime = System.currentTimeMillis();
            }
         }
      }

      @Override
      public final List<MapleMonster.AttackingMapleCharacter> getAttackers() {
         MapleCharacter chr = MapleMonster.this.map.getCharacterById(this.chrid);
         return chr != null
               ? Collections.singletonList(new MapleMonster.AttackingMapleCharacter(chr, this.lastAttackTime))
               : Collections.emptyList();
      }

      @Override
      public boolean contains(MapleCharacter chr) {
         return this.chrid == chr.getId();
      }

      @Override
      public long getDamage() {
         return this.damage;
      }

      @Override
      public void killedMob(Field map, int baseExp, boolean mostDamage, int lastSkill) {
         MapleCharacter chr = map.getCharacterById(this.chrid);
         if (chr != null && chr.isAlive()) {
            try {
               try {
                  MapleMonster.this.giveExpToCharacter(chr, (long) baseExp, mostDamage, 1, (byte) 0, (byte) 0,
                        lastSkill, 0);
               } catch (Exception var11) {
                  System.out.println("giveExpToCharacter 예외 발생");
                  var11.printStackTrace();
               }

               if (!DBConfig.isGanglim) {
                  for (MaplePet pet : chr.getPets()) {
                     if (pet != null
                           && (pet.getFlags() & MaplePet.PetFlag.PET_GIANT.getValue()) != 0
                           && (pet.getLastGiantPetBuffTime() == 0L
                                 || pet.getLastGiantPetBuffTime() + 300000L >= System.currentTimeMillis())) {
                        pet.addPetSize((short) 1);
                        chr.send(pet.petModified(chr));
                        if (pet.getPetSize() >= 300) {
                           pet.setLastGiantPetBuffTime(System.currentTimeMillis());
                           pet.setPetSize((short) 100);
                           chr.send(pet.giantPetBuff());
                           chr.send(pet.petModified(chr));
                           MapleItemInformationProvider.getInstance().getItemEffect(2023091).applyTo(chr);
                        }
                     }
                  }
               }

               try {
                  AchievementFactory.checkMobKill(MapleMonster.this, Collections.singletonList(chr), chr, chr,
                        lastSkill);
               } catch (Exception var10) {
                  System.out.println("Achievement CheckMobKill 예외 발생");
               }
            } catch (Exception var12) {
               System.out.println("killedMob 예외 발생");
               var12.printStackTrace();
            }
         }
      }

      @Override
      public int hashCode() {
         return this.chrid;
      }

      @Override
      public final boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            MapleMonster.SingleAttackerEntry other = (MapleMonster.SingleAttackerEntry) obj;
            return this.chrid == other.chrid;
         }
      }
   }
}
