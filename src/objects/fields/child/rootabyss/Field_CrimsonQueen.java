package objects.fields.child.rootabyss;

import constants.QuestExConstants;
import database.DBConfig;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import network.game.GameServer;
import network.models.CField;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import objects.utils.Pair;
import scripting.EventInstanceManager;

public class Field_CrimsonQueen extends Field {
   public Field_CrimsonQueen(int mapid, int channel, int returnMapId, float monsterRate) {
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
      List<Integer> mobs = List.of(8920100, 8920101, 8920102, 8920103, 8920000, 8920001, 8920002, 8920003);
      if (mobs.contains(mob.getId()) && mob.getHp() <= 0L) {
         boolean set = false;
         int questId = (Integer)QuestExConstants.bossQuests.get(mob.getId());

         for (MapleCharacter p : this.getCharactersThreadsafe()) {
            if (p.getParty() != null) {
               if (mob.getId() < 8920100) {
                  p.addGuildContributionByBoss(8920000);
               } else {
                  p.addGuildContributionByBoss(8920100);
               }

               if (!set) {
                  String qexKey = "b_queen_clear";
                  int bossQuest = QuestExConstants.CrimsonQueen.getQuestID();
                  if (mob.getId() == 8920000 || mob.getId() == 8920001 || mob.getId() == 8920002 || mob.getId() == 8920003) {
                     qexKey = "chaos_b_queen_clear";
                     bossQuest = QuestExConstants.ChaosCrimsonQueen.getQuestID();
                  }

                  int qexID = 1234569;
                  List<Pair<Integer, String>> qex = new ArrayList<>(Arrays.asList(new Pair<>(qexID, qexKey), new Pair<>(bossQuest, "eNum")));
                  EventInstanceManager eim = p.getEventInstance();
                  if (eim != null) {
                     eim.restartEventTimer(300000L);
                     if (eim.getProperty("mode").equals("chaos")) {
                        eim.getMapInstance(Integer.parseInt(eim.getProperty("map")) + 10)
                           .spawnMonster(MapleLifeFactory.getMonster(8920006), new Point(60, 135), 1);
                     } else {
                        eim.getMapInstance(Integer.parseInt(eim.getProperty("map")) + 10)
                           .spawnMonster(MapleLifeFactory.getMonster(8920106), new Point(60, 135), 1);
                     }

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
                                 if (player.getQuestStatus(2000003) == 1 && player.getOneInfo(2000003, "queen") == null) {
                                    player.updateOneInfo(2000003, "queen", "1");
                                 }

                                 player.updateOneInfo(qexID, qexKey, "1");
                                 if (!DBConfig.isGanglim) {
                                    player.updateOneInfo(bossQuest, "eNum", "1");
                                    if (multiMode) {
                                       String questKey = "eNum_multi";
                                       int eNum_Count = player.getOneInfoQuestInteger(bossQuest, "eNum_multi");
                                       player.updateOneInfo(bossQuest, "eNum_multi", String.valueOf(eNum_Count + 1));
                                    } else {
                                       String questKey = "eNum_single";
                                       int eNum_Count = player.getOneInfoQuestInteger(bossQuest, "eNum_single");
                                       player.updateOneInfo(bossQuest, "eNum_single", String.valueOf(eNum_Count + 1));
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
                                 this.updateOfflineBossLimit(playerID, (Integer)qex.get(count).left, (String)qex.get(count).right, "1");
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
   public void onMobSkill(MapleMonster mob, int skillID, int skillLevel) {
      super.onMobSkill(mob, skillID, skillLevel);
      if (skillID == 201) {
         int itemID = 0;
         String msg = "";
         switch (skillLevel) {
            case 51:
            case 55:
               msg = "내가 상대해주겠어요.";
               itemID = 5120099;
               break;
            case 52:
            case 56:
               msg = "킥킥, 다 없애주지";
               itemID = 5120101;
               break;
            case 53:
            case 57:
               msg = "모두 불태워주마!";
               itemID = 5120100;
               break;
            case 54:
            case 58:
               msg = "내 고통을 느끼게 해줄게요.";
               itemID = 5120102;
         }

         this.broadcastMessage(CField.startMapEffect(msg, itemID, true, 3));
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
