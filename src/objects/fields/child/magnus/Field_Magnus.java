package objects.fields.child.magnus;

import constants.QuestExConstants;
import database.DBConfig;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import network.game.GameServer;
import network.models.CField;
import objects.effect.child.HPHeal;
import objects.fields.Field;
import objects.fields.SmartMobMsgType;
import objects.fields.SmartMobNoticeType;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MobZoneInfo;
import objects.fields.obstacle.ObstacleAtomCreateType;
import objects.fields.obstacle.ObstacleAtomCreatorOption;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Pair;
import objects.utils.Rect;
import objects.wz.provider.MapleData;
import objects.wz.provider.MapleDataTool;
import scripting.EventInstanceManager;

public class Field_Magnus extends Field {
   private boolean firstSet = false;
   private boolean activeDecHPr = false;
   private int decHPr = 0;
   private int decInterval = 0;
   private final int HardMagnus = 8880000;
   private final int NomarlMagnus = 8880002;
   private final int EasyMagnus = 8880010;

   public Field_Magnus(int mapid, int channel, int returnMapId, float monsterRate, MapleData mapData) {
      super(mapid, channel, returnMapId, monsterRate);
      this.decHPr = MapleDataTool.getInt(mapData.getChildByPath("info/decHPr"), 0);
      this.decInterval = MapleDataTool.getInt(mapData.getChildByPath("info/decInterval"), 0);
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(false);
      this.firstSet = false;
      this.activeDecHPr = true;
      this.clearObstacleAtomCreators();
      this.changeMobZone(0);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      super.onMobKilled(mob);
      List<Integer> mobs = new ArrayList<>(Arrays.asList(8880000, 8880002, 8880010));
      if (mobs.contains(mob.getId())) {
         boolean set = false;

         for (MapleMonster m : this.getAllMonstersThreadsafe()) {
            if (!mobs.contains(m.getId())) {
               this.removeMonster(m, 1);
            }
         }

         this.clearObstacleAtomCreators();
         this.changeMobZone(0);
         this.setActiveDebuffObjs(false);
         this.activeDecHPr = false;
         this.sendSmartMobNotice(SmartMobNoticeType.Normal, mob.getId(), SmartMobMsgType.Field, 6666, "매그너스가 사망하여 방출된 에너지로 인해 더이상 구와르의 힘에 영향을 받지 않습니다.");
         this.broadcastMessage(CField.clearObstacle());

         for (MapleCharacter p : this.getCharactersThreadsafe()) {
            if (p.getParty() != null) {
               p.addGuildContributionByBoss(mob.getId());
               if (!set) {
                  String qexKey = "magnus_clear";
                  int bossQuest = QuestExConstants.Magnus.getQuestID();
                  if (mob.getId() == 8880000) {
                     qexKey = "hard_magnus_clear";
                     bossQuest = QuestExConstants.HardMagnus.getQuestID();
                  }

                  int qexID = 1234569;
                  int questId = (Integer)QuestExConstants.bossQuests.get(mob.getId());
                  List<Pair<Integer, String>> qex = new ArrayList<>(Arrays.asList(new Pair<>(qexID, qexKey), new Pair<>(bossQuest, "eNum")));
                  EventInstanceManager eim = p.getEventInstance();
                  if (eim != null) {
                     eim.restartEventTimer(30000L);
                     List<Integer> partyPlayerList = eim.getPartyPlayerList();
                     boolean multiMode = false;
                     if (!DBConfig.isGanglim && partyPlayerList != null && !partyPlayerList.isEmpty()) {
                        for (Integer playerID : partyPlayerList) {
                           for (GameServer gs : GameServer.getAllInstances()) {
                              MapleCharacter player = gs.getPlayerStorage().getCharacterById(playerID);
                              if (player != null && player.isMultiMode()) {
                                 multiMode = true;
                                 break;
                              }
                           }
                        }
                     }

                     if (partyPlayerList != null && !partyPlayerList.isEmpty()) {
                        for (Integer playerID : partyPlayerList) {
                           boolean find = false;

                           for (GameServer gsx : GameServer.getAllInstances()) {
                              MapleCharacter player = gsx.getPlayerStorage().getCharacterById(playerID);
                              if (player != null) {
                                 player.updateOneInfo(qexID, qexKey, "1");
                                 if (!DBConfig.isGanglim) {
                                    player.updateOneInfo(bossQuest, "eNum", "1");
                                    if (multiMode) {
                                       player.updateOneInfo(bossQuest, "eNum_multi", String.valueOf(player.getOneInfoQuestInteger(bossQuest, "eNum_multi") + 1));
                                    } else {
                                       player.updateOneInfo(
                                          bossQuest, "eNum_single", String.valueOf(player.getOneInfoQuestInteger(bossQuest, "eNum_single") + 1)
                                       );
                                    }
                                 }

                                 player.updateOneInfo(questId, "count", String.valueOf(player.getOneInfoQuestInteger(questId, "count") + 1));
                                 player.updateOneInfo(questId, "mobid", String.valueOf(mob.getId()));
                                 player.updateOneInfo(questId, "lasttime", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                                 player.updateOneInfo(questId, "mobDead", "1");
                                 find = true;
                                 break;
                              }
                           }

                           if (!find) {
                              this.updateOfflineBossLimit(playerID, questId, "count", "1");
                              this.updateOfflineBossLimit(playerID, questId, "mobid", String.valueOf(mob.getId()));
                              this.updateOfflineBossLimit(playerID, questId, "lasttime", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                              this.updateOfflineBossLimit(playerID, questId, "mobDead", "1");

                              for (int count = 0; count < qex.size(); count++) {
                                 DBConnection db = new DBConnection();

                                 try (Connection con = DBConnection.getConnection()) {
                                    PreparedStatement ps = con.prepareStatement("SELECT `customData` FROM questinfo WHERE characterid = ? and quest = ?");
                                    ps.setInt(1, playerID);
                                    ps.setInt(2, (Integer)qex.get(count).left);
                                    ResultSet rs = ps.executeQuery();
                                    boolean f = false;

                                    while (rs.next()) {
                                       f = true;
                                       String value = rs.getString("customData");
                                       String[] v = value.split(";");
                                       StringBuilder sb = new StringBuilder();
                                       int i = 1;
                                       boolean a = false;
                                       sb.append((String)qex.get(count).right);
                                       sb.append("=");
                                       sb.append("1");
                                       sb.append(";");

                                       for (String v_ : v) {
                                          String[] cd = v_.split("=");
                                          if (!cd[0].equals(qex.get(count).right)) {
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
                                       ps2.setInt(3, (Integer)qex.get(count).left);
                                       ps2.executeUpdate();
                                       ps2.close();
                                    }

                                    if (!f) {
                                       PreparedStatement ps2 = con.prepareStatement(
                                          "INSERT INTO questinfo (characterid, quest, customData, date) VALUES (?, ?, ?, ?)"
                                       );
                                       ps2.setInt(1, playerID);
                                       ps2.setInt(2, (Integer)qex.get(count).left);
                                       ps2.setString(3, (String)qex.get(count).right + "=1");
                                       SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                                       String time = sdf.format(Calendar.getInstance().getTime());
                                       ps2.setString(4, time);
                                       ps2.executeQuery();
                                       ps2.close();
                                    }

                                    rs.close();
                                    ps.close();
                                 } catch (SQLException var35) {
                                    var35.printStackTrace();
                                 }
                              }
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

   private MapleMonster findBoss() {
      List<Integer> mobs = new ArrayList<>(Arrays.asList(8880000, 8880002, 8880010));

      for (MapleMonster mob : this.getAllMonstersThreadsafe()) {
         if (mobs.contains(mob.getId())) {
            return mob;
         }
      }

      return null;
   }

   public void changeMobZone(int zone) {
      MapleMonster boss = this.findBoss();
      if (boss != null) {
         if (zone != 0) {
            this.mobZoneMobs.add(boss);
            boss.setMobZoneDataType(zone);
            boss.setPhase(zone);
            MobZoneInfo zoneInfo = MobZoneInfo.getInfo(boss.getId());
            if (zoneInfo != null) {
               this.currentMobZone = zoneInfo;
            }

            this.broadcastMobPhaseChange(boss.getObjectId(), zone, 0);
            this.broadcastMobZoneChange(boss.getObjectId(), zone);
         } else {
            this.mobZoneMobs.stream().forEach(m -> {
               m.setPhase(6666);
               m.setMobZoneDataType(0);
               this.currentMobZone = null;
               this.broadcastMobPhaseChange(boss.getObjectId(), 6666, 0);
               this.broadcastMobZoneChange(boss.getObjectId(), 0);
            });
         }
      }
   }

   @Override
   public void onMobChangeHP(MapleMonster mob) {
      super.onMobChangeHP(mob);
      if (mob.getId() == 8880000 || mob.getId() == 8880002 || mob.getId() == 8880010) {
         if (mob.getHPPercent() <= 75 && mob.getMobZoneDataType() == 1) {
            if (mob.getId() == 8880010) {
               Rect mBR = this.calculateMBR();
               this.registerObstacleAtom(
                  ObstacleAtomCreateType.NORMAL,
                  5,
                  0,
                  1,
                  3600000,
                  4000,
                  new Consumer[]{
                     ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                     ObstacleAtomCreatorOption.SetMaxP(1, 3),
                     ObstacleAtomCreatorOption.SetVperSec(80, 200),
                     ObstacleAtomCreatorOption.SetTrueDamR(25),
                     ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
                  }
               );
            }

            this.changeMobZone(2);
            this.sendSmartMobNotice(SmartMobNoticeType.Normal, mob.getId(), SmartMobMsgType.Field, 2, "매그너스가 구와르를 제어하는 힘이 약화 되었습니다. 구와르의 기운이 더욱 강해집니다.");
         } else if (mob.getHPPercent() <= 50 && mob.getMobZoneDataType() == 2) {
            this.changeMobZone(3);
            this.clearObstacleAtomCreators();
            Rect mBR = this.calculateMBR();
            this.FirstObstacleAtomHandle(mob, mBR);
            this.sendSmartMobNotice(SmartMobNoticeType.Normal, mob.getId(), SmartMobMsgType.Field, 3, "매그너스가 구와르를 제어하는 힘이 약화 되었습니다. 구와르의 기운이 더욱 강해집니다.");
         } else if (mob.getHPPercent() <= 25 && mob.getMobZoneDataType() == 3) {
            this.changeMobZone(4);
            this.sendSmartMobNotice(SmartMobNoticeType.Normal, mob.getId(), SmartMobMsgType.Field, 4, "매그너스가 구와르를 제어하는 힘이 약화 되었습니다. 구와르의 기운이 더욱 강해집니다.");
         }
      }
   }

   @Override
   public void onPlayerDead(MapleCharacter player) {
      super.onPlayerDead(player);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
      if (this.activeDecHPr) {
         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            if (player.getBuffedValue(SecondaryStatFlag.MobZoneState) == null && System.currentTimeMillis() - player.getLastDecHPrTime() >= this.decInterval) {
               int hp = (int)(player.getStat().getCurrentMaxHp(player) * 0.1);
               player.addHP(-hp);
               HPHeal e = new HPHeal(player.getId(), -hp);
               player.send(e.encodeForLocal());
               player.getMap().broadcastMessage(player, e.encodeForRemote(), false);
               player.setLastDecHPrTime(System.currentTimeMillis());
            }
         }
      }

      if (!this.firstSet) {
         MapleMonster mob = this.findBoss();
         if (mob != null && (mob.getId() == 8880000 || mob.getId() == 8880002 || mob.getId() == 8880010)) {
            this.clearObstacleAtomCreators();
            Rect mBR = this.calculateMBR();
            this.SecondObstacleAtomHandle(mob, mBR);
            if (mob.getId() == 8880000 || mob.getId() == 8880002) {
               this.SleepGasHandle(mob);
            }

            this.changeMobZone(1);
            this.setActiveDebuffObjs(true);
         }

         this.firstSet = true;
      }
   }

   @Override
   public void onCompleteFieldCommand() {
      super.onCompleteFieldCommand();
   }

   public void SleepGasHandle(MapleMonster mob) {
      int duration = 40;
      if (mob.getMobZoneDataType() == 2 || mob.getMobZoneDataType() == 3) {
         duration = 30;
      } else if (mob.getMobZoneDataType() == 4) {
         duration = 20;
      }

      for (int i = 1; i <= 5; i++) {
         this.registerDebuffObj(i, 1, "sleepGas" + i, "sleepGas", duration);
      }
   }

   public void FirstObstacleAtomHandle(MapleMonster mob, Rect mBR) {
      switch (mob.getId()) {
         case 8880000:
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               1,
               2,
               5,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               2,
               2,
               5,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               3,
               2,
               5,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               4,
               2,
               5,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               5,
               2,
               5,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               6,
               1,
               4,
               3600000,
               7000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(100),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               7,
               1,
               4,
               3600000,
               7000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(80),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               8,
               1,
               4,
               3600000,
               7000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(100),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            break;
         case 8880002:
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               1,
               2,
               5,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               2,
               2,
               5,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               3,
               2,
               5,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               4,
               2,
               5,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               5,
               2,
               5,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               6,
               1,
               4,
               3600000,
               7000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(100),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               7,
               1,
               4,
               3600000,
               7000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(100),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               8,
               1,
               4,
               3600000,
               7000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(100),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            break;
         case 8880010:
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               1,
               0,
               4,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(10),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               2,
               0,
               4,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(10),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               3,
               0,
               2,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(10),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               4,
               0,
               2,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(10),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
      }
   }

   public void SecondObstacleAtomHandle(MapleMonster mob, Rect mBR) {
      switch (mob.getId()) {
         case 8880000:
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               1,
               1,
               3,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               2,
               1,
               3,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               3,
               1,
               3,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               4,
               1,
               3,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               5,
               1,
               3,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               6,
               1,
               3,
               3600000,
               7000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(100),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               7,
               0,
               2,
               3600000,
               7000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(80),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               8,
               0,
               2,
               3600000,
               7000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(100),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            break;
         case 8880002:
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               1,
               1,
               3,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               2,
               1,
               3,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               3,
               1,
               3,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               4,
               1,
               3,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               5,
               1,
               3,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               6,
               1,
               3,
               3600000,
               7000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(50),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               7,
               0,
               2,
               3600000,
               7000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(60),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               8,
               0,
               2,
               3600000,
               7000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(80),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            break;
         case 8880010:
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               1,
               0,
               2,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(10),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               2,
               0,
               2,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(10),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               3,
               0,
               1,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(10),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
            this.registerObstacleAtom(
               ObstacleAtomCreateType.NORMAL,
               4,
               0,
               1,
               3600000,
               5000,
               new Consumer[]{
                  ObstacleAtomCreatorOption.SetCreateDelay(600, 2000),
                  ObstacleAtomCreatorOption.SetMaxP(1, 3),
                  ObstacleAtomCreatorOption.SetVperSec(80, 200),
                  ObstacleAtomCreatorOption.SetTrueDamR(10),
                  ObstacleAtomCreatorOption.SetDirectionHorizontalToFootHold(mBR.getLeft(), mBR.getRight(), -2000, -1347, this)
               }
            );
      }
   }
}
