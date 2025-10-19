package script.Boss;

import constants.QuestExConstants;
import database.DBConfig;
import network.models.CField;
import network.models.CWvsContext;
import objects.effect.child.PlayMusicDown;
import objects.fields.Field;
import objects.fields.Portal;
import objects.fields.gameobject.lifes.ChangeableStats;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.newscripting.ScriptEngineNPC;

import java.awt.*;

public class RootAbyss extends ScriptEngineNPC {

    public void rootafirstDoor() { //피에르
        //30분 재입장가능함
        //105200200 서쪽정원(노말)
        //105200210 삐에르 보스맵
        //105200600 서쪽정원<카오스>
        //105200610 삐에르 보스맵<카오스>
        initNPC(MapleLifeFactory.getNPC(1064012));
        EventManager em = getEventManager("RootAbyssPierre");
        if (em == null) {
            self.say("現在不能使用。");
        } else {
        	String text = "";
        	if (DBConfig.isGanglim) {
        		text = "#r#e<Lutavis西側庭院入口>#n#k\r\n這是通往盧塔維斯西側封印守護者#r皮埃爾#k守護的庭院的大門。 清除記錄將於當天午夜初始化。";
                text += "使用#L0##i4033611##t4033611#進入正常模式。 （125級以上）";
                text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.Pierre.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#k";
                getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[諾馬爾·皮埃爾]今天將該BOSS"+chr.getOneInfoQuestInteger(QuestExConstants.Pierre.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
                text += "\r\n#L1##i403361##t403361#進入混沌模式。 （180級以上）";
                text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.ChaosPierre.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#b";
                getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[卡奧斯皮埃爾]今天將該BOSS"+chr.getOneInfoQuestInteger(QuestExConstants.ChaosPierre.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
                text += "#l\r\n#L3#進入混沌練習模式。 （180級以上）#l";
        	}
        	else {
        		boolean single = getPlayer().getPartyMemberSize() == 1;
        		text = "#r#e<Lutavis西花園入口>#n#k\r\n";
        		text += "這是通往盧塔維斯西邊封印守護者#r皮埃爾#k守護的庭院的大門。 #r清除記錄將在正常情况下於當天午夜初始化，混沌情况下於每週四午夜初始化。 #b";
                text += "使用#L0##i403361##t403361#正常模式" + (single ? "（單曲）" : "（多個）") + "移動到。 （125級以上）";
                text += "\r\n#L1##i403361##t403361#使用混沌模式" + (single ? "（單曲）" : "（多個）") + "移動到。 （180級以上）";
                int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalPierre" + (single ? "Single" : "Multi"));
                text += "\r\n#L4#正常模式" + (single ? "（單曲）" : "（多個）") + "入場次數新增" + ((single ? 2 : 1) - reset) + "可回收#l";
        	}
            
        	int v0 = self.askMenu(text);
            if (target.getParty() == null) {
                target.say("好像只有舉辦派對才能入場。 找找派對吧。");
                return;
            } else {
            	if (v0 == 4 && !DBConfig.isGanglim) {
            		if (getPlayer().getTogetherPoint() < 150) {
            			self.sayOk("合作積分不足。 當前積分：" + getPlayer().getTogetherPoint());
            			return;
            		}
            		boolean single = getPlayer().getPartyMemberSize() == 1;
            		int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalPierre" + (single ? "Single" : "Multi"));
            		if ((reset > 0 && !single) || reset > 1 && single) { //초기화 불가능
                    	self.sayOk("每日追加入場次數新增已全部使用。");
                    	return;
                    }
            		getPlayer().gainTogetherPoint(-150);
            		getPlayer().updateOneInfo(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalPierre" + (single ? "Single" : "Multi"), String.valueOf(reset + 1));
            		self.sayOk("入場次數新增。");
            		return;
            	}
                if (v0 == 0) { //노말모드
                    if (target.getParty().isPartySameMap()) {
                        if (em.getProperty("status0").equals("0")) {
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.Pierre.getQuestID(), DBConfig.isGanglim);
                            if (overLap == null) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.Pierre.getQuestID());
                                if (lastDate == null || DBConfig.isGanglim) {
                                    String exMember = target.exchangeParty(4033611, -1);
                                    if (exMember == null) { //입장시도

                                        em.setProperty("status0", "1");
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 105200200);
                                        eim.setProperty("mode", "normal");
                                        getClient().getChannelServer().getMapFactory().getMap(105200200).setLastRespawnTime(0);
                                        getClient().getChannelServer().getMapFactory().getMap(105200200).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200210).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200200).setLastRespawnTime(Long.MAX_VALUE);

                                        updateLastDate(getPlayer(), QuestExConstants.Pierre.getQuestID());
                                        if (DBConfig.isGanglim) { 
                                            updateQuestEx(getPlayer(), QuestExConstants.Pierre.getQuestID());
                                        }
                                        if (!DBConfig.isGanglim && getPlayer().getPartyMemberSize() > 1) {
                                           	for (MapleCharacter partyMember : getPlayer().getPartyMembers()) {
                                           		if (partyMember.getMapId() == getPlayer().getMapId()) {
                                           			partyMember.setMultiMode(true);
                                           			partyMember.applyBMCurseJinMulti();
                                          		}
                                          	}
                                        }
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        target.say("隊員中#b" + exMember + "#k先生沒有#i4033611##t4033611#，無法入場。");
                                    }
                                } else {
                                    target.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");
                                }
                            } else {
                                target.say("隊員中#b#e" + overLap + "#n#k今天已經無法挑戰了。");
                            }
                        } else {
                            self.say("所有實例已滿，無法使用。 請使用其他頻道。");
                        }
                    } else {
                        self.say("隊員們都應該在同一張地圖上。");
                    }
                } else if (v0 == 1) { //카오스모드
                    if (target.getParty().isPartySameMap()) {
                        if (em.getProperty("Cstatus0").equals("0")) {
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.ChaosPierre.getQuestID(), DBConfig.isGanglim);
                            if (overLap == null) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.ChaosPierre.getQuestID());
                                if (lastDate == null || DBConfig.isGanglim) {
                                    String exMember = target.exchangeParty(4033611, -1);
                                    if (exMember == null) { //입장시도
                                        em.setProperty("Cstatus0", "1");
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 105200600);
                                        eim.setProperty("mode", "chaos");
                                        getClient().getChannelServer().getMapFactory().getMap(105200600).setLastRespawnTime(0);
                                        getClient().getChannelServer().getMapFactory().getMap(105200600).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200610).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200600).setLastRespawnTime(Long.MAX_VALUE);
                                        updateLastDate(getPlayer(), QuestExConstants.ChaosPierre.getQuestID());
                                        if (DBConfig.isGanglim) {
                                            updateQuestEx(getPlayer(), QuestExConstants.ChaosPierre.getQuestID());
                                        }
                                        if (!DBConfig.isGanglim && getPlayer().getPartyMemberSize() > 1) {
                                           	for (MapleCharacter partyMember : getPlayer().getPartyMembers()) {
                                           		if (partyMember.getMapId() == getPlayer().getMapId()) {
                                           			partyMember.setMultiMode(true);
                                           			partyMember.applyBMCurseJinMulti();
                                          		}
                                          	}
                                        }
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        target.say("隊員中#b" + exMember + "#k先生沒有#i4033611##t4033611#，無法入場。");
                                    }
                                } else {
                                    target.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");
                                }
                            } else {
                                if (DBConfig.isGanglim) {
                                    target.say("隊員中#b#e" + overLap + "#n#k今日已無法挑戰。");
                                } else {
                                    target.say("隊員中#b#e" + overLap + "#n#k本周已無法挑戰。");
                                }
                            }
                        } else {
                            self.say("所有實例已滿，無法使用。 請使用其他頻道。");
                        }
                    } else {
                        self.say("隊員們都應該在同一張地圖上。");
                    }
                } else if (v0 == 2) { //연습모드
                    self.say("練習模式正在準備中。");
                }
            }
        }
    }

    public void pierreEnter() { //피에르
    	if (!DBConfig.isGanglim) {
    		rootafirstDoor();
    		return;
    	}
        EventManager em = getEventManager("RootAbyssPierre");
        if (em == null) {
            self.say("現在不能使用。");
        } else {
            String text = "#r#e<Lutavis西側庭院入口>#n#k\r\n這是通往盧塔維斯西側封印守護者#r皮埃爾#k守護的庭院的大門。" + (DBConfig.isGanglim ? "清除記錄將於當天午夜初始化。" : "#r清除記錄將在正常情况下於當天午夜初始化，混沌情况下於每週四午夜初始化。 #b");
            text += "使用#L0##i4033611##t4033611#進入正常模式。 （125級以上）";
            if (DBConfig.isGanglim) {
                text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.Pierre.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#k";
                getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[諾馬爾·皮埃爾]今天將該BOSS"+chr.getOneInfoQuestInteger(QuestExConstants.Pierre.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
            }
            text += "\r\n#L1##i403361##t403361#進入混沌模式。 （180級以上）";
            if (DBConfig.isGanglim) {
                text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.ChaosPierre.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#b";
                getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[卡奧斯皮埃爾]今天將該BOSS"+chr.getOneInfoQuestInteger(QuestExConstants.ChaosPierre.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
            }
            text += "#l\r\n#L3#進入混沌練習模式。 （180級以上）#l";
            int v0 = self.askMenu(text);
            if (target.getParty() == null) {
                target.say("好像只有舉辦派對才能入場。 找找派對吧。");
            } else {
                if (v0 == 0) { //노말모드
                    if (target.getParty().isPartySameMap()) {
                        if (em.getProperty("status0").equals("0")) {
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.Pierre.getQuestID(), DBConfig.isGanglim);
                            if (overLap == null) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.Pierre.getQuestID());
                                if (lastDate == null || DBConfig.isGanglim) {
                                    String exMember = target.exchangeParty(4033611, -1);
                                    if (exMember == null) { //입장시도
                                        em.setProperty("status0", "1");
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 105200200);
                                        eim.setProperty("mode", "normal");
                                        getClient().getChannelServer().getMapFactory().getMap(105200200).setLastRespawnTime(0);
                                        getClient().getChannelServer().getMapFactory().getMap(105200200).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200210).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200200).setLastRespawnTime(Long.MAX_VALUE);

                                        updateLastDate(getPlayer(), QuestExConstants.Pierre.getQuestID());
                                        if (DBConfig.isGanglim) {
                                            updateQuestEx(getPlayer(), QuestExConstants.Pierre.getQuestID());
                                        }
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        target.say("隊員中#b" + exMember + "#k先生沒有#i4033611##t4033611#，無法入場。");
                                    }
                                } else {
                                    target.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");
                                }
                            } else {
                                target.say("隊員中#b#e" + overLap + "#n#k今天已經無法挑戰了。");
                            }
                        } else {
                            self.say("所有實例已滿，無法使用。 請使用其他頻道。");
                        }
                    } else {
                        self.say("隊員們都應該在同一張地圖上。");
                    }
                } else if (v0 == 1) { //카오스모드
                    if (target.getParty().isPartySameMap()) {
                        if (em.getProperty("Cstatus0").equals("0")) {
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.ChaosPierre.getQuestID(), DBConfig.isGanglim);
                            if (overLap == null) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.ChaosPierre.getQuestID());
                                if (lastDate == null || DBConfig.isGanglim) {
                                    String exMember = target.exchangeParty(4033611, -1);
                                    if (exMember == null) { //입장시도
                                        em.setProperty("Cstatus0", "1");
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 105200600);
                                        eim.setProperty("mode", "chaos");
                                        getClient().getChannelServer().getMapFactory().getMap(105200600).setLastRespawnTime(0);
                                        getClient().getChannelServer().getMapFactory().getMap(105200600).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200610).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200600).setLastRespawnTime(Long.MAX_VALUE);
                                        updateLastDate(getPlayer(), QuestExConstants.ChaosPierre.getQuestID());
                                        if (DBConfig.isGanglim) {
                                            updateQuestEx(getPlayer(), QuestExConstants.ChaosPierre.getQuestID());
                                        }
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        target.say("隊員中#b" + exMember + "#k先生沒有#i4033611##t4033611#，無法入場。");
                                    }
                                } else {
                                    target.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");
                                }
                            } else {
                                if (DBConfig.isGanglim) {
                                    target.say("隊員中#b#e" + overLap + "#n#k今日已無法挑戰。");
                                } else {
                                    target.say("隊員中#b#e" + overLap + "#n#k本周已無法挑戰。");
                                }
                            }
                        } else {
                            self.say("所有實例已滿，無法使用。 請使用其他頻道。");
                        }
                    } else {
                        self.say("隊員們都應該在同一張地圖上。");
                    }
                } else if (v0 == 2) { //연습모드
                    self.say("練習模式正在準備中。");
                }
            }
        }
    }

    //피에르의 티파티에에

    public void pierre_Summon() { //노말 피에르 
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (eim.getProperty("summonMOB") == null) {
                eim.setProperty("summonMOB", "1");
                Field field = getPlayer().getMap();
                field.broadcastMessage(CField.environmentChange("rootabyss/firework", 19));
                field.startMapEffect("真心歡迎來到皮埃爾的茶派對！", 5120098, 3000);
                PlayMusicDown e = new PlayMusicDown(getPlayer().getId(), 100, "Field.img/rootabyss/firework");
                field.broadcastMessage(e.encodeForLocal());
                if (DBConfig.isGanglim) {
                	field.spawnMonster(MapleLifeFactory.getMonster(8900100), new Point(1000, 551), 1);
                }
                else {
                	if (!getPlayer().isMultiMode()) {
                		field.spawnMonster(MapleLifeFactory.getMonster(8900100), new Point(1000, 551), 1);
                	}
                	else {
                		final MapleMonster pierre = MapleLifeFactory.getMonster(8900100);
                		pierre.setPosition(new Point(1000, 551));
                		final long hp = pierre.getMobMaxHp();
                		long fixedhp = hp * 3L;
                        if (fixedhp < 0) {
                        	fixedhp = Long.MAX_VALUE;
                        }
                        pierre.setHp(fixedhp);
                        pierre.setMaxHp(fixedhp);

                        field.spawnMonster(pierre, 1);
                	}
                }
            }
        }
    }

    public void pierre_Summon1() {
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (eim.getProperty("summonMOB") == null) {
                eim.setProperty("summonMOB", "1");
                Field field = getPlayer().getMap();
                field.broadcastMessage(CField.environmentChange("rootabyss/firework", 19));
                field.startMapEffect("真心歡迎來到皮埃爾的茶派對！", 5120098, 3000);
                PlayMusicDown e = new PlayMusicDown(getPlayer().getId(), 100, "Field.img/rootabyss/firework");
                field.broadcastMessage(e.encodeForLocal());
                field.spawnMonster(MapleLifeFactory.getMonster(8900000), new Point(1000, 551), 1);
            }
        }
    }

    public void rootasecondDoor() { //반반
        //동쪽정원 105200100
        //카오스동쪽정원 105200500
        //차원의 틈에서 반반을 소환하자
        //반반은 뒤지면 바로템줌
        initNPC(MapleLifeFactory.getNPC(1064013));
        EventManager em = getEventManager("RootAbyssVonbon");
        if (em == null) {
            self.say("現在不能使用。");
        } else {
        	String text = "";
        	if (DBConfig.isGanglim) {
        		text = "#r#e<Lutavis東側庭院入口>#n#k\r\n通往Lutavis東側封印守護者#r班班#k守護的庭院的大門。" + (DBConfig.isGanglim ? "清除記錄將於當天午夜初始化。" : "#r清除記錄將在正常情况下於當天午夜初始化，混沌情况下於每週四午夜初始化。 #b") + "\r\n'";
                text += "使用#L0##i4033611##t4033611#進入正常模式。 （125級以上）";
                text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.VonBon.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#k";
                getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[半斤八兩]今天該老闆"+chr.getOneInfoQuestInteger(QuestExConstants.VonBon.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
                text += "使用#l\r\n#L1##i403361##t4033611#進入混沌模式。 （180級以上）";
                text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.ChaosVonBon.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#b";
                getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[混沌一斑]今天該BOSS"+chr.getOneInfoQuestInteger(QuestExConstants.ChaosVonBon.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
                text += "#l\r\n#L3#進入混沌練習模式。 （180級以上）#l";
        	}
        	else {
        		boolean single = getPlayer().getPartyMemberSize() == 1;
        		text = "#r#e<Lutavis東側庭院入口>#n#k\r\n通往Lutavis東側封印守護者#r班班#k守護的庭院的大門。" + (DBConfig.isGanglim ? "清除記錄將於當天午夜初始化。" : "#r清除記錄將在正常情况下於當天午夜初始化，混沌情况下於每週四午夜初始化。 #b") + "\r\n'";
                text += "使用#L0##i403361##t403361#正常模式"  + (single ? "（單曲）" : "（多個）") +  "移動到。 （125級以上）";
                text += "使用#l\r\n#L1##i403361##t403361#進入混沌模式"  + (single ? "（單曲）" : "（多個）") + "移動到。 （180級以上）";
                //text += "#l\r\n#L3#進入混沌練習模式。 （180級以上）#l";
                int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalVonBon" + (single ? "Single" : "Multi"));
                text += "\r\n#L4#正常模式" + (single ? "（單曲）" : "（多個）") + "入場次數新增" + ((single ? 2 : 1) - reset) + "可回收#l";
        	}
            int v0 = self.askMenu(text);
            if (target.getParty() == null) {
                target.say("好像只有舉辦派對才能入場。 找找派對吧。");
                return;
            } else {
            	if (v0 == 4 && !DBConfig.isGanglim) {
            		if (getPlayer().getTogetherPoint() < 150) {
            			self.sayOk("合作積分不足。 當前積分：" + getPlayer().getTogetherPoint());
            			return;
            		}
            		boolean single = getPlayer().getPartyMemberSize() == 1;
            		int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalVonBon" + (single ? "Single" : "Multi"));
            		if ((reset > 0 && !single) || reset > 1 && single) { //초기화 불가능
                    	self.sayOk("每日追加入場次數新增已全部使用。");
                    	return;
                    }
            		getPlayer().gainTogetherPoint(-150);
            		getPlayer().updateOneInfo(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalVonBon" + (single ? "Single" : "Multi"), String.valueOf(reset + 1));
            		self.sayOk("入場次數新增。");
            		return;
            	}
                if (target.getParty().isPartySameMap()) {
                    if (v0 == 0) { //노말모드
                        if (em.getProperty("status0").equals("0")) {
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.VonBon.getQuestID(), DBConfig.isGanglim);
                            if (overLap == null) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.VonBon.getQuestID());
                                if (lastDate == null || DBConfig.isGanglim) {
                                    String exMember = target.exchangeParty(4033611, -1);
                                    if (exMember == null) { //입장시도
                                        em.setProperty("status0", "1");
                                        updateLastDate(getPlayer(), QuestExConstants.VonBon.getQuestID());
                                        if (DBConfig.isGanglim) {
                                            updateQuestEx(getPlayer(), QuestExConstants.VonBon.getQuestID());
                                        }
                                        if (!DBConfig.isGanglim && getPlayer().getPartyMemberSize() > 1) {
                                            for (MapleCharacter partyMember : getPlayer().getPartyMembers()) {
                                            	if (partyMember.getMapId() == getPlayer().getMapId()) {
                                               		partyMember.setMultiMode(true);
                                               		partyMember.applyBMCurseJinMulti();
                                              	}
                                            }
                                        }
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 105200100);
                                        eim.setProperty("mode", "normal");
                                        getClient().getChannelServer().getMapFactory().getMap(105200100).setLastRespawnTime(0);
                                        getClient().getChannelServer().getMapFactory().getMap(105200100).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200110).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200100).setLastRespawnTime(Long.MAX_VALUE);
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        target.say("隊員中#b" + exMember + "#k先生沒有#i4033611##t4033611#，無法入場。");
                                    }
                                } else {
                                    target.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");
                                }
                            } else {
                                target.say("隊員中#b#e" + overLap + "#n#k今天已經無法挑戰了。");
                            }
                        } else {
                            self.say("所有實例已滿，無法使用。 請使用其他頻道。");
                        }
                    } else if (v0 == 1) { //카오스모드
                        if (em.getProperty("Cstatus0").equals("0")) {
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.ChaosVonBon.getQuestID(), DBConfig.isGanglim);
                            if (overLap == null) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.ChaosVonBon.getQuestID());
                                if (lastDate == null || DBConfig.isGanglim) {
                                    String exMember = target.exchangeParty(4033611, -1);
                                    if (exMember == null) { //입장시도
                                        em.setProperty("Cstatus0", "1");
                                        updateLastDate(getPlayer(), QuestExConstants.ChaosVonBon.getQuestID());
                                        if (DBConfig.isGanglim) {
                                            updateQuestEx(getPlayer(), QuestExConstants.ChaosVonBon.getQuestID());
                                        }
                                        if (!DBConfig.isGanglim && getPlayer().getPartyMemberSize() > 1) {
                                            for (MapleCharacter partyMember : getPlayer().getPartyMembers()) {
                                            	if (partyMember.getMapId() == getPlayer().getMapId()) {
                                               		partyMember.setMultiMode(true);
                                               		partyMember.applyBMCurseJinMulti();
                                              	}
                                            }
                                        }
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 105200500);
                                        eim.setProperty("mode", "chaos");
                                        getClient().getChannelServer().getMapFactory().getMap(105200500).setLastRespawnTime(0);
                                        getClient().getChannelServer().getMapFactory().getMap(105200500).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200510).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200500).setLastRespawnTime(Long.MAX_VALUE);
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        target.say("隊員中#b" + exMember + "#k先生沒有#i4033611##t4033611#，無法入場。");
                                    }
                                } else {
                                    target.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");
                                }
                            } else {
                                if (DBConfig.isGanglim) {
                                    target.say("隊員中#b#e" + overLap + "#n#k今日已無法挑戰。");
                                } else {
                                    target.say("隊員中#b#e" + overLap + "#n#k本周已無法挑戰。");
                                }
                            }
                        } else {
                            self.say("所有實例已滿，無法使用。 請使用其他頻道。");
                        }
                    } else if (v0 == 2) { //연습모드
                        self.say("練習模式正在準備中。");
                    }
                } else {
                    self.say("隊員們都應該在同一張地圖上。");
                }
            }
        }
    }

    public void banbanEnter() { //반반
    	if (!DBConfig.isGanglim) {
    		rootasecondDoor();
    		return;
    	}
        EventManager em = getEventManager("RootAbyssVonbon");
        if (em == null) {
            self.say("現在不能使用。");
        } else {
            String text = "#r#e<Lutavis東側庭院入口>#n#k\r\n通往Lutavis東側封印守護者#r班班#k守護的庭院的大門。" + (DBConfig.isGanglim ? "清除記錄將於當天午夜初始化。" : "#r清除記錄將在正常情况下於當天午夜初始化，混沌情况下於每週四午夜初始化。 #b") + "\r\n'";
            text += "使用#L0##i4033611##t4033611#進入正常模式。 （125級以上）";
            if (DBConfig.isGanglim) {
                text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.VonBon.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#k";
                getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[半斤八兩]今天該老闆"+chr.getOneInfoQuestInteger(QuestExConstants.VonBon.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
            }
            text += "使用#l\r\n#L1##i403361##t4033611#進入混沌模式。 （180級以上）";
            if (DBConfig.isGanglim) {
                text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.ChaosVonBon.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#b";
                getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[混沌一斑]今天該BOSS"+chr.getOneInfoQuestInteger(QuestExConstants.ChaosVonBon.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
            }
            text += "#l\r\n#L3#進入混沌練習模式。 （180級以上）#l";
            int v0 = self.askMenu(text);
            if (target.getParty() == null) {
                target.say("好像只有舉辦派對才能入場。 找找派對吧。");
            } else {
                if (target.getParty().isPartySameMap()) {
                    if (v0 == 0) { //노말모드
                        if (em.getProperty("status0").equals("0")) {
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.VonBon.getQuestID(), DBConfig.isGanglim);
                            if (overLap == null) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.VonBon.getQuestID());
                                if (lastDate == null || DBConfig.isGanglim) {
                                    String exMember = target.exchangeParty(4033611, -1);
                                    if (exMember == null) { //입장시도
                                        em.setProperty("status0", "1");
                                        updateLastDate(getPlayer(), QuestExConstants.VonBon.getQuestID());
                                        if (DBConfig.isGanglim) {
                                            updateQuestEx(getPlayer(), QuestExConstants.VonBon.getQuestID());
                                        }
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 105200100);
                                        eim.setProperty("mode", "normal");
                                        getClient().getChannelServer().getMapFactory().getMap(105200100).setLastRespawnTime(0);
                                        getClient().getChannelServer().getMapFactory().getMap(105200100).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200110).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200100).setLastRespawnTime(Long.MAX_VALUE);
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        target.say("隊員中#b" + exMember + "#k先生沒有#i4033611##t4033611#，無法入場。");
                                    }
                                } else {
                                    target.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");
                                }
                            } else {
                                target.say("隊員中#b#e" + overLap + "#n#k今天已經無法挑戰了。");
                            }
                        } else {
                            self.say("所有實例已滿，無法使用。 請使用其他頻道。");
                        }
                    } else if (v0 == 1) { //카오스모드
                        if (em.getProperty("Cstatus0").equals("0")) {
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.ChaosVonBon.getQuestID(), DBConfig.isGanglim);
                            if (overLap == null) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.ChaosVonBon.getQuestID());
                                if (lastDate == null || DBConfig.isGanglim) {
                                    String exMember = target.exchangeParty(4033611, -1);
                                    if (exMember == null) { //입장시도
                                        em.setProperty("Cstatus0", "1");
                                        updateLastDate(getPlayer(), QuestExConstants.ChaosVonBon.getQuestID());
                                        if (DBConfig.isGanglim) {
                                            updateQuestEx(getPlayer(), QuestExConstants.ChaosVonBon.getQuestID());
                                        }
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 105200500);
                                        eim.setProperty("mode", "chaos");
                                        getClient().getChannelServer().getMapFactory().getMap(105200500).setLastRespawnTime(0);
                                        getClient().getChannelServer().getMapFactory().getMap(105200500).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200510).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200500).setLastRespawnTime(Long.MAX_VALUE);
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        target.say("隊員中#b" + exMember + "#k先生沒有#i4033611##t4033611#，無法入場。");
                                    }
                                } else {
                                    target.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");
                                }
                            } else {
                                if (DBConfig.isGanglim) {
                                    target.say("隊員中#b#e" + overLap + "#n#k今日已無法挑戰。");
                                } else {
                                    target.say("隊員中#b#e" + overLap + "#n#k本周已無法挑戰。");
                                }
                            }
                        } else {
                            self.say("所有實例已滿，無法使用。 請使用其他頻道。");
                        }
                    } else if (v0 == 2) { //연습모드
                        self.say("練習模式正在準備中。");
                    }
                } else {
                    self.say("隊員們都應該在同一張地圖上。");
                }
            }
        }
    }

    public void banban_Summon() {
        //
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (eim.getProperty("summonMOB") == null) {
                eim.setProperty("summonMOB", "1");
                Field field = getPlayer().getMap();
                field.broadcastMessage(CField.environmentChange("Bgm29.img/banbantime", 19));
                field.startMapEffect("從次元的縫隙中召喚一半吧。", 5120025, 3000);
            }
        }
    }

    public void banbanInsideMob() {
        MapleMonster mob = MapleLifeFactory.getMonster(8910001);
        if (mob != null) {
            Field field = getPlayer().getMap();
            field.spawnMonsterOnGroundBelow(mob, new Point(-50, 245));
        }
    }

    public void banbanGoInside() {
        Portal portal = getPortal();
        Field field = getPlayer().getMap();
        if (portal.getId() == 2) {
            if (field.isObjectEnable("Pt01gate")) {
                registerTransferField(field.getId() + 10);
            }
        }
        if (portal.getId() == 3) {
            if (field.isObjectEnable("Pt02gate")) {
                registerTransferField(field.getId() + 10);
            }
        }
        if (portal.getId() == 4) {
            if (field.isObjectEnable("Pt03gate")) {
                registerTransferField(field.getId() + 10);
            }
        }
        if (portal.getId() == 5) {
            if (field.isObjectEnable("Pt04gate")) {
                registerTransferField(field.getId() + 10);
            }
        }
        if (portal.getId() == 6) {
            if (field.isObjectEnable("Pt05gate")) {
                registerTransferField(field.getId() + 10);
            }
        }
        if (portal.getId() == 7) {
            if (field.isObjectEnable("Pt06gate")) {
                registerTransferField(field.getId() + 10);
            }
        }
        if (portal.getId() == 8) {
            if (field.isObjectEnable("Pt07gate")) {
                registerTransferField(field.getId() + 10);
            }
        }
        if (portal.getId() == 9) {
            if (field.isObjectEnable("Pt08gate")) {
                registerTransferField(field.getId() + 10);
            }
        }
        if (portal.getId() == 10) {
            if (field.isObjectEnable("Pt09gate")) {
                registerTransferField(field.getId() + 10);
            }
        }
    }

    public void rootathirdDoor() { //블러디 퀸
        //남쪽정원, 여왕의 성 노말, 카오스
        //105200300, 105200310
        //105200700, 105200710
        //잠든 블러디 퀸에게 말을 걸어보자
        //어머, 귀여운
        //무엄하다!
        //킥킥, 여기가
        //흑흑, 당신의 죽음을
        //상자 부숴야 나옴
        initNPC(MapleLifeFactory.getNPC(1064014));
        EventManager em = getEventManager("RootAbyssCrimsonQueen");
        if (em == null) {
            self.say("現在不能使用。");
        } else {
        	String text = "";
        	if (DBConfig.isGanglim) {
        		text = "#r#e<Lutavis南庭院入口>#n#k\r\n這是通往Lutavis南封印守護者#r Bloody Queen#k守護的庭院的大門。" + (DBConfig.isGanglim ? "清除記錄將於當天午夜初始化。" : "#r清除記錄將在正常情况下於當天午夜初始化，混沌情况下於每週四午夜初始化。 #b") + "\r\n";
                text += "使用#L0##i4033611##t4033611#進入正常模式。 （125級以上）";
                text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.CrimsonQueen.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#k";
                getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[諾瑪爾Bloody Queen]今天該老闆"+chr.getOneInfoQuestInteger(QuestExConstants.CrimsonQueen.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
            	text += "使用#l\r\n#L1##i403361##t4033611#進入混沌模式。 （180級以上）";
                text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.ChaosCrimsonQueen.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#b";
                getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[混沌血色女王]今天該BOSS"+chr.getOneInfoQuestInteger(QuestExConstants.ChaosCrimsonQueen.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
            	text += "#l\r\n#L3#進入混沌練習模式。 （180級以上）#l";
        	}
        	else {
        		boolean single = getPlayer().getPartyMemberSize() == 1;
        		text = "#r#e<Lutavis南庭院入口>#n#k\r\n這是通往Lutavis南封印守護者#r Bloody Queen#k守護的庭院的大門。" + (DBConfig.isGanglim ? "清除記錄將於當天午夜初始化。" : "#r清除記錄將在正常情况下於當天午夜初始化，混沌情况下於每週四午夜初始化。 #b") + "\r\n";
                text += "使用#L0##i403361##t403361#正常模式"  + (single ? "（單曲）" : "（多個）") +  "移動到。 （125級以上）";
            	text += "使用#l\r\n#L1##i403361##t403361#進入混沌模式"  + (single ? "（單曲）" : "（多個）") +  "移動到。 （180級以上）";
            	//text += "#l\r\n#L3#進入混沌練習模式。 （180級以上）#l";
            	int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalCrimsonQueen" + (single ? "Single" : "Multi"));
                text += "\r\n#L4#正常模式" + (single ? "（單曲）" : "（多個）") + "入場次數新增" + ((single ? 2 : 1) - reset) + "可回收#l";
        	}
            
            int v0 = self.askMenu(text);
            if (target.getParty() == null) {
                target.say("好像只有舉辦派對才能入場。 找找派對吧。");
                return;
            } else {
            	if (v0 == 4 && !DBConfig.isGanglim) {
            		if (getPlayer().getTogetherPoint() < 150) {
            			self.sayOk("合作積分不足。 當前積分：" + getPlayer().getTogetherPoint());
            			return;
            		}
            		boolean single = getPlayer().getPartyMemberSize() == 1;
            		int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalCrimsonQueen" + (single ? "Single" : "Multi"));
            		if ((reset > 0 && !single) || reset > 1 && single) { //초기화 불가능
                    	self.sayOk("每日追加入場次數新增已全部使用。");
                    	return;
                    }
            		getPlayer().gainTogetherPoint(-150);
            		getPlayer().updateOneInfo(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalCrimsonQueen" + (single ? "Single" : "Multi"), String.valueOf(reset + 1));
            		self.sayOk("入場次數新增。");
            		return;
            	}
                if (target.getParty().isPartySameMap()) {
                    if (v0 == 0) { //노말모드
                        if (em.getProperty("status0").equals("0")) {
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.CrimsonQueen.getQuestID(), DBConfig.isGanglim);
                            if (overLap == null) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.CrimsonQueen.getQuestID());
                                if (lastDate == null || DBConfig.isGanglim) {
                                    String exMember = target.exchangeParty(4033611, -1);
                                    if (exMember == null) { //입장시도
                                        em.setProperty("status0", "1");
                                        updateLastDate(getPlayer(), QuestExConstants.CrimsonQueen.getQuestID());
                                        if (DBConfig.isGanglim) { 
                                            updateQuestEx(getPlayer(), QuestExConstants.CrimsonQueen.getQuestID());
                                        }
                                    	if (!DBConfig.isGanglim && getPlayer().getPartyMemberSize() > 1) {
                                            for (MapleCharacter partyMember : getPlayer().getPartyMembers()) {
                                            	if (partyMember.getMapId() == getPlayer().getMapId()) {
                                               		partyMember.setMultiMode(true);
                                               		partyMember.applyBMCurseJinMulti();
                                              	}
                                            }
                                        }
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 105200300);
                                        eim.setProperty("mode", "normal");
                                        getClient().getChannelServer().getMapFactory().getMap(105200300).setLastRespawnTime(0);
                                        getClient().getChannelServer().getMapFactory().getMap(105200300).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200310).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200300).setLastRespawnTime(Long.MAX_VALUE);
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        target.say("隊員中#b" + exMember + "#k先生沒有#i4033611##t4033611#，無法入場。");
                                    }
                                } else {
                                    target.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");
                                }
                            } else {
                                target.say("隊員中#b#e" + overLap + "#n#k今天已經無法挑戰了。");
                            }
                        } else {
                            self.say("所有實例已滿，無法使用。 請使用其他頻道。");
                        }
                    } else if (v0 == 1) { //카오스모드
                        if (em.getProperty("Cstatus0").equals("0")) {
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.ChaosCrimsonQueen.getQuestID(), DBConfig.isGanglim);
                            if (overLap == null) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.ChaosCrimsonQueen.getQuestID());
                                if (lastDate == null || DBConfig.isGanglim) {
                                    String exMember = target.exchangeParty(4033611, -1);
                                    if (exMember == null) { //입장시도
                                        em.setProperty("Cstatus0", "1");
                                        updateLastDate(getPlayer(), QuestExConstants.ChaosCrimsonQueen.getQuestID());
                                        if (DBConfig.isGanglim) {
                                            updateQuestEx(getPlayer(), QuestExConstants.ChaosCrimsonQueen.getQuestID());
                                        }
                                        if (!DBConfig.isGanglim && getPlayer().getPartyMemberSize() > 1) {
                                            for (MapleCharacter partyMember : getPlayer().getPartyMembers()) {
                                            	if (partyMember.getMapId() == getPlayer().getMapId()) {
                                               		partyMember.setMultiMode(true);
                                               		partyMember.applyBMCurseJinMulti();
                                              	}
                                            }
                                        }
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 105200700);
                                        eim.setProperty("mode", "chaos");
                                        getClient().getChannelServer().getMapFactory().getMap(105200700).setLastRespawnTime(0);
                                        getClient().getChannelServer().getMapFactory().getMap(105200700).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200710).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200700).setLastRespawnTime(Long.MAX_VALUE);
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        target.say("隊員中#b" + exMember + "#k先生沒有#i4033611##t4033611#，無法入場。");
                                    }
                                } else {
                                    target.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");
                                }
                            } else {
                                if (DBConfig.isGanglim) {
                                    target.say("隊員中#b#e" + overLap + "#n#k今日已無法挑戰。");
                                } else {
                                    target.say("隊員中#b#e" + overLap + "#n#k本周已無法挑戰。");
                                }
                            }
                        } else {
                            self.say("所有實例已滿，無法使用。 請使用其他頻道。");
                        }
                    } else if (v0 == 2) { //연습모드
                        self.say("練習模式正在準備中。");
                    }
                } else {
                    self.say("隊員們都應該在同一張地圖上。");
                }
            }
        }
    }

    public void bloodyqueenEnter() { //블러디 퀸
    	if (!DBConfig.isGanglim) {
    		rootathirdDoor();
    		return;
    	}
        EventManager em = getEventManager("RootAbyssCrimsonQueen");
        if (em == null) {
            self.say("現在不能使用。");
        } else {
            String text = "#r#e<Lutavis南庭院入口>#n#k\r\n這是通往Lutavis南封印守護者#r Bloody Queen#k守護的庭院的大門。" + (DBConfig.isGanglim ? "清除記錄將於當天午夜初始化。" : "#r清除記錄將在正常情况下於當天午夜初始化，混沌情况下於每週四午夜初始化。 #b") + "\r\n";
            text += "使用#L0##i4033611##t4033611#進入正常模式。 （125級以上）";
            if (DBConfig.isGanglim) {
                text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.CrimsonQueen.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#k";
                getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[諾瑪爾Bloody Queen]今天該老闆"+chr.getOneInfoQuestInteger(QuestExConstants.CrimsonQueen.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
            }
            text += "使用#l\r\n#L1##i403361##t4033611#進入混沌模式。 （180級以上）";
            if (DBConfig.isGanglim) {
                text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.ChaosCrimsonQueen.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#b";
                getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[混沌血色女王]今天該BOSS"+chr.getOneInfoQuestInteger(QuestExConstants.ChaosCrimsonQueen.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
            }
            text += "#l\r\n#L3#進入混沌練習模式。 （180級以上）#l";
            int v0 = self.askMenu(text);
            if (target.getParty() == null) {
                target.say("好像只有舉辦派對才能入場。 找找派對吧。");
            } else {
                if (target.getParty().isPartySameMap()) {
                    if (v0 == 0) { //노말모드
                        if (em.getProperty("status0").equals("0")) {
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.CrimsonQueen.getQuestID(), DBConfig.isGanglim);
                            if (overLap == null) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.CrimsonQueen.getQuestID());
                                if (lastDate == null || DBConfig.isGanglim) {
                                    String exMember = target.exchangeParty(4033611, -1);
                                    if (exMember == null) { //입장시도
                                        em.setProperty("status0", "1");
                                        updateLastDate(getPlayer(), QuestExConstants.CrimsonQueen.getQuestID());
                                        if (DBConfig.isGanglim) {
                                            updateQuestEx(getPlayer(), QuestExConstants.CrimsonQueen.getQuestID());
                                        }
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 105200300);
                                        eim.setProperty("mode", "normal");
                                        getClient().getChannelServer().getMapFactory().getMap(105200300).setLastRespawnTime(0);
                                        getClient().getChannelServer().getMapFactory().getMap(105200300).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200310).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200300).setLastRespawnTime(Long.MAX_VALUE);
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        target.say("隊員中#b" + exMember + "#k先生沒有#i4033611##t4033611#，無法入場。");
                                    }
                                } else {
                                    target.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");
                                }
                            } else {
                                target.say("隊員中#b#e" + overLap + "#n#k今天已經無法挑戰了。");
                            }
                        } else {
                            self.say("所有實例已滿，無法使用。 請使用其他頻道。");
                        }
                    } else if (v0 == 1) { //카오스모드
                        if (em.getProperty("Cstatus0").equals("0")) {
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.ChaosCrimsonQueen.getQuestID(), DBConfig.isGanglim);
                            if (overLap == null) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.ChaosCrimsonQueen.getQuestID());
                                if (lastDate == null || DBConfig.isGanglim) {
                                    String exMember = target.exchangeParty(4033611, -1);
                                    if (exMember == null) { //입장시도
                                        em.setProperty("Cstatus0", "1");
                                        updateLastDate(getPlayer(), QuestExConstants.ChaosCrimsonQueen.getQuestID());
                                        if (DBConfig.isGanglim) {
                                            updateQuestEx(getPlayer(), QuestExConstants.ChaosCrimsonQueen.getQuestID());
                                        }
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 105200700);
                                        eim.setProperty("mode", "chaos");
                                        getClient().getChannelServer().getMapFactory().getMap(105200700).setLastRespawnTime(0);
                                        getClient().getChannelServer().getMapFactory().getMap(105200700).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200710).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200700).setLastRespawnTime(Long.MAX_VALUE);
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        target.say("隊員中#b" + exMember + "#k先生沒有#i4033611##t4033611#，無法入場。");
                                    }
                                } else {
                                    target.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");
                                }
                            } else {
                                if (DBConfig.isGanglim) {
                                    target.say("隊員中#b#e" + overLap + "#n#k今日已無法挑戰。");
                                } else {
                                    target.say("隊員中#b#e" + overLap + "#n#k本周已無法挑戰。");
                                }
                            }
                        } else {
                            self.say("所有實例已滿，無法使用。 請使用其他頻道。");
                        }
                    } else if (v0 == 2) { //연습모드
                        self.say("練習模式正在準備中。");
                    }
                } else {
                    self.say("隊員們都應該在同一張地圖上。");
                }
            }
        }
    }

    public void queen_summon0() {
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (eim.getProperty("summonMOB") == null) {
                eim.setProperty("summonMOB", "1");
                Field field = getPlayer().getMap();
                field.startMapEffect("和沉睡的血女王搭話吧。", 5120025, 3000);
            }
        }
    }

    public void rootaforthDoor() { //벨룸
        //북쪽정원
        //105200400, 105200800
        //벨룸이 보이지 않는다
        // 내 경고를
        initNPC(MapleLifeFactory.getNPC(1064014));
        EventManager em = getEventManager("RootAbyssVellum");
        if (em == null) {
            self.say("現在不能使用。");
        } else {
        	String text = "";
        	if (DBConfig.isGanglim) {
        		text = "#r#e<Lutavis北庭院入口>#n#k\r\n通往Lutavis北封印守護者#r貝魯姆#k守護的庭院的大門。" + (DBConfig.isGanglim ? "清除記錄將於當天午夜初始化。" : "#r清除記錄將在正常情况下於當天午夜初始化，混沌情况下於每週四午夜初始化。 #b") + "\r\n";
                text += "使用#L0##i4033611##t4033611#進入正常模式。 （125級以上）";
                text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.Vellum.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#k";
                getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[諾瑪爾貝魯姆]今天該老闆"+chr.getOneInfoQuestInteger(QuestExConstants.Vellum.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
            	text += "使用#l\r\n#L1##i403361##t4033611#進入混沌模式。 （180級以上）";
                text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.ChaosVellum.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#b";
                getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[卡奧斯貝魯姆]今天將該BOSS"+chr.getOneInfoQuestInteger(QuestExConstants.ChaosVellum.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
            	text += "#l\r\n#L3#進入混沌練習模式。 （180級以上）#l";      
        	}
        	else {
        		boolean single = getPlayer().getPartyMemberSize() == 1;
        		text = "#r#e<Lutavis北庭院入口>#n#k\r\n通往Lutavis北封印守護者#r貝魯姆#k守護的庭院的大門。" + (DBConfig.isGanglim ? "清除記錄將於當天午夜初始化。" : "#r清除記錄將在正常情况下於當天午夜初始化，混沌情况下於每週四午夜初始化。 #b") + "\r\n";
                text += "使用#L0##i403361##t403361#正常模式"  + (single ? "（單曲）" : "（多個）") +  "移動到。 （125級以上）";
            	text += "使用#l\r\n#L1##i403361##t403361#進入混沌模式"  + (single ? "（單曲）" : "（多個）") +  "移動到。 （180級以上）";
            	//text += "#l\r\n#L3#進入混沌練習模式。 （180級以上）#l";
            	int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalVellum" + (single ? "Single" : "Multi"));
                text += "\r\n#L4#正常模式" + (single ? "（單曲）" : "（多個）") + "入場次數新增" + ((single ? 2 : 1) - reset) + "可回收#l";
        	}
                
            int v0 = self.askMenu(text);
            if (target.getParty() == null) {
                target.say("好像只有舉辦派對才能入場。 找找派對吧。");
                return;
            } else {
            	if (v0 == 4 && !DBConfig.isGanglim) {
            		if (getPlayer().getTogetherPoint() < 150) {
            			self.sayOk("合作積分不足。 當前積分：" + getPlayer().getTogetherPoint());
            			return;
            		}
            		boolean single = getPlayer().getPartyMemberSize() == 1;
            		int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalVellum" + (single ? "Single" : "Multi"));
            		if ((reset > 0 && !single) || reset > 1 && single) { //초기화 불가능
                    	self.sayOk("每日追加入場次數新增已全部使用。");
                    	return;
                    }
            		getPlayer().gainTogetherPoint(-150);
            		getPlayer().updateOneInfo(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalVellum" + (single ? "Single" : "Multi"), String.valueOf(reset + 1));
            		self.sayOk("入場次數新增。");
            		return;
            	}
                if (target.getParty().isPartySameMap()) {
                    if (v0 == 0) { //노말모드
                        if (em.getProperty("status0").equals("0")) {
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.Vellum.getQuestID(), DBConfig.isGanglim);
                            if (overLap == null) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.Vellum.getQuestID());
                                if (lastDate == null || DBConfig.isGanglim) {
                                    String exMember = target.exchangeParty(4033611, -1);
                                    if (exMember == null) { //입장시도
                                        em.setProperty("status0", "1");
                                        updateLastDate(getPlayer(), QuestExConstants.Vellum.getQuestID());
                                        if (DBConfig.isGanglim) {
                                            updateQuestEx(getPlayer(), QuestExConstants.Vellum.getQuestID());
                                        }
                                        if (!DBConfig.isGanglim && getPlayer().getPartyMemberSize() > 1) {
                                            for (MapleCharacter partyMember : getPlayer().getPartyMembers()) {
                                            	if (partyMember.getMapId() == getPlayer().getMapId()) {
                                               		partyMember.setMultiMode(true);
                                               		partyMember.applyBMCurseJinMulti();
                                              	}
                                            }
                                        }
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 105200400);
                                        eim.setProperty("mode", "normal");
                                        getClient().getChannelServer().getMapFactory().getMap(105200400).setLastRespawnTime(0);
                                        getClient().getChannelServer().getMapFactory().getMap(105200400).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200410).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200400).setLastRespawnTime(Long.MAX_VALUE);
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        target.say("隊員中#b" + exMember + "#k先生沒有#i4033611##t4033611#，無法入場。");
                                    }
                                } else {
                                    target.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");
                                }
                            } else {
                                target.say("隊員中#b#e" + overLap + "#n#k今天已經無法挑戰了。");
                            }
                        } else {
                            self.say("所有實例已滿，無法使用。 請使用其他頻道。");
                        }
                    } else if (v0 == 1) { //카오스모드
                        if (em.getProperty("Cstatus0").equals("0")) {
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.ChaosVellum.getQuestID(), DBConfig.isGanglim);
                            if (overLap == null) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.ChaosVellum.getQuestID());
                                if (lastDate == null || DBConfig.isGanglim) {
                                    String exMember = target.exchangeParty(4033611, -1);
                                    if (exMember == null) { //입장시도
                                        em.setProperty("Cstatus0", "1");
                                        updateLastDate(getPlayer(), QuestExConstants.ChaosVellum.getQuestID());
                                        if (DBConfig.isGanglim) {
                                            updateQuestEx(getPlayer(), QuestExConstants.ChaosVellum.getQuestID());
                                        }
                                        if (!DBConfig.isGanglim && getPlayer().getPartyMemberSize() > 1) {
                                            for (MapleCharacter partyMember : getPlayer().getPartyMembers()) {
                                            	if (partyMember.getMapId() == getPlayer().getMapId()) {
                                               		partyMember.setMultiMode(true);
                                               		partyMember.applyBMCurseJinMulti();
                                              	}
                                            }
                                        }
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 105200800);
                                        eim.setProperty("mode", "chaos");
                                        getClient().getChannelServer().getMapFactory().getMap(105200800).setLastRespawnTime(0);
                                        getClient().getChannelServer().getMapFactory().getMap(105200800).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200810).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200800).setLastRespawnTime(Long.MAX_VALUE);
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        target.say("隊員中#b" + exMember + "#k先生沒有#i4033611##t4033611#，無法入場。");
                                    }
                                } else {
                                    target.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");
                                }
                            } else {
                                if (DBConfig.isGanglim) {
                                    target.say("隊員中#b#e" + overLap + "#n#k今日已無法挑戰。");
                                } else {
                                    target.say("隊員中#b#e" + overLap + "#n#k本周已無法挑戰。");
                                }
                            }
                        } else {
                            self.say("所有實例已滿，無法使用。 請使用其他頻道。");
                        }
                    } else if (v0 == 2) { //연습모드
                        self.say("練習模式正在準備中。");
                    }
                } else {
                    self.say("隊員們都應該在同一張地圖上。");
                }
            }
        }
    }

    public void bellumEnter() { //벨룸
    	if (!DBConfig.isGanglim) {
    		rootaforthDoor();
    		return;
    	}
        EventManager em = getEventManager("RootAbyssVellum");
        if (em == null) {
            self.say("現在不能使用。");
        } else {
            String text = "#r#e<Lutavis北庭院入口>#n#k\r\n通往Lutavis北封印守護者#r貝魯姆#k守護的庭院的大門。" + (DBConfig.isGanglim ? "清除記錄將於當天午夜初始化。" : "#r清除記錄將在正常情况下於當天午夜初始化，混沌情况下於每週四午夜初始化。 #b") + "\r\n";
            text += "使用#L0##i4033611##t4033611#進入正常模式。 （125級以上）";
            if (DBConfig.isGanglim) {
                text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.Vellum.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#k";
                getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[諾瑪爾貝魯姆]今天該老闆"+chr.getOneInfoQuestInteger(QuestExConstants.Vellum.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
            }
            text += "使用#l\r\n#L1##i403361##t4033611#進入混沌模式。 （180級以上）";
            if (DBConfig.isGanglim) {
                text += " #r[" + getPlayer().getOneInfoQuestInteger(QuestExConstants.ChaosVellum.getQuestID(), "eNum") + "/" + (getPlayer().getBossTier() + 1) + "]#b";
                getPlayer().getPartyMembers().forEach(chr -> chr.dropMessage(5, "[卡奧斯貝魯姆]今天將該BOSS"+chr.getOneInfoQuestInteger(QuestExConstants.ChaosVellum.getQuestID(), "eNum")+"號入場了。 槍"+(chr.getBossTier() + 1)+"號可以入場。"));
            }
            text += "#l\r\n#L3#進入混沌練習模式。 （180級以上）#l";
            int v0 = self.askMenu(text);
            if (target.getParty() == null) {
                target.say("好像只有舉辦派對才能入場。 找找派對吧。");
            } else {
                if (target.getParty().isPartySameMap()) {
                    if (v0 == 0) { //노말모드
                        if (em.getProperty("status0").equals("0")) {
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.Vellum.getQuestID(), DBConfig.isGanglim);
                            if (overLap == null) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.Vellum.getQuestID());
                                if (lastDate == null || DBConfig.isGanglim) {
                                    String exMember = target.exchangeParty(4033611, -1);
                                    if (exMember == null) { //입장시도
                                        em.setProperty("status0", "1");
                                        updateLastDate(getPlayer(), QuestExConstants.Vellum.getQuestID());
                                        if (DBConfig.isGanglim) {
                                            updateQuestEx(getPlayer(), QuestExConstants.Vellum.getQuestID());
                                        }
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 105200400);
                                        eim.setProperty("mode", "normal");
                                        getClient().getChannelServer().getMapFactory().getMap(105200400).setLastRespawnTime(0);
                                        getClient().getChannelServer().getMapFactory().getMap(105200400).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200410).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200400).setLastRespawnTime(Long.MAX_VALUE);
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        target.say("隊員中#b" + exMember + "#k先生沒有#i4033611##t4033611#，無法入場。");
                                    }
                                } else {
                                    target.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");
                                }
                            } else {
                                target.say("隊員中#b#e" + overLap + "#n#k今天已經無法挑戰了。");
                            }
                        } else {
                            self.say("所有實例已滿，無法使用。 請使用其他頻道。");
                        }
                    } else if (v0 == 1) { //카오스모드
                        if (em.getProperty("Cstatus0").equals("0")) {
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.ChaosVellum.getQuestID(), DBConfig.isGanglim);
                            if (overLap == null) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.ChaosVellum.getQuestID());
                                if (lastDate == null || DBConfig.isGanglim) {
                                    String exMember = target.exchangeParty(4033611, -1);
                                    if (exMember == null) { //입장시도
                                        em.setProperty("Cstatus0", "1");
                                        updateLastDate(getPlayer(), QuestExConstants.ChaosVellum.getQuestID());
                                        if (DBConfig.isGanglim) {
                                            updateQuestEx(getPlayer(), QuestExConstants.ChaosVellum.getQuestID());
                                        }
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 105200800);
                                        eim.setProperty("mode", "chaos");
                                        getClient().getChannelServer().getMapFactory().getMap(105200800).setLastRespawnTime(0);
                                        getClient().getChannelServer().getMapFactory().getMap(105200800).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200810).resetFully(true);
                                        getClient().getChannelServer().getMapFactory().getMap(105200800).setLastRespawnTime(Long.MAX_VALUE);
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        target.say("隊員中#b" + exMember + "#k先生沒有#i4033611##t4033611#，無法入場。");
                                    }
                                } else {
                                    target.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");
                                }
                            } else {
                                if (DBConfig.isGanglim) {
                                    target.say("隊員中#b#e" + overLap + "#n#k今日已無法挑戰。");
                                } else {
                                    target.say("隊員中#b#e" + overLap + "#n#k本周已無法挑戰。");
                                }
                            }
                        } else {
                            self.say("所有實例已滿，無法使用。 請使用其他頻道。");
                        }
                    } else if (v0 == 2) { //연습모드
                        self.say("練習模式正在準備中。");
                    }
                } else {
                    self.say("隊員們都應該在同一張地圖上。");
                }
            }
        }
    }

    public void abysscave_ent() {
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (eim.getProperty("summonMOB") == null) {
                eim.setProperty("summonMOB", "1");
                Field field = getPlayer().getMap();
                field.startMapEffect("看不見貝魯姆。 去調查祭壇附近吧。", 5120025, 3000);
            }
        }
    }

    public void rootabyssOut() {
        initNPC(MapleLifeFactory.getNPC(1064012));
        if (target.askYesNo("鑰匙已經用掉了。 因為很可惜消耗的鑰匙，所以比起就這樣出去，處理掉老闆好像更好。 .那也直接出去吧？") == 1) {
            if (getPlayer().getEventInstance() != null) {
                getPlayer().setRegisterTransferFieldTime(0);
                getPlayer().setRegisterTransferField(0);
            }
            registerTransferField(105200000);
        } else {
            target.sayOk("既然用了鑰匙進來了，就連老闆也通關吧。");
        }
    }

    public void outrootaBoss() {
        initNPC(MapleLifeFactory.getNPC(1064012));
        if (target.askYesNo("戰鬥結束後出去嗎？") == 1) {
            if (getPlayer().getEventInstance() != null) {
                getPlayer().setRegisterTransferFieldTime(0);
                getPlayer().setRegisterTransferField(0);
            }
            registerTransferField(105200000);
        }
    }

    public void rootaBossOut() {
        initNPC(MapleLifeFactory.getNPC(1064012));
        if (target.askYesNo("戰鬥結束後出去嗎？") == 1) {
            if (getPlayer().getEventInstance() != null) {
                getPlayer().setRegisterTransferFieldTime(0);
                getPlayer().setRegisterTransferField(0);
            }
            registerTransferField(105200000);
        }
    }

    public void rootaNext() {
        initNPC(MapleLifeFactory.getNPC(1064012));
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (getPlayer().getMap().getAllMonster().size() == 0) {
                if (target.askYesNo("那我們移動吧？") == 1) {
                    if (eim.getProperty("stage1") == null) {
                        eim.setProperty("stage1", "clear");
                        target.getParty().registerTransferField(target.getMapId() + 10);
                    }
                } else {
                    target.sayOk("再準備一下再移動吧。");
                }
            } else {
                getPlayer().getMap().broadcastMessage(CWvsContext.getScriptProgressMessage("請先除掉庭院內的小惡魔。"));
                getPlayer().getMap().broadcastMessage(CWvsContext.serverNotice(5, "請先除掉庭院內的小惡魔。"));
            }
        }
    }
}
