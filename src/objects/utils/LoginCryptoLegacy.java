package objects.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class LoginCryptoLegacy {
   private static final Random rand = new Random();
   private static final char[] iota64 = new char[64];

   public static final String hashPassword(String password) {
      byte[] randomBytes = new byte[6];
      rand.setSeed(System.currentTimeMillis());
      rand.nextBytes(randomBytes);
      return myCrypt(password, genSalt(randomBytes));
   }

   public static final boolean checkPassword(String password, String hash) {
      return myCrypt(password, hash).equals(hash);
   }

   public static final boolean isLegacyPassword(String hash) {
      return hash.substring(0, 3).equals("$H$");
   }

   private static final String myCrypt(String password, String seed) throws RuntimeException {
      String out = null;
      int count = 8;
      if (!seed.substring(0, 3).equals("$H$")) {
         byte[] randomBytes = new byte[6];
         rand.nextBytes(randomBytes);
         seed = genSalt(randomBytes);
      }

      String salt = seed.substring(4, 12);
      if (salt.length() != 8) {
         throw new RuntimeException("Error hashing password - Invalid seed.");
      } else {
         try {
            MessageDigest digester = MessageDigest.getInstance("SHA-1");
            digester.update((salt + password).getBytes("iso-8859-1"), 0, (salt + password).length());
            byte[] sha1Hash = digester.digest();

            do {
               byte[] CombinedBytes = new byte[sha1Hash.length + password.length()];
               System.arraycopy(sha1Hash, 0, CombinedBytes, 0, sha1Hash.length);
               System.arraycopy(password.getBytes("iso-8859-1"), 0, CombinedBytes, sha1Hash.length, password.getBytes("iso-8859-1").length);
               digester.update(CombinedBytes, 0, CombinedBytes.length);
               sha1Hash = digester.digest();
            } while (--count > 0);

            out = seed.substring(0, 12);
            out = out + encode64(sha1Hash);
         } catch (NoSuchAlgorithmException var8) {
            System.err.println("Error hashing password." + var8);
         } catch (UnsupportedEncodingException var9) {
            System.err.println("Error hashing password." + var9);
         }

         if (out == null) {
            throw new RuntimeException("Error hashing password - out = null");
         } else {
            return out;
         }
      }
   }

   private static final String genSalt(byte[] Random) {
      StringBuilder Salt = new StringBuilder("$H$");
      Salt.append(iota64[30]);
      Salt.append(encode64(Random));
      return Salt.toString();
   }

   private static final String convertToHex(byte[] data) {
      StringBuffer buf = new StringBuffer();

      for (int i = 0; i < data.length; i++) {
         int halfbyte = data[i] >>> 4 & 15;
         int two_halfs = 0;

         do {
            if (0 <= halfbyte && halfbyte <= 9) {
               buf.append((char)(48 + halfbyte));
            } else {
               buf.append((char)(97 + (halfbyte - 10)));
            }

            halfbyte = data[i] & 15;
         } while (two_halfs++ >= 1);
      }

      return buf.toString();
   }

   public static final String encodeSHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      md.update(text.getBytes("iso-8859-1"), 0, text.length());
      return convertToHex(md.digest());
   }

   private static final String encode64(byte[] Input) {
      int iLen = Input.length;
      int oDataLen = (iLen * 4 + 2) / 3;
      int oLen = (iLen + 2) / 3 * 4;
      char[] out = new char[oLen];
      int ip = 0;

      for (int op = 0; ip < iLen; op++) {
         int i0 = Input[ip++] & 255;
         int i1 = ip < iLen ? Input[ip++] & 255 : 0;
         int i2 = ip < iLen ? Input[ip++] & 255 : 0;
         int o0 = i0 >>> 2;
         int o1 = (i0 & 3) << 4 | i1 >>> 4;
         int o2 = (i1 & 15) << 2 | i2 >>> 6;
         int o3 = i2 & 63;
         out[op++] = iota64[o0];
         out[op++] = iota64[o1];
         out[op] = op < oDataLen ? iota64[o2] : 61;
         out[++op] = op < oDataLen ? iota64[o3] : 61;
      }

      return new String(out);
   }

   static {
      int i = 0;
      iota64[i++] = '.';
      iota64[i++] = '/';
      char c = 'A';

      while (c <= 'Z') {
         iota64[i++] = c++;
      }

      c = 'a';

      while (c <= 'z') {
         iota64[i++] = c++;
      }

      c = '0';

      while (c <= '9') {
         iota64[i++] = c++;
      }
   }
}
