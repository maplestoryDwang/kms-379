package objects.fields.child.blackmage;

import database.DBConfig;
import java.awt.Point;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.effect.child.FieldSkillEffect;
import objects.fields.Field;
import objects.fields.fieldset.instance.HardBlackMageBoss;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.fields.obstacle.ObstacleAtom;
import objects.fields.obstacle.ObstacleAtomCreateType;
import objects.users.MapleCharacter;
import objects.utils.Randomizer;
import scripting.EventInstanceManager;

public class Field_BlackMage extends Field {
   private int phase = 1;
   protected boolean spawned1PhaseBoss = false;
   private boolean setShriekingWallPattern = false;
   private long createBarrierTime = 0L;
   private long removeBarrierTime = 0L;

   public Field_BlackMage(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.createBarrierTime != 0L && this.createBarrierTime <= System.currentTimeMillis()) {
         this.createBarrierTime = 0L;
         this.createBarrier();
      }

      if (this.removeBarrierTime != 0L && this.removeBarrierTime <= System.currentTimeMillis()) {
         if (this.getPhase() == 1) {
            MapleMonster boss = this.getMonsterById(8880505);
            if (boss != null) {
               boss.setShieldHP(0L);
               boss.setTotalShieldHP(0L);
               this.sendBlackMageShield(boss.getObjectId(), 0);
            }
         } else if (this.getPhase() == 2) {
            MapleMonster boss = this.getMonsterById(8880502);
            if (boss != null) {
               boss.setShieldHP(0L);
               boss.setTotalShieldHP(0L);
               this.sendBlackMageShield(boss.getObjectId(), 0);
            }
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.setPhase(1);
      this.spawned1PhaseBoss = false;
      this.setShriekingWallPattern = false;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      player.send(CField.UIPacket.openUI(1204));
      this.sendBlackMageDeathCount(player, player.getDeathCount());
      player.setCanAttackBMRewardMob(false);
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
      if (DBConfig.isGanglim
         && this.getFieldSetInstance() != null
         && this.getFieldSetInstance() instanceof HardBlackMageBoss
         && this.findBoss() != null
         && this.findBoss().getId() == mob.getId()) {
         MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
         e.setCancelTask(0L);
         mob.applyStatus(e);
      }

      if (mob.getId() == 8880505) {
         this.spawned1PhaseBoss = true;
      }

      if (mob.getId() == 8880500) {
         mob.addSkillFilter(0);
         mob.addSkillFilter(1);
         mob.addSkillFilter(2);
      } else if (mob.getId() == 8880501) {
         mob.addSkillFilter(0);
         mob.addSkillFilter(1);
         mob.addSkillFilter(2);
      }
   }

   public MapleMonster findBoss() {
      return this.getMonsterById(8880505);
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
   }

   @Override
   public void onPlayerDead(MapleCharacter player) {
      this.decrementDeathCount(player, true);
   }

   public void createBarrier() {
      if (this.getPhase() == 1) {
         MapleMonster mob = this.getMonsterById(8880505);
         mob.setTotalShieldHP(mob.getTotalShieldHP() + 1000000000000L);
         mob.setShieldHP(mob.getShieldHP() + 1000000000000L);
         this.sendBlackMageShield(mob.getObjectId(), mob.getShieldPercentage());
         mob = this.getMonsterById(8880500);
         this.sendBlackMageShieldEffect(mob.getObjectId());
         mob = this.getMonsterById(8880501);
         this.sendBlackMageShieldEffect(mob.getObjectId());

         for (MapleMonster m : this.getAllMonstersThreadsafe()) {
            for (int i = 0; i < 4; i++) {
               m.removeAttackBlocked(i);
            }

            m.broadcastAttackBlocked();
            m.removeSkillFilter(0);
         }

         this.setRemoveBarrierTime(System.currentTimeMillis() + 15000L);
      } else if (this.getPhase() == 2) {
         MapleMonster mob = this.getMonsterById(8880502);
         mob.setTotalShieldHP(mob.getTotalShieldHP() + 1000000000000L);
         mob.setShieldHP(mob.getShieldHP() + 1000000000000L);
         this.sendBlackMageShield(mob.getObjectId(), mob.getShieldPercentage());
         this.sendBlackMageShieldEffect(mob.getObjectId());
         this.setRemoveBarrierTime(System.currentTimeMillis() + 30000L);
      } else if (this.getPhase() == 3) {
         MapleMonster mob = this.getMonsterById(8880503);
         mob.setTotalShieldHP(mob.getTotalShieldHP() + 3000000000000L);
         mob.setShieldHP(mob.getShieldHP() + 3000000000000L);
         this.sendBlackMageShield(mob.getObjectId(), mob.getShieldPercentage());
         this.sendBlackMageShieldEffect(mob.getObjectId());
      } else if (this.getPhase() == 4) {
         MapleMonster mob = this.getMonsterById(8880504);
         mob.setTotalShieldHP(mob.getTotalShieldHP() + 3000000000000L);
         mob.setShieldHP(mob.getShieldHP() + 3000000000000L);
         this.sendBlackMageShield(mob.getObjectId(), mob.getShieldPercentage());
         this.sendBlackMageShieldEffect(mob.getObjectId());
      }
   }

   public void createMorningStarfall() {
      int startX = 0;
      int rand = Randomizer.rand(1, 4);
      if (rand == 1) {
         startX = Randomizer.rand(0, 450);
      } else if (rand == 2) {
         startX = Randomizer.rand(-100, 350);
      } else if (rand == 3) {
         startX = Randomizer.rand(-150, 300);
      } else if (rand == 4) {
         startX = Randomizer.rand(-200, 250);
      }

      for (int i = 0; i < rand; i++) {
         ObstacleAtom atom = new ObstacleAtom(77, new Point(startX + i * 300, -600), new Point(startX + i * 300 - 688, 88), 230 + i * 350);
         atom.setKey(Randomizer.nextInt());
         atom.setvPerSec(97);
         atom.setHeight(1000);
         atom.setTrueDamR(35);
         atom.setMaxP(7);
         this.broadcastMessage(CField.createSingleObstacle(ObstacleAtomCreateType.DIAGONAL, null, null, atom));
      }
   }

   public void decrementDeathCount(MapleCharacter player, boolean forced) {
      if (!player.isPlayerDead() || forced) {
         player.setDeathCount(player.getDeathCount() - 1);
         player.setDecrementDeathCount(player.getDecrementDeathCount() + 1);
         this.sendBlackMageDeathCount(player, player.getDeathCount());
         if (!forced) {
            this.sendBlackMageDeathCountEffect(player);
         }

         if (player.getDeathCount() <= 0) {
            EventInstanceManager eim = player.getEventInstance();
            if (eim != null) {
               eim.stopEventTimer();
            }

            player.send(CField.getClock(3));
            player.setRegisterTransferField(450013780);
            player.setRegisterTransferFieldTime(System.currentTimeMillis() + 3000L);
            player.send(CField.makeEffectScreen("hillah/fail"));
         }
      }
   }

   public void sendBlackMageDeathCount(MapleCharacter player, int count) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.DEATH_COUNT.getValue());
      packet.writeInt(player.getId());
      packet.writeInt(count);
      player.send(packet.getPacket());
   }

   public void sendBlackMageDeathCountEffect(MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.BLACK_MAGE_DEATH_COUNT_EFFECT.getValue());
      player.send(packet.getPacket());
   }

   public void sendCreateBlackChains() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_SKILL_RESULT.getValue());
      packet.writeInt(Field_BlackMage.FieldSkill.BlackChains.getSkillID());
      packet.writeInt(Randomizer.rand(1, 2));
      this.broadcastMessage(packet.getPacket());
   }

   public void sendCreateRedFalmes(MapleCharacter player, int level) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_SKILL_RESULT.getValue());
      packet.writeInt(Field_BlackMage.FieldSkill.RedFlames.getSkillID());
      packet.writeInt(level);
      player.send(packet.getPacket());
   }

   public void sendCreateShriekingWallsEffect(List<Point> spawnPoint) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_SKILL_RESULT.getValue());
      packet.writeInt(Field_BlackMage.FieldSkill.ShriekingWalls.getSkillID());
      packet.writeInt(1);
      packet.writeInt(spawnPoint.size());

      for (Point pos : spawnPoint) {
         packet.writeInt(pos.x);
         packet.writeInt(pos.y);
      }

      this.broadcastMessage(packet.getPacket());
   }

   public void sendRemoveShriekingWallsEffect() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.REMOVE_FIELD_SKILL_RESULT.getValue());
      packet.writeInt(Field_BlackMage.FieldSkill.ShriekingWalls.getSkillID());
      packet.writeInt(2);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendCreateLasers(List<BlackMageLaser> lasers) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_SKILL_RESULT.getValue());
      packet.writeInt(Field_BlackMage.FieldSkill.Lasers.getSkillID());
      packet.writeInt((Randomizer.nextInt() & 3) == 3 ? 1 : 2);
      packet.writeInt(lasers.size());

      for (BlackMageLaser laser : lasers) {
         laser.encode(packet);
      }

      this.broadcastMessage(packet.getPacket());
   }

   public void sendPiercingGaze() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_SKILL_RESULT.getValue());
      packet.writeInt(Field_BlackMage.FieldSkill.PiercingGaze.getSkillID());
      packet.writeInt(1);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendBlackMageShieldEffect(int mobObjectID) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_BARRIER_EFFECT.getValue());
      packet.writeInt(mobObjectID);
      packet.write(true);
      if (this.getPhase() == 4) {
         packet.writeMapleAsciiString("UI/UIWindow8.img/BlackMageShield/mobEffect_211");
      } else {
         packet.writeMapleAsciiString("UI/UIWindow8.img/BlackMageShield/mobEffect");
      }

      packet.writeInt(1);
      packet.write(true);
      packet.writeMapleAsciiString("Sound/Etc.img/BlackMageShield");
      packet.write(true);
      if (this.getPhase() == 4) {
         packet.writeMapleAsciiString("UI/UIWindow8.img/BlackMageShield/mobEffect0_211");
      } else {
         packet.writeMapleAsciiString("UI/UIWindow8.img/BlackMageShield/mobEffect0");
      }

      packet.writeInt(-1);
      packet.write(0);
      packet.write(0);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendBlackMageShield(int mobObjectID, int percentage) {
      this.sendBlackMageShield(null, mobObjectID, percentage);
   }

   public void sendBlackMageShield(MapleCharacter player, int mobObjectID, int percentage) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_BARRIER.getValue());
      packet.writeInt(mobObjectID);
      packet.writeInt(percentage);
      packet.writeLong(0L);
      if (player == null) {
         this.broadcastMessage(packet.getPacket());
      } else {
         player.send(packet.getPacket());
      }
   }

   public void sendWeldingCreation(Field_BlackMage.FieldSkill skill) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_SKILL_RESULT.getValue());
      packet.writeInt(skill.getSkillID());
      packet.writeInt(1);
      BlackMageWelding welding = new BlackMageWelding();
      welding.encode(packet);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendAttributesExplosion() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_SKILL_RESULT.getValue());
      packet.writeInt(Field_BlackMage.FieldSkill.AttributesExplosion.getSkillID());
      packet.writeInt(1);
      BlackMageAttributesExplosion explosion = new BlackMageAttributesExplosion();
      explosion.encode(packet);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendWeldingDestruction() {
      FieldSkillEffect eff = new FieldSkillEffect(-1, Field_BlackMage.FieldSkill.WeldingDestruction.skillID, 1, null);
      this.broadcastMessage(eff.encodeForLocal());
   }

   public void createDarkFalling() {
      int startX = 2196;
      int delta = 150;
      if (Randomizer.rand(1, 2) == 2) {
         startX *= -1;
         delta *= -1;
      }

      for (int i = 0; i < 30; i++) {
         ObstacleAtom atom = new ObstacleAtom(75, new Point(startX - i * delta, -600), new Point(startX - i * delta, 87), 1350 + i * 500);
         atom.setKey(Randomizer.nextInt());
         atom.setHitBoxRange(30);
         atom.setvPerSec(50);
         atom.setHeight(1000);
         atom.setTrueDamR(50);
         atom.setMaxP(7);
         this.broadcastMessage(CField.createSingleObstacle(ObstacleAtomCreateType.NORMAL, null, null, atom));
      }
   }

   public void sendBlackMageNotice(String message, int time) {
      this.broadcastMessage(CField.sendWeatherEffectNotice(265, time, false, message));
   }

   public int getPhase() {
      return this.phase;
   }

   public void setPhase(int phase) {
      this.phase = phase;
   }

   public boolean isSetShriekingWallPattern() {
      return this.setShriekingWallPattern;
   }

   public void setSetShriekingWallPattern(boolean setShriekingWallPattern) {
      this.setShriekingWallPattern = setShriekingWallPattern;
   }

   public long getCreateBarrierTime() {
      return this.createBarrierTime;
   }

   public void setCreateBarrierTime(long createBarrierTime) {
      this.createBarrierTime = createBarrierTime;
   }

   public long getRemoveBarrierTime() {
      return this.removeBarrierTime;
   }

   public void setRemoveBarrierTime(long removeBarrierTime) {
      this.removeBarrierTime = removeBarrierTime;
   }

   public static enum FieldSkill {
      WeldingDestruction(100006),
      BlackChains(100007),
      ShriekingWalls(100008),
      Lasers(100011),
      PiercingGaze(100012),
      PowerOfCreation(100013),
      WeldingCreation(100014),
      RedFlames(100015),
      AttributesExplosion(100016),
      FlamingStrikes(100017);

      private int skillID;

      private FieldSkill(int skillID) {
         this.skillID = skillID;
      }

      public int getSkillID() {
         return this.skillID;
      }
   }
}
