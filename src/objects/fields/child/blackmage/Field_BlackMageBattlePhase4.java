package objects.fields.child.blackmage;

import database.DBConfig;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import logging.LoggingManager;
import logging.entry.BossLog;
import logging.entry.BossLogType;
import network.SendPacketOpcode;
import network.center.Center;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import network.models.MobPacket;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.fields.fieldset.instance.HardBlackMageBoss;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;

public class Field_BlackMageBattlePhase4 extends Field_BlackMage {
   private long nextCreateAttributesExplosionTime = 0L;
   private long nextCreateWeldingCreationTime = 0L;
   private long createBarrierTime = 0L;
   private boolean spawned4PhaseBoss = false;

   public Field_BlackMageBattlePhase4(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
      this.setPhase(4);
   }

   @Override
   public MapleMonster findBoss() {
      return this.getMonsterById(8880504);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      MapleMonster boss = this.findBoss();
      if (this.spawned4PhaseBoss && boss == null) {
         for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
            this.removeMonster(mob, 1);
         }

         this.sendBlackMageNotice("창세의 알을 파괴하여 기나긴 싸움을 마무리 하자.", 30000);
         boolean set = false;
         MapleCharacter p = null;

         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            if (player != null) {
               p = player;
               break;
            }
         }

         Party party = p.getParty();
         if (party != null) {
            for (PartyMemberEntry entry : party.getPartyMember().getPartyMemberList()) {
               MapleCharacter character = GameServer.getInstance(p.getClient().getChannel()).getPlayerStorage().getCharacterById(entry.getId());
               if (character != null
                  && character.getDeathCount() > 0
                  && (character.getEventInstance() != null || character.getMap().getFieldSetInstance() != null)
                  && character.getCurrentBossPhase() >= 4
                  && character.getBossMode() != 1) {
                  if (character.getRegisterTransferFieldTime() == 0L) {
                     character.setRegisterTransferField(this.getId() + 50);
                     character.setRegisterTransferFieldTime(System.currentTimeMillis() + 3000L);
                  }

                  if (character.getParty() != null) {
                     if (character.getMapId() == this.getId()) {
                        character.setCanAttackBMRewardMob(true);
                        character.addGuildContributionByBoss(8880504);
                        character.setCurrentBossPhase(5);
                        if (character.getQuestStatus(2000021) == 1 && !character.haveItem(4036460, 1)) {
                           character.gainItem(4036460, 1);
                        }

                        if (character.getQuestStatus(2000022) == 1 && !character.haveItem(4036461, 1)) {
                           character.gainItem(4036461, 1);
                        }

                        if (character.getQuestStatus(2000023) == 1 && !character.haveItem(4036462, 1)) {
                           character.gainItem(4036462, 1);
                        }

                        if (character.getQuestStatus(2000024) == 1 && !character.haveItem(4036463, 1)) {
                           character.gainItem(4036463, 1);
                        }

                        if (character.getQuestStatus(2000025) == 1 && !character.haveItem(4036464, 1)) {
                           character.gainItem(4036464, 1);
                        }

                        if (character.getQuestStatus(2000026) == 1 && !character.haveItem(4036465, 1)) {
                           character.gainItem(4036465, 1);
                        }
                     }

                     if (!set) {
                        String list = "";
                        List<String> names = new ArrayList<>();

                        for (PartyMemberEntry mpc : new ArrayList<>(p.getParty().getPartyMember().getPartyMemberList())) {
                           names.add(mpc.getName());
                        }

                        list = String.join(",", names);

                        for (PartyMemberEntry mpc : new ArrayList<>(p.getParty().getPartyMember().getPartyMemberList())) {
                           StringBuilder sb = new StringBuilder("보스 검은 마법사 격파 (" + list + ")");
                           MapleCharacter playerx = this.getCharacterById(mpc.getId());
                           if (playerx != null) {
                              LoggingManager.putLog(new BossLog(playerx, BossLogType.ClearLog.getType(), sb));
                           }
                        }

                        Center.Broadcast.broadcastMessageCheckQuest(
                           CField.chatMsg(
                              DBConfig.isGanglim ? 8 : 22,
                              "[보스격파] [CH."
                                 + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                                 + "] '"
                                 + p.getParty().getPartyMember().getLeader().getName()
                                 + "' 파티("
                                 + list
                                 + ")가 [검은 마법사]를 격파하였습니다."
                           ),
                           "BossMessage",
                           set
                        );
                        this.bossClear(8880504, 1234570, "blackmage_clear");
                        set = true;
                     }
                  }
               }
            }

            this.spawned4PhaseBoss = false;
         }
      } else if (this.spawned4PhaseBoss) {
         if (this.nextCreateAttributesExplosionTime == 0L) {
            this.nextCreateAttributesExplosionTime = System.currentTimeMillis() + 7000L;
         }

         if (this.nextCreateAttributesExplosionTime != 0L && this.nextCreateAttributesExplosionTime <= System.currentTimeMillis()) {
            this.broadcastMessage(MobPacket.blackMageSkillAction(boss.getObjectId(), 0, false));
            this.sendAttributesExplosion();
            this.nextCreateAttributesExplosionTime = System.currentTimeMillis() + 7000L;
         }

         if (this.nextCreateWeldingCreationTime == 0L) {
            this.nextCreateWeldingCreationTime = System.currentTimeMillis() + 30000L;
         }

         if (this.nextCreateWeldingCreationTime != 0L && this.nextCreateWeldingCreationTime <= System.currentTimeMillis()) {
            this.sendBlackMageNotice("신에 가까운 자의 권능이 발현된다. 창조와 파괴, 어떤 힘을 품을지 선택해야 한다.", 3000);
            this.sendWeldingCreation(Field_BlackMage.FieldSkill.WeldingCreation);
            this.nextCreateWeldingCreationTime = System.currentTimeMillis() + 30000L;
            this.createBarrierTime = System.currentTimeMillis() + 3000L;
         }

         if (this.createBarrierTime != 0L && this.createBarrierTime <= System.currentTimeMillis()) {
            this.createBarrier();
            this.createBarrierTime = 0L;
         }
      }
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      this.setPhase(4);
      player.setBlackMageAttributes(2);
      this.blackMageTamporarySkill(player, 5);
      player.setCanAttackBMRewardMob(false);
      player.temporaryStatSet(SecondaryStatFlag.BlackMageAttributes, 0, Integer.MAX_VALUE, player.getBlackMageAttributes());
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
      this.blackMageTamporarySkill(player, 6);
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.setPhase(4);
      this.nextCreateAttributesExplosionTime = 0L;
      this.nextCreateWeldingCreationTime = 0L;
      this.createBarrierTime = 0L;
      this.spawned4PhaseBoss = false;
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

      if (mob.getId() == 8880504) {
         this.spawned4PhaseBoss = true;
         MapleMonster linked = this.getMonsterById(8880519);
         if (linked == null) {
            linked = MapleLifeFactory.getMonster(8880519);
            this.spawnMonsterOnGroundBelow(linked, new Point(0, 218));
         }
      }
   }

   public void blackMageTamporarySkill(MapleCharacter player, int type) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.BLACK_MAGE_TEMPORARY_SKILL.getValue());
      packet.writeInt(type);
      packet.writeInt(39);
      if (type == 8) {
         packet.writeInt(1);
         packet.writeInt(80002623);
         packet.writeInt(3);
         packet.writeInt(1);
         packet.writeInt(0);
         packet.writeInt(0);
      }

      player.send(packet.getPacket());
   }
}
