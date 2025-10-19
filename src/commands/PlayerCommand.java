package commands;

import constants.GameConstants;
import constants.QuestExConstants;
import constants.ServerConstants;
import constants.devtempConstants.DummyCharacterName;
import database.DBConfig;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import network.auction.AuctionServer;
import network.game.GameServer;
import network.models.CField;
import network.models.CWvsContext;
import network.shop.CashShopServer;
import objects.fields.Field;
import objects.fields.MapleMapObject;
import objects.fields.MapleMapObjectType;
import objects.fields.Portal;
import objects.fields.child.minigame.yutgame.Field_MultiYutGame;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.mobskills.MobSkillFactory;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryManipulator;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.users.MapleCharacter;
import objects.users.MapleClient;
import objects.users.MapleStat;
import objects.users.PlayerStats;
import objects.users.enchant.ItemFlag;
import objects.users.enchant.ItemStateFlag;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.ArrayMap;
import objects.utils.CurrentTime;
import objects.utils.Pair;
import objects.utils.Randomizer;
import scripting.EventInstanceManager;
import scripting.NPCScriptManager;
import scripting.newscripting.ScriptManager;

public class PlayerCommand implements Command {
   @Override
   public void execute(MapleClient c, String[] splitted) throws Exception, IllegalCommandSyntaxException {
      GameServer cserv = c.getChannelServer();
      MapleCharacter chr = c.getPlayer();
      if (splitted[0].equals("힘") || splitted[0].equals("str")) {
         int str = Integer.parseInt(splitted[1]);
         PlayerStats stat = c.getPlayer().getStat();
         if (stat.getStr() + str <= 32767 && c.getPlayer().getRemainingAp() >= str && c.getPlayer().getRemainingAp() >= 0 && str >= 0) {
            c.getPlayer().getStat().setStr((short)(stat.getStr() + str), c.getPlayer());
            c.getPlayer().setRemainingAp((short)(c.getPlayer().getRemainingAp() - str));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
            c.getPlayer().updateSingleStat(MapleStat.STR, stat.getStr());
         } else {
            c.getPlayer().dropMessage(5, "오류가 발생했습니다.");
         }
      } else if (splitted[0].equals("인트") || splitted[0].equals("int")) {
         int int_ = Integer.parseInt(splitted[1]);
         PlayerStats stat = c.getPlayer().getStat();
         if (stat.getInt() + int_ <= 32767 && c.getPlayer().getRemainingAp() >= int_ && c.getPlayer().getRemainingAp() >= 0 && int_ >= 0) {
            c.getPlayer().getStat().setInt((short)(stat.getInt() + int_), c.getPlayer());
            c.getPlayer().setRemainingAp((short)(c.getPlayer().getRemainingAp() - int_));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
            c.getPlayer().updateSingleStat(MapleStat.INT, stat.getInt());
         } else {
            c.getPlayer().dropMessage(5, "오류가 발생했습니다.");
         }
      } else if (splitted[0].equals("덱스") || splitted[0].equals("dex")) {
         int dex = Integer.parseInt(splitted[1]);
         PlayerStats stat = c.getPlayer().getStat();
         if (stat.getDex() + dex <= 32767 && c.getPlayer().getRemainingAp() >= dex && c.getPlayer().getRemainingAp() >= 0 && dex >= 0) {
            c.getPlayer().getStat().setDex((short)(stat.getDex() + dex), c.getPlayer());
            c.getPlayer().setRemainingAp((short)(c.getPlayer().getRemainingAp() - dex));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
            c.getPlayer().updateSingleStat(MapleStat.DEX, stat.getDex());
         } else {
            c.getPlayer().dropMessage(5, "오류가 발생했습니다.");
         }
      } else if (splitted[0].equals("럭") || splitted[0].equals("luk")) {
         int luk = Integer.parseInt(splitted[1]);
         PlayerStats stat = c.getPlayer().getStat();
         if (stat.getLuk() + luk <= 32767 && c.getPlayer().getRemainingAp() >= luk && c.getPlayer().getRemainingAp() >= 0 && luk >= 0) {
            c.getPlayer().getStat().setLuk((short)(stat.getLuk() + luk), c.getPlayer());
            c.getPlayer().setRemainingAp((short)(c.getPlayer().getRemainingAp() - luk));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
            c.getPlayer().updateSingleStat(MapleStat.LUK, stat.getLuk());
         } else {
            c.getPlayer().dropMessage(5, "오류가 발생했습니다.");
         }
      } else if (!splitted[0].equals("체력") && !splitted[0].equals("hp")) {
         if (!splitted[0].equals("마나") && !splitted[0].equals("mp")) {
            if (!splitted[0].equals("보스") && !splitted[0].equals("boss")) {
               if (splitted[0].equals("인벤초기화")) {
                  Map<Pair<Short, Short>, MapleInventoryType> eqs = new ArrayMap<>();
                  if (!splitted[1].equals("모두")) {
                     if (splitted[1].equals("장착")) {
                        for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED)) {
                           Equip equip = (Equip)item;
                           if ((equip.getItemState() & ItemStateFlag.LOCK.getValue()) == 0) {
                              eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.EQUIPPED);
                           }
                        }
                     } else if (splitted[1].equals("장비")) {
                        for (Item itemx : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                           Equip equip = (Equip)itemx;
                           if ((equip.getItemState() & ItemStateFlag.LOCK.getValue()) == 0) {
                              eqs.put(new Pair<>(itemx.getPosition(), itemx.getQuantity()), MapleInventoryType.EQUIP);
                           }
                        }
                     } else if (splitted[1].equals("소비")) {
                        for (Item itemxx : c.getPlayer().getInventory(MapleInventoryType.USE)) {
                           if ((itemxx.getFlag() & ItemFlag.LOCK.getValue()) == 0 && itemxx.getItemId() != 2431307 && itemxx.getItemId() != 2432128) {
                              eqs.put(new Pair<>(itemxx.getPosition(), itemxx.getQuantity()), MapleInventoryType.USE);
                           }
                        }
                     } else if (splitted[1].equals("설치")) {
                        for (Item itemxxx : c.getPlayer().getInventory(MapleInventoryType.SETUP)) {
                           eqs.put(new Pair<>(itemxxx.getPosition(), itemxxx.getQuantity()), MapleInventoryType.SETUP);
                        }
                     } else if (splitted[1].equals("기타")) {
                        for (Item itemxxx : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
                           eqs.put(new Pair<>(itemxxx.getPosition(), itemxxx.getQuantity()), MapleInventoryType.ETC);
                        }
                     } else if (splitted[1].equals("캐시")) {
                        for (Item itemxxx : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                           eqs.put(new Pair<>(itemxxx.getPosition(), itemxxx.getQuantity()), MapleInventoryType.CASH);
                        }
                     } else if (splitted[1].equals("치장")) {
                        for (Item itemxxx : c.getPlayer().getInventory(MapleInventoryType.CASH_EQUIP)) {
                           eqs.put(new Pair<>(itemxxx.getPosition(), itemxxx.getQuantity()), MapleInventoryType.CASH_EQUIP);
                        }
                     } else {
                        c.getPlayer().dropMessage(6, "[모두/장착/장비/소비/설치/기타/캐시/치장]");
                     }
                  } else {
                     for (MapleInventoryType type : MapleInventoryType.values()) {
                        for (Item itemxxx : c.getPlayer().getInventory(type)) {
                           eqs.put(new Pair<>(itemxxx.getPosition(), itemxxx.getQuantity()), type);
                        }
                     }
                  }

                  for (Entry<Pair<Short, Short>, MapleInventoryType> eq : eqs.entrySet()) {
                     MapleInventoryManipulator.removeFromSlot(c, eq.getValue(), (Short)eq.getKey().left, (Short)eq.getKey().right, false, false);
                  }
               } else if (splitted[0].equals("랙") || splitted[0].equals("렉") || splitted[0].equals("lag")) {
                  NPCScriptManager.getInstance().dispose(c);
                  if (DBConfig.isGanglim && c.isOverseasUser()) {
                     c.getPlayer().dropMessage(5, "The lag has been resolved");
                     return;
                  }

                  c.getPlayer().dropMessage(5, "렉 풀기가 완료 되었습니다.");
               } else if (!splitted[0].equals("보스격파알림") && !splitted[0].equals("bossclearmessage")) {
                  if (!splitted[0].equals("마을12313131버튼") && !splitted[0].equals("tow1231313nbtn")) {
                     if (!splitted[0].equals("광장") && !splitted[0].equals("헤네시스") && !splitted[0].equals("마을") && !splitted[0].equals("town")) {
                        if (splitted[0].equals("작방")) {
                           int jobid = c.getPlayer().getJob();
                           if (jobid == 0
                              || jobid == 1000
                              || jobid == 2000
                              || jobid == 2001
                              || jobid == 2002
                              || jobid == 2003
                              || jobid == 2004
                              || jobid == 3000
                              || jobid == 3001
                              || jobid == 5000
                              || jobid == 6000
                              || jobid == 6001
                              || jobid == 10112 && c.getPlayer().getMapId() == ServerConstants.StartMap) {
                              c.getPlayer().dropMessage(5, "[시스템] 초보자는 작방으로 이동 할 수 없습니다.");
                              return;
                           }

                           if (c.getPlayer().getMapId() == 910340500
                              || c.getPlayer().getMapId() == 240050200
                              || c.getPlayer().getMapId() == 272000600
                              || c.getPlayer().getMapId() == 921160400) {
                              c.getPlayer().dropMessage(5, "[시스템] 보스레이드 도중에는 이동 할 수 없습니다.");
                              return;
                           }

                           if (c.getPlayer().getHungryMuto() != null) {
                              c.getPlayer().dropMessage(5, "현재는 이동 할 수 없습니다.");
                              return;
                           }

                           Field target = c.getChannelServer().getMapFactory().getMap(910001001);
                           Portal targetPortal = null;
                           if (splitted.length > 1) {
                              try {
                                 targetPortal = target.getPortal(Integer.parseInt(splitted[1]));
                              } catch (IndexOutOfBoundsException var20) {
                                 c.getPlayer().dropMessage(5, "없는 포탈의 값이 있습니다.");
                              }
                           }

                           if (targetPortal == null) {
                              targetPortal = target.getPortal(0);
                           }

                           if (c.getPlayer().getStat().getHp() != 0L) {
                              EventInstanceManager eim = c.getPlayer().getEventInstance();
                              if (eim != null && eim.hasEventTimer()) {
                                 eim.stopEventTimer();
                                 eim.dispose();
                              }

                              c.getPlayer().changeMap(target, targetPortal);
                           } else {
                              c.getPlayer().dropMessage(5, "[알림] 죽어있는 상태에서는 작방으로 이동할 수 없습니다.");
                           }
                        } else if (splitted[0].equals("블라썸")) {
                           if (!DBConfig.isGanglim) {
                              ScriptManager.runScript(c, "npc_9062508", MapleLifeFactory.getNPC(9062508));
                              return;
                           }
                        } else if (splitted[0].equals("자유시장")) {
                           int jobidx = c.getPlayer().getJob();
                           if (jobidx == 0
                              || jobidx == 1000
                              || jobidx == 2000
                              || jobidx == 2001
                              || jobidx == 2002
                              || jobidx == 2003
                              || jobidx == 2004
                              || jobidx == 3000
                              || jobidx == 3001
                              || jobidx == 5000
                              || jobidx == 6000
                              || jobidx == 6001) {
                              c.getPlayer().dropMessage(5, "초보자는 자유시장으로 이동 할 수 없습니다.");
                              return;
                           }

                           if (c.getPlayer().getHungryMuto() != null) {
                              c.getPlayer().dropMessage(5, "현재는 이동 할 수 없습니다.");
                              return;
                           }

                           if (c.getPlayer().getStat().getHp() <= 0L) {
                              c.getPlayer().dropMessage(5, "[알림] 죽어있는 상태에서는 @마을 명령어를 사용할 수 없습니다.");
                           }

                           EventInstanceManager eim = c.getPlayer().getEventInstance();
                           if (eim != null && eim.hasEventTimer()) {
                              eim.stopEventTimer();
                              eim.dispose();
                              c.getPlayer().setClock(0);
                              c.getPlayer().setDeathCount(0);
                           }

                           Field targetx = c.getChannelServer().getMapFactory().getMap(15);
                           Portal targetPortalx = null;
                           if (splitted.length > 1) {
                              try {
                                 targetPortalx = targetx.getPortal(Integer.parseInt(splitted[1]));
                              } catch (IndexOutOfBoundsException var18) {
                                 c.getPlayer().dropMessage(5, "없는 포탈의 값이 있습니다.");
                              } catch (NumberFormatException var19) {
                              }
                           }

                           if (targetPortalx == null) {
                              targetPortalx = targetx.getPortal(0);
                           }

                           c.getPlayer().changeMap(targetx, targetPortalx);
                        } else if (!splitted[0].equals("도움말") && !splitted[0].equals("명령어") && !splitted[0].equals("commands")) {
                           if (splitted[0].equals("추천인")) {
                              NPCScriptManager.getInstance().dispose(c);
                              NPCScriptManager.getInstance().start(c, 9010031);
                           } else if (splitted[0].equals("유니온")) {
                              if (DBConfig.isGanglim) {
                                 int vv = c.getPlayer().getItemQuantity(4310229, false);
                                 c.getPlayer().updateOneInfo(QuestExConstants.UnionCoin.getQuestID(), "point", String.valueOf(vv));
                              }

                              NPCScriptManager.getInstance().dispose(c);
                              NPCScriptManager.getInstance().start(c, 9010106);
                           } else if (splitted[0].equals("엑어빌") || splitted[0].equals("엑스트라")) {
                              ScriptManager.runScript(c, "test", MapleLifeFactory.getNPC(9010017));
                           } else if (splitted[0].equals("황금마차") || splitted[0].equals("goldenchariot")) {
                              ScriptManager.runScript(c, "q100750s", MapleLifeFactory.getNPC(9000029));
                           } else if (splitted[0].equals("각인석판")) {
                              ScriptManager.runScript(c, "spinOff_UIOpen", MapleLifeFactory.getNPC(1530020));
                           } else if (splitted[0].equals("봉인해방")) {
                              ScriptManager.runScript(c, "rita_library", MapleLifeFactory.getNPC(250000));
                           } else if (splitted[0].equals("전직")) {
                              NPCScriptManager.getInstance().dispose(c);
                              NPCScriptManager.getInstance().start(c, 9062000);
                           } else if (splitted[0].equals("버프")) {
                              c.getPlayer().giveDonatorBuff();
                              c.getPlayer().checkExtraAbility();
                              c.getPlayer().checkLevelBuff();
                              c.getPlayer().checkPitchBlackBuff();
                              c.getPlayer().checkLiberationStats();
                              c.getPlayer().checkImprintedStone();
                           } else if (splitted[0].equals("동접")) {
                              if (DBConfig.isGanglim) {
                                 long lastTime = c.getPlayer().getOneInfoQuestLong(1234566, "lvc");
                                 if (lastTime != 0L && System.currentTimeMillis() - lastTime < 30000L && !c.getPlayer().isGM()) {
                                    c.getPlayer().send(CField.chatMsg(8, "해당 기능은 30초에 한 번 이용 가능합니다."));
                                    return;
                                 }

                                 double rate = ServerConstants.connectedRate;
                                 c.getPlayer().updateOneInfo(1234566, "lvc", String.valueOf(System.currentTimeMillis()));
                                 c.getPlayer().send(CField.chatMsg(8, "강림 메이플에 접속중인 유저들입니다."));
                                 int count = 0;
                                 int ch = 1;

                                 for (GameServer gameServer : GameServer.getAllInstances()) {
                                    int size = (int)(gameServer.getPlayerCountInChannel() * rate);
                                    count += size;
                                    if (ch == 2) {
                                       c.getPlayer().send(CField.chatMsg(5, gameServer.getServerName() + " Ch. 20세이상 - " + size + "명 "));
                                    } else {
                                       c.getPlayer()
                                          .send(CField.chatMsg(5, gameServer.getServerName() + " Ch. " + (ch == 1 ? ch : ch - 1) + " - " + size + "명 "));
                                    }

                                    ch++;
                                 }

                                 int auctionCount = 0;

                                 for (MapleCharacter p : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
                                    if (p != null) {
                                       auctionCount++;
                                    }
                                 }

                                 auctionCount = (int)(auctionCount * rate);
                                 c.getPlayer().send(CField.chatMsg(5, "옥션 - " + auctionCount + "명 "));
                                 count += auctionCount;
                                 c.getPlayer().send(CField.chatMsg(8, "강림 메이플 총 유저 접속 수 : " + count));
                              } else {
                                 SimpleDateFormat sdf = new SimpleDateFormat("yyyy년MM월dd일 HH시mm분ss초");
                                 Calendar CAL = new GregorianCalendar(Locale.KOREA);
                                 String fDate = sdf.format(CAL.getTime());
                                 long lastTime = c.getPlayer().getOneInfoQuestLong(1234566, "lvc");
                                 if (lastTime != 0L && System.currentTimeMillis() - lastTime < 30000L) {
                                    c.getPlayer().dropMessage(6, "@동접 명령어는 30초에 한 번 사용 가능합니다.");
                                    return;
                                 }

                                 c.getPlayer().dropMessage(6, "강림 동시 접속자 정보 (현재 시간 : " + fDate + ")");
                                 double rate = ServerConstants.connectedRate;
                                 c.getPlayer().updateOneInfo(1234566, "lvc", String.valueOf(System.currentTimeMillis()));
                                 String pick = DummyCharacterName.getConnected(rate);
                                 int count = 0;

                                 for (GameServer gameServer : GameServer.getAllInstances()) {
                                    int size = (int)(gameServer.getPlayerCountInChannel() * rate);
                                    count += size;
                                 }

                                 int csCount = 0;

                                 for (MapleCharacter px : new ArrayList<>(CashShopServer.getPlayerStorage().getAllCharacters())) {
                                    if (px != null) {
                                       csCount++;
                                    }
                                 }

                                 csCount = (int)(csCount * rate);
                                 count += csCount;
                                 int auctionCount = 0;

                                 for (MapleCharacter pxx : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
                                    if (pxx != null) {
                                       auctionCount++;
                                    }
                                 }

                                 auctionCount = (int)(auctionCount * rate);
                                 count += auctionCount;
                                 c.getPlayer().dropMessage(5, pick);
                                 c.getPlayer().dropMessage(6, "총 " + count + "명이 강림과 함께하고 있습니다.");
                              }
                           } else if (splitted[0].equals("online")) {
                              if (!DBConfig.isGanglim) {
                                 return;
                              }

                              long lastTime = c.getPlayer().getOneInfoQuestLong(1234566, "lvc");
                              if (lastTime != 0L && System.currentTimeMillis() - lastTime < 30000L && !c.getPlayer().isGM()) {
                                 c.getPlayer().send(CField.chatMsg(8, "This function is available once every 30 seconds."));
                                 return;
                              }

                              double rate = ServerConstants.connectedRate;
                              c.getPlayer().updateOneInfo(1234566, "lvc", String.valueOf(System.currentTimeMillis()));
                              c.getPlayer().send(CField.chatMsg(8, "< Ganglim >"));
                              int count = 0;
                              int ch = 1;

                              for (GameServer gameServer : GameServer.getAllInstances()) {
                                 int size = (int)(gameServer.getPlayerCountInChannel() * rate);
                                 count += size;
                                 if (ch == 2) {
                                    c.getPlayer().send(CField.chatMsg(5, gameServer.getServerName() + " Ch. 20↑ - " + size + " "));
                                 } else {
                                    c.getPlayer().send(CField.chatMsg(5, gameServer.getServerName() + " Ch. " + (ch == 1 ? ch : ch - 1) + " - " + size + " "));
                                 }

                                 ch++;
                              }

                              int auctionCount = 0;

                              for (MapleCharacter pxxx : new ArrayList<>(AuctionServer.getPlayerStorage().getAllCharacters())) {
                                 if (pxxx != null) {
                                    auctionCount++;
                                 }
                              }

                              auctionCount = (int)(auctionCount * rate);
                              c.getPlayer().send(CField.chatMsg(5, "Auction - " + auctionCount + " "));
                              count += auctionCount;
                              c.getPlayer().send(CField.chatMsg(8, "Number of users logged in to Ganglim : " + count));
                           } else if (!splitted[0].equals("칭찬홍보")) {
                              if (splitted[0].equals("뿌리기")) {
                                 if (DBConfig.isGanglim) {
                                    return;
                                 }

                                 if (c.getPlayer().getInventory(MapleInventoryType.CASH).findById(5121060) == null) {
                                    c.getPlayer().dropMessage(1, "경험치 50% 뿌리기 아이템이 존재하지 않습니다.");
                                    return;
                                 }

                                 c.getPlayer().getMap().startMapEffect(c.getPlayer().getName() + "님의 경험치 50% 뿌리기입니다.", 5121060);
                                 MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                                 int buff = ii.getStateChangeItem(5121060);
                                 if (buff != 0) {
                                    for (MapleCharacter mChar : c.getPlayer().getMap().getCharactersThreadsafe()) {
                                       ii.getItemEffect(buff).applyTo(mChar);
                                    }
                                 }

                                 c.getPlayer().gainItem(5121060, -1, false, 0L, "");
                              } else if (splitted[0].equals("스텟리로드")) {
                                 c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
                                 c.getPlayer().dropMessage(5, "[알림] 스텟리로드를 완료 하였습니다.");
                              } else if (splitted[0].equals("스우스우")) {
                                 c.getPlayer().giveDebuff(SecondaryStatFlag.ReverseInput, MobSkillFactory.getMobSkill(132, 1));
                              } else if (splitted[0].equals("데일리")) {
                                 c.getPlayer()
                                    .updateInfoQuest(
                                       16700, "count=300;day=" + c.getPlayer().getDailyGift().getDailyDay() + ";date=" + CurrentTime.getCurrentTime2()
                                    );
                              } else if (splitted[0].equals("언데드")) {
                                 c.getPlayer().giveDebuff(SecondaryStatFlag.Undead, MobSkillFactory.getMobSkill(133, 17));
                              } else if (splitted[0].equals("혼란")) {
                                 c.getPlayer().giveDebuff(SecondaryStatFlag.ReverseInput, MobSkillFactory.getMobSkill(132, 1));
                              } else if (!splitted[0].equals("스킬마스터") && !splitted[0].equals("skillmaster")) {
                                 if (splitted[0].equals("팬텀")) {
                                    NPCScriptManager.getInstance().start(c, 1520046, "SkillSteal", true);
                                 } else if (splitted[0].equals("사다리")) {
                                    if (c.getPlayer().showLadderGameResult) {
                                       c.getPlayer().showLadderGameResult = false;
                                       c.getPlayer().dropMessage(5, "사다리 게임 알림이 취소되었습니다.");
                                    } else {
                                       c.getPlayer().showLadderGameResult = true;
                                       c.getPlayer().dropMessage(5, "사다리 게임 알림이 등록되었습니다.");
                                    }
                                 } else if (splitted[0].equals("몬스터")) {
                                    if (!DBConfig.isGanglim) {
                                       return;
                                    }

                                    MapleMonster mob = null;

                                    for (MapleMapObject monstermo : c.getPlayer()
                                       .getMap()
                                       .getMapObjectsInRange(c.getPlayer().getPosition(), 100000.0, Arrays.asList(MapleMapObjectType.MONSTER))) {
                                       mob = (MapleMonster)monstermo;
                                       if (mob.isAlive()) {
                                          c.getPlayer().dropMessage(6, "몬스터 정보 :  " + mob.toString());
                                          break;
                                       }
                                    }

                                    if (mob == null) {
                                       c.getPlayer().dropMessage(6, "주변에 몬스터가 없습니다.");
                                    }
                                 } else if (splitted[0].equals("명성치알림")) {
                                    if (!DBConfig.isGanglim) {
                                       return;
                                    }

                                    if (c.getPlayer().getKeyValue("show_honor") != null && !c.getPlayer().getKeyValue("show_honor").equals("1")) {
                                       c.getPlayer().setKeyValue("show_honor", "1");
                                    } else {
                                       c.getPlayer().setKeyValue("show_honor", "0");
                                    }

                                    c.getPlayer().dropMessage(5, "설정이 완료되었습니다.");
                                 } else if (splitted[0].equals("올스탯")) {
                                    if (!DBConfig.isGanglim) {
                                       return;
                                    }

                                    if (c.getPlayer().getKeyValue("BackStat") == null) {
                                       c.getPlayer().setKeyValue("BackStat", "0");
                                    }

                                    if (splitted.length < 2) {
                                       c.getPlayer().dropMessage(5, "사용법 : @올스탯 수치");
                                       c.getPlayer().dropMessage(5, "현재 감소한 올스탯% : " + c.getPlayer().getKeyValue("BackStat") + "%");
                                       return;
                                    }

                                    String strNum = splitted[1].replaceAll("[^0-9]", "");
                                    int strNumInt = Integer.parseInt(strNum);
                                    if (strNumInt < 0 || strNumInt > 1000) {
                                       c.getPlayer().dropMessage(5, "0~1000 사이의 수치를 입력해 주세요.");
                                       return;
                                    }

                                    c.getPlayer().setKeyValue("BackStat", strNum);
                                    c.getPlayer().setBonusCTSStat();
                                    c.getPlayer().dropMessage(5, "현재 감소한 올스탯% : " + c.getPlayer().getKeyValue("BackStat") + "%");
                                 } else if (DBConfig.isGanglim && splitted[0].equals("창고")) {
                                    if (!DBConfig.isGanglim) {
                                       return;
                                    }

                                    c.removeClickedNPC();
                                    NPCScriptManager.getInstance().dispose(c);
                                    NPCScriptManager.getInstance().start(c, 1002005);
                                 } else if (splitted[0].equals("데스카운트")) {
                                    if (!DBConfig.isGanglim) {
                                       return;
                                    }

                                    c.getPlayer().dropMessage(5, "남은 데스카운트 수 : " + c.getPlayer().getDeathCount());
                                 } else if (splitted[0].equals("일퀘")) {
                                    if (!DBConfig.isGanglim) {
                                       return;
                                    }

                                    c.removeClickedNPC();
                                    NPCScriptManager.getInstance().dispose(c);
                                    NPCScriptManager.getInstance().start(c, 9062284, "일퀘", true);
                                 } else if (DBConfig.isGanglim && GameConstants.royalSimpleOpenNpc.containsKey(splitted[0])) {
                                    if (!DBConfig.isGanglim) {
                                       return;
                                    }

                                    c.removeClickedNPC();
                                    NPCScriptManager.getInstance().dispose(c);
                                    NPCScriptManager.getInstance().start(c, GameConstants.royalSimpleOpenNpc.get(splitted[0]));
                                 } else if (splitted[0].equals("일일보상")) {
                                    if (!DBConfig.isGanglim) {
                                       return;
                                    }

                                    c.removeClickedNPC();
                                    NPCScriptManager.getInstance().dispose(c);
                                    NPCScriptManager.getInstance().start(c, 1052206, "핫타임일퀘", true);
                                 } else if (splitted[0].equals("아획")) {
                                    if (!DBConfig.isGanglim) {
                                       return;
                                    }

                                    StringBuilder String = new StringBuilder();
                                    StringBuilder String1 = new StringBuilder();
                                    StringBuilder String2 = new StringBuilder();
                                    String.append("아이템 획득량 정보 (최대 400%) : 현재 : ");
                                    int max = 400;
                                    double dropBuff = Math.min((double)max, c.getPlayer().getStat().dropBuff);
                                    double extraBuff = c.getPlayer().getStat().extraDropBuff - 100.0;
                                    double dropRatef = dropBuff + extraBuff;
                                    String.append(dropRatef);
                                    String.append("%");
                                    c.getPlayer().dropMessage(5, String.toString());
                                    String1.append("(기본 100%이며 400%를 초과해도 효과를 받을 수 없습니다)");
                                    c.getPlayer().dropMessage(5, String1.toString());
                                    String2.append("(보스 몬스터를 대상으로는 최대 300%만 적용됩니다)");
                                    c.getPlayer().dropMessage(5, String2.toString());
                                 } else if (splitted[0].equals("메획")) {
                                    if (!DBConfig.isGanglim && !c.getPlayer().isGM()) {
                                       return;
                                    }

                                    StringBuilder String = new StringBuilder();
                                    StringBuilder String1 = new StringBuilder();
                                    String.append("메소 획득량 정보 (최대 300%) : 현재 : ");
                                    String.append(c.getPlayer().getStat().mesoBuff);
                                    String.append("%");
                                    c.getPlayer().dropMessage(5, String.toString());
                                    String1.append("(기본 100%이며 300%를 초과해도 효과를 받을 수 없습니다.)");
                                    c.getPlayer().dropMessage(5, String1.toString());
                                 } else if (splitted[0].equals("팬텀리셋")) {
                                    c.getPlayer().invokeJobMethod("removeAllStolenSkill");
                                 } else if (splitted[0].equals("픽파킷디버그")) {
                                    c.getPlayer().invokeJobMethod("debugPickPocket");
                                 } else if (!splitted[0].equals("보스인트로") && !splitted[0].equals("bossintro")) {
                                    if (splitted[0].equals("코디")) {
                                       c.removeClickedNPC();
                                       NPCScriptManager.getInstance().dispose(c);
                                       NPCScriptManager.getInstance().start(c, 9000172);
                                    } else if (splitted[0].equals("창고")) {
                                       c.removeClickedNPC();
                                       NPCScriptManager.getInstance().dispose(c);
                                       NPCScriptManager.getInstance().start(c, 2400002);
                                    } else if (splitted[0].equals("뉴비기부함")) {
                                       ScriptManager.runScript(c, "Donation", MapleLifeFactory.getNPC(250000));
                                    } else if (splitted[0].equals("킬미")) {
                                       long hp = chr.getStat().getCurrentMaxHp(chr);
                                       chr.addHP(-hp);
                                    } else if (splitted[0].equals("출석")) {
                                       ScriptManager.runScript(c, "RestPoint", MapleLifeFactory.getNPC(9010033));
                                    } else if (splitted[0].equals("일일미션")) {
                                       ScriptManager.runScript(c, "DailyMission", MapleLifeFactory.getNPC(9010033));
                                    } else if (splitted[0].equals("초강") || splitted[0].equals("초월강화")) {
                                       c.removeClickedNPC();
                                       NPCScriptManager.getInstance().dispose(c);
                                       NPCScriptManager.getInstance().start(c, 2400007);
                                    } else if (splitted[0].equals("전용강화")) {
                                       c.removeClickedNPC();
                                       NPCScriptManager.getInstance().dispose(c);
                                       NPCScriptManager.getInstance().start(c, 3004520);
                                    }
                                 } else if (DBConfig.isGanglim) {
                                    if (c.getPlayer().getIsSkipIntro()) {
                                       c.getPlayer().setIsSkipIntro(false);
                                       if (c.getPlayer().getParty() != null) {
                                          if (c.getPlayer().getParty().getLeader().getId() == c.getPlayer().getId()) {
                                             c.getPlayer().getParty().getLeader().setSkipIntro(false);
                                             c.getPlayer().getParty().getMemberById(c.getPlayer().getId()).setSkipIntro(false);
                                          } else {
                                             c.getPlayer().getParty().getMemberById(c.getPlayer().getId()).setSkipIntro(false);
                                          }
                                       }

                                       if (!c.isOverseasUser()) {
                                          c.getPlayer().dropMessage(5, "[보스 인트로] 스킵 기능이 비활성화되었습니다.");
                                       } else {
                                          c.getPlayer().dropMessage(5, "[Boss Intro] Skip function is Disabled");
                                       }
                                    } else {
                                       c.getPlayer().setIsSkipIntro(true);
                                       if (c.getPlayer().getParty() != null) {
                                          if (c.getPlayer().getParty().getLeader().getId() == c.getPlayer().getId()) {
                                             c.getPlayer().getParty().getLeader().setSkipIntro(true);
                                             c.getPlayer().getParty().getMemberById(c.getPlayer().getId()).setSkipIntro(true);
                                          } else {
                                             c.getPlayer().getParty().getMemberById(c.getPlayer().getId()).setSkipIntro(true);
                                          }
                                       }

                                       if (!c.isOverseasUser()) {
                                          c.getPlayer().dropMessage(5, "[보스 인트로] 스킵 기능이 활성화되었습니다.");
                                       } else {
                                          c.getPlayer().dropMessage(5, "[Boss Intro] Skip function is Enabled");
                                       }
                                    }
                                 }
                              } else {
                                 if (GameConstants.isYetiPinkBean(c.getPlayer().getJob())) {
                                    c.getPlayer().dropMessage(5, "[알림] 핑크빈과 예티는 이용할 수 없는 기능입니다.");
                                    return;
                                 }

                                 for (int i = 0; i < c.getPlayer().getJob() % 10 + 1; i++) {
                                    int job = i + 1 == c.getPlayer().getJob() % 10 + 1
                                       ? c.getPlayer().getJob() - c.getPlayer().getJob() % 100
                                       : c.getPlayer().getJob() - (i + 1);
                                    if (c.getPlayer().getJob() >= 330 && c.getPlayer().getJob() <= 332) {
                                       if (job == 300) {
                                          job = 301;
                                       }
                                    } else if (c.getPlayer().getJob() >= 530 && c.getPlayer().getJob() <= 532 && job == 500) {
                                       job = 501;
                                    }

                                    if (GameConstants.isDemonAvenger(c.getPlayer().getJob()) && job == 3100) {
                                       job = 3101;
                                    }

                                    c.getPlayer().maxskill(job);
                                 }

                                 int div = c.getPlayer().getJob() < 1000 ? 100 : 1000;
                                 int jobx = c.getPlayer().getJob();
                                 if (GameConstants.isKain(jobx)) {
                                    div = 6003;
                                 } else if (GameConstants.isKadena(jobx)) {
                                    div = 6002;
                                 } else if (GameConstants.isAngelicBuster(jobx)) {
                                    div = 6001;
                                 } else if (GameConstants.isEvan(jobx)) {
                                    div = 2001;
                                 } else if (GameConstants.isMercedes(jobx)) {
                                    div = 2002;
                                 } else if (GameConstants.isDemonSlayer(jobx) || GameConstants.isDemonAvenger(jobx)) {
                                    div = 3001;
                                 } else if (GameConstants.isPhantom(jobx)) {
                                    div = 2003;
                                 } else if (GameConstants.isLuminous(jobx)) {
                                    div = 2004;
                                 } else if (GameConstants.isXenon(jobx)) {
                                    div = 3002;
                                 } else if (GameConstants.isEunWol(jobx)) {
                                    div = 2005;
                                 } else if (GameConstants.isIllium(jobx)) {
                                    div = 15000;
                                 } else if (GameConstants.isArk(jobx)) {
                                    div = 15001;
                                 } else if (GameConstants.isAdele(jobx)) {
                                    div = 15002;
                                 } else if (GameConstants.isKhali(jobx)) {
                                    div = 15003;
                                 } else if (GameConstants.isHoyoung(jobx)) {
                                    div = 16000;
                                 } else if (GameConstants.isLara(jobx)) {
                                    div = 16001;
                                 } else if (GameConstants.isCannon(jobx)) {
                                    div = 501;
                                 }

                                 c.getPlayer().maxskill(c.getPlayer().getJob() / div * div);
                                 c.getPlayer().resetSP(0);
                                 c.getPlayer().maxskill(c.getPlayer().getJob());
                                 if (DBConfig.isGanglim && c.isOverseasUser()) {
                                    c.getPlayer().dropMessage(5, "All Skills Master is Completed");
                                    return;
                                 }

                                 c.getPlayer().dropMessage(5, "[알림] 스킬마스터가 완료 되었습니다.");
                              }
                           }
                        } else if (DBConfig.isGanglim) {
                           if (!c.isOverseasUser()) {
                              c.getPlayer().dropMessage(-21, "[ Ganglim ] 강림 명령어 안내");
                              c.getPlayer().dropMessage(5, "@힘, @덱스, @인트, @럭 ex)@럭 50 < 스탯 포인트를 명령어로 사용 >");
                              c.getPlayer().dropMessage(5, "@몬스터 < 해당 맵 몬스터에 대한 정보 확인 >");
                              c.getPlayer().dropMessage(5, "@렉 < 상호작용이 불가능한 상황일 때 사용 >");
                              c.getPlayer().dropMessage(5, "@스킬마스터 < 모든 스킬레벨을 맥스레벨까지 올림 >");
                              c.getPlayer().dropMessage(5, "@동접 < 서버의 접속 유저 수 확인 >");
                              c.getPlayer().dropMessage(5, "@마을 < 마을로 이동 >");
                              c.getPlayer().dropMessage(5, "@이동 < 이동 NPC를 호출 >");
                              c.getPlayer().dropMessage(5, "@편의 < 편의 NPC를 호출 >");
                              c.getPlayer().dropMessage(5, "@상점 < 상점 NPC를 호출 >");
                              c.getPlayer().dropMessage(5, "@코디 < 코디 NPC를 호출 >");
                              c.getPlayer().dropMessage(5, "@유니온 < 유니온 NPC를 호출 >");
                              c.getPlayer().dropMessage(5, "@보스 < 보스이동 NPC를 호출 >");
                              c.getPlayer().dropMessage(5, "@일퀘 < 일일퀘스트 NPC를 호출 >");
                              c.getPlayer().dropMessage(5, "@랭킹 < 랭킹게시판 NPC를 호출 >");
                              c.getPlayer().dropMessage(5, "@엑어빌, @엑스트라 < 엑스트라 어빌리티 시스템 NPC를 호출 >");
                              c.getPlayer().dropMessage(5, "@각인석판 < 각인석판 시스템 NPC를 호출 >");
                              c.getPlayer().dropMessage(5, "@보스인트로 < 보스인트로 스킵 기능 ON/OFF >");
                              c.getPlayer().dropMessage(5, "@보스격파알림 < 보스격파 알림 메세지 기능 ON/OFF >");
                              c.getPlayer().dropMessage(5, "@마을버튼 < 우측 마을이동 버튼 ON/OFF >");
                              c.getPlayer().dropMessage(5, "@황금마차 < 황금마차 이벤트에 참여합니다 >");
                              c.getPlayer().dropMessage(-22, "~할말 < 전체 채팅 >");
                           } else {
                              c.getPlayer().dropMessage(-21, "[ Ganglim ] User Command Notes");
                              c.getPlayer().dropMessage(5, "@str, @dex, @int, @luk ex)@luk 50 < Using Stat Points as Commands >");
                              c.getPlayer().dropMessage(5, "@lag < Use in situations where interaction is not possible >");
                              c.getPlayer().dropMessage(5, "@skillmaster < Raise all skill levels to max level >");
                              c.getPlayer().dropMessage(5, "@online < Confirm login user's number of server >");
                              c.getPlayer().dropMessage(5, "@town < Go To Town >");
                              c.getPlayer().dropMessage(5, "@bossintro < Boss intro skip function ON/OFF >");
                              c.getPlayer().dropMessage(5, "@bossclearmessage < Boss clear notification ON/OFF >");
                              c.getPlayer().dropMessage(5, "@townbtn < Town Move Button ON/OFF >");
                              c.getPlayer().dropMessage(5, "@goldenchariot < Check GoldenChariot EVENT >");
                              c.getPlayer().dropMessage(-22, "~msg < Global Chat >");
                           }
                        } else {
                           c.getPlayer().dropMessage(5, "사용가능한 명령어는 다음과 같습니다 :");
                           c.getPlayer().dropMessage(5, "힘, @덱스, @인트, @럭 <찍을 수치> : 해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.");
                           c.getPlayer().dropMessage(5, "렉 : 공격 등 채팅외에 아무것도 안될때 사용하세요.");
                           c.getPlayer().dropMessage(5, "광장 : 광장으로 이동합니다.");
                           c.getPlayer().dropMessage(5, "인벤초기화 : 인벤초기화 탭 모두/장착/장비/소비/설치/기타/캐시/치장");
                           c.getPlayer().dropMessage(5, "스킬마스터 : 현재 자신의 직업 스킬을 마스터합니다.");
                           c.getPlayer().dropMessage(5, "동접 : 현재 강림에 접속중인 플레이어의 수를 나타냅니다.");
                           c.getPlayer().dropMessage(5, "추천인 : 추천인 엔피씨를 오픈합니다.");
                           c.getPlayer().dropMessage(5, "뉴비기부함 : 뉴비기부함 엔피씨를 오픈합니다.");
                           c.getPlayer().dropMessage(5, "엑어빌, 엑스트라 : 엑스트라 어빌리티 시스템 엔피씨를 오픈합니다.");
                           c.getPlayer().dropMessage(5, "봉인해방 : 스탯 봉인 해방 시스템 엔피씨를 오픈합니다.");
                           c.getPlayer().dropMessage(5, "각인석판 : 각인석 시스템 엔피씨를 오픈합니다.");
                           c.getPlayer().dropMessage(5, "버프 : 구매한 후원 버프를 발동시킵니다.");
                           c.getPlayer().dropMessage(5, "유니온 : 유니온 엔피씨를 오픈합니다.");
                           c.getPlayer().dropMessage(5, "팬텀리셋 : 훔친 스킬을 모두 삭제합니다.");
                           c.getPlayer().dropMessage(5, "코디 : 코디 엔피씨를 오픈합니다.");
                           c.getPlayer().dropMessage(5, "창고 : 창고 엔피씨를 오픈합니다.");
                           c.getPlayer().dropMessage(5, "블라썸 : 강림 블라썸 엔피시를 오픈합니다.");
                           c.getPlayer().dropMessage(5, "출석 : 누적 휴식 시간에 따른 보상을 수령할 수 있습니다.");
                           c.getPlayer().dropMessage(5, "일일미션 : 일일미션을 확인합니다(계정당 1회 클리어).");
                           c.getPlayer().dropMessage(5, "초강, 초월강화 : 일반 초월강화 엔피씨를 오픈합니다.");
                           c.getPlayer().dropMessage(5, "전용강화 : 제네시스, 제로 초월강화 엔피씨를 오픈합니다.");
                        }
                     } else {
                        int jobidxx = c.getPlayer().getJob();
                        if (DBConfig.isGanglim
                           && (
                              jobidxx == 0
                                 || jobidxx == 1000
                                 || jobidxx == 2000
                                 || jobidxx == 2001
                                 || jobidxx == 2002
                                 || jobidxx == 2003
                                 || jobidxx == 2004
                                 || jobidxx == 3000
                                 || jobidxx == 3001
                                 || jobidxx == 5000
                                 || jobidxx == 6000
                                 || jobidxx == 6001
                                 || jobidxx == 16000
                                 || jobidxx == 15000
                                 || jobidxx == 15001
                                 || jobidxx == 15002
                                 || jobidxx == 10112 && c.getPlayer().getMapId() == ServerConstants.StartMap
                           )) {
                           c.getPlayer().dropMessage(5, "[시스템] 초보자는 광장으로 이동 할 수 없습니다.");
                           return;
                        }

                        if (c.getPlayer().getMapId() == 910340500
                           || c.getPlayer().getMapId() == 240050200
                           || c.getPlayer().getMapId() == 272000600
                           || c.getPlayer().getMapId() == 921160400) {
                           c.getPlayer().dropMessage(5, "[시스템] 보스레이드 도중에는 이동 할 수 없습니다.");
                           return;
                        }

                        if (c.getPlayer().getHungryMuto() != null) {
                           c.getPlayer().dropMessage(5, "현재는 이동 할 수 없습니다.");
                           return;
                        }

                        if (c.getPlayer().getMap() instanceof Field_MultiYutGame) {
                           c.getPlayer().dropMessage(5, "현재는 이동 할 수 없습니다.");
                           return;
                        }

                        if (DBConfig.isGanglim && c.getPlayer().getMapId() == ServerConstants.StartMap) {
                           c.getPlayer().dropMessage(5, "현재는 이동 할 수 없습니다.");
                           return;
                        }

                        Field targetxx = c.getChannelServer().getMapFactory().getMap(ServerConstants.TownMap);
                        Portal targetPortalxx = null;
                        if (splitted.length > 1) {
                           try {
                              targetPortalxx = targetxx.getPortal(Integer.parseInt(splitted[1]));
                           } catch (IndexOutOfBoundsException var21) {
                              c.getPlayer().dropMessage(5, "없는 포탈의 값이 있습니다.");
                           }
                        }

                        if (targetPortalxx == null) {
                           targetPortalxx = targetxx.getPortal(0);
                        }

                        boolean fromBoss = false;
                        if (c.getPlayer().getStat().getHp() != 0L) {
                           EventInstanceManager eimx = c.getPlayer().getEventInstance();
                           if (eimx != null) {
                              fromBoss = true;
                              eimx.stopEventTimer();
                              if (c.getPlayer().getParty() != null) {
                                 List<MapleCharacter> players = c.getPlayer().getMap().getCharactersThreadsafe();
                                 c.getPlayer().getParty().getPartyMemberList().forEach(pxxxx -> {
                                    if (pxxxx.isOnline()) {
                                       for (MapleCharacter player : players) {
                                          if (player.getId() == pxxxx.getId()) {
                                             player.warp(ServerConstants.TownMap);
                                             player.setEventInstance(null);
                                             player.setRegisterTransferFieldTime(0L);
                                             player.setRegisterTransferField(0);
                                          }
                                       }
                                    }
                                 });
                              }

                              eimx.dispose();
                           }

                           if (c.getPlayer().getQuestStatus(2000001) == 1 && c.getPlayer().getOneInfo(2000001, "yut") == null) {
                              c.getPlayer().updateOneInfo(2000001, "yut", "1");
                           }

                           if (c.getPlayer().getQuestStatus(501525) == 1) {
                              if (c.getPlayer().getOneInfo(501525, "value") == null) {
                                 c.getPlayer().updateOneInfo(501525, "value", "1");
                              }

                              if (c.getPlayer().getOneInfoQuestInteger(501524, "state") < 2) {
                                 c.getPlayer().updateOneInfo(501524, "state", "2");
                              }
                           }

                           if (!fromBoss) {
                              c.getPlayer().changeMap(targetxx, targetPortalxx);
                           }
                        } else if (DBConfig.isGanglim) {
                           c.getPlayer().dropMessage(5, "[알림] 사망 상태에서는 광장으로 이동할 수 없습니다.");
                        } else {
                           c.getPlayer().dropMessage(5, "[알림] 죽어있는 상태에서는 @마을 명령어를 사용할 수 없습니다.");
                        }
                     }
                  } else {
                     if (!DBConfig.isGanglim) {
                        return;
                     }

                     int value = c.getPlayer().getOneInfoQuestInteger(1234567, "offTownButton");
                     c.getPlayer().updateOneInfo(1234567, "offTownButton", value == 0 ? "1" : "0");
                     if (c.getPlayer().getEventInstance() == null
                        && c.getPlayer().getMap().getId() != ServerConstants.TownMap
                        && c.getPlayer().getOneInfoQuestInteger(1234567, "offTownButton") == 0) {
                        c.getPlayer().send(CField.UIPacket.openUI(1113));
                        if (!c.isOverseasUser()) {
                           c.getPlayer().dropMessage(5, "마을버튼 기능이 활성화 되었습니다");
                        } else {
                           c.getPlayer().dropMessage(5, "Town Button function has been Enabled");
                        }
                     } else {
                        c.getPlayer().send(CField.UIPacket.closeUI(1113));
                        if (!c.isOverseasUser()) {
                           c.getPlayer().dropMessage(5, "마을버튼 기능이 비활성화 되었습니다");
                        } else {
                           c.getPlayer().dropMessage(5, "Town Button function is Disabled");
                        }
                     }
                  }
               } else {
                  int questId = QuestExConstants.CustomQuests.getQuestID();
                  String questKey = "BossMessage";
                  boolean isBroadcastBossKill = c.getPlayer().getOneInfoQuestInteger(questId, questKey) == 0;
                  if (isBroadcastBossKill) {
                     c.getPlayer().updateOneInfo(questId, questKey, "1");
                     if (!c.isOverseasUser()) {
                        c.getPlayer().dropMessage(5, "보스격파 알림 메세지 기능이 비활성화 되었습니다");
                     } else {
                        c.getPlayer().dropMessage(5, "Boss clear notification message is disabled");
                     }
                  } else {
                     c.getPlayer().updateOneInfo(questId, questKey, "0");
                     if (!c.isOverseasUser()) {
                        c.getPlayer().dropMessage(5, "보스격파 알림 메세지 기능이 활성화 되었습니다");
                     } else {
                        c.getPlayer().dropMessage(5, "Boss clear notification message is activated");
                     }
                  }
               }
            } else if (DBConfig.isGanglim) {
               c.removeClickedNPC();
               NPCScriptManager.getInstance().dispose(c);
               NPCScriptManager.getInstance().start(c, 1052206, "보스이동", true);
            }
         } else {
            if (!DBConfig.isGanglim) {
               return;
            }

            int mp = Integer.parseInt(splitted[1]);
            if (c.getPlayer().getStat().getMaxHp() + mp <= 500000L && c.getPlayer().getRemainingAp() >= mp && c.getPlayer().getRemainingAp() >= 0 && mp >= 0) {
               long maxmp = c.getPlayer().getStat().getMaxMp();
               if (c.getPlayer().getHpApUsed() >= 10000 || maxmp >= 500000L) {
                  c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
                  return;
               }

               int jobxx = c.getPlayer().getJob();

               for (int i = 0; i < mp; i++) {
                  int delta = 0;
                  if (GameConstants.isNovice(jobxx)) {
                     delta = Randomizer.rand(6, 8);
                  } else {
                     if (jobxx >= 3100 && jobxx <= 3112) {
                        c.getPlayer().send(CWvsContext.enableActions(c.getPlayer()));
                        return;
                     }

                     if ((jobxx < 200 || jobxx > 232) && !GameConstants.isEvan(jobxx) && (jobxx < 3200 || jobxx > 3212) && (jobxx < 1200 || jobxx > 1212)) {
                        if ((jobxx < 300 || jobxx > 322)
                           && (jobxx < 400 || jobxx > 434)
                           && (jobxx < 500 || jobxx > 532)
                           && (jobxx < 3200 || jobxx > 3212)
                           && (jobxx < 3500 || jobxx > 3512)
                           && (jobxx < 1300 || jobxx > 1312)
                           && (jobxx < 1400 || jobxx > 1412)
                           && (jobxx < 1500 || jobxx > 1512)
                           && (jobxx < 2300 || jobxx > 2312)) {
                           if ((jobxx < 100 || jobxx > 132) && (jobxx < 1100 || jobxx > 1112) && (jobxx < 2000 || jobxx > 2112)) {
                              delta = Randomizer.rand(50, 100);
                           } else {
                              delta = Randomizer.rand(6, 9);
                           }
                        } else {
                           delta = Randomizer.rand(10, 12);
                        }
                     } else {
                        delta = Randomizer.rand(38, 40);
                     }
                  }

                  maxmp = Math.min(500000L, Math.abs(maxmp + delta));
                  c.getPlayer().setHpApUsed((short)(c.getPlayer().getHpApUsed() + 1));
                  c.getPlayer().getStat().setMaxMp(maxmp, c.getPlayer());
                  c.getPlayer().setRemainingAp((short)(c.getPlayer().getRemainingAp() - 1));
                  c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
                  c.getPlayer().updateSingleStat(MapleStat.MAXMP, c.getPlayer().getStat().getMaxMp());
                  c.getPlayer().updateOneInfo(100710, "ap_mp", String.valueOf(c.getPlayer().getOneInfoQuestInteger(100710, "ap_mp") + delta));
               }
            } else {
               c.getPlayer().dropMessage(5, "오류가 발생했습니다.");
            }
         }
      } else {
         if (!DBConfig.isGanglim) {
            return;
         }

         int hp = Integer.parseInt(splitted[1]);
         if (c.getPlayer().getStat().getMaxHp() + hp <= 500000L && c.getPlayer().getRemainingAp() >= hp && c.getPlayer().getRemainingAp() >= 0 && hp >= 0) {
            long maxhp = c.getPlayer().getStat().getMaxHp();
            if (c.getPlayer().getHpApUsed() >= 10000 || maxhp >= 500000L) {
               return;
            }

            int jobxx = c.getPlayer().getJob();

            for (int i = 0; i < hp; i++) {
               int delta = 0;
               if (GameConstants.isNovice(jobxx)) {
                  delta = Randomizer.rand(8, 12);
               } else if ((jobxx < 100 || jobxx > 132) && (jobxx < 3200 || jobxx > 3212) && (jobxx < 1100 || jobxx > 1112) && (jobxx < 3100 || jobxx > 3112)) {
                  if ((jobxx < 200 || jobxx > 232) && !GameConstants.isEvan(jobxx)) {
                     if ((jobxx < 300 || jobxx > 322)
                        && (jobxx < 400 || jobxx > 434)
                        && (jobxx < 1300 || jobxx > 1312)
                        && (jobxx < 1400 || jobxx > 1412)
                        && (jobxx < 3300 || jobxx > 3312)
                        && (jobxx < 2300 || jobxx > 2312)) {
                        if ((jobxx < 510 || jobxx > 512) && (jobxx < 1510 || jobxx > 1512)) {
                           if ((jobxx < 500 || jobxx > 532) && (jobxx < 3500 || jobxx > 3512) && jobxx != 1500) {
                              if (jobxx >= 1200 && jobxx <= 1212) {
                                 delta = Randomizer.rand(15, 21);
                              } else if (jobxx >= 2000 && jobxx <= 2112) {
                                 delta = Randomizer.rand(38, 42);
                              } else {
                                 delta = Randomizer.rand(50, 100);
                              }
                           } else {
                              delta = Randomizer.rand(18, 22);
                           }
                        } else {
                           delta = Randomizer.rand(28, 32);
                        }
                     } else {
                        delta = Randomizer.rand(16, 20);
                     }
                  } else {
                     delta = Randomizer.rand(10, 20);
                  }
               } else {
                  delta = Randomizer.rand(36, 42);
               }

               maxhp = Math.min(500000L, Math.abs(maxhp + delta));
               c.getPlayer().setHpApUsed((short)(c.getPlayer().getHpApUsed() + 1));
               c.getPlayer().getStat().setMaxHp(maxhp, c.getPlayer());
               c.getPlayer().setRemainingAp((short)(c.getPlayer().getRemainingAp() - 1));
               c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
               c.getPlayer().updateSingleStat(MapleStat.MAXHP, c.getPlayer().getStat().getMaxHp());
               c.getPlayer().updateOneInfo(100710, "ap_hp", String.valueOf(c.getPlayer().getOneInfoQuestInteger(100710, "ap_hp") + delta));
            }
         } else {
            c.getPlayer().dropMessage(5, "오류가 발생했습니다.");
         }
      }
   }

   @Override
   public CommandDefinition[] getDefinition() {
      return DBConfig.isGanglim
         ? new CommandDefinition[]{
            new CommandDefinition("체력", "<올릴AP>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("마나", "<올릴AP>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("힘", "<올릴AP>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("인트", "<올릴AP>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("덱스", "<올릴AP>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("럭", "<올릴AP>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("랙", "", "공격 등 채팅외에 아무것도 안될때 사용하세요.", 0),
            new CommandDefinition("렉", "", "공격 등 채팅외에 아무것도 안될때 사용하세요.", 0),
            new CommandDefinition("보스격파알림", "", "헬 모드를 제외한 보스 격파 메시지 ON/OFF", 0),
            new CommandDefinition("명령어", "", "유저 명령어를 출력합니다.", 0),
            new CommandDefinition("광장", "", "광장으로 이동합니다.", 0),
            new CommandDefinition("헤네시스", "", "광장으로 이동합니다.", 0),
            new CommandDefinition("자유시장", "", "광장으로 이동합니다.", 0),
            new CommandDefinition("인벤초기화", "모두/장착/장비/소비/설치/기타/캐시", "해당 탭의 인벤토리를 모두 비워버립니다.", 0),
            new CommandDefinition("도움말", "", "유저 명령어를 출력합니다.", 0),
            new CommandDefinition("마을", "", "마을이동", 0),
            new CommandDefinition("마을버튼", "", "마을버튼", 0),
            new CommandDefinition("추천인", "", "추천인 엔피시를 호출합니다.", 0),
            new CommandDefinition("유니온", "", "유니온 엔피시를 호출합니다.", 0),
            new CommandDefinition("경매장", "", "경매장", 0),
            new CommandDefinition("스텟리로드", "", "스텟리로드", 0),
            new CommandDefinition("스킬마스터", "", "", 0),
            new CommandDefinition("동접", "", "서버의 현재 동시 접속자수를 확인합니다.", 0),
            new CommandDefinition("팬텀", "", "팬텀이 스킬을 훔칠 수 있는 편의기능입니다.", 0),
            new CommandDefinition("버프", "", "구매한 후원 버프를 발동시킵니다.", 0),
            new CommandDefinition("사다리", "", "사다리 알림을 켜거나 끕니다.", 0),
            new CommandDefinition("몬스터", "", "몬스터 정보를 확인합니다.", 0),
            new CommandDefinition("명성치알림", "", "명성치 알림을 ON/OFF 합니다.", 0),
            new CommandDefinition("올스탯", "", "버프 수치의 올스탯 효과를 수정합니다.", 0),
            new CommandDefinition("창고", "", "창고 NPC를 엽니다.", 0),
            new CommandDefinition("데스카운트", "", "현재 데스카운트를 확인합니다.", 0),
            new CommandDefinition("이동", "", "이동 NPC를 엽니다.", 0),
            new CommandDefinition("상점", "", "상점 NPC를 엽니다.", 0),
            new CommandDefinition("편의", "", "편의 NPC를 엽니다..", 0),
            new CommandDefinition("코디", "", "코디 NPC를 엽니다.", 0),
            new CommandDefinition("홍보", "", "홍보 NPC를 엽니다.", 0),
            new CommandDefinition("후원", "", "후원 NPC를 엽니다.", 0),
            new CommandDefinition("랭킹", "", "랭킹 NPC를 엽니다.", 0),
            new CommandDefinition("레벨보상", "", "레벨보상 NPC를 엽니다.", 0),
            new CommandDefinition("유니온", "", "유니온 NPC를 엽니다.", 0),
            new CommandDefinition("제작", "", "제작 NPC를 엽니다.", 0),
            new CommandDefinition("일퀘", "", "일퀘 NPC를 엽니다.", 0),
            new CommandDefinition("보스", "", "보스 NPC를 엽니다.", 0),
            new CommandDefinition("일일보상", "", "일일보상 NPC를 엽니다.", 0),
            new CommandDefinition("아획", "", "아이템 드롭률을 확인합니다.", 0),
            new CommandDefinition("메획", "", "메소 드롭률을 확인합니다.", 0),
            new CommandDefinition("엑어빌", "", "엑어빌", 0),
            new CommandDefinition("엑스트라", "", "엑스트라", 0),
            new CommandDefinition("각인석판", "", "각인석판", 0),
            new CommandDefinition("팬텀리셋", "", "모든 훔친 스킬을 초기화합니다.", 0),
            new CommandDefinition("보스인트로", "", "보스 인트로를 스킵합니다.", 0),
            new CommandDefinition("황금마차", "", "황금마차 이벤트에 참여합니다", 0),
            new CommandDefinition("commands", "", "Check User Commands", 0),
            new CommandDefinition("online", "", "Confirm login user's number of server", 0),
            new CommandDefinition("hp", "<AP>", "This stat can be increased instead of a mouse click", 0),
            new CommandDefinition("mp", "<AP>", "This stat can be increased instead of a mouse click", 0),
            new CommandDefinition("str", "<AP>", "This stat can be increased instead of a mouse click", 0),
            new CommandDefinition("int", "<AP>", "This stat can be increased instead of a mouse click", 0),
            new CommandDefinition("dex", "<AP>", "This stat can be increased instead of a mouse click", 0),
            new CommandDefinition("luk", "<AP>", "This stat can be increased instead of a mouse click", 0),
            new CommandDefinition("lag", "", "Use it when you can't do anything other than chatting, such as attacking.", 0),
            new CommandDefinition("skillmaster", "", "", 0),
            new CommandDefinition("town", "", "", 0),
            new CommandDefinition("bossintro", "", "", 0),
            new CommandDefinition("townbtn", "", "", 0),
            new CommandDefinition("goldenchariot", "", "", 0),
            new CommandDefinition("bossclearmessage", "", "", 0),
            new CommandDefinition("픽파킷디버그", "", "", 0)
         }
         : new CommandDefinition[]{
            new CommandDefinition("힘", "<올릴양>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("인트", "<올릴양>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("덱스", "<올릴양>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("럭", "<올릴양>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("랙", "", "공격 등 채팅외에 아무것도 안될때 사용하세요.", 0),
            new CommandDefinition("렉", "", "공격 등 채팅외에 아무것도 안될때 사용하세요.", 0),
            new CommandDefinition("명령어", "", "유저 명령어를 출력합니다.", 0),
            new CommandDefinition("광장", "", "해당 서버의 광장인 헤네시스로 이동합니다.", 0),
            new CommandDefinition("헤네시스", "", "해당 서버의 광장인 헤네시스로 이동합니다.", 0),
            new CommandDefinition("자유시장", "", "자유시장으로 이동합니다.", 0),
            new CommandDefinition("인벤초기화", "모두/장착/장비/소비/설치/기타/캐시", "해당 탭의 인벤토리를 모두 비워버립니다.", 0),
            new CommandDefinition("도움말", "", "유저 명령어를 출력합니다.", 0),
            new CommandDefinition("마을", "", "마을이동", 0),
            new CommandDefinition("블라썸", "", "블라썸엔피씨", 0),
            new CommandDefinition("추천인", "", "추천인", 0),
            new CommandDefinition("뉴비기부함", "", "뉴비기부함", 0),
            new CommandDefinition("엑어빌", "", "엑어빌", 0),
            new CommandDefinition("엑스트라", "", "엑스트라", 0),
            new CommandDefinition("봉인해방", "", "봉인해방", 0),
            new CommandDefinition("각인석판", "", "각인석판", 0),
            new CommandDefinition("경매장", "", "경매장", 0),
            new CommandDefinition("스텟리로드", "", "스텟리로드", 0),
            new CommandDefinition("스킬마스터", "", "", 0),
            new CommandDefinition("유니온", "", "유니온", 0),
            new CommandDefinition("동접", "", "", 0),
            new CommandDefinition("버프", "", "구매한 후원 버프를 발동시킵니다.", 0),
            new CommandDefinition("뿌리기", "", "구매한 경험치 50% 뿌리기를 사용합니다.", 0),
            new CommandDefinition("팬텀", "", "팬텀이 스킬을 훔칠 수 있는 편의기능입니다.", 0),
            new CommandDefinition("사다리", "", "사다리 알림을 켜거나 끕니다.", 0),
            new CommandDefinition("팬텀리셋", "", "모든 훔친 스킬을 초기화합니다.", 0),
            new CommandDefinition("코디", "", "코디 NPC를 엽니다.", 0),
            new CommandDefinition("창고", "", "창고 NPC를 엽니다.", 0),
            new CommandDefinition("출석", "", "출석 NPC를 엽니다.", 0),
            new CommandDefinition("일일미션", "", "일일미션 NPC를 엽니다.", 0),
            new CommandDefinition("킬미", "", "", 0),
            new CommandDefinition("초강", "", "", 0),
            new CommandDefinition("초월강화", "", "", 0),
            new CommandDefinition("전용강화", "", "", 0)
         };
   }

   public static CommandDefinition[] getUserDefinition() {
      return DBConfig.isGanglim
         ? new CommandDefinition[]{
            new CommandDefinition("체력", "<올릴AP>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("마나", "<올릴AP>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("힘", "<올릴AP>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("인트", "<올릴AP>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("덱스", "<올릴AP>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("럭", "<올릴AP>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("랙", "", "공격 등 채팅외에 아무것도 안될때 사용하세요.", 0),
            new CommandDefinition("렉", "", "공격 등 채팅외에 아무것도 안될때 사용하세요.", 0),
            new CommandDefinition("보스격파알림", "", "헬 모드를 제외한 보스 격파 메시지 ON/OFF", 0),
            new CommandDefinition("명령어", "", "유저 명령어를 출력합니다.", 0),
            new CommandDefinition("광장", "", "광장으로 이동합니다.", 0),
            new CommandDefinition("헤네시스", "", "광장으로 이동합니다.", 0),
            new CommandDefinition("자유시장", "", "광장으로 이동합니다.", 0),
            new CommandDefinition("인벤초기화", "모두/장착/장비/소비/설치/기타/캐시", "해당 탭의 인벤토리를 모두 비워버립니다.", 0),
            new CommandDefinition("도움말", "", "유저 명령어를 출력합니다.", 0),
            new CommandDefinition("마을", "", "마을이동", 0),
            new CommandDefinition("마을버튼", "", "마을버튼", 0),
            new CommandDefinition("추천인", "", "추천인 엔피시를 호출합니다.", 0),
            new CommandDefinition("유니온", "", "유니온 엔피시를 호출합니다.", 0),
            new CommandDefinition("경매장", "", "경매장", 0),
            new CommandDefinition("스텟리로드", "", "스텟리로드", 0),
            new CommandDefinition("스킬마스터", "", "", 0),
            new CommandDefinition("동접", "", "서버의 현재 동시 접속자수를 확인합니다.", 0),
            new CommandDefinition("팬텀", "", "팬텀이 스킬을 훔칠 수 있는 편의기능입니다.", 0),
            new CommandDefinition("버프", "", "구매한 후원 버프를 발동시킵니다.", 0),
            new CommandDefinition("사다리", "", "사다리 알림을 켜거나 끕니다.", 0),
            new CommandDefinition("몬스터", "", "몬스터 정보를 확인합니다.", 0),
            new CommandDefinition("명성치알림", "", "명성치 알림을 ON/OFF 합니다.", 0),
            new CommandDefinition("올스탯", "", "버프 수치의 올스탯 효과를 수정합니다.", 0),
            new CommandDefinition("창고", "", "창고 NPC를 엽니다.", 0),
            new CommandDefinition("데스카운트", "", "현재 데스카운트를 확인합니다.", 0),
            new CommandDefinition("이동", "", "이동 NPC를 엽니다.", 0),
            new CommandDefinition("상점", "", "상점 NPC를 엽니다.", 0),
            new CommandDefinition("편의", "", "편의 NPC를 엽니다..", 0),
            new CommandDefinition("코디", "", "코디 NPC를 엽니다.", 0),
            new CommandDefinition("홍보", "", "홍보 NPC를 엽니다.", 0),
            new CommandDefinition("후원", "", "후원 NPC를 엽니다.", 0),
            new CommandDefinition("랭킹", "", "랭킹 NPC를 엽니다.", 0),
            new CommandDefinition("레벨보상", "", "레벨보상 NPC를 엽니다.", 0),
            new CommandDefinition("유니온", "", "유니온 NPC를 엽니다.", 0),
            new CommandDefinition("제작", "", "제작 NPC를 엽니다.", 0),
            new CommandDefinition("일퀘", "", "일퀘 NPC를 엽니다.", 0),
            new CommandDefinition("보스", "", "보스 NPC를 엽니다.", 0),
            new CommandDefinition("일일보상", "", "일일보상 NPC를 엽니다.", 0),
            new CommandDefinition("아획", "", "아이템 드롭률을 확인합니다.", 0),
            new CommandDefinition("메획", "", "메소 드롭률을 확인합니다.", 0),
            new CommandDefinition("엑어빌", "", "엑어빌", 0),
            new CommandDefinition("엑스트라", "", "엑스트라", 0),
            new CommandDefinition("각인석판", "", "각인석판", 0),
            new CommandDefinition("팬텀리셋", "", "모든 훔친 스킬을 초기화합니다.", 0),
            new CommandDefinition("보스인트로", "", "보스 인트로를 스킵합니다.", 0),
            new CommandDefinition("황금마차", "", "황금마차 이벤트에 참여합니다", 0),
            new CommandDefinition("commands", "", "Check User Commands", 0),
            new CommandDefinition("online", "", "Confirm login user's number of server", 0),
            new CommandDefinition("hp", "<AP>", "This stat can be increased instead of a mouse click", 0),
            new CommandDefinition("mp", "<AP>", "This stat can be increased instead of a mouse click", 0),
            new CommandDefinition("str", "<AP>", "This stat can be increased instead of a mouse click", 0),
            new CommandDefinition("int", "<AP>", "This stat can be increased instead of a mouse click", 0),
            new CommandDefinition("dex", "<AP>", "This stat can be increased instead of a mouse click", 0),
            new CommandDefinition("luk", "<AP>", "This stat can be increased instead of a mouse click", 0),
            new CommandDefinition("lag", "", "Use it when you can't do anything other than chatting, such as attacking.", 0),
            new CommandDefinition("skillmaster", "", "", 0),
            new CommandDefinition("town", "", "", 0),
            new CommandDefinition("bossintro", "", "", 0),
            new CommandDefinition("townbtn", "", "", 0),
            new CommandDefinition("goldenchariot", "", "", 0),
            new CommandDefinition("bossclearmessage", "", "", 0),
            new CommandDefinition("픽파킷디버그", "", "", 0)
         }
         : new CommandDefinition[]{
            new CommandDefinition("힘", "<올릴양>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("인트", "<올릴양>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("덱스", "<올릴양>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("럭", "<올릴양>", "해당 스탯을 마우스 클릭 대신 찍을 수 있습니다.", 0),
            new CommandDefinition("랙", "", "공격 등 채팅외에 아무것도 안될때 사용하세요.", 0),
            new CommandDefinition("렉", "", "공격 등 채팅외에 아무것도 안될때 사용하세요.", 0),
            new CommandDefinition("명령어", "", "유저 명령어를 출력합니다.", 0),
            new CommandDefinition("광장", "", "해당 서버의 광장인 헤네시스로 이동합니다.", 0),
            new CommandDefinition("헤네시스", "", "해당 서버의 광장인 헤네시스로 이동합니다.", 0),
            new CommandDefinition("자유시장", "", "자유시장으로 이동합니다.", 0),
            new CommandDefinition("인벤초기화", "모두/장착/장비/소비/설치/기타/캐시", "해당 탭의 인벤토리를 모두 비워버립니다.", 0),
            new CommandDefinition("도움말", "", "유저 명령어를 출력합니다.", 0),
            new CommandDefinition("마을", "", "마을이동", 0),
            new CommandDefinition("마을", "", "마을이동", 0),
            new CommandDefinition("추천인", "", "추천인", 0),
            new CommandDefinition("유니온", "", "유니온", 0),
            new CommandDefinition("뉴비기부함", "", "뉴비기부함", 0),
            new CommandDefinition("엑어빌", "", "엑어빌", 0),
            new CommandDefinition("엑스트라", "", "엑스트라", 0),
            new CommandDefinition("봉인해방", "", "봉인해방", 0),
            new CommandDefinition("각인석판", "", "각인석판", 0),
            new CommandDefinition("경매장", "", "경매장", 0),
            new CommandDefinition("스텟리로드", "", "스텟리로드", 0),
            new CommandDefinition("스킬마스터", "", "", 0),
            new CommandDefinition("동접", "", "", 0),
            new CommandDefinition("버프", "", "구매한 후원 버프를 발동시킵니다.", 0),
            new CommandDefinition("뿌리기", "", "구매한 경험치 50% 뿌리기를 사용합니다.", 0),
            new CommandDefinition("팬텀", "", "팬텀이 스킬을 훔칠 수 있는 편의기능입니다.", 0),
            new CommandDefinition("사다리", "", "사다리 알림을 켜거나 끕니다.", 0),
            new CommandDefinition("팬텀리셋", "", "모든 훔친 스킬을 초기화합니다.", 0),
            new CommandDefinition("코디", "", "코디 NPC를 엽니다.", 0),
            new CommandDefinition("창고", "", "창고 NPC를 엽니다.", 0),
            new CommandDefinition("출석", "", "출석 NPC를 엽니다.", 0),
            new CommandDefinition("일일미션", "", "일일미션 NPC를 엽니다.", 0),
            new CommandDefinition("킬미", "", "", 0),
            new CommandDefinition("초강", "", "", 0),
            new CommandDefinition("초월강화", "", "", 0),
            new CommandDefinition("전용강화", "", "", 0)
         };
   }
}
