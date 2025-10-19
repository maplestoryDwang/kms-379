package network.connector;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import network.decode.ByteArrayByteStream;
import network.decode.PacketDecoder;

public class ConnectorNettyDecoder extends ByteToMessageDecoder {
   protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> list) throws Exception {
      ConnectorClient client = (ConnectorClient)ctx.channel().attr(ConnectorClient.CLIENTKEY).get();
      if (client != null) {
         if (buffer.readableBytes() >= 4) {
            while (true) {
               int packetlength = buffer.readIntLE();
               if (buffer.readableBytes() < packetlength) {
                  buffer.resetReaderIndex();
                  return;
               }

               try {
                  buffer.markReaderIndex();
                  byte[] decoded = new byte[packetlength];
                  buffer.readBytes(decoded);
                  buffer.markReaderIndex();
                  list.add(new PacketDecoder(new ByteArrayByteStream(client.getSendCrypto().Decrypt(decoded))));
                  if (buffer.readableBytes() < 4) {
                     return;
                  }
               } catch (Exception var7) {
                  System.out.println("커넥터 오류 발생");
                  var7.printStackTrace();
               }
            }
         }
      }
   }
}
