package scripting;

public enum ScriptMessageType {
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
   AskMixHair(41),
   AskMixHairZero(42),
   AskCustomMixHair(43),
   AskCustomMixHairAndProb(44),
   AskMixHairNew(45),
   AskMixHairNewZero(46),
   InputUI(46),
   AskNumberKeyPad(47),
   SpinOffGuitarRhythmGame(48),
   AskGhostParkEnterUI(49),
   CameraMsg(54),
   SlidePuzzle(51),
   Disguise(52),
   NeedClientResponse(53);

   private int type;

   private ScriptMessageType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }
}
