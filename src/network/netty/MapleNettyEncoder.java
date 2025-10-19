package network.netty;

import constants.GameConstants;
import constants.ServerConstants;
import database.DBConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import network.SendPacketOpcode;
import objects.users.MapleClient;
import objects.utils.FileoutputUtil;
import objects.utils.HexTool;
import objects.utils.MapleAESOFB;

public class MapleNettyEncoder extends MessageToByteEncoder<byte[]> {
   protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf buffer) throws Exception {
      MapleClient client = (MapleClient)ctx.channel().attr(MapleClient.CLIENTKEY).get();
      if (client != null) {
         MapleAESOFB send_crypto = client.getSendCrypto();
         Lock mutex = client.getLock();
         byte[] header = send_crypto.getPacketHeader(msg.length);
         mutex.lock();

         byte[] data;
         try {
            String address = ctx.channel().remoteAddress().toString().split(":")[0];
            if (!DBConfig.isHosting && ServerConstants.SAVE_SEND_PACKET && !client.getDebugPacketSend() && client.getDebugPacketSendTime() == 0L) {
               client.setDebugPacketSendTime(System.currentTimeMillis() + 86400000L);
               client.setDebugPacketSend(true);
            }

            if (client.getPlayer() != null && client.isGm() && GameConstants.ignoreOpcode.size() > 0) {
               int opcode = (msg[1] & 255) * 256 + (msg[0] & 255);

               for (int check : GameConstants.ignoreOpcode) {
                  if (check == opcode) {
                     return;
                  }
               }
            }

            if (ServerConstants.INGAME_TEST_SEND_BLOCK && client.getServerType() == ServerType.GAME) {
               int opcode = (msg[1] & 255) * 256 + (msg[0] & 255);
               if (opcode != SendPacketOpcode.WARP_TO_MAP.getValue() && opcode != SendPacketOpcode.PRIVATE_SERVER_AUTH.getValue()) {
                  return;
               }
            }

            if (client.getDebugPacketSend() && client.getPlayer() != null) {
               String chrname = client.getPlayer().getName();
               int opcode = (msg[1] & 255) * 256 + (msg[0] & 255);
               if (opcode != SendPacketOpcode.PRIVATE_SERVER_AUTH.getValue()
                  && opcode != SendPacketOpcode.NPC_ACTION.getValue()
                  && opcode != SendPacketOpcode.SHOW_STATUS_INFO.getValue()) {
                  long time = System.currentTimeMillis();
                  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMdd.HHmm");
                  Date date = new Date();
                  date.setTime(client.getDebugPacketStartTime());
                  String dateString = simpleDateFormat.format(date);
                  StringBuilder sb = new StringBuilder();
                  sb.append("[보냄] " + opcode + " : " + SendPacketOpcode.getOpcodeName(opcode) + " :\n" + HexTool.toString(msg) + "\r\n");
                  sb.append(HexTool.toStringFromAscii(msg) + "\r\n");
                  FileoutputUtil.log("./PacketDebug/" + dateString + "_" + chrname + ".rtf", sb.toString());
                  if (time > client.getDebugPacketSendTime()) {
                     client.setDebugPacketSend(false);
                     client.setDebugPacketSendTime(0L);
                  }
               }
            }

            if (ServerConstants.DEBUG_SEND) {
               int opcode = (msg[1] & 255) * 256 + (msg[0] & 255);
               if (opcode != SendPacketOpcode.ELITE_BOSS_CURSE_STATE.getValue()
                  && opcode != SendPacketOpcode.MOVE_PET.getValue()
                  && opcode != SendPacketOpcode.NPC_ACTION.getValue()
                  && opcode != SendPacketOpcode.MOB_CTRL_ACK.getValue()
                  && opcode != SendPacketOpcode.SPAWN_MONSTER.getValue()
                  && opcode != SendPacketOpcode.SPAWN_MONSTER_CONTROL.getValue()
                  && opcode != SendPacketOpcode.MOVE_MONSTER.getValue()
                  && opcode != SendPacketOpcode.MOB_ATTACK_BLOCK.getValue()
                  && opcode != SendPacketOpcode.MOVE_PLAYER.getValue()) {
                  System.out.println("[SEND] " + opcode + " : " + SendPacketOpcode.getOpcodeName(opcode) + " :\n" + HexTool.toString(msg));
                  System.out.println(HexTool.toStringFromAscii(msg) + "\n");
               }
            }

            if (client.getServerType() == ServerType.LOGIN) {
               data = send_crypto.crypt(msg);
            } else {
               data = send_crypto.newcrypt(msg);
            }
         } finally {
            mutex.unlock();
         }

         buffer.writeBytes(header);
         buffer.writeBytes(data);
      } else {
         buffer.writeBytes(msg);
      }
   }
}
