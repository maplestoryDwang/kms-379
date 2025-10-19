package objects.users;

import constants.GameConstants;
import constants.HexaMatrixConstants;
import database.DBConfig;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import network.SendPacketOpcode;
import network.center.Center;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import objects.context.guild.Guild;
import objects.context.guild.GuildSkill;
import objects.effect.child.EffectReserved;
import objects.effect.child.SpecialSkillEffect;
import objects.fields.child.union.MapleUnion;
import objects.fields.gameobject.lifes.Element;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.item.MapleWeaponType;
import objects.item.StructSetItem;
import objects.quest.MapleQuestStatus;
import objects.summoned.Summoned;
import objects.users.enchant.ItemOptionLevelData;
import objects.users.enchant.ItemStateFlag;
import objects.users.extra.ExtraAbilityOption;
import objects.users.extra.ExtraAbilityStatEntry;
import objects.users.looks.zero.ZeroInfo;
import objects.users.potential.CharacterPotentialHolder;
import objects.users.skills.IndieTemporaryStatEntry;
import objects.users.skills.LinkSkillEntry;
import objects.users.skills.Skill;
import objects.users.skills.SkillEntry;
import objects.users.skills.SkillFactory;
import objects.users.stats.HexaCore;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Triple;

public class PlayerStats implements Serializable {
   private static final long serialVersionUID = -679541993413738569L;
   private Map<Integer, Integer> setHandling = new HashMap<>();
   private Map<Integer, Integer> skillsIncrement = new HashMap<>();
   private Map<Integer, Integer> damageIncrease = new HashMap<>();
   private EnumMap<Element, Integer> elemBoosts = new EnumMap<>(Element.class);
   private transient WeakReference<MapleCharacter> chr;
   private transient Map<Integer, Integer> demonForce = new HashMap<>();
   private List<Equip> durabilityHandling = new ArrayList<>();
   private List<Equip> equipLevelHandling = new ArrayList<>();
   private transient float shouldHealHP;
   private transient float shouldHealMP;
   public short str;
   public short dex;
   public short luk;
   public short int_;
   private int starforce;
   private int authenticforce;
   private int arcaneforce;
   public int bufftimeR;
   public int summonTimeR;
   public long hp;
   public long secondHp;
   public long maxhp;
   public long secondMaxHp;
   public long mp;
   public long maxmp;
   public transient short passive_sharpeye_min_percent;
   public transient short passive_sharpeye_percent;
   public transient short passive_sharpeye_rate;
   private transient byte passive_mastery;
   private transient int localstr;
   private transient int localdex;
   private transient int localluk;
   private transient int localint_;
   private transient long localmaxhp;
   private transient long localmaxmp;
   private transient long localSubMaxHp;
   private transient int subPercentHp;
   private transient int magic;
   private transient int watk;
   private transient int hands;
   private transient int accuracy;
   public transient boolean equippedWelcomeBackRing;
   public transient boolean hasClone;
   public transient boolean hasPartyBonus;
   public transient boolean Berserk;
   public transient double plusExpRate;
   public transient double plusExp;
   public transient double expBuff;
   public transient double extraDropBuff;
   public transient double dropBuff;
   public transient double dropBuff_;
   public transient double dropBuff__;
   public transient double mesoBuff;
   public transient double mesoBuff_;
   public transient double itemMesoBuff;
   public transient double mesoBuff__;
   public transient double cashBuff;
   public transient double expMod;
   public transient double pickupRange;
   public transient int reviveInvincibleTime;
   public transient int incExtraMobGegen;
   public transient double dam_r;
   public transient double bossdam_r;
   public transient int recoverHP;
   public transient int recoverMP;
   public transient int mpconReduce;
   public transient int mpconPercent;
   public transient int incMesoProp;
   public transient int incRewardProp;
   public transient int s_reduceCooltimeR;
   public transient int reduceCooltime;
   public transient int reduceCooltimeR;
   public transient int coolTimeR;
   public transient int suddenDeathR;
   public transient int expLossReduceR;
   public transient int DAMreflect;
   public transient int DAMreflect_rate;
   public transient int ignoreDAMr;
   public transient int ignoreDAMr_rate;
   public transient int ignoreDAM;
   public transient int ignoreDAM_rate;
   public transient int mpRestore;
   public transient int hpRecover;
   public transient int hpRecoverProp;
   public transient int hpRecoverPercent;
   public transient int mpRecover;
   public transient int mpRecoverProp;
   public transient int RecoveryUP;
   public transient int BuffUP;
   public transient int RecoveryUP_Skill;
   public transient int BuffUP_Skill;
   public transient int incAllskill;
   public transient int combatOrders;
   public transient int passivePlus;
   public transient int ignoreTargetDEF;
   public transient int defRange;
   public transient int BuffUP_Summon;
   public transient int dodgeChance;
   public transient int speed;
   public transient int jump;
   public transient int harvestingTool;
   public transient int evaR;
   public transient int equipmentBonusExp;
   public transient int dropMod;
   public transient int cashMod;
   public transient int levelBonus;
   public transient int ASR;
   public transient int TER;
   public transient int pickRate;
   public transient int decreaseDebuff;
   public transient int equippedFairy;
   public transient int equippedSummon;
   public transient int percent_hp;
   public transient int percent_mp;
   public transient int percent_str;
   public transient int percent_dex;
   public transient int percent_int;
   public transient int percent_luk;
   public transient int percent_acc;
   public transient int percent_atk;
   public transient int percent_matk;
   public transient int percent_wdef;
   public transient int percent_mdef;
   public transient int expGuild = 0;
   public transient int itemTUCProtectR = 0;
   public transient int itemCursedProtectR = 0;
   public transient int itemUpgradeBonusR = 0;
   public transient int disCountR = 0;
   public transient int mesoG = 0;
   public transient int pvpDamage;
   public transient int hpRecoverTime = 0;
   public transient int mpRecoverTime = 0;
   public transient int dot;
   public transient int dotTime;
   public transient int questBonus;
   public transient int pvpRank;
   public transient int pvpExp;
   public transient int wdef;
   public transient int mdef;
   public transient int trueMastery;
   public transient int damX;
   public transient int DAMreduceR;
   private transient float localmaxbasedamage;
   private transient float localmaxbasepvpdamage;
   private transient float localmaxbasepvpdamageL;
   public transient int def;
   public transient int element_ice;
   public transient int element_fire;
   public transient int element_light;
   public transient int element_psn;
   public transient int monsterCollectionR;
   private double sword;
   private double blunt;
   private double axe;
   private double spear;
   private double polearm;
   private double claw;
   private double dagger;
   private double staffwand;
   private double crossbow;
   private double bow;
   private int skill = 0;
   private Skill skil;
   private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
   private static final int[] allJobs = new int[] { 0, 10000, 10000000, 20000000, 20010000, 20020000, 30000000,
         30010000 };
   public static final int[] pvpSkills = new int[] {
         1000007,
         2000007,
         3000006,
         4000010,
         5000006,
         5010004,
         11000006,
         12000006,
         13000005,
         14000006,
         15000005,
         21000005,
         22000002,
         23000004,
         31000005,
         32000012,
         33000004,
         35000005
   };

   public final void init(MapleCharacter chra) {
      this.recalcLocalStats(chra);
   }

   public final short getStr() {
      return this.str;
   }

   public final short getDex() {
      return this.dex;
   }

   public final short getLuk() {
      return this.luk;
   }

   public final short getInt() {
      return this.int_;
   }

   public final int getStarForce() {
      return this.starforce;
   }

   public final int getAuthenticForce() {
      return this.authenticforce;
   }

   public final int getArcaneForce() {
      return this.arcaneforce;
   }

   public final void setStr(short str, MapleCharacter chra) {
      this.str = str;
      this.recalcLocalStats(chra);
   }

   public final void setDex(short dex, MapleCharacter chra) {
      this.dex = dex;
      this.recalcLocalStats(chra);
   }

   public final void setLuk(short luk, MapleCharacter chra) {
      this.luk = luk;
      this.recalcLocalStats(chra);
   }

   public final void setInt(short int_, MapleCharacter chra) {
      this.int_ = int_;
      this.recalcLocalStats(chra);
   }

   public final boolean setHp(long newhp, MapleCharacter chra) {
      return this.setHp(newhp, false, chra);
   }

   public final boolean setHp(long newhp, boolean silent, MapleCharacter chra) {
      return this.setHp(newhp, silent, chra, true);
   }

   public final boolean setHp(long newhp, boolean silent, MapleCharacter chra, boolean bmDecrementDC) {
      long oldHp = this.hp;
      long thp = newhp;
      if (newhp < 0L) {
         thp = 0L;
      }

      if (thp > chra.getStat().getCurrentMaxHp(chra)) {
         thp = chra.getStat().getCurrentMaxHp(chra);
      }

      this.hp = thp;
      if (this.hp > 0L && chra.getInventory(MapleInventoryType.EQUIPPED).findById(1113123) != null
            && chra.getRemainCooltime(80000299) <= 0L) {
         Equip equip = (Equip) chra.getInventory(MapleInventoryType.EQUIPPED).findById(1113123);
         SecondaryStatEffect effect = SkillFactory.getSkill(80000299).getEffect(equip.getTheSeedRingLevel());
         if (this.hp <= (int) (chra.getStat().getCurrentMaxHp(chra) * (effect.getX() / 100.0))) {
            chra.send(CField.skillCooldown(80000299, 180000));
            chra.addCooldown(80000299, System.currentTimeMillis(), 180000L);
            chra.temporaryStatSet(SecondaryStatFlag.indiePddR, 80000299, effect.getDuration(),
                  equip.getTheSeedRingLevel(), effect.getY(), false);
         }
      }

      if (this.hp > 0L && this.mp > 0L && chra.getInventory(MapleInventoryType.EQUIPPED).findById(1113125) != null
            && chra.getRemainCooltime(80000301) <= 0L) {
         Equip equip = (Equip) chra.getInventory(MapleInventoryType.EQUIPPED).findById(1113125);
         SecondaryStatEffect effect = SkillFactory.getSkill(80000301).getEffect(equip.getTheSeedRingLevel());
         if (this.mp <= (int) (this.localmaxmp * (effect.getX() / 100.0))
               && this.hp <= (int) (this.localmaxhp * (effect.getX() / 100.0))) {
            chra.send(CField.skillCooldown(80000301, 180000));
            chra.addCooldown(80000301, System.currentTimeMillis(), 180000L);
            effect.applyTo(chra);
         }
      }

      if (chra != null) {
         if (!silent) {
            chra.updatePartyMemberHP();
         }

         if (oldHp > this.hp && !chra.isAlive()) {
            if (GameConstants.isZero(chra.getJob())) {
               if (chra.getBuffedValue(SecondaryStatFlag.PreReviveOnce) != null) {
                  this.hp = (int) (chra.getBuffedValue(SecondaryStatFlag.PreReviveOnce).intValue()
                        * (this.getCurrentMaxHp(chra) * 0.01));
                  this.mp = (int) (chra.getBuffedValue(SecondaryStatFlag.PreReviveOnce).intValue()
                        * (this.getCurrentMaxMp(chra) * 0.01));
                  chra.updateSingleStat(MapleStat.HP, this.hp);
                  chra.updateSingleStat(MapleStat.MP, this.mp);
                  SecondaryStatEffect e = chra.getBuffedEffect(SecondaryStatFlag.PreReviveOnce);
                  SpecialSkillEffect eff = new SpecialSkillEffect(chra.getId(), e.getSourceId(), null);
                  chra.send(eff.encodeForLocal());
                  chra.getMap().broadcastMessage(chra, eff.encodeForRemote(), false);
                  chra.temporaryStatReset(SecondaryStatFlag.PreReviveOnce);
                  if (e != null) {
                     chra.temporaryStatSet(SecondaryStatFlag.indiePartialNotDamaged, 100001272, e.getY() * 1000, 1);
                  }
               } else {
                  chra.playerDead(bmDecrementDC);
               }
            } else if (chra.getBuffedValue(SecondaryStatFlag.LotusFlower) != null) {
               Summoned summoned = chra.getSummonBySkillID(400001061);
               if (summoned != null) {
                  SecondaryStatEffect level = chra.getSkillLevelData(400001061);
                  if (level != null) {
                     boolean check = false;

                     for (MapleCharacter player : chra.getMap()
                           .getPlayerInRect(summoned.getTruePosition(), level.getLt().x, level.getLt().y,
                                 level.getRb().x, level.getRb().y)) {
                        if (player.getId() == chra.getId()) {
                           check = true;
                           break;
                        }
                     }

                     if (check) {
                        long hp = this.getCurrentMaxHp(chra);
                        this.hp = hp;
                        chra.updateSingleStat(MapleStat.HP, this.hp);
                        PacketEncoder packet = new PacketEncoder();
                        packet.writeShort(SendPacketOpcode.SUMMON_LOTUS_FLOWER_ACTION.getValue());
                        packet.writeInt(chra.getId());
                        packet.writeInt(summoned.getObjectId());
                        chra.send(packet.getPacket());
                        chra.temporaryStatResetBySkillID(400001062);
                        chra.temporaryStatSet(SecondaryStatFlag.indiePartialNotDamaged, 400001061, level.getX(), 1);
                        long remain = chra.getRemainCooltime(400001061);
                        if (level != null) {
                           chra.changeCooldown(400001061, remain + level.getW() * 1000);
                        }
                     }
                  }
               }
            } else if (chra.getBuffedValue(SecondaryStatFlag.PreReviveOnce) != null && Randomizer.isSuccess(10)) {
               int after = (int) (this.localmaxhp * 0.1);
               this.hp = after;
               this.mp = (int) (this.localmaxmp * 0.1);
               chra.updateSingleStat(MapleStat.HP, this.hp);
               chra.updateSingleStat(MapleStat.MP, this.mp);
               chra.dropMessage(5, "구사 일생의 효과로 죽음의 운명을 회피했습니다.");
               chra.temporaryStatReset(SecondaryStatFlag.PreReviveOnce);
            } else {
               chra.playerDead(bmDecrementDC);
            }
         }
      }

      if (GameConstants.isDemonAvenger(chra.getJob())) {
         chra.invokeJobMethod("initDemonAvenger");
      }

      return this.hp != oldHp;
   }

   public final boolean setMp(long newmp, MapleCharacter chra) {
      long oldMp = this.mp;
      long tmp = newmp;
      if (newmp < 0L) {
         tmp = 0L;
      }

      if (tmp > this.localmaxmp) {
         tmp = this.localmaxmp;
      }

      if (this.mp > 0L && chra.getInventory(MapleInventoryType.EQUIPPED).findById(1113124) != null
            && chra.getRemainCooltime(80000300) <= 0L) {
         Equip equip = (Equip) chra.getInventory(MapleInventoryType.EQUIPPED).findById(1113124);
         SecondaryStatEffect effect = SkillFactory.getSkill(80000300).getEffect(equip.getTheSeedRingLevel());
         if (this.mp <= (int) (this.localmaxmp * (effect.getX() / 100.0))) {
            chra.send(CField.skillCooldown(80000300, 180000));
            chra.addCooldown(80000300, System.currentTimeMillis(), 180000L);
            chra.temporaryStatSet(SecondaryStatFlag.indiePddR, 80000300, effect.getDuration(),
                  equip.getTheSeedRingLevel(), effect.getY(), false);
         }
      }

      if (this.hp > 0L && this.mp > 0L && chra.getInventory(MapleInventoryType.EQUIPPED).findById(1113125) != null
            && chra.getRemainCooltime(80000301) <= 0L) {
         Equip equip = (Equip) chra.getInventory(MapleInventoryType.EQUIPPED).findById(1113125);
         SecondaryStatEffect effect = SkillFactory.getSkill(80000301).getEffect(equip.getTheSeedRingLevel());
         if (this.mp <= (int) (this.localmaxmp * (effect.getX() / 100.0))
               && this.hp <= (int) (this.localmaxhp * (effect.getX() / 100.0))) {
            chra.send(CField.skillCooldown(80000301, 180000));
            chra.addCooldown(80000301, System.currentTimeMillis(), 180000L);
            effect.applyTo(chra);
         }
      }

      this.mp = tmp;
      return this.mp != oldMp;
   }

   public final void setInfo(long maxhp, long maxmp, long hp, long mp) {
      this.maxhp = maxhp;
      this.maxmp = maxmp;
      this.hp = hp;
      this.mp = mp;
   }

   public final void setMaxHp(long hp, MapleCharacter chra) {
      this.maxhp = hp;
      this.recalcLocalStats(chra);
   }

   public final void setMaxHp(long hp) {
      this.maxhp = hp;
   }

   public final void setMaxMp(long mp) {
      this.maxmp = mp;
   }

   public final void setMaxMp(long mp, MapleCharacter chra) {
      this.maxmp = mp;
      this.recalcLocalStats(chra);
   }

   public final long getHp() {
      return this.hp;
   }

   public final long getSecondHp() {
      return this.secondHp;
   }

   public final long getMaxHp() {
      return this.maxhp;
   }

   public final long getMp() {
      return this.mp;
   }

   public final long getMaxMp() {
      return this.maxmp;
   }

   public final int getTotalDex() {
      return this.localdex;
   }

   public final int getTotalInt() {
      return this.localint_;
   }

   public final int getTotalStr() {
      return this.localstr;
   }

   public final int getTotalLuk() {
      return this.localluk;
   }

   public final int getTotalMagic() {
      return this.magic;
   }

   public final int getSpeed() {
      return this.speed;
   }

   public final int getJump() {
      return this.jump;
   }

   public final int getTotalWatk() {
      return this.watk;
   }

   public final long getCurrentMaxHp(MapleCharacter player) {
      if (GameConstants.isZero(player.getJob())) {
         ZeroInfo zeroInfo = player.getZeroInfo();
         if (zeroInfo != null && zeroInfo.isBeta()) {
            return this.localSubMaxHp;
         }
      }

      return this.localmaxhp;
   }

   public final long getCurrentMaxHp() {
      return this.localmaxhp;
   }

   public final long getCurrentSubMaxHp() {
      return this.localSubMaxHp;
   }

   public final long getCurrentMaxMp(MapleCharacter chr) {
      return GameConstants.isDemonSlayer(chr.getJob()) ? GameConstants.getMPByJob(chr) : this.localmaxmp;
   }

   public final int getHands() {
      return this.hands;
   }

   public final float getCurrentMaxBaseDamage() {
      return this.localmaxbasedamage;
   }

   public final float getCurrentMaxBasePVPDamage() {
      return this.localmaxbasepvpdamage;
   }

   public final float getCurrentMaxBasePVPDamageL() {
      return this.localmaxbasepvpdamageL;
   }

   public final int getBufftimeR() {
      return this.bufftimeR;
   }

   public final int getMonsterCollectionR() {
      return this.monsterCollectionR;
   }

   private void resetLocalStats(int job) {
      this.plusExp = 0.0;
      this.bufftimeR = 0;
      this.summonTimeR = 0;
      this.accuracy = 0;
      this.wdef = 0;
      this.mdef = 0;
      this.damX = 0;
      this.localdex = this.getDex();
      this.localint_ = this.getInt();
      this.localstr = this.getStr();
      this.localluk = this.getLuk();
      this.speed = 100;
      this.jump = 100;
      this.pickupRange = 0.0;
      this.decreaseDebuff = 0;
      this.ASR = 0;
      this.TER = 0;
      this.dot = 0;
      this.questBonus = 1;
      this.dotTime = 0;
      this.trueMastery = 0;
      this.percent_wdef = 0;
      this.percent_mdef = 0;
      this.subPercentHp = 0;
      this.percent_hp = 0;
      this.percent_mp = 0;
      this.percent_str = 0;
      this.percent_dex = 0;
      this.percent_int = 0;
      this.percent_luk = 0;
      this.percent_acc = 0;
      this.percent_atk = 0;
      this.percent_matk = 0;
      this.starforce = 0;
      this.authenticforce = 0;
      this.arcaneforce = 0;
      this.passive_sharpeye_rate = 5;
      if (GameConstants.isPhantom(job)) {
         this.passive_sharpeye_rate = 21;
      }

      this.passive_sharpeye_min_percent = 20;
      this.passive_sharpeye_percent = 50;
      this.magic = 0;
      this.watk = 0;
      this.evaR = 0;
      this.mesoG = 0;
      this.pvpDamage = 0;
      this.dam_r = 100.0;
      this.bossdam_r = 100.0;
      this.expBuff = 100.0;
      this.cashBuff = 100.0;
      this.dropBuff = 100.0;
      this.dropBuff_ = 0.0;
      this.dropBuff__ = 0.0;
      this.extraDropBuff = 100.0;
      this.mesoBuff = 100.0;
      this.mesoBuff_ = 0.0;
      this.mesoBuff__ = 0.0;
      this.recoverHP = 0;
      this.recoverMP = 0;
      this.plusExpRate = 0.0;
      this.mpconReduce = 0;
      this.mpconPercent = 100;
      this.incMesoProp = 0;
      this.itemMesoBuff = 0.0;
      this.incRewardProp = 0;
      this.reduceCooltime = 0;
      this.coolTimeR = 0;
      this.suddenDeathR = 0;
      this.expLossReduceR = 0;
      this.DAMreflect = 0;
      this.DAMreflect_rate = 0;
      this.ignoreDAMr = 0;
      this.ignoreDAMr_rate = 0;
      this.ignoreDAM = 0;
      this.ignoreDAM_rate = 0;
      this.ignoreTargetDEF = 0;
      this.hpRecover = 0;
      this.hpRecoverProp = 0;
      this.hpRecoverPercent = 0;
      this.mpRecover = 0;
      this.mpRecoverProp = 0;
      this.pickRate = 0;
      this.equippedWelcomeBackRing = false;
      this.equippedFairy = 0;
      this.equippedSummon = 0;
      this.hasClone = false;
      this.Berserk = false;
      this.equipmentBonusExp = 0;
      this.RecoveryUP = 0;
      this.BuffUP = 0;
      this.RecoveryUP_Skill = 0;
      this.BuffUP_Skill = 0;
      this.BuffUP_Summon = 0;
      this.dropMod = 1;
      this.expMod = 1.0;
      this.cashMod = 1;
      this.levelBonus = 0;
      this.incAllskill = 0;
      this.combatOrders = 0;
      this.passivePlus = 0;
      this.defRange = this.isRangedJob(job) ? 200 : 0;
      this.harvestingTool = 0;
      this.element_fire = 100;
      this.element_ice = 100;
      this.element_light = 100;
      this.element_psn = 100;
      this.reduceCooltimeR = 0;
      this.s_reduceCooltimeR = 0;
      this.def = 100;
      this.disCountR = 0;
      this.itemUpgradeBonusR = 0;
      this.itemCursedProtectR = 0;
      this.itemTUCProtectR = 0;
      this.expGuild = 0;
      this.monsterCollectionR = 0;
      this.reviveInvincibleTime = 0;
      this.incExtraMobGegen = 0;
   }

   public void recalcLocalStats(MapleCharacter chra) {
      this.recalcLocalStats(chra, false);
   }

   public void recalcLocalStats(MapleCharacter chra, boolean canLock) {
      this.recalcLocalStats(false, chra, canLock);
   }

   public void recalcLocalStats(boolean first_login, MapleCharacter chra, boolean canLock) {
      if (!chra.isClone()) {
         if (canLock) {
            this.getLock().writeLock().lock();
         }

         try {
            AtomicInteger eqplucky = new AtomicInteger(0);
            List<Integer> luckyItems = new ArrayList<>();
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            long oldmaxhp = this.localmaxhp;
            long localmaxhp_ = this.getMaxHp();
            long localmaxmp_ = this.getMaxMp();
            long localSubMaxHp_ = this.getMaxHp();
            int multiLeteralPercentHP = 0;
            int multiLeteralPercentMP = 0;
            this.resetLocalStats(chra.getJob());

            for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
               chra.getTrait(t).clearLocalExp();
            }

            Map<Skill, SkillEntry> sData = new HashMap<>();
            int starForce = 0;
            synchronized (this.setHandling) {
               this.setHandling.clear();
            }

            synchronized (this.skillsIncrement) {
               this.skillsIncrement.clear();
            }

            synchronized (this.durabilityHandling) {
               this.durabilityHandling.clear();
            }

            synchronized (this.equipLevelHandling) {
               this.equipLevelHandling.clear();
            }

            AtomicInteger starForce_ = new AtomicInteger(0);
            AtomicInteger authenticForce_ = new AtomicInteger(0);
            AtomicInteger arcaneForce_ = new AtomicInteger(0);
            AtomicLong maxHP = new AtomicLong(localmaxhp_);
            AtomicLong maxHPBySymbol = new AtomicLong(0L);
            AtomicLong maxMP = new AtomicLong(localmaxmp_);
            AtomicBoolean equippedAndroid = new AtomicBoolean(false);
            new ArrayList<>(chra.getInventory(MapleInventoryType.EQUIPPED).list()).forEach(item -> {
               if (GameConstants.isAndroid(item.getItemId())) {
                  equippedAndroid.set(true);
               }
            });
            new ArrayList<>(chra.getInventory(MapleInventoryType.EQUIPPED).list())
                  .forEach(
                        item -> {
                           Equip equip = (Equip) item;
                           if ((equip.getItemState() & ItemStateFlag.VESTIGE_BOUND.getValue()) == 0) {
                              if (equip.getState() > 1 || equip.getSoulPotential() > 0) {
                                 int[] potentials = new int[] {
                                       equip.getPotential1(),
                                       equip.getPotential2(),
                                       equip.getPotential3(),
                                       equip.getPotential4(),
                                       equip.getPotential5(),
                                       equip.getPotential6(),
                                       equip.getSoulPotential()
                                 };

                                 for (int i : potentials) {
                                    if (i > 0) {
                                       if (ii.getReqLevel(equip.getItemId()) >= 10) {
                                          ItemOptionLevelData pot = ii.getPotentialInfo(i)
                                                .get(Math.min(19,
                                                      (int) Math.ceil(ii.getReqLevel(equip.getItemId()) / 10.0)) - 1);
                                          if (pot != null) {
                                             int addHp = pot.incMHP;
                                             int addHpR = pot.incMHPr;
                                             if (GameConstants.isDemonAvenger(chra.getJob())) {
                                                addHp /= 2;
                                             }

                                             if (equippedAndroid.get()
                                                   && GameConstants.isAndroidHeart(equip.getItemId())
                                                   || !GameConstants.isAndroidHeart(equip.getItemId())) {
                                                this.percent_hp += addHpR;
                                                this.subPercentHp += addHpR;
                                                maxHP.addAndGet(addHp);
                                                if (!GameConstants.isDemonSlayer(chra.getJob())) {
                                                   maxMP.addAndGet(pot.incMMP);
                                                }
                                             }

                                             this.handlePotential(pot, chra);
                                          }
                                       } else {
                                          ItemOptionLevelData pot = ii.getPotentialInfo(i).get(0);
                                          if (pot != null) {
                                             int addHpx = pot.incMHP;
                                             int addHpRx = pot.incMHPr;
                                             if (GameConstants.isDemonAvenger(chra.getJob())) {
                                                addHpx /= 2;
                                             }

                                             if (equippedAndroid.get()
                                                   && GameConstants.isAndroidHeart(equip.getItemId())
                                                   || !GameConstants.isAndroidHeart(equip.getItemId())) {
                                                this.percent_hp += addHpRx;
                                                this.subPercentHp += addHpRx;
                                                maxHP.addAndGet(addHpx);
                                                if (!GameConstants.isDemonSlayer(chra.getJob())) {
                                                   maxMP.addAndGet(pot.incMMP);
                                                }
                                             }

                                             this.handlePotential(pot, chra);
                                          }
                                       }
                                    }
                                 }
                              }

                              if (equip.getPosition() == -11 && GameConstants.isMagicWeapon(equip.getItemId())) {
                                 Map<String, Integer> eqstat = MapleItemInformationProvider.getInstance()
                                       .getEquipStats(equip.getItemId());
                                 if (eqstat != null) {
                                    if (eqstat.containsKey("incRMAF")) {
                                       this.element_fire = eqstat.get("incRMAF");
                                    }

                                    if (eqstat.containsKey("incRMAI")) {
                                       this.element_ice = eqstat.get("incRMAI");
                                    }

                                    if (eqstat.containsKey("incRMAL")) {
                                       this.element_light = eqstat.get("incRMAL");
                                    }

                                    if (eqstat.containsKey("incRMAS")) {
                                       this.element_psn = eqstat.get("incRMAS");
                                    }

                                    if (eqstat.containsKey("elemDefault")) {
                                       this.def = eqstat.get("elemDefault");
                                    }
                                 }
                              }

                              if (equip.getCHUC() > 0 && !equip.isAmazingHyperUpgradeUsed()) {
                                 if (GameConstants.isOverall(equip.getItemId())) {
                                    starForce_.addAndGet(equip.getCHUC() * 2);
                                 } else {
                                    starForce_.addAndGet(equip.getCHUC());
                                 }
                              }

                              chra.getTrait(MapleTrait.MapleTraitType.craft).addLocalExp(equip.getHands());
                              this.accuracy = this.accuracy + equip.getTotalAcc();
                              int addHpxx = equip.getTotalHp();
                              if (GameConstants.isArcaneSymbol(equip.getItemId())
                                    || GameConstants.isAuthenticSymbol(equip.getItemId())) {
                                 addHpxx = equip.getTotalHp_Int();
                              }

                              if (!GameConstants.isArcaneSymbol(equip.getItemId())
                                    && !GameConstants.isAuthenticSymbol(equip.getItemId())
                                    && GameConstants.isDemonAvenger(chra.getJob())) {
                                 addHpxx /= 2;
                              }

                              if (GameConstants.isAuthenticSymbol(equip.getItemId())) {
                                 authenticForce_.addAndGet(equip.getArc());
                              }

                              if (GameConstants.isArcaneSymbol(equip.getItemId())) {
                                 arcaneForce_.addAndGet(equip.getArc());
                              }

                              boolean zeroCheck = true;
                              if (GameConstants.isZeroWeapon(equip.getItemId())) {
                                 ZeroInfo zeroInfo = chra.getZeroInfo();
                                 if (equip.getPosition() == -11) {
                                    if (zeroInfo.isBeta()) {
                                       zeroCheck = false;
                                    }
                                 } else if (!zeroInfo.isBeta()) {
                                    zeroCheck = false;
                                 }
                              }

                              if (zeroCheck
                                    && (equippedAndroid.get() && GameConstants.isAndroidHeart(equip.getItemId())
                                          || !GameConstants.isAndroidHeart(equip.getItemId()))) {
                                 if (!GameConstants.isArcaneSymbol(equip.getItemId())
                                       && !GameConstants.isAuthenticSymbol(equip.getItemId())) {
                                    maxHP.addAndGet(addHpxx);
                                 } else {
                                    maxHPBySymbol.addAndGet(addHpxx);
                                 }

                                 if (GameConstants.isDemonSlayer(chra.getJob()) && equip.getItemId() / 1000 == 1099
                                       || !GameConstants.isDemonSlayer(chra.getJob())) {
                                    maxMP.addAndGet(equip.getTotalMp());
                                 }
                              }

                              label325: {
                                 this.localdex = this.localdex + equip.getTotalDex();
                                 this.localint_ = this.localint_ + equip.getTotalInt();
                                 this.localstr = this.localstr + equip.getTotalStr();
                                 this.localluk = this.localluk + equip.getTotalLuk();
                                 int addHpRxx = equip.getHpR();
                                 this.percent_hp += addHpRxx;
                                 this.subPercentHp += addHpRxx;
                                 this.percent_mp = this.percent_mp + equip.getMpR();
                                 this.watk = this.watk + equip.getTotalWatk();
                                 this.magic = this.magic + equip.getTotalMatk();
                                 this.wdef = this.wdef + equip.getTotalWdef();
                                 this.mdef = this.mdef + equip.getTotalMdef();
                                 this.speed = this.speed + equip.getTotalSpeed();
                                 this.jump = this.jump + equip.getTotalJump();
                                 this.pvpDamage = this.pvpDamage + equip.getPVPDamage();
                                 switch (equip.getItemId()) {
                                    case 1112127:
                                       this.equippedWelcomeBackRing = true;
                                       break label325;
                                    case 1112585:
                                       this.equippedSummon = 1085;
                                       break label325;
                                    case 1112586:
                                       this.equippedSummon = 1087;
                                       break label325;
                                    case 1112594:
                                       this.equippedSummon = 1090;
                                       break label325;
                                    case 1112663:
                                       this.equippedSummon = 1179;
                                       break label325;
                                    case 1112735:
                                       this.equippedSummon = 1179;
                                       break label325;
                                    case 1114317:
                                       this.expBuff += 10.0;
                                       break label325;
                                    case 1122017:
                                    case 1122155:
                                    case 1122156:
                                    case 1122207:
                                    case 1122215:
                                    case 1122307:
                                       this.equippedFairy = 10;
                                       break label325;
                                    case 1122158:
                                       this.equippedFairy = 5;
                                       break label325;
                                    case 1122219:
                                       this.dropBuff_ += 20.0;
                                       break label325;
                                    case 1122271:
                                    case 1122334:
                                       this.equippedFairy = 30;
                                       break label325;
                                    case 1122316:
                                    case 1122323:
                                       this.equippedFairy = 20;
                                       break label325;
                                 }

                                 for (int eb_bonus : GameConstants.Equipments_Bonus) {
                                    if (equip.getItemId() == eb_bonus) {
                                       this.equipmentBonusExp = this.equipmentBonusExp
                                             + GameConstants.getEquipmentBonusEXP(eb_bonus);
                                       break;
                                    }
                                 }

                                 for (int b : GameConstants.Equipments_ReduceCool) {
                                    if (equip.getItemId() == b) {
                                       this.s_reduceCooltimeR = this.s_reduceCooltimeR
                                             + GameConstants.getEquipmentReduceCool(b);
                                    }
                                 }
                              }

                              if (ii.isJokerToSetItem(equip.getItemId())) {
                                 if (GameConstants.isWeapon(equip.getItemId())) {
                                    eqplucky.set(equip.getItemId());
                                 } else {
                                    luckyItems.add(equip.getItemId());
                                 }
                              }

                              Integer set = ii.getSetItemID(equip.getItemId());
                              synchronized (this.setHandling) {
                                 if (set != null && set > 0) {
                                    int value = 1;
                                    if (this.setHandling.containsKey(set)) {
                                       value += this.setHandling.get(set);
                                    }

                                    this.setHandling.put(set, value);
                                 }
                              }

                              if (equip.getIncSkill() > 0 && ii.getEquipSkills(equip.getItemId()) != null) {
                                 synchronized (this.skillsIncrement) {
                                    for (int zzz : ii.getEquipSkills(equip.getItemId())) {
                                       Skill skil = SkillFactory.getSkill(zzz);
                                       if (skil != null && skil.canBeLearnedBy(chra.getJob())) {
                                          int value = 1;
                                          if (this.skillsIncrement.get(skil.getId()) != null) {
                                             value += this.skillsIncrement.get(skil.getId());
                                          }

                                          this.skillsIncrement.put(skil.getId(), value);
                                       }
                                    }
                                 }
                              }

                              Pair<Integer, Integer> ix = this.handleEquipAdditions(ii, chra, first_login, sData,
                                    equip.getItemId());
                              if (ix != null) {
                                 maxHP.addAndGet(ix.getLeft().intValue());
                                 if (!GameConstants.isDemonSlayer(chra.getJob())) {
                                    maxMP.addAndGet(ix.getRight().intValue());
                                 }
                              }

                              if (equip.getExceptionalSlot() > 0) {
                                 maxHP.addAndGet(equip.getExceptHP());
                                 if (!GameConstants.isDemonSlayer(chra.getJob())) {
                                    maxMP.addAndGet(equip.getExceptMP());
                                 }
                              }

                              if (equip.getDurability() > 0) {
                                 synchronized (this.durabilityHandling) {
                                    this.durabilityHandling.add(equip);
                                 }
                              }

                              if (GameConstants.getMaxLevel(equip.getItemId()) > 0
                                    && (GameConstants.getStatFromWeapon(equip.getItemId()) == null
                                          ? equip.getEquipLevel() <= GameConstants.getMaxLevel(equip.getItemId())
                                          : equip.getEquipLevel() < GameConstants.getMaxLevel(equip.getItemId()))) {
                                 synchronized (this.equipLevelHandling) {
                                    this.equipLevelHandling.add(equip);
                                 }
                              }
                           }
                        });
            if (GameConstants.isZero(chra.getJob()) && chra.getQuestStatus(41907) == 1) {
               MapleQuestStatus quest = chra.getQuest(41907);
               if (quest != null) {
                  String set = quest.getCustomData();
                  if (!set.isEmpty()) {
                     int setID = Integer.parseInt(set);
                     int value = 1;
                     if (this.setHandling.containsKey(setID)) {
                        value += this.setHandling.get(setID);
                     }

                     this.setHandling.put(setID, value);
                  }
               }
            }

            localmaxhp_ = maxHP.get();
            localSubMaxHp_ = maxHP.get();
            localmaxmp_ = maxMP.get();
            starForce = starForce_.get();
            this.starforce = starForce;
            this.authenticforce = authenticForce_.get();
            this.arcaneforce = arcaneForce_.get();
            if (GameConstants.isDemonAvenger(chra.getJob()) && starForce > 0) {
               int addHp = 35;
               if (starForce >= 16 && starForce < 36) {
                  addHp = 60;
               } else if (starForce >= 36 && starForce < 61) {
                  addHp = 80;
               } else if (starForce >= 61 && starForce < 91) {
                  addHp = 100;
               } else if (starForce >= 91 && starForce < 121) {
                  addHp = 120;
               } else if (starForce >= 121 && starForce < 151) {
                  addHp = 135;
               } else if (starForce >= 151 && starForce < 181) {
                  addHp = 138;
               } else if (starForce >= 181 && starForce < 211) {
                  addHp = 140;
               } else if (starForce >= 211 && starForce < 241) {
                  addHp = 142;
               } else if (starForce >= 241 && starForce < 251) {
                  addHp = 144;
               } else if (starForce >= 251 && starForce < 271) {
                  addHp = 146;
               } else if (starForce >= 271 && starForce < 291) {
                  addHp = 148;
               } else if (starForce >= 291 && starForce < 311) {
                  addHp = 150;
               } else if (starForce >= 311 && starForce < 321) {
                  addHp = 152;
               } else if (starForce >= 321 && starForce < 331) {
                  addHp = 154;
               } else if (starForce >= 331 && starForce < 341) {
                  addHp = 156;
               } else if (starForce >= 341 && starForce < 351) {
                  addHp = 158;
               } else if (starForce >= 351 && starForce < 361) {
                  addHp = 160;
               } else if (starForce >= 361 && starForce < 371) {
                  addHp = 162;
               } else if (starForce >= 371 && starForce < 381) {
                  addHp = 164;
               } else if (starForce >= 381 && starForce < 391) {
                  addHp = 166;
               } else if (starForce >= 391) {
                  addHp = 168;
               }

               addHp *= starForce;
               localmaxhp_ += addHp;
            }

            Collections.sort(luckyItems);
            if (eqplucky.get() != 0) {
               luckyItems.add(0, eqplucky.get());
            }

            int applyed = 0;
            synchronized (this.setHandling) {
               Iterator<Entry<Integer, Integer>> iter = this.setHandling.entrySet().iterator();

               for (int luckyItem : luckyItems) {
                  boolean applyLuckyItem = true;

                  for (Entry<Integer, Integer> entry : this.setHandling.entrySet()) {
                     StructSetItem set = ii.getSetItem(entry.getKey());
                     if (set != null) {
                        int sets = entry.getValue();
                        if (applyed == 0 && sets >= 3 && set.itemParts.contains(luckyItem / 10000)) {
                           for (int id : set.itemIDs) {
                              if (id / 10000 == luckyItem / 10000 && chra.hasEquipped(id)) {
                                 applyLuckyItem = false;
                              }
                           }

                           if (applyLuckyItem) {
                              applyed = luckyItem;
                           }
                        }
                     }
                  }
               }

               while (iter.hasNext()) {
                  Entry<Integer, Integer> entryx = iter.next();
                  StructSetItem set = ii.getSetItem(entryx.getKey());
                  if (set != null) {
                     int sets = entryx.getValue();
                     if (sets >= 3 && set.itemParts.contains(applyed / 10000)) {
                        sets++;
                     }

                     Map<Integer, StructSetItem.SetItem> itemz = set.getItems();

                     for (Entry<Integer, StructSetItem.SetItem> ent : itemz.entrySet()) {
                        if (ent.getKey() <= sets) {
                           StructSetItem.SetItem se = ent.getValue();
                           this.localstr = this.localstr + se.incSTR + se.incAllStat;
                           this.localdex = this.localdex + se.incDEX + se.incAllStat;
                           this.localint_ = this.localint_ + se.incINT + se.incAllStat;
                           this.localluk = this.localluk + se.incLUK + se.incAllStat;
                           this.watk = this.watk + se.incPAD;
                           this.magic = this.magic + se.incMAD;
                           this.speed = this.speed + se.incSpeed;
                           this.accuracy = this.accuracy + se.incACC;
                           localmaxhp_ += se.incMHP / (GameConstants.isDemonAvenger(chra.getJob()) ? 2 : 1);
                           localSubMaxHp_ += se.incMHP / (GameConstants.isDemonAvenger(chra.getJob()) ? 2 : 1);
                           if (!GameConstants.isDemonSlayer(chra.getJob())) {
                              localmaxmp_ += se.incMMP;
                           }

                           this.percent_hp = this.percent_hp + se.incMHPr;
                           this.subPercentHp = this.subPercentHp + se.incMHPr;
                           this.percent_mp = this.percent_mp + se.incMMPr;
                           this.wdef = this.wdef + se.incPDD;
                           this.mdef = this.mdef + se.incMDD;
                        }
                     }
                  }
               }
            }

            if (!GameConstants.isKaiser(chra.getJob())) {
               for (LinkSkillEntry entryx : chra.getLinkSkill().getLinkSkills()) {
                  if (chra.getId() == entryx.getLinkedPlayerID() && entryx.getSkillID() == 60000222
                        && entryx.getSkillLevel() > 0) {
                     int hpR = entryx.getSkillLevel() >= 2 ? 15 : 10;
                     this.percent_hp += hpR;
                  }
               }
            }

            this.handleProfessionTool(chra);
            if (first_login && chra.getLevel() >= 30) {
               if (chra.isGM()) {
                  for (int i = 0; i < allJobs.length; i++) {
                     sData.put(SkillFactory.getSkill(1085 + allJobs[i]), new SkillEntry(1, (byte) 0, -1L));
                     sData.put(SkillFactory.getSkill(1087 + allJobs[i]), new SkillEntry(1, (byte) 0, -1L));
                  }
               } else {
                  sData.put(SkillFactory.getSkill(getSkillByJob(1085, chra.getJob())),
                        new SkillEntry(1, (byte) 0, -1L));
                  sData.put(SkillFactory.getSkill(getSkillByJob(1087, chra.getJob())),
                        new SkillEntry(1, (byte) 0, -1L));
               }
            }

            this.handleBuffStats(chra);
            Integer buff = chra.getBuffedValue(SecondaryStatFlag.EnhancedMaxHP);
            if (buff != null) {
               localmaxhp_ += buff.intValue();
               localSubMaxHp_ += buff.intValue();
            }

            buff = chra.getBuffedValue(SecondaryStatFlag.EnhancedMaxMP);
            if (buff != null && !GameConstants.isDemonSlayer(chra.getJob())) {
               localmaxmp_ += buff.intValue();
            }

            List<IndieTemporaryStatEntry> entrys = chra.getIndieTemporaryStats(SecondaryStatFlag.indieMHP);
            if (entrys != null) {
               for (IndieTemporaryStatEntry indie : entrys) {
                  localmaxhp_ += indie.getStatValue();
                  localSubMaxHp_ += indie.getStatValue();
               }
            }

            entrys = chra.getIndieTemporaryStats(SecondaryStatFlag.indieMMP);
            if (entrys != null && !GameConstants.isDemonSlayer(chra.getJob())) {
               for (IndieTemporaryStatEntry indie : entrys) {
                  localmaxmp_ += indie.getStatValue();
               }
            }

            buff = chra.getBuffedValue(SecondaryStatFlag.SiphonVitalityShield);
            if (buff != null && chra.getTotalSkillLevel(14120051) > 0) {
               this.ASR = this.ASR
                     + SkillFactory.getSkill(14120051).getEffect(chra.getTotalSkillLevel(14120051)).getX();
            }

            Skill bx = SkillFactory.getSkill(1000009);
            int bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               localmaxhp_ += SkillFactory.getSkill(1000009).getEffect(chra.getTotalSkillLevel(1000009)).getLv2mhp()
                     * chra.getLevel();
            }

            bx = SkillFactory.getSkill(11000021);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               localmaxhp_ += SkillFactory.getSkill(11000021).getEffect(chra.getTotalSkillLevel(11000021)).getMaxHpX();
            }

            bx = SkillFactory.getSkill(13110028);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               localmaxhp_ += SkillFactory.getSkill(13110028).getEffect(chra.getTotalSkillLevel(13110028)).getMaxHpX();
            }

            bx = SkillFactory.getSkill(154000004);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               localmaxhp_ += SkillFactory.getSkill(154000004).getEffect(chra.getTotalSkillLevel(154000004))
                     .getMaxHpX();
               localmaxmp_ += SkillFactory.getSkill(154000004).getEffect(chra.getTotalSkillLevel(154000004))
                     .getMaxHpX();
            }

            int[] masterMagic = new int[] { 2120012, 2220013, 2320012, 142120009 };

            for (int s : masterMagic) {
               bx = SkillFactory.getSkill(s);
               bof = chra.getTotalSkillLevel(bx);
               if (bof > 0) {
                  int v = SkillFactory.getSkill(s).getEffect(bof).getBufftimeR();
                  this.bufftimeR += v;
               }
            }

            if (chra.getTrait(MapleTrait.MapleTraitType.sense).getLevel() >= 1) {
               this.bufftimeR = this.bufftimeR + chra.getTrait(MapleTrait.MapleTraitType.sense).getLevel() / 10;
            }

            bx = SkillFactory.getSkill(20050074);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               this.summonTimeR = this.summonTimeR
                     + SkillFactory.getSkill(20050074).getEffect(chra.getTotalSkillLevel(20050074)).getSummonTimeR();
            }

            bx = SkillFactory.getSkill(71000052);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               this.summonTimeR = this.summonTimeR
                     + SkillFactory.getSkill(71000052).getEffect(chra.getTotalSkillLevel(71000052)).getSummonTimeR();
            }

            bx = SkillFactory.getSkill(160000000);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               this.summonTimeR = this.summonTimeR
                     + SkillFactory.getSkill(160000000).getEffect(chra.getTotalSkillLevel(160000000)).getSummonTimeR();
            }

            bx = SkillFactory.getSkill(160010000);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               this.summonTimeR = this.summonTimeR
                     + SkillFactory.getSkill(160010000).getEffect(chra.getTotalSkillLevel(160010000)).getSummonTimeR();
            }

            for (int skillID = 80000654; skillID <= 80000661; skillID++) {
               bx = SkillFactory.getSkill(skillID);
               bof = chra.getTotalSkillLevel(bx);
               if (bof > 0) {
                  SecondaryStatEffect e = bx.getEffect(1);
                  this.plusExp = this.plusExp + e.getExpRPerM();
                  localmaxhp_ += e.getMaxHpX();
                  localSubMaxHp_ += e.getMaxHpX();
                  localmaxmp_ += e.getMaxMpX();
               }
            }

            bx = SkillFactory.getSkill(31200006);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               localmaxhp_ += SkillFactory.getSkill(31200006).getEffect(chra.getTotalSkillLevel(31200006)).getMaxHpX();
            }

            bx = SkillFactory.getSkill(65001002);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               localmaxhp_ += SkillFactory.getSkill(65001002).getEffect(chra.getTotalSkillLevel(65001002)).getMaxHpX();
            }

            bx = SkillFactory.getSkill(65110005);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               localmaxhp_ += SkillFactory.getSkill(65110005).getEffect(chra.getTotalSkillLevel(65110005)).getMaxHpX();
            }

            bx = SkillFactory.getSkill(400011066);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               int hp = SkillFactory.getSkill(400011066).getEffect(chra.getTotalSkillLevel(400011066)).getMaxHpX();
               localmaxhp_ += hp;
               localSubMaxHp_ += hp;
            }

            bx = SkillFactory.getSkill(3111010);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               this.percent_hp = this.percent_hp
                     + SkillFactory.getSkill(3111010).getEffect(chra.getTotalSkillLevel(3111010)).getPercentHP();
            }

            bx = SkillFactory.getSkill(3211010);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               this.percent_hp = this.percent_hp
                     + SkillFactory.getSkill(3111010).getEffect(chra.getTotalSkillLevel(3211010)).getPercentHP();
            }

            bx = SkillFactory.getSkill(5210012);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect eff = SkillFactory.getSkill(5210012).getEffect(chra.getTotalSkillLevel(5210012));
               if (eff != null) {
                  localmaxhp_ += eff.getMaxHpX();
                  if (!GameConstants.isDemonSlayer(chra.getJob())) {
                     localmaxmp_ += eff.getMaxMpX();
                  }
               }
            }

            bx = SkillFactory.getSkill(5220019);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect eff = SkillFactory.getSkill(5220019).getEffect(chra.getTotalSkillLevel(5220019));
               if (eff != null) {
                  localmaxhp_ += eff.getMaxHpX();
               }
            }

            bx = SkillFactory.getSkill(13120004);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect eff = SkillFactory.getSkill(13120004).getEffect(chra.getTotalSkillLevel(13120004));
               if (eff != null) {
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
               }
            }

            bx = SkillFactory.getSkill(37000006);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               this.percent_hp = this.percent_hp
                     + SkillFactory.getSkill(37000006).getEffect(chra.getTotalSkillLevel(37000006)).getPercentHP();
            }

            bx = SkillFactory.getSkill(164110012);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               this.percent_hp = this.percent_hp
                     + SkillFactory.getSkill(164110012).getEffect(chra.getTotalSkillLevel(164110012)).getPercentHP();
            }

            bx = SkillFactory.getSkill(135000021);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               this.percent_hp = this.percent_hp
                     + SkillFactory.getSkill(135000021).getEffect(chra.getTotalSkillLevel(135000021)).getPercentHP();
            }

            bx = SkillFactory.getSkill(131000016);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               this.percent_hp = this.percent_hp
                     + SkillFactory.getSkill(131000016).getEffect(chra.getTotalSkillLevel(131000016)).getPercentHP();
            }

            bx = SkillFactory.getSkill(154110009);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect level = bx.getEffect(bof);
               if (level != null) {
                  this.percent_hp = this.percent_hp + level.getPercentHP();
                  this.percent_mp = this.percent_mp + level.getPercentMP();
                  this.ASR = this.ASR + level.getASRRate();
                  this.TER = this.TER + level.getTERRate();
               }
            }

            if (chra.getJob() == 3612) {
               SecondaryStatEffect eff = SkillFactory.getSkill(36120010).getEffect(1);
               if (eff != null && this.str >= eff.getX() && this.dex >= eff.getX() && this.luk >= eff.getX()) {
                  multiLeteralPercentHP += eff.getS();
                  multiLeteralPercentMP += eff.getS();
               }
            }

            if (chra.getJob() == 3612 || chra.getJob() == 3611) {
               SecondaryStatEffect eff = SkillFactory.getSkill(36110007).getEffect(1);
               if (eff != null && this.str >= eff.getX() && this.dex >= eff.getX() && this.luk >= eff.getX()) {
                  multiLeteralPercentHP += eff.getS();
                  multiLeteralPercentMP += eff.getS();
               }
            }

            if (chra.getJob() >= 3610 && chra.getJob() <= 3612) {
               SecondaryStatEffect eff = SkillFactory.getSkill(36100007).getEffect(1);
               if (eff != null && this.str >= eff.getX() && this.dex >= eff.getX() && this.luk >= eff.getX()) {
                  multiLeteralPercentHP += eff.getS();
                  multiLeteralPercentMP += eff.getS();
               }
            }

            if (GameConstants.isXenon(chra.getJob())) {
               SecondaryStatEffect eff = SkillFactory.getSkill(36000004).getEffect(1);
               if (eff != null && this.str >= eff.getX() && this.dex >= eff.getX() && this.luk >= eff.getX()) {
                  multiLeteralPercentHP += eff.getS();
                  multiLeteralPercentMP += eff.getS();
               }
            }

            if (chra.getGuild() != null) {
               int gSLV = Center.Guild.getSkillLevel(chra.getGuildId(), 91000011);
               SecondaryStatEffect eff = SkillFactory.getSkill(91000011).getEffect(gSLV);
               if (eff != null) {
                  this.mesoG = this.mesoG + eff.getMesoG();
               }

               gSLV = Center.Guild.getSkillLevel(chra.getGuildId(), 91000008);
               eff = SkillFactory.getSkill(91000008).getEffect(gSLV);
               if (eff != null) {
                  this.disCountR = this.disCountR + eff.getDisCountR();
               }

               gSLV = Center.Guild.getSkillLevel(chra.getGuildId(), 91000012);
               eff = SkillFactory.getSkill(91000012).getEffect(gSLV);
               if (eff != null) {
                  this.itemUpgradeBonusR = this.itemUpgradeBonusR + eff.getItemUpgradeBonusR();
               }

               gSLV = Center.Guild.getSkillLevel(chra.getGuildId(), 91000013);
               eff = SkillFactory.getSkill(91000013).getEffect(gSLV);
               if (eff != null) {
                  this.itemCursedProtectR = this.itemCursedProtectR + eff.getItemCursedProtectR();
               }

               gSLV = Center.Guild.getSkillLevel(chra.getGuildId(), 91000014);
               eff = SkillFactory.getSkill(91000014).getEffect(gSLV);
               if (eff != null) {
                  this.itemTUCProtectR = this.itemTUCProtectR + eff.getItemTUCProtectR();
               }

               gSLV = Center.Guild.getSkillLevel(chra.getGuildId(), 91000027);
               eff = SkillFactory.getSkill(91000027).getEffect(gSLV);
               if (eff != null) {
                  this.expGuild = this.expGuild + eff.getExpGuild();
               }

               gSLV = Center.Guild.getSkillLevel(chra.getGuildId(), 91000034);
               eff = SkillFactory.getSkill(91000034).getEffect(gSLV);
               if (eff != null) {
                  localmaxhp_ += eff.getMaxHpX();
                  localSubMaxHp_ += eff.getMaxHpX();
               }
            }

            bx = SkillFactory.getSkill(35001002);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(35001002).getEffect(chra.getTotalSkillLevel(35001002));
               if (effx != null) {
                  localmaxhp_ += effx.getMaxHpX();
                  localmaxmp_ += effx.getMaxMpX();
               }
            }

            bx = SkillFactory.getSkill(60000222);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(60000222).getEffect(chra.getTotalSkillLevel(60000222));
               if (effx != null) {
                  this.percent_hp = this.percent_hp + effx.getPercentHP();
               }
            }

            bx = SkillFactory.getSkill(80000006);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(80000006).getEffect(chra.getTotalSkillLevel(80000006));
               if (effx != null) {
                  this.percent_hp = this.percent_hp + effx.getPercentHP();
               }
            }

            bx = SkillFactory.getSkill(61100007);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(61100007).getEffect(chra.getTotalSkillLevel(61100007));
               if (effx != null) {
                  this.percent_hp = this.percent_hp + effx.getPercentHP();
               }
            }

            bx = SkillFactory.getSkill(61110007);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(61110007).getEffect(chra.getTotalSkillLevel(61110007));
               if (effx != null) {
                  this.percent_hp = this.percent_hp + effx.getPercentHP();
               }
            }

            bx = SkillFactory.getSkill(101100203);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && chra.getGender() == 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(101100203)
                     .getEffect(chra.getTotalSkillLevel(101100203));
               if (effx != null) {
                  this.percent_hp = this.percent_hp + effx.getPercentHP();
               }
            }

            bx = SkillFactory.getSkill(100000279);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(100000279)
                     .getEffect(chra.getTotalSkillLevel(100000279));
               if (effx != null) {
                  this.percent_hp = this.percent_hp + effx.getPercentHP();
                  this.subPercentHp = this.subPercentHp + effx.getPercentHP();
               }
            }

            bx = SkillFactory.getSkill(3310006);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(3310006).getEffect(chra.getTotalSkillLevel(3310006));
               if (effx != null) {
                  this.percent_hp = this.percent_hp + effx.getPercentHP();
               }
            }

            bx = SkillFactory.getSkill(80000404);
            bof = chra.getHyperStat().getStat().getSkillLevel(80000404);
            if (bof > 0) {
               int per = bx.getEffect(bof).getPercentHP();
               this.percent_hp += per;
               this.subPercentHp += per;
            }

            bx = SkillFactory.getSkill(80000405);
            bof = chra.getHyperStat().getStat().getSkillLevel(80000405);
            if (bof > 0) {
               this.percent_mp = this.percent_mp + bx.getEffect(bof).getPercentMP();
            }

            bx = SkillFactory.getSkill(135000021);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               this.percent_mp = this.percent_mp
                     + SkillFactory.getSkill(135000021).getEffect(chra.getTotalSkillLevel(135000021)).getPercentMP();
            }

            bx = SkillFactory.getSkill(131000016);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               this.percent_mp = this.percent_mp
                     + SkillFactory.getSkill(131000016).getEffect(chra.getTotalSkillLevel(131000016)).getPercentMP();
            }

            bx = SkillFactory.getSkill(80000406);
            bof = chra.getHyperStat().getStat().getSkillLevel(80000406);
            if (bof > 0) {
               localmaxmp_ += bx.getEffect(bof).getMDF();
            }

            bx = SkillFactory.getSkill(5310007);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(5310007).getEffect(bof);
               if (effx != null) {
                  this.percent_hp = this.percent_hp + effx.getPercentHP();
                  this.ASR = this.ASR + effx.getASRRate();
               }
            }

            bx = SkillFactory.getSkill(151000005);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(151000005).getEffect(bof);
               if (effx != null) {
                  localmaxhp_ += effx.getMaxHpX();
               }
            }

            bx = SkillFactory.getSkill(151110007);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(151110007).getEffect(bof);
               if (effx != null) {
                  this.percent_hp = this.percent_hp + effx.getPercentHP();
                  this.ASR = this.ASR + effx.getASRRate();
                  this.TER = this.TER + effx.getTERRate();
               }
            }

            bx = SkillFactory.getSkill(151120010);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(151120010).getEffect(bof);
               if (effx != null) {
                  this.percent_hp = this.percent_hp + effx.getPercentHP();
               }
            }

            bx = SkillFactory.getSkill(164110013);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(164110013).getEffect(bof);
               if (effx != null) {
                  this.ASR = this.ASR + effx.getASRRate();
                  this.TER = this.TER + effx.getTERRate();
               }
            }

            bx = SkillFactory.getSkill(36100003);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(36100003).getEffect(chra.getTotalSkillLevel(36100003));
               if (effx != null) {
                  this.percent_hp = this.percent_hp + effx.getPercentHP();
                  this.percent_mp = this.percent_mp + effx.getPercentMP();
               }
            }

            bx = SkillFactory.getSkill(36100004);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(36100004).getEffect(chra.getTotalSkillLevel(36100004));
               if (effx != null) {
                  localmaxhp_ += effx.getMaxHpX();
                  localmaxmp_ += effx.getMaxMpX();
               }
            }

            bx = SkillFactory.getSkill(36110003);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(36110003).getEffect(chra.getTotalSkillLevel(36110003));
               if (effx != null) {
                  this.localstr = this.localstr + effx.getStrX();
                  this.localdex = this.localdex + effx.getDexX();
                  this.localint_ = this.localint_ + effx.getIntX();
                  this.localluk = this.localluk + effx.getLukX();
               }
            }

            bx = SkillFactory.getSkill(24110003);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(24110003).getEffect(bof);
               if (effx != null) {
                  this.percent_hp = this.percent_hp + effx.getPercentHP();
                  this.percent_mp = this.percent_mp + effx.getPercentMP();
                  this.TER = this.TER + effx.getTERRate();
                  this.ASR = this.ASR + effx.getASRRate();
               }
            }

            bx = SkillFactory.getSkill(13111023);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(13111023).getEffect(bof);
               if (effx != null) {
                  localmaxhp_ += effx.getMaxHpX();
                  this.watk = this.watk + effx.getWatk();
                  this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + effx.getCr());
               }
            }

            bx = SkillFactory.getSkill(8880501);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
               SecondaryStatEffect effx = SkillFactory.getSkill(8880501).getEffect(bof);
               if (effx != null) {
                  chra.temporaryStatSet(SecondaryStatFlag.indiePMDR, 8880501, 2100000000, effx.getLevel());
               }
            }

            int totalLevel = 0;

            for (LinkSkillEntry entryxx : chra.getLinkSkill().getLinkSkills()) {
               if ((entryxx.getLinkedPlayerID() == chra.getId() || entryxx.getLinkingPlayerID() == chra.getId())
                     && (entryxx.getSkillID() == 110 || entryxx.getSkillID() == 264 || entryxx.getSkillID() == 265)) {
                  totalLevel += entryxx.getSkillLevel();
               }
            }

            if (totalLevel > 0) {
               SecondaryStatEffect e = SkillFactory.getSkill(80002774).getEffect(totalLevel);
               if (e != null) {
                  localmaxhp_ += e.getMaxHpX();
                  localSubMaxHp_ += e.getMaxHpX();
                  if (!GameConstants.isDemonSlayer(chra.getJob())) {
                     localmaxmp_ += e.getMaxMpX();
                  }
               }
            }

            int var244 = 0;

            for (LinkSkillEntry entryxxx : chra.getLinkSkill().getLinkSkills()) {
               if ((entryxxx.getLinkedPlayerID() == chra.getId() || entryxxx.getLinkingPlayerID() == chra.getId())
                     && (entryxxx.getSkillID() == 258 || entryxxx.getSkillID() == 259
                           || entryxxx.getSkillID() == 260)) {
                  var244 += entryxxx.getSkillLevel();
               }
            }

            if (var244 > 0) {
               SecondaryStatEffect e = SkillFactory.getSkill(80002766).getEffect(var244);
               if (e != null) {
                  this.monsterCollectionR = this.monsterCollectionR + e.getMonsterCollectionProp();
               }
            }

            for (ExtraAbilityStatEntry entryxxxx : chra.getExtraAbilityStats()[chra.getExtraAbilitySlot()]) {
               if (entryxxxx.getOption() == ExtraAbilityOption.ReviveInvincible) {
                  this.reviveInvincibleTime = this.reviveInvincibleTime + entryxxxx.getValue();
               } else if (entryxxxx.getOption() == ExtraAbilityOption.IncMobGen) {
                  this.incExtraMobGegen = 50;
               }
            }

            if (DBConfig.isGanglim) {
               int[][] reviveDonate = new int[][] { { 1500, 3 }, { 2000, 5 }, { 2500, 7 }, { 3000, 10 } };
               int incReviveInvinTime = 0;

               for (int[] donates : reviveDonate) {
                  int price = donates[0];
                  int reviveInvinTime = donates[1];
                  String donateKeyValue = chra.getClient().getKeyValue("DPointAll_" + price);
                  if (donateKeyValue != null) {
                     int check = Integer.parseInt(donateKeyValue);
                     if (check == 1) {
                        incReviveInvinTime = reviveInvinTime;
                     }
                  }
               }

               this.reviveInvincibleTime += incReviveInvinTime;
            }

            this.handlePassiveSkills(chra);
            if (chra.getGuildId() > 0) {
               Guild g = Center.Guild.getGuild(chra.getGuildId());
               if (g != null && g.getSkills().size() > 0) {
                  long now = System.currentTimeMillis();

                  for (GuildSkill gs : g.getSkills()) {
                     if (gs.timestamp > now && gs.activator.length() > 0) {
                        SecondaryStatEffect e = SkillFactory.getSkill(gs.skillID).getEffect(gs.level);
                        this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + e.getCr());
                        this.watk = this.watk + e.getAttackX();
                        this.magic = this.magic + e.getMagicX();
                        this.plusExp = this.plusExp * ((e.getEXPRate() + 100.0) / 100.0);
                        this.evaR = this.evaR + e.getER();
                        this.percent_wdef = this.percent_wdef + e.getWDEFRate();
                        this.percent_mdef = this.percent_mdef + e.getMDEFRate();
                     }
                  }
               }
            }

            this.localstr = (int) (this.localstr + Math.floor(this.localstr * this.percent_str / 100.0F));
            this.localdex = (int) (this.localdex + Math.floor(this.localdex * this.percent_dex / 100.0F));
            this.localint_ = (int) (this.localint_ + Math.floor(this.localint_ * this.percent_int / 100.0F));
            this.localluk = (int) (this.localluk + Math.floor(this.localluk * this.percent_luk / 100.0F));
            if (this.localint_ > this.localdex) {
               this.accuracy = (int) (this.accuracy + (this.localint_ + Math.floor(this.localluk * 1.2)));
            } else {
               this.accuracy = (int) (this.accuracy + (this.localluk + Math.floor(this.localdex * 1.2)));
            }

            this.watk = (int) (this.watk + Math.floor(this.watk * this.percent_atk / 100.0F));
            this.magic = (int) (this.magic + Math.floor(this.magic * this.percent_matk / 100.0F));
            this.wdef = (int) (this.wdef
                  + Math.floor(this.localstr * 1.2 + (this.localdex + this.localluk) * 0.5 + this.localint_ * 0.4));
            this.mdef = (int) (this.mdef
                  + Math.floor(this.localstr * 0.4 + (this.localdex + this.localluk) * 0.5 + this.localint_ * 1.2));
            this.wdef = (int) (this.wdef + Math.min(30000.0, Math.floor(this.wdef * this.percent_wdef / 100.0F)));
            this.mdef = (int) (this.mdef + Math.min(30000.0, Math.floor(this.wdef * this.percent_mdef / 100.0F)));
            this.hands = this.localdex + this.localint_ + this.localluk;
            this.calculateFame(chra);
            this.ignoreTargetDEF = (int) (this.ignoreTargetDEF + (100 - this.ignoreTargetDEF)
                  * (chra.getTrait(MapleTrait.MapleTraitType.charisma).getLevel() / 10 / 100.0));
            this.pvpDamage = this.pvpDamage + chra.getTrait(MapleTrait.MapleTraitType.charisma).getLevel() / 10;
            this.ASR = this.ASR + chra.getTrait(MapleTrait.MapleTraitType.will).getLevel() / 5;
            this.accuracy = (int) (this.accuracy + Math.floor(this.accuracy * this.percent_acc / 100.0F));
            this.accuracy = this.accuracy + chra.getTrait(MapleTrait.MapleTraitType.insight).getLevel() * 15 / 10;

            for (CharacterPotentialHolder holder : chra.getInnerSkills()) {
               int skillIDx = holder.getSkillId();
               int skillLevel = holder.getSkillLevel();
               SecondaryStatEffect effect = SkillFactory.getSkill(skillIDx).getEffect(skillLevel);
               if (effect != null) {
                  this.watk = this.watk + effect.getWatk();
                  this.magic = this.magic + effect.getMatk();
                  this.localstr = this.localstr + effect.getStrX();
                  this.localdex = this.localdex + effect.getDexX();
                  this.localint_ = this.localint_ + effect.getIntX();
                  this.localluk = this.localluk + effect.getLukX();
                  localmaxhp_ += effect.getMaxHpX();
                  localSubMaxHp_ += effect.getMaxHpX();
                  if (!GameConstants.isDemonSlayer(chra.getJob())) {
                     localmaxmp_ += effect.getMaxMpX();
                  }

                  this.percent_hp = this.percent_hp + effect.getPercentHP();
                  this.subPercentHp = this.subPercentHp + effect.getPercentHP();
                  this.percent_mp = this.percent_mp + effect.getPercentMP();
                  this.bufftimeR = this.bufftimeR + effect.getBufftimeR();
                  double temp = effect.getExpRPerM();
                  this.mesoBuff_ = this.mesoBuff_ + effect.getMesoRate();
                  this.dropBuff_ = this.dropBuff_ + effect.getDropRate();
                  this.plusExp += temp;
               }
            }

            String v = chra.getOneInfoQuest(19019, "id");
            int nickItemID = 0;
            if (v != null && !v.isEmpty()) {
               nickItemID = Integer.parseInt(v);
            }

            if (nickItemID > 0) {
               int nickSKill = ii.getNickSkill(nickItemID);
               if (nickSKill > 0) {
                  SecondaryStatEffect e = SkillFactory.getSkill(nickSKill).getEffect(1);
                  if (e != null) {
                     localmaxhp_ += e.getMaxHpX();
                     localSubMaxHp_ += e.getMaxHpX();
                     if (!GameConstants.isDemonSlayer(chra.getJob())) {
                        localmaxmp_ += e.getMaxMpX();
                     }

                     this.localstr = this.localstr + e.getStrX();
                     this.localdex = this.localdex + e.getDexX();
                     this.localint_ = this.localint_ + e.getIntX();
                     this.localluk = this.localluk + e.getLukX();
                     this.percent_hp = this.percent_hp + e.getPercentHP();
                     this.subPercentHp = this.subPercentHp + e.getPercentHP();
                     this.percent_mp = this.percent_mp + e.getPercentMP();
                  }
               }
            }

            MapleUnion union = chra.getMapleUnion();
            List<Pair<Integer, Integer>> pair = null;
            if (union != null) {
               pair = union.calculateMapleUnionPassive();

               for (Pair<Integer, Integer> p : pair) {
                  int skillIDx = p.getLeft();
                  int skillLevel = p.getRight();
                  SecondaryStatEffect effect = SkillFactory.getSkill(skillIDx).getEffect(skillLevel);
                  this.percent_hp = this.percent_hp + effect.getPercentHP();
                  this.subPercentHp = this.subPercentHp + effect.getPercentHP();
                  this.percent_mp = this.percent_mp + effect.getPercentMP();
                  localmaxhp_ += effect.getMaxHpX();
                  localSubMaxHp_ += effect.getMaxHpX();
                  if (!GameConstants.isDemonSlayer(chra.getJob())) {
                     localmaxmp_ += effect.getMaxMpX();
                  }

                  this.localstr = this.localstr + effect.getStrX();
                  this.localdex = this.localdex + effect.getDexX();
                  this.localint_ = this.localint_ + effect.getIntX();
                  this.localluk = this.localluk + effect.getLukX();
                  this.expBuff = this.expBuff * ((effect.getEXPRate() + 100.0) / 100.0);
               }
            }

            localmaxhp_ += chra.getTrait(MapleTrait.MapleTraitType.will).getLevel() / 5 * 100L;
            localSubMaxHp_ += chra.getTrait(MapleTrait.MapleTraitType.will).getLevel() / 5 * 100L;
            localmaxhp_ += (int) Math.floor((float) (this.percent_hp * localmaxhp_) / 100.0F);
            localmaxhp_ += (int) Math.floor((float) (multiLeteralPercentHP * localmaxhp_) / 100.0F);
            localSubMaxHp_ += (int) Math.floor((float) (this.subPercentHp * localSubMaxHp_) / 100.0F);
            localSubMaxHp_ += (int) Math.floor((float) (multiLeteralPercentHP * this.localSubMaxHp) / 100.0F);
            if (union != null) {
               for (Pair<Integer, Integer> p : pair) {
                  int skillIDx = p.getLeft();
                  int skillLevel = p.getRight();
                  SecondaryStatEffect effect = SkillFactory.getSkill(skillIDx).getEffect(skillLevel);
                  this.watk = this.watk + effect.getWatk();
                  this.magic = this.magic + effect.getMatk();
                  this.localstr = this.localstr + effect.getStrFX();
                  this.localdex = this.localdex + effect.getDexFX();
                  this.localint_ = this.localint_ + effect.getIntFX();
                  this.localluk = this.localluk + effect.getLukFX();
                  localmaxhp_ += effect.getHpFX();
                  localSubMaxHp_ += effect.getHpFX();
                  this.bufftimeR = this.bufftimeR + effect.getBufftimeR();
                  this.summonTimeR = this.summonTimeR + effect.getSummonTimeR();
                  double temp = effect.getExpRPerM();
                  this.plusExp += temp;
                  this.mesoBuff_ = this.mesoBuff_ + effect.getMesoRate();
                  this.coolTimeR = this.coolTimeR + effect.getCoolTimeR();
               }
            }

            localmaxhp_ += maxHPBySymbol.get();
            localSubMaxHp_ += maxHPBySymbol.get();
            if (chra != null && chra.getHexaCore() != null && GameConstants.isDemonAvenger(chra.getJob())
                  && chra.getHexaCore().getStatSize() > 0) {
               for (Entry<Integer, HexaCore.HexaStatInfo> entryxxxxx : chra.getHexaCore().getStat(0).getStats()
                     .entrySet()) {
                  if (entryxxxxx.getValue().type == HexaMatrixConstants.HexaStatOption.INCREASE_MAINSTAT) {
                     localmaxhp_ += entryxxxxx.getValue().level * 2100;
                  }
               }
            }

            this.localmaxhp = Math.min(500000L, Math.abs(Math.max(-500000L, localmaxhp_)));
            this.localSubMaxHp = Math.min(500000L, Math.abs(Math.max(-500000L, localSubMaxHp_)));
            this.incMesoProp = Math.min(100, this.incMesoProp);
            this.itemMesoBuff = Math.min(200.0, this.itemMesoBuff);
            this.mesoBuff = this.mesoBuff + Math.min(300.0, this.mesoBuff_ + this.incMesoProp + this.itemMesoBuff);
            this.mesoBuff = this.mesoBuff * ((100.0 + this.mesoBuff__) / 100.0);
            this.incRewardProp = Math.min(200, this.incRewardProp);
            this.dropBuff = this.dropBuff + Math.min(300.0, this.dropBuff_ + this.incRewardProp);
            this.dropBuff = this.dropBuff * ((100.0 + this.dropBuff__) / 100.0);
            if (chra.getBuffedValue(SecondaryStatFlag.ItemUpByItem) != null) {
               this.dropBuff = this.dropBuff
                     + Math.min(300.0, this.dropBuff_ + chra.getBuffedValue(SecondaryStatFlag.ItemUpByItem).intValue());
               this.dropBuff = this.dropBuff * ((100.0 + this.dropBuff__) / 100.0);
            }

            if (!GameConstants.isDemonSlayer(chra.getJob())) {
               localmaxmp_ += chra.getTrait(MapleTrait.MapleTraitType.sense).getLevel() / 5 * 100;
               localmaxmp_ = (long) (localmaxmp_ + Math.floor((float) (this.percent_mp * localmaxmp_) / 100.0F));
               localmaxmp_ = (long) (localmaxmp_ + Math.floor((float) (multiLeteralPercentMP * localmaxmp_) / 100.0F));
               if (union != null && pair != null) {
                  for (Pair<Integer, Integer> p : pair) {
                     int skillIDx = p.getLeft();
                     int skillLevel = p.getRight();
                     SecondaryStatEffect effect = SkillFactory.getSkill(skillIDx).getEffect(skillLevel);
                     localmaxmp_ += effect.getMpFX();
                  }
               }

               bx = SkillFactory.getSkill(2000006);
               bof = chra.getTotalSkillLevel(bx);
               if (bof > 0) {
                  localmaxmp_ += SkillFactory.getSkill(2000006).getEffect(chra.getTotalSkillLevel(2000006)).getLv2mmp()
                        * chra.getLevel();
               }

               bx = SkillFactory.getSkill(12000025);
               bof = chra.getTotalSkillLevel(bx);
               if (bof > 0) {
                  localmaxmp_ += SkillFactory.getSkill(12000025).getEffect(chra.getTotalSkillLevel(12000025))
                        .getLv2mmp() * chra.getLevel();
               }

               bx = SkillFactory.getSkill(22000014);
               bof = chra.getTotalSkillLevel(bx);
               if (bof > 0) {
                  localmaxmp_ += SkillFactory.getSkill(22000014).getEffect(chra.getTotalSkillLevel(22000014))
                        .getLv2mmp() * chra.getLevel();
               }
            }

            this.localmaxmp = Math.min(500000L, Math.abs(Math.max(-500000L, localmaxmp_)));
            if (chra.getBuffedValue(SecondaryStatFlag.MHPCutR) != null) {
               long tempHP = this.localmaxhp;
               long decHP = (long) (tempHP * (chra.getBuffedValue(SecondaryStatFlag.MHPCutR).intValue() / 100.0));
               this.localmaxhp = tempHP - decHP;
            }

            if (chra.getBuffedValue(SecondaryStatFlag.MMPCutR) != null) {
               long tempMP = this.localmaxmp;
               long decMP = (long) (tempMP * (chra.getBuffedValue(SecondaryStatFlag.MMPCutR).intValue() / 100.0));
               this.localmaxmp = tempMP - decMP;
            }

            if (chra.getBuffedValue(SecondaryStatFlag.LimitMP) != null) {
               this.localmaxmp = chra.getBuffedValue(SecondaryStatFlag.LimitMP).intValue();
            }

            chra.changeSkillLevel_Skip(sData, false);
            if (GameConstants.isDemonSlayer(chra.getJob())) {
               this.localmaxmp = GameConstants.getMPByJob(chra);
            } else if (GameConstants.isZero(chra.getJob())) {
               this.localmaxmp = 100L;
            }

            this.CalcPassive_SharpEye(chra);
            this.CalcPassive_Mastery(chra);
            this.recalcPVPRank(chra);
            if (first_login) {
               chra.silentEnforceMaxHpMp();
               this.relocHeal(chra);
            } else {
               chra.enforceMaxHpMp();
            }

            if (chra.getZeroInfo() != null) {
               chra.getZeroInfo().updateHPMP(chra);
            }

            this.calculateMaxBaseDamage(Math.max(this.magic, this.watk), this.pvpDamage, chra);
            this.trueMastery = Math.min(100, this.trueMastery);
            this.passive_sharpeye_min_percent = (short) Math.min(this.passive_sharpeye_min_percent,
                  this.passive_sharpeye_percent);
            if (oldmaxhp != 0L && oldmaxhp != this.localmaxhp) {
               chra.updatePartyMemberHP();
            }
         } finally {
            if (canLock) {
               this.getLock().writeLock().unlock();
            }
         }
      }
   }

   private void handlePassiveSkills(final MapleCharacter chra) {
      SecondaryStatEffect eff = null;
      this.DAMreduceR = 0;
      if (GameConstants.isKOC(chra.getJob())) {
         Skill bx = SkillFactory.getSkill(2000006);
         int bof = chra.getTotalSkillLevel(bx);
         if (bof > 0) {
            eff = bx.getEffect(bof);
            this.percent_hp = this.percent_hp + eff.getX();
            this.percent_mp = this.percent_mp + eff.getX();
         }
      }

      synchronized (this.damageIncrease) {
         this.damageIncrease.clear();
         switch (chra.getJob()) {
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
               Skill bxxxxxxxxxx = SkillFactory.getSkill(1000003);
               int bofxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxx);
               if (bofxxxxxxxxx > 0) {
                  this.percent_hp = this.percent_hp + bxxxxxxxxxx.getEffect(bofxxxxxxxxx).getPercentHP();
               }

               bxxxxxxxxxx = SkillFactory.getSkill(1210001);
               bofxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxx);
               if (bofxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxx.getEffect(bofxxxxxxxxx);
                  this.percent_wdef = this.percent_wdef + eff.getX();
                  this.percent_mdef = this.percent_mdef + eff.getX();
               }

               bxxxxxxxxxx = SkillFactory.getSkill(1220005);
               bofxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxx);
               if (bofxxxxxxxxx > 0) {
                  this.DAMreduceR = (int) (this.DAMreduceR + (5.0 + 0.5 * bofxxxxxxxxx));
               }

               bxxxxxxxxxx = SkillFactory.getSkill(1220010);
               bofxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxx);
               if (bofxxxxxxxxx > 0) {
                  this.trueMastery = this.trueMastery + bxxxxxxxxxx.getEffect(bofxxxxxxxxx).getMastery();
               }

               bxxxxxxxxxx = SkillFactory.getSkill(1310000);
               bofxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxx);
               if (bofxxxxxxxxx > 0) {
                  this.TER = this.TER + bxxxxxxxxxx.getEffect(bofxxxxxxxxx).getX();
               }
               break;
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
               Skill bxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2000006);
               int bofxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.percent_mp = this.percent_mp
                        + bxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxx).getPercentMP();
               }
               break;
            case 312:
               Skill bxxxxxxxx = SkillFactory.getSkill(3120005);
               int bofxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxx);
               if (bofxxxxxxx > 0) {
                  this.watk = this.watk + bxxxxxxxx.getEffect(bofxxxxxxx).getX();
               }

               bxxxxxxxx = SkillFactory.getSkill(3120011);
               bofxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxx);
               if (bofxxxxxxx > 0) {
                  eff = bxxxxxxxx.getEffect(bofxxxxxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
                  this.ignoreTargetDEF = (int) (this.ignoreTargetDEF
                        + (100 - this.ignoreTargetDEF) * (eff.getIgnoreMob() / 100.0));
               }

               bxxxxxxxx = SkillFactory.getSkill(3120006);
               bofxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxx);
               if (bofxxxxxxx > 0 && chra.getBuffedValue(SecondaryStatFlag.CrewCommandership) != null) {
                  eff = bxxxxxxxx.getEffect(bofxxxxxxx);
                  this.percent_hp = this.percent_hp + eff.getX();
                  this.dam_r = this.dam_r * ((eff.getDamage() + 100.0) / 100.0);
                  this.bossdam_r = this.bossdam_r * ((eff.getDamage() + 100.0) / 100.0);
               }
               break;
            case 322:
               Skill bxxxxxxxxx = SkillFactory.getSkill(3220004);
               int bofxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxx);
               if (bofxxxxxxxx > 0) {
                  eff = bxxxxxxxxx.getEffect(bofxxxxxxxx);
                  this.watk = this.watk + eff.getX();
                  this.trueMastery = this.trueMastery + eff.getMastery();
                  this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                        + eff.getCriticalMin());
               }

               bxxxxxxxxx = SkillFactory.getSkill(3220009);
               bofxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxx);
               if (bofxxxxxxxx > 0) {
                  eff = bxxxxxxxxx.getEffect(bofxxxxxxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
                  this.ignoreTargetDEF = (int) (this.ignoreTargetDEF
                        + (100 - this.ignoreTargetDEF) * (eff.getIgnoreMob() / 100.0));
               }
               break;
            case 400:
            case 410:
            case 411:
            case 412:
            case 420:
            case 421:
            case 422:
               Skill bxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(4001005);
               int bofxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.speed = this.speed + eff.getSpeedMax();
               }

               if (chra.getJob() >= 410 && chra.getJob() <= 412) {
                  bxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(4100007);
                  bofxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  if (bofxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                     eff = bxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxx);
                     this.localluk = this.localluk + eff.getLukX();
                     this.localdex = this.localdex + eff.getDexX();
                  }
               }

               if (chra.getJob() >= 420 && chra.getJob() <= 422) {
                  bxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(4200007);
                  bofxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  if (bofxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                     eff = bxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxx);
                     this.localluk = this.localluk + eff.getLukX();
                     this.localdex = this.localdex + eff.getDexX();
                  }
               }

               if (chra.getJob() == 411 || chra.getJob() == 412) {
                  bxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(4110008);
                  bofxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  if (bofxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                     eff = bxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxx);
                     this.percent_hp = this.percent_hp + eff.getPercentHP();
                     this.ASR = this.ASR + eff.getASRRate();
                     this.TER = this.TER + eff.getTERRate();
                  }

                  bxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(4110012);
                  bofxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  if (bofxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                     eff = bxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxx);
                     this.damageIncrease.put(4001344, eff.getDAMRate());
                     this.damageIncrease.put(4101008, eff.getDAMRate());
                     this.damageIncrease.put(4101009, eff.getDAMRate());
                     this.damageIncrease.put(4101010, eff.getDAMRate());
                  }

                  bxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(4110014);
                  bofxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  if (bofxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                     eff = bxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxx);
                     this.RecoveryUP = this.RecoveryUP + (eff.getX() - 100);
                  }
               }

               if (chra.getJob() == 412) {
                  bxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(4121014);
                  bofxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  if (bofxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                     eff = bxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxx);
                     this.ignoreTargetDEF = (int) (this.ignoreTargetDEF
                           + (100 - this.ignoreTargetDEF) * (eff.getIgnoreMob() / 100.0));
                  }
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(4200006);
               bofxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
                  this.ASR = this.ASR + eff.getASRRate();
                  this.TER = this.TER + eff.getTERRate();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(4210000);
               bofxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.percent_wdef = this.percent_wdef + eff.getX();
                  this.percent_mdef = this.percent_mdef + eff.getX();
               }
               break;
            case 431:
            case 432:
            case 433:
            case 434:
               Skill bxxxxxxxxxxx = SkillFactory.getSkill(4001006);
               int bofxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxx);
               if (bofxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxx.getEffect(bofxxxxxxxxxx);
                  this.speed = this.speed + eff.getSpeedMax();
               }

               bxxxxxxxxxxx = SkillFactory.getSkill(4330001);
               bofxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxx);
               if (bofxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxx.getEffect(bofxxxxxxxxxx);
                  this.damageIncrease.put(4331000, eff.getY());
                  this.damageIncrease.put(4331006, eff.getY());
                  this.damageIncrease.put(4341002, eff.getY());
                  this.damageIncrease.put(4341004, eff.getY());
                  this.damageIncrease.put(4341009, eff.getY());
                  this.damageIncrease.put(4341011, eff.getY());
               }

               bxxxxxxxxxxx = SkillFactory.getSkill(4310004);
               bofxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxx);
               if (bofxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxx.getEffect(bofxxxxxxxxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
                  this.ASR = this.ASR + eff.getASRRate();
                  this.TER = this.TER + eff.getTERRate();
               }

               bxxxxxxxxxxx = SkillFactory.getSkill(4341006);
               bofxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxx);
               if (bofxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxx.getEffect(bofxxxxxxxxxx);
                  this.percent_wdef = this.percent_wdef + eff.getWDEFRate();
                  this.percent_mdef = this.percent_mdef + eff.getMDEFRate();
               }

               bxxxxxxxxxxx = SkillFactory.getSkill(4310006);
               bofxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxx);
               if (bofxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxx.getEffect(bofxxxxxxxxxx);
                  this.localdex = this.localdex + eff.getDexX();
                  this.localluk = this.localluk + eff.getLukX();
               }

               bxxxxxxxxxxx = SkillFactory.getSkill(4330008);
               bofxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxx);
               if (bofxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxx.getEffect(bofxxxxxxxxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
                  this.ASR = this.ASR + eff.getASRRate();
                  this.TER = this.TER + eff.getTERRate();
               }
               break;
            case 501:
            case 530:
            case 531:
            case 532:
               Skill bxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(5010003);
               int bofxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxx > 0) {
                  this.watk = this.watk + bxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxx).getAttackX();
               }

               bxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(5300008);
               bofxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxx);
                  this.localstr = this.localstr + eff.getStrX();
                  this.localdex = this.localdex + eff.getDexX();
               }

               bxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(5311001);
               bofxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxx > 0) {
                  this.damageIncrease.put(5301001, bxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxx).getDAMRate());
               }

               bxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(5310006);
               bofxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxx > 0) {
                  this.watk = this.watk + bxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxx).getAttackX();
               }

               bxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(5321009);
               bofxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxx);
                  this.dam_r = this.dam_r * ((eff.getDAMRate() + 100.0) / 100.0);
                  this.bossdam_r = this.bossdam_r * ((eff.getDAMRate() + 100.0) / 100.0);
                  this.ignoreTargetDEF = (int) (this.ignoreTargetDEF
                        + (100 - this.ignoreTargetDEF) * (eff.getIgnoreMob() / 100.0));
               }
               break;
            case 1100:
            case 1110:
            case 1111:
            case 1112:
               Skill bxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(11000005);
               int bofxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.percent_hp = this.percent_hp
                        + bxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxx).getPercentHP();
               }

               bxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(11000023);
               bofxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.percent_hp = this.percent_hp
                        + bxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxx).getPercentHP();
               }

               bxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(11110025);
               bofxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.ASR = this.ASR + bxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxx).getASRRate();
                  this.TER = this.TER + bxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxx).getTERRate();
               }
               break;
            case 1200:
            case 1210:
            case 1211:
            case 1212:
               Skill bxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(12000005);
               int bofxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.percent_mp = this.percent_mp
                        + bxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxx).getPercentMP();
               }

               bxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(12000025);
               bofxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.percent_mp = this.percent_mp
                        + bxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxx).getPercentMP();
               }

               bxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(12120008);
               bofxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.TER = this.TER + bxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxx).getTERRate();
                  this.ASR = this.ASR + bxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxx).getASRRate();
               }
               break;
            case 1510:
            case 1511:
            case 1512:
               Skill bxxxxxxxxxxxxxx = SkillFactory.getSkill(15100007);
               int bofxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxx > 0) {
                  this.percent_hp = this.percent_hp + bxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxx).getPercentHP();
               }

               bxxxxxxxxxxxxxx = SkillFactory.getSkill(15120007);
               bofxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxx > 0) {
                  this.percent_hp = this.percent_hp + bxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxx).getPercentHP();
               }

               bxxxxxxxxxxxxxx = SkillFactory.getSkill(15110023);
               bofxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxx > 0) {
                  this.ASR = this.ASR + bxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxx).getASRRate();
                  this.TER = this.TER + bxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxx).getTERRate();
               }
               break;
            case 2112:
               Skill bx = SkillFactory.getSkill(21120001);
               int bof = chra.getTotalSkillLevel(bx);
               if (bof > 0) {
                  eff = bx.getEffect(bof);
                  this.watk = this.watk + eff.getX();
                  this.trueMastery = this.trueMastery + eff.getMastery();
                  this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                        + eff.getCriticalMin());
               }
               break;
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
               this.magic = this.magic + chra.getTotalSkillLevel(SkillFactory.getSkill(22000000));
               Skill bxx = SkillFactory.getSkill(22000014);
               int bofx = chra.getTotalSkillLevel(bxx);
               if (bofx > 0) {
                  eff = bxx.getEffect(bofx);
                  this.percent_mp = this.percent_mp + eff.getPercentMP();
               }

               bxx = SkillFactory.getSkill(22140019);
               bofx = chra.getTotalSkillLevel(bxx);
               if (bofx > 0) {
                  eff = bxx.getEffect(bofx);
                  this.TER = this.TER + eff.getTERRate();
                  this.ASR = this.ASR + eff.getASRRate();
               }

               bxx = SkillFactory.getSkill(22150000);
               bofx = chra.getTotalSkillLevel(bxx);
               if (bofx > 0) {
                  eff = bxx.getEffect(bofx);
                  this.mpconPercent = this.mpconPercent + (eff.getX() - 100);
                  this.dam_r = this.dam_r * (eff.getY() / 100.0);
                  this.bossdam_r = this.bossdam_r * (eff.getY() / 100.0);
               }

               bxx = SkillFactory.getSkill(22160000);
               bofx = chra.getTotalSkillLevel(bxx);
               if (bofx > 0) {
                  eff = bxx.getEffect(bofx);
                  this.dam_r = this.dam_r * ((eff.getDamage() + 100.0) / 100.0);
                  this.bossdam_r = this.bossdam_r * ((eff.getDamage() + 100.0) / 100.0);
               }

               bxx = SkillFactory.getSkill(22170001);
               bofx = chra.getTotalSkillLevel(bxx);
               if (bofx > 0) {
                  eff = bxx.getEffect(bofx);
                  this.magic = this.magic + eff.getX();
                  this.trueMastery = this.trueMastery + eff.getMastery();
                  this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                        + eff.getCriticalMin());
               }
               break;
            case 2400:
            case 2410:
            case 2411:
            case 2412:
               Skill bxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(24001002);
               int bofxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxx);
                  this.speed = this.speed + eff.getPassiveSpeed();
                  this.jump = this.jump + eff.getPassiveJump();
               }

               bxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(24000003);
               bofxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxx);
                  this.evaR = this.evaR + eff.getX();
               }

               bxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(24100006);
               bofxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxx);
                  this.localluk = this.localluk + eff.getLukX();
               }

               bxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(24110007);
               bofxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxx);
                  this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + eff.getCr());
                  this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                        + eff.getCriticalMin());
               }

               bxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(20030204);
               bofxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxx);
                  this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + eff.getCr());
                  this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                        + eff.getCriticalMin());
               }

               bxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(20030206);
               bofxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxx);
                  this.localdex = this.localdex + eff.getDexX();
               }

               bxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(24111002);
               bofxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxx);
                  this.localluk = this.localluk + eff.getLukX();
               }
               break;
            case 2500:
            case 2510:
            case 2511:
            case 2512:
               Skill bxxxxxxxxxxxxxxxx = SkillFactory.getSkill(25000105);
               int bofxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
                  this.percent_mp = this.percent_mp + eff.getPercentMP();
               }

               bxxxxxxxxxxxxxxxx = SkillFactory.getSkill(25110108);
               bofxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxx);
                  this.ASR = this.ASR + eff.getASRRate();
                  this.TER = this.TER + eff.getTERRate();
               }
               break;
            case 3001:
            case 3100:
            case 3110:
            case 3111:
            case 3112:
               this.mpRecoverProp = 100;
               Skill bxxxxxxxxxxxxxxx = SkillFactory.getSkill(31000003);
               int bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  this.percent_hp = this.percent_hp + bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx).getPercentHP();
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(31100007);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx);
                  this.damageIncrease.put(31000004, eff.getDAMRate());
                  this.damageIncrease.put(31001006, eff.getDAMRate());
                  this.damageIncrease.put(31001007, eff.getDAMRate());
                  this.damageIncrease.put(31001008, eff.getDAMRate());
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(31121005);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  this.percent_hp = this.percent_hp + bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx).getPercentHP();
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(31100005);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx);
                  this.localstr = this.localstr + eff.getStrX();
                  this.localdex = this.localdex + eff.getDexX();
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(31100010);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx);
                  this.damageIncrease.put(31000004, eff.getX());
                  this.damageIncrease.put(31001006, eff.getX());
                  this.damageIncrease.put(31001007, eff.getX());
                  this.damageIncrease.put(31001008, eff.getX());
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(31111007);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx);
                  this.dam_r = this.dam_r * ((eff.getDAMRate() + 100.0) / 100.0);
                  this.bossdam_r = this.bossdam_r * ((eff.getDAMRate() + 100.0) / 100.0);
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(31110008);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx);
                  this.evaR = this.evaR + eff.getX();
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(31110009);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx);
                  this.mpRecover++;
                  this.mpRecoverProp = this.mpRecoverProp + eff.getProb();
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(31110004);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx);
                  this.TER = this.TER + eff.getTERRate();
                  this.ASR = this.ASR + eff.getASRRate();
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(31111006);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx);
                  this.dam_r = this.dam_r * ((eff.getX() + 100.0) / 100.0);
                  this.bossdam_r = this.bossdam_r * ((eff.getX() + 100.0) / 100.0);
                  this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + eff.getY());
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(31121006);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  this.ignoreTargetDEF = (int) (this.ignoreTargetDEF + (100 - this.ignoreTargetDEF)
                        * (bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx).getIgnoreMob() / 100.0));
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(31120011);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx);
                  this.damageIncrease.put(31000004, eff.getX());
                  this.damageIncrease.put(31001006, eff.getX());
                  this.damageIncrease.put(31001007, eff.getX());
                  this.damageIncrease.put(31001008, eff.getX());
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(31120008);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx);
                  this.watk = this.watk + eff.getAttackX();
                  this.trueMastery = this.trueMastery + eff.getMastery();
                  this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                        + eff.getCriticalMin());
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(31120010);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  this.percent_wdef = (int) (this.percent_wdef + bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx).getT());
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(30010112);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx);
                  this.bossdam_r = this.bossdam_r + eff.getBossDamage();
                  this.mpRecover = this.mpRecover + eff.getX();
                  this.mpRecoverProp = this.mpRecoverProp + eff.getBossDamage();
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(30010185);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx);
                  chra.getTrait(MapleTrait.MapleTraitType.will)
                        .addLocalExp(GameConstants.getTraitExpNeededForLevel(eff.getY()));
                  chra.getTrait(MapleTrait.MapleTraitType.charisma)
                        .addLocalExp(GameConstants.getTraitExpNeededForLevel(eff.getZ()));
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(30010111);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx);
                  this.hpRecoverPercent = this.hpRecoverPercent + eff.getX();
                  this.hpRecoverProp = this.hpRecoverProp + eff.getProb();
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(31120009);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxx);
                  this.DAMreduceR += (int) (5.0 + 0.5 * bofxxxxxxxxxxxxxx);
               }

               bxxxxxxxxxxxxxxx = SkillFactory.getSkill(31110009);
               bofxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxx > 0) {
                  final int DFRecovery = 2 * bofxxxxxxxxxxxxxx;
                  if (chra.getDFRecoveryTimer() != null) {
                     chra.getDFRecoveryTimer().cancel();
                     chra.getDFRecoveryTimer().purge();
                  }

                  chra.setDFRecoveryTimer(new Timer());
                  TimerTask healTask = new TimerTask() {
                     @Override
                     public void run() {
                        if (chra.getStat().getMp() < chra.getStat().getMaxMp() && chra.isAlive()) {
                           chra.addMP(DFRecovery);
                        }
                     }
                  };
                  chra.getDFRecoveryTimer().scheduleAtFixedRate(healTask, 4000L, 4000L);
               }
               break;
            case 3122:
               Skill bxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(31221008);
               int bofxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(31221008);
               if (bofxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.percent_hp = this.percent_hp
                        + bxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxx).getPercentHP();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(31220047);
               bofxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(31220047);
               if (bofxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.TER = this.TER + bxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxx).getTERRate();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(31220048);
               bofxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(31220048);
               if (bofxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.ASR = this.ASR + bxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxx).getTERRate();
               }
               break;
            case 3210:
            case 3211:
            case 3212:
               Skill bxxxxx = SkillFactory.getSkill(32100008);
               int bofxxxx = chra.getTotalSkillLevel(bxxxxx);
               if (bofxxxx > 0) {
                  this.percent_hp = this.percent_hp + bxxxxx.getEffect(bofxxxx).getPercentHP();
               }

               bxxxxx = SkillFactory.getSkill(32120015);
               bofxxxx = chra.getTotalSkillLevel(bxxxxx);
               if (bofxxxx > 0) {
                  this.ASR = this.ASR + bxxxxx.getEffect(bofxxxx).getASRRate();
                  this.TER = this.TER + bxxxxx.getEffect(bofxxxx).getTERRate();
               }

               bxxxxx = SkillFactory.getSkill(32110001);
               bofxxxx = chra.getTotalSkillLevel(bxxxxx);
               if (bofxxxx > 0) {
                  eff = bxxxxx.getEffect(bofxxxx);
                  this.dam_r = this.dam_r * ((eff.getDAMRate() + 100.0) / 100.0);
                  this.bossdam_r = this.bossdam_r * ((eff.getDAMRate() + 100.0) / 100.0);
                  this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                        + eff.getCriticalMin());
               }

               bxxxxx = SkillFactory.getSkill(32120013);
               bofxxxx = chra.getTotalSkillLevel(bxxxxx);
               if (bofxxxx > 0) {
                  this.magic = this.magic + bxxxxx.getEffect(bofxxxx).getMagicX();
               }

               bxxxxx = SkillFactory.getSkill(32120014);
               bofxxxx = chra.getTotalSkillLevel(bxxxxx);
               if (bofxxxx > 0) {
                  this.evaR = this.evaR + bxxxxx.getEffect(bofxxxx).getER();
               }

               bxxxxx = SkillFactory.getSkill(32120009);
               bofxxxx = chra.getTotalSkillLevel(bxxxxx);
               if (bofxxxx > 0) {
                  this.percent_hp = this.percent_hp + bxxxxx.getEffect(bofxxxx).getPercentHP();
               }

               bxxxxx = SkillFactory.getSkill(32121010);
               bofxxxx = chra.getTotalSkillLevel(bxxxxx);
               if (bofxxxx > 0) {
                  this.percent_hp = this.percent_hp + bxxxxx.getEffect(bofxxxx).getPercentHP();
                  this.percent_mp = this.percent_mp + bxxxxx.getEffect(bofxxxx).getPercentMP();
               }
               break;
            case 3300:
            case 3310:
            case 3311:
            case 3312:
               Skill bxxxx = SkillFactory.getSkill(33120000);
               int bofxxx = chra.getTotalSkillLevel(bxxxx);
               if (bofxxx > 0) {
                  eff = bxxxx.getEffect(bofxxx);
                  this.watk = this.watk + eff.getX();
                  this.trueMastery = this.trueMastery + eff.getMastery();
                  this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                        + eff.getCriticalMin());
               }

               bxxxx = SkillFactory.getSkill(33100014);
               bofxxx = chra.getTotalSkillLevel(bxxxx);
               if (bofxxx > 0) {
                  eff = bxxxx.getEffect(bofxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
               }

               bxxxx = SkillFactory.getSkill(33111007);
               bofxxx = chra.getTotalSkillLevel(bxxxx);
               if (bofxxx > 0) {
                  eff = bxxxx.getEffect(bofxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
               }

               bxxxx = SkillFactory.getSkill(33110000);
               bofxxx = chra.getTotalSkillLevel(bxxxx);
               if (bofxxx > 0) {
                  eff = bxxxx.getEffect(bofxxx);
                  this.dam_r = this.dam_r * ((eff.getDamage() + 100.0) / 100.0);
                  this.bossdam_r = this.bossdam_r * ((eff.getDamage() + 100.0) / 100.0);
               }

               bxxxx = SkillFactory.getSkill(33120010);
               bofxxx = chra.getTotalSkillLevel(bxxxx);
               if (bofxxx > 0) {
                  eff = bxxxx.getEffect(bofxxx);
                  this.ignoreTargetDEF = (int) (this.ignoreTargetDEF
                        + (100 - this.ignoreTargetDEF) * (eff.getIgnoreMob() / 100.0));
                  this.evaR = this.evaR + eff.getER();
               }

               bxxxx = SkillFactory.getSkill(32110001);
               bofxxx = chra.getTotalSkillLevel(bxxxx);
               if (bofxxx > 0) {
                  eff = bxxxx.getEffect(bofxxx);
                  this.dam_r = this.dam_r * ((eff.getDAMRate() + 100.0) / 100.0);
                  this.bossdam_r = this.bossdam_r * ((eff.getDAMRate() + 100.0) / 100.0);
               }

               bxxxx = SkillFactory.getSkill(33120044);
               bofxxx = chra.getTotalSkillLevel(bxxxx);
               if (bofxxx > 0) {
                  eff = bxxxx.getEffect(bofxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
               }

               bxxxx = SkillFactory.getSkill(33120015);
               bofxxx = chra.getTotalSkillLevel(bxxxx);
               if (bofxxx > 0) {
                  eff = bxxxx.getEffect(bofxxx);
                  this.TER = this.TER + eff.getTERRate();
                  this.ASR = this.ASR + eff.getASRRate();
               }

               bxxxx = SkillFactory.getSkill(33101005);
               bofxxx = chra.getTotalSkillLevel(bxxxx);
               if (bofxxx > 0) {
                  eff = bxxxx.getEffect(bofxxx);
                  this.percent_mp = this.percent_mp + eff.getPercentMP();
               }
               break;
            case 3510:
            case 3511:
            case 3512:
               Skill bxxxxxx = SkillFactory.getSkill(35100000);
               int bofxxxxx = chra.getTotalSkillLevel(bxxxxxx);
               if (bofxxxxx > 0) {
                  this.watk = this.watk + bxxxxxx.getEffect(bofxxxxx).getAttackX();
               }

               bxxxxxx = SkillFactory.getSkill(35110018);
               bofxxxxx = chra.getTotalSkillLevel(bxxxxxx);
               if (bofxxxxx > 0) {
                  this.percent_hp = this.percent_hp + bxxxxxx.getEffect(bofxxxxx).getPercentHP();
                  this.percent_mp = this.percent_mp + bxxxxxx.getEffect(bofxxxxx).getPercentMP();
               }

               bxxxxxx = SkillFactory.getSkill(35120000);
               bofxxxxx = chra.getTotalSkillLevel(bxxxxxx);
               if (bofxxxxx > 0) {
                  this.trueMastery = this.trueMastery + bxxxxxx.getEffect(bofxxxxx).getMastery();
               }

               bxxxxxx = SkillFactory.getSkill(35100011);
               bofxxxxx = chra.getTotalSkillLevel(bxxxxxx);
               if (bofxxxxx > 0) {
                  eff = bxxxxxx.getEffect(bofxxxxx);
                  this.localstr = this.localstr + eff.getStrX();
                  this.localdex = this.localdex + eff.getDexX();
               }
               break;
            case 3711:
            case 3712:
               Skill bxxxxxxx = SkillFactory.getSkill(37110008);
               int bofxxxxxx = chra.getTotalSkillLevel(bxxxxxxx);
               if (bofxxxxxx > 0) {
                  this.ASR = this.ASR + bxxxxxxx.getEffect(bofxxxxxx).getASRRate();
                  this.TER = this.TER + bxxxxxxx.getEffect(bofxxxxxx).getTERRate();
               }

               bxxxxxxx = SkillFactory.getSkill(37120009);
               bofxxxxxx = chra.getTotalSkillLevel(bxxxxxxx);
               if (bofxxxxxx > 0) {
                  this.ASR = this.ASR + bxxxxxxx.getEffect(bofxxxxxx).getASRRate();
                  this.TER = this.TER + bxxxxxxx.getEffect(bofxxxxxx).getTERRate();
               }
               break;
            case 5000:
            case 5100:
            case 5110:
            case 5111:
            case 5112:
               Skill bxxx = SkillFactory.getSkill(51000001);
               int bofxx = chra.getTotalSkillLevel(bxxx);
               if (bofxx > 0) {
                  eff = bxxx.getEffect(bofxx);
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
                  this.percent_wdef = this.percent_wdef + eff.getX();
                  this.percent_mdef = this.percent_mdef + eff.getX();
               }

               bxxx = SkillFactory.getSkill(51000002);
               bofxx = chra.getTotalSkillLevel(bxxx);
               if (bofxx > 0) {
                  eff = bxxx.getEffect(bofxx);
                  this.accuracy = this.accuracy + eff.getAccX();
                  this.speed = this.speed + eff.getPassiveSpeed();
                  this.jump = this.jump + eff.getPassiveJump();
               }

               bxxx = SkillFactory.getSkill(51100000);
               bofxx = chra.getTotalSkillLevel(bxxx);
               if (bofxx > 0) {
                  eff = bxxx.getEffect(bofxx);
                  this.damageIncrease.put(5001002, eff.getX());
                  this.damageIncrease.put(5001003, eff.getY());
                  this.localstr = this.localstr + eff.getStrX();
                  this.localdex = this.localdex + eff.getDexX();
               }

               bxxx = SkillFactory.getSkill(51120002);
               bofxx = chra.getTotalSkillLevel(bxxx);
               if (bofxx > 0) {
                  eff = bxxx.getEffect(bofxx);
                  this.watk = this.watk + eff.getAttackX();
                  this.damageIncrease.put(51100002, Integer.valueOf(eff.getDamage()));
               }

               bxxx = SkillFactory.getSkill(51110000);
               bofxx = chra.getTotalSkillLevel(bxxx);
               if (bofxx > 0) {
                  eff = bxxx.getEffect(bofxx);
                  final int mpToHeal = eff.getMp();
                  final int hpToHeal = eff.getHp();
                  int delay = 4000;
                  if (chra.getDFRecoveryTimer() != null) {
                     chra.getDFRecoveryTimer().cancel();
                     chra.getDFRecoveryTimer().purge();
                  }

                  chra.setDFRecoveryTimer(new Timer());
                  TimerTask healTask = new TimerTask() {
                     @Override
                     public void run() {
                        if (chra.getStat().getMp() < chra.getStat().getCurrentMaxMp(chra) && chra.isAlive()) {
                           chra.healMP(mpToHeal);
                        }

                        if (chra.getStat().getHp() < chra.getStat().getCurrentMaxHp(chra) && chra.isAlive()) {
                           chra.healHP(hpToHeal);
                        }
                     }
                  };
                  chra.getDFRecoveryTimer().scheduleAtFixedRate(healTask, 4000L, 4000L);
               }

               bxxx = SkillFactory.getSkill(51110001);
               bofxx = chra.getTotalSkillLevel(bxxx);
               if (bofxx > 0) {
                  eff = bxxx.getEffect(bofxx);
                  this.localstr = this.localstr + eff.getStrX();
               }

               bxxx = SkillFactory.getSkill(51110002);
               bofxx = chra.getTotalSkillLevel(bxxx);
               if (bofxx > 0) {
                  eff = bxxx.getEffect(bofxx);
                  this.ASR = this.ASR + eff.getX();
                  this.percent_atk = this.percent_atk + eff.getX();
                  this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                        + eff.getCriticalMin());
               }

               bxxx = SkillFactory.getSkill(51110009);
               bofxx = chra.getTotalSkillLevel(bxxx);
               if (bofxx > 0) {
                  eff = bxxx.getEffect(bofxx);
                  this.TER = this.TER + eff.getTERRate();
                  this.ASR = this.ASR + eff.getASRRate();
               }

               bxxx = SkillFactory.getSkill(51120000);
               bofxx = chra.getTotalSkillLevel(bxxx);
               if (bofxx > 0) {
                  eff = bxxx.getEffect(bofxx);
                  this.ignoreTargetDEF = (int) (this.ignoreTargetDEF
                        + (100 - this.ignoreTargetDEF) * (eff.getIgnoreMob() / 100.0));
               }

               bxxx = SkillFactory.getSkill(51120001);
               bofxx = chra.getTotalSkillLevel(bxxx);
               if (bofxx > 0) {
                  eff = bxxx.getEffect(bofxx);
                  this.watk = this.watk + bxxx.getEffect(bofxx).getX();
                  this.trueMastery = this.trueMastery + eff.getMastery();
                  this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                        + eff.getCriticalMin());
               }

               bxxx = SkillFactory.getSkill(51120003);
               bofxx = chra.getTotalSkillLevel(bxxx);
               if (bofxx > 0) {
                  eff = bxxx.getEffect(bofxx);
                  this.DAMreduceR = (int) (this.DAMreduceR + eff.getT());
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
               }
               break;
            case 6300:
            case 6310:
            case 6311:
            case 6312:
               Skill bxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(63000007);
               int bofxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxx > 0) {
                  this.percent_hp = this.percent_hp
                        + bxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxx).getPercentHP();
               }

               bxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(63110014);
               bofxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxx > 0) {
                  this.ASR = this.ASR + bxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxx).getASRRate();
                  this.TER = this.TER + bxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxx).getTERRate();
               }

               bxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(63110015);
               bofxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxx > 0) {
                  this.percent_mp = this.percent_mp
                        + bxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxx).getPercentMP();
               }

               bxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(63120014);
               bofxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxx > 0) {
                  this.percent_hp = this.percent_hp
                        + bxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxx).getPercentHP();
               }
               break;
            case 6411:
            case 6412:
               Skill bxxxxxxxxxxxxx = SkillFactory.getSkill(64110006);
               int bofxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
               }
               break;
            case 6510:
            case 6511:
            case 6512:
               Skill bxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(65100005);
               int bofxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxx > 0) {
                  this.ASR = this.ASR + bxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxx).getASRRate();
                  this.TER = this.TER + bxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxx).getTERRate();
               }
               break;
            case 10112:
               ZeroInfo zeroInfo = chra.getZeroInfo();
               if (zeroInfo != null) {
                  if (!zeroInfo.isBeta()) {
                     SecondaryStatEffect effect = chra.getSkillLevelData(101100203);
                     if (effect != null) {
                        this.TER = this.TER + effect.getTERRate();
                        this.ASR = this.ASR + effect.getASRRate();
                     }
                  } else {
                     SecondaryStatEffect effect = chra.getSkillLevelData(101100102);
                     if (effect != null) {
                        this.TER = this.TER + effect.getTERRate();
                        this.ASR = this.ASR + effect.getASRRate();
                     }
                  }
               }
               break;
            case 15200:
            case 15210:
            case 15211:
            case 15212:
               Skill bxxxxxxxxxxxx = SkillFactory.getSkill(152000007);
               int bofxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxx);
               if (bofxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentMP();
                  this.percent_mp = this.percent_mp + eff.getPercentMP();
               }

               bxxxxxxxxxxxx = SkillFactory.getSkill(152110011);
               bofxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxx);
               if (bofxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentMP();
                  this.percent_mp = this.percent_mp + eff.getPercentMP();
               }
               break;
            case 15500:
            case 15510:
            case 15511:
            case 15512:
               Skill bxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(155100010);
               int bofxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(155100010);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.percent_hp = this.percent_hp
                        + bxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxx).getPercentHP();
                  this.percent_mp = this.percent_mp
                        + bxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxx).getPercentMP();
               }
         }
      }

      Skill bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(80000000);
      int bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
      if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
         eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
         this.localstr = this.localstr + eff.getStrX();
         this.localdex = this.localdex + eff.getDexX();
         this.localint_ = this.localint_ + eff.getIntX();
         this.localluk = this.localluk + eff.getLukX();
         this.percent_hp = (int) (this.percent_hp + eff.getHpR());
         this.percent_mp = (int) (this.percent_mp + eff.getMpR());
      }

      bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(80000001);
      bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
      if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
         eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
         this.bossdam_r = this.bossdam_r + eff.getBossDamage();
      }

      bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(80000420);
      bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getHyperStat().getStat().getSkillLevel(80000420);
      if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
         double temp = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getExpRPerM();
         this.plusExp += temp;
      }

      if (GameConstants.isAdventurer(chra.getJob())) {
         bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(74);
         bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
         if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
            this.levelBonus = this.levelBonus
                  + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getX();
         }

         bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(80);
         bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
         if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
            this.levelBonus = this.levelBonus
                  + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getX();
         }

         bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(10074);
         bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
         if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
            this.levelBonus = this.levelBonus
                  + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getX();
         }

         bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(10080);
         bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
         if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
            this.levelBonus = this.levelBonus
                  + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getX();
         }

         bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(110);
         bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
         if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
            eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
            this.localstr = this.localstr + eff.getStrX();
            this.localdex = this.localdex + eff.getDexX();
            this.localint_ = this.localint_ + eff.getIntX();
            this.localluk = this.localluk + eff.getLukX();
            this.percent_hp = (int) (this.percent_hp + eff.getHpR());
            this.percent_mp = (int) (this.percent_mp + eff.getMpR());
         }

         bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(10110);
         bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
         if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
            eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
            this.localstr = this.localstr + eff.getStrX();
            this.localdex = this.localdex + eff.getDexX();
            this.localint_ = this.localint_ + eff.getIntX();
            this.localluk = this.localluk + eff.getLukX();
            this.percent_hp = (int) (this.percent_hp + eff.getHpR());
            this.percent_mp = (int) (this.percent_mp + eff.getMpR());
         }
      }

      bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(GameConstants.getBOF_ForJob(chra.getJob()));
      bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
      if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
         eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
         this.watk = this.watk + eff.getX();
         this.magic = this.magic + eff.getY();
         this.accuracy = this.accuracy + eff.getX();
      }

      bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(GameConstants.getEmpress_ForJob(chra.getJob()));
      bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
      if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
         eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
         this.watk = this.watk + eff.getX();
         this.magic = this.magic + eff.getY();
         this.accuracy = this.accuracy + eff.getZ();
      }

      synchronized (this.damageIncrease) {
         switch (chra.getJob()) {
            case 110:
            case 111:
            case 112:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(1100009);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.damageIncrease.put(1001004, eff.getX());
                  this.damageIncrease.put(1001005, eff.getY());
                  this.localstr = this.localstr + eff.getStrX();
                  this.localdex = this.localdex + eff.getDexX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(1110009);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.dam_r = this.dam_r * (eff.getDamage() / 100.0);
                  this.bossdam_r = this.bossdam_r * (eff.getDamage() / 100.0);
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(1120012);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.ignoreTargetDEF = (int) (this.ignoreTargetDEF
                        + (100 - this.ignoreTargetDEF)
                              * (bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getIgnoreMob()
                                    / 100.0));
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(1120013);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.watk = this.watk + eff.getAttackX();
                  this.damageIncrease.put(1100002, Integer.valueOf(eff.getDamage()));
               }
               break;
            case 120:
            case 121:
            case 122:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(1200009);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.damageIncrease.put(1001004, eff.getX());
                  this.damageIncrease.put(1001005, eff.getY());
                  this.localstr = this.localstr + eff.getStrX();
                  this.localdex = this.localdex + eff.getDexX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(1220006);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.ASR = this.ASR
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getASRRate();
                  this.TER = this.TER
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getTERRate();
               }
               break;
            case 130:
            case 131:
            case 132:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(1300009);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.damageIncrease.put(1001004, eff.getX());
                  this.damageIncrease.put(1001005, eff.getY());
                  this.localstr = this.localstr + eff.getStrX();
                  this.localdex = this.localdex + eff.getDexX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(1310009);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + eff.getCr());
                  this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                        + eff.getCriticalMin());
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(1320006);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.dam_r = this.dam_r * ((eff.getDamage() + 100.0) / 100.0);
                  this.bossdam_r = this.bossdam_r * ((eff.getDamage() + 100.0) / 100.0);
               }
               break;
            case 210:
            case 211:
            case 212:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2100007);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.localint_ = this.localint_
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getIntX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2110000);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.dotTime = this.dotTime + eff.getX();
                  this.dot = this.dot + eff.getZ();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2110001);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.mpconPercent = this.mpconPercent + (eff.getX() - 100);
                  this.dam_r = this.dam_r * (eff.getY() / 100.0);
                  this.bossdam_r = this.bossdam_r * (eff.getY() / 100.0);
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2121003);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.damageIncrease.put(2111003, eff.getX());
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2121009);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.magic = this.magic + eff.getMagicX();
                  this.BuffUP_Skill = this.BuffUP_Skill + eff.getX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2121005);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.TER = this.TER
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getTERRate();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2121009);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.magic = this.magic
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getMagicX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2120010);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.dam_r = this.dam_r * ((eff.getX() * eff.getY() + 100.0) / 100.0);
                  this.bossdam_r = this.bossdam_r * ((eff.getX() * eff.getY() + 100.0) / 100.0);
                  this.ignoreTargetDEF = (int) (this.ignoreTargetDEF
                        + (100 - this.ignoreTargetDEF) * (eff.getIgnoreMob() / 100.0));
               }
               break;
            case 220:
            case 221:
            case 222:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2200007);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.localint_ = this.localint_
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getIntX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2210000);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.dot = this.dot + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getZ();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2210001);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.mpconPercent = this.mpconPercent + (eff.getCostMpR() - 100);
                  this.dam_r = this.dam_r * (eff.getY() / 100.0);
                  this.bossdam_r = this.bossdam_r * (eff.getY() / 100.0);
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2221009);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.magic = this.magic + eff.getMagicX();
                  this.BuffUP_Skill = this.BuffUP_Skill + eff.getX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2221005);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.TER = this.TER
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getTERRate();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2221009);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.magic = this.magic
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getMagicX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2220010);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.dam_r = this.dam_r * ((eff.getX() * eff.getY() + 100.0) / 100.0);
                  this.bossdam_r = this.bossdam_r * ((eff.getX() * eff.getY() + 100.0) / 100.0);
                  this.ignoreTargetDEF = (int) (this.ignoreTargetDEF
                        + (100 - this.ignoreTargetDEF) * (eff.getIgnoreMob() / 100.0));
               }
               break;
            case 230:
            case 231:
            case 232:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2300007);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.localint_ = this.localint_
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getIntX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2310008);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getCr());
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2321010);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.magic = this.magic + eff.getMagicX();
                  this.BuffUP_Skill = this.BuffUP_Skill + eff.getX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2321010);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.magic = this.magic
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getMagicX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2320005);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.ASR = this.ASR
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getASRRate();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(2320011);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.dam_r = this.dam_r * ((eff.getX() * eff.getY() + 100.0) / 100.0);
                  this.bossdam_r = this.bossdam_r * ((eff.getX() * eff.getY() + 100.0) / 100.0);
                  this.ignoreTargetDEF = (int) (this.ignoreTargetDEF
                        + (100 - this.ignoreTargetDEF) * (eff.getIgnoreMob() / 100.0));
               }
               break;
            case 300:
            case 310:
            case 311:
            case 312:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(3000002);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.defRange = this.defRange
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getRange();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(3100006);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.damageIncrease.put(3001004, eff.getX());
                  this.damageIncrease.put(3001005, eff.getY());
                  this.localstr = this.localstr + eff.getStrX();
                  this.localdex = this.localdex + eff.getDexX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(3110007);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.evaR = this.evaR + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getER();
                  this.percent_hp = this.percent_hp
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getPercentHP();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(3120005);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.trueMastery = this.trueMastery + eff.getMastery();
                  this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                        + eff.getCriticalMin());
               }
               break;
            case 320:
            case 321:
            case 322:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(3000002);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.defRange = this.defRange
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getRange();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(3200006);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.localstr = this.localstr + eff.getStrX();
                  this.localdex = this.localdex + eff.getDexX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(3220010);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.damageIncrease.put(3211006,
                        bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getDamage() - 150);
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(3210007);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.evaR = this.evaR + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getER();
                  this.percent_hp = this.percent_hp
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getPercentHP();
               }
               break;
            case 331:
            case 332:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(1298);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.ASR = this.ASR
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getASRRate();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(3311012);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.ASR = this.ASR
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getASRRate();
                  this.TER = this.TER
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getTERRate();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(3310007);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.percent_hp = this.percent_hp
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getPercentHP();
               }
               break;
            case 422:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(4221007);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.damageIncrease.put(4201005, eff.getDAMRate());
                  this.damageIncrease.put(4211002, eff.getDAMRate());
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(4210012);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.mesoBuff_ = this.mesoBuff_ + eff.getMesoRate();
                  this.damageIncrease.put(4211006, eff.getX());
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(4210013);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.ASR = this.ASR + eff.getASRRate();
                  this.TER = this.TER + eff.getTERRate();
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(4220045);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.pickRate = this.pickRate + eff.getProb();
               }
               break;
            case 433:
            case 434:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(4330007);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.hpRecoverProp = this.hpRecoverProp + eff.getProb();
                  this.hpRecoverPercent = this.hpRecoverPercent + eff.getX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(4330009);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.evaR = this.evaR + eff.getER();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(4341002);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.damageIncrease.put(4311002, eff.getDAMRate());
                  this.damageIncrease.put(4311003, eff.getDAMRate());
                  this.damageIncrease.put(4321000, eff.getDAMRate());
                  this.damageIncrease.put(4321001, eff.getDAMRate());
                  this.damageIncrease.put(4331000, eff.getDAMRate());
                  this.damageIncrease.put(4331004, eff.getDAMRate());
                  this.damageIncrease.put(4331005, eff.getDAMRate());
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(4341006);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.evaR = this.evaR + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getER();
               }
               break;
            case 510:
            case 511:
            case 512:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(5120014);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.ignoreTargetDEF = (int) (this.ignoreTargetDEF
                        + (100 - this.ignoreTargetDEF)
                              * (bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getIgnoreMob()
                                    / 100.0));
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(5100009);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(5100010);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.localstr = this.localstr + eff.getStrX();
                  this.localdex = this.localdex + eff.getDexX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(5121015);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.trueMastery = this.trueMastery + eff.getMastery();
                  this.TER = this.TER + eff.getTERRate();
                  this.ASR = this.ASR + eff.getASRRate();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(5100013);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  int delay = eff.getY() * 1000;
                  if (chra.getDFRecoveryTimer() != null) {
                     chra.getDFRecoveryTimer().cancel();
                     chra.getDFRecoveryTimer().purge();
                  }

                  chra.setDFRecoveryTimer(new Timer());
                  TimerTask healTask = new TimerTask() {
                     @Override
                     public void run() {
                        long hpToHeal = chra.getStat().getCurrentMaxHp(chra);
                        long mpToHeal = chra.getStat().getCurrentMaxMp(chra);
                        if (chra.getStat().getMp() < chra.getStat().getMaxMp() && chra.isAlive()) {
                           chra.healMP(mpToHeal);
                        }

                        if (chra.getStat().getHp() < chra.getStat().getMaxHp() && chra.isAlive()) {
                           chra.healHP(hpToHeal);
                        }
                     }
                  };
                  chra.getDFRecoveryTimer().scheduleAtFixedRate(healTask, delay, delay);
               }
               break;
            case 520:
            case 521:
            case 522:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(5210013);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.ignoreTargetDEF = (int) (this.ignoreTargetDEF
                        + (100 - this.ignoreTargetDEF)
                              * (bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getIgnoreMob()
                                    / 100.0));
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(5210012);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.localmaxhp = this.localmaxhp + eff.getMaxHpX();
                  this.localmaxmp = this.localmaxmp + eff.getMaxMpX();
                  this.percent_wdef = this.percent_wdef + eff.getWDEFRate();
                  this.percent_mdef = this.percent_mdef + eff.getMDEFRate();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(5211006);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.dam_r = this.dam_r * (eff.getDamage() / 100.0);
                  this.bossdam_r = this.bossdam_r * (eff.getDamage() / 100.0);
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(5220019);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.localmaxhp = this.localmaxhp + eff.getMaxHpX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(5220020);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.trueMastery = this.trueMastery + eff.getMastery();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(5200009);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.localstr = this.localstr + eff.getStrX();
                  this.localdex = this.localdex + eff.getDexX();
               }
               break;
            case 1211:
            case 1212:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(12110025);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.mpconPercent = this.mpconPercent + (eff.getX() - 100);
                  this.dam_r = this.dam_r * (eff.getY() / 100.0);
                  this.bossdam_r = this.bossdam_r * (eff.getY() / 100.0);
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(12111004);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.TER = this.TER
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getTERRate();
               }
               break;
            case 1300:
            case 1310:
            case 1311:
            case 1312:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(13000001);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.defRange = this.defRange
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getRange();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(13110008);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.evaR = this.evaR + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getER();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(13110003);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.trueMastery = this.trueMastery + eff.getMastery();
                  this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                        + eff.getCriticalMin());
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(13110025);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.ASR = this.ASR + eff.getASRRate();
                  this.TER = this.TER + eff.getTERRate();
               }
               break;
            case 1400:
            case 1410:
            case 1411:
            case 1412:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(14110003);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.RecoveryUP = this.RecoveryUP + (eff.getX() - 100);
                  this.BuffUP = this.BuffUP + (eff.getY() - 100);
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(14000001);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.defRange = this.defRange
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getRange();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(14110026);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.TER = this.TER
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getTERRate();
                  this.ASR = this.ASR
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getASRRate();
               }
               break;
            case 2002:
            case 2300:
            case 2310:
            case 2311:
            case 2312:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(20020012);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.evaR = this.evaR + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getX();
                  this.accuracy = this.accuracy
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getX();
                  this.watk = this.watk + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getX();
                  this.magic = this.magic
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(20021110);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  if (eff != null) {
                     this.expBuff = this.expBuff + eff.getEXPRate();
                  }
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(20020112);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  chra.getTrait(MapleTrait.MapleTraitType.charm)
                        .addLocalExp(GameConstants.getTraitExpNeededForLevel(30));
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(23000001);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.evaR = this.evaR + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getER();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(23100008);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.localstr = this.localstr + eff.getStrX();
                  this.localdex = this.localdex + eff.getDexX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(23110004);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.evaR = this.evaR
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getProb();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(23110004);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.damageIncrease.put(23101001,
                        bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getDAMRate());
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(23121004);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.evaR = this.evaR
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getProb();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(23120009);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.watk = this.watk + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getX();
                  this.trueMastery = this.trueMastery + eff.getMastery();
                  this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                        + eff.getCriticalMin());
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(23120010);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.ignoreTargetDEF = (int) (this.ignoreTargetDEF
                        + (100 - this.ignoreTargetDEF)
                              * (bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getX() / 100.0));
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(23120011);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.damageIncrease.put(23101001,
                        bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getDAMRate());
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(23120012);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.watk = this.watk
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getAttackX();
               }
               break;
            case 2110:
            case 2111:
            case 2112:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(21101006);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.dam_r = this.dam_r * ((eff.getDAMRate() + 100.0) / 100.0);
                  this.bossdam_r = this.bossdam_r * ((eff.getDAMRate() + 100.0) / 100.0);
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(21101005);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(21110002);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.damageIncrease.put(21000004,
                        bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getW());
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(21111010);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.ignoreTargetDEF = (int) (this.ignoreTargetDEF
                        + (100 - this.ignoreTargetDEF)
                              * (bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getIgnoreMob()
                                    / 100.0));
                  this.bossdam_r = this.bossdam_r
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getBossDamage();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(21120002);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  this.damageIncrease.put(21100007,
                        bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getZ());
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(21120011);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.damageIncrease.put(21100002, eff.getDAMRate());
                  this.damageIncrease.put(21110003, eff.getDAMRate());
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(21100008);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.localstr = this.localstr + eff.getStrX();
                  this.localdex = this.localdex + eff.getDexX();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(21120004);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.DAMreduceR = this.DAMreduceR
                        + (int) (5.0 + 1.5 * Math.floor(bofxxxxxxxxxxxxxxxxxxxxxxxxxx / 2.0));
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
               }
               break;
            case 2700:
            case 2710:
            case 2711:
            case 2712:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(27000004);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.percent_mp = this.percent_mp + eff.getPercentMP();
               }
               break;
            case 3500:
            case 3510:
            case 3511:
            case 3512:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(35120018);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
                  this.percent_mp = this.percent_mp + eff.getPercentMP();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(35110014);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.damageIncrease.put(35001003, eff.getDAMRate());
                  this.damageIncrease.put(35101003, eff.getDAMRate());
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(35121006);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.damageIncrease.put(35111001, eff.getDAMRate());
                  this.damageIncrease.put(35111009, eff.getDAMRate());
                  this.damageIncrease.put(35111010, eff.getDAMRate());
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(35120001);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.damageIncrease.put(35111005, eff.getX());
                  this.damageIncrease.put(35111011, eff.getX());
                  this.damageIncrease.put(35121009, eff.getX());
                  this.damageIncrease.put(35121010, eff.getX());
                  this.damageIncrease.put(35121011, eff.getX());
                  this.summonTimeR = this.summonTimeR + eff.getY();
               }
               break;
            case 14200:
            case 14210:
            case 14211:
            case 14212:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(142000005);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(142100007);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(142111003);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.TER = this.TER + eff.getTERRate();
                  this.ASR = this.ASR + eff.getASRRate();
               }
               break;
            case 16001:
            case 16200:
            case 16210:
            case 16211:
            case 16212:
               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(162000006);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.percent_hp = this.percent_hp + eff.getPercentHP();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(162110008);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.percent_mp = this.percent_mp + eff.getPercentMP();
               }

               bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(162120028);
               bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
               if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
                  eff = bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx);
                  this.TER = this.TER
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getTERRate();
                  this.ASR = this.ASR
                        + bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getASRRate();
               }
         }
      }

      if (GameConstants.isResist(chra.getJob())) {
         bxxxxxxxxxxxxxxxxxxxxxxxxxxx = SkillFactory.getSkill(30000002);
         bofxxxxxxxxxxxxxxxxxxxxxxxxxx = chra.getTotalSkillLevel(bxxxxxxxxxxxxxxxxxxxxxxxxxxx);
         if (bofxxxxxxxxxxxxxxxxxxxxxxxxxx > 0) {
            this.RecoveryUP = this.RecoveryUP
                  + (bxxxxxxxxxxxxxxxxxxxxxxxxxxx.getEffect(bofxxxxxxxxxxxxxxxxxxxxxxxxxx).getX() - 100);
         }
      }
   }

   private void handleBuffStats(MapleCharacter chra) {
      SecondaryStatEffect eff = chra.getBuffedEffect(SecondaryStatFlag.RideVehicle);
      if (eff != null && eff.getSourceId() == 33001001) {
         this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + eff.getW());
         this.percent_hp = this.percent_hp + eff.getZ();
      }

      List<IndieTemporaryStatEntry> entrys = chra.getIndieTemporaryStats(SecondaryStatFlag.indieMHPR);
      if (entrys != null) {
         for (IndieTemporaryStatEntry indie : entrys) {
            this.percent_hp = this.percent_hp + indie.getStatValue();
         }
      }

      entrys = chra.getIndieTemporaryStats(SecondaryStatFlag.indieMMPR);
      if (entrys != null) {
         for (IndieTemporaryStatEntry indie : entrys) {
            this.percent_mp = this.percent_mp + indie.getStatValue();
         }
      }

      Integer buff = chra.getBuffedValue(SecondaryStatFlag.CrewCommandership);
      if (buff != null && (buff & 4) != 0) {
         SecondaryStatEffect e = chra.getSkillLevelData(5220019);
         if (e != null) {
            this.percent_hp = this.percent_hp + e.getX();
            this.percent_mp = this.percent_mp + e.getX();
         }
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.TerR);
      if (buff != null) {
         this.TER = this.TER + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.AsrR);
      if (buff != null) {
         this.ASR = this.ASR + buff;
      }

      entrys = chra.getIndieTemporaryStats(SecondaryStatFlag.indieAsrR);
      if (entrys != null) {
         for (IndieTemporaryStatEntry indie : entrys) {
            this.ASR = this.ASR + indie.getStatValue();
         }
      }

      entrys = chra.getIndieTemporaryStats(SecondaryStatFlag.indieEXP);
      if (entrys != null) {
         for (IndieTemporaryStatEntry indie : entrys) {
            this.expBuff = this.expBuff + indie.getStatValue();
         }
      }

      entrys = chra.getIndieTemporaryStats(SecondaryStatFlag.indieDropPer);
      if (entrys != null) {
         for (IndieTemporaryStatEntry indie : entrys) {
            this.extraDropBuff = this.extraDropBuff + indie.getStatValue();
         }
      }

      entrys = chra.getIndieTemporaryStats(SecondaryStatFlag.indieMesoAmountRate);
      if (entrys != null) {
         for (IndieTemporaryStatEntry indie : entrys) {
            this.mesoBuff_ = this.mesoBuff_ + indie.getStatValue();
         }
      }

      Integer v = chra.getBuffedValue(SecondaryStatFlag.PlusExpRate);
      if (v != null) {
         this.plusExpRate = this.plusExpRate + v.intValue();
      }

      entrys = chra.getIndieTemporaryStats(SecondaryStatFlag.indieCooltimeReduce);
      if (entrys != null) {
         for (IndieTemporaryStatEntry indie : entrys) {
            this.reduceCooltimeR = this.reduceCooltimeR + indie.getStatValue();
         }
      }

      for (ExtraAbilityStatEntry e : chra.getExtraAbilityStats()[chra.getExtraAbilitySlot()]) {
         if (e.getOption() == ExtraAbilityOption.ReduceCooltime) {
            this.reduceCooltime = this.reduceCooltime + e.getValue();
         }
      }

      if (DBConfig.isGanglim) {
         int[][] coolTimeDonate = new int[][] { { 1500, 10 }, { 2000, 15 }, { 2500, 20 }, { 3000, 30 } };
         int incReduceCoolTimeR = 0;

         for (int[] donates : coolTimeDonate) {
            int price = donates[0];
            int reduceCoolR = donates[1];
            String donateKeyValue = chra.getClient().getKeyValue("DPointAll_" + price);
            if (donateKeyValue != null) {
               int check = Integer.parseInt(donateKeyValue);
               if (check == 1) {
                  incReduceCoolTimeR = reduceCoolR;
               }
            }
         }

         this.s_reduceCooltimeR += incReduceCoolTimeR;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.Infinity);
      if (buff != null) {
         this.percent_matk = this.percent_matk + (buff - 1);
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.MaxMP);
      if (buff != null) {
         this.percent_mp = this.percent_mp + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.MaxHP);
      if (buff != null) {
         this.percent_hp = this.percent_hp + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.STR);
      if (buff != null) {
         this.localstr = this.localstr + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.DEX);
      if (buff != null) {
         this.localdex = this.localdex + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.INT);
      if (buff != null) {
         this.localint_ = this.localint_ + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.LUK);
      if (buff != null) {
         this.localluk = this.localluk + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.indieAllStat);
      if (buff != null) {
         this.localstr = this.localstr + buff;
         this.localdex = this.localdex + buff;
         this.localint_ = this.localint_ + buff;
         this.localluk = this.localluk + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.EnhancedDEF);
      if (buff != null) {
         this.wdef = this.wdef + buff;
         this.mdef = this.mdef + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.DEF);
      if (buff != null) {
         this.wdef = this.wdef + buff;
         this.mdef = this.mdef + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.BasicStatUp);
      if (buff != null) {
         double d = buff.doubleValue() / 100.0;
         this.localstr = (int) (this.localstr + d * this.str);
         this.localdex = (int) (this.localdex + d * this.dex);
         this.localluk = (int) (this.localluk + d * this.luk);
         this.localint_ = (int) (this.localint_ + d * this.int_);
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.IllusionStep);
      if (buff != null) {
         double d = buff.doubleValue() / 100.0;
         this.watk = this.watk + (int) (this.watk * d);
         this.magic = this.magic + (int) (this.magic * d);
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.ComboAbilityBuff);
      if (buff != null) {
         this.watk = this.watk + buff / 10;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.ExpBuffRate);
      if (buff != null) {
         this.expBuff = this.expBuff + buff.doubleValue();
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.MesoUp);
      if (buff != null) {
         this.mesoBuff = this.mesoBuff + buff.intValue();
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.MesoUpByItem);
      if (buff != null) {
         this.mesoBuff__ = this.mesoBuff__ + buff.doubleValue();
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.DropRate);
      if (buff != null) {
         this.dropBuff_ = this.dropBuff_ + buff.doubleValue();
      }

      Integer value;
      if ((value = chra.getBuffedValue(SecondaryStatFlag.Judgement)) != null && value == 2) {
         SecondaryStatEffect ex = chra.getBuffedEffect(SecondaryStatFlag.Judgement);
         this.dropBuff_ = this.dropBuff_ + ex.getW();
      }

      if (chra.getBuffedEffect(SecondaryStatFlag.HolySymbol) != null) {
         this.dropBuff_ = this.dropBuff_ + ((Integer) chra.getJobField("holySymbolDropR")).intValue();
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.LuckOfUnion);
      if (buff != null) {
         this.dropBuff_ = this.dropBuff_ + buff.intValue();
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.WealthOfUnion);
      if (buff != null) {
         this.itemMesoBuff = this.itemMesoBuff + buff.intValue();
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.ACC);
      if (buff != null) {
         this.accuracy = this.accuracy + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.indieACC);
      if (buff != null) {
         this.accuracy = this.accuracy + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.indiePAD);
      if (buff != null) {
         this.watk = this.watk + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.indieMAD);
      if (buff != null) {
         this.magic = this.magic + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.indiePadR);
      if (buff != null) {
         this.watk = this.watk + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.CriticalBuff);
      if (buff != null) {
         this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + buff);
         this.dam_r = this.dam_r * ((buff.intValue() + 100.0) / 100.0);
         this.bossdam_r = this.bossdam_r * ((buff.intValue() + 100.0) / 100.0);
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.EnhancedPAD);
      if (buff != null) {
         this.watk = this.watk + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.indieMadR);
      if (buff != null) {
         this.magic = this.magic + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.Speed);
      if (buff != null) {
         this.speed = this.speed + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.Jump);
      if (buff != null) {
         this.jump = this.jump + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.DashSpeed);
      if (buff != null) {
         this.speed = this.speed + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.DashJump);
      if (buff != null) {
         this.jump = this.jump + buff;
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.indieDamR);
      if (buff != null) {
         this.dam_r = this.dam_r * ((buff.doubleValue() + 100.0) / 100.0);
         this.bossdam_r = this.bossdam_r * ((buff.doubleValue() + 100.0) / 100.0);
      }

      eff = chra.getBuffedEffect(SecondaryStatFlag.Bless);
      if (eff != null) {
         this.watk = this.watk + eff.getX();
         this.magic = this.magic + eff.getY();
         this.accuracy = this.accuracy + eff.getV();
      }

      eff = chra.getBuffedEffect(SecondaryStatFlag.Combo);
      buff = chra.getBuffedValue(SecondaryStatFlag.Combo);
      if (eff != null && buff != null) {
         this.dam_r = this.dam_r * ((100.0 + (eff.getV() + eff.getDAMRate()) * (buff - 1)) / 100.0);
         this.bossdam_r = this.bossdam_r * ((100.0 + (eff.getV() + eff.getDAMRate()) * (buff - 1)) / 100.0);
      }

      eff = chra.getBuffedEffect(SecondaryStatFlag.indieSummon);
      if (eff != null && eff.getSourceId() == 35121010) {
         this.dam_r = this.dam_r * ((eff.getX() + 100.0) / 100.0);
         this.bossdam_r = this.bossdam_r * ((eff.getX() + 100.0) / 100.0);
      }

      eff = chra.getBuffedEffect(SecondaryStatFlag.Beholder);
      if (eff != null) {
         this.trueMastery = this.trueMastery + eff.getMastery();
      }

      eff = chra.getBuffedEffect(SecondaryStatFlag.PickPocket);
      if (eff != null) {
         this.pickRate = eff.getProb();
      }

      eff = chra.getBuffedEffect(SecondaryStatFlag.indieDamR);
      if (eff != null) {
         this.dam_r = this.dam_r * ((eff.getDAMRate() + 100.0) / 100.0);
         this.bossdam_r = this.bossdam_r * ((eff.getDAMRate() + 100.0) / 100.0);
      }

      eff = chra.getBuffedEffect(SecondaryStatFlag.SoulExplosion);
      if (eff != null) {
         this.dam_r = this.dam_r * (eff.getDamage() / 100.0);
         this.bossdam_r = this.bossdam_r * (eff.getDamage() / 100.0);
      }

      eff = chra.getBuffedEffect(SecondaryStatFlag.BlessingArmor);
      if (eff != null) {
         this.watk = this.watk + eff.getEnhancedWatk();
      }

      if (chra.getBuffedValue(SecondaryStatFlag.CombatOrders) != null) {
         this.combatOrders = chra.getBuffedValue(SecondaryStatFlag.CombatOrders);
      }

      int slv = chra.getInnerSkillLevel(70000046);
      if (slv > 0) {
         SecondaryStatEffect effect = SkillFactory.getSkill(70000046).getEffect(slv);
         if (effect != null) {
            this.passivePlus = effect.getPassivePlus();
         }
      }

      eff = chra.getBuffedEffect(SecondaryStatFlag.SharpEyes);
      if (eff != null) {
         this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + eff.getX());
         this.passive_sharpeye_percent = (short) (this.passive_sharpeye_percent + eff.getCriticalMax());
      }

      eff = chra.getBuffedEffect(SecondaryStatFlag.indieMonsterCollectionR);
      if (eff != null) {
         this.monsterCollectionR = this.monsterCollectionR + eff.getMonsterCollectionProp();
      }

      buff = chra.getBuffedValue(SecondaryStatFlag.RideVehicle);
      if (buff != null) {
         this.jump = 120;
         switch (buff) {
            case 1:
               this.speed = 150;
               break;
            case 2:
               this.speed = 170;
               break;
            case 3:
               this.speed = 180;
               break;
            default:
               this.speed = 200;
         }
      }
   }

   public boolean checkEquipLevels(MapleCharacter chr, int gain) {
      if (chr.isClone()) {
         return false;
      } else {
         boolean changed = false;
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         synchronized (this.equipLevelHandling) {
            for (Equip eq : new ArrayList<>(this.equipLevelHandling)) {
               if (eq != null) {
                  int lvlz = eq.getEquipLevel();
                  eq.setItemEXP(eq.getItemEXP() + gain);
                  if (eq.getEquipLevel() > lvlz) {
                     for (int i = eq.getEquipLevel() - lvlz; i > 0; i--) {
                        Map<Integer, Map<String, Integer>> inc = ii.getEquipIncrements(eq.getItemId());
                        if (inc != null && inc.containsKey(lvlz + i)) {
                           eq = ii.levelUpEquip(eq, inc.get(lvlz + i));
                        }

                        if (GameConstants.getStatFromWeapon(eq.getItemId()) == null
                              && GameConstants.getMaxLevel(eq.getItemId()) < lvlz + i
                              && Math.random() < 0.1
                              && eq.getIncSkill() <= 0
                              && ii.getEquipSkills(eq.getItemId()) != null) {
                           for (int zzz : ii.getEquipSkills(eq.getItemId())) {
                              Skill skil = SkillFactory.getSkill(zzz);
                              if (skil != null && skil.canBeLearnedBy(chr.getJob())) {
                                 eq.setIncSkill(skil.getId());
                                 chr.dropMessage(5, "Your skill has gained a levelup: " + skil.getName() + " +1");
                              }
                           }
                        }
                     }

                     changed = true;
                  }

                  chr.forceReAddItem(eq.copy(), MapleInventoryType.EQUIPPED);
               }
            }

            if (changed) {
               chr.equipChanged();
               EffectReserved e = new EffectReserved(chr.getId(), "");
               chr.send(e.encodeForLocal());
               chr.getMap().broadcastMessage(chr, e.encodeForRemote(), false);
            }

            return changed;
         }
      }
   }

   public boolean checkEquipDurabilitys(MapleCharacter chr, int gain) {
      return this.checkEquipDurabilitys(chr, gain, false);
   }

   public boolean checkEquipDurabilitys(MapleCharacter chr, int gain, boolean aboveZero) {
      if (!chr.isClone() && !chr.inPVP()) {
         synchronized (this.durabilityHandling) {
            List<Equip> all = new ArrayList<>(this.durabilityHandling);

            for (Equip item : all) {
               if (item != null && item.getPosition() >= 0 == aboveZero) {
                  item.setDurability(item.getDurability() + gain);
                  if (item.getDurability() < 0) {
                     item.setDurability(0);
                  }
               }
            }

            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

            for (Equip eqq : all) {
               if (eqq != null && eqq.getDurability() == 0 && eqq.getPosition() < 0) {
                  MapleInventoryType type = MapleInventoryType.EQUIP;
                  if (ii.isCash(eqq.getItemId())) {
                     type = MapleInventoryType.CASH_EQUIP;
                  }

                  if (chr.getInventory(type).isFull()) {
                     chr.getClient().getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                     chr.getClient().getSession().writeAndFlush(CWvsContext.InventoryPacket.getShowInventoryFull());
                     return false;
                  }

                  this.durabilityHandling.remove(eqq);
                  short pos = chr.getInventory(type).getNextFreeSlot();
                  MapleInventoryManipulator.unequip(type, chr.getClient(), eqq.getPosition(), pos);
               } else if (eqq != null) {
                  chr.forceReAddItem(eqq.copy(), MapleInventoryType.EQUIPPED);
               }
            }

            return true;
         }
      } else {
         return true;
      }
   }

   private void handlePotential(ItemOptionLevelData pot, MapleCharacter player) {
      this.localstr = this.localstr + pot.incSTR;
      this.localdex = this.localdex + pot.incDEX;
      this.localint_ = this.localint_ + pot.incINT;
      this.localluk = this.localluk + pot.incLUK;
      this.watk = this.watk + pot.incPAD;
      this.magic = this.magic + pot.incINT + pot.incMAD;
      this.speed = this.speed + pot.incSpeed;
      this.jump = this.jump + pot.incJump;
      this.accuracy = this.accuracy + pot.incACC;
      this.incAllskill = this.incAllskill + pot.incAllskill;
      this.percent_mp = this.percent_mp + pot.incMMPr;
      this.percent_str = this.percent_str + pot.incSTRr;
      this.percent_dex = this.percent_dex + pot.incDEXr;
      this.percent_int = this.percent_int + pot.incINTr;
      this.percent_luk = this.percent_luk + pot.incLUKr;
      this.percent_acc = this.percent_acc + pot.incACCr;
      this.percent_atk = this.percent_atk + pot.incPADr;
      this.percent_matk = this.percent_matk + pot.incMADr;
      this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + pot.incCr);
      if (!pot.boss) {
         this.dam_r = Math.max((double) pot.incDAMr, this.dam_r);
      } else {
         this.bossdam_r = Math.max((double) pot.incDAMr, this.bossdam_r);
      }

      this.recoverHP = this.recoverHP + pot.RecoveryHP;
      this.recoverMP = this.recoverMP + pot.RecoveryMP;
      this.RecoveryUP = this.RecoveryUP + pot.RecoveryUP;
      this.reduceCooltime = this.reduceCooltime + pot.reduceCooltime;
      if (pot.HP > 0) {
         this.hpRecover = this.hpRecover + pot.HP;
         this.hpRecoverProp = this.hpRecoverProp + pot.prop;
      }

      if (pot.MP > 0) {
         this.mpRecover = this.mpRecover + pot.MP;
         this.mpRecoverProp = this.mpRecoverProp + pot.prop;
      }

      this.mpconReduce = this.mpconReduce + pot.mpconReduce;
      this.incMesoProp = this.incMesoProp + pot.incMesoProp;
      this.incRewardProp = this.incRewardProp + pot.incRewardProp;
      if (pot.DAMreflect > 0) {
         this.DAMreflect = this.DAMreflect + pot.DAMreflect;
         this.DAMreflect_rate = this.DAMreflect_rate + pot.prop;
      }

      this.mpRestore = this.mpRestore + pot.mpRestore;
   }

   public final void handleProfessionTool(MapleCharacter chra) {
      if (chra.getProfessionLevel(92000000) > 0 || chra.getProfessionLevel(92010000) > 0) {
         synchronized (this.durabilityHandling) {
            chra.getInventory(MapleInventoryType.EQUIP)
                  .list()
                  .stream()
                  .collect(Collectors.toList())
                  .forEach(
                        item -> {
                           Equip equip = (Equip) item;
                           if (equip.getDurability() != 0 && equip.getItemId() / 10000 == 150
                                 && chra.getProfessionLevel(92000000) > 0
                                 || equip.getItemId() / 10000 == 151 && chra.getProfessionLevel(92010000) > 0) {
                              if (equip.getDurability() > 0) {
                                 this.durabilityHandling.add(equip);
                              }

                              this.harvestingTool = equip.getPosition();
                           }
                        });
         }
      }
   }

   private void CalcPassive_Mastery(MapleCharacter player) {
      if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11) == null) {
         this.passive_mastery = 0;
      } else {
         MapleWeaponType weaponType = GameConstants
               .getWeaponType(player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11).getItemId());
         boolean acc = true;
         int skil;
         switch (weaponType) {
            case BOW:
               skil = GameConstants.isKOC(player.getJob()) ? 13100000 : 3100000;
               break;
            case CLAW:
               skil = 4100000;
               break;
            case CANE:
               skil = player.getTotalSkillLevel(24120006) > 0 ? 24120006 : 24100004;
               break;
            case HANDCANNON:
               skil = 5300005;
               break;
            case KATARA:
            case DAGGER:
               skil = player.getJob() >= 430 && player.getJob() <= 434 ? 4300000 : 4200000;
               break;
            case CROSSBOW:
               skil = GameConstants.isResist(player.getJob()) ? 33100000 : 3200000;
               break;
            case AXE1H:
            case BLUNT1H:
               skil = GameConstants.isResist(player.getJob())
                     ? 31100004
                     : (GameConstants.isKOC(player.getJob()) ? 11100000 : (player.getJob() > 112 ? 1200000 : 1100000));
               break;
            case AXE2H:
            case SWORD1H:
            case SWORD2H:
            case BLUNT2H:
               skil = GameConstants.isKOC(player.getJob()) ? 11100000 : (player.getJob() > 112 ? 1200000 : 1100000);
               break;
            case POLE_ARM:
               skil = GameConstants.isAran(player.getJob()) ? 21100000 : 1300000;
               break;
            case SPEAR:
               skil = 1300000;
               break;
            case KNUCKLE:
               skil = GameConstants.isKOC(player.getJob()) ? 15100001 : 5100001;
               break;
            case GUN:
               skil = GameConstants.isResist(player.getJob()) ? 35100000 : 5200000;
               break;
            case DUAL_BOW:
               skil = 23100005;
               break;
            case WAND:
            case STAFF:
               acc = false;
               skil = GameConstants.isResist(player.getJob())
                     ? 32100006
                     : (player.getJob() <= 212
                           ? 2100006
                           : (player.getJob() <= 222 ? 2200006
                                 : (player.getJob() <= 232 ? 2300006
                                       : (player.getJob() <= 2000 ? 12100007 : 22120002))));
               break;
            default:
               this.passive_mastery = 0;
               return;
         }

         if (player.getTotalSkillLevel(skil) <= 0) {
            this.passive_mastery = 0;
         } else {
            SecondaryStatEffect eff = SkillFactory.getSkill(skil).getEffect(player.getTotalSkillLevel(skil));
            if (acc) {
               this.accuracy = this.accuracy + eff.getX();
               if (skil == 35100000) {
                  this.watk = this.watk + eff.getX();
               }
            } else {
               this.magic = this.magic + eff.getX();
            }

            this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + eff.getCr());
            this.passive_mastery = eff.getMastery();
            this.trueMastery = this.trueMastery + eff.getMastery() + weaponType.getBaseMastery();
         }
      }
   }

   private void calculateFame(MapleCharacter player) {
      player.getTrait(MapleTrait.MapleTraitType.charm).addLocalExp(player.getFame());

      for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
         player.getTrait(t).recalcLevel();
      }
   }

   private void CalcPassive_SharpEye(MapleCharacter player) {
      if (GameConstants.isDemonSlayer(player.getJob())) {
         Skill skill = SkillFactory.getSkill(30010022);
         int level = player.getTotalSkillLevel(skill);
         if (level > 0) {
            this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + skill.getEffect(level).getProb());
            this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                  + skill.getEffect(level).getCriticalMin());
         }
      } else if (GameConstants.isMercedes(player.getJob())) {
         Skill skill = SkillFactory.getSkill(20020022);
         int level = player.getTotalSkillLevel(skill);
         if (level > 0) {
            this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + skill.getEffect(level).getProb());
            this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                  + skill.getEffect(level).getCriticalMin());
         }
      } else if (GameConstants.isResist(player.getJob())) {
         Skill skill = SkillFactory.getSkill(30000022);
         int level = player.getTotalSkillLevel(skill);
         if (level > 0) {
            this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + skill.getEffect(level).getProb());
            this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                  + skill.getEffect(level).getCriticalMin());
         }
      }

      switch (player.getJob()) {
         case 300:
         case 310:
         case 311:
         case 312:
         case 320:
         case 321:
         case 322:
            Skill skillxx = SkillFactory.getSkill(3000001);
            int levelxx = player.getTotalSkillLevel(skillxx);
            if (levelxx > 0) {
               this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + skillxx.getEffect(levelxx).getProb());
               this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                     + skillxx.getEffect(levelxx).getCriticalMin());
               return;
            }
            break;
         case 410:
         case 411:
         case 412:
            Skill skillxxxxxxxxxxxxxxx = SkillFactory.getSkill(4100001);
            int levelxxxxxxxxxxxxxxx = player.getTotalSkillLevel(skillxxxxxxxxxxxxxxx);
            if (levelxxxxxxxxxxxxxxx > 0) {
               this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate
                     + skillxxxxxxxxxxxxxxx.getEffect(levelxxxxxxxxxxxxxxx).getProb());
               this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                     + skillxxxxxxxxxxxxxxx.getEffect(levelxxxxxxxxxxxxxxx).getCriticalMin());
               return;
            }
            break;
         case 434:
            Skill skillxxxxxxxxx = SkillFactory.getSkill(4340010);
            int levelxxxxxxxxx = player.getTotalSkillLevel(skillxxxxxxxxx);
            if (levelxxxxxxxxx > 0) {
               this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate
                     + skillxxxxxxxxx.getEffect(levelxxxxxxxxx).getProb());
               this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                     + skillxxxxxxxxx.getEffect(levelxxxxxxxxx).getCriticalMin());
               return;
            }
            break;
         case 510:
         case 511:
         case 512:
            Skill skillxxxxx = SkillFactory.getSkill(5110000);
            int levelxxxxx = player.getTotalSkillLevel(skillxxxxx);
            if (levelxxxxx > 0) {
               this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate
                     + skillxxxxx.getEffect(levelxxxxx).getProb());
               this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                     + skillxxxxx.getEffect(levelxxxxx).getCriticalMin());
               return;
            }

            Skill critSkill2 = SkillFactory.getSkill(5100008);
            int critlevel2 = player.getTotalSkillLevel(skillxxxxx);
            if (critlevel2 > 0) {
               this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate
                     + (short) critSkill2.getEffect(critlevel2).getCr());
               this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                     + critSkill2.getEffect(critlevel2).getCriticalMin());
               return;
            }
            break;
         case 520:
         case 521:
         case 522:
            Skill skillxxxxxxxx = SkillFactory.getSkill(5200007);
            int levelxxxxxxxx = player.getTotalSkillLevel(skillxxxxxxxx);
            if (levelxxxxxxxx > 0) {
               this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate
                     + (short) skillxxxxxxxx.getEffect(levelxxxxxxxx).getCr());
               this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                     + skillxxxxxxxx.getEffect(levelxxxxxxxx).getCriticalMin());
               return;
            }

            skillxxxxxxxx = SkillFactory.getSkill(5221054);
            levelxxxxxxxx = player.getTotalSkillLevel(skillxxxxxxxx);
            if (levelxxxxxxxx > 0) {
               this.TER = this.TER + skillxxxxxxxx.getEffect(levelxxxxxxxx).getTERRate();
               this.ASR = this.ASR + skillxxxxxxxx.getEffect(levelxxxxxxxx).getASRRate();
               return;
            }
            break;
         case 530:
         case 531:
         case 532:
            Skill skillxxxxxx = SkillFactory.getSkill(5300004);
            int levelxxxxxx = player.getTotalSkillLevel(skillxxxxxx);
            if (levelxxxxxx > 0) {
               this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate
                     + (short) skillxxxxxx.getEffect(levelxxxxxx).getCr());
               this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                     + skillxxxxxx.getEffect(levelxxxxxx).getCriticalMin());
               return;
            }
            break;
         case 1211:
         case 1212:
            Skill skillxxxxxxx = SkillFactory.getSkill(12110000);
            int levelxxxxxxx = player.getTotalSkillLevel(skillxxxxxxx);
            if (levelxxxxxxx > 0) {
               this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate
                     + (short) skillxxxxxxx.getEffect(levelxxxxxxx).getCr());
               this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                     + skillxxxxxxx.getEffect(levelxxxxxxx).getCriticalMin());
               return;
            }
            break;
         case 1300:
         case 1310:
         case 1311:
         case 1312:
            Skill skillx = SkillFactory.getSkill(13000000);
            int levelx = player.getTotalSkillLevel(skillx);
            if (levelx > 0) {
               this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + skillx.getEffect(levelx).getProb());
               this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                     + skillx.getEffect(levelx).getCriticalMin());
               return;
            }
            break;
         case 1410:
         case 1411:
         case 1412:
            Skill skillxxxxxxxxxxxxx = SkillFactory.getSkill(14100001);
            int levelxxxxxxxxxxxxx = player.getTotalSkillLevel(skillxxxxxxxxxxxxx);
            if (levelxxxxxxxxxxxxx > 0) {
               this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate
                     + skillxxxxxxxxxxxxx.getEffect(levelxxxxxxxxxxxxx).getProb());
               this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                     + skillxxxxxxxxxxxxx.getEffect(levelxxxxxxxxxxxxx).getCriticalMin());
               return;
            }
            break;
         case 1511:
         case 1512:
            Skill skillxxxx = SkillFactory.getSkill(15110000);
            int levelxxxx = player.getTotalSkillLevel(skillxxxx);
            if (levelxxxx > 0) {
               this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate
                     + skillxxxx.getEffect(levelxxxx).getProb());
               this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                     + skillxxxx.getEffect(levelxxxx).getCriticalMin());
               return;
            }
            break;
         case 2111:
         case 2112:
            Skill skillxxx = SkillFactory.getSkill(21110000);
            int levelxxx = player.getTotalSkillLevel(skillxxx);
            if (levelxxx > 0) {
               this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate
                     + (short) (skillxxx.getEffect(levelxxx).getX() * skillxxx.getEffect(levelxxx).getY()
                           + skillxxx.getEffect(levelxxx).getCr()));
               return;
            }
            break;
         case 2214:
         case 2215:
         case 2216:
         case 2217:
         case 2218:
            Skill skill = SkillFactory.getSkill(22140000);
            int level = player.getTotalSkillLevel(skill);
            if (level > 0) {
               this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + skill.getEffect(level).getProb());
               this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                     + skill.getEffect(level).getCriticalMin());
               return;
            }
            break;
         case 2300:
         case 2310:
         case 2311:
         case 2312:
            Skill skillxxxxxxxxxxx = SkillFactory.getSkill(23000003);
            int levelxxxxxxxxxxx = player.getTotalSkillLevel(skillxxxxxxxxxxx);
            if (levelxxxxxxxxxxx > 0) {
               this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate
                     + (short) skillxxxxxxxxxxx.getEffect(levelxxxxxxxxxxx).getCr());
               this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                     + skillxxxxxxxxxxx.getEffect(levelxxxxxxxxxxx).getCriticalMin());
               return;
            }
            break;
         case 2412:
            Skill skillxxxxxxxxxxxxxx = SkillFactory.getSkill(24120006);
            int levelxxxxxxxxxxxxxx = player.getTotalSkillLevel(skillxxxxxxxxxxxxxx);
            if (levelxxxxxxxxxxxxxx > 0) {
               this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                     + skillxxxxxxxxxxxxxx.getEffect(levelxxxxxxxxxxxxxx).getCriticalMin());
               this.watk = this.watk + skillxxxxxxxxxxxxxx.getEffect(levelxxxxxxxxxxxxxx).getAttackX();
               return;
            }
            break;
         case 3100:
         case 3110:
         case 3111:
         case 3112:
            Skill skillxxxxxxxxxxxx = SkillFactory.getSkill(31100006);
            int levelxxxxxxxxxxxx = player.getTotalSkillLevel(skillxxxxxxxxxxxx);
            if (levelxxxxxxxxxxxx > 0) {
               this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate
                     + (short) skillxxxxxxxxxxxx.getEffect(levelxxxxxxxxxxxx).getCr());
               this.watk = this.watk + skillxxxxxxxxxxxx.getEffect(levelxxxxxxxxxxxx).getAttackX();
               return;
            }
            break;
         case 3210:
         case 3211:
         case 3212:
            Skill skillxxxxxxxxxx = SkillFactory.getSkill(32100006);
            int levelxxxxxxxxxx = player.getTotalSkillLevel(skillxxxxxxxxxx);
            if (levelxxxxxxxxxx > 0) {
               this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate
                     + (short) skillxxxxxxxxxx.getEffect(levelxxxxxxxxxx).getCr());
               this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent
                     + skillxxxxxxxxxx.getEffect(levelxxxxxxxxxx).getCriticalMin());
               return;
            }

            skillxxxxxxxxxx = SkillFactory.getSkill(32111012);
            levelxxxxxxxxxx = player.getTotalSkillLevel(skillxxxxxxxxxx);
            if (levelxxxxxxxxxx > 0) {
               this.TER = this.TER + skillxxxxxxxxxx.getEffect(levelxxxxxxxxxx).getTERRate();
               this.ASR = this.ASR + skillxxxxxxxxxx.getEffect(levelxxxxxxxxxx).getASRRate();
               return;
            }
      }
   }

   public final short passive_sharpeye_min_percent() {
      return this.passive_sharpeye_min_percent;
   }

   public final short passive_sharpeye_percent() {
      return this.passive_sharpeye_percent;
   }

   public final short passive_sharpeye_rate() {
      return this.passive_sharpeye_rate;
   }

   public final byte passive_mastery() {
      return this.passive_mastery;
   }

   public int calculateMinBaseDamage(MapleCharacter player) {
      int minbasedamage = 0;
      int atk = player.getStat().getTotalWatk();
      if (atk == 0) {
         minbasedamage = 1;
      } else {
         Item weapon_item = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
         if (weapon_item != null) {
            MapleWeaponType weapon = MapleItemInformationProvider.getInstance().getWeaponType(weapon_item.getItemId());
            if (player.getJob() == 110) {
               this.skil = SkillFactory.getSkill(1100000);
               this.skill = player.getTotalSkillLevel(this.skil);
               if (this.skill > 0) {
                  this.sword = (this.skil.getEffect(player.getTotalSkillLevel(this.skil)).getMastery() * 5 + 10) / 100;
               } else {
                  this.sword = 0.1;
               }
            } else {
               this.skil = SkillFactory.getSkill(1200000);
               this.skill = player.getTotalSkillLevel(this.skil);
               if (this.skill > 0) {
                  this.sword = (this.skil.getEffect(player.getTotalSkillLevel(this.skil)).getMastery() * 5 + 10) / 100;
               } else {
                  this.sword = 0.1;
               }
            }

            this.skil = SkillFactory.getSkill(1100001);
            this.skill = player.getTotalSkillLevel(this.skil);
            if (this.skill > 0) {
               this.axe = (this.skil.getEffect(player.getTotalSkillLevel(this.skil)).getMastery() * 5 + 10) / 100;
            } else {
               this.axe = 0.1;
            }

            this.skil = SkillFactory.getSkill(1200001);
            this.skill = player.getTotalSkillLevel(this.skil);
            if (this.skill > 0) {
               this.blunt = (this.skil.getEffect(player.getTotalSkillLevel(this.skil)).getMastery() * 5 + 10) / 100;
            } else {
               this.blunt = 0.1;
            }

            this.skil = SkillFactory.getSkill(1300000);
            this.skill = player.getTotalSkillLevel(this.skil);
            if (this.skill > 0) {
               this.spear = (this.skil.getEffect(player.getTotalSkillLevel(this.skil)).getMastery() * 5 + 10) / 100;
            } else {
               this.spear = 0.1;
            }

            this.skil = SkillFactory.getSkill(1300001);
            this.skill = player.getTotalSkillLevel(this.skil);
            if (this.skill > 0) {
               this.polearm = (this.skil.getEffect(player.getTotalSkillLevel(this.skil)).getMastery() * 5 + 10) / 100;
            } else {
               this.polearm = 0.1;
            }

            this.skil = SkillFactory.getSkill(3200000);
            this.skill = player.getTotalSkillLevel(this.skil);
            if (this.skill > 0) {
               this.crossbow = (this.skil.getEffect(player.getTotalSkillLevel(this.skil)).getMastery() * 5 + 10) / 100;
            } else {
               this.crossbow = 0.1;
            }

            this.skil = SkillFactory.getSkill(3100000);
            this.skill = player.getTotalSkillLevel(this.skil);
            if (this.skill > 0) {
               this.bow = (this.skil.getEffect(player.getTotalSkillLevel(this.skil)).getMastery() * 5 + 10) / 100;
            } else {
               this.bow = 0.1;
            }

            if (weapon == MapleWeaponType.CROSSBOW) {
               minbasedamage = (int) (this.localdex * 0.9 * 3.6 * this.crossbow + this.localstr) / 100 * (atk + 15);
            }

            if (weapon == MapleWeaponType.BOW) {
               minbasedamage = (int) (this.localdex * 0.9 * 3.4 * this.bow + this.localstr) / 100 * (atk + 15);
            }

            if (player.getJob() == 400 && weapon == MapleWeaponType.DAGGER) {
               minbasedamage = (int) (this.localluk * 0.9 * 3.6 * this.dagger + this.localstr + this.localdex) / 100
                     * atk;
            }

            if (player.getJob() != 400 && weapon == MapleWeaponType.DAGGER) {
               minbasedamage = (int) (this.localstr * 0.9 * 4.0 * this.dagger + this.localdex) / 100 * atk;
            }

            if (player.getJob() == 400 && weapon == MapleWeaponType.CLAW) {
               minbasedamage = (int) (this.localluk * 0.9 * 3.6 * this.claw + this.localstr + this.localdex) / 100
                     * (atk + 15);
            }

            if (weapon == MapleWeaponType.SPEAR) {
               minbasedamage = (int) (this.localstr * 0.9 * 3.0 * this.spear + this.localdex) / 100 * atk;
            }

            if (weapon == MapleWeaponType.POLE_ARM) {
               minbasedamage = (int) (this.localstr * 0.9 * 3.0 * this.polearm + this.localdex) / 100 * atk;
            }

            if (weapon == MapleWeaponType.SWORD1H) {
               minbasedamage = (int) (this.localstr * 0.9 * 4.0 * this.sword + this.localdex) / 100 * atk;
            }

            if (weapon == MapleWeaponType.SWORD2H) {
               minbasedamage = (int) (this.localstr * 0.9 * 4.6 * this.sword + this.localdex) / 100 * atk;
            }

            if (weapon == MapleWeaponType.AXE1H) {
               minbasedamage = (int) (this.localstr * 0.9 * 3.2 * this.axe + this.localdex) / 100 * atk;
            }

            if (weapon == MapleWeaponType.BLUNT1H) {
               minbasedamage = (int) (this.localstr * 0.9 * 3.2 * this.blunt + this.localdex) / 100 * atk;
            }

            if (weapon == MapleWeaponType.AXE2H) {
               minbasedamage = (int) (this.localstr * 0.9 * 3.4 * this.axe + this.localdex) / 100 * atk;
            }

            if (weapon == MapleWeaponType.BLUNT2H) {
               minbasedamage = (int) (this.localstr * 0.9 * 3.4 * this.blunt + this.localdex) / 100 * atk;
            }

            if (weapon == MapleWeaponType.STAFF || weapon == MapleWeaponType.WAND) {
               minbasedamage = (int) (this.localstr * 0.9 * 3.0 * this.staffwand + this.localdex) / 100 * atk;
            }
         }
      }

      return minbasedamage;
   }

   public final float calculateMaxBaseDamage(int watk) {
      MapleCharacter chra = this.chr.get();
      if (chra == null) {
         return 0.0F;
      } else {
         float maxbasedamage;
         if (watk == 0) {
            maxbasedamage = 1.0F;
         } else {
            Item weapon_item = chra.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
            int job = chra.getJob();
            MapleWeaponType weapon = weapon_item == null ? MapleWeaponType.NOT_A_WEAPON
                  : GameConstants.getWeaponType(weapon_item.getItemId());
            int mainstat;
            int secondarystat;
            switch (weapon) {
               case BOW:
               case CROSSBOW:
                  mainstat = this.localdex;
                  secondarystat = this.localstr;
                  break;
               case CLAW:
               case KATARA:
               case DAGGER:
                  if ((job < 400 || job > 434) && (job < 1400 || job > 1412)) {
                     mainstat = this.localstr;
                     secondarystat = this.localdex;
                  } else {
                     mainstat = this.localluk;
                     secondarystat = this.localdex + this.localstr;
                  }
                  break;
               case CANE:
               case HANDCANNON:
               case AXE1H:
               case BLUNT1H:
               case AXE2H:
               case SWORD1H:
               case SWORD2H:
               case BLUNT2H:
               case POLE_ARM:
               case SPEAR:
               case DUAL_BOW:
               case WAND:
               case STAFF:
               default:
                  mainstat = this.localstr;
                  secondarystat = this.localdex;
                  break;
               case KNUCKLE:
                  mainstat = this.localstr;
                  secondarystat = this.localdex;
                  break;
               case GUN:
                  mainstat = this.localdex;
                  secondarystat = this.localstr;
                  break;
               case NOT_A_WEAPON:
                  if ((job < 500 || job > 522) && (job < 1500 || job > 1512) && (job < 3500 || job > 3512)) {
                     mainstat = 0;
                     secondarystat = 0;
                  } else {
                     mainstat = this.localstr;
                     secondarystat = this.localdex;
                  }
            }

            maxbasedamage = (weapon.getMaxDamageMultiplier() * mainstat + secondarystat) * watk / 100.0F;
         }

         return maxbasedamage;
      }
   }

   public final void calculateMaxBaseDamage(int watk, int pvpDamage, MapleCharacter chra) {
      if (watk <= 0) {
         this.localmaxbasedamage = 1.0F;
         this.localmaxbasepvpdamage = 1.0F;
      } else {
         Item weapon_item = chra.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
         Item weapon_item2 = chra.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10);
         int job = chra.getJob();
         MapleWeaponType weapon = weapon_item == null ? MapleWeaponType.NOT_A_WEAPON
               : GameConstants.getWeaponType(weapon_item.getItemId());
         MapleWeaponType weapon2 = weapon_item2 == null ? MapleWeaponType.NOT_A_WEAPON
               : GameConstants.getWeaponType(weapon_item2.getItemId());
         boolean mage = job >= 200 && job <= 232 || job >= 1200 && job <= 1212 || job >= 2200 && job <= 2218
               || job >= 3200 && job <= 3212;
         int mainstat;
         int secondarystat;
         int mainstatpvp;
         int secondarystatpvp;
         switch (weapon) {
            case BOW:
            case CROSSBOW:
            case GUN:
               mainstat = this.localdex;
               secondarystat = this.localstr;
               mainstatpvp = this.dex;
               secondarystatpvp = this.str;
               break;
            case CLAW:
            case CANE:
            case KATARA:
            case DAGGER:
               mainstat = this.localluk;
               secondarystat = this.localdex + this.localstr;
               mainstatpvp = this.luk;
               secondarystatpvp = this.dex + this.str;
               break;
            case HANDCANNON:
            case AXE1H:
            case BLUNT1H:
            case AXE2H:
            case SWORD1H:
            case SWORD2H:
            case BLUNT2H:
            case POLE_ARM:
            case SPEAR:
            case KNUCKLE:
            default:
               if (mage) {
                  mainstat = this.localint_;
                  secondarystat = this.localluk;
                  mainstatpvp = this.int_;
                  secondarystatpvp = this.luk;
               } else {
                  mainstat = this.localstr;
                  secondarystat = this.localdex;
                  mainstatpvp = this.str;
                  secondarystatpvp = this.dex;
               }
         }

         this.localmaxbasepvpdamage = weapon.getMaxDamageMultiplier() * (4 * mainstatpvp + secondarystatpvp)
               * (100.0F + pvpDamage / 100.0F);
         this.localmaxbasepvpdamageL = weapon.getMaxDamageMultiplier() * (4 * mainstat + secondarystat)
               * (100.0F + pvpDamage / 100.0F);
         if (weapon2 != MapleWeaponType.NOT_A_WEAPON && weapon_item != null && weapon_item2 != null) {
            Equip we1 = (Equip) weapon_item;
            Equip we2 = (Equip) weapon_item2;
            this.localmaxbasedamage = weapon.getMaxDamageMultiplier()
                  * (4 * mainstat + secondarystat)
                  * ((watk - (mage ? we2.getMatk() : we2.getWatk())) / 100.0F);
            this.localmaxbasedamage = this.localmaxbasedamage
                  + weapon2.getMaxDamageMultiplier() * (4 * mainstat + secondarystat)
                        * ((watk - (mage ? we1.getMatk() : we1.getWatk())) / 100.0F);
         } else {
            this.localmaxbasedamage = weapon.getMaxDamageMultiplier() * (4 * mainstat + secondarystat)
                  * (watk / 100.0F);
         }
      }
   }

   public final float getHealHP() {
      return this.shouldHealHP;
   }

   public final float getHealMP() {
      return this.shouldHealMP;
   }

   public final void relocHeal(MapleCharacter chra) {
      if (!chra.isClone()) {
         int playerjob = chra.getJob();
         this.shouldHealHP = 10 + this.recoverHP;
         this.shouldHealMP = GameConstants.isDemonSlayer(chra.getJob()) ? 0.0F
               : 3 + this.mpRestore + this.recoverMP + this.localint_ / 10;
         this.mpRecoverTime = 0;
         this.hpRecoverTime = 0;
         if (playerjob == 111 || playerjob == 112) {
            Skill effect = SkillFactory.getSkill(1110000);
            int lvl = chra.getTotalSkillLevel(effect);
            if (lvl > 0) {
               SecondaryStatEffect eff = effect.getEffect(lvl);
               if (eff.getHp() > 0) {
                  this.shouldHealHP = this.shouldHealHP + eff.getHp();
                  this.hpRecoverTime = 4000;
               }

               this.shouldHealMP = this.shouldHealMP + eff.getMp();
               this.mpRecoverTime = 4000;
            }
         } else if (playerjob == 1111 || playerjob == 1112) {
            Skill effect = SkillFactory.getSkill(11110000);
            int lvl = chra.getTotalSkillLevel(effect);
            if (lvl > 0) {
               this.shouldHealMP = this.shouldHealMP + effect.getEffect(lvl).getMp();
               this.mpRecoverTime = 4000;
            }
         } else if (GameConstants.isMercedes(playerjob)) {
            Skill effect = SkillFactory.getSkill(20020109);
            int lvl = chra.getTotalSkillLevel(effect);
            if (lvl > 0) {
               this.shouldHealHP = this.shouldHealHP + (float) (effect.getEffect(lvl).getX() * this.localmaxhp / 100L);
               this.hpRecoverTime = 4000;
               this.shouldHealMP = this.shouldHealMP + (float) (effect.getEffect(lvl).getX() * this.localmaxmp / 100L);
               this.mpRecoverTime = 4000;
            }
         } else if (playerjob == 3111 || playerjob == 3112) {
            Skill effect = SkillFactory.getSkill(31110009);
            int lvl = chra.getTotalSkillLevel(effect);
            if (lvl > 0) {
               this.shouldHealMP = this.shouldHealMP + effect.getEffect(lvl).getY();
               this.mpRecoverTime = 4000;
            }
         }

         if (chra.getChair() != 0) {
            this.shouldHealHP += 99.0F;
            this.shouldHealMP += 99.0F;
         } else if (chra.getMap() != null) {
            float recvRate = chra.getMap().getRecoveryRate();
            if (recvRate > 0.0F) {
               this.shouldHealHP *= recvRate;
               this.shouldHealMP *= recvRate;
            }
         }
      }
   }

   public final void connectData(PacketEncoder mplew, ZeroInfo info) {
      mplew.writeShort(this.str);
      mplew.writeShort(this.dex);
      mplew.writeShort(this.int_);
      mplew.writeShort(this.luk);
      if (info != null) {
         mplew.writeInt(this.hp);
         mplew.writeInt(this.maxhp);
         mplew.writeInt(info.getSubMP());
         mplew.writeInt(info.getCalcSubMMP());
      } else {
         mplew.writeInt(this.hp);
         mplew.writeInt(this.maxhp);
         mplew.writeInt(this.mp);
         mplew.writeInt(this.maxmp);
      }
   }

   public static int getSkillByJob(int skillID, int job) {
      int order = GameConstants.get_novice_skill_root(job);
      return order < 0 ? 0 : skillID + order * 10000;
   }

   public final int getSkillIncrement(int skillID) {
      return this.skillsIncrement.containsKey(skillID) ? this.skillsIncrement.get(skillID) : 0;
   }

   public final int getElementBoost(Element key) {
      return this.elemBoosts.containsKey(key) ? this.elemBoosts.get(key) : 0;
   }

   public final int getDamageIncrease(int key) {
      return this.damageIncrease != null && this.damageIncrease.containsKey(key)
            ? this.damageIncrease.get(key) + this.damX
            : this.damX;
   }

   public final int getAccuracy() {
      return this.accuracy;
   }

   public void heal_noUpdate(MapleCharacter chra) {
      this.setHp(this.getCurrentMaxHp(chra), chra);
      this.setMp(this.getCurrentMaxMp(chra), chra);
   }

   public void heal(MapleCharacter chra) {
      this.heal_noUpdate(chra);
      chra.updateSingleStat(MapleStat.HP, this.getCurrentMaxHp(chra));
      chra.updateSingleStat(MapleStat.MP, this.getCurrentMaxMp(chra));
   }

   public Pair<Integer, Integer> handleEquipAdditions(
         MapleItemInformationProvider ii, MapleCharacter chra, boolean first_login, Map<Skill, SkillEntry> sData,
         int itemId) {
      List<Triple<String, String, String>> additions = ii.getEquipAdditions(itemId);
      if (additions == null) {
         return null;
      } else {
         int localmaxhp_x = 0;
         int localmaxmp_x = 0;
         int skillid = 0;
         int skilllevel = 0;

         for (Triple<String, String, String> add : additions) {
            if (!add.getMid().contains("con")) {
               int right = 0;

               try {
                  right = Integer.parseInt(add.getRight());
               } catch (NumberFormatException var28) {
               }

               String var17 = add.getLeft();
               switch (var17) {
                  case "elemboost":
                     String craftx = ii.getEquipAddReqs(itemId, add.getLeft(), "craft");
                     if (!add.getMid().equals("elemVol")
                           || craftx != null && (craftx == null || chra.getTrait(MapleTrait.MapleTraitType.craft)
                                 .getLocalTotalExp() < Integer.parseInt(craftx))) {
                        break;
                     }

                     int value = Integer.parseInt(add.getRight().substring(1, add.getRight().length()));
                     Element key = Element.getFromChar(add.getRight().charAt(0));
                     if (this.elemBoosts.get(key) != null) {
                        value += this.elemBoosts.get(key);
                     }

                     this.elemBoosts.put(key, value);
                     break;
                  case "mobcategory":
                     if (add.getMid().equals("damage")) {
                        this.dam_r *= (right + 100.0) / 100.0;
                        this.bossdam_r += (right + 100.0) / 100.0;
                     }
                     break;
                  case "critical":
                     boolean canJob = false;
                     boolean canLevel = false;
                     String job = ii.getEquipAddReqs(itemId, add.getLeft(), "job");
                     if (job != null) {
                        if (job.contains(",")) {
                           String[] jobs = job.split(",");

                           for (String x : jobs) {
                              if (chra.getJob() == Integer.parseInt(x)) {
                                 canJob = true;
                              }
                           }
                        } else if (chra.getJob() == Integer.parseInt(job)) {
                           canJob = true;
                        }
                     }

                     String level = ii.getEquipAddReqs(itemId, add.getLeft(), "level");
                     if (level != null && chra.getLevel() >= Integer.parseInt(level)) {
                        canLevel = true;
                     }

                     if ((job != null && canJob || job == null) && (level != null && canLevel || level == null)) {
                        String var40 = add.getMid();
                        switch (var40) {
                           case "prob":
                              this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + right);
                              continue;
                           case "damage":
                              this.passive_sharpeye_min_percent = (short) (this.passive_sharpeye_min_percent + right);
                              this.passive_sharpeye_percent = (short) (this.passive_sharpeye_percent + right);
                        }
                     }
                     break;
                  case "boss": {
                     String craft = ii.getEquipAddReqs(itemId, add.getLeft(), "craft");
                     if (add.getMid().equals("damage")
                           && (craft == null || craft != null && chra.getTrait(MapleTrait.MapleTraitType.craft)
                                 .getLocalTotalExp() >= Integer.parseInt(craft))) {
                        this.bossdam_r *= (right + 100.0) / 100.0;
                     }
                  }
                     break;
                  case "mobdie": {
                     String craft = ii.getEquipAddReqs(itemId, add.getLeft(), "craft");
                     if (craft != null && (craft == null || chra.getTrait(MapleTrait.MapleTraitType.craft)
                           .getLocalTotalExp() < Integer.parseInt(craft))) {
                        break;
                     }

                     String var38 = add.getMid();
                     switch (var38) {
                        case "hpIncOnMobDie":
                           this.hpRecover += right;
                           this.hpRecoverProp += 5;
                           continue;
                        case "mpIncOnMobDie":
                           this.mpRecover += right;
                           this.mpRecoverProp += 5;
                        default:
                           continue;
                     }
                  }
                  case "skill":
                     if (!first_login) {
                        break;
                     }

                     String craft = ii.getEquipAddReqs(itemId, add.getLeft(), "craft");
                     if (craft != null && (craft == null || chra.getTrait(MapleTrait.MapleTraitType.craft)
                           .getLocalTotalExp() < Integer.parseInt(craft))) {
                        break;
                     }

                     String var37 = add.getMid();
                     switch (var37) {
                        case "id":
                           skillid = right;
                           continue;
                        case "level":
                           skilllevel = right;
                        default:
                           continue;
                     }
                  case "hpmpchange":
                     String var36 = add.getMid();
                     switch (var36) {
                        case "hpChangerPerTime":
                           this.recoverHP += right;
                           continue;
                        case "mpChangerPerTime":
                           this.recoverMP += right;
                        default:
                           continue;
                     }
                  case "statinc":
                     boolean canJobx = false;
                     boolean canLevelx = false;
                     String jobx = ii.getEquipAddReqs(itemId, add.getLeft(), "job");
                     if (jobx != null) {
                        if (jobx.contains(",")) {
                           String[] jobs = jobx.split(",");

                           for (String xx : jobs) {
                              if (chra.getJob() == Integer.parseInt(xx)) {
                                 canJobx = true;
                              }
                           }
                        } else if (chra.getJob() == Integer.parseInt(jobx)) {
                           canJobx = true;
                        }
                     }

                     String levelx = ii.getEquipAddReqs(itemId, add.getLeft(), "level");
                     if (levelx != null && chra.getLevel() >= Integer.parseInt(levelx)) {
                        canLevelx = true;
                     }

                     if ((canJobx || jobx == null) && (canLevelx || levelx == null)) {
                        if (itemId == 1142367) {
                           int day = Calendar.getInstance().get(7);
                           if (day != 1 && day != 7) {
                              continue;
                           }
                        }

                        String var47 = add.getMid();
                        switch (var47) {
                           case "incPAD":
                              this.watk += right;
                              break;
                           case "incMAD":
                              this.magic += right;
                              break;
                           case "incSTR":
                              this.localstr += right;
                              break;
                           case "incDEX":
                              this.localdex += right;
                              break;
                           case "incINT":
                              this.localint_ += right;
                              break;
                           case "incLUK":
                              this.localluk += right;
                              break;
                           case "incJump":
                              this.jump += right;
                              break;
                           case "incMHP":
                              localmaxhp_x += right;
                              break;
                           case "incMMP":
                              localmaxmp_x += right;
                              break;
                           case "incPDD":
                              this.wdef += right;
                              break;
                           case "incMDD":
                              this.mdef += right;
                              break;
                           case "incACC":
                              this.accuracy += right;
                           case "incEVA":
                           default:
                              break;
                           case "incSpeed":
                              this.speed += right;
                              break;
                           case "incMMPr":
                              this.percent_mp += right;
                        }
                     }
               }
            }
         }

         if (skillid != 0 && skilllevel != 0) {
            sData.put(SkillFactory.getSkill(skillid), new SkillEntry((byte) skilllevel, (byte) 0, -1L));
         }

         return new Pair<>(localmaxhp_x, localmaxmp_x);
      }
   }

   public void recalcPVPRank(MapleCharacter chra) {
      this.pvpRank = 10;
      this.pvpExp = chra.getTotalBattleExp();

      for (int i = 0; i < 10; i++) {
         if (this.pvpExp > GameConstants.getPVPExpNeededForLevel(i + 1)) {
            this.pvpRank--;
            this.pvpExp = this.pvpExp - GameConstants.getPVPExpNeededForLevel(i + 1);
         }
      }
   }

   public int getHPPercent() {
      return (int) Math.ceil(this.hp * 100.0 / this.localmaxhp);
   }

   public int getMPPercent() {
      return (int) Math.ceil(this.mp * 100.0 / this.localmaxmp);
   }

   public final boolean isRangedJob(int job) {
      return GameConstants.isMercedes(job)
            || GameConstants.isCannon(job)
            || job == 400
            || job / 10 == 52
            || job / 100 == 3
            || job / 100 == 13
            || job / 100 == 14
            || job / 100 == 33
            || job / 100 == 35
            || job / 10 == 41;
   }

   public ReentrantReadWriteLock getLock() {
      return this.lock;
   }
}
