package script.Boss;

import constants.QuestExConstants;
import database.DBConfig;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.fields.Field;
import objects.fields.gameobject.lifes.ChangeableStats;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.users.MapleCharacter;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.newscripting.ScriptEngineNPC;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PinkBeen extends ScriptEngineNPC {

    public void PinkBeen_accept() {
        EventManager em = getEventManager("PinkBeen");
        int[] normalMaps = new int[]{270050100};
        int[] chaosMaps = new int[]{270051100};
        if (em == null) {
            self.say("현재 핑크빈 보스 레이드를 이용하실 수 없습니다.");
        } else {
            String v = "#e<보스:핑크빈>#n\r\n침입자는 여신의 제단으로 향한 듯 합니다. 그를 어서 저지하지 못하면 무서운 일이 일어날 겁니다.\r\n#b\r\n#L0# <보스:핑크빈> 입장을 신청한다.#l";
            int v0 = self.askMenu(v);
            if (v0 == 0) {
                if (target.getParty() == null) {
                    self.say("1인 이상의 파티에 속해야만 입장할 수 있습니다.");
                } else if (DBConfig.isGanglim && getPlayer().getParty().getLeader().getId() != getPlayer().getId()) {
                    self.say("파티장을 통해 진행해 주십시오.");
                } else {
                    if (target.getParty().isPartySameMap()) {
                    	String v2 = "";
                    	if (DBConfig.isGanglim) {
                    		v2 = "#e<보스:핑크빈>#n\r\n원하시는 모드를 선택해주세요.\r\n\r\n#L0# 노멀 모드 ( 레벨 160 이상 )#l\r\n";
                    		//v2 += "#L1# 카오스 모드 ( 레벨 170 이상 )#l\r\n#L2# 카오스 연습 모드 ( 레벨 170 이상 )#l";
                    	}
                    	else {
                    		boolean single = getPlayer().getPartyMemberSize() == 1;
                    		v2 = "#e<보스:핑크빈>#n\r\n원하시는 모드를 선택해주세요.\r\n\r\n"
                    				+ "#L0# 노멀 모드 " + (single ? "(싱글)" : "(멀티)") + " ( 레벨 160 이상 )#l\r\n"
                    				+ "#L1# 카오스 모드 " + (single ? "(싱글)" : "(멀티)") + " ( 레벨 170 이상 )#l\r\n"
                    				+ "#L2# 카오스 연습 모드 " + (single ? "(싱글)" : "(멀티)") + "( 레벨 170 이상 )#l\r\n";
                    		int nreset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalPinkBeen" + (single ? "Single" : "Multi"));
                    		v2 += "#L3# 노멀 모드 " + (single ? "(싱글)" : "(멀티)") + " 입장횟수 증가 (" + ((single ? 2 : 1) - nreset) + "회 가능)\r\n";
                    		int creset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "ChaosPinkBeen" + (single ? "Single" : "Multi"));
                    		v2 += "#L4# 카오스 모드 " + (single ? "(싱글)" : "(멀티)") + " 입장횟수 증가 (" + (1 - creset) + "회 가능)";
                    	}
                        boolean canEnter = false;
                        int questID = QuestExConstants.PinkBeen.getQuestID();
                        int selection = self.askMenu(v2);
                        if (selection == 1) { //노말모드
                            questID = QuestExConstants.ChaosPinkBeen.getQuestID();
                        }
                        String overLap = null;
                        if (!DBConfig.isGanglim) {
                        	if (selection == 3) {
                        		boolean single = getPlayer().getPartyMemberSize() == 1;
                        		int nreset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalPinkBeen" + (single ? "Single" : "Multi"));
                        		if (getPlayer().getTogetherPoint() < 150) {
                        			self.sayOk("협동 포인트가 부족합니다. 보유 포인트 : " + getPlayer().getTogetherPoint());
                        			return;
                        		}
                        		if (nreset > (single ? 1 : 0)) {
                        			self.sayOk("금일 추가입장 증가 가능 횟수를 모두 사용하였습니다.");
                        			return;
                        		}
                        		getPlayer().gainTogetherPoint(-150);
                        		getPlayer().updateOneInfo(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalPinkBeen" + (single ? "Single" : "Multi"), String.valueOf(nreset + 1));
                        		self.sayOk("입장 횟수가 증가되었습니다.");
                        		return;
                        	}
                        	if (selection == 4) {
                        		boolean single = getPlayer().getPartyMemberSize() == 1;
                        		int creset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "ChaosPinkBeen" + (single ? "Single" : "Multi"));
                        		if (getPlayer().getTogetherPoint() < 150) {
                        			self.sayOk("협동 포인트가 부족합니다. 보유 포인트 : " + getPlayer().getTogetherPoint());
                        			return;
                        		}
                        		if (creset > 0) {
                        			self.sayOk("이번주 추가입장 증가 가능 횟수를 모두 사용하였습니다.");
                        			return;
                        		}
                        		getPlayer().gainTogetherPoint(-150);
                        		getPlayer().updateOneInfo(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "ChaosPinkBeen" + (single ? "Single" : "Multi"), String.valueOf(creset + 1));
                        		self.sayOk("입장 횟수가 증가되었습니다.");
                        		return;
                        	}
                        	
                        	if (selection == 0 || selection == 1 || selection == 2) { //진 파티원 입장시도 체크
                        		if (target.getParty().getLeader().getId() != getPlayer().getId()) {
                        			self.say("파티장을 통해 진행해 주십시오.");
                        			return;
                        		}
                        	}
                            overLap = checkEventNumber(getPlayer(), questID);
                        }
                        if (selection == 0 || selection == 1) {
                            if (overLap == null) {
                                if (selection == 0) { //노말모드
                                    if (em.getProperty("status0").equals("0")) {
                                        canEnter = true;
                                    }
                                } else if (selection == 1) { //카오스모드
                                    if (em.getProperty("Cstatus0").equals("0")) {
                                        canEnter = true;
                                    }
                                }
                                if (canEnter) {
                                    if (DBConfig.isGanglim) {
                                        Party party = getPlayer().getParty();
                                        for (PartyMemberEntry mpc : party.getPartyMemberList()) {
                                            MapleCharacter p = getPlayer().getMap().getCharacterById(mpc.getId());
                                            int key = 1234569;
                                            if (p != null) {
                                                int count = p.getOneInfoQuestInteger(key, "pinkbean_clear");
                                                if (count >= (1 + p.getBossTier())) {
                                                    self.say("파티원 중 #b#e" + p.getName() + "#n#k가 오늘 더 이상 도전할 수 없습니다.");
                                                    return;
                                                }
                                                p.updateOneInfo(key, "pinkbean_clear", String.valueOf(count + 1));
                                            }
                                        }
                                    }
                                    if (selection == 0)
                                        em.setProperty("status0", "1");
                                    else if (selection == 1)
                                        em.setProperty("Cstatus0", "1");
                                    EventInstanceManager eim = em.readyInstance();
                                    int map = selection == 0 ? normalMaps[0] : chaosMaps[0];
                                    eim.setProperty("map", map);
                                    eim.setProperty("mode", selection == 0 ? "normal" : "chaos");
                                    eim.getMapInstance(map).setLastRespawnTime(Long.MAX_VALUE); //리스폰방지
                                    eim.getMapInstance(map).resetFully(false);
                                    if (selection == 0) {
                                    	if (DBConfig.isGanglim) {
                                    		updateEventNumber(getPlayer(), questID);
                                    	}
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
                                    eim.registerParty(target.getParty(), getPlayer().getMap());
                                } else {
                                    self.say("현재 모든맵이 가득차 이용하실 수 없습니다. 다른 채널을 이용해주세요.");
                                }
                            } else {
                                self.say("파티원 중#b#e" + overLap + "가#n#k 오늘 입장했군요 오늘은 더 이상 들어가실 수 없습니다.");
                            }
                        } else if (selection == 2) {
                            self.say("핑크빈 레이드 연습모드는 준비중입니다!");
                        }
                    } else {
                        self.say(target.getParty().getPartyMemberList().size() + "명 모두 같은맵에 모여 주세요.");
                    }
                }
            }
        }
    }

    public void PinkBeen_before() {
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (eim.getProperty("summonNPC") == null) {
                eim.setProperty("summonNPC", "1");
                getPlayer().getMap().spawnMonster(MapleLifeFactory.getMonster(8820023), new Point(5, -42), -1);
                getPlayer().getMap().spawnMonster(MapleLifeFactory.getMonster(8820022), new Point(5, -42), -1);
                getPlayer().getMap().spawnMonster(MapleLifeFactory.getMonster(8820021), new Point(5, -42), -1);
                getPlayer().getMap().spawnMonster(MapleLifeFactory.getMonster(8820020), new Point(5, -42), -1);
                getPlayer().getMap().spawnMonster(MapleLifeFactory.getMonster(8820019), new Point(5, -42), -1);
                getPlayer().getMap().spawnNpc(2141000, new Point(-171, -48));
            }
        }
    }

    public void PinkBeen_Summon() {
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (target.getParty().getLeader().getId() == target.getId()) { //파티장만 소환가능(중복소환 방지)
                if (self.askAccept("여신의 거울만 있으면... 다시 검은 마법사를 불러낼 수 있어!...\r\n이, 이상해... 왜 검은 마법사를 불러내지 않는 거지? 이 기운은 뭐지? 검은 마법사와는 전혀 다른... 크아아악!\r\n\r\n#b(키르스턴의 어깨에 손을 댄다.)#k") == 1) {
                    if (eim.getProperty("summonMOB") == null) {
                        eim.setProperty("summonMOB", "1");
                        Field field = getPlayer().getMap();
                        List<Integer> spongeMob = null;
                        int spongeMobId = 8820014;
                        
                        if (eim.getProperty("mode").equals("normal")) {
                        	if (DBConfig.isGanglim || (!DBConfig.isGanglim && getPlayer().getPartyMemberSize() == 1)) {
                        		field.spawnMonster(MapleLifeFactory.getMonster(8820008), new Point(5, -42), 1);
                                field.spawnMonster(MapleLifeFactory.getMonster(8820002), new Point(5, -42), -2);
                                field.spawnMonster(MapleLifeFactory.getMonster(8820003), new Point(5, -42), -2);
                                field.spawnMonster(MapleLifeFactory.getMonster(8820004), new Point(5, -42), -2);
                                field.spawnMonster(MapleLifeFactory.getMonster(8820005), new Point(5, -42), -2);
                                field.spawnMonster(MapleLifeFactory.getMonster(8820006), new Point(5, -42), -2);
                                field.spawnMonster(MapleLifeFactory.getMonster(8820014), new Point(5, -42), -2); //핑크빈 소환용
                        	}
                        	else {
                        		int[] pinkbeanparts = {8820008, 8820002, 8820003, 8820004, 8820005, 8820006, 8820014};
                        		for (int part : pinkbeanparts) {
                        			final MapleMonster pinkbeanpart = MapleLifeFactory.getMonster(part);
                        			pinkbeanpart.setPosition(new Point(5, -42));
                                    final long orghp = pinkbeanpart.getMobMaxHp();
                                    long fixedhp = orghp * 3L;
                                    if (fixedhp < 0) {
                                    	fixedhp = Long.MAX_VALUE;
                                    }
                                    pinkbeanpart.setHp(fixedhp);
                                    pinkbeanpart.setMaxHp(fixedhp);
                                    if (part == 8820008) {
                                    	field.spawnMonster(pinkbeanpart, 1);
                                    }
                                    else {
                                    	field.spawnMonster(pinkbeanpart, -2);
                                    }
                        		}
                        	}
                        	spongeMob = new ArrayList<>(Arrays.asList(8820002, 8820003, 8820004, 8820005, 8820006));
                        } else {
                        	if (DBConfig.isGanglim || (!DBConfig.isGanglim && getPlayer().getPartyMemberSize() == 1)) {
                        		field.spawnMonster(MapleLifeFactory.getMonster(8820108), new Point(5, -42), 1);
                            	field.spawnMonster(MapleLifeFactory.getMonster(8820102), new Point(5, -42), -2);
                            	field.spawnMonster(MapleLifeFactory.getMonster(8820103), new Point(5, -42), -2);
                            	field.spawnMonster(MapleLifeFactory.getMonster(8820104), new Point(5, -42), -2);
                            	field.spawnMonster(MapleLifeFactory.getMonster(8820105), new Point(5, -42), -2);
                            	field.spawnMonster(MapleLifeFactory.getMonster(8820106), new Point(5, -42), -2);
                            	field.spawnMonster(MapleLifeFactory.getMonster(8820114), new Point(5, -42), -2); //핑크빈 소환용
                        	}   
                        	else {
                        		int[] pinkbeanparts = {8820108, 8820102, 8820103, 8820104, 8820105, 8820106, 8820114};
                        		for (int part : pinkbeanparts) {
                        			final MapleMonster pinkbeanpart = MapleLifeFactory.getMonster(part);
                        			pinkbeanpart.setPosition(new Point(5, -42));
                                    final long orghp = pinkbeanpart.getMobMaxHp();
                                    long fixedhp = orghp * 3L;
                                    if (fixedhp < 0) {
                                    	fixedhp = Long.MAX_VALUE;
                                    }
                                    pinkbeanpart.setHp(fixedhp);
                                    pinkbeanpart.setMaxHp(fixedhp);
                                    if (part == 8820108) {
                                    	field.spawnMonster(pinkbeanpart, 1);
                                    }
                                    else {
                                    	field.spawnMonster(pinkbeanpart, -2);
                                    }
                        		}
                        	}
                        	spongeMob = new ArrayList<>(Arrays.asList(8820102, 8820103, 8820104, 8820105, 8820106));
                            spongeMobId = 8820114;
                        }
                        for (MapleMonster mob : field.getAllMonstersThreadsafe()) {
                            if (spongeMob.contains(mob.getId())) {
                                mob.setSponge(getPlayer().getMap().getMonsterById(spongeMobId));
                            }
                        }

                        field.killMonster(field.getMonsterById(8820023)); //죽은 석상들
                        field.killMonster(field.getMonsterById(8820022));
                        field.killMonster(field.getMonsterById(8820021));
                        field.killMonster(field.getMonsterById(8820020));
                        field.killMonster(field.getMonsterById(8820019));

                        field.removeNpc(2141000);
                    }
                }
            }
        }
    }

    public void PinkBeen_Out() {
        if (self.askYesNo("전투를 중지하고 나가시겠습니까?\r\n#r#e※주의 : 퇴장하게 되면 오늘은 더 이상 핑크빈에 도전할 수 없습니다.#n#k") == 1) {
            List<Integer> normalMap = new ArrayList(Arrays.asList(270050100, 270050101, 270050102, 270050103, 270050104, 270050105, 270050106, 270050107, 270050108, 270050109));
            //270050300(노말 핑크빈맵에서 퇴장했을경우)
            //270051300(카오스 핑크빈맵에서 퇴장했을경우)
            getPlayer().setRegisterTransferFieldTime(0);
            getPlayer().setRegisterTransferField(0);
            if (normalMap.contains(target.getMapId())) { //노말맵퇴장
                registerTransferField(270050300);
            } else {
                registerTransferField(270051300);
            }
            if (getPlayer().getEventInstance() != null) {
                getPlayer().getEventInstance().unregisterPlayer(getPlayer());
                getPlayer().setEventInstance(null);
            }
        } else {
            self.say("계속해서 도전해주십시오.");
        }
    }
}
