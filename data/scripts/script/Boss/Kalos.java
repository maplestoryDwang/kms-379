package script.Boss;

import constants.QuestExConstants;
import database.DBConfig;
import network.models.CField;
import objects.fields.child.karrotte.Field_BossKalos;
import objects.fields.fieldset.childs.*;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

public class Kalos extends ScriptEngineNPC {
	public void kalos_enterGate() {
		/*
		if (!(getPlayer().getClient().isGm() || getPlayer().isGM())) {
			target.say("現在無法入場。");
			return;
		}
		*/
		String Message = "[可以3人入場]為了與監視者卡洛斯的戰鬥移動嗎？ （可進入#r等級500以上#k）\r\n\r\n";
		Message += "#L1#<老闆：申請進入監視者卡洛斯（#b混沌#k>）。#g[" + getPlayer().getOneInfoQuestInteger(1234569, "kalos_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
		Message += "#L5#<老闆：申請進入監視者卡洛斯（#b混沌練習模式#k）>。 #l#k\r\n";
		/*  380 이후에 사용할 입장스크립트
		Message += "#L0#<老闆：申請進入監視者卡洛斯（#b吧#k）>。 #g[" + getPlayer().getOneInfoQuestInteger(1234570, "kalos_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
        Message += "#L1#<老闆：申請進入監視者卡洛斯（#b普通#k）>。 #g[" + getPlayer().getOneInfoQuestInteger(1234569, "kalos_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
        Message += "#L2#<老闆：申請進入監視者卡洛斯（#b混沌#k>）。#g[" + getPlayer().getOneInfoQuestInteger(1234569, "kalos_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
        Message += "#L3#<老闆：申請進入監視者卡洛斯（#b極限#k>）。#g[" + getPlayer().getOneInfoQuestInteger(1234569, "kalos_clear") + "/" + (getPlayer().getBossTier() + 1) + "]#k#l\r\n";
        Message += "#L4#<老闆：申請進入監視者卡洛斯（#b而不是練習模式#k）>。 #l#k\r\n";
        Message += "#L5#<老闆：申請進入監視者卡洛斯（#b正常練習模式#k）>。 #l#k\r\n";
        Message += "#L6#<老闆：申請進入監視者卡洛斯（#b混沌練習模式#k）>。 #l#k\r\n";
        Message += "#L7#<老闆：申請進入監視者卡洛斯（#b極限練習模式#k）>。 #l#k\r\n";
        */
        //Message += "#L8#<老闆：申請進入監視者卡洛斯（#r健康模式#k）>。 #l#k\r\n";
        Message += "#L8#離開地圖 #l#k\n";
        Message += "#L9#不移動。 #l";
        int Menu = target.askMenu(Message, ScriptMessageFlag.BigScenario);
        if (Menu < 0 || Menu >= 9) {
        	return;
        } else if (Menu == 8) {
            getPlayer().warp(100000000);
            return;
        } else if (target.getParty() == null) {
        	int partyReq = target.askYesNo("必須有派對才能入場。 您要創建派對嗎？");
        	if (partyReq != 1) {
        		return;
        	}
        	else {
        		getPlayer().createParty();
        	}
        }
        if (target.getParty().getLeader().getId() != target.getId()) {
            self.sayOk("請通過派對現場進行。", ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        if (!target.getParty().isPartySameMap()) {
            self.sayOk("所有隊員必須在同一地圖上。", ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        if (target.getParty().getPartyMemberList().size() > 3) {
            self.sayOk("最多3人可以進入同一個隊伍。", ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        
        //현재는 카오스임. 380때 카오스 -> 노말로 바뀜
        if (Menu == 1 || Menu == 5) {
        	NormalKalosEnter fieldSet = (NormalKalosEnter) fieldSet("NormalKalosEnter");
            int enter = fieldSet.enter(target.getId(), Menu == 5, 7);
            if (enter == 6) {
                self.sayOk("沒有可用的實例。 請使用其他頻道。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                return;
            }
            if (enter == -5) {
                self.sayOk("BOSS提爾不足的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                return;
            }
            if (enter == -3) {
            	self.sayOk("最近一周內有打倒卡洛斯的隊員。 卡洛斯每週只可通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
            	/*
                if (DBConfig.isGanglim) {
                    self.sayOk("今天有消耗了所有入場次數的隊員。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                } else {
                    self.sayOk("最近一周內有打倒卡洛斯的隊員。 卡洛斯每週只可通關一次\r\n#r<通關記錄每週四統一初始化。>", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                }
                */
                return;
            }
            if ((enter == -1 || enter == 4)) {
                self.sayOk("入場限制次數不足或有等級限制不符的隊員無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                return;
            }
            if (enter == -2) {
                self.sayOk("因為還有限制入場時間的隊員，所以無法入場。", ScriptMessageFlag.Scenario, ScriptMessageFlag.NpcReplacedByNpc);
                return;
            }
            //break;
        }
	}

    
    public void kalos_direction1() {
        setIngameDirectionMode(false, false, false);
        blind(1, 255, 0, 0, 0, 0, 0);
        spineEffect("Effect/Direction20.img/bossKalos/1phase_spine/skeleton", "animation", "intro", 0, 0, 1, 0);
        effectSound("Sound/SoundEff.img/kalos/1phase");
        getPlayer().setRegisterTransferField(getPlayer().getMapId() + 20);
        getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis() + 7000);
        delay(7000);
        environmentChange(31, "intro", 100);
        getOnOffFade(100, "BlackOPut", 0);
    }
    
    public void kalos_direction1_easy() {
    	kalos_direction1();
    }
    
    public void kalos_direction1_chaos() {
    	kalos_direction1();
    }
    
    public void kalos_direction1_ex() {
    	kalos_direction1();
    }
    
    public void kalos_direction2() {
        setIngameDirectionMode(false, false, false);
        blind(1, 255, 0, 0, 0, 0, 0);
        spineEffect("Effect/Direction20.img/bossKalos/2phase_spine/skeleton", "animation", "intro", 0, 0, 1, 0);
        effectSound("Sound/SoundEff.img/kalos/2phase");
        getPlayer().setRegisterTransferField(getPlayer().getMapId() + 20);
        getPlayer().setRegisterTransferFieldTime(System.currentTimeMillis() + 7000);
        delay(7000);
        environmentChange(31, "intro", 100);
        getOnOffFade(100, "BlackOPut", 0);
    }
    
    public void kalos_direction2_easy() {
    	kalos_direction2();
    }
    
    public void kalos_direction2_chaos() {
    	kalos_direction2();
    }
    
    public void kalos_direction2_ex() {
    	kalos_direction2();
    }
    
    
    public void ptKalosOut() {
    	if (getPlayer().getMap() instanceof Field_BossKalos) {
    		Field_BossKalos kalosmap = (Field_BossKalos) getPlayer().getMap();
    		if (kalosmap.findBoss() != null) {
    			getPlayer().dropMessage(5, "戰鬥進行中可點擊掠奪退出。");
    			return;
    		}
    	}
    	kalosOut();
    }
    
    public void kalosOut() {
    	initNPC(MapleLifeFactory.getNPC(9091028));
    	if (1 == target.askYesNo("您確定要停止戰鬥並退出嗎？")) {
    		getPlayer().warp(410005005);    		
    	}
    }


}
