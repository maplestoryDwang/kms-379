package scripting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledFuture;
import javax.script.Invocable;
import javax.script.ScriptException;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.fields.Field;
import objects.fields.MapleMapFactory;
import objects.fields.MapleMapObject;
import objects.fields.MapleSquad;
import objects.fields.events.MapleEvent;
import objects.fields.events.MapleEventType;
import objects.fields.gameobject.Reactor;
import objects.fields.gameobject.ReactorFactory;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.OverrideMonsterStats;
import objects.users.MapleCharacter;
import objects.utils.Randomizer;
import objects.utils.Timer;

public class EventManager {
   private static int[] eventChannel = new int[2];
   private Invocable iv;
   private int channel;
   private Map<String, EventInstanceManager> instances = new WeakHashMap<>();
   private Properties props = new Properties();
   private String name;

   public EventManager(GameServer cserv, Invocable iv, String name) {
      this.iv = iv;
      this.channel = cserv.getChannel();
      this.name = name;
   }

   public void cancel() {
      try {
         this.iv.invokeFunction("cancelSchedule", null);
      } catch (Exception var2) {
      }
   }

   public ScheduledFuture<?> schedule(final String methodName, long delay) {
      return Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            try {
               EventManager.this.iv.invokeFunction(methodName, null);
            } catch (Exception var2) {
            }
         }
      }, delay);
   }

   public ScheduledFuture<?> schedule(final String methodName, long delay, final EventInstanceManager eim) {
      return Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            try {
               EventManager.this.iv.invokeFunction(methodName, eim);
            } catch (Exception var2) {
            }
         }
      }, delay);
   }

   public ScheduledFuture<?> scheduleAtTimestamp(final String methodName, long timestamp) {
      return Timer.EventTimer.getInstance().scheduleAtTimestamp(new Runnable() {
         @Override
         public void run() {
            try {
               EventManager.this.iv.invokeFunction(methodName, null);
            } catch (ScriptException var2) {
            } catch (NoSuchMethodException var3) {
            }
         }
      }, timestamp);
   }

   public int getChannel() {
      return this.channel;
   }

   public GameServer getChannelServer() {
      return GameServer.getInstance(this.channel);
   }

   public EventInstanceManager getInstance(String name) {
      return this.instances.get(name);
   }

   public Collection<EventInstanceManager> getInstances() {
      return Collections.unmodifiableCollection(this.instances.values());
   }

   public EventInstanceManager newInstance(String name) {
      EventInstanceManager ret = new EventInstanceManager(this, name, this.channel);
      this.instances.put(name, ret);
      return ret;
   }

   public EventInstanceManager readyInstance() {
      try {
         return (EventInstanceManager)this.iv.invokeFunction("setup", null);
      } catch (ScriptException var2) {
         var2.printStackTrace();
      } catch (NoSuchMethodException var3) {
         var3.printStackTrace();
      }

      System.out.println("NULL");
      return null;
   }

   public void disposeInstance(String name) {
      this.instances.remove(name);
      if (this.getProperty("state") != null && this.instances.size() == 0) {
         this.setProperty("state", "0");
      }

      if (this.getProperty("leader") != null && this.instances.size() == 0 && this.getProperty("leader").equals("false")) {
         this.setProperty("leader", "true");
      }

      if (this.name.equals("CWKPQ")) {
         MapleSquad squad = GameServer.getInstance(this.channel).getMapleSquad("CWKPQ");
         if (squad != null) {
            squad.clear();
            squad.copy();
         }
      }
   }

   public Invocable getIv() {
      return this.iv;
   }

   public void setProperty(String key, String value) {
      this.props.setProperty(key, value);
   }

   public String getProperty(String key) {
      return this.props.getProperty(key);
   }

   public final Properties getProperties() {
      return this.props;
   }

   public String getName() {
      return this.name;
   }

   public void startInstance() {
      try {
         this.iv.invokeFunction("setup", null);
      } catch (Exception var2) {
      }
   }

   public void startInstance_Solo(String mapid, MapleCharacter chr) {
      try {
         EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", mapid);
         eim.registerPlayer(chr);
      } catch (Exception var4) {
      }
   }

   public void startInstance(String mapid, MapleCharacter chr) {
      try {
         EventInstanceManager var3 = (EventInstanceManager)this.iv.invokeFunction("setup", mapid);
      } catch (Exception var4) {
      }
   }

   public void startInstance_Party(String mapid, MapleCharacter chr) {
      try {
         EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", mapid);
         eim.registerParty(chr.getParty(), chr.getMap());
      } catch (Exception var4) {
      }
   }

   public void startInstance(MapleCharacter character, String leader) {
      try {
         EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", null);
         eim.registerPlayer(character);
         eim.setProperty("leader", leader);
         eim.setProperty("guildid", String.valueOf(character.getGuildId()));
         this.setProperty("guildid", String.valueOf(character.getGuildId()));
      } catch (Exception var4) {
      }
   }

   public void startInstance_CharID(MapleCharacter character) {
      try {
         EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", character.getId());
         eim.registerPlayer(character);
      } catch (Exception var3) {
      }
   }

   public void startInstance_CharMapID(MapleCharacter character) {
      try {
         EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", character.getId(), character.getMapId());
         eim.registerPlayer(character);
      } catch (Exception var3) {
      }
   }

   public void startInstance(MapleCharacter character) {
      try {
         EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", null);
         eim.registerPlayer(character);
      } catch (Exception var3) {
      }
   }

   public void startInstance(Party party, Field map) {
      this.startInstance(party, map, 255);
   }

   public void startInstance(Party party, Field map, int maxLevel) {
      try {
         int averageLevel = 0;
         int size = 0;

         for (PartyMemberEntry mpc : party.getPartyMemberList()) {
            if (mpc.isOnline() && mpc.getFieldID() == map.getId() && mpc.getChannel() == map.getChannel()) {
               averageLevel += mpc.getLevel();
               size++;
            }
         }

         if (size <= 0) {
            return;
         }

         averageLevel /= size;
         EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", Math.min(maxLevel, averageLevel), party.getId());
         eim.registerParty(party, map);
      } catch (ScriptException var8) {
      } catch (Exception var9) {
         this.startInstance_NoID(party, map, var9);
      }
   }

   public void startInstance_NoID(Party party, Field map) {
      this.startInstance_NoID(party, map, null);
   }

   public void startInstance_NoID(Party party, Field map, Exception old) {
      try {
         EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", null);
         eim.registerParty(party, map);
      } catch (Exception var5) {
      }
   }

   public void startInstance(EventInstanceManager eim, String leader) {
      try {
         this.iv.invokeFunction("setup", eim);
         eim.setProperty("leader", leader);
      } catch (Exception var4) {
      }
   }

   public void startInstance(MapleSquad squad, Field map) {
      this.startInstance(squad, map, -1);
   }

   public void startInstance(MapleSquad squad, Field map, int questID) {
      if (squad.getStatus() != 0) {
         if (!squad.getLeader().isGM()) {
            if (squad.getMembers().size() < squad.getType().i) {
               squad.getLeader().dropMessage(5, "The squad has less than " + squad.getType().i + " people participating.");
               return;
            }

            if (this.name.equals("CWKPQ") && squad.getJobs().size() < 5) {
               squad.getLeader().dropMessage(5, "The squad requires members from every type of job.");
               return;
            }
         }

         try {
            EventInstanceManager eim = (EventInstanceManager)this.iv.invokeFunction("setup", squad.getLeaderName());
            eim.registerSquad(squad, map, questID);
         } catch (Exception var5) {
         }
      }
   }

   public void warpAllPlayer(int from, int to) {
      Field tomap = this.getMapFactory().getMap(to);
      Field frommap = this.getMapFactory().getMap(from);
      List<MapleCharacter> list = frommap.getCharactersThreadsafe();
      if (tomap != null && frommap != null && list != null && frommap.getCharactersSize() > 0) {
         for (MapleMapObject mmo : list) {
            ((MapleCharacter)mmo).changeMap(tomap, tomap.getPortal(0));
         }
      }
   }

   public MapleMapFactory getMapFactory() {
      return this.getChannelServer().getMapFactory();
   }

   public OverrideMonsterStats newMonsterStats() {
      return new OverrideMonsterStats();
   }

   public List<MapleCharacter> newCharList() {
      return new ArrayList<>();
   }

   public MapleMonster getMonster(int id) {
      return MapleLifeFactory.getMonster(id);
   }

   public Reactor getReactor(int id) {
      return new Reactor(ReactorFactory.getReactor(id), id);
   }

   public void broadcastShip(int mapid, int effect, int mode) {
      this.getMapFactory().getMap(mapid).broadcastMessage(CField.boatPacket(effect, mode));
   }

   public void broadcastYellowMsg(String msg) {
      this.getChannelServer().broadcastPacket(CWvsContext.yellowChat(msg));
   }

   public void broadcastServerMsg(int type, String msg, boolean weather) {
      if (!weather) {
         this.getChannelServer().broadcastPacket(CWvsContext.serverNotice(type, msg));
      } else {
         for (Field load : this.getMapFactory().getAllMaps()) {
            if (load.getCharactersSize() > 0) {
               load.startMapEffect(msg, type);
            }
         }
      }
   }

   public boolean scheduleRandomEvent() {
      boolean omg = false;

      for (int i = 0; i < eventChannel.length; i++) {
         omg |= this.scheduleRandomEventInChannel(eventChannel[i]);
      }

      return omg;
   }

   public boolean scheduleRandomEventInChannel(int chz) {
      final GameServer cs = GameServer.getInstance(chz);
      if (cs != null && cs.getEvent() <= -1) {
         MapleEventType t = null;

         while (t == null) {
            for (MapleEventType x : MapleEventType.values()) {
               if (Randomizer.nextInt(MapleEventType.values().length) == 0 && x != MapleEventType.OxQuiz) {
                  t = x;
                  break;
               }
            }
         }

         String msg = MapleEvent.scheduleEvent(t, cs);
         if (msg.length() > 0) {
            this.broadcastYellowMsg(msg);
            return false;
         } else {
            Timer.EventTimer.getInstance().schedule(new Runnable() {
               @Override
               public void run() {
                  if (cs.getEvent() >= 0) {
                     MapleEvent.setEvent(cs, true);
                  }
               }
            }, 180000L);
            return true;
         }
      } else {
         return false;
      }
   }

   public void setWorldEvent() {
      for (int i = 0; i < eventChannel.length; i++) {
         eventChannel[i] = Randomizer.nextInt(GameServer.getAllInstances().size() - 4) + 2 + i;
      }
   }
}
