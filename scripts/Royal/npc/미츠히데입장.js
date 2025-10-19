var status = -1;

보라 = "#fMap/MapHelper.img/weather/starPlanet/7#";
파랑 = "#fMap/MapHelper.img/weather/starPlanet/8#";
별파 = "#fUI/GuildMark.img/Mark/Pattern/00004001/11#"
별노 = "#fUI/GuildMark.img/Mark/Pattern/00004001/3#"
별흰 = "#fUI/GuildMark.img/Mark/Pattern/00004001/15#"
별갈 = "#fUI/GuildMark.img/Mark/Pattern/00004001/5#"
별빨 = "#fUI/GuildMark.img/Mark/Pattern/00004001/1#"
별검 = "#fUI/GuildMark.img/Mark/Pattern/00004001/16#"
별보 = "#fUI/GuildMark.img/Mark/Pattern/00004001/13#"
별 = "#fUI/FarmUI.img/objectStatus/star/whole#"
S = "#fUI/CashShop.img/CSEffect/today/0#"
보상 = "#fUI/UIWindow2.img/Quest/quest_info/summary_icon/reward#"
획득 = "#fUI/UIWindow2.img/QuestIcon/4/0#"
색 = "#fc0xFF6600CC#"
엔터 = "\r\n"
엔터2 = "\r\n\r\n"

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
        var count = 1;
    setting = [
        ["BossAMN", 1, 874004002, 220],
        ["BossAMH", 1, 874004002, 280],
    ]
    name = ["노말 아케치 미츠히데","하드 아케치 미츠히데"]
    if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }

    if (status == 0) {
        if (cm.getPlayer().getMapId() == 874004002) {
            cm.sendYesNo("전투를 마치고 마을로 퇴장하시겠습니까?");
        } else {
            talk = "#fs11#죽음의 무사 아케치 미츠히데 에게 맞설 준비는 되셨습니까?\r\n\r\n"
            talk += 색 +"#L0# [이벤트보스] - #b[노말] #r아케치 미츠히데 #b입장을 신청한다.\r\n";
            talk += 색 +"#L1# [이벤트보스] - #b[하드] #r아케치 미츠히데 #b입장을 신청한다.";
            cm.sendSimple(talk);
        }
    } else if (status == 1) {
        if (cm.getPlayer().getMapId() == 874004002) {
            cm.warp(100000000);
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
            cm.sendNext("이미 누군가가 도전하고 있습니다.\r\n다른채널을 이용 해 주세요.");
            cm.dispose();
            return;
        }
        if (!cm.isBossAvailable2(setting[st][0], setting[st][1])) {
            talk = "#fs11#파티원 중 "
            for (i = 0; i < cm.BossNotAvailableChrList2(setting[st][0],  setting[st][1]).length; i++) {
                if (i != 0) {
                    talk += ", "
                }
                talk += "#b#e" + cm.BossNotAvailableChrList2(setting[st][0], setting[st][1])[i] + ""
            }
            talk += "#k#n님이 오늘 입장횟수를 모두 소진하셨습니다";
            cm.sendOk(talk);
            cm.dispose();
            return;
        } else if (!cm.isLevelAvailable(setting[st][3])) {
            talk = "#fs11#파티원 중 #b#e"
            for (i = 0; i < cm.LevelNotAvailableChrList(setting[st][3]).length; i++) {
                if (i != 0) {
                    talk += ", "
                }
                talk += "#b#e" + cm.LevelNotAvailableChrList(setting[st][3])[i] + ""
            }
            talk += "#k#n님의 레벨이 부족합니다. "+ name[st] +"는 레벨 " + setting[st][3] + "이상만 도전하실 수 있습니다.";
            cm.sendOk(talk);
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