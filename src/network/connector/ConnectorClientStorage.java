package network.connector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import objects.utils.Pair;

public class ConnectorClientStorage {
   private final Map<String, ConnectorClient> MainClients = new ConcurrentHashMap<>();
   private final Map<String, ConnectorClient> LoginClients = new ConcurrentHashMap<>();
   private final Map<String, ConnectorClient> SClients = new ConcurrentHashMap<>();
   private static final Map<String, Long> BlockedTime = new ConcurrentHashMap<>();
   private static final Map<String, Pair<Long, Byte>> tracker = new ConcurrentHashMap<>();

   public final Map<String, Pair<Long, Byte>> getTracker() {
      return tracker;
   }

   public final Map<String, Long> getBlockedTime() {
      return BlockedTime;
   }

   public final void addTracker(String address, byte count) {
      tracker.put(address, new Pair<>(System.currentTimeMillis(), count));
   }

   public final void registerMainClient(ConnectorClient c, String s) {
      this.MainClients.put(s.toLowerCase(), c);
   }

   public final void registerClient(ConnectorClient c, String s) {
      this.LoginClients.put(s.toLowerCase(), c);
   }

   public final void registerSClient(ConnectorClient c, String s) {
      this.SClients.put(s.toLowerCase(), c);
   }

   public final void deregisterClient(ConnectorClient c) {
      if (c != null) {
         for (ConnectorClient cli : this.getMainClients()) {
            if (cli.getAddressIP().equals(c.getAddressIP())) {
               this.removeMainClient(cli.getAddressIP());
            }
         }

         for (ConnectorClient clix : this.getLoginClients()) {
            if (clix.getId() == c.getId()) {
               this.removeLoginClient(clix.getId());
            }
         }

         for (ConnectorClient clixx : this.getSClients()) {
            if (clixx.getSecondId() == c.getSecondId()) {
               this.removeSClient(clixx.getSecondId());
            }
         }
      }
   }

   public final ConnectorClient getClientByName(String c) {
      if (this.LoginClients.get(c) != null) {
         return this.LoginClients.get(c);
      } else if (this.SClients.get(c) != null) {
         return this.SClients.get(c);
      } else {
         return this.MainClients.get(c) != null ? this.MainClients.get(c) : null;
      }
   }

   public ConnectorClient getMainClient(String c) {
      return this.MainClients.get(c.toLowerCase());
   }

   public void removeMainClient(String c) {
      this.MainClients.remove(c.toLowerCase());
   }

   public ConnectorClient getLoginClient(String c) {
      return this.LoginClients.get(c.toLowerCase());
   }

   public void removeLoginClient(String c) {
      this.LoginClients.remove(c.toLowerCase());
   }

   public ConnectorClient getSClient(String c) {
      return this.LoginClients.get(c.toLowerCase());
   }

   public void removeSClient(String c) {
      this.LoginClients.remove(c.toLowerCase());
   }

   public final List<ConnectorClient> getMainClients() {
      Iterator<ConnectorClient> itr = this.MainClients.values().iterator();
      List<ConnectorClient> asd = new ArrayList<>();

      while (itr.hasNext()) {
         asd.add(itr.next());
      }

      return asd;
   }

   public final List<ConnectorClient> getLoginClients() {
      Iterator<ConnectorClient> itr = this.LoginClients.values().iterator();
      List<ConnectorClient> asd = new ArrayList<>();

      while (itr.hasNext()) {
         asd.add(itr.next());
      }

      return asd;
   }

   public final List<ConnectorClient> getSClients() {
      Iterator<ConnectorClient> itr = this.SClients.values().iterator();
      List<ConnectorClient> asd = new ArrayList<>();

      while (itr.hasNext()) {
         asd.add(itr.next());
      }

      return asd;
   }
}
