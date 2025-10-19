package objects.fields.child.sernium;

import constants.QuestExConstants;
import constants.ServerConstants;
import database.DBConfig;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import logging.LoggingManager;
import logging.entry.BossLog;
import logging.entry.BossLogType;
import network.SendPacketOpcode;
import network.center.Center;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import network.models.MobPacket;
import objects.context.party.PartyMemberEntry;
import objects.effect.child.TextEffect;
import objects.fields.fieldset.instance.NormalSerenBoss;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobMoveAction;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.OverrideMonsterStats;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.fields.obstacle.ObstacleAtom;
import objects.fields.obstacle.ObstacleAtomCreateType;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Pair;
import objects.utils.Randomizer;
import objects.utils.Rect;

public class Field_SerenPhase2 extends Field_Seren {
   private static int MAX_GAUGE = 1000;
   private static int ONE_CYCLE_TIME = 420;
   private static double ONE_MOVE_TIME = ONE_CYCLE_TIME / 360.0;
   private Field_SerenPhase2.SerenClock serenClock = null;
   private int currentPhase = 0;
   private Point serenPos = new Point(0, 0);
   private long serenHP = 0L;
   private long nextPhaseTime = 0L;
   private long nextMitrasFlamesTime = 0L;
   private long nextLaserTime = 0L;
   private long nextCreateFlameTentTime = 0L;
   private long nextAreaExplosionTime = 0L;
   private long nextNerotasPower_1Time = 0L;
   private long nextNerotasPower_2Time = 0L;
   private long lastBossFreezeTime = 0L;
   private boolean canDecPhaseTime = true;
   private boolean nextPhaseTimeUserHit = false;
   private AtomicInteger delaySN = new AtomicInteger(0);
   int[] boss = new int[]{8880607, 8880609, 8880612, 8880603, 8880637, 8880639, 8880642, 8880633};
   int[] boss2 = new int[]{8880608, 8880610, 8880613, 8880604, 8880638, 8880640, 8880643, 8880634};

   public Field_SerenPhase2(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.serenClock != null) {
         MapleMonster boss = this.findBoss();
         if (boss != null) {
            this.sendMoveSerenClock(null, this.serenClock.getCurrentAngle(this.currentPhase, this.nextPhaseTime));
            switch (boss.getId()) {
               case 8880603:
               case 8880633:
                  if (this.nextAreaExplosionTime == 0L || this.nextAreaExplosionTime <= System.currentTimeMillis()) {
                     MapleMonster dummy = this.getMonsterById(boss.getId() + 1);
                     if (dummy != null) {
                        PacketEncoder packet = new PacketEncoder();
                        packet.writeShort(SendPacketOpcode.MOB_NEXT_ATTACK.getValue());
                        packet.writeInt(dummy.getObjectId());
                        packet.writeInt(1);
                        packet.writeInt(0);
                        packet.writeInt(0);
                        this.broadcastMessage(packet.getPacket());
                     }

                     this.nextAreaExplosionTime = System.currentTimeMillis() + 10000L;
                  }
                  break;
               case 8880607:
               case 8880637:
                  MapleMonster dummy = this.getMonsterById(boss.getId() + 1);
                  if (dummy != null) {
                     if (this.nextMitrasFlamesTime == 0L || this.nextMitrasFlamesTime <= System.currentTimeMillis()) {
                        PacketEncoder packet = new PacketEncoder();
                        packet.writeShort(SendPacketOpcode.MOB_NEXT_ATTACK.getValue());
                        packet.writeInt(dummy.getObjectId());
                        packet.writeInt(1);
                        packet.writeInt(0);
                        packet.writeInt(0);
                        this.broadcastMessage(packet.getPacket());
                        this.nextMitrasFlamesTime = System.currentTimeMillis() + 15000L;
                     }

                     if (this.nextLaserTime == 0L || this.nextLaserTime <= System.currentTimeMillis()) {
                        PacketEncoder packet = new PacketEncoder();
                        packet.writeShort(SendPacketOpcode.FIELD_SKILL_RESULT.getValue());
                        packet.writeInt(100023);
                        packet.writeInt(1);
                        packet.writeInt(boss.getId() == 8880607 ? 8880608 : 8880638);
                        packet.writeInt(1);
                        packet.writeInt(0);
                        packet.writeInt(80);
                        packet.writeInt(80);
                        packet.writeInt(0);
                        packet.writeInt(1440);
                        packet.writeInt(210);
                        List<Pair<Integer, Integer>> list = new ArrayList<>();
                        Rect mBR = this.calculateMBR();
                        int size = 2;
                        if (boss.getHPPercent() < 30 && Randomizer.isSuccess(20)) {
                           size = 3;
                        }

                        for (int i = 0; i < size; i++) {
                           boolean isLeft = false;
                           if (i == 1 || i >= 2 && Randomizer.isSuccess(50)) {
                              isLeft = true;
                           }

                           if (isLeft) {
                              int startX = Randomizer.rand(mBR.getRight() / 2, mBR.getRight() - 10);
                              int endDelta = Randomizer.rand(800, 1900);
                              list.add(new Pair<>(startX, startX - endDelta));
                           } else {
                              int startX = Randomizer.rand(mBR.getLeft() + 10, mBR.getLeft() / 2);
                              int endDelta = Randomizer.rand(800, 1900);
                              list.add(new Pair<>(startX, startX + endDelta));
                           }
                        }

                        packet.writeInt(size);
                        list.stream().forEach(p -> {
                           packet.writeInt(p.left);
                           packet.writeInt(p.right);
                           packet.writeInt(Randomizer.rand(810, 3420));
                        });
                        this.broadcastMessage(packet.getPacket());
                        this.nextLaserTime = System.currentTimeMillis() + 10000L;
                     }
                  }
                  break;
               case 8880612:
               case 8880642:
                  if (this.nextAreaExplosionTime == 0L || this.nextAreaExplosionTime <= System.currentTimeMillis()) {
                     dummy = this.getMonsterById(boss.getId() + 1);
                     if (dummy != null) {
                        PacketEncoder packet = new PacketEncoder();
                        packet.writeShort(SendPacketOpcode.MOB_NEXT_ATTACK.getValue());
                        packet.writeInt(dummy.getObjectId());
                        packet.writeInt(1);
                        packet.writeInt(0);
                        packet.writeInt(0);
                        this.broadcastMessage(packet.getPacket());
                     }

                     this.nextAreaExplosionTime = System.currentTimeMillis() + 5000L;
                  }
            }

            if (this.currentPhase == 1) {
               for (MapleCharacter player : this.getCharactersThreadsafe()) {
                  int delta = (int)(player.getStat().getCurrentMaxHp(player) * 0.01 * 1.0);
                  player.addHP(-delta);
               }

               if (this.nextCreateFlameTentTime == 0L) {
                  this.nextCreateFlameTentTime = System.currentTimeMillis() + 60000L;
               }

               if (this.nextCreateFlameTentTime <= System.currentTimeMillis()) {
                  MapleMonster mob = MapleLifeFactory.getMonster(8880611);
                  this.spawnMonsterOnGroundBelow(mob, new Point(547, 304));
                  mob = MapleLifeFactory.getMonster(8880611);
                  this.spawnMonsterOnGroundBelow(mob, new Point(-587, 304));
                  this.nextCreateFlameTentTime = System.currentTimeMillis() + 60000L;
               }
            } else if (this.currentPhase == 2) {
               for (MapleCharacter player : this.getCharactersThreadsafe()) {
                  if (player != null) {
                     this.addSerenGauge(player, MAX_GAUGE, -100);
                  }
               }
            } else if (this.currentPhase == 3) {
               if (this.nextNerotasPower_1Time == 0L) {
                  this.nextNerotasPower_1Time = System.currentTimeMillis() + 15000L;
               }

               if (this.nextNerotasPower_2Time == 0L) {
                  this.nextNerotasPower_2Time = System.currentTimeMillis() + 18000L;
               }

               if (this.nextNerotasPower_1Time <= System.currentTimeMillis()) {
                  this.nextNerotasPower_1Time = System.currentTimeMillis() + 15000L;
                  int[] xPos = new int[]{-450, 50, 500};

                  for (int i = 0; i < 3; i++) {
                     if (Randomizer.isSuccess(50)) {
                        MapleMonster mob = MapleLifeFactory.getMonster(8880606);
                        this.spawnMonsterOnGroundBelow(mob, new Point(xPos[i], 305));
                     }
                  }
               }

               if (this.nextNerotasPower_2Time <= System.currentTimeMillis()) {
                  this.nextNerotasPower_2Time = System.currentTimeMillis() + 15000L;
                  int[] xPos = new int[]{-500, 0, 450};

                  for (int ix = 0; ix < 3; ix++) {
                     if (Randomizer.isSuccess(50)) {
                        MapleMonster mob = MapleLifeFactory.getMonster(8880606);
                        this.spawnMonsterOnGroundBelow(mob, new Point(xPos[ix], 305));
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.serenClock = null;
      this.currentPhase = 0;
      this.serenHP = 0L;
      this.serenPos = new Point(0, 0);
      this.delaySN = new AtomicInteger(0);
      this.nextPhaseTime = 0L;
      this.nextMitrasFlamesTime = 0L;
      this.nextLaserTime = 0L;
      this.nextCreateFlameTentTime = 0L;
      this.nextAreaExplosionTime = 0L;
      this.nextNerotasPower_1Time = 0L;
      this.nextNerotasPower_2Time = 0L;
      this.lastBossFreezeTime = 0L;
      this.canDecPhaseTime = true;
      this.nextPhaseTimeUserHit = false;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      if (this.serenClock == null) {
         this.serenClock = new Field_SerenPhase2.SerenClock(105, 110, 35, 110);
      }

      this.sendSerenGauge(player, MAX_GAUGE, player.getSerenGauge());
      this.sendSerenClock(player, Field_SerenPhase2.SerenClockType.SetClock, this.serenClock);
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
         && (mob.getId() == 8880607 || mob.getId() == 8880637)) {
         MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
         e.setCancelTask(0L);
         mob.applyStatus(e);
      }

      if (mob.getId() != 8880607 && mob.getId() != 8880637) {
         if (mob.getId() == 8880603 || mob.getId() == 8880633) {
            this.addSerenShield(17000000000000L);
         }
      } else {
         if (this.serenClock == null) {
            this.serenClock = new Field_SerenPhase2.SerenClock(105, 110, 35, 110);
            this.broadcastMessage(CWvsContext.serverNotice(5, "태양의 빛으로 가득찬 정오가 시작됩니다."));
         }

         this.sendSerenClock(null, Field_SerenPhase2.SerenClockType.SetClock, this.serenClock);
         int time = this.serenClock.getCurrentPhaseAngle(this.currentPhase);
         this.sendDelayPacket((int)(time * ONE_MOVE_TIME * 1000.0), this.getDelaySN());
         this.nextPhaseTime = System.currentTimeMillis() + (int)(time * ONE_MOVE_TIME * 1000.0);
      }
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
      super.onMobChangeHP(mob);
      if (mob.getId() == 8880602 || mob.getId() == 8880632) {
         if (this.currentPhase == 3) {
            if (mob.getHPPercent() <= 35 && mob.getHPPercent() > 15) {
               MapleMonster boss = this.findBoss();
               if (boss != null && this.getMobsSize(8880605) < 2) {
                  mob = MapleLifeFactory.getMonster(8880605);
                  this.spawnMonsterOnGroundBelow(mob, new Point(boss.getTruePosition().x + Randomizer.rand(-200, 200), 305));
               }
            } else if (mob.getHPPercent() <= 15) {
               MapleMonster boss = this.findBoss();
               if (boss != null && this.getMobsSize(8880605) < 3) {
                  mob = MapleLifeFactory.getMonster(8880605);
                  this.spawnMonsterOnGroundBelow(mob, new Point(boss.getTruePosition().x + Randomizer.rand(-200, 200), 305));
               }
            }
         }

         if (mob.getHp() <= 0L) {
            this.broadcastMessage(MobPacket.showBossHP(mob));

            for (MapleMonster m : this.getAllMonstersThreadsafe()) {
               this.removeMonster(m, 1);
            }

            TextEffect e = new TextEffect(-1, "#fn나눔고딕 Extrabold##fs25##r머지않아 태양은 다시 떠오를 것이다…", 100, 2000, 4, 0, 0, 0);
            this.broadcastMessage(e.encodeForLocal());
            if (this.getFieldSetInstance() != null) {
               this.getFieldSetInstance().restartTimeOut(300000);
            }

            AtomicBoolean broadcast = new AtomicBoolean(false);
            this.getCharactersThreadsafe()
               .stream()
               .collect(Collectors.toList())
               .forEach(
                  p -> {
                     p.temporaryStatReset(SecondaryStatFlag.DebuffIncHP);
                     if (p.getBossMode() == 0) {
                        if (this.getFieldSetInstance() instanceof NormalSerenBoss) {
                           p.setRegisterTransferField(410002180);
                        } else {
                           p.setRegisterTransferField(410002080);
                        }

                        p.setRegisterTransferFieldTime(System.currentTimeMillis() + 5000L);
                     } else {
                        if (this.getFieldSetInstance() != null) {
                           this.getFieldSetInstance().restartTimeOut(60000);
                        }

                        p.send(CField.getClock(60));
                        p.setRegisterTransferField(ServerConstants.TownMap);
                        p.setRegisterTransferFieldTime(System.currentTimeMillis() + 60000L);
                     }

                     if (p.getBossMode() == 0 && p.getCurrentBossPhase() == 2 && p.getDeathCount() > 0) {
                        p.gainItem(2632972, 1);
                        if (p.getParty() != null) {
                           if (!broadcast.get()) {
                              String list = "";
                              List<String> names = new ArrayList<>();
                              boolean normal = false;
                              if (this.getFieldSetInstance() instanceof NormalSerenBoss) {
                                 normal = true;
                              }

                              for (PartyMemberEntry mpc : new ArrayList<>(p.getParty().getPartyMemberList())) {
                                 names.add(mpc.getName());
                              }

                              list = String.join(",", names);

                              for (PartyMemberEntry mpc : new ArrayList<>(p.getParty().getPartyMemberList())) {
                                 StringBuilder sb = new StringBuilder("보스 " + (normal ? "노말" : "하드") + " 세렌 격파 (" + list + ")");
                                 MapleCharacter player = this.getCharacterById(mpc.getId());
                                 if (player != null) {
                                    LoggingManager.putLog(new BossLog(player, BossLogType.ClearLog.getType(), sb));
                                 }
                              }

                              if (this.getFieldSetInstance() instanceof NormalSerenBoss) {
                                 Center.Broadcast.broadcastMessageCheckQuest(
                                    CField.chatMsg(
                                       DBConfig.isGanglim ? 8 : 22,
                                       "[보스격파] [CH."
                                          + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                                          + "] '"
                                          + p.getParty().getLeader().getName()
                                          + "' 파티("
                                          + list
                                          + ")가 [노말 세렌]을 격파하였습니다."
                                    ),
                                    "BossMessage"
                                 );
                              } else {
                                 Center.Broadcast.broadcastMessageCheckQuest(
                                    CField.chatMsg(
                                       DBConfig.isGanglim ? 8 : 22,
                                       "[보스격파] [CH."
                                          + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                                          + "] '"
                                          + p.getParty().getLeader().getName()
                                          + "' 파티("
                                          + list
                                          + ")가 [하드 세렌]을 격파하였습니다."
                                    ),
                                    "BossMessage"
                                 );
                              }

                              if (!DBConfig.isGanglim) {
                                 boolean single = !p.isMultiMode();
                                 this.bossClear(8880602, QuestExConstants.SerniumSeren.getQuestID(), "clear_" + (single ? "single" : "multi"));
                              }
                           }

                           if (DBConfig.isGanglim) {
                              this.bossClear(8880602, QuestExConstants.SerniumSeren.getQuestID(), "clear");
                           } else {
                              this.bossClear(8880602, QuestExConstants.SerniumSeren.getQuestID(), "clear");
                           }

                           broadcast.set(true);
                        }
                     }
                  }
               );
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
      if (!player.isInvincible()) {
         if (mobTemplateID == 8880611) {
            MobSkillInfo skill = MobSkillFactory.getMobSkill(125, 43);
            if (skill != null) {
               MapleMonster mob = this.getMonsterById(mobTemplateID);
               if (mob != null) {
                  skill.applyEffect(player, mob, null, false);
               }
            }
         }

         if (this.currentPhase == 3 && (player.getSerenCanAddShieldTime() == 0L || player.getSerenCanAddShieldTime() <= System.currentTimeMillis())) {
            this.addSerenShield(1700000000000L);
            player.setSerenCanAddShieldTime(System.currentTimeMillis() + 1000L);
         }

         if (this.currentPhase != 2 && this.addSerenGauge(player, MAX_GAUGE, 100)) {
            this.canDecPhaseTime = false;
            this.addCurrentPhaseTime(this.currentPhase, 3);
         }

         if (!this.nextPhaseTimeUserHit) {
            this.nextPhaseTimeUserHit = true;
         }
      }
   }

   public void addSerenShield(long delta) {
      MapleMonster boss = this.getMonsterById(8880603);
      if (boss == null) {
         boss = this.getMonsterById(8880633);
      }

      MapleMonster serenHP = this.getMonsterById(8880602);
      if (serenHP == null) {
         serenHP = this.getMonsterById(8880632);
      }

      if (boss != null && serenHP != null) {
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.SEREN_SHIELD_EFFECT.getValue());
         packet.writeInt(boss.getObjectId());
         packet.writeMapleAsciiString("Mob/" + boss.getId() + ".img/info/shield");
         packet.writeInt(2);
         packet.writeInt(0);
         packet.writeInt(1);
         packet.writeInt(boss.getId());
         this.broadcastMessage(packet.getPacket());
         serenHP.setTotalShieldHP(serenHP.getTotalShieldHP() + delta);
         serenHP.setShieldHP(serenHP.getShieldHP() + delta);
         this.sendSerenShield(boss, serenHP);
      }
   }

   public MapleMonster findBoss() {
      for (int mobID : this.boss) {
         MapleMonster mob = this.getMonsterById(mobID);
         if (mob != null) {
            return mob;
         }
      }

      return null;
   }

   public void startNextPhase() {
      if (this.findBoss() != null) {
         this.currentPhase++;
         if (this.currentPhase >= 4) {
            this.currentPhase = 0;
         }

         this.nextPhaseTimeUserHit = false;

         for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
            if (mob != null && mob.getId() != 8880602 && mob.getId() != 8880632) {
               boolean check = false;

               for (int m : this.boss) {
                  if (m == mob.getId()) {
                     check = true;
                  }
               }

               if (!check) {
                  this.removeMonster(mob, 1);
               }
            }
         }

         for (int mx : this.boss) {
            MapleMonster mobx = this.getMonsterById(mx);
            if (mobx != null) {
               this.serenPos = mobx.getTruePosition();
               this.serenHP = mobx.getHp();
               this.lastBossFreezeTime = mobx.getCanFreezeTime();
               this.broadcastMessage(MobPacket.mobDirectionActionPacket(mobx.getObjectId(), MobMoveAction.Skill3));
               this.removeMonster(mobx, -1);
               this.sendDelayPacket(3500, this.getDelaySN());
               break;
            }
         }

         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.FIELD_SKILL_RESULT.getValue());
         packet.writeInt(100024);
         packet.writeInt(1);
         int size = 7;
         packet.writeInt(size);
         packet.writeInt(Randomizer.rand(0, size - 1));
         packet.writeInt(3060);
         packet.writeInt(2610);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         packet.writeInt(0);
         this.broadcastMessage(packet.getPacket());
      }
   }

   public void doNextPhase() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.CHANGE_SEREN_FIELD_TYPE.getValue());
      packet.writeInt(this.currentPhase + 1);
      this.broadcastMessage(packet.getPacket());
      MapleMonster mob = MapleLifeFactory.getMonster(this.boss[this.currentPhase]);
      if (mob == null) {
         mob = MapleLifeFactory.getMonster(this.boss[this.currentPhase + 4]);
      }

      OverrideMonsterStats overrideStats = new OverrideMonsterStats();
      overrideStats.setOHp(this.serenHP);
      mob.setHp(this.serenHP);
      mob.setCanFreezeTime(this.lastBossFreezeTime);
      mob.setOverrideStats(overrideStats);
      this.spawnMonsterOnGroundBelow(mob, this.serenPos);
      mob = MapleLifeFactory.getMonster(this.boss2[this.currentPhase]);
      if (mob == null) {
         mob = MapleLifeFactory.getMonster(this.boss2[this.currentPhase + 4]);
      }

      overrideStats = new OverrideMonsterStats();
      overrideStats.setOHp(this.serenHP);
      mob.setHp(this.serenHP);
      mob.setOverrideStats(overrideStats);
      this.spawnMonsterOnGroundBelow(mob, this.serenPos);
      int time = this.serenClock.getCurrentPhaseAngle(this.currentPhase);
      this.sendDelayPacket((int)(time * ONE_MOVE_TIME * 1000.0), this.getDelaySN());
      this.nextPhaseTime = System.currentTimeMillis() + (int)(time * ONE_MOVE_TIME * 1000.0);
      if (this.currentPhase == 1) {
         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            player.temporaryStatSet(SecondaryStatFlag.DebuffIncHP, 0, Integer.MAX_VALUE, 1, 80, false, false, false);
         }

         this.broadcastMessage(CWvsContext.serverNotice(5, "황혼의 불타는 듯한 석양이 회복 효율을 낮추고 지속적으로 피해를 입힙니다."));
      } else if (this.currentPhase == 2) {
         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            player.temporaryStatReset(SecondaryStatFlag.DebuffIncHP);
         }

         this.broadcastMessage(CWvsContext.serverNotice(5, "태양이 저물어 빛을 잃고 자정이 시작됩니다."));
      } else if (this.currentPhase == 3) {
         this.broadcastMessage(CWvsContext.serverNotice(5, "태양이 서서히 떠올라 빛과 희망이 시작되는 여명이 다가옵니다."));
         MapleMonster boss = this.findBoss();
         if (boss != null) {
            int count = 1;
            if (boss.getHPPercent() <= 35 && boss.getHPPercent() > 15) {
               count = 2;
            } else if (boss.getHPPercent() <= 15) {
               count = 3;
            }

            for (int i = 0; i < count; i++) {
               mob = MapleLifeFactory.getMonster(8880605);
            }
         }
      } else if (this.currentPhase == 0) {
         MapleMonster serenHP = this.getMonsterById(8880602);
         if (serenHP == null) {
            serenHP = this.getMonsterById(8880632);
         }

         if (serenHP != null) {
            long shieldHP = serenHP.getShieldHP();
            this.serenHP += shieldHP;
            serenHP.setHp(Math.min(serenHP.getStats().getMaxHp(), serenHP.getHp() + shieldHP));
            this.broadcastMessage(MobPacket.showBossHP(serenHP));
            MapleMonster boss = this.findBoss();
            if (boss != null) {
               boss.setHp(Math.min(boss.getStats().getMaxHp(), boss.getHp() + shieldHP));
            }

            serenHP.setShieldHP(0L);
            serenHP.setTotalShieldHP(0L);
            this.sendSerenShield(boss, serenHP);
            this.broadcastMessage(CWvsContext.serverNotice(5, "정오가 시작됨과 동시에 남아있는 여명의 기운이 세렌을 회복시킵니다."));
         }
      }

      if (this.currentPhase != 3 && this.canDecPhaseTime && !this.nextPhaseTimeUserHit) {
         boolean check = true;

         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            if (!player.isAlive()) {
               check = false;
               break;
            }
         }

         if (check) {
            if (this.currentPhase == 2) {
               this.addCurrentPhaseTime(1, -3);
            } else {
               this.addCurrentPhaseTime(this.currentPhase, -3);
            }
         }
      }
   }

   public void createFlames() {
      if (this.findBoss() != null) {
         int startX = -630;

         for (int i = 0; i < 9; i++) {
            int x = startX + i * 240;
            int maxP = i % 2 == 0 ? Randomizer.rand(3, 5) : Randomizer.rand(4, 6);
            int startY = i % 2 == 0 ? -220 : -180;
            int endY = 270;
            Point endPos = this.calcDropPos(new Point(x, endY), new Point(x, endY));
            ObstacleAtom atom = new ObstacleAtom(83, new Point(x, startY), endPos, 1000);
            atom.setKey(Randomizer.nextInt());
            atom.setTrueDamR(40);
            atom.setMaxP(maxP);
            atom.setLength(endY - startY);
            atom.setDiseaseSkillID(120);
            atom.setDiseaseSkillLevel(33);
            this.broadcastMessage(CField.createSingleObstacle(ObstacleAtomCreateType.DIAGONAL, null, null, atom));
         }
      }
   }

   public void sendSerenClock(MapleCharacter player, Field_SerenPhase2.SerenClockType type, Field_SerenPhase2.SerenClock clock) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SEREN_CLOCK.getValue());
      packet.writeInt(type.getType());
      switch (type) {
         case SetClock:
            packet.writeInt(ONE_CYCLE_TIME * 1000);
            clock.encode(packet);
            break;
         case RotateClock:
            clock.encode(packet);
            packet.writeInt(360);
            break;
         case StopClock:
            packet.write(true);
      }

      if (player == null) {
         this.broadcastMessage(packet.getPacket());
      } else {
         player.send(packet.getPacket());
      }
   }

   public void sendMoveSerenClock(MapleCharacter player, int angle) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SEREN_CLOCK.getValue());
      packet.writeInt(Field_SerenPhase2.SerenClockType.Moveclock.getType());
      packet.writeInt(angle);
      if (player == null) {
         this.broadcastMessage(packet.getPacket());
      } else {
         player.send(packet.getPacket());
      }
   }

   public void sendDelayPacket(int delay, int SN) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.SEREN_DELAY_REQUEST.getValue());
      packet.writeInt(SN);
      packet.writeInt(delay);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendSerenShield(MapleMonster boss, MapleMonster serenHP) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_BARRIER.getValue());
      packet.writeInt(boss.getObjectId());
      int per = 0;
      if (serenHP.getShieldHP() > 0L) {
         per = (int)Math.ceil(serenHP.getShieldHP() * 100.0 / serenHP.getStats().getMaxHp());
      }

      packet.writeInt(per);
      packet.writeLong(serenHP.getStats().getMaxHp());
      this.broadcastMessage(packet.getPacket());
   }

   public int getAndAddDelaySN() {
      return this.delaySN.getAndAdd(1);
   }

   public int getDelaySN() {
      return this.delaySN.get();
   }

   public void addCurrentPhaseTime(int phase, int delta) {
      if (this.serenClock != null) {
         this.serenClock.addCurrentPhaseTime(phase, delta);
         if (phase != 2) {
            this.serenClock.addCurrentPhaseTime(2, -delta);
         }

         this.sendSerenClock(null, Field_SerenPhase2.SerenClockType.SetClock, this.serenClock);
         this.delaySN.addAndGet(2);
         int remain = (int)(this.nextPhaseTime - System.currentTimeMillis()) + delta * 1000;
         this.nextPhaseTime = System.currentTimeMillis() + remain;
         this.sendMoveSerenClock(null, this.serenClock.getCurrentAngle(phase, this.nextPhaseTime));
         this.sendDelayPacket(remain, this.getDelaySN());
         if (this.serenClock.getCurrentPhaseAngle(2) <= 0) {
            TextEffect e = new TextEffect(-1, "#fn나눔고딕 Extrabold##fs25##r태양이 지지 않는다면 누구도 나에게 대항할 수 없다.", 100, 2000, 4, 0, 0, 0);
            this.broadcastMessage(e.encodeForLocal());

            for (MapleCharacter player : this.getCharactersThreadsafe()) {
               player.setRegisterTransferField(ServerConstants.TownMap);
               player.setRegisterTransferFieldTime(System.currentTimeMillis() + 5000L);
            }
         }
      }
   }

   public class SerenClock {
      private int noonAngle = 0;
      private int sunsetAngle = 0;
      private int midnightAngle = 0;
      private int dawnAngle = 0;

      public SerenClock(int noonAngle, int sunsetAngle, int midnightAngle, int dawnAngle) {
         this.noonAngle = noonAngle;
         this.sunsetAngle = sunsetAngle;
         this.midnightAngle = midnightAngle;
         this.dawnAngle = dawnAngle;
      }

      public void encode(PacketEncoder packet) {
         packet.writeInt(this.noonAngle);
         packet.writeInt(this.sunsetAngle);
         packet.writeInt(this.midnightAngle);
         packet.writeInt(this.dawnAngle);
      }

      public int getCurrentPhaseAngle(int currentPhase) {
         if (currentPhase == 0) {
            return this.noonAngle;
         } else if (currentPhase == 1) {
            return this.sunsetAngle;
         } else if (currentPhase == 2) {
            return this.midnightAngle;
         } else {
            return currentPhase == 3 ? this.dawnAngle : 0;
         }
      }

      public void addCurrentPhaseTime(int phase, int delta) {
         if (phase == 0) {
            this.noonAngle += delta;
         } else if (phase == 1) {
            this.sunsetAngle += delta;
         } else if (phase == 2) {
            this.midnightAngle = Math.max(0, this.midnightAngle + delta);
         } else if (phase == 3) {
            this.dawnAngle += delta;
         }
      }

      public int getCurrentAngle(int currentPhase, long nextPhaseTime) {
         int rotate = 0;
         int remain = (int)(nextPhaseTime - System.currentTimeMillis());
         int startRotate = 360 - this.noonAngle / 2;
         rotate = startRotate;
         if (currentPhase >= 1) {
            rotate = startRotate + this.noonAngle;
         }

         if (currentPhase >= 2) {
            rotate += this.sunsetAngle;
         }

         if (currentPhase >= 3) {
            rotate += this.midnightAngle;
         }

         if (remain >= 0) {
            int currentAngle = this.getCurrentPhaseAngle(currentPhase);
            rotate += currentAngle - (int)(Math.round(remain / 1000.0) / Field_SerenPhase2.ONE_MOVE_TIME);
         }

         if (rotate >= 360) {
            rotate -= 360;
         }

         return rotate;
      }
   }

   public static enum SerenClockType {
      SetClock(0),
      RotateClock(1),
      StopClock(2),
      Moveclock(3);

      private int type;

      private SerenClockType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }
   }
}
