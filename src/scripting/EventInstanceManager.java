package scripting;

import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.script.ScriptException;
import network.SendPacketOpcode;
import network.center.Center;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.context.party.PartySearch;
import objects.fields.Field;
import objects.fields.MapleFoothold;
import objects.fields.MapleMapFactory;
import objects.fields.MapleSquad;
import objects.fields.child.muto.FoodType;
import objects.fields.child.muto.HungryMuto;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.obstacle.ObstacleAtom;
import objects.fields.obstacle.ObstacleAtomCreateType;
import objects.fields.obstacle.ObstacleAtomEnum;
import objects.fields.obstacle.ObstacleInRowInfo;
import objects.fields.obstacle.ObstacleRadialInfo;
import objects.item.MapleItemInformationProvider;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.users.MapleCharacter;
import objects.users.MapleTrait;
import objects.users.skills.SkillFactory;
import objects.utils.FileoutputUtil;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Timer;
import objects.utils.Util;

public class EventInstanceManager {
   private List<MapleCharacter> chars = new LinkedList<>();
   private List<Integer> dced = new LinkedList<>();
   private List<MapleMonster> mobs = new LinkedList<>();
   private Map<Integer, Integer> killCount = new HashMap<>();
   private EventManager em;
   private int channel;
   private String name;
   private Properties props = new Properties();
   private long timeStarted = 0L;
   private long endTime = 0L;
   private long eventTime = 0L;
   private List<Integer> mapIds = new LinkedList<>();
   private List<Boolean> isInstanced = new LinkedList<>();
   private ScheduledFuture<?> eventTimer;
   private final ReentrantReadWriteLock mutex = new ReentrantReadWriteLock();
   private final Lock rL = this.mutex.readLock();
   private final Lock wL = this.mutex.writeLock();
   private boolean disposed = false;
   private Map<String, ScheduledFuture<?>> eventScheduleList = new HashMap<>();
   private List<Integer> partyPlayerList = new LinkedList<>();
   private Consumer<Boolean> fieldSetTimerGauge = null;
   private ScheduledFuture<?> fieldSetTimerScheduled;
   private long fieldSetTimerGuageEndTime = 0L;
   private int timerGauge = 0;
   private Map<String, Long> damageInfo = new HashMap<>();

   public EventInstanceManager(EventManager em, String name, int channel) {
      this.em = em;
      this.name = name;
      this.channel = channel;
   }

   public List<Integer> getPartyPlayerList() {
      return this.partyPlayerList;
   }

   public void registerPlayer(MapleCharacter chr) {
      if (!this.disposed && chr != null) {
         try {
            this.wL.lock();

            try {
               this.chars.add(chr);
            } finally {
               this.wL.unlock();
            }

            this.partyPlayerList.add(chr.getId());
            chr.setEventInstance(this);
            this.em.getIv().invokeFunction("playerEntry", this, chr);
         } catch (NullPointerException var7) {
            FileoutputUtil.outputFileError("Log_Script_Except.rtf", var7);
         } catch (Exception var8) {
            FileoutputUtil.log(
               "Log_Script_Except.rtf", "Event name" + this.em.getName() + ", Instance name : " + this.name + ", method Name : playerEntry:\n" + var8
            );
         }
      }
   }

   public void changedMap(MapleCharacter chr, int mapid) {
      if (!this.disposed) {
         try {
            this.em.getIv().invokeFunction("changedMap", this, chr, mapid);
         } catch (NullPointerException var4) {
         } catch (Exception var5) {
            FileoutputUtil.log(
               "Log_Script_Except.rtf", "Event name : " + this.em.getName() + ", Instance name : " + this.name + ", method Name : changedMap:\n" + var5
            );
         }
      }
   }

   public void timeOut(long delay, final EventInstanceManager eim) {
      if (!this.disposed && eim != null) {
         this.removeAllEventSchedule();
         this.eventTimer = Timer.EventTimer.getInstance()
            .schedule(
               new Runnable() {
                  @Override
                  public void run() {
                     if (!EventInstanceManager.this.disposed && eim != null && EventInstanceManager.this.em != null) {
                        try {
                           EventInstanceManager.this.em.getIv().invokeFunction("scheduledTimeout", eim);
                        } catch (Exception var2) {
                           FileoutputUtil.log(
                              "Log_Script_Except.rtf",
                              "Event name"
                                 + EventInstanceManager.this.em.getName()
                                 + ", Instance name : "
                                 + EventInstanceManager.this.name
                                 + ", method Name : scheduledTimeout:\n"
                                 + var2
                           );
                           System.out
                              .println(
                                 "Event name"
                                    + EventInstanceManager.this.em.getName()
                                    + ", Instance name : "
                                    + EventInstanceManager.this.name
                                    + ", method Name : scheduledTimeout:\n"
                                    + var2
                              );
                        }
                     }
                  }
               },
               delay
            );
      }
   }

   public boolean hasEventTimer() {
      return this.eventTimer != null;
   }

   public void stopEventTimer() {
      this.eventTime = 0L;
      this.timeStarted = 0L;
      this.endTime = 0L;
      if (this.eventTimer != null) {
         this.eventTimer.cancel(false);
         this.eventTimer = null;
      }
   }

   public void addTimer(int times) {
      if (!this.disposed) {
         if (this.eventTimer != null) {
            this.endTime += times;
            int remain = (int)(this.endTime - this.timeStarted);
            if (remain > 600000) {
               remain = 600000;
               this.endTime = System.currentTimeMillis() + 600000L;
               this.timeStarted = System.currentTimeMillis();
            }

            if (this.eventTimer != null) {
               this.eventTimer.cancel(false);
            }

            this.eventTimer = null;

            for (MapleCharacter chr : this.getPlayers()) {
               chr.send(CField.getTimezoneClock(remain / 1000, times < 0 ? 1 : 0));
            }

            this.timeOut(remain, this);
         }
      }
   }

   public void restartEventTimer(long time) {
      try {
         if (this.disposed) {
            return;
         }

         this.timeStarted = System.currentTimeMillis();
         this.endTime = System.currentTimeMillis() + time;
         this.eventTime = time;
         if (this.eventTimer != null) {
            this.eventTimer.cancel(false);
         }

         this.eventTimer = null;
         int timesend = (int)time / 1000;

         for (MapleCharacter chr : this.getPlayers()) {
            if (this.name.startsWith("PVP")) {
               chr.getClient().getSession().writeAndFlush(CField.getPVPClock(Integer.parseInt(this.getProperty("type")), timesend));
            } else {
               chr.getClient().getSession().writeAndFlush(CField.getClock(timesend));
            }
         }

         this.timeOut(time, this);
      } catch (Exception var6) {
      }
   }

   public void startEventTimer(long time) {
      this.restartEventTimer(time);
   }

   public boolean isTimerStarted() {
      return this.eventTime > 0L && this.timeStarted > 0L;
   }

   public long getTimeLeft() {
      return this.eventTime - (System.currentTimeMillis() - this.timeStarted);
   }

   public void registerParty(Party party, Field map) {
      if (!this.disposed) {
         this.partyPlayerList.clear();
         boolean canEnter = true;
         List<MapleCharacter> chars = new ArrayList<>();

         for (PartyMemberEntry pc : party.getPartyMemberList()) {
            MapleCharacter chr = map.getCharacterById(pc.getId());
            if (chr == null) {
               canEnter = false;
               break;
            }

            chars.add(chr);
         }

         if (canEnter) {
            for (MapleCharacter chr : chars) {
               this.registerPlayer(chr);
            }
         }

         PartySearch ps = Center.Party.getSearch(party);
         if (ps != null) {
            Center.Party.removeSearch(ps, "The Party Listing has been removed because the Party Quest started.");
         }

         if (this.getProperty("DeathCount") != null) {
            for (MapleCharacter chr : chars) {
               chr.send(CField.getPartyDeathCount(chars, Integer.parseInt(this.getProperty("DeathCount"))));
            }
         }
      }
   }

   public boolean registerParty(Party party) {
      if (this.disposed) {
         return false;
      } else {
         this.partyPlayerList.clear();
         int leaderMap = party.getLeader().getFieldID();
         Field field = this.getChannelServer().getMapFactory().getMap(leaderMap);
         List<MapleCharacter> chars = new ArrayList<>();

         for (PartyMemberEntry pc : party.getPartyMemberList()) {
            MapleCharacter chr = field.getCharacterById(pc.getId());
            if (chr == null) {
               return false;
            }

            chars.add(chr);
         }

         for (MapleCharacter chr : chars) {
            this.registerPlayer(chr);
         }

         PartySearch ps = Center.Party.getSearch(party);
         if (ps != null) {
            Center.Party.removeSearch(ps, "The Party Listing has been removed because the Party Quest started.");
         }

         return true;
      }
   }

   public void unregisterPlayer(MapleCharacter chr) {
      if (this.disposed) {
         chr.setEventInstance(null);
      } else {
         this.wL.lock();

         try {
            this.unregisterPlayer_NoLock(chr);
         } finally {
            this.wL.unlock();
         }
      }
   }

   public void unregisterAll() {
      this.wL.lock();

      try {
         this.removeAllEventSchedule();

         for (MapleCharacter chr : this.chars) {
            chr.setEventInstance(null);
         }

         this.chars.clear();
      } finally {
         this.wL.unlock();
      }
   }

   private boolean unregisterPlayer_NoLock(MapleCharacter chr) {
      if (this.name.equals("CWKPQ")) {
         MapleSquad squad = GameServer.getInstance(this.channel).getMapleSquad("CWKPQ");
         if (squad != null) {
            squad.removeMember(chr.getName());
            if (squad.getLeaderName().equals(chr.getName())) {
               this.em.setProperty("leader", "false");
            }
         }
      }

      chr.setEventInstance(null);
      if (this.disposed) {
         return false;
      } else if (this.chars.contains(chr)) {
         this.chars.remove(chr);
         chr.send(CField.getPartyDeathCount(List.of(), -1));
         return true;
      } else {
         return false;
      }
   }

   public final boolean disposeIfPlayerBelow(byte size, int towarp) {
      if (this.disposed) {
         return true;
      } else {
         Field map = null;
         if (towarp > 0) {
            map = this.getMapFactory().getMap(towarp);
         }

         this.wL.lock();

         boolean var12;
         try {
            if (this.chars == null || this.chars.size() > size) {
               return false;
            }

            for (MapleCharacter chr : new LinkedList<>(this.chars)) {
               if (chr != null) {
                  this.unregisterPlayer_NoLock(chr);
                  if (towarp > 0) {
                     chr.changeMap(map, map.getPortal(0));
                  }
               }
            }

            this.dispose_NoLock();
            var12 = true;
         } catch (Exception var10) {
            return false;
         } finally {
            this.wL.unlock();
         }

         return var12;
      }
   }

   public final void saveBossQuest(int points) {
      if (!this.disposed) {
         for (MapleCharacter chr : this.getPlayers()) {
            MapleQuestStatus record = chr.getQuestIfNullAdd(MapleQuest.getInstance(150001));
            if (record.getCustomData() != null) {
               record.setCustomData(String.valueOf(points + Integer.parseInt(record.getCustomData())));
            } else {
               record.setCustomData(String.valueOf(points));
            }

            chr.getTrait(MapleTrait.MapleTraitType.will).addExp(points / 100, chr);
         }
      }
   }

   public final void saveNX(int points) {
      if (!this.disposed) {
         for (MapleCharacter chr : this.getPlayers()) {
            chr.modifyCSPoints(1, points, true);
         }
      }
   }

   public List<MapleCharacter> getPlayers() {
      if (this.disposed) {
         return Collections.emptyList();
      } else {
         this.rL.lock();

         LinkedList var1;
         try {
            var1 = new LinkedList<>(this.chars);
         } finally {
            this.rL.unlock();
         }

         return var1;
      }
   }

   public List<Integer> getDisconnected() {
      return this.dced;
   }

   public final int getPlayerCount() {
      return this.disposed ? 0 : this.chars.size();
   }

   public void resetMobs() {
      if (this.mobs != null) {
         this.mobs.clear();
      }
   }

   public void registerMonster(MapleMonster mob) {
      if (!this.disposed) {
         this.mobs.add(mob);
         mob.setEventInstance(this);
      }
   }

   public void unregisterMonster(MapleMonster mob) {
      mob.setEventInstance(null);
      if (!this.disposed) {
         if (this.mobs.contains(mob)) {
            this.mobs.remove(mob);
         }

         if (mob.getId() == 8950101
            || mob.getId() == 8950102
            || mob.getId() == 8860000
            || mob.getId() == 8880000
            || mob.getId() == 8880101
            || mob.getId() == 8950100) {
            List<MapleMonster> removes = new LinkedList<>();

            for (MapleMonster mo : this.getMobs()) {
               removes.add(mo);
            }

            for (MapleMonster mo : removes) {
               mo.killed();
            }

            this.mobs.clear();
         }

         if (this.mobs.isEmpty()) {
            try {
               this.removeAllEventSchedule();
               this.em.getIv().invokeFunction("allMonstersDead", this);
            } catch (Exception var5) {
            }
         }
      }
   }

   public void playerKilled(MapleCharacter chr) {
      if (!this.disposed) {
         try {
            this.em.getIv().invokeFunction("playerDead", this, chr);
         } catch (Exception var3) {
         }
      }
   }

   public boolean revivePlayer(MapleCharacter chr) {
      if (this.disposed) {
         return false;
      } else {
         try {
            Object b = this.em.getIv().invokeFunction("playerRevive", this, chr);
            if (b instanceof Boolean) {
               return (Boolean)b;
            }
         } catch (Exception var3) {
         }

         return true;
      }
   }

   public void playerDisconnected(MapleCharacter chr, int idz) {
      if (!this.disposed) {
         byte ret;
         try {
            ret = ((Double)this.em.getIv().invokeFunction("playerDisconnected", this, chr)).byteValue();
         } catch (Exception var11) {
            ret = 0;
         }

         this.wL.lock();

         try {
            if (!this.disposed) {
               if (chr == null || chr.isAlive()) {
                  this.dced.add(idz);
               }

               if (chr != null) {
                  this.unregisterPlayer_NoLock(chr);
               }

               if (ret == 0) {
                  if (this.getPlayerCount() <= 0) {
                     this.dispose_NoLock();
                     return;
                  }
               } else if (ret > 0 && this.getPlayerCount() < ret || ret < 0 && (this.isLeader(chr) || this.getPlayerCount() < ret * -1)) {
                  for (MapleCharacter player : new LinkedList<>(this.chars)) {
                     if (player.getId() != idz) {
                        this.removePlayer(player);
                     }
                  }

                  this.dispose_NoLock();
                  return;
               }

               return;
            }
         } catch (Exception var12) {
            return;
         } finally {
            this.wL.unlock();
         }
      }
   }

   public void monsterKilled(MapleCharacter chr, MapleMonster mob) {
      if (!this.disposed) {
         try {
            int inc = (Integer)this.em.getIv().invokeFunction("monsterValue", this, mob.getId());
            if (this.disposed || chr == null) {
               return;
            }

            Integer kc = this.killCount.get(chr.getId());
            if (kc == null) {
               kc = inc;
            } else {
               kc = kc + inc;
            }

            this.killCount.put(chr.getId(), kc);
         } catch (ScriptException var5) {
         } catch (NoSuchMethodException var6) {
         } catch (Exception var7) {
         }
      }
   }

   public void monsterDamaged(MapleCharacter chr, MapleMonster mob, int damage) {
      List<Integer> mobs = Collections.unmodifiableList(Arrays.asList(9700037, 8850011));
      if (!this.disposed && mobs.contains(mob.getId())) {
         try {
            this.em.getIv().invokeFunction("monsterDamaged", this, chr, mob.getId(), damage);
         } catch (ScriptException var6) {
         } catch (NoSuchMethodException var7) {
         } catch (Exception var8) {
         }
      }
   }

   public void addPVPScore(MapleCharacter chr, int score) {
      if (!this.disposed) {
         try {
            this.em.getIv().invokeFunction("addPVPScore", this, chr, score);
         } catch (ScriptException var4) {
         } catch (NoSuchMethodException var5) {
         } catch (Exception var6) {
         }
      }
   }

   public int getKillCount(MapleCharacter chr) {
      if (this.disposed) {
         return 0;
      } else {
         Integer kc = this.killCount.get(chr.getId());
         return kc == null ? 0 : kc;
      }
   }

   public void dispose_NoLock() {
      if (!this.disposed && this.em != null) {
         String emN = this.em.getName();

         try {
            this.removeAllEventSchedule();
            this.disposed = true;

            for (MapleCharacter chr : this.chars) {
               chr.setEventInstance(null);
            }

            this.chars.clear();
            this.chars = null;
            this.damageInfo.clear();
            this.damageInfo = null;
            if (this.mobs.size() >= 1) {
               for (MapleMonster mob : this.mobs) {
                  if (mob != null) {
                     mob.setEventInstance(null);
                  }
               }
            }

            this.mobs.clear();
            this.mobs = null;
            this.killCount.clear();
            this.killCount = null;
            this.dced.clear();
            this.dced = null;
            this.timeStarted = 0L;
            this.eventTime = 0L;
            this.props.clear();
            this.props = null;

            for (int i = 0; i < this.mapIds.size(); i++) {
               if (this.isInstanced.get(i)) {
                  this.getMapFactory().removeInstanceMap(this.mapIds.get(i));
               }
            }

            this.mapIds.clear();
            this.mapIds = null;
            this.isInstanced.clear();
            this.isInstanced = null;
            this.em.disposeInstance(this.name);
         } catch (Exception var4) {
         }
      }
   }

   public void dispose() {
      this.wL.lock();

      try {
         this.dispose_NoLock();
      } finally {
         this.wL.unlock();
      }
   }

   public GameServer getChannelServer() {
      return GameServer.getInstance(this.channel);
   }

   public List<MapleMonster> getMobs() {
      return this.mobs;
   }

   public final void giveAchievement(int type) {
      if (!this.disposed) {
         for (MapleCharacter var3 : this.getPlayers()) {
            ;
         }
      }
   }

   public final void broadcastPlayerMsg(int type, String msg) {
      if (!this.disposed) {
         for (MapleCharacter chr : this.getPlayers()) {
            chr.dropMessage(type, msg);
         }
      }
   }

   public final void broadcastEnablePvP(MapleCharacter chr) {
      if (!this.disposed) {
         for (MapleCharacter mc : chr.getMap().getCharacters()) {
            mc.enablePvP();
         }
      }
   }

   public final List<Pair<Integer, String>> newPair() {
      return new ArrayList<>();
   }

   public void addToPair(List<Pair<Integer, String>> e, int e1, String e2) {
      e.add(new Pair<>(e1, e2));
   }

   public final List<Pair<Integer, MapleCharacter>> newPair_chr() {
      return new ArrayList<>();
   }

   public void addToPair_chr(List<Pair<Integer, MapleCharacter>> e, int e1, MapleCharacter e2) {
      e.add(new Pair<>(e1, e2));
   }

   public final void broadcastPacket(byte[] p) {
      if (!this.disposed) {
         for (MapleCharacter chr : this.getPlayers()) {
            chr.getClient().getSession().writeAndFlush(p);
         }
      }
   }

   public final void broadcastTeamPacket(byte[] p, int team) {
      if (!this.disposed) {
         for (MapleCharacter chr : this.getPlayers()) {
            if (chr.getTeam() == team) {
               chr.getClient().getSession().writeAndFlush(p);
            }
         }
      }
   }

   public final Field createInstanceMap(int mapid) {
      if (this.disposed) {
         return null;
      } else {
         int assignedid = EventScriptManager.getNewInstanceMapId();
         this.mapIds.add(assignedid);
         this.isInstanced.add(true);
         return this.getMapFactory().CreateInstanceMap(mapid, true, true, true, assignedid);
      }
   }

   public final Field createInstanceMapS(int mapid) {
      if (this.disposed) {
         return null;
      } else {
         int assignedid = EventScriptManager.getNewInstanceMapId();
         this.mapIds.add(assignedid);
         this.isInstanced.add(true);
         return this.getMapFactory().CreateInstanceMap(mapid, false, false, false, assignedid);
      }
   }

   public final Field setInstanceMap(int mapid) {
      if (this.disposed) {
         return this.getMapFactory().getMap(mapid);
      } else {
         this.mapIds.add(mapid);
         this.isInstanced.add(false);
         return this.getMapFactory().getMap(mapid);
      }
   }

   public final MapleMapFactory getMapFactory() {
      return this.getChannelServer().getMapFactory();
   }

   public final Field getMapInstance(int args) {
      if (this.disposed) {
         return null;
      } else {
         try {
            boolean instanced = false;
            int trueMapID = -1;
            if (args >= this.mapIds.size()) {
               trueMapID = args;
            } else {
               trueMapID = this.mapIds.get(args);
               instanced = this.isInstanced.get(args);
            }

            Field map = null;
            if (!instanced) {
               map = this.getMapFactory().getMap(trueMapID);
               if (map == null) {
                  return null;
               }

               if (map.getCharactersSize() == 0 && this.em.getProperty("shuffleReactors") != null && this.em.getProperty("shuffleReactors").equals("true")) {
                  map.shuffleReactors();
               }
            } else {
               map = this.getMapFactory().getInstanceMap(trueMapID);
               if (map == null) {
                  return null;
               }

               if (map.getCharactersSize() == 0 && this.em.getProperty("shuffleReactors") != null && this.em.getProperty("shuffleReactors").equals("true")) {
                  map.shuffleReactors();
               }
            }

            return map;
         } catch (NullPointerException var5) {
            return null;
         }
      }
   }

   public final void schedule(final String methodName, long delay) {
      if (!this.disposed) {
         Timer.EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               if (!EventInstanceManager.this.disposed && EventInstanceManager.this != null && EventInstanceManager.this.em != null) {
                  try {
                     EventInstanceManager.this.em.getIv().invokeFunction(methodName, EventInstanceManager.this);
                  } catch (NullPointerException var2) {
                  } catch (Exception var3) {
                  }
               }
            }
         }, delay);
      }
   }

   public final String getName() {
      return this.name;
   }

   public final void setProperty(String key, String value) {
      if (!this.disposed) {
         this.props.setProperty(key, value);
      }
   }

   public final void setProperty(String key, int value) {
      if (!this.disposed) {
         this.props.setProperty(key, String.valueOf(value));
      }
   }

   public final Object setProperty(String key, String value, boolean prev) {
      return this.disposed ? null : this.props.setProperty(key, value);
   }

   public final String getProperty(String key) {
      return this.disposed ? "" : this.props.getProperty(key);
   }

   public final Properties getProperties() {
      return this.props;
   }

   public final void leftParty(MapleCharacter chr) {
      if (!this.disposed) {
         ;
      }
   }

   public final void disbandParty() {
      if (!this.disposed) {
         ;
      }
   }

   public final void finishPQ() {
      if (!this.disposed) {
         try {
            this.em.getIv().invokeFunction("clearPQ", this);
         } catch (Exception var2) {
         }
      }
   }

   public final void removePlayer(MapleCharacter chr) {
      if (!this.disposed) {
         try {
            this.em.getIv().invokeFunction("playerExit", this, chr);
         } catch (Exception var3) {
         }
      }
   }

   public void onMapLoad(MapleCharacter chr) {
      if (!this.disposed) {
         try {
            this.em.getIv().invokeFunction("onMapLoad", this, chr);
         } catch (ScriptException var3) {
         } catch (NoSuchMethodException var4) {
         }
      }
   }

   public boolean isLeader(MapleCharacter chr) {
      return chr != null && chr.getParty() != null && chr.getParty().getLeader().getId() == chr.getId();
   }

   public void registerSquad(MapleSquad squad, Field map, int questID) {
      if (!this.disposed) {
         int mapid = map.getId();

         for (String chr : squad.getMembers()) {
            MapleCharacter player = squad.getChar(chr);
            if (player != null && player.getMapId() == mapid) {
               if (questID > 0) {
                  player.getQuestIfNullAdd(MapleQuest.getInstance(questID)).setCustomData(String.valueOf(System.currentTimeMillis()));
               }

               this.registerPlayer(player);
               if (player.getParty() != null) {
                  PartySearch ps = Center.Party.getSearch(player.getParty());
                  if (ps != null) {
                     Center.Party.removeSearch(ps, "The Party Listing has been removed because the Party Quest has started.");
                  }
               }
            }
         }

         squad.setStatus((byte)2);
         squad.getBeginMap().broadcastMessage(CField.stopClock());
      }
   }

   public boolean isDisconnected(MapleCharacter chr) {
      return this.disposed ? false : this.dced.contains(chr.getId());
   }

   public void removeDisconnected(int id) {
      if (!this.disposed) {
         this.dced.remove(id);
      }
   }

   public EventManager getEventManager() {
      return this.em;
   }

   public void applyBuff(MapleCharacter chr, int id) {
      MapleItemInformationProvider.getInstance().getItemEffect(id).applyTo(chr);
      chr.getClient().getSession().writeAndFlush(CWvsContext.InfoPacket.getStatusMsg(id));
   }

   public void applySkill(MapleCharacter chr, int id) {
      SkillFactory.getSkill(id).getEffect(1).applyTo(chr);
   }

   public void worldGMMessage(int type, String message) {
      Center.Broadcast.broadcastMessage(CField.chatMsg(type, message));
   }

   public final void startHungryMuto(MapleCharacter chr, int difficulty, int startMap) {
      int type = Randomizer.rand(0, 7);
      HungryMuto muto = new HungryMuto(type);
      Party party = chr.getParty();
      if (party != null) {
         party.getPartyMemberList()
            .stream()
            .filter(p -> p.getChannel() == chr.getClient().getChannel())
            .filter(p -> p.isOnline())
            .forEach(
               p -> {
                  MapleCharacter player = chr.getClient().getChannelServer().getPlayerStorage().getCharacterById(p.getId());
                  if (player != null) {
                     player.setHungryMuto(muto);
                     player.warp(startMap);
                     player.getClient().getSession().writeAndFlush(new HungryMuto.TimerSet(600000).encode());
                     player.getClient()
                        .getSession()
                        .writeAndFlush(
                           new HungryMuto.GameInit(
                                 FoodType.getFoodType(type), difficulty, 130000, HungryMuto.getBonusTime(130000, muto.getRecipes().length), muto.getRecipes()
                              )
                              .encode()
                        );
                     player.getClient().getSession().writeAndFlush(CField.fieldSetVariable("rType", "0"));
                     player.getClient().getSession().writeAndFlush(CField.fieldSetVariable("rCount", "0"));
                     player.getClient().getSession().writeAndFlush(CField.fieldValue("foodType", String.valueOf(type)));
                     player.getClient().getSession().writeAndFlush(CField.environmentChange("event/start", 15));
                     player.getClient().getSession().writeAndFlush(CField.playSound("Dojang/cleard", 100));
                     player.getClient().getSession().writeAndFlush(CField.MapEff("Map/Effect3.img/hungryMutoMsg/msg1"));
                     player.getClient().getSession().writeAndFlush(CField.fieldValue("score", String.valueOf(muto.getScore())));
                     player.getClient().getSession().writeAndFlush(CField.fieldValue("time", String.valueOf(muto.getTime())));
                  }
               }
            );
      }

      muto.startGame(chr, difficulty);
   }

   public Map<String, ScheduledFuture<?>> getAllEventSchedule() {
      return new HashMap<>(this.eventScheduleList);
   }

   public void removeAllEventSchedule() {
      for (Entry<String, ScheduledFuture<?>> entry : new HashMap<>(this.eventScheduleList).entrySet()) {
         ScheduledFuture s = entry.getValue();
         if (s != null) {
            s.cancel(true);
            this.eventScheduleList.remove(entry.getKey());
         }
      }
   }

   public void removeEventSchedule(String key) {
      ScheduledFuture s = this.eventScheduleList.get(key);
      if (s != null) {
         s.cancel(true);
         this.eventScheduleList.remove(key);
      }
   }

   public ScheduledFuture addEventSchedule(String keyName, ScheduledFuture<?> schedule) {
      return this.eventScheduleList.put(keyName, schedule);
   }

   public ScheduledFuture registerEventSchedule(String keyName, long delay, String methodName, Object... args) {
      return this.addEventSchedule(keyName, Timer.MapTimer.getInstance().register(() -> this.invoke(this, methodName, args), delay));
   }

   private Object invoke(Object invokeOn, String methodName, Object... args) {
      List<Class<?>> classList = Arrays.stream(args).map(Object::getClass).collect(Collectors.toList());
      Class<?>[] classes = classList.stream().map(Util::convertBoxedToPrimitiveClass).toArray(Class[]::new);

      try {
         Method func = this.getClass().getMethod(methodName, classes);
         return func.invoke(invokeOn, args);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException var8) {
         System.out.println("EventInstanceManage.invoke 함수 실행중 오류발생" + var8.toString());
         var8.printStackTrace();
         return null;
      }
   }

   public void spawnSelfDestructMob(Field map) {
      if (map.getCharactersSize() != 0) {
         MapleMonster mob = MapleLifeFactory.getMonster(8950103);
         mob.setPosition(new Point(507, -248));
         mob.setStance(13);
         map.spawnMonster(mob, -2);
         mob = MapleLifeFactory.getMonster(8950104);
         mob.setPosition(new Point(-420, -421));
         mob.setStance(12);
         map.spawnMonster(mob, -2);
         mob = MapleLifeFactory.getMonster(8950105);
         mob.setPosition(new Point(-511, -250));
         mob.setStance(12);
         map.spawnMonster(mob, -2);
         mob = MapleLifeFactory.getMonster(8950107);
         mob.setPosition(new Point(417, -423));
         mob.setStance(13);
         map.spawnMonster(mob, -2);
      }
   }

   public void createObstacleAtom(Field map, ObstacleAtomEnum oae, int key, int damage, int velocity, int angle, int amount, int proc) {
      int xLeft = map.getLeft();
      int yTop = map.getTop();
      ObstacleInRowInfo obstacleInRowInfo = new ObstacleInRowInfo(4, false, 5000, 0, 0, 0);
      ObstacleRadialInfo obstacleRadianInfo = new ObstacleRadialInfo(4, 0, 0, 0, 0, 0);
      Set<ObstacleAtom> obstacleAtomInfosSet = new HashSet<>();

      for (int i = 0; i < amount; i++) {
         if (Randomizer.nextInt(100) < proc) {
            int randomX = Randomizer.nextInt(map.getWidth()) + xLeft;
            Point position = new Point(randomX, yTop + 55);
            MapleFoothold fh = map.getFootholds().findBelow(position);
            if (fh != null) {
               Point pos = map.calcPointBelow(position);
               int footholdY = pos.y;
               int height = position.y - footholdY;
               height = height < 0 ? -height : height;
               ObstacleAtom atom = new ObstacleAtom(oae.getType(), position, new Point(0, 0), 0);
               atom.setKey(key);
               atom.setHitBoxRange(oae.getHitBox());
               atom.setTrueDamR(damage);
               atom.setHeight(height);
               atom.setMaxP(velocity);
               atom.setLength(height);
               atom.setAngle(angle);
               obstacleAtomInfosSet.add(atom);
            }
         }
      }

      map.broadcastMessage(CField.createObstacle(ObstacleAtomCreateType.NORMAL, obstacleInRowInfo, obstacleRadianInfo, obstacleAtomInfosSet));
   }

   public void startMapEffect(MapleCharacter player, String msg, int itemID, int seconds) {
      player.send(CField.startMapEffect(msg, itemID, true, seconds));
   }

   public void setFieldSetTimerGauge(int second, Consumer<Boolean> consumer) {
      this.fieldSetTimerGauge = consumer;
      this.timerGauge = second * 1000;
      this.setFieldSetTimerGuageEndTime(System.currentTimeMillis() + second * 1000);
      this.fieldSetTimerScheduled = Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            EventInstanceManager.this.fieldSetTimerGauge.accept(false);
            EventInstanceManager.this.fieldSetTimerGauge = null;
            EventInstanceManager.this.fieldSetTimerScheduled = null;
            EventInstanceManager.this.fieldSetTimerGuageEndTime = 0L;
            EventInstanceManager.this.timerGauge = 0;
         }
      }, second * 1000);
   }

   public void doTimerGaugeEndAction(boolean success, MapleCharacter player) {
      if (this.fieldSetTimerGauge != null) {
         this.fieldSetTimerGauge.accept(success);
         this.fieldSetTimerGauge = null;
         this.fieldSetTimerScheduled.cancel(true);
         this.fieldSetTimerScheduled = null;
         this.fieldSetTimerGuageEndTime = 0L;
         this.timerGauge = 0;
      } else {
         player.setRegisterTransferField(player.getMapId() - 10);
         player.setRegisterTransferFieldTime(1000L);
      }
   }

   public long getFieldSetTimerGuageEndTime() {
      return this.fieldSetTimerGuageEndTime;
   }

   public void setFieldSetTimerGuageEndTime(long fieldSetTimerGuageEndTime) {
      this.fieldSetTimerGuageEndTime = fieldSetTimerGuageEndTime;
   }

   public void broadcastTimerGauge() {
      if (this.getFieldSetTimerGuageEndTime() > 0L) {
         int sec = (int)(this.getFieldSetTimerGuageEndTime() - System.currentTimeMillis());
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.CLOCK.getValue());
         packet.write(4);
         packet.writeInt(this.timerGauge);
         packet.writeInt(sec);
         this.broadcastPacket(packet.getPacket());
      }
   }

   public void broadcastResetTimerGuage() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CLOCK.getValue());
      packet.write(4);
      packet.writeInt(-1);
      packet.writeInt(-1);
      this.broadcastPacket(packet.getPacket());
   }

   public Map<String, Long> getDamageInfo() {
      return this.damageInfo;
   }

   public void setDamageInfo(Map<String, Long> damageInfo) {
      this.damageInfo = damageInfo;
   }
}
