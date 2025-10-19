package network.encode;

import org.apache.mina.core.buffer.IoBuffer;

public class IoBufferOutputstream implements IoOutputStream {
   private IoBuffer bb;

   public IoBufferOutputstream(IoBuffer bb) {
      this.bb = bb;
   }

   @Override
   public void writeByte(byte b) {
      this.bb.put(b);
   }
}
