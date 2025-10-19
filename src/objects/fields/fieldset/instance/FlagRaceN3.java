package objects.fields.fieldset.instance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.context.party.PartyMemberEntry;
import objects.fields.Field;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Timer;

public class FlagRaceN3 extends FieldSetInstance {
   boolean isPracticeMode = false;
   boolean startGame = false;
   HashMap<String, Integer> flagGoalNumber = new HashMap<>();

   public FlagRaceN3(FieldSet fs, FieldSetInstanceMap fsim, MapleCharacter leader, boolean isPracticeMode) {
      super(fs, fsim, leader);
      this.init(fs.channel);
      this.isPracticeMode = isPracticeMode;
   }

   @Override
   public void init(int channel) {
      this.channel = channel;
      this.fieldSeteventTime = 480000;
      this.setFieldSetStartTime(System.currentTimeMillis());
      this.fieldSetEndTime = this.getFieldSetStartTime() + this.fieldSeteventTime;
      this.remainingTime = (int)((this.fieldSeteventTime - (System.currentTimeMillis() - this.getFieldSetStartTime())) / 1000L);

      for (Integer map : this.fsim.instances) {
         this.field(map).resetFully(false);
         this.field(map).setLastRespawnTime(System.currentTimeMillis());
         this.field(map).setFieldSetInstance(this);
      }

      this.fs.fInstance.putIfAbsent(this, new ArrayList<>());

      for (PartyMemberEntry mpc : this.leader.getParty().getPartyMemberList()) {
         this.fs.fInstance.get(this).add(mpc.getId());
      }

      this.userList = this.fs.fInstance.get(this);
      this.timeOut(this.fieldSeteventTime + 25000);
      this.waitGameStart();
   }

   @Override
   public void userEnter(MapleCharacter user) {
      if (user.getMap().getId() == this.fsim.instances.get(0)) {
         user.updateOneInfo(32581, "complete", "0");
         user.updateOneInfo(32581, "lap", "0");
         user.updateOneInfo(32581, "finish", "0");
         user.updateOneInfo(32581, "mode", "1");
         user.updateOneInfo(32581, "record", "0");
         user.send(CField.getClock(20));
         if (this.isPracticeMode) {
            user.updateOneInfo(32581, "mode", "2");
            user.send(CField.setBossMode(1));
         }
      }
   }

   @Override
   public void userLeave(MapleCharacter user, Field to) {
      super.userLeave(user, to);
      user.temporaryStatReset(SecondaryStatFlag.FlagRace_CramponShoes);
      user.send(this.showFlagRaceSkillUI(false));
      user.send(CField.setBossMode(0));
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
      this.eventSchedules.put("waitGameStart1", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!FlagRaceN3.this.dispose) {
               Field map = FlagRaceN3.this.field(FlagRaceN3.this.fsim.instances.get(0));
               if (map.getCharactersSize() > 0) {
                  map.broadcastMessage(CField.environmentChange("Effect/EventEffect.img/ChallengeMission/Count/3", 4));
               }
            }
         }
      }, 21000L));
      this.eventSchedules.put("waitGameStart2", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!FlagRaceN3.this.dispose) {
               Field map = FlagRaceN3.this.field(FlagRaceN3.this.fsim.instances.get(0));
               if (map.getCharactersSize() > 0) {
                  map.broadcastMessage(CField.environmentChange("Effect/EventEffect.img/ChallengeMission/Count/2", 4));
               }
            }
         }
      }, 22000L));
      this.eventSchedules.put("waitGameStart3", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!FlagRaceN3.this.dispose) {
               Field map = FlagRaceN3.this.field(FlagRaceN3.this.fsim.instances.get(0));
               if (map.getCharactersSize() > 0) {
                  map.broadcastMessage(CField.environmentChange("Effect/EventEffect.img/ChallengeMission/Count/1", 4));
               }
            }
         }
      }, 23000L));
      this.eventSchedules.put("waitGameStart4", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!FlagRaceN3.this.dispose) {
               Field map = FlagRaceN3.this.field(FlagRaceN3.this.fsim.instances.get(0));
               if (map.getCharactersSize() > 0) {
                  map.broadcastMessage(CField.environmentChange("killing/first/start", 4));
                  map.broadcastMessage(CField.getClock(480));
                  map.broadcastMessage(FlagRaceN3.this.showFlagRaceSkillUI(true));
               }
            }
         }
      }, 24000L));
      this.eventSchedules.put("waitGameStart4", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!FlagRaceN3.this.dispose) {
               Field map = FlagRaceN3.this.field(FlagRaceN3.this.fsim.instances.get(0));
               if (map.getCharactersSize() > 0) {
                  FlagRaceN3.this.startGame = true;
                  SimpleDateFormat sdf = new SimpleDateFormat("YYMMddHHmmssSS");

                  for (MapleCharacter user : map.getCharacters()) {
                     user.updateOneInfo(32581, "start", sdf.format(new Date()));
                  }
               }
            }
         }
      }, 25000L));
   }

   public boolean isPracticeMode() {
      return this.isPracticeMode;
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
