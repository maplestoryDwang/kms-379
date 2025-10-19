package objects.fields.gameobject.lifes;

import java.io.Serializable;
import objects.users.stats.CTS;
import objects.users.stats.SecondaryStatFlag;

public enum MobTemporaryStatFlag implements Serializable, CTS {
   INDIE_UNK(0, true),
   INDIE_PDR(1, true),
   INDIE_MDR(2, true),
   Indie_Additional_DamageR(3, true),
   __END_OF_INDIE_STATS__(4),
   PAD(5),
   PDR(6),
   MAD(7),
   MDR(8),
   ACC(9),
   EVA(10),
   SPEED(11),
   STUN(12),
   FREEZE(13),
   UNK_14(14),
   POISON(15),
   SEAL(16),
   DARKNESS(17),
   POWER_UP(18),
   MAGIC_UP(19),
   P_GUARD_UP(20),
   M_GUARD_UP(21),
   P_IMMUNE(22),
   M_IMMUNE(23),
   WEB(24),
   HARD_SKIN(25),
   NINJA_AMBUSH(26),
   VENOM(27),
   BLIND(28),
   SEAL_SKILL(29),
   DAZZLE(30),
   P_COUNTER(31),
   M_COUNTER(32),
   RISE_BY_TOSS(33),
   BODY_PRESSURE(34),
   WEAKNESS(35),
   SHOWDOWN(36),
   UNK_37(37),
   MAGIC_CRASH(38),
   WEAKNESS2(39),
   ADD_DAM_PARTY(40),
   HIT_CRI_DAM_R(41),
   FATALITY(42),
   LIFTING(43),
   DEADLY_CHARGE(44),
   SMITE(45),
   ADD_DAM_SKILL(46),
   INCIZING(47),
   DODGE_BODY_ATTACK(48),
   DEBUFF_HEALING(49),
   ADD_DAM_SKILL_2(50),
   BODY_ATTACK(51),
   TEMP_MOVE_AVILITY(52),
   FIX_DAM_R_BUFF(53),
   GHOST_DISPOSITION(54),
   ELEMENT_DARKNESS(55),
   AREA_INSTALL_BY_HIT(56),
   B_MAGE_DEBUFF(57),
   JAGUAR_PROVOKE(58),
   JAGUAR_BLEEDING(59),
   PINKBEAN_FLOWER_POT(60),
   BATTLE_PVP_HELENA_MARK(61),
   PSYCHIC_LOCK(62),
   PSYCHIC_LOCK_COOLTIME(63),
   PSYCHIC_GROUND_MARK(64),
   POWER_IMMUNE(65),
   MULTI_PMDR(66),
   ELEMENT_RESET_BY_SUMMON(67),
   BAHAMUT_LIGHT_ELEM_ADD_DAM(68),
   CURSE_MARK(69),
   BOSS_PROP_PLUS(70),
   MULTI_DAM_SKILL(71),
   RW_LIFT_PRESS(72),
   RW_CHOPPING_HAMMER(73),
   UNK_74(74),
   MULTI_PMDR2(75),
   TIME_CURSE(76),
   ALARM_MODE(77),
   UNK_78(78),
   UNK_79(79),
   UNK_80(80),
   HIDDEN_DEBUFF(81),
   ANCIENT_CURSE(82),
   PINPOINT_PIERCE_DEBUFF(83),
   TRANSFORMATION(84),
   SUCTION_BOTTLE(85),
   TIME_BOMB(86),
   ADD_EFFECT(87),
   UNK_88(88),
   THESEED_STACK(89),
   UNK_90(90),
   UNK_91(91),
   UNK_92(92),
   UNK_93(93),
   UNK_94(94),
   UNK_95(95),
   UNK_96(96),
   HILLAH_STACK(97),
   INVINCIBLE(98),
   EXPLOSION(99),
   HANG_OVER(100),
   CASTING(101),
   UNK_102(102),
   UNK_103(103),
   BURNED(104),
   BALOG_DISABLE(105),
   EXCHANGE_ATTACK(106),
   ADD_BUFF_STAT(107),
   LINK_TEAM(108),
   SOUL_EXPLOSTION(109),
   SEPERATE_SOUL_P(110),
   SEPERATE_SOUL_C(111),
   TRUE_SIGHT(112),
   LASER(113),
   UNK_114(114),
   UNK_115(115),
   UNK_116(116),
   UNK_117(117),
   UNK_118(118),
   UNK_119(119),
   UNK_120(120),
   UNK_121(121),
   UNK_122(122),
   UNK_123(123),
   UNK_124(124);

   static final long serialVersionUID = 0L;
   private final int bit;
   private final boolean indieStat;

   private MobTemporaryStatFlag(int bit, boolean indieStat) {
      this.bit = bit;
      this.indieStat = indieStat;
   }

   private MobTemporaryStatFlag(int bit) {
      this.bit = bit;
      this.indieStat = false;
   }

   @Override
   public int getPosition() {
      return 4 - this.bit / 32;
   }

   @Override
   public long getValue() {
      return (long)Math.max(1.0, Math.pow(2.0, 31 - this.bit % 32));
   }

   @Override
   public int getBit() {
      return this.bit;
   }

   @Override
   public boolean isIndie() {
      return this.indieStat;
   }

   public static final SecondaryStatFlag getLinkedDisease(MobTemporaryStatFlag skill) {
      switch (skill) {
         case STUN:
         case WEB:
            return SecondaryStatFlag.Stun;
         case POISON:
         case BURNED:
            return SecondaryStatFlag.Poison;
         case SEAL:
         case MAGIC_CRASH:
            return SecondaryStatFlag.Seal;
         case FREEZE:
            return SecondaryStatFlag.Frozen;
         case DARKNESS:
            return SecondaryStatFlag.Darkness;
         case SPEED:
            return SecondaryStatFlag.Slow;
         default:
            return null;
      }
   }
}
