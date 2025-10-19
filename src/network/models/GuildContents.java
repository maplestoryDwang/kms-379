package network.models;

import constants.GameConstants;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import network.SendPacketOpcode;
import network.center.Center;
import network.encode.PacketEncoder;
import network.game.MapleGuildRanking;
import objects.context.guild.Guild;
import objects.context.guild.GuildContentsLog;
import objects.context.guild.GuildContentsType;
import objects.utils.HexTool;
import objects.utils.Pair;

public class GuildContents {
   public static byte[] showGuildRanks(MapleGuildRanking ranks, Guild guild) {
      PacketEncoder o = new PacketEncoder();
      o.writeShort(SendPacketOpcode.GUILD_LOG.getValue());
      o.write(1);
      Calendar cal = Calendar.getInstance();
      if (cal.getTime().getDay() == 0) {
         cal.set(5, cal.getTime().getDate() - 6);
      } else {
         cal.set(7, 2);
      }

      cal.set(10, 0);
      cal.set(12, 0);
      cal.set(13, 0);
      cal.set(14, 0);
      cal.set(11, 0);
      o.writeLong(PacketHelper.getTime(cal.getTime().getTime()));
      cal.set(5, cal.getTime().getDate() + 7);
      o.writeLong(PacketHelper.getTime(cal.getTime().getTime()));

      for (int i = 0; i < 4; i++) {
         List<MapleGuildRanking.GuildRankingInfo> rank = null;
         switch (i) {
            case 0:
               rank = ranks.getFlagRaceRank();
               break;
            case 1:
               rank = ranks.getHonorRank();
               break;
            case 2:
               rank = ranks.getCulvertRank();
               break;
            case 3:
               rank = ranks.getWeekMissionRank();
         }

         if (rank == null) {
            o.writeInt(0);
         } else {
            o.writeInt(rank.size());

            for (MapleGuildRanking.GuildRankingInfo info : rank) {
               o.writeInt(info.getGuildId());
               o.writeMapleAsciiString(info.getGuildName());
               o.writeInt(info.getScore());
               o.writeLong(PacketHelper.getTime(info.getLastUpdateTime()));
               o.writeInt(rank.indexOf(info) + 1);
               o.write(guild.getId() == info.getGuildId());
               if (guild.getId() == info.getGuildId()) {
                  o.writeInt(info.getUserInfo().size());

                  for (MapleGuildRanking.GuildRankingCharacterInfo user : info.getUserInfo()) {
                     o.writeInt(user.getId());
                     o.writeInt(user.getPoint());
                     o.writeLong(PacketHelper.getTime(user.getUpdateTime()));
                  }
               }
            }
         }
      }

      contentsSameStuff(o, guild);
      return o.getPacket();
   }

   public static final byte[] loadGuildLog(Guild guild) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.GUILD_LOG.getValue());
      mplew.write(5);
      Calendar cal = Calendar.getInstance();
      if (cal.getTime().getDay() == 0) {
         cal.set(5, cal.getTime().getDate() - 6);
      } else {
         cal.set(7, 2);
      }

      cal.set(10, 0);
      cal.set(12, 0);
      cal.set(13, 0);
      cal.set(14, 0);
      cal.set(11, 0);
      mplew.writeLong(PacketHelper.getTime(cal.getTime().getTime()));
      cal.set(5, cal.getTime().getDate() + 7);
      mplew.writeLong(PacketHelper.getTime(cal.getTime().getTime()));
      contentsSameStuff(mplew, guild);
      return mplew.getPacket();
   }

   public static final byte[] loadGuildDefaultSettings() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.GUILD_SETTINGS.getValue());
      mplew.writeInt(5);
      mplew.writeInt(0);

      for (int i = 0; i < 30; i++) {
         mplew.writeInt(GameConstants.getGuildExpNeededForLevel(i));
      }

      return mplew.getPacket();
   }

   public static void contentsSameStuff(PacketEncoder mplew, Guild guild) {
      mplew.writeInt(guild.getId());
      mplew.write(guild.getGuildContentsThisWeekTotalScoreByType(GuildContentsType.FLAG_RACE) > 0);
      if (guild.getGuildContentsThisWeekTotalScoreByType(GuildContentsType.FLAG_RACE) > 0) {
         mplew.writeInt(guild.getId());
         mplew.writeMapleAsciiString(guild.getName());
         mplew.writeInt(guild.getGuildContentsThisWeekTotalScoreByType(GuildContentsType.FLAG_RACE));
         mplew.writeLong(PacketHelper.getTime(guild.getGuildContentsThisWeekLastUpdateTimeByType(GuildContentsType.FLAG_RACE)));
         mplew.writeInt(1);
         mplew.write(1);
         mplew.writeInt(guild.getGuildContentsThisWeekLogsByType(GuildContentsType.FLAG_RACE).size());

         for (GuildContentsLog log : guild.getGuildContentsThisWeekLogsByType(GuildContentsType.FLAG_RACE)) {
            mplew.writeInt(log.getCharacterid());
            mplew.writeInt(log.getThisweekpoint());
            mplew.writeLong(PacketHelper.getTime(log.getThisweektime()));
         }

         mplew.writeInt(1);
      }

      mplew.write(false);
      mplew.write(guild.getGuildContentsThisWeekTotalScoreByType(GuildContentsType.CULVERT) > 0);
      if (guild.getGuildContentsThisWeekTotalScoreByType(GuildContentsType.CULVERT) > 0) {
         mplew.writeInt(guild.getId());
         mplew.writeMapleAsciiString(guild.getName());
         mplew.writeInt(guild.getGuildContentsThisWeekTotalScoreByType(GuildContentsType.CULVERT));
         mplew.writeLong(PacketHelper.getTime(guild.getGuildContentsThisWeekLastUpdateTimeByType(GuildContentsType.CULVERT)));
         mplew.writeInt(1);
         mplew.write(1);
         mplew.writeInt(guild.getGuildContentsThisWeekLogsByType(GuildContentsType.CULVERT).size());

         for (GuildContentsLog log : guild.getGuildContentsThisWeekLogsByType(GuildContentsType.CULVERT)) {
            mplew.writeInt(log.getCharacterid());
            mplew.writeInt(log.getThisweekpoint());
            mplew.writeLong(PacketHelper.getTime(log.getThisweektime()));
         }

         mplew.writeInt(1);
      }

      mplew.write(guild.getGuildContentsThisWeekTotalScoreByType(GuildContentsType.WEEK_MISSIONS) > 0);
      if (guild.getGuildContentsThisWeekTotalScoreByType(GuildContentsType.WEEK_MISSIONS) > 0) {
         mplew.writeInt(guild.getId());
         mplew.writeMapleAsciiString(guild.getName());
         mplew.writeInt(guild.getGuildContentsThisWeekTotalScoreByType(GuildContentsType.WEEK_MISSIONS));
         mplew.writeLong(PacketHelper.getTime(guild.getGuildContentsThisWeekLastUpdateTimeByType(GuildContentsType.WEEK_MISSIONS)));
         mplew.writeInt(1);
         mplew.write(1);
         mplew.writeInt(guild.getGuildContentsThisWeekLogsByType(GuildContentsType.WEEK_MISSIONS).size());

         for (GuildContentsLog log : guild.getGuildContentsThisWeekLogsByType(GuildContentsType.WEEK_MISSIONS)) {
            mplew.writeInt(log.getCharacterid());
            mplew.writeInt(log.getThisweekpoint());
            mplew.writeLong(PacketHelper.getTime(log.getThisweektime()));
         }

         mplew.writeInt(1);
      }

      mplew.write(1);
      mplew.writeInt(guild.getNoblessSkillPoint());
      mplew.writeInt(3);
      mplew.write(GuildContentsType.FLAG_RACE.getType());
      mplew.writeInt(guild.getGuildContentsLastWeekTotalScoreByType(GuildContentsType.FLAG_RACE));
      mplew.writeInt(1);
      mplew.write(1);
      mplew.write(0);
      mplew.write(Math.min(25, guild.getGuildContentsLastWeekTotalScoreByType(GuildContentsType.FLAG_RACE) / 1000));
      List<GuildContentsLog> logs = guild.getGuildContentsLastWeekLogsByType(GuildContentsType.FLAG_RACE);
      mplew.writeInt(logs.size());

      for (GuildContentsLog log : logs) {
         mplew.writeInt(log.getCharacterid());
         mplew.writeInt(log.getLastweekpoint());
         mplew.writeLong(PacketHelper.getTime(log.getLastweektime()));
      }

      mplew.write(GuildContentsType.CULVERT.getType());
      mplew.writeInt(guild.getGuildContentsLastWeekTotalScoreByType(GuildContentsType.CULVERT));
      mplew.writeInt(1);
      mplew.write(1);
      mplew.write(0);
      mplew.write(Math.min(25, guild.getGuildContentsLastWeekTotalScoreByType(GuildContentsType.CULVERT) / 10000));
      logs = guild.getGuildContentsLastWeekLogsByType(GuildContentsType.CULVERT);
      mplew.writeInt(logs.size());

      for (GuildContentsLog log : logs) {
         mplew.writeInt(log.getCharacterid());
         mplew.writeInt(log.getLastweekpoint());
         mplew.writeLong(PacketHelper.getTime(log.getLastweektime()));
      }

      mplew.write(GuildContentsType.WEEK_MISSIONS.getType());
      mplew.writeInt(guild.getGuildContentsLastWeekTotalScoreByType(GuildContentsType.WEEK_MISSIONS));
      mplew.writeInt(1);
      mplew.write(1);
      mplew.write(0);
      mplew.write(Math.min(10, guild.getGuildContentsLastWeekTotalScoreByType(GuildContentsType.WEEK_MISSIONS) / 20));
      logs = guild.getGuildContentsLastWeekLogsByType(GuildContentsType.WEEK_MISSIONS);
      mplew.writeInt(logs.size());

      for (GuildContentsLog log : logs) {
         mplew.writeInt(log.getCharacterid());
         mplew.writeInt(log.getLastweekpoint());
         mplew.writeLong(PacketHelper.getTime(log.getLastweektime()));
      }

      mplew.writeInt(5);
      mplew.writeInt(3);
      mplew.writeInt(GuildContentsType.FLAG_RACE.getType());
      mplew.writeInt(2);
      mplew.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 23 5F B4 EF D7 01"));
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(10);
      mplew.writeInt(10000);
      mplew.writeInt(10);
      mplew.writeInt(9000);
      mplew.writeInt(9);
      mplew.writeInt(8000);
      mplew.writeInt(8);
      mplew.writeInt(7000);
      mplew.writeInt(7);
      mplew.writeInt(6000);
      mplew.writeInt(6);
      mplew.writeInt(5000);
      mplew.writeInt(5);
      mplew.writeInt(4000);
      mplew.writeInt(4);
      mplew.writeInt(3000);
      mplew.writeInt(3);
      mplew.writeInt(2000);
      mplew.writeInt(2);
      mplew.writeInt(1000);
      mplew.writeInt(1);
      mplew.writeInt(10000);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeLong(PacketHelper.getTime(-2L));
      mplew.writeInt(10);
      mplew.writeInt(1);
      mplew.writeInt(20);
      mplew.writeInt(2);
      mplew.writeInt(17);
      mplew.writeInt(3);
      mplew.writeInt(15);
      mplew.writeInt(4);
      mplew.writeInt(13);
      mplew.writeInt(5);
      mplew.writeInt(13);
      mplew.writeInt(6);
      mplew.writeInt(13);
      mplew.writeInt(7);
      mplew.writeInt(13);
      mplew.writeInt(8);
      mplew.writeInt(13);
      mplew.writeInt(9);
      mplew.writeInt(13);
      mplew.writeInt(10);
      mplew.writeInt(13);
      mplew.writeInt(10);
      mplew.writeInt(10);
      mplew.writeInt(11);
      mplew.writeInt(20);
      mplew.writeInt(9);
      mplew.writeInt(30);
      mplew.writeInt(7);
      mplew.writeInt(40);
      mplew.writeInt(5);
      mplew.writeInt(50);
      mplew.writeInt(4);
      mplew.writeInt(60);
      mplew.writeInt(3);
      mplew.writeInt(70);
      mplew.writeInt(2);
      mplew.writeInt(80);
      mplew.writeInt(1);
      mplew.writeInt(90);
      mplew.writeInt(0);
      mplew.writeInt(100);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(1000);
      mplew.writeInt(5);
      mplew.writeInt(GuildContentsType.CULVERT.getType());
      mplew.writeInt(2);
      mplew.encodeBuffer(HexTool.getByteArrayFromHexString("00 00 23 5F B4 EF D7 01"));
      mplew.writeInt(10);
      mplew.writeInt(1);
      mplew.writeInt(30);
      mplew.writeInt(2);
      mplew.writeInt(28);
      mplew.writeInt(3);
      mplew.writeInt(26);
      mplew.writeInt(4);
      mplew.writeInt(24);
      mplew.writeInt(5);
      mplew.writeInt(24);
      mplew.writeInt(6);
      mplew.writeInt(22);
      mplew.writeInt(7);
      mplew.writeInt(22);
      mplew.writeInt(8);
      mplew.writeInt(22);
      mplew.writeInt(9);
      mplew.writeInt(22);
      mplew.writeInt(10);
      mplew.writeInt(22);
      mplew.writeInt(13);
      mplew.writeInt(5);
      mplew.writeInt(19);
      mplew.writeInt(10);
      mplew.writeInt(17);
      mplew.writeInt(15);
      mplew.writeInt(15);
      mplew.writeInt(20);
      mplew.writeInt(13);
      mplew.writeInt(25);
      mplew.writeInt(11);
      mplew.writeInt(30);
      mplew.writeInt(9);
      mplew.writeInt(40);
      mplew.writeInt(7);
      mplew.writeInt(50);
      mplew.writeInt(5);
      mplew.writeInt(60);
      mplew.writeInt(5);
      mplew.writeInt(70);
      mplew.writeInt(3);
      mplew.writeInt(80);
      mplew.writeInt(3);
      mplew.writeInt(90);
      mplew.writeInt(0);
      mplew.writeInt(100);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(500);
      mplew.writeInt(10);
      mplew.writeLong(PacketHelper.getTime(-2L));
      mplew.writeInt(10);
      mplew.writeInt(1);
      mplew.writeInt(20);
      mplew.writeInt(2);
      mplew.writeInt(18);
      mplew.writeInt(3);
      mplew.writeInt(16);
      mplew.writeInt(4);
      mplew.writeInt(15);
      mplew.writeInt(5);
      mplew.writeInt(15);
      mplew.writeInt(6);
      mplew.writeInt(15);
      mplew.writeInt(7);
      mplew.writeInt(15);
      mplew.writeInt(8);
      mplew.writeInt(15);
      mplew.writeInt(9);
      mplew.writeInt(15);
      mplew.writeInt(10);
      mplew.writeInt(15);
      mplew.writeInt(10);
      mplew.writeInt(10);
      mplew.writeInt(14);
      mplew.writeInt(20);
      mplew.writeInt(12);
      mplew.writeInt(30);
      mplew.writeInt(12);
      mplew.writeInt(40);
      mplew.writeInt(10);
      mplew.writeInt(50);
      mplew.writeInt(10);
      mplew.writeInt(60);
      mplew.writeInt(10);
      mplew.writeInt(70);
      mplew.writeInt(5);
      mplew.writeInt(80);
      mplew.writeInt(5);
      mplew.writeInt(90);
      mplew.writeInt(0);
      mplew.writeInt(100);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(500);
      mplew.writeInt(5);
      mplew.writeInt(GuildContentsType.WEEK_MISSIONS.getType());
      mplew.writeInt(1);
      mplew.writeLong(PacketHelper.getTime(-2L));
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(10);
      mplew.writeInt(200);
      mplew.writeInt(10);
      mplew.writeInt(180);
      mplew.writeInt(9);
      mplew.writeInt(160);
      mplew.writeInt(8);
      mplew.writeInt(140);
      mplew.writeInt(7);
      mplew.writeInt(120);
      mplew.writeInt(6);
      mplew.writeInt(100);
      mplew.writeInt(5);
      mplew.writeInt(80);
      mplew.writeInt(4);
      mplew.writeInt(60);
      mplew.writeInt(3);
      mplew.writeInt(40);
      mplew.writeInt(2);
      mplew.writeInt(20);
      mplew.writeInt(1);
      mplew.writeInt(200);
      mplew.writeInt(0);
      mplew.writeInt(0);
   }

   public static final byte[] openGuildRankNPC() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(-1);
      mplew.write(153);
      return mplew.getPacket();
   }

   public static final byte[] guildRankNPC() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.GUILD_LOG.getValue());
      mplew.write(1);
      Calendar cal = Calendar.getInstance();
      cal.set(7, 2);
      mplew.writeLong(PacketHelper.getTime(cal.getTime().getTime()));
      cal.add(5, 7);
      mplew.writeLong(PacketHelper.getTime(cal.getTime().getTime()));
      List<Pair<Integer, Guild>> flagRaceRanks = new ArrayList<>();

      for (Guild guild : Center.Guild.getGuilds()) {
         if (guild.getGuildContentsThisWeekTotalScoreByType(GuildContentsType.FLAG_RACE) > 0) {
            flagRaceRanks.add(new Pair<>(guild.getGuildContentsThisWeekTotalScoreByType(GuildContentsType.FLAG_RACE), guild));
         }
      }

      Collections.sort(flagRaceRanks, new Comparator<Pair<Integer, Guild>>() {
         public int compare(Pair<Integer, Guild> o1, Pair<Integer, Guild> o2) {
            return o1.left != o2.left ? o2.left - o1.left : o2.left.compareTo(o1.left);
         }
      });
      mplew.writeInt(flagRaceRanks.size());
      int index = 1;

      for (Pair<Integer, Guild> e : flagRaceRanks) {
         mplew.writeInt(e.right.getId());
         mplew.writeMapleAsciiString(e.right.getName());
         mplew.writeInt(e.left);
         mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
         mplew.writeInt(index);
         mplew.write(0);
         index++;
      }

      List<Pair<Integer, Guild>> honorRanks = new ArrayList<>();

      for (Guild guildx : Center.Guild.getGuilds()) {
         if (guildx.getHonorEXP() > 0) {
            honorRanks.add(new Pair<>(guildx.getHonorEXP(), guildx));
         }
      }

      Collections.sort(honorRanks, (o1, o2) -> !Objects.equals(o1.left, o2.left) ? o2.left - o1.left : o2.left.compareTo(o1.left));
      index = 1;
      mplew.writeInt(honorRanks.size());

      for (Pair<Integer, Guild> e : honorRanks) {
         mplew.writeInt(e.right.getId());
         mplew.writeMapleAsciiString(e.right.getName());
         mplew.writeInt(e.left);
         mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
         mplew.writeInt(index);
         mplew.write(0);
         index++;
      }

      List<Pair<Integer, Guild>> culvertRanks = new ArrayList<>();

      for (Guild guildxx : Center.Guild.getGuilds()) {
         if (guildxx.getGuildContentsThisWeekTotalScoreByType(GuildContentsType.CULVERT) > 0) {
            culvertRanks.add(new Pair<>(guildxx.getGuildContentsThisWeekTotalScoreByType(GuildContentsType.CULVERT), guildxx));
         }
      }

      Collections.sort(culvertRanks, new Comparator<Pair<Integer, Guild>>() {
         public int compare(Pair<Integer, Guild> o1, Pair<Integer, Guild> o2) {
            return o1.left != o2.left ? o2.left - o1.left : o2.left.compareTo(o1.left);
         }
      });
      index = 1;
      mplew.writeInt(culvertRanks.size());

      for (Pair<Integer, Guild> e : culvertRanks) {
         mplew.writeInt(e.right.getId());
         mplew.writeMapleAsciiString(e.right.getName());
         mplew.writeInt(e.left);
         mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
         mplew.writeInt(index);
         mplew.write(0);
         index++;
      }

      mplew.writeInt(0);
      mplew.encodeBuffer(
         HexTool.getByteArrayFromHexString(
            "8B 37 00 00 00 00 00 00 00 05 00 00 00 03 00 00 00 00 00 00 00 02 00 00 00 00 00 23 5F B4 EF D7 01 00 00 00 00 00 00 00 00 0A 00 00 00 10 27 00 00 0A 00 00 00 28 23 00 00 09 00 00 00 40 1F 00 00 08 00 00 00 58 1B 00 00 07 00 00 00 70 17 00 00 06 00 00 00 88 13 00 00 05 00 00 00 A0 0F 00 00 04 00 00 00 B8 0B 00 00 03 00 00 00 D0 07 00 00 02 00 00 00 E8 03 00 00 01 00 00 00 10 27 00 00 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 0A 00 00 00 01 00 00 00 14 00 00 00 02 00 00 00 11 00 00 00 03 00 00 00 0F 00 00 00 04 00 00 00 0D 00 00 00 05 00 00 00 0D 00 00 00 06 00 00 00 0D 00 00 00 07 00 00 00 0D 00 00 00 08 00 00 00 0D 00 00 00 09 00 00 00 0D 00 00 00 0A 00 00 00 0D 00 00 00 0A 00 00 00 0A 00 00 00 0B 00 00 00 14 00 00 00 09 00 00 00 1E 00 00 00 07 00 00 00 28 00 00 00 05 00 00 00 32 00 00 00 04 00 00 00 3C 00 00 00 03 00 00 00 46 00 00 00 02 00 00 00 50 00 00 00 01 00 00 00 5A 00 00 00 00 00 00 00 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 E8 03 00 00 05 00 00 00 02 00 00 00 02 00 00 00 00 00 23 5F B4 EF D7 01 0A 00 00 00 01 00 00 00 1E 00 00 00 02 00 00 00 1C 00 00 00 03 00 00 00 1A 00 00 00 04 00 00 00 18 00 00 00 05 00 00 00 18 00 00 00 06 00 00 00 16 00 00 00 07 00 00 00 16 00 00 00 08 00 00 00 16 00 00 00 09 00 00 00 16 00 00 00 0A 00 00 00 16 00 00 00 0D 00 00 00 05 00 00 00 13 00 00 00 0A 00 00 00 11 00 00 00 0F 00 00 00 0F 00 00 00 14 00 00 00 0D 00 00 00 19 00 00 00 0B 00 00 00 1E 00 00 00 09 00 00 00 28 00 00 00 07 00 00 00 32 00 00 00 05 00 00 00 3C 00 00 00 05 00 00 00 46 00 00 00 03 00 00 00 50 00 00 00 03 00 00 00 5A 00 00 00 00 00 00 00 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 F4 01 00 00 0A 00 00 00 00 40 E0 FD 3B 37 4F 01 0A 00 00 00 01 00 00 00 14 00 00 00 02 00 00 00 12 00 00 00 03 00 00 00 10 00 00 00 04 00 00 00 0F 00 00 00 05 00 00 00 0F 00 00 00 06 00 00 00 0F 00 00 00 07 00 00 00 0F 00 00 00 08 00 00 00 0F 00 00 00 09 00 00 00 0F 00 00 00 0A 00 00 00 0F 00 00 00 0A 00 00 00 0A 00 00 00 0E 00 00 00 14 00 00 00 0C 00 00 00 1E 00 00 00 0C 00 00 00 28 00 00 00 0A 00 00 00 32 00 00 00 0A 00 00 00 3C 00 00 00 0A 00 00 00 46 00 00 00 05 00 00 00 50 00 00 00 05 00 00 00 5A 00 00 00 00 00 00 00 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 F4 01 00 00 05 00 00 00 03 00 00 00 01 00 00 00 00 40 E0 FD 3B 37 4F 01 00 00 00 00 00 00 00 00 0A 00 00 00 C8 00 00 00 0A 00 00 00 B4 00 00 00 09 00 00 00 A0 00 00 00 08 00 00 00 8C 00 00 00 07 00 00 00 78 00 00 00 06 00 00 00 64 00 00 00 05 00 00 00 50 00 00 00 04 00 00 00 3C 00 00 00 03 00 00 00 28 00 00 00 02 00 00 00 14 00 00 00 01 00 00 00 C8 00 00 00 00 00 00 00 00 00 00 00"
         )
      );
      return mplew.getPacket();
   }
}
