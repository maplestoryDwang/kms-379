package objects.fields.child.etc;

import constants.JosaType;
import constants.Locales;
import java.awt.Point;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.users.MapleCharacter;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Timer;

public class Field_MMRace extends Field {
   private boolean endGame = true;
   private boolean readyRace = false;
   private int startTimer = 20;
   private MapleMonster victoryMob = null;
   public HashMap<Integer, Pair<String, Long>> participateUsers = new HashMap<>();
   private int gameRound = 1;
   public ScheduledFuture<?> eventRace = null;
   public HashMap<String, Integer> winningRate = new HashMap<>();
   public HashMap<Integer, Long> accReward = new HashMap<>();

   public Field_MMRace(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if ((this.eventRace == null && this.startTimer == 0 || this.eventRace == null && this.startTimer == 20) && !this.readyRace) {
         this.readyRace = true;
         this.broadcastMessage(CField.getClock(20));
      }

      if (this.readyRace) {
         if (this.startTimer <= 0) {
            PacketEncoder p = new PacketEncoder();
            p.writeShort(SendPacketOpcode.STOP_CLOCK.getValue());
            p.encodeBuffer(new byte[100]);
            this.broadcastMessage(p.getPacket());
            this.broadcastMessage(CField.makeEffectScreen("killing/first/start"));
            this.readyRace = false;
            this.startTimer = 20;
            this.broadcastMessage(CWvsContext.serverNotice(6, "멍멍이 레이싱이 시작되었습니다."));
            MapleMonster buggy = MapleLifeFactory.getMonster(9305409);
            buggy.setEventName("버기");
            MapleMonster ealroo = MapleLifeFactory.getMonster(9305410);
            ealroo.setEventName("얼루");
            MapleMonster grodon = MapleLifeFactory.getMonster(9305415);
            grodon.setEventName("그로돈");
            MapleMonster coondoora = MapleLifeFactory.getMonster(9305417);
            coondoora.setEventName("쿤두라");
            MapleMonster[] mobs = new MapleMonster[]{buggy, ealroo, grodon, coondoora};
            this.spawnMonsterOnGroundBelow(buggy, new Point(910, 140));
            this.spawnMonsterOnGroundBelow(ealroo, new Point(910, -40));
            this.spawnMonsterOnGroundBelow(grodon, new Point(910, -220));
            this.spawnMonsterOnGroundBelow(coondoora, new Point(910, -400));
            MobTemporaryStatEffect eff = new MobTemporaryStatEffect(MobTemporaryStatFlag.DAZZLE, 1, 30001062, null, false);
            eff.setDuration(500000);
            eff.setCancelTask(500000L);
            eff.setValue(1);

            for (MapleMonster mob : mobs) {
               mob.applyStatus(eff);
            }

            this.RaceEvent();
            return;
         }

         if (this.startTimer % 5 == 0) {
            this.broadcastMessage(CWvsContext.serverNotice(5, "게임 시작까지 " + this.startTimer + "초 남았습니다. 참여하지 않은 분들은 빨리 참여해 주세요!"));
         }

         this.startTimer--;
      }
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      player.dropMessage(5, "멍멍 레이스에 참여하고 싶다면 나 두아트에게 말을 걸어보라고~!");
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
   }

   public boolean isReadyRace() {
      return this.readyRace;
   }

   public void setReadyRace(boolean readyRace) {
      this.readyRace = readyRace;
   }

   public HashMap<Integer, Pair<String, Long>> getParticipateUsers() {
      return this.participateUsers;
   }

   public void setVictoryMob(MapleMonster mob) {
      this.winningRate.putIfAbsent(mob.getEventName(), 0);
      this.winningRate.put(mob.getEventName(), this.winningRate.get(mob.getEventName()) + 1);
      this.broadcastMessage(CWvsContext.serverNotice(6, "제 " + this.gameRound + "회 운동회의 우승자는 '#" + mob.getEventName() + "' 입니다."));
      this.victoryMob = mob;
      this.gameRound++;
      this.killAllMonsters(true);

      for (MapleMonster a : this.getAllMonstersThreadsafe()) {
         this.removeMonster(a);
      }

      for (Integer par : this.participateUsers.keySet()) {
         if (((String)this.participateUsers.get(par).left).equals(mob.getEventName())) {
            this.accReward.putIfAbsent(par, 0L);
            this.accReward.put(par, this.accReward.get(par) + (Long)this.participateUsers.get(par).right * 3L);
         }
      }

      this.participateUsers.clear();
      this.participateUsers = new HashMap<>();
      this.RaceEvent();
   }

   public void RaceEvent() {
      if (this.eventRace != null) {
         this.eventRace.cancel(false);
         this.eventRace = null;
      } else {
         this.eventRace = Timer.EventTimer.getInstance()
            .register(
               new Runnable() {
                  @Override
                  public void run() {
                     for (MapleMonster m : Field_MMRace.this.getAllMonstersThreadsafe()) {
                        if (Randomizer.nextInt(100) < 0) {
                           MobTemporaryStatEffect[] eff = new MobTemporaryStatEffect[]{
                              new MobTemporaryStatEffect(MobTemporaryStatFlag.SPEED, -40, 90001002, null, false)
                           };
                           int randomBuff = Randomizer.nextInt(eff.length);
                           eff[randomBuff].setDuration(5000);
                           eff[randomBuff].setCancelTask(5000L);
                           eff[randomBuff].setValue(1);
                           m.applyStatus(eff[randomBuff]);
                           Field_MMRace.this.broadcastMessage(
                              CWvsContext.serverNotice(6, m.getEventName() + Locales.getKoreanJosa(m.getEventName(), JosaType.이가) + " 슬로우에 걸렸습니다!")
                           );
                        }
                     }
                  }
               },
               2000L
            );
      }
   }
}
