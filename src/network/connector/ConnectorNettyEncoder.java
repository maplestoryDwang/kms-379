package network.connector;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.concurrent.locks.Lock;

public class ConnectorNettyEncoder extends MessageToByteEncoder<byte[]> {
   protected void encode(ChannelHandlerContext ctx, byte[] pData, ByteBuf buffer) throws Exception {
      ConnectorClient client = (ConnectorClient)ctx.channel().attr(ConnectorClient.CLIENTKEY).get();
      if (client != null) {
         Lock mutex = client.getLock();
         mutex.lock();

         try {
            TundraAes send_crpyto = client.getRecvCrypto();
            int i = pData.length;
            byte[] a = new byte[]{(byte)(i & 0xFF), (byte)(i >>> 8 & 0xFF), (byte)(i >>> 16 & 0xFF), (byte)(i >>> 24 & 0xFF)};
            buffer.writeBytes(a);
            buffer.writeBytes(send_crpyto.Encrypt(pData));
         } finally {
            mutex.unlock();
         }
      } else {
         buffer.writeByte(-1);
         buffer.writeBytes(pData);
      }

      ctx.flush();
   }
}
