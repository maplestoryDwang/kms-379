package network.game.processors;

import commands.CommandProcessor;
import constants.QuestExConstants;
import constants.ServerConstants;
import constants.devtempConstants.DummyCharacterName;
import database.DBConfig;
import java.util.List;
import network.RecvPacketOpcode;
import network.center.Center;
import network.decode.PacketDecoder;
import network.discordbot.DiscordBotHandler;
import network.game.GameServer;
import network.game.processors.inventory.InventoryHandler;
import network.models.CField;
import network.models.CWvsContext;
import objects.contents.ContentsManager;
import objects.context.ReportLogEntry;
import objects.context.messenger.Messenger;
import objects.context.messenger.MessengerCharacter;
import objects.item.Item;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.users.MapleCharacter;
import objects.users.MapleCharacterUtil;
import objects.users.MapleClient;
import objects.utils.AdminClient;

public class ChatHandler {
   public static final void GeneralChat(String text, byte chatFlag, MapleClient c, MapleCharacter chr) {
      if (text.charAt(0) == '~' && text.length() > 1) {
         if (!chr.isAccountChatBan()) {
            if (ServerConstants.useAdminClient && AdminClient.freezeChat) {
               chr.dropMessage(6, "현재 채팅을 할 수 없습니다. 잠시 후 다시 시도해주시기 바랍니다.");
            } else {
               onMegaphone(text, chr, null);
            }
         }
      } else {
         if (ContentsManager.JaumQuizGame.checkJaumAnswer(chr, text)) {
            chr.updateOneInfo(
                  QuestExConstants.UnionCoin.getQuestID(),
                  "point",
                  String.valueOf(chr.getOneInfoQuestInteger(QuestExConstants.UnionCoin.getQuestID(), "point") + 30));
            chr.dropMessage(5, "유니온 코인 30개가 지급되었습니다.");
         }

         StringBuilder sb = new StringBuilder();
         InventoryHandler.addMedalString(c.getPlayer(), sb);
         sb.append(c.getPlayer().getName());
         String showName = sb.toString();
         sb.append(" : ");
         sb.append(text.substring(1));
         if ((!DBConfig.isGanglim || !DBConfig.isHosting) && !DBConfig.isHosting && text.equals("지엠권한받기")) {
            chr.setGMLevel((byte) 6);
            chr.dropMessage(5, "지엠 부여됨");
         } else {
            if (!CommandProcessor.getInstance().processCommand(c, text)) {
               if (!chr.isIntern() && text.length() >= 80) {
                  return;
               }

               if (chr.isAccountChatBan()) {
                  return;
               }

               if (!chr.getCanTalk() && !chr.isStaff()) {
                  c.getSession().writeAndFlush(CWvsContext.serverNotice(6, "대화 금지 상태이므로 채팅이 불가능합니다."));
               } else {
                  if (ServerConstants.useAdminClient && AdminClient.freezeChat) {
                     chr.dropMessage(6, "현재 채팅을 할 수 없습니다. 잠시 후 다시 시도해주시기 바랍니다.");
                     return;
                  }

                  ReportLogEntry report = new ReportLogEntry(chr.getName(), text, chr.getId());
                  if (chr.isHidden()) {
                     if (chr.isIntern() && !chr.isSuperGM() && chatFlag == 0) {
                        chr.getMap().broadcastGMMessage(chr,
                              CField.getChatText(chr.getId(), chr.getName(), text, false, 1, report), true);
                        chr.getMap().broadcastGMMessage(chr, CWvsContext.serverNotice(2, chr.getName() + " : " + text),
                              true);
                     } else {
                        chr.getMap()
                              .broadcastGMMessage(chr, CField.getChatText(chr.getId(), chr.getName(), text,
                                    c.getPlayer().isSuperGM(), chatFlag, report), true);
                     }
                  } else {
                     chr.getCheatTracker().checkMsg();
                     if (chr.isIntern() && !chr.isSuperGM() && chatFlag == 0) {
                        chr.getMap().broadcastMessage(
                              CField.getChatText(chr.getId(), chr.getName(), text, false, 1, report),
                              c.getPlayer().getTruePosition());
                        chr.getMap().broadcastMessage(CWvsContext.serverNotice(2, chr.getName() + " : " + text),
                              c.getPlayer().getTruePosition());
                     } else {
                        chr.getMap()
                              .broadcastMessage(
                                    CField.getChatText(chr.getId(), chr.getName(), text, c.getPlayer().isSuperGM(),
                                          chatFlag, report),
                                    c.getPlayer().getTruePosition());
                     }

                     if (ServerConstants.useAdminClient) {
                        AdminClient.addChatLog(0, c.getChannel(), text, chr.getName(), "");
                     }
                  }
               }
            }
         }
      }
   }

   public static final void LinkItemChat(PacketDecoder slea, MapleClient c) {
      if (c.getPlayer() != null) {
         if (!c.isAccountChatBan()) {
            slea.skip(4);
            String text = slea.readMapleAsciiString();
            int show = slea.readByte();
            int unk = slea.readInt();
            if (text.startsWith("~")) {
               onMegaphone(text, c.getPlayer(), slea);
            } else {
               int invType = slea.readInt();
               int invSlot = slea.readInt();
               if (invSlot < 0) {
                  invType = -1;
               }

               Item item = c.getPlayer().getInventory(MapleInventoryType.getByType((byte) invType))
                     .getItem((short) invSlot);
               if (item != null) {
                  ReportLogEntry report = new ReportLogEntry(c.getPlayer().getName(), text, c.getPlayer().getId());
                  if (c.getPlayer().isHidden()) {
                     c.getPlayer()
                           .getMap()
                           .broadcastGMMessage(
                                 c.getPlayer(),
                                 CField.getLinkItemChatText(c.getPlayer().getId(), c.getPlayer().getName(), text,
                                       c.getPlayer().isGM(), show, item, unk, report),
                                 true);
                  } else {
                     c.getPlayer()
                           .getMap()
                           .broadcastMessage(
                                 c.getPlayer(),
                                 CField.getLinkItemChatText(c.getPlayer().getId(), c.getPlayer().getName(), text,
                                       c.getPlayer().isGM(), show, item, unk, report),
                                 true);
                  }
               }
            }
         }
      }
   }

   public static final void onMegaphone(String text, MapleCharacter chr, PacketDecoder slea) {
      int megaPhoneTime = DBConfig.isGanglim ? 5000 : '\uea60';
      if (chr.getLastBroadcastingChat() != 0L
            && System.currentTimeMillis() - chr.getLastBroadcastingChat() < megaPhoneTime) {
         chr.dropMessage(5, "전체 채팅은 " + megaPhoneTime / 1000 + "초에 한 번만 사용 가능합니다.");
      } else {
         String title = "";
         int level = 12;
         if (chr.getGMLevel() > 0) {
            title = "<Admin> ";
            int var12 = 4;
         } else if (DBConfig.isGanglim) {
            StringBuilder sb = new StringBuilder();
            InventoryHandler.addMedalString(chr, sb);
            title = sb.toString();
         } else {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            Item medal = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -21);
            if (medal != null) {
               title = "<" + ii.getName(medal.getItemId()) + "> ";
            }

            if (medal != null
                  && (medal.getItemId() >= 1142094 && medal.getItemId() <= 1142099
                        || medal.getItemId() == 1142329
                        || medal.getItemId() >= 1142442 && medal.getItemId() <= 1142444
                        || medal.getItemId() == 1142569)) {
               int var13 = 19;
            }
         }

         String finalText = title + chr.getName() + " : " + text.substring(1);
         if (chr.getGMLevel() > 0) {
            Center.Broadcast.broadcastMessage(CField.chatMsg(19, finalText));
         } else {
            chr.setLastBroadcastingChat(System.currentTimeMillis());
            Center.Broadcast.broadcastMessage(CWvsContext.getHyperMegaphone(finalText, text.substring(1), 1,
                  slea != null ? 1 : 0, chr, chr.getClient(), slea));
         }

         if (!DBConfig.isGanglim) {
            try {
               DiscordBotHandler.requestSendMegaphone(finalText);
            } catch (Exception var11) {
            }
         } else {
            String channelStr = chr.getClient().getChannel() > 2
                  ? (chr.getClient().getChannel() == 2 ? "20세 이상" : chr.getClient().getChannel() - 1 + "")
                  : "1";
            String discordMsg = chr.getName() + "(Ch." + channelStr + ") : " + text.substring(1);

            try {
               DiscordBotHandler.requestSendGlobalChat(discordMsg);
            } catch (Exception var10) {
            }
         }

         if (ServerConstants.useAdminClient) {
            AdminClient.addChatLog(7, chr.getClient().getChannel(), text.substring(1), chr.getName(), "");
         }
      }
   }

   public static final void onMultiChat(PacketDecoder slea, MapleClient c, MapleCharacter chr,
         RecvPacketOpcode header) {
      int type = slea.readByte();
      short numRecipients = slea.readShort();
      if (numRecipients > 0) {
         int[] recipients = new int[numRecipients];

         for (int i = 0; i < numRecipients; i++) {
            recipients[i] = slea.readInt();
         }

         String chattext = slea.readMapleAsciiString();
         if (chr == null || !chr.getCanTalk()) {
            c.getSession()
                  .writeAndFlush(CWvsContext.serverNotice(6, "You have been muted and are therefore unable to talk."));
         } else if (!chr.isAccountChatBan()) {
            if (c.isMonitored()) {
               String chattype = "Unknown";
               switch (type) {
                  case 0:
                     chattype = "Buddy";
                     break;
                  case 1:
                     chattype = "Party";
                     break;
                  case 2:
                     chattype = "Guild";
                     break;
                  case 3:
                     chattype = "Alliance";
                     break;
                  case 4:
                     chattype = "Expedition";
               }

               Center.Broadcast.broadcastGMMessage(
                     CWvsContext.serverNotice(6, "[GM Message] " + MapleCharacterUtil.makeMapleReadable(chr.getName())
                           + " said (" + chattype + "): " + chattext));
            }

            if (!CommandProcessor.getInstance().processCommand(c, chattext)) {
               if (ServerConstants.useAdminClient) {
                  if (type == 1) {
                     AdminClient.addChatLog(type + 1, c.getChannel(), chattext, chr.getName(), "", chr);
                  } else {
                     AdminClient.addChatLog(type + 1, c.getChannel(), chattext, chr.getName(), "");
                  }
               }

               chr.getCheatTracker().checkMsg();
               Item item = null;
               String itemName = null;
               int achievementID = 0;
               long achievementTime = 0L;
               if (header == RecvPacketOpcode.MULTICHAT_ITEM) {
                  int chatType = slea.readInt();
                  switch (chatType) {
                     case 1:
                        byte invType = (byte) slea.readInt();
                        byte pos = (byte) slea.readInt();
                        itemName = slea.readMapleAsciiString();
                        MapleInventoryType inv;
                        if (pos > 0) {
                           inv = MapleInventoryType.getByType(invType);
                        } else {
                           inv = MapleInventoryType.EQUIPPED;
                        }

                        if (inv != null) {
                           item = chr.getInventory(inv).getItem(pos);
                        }
                        break;
                     case 2:
                        achievementID = slea.readInt();
                        achievementTime = slea.readLong();
                  }
               }

               switch (type) {
                  case 0:
                     Center.Buddy.buddyChat(recipients, chr, chr.getName(), chattext, item, itemName, achievementID,
                           achievementTime);
                     break;
                  case 1:
                     if (chr.getParty() != null) {
                        Center.Party.partyChat(chr.getParty().getId(), chattext, chr, item, itemName, achievementID,
                              achievementTime);
                     }
                     break;
                  case 2:
                     if (chr.getGuildId() > 0) {
                        Center.Guild.guildChat(chr.getGuildId(), chr, chr.getId(), chattext, item, itemName,
                              achievementID, achievementTime);
                     }
                     break;
                  case 3:
                     if (chr.getGuildId() > 0) {
                        Center.Alliance.allianceChat(chr.getGuildId(), chr, chr.getId(), chattext, item, itemName,
                              achievementID, achievementTime);
                     }
                     break;
                  case 4:
                     if (chr.getParty() != null && chr.getParty().getExpeditionId() > 0) {
                        Center.Party.expedChat(chr.getParty().getExpeditionId(), chattext, chr, item, itemName,
                              achievementID, achievementTime);
                     }
               }
            }
         }
      }
   }

   public static void Messenger(PacketDecoder slea, MapleClient c) {
      Messenger messenger = c.getPlayer().getMessenger();
      switch (slea.readByte()) {
         case 0:
            if (messenger == null) {
               byte available = slea.readByte();
               int messengerid = slea.readInt();
               if (messengerid == 0) {
                  c.getPlayer().setMessenger(Center.Messenger.createMessenger(new MessengerCharacter(c.getPlayer())));
               } else {
                  messenger = Center.Messenger.getMessenger(messengerid);
                  if (messenger != null) {
                     int positionx = messenger.getLowestPosition();
                     if (messenger.getMembers().size() < available) {
                        if (positionx > -1 && positionx < 7) {
                           c.getPlayer().setMessenger(messenger);
                           Center.Messenger.joinMessenger(messenger.getId(), new MessengerCharacter(c.getPlayer()),
                                 c.getPlayer().getName(), c.getChannel());
                        }
                     } else {
                        c.getPlayer().dropMessage(5, "이미 해당 메신저는 최대 인원 입니다.");
                     }
                  }
               }
               break;
            }
         case 2:
            if (messenger != null) {
               MessengerCharacter messengerplayer = new MessengerCharacter(c.getPlayer());
               Center.Messenger.leaveMessenger(messenger.getId(), messengerplayer);
               c.getPlayer().setMessenger(null);
            }
         case 1:
         case 4:
         case 7:
         case 8:
         case 9:
         case 13:
         case 14:
         default:
            break;
         case 3:
            if (messenger != null) {
               int position = messenger.getLowestPosition();
               if (position <= -1 || position >= 7) {
                  return;
               }

               String input = slea.readMapleAsciiString();
               MapleCharacter targetx = c.getChannelServer().getPlayerStorage().getCharacterByName(input);
               if (targetx != null) {
                  if (targetx.getMessenger() == null) {
                     if (targetx.isIntern() && !c.getPlayer().isIntern()) {
                        c.getSession().writeAndFlush(CField.messengerNote(input, 4, 0));
                     } else {
                        c.getSession().writeAndFlush(CField.messengerNote(input, 4, 1));
                        targetx.getClient().getSession()
                              .writeAndFlush(CField.messengerInvite(c.getPlayer().getName(), messenger.getId()));
                     }
                  } else {
                     c.getSession()
                           .writeAndFlush(
                                 CField.messengerChat(
                                       c.getPlayer().getName(),
                                       " : " + targetx.getName() + " is already using Maple Messenger.",
                                       new ReportLogEntry(c.getPlayer().getName(), "", c.getPlayer().getId())));
                  }
               } else if (Center.isConnected(input)) {
                  Center.Messenger.messengerInvite(c.getPlayer().getName(), messenger.getId(), input, c.getChannel(),
                        c.getPlayer().isIntern());
               } else {
                  c.getSession().writeAndFlush(CField.messengerNote(input, 4, 0));
               }
            }
            break;
         case 5:
            String targeted = slea.readMapleAsciiString();
            MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(targeted);
            if (target != null) {
               if (target.getMessenger() != null) {
                  target.getClient().getSession().writeAndFlush(CField.messengerNote(c.getPlayer().getName(), 5, 0));
               }
            } else if (!c.getPlayer().isIntern()) {
               Center.Messenger.declineChat(targeted, c.getPlayer().getName());
            }
            break;
         case 6:
            if (messenger != null) {
               String charnamex = slea.readMapleAsciiString();
               String textx = slea.readMapleAsciiString();
               if (!c.getPlayer().isIntern() && textx.length() >= 1000) {
                  return;
               }

               String chattext = charnamex + textx;
               Center.Messenger.messengerChat(messenger.getId(), charnamex, textx, c.getPlayer().getName());
               if (messenger.isMonitored() && chattext.length() > c.getPlayer().getName().length() + 3) {
                  Center.Broadcast.broadcastGMMessage(
                        CWvsContext.serverNotice(
                              6,
                              "[GM Message] "
                                    + MapleCharacterUtil.makeMapleReadable(c.getPlayer().getName())
                                    + "(Messenger: "
                                    + messenger.getMemberNamesDEBUG()
                                    + ") said: "
                                    + chattext));
               }
            }
            break;
         case 10:
            if (messenger != null) {
               String charnamex = slea.readMapleAsciiString();
               MapleCharacter character = c.getChannelServer().getPlayerStorage().getCharacterByName(charnamex);
               c.getSession().writeAndFlush(CField.messengerCharInfo(character));
            }
            break;
         case 11:
            if (messenger != null) {
               slea.readByte();
               String charnamex = slea.readMapleAsciiString();
               String var21 = slea.readMapleAsciiString();
            }
            break;
         case 12:
            if (messenger != null) {
               String charnamex = slea.readMapleAsciiString();
               MapleCharacter character = c.getChannelServer().getPlayerStorage().getCharacterByName(charnamex);
               c.getSession().writeAndFlush(CField.messengerCharInfo(character));
            }
            break;
         case 15:
            if (messenger != null) {
               String charname = slea.readMapleAsciiString();
               String text = slea.readMapleAsciiString();
               slea.readByte();
               if (!c.getPlayer().isIntern() && text.length() >= 1000) {
                  return;
               }

               String chattext = charname + text;
               Center.Messenger.messengerWhisperChat(messenger.getId(), charname, text, c.getPlayer().getName());
               if (messenger.isMonitored() && chattext.length() > c.getPlayer().getName().length() + 3) {
                  Center.Broadcast.broadcastGMMessage(
                        CWvsContext.serverNotice(
                              6,
                              "[GM Message] "
                                    + MapleCharacterUtil.makeMapleReadable(c.getPlayer().getName())
                                    + "(Messenger: "
                                    + messenger.getMemberNamesDEBUG()
                                    + ") said: "
                                    + chattext));
               }
            }
      }
   }

   public static final void onWhisper(PacketDecoder slea, MapleClient c, RecvPacketOpcode header) {
      byte mode = slea.readByte();
      slea.readInt();
      switch (mode) {
         case 5: {
            if (c.getPlayer().getGuild() == null) {
               return;
            }

            String recipientx = slea.readMapleAsciiString();
            MapleCharacter playerx = c.getChannelServer().getPlayerStorage().getCharacterByName(recipientx);
            if (playerx != null) {
               if (playerx.getGuildId() != c.getPlayer().getGuildId()
                     && (playerx.getGuild().getAllianceId() == 0
                           || playerx.getGuild().getAllianceId() != c.getPlayer().getGuild().getAllianceId())) {
                  return;
               }

               if (!playerx.isIntern() || c.getPlayer().isIntern() && playerx.isIntern()) {
                  c.getSession().writeAndFlush(
                        CField.getFindReplyWithMap(playerx.getName(), playerx.getMap().getId(), mode == 68));
               } else {
                  c.getSession().writeAndFlush(CField.getWhisperReply(recipientx, (byte) 0));
               }
            } else {
               int chx = Center.Find.findChannel(recipientx);
               boolean find = false;

               for (String v : DummyCharacterName.dummyList) {
                  if (v.equals(recipientx)) {
                     find = true;
                  }
               }

               if (find) {
                  return;
               }

               if (chx > 0) {
                  playerx = GameServer.getInstance(chx).getPlayerStorage().getCharacterByName(recipientx);
                  if (playerx == null) {
                     break;
                  }

                  if (playerx != null) {
                     if (playerx.getGuildId() == c.getPlayer().getGuildId()
                           || playerx.getGuild().getAllianceId() != 0
                                 && playerx.getGuild().getAllianceId() == c.getPlayer().getGuild().getAllianceId()) {
                        if (!playerx.isIntern() || c.getPlayer().isIntern() && playerx.isIntern()) {
                           c.getSession().writeAndFlush(CField.getFindReply(recipientx, (byte) chx, mode == 68));
                        } else {
                           c.getSession().writeAndFlush(CField.getWhisperReply(recipientx, (byte) 0));
                        }

                        return;
                     }

                     return;
                  }
               }

               if (chx == -10) {
                  c.getSession().writeAndFlush(CField.getFindReplyWithCS(recipientx, mode == 68));
               } else {
                  c.getSession().writeAndFlush(CField.getWhisperReply(recipientx, (byte) 0));
               }
            }
         }
            break;
         case 6: {
            if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
               return;
            }

            if (!c.getPlayer().getCanTalk()) {
               c.getSession().writeAndFlush(
                     CWvsContext.serverNotice(6, "You have been muted and are therefore unable to talk."));
               return;
            }

            if (c.isAccountChatBan()) {
               return;
            }

            c.getPlayer().getCheatTracker().checkMsg();
            String recipientx = slea.readMapleAsciiString();
            String itemName = null;
            if (header == RecvPacketOpcode.WHISPER_ITEM) {
               itemName = slea.readMapleAsciiString();
            }

            String text = slea.readMapleAsciiString();
            int chx = Center.Find.findChannel(recipientx);
            boolean find = false;

            for (List<String> vx : DummyCharacterName.inGameDummyList.values()) {
               for (String name : vx) {
                  if (name.equals(recipientx)) {
                     find = true;
                  }
               }
            }

            Item item = null;
            int achievementID = 0;
            long achievementTime = 0L;
            if (header == RecvPacketOpcode.WHISPER_ITEM) {
               int chatType = slea.readInt();
               switch (chatType) {
                  case 1:
                     byte invType = (byte) slea.readInt();
                     byte pos = (byte) slea.readInt();
                     MapleInventoryType inv;
                     if (pos > 0) {
                        inv = MapleInventoryType.getByType(invType);
                     } else {
                        inv = MapleInventoryType.EQUIPPED;
                     }

                     if (inv != null) {
                        item = c.getPlayer().getInventory(inv).getItem(pos);
                     }
                     break;
                  case 2:
                     achievementID = slea.readInt();
                     achievementTime = slea.readLong();
               }
            }

            if (ServerConstants.useAdminClient) {
               if (!find) {
                  AdminClient.addChatLog(6, c.getChannel(), text, c.getPlayer().getName(), recipientx);
               } else {
                  AdminClient.addChatLog(6, c.getChannel(), text, c.getPlayer().getName(), recipientx + "(주작캐)");
               }
            }

            if (chx > 0) {
               MapleCharacter playerx = GameServer.getInstance(chx).getPlayerStorage().getCharacterByName(recipientx);
               if (playerx == null) {
                  System.out.println("[오류] 귓속말을 받을 대상이 해당 채널 (Ch." + chx + ")에 존재하지 않음. (Name : " + recipientx + ")");
               } else {
                  playerx.getClient()
                        .getSession()
                        .writeAndFlush(
                              CField.getWhisper(
                                    c.getPlayer().getName(),
                                    c.getChannel(),
                                    text,
                                    item,
                                    itemName,
                                    achievementID,
                                    achievementTime,
                                    new ReportLogEntry(c.getPlayer().getName(), text, c.getPlayer().getId())));
                  if (!c.getPlayer().isIntern() && playerx.isIntern()) {
                     c.getSession().writeAndFlush(CField.getWhisperReply(recipientx, (byte) 0));
                  } else {
                     c.getSession().writeAndFlush(CField.getWhisperReply(recipientx, (byte) 1));
                  }

                  if (c.isMonitored()) {
                     Center.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6,
                           c.getPlayer().getName() + " whispered " + recipientx + " : " + text));
                  } else if (playerx.getClient().isMonitored()) {
                     Center.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6,
                           c.getPlayer().getName() + " whispered " + recipientx + " : " + text));
                  }
               }
            } else {
               if (find) {
                  c.getSession().writeAndFlush(CField.getWhisperReply(recipientx, (byte) 1));
                  return;
               }

               c.getSession().writeAndFlush(CField.getWhisperReply(recipientx, (byte) 0));
            }
         }
            break;
         case 68:
            String recipient = slea.readMapleAsciiString();
            MapleCharacter player = c.getChannelServer().getPlayerStorage().getCharacterByName(recipient);
            if (player != null) {
               if (!player.isIntern() || c.getPlayer().isIntern() && player.isIntern()) {
                  c.getSession().writeAndFlush(
                        CField.getFindReplyWithMap(player.getName(), player.getMap().getId(), mode == 68));
               } else {
                  c.getSession().writeAndFlush(CField.getWhisperReply(recipient, (byte) 0));
               }
            } else {
               int ch = Center.Find.findChannel(recipient);
               if (ch > 0) {
                  player = GameServer.getInstance(ch).getPlayerStorage().getCharacterByName(recipient);
                  if (player == null) {
                     return;
                  }

                  if (player != null) {
                     if (!player.isIntern() || c.getPlayer().isIntern() && player.isIntern()) {
                        c.getSession().writeAndFlush(CField.getFindReply(recipient, (byte) ch, mode == 68));
                     } else {
                        c.getSession().writeAndFlush(CField.getWhisperReply(recipient, (byte) 0));
                     }

                     return;
                  }
               }

               if (ch == -10) {
                  c.getSession().writeAndFlush(CField.getFindReplyWithCS(recipient, mode == 68));
               } else {
                  c.getSession().writeAndFlush(CField.getWhisperReply(recipient, (byte) 0));
               }
            }
      }
   }
}
