package network.encode;

import java.awt.Point;

public interface LittleEndianWriter {
   void writeZeroBytes(int var1);

   void write(byte[] var1);

   void write(byte var1);

   void write(int var1);

   void writeInt(int var1);

   void writeShort(short var1);

   void writeShort(int var1);

   void writeLong(long var1);

   void writeAsciiString(String var1);

   void writeAsciiString(String var1, int var2);

   void writePos(Point var1);

   void writeMapleAsciiString(String var1);
}
