package objects.users.extra;

import objects.users.stats.SecondaryStatFlag;

public enum ExtraAbilityOption {
   None(-1, "없음", false),
   DamageReduceR(0, "아케인 포스 +%d 증가", false, SecondaryStatFlag.indieArc),
   Str(1, "STR +%d 증가", false, SecondaryStatFlag.indieSTR),
   Dex(2, "DEX +%d 증가", false, SecondaryStatFlag.indieDEX),
   Int(3, "INT +%d 증가", false, SecondaryStatFlag.indieINT),
   Luk(4, "LUK +%d 증가", false, SecondaryStatFlag.indieLUK),
   AllStat(5, "올스탯 +%d 증가", false, SecondaryStatFlag.indieAllStat),
   MaxHp(6, "최대 HP +%d 증가", false, SecondaryStatFlag.indieMHP),
   Attack(7, "공격력/마력 +%d 증가", false, SecondaryStatFlag.indieMAD, SecondaryStatFlag.indiePAD),
   MaxHpR(8, "최대 HP +%d%s 증가", false, SecondaryStatFlag.indieMHPR),
   AllStatR(9, "올스탯 +%d%s 증가 (직접 투자한 스탯)", false, SecondaryStatFlag.indieStatRBasic),
   AttackR(10, "공격력/마력 +%d%s 증가", false, SecondaryStatFlag.indieMadR, SecondaryStatFlag.indiePadR),
   CriticalRate(11, "크리티컬 확률 +%d%s 증가", false, SecondaryStatFlag.indieCR),
   IgnoreMobPdpR(12, "몬스터 방어력 무시 +%d%s", false, SecondaryStatFlag.indieIgnoreMobPdpR),
   BossDamageR(13, "보스 공격 시 데미지 +%d%s 증가", false, SecondaryStatFlag.indieBDR),
   ReduceCooltime(14, "재사용 대기시간 %d초 감소", true, SecondaryStatFlag.indieCooltimeReduce),
   MesoRateR(15, "메소 획득량 +%d%s 증가", true, SecondaryStatFlag.indieMesoAmountRate),
   DropRateR(16, "아이템 드롭률 +%d%s 증가", true, SecondaryStatFlag.indieDropPer),
   ExpRateR(17, "경험치 획득량 +%d%s 증가", true, SecondaryStatFlag.indieEXP),
   CriticalDamage(18, "크리티컬 데미지 +%d%s 증가", false, SecondaryStatFlag.indieCD),
   IncMobGen(19, "몬스터 리젠 개체수 1.5배 증가", true, null),
   ReviveInvincible(20, "부활 시 무적 시간 %d초 증가", true, null),
   TerR(21, "상태 이상 내성 +%d%s 증가", true, SecondaryStatFlag.indieTerR, SecondaryStatFlag.indieAsrR),
   PMDR(22, "최종 데미지 +%d%s 증가", true, SecondaryStatFlag.indiePMDR);

   private int option;
   private String desc;
   private SecondaryStatFlag[] flag;
   private boolean extraOption;

   private ExtraAbilityOption(int option, String desc, boolean extraOption, SecondaryStatFlag... flag) {
      this.option = option;
      this.desc = desc;
      this.flag = flag;
      this.setExtraOption(extraOption);
   }

   public ExtraAbilityOption getOption() {
      for (ExtraAbilityOption g : values()) {
         if (g.option == this.option) {
            return g;
         }
      }

      return null;
   }

   public ExtraAbilityOption getGrade(int option) {
      for (ExtraAbilityOption g : values()) {
         if (g.option == option) {
            return g;
         }
      }

      return null;
   }

   public String getDesc() {
      return this.desc;
   }

   public SecondaryStatFlag[] getFlag() {
      return this.flag;
   }

   public int getOptionID() {
      return this.option;
   }

   public SecondaryStatFlag[] getFlag(ExtraAbilityOption option) {
      for (ExtraAbilityOption g : values()) {
         if (g.option == option.option) {
            return g.flag;
         }
      }

      return null;
   }

   public static ExtraAbilityOption getByName(String name) {
      for (ExtraAbilityOption option : values()) {
         if (option.name().equals(name)) {
            return option;
         }
      }

      return null;
   }

   public static ExtraAbilityOption getByOption(int option) {
      for (ExtraAbilityOption o : values()) {
         if (o.option == option) {
            return o;
         }
      }

      return null;
   }

   public boolean isExtraOption() {
      return this.extraOption;
   }

   public void setExtraOption(boolean extraOption) {
      this.extraOption = extraOption;
   }
}
