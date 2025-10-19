package objects.fields;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.fields.gameobject.Drop;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.Spawns;
import objects.item.Item;
import objects.users.MapleCharacter;
import objects.utils.Randomizer;

public class EliteBossBonusStageEvent extends FieldEvent {
   private boolean started = false;
   private long startTime;
   List<EliteBossBonusStageEvent.MimicGen> mimicGen = new ArrayList<>();

   public EliteBossBonusStageEvent(Field map, long expireTime) {
      super(map, expireTime);
      this.startTime = System.currentTimeMillis() + 5000L;

      for (Spawns spawns : map.getClosestSpawns(new Point(0, 0), 999)) {
         this.mimicGen.add(new EliteBossBonusStageEvent.MimicGen(spawns.getPosition()));
      }
   }

   @Override
   public void onStart() {
      this.map.setEliteState(EliteState.BonusStage);
      this.map.setMobGen(false);

      for (MapleMonster mob : this.map.getAllMonstersThreadsafe()) {
         this.map.removeMonster(mob, 1);
      }

      this.sendEliteState(null, true);
   }

   @Override
   public void onEnd() {
      for (MapleMonster mob : this.map.getAllMonstersThreadsafe()) {
         this.map.removeMonster(mob, 1);
      }

      this.map.setMobGen(true);
      this.map.setEliteState(EliteState.Normal);
      this.map.setEliteLevel(0);
      this.map.broadcastMessage(CField.stopClock());
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.ELITE_STATE.getValue());
      packet.writeInt(EliteState.Normal.getType());
      packet.writeInt(0);
      packet.writeInt(0);
      this.map.broadcastMessage(packet.getPacket());
      this.map.broadcastMessage(CField.playSound("eliteMonster/gameOver", 100));

      for (Drop item : this.map.getAllItemsThreadsafe()) {
         item.expire(this.map);
      }
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
   }

   @Override
   public void onMobLeave(MapleMonster mob) {
      if (mob.getId() == 8220027) {
         Point pos = new Point(0, mob.getTruePosition().y);
         byte d = 1;
         int[] itemID = new int[]{2432391, 2432392, 2432393, 2432394, 2432395, 2432391, 2432392, 2432393, 2432394, 2432395};
         int[] prop = new int[]{40, 10, 40, 10, 10, 50, 20, 50, 20, 10};
         int[] exp = new int[]{15, 30, 0, 0, 0, 15, 30, 0, 0, 0};

         for (int i = 0; i < itemID.length; i++) {
            if (Randomizer.nextInt(100) < prop[i]) {
               long giveExp = 0L;
               if (exp[i] > 0) {
                  Iterator item = this.map.getClosestSpawns(mob.getTruePosition(), 999).iterator();
                  if (item.hasNext()) {
                     Spawns spawn = (Spawns)item.next();
                     giveExp = spawn.getMonster().getStats().getExp() * exp[i];
                  }
               }

               int item = itemID[i];
               Item drop = new Item(item, (short)0, (short)1, 0);
               pos.x = mob.getTruePosition().x + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
               this.map.spawnMobDrop(drop, this.map.calcDropPos(pos, mob.getTruePosition()), mob, mob.getController(), (byte)0, 0, giveExp);
            }

            d++;
         }
      }
   }

   @Override
   public void onUserEnter(MapleCharacter player) {
      this.sendEliteBossTime(player);
   }

   @Override
   public void onUserLeave(MapleCharacter player) {
   }

   public int getLeftTimeoutSeconds() {
      return (int)(this.expireTime - System.currentTimeMillis());
   }

   public void sendEliteBossTime(MapleCharacter target) {
      if (target != null) {
         target.send(CField.getStopwatch(this.getLeftTimeoutSeconds()));
      } else {
         this.map.broadcastMessage(CField.getStopwatch(this.getLeftTimeoutSeconds()));
      }
   }

   @Override
   public void onUpdatePerSecond(long now) {
      super.onUpdatePerSecond(now);
      if (System.currentTimeMillis() - this.startTime >= 23000L) {
         this.onTimeOut();
      } else if (!this.expired) {
         if (this.started) {
            for (EliteBossBonusStageEvent.MimicGen gen : this.mimicGen) {
               if (gen.count == 0) {
                  int templateID = 8220027;
                  Point pos = gen.pos;
                  MapleMonster mob = MapleLifeFactory.getMonster(templateID);
                  mob.setHp(777L);
                  mob.setMaxHp(777L);
                  this.map.spawnMonsterOnGroundBelow(mob, pos);
                  gen.count = 1;
               }
            }
         } else if (this.startTime <= System.currentTimeMillis()) {
            this.expireTime = System.currentTimeMillis() + 22000L;
            this.started = true;
            this.sendEliteBossTime(null);
            this.map.broadcastMessage(CField.sendWeatherEffectNotice(1, 4000, false, "일반 공격으로 공격하면 아이템을 얻을 수 있을것 같아요!"));
            return;
         }
      }
   }

   private void sendEliteState(MapleCharacter player, boolean start) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.ELITE_STATE.getValue());
      packet.writeInt(EliteState.BonusStage.getType());
      packet.writeInt(start ? 0 : 1);
      packet.writeInt(0);
      packet.write(1);
      packet.writeMapleAsciiString("Bgm36.img/HappyTimeShort");
      packet.writeMapleAsciiString("Map/Map/Map9/924050000.img/back");
      packet.writeMapleAsciiString("Effect/EliteMobEff.img/eliteBonusStage");
      packet.writeInt(0);
      packet.write(0);
      if (player != null) {
         player.send(packet.getPacket());
      } else {
         this.map.broadcastMessage(packet.getPacket());
      }
   }

   public class MimicGen {
      public Point pos;
      public int count;

      public MimicGen(Point pos) {
         this.pos = pos;
         this.count = 0;
      }
   }
}
