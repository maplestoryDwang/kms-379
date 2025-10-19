package objects.utils;

public class BitTools {
   public static final int getShort(byte[] array, int index) {
      int ret = array[index];
      ret &= 255;
      return ret | array[index + 1] << 8 & 0xFF00;
   }

   public static final String getString(byte[] array, int index, int length) {
      char[] cret = new char[length];

      for (int x = 0; x < length; x++) {
         cret[x] = (char)array[x + index];
      }

      return String.valueOf(cret);
   }

   public static final String getMapleString(byte[] array, int index) {
      int length = array[index] & 255 | array[index + 1] << 8 & 0xFF00;
      return getString(array, index + 2, length);
   }

   public static final byte rollLeft(byte in, int count) {
      int tmp = in & 255;
      tmp <<= count % 8;
      return (byte)(tmp & 0xFF | tmp >> 8);
   }

   public static final byte rollRight(byte in, int count) {
      int tmp = in & 255;
      tmp = tmp << 8 >>> count % 8;
      return (byte)(tmp & 0xFF | tmp >>> 8);
   }

   public static final byte[] multiplyBytes(byte[] in, int count, int mul) {
      byte[] ret = new byte[count * mul];

      for (int x = 0; x < count * mul; x++) {
         ret[x] = in[x % count];
      }

      return ret;
   }

   public static final int doubleToShortBits(double d) {
      long l = Double.doubleToLongBits(d);
      return (int)(l >> 48);
   }
}
