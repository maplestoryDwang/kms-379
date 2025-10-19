package objects.fields.gameobject;

import java.awt.Rectangle;
import network.models.CField;
import objects.fields.Field;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.users.MapleClient;
import objects.utils.Pair;
import objects.utils.Timer;
import scripting.ReactorScriptManager;

public class Reactor extends MapleMapObject {
   private int rid;
   private ReactorStats stats;
   private byte state = 0;
   private byte facingDirection = 0;
   private int delay = -1;
   private int rank = 0;
   private Field map;
   private String name = "";
   private boolean timerActive = false;
   private boolean alive = true;
   private boolean custom = false;

   public Reactor(ReactorStats stats, int rid) {
      this.stats = stats;
      this.rid = rid;
   }

   public void setCustom(boolean c) {
      this.custom = c;
   }

   public boolean isCustom() {
      return this.custom;
   }

   public final void setFacingDirection(byte facingDirection) {
      this.facingDirection = facingDirection;
   }

   public final byte getFacingDirection() {
      return this.facingDirection;
   }

   public void setTimerActive(boolean active) {
      this.timerActive = active;
   }

   public boolean isTimerActive() {
      return this.timerActive;
   }

   public int getReactorId() {
      return this.rid;
   }

   public void setState(byte state) {
      this.state = state;
   }

   public byte getState() {
      return this.state;
   }

   public boolean isAlive() {
      return this.alive;
   }

   public void setAlive(boolean alive) {
      this.alive = alive;
   }

   public void setDelay(int delay) {
      this.delay = delay;
   }

   public int getDelay() {
      return this.delay;
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.REACTOR;
   }

   public int getReactorType() {
      return this.stats.getType(this.state);
   }

   public byte getTouch() {
      return this.stats.canTouch(this.state);
   }

   public void setMap(Field map) {
      this.map = map;
   }

   public Field getMap() {
      return this.map;
   }

   public Pair<Integer, Integer> getReactItem() {
      return this.stats.getReactItem(this.state);
   }

   @Override
   public void sendDestroyData(MapleClient client) {
      client.getSession().writeAndFlush(CField.destroyReactor(this));
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      client.getSession().writeAndFlush(CField.spawnReactor(this));
   }

   public void forceStartReactor(MapleClient c) {
      ReactorScriptManager.getInstance().act(c, this);
   }

   public void forceHitReactor(byte newState) {
      this.setState(newState);
      this.setTimerActive(false);
      this.map.broadcastMessage(CField.triggerReactor(this, 0));
   }

   public void hitReactor(MapleClient c) {
      this.hitReactor(0, (short)0, c);
   }

   public void forceTrigger() {
      this.map.broadcastMessage(CField.triggerReactor(this, 0));
   }

   public void delayedDestroyReactor(long delay) {
      Timer.MapTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            Reactor.this.map.destroyReactor(Reactor.this.getObjectId());
         }
      }, delay);
   }

   public void hitReactor(int charPos, short stance, MapleClient c) {
      if (this.stats.getType(this.state) < 999 && this.stats.getType(this.state) != -1) {
         byte oldState = this.state;
         if (this.stats.getType(this.state) != 2 || charPos != 0 && charPos != 2) {
            this.state = this.stats.getNextState(this.state);
            if (this.stats.getNextState(this.state) != -1 && this.stats.getType(this.state) != 999) {
               boolean done = false;
               this.map.broadcastMessage(CField.triggerReactor(this, stance));
               if (this.state == this.stats.getNextState(this.state)
                  || this.rid == 2618000
                  || this.rid == 2309000
                  || this.rid == 1058018
                  || this.rid == 1058019) {
                  if (this.rid > 200013) {
                     ReactorScriptManager.getInstance().act(c, this);
                  }

                  done = true;
               }

               if (this.stats.getTimeOut(this.state) > 0) {
                  if (!done && this.rid > 200013) {
                     ReactorScriptManager.getInstance().act(c, this);
                  }

                  if (this.rid == 2112018) {
                     ReactorScriptManager.getInstance().destroy(c, this);
                     oldState = 0;
                  }

                  this.scheduleSetState(this.state, oldState, this.stats.getTimeOut(this.state));
               }
            } else {
               if ((this.stats.getType(this.state) < 100 || this.stats.getType(this.state) == 999) && this.delay > 0) {
                  if (this.delay > 0) {
                     this.map.destroyReactor(this.getObjectId());
                  } else {
                     this.map.broadcastMessage(CField.triggerReactor(this, stance));
                  }
               } else {
                  this.map.broadcastMessage(CField.triggerReactor(this, stance));
               }

               if (this.rid != 1058018 && this.rid != 1058019 && this.rid != 2112018) {
                  ReactorScriptManager.getInstance().act(c, this);
               } else {
                  ReactorScriptManager.getInstance().destroy(c, this);
               }
            }
         }
      }
   }

   public Rectangle getArea() {
      int height = this.stats.getBR().y - this.stats.getTL().y;
      int width = this.stats.getBR().x - this.stats.getTL().x;
      int origX = this.getTruePosition().x + this.stats.getTL().x;
      int origY = this.getTruePosition().y + this.stats.getTL().y;
      return new Rectangle(origX, origY, width, height);
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setRank(int r) {
      this.rank = r;
   }

   public int getRank() {
      return this.rank;
   }

   @Override
   public String toString() {
      return "Reactor "
         + this.getObjectId()
         + " of id "
         + this.rid
         + " at position "
         + this.getPosition().toString()
         + " state"
         + this.state
         + " type "
         + this.stats.getType(this.state);
   }

   public void delayedHitReactor(final MapleClient c, long delay) {
      Timer.MapTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            Reactor.this.hitReactor(c);
         }
      }, delay);
   }

   public void scheduleSetState(final byte oldState, final byte newState, long delay) {
      Timer.MapTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (Reactor.this.state == oldState) {
               Reactor.this.forceHitReactor(newState);
            }
         }
      }, delay);
   }
}
