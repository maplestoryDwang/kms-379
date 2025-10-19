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
        String v = "#e<時間的裂痕>#n\r\n過去和未來，以及其間的某個地方。 …要去的地方是哪裡？ \r\n#b#L0#過去的轉發#l\r\n#L1#維度的間隙#l";
        int v0 = self.askMenu(v, ScriptMessageFlag.NpcReplacedByNpc);
        if (v0 == 0) {
            self.say("目前正在準備中。");
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
            if (self.askYesNo("戰鬥結束後，是否退出存檔祭壇？") == 1) {
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
                self.say("1人以上組隊才能入場。");
            } else {
                if (target.getParty().getLeader().getId() != target.getId() && DBConfig.isGanglim) {
                    self.say("請通過派對現場進行。");
                } else {
                    int v0 = self.askMenu("#e<老闆：存檔>#n\r\n偉大的勇士。 準備好對抗黑魔法師的邪惡軍團長了嗎？ \r\n#b\r\n#L0#<老闆：歸檔>申請入場。 #l");
                    if (v0 == 0) {
                    	String menu = "";
                    	if (DBConfig.isGanglim) {
                    		menu = "#e<老闆：存檔>#n\r\n請選擇您想要的模式。 \r\n\r\n#L0#异地模式（等級140以上）#l\r\n#L1#正常模式（等級140以上）#l";
                    	}
                    	else {
                    		boolean single = getPlayer().getPartyMemberSize() == 1;
                    		int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "Arkarium" + (single ? "Single" : "Multi"));
                    		menu = "#e<老闆：存檔>#n\r\n請選擇您想要的模式。 \r\n\r\n" 
                    				+ "#L0#异地模式" + (single ? "（單曲）" : "（多個）") + "（等級140以上）#l\r\n"
                    				+ "#L1#正常模式" + (single ? "（單曲）" : "（多個）") + "（等級140以上）#l\r\n";
                    				if (((single ? 2 : 1) - reset) >= 0) { 
                    					menu += "#L2#入場次數新增" + (single ? "（單曲）" : "（多個）") + ((single ? 2 : 1) - reset)+ "可回收）#l";
                    				}
                    	}
                    	int v1 = self.askMenu(menu);
                        if (target.getParty().isPartySameMap()) {
                        	if (v1 == 2 && !DBConfig.isGanglim) {
                        		boolean single = getPlayer().getPartyMemberSize() == 1;
                        		int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "Arkarium" + (single ? "Single" : "Multi"));
                        		if (getPlayer().getTogetherPoint() < 150) {
                        			self.sayOk("合作積分不足。 擁有積分：" + getPlayer().getTogetherPoint());
                        			return;
                        		}
                        		if ((single ? 1 : 0) < reset) {
                        			self.sayOk("今天不能再新增入場次數了。");
                        			return;
                        		}
                        		getPlayer().gainTogetherPoint(-150);
                        		getPlayer().updateOneInfo(QuestExConstants.DailyQuestResetCount.getQuestID(), "Arkarium" + (single ? "Single" : "Multi"), String.valueOf(reset + 1));
                        		self.sayOk("可進入次數新增。");
                        		return;
                        	}
                        	if (!DBConfig.isGanglim) {
                        		if (target.getParty().getLeader().getId() != target.getId()) {
                        			self.say("請通過派對現場進行。");
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
                                            v2 = self.askMenu("#e<勞恩斯武器>#n\r\n劍可以執行解除魔法師力量的#b勞恩斯武器#k秘密的任務。 怎麼辦？ \r\n\r\n#e#r<任務執行條件>#n#k\r\n#b-獨自擊破\r\n-最終傷害减少70%\r\n-只適用裝備的純能力值\r\n#k#L0#執行任務。 #l\r\n#L1#不執行任務。 #l", ScriptMessageFlag.Self);
                                        } else {
                                            v2 = self.askMenu("#e<勞恩斯武器>#n\r\n劍可以執行解除魔法師力量的#b勞恩斯武器#k秘密的任務。 怎麼辦？ \r\n\r\n#e#r<任務執行條件>#n#k\r\n#b-獨自擊破\r\n-只裝備封印的勞恩斯武器和輔助武器\r\n-最終傷害减少70%\r\n-只適用裝備的純能力值\r\n#k#L0#執行任務。 #l\r\n#L1#不執行任務。 #l", ScriptMessageFlag.Self);
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
                                if (canEnter) {
                                    if (DBConfig.isGanglim) {
                                        Party party = getPlayer().getParty();
                                        for (PartyMemberEntry mpc : party.getPartyMemberList()) {
                                            MapleCharacter p = getPlayer().getMap().getCharacterById(mpc.getId());
                                            int key = 1234569 + v1;
                                            if (p != null) {
                                                int count = p.getOneInfoQuestInteger(key, "akairum_clear");
                                                if (count >= (1 + p.getBossTier())) {
                                                    self.say("隊員中#b#e" + p.getName() + "#n#k今天不能再挑戰了。");
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
                                    self.sayOk("當前所有地圖已滿，無法使用。 請使用其他頻道。");
                                }
                            } else {
                                self.say("隊員中#b#e" + overLap + "今天入場了。 那麼今天就不能再進去了。");
                            }
                        } else {
                            self.say(target.getParty().getPartyMemberList().size() + "名稱必須在同一映射中。");
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

    public void Akayrum_accept() {
    	if (!DBConfig.isGanglim) {
    		portalNPC1();
    		return;
    	}
        EventManager em = getEventManager("Arkarium");
        List<Integer> arkMap = new ArrayList(Arrays.asList(272020200, 272020201, 272020202, 272020203, 272020204, 272020205, 272020206, 272020207, 272020208, 272020209, 272020210, 272020211, 272020212, 272020213, 272020214, 272020215, 272020216, 272020217, 272020218, 272020219));
        if (arkMap.contains(target.getMapId())) { //전투맵인상태
            if (self.askYesNo("戰鬥結束後，是否退出存檔祭壇？") == 1) {
                registerTransferField(272020110);
            }
        } else {
            if (target.getParty() == null) {
                self.say("1人以上組隊才能入場。");
            } else {
                if (target.getParty().getLeader().getId() != target.getId()) {
                    self.say("請通過派對現場進行。");
                } else {
                    int v0 = self.askMenu("#e<老闆：存檔>#n\r\n偉大的勇士。 準備好對抗黑魔法師的邪惡軍團長了嗎？ \r\n#b\r\n#L0#<老闆：歸檔>申請入場。 #l");
                    if (v0 == 0) {
                        int v1 = self.askMenu("#e<老闆：存檔>#n\r\n請選擇您想要的模式。 \r\n\r\n#L0#异地模式（等級140以上）#l\r\n#L1#正常模式（等級140以上）#l");
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
                                    self.sayOk("當前所有地圖已滿，無法使用。 請使用其他頻道。");
                                }
                            } else {
                                self.say("隊員中#b#e" + overLap + "今天入場了。 那麼今天就不能再進去了。");
                            }
                        } else {
                            self.say(target.getParty().getPartyMemberList().size() + "名稱必須在同一映射中。");
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
                field.startMapEffect("無法區分勇氣和蠻勇的人們。 如果不捨不得性命的話，就來找我吧。 呼呼。", 5120056);
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
                field.startMapEffect("無法區分勇氣和蠻勇的人們。 如果不捨不得性命的話，就來找我吧。 呼呼。", 5120056);
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
                if (self.askAccept("把我的老計畫化為泡影的傢伙們這樣主動來找我，真是太高興了。 \r\n\r\n#r作為代價，我會給你帶來世界上最痛苦的死亡。 #k") == 1) {
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
                if (self.askAccept("把我的老計畫化為泡影的傢伙們這樣主動來找我，真是太高興了。 \r\n\r\n#r作為代價，我會給你帶來世界上最痛苦的死亡。 #k") == 1) {
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
        self.say("終於打敗了邪惡軍團團長#p2144010#。");
        self.say("終於從被關了很久的封印中走出來了。 謝謝#h0#。");
    }

    // 노멀
    public void inAkayrumPrison() {
        for (MapleMonster mob : getPlayer().getMap().getAllMonstersThreadsafe()) {
            getPlayer().getMap().removeMonster(mob, 1);
        }
        getPlayer().getMap().startMapEffect("面對自己內心醜陋的樣子感覺如何？", 5120057, false, 5);

        MapleMonster mob = MapleLifeFactory.getMonster(8860003);
        getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(88, 95));
    }

    // 이지
    public void inAkayrumPrison2() {
        for (MapleMonster mob : getPlayer().getMap().getAllMonstersThreadsafe()) {
            getPlayer().getMap().removeMonster(mob, 1);
        }
        getPlayer().getMap().startMapEffect("面對自己內心醜陋的樣子感覺如何？", 5120057, false, 5);

        MapleMonster mob = MapleLifeFactory.getMonster(8860003);
        getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(88, 95));
    }

    public void outAkayrumPrison() {
        if (getPlayer().getMap().getMobsSize() == 0) {
            getPlayer().setRegisterTransferField(272020200);
            getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
        } else {
            getPlayer().dropMessage(5, "首先要消除自己扭曲的分身，才能從邪惡的內心中逃脫出來。");
        }
    }

    public void outAkayrumP2() {
        if (getPlayer().getMap().getMobsSize() == 0) {
            getPlayer().setRegisterTransferField(272020210);
            getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis());
        } else {
            getPlayer().dropMessage(5, "首先要消除自己扭曲的分身，才能從邪惡的內心中逃脫出來。");
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
            getPlayer().getMap().startMapEffect("竟敢把我推到這裡。 …現在我會好好對付你的。", 5120057, false, 5);
        }
    }
}
