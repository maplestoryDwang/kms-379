package network.game.processors;

import database.DBConfig;
import java.util.List;
import network.center.Center;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.context.party.PartyOperation;
import objects.context.party.PartyRequestType;
import objects.context.party.PartyResultType;
import objects.context.party.ProcessJoinRequestType;
import objects.context.party.boss.BossPartyRecruiment;
import objects.context.party.boss.BossPartyRecruimentEntry;
import objects.fields.Field;
import objects.quest.MapleQuest;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import scripting.EventInstanceManager;

public class PartyHandler {
   public static final void PartyResult(PacketDecoder slea, MapleClient c) {
      PartyRequestType requestType = PartyRequestType.getType(slea.readByte());
      PartyResultType resultType = PartyResultType.getType(slea.readInt());
      if (requestType != null) {
         if (resultType != null) {
            int partyID = slea.readInt();
            if (c.getPlayer().getParty() == null) {
               Party party = Center.Party.getParty(partyID);
               if (party != null) {
                  if (requestType == PartyRequestType.PartyResult) {
                     switch (resultType) {
                        case InviteResult:
                        default:
                           break;
                        case AcceptInvite:
                           if (party.getPartyMember().getPartyMemberList().size() < 6) {
                              c.getPlayer().setParty(party);
                              Center.Party.updateParty(partyID, PartyOperation.Join, new PartyMemberEntry(c.getPlayer()));
                              c.getPlayer().receivePartyMemberHP();
                              c.getPlayer().updatePartyMemberHP();
                           } else {
                              c.getPlayer().dropMessage(5, c.getPlayer().getName() + "님이 파티를 수락하셨습니다.");
                           }
                           break;
                        case DeclineInvite:
                           MapleCharacter leader = c.getChannelServer().getPlayerStorage().getCharacterById(party.getLeader().getId());
                           if (leader != null) {
                              leader.dropMessage(5, "'" + c.getPlayer().getName() + "'님이 파티 초대를 거절하였습니다.");
                           }
                     }
                  }
               } else {
                  c.getPlayer().dropMessage(5, "가입하려는 파티가 존재하지 않습니다.");
               }
            } else {
               c.getPlayer().dropMessage(5, "이미 파티에 가입되어 있어 파티에 가입할 수 없습니다.");
            }
         }
      }
   }

   public static final void PartyRequest(PacketDecoder slea, MapleClient c) {
      PartyRequestType type = PartyRequestType.getType(slea.readByte());
      Party party = c.getPlayer().getParty();
      MapleCharacter player = c.getPlayer();
      if (player != null) {
         Field field = player.getMap();
         if (field != null) {
            PartyMemberEntry memberEntry = null;
            if (party != null) {
               memberEntry = party.getMemberById(player.getId());
            }

            if (memberEntry == null) {
               memberEntry = new PartyMemberEntry(player);
            }

            if (type != null) {
               switch (type) {
                  case CreateParty:
                     String titlexx = slea.readMapleAsciiString();
                     boolean privatePartyx = slea.readByte() == 1;
                     boolean onlyLeaderPickUpx = slea.readByte() == 1;
                     if (party == null) {
                        party = Center.Party.createParty(memberEntry);
                        party.setPrivateParty(privatePartyx);
                        party.setOnlyLeaderPickUp(onlyLeaderPickUpx);
                        party.setPartyTitle(titlexx);
                        player.setParty(party);
                        PacketEncoder packet = new PacketEncoder();
                        Party.PartyPacket.CreateParty createParty = new Party.PartyPacket.CreateParty(party);
                        createParty.encode(packet);
                        player.send(packet.getPacket());
                     } else if (memberEntry.equals(party.getLeader()) && party.getPartyMember().getPartyMemberList().size() == 1) {
                        PacketEncoder packet = new PacketEncoder();
                        Party.PartyPacket.CreateParty createParty = new Party.PartyPacket.CreateParty(party);
                        createParty.encode(packet);
                        player.send(packet.getPacket());
                     } else {
                        player.dropMessage(5, "파티에 이미 가입되어 있어 파티를 만들 수 없습니다.");
                     }
                     break;
                  case WithdrawParty:
                     if (party != null) {
                        if (player.getMap().getFieldSetInstance() != null || player.getEventInstance() != null) {
                           player.dropMessage(5, "해당 맵에서는 파티 탈퇴가 불가능합니다.");
                           return;
                        }

                        boolean inBattle = false;

                        for (MapleCharacter partyMember : player.getPartyMembers()) {
                           if (partyMember.getMap().getFieldSetInstance() != null || player.getEventInstance() != null) {
                              inBattle = true;
                              break;
                           }
                        }

                        if (inBattle) {
                           player.dropMessage(5, "파티원이 보스와 전투 중에는 파티 탈퇴가 불가능합니다.");
                           return;
                        }

                        if (player.getHungryMuto() != null) {
                           player.getHungryMuto().gameOver(c.getPlayer(), false);
                        }

                        PartyMemberEntry leaderEntry = party.getLeader();
                        if (leaderEntry.getId() == player.getId()) {
                           field.onDisbandParty();
                           EventInstanceManager eim = c.getPlayer().getEventInstance();
                           if (eim != null) {
                              eim.stopEventTimer();
                              if (party != null) {
                                 List<MapleCharacter> players = field.getCharactersThreadsafe();
                                 party.getPartyMemberList().stream().forEach(px -> {
                                    if (px.isOnline()) {
                                       for (MapleCharacter p_ : players) {
                                          if (p_.getId() == px.getId() && p_.getEventInstance() != null) {
                                             p_.getEventInstance().unregisterPlayer(p_);
                                             p_.setEventInstance(null);
                                          }
                                       }
                                    }
                                 });
                              }

                              eim.dispose();
                           }

                           Center.Party.updateParty(party.getId(), PartyOperation.Disband, memberEntry);
                        } else {
                           c.getPlayer().getMap().onLeftParty(c.getPlayer());
                           Center.Party.updateParty(party.getId(), PartyOperation.Withdraw, memberEntry);
                        }

                        c.getPlayer().setParty(null);
                     }
                     break;
                  case AcceptInvite:
                     int partyIDx = slea.readInt();
                     if (party == null) {
                        party = Center.Party.getParty(partyIDx);
                        if (party != null) {
                           if (party.getMembers().size() < 6) {
                              c.getPlayer().setParty(party);
                              Center.Party.updateParty(party.getId(), PartyOperation.Join, memberEntry);
                              c.getPlayer().receivePartyMemberHP();
                              c.getPlayer().updatePartyMemberHP();
                           } else {
                              c.getPlayer().dropMessage(5, "해당 파티는 이미 파티원이 꽉 찼습니다.");
                           }
                        } else {
                           c.getPlayer().dropMessage(5, "가입하려는 파티는 존재하지 않습니다.");
                        }
                     } else {
                        c.getPlayer().dropMessage(5, "이미 파티에 가입되어 있어 파티에 가입할 수 없습니다.");
                     }
                     break;
                  case Invite:
                     String targetName = slea.readMapleAsciiString();
                     int channel = Center.Find.findChannel(targetName);
                     if (party == null) {
                        party = Center.Party.createParty(memberEntry);
                        party.setPartyTitle(memberEntry.getName() + "의 파티");
                        c.getPlayer().setParty(party);
                        party.setPartyTitle(party.getPatryTitle());
                        PacketEncoder packet = new PacketEncoder();
                        Party.PartyPacket.CreateParty createParty = new Party.PartyPacket.CreateParty(party);
                        createParty.encode(packet);
                        player.send(packet.getPacket());
                        if (channel > 0) {
                           MapleCharacter target = GameServer.getInstance(channel).getPlayerStorage().getCharacterByName(targetName);
                           if (target != null && target.getParty() == null) {
                              if (party.getPartyMember().getPartyMemberList().size() < 6) {
                                 c.getPlayer().dropMessage(1, target.getName() + "님을 파티에 초대했습니다.");
                                 packet = new PacketEncoder();
                                 Party.PartyPacket.InviteParty p = new Party.PartyPacket.InviteParty(
                                    party.getId(), player.getId(), player.getName(), player.getLevel(), player.getJob()
                                 );
                                 p.encode(packet);
                                 target.send(packet.getPacket());
                              } else {
                                 c.getPlayer().dropMessage(5, "이미 파티원이 최대로 가득 찬 상태입니다.");
                              }
                           } else {
                              c.getPlayer().dropMessage(5, "이미 파티에 가입되어 있는 대상입니다.");
                           }
                        } else {
                           c.getPlayer().dropMessage(5, "대상을 찾지 못했습니다.");
                        }
                     } else if (channel > 0) {
                        MapleCharacter target = GameServer.getInstance(channel).getPlayerStorage().getCharacterByName(targetName);
                        if (target != null && target.getParty() == null) {
                           if (party.getMembers().size() < 6) {
                              c.getPlayer().dropMessage(1, target.getName() + "님을 파티에 초대했습니다.");
                              PacketEncoder packet = new PacketEncoder();
                              Party.PartyPacket.InviteParty p = new Party.PartyPacket.InviteParty(
                                 party.getId(), player.getId(), player.getName(), player.getLevel(), player.getJob()
                              );
                              p.encode(packet);
                              target.send(packet.getPacket());
                           } else {
                              c.getPlayer().dropMessage(5, "이미 파티원이 최대로 가득 찬 상태입니다.");
                           }
                        } else {
                           c.getPlayer().dropMessage(5, "이미 파티에 가입되어 있는 대상입니다.");
                        }
                     } else {
                        c.getPlayer().dropMessage(5, "대상을 찾지 못했습니다.");
                     }
                     break;
                  case KickParty:
                     PartyMemberEntry leaderEntry = party.getLeader();
                     if (leaderEntry.getId() == player.getId()) {
                        if (player.getMap().getFieldSetInstance() != null || player.getEventInstance() != null) {
                           player.dropMessage(5, "해당 맵에서는 파티원 추방이 불가능합니다.");
                           return;
                        }

                        PartyMemberEntry entry = party.getMemberById(slea.readInt());
                        if (entry != null) {
                           Center.Party.updateParty(party.getId(), PartyOperation.KickParty, entry);
                           if (c.getPlayer().getEventInstance() != null && entry.isOnline()) {
                              c.getPlayer().getEventInstance().disbandParty();
                              MapleCharacter kickedPlayer = GameServer.getInstance(entry.getChannel()).getPlayerStorage().getCharacterById(entry.getId());
                              if (kickedPlayer != null) {
                                 kickedPlayer.getMap().onLeftParty(kickedPlayer);
                              }
                           }
                        }
                     }
                     break;
                  case ChangeLeader:
                     if (party != null) {
                        leaderEntry = party.getLeader();
                        PartyMemberEntry newLeader = party.getMemberById(slea.readInt());
                        if (newLeader != null && leaderEntry.getId() == player.getId()) {
                           Center.Party.updateParty(party.getId(), PartyOperation.ChangeLeader, newLeader);
                        }
                     }
                     break;
                  case ChangePartySetting:
                     String newTitle = slea.readMapleAsciiString();
                     privatePartyx = slea.readByte() == 1;
                     onlyLeaderPickUpx = slea.readByte() == 1;
                     if (newTitle.length() < 0) {
                        c.getPlayer().dropMessage(1, "한 글자 이상의 변경할 파티명을 입력해주십시오.");
                        return;
                     }

                     party.setPrivateParty(privatePartyx);
                     party.setOnlyLeaderPickUp(onlyLeaderPickUpx);
                     party.setPartyTitle(newTitle);
                     Center.Party.updateParty(party.getId(), PartyOperation.PartySetting, memberEntry);
                     break;
                  case RegisterBossPartyRecruiment: {
                     String partyTitle = slea.readMapleAsciiString();
                     boolean privateParty = slea.readByte() == 1;
                     boolean onlyLeaderPickUp = slea.readByte() == 1;
                     int bossType = slea.readInt();
                     byte difficultyx = slea.readByte();
                     String titlex = slea.readMapleAsciiString();
                     int minLevelx = slea.readInt();
                     int minArcanex = slea.readInt();
                     int minAthenticx = slea.readInt();
                     int minDojangRankx = slea.readInt();
                     int minUnionx = slea.readInt();
                     if (party != null) {
                        player.dropMessage(5, "파티에 이미 가입되어 있어 파티를 만들 수 없습니다.");
                        return;
                     }

                     party = Center.Party.createParty(memberEntry);
                     party.setPrivateParty(privateParty);
                     party.setOnlyLeaderPickUp(onlyLeaderPickUp);
                     party.setPartyTitle(partyTitle);
                     player.setParty(party);
                     BossPartyRecruimentEntry entry = new BossPartyRecruimentEntry(
                        titlex, difficultyx, minLevelx, minArcanex, minAthenticx, minDojangRankx, minUnionx
                     );
                     BossPartyRecruiment recruimentx = new BossPartyRecruiment(bossType, player.getName(), player.getId(), entry);
                     party.setBossPartyRecruiment(recruimentx);
                     if (DBConfig.isGanglim) {
                        Center.Broadcast.broadcastMessage(CField.chatMsg(22, "[보스파티모집] " + player.getName() + " : " + titlex));
                     }

                     PacketEncoder packet = new PacketEncoder();
                     Party.PartyPacket.CreateParty createParty = new Party.PartyPacket.CreateParty(party);
                     createParty.encode(packet);
                     player.send(packet.getPacket());
                     Center.BossPartyRecruiment.registerBossPartyRecruiment(party);
                     break;
                  }
                  case RegisterBossPartyRecruimentInParty: {
                     int bossType = slea.readInt();
                     byte difficultyx = slea.readByte();
                     String titlex = slea.readMapleAsciiString();
                     int minLevelx = slea.readInt();
                     int minArcanex = slea.readInt();
                     int minAthenticx = slea.readInt();
                     int minDojangRankx = slea.readInt();
                     int minUnionx = slea.readInt();
                     BossPartyRecruimentEntry entry = new BossPartyRecruimentEntry(
                        titlex, difficultyx, minLevelx, minArcanex, minAthenticx, minDojangRankx, minUnionx
                     );
                     BossPartyRecruiment recruimentx = new BossPartyRecruiment(bossType, player.getName(), player.getId(), entry);
                     party.setBossPartyRecruiment(recruimentx);
                     if (DBConfig.isGanglim) {
                        Center.Broadcast.broadcastMessage(CField.chatMsg(22, "[보스파티모집] " + player.getName() + " : " + titlex));
                     }

                     PacketEncoder packet = new PacketEncoder();
                     Party.UpdateBossPartyRecruiment.Create create = new Party.UpdateBossPartyRecruiment.Create(recruimentx);
                     create.encode(packet);
                     Center.Party.partyPacket(party.getId(), packet.getPacket(), null);
                     Center.BossPartyRecruiment.registerBossPartyRecruiment(party);
                     break;
                  }
                  case BossPartyRecruimentList:
                     Center.BossPartyRecruiment.displayBossPartyRecruiment(player, -1, (byte)-1);
                     break;
                  case ChangeBossPartyRecruimentSetting: {
                     int bossType = slea.readInt();
                     byte difficulty = slea.readByte();
                     String title = slea.readMapleAsciiString();
                     int minLevel = slea.readInt();
                     int minArcane = slea.readInt();
                     int minAthentic = slea.readInt();
                     int minDojangRank = slea.readInt();
                     int minUnion = slea.readInt();
                     BossPartyRecruiment recruiment = party.getBossPartyRecruiment();
                     if (recruiment == null) {
                        return;
                     }

                     recruiment.setBossType(bossType);
                     BossPartyRecruimentEntry entry = recruiment.getEntry();
                     if (entry == null) {
                        return;
                     }

                     entry.setBossDifficulty(difficulty);
                     entry.setTitle(title);
                     entry.setMinLevel(minLevel);
                     entry.setMinArcane(minArcane);
                     entry.setMinAthentic(minAthentic);
                     entry.setMinDojangRank(minDojangRank);
                     entry.setMinUnion(minUnion);
                     PacketEncoder packet = new PacketEncoder();
                     Party.UpdateBossPartyRecruiment.ChangeSetting setting = new Party.UpdateBossPartyRecruiment.ChangeSetting(recruiment);
                     setting.encode(packet);
                     Center.Party.partyPacket(party.getId(), packet.getPacket(), null);
                     break;
                  }
                  case OpenPartyRecruiment: {
                     int id = slea.readInt();
                     int bossType = slea.readInt();
                     int bossDifficulty = slea.readInt();
                     Center.BossPartyRecruiment.openBossPartyRecruiment(player, id, bossType, bossDifficulty);
                     break;
                  }
                  case JoinRequestFromBossPartyRecruiment: {
                     int id = slea.readInt();
                     Center.BossPartyRecruiment.joinRequestFromBossPartyRecruiment(player, id);
                     break;
                  }
                  case CancelJoinRequestFromBossPartyRecruiment:
                     int partyID = slea.readInt();
                     Party targetParty = Center.Party.getParty(partyID);
                     if (targetParty != null) {
                        targetParty.getPartyMember().removeRegisterRequestPlayer(player.getId());
                        PacketEncoder packet = new PacketEncoder();
                        Party.RequestJoinPartyFromRecruiment.RemoveRequest p = new Party.RequestJoinPartyFromRecruiment.RemoveRequest(player.getId());
                        p.encode(packet);
                        Center.Party.partyPacket(targetParty.getId(), packet.getPacket(), null);
                        packet = new PacketEncoder();
                        Party.CancelJoinRequestPacket.CancelJoinRequest request = new Party.CancelJoinRequestPacket.CancelJoinRequest(partyID);
                        request.encode(packet);
                        player.send(packet.getPacket());
                     }
                     break;
                  case ProcessJoinRequestFromBossPartyRecruiment:
                     ProcessJoinRequestType requestType = ProcessJoinRequestType.getType(slea.readInt());
                     int playerID = slea.readInt();
                     party.getPartyMember().removeRegisterRequestPlayer(playerID);
                     PacketEncoder packet = new PacketEncoder();
                     Party.RequestJoinPartyFromRecruiment.RemoveRequest p = new Party.RequestJoinPartyFromRecruiment.RemoveRequest(playerID);
                     p.encode(packet);
                     Center.Party.partyPacket(party.getId(), packet.getPacket(), null);
                     switch (requestType) {
                        case AcceptRequest:
                           if (party.getPartyMember().getPartyMemberList().size() >= 6) {
                              player.dropMessage(1, "파티 정원이 가득차 가입을 수락할 수 없습니다.");
                              return;
                           }

                           for (PartyMemberEntry e : party.getPartyMember().getPartyMemberList()) {
                              if (e.getId() == playerID) {
                                 return;
                              }
                           }

                           MapleCharacter target = null;
                           PartyMemberEntry targetEntry = null;

                           for (GameServer gameServer : GameServer.getAllInstances()) {
                              target = gameServer.getPlayerStorage().getCharacterById(playerID);
                              if (target != null) {
                                 targetEntry = new PartyMemberEntry(target);
                                 party.getPartyMember().addMember(targetEntry);
                                 target.setParty(party);
                                 packet = new PacketEncoder();
                                 Party.PartyPacket.JoinCompleteMessage message = new Party.PartyPacket.JoinCompleteMessage(party.isOnlyLeaderPickUp());
                                 message.encode(packet);
                                 target.send(packet.getPacket());
                                 packet = new PacketEncoder();
                                 Party.PartyPacket.PartyDataUpdate logOnOff = new Party.PartyPacket.PartyDataUpdate(party, target.getClient().getChannel());
                                 logOnOff.encode(packet);
                                 target.send(packet.getPacket());
                                 break;
                              }
                           }

                           if (target != null) {
                              packet = new PacketEncoder();
                              Party.PartyPacket.JoinMember joinMember = new Party.PartyPacket.JoinMember(
                                 party, target.getName(), target.getClient().getChannel()
                              );
                              joinMember.encode(packet);
                              Center.Party.partyPacket(party.getId(), packet.getPacket(), targetEntry);
                           }

                           return;
                        case DeclineRequest:
                           int ch = Center.Find.findChannel(playerID);
                           if (ch > 0) {
                              MapleCharacter player_ = GameServer.getInstance(ch).getPlayerStorage().getCharacterById(playerID);
                              if (player_ != null) {
                                 packet = new PacketEncoder();
                                 Party.BossPartyRecruimentMessage.DeclineJoinRequest message = new Party.BossPartyRecruimentMessage.DeclineJoinRequest(
                                    c.getPlayer().getName(), party.getId()
                                 );
                                 message.encode(packet);
                                 player_.send(packet.getPacket());
                                 return;
                              }
                           }

                           return;
                        default:
                           return;
                     }
                  case CancelBossPartyRecruiment:
                     Center.BossPartyRecruiment.removeBossPartyRecruiment(party);
                     packet = new PacketEncoder();
                     Party.UpdateBossPartyRecruiment.CancelBossPartyRecruiment p2 = new Party.UpdateBossPartyRecruiment.CancelBossPartyRecruiment();
                     p2.encode(packet);
                     Center.Party.partyPacket(party.getId(), packet.getPacket(), null);

                     for (PartyMemberEntry ex : party.getPartyMember().getRegisterRequestList()) {
                        int ch = Center.Find.findChannel(ex.getName());
                        if (ch > 0) {
                           MapleCharacter player_ = GameServer.getInstance(ch).getPlayerStorage().getCharacterByName(ex.getName());
                           if (player_ != null) {
                              packet = new PacketEncoder();
                              Party.BossPartyRecruimentPacket.CancelJoinMember cancelJoinMember = new Party.BossPartyRecruimentPacket.CancelJoinMember(
                                 party.getId(), party.getLeader().getName()
                              );
                              cancelJoinMember.encode(packet);
                              player_.send(packet.getPacket());
                           }
                        }
                     }

                     party.getPartyMember().clearRegisterRequestPlayer();
                     party.getBossPartyRecruiment().setEntry(null);
               }
            }
         }
      }
   }

   public static final void allowPartyInvite(PacketDecoder slea, MapleClient c) {
      if (slea.readByte() > 0) {
         c.getPlayer().getQuestRemove(MapleQuest.getInstance(122901));
      } else {
         c.getPlayer().getQuestIfNullAdd(MapleQuest.getInstance(122901));
      }
   }

   public static final void MemberSearch(PacketDecoder slea, MapleClient c) {
   }

   public static final void PartySearch(PacketDecoder slea, MapleClient c) {
   }

   public static final void PartyListing(PacketDecoder slea, MapleClient c) {
   }
}
