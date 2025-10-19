package objects.fields.child.hillah;

import constants.QuestExConstants;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.game.GameServer;
import objects.fields.Field;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import scripting.EventInstanceManager;

public class Field_Hillah extends Field {
   public Field_Hillah(int mapid, int channel, int returnMapId, float monsterRate) {
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
      List<Integer> mobs = new ArrayList<>(Arrays.asList(8870000, 8870100));
      if (mobs.contains(mob.getId())) {
         boolean set = false;

         for (MapleCharacter p : this.getCharactersThreadsafe()) {
            if (p.getParty() != null) {
               p.addGuildContributionByBoss(mob.getId());
               int questId = (Integer)QuestExConstants.bossQuests.get(mob.getId());
               if (!set) {
                  EventInstanceManager eim = p.getEventInstance();
                  if (eim != null) {
                     eim.restartEventTimer(300000L);
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

   public void applyHillahVampireState(MapleMonster mob) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.APPLY_HILLAH_VAMPIRE_STATE.getValue());
      packet.writeInt(mob.getObjectId());
      this.broadcastMessage(packet.getPacket());
   }

   public void applyHillahVampireEffectsToMonsters(MapleMonster mob, List<MapleMonster> mobs) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.APPLY_HILLAH_VAMPIRE_EFFECTS_TO_MONSTERS.getValue());
      packet.writeInt(mob.getObjectId());
      packet.writeInt(mobs.size());
      mobs.forEach(m -> packet.writeInt(m.getObjectId()));
      this.broadcastMessage(packet.getPacket());
   }

   public void applyHillahVampireEffect(MapleMonster mob) {
      PacketEncoder packet = new PacketEncoder();
      packet.writeShort(SendPacketOpcode.APPLY_HILLAH_VAMPIRE_EFFECT.getValue());
      packet.writeInt(mob.getObjectId());
      this.broadcastMessage(packet.getPacket());
   }
}
