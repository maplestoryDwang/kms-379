package network.game.processors;

import network.center.Center;
import network.decode.PacketDecoder;
import network.models.CWvsContext;
import objects.context.guild.Guild;
import objects.context.guild.alliance.Alliance;
import objects.users.MapleCharacter;
import objects.users.MapleClient;

public class AllianceHandler {
   public static final void HandleAlliance(PacketDecoder slea, MapleClient c, boolean denied) {
      if (c.getPlayer().getGuildId() <= 0) {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      } else {
         Guild gs = Center.Guild.getGuild(c.getPlayer().getGuildId());
         if (gs == null) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         } else {
            byte op = slea.readByte();
            if (c.getPlayer().getGuildRank() == 1 || op == 1) {
               if (op == 22) {
                  denied = true;
               }

               int leaderid = 0;
               if (gs.getAllianceId() > 0) {
                  leaderid = Center.Alliance.getAllianceLeader(gs.getAllianceId());
               }

               if (op != 4 && !denied) {
                  if (gs.getAllianceId() <= 0 || leaderid <= 0) {
                     return;
                  }
               } else if (leaderid > 0 || gs.getAllianceId() > 0) {
                  return;
               }

               if (denied) {
                  DenyInvite(c, gs);
               } else {
                  switch (op) {
                     case 1:
                        for (byte[] pack : Center.Alliance.getAllianceInfo(gs.getAllianceId(), false)) {
                           if (pack != null) {
                              c.getSession().writeAndFlush(pack);
                           }
                        }
                        break;
                     case 2:
                     case 6:
                        int gid;
                        if (op == 6 && slea.available() >= 4L) {
                           gid = slea.readInt();
                           if (slea.available() >= 4L && gs.getAllianceId() != slea.readInt()) {
                              break;
                           }
                        } else {
                           gid = c.getPlayer().getGuildId();
                        }

                        try {
                           Alliance as = Center.Alliance.getAlliance(gs.getAllianceId());
                           int allianceCount = as.getGuildCount();
                           if (allianceCount > 2
                              ? gs != null
                                 && c.getPlayer().getGuildRank() == 1
                                 && (op == 2 && c.getPlayer().getAllianceRank() == 2 || op == 6 && c.getPlayer().getAllianceRank() == 1)
                                 && Center.Alliance.removeGuildFromAlliance(gs.getAllianceId(), gid, op == 6 && c.getPlayer().getAllianceRank() == 1)
                              : gs != null
                                 && c.getPlayer().getGuildRank() == 1
                                 && (op == 2 && c.getPlayer().getAllianceRank() == 2 || op == 6 && c.getPlayer().getAllianceRank() == 1)
                                 && Center.Alliance.disbandAlliance(gs.getAllianceId())) {
                           }
                        } catch (Exception var12) {
                           System.out.println("disbandAlliance 함수 실행중 오류발생" + var12.toString());
                           var12.printStackTrace();
                        }
                        break;
                     case 3:
                        int newGuild = Center.Guild.getGuildLeader(slea.readMapleAsciiString());
                        if (newGuild > 0 && c.getPlayer().getAllianceRank() == 1 && leaderid == c.getPlayer().getId()) {
                           MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterById(newGuild);
                           int inviteCheck = chr.getGuild().getAllianceId();
                           if (inviteCheck > 0) {
                              if (inviteCheck == gs.getAllianceId()) {
                                 c.getPlayer().dropMessage(1, "해당 길드는 같은 연합에 이미 소속되어 있습니다.");
                              } else {
                                 c.getPlayer().dropMessage(1, "해당 길드는 다른 연합에 소속되어 있습니다.");
                              }

                              return;
                           }

                           if (chr != null && chr.getGuildId() > 0 && Center.Alliance.canInvite(gs.getAllianceId())) {
                              chr.getClient()
                                 .getSession()
                                 .writeAndFlush(
                                    CWvsContext.AlliancePacket.sendAllianceInvite(Center.Alliance.getAlliance(gs.getAllianceId()).getName(), c.getPlayer())
                                 );
                              Center.Guild.setInvitedId(chr.getGuildId(), gs.getAllianceId());
                           } else {
                              c.getPlayer().dropMessage(1, "해당 길드의 길드장이 같은 채널에 있는 지 확인해주세요.");
                           }
                        } else {
                           c.getPlayer().dropMessage(1, "길드를 찾지 못하였습니다. 올바른 길드 이름을 입력해 주세요.");
                        }
                        break;
                     case 4:
                        int inviteid = Center.Guild.getInvitedId(c.getPlayer().getGuildId());
                        if (inviteid > 0) {
                           if (!Center.Alliance.addGuildToAlliance(inviteid, c.getPlayer().getGuildId())) {
                              c.getPlayer().dropMessage(5, "An error occured when adding guild.");
                           }

                           Center.Guild.setInvitedId(c.getPlayer().getGuildId(), 0);
                        }
                        break;
                     case 5:
                     default:
                        System.out.println("Unhandled GuildAlliance op: " + op + ", \n" + slea.toString());
                        break;
                     case 7:
                        if (c.getPlayer().getAllianceRank() == 1
                           && leaderid == c.getPlayer().getId()
                           && !Center.Alliance.changeAllianceLeader(gs.getAllianceId(), slea.readInt())) {
                           c.getPlayer().dropMessage(5, "An error occured when changing leader.");
                        }
                        break;
                     case 8:
                        String[] ranks = new String[5];

                        for (int i = 0; i < 5; i++) {
                           ranks[i] = slea.readMapleAsciiString();
                        }

                        Center.Alliance.updateAllianceRanks(gs.getAllianceId(), ranks);
                        break;
                     case 9:
                        if (c.getPlayer().getAllianceRank() <= 2 && !Center.Alliance.changeAllianceRank(gs.getAllianceId(), slea.readInt(), slea.readByte())) {
                           c.getPlayer().dropMessage(5, "An error occured when changing rank.");
                        }
                        break;
                     case 10:
                        if (c.getPlayer().getAllianceRank() <= 2) {
                           String notice = slea.readMapleAsciiString();
                           if (notice.length() <= 100) {
                              Center.Alliance.updateAllianceNotice(gs.getAllianceId(), notice);
                           }
                        }
                  }
               }
            }
         }
      }
   }

   public static final void DenyInvite(MapleClient c, Guild gs) {
      int inviteid = Center.Guild.getInvitedId(c.getPlayer().getGuildId());
      if (inviteid > 0) {
         int newAlliance = Center.Alliance.getAllianceLeader(inviteid);
         if (newAlliance > 0) {
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterById(newAlliance);
            if (chr != null) {
               chr.dropMessage(5, gs.getName() + " Guild has rejected the Guild Union invitation.");
            }

            Center.Guild.setInvitedId(c.getPlayer().getGuildId(), 0);
         }
      }
   }
}
