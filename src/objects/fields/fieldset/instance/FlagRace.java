package objects.fields.fieldset.instance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.fields.Field;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Timer;

public class FlagRace extends FieldSetInstance {
   boolean startGame = false;
   HashMap<String, Integer> flagGoalNumber = new HashMap<>();

   public FlagRace(FieldSet fs, FieldSetInstanceMap fsim, MapleCharacter leader) {
      super(fs, fsim, leader);
      this.init(fs.channel);
   }

   @Override
   public void init(int channel) {
      this.channel = channel;
      this.fieldSeteventTime = 120000;
      this.setFieldSetStartTime(System.currentTimeMillis());
      this.fieldSetEndTime = this.getFieldSetStartTime() + this.fieldSeteventTime;
      this.remainingTime = (int)((this.fieldSeteventTime - (System.currentTimeMillis() - this.getFieldSetStartTime())) / 1000L);

      for (Integer map : this.fsim.instances) {
         this.field(map).resetFully(false);
         this.field(map).setLastRespawnTime(System.currentTimeMillis());
         this.field(map).setFieldSetInstance(this);
      }

      this.fs.fInstance.putIfAbsent(this, new ArrayList<>());
      this.waitGameStart();
   }

   @Override
   public void userEnter(MapleCharacter user) {
      if (user.getMap().getId() == this.fsim.instances.get(0)) {
         user.send(CField.getClock((int)(this.fieldSeteventTime - (System.currentTimeMillis() - this.getFieldSetStartTime())) / 1000));
      } else if (user.getMap().getId() == this.fsim.instances.get(1)) {
         SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd/HH/mm");
         Date lastTime = null;

         try {
            lastTime = sdf.parse(user.getOneInfo(32581, "date"));
         } catch (Exception var5) {
            System.out.println("Flag Race Err");
            var5.printStackTrace();
            lastTime = null;
         }

         if (lastTime != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(lastTime);
            cal.set(7, 2);
            cal.set(10, 0);
            cal.set(12, 0);
            cal.set(13, 0);
            cal.set(14, 0);
            cal.set(11, 0);
            cal.set(5, cal.getTime().getDate() + 7);
            if (cal.getTime().getTime() <= new Date().getTime()) {
               user.updateOneInfo(32581, "date", sdf.format(new Date()));
               user.updateOneInfo(32581, "weeklyRecord", "0");
            }
         }

         user.updateOneInfo(32581, "date", sdf.format(new Date()));
         user.updateOneInfo(32581, "complete", "0");
         user.updateOneInfo(32581, "lap", "0");
         user.updateOneInfo(32581, "finish", "0");
         user.updateOneInfo(32581, "mode", "1");
         user.updateOneInfo(32581, "record", "0");
         user.send(CField.getClock(20));
      }
   }

   @Override
   public void userLeave(MapleCharacter user, Field to) {
      user.temporaryStatReset(SecondaryStatFlag.FlagRace_CramponShoes);
      user.send(this.showFlagRaceSkillUI(false));
   }

   @Override
   public void userDead(MapleCharacter user) {
   }

   @Override
   public void userLeftParty(MapleCharacter user) {
      super.userLeftParty(user);
   }

   @Override
   public void userDisbandParty() {
      super.userDisbandParty();
   }

   @Override
   public void userDisconnected(MapleCharacter user) {
      super.userDisconnected(user);
   }

   @Override
   public void mobDead(MapleMonster mMob) {
   }

   public void waitGameStart() {
      this.eventSchedules.put("waitGame1", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!FlagRace.this.dispose) {
               Field map = FlagRace.this.field(FlagRace.this.fsim.instances.get(0));
               if (map.getCharactersSize() > 0) {
                  map.startMapEffect("사이먼의 멋--진 월드에 오신 것을 환영합니다!", 5120123, 3000);
               }
            }
         }
      }, 10000L));
      this.eventSchedules.put("waitGame2", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!FlagRace.this.dispose) {
               Field map = FlagRace.this.field(FlagRace.this.fsim.instances.get(0));
               if (map.getCharactersSize() > 0) {
                  map.startMapEffect("경기장을 수놓는 아름다운 플래그 스타들을 배치하고 있습니다. 잠시만 기다려주세요!", 5120123, 3000);
               }
            }
         }
      }, 60000L));
      this.eventSchedules.put("waitGame2", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!FlagRace.this.dispose) {
               Field map = FlagRace.this.field(FlagRace.this.fsim.instances.get(0));
               if (map.getCharactersSize() > 0) {
                  map.startMapEffect("곧 플래그 레이스 경기장으로 이동합니다. 이제 정말 준비해 주세요!", 5120123, 3000);
               }
            }
         }
      }, 110000L));
      this.eventSchedules.put("waitGame2", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!FlagRace.this.dispose) {
               Field map = FlagRace.this.field(FlagRace.this.fsim.instances.get(0));
               if (map.getCharactersSize() > 0) {
                  map.startMapEffect("곧 플래그 레이스 경기장으로 이동합니다. 이제 정말 준비해 주세요!", 5120123, 3000);
               }
            }
         }
      }, 110000L));
      this.eventSchedules.put("waitGame2", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!FlagRace.this.dispose) {
               Field map = FlagRace.this.field(FlagRace.this.fsim.instances.get(0));
               FlagRace.this.waitGameStart2();
               if (map.getCharactersSize() > 0) {
                  for (MapleCharacter chr : map.getCharactersThreadsafe()) {
                     chr.changeMap(FlagRace.this.fsim.instances.get(1));
                  }
               }
            }
         }
      }, 120000L));
   }

   public void waitGameStart2() {
      this.eventSchedules.put("waitGameStart1", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!FlagRace.this.dispose) {
               Field map = FlagRace.this.field(FlagRace.this.fsim.instances.get(1));
               if (map.getCharactersSize() > 0) {
                  map.broadcastMessage(CField.environmentChange("Effect/EventEffect.img/ChallengeMission/Count/3", 4));
               }
            }
         }
      }, 21000L));
      this.eventSchedules.put("waitGameStart2", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!FlagRace.this.dispose) {
               Field map = FlagRace.this.field(FlagRace.this.fsim.instances.get(1));
               if (map.getCharactersSize() > 0) {
                  map.broadcastMessage(CField.environmentChange("Effect/EventEffect.img/ChallengeMission/Count/2", 4));
               }
            }
         }
      }, 22000L));
      this.eventSchedules.put("waitGameStart3", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!FlagRace.this.dispose) {
               Field map = FlagRace.this.field(FlagRace.this.fsim.instances.get(1));
               if (map.getCharactersSize() > 0) {
                  map.broadcastMessage(CField.environmentChange("Effect/EventEffect.img/ChallengeMission/Count/1", 4));
               }
            }
         }
      }, 23000L));
      this.eventSchedules.put("waitGameStart4", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!FlagRace.this.dispose) {
               Field map = FlagRace.this.field(FlagRace.this.fsim.instances.get(1));
               if (map.getCharactersSize() > 0) {
                  FlagRace.this.restartTimeOutNoClock(480000);
                  map.broadcastMessage(CField.environmentChange("killing/first/start", 4));
                  map.broadcastMessage(CField.getClock(480));
                  map.broadcastMessage(FlagRace.this.showFlagRaceSkillUI(true));
               }
            }
         }
      }, 24000L));
      this.eventSchedules.put("waitGameStart4", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!FlagRace.this.dispose) {
               Field map = FlagRace.this.field(FlagRace.this.fsim.instances.get(1));
               if (map.getCharactersSize() > 0) {
                  FlagRace.this.startGame = true;
                  SimpleDateFormat sdf = new SimpleDateFormat("YYMMddHHmmssSS");

                  for (MapleCharacter user : map.getCharacters()) {
                     user.updateOneInfo(32581, "start", sdf.format(new Date()));
                  }
               }
            }
         }
      }, 25000L));
   }

   public boolean isStartGame() {
      return this.startGame;
   }

   public Integer addFlagGoalNumber(String name) {
      this.flagGoalNumber.putIfAbsent(name, 0);
      this.flagGoalNumber.put(name, this.flagGoalNumber.get(name) + 1);
      return this.flagGoalNumber.get(name);
   }

   public Integer getFlagGoalNumber(String name) {
      this.flagGoalNumber.putIfAbsent(name, 0);
      return this.flagGoalNumber.get(name);
   }

   public byte[] showFlagRaceSkillUI(boolean in) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.BLACK_MAGE_TEMPORARY_SKILL.getValue());
      packet.writeInt(in ? 5 : 6);
      packet.writeInt(22);
      return packet.getPacket();
   }
}
