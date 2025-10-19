package objects.fields.events;

import constants.GameConstants;
import network.RandomRewards;
import network.center.Center;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.fields.FieldLimitType;
import objects.fields.SavedLocationType;
import objects.item.MapleInventoryManipulator;
import objects.users.MapleCharacter;
import objects.utils.FileoutputUtil;
import objects.utils.Randomizer;
import objects.utils.StringUtil;
import objects.utils.Timer;

public abstract class MapleEvent {
   protected MapleEventType type;
   protected int channel;
   protected int playerCount = 0;
   protected boolean isRunning = false;

   public MapleEvent(int channel, MapleEventType type) {
      this.channel = channel;
      this.type = type;
   }

   public void incrementPlayerCount() {
      this.playerCount++;
      if (this.playerCount == 250) {
         setEvent(GameServer.getInstance(this.channel), true);
      }
   }

   public MapleEventType getType() {
      return this.type;
   }

   public boolean isRunning() {
      return this.isRunning;
   }

   public Field getMap(int i) {
      return this.getChannelServer().getMapFactory().getMap(this.type.mapids[i]);
   }

   public GameServer getChannelServer() {
      return GameServer.getInstance(this.channel);
   }

   public void broadcast(byte[] packet) {
      for (int i = 0; i < this.type.mapids.length; i++) {
         this.getMap(i).broadcastMessage(packet);
      }
   }

   public static void givePrize(MapleCharacter chr) {
      int reward = RandomRewards.getEventReward();
      if (reward == 0) {
         int mes = Randomizer.nextInt(9000000) + 1000000;
         chr.gainMeso(mes, true, false, true);
         chr.dropMessage(5, "축하합니다! " + mes + " 메소를 얻으셨습니다!");
      } else if (reward == 1) {
         int cs = Randomizer.nextInt(4000) + 1000;
         chr.modifyCSPoints(1, cs, true);
         chr.dropMessage(5, "축하합니다! " + cs + " 캐시를 얻으셨습니다!");
      } else if (reward == 2) {
         chr.setRealCash(chr.getRealCash() + 1);
         chr.dropMessage(5, "축하합니다! V포인트 1을 획득하셨습니다.");
      } else if (reward == 3) {
         chr.addFame(10);
         chr.dropMessage(5, "축하합니다! 인기도 10을 얻으셨습니다.");
      } else if (reward == 4) {
         chr.dropMessage(5, "아쉽지만, 보상을 획득하지 못하였습니다. 다음 기회에 ~ ^^");
      } else {
         int max_quantity = 1;
         switch (reward) {
            case 2022121:
               max_quantity = 10;
               break;
            case 4031307:
            case 5050000:
               max_quantity = 5;
               break;
            case 5062000:
               max_quantity = 3;
               break;
            case 5220000:
               max_quantity = 25;
         }

         int quantity = (max_quantity > 1 ? Randomizer.nextInt(max_quantity) : 0) + 1;
         if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, quantity, "")) {
            MapleInventoryManipulator.addById(chr.getClient(), reward, (short)quantity, "Event prize on " + FileoutputUtil.CurrentReadable_Date());
         } else {
            givePrize(chr);
         }
      }
   }

   public abstract void finished(MapleCharacter var1);

   public abstract void startEvent();

   public void onMapLoad(MapleCharacter chr) {
      if (GameConstants.isEventMap(chr.getMapId())
         && FieldLimitType.Event.check(chr.getMap().getFieldLimit())
         && FieldLimitType.Event2.check(chr.getMap().getFieldLimit())) {
         chr.getClient().getSession().writeAndFlush(CField.showEventInstructions());
      }
   }

   public void warpBack(MapleCharacter chr) {
      int map = chr.getSavedLocation(SavedLocationType.EVENT);
      if (map <= -1) {
         map = 104000000;
      }

      Field mapp = chr.getClient().getChannelServer().getMapFactory().getMap(map);
      chr.changeMap(mapp, mapp.getPortal(0));
   }

   public void reset() {
      this.isRunning = true;
      this.playerCount = 0;
   }

   public void unreset() {
      this.isRunning = false;
      this.playerCount = 0;
   }

   public static final void setEvent(GameServer cserv, boolean auto) {
      if (auto && cserv.getEvent() > -1) {
         for (MapleEventType t : MapleEventType.values()) {
            final MapleEvent e = cserv.getEvent(t);
            if (e.isRunning) {
               for (int i : e.type.mapids) {
                  if (cserv.getEvent() == i) {
                     Center.Broadcast.broadcastMessage(CWvsContext.serverNotice(0, "Entries for the event are now closed!"));
                     e.broadcast(CWvsContext.serverNotice(0, "The event will start in 30 seconds!"));
                     e.broadcast(CField.getClock(30));
                     Timer.EventTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                           e.startEvent();
                        }
                     }, 30000L);
                     break;
                  }
               }
            }
         }
      }

      cserv.setEvent(-1);
   }

   public static final void mapLoad(MapleCharacter chr, int channel) {
      if (chr != null) {
         for (MapleEventType t : MapleEventType.values()) {
            MapleEvent e = GameServer.getInstance(channel).getEvent(t);
            if (e.isRunning) {
               if (chr.getMapId() == 109050000) {
                  e.finished(chr);
               }

               for (int i = 0; i < e.type.mapids.length; i++) {
                  if (chr.getMapId() == e.type.mapids[i]) {
                     e.onMapLoad(chr);
                     if (i == 0) {
                        e.incrementPlayerCount();
                     }
                  }
               }
            }
         }
      }
   }

   public static final void onStartEvent(MapleCharacter chr) {
      for (MapleEventType t : MapleEventType.values()) {
         MapleEvent e = chr.getClient().getChannelServer().getEvent(t);
         if (e.isRunning) {
            for (int i : e.type.mapids) {
               if (chr.getMapId() == i) {
                  e.startEvent();
                  setEvent(chr.getClient().getChannelServer(), false);
                  chr.dropMessage(5, t + " 이벤트가 시작되었습니다.");
               }
            }
         }
      }
   }

   public static final String scheduleEvent(MapleEventType event, GameServer cserv) {
      if (cserv.getEvent() == -1 && cserv.getEvent(event) != null) {
         for (int i : cserv.getEvent(event).type.mapids) {
            if (cserv.getMapFactory().getMap(i).getCharactersSize() > 0) {
               return "The event is already running.";
            }
         }

         cserv.setEvent(cserv.getEvent(event).type.mapids[0]);
         cserv.getEvent(event).reset();
         Center.Broadcast.broadcastMessage(
            CWvsContext.serverNotice(
               0,
               "Hello "
                  + cserv.getServerName()
                  + "! Let's play a "
                  + StringUtil.makeEnumHumanReadable(event.name())
                  + " event in channel "
                  + cserv.getChannel()
                  + "! Change to channel "
                  + cserv.getChannel()
                  + " and use @event command!"
            )
         );
         return "";
      } else {
         return "The event must not have been already scheduled.";
      }
   }
}
