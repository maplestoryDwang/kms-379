package network.game.processors;

import constants.ArcaneStoryQuests;
import constants.DailyEventType;
import constants.GameConstants;
import constants.HexaMatrixConstants;
import constants.QuestExConstants;
import constants.ServerConstants;
import constants.devtempConstants.MapleDailyGiftInfo;
import database.DBConfig;
import database.DBConnection;
import database.loader.CharacterSaveFlag;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import logging.LoggingManager;
import logging.entry.ConnectLog;
import logging.entry.ConnectLogType;
import network.auction.AuctionServer;
import network.center.AccountIdChannelPair;
import network.center.Center;
import network.center.CharacterTransfer;
import network.center.PlayerBuffStorage;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import network.models.GuildContents;
import network.models.LoginPacket;
import network.shop.CashShopServer;
import objects.context.friend.FriendEntry;
import objects.context.guild.Guild;
import objects.context.guild.GuildCharacter;
import objects.context.guild.GuildPacket;
import objects.context.messenger.Messenger;
import objects.context.messenger.MessengerCharacter;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.context.party.PartyOperation;
import objects.effect.child.TextEffect;
import objects.fields.Field;
import objects.fields.FieldLimitType;
import objects.fields.child.etc.DamageMeasurementRank;
import objects.fields.child.union.MapleUnion;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventory;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.quest.MobQuest;
import objects.users.CharacterNameAndId;
import objects.users.MailBox;
import objects.users.MapleCabinet;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MapleTrait;
import objects.users.potential.CharacterPotential;
import objects.users.potential.CharacterPotentialHolder;
import objects.users.skills.LinkSkill;
import objects.users.skills.SkillFactory;
import objects.users.skills.VMatrixSlot;
import objects.users.stats.HexaCore;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.AdminClient;
import objects.utils.CurrentTime;
import objects.utils.FileoutputUtil;
import objects.utils.Randomizer;
import objects.utils.Timer;
import scripting.NPCScriptManager;
import scripting.newscripting.ScriptManager;

public class InterServerHandler {
   public static final void EnterCS(MapleClient c, MapleCharacter chr, boolean npc) {
      try {
         c.loadMacsIfNescessary();
         if (chr == null || c.getChannelServer().getPlayerStorage().getCharacterById(chr.getId()) == null || chr.getMap().getCharacterById(chr.getId()) == null
            )
          {
            c.getSession().writeAndFlush(CField.serverBlocked(2));
            c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
            return;
         }

         if (npc) {
            if (DBConfig.isGanglim && chr.getMapId() == ServerConstants.StartMap) {
               c.getPlayer().dropMessage(5, "해당 맵에선 이용할 수 없습니다.");
               c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
               return;
            }

            chr.getClient().removeClickedNPC();
            NPCScriptManager.getInstance().dispose(chr.getClient());
            chr.getClient().getSession().writeAndFlush(CWvsContext.enableActions(chr));
            NPCScriptManager.getInstance().start(c, 1531010);
         } else {
            if (chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null) {
               c.getSession().writeAndFlush(CField.serverBlocked(2));
               c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
               return;
            }

            if (Center.getPendingCharacterSize() >= 10) {
               chr.dropMessage(1, "The server is busy at the moment. Please try again in a minute or less.");
               c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
               return;
            }

            GameServer ch = GameServer.getInstance(c.getChannel());
            chr.changeRemoval();
            if (chr.getMessenger() != null) {
               MessengerCharacter messengerplayer = new MessengerCharacter(chr);
               Center.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
            }

            List<String> macString = new ArrayList<>();
            if (c.getMacs() != null) {
               macString = c.getMacs();
            }

            StringBuilder sb = new StringBuilder();
            sb.append("접속 로그 (아이피 : ");
            sb.append(c.getSessionIPAddress());
            sb.append(", 접속 서버 : 캐시샵)");
            String getMac = "";
            String getVolume = "";
            if (macString.size() == 2) {
               getMac = macString.get(0);
               getVolume = macString.get(1);
            }

            LoggingManager.putLog(
               new ConnectLog(
                  c.getPlayer().getName(), c.getAccountName(), c.getPlayer().getId(), c.getAccID(), ConnectLogType.Connect.getType(), getMac, getVolume, sb
               )
            );
            PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
            PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getDiseasesList());
            Center.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), -10);
            ch.removePlayer(chr);
            c.updateLoginState(3, c.getSessionIPAddress());
            chr.saveToDB(false, false);
            chr.getMap().removePlayer(chr);
            c.getSession().writeAndFlush(CField.getChannelChange(c, Integer.parseInt(CashShopServer.getIP().split(":")[1])));
            c.setPlayer(null);
            c.setReceiving(false);
         }
      } catch (Exception var8) {
         System.out.println("EnterCS 함수 실행중 오류발생" + var8.toString());
         var8.printStackTrace();
      }
   }

   public static final void EnterAuction(MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      c.loadMacsIfNescessary();
      if (ServerConstants.blockedEnterAuction) {
         chr.dropMessage(5, "현재 경매장에 접속할 수 없습니다. 잠시 후 다시 시도해주시기 바랍니다.");
         chr.send(CWvsContext.enableActions(chr));
      } else if (chr == null
         || c.getChannelServer().getPlayerStorage().getCharacterById(chr.getId()) == null
         || chr.getMap().getCharacterById(chr.getId()) == null) {
         c.getSession().writeAndFlush(CField.serverBlocked(2));
         c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
      } else if (chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null) {
         c.getSession().writeAndFlush(CField.serverBlocked(2));
         c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
      } else if (Center.getPendingCharacterSize() >= 10) {
         chr.dropMessage(1, "The server is busy at the moment. Please try again in a minute or less.");
         c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
      } else {
         GameServer ch = GameServer.getInstance(c.getChannel());
         chr.changeRemoval();
         if (chr.getMessenger() != null) {
            MessengerCharacter messengerplayer = new MessengerCharacter(chr);
            Center.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
         }

         List<String> macString = new ArrayList<>();
         if (c.getMacs() != null) {
            macString = c.getMacs();
         }

         StringBuilder sb = new StringBuilder();
         sb.append("접속 로그 (아이피 : ");
         sb.append(c.getSessionIPAddress());
         sb.append(", 접속 서버 : 옥션)");
         String getMac = "";
         String getVolume = "";
         if (macString.size() == 2) {
            getMac = macString.get(0);
            getVolume = macString.get(1);
         }

         LoggingManager.putLog(
            new ConnectLog(
               c.getPlayer().getName(), c.getAccountName(), c.getPlayer().getId(), c.getAccID(), ConnectLogType.Connect.getType(), getMac, getVolume, sb
            )
         );
         PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
         PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getDiseasesList());
         Center.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), -20);
         ch.removePlayer(chr);
         c.updateLoginState(3, c.getSessionIPAddress());
         chr.saveToDB(false, false);
         chr.getMap().removePlayer(chr);
         c.getSession().writeAndFlush(CField.getChannelChange(c, AuctionServer.getPort()));
         c.setPlayer(null);
         c.setReceiving(false);
      }
   }

   public static final void Loggedin(int playerid, MapleClient c, boolean noGame) {
      Timer.EtcTimer.getInstance()
         .schedule(
            () -> {
               try {
                  GameServer channelServer = c.getChannelServer();
                  CharacterTransfer transfer = channelServer.getPlayerStorage().getPendingCharacter(playerid);
                  MapleCharacter player;
                  if (transfer == null) {
                     c.updateLinkSkills(playerid);
                     player = MapleCharacter.loadCharFromDB(playerid, c, true);
                  } else {
                     player = MapleCharacter.ReconstructChr(transfer, c, true);
                     player.setLastChangedChannelTime(System.currentTimeMillis());
                  }

                  try {
                     boolean find = false;
                     boolean forcebreak = false;

                     for (GameServer gs : GameServer.getAllInstances()) {
                        if (find || forcebreak) {
                           break;
                        }

                        MapleCharacter findchr = gs.getPlayerStorage().getCharacterByName(player.getName());
                        if (findchr != null && findchr.getClient().getAccID() == c.getAccID()) {
                           find = true;
                           if (!findchr.getClient().getSession().isActive()
                              || !findchr.getClient().getSession().isOpen()
                              || System.currentTimeMillis() - findchr.getLastHeartBeatTime() > 120000L) {
                              findchr.getClient().disconnect(false);
                              findchr.getClient().getSession().close();
                              System.out.println("팅겼다고인마");

                              try {
                                 if (findchr.getClient().getPlayer() == null) {
                                    findchr.setBlockSave(true);
                                    gs.removePlayer(findchr);
                                    forcebreak = true;
                                    find = false;
                                 }
                              } catch (Exception var38) {
                              }
                           }
                        }

                        if (find || forcebreak) {
                           break;
                        }

                        try {
                           for (Field map : new ArrayList<>(gs.getMapFactory().getAllMaps())) {
                              if (find || forcebreak) {
                                 break;
                              }

                              if (map != null) {
                                 try {
                                    for (MapleCharacter chr : new ArrayList<>(map.getCharacters())) {
                                       if (find || forcebreak) {
                                          break;
                                       }

                                       if (chr != null && chr.getClient().getAccID() == c.getAccID()) {
                                          if (!chr.getClient().getSession().isActive()
                                             || !chr.getClient().getSession().isOpen()
                                             || System.currentTimeMillis() - chr.getLastHeartBeatTime() > 120000L) {
                                             chr.getClient().disconnect(false);
                                             chr.getClient().getSession().close();
                                             System.out.println("팅겼다고인마");
                                          }

                                          find = true;
                                       }
                                    }
                                 } catch (Exception var49) {
                                    FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "캐릭터 로드중 오류 발생", var49);
                                 }
                              }
                           }
                        } catch (Exception var50) {
                           FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "맵 로드중 발생", var50);
                        }
                     }

                     try {
                        for (MapleCharacter p : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
                           if (find || forcebreak) {
                              break;
                           }

                           if (p != null && p.getAccountID() == c.getAccID()) {
                              if (!p.getClient().getSession().isActive() || !p.getClient().getSession().isOpen()) {
                                 p.getClient().setPlayer(p);
                                 p.getClient().disconnect(false);
                                 p.getClient().getSession().close();
                                 System.out.println("팅겼다고인마");
                              }

                              find = true;
                           }
                        }
                     } catch (Exception var51) {
                        FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "캐시샵 캐릭터 로드중 오류 발생", var51);
                     }

                     try {
                        for (MapleCharacter p : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
                           if (find || forcebreak) {
                              break;
                           }

                           if (p != null && p.getAccountID() == c.getAccID()) {
                              if (p.getClient() != null && (!p.getClient().getSession().isActive() || !p.getClient().getSession().isOpen())) {
                                 p.getClient().setPlayer(p);
                                 p.getClient().disconnect(false);
                                 p.getClient().getSession().close();
                                 System.out.println("팅겼다고인마");
                              }

                              find = true;
                           }
                        }
                     } catch (Exception var52) {
                        FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "경매장 캐릭터 로드중 오류 발생", var52);
                     }

                     if (find) {
                        return;
                     }
                  } catch (Exception var53) {
                     FileoutputUtil.log("Log_Login_Error.rtf", "중복 캐릭터 로드중 오류발생");
                     FileoutputUtil.outputFileError("Log_Login_Error.rtf", var53);
                  }

                  c.setPlayer(player);
                  c.setAccID(player.getAccountID());

                  try (Connection con = DBConnection.getConnection()) {
                     PreparedStatement ps = con.prepareStatement("SELECT `gm` FROM accounts WHERE id = ?");
                     ps.setInt(1, player.getAccountID());
                     ResultSet rs = ps.executeQuery();

                     while (rs.next()) {
                        c.setGm(rs.getInt("gm") > 0);
                     }

                     rs.close();
                     ps.close();
                  } catch (SQLException var48) {
                  }

                  try {
                     if (transfer != null) {
                        player.setTempKeyValue("FromTransfer", true);
                     }
                  } catch (Exception var36) {
                  }

                  if (c.getPlayer().isGM()
                     && DBConfig.isHosting
                     && !c.getAccountName().equals("vmfl1122")
                     && !c.getAccountName().equals("vmfl112")
                     && !c.getAccountName().equals("vmfl119")
                     && DBConfig.DB_PASSWORD.equals("J2vs@efh6@K6!2")) {
                     System.err.println("헉 님은 GM이 아니라서 여기서 팅겼음!" + c.getAccountName());
                     return;
                  }

                  if (ServerConstants.workingReboot) {
                     c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "현재 서버가 점검 진행중입니다. 점검 내용은 공지사항을 확인해주시기 바랍니다."));
                     return;
                  }

                  c.updateLoginState(2, c.getSessionIPAddress());

                  try {
                     channelServer.addPlayer(player);
                  } catch (Exception var35) {
                     FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "addPlayer 처리중 오류발생", var35);
                  }

                  try {
                     player.sortedVCoreSkillsReadLock();
                  } catch (Exception var34) {
                     FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "V코어 정렬중 오류 발생", var34);
                  }

                  try {
                     if (DBConfig.isGanglim) {
                        int[] questList = new int[]{6500, 12396};

                        for (int i : questList) {
                           if (player.getQuestStatus(i) != 2) {
                              player.forceCompleteQuest(i);
                           }
                        }

                        player.updateOneInfo(QuestExConstants.IntensePowerCrystal.getQuestID(), "count", "180");
                     }

                     if (player.getLevel() >= 260) {
                        MapleQuest quest = MapleQuest.getInstance(1484);
                        MapleQuestStatus qs = player.getQuest(quest);
                        if (qs.getStatus() != 2) {
                           MapleQuest.getInstance(1484).forceComplete(player, 2003);
                        }
                     }

                     if (player.getLevel() >= 260) {
                        int questId = QuestExConstants.SixthJobQuest.getQuestID();
                        MapleQuest quest = MapleQuest.getInstance(questId);
                        MapleQuestStatus qs = player.getQuest(quest);
                        if (quest != null && qs.getStatus() != 2) {
                           MapleQuest.getInstance(questId).forceComplete(player, 2003);
                        }

                        player.send(CWvsContext.hexaMatrixInformation());
                        if (player.getHexaCore() == null) {
                           player.setHexaCore(new HexaCore(player.getId()));
                        }

                        for (int coreId : HexaMatrixConstants.getAllJobCores(player.getJob())) {
                           if (player.getHexaCore().getSkillCoreLevel(coreId) == 0) {
                              player.getHexaCore().setSkillCoreLevel(player, coreId, 1);
                           }
                        }

                        player.send(CWvsContext.onCharacterModified(player, -1L));
                        HyperHandler.updateSkills(player, 0);
                     }

                     if (player.getLevel() >= 200) {
                        MapleQuest questx = MapleQuest.getInstance(1466);
                        MapleQuestStatus qsx = player.getQuest(questx);
                        if (qsx.getStatus() != 2) {
                           MapleQuest.getInstance(1466).forceComplete(player, 2003);
                        }
                     }

                     MapleQuest questx = MapleQuest.getInstance(100590);
                     MapleQuestStatus qsx = player.getQuest(questx);
                     if (qsx.getStatus() != 2) {
                        MapleQuest.getInstance(100590).forceComplete(player, 2003);
                     }

                     if (player.getLevel() >= 270) {
                        for (int arcaneStoryQuest : ArcaneStoryQuests.Audium) {
                           MapleQuest questxx = MapleQuest.getInstance(arcaneStoryQuest);
                           MapleQuestStatus qsxx = player.getQuest(questxx);
                           if (questxx != null && qsxx.getStatus() != 2) {
                              MapleQuest.getInstance(arcaneStoryQuest).forceComplete(player, 2003);
                           }
                        }
                     }

                     if (player.getLevel() >= 275) {
                        for (int arcaneStoryQuestx : ArcaneStoryQuests.Dowonkyung) {
                           MapleQuest questxx = MapleQuest.getInstance(arcaneStoryQuestx);
                           MapleQuestStatus qsxx = player.getQuest(questxx);
                           if (questxx != null && qsxx.getStatus() != 2) {
                              MapleQuest.getInstance(arcaneStoryQuestx).forceComplete(player, 2003);
                           }
                        }
                     }

                     if (player.getLevel() >= 280) {
                        for (int arcaneStoryQuestxx : ArcaneStoryQuests.Arteria) {
                           MapleQuest questxx = MapleQuest.getInstance(arcaneStoryQuestxx);
                           MapleQuestStatus qsxx = player.getQuest(questxx);
                           if (questxx != null && qsxx.getStatus() != 2) {
                              MapleQuest.getInstance(arcaneStoryQuestxx).forceComplete(player, 2003);
                           }
                        }
                     }
                  } catch (Exception var46) {
                     FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "퀘스트 데이터 처리중 오류 발생", var46);
                  }

                  if (!DBConfig.isGanglim) {
                     try {
                        if (player.getLevel() >= 200) {
                           int[] professionSkillIDs = new int[]{92000000, 92010000, 92020000, 92030000, 92040000};

                           for (int professionSkill : professionSkillIDs) {
                              if (player.getProfessionLevel(professionSkill) <= 0) {
                                 player.changeProfessionLevelExp(professionSkill, 1, 0, (byte)10);
                              }
                           }
                        }
                     } catch (Exception var45) {
                     }
                  }

                  try {
                     if (GameConstants.isDemon(player.getJob()) && player.getOneInfoQuestInteger(1234591, "DemonTraitFix") <= 0) {
                        player.getTrait(MapleTrait.MapleTraitType.charisma).addTrueExp(4563, player);
                        player.getTrait(MapleTrait.MapleTraitType.will).addTrueExp(4563, player);
                        player.updateOneInfo(1234591, "DemonTraitFix", "1");
                     }
                  } catch (Exception var33) {
                  }

                  try {
                     if (GameConstants.isEunWol(player.getJob()) && player.getLevel() >= 200) {
                        MapleQuest questxx = MapleQuest.getInstance(1542);
                        MapleQuestStatus qsxx = player.getQuest(questxx);
                        if (questxx != null && qsxx.getStatus() != 2) {
                           MapleQuest.getInstance(1542).forceComplete(player, 2003);
                           player.send(CWvsContext.getScriptProgressMessage("[정령결속 극대화]를 통해 그리운 랑의 모습을 구현할 수 있게 되었습니다."));
                        }
                     }

                     if (GameConstants.isLara(player.getJob()) && player.getSkillLevel(160011005) == 0) {
                        player.changeSingleSkillLevel(SkillFactory.getSkill(160011005), 1, (byte)1);
                     }

                     MapleQuestStatus mqs = null;
                     if ((mqs = player.getQuestIfNullAdd(MapleQuest.getInstance(1237))).getCustomData() != null
                        && mqs.getCustomData().equals("start")
                        && (player.getMobQuest(1237) == null || player.getMobQuest(1237).isEmpty())) {
                        player.registerMobQuest(1237, new MobQuest(1237, 8200001, 200, 0));
                     }

                     if ((mqs = player.getQuestIfNullAdd(MapleQuest.getInstance(1238))).getCustomData() != null
                        && mqs.getCustomData().equals("start")
                        && (player.getMobQuest(1238) == null || player.getMobQuest(1238).isEmpty())) {
                        player.registerMobQuest(1238, new MobQuest(1238, 8810018, 1, 0));
                     }

                     if ((mqs = player.getQuestIfNullAdd(MapleQuest.getInstance(1239))).getCustomData() != null
                        && mqs.getCustomData().equals("start")
                        && (player.getMobQuest(1239) == null || player.getMobQuest(1239).isEmpty())) {
                        for (int mobID = 8610010; mobID <= 8610013; mobID++) {
                           player.registerMobQuest(1239, new MobQuest(1239, mobID, 50, 0));
                        }
                     }

                     if ((mqs = player.getQuestIfNullAdd(MapleQuest.getInstance(1240))).getCustomData() != null
                        && mqs.getCustomData().equals("start")
                        && (player.getMobQuest(1240) == null || player.getMobQuest(1240).isEmpty())) {
                        player.registerMobQuest(1240, new MobQuest(1240, 8290100, 1, 0));
                     }
                  } catch (Exception var44) {
                     FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "퀘스트 데이터 처리중 오류발생 _ 2", var44);
                  }

                  try {
                     if (transfer != null) {
                        player.giveCoolDowns(PlayerBuffStorage.getCooldownsFromStorage(player.getId()));
                     }
                  } catch (Exception var32) {
                     FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "쿨다운 설정중 오류 발생", var32);
                  }

                  try {
                     if (noGame) {
                        c.updateLoginState(0, c.getSessionIPAddress());
                        c.clearInformation();
                        c.disconnect(false);
                        return;
                     }

                     List<String> macString = new ArrayList<>();
                     if (c.getMacs() != null) {
                        macString = c.getMacs();
                     }

                     StringBuilder sb = new StringBuilder();
                     sb.append("접속 로그 (아이피 : ");
                     sb.append(c.getSessionIPAddress());
                     sb.append(", 접속 서버 : 인게임)");
                     String getMac = "";
                     String getVolume = "";
                     if (macString.size() == 2) {
                        getMac = macString.get(0);
                        getVolume = macString.get(1);
                     }

                     LoggingManager.putLog(
                        new ConnectLog(
                           c.getPlayer().getName(),
                           c.getAccountName(),
                           c.getPlayer().getId(),
                           c.getAccID(),
                           ConnectLogType.Connect.getType(),
                           getMac,
                           getVolume,
                           sb
                        )
                     );
                  } catch (Exception var31) {
                     FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "접속 처리중 오류 발생", var31);
                  }

                  try {
                     player.checkSkills();
                  } catch (Exception var30) {
                     FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "checkSkills 오류 발생", var30);
                  }

                  try {
                     if (player.getGuildId() > 0) {
                        boolean find = false;

                        for (GuildCharacter guildMember : player.getGuild().getMembers()) {
                           if (guildMember.getId() == player.getId()) {
                              find = true;
                              break;
                           }
                        }

                        if (!find) {
                           player.setGuildId(0);
                        }
                     }
                  } catch (Exception var43) {
                  }

                  c.getSession().writeAndFlush(CField.getCharInfo(player));

                  try {
                     c.getSession().writeAndFlush(CField.HeadTitle(player.HeadTitle()));
                  } catch (Exception var29) {
                     FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "HeadTitle 패킷 전송중 오류 발생", var29);
                  }

                  try {
                     c.getSession().writeAndFlush(LoginPacket.secureClient(11, 157, 1, "All"));
                  } catch (Exception var28) {
                     FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "secureClient 패킷 전송중 오류 발생", var28);
                  }

                  try {
                     PacketEncoder packet = new PacketEncoder();
                     GuildPacket.LoadGuildID guildID = new GuildPacket.LoadGuildID(player.getGuildId());
                     guildID.encode(packet);
                     c.getSession().writeAndFlush(packet.getPacket());
                     packet = new PacketEncoder();
                     GuildPacket.AttendanceInit init = new GuildPacket.AttendanceInit();
                     init.encode(packet);
                     c.getSession().writeAndFlush(packet.getPacket());
                  } catch (Exception var27) {
                     FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "getCharInfo 패킷 전송중 오류 발생", var27);
                  }

                  try {
                     if (transfer != null) {
                        player.giveSilentDebuff(PlayerBuffStorage.getDiseaseFromStorage(player.getId()));
                     }
                  } catch (Exception var26) {
                     FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "디버프 처리중 오류 발생", var26);
                  }

                  try {
                     int[] buddyIds = player.getBuddylist().getBuddyIds();
                     Center.Buddy.loggedOn(player.getName(), player.getId(), c.getAccID(), c.getChannel(), buddyIds);
                     if (player.getParty() != null) {
                        Party party = player.getParty();
                        Center.Party.updateParty(party.getId(), PartyOperation.LogOnOff, new PartyMemberEntry(player));
                     }

                     AccountIdChannelPair[] onlineBuddies = Center.Find.multiBuddyFind(player.getBuddylist(), buddyIds);

                     for (AccountIdChannelPair onlineBuddy : onlineBuddies) {
                        player.getBuddylist().get(onlineBuddy.getAccountId()).setChannel(onlineBuddy.getChannel());
                     }

                     player.getBuddylist().setChanged(true);
                     c.getSession().writeAndFlush(CWvsContext.BuddylistPacket.updateBuddylist(player.getBuddylist().getBuddies(), null, (byte)21));
                  } catch (Exception var42) {
                     FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "Loggedin 함수 실행중 오류발생 친구", var42);
                  }

                  try {
                     Messenger messenger = player.getMessenger();
                     if (messenger != null) {
                        Center.Messenger.silentJoinMessenger(messenger.getId(), new MessengerCharacter(player));
                        Center.Messenger.updateMessenger(messenger.getId(), player.getName(), c.getChannel());
                     }
                  } catch (Exception var25) {
                     FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "Loggedin 함수 실행중 오류발생 메신저", var25);
                  }

                  try {
                     PacketEncoder packet = new PacketEncoder();
                     GuildPacket.GuildInit guildInit = new GuildPacket.GuildInit(player.getGuild(), playerid);
                     guildInit.encode(packet);
                     c.getSession().writeAndFlush(packet.getPacket());
                     if (player.getGuildId() > 0) {
                        Guild gs = Center.Guild.getGuild(player.getGuildId());
                        if (gs != null) {
                           GuildPacket.UpdateGuildPoint ugp = new GuildPacket.UpdateGuildPoint(gs);
                           packet = new PacketEncoder();
                           ugp.encode(packet);
                           c.getPlayer().send(packet.getPacket());
                           GuildCharacter mgc = c.getPlayer().getMGC();
                           GuildPacket.AddIGPLog log = new GuildPacket.AddIGPLog(
                              gs.getId(), mgc.getId(), mgc.getGuildContribution(), mgc.getTodayContribution()
                           );
                           packet = new PacketEncoder();
                           log.encode(packet);
                           c.getPlayer().send(packet.getPacket());
                           c.getSession().writeAndFlush(GuildContents.loadGuildLog(gs));
                           c.getSession().writeAndFlush(GuildContents.loadGuildDefaultSettings());
                        } else {
                           player.setGuildId(0);
                           player.setGuildRank((byte)5);
                           player.setAllianceRank((byte)5);
                           player.saveGuildStatus();
                        }
                     }
                  } catch (Exception var24) {
                     FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "Loggedin 함수 실행중 오류발생 길드", var24);
                  }

                  try {
                     if (transfer == null) {
                        PlayerHandler.loadChattingShortCut(player);
                     }
                  } catch (Exception var23) {
                     FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "채팅 단축어 불러오기 오류 발생", var23);
                  }

                  if (!DBConfig.isGanglim) {
                     if (player.getLevel() != 1 && (player.getLevel() != 100 || !GameConstants.isZero(player.getJob()))) {
                        int reset = player.getOneInfoQuestInteger(QuestExConstants.JinQuestEx.getQuestID(), "APReset");
                        if (reset == 0) {
                           player.updateOneInfo(QuestExConstants.JinQuestEx.getQuestID(), "APReset", "1");
                           player.resetStats(4, 4, 4, 4);
                           player.dropMessage(1, "전직시 캐릭터 스탯 분배 오류로 인해 AP가 초기화되었습니다. 불편을 드려서 죄송합니다.");
                        }
                     } else {
                        player.resetStats(4, 4, 4, 4);
                        player.updateOneInfo(QuestExConstants.JinQuestEx.getQuestID(), "APReset", "1");
                     }
                  }

                  try {
                     String key = "ContinousRingTime";
                     if (player.hasEquipped(1113329) && player.getTempKeyValue(key) == null) {
                        player.setTempKeyValue(key, System.currentTimeMillis() + 120000L);
                        player.temporaryStatSet(SecondaryStatFlag.ContinousRingReady, 80003341, 120000, 1);
                     }
                  } catch (Exception var22) {
                  }

                  try {
                     HyperHandler.updateSkills(player, 0);
                     player.getStat().recalcLocalStats(player);
                     c.getSession().writeAndFlush(CField.getIntensePowerCrystalInfo(GameConstants.intensePowerCrystal));
                     c.getSession().writeAndFlush(CWvsContext.loadBlackLists(false, player.getBlackLists()));
                     if (GameConstants.isZero(player.getJob())) {
                        player.send(CField.updateWP(player.getWp()));
                        if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)-11) == null) {
                           ScriptManager.runScript(c, "zero_reinvoke_weapon", MapleLifeFactory.getNPC(2400009));
                        }
                     }

                     player.getStat().recalcLocalStats(false, player, false);
                     player.checkAfterInGame();
                     player.checkJoinNewbieGuild();
                     MapleQuest.sendModifiedQuestTime(player);
                     player.checkQuestData();
                     player.checkUpgradeSymbol();
                     player.checkJinArcaneSymbol();
                     if (DBConfig.isGanglim && player.getLevel() >= 300) {
                        for (VMatrixSlot vMatrixSlot : player.getVMatrixSlots()) {
                           vMatrixSlot.setSlotEnforcement(5);
                        }

                        player.send(CWvsContext.UpdateCore(player, true, 0, 0));
                     }

                     if (GameConstants.isLuminous(player.getJob()) && player.getOneInfoQuestInteger(1234591, "Trait") <= 0) {
                        player.getTrait(MapleTrait.MapleTraitType.will).addTrueExp(4563, player);
                        player.getTrait(MapleTrait.MapleTraitType.insight).addTrueExp(4563, player);
                        player.updateOneInfo(1234591, "Trait", "1");
                     }

                     player.checkExtraAbility();
                     player.checkLevelBuff();
                     player.checkPitchBlackBuff();
                     player.checkLiberationStats();
                     player.checkImprintedStone();
                     player.invokeJobMethod("setMemoryChoice", 0, false);
                     if (player.getOneInfoQuestInteger(1234590, "dojang_reward") > 0 && player.getOneInfoQuestInteger(1234590, "dojang_reward_get") <= 0) {
                        player.dropMessage(5, "[알림] 무릉도장 랭커 보상을 수령해주시기 바랍니다.");
                     }

                     if (player.getOneInfoQuestInteger(1234590, "dojang_reward_c") > 0 && player.getOneInfoQuestInteger(1234590, "dojang_reward_get_c") <= 0) {
                        player.dropMessage(5, "[알림] 무릉도장(챌린지) 랭커 보상을 수령해주시기 바랍니다.");
                     }

                     if (player.getOneInfoQuestLong(1234599, "praise_reward2") > 0L && player.getOneInfoQuestInteger(1234599, "praise_reward2_get") > 0) {
                        player.dropMessage(5, "[알림] 칭찬 포인트 정산 이벤트에 당첨되었습니다. 기부함 NPC를 통해 메소를 수령해주시기 바랍니다.");
                     }

                     try {
                        LinkSkill.linkSkillUpdate(player);
                     } catch (Exception var21) {
                        System.out.println("링크 스킬 업데이트 오류 발생");
                        var21.printStackTrace();
                     }

                     if (player.getLinkSkill() != null) {
                        c.getSession().writeAndFlush(CWvsContext.setLinkSkillPreset(player.getLinkSkill()));
                     }

                     if (ServerConstants.useDailyGift) {
                        MapleQuest questxx = MapleQuest.getInstance(16013);
                        MapleQuestStatus qsxx = player.getQuest(questxx);
                        if (questxx != null && qsxx.getStatus() != 2) {
                           MapleQuest.getInstance(16013).forceComplete(player, 2003);
                        }

                        c.getSession().writeAndFlush(CField.OnDailyGift((byte)0, 2, 0));
                     }

                     MapleDailyGiftInfo gift = player.getDailyGift();
                     if (gift != null) {
                        if (gift.checkDailyGift(c.getAccID())) {
                           gift.loadDailyGift(c.getAccID());
                        } else {
                           gift.InsertDailyData(c.getAccID(), 0, 0);
                           gift.loadDailyGift(c.getAccID());
                        }

                        if (!gift.getDailyData().equals(CurrentTime.getCurrentTime2()) && gift.getDailyCount() > 0) {
                           gift.setDailyCount(0);
                           gift.saveDailyGift(c.getAccID(), gift.getDailyDay(), gift.getDailyCount(), gift.getDailyData());
                        }

                        if (gift.getDailyData().equals("0")) {
                           gift.setDailyData(CurrentTime.getCurrentTime2());
                           gift.saveDailyGift(c.getAccID(), gift.getDailyDay(), gift.getDailyCount(), gift.getDailyData());
                        }

                        c.getSession()
                           .writeAndFlush(
                              CField.getDailyGiftRecord(
                                 "count=" + gift.getDailyCount() + ";day=" + gift.getDailyDay() + ";date=" + CurrentTime.getCurrentTime2()
                              )
                           );
                        String value = player.getOneInfoQuest(16700, "count");
                        if (value != null && !value.isEmpty()) {
                           if (!player.getOneInfoQuest(16700, "date").equals(CurrentTime.getCurrentTime2())) {
                              player.updateInfoQuest(16700, "count=0;day=" + gift.getDailyDay() + ";date=" + CurrentTime.getCurrentTime2());
                           } else {
                              int count = Integer.parseInt(value);
                              player.updateInfoQuest(16700, "count=" + count + ";day=" + gift.getDailyDay() + ";date=" + CurrentTime.getCurrentTime2());
                           }
                        } else {
                           player.updateInfoQuest(16700, "count=0;day=0;date=" + CurrentTime.getCurrentTime2());
                        }
                     }

                     if (channelServer.getPlayerStorage().getCharacterById(player.getId()) == null) {
                        c.getSession().writeAndFlush(CWvsContext.serverNotice(1, "알 수 없는 오류가 발생하였습니다.\r\n모든 아이템 거래, 이동이 중지되며 경매장이용이 불가능합니다.\r\n재접속 부탁드립니다."));
                        return;
                     }

                     if (player.getOneInfoQuestInteger(1234555, "beginner_package") == 1) {
                        player.dropMessage(5, "초심자 패키지 지급이 완료되었습니다. 상점 시스템에서 수령해주시기 바랍니다.");
                        player.send(CField.addPopupSay(9062000, 3000, "초심자 패키지 지급이 완료되었습니다. 상점 시스템에서 수령해주시기 바랍니다.", ""));
                        player.updateOneInfo(1234555, "beginner_package", "0");
                     }

                     if (ServerConstants.expFeverRate != 1.0) {
                        player.send(CField.chatMsg(3, "[피버이벤트] 경험치 " + ServerConstants.expFeverRate + "배 이벤트가 진행중입니다."));
                     }

                     if (ServerConstants.dropFeverRate != 1.0) {
                        player.send(CField.chatMsg(3, "[피버이벤트] 드롭 " + ServerConstants.dropFeverRate + "배 이벤트가 진행중입니다."));
                     }

                     if (ServerConstants.mesoFeverRate != 1.0) {
                        player.send(CField.chatMsg(3, "[피버이벤트] 메소 " + ServerConstants.mesoFeverRate + "배 이벤트가 진행중입니다."));
                     }

                     if (ServerConstants.dailyEventType != null) {
                        if (DBConfig.isGanglim) {
                           TextEffect e = null;
                           String str = null;
                           switch (ServerConstants.dailyEventType) {
                              case ExpRateFever_:
                                 e = new TextEffect(-1, "[주말의 강림 버닝] 경험치 추가 획득\r\n레벨 범위 몬스터 사냥 시 경험치 100% 추가 획득!!", 50, 5000, 4, 0);
                                 str = "[주말의 강림 버닝] '경험치 100% 추가 획득' 이벤트가 진행됩니다. 오른쪽 경험치 획득 로그에는 표기되지 않지만, 실제로는 적용중입니다.";
                                 break;
                              case DropRateFever:
                                 e = new TextEffect(-1, "[월요일의 강림 버닝] 아이템 획득 확률 증가\r\n사냥 시 아이템 획득 확률 50% 증가!!", 50, 5000, 4, 0);
                                 str = "[월요일의 강림 버닝] '아이템 획득 확률 50% 증가' 이벤트가 진행됩니다.";
                                 break;
                              case StarForceDiscount:
                                 e = new TextEffect(-1, "[화/목요일의 강림 버닝] 스타포스 강화 비용 할인\r\n스타포스 강화 비용 30% 할인 적용!!", 50, 5000, 4, 0);
                                 str = "[화/목요일의 강림 버닝] '스타포스 강화 비용 할인' 이벤트가 진행됩니다.";
                                 break;
                              case ExpRateFever:
                                 e = new TextEffect(-1, "[수요일의 강림 버닝] 경험치 추가 획득\r\n레벨 범위 몬스터 사냥 시 경험치 50% 추가 획득!!", 50, 5000, 4, 0);
                                 str = "[수요일의 강림 버닝] '경험치 50% 추가 획득' 이벤트가 진행됩니다.";
                                 break;
                              case MesoRateFever:
                                 e = new TextEffect(-1, "[금요일의 강림 버닝] 메소 추가 획득\r\n사냥 시 메소 100% 추가 획득!!", 50, 5000, 4, 0);
                                 str = "[금요일의 강림 버닝] '메소 100% 추가 획득' 이벤트가 진행됩니다.";
                           }

                           if (e != null) {
                           }

                           if (str != null) {
                           }
                        } else {
                           if (ServerConstants.dailyEventType == DailyEventType.ExpRateFever) {
                              TextEffect e = new TextEffect(-1, "[주말의 데일리] 경험치 추가 획득\r\n레벨 범위 몬스터 사냥 시 경험치 20% 추가 획득!", 50, 5000, 4, 0);
                              player.send(e.encodeForLocal());
                              player.send(CField.chatMsg(3, "[주말의 데일리] 경험치 20% 추가 획득 이벤트가 진행됩니다."));
                           }

                           if (ServerConstants.dailyEventType == DailyEventType.MesoRateFever) {
                              TextEffect e = new TextEffect(-1, "[월요일 데일리] 메소 추가 획득\r\n사냥 시 메소 20% 추가 획득!", 50, 5000, 4, 0);
                              player.send(e.encodeForLocal());
                              player.send(CField.chatMsg(3, "[월요일 데일리] 메소 20% 추가 획득 이벤트가 진행됩니다."));
                           }

                           if (ServerConstants.dailyEventType == DailyEventType.DropRateFever) {
                              TextEffect e = new TextEffect(-1, "[화요일 데일리] 아이템 획득 확률 증가\r\n사냥 시 아이템 획득 확률 20% 증가!", 50, 5000, 4, 0);
                              player.send(e.encodeForLocal());
                              player.send(CField.chatMsg(3, "[화요일 데일리] 아이템 획득 확률 20% 증가 이벤트가 진행됩니다."));
                           }

                           if (ServerConstants.dailyEventType == DailyEventType.MobGenFever) {
                              TextEffect e = new TextEffect(-1, "[수요일 데일리] 몬스터 젠량 증가\r\n몬스터 젠량 20% 증가!", 50, 5000, 4, 0);
                              player.send(e.encodeForLocal());
                              player.send(CField.chatMsg(3, "[수요일 데일리] 몬스터 젠량 증가 이벤트가 진행됩니다."));
                           }

                           if (ServerConstants.dailyEventType == DailyEventType.StarForceDiscount) {
                              TextEffect e = new TextEffect(-1, "[목요일 데일리] 스타포스 강화 비용 할인\r\n스타포스 강화 비용이 30% 할인됩니다!", 50, 5000, 4, 0);
                              player.send(e.encodeForLocal());
                              player.send(CField.chatMsg(3, "[목요일 데일리] 스타포스 강화 비용 할인 이벤트가 진행됩니다."));
                           }

                           if (ServerConstants.dailyEventType == DailyEventType.CubeFever) {
                              TextEffect e = new TextEffect(-1, "[금요일 데일리] 몬스터 컬렉션 신규 몬스터 등록 확률 20% 증가\r\n몬스터 컬렉션 신규 몬스터 등록 확률 증가!", 50, 5000, 4, 0);
                              player.send(e.encodeForLocal());
                              player.send(CField.chatMsg(3, "[금요일 데일리] 몬스터 컬렉션 신규 몬스터 등록 확률 증가 이벤트가 진행됩니다."));
                           }
                        }
                     }

                     player.setLastHeartBeatTime(System.currentTimeMillis());
                     if (player.getSkillLevel(92040000) > 0 && DBConfig.isGanglim) {
                        player.changeSkillLevel(92040000, 1, 13);
                     }

                     if (DBConfig.isGanglim) {
                     }

                     player.getMap().addPlayer(player);
                     c.getSession().writeAndFlush(CWvsContext.updateMaplePoint(player.getMaplePoints()));
                     if (transfer == null) {
                        c.getSession().writeAndFlush(CField.getKeymap(player.getKeyLayout(), player.getQuickSlotKeyMapped()));
                     }

                     SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                     player.updateOneInfo(16413, "lastSyncDate", sdf.format(new Date()));
                     Center.RemainBuffStorage.processSetBuff(player);
                     player.send(CWvsContext.bossClearCheck());
                     if (Center.sunShineStorage.bloomFlower) {
                        sdf = new SimpleDateFormat("MM월 dd일 HH시 mm분 까지");
                        String endTime = sdf.format(Center.sunShineStorage.endTime);
                        TextEffect e = new TextEffect(-1, "광장에 꽃이 피어 이벤트가 진행중입니다.", 100, 1000, 4, 0);
                        player.send(e.encodeForLocal());
                        if (Center.sunShineStorage.randomBuff == 1) {
                           player.dropMessage(5, "광장에 꽃이피어 [경험치 1.5배][아케인 심볼 드롭률 1.5배] 이벤트가 " + endTime + " 적용됩니다!");
                        } else if (Center.sunShineStorage.randomBuff == 2) {
                           player.dropMessage(5, "광장에 꽃이피어 [경험치 1.5배][드롭률 1.5배] 이벤트가 " + endTime + " 적용됩니다!");
                        } else if (Center.sunShineStorage.randomBuff == 3) {
                           player.dropMessage(5, "광장에 꽃이피어 [경험치 1.5배][메소 획득량 1.5배] 이벤트가 " + endTime + " 적용됩니다!");
                        } else if (Center.sunShineStorage.randomBuff == 4) {
                           player.dropMessage(5, "광장에 꽃이피어 [아케인 심볼 드롭률 1.5배][메소 획득량 1.5배] 이벤트가 " + endTime + " 적용됩니다!");
                        } else if (Center.sunShineStorage.randomBuff == 5) {
                           player.dropMessage(5, "광장에 꽃이피어 [드롭률 1.5배][메소 획득량 1.5배] 이벤트가 " + endTime + " 적용됩니다!");
                        }
                     }
                  } catch (Exception var41) {
                     FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "LoggedIn 처리중 오류발생", var41);
                  }

                  try {
                     CharacterNameAndId pendingBuddyRequest = player.getBuddylist().pollPendingRequest();
                     if (pendingBuddyRequest != null) {
                        player.getBuddylist()
                           .put(
                              new FriendEntry(
                                 pendingBuddyRequest.getName(),
                                 pendingBuddyRequest.getAccId(),
                                 pendingBuddyRequest.getId(),
                                 pendingBuddyRequest.getGroupName(),
                                 -1,
                                 false,
                                 pendingBuddyRequest.getLevel(),
                                 pendingBuddyRequest.getJob(),
                                 pendingBuddyRequest.getMemo()
                              )
                           );
                        c.getSession()
                           .writeAndFlush(
                              CWvsContext.BuddylistPacket.requestBuddylistAdd(
                                 pendingBuddyRequest.getId(),
                                 pendingBuddyRequest.getAccId(),
                                 pendingBuddyRequest.getName(),
                                 pendingBuddyRequest.getLevel(),
                                 pendingBuddyRequest.getJob(),
                                 c,
                                 pendingBuddyRequest.getGroupName(),
                                 pendingBuddyRequest.getMemo()
                              )
                           );
                     }

                     player.getClient().getSession().writeAndFlush(CWvsContext.serverMessage(channelServer.getServerMessage()));
                  } catch (Exception var20) {
                     FileoutputUtil.outputFileErrorReason("Log_GameServerLogIn_Error.rtf", "Loggedin 함수 실행중 오류발생 친구접속알림", var20);
                  }

                  try {
                     double rate = ServerConstants.connectedRate;
                     int count = 0;

                     for (GameServer gameServer : GameServer.getAllInstances()) {
                        int size = (int)(gameServer.getPlayerCountInChannel() * rate);
                        count += size;
                     }

                     int csCount = 0;

                     for (MapleCharacter p : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
                        if (p != null) {
                           csCount++;
                        }
                     }

                     csCount = (int)(csCount * rate);
                     count += csCount;
                     int auctionCount = 0;

                     for (MapleCharacter px : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
                        if (px != null) {
                           auctionCount++;
                        }
                     }

                     auctionCount = (int)(auctionCount * rate);
                     count += auctionCount;
                     player.sendMacros();
                     player.showNote();
                     player.updatePartyMemberHP();
                     player.DailyFairyPendantReset(GameConstants.FairyQuestEx);
                     if (transfer == null) {
                        player.startFairySchedule(true);
                     }

                     player.updateMatrixSkillsNoLock();
                     MapleCabinet cabinet = player.getCabinet();
                     if (cabinet != null) {
                        player.checkExpiredCabinetItem();
                        if (cabinet.checkAlert()) {
                           player.send(CField.maplecabinetResult(8));
                        }

                        if (player.isUnequipShield()) {
                           player.dropMessage(5, "[알림] 두손무기에 방패가 장착되어 방패의 장착이 해제되었습니다. [메이플 보관함]에서 수령해주시기 바랍니다.");
                        }

                        if (player.isUnequipPants()) {
                           player.dropMessage(5, "[알림] 한벌옷과 하의가 함께 장착되어 하의 장착이 해제되었습니다. [메이플 보관함]에서 수령해주시기 바랍니다.");
                        }
                     }

                     c.getSession().writeAndFlush(CField.setBRMOnServerCalcResult(true));
                     player.setStackEventGauge();
                     player.updatePetAuto();
                     player.checkDailyQuest();
                     player.checkGenesisWeapon();
                     if (DBConfig.isGanglim) {
                        for (int ix = 501525; ix <= 501553L; ix++) {
                           if (player.getQuestStatus(ix) == 2
                              && player.getOneInfoQuestInteger(ix, "value") == 1
                              && ix - 501525 + 1 == player.getOneInfoQuestInteger(501524, "step")) {
                              player.updateQuest(new MapleQuestStatus(MapleQuest.getInstance(ix), 1));
                              break;
                           }
                        }
                     }

                     player.sendUnionPacket();
                     player.send(CWvsContext.initSecurity());
                     player.send(CWvsContext.updateSecurity());
                     if (DBConfig.isGanglim) {
                        player.updateDonationPoint();
                     }

                     player.send(CField.getInternetCafe((byte)1, 0));
                     player.onMagicBell();
                     player.checkExtremeDonation();
                     player.updatePetSkills();

                     for (int ixx = 0; ixx < player.getMapleUnionPreset().length; ixx++) {
                        MapleUnion union = player.getMapleUnionPreset()[ixx];
                        if (union != null) {
                           StringBuilder matrix = new StringBuilder();

                           try (Connection con = DBConnection.getConnection()) {
                              if (union.changeableGroup.isEmpty()) {
                                 player.loadMapleUnionPreset(union, con, ixx);
                              }
                           }

                           for (int g = 0; g < union.changeableGroup.size(); g++) {
                              matrix.append(g).append("=").append(union.changeableGroup.get(g)).append(";");
                           }

                           player.updateInfoQuest(18790 + ixx + 1, matrix.toString());
                        }
                     }

                     if (player.getMapleUnion() != null) {
                        MapleUnion union = player.getMapleUnion();
                        StringBuilder matrix = new StringBuilder();

                        for (int g = 0; g < union.changeableGroup.size(); g++) {
                           matrix.append(g).append("=").append(union.changeableGroup.get(g)).append(";");
                        }

                        player.updateInfoQuest(500627, matrix.toString());
                     }

                     if (player.haveItem(2000047)) {
                        player.removeItem(2000047, -50);
                     }

                     player.expirationTask(true, transfer == null);
                     if (!DBConfig.isGanglim && ServerConstants.JuhunFever == 1) {
                        player.send(CWvsContext.scrollUpgradeFeverTime(2));
                     }

                     if (GameConstants.isBlaster(player.getJob())) {
                        player.invokeJobMethod("setRWCylinder", 37000010, -1, -1, -1);
                     }

                     if (player.getJob() >= 3300 && player.getJob() <= 3312) {
                        if (player.getTotalSkillLevel(30001062) > 0) {
                           player.changeSkillLevel(30001062, 1, 1);
                        }

                        player.onJaguarLinkPassive();
                     }

                     if (GameConstants.isZero(player.getJob()) && player.getSkillLevel(100000280) <= 1) {
                        player.changeSkillLevel(100000280, 5, 5);
                     }

                     if (GameConstants.isXenon(player.getJob())) {
                        if (player.getTotalSkillLevel(30020234) == 0) {
                           player.changeSkillLevel(30020234, 1, 1);
                        }

                        if (player.getTotalSkillLevel(30021236) == 0) {
                           player.changeSkillLevel(30021236, 1, 1);
                        }

                        if (player.getTotalSkillLevel(30021237) == 0) {
                           player.changeSkillLevel(30021237, 1, 1);
                        }
                     }

                     if (GameConstants.isDemonAvenger(player.getJob())) {
                        if (player.getTotalSkillLevel(30010185) == 0) {
                           player.changeSkillLevel(30010185, 1, 1);
                        }

                        if (player.getTotalSkillLevel(30010232) == 0) {
                           player.changeSkillLevel(30010232, 1, 1);
                        }

                        if (player.getTotalSkillLevel(30010230) == 0) {
                           player.changeSkillLevel(30010230, 1, 1);
                        }

                        if (player.getTotalSkillLevel(30010242) == 0) {
                           player.changeSkillLevel(30010242, 1, 1);
                        }
                     }

                     if (GameConstants.isHoyoung(player.getJob()) && player.getTotalSkillLevel(160000076) == 0) {
                        player.changeSkillLevel(160000076, 10, 10);
                     }

                     if (GameConstants.isAdele(player.getJob())) {
                        if (player.getTotalSkillLevel(150020079) == 0) {
                           player.changeSkillLevel(150020079, 1, 1);
                        }

                        if (player.getLevel() >= 200 && player.getTotalSkillLevel(150020006) == 0) {
                           player.changeSkillLevel(150020006, 10, 10);
                        }

                        if (player.getTotalSkillLevel(151001004) == 0) {
                           player.changeSkillLevel(151001004, 1, 1);
                        }
                     }

                     if (GameConstants.isCannon(player.getJob())) {
                        player.getSkills().keySet().stream().collect(Collectors.toList()).forEach(s -> {
                           if (s.getId() / 10000 == 500) {
                              player.changeSkillLevel(s.getId(), 0, 0);
                           }
                        });
                     }

                     if ((GameConstants.isAngelicBuster(player.getJob()) || GameConstants.isArk(player.getJob()) || GameConstants.isEunWol(player.getJob()))
                        && player.getTotalSkillLevel(400051000) > 0) {
                        player.changeSkillLevel(400051001, player.getTotalSkillLevel(400051000), 30);
                     }

                     if (GameConstants.isKaiser(player.getJob())) {
                        if (player.getTotalSkillLevel(60000219) == 0) {
                           player.changeSkillLevel(60000219, 1, 1);
                        }

                        if (player.getTotalSkillLevel(60001216) == 0) {
                           player.changeSkillLevel(60001216, 1, 1);
                        }

                        if (player.getTotalSkillLevel(60001217) == 0) {
                           player.changeSkillLevel(60001217, 1, 1);
                        }

                        if (player.getTotalSkillLevel(60001218) == 0) {
                           player.changeSkillLevel(60001218, 1, 1);
                        }
                     }

                     if (GameConstants.isDemonSlayer(player.getJob()) && player.getTotalSkillLevel(30010111) == 0) {
                        player.changeSkillLevel(30010111, 1, 1);
                     }

                     if (GameConstants.isCygnus(player.getJob()) && player.getTotalSkillLevel(10001075) == 0) {
                        player.changeSkillLevel(10001075, 1, 1);
                     }

                     if (GameConstants.isMichael(player.getJob()) && player.getTotalSkillLevel(50001075) == 0) {
                        player.changeSkillLevel(50001075, 1, 1);
                     }

                     if (player.getJob() >= 1112 && player.getJob() <= 1512 || player.getJob() == 5112) {
                        if (player.getJob() == 1112 && player.getTotalSkillLevel(11121000) == 0) {
                           player.changeSkillLevel(11121000, 30, 30);
                        }

                        if (player.getJob() == 1212 && player.getTotalSkillLevel(12121000) == 0) {
                           player.changeSkillLevel(12121000, 30, 30);
                        }

                        if (player.getJob() == 1312 && player.getTotalSkillLevel(13121000) == 0) {
                           player.changeSkillLevel(13121000, 30, 30);
                        }

                        if (player.getJob() == 1412 && player.getTotalSkillLevel(14121000) == 0) {
                           player.changeSkillLevel(14121000, 30, 30);
                        }

                        if (player.getJob() == 1512 && player.getTotalSkillLevel(15121000) == 0) {
                           player.changeSkillLevel(15121000, 30, 30);
                        }

                        if (player.getJob() == 5112 && player.getTotalSkillLevel(51121005) == 0) {
                           player.changeSkillLevel(51121005, 30, 30);
                        }
                     }

                     if (transfer == null) {
                        player.giveCoolDowns(null);
                     }

                     c.sendPing();
                     if (DBConfig.isGanglim && !player.hasBuffBySkillID(80003016)) {
                     }

                     if ((player.getJob() == 301 || player.getJob() >= 330 && player.getJob() <= 332) && player.getSkillLevel(3001007) > 0) {
                        player.changeSkillLevel(3001007, 0, 0);
                     }

                     if (player.getJob() == 2412) {
                        if (player.getSkillLevel(20031210) == 0) {
                           player.changeSkillLevel(20031210, 1, 1);
                        }

                        if (player.getSkillLevel(20031209) > 0) {
                           player.changeSkillLevel(20031209, 0, 0);
                        }
                     }

                     player.invokeJobMethod("updateLarkness", true);
                     if (player.getCooldownLimit(80002282) != 0L) {
                        player.temporaryStatSet(80002282, (int)(900000L - player.getCooldownLimit(80002282)), SecondaryStatFlag.RuneBlocked, 1);
                     }

                     if (player.getCooldownLimit(2310013) != 0L) {
                        SecondaryStatEffect effect = player.getSkillLevelData(2311009);
                        if (effect != null) {
                           player.temporaryStatSet(
                              2310013, (int)(effect.getY() * 1000 - player.getCooldownLimit(2310013)), SecondaryStatFlag.HolyMagicShellBlocked, 1
                           );
                        }
                     }

                     DamageMeasurementRank.applyDamageRankBuff(player);
                  } catch (Exception var40) {
                     System.out.println("[오류] Loggedin 함수 실행중 오류발생_3 : " + var40.toString());
                     var40.printStackTrace();
                  }

                  try {
                     if (ServerConstants.useAdminClient) {
                        AdminClient.updatePlayerList();
                     }
                  } catch (Exception var18) {
                     System.out.println("[오류] updatePlayerList 함수 실행중 오류 발생 : " + var18.toString());
                     var18.printStackTrace();
                  }

                  player.checkSoulSkillLevel();
                  if (!DBConfig.isGanglim && ServerConstants.JuhunFever == 1) {
                     player.send(CWvsContext.scrollUpgradeFeverTime(2));
                  }

                  if (DBConfig.isGanglim && player.getInnerSkills().size() != 3) {
                     List<CharacterPotentialHolder> newValues = new LinkedList<>();
                     int level = Math.min(3, player.getInnerSkills().size());
                     int rank = 0;

                     for (int ixxx = 0; ixxx < 3; ixxx++) {
                        if (ixxx < level) {
                           newValues.add(player.getInnerSkills().get(ixxx));
                        } else {
                           newValues.add(CharacterPotential.getInstance().renewSkill(player.getInnerSkills(), newValues, rank, false, true));
                        }
                     }

                     player.getInnerSkills().clear();

                     for (CharacterPotentialHolder isvh : newValues) {
                        player.getInnerSkills().add(isvh);
                     }

                     player.setSaveFlag(player.getSaveFlag() | CharacterSaveFlag.INNER_SKILL.getFlag());
                     List var173 = player.getInnerSkills();
                  }

                  if (!DBConfig.isGanglim) {
                     for (CharacterPotentialHolder holder : new ArrayList<>(player.getInnerSkills())) {
                        int count = 0;

                        for (CharacterPotentialHolder h : new ArrayList<>(player.getInnerSkills())) {
                           if (holder.getSkillId() == h.getSkillId()) {
                              count++;
                           }
                        }

                        if (count > 1) {
                           List<CharacterPotentialHolder> newValues = new LinkedList<>();

                           for (int next = 0; next < player.getInnerLevel(); next++) {
                              int rank = 0;
                              if (next == 0) {
                                 newValues.add(player.getInnerSkills().get(next));
                              } else {
                                 newValues.add(CharacterPotential.getInstance().renewSkill(player.getInnerSkills(), newValues, rank, false, true));
                              }
                           }

                           player.getInnerSkills().clear();

                           for (CharacterPotentialHolder isvh : newValues) {
                              player.getInnerSkills().add(isvh);
                           }

                           player.setSaveFlag(player.getSaveFlag() | CharacterSaveFlag.INNER_SKILL.getFlag());
                           List<CharacterPotentialHolder> skills = player.getInnerSkills();

                           for (byte ixxxx = 0; ixxxx < skills.size(); ixxxx++) {
                              c.getSession()
                                 .writeAndFlush(
                                    CField.updateInnerPotential(
                                       (byte)(ixxxx + 1), skills.get(ixxxx).getSkillId(), skills.get(ixxxx).getSkillLevel(), skills.get(ixxxx).getRank()
                                    )
                                 );
                           }

                           player.getStat().recalcLocalStats(player);
                           player.gainHonor(100000, true);
                           if (!DBConfig.isGanglim) {
                              player.dropMessage(5, "[알림] 어빌리티 중복 옵션이 발견되어 첫번째 줄을 제외한 옵션이 초기화됩니다.");
                           }
                        }
                     }

                     if (player.getKeyValue2("check_inner") != 1) {
                        List<CharacterPotentialHolder> newValues = new LinkedList<>();
                        if (!player.getInnerSkills().isEmpty()) {
                           for (int nextx = 0; nextx < player.getInnerLevel(); nextx++) {
                              int rank = 0;
                              if (nextx == 0) {
                                 newValues.add(player.getInnerSkills().get(nextx));
                              } else {
                                 newValues.add(CharacterPotential.getInstance().renewSkill(player.getInnerSkills(), newValues, rank, false, true));
                              }
                           }

                           player.getInnerSkills().clear();

                           for (CharacterPotentialHolder isvh : newValues) {
                              player.getInnerSkills().add(isvh);
                           }

                           player.setSaveFlag(player.getSaveFlag() | CharacterSaveFlag.INNER_SKILL.getFlag());
                           List<CharacterPotentialHolder> skills = player.getInnerSkills();

                           for (byte ixxxx = 0; ixxxx < skills.size(); ixxxx++) {
                              c.getSession()
                                 .writeAndFlush(
                                    CField.updateInnerPotential(
                                       (byte)(ixxxx + 1), skills.get(ixxxx).getSkillId(), skills.get(ixxxx).getSkillLevel(), skills.get(ixxxx).getRank()
                                    )
                                 );
                           }

                           player.getStat().recalcLocalStats(player);
                           player.gainHonor(100000, true);
                           player.dropMessage(5, "[알림] 어빌리티 버그 수정으로 인하여 첫번째줄을 제외한 옵션이 초기화됩니다.");
                        }

                        player.setKeyValue2("check_inner", 1);
                     }
                  }

                  player.checkAngelicBless();

                  for (int medalID : ServerConstants.donatorMedalItemID) {
                     player.checkDonatorMedalBuff(medalID);
                  }

                  for (int index = 0; index < ServerConstants.guildMedalList.length; index++) {
                     String gName = ServerConstants.guildMedalList[index];
                     int medalID = ServerConstants.guildMedalItemID[index];
                     if ((player.getGuild() == null || !player.getGuild().getName().equals(gName)) && player.haveItem(medalID)) {
                        player.removeAll(medalID, false);
                        player.dropMessage(5, MapleItemInformationProvider.getInstance().getName(medalID) + " 자격이 박탈되어, 칭호가 회수되었습니다.");
                     }

                     if (player.getGuild() != null && player.getGuild().getName().equals(gName) && !player.haveItem(medalID, 1, true, true)) {
                        player.gainItemAllStat(medalID, (short)1, (short)200, (short)100);
                        player.dropMessage(5, MapleItemInformationProvider.getInstance().getName(medalID) + " 훈장이 지급 되었습니다.");
                     }
                  }

                  player.checkDead();
                  if (DBConfig.isGanglim
                     && player.getEventInstance() == null
                     && player.getMapId() != ServerConstants.StartMap
                     && player.getMapId() != ServerConstants.TownMap
                     && player.getOneInfoQuestInteger(1234567, "offTownButton") == 0) {
                  }

                  if (DBConfig.isGanglim && MailBox.isItemOff(player)) {
                     if (c.isOverseasUser()) {
                        player.dropMessage(-22, "[LetterBox] The Letter has arrived. Please pick it up in Event NPC - LetterBox");
                     } else {
                        player.dropMessage(-22, "[우편함] 우편함에 아이템이 도착하였습니다. 이벤트 NPC - 우편함에서 수령해 주세요 !");
                     }
                  }
               } catch (Exception var54) {
                  System.out.println("Loggedin 함수 실행중 오류발생_2 : " + var54.toString());
                  var54.printStackTrace();
               }
            },
            10L
         );
   }

   public static final void ChangeChannel(PacketDecoder slea, MapleClient c, MapleCharacter chr, boolean room) {
      try {
         if ((
               chr == null
                  || chr.getEventInstance() != null
                  || chr.getMap() == null
                  || chr.isInBlockedMap()
                  || FieldLimitType.ChannelSwitch.check(chr.getMap().getFieldLimit())
            )
            && (chr == null || chr.getMapId() != ServerConstants.TownMap)) {
            c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
            return;
         }

         if (Center.getPendingCharacterSize() >= 10) {
            chr.dropMessage(1, "The server is busy at the moment. Please try again in a less than a minute.");
            c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
            return;
         }

         if (!chr.isGM() && System.currentTimeMillis() - chr.getLastChangedChannelTime() < 5000L) {
            if (DBConfig.isGanglim) {
               chr.dropMessage(5, "채널 변경은 5초에 한 번 변경할 수 있습니다.");
            } else {
               chr.dropMessage(5, "지금은 채널변경이 불가능합니다. 잠시 후 다시 시도해주시기 바랍니다.");
            }

            c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
            return;
         }

         int chc = slea.readByte() + 1;
         int mapid = 0;
         if (room) {
            mapid = slea.readInt();
         }

         slea.readInt();
         if (!Center.isChannelAvailable(chc)) {
            chr.dropMessage(1, "The channel is full at the moment.");
            c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
            return;
         }

         if (room && (mapid < 910000001 || mapid > 910000022)) {
            chr.dropMessage(1, "The channel is full at the moment.");
            c.getSession().writeAndFlush(CWvsContext.enableActions(chr));
            return;
         }

         chr.changeChannel(chc);
      } catch (Exception var6) {
         System.out.println("[오류] 채널 변경 시도 중 오류 발생 (Name : " + chr.getName() + ") " + var6.toString());
         var6.printStackTrace();
      }
   }

   public static void checkAmazingScrollOverCHUC(MapleCharacter player) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      MapleInventory[] invList = new MapleInventory[]{player.getInventory(MapleInventoryType.EQUIP), player.getInventory(MapleInventoryType.EQUIPPED)};

      for (MapleInventory inv : invList) {
         if (inv != null) {
            for (Item item : inv) {
               if (item instanceof Equip) {
                  Equip equip = (Equip)item;
                  Equip zeroEquip = null;
                  if (GameConstants.isZeroWeapon(equip.getItemId())) {
                     zeroEquip = (Equip)player.getInventory(MapleInventoryType.EQUIPPED).getItem((short)(equip.getPosition() == -11 ? -10 : -11));
                  }

                  if (equip.isAmazingHyperUpgradeUsed() && equip.getCHUC() <= 15) {
                     while (equip.getCHUC() > 12) {
                        int reqLev = ii.getReqLevel(equip.getItemId());
                        int realLevel = reqLev / 10 * 10;
                        int[] data;
                        switch (realLevel) {
                           case 80:
                              data = new int[]{2, 3, 5, 8, 12, 2, 3, 4, 5, 6, 7, 9, 10, 11};
                              break;
                           case 90:
                              data = new int[]{4, 5, 7, 10, 14, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13};
                              break;
                           case 100:
                              data = new int[]{7, 8, 10, 13, 17, 4, 5, 6, 7, 8, 9, 11, 12, 13, 14};
                              break;
                           case 110:
                              data = new int[]{9, 10, 12, 15, 19, 5, 6, 7, 8, 9, 10, 12, 13, 14, 15};
                              break;
                           case 120:
                              data = new int[]{12, 13, 15, 18, 22, 6, 7, 8, 9, 10, 11, 13, 14, 15, 16};
                              break;
                           case 130:
                              data = new int[]{14, 15, 17, 20, 24, 7, 8, 9, 10, 11, 12, 14, 15, 16, 17};
                              break;
                           case 140:
                              data = new int[]{17, 18, 20, 23, 27, 8, 9, 10, 11, 12, 13, 15, 16, 17, 18};
                              break;
                           case 150:
                              data = new int[]{19, 20, 22, 25, 29, 9, 10, 11, 12, 13, 14, 16, 17, 18, 19};
                              break;
                           default:
                              data = new int[]{1, 2, 4, 7, 11, 1, 2, 3, 4, 5, 6, 8, 9, 10, 11};
                        }

                        int ordinary;
                        if (GameConstants.isMagicWeapon(equip.getItemId())) {
                           ordinary = equip.getMatk();
                        } else {
                           ordinary = equip.getWatk();
                        }

                        int weaponwatk = ordinary / 50 + 1;
                        int weaponmatk = ordinary / 50 + 1;
                        equip.setCHUC((byte)(equip.getCHUC() - 1));
                        if (equip.getCHUC() < 5) {
                           equip.setStr((short)(equip.getStr() - data[equip.getCHUC()]));
                           equip.addDex((short)(equip.getDex() - data[equip.getCHUC()]));
                           equip.addInt((short)(equip.getInt() - data[equip.getCHUC()]));
                           equip.addLuk((short)(equip.getLuk() - data[equip.getCHUC()]));
                           if (zeroEquip != null) {
                              zeroEquip.addStr((short)(zeroEquip.getStr() - data[equip.getCHUC()]));
                              zeroEquip.addDex((short)(zeroEquip.getDex() - data[equip.getCHUC()]));
                              zeroEquip.addInt((short)(zeroEquip.getInt() - data[equip.getCHUC()]));
                              zeroEquip.addLuk((short)(zeroEquip.getLuk() - data[equip.getCHUC()]));
                           }
                        } else {
                           equip.addWatk((short)(equip.getWatk() - data[equip.getCHUC()]));
                           equip.addMatk((short)(equip.getMatk() - data[equip.getCHUC()]));
                           if (zeroEquip != null) {
                              zeroEquip.addWatk((short)(zeroEquip.getWatk() - data[equip.getCHUC()]));
                              zeroEquip.addMatk((short)(zeroEquip.getMatk() - data[equip.getCHUC()]));
                           }
                        }

                        if (GameConstants.isWeapon(equip.getItemId())) {
                           equip.addWatk((short)(equip.getStr() - weaponwatk));
                           equip.addMatk((short)(equip.getStr() - weaponmatk));
                           if (Randomizer.nextBoolean()) {
                              equip.addWatk((short)(equip.getWatk() - 1));
                              equip.addMatk((short)(equip.getMatk() - 1));
                              if (zeroEquip != null) {
                                 zeroEquip.addWatk((short)(equip.getWatk() - 1));
                                 zeroEquip.addMatk((short)(equip.getMatk() - 1));
                              }
                           }
                        } else if (GameConstants.isAccessory(equip.getItemId()) && Randomizer.nextBoolean()) {
                           if (reqLev < 120) {
                              if (equip.getCHUC() < 5) {
                                 equip.addStr((short)(equip.getStr() - 1));
                                 equip.addDex((short)(equip.getDex() - 1));
                                 equip.addInt((short)(equip.getInt() - 1));
                                 equip.addLuk((short)(equip.getLuk() - 1));
                                 if (zeroEquip != null) {
                                    zeroEquip.addStr((short)(equip.getStr() - 1));
                                    zeroEquip.addDex((short)(equip.getDex() - 1));
                                    zeroEquip.addInt((short)(equip.getInt() - 1));
                                    zeroEquip.addLuk((short)(equip.getLuk() - 1));
                                 }
                              } else {
                                 equip.addStr((short)(equip.getStr() - 2));
                                 equip.addDex((short)(equip.getDex() - 2));
                                 equip.addInt((short)(equip.getInt() - 2));
                                 equip.addLuk((short)(equip.getLuk() - 2));
                                 if (zeroEquip != null) {
                                    zeroEquip.addStr((short)(equip.getStr() - 2));
                                    zeroEquip.addDex((short)(equip.getDex() - 2));
                                    zeroEquip.addInt((short)(equip.getInt() - 2));
                                    zeroEquip.addLuk((short)(equip.getLuk() - 2));
                                 }
                              }
                           } else if (equip.getCHUC() < 5) {
                              int rand = Randomizer.rand(1, 2);
                              int rand2 = Randomizer.rand(1, 2);
                              int rand3 = Randomizer.rand(1, 2);
                              int rand4 = Randomizer.rand(1, 2);
                              equip.addStr((short)(equip.getStr() - rand));
                              equip.addDex((short)(equip.getDex() - rand2));
                              equip.addInt((short)(equip.getInt() - rand3));
                              equip.addLuk((short)(equip.getLuk() - rand4));
                              if (zeroEquip != null) {
                                 zeroEquip.addStr((short)(equip.getStr() - rand));
                                 zeroEquip.addDex((short)(equip.getDex() - rand2));
                                 zeroEquip.addInt((short)(equip.getInt() - rand3));
                                 zeroEquip.addLuk((short)(equip.getLuk() - rand4));
                              }
                           } else {
                              equip.addStr((short)(equip.getStr() - 2));
                              equip.addDex((short)(equip.getDex() - 2));
                              equip.addInt((short)(equip.getInt() - 2));
                              equip.addLuk((short)(equip.getLuk() - 2));
                              if (zeroEquip != null) {
                                 zeroEquip.addStr((short)(equip.getStr() - 2));
                                 zeroEquip.addDex((short)(equip.getDex() - 2));
                                 zeroEquip.addInt((short)(equip.getInt() - 2));
                                 zeroEquip.addLuk((short)(equip.getLuk() - 2));
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
