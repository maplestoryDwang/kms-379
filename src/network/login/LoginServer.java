package network.login;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import network.netty.MapleNettyDecoder;
import network.netty.MapleNettyEncoder;
import network.netty.MapleNettyHandler;
import network.netty.ServerType;
import objects.utils.Pair;
import objects.utils.ServerProperties;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;

public class LoginServer {
   public static final int PORT = 8484;
   private static Map<Integer, Integer> load = new HashMap<>();
   private static String serverName;
   private static String eventMessage;
   private static byte flag;
   private static int maxCharacters;
   private static int userLimit;
   private static int usersOn = 0;
   private static boolean finishedShutdown = true;
   private static boolean adminOnly = false;
   private static HashMap<Integer, Pair<String, String>> loginAuth = new HashMap<>();
   private static HashMap<Integer, String> lastCreateCharDate = new HashMap<>();
   private static HashSet<String> loginIPAuth = new HashSet<>();
   private static ServerBootstrap bootstrap;

   public static void putLoginAuth(int chrid, String ip, String tempIP) {
      loginAuth.put(chrid, new Pair<>(ip, tempIP));
      loginIPAuth.add(ip);
   }

   public static Pair<String, String> getLoginAuth(int chrid) {
      return loginAuth.remove(chrid);
   }

   public static boolean containsIPAuth(String ip) {
      return loginIPAuth.contains(ip);
   }

   public static void removeIPAuth(String ip) {
      loginIPAuth.remove(ip);
   }

   public static void addIPAuth(String ip) {
      loginIPAuth.add(ip);
   }

   public static final void addChannel(int channel) {
      load.put(channel, 0);
   }

   public static final void removeChannel(int channel) {
      load.remove(channel);
   }

   public static final void startLoginServer() {
      userLimit = Integer.parseInt(ServerProperties.getProperty("net.sf.odinms.login.userlimit"));
      serverName = ServerProperties.getProperty("net.sf.odinms.login.serverName");
      eventMessage = ServerProperties.getProperty("net.sf.odinms.login.eventMessage");
      flag = Byte.parseByte(ServerProperties.getProperty("net.sf.odinms.login.flag"));
      adminOnly = Boolean.parseBoolean(ServerProperties.getProperty("net.sf.odinms.world.admin", "false"));
      maxCharacters = Integer.parseInt(ServerProperties.getProperty("net.sf.odinms.login.maxCharacters"));
      IoBuffer.setUseDirectBuffer(false);
      IoBuffer.setAllocator(new SimpleBufferAllocator());
      EventLoopGroup bossGroup = new NioEventLoopGroup(24);
      EventLoopGroup workerGroup = new NioEventLoopGroup(24);

      try {
         bootstrap = new ServerBootstrap();
         ((ServerBootstrap)((ServerBootstrap)bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class))
               .childHandler(new ChannelInitializer<SocketChannel>() {
                  public void initChannel(SocketChannel ch) {
                     ch.pipeline().addLast("decoder", new MapleNettyDecoder());
                     ch.pipeline().addLast("encoder", new MapleNettyEncoder());
                     ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(60, 30, 0));
                     ch.pipeline().addLast("handler", new MapleNettyHandler(ServerType.LOGIN, -1));
                  }
               })
               .option(ChannelOption.SO_BACKLOG, 128))
            .childOption(ChannelOption.SO_SNDBUF, 4194304)
            .childOption(ChannelOption.SO_KEEPALIVE, true);
         ChannelFuture f = bootstrap.bind(8484).sync();
         System.out.println("로그인 서버가 8484 포트를 성공적으로 개방하였습니다.");
      } catch (InterruptedException var3) {
         System.err.println("[오류] 로그인 서버가 8484 포트를 개방하는데 실패했습니다.");
         var3.printStackTrace();
      } catch (Exception var4) {
         System.err.println("[오류] 로그인 서버가 8484 포트를 개방하는데 실패했습니다.");
         var4.printStackTrace();
      }
   }

   public static final void shutdown() {
      if (!finishedShutdown) {
         System.out.println("로그인 서버가 종료됩니다.");
         finishedShutdown = true;
      }
   }

   public static final String getServerName() {
      return serverName;
   }

   public static final String getTrueServerName() {
      return DBConfig.isGanglim ? serverName : serverName.substring(0, serverName.length() - 3);
   }

   public static final String getEventMessage() {
      return eventMessage;
   }

   public static final byte getFlag() {
      return flag;
   }

   public static final int getMaxCharacters() {
      return maxCharacters;
   }

   public static final Map<Integer, Integer> getLoad() {
      return load;
   }

   public static void setLoad(Map<Integer, Integer> load_, int usersOn_) {
      load = load_;
      usersOn = usersOn_;
   }

   public static final void setEventMessage(String newMessage) {
      eventMessage = newMessage;
   }

   public static final void setFlag(byte newflag) {
      flag = newflag;
   }

   public static final int getUserLimit() {
      return userLimit;
   }

   public static final int getUsersOn() {
      return usersOn;
   }

   public static final void setUserLimit(int newLimit) {
      userLimit = newLimit;
   }

   public static final boolean isAdminOnly() {
      return adminOnly;
   }

   public static final boolean isShutdown() {
      return finishedShutdown;
   }

   public static final void setOn() {
      finishedShutdown = false;
   }

   public static String getLastCreateCharDate(int accId) {
      return lastCreateCharDate.get(accId) == null ? null : lastCreateCharDate.get(accId);
   }

   public static void putLastCreateCharDate(int accId, String date) {
      lastCreateCharDate.put(accId, date);
   }
}
