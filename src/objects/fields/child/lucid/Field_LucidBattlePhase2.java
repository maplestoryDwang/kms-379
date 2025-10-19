package objects.fields.child.lucid;

import database.DBConfig;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import logging.LoggingManager;
import logging.entry.BossLog;
import logging.entry.BossLogType;
import network.SendPacketOpcode;
import network.center.Center;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.fields.DynamicObject;
import objects.fields.fieldset.instance.HardLucidBoss;
import objects.fields.fieldset.instance.HellLucidBoss;
import objects.fields.fieldset.instance.NormalLucidBoss;
import objects.fields.gameobject.lifes.BossLucid;
import objects.fields.gameobject.lifes.ChangeableStats;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Randomizer;
import scripting.EventInstanceManager;

public class Field_LucidBattlePhase2 extends Field_LucidBattle {
   public boolean maxButterfly;
   public long lastBarriageTime;
   private long endIllusionBarrage;
   private long endPhase3Time;
   private long nextBoardAttack;
   private boolean pendingPhase3;
   private boolean isTimeOut;
   private boolean clear;
   private boolean hellMode = false;

   public Field_LucidBattlePhase2(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate, 2);
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.setNextBoardAttack(0L);
      this.endPhase3Time = 0L;
      this.endIllusionBarrage = 0L;
      this.lastBarriageTime = 0L;
      this.maxButterfly = false;
      this.pendingPhase3 = false;
      this.isTimeOut = false;
      this.setClear(false);

      for (DynamicObject object : this.getDynamicObjects()) {
         object.setCurState(1);
      }
   }

   @Override
   public MapleMonster getBoss() {
      int[] boss = new int[]{8880150, 8880151, 8880152, 8880153, 8880191};

      for (int mob : boss) {
         MapleMonster ret = this.getMonsterById(mob);
         if (ret != null) {
            return ret;
         }
      }

      return null;
   }

   @Override
   public List<Point> getBufferflyPos() {
      return BossLucid.butterfly.phase2Pos;
   }

   @Override
   public void onButterflyFull() {
      this.maxButterfly = true;
   }

   @Override
   public boolean isCanSummonSubMob(int skillID, int skillLevel) {
      return this.lastSubSummon + 150000L <= System.currentTimeMillis();
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellLucidBoss) {
         mob.addResistSkill(MobTemporaryStatFlag.FREEZE, System.currentTimeMillis() + System.currentTimeMillis(), null, 0);
         if (this.getBoss() != null && this.getBoss().getId() == mob.getId()) {
            MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
            e.setCancelTask(5000L);
            mob.applyStatus(e);
         }
      }

      if (DBConfig.isGanglim
         && (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HardLucidBoss || this.getFieldSetInstance() instanceof NormalLucidBoss)
         && this.getBoss() != null
         && this.getBoss().getId() == mob.getId()) {
         MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
         e.setCancelTask(0L);
         mob.applyStatus(e);
      }

      MapleMonster boss = this.getBoss();
      if (boss != null && this.getBoss().getId() == mob.getId()) {
         if (boss.getId() == 8880152) {
            this.phase = 3;
         } else {
            this.phase = 2;
         }
      }
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      player.send(CField.UIPacket.endInGameDirectionMode(1));
      player.setCanAttackLucidRewardMob(false);
   }

   private void startPhase3() {
      Point pt = new Point(620, -50);
      MapleMonster mob = MapleLifeFactory.getMonster(8880152);
      mob.setPosition(pt);
      if (DBConfig.isGanglim && this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellLucidBoss) {
         long hp = mob.getStats().getMaxHp();
         ChangeableStats cs = new ChangeableStats(mob.getStats());
         cs.hp = hp * 40L;
         mob.getStats().setMaxHp(hp * 40L);
         mob.getStats().setHp(hp * 40L);
         mob.setMaxHp(hp * 40L);
         mob.setHp(hp * 40L);
         mob.setOverrideStats(cs);
      }

      this.spawnMonsterOnGroundBelow(mob, pt, (byte)-2, true);

      for (MapleMonster m : this.getAllMonstersThreadsafe()) {
         this.killMonster(m, false);
      }

      for (MapleCharacter player : this.getCharactersThreadsafe()) {
         player.dispelDebuff(SecondaryStatFlag.Contagion);
         EventInstanceManager eim = player.getEventInstance();
         if (eim != null) {
            eim.stopEventTimer();
         }
      }

      if (this.getFieldSetInstance() != null) {
         this.getFieldSetInstance().restartTimeOutNoClock(200000);
      }

      this.broadcastMessage(CField.getStopwatch(45000));
      this.endPhase3Time = System.currentTimeMillis() + 45000L;
      this.sendLucidHardEffectRed();
      this.phase = 3;
      this.setNextBoardAttack(System.currentTimeMillis() + 10000L);
   }

   @Override
   public void addPlayer(MapleCharacter chr) {
      super.addPlayer(chr);
      chr.send(this.makeDynamicObjectSyncPacket());
   }

   private void updateFH() {
      List<String> removeList = new ArrayList<>();

      for (Entry<String, Long> value : stainedGlasses_removed.entrySet()) {
         if (System.currentTimeMillis() - value.getValue() >= 3000L) {
            removeList.add(value.getKey());
         }
      }

      if (!removeList.isEmpty()) {
         this.sendLucidStainedGlassOnOff(true, removeList);
      }

      for (String str : removeList) {
         stainedGlasses_removed.remove(str);
      }
   }

   private void updateGolem() {
      MapleMonster lucid = this.getBoss();
      if (lucid != null) {
         this.onCreateGolem();
      }
   }

   public void onCreateGolem() {
      if (!this.illusionBarrage) {
         if (System.currentTimeMillis() >= this.lastCreateGolem + 10000L) {
            this.createGolem();
            this.lastCreateGolem = System.currentTimeMillis();
         }
      }
   }

   public void createGolem() {
      String name = stainedGlasses.get(this.lastCreateGolemIndex);
      DynamicObject object = this.getDynamicObject(name);
      if (this.lastCreateGolemIndex + 1 >= stainedGlasses.size()) {
         this.lastCreateGolemIndex = 0;
      } else {
         this.lastCreateGolemIndex++;
      }

      if (object != null && this.getBoss() != null) {
         MapleMonster mob = MapleLifeFactory.getMonster(this.getBoss().getId() == 8880151 ? 8880182 : 8880170);
         this.spawnMonster_sSack(mob, new Point(object.getPosX(), object.getPosY() - 4), -1, false, true);
         stainedGlasses_removed.put(name, System.currentTimeMillis());
         this.sendLucidStainedGlassBreak(Collections.singletonList(name));
      }
   }

   public void doIllusionBarrage() {
      MapleMonster boss = this.getBoss();
      if (boss != null) {
         this.sendLucidMoveButterfly(new Point(700, -600));

         for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
            if (mob.getId() != boss.getId()) {
               this.removeMonster(mob, 1);
            }
         }

         this.sendLucidShootAction(2);
         this.sendLucidStainedGlassOnOff(null, false);
         this.sendLucidFlyingMode(null, true);
         this.butterflies.clear();
         this.maxButterfly = false;
         this.illusionBarrage = true;
         this.endIllusionBarrage = System.currentTimeMillis() + 19000L;
         this.sendLucidSpiralShoot(10, 135, 13, 3, 3500, 10, 10, 1);
         this.sendLucidSpiralShoot(190, 270, 13, 3, 3500, 10, 10, 1);
         this.sendLucidSpiralShoot(390, 225, 13, 3, 3500, 10, 10, 1);
         this.sendLucidBiDirectionShoot(50, 20, 100000, 8);
         this.sendLucidSpiralButterfly(10, 15, 20, 40, 4000, 13, 0, 0);
      }
   }

   public void endIllusionBarrage() {
      this.illusionBarrage = false;
      this.sendLucidFlyingMode(null, false);
      this.sendLucidStainedGlassOnOff(null, true);
      this.sendLucidShootAction(6);
   }

   public void sendLucidHardEffect(int a) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.LUCID_HARD_EFFECT.getValue());
      packet.writeInt(a);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLucidHardEffectFailed() {
      this.sendLucidHardEffect(2);
   }

   public void sendLucidHardEffectNormal() {
      this.sendLucidHardEffect(3);
   }

   public void sendLucidHardEffectRed() {
      this.sendLucidHardEffect(1);
   }

   private void clearButterflies() {
      this.butterflies.clear();
      this.sendLucidRemoveButterfly();
   }

   public void doBlockAttack(int attackIndex) {
      MapleMonster target = this.getMonsterById(8880150);
      if (target != null || (target = this.getMonsterById(8880151)) != null) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.MOB_NEXT_ATTACK.getValue());
         packet.writeInt(target.getObjectId());
         packet.writeInt(attackIndex);
         packet.writeInt(0);
         packet.writeInt(0);
         this.broadcastMessage(packet.getPacket());
      }
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      this.updateGolem();
      this.updateFH();
      if (this.illusionBarrage && this.endIllusionBarrage < System.currentTimeMillis()) {
         this.endIllusionBarrage();
      }

      if (this.pendingPhase3) {
         this.setClear(true);
         this.startPhase3();
         this.pendingPhase3 = false;
      }

      if (this.endPhase3Time != 0L && this.endPhase3Time <= System.currentTimeMillis() && !this.isTimeOut) {
         this.isTimeOut = true;

         for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
            this.removeMonster(mob, 1);
         }

         this.sendLucidHardEffectFailed();
         this.clearButterflies();

         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            player.setRegisterTransferField(450004000);
            player.setRegisterTransferFieldTime(System.currentTimeMillis() + 8000L);
         }
      }
   }

   public void setLastBarriageTime(long time) {
      this.lastBarriageTime = time;
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      if (mob.getId() == 8880151) {
         this.pendingPhase3 = true;
      }

      int sub = this.getId() % 50;
      MapleCharacter pp = null;

      for (MapleCharacter player : this.getCharactersThreadsafe()) {
         if (player != null) {
            pp = player;
            break;
         }
      }

      if (pp != null) {
         Party party = pp.getParty();
         if (party != null) {
            if (mob.getId() == 8880150) {
               this.clearButterflies();
               this.sendLucidStatueDisplay(null, false);

               for (MapleCharacter playerx : this.getCharactersThreadsafe()) {
                  if (playerx.getBossMode() == 1) {
                     if (this.getFieldSetInstance() != null) {
                        this.getFieldSetInstance().restartTimeOutNoClock(10000);
                     }

                     return;
                  }
               }

               for (MapleCharacter playerxx : this.getCharactersThreadsafe()) {
                  EventInstanceManager eim = playerxx.getEventInstance();
                  if (eim != null) {
                     eim.stopEventTimer();
                  }
               }

               this.broadcastMessage(CField.getClock(10));

               for (PartyMemberEntry entry : party.getPartyMember().getPartyMemberList()) {
                  MapleCharacter character = GameServer.getInstance(pp.getClient().getChannel()).getPlayerStorage().getCharacterById(entry.getId());
                  if (character != null
                     && character.getDeathCount() > 0
                     && (character.getEventInstance() != null || character.getMap().getFieldSetInstance() != null)) {
                     character.setRegisterTransferFieldTime(System.currentTimeMillis() + 10000L);
                     character.setRegisterTransferField(450004300 + sub);
                  }
               }
            } else if (mob.getId() == 8880153) {
               if (this.isTimeOut) {
                  return;
               }

               this.broadcastMessage(CField.getClock(10));
               this.endPhase3Time = 0L;
               this.clearButterflies();
               this.sendLucidStatueDisplay(null, false);
               this.sendLucidHardEffectNormal();

               for (MapleCharacter playerxxx : this.getCharactersThreadsafe()) {
                  if (playerxxx.getBossMode() == 1 || playerxxx.getCurrentBossPhase() == 0) {
                     if (this.getFieldSetInstance() != null) {
                        this.getFieldSetInstance().restartTimeOutNoClock(10000);
                     }

                     return;
                  }
               }

               for (PartyMemberEntry entryx : party.getPartyMember().getPartyMemberList()) {
                  MapleCharacter character = GameServer.getInstance(pp.getClient().getChannel()).getPlayerStorage().getCharacterById(entryx.getId());
                  if (character != null
                     && character.getDeathCount() > 0
                     && (character.getEventInstance() != null || character.getMap().getFieldSetInstance() != null)) {
                     character.setRegisterTransferFieldTime(System.currentTimeMillis() + 10000L);
                     character.setRegisterTransferField(450004600 + sub);
                  }
               }
            }

            if (mob.getId() == 8880150 || mob.getId() == 8880153) {
               boolean set = false;

               for (MapleCharacter p : this.getCharactersThreadsafe()) {
                  if (p.getParty() != null) {
                     if (p.getMapId() == this.getId()) {
                        int quantity = 0;
                        int rand = Randomizer.nextInt(100);
                        if (mob.getId() == 8880150) {
                           if (rand <= 90) {
                              quantity = 1;
                           } else {
                              quantity = 2;
                           }
                        } else if (mob.getId() == 8880153) {
                           if (rand <= 30) {
                              quantity = 1;
                           } else if (rand <= 90) {
                              quantity = 2;
                           } else {
                              quantity = 3;
                           }
                        }

                        p.setCanAttackLucidRewardMob(true);
                        p.addGuildContributionByBoss(mob.getId());
                        p.gainItem(4001879, (short)quantity, false, -1L, "루시드 격파로 얻은 아이템");
                     }

                     if (!set) {
                        if (mob.getId() == 8880150) {
                           this.bossClearQex(p, 1234569, "normal_lucid_clear");
                           String list = "";
                           List<String> names = new ArrayList<>();

                           for (PartyMemberEntry mpc : new ArrayList<>(p.getParty().getPartyMemberList())) {
                              names.add(mpc.getName());
                              StringBuilder sb = new StringBuilder("보스 노말 루시드 격파");
                              MapleCharacter playerxxxx = this.getCharacterById(mpc.getId());
                              if (playerxxxx != null) {
                                 LoggingManager.putLog(new BossLog(playerxxxx, BossLogType.ClearLog.getType(), sb));
                              }
                           }

                           if (!DBConfig.isGanglim) {
                              list = String.join(",", names);
                              Center.Broadcast.broadcastMessage(
                                 CField.chatMsg(
                                    DBConfig.isGanglim ? 8 : 22,
                                    "[보스격파] [CH."
                                       + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                                       + "] '"
                                       + p.getParty().getLeader().getName()
                                       + "' 파티("
                                       + list
                                       + ")가 [노말 루시드]를 격파하였습니다."
                                 )
                              );
                           }
                        } else if (mob.getId() == 8880153) {
                           this.bossClearQex(p, 1234569, "hard_lucid_clear");
                           if (!DBConfig.isGanglim) {
                              String list = "";
                              List<String> names = new ArrayList<>();
                              boolean check = false;

                              for (PartyMemberEntry mpcx : new ArrayList<>(p.getParty().getPartyMemberList())) {
                                 names.add(mpcx.getName());
                              }

                              list = String.join(",", names);

                              for (PartyMemberEntry mpcx : new ArrayList<>(p.getParty().getPartyMemberList())) {
                                 boolean hell = false;
                                 if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellLucidBoss) {
                                    MapleCharacter p_ = this.getCharacterById(mpcx.getId());
                                    if (p_ != null) {
                                       String keyValue = "hell_lucid_point";
                                       p_.updateOneInfo(787777, keyValue, String.valueOf(p_.getOneInfoQuestInteger(787777, keyValue) + 3));
                                       if (!check) {
                                          this.bossClearQex(p, 1234569, "hell_lucid_clear");
                                          check = true;
                                       }
                                    }

                                    hell = true;
                                 }

                                 StringBuilder sb = new StringBuilder("보스 " + (hell ? "헬" : "하드") + " 루시드 격파 (" + list + ")");
                                 MapleCharacter playerxxxx = this.getCharacterById(mpcx.getId());
                                 if (playerxxxx != null) {
                                    LoggingManager.putLog(new BossLog(playerxxxx, BossLogType.ClearLog.getType(), sb));
                                 }
                              }

                              if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellLucidBoss) {
                                 Center.Broadcast.broadcastMessage(
                                    CField.chatMsg(
                                       22,
                                       "[보스격파] [CH."
                                          + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                                          + "] '"
                                          + p.getParty().getLeader().getName()
                                          + "' 파티("
                                          + list
                                          + ")가 [헬 루시드]를 격파하였습니다."
                                    )
                                 );
                              } else if (!DBConfig.isGanglim) {
                                 Center.Broadcast.broadcastMessage(
                                    CField.chatMsg(
                                       22,
                                       "[보스격파] [CH."
                                          + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                                          + "] '"
                                          + p.getParty().getLeader().getName()
                                          + "' 파티("
                                          + list
                                          + ")가 [하드 루시드]를 격파하였습니다."
                                    )
                                 );
                              }
                           } else {
                              String list = "";
                              List<String> names = new ArrayList<>();

                              for (PartyMemberEntry mpcx : new ArrayList<>(p.getParty().getPartyMemberList())) {
                                 names.add(mpcx.getName());
                              }

                              list = String.join(",", names);

                              for (PartyMemberEntry mpcx : new ArrayList<>(p.getParty().getPartyMemberList())) {
                                 boolean hellx = false;
                                 if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellLucidBoss) {
                                    MapleCharacter p_ = p.getClient().getChannelServer().getPlayerStorage().getCharacterById(mpcx.getId());
                                    if (p_ != null) {
                                       p_.updateOneInfo(1234569, "hell_lucid_clear", String.valueOf(p_.getOneInfoQuestInteger(1234569, "hell_lucid_clear") + 1));
                                    } else {
                                       this.updateOfflineBossLimit(mpcx.getId(), 1234569, "hell_lucid_clear", "1");
                                    }

                                    hellx = true;
                                 }

                                 StringBuilder sb = new StringBuilder("보스 " + (hellx ? "헬" : "하드") + " 루시드 격파 (" + list + ")");
                                 MapleCharacter playerxxxx = this.getCharacterById(mpcx.getId());
                                 if (playerxxxx != null) {
                                    LoggingManager.putLog(new BossLog(playerxxxx, BossLogType.ClearLog.getType(), sb));
                                 }
                              }

                              if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof HellLucidBoss) {
                                 Center.Broadcast.broadcastMessage(
                                    CField.chatMsg(
                                       22,
                                       "[보스격파] [CH."
                                          + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                                          + "] '"
                                          + p.getParty().getLeader().getName()
                                          + "' 파티("
                                          + list
                                          + ")가 [헬 루시드]를 격파하였습니다."
                                    )
                                 );
                              }
                           }
                        }

                        if (DBConfig.isGanglim) {
                           if (this.getFieldSetInstance() == null
                              || this.getFieldSetInstance() != null && !(this.getFieldSetInstance() instanceof HellLucidBoss)) {
                              this.bossClear(mob.getId(), 1234569, "lucid_clear");
                           }
                        } else {
                           this.bossClear(mob.getId(), 1234569, "lucid_clear");
                           if (this.getFieldSetInstance() == null
                              || this.getFieldSetInstance() != null && !(this.getFieldSetInstance() instanceof HellLucidBoss)) {
                              boolean multiCheck = false;

                              for (MapleCharacter partyMember : this.getCharacters()) {
                                 if (multiCheck) {
                                    break;
                                 }

                                 if (partyMember != null) {
                                    multiCheck = partyMember.isMultiMode();
                                 }
                              }

                              this.bossClear(mob.getId(), 1234569, "lucid_clear_" + (multiCheck ? "multi" : "single"));
                           }
                        }

                        set = true;
                     }
                  }
               }
            }
         }
      }
   }

   public long getNextBoardAttack() {
      return this.nextBoardAttack;
   }

   public void setNextBoardAttack(long nextBoardAttack) {
      this.nextBoardAttack = nextBoardAttack;
   }

   public boolean isClear() {
      return this.clear;
   }

   public void setClear(boolean clear) {
      this.clear = clear;
   }
}
