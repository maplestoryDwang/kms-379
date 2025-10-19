package objects.fields.child.blackheaven;

import database.DBConfig;
import java.awt.Point;
import java.util.function.Consumer;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.fields.EliteState;
import objects.fields.Field;
import objects.fields.fieldset.instance.HardBlackHeavenBoss;
import objects.fields.fieldset.instance.HellBlackHeavenBoss;
import objects.fields.fieldset.instance.NormalBlackHeavenBoss;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.fields.obstacle.ObstacleAtomCreateType;
import objects.fields.obstacle.ObstacleAtomCreatorOption;
import objects.users.MapleCharacter;
import objects.utils.Rect;

public class Field_BlackHeavenBoss extends Field {
   private boolean hellMode = false;

   public Field_BlackHeavenBoss(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(false);
      this.hellMode = false;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof NormalBlackHeavenBoss) {
         NormalBlackHeavenBoss fs = (NormalBlackHeavenBoss)this.getFieldSetInstance();
         if (!fs.isPracticeMode() && this.getId() == 350060950 && this.getMonsterById(8950109) == null && player.getCurrentBossPhase() > 0) {
            this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8950109), new Point(70, 18));
            this.clearCurrentPhase(player.getParty());
         }
      }

      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HardBlackHeavenBoss) {
         HardBlackHeavenBoss fs = (HardBlackHeavenBoss)this.getFieldSetInstance();
         if (!fs.isPracticeMode() && this.getId() == 350060650 && this.getMonsterById(8950110) == null && player.getCurrentBossPhase() > 0) {
            this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8950110), new Point(70, 18));
            this.clearCurrentPhase(player.getParty());
         }
      }

      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellBlackHeavenBoss) {
         HellBlackHeavenBoss fs = (HellBlackHeavenBoss)this.getFieldSetInstance();
         if (!fs.isPracticeMode() && this.getId() == 350060650 && this.getMonsterById(8950110) == null && player.getCurrentBossPhase() > 0) {
            this.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8950110), new Point(70, 18));
            this.clearCurrentPhase(player.getParty());
         }
      }

      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellBlackHeavenBoss && this.getId() != 350060650) {
         this.hellMode = true;
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.ELITE_STATE.getValue());
         packet.writeInt(EliteState.EliteBoss.getType());
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeMapleAsciiString("Bgm41/Gravity Core");
         packet.writeMapleAsciiString("Effect/EliteMobEff.img/eliteMonsterEffect2");
         packet.writeMapleAsciiString("Effect/EventEffect.img/gloryWmission/screenEff");
         player.send(packet.getPacket());
      }

      MapleMonster boss = this.findBoss();
      if (boss != null) {
         boss.updateLaser();
      }
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellBlackHeavenBoss && this.getId() != 350060650) {
         this.hellMode = true;
         mob.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + System.currentTimeMillis(), null, 0);
         if (mob.getId() == 8950000
            || mob.getId() == 8950001
            || mob.getId() == 8950002
            || mob.getId() == 8950100
            || mob.getId() == 8950101
            || mob.getId() == 8950102) {
            MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
            e.setCancelTask(5000L);
            mob.applyStatus(e);
         }
      }

      if (DBConfig.isGanglim
         && (
            this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof NormalBlackHeavenBoss
               || this.getFieldSetInstance() instanceof HardBlackHeavenBoss
         )
         && this.getBoss() != null
         && this.getBoss().getId() == mob.getId()) {
         MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
         e.setCancelTask(0L);
         mob.applyStatus(e);
      }

      if (mob.getId() == 8950000 || mob.getId() == 8950100) {
         Rect mBR = this.calculateMBR();
         if (mob.getId() == 8950100) {
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               48,
               1,
               2,
               3600000,
               4000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetTrueDamR(10),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, 16, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               49,
               1,
               2,
               3600000,
               4000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetTrueDamR(10),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, 16, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               50,
               1,
               2,
               3600000,
               4000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetTrueDamR(20),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, 16, this)
               }
            );
         } else {
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               48,
               1,
               2,
               3600000,
               4000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetTrueDamR(10),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 65),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, 16, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               49,
               1,
               2,
               3600000,
               4000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetTrueDamR(20),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 65),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, 16, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               50,
               1,
               2,
               3600000,
               4000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetTrueDamR(30),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 65),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, 16, this)
               }
            );
         }

         mob.addAllowedFsmSkill(1);
         mob.addAllowedFsmSkill(2);
         mob.addAllowedFsmSkill(3);
         mob.addSkillFilter(1);
         mob.addSkillFilter(2);
         mob.addSkillFilter(3);
      } else if (mob.getId() == 8950001 || mob.getId() == 8950101) {
         Rect mBR = this.calculateMBR();
         if (mob.getId() == 8950101) {
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               48,
               1,
               2,
               3600000,
               4000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetTrueDamR(10),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               49,
               1,
               2,
               3600000,
               4000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetTrueDamR(10),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               50,
               1,
               2,
               3600000,
               5000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetTrueDamR(20),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               51,
               1,
               2,
               3600000,
               5000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetTrueDamR(40),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
         } else {
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               48,
               1,
               2,
               3600000,
               4000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetTrueDamR(10),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               49,
               1,
               2,
               3600000,
               4000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetTrueDamR(20),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               50,
               1,
               2,
               3600000,
               5000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetTrueDamR(30),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               51,
               1,
               2,
               3600000,
               5000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
         }
      } else if (mob.getId() == 8950002 || mob.getId() == 8950102) {
         Rect mBR = this.calculateMBR();
         if (mob.getId() == 8950102) {
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               48,
               1,
               2,
               3600000,
               4000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetTrueDamR(10),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               49,
               1,
               2,
               3600000,
               4000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetTrueDamR(10),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               50,
               1,
               2,
               3600000,
               5000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetTrueDamR(20),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               51,
               1,
               2,
               3600000,
               5000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetTrueDamR(40),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               52,
               0,
               2,
               3600000,
               10000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetTrueDamR(100),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
         } else {
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               48,
               0,
               2,
               3600000,
               4000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetTrueDamR(10),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               49,
               1,
               2,
               3600000,
               4000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetTrueDamR(20),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               50,
               1,
               2,
               3600000,
               5000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetTrueDamR(30),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               51,
               1,
               2,
               3600000,
               5000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               52,
               0,
               2,
               3600000,
               5000,
               false,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(15, 15),
                  ObstacleAtomCreatorOption.SetTrueDamR(100),
                  ObstacleAtomCreatorOption.SetCollisionDisease(123, 47),
                  ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -520, -14, this)
               }
            );
         }
      }
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      super.onMobKilled(mob);
      MapleCharacter p = null;

      for (MapleCharacter player : this.getCharactersThreadsafe()) {
         if (player != null) {
            p = player;
            break;
         }
      }

      Party party = p.getParty();
      if (party != null) {
         if (mob.getId() != 8950000 && mob.getId() != 8950100) {
            if (mob.getId() == 8950001 || mob.getId() == 8950101) {
               for (MapleMonster m : this.getAllMonstersThreadsafe()) {
                  this.removeMonster(m, 1);
               }

               this.clearObstacleAtomCreators();

               for (PartyMemberEntry entry : party.getPartyMember().getPartyMemberList()) {
                  MapleCharacter character = GameServer.getInstance(p.getClient().getChannel()).getPlayerStorage().getCharacterById(entry.getId());
                  if (character != null
                     && character.getDeathCount() > 0
                     && (character.getEventInstance() != null || character.getMap().getFieldSetInstance() != null)) {
                     character.setRegisterTransferFieldTime(System.currentTimeMillis() + 6000L);
                     character.setRegisterTransferField(mob.getMap().getId() + 100);
                  }
               }
            } else if (mob.getId() == 8950002 || mob.getId() == 8950102) {
               for (MapleMonster m : this.getAllMonstersThreadsafe()) {
                  this.removeMonster(m, 1);
               }

               this.clearObstacleAtomCreators();

               for (PartyMemberEntry entryx : party.getPartyMember().getPartyMemberList()) {
                  MapleCharacter character = GameServer.getInstance(p.getClient().getChannel()).getPlayerStorage().getCharacterById(entryx.getId());
                  if (character != null
                     && character.getDeathCount() > 0
                     && (character.getEventInstance() != null || character.getMap().getFieldSetInstance() != null)) {
                     character.setRegisterTransferFieldTime(System.currentTimeMillis() + 6000L);
                     character.setRegisterTransferField(mob.getMap().getId() + 50);
                  }
               }
            }
         } else {
            for (MapleMonster m : this.getAllMonstersThreadsafe()) {
               this.removeMonster(m, 1);
            }

            this.clearObstacleAtomCreators();
            this.broadcastMessage(CField.MapEff("Map/Effect2.img/blackHeavenBossDie3"));
            this.broadcastMessage(CField.playSound("BlackHeavenBoss/CoreEnd", 100));

            for (PartyMemberEntry entryxx : party.getPartyMember().getPartyMemberList()) {
               MapleCharacter character = GameServer.getInstance(p.getClient().getChannel()).getPlayerStorage().getCharacterById(entryxx.getId());
               if (character != null
                  && character.getDeathCount() > 0
                  && (character.getEventInstance() != null || character.getMap().getFieldSetInstance() != null)) {
                  character.setRegisterTransferFieldTime(System.currentTimeMillis() + 6000L);
                  character.setRegisterTransferField(mob.getMap().getId() + 100);
               }
            }
         }
      }
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
      super.onMobChangeHP(mob);
      if (mob.getId() == 8950000 || mob.getId() == 8950100) {
         if (mob.getHPPercent() <= 95 && mob.getHPPercent() > 80) {
            mob.addAllowedFsmSkill(4);
         } else if (mob.getHPPercent() <= 80 && mob.getHPPercent() > 60 && !this.hellMode) {
            mob.removeSkillFilter(1);
            mob.addSkillFilter(2);
            mob.addSkillFilter(3);
         } else if (mob.getHPPercent() <= 60 && mob.getHPPercent() > 40 && !this.hellMode) {
            mob.addSkillFilter(1);
            mob.removeSkillFilter(2);
            mob.addSkillFilter(3);
         } else if (mob.getHPPercent() <= 40 && mob.getHPPercent() > 20 && !this.hellMode) {
            mob.addSkillFilter(1);
            mob.addSkillFilter(2);
            mob.removeSkillFilter(3);
         } else if (mob.getHPPercent() <= 20) {
            mob.addSkillFilter(1);
            mob.addSkillFilter(2);
            mob.addSkillFilter(3);
         }
      }
   }

   @Override
   public void onPlayerDead(MapleCharacter player) {
      super.onPlayerDead(player);
   }

   @Override
   public void onCompleteFieldCommand() {
      super.onCompleteFieldCommand();
   }

   @Override
   public void onMobSkill(MapleMonster mob, int skillID, int skillLevel) {
      super.onMobSkill(mob, skillID, skillLevel);
      if (skillID == 228) {
         if (skillLevel == 1) {
            mob.removeAllowedFsmSkill(1);
         } else if (skillLevel == 2) {
            mob.removeAllowedFsmSkill(2);
         } else if (skillLevel == 3) {
            mob.removeAllowedFsmSkill(3);
         }
      } else if (skillID == 223) {
         this.broadcastMessage(CWvsContext.getScriptProgressMessage("블랙헤븐의 코어가 침입자를 향해 공격을 시작합니다."));
         this.broadcastMessage(CField.playSound("BlackHeavenBoss/CoreStart", 100));
      }
   }

   public MapleMonster findBoss() {
      int[] bossList = new int[]{8950000, 8950100};

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         for (int boss : bossList) {
            if (mob.getId() == boss) {
               return mob;
            }
         }
      }

      return null;
   }

   public MapleMonster getBoss() {
      int[] boss = new int[]{8950000, 8950001, 8950002, 8950100, 8950101, 8950102};

      for (int mob : boss) {
         MapleMonster ret = this.getMonsterById(mob);
         if (ret != null) {
            return ret;
         }
      }

      return null;
   }

   public boolean isHellMode() {
      return this.hellMode;
   }

   public void setHellMode(boolean hellMode) {
      this.hellMode = hellMode;
   }
}
