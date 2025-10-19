package network.connector;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.encode.PacketEncoder;

public class PacketCreator {
   public static byte[] sShiftKey = new byte[]{
      -97, -109, 26, 101, 57, -95, -106, 76, 19, -74, -89, 34, 33, -101, 119, -121, -26, 99, 69, -128, -109, 78, 69, -104, 11, 52, -4, 108, 48, 29, 100, 126
   };

   public static final byte[] sendHandShake(byte[] sendiv, byte[] recviv) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.encodeBuffer(sendiv);
      mplew.encodeBuffer(recviv);
      byte[] qwer = mplew.getPacket();

      for (int a = 0; a < mplew.getPacket().length; a++) {
         qwer[a] ^= sShiftKey[a % 32];
      }

      return qwer;
   }

   public static final byte[] sendProcEnd(String prn) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.write(SendPacketOpcode.KillProcess.getValue());
      mplew.writeMapleAsciiString2(prn);
      return mplew.getPacket();
   }

   public static final byte[] sendCharInfo(String name, int id) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.write((byte)SendPacketOpcode.CharacterInfo.getValue());
      mplew.write(name != null);
      if (name != null) {
         mplew.writeInt(id);
         mplew.writeMapleAsciiString2(name);
      }

      return mplew.getPacket();
   }

   public static final byte[] sendProcessKillList(ConnectorClient c) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.write((byte)SendPacketOpcode.BlacklistProcessList.getValue());

      try {
         byte[] a = c.processKillList().getBytes("UTF-8");
         mplew.writeShort(a.length);
         mplew.encodeBuffer(a);
      } catch (UnsupportedEncodingException var3) {
         Logger.getLogger(PacketCreator.class.getName()).log(Level.SEVERE, null, var3);
      }

      return mplew.getPacket();
   }

   public static byte[] SendCharacterList(int accountId) {
      PacketEncoder mplew = new PacketEncoder();
      Map<Integer, String> map = ConnectorServerHandler.GetCharacterList(accountId);
      mplew.write((byte)SendPacketOpcode.CharacterListResponse.getValue());
      mplew.writeInt(map.size());
      map.forEach((id, name) -> {
         mplew.writeInt(id);
         mplew.writeMapleAsciiString2(name);
      });
      return mplew.getPacket();
   }
}
