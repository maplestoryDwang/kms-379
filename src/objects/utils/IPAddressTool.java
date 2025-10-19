package objects.utils;

public class IPAddressTool {
   public static final long dottedQuadToLong(String dottedQuad) throws RuntimeException {
      String[] quads = dottedQuad.split("\\.");
      if (quads.length != 4) {
         throw new RuntimeException("Invalid IP Address format.");
      } else {
         long ipAddress = 0L;

         for (int i = 0; i < 4; i++) {
            ipAddress += Integer.parseInt(quads[i]) % 256 * (long)Math.pow(256.0, 4 - i);
         }

         return ipAddress;
      }
   }

   public static final String longToDottedQuad(long longIP) throws RuntimeException {
      StringBuilder ipAddress = new StringBuilder();

      for (int i = 0; i < 4; i++) {
         int quad = (int)(longIP / (long)Math.pow(256.0, 4 - i));
         longIP -= quad * (long)Math.pow(256.0, 4 - i);
         if (i > 0) {
            ipAddress.append(".");
         }

         if (quad > 255) {
            throw new RuntimeException("Invalid long IP address.");
         }

         ipAddress.append(quad);
      }

      return ipAddress.toString();
   }
}
