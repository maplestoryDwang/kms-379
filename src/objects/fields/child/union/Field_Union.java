package objects.fields.child.union;

import constants.QuestExConstants;
import java.awt.Point;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import objects.fields.Field;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.users.MapleCharacter;
import objects.utils.Timer;

public class Field_Union {
   private MapleCharacter player;
   private MapleMonster normalMob;
   private MapleMonster defenceMob;
   private boolean advanced = false;
   private long unionAttackPower;
   private long lastNormalMobHP;
   private long totalDamage;
   private ScheduledFuture<?> respawnTask = null;
   private ScheduledFuture<?> mobGenTask = null;

   public void updateTotalDamage() {
      String td = this.player.getOneInfoQuest(18790, "damage");
      if (td != null && !td.isEmpty()) {
         this.totalDamage = Long.parseLong(td);
      }
   }

   public void beginRaid(MapleCharacter player) {
      if (this.player == null) {
         MapleUnion union = player.getMapleUnion();
         if (union == null) {
            player.dropMessage(5, "유니온 정보를 불러오는데 실패하였습니다.");
            player.warp(15);
            return;
         }

         this.player = player;
         this.unionAttackPower = union.getAttackPower();
         player.updateUnionRaid();
         int mobType = 0;
         long mobDefenceHP = 0L;
         long mobNormalHP = 0L;
         String mobType_ = player.getOneInfoQuest(QuestExConstants.UnionMobInfo.getQuestID(), "mobType");
         if (mobType_ != null && !mobType_.isEmpty()) {
            mobType = Integer.parseInt(mobType_);
         }

         String mobDefenceHP_ = player.getOneInfo(QuestExConstants.UnionMobInfo.getQuestID(), "mobDefenceHP");
         if (mobDefenceHP_ != null && !mobDefenceHP_.isEmpty()) {
            mobDefenceHP = Long.parseLong(mobDefenceHP_);
         }

         String mobNormalHP_ = player.getOneInfo(QuestExConstants.UnionMobInfo.getQuestID(), "mobNormalHP");
         if (mobNormalHP_ != null && !mobNormalHP_.isEmpty()) {
            mobNormalHP = Long.parseLong(mobNormalHP_);
         }

         int normalMobID = 9833101 + mobType;
         int defenceMobID = 9833201 + mobType;
         String td = player.getOneInfoQuest(18790, "damage");
         if (td != null && !td.isEmpty()) {
            this.totalDamage = Long.parseLong(td);
         }

         Field map = player.getMap();
         MapleMonster normalMob = MapleLifeFactory.getMonster(normalMobID);
         MapleMonster defenceMob = MapleLifeFactory.getMonster(defenceMobID);
         if (map.getId() / 100 == 9211721) {
            normalMob.setFh(2);
            map.spawnMonsterOnGroundBelow(normalMob, new Point(2365, 21));
            defenceMob.setFh(2);
            map.spawnMonsterOnGroundBelow(defenceMob, new Point(2365, 21));
         } else {
            normalMob.setFh(1);
            map.spawnMonsterOnGroundBelow(normalMob, new Point(2320, 17));
            defenceMob.setFh(1);
            map.spawnMonsterOnGroundBelow(defenceMob, new Point(2320, 17));
         }

         this.normalMob = normalMob;
         this.defenceMob = defenceMob;
         if (mobNormalHP > 0L) {
            normalMob.setHp(mobNormalHP);
         }

         if (mobDefenceHP > 0L) {
            defenceMob.setHp(mobDefenceHP);
         }

         this.lastNormalMobHP = normalMob.getHp();
         this.sendRaidSetBossHP(normalMob, defenceMob);
         String v = player.getOneInfo(18098, "coin");
         int coin = 0;
         if (v != null && !v.isEmpty()) {
            coin = Integer.parseInt(v);
         }

         this.sendRaidCoin(coin, true);
         this.startMobGenTask();
      }
   }

   public void endRaid(MapleCharacter player) {
      if (this.player != null && this.player.getId() == player.getId()) {
         if (this.normalMob.getHp() == 1L && this.defenceMob.getHp() == 1L && !this.advanced) {
            this.advanced = true;
            this.advanceNextUnion();
         }

         if (!this.advanced) {
            player.updateOneInfo(QuestExConstants.UnionMobInfo.getQuestID(), "mobDefenceHP", String.valueOf(this.defenceMob.getHp()));
            player.updateOneInfo(QuestExConstants.UnionMobInfo.getQuestID(), "mobNormalHP", String.valueOf(this.normalMob.getHp()));
            player.updateOneInfo(18790, "lastTime", MapleUnionConstants.toUnionDate());
         }

         String v = player.getOneInfoQuest(18098, "coin");
         int coin = 0;
         if (v != null && !v.isEmpty()) {
            coin = Integer.parseInt(v);
         }

         player.updateOneInfo(18790, "coin", String.valueOf(coin));
         player.setSavedUnionCoin(coin);
         player.updateOneInfo(18790, "damage", String.valueOf(this.totalDamage));
         this.cancelMobGenTask();
         this.player = null;
      }
   }

   public void updateRaid() {
      long lastTotalDamage = this.totalDamage;
      this.updateRaidDamage();
      this.updateRaidCoin(lastTotalDamage);
      this.sendRaidSetBossHP(this.normalMob, this.defenceMob);
      if (this.normalMob != null && this.normalMob.getHp() <= 1L) {
         MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 13), true);
         this.normalMob.applyStatus(e);
      }

      if (this.defenceMob != null && this.normalMob != null && this.defenceMob.getHp() <= 1L && this.normalMob.getHp() <= 1L && !this.advanced) {
         this.advanced = true;
         this.advanceNextUnion();
         this.transferNextUnionRaid();
      }
   }

   private void transferNextUnionRaid() {
      final Field map = this.player.getMap();

      for (MapleMapObject monstermo : map.getMapObjectsInRange(this.player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER))) {
         MapleMonster mob = (MapleMonster)monstermo;
         map.killMonster(mob, this.player, false, false, (byte)1);
      }

      this.respawnTask = Timer.MapTimer.getInstance().schedule(new Runnable() {
         @Override
         public void run() {
            int mobType = 0;
            long mobDefenceHP = 0L;
            long mobNormalHP = 0L;
            if (Field_Union.this.player == null) {
               Field_Union.this.respawnTask.cancel(true);
               Field_Union.this.respawnTask = null;
            } else {
               String mobType_ = Field_Union.this.player.getOneInfoQuest(QuestExConstants.UnionMobInfo.getQuestID(), "mobType");
               if (mobType_ != null && !mobType_.isEmpty()) {
                  mobType = Integer.parseInt(mobType_);
               }

               String mobDefenceHP_ = Field_Union.this.player.getOneInfo(QuestExConstants.UnionMobInfo.getQuestID(), "mobDefenceHP");
               if (mobDefenceHP_ != null && !mobDefenceHP_.isEmpty()) {
                  mobDefenceHP = Long.parseLong(mobDefenceHP_);
               }

               String mobNormalHP_ = Field_Union.this.player.getOneInfo(QuestExConstants.UnionMobInfo.getQuestID(), "mobNormalHP");
               if (mobNormalHP_ != null && !mobNormalHP_.isEmpty()) {
                  mobNormalHP = Long.parseLong(mobNormalHP_);
               }

               int normalMobID = 9833101 + mobType;
               int defenceMobID = 9833201 + mobType;
               MapleMonster normalMob = MapleLifeFactory.getMonster(normalMobID);
               MapleMonster defenceMob = MapleLifeFactory.getMonster(defenceMobID);
               if (map.getId() / 100 == 9211721) {
                  normalMob.setFh(2);
                  map.spawnMonsterOnGroundBelow(normalMob, new Point(2365, 21));
                  defenceMob.setFh(2);
                  map.spawnMonsterOnGroundBelow(defenceMob, new Point(2365, 21));
               } else {
                  normalMob.setFh(1);
                  map.spawnMonsterOnGroundBelow(normalMob, new Point(2320, 17));
                  defenceMob.setFh(1);
                  map.spawnMonsterOnGroundBelow(defenceMob, new Point(2320, 17));
               }

               Field_Union.this.normalMob = normalMob;
               Field_Union.this.defenceMob = defenceMob;
               if (mobNormalHP > 0L) {
                  normalMob.setHp(mobNormalHP);
               }

               if (mobDefenceHP > 0L) {
                  defenceMob.setHp(mobDefenceHP);
               }

               Field_Union.this.lastNormalMobHP = normalMob.getHp();
               Field_Union.this.sendRaidSetBossHP(normalMob, defenceMob);
               Field_Union.this.advanced = false;
               Field_Union.this.respawnTask.cancel(true);
               Field_Union.this.respawnTask = null;
            }
         }
      }, 4000L);
   }

   private void advanceNextUnion() {
      if (this.player != null) {
         this.player.advanceNextUnion();
      }
   }

   private void updateRaidCoin(long lastTotalDamage) {
      long lastD = lastTotalDamage / 100000000000L;
      long curD = this.totalDamage / 100000000000L;
      if (lastD != curD) {
         this.sendRaidCoin(this.player.incSavedUnionCoin(), true);
      }
   }

   public void updateRaidDamage() {
      long unionDealt = this.unionAttackPower;
      if (unionDealt > this.defenceMob.getHp()) {
         unionDealt = this.defenceMob.getHp() - 1L;
      }

      if (unionDealt > 0L) {
         this.totalDamage += unionDealt;
         this.defenceMob.setHp(this.defenceMob.getHp() - unionDealt);
      }

      long hpdiff = this.lastNormalMobHP - this.normalMob.getHp();
      if (hpdiff > 0L) {
         this.totalDamage += hpdiff;
      }

      if (this.totalDamage >= 100000000000000L) {
         this.totalDamage %= 100000000000000L;
      }

      long missingDamage = this.unionAttackPower - unionDealt;
      if (missingDamage > 0L) {
         if (missingDamage > this.normalMob.getHp()) {
            missingDamage = this.normalMob.getHp() - 1L;
         }

         if (missingDamage > 0L) {
            this.normalMob.setHp(this.normalMob.getHp() - missingDamage);
            this.totalDamage += missingDamage;
         }
      }

      this.lastNormalMobHP = this.normalMob.getHp();
      this.sendTotalDamage();
   }

   public void cancelMobGenTask() {
      if (this.mobGenTask != null) {
         this.mobGenTask.cancel(true);
         this.mobGenTask = null;
      }
   }

   public void startMobGenTask() {
      if (this.mobGenTask != null) {
         this.cancelMobGenTask();
      }

      this.mobGenTask = Timer.MapTimer.getInstance().register(new Runnable() {
         @Override
         public void run() {
            if (Field_Union.this.player == null) {
               Field_Union.this.mobGenTask.cancel(true);
               Field_Union.this.mobGenTask = null;
            } else {
               Field map = Field_Union.this.player.getMap();
               if (map instanceof Field_UnionRaid) {
                  ((Field_UnionRaid)map).spawnUnionWyvern();
               } else {
                  Field_Union.this.mobGenTask.cancel(true);
                  Field_Union.this.mobGenTask = null;
               }
            }
         }
      }, 7000L);
   }

   public void sendTotalDamage() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.UNION_RAID_TOTAL_DAMAGE.getValue());
      packet.writeLong(this.totalDamage);
      if (this.player != null) {
         this.player.send(packet.getPacket());
      }
   }

   public void sendRaidCoin(int coin, boolean ui) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.UNION_RAID_COIN.getValue());
      packet.writeInt(coin);
      packet.write(ui);
      if (this.player != null) {
         this.player.send(packet.getPacket());
      }
   }

   public void sendRaidSetBossHP(MapleMonster normalMob, MapleMonster defenceMob) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.UNION_RAID_SET_BOSS_HP.getValue());
      packet.writeInt(normalMob.getId());
      packet.writeLong(normalMob.getHp());
      packet.writeLong(normalMob.getStats().getMaxHp());
      packet.writeInt(defenceMob.getId());
      packet.writeLong(defenceMob.getHp());
      packet.writeLong(defenceMob.getStats().getMaxHp());
      if (this.player != null) {
         this.player.send(packet.getPacket());
      }
   }
}
