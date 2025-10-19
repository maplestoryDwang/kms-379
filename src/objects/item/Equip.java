package objects.item;

import constants.GameConstants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import objects.users.enchant.BonusStat;
import objects.users.enchant.EquipStat;
import objects.users.enchant.ExItemType;
import objects.users.enchant.ItemStateFlag;
import objects.users.enchant.StarForceHyperUpgrade;
import objects.utils.Randomizer;

public class Equip extends Item implements Serializable {
   public static final int ARMOR_RATIO = 350000;
   public static final int WEAPON_RATIO = 700000;
   private byte state = 0;
   private byte lines = 0;
   private byte starforce = 0;
   private byte upgradeSlots = 0;
   private byte level = 0;
   private byte vicioushammer = 0;
   private byte enhance = 0;
   private byte reqLevel = 0;
   private byte yggdrasilWisdom = 0;
   private byte totalDamage = 0;
   private byte downLevel = 0;
   private byte allStat = 0;
   private byte karmaCount = -1;
   private short str = 0;
   private short dex = 0;
   private short _int = 0;
   private short luk = 0;
   private short hp = 0;
   private short mp = 0;
   private short hpR = 0;
   private short mpR = 0;
   private short specialAttribute = 0;
   private short watk = 0;
   private short matk = 0;
   private short wdef = 0;
   private short mdef = 0;
   private short acc = 0;
   private short avoid = 0;
   private short hands = 0;
   private short speed = 0;
   private short jump = 0;
   private short charmExp = 0;
   private short pvpDamage = 0;
   private short bossDamage = 0;
   private short ignorePDR = 0;
   private short soulname = 0;
   private short soulenchanter = 0;
   private short soulpotential = 0;
   private int arc = 0;
   private int arcexp = 1;
   private int arclevel = 0;
   private int itemEXP = 0;
   private int durability = -1;
   private int incSkill = -1;
   private int potential1 = 0;
   private int potential2 = 0;
   private int potential3 = 0;
   private int potential4 = 0;
   private int potential5 = 0;
   private int potential6 = 0;
   private int fusionAnvil = 0;
   private int soulskill = 0;
   private short exceptSTR = 0;
   private short exceptDEX = 0;
   private short exceptINT = 0;
   private short exceptLUK = 0;
   private short exceptARC = 0;
   private short exceptHP = 0;
   private short exceptMP = 0;
   private short exceptWATK = 0;
   private short exceptMATK = 0;
   private short exceptWDEF = 0;
   private short exceptMDEF = 0;
   private short exceptACC = 0;
   private short exceptAVOID = 0;
   private short exceptHANDS = 0;
   private short exceptSPEED = 0;
   private short exceptJUMP = 0;
   private byte ExceptionalSlot = 0;
   private boolean finalStrike = false;
   private byte theSeedRingLevel = 0;
   private long fire = -1L;
   private int itemState = 0;
   private boolean masterLabel = false;
   private boolean specialLabel = false;
   private int csGrade = 0;
   private int csOption1 = 0;
   private int csOption2 = 0;
   private int csOption3 = 0;
   private long csOptionExpireDate = 0L;
   private long exGradeOption = 0L;
   private int specialPotential = -1;
   private int spGrade = 0;
   private int spAllStat = 0;
   private int spAttack = 0;
   private int CHUC = 0;
   private int clearCheck = 0;
   private long serialNumberEquip = 0L;
   private int cashEnchantCount = 0;
   private boolean specialRoyal = false;
   private MapleRing ring = null;
   private List<EquipStat> stats = new LinkedList<>();

   public int getCashEnchantCount() {
      return this.cashEnchantCount;
   }

   public void setCashEnchantCount(int cashEnchantCount) {
      this.cashEnchantCount = cashEnchantCount;
   }

   public Equip(int id, short position, byte flag) {
      super(id, position, (short)1, flag);
   }

   public Equip(int id, short position, long uniqueid, int flag) {
      super(id, position, (short)1, flag, uniqueid);
   }

   public void set(Equip set) {
      this.set(set, false);
   }

   public void set(Equip set, boolean transmitExceptional) {
      this.str = set.str;
      this.dex = set.dex;
      this._int = set._int;
      this.luk = set.luk;
      this.arc = set.arc;
      this.arclevel = set.arclevel;
      this.arcexp = set.arcexp;
      this.hp = set.hp;
      this.mp = set.mp;
      this.matk = set.matk;
      this.mdef = set.mdef;
      this.watk = set.watk;
      this.wdef = set.wdef;
      this.acc = set.acc;
      this.avoid = set.avoid;
      this.hands = set.hands;
      this.speed = set.speed;
      this.jump = set.jump;
      this.enhance = set.enhance;
      this.upgradeSlots = set.upgradeSlots;
      this.level = set.level;
      this.itemEXP = set.itemEXP;
      this.durability = set.durability;
      this.vicioushammer = set.vicioushammer;
      this.potential1 = set.potential1;
      this.potential2 = set.potential2;
      this.potential3 = set.potential3;
      this.potential4 = set.potential4;
      this.potential5 = set.potential5;
      this.potential6 = set.potential6;
      this.fusionAnvil = set.fusionAnvil;
      this.charmExp = set.charmExp;
      this.pvpDamage = set.pvpDamage;
      this.incSkill = set.incSkill;
      this.specialAttribute = set.specialAttribute;
      this.reqLevel = set.reqLevel;
      this.yggdrasilWisdom = set.yggdrasilWisdom;
      if (!GameConstants.isTheSeedRing(this.getItemId())) {
         this.finalStrike = set.finalStrike;
      } else {
         this.finalStrike = set.finalStrike;
         this.theSeedRingLevel = set.theSeedRingLevel;
      }

      this.bossDamage = set.bossDamage;
      this.ignorePDR = set.ignorePDR;
      this.totalDamage = set.totalDamage;
      this.allStat = set.allStat;
      this.setDownLevel(set.getDownLevel());
      this.karmaCount = set.karmaCount;
      this.soulname = set.soulname;
      this.soulenchanter = set.soulenchanter;
      this.soulpotential = set.soulpotential;
      this.soulskill = set.soulskill;
      this.stats = set.stats;
      this.state = set.state;
      this.lines = set.lines;
      this.starforce = set.starforce;
      this.fire = set.fire;
      this.specialPotential = set.specialPotential;
      this.spAttack = set.spAttack;
      this.spAllStat = set.spAllStat;
      this.spGrade = set.spGrade;
      this.masterLabel = set.masterLabel;
      this.specialLabel = set.specialLabel;
      this.itemState = set.itemState;
      this.exGradeOption = set.exGradeOption;
      this.CHUC = set.CHUC;
      this.clearCheck = set.clearCheck;
      this.specialRoyal = set.specialRoyal;
      this.cashEnchantCount = set.cashEnchantCount;
      this.serialNumberEquip = set.serialNumberEquip;
      this.setCsGrade(set.getCsGrade());
      this.setCsOption1(set.getCsOption1());
      this.setCsOption2(set.getCsOption2());
      this.setCsOption3(set.getCsOption3());
      this.setCsOptionExpireDate(set.getCsOptionExpireDate());
      if (this.ExceptionalSlot > 0 && set.ExceptionalSlot > 0 || transmitExceptional) {
         this.ExceptionalSlot = set.ExceptionalSlot;
         this.exceptSTR = set.exceptSTR;
         this.exceptDEX = set.exceptDEX;
         this.exceptINT = set.exceptINT;
         this.exceptLUK = set.exceptLUK;
         this.exceptHP = set.exceptHP;
         this.exceptMP = set.exceptMP;
         this.exceptMATK = set.exceptMATK;
         this.exceptMDEF = set.exceptMDEF;
         this.exceptWATK = set.exceptWATK;
         this.exceptWDEF = set.exceptWDEF;
         this.exceptACC = set.exceptACC;
         this.exceptAVOID = set.exceptAVOID;
         this.exceptHANDS = set.exceptHANDS;
         this.exceptSPEED = set.exceptSPEED;
         this.exceptJUMP = set.exceptJUMP;
      }
   }

   public void set2(Equip set) {
      this.str = set.str;
      this.dex = set.dex;
      this._int = set._int;
      this.luk = set.luk;
      this.arc = set.arc;
      this.arclevel = set.arclevel;
      this.arcexp = set.arcexp;
      this.hp = set.hp;
      this.mp = set.mp;
      this.matk = set.matk;
      this.mdef = set.mdef;
      this.watk = set.watk;
      this.wdef = set.wdef;
      this.acc = set.acc;
      this.avoid = set.avoid;
      this.hands = set.hands;
      this.speed = set.speed;
      this.jump = set.jump;
      this.enhance = set.enhance;
      this.upgradeSlots = set.upgradeSlots;
      this.level = set.level;
      this.itemEXP = set.itemEXP;
      this.durability = set.durability;
      this.vicioushammer = set.vicioushammer;
      this.potential1 = set.potential1;
      this.potential2 = set.potential2;
      this.potential3 = set.potential3;
      this.potential4 = set.potential4;
      this.potential5 = set.potential5;
      this.potential6 = set.potential6;
      this.fusionAnvil = set.fusionAnvil;
      this.charmExp = set.charmExp;
      this.pvpDamage = set.pvpDamage;
      this.incSkill = set.incSkill;
      this.specialAttribute = set.specialAttribute;
      this.reqLevel = set.reqLevel;
      this.yggdrasilWisdom = set.yggdrasilWisdom;
      if (!GameConstants.isTheSeedRing(this.getItemId())) {
         this.finalStrike = set.finalStrike;
      } else {
         this.finalStrike = set.finalStrike;
         this.theSeedRingLevel = set.theSeedRingLevel;
      }

      this.bossDamage = set.bossDamage;
      this.ignorePDR = set.ignorePDR;
      this.totalDamage = set.totalDamage;
      this.allStat = set.allStat;
      this.setDownLevel(set.getDownLevel());
      this.karmaCount = set.karmaCount;
      this.soulname = set.soulname;
      this.soulenchanter = set.soulenchanter;
      this.soulpotential = set.soulpotential;
      this.soulskill = set.soulskill;
      this.stats = set.stats;
      this.state = set.state;
      this.lines = set.lines;
      this.starforce = set.starforce;
      this.fire = set.fire;
      this.specialPotential = set.specialPotential;
      this.spAttack = set.spAttack;
      this.spAllStat = set.spAllStat;
      this.spGrade = set.spGrade;
      this.masterLabel = set.masterLabel;
      this.specialLabel = set.specialLabel;
      this.itemState = set.itemState;
      this.exGradeOption = set.exGradeOption;
      this.CHUC = set.CHUC;
      this.clearCheck = set.clearCheck;
      this.specialRoyal = set.specialRoyal;
      this.cashEnchantCount = set.cashEnchantCount;
      this.setCsGrade(set.getCsGrade());
      this.setCsOption1(set.getCsOption1());
      this.setCsOption2(set.getCsOption2());
      this.setCsOption3(set.getCsOption3());
      this.setCsOptionExpireDate(set.getCsOptionExpireDate());
   }

   @Override
   public Item copy() {
      Equip ret = new Equip(this.getItemId(), this.getPosition(), this.getUniqueId(), this.getFlag());
      ret.setTempUniqueID(this.getTempUniqueID());
      ret.str = this.str;
      ret.dex = this.dex;
      ret._int = this._int;
      ret.luk = this.luk;
      ret.arc = this.arc;
      ret.arcexp = this.arcexp;
      ret.arclevel = this.arclevel;
      ret.hp = this.hp;
      ret.mp = this.mp;
      ret.hpR = this.hpR;
      ret.mpR = this.mpR;
      ret.matk = this.matk;
      ret.mdef = this.mdef;
      ret.watk = this.watk;
      ret.wdef = this.wdef;
      ret.acc = this.acc;
      ret.avoid = this.avoid;
      ret.hands = this.hands;
      ret.speed = this.speed;
      ret.jump = this.jump;
      ret.enhance = this.enhance;
      ret.upgradeSlots = this.upgradeSlots;
      ret.level = this.level;
      ret.itemEXP = this.itemEXP;
      ret.durability = this.durability;
      ret.vicioushammer = this.vicioushammer;
      ret.potential1 = this.potential1;
      ret.potential2 = this.potential2;
      ret.potential3 = this.potential3;
      ret.potential4 = this.potential4;
      ret.potential5 = this.potential5;
      ret.potential6 = this.potential6;
      ret.fusionAnvil = this.fusionAnvil;
      ret.charmExp = this.charmExp;
      ret.pvpDamage = this.pvpDamage;
      ret.incSkill = this.incSkill;
      ret.specialAttribute = this.specialAttribute;
      ret.reqLevel = this.reqLevel;
      ret.yggdrasilWisdom = this.yggdrasilWisdom;
      ret.finalStrike = this.finalStrike;
      ret.theSeedRingLevel = this.theSeedRingLevel;
      ret.bossDamage = this.bossDamage;
      ret.ignorePDR = this.ignorePDR;
      ret.totalDamage = this.totalDamage;
      ret.allStat = this.allStat;
      ret.setDownLevel(this.getDownLevel());
      ret.karmaCount = this.karmaCount;
      ret.soulname = this.soulname;
      ret.soulenchanter = this.soulenchanter;
      ret.soulpotential = this.soulpotential;
      ret.setGiftFrom(this.getGiftFrom());
      ret.setOwner(this.getOwner());
      ret.setQuantity(this.getQuantity());
      ret.setExpiration(this.getExpiration());
      ret.stats = this.stats;
      ret.state = this.state;
      ret.lines = this.lines;
      ret.starforce = this.starforce;
      ret.fire = this.fire;
      ret.soulskill = this.soulskill;
      ret.specialPotential = this.specialPotential;
      ret.spAllStat = this.spAllStat;
      ret.spAttack = this.spAttack;
      ret.spGrade = this.spGrade;
      ret.masterLabel = this.masterLabel;
      ret.specialLabel = this.specialLabel;
      ret.itemState = this.itemState;
      ret.exGradeOption = this.exGradeOption;
      ret.CHUC = this.CHUC;
      ret.clearCheck = this.clearCheck;
      ret.specialRoyal = this.specialRoyal;
      ret.serialNumberEquip = this.serialNumberEquip;
      ret.cashEnchantCount = this.cashEnchantCount;
      ret.setCsGrade(this.getCsGrade());
      ret.setCsOption1(this.getCsOption1());
      ret.setCsOption2(this.getCsOption2());
      ret.setCsOption3(this.getCsOption3());
      ret.setCsOptionExpireDate(this.getCsOptionExpireDate());
      ret.ExceptionalSlot = this.ExceptionalSlot;
      ret.exceptSTR = this.exceptSTR;
      ret.exceptDEX = this.exceptDEX;
      ret.exceptINT = this.exceptINT;
      ret.exceptLUK = this.exceptLUK;
      ret.exceptHP = this.exceptHP;
      ret.exceptMP = this.exceptMP;
      ret.exceptMATK = this.exceptMATK;
      ret.exceptMDEF = this.exceptMDEF;
      ret.exceptWATK = this.exceptWATK;
      ret.exceptWDEF = this.exceptWDEF;
      ret.exceptACC = this.exceptACC;
      ret.exceptAVOID = this.exceptAVOID;
      ret.exceptHANDS = this.exceptHANDS;
      ret.exceptSPEED = this.exceptSPEED;
      ret.exceptJUMP = this.exceptJUMP;
      return ret;
   }

   @Override
   public byte getType() {
      return 1;
   }

   public byte getUpgradeSlots() {
      return this.upgradeSlots;
   }

   public byte getAdditionalGrade() {
      return (byte)(this.potential4 / 10000);
   }

   public int getAdditionalPA() {
      return (this.potential4 != 0 ? 1 : 0) + (this.potential5 != 0 ? 1 : 0) + (this.potential6 != 0 ? 1 : 0);
   }

   public int getPA() {
      return (this.potential1 != 0 ? 1 : 0) + (this.potential2 != 0 ? 1 : 0) + (this.potential3 != 0 ? 1 : 0);
   }

   public byte getItemGrade() {
      return (byte)(this.state & 15);
   }

   public void setPotentialOption(int idx, int option) {
      switch (idx) {
         case 0:
            this.potential1 = option;
            break;
         case 1:
            this.potential2 = option;
            break;
         case 2:
            this.potential3 = option;
            break;
         case 3:
            this.potential4 = option;
            break;
         case 4:
            this.potential5 = option;
            break;
         case 5:
            this.potential6 = option;
      }
   }

   public int getPotentialOption(int idx) {
      switch (idx) {
         case 0:
            return this.potential1;
         case 1:
            return this.potential2;
         case 2:
            return this.potential3;
         case 3:
            return this.potential4;
         case 4:
            return this.potential5;
         case 5:
            return this.potential6;
         default:
            return 0;
      }
   }

   public List<Integer> getPotentials(boolean additional, int count) {
      List<Integer> ret = new ArrayList<>();
      if (!additional) {
         if (count >= 1) {
            ret.add(this.potential1);
         }

         if (count >= 2) {
            ret.add(this.potential2);
         }

         if (count >= 3) {
            ret.add(this.potential3);
         }
      } else {
         if (count >= 1) {
            ret.add(this.potential4);
         }

         if (count >= 2) {
            ret.add(this.potential5);
         }

         if (count >= 3) {
            ret.add(this.potential6);
         }
      }

      return ret;
   }

   public short getStatByNumber(int num) {
      short ret = 0;
      switch (num) {
         case 0:
            ret = this.watk;
            break;
         case 1:
            ret = this.matk;
            break;
         case 2:
            ret = this.str;
            break;
         case 3:
            ret = this.dex;
            break;
         case 4:
            ret = this._int;
            break;
         case 5:
            ret = this.luk;
            break;
         case 6:
            ret = this.wdef;
            break;
         case 7:
            ret = this.hp;
            break;
         case 8:
            ret = this.mp;
            break;
         case 9:
            ret = this.jump;
            break;
         case 10:
            ret = this.speed;
      }

      return ret;
   }

   public short getStr() {
      return this.str;
   }

   public short getTotalStr() {
      short ret = this.str;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case Str:
            case StrDex:
            case StrInt:
            case StrLuk:
               ret = (short)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret += StarForceHyperUpgrade.getHUStat(this, EquipStat.STR, this.str);
      }

      return ret;
   }

   public short getStarForceStat(EquipStat stat, short value) {
      return StarForceHyperUpgrade.getHUStat(this, stat, value);
   }

   public short getDex() {
      return this.dex;
   }

   public short getTotalDex() {
      short ret = this.dex;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case StrDex:
            case Dex:
            case DexInt:
            case DexLuk:
               ret = (short)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
            case StrInt:
            case StrLuk:
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret += StarForceHyperUpgrade.getHUStat(this, EquipStat.DEX, this.dex);
      }

      return ret;
   }

   public short getInt() {
      return this._int;
   }

   public short getTotalInt() {
      short ret = this._int;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case StrInt:
            case DexInt:
            case Int:
            case IntLuk:
               ret = (short)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
            case StrLuk:
            case Dex:
            case DexLuk:
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret += StarForceHyperUpgrade.getHUStat(this, EquipStat.INT, this._int);
      }

      return ret;
   }

   public short getLuk() {
      return this.luk;
   }

   public short getTotalLuk() {
      short ret = this.luk;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case StrLuk:
            case DexLuk:
            case IntLuk:
            case Luk:
               ret = (short)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
            case Dex:
            case DexInt:
            case Int:
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret += StarForceHyperUpgrade.getHUStat(this, EquipStat.LUK, this.luk);
      }

      return ret;
   }

   public int getArc() {
      return this.arc;
   }

   public short getHp() {
      return this.hp;
   }

   public short getTotalHp() {
      short ret = this.hp;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case MaxHP:
               ret = (short)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret += StarForceHyperUpgrade.getHUStat(this, EquipStat.MHP, this.hp);
      }

      return ret;
   }

   public int getTotalHp_Int() {
      int ret = this.hp;
      if (GameConstants.isArcaneSymbol(this.getItemId()) || GameConstants.isAuthenticSymbol(this.getItemId())) {
         ret *= 10;
      }

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case MaxHP:
               ret += BonusStat.getBonusStat(this, entry.getKey(), entry.getValue());
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret += StarForceHyperUpgrade.getHUStat(this, EquipStat.MHP, this.hp);
      }

      return ret;
   }

   public short getMp() {
      return this.mp;
   }

   public short getTotalMp() {
      short ret = this.mp;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case MaxMP:
               ret = (short)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret += StarForceHyperUpgrade.getHUStat(this, EquipStat.MMP, this.mp);
      }

      return ret;
   }

   public short getHpR() {
      return this.hpR;
   }

   public short getMpR() {
      return this.mpR;
   }

   public short getWatk() {
      return this.watk;
   }

   public short getTotalWatk() {
      short ret = this.watk;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case Pad:
               ret = (short)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret += StarForceHyperUpgrade.getHUStat(this, EquipStat.WATK, this.watk);
      }

      return ret;
   }

   public short getMatk() {
      return this.matk;
   }

   public short getTotalMatk() {
      short ret = this.matk;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case Mad:
               ret = (short)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret += StarForceHyperUpgrade.getHUStat(this, EquipStat.MATK, this.matk);
      }

      return ret;
   }

   public short getWdef() {
      return this.wdef;
   }

   public short getTotalWdef() {
      short ret = this.wdef;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case Pdd:
               ret = (short)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret += StarForceHyperUpgrade.getHUStat(this, EquipStat.WDEF, this.wdef);
      }

      return ret;
   }

   public short getMdef() {
      return this.mdef;
   }

   public short getTotalMdef() {
      short ret = this.mdef;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case Mdd:
               ret = (short)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret += StarForceHyperUpgrade.getHUStat(this, EquipStat.MDEF, this.mdef);
      }

      return ret;
   }

   public short getAcc() {
      return this.acc;
   }

   public short getTotalAcc() {
      short ret = this.acc;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case Acc:
               ret = (short)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret += StarForceHyperUpgrade.getHUStat(this, EquipStat.ACC, this.acc);
      }

      return ret;
   }

   public short getAvoid() {
      return this.avoid;
   }

   public short getTotalAvoid() {
      short ret = this.avoid;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case Eva:
               ret = (short)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret += StarForceHyperUpgrade.getHUStat(this, EquipStat.AVOID, this.avoid);
      }

      return ret;
   }

   public short getHands() {
      return this.hands;
   }

   public short getSpeed() {
      return this.speed;
   }

   public short getTotalSpeed() {
      short ret = this.speed;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case Speed:
               ret = (short)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret += StarForceHyperUpgrade.getHUStat(this, EquipStat.SPEED, this.speed);
      }

      return ret;
   }

   public short getJump() {
      return this.jump;
   }

   public short getTotalJump() {
      short ret = this.jump;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case Jump:
               ret = (short)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret += StarForceHyperUpgrade.getHUStat(this, EquipStat.JUMP, this.jump);
      }

      return ret;
   }

   public void setStatByNumber(int num, short option) {
      if (option < 0) {
         num = -1;
      }

      switch (num) {
         case 0:
            this.watk = option;
            break;
         case 1:
            this.matk = option;
            break;
         case 2:
            this.str = option;
            break;
         case 3:
            this.dex = option;
            break;
         case 4:
            this._int = option;
            break;
         case 5:
            this.luk = option;
            break;
         case 6:
            this.wdef = option;
            break;
         case 7:
            this.hp = option;
            break;
         case 8:
            this.mp = option;
            break;
         case 9:
            this.jump = option;
            break;
         case 10:
            this.speed = option;
      }
   }

   public void setStr(short str) {
      if (str < 0) {
         str = 0;
      }

      this.str = str;
   }

   public void addStr(short str) {
      if (str < 0) {
         str = 0;
      }

      this.str += str;
   }

   public void setDex(short dex) {
      if (dex < 0) {
         dex = 0;
      }

      this.dex = dex;
   }

   public void addDex(short dex) {
      if (dex < 0) {
         dex = 0;
      }

      this.dex += dex;
   }

   public void setInt(short _int) {
      if (_int < 0) {
         _int = 0;
      }

      this._int = _int;
   }

   public void addInt(short int_) {
      if (int_ < 0) {
         int_ = 0;
      }

      this._int += int_;
   }

   public void setLuk(short luk) {
      if (luk < 0) {
         luk = 0;
      }

      this.luk = luk;
   }

   public void addLuk(short luk) {
      if (luk < 0) {
         luk = 0;
      }

      this.luk += luk;
   }

   public void addHp(short hp) {
      if (hp < 0) {
         hp = 0;
      }

      this.hp += hp;
   }

   public void addMp(short mp) {
      if (mp < 0) {
         mp = 0;
      }

      this.mp += mp;
   }

   public void setArc(int arc) {
      if (arc < 0) {
         arc = 0;
      }

      this.arc = arc;
   }

   public void setHp(short hp) {
      if (hp < 0) {
         hp = 0;
      }

      this.hp = hp;
   }

   public void setMp(short mp) {
      if (mp < 0) {
         mp = 0;
      }

      this.mp = mp;
   }

   public void setHpR(short hpR) {
      if (hpR < 0) {
         hpR = 0;
      }

      this.hpR = hpR;
   }

   public void setMpR(short mpR) {
      if (mpR < 0) {
         mpR = 0;
      }

      this.mpR = mpR;
   }

   public void setWatk(short watk) {
      if (watk < 0) {
         watk = 0;
      }

      this.watk = watk;
   }

   public void addWatk(short watk) {
      if (watk < 0) {
         watk = 0;
      }

      this.watk += watk;
   }

   public void setMatk(short matk) {
      if (matk < 0) {
         matk = 0;
      }

      this.matk = matk;
   }

   public void addMatk(short matk) {
      if (matk < 0) {
         matk = 0;
      }

      this.matk += matk;
   }

   public void setWdef(short wdef) {
      this.wdef = wdef;
   }

   public void addWdef(short wdef) {
      if (wdef < 0) {
         wdef = 0;
      }

      this.wdef += wdef;
   }

   public void setMdef(short mdef) {
      this.mdef = mdef;
   }

   public void addMdef(short mdef) {
      if (mdef < 0) {
         mdef = 0;
      }

      this.mdef += mdef;
   }

   public void setAcc(short acc) {
      if (acc < 0) {
         acc = 0;
      }

      this.acc = acc;
   }

   public void addAcc(short acc) {
      if (acc < 0) {
         acc = 0;
      }

      this.acc += acc;
   }

   public void setAvoid(short avoid) {
      if (avoid < 0) {
         avoid = 0;
      }

      this.avoid = avoid;
   }

   public void setHands(short hands) {
      if (hands < 0) {
         hands = 0;
      }

      this.hands = hands;
   }

   public void setSpeed(short speed) {
      this.speed = speed;
   }

   public void setJump(short jump) {
      this.jump = jump;
   }

   public void setUpgradeSlots(byte upgradeSlots) {
      this.upgradeSlots = (byte)Math.max(0, upgradeSlots);
   }

   public void addUpgradeSlots(byte upgradeSlots) {
      int upgrade = Math.max(0, this.upgradeSlots + upgradeSlots);
      this.upgradeSlots = (byte)upgrade;
   }

   public byte getLevel() {
      return this.level;
   }

   public void setLevel(byte level) {
      this.level = level;
   }

   public byte getViciousHammer() {
      return this.vicioushammer;
   }

   public void setViciousHammer(byte ham) {
      this.vicioushammer = ham;
   }

   public int getItemEXP() {
      return this.itemEXP;
   }

   public void setItemEXP(int itemEXP) {
      if (itemEXP < 0) {
         itemEXP = 0;
      }

      this.itemEXP = itemEXP;
   }

   public int getEquipExp() {
      if (this.itemEXP <= 0) {
         return 0;
      } else {
         return GameConstants.isWeapon(this.getItemId()) ? this.itemEXP / 700000 : this.itemEXP / 350000;
      }
   }

   public int getEquipExpForLevel() {
      if (this.getEquipExp() <= 0) {
         return 0;
      } else {
         int expz = this.getEquipExp();

         for (int i = this.getBaseLevel(); i <= GameConstants.getMaxLevel(this.getItemId()) && expz >= GameConstants.getExpForLevel(i, this.getItemId()); i++) {
            expz -= GameConstants.getExpForLevel(i, this.getItemId());
         }

         return expz;
      }
   }

   public int getExpPercentage() {
      return this.getEquipLevel() >= this.getBaseLevel()
            && this.getEquipLevel() <= GameConstants.getMaxLevel(this.getItemId())
            && GameConstants.getExpForLevel(this.getEquipLevel(), this.getItemId()) > 0
         ? this.getEquipExpForLevel() * 100 / GameConstants.getExpForLevel(this.getEquipLevel(), this.getItemId())
         : 0;
   }

   public int getEquipLevel() {
      if (GameConstants.isTheSeedRing(this.getItemId())) {
         return this.getTheSeedRingLevel();
      } else if (GameConstants.getMaxLevel(this.getItemId()) <= 0) {
         return 0;
      } else if (this.getEquipExp() <= 0) {
         return this.getBaseLevel();
      } else {
         int levelz = this.getBaseLevel();
         int expz = this.getEquipExp();

         for (int i = levelz;
            (
                  GameConstants.getStatFromWeapon(this.getItemId()) == null
                     ? i <= GameConstants.getMaxLevel(this.getItemId())
                     : i < GameConstants.getMaxLevel(this.getItemId())
               )
               && expz >= GameConstants.getExpForLevel(i, this.getItemId());
            i++
         ) {
            levelz++;
            expz -= GameConstants.getExpForLevel(i, this.getItemId());
         }

         return levelz;
      }
   }

   public int getBaseLevel() {
      return GameConstants.getStatFromWeapon(this.getItemId()) == null ? 1 : 0;
   }

   @Override
   public void setQuantity(short quantity) {
      if (quantity >= 0 && quantity <= 1) {
         super.setQuantity(quantity);
      } else {
         throw new RuntimeException("Setting the quantity to " + quantity + " on an equip (itemid: " + this.getItemId() + ")");
      }
   }

   public int getDurability() {
      return this.durability;
   }

   public void setDurability(int dur) {
      this.durability = dur;
   }

   public byte getEnhance() {
      return this.enhance;
   }

   public void setEnhance(byte en) {
      this.enhance = en;
   }

   public int getPotential1() {
      return this.potential1;
   }

   public void setPotential1(int en) {
      this.potential1 = en;
   }

   public int getPotential2() {
      return this.potential2;
   }

   public void setPotential2(int en) {
      this.potential2 = en;
   }

   public int getPotential3() {
      return this.potential3;
   }

   public void setPotential3(int en) {
      this.potential3 = en;
   }

   public int getPotential4() {
      return this.potential4;
   }

   public void setPotential4(int en) {
      this.potential4 = en;
   }

   public int getPotential5() {
      return this.potential5;
   }

   public void setPotential5(int en) {
      this.potential5 = en;
   }

   public int getPotential6() {
      return this.potential6;
   }

   public void setPotential6(int en) {
      this.potential6 = en;
   }

   public int getFusionAnvil() {
      return this.fusionAnvil;
   }

   public void setFusionAnvil(int en) {
      this.fusionAnvil = en;
   }

   public byte getState() {
      return this.state;
   }

   public void setState(byte state) {
      this.state = state;
   }

   public void setItemGrade(int grade) {
      int newGrade = this.state & 240 | grade & 15;
      this.state = (byte)newGrade;
   }

   public void setAdditionalReleased(boolean opened) {
      this.state = (byte)(this.state & -33 | (opened ? 0 : 1) << 5);
   }

   public void setReleased(boolean opened) {
      this.state = (byte)(this.state & -17 | (opened ? 1 : 0) << 4);
   }

   public boolean isAdditionalReleased() {
      return (byte)(this.state >> 5 & 1) == 0;
   }

   public boolean isReleased() {
      return (byte)(this.state >> 4 & 1) != 0;
   }

   public byte getLines() {
      return this.lines;
   }

   public void setLines(byte lines) {
      this.lines = lines;
   }

   public void copyPotential(Equip equip) {
      this.setState(equip.getState());
      this.setPotential1(equip.getPotential1());
      this.setPotential2(equip.getPotential2());
      this.setPotential3(equip.getPotential3());
      this.setPotential4(equip.getPotential4());
      this.setPotential5(equip.getPotential5());
      this.setPotential6(equip.getPotential6());
   }

   public byte getStarForce() {
      return this.starforce;
   }

   public void setStarForce(byte starforce) {
      this.starforce = starforce;
   }

   public long getFire() {
      return this.fire;
   }

   public void setFire(long fire) {
      this.fire = fire;
   }

   public int getIncSkill() {
      return this.incSkill;
   }

   public void setIncSkill(int inc) {
      this.incSkill = inc;
   }

   public short getCharmEXP() {
      return this.charmExp;
   }

   public short getPVPDamage() {
      return this.pvpDamage;
   }

   public void setCharmEXP(short s) {
      this.charmExp = s;
   }

   public void setPVPDamage(short p) {
      this.pvpDamage = p;
   }

   public short getSpecialAttribute() {
      return this.specialAttribute;
   }

   public void setSpecialAttribute(short specialAttribute) {
      this.specialAttribute = specialAttribute;
   }

   public byte getReqLevel() {
      return this.reqLevel;
   }

   public void setReqLevel(byte reqLevel) {
      this.reqLevel = reqLevel;
   }

   public byte getGrowthEnchant() {
      return this.yggdrasilWisdom;
   }

   public void setGrowthEnchant(byte yggdrasilWisdom) {
      this.yggdrasilWisdom = yggdrasilWisdom;
   }

   public boolean getFinalStrike() {
      return this.finalStrike;
   }

   public void setFinalStrike(boolean finalStrike) {
      this.finalStrike = finalStrike;
   }

   public byte getTheSeedRingLevel() {
      return this.theSeedRingLevel;
   }

   public void setTheSeedRingLevel(byte theSeedRingLevel) {
      this.theSeedRingLevel = theSeedRingLevel;
   }

   public short getBossDamage() {
      return this.bossDamage;
   }

   public short getTotalBossDamage() {
      short ret = this.bossDamage;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case BdR:
               ret = (short)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret += StarForceHyperUpgrade.getHUStat(this, EquipStat.BOSS_DAMAGE, this.bossDamage);
      }

      return ret;
   }

   public void setBossDamage(short bossDamage) {
      this.bossDamage = bossDamage;
   }

   public void addBossDamage(byte dmg) {
      this.bossDamage = (short)(this.bossDamage + dmg);
   }

   public short getIgnorePDR() {
      return this.ignorePDR;
   }

   public short getTotalIgnorePDR() {
      short ret = this.ignorePDR;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case IMdR:
               ret = (short)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret += StarForceHyperUpgrade.getHUStat(this, EquipStat.IGNORE_PDR, this.ignorePDR);
      }

      return ret;
   }

   public void setIgnorePDR(short ignorePDR) {
      this.ignorePDR = ignorePDR;
   }

   public void addIgnoreWdef(short ignorePDR) {
      this.ignorePDR += ignorePDR;
   }

   public byte getTotalDamage() {
      return this.totalDamage;
   }

   public byte getTotalMaxDamage() {
      byte ret = this.totalDamage;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case DamR:
               ret = (byte)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret = (byte)(ret + StarForceHyperUpgrade.getHUStat(this, EquipStat.TOTAL_DAMAGE, this.totalDamage));
      }

      return ret;
   }

   public void setTotalDamage(byte totalDamage) {
      this.totalDamage = totalDamage;
   }

   public byte getAllStat() {
      return this.allStat;
   }

   public byte getTotalAllStat() {
      byte ret = this.allStat;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case StatR:
               ret = (byte)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret = (byte)(ret + StarForceHyperUpgrade.getHUStat(this, EquipStat.ALL_STAT, this.allStat));
      }

      return ret;
   }

   public void setAllStat(byte allStat) {
      this.allStat = allStat;
   }

   public byte getKarmaCount() {
      return this.karmaCount;
   }

   public void setKarmaCount(byte karmaCount) {
      this.karmaCount = karmaCount;
   }

   public short getSoulName() {
      return this.soulname;
   }

   public void setSoulName(short soulname) {
      this.soulname = soulname;
   }

   public short getSoulEnchanter() {
      return this.soulenchanter;
   }

   public void setSoulEnchanter(short soulenchanter) {
      this.soulenchanter = soulenchanter;
   }

   public short getSoulPotential() {
      return this.soulpotential;
   }

   public void setSoulPotential(short soulpotential) {
      this.soulpotential = soulpotential;
   }

   public int getSoulSkill() {
      return this.soulskill;
   }

   public void setSoulSkill(int skillid) {
      this.soulskill = skillid;
   }

   public MapleRing getRing() {
      if (GameConstants.isEffectRing(this.getItemId()) && this.getUniqueId() > 0L) {
         if (this.ring == null) {
            this.ring = MapleRing.loadFromDb(this.getUniqueId(), this.getPosition() < 0);
         }

         return this.ring;
      } else {
         return null;
      }
   }

   public void setRing(MapleRing ring) {
      this.ring = ring;
   }

   public List<EquipStat> getStats() {
      List<Integer> PCroomlegendartyArcane = new ArrayList<>(
         Arrays.asList(
            1212131,
            1213030,
            1214030,
            1222124,
            1232124,
            1242144,
            1242145,
            1262053,
            1272043,
            1282043,
            1292030,
            1302359,
            1312215,
            1322266,
            1332291,
            1362151,
            1372239,
            1382276,
            1402271,
            1412191,
            1422199,
            1432229,
            1442287,
            1452269,
            1462254,
            1472277,
            1482234,
            1492247,
            1522154,
            1532159,
            1582046,
            1592037
         )
      );
      List<Integer> PCroomlegendartyAbs = new ArrayList<>(
         Arrays.asList(
            1212121,
            1213028,
            1214028,
            1222114,
            1232114,
            1242123,
            1242124,
            1262040,
            1272021,
            1282022,
            1292028,
            1302344,
            1312204,
            1322256,
            1332280,
            1342105,
            1362141,
            1372229,
            1382266,
            1402260,
            1412182,
            1422190,
            1432219,
            1442276,
            1452258,
            1462244,
            1472266,
            1482222,
            1492236,
            1522144,
            1532148,
            1582027,
            1592028
         )
      );
      if ((PCroomlegendartyArcane.contains(this.getItemId()) || PCroomlegendartyAbs.contains(this.getItemId())) && !this.stats.contains(EquipStat.ITEM_STATE)) {
         this.stats.add(EquipStat.ITEM_STATE);
         this.setItemState(ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
      }

      return this.stats;
   }

   public List<EquipStat> getStats(int pos) {
      List<EquipStat> tempStats = new ArrayList<>();
      this.getStats().stream().forEach(stat -> {
         if (stat.getPosition() == pos) {
            tempStats.add(stat);
         }
      });
      return tempStats;
   }

   public static Equip calculateEquipStats(Equip eq) {
      eq.getStats().clear();
      if (eq.getUpgradeSlots() > 0) {
         eq.getStats().add(EquipStat.SLOTS);
      }

      if (eq.getLevel() > 0) {
         eq.getStats().add(EquipStat.LEVEL);
      }

      if (eq.getTotalStr() > 0) {
         eq.getStats().add(EquipStat.STR);
      }

      if (eq.getTotalDex() > 0) {
         eq.getStats().add(EquipStat.DEX);
      }

      if (eq.getTotalInt() > 0) {
         eq.getStats().add(EquipStat.INT);
      }

      if (eq.getTotalLuk() > 0) {
         eq.getStats().add(EquipStat.LUK);
      }

      if (eq.getTotalHp() > 0) {
         eq.getStats().add(EquipStat.MHP);
      }

      if (eq.getTotalMp() > 0) {
         eq.getStats().add(EquipStat.MMP);
      }

      if (eq.getTotalWatk() > 0) {
         eq.getStats().add(EquipStat.WATK);
      }

      if (eq.getTotalMatk() > 0) {
         eq.getStats().add(EquipStat.MATK);
      }

      if (eq.getTotalWdef() > 0) {
         eq.getStats().add(EquipStat.WDEF);
      }

      if (eq.getHands() > 0) {
         eq.getStats().add(EquipStat.HANDS);
      }

      if (eq.getTotalSpeed() > 0) {
         eq.getStats().add(EquipStat.SPEED);
      }

      if (eq.getTotalJump() > 0) {
         eq.getStats().add(EquipStat.JUMP);
      }

      if (eq.getFlag() > 0) {
         eq.getStats().add(EquipStat.FLAG);
      }

      if (eq.getIncSkill() > 0) {
         eq.getStats().add(EquipStat.INC_SKILL);
      }

      if (eq.getEquipLevel() > 0 && GameConstants.isTheSeedRing(eq.getItemId())) {
         eq.getStats().add(EquipStat.ITEM_LEVEL);
      }

      if (eq.getItemEXP() > 0) {
         eq.getStats().add(EquipStat.ITEM_EXP);
      }

      if (eq.getDurability() > -1) {
         eq.getStats().add(EquipStat.DURABILITY);
      }

      if (eq.getViciousHammer() > 0) {
         eq.getStats().add(EquipStat.VICIOUS_HAMMER);
      }

      if (eq.getPVPDamage() > 0) {
         eq.getStats().add(EquipStat.PVP_DAMAGE);
      }

      if (eq.getTotalDownLevel() > 0) {
         eq.getStats().add(EquipStat.DOWNLEVEL);
      }

      if (eq.getSpecialAttribute() > 0) {
         eq.getStats().add(EquipStat.SPECIAL_ATTRIBUTE);
      }

      if (eq.getReqLevel() > 0) {
         eq.getStats().add(EquipStat.REQUIRED_LEVEL);
      }

      if (eq.getGrowthEnchant() > 0) {
         eq.getStats().add(EquipStat.GROWTH_ENCHANT);
      }

      if (eq.getFinalStrike()) {
         eq.getStats().add(EquipStat.FINAL_STRIKE);
      }

      if (eq.getTotalBossDamage() > 0) {
         eq.getStats().add(EquipStat.BOSS_DAMAGE);
      }

      if (eq.getIgnorePDR() > 0) {
         eq.getStats().add(EquipStat.IGNORE_PDR);
      }

      if (eq.getTotalMaxDamage() > 0) {
         eq.getStats().add(EquipStat.DAM_R);
      }

      if (eq.getTotalAllStat() > 0) {
         eq.getStats().add(EquipStat.STAT_R);
      }

      eq.getStats().add(EquipStat.CUTTABLE);
      eq.getStats().add(EquipStat.EX_GRADE_OPTION);
      if (eq.getItemState() > 0) {
         eq.getStats().add(EquipStat.ITEM_STATE);
      }

      if (eq.getExceptSTR() > 0) {
         eq.getStats().add(EquipStat.ExceptSTR);
      }

      if (eq.getExceptDEX() > 0) {
         eq.getStats().add(EquipStat.ExceptDEX);
      }

      if (eq.getExceptINT() > 0) {
         eq.getStats().add(EquipStat.ExceptINT);
      }

      if (eq.getExceptLUK() > 0) {
         eq.getStats().add(EquipStat.ExceptLUK);
      }

      if (eq.getExceptHP() > 0) {
         eq.getStats().add(EquipStat.ExceptMHP);
      }

      if (eq.getExceptMP() > 0) {
         eq.getStats().add(EquipStat.ExceptMMP);
      }

      if (eq.getExceptWATK() > 0) {
         eq.getStats().add(EquipStat.ExceptWATK);
      }

      if (eq.getExceptMATK() > 0) {
         eq.getStats().add(EquipStat.ExceptMATK);
      }

      if (eq.getExceptWDEF() > 0) {
         eq.getStats().add(EquipStat.ExceptWDEF);
      }

      if (eq.getSpeed() > 0) {
         eq.getStats().add(EquipStat.ExceptSPEED);
      }

      return (Equip)eq.copy();
   }

   public int getArcEXP() {
      return this.arcexp;
   }

   public int getArcLevel() {
      return this.arclevel;
   }

   public void setArcEXP(int exp) {
      this.arcexp = exp;
   }

   public void setArcLevel(int lv) {
      this.arclevel = lv;
   }

   public byte getDownLevel() {
      return this.downLevel;
   }

   public byte getTotalDownLevel() {
      byte ret = this.downLevel;

      for (Entry<ExItemType, Integer> entry : BonusStat.getExItemOptions(this).entrySet()) {
         switch ((ExItemType)entry.getKey()) {
            case ReqLevel:
               ret = (byte)(ret + BonusStat.getBonusStat(this, entry.getKey(), entry.getValue()));
         }
      }

      if (!this.isAmazingHyperUpgradeUsed()) {
         ret = (byte)(ret + StarForceHyperUpgrade.getHUStat(this, EquipStat.DOWNLEVEL, this.downLevel));
      }

      return ret;
   }

   public void setDownLevel(byte downLevel) {
      this.downLevel = downLevel;
   }

   public int getSpecialPotential() {
      return this.specialPotential;
   }

   public void setSpecialPotential(int sp) {
      this.specialPotential = sp;
   }

   public int getSPAttack() {
      return this.spAttack;
   }

   public void setSPAttack(int spAttack) {
      this.spAttack = spAttack;
   }

   public int getSPAllStat() {
      return this.spAllStat;
   }

   public void setSPAllStat(int spAllStat) {
      this.spAllStat = spAllStat;
   }

   public int getSPGrade() {
      return this.spGrade;
   }

   public void setSPGrade(int spGrade) {
      this.spGrade = spGrade;
   }

   public void innocent(Equip origin) {
      Equip equip = (Equip)this.copy();
      this.set(origin);
      this.setDurability(equip.getDurability());
      this.setExpiration(equip.getExpiration());
      this.setFlag(equip.getFlag());
      this.setLines(equip.getLines());
      this.setState(equip.getState());
      this.setPotential1(equip.getPotential1());
      this.setPotential2(equip.getPotential2());
      this.setPotential3(equip.getPotential3());
      this.setPotential4(equip.getPotential4());
      this.setPotential5(equip.getPotential5());
      this.setPotential6(equip.getPotential6());
      this.setFusionAnvil(equip.getFusionAnvil());
      this.setOwner("");
   }

   public boolean isMasterLabel() {
      return this.masterLabel;
   }

   public void setMasterLabel(boolean masterLabel) {
      this.masterLabel = masterLabel;
   }

   public boolean isSpecialLabel() {
      return this.specialLabel;
   }

   public void setSpecialLabel(boolean specialLabel) {
      this.specialLabel = specialLabel;
   }

   public int getItemState() {
      return this.itemState;
   }

   public void setItemState(int itemState) {
      this.itemState = itemState;
   }

   public int getCsGrade() {
      return this.csGrade;
   }

   public void setCsGrade(int csGrade) {
      this.csGrade = csGrade;
   }

   public int getCsOption1() {
      return this.csOption1;
   }

   public void setCsOption1(int csOption1) {
      this.csOption1 = csOption1;
   }

   public int getCsOption2() {
      return this.csOption2;
   }

   public void setCsOption2(int csOption2) {
      this.csOption2 = csOption2;
   }

   public int getCsOption3() {
      return this.csOption3;
   }

   public void setCsOption3(int csOption3) {
      this.csOption3 = csOption3;
   }

   public long getCsOptionExpireDate() {
      return this.csOptionExpireDate;
   }

   public void setCsOptionExpireDate(long csOptionExpireDate) {
      this.csOptionExpireDate = csOptionExpireDate;
   }

   public long getExGradeOption() {
      return this.exGradeOption;
   }

   public void setExGradeOption(long exGradeOption) {
      this.exGradeOption = exGradeOption;
   }

   public int getCHUC() {
      return this.CHUC;
   }

   public void setCHUC(int CHUC) {
      this.CHUC = CHUC;
   }

   public boolean isSpecialRoyal() {
      return this.specialRoyal;
   }

   public void setSpecialRoyal(boolean specialRoyal) {
      this.specialRoyal = specialRoyal;
   }

   public boolean isAmazingHyperUpgradeUsed() {
      return this.getCHUC() > 0 && (this.getItemState() & ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue()) == 0;
   }

   public int getClearCheck() {
      return this.clearCheck;
   }

   public void setClearCheck(int clearCheck) {
      this.clearCheck = clearCheck;
   }

   public byte getExceptionalSlot() {
      return this.ExceptionalSlot;
   }

   public void addExceptionalSlot(byte a) {
      this.ExceptionalSlot += a;
   }

   public void setExceptionalSlot(byte value) {
      this.ExceptionalSlot = value;
   }

   public short getExceptSTR() {
      return this.exceptSTR;
   }

   public void addExceptSTR(short a) {
      this.exceptSTR += a;
   }

   public void setExceptStr(short value) {
      this.exceptSTR = value;
   }

   public short getExceptDEX() {
      return this.exceptDEX;
   }

   public void addExceptDEX(short a) {
      this.exceptDEX += a;
   }

   public void setExceptDex(short value) {
      this.exceptDEX = value;
   }

   public short getExceptINT() {
      return this.exceptINT;
   }

   public void addExceptINT(short a) {
      this.exceptINT += a;
   }

   public void setExceptInt(short value) {
      this.exceptINT = value;
   }

   public short getExceptLUK() {
      return this.exceptLUK;
   }

   public void addExceptLUK(short a) {
      this.exceptLUK += a;
   }

   public void setExceptLuk(short value) {
      this.exceptLUK = value;
   }

   public short getExceptHP() {
      return this.exceptHP;
   }

   public void addExceptHP(short a) {
      this.exceptHP += a;
   }

   public void setExceptHP(short value) {
      this.exceptHP = value;
   }

   public short getExceptMP() {
      return this.exceptMP;
   }

   public void addExceptMP(short a) {
      this.exceptMP += a;
   }

   public void setExceptMP(short value) {
      this.exceptMP = value;
   }

   public short getExceptMATK() {
      return this.exceptMATK;
   }

   public void addExceptMATK(short a) {
      this.exceptMATK += a;
   }

   public void setExceptMatk(short value) {
      this.exceptMATK = value;
   }

   public short getExceptMDEF() {
      return this.exceptMDEF;
   }

   public void addExceptMDEF(short a) {
      this.exceptMDEF += a;
   }

   public void setExceptMdef(short value) {
      this.exceptMDEF = value;
   }

   public short getExceptWATK() {
      return this.exceptWATK;
   }

   public void addExceptWATK(short a) {
      this.exceptWATK += a;
   }

   public void setExceptWatk(short value) {
      this.exceptWATK = value;
   }

   public short getExceptWDEF() {
      return this.exceptWDEF;
   }

   public void addExceptWDEF(short a) {
      this.exceptWDEF += a;
   }

   public void setExceptWdef(short value) {
      this.exceptWDEF = value;
   }

   public short getExceptACC() {
      return this.exceptACC;
   }

   public void addExceptACC(short a) {
      this.exceptACC += a;
   }

   public void setExceptAcc(short value) {
      this.exceptACC = value;
   }

   public short getExceptAVOID() {
      return this.exceptAVOID;
   }

   public void addExceptAVOID(short a) {
      this.exceptAVOID += a;
   }

   public void setExceptAvoid(short value) {
      this.exceptAVOID = value;
   }

   public short getExceptHANDS() {
      return this.exceptHANDS;
   }

   public void addExceptHANDS(short a) {
      this.exceptHANDS += a;
   }

   public void setExcpetHands(short value) {
      this.exceptHANDS = value;
   }

   public short getExceptSPEED() {
      return this.exceptSPEED;
   }

   public void addExceptSPEED(short a) {
      this.exceptSPEED += a;
   }

   public void setExceptSpeed(short value) {
      this.exceptSPEED = value;
   }

   public short getExceptJUMP() {
      return this.exceptJUMP;
   }

   public void addExceptJUMP(short a) {
      this.exceptJUMP += a;
   }

   public void setExceptJump(short value) {
      this.exceptJUMP = value;
   }

   public void resetPotential_Fuse(boolean half, int potentialState) {
      potentialState = -potentialState;
      if (Randomizer.nextInt(100) < 4) {
         potentialState -= Randomizer.nextInt(100) < 4 ? 2 : 1;
      }

      this.setPotential1(potentialState);
      this.setPotential2(Randomizer.nextInt(half ? 5 : 10) == 0 ? potentialState : 0);
      this.setPotential3(0);
      this.setPotential4(0);
      this.setPotential5(0);
   }

   public void resetPotential() {
      int rank = Randomizer.nextInt(100) < 4 ? (Randomizer.nextInt(100) < 4 ? -19 : -18) : -17;
      this.setPotential1(rank);
      this.setPotential2(Randomizer.nextInt(10) == 0 ? rank : 0);
      this.setPotential3(0);
      this.setPotential4(0);
      this.setPotential5(0);
   }

   public void renewPotential() {
      int epic = 7;
      int unique = 5;
      if (this.getState() == 17 && Randomizer.nextInt(100) <= epic) {
         this.setState((byte)2);
      } else if (this.getState() == 18 && Randomizer.nextInt(100) <= unique) {
         this.setState((byte)3);
      } else if (this.getState() == 19 && Randomizer.nextInt(100) <= 2) {
         this.setState((byte)4);
      } else {
         this.setState((byte)(this.getState() - 16));
      }
   }

   public long getSerialNumberEquip() {
      if (this.serialNumberEquip == 0L) {
         this.setSerialNumberEquip(System.currentTimeMillis() + Randomizer.nextInt());
      }

      return this.serialNumberEquip;
   }

   public void setSerialNumberEquip(long serialNumber) {
      this.serialNumberEquip = serialNumber;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("str=" + this.getStr());
      if (this.getDex() > 0) {
         sb.append(",dex=" + this.getDex());
      }

      if (this.getInt() > 0) {
         sb.append(",int=" + this.getInt());
      }

      if (this.getLuk() > 0) {
         sb.append(",luk=" + this.getLuk());
      }

      if (this.getArc() > 0) {
         sb.append(",arc=" + this.getArc());
      }

      if (this.getArcLevel() > 0) {
         sb.append(",arcLevel=" + this.getArcLevel());
      }

      if (this.getHp() > 0) {
         sb.append(",hp=" + this.getHp());
      }

      if (this.getMp() > 0) {
         sb.append(",mp=" + this.getMp());
      }

      if (this.getHpR() > 0) {
         sb.append(",hpR=" + this.getHpR());
      }

      if (this.getMpR() > 0) {
         sb.append(",mpR=" + this.getMpR());
      }

      if (this.getWatk() > 0) {
         sb.append(",watk=" + this.getWatk());
      }

      if (this.getMatk() > 0) {
         sb.append(",matk=" + this.getMatk());
      }

      if (this.getWdef() > 0) {
         sb.append(",wdef=" + this.getWdef());
      }

      if (this.getMdef() > 0) {
         sb.append(",mdef=" + this.getMdef());
      }

      if (this.getSpeed() > 0) {
         sb.append(",speed=" + this.getSpeed());
      }

      if (this.getJump() > 0) {
         sb.append(",jump=" + this.getJump());
      }

      if (this.getCHUC() > 0) {
         sb.append(",chuc=" + this.getCHUC());
      }

      sb.append(",upgradeSlots=" + this.getUpgradeSlots());
      if (this.getLevel() > 0) {
         sb.append(",level=" + this.getLevel());
      }

      sb.append(",hammer=" + this.getViciousHammer());
      if (this.getPotential1() > 0) {
         sb.append(",potential1=" + this.getPotential1());
      }

      if (this.getPotential2() > 0) {
         sb.append(",potential2=" + this.getPotential2());
      }

      if (this.getPotential3() > 0) {
         sb.append(",potential3=" + this.getPotential3());
      }

      if (this.getPotential4() > 0) {
         sb.append(",potential4=" + this.getPotential4());
      }

      if (this.getPotential5() > 0) {
         sb.append(",potential5=" + this.getPotential5());
      }

      if (this.getPotential6() > 0) {
         sb.append(",potential6=" + this.getPotential6());
      }

      if (this.getReqLevel() > 0) {
         sb.append(",reqLevel=" + this.getReqLevel());
      }

      if (this.getTheSeedRingLevel() > 0) {
         sb.append(",tsrl=" + this.getTheSeedRingLevel());
      }

      if (this.getBossDamage() > 0) {
         sb.append(",bdr=" + this.getBossDamage());
      }

      if (this.getIgnorePDR() > 0) {
         sb.append(",ipdr=" + this.getIgnorePDR());
      }

      if (this.getTotalDamage() > 0) {
         sb.append(",pmdr=" + this.getTotalDamage());
      }

      if (this.getAllStat() > 0) {
         sb.append(",allStat=" + this.getAllStat());
      }

      if (this.getDownLevel() > 0) {
         sb.append(",downLevel=" + this.getDownLevel());
      }

      sb.append(",karma=" + this.getKarmaCount());
      if (this.getSoulName() > 0) {
         sb.append(",soulName=" + this.getSoulName());
      }

      if (this.getSoulEnchanter() > 0) {
         sb.append(",soulEnchanter=" + this.getSoulEnchanter());
      }

      if (this.getSoulPotential() > 0) {
         sb.append(",soulPotential=" + this.getSoulPotential());
      }

      if (this.getSoulSkill() > 0) {
         sb.append(",soulSkill=" + this.getSoulSkill());
      }

      if (this.getOwner() != null && !this.getOwner().isEmpty()) {
         sb.append(",onwer=" + this.getOwner());
      }

      if (this.getFire() > 0L) {
         sb.append(",fire=" + this.getFire());
      }

      if (this.getSpecialPotential() > 0) {
         sb.append(",specialPotential=" + this.getSpecialPotential());
      }

      if (this.getSPAllStat() > 0) {
         sb.append(",spAllStat=" + this.getSPAllStat());
      }

      if (this.getSPAttack() > 0) {
         sb.append(",spAttack=" + this.getSPAttack());
      }

      if (this.getSPGrade() > 0) {
         sb.append(",spGrade=" + this.getSPGrade());
      }

      if (this.isMasterLabel()) {
         sb.append(",masterLabel=" + this.isMasterLabel());
      }

      if (this.isSpecialLabel()) {
         sb.append(",specialLabel=" + this.isSpecialLabel());
      }

      if (this.getItemState() > 0) {
         sb.append(",itemState=" + this.getItemState());
      }

      if (this.getExGradeOption() > 0L) {
         sb.append(",exGrade=" + this.getExGradeOption());
      }

      if (this.getCashEnchantCount() > 0) {
         sb.append(",cashEnchant=" + this.getCashEnchantCount());
      }

      if (this.getCsGrade() > 0) {
         sb.append(",csGrade=" + this.getCsGrade());
      }

      if (this.getCsOption1() > 0) {
         sb.append(",csOption1=" + this.getCsOption1());
      }

      if (this.getCsOption2() > 0) {
         sb.append(",csOption2=" + this.getCsOption2());
      }

      if (this.getCsOption3() > 0) {
         sb.append(",csOption3=" + this.getCsOption3());
      }

      if (this.getCsOptionExpireDate() > 0L) {
         sb.append(",csOptionExpire=" + this.getCsOptionExpireDate());
      }

      if (this.getSerialNumberEquip() > 0L) {
         sb.append(",getSerialNumberEquip=" + this.getSerialNumberEquip());
      }

      if (this.getExceptionalSlot() > 0) {
         sb.append(",getExceptionalSlot=" + this.getExceptionalSlot());
         if (this.getExceptSTR() > 0) {
            sb.append(",getExceptSTR=" + this.getExceptSTR());
         }

         if (this.getExceptDEX() > 0) {
            sb.append(",getExceptDEX=" + this.getExceptDEX());
         }

         if (this.getExceptINT() > 0) {
            sb.append(",getExceptINT=" + this.getExceptINT());
         }

         if (this.getExceptLUK() > 0) {
            sb.append(",getExceptLUK=" + this.getExceptLUK());
         }

         if (this.getExceptWATK() > 0) {
            sb.append(",getExceptWATK=" + this.getExceptWATK());
         }

         if (this.getExceptMATK() > 0) {
            sb.append(",getExceptMATK=" + this.getExceptMATK());
         }

         if (this.getExceptHP() > 0) {
            sb.append(",getExceptHP=" + this.getExceptHP());
         }

         if (this.getExceptMP() > 0) {
            sb.append(",getExceptMP=" + this.getExceptMP());
         }
      }

      return sb.toString();
   }

   public static enum ScrollResult {
      SUCCESS,
      FAIL,
      CURSE;
   }
}
