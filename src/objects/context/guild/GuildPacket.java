package objects.context.guild;

import java.util.ArrayList;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.PacketHelper;
import objects.context.guild.alliance.Alliance;
import objects.users.MapleCharacter;

public class GuildPacket {
   public static void sendGuildPacket(MapleCharacter player, GuildPacket.GuildResult guildResult) {
      PacketEncoder packet = new PacketEncoder();
      guildResult.encode(packet);
      player.send(packet.getPacket());
   }

   public static byte[] inviteAlliance(int allianceId, String inviter) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.GUILD_SUB_OPERATION.getValue());
      packet.writeInt(GuildSubType.InviteAlliance.getType());
      packet.writeInt(allianceId);
      packet.writeMapleAsciiString(inviter);
      return packet.getPacket();
   }

   public static class AddIGPLog extends GuildPacket.GuildResult {
      int guildID;
      int playerID;
      int totalIGP;
      int todayIGP;

      public AddIGPLog(int guildID, int playerID, int totalIGP, int todayIGP) {
         super(GuildRequestResultType.Result.AddIGPLog);
         this.guildID = guildID;
         this.playerID = playerID;
         this.totalIGP = totalIGP;
         this.todayIGP = todayIGP;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
         packet.writeInt(this.playerID);
         packet.writeInt(this.totalIGP);
         packet.writeInt(this.todayIGP);
         packet.writeLong(PacketHelper.getKoreanTimestamp(System.currentTimeMillis()));
      }
   }

   public static class Attendance extends GuildPacket.GuildResult {
      int guildID;
      int playerID;
      int date;

      public Attendance(int guildID, int playerID, int date) {
         super(GuildRequestResultType.Result.Attendance);
         this.guildID = guildID;
         this.playerID = playerID;
         this.date = date;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
         packet.writeInt(this.playerID);
         packet.writeInt(this.date);
      }
   }

   public static class AttendanceInit extends GuildPacket.GuildResult {
      List<Guild.AttendanceData> list = new ArrayList<>();

      public AttendanceInit() {
         super(GuildRequestResultType.Result.AttendanceInit);
         this.list.add(new Guild.AttendanceData(100, 2000));
         this.list.add(new Guild.AttendanceData(60, 1000));
         this.list.add(new Guild.AttendanceData(30, 100));
         this.list.add(new Guild.AttendanceData(15, 50));
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.list.size());
         this.list.forEach(l -> l.encode(packet));
      }
   }

   public static class ChangeAllianceLeader extends GuildPacket.GuildResult {
      Alliance alliance;

      public ChangeAllianceLeader(Alliance alliance) {
         super(GuildRequestResultType.Result.ChangeAllianceLeader);
         this.alliance = alliance;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.alliance.getGuildId(0));
         packet.writeInt(this.alliance.getId());
         packet.writeInt(this.alliance.getLeaderId());
      }
   }

   public static class ChangeAllianceRank extends GuildPacket.GuildResult {
      int allianceID;
      int guildID;
      int playerID;
      byte newRank;

      public ChangeAllianceRank(int allianceID, int guildID, int playerID, byte newRank) {
         super(GuildRequestResultType.Result.ChangeAllianceRank);
         this.allianceID = allianceID;
         this.guildID = guildID;
         this.playerID = playerID;
         this.newRank = newRank;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.allianceID);
         packet.writeInt(this.guildID);
         packet.writeInt(this.playerID);
         packet.write(this.newRank);
      }
   }

   public static class ChangeAllianceRankRole extends GuildPacket.GuildResult {
      int allianceID;
      int playerID;
      String[] ranks;

      public ChangeAllianceRankRole(int allianceID, int playerID, String[] ranks) {
         super(GuildRequestResultType.Result.ChangeAllianceRankRole);
         this.allianceID = allianceID;
         this.playerID = playerID;
         this.ranks = ranks;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.allianceID);
         packet.writeInt(this.playerID);

         for (int i = 0; i < 5; i++) {
            packet.writeMapleAsciiString(this.ranks[i]);
         }
      }
   }

   public static class ChangeCapacity extends GuildPacket.GuildResult {
      int guildID;
      byte newCapacity;

      public ChangeCapacity(int guildID, byte newCapacity) {
         super(GuildRequestResultType.Result.ChangeCapacity);
         this.guildID = guildID;
         this.newCapacity = newCapacity;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
         packet.write(this.newCapacity);
      }
   }

   public static class ChangeEmblem extends GuildPacket.GuildResult {
      int guildID;
      int playerID;
      short bg;
      byte bgColor;
      short logo;
      byte logoColor;
      byte[] imageData;

      public ChangeEmblem(int guildID, int playerID, short bg, byte bgColor, short logo, byte logoColor, byte[] imageData) {
         super(GuildRequestResultType.Result.ChangeEmblem);
         this.guildID = guildID;
         this.playerID = playerID;
         this.bg = bg;
         this.bgColor = bgColor;
         this.logo = logo;
         this.logoColor = logoColor;
         this.imageData = imageData;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
         packet.writeInt(this.playerID);
         packet.write(1);
         packet.writeShort(this.bg);
         packet.write(this.bgColor);
         packet.writeShort(this.logo);
         packet.write(this.logoColor);
         packet.write(0);
         packet.writeInt(this.imageData != null ? this.imageData.length : 0);
         if (this.imageData != null) {
            packet.encodeBuffer(this.imageData);
         }

         packet.writeInt(this.imageData != null ? 1 : 0);
      }
   }

   public static class ChangeLeader extends GuildPacket.GuildResult {
      int guildID;
      int beforeLeaderID;
      int afterLeaderID;
      byte unk1;
      byte guildRank;

      public ChangeLeader(int guildID, int beforeLeaderID, int afterLeaderID, byte unk1, byte guildRank) {
         super(GuildRequestResultType.Result.ChangeLeader);
         this.guildID = guildID;
         this.beforeLeaderID = beforeLeaderID;
         this.afterLeaderID = afterLeaderID;
         this.unk1 = unk1;
         this.guildRank = guildRank;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
         packet.writeInt(this.beforeLeaderID);
         packet.writeInt(this.afterLeaderID);
         packet.write(this.unk1);
         packet.write(this.guildRank);
      }
   }

   public static class ChangeRank extends GuildPacket.GuildResult {
      int guildID;
      int playerID;
      byte newRank;

      public ChangeRank(int guildID, int playerID, byte newRank) {
         super(GuildRequestResultType.Result.ChangeRank);
         this.guildID = guildID;
         this.playerID = playerID;
         this.newRank = newRank;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
         packet.writeInt(this.playerID);
         packet.write(this.newRank);
      }
   }

   public static class ChangeRankRole extends GuildPacket.GuildResult {
      int guildID;
      int playerID;
      String[] ranks;
      int[] permissions;

      public ChangeRankRole(GuildRequestResultType.Result result, int guildID, int playerID, String[] ranks, int[] permissions) {
         super(result);
         this.guildID = guildID;
         this.playerID = playerID;
         this.ranks = ranks;
         this.permissions = permissions;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
         packet.writeInt(this.playerID);

         for (int i = 0; i < 10; i++) {
            packet.writeInt(this.permissions[i]);
            packet.writeMapleAsciiString(this.ranks[i]);
         }
      }
   }

   public static class CreateAlliance extends GuildPacket.GuildResult {
      Alliance alliance;

      public CreateAlliance(Alliance alliance) {
         super(GuildRequestResultType.Result.CreateAlliance);
         this.alliance = alliance;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         this.alliance.encode(packet);
      }
   }

   public static class CreateGuildRequest extends GuildPacket.GuildResult {
      public CreateGuildRequest() {
         super(GuildRequestResultType.Result.CreateGuild_Request);
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
      }
   }

   public static class CreateGuild_AvailableName extends GuildPacket.GuildResult {
      String name;

      public CreateGuild_AvailableName(String name) {
         super(GuildRequestResultType.Result.CreateGuild_AvailableName);
         this.name = name;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeMapleAsciiString(this.name);
      }
   }

   public static class CreateGuild_InitGuild extends GuildPacket.GuildResult {
      Guild guild;
      int playerID;

      public CreateGuild_InitGuild(Guild guild, int playerID) {
         super(GuildRequestResultType.Result.CreateGuild_InitGuild);
         this.guild = guild;
         this.playerID = playerID;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(50000);
         packet.writeInt(this.playerID);
         packet.writeInt(this.guild.getId());
         packet.write(this.guild.getAllianceId() > 0);
         this.guild.encode(packet, true);
      }
   }

   public static class CreateGuild_NotAvailableName extends GuildPacket.GuildResult {
      public CreateGuild_NotAvailableName() {
         super(GuildRequestResultType.Result.CreateGuild_NotAvailableName);
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
      }
   }

   public static class DeleteJoinRequester extends GuildPacket.GuildResult {
      int playerID;
      int guildID;

      public DeleteJoinRequester(int playerID, int guildID) {
         super(GuildRequestResultType.Result.DeleteJoinRequester);
         this.playerID = playerID;
         this.guildID = guildID;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.playerID);
         packet.writeInt(this.guildID);
      }
   }

   public static class DisbandAlliance extends GuildPacket.GuildResult {
      int allianceID;

      public DisbandAlliance(int allianceID) {
         super(GuildRequestResultType.Result.DisbandAlliance);
         this.allianceID = allianceID;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.allianceID);
      }
   }

   public static class DisbandGuild extends GuildPacket.GuildResult {
      int guildID;

      public DisbandGuild(int guildID) {
         super(GuildRequestResultType.Result.DisbandGuild);
         this.guildID = guildID;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
      }
   }

   public static class EditJoinSetting extends GuildPacket.GuildResult {
      int guildID;
      int playerID;
      boolean allowJoinRequest;
      int connectTimeFlag;
      int activityFlag;
      int ageGroupFlag;

      public EditJoinSetting(int guildID, int playerID, boolean allowJoinRequest, int connectTimeFlag, int activityFlag, int ageGroupFlag) {
         super(GuildRequestResultType.Result.EditJoinSetting);
         this.guildID = guildID;
         this.playerID = playerID;
         this.allowJoinRequest = allowJoinRequest;
         this.connectTimeFlag = connectTimeFlag;
         this.activityFlag = activityFlag;
         this.ageGroupFlag = ageGroupFlag;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
         packet.writeInt(this.playerID);
         packet.write(this.allowJoinRequest);
         packet.writeInt(this.connectTimeFlag);
         packet.writeInt(this.activityFlag);
         packet.writeInt(this.ageGroupFlag);
      }
   }

   public static class GuildInit extends GuildPacket.GuildResult {
      Guild guild;
      int playerID;

      public GuildInit(Guild guild, int playerID) {
         super(GuildRequestResultType.Result.GuildInit);
         this.guild = guild;
         this.playerID = playerID;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(50000);
         packet.writeInt(this.playerID);
         if (this.guild == null) {
            packet.writeInt(0);
         } else {
            packet.writeInt(this.guild.getId());
            packet.write(this.guild.getAllianceId() > 0);
            this.guild.encode(packet, false);
         }
      }
   }

   public static class GuildMessage extends GuildPacket.GuildResult {
      public GuildMessage(GuildRequestResultType.Result result) {
         super(result);
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
      }
   }

   public static class GuildMessageWithString extends GuildPacket.GuildResult {
      String string;

      public GuildMessageWithString(GuildRequestResultType.Result result, String string) {
         super(result);
         this.string = string;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeMapleAsciiString(this.string);
      }
   }

   public static class GuildResult {
      GuildRequestResultType.Result result;

      public GuildResult(GuildRequestResultType.Result result) {
         this.result = result;
      }

      public void encode(PacketEncoder packet) {
         packet.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
         packet.writeInt(this.result.getFlag());
      }
   }

   public static class InsertJoinRequester extends GuildPacket.GuildResult {
      int guildID;
      int playerID;
      Guild.JoinRequester joinRequester;

      public InsertJoinRequester(int guildID, int playerID, Guild.JoinRequester joinRequester) {
         super(GuildRequestResultType.Result.InsertJoinRequester);
         this.guildID = guildID;
         this.playerID = playerID;
         this.joinRequester = joinRequester;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
         packet.writeInt(this.playerID);
         this.joinRequester.encode(packet);
      }
   }

   public static class InviteGuild extends GuildPacket.GuildResult {
      int guildID;
      String guildName;
      int playerID;
      String playerName;
      int level;
      int job;
      int unk1;
      int unk2;

      public InviteGuild(int guildID, String guildName, int playerID, String playerName, int level, int job, int unk1, int unk2) {
         super(GuildRequestResultType.Result.InviteGuild);
         this.guildID = guildID;
         this.guildName = guildName;
         this.playerID = playerID;
         this.playerName = playerName;
         this.level = level;
         this.job = job;
         this.unk1 = unk1;
         this.unk2 = unk2;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
         packet.writeMapleAsciiString(this.guildName);
         packet.writeInt(this.playerID);
         packet.writeMapleAsciiString(this.playerName);
         packet.writeInt(this.level);
         packet.writeInt(this.job);
         packet.writeInt(this.unk1);
         packet.writeInt(this.unk2);
      }
   }

   public static class JoinGuildInAlliance extends GuildPacket.GuildResult {
      Guild guild;

      public JoinGuildInAlliance(Guild guild) {
         super(GuildRequestResultType.Result.JoinGuildInAlliance);
         this.guild = guild;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guild.getAllianceId());
         packet.writeInt(this.guild.getId());
         this.guild.encode(packet, false);
      }
   }

   public static class JoinMember extends GuildPacket.GuildResult {
      GuildCharacter guildCharacter;

      public JoinMember(GuildCharacter guildCharacter) {
         super(GuildRequestResultType.Result.JoinMember);
         this.guildCharacter = guildCharacter;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildCharacter.getGuildId());
         packet.writeInt(this.guildCharacter.getId());
         packet.writeInt(this.guildCharacter.getId());
         packet.writeMapleAsciiString(this.guildCharacter.getName());
         packet.writeInt(this.guildCharacter.getJobId());
         packet.writeInt(this.guildCharacter.getLevel());
         packet.writeInt(this.guildCharacter.getGuildRank());
         packet.writeInt(this.guildCharacter.getAllianceRank());
         packet.writeLong(PacketHelper.getTime(this.guildCharacter.getLastLoggedInDate()));
         packet.write(this.guildCharacter.isOnline() ? 1 : 0);
         packet.writeLong(PacketHelper.getTime(this.guildCharacter.getTodayLoggedInDate()));
         packet.writeInt(this.guildCharacter.getGuildContribution());
         packet.writeInt(this.guildCharacter.getTodayContribution());
         packet.writeLong(PacketHelper.getTime(-2L));
         packet.writeInt(this.guildCharacter.getLastAttendanceDate());
         packet.writeLong(PacketHelper.getTime(this.guildCharacter.getCreateDate()));
      }
   }

   public static class JoinMember_InitGuild extends GuildPacket.GuildResult {
      Guild guild;
      int playerID;

      public JoinMember_InitGuild(Guild guild, int playerID) {
         super(GuildRequestResultType.Result.JoinMember_InitGuild);
         this.guild = guild;
         this.playerID = playerID;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(50000);
         packet.writeInt(this.playerID);
         packet.writeInt(this.guild.getId());
         packet.write(this.guild.getAllianceId() > 0);
         this.guild.encode(packet, false);
      }
   }

   public static class KickMember extends GuildPacket.GuildResult {
      int guildID;
      int playerID;
      String name;

      public KickMember(int guildID, int playerID, String name) {
         super(GuildRequestResultType.Result.KickMember);
         this.guildID = guildID;
         this.playerID = playerID;
         this.name = name;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
         packet.writeInt(this.playerID);
         packet.writeMapleAsciiString(this.name);
      }
   }

   public static class LoadGuildID extends GuildPacket.GuildResult {
      int guildID;

      public LoadGuildID(int guildID) {
         super(GuildRequestResultType.Result.LoadGuildID);
         this.guildID = guildID;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
      }
   }

   public static class LoadJoinRequestList extends GuildPacket.GuildResult {
      List<Guild.RecruitmentGuildData> recruitmentGuildData;

      public LoadJoinRequestList(List<Guild.RecruitmentGuildData> recruitmentGuildData) {
         super(GuildRequestResultType.Result.LoadJoinRequestList);
         this.recruitmentGuildData = recruitmentGuildData;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.recruitmentGuildData.size());
         this.recruitmentGuildData.forEach(g -> g.encode(packet));
      }
   }

   public static class MemberLogOnOff extends GuildPacket.GuildResult {
      int guildID;
      int playerID;
      boolean isOnline;
      long lastLoggedinTime;
      int job;
      int level;
      boolean show;

      public MemberLogOnOff(int guildID, int playerID, boolean isOnline, long lastLoggedinTime, int job, int level, boolean show) {
         super(GuildRequestResultType.Result.MemberLogOnOff);
         this.guildID = guildID;
         this.playerID = playerID;
         this.isOnline = isOnline;
         this.lastLoggedinTime = lastLoggedinTime;
         this.job = job;
         this.level = level;
         this.show = show;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
         packet.writeInt(this.playerID);
         packet.write(this.isOnline);
         if (!this.isOnline) {
            packet.writeLong(this.lastLoggedinTime);
         }

         packet.write(this.level != 0);
         if (this.level != 0) {
            packet.writeInt(this.job);
            packet.writeInt(this.level);
         }

         packet.write(this.show);
      }
   }

   public static class PurchaseGuildSkill extends GuildPacket.GuildResult {
      int guildID;
      int skillID;
      int unk1;
      short skillLevel;
      long expiration;
      String purchaser;
      String activater;

      public PurchaseGuildSkill(int guildID, int skillID, int unk1, short skillLevel, long expiration, String purchaser, String activater) {
         super(GuildRequestResultType.Result.PurchaseGuildSkill);
         this.guildID = guildID;
         this.skillID = skillID;
         this.unk1 = unk1;
         this.skillLevel = skillLevel;
         this.expiration = expiration;
         this.purchaser = purchaser;
         this.activater = activater;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
         packet.writeInt(this.skillID);
         packet.writeInt(this.unk1);
         packet.writeShort(this.skillLevel);
         packet.writeLong(this.expiration);
         packet.writeMapleAsciiString(this.purchaser);
         packet.writeMapleAsciiString(this.activater);
      }
   }

   public static class ResetEmblem extends GuildPacket.GuildResult {
      int guildID;

      public ResetEmblem(int guildID) {
         super(GuildRequestResultType.Result.ResetEmblem);
         this.guildID = guildID;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
         packet.writeInt(4567890);
      }
   }

   public static class SearchGuildResult {
      int playerID;
      byte type1;
      byte type2;
      String keyword;
      boolean likeSearch;
      byte unk1;
      byte unk2;
      byte unk3;
      boolean unk;
      List<Guild.RecruitmentGuildData> guildList;
      List<Guild> searchGuildList;

      public SearchGuildResult(
         int playerID,
         byte type1,
         byte type2,
         String keyword,
         boolean likeSearch,
         byte unk1,
         byte unk2,
         byte unk3,
         boolean unk,
         List<Guild.RecruitmentGuildData> guildList
      ) {
         this.playerID = playerID;
         this.type1 = type1;
         this.type2 = type2;
         this.keyword = keyword;
         this.likeSearch = likeSearch;
         this.unk1 = unk1;
         this.unk2 = unk2;
         this.unk3 = unk3;
         this.guildList = guildList;
         this.searchGuildList = null;
         this.unk = unk;
      }

      public void encode(PacketEncoder packet) {
         packet.writeShort(SendPacketOpcode.SEARCH_GUILD.getValue());
         packet.write(this.type1);
         packet.write(this.type2);
         packet.writeMapleAsciiString(this.keyword);
         packet.write(this.likeSearch);
         packet.write(0);
         packet.write(this.unk2);
         packet.write(this.unk);
         if (this.unk) {
            packet.write(0);
            packet.writeInt(0);
            packet.writeLong(0L);
            packet.write(1);
            packet.write(1);
            packet.writeInt(0);
         }

         packet.writeInt(0);
         packet.writeInt(this.guildList.size());

         for (Guild.RecruitmentGuildData recruitmentGuildData : this.guildList) {
            recruitmentGuildData.encode(packet);
         }
      }
   }

   public static class UpdateCustomGuildMark extends GuildPacket.GuildResult {
      Guild guild;

      public UpdateCustomGuildMark(Guild guild) {
         super(GuildRequestResultType.Result.UpdateCustomGuildMark);
         this.guild = guild;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guild.getId());
         packet.write(1);
         byte[] customEmblem = this.guild.getCustomEmblem();
         if (customEmblem != null && customEmblem.length > 0) {
            packet.writeInt(customEmblem.length);
            packet.encodeBuffer(customEmblem);
         } else {
            packet.writeInt(0);
         }

         packet.writeInt(1);
      }
   }

   public static class UpdateGuildPoint extends GuildPacket.GuildResult {
      Guild guild;

      public UpdateGuildPoint(Guild guild) {
         super(GuildRequestResultType.Result.UpdateGuildPoint);
         this.guild = guild;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guild.getId());
         packet.writeInt(this.guild.getHonorEXP());
         packet.writeInt(this.guild.getLevel());
         packet.writeInt(this.guild.getGP());
         packet.writeInt(0);
      }
   }

   public static class UpdateNotice extends GuildPacket.GuildResult {
      int guildID;
      int playerID;
      String notice;

      public UpdateNotice(int guildID, int playerID, String notice) {
         super(GuildRequestResultType.Result.UpdateNotice);
         this.guildID = guildID;
         this.playerID = playerID;
         this.notice = notice;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
         packet.writeInt(this.playerID);
         packet.writeMapleAsciiString(this.notice);
      }
   }

   public static class UpdateRecruitmentGuild extends GuildPacket.GuildResult {
      Guild.RecruitmentGuildData recruitmentGuildData;

      public UpdateRecruitmentGuild(Guild.RecruitmentGuildData recruitmentGuildData) {
         super(GuildRequestResultType.Result.UpdateRecruitmentGuild);
         this.recruitmentGuildData = recruitmentGuildData;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         this.recruitmentGuildData.encode(packet);
      }
   }

   public static class UseGuildSkill extends GuildPacket.GuildResult {
      int skillID;

      public UseGuildSkill(int skillID) {
         super(GuildRequestResultType.Result.UseGuildSkill);
         this.skillID = skillID;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.skillID);
      }
   }

   public static class VisitGuild extends GuildPacket.GuildResult {
      Guild guild;

      public VisitGuild(Guild guild) {
         super(GuildRequestResultType.Result.VisitGuild);
         this.guild = guild;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         this.guild.encode(packet, true);

         for (int i = 0; i < 5; i++) {
            packet.writeInt(0);
         }
      }
   }

   public static class WithdrawGuildInAlliance extends GuildPacket.GuildResult {
      int allianceID;
      int guildID;
      boolean kick;

      public WithdrawGuildInAlliance(int allianceID, int guildID, boolean kick) {
         super(GuildRequestResultType.Result.WithdrawGuildInAlliance);
         this.allianceID = allianceID;
         this.guildID = guildID;
         this.kick = kick;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.allianceID);
         packet.writeInt(this.guildID);
         packet.write(this.kick);
      }
   }

   public static class WithdrawGuildResult extends GuildPacket.GuildResult {
      int guildID;
      int playerID;
      String playerName;

      public WithdrawGuildResult(int guildID, int playerID, String playerName) {
         super(GuildRequestResultType.Result.WithdrawGuildResult);
         this.guildID = guildID;
         this.playerID = playerID;
         this.playerName = playerName;
      }

      @Override
      public void encode(PacketEncoder packet) {
         super.encode(packet);
         packet.writeInt(this.guildID);
         packet.writeInt(this.playerID);
         packet.writeMapleAsciiString(this.playerName);
      }
   }
}
