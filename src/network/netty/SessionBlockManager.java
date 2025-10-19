package network.netty;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import objects.utils.Timer;

public class SessionBlockManager {
   public static final String block = "netsh advfirewall firewall add rule name=\"MapleStory_LoginBlock_%s\" dir=in action=block remoteip=%s";
   public static final String unblock = "netsh advfirewall firewall delete rule name=\"MapleStory_LoginBlock_%s\"";
   public static final Map<String, SessionBlockManager.SessionInfo> infos = new ConcurrentHashMap<>();
   public static final Map<String, Long> firewalls = new ConcurrentHashMap<>();

   public static String getIP(SocketChannel sc) {
      return sc.socket().getInetAddress().getHostAddress();
   }

   public static boolean tryAction(String ip, SessionBlockManager.ActionType a) {
      long cur = System.currentTimeMillis();
      SessionBlockManager.SessionInfo info = infos.computeIfAbsent(ip, SessionBlockManager.SessionInfo::new);
      if (info.unblockTime == 0L) {
         int cBlockCount = info.blockCount.get();
         long newUnblockTime = info.count(a);
         if (newUnblockTime != 0L) {
            System.out.println("해당 아이피가 차단됩니다. : " + ip);
            if (a.registerFirewall(cBlockCount)) {
               registerFirewall(ip, newUnblockTime);
            }
         }
      }

      if (info.unblockTime != 0L) {
         if (cur < info.unblockTime) {
            return false;
         }

         info.unblock();
         System.out.println("해당 아이피의 차단이 해제되었습니다. : " + ip);
      }

      return true;
   }

   public static void reset(String ip) {
      infos.remove(ip);
   }

   public static void registerFirewall(String ip, long unblockTime) {
      try {
         Runtime.getRuntime()
            .exec(String.format("netsh advfirewall firewall add rule name=\"MapleStory_LoginBlock_%s\" dir=in action=block remoteip=%s", ip, ip));
         firewalls.put(ip, unblockTime);
         System.out.println("방화벽 규칙이 추가되었습니다. : " + ip);
      } catch (IOException var4) {
         System.err.println("방화벽 규칙 추가에 실패했습니다. : \r\n" + var4);
      }
   }

   public static void unregisterFirewall(String ip) {
      try {
         Runtime.getRuntime().exec(String.format("netsh advfirewall firewall delete rule name=\"MapleStory_LoginBlock_%s\"", ip));
         firewalls.remove(ip);
         System.out.println("방화벽 규칙이 제거되었습니다. : " + ip);
      } catch (IOException var2) {
         System.err.println("방화벽 규칙 제거에 실패했습니다. : \r\n" + var2);
      }
   }

   public static void startUpdater(long delay) {
      Timer.EtcTimer.getInstance().register(SessionBlockManager::update, delay);
   }

   public static void update() {
      long cur = System.currentTimeMillis();
      Set<String> unblockFirewalls = new HashSet<>();

      for (Entry<String, Long> entry : firewalls.entrySet()) {
         if (entry.getValue() > cur) {
            unblockFirewalls.add(entry.getKey());
         }
      }

      for (String ip : unblockFirewalls) {
         unregisterFirewall(ip);
      }
   }

   public static enum ActionType {
      CONNECT {
         @Override
         public int maxCount(int blockCount) {
            return blockCount < 3 ? 35 : 2;
         }

         @Override
         public long firstDiff(int blockCount) {
            return blockCount < 3 ? 35000L : 3500L;
         }

         @Override
         public long latestDiff(int blockCount) {
            return 1000L;
         }

         @Override
         public long unblockTime(int blockCount, long cur) {
            switch (blockCount) {
               case 0:
                  return cur + 60000L;
               case 1:
                  return cur + 300000L;
               case 2:
                  return cur + 3600000L;
               case 3:
                  return cur + 14400000L;
               case 4:
                  return cur + 43200000L;
               default:
                  return cur + 86400000L;
            }
         }

         @Override
         public boolean registerFirewall(int blockCount) {
            return true;
         }
      };

      public abstract int maxCount(int var1);

      public abstract long firstDiff(int var1);

      public abstract long latestDiff(int var1);

      public abstract long unblockTime(int var1, long var2);

      public abstract boolean registerFirewall(int var1);
   }

   public static class CountInfo {
      public final AtomicInteger count = new AtomicInteger();
      public long firstCountTime = 0L;
      public long latestCountTime = 0L;

      public CountInfo(SessionBlockManager.ActionType a) {
      }

      public void reset() {
         this.count.set(0);
         this.firstCountTime = 0L;
         this.latestCountTime = 0L;
      }
   }

   public static class SessionInfo {
      public final String ip;
      public final Map<SessionBlockManager.ActionType, SessionBlockManager.CountInfo> counts = new ConcurrentHashMap<>();
      public AtomicInteger blockCount = new AtomicInteger();
      public long unblockTime = 0L;

      public SessionInfo(String ip) {
         this.ip = ip;
      }

      public long count(SessionBlockManager.ActionType a) {
         long cur = System.currentTimeMillis();
         SessionBlockManager.CountInfo info = this.counts.computeIfAbsent(a, SessionBlockManager.CountInfo::new);
         int cBlockCount = this.blockCount.get();
         if (info.firstCountTime == 0L) {
            info.firstCountTime = cur;
         }

         info.latestCountTime = cur;
         if (cur > info.firstCountTime + a.firstDiff(cBlockCount) || cur > info.latestCountTime + a.latestDiff(cBlockCount)) {
            info.reset();
         }

         if (info.count.incrementAndGet() >= a.maxCount(cBlockCount)) {
            long newUnblockTime = a.unblockTime(cBlockCount, cur);
            this.block(newUnblockTime);
            return newUnblockTime;
         } else {
            return 0L;
         }
      }

      public void recount() {
         this.counts.clear();
      }

      public void block(long unblockTime) {
         this.unblockTime = unblockTime;
         this.blockCount.incrementAndGet();
      }

      public void unblock() {
         this.recount();
         this.unblockTime = 0L;
      }
   }
}
