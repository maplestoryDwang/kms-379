package network.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import network.decode.ByteArrayByteStream;
import network.decode.PacketDecoder;
import objects.users.MapleClient;
import objects.utils.MapleAESOFB;

public class MapleNettyDecoder extends ByteToMessageDecoder {
   protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> list) throws Exception {
      MapleClient client = (MapleClient)ctx.channel().attr(MapleClient.CLIENTKEY).get();
      if (client != null) {
         if (buffer.readableBytes() >= 4) {
            int packetHeader = buffer.readInt();
            int packetlength = MapleAESOFB.getPacketLength(packetHeader);
            if (buffer.readableBytes() < packetlength) {
               buffer.resetReaderIndex();
            } else {
               buffer.markReaderIndex();
               byte[] decoded = new byte[packetlength];
               buffer.readBytes(decoded);
               buffer.markReaderIndex();
               list.add(new PacketDecoder(new ByteArrayByteStream(client.getReceiveCrypto().crypt(decoded))));
            }
         }
      }
   }
}
