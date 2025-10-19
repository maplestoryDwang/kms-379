package objects.fields;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.SendPacketOpcode;
import network.center.Center;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import objects.fields.gameobject.lifes.EliteType;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.Spawns;
import objects.item.Item;
import objects.users.MapleCharacter;
import objects.utils.Randomizer;

public class EliteBossEvent extends FieldEvent {
   private int bossTemplateID = Randomizer.rand(8220022, 8220026);
   private long nextBroadcastEliteBossWMI;
   private GameServer gameServer;
   private boolean summonedBoss;
   private long startTime;
   private Point pos;
   private List<MapleMonster> mobs;

   public EliteBossEvent(Field map, long expireTime, GameServer gameServer, Point pos) {
      super(map, expireTime);
      this.gameServer = gameServer;
      this.nextBroadcastEliteBossWMI = System.currentTimeMillis();
      this.summonedBoss = false;
      this.startTime = System.currentTimeMillis();
      this.pos = pos;
      this.mobs = new ArrayList<>();
   }

   @Override
   public void onStart() {
      this.map.setEliteState(EliteState.EliteBoss);
      this.map.setMobGen(false);

      for (MapleMonster mob : this.map.getAllMonstersThreadsafe()) {
         this.map.removeMonster(mob, 1);
      }

      this.sendEliteBossTime(null);
      this.sendEliteState(null, true);
      Center.Broadcast.broadcastEliteBossWMI(this.map.getId(), 2, this.bossTemplateID, this.getLeftTimeoutSeconds(), this.gameServer);
   }

   @Override
   public void onEnd() {
      for (MapleMonster mob : this.mobs) {
         this.map.removeMonster(mob, 1);
      }

      this.map.setEliteLevel(0);
      this.map.setMobGen(true);
      Center.Broadcast.broadcastEliteBossWMI(this.map.getId(), 1, 0, 0, this.gameServer);
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.ELITE_STATE.getValue());
      packet.writeInt(EliteState.Normal.getType());
      packet.writeInt(0);
      packet.writeInt(0);
      this.map.broadcastMessage(packet.getPacket());
      this.map.broadcastMessage(CField.stopClock());
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
   }

   @Override
   public void onMobLeave(MapleMonster mob) {
      if (mob.getEliteMobType() == EliteType.Boss) {
         Center.Broadcast.broadcastEliteBossWMI(this.map.getId(), 1, 0, 0, this.gameServer);
         this.map.setFieldEvent(new EliteBossBonusStageEvent(this.map, System.currentTimeMillis() + 22000L));
         int dropSize = 0;

         for (MapleMonster.AttackerEntry e : mob.getAttackers()) {
            dropSize = e.getAttackers().size();
         }

         dropSize = Math.min(mob.getMap().getCharactersSize(), dropSize);
         dropSize += Randomizer.rand(0, 1);
         byte d = 1;

         for (int i = 0; i < dropSize; i++) {
            Item drop = new Item(2433834, (short)0, (short)1, 0);
            this.pos.x = mob.getTruePosition().x + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
            this.map.spawnMobDrop(drop, this.map.calcDropPos(this.pos, mob.getTruePosition()), mob, mob.getController(), (byte)0, 0);
            d++;
         }
      } else {
         int[] itemID = new int[]{2049501, 2048300, 2049400, 2048308, 2049100, 2470003, 2436605, 2049153, 2049004, 2430441, 2048716, 2048717, 5680148, 5520001};
         int[] prop = new int[]{2500, 2500, 2500, 2500, 2500, 2500, 800, 30, 40, 20, 20, 50, 10, 2};
         byte d = 1;
         Point pos = new Point(0, mob.getTruePosition().y);

         for (int i = 0; i < itemID.length; i++) {
            if (Randomizer.nextInt(10000) < prop[i]) {
               int item = itemID[i];
               Item drop = new Item(item, (short)0, (short)1, 0);
               pos.x = mob.getTruePosition().x + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
               this.map.spawnMobDrop(drop, this.map.calcDropPos(pos, mob.getTruePosition()), mob, mob.getController(), (byte)0, 0);
               d++;
            }
         }
      }
   }

   @Override
   public void onUserEnter(MapleCharacter player) {
      this.sendEliteBossTime(player);
      this.sendEliteState(player, false);
   }

   @Override
   public void onUserLeave(MapleCharacter player) {
   }

   @Override
   public void onUpdatePerSecond(long now) {
      super.onUpdatePerSecond(now);
      if (!this.expired) {
         if (!this.summonedBoss && this.startTime + 4500L <= now) {
            switch (this.bossTemplateID) {
               case 8220022:
                  this.map.startMapEffect("검은 기사 모카딘 : 위대한 분을 위하여 너를 처단하겠다.", 5120125, false, 5);
                  break;
               case 8220023:
                  this.map.startMapEffect("미친 마법사 카리아인 : 미천한 것들이 날뛰고 있구나. 크크크크...", 5120126, false, 5);
                  break;
               case 8220024:
                  this.map.startMapEffect("돌격형 CQ57 : 목표발견. 제거 행동을 시작한다.", 5120127, false, 5);
                  break;
               case 8220025:
                  this.map.startMapEffect("인간사냥꾼 줄라이 : 사냥감이 나타났군.", 5120128, false, 5);
                  break;
               case 8220026:
                  this.map.startMapEffect("싸움꾼 플레드 : 재미 있겠군. 어디 한 번 놀아볼까.", 5120129, false, 5);
            }

            this.summonEliteBoss();
            this.summonEliteMob(2);
            this.summonedBoss = true;
         }

         this.notifyEliteBossWMI(now);
      }
   }

   public int getLeftTimeoutSeconds() {
      return (int)(this.expireTime - System.currentTimeMillis()) / 1000;
   }

   public void sendEliteBossTime(MapleCharacter target) {
      if (target != null) {
         target.send(CField.getClock(this.getLeftTimeoutSeconds()));
      } else {
         this.map.broadcastMessage(CField.getClock(this.getLeftTimeoutSeconds()));
      }
   }

   public void sendEliteState(MapleCharacter player, boolean start) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.ELITE_STATE.getValue());
      packet.writeInt(EliteState.EliteBoss.getType());
      packet.writeInt(start ? 0 : 1);
      packet.writeInt(0);
      packet.writeMapleAsciiString("Bgm36.img/RoyalGuard");
      packet.writeMapleAsciiString("Effect/EliteMobEff.img/eliteMonsterFrame");
      packet.writeMapleAsciiString("Effect/EliteMobEff.img/eliteMonsterEffect");
      if (player != null) {
         player.send(packet.getPacket());
      } else {
         this.map.broadcastMessage(packet.getPacket());
      }
   }

   private void notifyEliteBossWMI(long now) {
      if (this.nextBroadcastEliteBossWMI < now) {
         Center.Broadcast.broadcastEliteBossWMI(this.map.getId(), 2, this.bossTemplateID, this.getLeftTimeoutSeconds(), this.gameServer);
         this.nextBroadcastEliteBossWMI = System.currentTimeMillis() + 15000L;
      }
   }

   private void summonEliteBoss() {
      for (Spawns spawns : this.map.getClosestSpawns(this.pos, 1)) {
         MapleMonster mob = MapleLifeFactory.getMonster(this.bossTemplateID);
         if (mob != null) {
            int level = spawns.getMonster().getStats().getLevel();
            mob.getStats().setLevel((short)level);
            mob.setEliteBoss(spawns.getMonster().getStats().getMaxHp(), level);
            mob.getChangedStats().level = level;
            this.mobs.add(mob);
            this.map.spawnMonsterOnGroundBelow(mob, spawns.getPosition());
         }
      }
   }

   private void summonEliteMob(int summonCount) {
      for (Spawns spawns : this.map.getClosestSpawns(this.pos, summonCount)) {
         int mobTemplateID = spawns.getMonster().getId();
         MapleMonster mob = MapleLifeFactory.getMonster(mobTemplateID);
         if (mob != null) {
            mob.setElite(null);
            this.mobs.add(mob);
            this.map.spawnMonsterOnGroundBelow(mob, spawns.getPosition());
         }
      }
   }
}
