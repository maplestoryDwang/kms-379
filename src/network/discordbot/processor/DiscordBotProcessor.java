package network.discordbot.processor;

import java.nio.charset.Charset;
import java.util.regex.Pattern;
import network.center.Center;
import network.decode.PacketDecoder;
import network.discordbot.DiscordBotHandler;
import network.encode.PacketEncoder;
import network.login.AutoRegister;
import network.models.CField;

public class DiscordBotProcessor {
   public static void sendGlobalNotice(PacketDecoder slea) {
      String name = slea.readMapleAsciiString();
      String msg = slea.readMapleAsciiString();
      Center.Broadcast.broadcastMessage(CField.chatMsg(28, "[디스코드] " + name + " : " + msg));
   }

   public static void registerAccount(PacketDecoder slea) {
      String discordid = slea.readMapleAsciiString();
      String id = slea.readMapleAsciiString();
      String pw = slea.readMapleAsciiString();
      String phoneNumber = slea.readMapleAsciiString();
      String chatid = slea.readMapleAsciiString();
      String pattern = "^[a-zA-Z0-9]*$";
      String pattern2 = "^([0-1]{3})-([0-9]{4})-([0-9]{4})$";
      PacketEncoder encoder = new PacketEncoder();
      encoder.writeShort(100);
      int loginop = 0;
      if (AutoRegister.getAccountExists(id)) {
         encoder.write(0);
      } else if (!Pattern.matches(pattern, id) || id.getBytes(Charset.forName("MS949")).length < 4 || id.getBytes(Charset.forName("MS949")).length > 13) {
         encoder.write(1);
         loginop = 1;
      } else if (pw.getBytes(Charset.forName("MS949")).length < 4 || pw.getBytes(Charset.forName("MS949")).length > 25) {
         encoder.write(2);
         loginop = 2;
      } else if (!Pattern.matches(pattern2, phoneNumber) || AutoRegister.getAccountExistsPhoneNumber(phoneNumber)) {
         encoder.write(3);
         loginop = 3;
      } else if (AutoRegister.getAccountExistsDiscordId(Long.valueOf(discordid))) {
         encoder.write(4);
         loginop = 4;
      } else {
         encoder.write(5);
         loginop = 5;
      }

      encoder.writeMapleAsciiString(discordid);
      encoder.writeMapleAsciiString(phoneNumber);
      encoder.writeMapleAsciiString(chatid);
      if (loginop == 5) {
         encoder.writeMapleAsciiString(id);
         encoder.writeMapleAsciiString(pw);
      }

      DiscordBotHandler.session.writeAndFlush(encoder.getPacket());
   }

   public static void registerAccountResult(PacketDecoder slea) {
      String discordid = slea.readMapleAsciiString();
      String id = slea.readMapleAsciiString();
      String pw = slea.readMapleAsciiString();
      String phoneNumber = slea.readMapleAsciiString();
      String chatid = slea.readMapleAsciiString();
      PacketEncoder encoder = new PacketEncoder();
      encoder.writeShort(101);
      if (AutoRegister.getAccountExists(id)) {
         encoder.write(1);
         encoder.writeMapleAsciiString("가입하려는 ID가 이미 존재하는 ID입니다. 다시 시도해주세요.");
      } else if (AutoRegister.createAccount(id, pw, Long.parseLong(discordid), phoneNumber)) {
         encoder.write(0);
         encoder.writeMapleAsciiString("가입에 성공했습니다.\r\n ID : " + id);
      }

      encoder.writeMapleAsciiString(discordid);
      encoder.writeMapleAsciiString(chatid);
      DiscordBotHandler.session.writeAndFlush(encoder.getPacket());
   }

   public static void connectAccount(PacketDecoder slea) {
      String discordid = slea.readMapleAsciiString();
      String id = slea.readMapleAsciiString();
      String pw = slea.readMapleAsciiString();
      String phoneNumber = slea.readMapleAsciiString();
      String chatid = slea.readMapleAsciiString();
      String pattern2 = "^([0-1]{3})-([0-9]{4})-([0-9]{4})$";
      PacketEncoder encoder = new PacketEncoder();
      encoder.writeShort(104);
      int loginop = 0;
      if (!AutoRegister.getAccountCheckPassword(id, pw)) {
         encoder.write(0);
      } else if (AutoRegister.getAccountExistsDiscordId(Long.valueOf(discordid))) {
         encoder.write(4);
         loginop = 4;
      } else if (Pattern.matches(pattern2, phoneNumber) && !AutoRegister.getAccountExistsPhoneNumber(phoneNumber)) {
         encoder.write(5);
         loginop = 5;
      } else {
         encoder.write(3);
         loginop = 3;
      }

      encoder.writeMapleAsciiString(discordid);
      encoder.writeMapleAsciiString(phoneNumber);
      encoder.writeMapleAsciiString(chatid);
      if (loginop == 5) {
         encoder.writeMapleAsciiString(id);
         encoder.writeMapleAsciiString(pw);
      }

      DiscordBotHandler.session.writeAndFlush(encoder.getPacket());
   }

   public static void connectAccountResult(PacketDecoder slea) {
      String discordid = slea.readMapleAsciiString();
      String id = slea.readMapleAsciiString();
      String pw = slea.readMapleAsciiString();
      String phoneNumber = slea.readMapleAsciiString();
      String chatid = slea.readMapleAsciiString();
      PacketEncoder encoder = new PacketEncoder();
      encoder.writeShort(105);
      if (AutoRegister.updateAccount(id, pw, Long.parseLong(discordid), phoneNumber)) {
         encoder.write(0);
         encoder.writeMapleAsciiString("계정 연결에 성공했습니다.\r\n ID : " + id);
      } else {
         encoder.write(1);
         encoder.writeMapleAsciiString("알 수 없는 오류로 계정 연결에 실패했습니다.");
      }

      encoder.writeMapleAsciiString(discordid);
      encoder.writeMapleAsciiString(chatid);
      DiscordBotHandler.session.writeAndFlush(encoder.getPacket());
   }
}
