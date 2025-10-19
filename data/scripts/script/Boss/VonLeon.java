package script.Boss;

import constants.GameConstants;
import constants.QuestExConstants;
import database.DBConfig;
import network.models.CField;
import objects.context.party.Party;
import objects.context.party.PartyMemberEntry;
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
import java.util.List;


public class VonLeon extends ScriptEngineNPC {

    public void portalNPC() {
        initNPC(MapleLifeFactory.getNPC(2161005)); //포탈스크립트 엔피시
        EventManager em = getEventManager("VonLeon");
        if (em == null) {
            self.say("現在不能使用Vanlion RAID。");
        } else {
            if (target.getMapId() == 211070000) { //입장맵(알현실 앞 복도)
                if (target.getParty() == null) {
                    self.say("1人以上組隊才能入場。");
                } else {
                    if (target.getParty().getLeader().getId() != target.getId() && DBConfig.isGanglim) {
                        self.say("請通過派對現場進行。");
                    } else {
                    	boolean single = getPlayer().getPartyMemberSize() == 1;
                        int v0 = self.askMenu("#e<老闆：班萊昂>#n\r\n偉大的勇士。 準備好對抗墮落的獅子王了嗎？ \r\n#b\r\n#L0#申請進入萬瑞恩遠征隊。 #l");
                        if (v0 == 0) {
                        	String menu = "";
                        	if (DBConfig.isGanglim) {
                        		menu = "#e<老闆：班萊昂>#n\r\n請選擇您想要的模式。 \r\n\r\n"
                            			+ "#L0#异地模式（級別125或更高）#l\r\n"
                            			+ "#L1#正常模式（等級125以上）#l\r\n"
                            			+ "#L2#硬模式（級別125或更高）#l";
                        	}
                        	else {
                        		menu = "#e<老闆：班萊昂>#n\r\n請選擇您想要的模式。 \r\n\r\n"
                            			+ "#L0#异地模式" + (single ? "（單曲）" : "（多個）") + "（125級以上）#l\r\n"
                            			+ "#L1#正常模式" + (single ? "（單曲）" : "（多個）") + "（125級以上）#l\r\n"
                            			+ "#L2#硬模式" + (single ? "（單曲）" : "（多個）") + "（125級以上）#l\r\n";
                        		int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "VonLeon" + (single ? "Single" : "Multi"));
                        		menu += "#L3#入場次數" + (single ? "（單曲）" : "（多個）") + "新增" + ((single ? 2 : 1) - reset) + "可新增次";
                        	}
                            int v1 = self.askMenu(menu);
                            if (v1 == 3 && !DBConfig.isGanglim) {
                            	if (getPlayer().getTogetherPoint() < 150) {
                            		self.sayOk("合作積分不足。 當前積分：" + getPlayer().getTogetherPoint());
                            		return;
                            	}
                            	int reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "VonLeon" + (single ? "Single" : "Multi"));
                            	if ((reset > 0 && !single) || (reset > 1 && single)) {
                            		self.sayOk("今天可以追加的次數已全部使用。");
                            		return;
                            	}
                            	getPlayer().gainTogetherPoint(-150);
                            	getPlayer().updateOneInfo(QuestExConstants.DailyQuestResetCount.getQuestID(), "VonLeon" + (single ? "Single" : "Multi"), String.valueOf(reset + 1));
                            	self.sayOk("可進入次數新增。");
                            	return;
                            }
                            if (!DBConfig.isGanglim) {
                            	if (target.getParty().getLeader().getId() != target.getId()) {
                            		self.say("請通過派對現場進行。");
                            		return;
                            	}
                            }
                            if (target.getParty().isPartySameMap()) {
                                boolean canEnter = false;
                                String overLap = null;
                                if (!DBConfig.isGanglim) {
                                    overLap = checkEventNumber(getPlayer(), QuestExConstants.VonLeon.getQuestID());
                                }
                                if (overLap == null) {
                                    if (em.getProperty("status0").equals("0")) {
                                        canEnter = true;
                                    }

                                    int v2 = -1;
                                    if (v1 == 2) {
                                        if (getPlayer().getQuestStatus(2000019) == 1) {
                                            if (GameConstants.isZero(getPlayer().getJob())) {
                                                v2 = self.askMenu("#e<勞恩斯武器>#n\r\n劍可以執行解除魔法師力量的#b勞恩斯武器#k秘密的任務。 怎麼辦？ \r\n\r\n#e#r<任務執行條件>#n#k\r\n#b-獨自擊破\r\n-最終傷害减少90%\r\n-只適用裝備的純能力值\r\n#k#L0#執行任務。 #l\r\n#L1#不執行任務。 #l", ScriptMessageFlag.Self);
                                            } else {
                                                v2 = self.askMenu("#e<勞恩斯武器>#n\r\n劍可以執行解除魔法師力量的#b勞恩斯武器#k秘密的任務。 怎麼辦？ \r\n\r\n#e#r<任務執行條件>#n#k\r\n#b-獨自擊破\r\n-只裝備封印的勞恩斯武器和輔助武器\r\n-最終傷害减少90%\r\n-只適用裝備的純能力值\r\n#k#L0#執行任務。 #l\r\n#L1#不執行任務。 #l", ScriptMessageFlag.Self);
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
                                    
									if (!canEnter) { // 입장이 불가능한 경우 맵에 유저가 없는지 체크 후 인스턴스 초기화
										if (getClient().getChannelServer().getMapFactory().getMap(211070100).getCharactersSize() == 0) {
											String rt = em.getProperty("ResetTime");
											long curTime = System.currentTimeMillis();
											long time = rt == null ? 0 : Long.parseLong(rt);
											if (time == 0) {
												em.setProperty("ResetTime", String.valueOf(curTime));
											} else if (time - curTime >= 10000) { // 10초이상 맵이 빈경우 입장가능하게 변경
												canEnter = true;
												em.setProperty("ResetTime", "0");
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
                                                    int count = p.getOneInfoQuestInteger(key, "vonleon_clear");
                                                    if (count >= (1 + p.getBossTier())) {
                                                        self.say("隊員中#b#e" + p.getName() + "#n#k今天不能再挑戰了。");
                                                        return;
                                                    }
                                                    p.updateOneInfo(key, "vonleon_clear", String.valueOf(count + 1));
                                                }
                                            }
                                        }

                                        em.setProperty("status0", "1");
                                        EventInstanceManager eim = em.readyInstance();
                                        eim.setProperty("map", 211070100);
                                        if (v1 == 0) {
                                            eim.setProperty("mode", "easy");
                                        } else if (v1 == 1) {
                                            eim.setProperty("mode", "normal");
                                        } else if (v1 == 2) {
                                            eim.setProperty("mode", "hard");
                                        }
                                        getClient().getChannelServer().getMapFactory().getMap(211070100).resetFully(false);
                                        if (v2 == 0) {
                                            getPlayer().applyBMCurse1(1);
                                        }
                                        if (!DBConfig.isGanglim && !single) {
                                        	for (MapleCharacter partyMember : getPlayer().getPartyMembers()) {
                                        		if (partyMember.getMapId() == getPlayer().getMapId()) {
                                        			partyMember.setMultiMode(true);
                                        			partyMember.applyBMCurseJinMulti();
                                        		}
                                        	}
                                        }
                                        updateEventNumber(getPlayer(), QuestExConstants.VonLeon.getQuestID());
                                        eim.registerParty(target.getParty(), getPlayer().getMap());
                                    } else {
                                        self.sayOk("當前所有地圖已滿，無法使用。 請使用其他頻道。");
                                    }
                                } else {
                                    self.say("隊員中#b#e" + overLap + "今天已進入#n#k，無法再挑戰。");
                                }
                            } else {
                                self.say(target.getParty().getPartyMemberList().size() + "名稱必須在同一映射中。");
                            }
                        }
                    }
                }
            } else {
                if (self.askYesNo("挑戰結束後退出蛋室嗎？") == 1) {
                    //수락시 퇴장 (네 번째 탑루로 가짐)
                    getPlayer().setRegisterTransferFieldTime(0);
                    getPlayer().setRegisterTransferField(0);
                    registerTransferField(211060801);

                    if (getPlayer().getEventInstance() != null) {
                        getPlayer().getEventInstance().unregisterPlayer(getPlayer());
                        getPlayer().setEventInstance(null);
                    }
                }
                //거절시 아무것도없다
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

    public void VanLeon_Before() {
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (eim.getProperty("summonNPC") == null) {
                eim.setProperty("summonNPC", "1");
                Field field = getPlayer().getMap();
                field.spawnNpc(2161000, new Point(-6, -188));
                field.broadcastMessage(CField.NPCPacket.npcSpecialAction(field.getNPCById(2161000).getObjectId(), "summon", 0, 0));
            }
        }
    }

    public void VanLeon_Summon() {
        /*
8840013 - 이지소환
8840010 - 노말소환
8840018 - 하드소환
         */
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (target.getParty().getLeader().getId() == target.getId()) { //파티장만 소환가능(중복소환 방지)
                if (self.askAccept("是來打敗我的勇士嗎。 ……難道是對抗黑魔法師的人嗎？ ……不管是哪一方都沒關係吧。 如果彼此的目的明確的話，就不用多說了。 .\r\n來吧。 愚蠢的傢伙們。 ..#k", ScriptMessageFlag.NoEsc) == 1) {
                    //수락하면 반레온이 소환된다
                    if (eim.getProperty("summonMOB") == null) {
                        eim.setProperty("summonMOB", "1");
                        Field field = getPlayer().getMap();
                        field.removeNpc(2161000);
                        if (eim.getProperty("mode").equals("hard")) {
                        	if (DBConfig.isGanglim) {
                        		field.spawnMonster(MapleLifeFactory.getMonster(8840018), new Point(-6, -188), 32);
                        	}
                        	else {
                        		if (!getPlayer().isMultiMode()) { //싱글모드
                        			field.spawnMonster(MapleLifeFactory.getMonster(8840018), new Point(-6, -188), 32);
                        			if (getPlayer().getQuestStatus(2000019) == 1) {
                        				getPlayer().applyBMCurse1(2);
                        			}
                        		}
                        		else {
                        			final MapleMonster vonleon = MapleLifeFactory.getMonster(8840018);
                        			vonleon.setPosition(new Point(-6, -188));
                        			
                        			final long hp = vonleon.getMobMaxHp();
                        			long fixedhp = hp * 3L;
                        			if (fixedhp < 0) {
                        				fixedhp = Long.MAX_VALUE;
                        			}
                        			vonleon.setHp(fixedhp);
                        			vonleon.setMaxHp(fixedhp);

        							field.spawnMonster(vonleon, 32);
                        		}
                        	}
                        } else if (eim.getProperty("mode").equals("normal")) {
                        	if (DBConfig.isGanglim) {
                        		field.spawnMonster(MapleLifeFactory.getMonster(8840010), new Point(-6, -188), 32);
                        	}
                        	else {
                        		if (getPlayer().getPartyMemberSize() == 1) {
                        			field.spawnMonster(MapleLifeFactory.getMonster(8840010), new Point(-6, -188), 32);
                        		}
                        		else {
                        			final MapleMonster vonleon = MapleLifeFactory.getMonster(8840010);
                        			vonleon.setPosition(new Point(-6, -188));
        							final long hp = vonleon.getMobMaxHp();
                        			long fixedhp = hp * 3L;
                        			if (fixedhp < 0) {
                        				fixedhp = Long.MAX_VALUE;
                        			}
                        			vonleon.setHp(fixedhp);
                        			vonleon.setMaxHp(fixedhp);

        							field.spawnMonster(vonleon, 32);
                        		}
                        	}
                        } else if (eim.getProperty("mode").equals("easy")) {
                        	if (DBConfig.isGanglim) {
                        		field.spawnMonster(MapleLifeFactory.getMonster(8840013), new Point(-6, -188), 32);
                        	}
                        	else {
                        		if (getPlayer().getPartyMemberSize() == 1) {
                        			field.spawnMonster(MapleLifeFactory.getMonster(8840013), new Point(-6, -188), 32);
                        		}
                        		else {
                        			final MapleMonster vonleon = MapleLifeFactory.getMonster(8840013);
                        			vonleon.setPosition(new Point(-6, -188));
                        			final long hp = vonleon.getMobMaxHp();
                        			long fixedhp = hp * 3L;
                        			if (fixedhp < 0) {
                        				fixedhp = Long.MAX_VALUE;
                        			}
                        			vonleon.setHp(fixedhp);
                        			vonleon.setMaxHp(fixedhp);

        							field.spawnMonster(vonleon, 32);
                        		}
                        	}
                        }
                    }
                }
            } else {
                self.say("是來打敗我的勇士嗎。 ……難道是對抗黑魔法師的人嗎？ ……不管是哪一方都沒關係吧。 如果彼此的目的明確的話，就不用多說了。 ..");
            }
        }
        //거절시 아무것도없다
    }

    public void outVanLeonPrison() {
        // 감옥열쇠 있어야 탈출가능
        EventInstanceManager eim = getEventInstance();
        if (eim != null) {
            if (getPlayer().haveItem(4032860, 1, false, true)) {
                registerTransferField(Integer.parseInt(eim.getProperty("map")));
                getPlayer().removeItem(4032860, -1);
            } else {
                getPlayer().dropMessage(5, "沒有鑰匙就無法逃出監獄。 請翻箱子找鑰匙。");
            }
        }
    }
}
