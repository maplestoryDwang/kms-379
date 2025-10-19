package network.center;

import constants.GameConstants;
import database.DBConfig;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.Map.Entry;
import objects.context.friend.FriendEntry;
import objects.fields.child.union.MapleUnion;
import objects.item.DamageSkinSaveInfo;
import objects.item.IntensePowerCrystal;
import objects.item.Item;
import objects.item.ItemPot;
import objects.item.MapleMount;
import objects.item.MaplePet;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.quest.MobQuest;
import objects.quest.QuestEx;
import objects.quest.WeeklyQuest;
import objects.summoned.Summoned;
import objects.users.AntiMacro;
import objects.users.BuyLimit;
import objects.users.CharacterNameAndId;
import objects.users.MapleCabinet;
import objects.users.MapleCharacter;
import objects.users.MapleKeyLayout;
import objects.users.MapleMessage;
import objects.users.MapleTrait;
import objects.users.MobCollectionEx;
import objects.users.PraisePoint;
import objects.users.WorldBuyLimit;
import objects.users.achievement.Achievement;
import objects.users.extra.ExtraAbilityGrade;
import objects.users.extra.ExtraAbilityStatEntry;
import objects.users.jobs.BasicJob;
import objects.users.jobs.resistance.WildHunterInfo;
import objects.users.looks.mannequin.Mannequin;
import objects.users.looks.zero.ZeroInfo;
import objects.users.skills.IndieTemporaryStatEntry;
import objects.users.skills.KainStackSkill;
import objects.users.skills.LinkSkill;
import objects.users.skills.Skill;
import objects.users.skills.SkillAlarm;
import objects.users.skills.SkillEntry;
import objects.users.skills.VCore;
import objects.users.skills.VMatrixSlot;
import objects.users.stats.HexaCore;
import objects.users.stats.HyperStat;
import objects.users.stats.SecondaryStat;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Pair;
import security.anticheat.ReportType;

public class CharacterTransfer implements Externalizable {
   public int characterid;
   public int accountid;
   public int fame;
   public int pvpExp;
   public int pvpPoints;
   public int hair;
   public int secondhair;
   public int face;
   public int secondface;
   public int demonMarking;
   public int mapid;
   public int guildid;
   public int partyid;
   public int messengerid;
   public int nxCredit;
   public int ACash;
   public int MaplePoints;
   public int honourexp;
   public int honourlevel;
   public int itcafetime;
   public int mount_itemid;
   public int mount_exp;
   public int points;
   public int realCash;
   public int extremeRealCash;
   public int getExtremeRealCash;
   public int extremeLevelPoint;
   public int marriageId;
   public int familyid;
   public int seniorid;
   public int junior1;
   public int junior2;
   public int currentrep;
   public int totalrep;
   public int battleshipHP;
   public int todayContribution;
   public int guildContribution;
   public int totalWins;
   public int totalLosses;
   public int zeroLinkCashPart;
   public byte channel;
   public byte gender;
   public byte secondgender;
   public byte gmLevel;
   public byte guildrank;
   public byte alliancerank;
   public byte fairyExp;
   public byte cardStack;
   public byte buddysize;
   public byte world;
   public byte initialSpawnPoint;
   public byte secondSkinColor;
   public byte mount_level;
   public byte mount_Fatigue;
   public byte subcategory;
   public long meso;
   public long maxhp;
   public long maxmp;
   public long hp;
   public long mp;
   public long exp;
   public long lastfametime;
   public long TranferTime;
   public long createDate;
   public long lastLoggedinDate;
   public long todayLoggedinDate;
   public String name;
   public String accountname;
   public String secondPassword;
   public String BlessOfFairy;
   public String BlessOfEmpress;
   public String chalkboard;
   public String tempIP;
   public short level;
   public short str;
   public short dex;
   public short int_;
   public short luk;
   public short remainingAp;
   public short hpApUsed;
   public short job;
   public short fatigue;
   public short soulCount;
   public Object inventorys;
   public Object skillmacro;
   public Object storage;
   public Object cs;
   public Object anticheat;
   public Object innerSkills;
   public int[] savedlocation;
   public int[] wishlist;
   public int[] rocks;
   public int[] remainingSp;
   public int[] regrocks;
   public int[] hyperrocks;
   public ItemPot[] itemPots;
   public MaplePet[] pets = new MaplePet[3];
   public Map<Byte, Integer> reports = new LinkedHashMap<>();
   public List<Pair<Integer, Integer>> stolenSkills;
   public MapleKeyLayout[] keymap = new MapleKeyLayout[3];
   public List<Integer> famedcharacters = null;
   public List<Integer> battledaccs = null;
   public Map<Byte, List<Integer>> extendedSlots = null;
   public List<Item> rebuy = null;
   public Map<MapleTrait.MapleTraitType, MapleTrait> traits = new HashMap<>();
   public final Map<CharacterNameAndId, Boolean> buddies = new LinkedHashMap<>();
   public final Map<Integer, Object> Quest = new LinkedHashMap<>();
   public Map<Integer, QuestEx> InfoQuest;
   public Map<String, String> InfoCustom;
   public Map<Integer, MobCollectionEx> collectionInfo;
   public List<Integer> auctionWishList;
   public final Map<Integer, SkillEntry> Skills = new LinkedHashMap<>();
   public final Map<Integer, Integer> skillCustomValue = new HashMap<>();
   public Map<SecondaryStatFlag, List<IndieTemporaryStatEntry>> indieTemporaryStats = new HashMap<>();
   public List<VCore> vcoreSkills = new LinkedList<>();
   public List<VMatrixSlot> vmatrixSlots = new LinkedList<>();
   public Pair<Integer, Integer> equippedSpecialCore = null;
   public int spAttackCountMobId = 0;
   public int spCount = 0;
   public long spLastValidTime = 0L;
   public Timer DFRecoveryTimer;
   public int reborns;
   public int apstorage;
   public long mesoChairCount;
   public int hongboPoint;
   public int tsdPoint;
   public int tsdTotalPoint;
   public int tsPoint;
   public int killPoint;
   public int baseColor;
   public int addColor;
   public int baseProb;
   public int secondBaseColor;
   public int secondAddColor;
   public int secondBaseProb;
   public boolean petLoot;
   public boolean ccByScript;
   public boolean useMortalWingBit;
   public boolean activeGloryWing;
   public int blessMark;
   public long canNextSpecterStateTime;
   public int dancePoint;
   public int totalDancePoint;
   public int drawElfEar;
   public int drawTail;
   public int shift;
   public int curseWeakeningStack;
   public int relicCharge;
   public int lastUseCardinalForce;
   public int pathfinderPattern;
   public int curseToleranceStack;
   public int ancientGuidance;
   public int frozenLinkMobID;
   public int frozenLinkMobCount;
   public int bossLimitClear1;
   public int bossLimitClear2;
   public int bossLimitClear3;
   public int enchantPoint;
   public long lastFairyTime;
   public List<Summoned> summons = new LinkedList<>();
   public final Map<String, String> CustomValues = new HashMap<>();
   public final Map<String, Integer> CustomValues2 = new HashMap<>();
   public LinkSkill linkSkill = null;
   public SkillAlarm skillAlarm = null;
   public Map<Integer, List<MobQuest>> mobQuests = new HashMap<>();
   public List<WeeklyQuest> weeklyQuests = new ArrayList<>();
   public DamageSkinSaveInfo damageSkinSaveInfo;
   public int scrollFeverProbInc;
   public int huFailedStreak;
   public long huLastFailedUniqueID;
   public List<Integer> quickSlotKeyMapped;
   public int reincarnationCount;
   public MapleUnion currentUnion;
   public MapleUnion[] unionPreset;
   public WildHunterInfo wildHunterInfo;
   public int tteokgukPoint = 0;
   public int skinColor;
   public int registerTransferField = 0;
   public long registerTransferFieldTime = 0L;
   public int lastAttendanceDate = 0;
   public int faceBaseColor = 0;
   public int faceAddColor = 0;
   public int faceBaseProb = 0;
   public int secondFaceBaseColor = 0;
   public int secondFaceAddColor = 0;
   public int secondFaceBaseProb = 0;
   public int xenonSurplus = 0;
   public int lockEclipseLook = 0;
   public int lockEquilibriumLook = 0;
   public int lockBeastFormWingEffect = 0;
   public int lockKinesisPsychicEnergyShieldEffect = 0;
   public int lockHomingMissileEffect = 0;
   public short combo = 0;
   public int comboX = 0;
   public int liberationOrbDarkMad = 0;
   public int liberationOrbLightMad = 0;
   public int revenantRage = 0;
   public int viperEnergyCharge = 0;
   public HyperStat hyperStat = null;
   public HexaCore hexaCore = null;
   public BuyLimit buyLimit = null;
   public WorldBuyLimit worldBuyLimit = null;
   public SecondaryStat secondaryStat = null;
   public String nickItemMsg = null;
   public long startReincarnationTime = 0L;
   public AntiMacro antiMacro = null;
   public long startActiveMacroTime = 0L;
   public int darknessAuraStack = 0;
   public int accountTotalLevel = 0;
   public PraisePoint praisePoint = null;
   public Map<Integer, Long> affectedLimits;
   public MapleCabinet cabinetItem;
   public Map<Integer, Long> consumeItemLimits;
   public Map<Long, IntensePowerCrystal> intensePowerCrystals;
   public KainStackSkill kainStackSkill = null;
   public Mannequin hairMannequin = null;
   public Mannequin faceMannequin = null;
   public Mannequin skinMannequin = null;
   public BasicJob playerBasicJob = null;
   public ZeroInfo zeroInfo = null;
   public int smashStack;
   public ExtraAbilityStatEntry[][] extraAbilityStats = new ExtraAbilityStatEntry[2][3];
   public int extraAbilitySlot;
   public ExtraAbilityGrade extraAbilityGrade = ExtraAbilityGrade.Rare;
   public Achievement achievement = null;
   public MapleMessage[] sentMessage;
   public MapleMessage[] receivedMessage;
   public int bossTier;
   public Map<String, String> accKeyValues;
   public Map<String, Object> tempKeyValues;
   public boolean isSkipIntro;
   public long lastSpeedHackCheckTime;
   public int transferFieldCount;
   public int etherPoint;

   public CharacterTransfer() {
      this.famedcharacters = new ArrayList<>();
      this.battledaccs = new ArrayList<>();
      this.extendedSlots = new HashMap<>();
      this.rebuy = new ArrayList<>();
      this.InfoQuest = new LinkedHashMap<>();
      this.InfoCustom = new LinkedHashMap<>();
      this.collectionInfo = new LinkedHashMap<>();
      this.quickSlotKeyMapped = new LinkedList<>();
   }

   public CharacterTransfer(MapleCharacter chr) {
      this.characterid = chr.getId();
      this.accountid = chr.getAccountID();
      this.accountname = chr.getClient().getAccountName();
      this.secondPassword = chr.getClient().getSecondPassword();
      this.channel = (byte)chr.getClient().getChannel();
      this.nxCredit = chr.getCSPoints(1);
      this.ACash = chr.getCSPoints(4);
      this.MaplePoints = chr.getCSPoints(2);
      this.realCash = chr.getRealCash();
      this.extremeRealCash = chr.getExtremeRealCash();
      this.getExtremeRealCash = chr.getGetExtremeRealCash();
      this.extremeLevelPoint = chr.getExtremeLevelPoint();
      this.stolenSkills = chr.getStolenSkills();
      this.name = chr.getName();
      this.fame = chr.getFame();
      this.gender = chr.getGender();
      this.secondgender = chr.getSecondGender();
      this.level = chr.getLevel();
      this.str = chr.getStat().getStr();
      this.dex = chr.getStat().getDex();
      this.int_ = chr.getStat().getInt();
      this.luk = chr.getStat().getLuk();
      this.hp = chr.getStat().getHp();
      this.mp = chr.getStat().getMp();
      this.maxhp = chr.getStat().getMaxHp();
      this.maxmp = chr.getStat().getMaxMp();
      this.exp = chr.getExp();
      this.hpApUsed = chr.getHpApUsed();
      this.remainingAp = chr.getRemainingAp();
      this.remainingSp = chr.getRemainingSps();
      this.meso = chr.getMeso();
      this.pvpExp = chr.getTotalBattleExp();
      this.pvpPoints = chr.getBattlePoints();
      this.itcafetime = chr.getInternetCafeTime();
      this.reborns = chr.getReborns();
      this.apstorage = chr.getAPS();
      this.skinColor = chr.getSkinColor();
      this.secondSkinColor = chr.getSecondSkinColor();
      this.job = chr.getJob();
      this.hair = chr.getHair();
      this.secondhair = chr.getSecondHair();
      this.face = chr.getFace();
      this.secondface = chr.getSecondFace();
      this.demonMarking = chr.getDemonMarking();
      this.mapid = chr.getMapId();
      this.initialSpawnPoint = chr.getInitialSpawnpoint();
      this.marriageId = chr.getMarriageId();
      this.world = chr.getWorld();
      this.guildid = chr.getGuildId();
      this.guildrank = chr.getGuildRank();
      this.guildContribution = chr.getGuildContribution();
      this.todayContribution = chr.getTodayContribution();
      this.alliancerank = chr.getAllianceRank();
      this.gmLevel = (byte)chr.getGMLevel();
      this.points = chr.getPoints();
      this.fairyExp = chr.getFairyExp();
      this.cardStack = chr.getCardStack();
      this.auctionWishList = chr.getAuctionWishList();
      this.pets = chr.getPets();
      this.DFRecoveryTimer = chr.getDFRecoveryTimer();
      this.subcategory = chr.getSubcategory();
      this.itemPots = chr.getItemPots();
      this.fatigue = chr.getFatigue();
      this.totalWins = chr.getTotalWins();
      this.totalLosses = chr.getTotalLosses();
      this.battleshipHP = chr.currentBattleshipHP();
      chr.getCheatTracker().dispose();
      this.anticheat = chr.getCheatTracker();
      this.tempIP = chr.getClient().getTempIP();
      this.rebuy = chr.getRebuy();
      this.mesoChairCount = chr.getMesoChairCount();
      this.hongboPoint = chr.getHongboPoint();
      this.tsdPoint = chr.getTSDPoint();
      this.tsdTotalPoint = chr.getTSDTotalPoint();
      this.tsPoint = chr.getTS();
      this.petLoot = chr.getPetLoot();
      this.vcoreSkills = chr.getVCoreSkillsNoLock();
      this.vmatrixSlots = chr.getVMatrixSlots();
      this.equippedSpecialCore = chr.getEquippedSpecialCore();
      this.spAttackCountMobId = chr.getSpAttackCountMobId();
      this.spCount = chr.getSpCount();
      this.spLastValidTime = chr.getSpLastValidTime();
      this.useMortalWingBit = chr.isUseMortalWingBit();
      this.activeGloryWing = chr.isActiveGloryWing();
      this.blessMark = chr.getBlessMark();
      this.ccByScript = chr.isCCByScript();
      this.summons = chr.getSummons();
      this.linkSkill = chr.getLinkSkill();
      this.skillAlarm = chr.getSkillAlarm();
      this.canNextSpecterStateTime = chr.getCanNextSpecterStateTime();
      this.frozenLinkMobCount = chr.getFrozenLinkMobCount();
      this.frozenLinkMobID = chr.getFrozenLinkMobID();
      this.curseWeakeningStack = chr.getCurseWeakeningStack();
      this.relicCharge = chr.getRelicCharge();
      this.lastUseCardinalForce = chr.getLastUseCardinalForce();
      this.pathfinderPattern = chr.getPathfinderPattern();
      this.curseToleranceStack = chr.getCurseToleranceStack();
      this.ancientGuidance = chr.getAncientGuidance();
      this.bossLimitClear1 = chr.getBossLimitClear1();
      this.bossLimitClear2 = chr.getBossLimitClear2();
      this.bossLimitClear3 = chr.getBossLimitClear3();
      this.enchantPoint = chr.getEnchantPoint();
      this.lastFairyTime = chr.getLastFairyTime();
      this.damageSkinSaveInfo = chr.getDamageSkinSaveInfo();
      this.drawTail = chr.getDrawTail();
      this.drawElfEar = chr.getDrawElfEar();
      this.shift = chr.getShift();
      this.scrollFeverProbInc = chr.getScrollFeverProbInc();
      this.huFailedStreak = chr.getHuFailedStreak();
      this.huLastFailedUniqueID = chr.getHuLastFailedUniqueID();
      this.reincarnationCount = chr.getReincarnationCount();
      this.startReincarnationTime = chr.getStartReincarnationTime();
      this.currentUnion = chr.getMapleUnion();
      this.unionPreset = chr.getMapleUnionPreset();
      this.wildHunterInfo = chr.getWildHunterInfo();
      this.tteokgukPoint = chr.getTteokgukPoint();
      this.registerTransferField = chr.getRegisterTransferField();
      this.registerTransferFieldTime = chr.getRegisterTransferFieldTime();
      this.todayLoggedinDate = chr.getTodayLoggedinDate();
      this.lastLoggedinDate = chr.getLastLoggedinDate();
      this.liberationOrbDarkMad = chr.getLiberationOrbDarkMad();
      this.liberationOrbLightMad = chr.getLiberationOrbLightMad();
      this.revenantRage = chr.getRevenantRage();
      this.antiMacro = chr.getAntiMacro();
      this.startActiveMacroTime = chr.getStartActiveMacroTime();
      this.nickItemMsg = chr.getNickItemMsg();
      this.cabinetItem = chr.getCabinet();
      this.darknessAuraStack = chr.getDarknessAuraStack();
      this.secondaryStat = chr.getSecondaryStat();
      this.buyLimit = chr.getBuyLimit();
      this.worldBuyLimit = chr.getWorldBuyLimit();
      this.viperEnergyCharge = chr.getViperEnergyCharge();
      this.hyperStat = chr.getHyperStat();
      this.hexaCore = chr.getHexaCore();
      this.kainStackSkill = chr.getKainStackSKill();
      this.accountTotalLevel = chr.getAccountTotalLevel();
      this.lastSpeedHackCheckTime = chr.getLastSpeedHackCheckTime();
      this.extraAbilityStats = chr.getExtraAbilityStats();
      this.extraAbilitySlot = chr.getExtraAbilitySlot();
      this.extraAbilityGrade = chr.getExtraAbilityGrade();
      this.achievement = chr.getAchievement();
      this.transferFieldCount = chr.getTransferFieldCount();

      for (Entry<Integer, List<MobQuest>> ee : chr.getMobQuests().entrySet()) {
         this.mobQuests.put(ee.getKey(), ee.getValue());
      }

      for (WeeklyQuest q : chr.getWeeklyQuest()) {
         this.weeklyQuests.add(q);
      }

      this.quickSlotKeyMapped = new LinkedList<>();

      for (Integer key : chr.getQuickSlotKeyMapped()) {
         this.quickSlotKeyMapped.add(key);
      }

      this.affectedLimits = new HashMap<>();

      for (Entry<Integer, Long> ee : chr.getAffectedLimits().entrySet()) {
         this.affectedLimits.put(ee.getKey(), ee.getValue());
      }

      this.consumeItemLimits = new HashMap<>();

      for (Entry<Integer, Long> ee : chr.getConsumeItemLimits().entrySet()) {
         this.consumeItemLimits.put(ee.getKey(), ee.getValue());
      }

      this.intensePowerCrystals = new HashMap<>();

      for (Entry<Long, IntensePowerCrystal> ee : chr.getIntensePowerCrystals().entrySet()) {
         this.intensePowerCrystals.put(ee.getKey(), ee.getValue());
      }

      this.lastAttendanceDate = chr.getLastAttendacneDate();
      this.createDate = chr.getCreateDate();
      this.dancePoint = chr.getDancePoint();
      this.totalDancePoint = chr.getTotalDancePoint();

      for (Entry<String, String> cv : chr.getCustomValues().entrySet()) {
         this.CustomValues.put(cv.getKey(), cv.getValue());
      }

      for (Entry<String, Integer> cv : chr.getCustomValues2().entrySet()) {
         this.CustomValues2.put(cv.getKey(), cv.getValue());
      }

      this.traits = chr.getTraits();

      for (FriendEntry qs : chr.getBuddylist().getBuddies()) {
         this.buddies
            .put(
               new CharacterNameAndId(qs.getCharacterId(), qs.getAccountId(), qs.getName(), qs.getLevel(), qs.getJob(), qs.getGroupName(), qs.getMemo()),
               qs.isVisible()
            );
      }

      for (Entry<ReportType, Integer> ss : chr.getReports().entrySet()) {
         this.reports.put(ss.getKey().i, ss.getValue());
      }

      this.buddysize = chr.getBuddyCapacity();
      this.partyid = chr.getParty() == null ? -1 : chr.getParty().getId();
      if (chr.getMessenger() != null) {
         this.messengerid = chr.getMessenger().getId();
      } else {
         this.messengerid = 0;
      }

      this.InfoQuest = chr.getInfoQuest_Map();
      this.InfoCustom = chr.getInfoCustom_Map();
      this.collectionInfo = chr.getCollectionInfo();

      for (Entry<MapleQuest, MapleQuestStatus> qs : chr.getQuest_Map().entrySet()) {
         this.Quest.put(qs.getKey().getId(), qs.getValue());
      }

      this.inventorys = chr.getInventorys();

      for (Entry<Skill, SkillEntry> qs : chr.getSkills().entrySet()) {
         this.Skills.put(qs.getKey().getId(), qs.getValue());
      }

      for (Entry<Integer, Integer> cv : chr.getSkillCustomValues().entrySet()) {
         this.skillCustomValue.put(cv.getKey(), cv.getValue());
      }

      this.lockEclipseLook = chr.getLockEclipseLook();
      this.lockBeastFormWingEffect = chr.getLockBeastFormWingEffect();
      this.lockEquilibriumLook = chr.getLockEquilibriumLook();
      this.lockHomingMissileEffect = chr.getLockHomingMissileEffect();
      this.lockKinesisPsychicEnergyShieldEffect = chr.getLockKinesisPsychicEnergyShieldEffect();
      this.combo = chr.getCombo();
      this.comboX = chr.getComboX();
      this.secondBaseColor = chr.getSecondBaseColor();
      this.secondBaseProb = chr.getSecondBaseProb();
      this.secondAddColor = chr.getSecondAddColor();
      this.xenonSurplus = chr.getXenonSurplus();
      this.faceAddColor = chr.getFaceAddColor();
      this.faceBaseColor = chr.getFaceBaseColor();
      this.faceBaseProb = chr.getFaceBaseProb();
      this.secondFaceAddColor = chr.getSecondFaceAddColor();
      this.secondFaceBaseColor = chr.getSecondFaceBaseColor();
      this.secondFaceBaseProb = chr.getSecondFaceBaseProb();
      this.BlessOfFairy = chr.getBlessOfFairyOrigin();
      this.BlessOfEmpress = chr.getBlessOfEmpressOrigin();
      this.chalkboard = chr.getChalkboard();
      this.skillmacro = chr.getMacros();
      this.keymap = chr.getKeyLayout();
      this.savedlocation = chr.getSavedLocations();
      this.wishlist = chr.getWishlist();
      this.rocks = chr.getRocks();
      this.regrocks = chr.getRegRocks();
      this.hyperrocks = chr.getHyperRocks();
      this.famedcharacters = chr.getFamedCharacters();
      this.battledaccs = chr.getBattledCharacters();
      this.lastfametime = chr.getLastFameTime();
      this.storage = chr.getStorage();
      this.cs = chr.getCashInventory();
      this.honourexp = chr.getInnerExp();
      this.honourlevel = chr.getInnerLevel();
      this.soulCount = chr.getSoulCount();
      this.innerSkills = chr.getInnerSkills();
      this.extendedSlots = chr.getExtendedSlots();
      this.zeroLinkCashPart = chr.getZeroLinkCashPart();
      this.baseColor = chr.getBaseColor();
      this.addColor = chr.getAddColor();
      this.baseProb = chr.getBaseProb();
      this.killPoint = chr.getKillPoint();
      MapleMount mount = chr.getMount();
      this.mount_itemid = mount.getItemId();
      this.mount_Fatigue = mount.getFatigue();
      this.mount_level = mount.getLevel();
      this.mount_exp = mount.getExp();
      this.praisePoint = chr.getPraisePoint();
      this.hairMannequin = chr.getHairMannequin();
      this.faceMannequin = chr.getFaceMannequin();
      this.skinMannequin = chr.getSkinMannequin();
      this.playerBasicJob = chr.getPlayerJob();
      this.accKeyValues = chr.getClient().getKeyValues();
      this.tempKeyValues = chr.getAllTempKeyValue();
      this.bossTier = chr.getBossTier();
      if (DBConfig.isGanglim) {
         this.isSkipIntro = chr.getIsSkipIntro();
      }

      if (GameConstants.isZero(chr.getJob())) {
         this.zeroInfo = chr.getZeroInfo();
      }

      if (GameConstants.isKaiser(chr.getJob())) {
         this.smashStack = chr.getSmashStack();
      }

      if (GameConstants.isAdele(chr.getJob())) {
         this.etherPoint = chr.getEtherPoint();
      }

      this.receivedMessage = chr.getReceivedMessages();
      this.sentMessage = chr.getSentMessages();
      this.TranferTime = System.currentTimeMillis();
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      this.characterid = in.readInt();
      this.accountid = in.readInt();
      this.accountname = in.readUTF();
      this.secondPassword = in.readUTF();
      this.channel = in.readByte();
      this.nxCredit = in.readInt();
      this.ACash = in.readInt();
      this.MaplePoints = in.readInt();
      this.name = in.readUTF();
      this.fame = in.readInt();
      this.gender = in.readByte();
      this.secondgender = in.readByte();
      this.level = in.readShort();
      this.str = in.readShort();
      this.dex = in.readShort();
      this.int_ = in.readShort();
      this.luk = in.readShort();
      this.hp = in.readInt();
      this.mp = in.readInt();
      this.maxhp = in.readInt();
      this.maxmp = in.readInt();
      this.exp = in.readLong();
      this.hpApUsed = in.readShort();
      this.remainingAp = in.readShort();
      this.remainingSp = new int[in.readByte()];

      for (int i = 0; i < this.remainingSp.length; i++) {
         this.remainingSp[i] = in.readInt();
      }

      this.meso = in.readLong();
      this.skinColor = in.readByte();
      this.secondSkinColor = in.readByte();
      this.job = in.readShort();
      this.hair = in.readInt();
      this.secondhair = in.readInt();
      this.face = in.readInt();
      this.secondface = in.readInt();
      this.demonMarking = in.readInt();
      this.mapid = in.readInt();
      this.initialSpawnPoint = in.readByte();
      this.world = in.readByte();
      this.guildid = in.readInt();
      this.guildrank = in.readByte();
      this.guildContribution = in.readInt();
      this.alliancerank = in.readByte();
      this.gmLevel = in.readByte();
      this.points = in.readInt();
      this.realCash = in.readInt();
      this.hongboPoint = in.readInt();
      this.tsdPoint = in.readInt();
      this.tsdTotalPoint = in.readInt();
      this.tsPoint = in.readInt();
      this.baseColor = in.readInt();
      this.addColor = in.readInt();
      this.baseProb = in.readInt();
      this.petLoot = in.readBoolean();
      this.killPoint = in.readInt();
      if (in.readByte() == 1) {
         this.BlessOfFairy = in.readUTF();
      } else {
         this.BlessOfFairy = null;
      }

      if (in.readByte() == 1) {
         this.BlessOfEmpress = in.readUTF();
      } else {
         this.BlessOfEmpress = null;
      }

      if (in.readByte() == 1) {
         this.chalkboard = in.readUTF();
      } else {
         this.chalkboard = null;
      }

      this.skillmacro = in.readObject();
      this.lastfametime = in.readLong();
      this.storage = in.readObject();
      this.cs = in.readObject();
      this.mount_itemid = in.readInt();
      this.mount_Fatigue = in.readByte();
      this.mount_level = in.readByte();
      this.mount_exp = in.readInt();
      this.partyid = in.readInt();
      this.messengerid = in.readInt();
      this.inventorys = in.readObject();
      this.fairyExp = in.readByte();
      this.cardStack = in.readByte();
      this.subcategory = in.readByte();
      this.fatigue = in.readShort();
      this.marriageId = in.readInt();
      this.familyid = in.readInt();
      this.seniorid = in.readInt();
      this.junior1 = in.readInt();
      this.junior2 = in.readInt();
      this.currentrep = in.readInt();
      this.totalrep = in.readInt();
      this.battleshipHP = in.readInt();
      this.totalWins = in.readInt();
      this.totalLosses = in.readInt();
      this.anticheat = in.readObject();
      this.tempIP = in.readUTF();
      this.honourexp = in.readInt();
      this.honourlevel = in.readInt();
      this.soulCount = (short)in.readInt();
      this.innerSkills = in.readObject();
      this.pvpExp = in.readInt();
      this.pvpPoints = in.readInt();
      this.itcafetime = in.readInt();
      this.reborns = in.readInt();
      this.apstorage = in.readInt();
      this.mesoChairCount = in.readLong();
      this.zeroLinkCashPart = in.readInt();
      int customKeySize = in.readInt();

      for (int i = 0; i < customKeySize; i++) {
         this.CustomValues.put(in.readUTF(), in.readUTF());
      }

      int customKeySize2 = in.readInt();

      for (int i = 0; i < customKeySize2; i++) {
         this.CustomValues2.put(in.readUTF(), in.readInt());
      }

      int skillsize = in.readShort();

      for (int i = 0; i < skillsize; i++) {
         this.Skills.put(in.readInt(), new SkillEntry(in.readInt(), in.readByte(), in.readLong()));
      }

      int customsize = in.readByte();

      for (int i = 0; i < customsize; i++) {
         this.skillCustomValue.put(in.readInt(), in.readInt());
      }

      int questsize = in.readShort();

      for (int i = 0; i < questsize; i++) {
         this.Quest.put(in.readInt(), in.readObject());
      }

      int rzsize = in.readByte();

      for (int i = 0; i < rzsize; i++) {
         this.reports.put(in.readByte(), in.readInt());
      }

      int famesize = in.readByte();

      for (int i = 0; i < famesize; i++) {
         this.famedcharacters.add(in.readInt());
      }

      int battlesize = in.readInt();

      for (int i = 0; i < battlesize; i++) {
         this.battledaccs.add(in.readInt());
      }

      int savesize = in.readByte();
      this.savedlocation = new int[savesize];

      for (int i = 0; i < savesize; i++) {
         this.savedlocation[i] = in.readInt();
      }

      int wsize = in.readByte();
      this.wishlist = new int[wsize];

      for (int i = 0; i < wsize; i++) {
         this.wishlist[i] = in.readInt();
      }

      int rsize = in.readByte();
      this.rocks = new int[rsize];

      for (int i = 0; i < rsize; i++) {
         this.rocks[i] = in.readInt();
      }

      int resize = in.readByte();
      this.regrocks = new int[resize];

      for (int i = 0; i < resize; i++) {
         this.regrocks[i] = in.readInt();
      }

      int hesize = in.readByte();
      this.hyperrocks = new int[resize];

      for (int i = 0; i < hesize; i++) {
         this.hyperrocks[i] = in.readInt();
      }

      int rebsize = in.readShort();

      for (int i = 0; i < rebsize; i++) {
         this.rebuy.add((Item)in.readObject());
      }

      this.TranferTime = System.currentTimeMillis();
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeInt(this.characterid);
      out.writeInt(this.accountid);
      out.writeUTF(this.accountname);
      out.writeUTF(this.secondPassword);
      out.writeByte(this.channel);
      out.writeInt(this.nxCredit);
      out.writeInt(this.ACash);
      out.writeInt(this.MaplePoints);
      out.writeUTF(this.name);
      out.writeInt(this.fame);
      out.writeByte(this.gender);
      out.writeByte(this.secondgender);
      out.writeShort(this.level);
      out.writeShort(this.str);
      out.writeShort(this.dex);
      out.writeShort(this.int_);
      out.writeShort(this.luk);
      out.writeLong(this.hp);
      out.writeLong(this.mp);
      out.writeLong(this.maxhp);
      out.writeLong(this.maxmp);
      out.writeLong(this.exp);
      out.writeShort(this.hpApUsed);
      out.writeShort(this.remainingAp);
      out.writeByte(this.remainingSp.length);

      for (int i = 0; i < this.remainingSp.length; i++) {
         out.writeInt(this.remainingSp[i]);
      }

      out.writeLong(this.meso);
      out.writeByte(this.skinColor);
      out.writeByte(this.secondSkinColor);
      out.writeShort(this.job);
      out.writeInt(this.hair);
      out.writeInt(this.secondhair);
      out.writeInt(this.face);
      out.writeInt(this.secondface);
      out.writeInt(this.demonMarking);
      out.writeInt(this.mapid);
      out.writeByte(this.initialSpawnPoint);
      out.writeByte(this.world);
      out.writeInt(this.guildid);
      out.writeByte(this.guildrank);
      out.writeInt(this.guildContribution);
      out.writeByte(this.alliancerank);
      out.writeByte(this.gmLevel);
      out.writeInt(this.points);
      out.writeInt(this.realCash);
      out.writeInt(this.hongboPoint);
      out.writeInt(this.tsdPoint);
      out.writeInt(this.tsdTotalPoint);
      out.writeInt(this.tsPoint);
      out.writeInt(this.baseColor);
      out.writeInt(this.addColor);
      out.writeInt(this.baseProb);
      out.writeBoolean(this.petLoot);
      out.writeInt(this.killPoint);
      out.writeByte(this.BlessOfFairy == null ? 0 : 1);
      if (this.BlessOfFairy != null) {
         out.writeUTF(this.BlessOfFairy);
      }

      out.writeByte(this.BlessOfEmpress == null ? 0 : 1);
      if (this.BlessOfEmpress != null) {
         out.writeUTF(this.BlessOfEmpress);
      }

      out.writeByte(this.chalkboard == null ? 0 : 1);
      if (this.chalkboard != null) {
         out.writeUTF(this.chalkboard);
      }

      out.writeObject(this.skillmacro);
      out.writeLong(this.lastfametime);
      out.writeObject(this.storage);
      out.writeObject(this.cs);
      out.writeInt(this.mount_itemid);
      out.writeByte(this.mount_Fatigue);
      out.writeByte(this.mount_level);
      out.writeInt(this.mount_exp);
      out.writeInt(this.partyid);
      out.writeInt(this.messengerid);
      out.writeObject(this.inventorys);
      out.writeByte(this.fairyExp);
      out.writeByte(this.cardStack);
      out.writeByte(this.subcategory);
      out.writeShort(this.fatigue);
      out.writeInt(this.marriageId);
      out.writeInt(this.familyid);
      out.writeInt(this.seniorid);
      out.writeInt(this.junior1);
      out.writeInt(this.junior2);
      out.writeInt(this.currentrep);
      out.writeInt(this.totalrep);
      out.writeInt(this.battleshipHP);
      out.writeInt(this.totalWins);
      out.writeInt(this.totalLosses);
      out.writeObject(this.anticheat);
      out.writeUTF(this.tempIP);
      out.writeInt(this.pvpExp);
      out.writeInt(this.pvpPoints);
      out.writeInt(this.itcafetime);
      out.writeInt(this.reborns);
      out.writeInt(this.apstorage);
      out.writeLong(this.mesoChairCount);
      out.writeInt(this.zeroLinkCashPart);
      out.writeInt(this.honourexp);
      out.writeInt(this.honourlevel);
      out.writeObject(this.innerSkills);
      out.writeInt(this.CustomValues.size());

      for (Entry<String, String> cv : this.CustomValues.entrySet()) {
         out.writeUTF(cv.getKey());
         out.writeUTF(cv.getValue());
      }

      out.writeInt(this.CustomValues2.size());

      for (Entry<String, Integer> cv : this.CustomValues2.entrySet()) {
         out.writeUTF(cv.getKey());
         out.writeInt(cv.getValue());
      }

      out.writeShort(this.Skills.size());

      for (Entry<Integer, SkillEntry> qs : this.Skills.entrySet()) {
         out.writeInt(qs.getKey());
         out.writeInt(qs.getValue().skillevel);
         out.writeByte(qs.getValue().masterlevel);
         out.writeLong(qs.getValue().expiration);
      }

      out.writeByte(this.skillCustomValue.size());

      for (Entry<Integer, Integer> cv : this.skillCustomValue.entrySet()) {
         out.writeInt(cv.getKey());
         out.writeInt(cv.getValue());
      }

      out.writeShort(this.Quest.size());

      for (Entry<Integer, Object> qs : this.Quest.entrySet()) {
         out.writeInt(qs.getKey());
         out.writeObject(qs.getValue());
      }

      out.writeByte(this.reports.size());

      for (Entry<Byte, Integer> ss : this.reports.entrySet()) {
         out.writeByte(ss.getKey());
         out.writeInt(ss.getValue());
      }

      out.writeByte(this.famedcharacters.size());

      for (Integer zz : this.famedcharacters) {
         out.writeInt(zz);
      }

      out.writeInt(this.battledaccs.size());

      for (Integer zz : this.battledaccs) {
         out.writeInt(zz);
      }

      out.writeByte(this.savedlocation.length);

      for (int zz : this.savedlocation) {
         out.writeInt(zz);
      }

      out.writeByte(this.wishlist.length);

      for (int zz : this.wishlist) {
         out.writeInt(zz);
      }

      out.writeByte(this.rocks.length);

      for (int zz : this.rocks) {
         out.writeInt(zz);
      }

      out.writeByte(this.regrocks.length);

      for (int zz : this.regrocks) {
         out.writeInt(zz);
      }

      out.writeByte(this.hyperrocks.length);

      for (int zz : this.hyperrocks) {
         out.writeInt(zz);
      }

      out.writeShort(this.rebuy.size());

      for (int i = 0; i < this.rebuy.size(); i++) {
         out.writeObject(this.rebuy.get(i));
      }

      out.writeInt(this.soulCount);
   }
}
