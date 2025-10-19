var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
        var count = 1;
    setting = [
        ["Normal_Cygnus", count, 271040100, 170],
    ]
    name = ["노멀"]
    if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }

    if (status == 0) {
        if (cm.getPlayer().getMapId() == 271040100) {
            cm.sendYesNo("전투를 마치고 시그너스의 후원으로 퇴장하시겠습니까?");
        } else {
            talk = "타락한 시그너스에게 맞설 준비는 되셨습니까?\r\n\r\n"
            talk += "#L0# #b시그너스(노멀) 입장을 신청한다.";
            cm.sendSimple(talk);
        }
    } else if (status == 1) {
        if (cm.getPlayer().getMapId() == 271040100) {
            cm.warp(271040000);
            cm.dispose();
            return;
        }
        st = selection;
        if (cm.getPlayer().getParty() == null) {
            cm.sendOk("1인 이상의 파티에 속해야만 입장할 수 있습니다.");
            cm.dispose();
            return;
        } else if (!cm.isLeader()) {
            cm.sendOk("파티장만 입장을 신청할 수 있습니다.");
            cm.dispose();
            return;
	} else if (!cm.allMembersHere()) {
	    cm.sendOk("모든 멤버가 같은 장소에 있어야 합니다.");
	    cm.dispose();
            return;
        } else if (cm.getPlayerCount(setting[st][2]) >= 1) {
            cm.sendNext("이미 다른 파티가 안으로 들어가 시그너스에게 도전하고 있는 중입니다.");
            cm.dispose();
            return;
        }
        if (!cm.isBossAvailable(setting[st][0], setting[st][1])) {
            talk = "오늘 시그너스를 클리어한 파티원이 있습니다. 시그너스(노멀)은 하루에 " + setting[st][1] + "회만 클리어 가능합니다.\r\n"
            talk += "#r#e<클리어 기록은 매일 자정에 일괄 초기화됩니다.>#k#n"
            cm.sendOk(talk);
            cm.dispose();
            return;
        } else if (!cm.isLevelAvailable(setting[st][3])) {
            talk = "파티원 중 #b#e"
            for (i = 0; i < cm.LevelNotAvailableChrList(setting[st][3]).length; i++) {
                if (i != 0) {
                    talk += ", "
                }
                talk += "#b#e" + cm.LevelNotAvailableChrList(setting[st][3])[i] + ""
            }
            talk += "#k#n님의 레벨이 부족합니다. 시그너스는 레벨 " + setting[st][3] + "이상만 도전하실 수 있습니다.";
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