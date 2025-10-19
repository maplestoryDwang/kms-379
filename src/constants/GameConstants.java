package constants;

import database.DBConfig;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import objects.fields.MapleMapObjectType;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.instance.HellBlackHeavenBoss;
import objects.fields.fieldset.instance.HellDemianBoss;
import objects.fields.fieldset.instance.HellDunkelBoss;
import objects.fields.fieldset.instance.HellLucidBoss;
import objects.fields.fieldset.instance.HellWillBoss;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.item.CustomItem;
import objects.item.Equip;
import objects.item.IntensePowerCrystalData;
import objects.item.Item;
import objects.item.ItemInformation;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.item.MapleWeaponType;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.users.MapleCharacter;
import objects.users.PlayerStats;
import objects.users.enchant.EquipSpecialAttribute;
import objects.users.enchant.ExItemType;
import objects.users.enchant.GradeRandomOption;
import objects.users.enchant.ItemOption;
import objects.users.enchant.ItemOptionInfo;
import objects.users.jobs.zero.EgoEquipUpgradeCost;
import objects.users.skills.HoyoungAttributes;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.FileoutputUtil;
import objects.utils.Pair;
import objects.utils.Properties;
import objects.utils.Randomizer;
import objects.utils.Table;

public class GameConstants {
   public static int FairyQuestEx = 27039;
   public static long[] tangyoonExp = new long[]{
      740400000L,
      760500000L,
      780800000L,
      803500000L,
      824200000L,
      845000000L,
      866100000L,
      889500000L,
      910900000L,
      932500000L,
      1860100000L,
      1902600000L,
      1950400000L,
      1993600000L,
      2037200000L,
      2086100000L,
      2134000000L,
      2180100000L,
      2224900000L,
      2275500000L,
      2321100000L,
      2372400000L,
      2418600000L,
      2470700000L,
      2517600000L,
      2570400000L,
      2623800000L,
      2671700000L,
      2725800000L,
      2780200000L,
      2950300000L,
      3008000000L,
      3066000000L,
      3117800000L,
      3176600000L,
      3236000000L,
      3295900000L,
      3348800000L,
      3409300000L,
      3470100000L,
      3531200000L,
      3642200000L,
      3705100000L,
      3761000000L,
      3824800000L,
      3888900000L,
      3953300000L,
      4018200000L,
      4083500000L,
      4149200000L,
      4386100000L,
      4455300000L,
      4524900000L,
      4594900000L,
      4665400000L,
      4736000000L,
      4807300000L,
      4878800000L,
      4959500000L,
      5032100000L,
      12941000000L,
      13127200000L,
      13313700000L,
      13501600000L,
      13713100000L,
      15427400000L,
      15639200000L,
      15851800000L,
      16092000000L,
      16307100000L,
      17057600000L,
      17866900000L,
      18099500000L,
      19096200000L,
      19371800000L,
      21772700000L,
      22047200000L,
      22357600000L,
      22671000000L,
      23102000000L,
      25770300000L,
      26121900000L,
      26435800000L,
      26754500000L,
      27015400000L,
      29345000000L,
      29625400000L,
      29923100000L,
      30220800000L,
      30518500000L,
      34150300000L,
      34451200000L,
      34752100000L,
      35053000000L,
      35353900000L,
      38654800000L,
      38912500000L,
      39170200000L,
      39427900000L,
      39685600000L
   };
   public static List<CustomItem> customItems = new ArrayList<>();
   public static List<Pair<String, String>> musicList = new ArrayList<>();
   private static Map<Integer, Boolean> isExceptionalEquip = new HashMap<>();
   public static List<Integer> ignoreOpcode = new ArrayList<>();
   public static Map<String, Integer> royalSimpleOpenNpc = new HashMap<>();
   public static final Map<Integer, Integer> royalDamageTearMap = new HashMap<>();
   public static final Map<Integer, Integer> royalBossDamageTearMap = new HashMap<>();
   public static final List<Integer> FatigueMapList;
   public static final List<MapleMapObjectType> rangedMapobjectTypes;
   private static final int[] closeness;
   private static final int[] setScore;
   private static final int[] cumulativeTraitExp;
   private static final int[] pvpExp;
   private static final int[] guildexp;
   private static final int[] mountexp;
   public static final int[] itemBlock;
   public static final int[] cashBlock;
   public static final int JAIL = 180000002;
   public static final int MAX_BUFFSTAT = 31;
   public static final int MAX_MOB_BUFFSTAT = 4;
   public static final int[] blockedSkills;
   public static final String[] RESERVED;
   public static final String[] stats;
   public static final int[] hyperTele;
   public static final int[] rankC;
   public static final int[] rankB;
   public static final int[] rankA;
   public static final int[] rankS;
   public static final long[] exp;
   public static final int[] goldrewards;
   public static final int[] silverrewards;
   public static final int[] peanuts;
   public static int[] eventCommonReward;
   public static int[] theSeedBoxReward;
   public static int[] eventUncommonReward;
   public static int[] eventRareReward;
   public static int[] eventSuperReward;
   public static int[] tenPercent;
   public static int[] fishingReward;
   public static int[] randomReward;
   public static int[] Equipments_Bonus;
   public static int[] Equipments_ReduceCool;
   public static int[] blockedMaps;
   public static final int[] normalDrops;
   public static final int[] rareDrops;
   public static final int[] superDrops;
   public static int[] owlItems;
   public static int[] royalstyle;
   public static int[] masterpiece;
   public static int[] soulItemid;
   public static short[] soulPotentials;
   private static int[] dmgskinitem;
   private static int[] dmgskinnum;
   public static boolean isOpen;
   public static final int OMOK_SCORE = 122200;
   public static final int MATCH_SCORE = 122210;
   public static final int HP_ITEM = 122221;
   public static final int MP_ITEM = 122223;
   public static final int JAIL_TIME = 123455;
   public static final int JAIL_QUEST = 123456;
   public static final int REPORT_QUEST = 123457;
   public static final int ULT_EXPLORER = 111111;
   public static final int ENERGY_DRINK = 122500;
   public static final int HARVEST_TIME = 122501;
   public static final int PENDANT_SLOT = 122700;
   public static final int BOSS_PQ = 150001;
   public static final int JAGUAR = 111112;
   public static final int DOJO = 150100;
   public static final int DOJO_RECORD = 150101;
   public static final int PARTY_REQUEST = 122900;
   public static final int PARTY_INVITE = 122901;
   public static final int BUDDY_ADD = 122902;
   public static final int ITEM_TITLE = 124000;
   public static final int RUNE = 222222;
   public static final boolean GMS = false;
   private static int[] weaponTypes;
   public static Map<Integer, IntensePowerCrystalData> intensePowerCrystal;
   public static int[] bmWeapons;
   public static int MaxNeoCore;

   public static boolean isFairyPendant(int itemId) {
      switch (itemId) {
         case 1122017:
         case 1122155:
         case 1122156:
         case 1122158:
         case 1122207:
         case 1122215:
         case 1122271:
         case 1122307:
         case 1122316:
         case 1122323:
         case 1122334:
            return true;
         default:
            return false;
      }
   }

   public static int getFairyPendantEnhance(int itemId, int expRate) {
      switch (itemId) {
         case 1122017:
         case 1122155:
         case 1122156:
         case 1122207:
         case 1122215:
         case 1122307:
            if (expRate == 10) {
               return 0;
            }

            if (expRate == 20) {
               return 1;
            }

            if (expRate == 30) {
               return 2;
            }
            break;
         case 1122158:
            if (expRate == 5) {
               return 0;
            }

            if (expRate == 10) {
               return 1;
            }

            if (expRate == 15) {
               return 2;
            }
            break;
         case 1122271:
         case 1122334:
            return 0;
         case 1122316:
         case 1122323:
            if (expRate == 20) {
               return 0;
            }

            if (expRate == 30) {
               return 1;
            }
      }

      return 0;
   }

   public static long getExpNeededForLevel(int level) {
      if (level == 300) {
         return 1L;
      } else if (level > 300) {
         return exp[exp.length - 1];
      } else {
         return level < 0 ? 2147483647L : exp[level];
      }
   }

   public static int getGuildExpNeededForLevel(int level) {
      return level >= 0 && level < guildexp.length ? guildexp[level] : Integer.MAX_VALUE;
   }

   public static int getPVPExpNeededForLevel(int level) {
      return level >= 0 && level < pvpExp.length ? pvpExp[level] : Integer.MAX_VALUE;
   }

   public static int getClosenessNeededForLevel(int level) {
      return closeness[level - 1];
   }

   public static int getMountExpNeededForLevel(int level) {
      return mountexp[level - 1];
   }

   public static int getTraitExpNeededForLevel(int level) {
      return level >= 0 && level < cumulativeTraitExp.length ? cumulativeTraitExp[level] : Integer.MAX_VALUE;
   }

   public static int getSetExpNeededForLevel(int level) {
      return level >= 0 && level < setScore.length ? setScore[level] : Integer.MAX_VALUE;
   }

   public static long getEliteMonsterHPRate(int level) {
      if (level < 100) {
         return 300L;
      } else {
         return level <= 150 ? 500L : 750L;
      }
   }

   public static long getMonsterHP(int level) {
      long hp;
      if (level <= 3) {
         hp = 5 * level + 10;
      } else if (level <= 10) {
         hp = 15 * level - 25;
      } else if (level <= 20) {
         hp = 25 * level - 125;
      } else if (level <= 25) {
         hp = 30 * level - 225;
      } else if (level <= 29) {
         hp = 70 * level - 1240;
      } else if (level <= 31) {
         hp = 90 * level - 1800;
      } else if (level <= 45) {
         hp = 100 * level - 2100;
      } else if (level <= 50) {
         hp = 120 * level - 3000;
      } else if (level <= 55) {
         hp = 200 * level - 7000;
      } else if (level <= 60) {
         hp = 300 * level - 12500;
      } else if (level <= 65) {
         hp = 400 * level - 18500;
      } else if (level <= 70) {
         hp = 500 * level - 25000;
      } else if (level <= 75) {
         hp = 1000 * level - 60000;
      } else if (level <= 125) {
         hp = 2000 * level - 135000;
      } else if (level <= 127) {
         hp = 2000 * level - 134000;
      } else if (level <= 150) {
         hp = 5000 * level - 515000;
      } else if (level <= 180) {
         hp = 10000 * level - 1270000;
      } else {
         hp = 20000 * level - 3070000;
      }

      if (level <= 29) {
         return hp;
      } else if (level <= 100) {
         return hp * 2L;
      } else if (level <= 109) {
         return (long)(0.01 * Math.round(level * level / 50.0));
      } else {
         return level <= 159 ? (long)(0.015 * Math.round(level * level / 50.0)) : (long)(0.02 * Math.round(level * level / 50.0));
      }
   }

   public static int getTimelessRequiredEXP(int level) {
      return 70 + level * 10;
   }

   public static int getReverseRequiredEXP(int level) {
      return 60 + level * 5;
   }

   public static int getProfessionEXP(int level) {
      int[] expTable = new int[]{250, 600, 1050, 1600, 2250, 3000, 3850, 4900, 5850, 45000, 160000, 0};
      return expTable[level - 1];
   }

   public static boolean isHarvesting(int itemId) {
      return itemId >= 1500000 && itemId < 1520000;
   }

   public static final double maxViewRangeSq() {
      return Double.POSITIVE_INFINITY;
   }

   public static int maxViewRangeSq_Half() {
      return 500000;
   }

   public static boolean isJobFamily(int baseJob, int currentJob) {
      return currentJob >= baseJob && currentJob / 100 == baseJob / 100;
   }

   public static boolean isGM(int job) {
      return job == 800 || job == 900;
   }

   public static boolean isKOC(int job) {
      return job >= 1000 && job < 2000;
   }

   public static final boolean isNightWalker(int job) {
      return job == 1400 || job >= 1400 && job <= 1412 || job == 1414;
   }

   public static boolean isEvan(int job) {
      return job == 2001 || job >= 2200 && job <= 2218 || job == 2220;
   }

   public static boolean isMichael(int job) {
      return isMihile(job);
   }

   public static boolean isMihile(int job) {
      return job == 5000 || job >= 5000 && job <= 5112 || job == 5114;
   }

   public static boolean isCanElfEar(int job) {
      return isFlora(job) || isMercedes(job);
   }

   public static boolean isFlora(int job) {
      return isArk(job) || isAdele(job) || isIllium(job) || isKhali(job);
   }

   public static boolean isStriker(int job) {
      return job >= 1500 && job <= 1512 || job == 1514;
   }

   public static boolean isMercedes(int job) {
      return job == 2002 || job >= 2300 && job <= 2312 || job == 2314;
   }

   public static boolean isEunWol(int job) {
      return job == 2005 || job >= 2500 && job <= 2512 || job == 2514;
   }

   public static boolean isKinesis(int job) {
      return job == 14000 || job >= 14200 && job <= 14212 || job == 14214;
   }

   public static boolean isDemonSlayer(int job) {
      return job == 3001 || job == 3100 || job >= 3110 && job <= 3112 || job == 3114;
   }

   public static boolean isDemonAvenger(int job) {
      return job == 3001 || job == 3101 || job >= 3120 && job <= 3122 || job == 3124;
   }

   public static boolean isHoyoung(int job) {
      return job == 16000 || job >= 16400 && job <= 16412 || job == 16414;
   }

   public static boolean isAdele(int job) {
      return job == 15002 || job >= 15100 && job <= 15112 || job == 15114;
   }

   public static boolean isArk(int job) {
      return job == 15001 || job >= 15500 && job <= 15512 || job == 15514;
   }

   public static boolean isIllium(int job) {
      return job == 15000 | (job >= 15200 && job <= 15212) || job == 15214;
   }

   public static boolean isXenon(int job) {
      return job == 3002 || job >= 3600 && job <= 3612 || job == 3614;
   }

   public static boolean isAngelicBuster(int job) {
      return job == 6001 || job >= 6500 && job <= 6512 || job == 6514;
   }

   public static boolean isAran(int job) {
      return job >= 2000 && job <= 2112 && job != 2001 && job != 2002 && job != 2003 && job != 2004 && job != 2005 || job == 2114;
   }

   public static boolean isResist(int job) {
      return job >= 3000 && job <= 3514;
   }

   public static boolean isAdventurer(int job) {
      return job >= 0 && job < 1000;
   }

   public static boolean isCygnus(int job) {
      return job >= 1000 && job < 2000;
   }

   public static boolean isHero(int job) {
      return job >= 2000 && job < 3000;
   }

   public static boolean isResistance(int job) {
      return job >= 3000 && job < 4000;
   }

   public static boolean isDemon(int job) {
      return job == 3112 || job == 3122 || job == 3114 || job == 3124;
   }

   public static boolean isCannon(int job) {
      return job == 1 || job == 501 || job >= 530 && job <= 532 || job == 534;
   }

   public static boolean isPathFinder(int job) {
      return job == 301 || job >= 330 && job <= 332 || job == 334;
   }

   public static boolean isDualBlade(int job) {
      return job >= 430 && job <= 434 || job == 436;
   }

   public static boolean isPhantom(int job) {
      return job == 2003 || job / 100 == 24;
   }

   public static boolean isWildHunter(int job) {
      return job == 3000 || job >= 3300 && job <= 3312 || job == 3314;
   }

   public static boolean isMechanic(int job) {
      return job == 3000 || job >= 3500 && job <= 3512 || job == 3514;
   }

   public static boolean isLuminous(int job) {
      return job == 2004 || job >= 2700 && job <= 2712 || job == 2714;
   }

   public static boolean isKaiser(int job) {
      return job == 6000 || job >= 6100 && job <= 6112 || job == 6114;
   }

   public static boolean isKain(int job) {
      return job == 6003 || job >= 6300 && job <= 6312 || job == 6314;
   }

   public static boolean isKadena(int job) {
      return job == 6002 || job >= 6400 && job <= 6412 || job == 6414;
   }

   public static boolean isFlameWizard(int job) {
      return job >= 1200 && job <= 1212 || job == 1214;
   }

   public static boolean isZero(int job) {
      return job == 10000 || job >= 10100 && job <= 10112 || job == 10114;
   }

   public static boolean isPinkbean(int job) {
      return job == 13100;
   }

   public static boolean isYeti(int job) {
      return job == 13500;
   }

   public static boolean isLara(int job) {
      return job == 16001 || job >= 16200 && job <= 16212 || job == 16214;
   }

   public static boolean isKhali(int job) {
      return job == 15003 || job >= 15400 && job <= 15412 || job == 15414;
   }

   public static boolean isFourthJob(int job) {
      switch (job) {
         case 112:
         case 122:
         case 132:
         case 212:
         case 222:
         case 232:
         case 312:
         case 322:
         case 412:
         case 422:
         case 512:
         case 522:
            return true;
         default:
            return false;
      }
   }

   public static boolean isRecoveryIncSkill(int id) {
      switch (id) {
         case 1110000:
         case 1210000:
         case 2000000:
         case 4100002:
         case 4200001:
         case 11110000:
            return true;
         default:
            return false;
      }
   }

   public static boolean isLinkedAranSkill(int id) {
      return getLinkedAranSkill(id) != id;
   }

   public static final boolean isForceIncrease(int skillid) {
      switch (skillid) {
         case 24100003:
         case 24120002:
         case 30010166:
         case 30011167:
         case 30011168:
         case 30011169:
         case 30011170:
         case 31000004:
         case 31001006:
         case 31001007:
         case 31001008:
         case 400011007:
            return true;
         default:
            return false;
      }
   }

   public static int getBOF_ForJob(int job) {
      return PlayerStats.getSkillByJob(12, job);
   }

   public static int get_novice_skill_root(int a1) {
      int v1 = a1 / 100;
      if (a1 / 100 == 22 || a1 == 2001) {
         return 2001;
      } else if (v1 == 23 || a1 == 2002) {
         return 2002;
      } else if (v1 != 24 && a1 != 2003) {
         if (Integer.toUnsignedLong(a1 - 3100) < 100L || a1 == 3001) {
            return 3001;
         } else if (isMihile(a1)) {
            return 5000;
         } else if (isLuminous(a1)) {
            return 2004;
         } else if (isAngelicBuster(a1)) {
            return 6001;
         } else if (isXenon(a1)) {
            return 3002;
         } else if (isEunWol(a1)) {
            return 2005;
         } else if (isKinesis(a1)) {
            return 14000;
         } else if (isBlaster(a1)) {
            return 3000;
         } else if (isKadena(a1)) {
            return 6002;
         } else if (isKain(a1)) {
            return 6003;
         } else if (isArk(a1)) {
            return 15001;
         } else if (isAdele(a1)) {
            return 15002;
         } else if (isLara(a1)) {
            return 16001;
         } else {
            return isKhali(a1) ? 15003 : 1000 * (a1 / 1000);
         }
      } else {
         return 2003;
      }
   }

   public static int getEmpress_ForJob(int job) {
      return PlayerStats.getSkillByJob(73, job);
   }

   public static boolean isElementAmp_Skill(int skill) {
      switch (skill) {
         case 2110001:
         case 2210001:
         case 12110001:
         case 22150000:
            return true;
         default:
            return false;
      }
   }

   public static int getMPEaterForJob(int job) {
      switch (job) {
         case 210:
         case 211:
         case 212:
            return 2100000;
         case 213:
         case 214:
         case 215:
         case 216:
         case 217:
         case 218:
         case 219:
         case 223:
         case 224:
         case 225:
         case 226:
         case 227:
         case 228:
         case 229:
         default:
            return 2100000;
         case 220:
         case 221:
         case 222:
            return 2200000;
         case 230:
         case 231:
         case 232:
            return 2300000;
      }
   }

   public static int getJobShortValue(int job) {
      if (job >= 1000) {
         job -= job / 1000 * 1000;
      }

      job /= 100;
      if (job == 4) {
         job *= 2;
      } else if (job == 3) {
         job++;
      } else if (job == 5) {
         job += 11;
      }

      return job;
   }

   public static boolean isPyramidSkill(int skill) {
      return isNovice(skill / 10000) && skill % 10000 == 1020;
   }

   public static boolean isInflationSkill(int skill) {
      return isNovice(skill / 10000) && skill % 10000 == 1092;
   }

   public static boolean isMulungSkill(int skill) {
      return isNovice(skill / 10000) && (skill % 10000 == 1009 || skill % 10000 == 1010 || skill % 10000 == 1011);
   }

   public static boolean isIceKnightSkill(int skill) {
      return isNovice(skill / 10000)
         && (skill % 10000 == 1098 || skill % 10000 == 99 || skill % 10000 == 100 || skill % 10000 == 103 || skill % 10000 == 104 || skill % 10000 == 1105);
   }

   public static boolean isThrowingStar(int itemId) {
      return itemId / 10000 == 207;
   }

   public static boolean isCashThrowingStar(int itemID) {
      return itemID / 1000 == 5021;
   }

   public static boolean isArcaneSymbol(int itemid) {
      return itemid / 1000 == 1712;
   }

   public static boolean isAuthenticSymbol(int itemID) {
      return itemID / 1000 == 1713;
   }

   public static boolean isBullet(int itemId) {
      return itemId / 10000 == 233;
   }

   public static boolean isRechargable(int itemId) {
      return isThrowingStar(itemId) || isBullet(itemId);
   }

   public static boolean isOverall(int itemId) {
      return itemId / 10000 == 105;
   }

   public static boolean isPet(int itemId) {
      return itemId / 10000 == 500;
   }

   public static boolean isArrowForCrossBow(int itemId) {
      return itemId >= 2061000 && itemId < 2062000;
   }

   public static boolean isArrowForBow(int itemId) {
      return itemId >= 2060000 && itemId < 2061000;
   }

   public static boolean isMagicWeapon(int itemId) {
      int s = itemId / 10000;
      return s == 137 || s == 138;
   }

   public static boolean isWeapon(int itemId) {
      return itemId >= 1212000 && itemId < 1500000
         || itemId / 10000 == 152
         || itemId / 10000 == 153
         || itemId / 1000 == 1562
         || itemId / 1000 == 1572
         || itemId / 1000 == 1582
         || itemId / 1000 == 1592
         || itemId / 100000 == 16
            && itemId / 1000 != 1612
            && itemId / 1000 != 1622
            && itemId / 1000 != 1632
            && itemId / 1000 != 1642
            && itemId / 1000 != 1652
            && itemId / 1000 != 1662
            && itemId / 1000 != 1672
         || itemId / 100000 == 17 && itemId / 1000 != 1712 && itemId / 1000 != 1713;
   }

   public static boolean isSubWeapon(int itemID) {
      return itemID / 10000 != 134 && itemID / 10000 != 135 && itemID / 1000 != 1099 ? itemID / 1000 == 1098 : true;
   }

   public static boolean isSecondaryWeapon(int itemId) {
      return itemId / 10000 == 135;
   }

   public static boolean isCape(int itemId) {
      return itemId / 10000 == 109 || itemId / 10000 == 110 || itemId / 10000 == 113;
   }

   public static boolean isAndroidHeart(int itemID) {
      return itemID / 10000 == 167;
   }

   public static boolean isAndroid(int itemID) {
      return itemID / 10000 == 166;
   }

   public static MapleInventoryType getInventoryType(int itemId) {
      byte type = (byte)(itemId / 1000000);
      return type >= 1 && type <= 5 ? MapleInventoryType.getByType(type) : MapleInventoryType.UNDEFINED;
   }

   public static boolean isInBag(int slot, byte type) {
      return slot >= 10000;
   }

   public static MapleWeaponType getWeaponType(int itemId) {
      int cat = itemId / 10000;
      cat %= 100;
      switch (cat) {
         case 21:
            return MapleWeaponType.PLANE;
         case 22:
            return MapleWeaponType.SOULSHOOTER;
         case 23:
            return MapleWeaponType.DESPERADO;
         case 24:
            return MapleWeaponType.ENERGYSWORD;
         case 25:
         case 29:
         case 39:
         case 50:
         case 51:
         case 54:
         case 55:
         default:
            return MapleWeaponType.NOT_A_WEAPON;
         case 26:
            return MapleWeaponType.LIMITER;
         case 27:
            return MapleWeaponType.CHAIN;
         case 28:
            return MapleWeaponType.MAGIC_GUNTLIT;
         case 30:
            return MapleWeaponType.SWORD1H;
         case 31:
            return MapleWeaponType.AXE1H;
         case 32:
            return MapleWeaponType.BLUNT1H;
         case 33:
            return MapleWeaponType.DAGGER;
         case 34:
            return MapleWeaponType.KATARA;
         case 35:
            return MapleWeaponType.MAGIC_ARROW;
         case 36:
            return MapleWeaponType.CANE;
         case 37:
            return MapleWeaponType.WAND;
         case 38:
            return MapleWeaponType.STAFF;
         case 40:
            return MapleWeaponType.SWORD2H;
         case 41:
            return MapleWeaponType.AXE2H;
         case 42:
            return MapleWeaponType.BLUNT2H;
         case 43:
            return MapleWeaponType.SPEAR;
         case 44:
            return MapleWeaponType.POLE_ARM;
         case 45:
            return MapleWeaponType.BOW;
         case 46:
            return MapleWeaponType.CROSSBOW;
         case 47:
            return MapleWeaponType.CLAW;
         case 48:
            return MapleWeaponType.KNUCKLE;
         case 49:
            return MapleWeaponType.GUN;
         case 52:
            return MapleWeaponType.DUAL_BOW;
         case 53:
            return MapleWeaponType.HANDCANNON;
         case 56:
            return MapleWeaponType.BIG_SWORD;
         case 57:
            return MapleWeaponType.LONG_SWORD;
         case 58:
            return MapleWeaponType.GUNTLIT;
         case 59:
            return MapleWeaponType.ANCIENT_BOW;
      }
   }

   public static boolean isShield(int itemId) {
      int cat = itemId / 10000;
      cat %= 100;
      return cat == 9 && itemId / 1000 != 1099;
   }

   public static boolean isOneHandWeapon(int itemId) {
      int cat = itemId / 10000;
      return cat == 130 || cat == 131 || cat == 132 || cat == 133 || cat == 137 || cat == 138;
   }

   public static boolean isEquip(int itemId) {
      return itemId / 1000000 == 1;
   }

   public static boolean isCleanSlate(int itemId) {
      return itemId / 100 == 20490;
   }

   public static boolean isAccessoryScroll(int itemId) {
      return itemId / 100 == 20492;
   }

   public static boolean isChaosScroll(int itemId) {
      return itemId >= 2049105 && itemId <= 2049110 ? false : itemId / 100 == 20491 || itemId == 2040126;
   }

   public static boolean isEquipScroll(int scrollId) {
      return scrollId / 100 == 20493 || scrollId / 100 == 26440;
   }

   public static boolean isNormalEquipScroll(int scrollId) {
      switch (scrollId) {
         case 2049300:
         case 2049301:
         case 2049303:
         case 2049306:
         case 2049307:
         case 2049323:
         case 2049325:
         case 2049351:
            return true;
         default:
            return false;
      }
   }

   public static boolean isAlphaWeapon(int itemId) {
      return itemId / 10000 == 157;
   }

   public static boolean isBetaWeapon(int itemId) {
      return itemId / 10000 == 156;
   }

   public static boolean isZeroWeapon(int itemId) {
      return isAlphaWeapon(itemId) || isBetaWeapon(itemId);
   }

   public static boolean isPotentialScroll(int scrollId) {
      return scrollId / 100 == 20494 || scrollId / 100 == 20497 || scrollId == 5534000;
   }

   public static boolean isProstyScroll(int scrollId) {
      switch (scrollId) {
         case 2046841:
         case 2046842:
         case 2046856:
         case 2046857:
         case 2046964:
         case 2046965:
         case 2046967:
         case 2046971:
         case 2046991:
         case 2046992:
         case 2047402:
         case 2047403:
         case 2047405:
         case 2047406:
         case 2047407:
         case 2047408:
         case 2047801:
         case 2047803:
         case 2047814:
         case 2047914:
         case 2047915:
         case 2047917:
         case 2048094:
         case 2048095:
         case 2048804:
         case 2048805:
         case 2048836:
         case 2048837:
         case 2048838:
         case 2048839:
            return true;
         default:
            return false;
      }
   }

   public static boolean isSpecialCSScroll(int scrollId) {
      switch (scrollId) {
         case 5063000:
         case 5063100:
         case 5064000:
         case 5064003:
         case 5064100:
         case 5064200:
         case 5064300:
         case 5064400:
            return true;
         default:
            return false;
      }
   }

   public static boolean isEightRockScroll(int scrollId) {
      return scrollId / 1000 == 2046;
   }

   public static boolean isRebirhFireScroll(int scrollId) {
      return scrollId / 100 == 20487;
   }

   public static boolean isSpecialScroll(int scrollId) {
      switch (scrollId) {
         case 2040727:
         case 2041058:
         case 2530000:
         case 2530001:
         case 2531000:
         case 5063000:
         case 5064000:
         case 5064400:
            return true;
         default:
            return false;
      }
   }

   public static boolean isTwoHanded(int itemId) {
      switch (getWeaponType(itemId)) {
         case AXE2H:
         case GUN:
         case KNUCKLE:
         case BLUNT2H:
         case BOW:
         case CLAW:
         case CROSSBOW:
         case POLE_ARM:
         case SPEAR:
         case SWORD2H:
         case HANDCANNON:
            return true;
         default:
            return false;
      }
   }

   public static boolean isSpecialShield(int itemid) {
      return itemid / 1000 == 1098 || itemid / 1000 == 1099 || itemid / 10000 == 135;
   }

   public static boolean isTownScroll(int id) {
      return id >= 2030000 && id < 2040000;
   }

   public static boolean isUpgradeScroll(int id) {
      return id >= 2040000 && id < 2050000;
   }

   public static boolean isGun(int id) {
      return id >= 1492000 && id < 1500000;
   }

   public static boolean isUse(int id) {
      return id >= 2000000 && id < 3000000;
   }

   public static boolean isSummonSack(int id) {
      return id / 10000 == 210;
   }

   public static boolean isMonsterCard(int id) {
      return id / 10000 == 238;
   }

   public static boolean isSpecialCard(int id) {
      return id / 1000 >= 2388;
   }

   public static int getCardShortId(int id) {
      return id % 10000;
   }

   public static boolean isGem(int id) {
      return id >= 4250000 && id <= 4251402;
   }

   public static boolean isOtherGem(int id) {
      switch (id) {
         case 1032062:
         case 1142156:
         case 1142157:
         case 2040727:
         case 2041058:
         case 4001174:
         case 4001175:
         case 4001176:
         case 4001177:
         case 4001178:
         case 4001179:
         case 4001180:
         case 4001181:
         case 4001182:
         case 4001183:
         case 4001184:
         case 4001185:
         case 4001186:
         case 4031980:
         case 4032312:
         case 4032334:
            return true;
         default:
            return false;
      }
   }

   public static boolean isCustomQuest(int id) {
      return id > 99999;
   }

   public static long getTaxAmount(long meso) {
      if (meso >= 100000000L) {
         return Math.round(0.06 * meso);
      } else if (meso >= 25000000L) {
         return Math.round(0.05 * meso);
      } else if (meso >= 10000000L) {
         return Math.round(0.04 * meso);
      } else if (meso >= 5000000L) {
         return Math.round(0.03 * meso);
      } else if (meso >= 1000000L) {
         return Math.round(0.018 * meso);
      } else {
         return meso >= 100000L ? Math.round(0.008 * meso) : 0L;
      }
   }

   public static int EntrustedStoreTax(int meso) {
      if (meso >= 100000000) {
         return (int)Math.round(0.03 * meso);
      } else if (meso >= 25000000) {
         return (int)Math.round(0.025 * meso);
      } else if (meso >= 10000000) {
         return (int)Math.round(0.02 * meso);
      } else if (meso >= 5000000) {
         return (int)Math.round(0.015 * meso);
      } else if (meso >= 1000000) {
         return (int)Math.round(0.009 * meso);
      } else {
         return meso >= 100000 ? (int)Math.round(0.004 * meso) : 0;
      }
   }

   public static int getAttackDelay(int id, Skill skill) {
      switch (id) {
         case 0:
            return 570;
         case 3121004:
         case 5201006:
         case 5221004:
         case 13111002:
         case 23121000:
         case 33121009:
         case 35111004:
         case 35121005:
         case 35121013:
            return 40;
         case 4121007:
         case 5221007:
         case 14111005:
            return 99;
         default:
            if (skill != null && skill.getSkillType() == 3) {
               return 0;
            } else {
               return skill != null && skill.getDelay() > 0 && !isNoDelaySkill(id) ? skill.getDelay() : 330;
            }
      }
   }

   public static byte gachaponRareItem(int id) {
      switch (id) {
         case 2040006:
         case 2040007:
         case 2040303:
         case 2040403:
         case 2040506:
         case 2040507:
         case 2040603:
         case 2040709:
         case 2040710:
         case 2040711:
         case 2040806:
         case 2040903:
         case 2041024:
         case 2041025:
         case 2043003:
         case 2043103:
         case 2043203:
         case 2043303:
         case 2043703:
         case 2043803:
         case 2044003:
         case 2044019:
         case 2044103:
         case 2044203:
         case 2044303:
         case 2044403:
         case 2044503:
         case 2044603:
         case 2044703:
         case 2044815:
         case 2044908:
         case 2049000:
         case 2049001:
         case 2049002:
         case 2049100:
         case 2340000:
            return 2;
         default:
            return 0;
      }
   }

   public static boolean isReverseItem(int itemId) {
      switch (itemId) {
         case 1002790:
         case 1002791:
         case 1002792:
         case 1002793:
         case 1002794:
         case 1052160:
         case 1052161:
         case 1052162:
         case 1052163:
         case 1052164:
         case 1072361:
         case 1072362:
         case 1072363:
         case 1072364:
         case 1072365:
         case 1082239:
         case 1082240:
         case 1082241:
         case 1082242:
         case 1082243:
         case 1302086:
         case 1312038:
         case 1322061:
         case 1332075:
         case 1332076:
         case 1342012:
         case 1372045:
         case 1382059:
         case 1402047:
         case 1412034:
         case 1422038:
         case 1432049:
         case 1442067:
         case 1452059:
         case 1462051:
         case 1472071:
         case 1482024:
         case 1492025:
         case 1522017:
         case 1532016:
         case 1942002:
         case 1952002:
         case 1962002:
         case 1972002:
            return true;
         default:
            return false;
      }
   }

   public static boolean isTimelessItem(int itemId) {
      switch (itemId) {
         case 1002776:
         case 1002777:
         case 1002778:
         case 1002779:
         case 1002780:
         case 1032031:
         case 1052155:
         case 1052156:
         case 1052157:
         case 1052158:
         case 1052159:
         case 1072355:
         case 1072356:
         case 1072357:
         case 1072358:
         case 1072359:
         case 1082234:
         case 1082235:
         case 1082236:
         case 1082237:
         case 1082238:
         case 1092057:
         case 1092058:
         case 1092059:
         case 1102172:
         case 1122011:
         case 1122012:
         case 1302081:
         case 1312037:
         case 1322060:
         case 1332073:
         case 1332074:
         case 1342011:
         case 1372044:
         case 1382057:
         case 1402046:
         case 1412033:
         case 1422037:
         case 1432047:
         case 1442063:
         case 1452057:
         case 1462050:
         case 1472068:
         case 1482023:
         case 1492023:
         case 1522016:
         case 1532015:
            return true;
         default:
            return false;
      }
   }

   public static boolean isRing(int itemId) {
      return itemId / 10000 == 111;
   }

   public static boolean isEffectRing(int itemid) {
      return isFriendshipRing(itemid) || isCrushRing(itemid) || isMarriageRing(itemid);
   }

   public static boolean isMarriageRing(int itemId) {
      switch (itemId) {
         case 1112803:
         case 1112806:
         case 1112807:
         case 1112809:
            return true;
         case 1112804:
         case 1112805:
         case 1112808:
         default:
            return false;
      }
   }

   public static boolean isFriendshipRing(int itemId) {
      switch (itemId) {
         case 1049000:
         case 1112001:
         case 1112002:
         case 1112003:
         case 1112005:
         case 1112006:
         case 1112007:
         case 1112012:
         case 1112800:
         case 1112801:
         case 1112802:
         case 1112810:
         case 1112811:
         case 1112812:
         case 1112816:
         case 1112817:
            return true;
         default:
            return false;
      }
   }

   public static boolean isCrushRing(int itemId) {
      switch (itemId) {
         case 1048000:
         case 1048001:
         case 1048002:
         case 1112001:
         case 1112002:
         case 1112003:
         case 1112005:
         case 1112006:
         case 1112007:
         case 1112012:
         case 1112013:
         case 1112014:
         case 1112015:
            return true;
         default:
            return false;
      }
   }

   public static int getEquipmentBonusEXP(int itemid) {
      if (DBConfig.isGanglim) {
         switch (itemid) {
            case 1142093:
               return 25;
            case 1142094:
               return 30;
            case 1142095:
               return 35;
            case 1142096:
               return 40;
            case 1142097:
            case 1142098:
               return 45;
            case 1142099:
               return 50;
         }
      } else {
         switch (itemid) {
            case 1142096:
               return 20;
            case 1142097:
               return 20;
            case 1142098:
               return 25;
            case 1142099:
            case 1142329:
               return 25;
            case 1142107:
               return 40;
            case 1142108:
               return 45;
            case 1142109:
               return 50;
            case 1142110:
               return 55;
            case 1142242:
            case 1142243:
            case 1142244:
            case 1142245:
            case 1142246:
            case 1142247:
               return 60;
            case 1142442:
            case 1142443:
               return 30;
            case 1142444:
            case 1142569:
               return 35;
         }
      }

      return 0;
   }

   public static int getEquipmentReduceCool(int itemid) {
      if (DBConfig.isGanglim) {
         switch (itemid) {
            case 1142094:
               return 20;
            case 1142095:
               return 25;
            case 1142096:
            case 1142097:
               return 30;
            case 1142098:
               return 35;
            case 1142099:
               return 40;
         }
      } else {
         switch (itemid) {
            case 1142098:
               return 10;
            case 1142099:
            case 1142329:
               return 20;
            case 1142107:
            case 1142108:
               return 35;
            case 1142109:
            case 1142110:
               return 40;
            case 1142242:
            case 1142243:
            case 1142244:
            case 1142245:
            case 1142246:
            case 1142247:
               return 60;
            case 1142442:
            case 1142443:
               return 25;
            case 1142444:
            case 1142569:
               return 30;
         }
      }

      return 0;
   }

   public static int getExpForLevel(int i, int itemId) {
      if (isReverseItem(itemId)) {
         return getReverseRequiredEXP(i);
      } else {
         return getMaxLevel(itemId) > 0 ? getTimelessRequiredEXP(i) : 0;
      }
   }

   public static int getMaxLevel(int itemId) {
      Map<Integer, Map<String, Integer>> inc = MapleItemInformationProvider.getInstance().getEquipIncrements(itemId);
      return inc != null ? inc.size() : 0;
   }

   public static int getStatChance() {
      return 25;
   }

   public static MobTemporaryStatFlag getStatFromWeapon(int itemid) {
      switch (itemid) {
         case 1302108:
         case 1312040:
         case 1322066:
         case 1332082:
         case 1372047:
         case 1382063:
         case 1402054:
         case 1412036:
         case 1422040:
         case 1432051:
         case 1442072:
         case 1452063:
         case 1462057:
         case 1472078:
         case 1482036:
            return MobTemporaryStatFlag.SPEED;
         case 1302109:
         case 1312041:
         case 1322067:
         case 1332083:
         case 1372048:
         case 1382064:
         case 1402055:
         case 1412037:
         case 1422041:
         case 1432052:
         case 1442073:
         case 1452064:
         case 1462058:
         case 1472079:
         case 1482035:
            return MobTemporaryStatFlag.DARKNESS;
         default:
            return null;
      }
   }

   public static int getXForStat(MobTemporaryStatFlag stat) {
      switch (stat) {
         case DARKNESS:
            return -70;
         case SPEED:
            return -50;
         default:
            return 0;
      }
   }

   public static int getSkillForStat(MobTemporaryStatFlag stat) {
      switch (stat) {
         case DARKNESS:
            return 1111003;
         case SPEED:
            return 3121007;
         default:
            return 0;
      }
   }

   public static int getSkillBook(int job, int skill) {
      if (job >= 2210 && job <= 2218) {
         return job - 2209;
      } else if (isZero(job)) {
         if (skill > 0) {
            int type = skill % 1000 / 100;
            return type == 1 ? 1 : 0;
         } else {
            return 0;
         }
      } else {
         switch (job) {
            case 110:
            case 120:
            case 130:
            case 210:
            case 220:
            case 230:
            case 310:
            case 320:
            case 410:
            case 420:
            case 510:
            case 520:
            case 570:
            case 1110:
            case 1310:
            case 1510:
            case 2310:
            case 2410:
            case 2510:
            case 2710:
            case 3110:
            case 3120:
            case 3200:
            case 3210:
            case 3310:
            case 3510:
            case 3610:
            case 4110:
            case 4210:
            case 5110:
            case 6110:
            case 6510:
            case 16410:
               return 1;
            case 111:
            case 121:
            case 131:
            case 211:
            case 221:
            case 231:
            case 311:
            case 321:
            case 411:
            case 421:
            case 511:
            case 521:
            case 571:
            case 1111:
            case 1311:
            case 1511:
            case 2311:
            case 2411:
            case 2511:
            case 2711:
            case 3111:
            case 3121:
            case 3211:
            case 3311:
            case 3511:
            case 3611:
            case 4111:
            case 4211:
            case 5111:
            case 6111:
            case 6511:
            case 16411:
               return 2;
            case 112:
            case 122:
            case 132:
            case 212:
            case 222:
            case 232:
            case 312:
            case 322:
            case 412:
            case 422:
            case 512:
            case 522:
            case 572:
            case 1112:
            case 1312:
            case 1512:
            case 2312:
            case 2412:
            case 2512:
            case 2712:
            case 3112:
            case 3122:
            case 3212:
            case 3312:
            case 3512:
            case 3612:
            case 4112:
            case 4212:
            case 5112:
            case 6112:
            case 6512:
            case 16412:
               return 3;
            case 508:
            case 3101:
               return 0;
            default:
               if (isSeparatedSp(job)) {
                  return job % 10 > 4 ? 0 : job % 10;
               } else {
                  return 0;
               }
         }
      }
   }

   public static int getSkillBook(int job, int level, int skill) {
      if (job >= 2210 && job <= 2218) {
         return job - 2209;
      } else if (!isSeparatedSp(job)) {
         return 0;
      } else {
         return level <= 30 ? 0 : (level >= 31 && level <= 60 ? 1 : (level >= 61 && level <= 100 ? 2 : (level >= 100 ? 3 : 0)));
      }
   }

   public static int getSkillBookForSkill(int skillid) {
      return getSkillBook(skillid / 10000, skillid);
   }

   public static int getLinkedMountItem(int sourceid) {
      switch (sourceid % 1000) {
         case 1:
         case 24:
         case 25:
            return 1018;
         case 2:
         case 26:
            return 1019;
         case 3:
            return 1025;
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
            return sourceid % 1000 + 1023;
         case 9:
         case 10:
         case 11:
            return sourceid % 1000 + 1024;
         case 12:
            return 1042;
         case 13:
            return 1044;
         case 14:
            return 1049;
         case 15:
         case 16:
         case 17:
            return sourceid % 1000 + 1036;
         case 18:
         case 19:
            return sourceid % 1000 + 1045;
         case 20:
            return 1072;
         case 21:
            return 1084;
         case 22:
            return 1089;
         case 23:
            return 1106;
         case 27:
            return 1932049;
         case 28:
            return 1932050;
         case 29:
            return 1151;
         case 30:
         case 50:
            return 1054;
         case 31:
         case 51:
            return 1069;
         case 32:
            return 1138;
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 57:
         case 79:
         case 80:
         case 81:
         case 84:
         case 89:
         case 91:
         case 92:
         case 93:
         case 94:
         case 95:
         case 96:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         case 107:
         case 108:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 127:
         case 128:
         case 129:
         case 130:
         case 131:
         case 132:
         case 133:
         case 134:
         case 135:
         case 136:
         case 137:
         case 138:
         case 139:
         case 140:
         case 141:
         case 142:
         case 143:
         case 144:
         case 145:
         case 146:
         case 147:
         case 149:
         case 150:
         case 151:
         case 152:
         case 153:
         case 154:
         case 155:
         case 156:
         case 157:
         case 158:
         case 160:
         case 161:
         case 162:
         case 163:
         case 164:
         case 165:
         case 166:
         case 167:
         case 168:
         case 169:
         case 170:
         case 171:
         case 172:
         case 173:
         case 174:
         case 176:
         case 177:
         case 178:
         case 179:
         case 180:
         case 181:
         case 182:
         case 184:
         case 185:
         case 186:
         case 187:
         case 188:
         case 189:
         case 190:
         case 192:
         case 194:
         case 196:
         case 197:
         case 198:
         case 199:
         case 200:
         default:
            return 0;
         case 38:
            return 1932088;
         case 44:
            return 1932090;
         case 45:
         case 46:
         case 47:
         case 48:
         case 49:
            return sourceid % 1000 + 1009;
         case 52:
            return 1070;
         case 53:
            return 1071;
         case 54:
            return 1096;
         case 55:
            return 1101;
         case 56:
            return 1102;
         case 58:
            return 1118;
         case 59:
            return 1121;
         case 60:
            return 1122;
         case 61:
            return 1129;
         case 62:
            return 1139;
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
            return sourceid % 1000 + 1080;
         case 82:
            return 1932093;
         case 83:
            return 1932094;
         case 85:
         case 86:
         case 87:
            return sourceid % 1000 + 928;
         case 88:
            return 1065;
         case 90:
            return 1932096;
         case 109:
         case 159:
            return 1932051;
         case 148:
            return 1932114;
         case 175:
            return 1932095;
         case 183:
            return 1932028;
         case 191:
            return 1932055;
         case 193:
            return 1932113;
         case 195:
            return 1932115;
         case 201:
            return 1932394;
      }
   }

   public static int getMountItem(int sourceid, MapleCharacter chr) {
      switch (sourceid) {
         case 5221006:
            return 1932000;
         case 33001001:
            if (chr == null) {
               return 0;
            } else {
               switch (chr.getWildHunterInfo().getRidingType() * 10) {
                  case 10:
                  default:
                     return 1932015;
                  case 20:
                     return 1932030;
                  case 30:
                     return 1932031;
                  case 40:
                     return 1932032;
                  case 50:
                     return 1932033;
                  case 60:
                     return 1932036;
                  case 70:
                     return 1932100;
                  case 80:
                     return 1932149;
                  case 90:
                     return 1932215;
               }
            }
         case 35001002:
         case 35120000:
            return 1932016;
         case 80001398:
            return 1932216;
         case 80001400:
            return 1932217;
         case 80001404:
            return 1932223;
         case 80001413:
            return 1932219;
         case 80001423:
            return 1932222;
         case 80001435:
            return 1932224;
         case 80001440:
            return 1932237;
         case 80001441:
            return 1932238;
         case 80001442:
            return 1932239;
         case 80001443:
            return 1932240;
         case 80001444:
            return 1932241;
         case 80001445:
            return 1932242;
         case 80001447:
            return 1932243;
         case 80001453:
            return 1932228;
         case 80001480:
            return 1932231;
         case 80001482:
            return 1932232;
         case 80001484:
            return 1932235;
         case 80001490:
            return 1932247;
         case 80001491:
            return 1932248;
         case 80001492:
            return 1932249;
         case 80001503:
            return 1932250;
         case 80001505:
            return 1932251;
         case 80001517:
            return 1932252;
         case 80001531:
            return 1932253;
         case 80001533:
            return 1930001;
         case 80001549:
            return 1932254;
         case 80001550:
            return 1932255;
         case 80001552:
            return 1932256;
         case 80001554:
            return 1932258;
         case 80001557:
            return 1932259;
         case 80001561:
            return 1932263;
         case 80001562:
            return 1932264;
         case 80001563:
            return 1932265;
         case 80001564:
            return 1932266;
         case 80001565:
            return 1932267;
         case 80001566:
            return 1932268;
         case 80001567:
            return 1932269;
         case 80001568:
            return 1932270;
         case 80001569:
            return 1932271;
         case 80001570:
            return 1932272;
         case 80001582:
            return 1932275;
         case 80001584:
            return 1932276;
         case 80001639:
            return 1932296;
         case 80001640:
            return 1932298;
         case 80001642:
            return 1932053;
         case 80001643:
            return 1932010;
         case 80001644:
            return 1932153;
         case 80001703:
            return 1932305;
         case 80001707:
            return 1932306;
         case 80001708:
            return 1932307;
         case 80001711:
            return 1932310;
         case 80001713:
            return 1932311;
         case 80001763:
            return 1932319;
         case 80001764:
            return 1932320;
         case 80001766:
            return 1932321;
         case 80001769:
            return 1932322;
         case 80001775:
            return 1932323;
         case 80001776:
            return 1932324;
         case 80001785:
            return 1932334;
         case 80001786:
            return 1932335;
         case 80001790:
            return 1932336;
         case 80001792:
            return 1932337;
         case 80001811:
            return 1932341;
         case 80001813:
            return 1932342;
         case 80001814:
            return 1932342;
         case 80001867:
            return 1932344;
         case 80001868:
            return 1932345;
         case 80001870:
            return 1932347;
         case 80001872:
            return 1932349;
         case 80001918:
            return 1932353;
         case 80001920:
            return 1932354;
         case 80001921:
            return 1932360;
         case 80001923:
            return 1932361;
         case 80001933:
            return 1932357;
         case 80001934:
            return 1932358;
         case 80001935:
            return 1932359;
         case 80001942:
            return 1932365;
         case 80001953:
            return 1932371;
         case 80001954:
            return 1932372;
         case 80001955:
            return 1932373;
         case 80001956:
            return 1932374;
         case 80001958:
            return 1932375;
         case 80001975:
            return 1932377;
         case 80001977:
            return 1932378;
         case 80001980:
            return 1932379;
         case 80001988:
            return 1932383;
         case 80001989:
            return 1932384;
         case 80001990:
            return 1932385;
         case 80001991:
            return 1932386;
         case 80001993:
            return 1932388;
         case 80001995:
            return 1932392;
         case 80001997:
            return 1932391;
         case 80002201:
            return 1932394;
         case 80002202:
            return 1932395;
         case 80002204:
            return 1932399;
         case 80002219:
            return 1932398;
         case 80002220:
            return 1932401;
         case 80002221:
            return 1932402;
         case 80002222:
            return 1932403;
         case 80002223:
            return 1932405;
         case 80002225:
            return 1932406;
         case 80002233:
            return 1932410;
         case 80002234:
            return 1932411;
         case 80002235:
            return 1932412;
         case 80002236:
            return 1932414;
         case 80002238:
            return 1932415;
         case 80002240:
            return 1932418;
         case 80002242:
            return 1932419;
         case 80002248:
            return 1932421;
         case 80002250:
            return 1932422;
         case 80002257:
            return 1932429;
         case 80002258:
            return 1932428;
         case 80002259:
            return 1932422;
         case 80002261:
            return 1932430;
         case 80002262:
            return 1932431;
         case 80002265:
            return 1932432;
         case 80002266:
            return 1932434;
         case 80002270:
            return 1932426;
         case 80002271:
            return 1932439;
         case 80002272:
            return 1932438;
         case 80002277:
            return 1932441;
         case 80002278:
            return 1932442;
         case 80002287:
            return 1932445;
         case 80002289:
            return 1932446;
         case 80002295:
            return 1932448;
         case 80002297:
            return 1932449;
         case 80002302:
            return 1932452;
         case 80002304:
            return 1932453;
         case 80002305:
            return 1932454;
         case 80002307:
            return 1932455;
         case 80002314:
            return 1932459;
         case 80002315:
            return 1932447;
         case 80002318:
            return 1932463;
         case 80002319:
            return 1932464;
         case 80002321:
            return 1932465;
         case 80002335:
            return 1932468;
         case 80002345:
            return 1932470;
         case 80002347:
            return 1932471;
         case 80002354:
            return 1932478;
         case 80002355:
            return 1932480;
         case 80002356:
            return 1932474;
         case 80002358:
            return 1932475;
         case 80002361:
            return 1932479;
         case 80002367:
            return 1932483;
         case 80002369:
            return 1932486;
         default:
            if (!isNovice(sourceid / 10000)) {
               if (sourceid / 10000 == 8000 && sourceid != 80001000) {
                  Skill skil = SkillFactory.getSkill(sourceid);
                  if (skil != null && skil.getTamingMob() > 0) {
                     return skil.getTamingMob();
                  }

                  int link = getLinkedMountItem(sourceid);
                  if (link > 0) {
                     if (link < 10000) {
                        if (chr == null) {
                           return link;
                        }

                        return getMountItem(link, chr);
                     }

                     return link;
                  }
               }

               return 0;
            } else {
               switch (sourceid % 10000) {
                  case 1013:
                     return 1932001;
                  case 1014:
                  case 1020:
                  case 1021:
                  case 1022:
                  case 1023:
                  case 1024:
                  case 1026:
                  case 1032:
                  case 1041:
                  case 1043:
                  case 1045:
                  case 1046:
                  case 1047:
                  case 1055:
                  case 1056:
                  case 1057:
                  case 1058:
                  case 1059:
                  case 1060:
                  case 1061:
                  case 1062:
                  case 1066:
                  case 1067:
                  case 1068:
                  case 1073:
                  case 1074:
                  case 1075:
                  case 1076:
                  case 1077:
                  case 1078:
                  case 1079:
                  case 1080:
                  case 1081:
                  case 1082:
                  case 1083:
                  case 1085:
                  case 1086:
                  case 1087:
                  case 1088:
                  case 1090:
                  case 1091:
                  case 1092:
                  case 1093:
                  case 1094:
                  case 1095:
                  case 1097:
                  case 1098:
                  case 1099:
                  case 1100:
                  case 1103:
                  case 1104:
                  case 1105:
                  case 1107:
                  case 1108:
                  case 1109:
                  case 1110:
                  case 1111:
                  case 1112:
                  case 1113:
                  case 1114:
                  case 1116:
                  case 1117:
                  case 1119:
                  case 1120:
                  case 1124:
                  case 1125:
                  case 1126:
                  case 1127:
                  case 1129:
                  case 1131:
                  case 1132:
                  case 1133:
                  case 1134:
                  case 1135:
                  case 1137:
                  case 1140:
                  case 1141:
                  case 1142:
                  default:
                     return 0;
                  case 1015:
                  case 1048:
                     return 1932002;
                  case 1016:
                  case 1017:
                  case 1027:
                     return 1932007;
                  case 1018:
                     return 1932003;
                  case 1019:
                     return 1932005;
                  case 1025:
                     return 1932006;
                  case 1028:
                     return 1932008;
                  case 1029:
                     return 1932009;
                  case 1030:
                     return 1932011;
                  case 1031:
                     return 1932010;
                  case 1033:
                     return 1932013;
                  case 1034:
                     return 1932014;
                  case 1035:
                     return 1932012;
                  case 1036:
                     return 1932017;
                  case 1037:
                     return 1932018;
                  case 1038:
                     return 1932019;
                  case 1039:
                     return 1932020;
                  case 1040:
                     return 1932021;
                  case 1042:
                     return 1932022;
                  case 1044:
                     return 1932023;
                  case 1049:
                     return 1932025;
                  case 1050:
                     return 1932004;
                  case 1051:
                     return 1932026;
                  case 1052:
                     return 1932027;
                  case 1053:
                     return 1932028;
                  case 1054:
                     return 1932029;
                  case 1063:
                     return 1932034;
                  case 1064:
                     return 1932035;
                  case 1065:
                     return 1932037;
                  case 1069:
                     return 1932038;
                  case 1070:
                     return 1932039;
                  case 1071:
                     return 1932040;
                  case 1072:
                     return 1932041;
                  case 1084:
                     return 1932043;
                  case 1089:
                     return 1932044;
                  case 1096:
                     return 1932045;
                  case 1101:
                     return 1932046;
                  case 1102:
                     return 1932061;
                  case 1106:
                     return 1932048;
                  case 1115:
                     return 1932052;
                  case 1118:
                     return 1932060;
                  case 1121:
                     return 1932063;
                  case 1122:
                     return 1932064;
                  case 1123:
                     return 1932065;
                  case 1128:
                     return 1932066;
                  case 1130:
                     return 1932072;
                  case 1136:
                     return 1932078;
                  case 1138:
                     return 1932080;
                  case 1139:
                     return 1932081;
                  case 1143:
                  case 1144:
                  case 1145:
                  case 1146:
                  case 1147:
                  case 1148:
                  case 1149:
                  case 1150:
                  case 1151:
                  case 1152:
                  case 1153:
                  case 1154:
                  case 1155:
                  case 1156:
                  case 1157:
                     return 1992000 + sourceid % 10000 - 1143;
               }
            }
      }
   }

   public static boolean isNewFlying(int skillID) {
      switch (skillID) {
         case 80001435:
         case 80001484:
         case 80001709:
         case 80001710:
         case 80001774:
         case 80001976:
         case 80001981:
         case 80001993:
         case 80001997:
         case 80002220:
         case 80002227:
         case 80002236:
         case 80002240:
         case 80002260:
         case 80002278:
         case 80002287:
         case 80002302:
         case 80002304:
         case 80002307:
         case 80002345:
         case 80002356:
         case 80002358:
         case 80002361:
         case 80002367:
         case 80002370:
         case 80002392:
         case 80002400:
         case 80002402:
         case 80002426:
         case 80002429:
         case 80002431:
         case 80002433:
         case 80002435:
         case 80002437:
         case 80002439:
         case 80002440:
         case 80002443:
         case 80002448:
         case 80002450:
         case 80002454:
         case 80002571:
         case 80002573:
         case 80002622:
         case 80002628:
         case 80002630:
         case 80002648:
         case 80002650:
         case 80002655:
         case 80002659:
         case 80002661:
         case 80002664:
         case 80002665:
         case 80002667:
         case 80002712:
         case 80002715:
         case 80002735:
         case 80002738:
         case 80002740:
         case 80002742:
         case 80002743:
         case 80002744:
         case 80002754:
         case 80002756:
         case 80002831:
         case 80002843:
         case 80002845:
         case 80002846:
         case 80002853:
         case 80002855:
         case 80002858:
         case 80002859:
         case 80002862:
         case 80002869:
         case 80002872:
         case 80002875:
         case 80002881:
         case 80002920:
         case 80002936:
         case 80002979:
         case 80002980:
         case 80002986:
         case 80002987:
         case 80002991:
         case 80002997:
         case 80002999:
         case 80003030:
         case 80003057:
         case 80003065:
         case 80003121:
         case 80003122:
            return true;
         default:
            return false;
      }
   }

   public static boolean isKatara(int itemId) {
      return itemId / 10000 == 134;
   }

   public static boolean isDagger(int itemId) {
      return itemId / 10000 == 133;
   }

   public static boolean isApplicableSkill(int skil) {
      return (skil < 80000000 || skil >= 100000000) && !isAngel(skil)
         || skil >= 92000000
         || skil >= 80000000 && skil < 80010000
         || skil == 80001040
         || skil == 110
         || skil == 20050110
         || skil == 80000000
         || skil == 100000110
         || skil == 130000110
         || skil == 140000110;
   }

   public static boolean isApplicableSkill_(int skil) {
      for (int i : PlayerStats.pvpSkills) {
         if (skil == i) {
            return true;
         }
      }

      return skil >= 90000000 && skil < 92000000 || isAngel(skil);
   }

   public static boolean isTablet(int itemId) {
      return itemId / 1000 == 2047;
   }

   public static int getSuccessTablet(int scrollId, int level) {
      if (scrollId % 1000 / 100 == 2) {
         switch (level) {
            case 0:
               return 70;
            case 1:
               return 55;
            case 2:
               return 43;
            case 3:
               return 33;
            case 4:
               return 26;
            case 5:
               return 20;
            case 6:
               return 16;
            case 7:
               return 12;
            case 8:
               return 10;
            default:
               return 7;
         }
      } else if (scrollId % 1000 / 100 == 3) {
         switch (level) {
            case 0:
               return 70;
            case 1:
               return 35;
            case 2:
               return 18;
            case 3:
               return 12;
            default:
               return 7;
         }
      } else {
         switch (level) {
            case 0:
               return 70;
            case 1:
               return 50;
            case 2:
               return 36;
            case 3:
               return 26;
            case 4:
               return 19;
            case 5:
               return 14;
            case 6:
               return 10;
            default:
               return 7;
         }
      }
   }

   public static int getCurseTablet(int scrollId, int level) {
      if (scrollId % 1000 / 100 == 2) {
         switch (level) {
            case 0:
               return 10;
            case 1:
               return 12;
            case 2:
               return 16;
            case 3:
               return 20;
            case 4:
               return 26;
            case 5:
               return 33;
            case 6:
               return 43;
            case 7:
               return 55;
            case 8:
               return 70;
            default:
               return 100;
         }
      } else if (scrollId % 1000 / 100 == 3) {
         switch (level) {
            case 0:
               return 12;
            case 1:
               return 18;
            case 2:
               return 35;
            case 3:
               return 70;
            default:
               return 100;
         }
      } else {
         switch (level) {
            case 0:
               return 10;
            case 1:
               return 14;
            case 2:
               return 19;
            case 3:
               return 26;
            case 4:
               return 36;
            case 5:
               return 50;
            case 6:
               return 70;
            default:
               return 100;
         }
      }
   }

   public static boolean isAccessory(int itemId) {
      return itemId >= 1010000 && itemId < 1040000
         || itemId >= 1122000 && itemId < 1153000
         || itemId >= 1112000 && itemId < 1113000
         || itemId >= 1132000 && itemId < 1133000;
   }

   public static boolean isMedal(int itemId) {
      return itemId / 10000 == 114;
   }

   public static boolean potentialIDFits(int potentialID, int newstate, int i) {
      if (newstate == 20) {
         return i != 0 && Randomizer.nextInt(10) != 0 ? potentialID >= 30000 && potentialID < 60004 : potentialID >= 40000;
      } else if (newstate == 19) {
         return i != 0 && Randomizer.nextInt(10) != 0 ? potentialID >= 20000 && potentialID < 30000 : potentialID >= 30000;
      } else if (newstate == 18) {
         return i != 0 && Randomizer.nextInt(10) != 0 ? potentialID >= 10000 && potentialID < 20000 : potentialID >= 20000 && potentialID < 30000;
      } else if (newstate != 17) {
         return false;
      } else {
         return i != 0 && Randomizer.nextInt(10) != 0 ? potentialID < 10000 : potentialID >= 10000 && potentialID < 20000;
      }
   }

   public static boolean optionTypeFits(int optionType, int itemId) {
      switch (optionType) {
         case 10:
            return isWeapon(itemId);
         case 11:
            return !isWeapon(itemId);
         case 20:
            return !isAccessory(itemId) && !isWeapon(itemId);
         case 40:
            return isAccessory(itemId);
         case 51:
            return itemId / 10000 == 100;
         case 52:
            return itemId / 10000 == 104 || itemId / 10000 == 105;
         case 53:
            return itemId / 10000 == 106 || itemId / 10000 == 105;
         case 54:
            return itemId / 10000 == 108;
         case 55:
            return itemId / 10000 == 107;
         default:
            return true;
      }
   }

   public static final boolean isMountItemAvailable(int mountid, int jobid) {
      if (jobid != 900 && mountid / 10000 == 190 || mountid == 1939007) {
         switch (mountid) {
            case 1902000:
            case 1902001:
            case 1902002:
               return isAdventurer(jobid);
            case 1902005:
            case 1902006:
            case 1902007:
               return isKOC(jobid);
            case 1902015:
            case 1902016:
            case 1902017:
            case 1902018:
               return isAran(jobid);
            case 1902040:
            case 1902041:
            case 1902042:
            case 1939007:
               return isEvan(jobid);
            default:
               if (isResist(jobid)) {
                  return false;
               }
         }
      }

      return mountid / 10000 == 190;
   }

   public static boolean isMechanicItem(int itemId) {
      return itemId >= 1610000 && itemId < 1660000;
   }

   public static boolean isEvanDragonItem(int itemId) {
      return itemId >= 1940000 && itemId < 1980000;
   }

   public static boolean canScroll(int itemId) {
      return itemId / 100000 != 19 && itemId / 100000 != 16 || itemId / 1000 == 1672;
   }

   public static boolean canHammer(int itemId) {
      switch (itemId) {
         case 1122000:
         case 1122076:
            return false;
         default:
            return canScroll(itemId);
      }
   }

   public static int getMasterySkill(int job) {
      if (job >= 1410 && job <= 1412) {
         return 14100000;
      } else if (job >= 410 && job <= 412) {
         return 4100000;
      } else {
         return job >= 520 && job <= 522 ? 5200000 : 0;
      }
   }

   public static int getExpRate_Below10(int job) {
      if (isEvan(job)) {
         return 1;
      } else {
         return !isAran(job) && !isKOC(job) && !isResist(job) ? 1 : 1;
      }
   }

   public static int getExpRate_Quest(int level) {
      return level >= 30 ? (level >= 70 ? (level >= 120 ? 10 : 5) : 2) : 1;
   }

   public static String getCashBlockedMsg(int id) {
      switch (id) {
         case 5050000:
         case 5062000:
         case 5062001:
            return "This item may only be purchased at the PlayerNPC in FM.";
         default:
            return "This item is blocked from the Cash Shop.";
      }
   }

   public static int getCustomReactItem(int rid, int original) {
      return rid == 2008006 ? Calendar.getInstance().get(7) + 4001055 : original;
   }

   public static int getJobNumber(int jobz) {
      Skill skill = SkillFactory.getSkill(jobz);
      if (skill != null) {
         if (skill.isHyper()) {
            return 5;
         } else {
            jobz /= 10000;
            int job = jobz % 1000;
            if (isDualBlade(job)) {
               if (job == 400) {
                  return 1;
               }

               if (job == 430) {
                  return 9;
               }

               if (job == 431) {
                  return 2;
               }

               if (job == 432) {
                  return 9;
               }

               if (job == 433) {
                  return 3;
               }

               if (job == 434) {
                  return 4;
               }
            }

            if (job / 100 != 0 && !isNovice(jobz)) {
               return job / 10 % 10 != 0 && job != 501 ? 2 + job % 10 : 1;
            } else {
               return 0;
            }
         }
      } else {
         return 0;
      }
   }

   public static int getJobClass(int job) {
      if (isDualBlade(job)) {
         if (job == 400) {
            return 1;
         }

         if (job == 430) {
            return 9;
         }

         if (job == 431) {
            return 2;
         }

         if (job == 432) {
            return 9;
         }

         if (job == 433) {
            return 3;
         }

         if (job == 434) {
            return 4;
         }
      }

      if (job / 100 != 0 && !isNovice(job)) {
         return job / 10 % 10 != 0 && job != 501 ? 2 + job % 10 : 1;
      } else {
         return 0;
      }
   }

   public static boolean isNovice(int job) {
      return job == 0
         || job == 1
         || job == 1000
         || job == 2000
         || job == 2001
         || job == 2002
         || job == 2003
         || job == 2004
         || job == 2005
         || job == 3000
         || job == 3001
         || job == 3002
         || job == 5000
         || job == 6000
         || job == 6001
         || job == 6002
         || job == 6003
         || job == 10000
         || job == 14000
         || job == 15000
         || job == 15001
         || job == 15002
         || job == 16000
         || job == 16001;
   }

   public static boolean isForceRespawn(int mapid) {
      switch (mapid) {
         case 103000800:
         case 925100100:
            return true;
         default:
            return mapid / 100000 == 9800 && (mapid % 10 == 1 || mapid % 1000 == 100);
      }
   }

   public static int getCustomSpawnID(int summoner, int def) {
      switch (summoner) {
         case 9400589:
         case 9400748:
            return 9400706;
         default:
            return def;
      }
   }

   public static boolean canForfeit(int questid) {
      switch (questid) {
         case 20000:
         case 20010:
         case 20015:
         case 20020:
         case 38151:
         case 38152:
         case 38153:
         case 38154:
         case 38155:
         case 38156:
         case 39820:
         case 39821:
         case 39822:
         case 39823:
         case 39824:
         case 39825:
         case 39923:
         case 39924:
         case 39925:
         case 39926:
         case 39927:
         case 39928:
            return false;
         default:
            return true;
      }
   }

   public static double getAttackRange(SecondaryStatEffect def, int rangeInc) {
      double defRange = (400.0 + rangeInc) * (400.0 + rangeInc);
      if (def != null) {
         defRange += def.getMaxDistanceSq() + def.getRange() * def.getRange();
      }

      return defRange + 120000.0;
   }

   public static double getAttackRange(Point lt, Point rb) {
      double defRange = 160000.0;
      int maxX = Math.max(Math.abs(lt == null ? 0 : lt.x), Math.abs(rb == null ? 0 : rb.x));
      int maxY = Math.max(Math.abs(lt == null ? 0 : lt.y), Math.abs(rb == null ? 0 : rb.y));
      defRange += maxX * maxX + maxY * maxY;
      return defRange + 120000.0;
   }

   public static int getLowestPrice(int itemId) {
      switch (itemId) {
         case 2340000:
         case 2530000:
         case 2531000:
            return 50000000;
         default:
            return -1;
      }
   }

   public static boolean isNoDelaySkill(int skillId) {
      return skillId == 5100015
         || skillId == 5110014
         || skillId == 5120018
         || skillId == 21101003
         || skillId == 15100004
         || skillId == 33101004
         || skillId == 32111010
         || skillId == 2111007
         || skillId == 2211007
         || skillId == 2311007
         || skillId == 32121003
         || skillId == 35121005
         || skillId == 35111004
         || skillId == 35121013
         || skillId == 35121003
         || skillId == 22150004
         || skillId == 22181004
         || skillId == 11101002
         || skillId == 13101002
         || skillId == 24121000
         || skillId == 25100001
         || skillId == 25100002
         || skillId == 3121013
         || skillId == 95001000
         || skillId >= 400041016 && skillId <= 400041018;
   }

   public static boolean isAntiRepeatSkill_(int skillId) {
      switch (skillId) {
         case 2100010:
         case 2101010:
         case 2111007:
         case 2120013:
         case 2121054:
         case 2201009:
         case 2211007:
         case 2214002:
         case 2220014:
         case 2301002:
         case 2311007:
         case 2321001:
         case 2321007:
         case 2341000:
         case 2341001:
         case 3100010:
         case 3111013:
         case 4100012:
         case 4120019:
         case 4221052:
         case 4331003:
         case 4341002:
         case 4341052:
         case 4341054:
         case 5221022:
         case 11121055:
         case 11121056:
         case 12100029:
         case 12111007:
         case 12120011:
         case 12120012:
         case 12121001:
         case 13100022:
         case 13101022:
         case 13110022:
         case 13110027:
         case 13120003:
         case 13120010:
         case 13121054:
         case 14000027:
         case 14000028:
         case 14000029:
         case 14001027:
         case 14110033:
         case 14110034:
         case 14121003:
         case 21001008:
         case 21121057:
         case 21121068:
         case 22110023:
         case 22110025:
         case 22140022:
         case 22140024:
         case 22141017:
         case 22161005:
         case 22170066:
         case 22170093:
         case 22170094:
         case 22171063:
         case 22171080:
         case 22171083:
         case 24120055:
         case 25111206:
         case 31121005:
         case 31221014:
         case 31241001:
         case 32110020:
         case 32111010:
         case 32111016:
         case 32121011:
         case 33001007:
         case 33111013:
         case 33121016:
         case 33121214:
         case 35001002:
         case 35101002:
         case 35110017:
         case 35111003:
         case 35111004:
         case 35121003:
         case 35121013:
         case 35121016:
         case 35121052:
         case 36001005:
         case 36110004:
         case 61101002:
         case 61110211:
         case 61120007:
         case 61120018:
         case 61121217:
         case 63101004:
         case 63101005:
         case 63101006:
         case 63111003:
         case 63111004:
         case 63111005:
         case 63111103:
         case 63111104:
         case 63111105:
         case 63111106:
         case 63121041:
         case 63121042:
         case 63121102:
         case 63121103:
         case 63121140:
         case 63121141:
         case 64001010:
         case 64121055:
         case 65120011:
         case 80001762:
         case 80002633:
         case 80002634:
         case 80003017:
         case 142120002:
         case 142121005:
         case 151001001:
         case 151111002:
         case 151111003:
         case 151121003:
         case 151121041:
         case 152110004:
         case 152120008:
         case 152120016:
         case 152120017:
         case 152121007:
         case 155100009:
         case 155121004:
         case 155121006:
         case 155121306:
         case 164101004:
         case 164120007:
         case 164121042:
         case 400001010:
         case 400001011:
         case 400001018:
         case 400001037:
         case 400001038:
         case 400010000:
         case 400010010:
         case 400011000:
         case 400011006:
         case 400011007:
         case 400011008:
         case 400011009:
         case 400011017:
         case 400011018:
         case 400011019:
         case 400011020:
         case 400011022:
         case 400011023:
         case 400011039:
         case 400011040:
         case 400011041:
         case 400011042:
         case 400011043:
         case 400011044:
         case 400011045:
         case 400011046:
         case 400011047:
         case 400011050:
         case 400011052:
         case 400011053:
         case 400011056:
         case 400011065:
         case 400011072:
         case 400011073:
         case 400011074:
         case 400011075:
         case 400011076:
         case 400011083:
         case 400011084:
         case 400011085:
         case 400011088:
         case 400011089:
         case 400011102:
         case 400011108:
         case 400011109:
         case 400011110:
         case 400011111:
         case 400011116:
         case 400011117:
         case 400011118:
         case 400011119:
         case 400011120:
         case 400011122:
         case 400011123:
         case 400011124:
         case 400011125:
         case 400011126:
         case 400011127:
         case 400011129:
         case 400011130:
         case 400011132:
         case 400011133:
         case 400011136:
         case 400011137:
         case 400020002:
         case 400020009:
         case 400020010:
         case 400020011:
         case 400020051:
         case 400021001:
         case 400021004:
         case 400021007:
         case 400021008:
         case 400021009:
         case 400021010:
         case 400021011:
         case 400021028:
         case 400021029:
         case 400021031:
         case 400021041:
         case 400021042:
         case 400021043:
         case 400021044:
         case 400021045:
         case 400021046:
         case 400021047:
         case 400021049:
         case 400021050:
         case 400021055:
         case 400021063:
         case 400021064:
         case 400021065:
         case 400021086:
         case 400021092:
         case 400021094:
         case 400021096:
         case 400021097:
         case 400021098:
         case 400021104:
         case 400021105:
         case 400021106:
         case 400021107:
         case 400021108:
         case 400021109:
         case 400021110:
         case 400021112:
         case 400030002:
         case 400031000:
         case 400031001:
         case 400031002:
         case 400031007:
         case 400031008:
         case 400031009:
         case 400031010:
         case 400031011:
         case 400031012:
         case 400031013:
         case 400031014:
         case 400031016:
         case 400031018:
         case 400031019:
         case 400031020:
         case 400031021:
         case 400031022:
         case 400031029:
         case 400031031:
         case 400031033:
         case 400031036:
         case 400031037:
         case 400031039:
         case 400031040:
         case 400031054:
         case 400031056:
         case 400031061:
         case 400031062:
         case 400031063:
         case 400031064:
         case 400031065:
         case 400031066:
         case 400040008:
         case 400041002:
         case 400041003:
         case 400041004:
         case 400041005:
         case 400041007:
         case 400041008:
         case 400041010:
         case 400041019:
         case 400041020:
         case 400041021:
         case 400041023:
         case 400041024:
         case 400041029:
         case 400041031:
         case 400041033:
         case 400041034:
         case 400041035:
         case 400041036:
         case 400041038:
         case 400041039:
         case 400041042:
         case 400041043:
         case 400041050:
         case 400041053:
         case 400041056:
         case 400041057:
         case 400041058:
         case 400041059:
         case 400041060:
         case 400041061:
         case 400041062:
         case 400041063:
         case 400041064:
         case 400041065:
         case 400041066:
         case 400041067:
         case 400041070:
         case 400041071:
         case 400041072:
         case 400041073:
         case 400041074:
         case 400041076:
         case 400041077:
         case 400041078:
         case 400041079:
         case 400050238:
         case 400051003:
         case 400051004:
         case 400051005:
         case 400051006:
         case 400051007:
         case 400051008:
         case 400051011:
         case 400051013:
         case 400051015:
         case 400051017:
         case 400051018:
         case 400051019:
         case 400051020:
         case 400051025:
         case 400051026:
         case 400051039:
         case 400051041:
         case 400051049:
         case 400051050:
         case 400051068:
         case 400051069:
         case 400051070:
         case 400051071:
         case 400051072:
         case 400051075:
         case 400051076:
         case 400051077:
         case 400051080:
         case 400051081:
         case 500061010:
         case 500061029:
         case 500061030:
         case 500061031:
         case 500061032:
         case 500061046:
         case 500061047:
         case 500061048:
         case 500061049:
         case 500061058:
         case 500061059:
         case 500061060:
            return true;
         default:
            return false;
      }
   }

   public static boolean isNoSpawn(int mapID) {
      return mapID == 809040100
         || mapID == 925020010
         || mapID == 925020011
         || mapID == 925020012
         || mapID == 925020013
         || mapID == 925020014
         || mapID == 980010000
         || mapID == 980010100
         || mapID == 980010200
         || mapID == 980010300
         || mapID == 980010020;
   }

   public static short getSlotMax(int itemId) {
      if (DBConfig.isGanglim) {
         switch (itemId) {
            case 40:
            case 2000004:
            case 2000005:
            case 2000019:
            case 2003550:
            case 2003551:
            case 2003575:
            case 2023072:
            case 2023658:
            case 2023659:
            case 2023660:
            case 2023661:
            case 2023662:
            case 2023663:
            case 2023664:
            case 2023665:
            case 2023666:
            case 2046251:
            case 2046831:
            case 2046832:
            case 2046970:
            case 2046981:
            case 2047810:
            case 2048226:
            case 2048716:
            case 2048717:
            case 2048724:
            case 2048745:
            case 2048753:
            case 2049153:
            case 2049370:
            case 2049372:
            case 2049704:
            case 2430016:
            case 2430026:
            case 2430027:
            case 2430658:
            case 2431341:
            case 2431661:
            case 2431662:
            case 2431710:
            case 2431753:
            case 2431940:
            case 2431964:
            case 2432423:
            case 2433446:
            case 2433515:
            case 2433591:
            case 2433592:
            case 2433979:
            case 2434851:
            case 2435122:
            case 2435719:
            case 2435748:
            case 2435885:
            case 2437121:
            case 2437157:
            case 2437158:
            case 2437478:
            case 2437760:
            case 2438145:
            case 2439653:
            case 2450042:
            case 2450054:
            case 2450064:
            case 2450134:
            case 2450147:
            case 2450148:
            case 2450149:
            case 2538000:
            case 2630127:
            case 2630133:
            case 2630281:
            case 2630291:
            case 2630437:
            case 2630442:
            case 2630755:
            case 2630782:
            case 2633336:
            case 2633616:
            case 2633927:
            case 2711003:
            case 2711004:
            case 2711005:
            case 2711012:
            case 3993000:
            case 3993002:
            case 3993003:
            case 3996007:
            case 4000006:
            case 4000094:
            case 4000101:
            case 4000178:
            case 4000190:
            case 4000220:
            case 4000439:
            case 4000620:
            case 4000896:
            case 4000965:
            case 4000979:
            case 4001168:
            case 4001209:
            case 4001715:
            case 4001786:
            case 4001832:
            case 4001842:
            case 4001843:
            case 4001868:
            case 4001869:
            case 4001878:
            case 4003002:
            case 4009005:
            case 4009155:
            case 4009239:
            case 4010000:
            case 4010001:
            case 4021016:
            case 4021031:
            case 4021037:
            case 4031213:
            case 4031227:
            case 4031306:
            case 4031307:
            case 4031311:
            case 4031457:
            case 4031466:
            case 4031569:
            case 4031788:
            case 4031831:
            case 4031838:
            case 4033114:
            case 4033151:
            case 4033172:
            case 4033338:
            case 4033884:
            case 4033885:
            case 4033891:
            case 4033892:
            case 4034181:
            case 4034271:
            case 4034803:
            case 4034809:
            case 4034922:
            case 4036068:
            case 4036444:
            case 4036531:
            case 4036573:
            case 4162009:
            case 4260003:
            case 4260004:
            case 4260005:
            case 4310001:
            case 4310027:
            case 4310029:
            case 4310034:
            case 4310036:
            case 4310038:
            case 4310048:
            case 4310057:
            case 4310059:
            case 4310061:
            case 4310063:
            case 4310065:
            case 4310085:
            case 4310086:
            case 4310113:
            case 4310129:
            case 4310153:
            case 4310198:
            case 4310229:
            case 4310237:
            case 4310261:
            case 4310266:
            case 4310291:
            case 5060048:
            case 5062005:
            case 5062009:
            case 5062010:
            case 5062500:
            case 5062503:
            case 5068300:
            case 5068301:
            case 5068302:
            case 5068303:
            case 5068304:
            case 5133000:
            case 5220000:
            case 5220010:
            case 5220013:
            case 5220020:
            case 5520001:
               return 30000;
            default:
               return 0;
         }
      } else {
         switch (itemId) {
            case 2023658:
            case 2023659:
            case 2023660:
            case 2023661:
            case 2023662:
            case 2023663:
            case 2023664:
            case 2023665:
            case 2023666:
            case 2432098:
            case 2434560:
            case 2436018:
            case 2439259:
            case 2439604:
            case 2439605:
            case 2450147:
            case 2450148:
            case 2450149:
            case 2631879:
            case 2634162:
            case 3993000:
            case 3993002:
            case 3993003:
            case 4001168:
            case 4031306:
            case 4031307:
               return 100;
            case 2048758:
            case 2048759:
            case 2048766:
            case 2633349:
               return 999;
            case 2450155:
               return 100;
            case 4030003:
            case 4030004:
            case 4030005:
            case 5520001:
               return 1;
            case 5121060:
               return 10;
            case 5220010:
            case 5220013:
               return 1000;
            case 5220020:
               return 2000;
            default:
               return 0;
         }
      }
   }

   public static boolean isDropRestricted(int itemId) {
      return itemId == 3012000 || itemId == 4030004 || itemId == 1052098 || itemId == 1052202;
   }

   public static boolean isPickupRestricted(int itemId) {
      return itemId == 4030003 || itemId == 4030004;
   }

   public static short getStat(int itemId, int def) {
      switch (itemId) {
         case 1002419:
            return 5;
         case 1002959:
            return 25;
         case 1122121:
            return 7;
         case 1142002:
            return 10;
         default:
            return (short)def;
      }
   }

   public static short getHpMp(int itemId, int def) {
      switch (itemId) {
         case 1002959:
         case 1142002:
            return 1000;
         case 1122121:
            return 500;
         default:
            return (short)def;
      }
   }

   public static short getATK(int itemId, int def) {
      switch (itemId) {
         case 1002959:
            return 4;
         case 1122121:
            return 3;
         case 1142002:
            return 9;
         default:
            return (short)def;
      }
   }

   public static short getDEF(int itemId, int def) {
      switch (itemId) {
         case 1002959:
            return 500;
         case 1122121:
            return 250;
         default:
            return (short)def;
      }
   }

   public static boolean isDojo(int mapId) {
      return mapId >= 925020100 && mapId <= 925023814;
   }

   public static int getPartyPlayHP(int mobID) {
      switch (mobID) {
         case 4250000:
            return 836000;
         case 4250001:
            return 924000;
         case 5250000:
            return 1100000;
         case 5250001:
            return 1276000;
         case 5250002:
            return 1452000;
         case 9400658:
            return 20000000;
         case 9400659:
            return 45000000;
         case 9400660:
            return 30000000;
         case 9400661:
            return 15000000;
         default:
            return 0;
      }
   }

   public static int getPartyPlayEXP(int mobID) {
      switch (mobID) {
         case 4250000:
            return 5770;
         case 4250001:
            return 6160;
         case 5250000:
            return 7100;
         case 5250001:
            return 7975;
         case 5250002:
            return 8800;
         case 9400658:
            return 50000;
         case 9400659:
            return 90000;
         case 9400660:
            return 70000;
         case 9400661:
            return 40000;
         default:
            return 0;
      }
   }

   public static int getPartyPlay(int mapId) {
      switch (mapId) {
         case 300010000:
         case 300010100:
         case 300010200:
         case 300010300:
         case 300010400:
         case 300020000:
         case 300020100:
         case 300020200:
         case 300030000:
         case 683070400:
         case 683070401:
         case 683070402:
            return 25;
         default:
            return 0;
      }
   }

   public static int getPartyPlay(int mapId, int def) {
      int dd = getPartyPlay(mapId);
      return dd > 0 ? dd : def / 2;
   }

   public static boolean isHyperTeleMap(int mapId) {
      for (int i : hyperTele) {
         if (i == mapId) {
            return true;
         }
      }

      return false;
   }

   public static int getCurrentDate() {
      String time = FileoutputUtil.CurrentReadable_Time();
      return Integer.parseInt(time.substring(0, 4) + time.substring(5, 7) + time.substring(8, 10) + time.substring(11, 13));
   }

   public static int getCurrentDate_NoTime() {
      String time = FileoutputUtil.CurrentReadable_Time();
      return Integer.parseInt(time.substring(0, 4) + time.substring(5, 7) + time.substring(8, 10));
   }

   public static String getCurrentDate_NoTime2() {
      String time = FileoutputUtil.CurrentReadable_Time();
      return time.substring(2, 4) + "/" + time.substring(5, 7) + "/" + time.substring(8, 10);
   }

   public static boolean isAngel(int sourceid) {
      return isNovice(sourceid / 10000) && (sourceid % 10000 == 1085 || sourceid % 10000 == 1087 || sourceid % 10000 == 1090 || sourceid % 10000 == 1179);
   }

   public static boolean isStackedLinkSkill(int skill, MapleCharacter chr) {
      int jobId = chr.getJob();
      if (jobId >= 100 && jobId <= 132) {
         return skill == 80002758;
      } else if (jobId >= 200 && jobId <= 232) {
         return skill == 80002762;
      } else if (jobId >= 300 && jobId <= 332) {
         return skill == 80002766;
      } else if (jobId >= 400 && jobId <= 434) {
         return skill == 80002770;
      } else {
         return jobId >= 500 && jobId <= 532 ? skill == 80002774 : skill == 61120008;
      }
   }

   public static boolean isEventMap(int mapid) {
      return mapid >= 109010000 && mapid < 109050000 || mapid > 109050001 && mapid < 109090000 || mapid >= 809040000 && mapid <= 809040100;
   }

   public static boolean isTeamMap(int mapid) {
      return mapid == 109080000
         || mapid == 109080001
         || mapid == 109080002
         || mapid == 109080003
         || mapid == 109080010
         || mapid == 109080011
         || mapid == 109080012
         || mapid == 109090301
         || mapid == 109090302
         || mapid == 109090303
         || mapid == 109090304
         || mapid == 910040100
         || mapid == 960020100
         || mapid == 960020101
         || mapid == 960020102
         || mapid == 960020103
         || mapid == 960030100
         || mapid == 689000000
         || mapid == 689000010;
   }

   public static int getMPByJob(MapleCharacter chr) {
      int delta = 10;
      Equip shield = (Equip)chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-10);
      if (shield != null) {
         delta += shield.getMp();
      }

      Skill bx = SkillFactory.getSkill(80000406);
      int hyperLevel = chr.getHyperStat().getStat().getSkillLevel(80000406);
      if (hyperLevel > 0) {
         SecondaryStatEffect eff = SkillFactory.getSkill(80000406).getEffect(hyperLevel);
         if (eff != null) {
            delta += eff.getMDF();
         }
      }

      if (chr.hasBuffBySkillID(31141501)) {
         delta = Math.min(220, delta);
         return delta + 500;
      } else {
         return Math.min(220, delta);
      }
   }

   public static boolean isSeparatedSp(int job) {
      switch (job) {
         case 0:
         case 100:
         case 110:
         case 111:
         case 112:
         case 120:
         case 121:
         case 122:
         case 130:
         case 131:
         case 132:
         case 200:
         case 210:
         case 211:
         case 212:
         case 220:
         case 221:
         case 222:
         case 230:
         case 231:
         case 232:
         case 300:
         case 301:
         case 310:
         case 311:
         case 312:
         case 320:
         case 321:
         case 322:
         case 330:
         case 331:
         case 332:
         case 400:
         case 410:
         case 411:
         case 412:
         case 420:
         case 421:
         case 422:
         case 430:
         case 431:
         case 432:
         case 433:
         case 434:
         case 500:
         case 501:
         case 510:
         case 511:
         case 512:
         case 520:
         case 521:
         case 522:
         case 530:
         case 531:
         case 532:
         case 1000:
         case 1100:
         case 1110:
         case 1111:
         case 1112:
         case 1200:
         case 1210:
         case 1211:
         case 1212:
         case 1300:
         case 1310:
         case 1311:
         case 1312:
         case 1400:
         case 1410:
         case 1411:
         case 1412:
         case 1500:
         case 1510:
         case 1511:
         case 1512:
         case 2000:
         case 2001:
         case 2002:
         case 2003:
         case 2004:
         case 2005:
         case 2100:
         case 2110:
         case 2111:
         case 2112:
         case 2200:
         case 2210:
         case 2211:
         case 2212:
         case 2213:
         case 2214:
         case 2215:
         case 2216:
         case 2217:
         case 2218:
         case 2300:
         case 2310:
         case 2311:
         case 2312:
         case 2400:
         case 2410:
         case 2411:
         case 2412:
         case 2500:
         case 2510:
         case 2511:
         case 2512:
         case 2700:
         case 2710:
         case 2711:
         case 2712:
         case 3000:
         case 3001:
         case 3002:
         case 3100:
         case 3101:
         case 3110:
         case 3111:
         case 3112:
         case 3120:
         case 3121:
         case 3122:
         case 3200:
         case 3210:
         case 3211:
         case 3212:
         case 3300:
         case 3310:
         case 3311:
         case 3312:
         case 3500:
         case 3510:
         case 3511:
         case 3512:
         case 3600:
         case 3610:
         case 3611:
         case 3612:
         case 3700:
         case 3710:
         case 3711:
         case 3712:
         case 5000:
         case 5100:
         case 5110:
         case 5111:
         case 5112:
         case 6000:
         case 6001:
         case 6002:
         case 6100:
         case 6110:
         case 6111:
         case 6112:
         case 6400:
         case 6410:
         case 6411:
         case 6412:
         case 6500:
         case 6510:
         case 6511:
         case 6512:
         case 10000:
         case 10100:
         case 10110:
         case 10111:
         case 10112:
         case 14000:
         case 14200:
         case 14210:
         case 14211:
         case 14212:
         case 15000:
         case 15001:
         case 15200:
         case 15210:
         case 15211:
         case 15212:
         case 15500:
         case 15510:
         case 15511:
         case 15512:
            return true;
         case 6003:
         case 6300:
         case 6310:
         case 6311:
         case 6312:
            return true;
         case 15002:
         case 15100:
         case 15110:
         case 15111:
         case 15112:
            return true;
         case 15003:
         case 15400:
         case 15410:
         case 15411:
         case 15412:
            return true;
         case 16000:
         case 16400:
         case 16410:
         case 16411:
         case 16412:
            return true;
         case 16001:
         case 16200:
         case 16210:
         case 16211:
         case 16212:
            return true;
         default:
            return false;
      }
   }

   public static int getSkillLevel(int level) {
      if (level >= 70 && level < 120) {
         return 2;
      } else if (level >= 120 && level < 200) {
         return 3;
      } else {
         return level >= 200 ? 4 : 1;
      }
   }

   public static final int getPetBuff(int itemId) {
      switch (itemId) {
         case 5000228:
         case 5000229:
         case 5000230:
         case 5000231:
         case 5000232:
         case 5000233:
            return 7;
         case 5000234:
         case 5000235:
         case 5000236:
         case 5000237:
         case 5000238:
         case 5000241:
         case 5000242:
         case 5000246:
         case 5000247:
         case 5000248:
         case 5000252:
         case 5000253:
         case 5000254:
         case 5000255:
         case 5000259:
         case 5000260:
         case 5000261:
         case 5000262:
         case 5000263:
         case 5000264:
         case 5000265:
         case 5000266:
         case 5000267:
         case 5000268:
         case 5000269:
         case 5000270:
         case 5000274:
         case 5000278:
         case 5000279:
         case 5000280:
         case 5000284:
         case 5000285:
         case 5000286:
         case 5000287:
         case 5000288:
         case 5000289:
         case 5000299:
         case 5000300:
         case 5000301:
         case 5000302:
         case 5000303:
         case 5000304:
         case 5000305:
         case 5000306:
         case 5000307:
         case 5000308:
         case 5000312:
         case 5000313:
         case 5000314:
         case 5000315:
         case 5000316:
         case 5000317:
         case 5000318:
         case 5000319:
         case 5000323:
         case 5000324:
         case 5000325:
         case 5000326:
         case 5000327:
         case 5000328:
         case 5000329:
         default:
            return 0;
         case 5000239:
            return 13;
         case 5000240:
            return 17;
         case 5000243:
         case 5000244:
         case 5000245:
            return 20;
         case 5000249:
         case 5000250:
         case 5000251:
            return 26;
         case 5000256:
         case 5000257:
         case 5000258:
            return 29;
         case 5000271:
         case 5000272:
         case 5000273:
            return 56;
         case 5000275:
         case 5000276:
         case 5000277:
            return 63;
         case 5000281:
         case 5000282:
         case 5000283:
            return 73;
         case 5000290:
         case 5000291:
         case 5000292:
            return 77;
         case 5000293:
         case 5000294:
         case 5000295:
            return 81;
         case 5000296:
         case 5000297:
         case 5000298:
            return 98;
         case 5000309:
         case 5000310:
         case 5000311:
            return 101;
         case 5000320:
         case 5000321:
         case 5000322:
            return 111;
         case 5000330:
         case 5000331:
         case 5000332:
            return 120;
      }
   }

   public static boolean isDropEffect(int effect) {
      return effect >= 1 && effect <= 4;
   }

   public static int[] getInnerSkillbyRank(int rank) {
      if (rank == 0) {
         return rankC;
      } else if (rank == 1) {
         return rankB;
      } else if (rank == 2) {
         return rankA;
      } else {
         return rank == 3 ? rankS : null;
      }
   }

   public static int getRankByInnerSkillID(int skillID) {
      for (int id : rankS) {
         if (id == skillID) {
            return 3;
         }
      }

      for (int idx : rankA) {
         if (idx == skillID) {
            return 2;
         }
      }

      for (int idxx : rankB) {
         if (idxx == skillID) {
            return 1;
         }
      }

      for (int idxxx : rankC) {
         if (idxxx == skillID) {
            return 0;
         }
      }

      return 0;
   }

   public static long getItemReleaseCost(int itemID) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      int reqLevel = ii.getReqLevel(itemID);
      if (reqLevel <= 30) {
         return 0L;
      } else if (reqLevel <= 70) {
         return (long)(reqLevel * reqLevel * 0.5);
      } else {
         return reqLevel > 120 ? reqLevel * reqLevel * 20 : (int)(reqLevel * reqLevel * 2.5);
      }
   }

   public static long getMagnifyPrice(Equip eq) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (!ii.getEquipStats(eq.getItemId()).containsKey("reqLevel")) {
         return -1L;
      } else {
         int level = ii.getEquipStats(eq.getItemId()).get("reqLevel");
         int v1 = 0;
         if (level > 120) {
            v1 = 20;
         } else if (level > 70) {
            v1 = 5;
         } else if (level > 30) {
            v1 = 1;
         }

         double v2 = level;
         int v3 = 2;
         double v4 = 0.5;

         while (true) {
            if ((v3 & 1) != 0) {
               v4 *= v2;
            }

            v3 >>= 1;
            if (v3 == 0) {
               int v5 = (int)Math.ceil(v4);
               return (v1 * v5 <= 0 ? 1 : 0) - 1 & v1 * v5;
            }

            v2 *= v2;
         }
      }
   }

   public static final boolean isSuperior(int itemId) {
      return itemId >= 1102471 && itemId <= 1102485
         || itemId >= 1072732 && itemId <= 1072747
         || itemId >= 1132164 && itemId <= 1132178
         || itemId >= 1122241 && itemId <= 1122245
         || itemId >= 1082543 && itemId <= 1082547;
   }

   public static int getOptionType(int itemId) {
      int id = itemId / 10000;
      if (isWeapon(itemId) || itemId / 1000 == 1099) {
         return 10;
      } else if (id == 109 || id == 110 || id == 113) {
         return 20;
      } else if (isAccessory(itemId)) {
         return 40;
      } else if (id == 100) {
         return 51;
      } else if (id == 104 || id == 106) {
         return 52;
      } else if (id == 105) {
         return 53;
      } else if (id == 108) {
         return 54;
      } else {
         return id == 107 ? 55 : 0;
      }
   }

   public static boolean isAngelicBlessBuffEffectItem(int skill) {
      switch (skill) {
         case 2022746:
         case 2022747:
         case 2022764:
         case 2022823:
            return true;
         default:
            return false;
      }
   }

   public static int getRandomProfessionReactorByRank(int rank) {
      int base1 = 100000;
      int base2 = 200000;
      if (Randomizer.nextBoolean()) {
         if (rank == 1) {
            base1 += Randomizer.rand(0, 7);
         } else if (rank == 2) {
            base1 += Randomizer.rand(4, 9);
         } else if (rank == 3) {
            if (Randomizer.rand(0, 4) == 1) {
               base1 += 11;
            } else {
               base1 += Randomizer.rand(0, 9);
            }
         }

         return base1;
      } else {
         if (rank == 1) {
            base2 += Randomizer.rand(0, 7);
         } else if (rank == 2) {
            base2 += Randomizer.rand(4, 9);
         } else if (rank == 3) {
            if (Randomizer.rand(0, 6) == 1) {
               base2 += 11;
            } else {
               base2 += Randomizer.rand(0, 9);
            }
         }

         return base2;
      }
   }

   public static final boolean AlphaSlot(short slot) {
      switch (slot) {
         case -111:
         case -109:
         case -108:
         case -107:
         case -106:
         case -105:
         case -104:
         case -103:
         case -102:
         case -101:
            return true;
         case -110:
         default:
            return false;
      }
   }

   public static final boolean BetaSlot(short slot) {
      return slot >= -1509 && slot <= -1500;
   }

   public static final short linkedZeroSlot(short slot) {
      switch (slot) {
         case -1509:
            return -107;
         case -1508:
            return -106;
         case -1507:
            return -111;
         case -1506:
            return -108;
         case -1505:
            return -105;
         case -1504:
            return -109;
         case -1503:
            return -104;
         case -1502:
            return -102;
         case -1501:
            return -101;
         case -1500:
            return -103;
         case -111:
            return -1507;
         case -109:
            return -1504;
         case -108:
            return -1506;
         case -107:
            return -1509;
         case -106:
            return -1508;
         case -105:
            return -1505;
         case -104:
            return -1503;
         case -103:
            return -1500;
         case -102:
            return -1502;
         case -101:
            return -1501;
         default:
            return 0;
      }
   }

   public static int getTriFling(int job) {
      switch (job) {
         case 1310:
            return 13100022;
         case 1311:
            return 13110022;
         case 1312:
            return 13120003;
         default:
            return 0;
      }
   }

   public static boolean isSpecialBuff(SecondaryStatFlag stat) {
      switch (stat) {
         case DotHealMPPerSecondR:
         case ShadowPartner:
         case EnhancedPAD:
         case EnhancedMaxHP:
         case EnhancedMaxMP:
         case EnhancedDEF:
         case PowerTransferGauge:
            return true;
         default:
            return false;
      }
   }

   public static int getNumSteal(int jobNum) {
      switch (jobNum) {
         case 1:
         case 2:
            return 4;
         case 3:
         case 4:
            return 3;
         case 5:
            return 2;
         default:
            return 0;
      }
   }

   public static boolean canSteal(Skill skil) {
      return skil != null
         && !skil.isMovement()
         && !isLinkedAranSkill(skil.getId())
         && skil.getId() % 10000 >= 1000
         && getJobNumber(skil.getId()) > 0
         && skil.getId() < 8000000
         && skil.getEffect(1) != null
         && skil.getEffect(1).getSummonMovementType() == null
         && !skil.getEffect(1).isUnstealable();
   }

   public static int getAndroidType(int itemId) {
      return MapleItemInformationProvider.getInstance().getEquipStats(itemId).containsKey("android")
         ? MapleItemInformationProvider.getInstance().getEquipStats(itemId).get("android")
         : 1;
   }

   public static boolean isExceedAttack(int id) {
      switch (id) {
         case 31011000:
         case 31011004:
         case 31011005:
         case 31011006:
         case 31011007:
         case 31201000:
         case 31201007:
         case 31201008:
         case 31201009:
         case 31201010:
         case 31211000:
         case 31211007:
         case 31211008:
         case 31211009:
         case 31211010:
         case 31221000:
         case 31221009:
         case 31221010:
         case 31221011:
         case 31221012:
            return true;
         default:
            return false;
      }
   }

   public static int isLightSkillsGaugeCheck(int skillid) {
      switch (skillid) {
         case 20041226:
            return 110;
         case 27001100:
            return 123;
         case 27101100:
            return 225;
         case 27101101:
            return 30;
         case 27111100:
            return 155;
         case 27111101:
            return 50;
         case 27121100:
            return 264;
         default:
            return 0;
      }
   }

   public static int isDarkSkillsGaugeCheck(int skillid) {
      switch (skillid) {
         case 27001201:
            return 140;
         case 27101202:
            return 73;
         case 27111202:
            return 210;
         case 27121201:
            return 10;
         case 27121202:
            return 397;
         default:
            return 0;
      }
   }

   public static final String getJobNameById(int job) {
      switch (job) {
         case 0:
            return "초보자";
         case 100:
            return "검사";
         case 110:
            return "파이터";
         case 111:
            return "크루세이더";
         case 112:
            return "히어로";
         case 120:
            return "페이지";
         case 121:
            return "나이트";
         case 122:
            return "팔라딘";
         case 130:
            return "스피어맨";
         case 131:
            return "버서커";
         case 132:
            return "다크나이트";
         case 200:
            return "마법사";
         case 210:
            return "위자드(불,독)";
         case 211:
            return "메이지(불,독)";
         case 212:
            return "아크메이지(불,독)";
         case 220:
            return "위자드(썬,콜)";
         case 221:
            return "메이지(썬,콜)";
         case 222:
            return "아크메이지(썬,콜)";
         case 230:
            return "클레릭";
         case 231:
            return "프리스트";
         case 232:
            return "비숍";
         case 300:
            return "아처";
         case 301:
         case 330:
         case 331:
         case 332:
            return "패스파인더";
         case 310:
            return "헌터";
         case 311:
            return "레인저";
         case 312:
            return "보우마스터";
         case 320:
            return "사수";
         case 321:
            return "저격수";
         case 322:
            return "신궁";
         case 400:
            return "로그";
         case 410:
            return "어쌔신";
         case 411:
            return "허밋";
         case 412:
            return "나이트로드";
         case 420:
            return "시프";
         case 421:
            return "시프마스터";
         case 422:
            return "섀도어";
         case 430:
            return "세미듀어러";
         case 431:
            return "듀어러";
         case 432:
            return "듀얼마스터";
         case 433:
            return "슬래셔";
         case 434:
            return "듀얼블레이더";
         case 500:
            return "해적";
         case 501:
            return "해적(캐논슈터)";
         case 510:
            return "인파이터";
         case 511:
            return "버커니어";
         case 512:
            return "바이퍼";
         case 520:
            return "건슬링거";
         case 521:
            return "발키리";
         case 522:
            return "캡틴";
         case 530:
            return "캐논슈터";
         case 531:
            return "캐논블래스터";
         case 532:
            return "캐논마스터";
         case 800:
            return "매니저";
         case 900:
            return "운영자";
         case 1000:
            return "노블레스";
         case 1100:
         case 1110:
         case 1111:
         case 1112:
            return "소울마스터";
         case 1200:
         case 1210:
         case 1211:
         case 1212:
            return "플레임위자드";
         case 1300:
         case 1310:
         case 1311:
         case 1312:
            return "윈드브레이커";
         case 1400:
         case 1410:
         case 1411:
         case 1412:
            return "나이트워커";
         case 1500:
         case 1510:
         case 1511:
         case 1512:
            return "스트라이커";
         case 2000:
            return "레전드";
         case 2001:
         case 2200:
         case 2210:
         case 2211:
         case 2212:
         case 2213:
         case 2214:
         case 2215:
         case 2216:
         case 2217:
         case 2218:
            return "에반";
         case 2002:
         case 2300:
         case 2310:
         case 2311:
         case 2312:
            return "메르세데스";
         case 2003:
         case 2400:
         case 2410:
         case 2411:
         case 2412:
            return "팬텀";
         case 2004:
         case 2700:
         case 2710:
         case 2711:
         case 2712:
            return "루미너스";
         case 2005:
            return "???";
         case 2100:
         case 2110:
         case 2111:
         case 2112:
            return "아란";
         case 2500:
         case 2510:
         case 2511:
         case 2512:
            return "은월";
         case 3000:
            return "시티즌";
         case 3001:
         case 3100:
         case 3110:
         case 3111:
         case 3112:
            return "데몬슬레이어";
         case 3002:
         case 3600:
         case 3610:
         case 3611:
         case 3612:
            return "제논";
         case 3101:
         case 3120:
         case 3121:
         case 3122:
            return "데몬어벤져";
         case 3200:
         case 3210:
         case 3211:
         case 3212:
            return "배틀메이지";
         case 3300:
         case 3310:
         case 3311:
         case 3312:
            return "와일드헌터";
         case 3500:
         case 3510:
         case 3511:
         case 3512:
            return "메카닉";
         case 3700:
         case 3710:
         case 3711:
         case 3712:
            return "블래스터";
         case 5000:
         case 5100:
         case 5110:
         case 5111:
         case 5112:
            return "미하일";
         case 6000:
         case 6100:
         case 6110:
         case 6111:
         case 6112:
            return "카이저";
         case 6001:
         case 6500:
         case 6510:
         case 6511:
         case 6512:
            return "엔젤릭버스터";
         case 6300:
         case 6310:
         case 6311:
         case 6312:
            return "카인";
         case 6400:
         case 6410:
         case 6411:
         case 6412:
            return "카데나";
         case 10000:
            return "제로JR";
         case 10100:
            return "제로10100";
         case 10110:
            return "제로10110";
         case 10111:
            return "제로10111";
         case 10112:
            return "제로";
         case 13100:
            return "핑크빈";
         case 13500:
            return "예티";
         case 14000:
         case 14200:
         case 14210:
         case 14211:
         case 14212:
            return "키네시스";
         case 15100:
         case 15110:
         case 15111:
         case 15112:
            return "아델";
         case 15200:
         case 15210:
         case 15211:
         case 15212:
            return "일리움";
         case 15500:
         case 15510:
         case 15511:
         case 15512:
            return "아크";
         case 16200:
         case 16210:
         case 16211:
         case 16212:
            return "라라";
         case 16400:
         case 16410:
         case 16411:
         case 16412:
            return "호영";
         default:
            return isKhali(job) ? "칼리" : "알수없음";
      }
   }

   public static boolean isProfessionalSkill(int skillID) {
      return skillID / 1000000 == 92 && skillID % 10000 == 0;
   }

   public static short getSoulName(int soulid) {
      return (short)(soulid - 2591000 + 1);
   }

   public static short getSoulPotential(int soulid) {
      short potential = 0;

      for (int i = 0; i < soulItemid.length; i++) {
         if (soulItemid[i] == soulid) {
            potential = soulPotentials[i];
            break;
         }
      }

      return potential;
   }

   public static boolean isSoulSummonSkill(int skillid) {
      switch (skillid) {
         case 80001266:
         case 80001267:
         case 80001268:
         case 80001269:
         case 80001270:
         case 80001273:
         case 80001280:
         case 80001281:
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
         case 80001684:
         case 80001685:
         case 80001690:
         case 80001691:
         case 80001692:
         case 80001693:
         case 80001695:
         case 80001696:
         case 80001697:
         case 80001806:
         case 80001807:
         case 80001808:
         case 80001984:
         case 80001985:
         case 80002230:
         case 80002231:
         case 80002406:
         case 80002639:
         case 80002641:
            return true;
         default:
            return false;
      }
   }

   public static boolean isIgnoreMasterLevelForCommon(int a1) {
      if (a1 <= 5321006) {
         if (a1 == 5321006) {
            return true;
         } else if (a1 > 4340010) {
            if (a1 > 5220014) {
               return a1 != 5221022 && a1 != 5320007 ? a1 == 5321004 : true;
            } else {
               if (a1 != 5220014) {
                  if (a1 > 5120012) {
                     return a1 == 5220012;
                  }

                  if (a1 < 5120011) {
                     return a1 == 4340012;
                  }
               }

               return true;
            }
         } else if (a1 == 4340010) {
            return true;
         } else if (a1 > 2321010) {
            return a1 != 3210015 && a1 != 4110012 ? a1 == 4210012 : true;
         } else if (a1 == 2321010) {
            return true;
         } else if (a1 > 2121009) {
            return a1 == 2221009;
         } else {
            return a1 != 2121009 && a1 != 1120012 ? a1 == 1320011 : true;
         }
      } else {
         if (a1 > 33120010) {
            if (a1 <= 152120003) {
               if (a1 != 152120003 && a1 != 35120014 && a1 != 51120000) {
                  return a1 == 80001913;
               }

               return true;
            }

            if (a1 > 152121006) {
               return a1 == 152121010;
            }

            if (a1 != 152121006 && (a1 < 152120012 || a1 > 152120013)) {
               return false;
            }
         } else if (a1 != 33120010) {
            if (a1 > 22171069) {
               if (a1 != 23120013 && a1 != 23121008) {
                  return a1 == 23121011;
               }

               return true;
            }

            if (a1 != 22171069) {
               if (a1 > 21120021) {
                  return a1 == 21121008;
               }

               if (a1 < 21120020 && a1 != 21120011) {
                  return a1 == 21120014;
               }

               return true;
            }
         }

         return true;
      }
   }

   public static boolean isRecipe(int a1) {
      int v1 = a1 / 10000 * 10000;
      boolean a = a1 / 1000000 != 92 || a1 % 10000 != 0;
      boolean b = v1 / 1000000 == 92;
      boolean c = v1 % 10000 == 0;
      return a && b && c;
   }

   public static boolean isCommonSkill(int skillId) {
      int v1 = get_skill_root_from_skill(skillId);
      return v1 >= 800000 && v1 <= 800099;
   }

   public static boolean isNoviceSkill(int skillId) {
      int job = get_skill_root_from_skill(skillId);
      return isNovice(job);
   }

   public static boolean isFieldAttackObjSkill(int skillId) {
      int v1 = 0;
      if (skillId >= 0) {
         v1 = get_skill_root_from_skill(skillId);
         return v1 == 9500;
      } else {
         return false;
      }
   }

   public static int get_skill_root_from_skill(int skillId) {
      int result = 0;
      result = skillId / 10000;
      if (skillId / 10000 == 8000) {
         result = skillId / 100;
      }

      return result;
   }

   public static boolean isAddedSPDualAndZeroSkill(int skillId) {
      switch (skillId) {
         case 4311003:
         case 4321006:
         case 4330009:
         case 4331002:
         case 4340007:
         case 4341004:
         case 101000101:
         case 101100101:
         case 101100201:
         case 101110102:
         case 101110200:
         case 101110203:
         case 101120104:
         case 101120204:
            return true;
         default:
            return false;
      }
   }

   public static int getJobLevel(int job) {
      if (isNovice(job) || job % 100 == 0 || job == 501 || job == 3101 || job == 301) {
         return 1;
      } else if (isEvan(job)) {
         return getEvanJobLevel(job);
      } else {
         if (!isDualJob(job)) {
            int a = job % 10;
            if (a <= 2) {
               return a + 2;
            }
         } else {
            int a = job % 10 / 2;
            if (a <= 2) {
               return a + 2;
            }
         }

         return 0;
      }
   }

   public static int getEvanJobLevel(int job) {
      switch (job) {
         case 2200:
         case 2210:
            return 1;
         case 2201:
         case 2202:
         case 2203:
         case 2204:
         case 2205:
         case 2206:
         case 2207:
         case 2208:
         case 2209:
         default:
            return 0;
         case 2211:
         case 2212:
         case 2213:
            return 2;
         case 2214:
         case 2215:
         case 2216:
            return 3;
         case 2217:
         case 2218:
            return 4;
      }
   }

   public static int getJaguarType(MapleCharacter hp) {
      if (hp.getIntNoRecord(111112) == -1) {
         MapleQuestStatus stats = hp.getQuestNoAdd(MapleQuest.getInstance(111112));
         if (stats != null && stats.getStatus() == 1) {
            stats.setCustomData(String.valueOf(0));
         }
      }

      return hp.getIntNoRecord(111112);
   }

   public static boolean isKinesisPsychiclockSkill(int skillId) {
      if (skillId > 142111002) {
         if (skillId < 142120000 || skillId > 142120002 && skillId != 142120014) {
            return false;
         }
      } else if (skillId != 142111002 && skillId != 142100010 && skillId != 142110003 && skillId != 142110015) {
         return false;
      }

      return true;
   }

   public static boolean isDualJob(int job) {
      return job / 10 == 43;
   }

   public static boolean isSkillNeedMasterLevel(int skillID) {
      if (!isIgnoreMasterLevelForCommon(skillID)
         && (skillID / 1000000 != 92 || skillID % 10000 != 0)
         && !isRecipe(skillID)
         && !isCommonSkill(skillID)
         && !isNoviceSkill(skillID)
         && !isFieldAttackObjSkill(skillID)) {
         int job = get_skill_root_from_skill(skillID);
         int jobLevel = getJobLevel(job);
         if (is5thSkill(skillID)) {
            return false;
         } else {
            return jobLevel == 4 && !isZero(job) ? true : isAddedSPDualAndZeroSkill(skillID);
         }
      } else {
         return false;
      }
   }

   public static boolean is5thSkill(int skillID) {
      int job = getSkillRootFromSkill(skillID);
      return job >= 40000 && job <= 40005;
   }

   public static int getDamageSkinNumberByItem(int itemid) {
      for (int i = 0; i < dmgskinitem.length; i++) {
         if (dmgskinitem[i] == itemid) {
            return dmgskinnum[i];
         }
      }

      return -1;
   }

   public static int getDamageSkinItemID(int skinID) {
      for (int i = 0; i < dmgskinnum.length; i++) {
         if (dmgskinnum[i] == skinID) {
            return dmgskinitem[i];
         }
      }

      return 0;
   }

   public static boolean isBlaster(int job) {
      return job >= 3700 && job <= 3712;
   }

   public static int MatrixExp(int level) {
      switch (level) {
         case 1:
            return 50;
         case 2:
            return 89;
         case 3:
            return 149;
         case 4:
            return 221;
         case 5:
            return 306;
         case 6:
            return 404;
         case 7:
            return 514;
         case 8:
            return 638;
         case 9:
            return 774;
         case 10:
            return 922;
         case 11:
            return 1084;
         case 12:
            return 1258;
         case 13:
            return 1445;
         case 14:
            return 1645;
         case 15:
            return 1857;
         case 16:
            return 2083;
         case 17:
            return 2321;
         case 18:
            return 2571;
         case 19:
            return 2835;
         case 20:
            return 3111;
         case 21:
            return 3400;
         case 22:
            return 3702;
         case 23:
            return 4016;
         case 24:
            return 4344;
         default:
            return 4684;
      }
   }

   public static int getExpNeededForArcaneSymbol(int level) {
      return level * level + 11;
   }

   public static int getMesoNeededForArcaneSymbolUpgrade(int level, int itemID) {
      double x;
      double y;
      switch (itemID) {
         case 1712001:
            x = 8.0;
            y = 88.0;
            break;
         case 1712002:
            x = 10.0;
            y = 110.0;
            break;
         case 1712003:
            x = 12.0;
            y = 132.0;
            break;
         case 1712004:
            x = 14.0;
            y = 154.0;
            break;
         case 1712005:
            x = 16.0;
            y = 176.0;
            break;
         default:
            x = 18.0;
            y = 198.0;
      }

      double calc = 0.1 * Math.pow(level, 3.0) + x * Math.pow(level, 2.0) + 1.1 * level + y;
      return (int)Math.floor(calc) * 10000;
   }

   public static int getMesoNeededForAuthenticSymbolUpgrade(int level, int itemID) {
      double x;
      double y;
      switch (itemID) {
         case 1713000:
            x = 106.8;
            y = 264.0;
            break;
         case 1713001:
            x = 123.0;
            y = 300.0;
            break;
         case 1713002:
            x = 139.2;
            y = 336.0;
            break;
         case 1713003:
            x = 155.4;
            y = 372.0;
            break;
         case 1713004:
            x = 171.6;
            y = 408.0;
            break;
         case 1713005:
            x = 187.8;
            y = 444.0;
            break;
         default:
            x = 187.8;
            y = 444.0;
      }

      double calc = -5.4 * Math.pow(level, 3.0) + x * Math.pow(level, 2.0) + y * level;
      return (int)Math.floor(calc) * 100000;
   }

   public static int getExpNeededForAuthenticSymbol(int level) {
      switch (level) {
         case 1:
            return 29;
         case 2:
            return 76;
         case 3:
            return 141;
         case 4:
            return 224;
         case 5:
            return 325;
         case 6:
            return 444;
         case 7:
            return 581;
         case 8:
            return 736;
         case 9:
            return 909;
         case 10:
            return 1100;
         default:
            return 1100;
      }
   }

   public static boolean is_zero_skill(int nSkillID) {
      int v1 = nSkillID / 10000;
      if (nSkillID / 10000 == 8000) {
         v1 = nSkillID / 100;
      }

      return v1 == 10000 || v1 == 10100 || v1 == 10110 || v1 == 10111 || v1 == 10112;
   }

   public static boolean isAffectedAreaSkill(int skillID) {
      boolean v1;
      if (skillID > 35121052) {
         if (skillID == 400020046) {
            return true;
         }

         v1 = skillID == 400020051;
      } else {
         if (skillID == 35121052 || skillID == 33111013 || skillID == 33121012) {
            return true;
         }

         v1 = skillID == 33121016;
      }

      return v1
         || skillID == 131001207
         || skillID == 4121015
         || skillID == 4221006
         || skillID == 51120057
         || skillID == 131001107
         || skillID == 135001012
         || skillID == 152121041
         || skillID == 400001017
         || skillID == 400041041;
   }

   public static boolean isBlasterCanonMasterySkill(int skillid) {
      switch (skillid) {
         case 37001000:
         case 37101000:
         case 37110001:
         case 37110002:
         case 37111000:
         case 37120002:
         case 37120022:
         case 37120024:
         case 37120055:
         case 37120056:
         case 37120057:
         case 37120058:
         case 37120059:
         case 37121003:
         case 37121052:
            return true;
         default:
            return false;
      }
   }

   public static boolean isAeroSwingSkill(int skillID) {
      switch (skillID) {
         case 21110022:
         case 21110023:
         case 21110026:
            return true;
         case 21110024:
         case 21110025:
         default:
            return false;
      }
   }

   public static boolean isKeydownSkillRectMoveXY(int skillID) {
      return skillID == 13111020;
   }

   public static boolean is_unregistered_skill(int nSkillID) {
      if (nSkillID != 0 && nSkillID >= 0) {
         int v1 = nSkillID / 10000;
         if (nSkillID / 10000 == 8000) {
            v1 = nSkillID / 100;
         }

         boolean result;
         if (v1 == 9500) {
            result = false;
         } else {
            result = nSkillID / 10000000 == 9;
         }

         return result;
      } else {
         return false;
      }
   }

   public static int getAdvancedBulletCountHyperSkill(int skillID) {
      if (skillID == 4121013) {
         return 4120051;
      } else {
         return skillID == 5321012 ? 5320051 : 0;
      }
   }

   public static int getAdvancedAttackCountHyperSkill(int skillID) {
      switch (skillID) {
         case 1120017:
         case 1121008:
            return 1120051;
         case 1221009:
            return 1220048;
         case 1221011:
            return 1220050;
         case 2121006:
            return 2120048;
         case 2221006:
            return 2220048;
         case 3121015:
            return 3120048;
         case 3121020:
            return 3120051;
         case 3221017:
            return 3220048;
         case 4221006:
            return 4220048;
         case 4331000:
            return 4340045;
         case 4341009:
            return 4340048;
         case 5121007:
            return 5120048;
         case 5121016:
         case 5121017:
            return 5120051;
         case 5121020:
            return 5120048;
         case 5221016:
            return 5220047;
         case 5320011:
            return 5320043;
         case 5320999:
            return 5320048;
         case 5321004:
            return 5320043;
         case 11121103:
         case 11121203:
            return 11120048;
         case 12000026:
         case 12100028:
         case 12110028:
            return 12120045;
         case 12120010:
            return 12120045;
         case 12120011:
            return 12120046;
         case 13121002:
            return 13120048;
         case 14121002:
            return 14120045;
         case 15111022:
            return 15120045;
         case 15120003:
            return 15120045;
         case 15121002:
            return 15120048;
         case 21110020:
         case 21111021:
            return 21120047;
         case 21120006:
            return 21120049;
         case 21120022:
            return 21120066;
         case 22140023:
            return 22170086;
         case 25121005:
            return 25120148;
         case 31121001:
            return 31120050;
         case 35121016:
            return 35120051;
         case 37110002:
         case 37120001:
            return 37120045;
         case 51120057:
            return 51120058;
         case 51121007:
            return 51120051;
         case 51121008:
            return 51120048;
         case 51121009:
            return 51120058;
         case 61121100:
            return 61120045;
         case 65121007:
         case 65121008:
         case 65121101:
            return 65120051;
         default:
            return 0;
      }
   }

   public static String getMedalName(Item medal) {
      String name = MapleItemInformationProvider.getInstance().getName(medal.getItemId());
      if (name != null && !name.isEmpty()) {
         String ret = name.split("의 훈장")[0];
         return ret.contains("훈장") ? ret.split(" 훈장")[0] : ret;
      } else {
         return "";
      }
   }

   public static boolean isShadowServant(int a1) {
      if (a1 <= 131003017) {
         if (a1 == 131003017) {
            return true;
         }

         boolean v1;
         if (a1 > 14121056) {
            if (a1 == 131001017) {
               return true;
            }

            v1 = a1 == 131002017;
         } else {
            if (a1 == 14121056 || a1 == 14111024 || a1 == 14121054) {
               return true;
            }

            v1 = a1 == 14121055;
         }

         if (v1) {
            return true;
         }
      }

      if (a1 != 400001071 && a1 != 400031007 && a1 != 400041028 && a1 != 500061004 && a1 != 500061046) {
         boolean v1x = false;
         if (a1 <= 500061046) {
            int v2 = a1 - 400031007;
            if (v2 == 0) {
               return true;
            } else {
               int v4 = v2 - 1;
               boolean v3 = v4 == 0;
               return v3 || v4 == 1;
            }
         } else {
            int v4 = a1 - 500061047;
            boolean v3 = v4 == 0;
            return v3 || v4 == 1;
         }
      } else {
         return true;
      }
   }

   public static boolean isNightWalkerThrowingSkill(int skillID) {
      switch (skillID) {
         case 14001020:
         case 14101020:
         case 14101021:
         case 14111005:
         case 14111020:
         case 14111021:
         case 14111022:
         case 14111023:
         case 14121001:
         case 14121002:
         case 14121003:
         case 14141000:
         case 14141001:
         case 14141002:
         case 14141003:
            return true;
         default:
            return false;
      }
   }

   public static boolean isColdSkill(int skillID) {
      switch (skillID) {
         case 2201004:
         case 2201008:
         case 2211002:
         case 2211010:
         case 2221005:
         case 2221007:
         case 2221012:
         case 400021067:
            return true;
         default:
            return false;
      }
   }

   public static int getLinkLevel(int level, boolean isZero) {
      if (isZero) {
         if (level >= 180) {
            return 5;
         }

         if (level >= 160) {
            return 4;
         }

         if (level >= 140) {
            return 3;
         }

         if (level >= 120) {
            return 2;
         }

         if (level >= 100) {
            return 1;
         }
      } else {
         if (level >= 120) {
            return 2;
         }

         if (level >= 70) {
            return 1;
         }
      }

      return 0;
   }

   public static int getDuplicateOfOriginalSkill(int skillID) {
      switch (skillID) {
         case 110:
            return 80000000;
         case 252:
            return 80002759;
         case 253:
            return 80002760;
         case 254:
            return 80002761;
         case 255:
            return 80002763;
         case 256:
            return 80002764;
         case 257:
            return 80002765;
         case 258:
            return 80002767;
         case 259:
            return 80002768;
         case 260:
            return 80002769;
         case 261:
            return 80002771;
         case 262:
            return 80002772;
         case 263:
            return 80002773;
         case 264:
            return 80002775;
         case 265:
            return 80002776;
         case 10000255:
            return 80000066;
         case 10000256:
            return 80000067;
         case 10000257:
            return 80000068;
         case 10000258:
            return 80000069;
         case 10000259:
            return 80000070;
         case 20000297:
            return 80000370;
         case 20010294:
            return 80000369;
         case 20021110:
            return 80001040;
         case 20030204:
            return 80000002;
         case 20040218:
            return 80000005;
         case 20050286:
            return 80000169;
         case 30000074:
            return 80000333;
         case 30000075:
            return 80000334;
         case 30000076:
            return 80000335;
         case 30000077:
            return 80000378;
         case 30010112:
            return 80000001;
         case 30010241:
            return 80000050;
         case 30020233:
            return 80000047;
         case 50001214:
            return 80001140;
         case 60000222:
            return 80000006;
         case 60011219:
            return 80001155;
         case 60020218:
            return 80000261;
         case 60030241:
            return 80003015;
         case 100000271:
            return 80000110;
         case 140000292:
            return 80000188;
         case 150000017:
            return 80000268;
         case 150010241:
            return 80000514;
         case 150020241:
            return 80002857;
         case 150030241:
            return 80003224;
         case 160000001:
            return 80000609;
         case 160010001:
            return 80003058;
         default:
            return 0;
      }
   }

   public static boolean isLinkSkill(int skillID) {
      switch (skillID) {
         case 80000000:
         case 80000001:
         case 80000002:
         case 80000005:
         case 80000006:
         case 80000047:
         case 80000050:
         case 80000055:
         case 80000066:
         case 80000067:
         case 80000068:
         case 80000069:
         case 80000070:
         case 80000110:
         case 80000169:
         case 80000188:
         case 80000261:
         case 80000268:
         case 80000329:
         case 80000333:
         case 80000334:
         case 80000335:
         case 80000369:
         case 80000370:
         case 80000378:
         case 80000514:
         case 80000609:
         case 80001040:
         case 80001140:
         case 80001155:
         case 80002758:
         case 80002759:
         case 80002760:
         case 80002761:
         case 80002762:
         case 80002763:
         case 80002764:
         case 80002765:
         case 80002766:
         case 80002767:
         case 80002768:
         case 80002769:
         case 80002770:
         case 80002771:
         case 80002772:
         case 80002773:
         case 80002774:
         case 80002775:
         case 80002776:
         case 80002857:
         case 80003015:
         case 80003058:
         case 80003224:
            return true;
         default:
            return false;
      }
   }

   public static int getBattlePvPBasicAttackACtion(int skillID) {
      if (skillID == 80001647) {
         return Randomizer.rand(1087, 1088);
      } else if (skillID == 80001648) {
         return Randomizer.rand(1101, 1102);
      } else if (skillID == 80001649) {
         return Randomizer.rand(1113, 1114);
      } else {
         return skillID == 80001651 ? 1126 : 1150;
      }
   }

   public static boolean isConvergenceSkill(int skillID) {
      switch (skillID) {
         case 22110014:
         case 22110024:
         case 22110025:
         case 22140014:
         case 22140015:
         case 22140023:
         case 22140024:
         case 22170065:
         case 22170066:
         case 22170067:
         case 22170094:
         case 80001939:
            return true;
         default:
            return false;
      }
   }

   public static boolean isDischargeSkill(int skillID) {
      switch (skillID) {
         case 3011004:
         case 3300002:
         case 3321003:
            return true;
         default:
            return false;
      }
   }

   public static boolean isBlastSkill(int skillID) {
      switch (skillID) {
         case 3301003:
         case 3301004:
         case 3310001:
         case 3310004:
         case 3311013:
         case 3321004:
         case 3321005:
         case 3341000:
         case 3341001:
         case 3341002:
         case 3341003:
            return true;
         default:
            return false;
      }
   }

   public static boolean isTransitionSkill(int skillID) {
      switch (skillID) {
         case 3311002:
         case 3311003:
         case 3320001:
         case 3321006:
         case 3321007:
            return true;
         default:
            return false;
      }
   }

   public static boolean isAncientSkill(int skillID) {
      switch (skillID) {
         case 3301008:
         case 3311010:
         case 3321012:
            return true;
         default:
            return false;
      }
   }

   public static boolean isEnchantSkill(int skillID) {
      switch (skillID) {
         case 3321036:
         case 3321037:
         case 3321038:
         case 3321039:
         case 3321040:
         case 400031038:
         case 400031039:
         case 400031040:
         case 400031042:
         case 400031043:
         case 400031047:
         case 400031048:
         case 400031049:
         case 400031050:
         case 400031051:
            return true;
         default:
            return false;
      }
   }

   public static boolean IsEligibleExOptionPart(int itemID, int level, ExItemType exItemType) {
      switch (exItemType) {
         case Pdd:
         case Mdd:
         case Acc:
         case Eva:
         case IMdR:
            return false;
         case BdR:
            if (isWeapon(itemID)) {
               return level >= 90;
            }

            return false;
         case DamR:
            return isWeapon(itemID);
         case StatR:
            return level >= 70;
         default:
            return true;
      }
   }

   public static boolean isZeniaScroll(int itemID) {
      switch (itemID) {
         case 2046076:
         case 2046077:
         case 2046150:
         case 2046251:
         case 2046340:
         case 2046341:
         case 2046831:
         case 2046832:
         case 2046970:
         case 2046981:
         case 2046991:
         case 2046992:
         case 2047810:
         case 2047814:
         case 2047844:
         case 2048047:
         case 2048048:
         case 2048049:
         case 2048050:
         case 2640024:
         case 2640025:
            return true;
         default:
            return false;
      }
   }

   public static boolean IsExNewScroll(int itemID) {
      switch (itemID) {
         case 2048716:
         case 2048717:
         case 2048718:
         case 2048719:
         case 2048720:
         case 2048721:
         case 2048722:
         case 2048723:
         case 2048724:
         case 2048726:
         case 2048727:
         case 2048738:
         case 2048753:
         case 2048755:
         case 2048757:
         case 2048758:
         case 2048759:
         case 2048766:
         case 2048767:
         case 2048768:
            return true;
         case 2048725:
         case 2048728:
         case 2048729:
         case 2048730:
         case 2048731:
         case 2048732:
         case 2048733:
         case 2048734:
         case 2048735:
         case 2048736:
         case 2048737:
            return false;
         case 2048739:
         case 2048740:
         case 2048741:
         case 2048742:
         case 2048743:
         case 2048744:
         case 2048745:
         case 2048746:
         case 2048747:
         case 2048748:
         case 2048749:
         case 2048750:
         case 2048751:
         case 2048752:
         case 2048754:
         case 2048756:
         case 2048760:
         case 2048761:
         case 2048762:
         case 2048763:
         case 2048764:
         case 2048765:
         default:
            return itemID - 2048745 <= 2;
      }
   }

   public static boolean IsPowerfulRebirthFlame(int itemID) {
      if (itemID == 2048759) {
         return true;
      } else {
         if (itemID <= 2048720) {
            if (itemID != 2048716 && itemID != 2048720) {
               return false;
            }
         } else if (itemID != 2048724 && itemID != 2048745) {
            return false;
         }

         return true;
      }
   }

   public static boolean IsEternalRebirthFlame(int itemID) {
      if (itemID == 2048757) {
         return true;
      } else if (itemID == 2048758) {
         return true;
      } else {
         if (itemID <= 2048721) {
            if (itemID != 2048717 && itemID != 2048721) {
               return false;
            }
         } else if (itemID != 2048723 && itemID - 2048746 > 1) {
            return false;
         }

         return true;
      }
   }

   public static boolean IsBlackRebirthFlame(int itemID) {
      switch (itemID) {
         case 2048753:
         case 2048755:
         case 2048766:
         case 2048767:
         case 2048768:
            return true;
         default:
            return false;
      }
   }

   public static int getStarLimit(boolean isSuperial, int rLevel) {
      if (isSuperial) {
         if (rLevel <= 95) {
            return 3;
         } else if (rLevel <= 107) {
            return 5;
         } else if (rLevel <= 117) {
            return 8;
         } else if (rLevel <= 127) {
            return 10;
         } else {
            return rLevel <= 137 ? 12 : 15;
         }
      } else if (rLevel <= 95) {
         return 5;
      } else if (rLevel <= 107) {
         return 8;
      } else if (rLevel <= 117) {
         return 10;
      } else if (rLevel <= 127) {
         return 15;
      } else {
         return rLevel <= 137 ? 20 : 25;
      }
   }

   public static boolean isFaceAccessory(int ID) {
      return ID / 10000 == 101;
   }

   public static boolean isEyeAccessory(int ID) {
      return ID / 10000 == 102;
   }

   public static boolean isEarAccessory(int ID) {
      return ID / 10000 == 103;
   }

   public static boolean isPendant(int ID) {
      return ID / 10000 == 112;
   }

   public static boolean isHontalePendant(int id) {
      return id == 1122000 || id == 1122076 || id == 1122278;
   }

   public static boolean isPocket(int ID) {
      return ID / 10000 == 116;
   }

   public static boolean isBadge(int ID) {
      return ID / 10000 == 118;
   }

   public static boolean isBelt(int ID) {
      return ID / 10000 == 113;
   }

   public static boolean isShoulder(int ID) {
      return ID / 10000 == 115;
   }

   public static boolean isCap(int ID) {
      return ID / 10000 == 100;
   }

   public static boolean isShoes(int ID) {
      return ID / 10000 == 107;
   }

   public static boolean isCoat(int ID) {
      return ID / 10000 == 104;
   }

   public static boolean isPants(int ID) {
      return ID / 10000 == 106;
   }

   public static boolean isGlove(int ID) {
      return ID / 10000 == 108;
   }

   public static boolean isEmblem(int ID) {
      return ID / 10000 == 119;
   }

   public static boolean isShieldBodyPart(int ID) {
      return ID / 10000 != 109 && ID / 10000 != 134 && ID / 10000 != 135 ? ID / 10000 == 156 : true;
   }

   public static boolean isWeaponSticker(int ID) {
      return ID / 100000 == 17;
   }

   public static boolean isFace(int ID) {
      return ID / 10000 == 2;
   }

   public static boolean isHair(int ID) {
      return ID / 10000 != 3 ? ID / 10000 == 4 : true;
   }

   public static boolean isLongcoat(int ID) {
      return ID / 10000 == 105;
   }

   public static int jobCategory(int job) {
      if (job == 10000900) {
         return -1;
      } else if (isLuminous(job) || isKinesis(job)) {
         return 2;
      } else if (isXenon(job)) {
         return 4;
      } else {
         return isBlaster((short)job) ? 1 : job % 1000 / 100;
      }
   }

   public static boolean isEmberExplosionSkill(int skillID) {
      return skillID == 12121002 || skillID == 12121052 || skillID - 12121054 <= 1;
   }

   public static boolean findProcessType(int skillID, int type) {
      Skill skill = SkillFactory.getSkill(skillID);
      return skill == null ? false : skill.findProcessType(type);
   }

   public static boolean isKeydownSkill_(int skillID) {
      if (skillID == 11121055 || skillID == 11121056 || skillID == 400041059) {
         return true;
      } else if (skillID <= 33121214) {
         if (skillID <= 14121004) {
            if (skillID <= 4341002) {
               if (skillID <= 2321001) {
                  if (skillID > 2221011) {
                     return skillID == 2221052 ? true : findProcessType(skillID, 11);
                  } else {
                     return skillID != 1311011 && skillID != 2221011 ? findProcessType(skillID, 11) : true;
                  }
               } else if (skillID > 3111013) {
                  return skillID != 3121020 && skillID != 4341002 ? findProcessType(skillID, 11) : true;
               } else {
                  return skillID != 3101008 && skillID != 3111013 ? findProcessType(skillID, 11) : true;
               }
            } else if (skillID <= 11121055) {
               if (skillID > 5311002) {
                  return skillID != 11121052 && skillID != 11121055 ? findProcessType(skillID, 11) : true;
               } else {
                  return skillID != 5221004 && skillID != 5311002 ? findProcessType(skillID, 11) : true;
               }
            } else if (skillID > 13111020) {
               return skillID != 13121001 && skillID != 14111006 && skillID != 14121004 ? findProcessType(skillID, 11) : true;
            } else {
               return skillID != 12121054 && skillID != 13111020 ? findProcessType(skillID, 11) : true;
            }
         } else if (skillID <= 25121030) {
            if (skillID <= 23121000) {
               if (skillID > 21120019) {
                  return skillID != 22171083 && skillID != 23121000 ? findProcessType(skillID, 11) : true;
               } else {
                  return skillID != 20041226 && skillID != 21120018 && skillID != 21120019 ? findProcessType(skillID, 11) : true;
               }
            } else if (skillID > 24121005) {
               return skillID != 25111005 && skillID != 25121030 ? findProcessType(skillID, 11) : true;
            } else {
               return skillID != 24121000 && skillID != 24121005 ? findProcessType(skillID, 11) : true;
            }
         } else if (skillID <= 31001000) {
            if (skillID > 27111100) {
               return skillID != 30021238 && skillID != 31001000 ? findProcessType(skillID, 11) : true;
            } else {
               return skillID != 27101202 && skillID != 27111100 ? findProcessType(skillID, 11) : true;
            }
         } else if (skillID > 31111005) {
            return skillID != 31211001 && skillID != 33121114 && skillID != 33121214 ? findProcessType(skillID, 11) : true;
         } else {
            return skillID != 31101000 && skillID != 31111005 ? findProcessType(skillID, 11) : true;
         }
      } else if (skillID <= 95001001) {
         if (skillID <= 64001008) {
            if (skillID <= 37121003) {
               if (skillID > 36101001) {
                  return skillID != 36121000 && skillID != 37121003 ? findProcessType(skillID, 11) : true;
               } else {
                  return skillID != 35121015 && skillID != 36101001 ? findProcessType(skillID, 11) : true;
               }
            } else if (skillID > 60011216) {
               return skillID != 64001000 && skillID != 64001007 && skillID != 64001008 ? findProcessType(skillID, 11) : true;
            } else {
               return skillID != 37121052 && skillID != 60011216 ? findProcessType(skillID, 11) : true;
            }
         } else if (skillID <= 80001587) {
            if (skillID > 65121003) {
               return skillID != 80001389 && skillID != 80001390 && skillID != 80001391 && skillID != 80001587 ? findProcessType(skillID, 11) : true;
            } else {
               return skillID != 64121002 && skillID != 65121003 ? findProcessType(skillID, 11) : true;
            }
         } else if (skillID > 80001836) {
            return skillID != 80001887 && skillID != 80002458 && skillID != 95001001 ? findProcessType(skillID, 11) : true;
         } else {
            return skillID != 80001629 && skillID != 80001836 ? findProcessType(skillID, 11) : true;
         }
      } else if (skillID <= 400011091) {
         if (skillID <= 131001021) {
            if (skillID > 131001004) {
               return skillID != 131001008 && skillID != 131001020 && skillID != 131001021 ? findProcessType(skillID, 11) : true;
            } else {
               return skillID != 101110101 && skillID != 101110102 && skillID != 131001004 ? findProcessType(skillID, 11) : true;
            }
         } else if (skillID > 400011028) {
            return skillID != 400011068 && skillID != 400011072 && skillID != 400011091 ? findProcessType(skillID, 11) : true;
         } else {
            return skillID != 142111010 && skillID != 400011028 ? findProcessType(skillID, 11) : true;
         }
      } else if (skillID <= 400041006) {
         if (skillID > 400021072) {
            return skillID != 400031024 && skillID != 400041006 ? findProcessType(skillID, 11) : true;
         } else {
            return skillID != 400021061 && skillID != 400021072 ? findProcessType(skillID, 11) : true;
         }
      } else if (skillID <= 400041039) {
         return skillID != 400041009 && skillID != 400041039 ? findProcessType(skillID, 11) : true;
      } else {
         return skillID != 400051024 && skillID != 400051041 && skillID != 400051334 ? findProcessType(skillID, 11) : true;
      }
   }

   public static boolean isSuperNovaSkill(int skillID) {
      return skillID == 4221052 || skillID == 65121052;
   }

   public static boolean isScreenCenterAttackSkill(int skillID) {
      switch (skillID) {
         case 13121052:
         case 14121052:
         case 15121052:
         case 21121057:
         case 80001431:
         case 80003084:
         case 100001283:
            return true;
         default:
            return false;
      }
   }

   public static boolean isFieldAttackSkill(int skillID) {
      switch (skillID) {
         case 80001762:
         case 80002212:
         case 80002463:
            return true;
         default:
            return false;
      }
   }

   public static boolean isSeekingAttackSkill(int skillID) {
      switch (skillID) {
         case 152110004:
         case 152120016:
         case 155121003:
            return true;
         default:
            return false;
      }
   }

   public static boolean isPathfinderSpecialThrowingBombSkill(int skillID) {
      return skillID <= 3311013
         ? skillID == 3301004 || skillID == 3311011 || skillID == 3311013
         : skillID == 3321005 || skillID == 3321039 || skillID == 400031035;
   }

   public static boolean isThrowingBombSkill(int skillID) {
      switch (skillID) {
         case 80002691:
         case 80002832:
         case 80002834:
         case 152001002:
         case 152120003:
         case 152121004:
         case 400011004:
         case 400021004:
         case 400021009:
         case 400021010:
         case 400021011:
         case 400021028:
         case 400021047:
         case 400021048:
         case 400021064:
         case 400021065:
         case 400031048:
         case 400041016:
         case 400041017:
         case 400041018:
         case 400041020:
         case 400041034:
         case 400051003:
         case 400051008:
            return true;
         default:
            return false;
      }
   }

   public static boolean isRushBombSkill_(int skillID) {
      switch (skillID) {
         case 2221012:
         case 5101012:
         case 5101014:
         case 5301001:
         case 12121001:
         case 14111022:
         case 22140015:
         case 22140024:
         case 27121201:
         case 31201001:
         case 61111100:
         case 61111113:
         case 61111218:
         case 64101002:
         case 80002247:
         case 80002300:
         case 101120200:
         case 101120203:
         case 101120205:
         case 400001018:
         case 400031003:
         case 400031004:
         case 400031036:
            return true;
         default:
            return false;
      }
   }

   public static boolean isUserCloneSummonedAbleSkill(int skillId) {
      if (skillId <= 101110200) {
         if (skillId == 101110200) {
            return true;
         } else if (skillId <= 23111002) {
            if (skillId == 23111002) {
               return true;
            } else if (skillId <= 14141000) {
               if (skillId == 14141000) {
                  return true;
               } else if (skillId <= 14101029) {
                  if (skillId == 14101029) {
                     return true;
                  } else if (skillId > 14101020) {
                     return skillId == 14101021 ? true : skillId == 14101028;
                  } else if (skillId == 14101020) {
                     return true;
                  } else if (skillId == 11111130) {
                     return true;
                  } else {
                     return skillId == 11111230 ? false : skillId == 14001020;
                  }
               } else if (skillId == 14111020) {
                  return true;
               } else if (skillId == 14111021) {
                  return true;
               } else if (skillId == 14120045) {
                  return true;
               } else {
                  return skillId == 14121001 ? true : skillId == 14121002;
               }
            } else if (skillId <= 23101000) {
               if (skillId == 23101000) {
                  return true;
               } else if (skillId == 14141001) {
                  return true;
               } else if (skillId == 14141002) {
                  return true;
               } else if (skillId == 14141003) {
                  return true;
               } else {
                  return skillId == 23001000 ? true : skillId == 23100004;
               }
            } else if (skillId == 23101001) {
               return true;
            } else if (skillId == 23101007) {
               return true;
            } else if (skillId == 23110006) {
               return true;
            } else {
               return skillId == 23111000 ? true : skillId == 23111001;
            }
         } else if (skillId > 101000201) {
            if (skillId > 101100201) {
               if (skillId == 101101100) {
                  return true;
               } else if (skillId == 101101200) {
                  return true;
               } else if (skillId == 101110101) {
                  return true;
               } else {
                  return skillId == 101110102 ? true : skillId == 101110104;
               }
            } else if (skillId == 101100201) {
               return true;
            } else if (skillId == 101001100) {
               return true;
            } else if (skillId == 101001200) {
               return true;
            } else if (skillId == 101100100) {
               return true;
            } else {
               return skillId == 101100101 ? true : skillId == 101100200;
            }
         } else if (skillId == 101000201) {
            return true;
         } else if (skillId <= 23121011) {
            if (skillId == 23121011) {
               return true;
            } else if (skillId == 23111003) {
               return true;
            } else if (skillId == 23120013) {
               return true;
            } else if (skillId == 23121000) {
               return true;
            } else {
               return skillId == 23121002 ? true : skillId == 23121003;
            }
         } else if (skillId == 23121052) {
            return true;
         } else if (skillId == 23141000) {
            return true;
         } else if (skillId == 101000100) {
            return true;
         } else {
            return skillId == 101000101 ? true : skillId == 101000200;
         }
      } else if (skillId <= 131001000) {
         if (skillId == 131001000) {
            return true;
         } else if (skillId > 101120200) {
            if (skillId > 101141000) {
               switch (skillId) {
                  case 101141001:
                  case 101141003:
                  case 101141006:
                  case 101141007:
                  case 101141008:
                  case 101141009:
                     return true;
                  case 101141002:
                  case 101141004:
                  case 101141005:
                  default:
                     return false;
               }
            } else if (skillId == 101141000) {
               return true;
            } else if (skillId == 101120201) {
               return true;
            } else if (skillId == 101120202) {
               return true;
            } else if (skillId == 101120204) {
               return true;
            } else {
               return skillId == 101121100 ? true : skillId == 101121200;
            }
         } else if (skillId == 101120200) {
            return true;
         } else if (skillId > 101111200) {
            if (skillId == 101120100) {
               return true;
            } else {
               return skillId == 101120102 ? true : skillId == 101120104;
            }
         } else if (skillId == 101111200) {
            return true;
         } else if (skillId == 101110202) {
            return true;
         } else {
            return skillId == 101110203 ? true : skillId == 101111100;
         }
      } else if (skillId <= 131001313) {
         if (skillId != 131001313) {
            switch (skillId) {
               case 131001001:
               case 131001002:
               case 131001003:
               case 131001004:
               case 131001005:
               case 131001008:
               case 131001010:
               case 131001011:
               case 131001012:
               case 131001013:
               case 131001101:
               case 131001102:
               case 131001103:
               case 131001104:
               case 131001108:
               case 131001113:
               case 131001201:
               case 131001202:
               case 131001203:
               case 131001208:
               case 131001213:
                  return true;
               default:
                  return false;
            }
         } else {
            return true;
         }
      } else {
         int v39 = skillId - 131002010;
         if (Integer.toUnsignedLong(v39) > 0L) {
            int v40 = v39 - 269029014;
            if (Integer.toUnsignedLong(v40) > 0L) {
               int v9 = v40 - 10035;
               boolean v8 = v9 == 0;
               if (v8) {
                  return true;
               }

               return v9 == 1;
            }
         }

         return true;
      }
   }

   public static boolean isZeroSkill(int skillID) {
      int root = getSkillRootFromSkill(skillID);
      return isZero(root) || root == 10114;
   }

   public static int getSkillRootFromSkill(int skillID) {
      int r = skillID / 10000;
      return r != 8000 ? r : skillID / 100;
   }

   public static boolean isEvanForceSkill(int skillID) {
      if (skillID > 22141012) {
         if (skillID > 400021012) {
            return skillID == 400021046 ? true : skillID == 500061041;
         } else if (skillID == 400021012) {
            return true;
         } else {
            int v4 = skillID - 22171062;
            if (skillID == 22171062) {
               return true;
            } else {
               int v5 = v4 - 1;
               return skillID == 22171063 ? true : v5 == 57830831;
            }
         }
      } else if (skillID == 22141012) {
         return true;
      } else if (skillID > 22111012) {
         return skillID != 22111017 && skillID != 22140022 ? skillID == 22141011 : true;
      } else if (skillID == 22111012) {
         return true;
      } else {
         int v1 = skillID - 22110022;
         if (skillID == 22110022) {
            return true;
         } else {
            int v2 = v1 - 1;
            return skillID == 22110023 ? true : v2 == 988;
         }
      }
   }

   public static boolean isEvanMixSkill(int skillID) {
      if (!isEvan(getSkillRootFromSkill(skillID))) {
         return false;
      } else {
         return skillID <= 22140015
            ? skillID == 22110014 || skillID == 22110024 || skillID == 22110025 || skillID == 22140014 || skillID == 22140015
            : skillID - 22140023 <= 1 || skillID - 22170064 <= 3 || skillID == 22170094;
      }
   }

   public static boolean isAranSwingSkill(int skillID) {
      switch (skillID) {
         case 21000006:
         case 21000007:
         case 21001010:
         case 21110022:
         case 21110023:
         case 21110026:
         case 21110028:
         case 21120025:
            return true;
         default:
            return false;
      }
   }

   public static int getSkillCommandStateIndex(int job, int skillID) {
      if (job == 1412 && skillID == 14110032) {
         return 0;
      } else if (isLuminous(job) && skillID == 27111009) {
         return 0;
      } else if (job - 2100 >= 100 && job != 2000) {
         if (skillID == 164000010) {
            return 2;
         } else if (skillID == 164001004) {
            return 1;
         } else if (skillID == 164121005) {
            return 0;
         } else if (skillID == 151001004) {
            return 0;
         } else if (skillID == 150011074) {
            return 1;
         } else if (skillID == 155001104 || skillID == 155001204 || skillID == 155001205) {
            return 0;
         } else if (skillID == 37101001) {
            return 1;
         } else if (skillID == 37111003) {
            return 0;
         } else if (skillID == 100000282) {
            return 1;
         } else if (skillID == 100001266 || skillID == 100001269) {
            return 0;
         } else if (skillID == 30010110) {
            return 0;
         } else if (skillID == 5001005) {
            return 0;
         } else if (skillID == 30001068) {
            return 1;
         } else if (skillID == 400021095) {
            return 0;
         } else if (isAngelicBuster(job)) {
            return 0;
         } else if (skillID == 35120001 || skillID == 35120002 || skillID == 35121009) {
            return 2;
         } else if (skillID == 35111008) {
            return 2;
         } else if (skillID == 35001006) {
            return 0;
         } else {
            return skillID != 35101005 && skillID != 35101012 ? 1 : 2;
         }
      } else if (isPirate(job) && skillID == 5001005) {
         return 0;
      } else if (skillID <= 21110011) {
         if (skillID != 21110011) {
            if (skillID <= 21100012) {
               if (skillID != 21100012) {
                  if (skillID == 20001295) {
                     return 0;
                  } else if (skillID == 21000004 || skillID == 21001009) {
                     return 1;
                  } else {
                     return skillID != 21100002 ? -1 : 2;
                  }
               } else {
                  return 3;
               }
            } else {
               if (skillID != 21100013) {
                  if (skillID == 21101011) {
                     return 2;
                  }

                  if (skillID == 21101016) {
                     return 3;
                  }

                  if (skillID != 21101017) {
                     return -1;
                  }
               }

               return 4;
            }
         } else {
            return 5;
         }
      } else if (skillID > 21111021) {
         switch (skillID) {
            case 21120019:
               return 9;
            case 21120023:
               return 8;
            case 400010030:
            case 400011031:
               return 10;
            default:
               return -1;
         }
      } else {
         if (skillID != 21111021) {
            if (skillID == 21110018) {
               return 6;
            }

            if (skillID != 21110020) {
               if (skillID == 21111017) {
                  return 5;
               }

               if (skillID != 21111019) {
                  return -1;
               }

               return 6;
            }
         }

         return 7;
      }
   }

   public static boolean isEvanDragonSkill(int skillID) {
      if (skillID <= 22140024) {
         return skillID > 22110025
            ? skillID == 22111012 || skillID == 22140015 || skillID == 22140014 || skillID == 22140024 || skillID == 22140023 || skillID == 22140022
            : skillID == 22110014 || skillID == 22110022 || skillID == 22110023 || skillID == 22110024 || skillID == 22110025;
      } else {
         return skillID <= 22170067
            ? skillID == 22141012 || skillID == 22170065 || skillID == 22170066 || skillID == 22170067
            : skillID == 22171063 || skillID == 400020046 || skillID == 400021046;
      }
   }

   public static boolean isRWReleasePileBunker(int skillID) {
      switch (skillID) {
         case 37000008:
         case 37000011:
         case 37000012:
         case 37000013:
         case 37001002:
         case 37100009:
         case 37110010:
         case 37120013:
            return true;
         default:
            return false;
      }
   }

   public static boolean isRWChargingSkill(int skillID) {
      return skillID == 37100002 || skillID == 37110001 || skillID == 37110004;
   }

   public static boolean isRWMultiChargeSkill(int skillID) {
      if (skillID <= 37101001) {
         return skillID > 37001001 ? skillID == 37100002 || skillID == 37101001 : skillID == 37000010 || skillID == 37001001;
      } else {
         return skillID <= 37110004 ? skillID == 37110001 || skillID == 37110004 : skillID == 37111000 || skillID == 37111003;
      }
   }

   public static boolean isRWNeedBulletSkill(int skillID) {
      if (skillID <= 37100008) {
         return skillID > 37000009 ? skillID == 37001004 || skillID == 37100008 : skillID == 37000005 || skillID == 37000009;
      } else {
         return skillID > 400011019 ? skillID == 400011091 || skillID == 400011103 : skillID >= 37120014 && skillID <= 37120019 || skillID == 400011019;
      }
   }

   public static int getJaguarIndex(int mobID) {
      return mobID - 9304000 + 1;
   }

   public static int getJaguarSkillByIndex(int index) {
      return 33001007 + index - 1;
   }

   public static int getJaguarVehicle(int index) {
      switch (index) {
         case 1:
            return 1932015;
         case 2:
            return 1932030;
         case 3:
            return 1932031;
         case 4:
            return 1932032;
         case 5:
            return 1932033;
         case 6:
            return 1932036;
         case 7:
            return 1932100;
         case 8:
            return 1932148;
         case 9:
            return 1932215;
         default:
            return 1932015;
      }
   }

   public static boolean isWildHunterSummonJaguar(int skillID) {
      return skillID < 33001007 ? false : skillID <= 33001015;
   }

   public static boolean isGuildSkill(int skillID) {
      int skillRoot = skillID / 10000;
      return skillRoot == 9100;
   }

   public static boolean isNoneCooltimeSkillReduce(int skillID) {
      switch (skillID) {
         case 11121157:
         case 11121257:
         case 15121052:
         case 36121052:
         case 36121055:
         case 61121052:
         case 164121042:
            return true;
         default:
            return false;
      }
   }

   public static boolean isResettableCooltimeSkill(int skillID) {
      Skill skill = SkillFactory.getSkill(skillID);
      if (skill == null) {
         return false;
      } else if (skill.isNotCooltimeReset()) {
         return false;
      } else if (skillID == 21121057) {
         return false;
      } else {
         return skillID == 100001274 ? false : skillID != 80002282;
      }
   }

   public static int getKaiserStackIncrement(int skillID) {
      switch (skillID) {
         case 61001000:
         case 61111111:
         case 61120219:
            return 1;
         case 61001004:
            return 3;
         case 61001005:
            return 4;
         case 61001101:
         case 61111100:
         case 61111215:
            return 2;
         case 61101002:
         case 61120007:
            return 15;
         case 61101100:
         case 61101101:
            return 5;
         case 61111101:
         case 61121052:
         case 61121100:
         case 400011079:
            return 8;
         case 61121102:
         case 61121104:
         case 61121124:
            return 6;
         case 61121105:
            return 14;
         default:
            return 0;
      }
   }

   public static boolean isOnOffSkill(int skillID) {
      if (skillID == 65121007 || skillID == 65121008 || skillID == 65121101) {
         return false;
      } else {
         return !isAngelicBuster(getSkillRootFromSkill(skillID)) ? false : skillID / 100 % 10 == 1;
      }
   }

   public static int getLinked5thSkill(int skillID) {
      if (skillID == 400041086) {
         return 400041084;
      } else if (skillID == 400011056) {
         return 400011055;
      } else if (skillID == 400031068) {
         return 400031003;
      } else if (skillID == 400001060) {
         return 400001059;
      } else if (skillID == 400031067) {
         return 400031036;
      } else if (skillID == 400021102 || skillID == 400021103) {
         skillID = 400021101;
         return 400021101;
      } else if (skillID == 400011125 || skillID == 400011126) {
         return 400011124;
      } else if (skillID == 400011132) {
         return 400011131;
      } else if (skillID == 400031054) {
         return 400031053;
      } else if (skillID == 400031056) {
         return 400031055;
      } else if (skillID >= 400031047 && skillID <= 400031051) {
         return 400031057;
      } else if (skillID == 400041062 || skillID == 400041079) {
         return 400041061;
      } else if (skillID >= 400041070 && skillID <= 400041073) {
         return 400041069;
      } else if (skillID >= 400041076 && skillID <= 400041078) {
         return 400041075;
      } else if (skillID == 400051071) {
         return 400051070;
      } else if (skillID >= 400051075 && skillID <= 400051077) {
         return 400051074;
      } else if (skillID >= 500061030 && skillID <= 500061032) {
         return 500061029;
      } else if (skillID == 400051081) {
         return 400051073;
      } else if (skillID == 400011050) {
         return 400011127;
      } else if (skillID == 400041060) {
         return 400041059;
      } else if (skillID >= 400051059 && skillID <= 400051067) {
         return 400051058;
      } else if (skillID == 400011122 || skillID == 400011123) {
         return 400011121;
      } else if (skillID >= 400021106 && skillID <= 400021110) {
         return 400021105;
      } else if (skillID == 400041056) {
         return 400041055;
      } else if (skillID == 400011129) {
         return 400011112;
      } else if (skillID == 400011111 || skillID == 400011137) {
         return 400011110;
      } else if (skillID == 400011117 || skillID == 400011133) {
         return 400011116;
      } else if (skillID == 400051069) {
         return 400051068;
      } else if (skillID == 400041058) {
         return 400041057;
      } else if ((skillID < 400011118 || skillID > 400011120) && skillID != 400011130) {
         if (skillID == 400021100) {
            return 400021099;
         } else if (skillID >= 400041064 && skillID <= 400041067) {
            return 400041063;
         } else if ((skillID < 400021096 || skillID > 400021098) && skillID != 400021104) {
            if (skillID == 400011085) {
               return 400011047;
            } else if (skillID == 400021112) {
               return 400021094;
            } else if (skillID == 400021088 || skillID == 400021113 || skillID == 400021089) {
               return 400021087;
            } else if (skillID == 400031063 || skillID == 400031064) {
               return 400031062;
            } else if (skillID == 400001052) {
               return 400001007;
            } else if (skillID == 400011135) {
               return 400011134;
            } else if (skillID == 400021131) {
               return 400021130;
            } else if (skillID >= 400021124 && skillID <= 400021128) {
               return 400021123;
            } else if (skillID == 400041080) {
               return 400041022;
            } else if (skillID == 400001044) {
               return 400001043;
            } else {
               if (skillID <= 400021053) {
                  if (skillID <= 400010071) {
                     if (skillID <= 400010000) {
                        if (skillID > 400001030) {
                           switch (skillID) {
                              case 400001038:
                                 return 400001037;
                              case 400001039:
                              case 400001042:
                              case 400001043:
                              case 400001044:
                              case 400001045:
                              case 400001046:
                              case 400001050:
                                 break;
                              case 400001040:
                                 return 400001039;
                              case 400001041:
                                 return 400001039;
                              case 400001047:
                              case 400001048:
                              case 400001049:
                                 return 400001046;
                              case 400001051:
                              case 400001053:
                              case 400001054:
                              case 400001055:
                                 return 400001050;
                              case 400001052:
                              default:
                                 if (skillID == 400010000) {
                                    return 400011000;
                                 }
                                 break;
                              case 400001056:
                                 return 400001045;
                           }
                        } else {
                           if (skillID == 400001011) {
                              return 400001010;
                           }

                           switch (skillID) {
                              case 400001015:
                                 return 400001014;
                              case 400001016:
                                 return 400001013;
                              case 400001017:
                              case 400001018:
                              case 400001019:
                              case 400001020:
                              case 400001021:
                              case 400001023:
                              case 400001024:
                              default:
                                 break;
                              case 400001022:
                                 return 400001019;
                              case 400001025:
                                 return 400001024;
                              case 400001026:
                                 return 400001024;
                              case 400001027:
                                 return 400001024;
                              case 400001028:
                                 return 400001024;
                              case 400001029:
                                 return 400001024;
                              case 400001030:
                                 return 400001024;
                           }
                        }
                     } else if (skillID > 400010028) {
                        if (skillID == 400010030) {
                           return 400011031;
                        }

                        if (skillID == 400010071) {
                           return 400010070;
                        }
                     } else {
                        if (skillID == 400010010) {
                           return 400011010;
                        }

                        if (skillID == 400010028) {
                           return 400011028;
                        }
                     }
                  } else if (skillID <= 400020046) {
                     if (skillID > 400020002) {
                        switch (skillID) {
                           case 400020009:
                              return 400021008;
                           case 400020010:
                              return 400021008;
                           case 400020011:
                              return 400021008;
                           default:
                              if (skillID == 400020046) {
                                 return 400021046;
                              }
                        }
                     } else {
                        switch (skillID) {
                           case 400011002:
                              return 400011001;
                           case 400011003:
                           case 400011004:
                           case 400011005:
                           case 400011006:
                           case 400011010:
                           case 400011011:
                           case 400011012:
                           case 400011015:
                           case 400011016:
                           case 400011017:
                           case 400011021:
                           case 400011026:
                           case 400011027:
                           case 400011028:
                           case 400011029:
                           case 400011030:
                           case 400011031:
                           case 400011032:
                           case 400011038:
                           case 400011039:
                           case 400011047:
                           case 400011048:
                           case 400011049:
                           case 400011050:
                           case 400011051:
                           case 400011052:
                           case 400011054:
                           case 400011055:
                           case 400011057:
                           case 400011058:
                           case 400011066:
                           case 400011068:
                           case 400011070:
                           case 400011071:
                           case 400011072:
                           case 400011073:
                           case 400011077:
                           case 400011079:
                           case 400011083:
                           case 400011088:
                           case 400011090:
                           case 400011091:
                           case 400011098:
                              break;
                           case 400011007:
                              return 400011006;
                           case 400011008:
                              return 400011006;
                           case 400011009:
                              return 400011006;
                           case 400011013:
                              return 400011012;
                           case 400011014:
                              return 400011012;
                           case 400011018:
                              return 400011006;
                           case 400011019:
                              return 400011017;
                           case 400011020:
                              return 400011016;
                           case 400011022:
                              return 400011005;
                           case 400011023:
                              return 400011005;
                           case 400011024:
                              return 400011015;
                           case 400011025:
                              return 400011015;
                           case 400011033:
                              return 400011032;
                           case 400011034:
                              return 400011032;
                           case 400011035:
                              return 400011032;
                           case 400011036:
                              return 400011032;
                           case 400011037:
                              return 400011032;
                           case 400011040:
                              return 400011039;
                           case 400011041:
                              return 400011039;
                           case 400011042:
                              return 400011039;
                           case 400011043:
                              return 400011039;
                           case 400011044:
                              return 400011039;
                           case 400011045:
                              return 400011039;
                           case 400011046:
                              return 400011039;
                           case 400011053:
                              return 400011052;
                           case 400011056:
                              return 11121052;
                           case 400011059:
                              return 400011058;
                           case 400011060:
                              return 400011058;
                           case 400011061:
                              return 400011058;
                           case 400011062:
                              return 400011038;
                           case 400011063:
                              return 400011038;
                           case 400011064:
                              return 400011038;
                           case 400011065:
                              return 400011055;
                           case 400011067:
                              return 400011032;
                           case 400011069:
                              return 400011068;
                           case 400011074:
                              return 400011073;
                           case 400011075:
                              return 400011073;
                           case 400011076:
                              return 400011073;
                           case 400011078:
                              return 400011077;
                           case 400011080:
                              return 400011079;
                           case 400011081:
                              return 400011079;
                           case 400011082:
                              return 400011079;
                           case 400011084:
                              return 400011083;
                           case 400011085:
                              return 400011073;
                           case 400011086:
                              return 400011073;
                           case 400011087:
                              return 400011073;
                           case 400011089:
                              return 400011088;
                           case 400011092:
                              return 400011091;
                           case 400011093:
                              return 400011091;
                           case 400011094:
                              return 400011091;
                           case 400011095:
                              return 400011091;
                           case 400011096:
                              return 400011091;
                           case 400011097:
                              return 400011091;
                           case 400011099:
                              return 400011098;
                           case 400011100:
                              return 400011098;
                           case 400011101:
                              return 400011098;
                           case 400011102:
                              return 400011090;
                           case 400011103:
                              return 400011091;
                           default:
                              if (skillID == 400020002) {
                                 return 400021002;
                              }
                        }
                     }
                  } else if (skillID > 400021016) {
                     switch (skillID) {
                        case 400021029:
                           return 400021028;
                        case 400021030:
                        case 400021032:
                           break;
                        case 400021031:
                           return 400021030;
                        case 400021033:
                           return 400021032;
                        default:
                           switch (skillID) {
                              case 400021040:
                                 return 400021030;
                              case 400021041:
                              case 400021042:
                              case 400021046:
                              case 400021047:
                              case 400021048:
                              case 400021051:
                              default:
                                 break;
                              case 400021043:
                                 return 400021042;
                              case 400021044:
                                 return 400021042;
                              case 400021045:
                                 return 400021042;
                              case 400021049:
                                 return 400021041;
                              case 400021050:
                                 return 400021041;
                              case 400021052:
                                 return 400021032;
                              case 400021053:
                                 return 400021048;
                           }
                     }
                  } else {
                     if (skillID == 400020051) {
                        return 400021046;
                     }

                     switch (skillID) {
                        case 400021007:
                           return 400021006;
                        case 400021008:
                        case 400021012:
                        default:
                           break;
                        case 400021009:
                           return 400021008;
                        case 400021010:
                           return 400021008;
                        case 400021011:
                           return 400021008;
                        case 400021013:
                           return 400021012;
                        case 400021014:
                           return 400021012;
                        case 400021015:
                           return 400021012;
                        case 400021016:
                           return 400021012;
                     }
                  }
               } else if (skillID <= 400041036) {
                  if (skillID <= 400031021) {
                     if (skillID > 400021077) {
                        if (skillID == 400030002) {
                           return 400031002;
                        }

                        switch (skillID) {
                           case 400031004:
                              return 400031003;
                           case 400031005:
                           case 400031006:
                           case 400031007:
                           case 400031012:
                           case 400031015:
                           case 400031017:
                           case 400031020:
                           default:
                              break;
                           case 400031008:
                              return 400031007;
                           case 400031009:
                              return 400031007;
                           case 400031010:
                              return 400031006;
                           case 400031011:
                              return 400031007;
                           case 400031013:
                              return 400031012;
                           case 400031014:
                              return 400031012;
                           case 400031016:
                              return 400031015;
                           case 400031018:
                              return 400031017;
                           case 400031019:
                              return 400031017;
                           case 400031021:
                              return 400031020;
                        }
                     } else {
                        switch (skillID) {
                           case 400021062:
                              return 400021061;
                           case 400021063:
                              break;
                           case 400021064:
                              return 400021063;
                           case 400021065:
                              return 400021063;
                           default:
                              switch (skillID) {
                                 case 400021075:
                                    return 400021074;
                                 case 400021076:
                                    return 400021074;
                                 case 400021077:
                                    return 400021070;
                              }
                        }
                     }
                  } else if (skillID > 400040006) {
                     if (skillID == 400040008) {
                        return 400041008;
                     }

                     switch (skillID) {
                        case 400041003:
                           return 400041002;
                        case 400041004:
                           return 400041002;
                        case 400041005:
                           return 400041002;
                        case 400041006:
                        case 400041007:
                        case 400041008:
                        case 400041009:
                        case 400041020:
                        case 400041021:
                        case 400041022:
                        case 400041025:
                        case 400041028:
                        case 400041029:
                        case 400041032:
                        case 400041033:
                        case 400041035:
                        default:
                           break;
                        case 400041010:
                           return 400041009;
                        case 400041011:
                           return 400041009;
                        case 400041012:
                           return 400041009;
                        case 400041013:
                           return 400041009;
                        case 400041014:
                           return 400041009;
                        case 400041015:
                           return 400041009;
                        case 400041016:
                           return 4001344;
                        case 400041017:
                           return 4111010;
                        case 400041018:
                           return 4121013;
                        case 400041019:
                           return 400041008;
                        case 400041023:
                           return 400041022;
                        case 400041024:
                           return 400041022;
                        case 400041026:
                           return 400041025;
                        case 400041027:
                           return 400041025;
                        case 400041030:
                           return 400041000;
                        case 400041031:
                           return 400041029;
                        case 400041034:
                           return 400041033;
                        case 400041036:
                           return 400041035;
                     }
                  } else {
                     if (skillID == 400040000) {
                        return 400041000;
                     }

                     if (skillID == 400040006) {
                        return 400041006;
                     }
                  }
               } else if (skillID <= 400051013) {
                  if (skillID > 400051005) {
                     if (skillID == 400051012) {
                        return 400051009;
                     }

                     if (skillID == 400051013) {
                        return 400051007;
                     }
                  } else {
                     switch (skillID) {
                        case 400041047:
                           return 400041044;
                        case 400041048:
                        case 400041050:
                        case 400041052:
                           break;
                        case 400041049:
                           return 400041048;
                        case 400041051:
                           return 400041050;
                        case 400041053:
                           return 400041052;
                        default:
                           switch (skillID) {
                              case 400051001:
                                 return 400051000;
                              case 400051002:
                              default:
                                 break;
                              case 400051003:
                                 return 400051002;
                              case 400051004:
                                 return 400051002;
                              case 400051005:
                                 return 400051002;
                           }
                     }
                  }
               } else if (skillID > 400051035) {
                  if (skillID == 400051039) {
                     return 400051038;
                  }

                  switch (skillID) {
                     case 400051045:
                        return 400051044;
                     case 400051046:
                     case 400051047:
                     case 400051048:
                     case 400051051:
                     default:
                        break;
                     case 400051049:
                        return 400051040;
                     case 400051050:
                        return 400051040;
                     case 400051052:
                        return 400051038;
                     case 400051053:
                        return 400051038;
                  }
               } else {
                  switch (skillID) {
                     case 400051019:
                        return 400051018;
                     case 400051020:
                        return 400051018;
                     case 400051021:
                     case 400051022:
                     case 400051024:
                        break;
                     case 400051023:
                        return 400051022;
                     case 400051025:
                        return 400051024;
                     case 400051026:
                        return 400051024;
                     case 400051027:
                        return 400051018;
                     default:
                        if (skillID == 400051035) {
                           return 400051334;
                        }
                  }
               }

               return skillID;
            }
         } else {
            return 400021096;
         }
      } else {
         return 400011118;
      }
   }

   public static int getCooldown6thAttackSkill(int skillID) {
      switch (skillID) {
         case 31141001:
            return 31141001;
         case 36141001:
            return 36141001;
         case 36141003:
            return 36141003;
         case 154141001:
         case 154141002:
            return 154141001;
         case 155141004:
         case 155141005:
         case 155141006:
         case 155141007:
         case 155141008:
            return 155141004;
         case 155141011:
         case 155141012:
            return 155141011;
         case 155141016:
            return 155141016;
         case 164141000:
            return 164121000;
         case 164141005:
            return 164111003;
         case 164141011:
            return 164121003;
         case 500061025:
            return 400041002;
         case 500061035:
            return 400041037;
         case 500061041:
            return 400021012;
         default:
            return 0;
      }
   }

   public static int getCooldown6thActiveSkill(int skillID) {
      switch (skillID) {
         case 500061000:
            return 400011003;
         case 500061001:
         case 500061002:
         case 500061003:
         case 500061005:
         case 500061008:
         case 500061009:
         case 500061011:
         case 500061012:
         case 500061015:
         case 500061016:
         case 500061017:
         case 500061025:
         case 500061026:
         case 500061027:
         case 500061028:
         case 500061029:
         case 500061030:
         case 500061031:
         case 500061032:
         case 500061034:
         case 500061035:
         case 500061037:
         case 500061038:
         case 500061040:
         case 500061041:
         case 500061042:
         case 500061043:
         case 500061044:
         case 500061045:
         case 500061047:
         case 500061048:
         case 500061049:
         case 500061051:
         case 500061052:
         case 500061053:
         case 500061054:
         case 500061055:
         case 500061056:
         case 500061057:
         case 500061058:
         case 500061060:
         case 500061061:
         case 500061062:
         case 500061063:
         case 500061064:
         default:
            return 0;
         case 500061004:
            return 400041028;
         case 500061006:
            return 400051010;
         case 500061007:
            return 400031005;
         case 500061010:
            return 400011127;
         case 500061013:
            return 400051036;
         case 500061014:
            return 400041048;
         case 500061018:
         case 500061019:
         case 500061020:
         case 500061021:
         case 500061022:
         case 500061023:
         case 500061024:
            return 400031037;
         case 500061033:
            return 400031030;
         case 500061036:
            return 400051007;
         case 500061039:
            return 400011016;
         case 500061046:
            return 400031007;
         case 500061050:
            return 400031017;
         case 500061059:
            return 400041029;
         case 500061065:
            return 400011109;
      }
   }

   public static int getLinkedSkillID(int skillID) {
      if (getCooldown6thAttackSkill(skillID) > 0) {
         return getCooldown6thAttackSkill(skillID);
      } else {
         int ret = getLinkedAranSkill(skillID);
         if (skillID != 1321024 && skillID == 1321025) {
         }

         if (skillID == 154141001 || skillID == 154141002) {
            ret = 154141001;
         }

         if (skillID == 155141005 || skillID == 155141006) {
            ret = 155141004;
         }

         if (skillID == 11121014) {
            ret = 11121014;
         }

         if (skillID != 4241000 && ret == 4241000) {
            ret = skillID;
         }

         if (skillID == 22110023) {
            ret = 22110023;
         }

         if (skillID == 22140022) {
            ret = 22140022;
         }

         if (skillID == 22170064) {
            ret = 22170064;
         }

         if (skillID == 22170093) {
            ret = 22170093;
         }

         if (skillID == 22171063) {
            ret = 22171063;
         }

         if (skillID == 25121133) {
            ret = 25121133;
         }

         if (skillID == 33101115) {
            ret = 33101115;
         }

         if (skillID == 400020051) {
            ret = 400020051;
         }

         return ret;
      }
   }

   public static int getLinkedAranSkill(int skillID) {
      if (skillID >= 400000000) {
         int s = getLinked5thSkill(skillID);
         if (s != skillID) {
            return s;
         }
      }

      if (skillID >= 3141501 && skillID <= 3141502) {
         return 3141500;
      } else if (skillID >= 21141500 && skillID <= 21141506) {
         return 21141500;
      } else if (skillID == 12141501 || skillID == 12141502) {
         return 12141500;
      } else if (skillID == 24141501 || skillID == 24141502) {
         return 24141500;
      } else if (skillID >= 1141000 && skillID <= 1141001) {
         return 1141000;
      } else if ((skillID < 1241000 || skillID > 1241002) && skillID != 1240001) {
         if (skillID >= 1241500 && skillID <= 1241503) {
            return 1241500;
         } else if (skillID >= 1341500 && skillID <= 1341501) {
            return 1341500;
         } else if (skillID >= 2341500 && skillID <= 2341506) {
            return 2341500;
         } else if (skillID == 2221055) {
            return 2221054;
         } else if (skillID >= 2241500 && skillID <= 2241504) {
            return 2241500;
         } else if (skillID >= 2141000 && skillID <= 2141002) {
            return 2141000;
         } else if (skillID >= 3141000 && skillID <= 3141001) {
            return 3141000;
         } else if (skillID >= 3241000 && skillID <= 3241004) {
            return 3241000;
         } else if (skillID >= 3341000 && skillID <= 3341004) {
            return 3341000;
         } else if (skillID >= 3341500 && skillID <= 3341501) {
            return 3341500;
         } else if (skillID >= 4141000 && skillID <= 4141001) {
            return 4141000;
         } else if (skillID >= 4141500 && skillID <= 4141502) {
            return 4141500;
         } else if (skillID >= 4241000 && skillID <= 4241005) {
            return 4241000;
         } else if (skillID >= 4241500 && skillID <= 4241502) {
            return 4241500;
         } else if (skillID >= 4361500 && skillID <= 4361503) {
            return 4361500;
         } else if (skillID >= 5141000 && skillID <= 5141003) {
            return 5141000;
         } else if (skillID >= 5141500 && skillID <= 5141506) {
            return 5141500;
         } else if (skillID >= 5241000 && skillID <= 5241001) {
            return 5241000;
         } else if (skillID >= 5241500 && skillID <= 5241502) {
            return 5241500;
         } else if (skillID >= 5341500 && skillID <= 5341501) {
            return 5341500;
         } else if (skillID >= 11141500 && skillID <= 11141502) {
            return 11141500;
         } else if (skillID >= 12141000 && skillID <= 12141006) {
            return 12141000;
         } else if (skillID >= 12141500 && skillID <= 12141502) {
            return 12141500;
         } else if (skillID >= 13141500 && skillID <= 13141505) {
            return 13141500;
         } else if (skillID >= 14141000 && skillID <= 14141003) {
            return 14141000;
         } else if (skillID >= 14141500 && skillID <= 14141502) {
            return 14141500;
         } else if (skillID >= 15141000 && skillID <= 15141001) {
            return 15141000;
         } else if (skillID >= 15141500 && skillID <= 15141501) {
            return 15141500;
         } else if (skillID >= 21141000 && skillID <= 21141003) {
            return 21141000;
         } else if (skillID >= 21141500 && skillID <= 21141506) {
            return 21141500;
         } else if (skillID >= 22201000 && skillID <= 22201001) {
            return 22201000;
         } else if (skillID >= 22201500 && skillID <= 22201501) {
            return 22201500;
         } else if (skillID >= 23141000 && skillID <= 23141002) {
            return 23141000;
         } else if (skillID >= 23141500 && skillID <= 23141503) {
            return 23141500;
         } else if (skillID >= 24141500 && skillID <= 24141502) {
            return 24141500;
         } else if (skillID >= 25141500 && skillID <= 25141506) {
            return 25141500;
         } else if (skillID >= 27141500 && skillID <= 27141501) {
            return 27141500;
         } else if (skillID >= 31141000 && skillID <= 31141001) {
            return 31141000;
         } else if (skillID >= 31141500 && skillID <= 31141503) {
            return 31141500;
         } else if (skillID >= 31241000 && skillID <= 31241001) {
            return 31241000;
         } else if (skillID >= 31241500 && skillID <= 31241502) {
            return 31241500;
         } else if (skillID >= 32141500 && skillID <= 32141501) {
            return 32141500;
         } else if (skillID >= 33141000 && skillID <= 33141005) {
            return 33141000;
         } else if (skillID >= 33141500 && skillID <= 33141503) {
            return 33141500;
         } else if (skillID >= 35141000 && skillID <= 35141002) {
            return 35141000;
         } else if (skillID >= 35141500 && skillID <= 35141503) {
            return 35141500;
         } else if (skillID >= 36141000 && skillID <= 36141003) {
            return 36141000;
         } else if (skillID >= 36141500 && skillID <= 36141503) {
            return 36141500;
         } else if (skillID >= 37141000 && skillID <= 37141001) {
            return 37141000;
         } else if (skillID >= 37141500 && skillID <= 37141501) {
            return 37141500;
         } else if (skillID >= 51141000 && skillID <= 51141001) {
            return 51141000;
         } else if (skillID >= 51141500 && skillID <= 51141502) {
            return 51141500;
         } else if (skillID >= 61141000 && skillID <= 61141001) {
            return 61141000;
         } else if (skillID >= 61141500 && skillID <= 61141502) {
            return 61141500;
         } else if (skillID >= 63141000 && skillID <= 63141006) {
            return 63141000;
         } else if (skillID >= 63141500 && skillID <= 63141503) {
            return 63141500;
         } else if (skillID >= 64141000 && skillID <= 64141003) {
            return 64141000;
         } else if (skillID >= 64141500 && skillID <= 64141502) {
            return 64141500;
         } else if (skillID >= 65141500 && skillID <= 65141502) {
            return 65141500;
         } else if (skillID >= 101141000 && skillID <= 101141013) {
            return 101141000;
         } else if (skillID >= 101141500 && skillID <= 101141502) {
            return 101141500;
         } else if (skillID >= 151141500 && skillID <= 151141501) {
            return 151141500;
         } else if (skillID >= 152141000 && skillID <= 152141006) {
            return 152141000;
         } else if (skillID >= 152141500 && skillID <= 152141506) {
            return 152141500;
         } else if (skillID >= 154141000 && skillID <= 154141002) {
            return 154141000;
         } else if (skillID >= 154141500 && skillID <= 154141503) {
            return 154141500;
         } else if (skillID >= 155141000 && skillID <= 155141020) {
            return 155141000;
         } else if (skillID >= 155141500 && skillID <= 155141501) {
            return 155141500;
         } else if (skillID >= 164141000 && skillID <= 164141029) {
            return 164141000;
         } else if (skillID >= 164141500 && skillID <= 164141502) {
            return 164141500;
         } else if (skillID >= 400001066 && skillID <= 400001090) {
            return 400001066;
         } else if (skillID == 154101004) {
            return 154100003;
         } else if (skillID == 154101002) {
            return 154101001;
         } else if (skillID == 154141002) {
            return 154141001;
         } else if (skillID == 154111011 || skillID == 154111004) {
            return 154110003;
         } else if (skillID == 154121009 || skillID == 154121012) {
            return 154120008;
         } else if (skillID == 11001024) {
            return 11001025;
         } else if (skillID == 11100034 || skillID == 11001030 || skillID == 11001027) {
            return 11001022;
         } else if (skillID == 12101030) {
            return 12101024;
         } else if (skillID == 12120022 || skillID == 12120023) {
            return 12121002;
         } else if (skillID == 12120013 || skillID == 12120014) {
            return 12121004;
         } else if (skillID == 12121056 || skillID == 12121057 || skillID == 12121058 || skillID == 12121059) {
            return 12121054;
         } else if (skillID == 12000026) {
            return 12001020;
         } else if (skillID == 12100028) {
            return 12100020;
         } else if (skillID == 12110028 || skillID == 12110030) {
            return 12110020;
         } else if (skillID == 12120010 || skillID >= 12120017 && skillID <= 12120021 || skillID == 12120024) {
            return 12120006;
         } else if (skillID == 15101028) {
            return 15100027;
         } else {
            switch (skillID) {
               case 2111014:
                  return 2111013;
               case 2211015:
                  return 2211011;
               case 2311014:
                  return 2311011;
               case 2311015:
                  return 2311001;
               case 2321016:
                  return 2321015;
               case 3120017:
                  return 3120022;
               case 3211020:
                  return 3211019;
               case 3221023:
               case 3221024:
               case 3221027:
                  return 3220020;
               case 3221025:
                  return 3221007;
               case 4220021:
               case 4221019:
                  return 4211006;
               case 5101019:
                  return 5100018;
               case 5111021:
                  return 5110016;
               case 5121023:
                  return 5120029;
               case 5121025:
                  return 5120024;
               case 5121027:
                  return 5120026;
               case 5221022:
               case 5221027:
               case 5221029:
                  return 5220019;
               case 14001031:
                  return 14001026;
               case 22110013:
                  return 22111012;
               case 22140013:
                  return 22141012;
               case 22170064:
               case 22170093:
                  return 22171063;
               case 23111009:
               case 23111010:
               case 23111011:
                  return 23111008;
               case 63001001:
                  return 63001000;
               case 63001003:
               case 63001005:
                  return 63001002;
               case 63001100:
               case 63101100:
                  return 63100100;
               case 63101003:
                  return 63100002;
               case 63101006:
                  return 63101005;
               case 63101104:
                  return 63100104;
               case 63111002:
                  return 63110001;
               case 63111004:
               case 63111005:
                  return 63111003;
               case 63111012:
               case 63111013:
                  return 63110011;
               case 63111103:
               case 63111104:
               case 63111105:
               case 63111106:
                  return 63110103;
               case 63121005:
                  return 63121004;
               case 63121007:
                  return 63121006;
               case 63121041:
                  return 63121040;
               case 63121102:
               case 63121103:
                  return 63120102;
               case 63121140:
               case 63121141:
                  return 63120140;
               case 80003026:
                  return 80003025;
               case 151101004:
               case 151101010:
                  return 151101003;
               case 155101201:
                  return 155101200;
               case 155101212:
                  return 155101200;
               case 155121215:
                  return 155121202;
               case 162101003:
               case 162101004:
                  return 162100002;
               case 162101006:
               case 162101007:
                  return 162100005;
               case 162101009:
               case 162101010:
               case 162101011:
                  return 162100008;
               case 162121003:
               case 162121004:
                  return 162120002;
               case 162121006:
               case 162121007:
                  return 162120005;
               case 162121009:
               case 162121010:
                  return 162120008;
               case 162121012:
               case 162121013:
               case 162121014:
               case 162121015:
               case 162121016:
               case 162121017:
               case 162121018:
               case 162121019:
                  return 162120011;
               case 162121021:
                  return 162120020;
               default:
                  if (skillID <= 37100008) {
                     if (skillID <= 21000007) {
                        if (skillID <= 5201014) {
                           if (skillID <= 3311013) {
                              if (skillID <= 2321055) {
                                 if (skillID <= 2100010) {
                                    if (skillID > 1120017) {
                                       if (skillID == 1320019) {
                                          return 1320016;
                                       }

                                       if (skillID == 2100010) {
                                          return 2101010;
                                       }
                                    } else {
                                       if (skillID == 1100012) {
                                          return 1101012;
                                       }

                                       if (skillID == 1120017) {
                                          return 1121008;
                                       }
                                    }
                                 } else if (skillID > 2121055) {
                                    if (skillID == 2220014) {
                                       return 2221007;
                                    }

                                    if (skillID == 2321055) {
                                       return 2321052;
                                    }
                                 } else {
                                    if (skillID == 2120013) {
                                       return 2121007;
                                    }

                                    if (skillID == 2121055) {
                                       return 2121052;
                                    }
                                 }
                              } else if (skillID <= 3120017) {
                                 if (skillID > 3011008) {
                                    if (skillID == 3100010) {
                                       return 3101009;
                                    }

                                    if (skillID == 3120017) {
                                       return 3121016;
                                    }
                                 } else {
                                    if (skillID >= 3000008 && skillID <= 3000010) {
                                       return 3001007;
                                    }

                                    if (skillID >= 3011006 && skillID <= 3011008) {
                                       return 3011005;
                                    }
                                 }
                              } else if (skillID > 3301009) {
                                 if (skillID == 3311003) {
                                    return 3311002;
                                 }

                                 if (skillID == 3311011) {
                                    return 3311010;
                                 }

                                 if (skillID == 3311013) {
                                    return 3310001;
                                 }
                              } else {
                                 if (skillID == 3301004) {
                                    return 3301003;
                                 }

                                 if (skillID == 3301009) {
                                    return 3301008;
                                 }
                              }
                           } else if (skillID <= 5001009) {
                              if (skillID <= 4100012) {
                                 if (skillID > 3321021) {
                                    if (skillID >= 3321036 && skillID <= 3321040) {
                                       return 3321035;
                                    }

                                    if (skillID == 4100012) {
                                       return 4100011;
                                    }
                                 } else {
                                    if (skillID >= 3321003 && skillID <= 3321007) {
                                       return 3320002;
                                    }

                                    if (skillID >= 3321015 && skillID <= 3321021) {
                                       return 3321014;
                                    }
                                 }
                              } else if (skillID > 4210014) {
                                 if (skillID == 4221016) {
                                    return 4221014;
                                 }

                                 if (skillID == 5001008) {
                                    return 5200010;
                                 }

                                 if (skillID == 5001009) {
                                    return 5101004;
                                 }
                              } else {
                                 if (skillID == 4120019) {
                                    return 4120018;
                                 }

                                 if (skillID == 4210014) {
                                    return 4211006;
                                 }
                              }
                           } else if (skillID <= 5120021) {
                              if (skillID > 5111013) {
                                 if (skillID == 5111015) {
                                    return 5111012;
                                 }

                                 if (skillID == 5120021) {
                                    return 5121013;
                                 }
                              } else {
                                 if (skillID == 5101014) {
                                    return 5101012;
                                 }

                                 if (skillID == 5111013) {
                                    return 5111002;
                                 }
                              }
                           } else if (skillID > 5121020) {
                              if (skillID == 5121055) {
                                 return 5121052;
                              }

                              if (skillID == 5201005) {
                                 return 5201011;
                              }

                              if (skillID == 5201013 || skillID == 5201014) {
                                 return 5201012;
                              }
                           } else {
                              if (skillID == 5121017) {
                                 return 5121016;
                              }

                              if (skillID == 5121020) {
                                 return 5121007;
                              }
                           }
                        } else if (skillID <= 12100028) {
                           if (skillID <= 5320011) {
                              if (skillID <= 5300007) {
                                 if (skillID > 5220025) {
                                    if (skillID == 5221026) {
                                       return 5221017;
                                    }

                                    if (skillID == 5300007) {
                                       return 5301001;
                                    }
                                 } else {
                                    if (skillID >= 5210016 && skillID <= 5210018) {
                                       return 5210015;
                                    }

                                    if (skillID >= 5220023 && skillID <= 5220025) {
                                       return 5221022;
                                    }
                                 }
                              } else if (skillID > 5310008) {
                                 if (skillID == 5310011) {
                                    return 5311010;
                                 }

                                 if (skillID == 5320011) {
                                    return 5321004;
                                 }
                              } else {
                                 if (skillID == 5310004) {
                                    return 5311004;
                                 }

                                 if (skillID == 5310008) {
                                    return 5311002;
                                 }
                              }
                           } else if (skillID <= 11121055) {
                              if (skillID > 11120010) {
                                 switch (skillID) {
                                    case 11121011:
                                       return 11101022;
                                    case 11121012:
                                       return 11111022;
                                    case 11121013:
                                       return 11121004;
                                    default:
                                       if (skillID == 11121055) {
                                          return 11121052;
                                       }
                                 }
                              } else {
                                 if (skillID == 10001253) {
                                    return 10000252;
                                 }

                                 if (skillID == 11120010) {
                                    return 11120009;
                                 }
                              }
                           } else if (skillID > 11121202) {
                              if (skillID == 12000026) {
                                 return 12001020;
                              }

                              if (skillID == 12001027 || skillID == 12001028) {
                                 return 12000023;
                              }

                              if (skillID == 12100028) {
                                 return 12100020;
                              }
                           } else {
                              if (skillID == 11121102) {
                                 return 11121101;
                              }

                              if (skillID == 11121202) {
                                 return 11121201;
                              }
                           }
                        } else if (skillID <= 13121009) {
                           if (skillID <= 12120014) {
                              if (skillID > 12110028) {
                                 if (skillID == 12111029) {
                                    return 12111023;
                                 }

                                 switch (skillID) {
                                    case 12120010:
                                       return 12120006;
                                    case 12120011:
                                       return 12121001;
                                    case 12120012:
                                       return 12121003;
                                    case 12120013:
                                    case 12120014:
                                       return 12121004;
                                 }
                              } else {
                                 if (skillID == 12100029) {
                                    return 12101024;
                                 }

                                 if (skillID == 12110028) {
                                    return 12110020;
                                 }
                              }
                           } else if (skillID > 13100027) {
                              if (skillID == 13110027) {
                                 return 13110022;
                              }

                              if (skillID == 13120010) {
                                 return 13120003;
                              }

                              if (skillID == 13121009) {
                                 return 13121002;
                              }
                           } else {
                              if (skillID == 12121055) {
                                 return 12121054;
                              }

                              if (skillID == 13100027) {
                                 return 13101022;
                              }
                           }
                        } else if (skillID <= 14111021) {
                           if (skillID > 14000029) {
                              if (skillID == 14101021) {
                                 return 14101020;
                              }

                              if (skillID == 14111021) {
                                 return 14111020;
                              }
                           } else {
                              if (skillID == 14000027) {
                                 return 14001027;
                              }

                              if (skillID == 14000028 || skillID == 14000029) {
                                 return 14000027;
                              }
                           }
                        } else if (skillID > 14121002) {
                           if (skillID == 14121055 || skillID == 14121056) {
                              return 14121054;
                           }

                           if (skillID == 21000004) {
                              return 21001009;
                           }

                           if (skillID == 21000006 || skillID == 21000007) {
                              return 21001010;
                           }
                        } else {
                           if (skillID == 14111023) {
                              return 14111022;
                           }

                           if (skillID == 14121002) {
                              return 14121001;
                           }
                        }
                     } else if (skillID <= 25121133) {
                        if (skillID <= 22170094) {
                           if (skillID <= 21120027) {
                              if (skillID > 21100018) {
                                 if (skillID > 21110028) {
                                    if (skillID != 21120018) {
                                       switch (skillID) {
                                          case 21120024:
                                             return 21120022;
                                          case 21120025:
                                             return 21111021;
                                          case 21120026:
                                             return 21120019;
                                          case 21120027:
                                             break;
                                          default:
                                             return skillID;
                                       }
                                    }

                                    return 21120023;
                                 }

                                 if (skillID != 21110011) {
                                    switch (skillID) {
                                       case 21110018:
                                          return 21111019;
                                       case 21110019:
                                       case 21110021:
                                       case 21110026:
                                          return skillID;
                                       case 21110020:
                                       case 21110027:
                                       case 21110028:
                                          return 21111021;
                                       case 21110022:
                                       case 21110023:
                                          return 21110026;
                                       case 21110024:
                                       case 21110025:
                                          break;
                                       default:
                                          return skillID;
                                    }
                                 }

                                 return 21111017;
                              }

                              if (skillID > 21100012) {
                                 if (skillID == 21100013) {
                                    return 21101017;
                                 }

                                 if (skillID == 21100018) {
                                    return 21101011;
                                 }

                                 return skillID;
                              }

                              if (skillID == 21100002) {
                                 return 21101011;
                              }

                              if (skillID == 21100012) {
                                 return 21101016;
                              }

                              return skillID;
                           }

                           if (skillID > 22110025) {
                              if (skillID > 22140024) {
                                 switch (skillID) {
                                    case 22170061:
                                       return 22170060;
                                    case 22170062:
                                    case 22170063:
                                       return skillID;
                                    case 22170064:
                                    case 22170065:
                                    case 22170066:
                                       return 22171063;
                                    case 22170067:
                                       break;
                                    default:
                                       if (skillID == 22170093) {
                                          return 22171063;
                                       }

                                       if (skillID == 22170094) {
                                          return 22171063;
                                       }

                                       return skillID;
                                 }
                              } else {
                                 switch (skillID) {
                                    case 22140013:
                                    case 22140015:
                                       break;
                                    case 22140014:
                                       return 22111012;
                                    default:
                                       switch (skillID) {
                                          case 22140022:
                                          case 22140024:
                                             break;
                                          case 22140023:
                                             return 22111012;
                                          default:
                                             return skillID;
                                       }
                                 }
                              }

                              return 22141012;
                           }

                           if (skillID <= 21121068) {
                              if (skillID != 21121016 && skillID != 21121017) {
                                 if (skillID == 21121068) {
                                    return 21121057;
                                 }

                                 return skillID;
                              }

                              return 21120022;
                           }

                           if ((skillID < 22110013 || skillID > 22110014) && (skillID < 22110022 || skillID > 22110025)) {
                              return skillID;
                           }

                           return 22111012;
                        }

                        if (skillID <= 25000003) {
                           if (skillID <= 23121015) {
                              if (skillID > 23101007) {
                                 if (skillID == 23111009 || skillID == 23111010 || skillID == 23111011) {
                                    return 23111008;
                                 }

                                 if (skillID == 23121015) {
                                    return 23121014;
                                 }
                              } else {
                                 if (skillID == 22171083) {
                                    return 22171080;
                                 }

                                 if (skillID == 23101007) {
                                    return 23101001;
                                 }
                              }
                           } else if (skillID > 24120055) {
                              if (skillID == 24121010) {
                                 return 24121003;
                              }

                              if (skillID == 25000001) {
                                 return 25001000;
                              }

                              if (skillID == 25000003) {
                                 return 25001002;
                              }
                           } else {
                              if (skillID == 24111008) {
                                 return 24111006;
                              }

                              if (skillID == 24120055) {
                                 return 24121052;
                              }
                           }
                        } else if (skillID <= 25111012) {
                           if (skillID > 25100010) {
                              if (skillID >= 25110001 && skillID <= 25110003) {
                                 return 25111000;
                              }

                              if (skillID == 25111012) {
                                 return 25111005;
                              }
                           } else {
                              if (skillID == 25100001 || skillID == 25100002) {
                                 return 25101000;
                              }

                              if (skillID == 25100010) {
                                 return 25100009;
                              }
                           }
                        } else if (skillID > 25120004) {
                           if (skillID == 25120115) {
                              return 25120110;
                           }

                           if (skillID == 25121055) {
                              return 25121030;
                           }

                           if (skillID == 25121133) {
                              return 25121131;
                           }
                        } else {
                           if (skillID == 25111211) {
                              return 25111209;
                           }

                           if (skillID >= 25120001 && skillID <= 25120004) {
                              return 25121000;
                           }
                        }
                     } else if (skillID <= 33001205) {
                        if (skillID <= 31201010) {
                           if (skillID <= 30011169) {
                              if (skillID > 30010184) {
                                 if (skillID == 30010186) {
                                    return 30010110;
                                 }

                                 if (skillID >= 30011167 && skillID <= 30011169) {
                                    return 30010166;
                                 }

                                 return skillID;
                              }

                              if (skillID == 27120211) {
                                 return 27121201;
                              }

                              if (skillID != 30010183 && skillID != 30010184) {
                                 return skillID;
                              }

                              return 30010110;
                           }

                           if (skillID > 31011007) {
                              if (skillID == 31121010) {
                                 return 31121000;
                              }

                              if (skillID >= 31201007 && skillID <= 31201010) {
                                 return 31201000;
                              }
                           } else {
                              if (skillID >= 31001006 && skillID <= 31001008) {
                                 return 31000004;
                              }

                              if (skillID >= 31011004 && skillID <= 31011007) {
                                 return 31011000;
                              }
                           }
                        } else if (skillID <= 31221014) {
                           if (skillID > 31211011) {
                              if (skillID == 31221014) {
                                 return 31221001;
                              }
                           } else {
                              if (skillID >= 31211007 && skillID <= 31211010) {
                                 return 31211000;
                              }

                              if (skillID == 31211011) {
                                 return 31211002;
                              }
                           }
                        } else if (skillID > 32120055) {
                           if (skillID == 32121011) {
                              return 32121004;
                           }

                           if (skillID == 33001202) {
                              return 33001102;
                           }

                           if (skillID == 33001205) {
                              return 33001105;
                           }
                        } else {
                           if (skillID == 32110020) {
                              return 32111016;
                           }

                           if (skillID == 32120055) {
                              return 32121052;
                           }
                        }
                     } else if (skillID <= 35121019) {
                        if (skillID <= 33121214) {
                           if (skillID > 33101215) {
                              if (skillID == 33111212) {
                                 return 33111112;
                              }

                              if (skillID == 33121214) {
                                 return 33121114;
                              }
                           } else {
                              if (skillID == 33101213) {
                                 return 33101113;
                              }

                              if (skillID == 33101215) {
                                 return 33101115;
                              }
                           }
                        } else if (skillID > 35111007) {
                           if (skillID == 35121011) {
                              return 35121009;
                           }

                           if (skillID == 35121016 || skillID == 35121019) {
                              return 35121015;
                           }
                        } else {
                           if (skillID == 33121255) {
                              return 33121155;
                           }

                           if (skillID == 35111007) {
                              return 35111006;
                           }
                        }
                     } else if (skillID <= 36121012) {
                        if (skillID > 36110004) {
                           if (skillID == 36111009 || skillID == 36111010) {
                              return 36111000;
                           }

                           if (skillID == 36121011 || skillID == 36121012) {
                              return 36121001;
                           }
                        } else {
                           if (skillID == 36101008 || skillID == 36101009) {
                              return 36101000;
                           }

                           if (skillID == 36110004) {
                              return 36111004;
                           }
                        }
                     } else if (skillID > 36121055) {
                        switch (skillID) {
                           case 37000005:
                              return 37001004;
                           case 37000006:
                           case 37000007:
                              break;
                           case 37000008:
                           case 37000011:
                           case 37000012:
                           case 37000013:
                              return 37001002;
                           case 37000009:
                              return 37001000;
                           case 37000010:
                              return 37001001;
                           default:
                              if (skillID == 37100002) {
                                 return 37101001;
                              }

                              if (skillID == 37100008) {
                                 return 37101000;
                              }
                        }
                     } else {
                        if (skillID == 36121013 || skillID == 36121014) {
                           return 36121002;
                        }

                        if (skillID == 36121055) {
                           return 36121052;
                        }
                     }

                     return skillID;
                  } else if (skillID > 131006022) {
                     if (skillID <= 155101204) {
                        if (skillID <= 152110004) {
                           if (skillID <= 142120002) {
                              if (skillID > 142100010) {
                                 if (skillID > 142110003) {
                                    if (skillID == 142110015) {
                                       return 142111002;
                                    } else {
                                       return skillID != 142120001 && skillID != 142120002 ? skillID : 142120000;
                                    }
                                 } else if (skillID == 142110001) {
                                    return 142110000;
                                 } else {
                                    return skillID == 142110003 ? 142111002 : skillID;
                                 }
                              } else if (skillID > 142100001) {
                                 if (skillID == 142100008) {
                                    return 142101003;
                                 } else {
                                    return skillID == 142100010 ? 142101009 : skillID;
                                 }
                              } else if (skillID == 142000006) {
                                 return 142001004;
                              } else {
                                 return skillID == 142100001 ? 142100000 : skillID;
                              }
                           } else if (skillID <= 152000010) {
                              if (skillID > 142120015) {
                                 if (skillID == 142120030) {
                                    return 142121030;
                                 } else {
                                    return skillID != 152000009 && skillID != 152000010 ? skillID : 152001002;
                                 }
                              } else if (skillID == 142120014) {
                                 return 142120000;
                              } else {
                                 return skillID == 142120015 ? 142121008 : skillID;
                              }
                           } else if (skillID > 152001005) {
                              if (skillID == 152101000 || skillID == 152101004) {
                                 return 152101003;
                              } else {
                                 return skillID == 152110004 ? 152111003 : skillID;
                              }
                           } else if (skillID == 152001002) {
                              return 152001001;
                           } else {
                              return skillID == 152001005 ? 152001004 : skillID;
                           }
                        } else if (skillID <= 155001205) {
                           if (skillID <= 152121006) {
                              if (skillID > 152120016) {
                                 if (skillID == 152120017) {
                                    return 152120008;
                                 } else {
                                    return skillID == 152121006 ? 152121005 : skillID;
                                 }
                              } else if (skillID == 152120002 || skillID == 152120003) {
                                 return 152120001;
                              } else {
                                 return skillID == 152120016 ? 152111003 : skillID;
                              }
                           } else if (skillID > 155001008) {
                              if (skillID == 155001009) {
                                 return 155001104;
                              } else if (skillID == 155001202) {
                                 return 155001102;
                              } else {
                                 return skillID != 155001204 && skillID != 155001205 ? skillID : 155001104;
                              }
                           } else if (skillID == 155001000 || skillID == 155001001) {
                              return 155001100;
                           } else {
                              return skillID == 155001008 ? 155000007 : skillID;
                           }
                        } else if (skillID <= 155101015) {
                           if (skillID > 155101003) {
                              return skillID != 155101013 && skillID != 155101015 ? skillID : 155101100;
                           } else if (skillID == 155100009) {
                              return 155101008;
                           } else {
                              return skillID != 155101002 && skillID != 155101003 ? skillID : 155101100;
                           }
                        } else if (skillID <= 155101112) {
                           return skillID != 155101101 && skillID != 155101112 ? skillID : 155101100;
                        } else if (skillID == 155101114) {
                           return 155101104;
                        } else {
                           return skillID == 155101204 ? 155101104 : skillID;
                        }
                     } else if (skillID <= 164101006) {
                        if (skillID > 155111212) {
                           if (skillID <= 155121041) {
                              if (skillID > 155121005) {
                                 if (skillID == 155121006 || skillID == 155121007) {
                                    return 155121306;
                                 } else {
                                    return skillID == 155121041 ? 155121341 : skillID;
                                 }
                              } else if (skillID == 155120001) {
                                 return 155120000;
                              } else if (skillID == 155120000) {
                                 return 155001102;
                              } else {
                                 return skillID >= 155121002 && skillID <= 155121005 ? 155121102 : skillID;
                              }
                           } else if (skillID <= 155121215) {
                              return skillID;
                           } else if (skillID == 164001002) {
                              return 164001001;
                           } else {
                              if (skillID != 164100000) {
                                 switch (skillID) {
                                    case 164101001:
                                    case 164101002:
                                       break;
                                    case 164101003:
                                    case 164101005:
                                       return skillID;
                                    case 164101004:
                                       return 164101003;
                                    case 164101006:
                                       return 164100006;
                                    default:
                                       return skillID;
                                 }
                              }

                              return 164101000;
                           }
                        } else if (skillID <= 155111005) {
                           if (skillID > 155101214) {
                              if (skillID == 155110001) {
                                 return 155110000;
                              } else if (skillID == 155110000) {
                                 return 155001102;
                              } else {
                                 return skillID >= 155111003 && skillID <= 155111005 ? 155111102 : skillID;
                              }
                           } else if (skillID == 155101212) {
                              return 155101100;
                           } else {
                              return skillID == 155101214 ? 155101104 : skillID;
                           }
                        } else if (skillID > 155111111) {
                           return skillID != 155111202 && skillID != 155111211 && skillID != 155111212 ? skillID : 155111102;
                        } else if (skillID == 155111006) {
                           return 155111306;
                        } else {
                           return skillID == 155111111 ? 155111102 : skillID;
                        }
                     } else if (skillID <= 164121044) {
                        if (skillID <= 164120000) {
                           if (skillID > 164110003) {
                              switch (skillID) {
                                 case 164111001:
                                 case 164111002:
                                 case 164111009:
                                 case 164111010:
                                    return 164111000;
                                 case 164111003:
                                 case 164111007:
                                 case 164111008:
                                    return skillID;
                                 case 164111004:
                                 case 164111005:
                                 case 164111006:
                                    return 164111003;
                                 default:
                                    return skillID == 164120000 ? 164121000 : skillID;
                              }
                           } else if (skillID == 164110000) {
                              return 164111000;
                           } else {
                              return skillID == 164110003 ? 164111003 : skillID;
                           }
                        } else if (skillID > 164121002) {
                           if (skillID == 164121011 || skillID == 164121012) {
                              return 164121006;
                           } else if (skillID == 164121015) {
                              return 164121008;
                           } else {
                              return skillID == 164121044 ? 164121043 : skillID;
                           }
                        } else if (skillID == 164120007) {
                           return 164121007;
                        } else {
                           return skillID != 164121001 && skillID != 164121002 ? skillID : 164121000;
                        }
                     } else if (skillID <= 400011087) {
                        if (skillID > 400010071) {
                           if (skillID == 400011065) {
                              return 400011055;
                           } else {
                              switch (skillID) {
                                 case 400011074:
                                 case 400011075:
                                 case 400011076:
                                 case 400011085:
                                 case 400011086:
                                 case 400011087:
                                    return 400011073;
                                 case 400011077:
                                 case 400011079:
                                 case 400011080:
                                 case 400011081:
                                 case 400011082:
                                 case 400011083:
                                    return skillID;
                                 case 400011078:
                                    return 400011077;
                                 case 400011084:
                                    return 400011083;
                                 default:
                                    return skillID;
                              }
                           }
                        } else if (skillID == 400001015) {
                           return 400001014;
                        } else {
                           return skillID == 400010071 ? 400010070 : skillID;
                        }
                     } else if (skillID <= 400041043) {
                        switch (skillID) {
                           case 400031026:
                           case 400031027:
                              return 400031025;
                           case 400031028:
                           case 400031030:
                           case 400031032:
                           case 400031034:
                           case 400031036:
                           case 400031037:
                              return skillID;
                           case 400031029:
                              return 400031028;
                           case 400031031:
                              return 400031030;
                           case 400031033:
                              return 400031032;
                           case 400031035:
                              return 400031034;
                           case 400031038:
                           case 400031039:
                           case 400031040:
                              return 400031037;
                           default:
                              return skillID == 400041043 ? 400041042 : skillID;
                        }
                     } else if (skillID == 400041045 || skillID == 400041046) {
                        return 400041040;
                     } else if (skillID == 400051045) {
                        return 400051044;
                     } else {
                        return skillID == 400051048 ? 400051047 : skillID;
                     }
                  } else if (skillID > 65121008) {
                     if (skillID <= 101000102) {
                        if (skillID <= 80001911) {
                           if (skillID <= 80001831) {
                              if (skillID <= 80001818) {
                                 switch (skillID) {
                                    case 80001681:
                                    case 80001682:
                                    case 80001683:
                                       return 80001698;
                                    case 80001684:
                                    case 80001685:
                                       return skillID;
                                    case 80001686:
                                    case 80001687:
                                    case 80001688:
                                    case 80001689:
                                       return 80001699;
                                    case 80001690:
                                    case 80001691:
                                    case 80001692:
                                    case 80001693:
                                       return 80001700;
                                    default:
                                       return skillID == 80001818 ? 80001817 : skillID;
                                 }
                              } else if (skillID >= 80001821 && skillID <= 80001823) {
                                 return 80001820;
                              } else {
                                 return skillID == 80001831 ? 80001830 : skillID;
                              }
                           } else if (skillID > 80001857) {
                              if (skillID >= 80001907 && skillID <= 80001909) {
                                 return 80001895;
                              } else {
                                 return skillID == 80001911 ? 80001910 : skillID;
                              }
                           } else if (skillID == 80001833) {
                              return 80001832;
                           } else {
                              switch (skillID) {
                                 case 80001842:
                                 case 80001843:
                                 case 80001845:
                                    return 80001841;
                                 case 80001844:
                                 case 80001846:
                                 case 80001848:
                                 case 80001851:
                                 case 80001852:
                                 case 80001853:
                                    return skillID;
                                 case 80001847:
                                    return 80001846;
                                 case 80001849:
                                 case 80001850:
                                    return 80001848;
                                 case 80001854:
                                 case 80001855:
                                 case 80001856:
                                 case 80001857:
                                    return 80001853;
                                 default:
                                    return skillID;
                              }
                           }
                        } else if (skillID <= 80002634) {
                           if (skillID > 80002445) {
                              if (skillID == 80002568) {
                                 return 80002592;
                              } else {
                                 return skillID == 80002634 ? 80002633 : skillID;
                              }
                           } else if (skillID == 80001926 || skillID == 80001927) {
                              return 80001925;
                           } else {
                              return skillID == 80002445 ? 80002444 : skillID;
                           }
                        } else if (skillID > 100001269) {
                           if (skillID == 100001275) {
                              return 100009999;
                           } else if (skillID == 100001281) {
                              return 100001274;
                           } else {
                              return skillID == 101000102 ? 101000101 : skillID;
                           }
                        } else if (skillID == 95001000) {
                           return 3111013;
                        } else {
                           return skillID == 100001269 ? 100001266 : skillID;
                        }
                     } else if (skillID > 131001011) {
                        if (skillID <= 131002022) {
                           if (skillID > 131001108) {
                              if (skillID == 131001208) {
                                 return 131001008;
                              } else {
                                 switch (skillID) {
                                    case 131002014:
                                       return 131000014;
                                    case 131002015:
                                    case 131002018:
                                    case 131002019:
                                       return skillID;
                                    case 131002016:
                                       return 131000016;
                                    case 131002017:
                                       return 131001017;
                                    case 131002020:
                                       return 131001020;
                                    case 131002021:
                                       return 131001021;
                                    case 131002022:
                                       return 131001022;
                                    default:
                                       return skillID;
                                 }
                              }
                           } else if (skillID == 131001104) {
                              return 131001004;
                           } else {
                              return skillID == 131001108 ? 131001008 : skillID;
                           }
                        } else if (skillID > 131003022) {
                           return skillID != 131004022 && skillID != 131005022 && skillID != 131006022 ? skillID : 131001022;
                        } else if (skillID == 131003017) {
                           return 131001017;
                        } else {
                           return skillID == 131003022 ? 131001022 : skillID;
                        }
                     } else if (skillID <= 101110104) {
                        if (skillID > 101000202) {
                           if (skillID == 101100202) {
                              return 101100201;
                           } else {
                              return skillID == 101110104 ? 101110102 : skillID;
                           }
                        } else if (skillID == 101000104) {
                           return 101000101;
                        } else {
                           return skillID == 101000202 ? 101000201 : skillID;
                        }
                     } else if (skillID > 101110204) {
                        switch (skillID) {
                           case 101120101:
                              return 101120100;
                           case 101120102:
                           case 101120104:
                              return skillID;
                           case 101120103:
                              return 101120102;
                           case 101120105:
                           case 101120106:
                              return 101120104;
                           default:
                              switch (skillID) {
                                 case 101120200:
                                    return 101121200;
                                 case 101120201:
                                 case 101120202:
                                 case 101120204:
                                    return skillID;
                                 case 101120203:
                                    return 101120202;
                                 case 101120205:
                                 case 101120206:
                                    return 101120204;
                                 default:
                                    return skillID == 131001011 ? 131001010 : skillID;
                              }
                        }
                     } else if (skillID == 101110201) {
                        return 101110200;
                     } else {
                        return skillID == 101110204 ? 101110203 : skillID;
                     }
                  } else if (skillID <= 61120008) {
                     if (skillID <= 37120059) {
                        if (skillID <= 37110010) {
                           if (skillID > 37110002) {
                              if (skillID == 37110004) {
                                 return 37111003;
                              } else {
                                 return skillID == 37110010 ? 37110007 : skillID;
                              }
                           } else if (skillID == 37100009) {
                              return 37100007;
                           } else {
                              return skillID != 37110001 && skillID != 37110002 ? skillID : 37111000;
                           }
                        } else if (skillID > 37120001) {
                           switch (skillID) {
                              case 37120013:
                                 return 37120008;
                              case 37120014:
                              case 37120015:
                              case 37120016:
                              case 37120017:
                              case 37120018:
                              case 37120019:
                              case 37120023:
                                 return 37121004;
                              case 37120020:
                              case 37120021:
                                 return skillID;
                              case 37120022:
                              case 37120024:
                                 return 37121003;
                              default:
                                 return skillID >= 37120055 && skillID <= 37120059 ? 37121052 : skillID;
                           }
                        } else if (skillID == 37110011) {
                           return 37111005;
                        } else {
                           return skillID == 37120001 ? 37121000 : skillID;
                        }
                     } else if (skillID > 61110211) {
                        if (skillID > 61111113) {
                           if (skillID != 61111114) {
                              switch (skillID) {
                                 case 61111215:
                                    return 61001101;
                                 case 61111216:
                                    return 61101100;
                                 case 61111217:
                                    return 61101101;
                                 case 61111218:
                                    return 61111100;
                                 case 61111219:
                                    return 61111101;
                                 case 61111220:
                                    return 61111002;
                                 case 61111221:
                                    return 61001002;
                                 default:
                                    if (skillID != 61120008) {
                                       return skillID;
                                    }
                              }
                           }

                           return 61111008;
                        } else if (skillID == 61110212) {
                           return 61001000;
                        } else {
                           return skillID == 61111113 ? 61111100 : skillID;
                        }
                     } else if (skillID > 61001005) {
                        if (skillID == 61110009) {
                           return 61111003;
                        } else {
                           return skillID == 61110211 ? 61101002 : skillID;
                        }
                     } else if (skillID == 60021279) {
                        return 60021278;
                     } else {
                        return skillID != 61001004 && skillID != 61001005 ? skillID : 61001000;
                     }
                  } else if (skillID <= 64101008) {
                     if (skillID <= 61121124) {
                        if (skillID > 61120219) {
                           return skillID != 61121116 && skillID != 61121124 ? skillID : 61121104;
                        } else if (skillID == 61120018) {
                           return 61121105;
                        } else {
                           return skillID == 61120219 ? 61001000 : skillID;
                        }
                     } else if (skillID > 61121203) {
                        switch (skillID) {
                           case 61121217:
                              return 61120007;
                           case 61121218:
                           case 61121219:
                           case 61121224:
                              return skillID;
                           case 61121220:
                              return 61121015;
                           case 61121221:
                           case 61121223:
                           case 61121225:
                              return 61121104;
                           case 61121222:
                              return 61121105;
                           default:
                              switch (skillID) {
                                 case 64001006:
                                    return 64001001;
                                 case 64001007:
                                 case 64001008:
                                 case 64001009:
                                 case 64001010:
                                 case 64001011:
                                 case 64001012:
                                    return 64001000;
                                 case 64001013:
                                    return 64001002;
                                 default:
                                    return skillID == 64101008 ? 64101002 : skillID;
                              }
                        }
                     } else if (skillID == 61121201) {
                        return 61121100;
                     } else {
                        return skillID == 61121203 ? 61121102 : skillID;
                     }
                  } else if (skillID <= 64121024) {
                     if (skillID > 64111012) {
                        if (skillID == 64111013) {
                           return 64110005;
                        } else {
                           switch (skillID) {
                              case 64121011:
                              case 64121016:
                                 return 64121003;
                              case 64121012:
                              case 64121013:
                              case 64121014:
                              case 64121015:
                              case 64121017:
                              case 64121018:
                              case 64121019:
                                 return 64121001;
                              case 64121020:
                                 return 64120006;
                              case 64121021:
                                 return skillID;
                              case 64121022:
                              case 64121023:
                              case 64121024:
                                 return 64121021;
                              default:
                                 return skillID;
                           }
                        }
                     } else if (skillID == 64101009) {
                        return 64100004;
                     } else {
                        return skillID == 64111012 ? 64111004 : skillID;
                     }
                  } else if (skillID > 65101006) {
                     if (skillID == 65111007) {
                        return 65111100;
                     } else if (skillID == 65120011) {
                        return 65121011;
                     } else {
                        return skillID != 65121007 && skillID != 65121008 ? skillID : 65121101;
                     }
                  } else if (skillID == 64121055) {
                     return 64121053;
                  } else {
                     return skillID == 65101006 ? 65101100 : skillID;
                  }
            }
         }
      } else {
         return 1241000;
      }
   }

   public static boolean isLuminousSkill(int skillID) {
      return isLuminous(getSkillRootFromSkill(skillID));
   }

   public static boolean isLarknessDarkSkill(int skillID) {
      return skillID != 20041222 && isLuminousSkill(skillID) ? skillID / 100 % 10 == 2 : false;
   }

   public static boolean isLarknessLightSkill(int skillID) {
      return skillID != 20041226 && isLuminousSkill(skillID) ? skillID / 100 % 10 == 1 : false;
   }

   public static boolean isLarknessMixSkill(int skillID) {
      if (!isLuminousSkill(skillID)) {
         return false;
      } else {
         return skillID == 27141000 ? true : skillID / 100 % 10 == 3;
      }
   }

   public static boolean isPassiveSkill(int skillID) {
      return skillID / 1000 % 10 == 0;
   }

   public static boolean isNoManaJob(int job) {
      return isDemonSlayer(job) || isDemonAvenger(job) || isAngelicBuster(job) || isZero(job) || isKinesis(job);
   }

   public static int getCursedRunesRate(int level) {
      switch (level) {
         case 1:
            return 50;
         case 2:
            return 65;
         case 3:
            return 80;
         case 4:
            return 100;
         default:
            return 0;
      }
   }

   public static boolean isRuneStoneSkill(int skillID) {
      switch (skillID) {
         case 80001427:
         case 80001428:
         case 80001432:
         case 80001757:
         case 80001762:
         case 80001875:
         case 80002280:
         case 80002281:
         case 80002282:
            return true;
         default:
            return false;
      }
   }

   public static HoyoungAttributes getHoyoungAttribute(int skillID) {
      switch (skillID) {
         case 164001000:
         case 164121003:
         case 164141011:
            return HoyoungAttributes.Human;
         case 164101000:
         case 164111003:
         case 164141005:
            return HoyoungAttributes.Earth;
         case 164111000:
         case 164121000:
         case 164141000:
            return HoyoungAttributes.Heaven;
         default:
            return HoyoungAttributes.None;
      }
   }

   public static int getCharmByAttributeLevel(int level) {
      switch (level) {
         case 2:
            return 15;
         case 3:
            return 20;
         default:
            return 10;
      }
   }

   public static boolean isTriggerSkill(int skillID) {
      switch (skillID) {
         case 151101000:
         case 151111000:
         case 151121000:
         case 151121002:
         case 151141000:
         case 151141001:
            return true;
         default:
            return false;
      }
   }

   public static boolean isMesoChair(int itemID) {
      switch (itemID) {
         case 3015440:
         case 3015650:
         case 3015651:
         case 3015895:
         case 3015897:
         case 3018430:
         case 3018450:
            return true;
         default:
            return false;
      }
   }

   public static long getDreamBreakerHP(int stage) {
      long hp = 220000000L;

      try {
         if (stage < 10) {
            return hp;
         } else {
            if (stage >= 10 && stage < 20) {
               hp = 500000000L;
            } else if (stage >= 20 && stage < 30) {
               hp = 1200000000L;
            } else if (stage >= 30 && stage < 40) {
               hp = 2300000000L;
            } else if (stage >= 40 && stage < 50) {
               hp = 5400000000L;
            } else if (stage >= 50 && stage < 60) {
               hp = 9750000000L;
            } else if (stage >= 60 && stage < 70) {
               hp = 15250000000L;
            } else if (stage >= 70 && stage < 80) {
               hp = 24700000000L;
            } else if (stage >= 80 && stage < 90) {
               hp = 36000000000L;
            } else if (stage == 90) {
               hp = 50000000000L;
            } else if (stage >= 91 && stage < 100) {
               hp = 87000000000L;
            } else if (stage == 100) {
               hp = 135000000000L;
            } else if (stage >= 101 && stage < 110) {
               hp = 335000000000L;
            } else if (stage >= 110 && stage < 120) {
               hp = 373000000000L;
            } else if (stage >= 120 && stage < 130) {
               hp = 403000000000L;
            } else if (stage >= 130 && stage < 140) {
               hp = 435000000000L;
            } else if (stage >= 140 && stage < 150) {
               hp = 469000000000L;
            } else if (stage >= 150 && stage < 160) {
               hp = 503000000000L;
            } else if (stage >= 160 && stage < 170) {
               hp = 533000000000L;
            } else if (stage >= 170 && stage < 180) {
               hp = 569000000000L;
            } else if (stage >= 180 && stage < 190) {
               hp = 603000000000L;
            } else if (stage >= 190 && stage < 200) {
               hp = 635000000000L;
            } else if (stage == 200) {
               hp = 669000000000L;
            } else if (stage == 201) {
               hp = 4700000000000L;
            } else if (stage > 201) {
               hp = 4700000000000L * (stage - 201);
            }

            return hp;
         }
      } catch (Exception var8) {
         return Long.MAX_VALUE;
      } finally {
         ;
      }
   }

   public static boolean isBlockedInnerAbility(int skillID) {
      return skillID == 70000037 || skillID == 70000038;
   }

   public static boolean isKeydownEndCooltimeSkill(int skillID) {
      if (skillID <= 31211001) {
         return skillID > 24121005
            ? skillID == 25111005 || skillID == 25121030 || skillID == 31211001
            : skillID == 2221052 || skillID == 12121054 || skillID == 24121005;
      } else if (skillID <= 65121003) {
         return skillID == 37121003 || skillID == 64121002 || skillID == 65121003;
      } else {
         return skillID <= 400011068 ? skillID == 155121341 || skillID == 400011068 : skillID == 400021072 || skillID == 400041039;
      }
   }

   public static boolean isAfterApplyCooltimeSkill(int skillID, int realSkillID) {
      switch (realSkillID) {
         case 2120013:
         case 2220014:
         case 22110023:
         case 22140022:
         case 22140024:
         case 22170066:
         case 22171063:
         case 32121011:
         case 61101002:
         case 61120018:
         case 64101002:
         case 64101008:
         case 400011081:
         case 400011082:
         case 400051026:
         case 400051048:
            return false;
         case 500061025:
            return true;
         default:
            return skillID != 152121004
               && skillID != 61121222
               && skillID != 61120047
               && skillID != 400011010
               && skillID != 31221001
               && skillID != 31241000
               && skillID != 31241001
               && skillID != 31221014
               && skillID != 25111206
               && skillID != 400021094
               && skillID != 400011131
               && (skillID < 400021096 || skillID > 400021098)
               && skillID != 400021104
               && (skillID < 400041063 || skillID > 400041067)
               && skillID != 400011136
               && skillID != 400051080
               && (skillID < 400011118 || skillID > 400011120)
               && skillID != 400011130
               && skillID != 400041057
               && skillID != 400041058
               && skillID != 400051068
               && skillID != 400021105
               && skillID != 400021108
               && skillID != 400021109
               && (skillID < 400041069 || skillID > 400041073)
               && skillID != 400041061
               && skillID != 400021086
               && skillID != 400021028
               && skillID != 400021029
               && skillID != 400021001
               && skillID != 400031022
               && skillID != 5221022
               && skillID != 142111006
               && skillID != 142120003
               && skillID != 33111013
               && skillID != 400031000
               && skillID != 12121001
               && skillID != 2301002
               && skillID != 5111009
               && skillID != 4341054
               && skillID != 151111003
               && skillID != 151001001
               && skillID != 151111002
               && skillID != 151121003
               && (skillID < 400021042 || skillID > 400021045)
               && (skillID < 400041003 || skillID > 400041005)
               && skillID != 400011081
               && skillID != 2221012
               && skillID != 142101009
               && skillID != 400021004
               && skillID != 400011109
               && skillID != 400011108
               && skillID != 12120011
               && skillID != 400021072
               && skillID != 31121005
               && skillID != 400011052
               && skillID != 400011053
               && skillID != 400041002
               && skillID != 31221014
               && skillID != 155111212
               && skillID != 61120018
               && skillID != 400031020
               && skillID != 400051008
               && skillID != 400041039;
      }
   }

   public static boolean isPrepareApplyCooltimeSkill(int skillID, int realSkillID) {
      switch (skillID) {
         case 2211012:
         case 3111018:
         case 3211019:
         case 4001003:
         case 5111007:
         case 5120012:
         case 5211007:
         case 5220014:
         case 5311005:
         case 5320007:
         case 14001023:
         case 14111030:
         case 15111011:
         case 22170064:
         case 23111008:
         case 35111013:
         case 35120014:
         case 154101004:
         case 154111004:
         case 154111011:
         case 154121009:
         case 400011089:
         case 400021102:
         case 400051001:
            return false;
         default:
            return skillID == 400020051 && realSkillID == 400021046
               ? false
               : skillID != 400021088
                  && skillID != 400011001
                  && skillID != 400041058
                  && skillID != 5221006
                  && skillID != 35111002
                  && skillID != 14110030
                  && skillID != 20031205
                  && (skillID < 400041003 || skillID > 400041005)
                  && realSkillID != 155111102;
      }
   }

   public static int getVeritasRoomPeriod(int count) {
      switch (count) {
         case 0:
         case 1:
            return 180;
         case 2:
            return 300;
         case 3:
            return 600;
         case 4:
            return 1800;
         case 5:
            return 3600;
         case 6:
            return 6000;
         case 7:
            return 8400;
         case 8:
            return 10800;
         default:
            return DBConfig.isGanglim ? 21600 : 259200;
      }
   }

   public static int getMParkMonsterExp(int mobID) {
      switch (mobID) {
         case 9800073:
            return 36320;
         case 9800074:
            return 45270;
         case 9800075:
         case 9800076:
         case 9800077:
            return 1469360;
         case 9800078:
            return 49260;
         case 9800079:
            return 51150;
         case 9800080:
            return 55020;
         case 9800081:
            return 59260;
         case 9800082:
         case 9800083:
         case 9800084:
         case 9800085:
            return 38080;
         case 9800086:
            return 39100;
         case 9800087:
            return 40120;
         case 9800088:
            return 41500;
         case 9800089:
            return 43000;
         case 9800090:
            return 1606200;
         case 9800091:
            return 1500000;
         case 9800092:
            return 40000;
         case 9800093:
            return 41100;
         case 9800094:
            return 42200;
         case 9800095:
            return 43550;
         case 9800096:
            return 44120;
         case 9800097:
            return 45550;
         case 9800098:
            return 47120;
         case 9800099:
            return 1806200;
         case 9800100:
            return 42000;
         case 9800101:
            return 43100;
         case 9800102:
            return 44200;
         case 9800103:
            return 45550;
         case 9800104:
            return 46120;
         case 9800105:
            return 1910200;
         case 9800106:
            return 89280;
         case 9800107:
            return 91100;
         case 9800108:
            return 3110000;
         case 9800109:
            return 3824500;
         case 9800110:
            return 93250;
         case 9800111:
            return 94880;
         case 9800112:
            return 96100;
         case 9800113:
            return 98220;
         case 9800114:
            return 100990;
         case 9800115:
            return 190000;
         case 9800116:
            return 195000;
         case 9800117:
            return 197100;
         case 9800118:
            return 199500;
         case 9800119:
            return 201580;
         case 9800120:
            return 203000;
         case 9800121:
            return 205410;
         case 9800122:
            return 207760;
         case 9800123:
            return 209990;
         case 9800124:
            return 231200;
         case 9800125:
         case 9800126:
         case 9800127:
         case 9800128:
         case 9800129:
         case 9800130:
         case 9800131:
         case 9800132:
         case 9800133:
         case 9800134:
         case 9800135:
         case 9800136:
         case 9800137:
         case 9800138:
         case 9800139:
         case 9800140:
         case 9800141:
         case 9800142:
         case 9800143:
         case 9800144:
         case 9800145:
         case 9800146:
         case 9800147:
         case 9800148:
         case 9800149:
         case 9800150:
         case 9800151:
         case 9800152:
         case 9800153:
         case 9800154:
         case 9800155:
         case 9800156:
         case 9800157:
         case 9800158:
         case 9800159:
         case 9800160:
         case 9800161:
         case 9800162:
         case 9800163:
         case 9800164:
         case 9800165:
         case 9800166:
         case 9800167:
         case 9800168:
         case 9800169:
         case 9800170:
         case 9800171:
         case 9800172:
         case 9800173:
         case 9800174:
         case 9800175:
         case 9800176:
         case 9800177:
         case 9800178:
         default:
            return 0;
         case 9800179:
            return 504190;
         case 9800180:
            return 552440;
         case 9800181:
            return 593110;
         case 9800182:
            return 632110;
         case 9800183:
            return 652440;
         case 9800184:
            return 681080;
         case 9800185:
            return 700100;
         case 9800186:
            return 723210;
         case 9800187:
            return 987080;
         case 9800188:
            return 1111000;
         case 9800189:
            return 1455020;
         case 9800190:
            return 1678520;
         case 9800191:
            return 1785220;
         case 9800192:
            return 1853240;
         case 9800193:
            return 1958970;
         case 9800194:
            return 2015780;
         case 9800195:
            return 2121050;
         case 9800196:
            return 2213450;
         case 9800197:
            return 44500000;
         case 9800198:
         case 9800199:
         case 9800200:
            return 2500120;
         case 9800201:
            return 2536730;
         case 9800202:
            return 2732110;
         case 9800203:
            return 2845550;
         case 9800204:
            return 2965410;
         case 9800205:
            return 3084440;
         case 9800206:
            return 3224520;
         case 9800207:
            return 3412120;
         case 9800208:
            return 3510550;
         case 9800209:
            return 3625480;
         case 9800210:
            return 3799850;
         case 9800211:
            return 3951250;
         case 9800212:
            return 4185420;
         case 9800213:
            return 4312150;
         case 9800214:
            return 48500000;
      }
   }

   public static int getWeaponTypeIndex(int itemID) {
      if (itemID / 1000000 == 1) {
         int type = itemID / 10000 % 100;

         for (int i = 0; i < weaponTypes.length; i++) {
            if (weaponTypes[i] == type) {
               return i;
            }
         }
      }

      return -1;
   }

   public static boolean checkSkillRoot(int job, int skillID) {
      int j = skillID / 10000;
      if (isCommonSkill(skillID)) {
         return true;
      } else {
         if (j == 8000) {
            j = skillID / 100;
         }

         if (j == 9500) {
            return true;
         } else {
            int sr = getSkillRootFromSkill(skillID);
            if (sr == 501) {
               return isCannon(j);
            } else if (sr == 301) {
               return isCannon(job);
            } else if (sr == 10112) {
               return isZero(job);
            } else if (isCannon(job) && sr == 500) {
               return false;
            } else {
               return sr % 100 == 0 ? sr / 100 == job / 100 : sr / 10 == job / 10 && sr % 10 <= job % 10;
            }
         }
      }
   }

   public static boolean isTowerChair(int itemID) {
      return itemID / 1000 == 3017;
   }

   public static boolean isArmorPiercingPossibleSkill(int skillID) {
      return SkillFactory.getSkill(3120018).getSkillList().contains(skillID);
   }

   public static boolean isCardinalDischargeSkill(int skillID) {
      return skillID == 3011004 || skillID == 33000002 || skillID == 3321003;
   }

   public static boolean isCardinalBlastSkill(int skillID) {
      return skillID == 3301003
         || skillID == 3301004
         || skillID == 3310001
         || skillID == 3311013
         || skillID == 3321004
         || skillID == 3321005
         || skillID == 3341000
         || skillID == 3341001;
   }

   public static boolean isCardinalTransitionSkill(int skillID) {
      return skillID == 3311002 || skillID == 3311003 || skillID == 3321006 || skillID == 3321007;
   }

   public static boolean isCardinalForceSkill(int skillID) {
      return isCardinalDischargeSkill(skillID) || isCardinalBlastSkill(skillID) || isCardinalTransitionSkill(skillID);
   }

   public static boolean isExceptionCancelSkillByDead(SecondaryStatFlag flag) {
      return flag == SecondaryStatFlag.RelicPattern || flag == SecondaryStatFlag.RelicCharge || flag == SecondaryStatFlag.AncientGuardians;
   }

   public static boolean isExceptionCancelSkillByDead(int skillID) {
      return (skillID < 888888 || skillID > 888895)
            && (skillID < 797878 || skillID > 797891)
            && (skillID < 80003203 || skillID > 80003207)
            && (skillID < 80003170 || skillID > 80003199)
            && (skillID < 777799 || skillID > 777805)
            && (skillID < 787878 || skillID > 787899)
            && skillID != 3310006
            && skillID != 3311002
            && skillID != 400011131
            && skillID != 400021086
            && skillID != 400041074
            && skillID != 80002635
            && skillID != 80002636
            && skillID != 80002637
         ? DBConfig.isGanglim && (skillID == 80002419 || skillID >= 80003160 && skillID <= 80003180)
         : true;
   }

   public static boolean isApplicationJob(int skillID, int job) {
      if ((
            skillID == 1121053
               || skillID == 1221053
               || skillID == 1321053
               || skillID == 2121053
               || skillID == 2221053
               || skillID == 2321053
               || skillID == 3121053
               || skillID == 3221053
               || skillID == 3321053
               || skillID == 4121053
               || skillID == 4221053
               || skillID == 4341053
               || skillID == 5121053
               || skillID == 5221053
               || skillID == 5321053
         )
         && isAdventurer(job)) {
         return true;
      } else if (skillID != 11121053 && skillID != 12121053 && skillID != 13121053 && skillID != 14121053 && skillID != 15121053) {
         if ((
               skillID == 21121053
                  || skillID == 22171053
                  || skillID == 22171082
                  || skillID == 23121053
                  || skillID == 24121053
                  || skillID == 25121132
                  || skillID == 27121053
            )
            && isHero(job)) {
            return true;
         } else if ((skillID == 31121053 || skillID == 31221053) && isResistance(job)) {
            return true;
         } else if ((skillID == 32121053 || skillID == 33121053 || skillID == 35121053 || skillID == 37121053) && isResistance(job)) {
            return true;
         } else {
            return skillID != 51121053 || !isCygnus(job) && !isMihile(job)
               ? (skillID == 151121042 || skillID == 152121042 || skillID == 155121042 || skillID == 154121042) && isFlora(job)
               : true;
         }
      } else {
         return !isCygnus(job) && !isMihile(job) ? true : true;
      }
   }

   public static boolean isSameJobApplicationSkill(int skillID) {
      if (skillID == 1121053
         || skillID == 1221053
         || skillID == 1321053
         || skillID == 2121053
         || skillID == 2221053
         || skillID == 2321053
         || skillID == 3121053
         || skillID == 3221053
         || skillID == 3321053
         || skillID == 4121053
         || skillID == 4221053
         || skillID == 4341053
         || skillID == 5121053
         || skillID == 5221053
         || skillID == 5321053) {
         return true;
      } else if (skillID == 11121053 || skillID == 12121053 || skillID == 13121053 || skillID == 14121053 || skillID == 15121053) {
         return true;
      } else if (skillID == 21121053
         || skillID == 22171053
         || skillID == 22171082
         || skillID == 23121053
         || skillID == 24121053
         || skillID == 25121132
         || skillID == 27121053) {
         return true;
      } else if (skillID == 31121053 || skillID == 31221053) {
         return true;
      } else if (skillID == 32121053 || skillID == 33121053 || skillID == 35121053 || skillID == 37121053) {
         return true;
      } else {
         return skillID == 51121053 ? true : skillID == 151121042 || skillID == 152121042 || skillID == 155121042 || skillID == 154121042;
      }
   }

   public static boolean isTauntSkill(int skillID) {
      switch (skillID) {
         case 4341006:
         case 13111004:
         case 13111024:
         case 13120007:
         case 131001307:
            return true;
         default:
            return false;
      }
   }

   public static boolean isIntensePowerCrystal(int itemID) {
      return itemID == 4001886;
   }

   public static IntensePowerCrystalData getIntensePowerCrystalData(int bossId) {
      return intensePowerCrystal.containsKey(bossId) ? intensePowerCrystal.get(bossId) : null;
   }

   public static int fuseMobSkillInfo(int mobSkillID, int skillLevel) {
      return mobSkillID | skillLevel << 16;
   }

   public static boolean isPossessSkill(int skillID) {
      switch (skillID) {
         case 63001100:
         case 63100100:
         case 63100104:
         case 63101100:
         case 63101104:
         case 63110103:
         case 63111103:
         case 63111104:
         case 63111105:
         case 63111106:
         case 63120102:
         case 63120140:
         case 63121102:
         case 63121103:
         case 63121140:
         case 63121141:
         case 400031061:
            return true;
         default:
            return false;
      }
   }

   public static boolean isKainStackSkill(int skillID) {
      switch (skillID) {
         case 63101004:
         case 63111003:
         case 63121002:
         case 63121040:
         case 63141000:
            return true;
         default:
            return false;
      }
   }

   public static boolean isGenesisWeapon(int itemID) {
      for (int weaponID : bmWeapons) {
         if (itemID == weaponID || itemID == weaponID + 1) {
            return true;
         }
      }

      return false;
   }

   public static boolean isUnkSkill1(int skillId) {
      switch (skillId) {
         case 21000006:
         case 21000007:
         case 21001010:
         case 21110022:
         case 21110023:
         case 21110026:
         case 21110028:
         case 21120025:
         case 21141501:
         case 21141502:
         case 21141503:
         case 21141504:
         case 80001925:
         case 80001926:
         case 80001927:
         case 80001936:
         case 80001937:
         case 80001938:
            return true;
         default:
            return false;
      }
   }

   public static boolean isUnkSkill2(int skillID) {
      return skillID != 37100002 && skillID != 37101001 && skillID != 37110004 ? false : false;
   }

   public static boolean isChainArtsChase2(int skillID) {
      return skillID == 64001009 || skillID == 64001010 || skillID == 64001011;
   }

   public static boolean isChainArtsChase(int skillID) {
      return skillID == 64001000 || skillID == 64001008;
   }

   public static boolean isSpecialMovingSkill(int skillId) {
      boolean v1;
      if (skillId > 27120211) {
         if (skillId > 400020010) {
            if (skillId == 400020011 || skillId == 400021029 || skillId == 400021053) {
               return true;
            }

            v1 = skillId == 400031035;
         } else {
            if (skillId == 400020009 || skillId == 400020010 || skillId == 33141005 || skillId == 64111012) {
               return true;
            }

            v1 = skillId == 80003387;
         }
      } else {
         if (skillId == 27120211) {
            return true;
         }

         if (skillId > 3321005) {
            if (skillId == 3321039 || skillId == 3341001 || skillId == 4101014) {
               return true;
            }

            v1 = skillId == 5211021;
         } else {
            if (skillId == 3321005 || skillId == 3221024 || skillId == 3301004 || skillId == 3311011) {
               return true;
            }

            v1 = skillId == 3311013;
         }
      }

      if (v1) {
         return true;
      } else {
         boolean result = findProcessType(skillId, 39);
         return result ? true : result;
      }
   }

   public static boolean isKinesisPsychicLockSkill(int skillID) {
      return skillID <= 142110015
         ? skillID == 142100010 || skillID == 142110003 || skillID == 142110015
         : skillID == 142111002 || skillID == 142120001 || skillID == 142120000 || skillID == 142120002 || skillID == 142120014;
   }

   public static boolean isKinesisPsychicTornadoSkill(int skillID) {
      return skillID == 400021008;
   }

   public static boolean isSwiftOfWindSkill(int skillID) {
      return skillID == 22110014 || skillID == 22110025 || skillID == 80001939;
   }

   public static boolean isAntiRepeatSkill(int skillID) {
      boolean v1 = false;
      if (skillID > 80000365) {
         if (skillID > 400011028) {
            if (skillID > 400021029) {
               if (skillID > 400041039) {
                  if (skillID > 400051041) {
                     if (skillID > 400051067) {
                        v1 = skillID == 400051334;
                        return isAntiRepeatSkill_(v1, skillID);
                     }

                     if (skillID == 400051067) {
                        return true;
                     }

                     if (skillID == 400051067) {
                        return true;
                     }

                     if (skillID >= 400051049) {
                        if (skillID <= 400051050) {
                           return false;
                        }

                        v1 = skillID == 400051065;
                        return isAntiRepeatSkill_(v1, skillID);
                     }
                  } else {
                     if (skillID == 400051041) {
                        return true;
                     }

                     switch (skillID) {
                        case 400051006:
                        case 400051015:
                        case 400051017:
                        case 400051035:
                           return true;
                     }
                  }
               } else {
                  if (skillID == 400041039) {
                     return true;
                  }

                  if (skillID > 400031027) {
                     if (skillID > 400041007) {
                        v1 = skillID == 400041031;
                     } else {
                        if (skillID >= 400041006 || skillID == 400031036 || skillID == 400031067) {
                           return true;
                        }

                        v1 = skillID == 400031046;
                     }

                     return isAntiRepeatSkill_(v1, skillID);
                  }

                  if (skillID >= 400031025) {
                     return true;
                  }

                  if (skillID > 400021076) {
                     if (skillID < 400031003) {
                        return isAntiRepeatSkill_(v1, skillID);
                     }

                     if (skillID <= 400031004) {
                        return true;
                     }

                     v1 = skillID == 400031016;
                     return isAntiRepeatSkill_(v1, skillID);
                  }

                  if (skillID >= 400021075) {
                     return true;
                  }

                  if (skillID >= 400021061) {
                     if (skillID <= 400021062) {
                        return true;
                     }

                     v1 = skillID == 400021072;
                     return isAntiRepeatSkill_(v1, skillID);
                  }
               }
            } else {
               if (skillID == 400021029) {
                  return true;
               }

               switch (skillID) {
                  case 400011046:
                  case 400011047:
                  case 400011050:
                  case 400011052:
                  case 400011053:
                  case 400011068:
                  case 400011069:
                  case 400011072:
                  case 400011084:
                  case 400011089:
                  case 400011106:
                  case 400011107:
                  case 400011118:
                  case 400011132:
                  case 400011134:
                  case 400011135:
                  case 400011136:
                     return true;
               }
            }
         } else {
            if (skillID == 400011028) {
               return true;
            }

            if (skillID > 101110102) {
               if (skillID > 152120013) {
                  if (skillID > 400001011) {
                     v1 = skillID == 400001018;
                  } else {
                     if (skillID == 400001011 || skillID == 155121041) {
                        return true;
                     }

                     v1 = skillID == 155121341;
                  }
               } else {
                  if (skillID == 152120013) {
                     return true;
                  }

                  if (skillID <= 152100012) {
                     if (skillID != 152100012 && skillID != 131001004 && (skillID <= 131001019 || skillID > 131001021)) {
                        return isAntiRepeatSkill_(v1, skillID);
                     }

                     return true;
                  }

                  if (skillID == 152110010) {
                     return true;
                  }

                  v1 = skillID == 152120002;
               }

               return isAntiRepeatSkill_(v1, skillID);
            }

            if (skillID >= 101110101) {
               return true;
            }

            if (skillID > 80001863) {
               if (skillID > 80002895) {
                  v1 = skillID == 95001001;
               } else {
                  if (skillID == 80002895 || skillID == 80001887) {
                     return true;
                  }

                  v1 = skillID == 80002685;
               }

               return isAntiRepeatSkill_(v1, skillID);
            }

            if (skillID == 80001863) {
               return true;
            }

            if (skillID > 80001588) {
               if (skillID == 80001836) {
                  return true;
               }

               v1 = skillID - 80001836 == 21;
               return isAntiRepeatSkill_(v1, skillID);
            }

            if (skillID == 80001588) {
               return true;
            }

            if (skillID >= 80001275) {
               if (skillID <= 80001276) {
                  return true;
               }

               v1 = skillID == 80001387;
               return isAntiRepeatSkill_(v1, skillID);
            }
         }
      } else {
         if (skillID == 80000365) {
            return true;
         }

         if (skillID <= 27111100) {
            if (skillID == 27111100) {
               return true;
            }

            if (skillID <= 13121009) {
               if (skillID == 13121009) {
                  return true;
               }

               if (skillID > 3321005) {
                  if (skillID > 12121054) {
                     if (skillID == 13111020) {
                        return true;
                     }

                     v1 = skillID == 13121001;
                  } else {
                     if (skillID == 12121054 || skillID == 4341052 || skillID == 5221004) {
                        return true;
                     }

                     v1 = skillID == 11121013;
                  }
               } else {
                  if (skillID == 3321005) {
                     return true;
                  }

                  if (skillID > 3121020) {
                     if (skillID == 3301004) {
                        return true;
                     }

                     v1 = skillID == 3311013;
                  } else {
                     if (skillID == 3121020 || skillID == 1311011 || skillID == 2221052) {
                        return true;
                     }

                     v1 = skillID == 3111013;
                  }
               }

               return isAntiRepeatSkill_(v1, skillID);
            }
         }

         if (skillID <= 36121000) {
            if (skillID == 36121000) {
               return true;
            }

            if (skillID > 33000036) {
               if (skillID > 35121015) {
                  if (skillID == 36101001) {
                     return true;
                  }

                  v1 = skillID == 36110005;
               } else {
                  if (skillID == 35121015 || skillID == 33121009 || skillID - 33121009 == 105) {
                     return true;
                  }

                  v1 = skillID - 33121009 == 205;
               }
            } else {
               if (skillID == 33000036) {
                  return true;
               }

               if (skillID > 31111005) {
                  if (skillID == 31121005) {
                     return true;
                  }

                  v1 = skillID == 31211001;
               } else {
                  if (skillID == 31111005 || skillID == 30021238 || skillID == 31001000) {
                     return true;
                  }

                  v1 = skillID == 31101000;
               }
            }

            return isAntiRepeatSkill_(v1, skillID);
         }

         if (skillID <= 37120019) {
            if (skillID >= 37120013) {
               return true;
            }

            if (skillID > 37110006) {
               if (skillID < 37110010) {
                  return isAntiRepeatSkill_(v1, skillID);
               }

               if (skillID <= 37110011) {
                  return true;
               }

               v1 = skillID == 37120001;
            } else {
               if (skillID == 37110006) {
                  return true;
               }

               if (skillID <= 37100009) {
                  if (skillID < 37100008 && (skillID < 37000007 || skillID > 37000009)) {
                     return isAntiRepeatSkill_(v1, skillID);
                  }

                  return true;
               }

               v1 = skillID == 37110002;
            }

            return isAntiRepeatSkill_(v1, skillID);
         }

         if (skillID > 64121002) {
            if (skillID == 65101006) {
               return true;
            }

            v1 = skillID == 65121003;
            return isAntiRepeatSkill_(v1, skillID);
         }

         if (skillID == 64121002) {
            return true;
         }

         switch (skillID) {
            case 37120022:
            case 37120023:
            case 37120024:
            case 37120055:
            case 37120056:
            case 37120057:
            case 37120058:
               return true;
         }
      }

      return isAntiRepeatAttackSkill(skillID);
   }

   public static boolean isAntiRepeatSkill_(boolean result, int skillID) {
      return !result ? isAntiRepeatAttackSkill(skillID) : true;
   }

   public static boolean isAntiRepeatAttackSkill(int skillID) {
      Skill skill = SkillFactory.getSkill(skillID);
      if (skill == null) {
         return false;
      } else {
         return skill.getType() != 53
               && skillID != 80001587
               && skillID != 80001629
               && skillID != 80003748
               && !isForceAtomAttackSkill(skillID)
               && (skillID < 80001389 || skillID > 80001392)
               && (skillID < 80002563 || skillID > 80002566)
               && !isSecondForceAtomAttackSkill(skillID)
               && skillID != 35101002
               && skillID != 35110017
            ? findProcessType(skillID, 18)
            : true;
      }
   }

   public static boolean isBeyonderSkill(int skillId) {
      boolean v2;
      int v3;
      if (skillId > 21141000) {
         v3 = skillId - 21141001;
         v2 = v3 == 0;
      } else {
         if (skillId == 21141000) {
            return true;
         }

         int v1 = skillId - 21120022;
         if (skillId == 21120022) {
            return true;
         }

         v3 = v1 - 994;
         v2 = v3 == 0;
      }

      return v2 || v3 == 1;
   }

   public static boolean sub_140A874C0(int skillId) {
      return (
            skillId == 152141004
               || skillId == 152141005
               || skillId == 152141006
               || skillId == 152110004
               || skillId == 152120016
               || skillId == 155121003
               || skillId == 155141018
         )
         && isUnknown379_2(skillId)
         && skillId == 152141006;
   }

   public static boolean sub_140A5FC90(int skillId) {
      boolean v1;
      if (skillId > 151001001) {
         if (skillId > 400011119) {
            if (skillId == 400011120 || skillId == 400021092 || skillId == 400031063 || skillId == 400041058) {
               return true;
            }

            v1 = skillId == 400051069;
         } else {
            if (skillId == 400011119 || skillId == 151111002 || skillId == 151111003 || skillId == 151121003 || skillId == 151141002) {
               return true;
            }

            v1 = skillId == 400011108;
         }
      } else {
         if (skillId == 151001001
            || skillId == 5201017
            || skillId == 2121052
            || skillId == 2311017
            || skillId == 3111016
            || skillId == 3341003
            || skillId == 5121027
            || skillId == 11121018
            || skillId == 14141501
            || skillId == 21141003
            || skillId == 63101006) {
            return true;
         }

         v1 = skillId == 80003148;
      }

      if (v1) {
         return true;
      } else {
         boolean result = findProcessType(skillId, 59);
         return result ? true : result;
      }
   }

   public static boolean isSecondForceAtomAttackSkill(int skillID) {
      boolean v1 = false;
      if (skillID <= 400011108) {
         if (skillID == 400011108) {
            return true;
         } else {
            if (skillID > 151111003) {
               v1 = skillID == 151121003;
            } else {
               if (skillID >= 151111002 || skillID == 63101006) {
                  return true;
               }

               v1 = skillID == 151001001;
            }

            return isSecondForceAtomAttackSkill_(v1, skillID);
         }
      } else if (skillID > 400041058) {
         v1 = skillID == 400051069;
         return isSecondForceAtomAttackSkill_(v1, skillID);
      } else {
         if (skillID != 400041058) {
            if (skillID < 400011119) {
               return findProcessType(skillID, 59);
            }

            if (skillID > 400011120) {
               v1 = skillID == 400021092;
               return isSecondForceAtomAttackSkill_(v1, skillID);
            }
         }

         return true;
      }
   }

   private static boolean isSecondForceAtomAttackSkill_(boolean result, int skillID) {
      return !result ? findProcessType(skillID, 59) : true;
   }

   public static boolean isForceAtomAttackSkill(int skillID) {
      boolean v2 = false;
      boolean v3 = false;
      if (skillID > 61120007) {
         if (skillID == 61121217 || skillID > 400011057 && skillID <= 400011059) {
            return true;
         }
      } else if (skillID == 61120007 || skillID == 61101002 || skillID == 61110211) {
         return true;
      }

      if (skillID > 36110012) {
         v2 = skillID == 36120015;
      } else {
         if (skillID == 36110012 || skillID == 36001005) {
            return true;
         }

         v2 = skillID == 36100010;
      }

      if (!v2
         && skillID != 4100012
         && skillID != 4120019
         && skillID != 35101002
         && skillID != 35110017
         && skillID != 152110004
         && skillID != 152120016
         && skillID != 155121003
         && skillID != 155121003
         && skillID != 155111003
         && skillID != 155001000
         && skillID != 155101002
         && skillID != 155111207
         && skillID != 63111010
         && skillID != 22141017
         && skillID != 22170070
         && (skillID < 80002602 || skillID > 80002621)
         && skillID != 3011004
         && skillID != 3300002
         && skillID != 3321003) {
         if (skillID > 65111007) {
            if (skillID > 400021001) {
               if (skillID > 400041010) {
                  if (skillID > 400041049) {
                     if (skillID == 400041068) {
                        return true;
                     }

                     v3 = skillID == 400051017;
                  } else {
                     if (skillID == 400041049 || skillID == 400041023) {
                        return true;
                     }

                     v3 = skillID - 400041023 == 15;
                  }

                  return isForceAtomAttackSkill_(v3, skillID);
               }

               if (skillID == 400041010) {
                  return true;
               }

               if (skillID > 400031029) {
                  if (skillID == 400031031) {
                     return true;
                  }

                  v3 = skillID - 400031031 == 23;
                  return isForceAtomAttackSkill_(v3, skillID);
               }

               if (skillID == 400031029) {
                  return true;
               }

               if (skillID <= 400031000) {
                  if (skillID == 400031000) {
                     return true;
                  }

                  v3 = skillID == 400021045;
                  return isForceAtomAttackSkill_(v3, skillID);
               }

               if (skillID >= 400031020) {
                  if (skillID > 400031022) {
                     return false;
                  }

                  return true;
               }
            } else {
               if (skillID == 400021001) {
                  return true;
               }

               if (skillID <= 152001001) {
                  if (skillID == 152001001) {
                     return true;
                  }

                  if (skillID > 80002811) {
                     if (skillID == 131003016) {
                        return true;
                     }

                     v3 = skillID == 142110011;
                  } else {
                     if (skillID == 80002811 || skillID == 65120011 || skillID == 80001588) {
                        return true;
                     }

                     v3 = skillID == 80001890;
                  }

                  return isForceAtomAttackSkill_(v3, skillID);
               }

               if (skillID > 164101004) {
                  if (skillID == 164120007) {
                     return true;
                  }

                  return skillID == 400011131;
               }

               if (skillID == 164101004) {
                  return true;
               }

               if (skillID >= 152120001) {
                  if (skillID <= 152120002) {
                     return true;
                  }

                  v3 = skillID == 155100009;
                  return isForceAtomAttackSkill_(v3, skillID);
               }
            }
         } else {
            if (skillID == 65111007) {
               return true;
            }

            if (skillID <= 13101022) {
               if (skillID == 13101022) {
                  return true;
               }

               if (skillID > 4210014) {
                  if (skillID > 12110028) {
                     if (skillID == 12120010) {
                        return true;
                     }

                     v3 = skillID == 13100027;
                  } else {
                     if (skillID == 12110028 || skillID == 12000026) {
                        return true;
                     }

                     v3 = skillID == 12100028;
                  }
               } else {
                  if (skillID == 4210014) {
                     return true;
                  }

                  if (skillID > 3300005) {
                     if (skillID == 3301009) {
                        return true;
                     }

                     v3 = skillID == 3321037;
                  } else {
                     if (skillID == 3300005 || skillID == 2121055 || skillID == 31000010) {
                        return true;
                     }

                     v3 = skillID == 3120017;
                  }
               }

               return isForceAtomAttackSkill_(v3, skillID);
            }

            if (skillID > 24100003) {
               if (skillID > 25120115) {
                  if (skillID == 31221014) {
                     return true;
                  }

                  v3 = skillID == 36110004;
               } else {
                  if (skillID == 25120115 || skillID == 24120002) {
                     return true;
                  }

                  v3 = skillID == 25100010;
               }

               return isForceAtomAttackSkill_(v3, skillID);
            }

            if (skillID == 24100003) {
               return true;
            }

            if (skillID <= 13120010) {
               if (skillID != 13120010 && skillID != 13110022 && skillID - 13110022 != 5) {
                  v3 = skillID - 13110022 == 9981;
                  return isForceAtomAttackSkill_(v3, skillID);
               }

               return true;
            }

            if (skillID == 13121054) {
               return true;
            }

            if (skillID > 14000027) {
               if (skillID > 14000029) {
                  return false;
               }

               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private static boolean isForceAtomAttackSkill_(boolean result, int skillID) {
      if (result) {
         return true;
      } else if (skillID == 13121054) {
         return true;
      } else {
         return skillID > 14000027 ? skillID <= 14000029 : false;
      }
   }

   public static boolean sub_81A420(int a1) {
      if (!findProcessType(a1, 19)) {
         return false;
      } else {
         if (a1 > 155121002) {
            if (a1 - 400051047 > 1) {
               return false;
            }
         } else if (a1 != 155121002 && a1 != 155101013 && a1 != 155101015) {
            return false;
         }

         return true;
      }
   }

   public static boolean canConsumeAttackSkill(int skillID) {
      switch (skillID) {
         case 4221052:
         case 22170094:
         case 25111012:
         case 25121055:
         case 63101004:
         case 63101005:
         case 63101006:
         case 63111003:
         case 63111004:
         case 63111005:
         case 63111103:
         case 63111104:
         case 63111105:
         case 63111106:
         case 63121041:
         case 63121042:
         case 63121102:
         case 63121103:
         case 63121140:
         case 63121141:
         case 80001762:
         case 80003017:
         case 80003025:
         case 80003026:
         case 152121041:
         case 154111004:
         case 154111011:
         case 154121009:
         case 154121012:
         case 400001011:
         case 400011099:
         case 400011101:
         case 400011109:
         case 400021029:
         case 400021124:
         case 400021125:
         case 400021126:
         case 400021127:
         case 400021128:
         case 400021129:
         case 400021131:
         case 400031003:
         case 400031046:
         case 400031061:
         case 400031062:
         case 400031063:
         case 400031064:
         case 400031065:
         case 400031066:
         case 400031068:
         case 400041039:
         case 400041055:
         case 400041056:
         case 400041059:
         case 400041060:
         case 400051015:
            return false;
         default:
            switch (skillID) {
               case 3321012:
               case 151101001:
               case 155121202:
               case 155121215:
               case 400011038:
               case 400041026:
                  return true;
               default:
                  if (!isThrowBombSkill(skillID)
                     && !isStormArrowSkill(skillID)
                     && !isForceAtomAttackSkill(skillID)
                     && !isRWBonusAttack(skillID)
                     && !isBattleshipBomber(skillID)
                     && !isFieldAttackObjSkill(skillID)
                     && !isShootObjSkill(skillID)
                     && getNoviceSkillFromRace(skillID) != 1095
                     && skillID != 4341052
                     && skillID != 35111003
                     && skillID != 400051006
                     && skillID != 400041007
                     && skillID != 5220023
                     && skillID != 12120011
                     && skillID != 32111016
                     && skillID != 33000036
                     && skillID != 35121019
                     && skillID != 142120030
                     && skillID != 131000016
                     && skillID != 142100010
                     && skillID != 21100015
                     && skillID != 21120021
                     && skillID != 21110027
                     && skillID != 21120024
                     && skillID != 21120027
                     && skillID != 400010000
                     && skillID != 400040006
                     && skillID != 3310004
                     && skillID != 400031011
                     && skillID != 400010028
                     && skillID != 400031016
                     && skillID != 3221019
                     && skillID != 400041024
                     && skillID != 152110004
                     && skillID != 152120016
                     && skillID != 155121003
                     && (skillID < 400051018 || skillID > 400051020)
                     && skillID != 64121012
                     && skillID != 64121017
                     && skillID != 64121018
                     && skillID != 64121019
                     && skillID != 64121055
                     && skillID != 2221011
                     && skillID != 400001038
                     && skillID != 400051044
                     && !sub_81A420(skillID)
                     && !findProcessType(skillID, 27)
                     && !findProcessType(skillID, 70)
                     && skillID != 3321039
                     && skillID != 3321012
                     && skillID != 400041021
                     && skillID != 400011031
                     && skillID != 400010030
                     && skillID != 400031032) {
                     boolean v39 = isSecondForceAtomAttackSkill(skillID);
                     if (!v39 && skillID != 400051065 && skillID != 400051067 && skillID != 400011086 && skillID != 400011133 && skillID != 400011117) {
                        if (skillID == 11121056 || skillID == 11121055 || skillID == 400011056 || skillID == 400041026) {
                           v39 = true;
                        }

                        if (skillID != 400011019 && !v39) {
                           return true;
                        }
                     }
                  }

                  return false;
            }
      }
   }

   public static boolean isStormArrowSkill_(boolean result, int skillID) {
      if (!result) {
         return skillID != 80001587 && skillID - 80001587 != 42 && skillID - 80001587 != 871 ? findProcessType(skillID, 26) : true;
      } else {
         return true;
      }
   }

   public static boolean isStormArrowSkill(int skillID) {
      if (skillID <= 33121009) {
         if (skillID == 33121009) {
            return true;
         } else {
            boolean v1;
            if (skillID > 22171083) {
               if (skillID > 27111100) {
                  if (skillID > 31101000) {
                     if (skillID == 31111005) {
                        return true;
                     }

                     v1 = skillID == 31211001;
                  } else {
                     if (skillID == 31101000 || skillID == 30021238) {
                        return true;
                     }

                     v1 = skillID == 31001000;
                  }
               } else {
                  if (skillID == 27111100) {
                     return true;
                  }

                  if (skillID > 25111005) {
                     if (skillID == 25121030) {
                        return true;
                     }

                     v1 = skillID == 27101202;
                  } else {
                     if (skillID == 25111005 || skillID == 23121000 || skillID - 23121000 == 1000000) {
                        return true;
                     }

                     v1 = skillID - 23121000 == 1000005;
                  }
               }
            } else {
               if (skillID == 22171083) {
                  return true;
               }

               if (skillID > 12121054) {
                  if (skillID > 13121009) {
                     if (skillID == 20041226) {
                        return true;
                     }

                     v1 = skillID == 21120018;
                  } else {
                     if (skillID == 13121009 || skillID == 13111020) {
                        return true;
                     }

                     v1 = skillID == 13121001;
                  }
               } else {
                  if (skillID == 12121054) {
                     return true;
                  }

                  if (skillID > 3111013) {
                     if (skillID == 3121020) {
                        return true;
                     }

                     v1 = skillID == 5221004;
                  } else {
                     if (skillID == 3111013 || skillID == 1311011 || skillID == 2221052) {
                        return true;
                     }

                     v1 = skillID == 3101008;
                  }
               }
            }

            return isStormArrowSkill_(v1, skillID);
         }
      } else if (skillID <= 80002685) {
         if (skillID == 80002685) {
            return true;
         } else {
            boolean v1;
            if (skillID > 37121003) {
               if (skillID > 65101006) {
                  if (skillID == 65121003) {
                     return true;
                  }

                  v1 = skillID == 80001887;
               } else {
                  if (skillID == 65101006 || skillID == 60011216) {
                     return true;
                  }

                  v1 = skillID == 64121002;
               }
            } else {
               if (skillID == 37121003) {
                  return true;
               }

               if (skillID > 36101001) {
                  if (skillID == 36110005) {
                     return true;
                  }

                  v1 = skillID == 36121000;
               } else {
                  if (skillID == 36101001 || skillID == 33121114 || skillID - 33121114 == 100) {
                     return true;
                  }

                  v1 = skillID - 33121114 == 1999901;
               }
            }

            return isStormArrowSkill_(v1, skillID);
         }
      } else if (skillID > 400011028) {
         boolean v1;
         if (skillID > 400031046) {
            if (skillID == 400041006) {
               return true;
            }

            v1 = skillID == 400041010;
         } else {
            if (skillID == 400031046 || skillID == 400011072) {
               return true;
            }

            v1 = skillID == 400021061;
         }

         return isStormArrowSkill_(v1, skillID);
      } else if (skillID == 400011028) {
         return true;
      } else {
         if (skillID > 131001004) {
            if (skillID >= 131001020) {
               if (skillID <= 131001021) {
                  return true;
               }

               boolean v1 = skillID == 155111306;
               return isStormArrowSkill_(v1, skillID);
            }
         } else if (skillID == 131001004 || skillID == 95001001 || skillID > 101110100 && skillID <= 101110102) {
            return true;
         }

         return isStormArrowSkill_(false, skillID);
      }
   }

   public static boolean isThrowBombSkill(int skillID) {
      return skillID == 14111006 || isRushBombSkill(skillID);
   }

   public static boolean isGrandeSkill(int skillID) {
      boolean v1;
      if (skillID > 61111113) {
         if (skillID > 101120203) {
            if (skillID > 400031004) {
               v1 = skillID == 400031036 || skillID == 400031067;
            } else {
               if (skillID >= 400031003 || skillID == 101120205) {
                  return true;
               }

               v1 = skillID == 400001018;
            }
         } else {
            if (skillID == 101120203) {
               return true;
            }

            if (skillID > 80002247) {
               if (skillID == 80002300) {
                  return true;
               }

               v1 = skillID == 101120200;
            } else {
               if (skillID == 80002247 || skillID == 61111218) {
                  return true;
               }

               v1 = skillID == 64101002;
            }
         }
      } else {
         if (skillID == 61111113) {
            return true;
         }

         if (skillID > 14111022) {
            if (skillID > 27121201) {
               if (skillID == 31201001) {
                  return true;
               }

               v1 = skillID == 61111100;
            } else {
               if (skillID == 27121201 || skillID == 22140015) {
                  return true;
               }

               v1 = skillID - 22140015 == 9;
            }
         } else {
            if (skillID == 14111022) {
               return true;
            }

            if (skillID > 5101014) {
               if (skillID == 5301001) {
                  return true;
               }

               v1 = skillID == 12121001;
            } else {
               if (skillID == 5101014 || skillID == 2221012) {
                  return true;
               }

               v1 = false;
            }
         }
      }

      return v1;
   }

   public static boolean isUnknown379(int skillId) {
      return isUnknown379_3(skillId) || isUnknown379_2(skillId) || skillId == 152141001;
   }

   public static boolean isUnknown379_3(int skillId) {
      boolean v2;
      if (skillId > 61121217) {
         if (skillId == 400011058) {
            return true;
         }

         v2 = skillId == 400011059;
      } else {
         if (skillId == 61121217
            || skillId == 61101002
            || skillId == 61110211
            || skillId == 61120007
            || skillId == 36001005
            || skillId == 36100010
            || skillId == 36110012) {
            return true;
         }

         v2 = skillId == 36120015;
      }

      if (!v2 && skillId != 4100012 && skillId != 4120019 && skillId != 35101002 && skillId != 35110017 && !sub_140A87480(skillId)) {
         boolean v4;
         if (skillId > 155141002) {
            if (skillId == 155141009 || skillId == 155141013) {
               return true;
            }

            v4 = skillId == 155141018;
         } else {
            if (skillId == 155141002 || skillId == 155001000 || skillId == 155101002 || skillId == 155111003) {
               return true;
            }

            v4 = skillId == 155121003;
         }

         if (!v4 && !sub_140ABF6A0(skillId) && !sub_140A66A20(skillId) && skillId != 3011004 && skillId != 3300002 && skillId != 3321003) {
            boolean v5;
            if (skillId > 31221014) {
               if (skillId > 164120007) {
                  if (skillId > 400031054) {
                     if (skillId > 400041068) {
                        if (skillId == 400051017) {
                           return true;
                        }

                        if (skillId == 500061015) {
                           return true;
                        }

                        if (skillId == 500061017) {
                           return true;
                        }

                        v5 = skillId == 500061034;
                     } else {
                        if (skillId == 400041068) {
                           return true;
                        }

                        if (skillId == 400041010) {
                           return true;
                        }

                        if (skillId == 400041023) {
                           return true;
                        }

                        if (skillId == 400041048) {
                           return true;
                        }

                        v5 = skillId == 400041059;
                     }
                  } else {
                     if (skillId == 400031054) {
                        return true;
                     }

                     if (skillId > 400031020) {
                        if (skillId == 400031021) {
                           return true;
                        }

                        if (skillId == 400031022) {
                           return true;
                        }

                        if (skillId == 400031029) {
                           return true;
                        }

                        v5 = skillId == 400031031;
                     } else {
                        if (skillId == 400031020) {
                           return true;
                        }

                        if (skillId == 400011131) {
                           return true;
                        }

                        if (skillId == 400021001) {
                           return true;
                        }

                        if (skillId == 400021001) {
                           return true;
                        }

                        v5 = skillId == 400031000;
                     }
                  }
               } else {
                  if (skillId == 164120007) {
                     return true;
                  }

                  if (skillId > 135002015) {
                     if (skillId > 152141000) {
                        if (skillId == 152141001) {
                           return true;
                        }

                        if (skillId == 152141002) {
                           return true;
                        }

                        if (skillId == 155100009) {
                           return true;
                        }

                        v5 = skillId == 164101004;
                     } else {
                        if (skillId == 152141000) {
                           return true;
                        }

                        if (skillId == 142110011) {
                           return true;
                        }

                        if (skillId == 152001001) {
                           return true;
                        }

                        if (skillId == 152120001) {
                           return true;
                        }

                        v5 = skillId == 152120002;
                     }
                  } else {
                     if (skillId == 135002015
                        || skillId == 65141502
                        || skillId == 31241001
                        || skillId == 36110004
                        || skillId == 65111007
                        || skillId == 65120011
                        || skillId == 80001588
                        || skillId == 80001890
                        || skillId == 80002811) {
                        return true;
                     }

                     v5 = skillId == 131003016;
                  }
               }
            } else {
               if (skillId == 31221014) {
                  return true;
               }

               if (skillId > 12141005) {
                  if (skillId > 14110034) {
                     if (skillId > 24120002) {
                        if (skillId == 24121011 || skillId == 25100010 || skillId == 25120115) {
                           return true;
                        }

                        v5 = skillId == 25141505;
                     } else {
                        if (skillId == 24120002) {
                           return true;
                        }

                        if (skillId == 14110035) {
                           return true;
                        }

                        if (skillId == 14120018) {
                           return true;
                        }

                        if (skillId == 14120020) {
                           return true;
                        }

                        v5 = skillId == 24100003;
                     }
                  } else {
                     if (skillId == 14110034) {
                        return true;
                     }

                     if (skillId > 13120003) {
                        if (skillId == 13120010) {
                           return true;
                        }

                        if (skillId == 13121017) {
                           return true;
                        }

                        if (skillId == 14000028) {
                           return true;
                        }

                        v5 = skillId == 14000029;
                     } else {
                        if (skillId == 13120003) {
                           return true;
                        }

                        if (skillId == 13100027) {
                           return true;
                        }

                        if (skillId == 13101022) {
                           return true;
                        }

                        if (skillId == 13110022) {
                           return true;
                        }

                        v5 = skillId == 13110027;
                     }
                  }
               } else {
                  if (skillId == 12141005) {
                     return true;
                  }

                  if (skillId > 12110030) {
                     if (skillId > 12121057) {
                        if (skillId == 12121059) {
                           return true;
                        }

                        if (skillId == 12141001) {
                           return true;
                        }

                        if (skillId == 12141002) {
                           return true;
                        }

                        v5 = skillId == 12141004;
                     } else {
                        if (skillId == 12121057) {
                           return true;
                        }

                        if (skillId == 12120010) {
                           return true;
                        }

                        if (skillId == 12120017) {
                           return true;
                        }

                        if (skillId == 12120019) {
                           return true;
                        }

                        v5 = skillId == 12120020;
                     }
                  } else {
                     if (skillId == 12110030
                        || skillId == 4210014
                        || skillId == 3100010
                        || skillId == 3120017
                        || skillId == 3300005
                        || skillId == 3301009
                        || skillId == 3321037
                        || skillId == 4220021
                        || skillId == 12000026
                        || skillId == 12100028) {
                        return true;
                     }

                     v5 = skillId == 12110028;
                  }
               }
            }

            if (!v5) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean isUnknown379_2(int skillId) {
      if (skillId > 152141005) {
         return skillId != 162121010 && skillId != 152141006 && skillId != 162101011 && skillId != 162111005 && skillId != 162121019
            ? skillId == 400021122
            : true;
      } else if (skillId == 152141005) {
         return true;
      } else if (skillId > 31241001) {
         return skillId == 152141001 ? true : skillId == 152141002;
      } else {
         return skillId != 31241001 && skillId != 2121052 && skillId != 3341002 ? skillId == 11121018 : true;
      }
   }

   public static boolean sub_140A66A20(int skillId) {
      return skillId >= 80002602 && skillId <= 80002621;
   }

   public static boolean sub_140ABF6A0(int skillId) {
      return skillId == 22141017 || skillId == 22170070 || skillId == 63111010 || skillId == 155111207;
   }

   public static boolean sub_140A87480(int skillId) {
      return skillId == 152141004
         || skillId == 152141005
         || skillId == 152141006
         || skillId == 152110004
         || skillId == 152120016
         || skillId == 155121003
         || skillId == 155141018;
   }

   public static boolean isRushBombSkill(int skillId) {
      if (skillId <= 80002247) {
         return skillId != 80002247
               && skillId != 22140015
               && skillId != 12121001
               && skillId != 2221012
               && skillId != 5301001
               && skillId != 11101029
               && skillId != 14101028
               && skillId != 15101028
               && skillId != 61111113
               && skillId != 22140024
               && skillId != 31201001
               && skillId != 61111100
               && skillId != 61111218
            ? skillId == 64101002
            : true;
      } else if (skillId <= 400001018) {
         if (skillId == 400001018) {
            return true;
         } else {
            boolean v1;
            if (skillId > 101120205) {
               if (skillId == 101141007) {
                  return true;
               }

               v1 = skillId == 101141010;
            } else {
               if (skillId == 101120205) {
                  return true;
               }

               int v2 = skillId - 80002300;
               if (skillId == 80002300) {
                  return true;
               }

               int v3 = v2 - 21117900;
               if (skillId == 21117900) {
                  return true;
               }

               v1 = skillId == 21117903;
            }

            return v1;
         }
      } else {
         boolean v6;
         int v7;
         if (skillId > 400031036) {
            v7 = skillId - 400031067;
            v6 = v7 == 0;
         } else {
            if (skillId == 400031036) {
               return true;
            }

            int v5 = skillId - 400021131;
            if (skillId == 400021131) {
               return true;
            }

            v7 = v5 - 9872;
            v6 = v7 == 0;
         }

         if (!v6) {
            boolean v1 = v7 == 1;
            if (!v1) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isRWBonusAttack(int a1) {
      if (Integer.toUnsignedLong(a1 - 400011092) > 5L) {
         if (a1 > 37120001) {
            switch (a1) {
               case 37120013:
               case 37120014:
               case 37120015:
               case 37120016:
               case 37120017:
               case 37120018:
               case 37120019:
               case 37120022:
               case 37120024:
               case 37120055:
               case 37120056:
               case 37120057:
               case 37120058:
                  return true;
               case 37120020:
               case 37120021:
               case 37120023:
               case 37120025:
               case 37120026:
               case 37120027:
               case 37120028:
               case 37120029:
               case 37120030:
               case 37120031:
               case 37120032:
               case 37120033:
               case 37120034:
               case 37120035:
               case 37120036:
               case 37120037:
               case 37120038:
               case 37120039:
               case 37120040:
               case 37120041:
               case 37120042:
               case 37120043:
               case 37120044:
               case 37120045:
               case 37120046:
               case 37120047:
               case 37120048:
               case 37120049:
               case 37120050:
               case 37120051:
               case 37120052:
               case 37120053:
               case 37120054:
               default:
                  return false;
            }
         }

         if (a1 != 37120001) {
            boolean v1;
            if (a1 > 37110002) {
               if (a1 < 37110010) {
                  return false;
               }

               v1 = a1 == 37110011;
            } else {
               if (a1 == 37110002) {
                  return true;
               }

               if (a1 > 37100009) {
                  return false;
               }

               if (a1 >= 37100008) {
                  return true;
               }

               if (a1 < 37000007) {
                  return false;
               }

               v1 = a1 == 37000009;
            }

            return !v1;
         }
      }

      return true;
   }

   public static boolean isBattleshipBomber(int skillID) {
      return skillID >= 5220023 && (skillID <= 5220025 || skillID == 5221022);
   }

   public static boolean isShootObjSkill(int skillID) {
      if (skillID > 400021028) {
         if (skillID > 400041020) {
            boolean v1;
            if (skillID > 400051008) {
               v1 = skillID == 400051016;
            } else {
               if (skillID == 400051008 || skillID == 400041034) {
                  return true;
               }

               v1 = skillID == 400051003;
            }

            return v1 ? true : findProcessType(skillID, 4);
         } else {
            if (skillID != 400041020) {
               if (skillID > 400031048) {
                  if (skillID < 400041016 || skillID > 400041018) {
                     return findProcessType(skillID, 4);
                  }
               } else if (skillID != 400031048) {
                  switch (skillID) {
                     case 400021047:
                     case 400021048:
                     case 400021064:
                     case 400021065:
                        return true;
                     default:
                        return findProcessType(skillID, 4);
                  }
               }
            }

            return true;
         }
      } else if (skillID == 400021028) {
         return true;
      } else if (skillID > 152120003) {
         if (skillID <= 400021004) {
            if (skillID != 400021004 && skillID != 152121004) {
               boolean v1 = skillID == 400011004;
               return v1 ? true : findProcessType(skillID, 4);
            } else {
               return true;
            }
         } else {
            return skillID >= 400021009 && skillID <= 400021011 ? true : findProcessType(skillID, 4);
         }
      } else if (skillID == 152120003) {
         return true;
      } else {
         boolean v1;
         if (skillID > 80002834) {
            v1 = skillID == 152001002;
         } else {
            if (skillID == 80002834 || skillID == 80002691) {
               return true;
            }

            v1 = skillID == 80002832;
         }

         return v1 ? true : findProcessType(skillID, 4);
      }
   }

   public static int getNoviceSkillFromRace(int skillID) {
      if (skillID == 10001215 || skillID == 50001215) {
         return 1005;
      } else if (isCommonSkill(skillID)) {
         return skillID;
      } else {
         return !isNoviceSkill(skillID) ? 0 : skillID % 10000;
      }
   }

   public static boolean isExceptionKeydownSkill(int skillID) {
      return skillID == 400031046
         || skillID == 400041059
         || skillID == 400041060
         || skillID == 400031065
         || skillID == 400031061
         || skillID == 400051070
         || skillID == 400051041;
   }

   public static boolean isKeydownSkill(int skillID) {
      boolean v1 = false;
      if (skillID > 35101002) {
         if (skillID > 80003302) {
            if (skillID > 142111010) {
               if (skillID > 400031046) {
                  if (skillID == 400041006 || skillID == 400041009) {
                     return true;
                  }

                  v1 = skillID == 400051024;
               } else {
                  if (skillID == 400031046 || skillID == 400011028 || skillID == 400011072 || skillID == 400011091) {
                     return true;
                  }

                  v1 = skillID == 400021061;
               }
            } else {
               if (skillID == 142111010) {
                  return true;
               }

               if (skillID > 131001004) {
                  if (skillID == 131001008 || skillID == 131001020) {
                     return true;
                  }

                  v1 = skillID == 131001021;
               } else {
                  if (skillID == 131001004 || skillID == 80003370 || skillID == 95001001 || skillID == 101110101) {
                     return true;
                  }

                  v1 = skillID == 101110102;
               }
            }
         } else {
            if (skillID == 80003302) {
               return true;
            }

            if (skillID > 80001836) {
               if (skillID > 80003075) {
                  if (skillID == 80003076 || skillID == 80003077) {
                     return true;
                  }

                  v1 = skillID == 80003087;
               } else {
                  if (skillID == 80003075 || skillID == 80001887 || skillID == 80002685 || skillID == 80002780) {
                     return true;
                  }

                  v1 = skillID == 80002785;
               }
            } else {
               if (skillID == 80001836) {
                  return true;
               }

               if (skillID > 60011216) {
                  if (skillID == 64001000 || skillID == 64001007 || skillID == 64001008) {
                     return true;
                  }

                  v1 = skillID == 64121002;
               } else {
                  if (skillID == 60011216 || skillID == 35110017 || skillID == 36101001 || skillID == 36121000) {
                     return true;
                  }

                  v1 = skillID == 37121052;
               }
            }
         }
      } else {
         if (skillID == 35101002) {
            return true;
         }

         if (skillID > 23141000) {
            if (skillID > 31101000) {
               if (skillID > 33121214) {
                  if (skillID == 33141000 || skillID == 33141001) {
                     return true;
                  }

                  v1 = skillID == 33141002;
               } else {
                  if (skillID == 33121214 || skillID == 31111005 || skillID == 31211001 || skillID == 33121009) {
                     return true;
                  }

                  v1 = skillID == 33121114;
               }
            } else {
               if (skillID == 31101000) {
                  return true;
               }

               if (skillID > 25121030) {
                  if (skillID == 27101202 || skillID == 27111100 || skillID == 30021238) {
                     return true;
                  }

                  v1 = skillID == 31001000;
               } else {
                  if (skillID == 25121030 || skillID == 24121000 || skillID == 24121005 || skillID == 24141000) {
                     return true;
                  }

                  v1 = skillID == 25111005;
               }
            }
         } else {
            if (skillID == 23141000) {
               return true;
            }

            if (skillID > 13121001) {
               if (skillID == 21120018
                  || skillID == 13141000
                  || skillID == 14111006
                  || skillID == 14121004
                  || skillID == 20041226
                  || skillID == 21121029
                  || skillID == 22171083) {
                  return true;
               }

               v1 = skillID == 23121000;
            } else {
               if (skillID == 13121001) {
                  return true;
               }

               if (skillID > 3121020) {
                  if (skillID == 3141000 || skillID == 3141001 || skillID == 3201012) {
                     return true;
                  }

                  v1 = skillID == 4341002;
               } else {
                  if (skillID == 3121020 || skillID == 2221011 || skillID == 2221052 || skillID == 3101008) {
                     return true;
                  }

                  v1 = skillID == 3111013;
               }
            }
         }
      }

      return v1
         || skillID == 5221004
         || skillID == 5241000
         || skillID == 5241001
         || skillID == 80001389
         || skillID == 80001390
         || skillID == 80001391
         || skillID == 80001392
         || skillID == 80001587
         || skillID == 80001629
         || skillID == 80002458
         || findProcessType(skillID, 11);
   }

   public static boolean sub_7A6200(int a1) {
      return a1 == 80001587 || a1 - 80001587 == 42 || a1 - 80001587 == 871;
   }

   public static boolean isNoConsumeMPJob(int job) {
      return isDemon(job) || isAngelicBuster(job) || isZero(job);
   }

   public static boolean isYetiPinkBean(int job) {
      switch (job) {
         case 13100:
         case 13500:
            return true;
         default:
            return false;
      }
   }

   public static boolean isAutoMaxSkill() {
      return true;
   }

   public static boolean isToadsHammerAvailableItem(Equip equip) {
      if ((equip.getSpecialAttribute() & EquipSpecialAttribute.VESTIGE.getType()) != 0) {
         return false;
      } else if (equip.getUniqueId() > 0L) {
         return false;
      } else if (equip.isAmazingHyperUpgradeUsed()) {
         return false;
      } else {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         return !ii.isCash(equip.getItemId());
      }
   }

   public static List<Integer> getDowngraded(int itemID, int optionID, int target) {
      ItemOption option = null;
      if ((option = ItemOptionInfo.options.get(optionID)) == null) {
         return null;
      } else {
         int curGrade = optionID / 10000;
         int targetGrade = target;
         if (target == -1) {
            targetGrade = Math.max(0, curGrade - 1);
         }

         Map<Integer, List<ItemOption>> options = ItemOptionInfo.optionsSorted.get(targetGrade);
         List<Integer> possibleList = new ArrayList<>();
         List<ItemOption> downList;
         if ((downList = options.get(option.optionType)) != null) {
            for (ItemOption downgraded : downList) {
               if (downgraded.uniqueOption == option.uniqueOption && isAddiMatched(optionID, downgraded.id)) {
                  possibleList.add(downgraded.id);
               }
            }
         }

         if (possibleList.isEmpty()) {
            for (int optionType : ItemOptionInfo.getOptionTypes(itemID)) {
               List<ItemOption> downgradedList;
               if ((downgradedList = options.get(optionType)) != null) {
                  for (ItemOption downgradedOption : downgradedList) {
                     if (isAddiMatched(optionID, downgradedOption.id)) {
                        possibleList.add(downgradedOption.id);
                     }
                  }
               }
            }
         }

         return possibleList;
      }
   }

   private static boolean isAddi(int optionID) {
      return optionID / 1000 % 10 == 2;
   }

   private static boolean isAddiMatched(int optionID1, int optionID2) {
      return isAddi(optionID1) == isAddi(optionID2);
   }

   public static int[][] extreamPotion() {
      return new int[][]{
         {5, 5, 5, 5, 5, 5, 10, 20, 20, 20},
         {5, 5, 5, 5, 5, 10, 10, 20, 20, 15},
         {5, 5, 5, 5, 5, 10, 20, 15, 15, 15},
         {5, 5, 5, 5, 5, 20, 10, 15, 15, 15},
         {5, 5, 5, 10, 10, 10, 10, 15, 15, 15},
         {5, 5, 5, 10, 10, 10, 15, 15, 15, 10},
         {5, 5, 5, 10, 10, 15, 15, 15, 10, 10},
         {5, 5, 5, 10, 15, 15, 15, 10, 10, 10},
         {5, 5, 10, 10, 15, 10, 15, 10, 10, 10},
         {5, 5, 10, 15, 10, 15, 10, 10, 10, 10},
         {5, 5, 10, 10, 15, 20, 10, 10, 10, 5},
         {5, 5, 10, 10, 20, 15, 15, 10, 5, 5},
         {5, 5, 10, 15, 15, 20, 10, 10, 5, 5},
         {5, 5, 10, 20, 20, 10, 10, 10, 5, 5},
         {5, 10, 10, 20, 15, 10, 10, 10, 5, 5},
         {10, 10, 10, 15, 15, 10, 10, 10, 5, 5},
         {10, 10, 15, 15, 10, 10, 10, 10, 5, 5},
         {10, 15, 15, 10, 10, 10, 10, 10, 5, 5},
         {15, 20, 5, 10, 10, 10, 10, 10, 5, 5},
         {15, 10, 15, 15, 10, 10, 10, 5, 5, 5},
         {15, 15, 15, 10, 10, 10, 10, 5, 5, 5},
         {20, 15, 10, 10, 10, 10, 10, 5, 5, 5},
         {15, 20, 15, 10, 10, 10, 5, 5, 5, 5},
         {20, 20, 10, 10, 10, 10, 5, 5, 5, 5},
         {20, 20, 15, 10, 10, 5, 5, 5, 5, 5},
         {20, 15, 15, 15, 10, 10, 5, 5, 5, 0},
         {20, 20, 15, 10, 10, 10, 5, 5, 5, 0},
         {20, 25, 10, 10, 10, 10, 5, 5, 5, 0},
         {25, 20, 10, 10, 10, 10, 5, 5, 5, 0},
         {25, 20, 15, 10, 10, 5, 5, 5, 5, 0},
         {25, 20, 10, 15, 10, 10, 5, 5, 0, 0},
         {25, 20, 15, 15, 10, 5, 5, 5, 0, 0},
         {25, 25, 15, 10, 10, 5, 5, 5, 0, 0},
         {25, 30, 10, 10, 10, 5, 5, 5, 0, 0},
         {30, 20, 20, 10, 5, 5, 5, 5, 0, 0},
         {25, 20, 25, 10, 10, 5, 5, 0, 0, 0},
         {30, 20, 20, 10, 10, 5, 5, 0, 0, 0},
         {30, 25, 15, 10, 10, 5, 5, 0, 0, 0},
         {30, 25, 20, 10, 5, 5, 5, 0, 0, 0},
         {35, 25, 20, 5, 5, 5, 5, 0, 0, 0},
         {35, 30, 15, 10, 5, 5, 0, 0, 0, 0},
         {35, 35, 15, 5, 5, 5, 0, 0, 0, 0},
         {40, 35, 10, 5, 5, 5, 0, 0, 0, 0},
         {50, 25, 10, 5, 5, 5, 0, 0, 0, 0},
         {55, 25, 5, 5, 5, 5, 0, 0, 0, 0},
         {50, 30, 10, 5, 5, 0, 0, 0, 0, 0},
         {50, 35, 5, 5, 5, 0, 0, 0, 0, 0},
         {60, 25, 5, 5, 5, 0, 0, 0, 0, 0},
         {60, 25, 10, 5, 0, 0, 0, 0, 0, 0},
         {55, 35, 10, 0, 0, 0, 0, 0, 0, 0},
         {60, 35, 5, 0, 0, 0, 0, 0, 0, 0},
         {65, 30, 5, 0, 0, 0, 0, 0, 0, 0},
         {65, 35, 0, 0, 0, 0, 0, 0, 0, 0},
         {75, 25, 0, 0, 0, 0, 0, 0, 0, 0},
         {80, 20, 0, 0, 0, 0, 0, 0, 0, 0},
         {85, 15, 0, 0, 0, 0, 0, 0, 0, 0},
         {90, 10, 0, 0, 0, 0, 0, 0, 0, 0},
         {95, 5, 0, 0, 0, 0, 0, 0, 0, 0},
         {100, 0, 0, 0, 0, 0, 0, 0, 0, 0}
      };
   }

   public static int getZeroInheritanceNeedLevel(int newLevel) {
      switch (newLevel) {
         case 1:
            return 100;
         case 2:
            return 110;
         case 3:
            return 120;
         case 4:
            return 130;
         case 5:
            return 140;
         case 6:
            return 150;
         case 7:
            return 170;
         case 8:
            return 180;
         case 9:
         case 10:
            return 200;
         default:
            return 999;
      }
   }

   public static EgoEquipUpgradeCost getZeroEgoEquipUpgradeCost(int index) {
      int meso = 0;
      int wp = 0;
      short var4;
      if (index == 0) {
         meso = 50000;
         var4 = 500;
      } else {
         if (index != 1) {
            return null;
         }

         meso = 100000;
         var4 = 600;
      }

      return new EgoEquipUpgradeCost(meso, var4);
   }

   public static int LinkedZeroSkil(int skillID) {
      switch (skillID) {
         case 101000100:
         case 101000101:
            return 101001100;
         case 101000200:
         case 101000201:
         case 101000202:
            return 101001200;
         case 101100201:
         case 101100202:
            return 101101200;
         case 101110101:
         case 101110102:
         case 101110104:
            return 101111100;
         case 101110201:
         case 101110202:
         case 101110203:
         case 101110204:
            return 101110200;
         case 101120100:
         case 101120101:
         case 101120102:
         case 101120103:
         case 101120104:
         case 101120105:
         case 101120106:
            return 101121100;
         case 101120201:
         case 101120202:
         case 101120203:
         case 101120204:
         case 101120205:
         case 101120206:
            return 101121200;
         default:
            return skillID;
      }
   }

   public static boolean isTheSeedRing(int itemId) {
      return itemId >= 1113098 && itemId <= 1113128 ? true : isContinousRing(itemId);
   }

   public static boolean isContinousRing(int itemId) {
      return itemId == 1113329;
   }

   public static boolean isTheSeedSkill(int skillID) {
      if ((skillID < 80001455 || skillID > 80001479) && (skillID < 80000299 || skillID > 80000304)) {
         return skillID == 80003034 ? true : skillID >= 80003341 && skillID <= 80003342;
      } else {
         return true;
      }
   }

   public static int getTheSeedRingSkill(int itemId) {
      switch (itemId) {
         case 1113098:
            return 80001455;
         case 1113099:
            return 80001456;
         case 1113100:
            return 80001457;
         case 1113101:
            return 80001458;
         case 1113102:
            return 80001459;
         case 1113103:
            return 80001460;
         case 1113104:
            return 80001461;
         case 1113105:
            return 80001462;
         case 1113106:
            return 80001463;
         case 1113107:
            return 80001464;
         case 1113108:
            return 80001465;
         case 1113109:
            return 80001466;
         case 1113110:
            return 80001467;
         case 1113111:
            return 80001468;
         case 1113112:
            return 80001469;
         case 1113113:
            return 80001470;
         case 1113114:
            return 80001471;
         case 1113115:
            return 80001472;
         case 1113116:
            return 80001473;
         case 1113117:
            return 80001474;
         case 1113118:
            return 80001475;
         case 1113119:
            return 80001476;
         case 1113120:
            return 80001477;
         case 1113121:
            return 80001478;
         case 1113122:
            return 80001479;
         case 1113123:
            return 80000299;
         case 1113124:
            return 80000300;
         case 1113125:
            return 80000301;
         case 1113126:
            return 80000302;
         case 1113127:
            return 80000303;
         case 1113128:
            return 80000304;
         case 1113329:
            return 80003342;
         default:
            return 0;
      }
   }

   public static int getTheSeedRingBySkill(int skillId) {
      switch (skillId) {
         case 80000299:
            return 1113123;
         case 80000300:
            return 1113124;
         case 80000301:
            return 1113125;
         case 80000302:
            return 1113126;
         case 80000303:
            return 1113127;
         case 80000304:
            return 1113128;
         case 80001455:
            return 1113098;
         case 80001456:
            return 1113099;
         case 80001457:
            return 1113100;
         case 80001458:
            return 1113101;
         case 80001459:
            return 1113102;
         case 80001460:
            return 1113103;
         case 80001461:
            return 1113104;
         case 80001462:
            return 1113105;
         case 80001463:
            return 1113106;
         case 80001464:
            return 1113107;
         case 80001465:
            return 1113108;
         case 80001466:
            return 1113109;
         case 80001467:
            return 1113110;
         case 80001468:
            return 1113111;
         case 80001469:
            return 1113112;
         case 80001470:
            return 1113113;
         case 80001471:
            return 1113114;
         case 80001472:
            return 1113115;
         case 80001473:
            return 1113116;
         case 80001474:
            return 1113117;
         case 80001475:
            return 1113118;
         case 80001476:
            return 1113119;
         case 80001477:
            return 1113120;
         case 80001478:
            return 1113121;
         case 80001479:
            return 1113122;
         default:
            return 0;
      }
   }

   public static int getNeededBagTypeByInputItemID(int a1, int a2) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      int result;
      if (a2 == 2) {
         ItemInformation v4 = ii.getItemInformation(a1);
         int v5 = a1 / 10000;
         if (a1 / 10000 == 251) {
            result = 1;
         } else if (Math.abs(a1 - 2591000) >= 1000) {
            if (!is_scroll_item(a1) || v5 != 204 && v5 != 264) {
               if (v4 != null) {
                  result = ii.getBagType(a1);
               } else {
                  result = 0;
               }
            } else {
               result = 3;
            }
         } else {
            result = 2;
         }
      } else {
         if (a2 != 3) {
            if (a2 == 4) {
               ItemInformation v2 = ii.getItemInformation(a1);
               if (Math.abs(a1 - 4310000) < 10000 || a1 == 4001254 || a1 == 4001620 || a1 == 4001623) {
                  return 5;
               }

               if (Math.abs(a1 - 4350000) < 10000) {
                  return 6;
               }

               if (v2 != null) {
                  return ii.getBagType(a1);
               }
            }

            return 0;
         }

         if (Math.abs(a1 - 3700000) < 10000) {
            return 1;
         }

         if (Math.abs(a1 - 3010000) >= 10000 && get_etc_cash_item_type(a1) != 71) {
            return 0;
         }

         result = 2;
      }

      return result;
   }

   public static boolean is_scroll_item(int a1) {
      return Math.abs(a1 - 2040000) < 10000 || Math.abs(a1 - 2640000) < 10000;
   }

   public static int get_etc_cash_item_type(int a1) {
      int result = get_cashslot_item_type(a1);
      switch (result) {
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 34:
         case 35:
         case 40:
         case 67:
         case 69:
         case 71:
         case 98:
            return result;
         default:
            return 0;
      }
   }

   public static int get_cashslot_item_type(int a1) {
      int result;
      switch (a1 / 10000) {
         case 500:
            return 8;
         case 501:
            return 9;
         case 502:
            return 10;
         case 503:
            return 11;
         case 504:
            return Math.abs(a1 % 10000 - 4000) < 1000 ? 61 : 22;
         case 505:
            if (a1 % 5050000 == 100) {
               return 66;
            }

            if (Math.abs(a1 % 5050000 - 1000) > 1) {
               return (a1 % 10 != 0 ? 1 : 0) + 23;
            }

            return 47;
         case 506:
            switch (a1 / 1000) {
               case 5060:
                  if (a1 % 10 != 0) {
                     result = 26;
                  } else if (a1 / 10 % 10 == 1) {
                     result = 92;
                  } else {
                     result = 25;
                  }
                  break;
               case 5061:
               case 5066:
               case 5067:
               default:
                  result = 0;
                  break;
               case 5062:
                  int v2 = a1 % 1000;
                  if (a1 % 1000 > 301) {
                     if (v2 > 501) {
                        if (v2 == 503) {
                           result = 91;
                        } else if (Math.abs(v2 - 800) > 1) {
                           result = 44;
                        } else {
                           result = 83;
                        }
                     } else if (v2 >= 500) {
                        result = 79;
                     } else {
                        switch (v2) {
                           case 400:
                           case 403:
                           case 405:
                              return 72;
                           case 401:
                              return 73;
                           case 402:
                              return 74;
                           case 404:
                           default:
                              result = 44;
                        }
                     }
                  } else if (v2 == 301) {
                     result = 75;
                  } else {
                     switch (v2) {
                        case 9:
                           return 86;
                        case 10:
                           return 87;
                        case 90:
                           return 84;
                        case 100:
                        case 103:
                           return 45;
                        case 200:
                           return 63;
                        case 201:
                           return 64;
                        case 202:
                           return 65;
                        default:
                           result = 44;
                     }
                  }
                  break;
               case 5063:
                  if (a1 % 1000 / 100 == 1) {
                     result = 62;
                  } else {
                     result = 49;
                  }
                  break;
               case 5064:
                  switch (a1 % 1000 / 100) {
                     case 1:
                        return 55;
                     case 2:
                        return 58;
                     case 3:
                        return 59;
                     case 4:
                        return 80;
                     default:
                        return 48;
                  }
               case 5065:
                  result = 70;
                  if (a1 % 5065000 != 100) {
                     result = 51;
                  }
                  break;
               case 5068:
                  int v3 = a1 % 1000;
                  if (a1 % 1000 / 100 == 1) {
                     result = 56;
                  } else if (v3 / 100 == 2) {
                     result = 60;
                  } else if (v3 / 100 == 3) {
                     result = 93;
                  } else {
                     result = 50;
                  }
                  break;
               case 5069:
                  result = 88;
                  if (a1 == 5069100) {
                     result = 94;
                  }
            }

            return result;
         case 507:
            switch (a1 % 1000 / 100) {
               case 1:
                  result = 12;
                  break;
               case 2:
                  result = 13;
                  break;
               case 3:
               case 4:
               case 5:
               default:
                  result = 0;
                  break;
               case 6:
                  result = 14;
                  break;
               case 7:
                  result = 38;
                  break;
               case 8:
                  result = 15;
            }

            return result;
         case 508:
            return 18;
         case 509:
            return 21;
         case 510:
            return 20;
         case 511:
         case 521:
         case 522:
         case 526:
         case 527:
         case 529:
         case 530:
         case 531:
         case 532:
         case 534:
         case 535:
         case 536:
         case 538:
         case 540:
         case 541:
         case 542:
         case 543:
         case 544:
         case 546:
         case 548:
         case 549:
         case 554:
         case 555:
         case 556:
         case 557:
         case 558:
         case 559:
         case 560:
         case 561:
         case 563:
         case 564:
         case 565:
         case 566:
         case 567:
         case 569:
         case 571:
         case 572:
         case 573:
         case 574:
         case 575:
         case 576:
         case 577:
         case 579:
         default:
            result = 0;
            break;
         case 512:
            return 16;
         case 513:
            int v4 = a1 % 5130000;
            if (a1 % 5130000 < 3000) {
               byte var10 = 7;
            }

            if (v4 <= 3001) {
               result = 67;
            } else if (v4 == 4000) {
               result = 77;
            } else {
               result = 7;
            }
            break;
         case 514:
            return 4;
         case 515:
            switch (a1 % 1000 / 100) {
               case 5150:
               case 5154:
                  result = 1;
                  break;
               case 5151:
                  result = 98;
                  break;
               case 5152:
                  switch (a1 % 1000 / 100) {
                     case 51520:
                     case 51521:
                        return 2;
                     case 51522:
                        return 31;
                     case 51523:
                        return 96;
                     default:
                        return 0;
                  }
               case 5153:
                  result = 3;
                  break;
               case 5155:
                  result = 57;
                  break;
               case 5156:
               default:
                  result = 0;
                  break;
               case 5157:
                  result = 89;
                  break;
               case 5158:
                  result = 90;
            }

            return result;
         case 516:
            return 6;
         case 517:
            return 10000 * (a1 / 10000) != a1 ? 0 : 17;
         case 518:
            byte var8 = 5;
         case 519:
            return 27;
         case 520:
            return Math.abs(a1 % 5200000 - 4000) < 1000 ? 71 : 19;
         case 523:
            return 29 - (a1 % 5230000 != 3 ? 1 : 0);
         case 524:
            return 30;
         case 525:
            if (a1 % 5250000 == 500) {
               result = 69;
            } else {
               result = 35 - (a1 % 5251000 != 100 ? 1 : 0);
            }

            return result;
         case 528:
            return Math.abs(a1 - 5281000) < 1000 ? 82 : 0;
         case 533:
            return 32;
         case 537:
            return 33;
         case 539:
            return 85;
         case 545:
            return 36;
         case 547:
            return 37;
         case 550:
            if (Math.abs(a1 - 5501000) >= 1000) {
               result = Math.abs(a1 - 5502000) < 1000 ? 68 : 39;
            } else {
               result = 52;
            }

            return result;
         case 551:
            return 40;
         case 552:
            result = 41;
            if (a1 % 10000 == 1000) {
               result = 54;
            }

            return result;
         case 553:
            return 42;
         case 562:
            return 43;
         case 568:
            if (Math.abs(a1 - 5689000) < 100) {
               result = 5;
            } else if (is_script_run_pet_life_item(a1)) {
               result = 95;
            } else if (a1 == 5680222) {
               result = 97;
            } else {
               result = 0;
            }
            break;
         case 570:
            return 53;
         case 578:
            return Math.abs(a1 - 5781000) < 1000 ? 78 : 76;
         case 580:
            return 81;
      }

      return result;
   }

   public static boolean is_script_run_pet_life_item(int nItemID) {
      return nItemID / 100 == 56890;
   }

   public static boolean isForbiddenListAuctionItem(int nItemID) {
      switch (nItemID) {
         case 2632860:
            return true;
         default:
            return false;
      }
   }

   public static boolean isArcaneWeaponArmor(int nItemID) {
      switch (nItemID) {
         case 1004808:
         case 1004809:
         case 1004810:
         case 1004811:
         case 1004812:
         case 1053063:
         case 1053064:
         case 1053065:
         case 1053066:
         case 1053067:
         case 1073158:
         case 1073159:
         case 1073160:
         case 1073161:
         case 1073162:
         case 1082695:
         case 1082696:
         case 1082697:
         case 1082698:
         case 1082699:
         case 1102940:
         case 1102941:
         case 1102942:
         case 1102943:
         case 1102944:
         case 1152196:
         case 1152197:
         case 1152198:
         case 1152199:
         case 1152200:
         case 1212120:
         case 1213018:
         case 1214018:
         case 1222113:
         case 1232113:
         case 1242121:
         case 1242144:
         case 1262039:
         case 1272017:
         case 1282017:
         case 1292018:
         case 1302343:
         case 1312203:
         case 1322255:
         case 1332279:
         case 1342104:
         case 1362140:
         case 1372228:
         case 1382265:
         case 1402259:
         case 1412181:
         case 1422189:
         case 1432218:
         case 1442274:
         case 1452257:
         case 1462243:
         case 1472265:
         case 1482221:
         case 1492235:
         case 1522143:
         case 1532150:
         case 1582023:
         case 1592020:
         case 4310217:
         case 4310300:
            return true;
         default:
            return false;
      }
   }

   public static boolean isInnocentScroll(int nItemID) {
      switch (nItemID) {
         case 2049600:
         case 2049601:
         case 2049602:
         case 2049603:
         case 2049604:
         case 2049605:
         case 2049606:
         case 2049607:
         case 2049610:
         case 2049611:
         case 2049612:
         case 2049615:
         case 2049616:
         case 2049618:
         case 2049623:
         case 2049624:
            return true;
         case 2049608:
         case 2049609:
         case 2049613:
         case 2049614:
         case 2049617:
         case 2049619:
         case 2049620:
         case 2049621:
         case 2049622:
         default:
            return false;
      }
   }

   public static boolean isRoyalSpecialScroll(int itemId) {
      switch (itemId) {
         case 2046076:
         case 2046077:
         case 2046150:
         case 2046251:
         case 2046340:
         case 2046341:
         case 2046831:
         case 2046832:
         case 2046970:
         case 2046981:
         case 2047810:
         case 2048047:
         case 2048048:
         case 2048049:
         case 2048050:
            return true;
         default:
            return false;
      }
   }

   public static boolean isWarrior(int job) {
      return isDefaultWarrior(job)
         || isAdventurerHero(job)
         || isPaladin(job)
         || isDarkKnight(job)
         || isSoulMaster(job)
         || isAran(job)
         || isBlaster((short)job)
         || isDemonSlayer(job)
         || isDemonAvenger(job)
         || isMihile(job)
         || isKaiser(job)
         || isZero(job)
         || isAdele(job);
   }

   public static boolean isMagician(int job) {
      return isDefaultMagician(job)
         || isFPMage(job)
         || isILMage(job)
         || isBishop(job)
         || isFlameWizard(job)
         || isEvan(job)
         || isLuminous(job)
         || isBattleMage(job)
         || isKinesis(job)
         || isIllium(job)
         || isLara(job);
   }

   public static boolean isArcher(int job) {
      return isDefaultArcher(job)
         || isKain(job)
         || isPathFinder(job)
         || isBowMaster(job)
         || isMarksMan(job)
         || isWindBreaker(job)
         || isMercedes(job)
         || isWildHunter(job);
   }

   public static boolean isThief(int job) {
      return isDefaultThief(job)
         || isHoyoung(job)
         || isNightLord(job)
         || isShadower(job)
         || isDualBlade(job)
         || isNightWalker(job)
         || isPhantom(job)
         || isKadena(job)
         || isXenon(job)
         || isKhali(job);
   }

   public static boolean isPirate(int job) {
      return isDefaultPirate(job)
         || isViper(job)
         || isCaptain(job)
         || isCannon(job)
         || isStriker(job)
         || isEunWol(job)
         || isMechanic(job)
         || isAngelicBuster(job)
         || isArk(job)
         || isXenon(job);
   }

   public static boolean isBattleMage(int job) {
      return job >= 3200 && job <= 3212;
   }

   public static boolean isSoulMaster(int job) {
      return job >= 1100 && job <= 1112;
   }

   public static boolean isWindBreaker(int job) {
      return job >= 1300 && job <= 1312;
   }

   public static boolean isPinkBean(int job) {
      return job == 13000 || job == 13100;
   }

   public static boolean isDefaultWarrior(int job) {
      return job == 100;
   }

   public static boolean isAdventurerHero(int job) {
      return job >= 110 && job <= 112;
   }

   public static boolean isPaladin(int job) {
      return job >= 120 && job <= 122;
   }

   public static boolean isDarkKnight(int job) {
      return job >= 130 && job <= 132;
   }

   public static boolean isDefaultMagician(int job) {
      return job == 200;
   }

   public static boolean isFPMage(int job) {
      return job >= 210 && job <= 212;
   }

   public static boolean isILMage(int job) {
      return job >= 220 && job <= 222;
   }

   public static boolean isBishop(int job) {
      return job >= 230 && job <= 232;
   }

   public static boolean isDefaultArcher(int job) {
      return job == 300;
   }

   public static boolean isBowMaster(int job) {
      return job >= 310 && job <= 312;
   }

   public static boolean isMarksMan(int job) {
      return job >= 320 && job <= 322;
   }

   public static boolean isDefaultThief(int job) {
      return job == 400;
   }

   public static boolean isNightLord(int job) {
      return job >= 410 && job <= 412;
   }

   public static boolean isShadower(int job) {
      return job >= 420 && job <= 422;
   }

   public static boolean isDefaultPirate(int job) {
      return job == 500 || job == 501;
   }

   public static boolean isViper(int job) {
      return job >= 510 && job <= 512;
   }

   public static boolean isCaptain(int job) {
      return job >= 520 && job <= 522;
   }

   public static Pair<Integer, String> getRidingData(int itemId) {
      String name = "";
      int ret = 0;
      switch (itemId) {
         case 263076:
            name = "달려라! 은빛갈기! 라이딩";
            ret = 2748;
            break;
         case 2430039:
            name = "근두운 라이딩";
            ret = 2824;
            break;
         case 2430053:
            name = "크로코 라이딩";
            ret = 1004;
            break;
         case 2430057:
            name = "경주용 카트 라이딩";
            ret = 1009;
            break;
         case 2430091:
            name = "나이트메어 라이딩";
            ret = 1014;
            break;
         case 2430149:
            name = "레오나르도 라이딩";
            ret = 1020;
            break;
         case 2430190:
            name = "블루 스쿠터 라이딩";
            ret = 1021;
            break;
         case 2430506:
            name = "버팔로 라이딩";
            ret = 1082;
            break;
         case 2430521:
            name = "꼬마토끼 라이딩";
            ret = 1044;
            break;
         case 2430610:
            name = "산타 썰매 라이딩";
            ret = 1022;
            break;
         case 2430633:
            name = "예티 라이딩";
            ret = 1001;
            break;
         case 2430634:
            name = "핑크 스쿠터 라이딩";
            ret = 1006;
            break;
         case 2430794:
            name = "스페이스쉽 라이딩";
            ret = 1163;
            break;
         case 2430906:
            name = "더블 토끼수레 라이딩";
            ret = 1173;
            break;
         case 2430907:
            name = "더블 주황버섯 라이딩";
            ret = 1174;
            break;
         case 2430908:
            name = "더블 슈퍼래빗 라이딩";
            ret = 1175;
            break;
         case 2430932:
            name = "더블 트랜스폼 라이딩";
            ret = 1183;
            break;
         case 2430933:
            name = "더블 나이트메어 라이딩";
            ret = 1184;
            break;
         case 2430934:
            name = "더블 신수 라이딩";
            ret = 1185;
            break;
         case 2430935:
            name = "더블 부엉이 라이딩";
            ret = 1186;
            break;
         case 2430936:
            name = "더블 곰 열기구 라이딩";
            ret = 1187;
            break;
         case 2430937:
            name = "더블 막시무스 라이딩";
            ret = 1144;
            break;
         case 2430938:
            name = "더블 빨간트럭 라이딩";
            ret = 1148;
            break;
         case 2430939:
            name = "더블 파워슈트 라이딩";
            ret = 1149;
            break;
         case 2431073:
            name = "더블 개구리 라이딩";
            ret = 1452;
            break;
         case 2431135:
            name = "너와 함께 팬텀 라이딩";
            ret = 1220;
            break;
         case 2431136:
            name = "너와 함께 아리아 라이딩";
            ret = 1221;
            break;
         case 2431137:
            name = "드래고니카 라이딩";
            ret = 1198;
            break;
         case 2431267:
            name = "오닉스 재규어 라이딩";
            ret = 1228;
            break;
         case 2431415:
            name = "버터플라이 스윙 라이딩";
            ret = 1872;
            break;
         case 2431424:
            name = "눈꽃송이 라이딩";
            ret = 1244;
            break;
         case 2431473:
            name = "핑크빈과 둥둥 여행 라이딩";
            ret = 1257;
            break;
         case 2431474:
            name = "블랙빈과 둥둥 라이딩";
            ret = 1258;
            break;
         case 2431494:
            name = "타조 라이딩";
            ret = 1051;
            break;
         case 2431495:
            name = "예티 라이더 라이딩";
            ret = 1001;
            break;
         case 2431498:
            name = "근두운 라이딩";
            ret = 1007;
            break;
         case 2431500:
            name = "오토바이 라이딩";
            ret = 1450;
            break;
         case 2431501:
            name = "돌격! 목마 라이딩";
            ret = 1003;
            break;
         case 2431503:
            name = "닭 라이딩";
            ret = 1030;
            break;
         case 2431504:
            name = "부엉이 라이딩";
            ret = 1031;
            break;
         case 2431505:
            name = "로우 라이더 라이딩";
            ret = 1032;
            break;
         case 2431528:
            name = "눈꽃송이 라이딩";
            ret = 1244;
            break;
         case 2431529:
            name = "비구름 라이딩";
            ret = 1245;
            break;
         case 2431541:
            name = "하늘 자전거 라이딩";
            ret = 1243;
            break;
         case 2431745:
            name = "카푸의 라이딩";
            ret = 1278;
            break;
         case 2431757:
            name = "둥실둥실 풍선 라이딩";
            ret = 1285;
            break;
         case 2431760:
            name = "플라잉 부엉이 라이딩";
            ret = 1291;
            break;
         case 2431764:
            name = "데비존의 포로 라이딩";
            ret = 1289;
            break;
         case 2431765:
            name = "흔들린 목마 라이딩";
            ret = 1290;
            break;
         case 2431797:
            name = "페가수스 라이딩";
            ret = 1039;
            break;
         case 2431799:
            name = "드래곤 라이딩";
            ret = 1302;
            break;
         case 2431898:
            name = "바운싱 카 라이딩";
            ret = 1324;
            break;
         case 2431915:
            name = "펠리컨 라이딩";
            ret = 1327;
            break;
         case 2431949:
            name = "거대수탉 라이딩";
            ret = 1336;
            break;
         case 2432003:
            name = "전투 비행정 라이딩";
            ret = 1344;
            break;
         case 2432007:
            name = "헤카톤의 주먹 라이딩";
            ret = 1345;
            break;
         case 2432015:
            name = "붉은 모래구름 라이딩";
            ret = 1333;
            break;
         case 2432029:
            name = "구형 배틀쉽 라이딩";
            ret = 1346;
            break;
         case 2432030:
            name = "가고일 라이딩";
            ret = 1347;
            break;
         case 2432031:
            name = "친구 라이딩";
            ret = 1348;
            break;
         case 2432078:
            name = "지옥개 라이딩";
            ret = 1353;
            break;
         case 2432085:
            name = "돌고래 라이딩";
            ret = 1355;
            break;
         case 2432149:
            name = "낭만가을 라이딩";
            ret = 1398;
            break;
         case 2432151:
            name = "비행침대 라이딩";
            ret = 1400;
            break;
         case 2432170:
            name = "천사와 함께 라이딩";
            ret = 1262;
            break;
         case 2432216:
            name = "좀비트럭 라이딩";
            ret = 1412;
            break;
         case 2432218:
            name = "니나의 마법진 라이딩";
            ret = 1413;
            break;
         case 2432291:
            name = "스케이트 보드 라이딩";
            ret = 1420;
            break;
         case 2432293:
            name = "호박마차 라이딩";
            ret = 1421;
            break;
         case 2432295:
            name = "벨룸 라이딩";
            ret = 1423;
            break;
         case 2432309:
            name = "지니 라이딩";
            ret = 1404;
            break;
         case 2432328:
            name = "네이버 모자 라이딩";
            ret = 1435;
            break;
         case 2432347:
            name = "나인하트와 설원을 라이딩";
            ret = 1440;
            break;
         case 2432348:
            name = "시그너스와 설원을 라이딩";
            ret = 1441;
            break;
         case 2432349:
            name = "오르카와 설원을 라이딩";
            ret = 1442;
            break;
         case 2432350:
            name = "하얀마법사와 설원을 라이딩";
            ret = 1443;
            break;
         case 2432351:
            name = "힐라와 설원을 라이딩";
            ret = 1444;
            break;
         case 2432359:
            name = "천사 미카엘 라이딩";
            ret = 1445;
            break;
         case 2432361:
            name = "악마 루시퍼 라이딩";
            ret = 1447;
            break;
         case 2432418:
            name = "BBQ 거대수탉 라이딩";
            ret = 1454;
            break;
         case 2432431:
            name = "드래곤나이트 라이딩";
            ret = 1480;
            break;
         case 2432433:
            name = "매직 브룸스틱 라이딩";
            ret = 1482;
            break;
         case 2432449:
            name = "윙부츠 라이딩";
            ret = 1484;
            break;
         case 2432498:
            name = "푸른불꽃 나이트메어 라이딩";
            ret = 1508;
            break;
         case 2432500:
            name = "새해 복많이 라이딩";
            ret = 1510;
            break;
         case 2432527:
            name = "열기구 O 라이딩";
            ret = 1490;
            break;
         case 2432528:
            name = "열기구 X 라이딩";
            ret = 1491;
            break;
         case 2432552:
            name = "꿀꿀나비 라이딩";
            ret = 1492;
            break;
         case 2432580:
            name = "투명발록 라이딩";
            ret = 1503;
            break;
         case 2432582:
            name = "지각했다! 라이딩";
            ret = 1505;
            break;
         case 2432635:
            name = "스밴 라이딩";
            ret = 1517;
            break;
         case 2432645:
            name = "당나귀 라이딩";
            ret = 1531;
            break;
         case 2432653:
            name = "꽃잎 프로펠러 라이딩";
            ret = 1533;
            break;
         case 2432724:
            name = "붕붕붕 주황버섯 라이딩";
            ret = 1549;
            break;
         case 2432733:
            name = "독수으리! 라이딩";
            ret = 1552;
            break;
         case 2432735:
            name = "팬더 라이딩";
            ret = 1550;
            break;
         case 2432751:
            name = "헬리콥터 라이딩";
            ret = 1554;
            break;
         case 2432806:
            name = "나으리 라이딩";
            ret = 1557;
            break;
         case 2432994:
            name = "비기닝 오픈카 라이딩";
            ret = 1561;
            break;
         case 2432995:
            name = "라이징 스포츠카 라이딩";
            ret = 1562;
            break;
         case 2432996:
            name = "플라잉 스포츠카 라이딩";
            ret = 1563;
            break;
         case 2432997:
            name = "뉴 보드 라이딩";
            ret = 1564;
            break;
         case 2432998:
            name = "프레시 보드 라이딩";
            ret = 1565;
            break;
         case 2432999:
            name = "주니어 보드 라이딩";
            ret = 1566;
            break;
         case 2433000:
            name = "시니어 보드 라이딩";
            ret = 1567;
            break;
         case 2433001:
            name = "마스터 보드 라이딩";
            ret = 1568;
            break;
         case 2433002:
            name = "프라임 보드 라이딩";
            ret = 1569;
            break;
         case 2433003:
            name = "샤이닝 벌룬 라이딩";
            ret = 1570;
            break;
         case 2433051:
            name = "고양이 손수레 라이딩";
            ret = 1582;
            break;
         case 2433053:
            name = "제트스키 라이딩";
            ret = 1584;
            break;
         case 2433272:
            name = "시그너스와 함께 비행정";
            ret = 1617;
            break;
         case 2433274:
            name = "지그문트와 함께 비행정";
            ret = 1620;
            break;
         case 2433276:
            name = "헬레나와 함께 비행정";
            ret = 1621;
            break;
         case 2433345:
            name = "겔리메르와 함께 비행정";
            ret = 1623;
            break;
         case 2433347:
            name = "베릴과 함께 비행정";
            ret = 1625;
            break;
         case 2433349:
            name = "스우와 함께 비행정";
            ret = 1628;
            break;
         case 2433454:
            name = "복만이";
            ret = 1023;
            break;
         case 2433458:
            name = "기사단 전차 라이딩";
            ret = 1642;
            break;
         case 2433459:
            name = "발록 라이딩";
            ret = 1643;
            break;
         case 2433460:
            name = "블랙 와이번 라이딩";
            ret = 1644;
            break;
         case 2433499:
            name = "오르카의 부축 라이딩";
            ret = 1639;
            break;
         case 2433501:
            name = "헬레나의 부축 라이딩";
            ret = 1640;
            break;
         case 2433658:
            name = "별에서 온 요원 라이딩";
            ret = 1703;
            break;
         case 2433718:
            name = "파워드 수트 라이딩";
            ret = 1019;
            break;
         case 2433735:
            name = "날아라 푸른양 라이딩";
            ret = 1707;
            break;
         case 2433736:
            name = "날아라 분홍양 라이딩";
            ret = 1708;
            break;
         case 2433809:
            name = "끌어준다냥 라이딩";
            ret = 1711;
            break;
         case 2433811:
            name = "딸기케이크 라이딩";
            ret = 1713;
            break;
         case 2433932:
            name = "The MAY.Full 라이딩";
            ret = 1763;
            break;
         case 2433946:
            name = "펭귄즈 라이딩";
            ret = 1764;
            break;
         case 2433948:
            name = "동물해적단 라이딩";
            ret = 1766;
            break;
         case 2433992:
            name = "돼지바 라이딩";
            ret = 1769;
            break;
         case 2434013:
            name = "함께해 핑크빈! 라이딩";
            ret = 1775;
            break;
         case 2434077:
            name = "잠수정 라이딩";
            ret = 1776;
            break;
         case 2434079:
            name = "언프리티 헬기스타";
            ret = 1778;
            break;
         case 2434275:
            name = "서핑보드 라이딩";
            ret = 1785;
            break;
         case 2434277:
            name = "오로라 밤비니 라이딩";
            ret = 1786;
            break;
         case 2434377:
            name = "아기가 되었다 라이딩";
            ret = 1792;
            break;
         case 2434379:
            name = "꽃가마 라이딩";
            ret = 1790;
            break;
         case 2434515:
            name = "꿈틀꿈틀 라이딩";
            ret = 1811;
            break;
         case 2434517:
            name = "밤을 걷는 기차와 나 라이딩";
            ret = 1813;
            break;
         case 2434525:
            name = "성조기 모자 라이딩";
            ret = 1814;
            break;
         case 2434526:
            name = "성난황소 라이딩";
            ret = 1867;
            break;
         case 2434527:
            name = "멀라이언 라이딩";
            ret = 1868;
            break;
         case 2434580:
            name = "꺄아 송편 라이딩";
            ret = 1870;
            break;
         case 2434649:
            name = "향기방구 라이딩";
            ret = 1918;
            break;
         case 2434674:
            name = "폭주 미메트 라이딩";
            ret = 1920;
            break;
         case 2434728:
            name = "게오르크 라이딩";
            ret = 1933;
            break;
         case 2434735:
            name = "익룡 라이딩";
            ret = 1921;
            break;
         case 2434737:
            name = "종이배 라이딩";
            ret = 1923;
            break;
         case 2434761:
            name = "분쇄기 라이딩";
            ret = 1934;
            break;
         case 2434762:
            name = "미니 블랙헤븐 라이딩";
            ret = 1935;
            break;
         case 2434967:
            name = "루돌프 썰매 라이딩";
            ret = 1942;
            break;
         case 2435089:
            name = "스팀 실린더 윙 라이딩";
            ret = 1956;
            break;
         case 2435091:
            name = "눈꽃 마법진 라이딩";
            ret = 1958;
            break;
         case 2435112:
            name = "개구리 수레 라이딩";
            ret = 1953;
            break;
         case 2435113:
            name = "타조 수레 라이딩";
            ret = 1954;
            break;
         case 2435114:
            name = "낙타 수레 라이딩";
            ret = 1955;
            break;
         case 2435203:
            name = "슈퍼 히어로 라이딩";
            ret = 1975;
            break;
         case 2435205:
            name = "귤팽이 라이딩";
            ret = 1977;
            break;
         case 2435296:
            name = "스킨 스쿠버 라이딩";
            ret = 1980;
            break;
         case 2435298:
            name = "꽃바람 라이딩";
            ret = 1982;
            break;
         case 2435440:
            name = "핑크빈과 함께 클래식카 라이딩";
            ret = 1988;
            break;
         case 2435441:
            name = "고급 클래식카 라이딩";
            ret = 1989;
            break;
         case 2435442:
            name = "구조되다 라이딩";
            ret = 1990;
            break;
         case 2435476:
            name = "요상한 몬스터 풍선 라이딩";
            ret = 1991;
            break;
         case 2435517:
            name = "별 빛 구름 라이딩";
            ret = 1993;
            break;
         case 2435720:
            name = "붕붕붕 핑크빈 라이딩";
            ret = 1995;
            break;
         case 2435722:
            name = "블링블링 하트 라이딩";
            ret = 1997;
            break;
         case 2435842:
            name = "구름동자 라이딩";
            ret = 2219;
            break;
         case 2435843:
            name = "난다 고래 라이딩";
            ret = 2220;
            break;
         case 2435844:
            name = "애드벌룬 라이딩";
            ret = 2221;
            break;
         case 2435845:
            name = "자이로콥터 라이딩";
            ret = 2222;
            break;
         case 2435965:
            name = "핑크 츄츄고래 라이딩";
            ret = 2223;
            break;
         case 2435967:
            name = "블루 츄츄고래 라이딩";
            ret = 2225;
            break;
         case 2435986:
            name = "화염새 라이딩";
            ret = 2202;
            break;
         case 2436030:
            name = "날치 라이딩";
            ret = 2228;
            break;
         case 2436031:
            name = "아기 도요새 라이딩";
            ret = 2229;
            break;
         case 2436079:
            name = "달 라이딩";
            ret = 1246;
            break;
         case 2436080:
            name = "도령 월묘 라이딩";
            ret = 2233;
            break;
         case 2436081:
            name = "방패연 라이딩";
            ret = 2234;
            break;
         case 2436126:
            name = "흩날려라 한글 라이딩";
            ret = 2235;
            break;
         case 2436183:
            name = "파스텔 자전거 라이딩";
            ret = 2236;
            break;
         case 2436185:
            name = "콩콩사슴 라이딩";
            ret = 2238;
            break;
         case 2436292:
            name = "스윗 도넛 라이딩";
            ret = 2240;
            break;
         case 2436294:
            name = "쇼핑카트 라이딩";
            ret = 2242;
            break;
         case 2436405:
            name = "아이스 하트 라이딩";
            ret = 2248;
            break;
         case 2436407:
            name = "총총 너구리 라이딩";
            ret = 2250;
            break;
         case 2436523:
            name = "절친 하늘자전거 라이딩";
            ret = 2270;
            break;
         case 2436524:
            name = "까치 라이딩";
            ret = 2259;
            break;
         case 2436525:
            name = "럭비 토끼 라이딩";
            ret = 2258;
            break;
         case 2436597:
            name = "냥냥 쿠션 라이딩";
            ret = 2261;
            break;
         case 2436599:
            name = "푸른불꽃 허스키 라이딩";
            ret = 2262;
            break;
         case 2436610:
            name = "까치 바구니 라이딩";
            ret = 2265;
            break;
         case 2436648:
            name = "유니온 랭킹 라이딩";
            ret = 2266;
            break;
         case 2436714:
            name = "절친 하늘 라이딩";
            ret = 2270;
            break;
         case 2436715:
            name = "북극곰 라이딩";
            ret = 2271;
            break;
         case 2436716:
            name = "아르카나 바람의 정령 라이딩";
            ret = 2272;
            break;
         case 2436728:
            name = "바나달 라이딩";
            ret = 2278;
            break;
         case 2436730:
            name = "붕붕 킥보드 라이딩";
            ret = 2277;
            break;
         case 2436778:
            name = "꿀냥 꿀냥 라이딩";
            ret = 2287;
            break;
         case 2436780:
            name = "키다리 풍선 라이딩";
            ret = 2289;
            break;
         case 2436837:
            name = "하트 병자리 라이딩";
            ret = 2295;
            break;
         case 2436839:
            name = "우주 별고래 라이딩";
            ret = 2297;
            break;
         case 2436957:
            name = "우주선 라이딩";
            ret = 2302;
            break;
         case 2437026:
            name = "야광별 라이딩";
            ret = 2304;
            break;
         case 2437040:
            name = "총총 사막여우 라이딩";
            ret = 2305;
            break;
         case 2437042:
            name = "둥실 무지개 너머 라이딩";
            ret = 2307;
            break;
         case 2437123:
            name = "샤이닝 요트 세일링 라이딩";
            ret = 2314;
            break;
         case 2437125:
            name = "더키와 목욕 타임 라이딩";
            ret = 2315;
            break;
         case 2437240:
            name = "바나나보트 라이딩";
            ret = 2318;
            break;
         case 2437259:
            name = "아이스 드래곤 라이딩";
            ret = 2319;
            break;
         case 2437261:
            name = "고고 캠핑카 라이딩";
            ret = 2321;
            break;
         case 2437497:
            name = "슬라임 빙수기 라이딩";
            ret = 2335;
            break;
         case 2437623:
            name = "크리스탈 마법진 라이딩";
            ret = 2345;
            break;
         case 2437625:
            name = "돌격! 정령군단 라이딩";
            ret = 2347;
            break;
         case 2437719:
            name = "바람은 풍선을 타고 라이딩";
            ret = 2358;
            break;
         case 2437721:
            name = "트윙클 솜사탕 라이딩";
            ret = 2356;
            break;
         case 2437737:
            name = "돈워리! 라이딩";
            ret = 2354;
            break;
         case 2437738:
            name = "날아라 보물지도 라이딩";
            ret = 2361;
            break;
         case 2437794:
            name = "베스트 드라이버! 라이딩";
            ret = 2355;
            break;
         case 2437809:
            name = "굿 밤 라이딩";
            ret = 2367;
            break;
         case 2437852:
            name = "마녀의 빗자루 라이딩";
            ret = 1002;
            break;
         case 2437923:
            name = "신나는 발걸음 라이딩";
            ret = 2369;
            break;
         case 2438136:
            name = "통통배 라이딩";
            ret = 2382;
            break;
         case 2438137:
            name = "증기기관선 라이딩";
            ret = 2383;
            break;
         case 2438138:
            name = "크루즈 라이딩";
            ret = 2384;
            break;
         case 2438139:
            name = "네이키드 바이크 라이딩";
            ret = 1005;
            break;
         case 2438340:
            name = "황금상어 라이딩";
            ret = 2375;
            break;
         case 2438373:
            name = "디스커버리 낙하산 라이딩";
            ret = 2392;
            break;
         case 2438380:
            name = "눈사람 콩콩 라이딩";
            ret = 2400;
            break;
         case 2438382:
            name = "귤끼리 라이딩";
            ret = 2402;
            break;
         case 2438408:
            name = "예핑크스 라이딩";
            ret = 2417;
            break;
         case 2438409:
            name = "껨디와 썰매 라이딩";
            ret = 2418;
            break;
         case 2438486:
            name = "날아! 토끼랑 붕붕 라이딩";
            ret = 2429;
            break;
         case 2438488:
            name = "돌돌 극세사 이불말이 라이딩";
            ret = 2432;
            break;
         case 2438493:
            name = "복조리 라이딩";
            ret = 2427;
            break;
         case 2438494:
            name = "이건 약과지 라이딩";
            ret = 2425;
            break;
         case 2438638:
            name = "봄날의 오리 라이딩";
            ret = 2433;
            break;
         case 2438640:
            name = "무지개 그네 라이딩";
            ret = 2436;
            break;
         case 2438657:
            name = "봄바람 튤립 라이딩";
            ret = 2437;
            break;
         case 2438715:
            name = "보석새 라이딩";
            ret = 2439;
            break;
         case 2438743:
            name = "러브 메신저 라이딩";
            ret = 2441;
            break;
         case 2438745:
            name = "셀럽의 슈퍼카 라이딩";
            ret = 2443;
            break;
         case 2438882:
            name = "15번가 시티투어 버스 라이딩";
            ret = 2446;
            break;
         case 2438886:
            name = "핑크빈 스쿠터 라이딩";
            ret = 2447;
            break;
         case 2439034:
            name = "오로라 돌고래 라이딩";
            ret = 2448;
            break;
         case 2439036:
            name = "화염마 라이딩";
            ret = 2450;
            break;
         case 2439127:
            name = "뽀송 하늘다람쥐 라이딩";
            ret = 2454;
            break;
         case 2439144:
            name = "파카파카 라이딩";
            ret = 2424;
            break;
         case 2439266:
            name = "워터 썸머 페스티벌 라이딩";
            ret = 2545;
            break;
         case 2439278:
            name = "거대 괴수 라이딩";
            ret = 2546;
            break;
         case 2439295:
            name = "리버스 다크소울 라이딩";
            ret = 2547;
            break;
         case 2439329:
            name = "디어 판타지아 라이딩";
            ret = 2572;
            break;
         case 2439331:
            name = "스완 판타지아 라이딩";
            ret = 2573;
            break;
         case 2439406:
            name = "어둠의 근원 라이딩";
            ret = 2622;
            break;
         case 2439443:
            name = "하프물범 라이딩";
            ret = 2585;
            break;
         case 2439484:
            name = "푸드트럭이담 라이딩";
            ret = 2628;
            break;
         case 2439486:
            name = "루시드의 날개 라이딩";
            ret = 2630;
            break;
         case 2439666:
            name = "트랙터 라이딩";
            ret = 2594;
            break;
         case 2439667:
            name = "병아리 라이딩";
            ret = 2595;
            break;
         case 2439675:
            name = "이클립스 버드 라이딩";
            ret = 2648;
            break;
         case 2439677:
            name = "케이카 라이딩";
            ret = 2650;
            break;
         case 2439909:
            name = "눈 굴리기 라이딩";
            ret = 2660;
            break;
         case 2439913:
            name = "글래셔 라이딩";
            ret = 2664;
            break;
         case 2439933:
            name = "붕어빵 라이딩";
            ret = 2667;
            break;
         case 2630116:
            name = "달콤 찐득 초코 슬라임 라이딩";
            ret = 2668;
            break;
         case 2630240:
            name = "호라이즌 페가수스 라이딩";
            ret = 2698;
            break;
         case 2630261:
            name = "돼지런 라이딩";
            ret = 2699;
            break;
         case 2630279:
            name = "허수아비 라이딩";
            ret = 2702;
            break;
         case 2630386:
            name = "집중! 라이딩";
            ret = 2712;
            break;
         case 2630387:
            name = "모험 가득 블랙빈 라이딩";
            ret = 2713;
            break;
         case 2630448:
            name = "전부 담으시개 라이딩";
            ret = 2714;
            break;
         case 2630451:
            name = "클라우드 라이딩";
            ret = 2715;
            break;
         case 2630452:
            name = "클라우드 라이딩";
            ret = 2716;
            break;
         case 2630476:
            name = "코튼 캔디빈 라이딩";
            ret = 2735;
            break;
         case 2630488:
            name = "뉴트로 화염새 라이딩";
            ret = 2738;
            break;
         case 2630563:
            name = "뉴트로 냉동참치 라이딩";
            ret = 2740;
            break;
         case 2630570:
            name = "해적 거북이 라이딩";
            ret = 2752;
            break;
         case 2630573:
            name = "뽀송 토끼 라이딩";
            ret = 2754;
            break;
         case 2630575:
            name = "쁘띠 샤크 라이딩";
            ret = 2756;
            break;
         case 2630576:
            name = "쁘띠 샤크 라이딩";
            ret = 2757;
            break;
         case 2630763:
            name = "글로리온 라이딩";
            ret = 2742;
            break;
         case 2630764:
            name = "도철 라이딩";
            ret = 2743;
            break;
         case 2630765:
            name = "천마 라이딩";
            ret = 2744;
            break;
         case 2630913:
            name = "얼음 마차 라이딩";
            ret = 2843;
            break;
         case 2630914:
            name = "얼음 마차 라이딩";
            ret = 2844;
            break;
         case 2630917:
            name = "레드 씨트론 라이딩";
            ret = 2831;
            break;
         case 2630918:
            name = "블루 씨트론 라이딩";
            ret = 2845;
            break;
         case 2630919:
            name = "옐로우 씨트론 라이딩";
            ret = 2846;
            break;
         case 2630971:
            name = "해치와 함께 라이딩";
            ret = 2853;
            break;
         case 2631136:
            name = "할로윈의 유령 라이딩";
            ret = 2858;
            break;
         case 2631140:
            name = "천둥새 라이딩";
            ret = 2855;
            break;
         case 2631190:
            name = "달려라 보드 라이딩";
            ret = 2859;
            break;
         case 2631191:
            name = "달려라 보드 라이딩";
            ret = 2860;
            break;
         case 2631460:
            name = "새우튀김 라이딩";
            ret = 2870;
            break;
         case 2633310:
            name = "핑크 래빗 라이딩";
            ret = 3050;
            break;
         case 2633562:
            name = "해의 토지령 라이딩";
            ret = 3065;
            break;
         case 2634022:
            name = "김밥 라이딩";
            ret = 3115;
            break;
         case 2634023:
            name = "핫도그 라이딩";
            ret = 3116;
            break;
         case 2634272:
            name = "별빛 마차 라이딩";
            ret = 3140;
            break;
         case 2634273:
            name = "슈팅 스타 라이딩";
            ret = 3139;
            break;
         case 2634425:
            name = "새로운 여정 라이딩";
            ret = 3145;
            break;
         case 2634506:
            name = "천둥 유니콘 라이딩";
            ret = 3149;
            break;
         case 2635082:
            name = "홀로그램 불꽃 늑대 라이딩";
            ret = 3212;
            break;
         case 2635130:
            name = "천둥 금붕어 라이딩";
            ret = 3214;
            break;
         case 2635523:
            name = "햄찌도 간식줘 라이딩";
            ret = 3244;
      }

      return name != "" ? new Pair<>(ret, name) : null;
   }

   public static void loadBGM() {
      if (musicList.size() > 0) {
         musicList.clear();
      }

      Table table = Properties.loadTable(DBConfig.isGanglim ? "data/Ganglim" : "data/Jin", "MusicList.data");

      for (Table child : table.list()) {
         String path = child.getProperty("bgmPath", "");
         String name = child.getProperty("musicName", "");
         musicList.add(new Pair<>(path, name));
      }

      System.out.println(musicList.size() + "개의 노래 리스트를 불러왔습니다.");
   }

   public static List<String> getBGMPaths() {
      List<String> list = new ArrayList<>();

      for (Pair<String, String> pair : musicList) {
         list.add(pair.left);
      }

      return list;
   }

   public static String getBGMPath(int sel) {
      return musicList.get(sel) == null ? "" : (String)musicList.get(sel).left;
   }

   public static List<String> getBGMNames() {
      List<String> list = new ArrayList<>();

      for (Pair<String, String> pair : musicList) {
         list.add(pair.right);
      }

      return list;
   }

   public static String getBGMName(int sel) {
      return musicList.get(sel) == null ? "" : (String)musicList.get(sel).right;
   }

   public static String getBGMNameUsingPath(String path) {
      for (Pair<String, String> pair : musicList) {
         if (pair.left.equals(path)) {
            return pair.right;
         }
      }

      return path.contains(".img/") ? path.split(".img/")[1] : path;
   }

   public static int getBGMSize() {
      return musicList.size();
   }

   public static boolean isTogetherPointMap(int mapid) {
      switch (mapid) {
         case 105200100:
         case 105200110:
         case 105200200:
         case 105200210:
         case 105200300:
         case 105200310:
         case 105200400:
         case 105200410:
         case 105200500:
         case 105200510:
         case 105200600:
         case 105200610:
         case 105200700:
         case 105200710:
         case 105200800:
         case 105200810:
         case 211070100:
         case 211070101:
         case 211070102:
         case 220080100:
         case 220080200:
         case 220080300:
         case 240060000:
         case 240060001:
         case 240060100:
         case 240060101:
         case 240060200:
         case 240060201:
         case 240060300:
         case 270050100:
         case 270051100:
         case 271040100:
         case 271040200:
         case 271040300:
         case 271041100:
         case 271041200:
         case 271041300:
         case 272020200:
         case 272020210:
         case 272020300:
         case 272020310:
         case 401060100:
         case 401060200:
         case 401060300:
         case 450009400:
         case 450009430:
         case 450009450:
         case 450009480:
            return true;
         default:
            return mapid >= 271040100 && mapid <= 271040399 ? true : mapid >= 271041100 && mapid <= 271041399;
      }
   }

   public static boolean isJobCode(int jobid) {
      int[] allJobs = new int[]{
         0,
         100,
         110,
         111,
         112,
         120,
         121,
         122,
         130,
         131,
         132,
         200,
         210,
         211,
         212,
         220,
         221,
         222,
         230,
         231,
         232,
         300,
         310,
         311,
         312,
         320,
         321,
         322,
         301,
         330,
         331,
         332,
         400,
         410,
         411,
         412,
         420,
         421,
         422,
         430,
         431,
         432,
         433,
         434,
         500,
         510,
         511,
         512,
         520,
         521,
         522,
         501,
         530,
         531,
         532,
         900,
         800,
         1000,
         1100,
         1110,
         1111,
         1112,
         1200,
         1210,
         1211,
         1212,
         1300,
         1310,
         1311,
         1312,
         1400,
         1410,
         1411,
         1412,
         1500,
         1510,
         1511,
         1512,
         2000,
         2100,
         2110,
         2111,
         2112,
         2001,
         2200,
         2210,
         2211,
         2212,
         2213,
         2214,
         2215,
         2216,
         2217,
         2218,
         2002,
         2300,
         2310,
         2311,
         2312,
         2003,
         2400,
         2410,
         2411,
         2412,
         2004,
         2700,
         2710,
         2711,
         2712,
         3000,
         3001,
         3100,
         3110,
         3111,
         3112,
         3101,
         3120,
         3121,
         3122,
         3200,
         3210,
         3211,
         3212,
         3300,
         3310,
         3311,
         3312,
         3500,
         3510,
         3511,
         3512,
         3002,
         3600,
         3610,
         3611,
         3612,
         5000,
         5100,
         5110,
         5111,
         5112,
         6000,
         6100,
         6110,
         6111,
         6112,
         6001,
         6500,
         6510,
         6511,
         6512,
         10000,
         10100,
         10110,
         10111,
         10112,
         2005,
         2500,
         2510,
         2511,
         2512,
         30000,
         13000,
         13100,
         13001,
         13500,
         14000,
         14200,
         14210,
         14211,
         14212,
         3700,
         3710,
         3711,
         3712,
         15000,
         15200,
         15210,
         15211,
         15212,
         6002,
         6400,
         6410,
         6411,
         6412,
         15001,
         15500,
         15510,
         15511,
         15512,
         16000,
         16400,
         16410,
         16411,
         16412,
         15002,
         15100,
         15110,
         15111,
         15112,
         6003,
         6300,
         6310,
         6311,
         6312,
         16001,
         16200,
         16210,
         16211,
         16212
      };

      for (int job : allJobs) {
         if (jobid == job) {
            return true;
         }
      }

      return false;
   }

   public static boolean isRoyalBanMesoItem(int itemid) {
      switch (itemid) {
         case 4001715:
         case 4001716:
            return true;
         default:
            return false;
      }
   }

   public static boolean isRoyalBanDropItem(int itemid) {
      switch (itemid) {
         case 1113032:
         case 1122812:
         case 1143891:
         case 1143892:
         case 1143893:
         case 1143894:
         case 1143895:
         case 1143896:
         case 1143897:
         case 1143898:
         case 1143899:
         case 1143900:
         case 1143901:
         case 1143902:
         case 1143903:
         case 1143904:
         case 1143905:
         case 1190305:
         case 4031228:
            return true;
         default:
            return false;
      }
   }

   public static boolean isRelicUnbound(int skillid) {
      return skillid >= 400031047 && skillid <= 400031051;
   }

   public static boolean isObsidianBarrier(int skillid) {
      return skillid >= 400031037 && skillid <= 400031043;
   }

   public static boolean isBlessStackSkill(int skillID) {
      switch (skillID) {
         case 400001042:
         case 400001043:
         case 400001044:
         case 400001045:
         case 400001046:
         case 400001047:
         case 400001048:
         case 400001049:
         case 400001050:
            return true;
         default:
            return false;
      }
   }

   public static boolean isHexSkill(int skillID) {
      switch (skillID) {
         case 154111006:
         case 154121001:
         case 154121002:
         case 400041082:
         case 400041083:
            return true;
         default:
            return false;
      }
   }

   public static boolean isHellInstance(FieldSetInstance fsi) {
      return fsi != null
         && (
            fsi instanceof HellLucidBoss
               || fsi instanceof HellDemianBoss
               || fsi instanceof HellBlackHeavenBoss
               || fsi instanceof HellWillBoss
               || fsi instanceof HellDunkelBoss
         );
   }

   public static boolean isJinEndlessMedal(int itemID) {
      switch (itemID) {
         case 1142242:
         case 1142243:
         case 1142244:
         case 1142245:
         case 1142246:
         case 1142247:
            return true;
         default:
            return false;
      }
   }

   public static boolean isJinBossMesoFixMap(int mapID) {
      switch (mapID) {
         case 105200110:
         case 105200210:
         case 105200310:
         case 105200410:
         case 105200510:
         case 105200610:
         case 105200710:
         case 105200810:
         case 160050000:
         case 160060000:
         case 211070100:
         case 220080100:
         case 220080200:
         case 220080300:
         case 240060200:
         case 240060201:
         case 240060300:
         case 270050100:
         case 270051100:
         case 271040100:
         case 272020200:
         case 272020210:
         case 280030000:
         case 280030100:
         case 350060650:
         case 350060950:
         case 350160180:
         case 350160280:
         case 401060100:
         case 401060200:
         case 410002080:
         case 410002180:
         case 450004300:
         case 450004600:
         case 450008080:
         case 450008380:
         case 450008980:
         case 450009430:
         case 450009480:
         case 450010550:
         case 450010950:
         case 450012250:
         case 450012650:
         case 450013750:
            return true;
         default:
            return false;
      }
   }

   public static int getLinkedCubeQuest(GradeRandomOption option) {
      switch (option) {
         case Red:
            return QuestExConstants.RedCubeInfo.getQuestID();
         case Black:
            return QuestExConstants.BlackCubeInfo.getQuestID();
         case Additional:
            return QuestExConstants.AdditionalCubeInfo.getQuestID();
         case AmazingAdditional:
            return QuestExConstants.WhiteAdditionalCubeInfo.getQuestID();
         default:
            return 0;
      }
   }

   public static int getCubeLevelUpCount(GradeRandomOption option, int grade) {
      switch (option) {
         case Red:
            switch (grade) {
               case 1:
                  return 50;
               case 2:
                  return 160;
               case 3:
                  return 1000;
               default:
                  return 10000;
            }
         case Black:
            switch (grade) {
               case 1:
                  return 20;
               case 2:
                  return 80;
               case 3:
                  return 220;
               default:
                  return 10000;
            }
         case Additional:
            switch (grade) {
               case 1:
                  return 60;
               case 2:
                  return 150;
               case 3:
                  return 430;
               default:
                  return 10000;
            }
         case AmazingAdditional:
            if (DBConfig.isGanglim) {
               return 0;
            }

            switch (grade) {
               case 1:
                  return 60;
               case 2:
                  return 150;
               case 3:
                  return 430;
            }
      }

      return 10000;
   }

   public static boolean isStandChair(int chairID) {
      switch (chairID) {
         case 3018996:
            return true;
         default:
            return false;
      }
   }

   public static boolean isUnvisibleMob(int mobid) {
      switch (mobid) {
         case 8644658:
         case 8644659:
         case 8645068:
         case 8880406:
         case 8880407:
         case 8880411:
         case 8880412:
         case 8880601:
         case 8880602:
         case 8880608:
         case 8880631:
         case 8880638:
         case 8880702:
         case 8880713:
            return true;
         default:
            return false;
      }
   }

   public static boolean isCanUpgradeExceptionalEquip(int itemId) {
      if (isExceptionalEquip.get(itemId) != null) {
         return isExceptionalEquip.get(itemId);
      } else {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         boolean isCan = ii.getMaxExceptionalSlots(itemId) > 0;
         isExceptionalEquip.put(itemId, isCan);
         return isCan;
      }
   }

   public static boolean isNeedToWriteLogItem(int itemId) {
      switch (itemId) {
         case 2046076:
         case 2046077:
         case 2046150:
         case 2046340:
         case 2046341:
         case 2046991:
         case 2048717:
         case 2430044:
         case 4001716:
         case 4021031:
         case 4031277:
         case 5068305:
            return true;
         default:
            return false;
      }
   }

   static {
      CustomItem ci = new CustomItem(customItems.size(), CustomItem.CustomItemType.보조장비, "호그의 이빨");
      ci.addEffects(CustomItem.CustomItemEffect.BdR, 5);
      ci.addEffects(CustomItem.CustomItemEffect.AllStatR, 3);
      customItems.add(ci);
      ci = new CustomItem(customItems.size(), CustomItem.CustomItemType.보조장비, "루칸의 등껍질");
      ci.addEffects(CustomItem.CustomItemEffect.BdR, 10);
      ci.addEffects(CustomItem.CustomItemEffect.AllStatR, 6);
      customItems.add(ci);
      ci = new CustomItem(customItems.size(), CustomItem.CustomItemType.보조장비, "골드리치의 이어링");
      ci.addEffects(CustomItem.CustomItemEffect.BdR, 15);
      ci.addEffects(CustomItem.CustomItemEffect.AllStatR, 9);
      customItems.add(ci);
      ci = new CustomItem(customItems.size(), CustomItem.CustomItemType.보조장비, "어둠의 보석");
      ci.addEffects(CustomItem.CustomItemEffect.BdR, 20);
      ci.addEffects(CustomItem.CustomItemEffect.AllStatR, 12);
      customItems.add(ci);
      ci = new CustomItem(customItems.size(), CustomItem.CustomItemType.보조장비, "이계의 보석");
      ci.addEffects(CustomItem.CustomItemEffect.BdR, 25);
      ci.addEffects(CustomItem.CustomItemEffect.AllStatR, 15);
      customItems.add(ci);
      ci = new CustomItem(customItems.size(), CustomItem.CustomItemType.보조장비, "전설의 증표");
      ci.addEffects(CustomItem.CustomItemEffect.BdR, 30);
      ci.addEffects(CustomItem.CustomItemEffect.AllStatR, 18);
      customItems.add(ci);
      ci = new CustomItem(customItems.size(), CustomItem.CustomItemType.보조장비, "정복자의 영광");
      ci.addEffects(CustomItem.CustomItemEffect.BdR, 35);
      ci.addEffects(CustomItem.CustomItemEffect.AllStatR, 21);
      customItems.add(ci);
      ci = new CustomItem(customItems.size(), CustomItem.CustomItemType.보조장비, "초월자의 이어링");
      ci.addEffects(CustomItem.CustomItemEffect.BdR, 40);
      ci.addEffects(CustomItem.CustomItemEffect.AllStatR, 24);
      customItems.add(ci);
      ci = new CustomItem(customItems.size(), CustomItem.CustomItemType.보조장비, "정복자의 펜던트");
      ci.addEffects(CustomItem.CustomItemEffect.CrD, 10);
      ci.addEffects(CustomItem.CustomItemEffect.BdR, 30);
      ci.addEffects(CustomItem.CustomItemEffect.AllStatR, 30);
      ci.addEffects(CustomItem.CustomItemEffect.MesoR, 20);
      ci.addEffects(CustomItem.CustomItemEffect.DropR, 20);
      customItems.add(ci);
      ci = new CustomItem(customItems.size(), CustomItem.CustomItemType.각인석, "흔한 각인석");
      ci.addEffects(CustomItem.CustomItemEffect.CrD, 8);
      ci.addEffects(CustomItem.CustomItemEffect.DropR, 10);
      customItems.add(ci);
      ci = new CustomItem(customItems.size(), CustomItem.CustomItemType.각인석, "희귀한 각인석");
      ci.addEffects(CustomItem.CustomItemEffect.CrD, 16);
      ci.addEffects(CustomItem.CustomItemEffect.DropR, 20);
      customItems.add(ci);
      ci = new CustomItem(customItems.size(), CustomItem.CustomItemType.각인석, "레전더리 각인석");
      ci.addEffects(CustomItem.CustomItemEffect.CrD, 24);
      ci.addEffects(CustomItem.CustomItemEffect.DropR, 30);
      customItems.add(ci);
      ci = new CustomItem(customItems.size(), CustomItem.CustomItemType.각인석, "에픽 각인석");
      ci.addEffects(CustomItem.CustomItemEffect.CrD, 40);
      ci.addEffects(CustomItem.CustomItemEffect.DropR, 50);
      customItems.add(ci);
      royalSimpleOpenNpc.put("창고", 9062294);
      royalSimpleOpenNpc.put("이동", 9062294);
      royalSimpleOpenNpc.put("상점", 9062277);
      royalSimpleOpenNpc.put("편의", 9010044);
      royalSimpleOpenNpc.put("코디", 1530055);
      royalSimpleOpenNpc.put("홍보", 1530051);
      royalSimpleOpenNpc.put("후원", 1530050);
      royalSimpleOpenNpc.put("랭킹", 9076004);
      royalSimpleOpenNpc.put("레벨보상", 1540101);
      royalSimpleOpenNpc.put("유니온", 9010106);
      royalSimpleOpenNpc.put("제작", 2040050);
      if (DBConfig.isGanglim) {
         royalDamageTearMap.put(1, 10);
         royalDamageTearMap.put(2, 15);
         royalDamageTearMap.put(3, 20);
         royalDamageTearMap.put(4, 25);
         royalDamageTearMap.put(5, 30);
         royalDamageTearMap.put(6, 40);
         royalDamageTearMap.put(7, 60);
         royalDamageTearMap.put(8, 80);
         royalDamageTearMap.put(9, 90);
         royalDamageTearMap.put(10, 100);
         royalDamageTearMap.put(11, 120);
         royalDamageTearMap.put(12, 140);
         royalDamageTearMap.put(13, 160);
         royalDamageTearMap.put(14, 180);
         royalDamageTearMap.put(15, 200);
         royalDamageTearMap.put(16, 220);
         royalDamageTearMap.put(17, 240);
         royalDamageTearMap.put(18, 260);
         royalDamageTearMap.put(19, 280);
         royalDamageTearMap.put(20, 300);
         royalBossDamageTearMap.put(1, 10);
         royalBossDamageTearMap.put(2, 20);
         royalBossDamageTearMap.put(3, 30);
         royalBossDamageTearMap.put(4, 50);
         royalBossDamageTearMap.put(5, 70);
         royalBossDamageTearMap.put(6, 100);
         royalBossDamageTearMap.put(7, 120);
         royalBossDamageTearMap.put(8, 150);
         royalBossDamageTearMap.put(9, 170);
         royalBossDamageTearMap.put(10, 200);
      }

      FatigueMapList = new ArrayList<>();
      FatigueMapList.add(261020700);
      FatigueMapList.add(261010103);
      rangedMapobjectTypes = Collections.unmodifiableList(
         Arrays.asList(
            MapleMapObjectType.ITEM,
            MapleMapObjectType.MONSTER,
            MapleMapObjectType.DOOR,
            MapleMapObjectType.REACTOR,
            MapleMapObjectType.SUMMON,
            MapleMapObjectType.NPC,
            MapleMapObjectType.MIST,
            MapleMapObjectType.EXTRACTOR,
            MapleMapObjectType.DYNAMIC_FOOTHOLD,
            MapleMapObjectType.PLAYER
         )
      );
      closeness = new int[]{
         0,
         1,
         3,
         6,
         14,
         31,
         60,
         108,
         181,
         287,
         434,
         632,
         891,
         1224,
         1642,
         2161,
         2793,
         3557,
         4467,
         5542,
         6801,
         8263,
         9950,
         11882,
         14084,
         16578,
         19391,
         22547,
         26074,
         30000
      };
      setScore = new int[]{0, 10, 100, 300, 600, 1000, 2000, 4000, 7000, 10000};
      cumulativeTraitExp = new int[]{
         0,
         20,
         46,
         80,
         124,
         181,
         255,
         351,
         476,
         639,
         851,
         1084,
         1340,
         1622,
         1932,
         2273,
         2648,
         3061,
         3515,
         4014,
         4563,
         5128,
         5710,
         6309,
         6926,
         7562,
         8217,
         8892,
         9587,
         10303,
         11040,
         11788,
         12547,
         13307,
         14089,
         14883,
         15689,
         16507,
         17337,
         18179,
         19034,
         19902,
         20783,
         21677,
         22584,
         23505,
         24440,
         25399,
         26362,
         27339,
         28331,
         29338,
         30360,
         31397,
         32450,
         33519,
         34604,
         35705,
         36823,
         37958,
         39110,
         40279,
         41466,
         32671,
         43894,
         45135,
         46395,
         47674,
         48972,
         50289,
         51626,
         52967,
         54312,
         55661,
         57014,
         58371,
         59732,
         61097,
         62466,
         63839,
         65216,
         66597,
         67982,
         69371,
         70764,
         72161,
         73562,
         74967,
         76376,
         77789,
         79206,
         80627,
         82052,
         83481,
         84914,
         86351,
         87792,
         89237,
         90686,
         92139,
         93596,
         96000
      };
      pvpExp = new int[]{0, 3000, 6000, 12000, 24000, 48000, 960000, 192000, 384000, 768000};
      guildexp = new int[]{
         0,
         15000,
         45000,
         75000,
         105000,
         135000,
         165000,
         195000,
         225000,
         255000,
         285000,
         315000,
         345000,
         375000,
         405000,
         435000,
         465000,
         495000,
         525000,
         555000,
         585000,
         615000,
         645000,
         675000,
         705000,
         3888000,
         5637600,
         8174520,
         11853050,
         30554530,
         0
      };
      mountexp = new int[]{
         0,
         6,
         25,
         50,
         105,
         134,
         196,
         254,
         263,
         315,
         367,
         430,
         543,
         587,
         679,
         725,
         897,
         1146,
         1394,
         1701,
         2247,
         2543,
         2898,
         3156,
         3313,
         3584,
         3923,
         4150,
         4305,
         4550
      };
      itemBlock = new int[]{
         4001168,
         5220013,
         3993003,
         2340000,
         2049100,
         4001129,
         2040037,
         2040006,
         2040007,
         2040303,
         2040403,
         2040506,
         2040507,
         2040603,
         2040709,
         2040710,
         2040711,
         2040806,
         2040903,
         2041024,
         2041025,
         2043003,
         2043103,
         2043203,
         2043303,
         2043703,
         2043803,
         2044003,
         2044103,
         2044203,
         2044303,
         2044403,
         2044503,
         2044603,
         2044908,
         2044815,
         2044019,
         2044703
      };
      cashBlock = new int[]{
         5080001,
         5080000,
         5063000,
         5064000,
         5660000,
         5660001,
         5222027,
         5530172,
         5530173,
         5530174,
         5530175,
         5530176,
         5530177,
         5251016,
         5534000,
         5152053,
         5152058,
         5150044,
         5150040,
         5220082,
         5680021,
         5150050,
         5211091,
         5211092,
         5220087,
         5220088,
         5220089,
         5220090,
         5220085,
         5220086,
         5470000,
         1002971,
         1052202,
         5060003,
         5060004,
         5680015,
         5220082,
         5530146,
         5530147,
         5530148,
         5710000,
         5500000,
         5500001,
         5500002,
         5500002,
         5500003,
         5500004,
         5500005,
         5500006,
         5050000,
         5075000,
         5075001,
         5075002,
         1122121,
         5450000,
         5190007,
         5600000,
         5600001,
         5350003,
         2300002,
         2300003,
         5330000,
         5062000,
         5062001,
         5211071,
         5211072,
         5211073,
         5211074,
         5211075,
         5211076,
         5211077,
         5211078,
         5211079,
         5650000,
         5431000,
         5431001,
         5432000,
         5450000,
         5550000,
         5550001,
         5640000,
         5530013,
         5150039,
         5150040,
         5150046,
         5150054,
         5150052,
         5150053,
         5151035,
         5151036,
         5152053,
         5152056,
         5152057,
         5152058,
         1812006,
         5650000,
         5222000,
         5221001,
         5220014,
         5220015,
         5420007,
         5451000,
         5210000,
         5210001,
         5210002,
         5210003,
         5210004,
         5210005,
         5210006,
         5210007,
         5210008,
         5210009,
         5210010,
         5210011,
         5211000,
         5211001,
         5211002,
         5211003,
         5211004,
         5211005,
         5211006,
         5211007,
         5211008,
         5211009,
         5211010,
         5211011,
         5211012,
         5211013,
         5211014,
         5211015,
         5211016,
         5211017,
         5211018,
         5211019,
         5211020,
         5211021,
         5211022,
         5211023,
         5211024,
         5211025,
         5211026,
         5211027,
         5211028,
         5211029,
         5211030,
         5211031,
         5211032,
         5211033,
         5211034,
         5211035,
         5211036,
         5211037,
         5211038,
         5211039,
         5211040,
         5211041,
         5211042,
         5211043,
         5211044,
         5211045,
         5211046,
         5211047,
         5211048,
         5211049,
         5211050,
         5211051,
         5211052,
         5211053,
         5211054,
         5211055,
         5211056,
         5211057,
         5211058,
         5211059,
         5211060,
         5211061,
         5360000,
         5360001,
         5360002,
         5360003,
         5360004,
         5360005,
         5360006,
         5360007,
         5360008,
         5360009,
         5360010,
         5360011,
         5360012,
         5360013,
         5360014,
         5360017,
         5360050,
         5211050,
         5360042,
         5360052,
         5360053,
         5360050,
         1112810,
         1112811,
         5530013,
         4001431,
         4001432,
         4032605,
         5140000,
         5140001,
         5140002,
         5140003,
         5140004,
         5140007,
         5270000,
         5270001,
         5270002,
         5270003,
         5270004,
         5270005,
         5270006,
         9102328,
         9102329,
         9102330,
         9102331,
         9102332,
         9102333,
         1112127,
         1112741,
         1112918
      };
      blockedSkills = new int[]{4341003};
      RESERVED = new String[]{"Rental", "Donor", "MapleNews"};
      stats = new String[]{
         "incSTR",
         "incDEX",
         "incINT",
         "incLUK",
         "incMHP",
         "incMMP",
         "incMHPr",
         "incMMPr",
         "incPAD",
         "incMAD",
         "incPDD",
         "incMDD",
         "incACC",
         "incEVA",
         "incCraft",
         "incSpeed",
         "incJump",
         "royalSpecial",
         "masterSpecial",
         "tuc",
         "reqLevel",
         "reqJob",
         "reqSTR",
         "reqDEX",
         "reqINT",
         "reqLUK",
         "reqPOP",
         "cash",
         "cursed",
         "success",
         "setItemID",
         "equipTradeBlock",
         "durability",
         "randOption",
         "randStat",
         "masterLevel",
         "reqSkillLevel",
         "elemDefault",
         "incRMAS",
         "incRMAF",
         "incRMAI",
         "incRMAL",
         "canLevel",
         "skill",
         "charmEXP",
         "bdR",
         "imdR",
         "android",
         "Etuc"
      };
      hyperTele = new int[]{
         310000000, 220000000, 100000000, 250000000, 240000000, 104000000, 103000000, 102000000, 101000000, 120000000, 260000000, 200000000, 230000000
      };
      rankC = new int[]{
         70000000, 70000001, 70000002, 70000003, 70000004, 70000005, 70000006, 70000007, 70000008, 70000009, 70000010, 70000011, 70000012, 70000013
      };
      rankB = new int[]{70000014, 70000015, 70000016, 70000017, 70000018, 70000021, 70000022, 70000023, 70000024, 70000025, 70000026};
      rankA = new int[]{
         70000027, 70000028, 70000029, 70000030, 70000031, 70000032, 70000033, 70000034, 70000035, 70000036, 70000039, 70000040, 70000041, 70000042
      };
      rankS = new int[]{
         70000043,
         70000044,
         70000045,
         70000047,
         70000048,
         70000049,
         70000050,
         70000051,
         70000052,
         70000053,
         70000054,
         70000055,
         70000056,
         70000057,
         70000058,
         70000059,
         70000060,
         70000061,
         70000062
      };
      exp = new long[]{
         1L,
         15L,
         34L,
         57L,
         92L,
         135L,
         372L,
         560L,
         840L,
         1242L,
         1242L,
         1242L,
         1242L,
         1242L,
         1242L,
         1490L,
         1788L,
         2145L,
         2574L,
         3088L,
         3705L,
         4446L,
         5335L,
         6402L,
         7682L,
         9218L,
         11061L,
         13273L,
         15927L,
         19112L,
         19112L,
         19112L,
         19112L,
         19112L,
         19112L,
         22934L,
         27520L,
         33024L,
         39628L,
         47553L,
         51357L,
         55465L,
         59902L,
         64694L,
         69869L,
         75458L,
         81494L,
         88013L,
         95054L,
         102658L,
         110870L,
         119739L,
         129318L,
         139663L,
         150836L,
         162902L,
         175934L,
         190008L,
         205208L,
         221624L,
         221624L,
         221624L,
         221624L,
         221624L,
         221624L,
         238245L,
         256113L,
         275321L,
         295970L,
         318167L,
         342029L,
         367681L,
         395257L,
         424901L,
         456768L,
         488741L,
         522952L,
         559558L,
         598727L,
         640637L,
         685481L,
         733464L,
         784806L,
         839742L,
         898523L,
         961419L,
         1028718L,
         1100728L,
         1177778L,
         1260222L,
         1342136L,
         1429374L,
         1522283L,
         1621231L,
         1726611L,
         1838840L,
         1958364L,
         2085657L,
         2221224L,
         2365603L,
         2365603L,
         2365603L,
         2365603L,
         2365603L,
         2365603L,
         2519367L,
         2683125L,
         2857528L,
         3043267L,
         3241079L,
         3451749L,
         3676112L,
         3915059L,
         4169537L,
         4440556L,
         4729192L,
         5036589L,
         5363967L,
         5712624L,
         6083944L,
         6479400L,
         6900561L,
         7349097L,
         7826788L,
         8335529L,
         8877338L,
         9454364L,
         10068897L,
         10723375L,
         11420394L,
         12162719L,
         12953295L,
         13795259L,
         14691950L,
         15646926L,
         16663976L,
         17747134L,
         18900697L,
         20129242L,
         21437642L,
         22777494L,
         24201087L,
         25713654L,
         27320757L,
         29028304L,
         30842573L,
         32770233L,
         34818372L,
         36994520L,
         39306677L,
         41763344L,
         44373553L,
         47146900L,
         50093581L,
         53224429L,
         56550955L,
         60085389L,
         63840725L,
         67830770L,
         72070193L,
         76574580L,
         81360491L,
         86445521L,
         91848366L,
         97588888L,
         103688193L,
         110168705L,
         117054249L,
         124370139L,
         132143272L,
         138750435L,
         145687956L,
         152972353L,
         160620970L,
         168652018L,
         177084618L,
         185938848L,
         192535790L,
         204997579L,
         215247457L,
         226009829L,
         237310320L,
         249175836L,
         261634627L,
         274716358L,
         288452175L,
         302874783L,
         318018522L,
         333919448L,
         350615420L,
         368146191L,
         386553500L,
         405881175L,
         426175233L,
         447483994L,
         469858193L,
         493351102L,
         518018657L,
         543919589L,
         571115568L,
         2207026470L,
         2471869646L,
         2768494003L,
         3100713283L,
         3472798876L,
         3889534741L,
         4356278909L,
         4879032378L,
         5464516263L,
         6120258214L,
         7956335678L,
         8831532602L,
         9803001188L,
         10881331318L,
         12078277762L,
         15701761090L,
         17114919588L,
         18655262350L,
         20334235961L,
         22164317197L,
         28813612356L,
         30830565220L,
         32988704785L,
         35297914119L,
         37768768107L,
         49099398539L,
         52536356436L,
         56213901386L,
         60148874483L,
         64359295696L,
         83667084404L,
         86177096936L,
         88762409844L,
         91425282139L,
         94168040603L,
         122418452783L,
         126091006366L,
         129873736556L,
         133769948652L,
         137783047111L,
         179117961244L,
         184491500081L,
         190026245083L,
         195727032435L,
         201598843408L,
         262078496430L,
         269940851322L,
         278039076861L,
         286380249166L,
         294971656640L,
         442457484960L,
         455731209508L,
         469403145793L,
         483485240166L,
         497989797370L,
         512929491291L,
         528317376029L,
         544166897309L,
         560491904228L,
         577306661354L,
         1731919984062L,
         1749239183902L,
         1766731575741L,
         1784398891498L,
         1802242880412L,
         2342915744535L,
         2366344901980L,
         2390008350999L,
         2413908434508L,
         2438047518853L,
         5412465491853L,
         5466590146771L,
         5521256048238L,
         5576468608720L,
         5632233294807L,
         11377111255510L,
         12514822381061L,
         13766304619167L,
         15142935081083L,
         16657228589191L,
         33647601750165L,
         37012361925181L,
         40713598117699L,
         44784957929468L,
         49263453722414L,
         99512176519276L,
         109463394171203L,
         120409733588323L,
         132450706947155L,
         145695777641870L,
         294305470836577L,
         323736017920234L,
         356109619712257L,
         391720581683482L,
         430892639851830L,
         870403132500696L,
         957443445750765L,
         1053187790325840L,
         1158506569358420L,
         1737759854037630L,
         1737759854037630L,
         9203372036854769664L
      };
      goldrewards = new int[]{
         2049400,
         1,
         2049401,
         2,
         2049301,
         2,
         2340000,
         1,
         2070007,
         2,
         2070016,
         1,
         2330007,
         1,
         2070018,
         1,
         1402037,
         1,
         2290096,
         1,
         2290049,
         1,
         2290041,
         1,
         2290047,
         1,
         2290095,
         1,
         2290017,
         1,
         2290075,
         1,
         2290085,
         1,
         2290116,
         1,
         1302059,
         3,
         2049100,
         1,
         1092049,
         1,
         1102041,
         1,
         1432018,
         3,
         1022047,
         3,
         3010051,
         1,
         3010020,
         1,
         2040914,
         1,
         1432011,
         3,
         1442020,
         3,
         1382035,
         3,
         1372010,
         3,
         1332027,
         3,
         1302056,
         3,
         1402005,
         3,
         1472053,
         3,
         1462018,
         3,
         1452017,
         3,
         1422013,
         3,
         1322029,
         3,
         1412010,
         3,
         1472051,
         1,
         1482013,
         1,
         1492013,
         1,
         1382049,
         1,
         1382050,
         1,
         1382051,
         1,
         1382052,
         1,
         1382045,
         1,
         1382047,
         1,
         1382048,
         1,
         1382046,
         1,
         1372035,
         1,
         1372036,
         1,
         1372037,
         1,
         1372038,
         1,
         1372039,
         1,
         1372040,
         1,
         1372041,
         1,
         1372042,
         1,
         1332032,
         8,
         1482025,
         7,
         4001011,
         8,
         4001010,
         8,
         4001009,
         8,
         2047000,
         1,
         2047001,
         1,
         2047002,
         1,
         2047100,
         1,
         2047101,
         1,
         2047102,
         1,
         2047200,
         1,
         2047201,
         1,
         2047202,
         1,
         2047203,
         1,
         2047204,
         1,
         2047205,
         1,
         2047206,
         1,
         2047207,
         1,
         2047208,
         1,
         2047300,
         1,
         2047301,
         1,
         2047302,
         1,
         2047303,
         1,
         2047304,
         1,
         2047305,
         1,
         2047306,
         1,
         2047307,
         1,
         2047308,
         1,
         2047309,
         1,
         2046004,
         1,
         2046005,
         1,
         2046104,
         1,
         2046105,
         1,
         2046208,
         1,
         2046209,
         1,
         2046210,
         1,
         2046211,
         1,
         2046212,
         1,
         1132014,
         3,
         1132015,
         2,
         1132016,
         1,
         1002801,
         2,
         1102205,
         2,
         1332079,
         2,
         1332080,
         2,
         1402048,
         2,
         1402049,
         2,
         1402050,
         2,
         1402051,
         2,
         1462052,
         2,
         1462054,
         2,
         1462055,
         2,
         1472074,
         2,
         1472075,
         2,
         1332077,
         1,
         1382082,
         1,
         1432063,
         1,
         1452087,
         1,
         1462053,
         1,
         1472072,
         1,
         1482048,
         1,
         1492047,
         1,
         2030008,
         5,
         1442018,
         3,
         2040900,
         4,
         2049100,
         10,
         2000005,
         10,
         2000004,
         10,
         4280000,
         8,
         2430144,
         10,
         2290285,
         10,
         2028061,
         10,
         2028062,
         10,
         2530000,
         5,
         2531000,
         5
      };
      silverrewards = new int[]{
         2049401,
         2,
         2049301,
         2,
         3010041,
         1,
         1002452,
         6,
         1002455,
         6,
         2290084,
         1,
         2290048,
         1,
         2290040,
         1,
         2290046,
         1,
         2290074,
         1,
         2290064,
         1,
         2290094,
         1,
         2290022,
         1,
         2290056,
         1,
         2290066,
         1,
         2290020,
         1,
         1102082,
         1,
         1302049,
         1,
         2340000,
         1,
         1102041,
         1,
         1452019,
         2,
         4001116,
         3,
         4001012,
         3,
         1022060,
         2,
         2430144,
         5,
         2290285,
         5,
         2028062,
         5,
         2028061,
         5,
         2530000,
         1,
         2531000,
         1,
         2041100,
         1,
         2041101,
         1,
         2041102,
         1,
         2041103,
         1,
         2041104,
         1,
         2041105,
         1,
         2041106,
         1,
         2041107,
         1,
         2041108,
         1,
         2041109,
         1,
         2041110,
         1,
         2041111,
         1,
         2041112,
         1,
         2041113,
         1,
         2041114,
         1,
         2041115,
         1,
         2041116,
         1,
         2041117,
         1,
         2041118,
         1,
         2041119,
         1,
         2041300,
         1,
         2041301,
         1,
         2041302,
         1,
         2041303,
         1,
         2041304,
         1,
         2041305,
         1,
         2041306,
         1,
         2041307,
         1,
         2041308,
         1,
         2041309,
         1,
         2041310,
         1,
         2041311,
         1,
         2041312,
         1,
         2041313,
         1,
         2041314,
         1,
         2041315,
         1,
         2041316,
         1,
         2041317,
         1,
         2041318,
         1,
         2041319,
         1,
         2049200,
         1,
         2049201,
         1,
         2049202,
         1,
         2049203,
         1,
         2049204,
         1,
         2049205,
         1,
         2049206,
         1,
         2049207,
         1,
         2049208,
         1,
         2049209,
         1,
         2049210,
         1,
         2049211,
         1,
         1432011,
         3,
         1442020,
         3,
         1382035,
         3,
         1372010,
         3,
         1332027,
         3,
         1302056,
         3,
         1402005,
         3,
         1472053,
         3,
         1462018,
         3,
         1452017,
         3,
         1422013,
         3,
         1322029,
         3,
         1412010,
         3,
         1002587,
         3,
         1402044,
         1,
         2101013,
         4,
         1442046,
         1,
         1422031,
         1,
         1332054,
         3,
         1012056,
         3,
         1022047,
         3,
         3012002,
         1,
         1442012,
         3,
         1442018,
         3,
         1432010,
         3,
         1432036,
         1,
         2000005,
         10,
         2049100,
         10,
         2000004,
         10,
         4280001,
         8
      };
      peanuts = new int[]{
         2430091,
         200,
         2430092,
         200,
         2430093,
         200,
         2430101,
         200,
         2430102,
         200,
         2430136,
         200,
         2430149,
         200,
         2340000,
         1,
         1152000,
         5,
         1152001,
         5,
         1152004,
         5,
         1152005,
         5,
         1152006,
         5,
         1152007,
         5,
         1152008,
         5,
         1152064,
         5,
         1152065,
         5,
         1152066,
         5,
         1152067,
         5,
         1152070,
         5,
         1152071,
         5,
         1152072,
         5,
         1152073,
         5,
         3010019,
         2,
         1001060,
         10,
         1002391,
         10,
         1102004,
         10,
         1050039,
         10,
         1102040,
         10,
         1102041,
         10,
         1102042,
         10,
         1102043,
         10,
         1082145,
         5,
         1082146,
         5,
         1082147,
         5,
         1082148,
         5,
         1082149,
         5,
         1082150,
         5,
         2043704,
         10,
         2040904,
         10,
         2040409,
         10,
         2040307,
         10,
         2041030,
         10,
         2040015,
         10,
         2040109,
         10,
         2041035,
         10,
         2041036,
         10,
         2040009,
         10,
         2040511,
         10,
         2040408,
         10,
         2043804,
         10,
         2044105,
         10,
         2044903,
         10,
         2044804,
         10,
         2043009,
         10,
         2043305,
         10,
         2040610,
         10,
         2040716,
         10,
         2041037,
         10,
         2043005,
         10,
         2041032,
         10,
         2040305,
         10,
         2040211,
         5,
         2040212,
         5,
         1022097,
         10,
         2049000,
         10,
         2049001,
         10,
         2049002,
         10,
         2049003,
         10,
         1012058,
         5,
         1012059,
         5,
         1012060,
         5,
         1012061,
         5,
         1332100,
         10,
         1382058,
         10,
         1402073,
         10,
         1432066,
         10,
         1442090,
         10,
         1452058,
         10,
         1462076,
         10,
         1472069,
         10,
         1482051,
         10,
         1492024,
         10,
         1342009,
         10,
         2049400,
         1,
         2049401,
         2,
         2049301,
         2,
         2049100,
         10,
         2430144,
         10,
         2290285,
         10,
         2028062,
         10,
         2028061,
         10,
         2530000,
         5,
         2531000,
         5,
         1032080,
         5,
         1032081,
         4,
         1032082,
         3,
         1032083,
         2,
         1032084,
         1,
         1112435,
         5,
         1112436,
         4,
         1112437,
         3,
         1112438,
         2,
         1112439,
         1,
         1122081,
         5,
         1122082,
         4,
         1122083,
         3,
         1122084,
         2,
         1122085,
         1,
         1132036,
         5,
         1132037,
         4,
         1132038,
         3,
         1132039,
         2,
         1132040,
         1,
         1092070,
         5,
         1092071,
         4,
         1092072,
         3,
         1092073,
         2,
         1092074,
         1,
         1092075,
         5,
         1092076,
         4,
         1092077,
         3,
         1092078,
         2,
         1092079,
         1,
         1092080,
         5,
         1092081,
         4,
         1092082,
         3,
         1092083,
         2,
         1092084,
         1,
         1092087,
         1,
         1092088,
         1,
         1092089,
         1,
         1302143,
         5,
         1302144,
         4,
         1302145,
         3,
         1302146,
         2,
         1302147,
         1,
         1312058,
         5,
         1312059,
         4,
         1312060,
         3,
         1312061,
         2,
         1312062,
         1,
         1322086,
         5,
         1322087,
         4,
         1322088,
         3,
         1322089,
         2,
         1322090,
         1,
         1332116,
         5,
         1332117,
         4,
         1332118,
         3,
         1332119,
         2,
         1332120,
         1,
         1332121,
         5,
         1332122,
         4,
         1332123,
         3,
         1332124,
         2,
         1332125,
         1,
         1342029,
         5,
         1342030,
         4,
         1342031,
         3,
         1342032,
         2,
         1342033,
         1,
         1372074,
         5,
         1372075,
         4,
         1372076,
         3,
         1372077,
         2,
         1372078,
         1,
         1382095,
         5,
         1382096,
         4,
         1382097,
         3,
         1382098,
         2,
         1392099,
         1,
         1402086,
         5,
         1402087,
         4,
         1402088,
         3,
         1402089,
         2,
         1402090,
         1,
         1412058,
         5,
         1412059,
         4,
         1412060,
         3,
         1412061,
         2,
         1412062,
         1,
         1422059,
         5,
         1422060,
         4,
         1422061,
         3,
         1422062,
         2,
         1422063,
         1,
         1432077,
         5,
         1432078,
         4,
         1432079,
         3,
         1432080,
         2,
         1432081,
         1,
         1442107,
         5,
         1442108,
         4,
         1442109,
         3,
         1442110,
         2,
         1442111,
         1,
         1452102,
         5,
         1452103,
         4,
         1452104,
         3,
         1452105,
         2,
         1452106,
         1,
         1462087,
         5,
         1462088,
         4,
         1462089,
         3,
         1462090,
         2,
         1462091,
         1,
         1472113,
         5,
         1472114,
         4,
         1472115,
         3,
         1472116,
         2,
         1472117,
         1,
         1482075,
         5,
         1482076,
         4,
         1482077,
         3,
         1482078,
         2,
         1482079,
         1,
         1492075,
         5,
         1492076,
         4,
         1492077,
         3,
         1492078,
         2,
         1492079,
         1,
         1132012,
         2,
         1132013,
         1,
         1942002,
         2,
         1952002,
         2,
         1962002,
         2,
         1972002,
         2,
         1612004,
         2,
         1622004,
         2,
         1632004,
         2,
         1642004,
         2,
         1652004,
         2,
         2047000,
         1,
         2047001,
         1,
         2047002,
         1,
         2047100,
         1,
         2047101,
         1,
         2047102,
         1,
         2047200,
         1,
         2047201,
         1,
         2047202,
         1,
         2047203,
         1,
         2047204,
         1,
         2047205,
         1,
         2047206,
         1,
         2047207,
         1,
         2047208,
         1,
         2047300,
         1,
         2047301,
         1,
         2047302,
         1,
         2047303,
         1,
         2047304,
         1,
         2047305,
         1,
         2047306,
         1,
         2047307,
         1,
         2047308,
         1,
         2047309,
         1,
         2046004,
         1,
         2046005,
         1,
         2046104,
         1,
         2046105,
         1,
         2046208,
         1,
         2046209,
         1,
         2046210,
         1,
         2046211,
         1,
         2046212,
         1,
         2049200,
         1,
         2049201,
         1,
         2049202,
         1,
         2049203,
         1,
         2049204,
         1,
         2049205,
         1,
         2049206,
         1,
         2049207,
         1,
         2049208,
         1,
         2049209,
         1,
         2049210,
         1,
         2049211,
         1,
         1372035,
         1,
         1372036,
         1,
         1372037,
         1,
         1372038,
         1,
         1382045,
         1,
         1382046,
         1,
         1382047,
         1,
         1382048,
         1,
         1382049,
         1,
         1382050,
         1,
         1382051,
         1,
         1382052,
         1,
         1372039,
         1,
         1372040,
         1,
         1372041,
         1,
         1372042,
         1,
         2070016,
         1,
         2070007,
         2,
         2330007,
         1,
         2070018,
         1,
         2330008,
         1,
         2070023,
         1,
         2070024,
         1,
         2028062,
         5,
         2028061,
         5
      };
      eventCommonReward = new int[]{0, 10, 1, 10, 4, 5, 5060004, 25, 4170024, 25, 4280000, 5, 4280001, 6, 5490000, 5, 5490001, 6};
      theSeedBoxReward = new int[]{0, 1, 1, 1, 4310034, 1, 4310014, 1, 4310016, 1, 4001208, 1, 4001547, 1, 4001548, 1, 4001549, 1, 4001550, 1, 4001551, 1};
      eventUncommonReward = new int[]{
         1,
         4,
         2,
         8,
         3,
         8,
         2022179,
         5,
         5062000,
         20,
         2430082,
         20,
         2430092,
         20,
         2022459,
         2,
         2022460,
         1,
         2022462,
         1,
         2430103,
         2,
         2430117,
         2,
         2430118,
         2,
         2430201,
         4,
         2430228,
         4,
         2430229,
         4,
         2430283,
         4,
         2430136,
         4,
         2430476,
         4,
         2430511,
         4,
         2430206,
         4,
         2430199,
         1,
         1032062,
         5,
         5220000,
         28,
         2022459,
         5,
         2022460,
         5,
         2022461,
         5,
         2022462,
         5,
         2022463,
         5,
         5050000,
         2,
         4080100,
         10,
         4080000,
         10,
         2049100,
         10,
         2430144,
         10,
         2290285,
         10,
         2028062,
         10,
         2028061,
         10,
         2530000,
         5,
         2531000,
         5,
         2041100,
         1,
         2041101,
         1,
         2041102,
         1,
         2041103,
         1,
         2041104,
         1,
         2041105,
         1,
         2041106,
         1,
         2041107,
         1,
         2041108,
         1,
         2041109,
         1,
         2041110,
         1,
         2041111,
         1,
         2041112,
         1,
         2041113,
         1,
         2041114,
         1,
         2041115,
         1,
         2041116,
         1,
         2041117,
         1,
         2041118,
         1,
         2041119,
         1,
         2041300,
         1,
         2041301,
         1,
         2041302,
         1,
         2041303,
         1,
         2041304,
         1,
         2041305,
         1,
         2041306,
         1,
         2041307,
         1,
         2041308,
         1,
         2041309,
         1,
         2041310,
         1,
         2041311,
         1,
         2041312,
         1,
         2041313,
         1,
         2041314,
         1,
         2041315,
         1,
         2041316,
         1,
         2041317,
         1,
         2041318,
         1,
         2041319,
         1,
         2049200,
         1,
         2049201,
         1,
         2049202,
         1,
         2049203,
         1,
         2049204,
         1,
         2049205,
         1,
         2049206,
         1,
         2049207,
         1,
         2049208,
         1,
         2049209,
         1,
         2049210,
         1,
         2049211,
         1
      };
      eventRareReward = new int[]{
         2049100,
         5,
         2430144,
         5,
         2290285,
         5,
         2028062,
         5,
         2028061,
         5,
         2530000,
         2,
         2531000,
         2,
         2049116,
         1,
         2049401,
         10,
         2049301,
         20,
         2049400,
         3,
         2340000,
         1,
         3010130,
         5,
         3010131,
         5,
         3010132,
         5,
         3010133,
         5,
         3010136,
         5,
         3010116,
         5,
         3010117,
         5,
         3010118,
         5,
         1112405,
         1,
         1112445,
         1,
         1022097,
         1,
         2040211,
         1,
         2040212,
         1,
         2049000,
         2,
         2049001,
         2,
         2049002,
         2,
         2049003,
         2,
         1012058,
         2,
         1012059,
         2,
         1012060,
         2,
         1012061,
         2,
         2022460,
         4,
         2022461,
         3,
         2022462,
         4,
         2022463,
         3,
         2040041,
         1,
         2040042,
         1,
         2040334,
         1,
         2040430,
         1,
         2040538,
         1,
         2040539,
         1,
         2040630,
         1,
         2040740,
         1,
         2040741,
         1,
         2040742,
         1,
         2040829,
         1,
         2040830,
         1,
         2040936,
         1,
         2041066,
         1,
         2041067,
         1,
         2043023,
         1,
         2043117,
         1,
         2043217,
         1,
         2043312,
         1,
         2043712,
         1,
         2043812,
         1,
         2044025,
         1,
         2044117,
         1,
         2044217,
         1,
         2044317,
         1,
         2044417,
         1,
         2044512,
         1,
         2044612,
         1,
         2044712,
         1,
         2046000,
         1,
         2046001,
         1,
         2046004,
         1,
         2046005,
         1,
         2046100,
         1,
         2046101,
         1,
         2046104,
         1,
         2046105,
         1,
         2046200,
         1,
         2046201,
         1,
         2046202,
         1,
         2046203,
         1,
         2046208,
         1,
         2046209,
         1,
         2046210,
         1,
         2046211,
         1,
         2046212,
         1,
         2046300,
         1,
         2046301,
         1,
         2046302,
         1,
         2046303,
         1,
         2047000,
         1,
         2047001,
         1,
         2047002,
         1,
         2047100,
         1,
         2047101,
         1,
         2047102,
         1,
         2047200,
         1,
         2047201,
         1,
         2047202,
         1,
         2047203,
         1,
         2047204,
         1,
         2047205,
         1,
         2047206,
         1,
         2047207,
         1,
         2047208,
         1,
         2047300,
         1,
         2047301,
         1,
         2047302,
         1,
         2047303,
         1,
         2047304,
         1,
         2047305,
         1,
         2047306,
         1,
         2047307,
         1,
         2047308,
         1,
         2047309,
         1,
         1112427,
         5,
         1112428,
         5,
         1112429,
         5,
         1012240,
         10,
         1022117,
         10,
         1032095,
         10,
         1112659,
         10,
         2070007,
         10,
         2330007,
         5,
         2070016,
         5,
         2070018,
         5,
         1152038,
         1,
         1152039,
         1,
         1152040,
         1,
         1152041,
         1,
         1122090,
         1,
         1122094,
         1,
         1122098,
         1,
         1122102,
         1,
         1012213,
         1,
         1012219,
         1,
         1012225,
         1,
         1012231,
         1,
         1012237,
         1,
         2070023,
         5,
         2070024,
         5,
         2330008,
         5,
         2003516,
         5,
         2003517,
         1,
         1132052,
         1,
         1132062,
         1,
         1132072,
         1,
         1132082,
         1,
         1112585,
         1,
         1072502,
         1,
         1072503,
         1,
         1072504,
         1,
         1072505,
         1,
         1072506,
         1,
         1052333,
         1,
         1052334,
         1,
         1052335,
         1,
         1052336,
         1,
         1052337,
         1,
         1082305,
         1,
         1082306,
         1,
         1082307,
         1,
         1082308,
         1,
         1082309,
         1,
         1003197,
         1,
         1003198,
         1,
         1003199,
         1,
         1003200,
         1,
         1003201,
         1,
         1662000,
         1,
         1662001,
         1,
         1672000,
         1,
         1672001,
         1,
         1672002,
         1,
         1112583,
         1,
         1032092,
         1,
         1132084,
         1,
         2430290,
         1,
         2430292,
         1,
         2430294,
         1,
         2430296,
         1,
         2430298,
         1,
         2430300,
         1,
         2430302,
         1,
         2430304,
         1,
         2430306,
         1,
         2430308,
         1,
         2430310,
         1,
         2430312,
         1,
         2430314,
         1,
         2430316,
         1,
         2430318,
         1,
         2430320,
         1,
         2430322,
         1,
         2430324,
         1,
         2430326,
         1,
         2430328,
         1,
         2430330,
         1,
         2430332,
         1,
         2430334,
         1,
         2430336,
         1,
         2430338,
         1,
         2430340,
         1,
         2430342,
         1,
         2430344,
         1,
         2430347,
         1,
         2430349,
         1,
         2430351,
         1,
         2430353,
         1,
         2430355,
         1,
         2430357,
         1,
         2430359,
         1,
         2430361,
         1,
         2430392,
         1,
         2430512,
         1,
         2430536,
         1,
         2430477,
         1,
         2430146,
         1,
         2430148,
         1,
         2430137,
         1
      };
      eventSuperReward = new int[]{
         2022121,
         10,
         4031307,
         50,
         3010127,
         10,
         3010128,
         10,
         3010137,
         10,
         3010157,
         10,
         2049300,
         10,
         2040758,
         10,
         1442057,
         10,
         2049402,
         10,
         2049304,
         1,
         2049305,
         1,
         2040759,
         7,
         2040760,
         5,
         2040125,
         10,
         2040126,
         10,
         1012191,
         5,
         1112514,
         1,
         1112531,
         1,
         1112629,
         1,
         1112646,
         1,
         1112515,
         1,
         1112532,
         1,
         1112630,
         1,
         1112647,
         1,
         1112516,
         1,
         1112533,
         1,
         1112631,
         1,
         1112648,
         1,
         2040045,
         10,
         2040046,
         10,
         2040333,
         10,
         2040429,
         10,
         2040542,
         10,
         2040543,
         10,
         2040629,
         10,
         2040755,
         10,
         2040756,
         10,
         2040757,
         10,
         2040833,
         10,
         2040834,
         10,
         2041068,
         10,
         2041069,
         10,
         2043022,
         12,
         2043120,
         12,
         2043220,
         12,
         2043313,
         12,
         2043713,
         12,
         2043813,
         12,
         2044028,
         12,
         2044120,
         12,
         2044220,
         12,
         2044320,
         12,
         2044520,
         12,
         2044513,
         12,
         2044613,
         12,
         2044713,
         12,
         2044817,
         12,
         2044910,
         12,
         2046002,
         5,
         2046003,
         5,
         2046102,
         5,
         2046103,
         5,
         2046204,
         10,
         2046205,
         10,
         2046206,
         10,
         2046207,
         10,
         2046304,
         10,
         2046305,
         10,
         2046306,
         10,
         2046307,
         10,
         2040006,
         2,
         2040007,
         2,
         2040303,
         2,
         2040403,
         2,
         2040506,
         2,
         2040507,
         2,
         2040603,
         2,
         2040709,
         2,
         2040710,
         2,
         2040711,
         2,
         2040806,
         2,
         2040903,
         2,
         2040913,
         2,
         2041024,
         2,
         2041025,
         2,
         2044815,
         2,
         2044908,
         2,
         1152046,
         1,
         1152047,
         1,
         1152048,
         1,
         1152049,
         1,
         1122091,
         1,
         1122095,
         1,
         1122099,
         1,
         1122103,
         1,
         1012214,
         1,
         1012220,
         1,
         1012226,
         1,
         1012232,
         1,
         1012238,
         1,
         1032088,
         1,
         1032089,
         1,
         1032090,
         1,
         1032091,
         1,
         1132053,
         1,
         1132063,
         1,
         1132073,
         1,
         1132083,
         1,
         1112586,
         1,
         1112593,
         1,
         1112597,
         1,
         1662002,
         1,
         1662003,
         1,
         1672003,
         1,
         1672004,
         1,
         1672005,
         1,
         1092088,
         1,
         1092089,
         1,
         1092087,
         1,
         1102275,
         1,
         1102276,
         1,
         1102277,
         1,
         1102278,
         1,
         1102279,
         1,
         1102280,
         1,
         1102281,
         1,
         1102282,
         1,
         1102283,
         1,
         1102284,
         1,
         1082295,
         1,
         1082296,
         1,
         1082297,
         1,
         1082298,
         1,
         1082299,
         1,
         1082300,
         1,
         1082301,
         1,
         1082302,
         1,
         1082303,
         1,
         1082304,
         1,
         1072485,
         1,
         1072486,
         1,
         1072487,
         1,
         1072488,
         1,
         1072489,
         1,
         1072490,
         1,
         1072491,
         1,
         1072492,
         1,
         1072493,
         1,
         1072494,
         1,
         1052314,
         1,
         1052315,
         1,
         1052316,
         1,
         1052317,
         1,
         1052318,
         1,
         1052319,
         1,
         1052329,
         1,
         1052321,
         1,
         1052322,
         1,
         1052323,
         1,
         1003172,
         1,
         1003173,
         1,
         1003174,
         1,
         1003175,
         1,
         1003176,
         1,
         1003177,
         1,
         1003178,
         1,
         1003179,
         1,
         1003180,
         1,
         1003181,
         1,
         1302152,
         1,
         1302153,
         1,
         1312065,
         1,
         1312066,
         1,
         1322096,
         1,
         1322097,
         1,
         1332130,
         1,
         1332131,
         1,
         1342035,
         1,
         1342036,
         1,
         1372084,
         1,
         1372085,
         1,
         1382104,
         1,
         1382105,
         1,
         1402095,
         1,
         1402096,
         1,
         1412065,
         1,
         1412066,
         1,
         1422066,
         1,
         1422067,
         1,
         1432086,
         1,
         1432087,
         1,
         1442116,
         1,
         1442117,
         1,
         1452111,
         1,
         1452112,
         1,
         1462099,
         1,
         1462100,
         1,
         1472122,
         1,
         1472123,
         1,
         1482084,
         1,
         1482085,
         1,
         1492085,
         1,
         1492086,
         1,
         1532017,
         1,
         1532018,
         1,
         2430291,
         1,
         2430293,
         1,
         2430295,
         1,
         2430297,
         1,
         2430299,
         1,
         2430301,
         1,
         2430303,
         1,
         2430305,
         1,
         2430307,
         1,
         2430309,
         1,
         2430311,
         1,
         2430313,
         1,
         2430315,
         1,
         2430317,
         1,
         2430319,
         1,
         2430321,
         1,
         2430323,
         1,
         2430325,
         1,
         2430327,
         1,
         2430329,
         1,
         2430331,
         1,
         2430333,
         1,
         2430335,
         1,
         2430337,
         1,
         2430339,
         1,
         2430341,
         1,
         2430343,
         1,
         2430345,
         1,
         2430348,
         1,
         2430350,
         1,
         2430352,
         1,
         2430354,
         1,
         2430356,
         1,
         2430358,
         1,
         2430360,
         1,
         2430362,
         1,
         1012239,
         1,
         1122104,
         1,
         1112584,
         1,
         1032093,
         1,
         1132085,
         1
      };
      tenPercent = new int[]{
         2040002,
         2040005,
         2040026,
         2040031,
         2040100,
         2040105,
         2040200,
         2040205,
         2040302,
         2040310,
         2040318,
         2040323,
         2040328,
         2040329,
         2040330,
         2040331,
         2040402,
         2040412,
         2040419,
         2040422,
         2040427,
         2040502,
         2040505,
         2040514,
         2040517,
         2040534,
         2040602,
         2040612,
         2040619,
         2040622,
         2040627,
         2040702,
         2040705,
         2040708,
         2040727,
         2040802,
         2040805,
         2040816,
         2040825,
         2040902,
         2040915,
         2040920,
         2040925,
         2040928,
         2040933,
         2041002,
         2041005,
         2041008,
         2041011,
         2041014,
         2041017,
         2041020,
         2041023,
         2041058,
         2041102,
         2041105,
         2041108,
         2041111,
         2041302,
         2041305,
         2041308,
         2041311,
         2043002,
         2043008,
         2043019,
         2043102,
         2043114,
         2043202,
         2043214,
         2043302,
         2043402,
         2043702,
         2043802,
         2044002,
         2044014,
         2044015,
         2044102,
         2044114,
         2044202,
         2044214,
         2044302,
         2044314,
         2044402,
         2044414,
         2044502,
         2044602,
         2044702,
         2044802,
         2044809,
         2044902,
         2045302,
         2048002,
         2048005
      };
      fishingReward = new int[]{
         0,
         100,
         1,
         100,
         2022179,
         1,
         1302021,
         5,
         1072238,
         1,
         1072239,
         1,
         2049100,
         2,
         2430144,
         1,
         2290285,
         1,
         2028062,
         1,
         2028061,
         1,
         2049301,
         1,
         2049401,
         1,
         1302000,
         3,
         1442011,
         1,
         4000517,
         8,
         4000518,
         10,
         4031627,
         2,
         4031628,
         1,
         4031630,
         1,
         4031631,
         1,
         4031632,
         1,
         4031633,
         2,
         4031634,
         1,
         4031635,
         1,
         4031636,
         1,
         4031637,
         2,
         4031638,
         2,
         4031639,
         1,
         4031640,
         1,
         4031641,
         2,
         4031642,
         2,
         4031643,
         1,
         4031644,
         1,
         4031645,
         2,
         4031646,
         2,
         4031647,
         1,
         4031648,
         1,
         4001187,
         20,
         4001188,
         20,
         4001189,
         20,
         4031629,
         1
      };
      randomReward = new int[]{
         2000005,
         5,
         2000005,
         10,
         2000004,
         5,
         2000004,
         10,
         2001554,
         5,
         2001554,
         10,
         2001555,
         3,
         2001555,
         5,
         2001556,
         5,
         2001556,
         10,
         2002000,
         3,
         2002000,
         5,
         2002001,
         3,
         2002001,
         5,
         2002002,
         3,
         2002002,
         5,
         2002003,
         3,
         2002003,
         5,
         2002004,
         3,
         2002004,
         5,
         2002005,
         3,
         2002005,
         5
      };
      Equipments_Bonus = new int[]{
         1142096,
         1142097,
         1142098,
         1142099,
         1142329,
         1142442,
         1142443,
         1142444,
         1142569,
         1142107,
         1142108,
         1142109,
         1142110,
         1142242,
         1142243,
         1142244,
         1142245,
         1142246,
         1142247
      };
      Equipments_ReduceCool = new int[]{
         1142098,
         1142099,
         1142329,
         1142442,
         1142443,
         1142444,
         1142569,
         1142107,
         1142108,
         1142109,
         1142110,
         1142242,
         1142243,
         1142244,
         1142245,
         1142246,
         1142247
      };
      if (DBConfig.isGanglim) {
         Equipments_Bonus = new int[]{1142093, 1142094, 1142095, 1142096, 1142097, 1142098, 1142099, 1142329, 1142442, 1142443, 1142444, 1142569};
         Equipments_ReduceCool = new int[]{1142094, 1142095, 1142096, 1142097, 1142098, 1142099, 1142329, 1142442, 1142443, 1142444, 1142569};
      }

      blockedMaps = new int[]{180000001, 180000002, 109050000, 280030000, 240060200, 280090000, 280030001, 240060201, 950101100, 950101010};
      normalDrops = new int[]{
         4001009,
         4001010,
         4001011,
         4001012,
         4001013,
         4001014,
         4001021,
         4001038,
         4001039,
         4001040,
         4001041,
         4001042,
         4001043,
         4001038,
         4001039,
         4001040,
         4001041,
         4001042,
         4001043,
         4001038,
         4001039,
         4001040,
         4001041,
         4001042,
         4001043,
         4000164,
         2000000,
         2000003,
         2000004,
         2000005,
         4000019,
         4000000,
         4000016,
         4000006,
         2100121,
         4000029,
         4000064,
         5110000,
         4000306,
         4032181,
         4006001,
         4006000,
         2050004,
         3994102,
         3994103,
         3994104,
         3994105,
         2430007,
         4000164,
         2000000,
         2000003,
         2000004,
         2000005,
         4000019,
         4000000,
         4000016,
         4000006,
         2100121,
         4000029,
         4000064,
         5110000,
         4000306,
         4032181,
         4006001,
         4006000,
         2050004,
         3994102,
         3994103,
         3994104,
         3994105,
         2430007,
         4000164,
         2000000,
         2000003,
         2000004,
         2000005,
         4000019,
         4000000,
         4000016,
         4000006,
         2100121,
         4000029,
         4000064,
         5110000,
         4000306,
         4032181,
         4006001,
         4006000,
         2050004,
         3994102,
         3994103,
         3994104,
         3994105,
         2430007
      };
      rareDrops = new int[]{2022179, 2049100, 2049100, 2430144, 2028062, 2028061, 2290285, 2049301, 2049401, 2022326, 2022193, 2049000, 2049001, 2049002};
      superDrops = new int[]{2040804, 2049400, 2028062, 2028061, 2430144, 2430144, 2430144, 2430144, 2290285, 2049100, 2049100, 2049100, 2049100};
      owlItems = new int[]{1002357, 1112585, 1032022, 1082002, 5062000, 2049100, 1050018, 1112400, 5062009, 1112748};
      royalstyle = new int[]{
         1042290,
         1042290,
         1003910,
         1003910,
         1003859,
         1003859,
         1012371,
         1012371,
         1022183,
         1022183,
         1112258,
         1112258,
         1112146,
         1112146,
         5065100,
         5065100,
         5281003,
         5281003,
         5030028,
         5030028
      };
      masterpiece = new int[]{5069000, 5069001};
      soulItemid = new int[]{
         2591010,
         2591011,
         2591012,
         2591013,
         2591014,
         2591015,
         2591016,
         2591017,
         2591018,
         2591019,
         2591020,
         2591021,
         2591022,
         2591023,
         2591024,
         2591025,
         2591026,
         2591027,
         2591028,
         2591029,
         2591030,
         2591031,
         2591032,
         2591033,
         2591034,
         2591035,
         2591036,
         2591037,
         2591038,
         2591039,
         2591040,
         2591041,
         2591042,
         2591043,
         2591044,
         2591045,
         2591046,
         2591047,
         2591048,
         2591049,
         2591050,
         2591051,
         2591052,
         2591053,
         2591054,
         2591055,
         2591056,
         2591057,
         2591058,
         2591059,
         2591060,
         2591061,
         2591062,
         2591063,
         2591064,
         2591065,
         2591066,
         2591067,
         2591068,
         2591069,
         2591070,
         2591071,
         2591072,
         2591073,
         2591074,
         2591075,
         2591076,
         2591077,
         2591078,
         2591079,
         2591080,
         2591081,
         2591082,
         2591085,
         2591086,
         2591087,
         2591088,
         2591089,
         2591090,
         2591091,
         2591092,
         2591093,
         2591094,
         2591095,
         2591096,
         2591097,
         2591098,
         2591099,
         2591100,
         2591101,
         2591102,
         2591103,
         2591104,
         2591105,
         2591106,
         2591107,
         2591108,
         2591109,
         2591110,
         2591111,
         2591112,
         2591113,
         2591114,
         2591115,
         2591116,
         2591117,
         2591118,
         2591119,
         2591120,
         2591121,
         2591122,
         2591123,
         2591124,
         2591125,
         2591126,
         2591127,
         2591128,
         2591129,
         2591130,
         2591131,
         2591132,
         2591133,
         2591134,
         2591135,
         2591136,
         2591137,
         2591138,
         2591139,
         2591140,
         2591141,
         2591142,
         2591143,
         2591144,
         2591145,
         2591146,
         2591147,
         2591148,
         2591149,
         2591150,
         2591151,
         2591152,
         2591153,
         2591154,
         2591155,
         2591156,
         2591157,
         2591158,
         2591159,
         2591160,
         2591161,
         2591162,
         2591163,
         2591164,
         2591165,
         2591166,
         2591167,
         2591168,
         2591169,
         2591170,
         2591171,
         2591172,
         2591173,
         2591174,
         2591175,
         2591176,
         2591177,
         2591178,
         2591179,
         2591180,
         2591181,
         2591182,
         2591183,
         2591184,
         2591185,
         2591186,
         2591187,
         2591188,
         2591189,
         2591190,
         2591191,
         2591192,
         2591193,
         2591194,
         2591195,
         2591196,
         2591197,
         2591198,
         2591199,
         2591200,
         2591201,
         2591202,
         2591203,
         2591204,
         2591205,
         2591206,
         2591207,
         2591208,
         2591209,
         2591210,
         2591211,
         2591212,
         2591213,
         2591214,
         2591215,
         2591216,
         2591217,
         2591218,
         2591219,
         2591220,
         2591221,
         2591222,
         2591223,
         2591224,
         2591225,
         2591226,
         2591227,
         2591228,
         2591229,
         2591230,
         2591231,
         2591232,
         2591233,
         2591234,
         2591235,
         2591236,
         2591237,
         2591238,
         2591239,
         2591240,
         2591241,
         2591242,
         2591243,
         2591244,
         2591245,
         2591246,
         2591247,
         2591248,
         2591249,
         2591250,
         2591251,
         2591252,
         2591253,
         2591254,
         2591255,
         2591256,
         2591257,
         2591258,
         2591259,
         2591260,
         2591261,
         2591262,
         2591263,
         2591264,
         2591265,
         2591266,
         2591267,
         2591268,
         2591269,
         2591270,
         2591271,
         2591272,
         2591273,
         2591274,
         2591275,
         2591276,
         2591277,
         2591278,
         2591279,
         2591288,
         2591289,
         2591290,
         2591291,
         2591292,
         2591293,
         2591294,
         2591295,
         2591296,
         2591297,
         2591298,
         2591299,
         2591300,
         2591301,
         2591302,
         2591303,
         2591304,
         2591305,
         2591306,
         2591307,
         2591308,
         2591309,
         2591310,
         2591311,
         2591312,
         2591313,
         2591314,
         2591315,
         2591316,
         2591317,
         2591318,
         2591319,
         2591320,
         2591321,
         2591322,
         2591323,
         2591324,
         2591325,
         2591326,
         2591327,
         2591328,
         2591329,
         2591330,
         2591331,
         2591332,
         2591333,
         2591334,
         2591335,
         2591336,
         2591337,
         2591338,
         2591339,
         2591340,
         2591341,
         2591342,
         2591343,
         2591344,
         2591345,
         2591346,
         2591347,
         2591348,
         2591349,
         2591350,
         2591351,
         2591352,
         2591353,
         2591354,
         2591355,
         2591356,
         2591357,
         2591358,
         2591359,
         2591360,
         2591361,
         2591362,
         2591363,
         2591364,
         2591365,
         2591366,
         2591367,
         2591368,
         2591369,
         2591370,
         2591371,
         2591372,
         2591373,
         2591374,
         2591375,
         2591376,
         2591377,
         2591378,
         2591379,
         2591380,
         2591381
      };
      soulPotentials = new short[]{
         177,
         102,
         103,
         104,
         131,
         132,
         201,
         101,
         102,
         103,
         104,
         131,
         132,
         201,
         105,
         106,
         107,
         108,
         133,
         134,
         202,
         105,
         106,
         107,
         108,
         133,
         134,
         202,
         109,
         110,
         111,
         112,
         135,
         136,
         203,
         113,
         114,
         115,
         116,
         204,
         151,
         152,
         137,
         403,
         603,
         121,
         122,
         123,
         124,
         206,
         155,
         156,
         139,
         403,
         603,
         117,
         118,
         119,
         120,
         207,
         153,
         154,
         138,
         403,
         603,
         167,
         168,
         169,
         170,
         208,
         171,
         172,
         177,
         0,
         0,
         0,
         0,
         101,
         102,
         103,
         104,
         131,
         132,
         201,
         101,
         102,
         103,
         104,
         131,
         132,
         201,
         105,
         106,
         107,
         108,
         133,
         134,
         202,
         105,
         106,
         107,
         108,
         133,
         134,
         202,
         109,
         110,
         111,
         112,
         135,
         136,
         203,
         113,
         114,
         115,
         116,
         204,
         151,
         152,
         137,
         117,
         118,
         119,
         120,
         207,
         153,
         154,
         138,
         121,
         122,
         123,
         124,
         206,
         155,
         156,
         139,
         101,
         102,
         103,
         104,
         131,
         132,
         201,
         163,
         164,
         165,
         166,
         210,
         151,
         152,
         175,
         0,
         101,
         102,
         103,
         104,
         131,
         132,
         201,
         163,
         164,
         165,
         166,
         210,
         151,
         152,
         175,
         167,
         168,
         169,
         170,
         208,
         171,
         172,
         177,
         179,
         180,
         181,
         182,
         183,
         184,
         201,
         185,
         186,
         187,
         188,
         205,
         153,
         154,
         189,
         0,
         179,
         180,
         181,
         182,
         183,
         184,
         201,
         185,
         186,
         187,
         188,
         205,
         153,
         154,
         189,
         109,
         110,
         111,
         112,
         135,
         136,
         203,
         117,
         118,
         119,
         120,
         207,
         153,
         154,
         138,
         0,
         109,
         110,
         111,
         112,
         135,
         136,
         203,
         117,
         118,
         119,
         120,
         205,
         153,
         154,
         138,
         101,
         102,
         103,
         104,
         131,
         132,
         201,
         167,
         168,
         169,
         170,
         208,
         173,
         172,
         177,
         0,
         101,
         102,
         103,
         104,
         131,
         132,
         201,
         167,
         168,
         169,
         170,
         208,
         173,
         172,
         177,
         167,
         168,
         169,
         170,
         208,
         171,
         172,
         177,
         0,
         121,
         186,
         187,
         188,
         205,
         153,
         154,
         189,
         0,
         185,
         186,
         187,
         188,
         207,
         153,
         154,
         189,
         0,
         185,
         186,
         187,
         188,
         205,
         153,
         154,
         189,
         0,
         185,
         186,
         187,
         188,
         207,
         153,
         154,
         189,
         0,
         185,
         186,
         187,
         188,
         206,
         153,
         154,
         189,
         0,
         121,
         186,
         187,
         188,
         205,
         153,
         154,
         189,
         185,
         186,
         187,
         188,
         205,
         153,
         154,
         189,
         185,
         186,
         187,
         188,
         205,
         153,
         154,
         189,
         185,
         186,
         187,
         188,
         207,
         153,
         154,
         189,
         185,
         186,
         187,
         188,
         206,
         153,
         154,
         189
      };
      dmgskinitem = new int[]{
         2431965,
         2438159,
         2431966,
         2431967,
         2432131,
         2438163,
         2438164,
         2438165,
         2438166,
         2438167,
         2438168,
         2438169,
         2438170,
         2438171,
         2438172,
         2438173,
         2438174,
         2438175,
         2438176,
         2438177,
         2438179,
         2438178,
         2438180,
         2438181,
         2438182,
         2438184,
         2438185,
         2438186,
         2438187,
         2438188,
         2438189,
         2438190,
         2438191,
         2438192,
         2438193,
         2438195,
         2438196,
         2438197,
         2438201,
         2438198,
         2438199,
         2438200,
         2438202,
         2438203,
         2438204,
         2438205,
         2438206,
         2438207,
         2438208,
         2438209,
         2438210,
         2438211,
         2438212,
         2438213,
         2438214,
         2438215,
         2438216,
         2438217,
         2438218,
         2438219,
         2438220,
         2438221,
         2438222,
         2438223,
         2438224,
         2438225,
         2438226,
         2438227,
         2438228,
         2438229,
         2438230,
         2438231,
         2438232,
         2438233,
         2438234,
         2438235,
         2438236,
         2438237,
         2438238,
         2438239,
         2438240,
         2438241,
         2438242,
         2438243,
         2438244,
         2438245,
         2438246,
         2438247,
         2438248,
         2438249,
         2438250,
         2438251,
         2438252,
         2438253,
         2438254,
         2438255,
         2438256,
         2438257,
         2438258,
         2438259,
         2438260,
         2438261,
         2438262,
         2438263,
         2438264,
         2438265,
         2438266,
         2438267,
         2438268,
         2438269,
         2438270,
         2438271,
         2438272,
         2438273,
         2438274,
         2438275,
         2438276,
         2438277,
         2438278,
         2438279,
         2438280,
         2438281,
         2438282,
         2438283,
         2438284,
         2438285,
         2438286,
         2438287,
         2438288,
         2438289,
         2438290,
         2438291,
         2438292,
         2438293,
         2438294,
         2438295,
         2438296,
         2438297,
         2438298,
         2438299,
         2438300,
         2438301,
         2438302,
         2438303,
         2438304,
         2438305,
         2438306,
         2438307,
         2438308,
         2438309,
         2438310,
         2438311,
         2438312,
         2438313,
         2438314,
         2438315,
         2438353,
         2438378,
         2438379,
         2438413,
         2438415,
         2438417,
         2438419,
         2438485,
         2438492,
         2438530,
         2438637,
         2438672,
         2438713,
         2438871,
         2438881,
         2438885,
         2439298,
         2439336,
         2439337,
         2439338,
         2439381,
         2439393,
         2439395,
         2439408,
         2439572,
         2439617,
         2439652,
         2439684,
         2439686,
         2439769,
         2439925,
         2439927,
         2630137,
         2630222,
         2630178,
         2630214,
         2630380,
         2630235,
         2630224,
         2630262,
         2630264,
         2630266,
         2630385,
         2630400,
         2630434,
         2630436,
         2439397,
         2439399,
         2630268,
         2439682,
         2439401,
         2438149,
         2438151,
         2630477,
         2630479,
         2630481,
         2630483,
         2630485,
         2630516,
         2630552,
         2630554,
         2630556,
         2630558,
         2630560,
         2630652,
         2630743,
         2630745,
         2630747,
         2630749,
         2630751,
         2630753,
         2630804,
         2630969,
         2631094,
         2631095,
         2631098,
         2631135,
         2631189,
         2631183,
         2631401,
         2631451,
         2631471,
         2631492,
         2631610,
         2438147,
         2631892,
         2631893,
         2631884,
         2631885,
         2631815,
         2631798,
         2632124,
         2632281,
         2632288,
         2632350,
         2632430,
         2632544,
         2632498,
         2632712,
         2632816,
         5680862,
         2632888,
         2632976,
         2633045,
         2633047,
         2633074,
         2633218,
         2633220,
         2633306,
         2633313,
         2633599,
         2438872,
         2435516,
         2633995,
         2634020,
         2631097,
         2634176,
         2634251,
         2634259,
         2634268,
         2634277,
         2634279,
         2634416,
         2634513,
         2634640,
         2634728,
         2634811,
         2634906,
         2634941,
         2633557,
         2633573,
         2633700,
         2635056,
         2635128,
         2635233,
         2635408,
         2635469,
         5681000,
         5681001,
         2635516,
         2635535,
         2635529,
         2635633,
         2635689,
         2635794,
         2635782,
         2635914,
         2635968,
         2635970,
         2635996,
         2635966,
         2635972,
         2636151
      };
      dmgskinnum = new int[]{
         0,
         0,
         1,
         2,
         3,
         4,
         5,
         6,
         7,
         8,
         9,
         10,
         11,
         12,
         13,
         14,
         15,
         16,
         17,
         18,
         19,
         20,
         22,
         23,
         24,
         26,
         27,
         28,
         29,
         34,
         35,
         36,
         37,
         38,
         39,
         41,
         42,
         43,
         47,
         44,
         45,
         46,
         48,
         49,
         50,
         51,
         52,
         53,
         74,
         75,
         76,
         77,
         78,
         79,
         80,
         81,
         82,
         83,
         84,
         85,
         86,
         87,
         88,
         89,
         90,
         91,
         92,
         93,
         94,
         95,
         96,
         97,
         98,
         99,
         100,
         101,
         102,
         103,
         104,
         105,
         106,
         107,
         108,
         109,
         110,
         111,
         112,
         113,
         114,
         120,
         121,
         122,
         123,
         124,
         125,
         126,
         127,
         128,
         129,
         130,
         131,
         132,
         133,
         134,
         135,
         136,
         137,
         138,
         139,
         140,
         141,
         143,
         144,
         145,
         146,
         147,
         148,
         149,
         150,
         152,
         153,
         154,
         155,
         156,
         157,
         158,
         159,
         161,
         162,
         163,
         164,
         165,
         166,
         167,
         168,
         169,
         170,
         171,
         172,
         173,
         174,
         175,
         176,
         177,
         178,
         179,
         180,
         181,
         182,
         183,
         184,
         185,
         186,
         187,
         188,
         189,
         193,
         194,
         195,
         196,
         197,
         198,
         199,
         200,
         201,
         202,
         203,
         204,
         205,
         208,
         206,
         207,
         209,
         210,
         211,
         212,
         213,
         214,
         215,
         216,
         217,
         218,
         219,
         221,
         222,
         223,
         224,
         225,
         227,
         228,
         229,
         230,
         231,
         232,
         233,
         234,
         235,
         236,
         237,
         238,
         239,
         240,
         1010,
         1017,
         1030,
         1290,
         1322,
         1287,
         1302,
         241,
         242,
         243,
         244,
         245,
         1343,
         246,
         247,
         248,
         249,
         250,
         251,
         252,
         253,
         254,
         255,
         256,
         257,
         258,
         259,
         260,
         261,
         262,
         263,
         264,
         265,
         266,
         267,
         268,
         269,
         270,
         192,
         276,
         276,
         275,
         275,
         274,
         273,
         277,
         278,
         279,
         280,
         281,
         282,
         283,
         284,
         286,
         285,
         287,
         288,
         289,
         290,
         291,
         292,
         293,
         294,
         295,
         298,
         208,
         115,
         301,
         302,
         262,
         304,
         305,
         306,
         307,
         308,
         309,
         310,
         311,
         312,
         313,
         314,
         315,
         316,
         296,
         297,
         299,
         317,
         318,
         319,
         320,
         321,
         322,
         323,
         324,
         325,
         326,
         327,
         328,
         329,
         330,
         331,
         332,
         333,
         334,
         335,
         336,
         337
      };
      isOpen = false;
      weaponTypes = new int[]{
         30, 31, 32, 33, 37, 38, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 39, 34, 52, 53, 35, 36, 21, 22, 23, 24, 56, 57, 26, 58, 27, 28, 59, 29
      };
      intensePowerCrystal = new HashMap<>();
      if (DBConfig.isGanglim) {
         intensePowerCrystal.put(8800002, new IntensePowerCrystalData(8800002, 8800002, 9210001, 5000000L));
         intensePowerCrystal.put(8800102, new IntensePowerCrystalData(8800102, 8800102, 9210002, 10000000L));
         intensePowerCrystal.put(8810018, new IntensePowerCrystalData(8810018, 8810018, 9210021, 5000000L));
         intensePowerCrystal.put(8810122, new IntensePowerCrystalData(8810122, 8810122, 9210022, 10000000L));
         intensePowerCrystal.put(8840014, new IntensePowerCrystalData(8840014, 8840014, 9210019, 10000000L));
         intensePowerCrystal.put(8860000, new IntensePowerCrystalData(8860000, 8860000, 9210024, 15000000L));
         intensePowerCrystal.put(8820001, new IntensePowerCrystalData(8820001, 8820001, 9210025, 15000000L));
         intensePowerCrystal.put(8880002, new IntensePowerCrystalData(8880002, 8880002, 9210004, 15000000L));
         intensePowerCrystal.put(8880000, new IntensePowerCrystalData(8880000, 8880000, 9210005, 37500000L));
         intensePowerCrystal.put(8900103, new IntensePowerCrystalData(8900100, 8900103, 9210009, 7500000L));
         intensePowerCrystal.put(8900003, new IntensePowerCrystalData(8900000, 8900003, 9210010, 32500000L));
         intensePowerCrystal.put(8910100, new IntensePowerCrystalData(8910100, 8910100, 9210011, 7500000L));
         intensePowerCrystal.put(8910000, new IntensePowerCrystalData(8910000, 8910000, 9210012, 32500000L));
         intensePowerCrystal.put(8920106, new IntensePowerCrystalData(8920100, 8920106, 9210013, 7500000L));
         intensePowerCrystal.put(8920006, new IntensePowerCrystalData(8920000, 8920006, 9210014, 32500000L));
         intensePowerCrystal.put(8930100, new IntensePowerCrystalData(8930100, 8930100, 9210015, 9000000L));
         intensePowerCrystal.put(8930000, new IntensePowerCrystalData(8930000, 8930000, 9210016, 42500000L));
         intensePowerCrystal.put(8500012, new IntensePowerCrystalData(8500012, 8500012, 9210036, 15000000L));
         intensePowerCrystal.put(8500022, new IntensePowerCrystalData(8500022, 8500022, 9210037, 47500000L));
         intensePowerCrystal.put(8950109, new IntensePowerCrystalData(8950102, 8950109, 9210029, 62500000L));
         intensePowerCrystal.put(8950110, new IntensePowerCrystalData(8950002, 8950110, 9210030, 125000000L));
         intensePowerCrystal.put(8950111, new IntensePowerCrystalData(8880111, 8950111, 9210031, 62500000L));
         intensePowerCrystal.put(8950112, new IntensePowerCrystalData(8880101, 8950112, 9210032, 125000000L));
         intensePowerCrystal.put(8880167, new IntensePowerCrystalData(8880150, 8880167, 9210033, 75000000L));
         intensePowerCrystal.put(8880177, new IntensePowerCrystalData(8880153, 8880177, 9210034, 175000000L));
         intensePowerCrystal.put(8950114, new IntensePowerCrystalData(8880342, 8950114, 9210038, 85000000L));
         intensePowerCrystal.put(8950115, new IntensePowerCrystalData(8880302, 8950115, 9210039, 400000000L));
         intensePowerCrystal.put(8950118, new IntensePowerCrystalData(8645009, 8950118, 9210043, 150000000L));
         intensePowerCrystal.put(8950119, new IntensePowerCrystalData(8645066, 8950119, 9210047, 450000000L));
         intensePowerCrystal.put(8950116, new IntensePowerCrystalData(8644650, 8950116, 9210042, 150000000L));
         intensePowerCrystal.put(8950117, new IntensePowerCrystalData(8644655, 8950117, 9210046, 600000000L));
         intensePowerCrystal.put(8880726, new IntensePowerCrystalData(8880700, 8880726, 9210052, 150000000L));
         intensePowerCrystal.put(8880725, new IntensePowerCrystalData(8880711, 8880725, 9210051, 600000000L));
         intensePowerCrystal.put(8950121, new IntensePowerCrystalData(8880410, 8950121, 9210040, 750000000L));
         intensePowerCrystal.put(8880518, new IntensePowerCrystalData(8880502, 8880518, 9210041, 900000000L));
         intensePowerCrystal.put(8880614, new IntensePowerCrystalData(8880600, 8880614, 9210050, 1000000000L));
      } else {
         intensePowerCrystal.put(8800002, new IntensePowerCrystalData(8800002, 8800002, 9210001, 306250L));
         intensePowerCrystal.put(8800102, new IntensePowerCrystalData(8800102, 8800102, 9210002, 8100000L));
         intensePowerCrystal.put(8880010, new IntensePowerCrystalData(8880010, 8880010, 9210004, 1296000L));
         intensePowerCrystal.put(8880000, new IntensePowerCrystalData(8880000, 8880000, 9210005, 9506250L));
         intensePowerCrystal.put(8900103, new IntensePowerCrystalData(8900100, 8900103, 9210009, 484300L));
         intensePowerCrystal.put(8900003, new IntensePowerCrystalData(8900000, 8900003, 9210010, 8100000L));
         intensePowerCrystal.put(8910100, new IntensePowerCrystalData(8910100, 8910100, 9210011, 484300L));
         intensePowerCrystal.put(8910000, new IntensePowerCrystalData(8910000, 8910000, 9210012, 8100000L));
         intensePowerCrystal.put(8920106, new IntensePowerCrystalData(8920100, 8920106, 9210013, 484300L));
         intensePowerCrystal.put(8920006, new IntensePowerCrystalData(8920000, 8920006, 9210014, 8100000L));
         intensePowerCrystal.put(8930100, new IntensePowerCrystalData(8930100, 8930100, 9210015, 484300L));
         intensePowerCrystal.put(8930000, new IntensePowerCrystalData(8930000, 8930000, 9210016, 10506250L));
         intensePowerCrystal.put(8840007, new IntensePowerCrystalData(8840007, 8840007, 9210017, 529000L));
         intensePowerCrystal.put(8840000, new IntensePowerCrystalData(8840000, 8840000, 9210018, 729000L));
         intensePowerCrystal.put(8840014, new IntensePowerCrystalData(8840014, 8840014, 9210019, 1225000L));
         intensePowerCrystal.put(8860005, new IntensePowerCrystalData(8860005, 8860005, 9210023, 576000L));
         intensePowerCrystal.put(8860000, new IntensePowerCrystalData(8860000, 8860000, 9210024, 1260000L));
         intensePowerCrystal.put(8850111, new IntensePowerCrystalData(8850111, 8850111, 9210027, 4556250L));
         intensePowerCrystal.put(8850011, new IntensePowerCrystalData(8850011, 8850011, 9210028, 7225000L));
         intensePowerCrystal.put(8950109, new IntensePowerCrystalData(8950102, 8950109, 9210029, 16256000L));
         intensePowerCrystal.put(8950110, new IntensePowerCrystalData(8950002, 8950110, 9210030, 37056250L));
         intensePowerCrystal.put(8950111, new IntensePowerCrystalData(8880111, 8950111, 9210031, 16900000L));
         intensePowerCrystal.put(8950112, new IntensePowerCrystalData(8880101, 8950112, 9210032, 35156250L));
         intensePowerCrystal.put(8880167, new IntensePowerCrystalData(8880150, 8880167, 9210033, 20306250L));
         intensePowerCrystal.put(8880177, new IntensePowerCrystalData(8880153, 8880177, 9210034, 40000000L));
         intensePowerCrystal.put(8500002, new IntensePowerCrystalData(8500002, 8500002, 9210035, 342250L));
         intensePowerCrystal.put(8500012, new IntensePowerCrystalData(8500012, 8500012, 9210036, 1332250L));
         intensePowerCrystal.put(8500022, new IntensePowerCrystalData(8500022, 8500022, 9210037, 12100000L));
         intensePowerCrystal.put(8950113, new IntensePowerCrystalData(8880362, 8950113, 9210053, 18256250L));
         intensePowerCrystal.put(8950114, new IntensePowerCrystalData(8880342, 8950114, 9210038, 23256250L));
         intensePowerCrystal.put(8950115, new IntensePowerCrystalData(8880302, 8950115, 9210039, 44100000L));
         intensePowerCrystal.put(8880518, new IntensePowerCrystalData(8880502, 8880518, 9210041, 122500000L));
         intensePowerCrystal.put(8950116, new IntensePowerCrystalData(8644650, 8950116, 9210042, 24806250L));
         intensePowerCrystal.put(8950117, new IntensePowerCrystalData(8644655, 8950117, 9210046, 46225000L));
         intensePowerCrystal.put(8950118, new IntensePowerCrystalData(8645009, 8950118, 9210043, 26406250L));
         intensePowerCrystal.put(8950119, new IntensePowerCrystalData(8645066, 8950119, 9210047, 49000000L));
         intensePowerCrystal.put(8880614, new IntensePowerCrystalData(8880600, 8880614, 9210050, 75625000L));
         intensePowerCrystal.put(8880725, new IntensePowerCrystalData(8880711, 8880725, 9210051, 60606991L));
         intensePowerCrystal.put(8880726, new IntensePowerCrystalData(8880700, 8880726, 9210052, 20710932L));
         intensePowerCrystal.put(8880644, new IntensePowerCrystalData(8880630, 8880644, 9210054, 50000000L));
         intensePowerCrystal.put(8950120, new IntensePowerCrystalData(8880405, 8950120, 9210055, 50225000L));
         intensePowerCrystal.put(8950121, new IntensePowerCrystalData(8880410, 8950121, 9210040, 55225000L));
      }

      bmWeapons = new int[]{
         1212128,
         1213021,
         1222121,
         1232121,
         1242138,
         1242140,
         1262050,
         1272039,
         1282039,
         1292021,
         1302354,
         1312212,
         1322263,
         1332288,
         1362148,
         1372236,
         1382273,
         1402267,
         1412188,
         1422196,
         1432226,
         1442284,
         1452265,
         1462251,
         1472274,
         1482231,
         1492244,
         1522151,
         1532156,
         1582043,
         1592021,
         1214021,
         1562010,
         1404021
      };
      MaxNeoCore = 5000;
   }
}
