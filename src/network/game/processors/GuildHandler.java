package network.game.processors;

import database.DBConfig;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import network.center.Center;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import network.models.GuildContents;
import objects.context.guild.Guild;
import objects.context.guild.GuildCharacter;
import objects.context.guild.GuildPacket;
import objects.context.guild.GuildRequestResultType;
import objects.context.guild.GuildResponse;
import objects.context.guild.alliance.Alliance;
import objects.fields.FieldLimitType;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.achievement.AchievementFactory;
import objects.users.skills.Skill;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatEffect;

public class GuildHandler {
   private static List<GuildHandler.Invited> invited = new LinkedList<>();
   private static long nextPruneTime = System.currentTimeMillis() + 300000L;

   public static final void DenyGuildRequest(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (player != null && player.getMap() != null) {
         int type = packet.readInt();
         GuildRequestResultType.Result request = GuildRequestResultType.Result.getByType(type);
         switch (request) {
            case DeclineInvite:
               String name = packet.readMapleAsciiString();
               MapleCharacter target = client.getChannelServer().getPlayerStorage().getCharacterByName(name);
               if (target != null) {
                  GuildPacket.sendGuildPacket(
                     target, new GuildPacket.GuildMessageWithString(GuildRequestResultType.Result.DeclineInvite, client.getPlayer().getName())
                  );
               }
               break;
            case DeclineInviteAlliance:
               int allianceId = packet.readInt();
               String guildName = packet.readMapleAsciiString();
               if (player.getGuildRank() != 1) {
                  return;
               }

               int gid = player.getGuildId();
               int leaderId = Center.Alliance.getAllianceLeader(allianceId);
               if (leaderId > 0) {
                  int ch = Center.Find.findChannel(leaderId);
                  if (ch > 0) {
                     MapleCharacter leader = null;

                     for (GameServer gs : GameServer.getAllInstances()) {
                        leader = gs.getPlayerStorage().getCharacterById(leaderId);
                        if (leader != null) {
                           break;
                        }
                     }

                     if (leader != null) {
                        leader.dropMessage(5, guildName + " 길드의 마스터가 연합 초대를 거부하였습니다.");
                     }
                  }
               }

               Center.Alliance.inviteList.remove(gid);
               Center.Alliance.inviteTime.remove(gid);
         }
      }
   }

   private static boolean isGuildNameAcceptable(String name) {
      return name.getBytes().length >= 3 && name.getBytes().length <= 12;
   }

   private static void respawnPlayer(MapleCharacter mc) {
      if (mc.getMap() != null) {
         mc.getMap().broadcastMessage(CField.loadGuildData(mc));
      }
   }

   public static void acceptJoinRequest(PacketDecoder slea, MapleCharacter chr) {
      byte count = slea.readByte();

      for (int next = 0; next < count; next++) {
         int playerId = slea.readInt();
         Guild guild = chr.getGuild();
         if (guild == null) {
            System.out.println("길드 가입 신청을 수락하는 도중에 길드가 null입니다. cid : " + chr.getId() + ", name : " + chr.getName());
            return;
         }

         MapleCharacter target = null;

         for (GameServer cs : GameServer.getAllInstances()) {
            target = cs.getPlayerStorage().getCharacterById(playerId);
            if (target != null) {
               break;
            }
         }

         if (target != null) {
            target.setGuildId(guild.getId());
         }

         GuildCharacter mgc = null;
         if (target != null) {
            mgc = target.getMGC();
         } else {
            DBConnection db = new DBConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            PreparedStatement ps2 = null;
            ResultSet rs2 = null;
            PreparedStatement ps3 = null;

            try (Connection con = DBConnection.getConnection()) {
               ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
               ps.setInt(1, playerId);
               rs = ps.executeQuery();
               if (rs.next()) {
                  mgc = new GuildCharacter(
                     playerId,
                     (short)rs.getInt("level"),
                     rs.getString("name"),
                     (byte)-1,
                     rs.getInt("job"),
                     (byte)5,
                     0,
                     (byte)0,
                     guild.getId(),
                     0L,
                     0,
                     0L,
                     0L,
                     false
                  );
               }

               ps2 = con.prepareStatement("SELECT `guild_id` FROM guild_request_member WHERE `name` = ?");
               ps2.setString(1, rs.getString("name"));
               rs2 = ps2.executeQuery();

               while (rs2.next()) {
                  int gid = rs2.getInt("guild_id");
                  if (gid != guild.getId()) {
                     Guild g = Center.Guild.getGuild(gid);
                     if (g != null) {
                        g.removeJoinRequester(playerId, false);
                     }
                  }
               }

               ps3 = con.prepareStatement("UPDATE characters SET guildid = ? WHERE id = ?");
               ps3.setInt(1, guild.getId());
               ps3.setInt(2, playerId);
               ps3.executeUpdate();
            } catch (SQLException var30) {
            } finally {
               try {
                  if (ps != null) {
                     ps.close();
                     PreparedStatement var34 = null;
                  }

                  if (rs != null) {
                     rs.close();
                  }

                  if (ps2 != null) {
                     ps2.close();
                     PreparedStatement var35 = null;
                  }

                  if (rs2 != null) {
                     rs2.close();
                     ResultSet var36 = null;
                  }

                  if (ps3 != null) {
                     ps3.close();
                  }
               } catch (SQLException var27) {
               }
            }
         }

         if (mgc != null) {
            int s = Center.Guild.addGuildMember(mgc);
            guild.removeJoinRequester(playerId, true);
            if (s == 0 && target != null) {
               target.dropMessage(1, "가입하려는 길드는 이미 정원이 꽉 찼습니다.");
               target.setGuildId(0);
               return;
            }

            if (target != null) {
               target.getClient().getSession().writeAndFlush(CWvsContext.GuildPacket.showGuildInfo(target));
               target.saveGuildStatus();
               respawnPlayer(target);
            }
         }
      }
   }

   public static void declineJoinRequest(PacketDecoder slea, MapleCharacter chr) {
      byte count = slea.readByte();

      for (int next = 0; next < count; next++) {
         int playerId = slea.readInt();
         Guild guild = chr.getGuild();
         if (guild == null) {
            System.out.println("길드 가입 신청을 거절하는 도중에 길드가 null입니다. cid : " + chr.getId() + ", name : " + chr.getName());
            return;
         }

         guild.removeJoinRequester(playerId, false);
      }
   }

   public static void joinRequest(PacketDecoder packet, MapleCharacter player) {
      GuildHandler.JoinRequestType type = GuildHandler.JoinRequestType.getByType(packet.readByte());
   }

   public static void removeJoinRequest(PacketDecoder slea, MapleCharacter chr) {
      String guildid = chr.getOneInfoQuest(26015, "guild");
      if (guildid == null || !guildid.isEmpty()) {
         Guild guild = Center.Guild.getGuild(Integer.parseInt(guildid));
         if (guild == null) {
            System.out.println("길드에 가입신청을 취소하는 도중 길드가 사라졌거나, 비정상적 접근입니다. cid : " + chr.getId() + ", name : " + chr.getName());
         } else {
            guild.removeJoinRequester(chr.getId(), false);
         }
      }
   }

   public static final void GuildRequest(PacketDecoder packet, MapleClient client) {
      if (System.currentTimeMillis() >= nextPruneTime) {
         Iterator<GuildHandler.Invited> itr = invited.iterator();

         while (itr.hasNext()) {
            GuildHandler.Invited inv = itr.next();
            if (System.currentTimeMillis() >= inv.expiration) {
               itr.remove();
            }
         }

         nextPruneTime += 300000L;
      }

      try {
         MapleCharacter player = client.getPlayer();
         if (player == null) {
            return;
         }

         int type = packet.readInt();
         GuildRequestResultType.Request request = GuildRequestResultType.Request.getByType(type);
         if (request == null) {
            System.out.println("[ERROR] 등록되지 않은 길드 핸들러입니다. (Type : " + type + ")");
            return;
         }

         switch (request) {
            case CreateGuild_CheckName:
               if (player.getGuildId() <= 0 && player.getMapId() == 200000301) {
                  if (player.getMeso() < 10000000L) {
                     player.dropMessage(1, "길드 제작에 필요한 메소 [1000만 메소] 가 충분하지 않습니다.");
                     return;
                  }

                  String guildNamex = packet.readMapleAsciiString();
                  if (!isGuildNameAcceptable(guildNamex)) {
                     player.dropMessage(1, "해당 길드 이름은 만들 수 없습니다.");
                     return;
                  }

                  if (!Guild.checkAvailableName(guildNamex)) {
                     PacketEncoder p = new PacketEncoder();
                     GuildPacket.CreateGuild_NotAvailableName n = new GuildPacket.CreateGuild_NotAvailableName();
                     n.encode(p);
                     player.send(p.getPacket());
                     return;
                  }

                  PacketEncoder p = new PacketEncoder();
                  GuildPacket.CreateGuild_AvailableName gp = new GuildPacket.CreateGuild_AvailableName(guildNamex);
                  gp.encode(p);
                  player.send(p.getPacket());
                  player.setCreateGuildName(guildNamex);
                  break;
               }

               player.dropMessage(1, "이미 길드에 가입되어 있어 길드를 만들 수 없습니다.");
               return;
            case CreateGuild_DoCreate: {
               String guildName = player.getCreateGuildName();
               if (!isGuildNameAcceptable(guildName)) {
                  player.dropMessage(1, "해당 길드 이름은 만들 수 없습니다.");
                  return;
               }

               if (!Guild.checkAvailableName(guildName)) {
                  PacketEncoder p = new PacketEncoder();
                  GuildPacket.CreateGuild_NotAvailableName n = new GuildPacket.CreateGuild_NotAvailableName();
                  n.encode(p);
                  player.send(p.getPacket());
                  return;
               }

               int guildID = Center.Guild.createGuild(player.getId(), guildName);
               if (guildID == 0) {
                  PacketEncoder p = new PacketEncoder();
                  GuildPacket.CreateGuild_NotAvailableName n = new GuildPacket.CreateGuild_NotAvailableName();
                  n.encode(p);
                  player.send(p.getPacket());
                  return;
               }

               player.gainMeso(-10000000L, true, true, true);
               player.setGuildIdFromCreate(guildID);
               player.setGuildRank((byte)1);
               player.saveGuildStatus();
               Guild guildxxx = Center.Guild.getGuild(guildID);
               if (guildxxx == null) {
                  return;
               }

               guildxxx.setOnline(player.getId(), guildxxx.getName(), true, player.getClient().getChannel());
               player.send(GuildContents.loadGuildLog(guildxxx));
               PacketEncoder p = new PacketEncoder();
               GuildPacket.CreateGuild_InitGuild init = new GuildPacket.CreateGuild_InitGuild(guildxxx, player.getId());
               init.encode(p);
               player.send(p.getPacket());
               if (DBConfig.isGanglim) {
                  player.addGuildContribution(1215000);
               } else {
                  player.addGuildContribution(500);
               }

               respawnPlayer(player);
               }
               break;
            case SearchGuild: {
               byte searchType = packet.readByte();
               byte searchType2 = packet.readByte();
               if (searchType2 == 1) {
                  int leaderid = 0;
                  int guildIDx = player.getGuildId();
                  Guild guildxxxx = Center.Guild.getGuild(guildIDx);
                  if (guildxxxx == null) {
                     player.send(CWvsContext.enableActions(player));
                     return;
                  }

                  if (guildxxxx.getAllianceId() > 0) {
                     leaderid = Center.Alliance.getAllianceLeader(guildxxxx.getAllianceId());
                  }

                  String searchText = packet.readMapleAsciiString();
                  List<Guild> guilds = Center.Guild.getGuildForSearch(client.getWorld(), searchType, searchText, true);
                  if (guilds.size() > 0) {
                     List<Guild.RecruitmentGuildData> recruitment = new ArrayList<>();
                     guilds.forEach(gx -> {
                        if (gx.getLeaderId() != player.getId()) {
                           recruitment.add(new Guild.RecruitmentGuildData(gx, player.getId(), false));
                        }
                     });
                     if (guilds.isEmpty()) {
                        player.dropMessage(1, "길드를 찾지 못하였습니다. 올바른 길드 이름을 입력해 주세요.");
                        return;
                     }

                     byte unk1x = packet.readByte();
                     byte unk2x = packet.readByte();
                     byte unk3 = packet.readByte();
                     GuildPacket.SearchGuildResult result = new GuildPacket.SearchGuildResult(
                        player.getId(), searchType, searchType2, searchText, true, unk1x, unk2x, unk3, false, recruitment
                     );
                     PacketEncoder p = new PacketEncoder();
                     result.encode(p);
                     player.send(p.getPacket());
                     return;
                  }

                  player.dropMessage(1, "길드를 찾지 못하였습니다. 올바른 길드 이름을 입력해 주세요.");
                  return;
               }

               if (searchType2 == 2) {
                  player.dropMessage(5, "길드 블랙리스트 기능은 현재 준비중입니다.");
                  player.send(CWvsContext.enableActions(player));
                  return;
               }

               String searchText = packet.readMapleAsciiString();
               boolean likeSearch = packet.readByte() == 0;
               byte unk1x = packet.readByte();
               byte unk2x = packet.readByte();
               byte unk3 = packet.readByte();
               List<Guild> var152;
               if (searchType == 4 && searchType2 == 0) {
                  var152 = Center.Guild.getAllRecruitmentGuilds();
               } else {
                  var152 = Center.Guild.getGuildForSearch(client.getWorld(), searchType, searchText, likeSearch);
               }

               if (var152.size() > 0) {
                  List<Guild.RecruitmentGuildData> recruitment = new ArrayList<>();
                  var152.forEach(gx -> recruitment.add(new Guild.RecruitmentGuildData(gx, player.getId(), false)));
                  GuildPacket.SearchGuildResult result = new GuildPacket.SearchGuildResult(
                     player.getId(), searchType, searchType2, searchText, likeSearch, unk1x, unk2x, unk3, true, recruitment
                  );
                  PacketEncoder p = new PacketEncoder();
                  result.encode(p);
                  player.send(p.getPacket());
               }
               }
               break;
            case InviteAlliance:
               if (player.getGuild() != null && player.getGuild().getAllianceId() != 0 && player.getGuildRank() == 1 && player.getAllianceRank() == 1) {
                  int allianceId = player.getGuild().getAllianceId();
                  if (!Center.Alliance.canInvite(allianceId)) {
                     player.dropMessage(1, "연합에 더이상 길드를 초대할 수 없습니다. 연합은 최대 5길드 까지 가능합니다. 연합 자리가 부족한 경우 길드 연합 담당자 NPC를 통해 늘릴 수 있습니다.");
                     return;
                  }

                  int targetGuildId = packet.readInt();
                  if (Center.Alliance.inviteList.containsKey(targetGuildId)) {
                     long time = Center.Alliance.inviteTime.get(targetGuildId);
                     if (System.currentTimeMillis() - time <= 60000L && !player.getClient().isGm()) {
                        player.dropMessage(1, "해당 길드는 다른 연합의 초대를 처리중입니다.");
                        return;
                     }

                     Center.Alliance.inviteList.remove(targetGuildId);
                     Center.Alliance.inviteTime.remove(targetGuildId);
                  }

                  int leaderId = Center.Guild.getGuildLeader(targetGuildId);
                  if (leaderId > 0) {
                     int ch = Center.Find.findChannel(leaderId);
                     if (ch <= 0) {
                        player.dropMessage(5, "해당 길드의 마스터가 오프라인 상태입니다.");
                        return;
                     }

                     MapleCharacter leader = null;

                     for (GameServer gs : GameServer.getAllInstances()) {
                        leader = gs.getPlayerStorage().getCharacterById(leaderId);
                        if (leader != null) {
                           break;
                        }
                     }

                     if (leader == null) {
                        player.dropMessage(5, "해당 길드의 마스터가 오프라인 상태입니다.");
                        return;
                     }

                     Center.Alliance.inviteList.put(leader.getGuildId(), allianceId);
                     Center.Alliance.inviteTime.put(leader.getGuildId(), System.currentTimeMillis());
                     leader.send(GuildPacket.inviteAlliance(allianceId, player.getName()));
                  }
                  break;
               }

               return;
            case AttendAlliance:
               if (player.getGuild() != null && player.getGuildRank() == 1) {
                  int allianceId = packet.readInt();
                  int guildId = packet.readInt();
                  if (Center.Alliance.inviteList.get(guildId) == null) {
                     return;
                  }

                  Center.Alliance.getAlliance(allianceId).addGuild(guildId);
                  Center.Alliance.inviteList.remove(guildId);
                  Center.Alliance.inviteTime.remove(guildId);
                  break;
               }

               return;
            case VisitGuild:
               int targetGuildIDxx = packet.readInt();
               Guild targetGuild = Center.Guild.getGuild(targetGuildIDxx);
               if (targetGuild == null) {
                  System.out
                     .println(String.format("guild가 null입니다. (name : %s, cid : %d, targetGuildID : %d)", player.getName(), player.getId(), targetGuildIDxx));
                  return;
               }

               PacketEncoder p = new PacketEncoder();
               GuildPacket.VisitGuild visitGuild = new GuildPacket.VisitGuild(targetGuild);
               visitGuild.encode(p);
               player.send(p.getPacket());
               break;
            case WithdrawGuild:
               int playerIDx = packet.readInt();
               String name = packet.readMapleAsciiString();
               if (playerIDx == player.getId() && name.equals(player.getName()) && player.getGuildId() > 0) {
                  Center.Guild.leaveGuild(player.getMGC());
                  break;
               }

               return;
            case JoinGuild:
               int targetGuildIDx = packet.readInt();
               String text = packet.readMapleAsciiString();
               long flag = packet.readLong();
               byte unk1 = packet.readByte();
               byte unk2 = packet.readByte();
               String removeTime = player.getOneInfoQuest(26015, "remove_time");
               if (removeTime != null && !removeTime.isEmpty()) {
                  if (Long.valueOf(removeTime) + 60000L > System.currentTimeMillis()) {
                     GuildPacket.sendGuildPacket(player, new GuildPacket.GuildMessage(GuildRequestResultType.Result.NotYetPossibleJoinGuild));
                     return;
                  }

                  player.updateOneInfo(26015, "remove_time", "");
               }

               Guild guildxx = Center.Guild.getGuild(targetGuildIDx);
               if (guildxx == null) {
                  System.out.println("길드에 가입을 요청하던 도중 길드가 없어졌거나, 비정상적 접근입니다. cid : " + player.getId() + ", name : " + player.getName());
                  return;
               }

               int applyGuildID = player.getRequestGuildByPlayerId();
               if (applyGuildID != -1) {
                  Guild guild_ = Center.Guild.getGuild(applyGuildID);
                  if (guild_ == null) {
                     player.deleteRequestGuildByPlayerId();
                  }
               }

               guildxx.insertJoinRequester(player, text);
               GuildPacket.UpdateRecruitmentGuild update = new GuildPacket.UpdateRecruitmentGuild(
                  new Guild.RecruitmentGuildData(guildxx, player.getId(), false)
               );
               GuildPacket.sendGuildPacket(player, update);
               break;
            case LoadJoinRequest:
               GuildPacket.LoadJoinRequestList list = new GuildPacket.LoadJoinRequestList(Center.Guild.getAllRecruitmentGuildByPlayerID(player.getId()));
               GuildPacket.sendGuildPacket(player, list);
               break;
            case AcceptJoinRequest:
               byte count = packet.readByte();

               for (int next = 0; next < count; next++) {
                  int playerID = packet.readInt();
                  Guild guildxxxxxx = player.getGuild();
                  if (guildxxxxxx == null) {
                     System.out.println("길드 가입 신청을 수락하는 도중에 길드가 null입니다. cid : " + player.getId() + ", name : " + player.getName());
                     return;
                  }

                  MapleCharacter target = null;

                  for (GameServer cs : GameServer.getAllInstances()) {
                     target = cs.getPlayerStorage().getCharacterById(playerID);
                     if (target != null) {
                        break;
                     }
                  }

                  if (target != null) {
                     target.setGuildId(guildxxxxxx.getId());
                  }

                  GuildCharacter mgc = null;
                  if (target != null) {
                     mgc = target.getMGC();
                  } else {
                     DBConnection db = new DBConnection();
                     PreparedStatement ps = null;
                     ResultSet rs = null;
                     PreparedStatement ps2 = null;
                     ResultSet rs2 = null;
                     PreparedStatement ps3 = null;

                     try (Connection con = DBConnection.getConnection()) {
                        ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
                        ps.setInt(1, playerID);
                        rs = ps.executeQuery();
                        if (rs.next()) {
                           mgc = new GuildCharacter(
                              playerID,
                              (short)rs.getInt("level"),
                              rs.getString("name"),
                              (byte)-1,
                              rs.getInt("job"),
                              (byte)5,
                              0,
                              (byte)0,
                              guildxxxxxx.getId(),
                              0L,
                              0,
                              0L,
                              0L,
                              false
                           );
                        }

                        ps2 = con.prepareStatement("SELECT `guild_id` FROM guild_request_member WHERE `name` = ?");
                        ps2.setString(1, rs.getString("name"));
                        rs2 = ps2.executeQuery();

                        while (rs2.next()) {
                           int gid = rs2.getInt("guild_id");
                           if (gid != guildxxxxxx.getId()) {
                              Guild g = Center.Guild.getGuild(gid);
                              if (g != null) {
                                 g.removeJoinRequester(playerID, false);
                              }
                           }
                        }

                        ps3 = con.prepareStatement("UPDATE characters SET guildid = ? WHERE id = ?");
                        ps3.setInt(1, guildxxxxxx.getId());
                        ps3.setInt(2, playerID);
                        ps3.executeUpdate();
                     } catch (SQLException var37) {
                     } finally {
                        try {
                           if (ps != null) {
                              ps.close();
                              PreparedStatement var154 = null;
                           }

                           if (rs != null) {
                              rs.close();
                           }

                           if (ps2 != null) {
                              ps2.close();
                              PreparedStatement var163 = null;
                           }

                           if (rs2 != null) {
                              rs2.close();
                              ResultSet var166 = null;
                           }

                           if (ps3 != null) {
                              ps3.close();
                           }
                        } catch (SQLException var33) {
                        }
                     }
                  }

                  if (mgc != null) {
                     int s = Center.Guild.addGuildMember(mgc);
                     Center.Guild.removeAllJoinRequester(playerID);
                     if (s == 0 && target != null) {
                        target.dropMessage(1, "가입하려는 길드는 이미 정원이 꽉 찼습니다.");
                        target.setGuildId(0);
                        return;
                     }

                     if (target != null) {
                        PacketEncoder p2 = new PacketEncoder();
                        GuildPacket.JoinMember_InitGuild init = new GuildPacket.JoinMember_InitGuild(guildxxxxxx, target.getId());
                        init.encode(p2);
                        target.send(p2.getPacket());
                        target.saveGuildStatus();
                        respawnPlayer(target);
                     }
                  }
               }
               break;
            case DeclineJoinRequest:
               count = packet.readByte();

               for (int next = 0; next < count; next++) {
                  int playerID = packet.readInt();
                  Guild guildxxxxxx = player.getGuild();
                  if (guildxxxxxx == null) {
                     System.out.println("길드 가입 신청을 거절하는 도중에 길드가 null입니다. cid : " + player.getId() + ", name : " + player.getName());
                     return;
                  }

                  guildxxxxxx.removeJoinRequester(playerID, false);
               }
               break;
            case CancelJoinRequest:
               int targetGuildID = packet.readInt();
               Guild guildx = Center.Guild.getGuild(targetGuildID);
               if (guildx != null) {
                  guildx.removeJoinRequester(player.getId(), false);
               }
               break;
            case InviteGuild:
               String targetName = packet.readMapleAsciiString();
               if (player.getGuildId() > 0 && player.getGuildRank() <= 2) {
                  GuildResponse result = Guild.sendInvite(client, targetName);
                  if (result != null) {
                     player.send(result.getPacket());
                  } else {
                     GuildPacket.sendGuildPacket(player, new GuildPacket.GuildMessageWithString(GuildRequestResultType.Result.InviteMessage, targetName));
                     GuildHandler.Invited inv = new GuildHandler.Invited(targetName, player.getGuildId());
                     if (!invited.contains(inv)) {
                        invited.add(inv);
                     }
                  }
                  break;
               }

               return;
            case ChangeRankRole:
               int index = packet.readByte();
               String newName = packet.readMapleAsciiString();
               int newRole = packet.readInt();
               if (index == 1) {
                  newRole = -1;
               }

               Center.Guild.changeRankTitleRole(
                  request != GuildRequestResultType.Request.ChangeRankRole, player.getGuildId(), player.getId(), index, newName, newRole
               );
               break;
            case EditJoinSetting:
               boolean allowJoinRequest = packet.readByte() == 1;
               int connectTimeFlag = packet.readInt();
               int activityFlag = packet.readInt();
               int ageGroupFlag = packet.readInt();
               Center.Guild.editJoinSetting(player.getGuildId(), player.getId(), allowJoinRequest, connectTimeFlag, activityFlag, ageGroupFlag);
               break;
            case ChangeLeader:
               int targetPlayerIDx = packet.readInt();
               if (player.getGuildId() <= 0) {
                  return;
               }

               Center.Guild.setGuildLeader(player.getGuildId(), targetPlayerIDx);
               break;
            case ChangeRank:
               playerIDx = packet.readInt();
               byte newRank = packet.readByte();
               if (newRank > 1 && newRank <= 10 && player.getGuildId() > 0) {
                  Center.Guild.changeRank(player.getGuildId(), playerIDx, newRank);
                  break;
               }

               return;
            case KickMember:
               int targetPlayerID = packet.readInt();
               if (player.getGuildId() <= 0) {
                  return;
               }

               if (player.getGuildRank() > 2) {
                  return;
               }

               GuildCharacter targetMGC = Center.Guild.getGuild(player.getGuildId()).getMGC(targetPlayerID);
               Center.Guild.expelMember(player.getMGC(), targetMGC.getName(), targetPlayerID);
               respawnPlayer(player);
               break;
            case ChangeMark:
               if (player.getGuildId() > 0 && player.getGuildRank() == 1 && player.getMapId() == 200000301) {
                  boolean isCustomEmblem = packet.readByte() == 1;
                  if (!isCustomEmblem) {
                     short bg = packet.readShort();
                     byte bgcolor = packet.readByte();
                     short logo = packet.readShort();
                     byte logocolor = packet.readByte();
                     if (!Center.Guild.setGuildEmblem(player.getGuildId(), bg, bgcolor, logo, logocolor, Guild.BCOp.EMBELMCHANGE, null)) {
                        player.dropMessage(1, "길드마크를 만들기 위한 GP가 부족합니다.");
                        return;
                     }
                  } else {
                     packet.readByte();
                     int size = packet.readInt();
                     byte[] imageData = new byte[size];

                     for (int i = 0; i < size; i++) {
                        imageData[i] = packet.readByte();
                     }

                     if (!Center.Guild.setGuildEmblem(player.getGuildId(), (short)0, (byte)0, (short)0, (byte)0, Guild.BCOp.CUSTOMEMBLEMCHANGE, imageData)) {
                        player.dropMessage(1, "길드마크를 만들기 위한 GP가 부족합니다.");
                        return;
                     }
                  }

                  respawnPlayer(player);
                  break;
               }

               player.dropMessage(1, "길드 마크 제작은 영웅의 전당에서만 할 수 있습니다.");
               return;
            case ChangeNotice:
               String notice = packet.readMapleAsciiString();
               Center.Guild.setGuildNotice(player.getGuildId(), notice, player.getId());
               break;
            case AttendGuild:
               int date = Integer.parseInt(player.getToday());
               SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
               int lastAttend = player.getLastAttendacneDate();
               long check = 0L;
               if (lastAttend > 0) {
                  check = Calendar.getInstance().getTime().getTime() - sdf.parse(String.valueOf(lastAttend)).getTime();
               }

               if (player.getLastAttendacneDate() == 0 || check < 86400000L) {
                  AchievementFactory.checkGuildAttendCheck(player);
               }

               player.setLastAttendacneDate(date);
               player.addGuildContribution(30);
               Center.Guild.attendanceCheck(player.getGuildId(), player.getId(), date);
               break;
            case GuildCustomMarkRequest:
               Guild guild = Center.Guild.getGuild(packet.readInt());
               if (guild != null && guild.getCustomEmblem() != null) {
                  GuildPacket.UpdateCustomGuildMark s = new GuildPacket.UpdateCustomGuildMark(guild);
                  PacketEncoder pack = new PacketEncoder();
                  s.encode(pack);
                  player.send(pack.getPacket());
               }
               break;
            case SkillLevelUp:
               Skill skill = SkillFactory.getSkill(packet.readInt());
               if (player.getGuildId() > 0 && skill != null && skill.getId() >= 91000000) {
                  int levelUp = 1;
                  if (packet.available() > 0L) {
                     levelUp = packet.readByte();
                  }

                  int effx = Center.Guild.getSkillLevel(player.getGuildId(), skill.getId()) + levelUp;
                  if (effx - 1 > skill.getMaxLevel()) {
                     return;
                  }

                  SecondaryStatEffect effectx = skill.getEffect(effx);
                  if (effectx == null) {
                     return;
                  }

                  if (effectx.getReqGuildLevel() > player.getGuild().getLevel()) {
                     return;
                  }

                  Center.Guild.purchaseSkill(player.getGuildId(), effectx.getSourceId(), levelUp, player.getName(), player.getId());
                  break;
               }

               return;
            case UseGuildSkill:
               int guildSkillID = packet.readInt();
               Skill skilli = SkillFactory.getSkill(guildSkillID);
               if (player.getGuildId() > 0 && skilli != null && skilli.getId() >= 91000000) {
                  int eff = Center.Guild.getSkillLevel(player.getGuildId(), skilli.getId());
                  if (eff > skilli.getMaxLevel()) {
                     return;
                  }

                  SecondaryStatEffect effect = skilli.getEffect(eff);
                  if (effect == null) {
                     System.out.println("guildSkillID : " + guildSkillID + " / Level : " + eff + " effect가 null");
                     return;
                  }

                  if (player.getGuild() == null) {
                     return;
                  }

                  if (effect.getReqGuildLevel() > player.getGuild().getLevel()) {
                     return;
                  }

                  GuildSkillRequest(packet, effect, client);
                  player.send(CField.skillCooldown(skilli.getId(), effect.getCooldown(player)));
                  player.addCooldown(skilli.getId(), System.currentTimeMillis(), effect.getCooldown(player));
                  GuildPacket.UseGuildSkill sx = new GuildPacket.UseGuildSkill(skilli.getId());
                  GuildPacket.sendGuildPacket(player, sx);
                  break;
               }

               return;
            case ChangeAllianceReader:
               if (player.getAllianceRank() != 1) {
                  return;
               }

               int guildId = packet.readInt();
               int chrId = packet.readInt();
               Center.Alliance.changeAllianceLeader(player.getGuild().getAllianceId(), chrId);
               break;
            case ChangeAllianceRank:
               if (player.getAllianceRank() <= 2 && !Center.Alliance.changeAllianceRank(player.getGuild().getAllianceId(), packet.readInt(), packet.readByte())
                  )
                {
                  player.dropMessage(5, "연합 직위를 변경하는 도중 알 수 없는 오류가 발생했습니다.");
               }
               break;
            case ChangeAllianceRankRole:
               String[] ranks = new String[5];

               for (int i = 0; i < 5; i++) {
                  ranks[i] = packet.readMapleAsciiString();
               }

               Center.Alliance.updateAllianceRanks(player.getGuild().getAllianceId(), ranks);
               break;
            case WithdrawGuildInAlliance:
            case KickGuildInAlliance:
               Guild guildxxxxx = null;
               if (request == GuildRequestResultType.Request.WithdrawGuildInAlliance) {
                  guildxxxxx = player.getGuild();
               } else {
                  int tartgetGuildID = packet.readInt();
                  guildxxxxx = Center.Guild.getGuild(tartgetGuildID);
               }

               if (guildxxxxx == null) {
                  return;
               }

               Alliance alliance = null;
               if (request == GuildRequestResultType.Request.WithdrawGuildInAlliance) {
                  alliance = Center.Alliance.getAlliance(guildxxxxx.getAllianceId());
               } else {
                  int allianceID = packet.readInt();
                  if (guildxxxxx.getAllianceId() != allianceID) {
                     return;
                  }
               }

               if (alliance == null) {
                  return;
               }

               try {
                  int guildCount = alliance.getGuildCount();
                  if (guildCount > 2) {
                     if (player.getGuildRank() == 1
                        && (
                           request == GuildRequestResultType.Request.WithdrawGuildInAlliance && player.getAllianceRank() == 2
                              || request == GuildRequestResultType.Request.KickGuildInAlliance && player.getAllianceRank() == 1
                        )
                        && !Center.Alliance.removeGuildFromAlliance(
                           alliance.getId(), guildxxxxx.getId(), request == GuildRequestResultType.Request.KickGuildInAlliance && player.getAllianceRank() == 1
                        )) {
                        player.dropMessage(5, "알 수 없는 오류가 발생하였습니다.");
                        return;
                     }
                  } else if (guildxxxxx != null
                     && player.getGuildRank() == 1
                     && (
                        request == GuildRequestResultType.Request.WithdrawGuildInAlliance && player.getAllianceRank() == 2
                           || request == GuildRequestResultType.Request.KickGuildInAlliance && player.getAllianceRank() == 1
                     )
                     && !Center.Alliance.disbandAlliance(alliance.getId())) {
                     player.dropMessage(5, "알 수 없는 오류가 발생하였습니다.");
                     return;
                  }
               } catch (Exception var35) {
                  System.out.println("연합 탈퇴 / 추방 처리 중 오류발생" + var35.toString());
                  var35.printStackTrace();
               }
         }
      } catch (Exception var39) {
         System.out.println("길드 핸들러 오류 발생");
         var39.printStackTrace();
      }
   }

   public static final void GuildSkillRequest(PacketDecoder packet, SecondaryStatEffect level, MapleClient client) {
      switch (level.getSourceId()) {
         case 91001016:
         case 91001017:
            int playerID = packet.readInt();
            if (client.getPlayer().getCooldownLimit(level.getSourceId()) > 0L) {
               return;
            }

            MapleCharacter warpCharacter = client.getChannelServer().getPlayerStorage().getCharacterById(playerID);
            if (warpCharacter == null) {
               client.getPlayer().dropMessage(6, "현재 채널에서 해당 유저를 찾을 수 없습니다.");
               return;
            }

            if (level.getSourceId() == 91001016) {
               if (FieldLimitType.RegularExpLoss.check(warpCharacter.getMap().getFieldLimit())
                  || FieldLimitType.SpecificPortalScrollLimit.check(warpCharacter.getMap().getFieldLimit())) {
                  client.getPlayer().dropMessage(1, "상대방이 해당 스킬을 사용할 수 없는 위치입니다.");
                  client.getPlayer().send(CWvsContext.enableActions(client.getPlayer()));
                  return;
               }

               if (warpCharacter.getMap().getLevelLimit() > client.getPlayer().getLevel()) {
                  client.getPlayer().dropMessage(1, "레벨이 부족하여 이동할 수 없습니다.");
                  client.getPlayer().send(CWvsContext.enableActions(client.getPlayer()));
                  return;
               }

               client.getPlayer().changeMap(warpCharacter.getMap(), warpCharacter.getPosition());
            } else {
               if (FieldLimitType.RegularExpLoss.check(client.getPlayer().getMap().getFieldLimit())
                  || FieldLimitType.SpecificPortalScrollLimit.check(client.getPlayer().getMap().getFieldLimit())) {
                  client.getPlayer().dropMessage(1, "해당 스킬을 사용할 수 없는 위치입니다.");
                  client.getPlayer().send(CWvsContext.enableActions(client.getPlayer()));
                  return;
               }

               if (client.getPlayer().getMap().getLevelLimit() > warpCharacter.getLevel()) {
                  client.getPlayer().dropMessage(1, "지정한 길드원의 레벨이 부족하여 이동시킬 수 없습니다.");
                  client.getPlayer().send(CWvsContext.enableActions(client.getPlayer()));
                  return;
               }

               warpCharacter.changeMap(client.getPlayer().getMap(), client.getPlayer().getPosition());
            }
            break;
         default:
            level.applyTo(client.getPlayer());
      }
   }

   public static void guildContentsLogRequest(PacketDecoder slea, MapleCharacter chr) {
      if (chr != null) {
         int op = slea.readByte();
         switch (op) {
            case 1:
               chr.send(GuildContents.guildRankNPC());
               break;
            case 5:
               Guild guild = chr.getGuild();
               if (guild != null) {
                  chr.send(GuildContents.loadGuildLog(guild));
               }
         }
      }
   }

   private static class Invited {
      public String name;
      public int gid;
      public long expiration;

      public Invited(String n, int id) {
         this.name = n.toLowerCase();
         this.gid = id;
         this.expiration = System.currentTimeMillis() + 3600000L;
      }

      @Override
      public boolean equals(Object other) {
         if (!(other instanceof GuildHandler.Invited)) {
            return false;
         } else {
            GuildHandler.Invited oth = (GuildHandler.Invited)other;
            return this.gid == oth.gid && this.name.equals(oth);
         }
      }
   }

   public static enum JoinRequestType {
      LoadJoinRequester(5);

      private final int flag;

      private JoinRequestType(int flag) {
         this.flag = flag;
      }

      public int getFlag() {
         return this.flag;
      }

      public static GuildHandler.JoinRequestType getByType(int flag) {
         for (GuildHandler.JoinRequestType r : values()) {
            if (r.flag == flag) {
               return r;
            }
         }

         return null;
      }
   }
}
