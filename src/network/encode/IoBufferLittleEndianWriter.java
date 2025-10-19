package network.encode;

import org.apache.mina.core.buffer.IoBuffer;

public class IoBufferLittleEndianWriter extends GenericLittleEndianWriter {
   private IoBuffer bb;

   public IoBufferLittleEndianWriter() {
      this(50, true);
   }

   public IoBufferLittleEndianWriter(int size) {
      this(size, false);
   }

   public IoBufferLittleEndianWriter(int initialSize, boolean autoExpand) {
      this.bb = IoBuffer.allocate(initialSize);
      this.bb.setAutoExpand(autoExpand);
      this.setByteOutputStream(new IoBufferOutputstream(this.bb));
   }

   public IoBuffer getFlippedBB() {
      return this.bb.flip();
   }

   public IoBuffer getIoBuffer() {
      return this.bb;
   }
}
