package objects.fields.child.pinkbeen;

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
import network.game.GameServer;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.utils.Pair;
import scripting.EventInstanceManager;

public class Field_Pinkbeen extends Field {
   public Field_Pinkbeen(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(false);
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
      if (this.getId() == 270050100) {
         boolean fkingsummon = true;
         List<Integer> mobs = new ArrayList<>(Arrays.asList(8820002, 8820003, 8820004, 8820005, 8820006));

         for (MapleMonster m : this.getAllMonstersThreadsafe()) {
            if (m.getId() != mob.getId() && mobs.contains(m.getId())) {
               fkingsummon = false;
               break;
            }
         }

         if (fkingsummon) {
            this.killMonster(this.getMonsterById(8820000));
            this.killMonster(this.getMonsterById(8820014));
         }
      } else if (this.getId() == 270051100) {
         boolean fkingsummon = true;
         List<Integer> mobs = new ArrayList<>(Arrays.asList(8820102, 8820103, 8820104, 8820105, 8820106));

         for (MapleMonster mx : this.getAllMonstersThreadsafe()) {
            if (mx.getId() != mob.getId() && mobs.contains(mx.getId())) {
               fkingsummon = false;
               break;
            }
         }

         if (fkingsummon) {
            this.killMonster(this.getMonsterById(8820100));
            this.killMonster(this.getMonsterById(8820114));
         }
      }

      if (mob.getId() == 8820101 || mob.getId() == 8820200) {
         switch (mob.getId()) {
            case 8820101: {
               MapleMonster monster = MapleLifeFactory.getMonster(8820200);
               this.spawnMonsterSponge(monster, mob.getPosition(), mob.getObjectId());
               break;
            }
            case 8820200: {
               MapleMonster monster = MapleLifeFactory.getMonster(8820212);
               this.spawnMonsterSponge(monster, mob.getPosition(), mob.getObjectId());
            }
         }
      }

      List<Integer> mobs = new ArrayList<>(Arrays.asList(8820001, 8820212));
      if (mobs.contains(mob.getId())) {
         int questId = (Integer)QuestExConstants.bossQuests.get(mob.getId());
         boolean set = false;

         for (MapleCharacter p : this.getCharactersThreadsafe()) {
            if (p.getParty() != null) {
               p.addGuildContributionByBoss(mob.getId());
               if (!set) {
                  String qexKey = "pinkbeen_clear";
                  int bossQuest = QuestExConstants.PinkBeen.getQuestID();
                  if (mob.getId() == 8820212) {
                     qexKey = "chaos_pinkbeen_clear";
                     bossQuest = QuestExConstants.ChaosPinkBeen.getQuestID();
                  }

                  int qexID = 1234569;
                  List<Pair<Integer, String>> qex = new ArrayList<>(Arrays.asList(new Pair<>(qexID, qexKey), new Pair<>(bossQuest, "eNum")));
                  EventInstanceManager eim = p.getEventInstance();
                  if (eim != null) {
                     eim.restartEventTimer(300000L);
                     List<Integer> partyPlayerList = eim.getPartyPlayerList();
                     boolean multiMode = false;
                     if (!DBConfig.isGanglim && partyPlayerList != null && !partyPlayerList.isEmpty()) {
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

                     if (partyPlayerList != null && !partyPlayerList.isEmpty()) {
                        for (Integer playerID : partyPlayerList) {
                           boolean find = false;

                           for (GameServer gs : GameServer.getAllInstances()) {
                              MapleCharacter player = gs.getPlayerStorage().getCharacterById(playerID);
                              if (player != null) {
                                 player.updateOneInfo(qexID, qexKey, "1");
                                 player.updateOneInfo(bossQuest, "eNum", "1");
                                 if (!DBConfig.isGanglim) {
                                    player.updateOneInfo(
                                       bossQuest,
                                       "eNum_" + (multiMode ? "multi" : "single"),
                                       String.valueOf(player.getOneInfoQuestInteger(bossQuest, "eNum_" + (multiMode ? "multi" : "single")) + 1)
                                    );
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

   @Override
   public void onMobChangeHP(MapleMonster mob) {
      super.onMobChangeHP(mob);
   }

   @Override
   public void onPlayerDead(MapleCharacter player) {
      super.onPlayerDead(player);
   }

   @Override
   public void fieldUpdatePerSeconds() {
      super.fieldUpdatePerSeconds();
   }

   @Override
   public void onCompleteFieldCommand() {
      super.onCompleteFieldCommand();
   }
}
