package constants;

import java.util.List;

import com.google.common.collect.ImmutableMap;

public enum QuestExConstants {
   SixthJobQuest(1488),
   SixthJobSkill(1489),
   HasteEventInit(16402),
   HasteEventNormalMob(16403),
   HasteEventEliteMob(16404),
   HasteEventEliteBoss(16405),
   HasteEventRandomPortal(16406),
   HasteEventFireWolf(16407),
   HasteEventRuneAct(16408),
   HasteEventSuddenMK(16409),
   HasteEventComboKill(16410),
   HasteEventMultiKill(16411),
   UnionRankInfo(18771),
   UnionMobInfo(18792),
   CashCodyPreset(27043),
   SuddenMKInit(49000),
   SuddenMKNormalMob1(49001),
   SuddenMKNormalMob2(49002),
   SuddenMKNormalMob3(49003),
   SuddenMKComboKill1(49004),
   SuddenMKComboKill2(49005),
   SuddenMKComboKill3(49006),
   SuddenMKMultiKill1(49007),
   SuddenMKMultiKill2(49008),
   SuddenMKMultiKill3(49009),
   SuddenMKRuneAct(49010),
   SuddenMKRestField(49011),
   SuddenMKEliteMob1(49012),
   SuddenMKEliteMob2(49013),
   SuddenMKEliteMob3(49014),
   SuddenMKEliteBoss(49015),
   SuddenMKFireWolf(49016),
   SuddenMKRandomPortal(49017),
   SuddenMKInnerPortal(49018),
   KillPoint(100161),
   PinkBeanColor(100229),
   UnionCoin(500629),
   UnionPreset(500630),
   HasteEvent(500862),
   NeoCoreEvent(501215),
   NeoEventNormalMob(501225),
   NeoEventEliteMob(501226),
   NeoEventRuneAct(501227),
   NeoEventRandomPortal(501228),
   NeoEventAdventureLog(501229),
   IntensePowerCrystal(501619),
   RedCubeInfo(501930),
   BlackCubeInfo(501931),
   AdditionalCubeInfo(501932),
   WhiteAdditionalCubeInfo(501933),
   Horntail(997312),
   PinkBeen(997313),
   ChaosPinkBeen(997314),
   Arkarium(997315),
   Magnus(997316),
   HardMagnus(997317),
   VonLeon(997318),
   Cygnus(997319),
   Pierre(997320),
   ChaosPierre(997321),
   VonBon(997322),
   ChaosVonBon(997323),
   CrimsonQueen(997324),
   ChaosCrimsonQueen(997325),
   Vellum(997326),
   ChaosVellum(997327),
   Hillah(997328),
   Zakum(997329),
   ChaosZakum(997330),
   SerniumSeren(39932),
   CubeLevelUp(123456783),
   CustomQuests(123456784),
   JinQuestExAccount(123456785),
   JinRestPointReward(123456786),
   JinQuestEx(123456787),
   WeeklyQuestResetCount(123456788),
   DailyQuestResetCount(123456789),
   DisconnectBuffList(1000001);

   private final int questID;
   public static final ImmutableMap<Integer, Integer> bossEnterQuests = ImmutableMap.<Integer, Integer>builder()
      .put(31851, 115)
      .put(31833, 155)
      .put(31498, 155)
      .put(31686, 155)
      .put(34735, 155)
      .put(3470, 115)
      .put(30007, 125)
      .put(3170, 125)
      .put(31179, 140)
      .put(31198, 155)
      .put(31152, 165)
      .put(33294, 190)
      .put(34015, 190)
      .put(36013, 210)
      .build();
   public static final ImmutableMap<Integer, Integer> bossQuests = ImmutableMap.<Integer, Integer>builder()
      .put(8500002, 3655)
      .put(8500012, 3656)
      .put(8500022, 3657)
      .put(8644612, 0)
      .put(8644650, 3680)
      .put(8644655, 3682)
      .put(8645009, 3681)
      .put(8645066, 3683)
      .put(8800002, 3654)
      .put(8800022, 6994)
      .put(8800102, 15166)
      .put(8810018, 3789)
      .put(8810122, 3790)
      .put(8810214, 3651)
      .put(8820001, 3652)
      .put(8820212, 3653)
      .put(8840000, 3794)
      .put(8840007, 3793)
      .put(8840014, 3795)
      .put(8850005, 31095)
      .put(8850006, 31096)
      .put(8850007, 31097)
      .put(8850008, 31098)
      .put(8850009, 31099)
      .put(8850011, 31196)
      .put(8850111, 31199)
      .put(8860000, 3792)
      .put(8860005, 3791)
      .put(8870000, 3649)
      .put(8870100, 3650)
      .put(8880000, 3992)
      .put(8880002, 3993)
      .put(8880010, 3996)
      .put(8880100, 3663)
      .put(8880101, 34018)
      .put(8880110, 3662)
      .put(8880111, 34017)
      .put(8880140, 3659)
      .put(8880141, 3660)
      .put(8880142, 3684)
      .put(8880150, 34354)
      .put(8880151, 3661)
      .put(8880153, 34356)
      .put(8880155, 3685)
      .put(8880156, 34349)
      .put(8880167, 34368)
      .put(8880177, 34369)
      .put(8880200, 3591)
      .put(8880301, 3666)
      .put(8880302, 3658)
      .put(8880303, 3664)
      .put(8880304, 3665)
      .put(8880341, 3669)
      .put(8880342, 3670)
      .put(8880343, 3667)
      .put(8880344, 3668)
      .put(8880400, 3671)
      .put(8880405, 3672)
      .put(8880410, 3673)
      .put(8880415, 3674)
      .put(8880502, 3676)
      .put(8880503, 3677)
      .put(8880505, 3675)
      .put(8880518, 3679)
      .put(8880519, 3678)
      .put(8880600, 3686)
      .put(8880602, 3687)
      .put(8880614, 3687)
      .put(8881000, 0)
      .put(8900000, 30043)
      .put(8900001, 30043)
      .put(8900002, 30043)
      .put(8900003, 30043)
      .put(8900100, 30032)
      .put(8900101, 30032)
      .put(8900102, 30032)
      .put(8900103, 0)
      .put(8910000, 30044)
      .put(8910100, 30039)
      .put(8920000, 30045)
      .put(8920001, 30045)
      .put(8920002, 30045)
      .put(8920003, 30045)
      .put(8920006, 30045)
      .put(8920100, 30033)
      .put(8920101, 30033)
      .put(8920102, 30033)
      .put(8920103, 30033)
      .put(8920106, 30033)
      .put(8930000, 30046)
      .put(8930100, 30041)
      .put(8950000, 33261)
      .put(8950001, 33262)
      .put(8950002, 33263)
      .put(8950100, 33301)
      .put(8950101, 33302)
      .put(8950102, 33303)
      .put(9101078, 15172)
      .put(9101190, 0)
      .put(9309200, 0)
      .put(9309201, 0)
      .put(9309203, 0)
      .put(9309205, 0)
      .put(9309207, 0)
      .build();

   private QuestExConstants(int questID) {
      this.questID = questID;
   }

   public int getQuestID() {
      return this.questID;
   }

   public static QuestExConstants getQuest(int questID) {
      for (QuestExConstants quest : values()) {
         if (quest.getQuestID() == questID) {
            return quest;
         }
      }

      return null;
   }
}
