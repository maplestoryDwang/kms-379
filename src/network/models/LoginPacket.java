package network.models;

import constants.GameConstants;
import constants.JobConstants;
import database.DBConfig;
import java.util.List;
import java.util.Map;
import java.util.Set;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.login.LoginServer;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.utils.CurrentTime;
import objects.utils.HexTool;
import objects.utils.Randomizer;

public class LoginPacket {
   public static final String version;

   public static byte[] getServerHello(short mapleVersion, byte[] sendIv, byte[] recvIv) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(16);
      mplew.write(35);
      mplew.write(1);
      mplew.writeInt(379);
      mplew.encodeBuffer(recvIv);
      mplew.encodeBuffer(sendIv);
      mplew.write((byte)1);
      mplew.write(4);
      return mplew.getPacket();
   }

   public static final byte[] getHello(short mapleVersion, byte[] sendIv, byte[] recvIv) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(44 + LoginPacket.version.length());
      mplew.write(35);
      mplew.write(1);
      mplew.writeMapleAsciiString(LoginPacket.version);
      mplew.encodeBuffer(recvIv);
      mplew.encodeBuffer(sendIv);
      mplew.write((byte)1);
      mplew.write(0);
      mplew.write(35);
      mplew.write(1);
      mplew.writeInt((int)mapleVersion);
      mplew.encodeBuffer(recvIv);
      mplew.encodeBuffer(sendIv);
      mplew.write((byte)1);
      int version = 37908;
      mplew.writeInt(version);
      mplew.writeInt(version);
      mplew.writeZeroBytes(6);
      mplew.write(5);
      return mplew.getPacket();
   }

   public static final byte[] privateServerAuth(int value) {
      PacketEncoder mplew = new PacketEncoder(6);
      mplew.writeShort(SendPacketOpcode.PRIVATE_SERVER_AUTH.getValue());
      mplew.writeInt(value);
      return mplew.getPacket();
   }

   public static final byte[] CheckHotFix() {
      PacketEncoder mplew = new PacketEncoder(3);
      mplew.writeShort(SendPacketOpcode.HOT_FIX.getValue());
      mplew.write(0);
      return mplew.getPacket();
   }

   public static final byte[] getPing() {
      PacketEncoder mplew = new PacketEncoder(2);
      mplew.writeShort(SendPacketOpcode.PING.getValue());
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static final byte[] getAuthSuccessRequest(MapleClient client, String id) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
      mplew.write(0);
      mplew.writeMapleAsciiString("");
      mplew.writeMapleAsciiString(id);
      mplew.writeLong(12345678L);
      mplew.writeInt(client.getAccID());
      int gradeCode = 0;
      int grade = 0;
      if (client.isGm()) {
         gradeCode = 1;
         int var9 = 32;
      }

      mplew.write(gradeCode);
      mplew.writeInt(128);
      mplew.writeInt(0);
      mplew.writeInt(100);
      mplew.write(1);
      mplew.write(0);
      mplew.writeLong(0L);
      mplew.write(0);
      mplew.writeMapleAsciiString("j**stor*");
      mplew.writeZeroBytes(3);
      mplew.write(1);
      mplew.write(35);

      for (JobConstants.LoginJob j : JobConstants.LoginJob.values()) {
         mplew.write(j.getFlag());
         mplew.writeShort(j.getFlag());
      }

      mplew.write(0);
      mplew.writeInt(-1);
      return mplew.getPacket();
   }

   public static byte[] getSecondPasswordCheck(boolean enable) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHECK_SPW_EXIST_RESULT.getValue());
      mplew.write(enable ? 1 : 0);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static final byte[] getLoginFailed(int reason) {
      PacketEncoder mplew = new PacketEncoder(16);
      mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
      mplew.write(reason);
      mplew.write(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static final byte[] getPermBan(byte reason) {
      PacketEncoder mplew = new PacketEncoder(16);
      mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
      mplew.writeShort(2);
      mplew.writeInt(0);
      mplew.writeShort(reason);
      mplew.encodeBuffer(HexTool.getByteArrayFromHexString("01 01 01 01 00"));
      return mplew.getPacket();
   }

   public static final byte[] getTempBan(long timestampTill, byte reason) {
      PacketEncoder mplew = new PacketEncoder(17);
      mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
      mplew.write(2);
      mplew.write(0);
      mplew.writeInt(0);
      mplew.write(reason);
      mplew.writeLong(timestampTill);
      return mplew.getPacket();
   }

   public static final byte[] deleteCharResponse(int cid, int state) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.DELETE_CHAR_RESPONSE.getValue());
      mplew.writeInt(cid);
      mplew.write(state);
      mplew.write(0);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static final byte[] preCheckSPWResult(byte mode) {
      PacketEncoder mplew = new PacketEncoder(3);
      mplew.writeShort(SendPacketOpcode.PRE_CHECK_SPW_RESULT.getValue());
      mplew.write(mode);
      return mplew.getPacket();
   }

   public static final byte[] secondPwError(byte mode) {
      PacketEncoder mplew = new PacketEncoder(3);
      mplew.writeShort(SendPacketOpcode.SECONDPW_ERROR.getValue());
      mplew.write(mode);
      return mplew.getPacket();
   }

   public static byte[] lastConnectedWorld() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.LAST_CONNECTED_WORLD.getValue());
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] sendRecommended(int world, String message) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SEND_RECOMMENDED.getValue());
      mplew.write(message != null ? 1 : 0);
      if (message != null) {
         mplew.writeInt(world);
         mplew.writeMapleAsciiString(message);
      }

      return mplew.getPacket();
   }

   public static final byte[] getServerList(int serverId, Map<Integer, Integer> channelLoad) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
      mplew.write(serverId);
      String worldName = LoginServer.getTrueServerName();
      mplew.writeMapleAsciiString(worldName);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.write(0);
      mplew.write(3);
      mplew.write(0);
      mplew.writeMapleAsciiString("");
      int lastChannel = 1;
      Set<Integer> channels = channelLoad.keySet();

      for (int i = 30; i > 0; i--) {
         if (channels.contains(i)) {
            lastChannel = i;
            break;
         }
      }

      mplew.write(lastChannel);

      for (int ix = 1; ix <= lastChannel; ix++) {
         int load;
         if (channels.contains(ix)) {
            int max = 60;
            if (DBConfig.isGanglim) {
               load = (int)Math.min((double)max, GameServer.getInstance(ix).getPlayerStorage().getConnectedClients() * 3.3);
            } else {
               load = Math.min(max, GameServer.getInstance(ix).getPlayerStorage().getConnectedClients() * 3);
            }
         } else {
            load = 1;
         }

         mplew.writeMapleAsciiString(worldName + "-" + (ix == 1 ? ix : (ix == 2 ? "20세" : ix - 1)));
         mplew.writeInt(load == 0 ? 1 : load);
         mplew.write(serverId);
         mplew.write(ix - 1);
         mplew.write(0);
      }

      short v2 = 0;
      mplew.writeShort(v2);
      mplew.writeInt(0);
      byte v43 = 1;
      mplew.write(v43);
      if (v43 > 0) {
         mplew.writeInt(5);
         mplew.write(4);
         mplew.writeShort(2);
         mplew.write(3);
         mplew.writeShort(1);
         mplew.write(2);
         mplew.writeShort(1);
         mplew.write(1);
         mplew.writeShort(1);
         mplew.write(0);
         mplew.writeShort(1);
      }

      mplew.write(0);
      byte v4 = 0;
      mplew.write(0);
      if (v4 > 0) {
         int size = 0;
         int v5 = 0;
         mplew.write(0);
         mplew.write(0);
         mplew.write(0);
      }

      return mplew.getPacket();
   }

   public static final byte[] getChannelBackImg(boolean redisplay) {
      PacketEncoder w = new PacketEncoder();
      w.writeShort(SendPacketOpcode.CHANNEL_BACK_IMG.getValue());
      w.write(!redisplay ? 1 : 0);
      if (!redisplay) {
         String[] list = new String[]{"default", "default1", "default2", "default3"};
         w.writeMapleAsciiString(list[Randomizer.rand(0, 3)]);
         w.write(1);
         w.writeMapleAsciiString("");
         w.write(0);
      }

      w.writeZeroBytes(5);
      return w.getPacket();
   }

   public static final byte[] getMapTaggedObjectVisible(String id, int v, int v2, int v3) {
      PacketEncoder w = new PacketEncoder();
      w.writeShort(SendPacketOpcode.CHANNEL_BACK_IMG.getValue());
      w.write(1);
      w.writeMapleAsciiString(id);
      w.writeInt(v);
      w.writeInt(v2);
      w.writeInt(v3);
      return w.getPacket();
   }

   public static final byte[] getChannelBackImgNew() {
      PacketEncoder w = new PacketEncoder();
      w.writeShort(SendPacketOpcode.CHANNEL_BACK_IMG_NEW.getValue());
      String[] list = new String[]{"default", "default1", "default2", "default3"};
      w.writeMapleAsciiString(list[Randomizer.rand(0, 3)]);
      return w.getPacket();
   }

   public static final byte[] getEndOfServerList() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
      mplew.write(255);
      mplew.write(0);
      mplew.write(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static final byte[] getServerStatus(int status) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SERVERSTATUS.getValue());
      mplew.writeShort(status);
      return mplew.getPacket();
   }

   public static final byte[] getCharList(boolean isGm, String secondpw, List<MapleCharacter> chars, int charslots, byte nameChange, List<Integer> charPosition) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHARLIST.getValue());
      mplew.write(0);
      mplew.writeMapleAsciiString("");
      mplew.writeInt(1);
      mplew.writeInt(1);
      mplew.writeInt(1);
      mplew.writeInt(1);
      mplew.writeInt(1);
      mplew.writeInt(charslots);
      mplew.write(1);
      mplew.write(0);
      mplew.writeInt(0);
      mplew.writeLong(PacketHelper.getKoreanTimestamp(System.currentTimeMillis()));
      boolean isEditedList = charPosition != null && !charPosition.isEmpty();
      mplew.write(isEditedList ? 1 : 0);
      mplew.writeInt(isEditedList ? charPosition.size() : chars.size());
      if (isEditedList) {
         for (int position : charPosition) {
            mplew.writeInt(position);
         }
      } else {
         for (MapleCharacter chr : chars) {
            mplew.writeInt(chr.getId());
         }
      }

      mplew.write(chars.size());

      for (MapleCharacter chr : chars) {
         addCharEntry(mplew, chr, !chr.isGM() && chr.getLevel() >= 30, false);
      }

      mplew.write(secondpw != null && secondpw.length() > 0 ? 1 : 2);
      mplew.write(0);
      mplew.write(isGm ? 0 : 1);
      mplew.writeInt(charslots);
      mplew.writeInt(0);
      mplew.writeInt(-1);
      mplew.writeLong(PacketHelper.getKoreanTimestamp(System.currentTimeMillis()));
      mplew.write(0);
      mplew.write(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.write(0);
      mplew.write(0);
      mplew.writeInt(1);
      mplew.writeInt(isGm ? 1 : 0);
      return mplew.getPacket();
   }

   public static final byte[] addNewCharEntry(MapleCharacter chr, boolean worked) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ADD_NEW_CHAR_ENTRY.getValue());
      mplew.write(worked ? 0 : 1);
      mplew.writeInt(0);
      addCharEntry(mplew, chr, false, false);
      mplew.write(1);
      return mplew.getPacket();
   }

   public static final byte[] charNameResponse(String charname, boolean nameUsed) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHAR_NAME_RESPONSE.getValue());
      mplew.writeMapleAsciiString(charname);
      mplew.write(nameUsed ? 122 : 0);
      return mplew.getPacket();
   }

   private static final void addCharEntry(PacketEncoder mplew, MapleCharacter chr, boolean ranking, boolean viewAll) {
      PacketHelper.addCharStats(mplew, chr);
      mplew.writeInt(0);
      mplew.writeInt(0);
      mplew.writeLong(0L);
      mplew.writeInt(0);
      mplew.writeInt(1);
      mplew.write(0);
      mplew.write(0);
      mplew.writeMapleAsciiString("");
      PacketHelper.addCharLook(mplew, chr, true, false);
      if (GameConstants.isZero(chr.getJob())) {
         PacketHelper.addCharLook(mplew, chr, true, true);
      }
   }

   public static final byte[] getSecondPasswordConfirm(byte op) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.AUTH_STATUS_WITH_SPW.getValue());
      mplew.write(op);
      if (op == 0) {
         mplew.write(1);
         mplew.write(35);

         for (JobConstants.LoginJob j : JobConstants.LoginJob.values()) {
            mplew.write(j.getFlag());
            mplew.writeShort(j.getFlag());
         }
      }

      return mplew.getPacket();
   }

   public static byte[] NewSendPasswordWay(MapleClient c) {
      PacketEncoder w = new PacketEncoder();
      w.writeShort(SendPacketOpcode.NEW_PASSWORD_CHECK.getValue());
      w.writeShort(c.getSecondPassword() != null ? 1 : 0);
      return w.getPacket();
   }

   public static final byte[] getSecondPasswordResult(boolean success) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.AUTH_STATUS_WITH_SPW_RESULT.getValue());
      mplew.write(success ? 0 : 20);
      return mplew.getPacket();
   }

   public static final byte[] getReturnToTitle() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.RETURN_TO_TITLE.getValue());
      return mplew.getPacket();
   }

   public static final byte[] getReturnToCharacterSelect() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.RETURN_TO_CHARACTER_SELECT.getValue());
      return mplew.getPacket();
   }

   public static final byte[] getIssueReloginCookie(String cookie) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ISSUE_RELOGIN_COOKIE.getValue());
      mplew.writeMapleAsciiString(cookie);
      return mplew.getPacket();
   }

   public static byte[] getChangeSPWResult(byte result) {
      PacketEncoder w = new PacketEncoder();
      w.writeShort(SendPacketOpcode.CHANGE_SPW_RESULT.getValue());
      w.write(result);
      return w.getPacket();
   }

   public static byte[] checkWebLoginEmailID(byte result) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHECK_WEB_LOGIN_EMAIL_ID.getValue());
      mplew.write(result);
      return mplew.getPacket();
   }

   public static byte[] getAccountInfoResult(byte result, MapleClient c, boolean charList) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ACCOUNT_INFO_RESULT.getValue());
      mplew.write(result);
      mplew.writeMapleAsciiString("");
      if (result != 0) {
         return mplew.getPacket();
      } else {
         mplew.writeInt(c.getAccID());
         int gradeCode = 0;
         int grade = 0;
         if (c.isGm()) {
            gradeCode = 1;
            grade = 32;
         }

         mplew.write(gradeCode);
         mplew.writeInt(grade);
         mplew.writeInt(0);
         mplew.writeInt(20);
         mplew.write(0);
         mplew.write(0);
         mplew.writeLong(0L);
         mplew.writeMapleAsciiString(c.getAccountName());
         mplew.writeMapleAsciiString("Jihyeon*");
         mplew.writeMapleAsciiString("");
         mplew.write(1);
         mplew.write(35);

         for (JobConstants.LoginJob j : JobConstants.LoginJob.values()) {
            mplew.write(j.getFlag());
            mplew.writeShort(j.getFlag());
         }

         mplew.write(0);
         mplew.writeInt(-1);
         mplew.write(0);
         mplew.write(charList);
         return mplew.getPacket();
      }
   }

   public static byte[] touchWorldResult(int world) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.TOUCH_WORLD_RESULT.getValue());
      mplew.write(0);
      mplew.writeShort(0);
      mplew.writeInt(world);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   public static byte[] enableDataLoading() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ENABLE_DATA_LOAD.getValue());
      return mplew.getPacket();
   }

   public static byte[] getCharWorld(int world) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CHAR_WORLD.getValue());
      mplew.writeInt(world);
      return mplew.getPacket();
   }

   public static byte[] secureClient(int i, int i1, int i2, String all) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.SECURE_CLIENT.getValue());
      mplew.writeInt(1);
      mplew.writeInt(i);
      mplew.writeInt(i1);
      mplew.writeInt(i2);
      mplew.writeMapleAsciiString(all);
      return mplew.getPacket();
   }

   public static byte[] CheckClientTime() {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.CLIENT_CHECK_TIME.getValue());
      mplew.writeLong(CurrentTime.minute());
      mplew.writeLong(CurrentTime.second());
      return mplew.getPacket();
   }

   static {
      int ret = 0;
      ret ^= 379;
      ret ^= 0;
      ret ^= 524288;
      version = String.valueOf(ret);
   }
}
