package network.encode;

import java.awt.Point;
import java.nio.charset.Charset;

public class GenericLittleEndianWriter implements LittleEndianWriter {
   private static final Charset ASCII = Charset.forName("MS949");
   private IoOutputStream bos;

   protected GenericLittleEndianWriter() {
   }

   protected void setByteOutputStream(IoOutputStream bos) {
      this.bos = bos;
   }

   public GenericLittleEndianWriter(IoOutputStream bos) {
      this.bos = bos;
   }

   @Override
   public final void writeZeroBytes(int i) {
      for (int x = 0; x < i; x++) {
         this.bos.writeByte((byte)0);
      }
   }

   @Override
   public final void write(byte[] b) {
      for (int x = 0; x < b.length; x++) {
         this.bos.writeByte(b[x]);
      }
   }

   @Override
   public final void write(byte b) {
      this.bos.writeByte(b);
   }

   @Override
   public final void write(int b) {
      this.bos.writeByte((byte)b);
   }

   @Override
   public final void writeShort(short i) {
      this.bos.writeByte((byte)(i & 255));
      this.bos.writeByte((byte)(i >>> 8 & 0xFF));
   }

   @Override
   public final void writeShort(int i) {
      this.bos.writeByte((byte)(i & 0xFF));
      this.bos.writeByte((byte)(i >>> 8 & 0xFF));
   }

   @Override
   public final void writeInt(int i) {
      this.bos.writeByte((byte)(i & 0xFF));
      this.bos.writeByte((byte)(i >>> 8 & 0xFF));
      this.bos.writeByte((byte)(i >>> 16 & 0xFF));
      this.bos.writeByte((byte)(i >>> 24 & 0xFF));
   }

   @Override
   public final void writeAsciiString(String s) {
      this.write(s.getBytes(ASCII));
   }

   @Override
   public final void writeAsciiString(String s, int max) {
      if (s.getBytes(ASCII).length > max) {
         s = s.substring(0, max);
      }

      this.write(s.getBytes(ASCII));

      for (int i = s.getBytes(ASCII).length; i < max; i++) {
         this.write(0);
      }
   }

   @Override
   public final void writeMapleAsciiString(String s) {
      this.writeShort((short)s.getBytes(ASCII).length);
      this.writeAsciiString(s);
   }

   @Override
   public final void writePos(Point s) {
      this.writeShort(s.x);
      this.writeShort(s.y);
   }

   @Override
   public final void writeLong(long l) {
      this.bos.writeByte((byte)(l & 255L));
      this.bos.writeByte((byte)(l >>> 8 & 255L));
      this.bos.writeByte((byte)(l >>> 16 & 255L));
      this.bos.writeByte((byte)(l >>> 24 & 255L));
      this.bos.writeByte((byte)(l >>> 32 & 255L));
      this.bos.writeByte((byte)(l >>> 40 & 255L));
      this.bos.writeByte((byte)(l >>> 48 & 255L));
      this.bos.writeByte((byte)(l >>> 56 & 255L));
   }
}
