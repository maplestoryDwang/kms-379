package objects.fields.fieldset.instance;

import database.DBConfig;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.context.guild.GuildContentsType;
import objects.fields.Field;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.utils.Timer;

public class Culvert extends FieldSetInstance {
   int score = 0;
   int stage = 1;

   public Culvert(FieldSet fs, FieldSetInstanceMap fsim, MapleCharacter leader) {
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
      this.fs.fInstance.get(this).add(this.leader.getId());
      this.userList = this.fs.fInstance.get(this);
      this.timeOut(this.fieldSeteventTime);
   }

   @Override
   public void userEnter(MapleCharacter user) {
      if (user.getMap().getId() == this.fsim.instances.get(0)) {
         super.userEnter(user);
      } else if (user.getMap().getId() == this.fsim.instances.get(1)) {
         this.waitGameStart();
         this.restartTimeOutNoClock(3610000);
         user.send(CField.getClock(86));
      }

      if (DBConfig.isGanglim) {
         user.applyBMCurse(99);
      }
   }

   @Override
   public void userLeave(MapleCharacter user, Field to) {
      super.userLeave(user, to);
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
      Field map = this.field(this.fsim.instances.get(1));
      switch (mMob.getId()) {
         case 9500800:
            this.score = 10;
            map.broadcastMessage(this.CULVERT_PACKET(2, this.score));
            map.broadcastMessage(this.CULVERT_PACKET(3, mMob.getObjectId(), 1));
            map.broadcastMessage(CField.addPopupSay(2012041, 1300, "#rSTAGE : 2#k\r\n아르카누스의 힘이 더욱 강해지고 있습니다!", "", 1));
            this.stage++;
            map.broadcastMessage(this.CULVERT_PACKET(1, this.stage));
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9500801), new Point(235, 95), true);
            break;
         case 9500801:
            this.score = 30;
            map.broadcastMessage(this.CULVERT_PACKET(2, this.score));
            map.broadcastMessage(this.CULVERT_PACKET(3, mMob.getObjectId(), 1));
            map.broadcastMessage(CField.addPopupSay(2012041, 1300, "#rSTAGE : 3#k\r\n아르카누스의 힘이 더욱 강해지고 있습니다!", "", 1));
            this.stage++;
            map.broadcastMessage(this.CULVERT_PACKET(1, this.stage));
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9500802), new Point(235, 95), true);
            break;
         case 9500802:
            this.score = 130;
            map.broadcastMessage(this.CULVERT_PACKET(2, this.score));
            map.broadcastMessage(this.CULVERT_PACKET(3, mMob.getObjectId(), 1));
            map.broadcastMessage(CField.addPopupSay(2012041, 1300, "#rSTAGE : 4#k\r\n아르카누스의 힘이 더욱 강해지고 있습니다!", "", 1));
            this.stage++;
            map.broadcastMessage(this.CULVERT_PACKET(1, this.stage));
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9500803), new Point(235, 95), true);
            break;
         case 9500803:
            this.score = 630;
            map.broadcastMessage(this.CULVERT_PACKET(2, this.score));
            map.broadcastMessage(this.CULVERT_PACKET(3, mMob.getObjectId(), 1));
            map.broadcastMessage(CField.addPopupSay(2012041, 1300, "#rSTAGE : 5#k\r\n아르카누스의 힘이 더욱 강해지고 있습니다!", "", 1));
            this.stage++;
            map.broadcastMessage(this.CULVERT_PACKET(1, this.stage));
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9500804), new Point(235, 95), true);
            break;
         case 9500804:
            this.score = 4130;
            map.broadcastMessage(this.CULVERT_PACKET(2, this.score));
            map.broadcastMessage(this.CULVERT_PACKET(3, mMob.getObjectId(), 1));
            map.broadcastMessage(CField.addPopupSay(2012041, 1300, "#rSTAGE : 6#k\r\n아르카누스의 힘이 더욱 강해지고 있습니다!", "", 1));
            this.stage++;
            map.broadcastMessage(this.CULVERT_PACKET(1, this.stage));
            map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9500805), new Point(235, 95), true);
            break;
         case 9500805:
            this.score = 1000000;
            map.broadcastMessage(this.CULVERT_CLOCK(5000L));
            map.broadcastMessage(this.CULVERT_PACKET(2, this.score));
            map.broadcastMessage(CField.addPopupSay(2012041, 1300, "#r아르카누스#k를 무찔러서 5초뒤 퇴장맵으로 이동됩니다!", "", 1));
            this.finishGame(5000L);
      }
   }

   @Override
   public void mobChangeHP(MapleMonster mob) {
      long maxHp = mob.getMobMaxHp();
      int a = 100;
      switch (mob.getId()) {
         case 9500800:
            int aaxx = (100 - mob.getHPPercent()) / 10;
            if (this.score < aaxx) {
               mob.getMap().broadcastMessage(this.CULVERT_PACKET(3, mob.getObjectId(), aaxx - this.score));
               this.score = aaxx;
               mob.getMap().broadcastMessage(this.CULVERT_PACKET(2, this.score));
            }
            break;
         case 9500801:
            int aax = (100 - mob.getHPPercent()) / 5 + 10;
            if (this.score < aax) {
               mob.getMap().broadcastMessage(this.CULVERT_PACKET(3, mob.getObjectId(), aax - this.score));
               this.score = aax;
               mob.getMap().broadcastMessage(this.CULVERT_PACKET(2, this.score));
            }
            break;
         case 9500802:
            int aa = (100 - mob.getHPPercent()) / 1 + 30;
            if (this.score < aa) {
               mob.getMap().broadcastMessage(this.CULVERT_PACKET(3, mob.getObjectId(), aa - this.score));
               this.score = aa;
               mob.getMap().broadcastMessage(this.CULVERT_PACKET(2, this.score));
            }
            break;
         case 9500803:
            double pointCalcxx = (100.0 - mob.getHPPercentDouble()) * 5.0 + 130.0;
            int calcxx = (int)Math.floor(pointCalcxx);
            if (this.score < calcxx) {
               mob.getMap().broadcastMessage(this.CULVERT_PACKET(3, mob.getObjectId(), calcxx - this.score));
               this.score = calcxx;
               mob.getMap().broadcastMessage(this.CULVERT_PACKET(2, this.score));
            }
            break;
         case 9500804:
            double pointCalcx = (100.0 - mob.getHPPercentDouble()) * 33.0 + 630.0;
            int calcx = (int)Math.floor(pointCalcx);
            if (this.score < calcx) {
               mob.getMap().broadcastMessage(this.CULVERT_PACKET(3, mob.getObjectId(), calcx - this.score));
               this.score = calcx;
               mob.getMap().broadcastMessage(this.CULVERT_PACKET(2, this.score));
            }
            break;
         case 9500805:
            double pointCalc = (900000000000000L - mob.getHp()) / 2.9E9 + 4130.0;
            if (DBConfig.isGanglim) {
               pointCalc = (100.0 - mob.getHPPercentDouble()) * 10000.0 + 4130.0;
            }

            int calc = (int)Math.floor(pointCalc);
            if (this.score < calc) {
               mob.getMap().broadcastMessage(this.CULVERT_PACKET(3, mob.getObjectId(), calc - this.score));
               this.score = calc;
               mob.getMap().broadcastMessage(this.CULVERT_PACKET(2, this.score));
            }

            if (this.score > 1000000) {
               this.score = 1000000;
               mob.getMap().broadcastMessage(this.CULVERT_PACKET(2, this.score));
            }
      }
   }

   public synchronized void waitGameStart() {
      if (this.eventSchedules.get("waitGameStart1") == null) {
         this.eventSchedules.put("waitGameStart1", Timer.EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               if (!Culvert.this.dispose) {
                  Field map = Culvert.this.field(Culvert.this.fsim.instances.get(1));
                  if (map.getCharactersSize() > 0) {
                     map.broadcastMessage(CField.environmentChange("Effect/EventEffect.img/ChallengeMission/Count/3", 16));
                     map.broadcastMessage(CField.environmentChange("Sound/MiniGame.img/multiBingo/3", 5, 100));
                  }
               }
            }
         }, 3000L));
         this.eventSchedules.put("waitGameStart2", Timer.EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               if (!Culvert.this.dispose) {
                  Field map = Culvert.this.field(Culvert.this.fsim.instances.get(1));
                  if (map.getCharactersSize() > 0) {
                     map.broadcastMessage(CField.environmentChange("Effect/EventEffect.img/ChallengeMission/Count/2", 16));
                     map.broadcastMessage(CField.environmentChange("Sound/MiniGame.img/multiBingo/2", 5, 100));
                  }
               }
            }
         }, 4000L));
         this.eventSchedules.put("waitGameStart3", Timer.EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               if (!Culvert.this.dispose) {
                  Field map = Culvert.this.field(Culvert.this.fsim.instances.get(1));
                  if (map.getCharactersSize() > 0) {
                     map.broadcastMessage(CField.environmentChange("Effect/EventEffect.img/ChallengeMission/Count/1", 16));
                     map.broadcastMessage(CField.environmentChange("Sound/MiniGame.img/multiBingo/1", 5, 100));
                  }
               }
            }
         }, 5000L));
         this.eventSchedules.put("waitGameStart4", Timer.EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               if (!Culvert.this.dispose) {
                  Field map = Culvert.this.field(Culvert.this.fsim.instances.get(1));
                  if (map.getCharactersSize() > 0) {
                     map.broadcastMessage(CField.environmentChange("Effect/EventEffect.img/ChallengeMission/start", 16));
                     map.broadcastMessage(CField.environmentChange("Sound/MiniGame.img/multiBingo/start", 5, 100));
                  }
               }
            }
         }, 6000L));
         this.eventSchedules.put("waitGameStart5", Timer.EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               if (!Culvert.this.dispose) {
                  Field map = Culvert.this.field(Culvert.this.fsim.instances.get(1));
                  if (map.getCharactersSize() > 0) {
                     map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9500800), new Point(235, 95), true);
                     map.broadcastMessage(Culvert.this.CULVERT_PACKET(1, Culvert.this.stage));
                     map.broadcastMessage(Culvert.this.CULVERT_PACKET(0, 3));
                     map.broadcastMessage(Culvert.this.CULVERT_CLOCK(120000L));
                     Culvert.this.finishGame(121000L);
                  }
               }
            }
         }, 7000L));
      }
   }

   public void finishGame(long time) {
      this.eventSchedules.put("finishGame", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!Culvert.this.dispose) {
               Field map = Culvert.this.field(Culvert.this.fsim.instances.get(1));
               if (map.getCharactersSize() > 0) {
                  map.broadcastMessage(Culvert.this.CULVERT_PACKET(0, 4));
               }
            }
         }
      }, time));
      this.eventSchedules.put("finishGame2", Timer.EventTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            if (!Culvert.this.dispose) {
               try {
                  Field map = Culvert.this.field(Culvert.this.fsim.instances.get(1));
                  if (map.getCharactersSize() > 0) {
                     map.broadcastMessage(Culvert.this.CULVERT_PACKET(0, 5));

                     for (MapleCharacter chr : map.getCharacters()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd/HH/mm");
                        chr.updateOneInfo(100811, "point", String.valueOf(Culvert.this.score));
                        chr.updateOneInfo(100811, "complete", "1");
                        chr.updateOneInfo(100811, "date", sdf.format(new Date()));
                        if (chr.getOneInfoQuestInteger(100811, "guildPoint") < 1 && chr.getGuild() != null) {
                           chr.updateOneInfo(100811, "guildPoint", "1");
                           chr.getGuild().gainGP(2000);
                        }

                        if (chr.getGuild() != null) {
                           chr.getGuild().setPointLog(GuildContentsType.CULVERT, chr, Culvert.this.score);
                        }

                        chr.changeMap(chr.getMap().getForcedReturnMap());
                     }
                  }
               } catch (Exception var5) {
                  System.out.println("수로 정보 업데이트 오류 발생");
                  var5.printStackTrace();
               }
            }
         }
      }, time + 3000L));
   }

   public byte[] CULVERT_PACKET(int op, int stage) {
      return this.CULVERT_PACKET(op, stage, 0);
   }

   public byte[] CULVERT_PACKET(int op, int stage, int value) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CULVERT_PACKET.getValue());
      packet.writeInt(op);
      packet.writeInt(stage);
      if (op == 3) {
         packet.writeInt(value);
      }

      return packet.getPacket();
   }

   public byte[] CULVERT_CLOCK(long time) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CLOCK.getValue());
      packet.write(13);
      packet.writeLong(time);
      return packet.getPacket();
   }
}
