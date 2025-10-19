package objects.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import network.encode.PacketEncoder;

public class MapleAESOFB {
   private byte[] iv;
   private Cipher cipher;
   private short mapleVersion;
   private static final SecretKeySpec skey = new SecretKeySpec(
      new byte[]{94, 0, 0, 0, -100, 0, 0, 0, -97, 0, 0, 0, 12, 0, 0, 0, 101, 0, 0, 0, -119, 0, 0, 0, 122, 0, 0, 0, 99, 0, 0, 0}, "AES"
   );
   private static final byte[] funnyBytes = new byte[]{
      -20,
      63,
      119,
      -92,
      69,
      -48,
      113,
      -65,
      -73,
      -104,
      32,
      -4,
      75,
      -23,
      -77,
      -31,
      92,
      34,
      -9,
      12,
      68,
      27,
      -127,
      -67,
      99,
      -115,
      -44,
      -61,
      -14,
      16,
      25,
      -32,
      -5,
      -95,
      110,
      102,
      -22,
      -82,
      -42,
      -50,
      6,
      24,
      78,
      -21,
      120,
      -107,
      -37,
      -70,
      -74,
      66,
      122,
      42,
      -125,
      11,
      84,
      103,
      109,
      -24,
      101,
      -25,
      47,
      7,
      -13,
      -86,
      39,
      123,
      -123,
      -80,
      38,
      -3,
      -117,
      -87,
      -6,
      -66,
      -88,
      -41,
      -53,
      -52,
      -110,
      -38,
      -7,
      -109,
      96,
      45,
      -35,
      -46,
      -94,
      -101,
      57,
      95,
      -126,
      33,
      76,
      105,
      -8,
      49,
      -121,
      -18,
      -114,
      -83,
      -116,
      106,
      -68,
      -75,
      107,
      89,
      19,
      -15,
      4,
      0,
      -10,
      90,
      53,
      121,
      72,
      -113,
      21,
      -51,
      -105,
      87,
      18,
      62,
      55,
      -1,
      -99,
      79,
      81,
      -11,
      -93,
      112,
      -69,
      20,
      117,
      -62,
      -72,
      114,
      -64,
      -19,
      125,
      104,
      -55,
      46,
      13,
      98,
      70,
      23,
      17,
      77,
      108,
      -60,
      126,
      83,
      -63,
      37,
      -57,
      -102,
      28,
      -120,
      88,
      44,
      -119,
      -36,
      2,
      100,
      64,
      1,
      93,
      56,
      -91,
      -30,
      -81,
      85,
      -43,
      -17,
      26,
      124,
      -89,
      91,
      -90,
      111,
      -122,
      -97,
      115,
      -26,
      10,
      -34,
      43,
      -103,
      74,
      71,
      -100,
      -33,
      9,
      118,
      -98,
      48,
      14,
      -28,
      -78,
      -108,
      -96,
      59,
      52,
      29,
      40,
      15,
      54,
      -29,
      35,
      -76,
      3,
      -40,
      -112,
      -56,
      60,
      -2,
      94,
      50,
      36,
      80,
      31,
      58,
      67,
      -118,
      -106,
      65,
      116,
      -84,
      82,
      51,
      -16,
      -39,
      41,
      -128,
      -79,
      22,
      -45,
      -85,
      -111,
      -71,
      -124,
      127,
      97,
      30,
      -49,
      -59,
      -47,
      86,
      61,
      -54,
      -12,
      5,
      -58,
      -27,
      8,
      73
   };

   public MapleAESOFB(byte[] iv, short mapleVersion) {
      try {
         this.cipher = Cipher.getInstance("AES");
         this.cipher.init(1, skey);
      } catch (NoSuchAlgorithmException var4) {
         System.err.println("ERROR" + var4);
      } catch (NoSuchPaddingException var5) {
         System.err.println("ERROR" + var5);
      } catch (InvalidKeyException var6) {
         System.err.println("Error initalizing the encryption cipher.  Make sure you're using the Unlimited Strength cryptography jar files.");
      }

      this.setIv(iv);
      this.mapleVersion = (short)(mapleVersion >> 8 & 0xFF | mapleVersion << 8 & 0xFF00);
   }

   private void setIv(byte[] iv) {
      this.iv = iv;
   }

   public byte[] getIv() {
      return this.iv;
   }

   public byte[] newcrypt(byte[] data) {
      int remaining = data.length;
      byte[] datac = new byte[remaining];
      System.arraycopy(data, 0, datac, 0, data.length);

      try {
         for (int i = 0; i < remaining; i++) {
            datac[i] = (byte)(data[i] + this.iv[0]);
         }

         this.updateIv();
      } catch (Exception var5) {
         System.out.println("NewCrypt Err");
         var5.printStackTrace();
      }

      return datac;
   }

   public byte[] crypt(byte[] data) {
      int remaining = data.length;
      int llength = 1456;
      int start = 0;

      try {
         while (remaining > 0) {
            byte[] myIv = BitTools.multiplyBytes(this.iv, 4, 4);
            if (remaining < llength) {
               llength = remaining;
            }

            for (int x = start; x < start + llength; x++) {
               if ((x - start) % myIv.length == 0) {
                  byte[] newIv = this.cipher.doFinal(myIv);
                  System.arraycopy(newIv, 0, myIv, 0, myIv.length);
               }

               data[x] ^= myIv[(x - start) % myIv.length];
            }

            start += llength;
            remaining -= llength;
            llength = 1460;
         }

         this.updateIv();
      } catch (IllegalBlockSizeException var8) {
         var8.printStackTrace();
      } catch (BadPaddingException var9) {
         var9.printStackTrace();
      }

      return data;
   }

   private void updateIv() {
      this.iv = getNewIv(this.iv);
   }

   public byte[] getPacketHeader(int length) {
      PacketEncoder packet = new PacketEncoder();
      int uSeqSnd = (this.iv[2] & 255 | this.iv[3] << 8) & 65535;
      uSeqSnd ^= 65156;
      packet.writeShort((short)uSeqSnd);
      if (length >= 65280) {
         packet.writeShort((short)(65280 ^ uSeqSnd));
         packet.writeInt(length ^ uSeqSnd);
      } else {
         packet.writeShort((short)(length ^ uSeqSnd));
      }

      return packet.getPacket();
   }

   public static int getPacketLength(int packetHeader) {
      int packetLength = packetHeader >>> 16 ^ packetHeader & 65535;
      return packetLength << 8 & 0xFF00 | packetLength >>> 8 & 0xFF;
   }

   public static int getPacketLength(byte[] header) {
      return (header[0] ^ header[2]) & 0xFF | (header[1] ^ header[3]) << 8 & 0xFF00;
   }

   public boolean checkPacket(byte[] packet) {
      int t = Math.abs(Math.abs(packet[1]) - Math.abs(packet[3]));
      if (t < 0 || t > 5) {
         System.out.println("checkPacket 실패 (오차범위 : " + t + ")");
      }

      return t >= 0 && t <= 5;
   }

   public boolean checkPacket(int packetHeader) {
      return this.checkPacket(new byte[]{(byte)(packetHeader >> 24 & 0xFF), (byte)(packetHeader >> 16 & 0xFF)});
   }

   public static byte[] getNewIv(byte[] oldIv) {
      byte[] in = new byte[]{-14, 83, 80, -58};

      for (int x = 0; x < 4; x++) {
         funnyShit(oldIv[x], in);
      }

      return in;
   }

   @Override
   public String toString() {
      return "IV: " + HexTool.toString(this.iv);
   }

   public static final void funnyShit(byte inputByte, byte[] in) {
      byte elina = in[1];
      byte moritz = funnyBytes[elina & 255];
      moritz = (byte)(moritz - inputByte);
      in[0] += moritz;
      moritz = in[2];
      moritz = (byte)(moritz ^ funnyBytes[inputByte & 255]);
      elina = (byte)(elina - (moritz & 255));
      in[1] = elina;
      elina = in[3];
      byte var9 = (byte)(elina - (in[0] & 255));
      moritz = funnyBytes[elina & 255];
      moritz = (byte)(moritz + inputByte);
      moritz = (byte)(moritz ^ in[2]);
      in[2] = moritz;
      elina = (byte)(var9 + (funnyBytes[inputByte & 255] & 255));
      in[3] = elina;
      int merry = in[0] & 255;
      merry |= in[1] << 8 & 0xFF00;
      merry |= in[2] << 16 & 0xFF0000;
      merry |= in[3] << 24 & 0xFF000000;
      int ret_value = merry >>> 29;
      merry <<= 3;
      ret_value |= merry;
      in[0] = (byte)(ret_value & 0xFF);
      in[1] = (byte)(ret_value >> 8 & 0xFF);
      in[2] = (byte)(ret_value >> 16 & 0xFF);
      in[3] = (byte)(ret_value >> 24 & 0xFF);
   }
}
