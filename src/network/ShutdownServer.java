package network;

import database.DBConnection;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import network.center.Center;
import network.game.GameServer;
import network.login.LoginServer;
import network.models.CWvsContext;
import network.shop.CashShopServer;
import objects.fields.Field;
import objects.users.MapleCharacter;
import objects.utils.Timer;

public class ShutdownServer implements ShutdownServerMBean {
   public static ShutdownServer instance;
   public int mode = 0;

   public static void registerMBean() {
      MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

      try {
         instance = new ShutdownServer();
         mBeanServer.registerMBean(instance, new ObjectName("server:type=ShutdownServer"));
      } catch (Exception var2) {
         System.out.println("Error registering Shutdown MBean");
         var2.printStackTrace();
      }
   }

   public static ShutdownServer getInstance() {
      return instance;
   }

   @Override
   public void shutdown() {
      this.run();
   }

   @Override
   public void run() {
      if (this.mode == 0) {
         Center.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, "서버가 잠시후 종료됩니다. 모두 종료해주시기 바랍니다."));

         try {
            Thread.sleep(10000L);
         } catch (Exception var13) {
         }

         Center.Auction.save();

         for (GameServer cs : GameServer.getAllInstances()) {
            cs.setShutdown();
            cs.setServerMessage("서버가 잠시후 종료됩니다. 모두 종료해주시기 바랍니다.");

            for (Field map : cs.getMapFactory().getAllMaps()) {
               for (MapleCharacter chr : map.getCharacters()) {
                  chr.saveToDB(false, false);
               }
            }
         }

         Center.Guild.save();
         Center.Alliance.save();
         System.out.println("커뮤니티와 경매장이 저장되었습니다.");
         this.mode++;
      } else if (this.mode == 1) {
         while (this.mode != 1) {
            try {
               Thread.sleep(1000L);
            } catch (Exception var12) {
            }
         }

         this.mode++;
         Center.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, "서버가 잠시후 종료됩니다. 모두 종료해주시기 바랍니다."));
         Integer[] chs = GameServer.getAllInstance().toArray(new Integer[0]);
         Integer[] var15 = chs;
         int var16 = chs.length;

         for (int var17 = 0; var17 < var16; var17++) {
            int i = var15[var17];

            try {
               GameServer cs = GameServer.getInstance(i);
               synchronized (this) {
                  cs.shutdown();
               }
            } catch (Exception var11) {
               System.out.println("Shutdown Err");
               var11.printStackTrace();
            }
         }

         LoginServer.shutdown();
         CashShopServer.shutdown();
         DBConnection.shutdown();
         Timer.WorldTimer.getInstance().stop();
         Timer.MapTimer.getInstance().stop();
         Timer.BuffTimer.getInstance().stop();
         Timer.CloneTimer.getInstance().stop();
         Timer.EventTimer.getInstance().stop();
         Timer.EtcTimer.getInstance().stop();
         Timer.PingTimer.getInstance().stop();
         System.out.println("서버가 종료되었습니다.");

         try {
            Thread.sleep(3000L);
         } catch (Exception var9) {
         }

         System.exit(0);
      }
   }
}
