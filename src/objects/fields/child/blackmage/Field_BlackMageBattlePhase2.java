package objects.fields.child.blackmage;

import database.DBConfig;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.MobPacket;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.effect.child.FieldSkillEffect;
import objects.fields.fieldset.instance.HardBlackMageBoss;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatEffect;
import objects.users.stats.SecondaryStatFlag;
import objects.users.stats.SecondaryStatManager;
import objects.utils.Randomizer;

public class Field_BlackMageBattlePhase2 extends Field_BlackMage {
   private long nextCreateDarkFallingTime = 0L;
   private long lastCreateBlackChainsTime = 0L;
   private long nextCreateLightningColumnsTime = 0L;
   private long startApplyDamageByLightningColumnsTime = 0L;
   private long endApplyDamageByLightningColumnsTime = 0L;
   private long nextCreatePiercingGazeTime = 0L;
   private long nextCreateMorningStarfallTime = 0L;
   private long nextFlamingSpikesTime = 0L;
   private boolean spawned2PhaseBoss = false;
   private Field_BlackMageBattlePhase2.FlamingSpikesType flamingSpikesType = Field_BlackMageBattlePhase2.FlamingSpikesType.None;

   public Field_BlackMageBattlePhase2(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
      this.setPhase(2);
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.nextCreateDarkFallingTime = 0L;
      this.lastCreateBlackChainsTime = 0L;
      this.nextCreateLightningColumnsTime = 0L;
      this.startApplyDamageByLightningColumnsTime = 0L;
      this.endApplyDamageByLightningColumnsTime = 0L;
      this.nextCreatePiercingGazeTime = 0L;
      this.nextFlamingSpikesTime = 0L;
      this.spawned2PhaseBoss = false;
      this.flamingSpikesType = Field_BlackMageBattlePhase2.FlamingSpikesType.None;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      this.setPhase(2);
      player.setCanAttackBMRewardMob(false);
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

      if (mob.getId() == 8880502) {
         this.spawned2PhaseBoss = true;
      }
   }

   @Override
   public MapleMonster findBoss() {
      return this.getMonsterById(8880502);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      MapleMonster boss = this.findBoss();
      if (this.spawned2PhaseBoss && boss == null) {
         for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
            this.removeMonster(mob, 1);
         }

         this.sendBlackMageNotice("검은 마법사로부터 알 수 없는 기운이 뿜어져 나와 어둠의 왕좌를 삼킨다.", 7000);
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
                  && (character.getEventInstance() != null || character.getMap().getFieldSetInstance() != null)) {
                  character.setCurrentBossPhase(3);
                  if (character.getRegisterTransferFieldTime() == 0L) {
                     character.setRegisterTransferField(this.getId() + 100);
                     character.setRegisterTransferFieldTime(System.currentTimeMillis() + 3000L);
                  }
               }
            }

            this.spawned2PhaseBoss = false;
         }
      } else if (this.spawned2PhaseBoss) {
         if (this.nextCreatePiercingGazeTime == 0L) {
            this.nextCreatePiercingGazeTime = System.currentTimeMillis() + 35000L;
         }

         if (this.nextCreatePiercingGazeTime != 0L && this.nextCreatePiercingGazeTime <= System.currentTimeMillis()) {
            if (boss != null) {
               this.sendBlackMageNotice("파멸의 눈이 적을 쫓는다.", 3000);
               this.broadcastMessage(MobPacket.blackMageSkillAction(boss.getObjectId(), 5, false));
               this.sendPiercingGaze();
            }

            this.nextCreatePiercingGazeTime = System.currentTimeMillis() + 60000L;
         }

         if (this.nextCreateMorningStarfallTime == 0L) {
            this.nextCreateMorningStarfallTime = System.currentTimeMillis() + 45000L;
         }

         if (this.nextCreateMorningStarfallTime != 0L && this.nextCreateMorningStarfallTime <= System.currentTimeMillis()) {
            this.createMorningStarfall();
            this.nextCreateMorningStarfallTime = System.currentTimeMillis() + 60000L;
         }

         if (this.nextCreateDarkFallingTime == 0L) {
            this.nextCreateDarkFallingTime = System.currentTimeMillis() + 45000L;
         }

         if (this.nextCreateDarkFallingTime != 0L && this.nextCreateDarkFallingTime <= System.currentTimeMillis()) {
            this.createDarkFalling();
            this.nextCreateDarkFallingTime = System.currentTimeMillis() + 60000L;
         }

         if (this.lastCreateBlackChainsTime == 0L) {
            this.lastCreateBlackChainsTime = System.currentTimeMillis() + 3000L;
         }

         if (this.lastCreateBlackChainsTime != 0L && this.lastCreateBlackChainsTime <= System.currentTimeMillis()) {
            this.sendCreateBlackChains();
            if (boss != null) {
               this.broadcastMessage(MobPacket.blackMageSkillAction(boss.getObjectId(), 3, false));
            }

            this.lastCreateBlackChainsTime = System.currentTimeMillis() + 8000L;
         }

         if (this.nextCreateLightningColumnsTime == 0L) {
            this.nextCreateLightningColumnsTime = System.currentTimeMillis() + 72000L;
         }

         if (this.nextCreateLightningColumnsTime != 0L && this.nextCreateLightningColumnsTime <= System.currentTimeMillis()) {
            this.flamingSpikesType = Field_BlackMageBattlePhase2.FlamingSpikesType.get(Randomizer.rand(0, 2));
            if (boss != null) {
               this.broadcastMessage(MobPacket.blackMageSkillAction(boss.getObjectId(), this.flamingSpikesType.getType(), true));
               this.lightningColumns();
               this.sendBlackMageNotice("검은 마법사의 붉은 번개가 모든 곳을 뒤덮는다. 피할 곳을 찾아야 한다.", 3000);
            }

            this.nextCreateLightningColumnsTime = System.currentTimeMillis() + 72000L;
         }

         if (this.startApplyDamageByLightningColumnsTime != 0L && this.startApplyDamageByLightningColumnsTime <= System.currentTimeMillis()) {
            for (MapleCharacter playerx : this.getCharactersThreadsafe()) {
               if (playerx.isAlive()) {
                  int x = playerx.getTruePosition().x;
                  boolean f = false;
                  if (this.flamingSpikesType == Field_BlackMageBattlePhase2.FlamingSpikesType.Center) {
                     if (x > 206 || x < -142) {
                        f = true;
                     }
                  } else if (this.flamingSpikesType == Field_BlackMageBattlePhase2.FlamingSpikesType.Left) {
                     if (x > -686) {
                        f = true;
                     }
                  } else if (this.flamingSpikesType == Field_BlackMageBattlePhase2.FlamingSpikesType.Right && x < 667) {
                     f = true;
                  }

                  if (f) {
                     if (playerx.getBuffedValue(SecondaryStatFlag.HolyMagicShell) != null) {
                        Integer value = playerx.getBuffedValue(SecondaryStatFlag.HolyMagicShell);
                        SecondaryStatEffect eff = playerx.getBuffedEffect(SecondaryStatFlag.HolyMagicShell);
                        if (value <= 0) {
                           playerx.temporaryStatReset(SecondaryStatFlag.HolyMagicShell);
                        } else {
                           SecondaryStatManager statManager = new SecondaryStatManager(playerx.getClient(), playerx.getSecondaryStat());
                           statManager.changeStatValue(SecondaryStatFlag.HolyMagicShell, 2311009, value - 1);
                           statManager.temporaryStatSet();
                        }
                     } else if (playerx.getBuffedValue(SecondaryStatFlag.BlessingArmor) != null) {
                        Integer v = playerx.getBuffedValue(SecondaryStatFlag.BlessingArmor);
                        if (v != null) {
                           if (v <= 0) {
                              playerx.temporaryStatReset(SecondaryStatFlag.BlessingArmor);
                           } else {
                              SecondaryStatManager statManager = new SecondaryStatManager(playerx.getClient(), playerx.getSecondaryStat());
                              statManager.changeStatValue(SecondaryStatFlag.BlessingArmor, 1210016, v - 1);
                              statManager.temporaryStatSet();
                           }
                        }
                     } else if (playerx.getBuffedValue(SecondaryStatFlag.StormGuard) != null) {
                        playerx.temporaryStatReset(SecondaryStatFlag.StormGuard);
                     } else if (playerx.getBuffedValue(SecondaryStatFlag.indiePartialNotDamaged) == null
                        && playerx.getBuffedValue(SecondaryStatFlag.NotDamaged) == null) {
                        SecondaryStatEffect eff = playerx.getBuffedEffect(SecondaryStatFlag.indiePartialNotDamaged);
                        if (eff == null) {
                           int hp = (int)(playerx.getStat().getCurrentMaxHp(playerx) * 0.01 * 1000.0);
                           playerx.healHP(-hp);
                        }
                     }
                  }
               }
            }

            if (this.endApplyDamageByLightningColumnsTime != 0L && this.endApplyDamageByLightningColumnsTime <= System.currentTimeMillis()) {
               this.startApplyDamageByLightningColumnsTime = 0L;
               this.endApplyDamageByLightningColumnsTime = 0L;
               this.flamingSpikesType = Field_BlackMageBattlePhase2.FlamingSpikesType.None;
               this.createBarrier();
            }

            if (this.nextFlamingSpikesTime == 0L) {
               this.nextFlamingSpikesTime = System.currentTimeMillis() + Randomizer.rand(15, 25) * 1000;
            }

            if ((
                  this.startApplyDamageByLightningColumnsTime == 0L && this.endApplyDamageByLightningColumnsTime == 0L
                     || this.startApplyDamageByLightningColumnsTime - 5000L > System.currentTimeMillis()
                     || this.endApplyDamageByLightningColumnsTime <= System.currentTimeMillis()
               )
               && this.nextFlamingSpikesTime != 0L
               && this.nextFlamingSpikesTime <= System.currentTimeMillis()) {
               MapleMonster mob = this.getMonsterById(8880516);
               if (mob != null) {
                  PacketEncoder packet = new PacketEncoder();
                  packet.writeShort(SendPacketOpcode.MOB_NEXT_ATTACK.getValue());
                  packet.writeInt(mob.getObjectId());
                  packet.writeInt(1);
                  packet.writeInt(0);
                  packet.writeInt(0);
                  this.broadcastMessage(packet.getPacket());
               }

               this.nextFlamingSpikesTime = System.currentTimeMillis() + Randomizer.rand(15, 25) * 1000;
            }
         }
      }
   }

   public void lightningColumns() {
      PacketEncoder packet = new PacketEncoder();
      packet.write(this.flamingSpikesType.getType());
      packet.write(0);
      FieldSkillEffect eff = new FieldSkillEffect(-1, Field_BlackMage.FieldSkill.FlamingStrikes.getSkillID(), 2, packet);
      this.broadcastMessage(eff.encodeForLocal());
      this.startApplyDamageByLightningColumnsTime = System.currentTimeMillis() + 5000L;
      this.endApplyDamageByLightningColumnsTime = System.currentTimeMillis() + 10000L;
   }

   public static enum FlamingSpikesType {
      None(-1),
      Left(0),
      Center(1),
      Right(2);

      private int type;

      private FlamingSpikesType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }

      public static Field_BlackMageBattlePhase2.FlamingSpikesType get(int type) {
         for (Field_BlackMageBattlePhase2.FlamingSpikesType f : values()) {
            if (f.getType() == type) {
               return f;
            }
         }

         return null;
      }
   }
}
