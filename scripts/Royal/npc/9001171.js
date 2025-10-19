var status = -1;
var sel = 0;

var limit = 6; // 일일 제한 횟수
var ccoin = 2000005; // 필요 코인
var quantity = 15; // 코인 갯수

function action(mode, type, selection) {
    if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    switch (status) {
        case 0:
            var t = "메이플 갤럭시 최고의 사냥터!\r\n#b#e<프로즌 링크>#n#k에 오신 걸 환영합니다요~!\r\n";
            t += "#L0##b<프로즌 링크>에 입장하고 싶어요.\r\n";
            t += "#L1##b<프로즌 링크>에 대해 다시 알려주세요.\r\n";
            t += "#L2##b<프로즌 링크>를 이용한 횟수를 알고 싶어요.#k";
            cm.sendSimple(t);
            break;
        case 1:
            sel = selection;
            if (selection == 0) {
                cm.getPlayer().changeMap(cm.getPlayer().getWarpMap(993014200));
                cm.getPlayer().startFrozenLinkTask();
                cm.dispose();
            } else if (selection == 1) {
                cm.sendNext("#fs12##b#e<프로즌 링크>#k란?#n\r\n\r\n- #b자신의 몬스터#k만 보이고 사냥할 수 있는 사냥터입니다.\r\n\r\n- #b다른 유저의 몬스터#k는 보이지 않고 사냥할 수 있습니다.\r\n\r\n- 사냥 시 몬스터 경험치에 추가로 #b150%의 프로즌 링크 보너스 경험치#k를 획득할 수 있습니다.");
            } else if (selection == 2) {
                cm.sendNext("오늘 #h #님이 이용하신 프로즌 링크 횟수는 " + cm.GetCount("frozen_link") + "회 이용하셨습니다.");
                cm.dispose();
            }
            break;
        case 2:
            cm.sendNextPrev("#fs12##e#b<프로즌 링크>#k 이용 방법#n\r\n\r\n- <프로즌 링크>맵 내에 있는 NPC 따르뜨와 대화하면\r\n #b1일 " + limit + "회 네오 스톤 15개#k를 사용하여 #b사냥 가능 몬스터#k 수를 #b500마리 충전#k할 수 있습니다.\r\n\r\n- #b사냥 가능 몬스터 수#k가 충전되어 있으면 몬스터가 소환되며 사냥 시 차감됩니다.\r\n\r\n- #b사냥 가능 몬스터 수#k가 모두 소진되면 더 이상 몬스터가 소환되지 않습니다.");
            break;
        case 3:
            cm.sendNextPrev("#fs12##e#b<프로즌 링크>#k이용방법#n\r\n\r\n- 다른 맵으로 이동하거나 접속을 종료하여도 남은 #b사냥 가능 몬스터 수#k는 유지됩니다.\r\n\r\n- 날짜가 바뀌어도 남은 #b사냥 가능 몬스터 수#k는 유지 됩니다.\r\n\r\n- #b사냥 가능 몬스터 수#k가 모두 소진되기 전에는 다시 충전을 할 수 없습니다.");
            break;
        case 4:
            cm.sendNextPrev("#fs12##e#b<프로즌 링크>#k이용방법#n\r\n\r\n- 몬스터가 소환되고 있을 때 NPC #b따르뜨#k와 대화하면 소환되는 #b몬스터 종류를 변경#k할 수 있습니다.\r\n\r\n- 선택 가능한 몬스터의 범위는 #b캐릭터 레벨의 ± 20레벨#k입니다.");
            break;
        case 5:
            cm.dispose();
            break;
    }
}