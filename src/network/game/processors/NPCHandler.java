package network.game.processors;

import constants.GameConstants;
import constants.PlayerNPCConstants;
import database.DBConfig;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import logging.LoggingManager;
import logging.entry.ConsumeLogType;
import logging.entry.ItemLog;
import network.SendPacketOpcode;
import network.decode.PacketDecoder;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import network.models.GuildContents;
import network.models.StoragePacket;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleNPC;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.shop.MapleShop;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MapleStorage;
import objects.users.RockPaperScissors;
import objects.users.enchant.ItemFlag;
import objects.users.looks.zero.ZeroInfo;
import objects.utils.AutobanManager;
import objects.utils.CurrentTime;
import objects.utils.Pair;
import scripting.NPCConversationManager;
import scripting.NPCScriptManager;
import scripting.ScriptMessageType;
import scripting.newscripting.ScriptConversation;
import scripting.newscripting.ScriptEngineNPC;
import scripting.newscripting.ScriptManager;

public class NPCHandler {
   public static final void NPCAnimation(PacketDecoder slea, MapleClient c) {
      if (c.getPlayer() != null && c.getPlayer().getMap() != null) {
         int objectID = slea.readInt();
         MapleNPC npc = c.getPlayer().getMap().getNPCByOid(objectID);
         if (npc != null) {
            if (!PlayerNPCConstants.isPlayerNPC(npc.getId())) {
               PacketEncoder packet = new PacketEncoder();
               packet.writeShort(SendPacketOpcode.NPC_ACTION.getValue());
               packet.writeInt(objectID);
               byte act = slea.readByte();
               byte chatIndex = slea.readByte();
               int duration = slea.readInt();
               packet.write(act);
               packet.write(chatIndex);
               packet.writeInt(duration);
               packet.encodeBuffer(slea.read((int)slea.available()));
               c.getPlayer().send(packet.getPacket());
            }
         }
      }
   }

   public static final void NPCShop(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      byte bmode = slea.readByte();
      if (chr != null) {
         if (c.getChannelServer().getPlayerStorage().getCharacterById(c.getPlayer().getId()) != null) {
            switch (bmode) {
               case 0:
                  MapleShop shop = chr.getShop();
                  if (shop == null) {
                     return;
                  }

                  short slot = slea.readShort();
                  int itemId = slea.readInt();
                  short quantity = slea.readShort();
                  shop.buy(c, slot, itemId, quantity);
                  break;
               case 1:
                  shop = chr.getShop();
                  if (shop == null) {
                     return;
                  }

                  slot = (short)(slea.readShort() & 255);
                  itemId = slea.readInt();
                  quantity = slea.readShort();
                  shop.sell(c, GameConstants.getInventoryType(itemId), slot, quantity, true);
                  break;
               case 2:
                  shop = chr.getShop();
                  if (shop == null) {
                     return;
                  }

                  slot = (short)(slea.readShort() & 255);
                  shop.recharge(c, slot);
                  break;
               case 3:
               default:
                  chr.setConversation(0);
                  break;
               case 4:
                  shop = chr.getShop();
                  if (shop == null) {
                     return;
                  }

                  int count = slea.readInt();
                  if (count <= 0) {
                     return;
                  }

                  long remaining = slea.available();
                  if (remaining < count * 8) {
                     return;
                  }

                  for (int i = 0; i < count; i++) {
                     slot = (byte)slea.readShort();
                     itemId = slea.readInt();
                     quantity = slea.readShort();
                     shop.sell(c, GameConstants.getInventoryType(itemId), slot, quantity, i == count - 1);
                  }
            }
         }
      }
   }

   public static final void NPCTalk(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      if (chr != null && chr.getMap() != null) {
         int objectID = slea.readInt();
         MapleNPC npc = chr.getMap().getNPCByOid(objectID);
         if (npc != null) {
            if (npc.hasShop() && npc.getId() != 9090000) {
               chr.setConversation(1);
               npc.sendShop(c);
            } else {
               if (chr.isGM() || c.isGm()) {
                  chr.dropMessage(5, "NPC ID : " + npc.getId() + ", NPC Name : " + npc.getName() + " Script : " + MapleLifeFactory.getScript(npc.getId()));
                  System.out.println("NPC ID : " + npc.getId() + ", NPC Name : " + npc.getName() + " Script : " + MapleLifeFactory.getScript(npc.getId()));
               }

               if (chr.getConversation() == 1) {
                  return;
               }

               if (chr.getScriptThread() != null) {
                  return;
               }

               if (ScriptManager.get()._scripts.get(MapleLifeFactory.getScript(npc.getId())) != null) {
                  String script = MapleLifeFactory.getScript(npc.getId());
                  ScriptManager.runScript(c, script, npc, null);
               } else if (ScriptManager.get()._scripts.get("npc_" + npc.getId()) != null) {
                  ScriptManager.runScript(c, "npc_" + npc.getId(), npc, null);
               } else if (npc.getId() == 9010002) {
                  ScriptManager.runScript(c, "hongbo_npc", npc, null);
               } else if (npc.getId() == 9090000) {
                  ScriptManager.runScript(c, "Royal_Shop", npc, null);
               } else if (npc.getId() == 3004823) {
                  if (DBConfig.isGanglim) {
                     ScriptManager.runScript(c, "Royal_Storage", npc, null);
                  }
               } else if (npc.getId() == 9062454) {
                  if (DBConfig.isGanglim) {
                     ScriptManager.runScript(c, "recommend", npc, null);
                  }
               } else if (npc.getId() == 9040004) {
                  c.getPlayer().send(GuildContents.openGuildRankNPC());
               } else {
                  NPCScriptManager.getInstance().start(c, npc.getId());
               }
            }
         }
      }
   }

   public static final void QuestAction(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      byte action = slea.readByte();
      int quest = slea.readInt();
      if (chr != null) {
         if (quest < 100566 || quest > 100578) {
            if (quest < 501524 || quest > 501553) {
               if (!DBConfig.isGanglim && action == 1 && quest >= 2000030 && quest <= 2000035 && chr.getQuestStatus(quest) == 0) {
                  switch (quest) {
                     case 2000030:
                        if (chr.getLevel() >= 200) {
                           if (chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
                              chr.dropMessage(1, "장비 인벤토리 슬롯이 부족합니다.");
                              return;
                           }

                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           Item item = ii.randomizeStats((Equip)ii.getEquipById(1712001));
                           item.setGMLog(CurrentTime.getAllCurrentTime() + "에 " + c.getPlayer().getName() + "의 레벨 달성 퀘스트 보상");
                           short jobId = chr.getJob();
                           ((Equip)item).setArcLevel(5);
                           ((Equip)item).setArc(70);
                           if ((jobId < 100 || jobId >= 200)
                              && jobId != 512
                              && jobId != 1512
                              && jobId != 2512
                              && (jobId < 1100 || jobId >= 1200)
                              && !GameConstants.isAran(jobId)
                              && !GameConstants.isBlaster(jobId)
                              && !GameConstants.isDemonSlayer(jobId)
                              && !GameConstants.isMichael(jobId)
                              && !GameConstants.isKaiser(jobId)
                              && !GameConstants.isZero(jobId)
                              && !GameConstants.isArk(jobId)
                              && !GameConstants.isAdele(jobId)) {
                              if ((jobId < 200 || jobId >= 300)
                                 && !GameConstants.isFlameWizard(jobId)
                                 && !GameConstants.isEvan(jobId)
                                 && !GameConstants.isLuminous(jobId)
                                 && (jobId < 3200 || jobId >= 3300)
                                 && !GameConstants.isKinesis(jobId)
                                 && !GameConstants.isIllium(jobId)
                                 && !GameConstants.isLara(jobId)) {
                                 if (!GameConstants.isKain(jobId)
                                    && (jobId < 300 || jobId >= 400)
                                    && jobId != 522
                                    && jobId != 532
                                    && !GameConstants.isMechanic(jobId)
                                    && !GameConstants.isAngelicBuster(jobId)
                                    && (jobId < 1300 || jobId >= 1400)
                                    && !GameConstants.isMercedes(jobId)
                                    && (jobId < 3300 || jobId >= 3400)) {
                                    if ((jobId < 400 || jobId >= 500)
                                       && (jobId < 1400 || jobId >= 1500)
                                       && !GameConstants.isPhantom(jobId)
                                       && !GameConstants.isKadena(jobId)
                                       && !GameConstants.isHoyoung(jobId)
                                       && !GameConstants.isKhali(jobId)) {
                                       if (GameConstants.isDemonAvenger(jobId)) {
                                          ((Equip)item).setHp((short)1470);
                                       } else if (GameConstants.isXenon(jobId)) {
                                          ((Equip)item).setStr((short)336);
                                          ((Equip)item).setDex((short)336);
                                          ((Equip)item).setLuk((short)336);
                                       }
                                    } else {
                                       ((Equip)item).setLuk((short)700);
                                    }
                                 } else {
                                    ((Equip)item).setDex((short)700);
                                 }
                              } else {
                                 ((Equip)item).setInt((short)700);
                              }
                           } else {
                              ((Equip)item).setStr((short)700);
                           }

                           MapleInventoryManipulator.addbyItem(c, item);
                           chr.dropMessage(5, "아이템이 지급되었습니다.");
                           chr.forceCompleteQuest(quest);
                        }
                        break;
                     case 2000031:
                        if (chr.getLevel() >= 210) {
                           if (chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
                              chr.dropMessage(1, "장비 인벤토리 슬롯이 부족합니다.");
                              return;
                           }

                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           Item item = ii.randomizeStats((Equip)ii.getEquipById(1712002));
                           item.setGMLog(CurrentTime.getAllCurrentTime() + "에 " + c.getPlayer().getName() + "의 레벨 달성 퀘스트 보상");
                           short jobId = chr.getJob();
                           ((Equip)item).setArcLevel(5);
                           ((Equip)item).setArc(70);
                           if ((jobId < 100 || jobId >= 200)
                              && jobId != 512
                              && jobId != 1512
                              && jobId != 2512
                              && (jobId < 1100 || jobId >= 1200)
                              && !GameConstants.isAran(jobId)
                              && !GameConstants.isBlaster(jobId)
                              && !GameConstants.isDemonSlayer(jobId)
                              && !GameConstants.isMichael(jobId)
                              && !GameConstants.isKaiser(jobId)
                              && !GameConstants.isZero(jobId)
                              && !GameConstants.isArk(jobId)
                              && !GameConstants.isAdele(jobId)) {
                              if ((jobId < 200 || jobId >= 300)
                                 && !GameConstants.isFlameWizard(jobId)
                                 && !GameConstants.isEvan(jobId)
                                 && !GameConstants.isLuminous(jobId)
                                 && (jobId < 3200 || jobId >= 3300)
                                 && !GameConstants.isKinesis(jobId)
                                 && !GameConstants.isIllium(jobId)
                                 && !GameConstants.isLara(jobId)) {
                                 if (!GameConstants.isKain(jobId)
                                    && (jobId < 300 || jobId >= 400)
                                    && jobId != 522
                                    && jobId != 532
                                    && !GameConstants.isMechanic(jobId)
                                    && !GameConstants.isAngelicBuster(jobId)
                                    && (jobId < 1300 || jobId >= 1400)
                                    && !GameConstants.isMercedes(jobId)
                                    && (jobId < 3300 || jobId >= 3400)) {
                                    if ((jobId < 400 || jobId >= 500)
                                       && (jobId < 1400 || jobId >= 1500)
                                       && !GameConstants.isPhantom(jobId)
                                       && !GameConstants.isKadena(jobId)
                                       && !GameConstants.isHoyoung(jobId)
                                       && !GameConstants.isKhali(jobId)) {
                                       if (GameConstants.isDemonAvenger(jobId)) {
                                          ((Equip)item).setHp((short)1470);
                                       } else if (GameConstants.isXenon(jobId)) {
                                          ((Equip)item).setStr((short)336);
                                          ((Equip)item).setDex((short)336);
                                          ((Equip)item).setLuk((short)336);
                                       }
                                    } else {
                                       ((Equip)item).setLuk((short)700);
                                    }
                                 } else {
                                    ((Equip)item).setDex((short)700);
                                 }
                              } else {
                                 ((Equip)item).setInt((short)700);
                              }
                           } else {
                              ((Equip)item).setStr((short)700);
                           }

                           MapleInventoryManipulator.addbyItem(c, item);
                           chr.dropMessage(5, "아이템이 지급되었습니다.");
                           chr.forceCompleteQuest(quest);
                        }
                        break;
                     case 2000032:
                        if (chr.getLevel() >= 220) {
                           if (chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
                              chr.dropMessage(1, "장비 인벤토리 슬롯이 부족합니다.");
                              return;
                           }

                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           Item item = ii.randomizeStats((Equip)ii.getEquipById(1712003));
                           item.setGMLog(CurrentTime.getAllCurrentTime() + "에 " + c.getPlayer().getName() + "의 레벨 달성 퀘스트 보상");
                           short jobId = chr.getJob();
                           ((Equip)item).setArcLevel(5);
                           ((Equip)item).setArc(70);
                           if ((jobId < 100 || jobId >= 200)
                              && jobId != 512
                              && jobId != 1512
                              && jobId != 2512
                              && (jobId < 1100 || jobId >= 1200)
                              && !GameConstants.isAran(jobId)
                              && !GameConstants.isBlaster(jobId)
                              && !GameConstants.isDemonSlayer(jobId)
                              && !GameConstants.isMichael(jobId)
                              && !GameConstants.isKaiser(jobId)
                              && !GameConstants.isZero(jobId)
                              && !GameConstants.isArk(jobId)
                              && !GameConstants.isAdele(jobId)) {
                              if ((jobId < 200 || jobId >= 300)
                                 && !GameConstants.isFlameWizard(jobId)
                                 && !GameConstants.isEvan(jobId)
                                 && !GameConstants.isLuminous(jobId)
                                 && (jobId < 3200 || jobId >= 3300)
                                 && !GameConstants.isKinesis(jobId)
                                 && !GameConstants.isIllium(jobId)
                                 && !GameConstants.isLara(jobId)) {
                                 if (!GameConstants.isKain(jobId)
                                    && (jobId < 300 || jobId >= 400)
                                    && jobId != 522
                                    && jobId != 532
                                    && !GameConstants.isMechanic(jobId)
                                    && !GameConstants.isAngelicBuster(jobId)
                                    && (jobId < 1300 || jobId >= 1400)
                                    && !GameConstants.isMercedes(jobId)
                                    && (jobId < 3300 || jobId >= 3400)) {
                                    if ((jobId < 400 || jobId >= 500)
                                       && (jobId < 1400 || jobId >= 1500)
                                       && !GameConstants.isPhantom(jobId)
                                       && !GameConstants.isKadena(jobId)
                                       && !GameConstants.isKhali(jobId)
                                       && !GameConstants.isHoyoung(jobId)) {
                                       if (GameConstants.isDemonAvenger(jobId)) {
                                          ((Equip)item).setHp((short)1470);
                                       } else if (GameConstants.isXenon(jobId)) {
                                          ((Equip)item).setStr((short)336);
                                          ((Equip)item).setDex((short)336);
                                          ((Equip)item).setLuk((short)336);
                                       }
                                    } else {
                                       ((Equip)item).setLuk((short)700);
                                    }
                                 } else {
                                    ((Equip)item).setDex((short)700);
                                 }
                              } else {
                                 ((Equip)item).setInt((short)700);
                              }
                           } else {
                              ((Equip)item).setStr((short)700);
                           }

                           MapleInventoryManipulator.addbyItem(c, item);
                           chr.dropMessage(5, "아이템이 지급되었습니다.");
                           chr.forceCompleteQuest(quest);
                        }
                        break;
                     case 2000033:
                        if (chr.getLevel() >= 225) {
                           if (chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
                              chr.dropMessage(1, "장비 인벤토리 슬롯이 부족합니다.");
                              return;
                           }

                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           Item item = ii.randomizeStats((Equip)ii.getEquipById(1712004));
                           item.setGMLog(CurrentTime.getAllCurrentTime() + "에 " + c.getPlayer().getName() + "의 레벨 달성 퀘스트 보상");
                           short jobId = chr.getJob();
                           ((Equip)item).setArcLevel(5);
                           ((Equip)item).setArc(70);
                           if ((jobId < 100 || jobId >= 200)
                              && jobId != 512
                              && jobId != 1512
                              && jobId != 2512
                              && (jobId < 1100 || jobId >= 1200)
                              && !GameConstants.isAran(jobId)
                              && !GameConstants.isBlaster(jobId)
                              && !GameConstants.isDemonSlayer(jobId)
                              && !GameConstants.isMichael(jobId)
                              && !GameConstants.isKaiser(jobId)
                              && !GameConstants.isZero(jobId)
                              && !GameConstants.isArk(jobId)
                              && !GameConstants.isAdele(jobId)) {
                              if ((jobId < 200 || jobId >= 300)
                                 && !GameConstants.isFlameWizard(jobId)
                                 && !GameConstants.isEvan(jobId)
                                 && !GameConstants.isLuminous(jobId)
                                 && (jobId < 3200 || jobId >= 3300)
                                 && !GameConstants.isKinesis(jobId)
                                 && !GameConstants.isIllium(jobId)
                                 && !GameConstants.isLara(jobId)) {
                                 if (!GameConstants.isKain(jobId)
                                    && (jobId < 300 || jobId >= 400)
                                    && jobId != 522
                                    && jobId != 532
                                    && !GameConstants.isMechanic(jobId)
                                    && !GameConstants.isAngelicBuster(jobId)
                                    && (jobId < 1300 || jobId >= 1400)
                                    && !GameConstants.isMercedes(jobId)
                                    && (jobId < 3300 || jobId >= 3400)) {
                                    if ((jobId < 400 || jobId >= 500)
                                       && (jobId < 1400 || jobId >= 1500)
                                       && !GameConstants.isPhantom(jobId)
                                       && !GameConstants.isKadena(jobId)
                                       && !GameConstants.isHoyoung(jobId)
                                       && !GameConstants.isKhali(jobId)) {
                                       if (GameConstants.isDemonAvenger(jobId)) {
                                          ((Equip)item).setHp((short)1470);
                                       } else if (GameConstants.isXenon(jobId)) {
                                          ((Equip)item).setStr((short)336);
                                          ((Equip)item).setDex((short)336);
                                          ((Equip)item).setLuk((short)336);
                                       }
                                    } else {
                                       ((Equip)item).setLuk((short)700);
                                    }
                                 } else {
                                    ((Equip)item).setDex((short)700);
                                 }
                              } else {
                                 ((Equip)item).setInt((short)700);
                              }
                           } else {
                              ((Equip)item).setStr((short)700);
                           }

                           MapleInventoryManipulator.addbyItem(c, item);
                           chr.dropMessage(5, "아이템이 지급되었습니다.");
                           chr.forceCompleteQuest(quest);
                        }
                        break;
                     case 2000034:
                        if (chr.getLevel() >= 230) {
                           if (chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
                              chr.dropMessage(1, "장비 인벤토리 슬롯이 부족합니다.");
                              return;
                           }

                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           Item item = ii.randomizeStats((Equip)ii.getEquipById(1712005));
                           item.setGMLog(CurrentTime.getAllCurrentTime() + "에 " + c.getPlayer().getName() + "의 레벨 달성 퀘스트 보상");
                           short jobId = chr.getJob();
                           ((Equip)item).setArcLevel(5);
                           ((Equip)item).setArc(70);
                           if ((jobId < 100 || jobId >= 200)
                              && jobId != 512
                              && jobId != 1512
                              && jobId != 2512
                              && (jobId < 1100 || jobId >= 1200)
                              && !GameConstants.isAran(jobId)
                              && !GameConstants.isBlaster(jobId)
                              && !GameConstants.isDemonSlayer(jobId)
                              && !GameConstants.isMichael(jobId)
                              && !GameConstants.isKaiser(jobId)
                              && !GameConstants.isZero(jobId)
                              && !GameConstants.isArk(jobId)
                              && !GameConstants.isAdele(jobId)) {
                              if ((jobId < 200 || jobId >= 300)
                                 && !GameConstants.isFlameWizard(jobId)
                                 && !GameConstants.isEvan(jobId)
                                 && !GameConstants.isLuminous(jobId)
                                 && (jobId < 3200 || jobId >= 3300)
                                 && !GameConstants.isKinesis(jobId)
                                 && !GameConstants.isIllium(jobId)
                                 && !GameConstants.isLara(jobId)) {
                                 if (!GameConstants.isKain(jobId)
                                    && (jobId < 300 || jobId >= 400)
                                    && jobId != 522
                                    && jobId != 532
                                    && !GameConstants.isMechanic(jobId)
                                    && !GameConstants.isAngelicBuster(jobId)
                                    && (jobId < 1300 || jobId >= 1400)
                                    && !GameConstants.isMercedes(jobId)
                                    && (jobId < 3300 || jobId >= 3400)) {
                                    if ((jobId < 400 || jobId >= 500)
                                       && (jobId < 1400 || jobId >= 1500)
                                       && !GameConstants.isPhantom(jobId)
                                       && !GameConstants.isKadena(jobId)
                                       && !GameConstants.isHoyoung(jobId)
                                       && !GameConstants.isKhali(jobId)) {
                                       if (GameConstants.isDemonAvenger(jobId)) {
                                          ((Equip)item).setHp((short)1470);
                                       } else if (GameConstants.isXenon(jobId)) {
                                          ((Equip)item).setStr((short)336);
                                          ((Equip)item).setDex((short)336);
                                          ((Equip)item).setLuk((short)336);
                                       }
                                    } else {
                                       ((Equip)item).setLuk((short)700);
                                    }
                                 } else {
                                    ((Equip)item).setDex((short)700);
                                 }
                              } else {
                                 ((Equip)item).setInt((short)700);
                              }
                           } else {
                              ((Equip)item).setStr((short)700);
                           }

                           MapleInventoryManipulator.addbyItem(c, item);
                           chr.dropMessage(5, "아이템이 지급되었습니다.");
                           chr.forceCompleteQuest(quest);
                        }
                        break;
                     case 2000035:
                        if (chr.getLevel() >= 235) {
                           if (chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
                              chr.dropMessage(1, "장비 인벤토리 슬롯이 부족합니다.");
                              return;
                           }

                           MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                           Item item = ii.randomizeStats((Equip)ii.getEquipById(1712006));
                           item.setGMLog(CurrentTime.getAllCurrentTime() + "에 " + c.getPlayer().getName() + "의 레벨 달성 퀘스트 보상");
                           short jobId = chr.getJob();
                           ((Equip)item).setArcLevel(5);
                           ((Equip)item).setArc(70);
                           if ((jobId < 100 || jobId >= 200)
                              && jobId != 512
                              && jobId != 1512
                              && jobId != 2512
                              && (jobId < 1100 || jobId >= 1200)
                              && !GameConstants.isAran(jobId)
                              && !GameConstants.isBlaster(jobId)
                              && !GameConstants.isDemonSlayer(jobId)
                              && !GameConstants.isMichael(jobId)
                              && !GameConstants.isKaiser(jobId)
                              && !GameConstants.isZero(jobId)
                              && !GameConstants.isArk(jobId)
                              && !GameConstants.isAdele(jobId)) {
                              if ((jobId < 200 || jobId >= 300)
                                 && !GameConstants.isFlameWizard(jobId)
                                 && !GameConstants.isEvan(jobId)
                                 && !GameConstants.isLuminous(jobId)
                                 && (jobId < 3200 || jobId >= 3300)
                                 && !GameConstants.isKinesis(jobId)
                                 && !GameConstants.isIllium(jobId)
                                 && !GameConstants.isLara(jobId)) {
                                 if (!GameConstants.isKain(jobId)
                                    && (jobId < 300 || jobId >= 400)
                                    && jobId != 522
                                    && jobId != 532
                                    && !GameConstants.isMechanic(jobId)
                                    && !GameConstants.isAngelicBuster(jobId)
                                    && (jobId < 1300 || jobId >= 1400)
                                    && !GameConstants.isMercedes(jobId)
                                    && (jobId < 3300 || jobId >= 3400)) {
                                    if ((jobId < 400 || jobId >= 500)
                                       && (jobId < 1400 || jobId >= 1500)
                                       && !GameConstants.isPhantom(jobId)
                                       && !GameConstants.isKadena(jobId)
                                       && !GameConstants.isHoyoung(jobId)
                                       && !GameConstants.isKhali(jobId)) {
                                       if (GameConstants.isDemonAvenger(jobId)) {
                                          ((Equip)item).setHp((short)1470);
                                       } else if (GameConstants.isXenon(jobId)) {
                                          ((Equip)item).setStr((short)336);
                                          ((Equip)item).setDex((short)336);
                                          ((Equip)item).setLuk((short)336);
                                       }
                                    } else {
                                       ((Equip)item).setLuk((short)700);
                                    }
                                 } else {
                                    ((Equip)item).setDex((short)700);
                                 }
                              } else {
                                 ((Equip)item).setInt((short)700);
                              }
                           } else {
                              ((Equip)item).setStr((short)700);
                           }

                           MapleInventoryManipulator.addbyItem(c, item);
                           chr.dropMessage(5, "아이템이 지급되었습니다.");
                           chr.forceCompleteQuest(quest);
                        }
                  }
               }

               MapleQuest q = MapleQuest.getInstance(quest);
               if (quest == 7707 && (action == 4 || action == 5 || action == 6)) {
                  q.complete(chr, 9010000);
               }

               switch (action) {
                  case 0:
                     slea.readInt();
                     int itemid = slea.readInt();
                     q.RestoreLostItem(chr, itemid);
                     break;
                  case 1:
                     int npcxxx = slea.readInt();
                     if (!q.hasStartScript()) {
                        q.start(chr, npcxxx);
                     }
                     break;
                  case 2:
                     int npcxx = slea.readInt();
                     slea.readInt();
                     if (q.hasEndScript()) {
                        return;
                     }

                     if (slea.available() >= 4L) {
                        q.complete(chr, npcxx, slea.readInt());
                     } else {
                        q.complete(chr, npcxx);
                     }
                     break;
                  case 3:
                     if (GameConstants.canForfeit(q.getId())) {
                        q.forfeit(chr);
                     } else {
                        chr.dropMessage(1, "이 퀘스트는 포기할 수 없습니다.");
                     }
                     break;
                  case 4:
                     int npcx = slea.readInt();
                     if (c.getPlayer().isGM()) {
                     }

                     if (ScriptManager.get()._scripts.get(q.getStartscript()) != null) {
                        ScriptManager.runScript(c, q.getStartscript(), MapleLifeFactory.getNPC(npcx), null, q);
                     } else {
                        NPCScriptManager.getInstance().startQuest(c, npcx, quest);
                     }
                     break;
                  case 5:
                     int npc = slea.readInt();
                     slea.readInt();
                     if (c.getPlayer().isGM()) {
                        c.getPlayer().dropMessage(5, "완료퀘스트ID : " + quest + " 엔피시 : " + npc + " 스크립트 : " + q.getEndscript());
                     }

                     if (ScriptManager.get()._scripts.get(q.getEndscript()) != null) {
                        ScriptManager.runScript(c, q.getEndscript(), MapleLifeFactory.getNPC(npc), null, q);
                     } else {
                        NPCScriptManager.getInstance().endQuest(c, npc, quest, false);
                     }
                  case 6:
               }
            }
         }
      }
   }

   public static final void CompleteNpcSpeech(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      int quest = slea.readInt();
      if (chr != null) {
         MapleQuest q = MapleQuest.getInstance(quest);
         int npc = slea.readInt();
         byte action = slea.readByte();
         int npcObjectId = slea.readInt();
         NPCScriptManager.getInstance().CompleteNpcSpeech(c, npc, quest);
      }
   }

   public static final void Storage(PacketDecoder slea, MapleClient c, MapleCharacter chr) {
      byte mode = slea.readByte();
      if (chr != null) {
         if (c.getChannelServer().getPlayerStorage().getCharacterById(c.getPlayer().getId()) != null) {
            MapleStorage storage = chr.getStorage();
            switch (mode) {
               case 3:
                  String secondPassword = slea.readMapleAsciiString();
                  if (c.CheckSecondPassword(secondPassword)) {
                     c.getPlayer().setConversation(4);
                     c.getPlayer().getStorage().sendStorage(c, c.getPlayer().getStorageNPC());
                  } else {
                     c.getSession().writeAndFlush(StoragePacket.getStorage((byte)1));
                  }
                  break;
               case 4:
                  byte type = slea.readByte();
                  short slotbyte = (short)slea.readByteToInt();
                  short slot = storage.getSlot(MapleInventoryType.getByType(type), slotbyte);
                  Item item = storage.takeOut(slot);
                  if (item == null) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  short quantity;
                  boolean haveqty;
                  if (slea.available() >= 2L) {
                     quantity = slea.readShort();
                     haveqty = true;
                  } else {
                     quantity = 1;
                     haveqty = false;
                  }

                  if (item.getQuantity() < quantity || quantity <= 0) {
                     quantity = item.getQuantity();
                  }

                  if (!GameConstants.isRechargable(item.getItemId()) && quantity >= 1 && haveqty && item.getQuantity() > quantity) {
                     if (!MapleInventoryManipulator.checkSpace(c, item.getItemId(), quantity, item.getOwner())) {
                        storage.store(item);
                        chr.dropMessage(1, "인벤토리가 꽉 찼습니다.");
                        return;
                     }

                     short restorequantity = (short)(item.getQuantity() - quantity);
                     Item restoreitem = item.copy();
                     item.setQuantity(quantity);
                     restoreitem.setQuantity(restorequantity);
                     storage.store(restoreitem);
                  }

                  MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                  if (!MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                     storage.store(item);
                     chr.dropMessage(1, "인벤토리가 꽉 찼습니다.");
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  } else {
                     int flag = item.getFlag();
                     if (!GameConstants.isArcaneSymbol(item.getItemId()) && !GameConstants.isAuthenticSymbol(item.getItemId())) {
                        boolean karma = false;
                        if (item instanceof Equip) {
                           Equip gg = (Equip)item;
                           if (ItemFlag.KARMA_EQ.check(flag)) {
                              karma = true;
                           }
                        }

                        if (ii.isDropRestricted(item.getItemId()) || karma) {
                           if (ItemFlag.KARMA_EQ.check(flag)) {
                              item.setFlag((short)(flag - ItemFlag.KARMA_EQ.getValue()));
                           } else if (ItemFlag.KARMA_USE.check(flag)) {
                              item.setFlag((short)(flag - ItemFlag.KARMA_USE.getValue()));
                           } else if (ItemFlag.POSSIBLE_ONCE_TRADE_IN_ACCOUNT.check(flag)) {
                              item.setFlag((short)(flag - ItemFlag.POSSIBLE_ONCE_TRADE_IN_ACCOUNT.getValue()));
                           } else if (ItemFlag.KARMA_ACC_USE.check(flag)) {
                              item.setFlag((short)(flag - ItemFlag.KARMA_ACC_USE.getValue()));
                           } else if (flag == ItemFlag.None.getValue()) {
                           }
                        }
                     }

                     StringBuilder sb = new StringBuilder();
                     sb.append("창고 아이템 꺼냄 (캐릭터 : ");
                     sb.append(c.getPlayer().getName());
                     sb.append(", 계정 : ");
                     sb.append(c.getAccountName());
                     sb.append("(");
                     sb.append(c.getAccID());
                     sb.append(")");
                     sb.append(", 아이템 : ");
                     sb.append(item.getItemId());
                     sb.append(" ");
                     sb.append((int)quantity);
                     sb.append("개");
                     if (item instanceof Equip) {
                        sb.append(", 장비옵션[");
                        sb.append(((Equip)item).toString());
                        sb.append("]");
                     }

                     sb.append(")");
                     long serialNumber = 0L;
                     if (item instanceof Equip) {
                        serialNumber = ((Equip)item).getSerialNumberEquip();
                     }

                     LoggingManager.putLog(
                        new ItemLog(
                           chr, c.getChannel(), item.getItemId(), quantity, chr.getMapId(), ConsumeLogType.StorageTakeOut.getType(), 0L, serialNumber, sb
                        )
                     );
                     MapleInventoryManipulator.addFromDrop(c, item, false);
                     storage.sendTakenOut(c, GameConstants.getInventoryType(item.getItemId()));
                  }
                  break;
               case 5:
                  short slotx = slea.readShort();
                  int itemId = slea.readInt();
                  MapleInventoryType typex = GameConstants.getInventoryType(itemId);
                  short quantityx = slea.readShort();
                  ii = MapleItemInformationProvider.getInstance();
                  if (quantityx < 1) {
                     return;
                  }

                  if (itemId == 1112405) {
                     c.getPlayer().dropMessage(1, "해당 아이템은 창고에 넣을 수 없습니다.");
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                  }

                  if (storage.isFull()) {
                     c.getSession().writeAndFlush(StoragePacket.getStorageFull());
                     return;
                  }

                  if (chr.getInventory(typex).getItem(slotx) == null) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (chr.getMeso() < 100L) {
                     chr.dropMessage(1, "메소가 부족하여 창고에 아이템을 넣을 수 없습니다.");
                     storage.update(c);
                     return;
                  }

                  Item itemx = chr.getInventory(typex).getItem(slotx).copy();
                  if (GameConstants.isPet(itemx.getItemId())) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (ii.isPickupRestricted(itemx.getItemId()) && storage.findById(itemx.getItemId()) != null) {
                     c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
                     return;
                  }

                  if (itemx.getItemId() != itemId
                     || itemx.getQuantity() < quantityx && !GameConstants.isThrowingStar(itemId) && !GameConstants.isBullet(itemId)) {
                     AutobanManager.getInstance()
                        .addPoints(
                           c,
                           1000,
                           0L,
                           "Trying to store non-matching itemid ("
                              + itemId
                              + "/"
                              + itemx.getItemId()
                              + ") or quantity not in posession ("
                              + quantityx
                              + "/"
                              + itemx.getQuantity()
                              + ")"
                        );
                     return;
                  }

                  boolean merge = false;
                  int mergeqty = 0;
                  if (typex != MapleInventoryType.EQUIP) {
                     int slotmax = MapleItemInformationProvider.getInstance().getItemInformation(itemId).slotMax;
                     if (slotmax >= 100 && !GameConstants.isRechargable(itemId)) {
                        List<Item> sItems = storage.getItems();

                        for (short i = 0; i < sItems.size(); i++) {
                           Item sItem = sItems.get(i);
                           if (sItem.getItemId() == itemId) {
                              int checkqty = sItem.getQuantity() + quantityx;
                              if (checkqty <= slotmax) {
                                 mergeqty = checkqty;
                                 storage.updateQty(i, checkqty);
                                 merge = true;
                                 break;
                              }
                           }
                        }
                     }
                  }

                  if (GameConstants.isThrowingStar(itemId) || GameConstants.isBullet(itemId)) {
                     quantityx = itemx.getQuantity();
                  }

                  MapleInventoryManipulator.removeFromSlot(c, typex, slotx, quantityx, false);
                  itemx.setQuantity(quantityx);
                  chr.gainMeso(-100L, false, false, false);
                  StringBuilder sbx = new StringBuilder();
                  sbx.append("창고 아이템 보관 (캐릭터 : ");
                  sbx.append(c.getPlayer().getName());
                  sbx.append(", 계정 : ");
                  sbx.append(c.getAccountName());
                  sbx.append("(");
                  sbx.append(c.getAccID());
                  sbx.append(")");
                  sbx.append(", 아이템 : ");
                  sbx.append(itemx.getItemId());
                  sbx.append(" ");
                  sbx.append((int)quantityx);
                  sbx.append("개");
                  if (merge) {
                     sbx.append(") (창고 아이템과 병합 총 ");
                     sbx.append(mergeqty);
                     sbx.append("개");
                  }

                  if (itemx instanceof Equip) {
                     sbx.append(", 장비옵션[");
                     sbx.append(((Equip)itemx).toString());
                     sbx.append("]");
                  }

                  sbx.append(")");
                  long serialNumber = 0L;
                  if (itemx instanceof Equip) {
                     serialNumber = ((Equip)itemx).getSerialNumberEquip();
                  }

                  LoggingManager.putLog(
                     new ItemLog(
                        chr,
                        c.getChannel(),
                        itemx.getItemId(),
                        itemx.getQuantity(),
                        chr.getMapId(),
                        ConsumeLogType.StorageStore.getType(),
                        0L,
                        serialNumber,
                        sbx
                     )
                  );
                  if (!merge) {
                     storage.store(itemx);
                  }

                  storage.sendStored(c, GameConstants.getInventoryType(itemId));
                  break;
               case 6:
                  storage.arrange();
                  storage.update(c);
                  break;
               case 7:
                  long meso = slea.readLong();
                  long storageMesos = storage.getMeso();
                  long playerMesos = chr.getMeso();
                  if ((meso <= 0L || storageMesos < meso) && (meso >= 0L || playerMesos < -meso)) {
                     AutobanManager.getInstance()
                        .addPoints(
                           c,
                           1000,
                           0L,
                           "Trying to store or take out unavailable amount of mesos (" + meso + "/" + storage.getMeso() + "/" + c.getPlayer().getMeso() + ")"
                        );
                     return;
                  }

                  if (meso < 0L && storageMesos - meso < 0L) {
                     meso = -(9999999999L - storageMesos);
                     if (-meso > playerMesos) {
                        return;
                     }
                  } else if (meso > 0L && playerMesos + meso < 0L) {
                     meso = 9999999999L - playerMesos;
                     if (meso > storageMesos) {
                        return;
                     }
                  }

                  storage.setMeso(storageMesos - meso);
                  chr.gainMeso(meso, false, false, false);
                  StringBuilder sbxx = new StringBuilder();
                  sbxx.append("창고 메소 " + (meso < 0L ? "보관" : "찾음") + " (캐릭터 : ");
                  sbxx.append(c.getPlayer().getName());
                  sbxx.append(", 계정 : ");
                  sbxx.append(c.getAccountName());
                  sbxx.append("(");
                  sbxx.append(c.getAccID());
                  sbxx.append(")");
                  sbxx.append(", 메소 : ");
                  sbxx.append(Math.abs(meso));
                  sbxx.append(")");
                  LoggingManager.putLog(new ItemLog(chr, c.getChannel(), 0, 0, chr.getMapId(), ConsumeLogType.StorageStore.getType(), 0L, 0L, sbxx));
                  storage.sendMeso(c);
                  break;
               case 8:
                  storage.close();
                  c.getSession().writeAndFlush(CWvsContext.InventoryPacket.getInventoryFull());
                  c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer(), false));
                  chr.setConversation(0);
                  break;
               default:
                  System.out.println("Unhandled Storage mode : " + mode);
            }
         }
      }
   }

   public static final void NPCMoreTalk(PacketDecoder slea, MapleClient c) {
      ScriptEngineNPC scriptThread = c.getPlayer().getScriptThread();
      slea.skip(4);
      if (scriptThread != null) {
         byte lastMsg = slea.readByte();
         if (lastMsg == 10 && slea.available() >= 4L) {
            slea.skip(2);
         }

         if (lastMsg == 0 && slea.available() >= 4L) {
            slea.skip(4);
            slea.readMapleAsciiString();
         }

         byte action = slea.readByte();
         ScriptConversation sc = scriptThread.getSc();
         int selection = -1;
         if (sc != null) {
            String str = null;
            if (lastMsg == 4) {
               if (action == 0) {
                  dispose(c, sc);
                  return;
               }

               str = slea.readMapleAsciiString();
               sc.processText(str);
            } else if (lastMsg == 0) {
               if (action >= 0) {
                  sc.processSay(action);
               }
            } else if (lastMsg != 3 && lastMsg != 16) {
               if (lastMsg == 22) {
                  sc.processSay(1);
                  return;
               }

               if (lastMsg == 5 && action == 0) {
                  dispose(c, sc);
                  return;
               }

               if (action == 1) {
                  if (slea.available() >= 8L) {
                     long tempselection = slea.readLong();
                     if (tempselection > 2147483647L || tempselection < -2147483648L) {
                        dispose(c, sc);
                        return;
                     }

                     selection = (int)tempselection;
                  } else if (slea.available() >= 4L) {
                     selection = slea.readInt();
                  } else if (slea.available() > 0L) {
                     if (lastMsg == 10 && slea.available() > 1L) {
                        slea.readByte();
                     }

                     selection = slea.readByte() & 255;
                  }

                  if (selection > Integer.MAX_VALUE) {
                     dispose(c, sc);
                     return;
                  }

                  sc.processAnswer(selection);
               }
            } else if (action >= 0) {
               sc.processAnswer(action);
            }

            int type = sc.getLastmsg();
            if ((type == 6 || type == 10 || type == 17) && selection == -1) {
               dispose(c, sc);
               return;
            }

            if (action == -1 || selection < -1) {
               dispose(c, sc);
               return;
            }
         }
      } else if (scriptThread == null) {
         byte lastMsgx = slea.readByte();
         if (lastMsgx == 10 && slea.available() >= 4L) {
            slea.skip(2);
         }

         if (lastMsgx == 0 && slea.available() >= 4L) {
            slea.skip(4);
            slea.readMapleAsciiString();
         }

         if (lastMsgx == ScriptMessageType.Monologue.getType()) {
            NPCScriptManager.getInstance().action(c, (byte)1, lastMsgx, -1);
            return;
         }

         byte action = slea.readByte();
         NPCConversationManager cm = NPCScriptManager.getInstance().getCM(c);
         if (cm == null) {
            return;
         }

         if (c.getPlayer().getConversation() == 0) {
            cm.dispose();
            return;
         }

         if (cm != null) {
            cm.setLastMsg((byte)-1);
         }

         if (lastMsgx == 4) {
            if (action != 0) {
               cm.setGetText(slea.readMapleAsciiString());
               if (cm.getType() == 0) {
                  NPCScriptManager.getInstance().startQuest(c, action, lastMsgx, -1);
               } else if (cm.getType() == 1) {
                  NPCScriptManager.getInstance().endQuest(c, action, lastMsgx, -1);
               } else {
                  NPCScriptManager.getInstance().action(c, action, lastMsgx, -1);
               }
            } else {
               cm.dispose();
            }
         } else {
            int selection = -1;
            int selection2 = -1;
            if (slea.available() >= 8L) {
               long tempselection = slea.readLong();
               if (tempselection > 2147483647L || tempselection < -2147483648L) {
                  cm.dispose();
                  return;
               }

               selection = (int)tempselection;
            } else if (slea.available() >= 4L) {
               selection = slea.readInt();
            } else if (slea.available() > 0L) {
               if (GameConstants.isZero(c.getPlayer().getJob()) && lastMsgx == ScriptMessageType.AskAvatarZero.getType()) {
                  slea.skip(1);
                  selection = slea.readByte();
                  selection2 = slea.readByte();
               } else {
                  if (lastMsgx != 11 && slea.available() > 1L) {
                     slea.skip(1);
                  }

                  selection = slea.readByte();
               }
            }

            if (lastMsgx != ScriptMessageType.AskCustomMixHairAndProb.getType() && lastMsgx != ScriptMessageType.AskMixHairNew.getType()) {
               if (lastMsgx == ScriptMessageType.AskAngelicBuster.getType()) {
                  NPCScriptManager.getInstance().action(c, (byte)1, lastMsgx, selection, action);
                  return;
               }
            } else {
               boolean isAngelicBuster = slea.readByte() == 1;
               slea.skip(1);
               boolean isZeroBeta = slea.readByte() == 1;
               int nMixBaseHairColor = slea.readInt();
               int nMixAddHairColor = slea.readInt();
               int nMixHairBaseProb = slea.readInt();
               slea.readByte();
               int nMixBaseHairColor2 = slea.readInt();
               slea.readInt();
               int nMixHairBaseProb2 = slea.readInt();
               if (isAngelicBuster) {
                  c.getPlayer().setSecondBaseColor(nMixBaseHairColor);
                  c.getPlayer().setSecondAddColor(nMixAddHairColor);
                  c.getPlayer().setSecondBaseProb(nMixHairBaseProb);
                  c.getPlayer().fakeRelog();
               } else if (isZeroBeta) {
                  ZeroInfo zeroInfo = c.getPlayer().getZeroInfo();
                  if (zeroInfo != null) {
                     zeroInfo.setMixBaseHairColor(nMixBaseHairColor);
                     zeroInfo.setMixAddHairColor(nMixAddHairColor);
                     zeroInfo.setMixHairBaseProb(nMixHairBaseProb);
                     c.getPlayer().fakeRelog();
                  }
               } else {
                  c.getPlayer().setBaseColor(nMixBaseHairColor);
                  c.getPlayer().setAddColor(nMixAddHairColor);
                  c.getPlayer().setBaseProb(nMixHairBaseProb);
                  c.getPlayer().getMap().broadcastMessage(CField.updateCharLook(c.getPlayer()));
               }

               c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
               if (cm != null) {
                  cm.dispose();
               }
            }

            if (lastMsgx == 4 && selection == -1 && selection2 == -1) {
               cm.dispose();
               return;
            }

            if (selection >= -1 && action != -1) {
               if (cm == null) {
                  if (GameConstants.isZero(c.getPlayer().getJob()) && lastMsgx == ScriptMessageType.AskAvatarZero.getType()) {
                     NPCScriptManager.getInstance().action(c, action, lastMsgx, selection, selection2);
                  } else {
                     NPCScriptManager.getInstance().action(c, action, lastMsgx, selection);
                  }

                  return;
               }

               if (cm.getType() == 0) {
                  NPCScriptManager.getInstance().startQuest(c, action, lastMsgx, selection);
               } else if (cm.getType() == 1) {
                  NPCScriptManager.getInstance().endQuest(c, action, lastMsgx, selection);
               } else if (GameConstants.isZero(c.getPlayer().getJob()) && lastMsgx == ScriptMessageType.AskAvatarZero.getType()) {
                  NPCScriptManager.getInstance().action(c, action, lastMsgx, selection, selection2);
               } else {
                  NPCScriptManager.getInstance().action(c, action, lastMsgx, selection);
               }
            } else if (cm != null) {
               if ((cm.getNpc() == 9001059 || cm.getNpc() == 9001060) && c.getPlayer().getMapId() != 99300600) {
                  c.getPlayer().removeRandomPortal();
               }

               cm.dispose();
            }
         }
      }
   }

   private static void dispose(MapleClient c, ScriptConversation sc) {
      if (c != null && sc != null) {
         c.getPlayer().getScriptThread().end();
      }
   }

   public static final void repairAll(MapleClient c) {
      if (c.getPlayer().getMapId() == 240000000) {
         int price = 0;
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         Map<Equip, Integer> eqs = new HashMap<>();
         MapleInventoryType[] types = new MapleInventoryType[]{MapleInventoryType.EQUIP, MapleInventoryType.EQUIPPED};

         for (MapleInventoryType type : types) {
            for (Item item : c.getPlayer().getInventory(type).newList()) {
               if (item instanceof Equip) {
                  Equip eq = (Equip)item;
                  if (eq.getDurability() >= 0) {
                     Map<String, Integer> eqStats = ii.getEquipStats(eq.getItemId());
                     if (eqStats.containsKey("durability") && eqStats.get("durability") > 0 && eq.getDurability() < eqStats.get("durability")) {
                        double rPercentage = 100.0 - Math.ceil(eq.getDurability() * 1000.0 / (eqStats.get("durability").intValue() * 10.0));
                        eqs.put(eq, eqStats.get("durability"));
                        price += (int)Math.ceil(rPercentage * ii.getPrice(eq.getItemId()) / (ii.getReqLevel(eq.getItemId()) < 70 ? 100.0 : 1.0));
                     }
                  }
               }
            }
         }

         if (eqs.size() > 0 && c.getPlayer().getMeso() >= price) {
            c.getPlayer().gainMeso(-price, true);

            for (Entry<Equip, Integer> eqqz : eqs.entrySet()) {
               Equip ez = eqqz.getKey();
               ez.setDurability(eqqz.getValue());
               c.getPlayer().forceReAddItem(ez.copy(), ez.getPosition() < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP);
            }
         }
      }
   }

   public static final void repair(PacketDecoder slea, MapleClient c) {
      if (c.getPlayer().getMapId() == 240000000 && slea.available() >= 4L) {
         int position = slea.readInt();
         MapleInventoryType type = position < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
         Item item = c.getPlayer().getInventory(type).getItem((short)position);
         if (item != null) {
            Equip eq = (Equip)item;
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            Map<String, Integer> eqStats = ii.getEquipStats(item.getItemId());
            if (eq.getDurability() >= 0 && eqStats.containsKey("durability") && eqStats.get("durability") > 0 && eq.getDurability() < eqStats.get("durability")
               )
             {
               double rPercentage = 100.0 - Math.ceil(eq.getDurability() * 1000.0 / (eqStats.get("durability").intValue() * 10.0));
               int price = (int)Math.ceil(rPercentage * ii.getPrice(eq.getItemId()) / (ii.getReqLevel(eq.getItemId()) < 70 ? 100.0 : 1.0));
               if (c.getPlayer().getMeso() >= price) {
                  c.getPlayer().gainMeso(-price, false);
                  eq.setDurability(eqStats.get("durability"));
                  c.getPlayer().forceReAddItem(eq.copy(), type);
               }
            }
         }
      }
   }

   public static final void UpdateQuest(PacketDecoder slea, MapleClient c) {
      MapleQuest quest = MapleQuest.getInstance(slea.readInt());
      if (quest != null) {
         c.getPlayer().updateQuest(c.getPlayer().getQuest(quest), true);
      }
   }

   public static final void UseItemQuest(PacketDecoder slea, MapleClient c) {
      short slot = slea.readShort();
      int itemId = slea.readInt();
      Item item = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem(slot);
      int qid = slea.readInt();
      MapleQuest quest = MapleQuest.getInstance(qid);
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      Pair<Integer, List<Integer>> questItemInfo = null;
      boolean found = false;

      for (Item i : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
         if (i.getItemId() / 10000 == 422) {
            questItemInfo = ii.questItemInfo(i.getItemId());
            if (questItemInfo != null && questItemInfo.getLeft() == qid && questItemInfo.getRight() != null && questItemInfo.getRight().contains(itemId)) {
               found = true;
               break;
            }
         }
      }

      if (quest != null && found && item != null && item.getQuantity() > 0 && item.getItemId() == itemId) {
         int newData = slea.readInt();
         MapleQuestStatus stats = c.getPlayer().getQuestNoAdd(quest);
         if (stats != null && stats.getStatus() == 1) {
            stats.setCustomData(String.valueOf(newData));
            c.getPlayer().updateQuest(stats, true);
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, slot, (short)1, false);
         }
      }
   }

   public static final void ArcaneRiverQuickPath(PacketDecoder p, MapleClient c) {
      MapleCharacter user = c.getPlayer();
      if (user != null) {
         int mode = p.readInt();
         int questIndex = p.readInt();
         if (mode == 1) {
            boolean isNotPartyQuest = p.readByte() > 0;
            String questName = "arcaneRiverQuickPath";
            if (isNotPartyQuest) {
               questName = questName + questIndex;
               ScriptManager.runScript(c, questName, MapleLifeFactory.getNPC(3003146));
            } else {
               switch (questIndex) {
                  case 0:
                  case 1:
                  case 2:
                  case 3:
                     ScriptManager.runScript(c, questName + "1" + questIndex, MapleLifeFactory.getNPC(3003146));
               }
            }
         }
      }
   }

   public static final void RPSGame(PacketDecoder slea, MapleClient c) {
      if (slea.available() != 0L && c.getPlayer() != null && c.getPlayer().getMap() != null && c.getPlayer().getMap().containsNPC(9000019)) {
         byte mode = slea.readByte();
         switch (mode) {
            case 0:
            case 5:
               if (c.getPlayer().getRPS() != null) {
                  c.getPlayer().getRPS().reward(c);
               }

               if (c.getPlayer().getMeso() >= 1000L) {
                  c.getPlayer().setRPS(new RockPaperScissors(c, mode));
               } else {
                  c.getSession().writeAndFlush(CField.getRPSMode((byte)8, -1, -1, -1));
               }
               break;
            case 1:
               if (c.getPlayer().getRPS() == null || !c.getPlayer().getRPS().answer(c, slea.readByte())) {
                  c.getSession().writeAndFlush(CField.getRPSMode((byte)13, -1, -1, -1));
               }
               break;
            case 2:
               if (c.getPlayer().getRPS() == null || !c.getPlayer().getRPS().timeOut(c)) {
                  c.getSession().writeAndFlush(CField.getRPSMode((byte)13, -1, -1, -1));
               }
               break;
            case 3:
               if (c.getPlayer().getRPS() == null || !c.getPlayer().getRPS().nextRound(c)) {
                  c.getSession().writeAndFlush(CField.getRPSMode((byte)13, -1, -1, -1));
               }
               break;
            case 4:
               if (c.getPlayer().getRPS() != null) {
                  c.getPlayer().getRPS().dispose(c);
               } else {
                  c.getSession().writeAndFlush(CField.getRPSMode((byte)13, -1, -1, -1));
               }
         }
      } else {
         if (c.getPlayer() != null && c.getPlayer().getRPS() != null) {
            c.getPlayer().getRPS().dispose(c);
         }
      }
   }

   public static final void selectMirrorDungeon(PacketDecoder slea, MapleClient c) {
      String path = slea.readMapleAsciiString();
      if (c.getPlayer().isSelectDungeon()) {
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
      } else {
         c.removeClickedNPC();
         NPCScriptManager.getInstance().dispose(c);
         c.getSession().writeAndFlush(CWvsContext.enableActions(c.getPlayer()));
         Invocable iv = getInvocable("npc/1531010.js", c, true);
         if (iv == null) {
            iv = getInvocable("npc/notcoded.js", c, true);
         }

         ScriptEngine scriptengine = (ScriptEngine)iv;
         NPCConversationManager cm = new NPCConversationManager(c, 1531010, -1, (byte)-1, iv);
         scriptengine.put("cm", cm);
         c.getPlayer().setConversation(1);
         c.setClickedNPC();
         c.getSession().writeAndFlush(CField.UIPacket.OnSetMirrorDungeonInfo(true));

         try {
            iv.invokeFunction(path);
         } catch (ScriptException | NoSuchMethodException var7) {
         }
      }
   }

   public static Invocable getInvocable(String path, MapleClient c, boolean npc) {
      path = "scripts/" + (DBConfig.isGanglim ? "Royal/" : "Jin/") + path;
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
               System.out.println("스크립트 핸들 오류 발생");
               var13.printStackTrace();
               return null;
            }
         }
      } else if (c != null && npc) {
      }

      return (Invocable)engine;
   }
}
