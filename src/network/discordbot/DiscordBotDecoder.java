package network.discordbot;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import network.decode.ByteArrayByteStream;
import network.decode.PacketDecoder;

public class DiscordBotDecoder extends ByteToMessageDecoder {
   protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> list) throws Exception {
      while (true) {
         int length = buffer.readableBytes();

         try {
            buffer.markReaderIndex();
            byte[] decoded = new byte[length];
            buffer.readBytes(decoded);
            buffer.markReaderIndex();
            list.add(new PacketDecoder(new ByteArrayByteStream(decoded)));
            if (buffer.readableBytes() < 4) {
               return;
            }
         } catch (Exception var6) {
            System.out.println("디스코드 봇 오류 발생");
            var6.printStackTrace();
         }
      }
   }
}
