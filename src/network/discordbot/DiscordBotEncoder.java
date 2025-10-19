package network.discordbot;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DiscordBotEncoder extends MessageToByteEncoder<byte[]> {
   private final Lock mutex = new ReentrantLock();

   protected void encode(ChannelHandlerContext ctx, byte[] pData, ByteBuf buffer) throws Exception {
      this.mutex.lock();

      try {
         buffer.writeBytes(pData);
      } finally {
         this.mutex.unlock();
      }

      ctx.flush();
   }
}
