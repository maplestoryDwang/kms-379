package objects.fields;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import network.center.Center;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import objects.users.MapleCharacter;
import objects.utils.Pair;
import objects.utils.Timer;

public class MapleSquad {
   private WeakReference<MapleCharacter> leader;
   private final String leaderName;
   private final String toSay;
   private final Map<String, String> members = new LinkedHashMap<>();
   private final Map<String, String> bannedMembers = new LinkedHashMap<>();
   private final int ch;
   private final long startTime;
   private final int expiration;
   private final int beginMapId;
   private final MapleSquad.MapleSquadType type;
   private byte status = 0;
   private ScheduledFuture<?> removal;

   public MapleSquad(int ch, String type, MapleCharacter leader, int expiration, String toSay) {
      this.leader = new WeakReference<>(leader);
      this.members.put(leader.getName(), "dw");
      this.leaderName = leader.getName();
      this.ch = ch;
      this.toSay = toSay;
      this.type = MapleSquad.MapleSquadType.valueOf(type.toLowerCase());
      this.status = 1;
      this.beginMapId = leader.getMapId();
      leader.getMap().setSquad(this.type);
      if (this.type.queue.get(ch) == null) {
         this.type.queue.put(ch, new ArrayList<>());
         this.type.queuedPlayers.put(ch, new ArrayList<>());
      }

      this.startTime = System.currentTimeMillis();
      this.expiration = expiration;
   }

   public void copy() {
      while (this.type.queue.get(this.ch).size() > 0 && GameServer.getInstance(this.ch).getMapleSquad(this.type) == null) {
         int index = 0;
         long lowest = 0L;

         for (int i = 0; i < this.type.queue.get(this.ch).size(); i++) {
            if (lowest == 0L || (Long)this.type.queue.get(this.ch).get(i).right < lowest) {
               index = i;
               lowest = (Long)this.type.queue.get(this.ch).get(i).right;
            }
         }

         String nextPlayerId = (String)this.type.queue.get(this.ch).remove(index).left;
         int theirCh = Center.Find.findChannel(nextPlayerId);
         if (theirCh <= 0) {
            this.getBeginMap().broadcastMessage(CWvsContext.serverNotice(6, nextPlayerId + "'s squad has been skipped due to the player not being online."));
            this.type.queuedPlayers.get(this.ch).add(new Pair<>(nextPlayerId, "Not online"));
         } else {
            MapleCharacter lead = GameServer.getInstance(theirCh).getPlayerStorage().getCharacterByName(nextPlayerId);
            if (lead != null && lead.getMapId() == this.beginMapId && lead.getClient().getChannel() == this.ch) {
               MapleSquad squad = new MapleSquad(this.ch, this.type.name(), lead, this.expiration, this.toSay);
               if (GameServer.getInstance(this.ch).addMapleSquad(squad, this.type.name())) {
                  this.getBeginMap().broadcastMessage(CField.getClock(this.expiration / 1000));
                  this.getBeginMap().broadcastMessage(CWvsContext.serverNotice(6, nextPlayerId + this.toSay));
                  this.type.queuedPlayers.get(this.ch).add(new Pair<>(nextPlayerId, "Success"));
               } else {
                  squad.clear();
                  this.type.queuedPlayers.get(this.ch).add(new Pair<>(nextPlayerId, "Skipped"));
               }
               break;
            }

            if (lead != null) {
               lead.dropMessage(6, "Your squad has been skipped due to you not being in the right channel and map.");
            }

            this.getBeginMap()
               .broadcastMessage(
                  CWvsContext.serverNotice(6, nextPlayerId + "'s squad has been skipped due to the player not being in the right channel and map.")
               );
            this.type.queuedPlayers.get(this.ch).add(new Pair<>(nextPlayerId, "Not in map"));
         }
      }
   }

   public Field getBeginMap() {
      return GameServer.getInstance(this.ch).getMapFactory().getMap(this.beginMapId);
   }

   public void clear() {
      if (this.removal != null) {
         this.getBeginMap().broadcastMessage(CField.stopClock());
         this.removal.cancel(false);
         this.removal = null;
      }

      this.members.clear();
      this.bannedMembers.clear();
      this.leader = null;
      GameServer.getInstance(this.ch).removeMapleSquad(this.type);
      this.status = 0;
   }

   public MapleCharacter getChar(String name) {
      return GameServer.getInstance(this.ch).getPlayerStorage().getCharacterByName(name);
   }

   public long getTimeLeft() {
      return this.expiration - (System.currentTimeMillis() - this.startTime);
   }

   public void scheduleRemoval() {
      this.removal = Timer.EtcTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (MapleSquad.this.status != 0 && MapleSquad.this.leader != null && (MapleSquad.this.getLeader() == null || MapleSquad.this.status == 1)) {
               MapleSquad.this.clear();
               MapleSquad.this.copy();
            }
         }
      }, this.expiration);
   }

   public String getLeaderName() {
      return this.leaderName;
   }

   public List<Pair<String, Long>> getAllNextPlayer() {
      return this.type.queue.get(this.ch);
   }

   public String getNextPlayer() {
      StringBuilder sb = new StringBuilder("\nQueued members : ");
      sb.append("#b").append(this.type.queue.get(this.ch).size()).append(" #k ").append("List of participants : \n\r ");
      int i = 0;

      for (Pair<String, Long> chr : this.type.queue.get(this.ch)) {
         sb.append(++i).append(" : ").append(chr.left);
         sb.append(" \n\r ");
      }

      sb.append("Would you like to #ebe next#n in the queue, or #ebe removed#n from the queue if you are in it?");
      return sb.toString();
   }

   public void setNextPlayer(String i) {
      Pair<String, Long> toRemove = null;

      for (Pair<String, Long> s : this.type.queue.get(this.ch)) {
         if (s.left.equals(i)) {
            toRemove = s;
            break;
         }
      }

      if (toRemove != null) {
         this.type.queue.get(this.ch).remove(toRemove);
      } else {
         for (ArrayList<Pair<String, Long>> v : this.type.queue.values()) {
            for (Pair<String, Long> sx : v) {
               if (sx.left.equals(i)) {
                  return;
               }
            }
         }

         this.type.queue.get(this.ch).add(new Pair<>(i, System.currentTimeMillis()));
      }
   }

   public MapleCharacter getLeader() {
      if (this.leader == null || this.leader.get() == null) {
         if (this.members.size() <= 0 || this.getChar(this.leaderName) == null) {
            if (this.status != 0) {
               this.clear();
            }

            return null;
         }

         this.leader = new WeakReference<>(this.getChar(this.leaderName));
      }

      return this.leader.get();
   }

   public boolean containsMember(MapleCharacter member) {
      for (String mmbr : this.members.keySet()) {
         if (mmbr.equalsIgnoreCase(member.getName())) {
            return true;
         }
      }

      return false;
   }

   public List<String> getMembers() {
      return new LinkedList<>(this.members.keySet());
   }

   public List<String> getBannedMembers() {
      return new LinkedList<>(this.bannedMembers.keySet());
   }

   public int getSquadSize() {
      return this.members.size();
   }

   public boolean isBanned(MapleCharacter member) {
      return this.bannedMembers.containsKey(member.getName());
   }

   public int addMember(MapleCharacter member, boolean join) {
      if (this.getLeader() == null) {
         return -1;
      } else {
         String job = "";
         if (join) {
            if (this.containsMember(member) || this.getAllNextPlayer().contains(member.getName())) {
               return -1;
            } else if (this.members.size() <= 30) {
               this.members.put(member.getName(), "");
               this.getLeader().dropMessage(5, member.getName() + " () has joined the fight!");
               return 1;
            } else {
               return 2;
            }
         } else if (this.containsMember(member)) {
            this.members.remove(member.getName());
            this.getLeader().dropMessage(5, member.getName() + " () have withdrawed from the fight.");
            return 1;
         } else {
            return -1;
         }
      }
   }

   public void acceptMember(int pos) {
      if (pos >= 0 && pos < this.bannedMembers.size()) {
         List<String> membersAsList = this.getBannedMembers();
         String toadd = membersAsList.get(pos);
         if (toadd != null && this.getChar(toadd) != null) {
            this.members.put(toadd, this.bannedMembers.get(toadd));
            this.bannedMembers.remove(toadd);
            this.getChar(toadd).dropMessage(5, this.getLeaderName() + " has decided to add you back to the squad.");
         }
      }
   }

   public void reAddMember(MapleCharacter chr) {
      this.removeMember(chr);
      this.members.put(chr.getName(), "");
   }

   public void removeMember(MapleCharacter chr) {
      if (this.members.containsKey(chr.getName())) {
         this.members.remove(chr.getName());
      }
   }

   public void removeMember(String chr) {
      if (this.members.containsKey(chr)) {
         this.members.remove(chr);
      }
   }

   public void banMember(int pos) {
      if (pos > 0 && pos < this.members.size()) {
         List<String> membersAsList = this.getMembers();
         String toban = membersAsList.get(pos);
         if (toban != null && this.getChar(toban) != null) {
            this.bannedMembers.put(toban, this.members.get(toban));
            this.members.remove(toban);
            this.getChar(toban).dropMessage(5, this.getLeaderName() + " has removed you from the squad.");
         }
      }
   }

   public void setStatus(byte status) {
      this.status = status;
      if (status == 2 && this.removal != null) {
         this.removal.cancel(false);
         this.removal = null;
      }
   }

   public int getStatus() {
      return this.status;
   }

   public int getBannedMemberSize() {
      return this.bannedMembers.size();
   }

   public String getSquadMemberString(byte type) {
      switch (type) {
         case 0:
            StringBuilder sb = new StringBuilder("Squad members : ");
            sb.append("#b").append(this.members.size()).append(" #k ").append("List of participants : \n\r ");
            int i = 0;
            Iterator var15 = this.members.entrySet().iterator();

            for (; var15.hasNext(); sb.append(" \n\r ")) {
               Entry<String, String> chr = (Entry<String, String>)var15.next();
               sb.append(++i).append(" : ").append(chr.getKey()).append(" (").append(chr.getValue()).append(") ");
               if (i == 1) {
                  sb.append("(Leader of the squad)");
               }
            }

            while (i < 30) {
               sb.append(++i).append(" : ").append(" \n\r ");
            }

            return sb.toString();
         case 1:
            sb = new StringBuilder("Squad members : ");
            sb.append("#b").append(this.members.size()).append(" #n ").append("List of participants : \n\r ");
            i = 0;
            int selection = 0;

            for (Entry<String, String> chr : this.members.entrySet()) {
               i++;
               sb.append("#b#L").append(selection).append("#");
               selection++;
               sb.append(i).append(" : ").append(chr.getKey()).append(" (").append(chr.getValue()).append(") ");
               if (i == 1) {
                  sb.append("(Leader of the squad)");
               }

               sb.append("#l").append(" \n\r ");
            }

            while (i < 30) {
               sb.append(++i).append(" : ").append(" \n\r ");
            }

            return sb.toString();
         case 2:
            sb = new StringBuilder("Squad members : ");
            sb.append("#b").append(this.members.size()).append(" #n ").append("List of participants : \n\r ");
            i = 0;
            selection = 0;

            for (Entry<String, String> chr : this.bannedMembers.entrySet()) {
               i++;
               sb.append("#b#L").append(selection).append("#");
               selection++;
               sb.append(i).append(" : ").append(chr.getKey()).append(" (").append(chr.getValue()).append(") ");
               sb.append("#l").append(" \n\r ");
            }

            while (i < 30) {
               sb.append(++i).append(" : ").append(" \n\r ");
            }

            return sb.toString();
         case 3:
            sb = new StringBuilder("Jobs : ");
            Map<String, Integer> jobs = this.getJobs();

            for (Entry<String, Integer> chr : jobs.entrySet()) {
               sb.append("\r\n").append(chr.getKey()).append(" : ").append(chr.getValue());
            }

            return sb.toString();
         default:
            return null;
      }
   }

   public final MapleSquad.MapleSquadType getType() {
      return this.type;
   }

   public final Map<String, Integer> getJobs() {
      Map<String, Integer> jobs = new LinkedHashMap<>();

      for (Entry<String, String> chr : this.members.entrySet()) {
         if (jobs.containsKey(chr.getValue())) {
            jobs.put(chr.getValue(), jobs.get(chr.getValue()) + 1);
         } else {
            jobs.put(chr.getValue(), 1);
         }
      }

      return jobs;
   }

   public static enum MapleSquadType {
      bossbalrog(2),
      zak(2),
      chaoszak(3),
      horntail(2),
      chaosht(3),
      pinkbean(3),
      nmm_squad(2),
      vergamot(2),
      dunas(2),
      nibergen_squad(2),
      dunas2(2),
      core_blaze(2),
      aufheben(2),
      cwkpq(10),
      tokyo_2095(2),
      vonleon(3),
      scartar(2),
      cygnus(3);

      public int i;
      public HashMap<Integer, ArrayList<Pair<String, String>>> queuedPlayers = new HashMap<>();
      public HashMap<Integer, ArrayList<Pair<String, Long>>> queue = new HashMap<>();

      private MapleSquadType(int i) {
         this.i = i;
      }
   }
}
