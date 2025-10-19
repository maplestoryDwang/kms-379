


/*

	* 단문엔피시 자동제작 스크립트를 통해 만들어진 스크립트 입니다.

	* (Guardian Project Development Source Script)

	뭐지요 에 의해 만들어 졌습니다.

	엔피시아이디 : 1105008

	엔피시 이름 : 체키

	엔피시가 있는 맵 : 헤네시스 : 리나의 집 (100000003)

	엔피시 설명 : MISSINGNO


*/

var status = -1;

function start() {
    status = -1;
    action (1, 0, 0);
}

function action(mode, type, selection) {

    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        status --;
    }
    if (mode == 1) {
        status++;
    }

    if (status == 0) {
        말 = "#fs11#무슨 일이세요?\r\n\r\n"
        말 += "#L0##b메탈 아머의 위장색을 바꾸고 싶어요.";
        cm.sendNext(말);
    } else if (status == 1) {
        cm.sendYesNo("#fs11##b메탈아머의 위장색#k을 바꾸고 싶다구요?\r\n#e#r100만 메소#n#k만 주시면 #b원하시는 색상#k으로 도색해 드리죠!");

    } else if (status == 2) {
        if (cm.getPlayer().getJob() >= 3500 && cm.getPlayer().getJob() <= 3512) {
            if (cm.getPlayer().getMeso() > 1000000) {
                말 = "#fs11##b어떤 색#k으로 바꿔 드릴까요?\r\n#r(현재 메탈아머와 같은 색상을 선택해도 메소가 차감됩니다)\r\n\r\n#b"
                말 += "#L0#기본색\r\n"
                말 += "#L2881#하늘색\r\n"
                말 += "#L5745#붉은색\r\n"
                말 += "#L3601#파란색\r\n"
                말 += "#L4913#핑크색\r\n"
                말 += "#L4273#보라색\r\n"
                말 += "#L417#주황색\r\n"
                말 += "#L2049#초록색\r\n"
                말 += "#L1025#연두색\r\n"
                cm.sendNext(말);
            } else {
                cm.sendOk("#fs11#메소가 부족하신 것 같은데요?\r\n위장색을 바꾸려면 #e#r100만 메소#n#k가 필요해요.")
                cm.dispose();
            }
        } else {
            cm.sendOk("#fs11#메카닉만 이용하실 수 있습니다.");
            cm.dispose();
        }

    } else if (status == 3) {
        cm.sendOk("#fs11#자, 도색이 잘 끝났습니다. 아주 멋지군요!");
        cm.gainMeso(-1000000);
        cm.getPlayer().dropMessage(5,"메카닉 메탈 아머 위장색이 변경 되었습니다.")
        cm.getPlayer().setKeyValue(19752, "hiddenpiece", selection);
        cm.dispose();
    }
}