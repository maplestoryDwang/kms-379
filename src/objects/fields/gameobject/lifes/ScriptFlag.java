package objects.fields.gameobject.lifes;

public enum ScriptFlag {
   Say(0),
   UNK_1(1),
   SayImage(2),
   AskYesNo(3),
   AskText(4),
   AskNumber(5),
   AskMenu(6),
   AskInitialQuiz(7),
   AskSpeedQuiz(8),
   AskICQuiz(9),
   AskAvatar(10),
   AskAndroid(11),
   AskPet(12),
   AskPetAll(13),
   AskActionPetEvolution(14),
   Script(15),
   AskAccept(16),
   AskAccept2(17),
   AskBoxText(18),
   AskSlideMenu(19),
   AskIngameDirection(20),
   PlayMovieClip(21),
   PlayMovieClipWeb(22),
   AskCenter(23),
   AskSelectMenu(25),
   AskAngelicBuster(26),
   Say_Illustration(27),
   SayDual_Illustration(28),
   AskYesNo_Illustration(29),
   AskAccept_Illustration(30),
   AskMenu_Illustration(31),
   AskYesNoDual_Illustration(32),
   AskAcceptDual_Illustration(33),
   AskMenuDual_Illustration(34),
   AskSSN2(35),
   AskAvatarZero(36),
   Monologue(37),
   AskWeaponBox(38),
   AskBoxText_BgIMG(39),
   AskUserSurvey(40),
   SuccessCamera(41),
   AskMixHair(42),
   AskMixHairZero(43),
   AskCustomMixHair(44),
   AskCustomMixHairAndProb(45),
   AskMixHairNew(46),
   AskMixHairNewZero(47),
   NpcAction(48),
   AskScreenShiningStarMsg(49),
   InputUI(50),
   AskNumberKeyPad(51),
   SpinOffGuitarRhythmGame(52),
   AskGhostParkEnterUI(53),
   CameraMsg(54),
   SlidePuzzle(55),
   Disguise(56),
   NeedClientResponse(57),
   UNK_57(58),
   UNK_58(59),
   UNK_59(60),
   UNK_60(61);

   private final int value;

   private ScriptFlag(int val) {
      this.value = val;
   }

   public int getValue() {
      return this.value;
   }

   public static ScriptFlag getByFlag(int flag) {
      for (ScriptFlag f : values()) {
         if (f.value == flag) {
            return f;
         }
      }

      return null;
   }
}
