package objects.users.stone;

import objects.users.stats.SecondaryStatFlag;

public enum ImprintedStoneOption {
   None(0, "없음", false),
   AllStatP(1, "올스탯 %s%d%s%s", false, SecondaryStatFlag.indieStatRBasic),
   PadR(2, "공격력 %s%d%s%s", true, SecondaryStatFlag.indiePadR),
   MadR(3, "마력 %s%d%s%s", true, SecondaryStatFlag.indieMadR),
   CriticalDamageR(4, "크리티컬 데미지 %s%d%s%s", true, SecondaryStatFlag.indieCD),
   BossDamageR(5, "보스 공격 시 데미지 %s%d%s%s", true, SecondaryStatFlag.indieBDR),
   IgnoreMobPdpR(6, "몬스터 방어력 무시 %s%d%s", false, SecondaryStatFlag.indieIgnoreMobPdpR),
   CriticalRate(7, "크리티컬 확률 %s%d%s%s", false, SecondaryStatFlag.indieCR),
   PMDR(8, "최종 데미지 %s%d%s%s", true, SecondaryStatFlag.indiePMDR),
   DamR(9, "데미지 %s%d%s%s", false, SecondaryStatFlag.indieDamR);

   private int option;
   private String desc;
   private boolean special;
   private SecondaryStatFlag[] flag;

   private ImprintedStoneOption(int option, String desc, boolean special, SecondaryStatFlag... flag) {
      this.option = option;
      this.desc = desc;
      this.special = special;
      this.flag = flag;
   }

   public int getOption() {
      return this.option;
   }

   public String getDesc() {
      return this.desc;
   }

   public static ImprintedStoneOption getByOption(int option) {
      for (ImprintedStoneOption o : values()) {
         if (o.option == option) {
            return o;
         }
      }

      return null;
   }

   public SecondaryStatFlag[] getFlag() {
      return this.flag;
   }
}
