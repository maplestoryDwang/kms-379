package script.Boss;

import constants.GameConstants;
import constants.QuestExConstants;
import database.DBConfig;
import objects.fields.Field;
import objects.fields.gameobject.lifes.ChangeableStats;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.item.Item;
import objects.item.MapleInventory;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.users.MapleCharacter;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Magnus extends ScriptEngineNPC {

    public void magnus_easy() {
        initNPC(MapleLifeFactory.getNPC(3001000));
        if (DBConfig.isGanglim) {
            self.say("現時無法使用Magnus類比展。");
        }
        self.say("通過那個入口网站可以體驗與Magnus的類比展。 當然，雖然遠遠不及馬格納斯本來的力量，但以諾瓦現時的科技來看，這是極限。", ScriptMessageFlag.NpcReplacedByNpc);
        if (self.askYesNo("為了和Magnus的類比展（Izzie Mode），你要移動嗎？ \r\n#b<<馬格納斯類比展1日可通關1次。 >>\r\n<<115級以上用戶可以進入隊伍。 >>", ScriptMessageFlag.NpcReplacedByNpc) == 1) {
            self.say("為了營造盡可能相似的環境，重現了暴君的城堡。 可以從那裡進入王座。", ScriptMessageFlag.NpcReplacedByNpc);
            target.registerTransferField(401060399); //say이후에 넘어가야할때는 target을 붙인다!
        }
    }

    public void enter_magnusDoor() {
        initNPC(MapleLifeFactory.getNPC(3001020));
        EventManager em = getEventManager("Magnus");
        if (em == null) {
            self.say("現時無法使用Magnus RAID。");
        } else {
            if (target.getParty() == null) {
                self.say("必須屬於1人以上的隊伍才能入場。");
            } else {
                if (target.getParty().getLeader().getId() != target.getId() && DBConfig.isGanglim) {
                    self.say("請通過派對現場進行。");
                } else {
                    if (target.getMapId() == 401060399) { //이지매그 입장맵
                        if (DBConfig.isGanglim) {
                            self.say("現時無法使用Magnus類比展。");
                        }
                        if (self.askYesNo("為了消滅馬格納斯，您要前往暴君的王座嗎？ \r\n#b<<馬格納斯類比展1日可通關1次。 >>\r\n<<115級以上玩家可以進入隊伍。 >>") == 1) {
                            //401060200 ~ 401060209
                            if (target.getParty().isPartySameMap()) {
                                boolean canEnter = false;
                                String overLap = checkEventNumber(getPlayer(), QuestExConstants.Magnus.getQuestID());
                                if (overLap == null) {
                                    if (em.getProperty("status0").equals("0")) {
                                        canEnter = true;
                                    }
                                    if (canEnter) {
                                        em.setProperty("status0", "1");
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 401060300);
                                        eim.setProperty("mode", "easy");
                                        getClient().getChannelServer().getMapFactory().getMap(401060300).resetFully(false);
                                        updateLastDate(getPlayer(), QuestExConstants.Magnus.getQuestID()); //이지매그와 노말매그는 시간을 공유함
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        self.sayOk("該頻道已經在進行Magnus RAID了。");
                                    }
                                } else {
                                    self.say("隊員中#b#e" + overLap + "今天已進入#n#k，無法再挑戰。");
                                }
                            } else {
                                self.say("所有隊員都必須在同一張地圖上。");
                            }
                        }
                    } else {
                    	boolean single = false;
                    	if (!DBConfig.isGanglim) {
                    		single = getPlayer().getParty().getMembers().size() == 1;
                    	}
                    	String text = text = "為了消滅馬格納斯，您要前往暴君的王座嗎？ #b\r\n";
                    	if (DBConfig.isGanglim) {
                            text += "#L0#前往暴君的王座（困難）。 （等級175以上）";
                            text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.HardMagnus.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#b";
                            getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[Hard Magnus]今天該老闆"+chr.getOneInfoQuestInteger(QuestExConstants.HardMagnus.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
                            text += "\r\n#L1#前往暴君的王座（普通）。 （155級以上）";
                            text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.Magnus.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#b";
                            getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[諾瑪爾·馬格納斯]今天該老闆"+chr.getOneInfoQuestInteger(QuestExConstants.Magnus.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
                            text +="\r\n#L3#進入暴君王座（硬）練習模式。 （等級175以上）#l\r\n#L2#不移動。 #l";
                    	} else {
                            text += "#L0#暴君的王座（困難）" + (single ? "（單曲）" : "（多個）") + "移動到。 （等級175以上）";
                            text += "\r\n#L1#暴君的王座（普通）" + (single ? "（單曲）" : "（多個）") + "移動到。 （155級以上）";
                            //text +="\r\n#L3#進入暴君王座（硬）練習模式。 （等級175以上）#l\r\n#L2#不移動。 #l";
                            /* int hreset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "HardMagnus" + (single ? "Single" : "Multi"));
                            text += "\r\n#L5#暴君的王座（困難）" + (single ? "（單曲）" : "（多個）") + "入場次數1新增（" + ((single ? 2 : 1) - hreset) + "可回收）#l";
                            */
                            int nreset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalMagnus" + (single ? "Single" : "Multi"));
                            text += "\r\n#L6#暴君的王座（普通）" + (single ? "（單曲）" : "（多個）") + "入場次數1新增（" + ((single ? 2 : 1) - nreset) + "可回收）#l"; 
                    	}

                        int v0 = self.askMenu(text);
                        if (!DBConfig.isGanglim) {
                        	if (v0 == 6) { //(v0 == 5 || v0 == 6) {
                        		int togetherPoint = getPlayer().getTogetherPoint();
                        		if (togetherPoint < 150) {
                        			self.sayOk("合作積分不足。 當前合作點：" + togetherPoint);
                        			return;
                        		}
                        		if (v0 == 5) { //하드 매그너스 리셋
                            		int hreset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "HardMagnus" + (single ? "Single" : "Multi"));
                                    if (hreset > (single ? 1 : 0)) { //싱글은 2회 구매 가능
                                    	self.sayOk("本周無法再新增可入場次數。");
                                    	return;
                                    }
                                    getPlayer().gainTogetherPoint(-150);
                                    getPlayer().updateOneInfo(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "HardMagnus"  + (single ? "Single" : "Multi"), String.valueOf(hreset + 1));
                                    self.sayOk("可進入次數新增完畢。");
                                    return;
                            	}
                            	if (v0 == 6) { //노말 매그너스 리셋
                            		int nreset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalMagnus" + (single ? "Single" : "Multi"));
                                    if (nreset > (single ? 1 : 0)) { //싱글은 2회 구매 가능
                                    	self.sayOk("本周無法再新增可入場次數。");
                                    	return;
                                    }
                                    getPlayer().gainTogetherPoint(-150);
                                    getPlayer().updateOneInfo(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalMagnus"  + (single ? "Single" : "Multi"), String.valueOf(nreset + 1));
                                    self.sayOk("可進入次數新增完畢。");
                                    return;
                            	}
                        	}
                        } 
                        
                        if (!DBConfig.isGanglim && target.getParty().getLeader().getId() != target.getId()) {
                        	self.say("請通過派對現場進行。");
                        	return;
                        }
                        
                        if (target.getParty().isPartySameMap()) {
                            boolean canEnter = false;
                            if (v0 != 3) {
                                int v2 = -1;
                                if (v0 == 0) {
                                    if (getPlayer().getQuestStatus(2000021) == 1) {
                                        if (GameConstants.isZero(getPlayer().getJob())) {
                                            v2 = self.askMenu("#e<勞恩斯武器>#n\r\n劍可以執行解除魔法師力量的#b勞恩斯武器#k秘密的任務。 怎麼辦？ \r\n\r\n#e#r<任務執行條件>#n#k\r\n#b-獨自擊破\r\n-最終傷害降低50%\r\n-只適用裝備的純能力值\r\n#k#L0#執行任務。 #l\r\n#L1#不執行任務。 #l", ScriptMessageFlag.Self);
                                        } else {
                                            v2 = self.askMenu("#e<勞恩斯武器>#n\r\n劍可以執行解除魔法師力量的#b勞恩斯武器#k秘密的任務。 怎麼辦？ \r\n\r\n#e#r<任務執行條件>#n#k\r\n#b-獨自擊破\r\n-只裝備封印的勞恩斯武器和輔助武器\r\n-最終傷害降低50%\r\n-只適用裝備的純能力值\r\n#k#L0#執行任務。 #l\r\n#L1#不執行任務。 #l", ScriptMessageFlag.Self);
                                        }
                                        if (v2 == 0) {
                                        	if (!getPlayer().haveItem(4036460)) {
                                        		self.say("#b#i4036460##z4036460#需要1個#k。 可以通過擊殺黑魔法師獲得。", ScriptMessageFlag.Self);
                                        		return;
                                        	}
                                            if (!checkBMQuestEquip()) {
                                                return;
                                            }
                                            if (getPlayer().getParty().getPartyMemberList().size() > 1) {
                                                self.say("該任務需要獨自進行。", ScriptMessageFlag.Self);
                                                return;
                                            }
                                        }
                                    }
                                }

                                String overLap = checkEventNumber(getPlayer(), QuestExConstants.Magnus.getQuestID(), DBConfig.isGanglim);
                                if (v0 == 0) { //하드매그
                                    overLap = checkEventNumber(getPlayer(), QuestExConstants.HardMagnus.getQuestID(), DBConfig.isGanglim);
                                }
                                //getPlayer().dropMessage(5, (overLap == null ? "Null" : overLap));
                                if (overLap == null) {
                                    if (v0 == 0) { //하드매그너스
                                        String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.HardMagnus.getQuestID());
                                        if (lastDate == null || DBConfig.isGanglim) { // 강림은 30분 재입장 삭제
                                            if (em.getProperty("Hstatus0").equals("0")) {
                                                canEnter = true;
                                            }
                                            if (canEnter) {
                                                em.setProperty("Hstatus0", "1");
                                                EventInstanceManager eim = em.readyInstance();
                                                eim.setProperty("map", 401060100);
                                                eim.setProperty("mode", "hard");
                                                getClient().getChannelServer().getMapFactory().getMap(401060100).resetFully(false);
                                                updateLastDate(getPlayer(), QuestExConstants.HardMagnus.getQuestID());
                                                if (DBConfig.isGanglim) { 
                                                    updateQuestEx(getPlayer(), QuestExConstants.HardMagnus.getQuestID());
                                                }
                                                if (v2 == 0) {
                                                    getPlayer().applyBMCurse1(3);
                                                }
                                                if (!DBConfig.isGanglim && !single) {
                                                	for (MapleCharacter partyMember : getPlayer().getPartyMembers()) {
                                                		if (partyMember.getMapId() == getPlayer().getMapId()) {
                                                			partyMember.setMultiMode(true);
                                                			partyMember.applyBMCurseJinMulti();
                                                		}
                                                	}
                                                }
                                                eim.registerParty(target.getParty(), getPlayer().getMap());
                                            } else {
                                                self.sayOk("當前所有地圖已滿，無法使用。 請使用其他頻道。");
                                            }
                                        } else {
                                            self.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");//본메 : 30분 이내에 입장한 파티원이 있습니다. 이지 및 노멀 모드를 통합하여 입장 후 30분 이내에 재입장이 불가능합니다.
                                        }
                                    } else if (v0 == 1) { //노말매그너스
                                        if (em.getProperty("Nstatus0").equals("0")) {
                                            canEnter = true;
                                        }
                                        if (canEnter) {
                                            em.setProperty("Nstatus0", "1");
                                            EventInstanceManager eim = em.readyInstance();
                                            eim.setProperty("map", 401060200);
                                            eim.setProperty("mode", "normal");
                                            getClient().getChannelServer().getMapFactory().getMap(401060200).resetFully(false);
                                            updateLastDate(getPlayer(), QuestExConstants.Magnus.getQuestID());
                                            if (DBConfig.isGanglim) { 
                                                updateQuestEx(getPlayer(), QuestExConstants.Magnus.getQuestID());
                                            }
                                            if (!DBConfig.isGanglim && !single) {
                                            	for (MapleCharacter partyMember : getPlayer().getPartyMembers()) {
                                            		if (partyMember.getMapId() == getPlayer().getMapId()) {
                                            			partyMember.setMultiMode(true);
                                            			partyMember.applyBMCurseJinMulti();
                                            		}
                                            	}
                                            }
                                            eim.registerParty(target.getParty(), getPlayer().getMap());
                                        } else {
                                            self.sayOk("當前所有地圖已滿，無法使用。 請使用其他頻道。");
                                        }
                                    }
                                } else {
                                    String text_ = "隊員中#b#e" + overLap + "今天已進入#n#k，無法再挑戰。";
                                    if (!DBConfig.isGanglim) {
                                        text_ += "\r\n（對於困難Magnus，每週四重置一次。）";
                                    }
                                    self.say(text_);
                                }
                            } else {
                                self.say("目前正在準備練習模式。");
                            }
                        } else {
                            self.say("所有隊員都必須在同一張地圖上。");
                        }
                    }
                }
            }
        }
    }


    int[] bmWeapons = GameConstants.bmWeapons;
    public boolean checkBMQuestEquip() {
        MapleInventory inv = getPlayer().getInventory(MapleInventoryType.EQUIPPED);
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        List<Integer> blockedList = new ArrayList<>();
        for (int next = 0; next > -3999; --next) {
            Item item = inv.getItem((short) next);
            if (item == null) {
                continue;
            }
            if (!ii.isCash(item.getItemId())) {
                if (next == -11 || next == -10 || next <= -1600 && next >= -1700 || next == -117 || next == -122 || next == -131) {
                    if (next == -11) {
                        boolean find = false;
                        for (int i = 0; i < bmWeapons.length; ++i) {
                            int weapon = bmWeapons[i];
                            if (item.getItemId() == weapon) {
                                find = true;
                                break;
                            }
                        }
                        if (!find) {
                            if (!(item.getItemId() >= 1572000 && item.getItemId() <= 1572010)) {
                                blockedList.add(item.getItemId());
                            }
                        }
                    }
                } else {
                    blockedList.add(item.getItemId());
                }
            }
        }
        if (!blockedList.isEmpty()) {
            String v0 = "#r武器#k和#b只能佩戴輔助武器#k進行挑戰。 \r\n\r\n#r<需要解除佩戴的物品>#k\r\n";
            for (int i = 0; i < blockedList.size(); ++i) {
                int bid = blockedList.get(i);
                v0 += "#i" + bid + "# #z" + bid + "#\r\n";
            }
            self.say(v0, ScriptMessageFlag.Self);
            return false;
        }
        return true;
    }


    public void magnus_boss() {
    	if (!DBConfig.isGanglim) {
    		enter_magnusDoor();
    		return;
    	}
        initNPC(MapleLifeFactory.getNPC(3001020));
        EventManager em = getEventManager("Magnus");
        if (em == null) {
            self.say("現時無法使用Magnus RAID。");
        } else {
            if (target.getParty() == null) {
                self.say("必須屬於1人以上的隊伍才能入場。");
            } else {
                if (target.getParty().getLeader().getId() != target.getId()) {
                    self.say("請通過派對現場進行。");
                } else {
                    if (target.getMapId() == 401060399) { //이지매그 입장맵
                        if (DBConfig.isGanglim) {
                            self.say("現時無法使用Magnus類比展。");
                        }
                        if (self.askYesNo("為了消滅馬格納斯，您要前往暴君的王座嗎？ \r\n#b<<馬格納斯類比展1日可通關1次。 >>\r\n<<115級以上玩家可以進入隊伍。 >>") == 1) {
                            //401060200 ~ 401060209
                            if (target.getParty().isPartySameMap()) {
                                boolean canEnter = false;
                                String overLap = checkEventNumber(getPlayer(), QuestExConstants.Magnus.getQuestID());
                                if (overLap == null) {
                                    if (em.getProperty("status0").equals("0")) {
                                        canEnter = true;
                                    }
                                    if (canEnter) {
                                        em.setProperty("status0", "1");
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 401060300);
                                        eim.setProperty("mode", "easy");
                                        getClient().getChannelServer().getMapFactory().getMap(401060300).resetFully(false);
                                        updateLastDate(getPlayer(), QuestExConstants.Magnus.getQuestID()); //이지매그와 노말매그는 시간을 공유함
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        self.sayOk("該頻道已經在進行Magnus RAID了。");
                                    }
                                } else {
                                    self.say("隊員中#b#e" + overLap + "今天已進入#n#k，無法再挑戰。");
                                }
                            } else {
                                self.say("所有隊員都必須在同一張地圖上。");
                            }
                        }
                    } else {
                        String text = "為了消滅馬格納斯，您要前往暴君的王座嗎？ #b\r\n";
                        text += "#L0#前往暴君的王座（困難）。 （等級175以上）";
                        if (DBConfig.isGanglim) {
                            text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.HardMagnus.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#b";

                            getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[Hard Magnus]今天該老闆"+chr.getOneInfoQuestInteger(QuestExConstants.HardMagnus.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
                        }
                        text += "\r\n#L1#前往暴君的王座（普通）。 （155級以上）";
                        if (DBConfig.isGanglim) {
                            text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.Magnus.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#b";
                            getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[諾瑪爾·馬格納斯]今天該老闆"+chr.getOneInfoQuestInteger(QuestExConstants.Magnus.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
                        }
                        text +="\r\n#L3#進入暴君王座（硬）練習模式。 （等級175以上）#l\r\n#L2#不移動。 #l";

                        int v0 = self.askMenu(text);
                        if (target.getParty().isPartySameMap()) {
                            boolean canEnter = false;
                            if (v0 != 3) {
                                int v2 = -1;
                                if (v0 == 0) {
                                    if (getPlayer().getQuestStatus(2000021) == 1) {
                                        if (GameConstants.isZero(getPlayer().getJob())) {
                                            v2 = self.askMenu("#e<勞恩斯武器>#n\r\n劍可以執行解除魔法師力量的#b勞恩斯武器#k秘密的任務。 怎麼辦？ \r\n\r\n#e#r<任務執行條件>#n#k\r\n#b-獨自擊破\r\n-最終傷害降低50%\r\n-只適用裝備的純能力值\r\n#k#L0#執行任務。 #l\r\n#L1#不執行任務。 #l", ScriptMessageFlag.Self);
                                        } else {
                                            v2 = self.askMenu("#e<勞恩斯武器>#n\r\n劍可以執行解除魔法師力量的#b勞恩斯武器#k秘密的任務。 怎麼辦？ \r\n\r\n#e#r<任務執行條件>#n#k\r\n#b-獨自擊破\r\n-只裝備封印的勞恩斯武器和輔助武器\r\n-最終傷害降低50%\r\n-只適用裝備的純能力值\r\n#k#L0#執行任務。 #l\r\n#L1#不執行任務。 #l", ScriptMessageFlag.Self);
                                        }
                                        if (v2 == 0) {
                                            if (!checkBMQuestEquip()) {
                                                return;
                                            }
                                            if (getPlayer().getParty().getPartyMemberList().size() > 1) {
                                                self.say("該任務需要獨自進行。", ScriptMessageFlag.Self);
                                                return;
                                            }
                                        }
                                    }
                                }

                                String overLap = checkEventNumber(getPlayer(), QuestExConstants.Magnus.getQuestID(), DBConfig.isGanglim);
                                if (v0 == 0) { //하드매그
                                    overLap = checkEventNumber(getPlayer(), QuestExConstants.HardMagnus.getQuestID(), DBConfig.isGanglim);
                                }
                                if (overLap == null) {
                                    if (v0 == 0) { //하드매그너스
                                        String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.HardMagnus.getQuestID());
                                        if (lastDate == null || DBConfig.isGanglim) { // 강림은 30분 재입장 삭제
                                            if (em.getProperty("Hstatus0").equals("0")) {
                                                canEnter = true;
                                            }
                                            if (canEnter) {
                                                em.setProperty("Hstatus0", "1");
                                                EventInstanceManager eim = em.readyInstance();
                                                eim.setProperty("map", 401060100);
                                                eim.setProperty("mode", "hard");
                                                getClient().getChannelServer().getMapFactory().getMap(401060100).resetFully(false);
                                                updateLastDate(getPlayer(), QuestExConstants.HardMagnus.getQuestID());
                                                if (DBConfig.isGanglim) {
                                                    updateQuestEx(getPlayer(), QuestExConstants.HardMagnus.getQuestID());
                                                }
                                                if (v2 == 0) {
                                                    getPlayer().applyBMCurse1(3);
                                                }
                                                eim.registerParty(target.getParty(), getPlayer().getMap());
                                            } else {
                                                self.sayOk("當前所有地圖已滿，無法使用。 請使用其他頻道。");
                                            }
                                        } else {
                                            self.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");//본메 : 30분 이내에 입장한 파티원이 있습니다. 이지 및 노멀 모드를 통합하여 입장 후 30분 이내에 재입장이 불가능합니다.
                                        }
                                    } else if (v0 == 1) { //노말매그너스
                                        if (em.getProperty("Nstatus0").equals("0")) {
                                            canEnter = true;
                                        }
                                        if (canEnter) {
                                            em.setProperty("Nstatus0", "1");
                                            EventInstanceManager eim = em.readyInstance();
                                            eim.setProperty("map", 401060200);
                                            eim.setProperty("mode", "normal");
                                            getClient().getChannelServer().getMapFactory().getMap(401060200).resetFully(false);
                                            updateLastDate(getPlayer(), QuestExConstants.Magnus.getQuestID());
                                            if (DBConfig.isGanglim) {
                                                updateQuestEx(getPlayer(), QuestExConstants.Magnus.getQuestID());
                                            }
                                            eim.registerParty(target.getParty(), getPlayer().getMap());
                                        } else {
                                            self.sayOk("當前所有地圖已滿，無法使用。 請使用其他頻道。");
                                        }
                                    }
                                } else {
                                    String text_ = "隊員中#b#e" + overLap + "今天已進入#n#k，無法再挑戰。";
                                    if (!DBConfig.isGanglim) {
                                        text_ += "\r\n（對於困難Magnus，每週四重置一次。）";
                                    }
                                    self.say(text_);
                                }
                            } else {
                                self.say("目前正在準備練習模式。");
                            }
                        } else {
                            self.say("所有隊員都必須在同一張地圖上。");
                        }
                    }
                }
            }
        }
    }

    public void magnus_summon() { //하드매그너스 소환
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (eim.getProperty("summonMOB") == null) {
                eim.setProperty("summonMOB", "1");
                Field field = getPlayer().getMap();
                if (DBConfig.isGanglim) {
                	field.spawnMonster(MapleLifeFactory.getMonster(8880000), new Point(1860, -1450), 32);
                }
                else {
                	if (getPlayer().getPartyMembers().size() == 1)  {
                		field.spawnMonster(MapleLifeFactory.getMonster(8880000), new Point(1860, -1450), 32);
                    }
                	else {
                		final MapleMonster magnus = MapleLifeFactory.getMonster(8880000);
                		magnus.setPosition(new Point(1860, -1450));
                		final long hp = magnus.getMobMaxHp();
                        ChangeableStats cs = new ChangeableStats(magnus.getStats());
                        cs.hp = hp * 3L;
                        if (cs.hp < 0) {
                        	cs.hp = Long.MAX_VALUE;
                        }
                        magnus.getStats().setHp(cs.hp);
                        magnus.getStats().setMaxHp(cs.hp);
                        magnus.setOverrideStats(cs);

                        field.spawnMonster(magnus, 32);
                	}
                }
            }
        }
    }

    public void magnus_summon_N() { //노말매그너스 소환
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (eim.getProperty("summonMOB") == null) {
                eim.setProperty("summonMOB", "1");
                Field field = getPlayer().getMap();
                if (DBConfig.isGanglim) {
                	field.spawnMonster(MapleLifeFactory.getMonster(8880002), new Point(1860, -1450), 32);
                } else {
                	if (getPlayer().getPartyMembers().size() == 1)  {
                		field.spawnMonster(MapleLifeFactory.getMonster(8880002), new Point(1860, -1450), 32);
                    }
                	else {
                		final MapleMonster magnus = MapleLifeFactory.getMonster(8880002);
                		magnus.setPosition(new Point(1860, -1450));
                		final long hp = magnus.getMobMaxHp();
                        ChangeableStats cs = new ChangeableStats(magnus.getStats());
                        cs.hp = hp * 3L;;
                        if (cs.hp < 0) {
                        	cs.hp = Long.MAX_VALUE;
                        }
                        magnus.getStats().setHp(cs.hp);
                        magnus.getStats().setMaxHp(cs.hp);
                        magnus.setOverrideStats(cs);

                        field.spawnMonster(magnus, 32);
                	}
                }
            }
        }
    }

    public void magnus_summon_E() { //이지매그너스 소환
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (eim.getProperty("summonMOB") == null) {
                eim.setProperty("summonMOB", "1");
                Field field = getPlayer().getMap();
                field.spawnMonster(MapleLifeFactory.getMonster(8880010), new Point(1860, -1450), 32);
            }
        }
    }

    public void out_magnusDoor() {
        initNPC(MapleLifeFactory.getNPC(3001020));
        if (self.askYesNo("戰鬥結束後移動。") == 1) {
            getPlayer().setRegisterTransferFieldTime(0);
            getPlayer().setRegisterTransferField(0);
            List<Integer> normalMap = new ArrayList(Arrays.asList(401060200, 401060201, 401060202, 401060203, 401060204, 401060205, 401060206, 401060207, 401060208, 401060209));
            if (normalMap.contains(target.getMapId())) {
                registerTransferField(401060399); //이지매그
            } else {
                registerTransferField(401060000);
            }
            if (getPlayer().getEventInstance() != null) {
                getPlayer().getEventInstance().unregisterPlayer(getPlayer());
                getPlayer().setEventInstance(null);
            }
        }
    }

    public void magnus_out() {
        if (self.askYesNo("戰鬥結束後移動。") == 1) {
            getPlayer().setRegisterTransferFieldTime(0);
            getPlayer().setRegisterTransferField(0);
            List<Integer> normalMap = Arrays.asList(401060200, 401060201, 401060202, 401060203, 401060204, 401060205, 401060206, 401060207, 401060208, 401060209);
            if (!DBConfig.isGanglim && normalMap.contains(target.getMapId())) {
                registerTransferField(401060399); //이지매그
            } else {
                registerTransferField(401060000);
            }
            if (getPlayer().getEventInstance() != null) {
                getPlayer().getEventInstance().unregisterPlayer(getPlayer());
                getPlayer().setEventInstance(null);
            }
        }
    }


    /*
        FieldSet fieldSet = fieldSet("EasyMagnusEnter");
        if (target.getMapId() != 401060399) {
            fieldSet = fieldSet("MagnusEnter");
        }
        if (fieldSet == null) {
            self.sayOk("現在不能使用Magnus RAID。");
            return;
        }
        boolean enterField = false;
        if (target.getMapId() == 401060399) { //이지매그입장맵
            if (self.askYesNo("為了消滅馬格納斯，您要前往暴君的王座嗎？ \r\n#b<<馬格納斯類比展1日可通關1次。 >>\r\n<<115級以上玩家可以進入隊伍。 >>") == 1) {
                enterField = true;
            }
        } else { //노말, 하드매그
             //TODO
        }
        if (enterField) {
            int enter = fieldSet.enter(target.getId());
            if (enter == -1) self.say("因未知原因無法入場。 請稍後再試。");
            else if (enter == 1) self.say("只有結成派對才能挑戰。");
            else if (enter == 2) self.say("請通過派對現場進行。");
            else if (enter == 3) self.say("最少" + fieldSet.minMember + "人以上的隊伍可以開始任務。");
            else if (enter == 4) self.say("隊員的等級最低" + fieldSet.minLv + "必須大於。");
            else if (enter == 5) self.say("只有隊員都聚在一起才能開始。");
            else if (enter == 6) self.say("已經有其他遠征隊進入裡面，正在挑戰任務完成。");
            else if (enter == 7) { //30분 대기시간이 발생한경우
                self.say("有30分鐘內入場的隊員。 入場後30分鐘內無法再入場。");
            } else if (enter < -1) {
                MapleCharacter user = getClient().getChannelServer().getPlayerStorage().getCharacterById(enter * -1);
                String name = "";
                if (user != null) {
                    name = user.getName();
                }
                if (target.getMapId() != 401060399) {
                    self.sayOk("最近一周內，有一名隊員通過了<老闆：Magnus>困難模式。 <老闆：Magnus>困難模式每週只能通關一次。 \r\n#r#e<清除歷史記錄將於每週四進行批量初始化。>");
                } else {
                    self.say("隊員中#b#e" + name + "#k#n今天進入Magnus，不能進去。");
                }
            }
        }
        */
    //TODO mag_GateWayOut(이지매그퇴장), BPReturn_Magnus2(노말매그,하드매그퇴장)
}
