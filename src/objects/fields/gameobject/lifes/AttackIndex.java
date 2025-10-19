package objects.fields.gameobject.lifes;

public enum AttackIndex {
   FieldEtc(-10),
   PartsSystem(-9),
   MobSkill(-8),
   FieldSkill(-7),
   Dead(-6),
   ObstacleAtom(-5),
   Stat(-4),
   Obstacle(-3),
   Counter(-2),
   Mob_Body(-1),
   Mob_Attack1(0),
   Mob_Attack2(1),
   Mob_Attack3(2),
   Mob_Attack4(3),
   Mob_Attack5(4),
   Mob_Attack6(5),
   Mob_Attack7(6),
   Mob_Attack8(7),
   Mob_Attack9(8),
   Mob_Attack10(9),
   Mob_Attack11(10),
   Mob_Attack12(11),
   Mob_Attack13(12),
   Mob_Attack14(13),
   Mob_Attack15(14),
   Mob_Attack16(15),
   Mob_AttackF(16);

   private int index;

   private AttackIndex(int index) {
      this.index = index;
   }

   public int getIndex() {
      return this.index;
   }

   public static AttackIndex get(int index) {
      for (AttackIndex i : values()) {
         if (i.getIndex() == index) {
            return i;
         }
      }

      return null;
   }

   public static void main(String[] args) {
      int index = Mob_AttackF.getIndex() | Mob_Attack11.getIndex() | Mob_Attack9.getIndex() | Mob_Attack3.getIndex() | Dead.getIndex() | MobSkill.getIndex();
   }
}
