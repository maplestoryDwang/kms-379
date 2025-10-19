package objects.users.stats;

import constants.GameConstants;
import constants.HexaMatrixConstants;
import constants.ServerConstants;
import database.DBConfig;
import io.netty.util.internal.ThreadLocalRandom;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import objects.context.SpecialSunday;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.effect.child.ExpEffect;
import objects.effect.child.PostSkillEffect;
import objects.effect.child.SkillEffect;
import objects.fields.Field;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.child.blackmage.Field_BlackMageBattlePhase4;
import objects.fields.child.lucid.Field_LucidBattle;
import objects.fields.child.yutagolden.Field_YutaGolden;
import objects.fields.gameobject.AffectedArea;
import objects.fields.gameobject.Extractor;
import objects.fields.gameobject.OpenGate;
import objects.fields.gameobject.TownPortal;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventory;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.summoned.SummonMoveAbility;
import objects.summoned.Summoned;
import objects.users.MapleCharacter;
import objects.users.MapleCoolDownValueHolder;
import objects.users.MapleStat;
import objects.users.MapleTrait;
import objects.users.PlayerStats;
import objects.users.achievement.AchievementFactory;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.utils.CaltechEval;
import objects.utils.FileoutputUtil;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Triple;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;
import objects.wz.provider.MapleDataType;

public class SecondaryStatEffect implements Serializable {
   private static final long serialVersionUID = 9179541993413738569L;
   private byte mastery;
   private byte mobCount;
   private byte bulletCount;
   private byte reqGuildLevel;
   private byte period;
   private byte expR;
   private byte iceGageCon;
   private byte recipeUseCount;
   private byte recipeValidDay;
   private byte reqSkillLevel;
   private byte slotCount;
   private byte effectedOnAlly;
   private byte effectedOnEnemy;
   private byte type;
   private byte preventslip;
   private byte immortal;
   private byte bs;
   private byte powerCon;
   private short hp;
   private short mp;
   private short pad;
   private short mad;
   private short def;
   private short mdef;
   private short acc;
   private short avoid;
   private short hands;
   private short speed;
   private short jump;
   private short psdSpeed;
   private short psdJump;
   private short mpCon;
   private short hpCon;
   private short forceCon;
   private short reduceForceR;
   private short comboConAran;
   private short bdR;
   private short damage;
   private short prop;
   private short subProp;
   private short emhp;
   private short emmp;
   private short epad;
   private short emad;
   private short epdd;
   private short emdd;
   private short ignoreMobpdpR;
   private short dot;
   private short dotTime;
   private short dotInterval;
   private short dotSuperpos;
   private short dotTickDamR;
   private short maxDotTickDamR;
   private short criticaldamageMin;
   private short criticaldamageMax;
   private short pddX;
   private short mddX;
   private short pddR;
   private short mddR;
   private short asrR;
   private short terR;
   private short er;
   private short padX;
   private short madX;
   private short mesoR;
   private short dropR;
   private short thaw;
   private short selfDestruction;
   private short PVPdamage;
   private short indiePad;
   private short indiePadR;
   private short indieMad;
   private short indieMadR;
   private short indiePMd;
   private short fatigueChange;
   private short onActive;
   private short indieDEX;
   private short str;
   private short dex;
   private short int_;
   private short luk;
   private short strX;
   private short dexX;
   private short intX;
   private short lukX;
   private short hpFX;
   private short mpFX;
   private short strFX;
   private short dexFX;
   private short intFX;
   private short lukFX;
   private short lifeId;
   private short imhp;
   private short immp;
   private short inflation;
   private short useLevel;
   private short mpConReduce;
   private short soulmpCon;
   private short mhpR;
   private short mmpR;
   private short bufftimeR;
   private short evaR;
   private short indieCr;
   private short indieCD;
   private short indieMhp;
   private short indieMmp;
   private short indieStance;
   private short indieAllStat;
   private short indieSpeed;
   private short indieJump;
   private short indieBooster;
   private short indieAcc;
   private short indieEva;
   private short indieEvaR;
   private short indiePdd;
   private short indiePddR;
   private short indieMdd;
   private short incPVPdamage;
   private short indieExp;
   private short indieMhpR;
   private short indieMmpR;
   private short indieAsrR;
   private short indieTerR;
   private short indieDamR;
   private short indieBDR;
   private short indieIgnoreMobpdpR;
   private short indieMaxDamageOver;
   private short cancelableTime;
   private short mobSkill;
   private short mobSkillLevel;
   private short indiePMdR;
   private short morph;
   private short MDF;
   private short MPP;
   private short lv2mhp;
   private short lv2mmp;
   private short costmpR;
   private short summonTimeR;
   private short attackCount;
   private short indieDamReduceR;
   private short fixCoolTime;
   private short mesoAmountUp;
   private short indieCooltimeReduce;
   private short indieStatRBasic;
   private short dotHealHPPerSecondR;
   private short dotHealMPPerSecondR;
   private short hpRCon;
   private short mpRCon;
   private short targetPlus;
   private short targetPlus_5th;
   private short damAbsorbShieldR;
   private short expRPerM;
   private short killRecoveryR;
   private short ignoreMobDamR;
   private double hpR;
   private double mpR;
   private double t;
   private Map<MapleTrait.MapleTraitType, Integer> traits;
   private int nocoolProp;
   private int stanceProp;
   private int duration;
   private int time;
   private int subTime;
   private int ppCon;
   private int ppRecovery;
   private int sourceid;
   private int recipe;
   private int moveTo;
   private int u;
   private int u2;
   private int v;
   private int v2;
   private int w;
   private int w2;
   private int x;
   private int y;
   private int z;
   private int s;
   private int s2;
   private int q;
   private int q2;
   private int cr;
   private int itemCon;
   private int itemConNo;
   private int updatableTime;
   private int bulletConsume;
   private int moneyCon;
   private int damR;
   private int damR_6th;
   private int speedMax;
   private int accX;
   private int mhpX;
   private int mmpX;
   private int coolTimeR;
   private int cooltime;
   private int cooltimeMS;
   private int ndTime;
   private int morphId = 0;
   private int expinc;
   private int exp;
   private int consumeOnPickup;
   private int range;
   private int price;
   private int extendPrice;
   private int charColor;
   private int interval;
   private int rewardMeso;
   private int totalprob;
   private int cosmetic;
   private int gauge;
   private int passivePlus;
   private int mesoG;
   private int disCountR;
   private int itemUpgradeBonusR;
   private int itemCursedProtectR;
   private int itemTUCProtectR;
   private int expGuild;
   private boolean overTime;
   private boolean skill;
   private boolean partyBuff = true;
   private EnumMap<SecondaryStatFlag, Integer> statups;
   private ArrayList<Pair<Integer, Integer>> availableMap;
   private EnumMap<MobTemporaryStatFlag, Integer> monsterStatus;
   private Point lt;
   private Point lt2;
   private Point rb;
   private Point rb2;
   private Point lt3;
   private Point rb3;
   private int expBuff;
   private int itemup;
   private int mesoup;
   private int cashup;
   private int berserk;
   private int illusion;
   private int berserk2;
   private int cp;
   private int nuffSkill;
   private int weapon;
   private int atGauge1Con;
   private int atGauge2Inc;
   private int atGauge2Con = 0;
   private byte level;
   private List<SecondaryStatFlag> cureDebuffs;
   private List<Integer> petsCanConsume;
   private List<Integer> randomPickup;
   private List<Integer> mapList;
   private List<Triple<Integer, Integer, Integer>> rewardItem;
   private static int skillId;
   private int incMobCollectionProp;

   public void addMapList(MapleData item) {
      this.mapList = new ArrayList<>();
      MapleData mapList = item.getChildByPath("map");
      if (mapList != null) {
         for (MapleData child : mapList.getChildren()) {
            int mapID = MapleDataTool.getInt(child, 0);
            this.mapList.add(mapID);
         }
      }
   }

   public static final SecondaryStatEffect loadSkillEffectFromData(MapleData source, int skillid, boolean overtime,
         int level, String variables) {
      return loadFromData(source, skillid, true, overtime, level, variables);
   }

   public static final SecondaryStatEffect loadItemEffectFromData(MapleData source, int itemid) {
      return loadFromData(source, itemid, false, false, 1, null);
   }

   private static final void addBuffStatPairToListIfNotZero(EnumMap<SecondaryStatFlag, Integer> list,
         SecondaryStatFlag buffstat, Integer val) {
      if (val != 0) {
         list.put(buffstat, val);
      }
   }

   public static int parseEval(String data, int level) {
      String variables = "x";
      String dddd = data.replace(variables, String.valueOf(level));
      if (dddd.substring(0, 1).equals("-")) {
         if (!dddd.substring(1, 2).equals("u") && !dddd.substring(1, 2).equals("d")) {
            dddd = "n" + dddd.substring(1, dddd.length());
         } else {
            dddd = "n(" + dddd.substring(1, dddd.length()) + ")";
         }
      } else if (dddd.substring(0, 1).equals("=")) {
         dddd = dddd.substring(1, dddd.length());
      }

      return (int) new CaltechEval(dddd.replace("\\r\\n", "")).evaluate();
   }

   private static final int parseEval(String path, MapleData source, int def, String variables, int level) {
      return (int) parseEvalDouble(path, source, def, variables, level);
   }

   private static final double parseEvalDouble(String path, MapleData source, int def, String variables, int level) {
      if (variables == null) {
         return MapleDataTool.getIntConvert(path, source, def);
      } else {
         MapleData dd = source.getChildByPath(path);
         if (dd == null) {
            return def;
         } else if (dd.getType() != MapleDataType.STRING) {
            return MapleDataTool.getIntConvert(path, source, def);
         } else {
            boolean f = true;
            double d = 0.0;

            try {
               String ret = MapleDataTool.getString(dd);
               d = Double.parseDouble(ret);
            } catch (NumberFormatException var15) {
               f = false;
            }

            if (f) {
               return d;
            } else {
               String ddd = MapleDataTool.getString(dd).replace("y", "x");
               String dddd = ddd.replace(variables, String.valueOf(level));
               String var18 = dddd.replace("X", String.valueOf(level));
               var18.replace(" ", "");
               if (var18.contains("log30(")) {
                  String[] values = var18.split("log30\\(");
                  String sValue = values[1].split("\\)")[0];
                  double lValue = Double.parseDouble(sValue);
                  double result = Math.log10(lValue) / Math.log(30.0);
                  var18 = var18.replace("log30(" + sValue + ")", String.valueOf(result));
                  if (var18.contains("log10(")) {
                     values = var18.split("log10\\(");
                     sValue = values[1].split("\\)")[0];
                     lValue = Double.parseDouble(sValue);
                     result = Math.log10(lValue) / Math.log(10.0);
                     var18 = var18.replace("log10(" + sValue + ")", String.valueOf(result));
                  }
               }

               if (var18.contains("log20(")) {
                  String[] values = var18.split("log20\\(");
                  String sValue = values[1].split("\\)")[0];
                  double lValue = Double.parseDouble(sValue);
                  double result = Math.log10(lValue) / Math.log(20.0);
                  var18 = var18.replace("log20(" + sValue + ")", String.valueOf(result));
               }

               if (var18.contains("log")) {
                  String[] values = var18.split("\\(");
                  String l = values[0].replace("log", "");
                  int log = Integer.parseInt(l);
                  double n = Math.log10(level) / Math.log10(log);
                  String[] another = values[1].split("\\)");
                  var18 = String.valueOf(n);
                  if (another.length > 1) {
                     var18 = var18 + another[1];
                  }
               } else if (var18.substring(0, 1).equals("-")) {
                  if (!var18.substring(1, 2).equals("u") && !var18.substring(1, 2).equals("d")) {
                     var18 = "n" + var18.substring(1, var18.length());
                  } else {
                     var18 = "n(" + var18.substring(1, var18.length()) + ")";
                  }
               } else if (var18.substring(0, 1).equals("=")) {
                  var18 = var18.substring(1, var18.length());
               }

               if (var18.equals("2*u") || var18.equals("n2*u")) {
                  var18 = "2*0";
               }

               try {
                  return (int) new CaltechEval(var18.replace("\\r\\n", "")).evaluate();
               } catch (Exception var14) {
                  String dddddd = MapleDataTool.getString(dd);
                  System.out.println(dddddd);
                  throw new RuntimeException(
                        String.format("식 파싱 실패. %s %s %d", source.getParent().getName(), var18, level), var14);
               }
            }
         }
      }
   }

   private static SecondaryStatEffect loadFromData(MapleData source, int sourceid, boolean skill, boolean overTime,
         int level, String variables) {
      SecondaryStatEffect ret = new SecondaryStatEffect();

      try {
         ret.sourceid = sourceid;
         skillId = sourceid;
         ret.skill = skill;
         ret.level = (byte) level;
         if (source == null) {
            return ret;
         }

         ret.duration = parseEval("time", source, -1, variables, level);
         ret.updatableTime = parseEval("updatableTime", source, -1, variables, level);
         ret.subTime = parseEval("subTime", source, -1, variables, level);
         ret.hp = (short) parseEval("hp", source, 0, variables, level);
         ret.hpR = parseEval("hpR", source, 0, variables, level) / 100.0;
         ret.mp = (short) parseEval("mp", source, 0, variables, level);
         ret.mpR = parseEval("mpR", source, 0, variables, level) / 100.0;
         ret.ppRecovery = (short) parseEval("ppRecovery", source, 0, variables, level);
         ret.mhpR = (short) parseEval("mhpR", source, 0, variables, level);
         ret.mmpR = (short) parseEval("mmpR", source, 0, variables, level);
         ret.pddR = (short) parseEval("pddR", source, 0, variables, level);
         ret.mddR = (short) parseEval("mddR", source, 0, variables, level);
         ret.ignoreMobpdpR = (short) parseEval("ignoreMobpdpR", source, 0, variables, level);
         ret.asrR = (short) parseEval("asrR", source, 0, variables, level);
         ret.terR = (short) parseEval("terR", source, 0, variables, level);
         ret.bdR = (short) parseEval("bdR", source, 0, variables, level);
         ret.damR = parseEval("damR", source, 0, variables, level);
         ret.damR_6th = parseEval("damR_6th", source, 0, variables, level);
         ret.MDF = (short) parseEval("MDF", source, 0, variables, level);
         ret.setMPP((short) parseEval("MPP", source, 0, variables, level));
         ret.lv2mhp = (short) parseEval("lv2mhp", source, 0, variables, level);
         ret.lv2mmp = (short) parseEval("lv2mmp", source, 0, variables, level);
         ret.mesoR = (short) parseEval("mesoR", source, 0, variables, level);
         ret.dropR = (short) parseEval("dropR", source, 0, variables, level);
         ret.thaw = (short) parseEval("thaw", source, 0, variables, level);
         ret.padX = (short) parseEval("padX", source, 0, variables, level);
         ret.pddX = (short) parseEval("pddX", source, 0, variables, level);
         ret.mddX = (short) parseEval("mddX", source, 0, variables, level);
         ret.madX = (short) parseEval("madX", source, 0, variables, level);
         ret.dot = (short) parseEval("dot", source, 0, variables, level);
         ret.dotTime = (short) parseEval("dotTime", source, 0, variables, level);
         ret.dotInterval = (short) parseEval("dotInterval", source, 0, variables, level);
         ret.setDotTickDamR((short) parseEval("dotTickDamR", source, 0, variables, level));
         ret.setDotSuperpos((short) parseEval("dotSuperpos", source, 0, variables, level));
         ret.setMaxDotTickDamR((short) parseEval("maxDotTickDamR", source, 0, variables, level));
         ret.bufftimeR = (short) parseEval("bufftimeR", source, 0, variables, level);
         ret.setSummonTimeR((short) parseEval("summonTimeR", source, 0, variables, level));
         ret.gauge = (short) parseEval("gauge", source, 0, variables, level);
         ret.criticaldamageMin = (short) parseEval("criticaldamageMin", source, 0, variables, level);
         ret.criticaldamageMax = (short) parseEval("criticaldamageMax", source, 0, variables, level);
         ret.mpConReduce = (short) parseEval("mpConReduce", source, 0, variables, level);
         ret.soulmpCon = (short) parseEval("soulmpCon", source, 0, variables, level);
         ret.forceCon = (short) parseEval("forceCon", source, 0, variables, level);
         ret.reduceForceR = (short) parseEval("reduceForceR", source, 0, variables, level);
         ret.mpCon = (short) parseEval("mpCon", source, 0, variables, level);
         ret.hpCon = (short) parseEval("hpCon", source, 0, variables, level);
         ret.setAtGauge1Con((short) parseEval("atGauge1Con", source, 0, variables, level));
         ret.setAtGauge2Inc((short) parseEval("atGauge2Inc", source, 0, variables, level));
         ret.atGauge2Con = (short) parseEval("atGauge2Con", source, 0, variables, level);
         ret.comboConAran = (short) parseEval("comboConAran", source, 0, variables, level);
         ret.subProp = (short) parseEval("subProp", source, 100, variables, level);
         ret.prop = (short) parseEval("prop", source, 100, variables, level);
         ret.time = parseEval("time", source, 0, variables, level);
         ret.cooltime = Math.max(0, parseEval("cooltime", source, 0, variables, level));
         ret.cooltimeMS = Math.max(0, parseEval("cooltimeMS", source, 0, variables, level));
         ret.coolTimeR = Math.max(0, parseEval("coolTimeR", source, 0, variables, level));
         ret.ndTime = Math.max(0, parseEval("ndTime", source, 0, variables, level));
         ret.interval = parseEval("interval", source, 0, variables, level);
         ret.expinc = parseEval("expinc", source, 0, variables, level);
         ret.exp = parseEval("exp", source, 0, variables, level);
         ret.range = parseEval("range", source, 0, variables, level);
         ret.morphId = parseEval("morph", source, 0, variables, level);
         ret.cp = parseEval("cp", source, 0, variables, level);
         ret.cosmetic = parseEval("cosmetic", source, 0, variables, level);
         ret.er = (short) parseEval("er", source, 0, variables, level);
         ret.ppCon = parseEval("ppCon", source, 0, variables, level);
         ret.setNocoolProp(parseEval("nocoolProp", source, 0, variables, level));
         ret.stanceProp = parseEval("stanceProp", source, 0, variables, level);
         ret.ppRecovery = (short) parseEval("ppRecovery", source, 0, variables, level);
         ret.slotCount = (byte) parseEval("slotCount", source, 0, variables, level);
         ret.preventslip = (byte) parseEval("preventslip", source, 0, variables, level);
         ret.useLevel = (short) parseEval("useLevel", source, 0, variables, level);
         ret.nuffSkill = parseEval("nuffSkill", source, 0, variables, level);
         ret.mobCount = (byte) parseEval("mobCount", source, 1, variables, level);
         ret.immortal = (byte) parseEval("immortal", source, 0, variables, level);
         ret.iceGageCon = (byte) parseEval("iceGageCon", source, 0, variables, level);
         ret.expR = (byte) parseEval("expR", source, 0, variables, level);
         ret.reqGuildLevel = (byte) parseEval("reqGuildLevel", source, 0, variables, level);
         ret.period = (byte) parseEval("period", source, 0, variables, level);
         ret.type = (byte) parseEval("type", source, 0, variables, level);
         ret.bs = (byte) parseEval("bs", source, 0, variables, level);
         ret.attackCount = (short) parseEval("attackCount", source, 1, variables, level);
         ret.bulletCount = (byte) parseEval("bulletCount", source, 1, variables, level);
         ret.speedMax = parseEval("speedMax", source, 0, variables, level);
         ret.accX = parseEval("accX", source, 0, variables, level);
         ret.mhpX = parseEval("mhpX", source, 0, variables, level);
         ret.mmpX = parseEval("mmpX", source, 0, variables, level);
         ret.hpFX = (short) parseEval("hpFX", source, 0, variables, level);
         ret.mpFX = (short) parseEval("mpFX", source, 0, variables, level);
         ret.strFX = (short) parseEval("strFX", source, 0, variables, level);
         ret.dexFX = (short) parseEval("dexFX", source, 0, variables, level);
         ret.intFX = (short) parseEval("intFX", source, 0, variables, level);
         ret.lukFX = (short) parseEval("lukFX", source, 0, variables, level);
         ret.indieDEX = (short) parseEval("indieDEX", source, 0, variables, level);
         ret.mesoAmountUp = (short) parseEval("mesoAmountUp", source, 0, variables, level);
         ret.fixCoolTime = (short) parseEval("fixCoolTime", source, 0, variables, level);
         ret.indieDamReduceR = (short) parseEval("indieDamReduceR", source, 0, variables, level);
         ret.indieCooltimeReduce = (short) parseEval("indieCooltimeReduce", source, 0, variables, level);
         ret.hpRCon = (short) parseEval("hpRCon", source, 0, variables, level);
         ret.mpRCon = (short) parseEval("mpRCon", source, 0, variables, level);
         ret.damAbsorbShieldR = (short) parseEval("damAbsorbShieldR", source, 0, variables, level);
         ret.targetPlus = (short) parseEval("targetPlus", source, 0, variables, level);
         ret.targetPlus_5th = (short) parseEval("targetPlus_5th", source, 0, variables, level);
         ret.expRPerM = (short) parseEval("expRPerM", source, 0, variables, level);
         ret.killRecoveryR = (short) parseEval("killRecoveryR", source, 0, variables, level);
         ret.ignoreMobDamR = (short) parseEval("ignoreMobDamR", source, 0, variables, level);
         ret.incMobCollectionProp = (short) parseEval("incMobCollectionProp", source, 0, variables, level);
         ret.setMesoG(parseEval("mesoG", source, 0, variables, level));
         ret.setDisCountR(parseEval("disCountR", source, 0, variables, level));
         ret.setPassivePlus(parseEval("passivePlus", source, 0, variables, level));
         ret.setItemUpgradeBonusR(parseEval("itemUpgradeBonusR", source, 0, variables, level));
         ret.setItemCursedProtectR(parseEval("itemCursedProtectR", source, 0, variables, level));
         ret.setItemTUCProtectR(parseEval("itemTUCProtectR", source, 0, variables, level));
         ret.setExpGuild(parseEval("expGuild", source, 0, variables, level));
         int priceUnit = parseEval("priceUnit", source, 0, variables, level);
         if (priceUnit > 0) {
            ret.price = parseEval("price", source, 0, variables, level) * priceUnit;
            ret.extendPrice = parseEval("extendPrice", source, 0, variables, level) * priceUnit;
         } else {
            ret.price = 0;
            ret.extendPrice = 0;
         }

         if (ret.skill) {
            switch (sourceid) {
               case 1100002:
               case 1120013:
               case 1200002:
               case 1201011:
               case 1201012:
               case 1211008:
               case 1221004:
               case 1221011:
               case 1300002:
               case 2111007:
               case 2121003:
               case 2121006:
               case 2211007:
               case 2221006:
               case 2311007:
               case 3100001:
               case 3120008:
               case 3200001:
               case 3201008:
               case 4341009:
               case 5121007:
               case 5321012:
               case 11101002:
               case 12001020:
               case 12100020:
               case 12110020:
               case 12120006:
               case 13101002:
               case 15121001:
               case 21120006:
               case 21121013:
               case 22150004:
               case 22181002:
               case 22181004:
               case 23100006:
               case 23120012:
               case 24100003:
               case 27101202:
               case 27111100:
               case 31111005:
               case 31121001:
               case 32111003:
               case 32111010:
               case 33100009:
               case 51121007:
               case 61111100:
               case 61121100:
               case 61121201:
               case 65111007:
               case 65121101:
                  ret.attackCount = 12;
                  break;
               case 1120017:
                  ret.attackCount = 8;
                  break;
               case 1121008:
               case 3121015:
               case 3221017:
               case 4121016:
               case 4221007:
               case 4331000:
               case 5321000:
               case 11121103:
               case 11121203:
               case 12120011:
               case 12121001:
               case 13121002:
               case 15111022:
               case 15120003:
               case 15121002:
               case 33121002:
               case 35121012:
               case 51121008:
                  ret.attackCount = 10;
                  break;
               case 35111004:
               case 35121005:
               case 35121013:
                  ret.attackCount = 6;
                  ret.bulletCount = 6;
            }
         }

         if (!ret.skill && ret.duration > -1) {
            ret.overTime = true;
         } else {
            ret.duration *= 1000;
            ret.subTime *= 1000;
            ret.overTime = overTime || ret.isMorph() || ret.isPirateMorph() || ret.isAngel();
         }

         ret.statups = new EnumMap<>(SecondaryStatFlag.class);
         ret.mastery = (byte) parseEval("mastery", source, 0, variables, level);
         ret.pad = (short) parseEval("pad", source, 0, variables, level);
         ret.def = (short) parseEval("pdd", source, 0, variables, level);
         ret.mad = (short) parseEval("mad", source, 0, variables, level);
         ret.mdef = (short) parseEval("mdd", source, 0, variables, level);
         ret.emhp = (short) parseEval("emhp", source, 0, variables, level);
         ret.emmp = (short) parseEval("emmp", source, 0, variables, level);
         ret.epad = (short) parseEval("epad", source, 0, variables, level);
         ret.emad = (short) parseEval("emad", source, 0, variables, level);
         ret.epdd = (short) parseEval("epdd", source, 0, variables, level);
         ret.emdd = (short) parseEval("emdd", source, 0, variables, level);
         ret.acc = (short) parseEval("acc", source, 0, variables, level);
         ret.avoid = (short) parseEval("eva", source, 0, variables, level);
         ret.speed = (short) parseEval("speed", source, 0, variables, level);
         ret.jump = (short) parseEval("jump", source, 0, variables, level);
         ret.evaR = (short) parseEval("evaR", source, 0, variables, level);
         ret.psdSpeed = (short) parseEval("psdSpeed", source, 0, variables, level);
         ret.psdJump = (short) parseEval("psdJump", source, 0, variables, level);
         ret.indieCr = (short) parseEval("indieCr", source, 0, variables, level);
         ret.indieCD = (short) parseEval("indieCD", source, 0, variables, level);
         ret.indiePad = (short) parseEval("indiePad", source, 0, variables, level);
         ret.indiePadR = (short) parseEval("indiePadR", source, 0, variables, level);
         ret.indieMad = (short) parseEval("indieMad", source, 0, variables, level);
         ret.indieMadR = (short) parseEval("indieMadR", source, 0, variables, level);
         ret.indiePMd = (short) parseEval("indiePMd", source, 0, variables, level);
         ret.indieMhp = (short) parseEval("indieMhp", source, 0, variables, level);
         ret.indieMmp = (short) parseEval("indieMmp", source, 0, variables, level);
         ret.indieBooster = (short) parseEval("indieBooster", source, 0, variables, level);
         ret.indieSpeed = (short) parseEval("indieSpeed", source, 0, variables, level);
         ret.indieJump = (short) parseEval("indieJump", source, 0, variables, level);
         ret.indieAcc = (short) parseEval("indieAcc", source, 0, variables, level);
         ret.indieEva = (short) parseEval("indieEva", source, 0, variables, level);
         ret.indieEvaR = (short) parseEval("indieEvaR", source, 0, variables, level);
         ret.indiePdd = (short) parseEval("indiePdd", source, 0, variables, level);
         ret.indieMdd = (short) parseEval("indieMdd", source, 0, variables, level);
         ret.indiePddR = (short) parseEval("indiePddR", source, 0, variables, level);
         ret.indieDamR = (short) parseEval("indieDamR", source, 0, variables, level);
         ret.indieBDR = (short) parseEval("indieBDR", source, 0, variables, level);
         ret.indieIgnoreMobpdpR = (short) parseEval("indieIgnoreMobpdpR", source, 0, variables, level);
         ret.indieMaxDamageOver = (short) parseEval("indieMaxDamageOver", source, 0, variables, level);
         ret.indieAllStat = (short) parseEval("indieAllStat", source, 0, variables, level);
         ret.indieStance = (short) parseEval("indieStance", source, 0, variables, level);
         ret.indieMhpR = (short) parseEval("indieMhpR", source, 0, variables, level);
         ret.indieMmpR = (short) parseEval("indieMmpR", source, 0, variables, level);
         ret.indieAsrR = (short) parseEval("indieAsrR", source, 0, variables, level);
         ret.indieTerR = (short) parseEval("indieTerR", source, 0, variables, level);
         ret.indieStatRBasic = (short) parseEval("indieStatRBasic", source, 0, variables, level);
         ret.setCancelableTime((short) parseEval("cancelableTime", source, 0, variables, level));
         ret.dotHealHPPerSecondR = (short) parseEval("dotHealHPPerSecondR", source, 0, variables, level);
         ret.dotHealMPPerSecondR = (short) parseEval("dotHealMPPerSecondR", source, 0, variables, level);
         ret.onActive = (short) parseEval("onActive", source, 0, variables, level);
         ret.str = (short) parseEval("str", source, 0, variables, level);
         ret.dex = (short) parseEval("dex", source, 0, variables, level);
         ret.int_ = (short) parseEval("int", source, 0, variables, level);
         ret.luk = (short) parseEval("luk", source, 0, variables, level);
         ret.strX = (short) parseEval("strX", source, 0, variables, level);
         ret.dexX = (short) parseEval("dexX", source, 0, variables, level);
         ret.intX = (short) parseEval("intX", source, 0, variables, level);
         ret.lukX = (short) parseEval("lukX", source, 0, variables, level);
         ret.expBuff = parseEval("expBuff", source, 0, variables, level);
         ret.cashup = parseEval("cashBuff", source, 0, variables, level);
         ret.itemup = parseEval("itemupbyitem", source, 0, variables, level);
         ret.mesoup = parseEval("mesoupbyitem", source, 0, variables, level);
         ret.berserk = parseEval("berserk", source, 0, variables, level);
         ret.berserk2 = parseEval("berserk2", source, 0, variables, level);
         ret.lifeId = (short) parseEval("lifeId", source, 0, variables, level);
         ret.inflation = (short) parseEval("inflation", source, 0, variables, level);
         ret.imhp = (short) parseEval("imhp", source, 0, variables, level);
         ret.immp = (short) parseEval("immp", source, 0, variables, level);
         ret.illusion = parseEval("illusion", source, 0, variables, level);
         ret.consumeOnPickup = parseEval("consumeOnPickup", source, 0, variables, level);
         ret.indiePMdR = (short) parseEval("indiePMdR", source, 0, variables, level);
         ret.morph = (short) parseEval("morph", source, 0, variables, level);
         if (ret.consumeOnPickup == 1 && parseEval("party", source, 0, variables, level) > 0) {
            ret.consumeOnPickup = 2;
         }

         ret.charColor = 0;
         String cColor = MapleDataTool.getString("charColor", source, null);
         if (cColor != null) {
            ret.charColor = ret.charColor | Integer.parseInt("0x" + cColor.substring(0, 2));
            ret.charColor = ret.charColor | Integer.parseInt("0x" + cColor.substring(2, 4) + "00");
            ret.charColor = ret.charColor | Integer.parseInt("0x" + cColor.substring(4, 6) + "0000");
            ret.charColor = ret.charColor | Integer.parseInt("0x" + cColor.substring(6, 8) + "000000");
         }

         ret.traits = new EnumMap<>(MapleTrait.MapleTraitType.class);

         for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            int expz = parseEval(t.name() + "EXP", source, 0, variables, level);
            if (expz != 0) {
               ret.traits.put(t, expz);
            }
         }

         ret.recipe = parseEval("recipe", source, 0, variables, level);
         ret.recipeUseCount = (byte) parseEval("recipeUseCount", source, 0, variables, level);
         ret.recipeValidDay = (byte) parseEval("recipeValidDay", source, 0, variables, level);
         ret.reqSkillLevel = (byte) parseEval("reqSkillLevel", source, 0, variables, level);
         ret.powerCon = (byte) parseEval("powerCon", source, 0, variables, level);
         ret.effectedOnAlly = (byte) parseEval("effectedOnAlly", source, 0, variables, level);
         ret.effectedOnEnemy = (byte) parseEval("effectedOnEnemy", source, 0, variables, level);
         List<SecondaryStatFlag> cure = new ArrayList<>(5);
         if (parseEval("poison", source, 0, variables, level) > 0) {
            cure.add(SecondaryStatFlag.Poison);
         }

         if (parseEval("seal", source, 0, variables, level) > 0) {
            cure.add(SecondaryStatFlag.Seal);
         }

         if (parseEval("darkness", source, 0, variables, level) > 0) {
            cure.add(SecondaryStatFlag.Darkness);
         }

         if (parseEval("weakness", source, 0, variables, level) > 0) {
            cure.add(SecondaryStatFlag.Weakness);
         }

         if (parseEval("curse", source, 0, variables, level) > 0) {
            cure.add(SecondaryStatFlag.Curse);
         }

         ret.cureDebuffs = cure;
         ret.petsCanConsume = new ArrayList<>();
         int i = 0;

         while (true) {
            int dd = parseEval(String.valueOf(i), source, 0, variables, level);
            if (dd <= 0) {
               MapleData mdd = source.getChildByPath("0");
               if (mdd != null && mdd.getChildren().size() > 0) {
                  ret.mobSkill = (short) parseEval("mobSkill", mdd, 0, variables, level);
                  ret.mobSkillLevel = (short) parseEval("level", mdd, 0, variables, level);
               } else {
                  ret.mobSkill = 0;
                  ret.mobSkillLevel = 0;
               }

               MapleData pd = source.getChildByPath("randomPickup");
               if (pd != null) {
                  ret.randomPickup = new ArrayList<>();

                  for (MapleData p : pd) {
                     ret.randomPickup.add(MapleDataTool.getInt(p));
                  }
               }

               MapleData ltd = source.getChildByPath("lt");
               if (ltd != null) {
                  ret.lt = (Point) ltd.getData();
                  ret.rb = (Point) source.getChildByPath("rb").getData();
               }

               MapleData ltd2 = source.getChildByPath("lt2");
               if (ltd2 != null) {
                  ret.lt2 = (Point) ltd2.getData();
                  ret.rb2 = (Point) source.getChildByPath("rb2").getData();
               }

               MapleData ltd3 = source.getChildByPath("lt3");
               if (ltd3 != null) {
                  ret.lt3 = (Point) ltd3.getData();
                  ret.rb3 = (Point) source.getChildByPath("rb3").getData();
               }

               MapleData ltc = source.getChildByPath("con");
               if (ltc != null) {
                  ret.availableMap = new ArrayList<>();

                  for (MapleData ltb : ltc) {
                     ret.availableMap.add(new Pair<>(MapleDataTool.getInt("sMap", ltb, 0),
                           MapleDataTool.getInt("eMap", ltb, 999999999)));
                  }
               }

               ret.fatigueChange = 0;
               int totalprob = 0;
               MapleData lta = source.getChildByPath("reward");
               if (lta != null) {
                  ret.rewardMeso = parseEval("meso", lta, 0, variables, level);
                  MapleData ltz = lta.getChildByPath("case");
                  if (ltz != null) {
                     ret.rewardItem = new ArrayList<>();

                     for (MapleData lty : ltz) {
                        ret.rewardItem
                              .add(new Triple<>(MapleDataTool.getInt("id", lty, 0),
                                    MapleDataTool.getInt("count", lty, 0), MapleDataTool.getInt("prop", lty, 0)));
                        totalprob += MapleDataTool.getInt("prob", lty, 0);
                     }
                  }
               } else {
                  ret.rewardMeso = 0;
               }

               ret.totalprob = totalprob;
               ret.cr = parseEval("cr", source, 0, variables, level);
               ret.t = parseEvalDouble("t", source, 0, variables, level);
               ret.u = parseEval("u", source, 0, variables, level);
               ret.u2 = parseEval("u2", source, 0, variables, level);
               ret.v = parseEval("v", source, 0, variables, level);
               ret.v2 = parseEval("v2", source, 0, variables, level);
               ret.w = parseEval("w", source, 0, variables, level);
               ret.w2 = parseEval("w2", source, 0, variables, level);
               ret.x = parseEval("x", source, 0, variables, level);
               ret.y = parseEval("y", source, 0, variables, level);
               ret.z = parseEval("z", source, 0, variables, level);
               ret.s = parseEval("s", source, 0, variables, level);
               ret.s2 = parseEval("s2", source, 0, variables, level);
               ret.q = parseEval("q", source, 0, variables, level);
               ret.q2 = parseEval("q2", source, 0, variables, level);
               ret.damage = (short) parseEval("damage", source, 100, variables, level);
               ret.PVPdamage = (short) parseEval("PVPdamage", source, 0, variables, level);
               ret.incPVPdamage = (short) parseEval("incPVPDamage", source, 0, variables, level);
               ret.setIndieExp((short) parseEval("indieExp", source, 0, variables, level));
               ret.selfDestruction = (short) parseEval("selfDestruction", source, 0, variables, level);
               ret.bulletConsume = parseEval("bulletConsume", source, 0, variables, level);
               ret.moneyCon = parseEval("moneyCon", source, 0, variables, level);
               ret.itemCon = parseEval("itemCon", source, 0, variables, level);
               ret.itemConNo = parseEval("itemConNo", source, 0, variables, level);
               ret.moveTo = parseEval("moveTo", source, -1, variables, level);
               ret.monsterStatus = new EnumMap<>(MobTemporaryStatFlag.class);
               if (ret.isAddedInt()
                     || ret.isAddedLong()
                     || ret.overTime && ret.getSummonMovementType() == null && !ret.isMonsterRiding()
                           && sourceid != 80003062) {
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.PAD, Integer.valueOf(ret.pad));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.DEF, Integer.valueOf(ret.def));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.MAD, Integer.valueOf(ret.mad));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.Jump, Integer.valueOf(ret.jump));
                  if (sourceid != 2450124 && sourceid != 2023882) {
                     addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.ExpBuffRate,
                           Math.max(0, ret.expBuff - 100));
                  }

                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.EnhancedMaxHP,
                        Integer.valueOf(ret.emhp));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.EnhancedMaxMP,
                        Integer.valueOf(ret.emmp));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.EnhancedPAD, Integer.valueOf(ret.epad));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.EnhancedMAD, Integer.valueOf(ret.emad));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.EnhancedDEF, Integer.valueOf(ret.epdd));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.EnhancedDEF, Integer.valueOf(ret.emdd));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.STR, Integer.valueOf(ret.str));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.DEX, Integer.valueOf(ret.dex));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.INT, Integer.valueOf(ret.int_));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.LUK, Integer.valueOf(ret.luk));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.indiePAD,
                        Integer.valueOf(ret.indiePad));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.indieMAD,
                        Integer.valueOf(ret.indieMad));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.indieMHP, Integer.valueOf(ret.imhp));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.indieMMP, Integer.valueOf(ret.immp));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.indieMHPR,
                        Integer.valueOf(ret.indieMhpR));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.indieMMPR,
                        Integer.valueOf(ret.indieMmpR));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.indieAsrR,
                        Integer.valueOf(ret.indieAsrR));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.indieMHP,
                        Integer.valueOf(ret.indieMhp));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.indieMMP,
                        Integer.valueOf(ret.indieMmp));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.indieJump,
                        Integer.valueOf(ret.indieJump));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.indieSpeed,
                        Integer.valueOf(ret.indieSpeed));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.indieACC,
                        Integer.valueOf(ret.indieAcc));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.indieEVA,
                        Integer.valueOf(ret.indieEva));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.indieEVA,
                        Integer.valueOf(ret.indieEva));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.indieAllStat,
                        Integer.valueOf(ret.indieAllStat));
                  addBuffStatPairToListIfNotZero(ret.statups, SecondaryStatFlag.indieEXP,
                        Integer.valueOf(ret.getIndieExp()));
               }

               if (ret.skill) {
                  switch (sourceid) {
                     case 1001003:
                        ret.statups.put(SecondaryStatFlag.indieDEF, Integer.valueOf(ret.indiePdd));
                        break;
                     case 1101004:
                     case 1201004:
                     case 1301004:
                     case 2101008:
                     case 2201010:
                     case 2301008:
                     case 3101002:
                     case 3201002:
                     case 3301010:
                     case 4101003:
                     case 4201002:
                     case 4301002:
                     case 4311009:
                     case 5101006:
                     case 5201003:
                     case 5301002:
                     case 11101001:
                     case 11101024:
                     case 12101004:
                     case 13101001:
                     case 13101023:
                     case 14101022:
                     case 15101002:
                     case 15101022:
                     case 22101020:
                     case 22111020:
                     case 23101002:
                     case 24101005:
                     case 27101004:
                     case 31001001:
                     case 31201002:
                     case 32101005:
                     case 33001003:
                     case 33101012:
                     case 35101006:
                     case 37101003:
                     case 51101003:
                     case 151101005:
                     case 152101007:
                     case 164101005:
                        ret.statups.put(SecondaryStatFlag.Booster, ret.x);
                        break;
                     case 1101006:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        ret.statups.put(SecondaryStatFlag.PowerGuard, ret.x);
                        break;
                     case 1111005:
                     case 1111008:
                     case 1121001:
                     case 1201013:
                     case 1211002:
                     case 1221001:
                     case 1321001:
                     case 2211003:
                     case 2221006:
                     case 2311004:
                     case 3120010:
                     case 4121008:
                     case 4211002:
                     case 4221007:
                     case 4331005:
                     case 5101002:
                     case 5101003:
                     case 5111002:
                     case 5121004:
                     case 5121005:
                     case 5121007:
                     case 5201004:
                     case 5301001:
                     case 5310008:
                     case 5311001:
                     case 5311002:
                     case 9101020:
                     case 15101005:
                     case 21110006:
                     case 22131000:
                     case 22141001:
                     case 22151001:
                     case 22181001:
                     case 27101101:
                     case 31101002:
                     case 31111001:
                     case 32101001:
                     case 32111011:
                     case 32121004:
                     case 33101001:
                     case 33101002:
                     case 33111002:
                     case 33121002:
                     case 35101003:
                     case 35111015:
                     case 51111007:
                        ret.monsterStatus.put(MobTemporaryStatFlag.STUN, 1);
                        break;
                     case 1121000:
                     case 1221000:
                     case 1321000:
                     case 2121000:
                     case 2221000:
                     case 2321000:
                     case 3121000:
                     case 3221000:
                     case 3321023:
                     case 4121000:
                     case 4221000:
                     case 4341000:
                     case 5121000:
                     case 5221000:
                     case 5321005:
                     case 11121000:
                     case 12121000:
                     case 13121000:
                     case 14121000:
                     case 15121000:
                     case 21121000:
                     case 22171068:
                     case 23121005:
                     case 24121008:
                     case 25121108:
                     case 27121009:
                     case 31121004:
                     case 31221008:
                     case 32121007:
                     case 33121007:
                     case 35121007:
                     case 36121008:
                     case 37121006:
                     case 51121005:
                     case 61121014:
                     case 63121009:
                     case 64121004:
                     case 65121009:
                     case 100001268:
                     case 142121016:
                     case 151121005:
                     case 152121009:
                     case 154121005:
                     case 155121008:
                     case 162121023:
                     case 164121009:
                        ret.statups.put(SecondaryStatFlag.BasicStatUp, ret.x);
                        break;
                     case 1121010:
                        ret.statups.put(SecondaryStatFlag.Enrage, ret.x);
                        ret.statups.put(SecondaryStatFlag.EnrageCr, ret.z);
                        ret.statups.put(SecondaryStatFlag.EnrageCrDamMin, ret.y);
                        ret.duration = 2100000000;
                        break;
                     case 1121016:
                     case 1221014:
                     case 1321014:
                     case 11111008:
                     case 51111005:
                        ret.monsterStatus.put(MobTemporaryStatFlag.MAGIC_CRASH, 1);
                        break;
                     case 1121053:
                     case 1221053:
                     case 1321053:
                     case 2121053:
                     case 2221053:
                     case 2321053:
                     case 3121053:
                     case 3221053:
                     case 3321041:
                     case 4121053:
                     case 4221053:
                     case 4341053:
                     case 5121053:
                     case 5221053:
                     case 5321053:
                     case 11121053:
                     case 12121053:
                     case 13121053:
                     case 14121053:
                     case 15121053:
                     case 21121053:
                     case 22171082:
                     case 23121053:
                     case 24121053:
                     case 25121132:
                     case 27121053:
                     case 31121053:
                     case 31221053:
                     case 32121053:
                     case 33121053:
                     case 35121053:
                     case 37121053:
                     case 51121053:
                        ret.statups.clear();
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        break;
                     case 1210016:
                        ret.statups.put(SecondaryStatFlag.BlessingArmor, ret.x + 1);
                        ret.statups.put(SecondaryStatFlag.BlessingArmorIncPAD, Integer.valueOf(ret.epad));
                        break;
                     case 1211010:
                        ret.hpR = ret.x / 100.0;
                        break;
                     case 1211011:
                     case 400001004:
                        ret.statups.put(SecondaryStatFlag.CombatOrders, ret.x);
                        break;
                     case 1221015:
                        ret.statups.put(SecondaryStatFlag.indiePMDR, Integer.valueOf(ret.indiePMdR));
                        break;
                     case 1221054:
                        ret.statups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
                        break;
                     case 1301006:
                        ret.statups.put(SecondaryStatFlag.MAD, Integer.valueOf(ret.def));
                        ret.overTime = true;
                        break;
                     case 1301007:
                     case 9001008:
                     case 9101008:
                        ret.statups.put(SecondaryStatFlag.MaxHP, ret.x);
                        ret.statups.put(SecondaryStatFlag.MaxMP, ret.x);
                        break;
                     case 1301013:
                        ret.statups.put(SecondaryStatFlag.Beholder, Integer.valueOf(ret.level));
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 1311006:
                     case 21111009:
                        ret.hpR = -ret.x / 100.0;
                        break;
                     case 1311008:
                        ret.statups.put(SecondaryStatFlag.STR, ret.x);
                        break;
                     case 1321015:
                        ret.hpR = ret.y;
                        ret.statups.put(SecondaryStatFlag.indiePMDR, ret.v);
                        ret.statups.put(SecondaryStatFlag.IgnoreMobPdpR, ret.x);
                        ret.statups.put(SecondaryStatFlag.indieBDR, Integer.valueOf(ret.indieBDR));
                        break;
                     case 1321020:
                        ret.statups.put(SecondaryStatFlag.ReincarnationAccept, 1);
                        ret.duration = 2100000000;
                        break;
                     case 2001002:
                     case 12001001:
                     case 22001012:
                        ret.statups.put(SecondaryStatFlag.MagicGuard, ret.x);
                        ret.duration = 2100000000;
                        break;
                     case 2101001:
                     case 2201001:
                        ret.statups.put(SecondaryStatFlag.indieMAD, Integer.valueOf(ret.indieMad));
                        break;
                     case 2101003:
                     case 2201003:
                     case 12101001:
                     case 90001002:
                        ret.monsterStatus.put(MobTemporaryStatFlag.SPEED, ret.x);
                        break;
                     case 2101010:
                        ret.statups.put(SecondaryStatFlag.WizardIgnite, 1);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 2111004:
                     case 2211004:
                     case 12111002:
                     case 90001005:
                        ret.monsterStatus.put(MobTemporaryStatFlag.SEAL, 1);
                        break;
                     case 2111007:
                     case 2211007:
                     case 2311007:
                     case 22161005:
                     case 32111010:
                        ret.mpCon = (short) ret.y;
                        ret.duration = 2100000000;
                        ret.statups.put(SecondaryStatFlag.TeleportMasteryOn, ret.y);
                        ret.monsterStatus.put(MobTemporaryStatFlag.STUN, 1);
                        break;
                     case 2111008:
                     case 2211008:
                     case 12101005:
                     case 22141016:
                        ret.statups.put(SecondaryStatFlag.ElementReset, ret.x);
                        break;
                     case 2111011:
                        ret.statups.put(SecondaryStatFlag.AntiMagicShell, ret.y);
                        ret.duration = 2100000000;
                        break;
                     case 2111016:
                     case 2211017:
                     case 2221045:
                     case 2311016:
                     case 27111008:
                     case 32111021:
                     case 32120044:
                     case 152111014:
                        ret.statups.put(SecondaryStatFlag.TeleportMasteryRange, ret.x);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 2121004:
                     case 2221004:
                     case 2321004:
                        ret.statups.put(SecondaryStatFlag.Infinity, 1);
                        break;
                     case 2121005:
                     case 3101007:
                     case 3111005:
                     case 3121006:
                     case 3201007:
                     case 3211005:
                     case 23111009:
                     case 23111010:
                     case 23111011:
                     case 33111005:
                        ret.monsterStatus.put(MobTemporaryStatFlag.STUN, 1);
                        break;
                     case 2121006:
                        ret.monsterStatus.put(MobTemporaryStatFlag.STUN, 1);
                        break;
                     case 2121054:
                        ret.statups.put(SecondaryStatFlag.FireAura, 1);
                        ret.duration = 2100000000;
                        break;
                     case 2201009:
                        ret.statups.put(SecondaryStatFlag.ChillingStep, Integer.valueOf(ret.prop));
                        ret.duration = 2100000000;
                     case 2221005:
                     case 24121054:
                     case 400001066:
                        break;
                     case 2221054:
                     case 2221055:
                        if (sourceid == 2221055) {
                           ret.statups.put(SecondaryStatFlag.IceAuraTornado, 1);
                        } else {
                           ret.statups.put(SecondaryStatFlag.IceAura, 1);
                        }

                        ret.statups.put(SecondaryStatFlag.indieAsrR, ret.z);
                        ret.statups.put(SecondaryStatFlag.indieTerR, ret.w);
                        ret.duration = 2100000000;
                        break;
                     case 2301003:
                        ret.statups.put(SecondaryStatFlag.NotDamaged, ret.x);
                        break;
                     case 2301004:
                     case 9001003:
                     case 9101003:
                        ret.statups.put(SecondaryStatFlag.Bless, Integer.valueOf(ret.level));
                        break;
                     case 2311002:
                     case 3101004:
                     case 3201004:
                     case 13101003:
                     case 33101003:
                     case 35101005:
                        if (skillId == 33101003) {
                           ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        } else {
                           ret.statups.put(SecondaryStatFlag.EnhancedPAD, Integer.valueOf(ret.epad));
                        }

                        ret.statups.put(SecondaryStatFlag.SoulArrow, ret.x);
                        ret.statups.put(SecondaryStatFlag.Concentration, 1);
                        break;
                     case 2311012:
                        ret.statups.put(SecondaryStatFlag.AntiMagicShell, ret.u);
                        ret.duration = 2100000000;
                        break;
                     case 2321054:
                        ret.statups.put(SecondaryStatFlag.VengeanceOfAngel, 1);
                        ret.duration = 2100000000;
                        break;
                     case 3010001:
                        ret.statups.put(SecondaryStatFlag.indieFlyAcc, 1);
                        ret.statups.put(SecondaryStatFlag.indieCR, 2);
                        break;
                     case 3111000:
                     case 3121008:
                     case 13111001:
                        ret.statups.put(SecondaryStatFlag.pyramidFireBuff, ret.x);
                        break;
                     case 3111011:
                        ret.statups.put(SecondaryStatFlag.indiePMDR, Integer.valueOf(ret.indiePMdR));
                        ret.duration = 2100000000;
                        break;
                     case 3111015:
                        ret.statups.put(SecondaryStatFlag.FlashMirage, 1);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 3120006:
                     case 3220005:
                        ret.statups.put(SecondaryStatFlag.CrewCommandership, 1);
                        break;
                     case 3121007:
                     case 3221006:
                        ret.statups.put(SecondaryStatFlag.indieDEX, Integer.valueOf(ret.indieDEX));
                        ret.statups.put(SecondaryStatFlag.IllusionStep, ret.x);
                        break;
                     case 3121054:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        ret.statups.put(SecondaryStatFlag.indieBDR, ret.y);
                        break;
                     case 3201008:
                     case 3211003:
                     case 5211005:
                     case 21120006:
                     case 22121000:
                     case 90001006:
                        ret.monsterStatus.put(MobTemporaryStatFlag.FREEZE, 1);
                        break;
                     case 3210013:
                     case 101120109:
                        ret.statups.put(SecondaryStatFlag.PowerTransferGauge, ret.x);
                        break;
                     case 3211011:
                        ret.statups.put(SecondaryStatFlag.TerR, Integer.valueOf(ret.asrR));
                        ret.statups.put(SecondaryStatFlag.AsrR, Integer.valueOf(ret.terR));
                        ret.statups.put(SecondaryStatFlag.AntiMagicShell, Integer.valueOf(ret.asrR));
                        break;
                     case 3211012:
                        ret.duration = 2100000000;
                        break;
                     case 3221005:
                        ret.monsterStatus.put(MobTemporaryStatFlag.FREEZE, 1);
                        break;
                     case 3221014:
                        ret.monsterStatus.put(MobTemporaryStatFlag.STUN, 1);
                        break;
                     case 3221022:
                        ret.statups.put(SecondaryStatFlag.indiePMDR, ret.z);
                        ret.statups.put(SecondaryStatFlag.indieIgnoreMobPdpR, ret.x);
                        break;
                     case 3221054:
                        int x = ret.x;
                        int y = ret.y;
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        ret.statups.put(SecondaryStatFlag.indieIgnoreMobPdpR, Integer.valueOf(ret.indieIgnoreMobpdpR));
                        ret.statups.put(SecondaryStatFlag.BullsEye, x << 8 | y & 0xFF);
                        break;
                     case 3300000:
                        ret.statups.put(SecondaryStatFlag.RelicCharge, 1);
                        ret.statups.put(SecondaryStatFlag.AncientGuardians, 0);
                        break;
                     case 3300001:
                        ret.statups.put(SecondaryStatFlag.indieFlyAcc, 1);
                        ret.statups.put(SecondaryStatFlag.indiePddR, 2);
                        break;
                     case 3310000:
                        ret.statups.put(SecondaryStatFlag.indieFlyAcc, 1);
                        ret.statups.put(SecondaryStatFlag.indieAsrR, 2);
                        break;
                     case 3311012:
                        ret.statups.put(SecondaryStatFlag.indieAsrR, 30);
                        break;
                     case 4001002:
                     case 14001002:
                        ret.monsterStatus.put(MobTemporaryStatFlag.PAD, ret.x);
                        ret.monsterStatus.put(MobTemporaryStatFlag.PDR, ret.y);
                        break;
                     case 4001003:
                     case 14001023:
                        ret.statups.put(SecondaryStatFlag.DarkSight, ret.x);
                        break;
                     case 4001005:
                     case 4301003:
                     case 14001022:
                        ret.statups.put(SecondaryStatFlag.Jump, Integer.valueOf(ret.jump));
                        ret.statups.put(SecondaryStatFlag.Speed, Integer.valueOf(ret.speed));
                        break;
                     case 4101011:
                     case 4120018:
                     case 4120019:
                     case 25101009:
                        ret.statups.put(SecondaryStatFlag.ForceAtomOnOff, 1);
                        ret.duration = 2100000000;
                        break;
                     case 4111001:
                        ret.statups.put(SecondaryStatFlag.MesoUp, ret.x);
                        break;
                     case 4111002:
                     case 4211008:
                     case 4331002:
                     case 14111054:
                        ret.statups.put(SecondaryStatFlag.ShadowPartner, Integer.valueOf(ret.level));
                        break;
                     case 4111003:
                     case 14111001:
                        ret.monsterStatus.put(MobTemporaryStatFlag.WEB, 1);
                        break;
                     case 4121004:
                     case 4221004:
                        ret.monsterStatus.put(MobTemporaryStatFlag.NINJA_AMBUSH, Integer.valueOf(ret.damage));
                        break;
                     case 4121014:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        break;
                     case 4121017:
                     case 4221003:
                     case 33121005:
                        ret.monsterStatus.put(MobTemporaryStatFlag.SHOWDOWN, ret.x);
                        break;
                     case 4121054:
                        ret.statups.put(SecondaryStatFlag.BleedingToxin, 1);
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        break;
                     case 4201011:
                        ret.statups.put(SecondaryStatFlag.EnhanceAssassinate, ret.x);
                        break;
                     case 4201017:
                        ret.statups.put(SecondaryStatFlag.Steal, 1);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 4211003:
                     case 4221018:
                        ret.statups.put(SecondaryStatFlag.PickPocket, 1);
                        ret.duration = 2100000000;
                        break;
                     case 4221013:
                        ret.statups.put(SecondaryStatFlag.indiePAD, ret.x);
                        break;
                     case 4221054:
                        ret.statups.put(SecondaryStatFlag.FlipTheCoin, 1);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 4311005:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        break;
                     case 4321000:
                        ret.duration = 1000;
                        ret.statups.put(SecondaryStatFlag.DashSpeed, 100 + ret.x);
                        ret.statups.put(SecondaryStatFlag.DashJump, ret.y);
                        break;
                     case 4330001:
                        ret.statups.put(SecondaryStatFlag.DarkSight, Integer.valueOf(ret.level));
                        break;
                     case 4330009:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        ret.statups.put(SecondaryStatFlag.CriticalBuff, 100);
                        break;
                     case 4331003:
                        ret.duration = 2100000000;
                        break;
                     case 4341002:
                        ret.duration = 60000;
                        ret.hpR = -ret.x / 100.0;
                        ret.statups.put(SecondaryStatFlag.FinalCut, ret.y);
                        break;
                     case 4341007:
                        ret.statups.put(SecondaryStatFlag.EnhancedPAD, Integer.valueOf(ret.prop));
                        break;
                     case 4341052:
                        ret.statups.put(SecondaryStatFlag.Asura, ret.x);
                        break;
                     case 4341054:
                        ret.overTime = true;
                        ret.statups.put(SecondaryStatFlag.DamR, 10);
                        ret.statups.put(SecondaryStatFlag.DualBladeFinal, 1);
                        break;
                     case 5001005:
                        ret.statups.put(SecondaryStatFlag.DashSpeed, ret.x);
                        ret.statups.put(SecondaryStatFlag.DashJump, ret.y);
                        break;
                     case 5011002:
                        ret.monsterStatus.put(MobTemporaryStatFlag.SPEED, ret.z);
                        break;
                     case 5101017:
                        ret.statups.put(SecondaryStatFlag.SeaSerpent, 1);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 5111010:
                        ret.statups.put(SecondaryStatFlag.DamAbsorbShield, ret.x);
                        break;
                     case 5121009:
                     case 10008006:
                     case 15121005:
                     case 20008006:
                     case 20018006:
                     case 20028006:
                     case 20038006:
                     case 20048006:
                     case 20058006:
                     case 30008006:
                     case 30018006:
                     case 30028006:
                     case 50008006:
                     case 60008006:
                     case 60018006:
                     case 60028006:
                     case 64101003:
                     case 100008006:
                     case 130008006:
                     case 140008006:
                     case 150008006:
                     case 400001006:
                        ret.statups.put(SecondaryStatFlag.SpeedInfusion, ret.x);
                        break;
                     case 5121015:
                        ret.statups.put(SecondaryStatFlag.indiePadR, Integer.valueOf(ret.indiePadR));
                        break;
                     case 5121052:
                        ret.statups.put(SecondaryStatFlag.indieSummon, 1);
                        break;
                     case 5121054:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        break;
                     case 5211006:
                     case 5220011:
                     case 22151002:
                        ret.duration = 2100000000;
                        break;
                     case 5221009:
                        ret.monsterStatus.put(MobTemporaryStatFlag.M_COUNTER, 1);
                        break;
                     case 5221018:
                        ret.statups.put(SecondaryStatFlag.indieEVA, Integer.valueOf(ret.indieEva));
                        ret.statups.put(SecondaryStatFlag.indieAsrR, Integer.valueOf(ret.indieAsrR));
                        ret.statups.put(SecondaryStatFlag.indieTerR, Integer.valueOf(ret.indieTerR));
                        ret.statups.put(SecondaryStatFlag.indieStance, Integer.valueOf(ret.indieStance));
                        ret.statups.put(SecondaryStatFlag.indiePadR, Integer.valueOf(ret.indiePadR));
                        break;
                     case 5221054:
                        ret.statups.put(SecondaryStatFlag.IgnoreMobDamR, ret.w);
                        ret.statups.put(SecondaryStatFlag.UntiringNectar, ret.w);
                        ret.hpR = ret.z;
                        break;
                     case 5301003:
                     case 5320008:
                        ret.statups.put(SecondaryStatFlag.indieMHP, Integer.valueOf(ret.indieMhp));
                        ret.statups.put(SecondaryStatFlag.indieMMP, Integer.valueOf(ret.indieMmp));
                        ret.statups.put(SecondaryStatFlag.indieACC, Integer.valueOf(ret.indieAcc));
                        ret.statups.put(SecondaryStatFlag.indieEVA, Integer.valueOf(ret.indieEva));
                        ret.statups.put(SecondaryStatFlag.indieJump, Integer.valueOf(ret.indieJump));
                        ret.statups.put(SecondaryStatFlag.indieSpeed, Integer.valueOf(ret.indieSpeed));
                        ret.statups.put(SecondaryStatFlag.indieAllStat, Integer.valueOf(ret.indieAllStat));
                        break;
                     case 5311004:
                        ret.statups.put(SecondaryStatFlag.Roulette, 0);
                        break;
                     case 5321010:
                        ret.statups.put(SecondaryStatFlag.indieStance, ret.x);
                        break;
                     case 5321054:
                        ret.statups.put(SecondaryStatFlag.BuckShot, ret.x);
                        ret.duration = Integer.MAX_VALUE;
                        ret.statups.put(SecondaryStatFlag.indieBooster, -1);
                        break;
                     case 9001002:
                     case 9101002:
                        ret.statups.put(SecondaryStatFlag.HolySymbol, ret.x);
                        break;
                     case 9001004:
                     case 9101004:
                        ret.duration = 2100000000;
                        ret.statups.put(SecondaryStatFlag.DarkSight, 1);
                        break;
                     case 10001075:
                        ret.statups.put(SecondaryStatFlag.IllusionStep, ret.x);
                        break;
                     case 10008002:
                     case 13121005:
                     case 33121004:
                     case 50008002:
                     case 60008002:
                     case 60018002:
                     case 60028002:
                     case 100008002:
                     case 130008002:
                     case 140008002:
                     case 150008002:
                     case 400001002:
                        ret.statups.put(SecondaryStatFlag.SharpEyes, (ret.x << 8) + ret.y);
                        break;
                     case 11101002:
                     case 13101002:
                        ret.statups.put(SecondaryStatFlag.ExceedOverload, ret.x);
                        break;
                     case 12000022:
                     case 12100026:
                     case 12110024:
                     case 12120007:
                        ret.statups.put(SecondaryStatFlag.indieMAD, ret.x);
                        break;
                     case 12101022:
                        ret.mpR = ret.x / 100.0;
                        break;
                     case 12101023:
                        ret.statups.put(SecondaryStatFlag.indieBooster, Integer.valueOf(ret.indieBooster));
                        ret.statups.put(SecondaryStatFlag.indieMAD, Integer.valueOf(ret.indieMad));
                        break;
                     case 12111023:
                        ret.statups.put(SecondaryStatFlag.FlareTrick, ret.y);
                        break;
                     case 12121003:
                        ret.statups.put(SecondaryStatFlag.AntiMagicShell, ret.x);
                        break;
                     case 12121005:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        ret.statups.put(SecondaryStatFlag.indieBooster, Integer.valueOf(ret.indieBooster));
                        break;
                     case 12121043:
                        ret.statups.put(SecondaryStatFlag.AddRangeOnOff, 1);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 13001022:
                        ret.duration = Integer.MAX_VALUE;
                        ret.statups.put(SecondaryStatFlag.CygnusElementSkill, 1);
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        break;
                     case 13110026:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        break;
                     case 13121004:
                        ret.statups.put(SecondaryStatFlag.IllusionStep, Integer.valueOf(ret.prop));
                        break;
                     case 14001021:
                        ret.duration = Integer.MAX_VALUE;
                        ret.statups.put(SecondaryStatFlag.ElementDarkness, 1);
                        break;
                     case 14001027:
                        ret.statups.clear();
                        ret.statups.put(SecondaryStatFlag.NightWalkerBat, 1);
                        ret.duration = 2100000000;
                        break;
                     case 14001031:
                        ret.statups.put(SecondaryStatFlag.DarkSight, Integer.valueOf(ret.level));
                        break;
                     case 14111024:
                     case 400011005:
                     case 400031007:
                     case 500061046:
                        ret.statups.put(SecondaryStatFlag.ShadowServant, 1);
                        break;
                     case 14121016:
                        ret.statups.clear();
                        ret.statups.put(SecondaryStatFlag.NightWalkerBat, 1);
                        ret.duration = 2100000000;
                        break;
                     case 14121054:
                     case 14121055:
                     case 14121056:
                        ret.statups.put(SecondaryStatFlag.ShadowIllusion, 1);
                        break;
                     case 15001022:
                        ret.duration = Integer.MAX_VALUE;
                        ret.statups.put(SecondaryStatFlag.CygnusElementSkill, Integer.valueOf(ret.level));
                        break;
                     case 15111023:
                        ret.statups.put(SecondaryStatFlag.TerR, Integer.valueOf(ret.asrR));
                        ret.statups.put(SecondaryStatFlag.AsrR, Integer.valueOf(ret.asrR));
                        break;
                     case 15111024:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        ret.statups.put(SecondaryStatFlag.DamAbsorbShield, ret.y);
                        break;
                     case 15121054:
                        ret.statups.put(SecondaryStatFlag.StrikerHyperElectric, ret.x);
                        ret.statups.put(SecondaryStatFlag.indieMPConReduceR, 100);
                        break;
                     case 20021110:
                     case 80001040:
                        ret.moveTo = ret.x;
                        break;
                     case 20031205:
                        ret.statups.put(SecondaryStatFlag.Invisible, ret.x);
                        break;
                     case 20031209:
                     case 20031210:
                        ret.overTime = true;
                        break;
                     case 20040216:
                        ret.statups.put(SecondaryStatFlag.Larkness, ret.x);
                        break;
                     case 20040217:
                        ret.statups.put(SecondaryStatFlag.Larkness, ret.y);
                        break;
                     case 20040219:
                     case 20040220:
                        ret.statups.put(SecondaryStatFlag.Larkness, 2);
                        break;
                     case 20050286:
                     case 80000169:
                        ret.statups.put(SecondaryStatFlag.PreReviveOnce, Integer.valueOf(ret.prop));
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 21001003:
                        ret.statups.put(SecondaryStatFlag.Booster, -ret.y);
                        break;
                     case 21001008:
                        ret.duration = Integer.MAX_VALUE;
                        ret.statups.put(SecondaryStatFlag.BodyPressure, ret.x);
                        break;
                     case 21101005:
                        ret.statups.put(SecondaryStatFlag.AranDrain, ret.x);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 21101006:
                        ret.statups.put(SecondaryStatFlag.SnowCharge, ret.x);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 21110016:
                        ret.statups.put(SecondaryStatFlag.AdrenalinBoost, ret.w);
                        ret.overTime = true;
                        break;
                     case 21111012:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        ret.statups.put(SecondaryStatFlag.indieMAD, Integer.valueOf(ret.indieMad));
                        break;
                     case 21121058:
                        ret.statups.put(SecondaryStatFlag.AdrenalinBoost, 150);
                        ret.duration = 20000;
                        break;
                     case 22131001:
                        ret.statups.put(SecondaryStatFlag.MagicShield, ret.x);
                        break;
                     case 22141003:
                        ret.statups.put(SecondaryStatFlag.Slow, ret.x);
                        break;
                     case 22161002:
                        ret.monsterStatus.put(MobTemporaryStatFlag.WEAKNESS2, ret.x);
                        break;
                     case 22171073:
                        ret.statups.put(SecondaryStatFlag.EnhancedMAD, Integer.valueOf(ret.emad));
                        ret.statups.put(SecondaryStatFlag.EnhancedDEF, Integer.valueOf(ret.emdd));
                        break;
                     case 23101003:
                        ret.statups.put(SecondaryStatFlag.EnrageCr, Integer.valueOf(ret.damage));
                        ret.statups.put(SecondaryStatFlag.CriticalBuff, ret.x);
                        break;
                     case 23110004:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        ret.statups.put(SecondaryStatFlag.IgnisRore, ret.x);
                        break;
                     case 23111005:
                        ret.statups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
                        break;
                     case 23111008:
                        ret.statups.put(SecondaryStatFlag.Elemental_Knights, 1);
                        ret.setDuration(Integer.MAX_VALUE);
                        break;
                     case 23121004:
                        ret.statups.put(SecondaryStatFlag.EnhancedMaxHP, Integer.valueOf(ret.emhp));
                        ret.statups.put(SecondaryStatFlag.DamR, Integer.valueOf(ret.indiePadR));
                        break;
                     case 23121054:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        break;
                     case 24111002:
                        ret.duration = Integer.MAX_VALUE;
                        ret.statups.put(SecondaryStatFlag.ReviveOnce, ret.x);
                        break;
                     case 24111005:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        ret.statups.put(SecondaryStatFlag.indieACC, Integer.valueOf(ret.indieAcc));
                        break;
                     case 25111209:
                        ret.statups.put(SecondaryStatFlag.ReviveOnce, ret.x);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 25121006:
                        ret.monsterStatus.put(MobTemporaryStatFlag.POISON, Integer.valueOf(ret.dot));
                        break;
                     case 25121131:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        ret.statups.put(SecondaryStatFlag.indieBooster, Integer.valueOf(ret.indieBooster));
                        ret.statups.put(SecondaryStatFlag.indieBDR, Integer.valueOf(ret.indieBDR));
                        ret.statups.put(SecondaryStatFlag.indieIgnoreMobPdpR, Integer.valueOf(ret.indieIgnoreMobpdpR));
                        break;
                     case 25121133:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        ret.statups.put(SecondaryStatFlag.indieBooster, Integer.valueOf(ret.indieBooster));
                        ret.statups.put(SecondaryStatFlag.indieBDR, Integer.valueOf(ret.indieBDR));
                        ret.statups.put(SecondaryStatFlag.indieIgnoreMobPdpR, Integer.valueOf(ret.indieIgnoreMobpdpR));
                        ret.statups.put(SecondaryStatFlag.HiddenHyperLinkMaximization, 1);
                        break;
                     case 25121209:
                        ret.statups.put(SecondaryStatFlag.SpiritGuard, ret.x);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 27001004:
                        ret.statups.put(SecondaryStatFlag.indieMMPR, Integer.valueOf(ret.indieMmpR));
                        break;
                     case 27101202:
                        ret.statups.put(SecondaryStatFlag.KeyDownAreaMoving, ret.x);
                        ret.duration = 1000;
                        break;
                     case 27111004:
                        ret.statups.put(SecondaryStatFlag.AntiMagicShell, ret.u);
                        ret.duration = 2100000000;
                        break;
                     case 27111005:
                        ret.statups.put(SecondaryStatFlag.indieDEF, Integer.valueOf(ret.indiePdd));
                        break;
                     case 27111006:
                        ret.statups.put(SecondaryStatFlag.EnhancedMAD, Integer.valueOf(ret.emad));
                        break;
                     case 27121006:
                        ret.statups.put(SecondaryStatFlag.ElementReset, ret.y);
                        break;
                     case 30000227:
                        ret.statups.put(SecondaryStatFlag.indieMHPR, Integer.valueOf(ret.indieMhpR));
                        ret.statups.put(SecondaryStatFlag.indieMMPR, Integer.valueOf(ret.indieMmpR));
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        ret.duration = Integer.MAX_VALUE;
                        ret.overTime = true;
                        break;
                     case 30021237:
                     case 36141502:
                        ret.duration = 30000;
                        ret.statups.put(SecondaryStatFlag.NewFlying, 1);
                        break;
                     case 31101003:
                        ret.statups.put(SecondaryStatFlag.PowerGuard, ret.x);
                        break;
                     case 31111004:
                        ret.statups.put(SecondaryStatFlag.AsrR, ret.x);
                        ret.statups.put(SecondaryStatFlag.DDR, ret.y);
                        ret.statups.put(SecondaryStatFlag.TerR, ret.z);
                        break;
                     case 31121002:
                        ret.statups.put(SecondaryStatFlag.VampiricTouch, ret.x);
                        break;
                     case 31121007:
                        ret.statups.put(SecondaryStatFlag.InfinityForce, 1);
                        break;
                     case 31121054:
                        ret.statups.put(SecondaryStatFlag.ShadowPartner, ret.x);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 31201003:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        break;
                     case 31211003:
                        ret.statups.put(SecondaryStatFlag.DamAbsorbShield, ret.x);
                        ret.statups.put(SecondaryStatFlag.AsrR, ret.y);
                        ret.statups.put(SecondaryStatFlag.TerR, ret.z);
                        break;
                     case 31211004:
                        ret.statups.put(SecondaryStatFlag.DiabolicRecovery, ret.x);
                        ret.statups.put(SecondaryStatFlag.indieMHPR, Integer.valueOf(ret.indieMhpR));
                        break;
                     case 31221000:
                        ret.statups.put(SecondaryStatFlag.EnhancedMaxMP, 1);
                        break;
                     case 31221004:
                        ret.statups.put(SecondaryStatFlag.indieBooster, Integer.valueOf(ret.indieBooster));
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        break;
                     case 31221054:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        break;
                     case 32001014:
                     case 32100010:
                     case 32110017:
                     case 32120019:
                     case 32141000:
                        ret.statups.put(SecondaryStatFlag.BMageDeath, 0);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 32101004:
                        ret.statups.put(SecondaryStatFlag.AranDrain, ret.x);
                        break;
                     case 32111006:
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 32111016:
                        ret.statups.put(SecondaryStatFlag.DarkLighting, ret.x);
                        ret.duration = Integer.MAX_VALUE;
                        ret.overTime = true;
                        break;
                     case 32121010:
                        ret.statups.put(SecondaryStatFlag.CriticalBuff, ret.z);
                        ret.statups.put(SecondaryStatFlag.EnrageCrDamMin, ret.y);
                        ret.statups.put(SecondaryStatFlag.Enrage, ret.x);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 32121056:
                        ret.statups.clear();
                        ret.statups.put(SecondaryStatFlag.AttackCountX, Integer.valueOf(ret.attackCount));
                        break;
                     case 33001007:
                     case 33001008:
                     case 33001009:
                     case 33001010:
                     case 33001011:
                     case 33001012:
                     case 33001013:
                     case 33001014:
                     case 33001015:
                        ret.statups.put(SecondaryStatFlag.JaguarSummoned, 3870);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 33110008:
                        ret.statups.put(SecondaryStatFlag.CriticalBuff, 100);
                        break;
                     case 33111004:
                        ret.statups.put(SecondaryStatFlag.Blind, ret.x);
                        ret.monsterStatus.put(MobTemporaryStatFlag.ACC, ret.x);
                        break;
                     case 33111007:
                        ret.statups.put(SecondaryStatFlag.BeastFormDamageUp, 1);
                        break;
                     case 33111011:
                        ret.statups.put(SecondaryStatFlag.DrawBack, 1);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 33121013:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        break;
                     case 33121054:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        ret.statups.put(SecondaryStatFlag.FinalAttackProp, ret.x);
                        break;
                     case 35001002:
                        ret.statups.put(SecondaryStatFlag.Mechanic, level);
                        ret.statups.put(SecondaryStatFlag.EnhancedDEF, Integer.valueOf(ret.epdd));
                        ret.statups.put(SecondaryStatFlag.EnhancedPAD, Integer.valueOf(ret.epad));
                        ret.statups.put(SecondaryStatFlag.indieBooster, -1);
                        ret.statups.put(SecondaryStatFlag.indieSpeed, Integer.valueOf(ret.indieSpeed));
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 35101007:
                        ret.duration = 2100000000;
                        ret.statups.put(SecondaryStatFlag.Guard, ret.x);
                        break;
                     case 35111001:
                     case 35111009:
                     case 35111010:
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 35111002:
                        ret.monsterStatus.put(MobTemporaryStatFlag.STUN, 1);
                        break;
                     case 35111003:
                        ret.statups.put(SecondaryStatFlag.EnhancedDEF, Integer.valueOf(ret.epdd));
                        ret.statups.put(SecondaryStatFlag.EnhancedPAD, Integer.valueOf(ret.epad));
                        ret.statups.put(SecondaryStatFlag.EnhancedMaxMP, Integer.valueOf(ret.emmp));
                        ret.statups.put(SecondaryStatFlag.EnhancedMaxHP, Integer.valueOf(ret.emhp));
                        ret.statups.put(SecondaryStatFlag.Mechanic, level);
                        ret.statups.put(SecondaryStatFlag.CriticalBuff, ret.cr);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 35111005:
                        ret.monsterStatus.put(MobTemporaryStatFlag.SPEED, ret.x);
                        ret.monsterStatus.put(MobTemporaryStatFlag.PDR, ret.y);
                        break;
                     case 35111016:
                     case 151121042:
                     case 152121042:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        break;
                     case 35121003:
                        ret.statups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
                        break;
                     case 35121010:
                        ret.duration = 60000;
                        ret.statups.put(SecondaryStatFlag.indieDamR, ret.x);
                        break;
                     case 35121055:
                        ret.statups.put(SecondaryStatFlag.BombTime, ret.x);
                        break;
                     case 36001002:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        break;
                     case 36001005:
                        ret.statups.put(SecondaryStatFlag.PinPointRocket, 1);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 36101002:
                        ret.statups.put(SecondaryStatFlag.CriticalBuff, ret.x);
                        break;
                     case 36111004:
                        ret.statups.put(SecondaryStatFlag.XenonAegisSystem, ret.x);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 36111006:
                        ret.statups.put(SecondaryStatFlag.ShadowPartner, ret.x);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 36121003:
                        ret.statups.put(SecondaryStatFlag.indiePMDR, Integer.valueOf(ret.indiePMdR));
                        ret.statups.put(SecondaryStatFlag.indieBDR, Integer.valueOf(ret.indieBDR));
                        break;
                     case 36121054:
                        ret.statups.put(SecondaryStatFlag.AmaranthGenerator, 30020232);
                        ret.overTime = true;
                        break;
                     case 37121005:
                        ret.statups.put(SecondaryStatFlag.RWBarrierHeal, 1);
                        ret.overTime = true;
                        break;
                     case 37121054:
                        ret.statups.clear();
                        ret.statups.put(SecondaryStatFlag.RWMaximizeCannon, ret.y);
                        break;
                     case 51101004:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        break;
                     case 51111008:
                        ret.statups.put(SecondaryStatFlag.MichaelSoulLink, 1);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 51121006:
                        ret.statups.put(SecondaryStatFlag.Enrage, ret.x);
                        ret.statups.put(SecondaryStatFlag.EnrageCrDamMin, ret.y);
                        ret.statups.put(SecondaryStatFlag.EnrageCr, 0);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 51121054:
                        ret.statups.put(SecondaryStatFlag.DamAbsorbShield, ret.x);
                        break;
                     case 60001216:
                        ret.statups.put(SecondaryStatFlag.ReshuffleSwitch, 0);
                        ret.duration = 2100000000;
                        break;
                     case 60001217:
                        ret.statups.put(SecondaryStatFlag.ReshuffleSwitch, 0);
                        ret.duration = 2100000000;
                        break;
                     case 60011219:
                     case 80001155:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        break;
                     case 61101002:
                     case 61110211:
                     case 61120007:
                     case 61121217:
                        ret.statups.put(SecondaryStatFlag.StopForceAtomInfo, Integer.valueOf(ret.mobCount));
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 61101004:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        ret.statups.put(SecondaryStatFlag.Booster, -2);
                        break;
                     case 61111002:
                     case 61111220:
                     case 61121100:
                     case 61121201:
                        ret.monsterStatus.put(MobTemporaryStatFlag.SPEED, ret.x);
                        break;
                     case 61111003:
                        ret.statups.put(SecondaryStatFlag.TerR, Integer.valueOf(ret.terR));
                        ret.statups.put(SecondaryStatFlag.AsrR, Integer.valueOf(ret.asrR));
                        break;
                     case 61111004:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        break;
                     case 61111008:
                     case 61120008:
                     case 61121053:
                        ret.statups.clear();
                        ret.statups.put(SecondaryStatFlag.Speed, Integer.valueOf(ret.speed));
                        ret.statups.put(SecondaryStatFlag.Morph, Integer.valueOf(ret.morph));
                        ret.statups.put(SecondaryStatFlag.Jump, 100);
                        ret.statups.put(SecondaryStatFlag.indiePMDR, Integer.valueOf(ret.indiePMdR));
                        ret.statups.put(SecondaryStatFlag.CriticalBuff, 0);
                        ret.duration = sourceid == 61111008 ? '\uea60' : (sourceid == 61121053 ? '\uea60' : 90000);
                        ret.overTime = true;
                        break;
                     case 61121009:
                        ret.statups.put(SecondaryStatFlag.PartyBarrier, ret.x);
                        break;
                     case 61121054:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        ret.statups.put(SecondaryStatFlag.indieBooster, -1);
                        ret.statups.put(SecondaryStatFlag.IgnorePCounter, 1);
                        ret.statups.put(SecondaryStatFlag.IgnorePImmune, 1);
                        break;
                     case 63101005:
                        ret.duration = Integer.MAX_VALUE;
                        ret.statups.put(SecondaryStatFlag.DragonFang, 1);
                        break;
                     case 63101010:
                        ret.statups.put(SecondaryStatFlag.Booster, ret.x);
                        break;
                     case 63111009:
                        ret.duration = Integer.MAX_VALUE;
                        ret.statups.put(SecondaryStatFlag.RemainIncense, 1);
                        break;
                     case 64121053:
                        ret.statups.put(SecondaryStatFlag.ProfessionalAgent, 2);
                        break;
                     case 64121054:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        ret.statups.put(SecondaryStatFlag.indieCR, Integer.valueOf(ret.indieCr));
                        break;
                     case 65101002:
                        ret.statups.put(SecondaryStatFlag.PowerTransferGauge, 1000);
                        break;
                     case 65121004:
                        ret.statups.put(SecondaryStatFlag.SoulGazeCriDamR, ret.x);
                        break;
                     case 65121011:
                        ret.duration = Integer.MAX_VALUE;
                        ret.statups.put(SecondaryStatFlag.AngelicBursterSoulSeeker, 1);
                        break;
                     case 65121053:
                        ret.statups.put(SecondaryStatFlag.TerR, Integer.valueOf(ret.asrR));
                        ret.statups.put(SecondaryStatFlag.AsrR, Integer.valueOf(ret.terR));
                        ret.statups.put(SecondaryStatFlag.CriticalBuff, ret.x);
                        break;
                     case 65121054:
                        ret.statups.put(SecondaryStatFlag.SoulExalt, 1);
                        ret.statups.put(SecondaryStatFlag.indieIgnoreMobPdpR, Integer.valueOf(ret.indieIgnoreMobpdpR));
                        ret.statups.put(SecondaryStatFlag.indieBDR, Integer.valueOf(ret.indieBDR));
                        break;
                     case 80000301:
                        ret.statups.put(SecondaryStatFlag.indiePadR, ret.y);
                        ret.statups.put(SecondaryStatFlag.indieMadR, ret.y);
                        break;
                     case 80000302:
                        ret.statups.put(SecondaryStatFlag.indieStance, ret.x);
                        break;
                     case 80000303:
                        ret.statups.put(SecondaryStatFlag.indieAsrR, ret.x);
                        break;
                     case 80000514:
                     case 150010241:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        ret.statups.put(SecondaryStatFlag.Solus, 1);
                        break;
                     case 80001034:
                     case 80001035:
                     case 80001036:
                        ret.statups.put(SecondaryStatFlag.ItemCritical, 1);
                        break;
                     case 80001140:
                        ret.statups.put(SecondaryStatFlag.indieStance, Integer.valueOf(ret.indieStance));
                        break;
                     case 80001242:
                        ret.duration = Integer.MAX_VALUE;
                        ret.statups.put(SecondaryStatFlag.NewFlying, 1);
                        break;
                     case 80001427:
                        ret.statups.put(SecondaryStatFlag.indieJump, Integer.valueOf(ret.indieJump));
                        ret.statups.put(SecondaryStatFlag.indieSpeed, Integer.valueOf(ret.indieSpeed));
                        ret.statups.put(SecondaryStatFlag.indieBooster, Integer.valueOf(ret.indieBooster));
                        break;
                     case 80001428:
                        ret.statups.put(SecondaryStatFlag.DotHealMPPerSecondR,
                              Integer.valueOf(ret.dotHealHPPerSecondR));
                        ret.statups.put(SecondaryStatFlag.DotHealHPPerSecondR,
                              Integer.valueOf(ret.dotHealMPPerSecondR));
                        ret.statups.put(SecondaryStatFlag.indieAsrR, Integer.valueOf(ret.indieAsrR));
                        ret.statups.put(SecondaryStatFlag.indieTerR, Integer.valueOf(ret.indieTerR));
                        ret.statups.put(SecondaryStatFlag.indieStance, Integer.valueOf(ret.indieStance));
                        break;
                     case 80001430:
                        ret.statups.put(SecondaryStatFlag.Booster, Integer.valueOf(ret.indieBooster));
                        ret.statups.put(SecondaryStatFlag.TimeFastBBuff, Integer.valueOf(ret.indieDamR));
                        break;
                     case 80001432:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        break;
                     case 80001455:
                        ret.statups.put(SecondaryStatFlag.indiePadR, Integer.valueOf(ret.indiePadR));
                        ret.statups.put(SecondaryStatFlag.indieMadR, Integer.valueOf(ret.indiePadR));
                        break;
                     case 80001456:
                        ret.statups.put(SecondaryStatFlag.SetBaseDamageByBuff, ret.x);
                        break;
                     case 80001457:
                        ret.statups.put(SecondaryStatFlag.LimitMP, ret.x);
                        ret.statups.put(SecondaryStatFlag.indieBDR, Integer.valueOf(ret.indieBDR));
                        break;
                     case 80001458:
                        ret.statups.put(SecondaryStatFlag.MHPCutR, ret.x);
                        ret.statups.put(SecondaryStatFlag.indieBDR, Integer.valueOf(ret.indieBDR));
                        break;
                     case 80001459:
                        ret.statups.put(SecondaryStatFlag.MMPCutR, ret.x);
                        ret.statups.put(SecondaryStatFlag.indieBDR, Integer.valueOf(ret.indieBDR));
                        break;
                     case 80001460:
                        ret.statups.put(SecondaryStatFlag.indieMHPR, Integer.valueOf(ret.indieMhpR));
                        break;
                     case 80001461:
                        ret.statups.put(SecondaryStatFlag.indieCD, ret.x);
                        break;
                     case 80001474:
                        ret.statups.put(SecondaryStatFlag.indieBooster, Integer.valueOf(ret.indieBooster));
                        break;
                     case 80001475:
                        ret.statups.put(SecondaryStatFlag.IgnorePCounter, 1);
                        ret.statups.put(SecondaryStatFlag.IgnorePImmune, 1);
                        break;
                     case 80001476:
                        ret.statups.put(SecondaryStatFlag.indieIgnoreMobPdpR, Integer.valueOf(ret.indieIgnoreMobpdpR));
                        ret.statups.put(SecondaryStatFlag.indiePddR, Integer.valueOf(ret.indiePddR));
                        break;
                     case 80001477:
                        ret.statups.put(SecondaryStatFlag.ReflectDamR, ret.x);
                        break;
                     case 80001479:
                        ret.statups.put(SecondaryStatFlag.indiePadR, Integer.valueOf(ret.indiePadR));
                        ret.statups.put(SecondaryStatFlag.indieMadR, Integer.valueOf(ret.indiePadR));
                        break;
                     case 80001757:
                        ret.statups.put(SecondaryStatFlag.indieSpeed, Integer.valueOf(ret.indieSpeed));
                        ret.statups.put(SecondaryStatFlag.indieJump, Integer.valueOf(ret.indieJump));
                        ret.statups.put(SecondaryStatFlag.Inflation, ret.x);
                        break;
                     case 80001762:
                        ret.duration = 60000;
                        ret.statups.put(SecondaryStatFlag.RandAreaAttack, 3);
                        break;
                     case 80001875:
                        ret.statups.put(SecondaryStatFlag.FixCooltime, Integer.valueOf(ret.fixCoolTime));
                        break;
                     case 80002280:
                        ret.duration = 180000;
                        ret.statups.put(SecondaryStatFlag.indieEXP, 100);
                        break;
                     case 80002281:
                        ret.statups.put(SecondaryStatFlag.RuneOfGreed, Integer.valueOf(ret.mesoAmountUp));
                        break;
                     case 80002282:
                        ret.duration = 900000;
                        ret.statups.put(SecondaryStatFlag.RuneBlocked, 1);
                        break;
                     case 80002404:
                        ret.statups.put(SecondaryStatFlag.DebuffIncHP, ret.y);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 80002543:
                        ret.statups.put(SecondaryStatFlag.DebuffIncHP, ret.x);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 80002623:
                        ret.statups.put(SecondaryStatFlag.BlackMageAttributes, 2);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 80002632:
                        ret.statups.put(SecondaryStatFlag.Yaldabaoth, 1);
                        ret.setDuration(Integer.MAX_VALUE);
                        break;
                     case 80002633:
                        ret.statups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
                        ret.statups.put(SecondaryStatFlag.Aion, 1);
                        break;
                     case 80002770:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        break;
                     case 80002888:
                        ret.statups.put(SecondaryStatFlag.RuneofPurification, 1);
                        break;
                     case 80002890:
                        ret.statups.put(SecondaryStatFlag.RuneOfIgnition, 1);
                        break;
                     case 80003062:
                        ret.statups.put(SecondaryStatFlag.Speed, 4);
                        ret.statups.put(SecondaryStatFlag.indieForceSpeed, 30);
                        break;
                     case 90001003:
                        ret.monsterStatus.put(MobTemporaryStatFlag.POISON, 1);
                        break;
                     case 90001004:
                        ret.monsterStatus.put(MobTemporaryStatFlag.BLIND, ret.x);
                        break;
                     case 100000276:
                        ret.statups.put(SecondaryStatFlag.TimeFastABuff, Integer.valueOf(ret.level));
                        break;
                     case 100000277:
                        ret.statups.put(SecondaryStatFlag.TimeFastBBuff, Integer.valueOf(ret.level));
                        break;
                     case 100001263:
                        ret.statups.put(SecondaryStatFlag.ZeroAuraSTR, 1);
                        ret.statups.put(SecondaryStatFlag.indieAsrR, Integer.valueOf(ret.indieAsrR));
                        ret.statups.put(SecondaryStatFlag.indieMAD, Integer.valueOf(ret.indieMad));
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        ret.statups.put(SecondaryStatFlag.indieDEF, Integer.valueOf(ret.indiePdd));
                        ret.statups.put(SecondaryStatFlag.indieTerR, Integer.valueOf(ret.indieTerR));
                        ret.statups.put(SecondaryStatFlag.indieJump, Integer.valueOf(ret.indieJump));
                        ret.statups.put(SecondaryStatFlag.indieSpeed, Integer.valueOf(ret.indieSpeed));
                        ret.statups.put(SecondaryStatFlag.indieBooster, Integer.valueOf(ret.indieBooster));
                        ret.setDuration(Integer.MAX_VALUE);
                        break;
                     case 100001264:
                        ret.statups.put(SecondaryStatFlag.indieACC, Integer.valueOf(ret.indieAcc));
                        ret.statups.put(SecondaryStatFlag.indieBooster, Integer.valueOf(ret.indieBooster));
                        ret.statups.put(SecondaryStatFlag.indieEVA, Integer.valueOf(ret.indieEva));
                        ret.statups.put(SecondaryStatFlag.indieJump, Integer.valueOf(ret.indieJump));
                        ret.statups.put(SecondaryStatFlag.indieSpeed, Integer.valueOf(ret.indieSpeed));
                        ret.setDuration(Integer.MAX_VALUE);
                        break;
                     case 131001009:
                        ret.statups.put(SecondaryStatFlag.PinkBeanExpBuffBlock, 1);
                        ret.statups.put(SecondaryStatFlag.indiePadR, Integer.valueOf(ret.indiePadR));
                        ret.statups.put(SecondaryStatFlag.indieMadR, Integer.valueOf(ret.indiePadR));
                        break;
                     case 131001015:
                        ret.statups.put(SecondaryStatFlag.PinkbeanMinibeenMove, 1);
                        ret.duration = 2100000000;
                        break;
                     case 131001113:
                        ret.statups.put(SecondaryStatFlag.indiePadR, Integer.valueOf(ret.indiePadR));
                        ret.statups.put(SecondaryStatFlag.indieMadR, Integer.valueOf(ret.indiePadR));
                        break;
                     case 135001005:
                        ret.statups.put(SecondaryStatFlag.Yeti_RageOn, 1);
                        ret.statups.put(SecondaryStatFlag.indiePadR, Integer.valueOf(ret.indiePadR));
                        break;
                     case 135001009:
                        ret.statups.put(SecondaryStatFlag.ExpBuffBlock, 1);
                        ret.statups.put(SecondaryStatFlag.indiePadR, Integer.valueOf(ret.indiePadR));
                        break;
                     case 135001015:
                        ret.statups.put(SecondaryStatFlag.Yeti_MyFriendPepe, 1);
                        break;
                     case 135001017:
                        ret.statups.put(SecondaryStatFlag.Yeti_Spicy, 1);
                        break;
                     case 142001003:
                        ret.statups.put(SecondaryStatFlag.indieBooster, Integer.valueOf(ret.indieBooster));
                        break;
                     case 142001007:
                        ret.duration = Integer.MAX_VALUE;
                        ret.statups.put(SecondaryStatFlag.KinesisPsychicEnrageShield, 1);
                        break;
                     case 142111010:
                        ret.statups.put(SecondaryStatFlag.NewFlying, 1);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 142121032:
                        ret.statups.put(SecondaryStatFlag.KinesisPsychicOver, ret.x);
                        break;
                     case 150011075:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        ret.statups.put(SecondaryStatFlag.indieCooltimeReduce,
                              Integer.valueOf(ret.indieCooltimeReduce));
                        ret.statups.put(SecondaryStatFlag.indieEXP, Integer.valueOf(ret.indieExp));
                        break;
                     case 150011076:
                        ret.statups.put(SecondaryStatFlag.indieCooltimeReduce,
                              Integer.valueOf(ret.indieCooltimeReduce));
                        break;
                     case 150011077:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        break;
                     case 150011078:
                        ret.statups.put(SecondaryStatFlag.indieEXP, Integer.valueOf(ret.indieExp));
                        break;
                     case 151001004:
                        ret.statups.put(SecondaryStatFlag.NewFlying, 1);
                        ret.statups.put(SecondaryStatFlag.indieFlyAcc, 1);
                        ret.duration = 1010;
                        break;
                     case 151101006:
                        ret.statups.put(SecondaryStatFlag.Creation, 1);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 151101010:
                        ret.statups.put(SecondaryStatFlag.indieIgnoreMobPdpR, Integer.valueOf(ret.indieIgnoreMobpdpR));
                        ret.statups.put(SecondaryStatFlag.Resonance, 1);
                        break;
                     case 152001002:
                     case 152120003:
                        ret.statups.put(SecondaryStatFlag.SkillDamageR, 30);
                        ret.statups.put(SecondaryStatFlag.indieFlyAcc, 1);
                        ret.duration = 2000;
                        break;
                     case 152001005:
                        ret.statups.put(SecondaryStatFlag.NewFlying, 1);
                        ret.statups.put(SecondaryStatFlag.indieFlyAcc, 1);
                        ret.duration = 800;
                        break;
                     case 152101000:
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 152111003:
                        ret.statups.put(SecondaryStatFlag.indieBDR, Integer.valueOf(ret.indieBDR));
                        ret.statups.put(SecondaryStatFlag.indieStance, Integer.valueOf(ret.indieStance));
                        ret.statups.put(SecondaryStatFlag.indiePMDR, Integer.valueOf(ret.indiePMdR));
                        ret.statups.put(SecondaryStatFlag.NewFlying, 1);
                        ret.statups.put(SecondaryStatFlag.indieFlyAcc, 1);
                        ret.statups.put(SecondaryStatFlag.GloryWing, 1);
                        break;
                     case 152111007:
                        ret.statups.put(SecondaryStatFlag.HarmonyLink, 1);
                        ret.duration = 15000;
                        break;
                     case 152121005:
                        ret.statups.put(SecondaryStatFlag.CrystalDeus, 1);
                        break;
                     case 155001001:
                     case 155141003:
                        ret.statups.put(SecondaryStatFlag.indieStance, Integer.valueOf(ret.indieStance));
                        ret.statups.put(SecondaryStatFlag.Speed, Integer.valueOf(ret.speed));
                        break;
                     case 155101003:
                     case 155141010:
                        ret.statups.put(SecondaryStatFlag.indieCR, Integer.valueOf(ret.indieCr));
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        break;
                     case 155101005:
                        ret.statups.put(SecondaryStatFlag.Booster, -2);
                        break;
                     case 155101006:
                        ret.statups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(ret.indiePad));
                        ret.statups.put(SecondaryStatFlag.indieStance, Integer.valueOf(ret.indieStance));
                        ret.statups.put(SecondaryStatFlag.SpectralForm, 1);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 155101008:
                        ret.statups.put(SecondaryStatFlag.ImpendingDeath, 1);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 155111005:
                     case 155141015:
                        ret.statups.put(SecondaryStatFlag.indieBooster, Integer.valueOf(ret.indieBooster));
                        ret.statups.put(SecondaryStatFlag.indieEvaR, Integer.valueOf(ret.indieEvaR));
                        break;
                     case 155121005:
                     case 155141020:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        ret.statups.put(SecondaryStatFlag.indieIgnoreMobPdpR, Integer.valueOf(ret.indieIgnoreMobpdpR));
                        ret.statups.put(SecondaryStatFlag.indieBDR, Integer.valueOf(ret.indieBDR));
                        break;
                     case 155121042:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        break;
                     case 155121043:
                        ret.statups.put(SecondaryStatFlag.DivineWrath, 1);
                        break;
                     case 162001005:
                        ret.statups.put(SecondaryStatFlag.MountainGuardian, ret.x);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 162101000:
                        ret.statups.put(SecondaryStatFlag.DragonVeinReading, 1);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 164001004:
                        ret.statups.put(SecondaryStatFlag.NewFlying, 1);
                        ret.statups.put(SecondaryStatFlag.indieFlyAcc, 1);
                        ret.duration = 650;
                        break;
                     case 164101003:
                        ret.statups.put(SecondaryStatFlag.CloneAttack, 1);
                        break;
                     case 164121007:
                        ret.statups.put(SecondaryStatFlag.ButterflyDream, 1);
                        ret.statups.put(SecondaryStatFlag.indiePMDR, Integer.valueOf(ret.indiePMdR));
                        break;
                     case 164121041:
                        ret.statups.put(SecondaryStatFlag.MastersElixir, 1);
                        break;
                     case 164121042:
                        ret.statups.put(SecondaryStatFlag.KeyDownStart, 1);
                        ret.statups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
                        break;
                     case 400001003:
                        ret.statups.put(SecondaryStatFlag.MaxHP, ret.x);
                        ret.statups.put(SecondaryStatFlag.MaxMP, ret.y);
                        break;
                     case 400001005:
                        ret.statups.put(SecondaryStatFlag.indiePAD, ret.x);
                        ret.statups.put(SecondaryStatFlag.indieMAD, ret.y);
                        ret.statups.put(SecondaryStatFlag.indieDEF, ret.z);
                        ret.statups.put(SecondaryStatFlag.indieMHP, Integer.valueOf(ret.indieMhp));
                        ret.statups.put(SecondaryStatFlag.indieMMP, Integer.valueOf(ret.indieMmp));
                        break;
                     case 400001012:
                        ret.statups.put(SecondaryStatFlag.SetBaseDamageByBuff, Integer.valueOf(ret.damage));
                        break;
                     case 400001023:
                        ret.statups.put(SecondaryStatFlag.DarkSight, ret.x);
                        break;
                     case 400001037:
                        ret.statups.put(SecondaryStatFlag.MagicCircuitFullDrive, 1);
                        break;
                     case 400001039:
                     case 400001040:
                     case 400001041:
                        ret.duration = 50000;
                        break;
                     case 400001042:
                        ret.statups.put(SecondaryStatFlag.indieStatRBasic, ret.x);
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        break;
                     case 400001045:
                        ret.statups.put(SecondaryStatFlag.EmpressBless, ret.x);
                        break;
                     case 400001047:
                        ret.statups.put(SecondaryStatFlag.EmpressBless, 1);
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.getIndieDamR()));
                        break;
                     case 400001049:
                        ret.statups.put(SecondaryStatFlag.EmpressBless, 1);
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.getIndieDamR()));
                        break;
                     case 400001050:
                        ret.statups.put(SecondaryStatFlag.EmpressBless, 1);
                        ret.statups.put(SecondaryStatFlag.indiePMDR, Integer.valueOf(ret.indiePMdR));
                        break;
                     case 400001052:
                        ret.statups.put(SecondaryStatFlag.indieFlyAcc, 1);
                        ret.statups.put(SecondaryStatFlag.NewFlying, 1);
                        ret.duration /= 1000;
                        break;
                     case 400001059:
                        ret.duration = 51000;
                     case 400001044:
                        ret.statups.put(SecondaryStatFlag.indieDamReduceR, -ret.getZ());
                     case 400001043:
                        ret.statups.put(SecondaryStatFlag.EmpressBless, 1);
                        ret.statups.put(SecondaryStatFlag.indieDamR, ret.getQ());
                        break;
                     case 400010000:
                     case 400011000:
                        ret.statups.put(SecondaryStatFlag.indieIgnoreMobPdpR, Integer.valueOf(ret.indieIgnoreMobpdpR));
                        ret.statups.put(SecondaryStatFlag.indiePMDR, Integer.valueOf(ret.indiePMdR));
                        ret.statups.put(SecondaryStatFlag.AuraWeapon, ret.z);
                        break;
                     case 400011006:
                        ret.statups.put(SecondaryStatFlag.indieCR, Integer.valueOf(ret.indieCr));
                        break;
                     case 400011010:
                        ret.statups.put(SecondaryStatFlag.DemonFrenzy, 6);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 400011016:
                     case 500061039:
                        ret.statups.put(SecondaryStatFlag.indiePadR, Integer.valueOf(ret.indiePadR));
                        if (sourceid == 500061039) {
                           ret.statups.put(SecondaryStatFlag.indiePMDR, ret.y);
                        }

                        ret.statups.put(SecondaryStatFlag.InstallMaha, ret.x);
                        break;
                     case 400011017:
                        ret.statups.put(SecondaryStatFlag.ProfessionalAgent, 2);
                        break;
                     case 400011038:
                        ret.hpR = ret.x;
                        break;
                     case 400011047:
                        ret.statups.put(SecondaryStatFlag.DarknessAura, ret.getW());
                        break;
                     case 400011052:
                        ret.statups.put(SecondaryStatFlag.BlessedHammer, 1);
                        ret.overTime = true;
                        break;
                     case 400011053:
                        ret.overTime = true;
                        break;
                     case 400011055:
                        ret.statups.put(SecondaryStatFlag.Ellision, 1);
                        break;
                     case 400011073:
                        ret.statups.put(SecondaryStatFlag.ComboInstinct, 1);
                        break;
                     case 400011083:
                        ret.statups.put(SecondaryStatFlag.SwordOfSoulLight, 2);
                        ret.statups.put(SecondaryStatFlag.indieCR, Integer.valueOf(ret.indieCr));
                        ret.statups.put(SecondaryStatFlag.indiePadR, Integer.valueOf(ret.indiePadR));
                        ret.statups.put(SecondaryStatFlag.indieIgnoreMobPdpR, Integer.valueOf(ret.indieIgnoreMobpdpR));
                        break;
                     case 400011109:
                     case 500061065:
                        if (sourceid == 500061065) {
                           ret.statups.put(SecondaryStatFlag.indiePMDR, ret.u);
                        }

                        ret.statups.put(SecondaryStatFlag.indieDamR, ret.y);
                        ret.statups.put(SecondaryStatFlag.Restore, 1);
                        break;
                     case 400011112:
                     case 500061054:
                        ret.statups.put(SecondaryStatFlag.Revenant, 1);
                        break;
                     case 400011116:
                        ret.statups.put(SecondaryStatFlag.AfterImageShock, ret.getY());
                        break;
                     case 400011118:
                        ret.statups.put(SecondaryStatFlag.DevilishPower, 1);
                        break;
                     case 400011123:
                        ret.statups.put(SecondaryStatFlag.BlizzardTempest, 1);
                        break;
                     case 400011127:
                     case 500061010:
                        ret.statups.put(SecondaryStatFlag.indieBarrier, ret.x);
                        break;
                     case 400011129:
                     case 500061058:
                        ret.statups.put(SecondaryStatFlag.RevenantRage, 1);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 400021000:
                        ret.statups.put(SecondaryStatFlag.OverloadMana, ret.z);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 400021003:
                     case 500061002:
                        ret.statups.put(SecondaryStatFlag.Pray, ret.x);
                        break;
                     case 400021008:
                        ret.statups.put(SecondaryStatFlag.PsychicTornado, 1);
                        break;
                     case 400021060:
                        ret.statups.put(SecondaryStatFlag.EtherealForm, 1);
                        break;
                     case 400021086:
                        ret.statups.put(SecondaryStatFlag.KeyDownStart, 1);
                        break;
                     case 400021096:
                        ret.statups.put(SecondaryStatFlag.LawOfGravity, 1);
                        break;
                     case 400031000:
                        ret.statups.put(SecondaryStatFlag.GuidedArrow, ret.z);
                        ret.overTime = true;
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 400031002:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        break;
                     case 400031005:
                     case 500061007:
                        ret.statups.put(SecondaryStatFlag.ProfessionalAgent, 1);
                        break;
                     case 400031006:
                        ret.statups.put(SecondaryStatFlag.TrueSniping, ret.x);
                        break;
                     case 400031015:
                        ret.statups.put(SecondaryStatFlag.SplitArrow, 1);
                        break;
                     case 400031017:
                     case 500061050:
                        ret.statups.put(SecondaryStatFlag.Sylvidia, 1932417);
                        ret.statups.put(SecondaryStatFlag.indiePadR, Integer.valueOf(ret.indiePadR));
                        if (sourceid == 500061050) {
                           ret.statups.put(SecondaryStatFlag.indiePMDR, ret.u);
                        }

                        ret.statups.put(SecondaryStatFlag.indieKeyDownMoving, Integer.valueOf(ret.indieDamReduceR));
                        break;
                     case 400031023:
                        ret.statups.put(SecondaryStatFlag.CriticalReinforce, ret.x);
                        break;
                     case 400031030:
                     case 500061033:
                        ret.statups.put(SecondaryStatFlag.StormGuard, ret.getW());
                        break;
                     case 400031044:
                        ret.statups.put(SecondaryStatFlag.RoyalKnights, 1);
                        break;
                     case 400031053:
                     case 500061016:
                        ret.statups.put(SecondaryStatFlag.AutoChargeStackOnOff, 1);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 400031055:
                        ret.statups.put(SecondaryStatFlag.RepeatingCrossbowCartridge, ret.x);
                        break;
                     case 400031062:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        ret.statups.put(SecondaryStatFlag.ThanatosDescent, 1);
                        break;
                     case 400041001:
                        ret.statups.put(SecondaryStatFlag.SpreadThrow, 1);
                        break;
                     case 400041002:
                     case 400041003:
                     case 400041004:
                     case 400041005:
                     case 500061025:
                     case 500061026:
                     case 500061027:
                     case 500061028:
                        ret.statups.put(SecondaryStatFlag.ShadowAssault, 0);
                        break;
                     case 400041007:
                        ret.statups.put(SecondaryStatFlag.MegaSmasher, 1);
                        ret.statups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
                        break;
                     case 400041008:
                        ret.statups.put(SecondaryStatFlag.ShadowSpear, 1);
                        break;
                     case 400041029:
                     case 400041031:
                     case 500061059:
                     case 500061060:
                        ret.statups.put(SecondaryStatFlag.OverloadMode, 1);
                        ret.duration = Integer.MAX_VALUE;
                        break;
                     case 400041035:
                        ret.statups.put(SecondaryStatFlag.ChainArtsFury, 1);
                        break;
                     case 400041040:
                        ret.statups.put(SecondaryStatFlag.MarkOfPhantom, 1);
                        ret.statups.put(SecondaryStatFlag.MarkOfPhantomDebuff, 1);
                        break;
                     case 400041048:
                     case 500061014:
                        ret.statups.put(SecondaryStatFlag.HyperCloneRampage, 1);
                        break;
                     case 400041052:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.getIndieDamR()));
                        ret.statups.put(SecondaryStatFlag.WrathOfGods, 1);
                        break;
                     case 400041057:
                        ret.statups.put(SecondaryStatFlag.PhotonRay, 1);
                        break;
                     case 400041061:
                        ret.statups.put(SecondaryStatFlag.ThrowBlasting, ret.getX());
                        break;
                     case 400041063:
                        ret.statups.put(SecondaryStatFlag.HeavenEarthHumanApparition, 1);
                        break;
                     case 400051006:
                        ret.statups.put(SecondaryStatFlag.BulletParty, ret.y);
                        break;
                     case 400051007:
                     case 400051013:
                     case 500061036:
                     case 500061037:
                        ret.statups.put(SecondaryStatFlag.indiePMDR, Integer.valueOf(ret.indiePMdR));
                        ret.statups.put(SecondaryStatFlag.LightningCascade, 1);
                        break;
                     case 400051009:
                        ret.statups.put(SecondaryStatFlag.MultipleOption, ret.q2);
                        break;
                     case 400051011:
                        ret.statups.put(SecondaryStatFlag.EnergyBurst, 1);
                        break;
                     case 400051018:
                     case 400051019:
                     case 400051020:
                        ret.statups.put(SecondaryStatFlag.SpotLight, Integer.valueOf(ret.level));
                        break;
                     case 400051033:
                        ret.statups.put(SecondaryStatFlag.OverDrive, 1);
                        break;
                     case 400051036:
                     case 500061013:
                        ret.statups.put(SecondaryStatFlag.InfinitySpell, 1);
                        break;
                     case 400051077:
                        ret.statups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(ret.indieDamR));
                        break;
                     default:
                        if (GameConstants.isSoulSummonSkill(sourceid)) {
                           ret.statups.put(SecondaryStatFlag.indieSummon, 1);
                        }
                  }

                  if (GameConstants.isNovice(sourceid / 10000)) {
                     switch (sourceid % 10000) {
                        case 99:
                        case 104:
                           ret.monsterStatus.put(MobTemporaryStatFlag.FREEZE, 1);
                           ret.duration *= 2;
                           break;
                        case 103:
                           ret.monsterStatus.put(MobTemporaryStatFlag.STUN, 1);
                           break;
                        case 1001:
                           if (sourceid / 10000 != 3001 && sourceid / 10000 != 3000) {
                              ret.statups.put(SecondaryStatFlag.Recovery, ret.x);
                           }
                           break;
                        case 1005:
                        case 10001215:
                        case 20001005:
                        case 20011005:
                        case 20021005:
                        case 20031005:
                        case 20041005:
                        case 20051005:
                        case 30001005:
                        case 30011005:
                        case 30021005:
                        case 50001215:
                        case 60001005:
                        case 60011005:
                        case 60011215:
                        case 60021005:
                        case 60031005:
                        case 100001005:
                        case 130001005:
                        case 130011005:
                        case 140001005:
                        case 150001005:
                        case 150011005:
                        case 150021005:
                        case 150031005:
                        case 160001005:
                        case 160011005:
                           ret.statups.put(SecondaryStatFlag.MaxLevelBuff, 4);
                        case 1010:
                        case 1011:
                        default:
                           break;
                        case 1026:
                        case 1142:
                           ret.duration = 2100000000;
                           break;
                        case 1105:
                           ret.statups.put(SecondaryStatFlag.IceSkill, 1);
                           ret.duration = 2100000000;
                           break;
                        case 8001:
                           ret.statups.put(SecondaryStatFlag.SoulArrow, ret.x);
                           break;
                        case 8002:
                           ret.statups.put(SecondaryStatFlag.SharpEyes, (ret.x << 8) + ret.criticaldamageMax);
                           break;
                        case 8003:
                           ret.statups.put(SecondaryStatFlag.MaxHP, ret.x);
                           ret.statups.put(SecondaryStatFlag.MaxMP, ret.x);
                           break;
                        case 8004:
                           ret.statups.put(SecondaryStatFlag.CombatOrders, ret.x);
                           break;
                        case 8005:
                           ret.statups.put(SecondaryStatFlag.UsefulAdvancedBless, 1);
                           break;
                        case 8006:
                           ret.statups.put(SecondaryStatFlag.SpeedInfusion, ret.x);
                     }
                  }
               } else {
                  switch (sourceid) {
                     case 2002093:
                        ret.statups.put(SecondaryStatFlag.indiePAD, 30);
                     case 2003516:
                     case 2003517:
                     case 2003518:
                     case 2003519:
                     case 2003520:
                     case 2003552:
                     case 2003553:
                     case 2003561:
                     case 2003566:
                     case 2003568:
                     case 2003570:
                     case 2003571:
                     case 2003572:
                     case 2003576:
                     case 2003591:
                     case 2022125:
                     case 2022126:
                     default:
                        break;
                     case 2003526:
                        ret.statups.put(SecondaryStatFlag.indiePAD, 30);
                        ret.statups.put(SecondaryStatFlag.indieMAD, 30);
                        ret.duration = 7200000;
                        break;
                     case 2003550:
                     case 2003560:
                        ret.statups.put(SecondaryStatFlag.PlusExpRate, 20);
                        ret.duration = 7200000;
                        break;
                     case 2003551:
                     case 2003559:
                     case 2003575:
                        ret.statups.put(SecondaryStatFlag.DropRate, 20);
                        ret.statups.put(SecondaryStatFlag.MesoUpByItem, 20);
                        ret.duration = 7200000;
                        break;
                     case 2003586:
                        ret.statups.put(SecondaryStatFlag.IgnoreMobPdpR, 20);
                        break;
                     case 2003587:
                        ret.statups.put(SecondaryStatFlag.BdR, Integer.valueOf(ret.bdR));
                        break;
                     case 2003592:
                        ret.statups.put(SecondaryStatFlag.indieBDR, Integer.valueOf(ret.indieBDR));
                        ret.duration = 2400000;
                        break;
                     case 2003594:
                        ret.statups.put(SecondaryStatFlag.indieIgnoreMobPdpR, 10);
                        ret.duration = 2400000;
                        break;
                     case 2003596:
                        ret.statups.put(SecondaryStatFlag.indieBDR, 20);
                        ret.duration = 2400000;
                        break;
                     case 2003597:
                        ret.statups.put(SecondaryStatFlag.indieDamR, 10);
                        break;
                     case 2003598:
                        ret.statups.put(SecondaryStatFlag.indieIgnoreMobPdpR, 20);
                        ret.duration = 2400000;
                        break;
                     case 2022033:
                        ret.statups.put(SecondaryStatFlag.HolySymbol, 50);
                        break;
                     case 2022129:
                        ret.statups.put(SecondaryStatFlag.indiePadR, 1);
                        break;
                     case 2022746:
                        ret.statups.clear();
                        ret.duration = 3600000;
                        ret.overTime = true;
                        ret.statups.put(SecondaryStatFlag.indiePadR, 3);
                        ret.statups.put(SecondaryStatFlag.indieMadR, 3);
                        ret.statups.put(SecondaryStatFlag.RepeatEffect, 1);
                        break;
                     case 2022747:
                        ret.statups.clear();
                        ret.duration = 3600000;
                        ret.overTime = true;
                        ret.statups.put(SecondaryStatFlag.indiePadR, 6);
                        ret.statups.put(SecondaryStatFlag.indieMadR, 6);
                        ret.statups.put(SecondaryStatFlag.RepeatEffect, 1);
                        break;
                     case 2022764:
                        ret.statups.clear();
                        ret.duration = 3600000;
                        ret.overTime = true;
                        ret.statups.put(SecondaryStatFlag.indiePadR, 1);
                        ret.statups.put(SecondaryStatFlag.indieMadR, 1);
                        ret.statups.put(SecondaryStatFlag.RepeatEffect, 1);
                        break;
                     case 2022823:
                        ret.statups.clear();
                        ret.duration = 3600000;
                        ret.overTime = true;
                        ret.statups.put(SecondaryStatFlag.indiePadR, 10);
                        ret.statups.put(SecondaryStatFlag.indieMadR, 10);
                        ret.statups.put(SecondaryStatFlag.RepeatEffect, 1);
                        break;
                     case 2023072:
                        ret.statups.put(SecondaryStatFlag.ItemUpByItem, 100);
                        break;
                     case 2023126:
                     case 2023554:
                        ret.statups.put(SecondaryStatFlag.indieBooster, Integer.valueOf(ret.indieBooster));
                        ret.duration = 1800000;
                        ret.overTime = true;
                        break;
                     case 2023658:
                     case 2023659:
                     case 2023660:
                        ret.statups.put(SecondaryStatFlag.indiePAD, 30);
                        ret.statups.put(SecondaryStatFlag.indieMAD, 30);
                        break;
                     case 2023661:
                     case 2023662:
                     case 2023663:
                        ret.statups.put(SecondaryStatFlag.LuckOfUnion, 50);
                        break;
                     case 2023664:
                     case 2023665:
                     case 2023666:
                        ret.statups.put(SecondaryStatFlag.WealthOfUnion, 50);
                        break;
                     case 2023882:
                     case 2450124:
                        ret.statups.clear();
                        ret.duration = 1800000;
                        ret.statups.put(SecondaryStatFlag.indieEXP, 50);
                        ret.overTime = true;
                        break;
                     case 2024012:
                        ret.statups.put(SecondaryStatFlag.indieBDR, 15);
                        ret.statups.put(SecondaryStatFlag.indieIgnoreMobPdpR, 15);
                        break;
                     case 2450064:
                     case 2450147:
                     case 2450148:
                     case 2450149:
                        ret.statups.put(SecondaryStatFlag.ExpBuffRate, 100);
                        break;
                     case 2450134:
                        ret.statups.put(SecondaryStatFlag.ExpBuffRate, 200);
                  }
               }

               if (sourceid != 1211008 && sourceid != 2111010 && ret.isPoison()) {
                  ret.monsterStatus.put(MobTemporaryStatFlag.BURNED, 1);
               }

               if (ret.isMorph() || ret.isPirateMorph()) {
                  ret.statups.put(SecondaryStatFlag.Morph, ret.getMorph());
               }
               break;
            }

            ret.petsCanConsume.add(dd);
            i++;
         }
      } catch (Exception var21) {
         System.out.println("SecondaryStatEffect.loadFromData 함수 실행중 오류발생" + var21.toString());
         var21.printStackTrace();
      }

      return ret;
   }

   public final void applyPassive(MapleCharacter applyto, MapleMapObject obj) {
      if (this.makeChanceResult() && !GameConstants.isDemonSlayer(applyto.getJob())) {
         switch (this.sourceid) {
            case 2100000:
            case 2200000:
            case 2300000:
               if (obj == null || obj.getType() != MapleMapObjectType.MONSTER) {
                  return;
               }

               MapleMonster mob = (MapleMonster) obj;
               if (!mob.getStats().isBoss()) {
                  int absorbMp = Math.min((int) (mob.getMobMaxMp() * (this.getX() / 100.0)), mob.getMp());
                  if (absorbMp > 0) {
                     mob.setMp(mob.getMp() - absorbMp);
                     applyto.getStat().setMp(applyto.getStat().getMp() + absorbMp, applyto);
                     SkillEffect e = new SkillEffect(applyto.getId(), applyto.getLevel(), this.sourceid, this.level,
                           null);
                     applyto.getClient().getSession().writeAndFlush(e.encodeForLocal());
                     applyto.getMap().broadcastMessage(applyto, e.encodeForRemote(), false);
                  }
               }
         }
      }
   }

   public final boolean applyTo(MapleCharacter chr) {
      return this.applyTo(chr, chr, true, null, this.duration, (byte) 0, false, false);
   }

   public final boolean applyTo(MapleCharacter chr, boolean exclusive) {
      return this.applyTo(chr, chr, true, null, this.duration, (byte) 0, exclusive, false);
   }

   public final boolean applyTo(MapleCharacter chr, Point pos) {
      return this.applyTo(chr, chr, true, pos, this.duration, (byte) 0, false, false);
   }

   public final boolean applyTo(MapleCharacter chr, Point pos, byte rltype) {
      return this.applyTo(chr, chr, true, pos, this.duration, rltype, true, false);
   }

   public final boolean applyTo(MapleCharacter chr, Point pos, byte rltype, boolean exclusive) {
      return this.applyTo(chr, chr, true, pos, this.duration, rltype, exclusive, false);
   }

   public final boolean applyTo(MapleCharacter chr, Point pos, byte rltype, boolean exclusive, boolean attack) {
      return this.applyTo(chr, chr, true, pos, this.duration, rltype, exclusive, attack);
   }

   public final boolean applyTo(
         final MapleCharacter applyfrom, MapleCharacter applyto, boolean primary, Point pos, int newDuration,
         byte rltype, boolean exclusive, boolean attack) {
      Skill skill_ = SkillFactory.getSkill(this.sourceid);
      if (skill_ != null) {
         if (skill_.getType() == 40) {
            exclusive = false;
         }
      } else {
         exclusive = true;
      }

      if (this.sourceid == 30010186) {
         exclusive = true;
      }

      if (this.sourceid == 2003599) {
         this.overTime = true;
      }

      if ((!this.isSoaring_Mount() || applyfrom.getBuffedValue(SecondaryStatFlag.RideVehicle) != null)
            && (!this.isSoaring_Normal() || applyfrom.getMap().canSoar())) {
         if (this.sourceid == 4341006 && applyfrom.getBuffedValue(SecondaryStatFlag.ShadowPartner) == null) {
            applyfrom.getClient().getSession().writeAndFlush(CWvsContext.enableActions(applyfrom, exclusive));
            return false;
         } else if (this.sourceid != 33101008
               || applyfrom.getBuffedValue(SecondaryStatFlag.indieSummon) == null && applyfrom.canSummon()) {
            if (this.isShadow() && applyfrom.getJob() / 100 % 10 != 4) {
               applyfrom.getClient().getSession().writeAndFlush(CWvsContext.enableActions(applyfrom, exclusive));
               return false;
            } else if (this.sourceid == 33101004 && applyfrom.getMap().isTown()) {
               applyfrom.dropMessage(5, "You may not use this skill in towns.");
               applyfrom.getClient().getSession().writeAndFlush(CWvsContext.enableActions(applyfrom, exclusive));
               return false;
            } else if ((this.sourceid < 33001007 || this.sourceid > 33001015)
                  && (newDuration == 2100000000 || newDuration == Integer.MAX_VALUE)
                  && applyto.hasBuffBySkillID(this.sourceid)) {
               if (this.sourceid == 15001022 && !attack) {
                  applyto.temporaryStatResetBySkillID(this.sourceid);
                  applyto.send(CWvsContext.enableActions(applyto, exclusive));
               } else {
                  applyto.temporaryStatResetBySkillID(this.sourceid);
                  applyto.send(CWvsContext.enableActions(applyto, exclusive));
               }

               return false;
            } else if (!this.isMonsterRiding()
                  || this.sourceid == 35001002
                  || this.sourceid == 35111003
                  || applyfrom.getBuffedValue(SecondaryStatFlag.Mechanic) == null
                        && !applyfrom.hasBuffBySkillID(35121013)
                        && !applyfrom.hasBuffBySkillID(35121005)
                        && !applyfrom.hasBuffBySkillID(35111004)
                        && !applyfrom.hasBuffBySkillID(35111003)) {
               boolean undead = applyto.hasDisease(SecondaryStatFlag.Undead);
               long hpchange = this.calcHPChange(applyfrom, primary);
               long mpchange = this.calcMPChange(applyfrom, primary);
               int powerchange = this.calcPowerChange(applyfrom);
               if (this.sourceid == 400011057 || this.sourceid == 400011038) {
                  hpchange = (long) (hpchange
                        - applyfrom.getStat().getCurrentMaxHp(applyfrom) * (this.getHpRCon() * 0.01));
               }

               if (this.sourceid == 400011010) {
                  hpchange += this.getY();
               }

               PlayerStats stat = applyto.getStat();
               if (primary) {
                  if (this.itemConNo != 0 && !applyto.isClone() && !applyto.inPVP()) {
                     if (!applyto.haveItem(this.itemCon, this.itemConNo, false, true)) {
                        applyto.getClient().getSession().writeAndFlush(CWvsContext.enableActions(applyfrom, exclusive));
                        return false;
                     }

                     MapleInventoryManipulator.removeById(
                           applyto.getClient(), GameConstants.getInventoryType(this.itemCon), this.itemCon,
                           this.itemConNo, false, true);
                  }
               } else if (!primary && (this.isResurrection() || this.isGuardianSpirit())) {
                  hpchange = stat.getCurrentMaxHp(applyto);
                  applyto.setStance(0);
               }

               if (this.isDispel() && this.makeChanceResult()) {
                  applyto.dispelDebuffs();
               } else if (this.isHeroWill()) {
                  applyto.dispelDebuffs();
               } else if (this.cureDebuffs.size() > 0) {
                  for (SecondaryStatFlag debuff : this.cureDebuffs) {
                     applyfrom.dispelDebuff(debuff);
                  }
               } else if (this.isMPRecovery()) {
                  long toDecreaseHP = stat.getCurrentMaxHp(applyto) / 100L * 10L;
                  if (stat.getHp() > toDecreaseHP) {
                     hpchange += -toDecreaseHP;
                     mpchange += toDecreaseHP / 100L * this.getY();
                  } else {
                     hpchange = stat.getHp() == 1L ? 0L : stat.getHp() - 1L;
                  }
               }

               if (hpchange > 0L && applyfrom.getBuffedValue(SecondaryStatFlag.DemonFrenzy) != null) {
                  SecondaryStatEffect effect = applyfrom.getBuffedEffect(SecondaryStatFlag.DemonFrenzy);
                  if (effect != null) {
                     int delta = (int) (applyfrom.getStat().getCurrentMaxHp(applyfrom) * (effect.getW() * 0.01));
                     if (hpchange > delta) {
                        hpchange = delta;
                     }
                  }
               }

               Map<MapleStat, Long> hpmpupdate = new EnumMap<>(MapleStat.class);
               if (hpchange != 0L) {
                  if (hpchange < 0L && -hpchange > stat.getHp() && !undead) {
                     applyto.getClient().getSession().writeAndFlush(CWvsContext.enableActions(applyfrom, exclusive));
                     return false;
                  }

                  stat.setHp(stat.getHp() + hpchange, applyto);
                  hpmpupdate.put(MapleStat.HP, stat.getHp());
               }

               if (mpchange != 0L) {
                  if (mpchange < 0L && -mpchange > stat.getMp()) {
                     applyto.getClient().getSession().writeAndFlush(CWvsContext.enableActions(applyfrom, exclusive));
                     return false;
                  }

                  stat.setMp(stat.getMp() + mpchange, applyto);
                  hpmpupdate.put(MapleStat.MP, stat.getMp());
               }

               hpmpupdate.put(MapleStat.HP, stat.getHp());
               applyto.getClient().getSession()
                     .writeAndFlush(CWvsContext.updatePlayerStats(hpmpupdate, exclusive, applyto));
               applyto.checkDead();
               if (powerchange != 0) {
                  if (applyto.getXenonSurplus() - powerchange < 0) {
                     return false;
                  }

                  if (applyto.getBuffedValue(SecondaryStatFlag.AmaranthGenerator) == null
                        && applyto.getBuffedValue(SecondaryStatFlag.OverloadMode) == null) {
                     applyto.gainXenonSurplus((short) (-powerchange));
                  }
               }

               if (this.expinc != 0) {
                  applyto.gainExp(this.expinc, true, true, false);
                  ExpEffect eff = new ExpEffect(applyto.getId(), this.expinc);
                  applyto.getClient().getSession().writeAndFlush(eff.encodeForLocal());
                  applyto.getMap().broadcastMessage(applyto, eff.encodeForRemote(), false);
               } else if (this.isReturnScroll()) {
                  this.applyReturnScroll(applyto);
               } else if (this.useLevel > 0 && !this.skill) {
                  applyto.setExtractor(new Extractor(applyto, this.sourceid, this.useLevel * 50, 1440));
                  applyto.getMap().spawnExtractor(applyto.getExtractor());
               } else if (this.isMistEruption()) {
                  int i = this.y;
                  if (applyto.getPendingThrowingBomb() != null) {
                     applyto.sendExplodingThrowingBomb(applyto.getPendingThrowingBomb());
                     applyto.clearPendingThrowingBomb();
                     applyto.clearCooldown(2121011);
                  }

                  for (AffectedArea m : applyto.getMap().getAllMistsThreadsafe()) {
                     if (m.getOwnerId() == applyto.getId() && m.getSourceSkillID() == 2111003) {
                        if (m.getSchedule() != null) {
                           m.getSchedule().cancel(false);
                           m.setSchedule(null);
                        }

                        if (m.getPoisonSchedule() != null) {
                           m.getPoisonSchedule().cancel(false);
                           m.setPoisonSchedule(null);
                        }

                        applyto.getMap()
                              .broadcastMessage(CField.removeAffectedArea(m.getObjectId(), m.getSourceSkillID(), true));
                        applyto.getMap().removeMapObject(m);
                        if (--i <= 0) {
                           break;
                        }
                     }
                  }
               } else if (this.cosmetic > 0) {
                  if (this.cosmetic >= 30000) {
                     applyto.setHair(this.cosmetic);
                     applyto.updateSingleStat(MapleStat.HAIR, this.cosmetic);
                  } else if (this.cosmetic >= 20000) {
                     applyto.setFace(this.cosmetic);
                     applyto.updateSingleStat(MapleStat.FACE, this.cosmetic);
                  } else if (this.cosmetic < 100) {
                     applyto.setSkinColor((byte) this.cosmetic);
                     applyto.updateSingleStat(MapleStat.SKIN, this.cosmetic);
                  }

                  applyto.equipChanged();
               } else if (this.bs > 0) {
                  if (!applyto.inPVP()) {
                     return false;
                  }

                  int x = Integer.parseInt(applyto.getEventInstance().getProperty(String.valueOf(applyto.getId())));
                  applyto.getEventInstance().setProperty(String.valueOf(applyto.getId()), String.valueOf(x + this.bs));
                  applyto.getClient().getSession().writeAndFlush(CField.getPVPScore(x + this.bs, false));
               } else if (this.iceGageCon > 0) {
                  if (!applyto.inPVP()) {
                     return false;
                  }

                  int x = Integer.parseInt(applyto.getEventInstance().getProperty("icegage"));
                  if (x < this.iceGageCon) {
                     return false;
                  }

                  applyto.getEventInstance().setProperty("icegage", String.valueOf(x - this.iceGageCon));
                  applyto.getClient().getSession().writeAndFlush(CField.getPVPIceGage(x - this.iceGageCon));
                  applyto.applyIceGage(x - this.iceGageCon);
               } else if (this.recipe > 0) {
                  if (applyto.getSkillLevel(this.recipe) > 0
                        || applyto.getProfessionLevel(this.recipe / 10000 * 10000) < this.reqSkillLevel) {
                     return false;
                  }

                  applyto.changeSingleSkillLevel(
                        SkillFactory.getCraft(this.recipe),
                        Integer.MAX_VALUE,
                        this.recipeUseCount,
                        this.recipeValidDay > 0
                              ? System.currentTimeMillis() + this.recipeValidDay * 24L * 60L * 60L * 1000L
                              : -1L);
               } else if (this.isSpiritClaw() && !applyto.isClone()) {
                  MapleInventory use = applyto.getInventory(MapleInventoryType.USE);
                  boolean itemz = false;
                  Item item = use.findById(applyto.getSpiritJabelinConsumeID());
                  if (item == null) {
                     return false;
                  }

                  applyto.gainItem(applyto.getSpiritJabelinConsumeID(), (short) (-this.getBulletConsume()), false, -1L,
                        "");
               } else if ((this.effectedOnEnemy > 0 || this.effectedOnAlly > 0) && primary && applyto.inPVP()) {
                  int type = Integer.parseInt(applyto.getEventInstance().getProperty("type"));
                  if (type > 0 || this.effectedOnEnemy > 0) {
                     for (MapleCharacter chr : applyto.getMap().getCharactersThreadsafe()) {
                        if (chr.getId() != applyto.getId()
                              && (this.effectedOnAlly > 0 ? chr.getTeam() == applyto.getTeam()
                                    : chr.getTeam() != applyto.getTeam() || type == 0)) {
                           this.applyTo(applyto, chr, false, pos, newDuration, rltype, exclusive, attack);
                        }
                     }
                  }
               } else if (this.mobSkill > 0 && this.mobSkillLevel > 0 && primary && applyto.inPVP()) {
                  if (this.effectedOnEnemy > 0) {
                     int type = Integer.parseInt(applyto.getEventInstance().getProperty("type"));

                     for (MapleCharacter chrx : applyto.getMap().getCharactersThreadsafe()) {
                        if (chrx.getId() != applyto.getId() && (chrx.getTeam() != applyto.getTeam() || type == 0)) {
                           chrx.disease(this.mobSkill, this.mobSkillLevel);
                        }
                     }
                  } else {
                     applyto.disease(this.mobSkill, this.mobSkillLevel);
                  }
               } else if (this.randomPickup != null && this.randomPickup.size() > 0) {
                  MapleItemInformationProvider.getInstance()
                        .getItemEffect(this.randomPickup.get(Randomizer.nextInt(this.randomPickup.size())))
                        .applyTo(applyto);
               }

               for (Entry<MapleTrait.MapleTraitType, Integer> t : this.traits.entrySet()) {
                  applyto.getTrait(t.getKey()).addExp(t.getValue(), applyto);
               }

               SummonMoveAbility summonMovementType = this.getSummonMovementType();
               if (summonMovementType != null && !applyto.isClone() && this.sourceid != 151100002) {
                  int summId = this.sourceid;
                  if (summId == 131001026) {
                     summId = 131003026;
                  }

                  if (this.sourceid == 5211014) {
                     applyfrom.temporaryStatResetBySkillID(5211014);
                  } else if (this.sourceid != 5210015 && this.sourceid != 5210016 && this.sourceid != 5210017
                        && this.sourceid != 5210018) {
                     if ((this.sourceid != 14000027 || applyfrom.getBuffedEffect(SecondaryStatFlag.Dominion) == null)
                           && applyfrom.getEquippedSoulSkill() != this.getSourceId()
                           && this.sourceid != 400021068
                           && this.sourceid != 500061012
                           && this.sourceid != 5321004
                           && this.sourceid != 35111002
                           && this.sourceid != 400021047
                           && this.sourceid != 400021071) {
                        applyfrom.temporaryStatResetBySkillID(this.getSourceId());
                     }
                  } else {
                     for (int i = 5210015; i <= 5210018; i++) {
                        applyfrom.temporaryStatResetBySkillID(i);
                     }
                  }

                  if (this.sourceid == 14111024) {
                     applyfrom.temporaryStatResetBySkillID(14111024);
                  }

                  boolean remove = false;
                  if (this.sourceid == 23111009 || this.sourceid == 23111010 || this.sourceid == 23111011) {
                     int count = 0;

                     for (int i = 23111009; i <= 23111011; i++) {
                        count += applyfrom.getMap().getSummonCount(applyfrom, i);
                     }

                     if (count >= 2) {
                        for (Summoned summon : new ArrayList<>(applyfrom.getSummons())) {
                           if (summon.getSkill() == 23111009 || summon.getSkill() == 23111010
                                 || summon.getSkill() == 23111011) {
                              applyfrom.temporaryStatResetBySkillID(summon.getSkill());
                              break;
                           }
                        }
                     }
                  } else if (this.sourceid != 400021068 && this.sourceid != 500061012) {
                     if (this.sourceid != 12120013 && this.sourceid != 12120014) {
                        if ((this.sourceid != 14000027 || applyfrom.getBuffedEffect(SecondaryStatFlag.Dominion) == null)
                              && this.sourceid != 400021068
                              && this.sourceid != 500061012
                              && this.sourceid != 35111002
                              && this.sourceid != 400021047
                              && this.sourceid != 14120008
                              && this.sourceid != 14110029
                              && this.sourceid != 14100027) {
                           for (Summoned summonx : applyfrom.getSummons()) {
                              if (summonx.getSkill() == this.sourceid) {
                                 applyfrom.getMap().broadcastMessage(CField.SummonPacket.removeSummon(summonx, true));
                                 applyfrom.getMap().removeMapObject(summonx);
                                 applyfrom.removeVisibleMapObject(summonx);
                                 applyfrom.removeSummon(summonx);
                              }
                           }
                        }
                     } else {
                        for (Summoned summonxx : applyfrom.getSummons()) {
                           if (summonxx.getSkill() == 12120013 || summonxx.getSkill() == 12120014) {
                              applyfrom.getMap().broadcastMessage(CField.SummonPacket.removeSummon(summonxx, true));
                              applyfrom.getMap().removeMapObject(summonxx);
                              applyfrom.removeVisibleMapObject(summonxx);
                              applyfrom.removeSummon(summonxx);
                              break;
                           }
                        }
                     }
                  } else {
                     long count = applyfrom.getSummons().stream()
                           .filter(sx -> sx.getSkill() == 400021068 || sx.getSkill() == 500061012).count();
                     if (count >= 2L) {
                        for (Summoned summonxxx : applyfrom.getSummons()) {
                           if (summonxxx.getSkill() == 400021068 || summonxxx.getSkill() == 500061012) {
                              applyfrom.getMap().broadcastMessage(CField.SummonPacket.removeSummon(summonxxx, true));
                              applyfrom.getMap().removeMapObject(summonxxx);
                              applyfrom.removeVisibleMapObject(summonxxx);
                              applyfrom.removeSummon(summonxxx);
                              break;
                           }
                        }
                     }

                     applyfrom.setAutoChargeStack(applyfrom.getAutoChargeStack() - 1);
                     applyfrom.temporaryStatSet(400051042, Integer.MAX_VALUE, SecondaryStatFlag.AutoChargeStack,
                           applyfrom.getAutoChargeStack());
                  }

                  if (this.sourceid == 3111002) {
                     Skill elite = SkillFactory.getSkill(3120012);
                     if (applyfrom.getTotalSkillLevel(elite) > 0) {
                        return elite.getEffect(applyfrom.getTotalSkillLevel(elite))
                              .applyTo(applyfrom, applyto, primary, pos, newDuration, rltype, exclusive, attack);
                     }
                  } else if (this.sourceid == 3211002) {
                     Skill elite = SkillFactory.getSkill(3220012);
                     if (applyfrom.getTotalSkillLevel(elite) > 0) {
                        return elite.getEffect(applyfrom.getTotalSkillLevel(elite))
                              .applyTo(applyfrom, applyto, primary, pos, newDuration, rltype, exclusive, attack);
                     }
                  } else if (this.sourceid != 400001012) {
                     if (this.sourceid == 14121054) {
                        for (Summoned summonxxxx : new ArrayList<Summoned>() {
                           {
                              this.add(applyfrom.getSummonBySkillID(14121055));
                              this.add(applyfrom.getSummonBySkillID(14121056));
                           }
                        }) {
                           if (summonxxxx != null) {
                              applyfrom.getMap().broadcastMessage(CField.SummonPacket.removeSummon(summonxxxx, true));
                              applyfrom.getMap().removeMapObject(summonxxxx);
                              applyfrom.removeVisibleMapObject(summonxxxx);
                              applyfrom.removeSummon(summonxxxx);
                           }
                        }

                        if (applyfrom.getBuffedValue(SecondaryStatFlag.ShadowServant) == null) {
                           int servantLevel = applyfrom.getTotalSkillLevel(14111024);
                           SecondaryStatEffect effect = SkillFactory.getSkill(14111024).getEffect(servantLevel);
                           if (effect != null) {
                              effect.applyTo(applyfrom);
                           }
                        }

                        long summonRemoveTime = System.currentTimeMillis() + this.getDuration();
                        Summoned tosummon2 = new Summoned(
                              applyfrom,
                              14121055,
                              this.getLevel(),
                              new Point(pos == null ? applyfrom.getTruePosition() : pos),
                              SummonMoveAbility.SHADOW_SERVANT,
                              rltype,
                              summonRemoveTime);
                        Summoned tosummon3 = new Summoned(
                              applyfrom,
                              14121056,
                              this.getLevel(),
                              new Point(pos == null ? applyfrom.getTruePosition() : pos),
                              SummonMoveAbility.SHADOW_SERVANT,
                              rltype,
                              summonRemoveTime);
                        applyfrom.getMap().spawnSummon(tosummon2, this.getDuration());
                        applyfrom.getMap().spawnSummon(tosummon3, this.getDuration());
                        applyfrom.addSummon(tosummon2);
                        applyfrom.addSummon(tosummon3);
                        applyfrom.temporaryStatSet(14121054, this.getLevel(), this.getDuration(), this.getStatups());
                        return true;
                     }

                     if (this.sourceid == 400021032) {
                        Summoned summonxxxxx = applyfrom.getSummonBySkillID(2321003);
                        if (summonxxxxx != null) {
                           applyfrom.getMap().broadcastMessage(CField.SummonPacket.removeSummon(summonxxxxx, true));
                           applyfrom.getMap().removeMapObject(summonxxxxx);
                           applyfrom.removeVisibleMapObject(summonxxxxx);
                           applyfrom.removeSummon(summonxxxxx);
                        }

                        int realDuration = this.getDuration() + 10000;
                        long summonRemoveTime = System.currentTimeMillis() + realDuration;
                        Summoned tosummon2 = new Summoned(
                              applyfrom,
                              400021033,
                              this.getLevel(),
                              new Point(pos == null ? applyfrom.getTruePosition() : pos),
                              SummonMoveAbility.FOLLOW,
                              rltype,
                              summonRemoveTime);
                        applyfrom.getMap().spawnSummon(tosummon2, realDuration);
                        applyfrom.addSummon(tosummon2);
                     } else if (this.sourceid == 400011012) {
                        for (Summoned summonxxxxx : applyfrom.getSummons()) {
                           if (summonxxxxx.getSkill() >= 400011012 && summonxxxxx.getSkill() <= 400011014) {
                              applyfrom.getMap().broadcastMessage(CField.SummonPacket.removeSummon(summonxxxxx, true));
                              applyfrom.getMap().removeMapObject(summonxxxxx);
                              applyfrom.removeVisibleMapObject(summonxxxxx);
                              applyfrom.removeSummon(summonxxxxx);
                              break;
                           }
                        }

                        long summonRemoveTime = System.currentTimeMillis() + this.getDuration();

                        for (int i = 0; i < 3; i++) {
                           Summoned tosummon = new Summoned(
                                 applyfrom,
                                 400011012 + i,
                                 this.getLevel(),
                                 new Point(pos == null ? applyfrom.getTruePosition() : pos),
                                 SummonMoveAbility.FIX_V_MOVE,
                                 rltype,
                                 summonRemoveTime);
                           applyfrom.getMap().spawnSummon(tosummon, this.getDuration());
                           applyfrom.addSummon(tosummon);
                        }

                        return true;
                     }
                  } else {
                     for (Summoned summonxxxxxx : applyfrom.getSummons()) {
                        if (summonxxxxxx.getSkill() == 3111005 || summonxxxxxx.getSkill() == 3211005
                              || summonxxxxxx.getSkill() == 3311009) {
                           applyfrom.getMap().broadcastMessage(CField.SummonPacket.removeSummon(summonxxxxxx, true));
                           applyfrom.getMap().removeMapObject(summonxxxxxx);
                           applyfrom.removeVisibleMapObject(summonxxxxxx);
                           applyfrom.removeSummon(summonxxxxxx);
                           applyfrom.temporaryStatReset(SecondaryStatFlag.indieSummon);
                           break;
                        }
                     }
                  }

                  long duration = this.getDuration(this.getDuration(), applyfrom);
                  long summonRemoveTime = System.currentTimeMillis() + duration;
                  Point point = new Point(pos == null ? applyfrom.getTruePosition() : pos);
                  if (this.sourceid == 400051011) {
                     point.y -= 100;
                  }

                  if (this.sourceid == 400001039) {
                     summId = 400001040;
                     duration = 50000L;
                  } else if (this.sourceid == 400001059) {
                     summId = 400001060;
                     duration = 51000L;
                  } else if (this.sourceid == 400021032) {
                     duration = this.getDuration() + 10000;
                  }

                  Summoned tosummon = new Summoned(
                        applyfrom,
                        summId,
                        this.getLevel(),
                        applyfrom.getMap().calcDropPos(point, applyfrom.getTruePosition()),
                        summonMovementType,
                        rltype,
                        summonRemoveTime);
                  if (!tosummon.isPuppet()) {
                     applyfrom.getCheatTracker().resetSummonAttack();
                  }

                  applyfrom.getMap().spawnSummon(tosummon, (int) duration);
                  applyfrom.addSummon(tosummon);
                  tosummon.addHP(this.x);
                  if (this.sourceid == 152121005) {
                     List<Summoned> toRemove = new LinkedList<>();

                     try {
                        for (Summoned summonxxxxxxx : applyfrom.getSummonsReadLock()) {
                           if (summonxxxxxxx.getSkill() == 152101008 || summonxxxxxxx.getSkill() == 152001003) {
                              toRemove.add(summonxxxxxxx);
                           }
                        }
                     } finally {
                        applyfrom.unlockSummonsReadLock();
                     }

                     for (Summoned summonxxxxxxxx : toRemove) {
                        applyfrom.getMap().broadcastMessage(CField.SummonPacket.removeSummon(summonxxxxxxxx, true));
                        applyfrom.getMap().removeMapObject(summonxxxxxxxx);
                        applyfrom.removeVisibleMapObject(summonxxxxxxxx);
                        applyfrom.removeSummon(summonxxxxxxxx);
                     }

                     for (int i = 0; i < 5; i++) {
                        Summoned summonxxxxxxxx = new Summoned(
                              applyfrom,
                              152121006,
                              this.getLevel(),
                              new Point(pos == null ? applyfrom.getTruePosition() : pos),
                              SummonMoveAbility.BIRD_FOLLOW,
                              rltype,
                              summonRemoveTime);
                        applyfrom.getMap().spawnSummon(summonxxxxxxxx, (int) duration);
                        applyfrom.addSummon(summonxxxxxxxx);
                     }

                     Summoned crystal = applyfrom.getSummonByMovementType(SummonMoveAbility.SHADOW_SERVANT_EXTEND);
                     if (crystal != null) {
                        crystal.setEnableEnergySkill(3, 0);
                        applyfrom.getMap().broadcastMessage(applyfrom,
                              CField.summonCrystalToggleSkill(applyfrom, crystal, 2), true);
                        applyfrom.getMap().broadcastMessage(applyfrom, CField.summonSetEnergy(applyfrom, crystal, 3),
                              true);
                     }
                  } else if (this.isBeholder()) {
                     tosummon.addHP(1);
                  } else if (this.sourceid == 35111002) {
                     List<Integer> count = new ArrayList<>();

                     try {
                        for (Summoned s : applyfrom.getSummonsReadLock()) {
                           if (s.getSkill() == this.sourceid) {
                              count.add(s.getObjectId());
                           }
                        }
                     } finally {
                        applyfrom.unlockSummonsReadLock();
                     }

                     if (count.size() != 3) {
                        return true;
                     }

                     applyfrom.getClient().getSession()
                           .writeAndFlush(CField.skillCooldown(this.sourceid, this.getCooldown(applyfrom)));
                     applyfrom.addCooldown(this.sourceid, System.currentTimeMillis(), this.getCooldown(applyfrom));
                     applyfrom.getMap().broadcastMessage(
                           CField.teslaTriangle(applyfrom.getId(), count.get(0), count.get(1), count.get(2)));
                  } else if (this.sourceid == 35121003) {
                     applyfrom.getClient().getSession().writeAndFlush(CWvsContext.enableActions(applyfrom, exclusive));
                  }
               } else if (this.isMechDoor()) {
                  int newId = 0;
                  boolean applyBuff = false;
                  if (applyto.getMechDoors().size() >= 2) {
                     OpenGate removex = applyto.getMechDoors().remove(0);
                     newId = removex.getId();
                     applyto.getMap().broadcastMessage(CField.removeMechDoor(removex, true));
                     applyto.getMap().removeMapObject(removex);
                     applyto.removeOneMechDoor(removex);
                  } else {
                     for (OpenGate d : applyto.getMechDoors()) {
                        if (d.getId() == newId) {
                           applyBuff = true;
                           newId = 1;
                           break;
                        }
                     }
                  }

                  OpenGate door = new OpenGate(applyto, new Point(pos == null ? applyto.getTruePosition() : pos),
                        newId);
                  applyto.getMap().spawnMechDoor(door);
                  applyto.addMechDoor(door);
                  if (!applyBuff) {
                     return true;
                  }
               }

               if (primary && this.availableMap != null) {
                  for (Pair<Integer, Integer> e : this.availableMap) {
                     if (applyto.getMapId() < e.left || applyto.getMapId() > e.right) {
                        applyto.getClient().getSession().writeAndFlush(CWvsContext.enableActions(applyfrom, exclusive));
                        return true;
                     }
                  }
               }

               if (this.overTime) {
                  this.applyBuffEffect(applyfrom, applyto, primary, newDuration, attack, rltype);
               }

               if (this.skill) {
                  this.removeMonsterBuff(applyfrom);
               }

               if (primary) {
                  if ((this.overTime || this.isHeal()) && !attack) {
                     this.applyBuff(applyfrom, newDuration);
                  }

                  if (this.isMonsterBuff()) {
                     this.applyMonsterBuff(applyfrom);
                  }
               }

               if (this.isMagicDoor()) {
                  TownPortal door = new TownPortal(applyto, new Point(pos == null ? applyto.getTruePosition() : pos),
                        this.sourceid);
                  if (door.getTownPortal() != null) {
                     applyto.getMap().spawnDoor(door);
                     applyto.addDoor(door);
                     TownPortal townDoor = new TownPortal(door);
                     applyto.addDoor(townDoor);
                     door.getTown().spawnDoor(townDoor);
                     if (applyto.getParty() != null) {
                        applyto.silentPartyUpdate();
                     }
                  } else {
                     applyto.dropMessage(5, "You may not spawn a door because all doors in the town are taken.");
                  }
               } else if (this.isMist()) {
                  Rectangle bounds = this.calculateBoundingBox(pos != null ? pos : applyfrom.getPosition(),
                        rltype == 1);
                  AffectedArea mist = new AffectedArea(
                        bounds,
                        applyfrom,
                        this,
                        pos != null ? pos : applyfrom.getPosition(),
                        rltype,
                        System.currentTimeMillis() + this.getDuration(this.getDuration(), applyfrom));
                  applyfrom.getMap().spawnMist(mist);
                  applyfrom.temporaryStatSet(this.sourceid, this.getDuration(this.getDuration(), applyfrom),
                        SecondaryStatFlag.indieFlyAcc, 1);
               }

               if (this.rewardMeso != 0) {
                  applyto.gainMeso(this.rewardMeso, false);
               }

               if (this.rewardItem != null && this.totalprob > 0) {
                  for (Triple<Integer, Integer, Integer> reward : this.rewardItem) {
                     if (MapleInventoryManipulator.checkSpace(applyto.getClient(), reward.left, reward.mid, "")
                           && reward.right > 0
                           && Randomizer.nextInt(this.totalprob) < reward.right) {
                        if (GameConstants.getInventoryType(reward.left) == MapleInventoryType.EQUIP) {
                           Item item = MapleItemInformationProvider.getInstance().getEquipById(reward.left);
                           item.setGMLog("Reward item (effect): " + this.sourceid + " on "
                                 + FileoutputUtil.CurrentReadable_Date());
                           MapleInventoryManipulator.addbyItem(applyto.getClient(), item);
                        } else {
                           MapleInventoryManipulator.addById(
                                 applyto.getClient(),
                                 reward.left,
                                 reward.mid.shortValue(),
                                 "Reward item (effect): " + this.sourceid + " on "
                                       + FileoutputUtil.CurrentReadable_Date());
                        }
                     }
                  }
               }

               return true;
            } else {
               applyfrom.send(CWvsContext.enableActions(applyto, exclusive));
               return false;
            }
         } else {
            applyfrom.getClient().getSession().writeAndFlush(CWvsContext.enableActions(applyfrom, exclusive));
            return false;
         }
      } else {
         applyfrom.getClient().getSession().writeAndFlush(CWvsContext.enableActions(applyfrom, exclusive));
         return false;
      }
   }

   public final boolean applyReturnScroll(MapleCharacter applyto) {
      if (this.moveTo != -1 && (applyto.getMap().getReturnMapId() != applyto.getMapId() || this.sourceid == 2031010
            || this.sourceid == 2030021)) {
         Field target;
         if (this.moveTo == 999999999) {
            target = applyto.getMap().getReturnMap();
         } else {
            target = GameServer.getInstance(applyto.getClient().getChannel()).getMapFactory().getMap(this.moveTo);
            if (target.getId() / 10000000 != 60
                  && applyto.getMapId() / 10000000 != 61
                  && target.getId() / 10000000 != 21
                  && applyto.getMapId() / 10000000 != 20
                  && target.getId() / 10000000 != applyto.getMapId() / 10000000) {
               return false;
            }
         }

         applyto.changeMap(target, target.getPortal(0));
         applyto.getClient().getSession().writeAndFlush(CWvsContext.enableActions(applyto));
         return true;
      } else {
         return false;
      }
   }

   private final void applyBuff(MapleCharacter applyfrom, int newDuration) {
      if (this.isPartyBuff() && (applyfrom.getParty() != null || this.isGmBuff() || applyfrom.inPVP()
            || applyfrom.getMap() instanceof Field_YutaGolden)) {
         if (applyfrom.getMap() instanceof Field_BlackMageBattlePhase4) {
            return;
         }

         Rectangle bounds = this.calculateBoundingBox(applyfrom.getTruePosition(), applyfrom.isFacingLeft());
         List<MapleMapObject> affecteds = applyfrom.getMap().getMapObjectsInRect(bounds,
               List.of(MapleMapObjectType.PLAYER));
         int healCount = 0;

         for (MapleMapObject affectedmo : affecteds) {
            MapleCharacter affected = (MapleCharacter) affectedmo;
            if (GameConstants.isSameJobApplicationSkill(this.getSourceId())) {
               if (GameConstants.isApplicationJob(this.getSourceId(), affected.getJob())
                     && affected.checkAffectedLimit(this.getSourceId())) {
                  this.applyTo(applyfrom, affected, false, null, newDuration, (byte) 0, false, false);
                  PostSkillEffect e_ = new PostSkillEffect(affected.getId(), this.sourceid, this.level, null);
                  affected.send(e_.encodeForLocal());
                  affected.getMap().broadcastMessage(affected, e_.encodeForRemote(), false);
               }
            } else if (affected.getId() != applyfrom.getId()
                  && (this.isGmBuff()
                        || applyfrom.inPVP()
                              && affected.getTeam() == applyfrom.getTeam()
                              && Integer.parseInt(applyfrom.getEventInstance().getProperty("type")) != 0
                        || applyfrom.getParty() != null && affected.getParty() != null
                              && applyfrom.getParty().getId() == affected.getParty().getId()
                        || applyfrom.getMap() instanceof Field_YutaGolden)
                  && ((this.isResurrection() || this.isGuardianSpirit()) && !affected.isAlive()
                        || !this.isGuardianSpirit() && !this.isResurrection() && affected.isAlive())
                  && affected.checkAffectedLimit(this.getSourceId())) {
               if (!this.isResurrection() && !this.isGuardianSpirit()) {
                  this.applyTo(applyfrom, affected, false, null, newDuration, (byte) 0, false, false);
               }

               PostSkillEffect e_ = new PostSkillEffect(affected.getId(), this.sourceid, this.level, null);
               affected.send(e_.encodeForLocal());
               affected.getMap().broadcastMessage(affected, e_.encodeForRemote(), false);
               if (this.isResurrection() || this.isGuardianSpirit()) {
                  if (affected.getMap().getFieldSetInstance() == null) {
                     if (affected.getEventInstance() != null) {
                        if (affected.getDeathCount() > 0) {
                           this.applyTo(applyfrom, affected, false, null, newDuration, (byte) 0, false, false);
                           affected.setPlayerDead(false);
                           affected.temporaryStatSet(this.getSourceId(), this.getDuration(),
                                 SecondaryStatFlag.indiePartialNotDamaged, 1);
                           int plusDamR = Math.min(this.getZ(), 5 + applyfrom.getStat().getTotalInt() / this.getY());
                           affected.temporaryStatSet(this.getSourceId(), this.getSubTime(), SecondaryStatFlag.indieDamR,
                                 plusDamR);
                           applyfrom.temporaryStatSet(this.getSourceId(), this.getSubTime(),
                                 SecondaryStatFlag.indieDamR, plusDamR);
                        }
                     } else {
                        this.applyTo(applyfrom, affected, false, null, newDuration, (byte) 0, false, false);
                        affected.setPlayerDead(false);
                        affected.temporaryStatSet(this.getSourceId(), this.getDuration(),
                              SecondaryStatFlag.indiePartialNotDamaged, 1);
                     }
                  } else if (affected.getDeathCount() > 0) {
                     this.applyTo(applyfrom, affected, false, null, newDuration, (byte) 0, false, false);
                     affected.setPlayerDead(false);
                     affected.temporaryStatSet(this.getSourceId(), this.getDuration(),
                           SecondaryStatFlag.indiePartialNotDamaged, 1);
                     int plusDamR = Math.min(this.getZ(), 5 + applyfrom.getStat().getTotalInt() / this.getY());
                     affected.temporaryStatSet(this.getSourceId(), this.getSubTime(), SecondaryStatFlag.indieDamR,
                           plusDamR);
                     applyfrom.temporaryStatSet(this.getSourceId(), this.getSubTime(), SecondaryStatFlag.indieDamR,
                           plusDamR);
                  }

                  if (this.isGuardianSpirit()) {
                     return;
                  }
               }

               if (this.getSourceId() == 2301002) {
                  healCount++;
                  if (affected.getBuffedValue(SecondaryStatFlag.GiveMeHeal) != null) {
                     affected.temporaryStatReset(SecondaryStatFlag.GiveMeHeal);
                  }
               }
            }
         }

         if (this.getSourceId() == 2301002 && healCount > 0) {
            applyfrom.changeCooldown(2301002, -this.getY() * 1000);
         }
      }
   }

   private final void removeMonsterBuff(MapleCharacter applyfrom) {
      List<MobTemporaryStatFlag> cancel = new ArrayList<>();
      switch (this.sourceid) {
         case 1121016:
         case 1221014:
         case 1321014:
         case 2311001:
         case 11111008:
         case 51111005:
            cancel.add(MobTemporaryStatFlag.P_GUARD_UP);
            cancel.add(MobTemporaryStatFlag.M_GUARD_UP);
            cancel.add(MobTemporaryStatFlag.POWER_UP);
            cancel.add(MobTemporaryStatFlag.MAGIC_UP);
            cancel.add(MobTemporaryStatFlag.HARD_SKIN);
            Rectangle bounds = this.calculateBoundingBox(applyfrom.getTruePosition(), applyfrom.isFacingLeft());
            var affected = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.MONSTER));
            int i = 0;

            for (MapleMapObject mo : affected) {
               if (this.makeChanceResult()) {
                  for (MobTemporaryStatFlag stat : cancel) {
                     ((MapleMonster) mo).cancelStatus(stat);
                  }
               }

               if (++i >= this.mobCount) {
                  break;
               }
            }

            return;
      }
   }

   public final void applyMonsterBuff(MapleCharacter applyfrom) {
      Rectangle bounds = this.calculateBoundingBox(applyfrom.getTruePosition(), applyfrom.isFacingLeft());
      boolean pvp = applyfrom.inPVP();
      MapleMapObjectType type = pvp ? MapleMapObjectType.PLAYER : MapleMapObjectType.MONSTER;
      List<MapleMapObject> affected = this.sourceid == 35111005
            ? applyfrom.getMap().getMapObjectsInRange(applyfrom.getTruePosition(), Double.POSITIVE_INFINITY,
                  Arrays.asList(type))
            : applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(type));
      int i = 0;

      for (MapleMapObject mo : affected) {
         if (this.makeChanceResult()) {
            for (Entry<MobTemporaryStatFlag, Integer> stat : this.getMonsterStati().entrySet()) {
               if (pvp) {
                  MapleCharacter chr = (MapleCharacter) mo;
                  SecondaryStatFlag d = MobTemporaryStatFlag.getLinkedDisease(stat.getKey());
                  if (d != null) {
                     chr.giveDebuff(d, stat.getValue(), 0, this.getDuration(), d.getDisease(), 1);
                  }
               } else {
                  MapleMonster mons = (MapleMonster) mo;
                  if (this.sourceid == 35111005 && mons.getStats().isBoss()) {
                     break;
                  }

                  mons.applyStatus(
                        applyfrom,
                        new MobTemporaryStatEffect(stat.getKey(), stat.getValue(), this.sourceid, null, false),
                        this.isPoison(),
                        this.isSubTime(this.sourceid) ? this.getSubTime() : this.getDuration(),
                        true,
                        this);
               }
            }

            if (pvp && this.skill) {
               MapleCharacter chr = (MapleCharacter) mo;
               this.handleExtraPVP(applyfrom, chr);
            }
         }

         if (++i >= this.mobCount && this.sourceid != 35111005) {
            break;
         }
      }
   }

   public final boolean isSubTime(int source) {
      switch (source) {
         case 23111009:
         case 23111010:
         case 23111011:
         case 31101003:
         case 31121003:
         case 31121005:
            return true;
         default:
            return false;
      }
   }

   public final void handleExtraPVP(MapleCharacter applyfrom, MapleCharacter chr) {
   }

   public final Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft) {
      return calculateBoundingBox(this.sourceid, posFrom, facingLeft, this.lt, this.rb, this.lt2, this.rb2, this.range);
   }

   public final Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft, int addedRange) {
      return calculateBoundingBox(this.sourceid, posFrom, facingLeft, this.lt, this.rb, this.lt2, this.rb2,
            this.range + addedRange);
   }

   public static final Rectangle calculateBoundingBox(int skillID, Point posFrom, boolean facingLeft, Point lt,
         Point rb, Point lt2, Point rb2, int range) {
      if (lt != null && rb != null) {
         if (lt2 != null && rb2 != null) {
            return new Rectangle(posFrom.x + lt2.x, posFrom.y + lt2.y, -lt2.x + rb2.x, -lt2.y + rb2.y);
         } else {
            Point mylt;
            Point myrb;
            if (facingLeft) {
               mylt = new Point(lt.x + posFrom.x - range, lt.y + posFrom.y);
               myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
            } else {
               myrb = new Point(lt.x * -1 + posFrom.x + range, rb.y + posFrom.y);
               mylt = new Point(rb.x * -1 + posFrom.x, lt.y + posFrom.y);
            }

            return new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
         }
      } else {
         return skillID == 400021030
               ? new Rectangle(posFrom.x - 150, posFrom.y - 700, 330, 810)
               : new Rectangle((facingLeft ? -200 - range : 0) + posFrom.x, -100 - range + posFrom.y, 200 + range,
                     100 + range);
      }
   }

   public final double getMaxDistanceSq() {
      int maxX = Math.max(Math.abs(this.lt == null ? 0 : this.lt.x), Math.abs(this.rb == null ? 0 : this.rb.x));
      int maxY = Math.max(Math.abs(this.lt == null ? 0 : this.lt.y), Math.abs(this.rb == null ? 0 : this.rb.y));
      return maxX * maxX + maxY * maxY;
   }

   public final void setDuration(int d) {
      this.duration = d;
   }

   public final void applyComboBuff(MapleCharacter applyto, short combo) {
      applyto.temporaryStatSet(this.sourceid, Integer.MAX_VALUE, SecondaryStatFlag.ComboAbilityBuff, combo);
   }

   public final void applyBuffEffect(MapleCharacter applyfrom, MapleCharacter applyto, boolean primary, int newDuration,
         boolean attack, int rltype) {
      int localDuration = newDuration;
      boolean isSelf = applyfrom.getId() == applyto.getId();
      Map<SecondaryStatFlag, Integer> localstatups = new HashMap<>(this.statups);
      Map<SecondaryStatFlag, Integer> maskedStatups = null;
      boolean normal = true;
      boolean showEffect = primary;
      int maskedDuration = 0;
      if (localstatups.get(SecondaryStatFlag.LimitMP) != null) {
         applyto.addMP(-applyto.getStat().getCurrentMaxMp(applyto));
         applyto.addMP(localstatups.get(SecondaryStatFlag.LimitMP).intValue());
      }

      if (localstatups.get(SecondaryStatFlag.MHPCutR) != null) {
         int percent = localstatups.get(SecondaryStatFlag.MHPCutR);
         long playermaxHP = applyto.getStat().getCurrentMaxHp(applyto);
         long playerDecHP = (long) (playermaxHP * (percent / 100.0));
         applyto.addHP(-applyto.getStat().getHp() + (playermaxHP - playerDecHP));
      }

      if (localstatups.get(SecondaryStatFlag.MMPCutR) != null) {
         int percent = localstatups.get(SecondaryStatFlag.MMPCutR);
         long playermaxMP = applyto.getStat().getCurrentMaxMp(applyto);
         long playerDecMP = (long) (playermaxMP * (percent / 100.0));
         applyto.addMP(-applyto.getStat().getMp() + (playermaxMP - playerDecMP));
      }

      label1523: switch (this.sourceid) {
         case 1111003:
            localstatups.put(SecondaryStatFlag.ScarringSword, this.x);
            break;
         case 1121054:
            localstatups.put(SecondaryStatFlag.TerR, this.x);
            localstatups.put(SecondaryStatFlag.AsrR, this.x);
            localstatups.put(SecondaryStatFlag.Stance, this.x);
            localstatups.put(SecondaryStatFlag.indieCR, Integer.valueOf(this.indieCr));
            localstatups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(this.indiePad));
            applyto.invokeJobMethod("handleOrbgain", 1121054);
            break;
         case 1211014:
            if (isSelf && applyfrom.getBuffedValue(SecondaryStatFlag.KnightsAura) != null) {
               applyfrom.temporaryStatReset(SecondaryStatFlag.KnightsAura);
               return;
            }

            localstatups.clear();
            if (isSelf) {
               localstatups.put(SecondaryStatFlag.KnightsAura, this.y);
               localstatups.put(SecondaryStatFlag.indiePAD, Integer.valueOf(this.indiePad));
            } else {
               localstatups.put(SecondaryStatFlag.KnightsAura, this.x);
            }

            localDuration = Integer.MAX_VALUE;
            break;
         case 1221016:
            this.tryGuardianSpirit(applyfrom);
            break;
         case 1311015:
            if (!applyto.hasBuffBySkillID(80001841)) {
               localstatups.put(SecondaryStatFlag.CrossOverChain,
                     Math.min(this.x, (int) (applyto.getStat().getHPPercent() / 2.5)));
            }
            break;
         case 2003599:
            localstatups.put(SecondaryStatFlag.indieStatR, 10);
            break;
         case 2111011:
            this.setV(0);
            break;
         case 2120010:
         case 2220010:
         case 2320011:
            localDuration = 5000;
            localstatups.put(SecondaryStatFlag.ArcaneAim, applyfrom.getArcaneAim());
            break;
         case 2121004:
         case 2221004:
         case 2321004:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.Infinity, 1);
            break;
         case 2211012:
            localstatups.put(SecondaryStatFlag.AntiMagicShell, 1);
            localDuration = 2100000000;
            break;
         case 2301004:
         case 9001003:
         case 9101003:
            if (applyto.getBuffedValue(SecondaryStatFlag.AdvancedBless) != null) {
               return;
            }
            break;
         case 2311001:
            if (Randomizer.nextInt(100) < this.prop) {
               this.removeMonsterBuff(applyfrom);
               Consumer<MapleCharacter> consumer = MapleCharacter::dispelDebuffs;
               consumer.accept(applyfrom);
               int count = this.runToPMembersInArea(applyfrom, consumer, true);
               if (count > 0) {
                  if (applyfrom.getRemainCooltime(2311012) > 0L && !applyfrom.hasBuffBySkillID(2311012)) {
                     applyfrom.changeCooldown(2311012, -(this.getTime() * count) * 1000);
                  }

                  if (applyfrom.getRemainCooltime(2311001) > 0L) {
                     applyfrom.changeCooldown(2311001, -(this.getY() * count) * 1000);
                  }
               }
            }
            break;
         case 2311003: {
            int x = this.getX();
            if (isSelf) {
               x += applyfrom.getSkillLevelDataOne(2320046, SecondaryStatEffect::getY);
            }

            applyto.setJobField("holySymbolUserID", applyfrom.getId());
            applyto.setJobField("holySymbolLv", this.level);
            applyto.setJobField("holySymbolActive", true);
            applyto.setJobField("holySymbolDecrease", false);
            applyto.setJobField("holySymbolDropR", applyfrom.getSkillLevelDataOne(2320048, SecondaryStatEffect::getV));
            localstatups.put(SecondaryStatFlag.HolySymbol, x);
            if (applyfrom.getSkillLevel(2320047) > 0) {
               localstatups.put(SecondaryStatFlag.indieAsrR,
                     applyfrom.getSkillLevelDataOne(2320047, SecondaryStatEffect::getASRRate));
               localstatups.put(SecondaryStatFlag.indieTerR,
                     applyfrom.getSkillLevelDataOne(2320047, SecondaryStatEffect::getTERRate));
            }
         }
            break;
         case 2311009: {
            localstatups.clear();
            int xx = this.getX();
            xx += applyfrom.getSkillLevelDataOne(2320043, effx -> effx.getX());
            localstatups.put(SecondaryStatFlag.HolyMagicShell, xx);
            int z = this.getZ();
            if (applyto.getBuffedValue(SecondaryStatFlag.HolyMagicShellBlocked) != null) {
               return;
            }

            Party party = applyto.getParty();
            if (party == null) {
               int hpx = (int) (applyto.getStat().getMaxHp() * 0.01 * z);
               applyto.addHP(hpx);
            } else {
               for (PartyMemberEntry mpc : party.getPartyMemberList()) {
                  if (mpc.isOnline() && mpc.getFieldID() == applyfrom.getMapId()
                        && mpc.getChannel() == applyfrom.getClient().getChannel()) {
                     MapleCharacter p = applyfrom.getMap().getCharacterById(mpc.getId());
                     if (p != null && p.getBuffedValue(SecondaryStatFlag.HolyMagicShellBlocked) == null) {
                        int hpx = (int) (p.getStat().getMaxHp() * 0.01 * z);
                        p.addHP(hpx);
                     }
                  }
               }
            }

            int w = this.getW();
            w += applyfrom.getSkillLevelDataOne(2320045, SecondaryStatEffect::getW);
            applyto.setHolyMagicShellW(w);
            applyto.temporaryStatSet(2310013, this.y * 1000, SecondaryStatFlag.HolyMagicShellBlocked, 1);
            applyto.addCooldown(2310013, System.currentTimeMillis(), this.y * 1000L);
         }
            break;
         case 2311015:
            localstatups.put(SecondaryStatFlag.TriumphFeather, 1);
            break;
         case 2321005:
            localstatups.clear();
            if (applyto.getBuffedValue(SecondaryStatFlag.Bless) != null) {
               applyto.temporaryStatReset(SecondaryStatFlag.Bless);
            }

            int mhp = this.getIndieMHp() + applyfrom.getSkillLevelDataOne(2320051, SecondaryStatEffect::getIndieMHp);
            int mmp = this.getIndieMMp() + applyfrom.getSkillLevelDataOne(2320051, SecondaryStatEffect::getIndieMMp);
            localstatups.put(SecondaryStatFlag.AdvancedBless, Integer.valueOf(this.mpConReduce));
            localstatups.put(SecondaryStatFlag.indieMHP, mhp);
            localstatups.put(SecondaryStatFlag.indieMMP, mmp);
            break;
         case 2321016:
            localstatups.put(SecondaryStatFlag.indiePMDR,
                  Math.min(this.s2, this.u + applyfrom.getStat().getTotalInt() / this.s * this.u2));
            localstatups.put(SecondaryStatFlag.indieIncreaseHitDamage, this.v);
            localstatups.put(SecondaryStatFlag.HolyBlood, 1);
            break;
         case 2321055:
            localstatups.clear();
            localDuration = Integer.MAX_VALUE;
            if (applyto.getBuffedEffect(SecondaryStatFlag.HeavensDoorBlocked) == null) {
               applyto.temporaryStatSet(SecondaryStatFlag.HeavensDoorBlocked, 2321055, 600000, 1, 1);
               applyto.addAffectedLimit(2321055, System.currentTimeMillis() + 600000L);
               localstatups.put(SecondaryStatFlag.HeavensDoor, 1);
            }
            break;
         case 3010001:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.indieFlyAcc, applyfrom.getCurseWeakeningStack());
            localstatups.put(SecondaryStatFlag.indieCR, this.y * applyfrom.getCurseWeakeningStack());
            break;
         case 3101009:
            int value = 1;
            if (applyto.getBuffedValue(SecondaryStatFlag.QuiverCatridge) != null
                  && applyto.getSkillLevel(3120022) > 0
                  && applyto.getBuffedValue(SecondaryStatFlag.QuiverCatridge) == 1) {
               value = 2;
            }

            Map<SecondaryStatFlag, Integer> flag_ = new HashMap<>();
            flag_.put(SecondaryStatFlag.QuiverCatridge, value);
            applyto.temporaryStatSet(
                  this.sourceid,
                  applyto.getSkillLevel(this.sourceid),
                  Integer.MAX_VALUE,
                  flag_,
                  false,
                  applyto.getId(),
                  applyto.getBuffedValue(SecondaryStatFlag.QuiverCatridge) == null);
            break;
         case 3111005:
            if (applyfrom.getTotalSkillLevel(3120006) > 0) {
               SkillFactory.getSkill(3120006)
                     .getEffect(applyfrom.getTotalSkillLevel(3120006))
                     .applyBuffEffect(applyfrom, applyto, primary, newDuration, false, rltype);
            }
            break;
         case 3121002:
         case 3221002:
         case 3321022: {
            int x_ = this.getY();
            if (applyto.getTotalSkillLevel(3220045) > 0) {
               SecondaryStatEffect eff = SkillFactory.getSkill(3220045).getEffect(3220045);
               if (eff != null) {
                  x_ += eff.getX();
               }
            }

            if (applyto.getTotalSkillLevel(3120045) > 0) {
               SecondaryStatEffect eff = SkillFactory.getSkill(3120045).getEffect(3120045);
               if (eff != null) {
                  x_ += eff.getX();
               }
            }

            if (applyto.getTotalSkillLevel(3320027) > 0) {
               SecondaryStatEffect eff = SkillFactory.getSkill(3320027).getEffect(3320027);
               if (eff != null) {
                  x_ += eff.getX();
               }
            }

            localstatups.put(SecondaryStatFlag.SharpEyes, (x_ << 8) + this.getY());
         }
            break;
         case 3211005:
            if (applyfrom.getTotalSkillLevel(3220005) > 0) {
               SkillFactory.getSkill(3220005)
                     .getEffect(applyfrom.getTotalSkillLevel(3220005))
                     .applyBuffEffect(applyfrom, applyto, primary, newDuration, false, rltype);
            }
            break;
         case 3300000:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.RelicCharge, applyfrom.getRelicCharge());
            if (applyfrom.getJob() >= 331 && applyfrom.getJob() <= 332) {
               localstatups.put(SecondaryStatFlag.AncientGuardians, applyfrom.getAncientGuidance());
            }

            localDuration = Integer.MAX_VALUE;
            break;
         case 3300001:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.indieFlyAcc, applyfrom.getCurseWeakeningStack());
            localstatups.put(SecondaryStatFlag.indiePddR, this.y * applyfrom.getCurseWeakeningStack());
            break;
         case 3310000:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.indieFlyAcc, applyfrom.getCurseWeakeningStack());
            localstatups.put(SecondaryStatFlag.indieAsrR, this.y * applyfrom.getCurseWeakeningStack());
            break;
         case 3311012:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.indieAsrR, this.s + applyfrom.getCurseToleranceStack() * this.x);
            break;
         case 4101015:
            localstatups.clear();
            if (applyto.getBuffedEffect(SecondaryStatFlag.DarkSight) == null) {
               localstatups.put(SecondaryStatFlag.DarkSight, 1);
            }

            localDuration = this.subTime / 1000;
            break;
         case 4111002:
         case 4211008:
         case 4331002:
         case 14111054:
         case 36111006:
            if (!applyto.isHidden()) {
               EnumMap<SecondaryStatFlag, Integer> statx = new EnumMap<>(SecondaryStatFlag.class);
               statx.put(SecondaryStatFlag.ShadowPartner, this.x);
            }
            break;
         case 4111009:
         case 5201008:
         case 14111025:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.NoBulletConsume, applyfrom.getSpiritJabelinConsumeID() % 100 + 1);
            break;
         case 4341002:
            localstatups = new EnumMap<>(SecondaryStatFlag.class);
            localstatups.put(SecondaryStatFlag.FinalCut, this.y);
            applyto.temporaryStatSet(this.sourceid, this.getLevel(), this.getDuration(), localstatups);
            normal = false;
            break;
         case 5111017:
            applyto.temporaryStatReset(SecondaryStatFlag.SerpentStone);
            localstatups.put(SecondaryStatFlag.indieSummon, 1);
            break;
         case 5121010:
            localstatups.put(SecondaryStatFlag.ViperTimeLeap, 1);
            break;
         case 5210016:
         case 5210017:
         case 5210018:
            if (applyfrom.getTotalSkillLevel(5220019) > 0) {
               localstatups = new EnumMap<>(SecondaryStatFlag.class);
               if (!localstatups.isEmpty()) {
                  applyto.temporaryStatSet(5220019, this.getLevel(), 120000, localstatups);
                  normal = false;
               }
            }
            break;
         case 5221054: {
            int hp = (int) (applyto.getStat().getCurrentMaxHp(applyto) * 0.01 * this.z);
            applyto.healHP(hp);
            break;
         }
         case 5310008:
            localstatups.put(SecondaryStatFlag.KeydownTimeIgnore, 1);
            break;
         case 11101022: {
            SecondaryStatEffect bonusEffect = applyto.getSkillLevelData(11120009);
            localstatups.put(SecondaryStatFlag.indieCR,
                  Integer.valueOf(bonusEffect != null ? bonusEffect.indieCr : this.indieCr));
            localstatups.put(SecondaryStatFlag.BuckShot, 20);
            localstatups.put(SecondaryStatFlag.PoseType, 1);
            localDuration = Integer.MAX_VALUE;
            break;
         }
         case 11111022: {
            SecondaryStatEffect bonusEffect = applyto.getSkillLevelData(11120009);
            localstatups.put(SecondaryStatFlag.PoseType, 2);
            localstatups.put(SecondaryStatFlag.indieBooster, bonusEffect != null ? bonusEffect.w : this.indieBooster);
            localstatups.put(SecondaryStatFlag.indiePMDR, bonusEffect != null ? bonusEffect.v : this.indiePMdR);
            localstatups.put(SecondaryStatFlag.indiePAD, bonusEffect != null ? bonusEffect.s : 0);
            localDuration = Integer.MAX_VALUE;
            break;
         }
         case 13101022:
            int skillID = this.sourceid;
            if (applyfrom.getSkillLevel(13110022) > 0) {
               skillID = 13110022;
            }

            if (applyfrom.getSkillLevel(13120003) > 0) {
               skillID = 13120003;
            }

            applyfrom.temporaryStatSet(skillID, Integer.MAX_VALUE, SecondaryStatFlag.TriflingWhimOnOff, 1);
            return;
         case 14001023: {
            if (applyto.isHidden()) {
               return;
            }

            EnumMap<SecondaryStatFlag, Integer> stat = new EnumMap<>(SecondaryStatFlag.class);
            stat.put(SecondaryStatFlag.DarkSight, 0);
            break;
         }
         case 15120003:
            localstatups.clear();
            break;
         case 21111030:
            SecondaryStatEffect adb = SkillFactory.getSkill(21110016).getEffect(applyto.getTotalSkillLevel(21110016));
            adb.applyTo(applyto);
            applyto.temporaryStatReset(SecondaryStatFlag.AdrenalinBoostActivate);
            applyto.setCombo((short) 500);
            applyto.send(CField.aranCombo(500));
         case 21110016:
            if (this.sourceid != 21111030) {
               applyto.setCombo((short) 500);
               applyto.send(CField.aranCombo(500));
            }

            int stack = 10;
            applyto.setComboBuffStack(stack);
            applyto.temporaryStatSet(21000000, Integer.MAX_VALUE, SecondaryStatFlag.ComboAbilityBuff,
                  applyto.getComboBuffStack() * 50);
            applyto.setAdrenalinBoostCount(1);
            break;
         case 21121058:
            applyto.setAdrenalinBoostCount(1);
            applyto.setComboBuffStack(10);
            applyto.temporaryStatSet(21000000, Integer.MAX_VALUE, SecondaryStatFlag.ComboAbilityBuff,
                  applyto.getComboBuffStack() * 50);
            applyto.temporaryStatSet(21110016, 20000, SecondaryStatFlag.AdrenalinBoost, 150);
            if (this.sourceid != 21111030) {
               applyto.setCombo((short) 500);
               applyto.send(CField.aranCombo(500));
            }

            applyto.setAdrenalinBoostCount(1);
            return;
         case 21121068:
            localstatups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
            localDuration = 2000;
            break;
         case 23101003: {
            EnumMap<SecondaryStatFlag, Integer> stat = new EnumMap<>(SecondaryStatFlag.class);
            stat.put(SecondaryStatFlag.CriticalBuff, this.x);
            break;
         }
         case 24111003:
            int terR = this.getX();
            int asrR = this.getY();
            int mhpR = this.getIndieMhpR();
            int mmpR = this.getIndieMmpR();
            int slvx = 0;
            if ((slvx = applyto.getTotalSkillLevel(24120049)) > 0) {
               SecondaryStatEffect eff = SkillFactory.getSkill(24120049).getEffect(slvx);
               if (eff != null) {
                  terR += eff.getX();
                  asrR += eff.getX();
               }
            }

            if ((slvx = applyto.getTotalSkillLevel(24120050)) > 0) {
               SecondaryStatEffect eff = SkillFactory.getSkill(24120050).getEffect(slvx);
               if (eff != null) {
                  mhpR += eff.getX();
               }
            }

            if ((slvx = applyto.getTotalSkillLevel(24120051)) > 0) {
               SecondaryStatEffect eff = SkillFactory.getSkill(24120051).getEffect(slvx);
               if (eff != null) {
                  mmpR += eff.getX();
               }
            }

            localstatups.put(SecondaryStatFlag.TerR, terR);
            localstatups.put(SecondaryStatFlag.AsrR, asrR);
            localstatups.put(SecondaryStatFlag.indieMHPR, mhpR);
            localstatups.put(SecondaryStatFlag.indieMMPR, mmpR);
            break;
         case 24121007:
            localstatups.clear();
            break;
         case 25121209:
            if (applyto.getSpiritWardCount() == 0) {
               applyto.setSpiritWardCount(this.getX());
            }
            break;
         case 27101202:
            showEffect = false;
            break;
         case 31011001: {
            int exceedMax = 20;
            if (applyfrom.getTotalSkillLevel(31220044) > 0) {
               SecondaryStatEffect e = SkillFactory.getSkill(31220044)
                     .getEffect(applyfrom.getTotalSkillLevel(31220044));
               if (e != null) {
                  exceedMax -= e.getX();
               }
            }

            int indiePMdR_ = this.indiePMdR;
            if (applyfrom.getTotalSkillLevel(31210006) > 0) {
               SecondaryStatEffect e = SkillFactory.getSkill(31210006)
                     .getEffect(applyfrom.getTotalSkillLevel(31210006));
               if (e != null) {
                  indiePMdR_ = Math.max(indiePMdR_, e.getY());
               }
            }

            double em = 1.0 / exceedMax;
            double x = em * applyto.getSecondaryStat().OverloadCountValue;
            applyto.temporaryStatReset(SecondaryStatFlag.OverloadCount);
            applyto.temporaryStatReset(SecondaryStatFlag.Exceed);
            applyto.temporaryStatSet(31011001, this.duration, SecondaryStatFlag.indiePMDR, (int) x * indiePMdR_);
            int x_ = this.getX();
            if (applyto.getBuffedValue(SecondaryStatFlag.DemonFrenzy) != null) {
               x_ = this.getY();
            }

            long hp = (long) (applyto.getStat().getCurrentMaxHp(applyto) * 0.01 * x_);
            applyto.addHP(hp);
            break;
         }
         case 31121005:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(this.indieDamR));
            localstatups.put(SecondaryStatFlag.DevilishPower, Integer.valueOf(this.mobCount));
            if (applyfrom.getSkillLevel(31120046) > 0) {
               localstatups.put(SecondaryStatFlag.IgnoreAllCounter, 1);
               localstatups.put(SecondaryStatFlag.indieBlockSkill, 1);
            }
            break;
         case 32001016:
            if (isSelf && applyfrom.getBuffedValue(SecondaryStatFlag.YellowAura) != null) {
               applyfrom.temporaryStatReset(SecondaryStatFlag.YellowAura);
               return;
            }

            this.cancelAnotherAuraSkill(applyto);
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.YellowAura, Integer.valueOf(this.getLevel()));
            localstatups.put(SecondaryStatFlag.indieSpeed, this.getIndieSpeed());
            localstatups.put(SecondaryStatFlag.indieBooster, this.getIndieBooster());
            localDuration = Integer.MAX_VALUE;
            break;
         case 32101009:
            if (isSelf && applyfrom.getBuffedValue(SecondaryStatFlag.DrainAura) != null) {
               applyfrom.temporaryStatReset(SecondaryStatFlag.DrainAura);
               return;
            }

            this.cancelAnotherAuraSkill(applyto);
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.DrainAura, Integer.valueOf(this.getLevel()));
            localstatups.put(SecondaryStatFlag.ComboDrain, this.getX());
            localDuration = Integer.MAX_VALUE;
            break;
         case 32110008:
            localDuration = 10000;
         case 32101003:
            if (applyfrom.getTotalSkillLevel(32120014) > 0) {
               SkillFactory.getSkill(32120014)
                     .getEffect(applyfrom.getTotalSkillLevel(32120014))
                     .applyBuffEffect(applyfrom, applyto, primary, newDuration, false, rltype);
               return;
            }
         case 1101013:
            if (!applyto.isHidden()) {
               SecondaryStat ss = applyto.getSecondaryStat();
               localstatups.put(SecondaryStatFlag.Combo, 1);
               localDuration = Integer.MAX_VALUE;
               ss.ComboMaxValue = Math.max(this.x, applyto.getSkillLevelDataOne(1120003, SecondaryStatEffect::getX));
               ss.ComboRate = Math.max(this.prop, applyto.getSkillLevelDataOne(1110013, SecondaryStatEffect::getProb));
            }
            break;
         case 32111012:
            if (isSelf && applyfrom.getBuffedValue(SecondaryStatFlag.BlueAura) != null) {
               applyfrom.temporaryStatReset(SecondaryStatFlag.BlueAura);
               return;
            }

            this.cancelAnotherAuraSkill(applyto);
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.BlueAura, Integer.valueOf(this.getLevel()));
            localstatups.put(SecondaryStatFlag.indieAsrR, this.getIndieAsrR());
            applyto.getSecondaryStat().BlueAuraDispelCount = applyfrom.getSkillLevel(32120062);
            localDuration = Integer.MAX_VALUE;
            break;
         case 32121017:
            if (isSelf && applyfrom.getBuffedValue(SecondaryStatFlag.DarkAura) != null) {
               applyfrom.temporaryStatReset(SecondaryStatFlag.DarkAura);
               return;
            }

            this.cancelAnotherAuraSkill(applyto);
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.DarkAura, Integer.valueOf(this.getLevel()));
            localstatups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(this.getIndieDamR()));
            localDuration = Integer.MAX_VALUE;
            break;
         case 32121018:
            if (isSelf && applyfrom.getBuffedValue(SecondaryStatFlag.DebuffAura) != null) {
               applyfrom.temporaryStatReset(SecondaryStatFlag.DebuffAura);
               return;
            }

            this.cancelAnotherAuraSkill(applyto);
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.DebuffAura, 1);
            localDuration = Integer.MAX_VALUE;
            break;
         case 33101005:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.HowlingAttackDamage, this.getX());
            break;
         case 33101006:
            applyto.clearLinkMid();
            SecondaryStatFlag theBuff = null;
            int theStat = this.y;
            switch (Randomizer.nextInt(6)) {
               case 4:
                  theBuff = SecondaryStatFlag.indieDamR;
                  break;
               case 5:
                  theBuff = SecondaryStatFlag.BuckShot;
            }

            localstatups = new EnumMap<>(SecondaryStatFlag.class);
            localstatups.put(theBuff, theStat);
            applyto.temporaryStatSet(this.sourceid, this.getLevel(), this.getDuration(), localstatups);
            normal = false;
            break;
         case 36111004:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.XenonAegisSystem, this.x);
            localDuration = Integer.MAX_VALUE;
            break;
         case 37121005:
            int shield = 0;
            Integer value3 = applyto.getBuffedValue(SecondaryStatFlag.RWBarrier);
            if (value3 != null) {
               shield = value3;
            }

            applyto.temporaryStatReset(SecondaryStatFlag.RWBarrier);
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.RWBarrierHeal, 1);
            applyto.addHP(shield + applyto.getStat().getMaxHp() * this.getX() / 100L);
            break;
         case 50001214:
            localstatups.put(SecondaryStatFlag.GuardianOfLight, this.y);
            localstatups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(this.indieDamR));
            break;
         case 51001005:
         case 51001006:
         case 51001007:
         case 51001008:
         case 51001009:
         case 51001010:
            int time = 1000 + this.getX();
            if (applyto.hasBuffBySkillID(51121054)) {
               SecondaryStatEffect e = applyto.getBuffedEffect(SecondaryStatFlag.DamAbsorbShield);
               if (e != null) {
                  time = (int) (time + e.getT() * 1000.0);
               }
            }

            localDuration = time;
            localstatups.put(SecondaryStatFlag.RoyalGuardPrepare, 1);
            break;
         case 51111004: {
            localstatups.clear();
            int ddr = this.getX();
            int slv = 0;
            if ((slv = applyto.getTotalSkillLevel(51120044)) > 0) {
               SecondaryStatEffect eff = SkillFactory.getSkill(51120044).getEffect(slv);
               if (eff != null) {
                  ddr += eff.getX();
               }
            }

            int z = this.getZ();
            int y = this.getY();
            int var110 = 0;
            if ((var110 = applyto.getTotalSkillLevel(51120045)) > 0) {
               SecondaryStatEffect eff = SkillFactory.getSkill(51120045).getEffect(var110);
               if (eff != null) {
                  z += eff.getZ();
                  y += eff.getY();
               }
            }

            localstatups.put(SecondaryStatFlag.DDR, ddr);
            localstatups.put(SecondaryStatFlag.TerR, z);
            localstatups.put(SecondaryStatFlag.AsrR, y);
         }
            break;
         case 60031005:
            localstatups.put(SecondaryStatFlag.MaxLevelBuff, 4);
         case 61101002:
         case 61110211:
         case 61120007:
         case 61121217:
            break;
         case 61111008:
         case 61120008:
         case 61121053:
            if (applyto.getJob() == 6112 && this.sourceid == 61111008) {
               if (applyto.getSkillLevel(61120007) < 0) {
                  applyto.changeSkillLevel(61120007, 30, 30);
               }

               SkillFactory.getSkill(61120008).getEffect(applyto.getSkillLevel(61111008)).applyTo(applyto);
               return;
            }

            if (this.sourceid == 61121053) {
               int gauge = Math.min(700, applyto.getSmashStack());
               applyto.setViperEnergyCharge(gauge);
               applyto.temporaryStatSet(61121053, Integer.MAX_VALUE, SecondaryStatFlag.SmashStack, 700);
            }

            if (applyto.getBuffedValue(SecondaryStatFlag.StopForceAtomInfo) != null) {
               applyto.temporaryStatReset(SecondaryStatFlag.StopForceAtomInfo);
               if (this.sourceid != 61120008 && this.sourceid != 61121053) {
                  SkillFactory.getSkill(61110211)
                        .getEffect(applyto.getSkillLevel(GameConstants.getLinkedAranSkill(61101002))).applyTo(applyto);
               } else {
                  SkillFactory.getSkill(61121217)
                        .getEffect(applyto.getSkillLevel(GameConstants.getLinkedAranSkill(61120007))).applyTo(applyto);
               }
            }
            break;
         case 61121054:
            for (MapleCoolDownValueHolder holder : applyto.getCooldowns()) {
               Skill skill = SkillFactory.getSkill(holder.skillId);
               if (skill != null && GameConstants.isResettableCooltimeSkill(holder.skillId)
                     && holder.skillId != 61121054) {
                  applyto.removeCooldown(holder.skillId);
                  applyto.send(CField.skillCooldown(holder.skillId, 0));
               }
            }
            break;
         case 63101001:
            localstatups.put(SecondaryStatFlag.PossessionState, 1);
            applyto.invokeJobMethod("applyKainPossession", -100);
            break;
         case 63121044:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(this.indieDamR));
            localstatups.put(SecondaryStatFlag.indiePadR, Integer.valueOf(this.indiePadR));
            localstatups.put(SecondaryStatFlag.IncarnationAura, 1);
            break;
         case 80000514:
         case 150010241:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(this.indieDamR));
            localstatups.put(SecondaryStatFlag.Solus, applyfrom.getPassiveStack());
            break;
         case 80001462:
            Item itemx = applyto.getInventory(MapleInventoryType.EQUIPPED).findById(1113105);
            if (itemx != null) {
               Equip equip = (Equip) itemx;
               int seedLevel = equip.getTheSeedRingLevel();
               int xx = applyto.getStat().passive_sharpeye_rate();
               switch (seedLevel) {
                  case 1:
                     xx = (int) (xx * 0.25);
                     break;
                  case 2:
                     xx = (int) (xx * 0.5);
                     break;
                  case 3:
                     xx = (int) (xx * 0.75);
                     break;
                  case 4:
                     xx *= 1;
               }

               localstatups.put(SecondaryStatFlag.indieIgnoreMobPdpR, xx);
            }
            break;
         case 80001463:
            Item itemxxx = applyto.getInventory(MapleInventoryType.EQUIPPED).findById(1113106);
            if (itemxxx != null) {
               if ((applyto.getJob() < 100 || applyto.getJob() >= 200)
                     && applyto.getJob() != 512
                     && applyto.getJob() != 1512
                     && applyto.getJob() != 2512
                     && (applyto.getJob() < 1100 || applyto.getJob() >= 1200)
                     && !GameConstants.isAran(applyto.getJob())
                     && !GameConstants.isBlaster(applyto.getJob())
                     && !GameConstants.isDemonSlayer(applyto.getJob())
                     && !GameConstants.isMichael(applyto.getJob())
                     && !GameConstants.isKaiser(applyto.getJob())
                     && !GameConstants.isZero(applyto.getJob())
                     && !GameConstants.isArk(applyto.getJob())
                     && !GameConstants.isAdele(applyto.getJob())) {
                  if ((applyto.getJob() < 200 || applyto.getJob() >= 300)
                        && !GameConstants.isFlameWizard(applyto.getJob())
                        && !GameConstants.isEvan(applyto.getJob())
                        && !GameConstants.isLuminous(applyto.getJob())
                        && (applyto.getJob() < 3200 || applyto.getJob() >= 3300)
                        && !GameConstants.isKinesis(applyto.getJob())
                        && !GameConstants.isIllium(applyto.getJob())
                        && !GameConstants.isLara(applyto.getJob())) {
                     if ((applyto.getJob() < 300 || applyto.getJob() >= 400)
                           && applyto.getJob() != 522
                           && applyto.getJob() != 532
                           && !GameConstants.isMechanic(applyto.getJob())
                           && !GameConstants.isAngelicBuster(applyto.getJob())
                           && (applyto.getJob() < 1300 || applyto.getJob() >= 1400)
                           && !GameConstants.isMercedes(applyto.getJob())
                           && (applyto.getJob() < 3300 || applyto.getJob() >= 3400)) {
                        if ((applyto.getJob() < 400 || applyto.getJob() >= 500)
                              && (applyto.getJob() < 1400 || applyto.getJob() >= 1500)
                              && !GameConstants.isPhantom(applyto.getJob())
                              && !GameConstants.isKadena(applyto.getJob())
                              && !GameConstants.isHoyoung(applyto.getJob())) {
                           if (GameConstants.isDemonAvenger(applyto.getJob())) {
                              localstatups.put(SecondaryStatFlag.indieCR,
                                    (int) (applyto.getStat().getMaxHp() / 17.5 * (this.x / 100.0)));
                           } else if (GameConstants.isXenon(applyto.getJob())) {
                              int str = applyto.getStat().getStr();
                              int dex = applyto.getStat().getDex();
                              int luk = applyto.getStat().getLuk();
                              if (str > dex && str > luk) {
                                 localstatups.put(SecondaryStatFlag.indieCR,
                                       (int) (applyto.getStat().getStr() * (this.x / 100.0)));
                              } else if (dex > str && dex > luk) {
                                 localstatups.put(SecondaryStatFlag.indieCR,
                                       (int) (applyto.getStat().getDex() * (this.x / 100.0)));
                              } else if (luk > str && luk > dex) {
                                 localstatups.put(SecondaryStatFlag.indieCR,
                                       (int) (applyto.getStat().getLuk() * (this.x / 100.0)));
                              } else {
                                 localstatups.put(SecondaryStatFlag.indieCR,
                                       (int) (applyto.getStat().getStr() * (this.x / 100.0)));
                              }
                           }
                        } else {
                           localstatups.put(SecondaryStatFlag.indieCR,
                                 (int) (applyto.getStat().getLuk() * (this.x / 100.0)));
                        }
                     } else {
                        localstatups.put(SecondaryStatFlag.indieCR,
                              (int) (applyto.getStat().getDex() * (this.x / 100.0)));
                     }
                  } else {
                     localstatups.put(SecondaryStatFlag.indieCR, (int) (applyto.getStat().getInt() * (this.x / 100.0)));
                  }
               } else {
                  localstatups.put(SecondaryStatFlag.indieCR, (int) (applyto.getStat().getStr() * (this.x / 100.0)));
               }
            }
            break;
         case 80001464:
            Item itemxx = applyto.getInventory(MapleInventoryType.EQUIPPED).findById(1113107);
            if (itemxx != null) {
               if ((applyto.getJob() < 100 || applyto.getJob() >= 200)
                     && applyto.getJob() != 512
                     && applyto.getJob() != 1512
                     && applyto.getJob() != 2512
                     && (applyto.getJob() < 1100 || applyto.getJob() >= 1200)
                     && !GameConstants.isAran(applyto.getJob())
                     && !GameConstants.isBlaster(applyto.getJob())
                     && !GameConstants.isDemonSlayer(applyto.getJob())
                     && !GameConstants.isMichael(applyto.getJob())
                     && !GameConstants.isKaiser(applyto.getJob())
                     && !GameConstants.isZero(applyto.getJob())
                     && !GameConstants.isArk(applyto.getJob())
                     && !GameConstants.isAdele(applyto.getJob())) {
                  if ((applyto.getJob() < 200 || applyto.getJob() >= 300)
                        && !GameConstants.isFlameWizard(applyto.getJob())
                        && !GameConstants.isEvan(applyto.getJob())
                        && !GameConstants.isLuminous(applyto.getJob())
                        && (applyto.getJob() < 3200 || applyto.getJob() >= 3300)
                        && !GameConstants.isKinesis(applyto.getJob())
                        && !GameConstants.isIllium(applyto.getJob())
                        && !GameConstants.isLara(applyto.getJob())) {
                     if ((applyto.getJob() < 300 || applyto.getJob() >= 400)
                           && applyto.getJob() != 522
                           && applyto.getJob() != 532
                           && !GameConstants.isMechanic(applyto.getJob())
                           && !GameConstants.isAngelicBuster(applyto.getJob())
                           && (applyto.getJob() < 1300 || applyto.getJob() >= 1400)
                           && !GameConstants.isMercedes(applyto.getJob())
                           && (applyto.getJob() < 3300 || applyto.getJob() >= 3400)) {
                        if ((applyto.getJob() < 400 || applyto.getJob() >= 500)
                              && (applyto.getJob() < 1400 || applyto.getJob() >= 1500)
                              && !GameConstants.isPhantom(applyto.getJob())
                              && !GameConstants.isKadena(applyto.getJob())
                              && !GameConstants.isHoyoung(applyto.getJob())) {
                           if (GameConstants.isDemonAvenger(applyto.getJob())) {
                              localstatups.put(SecondaryStatFlag.indieStance,
                                    (int) (applyto.getStat().getMaxHp() / 17.5 * (this.x / 100.0)));
                           } else if (GameConstants.isXenon(applyto.getJob())) {
                              int str = applyto.getStat().getStr();
                              int dex = applyto.getStat().getDex();
                              int luk = applyto.getStat().getLuk();
                              if (str > dex && str > luk) {
                                 localstatups.put(SecondaryStatFlag.indieStance,
                                       (int) (applyto.getStat().getStr() * (this.x / 100.0)));
                              } else if (dex > str && dex > luk) {
                                 localstatups.put(SecondaryStatFlag.indieStance,
                                       (int) (applyto.getStat().getDex() * (this.x / 100.0)));
                              } else if (luk > str && luk > dex) {
                                 localstatups.put(SecondaryStatFlag.indieStance,
                                       (int) (applyto.getStat().getLuk() * (this.x / 100.0)));
                              } else {
                                 localstatups.put(SecondaryStatFlag.indieStance,
                                       (int) (applyto.getStat().getStr() * (this.x / 100.0)));
                              }
                           }
                        } else {
                           localstatups.put(SecondaryStatFlag.indieStance,
                                 (int) (applyto.getStat().getLuk() * (this.x / 100.0)));
                        }
                     } else {
                        localstatups.put(SecondaryStatFlag.indieStance,
                              (int) (applyto.getStat().getDex() * (this.x / 100.0)));
                     }
                  } else {
                     localstatups.put(SecondaryStatFlag.indieStance,
                           (int) (applyto.getStat().getInt() * (this.x / 100.0)));
                  }
               } else {
                  localstatups.put(SecondaryStatFlag.indieStance,
                        (int) (applyto.getStat().getStr() * (this.x / 100.0)));
               }
            }
            break;
         case 80001465:
            value = (int) ((applyto.getStat().getTotalStr() + applyto.getStat().getTotalDex()
                  + applyto.getStat().getTotalInt() + applyto.getStat().getTotalLuk())
                  * (this.x / 100.0));
            if ((applyto.getJob() < 100 || applyto.getJob() >= 200)
                  && applyto.getJob() != 512
                  && applyto.getJob() != 1512
                  && applyto.getJob() != 2512
                  && (applyto.getJob() < 1100 || applyto.getJob() >= 1200)
                  && !GameConstants.isAran(applyto.getJob())
                  && !GameConstants.isBlaster(applyto.getJob())
                  && !GameConstants.isDemonSlayer(applyto.getJob())
                  && !GameConstants.isMichael(applyto.getJob())
                  && !GameConstants.isKaiser(applyto.getJob())
                  && !GameConstants.isZero(applyto.getJob())
                  && !GameConstants.isArk(applyto.getJob())
                  && !GameConstants.isAdele(applyto.getJob())
                  && !GameConstants.isCannon(applyto.getJob())) {
               if ((applyto.getJob() < 200 || applyto.getJob() >= 300)
                     && !GameConstants.isFlameWizard(applyto.getJob())
                     && !GameConstants.isEvan(applyto.getJob())
                     && !GameConstants.isLuminous(applyto.getJob())
                     && (applyto.getJob() < 3200 || applyto.getJob() >= 3300)
                     && !GameConstants.isKinesis(applyto.getJob())
                     && !GameConstants.isIllium(applyto.getJob())
                     && !GameConstants.isLara(applyto.getJob())) {
                  if ((applyto.getJob() < 300 || applyto.getJob() >= 400)
                        && applyto.getJob() != 522
                        && applyto.getJob() != 532
                        && !GameConstants.isMechanic(applyto.getJob())
                        && !GameConstants.isAngelicBuster(applyto.getJob())
                        && (applyto.getJob() < 1300 || applyto.getJob() >= 1400)
                        && !GameConstants.isMercedes(applyto.getJob())
                        && (applyto.getJob() < 3300 || applyto.getJob() >= 3400)
                        && !GameConstants.isKain(applyto.getJob())) {
                     if ((applyto.getJob() < 400 || applyto.getJob() >= 500)
                           && (applyto.getJob() < 1400 || applyto.getJob() >= 1500)
                           && !GameConstants.isPhantom(applyto.getJob())
                           && !GameConstants.isKadena(applyto.getJob())
                           && !GameConstants.isHoyoung(applyto.getJob())) {
                        if (GameConstants.isDemonAvenger(applyto.getJob())) {
                           localstatups.put(SecondaryStatFlag.indieMHP, (int) (value * 17.5));
                        } else if (GameConstants.isXenon(applyto.getJob())) {
                           int str = applyto.getStat().getStr();
                           int dex = applyto.getStat().getDex();
                           int luk = applyto.getStat().getLuk();
                           if (str > dex && str > luk) {
                              localstatups.put(SecondaryStatFlag.indieSTR, value);
                           } else if (dex > str && dex > luk) {
                              localstatups.put(SecondaryStatFlag.indieDEX, value);
                           } else if (luk > str && luk > dex) {
                              localstatups.put(SecondaryStatFlag.indieLUK, value);
                           } else {
                              localstatups.put(SecondaryStatFlag.indieSTR, value);
                           }
                        }
                     } else {
                        localstatups.put(SecondaryStatFlag.indieLUK, value);
                     }
                  } else {
                     localstatups.put(SecondaryStatFlag.indieDEX, value);
                  }
               } else {
                  localstatups.put(SecondaryStatFlag.indieINT, value);
               }
            } else {
               localstatups.put(SecondaryStatFlag.indieSTR, value);
            }
            break;
         case 80001466:
         case 80001467:
         case 80001468:
         case 80001469:
            switch (this.sourceid) {
               case 80001466:
                  if (GameConstants.isDemonAvenger(applyto.getJob())) {
                     localstatups.put(SecondaryStatFlag.indieMHP, (int) (applyto.getLevel() * (this.x / 100.0) * 17.5));
                  } else {
                     localstatups.put(SecondaryStatFlag.indieSTR, (int) (applyto.getLevel() * (this.x / 100.0)));
                  }
                  break label1523;
               case 80001467:
                  localstatups.put(SecondaryStatFlag.indieDEX, (int) (applyto.getLevel() * (this.x / 100.0)));
                  break label1523;
               case 80001468:
                  localstatups.put(SecondaryStatFlag.indieINT, (int) (applyto.getLevel() * (this.x / 100.0)));
                  break label1523;
               case 80001469:
                  localstatups.put(SecondaryStatFlag.indieLUK, (int) (applyto.getLevel() * (this.x / 100.0)));
               default:
                  break label1523;
            }
         case 80001470:
         case 80001471:
         case 80001472:
         case 80001473:
            Item item = applyto.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
            if (item != null) {
               Equip equip = (Equip) item;
               int watk = equip.getTotalWatk();
               int matk = equip.getTotalMatk();
               switch (this.sourceid) {
                  case 80001470:
                     if (GameConstants.isDemonAvenger(applyto.getJob())) {
                        localstatups.put(SecondaryStatFlag.indieMHP, (int) (watk * (this.x / 100.0) * 17.5));
                     } else {
                        localstatups.put(SecondaryStatFlag.indieSTR, (int) (watk * (this.x / 100.0)));
                     }
                     break;
                  case 80001471:
                     localstatups.put(SecondaryStatFlag.indieDEX, (int) (watk * (this.x / 100.0)));
                     break;
                  case 80001472:
                     localstatups.put(SecondaryStatFlag.indieINT, (int) (matk * (this.x / 100.0)));
                     break;
                  case 80001473:
                     localstatups.put(SecondaryStatFlag.indieLUK, (int) (watk * (this.x / 100.0)));
               }
            }
            break;
         case 80001805:
            localstatups.clear();
            Map<SecondaryStatFlag, Integer> statups = new HashMap<>();
            statups.put(SecondaryStatFlag.indieMadR, 80);
            statups.put(SecondaryStatFlag.indiePadR, 80);
            statups.put(SecondaryStatFlag.SoulSkillDamageUp, 1);
            applyfrom.checkSoulState(true);
            applyfrom.temporaryStatSet(80001805, 1, 20000, statups, false, 0, false, true);
            applyfrom.temporaryStatSet(
                  SecondaryStatFlag.SoulMP,
                  applyfrom.getEquippedSoulSkill(),
                  Integer.MAX_VALUE,
                  applyfrom.getTotalSkillLevel(applyfrom.getEquippedSoulSkill()),
                  applyfrom.getSoulCount(),
                  false,
                  true,
                  false);
            break;
         case 80002280:
            if (new Date().getDay() == 0 && SpecialSunday.isActive && SpecialSunday.activeRuneEXP) {
               localstatups.clear();
               localstatups.put(SecondaryStatFlag.indieEXP, 200);
            }
         case 80001427:
         case 80001428:
         case 80001430:
         case 80001432:
            int buffTime = newDuration;
            int[] s = new int[] { 20010294, 80000369 };

            for (int skill : s) {
               if (applyfrom.getTotalSkillLevel(skill) > 0) {
                  SecondaryStatEffect e = applyfrom.getSkillLevelData(skill);
                  if (e != null) {
                     buffTime = (int) (newDuration + newDuration * 0.01 * e.getX());
                  }
                  break;
               }
            }

            localDuration = buffTime;
            break;
         case 80002623:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.BlackMageAttributes, applyto.getBlackMageAttributes());
            localDuration = Integer.MAX_VALUE;
            break;
         case 91001020:
            if (applyfrom.getParty() == null) {
               if (!applyfrom.isAlive()) {
                  applyfrom.healHP(applyfrom.getStat().getCurrentMaxHp(applyfrom));
               }
            } else {
               applyfrom.getPartyMembers().forEach(chr -> {
                  if (applyfrom.getId() != chr.getId() && !chr.isAlive() && chr.getMapId() == applyfrom.getMapId()) {
                     chr.healHP(chr.getStat().getCurrentMaxHp(chr), true);
                     if (chr.getGuildId() == applyfrom.getGuildId()) {
                        chr.healMP(chr.getStat().getCurrentMaxMp(chr));
                     }
                  }
               });
            }
         case 91001022:
            localstatups.put(SecondaryStatFlag.indieBDR, Integer.valueOf(this.indieBDR));
            break;
         case 91001023:
            localstatups.put(SecondaryStatFlag.indieIgnoreMobPdpR, Integer.valueOf(this.indieIgnoreMobpdpR));
            break;
         case 91001024:
            localstatups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(this.indieDamR));
            break;
         case 91001025:
            localstatups.put(SecondaryStatFlag.indieCD, Integer.valueOf(this.indieCD));
            break;
         case 151111005:
            localstatups.put(SecondaryStatFlag.Nobility, Integer.valueOf(this.level));
            applyto.setJobField("nobilityFromID", applyfrom.getId());
            applyto.setJobField("nobilityShield", 0);
            break;
         case 152111003:
            applyfrom.setActiveGloryWing(true);
            applyfrom.setUseMortalWingBit(false);
            applyfrom.temporaryStatReset(SecondaryStatFlag.CrystalChargeMax);
            Summoned crystal = applyfrom.getSummonBySkillID(152101000);
            if (crystal != null) {
               crystal.setEnergyCharge(0);
               applyfrom.setCrystalControlPos(crystal.getTruePosition());
               applyfrom.getMap().broadcastMessage(applyfrom, CField.summonSetEnergy(applyfrom, crystal, 2), true);
               crystal.resetEnableEnergySkill();
               applyfrom.getMap().broadcastMessage(applyfrom, CField.summonCrystalToggleSkill(applyfrom, crystal, 2),
                     true);
            }

            applyfrom.temporaryStatResetBySkillID(152101000);
            applyfrom.temporaryStatResetBySkillID(152101008);
            int blessMarkSKillID = applyfrom.getBlessMarkSkillID();
            SecondaryStatEffect blessMark = SkillFactory.getSkill(blessMarkSKillID)
                  .getEffect(applyfrom.getTotalSkillLevel(blessMarkSKillID));
            applyfrom.applyBlessMark(blessMark, 0, true, this.getDuration());
            PostSkillEffect e_ = new PostSkillEffect(applyfrom.getId(), 0, 1, null);
            applyfrom.send(e_.encodeForLocal());
            applyfrom.getMap().broadcastMessage(applyfrom, e_.encodeForRemote(), false);
            long count = applyfrom.getSummons().stream()
                  .filter(ss -> ss.getSkill() == 400021068 || ss.getSkill() == 500061012).count();
            long maxTill = 0L;
            if (count > 0L) {
               for (Summoned summonxxxx : applyfrom.getSummons()) {
                  if (summonxxxx.getSkill() == 400021068 || summonxxxx.getSkill() == 500061012) {
                     if (maxTill < summonxxxx.getSummonRemoveTime()) {
                        maxTill = summonxxxx.getSummonRemoveTime();
                     }

                     applyfrom.getMap().broadcastMessage(CField.SummonPacket.removeSummon(summonxxxx, true));
                     applyfrom.getMap().removeMapObject(summonxxxx);
                     applyfrom.removeVisibleMapObject(summonxxxx);
                     applyfrom.removeSummon(summonxxxx);
                     break;
                  }
               }

               int remainx = (int) (maxTill - System.currentTimeMillis());
               SecondaryStatEffect e = applyfrom.getSkillLevelData(400021068);
               if (e != null) {
                  applyfrom.temporaryStatSet(400021068, remainx, SecondaryStatFlag.indieDamR, (int) (e.getU() * count));
               }
            }
            break;
         case 152121043:
            localstatups = new EnumMap<>(SecondaryStatFlag.class);
            localstatups.put(SecondaryStatFlag.NotDamaged, 1);
            Integer valuex = applyfrom.getBuffedValue(SecondaryStatFlag.BlessMark);
            int blessMarkValue = 0;
            if (valuex != null) {
               blessMarkValue = valuex;
            }

            int duration = this.getDuration();
            if (blessMarkValue * 1000 > duration) {
               duration = blessMarkValue * 1000;
            }

            if (duration <= 0) {
               duration = 1000;
            } else if (duration > 10000) {
               duration = 10000;
            }

            Party party = applyfrom.getParty();
            if (party != null) {
               Rectangle bounds = this.calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());

               for (MapleMapObject object : applyfrom.getMap().getMapObjectsInRect(bounds,
                     Arrays.asList(MapleMapObjectType.PLAYER))) {
                  MapleCharacter player = (MapleCharacter) object;
                  if (player.getParty() != null && player.getParty().getId() == party.getId()) {
                     player.temporaryStatSet(152121043, applyfrom.getSkillLevel(152121043), duration, localstatups);
                  }
               }
            }

            applyfrom.temporaryStatSet(152121043, this.getLevel(), duration, localstatups);
            return;
         case 155001001:
            if (applyfrom.getBuffedValue(SecondaryStatFlag.DivineWrath) != null) {
               localDuration = 120000;
               localstatups.clear();
               localstatups.put(SecondaryStatFlag.indieStance, this.indieStance * 2);
               localstatups.put(SecondaryStatFlag.Speed, this.speed * 2);
               Consumer<MapleCharacter> consumer = p -> {
                  if (!GameConstants.isArk(p.getJob())) {
                     this.applyTo(p);
                  }
               };
               consumer.accept(applyfrom);
               SecondaryStatEffect effect = applyfrom.getBuffedEffect(SecondaryStatFlag.DivineWrath);
               if (effect != null) {
                  int var148 = effect.runToPMembersInArea(applyfrom, consumer);
               }
            }
            break;
         case 155101003:
            if (applyfrom.getBuffedValue(SecondaryStatFlag.DivineWrath) != null) {
               localstatups.clear();
               localDuration = 120000;
               localstatups.put(SecondaryStatFlag.indieCR, this.indieCr * 2);
               localstatups.put(SecondaryStatFlag.indiePAD, this.indiePad * 2);
               Consumer<MapleCharacter> consumer = p -> {
                  if (!GameConstants.isArk(p.getJob())) {
                     this.applyTo(p);
                  }
               };
               consumer.accept(applyfrom);
               SecondaryStatEffect effect = applyfrom.getBuffedEffect(SecondaryStatFlag.DivineWrath);
               if (effect != null) {
                  int var147 = effect.runToPMembersInArea(applyfrom, consumer);
               }
            }
            break;
         case 155111005:
            if (applyfrom.getBuffedValue(SecondaryStatFlag.DivineWrath) != null) {
               localstatups.clear();
               localDuration = 120000;
               localstatups.put(SecondaryStatFlag.indieBooster, this.indieBooster * 2);
               localstatups.put(SecondaryStatFlag.indieEvaR, this.indieEvaR * 2);
               Consumer<MapleCharacter> consumer = p -> {
                  if (!GameConstants.isArk(p.getJob())) {
                     this.applyTo(p);
                  }
               };
               consumer.accept(applyfrom);
               SecondaryStatEffect effect = applyfrom.getBuffedEffect(SecondaryStatFlag.DivineWrath);
               if (effect != null) {
                  int var146 = effect.runToPMembersInArea(applyfrom, consumer);
               }
            }
            break;
         case 155121005:
            if (applyfrom.getBuffedValue(SecondaryStatFlag.DivineWrath) != null) {
               localstatups.clear();
               localDuration = 120000;
               localstatups.put(SecondaryStatFlag.indieDamR, this.indieDamR * 2);
               localstatups.put(SecondaryStatFlag.indieIgnoreMobPdpR, this.indieIgnoreMobpdpR * 2);
               localstatups.put(SecondaryStatFlag.indieBDR, this.indieBDR * 2);
               Consumer<MapleCharacter> consumer = p -> {
                  if (!GameConstants.isArk(p.getJob())) {
                     this.applyTo(p);
                  }
               };
               consumer.accept(applyfrom);
               SecondaryStatEffect effect = applyfrom.getBuffedEffect(SecondaryStatFlag.DivineWrath);
               if (effect != null) {
                  int var145 = effect.runToPMembersInArea(applyfrom, consumer);
               }
            }
            break;
         case 164121042:
         case 400041007:
            localstatups.clear();
            break;
         case 400001005:
            applyto.temporaryStatResetBySkillID(2301004);
            applyto.temporaryStatResetBySkillID(2321005);
            applyto.temporaryStatResetBySkillID(8005);
            applyto.temporaryStatResetBySkillID(10008005);
            applyto.temporaryStatResetBySkillID(20008005);
            applyto.temporaryStatResetBySkillID(20018005);
            applyto.temporaryStatResetBySkillID(20028005);
            applyto.temporaryStatResetBySkillID(20038005);
            applyto.temporaryStatResetBySkillID(20048005);
            applyto.temporaryStatResetBySkillID(20058005);
            applyto.temporaryStatResetBySkillID(30008005);
            applyto.temporaryStatResetBySkillID(30018005);
            applyto.temporaryStatResetBySkillID(30028005);
            applyto.temporaryStatResetBySkillID(50008005);
            applyto.temporaryStatResetBySkillID(60008005);
            applyto.temporaryStatResetBySkillID(60018005);
            applyto.temporaryStatResetBySkillID(100008005);
            applyto.temporaryStatResetBySkillID(140008005);
            applyto.temporaryStatResetBySkillID(150008005);
            applyto.temporaryStatResetBySkillID(150018005);
            break;
         case 400001010:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.BlitzShield,
                  (int) (applyto.getStat().getCurrentMaxHp(applyto) * this.getX() * 0.01));
            break;
         case 400001011:
            localstatups.clear();
            applyto.temporaryStatReset(SecondaryStatFlag.BlitzShield);
            applyto.send(CField.userBonusAttackRequest(400001011, true, Collections.EMPTY_LIST));
            break;
         case 400001020:
            applyto.setJobField("holySymbolUserID", 0);
            applyto.setJobField("holySymbolLv", 0);
            applyto.setJobField("holySymbolActive", false);
            applyto.setJobField("holySymbolDecrease", false);
            applyto.setJobField("holySymbolDropR", this.v);
            localstatups.put(SecondaryStatFlag.HolySymbol, this.x);
            break;
         case 400001025:
         case 400001026:
         case 400001027:
         case 400001028:
         case 400001029:
         case 400001030:
            SecondaryStatEffect eff = SkillFactory.getSkill(400001024).getEffect(this.getLevel());
            if (eff != null) {
               localstatups.clear();
               int v = this.sourceid - 400001024;
               if (v > 0) {
                  applyto.temporaryStatReset(SecondaryStatFlag.FreudsProtection);
                  applyto.getStat().recalcLocalStats(applyto);
               }

               localstatups.put(SecondaryStatFlag.FreudsProtection, v);
               localstatups.put(SecondaryStatFlag.indieCooltimeReduce, eff.getW());
               if (this.sourceid >= 400001026) {
                  localstatups.put(SecondaryStatFlag.indieAsrR, eff.getZ());
               }

               if (this.sourceid >= 400001027) {
                  localstatups.put(SecondaryStatFlag.indieAllStat, eff.getQ());
               }

               if (this.sourceid >= 400001028) {
                  localstatups.put(SecondaryStatFlag.indiePAD, eff.getU());
                  localstatups.put(SecondaryStatFlag.indieMAD, eff.getU());
               }

               if (this.sourceid >= 400001029) {
                  localstatups.put(SecondaryStatFlag.indieBDR, eff.getV());
               }

               if (this.sourceid >= 400001030) {
                  localstatups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
               }

               if (v >= 6) {
                  applyto.send(CField.skillCooldown(400001024, eff.getY() * 1000));
                  applyto.addCooldown(400001024, System.currentTimeMillis(), eff.getY() * 1000L);
               }
            }
            break;
         case 400001037:
            localstatups.clear();
            int damR = 0;
            int mpPer = (int) (applyto.getStat().getMp() / (applyto.getStat().getCurrentMaxMp(applyto) * 0.01));
            damR = (int) (this.getY() * 0.01 * mpPer);
            localstatups.put(SecondaryStatFlag.indieDamR, damR);
            localstatups.put(SecondaryStatFlag.MagicCircuitFullDrive, 1);
            int mpRCon = this.getMpRCon();
            int mp = (int) (applyto.getStat().getCurrentMaxMp(applyto) * 0.01 * mpRCon);
            applyto.addMP(-mp, true);
            break;
         case 400001042:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(this.getIndieDamR()));
            Integer vx = applyto.getBuffedValue(SecondaryStatFlag.BasicStatUp);
            if (vx != null) {
               localstatups.put(SecondaryStatFlag.indieStatRBasic, (int) (vx.intValue() * 0.01 * this.x));
            }
            break;
         case 400001047:
            applyto.getSecondaryStat().EmpressBlessRemainNoCool = this.y;
            break;
         case 400001048:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.EmpressBless, 1);
            int indiePad_ = this.getIndiePad();
            int indieMad_ = this.getIndieMad();
            Iterator<Item> items = applyto.getInventory(MapleInventoryType.EQUIPPED).newList().iterator();
            int totalPad = 0;
            int totalMad = 0;
            int weaponPad = 0;
            int weaponMad = 0;

            while (items.hasNext()) {
               Equip equip = (Equip) items.next();
               if (equip.getPosition() == -11) {
                  weaponPad += equip.getWatk();
                  weaponMad += equip.getMatk();
               } else {
                  totalPad += equip.getTotalWatk();
                  totalMad += equip.getTotalMatk();
               }
            }

            int max = 0;
            if (GameConstants.isAdele(applyto.getJob()) || GameConstants.isArk(applyto.getJob())
                  || GameConstants.isKhali(applyto.getJob())) {
               max = weaponPad * this.getY() / 100;
            } else if (GameConstants.isIllium(applyto.getJob())) {
               max = weaponMad * this.getY() / 100;
            }

            if (max > 0) {
               int maxPad = Math.min(max, totalPad * this.getX() / 100);
               int maxMad = Math.min(max, totalMad * this.getX() / 100);
               localstatups.put(SecondaryStatFlag.indiePAD, indiePad_ + maxMad);
               localstatups.put(SecondaryStatFlag.indieMAD, indieMad_ + maxPad);
            }
            break;
         case 400007000:
         case 400007001:
         case 400007002:
         case 400007009:
         case 400007010:
            localstatups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(this.indieDamR));
            break;
         case 400007003:
            localstatups.put(SecondaryStatFlag.indieCR, Integer.valueOf(this.indieCr));
            break;
         case 400007004:
            localstatups.put(SecondaryStatFlag.indieCD, Integer.valueOf(this.indieCD));
            break;
         case 400007005:
            localstatups.put(SecondaryStatFlag.indieIgnoreMobPdpR, Integer.valueOf(this.indieIgnoreMobpdpR));
            break;
         case 400007006:
            localstatups.put(SecondaryStatFlag.indiePartialNotDamaged, 1);
            break;
         case 400007007:
         case 400007011:
            localstatups.put(SecondaryStatFlag.indieEXP, Integer.valueOf(this.indieExp));
            break;
         case 400007008:
            localstatups.put(SecondaryStatFlag.indieBDR, Integer.valueOf(this.indieBDR));
            break;
         case 400011001:
         case 400011002:
            localstatups.put(SecondaryStatFlag.indieSummon, 1);
            SummonMoveAbility summonMovementType = null;
            int summId = this.sourceid;
            if (this.sourceid != 400011002) {
               if (applyfrom.getSummonBySkillID(400011001) != null) {
                  summonMovementType = SummonMoveAbility.SHADOW_SERVANT_EXTEND;
                  summId = 400011002;

                  for (Summoned summon : applyfrom.getSummons()) {
                     if (summon.getSkill() == 400011001) {
                        localDuration = (int) (summon.getSummonRemoveTime() - System.currentTimeMillis());
                        applyfrom.getMap().broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
                        applyfrom.getMap().removeMapObject(summon);
                        applyfrom.removeVisibleMapObject(summon);
                        applyfrom.removeSummon(summon);
                     }
                  }
               } else if (applyfrom.getSummonBySkillID(400011002) != null) {
                  summonMovementType = SummonMoveAbility.FOLLOW;
                  summId = 400011001;

                  for (Summoned summonx : applyfrom.getSummons()) {
                     if (summonx.getSkill() == 400011002) {
                        localDuration = (int) (summonx.getSummonRemoveTime() - System.currentTimeMillis());
                        applyfrom.getMap().broadcastMessage(CField.SummonPacket.removeSummon(summonx, true));
                        applyfrom.getMap().removeMapObject(summonx);
                        applyfrom.removeVisibleMapObject(summonx);
                        applyfrom.removeSummon(summonx);
                     }
                  }
               } else {
                  summonMovementType = SummonMoveAbility.FOLLOW;
                  localDuration = this.duration;
                  applyto.invokeJobMethod("handleOrbgain", 400011001);
               }
            } else {
               summonMovementType = SummonMoveAbility.SHADOW_SERVANT_EXTEND;
               summId = 400011002;
               localDuration = this.duration;
               if (applyfrom.getSummonBySkillID(400011001) != null) {
                  for (Summoned summonxx : applyfrom.getSummons()) {
                     if (summonxx.getSkill() == 400011001) {
                        applyfrom.getMap().broadcastMessage(CField.SummonPacket.removeSummon(summonxx, true));
                        applyfrom.getMap().removeMapObject(summonxx);
                        applyfrom.removeVisibleMapObject(summonxx);
                        applyfrom.removeSummon(summonxx);
                     }
                  }
               }

               if (applyfrom.getSummonBySkillID(400011002) != null) {
                  for (Summoned summonxxx : applyfrom.getSummons()) {
                     if (summonxxx.getSkill() == 400011002) {
                        applyfrom.getMap().broadcastMessage(CField.SummonPacket.removeSummon(summonxxx, true));
                        applyfrom.getMap().removeMapObject(summonxxx);
                        applyfrom.removeVisibleMapObject(summonxxx);
                        applyfrom.removeSummon(summonxxx);
                     }
                  }
               }

               applyto.invokeJobMethod("handleOrbgain", 400011001);
            }

            long starttime = System.currentTimeMillis();
            if (localDuration >= 0) {
               Summoned tosummon = new Summoned(
                     applyfrom,
                     summId,
                     this.getLevel(),
                     applyfrom.getMap().calcDropPos(applyfrom.getTruePosition(), applyfrom.getTruePosition()),
                     summonMovementType,
                     (byte) 0,
                     starttime + localDuration);
               applyfrom.getMap().spawnSummon(tosummon, localDuration);
               applyfrom.addSummon(tosummon);
            }
            break;
         case 400011003:
         case 500061000:
            localstatups.clear();
            if (applyfrom.getParty() != null) {
               Object lastTargetId = applyfrom.getJobField("lastHolyUnityCharId");
               List<MapleCharacter> list = applyfrom.getPartyMembers();
               if (lastTargetId != null && (Integer) lastTargetId != 0) {
                  boolean isAround = false;
                  MapleCharacter target = null;
                  List<MapleCharacter> objs = applyfrom.getMap().getCharactersInRange(applyfrom.getTruePosition(),
                        500000.0);
                  if (objs != null) {
                     for (MapleCharacter chr : objs) {
                        try {
                           if (list.contains(chr) && chr.getId() == (Integer) lastTargetId) {
                              isAround = true;
                              target = chr;
                           }
                        } catch (Exception var41) {
                        }
                     }
                  }

                  if (isAround) {
                     localstatups.put(SecondaryStatFlag.HolyUnity, target != null ? target.getId() : 0);
                     if (target != null) {
                        applyfrom.setJobField("lastHolyUnityCharId", target.getId());
                        target.temporaryStatSet(SecondaryStatFlag.HolyUnity, 400011021, newDuration, this.level,
                              applyfrom.getId());
                     }
                  } else {
                     List<MapleCharacter> ret = new ArrayList<>();

                     for (MapleCharacter m : list) {
                        if (m.getId() != applyfrom.getId() && m.getJob() != 122) {
                           ret.add(m);
                        }
                     }

                     Collections.shuffle(ret);
                     MapleCharacter randomTarget = ret.get(0);
                     localstatups.put(SecondaryStatFlag.HolyUnity, randomTarget != null ? randomTarget.getId() : 0);
                     if (randomTarget != null) {
                        applyfrom.setJobField("lastHolyUnityCharId", randomTarget.getId());
                        randomTarget.temporaryStatSet(SecondaryStatFlag.HolyUnity, 400011021, newDuration, this.level,
                              applyfrom.getId());
                     }
                  }
               } else {
                  List<MapleCharacter> ret = new ArrayList<>();

                  for (MapleCharacter mx : list) {
                     if (mx.getId() != applyfrom.getId() && mx.getJob() != 122) {
                        ret.add(mx);
                     }
                  }

                  if (list.size() == 1 || ret.isEmpty()) {
                     ret.add(applyfrom);
                  }

                  Collections.shuffle(ret);
                  MapleCharacter targetx = ret.get(0);
                  localstatups.put(SecondaryStatFlag.HolyUnity, targetx != null ? targetx.getId() : 0);
                  if (targetx != null) {
                     applyfrom.setJobField("lastHolyUnityCharId", targetx.getId());
                     targetx.temporaryStatSet(SecondaryStatFlag.HolyUnity, 400011021, newDuration, this.level,
                           applyfrom.getId());
                  }
               }
            } else {
               localstatups.put(SecondaryStatFlag.HolyUnity, applyfrom.getId());
            }
            break;
         case 400011011:
         case 500061008:
            if (applyto.getBuffedValue(SecondaryStatFlag.RhoAias) != null) {
               applyto.temporaryStatReset(SecondaryStatFlag.RhoAias);
               return;
            }

            applyto.setJobField("rhoAiasC", isSelf ? 1 : this.x);
            applyto.setJobField("rhoAiasX", this.y);
            applyto.setJobField("rhoAiasFrom", applyfrom.getId());
            applyto.setJobField("rhoAiasLevel", 1);
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.RhoAias, this.x);
            int pmdr = this.indiePMdR == 0 ? this.u : this.indiePMdR;
            localstatups.put(SecondaryStatFlag.indiePMDR, pmdr);
            break;
         case 400011016:
         case 500061039:
            applyfrom.setLastCombo(System.currentTimeMillis());
            short combo = (short) Math.min(9999, applyfrom.getCombo() + 160);
            applyfrom.setCombo(combo);
            applyfrom.send(CField.aranCombo(combo));
            AffectedArea mist = applyfrom.getMap().getMistBySkillId(21121068);
            if (mist != null) {
               applyfrom.getMap()
                     .broadcastMessage(CField.removeAffectedArea(mist.getObjectId(), mist.getSourceSkillID(), false));
               applyfrom.getMap().removeMapObject(mist);
            }
            break;
         case 400011055:
            applyto.clearCooldown(11121052);
            break;
         case 400011066:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.StackDamR, 1);
            localstatups.put(SecondaryStatFlag.indieSuperStance, 1);
            localstatups.put(SecondaryStatFlag.indieAsrR, this.getIndieAsrR());
            break;
         case 400011087:
            if (applyto.getBuffedValue(SecondaryStatFlag.SwordOfSoulLight) != null
                  && applyto.getBuffedValue(SecondaryStatFlag.SwordOfSoulLight) > 1) {
               SecondaryStatManager statManager = new SecondaryStatManager(applyto.getClient(),
                     applyto.getSecondaryStat());
               statManager.changeStatValue(SecondaryStatFlag.SwordOfSoulLight, 400011083, 1);
               statManager.temporaryStatSet();
            }
            break;
         case 400011112:
         case 500061054: {
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.Revenant, 1);
            int hp = (int) (applyto.getStat().getCurrentMaxHp(applyto) * 0.01 * this.hpRCon);
            applyto.setRevenantRage(hp);
            break;
         }
         case 400011127:
         case 500061010: {
            int hp = (int) (applyto.getStat().getCurrentMaxHp(applyto) * 0.01) * this.x;
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.indieBarrier, hp);
            break;
         }
         case 400021006:
            if (isSelf && applyfrom.getBuffedValue(SecondaryStatFlag.UnionAuraBlow) != null) {
               applyfrom.temporaryStatReset(SecondaryStatFlag.UnionAuraBlow);
               return;
            }

            this.cancelAnotherAuraSkill(applyto);
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.UnionAuraBlow, 1);
            SecondaryStatEffect e1 = applyfrom.getSkillLevelData(32101009);
            SecondaryStatEffect e2 = applyfrom.getSkillLevelData(32001016);
            SecondaryStatEffect e3 = applyfrom.getSkillLevelData(32111012);
            SecondaryStatEffect e4 = applyfrom.getSkillLevelData(32121017);
            localstatups.put(SecondaryStatFlag.DebuffAura, 1);
            localstatups.put(SecondaryStatFlag.DarkAura, Integer.valueOf(this.getLevel()));
            localstatups.put(SecondaryStatFlag.BlueAura, Integer.valueOf(this.getLevel()));
            localstatups.put(SecondaryStatFlag.YellowAura, Integer.valueOf(this.getLevel()));
            localstatups.put(SecondaryStatFlag.DrainAura, Integer.valueOf(this.getLevel()));
            if (e1 != null) {
               localstatups.put(SecondaryStatFlag.ComboDrain, e1.getX());
            }

            if (e2 != null) {
               localstatups.put(SecondaryStatFlag.indieSpeed, e2.getIndieSpeed());
               localstatups.put(SecondaryStatFlag.indieBooster, e2.getIndieBooster());
            }

            if (e3 != null) {
               localstatups.put(SecondaryStatFlag.indieAsrR, e3.getIndieAsrR());
            }

            if (e4 != null) {
               localstatups.put(SecondaryStatFlag.indieDamR, Integer.valueOf(e4.getIndieDamR()));
            }

            if (isSelf) {
               localstatups.put(SecondaryStatFlag.indieMAD, Integer.valueOf(this.getIndieMad()));
            }
            break;
         case 400031020:
            localstatups.put(SecondaryStatFlag.ProfessionalAgent, 1);
            break;
         case 400031028:
            localstatups.clear();
            applyto.getSecondaryStat().QuiverFullBurst = this.getU2();
            localstatups.put(SecondaryStatFlag.QuiverFullBurst, 1);
            localstatups.put(SecondaryStatFlag.indiePadR, Integer.valueOf(this.indiePadR));
            break;
         case 400031037:
         case 400031038:
         case 500061018:
         case 500061019:
         case 500061022:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.indieDamReduceR, -this.x);
            localstatups.put(SecondaryStatFlag.indieQrPointTerm, 1);
            break;
         case 400031039:
         case 400031040:
         case 500061020:
         case 500061021:
         case 500061023:
         case 500061024:
            localstatups.clear();
            localstatups.put(SecondaryStatFlag.indieDamReduceR, -this.x);
            break;
         case 400041002:
         case 400041003:
         case 400041004:
         case 400041005:
         case 500061025:
         case 500061026:
         case 500061027:
         case 500061028:
            localstatups.clear();
            int remain = 0;
            if (this.sourceid != 400041002 && this.sourceid != 500061025) {
               Integer value2 = applyto.getBuffedValue(SecondaryStatFlag.ShadowAssault);
               if (value2 != null) {
                  remain = value2 - 1;
               }
            } else {
               remain = this.sourceid == 500061025 ? 4 : 3;
            }

            if (remain <= 0) {
               applyto.temporaryStatReset(SecondaryStatFlag.ShadowAssault);
            } else {
               localstatups.put(SecondaryStatFlag.ShadowAssault, remain);
               localDuration = Integer.MAX_VALUE;
            }
            break;
         case 400041009:
            showEffect = false;
            localstatups.put(SecondaryStatFlag.KeyDownMoving, this.x);
            break;
         case 400041040:
            localstatups.clear();
            break;
         case 400051002:
            applyto.setViperEnergyOrb(this.getW());
            localstatups.put(SecondaryStatFlag.indiePMDR, Integer.valueOf(this.indiePMdR));
            localstatups.put(SecondaryStatFlag.Transform, applyto.getViperEnergyOrb());
            break;
         case 400051010:
         case 500061006:
            localstatups.put(SecondaryStatFlag.ProfessionalAgent, 1);
            localstatups.put(SecondaryStatFlag.indiePMDR, Integer.valueOf(this.indiePMdR));
            break;
         case 400051015:
            applyto.getSecondaryStat().SerpentScrewRemainCount = this.w2;
            applyto.getSecondaryStat().SerpentScrewBossAttackCount = this.q;
            localstatups.put(SecondaryStatFlag.SerpentScrew, this.v);
            localDuration = Integer.MAX_VALUE;
            break;
         case 400051033:
            this.statups.clear();
            localstatups = new EnumMap<>(SecondaryStatFlag.class);
            localstatups.put(SecondaryStatFlag.OverDrive, 1);
            Equip weapon = (Equip) applyto.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
            if (weapon != null) {
               int pad = (int) (weapon.getWatk() * (this.getX() * 0.01));
               localstatups.put(SecondaryStatFlag.indiePAD, pad);
            }
            break;
         default:
            if (this.isPirateMorph()) {
               EnumMap<SecondaryStatFlag, Integer> statx = new EnumMap<>(SecondaryStatFlag.class);
               statx.put(SecondaryStatFlag.Morph, this.getMorph(applyto));
               localstatups.clear();
               applyto.temporaryStatSet(this.sourceid, this.getLevel(), this.getDuration(), localstatups);
               SkillEffect e = new SkillEffect(applyto.getId(), applyto.getLevel(), this.sourceid, this.level, null);
               applyto.send(e.encodeForLocal());
               maskedStatups.remove(SecondaryStatFlag.Morph);
            } else if (this.isMorph()) {
               if (!applyto.isHidden()) {
                  EnumMap<SecondaryStatFlag, Integer> statx = new EnumMap<>(SecondaryStatFlag.class);
                  statx.put(SecondaryStatFlag.Morph, this.getMorph(applyto));
                  localstatups.put(SecondaryStatFlag.Morph, 0);
                  localstatups.put(SecondaryStatFlag.EnhancedDEF, this.getMorph(applyto));
               }
            } else if (this.isInflation()) {
               if (!applyto.isHidden()) {
                  new EnumMap<>(SecondaryStatFlag.class);
               }
            } else if (this.isMonsterRiding()) {
               new EnumMap<>(SecondaryStatFlag.class);
               int skillID2 = this.sourceid;
               if (skillID2 == 400031014) {
                  skillID2 = 33001001;
               }

               int mountid = parseMountInfo(applyfrom, skillID2);
               if (this.sourceid == 400031017 || this.sourceid == 500061050) {
                  applyto.temporaryStatSet(23110004, this.getDuration(this.getDuration() + 15000, applyto),
                        SecondaryStatFlag.PMDR, 10);
               }

               if (this.sourceid != 35001002 && this.sourceid != 35111003) {
                  if (mountid != 0 && (this.sourceid == 33001001 || this.sourceid == 400031014)) {
                     this.applyJaguarRiding(applyfrom, mountid);
                     if (this.sourceid == 400031014) {
                        SecondaryStatEffect e = SkillFactory.getSkill(400031012).getEffect(this.getLevel());
                        if (e != null) {
                           applyto.temporaryStatSet(400031012, e.getS() * 1000, SecondaryStatFlag.NotDamaged, 1);
                        }
                     }

                     return;
                  }
               } else {
                  if (this.sourceid == 35001002) {
                     int epdd = this.getEnhancedWdef();
                     int epad = this.getEnhancedWatk();
                     int emmp = this.getEnhancedHP();
                     int emhp = this.getEnhancedMP();
                     int slvxx = 0;
                     if ((slvxx = applyfrom.getTotalSkillLevel(35120000)) > 0) {
                        SecondaryStatEffect effx = SkillFactory.getSkill(35120000).getEffect(slvxx);
                        if (effx != null) {
                           epdd = effx.getEnhancedWdef();
                           epad = effx.getEnhancedWatk();
                        }
                     }

                     localstatups.clear();
                     localstatups.put(SecondaryStatFlag.Mechanic, Integer.valueOf(this.level));
                     localstatups.put(SecondaryStatFlag.EnhancedDEF, epdd);
                     localstatups.put(SecondaryStatFlag.EnhancedPAD, epad);
                     localstatups.put(SecondaryStatFlag.indieBooster, -1);
                     localstatups.put(SecondaryStatFlag.indieSpeed, this.getIndieSpeed());
                     localDuration = Integer.MAX_VALUE;
                  }

                  Map<SecondaryStatFlag, Integer> flags = new HashMap<>();
                  SecondaryStatEffect e = SkillFactory.getSkill(30000227).getEffect(1);
                  flags.put(SecondaryStatFlag.HiddenPieceOn, applyto.getOneInfoQuestInteger(19752, "hiddenpiece"));
                  flags.put(SecondaryStatFlag.indieMHPR, e.getIndieMhpR());
                  flags.put(SecondaryStatFlag.indieMMPR, e.getIndieMmpR());
                  flags.put(SecondaryStatFlag.indieDamR, Integer.valueOf(e.getIndieDamR()));
                  applyto.temporaryStatSet(30000227, 1, Integer.MAX_VALUE, flags);
               }

               if (this.sourceid != 22171080 && this.sourceid != 400031017 && this.sourceid != 500061050) {
                  if (localDuration <= 0) {
                     localDuration = Integer.MAX_VALUE;
                  }

                  localstatups.put(SecondaryStatFlag.RideVehicle, mountid);
                  if (!DBConfig.isGanglim) {
                     applyto.startFishingTask();
                  } else if (ServerConstants.isRoyalFishingMap(applyto.getMapId())) {
                     applyto.startChairTask();
                  }
               } else if (this.sourceid != 400031017 && this.sourceid != 500061050) {
                  localstatups.put(SecondaryStatFlag.RideVehicleExpire, mountid);
                  localstatups.put(SecondaryStatFlag.NewFlying, 1);
                  localstatups.put(SecondaryStatFlag.NotDamaged, 1);
               } else {
                  localstatups.put(SecondaryStatFlag.RideVehicleExpire, mountid);
               }
            } else if (this.isSoaring()) {
               if (!applyto.isHidden()) {
                  new EnumMap<>(SecondaryStatFlag.class);
               }
            } else if (this.berserk > 0) {
               if (!applyto.isHidden()) {
                  new EnumMap<>(SecondaryStatFlag.class);
               }
            } else if (this.isHeroWill()) {
               localstatups.put(SecondaryStatFlag.AntiMagicShell, 1);
               localDuration = 3000;
            }
      }

      if (localstatups.size() != 0) {
         if (this.sourceid != 400021094) {
            if (showEffect
                  && !applyto.isHidden()
                  && !attack
                  && (this.sourceid < 400041002 || this.sourceid > 400041005)
                  && (this.sourceid < 500061025 || this.sourceid > 500061028)
                  && this.sourceid != 151111005
                  && this.sourceid != 63121044) {
               PacketEncoder p = new PacketEncoder();
               if (GameConstants.is_unregistered_skill(this.sourceid)) {
                  p.write(rltype);
               }

               if (this.sourceid == 400041032) {
                  p.write(0);
               }

               SkillEffect e = new SkillEffect(applyto.getId(), applyto.getLevel(), this.sourceid, this.level, p);
               applyto.getMap().broadcastMessage(applyto, e.encodeForRemote(), false);
            }

            if (this.isMechPassive()) {
               SkillEffect e = new SkillEffect(applyto.getId(), applyto.getLevel(), this.sourceid - 1000, this.level,
                     null);
               applyto.send(e.encodeForLocal());
               applyto.getMap().broadcastMessage(applyto, e.encodeForRemote(), false);
            }

            if (normal
                  && localstatups.size() > 0
                  && this.getSummonMovementType() == null
                  && this.sourceid == 100001264
                  && applyto.getBuffedValue(SecondaryStatFlag.ZeroAuraSTR) != null) {
               applyto.dispelSkill(100001263);
            }

            int durationx = maskedDuration > 0 ? maskedDuration : localDuration;
            durationx = this.getDuration() != durationx ? durationx : this.getDuration(durationx, applyfrom);
            applyto.temporaryStatSet(
                  this.skill ? this.sourceid : -this.sourceid,
                  this.level,
                  durationx,
                  maskedStatups == null ? localstatups : maskedStatups,
                  false,
                  applyfrom.getId(),
                  true,
                  false,
                  applyto.getId());
            if (this.sourceid == 35001002 && applyto.hasBuffBySkillID(35111003)) {
               applyto.temporaryStatResetBySkillID(35111003, false);
            }

            if (this.sourceid == 35111003 && applyto.hasBuffBySkillID(35001002)) {
               applyto.temporaryStatResetBySkillID(35001002, false);
            }
         }
      }
   }

   public static final int parseMountInfo(MapleCharacter player, int skillid) {
      int vehicleID = SkillFactory.getVehicleID(skillid);
      if (vehicleID > 0) {
         return vehicleID;
      } else {
         switch (skillid) {
            case 1004:
            case 10001004:
            case 20001004:
            case 20011004:
            case 20021004:
            case 20031004:
            case 30001004:
            case 30011004:
            case 50001004:
               if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -122) != null) {
                  return player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -122).getItemId();
               } else {
                  if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -22) != null) {
                     return player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -22).getItemId();
                  }

                  return 0;
               }
            case 22171080:
               return 1939007;
            case 30011109:
            case 30011159:
               return 1932051;
            case 35001002:
            case 35111003:
            case 35120000:
               return 1932016;
            case 400031017:
            case 500061050:
               return 1932417;
            default:
               return GameConstants.getMountItem(skillid, player);
         }
      }
   }

   public static final int parseMountInfo_Pure(MapleCharacter player, int skillid) {
      switch (skillid) {
         case 1004:
         case 11004:
         case 10001004:
         case 20001004:
         case 20011004:
         case 20021004:
         case 80001000:
            if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18) != null
                  && player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -19) != null) {
               return player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18).getItemId();
            }

            return 0;
         case 35001002:
         case 35120000:
            return 1932016;
         default:
            return GameConstants.getMountItem(skillid, player);
      }
   }

   public final int calcHPChange(MapleCharacter applyfrom, boolean primary) {
      int hpchange = 0;
      if (this.hp != 0) {
         if (!this.skill) {
            if (primary) {
               hpchange += this.alchemistModifyVal(applyfrom, this.hp, true);
            } else {
               hpchange += this.hp;
            }

            if (applyfrom.hasDisease(SecondaryStatFlag.Undead)) {
               hpchange /= 2;
            }
         } else if (this.sourceid != 2321007 && this.sourceid != 2341000 && this.sourceid != 2341001) {
            hpchange += makeHealHP(this.hp / 100.0, applyfrom.getStat().getTotalMagic(), 3.0, 5.0);
            if (applyfrom.hasDisease(SecondaryStatFlag.Undead)) {
               this.hpCon = (short) hpchange;
               hpchange = 0;
            }
         } else {
            this.hpR = this.hp;
         }
      }

      if (this.hpR != 0.0) {
         hpchange += (int) (applyfrom.getStat().getCurrentMaxHp(applyfrom) * this.hpR)
               / (applyfrom.hasDisease(SecondaryStatFlag.Undead) ? 2 : 1);
      }

      if (this.hpRCon != 0) {
         hpchange -= (int) (applyfrom.getStat().getCurrentMaxHp(applyfrom) * 0.01) * this.hpRCon;
      }

      if (primary && this.hpCon != 0) {
         hpchange -= this.hpCon;
      }

      switch (this.sourceid) {
         case 4211001:
            PlayerStats stat = applyfrom.getStat();
            int v42 = this.getY() + 100;
            int v38 = Randomizer.rand(1, 100) + 100;
            hpchange = (int) ((v38 * stat.getLuk() * 0.033 + stat.getDex()) * v42 * 0.002);
            hpchange += makeHealHP(this.getY() / 100.0, applyfrom.getStat().getTotalLuk(), 2.3, 3.5);
         default:
            if (!this.skill && hpchange > 0 && applyfrom.getTotalSkillLevel(14110027) > 0) {
               SecondaryStatEffect effect = SkillFactory.getSkill(14110027)
                     .getEffect(applyfrom.getTotalSkillLevel(14110027));
               if (effect != null) {
                  int x = effect.getX();
                  hpchange += (int) (hpchange * 0.01) * x;
               }
            }

            if (hpchange > 999999) {
               hpchange = 999999;
            }

            if (hpchange > 0 && !this.isSkill()
                  && applyfrom.getStat().getCurrentMaxHp(applyfrom) != applyfrom.getStat().getHp()) {
               AchievementFactory.checkItemUse(applyfrom, this.sourceid, hpchange, 0);
            }

            if (hpchange > 0 && applyfrom.getBuffedValue(SecondaryStatFlag.DebuffIncHP) != null) {
               hpchange -= (int) (hpchange * 0.01 * applyfrom.getBuffedValue(SecondaryStatFlag.DebuffIncHP).intValue());
            }

            if (applyfrom.getMap() instanceof Field_LucidBattle) {
               Field_LucidBattle f = (Field_LucidBattle) applyfrom.getMap();
               if (hpchange > 0 && f.isHellMode()) {
                  hpchange /= 2;
               }
            }

            if (applyfrom.hasBuffBySkillID(31221054) && hpchange < 0) {
               hpchange = 0;
            }

            return hpchange;
      }
   }

   private static final int makeHealHP(double rate, double stat, double lowerfactor, double upperfactor) {
      return (int) (Math.random() * ((int) (stat * upperfactor * rate) - (int) (stat * lowerfactor * rate) + 1)
            + (int) (stat * lowerfactor * rate));
   }

   public final int calcMPChange(MapleCharacter applyfrom, boolean primary) {
      int mpchange = 0;
      if (this.mp != 0) {
         if (primary) {
            mpchange += this.alchemistModifyVal(applyfrom, this.mp, false);
         } else {
            mpchange += this.mp;
         }
      }

      if (this.mpR != 0.0) {
         mpchange += (int) (applyfrom.getStat().getCurrentMaxMp(applyfrom) * this.mpR);
      }

      if (GameConstants.isDemonSlayer(applyfrom.getJob())) {
         mpchange = 0;
      }

      Integer value;
      if ((value = applyfrom.getBuffedValue(SecondaryStatFlag.AdvancedBless)) != null) {
         mpchange = (int) (mpchange - mpchange * value / 100.0);
      }

      if (GameConstants.isLuminous(applyfrom.getJob())
            && applyfrom.getBuffedValue(SecondaryStatFlag.Larkness) != null
            && GameConstants.isLarknessDarkSkill(this.sourceid)
            && applyfrom.getBuffedValue(SecondaryStatFlag.Larkness) == 2) {
         mpchange = 0;
         primary = false;
      }

      if (primary) {
         if (this.mpCon != 0 && !GameConstants.isDemonSlayer(applyfrom.getJob())) {
            if (applyfrom.getBuffedValue(SecondaryStatFlag.Infinity) != null || applyfrom.getEnergyChargeBuff() == 1) {
               mpchange = 0;
            } else if (!applyfrom.hasBuffBySkillID(12120013) && !applyfrom.hasBuffBySkillID(12120014)) {
               mpchange = (int) (mpchange - (this.mpCon - this.mpCon * applyfrom.getStat().mpconReduce / 100)
                     * (applyfrom.getStat().mpconPercent / 100.0));
            } else {
               SecondaryStatEffect e = applyfrom.getSkillLevelData(12121004);
               if (e != null) {
                  mpchange = (int) (this.mpCon * (e.getV() * 0.01));
               }
            }
         } else if (this.forceCon != 0 && GameConstants.isDemonSlayer(applyfrom.getJob())) {
            if (applyfrom.getBuffedValue(SecondaryStatFlag.InfinityForce) != null) {
               mpchange = 0;
            } else {
               int force = this.forceCon;
               if (applyfrom.getTotalSkillLevel(31121054) > 0) {
                  SecondaryStatEffect e = SkillFactory.getSkill(31121054)
                        .getEffect(applyfrom.getTotalSkillLevel(31121054));
                  if (e != null) {
                     force = (int) (force - this.forceCon * 0.01 * e.reduceForceR);
                  }
               }

               if (this.getSourceId() == 31121005) {
                  int slv = applyfrom.getTotalSkillLevel(31120048);
                  if (slv > 0) {
                     SecondaryStatEffect e = SkillFactory.getSkill(31120048).getEffect(slv);
                     force = (int) (force - force * 0.01 * e.reduceForceR);
                  }
               }

               if (this.getSourceId() == 31121001) {
                  int slv = applyfrom.getTotalSkillLevel(31120051);
                  if (slv > 0) {
                     SecondaryStatEffect e = SkillFactory.getSkill(31120051).getEffect(slv);
                     force = (int) (force - force * 0.01 * e.reduceForceR);
                  }
               }

               mpchange -= force;
            }
         }

         if (applyfrom.getJob() >= 330
               && applyfrom.getJob() <= 332
               && (this.sourceid < 3321035 || this.sourceid > 3321038)
               && this.sourceid != 3321040
               && this.sourceid != 400031036
               && this.sourceid != 400031067) {
            applyfrom.handleRelicChargeCon(this.sourceid, this.forceCon, 0);
         }
      }

      if (mpchange > 0 && !this.skill && applyfrom.getTotalSkillLevel(14110027) > 0) {
         SecondaryStatEffect effect = SkillFactory.getSkill(14110027).getEffect(applyfrom.getTotalSkillLevel(14110027));
         if (effect != null) {
            int x = effect.getX();
            mpchange += (int) (mpchange * 0.01) * x;
         }
      }

      if (mpchange != 0 && applyfrom.getTotalSkillLevel(22140020) > 0) {
         SecondaryStatEffect effect = SkillFactory.getSkill(22140020).getEffect(applyfrom.getTotalSkillLevel(22140020));
         if (effect != null) {
            int y = effect.getY();
            mpchange = (int) (mpchange * 0.01 * y);
         }
      }

      if (mpchange > 999999) {
         mpchange = 999999;
      }

      if (applyfrom.checkSpiritFlow(this.sourceid)) {
         mpchange = 0;
      }

      if (this.sourceid == 400021072) {
         mpchange = 0;
      }

      if ((this.sourceid == 11111022 || this.sourceid == 11101022 || this.sourceid == 11121011
            || this.sourceid == 11121012)
            && applyfrom.hasBuffBySkillID(11121005)) {
         mpchange = 0;
      }

      if (this.sourceid == 400011083) {
         mpchange = -this.x;
      }

      if (applyfrom.getIndieTemporaryStatEntry(SecondaryStatFlag.indieMPConReduceR, 15121054) != null) {
         mpchange = 0;
      }

      if (mpchange > 0 && applyfrom.getBuffedValue(SecondaryStatFlag.OverloadMode) != null) {
         mpchange = 0;
      }

      return mpchange;
   }

   public final int alchemistModifyVal(MapleCharacter chr, int val, boolean withX) {
      return !this.skill
            ? val * (100 + (withX ? chr.getStat().RecoveryUP : chr.getStat().BuffUP)) / 100
            : val
                  * (100 + (withX ? chr.getStat().RecoveryUP
                        : chr.getStat().BuffUP_Skill
                              + (this.getSummonMovementType() == null ? 0 : chr.getStat().BuffUP_Summon)))
                  / 100;
   }

   public final int calcPowerChange(MapleCharacter applyfrom) {
      int powerchange = 0;
      if (this.powerCon != 0 && GameConstants.isXenon(applyfrom.getJob())) {
         powerchange = this.powerCon;
      }

      return powerchange;
   }

   public final void setSourceId(int newid) {
      this.sourceid = newid;
   }

   public final boolean isGmBuff() {
      switch (this.sourceid) {
         case 9001000:
         case 9001001:
         case 9001002:
         case 9001003:
         case 9001005:
         case 9001008:
         case 9101000:
         case 9101001:
         case 9101002:
         case 9101003:
         case 9101005:
         case 9101008:
         case 10001075:
            return true;
         default:
            return GameConstants.isNovice(this.sourceid / 10000) && this.sourceid % 10000 == 1005;
      }
   }

   public final boolean isInflation() {
      return this.inflation > 0;
   }

   public final int getInflation() {
      return this.inflation;
   }

   public final boolean isMonsterBuff() {
      switch (this.sourceid) {
         case 1111007:
         case 1211009:
         case 1211013:
         case 1311007:
         case 2101003:
         case 2111004:
         case 2201003:
         case 2211004:
         case 2311005:
         case 4111003:
         case 4121004:
         case 4221004:
         case 4321002:
         case 4341003:
         case 5011002:
         case 12101001:
         case 12111002:
         case 14111001:
         case 22121000:
         case 22151001:
         case 22161002:
         case 25111206:
         case 27101101:
         case 32120013:
         case 32120014:
         case 35111005:
         case 90001002:
         case 90001003:
         case 90001004:
         case 90001005:
         case 90001006:
         case 101000101:
         case 101000102:
            return this.skill;
         default:
            return false;
      }
   }

   public final void setPartyBuff(boolean pb) {
      this.partyBuff = pb;
   }

   private boolean isPartyBuff() {
      Skill skill = SkillFactory.getSkill(this.sourceid);
      if (skill == null) {
         return false;
      } else {
         boolean isPartyBuff = false;
         if (!GameConstants.isNoviceSkill(skill.getId())
               || skill.getId() % 10000 != 1005 && skill.getId() % 10000 != 1215) {
            if (!this.isHeal() && !this.isResurrection() && !this.isGuardianSpirit()) {
               if (skill.getType() == 10 && skill.getAffected() != null) {
                  isPartyBuff = this.getLt() != null && this.getRb() != null;
               }

               if (this.sourceid == 400011003 || this.sourceid == 500061000) {
                  return false;
               } else {
                  if (!isPartyBuff) {
                     switch (this.sourceid) {
                        case 2321007:
                        case 2321055:
                        case 2341000:
                        case 2341001:
                        case 33101005:
                        case 400011011:
                           return true;
                     }
                  }

                  return isPartyBuff;
               }
            } else {
               return true;
            }
         } else {
            return true;
         }
      }
   }

   public final boolean isHeal() {
      return this.skill && (this.sourceid == 2301002 || this.sourceid == 9101000 || this.sourceid == 9001000);
   }

   public final boolean isGuardianSpirit() {
      return this.skill && this.sourceid == 1221016;
   }

   public final boolean isResurrection() {
      return this.skill && (this.sourceid == 9001005 || this.sourceid == 9101005 || this.sourceid == 2321006);
   }

   public final boolean isHeroEcho() {
      if (!this.skill) {
         return false;
      } else {
         switch (this.sourceid) {
            case 1005:
            case 10001005:
            case 10001215:
            case 20001005:
            case 20011005:
            case 20021005:
            case 20031005:
            case 20041005:
            case 20051005:
            case 30001005:
            case 30011005:
            case 30021005:
            case 50001005:
            case 50001215:
            case 60001005:
            case 60011005:
            case 60011215:
            case 60021005:
            case 60031005:
            case 100001005:
            case 130001005:
            case 130011005:
            case 140001005:
            case 150001005:
            case 150011005:
            case 150021005:
            case 150031005:
            case 160001005:
               return true;
            default:
               return false;
         }
      }
   }

   public final short getHp() {
      return this.hp;
   }

   public final short getMp() {
      return this.mp;
   }

   public final double getHpR() {
      return this.hpR;
   }

   public final short getHpRCon() {
      return this.hpRCon;
   }

   public final double getMpR() {
      return this.mpR;
   }

   public final short getMpRCon() {
      return this.mpRCon;
   }

   public final byte getMastery() {
      return this.mastery;
   }

   public final short getWatk() {
      return this.pad;
   }

   public final short getMatk() {
      return this.mad;
   }

   public final short getWdef() {
      return this.def;
   }

   public final short getMdef() {
      return this.mdef;
   }

   public final short getAcc() {
      return this.acc;
   }

   public final short getAvoid() {
      return this.avoid;
   }

   public final short getHands() {
      return this.hands;
   }

   public final short getSpeed() {
      return this.speed;
   }

   public final short getJump() {
      return this.jump;
   }

   public final short getPassiveSpeed() {
      return this.psdSpeed;
   }

   public final int getIndiePMdR() {
      return this.indiePMdR;
   }

   public final short getPassiveJump() {
      return this.psdJump;
   }

   public final int getDuration() {
      return this.duration;
   }

   public final int getDuration(int def, MapleCharacter player) {
      int duration = 0;
      if (def > 0) {
         duration = def;
      }

      if (duration < 2100000000) {
         duration = player.getBuffTime(this.sourceid, this.level, duration);
      }

      return duration;
   }

   public final int getSubTime() {
      return this.subTime;
   }

   public final boolean isOverTime() {
      return this.overTime;
   }

   public final Map<SecondaryStatFlag, Integer> getStatups() {
      return this.statups;
   }

   public final boolean sameSource(SecondaryStatEffect effect) {
      boolean sameSrc = this.sourceid == effect.sourceid;
      switch (this.sourceid) {
         case 32120013:
            sameSrc = effect.sourceid == 32001003;
            break;
         case 32120014:
            sameSrc = effect.sourceid == 32101003;
            break;
         case 32120015:
            sameSrc = effect.sourceid == 32111012;
            break;
         case 35120000:
            sameSrc = effect.sourceid == 35001002;
            break;
         case 35121013:
            sameSrc = effect.sourceid == 35111004;
      }

      return effect != null && sameSrc && this.skill == effect.skill;
   }

   public final int getCr() {
      return this.cr;
   }

   public final double getT() {
      return this.t;
   }

   public final int getU() {
      return this.u;
   }

   public final int getU2() {
      return this.u2;
   }

   public final int getV() {
      return this.v;
   }

   public final int getV2() {
      return this.v2;
   }

   public final void setV(int newvalue) {
      this.v = newvalue;
   }

   public final int getW() {
      return this.w;
   }

   public final int getW2() {
      return this.w2;
   }

   public final int getX() {
      return this.x;
   }

   public final int getY() {
      return this.y;
   }

   public final void setY(int newvalue) {
      this.y = newvalue;
   }

   public final int getZ() {
      return this.z;
   }

   public final int getForceCon() {
      return this.forceCon;
   }

   public final int getCostMpR() {
      return this.costmpR;
   }

   public final int getS() {
      return this.s;
   }

   public final int getS2() {
      return this.s2;
   }

   public final int getQ() {
      return this.q;
   }

   public final int getQ2() {
      return this.q2;
   }

   public final short getIndieCr() {
      return this.indieCr;
   }

   public final short getDamage() {
      return this.damage;
   }

   public final short getPVPDamage() {
      return this.PVPdamage;
   }

   public final short getAttackCount() {
      return this.attackCount;
   }

   public final byte getBulletCount() {
      return this.bulletCount;
   }

   public final int getBulletConsume() {
      return this.bulletConsume;
   }

   public final byte getMobCount() {
      return this.mobCount;
   }

   public final int getMoneyCon() {
      return this.moneyCon;
   }

   public final int getCooldown(MapleCharacter chra) {
      int ct = this.cooltime;
      if (this.sourceid == 400011001) {
         ct = this.x;
      }

      int cool;
      if (ct == 0) {
         cool = this.cooltimeMS;
      } else {
         cool = ct * 1000;
      }

      if (chra == null) {
         return cool;
      } else if (HexaMatrixConstants.isAllowCooldownSkill(chra != null ? chra.getJob() : 0, this.sourceid)
            || HexaMatrixConstants.searchCoreIdBySkill(this.sourceid) <= 0
                  && HexaMatrixConstants.searchCoreIdBySkill(GameConstants.getLinkedAranSkill(this.sourceid)) <= 0) {
         cool = chra.getCooltimeBySkillID(this.sourceid, cool);
         if ((this.sourceid == 400010030 || this.sourceid == 400011031)
               && chra.getBuffedValue(SecondaryStatFlag.InstallMaha) != null) {
            SecondaryStatEffect eff = chra.getSkillLevelData(400011031);
            if (eff != null) {
               cool = (int) (eff.getX() * 0.01 * cool);
            }
         }

         if (cool > 5000 && this.sourceid != 151121004 && this.sourceid != 164121042) {
            int reduce = (int) (cool * 0.01 * chra.getStat().s_reduceCooltimeR);
            cool = Math.max(5000, cool - reduce);
         }

         if (!SkillFactory.getSkill(this.sourceid).isNotCooltimeReduce() && !GameConstants.isGuildSkill(this.sourceid)
               || SkillFactory.getSkill(this.sourceid).isHyper()
                     && !SkillFactory.getSkill(this.sourceid).isNotCooltimeReduce()) {
            if (chra.getStat().coolTimeR > 0 && cool > 5000) {
               cool = Math.max(5000, cool - (int) (chra.getStat().coolTimeR * 0.01 * cool));
            }

            if (cool > 5000) {
               if (cool >= 10000) {
                  if (cool - chra.getStat().reduceCooltime * 1000 < 10000) {
                     int temp = cool - 10000;
                     int remain = chra.getStat().reduceCooltime * 1000 - temp;
                     cool -= temp;
                     cool = (int) (cool - cool * remain * 0.001 * 0.05);
                  } else {
                     cool = Math.max(5000, cool - chra.getStat().reduceCooltime * 1000);
                  }
               } else {
                  cool = (int) (cool - cool * chra.getStat().reduceCooltime * 0.05);
               }

               cool = Math.max(5000, cool);
            }
         }

         if (GameConstants.isLuminous(chra.getJob())) {
            Integer v = chra.getBuffedValue(SecondaryStatFlag.Larkness);
            if (v != null && v == 2 && (GameConstants.isLarknessMixSkill(this.sourceid) || this.sourceid == 27121201)) {
               cool = 0;
            }
         }

         if (this.sourceid == 11121052 && chra.getBuffedValue(SecondaryStatFlag.Ellision) != null) {
            cool = 0;
         }

         if ((!SkillFactory.getSkill(this.sourceid).isHyper() && !GameConstants.isGuildSkill(this.sourceid)
               || SkillFactory.getSkill(this.sourceid).isHyper()
                     && !GameConstants.isNoneCooltimeSkillReduce(this.sourceid))
               && !SkillFactory.getSkill(this.sourceid).isNotCooltimeReset()) {
            if (chra.getInnerSkillLevel(70000045) > 0 && GameConstants.isResettableCooltimeSkill(this.sourceid)) {
               SecondaryStatEffect effect = SkillFactory.getSkill(70000045)
                     .getEffect(chra.getInnerSkillLevel(70000045));
               if (effect != null) {
                  int nocoolProp = effect.getNocoolProp();
                  if (Randomizer.isSuccess(nocoolProp)) {
                     return 0;
                  }
               }
            }

            if (GameConstants.isKadena(chra.getJob()) && (this.sourceid == 64121011 || this.sourceid == 64121016)) {
               return 0;
            }

            SecondaryStat ss = chra.getSecondaryStat();
            if (ss != null && chra.hasBuffBySkillID(400001047) && ss.EmpressBlessRemainNoCool > 0) {
               SecondaryStatEffect effect = chra.getBuffedEffect(SecondaryStatFlag.EmpressBless);
               if (effect != null) {
                  if (GameConstants.isKadena(chra.getJob()) && this.sourceid == 64121003) {
                     if (!(Boolean) chra.getJobField("needleBatFirst")) {
                        return 0;
                     }

                     if ((Boolean) chra.getJobField("cooltimeBuff")) {
                        return 0;
                     }
                  }

                  if (effect.makeChanceResult()) {
                     ss.EmpressBlessRemainNoCool--;
                     if (GameConstants.isKadena(chra.getJob()) && this.sourceid == 64121003) {
                        chra.setJobField("cooltimeBuff", true);
                     }

                     return 0;
                  }
               }
            }
         }

         return cool;
      } else {
         return cool;
      }
   }

   public final int getCoolTime() {
      return this.cooltime * 1000;
   }

   public final int getNoDeathTime() {
      return this.ndTime;
   }

   public final int getCoolTimeR() {
      return this.coolTimeR;
   }

   public final Map<MobTemporaryStatFlag, Integer> getMonsterStati() {
      return this.monsterStatus;
   }

   public final boolean isHide() {
      return this.skill && (this.sourceid == 9001004 || this.sourceid == 9101004);
   }

   public final boolean isBeholder() {
      return this.skill && this.sourceid == 1301013;
   }

   public final boolean isMPRecovery() {
      return this.skill && this.sourceid == 5101005;
   }

   public final boolean isMonsterRiding_() {
      return this.skill
            && (this.sourceid == 1004
                  || this.sourceid == 10001004
                  || this.sourceid == 20001004
                  || this.sourceid == 20011004
                  || this.sourceid == 30001004 && this.sourceid >= 80001000 && this.sourceid <= 80001033
                  || this.sourceid == 80001037
                  || this.sourceid == 80001038
                  || this.sourceid == 80001039
                  || this.sourceid == 80001044
                  || this.sourceid >= 80001082 && this.sourceid <= 80001090
                  || this.sourceid == 30011159
                  || this.sourceid == 30011109
                  || this.sourceid == 33001001
                  || this.sourceid == 400031014
                  || this.sourceid == 22171080
                  || this.sourceid == 400031017
                  || this.sourceid == 500061050
                  || this.sourceid == 35001002
                  || this.sourceid == 35111003);
   }

   public final boolean isMonsterRiding() {
      return this.skill
            && (this.isMonsterRiding_()
                  || GameConstants.getLinkedMountItem(this.sourceid) != 0 && this.sourceid / 10000 == 8000
                        && this.sourceid != 80001000
                  || SkillFactory.getSkill(this.sourceid) != null && SkillFactory.getVehicleID(this.sourceid) > 0)
            && this.sourceid != 80003062;
   }

   public final boolean isMagicDoor() {
      return this.skill && (this.sourceid == 2311002 || this.sourceid % 10000 == 8001);
   }

   public final boolean isMechDoor() {
      return this.skill && this.sourceid == 35101005;
   }

   public final boolean isPoison() {
      return this.dot > 0 && this.dotTime > 0;
   }

   private final boolean isMist() {
      return this.skill
            && (this.sourceid == 4121015
                  || this.sourceid == 4221006
                  || this.sourceid == 12111005
                  || this.sourceid == 22161003
                  || this.sourceid == 32121006
                  || this.sourceid == 1076
                  || this.sourceid == 11076
                  || this.sourceid == 25111206
                  || this.sourceid == 12121005
                  || this.sourceid == 400010010
                  || this.sourceid == 400021002
                  || this.sourceid == 21121068
                  || this.sourceid == 400021041
                  || this.sourceid == 400021049
                  || this.sourceid == 400021050
                  || this.sourceid == 33111013
                  || this.sourceid == 33121016
                  || this.sourceid == 80001455);
   }

   private final boolean isSpiritClaw() {
      return this.skill && this.sourceid == 4111009 || this.sourceid == 5201008 || this.sourceid == 14111025;
   }

   private final boolean isDispel() {
      return this.skill && (this.sourceid == 2311001 || this.sourceid == 9001000 || this.sourceid == 9101000);
   }

   public final boolean isHeroWill() {
      switch (this.sourceid) {
         case 1121011:
         case 1221012:
         case 1321010:
         case 2121008:
         case 2221008:
         case 2321009:
         case 3121009:
         case 3211011:
         case 3221008:
         case 3321024:
         case 4121009:
         case 4221008:
         case 4341008:
         case 5121008:
         case 5221010:
         case 5321006:
         case 11121015:
         case 12121015:
         case 13121015:
         case 14121015:
         case 15121015:
         case 21121008:
         case 22171004:
         case 22171069:
         case 23121008:
         case 24121009:
         case 25121211:
         case 27121010:
         case 31121012:
         case 31221015:
         case 32121008:
         case 33121008:
         case 35121008:
         case 36121009:
         case 37121007:
         case 51121015:
         case 61121015:
         case 61121220:
         case 63121010:
         case 64121005:
         case 65121010:
         case 80001478:
         case 100001284:
         case 142121007:
         case 151121006:
         case 152121010:
         case 154121006:
         case 155121009:
         case 162121024:
         case 164121010:
         case 400001009:
            return this.skill;
         default:
            return false;
      }
   }

   public final boolean isPirateMorph() {
      switch (this.sourceid) {
         case 5111005:
         case 13111005:
         case 15111002:
            return this.skill;
         default:
            return false;
      }
   }

   public final boolean isMorph() {
      return this.morphId > 0;
   }

   public final int getMorph() {
      switch (this.sourceid) {
         case 5101007:
            return 1002;
         case 5111005:
         case 15111002:
            return 1000;
         case 13111005:
            return 1003;
         default:
            return this.morphId;
      }
   }

   public final int getMorph(MapleCharacter chr) {
      int morph = this.getMorph();
      switch (morph) {
         case 1000:
         case 1001:
         case 1003:
            return morph + (chr.getGender() == 1 ? 100 : 0);
         case 1002:
         default:
            return morph;
      }
   }

   public final byte getLevel() {
      return this.level;
   }

   public final SummonMoveAbility getSummonMovementType() {
      if (!this.skill) {
         return null;
      } else {
         switch (this.sourceid) {
            case 1301013:
            case 2121005:
            case 2211011:
            case 2221005:
            case 2321003:
            case 3111005:
            case 3211005:
            case 11001004:
            case 12000022:
            case 12001004:
            case 12100026:
            case 12110024:
            case 12111004:
            case 12120007:
            case 13001004:
            case 14001005:
            case 15001004:
            case 25121133:
            case 32001014:
            case 32100010:
            case 32110017:
            case 32120019:
            case 32141000:
            case 35111001:
            case 35111009:
            case 35111010:
            case 80001266:
            case 80001269:
            case 80001270:
            case 80001322:
            case 80001323:
            case 80001341:
            case 80001395:
            case 80001396:
            case 80001493:
            case 80001494:
            case 80001495:
            case 80001496:
            case 80001497:
            case 80001498:
            case 80001499:
            case 80001500:
            case 80001501:
            case 80001502:
            case 80001681:
            case 80001682:
            case 80001683:
            case 80001685:
            case 80001690:
            case 80001691:
            case 80001692:
            case 80001693:
            case 80001695:
            case 80001696:
            case 80001697:
            case 80001698:
            case 80001700:
            case 80001804:
            case 80001806:
            case 80001807:
            case 80001808:
            case 80001984:
            case 80001985:
            case 80002231:
            case 80002406:
            case 80002639:
            case 80002641:
            case 131001022:
            case 131002022:
            case 131003022:
            case 131004022:
            case 131005022:
            case 131006022:
            case 152001003:
            case 152121005:
            case 400001013:
            case 400001059:
            case 400001060:
            case 400011090:
            case 400021032:
            case 400021033:
            case 400021092:
            case 400041052:
            case 400051009:
            case 400051017:
            case 400051046:
               return SummonMoveAbility.FOLLOW;
            case 2111013:
            case 2211015:
            case 2311014:
            case 3111002:
            case 3111017:
            case 3120012:
            case 3211002:
            case 3211019:
            case 3220012:
            case 3221014:
            case 4111007:
            case 4211007:
            case 4341006:
            case 5211001:
            case 5211014:
            case 5220002:
            case 5220023:
            case 5220024:
            case 5220025:
            case 5221022:
            case 5221029:
            case 5321003:
            case 13111004:
            case 22171081:
            case 33101008:
            case 33111003:
            case 35101012:
            case 35111002:
            case 35111005:
            case 35111011:
            case 35121003:
            case 35121009:
            case 35121010:
            case 35121011:
            case 36121002:
            case 36121013:
            case 36121014:
            case 61111002:
            case 61111220:
            case 80002230:
            case 80002888:
            case 80002889:
            case 131001019:
            case 131001025:
            case 131001307:
            case 131003023:
            case 131004023:
            case 131005023:
            case 131006023:
            case 151100002:
            case 164121006:
            case 164121008:
            case 164121011:
            case 400001019:
            case 400001022:
            case 400001039:
            case 400001040:
            case 400001064:
            case 400011057:
            case 400011065:
            case 400021005:
            case 400021063:
            case 400021067:
            case 400021071:
            case 400021073:
            case 400041033:
            case 400041038:
            case 400041044:
            case 400041050:
            case 400051011:
            case 400051022:
               return SummonMoveAbility.STATIONARY;
            case 2311006:
            case 3101007:
            case 3121006:
            case 3201007:
            case 3221005:
            case 5211002:
            case 33111005:
            case 400001012:
               return SummonMoveAbility.CIRCLE_FOLLOW;
            case 3311009:
            case 14000027:
            case 14100027:
            case 14110029:
            case 14120008:
            case 23111009:
            case 23111010:
            case 23111011:
            case 131001026:
            case 131002026:
            case 131003026:
            case 152101008:
            case 152121006:
            case 164111007:
               return SummonMoveAbility.BIRD_FOLLOW;
            case 5201012:
            case 5210015:
               return SummonMoveAbility.FLAME_SUMMON;
            case 5201013:
            case 5201014:
            case 5210016:
            case 5210017:
            case 5210018:
            case 32111006:
               return SummonMoveAbility.WALK_STATIONARY;
            case 14111024:
            case 14121054:
            case 14121055:
            case 14121056:
            case 131001017:
            case 400011005:
               return SummonMoveAbility.SHADOW_SERVANT;
            case 33001007:
            case 33001008:
            case 33001009:
            case 33001010:
            case 33001011:
            case 33001012:
            case 33001013:
            case 33001014:
            case 33001015:
               return SummonMoveAbility.SUMMON_JAGUAR;
            case 131002015:
               return SummonMoveAbility.CIRCLE_STATIONARY;
            case 152101000:
            case 400021068:
            case 400021069:
            case 400041028:
            case 500061004:
            case 500061012:
               return SummonMoveAbility.SHADOW_SERVANT_EXTEND;
            case 400011006:
            case 400011012:
            case 400011013:
            case 400011014:
               return SummonMoveAbility.FIX_V_MOVE;
            case 400051068:
               return SummonMoveAbility.MECA_CARRIER;
            default:
               return this.isAngel() ? SummonMoveAbility.FOLLOW : null;
         }
      }
   }

   public void applyJaguarRiding(MapleCharacter player, int mountid) {
      if (player.getBuffedValue(SecondaryStatFlag.ProfessionalAgent) == null) {
         for (Summoned s : player.getSummons()) {
            if (s.getSkill() >= 33001007 && s.getSkill() <= 33001015) {
               player.getMap().broadcastMessage(CField.SummonPacket.removeSummon(s, true));
               player.getMap().removeMapObject(s);
               player.removeVisibleMapObject(s);
               List<Summoned> toRemove = new ArrayList<>();

               try {
                  for (Summoned summon : new ArrayList<>(player.getSummonsReadLock())) {
                     if (summon.getSkill() == s.getSkill()) {
                        toRemove.add(summon);
                     }
                  }
               } finally {
                  player.unlockSummonsReadLock();
               }

               toRemove.forEach(summonx -> player.removeSummon(summonx));
               toRemove.clear();
               toRemove = null;
               player.temporaryStatReset(SecondaryStatFlag.JaguarSummoned);
               break;
            }
         }

         if (player.getBuffedValue(SecondaryStatFlag.JaguarSummoned) != null) {
            player.temporaryStatReset(SecondaryStatFlag.JaguarSummoned);
         }
      }

      player.temporaryStatSet(33001001, Integer.MAX_VALUE, SecondaryStatFlag.RideVehicle, mountid);
   }

   public void tryGuardianSpirit(MapleCharacter chr) {
      Stream<MapleMapObject> stream = chr.getMap()
            .getMapObjectsInRect(this.calculateBoundingBox(chr.getPosition(), chr.isFacingLeft()),
                  Arrays.asList(MapleMapObjectType.PLAYER))
            .stream();
      Map<Integer, MapleCharacter> list = new HashMap<>();
      stream.forEach(
            c -> {
               MapleCharacter player = (MapleCharacter) c;
               int delta = (int) (Math.abs(player.getPosition().getX() - player.getPosition().getX())
                     + Math.abs(player.getPosition().getY() - player.getPosition().getY()));
               list.put(delta, player);
            });
      list.entrySet()
            .stream()
            .sorted(Comparator.comparingInt(Entry::getKey))
            .filter(c -> c.getValue().getId() != chr.getId())
            .filter(c -> !c.getValue().isAlive())
            .limit(1L)
            .forEach(c -> {
               c.getValue().healHP(c.getValue().getStat().getCurrentMaxHp(c.getValue()), true);
               c.getValue().temporaryStatSet(1221016, 10000, SecondaryStatFlag.NotDamaged, 1);
            });
      chr.temporaryStatSet(1221016, this.duration, SecondaryStatFlag.NotDamaged, 1);
   }

   public int runToPMembersInArea(MapleCharacter chr, Consumer<MapleCharacter> targetConsumer) {
      return this.runToPMembersInArea(chr, targetConsumer, false);
   }

   public int runToPMembersInArea(MapleCharacter chr, Consumer<MapleCharacter> targetConsumer, boolean dispel) {
      int count = 0;
      if (chr.getParty() != null && chr.getParty().getPartyMemberList().size() > 1) {
         for (PartyMemberEntry mpc : chr.getParty().getPartyMemberList()) {
            if (mpc != null && mpc.getId() != chr.getId() && mpc.getChannel() == chr.getClient().getChannel()
                  && mpc.getFieldID() == chr.getMapId()) {
               MapleCharacter targetChr = chr.getMap().getCharacterById(mpc.getId());
               if (targetChr != null && inRect(targetChr, chr, this.lt, this.rb)
                     && (targetChr.getDiseaseSize() != 0 || !dispel)) {
                  targetConsumer.accept(targetChr);
                  count++;
               }
            }
         }
      }

      return count;
   }

   public static boolean inRect(int objectX, int objectY, int baseX, int baseY, int ltX, int ltY, int rbX, int rbY) {
      return objectX >= baseX + ltX && objectX <= baseX + rbX && objectY >= baseY + ltY && objectY <= baseY + rbY;
   }

   public static boolean inRect(MapleMapObject target, MapleMapObject base, Point lt, Point rb) {
      return inRect(target.getPosition().x, target.getPosition().y, target.getPosition().x, target.getPosition().y,
            lt.x, lt.y, rb.x, rb.y);
   }

   public final boolean isAngel() {
      return GameConstants.isAngel(this.sourceid);
   }

   public final boolean isSkill() {
      return this.skill;
   }

   public final int getSourceId() {
      return this.sourceid;
   }

   public final boolean isSoaring() {
      return this.isSoaring_Normal() || this.isSoaring_Mount();
   }

   public final boolean isSoaring_Normal() {
      return this.skill && GameConstants.isNovice(this.sourceid / 10000) && this.sourceid % 10000 == 1026;
   }

   public final boolean isSoaring_Mount() {
      return this.skill && (GameConstants.isNovice(this.sourceid / 10000) && this.sourceid % 10000 == 1142
            || this.sourceid == 80001089);
   }

   public final boolean isMistEruption() {
      switch (this.sourceid) {
         case 2121003:
            return this.skill;
         default:
            return false;
      }
   }

   public final boolean isShadow() {
      switch (this.sourceid) {
         case 4111002:
         case 4211008:
         case 4331002:
         case 14111054:
            return this.skill;
         default:
            return false;
      }
   }

   public final boolean isMechPassive() {
      switch (this.sourceid) {
         case 35121013:
            return true;
         default:
            return false;
      }
   }

   public final boolean makeChanceResult() {
      return this.prop >= 100 || Randomizer.nextInt(100) < this.prop;
   }

   public final boolean makeChanceWithBonusResult(int bonus) {
      return this.prop + bonus >= 100 || ThreadLocalRandom.current().nextInt(100) < this.prop + bonus;
   }

   public final short getProb() {
      return this.prop;
   }

   public final short getIgnoreMob() {
      return this.ignoreMobpdpR;
   }

   public final int getIndieSpeed() {
      return this.indieSpeed;
   }

   public final int getIndieStance() {
      return this.indieStance;
   }

   public final int getEnhancedHP() {
      return this.emhp;
   }

   public final int getEnhancedMP() {
      return this.emmp;
   }

   public final int getEnhancedWatk() {
      return this.epad;
   }

   public final int getEnhancedMatk() {
      return this.emad;
   }

   public final int getEnhancedWdef() {
      return this.epdd;
   }

   public final int getIndiePadR() {
      return this.indiePadR;
   }

   public final int getIndieMadR() {
      return this.indieMadR;
   }

   public final int getEnhancedMdef() {
      return this.emdd;
   }

   public final short getDOT() {
      return this.dot;
   }

   public final short getDOTTime() {
      return this.dotTime;
   }

   public final short getDOTInterval() {
      return this.dotInterval;
   }

   public final short getCriticalMax() {
      return this.criticaldamageMax;
   }

   public final short getCriticalMin() {
      return this.criticaldamageMin;
   }

   public final short getASRRate() {
      return this.asrR;
   }

   public final short getTERRate() {
      return this.terR;
   }

   public final int getDAMRate() {
      return this.damR;
   }

   public final int getDamR_6th() {
      return this.damR_6th;
   }

   public final short getMesoRate() {
      return this.mesoR;
   }

   public final short getDropRate() {
      return this.dropR;
   }

   public final int getEXP() {
      return this.exp;
   }

   public final short getAttackX() {
      return this.padX;
   }

   public final short getMagicX() {
      return this.madX;
   }

   public final int getPercentHP() {
      return this.mhpR;
   }

   public final int getPercentMP() {
      return this.mmpR;
   }

   public final int getConsume() {
      return this.consumeOnPickup;
   }

   public final int getSelfDestruction() {
      return this.selfDestruction;
   }

   public final int getCharColor() {
      return this.charColor;
   }

   public final int getSpeedMax() {
      return this.speedMax;
   }

   public final int getAccX() {
      return this.accX;
   }

   public final int getMaxHpX() {
      return this.mhpX;
   }

   public final int getMaxMpX() {
      return this.mmpX;
   }

   public final List<Integer> getPetsCanConsume() {
      return this.petsCanConsume;
   }

   public final boolean isReturnScroll() {
      return this.skill && (this.sourceid == 80001040 || this.sourceid == 20021110);
   }

   public final boolean isMechChange() {
      switch (this.sourceid) {
         case 35001001:
         case 35101009:
         case 35111004:
         case 35121005:
         case 35121013:
         case 35121054:
            return this.skill;
         default:
            return false;
      }
   }

   public final int getRange() {
      return this.range;
   }

   public final short getER() {
      return this.er;
   }

   public final int getPrice() {
      return this.price;
   }

   public final int getExtendPrice() {
      return this.extendPrice;
   }

   public final byte getPeriod() {
      return this.period;
   }

   public final byte getReqGuildLevel() {
      return this.reqGuildLevel;
   }

   public final byte getEXPRate() {
      return this.expR;
   }

   public final short getLifeID() {
      return this.lifeId;
   }

   public final short getUseLevel() {
      return this.useLevel;
   }

   public final byte getSlotCount() {
      return this.slotCount;
   }

   public final short getStr() {
      return this.str;
   }

   public final short getStrX() {
      return this.strX;
   }

   public final short getDex() {
      return this.dex;
   }

   public final short getDexX() {
      return this.dexX;
   }

   public final short getInt() {
      return this.int_;
   }

   public final short getIntX() {
      return this.intX;
   }

   public final short getLuk() {
      return this.luk;
   }

   public final short getLukX() {
      return this.lukX;
   }

   public final short getComboConAran() {
      return this.comboConAran;
   }

   public final short getMPCon() {
      return this.mpCon;
   }

   public final short getMPConReduce() {
      return this.mpConReduce;
   }

   public final int getSoulMPCon() {
      return this.soulmpCon;
   }

   public final short getIndieMHp() {
      return this.indieMhp;
   }

   public final short getIndieMMp() {
      return this.indieMmp;
   }

   public final short getIndieBdR() {
      return this.indieBDR;
   }

   public final short getIndieAllStat() {
      return this.indieAllStat;
   }

   public final short getIndiePad() {
      return this.indiePad;
   }

   public final short getIndiePddR() {
      return this.indiePddR;
   }

   public final short getIndieMad() {
      return this.indieMad;
   }

   public final short getIndieDamR() {
      return this.indieDamR;
   }

   public final short getIndieDamReduceR() {
      return this.indieDamReduceR;
   }

   public final byte getType() {
      return this.type;
   }

   public int getBossDamage() {
      return this.bdR;
   }

   public int getInterval() {
      return this.interval;
   }

   public ArrayList<Pair<Integer, Integer>> getAvailableMaps() {
      return this.availableMap;
   }

   public short getWDEFRate() {
      return this.pddR;
   }

   public short getMDEFRate() {
      return this.mddR;
   }

   public short getOnActive() {
      return this.onActive;
   }

   public int getWeapon() {
      return this.weapon;
   }

   public final boolean isUnstealable() {
      for (SecondaryStatFlag b : this.statups.keySet()) {
         if (b == SecondaryStatFlag.BasicStatUp) {
            return true;
         }
      }

      return this.sourceid == 4221013;
   }

   public final boolean isAddedInt() {
      switch (this.sourceid) {
         case 2321005:
         case 4341007:
         case 13111023:
         case 13120008:
         case 80001428:
            return true;
         default:
            return false;
      }
   }

   public final boolean isAddedLong() {
      switch (this.sourceid) {
         case 2321054:
            return true;
         default:
            return false;
      }
   }

   public boolean isDoubleDice() {
      switch (this.sourceid) {
         case 5120012:
         case 5220014:
         case 5320007:
         case 35120014:
            return true;
         default:
            return false;
      }
   }

   public void cancelAnotherAuraSkill(MapleCharacter applyfrom) {
      if (applyfrom.getBuffedValue(SecondaryStatFlag.YellowAura) != null) {
         applyfrom.temporaryStatReset(SecondaryStatFlag.YellowAura);
      }

      if (applyfrom.getBuffedValue(SecondaryStatFlag.DrainAura) != null) {
         applyfrom.temporaryStatReset(SecondaryStatFlag.DrainAura);
      }

      if (applyfrom.getBuffedValue(SecondaryStatFlag.BlueAura) != null) {
         applyfrom.temporaryStatReset(SecondaryStatFlag.BlueAura);
      }

      if (applyfrom.getBuffedValue(SecondaryStatFlag.DarkAura) != null) {
         applyfrom.temporaryStatReset(SecondaryStatFlag.DarkAura);
      }

      if (applyfrom.getBuffedValue(SecondaryStatFlag.DebuffAura) != null) {
         applyfrom.temporaryStatReset(SecondaryStatFlag.DebuffAura);
      }

      if (applyfrom.getBuffedValue(SecondaryStatFlag.UnionAuraBlow) != null) {
         applyfrom.temporaryStatReset(SecondaryStatFlag.UnionAuraBlow);
      }
   }

   public int getPPCon() {
      return this.ppCon;
   }

   public int getPPRecovery() {
      return this.ppRecovery;
   }

   public short getLv2mhp() {
      return this.lv2mhp;
   }

   public short getLv2mmp() {
      return this.lv2mmp;
   }

   public short getTargetPlus_5th() {
      return this.targetPlus_5th;
   }

   public short getTargetPlus() {
      return this.targetPlus;
   }

   public short getDamAbsorbShieldR() {
      return this.damAbsorbShieldR;
   }

   public void setDamAbsorbShieldR(short damAbsorbShieldR) {
      this.damAbsorbShieldR = damAbsorbShieldR;
   }

   public int getSubProp() {
      return this.subProp;
   }

   public int getTime() {
      return this.time;
   }

   public int getExpRPerM() {
      return this.expRPerM;
   }

   public Point getLt() {
      return this.lt;
   }

   public Point getLt2() {
      return this.lt2;
   }

   public Point getRb() {
      return this.rb;
   }

   public Point getRb2() {
      return this.rb2;
   }

   public Point getLt3() {
      return this.lt3;
   }

   public Point getRb3() {
      return this.rb3;
   }

   public int getBufftimeR() {
      return this.bufftimeR;
   }

   public short getIndieExp() {
      return this.indieExp;
   }

   public void setIndieExp(short indieExp) {
      this.indieExp = indieExp;
   }

   public short getHpFX() {
      return this.hpFX;
   }

   public void setHpFX(short hpFX) {
      this.hpFX = hpFX;
   }

   public short getMpFX() {
      return this.mpFX;
   }

   public void setMpFX(short mpFX) {
      this.mpFX = mpFX;
   }

   public short getStrFX() {
      return this.strFX;
   }

   public void setStrFX(short strFX) {
      this.strFX = strFX;
   }

   public short getDexFX() {
      return this.dexFX;
   }

   public void setDexFX(short dexFX) {
      this.dexFX = dexFX;
   }

   public short getIntFX() {
      return this.intFX;
   }

   public void setIntFX(short intFX) {
      this.intFX = intFX;
   }

   public short getLukFX() {
      return this.lukFX;
   }

   public void setLukFX(short lukFX) {
      this.lukFX = lukFX;
   }

   public int getGauge() {
      return this.gauge;
   }

   public int getIndieMhpR() {
      return this.indieMhpR;
   }

   public int getIndieMmpR() {
      return this.indieMmpR;
   }

   public int getIndieBooster() {
      return this.indieBooster;
   }

   public int getIndieJump() {
      return this.indieJump;
   }

   public int getKillRecoveryR() {
      return this.killRecoveryR;
   }

   public int getIndieAsrR() {
      return this.indieAsrR;
   }

   public int getIgnoreMobDamR() {
      return this.ignoreMobDamR;
   }

   public int getEvaR() {
      return this.evaR;
   }

   public short getDotSuperpos() {
      return this.dotSuperpos;
   }

   public void setDotSuperpos(short dotSuperpos) {
      this.dotSuperpos = dotSuperpos;
   }

   public short getDotTickDamR() {
      return this.dotTickDamR;
   }

   public void setDotTickDamR(short dotTickDamR) {
      this.dotTickDamR = dotTickDamR;
   }

   public short getMaxDotTickDamR() {
      return this.maxDotTickDamR;
   }

   public void setMaxDotTickDamR(short maxDotTickDamR) {
      this.maxDotTickDamR = maxDotTickDamR;
   }

   public short getIndieStatRBasic() {
      return this.indieStatRBasic;
   }

   public short getIndieCooltimeReduce() {
      return this.indieCooltimeReduce;
   }

   public int getUpdatableTime() {
      return this.updatableTime;
   }

   public int getFixCoolTime() {
      return this.fixCoolTime;
   }

   public int getAtGauge1Con() {
      return this.atGauge1Con;
   }

   public void setAtGauge1Con(int atGauge1Con) {
      this.atGauge1Con = atGauge1Con;
   }

   public int getAtGauge2Inc() {
      return this.atGauge2Inc;
   }

   public void setAtGauge2Inc(int atGauge2Inc) {
      this.atGauge2Inc = atGauge2Inc;
   }

   public int getAtGauge2Con() {
      return this.atGauge2Con;
   }

   public int getIndieIgnoreMobPdpR() {
      return this.indieIgnoreMobpdpR;
   }

   public int getMDF() {
      return this.MDF;
   }

   public short getCancelableTime() {
      return this.cancelableTime;
   }

   public void setCancelableTime(short cancelableTime) {
      this.cancelableTime = cancelableTime;
   }

   public short getSummonTimeR() {
      return this.summonTimeR;
   }

   public void setSummonTimeR(short summonTimeR) {
      this.summonTimeR = summonTimeR;
   }

   public int getNocoolProp() {
      return this.nocoolProp;
   }

   public void setNocoolProp(int nocoolProp) {
      this.nocoolProp = nocoolProp;
   }

   public int getPassivePlus() {
      return this.passivePlus;
   }

   public void setPassivePlus(int passivePlus) {
      this.passivePlus = passivePlus;
   }

   public short getMPP() {
      return this.MPP;
   }

   public void setMPP(short MPP) {
      this.MPP = MPP;
   }

   public int getMesoG() {
      return this.mesoG;
   }

   public void setMesoG(int mesoG) {
      this.mesoG = mesoG;
   }

   public int getDisCountR() {
      return this.disCountR;
   }

   public void setDisCountR(int disCountR) {
      this.disCountR = disCountR;
   }

   public int getItemUpgradeBonusR() {
      return this.itemUpgradeBonusR;
   }

   public void setItemUpgradeBonusR(int itemUpgradeBonusR) {
      this.itemUpgradeBonusR = itemUpgradeBonusR;
   }

   public int getItemCursedProtectR() {
      return this.itemCursedProtectR;
   }

   public void setItemCursedProtectR(int itemCursedProtectR) {
      this.itemCursedProtectR = itemCursedProtectR;
   }

   public int getItemTUCProtectR() {
      return this.itemTUCProtectR;
   }

   public void setItemTUCProtectR(int itemTUCProtectR) {
      this.itemTUCProtectR = itemTUCProtectR;
   }

   public int getExpGuild() {
      return this.expGuild;
   }

   public void setExpGuild(int expGuild) {
      this.expGuild = expGuild;
   }

   public int getCooltimeMS() {
      return this.cooltimeMS;
   }

   public int getMonsterCollectionProp() {
      return this.incMobCollectionProp;
   }

   @Override
   public String toString() {
      return "SecondaryStatEffect{mastery="
            + this.mastery
            + ", mobCount="
            + this.mobCount
            + ", bulletCount="
            + this.bulletCount
            + ", reqGuildLevel="
            + this.reqGuildLevel
            + ", period="
            + this.period
            + ", expR="
            + this.expR
            + ", iceGageCon="
            + this.iceGageCon
            + ", recipeUseCount="
            + this.recipeUseCount
            + ", recipeValidDay="
            + this.recipeValidDay
            + ", reqSkillLevel="
            + this.reqSkillLevel
            + ", slotCount="
            + this.slotCount
            + ", effectedOnAlly="
            + this.effectedOnAlly
            + ", effectedOnEnemy="
            + this.effectedOnEnemy
            + ", type="
            + this.type
            + ", preventslip="
            + this.preventslip
            + ", immortal="
            + this.immortal
            + ", bs="
            + this.bs
            + ", powerCon="
            + this.powerCon
            + ", hp="
            + this.hp
            + ", mp="
            + this.mp
            + ", pad="
            + this.pad
            + ", mad="
            + this.mad
            + ", def="
            + this.def
            + ", mdef="
            + this.mdef
            + ", acc="
            + this.acc
            + ", avoid="
            + this.avoid
            + ", hands="
            + this.hands
            + ", speed="
            + this.speed
            + ", jump="
            + this.jump
            + ", psdSpeed="
            + this.psdSpeed
            + ", psdJump="
            + this.psdJump
            + ", mpCon="
            + this.mpCon
            + ", hpCon="
            + this.hpCon
            + ", forceCon="
            + this.forceCon
            + ", reduceForceR="
            + this.reduceForceR
            + ", comboConAran="
            + this.comboConAran
            + ", bdR="
            + this.bdR
            + ", damage="
            + this.damage
            + ", prop="
            + this.prop
            + ", subProp="
            + this.subProp
            + ", emhp="
            + this.emhp
            + ", emmp="
            + this.emmp
            + ", epad="
            + this.epad
            + ", emad="
            + this.emad
            + ", epdd="
            + this.epdd
            + ", emdd="
            + this.emdd
            + ", ignoreMobpdpR="
            + this.ignoreMobpdpR
            + ", dot="
            + this.dot
            + ", dotTime="
            + this.dotTime
            + ", dotInterval="
            + this.dotInterval
            + ", dotSuperpos="
            + this.dotSuperpos
            + ", dotTickDamR="
            + this.dotTickDamR
            + ", maxDotTickDamR="
            + this.maxDotTickDamR
            + ", criticaldamageMin="
            + this.criticaldamageMin
            + ", criticaldamageMax="
            + this.criticaldamageMax
            + ", pddX="
            + this.pddX
            + ", mddX="
            + this.mddX
            + ", pddR="
            + this.pddR
            + ", mddR="
            + this.mddR
            + ", asrR="
            + this.asrR
            + ", terR="
            + this.terR
            + ", er="
            + this.er
            + ", padX="
            + this.padX
            + ", madX="
            + this.madX
            + ", mesoR="
            + this.mesoR
            + ", dropR="
            + this.dropR
            + ", thaw="
            + this.thaw
            + ", selfDestruction="
            + this.selfDestruction
            + ", PVPdamage="
            + this.PVPdamage
            + ", indiePad="
            + this.indiePad
            + ", indiePadR="
            + this.indiePadR
            + ", indieMad="
            + this.indieMad
            + ", indiePMd="
            + this.indiePMd
            + ", fatigueChange="
            + this.fatigueChange
            + ", onActive="
            + this.onActive
            + ", indieDEX="
            + this.indieDEX
            + ", str="
            + this.str
            + ", dex="
            + this.dex
            + ", int_="
            + this.int_
            + ", luk="
            + this.luk
            + ", strX="
            + this.strX
            + ", dexX="
            + this.dexX
            + ", intX="
            + this.intX
            + ", lukX="
            + this.lukX
            + ", hpFX="
            + this.hpFX
            + ", mpFX="
            + this.mpFX
            + ", strFX="
            + this.strFX
            + ", dexFX="
            + this.dexFX
            + ", intFX="
            + this.intFX
            + ", lukFX="
            + this.lukFX
            + ", lifeId="
            + this.lifeId
            + ", imhp="
            + this.imhp
            + ", immp="
            + this.immp
            + ", inflation="
            + this.inflation
            + ", useLevel="
            + this.useLevel
            + ", mpConReduce="
            + this.mpConReduce
            + ", soulmpCon="
            + this.soulmpCon
            + ", mhpR="
            + this.mhpR
            + ", mmpR="
            + this.mmpR
            + ", bufftimeR="
            + this.bufftimeR
            + ", evaR="
            + this.evaR
            + ", indieCr="
            + this.indieCr
            + ", indieCD="
            + this.indieCD
            + ", indieMhp="
            + this.indieMhp
            + ", indieMmp="
            + this.indieMmp
            + ", indieStance="
            + this.indieStance
            + ", indieAllStat="
            + this.indieAllStat
            + ", indieSpeed="
            + this.indieSpeed
            + ", indieJump="
            + this.indieJump
            + ", indieBooster="
            + this.indieBooster
            + ", indieAcc="
            + this.indieAcc
            + ", indieEva="
            + this.indieEva
            + ", indieEvaR="
            + this.indieEvaR
            + ", indiePdd="
            + this.indiePdd
            + ", indiePddR="
            + this.indiePddR
            + ", indieMdd="
            + this.indieMdd
            + ", incPVPdamage="
            + this.incPVPdamage
            + ", indieExp="
            + this.indieExp
            + ", indieMhpR="
            + this.indieMhpR
            + ", indieMmpR="
            + this.indieMmpR
            + ", indieAsrR="
            + this.indieAsrR
            + ", indieTerR="
            + this.indieTerR
            + ", indieDamR="
            + this.indieDamR
            + ", indieBDR="
            + this.indieBDR
            + ", indieIgnoreMobpdpR="
            + this.indieIgnoreMobpdpR
            + ", indieMaxDamageOver="
            + this.indieMaxDamageOver
            + ", cancelableTime="
            + this.cancelableTime
            + ", mobSkill="
            + this.mobSkill
            + ", mobSkillLevel="
            + this.mobSkillLevel
            + ", indiePMdR="
            + this.indiePMdR
            + ", morph="
            + this.morph
            + ", MDF="
            + this.MDF
            + ", MPP="
            + this.MPP
            + ", lv2mhp="
            + this.lv2mhp
            + ", lv2mmp="
            + this.lv2mmp
            + ", costmpR="
            + this.costmpR
            + ", summonTimeR="
            + this.summonTimeR
            + ", attackCount="
            + this.attackCount
            + ", indieDamReduceR="
            + this.indieDamReduceR
            + ", fixCoolTime="
            + this.fixCoolTime
            + ", mesoAmountUp="
            + this.mesoAmountUp
            + ", indieCooltimeReduce="
            + this.indieCooltimeReduce
            + ", indieStatRBasic="
            + this.indieStatRBasic
            + ", dotHealHPPerSecondR="
            + this.dotHealHPPerSecondR
            + ", dotHealMPPerSecondR="
            + this.dotHealMPPerSecondR
            + ", hpRCon="
            + this.hpRCon
            + ", mpRCon="
            + this.mpRCon
            + ", targetPlus="
            + this.targetPlus
            + ", targetPlus_5th="
            + this.targetPlus_5th
            + ", damAbsorbShieldR="
            + this.damAbsorbShieldR
            + ", expRPerM="
            + this.expRPerM
            + ", killRecoveryR="
            + this.killRecoveryR
            + ", ignoreMobDamR="
            + this.ignoreMobDamR
            + ", hpR="
            + this.hpR
            + ", mpR="
            + this.mpR
            + ", t="
            + this.t
            + ", traits="
            + this.traits
            + ", nocoolProp="
            + this.nocoolProp
            + ", stanceProp="
            + this.stanceProp
            + ", duration="
            + this.duration
            + ", time="
            + this.time
            + ", subTime="
            + this.subTime
            + ", ppCon="
            + this.ppCon
            + ", ppRecovery="
            + this.ppRecovery
            + ", sourceid="
            + this.sourceid
            + ", recipe="
            + this.recipe
            + ", moveTo="
            + this.moveTo
            + ", u="
            + this.u
            + ", u2="
            + this.u2
            + ", v="
            + this.v
            + ", v2="
            + this.v2
            + ", w="
            + this.w
            + ", w2="
            + this.w2
            + ", x="
            + this.x
            + ", y="
            + this.y
            + ", z="
            + this.z
            + ", s="
            + this.s
            + ", s2="
            + this.s2
            + ", q="
            + this.q
            + ", q2="
            + this.q2
            + ", cr="
            + this.cr
            + ", itemCon="
            + this.itemCon
            + ", itemConNo="
            + this.itemConNo
            + ", updatableTime="
            + this.updatableTime
            + ", bulletConsume="
            + this.bulletConsume
            + ", moneyCon="
            + this.moneyCon
            + ", damR="
            + this.damR
            + ", speedMax="
            + this.speedMax
            + ", accX="
            + this.accX
            + ", mhpX="
            + this.mhpX
            + ", mmpX="
            + this.mmpX
            + ", coolTimeR="
            + this.coolTimeR
            + ", cooltime="
            + this.cooltime
            + ", cooltimeMS="
            + this.cooltimeMS
            + ", morphId="
            + this.morphId
            + ", expinc="
            + this.expinc
            + ", exp="
            + this.exp
            + ", consumeOnPickup="
            + this.consumeOnPickup
            + ", range="
            + this.range
            + ", price="
            + this.price
            + ", extendPrice="
            + this.extendPrice
            + ", charColor="
            + this.charColor
            + ", interval="
            + this.interval
            + ", rewardMeso="
            + this.rewardMeso
            + ", totalprob="
            + this.totalprob
            + ", cosmetic="
            + this.cosmetic
            + ", gauge="
            + this.gauge
            + ", passivePlus="
            + this.passivePlus
            + ", mesoG="
            + this.mesoG
            + ", disCountR="
            + this.disCountR
            + ", itemUpgradeBonusR="
            + this.itemUpgradeBonusR
            + ", itemCursedProtectR="
            + this.itemCursedProtectR
            + ", itemTUCProtectR="
            + this.itemTUCProtectR
            + ", expGuild="
            + this.expGuild
            + ", overTime="
            + this.overTime
            + ", skill="
            + this.skill
            + ", partyBuff="
            + this.partyBuff
            + ", statups="
            + this.statups
            + ", availableMap="
            + this.availableMap
            + ", monsterStatus="
            + this.monsterStatus
            + ", lt="
            + this.lt
            + ", lt2="
            + this.lt2
            + ", rb="
            + this.rb
            + ", rb2="
            + this.rb2
            + ", lt3="
            + this.lt3
            + ", rb3="
            + this.rb3
            + ", expBuff="
            + this.expBuff
            + ", itemup="
            + this.itemup
            + ", mesoup="
            + this.mesoup
            + ", cashup="
            + this.cashup
            + ", berserk="
            + this.berserk
            + ", illusion="
            + this.illusion
            + ", berserk2="
            + this.berserk2
            + ", cp="
            + this.cp
            + ", nuffSkill="
            + this.nuffSkill
            + ", weapon="
            + this.weapon
            + ", atGauge1Con="
            + this.atGauge1Con
            + ", atGauge2Inc="
            + this.atGauge2Inc
            + ", atGauge2Con="
            + this.atGauge2Con
            + ", level="
            + this.level
            + ", cureDebuffs="
            + this.cureDebuffs
            + ", petsCanConsume="
            + this.petsCanConsume
            + ", randomPickup="
            + this.randomPickup
            + ", mapList="
            + this.mapList
            + ", rewardItem="
            + this.rewardItem
            + ", incMobCollectionProp="
            + this.incMobCollectionProp
            + "}";
   }
}
