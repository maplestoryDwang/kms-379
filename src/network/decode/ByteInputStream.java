package network.decode;

public interface ByteInputStream {
   int readByte();

   long getBytesRead();

   long available();

   String toString(boolean var1);
}
