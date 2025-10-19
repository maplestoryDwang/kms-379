package objects.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.apache.mina.core.buffer.IoBuffer;

public class HexTool {
   private static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

   public static final String toString(byte byteValue) {
      int tmp = byteValue << 8;
      char[] retstr = new char[]{HEX[tmp >> 12 & 15], HEX[tmp >> 8 & 15]};
      return String.valueOf(retstr);
   }

   public static final String toString(IoBuffer buf) {
      buf.flip();
      byte[] arr = new byte[buf.remaining()];
      buf.get(arr);
      String ret = toString(arr);
      buf.flip();
      buf.put(arr);
      return ret;
   }

   public static final String toString(int intValue) {
      return Integer.toHexString(intValue);
   }

   public static final String toString(byte[] bytes) {
      StringBuilder hexed = new StringBuilder();

      for (int i = 0; i < bytes.length; i++) {
         hexed.append(toString(bytes[i]));
         hexed.append(' ');
      }

      return hexed.substring(0, hexed.length() - 1);
   }

   public static final String toStringFromAscii(byte[] bytes) {
      byte[] ret = new byte[bytes.length];

      for (int x = 0; x < bytes.length; x++) {
         if (bytes[x] < 32 && bytes[x] >= 0) {
            ret[x] = 46;
         } else {
            int chr = bytes[x] & 255;
            ret[x] = (byte)chr;
         }
      }

      try {
         return new String(ret, "MS949");
      } catch (Exception var4) {
         System.out.println("ASCII Error");
         var4.printStackTrace();
         return null;
      }
   }

   public static final String toPaddedStringFromAscii(byte[] bytes) {
      String str = toStringFromAscii(bytes);
      StringBuilder ret = new StringBuilder(str.length() * 3);

      for (int i = 0; i < str.length(); i++) {
         ret.append(str.charAt(i));
         ret.append("  ");
      }

      return ret.toString();
   }

   public static byte[] getByteArrayFromHexString(String hex) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int nexti = 0;
      int nextb = 0;
      boolean highoc = true;

      while (true) {
         int number;
         for (number = -1; number == -1; nexti++) {
            if (nexti == hex.length()) {
               return baos.toByteArray();
            }

            char chr = hex.charAt(nexti);
            if (chr >= '0' && chr <= '9') {
               number = chr - '0';
            } else if (chr >= 'a' && chr <= 'f') {
               number = chr - 'a' + 10;
            } else if (chr >= 'A' && chr <= 'F') {
               number = chr - 'A' + 10;
            } else {
               number = -1;
            }
         }

         if (highoc) {
            nextb = number << 4;
            highoc = false;
         } else {
            nextb |= number;
            highoc = true;
            baos.write(nextb);
         }
      }
   }

   public static final String getOpcodeToString(int op) {
      return "0x" + StringUtil.getLeftPaddedStr(Integer.toHexString(op).toUpperCase(), '0', 4);
   }

   public static String hexToString(String s) {
      try {
         String string = s.split("Data:")[1].replaceAll(" ", "");
         String s1 = "0123456789ABCDEF";
         int i = string.length();
         boolean flag = false;
         if (i % 2 != 0) {
            return null;
         } else {
            byte[] abyte0 = new byte[i / 2];

            for (int j = 0; j < i / 2; j++) {
               int k = s1.indexOf(string.charAt(j * 2));
               if (k < 0) {
                  return null;
               }

               abyte0[j] = (byte)(k * 16);
               k = s1.indexOf(string.charAt(j * 2 + 1));
               if (k < 0) {
                  return null;
               }

               abyte0[j] = (byte)(abyte0[j] + k);
            }

            return new String(abyte0, "euc-kr");
         }
      } catch (Exception var8) {
         return "";
      }
   }

   public static String getHexString(ByteBuffer bb) {
      String str = "";

      while (bb.hasRemaining()) {
         String d = Integer.toHexString(bb.get() & 255);

         while (d.length() < 2) {
            d = "0" + d;
         }

         if (d.length() > 2) {
            d = d.substring(0, 2);
         }

         str = str + d.toUpperCase() + " ";
      }

      if (bb.limit() > 0) {
         str = str.substring(0, str.length() - 1);
      }

      return str;
   }

   public static byte[] getByteArrayFromImage(BufferedImage image) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try {
         ImageIO.write(image, "jpg", baos);
      } catch (IOException var3) {
         System.out.println("Error on writing image" + var3);
      }

      return baos.toByteArray();
   }

   public static int byteArrayToInt(byte[] bytes, int index) {
      return (0xFF & bytes[index]) << 32 | (0xFF & bytes[index + 1]) << 40 | (0xFF & bytes[index + 2]) << 48 | (0xFF & bytes[index + 3]) << 56;
   }
}
