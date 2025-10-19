package objects.context.guild;

public enum GuildMemberPermissionFlag {
   Invite(1),
   EditIntroduce(2),
   ModifyMemberRank(4),
   EditGuildMark(8),
   KickMember(16),
   BoardManagement(32),
   AcceptJoin(64),
   SkillManagement(256),
   PossibleUseSkill(1024),
   CulvertEnterManagement(16384);

   private final int flag;

   private GuildMemberPermissionFlag(int flag) {
      this.flag = flag;
   }

   public int getFlag() {
      return this.flag;
   }
}
