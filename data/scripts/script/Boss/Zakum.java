package script.Boss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import constants.QuestExConstants;
import database.DBConfig;
import objects.fields.fieldset.FieldSet;
import objects.item.MapleInventoryType;
import objects.quest.MapleQuest;
import objects.quest.MapleQuestStatus;
import objects.users.MapleCharacter;
import scripting.newscripting.ScriptEngineNPC;

public class Zakum extends ScriptEngineNPC {

    public void zakum_accept() {
        FieldSet fieldSet = fieldSet("ZakumEnter");
        if (fieldSet == null) {
            self.sayOk("現在不能使用紮庫姆雷德。");
            return;
        }
        int v0 = -1;
        if (DBConfig.isGanglim) {
        	if (target.getMapId() == 211042401) {
                fieldSet = fieldSet("ChaosZakumEnter");
                v0 = self.askMenu("#e<紮庫姆：混沌模式>#n\r\n紮庫姆復活了。 如果放任不管的話，會引發火山爆發，把整個艾爾納斯山脈變成地獄。 \r\n#b#e（隊員同時移動。）#n#k\r\n#r（Kaosjakum每週可以通關一次#n，通關記錄將在#e每週四初始化#n）\r\n#b\r\n#L0#申請進入Kaosjakum。 #l");
            } else {
                v0 = self.askMenu("#e<紮庫姆：正常模式>#n\r\n紮庫姆復活了。 如果放任不管的話，會引發火山爆發，把整個艾爾納斯山脈變成地獄。 \r\n#r（在普通紮庫姆的祭壇上，兩個模式加起來每天可以進入1次#n，入場記錄將在#e每天午夜初始化#n）\r\n#b\r\n#L0#申請紮庫姆入場。 （隊員同時移動。）#l");
            }
        }
        else { //진서버 
        	if (target.getMapId() == 211042401) {
                fieldSet = fieldSet("ChaosZakumEnter");
                boolean isSingle = false;
            	if (getPlayer().getParty() == null || //파티가 없거나
    				getPlayer().getParty() != null && getPlayer().getParty().getMembers().size() == 1) { //파티원이 1명인경우
    				isSingle = true;
    			}
                String askString = "#e<紮庫姆：混沌模式>#n\r\n"
                		+ "紮庫姆復活了。 如果放任不管的話，會引發火山爆發，把整個艾爾納斯山脈變成地獄。 \r\n"
                		+ "#b#e（隊員同時移動。）#n#k\r\n"
                		+ "#r（Kaos Zakum每週可通關一次#n，通關記錄將在#e每週四初始化#n）\r\n";
                if (!isSingle) { //멀티모드
					askString += "在多模式下，BOSS的體力新增3倍，\r\n"
						+ "最終傷害降低50%，\r\n"
						+ "隊友的死亡計數將被共亯。";
				}
                askString += "#b\r\n";
                if (isSingle) {
					askString += "#L0#申請進入混沌。 （單模式）#l\r\n";
				}
				else {
					askString += "#L0#申請進入混沌。 （多模式）#l\r\n";
				}
                boolean canReset = false;
				int reset = 0;
				if (isSingle) {
					reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "ChaosZakumSingle");
					int count = getPlayer().getOneInfoQuestInteger(15166, "mobDeadSingle");
					if (count > 0) canReset = true;
				}
				else {
					reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "ChaosZakumMulti");
					int count = getPlayer().getOneInfoQuestInteger(15166, "mobDeadMulti");
					if (count > 0) canReset = true;
				}
				if (canReset) {
					askString += "#L1#重置入場次數" + (1 - reset) + "可追加進入次";
				}
                v0 = self.askMenu(askString);
            } else {
            	boolean isSingle = false;
            	if (getPlayer().getParty() == null || //파티가 없거나
    				getPlayer().getParty() != null && getPlayer().getParty().getMembers().size() == 1) { //파티원이 1명인경우
    				isSingle = true;
    			}
				String askString = "#e<紮庫姆：正常模式>#n\r\n"
						+ "紮庫姆復活了。 如果放任不管的話，會引發火山爆發，把整個艾爾納斯山脈變成地獄。 \r\n"
						+ "#r（伊茲，在普通紮庫姆的祭壇上加上兩個模式#e每天入場1次#n"
						+ "可以，入場記錄將在#e每天午夜初始化#n。 ）\r\n";
				if (!isSingle) { //멀티모드
					askString += "在多模式下，BOSS的體力新增3倍，\r\n"
						+ "最終傷害降低50%，\r\n"
						+ "隊友的死亡計數將被共亯。";
				}
				askString += "#b\r\n";
				if (isSingle) {
					askString += "#L0#申請紮庫姆入場。 （單模式）#l\r\n";
				}
				else {
					askString += "#L0#申請紮庫姆入場。 （多模式）#l\r\n";
				}
				boolean canReset = false;
				int reset = 0;
				if (isSingle) {
					reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalZakumSingle");
					int count = getPlayer().getOneInfoQuestInteger(7003, "Single");
					if (count > 0) canReset = true;
				}
				else {
					reset = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalZakumMulti");
					int count = getPlayer().getOneInfoQuestInteger(7003, "Multi");
					if (count > 1) canReset = true;
				}
					
				if (canReset) {
					if (isSingle) {
						askString += "#L1#重置入場次數" + (2 - reset) + "可追加進入次";
					}
					else {
						askString += "#L1#重置入場次數" + (1 - reset) + "可追加進入次";
					}
				}
                v0 = self.askMenu(askString);
            }
        }

        if (v0 == 0) { //입장시도
            if (getPlayer().getItemQuantity(4001017, false) < 1) {
                if (getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 1) {
                    self.say("其他庫存空間好像不足？ 請確保足够的其他庫存空間。");
                    return;
                }
                target.exchange(4001017, 1);
                self.say("看起來沒有火的眼睛。 為了見紮庫姆，非常需要。 我會把我有的東西給你，希望你一定要把紮庫姆處理掉。");
            }
            int enter = fieldSet.enter(target.getId(), 0);
            if (enter == -1) self.say("因未知原因無法入場。 請稍後再試。");
            else if (enter == 1) self.say("只有結成派對才能挑戰。");
            else if (enter == 2) self.say("請通過派對現場進行。");
            else if (enter == 3) self.say( "最少" + fieldSet.minMember + "人以上的隊伍可以開始任務。");
            else if (enter == 4) self.say( "隊員的等級最低" + fieldSet.minLv + "必須大於。");
            else if (enter == 5) self.say("只有隊員都聚在一起才能開始。");
            else if (enter == 6) self.say( "已經有其他遠征隊進入裡面，正在挑戰任務完成。");
            else if (enter == 7) { //30분 대기시간이 발생한경우 (카오스 자쿰만 해당된다.
                self.say("有30分鐘內入場的隊員。 入場後30分鐘內無法再入場。");
            }
            else if (enter < -1) {
                MapleCharacter user = getClient().getChannelServer().getPlayerStorage().getCharacterById(enter * -1);
                String name = "";
                if (user != null) {
                    name = user.getName();
                }
                if (target.getMapId() == 211042401) {
                    self.sayOk("最近一周內有一名隊員已經完成了<老闆：紮庫姆>混沌模式。 <BOSS：紮庫姆>混沌模式每週只能通關一次。 \r\n#r#e<清除歷史記錄將於每週四進行批量初始化。>");
                } else {
                    self.say("隊員中#b#e" + name + "#k#n今天進入了紮庫姆的祭壇，無法進入。");
                }
            }
        }
        else if (v0 == 1) { //진 입장횟수 초기화
        	if (!DBConfig.isGanglim) {
        		int togetherPoint = getPlayer().getTogetherPoint();
        		if (togetherPoint < 150) {
        			self.sayOk("合作積分不足。 當前合作點：" + togetherPoint);
        			return;
        		}
        		if (target.getMapId() == 211042401) {
        			boolean isSingle = false;
	            	if (getPlayer().getParty() == null || //파티가 없거나
	    				getPlayer().getParty() != null && getPlayer().getParty().getMembers().size() == 1) { //파티원이 1명인경우
	    				isSingle = true;
	    			}
	            	int resetCount = 0;
	            	if (isSingle) {
	            		resetCount = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "ChaosZakumSingle");
	            		if (resetCount > 0) {
							self.sayOk("本周無法再初始化。");
							return;
						}
	            		getPlayer().updateOneInfo(15166, "mobDeadSingle", "0");
	            		getPlayer().updateOneInfo(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "ChaosZakumSingle", String.valueOf(resetCount + 1));
	            	}
	            	else {
	            		resetCount = getPlayer().getOneInfoQuestInteger(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "ChaosZakumMulti");
	            		if (resetCount > 0) {
							self.sayOk("本周無法再初始化。");
							return;
						}
	            		getPlayer().updateOneInfo(15166, "mobDeadMulti", "0"); 
						getPlayer().updateOneInfo(QuestExConstants.WeeklyQuestResetCount.getQuestID(), "ChaosZakumMulti", String.valueOf(resetCount + 1));
	            	}
					getPlayer().gainTogetherPoint(-150);
					self.sayOk("卡奧斯紮庫姆入場次數已初始化。");
        		}
				else {
					boolean isSingle = false;
	            	if (getPlayer().getParty() == null || //파티가 없거나
	    				getPlayer().getParty() != null && getPlayer().getParty().getMembers().size() == 1) { //파티원이 1명인경우
	    				isSingle = true;
	    			}
	            	int resetCount = 0;
	            	if (isSingle) {
	            		resetCount = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalZakumSingle");
	            		if (resetCount > 1) {
							self.sayOk("今天不能再初始化了。");
							return;
						}
	            	}
	            	else {
	            		resetCount = getPlayer().getOneInfoQuestInteger(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalZakumMulti");
	            		if (resetCount > 0) {
							self.sayOk("今天不能再初始化了。");
							return;
						}
	            	}
	            	if (isSingle) {
	            		getPlayer().updateOneInfo(7003, "Single", "");
	            		getPlayer().updateOneInfo(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalZakumSingle", String.valueOf(resetCount + 1));
					}
					else {
						getPlayer().updateOneInfo(7003, "Multi", "1"); // 멀티는 1회추가입장으로 기본 1회로 내림
						getPlayer().updateOneInfo(QuestExConstants.DailyQuestResetCount.getQuestID(), "NormalZakumMulti", String.valueOf(resetCount + 1));
					}
	            	getPlayer().gainTogetherPoint(-150);
					self.sayOk("諾馬爾紮庫姆入場次數已初始化。");
				}
        	}
        }
    }
}
