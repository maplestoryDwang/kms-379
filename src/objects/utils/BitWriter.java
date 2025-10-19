package objects.utils;

public class BitWriter {
   private byte[] data;
   private int cursor = 0;
   private byte bitCursor = 0;

   private BitWriter() {
   }

   public BitWriter(int bitSize) {
      this();
      this.data = new byte[(bitSize + 7) / 8];
   }

   public void writeBits(byte bitSize, int value) {
      byte temp = this.data[this.cursor];

      for (int i = 0; i < bitSize; this.bitCursor++) {
         if (this.bitCursor >= 8) {
            this.data[this.cursor++] = temp;
            temp = 0;
            this.bitCursor = 0;
         }

         byte b = (byte)(value >> i & 1);
         temp |= (byte)(b << this.bitCursor);
         i++;
      }

      this.data[this.cursor] = temp;
   }

   public void writeBitsNullable(int item) {
      int bits = 10;
      int def = (1 << (bits & 31)) - 1;
      this.writeBits((byte)bits, item == 0 ? def : item % 1000);
   }

   public byte[] getData() {
      return this.data;
   }
}
