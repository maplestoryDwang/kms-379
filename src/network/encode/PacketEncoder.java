package network.encode;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import network.models.BufferbitFlag;
import objects.utils.HexTool;

public class PacketEncoder {
   private final ByteArrayOutputStream baos;
   private static final Charset ASCII = Charset.forName("MS949");

   public PacketEncoder() {
      this(32);
   }

   public PacketEncoder(int size) {
      this.baos = new ByteArrayOutputStream(size);
   }

   public final byte[] getPacket() {
      return this.baos.toByteArray();
   }

   @Override
   public final String toString() {
      return HexTool.toString(this.baos.toByteArray());
   }

   public final void writeZeroBytes(int i) {
      for (int x = 0; x < i; x++) {
         this.baos.write(0);
      }
   }

   public final void encodeBuffer(byte[] b) {
      for (int x = 0; x < b.length; x++) {
         this.baos.write(b[x]);
      }
   }

   public final void write(boolean b) {
      this.baos.write(b ? 1 : 0);
   }

   public void write(byte b) {
      this.baos.write(b);
   }

   public void write(int b) {
      if (b != -88888) {
         this.baos.write((byte)b);
      }
   }

   public final void writeShort(int i) {
      this.baos.write((byte)(i & 0xFF));
      this.baos.write((byte)(i >>> 8 & 0xFF));
   }

   public final void writeInt(int i) {
      if (i != -88888) {
         this.baos.write((byte)(i & 0xFF));
         this.baos.write((byte)(i >>> 8 & 0xFF));
         this.baos.write((byte)(i >>> 16 & 0xFF));
         this.baos.write((byte)(i >>> 24 & 0xFF));
      }
   }

   public void writeInt(long i) {
      this.baos.write((byte)(i & 255L));
      this.baos.write((byte)(i >>> 8 & 255L));
      this.baos.write((byte)(i >>> 16 & 255L));
      this.baos.write((byte)(i >>> 24 & 255L));
   }

   public final void writeMapleAsciiString_(String s) {
      this.encodeBuffer(s.getBytes(ASCII));
   }

   public final void writeMapleAsciiString_(String s, int max) {
      if (s.getBytes(ASCII).length > max) {
         s = s.substring(0, max);
      }

      this.encodeBuffer(s.getBytes(ASCII));

      for (int i = s.getBytes(ASCII).length; i < max; i++) {
         this.write(0);
      }
   }

   public final void writeMapleAsciiString(String s) {
      this.writeShort((short)s.getBytes(ASCII).length);
      this.writeMapleAsciiString_(s);
   }

   public void writeNullTerminatedAsciiString(String s) {
      this.writeMapleAsciiString_(s);
      this.write(0);
   }

   public final void writeMapleAsciiString2(String s) {
      this.writeShort((short)s.getBytes(StandardCharsets.UTF_8).length);
      this.encodeBuffer(s.getBytes(StandardCharsets.UTF_8));
   }

   public final void encodePos(Point s) {
      this.writeShort(s.x);
      this.writeShort(s.y);
   }

   public void encodePos4Byte(Point s) {
      this.writeInt(s.x);
      this.writeInt(s.y);
   }

   public final void encodeRect(Rectangle s) {
      this.writeInt(s.x);
      this.writeInt(s.y);
      this.writeInt(s.x + s.width);
      this.writeInt(s.y + s.height);
   }

   public final void writeLong(long l) {
      this.baos.write((byte)(l & 255L));
      this.baos.write((byte)(l >>> 8 & 255L));
      this.baos.write((byte)(l >>> 16 & 255L));
      this.baos.write((byte)(l >>> 24 & 255L));
      this.baos.write((byte)(l >>> 32 & 255L));
      this.baos.write((byte)(l >>> 40 & 255L));
      this.baos.write((byte)(l >>> 48 & 255L));
      this.baos.write((byte)(l >>> 56 & 255L));
   }

   public final void writeReversedLong(long l) {
      this.baos.write((byte)(l >>> 32 & 255L));
      this.baos.write((byte)(l >>> 40 & 255L));
      this.baos.write((byte)(l >>> 48 & 255L));
      this.baos.write((byte)(l >>> 56 & 255L));
      this.baos.write((byte)(l & 255L));
      this.baos.write((byte)(l >>> 8 & 255L));
      this.baos.write((byte)(l >>> 16 & 255L));
      this.baos.write((byte)(l >>> 24 & 255L));
   }

   public final void encodeDouble(double d) {
      this.writeDouble(d);
   }

   public final void writeDouble(double d) {
      this.writeLong(Double.doubleToLongBits(d));
   }

   public final void flagBuffer(List<BufferbitFlag> flag) {
      this.encodeBuffer(BufferbitFlag.getValueArray(flag));
   }

   public final void flagBuffer(BufferbitFlag flag) {
      this.encodeBuffer(BufferbitFlag.getValueArray(flag));
   }
}
