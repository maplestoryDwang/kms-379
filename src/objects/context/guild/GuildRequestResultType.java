package objects.context.guild;

public class GuildRequestResultType {
   public static enum Request {
      None(-1),
      CreateGuild_CheckName(1),
      CreateGuild_DoCreate(2),
      JoinGuild(3),
      CancelJoinRequest(4),
      AcceptJoinRequest(6),
      DeclineJoinRequest(7),
      WithdrawGuild(8),
      KickMember(9),
      ChangeRank(12),
      ChangeRankRole(14),
      ChangeMark(18),
      ChangeNotice(19),
      EditJoinSetting(20),
      ChangeLeader(32),
      VisitGuild(34),
      LoadJoinRequest(35),
      InviteGuild(36),
      AttendGuild(37),
      LoadGuildNotice(40),
      SkillLevelUp(41),
      UseGuildSkill(44),
      GuildCustomMarkRequest(45),
      SearchGuild(48),
      WithdrawGuildInAlliance(50),
      InviteAlliance(51),
      AttendAlliance(52),
      KickGuildInAlliance(54),
      ChangeAllianceReader(55),
      ChangeAllianceRankRole(56),
      ChangeAllianceRank(57);

      private final int flag;

      private Request(int flag) {
         this.flag = flag;
      }

      public int getFlag() {
         return this.flag;
      }

      public static GuildRequestResultType.Request getByType(int flag) {
         for (GuildRequestResultType.Request r : values()) {
            if (r.flag == flag) {
               return r;
            }
         }

         return null;
      }
   }

   public static enum Result {
      None(-1),
      CreateGuild_Request(0),
      GuildInit(1),
      CreateGuild_InitGuild(7),
      JoinMember_InitGuild(13),
      VisitGuild(3),
      CreateGuild_AvailableName(4),
      CreateGuild_NotAvailableName(5),
      MemberAlreadyExistGuild(8),
      AlreadyCreateGuild(9),
      ErrorOnCreateGuild(10),
      ErrorOnCreateGuild2(11),
      CancelCreateGuild(12),
      JoinMember(14),
      MemberAlreadyExistGuild2(15),
      MemberAlreadyExistGuild3(16),
      JoinGuildMemberIsFull(17),
      JoinGuildRequestIsFull(18),
      GuildNotAllowJoin(19),
      NotFoundCharacter(20),
      NotFoundJoinCharacter(21),
      Unknown_23(23),
      DeleteJoinRequester(25),
      DeleteJoinRequester_1MinBlock(32),
      InsertJoinRequester(24),
      UpdateRecruitmentGuild(26),
      RequestJoinGuildCanFive(28),
      NotYetPossibleJoinGuild(29),
      AlreadyRequestJoinGuild(30),
      WithdrawGuild_DeleteAccount(33),
      WithdrawGuildResult(34),
      KickMember(37),
      GuildNotFound(35),
      GuildNotFound2(38),
      DisbandGuild(40),
      DisbandGuildError(42),
      DisbandGuildInfo(43),
      NotAllowInviteGuild(44),
      ProcessingOtherRequest(45),
      DeclineInvite(46),
      InviteMessage(47),
      InviteGuild(48),
      UnknownGuildError(49),
      NotFoundCharacter2(50),
      GuildNotFound3(51),
      AlreadyJoinedGuild(52),
      NoMoreInviteGuild(53),
      GMCantCreateGuild(54),
      ChangeCapacity(56),
      ChangeCapacityError(57),
      Unknown_58(58),
      JoinRequesterChangeName(59),
      Unknown_60(60),
      MemberLogOnOff(61),
      MemberMarriageResult(62),
      ChangeGuildMemberRole(63),
      UnknownGuildRequestError(64),
      UnknownGuildRequestError2(65),
      UnknownGuildRequestError3(66),
      CantChangeGuildMemberRole(67),
      ChangeRankRole(68),
      AddRank(70),
      RemoveRank(72),
      ChangeRank(74),
      AddIGPLog(76),
      ChangeEmblem(78),
      ChangeEmblemError(79),
      CantChangeEmblem(80),
      CantChangeEmblem1(80),
      CantChangeEmblem3(82),
      CantChangeEmblem4(85),
      ChangeEmblemCooldown(83),
      ChangeEmblemRequireGuildLevel(84),
      ChangeEmblemExceedByte(86),
      GuildOperationIsBlocked(87),
      UpdateNotice(88),
      EditJoinSetting(90),
      StartRecruitmentGuild(92),
      CantStartRecruitmentGuild(94),
      UpdateGuildPoint(98),
      Unknown_99(99),
      ResetEmblem(100),
      GuildBattleNotEnoughMember(101),
      GuildBattleRequesterDisconnectGame(102),
      GuildBattleInformation(103),
      Unknown_104(104),
      ResetGuildPoint(105),
      PurchaseGuildSkill(106),
      PurchaseGuildSklilError(107),
      UseGuildSkill(108),
      NotEnoughGuildPointForSkillReset(110),
      AlreadyAllGuildSkillReset(111),
      GuildSkillResetError(112),
      GuildSkillResetError2(113),
      Unknown_114(114),
      Unknown_115(115),
      NotYetCanGuildSkill(116),
      NotEnoughGuildPoint(117),
      Unknown_118(118),
      ChangeLeader(119),
      Unknown_121(121),
      Unknown_122(122),
      Unknown_123(123),
      CantBeGuildMaster(126),
      Attendance(127),
      Unknown_128(128),
      LoadGuildID(129),
      AttendanceInit(130),
      AttendanceInfo(131),
      LoadJoinRequestList(132),
      Unknown_133(133),
      Unknown_136(136),
      ChangeguildLeaderError(134),
      Unknown_135(135),
      UpdateCustomGuildMark(137),
      NotYetPossible(138),
      CreateAlliance(139),
      WithdrawGuildInAlliance(140),
      DisbandAllianceError(141),
      JoinGuildInAlliance(142),
      Unknown_143(143),
      Unknown_150(150),
      DeclineInviteAlliance(146),
      ChangeAllianceLeader(147),
      ChangeAllianceRankRole(148),
      ChangeAllianceRank(149),
      Unknown_151(151),
      DisbandAlliance(152);

      private final int flag;

      private Result(int flag) {
         this.flag = flag;
      }

      public int getFlag() {
         return this.flag;
      }

      public static GuildRequestResultType.Result getByType(int flag) {
         for (GuildRequestResultType.Result r : values()) {
            if (r.flag == flag) {
               return r;
            }
         }

         return null;
      }
   }
}
