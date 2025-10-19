package script.Boss;

import constants.QuestExConstants;
import database.DBConfig;
import objects.fields.Field;
import objects.fields.gameobject.lifes.ChangeableStats;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

import java.awt.*;

public class Cygnus extends ScriptEngineNPC {

    public void in_cygnus() {
        //인스턴스맵 271040300, 271041300(볼품없는정원)
        initNPC(MapleLifeFactory.getNPC(2143004));
        if (DBConfig.isGanglim) {
            getPlayer().dropMessage(5, "現時無法參與墮落的Signus。");
            return;
        }
        EventManager em = getEventManager("Cygnus");
        if (em == null) {
            self.say("現時無法使用Signus RAID。");
        } else {
            if (target.getMapId() >= 271040100 && target.getMapId() <= 271040199) { //노말시그 전투맵
                int v0 = self.askYesNo("戰鬥結束後，在Signus的支持下退場嗎？");
                if (v0 == 1) {
                    registerTransferField(271040200); //노말시그너스 퇴장맵
                }
            } else if (target.getMapId() >= 271041100 && target.getMapId() <= 271041109) { //이지시그
                int v0 = self.askYesNo("戰鬥結束後，在Signus的支持下退場嗎？");
                if (v0 == 1) {
                    registerTransferField(271041200); //이지시그너스 퇴장맵
                }
            } else if (target.getMapId() == 271040000 || target.getMapId() == 271041000) { //노말시그, 이지시그너스 입장맵
                if (target.getParty() == null) {
                    self.say("1人以上組隊才能入場。");
                } else {
                    if (DBConfig.isGanglim && getPlayer().getParty().getLeader().getId() != target.getId()) {
                        self.say("請通過派對現場進行。");
                    } else {
                        boolean normalCygnus = target.getMapId() == 271040000;
                        if (!normalCygnus && DBConfig.isGanglim) {
                            getPlayer().dropMessage(5, "現時無法參與墮落的Signus。");
                            return;
                        }
                        String v = "";
                        if (DBConfig.isGanglim) {
                        	v = "準備好參加墮落的Signus（Izzie）了嗎？ \r\n#b\r\n#L0#申請進入Signus（异地）。 #l\r\n#L1#申請進入Signus練習模式。 #l";
                            if (normalCygnus) {
                                v = "準備好對抗墮落的西格納斯了嗎？ \r\n#b\r\n#L0#申請進入Signus（正常）。 #l\r\n#L1#申請進入Signus（正常）練習模式。 #l";
                            }
                        }
                        else {
                        	boolean single = getPlayer().getPartyMemberSize() == 1;
                        	int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "Cygnus" + (single ? "Single" : "Multi"));
                        	v = "準備好參加墮落的Signus（Izzie）了嗎？ \r\n#b\r\n"
                        			+ "#L0#西格納斯（李智）" + (single ? "（單曲）" : "（多個）") + "申請入場。 #l\r\n"
                        			+ "#L1#西格納斯（李智）" + (single ? "（單曲）" : "（多個）") + "申請進入練習模式。 #l\r\n";
                            if (normalCygnus) {
                                v = "準備好對抗墮落的西格納斯了嗎？ \r\n#b\r\n"
                                	+ "#L0#Signus（正常）" + (single ? "（單曲）" : "（多個）") + "申請入場。 #l\r\n"
                                	+ "#L1#Signus（正常）" + (single ? "（單曲）" : "（多個）") + "申請進入練習模式。 #l\r\n";
                            }
                        }
                        int v0 = self.askMenu(v);
                        if (target.getParty().isPartySameMap()) {
                            boolean canEnter = false;
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.Cygnus.getQuestID()); //시그너스는 격파시 클리어처리됨!!
                            if (overLap == null && v0 != 1) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.Cygnus.getQuestID());
                                if (lastDate == null) {
                                    if (v0 == 0) {
                                        //271040000 ~ 271040199 (무슨 맵을 199개나쓰냐;;)
                                        //271041100 ~ 271041109 (노말시그 맵)
                                        int instanceMapID = 271041100; //이지시그 전투맵
                                        if (target.getMapId() == 271040000) { //노말시그맵
                                            instanceMapID = 271040100;
                                        }
                                        String mode = "easy";
                                        if (instanceMapID == 271040100) {
                                            if (em.getProperty("status0").equals("0")) {
                                                canEnter = true;
                                            }
                                        } else {
                                            if (em.getProperty("Nstatus0").equals("0")) {
                                                mode = "normal";
                                                canEnter = true;
                                            }
                                        }
                                        if (!canEnter) { //입장이 불가능한 경우 맵에 유저가 없는지 체크 후 인스턴스 초기화
                                        	if (getClient().getChannelServer().getMapFactory().getMap(instanceMapID).getCharacters().size() == 0) {
                                        		String rt = em.getProperty("ResetTime");
                                        		long curTime = System.currentTimeMillis();
                                        		long time = rt == null ? 0 : Long.parseLong(rt);
                                        		if (time == 0) {
                                        			em.setProperty("ResetTime", String.valueOf(curTime));
                                        		}
												else if (time - curTime >= 10000) { // 10초이상 맵이 빈경우 입장가능하게 변경
													canEnter = true;
													em.setProperty("ResetTime", "0");
												}
                                        	}
                                        }
                                        if (canEnter) {
                                            if (mode.equals("easy")) {
                                                em.setProperty("status0", "1");
                                            } else {
                                                em.setProperty("Nstatus0", "1");
                                            }
                                            EventInstanceManager eim = em.readyInstance();
                                            eim.setProperty("map", instanceMapID);
                                            eim.setProperty("mode", mode);
                                            getClient().getChannelServer().getMapFactory().getMap(instanceMapID).resetFully(false);
                                            boolean single = getPlayer().getPartyMemberSize() == 1;
                                            if (!DBConfig.isGanglim && !single) {
                                            	for (MapleCharacter partyMember : getPlayer().getPartyMembers()) {
                                            		if (partyMember.getMapId() == getPlayer().getMapId()) {
                                            			partyMember.setMultiMode(true);
                                            			partyMember.applyBMCurseJinMulti();
                                            		}
                                            	}
                                            }
                                            updateLastDate(getPlayer(), QuestExConstants.Cygnus.getQuestID());
                                            eim.registerParty(target.getParty(), getPlayer().getMap());
                                        } else {
                                            self.sayOk("當前所有地圖已滿，無法使用。 請使用其他頻道。");
                                        }
                                    }
                                } else {
                                    self.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");//본메 : 30분 이내에 입장한 파티원이 있습니다. 이지 및 노멀 모드를 통합하여 입장 후 30분 이내에 재입장이 불가능합니다.
                                }
                            } else {
                                if (v0 == 1) {
                                    self.say("目前正在準備練習模式。");
                                } else {
                                    self.say("最近一周內有一個隊員完成了Signus的通關。 Signus（Izzie）、Signus（普通）加起來每週只能通關1次。 \r\n#r#e<清除歷史記錄將於每週四進行批量初始化。 >#k#n");
                                }
                            }
                        } else {
                            self.say(target.getParty().getPartyMemberList().size() + "名稱必須在同一映射中。");
                        }
                    }
                }
            }
        }
    }

    public void cygnus_accept() {
    	if (!DBConfig.isGanglim) {
    		in_cygnus();
    		return;
    	}
        //인스턴스맵 271040300, 271041300(볼품없는정원)
        if (DBConfig.isGanglim) {
            getPlayer().dropMessage(5, "現時無法參與墮落的Signus。");
            return;
        }
        EventManager em = getEventManager("Cygnus");
        if (em == null) {
            self.say("現時無法使用Signus RAID。");
        } else {
            if (target.getMapId() >= 271040100 && target.getMapId() <= 271040199) { //노말시그 전투맵
                int v0 = self.askYesNo("戰鬥結束後，在Signus的支持下退場嗎？");
                if (v0 == 1) {
                    registerTransferField(271040200); //노말시그너스 퇴장맵
                }
            } else if (target.getMapId() >= 271041100 && target.getMapId() <= 271041109) { //이지시그
                int v0 = self.askYesNo("戰鬥結束後，在Signus的支持下退場嗎？");
                if (v0 == 1) {
                    registerTransferField(271041200); //이지시그너스 퇴장맵
                }
            } else if (target.getMapId() == 271040000 || target.getMapId() == 271041000) { //노말시그, 이지시그너스 입장맵
                if (target.getParty() == null) {
                    self.say("1人以上組隊才能入場。");
                } else {
                    if (target.getParty().getLeader().getId() != target.getId()) {
                        self.say("請通過派對現場進行。");
                    } else {
                        boolean normalCygnus = target.getMapId() == 271040000;
                        if (!normalCygnus && DBConfig.isGanglim) {
                            getPlayer().dropMessage(5, "現時無法參與墮落的Signus。");
                            return;
                        }
                        String v = "準備好參加墮落的Signus（Izzie）了嗎？ \r\n#b\r\n#L0#申請進入Signus（异地）。 #l\r\n#L1#申請進入Signus練習模式。 #l";
                        if (normalCygnus) {
                            v = "準備好對抗墮落的西格納斯了嗎？ \r\n#b\r\n#L0#申請進入Signus（正常）。 #l\r\n#L1#申請進入Signus（正常）練習模式。 #l";
                        }
                        int v0 = self.askMenu(v);
                        if (target.getParty().isPartySameMap()) {
                            boolean canEnter = false;
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.Cygnus.getQuestID()); //시그너스는 격파시 클리어처리됨!!
                            if (overLap == null && v0 != 1) {
                                String lastDate = checkEventLastDate(getPlayer(), QuestExConstants.Cygnus.getQuestID());
                                if (lastDate == null) {
                                    if (v0 == 0) {
                                        //271040000 ~ 271040199 (무슨 맵을 199개나쓰냐;;)
                                        //271041100 ~ 271041109 (노말시그 맵)
                                        int instanceMapID = 271041100; //이지시그 전투맵
                                        if (target.getMapId() == 271040000) { //노말시그맵
                                            instanceMapID = 271040100;
                                        }
                                        String mode = "easy";
                                        if (instanceMapID == 271040100) {
                                            if (em.getProperty("status0").equals("0")) {
                                                canEnter = true;
                                            }
                                        } else {
                                            if (em.getProperty("Nstatus0").equals("0")) {
                                                mode = "normal";
                                                canEnter = true;
                                            }
                                        }
                                        if (!canEnter) { //입장이 불가능한 경우 맵에 유저가 없는지 체크 후 인스턴스 초기화
                                        	if (getClient().getChannelServer().getMapFactory().getMap(instanceMapID).getCharacters().size() == 0) {
                                        		String rt = em.getProperty("ResetTime");
                                        		long curTime = System.currentTimeMillis();
                                        		long time = rt == null ? 0 : Long.parseLong(rt);
                                        		if (time == 0) {
                                        			em.setProperty("ResetTime", String.valueOf(curTime));
                                        		}
												else if (time - curTime >= 10000) { // 10초이상 맵이 빈경우 입장가능하게 변경
													canEnter = true;
													em.setProperty("ResetTime", "0");
												}
                                        	}
                                        }
                                        if (canEnter) {
                                            if (mode.equals("easy")) {
                                                em.setProperty("status0", "1");
                                            } else {
                                                em.setProperty("Nstatus0", "1");
                                            }
                                            EventInstanceManager eim = em.readyInstance();
                                            eim.setProperty("map", instanceMapID);
                                            eim.setProperty("mode", mode);
                                            getClient().getChannelServer().getMapFactory().getMap(instanceMapID).resetFully(false);
                                            updateLastDate(getPlayer(), QuestExConstants.Cygnus.getQuestID());
                                            eim.registerParty(target.getParty(), getPlayer().getMap());
                                        } else {
                                            self.sayOk("當前所有地圖已滿，無法使用。 請使用其他頻道。");
                                        }
                                    }
                                } else {
                                    self.say("隊員中#b#e" + lastDate + "#n#k後可以重新入場。");//본메 : 30분 이내에 입장한 파티원이 있습니다. 이지 및 노멀 모드를 통합하여 입장 후 30분 이내에 재입장이 불가능합니다.
                                }
                            } else {
                                if (v0 == 1) {
                                    self.say("目前正在準備練習模式。");
                                } else {
                                    self.say("最近一周內有一個隊員完成了Signus的通關。 Signus（Izzie）、Signus（普通）加起來每週只能通關1次。 \r\n#r#e<清除歷史記錄將於每週四進行批量初始化。 >#k#n");
                                }
                            }
                        } else {
                            self.say(target.getParty().getPartyMemberList().size() + "名稱必須在同一映射中。");
                        }
                    }
                }
            }
        }
    }

    public void in_cygnusGarden() {
        initNPC(MapleLifeFactory.getNPC(2143004));
        int v0 = target.askMenu("#r#e要不要進入Signus的庭院？ #b\r\n#L0#為了打敗Signus（普通）而移動。 #l", ScriptMessageFlag.Self);
        if (v0 == 0) { //TODO 시그너스 정원의 열쇠 체크
            getPlayer().dropMessage(5, "前往Signus的庭院。");
            registerTransferField(271040000);
        }
    }

    public void cygnus_Summon_Easy() {
        //8850011 - 노말시그 (8850012) 소환용 더미
        //8850111 - 이지시그 (8850112) 소환용 더미
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (eim.getProperty("summonMOB") == null) {
                eim.setProperty("summonMOB", "1");
                Field field = getPlayer().getMap();
                if (DBConfig.isGanglim || (!DBConfig.isGanglim && !getPlayer().isMultiMode())) {
                	field.spawnMonster(MapleLifeFactory.getMonster(8850112), new Point(-160, -65), 1);
                }
                else {
                	final MapleMonster cygnus = MapleLifeFactory.getMonster(8850112);
                	cygnus.setPosition(new Point(-160, -65));
        			final long hp = cygnus.getMobMaxHp();
                    long fixedhp = hp * 3L;
                    if (fixedhp < 0) {
                    	fixedhp = Long.MAX_VALUE;
                    }
                    cygnus.setHp(fixedhp);
                    cygnus.setMaxHp(fixedhp);
                	field.spawnMonster(cygnus, new Point(-160, -65), 1);
                }
                eim.getMapInstance(getPlayer().getMapId()).startMapEffect("好久沒見到來這裡的人了。 但也沒有人平安回去。", 5120043);
            }
        }
    }

    public void cygnus_Summon() {
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (eim.getProperty("summonMOB") == null) {
                eim.setProperty("summonMOB", "1");
                Field field = getPlayer().getMap();
                if (DBConfig.isGanglim || (!DBConfig.isGanglim && !getPlayer().isMultiMode())) {
                	field.spawnMonster(MapleLifeFactory.getMonster(8850012), new Point(-160, -65), 1);
                }
                else {
                	final MapleMonster cygnus = MapleLifeFactory.getMonster(8850012);
                	cygnus.setPosition(new Point(-160, -65));
        			final long hp = cygnus.getMobMaxHp();
        			long fixedhp = hp * 3L;
                	if (fixedhp < 0) {
                		fixedhp = Long.MAX_VALUE;
                	}
                	cygnus.setHp(fixedhp);
                	cygnus.setMaxHp(fixedhp);
                    field.spawnMonster(cygnus, new Point(-160, -65), 1);
                }
                eim.getMapInstance(getPlayer().getMapId()).startMapEffect("好久沒見到來這裡的人了。 但也沒有人平安回去。", 5120043);
            }
        }
    }

    public void knights_Summon() {
        for (int i = 8610023; i <= 8610027; ++i) {
        	if (DBConfig.isGanglim || (!DBConfig.isGanglim && !getPlayer().isMultiMode())) {
        		MapleMonster mob = MapleLifeFactory.getMonster(i);
                getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(-551, 113));

                mob = MapleLifeFactory.getMonster(i);
                getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(-551, 113));
        	}
        	else {
        		MapleMonster mob = MapleLifeFactory.getMonster(i);
        		final long hp = mob.getMobMaxHp();
                long fixedhp = hp * 3L;
                if (fixedhp < 0) {
                	fixedhp = Long.MAX_VALUE;
                }
            	mob.setHp(fixedhp);
            	mob.setMaxHp(fixedhp);
                getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(-551, 113));

                mob = MapleLifeFactory.getMonster(i);
            	mob.setHp(fixedhp);
            	mob.setMaxHp(fixedhp);
                getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(-551, 113));
        	}
        }
    }

    public void knights_Summon_Easy() {
        for (int i = 8610028; i <= 8610032; ++i) {
        	if (DBConfig.isGanglim || (DBConfig.isGanglim && !getPlayer().isMultiMode())) {
        		MapleMonster mob = MapleLifeFactory.getMonster(i);
            	getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(-551, 113));

            	mob = MapleLifeFactory.getMonster(i);
            	getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(-551, 113));
        	}
        	else {
        		MapleMonster mob = MapleLifeFactory.getMonster(i);
        		final long hp = mob.getMobMaxHp();
        		long fixedhp = hp * 3L;
                if (fixedhp < 0) {
                	fixedhp = Long.MAX_VALUE;
                }
            	mob.setHp(fixedhp);
            	mob.setMaxHp(fixedhp);
                getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(-551, 113));

                mob = MapleLifeFactory.getMonster(i);
                mob.setHp(fixedhp);
            	mob.setMaxHp(fixedhp);
                getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(-551, 113));
        	}
        }
    }

    public void back_cygnus_Easy() {
        //이벤트 인스턴스 확인해서 상황에 맞게끔 맵이동시킬 것
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (getPlayer().getMap().getAllMonster().size() == 0) {
                registerTransferField(Integer.parseInt(eim.getProperty("map")));
            } else {
                getPlayer().dropMessage(5, "在返回西格納斯的庭院之前，必須先消滅所有騎士團。");
            }
        }
    }

    public void back_cygnus() {
        //이벤트 인스턴스 확인해서 상황에 맞게끔 맵이동시킬 것
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (getPlayer().getMap().getAllMonster().size() == 0) {
                registerTransferField(Integer.parseInt(eim.getProperty("map")));
            } else {
                getPlayer().dropMessage(5, "在返回西格納斯的庭院之前，必須先消滅所有騎士團。");
            }
        }
    }

    public void out_cygnusBackGarden() {
        //이벤트 인스턴스 확인해서 상황에 맞게끔 맵이동시킬 것
        registerTransferField(100000000);
    }

    public void out_cygnusBackGardenEasy() {
        //이벤트 인스턴스 확인해서 상황에 맞게끔 맵이동시킬 것(이지모드)
        registerTransferField(100000000);
    }
}
