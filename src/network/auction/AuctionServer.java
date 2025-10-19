package network.auction;

import constants.ServerConstants;
import database.DBConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import network.game.PlayerStorage;
import network.netty.MapleNettyDecoder;
import network.netty.MapleNettyEncoder;
import network.netty.MapleNettyHandler;
import network.netty.ServerType;
import objects.utils.ServerProperties;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;

public class AuctionServer {
   private static String ip;
   private static final int PORT = !ServerConstants.tempServer && DBConfig.isGanglim ? 5475 : 8616;
   private static PlayerStorage players;
   private static ServerBootstrap bootstrap;

   public static final void startAuctionServer() {
      ip = ServerProperties.getProperty("net.sf.odinms.world.host") + ":" + PORT;
      players = new PlayerStorage(-10);
      IoBuffer.setUseDirectBuffer(false);
      IoBuffer.setAllocator(new SimpleBufferAllocator());
      EventLoopGroup bossGroup = new NioEventLoopGroup(24);
      EventLoopGroup workerGroup = new NioEventLoopGroup(24);

      try {
         bootstrap = new ServerBootstrap();
         ((ServerBootstrap)((ServerBootstrap)bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class))
               .childHandler(new ChannelInitializer<SocketChannel>() {
                  public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast("decoder", new MapleNettyDecoder());
                     ch.pipeline().addLast("encoder", new MapleNettyEncoder());
                     ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(60, 30, 0));
                     ch.pipeline().addLast("handler", new MapleNettyHandler(ServerType.AUCTION, -1));
                  }
               })
               .option(ChannelOption.SO_BACKLOG, 128))
            .childOption(ChannelOption.SO_SNDBUF, 4194304)
            .childOption(ChannelOption.SO_KEEPALIVE, true);
         ChannelFuture f = bootstrap.bind(PORT).sync();
         System.out.println("경매장 서버가 " + PORT + " 포트를 성공적으로 개방하였습니다.");
      } catch (InterruptedException var3) {
         System.err.println("[오류] 경매장 서버가 " + PORT + " 포트를 개방하는데 실패했습니다.");
         var3.printStackTrace();
      }
   }

   public static final PlayerStorage getPlayerStorage() {
      return players;
   }

   public static String getIP() {
      return ip;
   }

   public static int getPort() {
      return PORT;
   }
}
