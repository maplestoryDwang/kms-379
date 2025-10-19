package network.discordbot;

import database.DBConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BotServer {
   private static final BotServer instance = new BotServer();
   private ServerBootstrap bootstrap;
   private Logger df = Logger.getLogger("BotServer");

   public static BotServer getInstance() {
      return instance;
   }

   public void run_startup_configurations() {
      EventLoopGroup bossGroup = new NioEventLoopGroup(24);
      EventLoopGroup workerGroup = new NioEventLoopGroup(24);

      try {
         this.bootstrap = new ServerBootstrap();
         ((ServerBootstrap)((ServerBootstrap)((ServerBootstrap)((ServerBootstrap)this.bootstrap
                        .group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class))
                     .childHandler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch) throws Exception {
                           ch.pipeline().addLast("decoder", new DiscordBotDecoder());
                           ch.pipeline().addLast("encoder", new DiscordBotEncoder());
                           ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(30, 5, 0));
                           ch.pipeline().addLast("handler", new DiscordBotHandler());
                           ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(65535));
                           ch.config().setOption(ChannelOption.SO_RCVBUF, 65535);
                        }
                     })
                     .option(ChannelOption.SO_BACKLOG, 128))
                  .option(ChannelOption.SO_RCVBUF, 65535))
               .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535)))
            .childOption(ChannelOption.SO_KEEPALIVE, true);
         if (DBConfig.isHosting) {
            this.bootstrap.bind(DBConfig.isGanglim ? 3125 : 5777).sync();
         } else {
            this.bootstrap.bind(3125).sync();
         }

         this.df.log(Level.INFO, "디스코드 봇서버가 오픈 되었습니다.");
      } catch (InterruptedException var4) {
         this.df.log(Level.INFO, "디스코드 봇서버 오픈이 실패하였습니다.");
      }
   }
}
