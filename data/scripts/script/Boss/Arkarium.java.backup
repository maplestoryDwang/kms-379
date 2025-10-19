package script.Boss;

import constants.GameConstants;
import constants.QuestExConstants;
import database.DBConfig;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
import objects.effect.child.PlayMusicDown;
import objects.fields.Field;
import objects.fields.gameobject.Reactor;
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

public class Arkarium extends ScriptEngineNPC {

    public void timeCrack() {
        initNPC(MapleLifeFactory.getNPC(2144017));
        String v = "#e<시간의 균열>#n\r\n과거와 미래, 그리고 그 사이의 어딘가... 가고자 하는 곳은 어디인가?\r\n#b#L0# 과거의 리프레#l\r\n#L1# 차원의 틈#l";
        int v0 = self.askMenu(v, ScriptMessageFlag.NpcReplacedByNpc);
        if (v0 == 0) {
            self.say("현재 준비중입니다.");
        } else if (v0 == 1) {
            registerTransferField(272020000);
        }
    }

    public void check_eNum() {
        registerTransferField(272020110);
    }

    public void portalNPC1() {
        initNPC(MapleLifeFactory.getNPC(2144017));
        EventManager em = getEventManager("Arkarium");
        List<Integer> arkMap = new ArrayList(Arrays.asList(272020200, 272020201, 272020202, 272020203, 272020204, 272020205, 272020206, 272020207, 272020208, 272020209, 272020210, 272020211, 272020212, 272020213, 272020214, 272020215, 272020216, 272020217, 272020218, 272020219));
        if (arkMap.contains(target.getMapId())) { //전투맵인상태
            if (self.askYesNo("전투을 마치고 아카이럼의 제단에서 퇴장하시겠습니까?") == 1) {
                registerTransferField(272020110);
                if (getPlayer().getEventInstance() != null) {
                    getPlayer().getEventInstance().unregisterPlayer(getPlayer());
                    getPlayer().setEventInstance(null);
                    getPlayer().setRegisterTransferFieldTime(0);
                    getPlayer().setRegisterTransferField(0);
                }
            }
        } else {
            if (target.getParty() == null) {
                self.say("1인 이상 파티를 맺어야만 입장할 수 있습니다.");
            } else {
                if (target.getParty().getLeader().getId() != target.getId() && DBConfig.isGanglim) {
                    self.say("파티장을 통해 진행해 주십시오.");
                } else {
                    int v0 = self.askMenu("#e<보스: 아카이럼>#n\r\n위대한 용사여. 검은 마법사의 사악한 군단장에게 맞설 준비를 마치셨습니까?\r\n#b\r\n#L0# <보스: 아카이럼> 입장을 신청한다.#l");
                    if (v0 == 0) {
                    	String menu = "";
                    	if (DBConfig.isGanglim) {
                    		menu = "#e<보스: 아카이럼>#n\r\n원하시는 모드를 선택해주세요.\r\n\r\n#L0# 이지 모드 ( 레벨 140 이상 )#l\r\n#L1# 노멀 모드 ( 레벨 140 이상 )#l";
                    	}
                    	else {
                    		boolean single = getPlayer().getPartyMemberSize() == 1;
                    		int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "Arkarium" + (single ? "Single" : "Multi"));
                    		menu = "#e<보스: 아카이럼>#n\r\n원하시는 모드를 선택해주세요.\r\n\r\n" 
                    				+ "#L0# 이지 모드 " + (single ? "(싱글)" : "(멀티)") + " ( 레벨 140 이상 )#l\r\n"
                    				+ "#L1# 노멀 모드 " + (single ? "(싱글)" : "(멀티)") + " ( 레벨 140 이상 )#l\r\n";
                    				if (((single ? 2 : 1) - reset) >= 0) { 
                    					menu += "#L2# 입장횟수 증가 " + (single ? "(싱글)" : "(멀티)") + ((single ? 2 : 1) - reset)+ "회 가능)#l";
                    				}
                    	}
                    	int v1 = self.askMenu(menu);
                        if (target.getParty().isPartySameMap()) {
                        	if (v1 == 2 && !DBConfig.isGanglim) {
                        		boolean single = getPlayer().getPartyMemberSize() == 1;
                        		int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "Arkarium" + (single ? "Single" : "Multi"));
                        		if (getPlayer().getTogetherPoint() < 150) {
                        			self.sayOk("협동 포인트가 부족합니다. 보유 포인트 : " + getPlayer().getTogetherPoint());
                        			return;
                        		}
                        		if ((single ? 1 : 0) < reset) {
                        			self.sayOk("오늘은 더 이상 입장횟수 증가가 불가능합니다.");
                        			return;
                        		}
                        		getPlayer().gainTogetherPoint(-150);
                        		getPlayer().updateOneInfo(QuestExConstants.DailyQuestResetCount.getQuestID(), "Arkarium" + (single ? "Single" : "Multi"), String.valueOf(reset + 1));
                        		self.sayOk("입장가능횟수가 증가되었습니다.");
                        		return;
                        	}
                        	if (!DBConfig.isGanglim) {
                        		if (target.getParty().getLeader().getId() != target.getId()) {
                        			self.say("파티장을 통해 진행해 주십시오.");
                        			return;
                        		}
                        	}
                            String overLap = null;
                            if (!DBConfig.isGanglim) {                            	
                                overLap = checkEventNumber(getPlayer(), QuestExConstants.Arkarium.getQuestID());
                            }
                            if (overLap == null) {
                                boolean canEnter = false;
                                String mode = "easy";
                                if (v1 == 0) { //이지모드
                                    if (em.getProperty("status0").equals("0")) {
                                        canEnter = true;
                                    }
                                } else if (v1 == 1) { //노말모드
                                    if (em.getProperty("Nstatus0").equals("0")) {
                                        mode = "normal";
                                        canEnter = true;
                                    }
                                }

                                int v2 = -1;
                                if (v1 == 1) {
                                    if (getPlayer().getQuestStatus(2000020) == 1) {
                                        if (GameConstants.isZero(getPlayer().getJob())) {
                                            v2 = self.askMenu("#e<제네시스 무기>#n\r\n검은 마법사의 힘이 담긴 #b제네시스 무기#k의 비밀을 풀기 위한 임무를 수행 할 수 있다. 어떻게 할까?\r\n\r\n#e#r<임무 수행 조건>#n#k\r\n#b -혼자서 격파\r\n -최종 데미지 70% 감소\r\n -착용 중인 장비의 순수 능력치만 적용\r\n#k#L0#미션을 수행한다.#l\r\n#L1#미션을 수행하지 않는다.#l", ScriptMessageFlag.Self);
                                        } else {
                                            v2 = self.askMenu("#e<제네시스 무기>#n\r\n검은 마법사의 힘이 담긴 #b제네시스 무기#k의 비밀을 풀기 위한 임무를 수행 할 수 있다. 어떻게 할까?\r\n\r\n#e#r<임무 수행 조건>#n#k\r\n#b -혼자서 격파\r\n -봉인된 제네시스 무기와 보조무기만 장착\r\n -최종 데미지 70% 감소\r\n -착용 중인 장비의 순수 능력치만 적용\r\n#k#L0#미션을 수행한다.#l\r\n#L1#미션을 수행하지 않는다.#l", ScriptMessageFlag.Self);
                                        }
                                        if (v2 == 0) {
                                            if (!checkBMQuestEquip()) {
                                                return;
                                            }
                                            if (getPlayer().getParty().getPartyMemberList().size() > 1) {
                                                self.say("해당 퀘스트는 혼자 진행해야 한다.", ScriptMessageFlag.Self);
                                                return;
                                            }
                                        }
                                    }
                                }
                                if (canEnter) {
                                    if (DBConfig.isGanglim) {
                                        Party party = getPlayer().getParty();
                                        for (PartyMemberEntry mpc : party.getPartyMemberList()) {
                                            MapleCharacter p = getPlayer().getMap().getCharacterById(mpc.getId());
                                            int key = 1234569 + v1;
                                            if (p != null) {
                                                int count = p.getOneInfoQuestInteger(key, "akairum_clear");
                                                if (count >= (1 + p.getBossTier())) {
                                                    self.say("파티원 중 #b#e" + p.getName() + "#n#k가 오늘 더 이상 도전할 수 없습니다.");
                                                    return;
                                                }
                                                p.updateOneInfo(key, "akairum_clear", String.valueOf(count + 1));
                                            }
                                        }
                                    }
                                    if (mode.equals("easy")) {
                                        em.setProperty("status0", "1");
                                    } else {
                                        em.setProperty("Nstatus0", "1");
                                    }
                                    EventInstanceManager eim = em.readyInstance();
                                    int map = 272020210;
                                    if (v1 == 1) {
                                        map = 272020200;
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
                                    eim.setProperty("map", map);
                                    eim.setProperty("mode", mode);
                                    getClient().getChannelServer().getMapFactory().getMap(map).resetFully(false);
                                    getClient().getChannelServer().getMapFactory().getMap(map + 100).resetFully(false); //사악한 내면의 공터
                                    if (v2 == 0) {
                                        getPlayer().applyBMCurse1(2);
                                    }
                                    updateEventNumber(getPlayer(), QuestExConstants.Arkarium.getQuestID());
                                    eim.registerParty(target.getParty(), getPlayer().getMap());
                                } else {
                                    self.sayOk("현재 모든맵이 가득차 이용하실 수 없습니다. 다른 채널을 이용해주세요.");
                                }
                            } else {
                                self.say("파티원 중#b#e" + overLap + "가#n#k 오늘 입장했군. 그렇다면 오늘은 더 이상 들어갈 수 없다.");
                            }
                        } else {
                            self.say(target.getParty().getPartyMemberList().size() + "명 모두 같은맵에 있어야 합니다.");
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
            String v0 = "#r무기#k와 #b보조무기#k만 착용하고 도전해야 한다.\r\n\r\n#r<착용 해제해야 하는 아이템>#k\r\n";
            for (int i = 0; i < blockedList.size(); ++i) {
                int bid = blockedList.get(i);
                v0 += "#i" + bid + "# #z" + bid + "#\r\n";
            }
            self.say(v0, ScriptMessageFlag.Self);
            return false;
        }
        return true;
    }

    public void Akayrum_accept() {
    	if (!DBConfig.isGanglim) {
    		portalNPC1();
    		return;
    	}
        EventManager em = getEventManager("Arkarium");
        List<Integer> arkMap = new ArrayList(Arrays.asList(272020200, 272020201, 272020202, 272020203, 272020204, 272020205, 272020206, 272020207, 272020208, 272020209, 272020210, 272020211, 272020212, 272020213, 272020214, 272020215, 272020216, 272020217, 272020218, 272020219));
        if (arkMap.contains(target.getMapId())) { //전투맵인상태
            if (self.askYesNo("전투을 마치고 아카이럼의 제단에서 퇴장하시겠습니까?") == 1) {
                registerTransferField(272020110);
            }
        } else {
            if (target.getParty() == null) {
                self.say("1인 이상 파티를 맺어야만 입장할 수 있습니다.");
            } else {
                if (target.getParty().getLeader().getId() != target.getId()) {
                    self.say("파티장을 통해 진행해 주십시오.");
                } else {
                    int v0 = self.askMenu("#e<보스: 아카이럼>#n\r\n위대한 용사여. 검은 마법사의 사악한 군단장에게 맞설 준비를 마치셨습니까?\r\n#b\r\n#L0# <보스: 아카이럼> 입장을 신청한다.#l");
                    if (v0 == 0) {
                        int v1 = self.askMenu("#e<보스: 아카이럼>#n\r\n원하시는 모드를 선택해주세요.\r\n\r\n#L0# 이지 모드 ( 레벨 140 이상 )#l\r\n#L1# 노멀 모드 ( 레벨 140 이상 )#l");
                        if (target.getParty().isPartySameMap()) {
                            String overLap = checkEventNumber(getPlayer(), QuestExConstants.Arkarium.getQuestID());
                            if (overLap == null) {
                                boolean canEnter = false;
                                String mode = "easy";
                                if (v1 == 0) { //이지모드
                                    if (em.getProperty("status0").equals("0")) {
                                        canEnter = true;
                                    }
                                } else if (v1 == 1) { //노말모드
                                    if (em.getProperty("Nstatus0").equals("0")) {
                                        mode = "normal";
                                        canEnter = true;
                                    }
                                }
                                if (!canEnter) { //입장이 불가능한 경우 맵에 유저가 없는지 체크 후 인스턴스 초기화
                                	int map = 272020210;
                                    if (v1 == 1) {
                                        map = 272020200;
                                    }
                                	if (getClient().getChannelServer().getMapFactory().getMap(map).getCharacters().size() == 0) {
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
                                    int map = 272020210;
                                    if (v1 == 1) {
                                        map = 272020200;
                                    }
                                    updateEventNumber(getPlayer(), QuestExConstants.Arkarium.getQuestID());
                                    eim.setProperty("map", map);
                                    eim.setProperty("mode", mode);
                                    getClient().getChannelServer().getMapFactory().getMap(map).resetFully(false);
                                    getClient().getChannelServer().getMapFactory().getMap(map + 100).resetFully(false); //사악한 내면의 공터
                                    eim.registerParty(target.getParty(), getPlayer().getMap());
                                } else {
                                    self.sayOk("현재 모든맵이 가득차 이용하실 수 없습니다. 다른 채널을 이용해주세요.");
                                }
                            } else {
                                self.say("파티원 중#b#e" + overLap + "가#n#k 오늘 입장했군. 그렇다면 오늘은 더 이상 들어갈 수 없다.");
                            }
                        } else {
                            self.say(target.getParty().getPartyMemberList().size() + "명 모두 같은맵에 있어야 합니다.");
                        }
                    }
                }
            }
        }
    }

    public void Akayrum_Before() {
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (eim.getProperty("summmonMOB") == null) {
                eim.setProperty("summmonMOB", "1");
                Field field = getPlayer().getMap();
                field.startMapEffect("용기와 만용을 구분하지 못하는 자들이여. 목숨이 아깝지 않다면 내게 덤비도록. 후후.", 5120056);
                PlayMusicDown e = new PlayMusicDown(getPlayer().getId(), 100, "Voice.img/akayrum/2");
                field.broadcastMessage(e.encodeForLocal());
                field.removeNpc(2144016); //륀느 꺼져!
                field.spawnNpc(2144010, new Point(320, -190));
            }
        }
    }

    public void Akayrum_Before2() { //이지아카이럼
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (eim.getProperty("summmonMOB") == null) {
                eim.setProperty("summmonMOB", "1");
                Field field = getPlayer().getMap();
                field.startMapEffect("용기와 만용을 구분하지 못하는 자들이여. 목숨이 아깝지 않다면 내게 덤비도록. 후후.", 5120056);
                PlayMusicDown e = new PlayMusicDown(getPlayer().getId(), 100, "Voice.img/akayrum/2");
                field.broadcastMessage(e.encodeForLocal());
                field.removeNpc(2144016); //륀느 꺼져!
                field.spawnNpc(2144021, new Point(320, -190));
            }
        }
    }

    public void Akayrum_Summon() { //아카이럼 소환술!!!!!!!!(NPC ID : 2144010)
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (target.getParty().getLeader().getId() == target.getId()) { //파티장만 소환가능(중복소환 방지)
                if (self.askAccept("내 오랜 계획을 물거품으로 만든 녀석들이 이렇게 제 발로 찾아와주니 정말 기쁘기 그지 없군.\r\n\r\n#r그 댓가로 세상에서 제일 고통스러운 죽음을 선사해주마.#k") == 1) {
                    //수락시 아카이럼 사라지고 몬스터 소환됨!
                    Field field = getPlayer().getMap();
                    field.removeNpc(2144010);
                    if (DBConfig.isGanglim) {
                    	field.spawnMonster(MapleLifeFactory.getMonster(8860010), new Point(320, -190), 32);
                    }
                    else {
                    	if (getPlayer().getPartyMemberSize() == 1) {
                    		field.spawnMonster(MapleLifeFactory.getMonster(8860010), new Point(320, -190), 32);
                    	}
                    	else {
                    		final MapleMonster arkarium = MapleLifeFactory.getMonster(8860010);
                    		arkarium.setPosition(new Point(320, -190));
                            final long orghp = arkarium.getMobMaxHp();
                            ChangeableStats cs = new ChangeableStats(arkarium.getStats());
                            cs.hp = orghp * 3L;
                            if (cs.hp < 0) {
                            	cs.hp = Long.MAX_VALUE;
                            }
                            arkarium.getStats().setHp(cs.hp);
                            arkarium.getStats().setMaxHp(cs.hp);
                            arkarium.setOverrideStats(cs);
                            field.spawnMonster(arkarium, -2);
                    	}
                    }
                }
            }
        }
    }

    public void Akayrum_Summon2() { //이지 아카이럼 소환술!!!!!!!!(NPC ID : 2144021)
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (target.getParty().getLeader().getId() == target.getId()) { //파티장만 소환가능(중복소환 방지)
                if (self.askAccept("내 오랜 계획을 물거품으로 만든 녀석들이 이렇게 제 발로 찾아와주니 정말 기쁘기 그지 없군.\r\n\r\n#r그 댓가로 세상에서 제일 고통스러운 죽음을 선사해주마.#k") == 1) {
                    //수락시 아카이럼 사라지고 몬스터 소환됨!
                    Field field = getPlayer().getMap();
                    field.removeNpc(2144021);
                    if (DBConfig.isGanglim) {
                    	field.spawnMonster(MapleLifeFactory.getMonster(8860007), new Point(320, -190), 32);
                    } else {
                    	if (getPlayer().getPartyMemberSize() == 1) {
                    		field.spawnMonster(MapleLifeFactory.getMonster(8860007), new Point(320, -190), 32);
                    	}
                    	else {
                    		final MapleMonster arkarium = MapleLifeFactory.getMonster(8860007);
                    		arkarium.setPosition(new Point(320, -190));
                            final long orghp = arkarium.getMobMaxHp();
                            ChangeableStats cs = new ChangeableStats(arkarium.getStats());
                            cs.hp = orghp * 3L;
                            if (cs.hp < 0) {
                            	cs.hp = Long.MAX_VALUE;
                            }
                            arkarium.getStats().setHp(cs.hp);
                            arkarium.getStats().setMaxHp(cs.hp);
                            arkarium.setOverrideStats(cs);
                            field.spawnMonster(arkarium, -2);
                    	}
                    }
                }
            }
        }
    }

    public void Akayrum_retry() {
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            registerTransferField(Integer.parseInt(eim.getProperty("map")));
        }
    }

    public void akayrum_saveTheGoddess() {
        getPlayer().getMap().hideNpc(2144020);
        self.say("악의 군단장 #p2144010#을 드디어 물리쳤군요.");
        self.say("오랫동안 갇혀있던 봉인에서 드디어 나오게 되었어요. 감사합니다 #h0#님.");
    }

    // 노멀
    public void inAkayrumPrison() {
        for (MapleMonster mob : getPlayer().getMap().getAllMonstersThreadsafe()) {
            getPlayer().getMap().removeMonster(mob, 1);
        }
        getPlayer().getMap().startMapEffect("자신 속의 추악한 모습을 마주한 기분이 어떠신지요?", 5120057, false, 5);

        MapleMonster mob = MapleLifeFactory.getMonster(8860003);
        getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(88, 95));
    }

    // 이지
    public void inAkayrumPrison2() {
        for (MapleMonster mob : getPlayer().getMap().getAllMonstersThreadsafe()) {
            getPlayer().getMap().removeMonster(mob, 1);
        }
        getPlayer().getMap().startMapEffect("자신 속의 추악한 모습을 마주한 기분이 어떠신지요?", 5120057, false, 5);

        MapleMonster mob = MapleLifeFactory.getMonster(8860003);
        getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(88, 95));
    }

    public void outAkayrumPrison() {
        if (getPlayer().getMap().getMobsSize() == 0) {
            getPlayer().setRegisterTransferField(272020200);
            getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
        } else {
            getPlayer().dropMessage(5, "뒤틀린 자신의 분신을 먼저 퇴치해야 사악한 내면에서 탈출할 수 있습니다.");
        }
    }

    public void outAkayrumP2() {
        if (getPlayer().getMap().getMobsSize() == 0) {
            getPlayer().setRegisterTransferField(272020210);
            getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
        } else {
            getPlayer().dropMessage(5, "뒤틀린 자신의 분신을 먼저 퇴치해야 사악한 내면에서 탈출할 수 있습니다.");
        }
    }

    // 아카이럼 모니터 브레이크 lua 스크립트
    public void Akayrum_lastHit1() {
        Reactor reactor = getPlayer().getMap().getReactorByName("marble1");
        if (reactor != null) {
            reactor.forceHitReactor((byte) 1);
        }
    }

    public void Akayrum_lastHit2() {
        Reactor reactor = getPlayer().getMap().getReactorByName("marble2");
        if (reactor != null) {
            reactor.forceHitReactor((byte) 1);
        }
    }

    public void Akayrum_lastHit3() {
        Reactor reactor = getPlayer().getMap().getReactorByName("marble3");
        if (reactor != null) {
            reactor.forceHitReactor((byte) 1);
        }
    }

    public void Akayrum_lastHit4() {
        Reactor reactor = getPlayer().getMap().getReactorByName("marble4");
        if (reactor != null) {
            reactor.forceHitReactor((byte) 1);
            getPlayer().getMap().startMapEffect("감히 나를 여기까지 밀어붙이다니...이제 제대로 상대해주지.", 5120057, false, 5);
        }
    }
}
