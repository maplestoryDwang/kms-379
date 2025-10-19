package objects.fields.child.sernium;

import database.DBConfig;
import java.util.function.Consumer;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.game.GameServer;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.effect.child.TextEffect;
import objects.fields.fieldset.instance.NormalSerenBoss;
import objects.fields.gameobject.lifes.AttackIndex;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.fields.obstacle.ObstacleAtomCreateType;
import objects.fields.obstacle.ObstacleAtomCreatorOption;
import objects.users.MapleCharacter;
import objects.utils.Rect;

public class Field_SerenPhase1 extends Field_Seren {
   private static int MAX_GAUGE = 1000;
   private static int HEAL_GAUGE = -25;
   private long nextGroundExplosionTime = 0L;
   private long nextHealGuageTime = 0L;

   public Field_SerenPhase1(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.nextGroundExplosionTime == 0L) {
         this.nextGroundExplosionTime = System.currentTimeMillis() + 5000L;
      }

      if (this.nextGroundExplosionTime <= System.currentTimeMillis()) {
         this.doGroundExplosion(1);
         this.nextGroundExplosionTime = System.currentTimeMillis() + 5000L;
      }

      if (this.nextHealGuageTime == 0L) {
         this.nextHealGuageTime = System.currentTimeMillis() + 4000L;
      }

      if (this.nextHealGuageTime <= System.currentTimeMillis()) {
         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            player.setSerenGauge(MAX_GAUGE, player.getSerenGauge() + HEAL_GAUGE);
            this.sendSerenGauge(player, MAX_GAUGE, player.getSerenGauge());
         }

         this.nextHealGuageTime = System.currentTimeMillis() + 4000L;
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      player.setSerenGauge(MAX_GAUGE, 0);
      this.sendSerenGauge(player, MAX_GAUGE, player.getSerenGauge());
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
         && this.getFieldSetInstance() instanceof NormalSerenBoss
         && (mob.getId() == 8880600 || mob.getId() == 8880630)) {
         MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
         e.setCancelTask(0L);
         mob.applyStatus(e);
      }

      if (mob.getId() == 8880600 || mob.getId() == 8880630) {
         Rect mBR = this.calculateMBR();
         this.registerObstacleAtom(
            ObstacleAtomCreateType.NORMAL,
            84,
            1,
            2,
            3600000,
            2000,
            false,
            new Consumer[]{
               ObstacleAtomCreatorOption.SetCreateDelay(100, 1000),
               ObstacleAtomCreatorOption.SetMaxP(6, 16),
               ObstacleAtomCreatorOption.SetTrueDamR(15),
               ObstacleAtomCreatorOption.Position_Horizontal(mBR.getLeft(), mBR.getRight(), -440, 270, this),
               ObstacleAtomCreatorOption.SetCollisionDisease(121, 30)
            }
         );
      }
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      super.onMobKilled(mob);
      this.clearObstacleAtomCreators();

      for (MapleMonster m : this.getAllMonstersThreadsafe()) {
         if (m != null) {
            this.removeMonster(m, 1);
         }
      }

      if (mob.getId() == 8880600 || mob.getId() == 8880630) {
         TextEffect e = new TextEffect(-1, "#fn나눔고딕 Extrabold##fs25##r태양의 불꽃은 복수를 잊지 않는다.", 100, 2000, 4, 0, 0, 0);
         this.broadcastMessage(e.encodeForLocal());
         MapleCharacter p = null;

         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            if (player != null) {
               p = player;
               break;
            }
         }

         Party party = p.getParty();
         if (party == null) {
            return;
         }

         for (PartyMemberEntry entry : party.getPartyMember().getPartyMemberList()) {
            MapleCharacter character = GameServer.getInstance(p.getClient().getChannel()).getPlayerStorage().getCharacterById(entry.getId());
            if (character != null
               && character.getDeathCount() > 0
               && (character.getEventInstance() != null || character.getMap().getFieldSetInstance() != null)) {
               character.setCurrentBossPhase(2);
               character.setRegisterTransferField(this.getId() + 20);
               character.setRegisterTransferFieldTime(System.currentTimeMillis() + 5000L);
            }
         }
      }
   }

   @Override
   public void onPlayerDead(MapleCharacter player) {
      super.onPlayerDead(player);
   }

   @Override
   public void onUserHit(MapleCharacter player, int mobTemplateID, int skillID, int skillLevel, int attackIndex) {
      super.onUserHit(player, mobTemplateID, skillID, skillLevel, attackIndex);
      if (attackIndex == AttackIndex.MobSkill.getIndex()) {
         if (skillID == 264 && skillLevel == 1) {
         }
      } else if (mobTemplateID != 8880600 && mobTemplateID != 8880630) {
         if (mobTemplateID == 8880601 || mobTemplateID == 8880631) {
            this.addSerenGauge(player, MAX_GAUGE, 200);
         }
      } else if (attackIndex == 0 || attackIndex == 1) {
         this.addSerenGauge(player, MAX_GAUGE, 100);
      } else if (attackIndex == 2) {
         this.addSerenGauge(player, MAX_GAUGE, 150);
      }
   }

   public void doGroundExplosion(int attackIndex) {
      MapleMonster target = this.getMonsterById(8880601);
      if (target == null) {
         target = this.getMonsterById(8880631);
         if (target == null) {
            return;
         }
      }

      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_NEXT_ATTACK.getValue());
      packet.writeInt(target.getObjectId());
      packet.writeInt(attackIndex);
      packet.writeInt(0);
      packet.writeInt(0);
      this.broadcastMessage(packet.getPacket());
   }
}
