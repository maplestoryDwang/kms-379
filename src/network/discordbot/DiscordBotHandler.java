package network.discordbot;

import api.telegram.TelegramSender;
import constants.ServerConstants;
import constants.devtempConstants.DummyCharacterName;
import database.DBConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.ArrayList;
import network.auction.AuctionServer;
import network.decode.PacketDecoder;
import network.discordbot.processor.DiscordBotProcessor;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.shop.CashShopServer;
import objects.item.MapleItemInformationProvider;
import objects.item.rewards.RandomRewards;
import objects.item.rewards.RoyalStyle;
import objects.users.MapleCharacter;
import scripting.newscripting.ScriptManager;

public class DiscordBotHandler extends SimpleChannelInboundHandler<PacketDecoder> {
   public static Channel session = null;

   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      String IP = ctx.channel().remoteAddress().toString().split(":")[0];
      System.out.println("디스코드 봇 체킹 : " + IP + " " + session + " " + ctx.channel().toString());
      long time = System.currentTimeMillis();
      if (session == null) {
         session = ctx.channel();
      }
   }

   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      if (session == ctx.channel()) {
         session = null;
      }
   }

   public void exceptionCaught(ChannelHandlerContext ctx, Throwable ex) {
      if (session == ctx.channel()) {
         session = null;
      }
   }

   public void userEventTriggered(ChannelHandlerContext ctx, Object event) {
   }

   protected void channelRead0(ChannelHandlerContext ctx, PacketDecoder slea) throws Exception {
      if (ctx.channel() == session) {
         if (!DBConfig.isGanglim) {
            byte header = slea.readByte();
            switch (header) {
               case 0: {
                  PacketEncoder encoder = new PacketEncoder();
                  encoder.write(0);
                  double rate = ServerConstants.connectedRate;
                  String pick = DummyCharacterName.getConnected(rate);
                  int count = 0;

                  for (GameServer gameServer : GameServer.getAllInstances()) {
                     int size = (int) (gameServer.getPlayerCountInChannel() * rate);
                     count += size;
                  }

                  int csCount = 0;

                  for (MapleCharacter p : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
                     if (p != null) {
                        csCount++;
                     }
                  }

                  csCount = (int) (csCount * rate);
                  count += csCount;
                  int auctionCount = 0;

                  for (MapleCharacter px : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
                     if (px != null) {
                        auctionCount++;
                     }
                  }

                  auctionCount = (int) (auctionCount * rate);
                  count += auctionCount;
                  encoder.writeMapleAsciiString("강림 총 인원은 현재 " + count + "명 입니다.\r\n" + pick);
                  session.writeAndFlush(encoder.getPacket());
               }
               case 1:
               default:
                  break;
               case 2: {
                  PacketEncoder encoder = new PacketEncoder();
                  encoder.write(header);
                  StringBuilder sb = new StringBuilder();

                  for (RandomRewards wd : RoyalStyle.wonderBerry) {
                     int itemid = wd.getMaleItemID();

                     String name;
                     try {
                        name = MapleItemInformationProvider.getInstance().getName(itemid);
                     } catch (NullPointerException var15) {
                        name = "이름없음";
                     }

                     sb.append(name).append("\r\n");
                  }

                  encoder.writeMapleAsciiString(sb.toString());
                  session.writeAndFlush(encoder.getPacket());
               }
                  break;
               case 3: {
                  PacketEncoder encoder = new PacketEncoder();
                  encoder.write(header);
                  StringBuilder sb = new StringBuilder();

                  for (RandomRewards wd : DBConfig.isGanglim ? RoyalStyle.lunaCrystal_Royal
                        : RoyalStyle.lunaCrystal_Jin) {
                     int itemid = wd.getMaleItemID();

                     String name;
                     try {
                        name = MapleItemInformationProvider.getInstance().getName(itemid);
                     } catch (NullPointerException var14) {
                        name = "이름없음";
                     }

                     sb.append(name).append("\r\n");
                  }

                  encoder.writeMapleAsciiString(sb.toString());
                  session.writeAndFlush(encoder.getPacket());
               }
                  break;
               case 4: {
                  PacketEncoder encoder = new PacketEncoder();
                  encoder.write(header);
                  StringBuilder sb = new StringBuilder();

                  for (RandomRewards wd : RoyalStyle.goldApple) {
                     int itemid = wd.getMaleItemID();

                     String name;
                     try {
                        name = MapleItemInformationProvider.getInstance().getName(itemid);
                     } catch (NullPointerException var13) {
                        name = "이름없음";
                     }

                     sb.append(name).append("\r\n");
                  }

                  encoder.writeMapleAsciiString(sb.toString());
                  session.writeAndFlush(encoder.getPacket());
               }
                  break;
               case 5:
                  if (ScriptManager.resetScriptBool()) {
                  }
            }
         } else {
            short header = slea.readShort();
            switch (header) {
               case 100:
                  DiscordBotProcessor.registerAccount(slea);
                  break;
               case 101:
                  DiscordBotProcessor.registerAccountResult(slea);
                  break;
               case 104:
                  DiscordBotProcessor.connectAccount(slea);
                  break;
               case 105:
                  DiscordBotProcessor.connectAccountResult(slea);
                  break;
               case 999:
                  DiscordBotProcessor.sendGlobalNotice(slea);
            }
         }
      }
   }

   public static void requestSendMegaphone(String message) {
      if (session != null && !DBConfig.isGanglim) {
         PacketEncoder encoder = new PacketEncoder();
         encoder.write(1);
         encoder.writeMapleAsciiString(message);
         session.writeAndFlush(encoder.getPacket());
      }
   }

   public static boolean requestSendTelegram(String message, long chatid) {
      if (DBConfig.isGanglim && DBConfig.isHosting) {
         try {
            TelegramSender.sendMessage(String.valueOf(chatid), message);
            return true;
         } catch (Exception var4) {
            System.out.println("텔레그램 메시지 전송 발생");
            var4.printStackTrace();
            return false;
         }
      } else if (session != null) {
         PacketEncoder encoder = new PacketEncoder();
         encoder.write(6);
         encoder.writeLong(chatid);
         encoder.writeMapleAsciiString(message);
         session.writeAndFlush(encoder.getPacket());
         return true;
      } else {
         return false;
      }
   }

   public static boolean requestSendGlobalChat(String msg) {
      if (!DBConfig.isGanglim) {
         return false;
      } else if (session != null) {
         PacketEncoder encoder = new PacketEncoder();
         encoder.writeShort(1000);
         encoder.writeMapleAsciiString(msg);
         session.writeAndFlush(encoder.getPacket());
         return true;
      } else {
         return false;
      }
   }

   public static boolean requestSendTelegramPacketError(String message) {
      if (session != null && !DBConfig.isGanglim) {
         PacketEncoder encoder = new PacketEncoder();
         encoder.write(7);
         encoder.writeMapleAsciiString(message);
         session.writeAndFlush(encoder.getPacket());
         return true;
      } else {
         return false;
      }
   }

   public static boolean requestSendTelegram(String message) {
      if (session != null && DBConfig.isGanglim) {
         PacketEncoder encoder = new PacketEncoder();
         encoder.writeShort(999);
         encoder.writeMapleAsciiString(message);
         session.writeAndFlush(encoder.getPacket());
         return true;
      } else {
         return false;
      }
   }

   public static boolean requestSendTelegramWithChatID(String message, long chatid) {
      if (DBConfig.isGanglim && DBConfig.isHosting) {
         try {
            TelegramSender.sendMessage(String.valueOf(chatid), message);
            return true;
         } catch (Exception var4) {
            System.out.println("텔레그램 메시지 전송 오류 발생");
            var4.printStackTrace();
            return false;
         }
      } else if (session != null && DBConfig.isGanglim) {
         PacketEncoder encoder = new PacketEncoder();
         encoder.writeShort(777);
         encoder.writeLong(chatid);
         encoder.writeMapleAsciiString(message);
         session.writeAndFlush(encoder.getPacket());
         return true;
      } else {
         return false;
      }
   }

   public static boolean requestSendTelegramCCU(String message) {
      if (DBConfig.isGanglim && DBConfig.isHosting) {
         try {
            TelegramSender.sendMessage("CCU", message);
            return true;
         } catch (Exception var2) {
            System.out.println("텔레그램 메시지 전송 오류 발생");
            var2.printStackTrace();
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean requestSendTelegramDonation(String message) {
      if (DBConfig.isGanglim && DBConfig.isHosting) {
         try {
            TelegramSender.sendMessage("Donation", message);
            return true;
         } catch (Exception var2) {
            System.out.println("텔레그램 메시지 전송 오류 발생");
            var2.printStackTrace();
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean requestSendDiscord(String message) {
      if (session != null && DBConfig.isGanglim) {
         PacketEncoder encoder = new PacketEncoder();
         encoder.writeShort(1000);
         encoder.writeMapleAsciiString(message);
         session.writeAndFlush(encoder.getPacket());
         return true;
      } else {
         return false;
      }
   }
}
