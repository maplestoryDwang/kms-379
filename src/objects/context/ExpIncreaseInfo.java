package objects.context;

import constants.DailyEventType;
import constants.GameConstants;
import constants.ServerConstants;
import database.DBConfig;
import java.util.HashMap;
import java.util.Map;
import network.encode.PacketEncoder;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.MapleCharacter;
import objects.users.skills.IndieTemporaryStatEntry;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Pair;

public class ExpIncreaseInfo {
   private final boolean isLastHit;
   private final boolean onQuest;
   private final int questBonusRate;
   private final int questBonusRemainCount;
   private final int restFieldExpRate;
   private int decState;
   private long baseExp;
   private long incExp;
   private long decExp;
   private final Map<ExpIncreaseInfo.ExpIncreaseFlag, Long> expDataMap = new HashMap<>();

   public ExpIncreaseInfo(boolean isLastHit, boolean onQuest, int restFieldExpRate, long incExp) {
      this.isLastHit = isLastHit;
      this.onQuest = onQuest;
      this.restFieldExpRate = restFieldExpRate;
      this.questBonusRate = 0;
      this.questBonusRemainCount = 0;
      this.incExp = incExp;
   }

   public void setCurseState(MapleCharacter chr) {
      int curseLv = chr.getMap().getEliteBossCurseLevel();
      long decExp;
      if (chr.getBuffedValue(SecondaryStatFlag.RuneBlocked) != null) {
         decExp = 0L;
      } else {
         int rate = GameConstants.getCursedRunesRate(curseLv);
         decExp = this.incExp * rate / 100L;
      }

      if (curseLv > 0 && decExp > 0L) {
         this.decState = 1;
         this.decExp = decExp;
      }

      this.baseExp = this.incExp;
      this.incExp = Math.max(0L, this.incExp - decExp);
   }

   public void setDeadState(MapleCharacter chr) {
      this.decState = 2;
   }

   public void setEventBonusExp(MapleCharacter chr) {
      if (ServerConstants.dailyEventType == DailyEventType.ExpRateFever) {
         this.expDataMap.put(ExpIncreaseInfo.ExpIncreaseFlag.SelectedMobBonusExp, (long)(this.incExp * (DBConfig.isGanglim ? 0.5 : 0.2)));
      } else if (ServerConstants.dailyEventType == DailyEventType.ExpRateFever_) {
         this.expDataMap.put(ExpIncreaseInfo.ExpIncreaseFlag.SelectedMobBonusExp, this.incExp);
      }
   }

   public void setPartyBonusExp(MapleCharacter chr) {
      if (chr.getParty() != null && chr.getPartyMembers().size() > 1) {
         double partyBonusRate = chr.getMap().getPartyBonusRate();
         double rate = partyBonusRate <= 0.0 ? 0.05 : partyBonusRate;
         this.expDataMap
            .put(ExpIncreaseInfo.ExpIncreaseFlag.PartyBonusExp, (long)((float)(this.incExp * rate) * (chr.getPartyMembers().size() + (rate > 0.05 ? -1 : 1))));
      }
   }

   public void setItemBonusExp(MapleCharacter chr) {
      long itemBonusExp = (long)(this.incExp / 100.0 * chr.getStat().equipmentBonusExp);
      if (chr.getStat().equippedFairy > 0 && chr.getFairyExp() > 0) {
         itemBonusExp = (long)(itemBonusExp + this.incExp / 100.0 * chr.getFairyExp());
      }

      if (chr.getRoadRingExpBoost() > 0) {
         itemBonusExp = (long)(itemBonusExp + this.incExp / 100.0 * chr.getRoadRingExpBoost());
      }

      if (chr.getGuildBonusExpBoost() > 0) {
         itemBonusExp = (long)(itemBonusExp + this.incExp / 100.0 * chr.getGuildBonusExpBoost());
      }

      if (chr.getDonatorBonusExpBoost() > 0) {
         itemBonusExp = (long)(itemBonusExp + this.incExp / 100.0 * chr.getDonatorBonusExpBoost());
      }

      if (DBConfig.isGanglim) {
         if (chr.hasEquipped(1032206)) {
            itemBonusExp = (long)(itemBonusExp + this.incExp * 0.2);
         } else if (chr.hasEquipped(1032207)) {
            itemBonusExp = (long)(itemBonusExp + this.incExp * 0.3);
         } else if (chr.hasEquipped(1032208)) {
            itemBonusExp = (long)(itemBonusExp + this.incExp * 0.4);
         } else if (chr.hasEquipped(1032209)) {
            itemBonusExp = (long)(itemBonusExp + this.incExp * 0.5);
         }
      }

      if (chr.hasEquipped(1114000)) {
         double bonus = 0.1;
         if (chr.getParty() != null) {
            for (MapleCharacter pChr : chr.getPartyMembers()) {
               if (chr.getId() != pChr.getId() & chr.getMap().getCharacterById(pChr.getId()) != null) {
                  bonus += 0.05;
               }
            }
         }

         itemBonusExp = (long)(itemBonusExp + this.incExp * bonus);
      }

      if (chr.hasEquipped(1114317)) {
         itemBonusExp = (long)(itemBonusExp + this.incExp * 0.1);
      }

      if (itemBonusExp > 0L) {
         this.expDataMap.put(ExpIncreaseInfo.ExpIncreaseFlag.ItemBonusExp, itemBonusExp);
      }
   }

   public void setPlusExpBuffExp(MapleCharacter chr) {
      long plusExpBuffBonusExp = (long)(this.incExp * chr.getBuffedValueDefault(SecondaryStatFlag.PlusExpRate, 0).intValue() / 100.0);
      if (plusExpBuffBonusExp > 0L) {
         this.expDataMap.put(ExpIncreaseInfo.ExpIncreaseFlag.PlusExpBuffBonusExp, plusExpBuffBonusExp);
      }
   }

   public void setPsdBonusExpRate(MapleCharacter chr, MapleMonster monster) {
      long psdBonusExpRate = (long)(chr.getStat().plusExp * 0.01 * (this.incExp * 0.01));
      SecondaryStatEffect effect;
      if ((effect = chr.getSkillLevelData(20020110)) != null) {
         psdBonusExpRate = (long)(psdBonusExpRate + this.incExp * (effect.getEXPRate() / 100.0));
      }

      if ((effect = chr.getSkillLevelData(80001040)) != null) {
         psdBonusExpRate = (long)(psdBonusExpRate + this.incExp * effect.getEXPRate() / 100.0);
      }

      if (chr.getMapleUnion() != null) {
         Pair<Integer, Integer> zeroUnion = chr.getMapleUnion()
            .calculateMapleUnionPassive()
            .stream()
            .filter(pair -> pair.left == 71000711)
            .findFirst()
            .orElse(null);
         if (zeroUnion != null) {
            effect = SkillFactory.getSkill(zeroUnion.left).getEffect(zeroUnion.right);
            if (effect != null) {
               psdBonusExpRate = (long)(psdBonusExpRate + this.incExp * (effect.getEXPRate() / 100.0));
            }
         }
      }

      if (monster.getStats().getLevel() >= 101 && monster.getStats().getLevel() <= 200 && chr.getStat().expGuild > 0) {
         psdBonusExpRate = (long)(psdBonusExpRate + this.incExp * 0.01 * chr.getStat().expGuild);
      }

      if (psdBonusExpRate > 0L) {
         this.expDataMap.put(ExpIncreaseInfo.ExpIncreaseFlag.PsdBonusExpRate, psdBonusExpRate);
      }
   }

   public void setIndieBonusExp(MapleCharacter chr, MapleMonster monster) {
      long buffExp = 0L;
      if (chr.getBuffedValue(SecondaryStatFlag.HolySymbol) != null) {
         buffExp = (long)(buffExp + chr.getBuffedValue(SecondaryStatFlag.HolySymbol).intValue() * (this.incExp / 100.0));
      }

      MobTemporaryStatEffect ms = monster.getStati().get(MobTemporaryStatFlag.SHOWDOWN);
      if (ms != null) {
         buffExp = (long)(buffExp + this.incExp * (ms.getX().intValue() / 100.0));
      }

      Integer dice = chr.getBuffedValue(SecondaryStatFlag.Dice);
      if (dice != null) {
         int[] diceStat = (int[])chr.getJobField("diceStatData");
         int diceBonusExpRate = diceStat[16];
         if (diceBonusExpRate > 0) {
            buffExp = (long)(buffExp + this.incExp / 100.0 * diceBonusExpRate);
         }
      }

      if (buffExp > 0L) {
         this.expDataMap.put(ExpIncreaseInfo.ExpIncreaseFlag.IndieBonusExp, buffExp);
      }
   }

   public void setBaseAddExp(MapleCharacter chr) {
      long baseAddExp = 0L;
      baseAddExp = (long)(baseAddExp + this.incExp * (chr.getStat().expBuff / 100.0 - 1.0));
      if (chr.getIndieTemporaryStats(SecondaryStatFlag.indieEXP) != null) {
         for (IndieTemporaryStatEntry indie : chr.getIndieTemporaryStats(SecondaryStatFlag.indieEXP)) {
            if (indie != null) {
               baseAddExp = (long)(baseAddExp + indie.getStatValue() * (this.incExp / 100.0));
            }
         }
      }

      if (baseAddExp > 0L) {
         this.expDataMap.put(ExpIncreaseInfo.ExpIncreaseFlag.BaseAddExp, baseAddExp);
      }
   }

   public void setRestFieldBonusExp(MapleCharacter chr) {
      long restFieldBonusExp = (long)(this.incExp * (chr.getMap().getBreakTimeFieldExpRate() * 0.01));
      if (restFieldBonusExp > 0L) {
         this.expDataMap.put(ExpIncreaseInfo.ExpIncreaseFlag.RestFieldBonusExp, restFieldBonusExp);
      }
   }

   public long getAllExp() {
      long exp = this.incExp;

      for (Long value : this.expDataMap.values()) {
         exp += value;
      }

      return exp;
   }

   public void decode(PacketEncoder packet) {
      packet.write(this.isLastHit);
      packet.writeLong(this.baseExp);
      packet.write(this.onQuest);
      packet.writeInt(this.decState);
      if (this.decState > 0) {
         packet.writeLong(-this.decExp);
      }

      long totalFlag = this.getFlag();
      packet.writeLong(totalFlag);
      if (ExpIncreaseInfo.ExpIncreaseFlag.SelectedMobBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.SelectedMobBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.PartyBonusPercentage.check(totalFlag)) {
         try {
            packet.write(Math.toIntExact(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.PartyBonusPercentage)));
         } catch (ArithmeticException var5) {
            packet.write(0);
         }
      }

      if (this.onQuest) {
         packet.write(this.questBonusRate);
         if (this.questBonusRate > 0) {
            packet.write(this.questBonusRemainCount);
         }
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.WeddingBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.WeddingBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.PartyBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.PartyBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.ItemBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.ItemBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.PremiumIPBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.PremiumIPBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.RainbowWeekEventBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.RainbowWeekEventBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.BoomUpEventBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.BoomUpEventBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.PlusExpBuffBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.PlusExpBuffBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.PsdBonusExpRate.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.PsdBonusExpRate));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.IndieBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.IndieBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.RelaxBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.RelaxBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.InstallItemBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.InstallItemBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.AswanWinnerBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.AswanWinnerBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.ExpByIncExpR.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.ExpByIncExpR));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.ValuePackBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.ValuePackBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.ExpByIncPQExpR.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.ExpByIncPQExpR));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.BaseAddExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.BaseAddExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.BloodAllianceBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.BloodAllianceBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.FreezeHotEventBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.FreezeHotEventBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.RestFieldBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.RestFieldBonusExp));
         packet.writeInt(this.restFieldExpRate);
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.UserHPRateBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.UserHPRateBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.FieldValueBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.FieldValueBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.MobKillBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.MobKillBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.LiveEventBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.LiveEventBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.PremiumIPBonusExpNew.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.PremiumIPBonusExpNew));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.FieldValueBonusExpNew.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.FieldValueBonusExpNew));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.SuperPigLuckyBonusExp.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.SuperPigLuckyBonusExp));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.WorldGaugeEventBonusExp1.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.WorldGaugeEventBonusExp1));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.WorldGaugeEventBonusExp2.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.WorldGaugeEventBonusExp2));
      }

      if (ExpIncreaseInfo.ExpIncreaseFlag.WorldGaugeEventBonusExp3.check(totalFlag)) {
         packet.writeLong(this.expDataMap.get(ExpIncreaseInfo.ExpIncreaseFlag.WorldGaugeEventBonusExp3));
      }
   }

   public long getFlag() {
      int totalFlag = 0;

      for (ExpIncreaseInfo.ExpIncreaseFlag flag : this.expDataMap.keySet()) {
         totalFlag = (int)(totalFlag + Math.pow(2.0, flag.getFlag()));
      }

      return totalFlag;
   }

   public static enum ExpIncreaseFlag {
      SelectedMobBonusExp(0),
      PartyBonusPercentage(2),
      PartyBonusExp(4),
      WeddingBonusExp(5),
      ItemBonusExp(6),
      PremiumIPBonusExp(7),
      RainbowWeekEventBonusExp(8),
      BoomUpEventBonusExp(9),
      PlusExpBuffBonusExp(10),
      PsdBonusExpRate(11),
      IndieBonusExp(12),
      RelaxBonusExp(13),
      InstallItemBonusExp(14),
      AswanWinnerBonusExp(15),
      ExpByIncExpR(16),
      ValuePackBonusExp(17),
      ExpByIncPQExpR(18),
      BaseAddExp(19),
      BloodAllianceBonusExp(20),
      FreezeHotEventBonusExp(21),
      RestFieldBonusExp(22),
      UserHPRateBonusExp(23),
      FieldValueBonusExp(24),
      MobKillBonusExp(25),
      LiveEventBonusExp(26),
      PremiumIPBonusExpNew(27),
      FieldValueBonusExpNew(28),
      SuperPigLuckyBonusExp(29),
      WorldGaugeEventBonusExp1(30),
      WorldGaugeEventBonusExp2(31),
      WorldGaugeEventBonusExp3(32);

      private final int flag;

      private ExpIncreaseFlag(int flag) {
         this.flag = flag;
      }

      public int getFlag() {
         return this.flag;
      }

      public boolean check(long value) {
         return ((long)Math.pow(2.0, this.flag) & value) != 0L;
      }
   }
}
