package objects.fields.gameobject.lifes;

public enum MobMoveAction {
   Null(-1),
   Move(0),
   Stand(1),
   Jump(2),
   Fly(3),
   Rope(4),
   Regen(5),
   Bomb(6),
   Hit1(7),
   Hit2(8),
   HitF(9),
   Die1(10),
   Die2(11),
   DieF(12),
   Attack1(13),
   Attack2(14),
   Attack3(15),
   Attack4(16),
   Attack5(17),
   Attack6(18),
   Attack7(19),
   Attack8(20),
   Attack9(21),
   Attack10(22),
   Attack11(23),
   Attack12(24),
   Attack13(25),
   Attack14(26),
   Attack15(27),
   Attack16(28),
   AttackF(29),
   Skill1(30),
   Skill2(31),
   Skill3(32),
   Skill4(33),
   Skill5(34),
   Skill6(35),
   Skill7(36),
   Skill8(37),
   Skill9(38),
   Skill10(39),
   Skill11(40),
   Skill12(41),
   Skill13(42),
   Skill14(43),
   Skill15(44),
   Skill16(45),
   SkillF(46),
   Chase(47),
   Miss(48),
   Say(49),
   Eye(50),
   SkillAfter1(51),
   SkillAfter2(52),
   SkillAfter3(53),
   SkillAfter4(54),
   SkillAfter5(55),
   SkillAfter6(56),
   SkillAfter7(57),
   SkillAfter8(58),
   SkillAfter9(59),
   SkillAfter10(60),
   SkillAfter11(61),
   SkillAfter12(62),
   SkillAfter13(63),
   SkillAfter14(64),
   SkillAfter15(65),
   SkillAfter16(66),
   Sleep(67),
   WakeUp(68),
   Patrol_Sense(69),
   Patrol_UserDetect(70),
   Patrol_AttractDetect(71),
   Patrol_AttractArrive(72),
   Transform(73),
   Skill_Use(74),
   Skill_Fail(75),
   Revive(76),
   Sealed(77),
   RunAway(78),
   DirectionAct1(79);

   private int type;

   private MobMoveAction(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public static MobMoveAction getAction(int type) {
      for (MobMoveAction action : values()) {
         if (action.getType() == type) {
            return action;
         }
      }

      return null;
   }
}
