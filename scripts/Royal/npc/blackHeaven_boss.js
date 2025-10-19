var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
        var count = 1;
    setting = [
        ["Hard_Lotus", count, 350060700, 230, 2],
        ["Normal_Lotus", count, 350060400, 220, 0],
        ["Extreme_Lotus", 1, 350060700, 290, 0],
    ]
    name = ["하드", "노멀", "익스트림"]
    if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }

    if (status == 0) {
        talk = "스우를 쓰러뜨리기 위해 블랙헤븐 코어로 이동할까?\r\n\r\n";
        talk += "#L1#블랙헤븐 코어(노멀 모드)로 이동 123한다.(레벨 220이상)#l\r\n";
        talk += "#L0#블랙헤븐 코어(하드 모드)로 이동 한다.(레벨 230이상)#l\r\n";
      //  talk += "#L2#블랙헤븐 코어(익스트림 모드)로 이동 한다.(레벨 190이상)#l\r\n";
        cm.sendSimpleS(talk, 0x26);
    } else if (status == 1) {
        st = selection;
        if (cm.getParty() == null) {
            cm.sendOkS("1인 이상 파티를 맺어야만 입장할 수 있습니다.", 0x24);
            cm.dispose();
            return;
        } else if (cm.getPlayerCount(setting[st][2]) >= 1 || cm.getPlayerCount(Number(setting[st][2] + 100)) >= 1 || cm.getPlayerCount(Number(setting[st][2] + 200)) >= 1 || cm.getPlayerCount(Number(setting[st][2] + 250)) >= 1) {
            cm.sendOkS("이미 누군가가 스우에 도전하고 있습니다.\r\n다른채널을 이용 해 주세요.", 0x24);
            cm.dispose();
            return;
        } else if (!cm.isLeader()) {
            cm.sendOkS("파티장만이 입장을 신청할 수 있습니다.", 0x24);
            cm.dispose();
            return;
	} else if (!cm.allMembersHere()) {
	    cm.sendOk("모든 멤버가 같은 장소에 있어야 합니다.");
	    cm.dispose();
            return;
        }
        if (!cm.isBossAvailable(setting[st][0], setting[st][1])) {
            talk = "#fs11#파티원 중 "
            for (i = 0; i < cm.BossNotAvailableChrList(setting[st][0], setting[st][1]).length; i++) {
                if (i != 0) {
                    talk += ", "
                }
                talk += "#b#e" + cm.BossNotAvailableChrList(setting[st][0], setting[st][1])[i] + ""
            }
            talk += "#k#n님이 오늘 입장횟수를 모두 소진하셨습니다";
            cm.sendOkS(talk, 0x24);
            cm.dispose();
            return;
        } else if (!cm.isLevelAvailable(setting[st][3])) {
            talk = "파티원 중 "
            for (i = 0; i < cm.LevelNotAvailableChrList(setting[st][3]).length; i++) {
                if (i != 0) {
                    talk += ", "
                }
                talk += "#b#e" + cm.LevelNotAvailableChrList(setting[st][3])[i] + ""
            }
            talk += "#k#n님의 레벨이 부족합니다.\r\n스우 " + name[st] + "모드는 " + setting[st][3] + " 레벨 이상만 입장 가능합니다.";
            cm.sendOkS(talk, 0x24);
            cm.dispose();
            return;
        } else if (!cm.isBossTier(setting[st][4])) {
            talk = "파티원 중 "
            for (i = 0; i < cm.BossTierChrList(setting[st][4]).length; i++) {
                if (i != 0) {
                    talk += ", "
                }
                talk += "#b#e" + cm.BossTierChrList(setting[st][4])[i] + ""
            }
            talk += "#k#n님의 보스랭크가 부족합니다.\r\n스우 " + name[st] + "모드는 " + setting[st][4] + " 랭크 이상만 입장 가능합니다.";
            cm.sendOkS(talk, 0x26);
            cm.dispose();
            return;
        } else {
            cm.addBoss(setting[st][0]);
            em = cm.getEventManager(setting[st][0]);
            if (em != null) {
                cm.getEventManager(setting[st][0]).startInstance_Party(setting[st][2] + "", cm.getPlayer());
            }
            cm.dispose();
        }
    }
}