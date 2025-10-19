package objects.fields.child.papulatus;

import constants.QuestExConstants;
import database.DBConfig;
import database.DBConnection;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import logging.LoggingManager;
import logging.entry.BossLog;
import logging.entry.BossLogType;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import network.models.MobPacket;
import objects.fields.Field;
import objects.fields.gameobject.lifes.BossPapulatus;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobTemporaryStatEffect;
import objects.fields.gameobject.lifes.MobTemporaryStatFlag;
import objects.fields.gameobject.lifes.mobskills.MobSkill;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.fields.gameobject.lifes.mobskills.MobSkillID;
import objects.fields.gameobject.lifes.mobskills.MobSkillInfo;
import objects.fields.gameobject.lifes.mobskills.MobSkillStat;
import objects.fields.obstacle.ObstacleAtomCreateType;
import objects.fields.obstacle.ObstacleAtomCreatorOption;
import objects.item.Item;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStat;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Randomizer;
import objects.utils.Rect;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;
import scripting.EventInstanceManager;

public class Field_Papulatus extends Field {
   private int phase = 0;
   String difficulty = "";
   private int timerCycle = -1;
   private long nextAlarm = 0L;
   private boolean alarm = false;
   private int dynamicDecHPR = 0;
   private int dynamicDecInterval = 0;
   private int dynamicConsumeitemCooltime = 0;
   private int alarmTemplate = 0;
   private long endLaserTime = 0L;
   private long lastLaserTime = 0L;
   private boolean secondPhase = false;
   private boolean startPapulatusCrack = false;
   private boolean canPapulatusCrack = true;
   private MapleCharacter teleportUser = null;
   private Field_Papulatus.LaserData laser0 = null;
   private Field_Papulatus.LaserData laser1 = null;
   private Field_Papulatus.HPWheel hpWheel = null;
   private Map<Integer, Field_Papulatus.CraneData> cranes;

   public Field_Papulatus(int mapid, int channel, int returnMapId, float monsterRate, String difficulty, MapleData data) {
      super(mapid, channel, returnMapId, monsterRate);
      this.setMobGen(false, 8500003);
      this.setMobGen(false, 8500004);
      this.setMobGen(false, 8500006);
      this.setMobGen(false, 8500015);
      this.difficulty = difficulty;
      this.cranes = new HashMap<>();
      this.loadAdditional(data);
   }

   @Override
   public void resetFully(boolean respawn) {
      this.setMobGen(false, 8500003);
      this.setMobGen(false, 8500004);
      this.setMobGen(false, 8500006);
      this.setMobGen(false, 8500015);
      super.resetFully(false);
      this.phase = 0;
      this.nextAlarm = 0L;
      this.timerCycle = -1;
      this.hpWheel = null;
      this.alarm = false;
      this.dynamicDecHPR = 0;
      this.dynamicDecInterval = 0;
      this.dynamicConsumeitemCooltime = 0;
      this.alarmTemplate = 0;
      this.endLaserTime = 0L;
      this.lastLaserTime = 0L;
      this.laser0 = null;
      this.laser1 = null;
      this.secondPhase = false;
      this.startPapulatusCrack = false;
      this.canPapulatusCrack = true;

      for (Field_Papulatus.CraneData crane : this.cranes.values()) {
         crane.reset();
      }
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      this.setMobGen(false, 8500003);
      this.setMobGen(false, 8500004);
      this.setMobGen(false, 8500006);
      this.setMobGen(false, 8500015);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
      if (mob.getId() == 8500001 || mob.getId() == 8500011 || mob.getId() == 8500021) {
         mob.addSkillFilter(3);
         mob.addSkillFilter(4);
         mob.addSkillFilter(5);
      } else if (mob.getId() == 8500002 || mob.getId() == 8500012 || mob.getId() == 8500022) {
         mob.addSkillFilter(4);
         mob.addSkillFilter(5);
      }
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      super.onMobKilled(mob);
      if (mob.getId() == 8500007 || mob.getId() == 8500008) {
         int itemID = 2437606 + Randomizer.rand(0, 1);
         Point pos = new Point(0, mob.getTruePosition().y);
         Item drop = new Item(itemID, (short)0, (short)1, 0);
         this.dropItem(drop, pos, mob);
      } else if (mob.getId() != 8500003 && mob.getId() != 8500004) {
         if (mob.getId() == 8500002 || mob.getId() == 8500012 || mob.getId() == 8500022) {
            for (MapleMonster m : this.getAllMonstersThreadsafe()) {
               this.removeMonster(m, 1);
            }

            boolean set = false;

            for (MapleCharacter p : this.getCharactersThreadsafe()) {
               if (p.getBossMode() == 1) {
                  return;
               }

               if (p.getParty() != null && !set) {
                  String qexKey = "papulatus_clear";
                  if (this.difficulty.equals("chaos")) {
                     qexKey = "chaos_papulatus_clear";
                  }

                  int qexID = 1234569;
                  boolean multiMode = false;
                  if (!DBConfig.isGanglim) {
                     EventInstanceManager eim = p.getEventInstance();
                     if (eim != null) {
                        List<Integer> partyPlayerList = eim.getPartyPlayerList();
                        if (partyPlayerList != null && !partyPlayerList.isEmpty()) {
                           for (Integer playerID : partyPlayerList) {
                              if (multiMode) {
                                 break;
                              }

                              for (GameServer gs : GameServer.getAllInstances()) {
                                 if (multiMode) {
                                    break;
                                 }

                                 MapleCharacter player = gs.getPlayerStorage().getCharacterById(playerID);
                                 if (player != null) {
                                    multiMode = player.isMultiMode();
                                 }
                              }
                           }
                        }
                     }
                  }

                  EventInstanceManager eim = p.getEventInstance();
                  if (eim != null) {
                     List<Integer> partyPlayerList = eim.getPartyPlayerList();
                     int questId = (Integer)QuestExConstants.bossQuests.get(mob.getId());
                     if (partyPlayerList != null && !partyPlayerList.isEmpty()) {
                        for (Integer playerID : partyPlayerList) {
                           boolean find = false;

                           for (GameServer gs : GameServer.getAllInstances()) {
                              MapleCharacter player = gs.getPlayerStorage().getCharacterById(playerID);
                              if (player != null) {
                                 if (this.difficulty.equals("chaos") && player.getQuestStatus(2000010) == 1 && player.getOneInfo(2000010, "clear") == null) {
                                    player.updateOneInfo(2000010, "clear", "1");
                                 }

                                 if (!DBConfig.isGanglim) {
                                    player.updateOneInfo(qexID, qexKey, "1");
                                    if (multiMode) {
                                       player.updateOneInfo(
                                          qexID, qexKey + "_multi", String.valueOf(player.getOneInfoQuestInteger(qexID, qexKey + "_multi") + 1)
                                       );
                                    } else {
                                       player.updateOneInfo(
                                          qexID, qexKey + "_single", String.valueOf(player.getOneInfoQuestInteger(qexID, qexKey + "_single") + 1)
                                       );
                                    }
                                 }

                                 player.updateOneInfo(questId, "count", String.valueOf(player.getOneInfoQuestInteger(questId, "count") + 1));
                                 player.updateOneInfo(questId, "mobid", String.valueOf(mob.getId()));
                                 player.updateOneInfo(questId, "lasttime", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                                 player.updateOneInfo(questId, "mobDead", "1");
                                 String difficulty = "";
                                 if (mob.getId() == 8500002) {
                                    difficulty = "이지";
                                 } else if (mob.getId() == 8500012) {
                                    difficulty = "노말";
                                 } else if (mob.getId() == 8500022) {
                                    difficulty = "카오스";
                                 }

                                 StringBuilder sb = new StringBuilder("보스 " + difficulty + " 파풀라투스 격파");
                                 LoggingManager.putLog(new BossLog(player, BossLogType.ClearLog.getType(), sb));
                                 find = true;
                                 break;
                              }
                           }

                           if (!find) {
                              this.updateOfflineBossLimit(playerID, questId, "count", "1");
                              this.updateOfflineBossLimit(playerID, questId, "mobid", String.valueOf(mob.getId()));
                              this.updateOfflineBossLimit(playerID, questId, "lasttime", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                              this.updateOfflineBossLimit(playerID, questId, "mobDead", "1");
                              DBConnection db = new DBConnection();

                              try (Connection con = DBConnection.getConnection()) {
                                 PreparedStatement ps = con.prepareStatement("SELECT `customData` FROM questinfo WHERE characterid = ? and quest = ?");
                                 ps.setInt(1, playerID);
                                 ps.setInt(2, qexID);
                                 ResultSet rs = ps.executeQuery();
                                 boolean f = false;

                                 while (rs.next()) {
                                    f = true;
                                    String value = rs.getString("customData");
                                    String[] v = value.split(";");
                                    StringBuilder sb = new StringBuilder();
                                    int i = 1;
                                    boolean a = false;
                                    sb.append(qexKey);
                                    sb.append("=");
                                    sb.append("1");
                                    sb.append(";");

                                    for (String v_ : v) {
                                       String[] cd = v_.split("=");
                                       if (!cd[0].equals(qexKey)) {
                                          sb.append(cd[0]);
                                          sb.append("=");
                                          if (cd.length > 1) {
                                             sb.append(cd[1]);
                                          }

                                          if (v.length > i++) {
                                             sb.append(";");
                                          }
                                       } else {
                                          a = true;
                                       }
                                    }

                                    PreparedStatement ps2 = con.prepareStatement("UPDATE questinfo SET customData = ? WHERE characterid = ? and quest = ?");
                                    ps2.setString(1, sb.toString());
                                    ps2.setInt(2, playerID);
                                    ps2.setInt(3, qexID);
                                    ps2.executeUpdate();
                                    ps2.close();
                                 }

                                 if (!f) {
                                    PreparedStatement ps2 = con.prepareStatement(
                                       "INSERT INTO questinfo (characterid, quest, customData, date) VALUES (?, ?, ?, ?)"
                                    );
                                    ps2.setInt(1, playerID);
                                    ps2.setInt(2, qexID);
                                    ps2.setString(3, qexKey + "=1");
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                                    String time = sdf.format(Calendar.getInstance().getTime());
                                    ps2.setString(4, time);
                                    ps2.executeQuery();
                                    ps2.close();
                                 }

                                 rs.close();
                                 ps.close();
                              } catch (SQLException var31) {
                                 var31.printStackTrace();
                              }
                           }
                        }

                        set = true;
                     }
                  }
               }
            }
         }
      } else if (Randomizer.isSuccess(50)) {
         int itemID = 2437659 + Randomizer.rand(0, 5);
         Point pos = new Point(0, mob.getTruePosition().y);
         Item drop = new Item(itemID, (short)0, (short)1, 0);
         this.dropItem(drop, pos, mob);
      }
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
      super.onMobChangeHP(mob);
      if ((mob.getId() == 8500001 || mob.getId() == 8500011 || mob.getId() == 8500021) && !mob.containsSkillFilter(0)) {
         MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(241, 2);
         int hp = mobSkillInfo.getMobSkillStatsInt(MobSkillStat.hp);
         if (mob.getHPPercent() <= hp) {
            mob.addSkillFilter(0);
         }
      }
   }

   @Override
   public void onPlayerDead(MapleCharacter player) {
      super.onPlayerDead(player);

      for (Field_Papulatus.CraneData crane : new ArrayList<>(this.cranes.values())) {
         if (crane.userID == player.getId()) {
            this.unHoldCrane(crane);
         }
      }
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      MapleMonster boss = this.getBoss();
      long nextAlarm = this.nextAlarm;
      long now = System.currentTimeMillis();
      if (boss != null) {
         if (this.phase == 0) {
            this.beginBattle();
            return;
         }

         if (this.dynamicDecInterval != 0 && this.dynamicDecHPR != 0) {
            for (MapleCharacter player : this.getCharactersThreadsafe()) {
               if (!player.isInvincible()) {
                  int hp = (int)(player.getStat().getCurrentMaxHp(player) * 0.01) * this.dynamicDecHPR;
                  player.addHP(-hp);
               }
            }
         }

         if (nextAlarm != 0L && nextAlarm <= now) {
            if (!this.alarm) {
               this.startAlarmMode();
            } else {
               this.endAlarmMode();
            }
         }

         if (this.hpWheel == null) {
            this.checkStartLaser();
         } else if (this.hpWheel.endTime <= System.currentTimeMillis() || this.hpWheel.allPartsRemoved()) {
            this.endPapulatusCrack();
         }

         if (nextAlarm == 0L) {
            this.startPapulatusAlarmTimer();
         }

         this.onCheckCrane();
         if (boss.getId() % 10 == 2 && !this.secondPhase) {
            return;
         }

         int remain = (int)(nextAlarm - System.currentTimeMillis()) / 1000;
         if (remain > 0 && remain < 60 && !this.isStartPapulatusCrack() && this.isCanPapulatusCrack() && !this.alarm) {
            MobSkillInfo mobSkillInfo = MobSkillFactory.getMobSkill(241, 7);
            long ls = boss.getLastSkillUsed(mobSkillInfo);
            long coolTime = mobSkillInfo.getCoolTime();
            if (ls == 0L || System.currentTimeMillis() - ls >= coolTime) {
               int hp = mobSkillInfo.getMobSkillStatsInt(MobSkillStat.hp);
               if (boss.getHPPercent() <= hp) {
                  this.setStartPapulatusCrack(true);
                  this.setCanPapulatusCrack(false);
                  boss.setLastSkillUsed(mobSkillInfo, System.currentTimeMillis(), mobSkillInfo.getCoolTime());
               }
            }
         }
      } else if (this.phase > 0) {
         this.phase = 0;
         this.endAlarmMode();
         this.endLaser();
         this.endPapulatusCrack();
      } else {
         for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
            if (mob.getId() == 8500003 || mob.getId() == 8500004) {
               this.removeMonster(mob, 1);
            }
         }
      }
   }

   public void dropItem(Item item, Point pos, MapleMonster mob) {
      pos.x = mob.getTruePosition().x + Randomizer.rand(-25, 25);
      this.spawnMobDrop(item, this.calcDropPos(pos, mob.getTruePosition()), mob, mob.getController(), (byte)0, 0, 0L, true, false);
   }

   public void applyTimeCurse(MapleMonster mob, int skillLevel, int duration) {
      if (mob.getBuff(MobTemporaryStatFlag.TIME_CURSE) == null) {
         MobSkillInfo msi = MobSkillFactory.getMobSkill(241, skillLevel);
         MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.TIME_CURSE, 1, 241, msi, true);
         e.setDuration(duration * 1000);
         e.setCancelTask(duration * 1000);
         mob.setTimeCurseX(duration * 1000);
         mob.setTimeCurseLevel(skillLevel);
         mob.applyStatus(e);
         int min = msi.getMobSkillStatsInt(MobSkillStat.userTimeMin);
         int max = msi.getMobSkillStatsInt(MobSkillStat.userTimeMax);

         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            int time = Randomizer.rand(min, max);
            player.getSecondaryStat().TimeCurseX = time * 1000;
            player.giveDebuff(SecondaryStatFlag.TimeCurse, 1, 0, time * 1000, 241, 3, true);
         }
      }
   }

   public void applyTimeCurseByUser(MapleCharacter player, int level) {
      MapleMonster boss = this.getBoss();
      if (boss != null) {
         MobTemporaryStatEffect effect = boss.getBuff(MobTemporaryStatFlag.TIME_CURSE);
         if (effect != null) {
            int remain = (int)(effect.getCancelTask() - System.currentTimeMillis());
            if (remain > 0) {
               if (player.getBuffedValue(SecondaryStatFlag.NotDamaged) == null
                  && player.getIndieTemporaryStats(SecondaryStatFlag.indiePartialNotDamaged).size() <= 0
                  && player.getBuffedValue(SecondaryStatFlag.DarkSight) == null) {
                  if (boss.getTimeCurseLevel() == 1) {
                     player.giveDebuff(SecondaryStatFlag.Stun, 1, 0, remain, 241, 1, false);
                  } else if (boss.getTimeCurseLevel() == 2) {
                     player.giveDebuff(SecondaryStatFlag.Seal, 1, 0, remain, 241, 2, false);
                  }
               }
            }
         }
      }
   }

   public void applyTimeCurseByMob(int level) {
      for (MapleCharacter player : this.getCharactersThreadsafe()) {
         this.applyTimeCurseByMob(player, level);
      }
   }

   private void applyTimeCurseByMob(MapleCharacter player, int level) {
      int remain = (int)(player.getSecondaryStat().TimeCurseTill - System.currentTimeMillis());
      if (remain > 0) {
         if (player.getBuffedValue(SecondaryStatFlag.NotDamaged) == null
            && player.getIndieTemporaryStats(SecondaryStatFlag.indiePartialNotDamaged).size() <= 0
            && player.getBuffedValue(SecondaryStatFlag.DarkSight) == null) {
            player.dispelDebuff(SecondaryStatFlag.TimeCurse);
            if (level == 1) {
               player.giveDebuff(SecondaryStatFlag.Stun, 1, 0, remain, 241, 1, false);
            } else if (level == 2) {
               player.giveDebuff(SecondaryStatFlag.Seal, 1, 0, remain, 241, 2, false);
            }
         }
      }
   }

   public boolean avaliableAlarm() {
      return this.hpWheel == null;
   }

   public void beginBattle() {
      this.phase = 1;
      this.lastLaserTime = System.currentTimeMillis();
      this.initCrane();
   }

   public void initCrane() {
      this.broadcastMessage(this.makeInitCrane());
   }

   public void onCheckCrane() {
      for (Field_Papulatus.CraneData crane : this.cranes.values()) {
         if (crane.mode == 5 && crane.endHoldTime <= System.currentTimeMillis()) {
            this.unHoldCrane(crane);
         }
      }
   }

   public void unHoldCrane(Field_Papulatus.CraneData crane) {
      MapleCharacter target = this.getCharacterById(crane.userID);
      if (target != null) {
         SecondaryStat ss = target.getSecondaryStat();
         if (ss.StunValue == -99999 || ss.StunValue == 0) {
            return;
         }

         int stunReason = ss.StunReason;
         int level = ss.StunLevel;
         if (stunReason != 241 || level != 8) {
            return;
         }

         target.temporaryStatReset(SecondaryStatFlag.Stun);
      }

      crane.mode = 6;
      crane.userID = 0;
      crane.endHoldTime = 0L;
      this.broadcastMessage(this.makeChangeCrane(crane));
   }

   public void onTimeTorrent(MapleCharacter player) {
      Point basePoint = player.getTruePosition();
      int skillID = 241;
      int skillLevel = 5;
      int trueDamR = this.difficulty.equals("easy") ? 50 : 80;
      MobSkillInfo msi = MobSkillFactory.getMobSkill(skillID, skillLevel);
      if (msi != null) {
         MapleMonster boss = this.getBoss();
         if (boss != null) {
            Point lt = msi.getLt();
            Point rb = msi.getRb();
            List<MapleCharacter> players = this.getPlayerInRect(basePoint, lt.x, lt.y, rb.x, rb.y);
            players.stream()
               .filter(p -> !p.isHidden())
               .forEach(p -> p.onDamageByMobSkill(MobSkillID.getMobSkillIDByValue(skillID), skillLevel, msi, 0, boss.getId(), players.size(), trueDamR));
         }
      }
   }

   public void endAlarmMode() {
      MapleMonster boss = this.getBoss();
      if (boss != null) {
         boss.cancelStatus(MobTemporaryStatFlag.ALARM_MODE);
      }

      this.alarm = false;
      this.dynamicDecHPR = 0;
      this.dynamicDecInterval = 0;
      this.nextAlarm = 0L;
      this.setConsumeItemCooltime(0);
      this.sendAlarmBGM(this.alarmTemplate, false);
      this.sendClearPapulatusAlarmTimer();
   }

   public void startAlarmMode() {
      MapleMonster boss = this.getBoss();
      if (boss != null) {
         MobSkillInfo msi = MobSkillFactory.getMobSkill(241, 6);
         int duration = msi.getMobSkillStatsInt(MobSkillStat.time);
         int subTime = msi.getMobSkillStatsInt(MobSkillStat.subTime);
         int fixDamR = msi.getMobSkillStatsInt(MobSkillStat.fixDamR);
         int consumeItemCooltime = msi.getMobSkillStatsInt(MobSkillStat.consumeItemCoolTime);
         MobTemporaryStatEffect e = new MobTemporaryStatEffect(MobTemporaryStatFlag.ALARM_MODE, 1, 241, msi, true);
         e.setDuration(duration * 1000);
         boss.applyStatus(e);
         this.dynamicDecInterval = subTime;
         this.dynamicDecHPR = fixDamR;
         this.setConsumeItemCooltime(consumeItemCooltime);
         this.alarm = true;
         this.nextAlarm = System.currentTimeMillis() + duration * 1000;

         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            this.sendPapulatusAlarmTimer(player);
         }

         this.broadcastMessage(MobPacket.talkMonster(boss.getObjectId(), 1));
         this.broadcastMessage(MobPacket.mobForcedSkillAction(boss.getObjectId(), 4, false));
         this.sendAlarmBGM(boss.getId(), true);
         this.clearTickTockLaser();
         Rect mBR = this.calculateMBR();
         this.registerObstacleAtom(
            ObstacleAtomCreateType.NORMAL,
            60,
            4,
            7,
            duration * 1000,
            4000,
            new Consumer[]{
               ObstacleAtomCreatorOption.SetCreateDelay(200, 800),
               ObstacleAtomCreatorOption.SetMaxP(1, 3),
               ObstacleAtomCreatorOption.SetVperSec(110, 150),
               ObstacleAtomCreatorOption.SetHitBoxRange(25),
               ObstacleAtomCreatorOption.SetTrueDamR(5),
               ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -500, 179, this)
            }
         );
         this.registerObstacleAtom(
            ObstacleAtomCreateType.NORMAL,
            61,
            3,
            7,
            duration * 1000,
            5000,
            new Consumer[]{
               ObstacleAtomCreatorOption.SetCreateDelay(200, 800),
               ObstacleAtomCreatorOption.SetMaxP(1, 3),
               ObstacleAtomCreatorOption.SetVperSec(110, 150),
               ObstacleAtomCreatorOption.SetHitBoxRange(25),
               ObstacleAtomCreatorOption.SetTrueDamR(5),
               ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -500, 179, this)
            }
         );
      }
   }

   public void endPapulatusCrack() {
      if (this.hpWheel != null) {
         AtomicInteger totalPercent = new AtomicInteger(0);
         this.hpWheel
            .parts
            .stream()
            .filter(p -> !p.removed)
            .forEach(part -> totalPercent.set(totalPercent.get() + BossPapulatus.HealMissionData.hp.get(part.type)));
         MapleMonster boss = this.getBoss();
         if (boss != null) {
            this.broadcastMessage(CField.startMapEffect("파풀라투스가 차원 이동을 통해서 HP를 회복하려고 시도합니다.", 5120177, true, 4));
            long hp = boss.getHp();
            long heal = boss.getMobMaxHp() * (Math.min(100, totalPercent.get()) / 100);
            long after = Math.min(boss.getMobMaxHp(), heal + hp);
            long diff = after - hp;
            if (diff > 0L) {
               boss.setHp(after);
               this.broadcastMessage(MobPacket.showBossHP(boss.getId(), after, boss.getMobMaxHp()));
            }

            boss.cancelStatus(MobTemporaryStatFlag.CASTING);
         }

         this.setCrackReactorState(1);
         this.setPortalReactorState(0);
         this.setMobGen(false, 8500003);
         this.setMobGen(false, 8500004);

         for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
            if (mob.getId() == 8500003 || mob.getId() == 8500004) {
               this.removeMonster(mob, 1);
            }
         }

         this.clearWheel();
         this.hpWheel = null;
         this.canPapulatusCrack = true;
         this.startPapulatusCrack = false;
      }
   }

   public void startPapulatusCrack(int castingTime, int hour, int minute) {
      if (this.hpWheel == null) {
         this.endLaser();
         this.endAlarmMode();
         this.hpWheel = new Field_Papulatus.HPWheel(castingTime, hour, minute);
         BossPapulatus.HealMissionData.DifficultyData data = this.getHealMissionData();
         data.setCount.entrySet().forEach(entry -> {
            for (int i = 0; i < entry.getValue(); i++) {
               this.hpWheel.parts.add(new Field_Papulatus.HPWheel.Part(entry.getKey()));
            }
         });

         while (this.hpWheel.parts.size() < 6) {
            this.hpWheel.parts.add(new Field_Papulatus.HPWheel.Part(Randomizer.rand(data.hpMin, data.hpMax)));
         }

         for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
            MapleMonster boss = this.getBoss();
            if (mob.getId() != boss.getId()) {
               this.removeMonster(mob, 1);
            }
         }

         this.sendPapulatusCrack_Start();
         this.setMobGen(true, 8500003);
         this.setMobGen(true, 8500004);
         this.broadcastMessage(CField.startMapEffect("파풀라투스가 시간 이동을 할 수 없도록 차원의 균열을 봉인해야 합니다.", 5120177, true, 4));
         this.setCrackReactorState(0);
         this.setPortalReactorState(1);
      }
   }

   public void tryRemoveWheel() {
      if (this.hpWheel != null) {
         int idx = this.hpWheel.currentIdx();
         Field_Papulatus.HPWheel.Part part = this.hpWheel.parts.get(idx);
         if (!part.removed) {
            part.removed = true;
            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.PAPULATUS_DIMENSION_CRACK.getValue());
            packet.write(2);
            this.hpWheel.encode(packet);
            this.broadcastMessage(packet.getPacket());
            this.broadcastMessage(CField.startMapEffect("차원의 균열을 봉인했습니다.", 5120177, true, 4));
            this.setReactorState("crack" + idx, (byte)1);
         }
      }
   }

   public void addWheelTime(int hour, int minute) {
      if (this.hpWheel != null) {
         if (hour > 0) {
            this.hpWheel.addHour(hour);
         }

         if (minute > 0) {
            this.hpWheel.addMinute(minute);
         }

         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.PAPULATUS_DIMENSION_CRACK.getValue());
         packet.write(1);
         packet.writeInt(hour);
         packet.writeInt(minute);
         this.broadcastMessage(packet.getPacket());
         this.broadcastMessage(CWvsContext.serverNotice(5, "파풀라투스의 시계가 움직입니다."));
         this.broadcastMessage(CField.startMapEffect("파풀라투스의 시계가 움직입니다. 차원의 포탈을 통해 시간을 봉인하세요.", 5120177, true, 4));
      }
   }

   public void setCrackReactorState(int state) {
      for (int i = 1; i <= 6; i++) {
         this.setReactorState("crack" + i, (byte)state);
      }
   }

   public void setPortalReactorState(int state) {
      this.setReactorState("portal", (byte)state);
   }

   public void startLaser(int index) {
      BossPapulatus.TickTockLaserData.PairInfoData pairInfo = BossPapulatus.TickTockLaserData.pairInfo.get(index);
      this.laser0 = new Field_Papulatus.LaserData(pairInfo.obj0);
      this.laser1 = new Field_Papulatus.LaserData(pairInfo.obj1);
      this.endLaserTime = System.currentTimeMillis() + pairInfo.duration;
      this.sendLaserBegin();
   }

   public void endLaser() {
      this.laser0 = null;
      this.laser1 = null;
      this.lastLaserTime = System.currentTimeMillis();
      this.endLaserTime = 0L;
      this.sendLaserEnd();
   }

   public void setConsumeItemCooltime(int sec) {
      this.dynamicConsumeitemCooltime = sec;
      this.setConsumeItemCoolTime(sec);
      this.broadcastMessage(CField.consumeItemCooltime(sec));
   }

   public boolean checkAlarm(int time) {
      return this.timerCycle == 0 ? false : this.getAlarmRemainingTime() <= System.currentTimeMillis();
   }

   public void checkStartLaser() {
      BossPapulatus.TickTockLaserData.PhaseInfoData phaseInfo = BossPapulatus.TickTockLaserData.phaseInfo.get(this.getPhaseForData());
      if (this.endLaserTime != 0L && this.endLaserTime <= System.currentTimeMillis()) {
         this.endLaser();
      }

      if (this.endLaserTime == 0L && phaseInfo != null && this.lastLaserTime + phaseInfo.cooltime <= System.currentTimeMillis()) {
         List<Integer> list = new ArrayList<>();

         for (int i = 0; i < 4; i++) {
            this.addPairSet(phaseInfo, list, i);
         }

         if (!list.isEmpty()) {
            Collections.shuffle(list);
            this.startLaser(list.stream().findAny().get());
         }
      }
   }

   public void addPairSet(BossPapulatus.TickTockLaserData.PhaseInfoData infoData, List<Integer> list, int value) {
      if (infoData.pair == -1 || infoData.pair == value) {
         list.add(value);
      }
   }

   public int getAlarmRemainingTime() {
      return (int)(this.nextAlarm - System.currentTimeMillis());
   }

   public void startPapulatusAlarmTimer() {
      this.timerCycle++;
      this.nextAlarm = System.currentTimeMillis() + 120000L;

      for (MapleCharacter player : this.getCharactersThreadsafe()) {
         this.sendPapulatusAlarmTimer(player);
      }
   }

   public void sendPapulatusAlarmTimer(MapleCharacter player) {
      if (this.nextAlarm > System.currentTimeMillis()) {
         player.send(this.makePapulatusAlarmTimer());
      }
   }

   public void sendPapulatusCrack_Start() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PAPULATUS_DIMENSION_CRACK.getValue());
      packet.write(0);
      int timeSpan = (int)(this.hpWheel.endTime - System.currentTimeMillis());
      packet.writeInt(timeSpan / 1000);
      packet.writeInt(this.hpWheel.hour);
      packet.writeInt(this.hpWheel.minute);
      this.hpWheel.encode(packet);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendPapulatusTeleport(MapleCharacter player, int pt) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PAPULATUS_USER_TELEPORT.getValue());
      packet.writeInt(MobSkillID.PAPULATUS_SKILL.getVal());
      packet.writeInt(4);
      packet.writeInt(pt);
      player.send(packet.getPacket());
      int skillID = 241;
      int skillLevel = 4;
      packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_ON_EFFECT.getValue());
      packet.write(73);
      packet.write(1);
      packet.writeInt(skillID);
      packet.writeInt(skillLevel);
      player.send(packet.getPacket());
      packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.USER_ON_EFFECT_REMOTE.getValue());
      packet.writeInt(player.getId());
      packet.write(73);
      packet.write(1);
      packet.writeInt(skillID);
      packet.writeInt(skillLevel);
      this.broadcastMessage(player, packet.getPacket(), false);
   }

   public byte[] makePapulatusAlarmTimer() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PAPULATUS_DO_SKILL.getValue());
      packet.writeInt(Field_Papulatus.PapulatusSkillType.ticktockAlarm.type);
      packet.write(true);
      packet.write(this.alarm);
      packet.writeInt(this.getAlarmRemainingTime());
      return packet.getPacket();
   }

   public byte[] makeInitCrane() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PAPULATUS_DO_SKILL.getValue());
      packet.writeInt(Field_Papulatus.PapulatusSkillType.ticktockCrane.type);
      packet.writeInt(this.cranes.size());

      for (Field_Papulatus.CraneData crane : this.cranes.values()) {
         crane.encode(packet);
      }

      return packet.getPacket();
   }

   public byte[] clearTickTockLaser() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PAPULATUS_DO_SKILL.getValue());
      packet.writeInt(Field_Papulatus.PapulatusSkillType.ticktockLaser.type);
      packet.writeInt(0);
      return packet.getPacket();
   }

   public void sendAlarmBGM(int templateID, boolean onOff) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PAPULATUS_DO_SKILL.getValue());
      packet.writeInt(Field_Papulatus.PapulatusSkillType.ticktockAlarmBgm.type);
      packet.writeInt(templateID);
      packet.write(onOff);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendClearPapulatusAlarmTimer() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PAPULATUS_DO_SKILL.getValue());
      packet.writeInt(Field_Papulatus.PapulatusSkillType.ticktockAlarm.type);
      packet.write(false);
      packet.write(false);
      packet.writeInt(0);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLaserBegin() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PAPULATUS_DO_SKILL.getValue());
      packet.writeInt(Field_Papulatus.PapulatusSkillType.ticktockLaser.type);
      packet.writeInt(2);
      this.laser0.encode(packet);
      this.laser1.encode(packet);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLaserEnd() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PAPULATUS_DO_SKILL.getValue());
      packet.writeInt(Field_Papulatus.PapulatusSkillType.ticktockLaser.type);
      packet.writeInt(0);
      this.broadcastMessage(packet.getPacket());
   }

   public void sendLaserCollisionSound() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PAPULATUS_DO_SKILL.getValue());
      packet.writeInt(Field_Papulatus.PapulatusSkillType.ticktockLaserCollision.type);
      packet.writeInt(1);
      this.broadcastMessage(packet.getPacket());
   }

   public byte[] makeChangeCrane(Field_Papulatus.CraneData crane) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PAPULATUS_DO_SKILL.getValue());
      packet.writeInt(Field_Papulatus.PapulatusSkillType.ticktockCrane.type);
      packet.writeInt(1);
      crane.encode(packet);
      return packet.getPacket();
   }

   public void clearWheel() {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.PAPULATUS_DIMENSION_CRACK.getValue());
      packet.write(3);
      this.broadcastMessage(packet.getPacket());
   }

   public void loadAdditional(MapleData data) {
      for (int i = 0; i <= 7; i++) {
         MapleData layer = data.getChildByPath(String.valueOf(i));
         if (layer != null) {
            MapleData obj = layer.getChildByPath("obj");
            if (obj != null) {
               for (MapleData o : obj) {
                  String tags = MapleDataTool.getString("tags", o, null);
                  String l0 = MapleDataTool.getString("l0", o, null);
                  if (tags != null && l0 != null && l0.equals("ticktockCrane")) {
                     BossPapulatus.TickTockCraneData.InfoData craneInfo = BossPapulatus.TickTockCraneData.infoByTag.get(tags);
                     this.cranes.put(craneInfo.idx, new Field_Papulatus.CraneData(craneInfo.idx));
                  }
               }
            }
         }
      }
   }

   public BossPapulatus.HealMissionData.DifficultyData getHealMissionData() {
      if (!this.difficulty.isEmpty()) {
         if (this.difficulty.equals("easy")) {
            return BossPapulatus.HealMissionData.easy;
         }

         if (this.difficulty.equals("normal")) {
            return BossPapulatus.HealMissionData.normal;
         }

         if (this.difficulty.equals("chaos")) {
            return BossPapulatus.HealMissionData.chaos;
         }
      }

      return null;
   }

   public int getPhaseForData() {
      if (this.phase > 0) {
         MapleMonster boss = this.getBoss();
         if (boss != null) {
            int sub = 0;
            int templateID = boss.getId() % 10;
            if (templateID != 1) {
               if (templateID == 2) {
                  sub = 5;
               }
            } else if (boss.getHPPercent() >= 80) {
               sub = 1;
            } else if (boss.getHPPercent() >= 60) {
               sub = 2;
            } else if (boss.getHPPercent() < 40) {
               sub = boss.getHPPercent() < 20 ? 5 : 4;
            } else {
               sub = 3;
            }

            if (!this.difficulty.isEmpty()) {
               if (this.difficulty.equals("normal")) {
                  sub += 10;
               } else if (this.difficulty.equals("chaos")) {
                  sub += 20;
               }
            }

            return sub;
         }
      }

      return 0;
   }

   public void onHoldByCrane(MapleCharacter player, PacketDecoder packet) {
      int idx = packet.readInt();
      int y = packet.readInt();
      Field_Papulatus.CraneData crane = this.cranes.get(idx);
      if (crane != null && crane.mode != 5) {
         crane.mode = 5;
         crane.y = y;
         crane.userID = player.getId();
         crane.endHoldTime = System.currentTimeMillis() + 4000L;
         this.broadcastMessage(this.makeChangeCrane(crane));
         player.giveDebuff(SecondaryStatFlag.Stun, 1, 0, 4000L, 241, 8);
      }
   }

   public void onRelaseHoldCrane(MapleCharacter player, PacketDecoder packet) {
      int idx = packet.readInt();
      Field_Papulatus.CraneData crane = this.cranes.get(idx);
      if (crane != null && crane.userID == player.getId()) {
         this.unHoldCrane(crane);
      }
   }

   public void onLaserCollision() {
      if (this.laser0 != null && this.laser1 != null) {
         MapleMonster boss = this.getBoss();
         if (boss != null) {
            this.broadcastMessage(MobPacket.mobForcedSkillAction(boss.getObjectId(), 0, false));
            MobSkillInfo msi = MobSkillFactory.getMobSkill(176, 41);
            MobSkill mobSkill = MapleLifeFactory.getRealMobSkill(176, 41);

            for (MapleCharacter player : this.getCharactersThreadsafe()) {
               msi.applyEffect(player, boss, mobSkill, false, false, new Point(0, 0));
            }

            this.broadcastMessage(CField.makeEffectScreen("Skill/MobSkill/176.img/level/41/screen"));
            this.sendLaserCollisionSound();
            this.endLaser();
         }
      }
   }

   public MapleMonster getBoss() {
      int[] mobID = new int[]{8500001, 8500002, 8500011, 8500012, 8500021, 8500022};

      for (int id : mobID) {
         MapleMonster mob = this.getMonsterById(id);
         if (mob != null) {
            return mob;
         }
      }

      return null;
   }

   public boolean isStartPapulatusCrack() {
      return this.startPapulatusCrack;
   }

   public void setStartPapulatusCrack(boolean startPapulatusCrack) {
      this.startPapulatusCrack = startPapulatusCrack;
   }

   public boolean isCanPapulatusCrack() {
      return this.canPapulatusCrack;
   }

   public void setCanPapulatusCrack(boolean canPapulatusCrack) {
      this.canPapulatusCrack = canPapulatusCrack;
   }

   public MapleCharacter getTeleportUser() {
      return this.teleportUser;
   }

   public void setTeleportUser(MapleCharacter teleportUser) {
      this.teleportUser = teleportUser;
   }

   public static class CraneData {
      public long endHoldTime;
      public int mode;
      public int objIdx;
      public int userID;
      public int y;

      public CraneData(int objIdx) {
         this.objIdx = objIdx;
         this.reset();
      }

      public void encode(PacketEncoder packet) {
         packet.writeInt(this.objIdx);
         packet.writeInt(this.mode);
         packet.writeInt(this.y);
         packet.writeInt(this.userID);
      }

      public void reset() {
         this.mode = 1;
         this.y = 0;
         this.userID = 0;
      }
   }

   public static class HPWheel {
      public long endTime;
      public int hour;
      public int minute;
      public List<Field_Papulatus.HPWheel.Part> parts;

      public HPWheel(int timeout, int hour, int minute) {
         this.endTime = System.currentTimeMillis() + timeout * 1000;
         this.hour = hour;
         this.minute = minute;
         this.parts = new ArrayList<>();
      }

      public boolean allPartsRemoved() {
         boolean ret = true;

         for (Field_Papulatus.HPWheel.Part part : this.parts) {
            if (!part.removed) {
               ret = false;
            }
         }

         return ret;
      }

      public int currentIdx() {
         return this.hour / 2;
      }

      public void addHour(int delta) {
         this.hour = (this.hour + delta) % 12;
      }

      public void addMinute(int delta) {
         int m = this.minute + delta;
         if (m >= 60) {
            this.addHour(m / 60);
         }

         this.minute = m % 60;
      }

      public void encode(PacketEncoder packet) {
         packet.writeInt(this.parts.size());

         for (Field_Papulatus.HPWheel.Part part : this.parts) {
            part.encode(packet);
         }
      }

      public static class Part {
         public boolean removed;
         public int type;

         public Part(int type) {
            this.type = type;
            this.removed = false;
         }

         public void encode(PacketEncoder packet) {
            packet.writeInt(this.type);
            packet.write(this.removed);
         }
      }
   }

   public static class LaserData {
      public double angleBase;
      public double anglePerSec;
      public int objIdx;

      public LaserData(int objIdx) {
         this.objIdx = objIdx;
         this.angleBase = BossPapulatus.TickTockLaserData.objInfo.get(objIdx).angleBase;
         this.anglePerSec = BossPapulatus.TickTockLaserData.objInfo.get(objIdx).anglePerSec;
      }

      public void encode(PacketEncoder packet) {
         packet.writeInt(this.objIdx);
         packet.write(true);
         packet.writeDouble(this.angleBase);
         packet.writeDouble(this.anglePerSec);
      }
   }

   public static enum PapulatusSkillType {
      ticktockLaser(0),
      ticktockLaserCollision(1),
      ticktockCrane(2),
      ticktockAlarm(4),
      ticktockAlarmBgm(5);

      private int type;

      private PapulatusSkillType(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }
   }
}
