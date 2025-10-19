package objects.fields.child.vonleon;

import constants.QuestExConstants;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import network.game.GameServer;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import scripting.EventInstanceManager;

public class Field_VonLeon extends Field {
   public Field_VonLeon(int mapid, int channel, int returnMapId, float monsterRate) {
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
      if (mob.getId() == 8840014 || mob.getId() == 8840000 || mob.getId() == 8840007) {
         mob.addAllowedFsmSkill(1);
         mob.addAllowedFsmSkill(6);
      }
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      super.onMobKilled(mob);
      List<Integer> mobs = new ArrayList<>(Arrays.asList(8840007, 8840000, 8840014));
      if (mobs.contains(mob.getId())) {
         boolean set = false;
         int questId = (Integer)QuestExConstants.bossQuests.get(mob.getId());

         for (MapleCharacter p : this.getCharactersThreadsafe()) {
            if (p.getParty() != null) {
               p.addGuildContributionByBoss(mob.getId());
               if (!set) {
                  EventInstanceManager eim = p.getEventInstance();
                  if (eim != null) {
                     eim.restartEventTimer(300000L);
                     eim.getMapInstance(Integer.parseInt(eim.getProperty("map"))).startMapEffect("반 레온을 물리치셨습니다. 왼쪽 포탈을 통해 이동해주시기 바랍니다.", 5120026);
                     List<Integer> partyPlayerList = eim.getPartyPlayerList();
                     if (partyPlayerList != null && !partyPlayerList.isEmpty()) {
                        for (Integer playerID : partyPlayerList) {
                           boolean find = false;

                           for (GameServer gs : GameServer.getAllInstances()) {
                              MapleCharacter player = gs.getPlayerStorage().getCharacterById(playerID);
                              if (player != null) {
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
      if (mob.getHPPercent() <= 50 && mob.checkTriggeredOnce(0)) {
         mob.addOnetimeFsmSkill(5);
         mob.addIgnoreIntervalSkill(5);
      } else if (mob.getHPPercent() <= 30 && mob.checkTriggeredOnce(1)) {
         mob.addOnetimeFsmSkill(5);
         mob.addIgnoreIntervalSkill(5);
      }
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
