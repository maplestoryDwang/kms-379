package network.game;

import constants.QuestExConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import network.center.Center;
import network.center.CharacterTransfer;
import network.center.CheaterData;
import objects.users.MapleCharacter;
import objects.users.MapleCharacterUtil;
import objects.users.MapleClient;
import objects.utils.Timer;

public class PlayerStorage {
   private final ReentrantReadWriteLock mutex = new ReentrantReadWriteLock();
   private final Lock rL = this.mutex.readLock();
   private final Lock wL = this.mutex.writeLock();
   private final ReentrantReadWriteLock mutex2 = new ReentrantReadWriteLock();
   private final Lock rL2 = this.mutex2.readLock();
   private final Lock wL2 = this.mutex2.writeLock();
   private final Map<String, MapleCharacter> nameToChar = new HashMap<>();
   private final Map<Integer, MapleCharacter> idToChar = new HashMap<>();
   private final Map<Integer, MapleClient> accIdToChar = new HashMap<>();
   private final Map<Integer, CharacterTransfer> PendingCharacter = new HashMap<>();
   private int channel;

   public PlayerStorage(int channel) {
      this.channel = channel;
      Timer.PingTimer.getInstance().register(new PlayerStorage.PersistingTask(), 60000L);
   }

   public final ArrayList<MapleCharacter> getAllCharacters() {
      this.rL.lock();

      ArrayList var1;
      try {
         var1 = new ArrayList<>(this.idToChar.values());
      } finally {
         this.rL.unlock();
      }

      return var1;
   }

   public final void registerPlayer(MapleCharacter chr) {
      this.wL.lock();

      try {
         this.nameToChar.put(chr.getName().toLowerCase(), chr);
         this.idToChar.put(chr.getId(), chr);
         this.accIdToChar.put(chr.getAccountID(), chr.getClient());
      } finally {
         this.wL.unlock();
      }

      Center.Find.register(chr.getId(), chr.getAccountID(), chr.getName(), this.channel);
   }

   public final void registerPendingPlayer(CharacterTransfer chr, int playerid) {
      this.wL2.lock();

      try {
         this.PendingCharacter.put(playerid, chr);
      } finally {
         this.wL2.unlock();
      }
   }

   public final void deregisterPlayer(MapleCharacter chr) {
      this.wL.lock();

      try {
         this.nameToChar.remove(chr.getName().toLowerCase());
         this.idToChar.remove(chr.getId());
         this.accIdToChar.remove(chr.getAccountID());
      } finally {
         this.wL.unlock();
      }

      Center.Find.forceDeregister(chr.getId(), chr.getName(), chr.getAccountID());
   }

   public final void deregisterPlayer(int idz, String namez, int accId) {
      this.wL.lock();

      try {
         this.nameToChar.remove(namez.toLowerCase());
         this.idToChar.remove(idz);
         this.accIdToChar.remove(accId);
      } finally {
         this.wL.unlock();
      }

      Center.Find.forceDeregister(idz, namez, accId);
   }

   public final int pendingCharacterSize() {
      return this.PendingCharacter.size();
   }

   public final void deregisterPendingPlayer(int charid) {
      this.wL2.lock();

      try {
         this.PendingCharacter.remove(charid);
      } finally {
         this.wL2.unlock();
      }
   }

   public final CharacterTransfer getPendingCharacter(int charid) {
      this.wL2.lock();

      CharacterTransfer var2;
      try {
         var2 = this.PendingCharacter.remove(charid);
      } finally {
         this.wL2.unlock();
      }

      return var2;
   }

   public final MapleCharacter getCharacterByName(String name) {
      this.rL.lock();

      MapleCharacter var2;
      try {
         var2 = this.nameToChar.get(name.toLowerCase());
      } finally {
         this.rL.unlock();
      }

      return var2;
   }

   public final MapleCharacter getCharacterById(int id) {
      this.rL.lock();

      MapleCharacter var2;
      try {
         var2 = this.idToChar.get(id);
      } finally {
         this.rL.unlock();
      }

      return var2;
   }

   public final MapleClient getClientById(int accId) {
      this.rL.lock();

      MapleClient var2;
      try {
         var2 = this.accIdToChar.get(accId);
      } finally {
         this.rL.unlock();
      }

      return var2;
   }

   public final int getConnectedClients() {
      return this.idToChar.size();
   }

   public final List<CheaterData> getCheaters() {
      List<CheaterData> cheaters = new ArrayList<>();
      this.rL.lock();

      try {
         for (MapleCharacter chr : this.nameToChar.values()) {
            if (chr.getCheatTracker().getPoints() > 0) {
               cheaters.add(
                  new CheaterData(
                     chr.getCheatTracker().getPoints(),
                     MapleCharacterUtil.makeMapleReadable(chr.getName()) + " (" + chr.getCheatTracker().getPoints() + ") " + chr.getCheatTracker().getSummary()
                  )
               );
            }
         }
      } finally {
         this.rL.unlock();
      }

      return cheaters;
   }

   public final List<CheaterData> getReports() {
      List<CheaterData> cheaters = new ArrayList<>();
      this.rL.lock();

      try {
         for (MapleCharacter chr : this.nameToChar.values()) {
            if (chr.getReportPoints() > 0) {
               cheaters.add(
                  new CheaterData(
                     chr.getReportPoints(), MapleCharacterUtil.makeMapleReadable(chr.getName()) + " (" + chr.getReportPoints() + ") " + chr.getReportSummary()
                  )
               );
            }
         }
      } finally {
         this.rL.unlock();
      }

      return cheaters;
   }

   public final void disconnectAll() {
      this.disconnectAll(false);
   }

   public final void disconnectAll(boolean checkGM) {
      this.wL.lock();

      try {
         Iterator<MapleCharacter> itr = this.nameToChar.values().iterator();

         while (itr.hasNext()) {
            MapleCharacter chr = itr.next();
            if (!chr.isGM() || !checkGM) {
               chr.getClient().disconnect(true);
               chr.getClient().getSession().close();
               System.out.println("팅겼다고인마");
               Center.Find.forceDeregister(chr.getId(), chr.getName(), chr.getAccountID());
               itr.remove();
            }
         }
      } finally {
         this.wL.unlock();
      }
   }

   public final String getOnlinePlayers(boolean byGM) {
      StringBuilder sb = new StringBuilder();
      if (byGM) {
         this.rL.lock();

         try {
            Iterator<MapleCharacter> itr = this.nameToChar.values().iterator();

            while (itr.hasNext()) {
               sb.append(MapleCharacterUtil.makeMapleReadable(itr.next().getName()));
               sb.append(", ");
            }
         } finally {
            this.rL.unlock();
         }
      } else {
         this.rL.lock();

         try {
            for (MapleCharacter chr : this.nameToChar.values()) {
               if (!chr.isGM()) {
                  sb.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                  sb.append(", ");
               }
            }
         } finally {
            this.rL.unlock();
         }
      }

      return sb.toString();
   }

   public final void broadcastPacketCheckQuest(byte[] data, String questStr, boolean force) {
      this.rL.lock();

      try {
         for (MapleCharacter chr : this.nameToChar.values()) {
            if (force || chr.getOneInfoQuestInteger(QuestExConstants.CustomQuests.getQuestID(), questStr) == 0) {
               chr.getClient().getSession().writeAndFlush(data);
            }
         }
      } finally {
         this.rL.unlock();
      }
   }

   public final void broadcastPacket(byte[] data) {
      this.rL.lock();

      try {
         for (MapleCharacter chr : this.nameToChar.values()) {
            chr.getClient().getSession().writeAndFlush(data);
         }
      } finally {
         this.rL.unlock();
      }
   }

   public final void broadcastSmegaPacket(byte[] data) {
      this.rL.lock();

      try {
         for (MapleCharacter chr : this.nameToChar.values()) {
            if (chr.getClient().isLoggedIn() && chr.getSmega()) {
               chr.getClient().getSession().writeAndFlush(data);
            }
         }
      } finally {
         this.rL.unlock();
      }
   }

   public final void broadcastGMPacket(byte[] data) {
      this.rL.lock();

      try {
         for (MapleCharacter chr : this.nameToChar.values()) {
            if (chr.getClient().isLoggedIn() && chr.isIntern()) {
               chr.getClient().getSession().writeAndFlush(data);
            }
         }
      } finally {
         this.rL.unlock();
      }
   }

   public final void broadcastPacketLadderGame(byte[] data) {
      this.rL.lock();

      try {
         for (MapleCharacter chr : this.nameToChar.values()) {
            if (chr.showLadderGameResult) {
               chr.getClient().getSession().writeAndFlush(data);
            }
         }
      } finally {
         this.rL.unlock();
      }
   }

   public class PersistingTask implements Runnable {
      @Override
      public void run() {
         PlayerStorage.this.wL2.lock();

         try {
            long currenttime = System.currentTimeMillis();
            Iterator<Entry<Integer, CharacterTransfer>> itr = PlayerStorage.this.PendingCharacter.entrySet().iterator();

            while (itr.hasNext()) {
               if (currenttime - itr.next().getValue().TranferTime > 40000L) {
                  itr.remove();
               }
            }
         } finally {
            PlayerStorage.this.wL2.unlock();
         }
      }
   }
}
