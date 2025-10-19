package network.decode;

import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PacketDecoder {
   private final ByteArrayByteStream bs;

   public PacketDecoder(ByteArrayByteStream bs) {
      this.bs = bs;
   }

   public final byte[] getByteArray() {
      return this.bs.getByteArray();
   }

   public final byte readByte() {
      return (byte)this.bs.readByte();
   }

   public final int readByteToInt() {
      return this.bs.readByte() & 0xFF;
   }

   public final int readInt() {
      int byte1 = this.bs.readByte();
      int byte2 = this.bs.readByte();
      int byte3 = this.bs.readByte();
      int byte4 = this.bs.readByte();
      return (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
   }

   public final short readShort() {
      int byte1 = this.bs.readByte();
      int byte2 = this.bs.readByte();
      return (short)((byte2 << 8) + byte1);
   }

   public final int readUShort() {
      int quest = this.readShort();
      if (quest < 0) {
         quest += 65536;
      }

      return quest;
   }

   public final char readChar() {
      return (char)this.readShort();
   }

   public final long readLong() {
      long byte1 = this.bs.readByte();
      long byte2 = this.bs.readByte();
      long byte3 = this.bs.readByte();
      long byte4 = this.bs.readByte();
      long byte5 = this.bs.readByte();
      long byte6 = this.bs.readByte();
      long byte7 = this.bs.readByte();
      long byte8 = this.bs.readByte();
      return (byte8 << 56) + (byte7 << 48) + (byte6 << 40) + (byte5 << 32) + (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
   }

   public final float readFloat() {
      return Float.intBitsToFloat(this.readInt());
   }

   public final double readDouble() {
      return Double.longBitsToDouble(this.readLong());
   }

   public String readAsciiString(int n) {
      byte[] ret = new byte[n];

      for (int x = 0; x < n; x++) {
         ret[x] = this.readByte();
      }

      return new String(ret, Charset.forName("MS949"));
   }

   public final String readNullTerminatedAsciiString() {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      while (true) {
         byte b = this.readByte();
         if (b == 0) {
            byte[] buf = baos.toByteArray();
            char[] chrBuf = new char[buf.length];

            for (int x = 0; x < buf.length; x++) {
               chrBuf[x] = (char)buf[x];
            }

            return String.valueOf(chrBuf);
         }

         baos.write(b);
      }
   }

   public final long getBytesRead() {
      return this.bs.getBytesRead();
   }

   public final String readMapleAsciiString() {
      return this.readAsciiString(this.readShort());
   }

   public final Point readPos() {
      int x = this.readShort();
      int y = this.readShort();
      return new Point(x, y);
   }

   public final Point readPosInt() {
      int x = this.readInt();
      int y = this.readInt();
      return new Point(x, y);
   }

   public final byte[] read(int num) {
      byte[] ret = new byte[num];

      for (int x = 0; x < num; x++) {
         ret[x] = this.readByte();
      }

      return ret;
   }

   public final long available() {
      return this.bs.available();
   }

   @Override
   public final String toString() {
      return this.bs.toString();
   }

   public final String toString(boolean b) {
      return this.bs.toString(b);
   }

   public final void seek(long offset) {
      try {
         this.bs.seek(offset);
      } catch (IOException var4) {
         System.err.println("Seek failed" + var4);
      }
   }

   public final long getPosition() {
      return this.bs.getPosition();
   }

   public final void skip(int num) {
      this.seek(this.getPosition() + num);
   }

   public final String readAsciiString2(int n) {
      byte[] ret = new byte[n];

      for (int x = 0; x < n; x++) {
         ret[x] = this.readByte();
      }

      return new String(ret, StandardCharsets.UTF_8);
   }

   public final String readMapleAsciiString2() {
      return this.readAsciiString2(this.readShort());
   }
}
