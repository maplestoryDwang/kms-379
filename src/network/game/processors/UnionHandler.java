package network.game.processors;

import constants.QuestExConstants;
import constants.ServerConstants;
import database.DBConfig;
import database.DBConnection;
import database.loader.CharacterSaveFlag;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.fields.child.union.MapleUnion;
import objects.fields.child.union.MapleUnionData;
import objects.fields.child.union.MapleUnionEntry;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import scripting.NPCConversationManager;
import scripting.NPCScriptManager;
import scripting.newscripting.ScriptManager;

public class UnionHandler {
   public static void userOpenMapleUnionRequest(PacketDecoder slea, MapleClient c) {
      if (DBConfig.isGanglim) {
         int vv = c.getPlayer().getItemQuantity(4310229, false);
         c.getPlayer().updateOneInfo(QuestExConstants.UnionCoin.getQuestID(), "point", String.valueOf(vv));
      }

      MapleUnion union = c.getPlayer().getMapleUnion();
      if (union == null) {
         c.getPlayer().dropMessage(1, "계정 내 캐릭터 종합 레벨이 500을 넘지 않거나 로드에 실패하였습니다.\r\n\r\n계정 내 캐릭터 종합 레벨이 500을 넘었다면 마을 내 유니온 엔피시를 찾아가보세요.");
         c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
      } else {
         c.getPlayer().vaildateMapleUnion(c.getPlayer().getMapleUnion(), true);
         PacketEncoder packet = new PacketEncoder();
         packet.writeShort(SendPacketOpcode.UI_MAPLE_UNION.getValue());
         String value = c.getPlayer().getOneInfo(18098, "coin");
         int coin = 0;
         if (value != null && !value.isEmpty()) {
            coin = Integer.parseInt(value);
         }

         packet.writeInt(coin);
         union.encodeUI(packet);
         c.getPlayer().send(packet.getPacket());
      }
   }

   public static void mapleUnionPresetModifyRequest(PacketDecoder slea, MapleClient c) {
      int presetID = slea.readInt();
      MapleUnion union = c.getPlayer().getMapleUnion(presetID);
      if (union != null) {
         slea.skip(1);
         union.changeableGroup.clear();

         for (int i = 0; i < 8; i++) {
            union.changeableGroup.add(slea.readInt());
         }

         int activeRaidersCount = slea.readInt();
         if (activeRaidersCount > MapleUnionData.getRankData(union.rank).getAttackerCount()) {
            c.getPlayer().dropMessage(1, "최대 공격대원을 초과하여 공격대를 배치할 수 없습니다. 다시 시도해주시기 바랍니다.");
            c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
            c.getPlayer().send(CField.UIPacket.closeUI(1148));
         } else {
            union.activeRaiders.clear();

            for (int i = 0; i < activeRaidersCount; i++) {
               MapleUnionEntry entry = new MapleUnionEntry();
               entry.type = slea.readInt();
               entry.characterID = slea.readInt();
               entry.level = slea.readInt();
               entry.job = slea.readInt();
               entry.unk1 = slea.readInt();
               entry.angle = slea.readInt();
               entry.board = slea.readInt();
               entry.starForce = slea.readInt();
               entry.name = slea.readMapleAsciiString();
               union.activeRaiders.add(entry);
            }

            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.MAPLE_UNION_PRESET.getValue());
            packet.writeInt(presetID);
            packet.write(1);

            for (int group : union.changeableGroup) {
               packet.writeInt(group);
            }

            packet.writeInt(union.activeRaiders.size());

            for (MapleUnionEntry r : union.activeRaiders) {
               r.encode(packet);
            }

            c.getPlayer().send(packet.getPacket());
            c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
            c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
            c.getPlayer().updateOneInfo(QuestExConstants.UnionPreset.getQuestID(), "presetNo", String.valueOf(presetID));
            c.getPlayer()
               .setSaveFlag(
                  c.getPlayer().getSaveFlag()
                     | CharacterSaveFlag.MAPLE_UNION_DATA.getFlag()
                     | CharacterSaveFlag.MAPLE_UNION_GROUP.getFlag()
                     | CharacterSaveFlag.MAPLE_UNION_RAIDERS.getFlag()
               );
         }
      }
   }

   public static void mapleUnionChangeRequest(PacketDecoder slea, MapleClient c) {
      int presetID = slea.readInt();
      int groupCount = slea.readInt();
      MapleUnion union = c.getPlayer().getMapleUnion(presetID);
      if (union != null) {
         c.getPlayer().setCurrentUnion(union);
         union.changeableGroup.clear();
         StringBuilder matrix = new StringBuilder();

         for (int i = 0; i < groupCount; i++) {
            int value = slea.readInt();
            matrix.append(i).append("=").append(value).append(";");
            union.changeableGroup.add(value);
         }

         c.getPlayer().updateInfoQuest(500627, matrix.toString());
         c.getPlayer().updateInfoQuest(18790 + presetID + 1, matrix.toString());
         int activeRaidersCount = slea.readInt();
         if (activeRaidersCount > MapleUnionData.getRankData(union.rank).getAttackerCount()) {
            c.getPlayer().dropMessage(1, "최대 공격대원을 초과하여 공격대를 배치할 수 없습니다. 다시 시도해주시기 바랍니다.");
            c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
            c.getPlayer().send(CField.UIPacket.closeUI(1148));
         } else {
            slea.skip(2);
            union.activeRaiders.clear();

            for (int i = 0; i < activeRaidersCount; i++) {
               MapleUnionEntry entry = new MapleUnionEntry();
               entry.type = slea.readInt();
               entry.characterID = slea.readInt();
               entry.level = slea.readInt();
               entry.job = slea.readInt();
               entry.unk1 = slea.readInt();
               entry.angle = slea.readInt();
               entry.board = slea.readInt();
               entry.starForce = slea.readInt();
               entry.name = slea.readMapleAsciiString();
               union.activeRaiders.add(entry);
            }

            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.MAPLE_UNION_CONTEXT.getValue());
            packet.writeInt(union.rank);
            union.encodeContext(packet);
            c.getPlayer().send(packet.getPacket());
            c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
            c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
            c.getPlayer().updateOneInfo(QuestExConstants.UnionPreset.getQuestID(), "presetNo", String.valueOf(presetID));
            c.getPlayer()
               .setSaveFlag(
                  c.getPlayer().getSaveFlag()
                     | CharacterSaveFlag.MAPLE_UNION_DATA.getFlag()
                     | CharacterSaveFlag.MAPLE_UNION_GROUP.getFlag()
                     | CharacterSaveFlag.MAPLE_UNION_RAIDERS.getFlag()
               );
         }
      }
   }

   public static void mapleUnionPresetRequest(PacketDecoder slea, MapleClient c) {
      int presetID = slea.readInt();
      MapleUnion union = c.getPlayer().getMapleUnion(presetID);
      if (union != null) {
         try (Connection con = DBConnection.getConnection()) {
            if (union.changeableGroup.isEmpty()) {
               c.getPlayer().loadMapleUnionPreset(union, con, presetID);
            }

            PacketEncoder packet = new PacketEncoder();
            packet.writeShort(SendPacketOpcode.MAPLE_UNION_PRESET.getValue());
            packet.writeInt(presetID);
            packet.write(union.changeableGroup.size() > 0);

            for (int group : union.changeableGroup) {
               packet.writeInt(group);
            }

            packet.writeInt(union.activeRaiders.size());

            for (MapleUnionEntry r : union.activeRaiders) {
               r.encode(packet);
            }

            c.getPlayer().send(packet.getPacket());
            c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
            c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
         } catch (SQLException var10) {
         }
      }
   }

   public static void unionRaidOutRequest(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      Field map = player.getMap();
      if (map != null) {
         Field target = c.getChannelServer().getMapFactory().getMap(map.getForcedReturnId());
         if (player.getHungryMuto() != null) {
            player.getHungryMuto().cancelTask();
            player.setHungryMuto(null);
         }

         player.changeMap(target);
      }
   }

   public static void userRunScript(PacketDecoder slea, MapleClient c) {
      MapleCharacter player = c.getPlayer();
      Field map = player.getMap();
      if (map != null) {
         int type = slea.readShort();
         int mapId = slea.readInt();
         if (!player.getClient().canRunScript()) {
            player.getClient().getSession().writeAndFlush(CWvsContext.enableActions(player));
         } else {
            player.getClient().removeClickedNPC();
            NPCScriptManager.getInstance().dispose(player.getClient());
            player.getClient().getSession().writeAndFlush(CWvsContext.enableActions(player));
            switch (type) {
               case 10:
               case 12:
               case 13:
               case 14:
               case 15:
               case 16:
               case 20:
               case 21:
               case 22:
               case 23:
               case 24:
               case 25:
               case 26:
               case 27:
               case 28:
               case 29:
               case 31:
               case 32:
               case 33:
               case 34:
               case 35:
               case 36:
               case 38:
               case 39:
               case 40:
               case 42:
               case 43:
               case 44:
               case 45:
               case 46:
               case 49:
               case 50:
               case 51:
               case 52:
               default:
                  break;
               case 11:
                  if (DBConfig.isGanglim && player.getEventInstance() == null && player.getMap().getId() != ServerConstants.TownMap) {
                     player.warp(ServerConstants.TownMap);
                  }
                  break;
               case 17:
                  NPCScriptManager.getInstance().start(c, 9010106, "mapleunion_battle", true);
                  break;
               case 18:
                  NPCScriptManager.getInstance().start(c, 9010106, "mapleunion_help", true);
                  break;
               case 19:
                  ScriptManager.runScript(c, "getUnionCoin", MapleLifeFactory.getNPC(9010108));
                  break;
               case 30:
                  int questId = slea.readInt();
                  int modexx = slea.readInt();
                  int wig = 1001;
                  int goldenPass = 1003;
                  int attend = 1004;
                  switch (modexx) {
                     case 1001:
                        ScriptManager.runScript(c, "goldenchariotWIG", MapleLifeFactory.getNPC(9000029));
                        return;
                     case 1002:
                     default:
                        return;
                     case 1003:
                        ScriptManager.runScript(c, "goldenchariotPass", MapleLifeFactory.getNPC(9000029));
                        return;
                     case 1004:
                        ScriptManager.runScript(c, "goldenchariot", MapleLifeFactory.getNPC(9000029));
                        return;
                  }
               case 37:
                  byte modex = slea.readByte();
                  if (modex == 0) {
                     NPCScriptManager.getInstance().start(c, 9062297, "membership_point", true);
                  } else if (modex == 1) {
                     int flag = slea.readInt();
                     startNpcWithFlag((DBConfig.isGanglim ? "Royal/" : "Jin/") + "npc/membership_reward.js", c, flag, 9062297);
                  } else if (modex == 2) {
                     int flag = slea.readInt();
                     startNpcWithFlag((DBConfig.isGanglim ? "Royal/" : "Jin/") + "npc/membership_skill.js", c, flag, 9062297);
                  }
                  break;
               case 41:
                  modex = (byte)slea.readInt();
                  c.removeClickedNPC();
                  NPCScriptManager.getInstance().dispose(c);
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  Invocable iv = NPCHandler.getInvocable("npc/1531010.js", c, true);
                  if (iv == null) {
                     iv = NPCHandler.getInvocable("npc/notcoded.js", c, true);
                  }

                  ScriptEngine scriptengine = (ScriptEngine)iv;
                  NPCConversationManager cm = new NPCConversationManager(c, 1531010, -1, (byte)-1, iv);
                  scriptengine.put("cm", cm);
                  c.getPlayer().setConversation(1);
                  c.setClickedNPC();

                  try {
                     iv.invokeFunction("trade_" + modex);
                  } catch (ScriptException | NoSuchMethodException var11) {
                  }
                  break;
               case 47:
                  int mode = slea.readInt();
                  if (mode == 1001) {
                     startNpcWithFlag((DBConfig.isGanglim ? "Royal/" : "Jin/") + "npc/adventure_report.js", c, 0, 9062454);
                  } else if (mode == 1003) {
                     startNpcWithFlag((DBConfig.isGanglim ? "Royal/" : "Jin/") + "npc/adventure_report.js", c, 1, 9062454);
                  }
                  break;
               case 48:
                  ScriptManager.runScript(c, "yetiXpinkHeadTitle", MapleLifeFactory.getNPC(9010000));
                  break;
               case 53:
                  int op = slea.readByte();
                  if (op == 0) {
                     ScriptManager.runScript(c, "flag_NPC", MapleLifeFactory.getNPC(9000233));
                  } else if (op == 2) {
                     ScriptManager.runScript(c, "Culvert_NPC", MapleLifeFactory.getNPC(2012041));
                  }
            }
         }
      }
   }

   public static void startNpcWithFlag(String path, MapleClient c, int flag, int npc) {
      c.removeClickedNPC();
      NPCScriptManager.getInstance().dispose(c);
      c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      Invocable iv = getInvocable(path, c, true);
      if (DBConfig.isGanglim) {
         int vv = c.getPlayer().getItemQuantity(4310229, false);
         c.getPlayer().updateOneInfo(QuestExConstants.UnionCoin.getQuestID(), "point", String.valueOf(vv));
      }

      if (iv == null) {
         iv = getInvocable("npc/notcoded.js", c, true);
      }

      ScriptEngine scriptengine = (ScriptEngine)iv;
      NPCConversationManager cm = new NPCConversationManager(c, npc, -1, (byte)-1, iv);
      scriptengine.put("cm", cm);
      NPCScriptManager.getInstance().putConversationManager(c, cm);
      c.getPlayer().setConversation(1);
      c.setClickedNPC();

      try {
         iv.invokeFunction("start", flag);
      } catch (ScriptException | NoSuchMethodException var8) {
      }
   }

   public static Invocable getInvocable(String path, MapleClient c, boolean npc) {
      path = "scripts/" + path;
      ScriptEngine engine = null;
      if (c != null) {
         engine = c.getScriptEngine(path);
      }

      if (engine == null) {
         File scriptFile = new File(path);
         if (!scriptFile.exists()) {
            return null;
         }

         engine = new ScriptEngineManager().getEngineByName("nashorn");
         if (c != null) {
            c.setScriptEngine(path, engine);
         }

         String encoding = "EUC-KR";

         try (Stream<String> stream = Files.lines(scriptFile.toPath(), Charset.forName(encoding))) {
            String lines = "load('nashorn:mozilla_compat.js');";
            lines = lines + stream.collect(Collectors.joining(System.lineSeparator()));
            engine.eval(lines);
         } catch (Exception var15) {
            encoding = "UTF-8";

            try (Stream<String> streamx = Files.lines(scriptFile.toPath(), Charset.forName(encoding))) {
               String linesx = "load('nashorn:mozilla_compat.js');";
               linesx = linesx + streamx.collect(Collectors.joining(System.lineSeparator()));
               engine.eval(linesx);
            } catch (IOException | ScriptException var13) {
               System.out.println("유니온 스크립트 오류 발생");
               var13.printStackTrace();
               return null;
            }
         }
      } else if (c != null && npc) {
      }

      return (Invocable)engine;
   }
}
