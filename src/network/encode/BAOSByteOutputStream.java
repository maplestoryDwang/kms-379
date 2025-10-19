package network.encode;

import java.io.ByteArrayOutputStream;

public class BAOSByteOutputStream implements IoOutputStream {
   private ByteArrayOutputStream baos;

   public BAOSByteOutputStream(ByteArrayOutputStream baos) {
      this.baos = baos;
   }

   @Override
   public void writeByte(byte b) {
      this.baos.write(b);
   }
}
