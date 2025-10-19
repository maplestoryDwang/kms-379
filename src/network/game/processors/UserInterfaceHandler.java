package network.game.processors;

import database.DBConfig;
import java.util.List;
import network.decode.PacketDecoder;
import network.models.CWvsContext;
import objects.context.party.boss.BossParty;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import scripting.NPCScriptManager;
import scripting.newscripting.ScriptManager;

public class UserInterfaceHandler {
   public static final void ShipObjectRequest(int mapid, MapleClient c) {
   }

   public static final void BossEnter_Request(PacketDecoder p, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      int order = p.readInt();
      int difficult = p.readInt();
      int mapId = p.readInt();
      BossParty bossParty = null;
      List<BossParty> bps = BossParty.cachedBossParty.get(order);
      if (bps != null) {
         for (BossParty bp : bps) {
            if (bp.getDifficulty() == difficult && bp.getEnterFieldID() == mapId) {
               bossParty = bp;
               break;
            }
         }

         if (bossParty != null) {
            if (chr.getLevel() >= bossParty.getLevelMin()) {
               if (DBConfig.isGanglim) {
                  if (chr.getParty() == null) {
                     chr.createParty();
                  }

                  if (chr.getId() != chr.getParty().getLeader().getId()) {
                     chr.dropMessage(1, "파티장만 입장 가능합니다.");
                     return;
                  }
               }

               if (chr.getEventInstance() != null) {
                  chr.getEventInstance().unregisterPlayer(chr);
                  chr.setEventInstance(null);
               }

               if (bossParty.getEnterFieldID() == 240040700) {
                  mapId = 240050000;
               }

               if (bossParty.getEnterFieldID() == 272000000) {
                  mapId = 272020110;
               }

               if (bossParty.getEnterFieldID() == 270040000) {
                  mapId = 270050000;
               }

               if (bossParty.getEnterFieldID() == 271030000) {
                  mapId = 271040000;
               }

               if (DBConfig.isGanglim) {
                  int portalNum = 0;
                  if (mapId == 350060300 || mapId == 105300303 || mapId == 450009301 || mapId == 450012500) {
                     portalNum = 1;
                  }

                  if (mapId == 450012200 || mapId == 450011990) {
                     portalNum = 2;
                  }

                  if (mapId == 410000670) {
                     portalNum = 4;
                  }

                  for (MapleCharacter member : c.getPlayer().getPartyMembers()) {
                     member.warp(mapId, portalNum);
                  }
               } else {
                  c.getPlayer().changeMap(mapId);
               }
            }
         }
      }
   }

   public static final void Open_EventList_Request(PacketDecoder p, MapleClient c) {
      if (p.readByte() == 0 && c.getPlayer() != null && c.getPlayer().isQuestStarted(501538)) {
         if (c.getPlayer().getOneInfoQuestInteger(501538, "value") < 1) {
            c.getPlayer().updateOneInfo(501538, "value", "1");
         }

         if (c.getPlayer().getOneInfoQuestInteger(501524, "state") < 2) {
            c.getPlayer().updateOneInfo(501524, "state", "2");
         }
      }
   }

   public static final void YetixPinkbeanStepUpCheck(PacketDecoder p, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr.getJob() == 13100 || chr.getJob() == 13500) {
         for (int i = 100566; i <= 100578; i++) {
            if (chr.getQuestStatus(i) == 1) {
               ScriptManager.runScript(c, "q" + i + "e", MapleLifeFactory.getNPC(9010000), null);
               break;
            }

            if (chr.getQuestStatus(i) == 2) {
               if (i != 100578) {
                  if (chr.getQuestStatus(i + 1) == 0) {
                     ScriptManager.runScript(c, "q" + i + "e", MapleLifeFactory.getNPC(9010000), null);
                     break;
                  }
               } else if (chr.getOneInfo(100565, "stepNum") != null && !chr.getOneInfo(100565, "stepNum").equals("finish")) {
                  ScriptManager.runScript(c, "q" + i + "e", MapleLifeFactory.getNPC(9010000), null);
                  break;
               }
            }
         }
      }
   }

   public static final void bossContentsHelp(PacketDecoder packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      int type = packet.readInt();
      int flag = packet.readInt();
      if (type == 1338 && flag == 1001) {
         if (!client.canRunScript()) {
            player.getClient().getSession().writeAndFlush(CWvsContext.enableActions(player));
            return;
         }

         client.removeClickedNPC();
         NPCScriptManager.getInstance().dispose(client);
         NPCScriptManager.getInstance().start(client, 1052206, "canbossenter", true);
      }
   }
}
