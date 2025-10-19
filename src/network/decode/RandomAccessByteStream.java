package network.decode;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessByteStream implements SeekableInputStreamBytestream {
   private final RandomAccessFile raf;
   private long read = 0L;

   public RandomAccessByteStream(RandomAccessFile raf) {
      this.raf = raf;
   }

   @Override
   public final int readByte() {
      try {
         int temp = this.raf.read();
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
   public final void seek(long offset) throws IOException {
      this.raf.seek(offset);
   }

   @Override
   public final long getPosition() throws IOException {
      return this.raf.getFilePointer();
   }

   @Override
   public final long getBytesRead() {
      return this.read;
   }

   @Override
   public final long available() {
      try {
         return this.raf.length() - this.raf.getFilePointer();
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
