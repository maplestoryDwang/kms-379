package network.decode;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamByteStream implements ByteInputStream {
   private final InputStream is;
   private long read = 0L;

   public InputStreamByteStream(InputStream is) {
      this.is = is;
   }

   @Override
   public final int readByte() {
      try {
         int temp = this.is.read();
         if (temp == -1) {
            throw new RuntimeException("EOF");
         } else {
            this.read++;
            return temp;
         }
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   @Override
   public final long getBytesRead() {
      return this.read;
   }

   @Override
   public final long available() {
      try {
         return this.is.available();
      } catch (IOException var2) {
         System.err.println("ERROR" + var2);
         return 0L;
      }
   }

   @Override
   public final String toString(boolean b) {
      return this.toString();
   }
}
