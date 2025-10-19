package objects.fields.child.moonbridge;

import database.DBConfig;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import logging.LoggingManager;
import logging.entry.BossLog;
import logging.entry.BossLogType;
import network.SendPacketOpcode;
import network.center.Center;
import network.encode.PacketEncoder;
import network.models.CField;
import objects.context.party.PartyMemberEntry;
import objects.fields.Field;
import objects.fields.MapleFoothold;
import objects.fields.fieldset.instance.ChaosDuskBoss;
import objects.fields.fieldset.instance.NormalDuskBoss;
import objects.fields.fieldskill.AttackInfo;
import objects.fields.fieldskill.FieldSkill;
import objects.fields.fieldskill.FieldSkillEntry;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.fields.obstacle.ObstacleAtom;
import objects.fields.obstacle.ObstacleAtomCreateType;
import objects.users.MapleCharacter;
import objects.users.skills.SkillFactory;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Randomizer;

public class Field_FerociousBattlefield extends Field {
   private long nextCreateObstacleTime = 0L;
   private long nextStormAttackTime = 0L;
   private long nextTentaclesAttackTime = 0L;
   private long nextTentaclesPatternTime = 0L;
   private long nextLaserAttackTime = 0L;
   private boolean setLaserAttack = false;
   private int mobZone = 0;
   private static double MAX_GAUGE = 1000.0;
   private int phase = 0;

   public Field_FerociousBattlefield(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.getBoss() != null) {
         if (this.nextCreateObstacleTime == 0L) {
            this.nextCreateObstacleTime = System.currentTimeMillis() + 2000L;
         }

         if (this.nextCreateObstacleTime <= System.currentTimeMillis()) {
            this.createObstacleAtoms();
            this.nextCreateObstacleTime = System.currentTimeMillis() + 2000L;
         }

         if (this.nextStormAttackTime == 0L) {
            this.nextStormAttackTime = System.currentTimeMillis() + 8000L;
         }

         if (this.nextStormAttackTime <= System.currentTimeMillis()) {
            if (this.mobZone == 1) {
               this.doStormAttack(2);
            }

            this.nextStormAttackTime = System.currentTimeMillis() + 8000L;
         }

         if (this.nextTentaclesAttackTime == 0L) {
            this.nextTentaclesAttackTime = System.currentTimeMillis() + 11000L;
         }

         if (this.nextTentaclesAttackTime <= System.currentTimeMillis()) {
            if (this.mobZone == 1) {
               if (this.getId() == 450009450) {
                  this.doFieldSkill(100020, 4, -1);
               } else {
                  this.doFieldSkill(100020, 2, -1);
               }
            }

            this.nextTentaclesAttackTime = System.currentTimeMillis() + 11000L;
         }

         if (this.nextTentaclesPatternTime == 0L) {
            this.nextTentaclesPatternTime = System.currentTimeMillis() + 55000L;
         }

         if (this.nextTentaclesPatternTime <= System.currentTimeMillis()) {
            this.doTentaclesPattern();
            this.nextTentaclesPatternTime = System.currentTimeMillis() + 999999999L;
         }

         if (this.nextLaserAttackTime == 0L) {
            this.nextLaserAttackTime = System.currentTimeMillis() + 999999999L;
         }

         if (this.nextLaserAttackTime <= System.currentTimeMillis()) {
            this.setLaserAttack = true;
            this.nextLaserAttackTime = System.currentTimeMillis() + 999999999L;
            this.nextTentaclesPatternTime = System.currentTimeMillis() + 35000L;
         }

         MapleMonster boss = this.getBoss();
         int hpPercent = boss == null ? 0 : boss.getHPPercent();

         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            if (player.getBuffedValue(SecondaryStatFlag.DuskDarkness) != null) {
               this.addDuskGauge(player, -45.0);
               if (player.getDuskGauge() <= 0.0) {
                  player.temporaryStatReset(SecondaryStatFlag.DuskDarkness);
                  this.killAllMonstersFL(true, player.getFrozenLinkSerialNumber());
                  PacketEncoder packet = new PacketEncoder();
                  packet.writeShort(SendPacketOpcode.DUSK_END_FEAR.getValue());
                  packet.writeInt(player.getId());
                  this.broadcastMessage(packet.getPacket());
               } else if (player.checkInterval(player.getLastSpawnFearMonsterTime(), 6000)) {
                  this.spawnFearHands(player);
                  this.spawnFearMonster(player);
                  if (this.getId() == 450009450) {
                     this.doFieldSkill(100022, 2, 8644659, player);
                  } else {
                     this.doFieldSkill(100022, 1, 8644658, player);
                  }

                  player.setLastSpawnFearMonsterTime(System.currentTimeMillis());
               }
            } else if (hpPercent >= 67) {
               this.addDuskGauge(player, 5.555);
            } else if (hpPercent < 67 && hpPercent >= 34) {
               this.addDuskGauge(player, 6.25);
            } else if (hpPercent < 34) {
               this.addDuskGauge(player, 7.142);
            }
         }
      }
   }

   public void setDuskGaugeByOnHit(MapleCharacter player) {
      double delta = 0.0;
      MapleMonster boss = this.getBoss();
      if (boss != null) {
         int hpPercent = boss.getHPPercent();
         if (hpPercent >= 67) {
            delta = 5.555;
         } else if (hpPercent < 67 && hpPercent >= 34) {
            delta = 6.25;
         } else if (hpPercent < 34) {
            delta = 7.142;
         }

         this.addDuskGauge(player, delta * 3.0);
      }
   }

   public void spawnFearHands(MapleCharacter player) {
      int mobID = 0;
      if (this.getId() == 450009450) {
         mobID = 8644657;
      } else {
         mobID = 8644654;
      }

      if (player.getFrozenLinkSerialNumber() == 0L) {
         player.setFrozenLinkSerialNumber(System.currentTimeMillis() + Randomizer.rand(100000, 500000));
      }

      int mobCount = this.getMobsSize(mobID);

      for (int i = mobCount; i < 3; i++) {
         MapleMonster mob = MapleLifeFactory.getMonster(mobID);
         if (mob != null) {
            this.spawnMonsterOnFrozenLink(mob, new Point(Randomizer.rand(-620, 450), -157), player.getFrozenLinkSerialNumber());
         }
      }
   }

   public void spawnFearMonster(MapleCharacter player) {
      int mobID = 0;
      if (this.getId() == 450009450) {
         mobID = 8644656;
      } else {
         mobID = 8644653;
      }

      if (player.getFrozenLinkSerialNumber() == 0L) {
         player.setFrozenLinkSerialNumber(System.currentTimeMillis() + Randomizer.rand(100000, 500000));
      }

      int mobCount = this.getMobsSize(mobID);
      if (mobCount < 6) {
         MapleMonster mob = MapleLifeFactory.getMonster(mobID);
         if (mob != null) {
            this.spawnMonsterOnFrozenLink(mob, new Point(465, -157), player.getFrozenLinkSerialNumber());
         }

         mob = MapleLifeFactory.getMonster(mobID);
         if (mob != null) {
            this.spawnMonsterOnFrozenLink(mob, new Point(-620, -157), player.getFrozenLinkSerialNumber());
         }
      }
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(respawn);
      this.nextCreateObstacleTime = 0L;
      this.nextStormAttackTime = 0L;
      this.nextTentaclesAttackTime = 0L;
      this.nextTentaclesPatternTime = 0L;
      this.nextLaserAttackTime = 0L;
      this.mobZone = 0;
      this.phase = 0;
      this.setLaserAttack = false;
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof NormalDuskBoss) {
         NormalDuskBoss fs = (NormalDuskBoss)this.getFieldSetInstance();
         if (!fs.isPracticeMode() && this.getId() == 450009430 && this.getMonsterById(8950116) == null && player.getCurrentBossPhase() > 0) {
            MapleMonster mob = MapleLifeFactory.getMonster(8950116);
            if (!DBConfig.isGanglim && player.isMultiMode()) {
               long hp = mob.getStats().getMaxHp();
               long fixedhp = hp * 3L;
               if (fixedhp < 0L) {
                  fixedhp = Long.MAX_VALUE;
               }

               mob.setHp(fixedhp);
               mob.setMaxHp(fixedhp);
            }

            this.spawnMonsterOnGroundBelow(mob, new Point(-2661, -488));
            this.clearCurrentPhase(player.getParty());
         }
      }

      if (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof ChaosDuskBoss) {
         ChaosDuskBoss fs = (ChaosDuskBoss)this.getFieldSetInstance();
         if (!fs.isPracticeMode() && this.getId() == 450009480 && this.getMonsterById(8950117) == null && player.getCurrentBossPhase() > 0) {
            MapleMonster mob = MapleLifeFactory.getMonster(8950117);
            if (!DBConfig.isGanglim && player.isMultiMode()) {
               long hp = mob.getStats().getMaxHp();
               long fixedhp = hp * 3L;
               if (fixedhp < 0L) {
                  fixedhp = Long.MAX_VALUE;
               }

               mob.setHp(fixedhp);
               mob.setMaxHp(fixedhp);
            }

            this.spawnMonsterOnGroundBelow(mob, new Point(-2661, -488));
            this.clearCurrentPhase(player.getParty());
         }
      }

      MapleMonster boss = this.getBoss();
      if (boss != null) {
         this.broadcastMobZoneChange(boss.getObjectId(), this.mobZone);
      }

      player.setDuskGauge(0.0);
      this.setDuskGauge(player, 0);
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
      player.temporaryStatReset(SecondaryStatFlag.DuskDarkness);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
      if (DBConfig.isGanglim
         && (this.getFieldSetInstance() != null && this.getFieldSetInstance() instanceof NormalDuskBoss || this.getFieldSetInstance() instanceof ChaosDuskBoss)
         && (mob.getId() == 8644655 || mob.getId() == 8644650)) {
         MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.INVINCIBLE, 1, 146, new MobSkillInfo(146, 18), true);
         e.setCancelTask(0L);
         mob.applyStatus(e);
      }

      if (mob.getId() == 8644655 || mob.getId() == 8644650) {
         mob.addSkillFilter(0);
         mob.addSkillFilter(1);
         mob.addSkillFilter(2);
         this.mobZone = 1;
         this.broadcastMobZoneChange(mob.getObjectId(), this.mobZone);
      }
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
      if (this.getBoss() != null && this.getBoss().getId() == mob.getId()) {
         if (mob.getHPPercent() < 67 && mob.getHPPercent() > 33 && this.mobZone == 1 && this.phase == 0) {
            this.doTentaclesPattern();
            this.phase++;
         } else if (mob.getHPPercent() <= 33 && this.mobZone == 1 && (this.phase == 0 || this.phase == 1)) {
            this.doTentaclesPattern();
            this.phase++;
         }
      }
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      if (mob.getId() == 8644650 || mob.getId() == 8644655) {
         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            if (player != null) {
               player.mobKilled(mob.getId(), 1, 0);
               player.setRegisterTransferField(this.getId() + 30);
               player.setRegisterTransferFieldTime(System.currentTimeMillis() + 6000L);
            }
         }
      }

      if (mob.getId() == 8950116 || mob.getId() == 8950117) {
         for (MapleMonster m : this.getAllMonstersThreadsafe()) {
            this.removeMonster(m, 1);
         }

         for (MapleCharacter playerx : this.getCharactersThreadsafe()) {
            if (playerx.getBossMode() == 1) {
               return;
            }
         }

         boolean set = false;

         for (MapleCharacter p : this.getCharactersThreadsafe()) {
            if (p.getParty() != null) {
               if (p.getMapId() == this.getId()) {
                  int quantity = 14;
                  p.gainItem(4001893, (short)quantity, false, -1L, "더스크 격파로 얻은 아이템");
               }

               p.addGuildContributionByBoss(8644650);
               if (!set) {
                  boolean multiMode = false;
                  if (mob.getId() == 8950117) {
                     String list = "";
                     List<String> names = new ArrayList<>();

                     for (PartyMemberEntry mpc : new ArrayList<>(p.getParty().getPartyMemberList())) {
                        names.add(mpc.getName());
                        StringBuilder sb = new StringBuilder("보스 카오스 더스크 격파");
                        MapleCharacter playerxx = this.getCharacterById(mpc.getId());
                        if (playerxx != null) {
                           if (!DBConfig.isGanglim && !multiMode) {
                              multiMode = playerxx.isMultiMode();
                           }

                           LoggingManager.putLog(new BossLog(playerxx, BossLogType.ClearLog.getType(), sb));
                        }
                     }

                     list = String.join(",", names);
                     if (!DBConfig.isGanglim) {
                        Center.Broadcast.broadcastMessage(
                           CField.chatMsg(
                              DBConfig.isGanglim ? 8 : 22,
                              "[보스격파] [CH."
                                 + (this.getChannel() == 2 ? "20세 이상" : (this.getChannel() == 1 ? "1" : this.getChannel() - 1))
                                 + "] '"
                                 + p.getParty().getLeader().getName()
                                 + "' 파티("
                                 + list
                                 + ")가 [카오스 더스크]를 격파하였습니다."
                           )
                        );
                     }

                     this.bossClearQex(p, 1234569, "chaos_dusk_clear");
                  } else {
                     for (PartyMemberEntry mpcx : new ArrayList<>(p.getParty().getPartyMember().getPartyMemberList())) {
                        StringBuilder sb = new StringBuilder("보스 노말 더스크 격파");
                        MapleCharacter playerxx = this.getCharacterById(mpcx.getId());
                        if (playerxx != null) {
                           if (!DBConfig.isGanglim && !multiMode) {
                              multiMode = playerxx.isMultiMode();
                           }

                           LoggingManager.putLog(new BossLog(playerxx, BossLogType.ClearLog.getType(), sb));
                        }
                     }

                     this.bossClearQex(p, 1234569, "normal_dusk_clear");
                  }

                  if (DBConfig.isGanglim) {
                     this.bossClear(8644650, 1234589, "dusk_clear");
                  } else {
                     this.bossClear(8644650, 1234589, "dusk_clear");
                     this.bossClear(8644650, 1234589, "dusk_clear_" + (multiMode ? "multi" : "single"));
                  }

                  set = true;
               }
            }
         }
      }
   }

   public void changeMobZone(int mobZone) {
      MapleMonster boss = this.getBoss();
      if (boss != null) {
         this.mobZone = mobZone;
         this.broadcastMobZoneChange(boss.getObjectId(), mobZone);
      }
   }

   @Override
   public void broadcastMobZoneChange(int objectID, int mobZoneDataType) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.MOB_ZONE_CHANGE.getValue());
      packet.writeInt(objectID);
      packet.writeInt(mobZoneDataType);
      this.broadcastMessage(packet.getPacket());
   }

   public MapleMonster getBoss() {
      int[] mobs = new int[]{8644655, 8644650};

      for (int id : mobs) {
         MapleMonster mob = this.getMonsterById(id);
         if (mob != null) {
            return mob;
         }
      }

      return null;
   }

   public void createObstacleAtoms() {
      int xLeft = this.getLeft();
      int yTop = this.getTop();
      Set<ObstacleAtom> obstacleAtomInfosSet = new HashSet<>();
      int amount = Randomizer.rand(2, 5);

      for (int i = 0; i < amount; i++) {
         int randomX = Randomizer.nextInt(this.getWidth()) + xLeft;
         Point position = new Point(randomX, -1055);
         MapleFoothold fh = this.getFootholds().findBelow(position);
         if (fh != null) {
            Point pos = this.calcPointBelow(position);
            int footholdY = pos.y;
            int height = position.y - footholdY;
            if (height < 0) {
               int var10000 = -height;
            }

            ObstacleAtom atom = new ObstacleAtom(Randomizer.rand(65, 67), position, new Point(randomX, -157), 0);
            atom.setKey(Randomizer.nextInt());
            atom.setHitBoxRange(36);
            if (this.getId() == 450009450) {
               atom.setTrueDamR(30);
            } else {
               atom.setTrueDamR(15);
            }

            atom.setHeight(0);
            atom.setMaxP(1);
            atom.setAngle(0);
            atom.setvPerSec(Randomizer.rand(4, 6) * 100);
            atom.setCreateDelay(190 + i * Randomizer.rand(300, 600));
            obstacleAtomInfosSet.add(atom);
            this.addObstacleAtom(atom);
         }
      }

      for (MapleCharacter chr : this.getCharactersThreadsafe()) {
         if (chr.getBuffedValue(SecondaryStatFlag.DuskDarkness) == null) {
            chr.send(CField.createObstacle(ObstacleAtomCreateType.NORMAL, null, null, obstacleAtomInfosSet));
         }
      }
   }

   public void setDuskGauge(MapleCharacter player, int gauge) {
      double g = Math.min(MAX_GAUGE, (double)gauge);
      if (gauge > 0 && g >= 1000.0) {
         this.doFearPattern(player);
      }

      this.sendDuskGauge(player, g, gauge < 0);
   }

   public void addDuskGauge(MapleCharacter player, double gauge) {
      double g = Math.max(0.0, Math.min(MAX_GAUGE, player.getDuskGauge() + gauge));
      player.setDuskGauge(g);
      if (gauge > 0.0 && g >= 1000.0) {
         this.doFearPattern(player);
      }

      this.sendDuskGauge(player, g, gauge < 0.0);
   }

   public void doFearPattern(MapleCharacter player) {
      player.temporaryStatSet(SecondaryStatFlag.DuskDarkness, 80002902, Integer.MAX_VALUE, 1);
   }

   public void sendDuskGauge(MapleCharacter player, double gauge, boolean decrease) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.DUSK_SET_GAUGE.getValue());
      packet.write(decrease);
      packet.writeInt((int)gauge);
      packet.writeInt((int)MAX_GAUGE);
      player.send(packet.getPacket());
   }

   public void sendDuskNotice(String message, int time) {
      this.broadcastMessage(CField.sendWeatherEffectNotice(250, time, false, message));
   }

   public void doStormAttack(int attackIndex) {
      MapleMonster target = this.getMonsterById(8644658);
      if (target == null) {
         target = this.getMonsterById(8644659);
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

   public void doTentaclesPattern() {
      int duration = 0;
      if (this.getId() == 450009450) {
         duration = this.doFieldSkill(100020, 3, Randomizer.rand(0, 3));
      } else {
         duration = this.doFieldSkill(100020, 1, Randomizer.rand(0, 3));
      }

      this.sendDuskNotice("방어하던 촉수로 강력한 공격을 할거예요! 버텨낸다면 드러난 공허의 눈을 공격할 수 있어요!", 3000);
      this.changeMobZone(0);
      this.nextLaserAttackTime = System.currentTimeMillis() + duration + 1000L;
   }

   public int doFieldSkill(int skillID, int skillLevel, int set) {
      return this.doFieldSkill(skillID, skillLevel, set, null);
   }

   public int doFieldSkill(int skillID, int skillLevel, int set, MapleCharacter player) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.FIELD_SKILL_RESULT.getValue());
      packet.writeInt(skillID);
      packet.writeInt(skillLevel);
      packet.writeInt(set == -1 ? 0 : set);
      FieldSkill fieldSkill = SkillFactory.getFieldSkill(skillID);
      AtomicInteger maxDuration = new AtomicInteger(0);
      if (fieldSkill != null) {
         FieldSkillEntry entry = fieldSkill.getFieldSkillEntry(skillLevel);
         if (entry != null) {
            List<AttackInfo> entrys = new ArrayList<>();
            if (set != -1) {
               entry.getAttackInfos().forEach((idx, attackInfo) -> {
                  if (attackInfo.getSet() == set) {
                     attackInfo.setPosition(attackInfo.getPos());
                     entrys.add(attackInfo);
                     if (attackInfo.getDuration() + attackInfo.getInterval() > maxDuration.get()) {
                        maxDuration.set(attackInfo.getDuration() + attackInfo.getInterval());
                     }
                  }
               });
            } else {
               entry.getAttackInfos().forEach((idx, attackInfo) -> {
                  attackInfo.setPosition(new Point(Randomizer.rand(-430, 460), -157));
                  if (attackInfo.getPosition().x >= 0) {
                     attackInfo.setLeft(true);
                  } else {
                     attackInfo.setLeft(false);
                  }

                  entrys.add(attackInfo);
                  if (attackInfo.getDuration() + attackInfo.getInterval() > maxDuration.get()) {
                     maxDuration.set(attackInfo.getDuration() + attackInfo.getInterval());
                  }
               });
            }

            if (!entrys.isEmpty()) {
               packet.writeInt(maxDuration.get());
               entrys.forEach(e -> {
                  packet.write(1);
                  e.encode(packet);
               });
               packet.write(0);
            } else {
               packet.writeInt(0);
            }

            if (player != null) {
               player.send(packet.getPacket());
            } else {
               this.broadcastMessage(packet.getPacket());
            }
         }
      }

      return maxDuration.get();
   }

   public boolean isSetLaserAttack() {
      return this.setLaserAttack;
   }

   public void setSetLaserAttack(boolean setLaserAttack) {
      this.setLaserAttack = setLaserAttack;
   }
}
