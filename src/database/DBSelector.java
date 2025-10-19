package database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import objects.utils.Pair;

public class DBSelector {
   private Object object;
   private List<Pair<DBSelectionKey, Pair<String, Object>>> pendingQuery = Collections.synchronizedList(new ArrayList<>());

   public DBSelector() {
      this.object = new Object();
   }

   public int select() {
      synchronized (this.object) {
         if (this.pendingQuery.isEmpty()) {
            try {
               this.object.wait();
            } catch (InterruptedException var4) {
            }
         }

         return 1;
      }
   }

   public void addQuery(DBSelectionKey key, String query, Object callback) {
      this.pendingQuery.add(new Pair<>(key, new Pair<>(query, callback)));
      synchronized (this.object) {
         this.object.notifyAll();
      }
   }

   public boolean isQueryEmpty() {
      return this.pendingQuery.isEmpty();
   }

   public Pair<DBSelectionKey, Pair<String, Object>> getAndRemove() {
      return this.pendingQuery.remove(0);
   }
}
