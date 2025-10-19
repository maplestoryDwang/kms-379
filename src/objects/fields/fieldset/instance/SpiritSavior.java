package objects.fields.fieldset.instance;

import java.awt.Point;
import java.util.ArrayList;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.fields.Field;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.FieldSetInstance;
import objects.fields.fieldset.FieldSetInstanceMap;
import objects.fields.gameobject.Reactor;
import objects.fields.gameobject.ReactorFactory;
import objects.fields.gameobject.ReactorStats;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Timer;

public class SpiritSavior extends FieldSetInstance {
   final Pair<Integer, Point>[] prison = new Pair[]{
      new Pair<>(8644101, new Point(580, -744)),
      new Pair<>(8644102, new Point(1350, -984)),
      new Pair<>(8644103, new Point(2654, -1598)),
      new Pair<>(8644104, new Point(1889, -1763)),
      new Pair<>(8644105, new Point(1078, -2002)),
      new Pair<>(8644106, new Point(407, -2242)),
      new Pair<>(8644107, new Point(-685, -2242)),
      new Pair<>(8644108, new Point(-1349, -2002)),
      new Pair<>(8644109, new Point(-2163, -1763)),
      new Pair<>(8644110, new Point(-2937, -1598)),
      new Pair<>(8644111, new Point(-1621, -984)),
      new Pair<>(8644112, new Point(-960, -744))
   };

   public SpiritSavior(FieldSet fs, FieldSetInstanceMap fsim, MapleCharacter leader) {
      super(fs, fsim, leader);
      this.init(fs.channel);
   }

   @Override
   public void init(int channel) {
      this.channel = channel;
      this.fieldSeteventTime = 181000;
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
      super.userEnter(user);
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
   public void mobDead(final MapleMonster mMob) {
      if (mMob.getId() >= 8644101 && mMob.getId() <= 8644112) {
         final Field map = this.field(921172300);
         Timer.EventTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               int prisonSize = 0;

               for (MapleMonster mob : map.getAllMonstersThreadsafe()) {
                  if (mob.getId() >= 8644101 && mob.getId() <= 8644112 && mob.getId() != mMob.getId()) {
                     prisonSize++;
                  }
               }

               while (prisonSize < 4) {
                  for (Pair a : SpiritSavior.this.prison) {
                     if (Randomizer.nextBoolean() && (Integer)a.left != mMob.getId() && map.getMonsterById((Integer)a.left) == null) {
                        map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster((Integer)a.left), (Point)a.right);
                        if (++prisonSize == 4) {
                           break;
                        }
                     }
                  }
               }
            }
         }, 2000L);
         int chaser = Integer.parseInt(this.Var.getProperty("chaser", "0"));
         ReactorStats reactorSt = ReactorFactory.getReactor(3600001);
         Reactor reactor = new Reactor(reactorSt, 3600001);
         reactor.setDelay(-1);
         reactor.setPosition(mMob.getPosition());
         map.spawnReactor(reactor);
         switch (chaser) {
            case 0:
               map.broadcastMessage(CField.removeMapEffect());
               map.broadcastMessage(CField.startMapEffect("맹독의 정령이 눈치챈 모양이야! 어서 도망가람!", 5120175, true, 3));
               map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8644301), new Point(-180, -1440), (byte)85, true, true);
               break;
            case 1:
               MapleMonster mob = map.getMonsterById(8644301);
               if (mob == null) {
                  break;
               }

               Point spawnPos = mob.getPosition();

               for (int i = 8644301; i <= 8644305; i++) {
                  map.killMonster(i);
               }

               map.broadcastMessage(CField.removeMapEffect());
               map.broadcastMessage(CField.startMapEffect("맹독의 정령이 더 강해지고 있담..!", 5120175, true, 3));
               map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8644302), spawnPos, (byte)86, true, true);
               break;
            case 2:
               mob = map.getMonsterById(8644302);
               if (mob != null) {
                  spawnPos = mob.getPosition();

                  for (int i = 8644301; i <= 8644305; i++) {
                     map.killMonster(i);
                  }

                  map.broadcastMessage(CField.removeMapEffect());
                  map.broadcastMessage(CField.startMapEffect("맹독의 정령이 더 강해지고 있담..!", 5120175, true, 3));
                  map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8644303), spawnPos, (byte)87, true, true);
                  break;
               }
            case 3:
               MapleMonster mobx = map.getMonsterById(8644303);
               if (mobx != null) {
                  spawnPos = mobx.getPosition();

                  for (int i = 8644301; i <= 8644305; i++) {
                     map.killMonster(i);
                  }

                  map.broadcastMessage(CField.removeMapEffect());
                  map.broadcastMessage(CField.startMapEffect("맹독의 정령이 더 강해지고 있담..!", 5120175, true, 3));
                  map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8644304), spawnPos, (byte)88, true, true);
                  break;
               }
            case 4:
               MapleMonster mobxx = map.getMonsterById(8644304);
               if (mobxx != null) {
                  spawnPos = mobxx.getPosition();

                  for (int i = 8644301; i <= 8644305; i++) {
                     map.killMonster(i);
                  }

                  map.broadcastMessage(CField.removeMapEffect());
                  map.broadcastMessage(CField.startMapEffect("맹독의 정령이 완전체가 되었담! 조심해람!", 5120175, true, 3));
                  map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8644305), spawnPos, (byte)89, true, true);
               }
         }

         this.Var.setProperty("chaser", String.valueOf(chaser + 1));
      }
   }

   public void spawnRandomPrison() {
      Field map = this.field(921172300);
      int prisonSize = 0;

      for (MapleMonster mob : map.getAllMonstersThreadsafe()) {
         if (mob.getId() >= 8644101 && mob.getId() <= 8644112) {
            prisonSize++;
         }
      }

      while (prisonSize < 4) {
         for (Pair a : this.prison) {
            if (Randomizer.nextBoolean()) {
               map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster((Integer)a.left), (Point)a.right);
               if (++prisonSize == 4) {
                  break;
               }
            }
         }
      }
   }

   public void spawnBomb() {
      if (this.eventSchedules.get("spawnBomb") != null) {
         this.eventSchedules.get("spawnBomb").cancel(false);
         this.eventSchedules.remove("spawnBomb");
      }

      final Field map = this.field(921172300);
      this.eventSchedules.put("spawnBomb", Timer.EventTimer.getInstance().register(new Runnable() {
         @Override
         public void run() {
            if (!SpiritSavior.this.dispose) {
               map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8644201), new Point(-2070, -1235), (byte)84, true, true);
            }
         }
      }, 10000L, 10000L));
   }

   public void attachReactor(MapleCharacter user, Reactor reactor) {
      Field map = reactor.getMap();
      map.destroyReactor(reactor.getObjectId());
      int saved = user.getOneInfoQuestInteger(16215, "saved");
      if (saved < 5) {
         int[] ids = new int[]{3003381, 3003481, 3003581, 3003681, 3003781};
         int id = ids[saved];
         user.updateOneInfo(16215, "saved", String.valueOf(Math.min(5, saved + 1)));
         user.send(attachSpirit(true, id, 80002310));
         user.send(rescueSpiritCount(saved + 1));
      }
   }

   public void goalArea(MapleCharacter user) {
      Field map = user.getMap();
      int saved = Math.min(5, user.getOneInfoQuestInteger(16215, "saved"));
      if (saved >= 1) {
         int point = user.getOneInfoQuestInteger(16215, "point");
         this.Var.setProperty("chaser", "0");

         for (int i = 8644301; i <= 8644305; i++) {
            map.killMonster(i);
         }

         int[] givePoint = new int[]{200, 500, 1000, 1500, 2500};
         user.send(CField.environmentChange("Map/Effect3.img/savingSpirit/" + saved, 16, 0));
         user.send(CField.environmentChange("Sound/MiniGame.img/Result_Yut", 5, 100));
         user.updateOneInfo(16215, "point", String.valueOf(point + givePoint[saved - 1]));
         user.updateOneInfo(16215, "saved", "0");
         int[] ids = new int[]{3003381, 3003481, 3003581, 3003681, 3003781};

         for (int i = 0; i < saved; i++) {
            user.send(attachSpirit(false, ids[i], 0));
         }

         user.send(rescueSpiritCount(0));
      }
   }

   public void bombSpirit(MapleCharacter user, MapleMonster mob) {
      Field map = user.getMap();
      if (mob.getId() != 8644201) {
         int saved = Math.min(5, user.getOneInfoQuestInteger(16215, "saved"));
         if (saved >= 1) {
            int life = user.getOneInfoQuestInteger(16215, "life");
            this.Var.setProperty("chaser", "0");
            user.updateOneInfo(16215, "saved", "0");
            user.updateOneInfo(16215, "life", String.valueOf(life - 10));
            user.send(CField.environmentChange("Map/Effect3.img/savingSpirit/failed", 4, 0));
            map.broadcastMessage(CField.removeMapEffect());
            map.broadcastMessage(CField.startMapEffect("친구들이... 맹독의 정령에게 당하고 말았담!", 5120175, true, 3));
            int[] ids = new int[]{3003381, 3003481, 3003581, 3003681, 3003781};

            for (int i = 0; i < saved; i++) {
               user.send(attachSpirit(false, ids[i], 0));
            }

            user.send(rescueSpiritCount(0));

            for (int i = 8644301; i <= 8644305; i++) {
               map.killMonster(i);
            }

            if (user.getOneInfoQuestInteger(16215, "life") <= 0) {
               this.timeOut(0);
            }
         }
      } else {
         int life = user.getOneInfoQuestInteger(16215, "life");
         user.updateOneInfo(16215, "life", String.valueOf(life - 5));
         map.killMonster(mob);
         if (user.getOneInfoQuestInteger(16215, "life") <= 0) {
            this.timeOut(0);
         }
      }
   }

   public static byte[] attachSpirit(boolean spawn, int id, int skillId) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.ATTACH_SPIRIT.getValue());
      mplew.write(spawn ? 1 : 0);
      mplew.writeInt(id);
      mplew.writeInt(skillId);
      mplew.write(0);
      return mplew.getPacket();
   }

   public static byte[] rescueSpiritCount(int count) {
      PacketEncoder mplew = new PacketEncoder();
      mplew.writeShort(SendPacketOpcode.RESCUE_SPIRIT_COUNT.getValue());
      mplew.writeInt(count);
      return mplew.getPacket();
   }
}
