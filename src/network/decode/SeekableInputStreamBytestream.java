package network.decode;

import java.io.IOException;

public interface SeekableInputStreamBytestream extends ByteInputStream {
   void seek(long var1) throws IOException;

   long getPosition() throws IOException;

   @Override
   String toString(boolean var1);
}
