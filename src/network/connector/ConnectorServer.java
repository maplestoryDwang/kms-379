package network.connector;

import constants.ServerConstants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import objects.utils.Timer;

public class ConnectorServer {
   private static final ConnectorServer instance = new ConnectorServer();
   private ServerBootstrap bootstrap;
   private ConnectorClientStorage clients;

   public static ConnectorServer getInstance() {
      return instance;
   }

   public final ConnectorClientStorage getClientStorage() {
      if (this.clients == null) {
         this.clients = new ConnectorClientStorage();
      }

      return this.clients;
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
                           ch.pipeline().addLast("decoder", new ConnectorNettyDecoder());
                           ch.pipeline().addLast("encoder", new ConnectorNettyEncoder());
                           ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(30, 5, 0));
                           ch.pipeline().addLast("handler", new ConnectorNettyHandler());
                           ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(65535));
                           ch.config().setOption(ChannelOption.SO_RCVBUF, 65535);
                        }
                     })
                     .option(ChannelOption.SO_BACKLOG, 128))
                  .option(ChannelOption.SO_RCVBUF, 65535))
               .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535)))
            .childOption(ChannelOption.SO_KEEPALIVE, true);
         ChannelFuture cf = this.bootstrap.bind(2000).sync();
         if (ServerConstants.ConnectorSetting) {
            Timer.WorldTimer.getInstance().register(new ConnectorThread(), 10000L);
         }

         this.clients = new ConnectorClientStorage();
         System.out.println("커넥터 서버가 오픈되었습니다.");
      } catch (InterruptedException var4) {
         System.err.println("커넥터 서버 오픈에 실패했습니다.\r\n" + var4);
      }
   }
}
