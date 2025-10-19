package objects.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;
import network.center.Center;
import network.models.CWvsContext;
import objects.users.MapleClient;

public class AutobanManager implements Runnable {
   private Map<Integer, Integer> points = new HashMap<>();
   private Map<Integer, List<String>> reasons = new HashMap<>();
   private Set<AutobanManager.ExpirationEntry> expirations = new TreeSet<>();
   private static final int AUTOBAN_POINTS = 5000;
   private static AutobanManager instance = new AutobanManager();
   private final ReentrantLock lock = new ReentrantLock(true);

   public static final AutobanManager getInstance() {
      return instance;
   }

   public final void autoban(MapleClient c, String reason) {
      if (c.getPlayer() != null) {
         if (!c.getPlayer().isGM() && !c.getPlayer().isClone()) {
            this.addPoints(c, 5000, 0L, reason);
         } else {
            c.getPlayer().dropMessage(5, "[경고] 운영자는 오토밴 대상이 아닙니다. : " + reason);
         }
      }
   }

   public final void addPoints(MapleClient c, int points, long expiration, String reason) {
      this.lock.lock();

      try {
         int acc = c.getPlayer().getAccountID();
         if (this.points.containsKey(acc)) {
            int SavedPoints = this.points.get(acc);
            if (SavedPoints >= 5000) {
               return;
            }

            this.points.put(acc, SavedPoints + points);
            List<String> reasonList = this.reasons.get(acc);
            reasonList.add(reason);
         } else {
            this.points.put(acc, points);
            List<String> reasonList = new LinkedList<>();
            reasonList.add(reason);
            this.reasons.put(acc, reasonList);
         }

         if (this.points.get(acc) < 5000) {
            if (expiration > 0L) {
               this.expirations.add(new AutobanManager.ExpirationEntry(System.currentTimeMillis() + expiration, acc, points));
            }
         } else if (!c.getPlayer().isGM() && !c.getPlayer().isClone()) {
            StringBuilder sb = new StringBuilder("오토밴 (name : ");
            sb.append(c.getPlayer().getName());
            sb.append(", IP : ");
            sb.append(c.getSession().remoteAddress().toString().split(":")[0]);
            sb.append(") 사유 : ");
            String reason_ = String.join(", ", this.reasons.get(acc));
            sb.append(reason_);
            Center.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, "[오토밴] " + c.getPlayer().getName() + "이(가) 밴처리되었습니다. (사유: " + reason + ")"));
            c.getPlayer().ban(sb.toString(), true, true, false);
            c.disconnect(false);
         } else {
            c.getPlayer().dropMessage(5, "[경고] 운영자는 오토밴 대상이 아닙니다. : " + reason);
         }
      } finally {
         this.lock.unlock();
      }
   }

   @Override
   public final void run() {
      long now = System.currentTimeMillis();

      for (AutobanManager.ExpirationEntry e : this.expirations) {
         if (e.time > now) {
            return;
         }

         this.points.put(e.acc, this.points.get(e.acc) - e.points);
      }
   }

   private static class ExpirationEntry implements Comparable<AutobanManager.ExpirationEntry> {
      public long time;
      public int acc;
      public int points;

      public ExpirationEntry(long time, int acc, int points) {
         this.time = time;
         this.acc = acc;
         this.points = points;
      }

      public int compareTo(AutobanManager.ExpirationEntry o) {
         return (int)(this.time - o.time);
      }

      @Override
      public boolean equals(Object oth) {
         if (!(oth instanceof AutobanManager.ExpirationEntry)) {
            return false;
         } else {
            AutobanManager.ExpirationEntry ee = (AutobanManager.ExpirationEntry)oth;
            return this.time == ee.time && this.points == ee.points && this.acc == ee.acc;
         }
      }
   }
}
