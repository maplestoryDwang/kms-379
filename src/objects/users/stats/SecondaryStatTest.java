package objects.users.stats;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import objects.utils.HexTool;

public class SecondaryStatTest {
   public static void main(String[] args) {
      System.out.println("------------SnippingValue--------------");
      h(
         "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 20 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 7D 2E DB 01 20 4E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 01 01 00 00 00 00 02"
      );
      System.out.println("------------Server--------------");
      h(
         "06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 FE E3 21 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 FE E3 21 00 14 00 00 00 5A A1 FB 6F 00 00 00 00 40 1F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 FE E3 21 00 14 00 00 00 5A A1 FB 6F 00 00 00 00 40 1F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 A4 AE 85 00 01 00 00 00 00 00 00 00 01 01 01 00 00 00 00 01"
      );
   }

   public static void h(String hex) {
      byte[] bytes = HexTool.getByteArrayFromHexString(hex);
      ByteBuffer b = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);

      for (int i = 0; i < 31; i++) {
         int dat = b.getInt();

         for (int j = 0; j < 32; j++) {
            if ((dat & 1) != 0) {
               String name = "UNKNOWN";
               int ps = 31 - i;
               int vl = 1 << j;

               for (SecondaryStatFlag ssf : SecondaryStatFlag.values()) {
                  if (ssf.getBit() == 31 - j + 32 * i) {
                     name = ssf.name();
                     break;
                  }
               }

               System.out.printf("pos: %d, bit: %d, value: 0x%02X [%s]\n", ps, 31 - j + 32 * i, vl, name);
            }

            dat >>>= 1;
         }
      }
   }

   public static void h2(byte[] bytes) {
      ByteBuffer b = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);

      for (int i = 0; i < 31; i++) {
         int dat = b.getInt();

         for (int j = 0; j < 32; j++) {
            if ((dat & 1) != 0) {
               String name = "UNKNOWN";
               int ps = 31 - i;
               int vl = 1 << j;

               for (SecondaryStatFlag ssf : SecondaryStatFlag.values()) {
                  if (ssf.getBit() == 31 - j + 32 * i) {
                     name = ssf.name();
                     break;
                  }
               }

               System.out.printf("pos: %d, bit: %d, value: 0x%02X [%s]\n", ps, 31 - j + 32 * i, vl, name);
            }

            dat >>>= 1;
         }
      }
   }

   public static void BitToHex(int bit) {
      int position = 30 - bit / 32;
      int a = 31 - bit % 32;
      int b = a == 0 ? 1 : sqr(2, a);
      String hex = String.format("%02X%n", b);
      System.out.println("bit : " + bit + ", " + position + " 0x" + hex);
   }

   public static int sqr(int x, int b) {
      int result = x;
      if (x != 0) {
         for (int i = 1; i < b; i++) {
            result *= x;
         }
      }

      return result;
   }
}
