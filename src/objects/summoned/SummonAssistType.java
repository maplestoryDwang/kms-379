package objects.summoned;

public enum SummonAssistType {
   None(0),
   Attack(1),
   Heal(2),
   AttackEx(3),
   AttackEx2(4),
   AttackEx3(5),
   Summon(6),
   AttackManual(7),
   AttackCounter(8),
   CreateArea(9),
   AttackBodyGuard(10),
   AttackJaguar(11),
   AttackB2Body(12),
   AttackForceAtom(13),
   AttackGuard(14),
   AttackProtector(15),
   AttackBomb(16),
   AttackCrew(17),
   AttackSpider(18),
   UnkAssistType18(19),
   UnkAssistType19(20);

   private int type;

   private SummonAssistType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
