package objects.fields.child.arkarium;

import constants.QuestExConstants;
import constants.ServerConstants;
import database.DBConfig;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.fields.gameobject.Drop;
import objects.fields.gameobject.Reactor;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.MapleNPC;
import objects.fields.gameobject.lifes.mobskills.MobSkillID;
import objects.item.Item;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;
import scripting.EventInstanceManager;

public class Field_Arkaium extends Field {
   public Field_Arkaium(int mapid, int channel, int returnMapId, float monsterRate) {
      super(mapid, channel, returnMapId, monsterRate);
   }

   @Override
   public void resetFully(boolean respawn) {
      super.resetFully(false);
   }

   @Override
   public void onEnter(MapleCharacter player) {
      super.onEnter(player);
      this.hideNpc(2144016);
   }

   @Override
   public void onLeave(MapleCharacter player) {
      super.onLeave(player);
      player.temporaryStatReset(SecondaryStatFlag.DeathMark);
      player.temporaryStatReset(SecondaryStatFlag.VenomSnake);
      player.temporaryStatReset(SecondaryStatFlag.Morph);
   }

   @Override
   public void onMobEnter(MapleMonster mob) {
      super.onMobEnter(mob);
   }

   @Override
   public void onMobKilled(MapleMonster mob) {
      super.onMobKilled(mob);
      List<Integer> mobs = new ArrayList<>(Arrays.asList(8860000, 8860005));
      if (mobs.contains(mob.getId())) {
         int questId = (Integer)QuestExConstants.bossQuests.get(mob.getId());

         for (Drop item : this.getAllItemsThreadsafe()) {
            item.expire(this);
         }

         for (MapleMonster m : this.getAllMonstersThreadsafe()) {
            if (m.getId() != mob.getId()) {
               this.removeMonster(m, 1);
            }
         }

         boolean set = false;

         for (MapleCharacter p : this.getCharactersThreadsafe()) {
            if (p.getParty() != null) {
               p.addGuildContributionByBoss(mob.getId());
               if (!set) {
                  EventInstanceManager eim = p.getEventInstance();
                  if (eim != null) {
                     eim.restartEventTimer(300000L);
                     Reactor reactor = this.getReactorByName("break1");
                     if (reactor != null) {
                        reactor.forceHitReactor((byte)1);
                     }

                     MapleNPC npc = this.getNPCById(2144020);
                     if (npc != null) {
                        this.broadcastMessage(CField.NPCPacket.npcSpecialAction(npc.getObjectId(), "teleport", 0, 0));
                     }

                     eim.getMapInstance(Integer.parseInt(eim.getProperty("map")))
                        .broadcastMessage(CWvsContext.getScriptProgressMessage("시간의 여신 륀느가 봉인에서 풀려났습니다."));
                     eim.getMapInstance(Integer.parseInt(eim.getProperty("map"))).startMapEffect("아카이럼을 퇴치하였습니다. 제단의 좌측 포탈을 통해 이동해주시기 바랍니다.", 5120026);
                     List<Integer> partyPlayerList = eim.getPartyPlayerList();
                     if (partyPlayerList != null && !partyPlayerList.isEmpty()) {
                        boolean multiMode = false;
                        if (!DBConfig.isGanglim) {
                           for (Integer playerID : partyPlayerList) {
                              if (multiMode) {
                                 break;
                              }

                              for (GameServer gs : GameServer.getAllInstances()) {
                                 if (multiMode) {
                                    break;
                                 }

                                 MapleCharacter player = gs.getPlayerStorage().getCharacterById(playerID);
                                 if (player != null && player.isMultiMode()) {
                                    multiMode = true;
                                    break;
                                 }
                              }
                           }
                        }

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

            p.postRunScript("akayrum_saveTheGoddess", 2144020);
         }
      }

      mobs = new ArrayList<>(Arrays.asList(8860002, 8860006));
      if (mobs.contains(mob.getId())) {
         int item = 2002058;
         Point pos = mob.getTruePosition();
         byte d = 1;

         for (int i = 0; i < 3; i++) {
            Item drop = new Item(item, (short)0, (short)1, 0);
            pos.x = mob.getTruePosition().x + (d % 2 == 0 ? 25 * (d + 1) / 2 : -(25 * (d / 2)));
            this.spawnMobDrop(drop, this.calcDropPos(pos, mob.getTruePosition()), mob, mob.getController(), (byte)0, 0);
            d++;
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

   @Override
   public void onMobSkill(MapleMonster mob, int skillID, int skillLevel) {
      super.onMobSkill(mob, skillID, skillLevel);
      if (skillID == MobSkillID.BAN_MAP.getVal() && skillLevel == 14 && !ServerConstants.disableBanMap) {
         int baseID = 272020310;
         if (this.getMobsSize(8860000) > 0) {
            baseID = 272020300;
         }

         List<Integer> mapList = new ArrayList<>();

         for (MapleCharacter player : this.getCharactersThreadsafe()) {
            if (!player.isHidden() && player.isAlive()) {
               for (int i = baseID; i <= baseID + 9; i++) {
                  Field field = GameServer.getInstance(player.getClient().getChannel()).getMapFactory().getMap(i);
                  if (field != null && field.getCharactersSize() == 0 && !mapList.contains(i)) {
                     player.dropMessage(5, "아카이럼이 시공간을 비틀어 어디론가 추방합니다.");
                     player.setRegisterTransferField(i);
                     player.setRegisterTransferFieldTime(System.currentTimeMillis());
                     mapList.add(i);
                     break;
                  }
               }
            }
         }
      }
   }
}
