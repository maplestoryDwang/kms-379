package script.Boss;

import constants.QuestExConstants;
import database.DBConfig;
import objects.fields.Field;
import objects.fields.gameobject.Reactor;
import objects.fields.gameobject.lifes.ChangeableStats;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.newscripting.ScriptEngineNPC;

import java.awt.*;

public class Horntail extends ScriptEngineNPC {

    public void hontale_enterToE() { //hontale 실화냐 ;;
        if (target.getParty() == null) {
            self.say("請通過派對現場進行。");
        } else {
            if (target.getParty().getLeader().getId() != target.getId()) {
                self.say("請通過派對現場進行。");
            } else {
                if (self.askYesNo("石板上寫的字閃閃發光，石板後面開了一扇小門。 您要使用秘密通道嗎？") == 1) {
                    if (target.getParty().isPartySameMap()) {
                        target.getParty().registerTransferField(240050400); //파티원 전체이동!
                    } else {
                        self.say(target.getParty().getPartyMemberList().size() + "名稱必須在同一映射中。");
                    }
                } else {
                    self.say("如果你要移動的話，請重新和我說話。");
                }
            }
        }
    }

    public void hontale_accept() {
        //이지, 노말혼테일 첫시작맵(240060000, 240060010, 240060020, 240060030, 240060040, 240060050, 240060060, 240060070)
        //카오스혼테일 첫시작맵(240060001, 240060011, 240060021, 240060031, 240060041, 240060051, 240060061, 240060071)
        int[] normalHortailMaps = new int[]{240060000, 240060010, 240060020, 240060030, 240060040, 240060050, 240060060, 240060070};
        int[] chaosHortailMaps = new int[]{240060001, 240060011, 240060021, 240060031, 240060041, 240060051, 240060061, 240060071};
        EventManager em = getEventManager("Horntail");
        if (em == null) {
            self.say("現時無法使用魂尾BOSS RAID。");
        } else {
            int v = self.askMenu("#e<老闆：魂尾>#n\r\n魂尾復活了。 如果放任不管，我會引發火山爆發，把米納爾一帶變成地獄。 \r\n#b\r\n#L0#<老闆：魂尾>申請入場。 #l");
            if (v == 0) {
            	String menu = "";

            	if (DBConfig.isGanglim) {
            		menu = "#e<老闆：魂尾>#n\r\n請選擇所需模式。 \r\n\r\n#L0#异地模式（等級130以上）#l\r\n#L1#正常模式（等級130以上）#l\r\n#L2#混沌模式（等級135以上）#l";
            	} else {
            		boolean single = getPlayer().getPartyMemberSize() == 1;
            		menu = "#e<老闆：魂尾>#n\r\n請選擇所需模式。 \r\n\r\n"
            				+ "#L0#异地模式" + (single ? "（單曲）" : "（多個）") + "（130級以上）#l\r\n"
            				+ "#L1#正常模式" + (single ? "（單曲）" : "（多個）") + "（130級以上）#l\r\n"
            				+ "#L2#混沌模式"  + (single ? "（單曲）" : "（多個）") + "（135級以上）#l\r\n";
            		int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "Horntail" + (single ? "Single" : "Multi"));
            		menu += "#L3#入場次數新增（" + ((single ? 2 : 1) - reset) + "可回收）#l";
            	}
                int v2 = self.askMenu(menu);

                if (target.getParty() == null) {
                    self.say("請通過派對現場進行。");
                    return;
                } else {
                	if (v2 == 3 && !DBConfig.isGanglim) {
                		boolean single = getPlayer().getPartyMemberSize() == 1;
                		if (getPlayer().getTogetherPoint() < 150) {
                			self.sayOk("合作積分不足。 擁有積分：" + getPlayer().getTogetherPoint());
                			return;
                		}
                		int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "Horntail" + (single ? "Single" : "Multi"));
                		if (reset > (single ? 1 : 0)) {
                			self.sayOk("今天無法新增可入場次數。");
                			return;
                		}
                		getPlayer().gainTogetherPoint(-150);
                		getPlayer().updateOneInfo(QuestExConstants.DailyQuestResetCount.getQuestID(), "Horntail" + (single ? "Single" : "Multi"), String.valueOf(reset + 1));
                		self.sayOk("入場次數新增。");
                		return;
                	}

                    if (target.getParty().getLeader().getId() != target.getId()) {
                        self.say("請通過派對現場進行。");
                    } else {
                        if (target.getParty().isPartySameMap()) {
                            // 이미 누가 안에 있는지 부터 알아보자
                            int[] fields = new int[]{};
                            int map = normalHortailMaps[0];
                            if (v2 == 2) {
                                map = chaosHortailMaps[0];
                            }
                            if (v2 == 0 || v2 == 1) {
                                fields = new int[]{map, map + 100, map + 200, map + 300};
                            } else if (v2 == 2) {
                                fields = new int[]{map, map + 100, map + 200};
                            }
                            boolean findUser = false;
                            for (int field : fields) {
                                Field fie = getClient().getChannelServer().getMapFactory().getMap(field);
                                if (fie.getCharactersSize() > 0) {
                                    findUser = true;
                                }
                            }
                            if (findUser) {
                                if (v2 == 0 || v2 == 1) {
                                    self.say("現在，諾馬爾地圖已滿，無法使用。 請使用其他頻道。");
                                } else if (v2 == 2) {
                                    self.say("現時混沌地圖已滿，無法使用。 請使用其他頻道。");
                                }
                                return;
                            }

                            int canEnter = -1;
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.Horntail.getQuestID(), DBConfig.isGanglim);
                            if (overLap != null) {
                                v2 = 3;
                                canEnter = -2;
                            }
                            String mode = "easy";
                            if (v2 == 0 || v2 == 1) { //이지, 노말모드
                                if (em.getProperty("status0").equals("0")) {
                                    em.setProperty("status0", "1");
                                    canEnter = 0;
                                    if (v2 == 1) {
                                        mode = "normal";
                                    }
                                }
                            } else if (v2 == 2) { //카오스모드
                                em.setProperty("Cstatus0", "0");
                                if (em.getProperty("Cstatus0").equals("0")) {
                                    em.setProperty("Cstatus0", "1");
                                    canEnter = 0;
                                    mode = "chaos";
                                }
                            }

                            if (canEnter == -1 || canEnter == -2) {
                                if (canEnter == -2) {
                                    self.say("隊員中#b#e" + overLap + "今天入場了。 那麼今天就不能再進去了。");
                                } else {
                                    self.say("當前所有地圖已滿，無法使用。 請使用其他頻道。");
                                }
                            } else if (canEnter == 0){
                                EventInstanceManager eim = em.readyInstance();
                                eim.setProperty("map", map);
                                eim.setProperty("mode", mode);
                                for (int field : fields) {
                                    Field fie = getClient().getChannelServer().getMapFactory().getMap(field);
                                    fie.resetFully(false);
                                }
                                boolean single = getPlayer().getPartyMemberSize() == 1;
                                if (!DBConfig.isGanglim && !single) {
                                	for (MapleCharacter partyMember : getPlayer().getPartyMembers()) {
                                		if (partyMember.getMapId() == getPlayer().getMapId()) {
                                			partyMember.setMultiMode(true);
                                			partyMember.applyBMCurseJinMulti();
                                		}
                                	}
                                }
                                updateEventNumber(getPlayer(), QuestExConstants.Horntail.getQuestID());
                                eim.registerParty(target.getParty(), getPlayer().getMap());
                            }
                        } else { //파티원 모두 같은맵에 없을 때
                            self.say(target.getParty().getPartyMemberList().size() + "名稱必須在同一映射中。");
                        }
                    }
                }
            }
        }
    }

    public void hontale_out() {
        if (target.getMapId() == 240050400) {
            if (self.askYesNo("您要返回#m240000000#嗎？") == 1) {
                registerTransferField(240050000);
            } else {
                self.say("請重新考慮後再跟我搭話。");
            }
        } else {
            if (self.askYesNo("你要停止戰鬥，出去嗎？ 退場時，今天不能再入場。") == 1) {
                //횟수 차감!
                registerTransferField(240050400);
            } else {
                self.say("請重新考慮後再跟我搭話。");
            }
        }
    }

    public void hontale_boss1() {
        EventInstanceManager eim = getPlayer().getEventInstance();
        if (eim != null) {
            if (eim.getProperty("boss1") == null) {
                eim.setProperty("boss1", "1");
                Reactor tremble = getPlayer().getMap().getReactorByName("tremble1");
                if (tremble != null) {
                	if (DBConfig.isGanglim) {
                		int mobId = 8810200;
                        if (eim.getProperty("mode").equals("normal")) {
                            mobId = 8810000;
                        } else if (eim.getProperty("mode").equals("chaos")) { //카오스모드
                            mobId = 8810100;
                        }
                        tremble.getMap().spawnMonster(MapleLifeFactory.getMonster(mobId), new Point(tremble.getPosition().x - 90, tremble.getPosition().y + 5), -2); //이거 본섭화 맞음ㅋㅋ
                        mapMessage(6, "洞穴深處出現了可怕的生命體。");
                        tremble.forceHitReactor((byte)0);
                        tremble.forceHitReactor((byte)1);
                	}
                	else {
                		if (getPlayer().getPartyMemberSize() == 1) {
                			int mobId = 8810200;
                            if (eim.getProperty("mode").equals("normal")) {
                                mobId = 8810000;
                            } else if (eim.getProperty("mode").equals("chaos")) { //카오스모드
                                mobId = 8810100;
                            }
                            tremble.getMap().spawnMonster(MapleLifeFactory.getMonster(mobId), new Point(tremble.getPosition().x - 90, tremble.getPosition().y + 5), -2); //이거 본섭화 맞음ㅋㅋ
                            mapMessage(6, "洞穴深處出現了可怕的生命體。");
                            tremble.forceHitReactor((byte)0);
                            tremble.forceHitReactor((byte)1);
                		}
                		else {
                			int mobId = 8810200;
                            if (eim.getProperty("mode").equals("normal")) {
                                mobId = 8810000;
                            } else if (eim.getProperty("mode").equals("chaos")) { //카오스모드
                                mobId = 8810100;
                            }
                            final MapleMonster horntail = MapleLifeFactory.getMonster(mobId);
                            horntail.setPosition(new Point(tremble.getPosition().x - 90, tremble.getPosition().y + 5));
                            final long orghp = horntail.getMobMaxHp();
                            ChangeableStats cs = new ChangeableStats(horntail.getStats());
                            cs.hp = orghp * 3L;
                            if (cs.hp < 0) {
                            	cs.hp = Long.MAX_VALUE;
                            }
                            horntail.getStats().setHp(cs.hp);
                            horntail.getStats().setMaxHp(cs.hp);
                            horntail.setOverrideStats(cs);
                            tremble.getMap().spawnMonster(horntail, -2);
                            mapMessage(6, "洞穴深處出現了可怕的生命體。");
                            tremble.forceHitReactor((byte)0);
                            tremble.forceHitReactor((byte)1);
                		}
                	}
                }
            }
        }
    }

    public void hontale_boss2() {
        EventInstanceManager eim = getPlayer().getEventInstance();
        if (eim != null) {
            if (eim.getProperty("boss2") == null) {
                eim.setProperty("boss2", "1");
                Reactor tremble = getPlayer().getMap().getReactorByName("tremble2");
                if (tremble != null) {
                	if (DBConfig.isGanglim) {
                		int mobId = 8810201;
                        if (eim.getProperty("mode").equals("normal")) {
                            mobId = 8810001;
                        } else if (eim.getProperty("mode").equals("chaos")) { //카오스모드
                            mobId = 8810101;
                        }
                        mapMessage(6, "洞穴深處出現了可怕的生命體。");
                        tremble.getMap().spawnMonster(MapleLifeFactory.getMonster(mobId), new Point(tremble.getPosition().x + 89, tremble.getPosition().y - 21), -2);
                        tremble.forceHitReactor((byte)0);
                        tremble.forceHitReactor((byte)1);
                	}
                	else {
                		if (getPlayer().getPartyMemberSize() == 1) {
                			int mobId = 8810201;
                            if (eim.getProperty("mode").equals("normal")) {
                                mobId = 8810001;
                            } else if (eim.getProperty("mode").equals("chaos")) { //카오스모드
                                mobId = 8810101;
                            }
                            mapMessage(6, "洞穴深處出現了可怕的生命體。");
                            tremble.getMap().spawnMonster(MapleLifeFactory.getMonster(mobId), new Point(tremble.getPosition().x + 89, tremble.getPosition().y - 21), -2);
                            tremble.forceHitReactor((byte)0);
                            tremble.forceHitReactor((byte)1);
                		}
                		else {
                			int mobId = 8810201;
                            if (eim.getProperty("mode").equals("normal")) {
                                mobId = 8810001;
                            } else if (eim.getProperty("mode").equals("chaos")) { //카오스모드
                                mobId = 8810101;
                            }
                            final MapleMonster horntail = MapleLifeFactory.getMonster(mobId);
                            horntail.setPosition(new Point(tremble.getPosition().x + 89, tremble.getPosition().y - 21));
                            final long orghp = horntail.getMobMaxHp();
                            ChangeableStats cs = new ChangeableStats(horntail.getStats());
                            cs.hp = orghp * 3L;
                            if (cs.hp < 0) {
                            	cs.hp = Long.MAX_VALUE;
                            }
                            horntail.getStats().setHp(cs.hp);
                            horntail.getStats().setMaxHp(cs.hp);
                            horntail.setOverrideStats(cs);
                            tremble.getMap().spawnMonster(horntail, -2);
                            mapMessage(6, "洞穴深處出現了可怕的生命體。");
                            tremble.forceHitReactor((byte)0);
                            tremble.forceHitReactor((byte)1);
                		}
                	}
                }
            }
        }
    }

    public void hontale_BR() {
        EventInstanceManager eim = getPlayer().getEventInstance();
        if (eim != null) {
            int enMap = 240060000;
            int chaosMap = 240060001;
            if (eim.getProperty("stage1") != null && (target.getMapId() == enMap || target.getMapId() == chaosMap)) { //1단계
                playPortalSE();
                getPlayer().changeMap(target.getMapId() + 100);
            } else if (eim.getProperty("stage2") != null && (target.getMapId() == enMap+100 || target.getMapId() == chaosMap + 100)) {
                playPortalSE();
                if (eim.getProperty("mode").equals("easy")) {
                    getPlayer().changeMap(target.getMapId() + 200);
                } else {
                    getPlayer().changeMap(target.getMapId() + 100);
                }
            } else {
                getPlayer().dropMessage(5, "現在還不能使用這個掠奪。");
            }
        }
    }
}
