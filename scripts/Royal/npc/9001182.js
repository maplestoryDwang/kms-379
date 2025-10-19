var status = -1;
var sel = 0;

var limit = 10; // 충전 횟수 제한
var ccoin = 4310261; // 사용할 코인
var quantity = 15; // 필요 코인 갯수

var promotion = false;

mobid = [9010172, 9010173, 9010174, 9010175, 9010176, 9010178, 8644005, 8644006, 8644424]

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
    if (status == 0) {
        if (cm.getPlayer().getFrozenLinkMobCount() <= 0) {
            if (!cm.CountCheck("frozen_link", limit)) {
                cm.sendNext("이미 오늘 충전가능횟수를 다 썼어!");
                cm.dispose();
                return;
            }
        }
        var msg = "메이플월드에서 가장 쉬원한 사냥터!\r\n"
        msg += "#b<프로즌 링크>#k에 온 걸 진심으로 환영해~!\r\n\r\n";
        msg += "#b#L0# 소환되는 몬스터 종류를 변경#k할래.\r\n"
        msg += "#b#L1# 사냥 가능 몬스터 수를 충전#k할래.\r\n"
        cm.sendSimple(msg);
    } else if (status == 1) {
        sel = selection;
        if (selection == 0) {
            var msg = "선택하실 수 있는 #b프로즌 링크 몬스터#k는 아래와 같습니다!\r\n원하시는 몬스터를 선택해 주세요~!\r\n\r\n";
            for (var i = 0; i < mobid.length; i++) {
                msg += "#L" + i + "# #b[프로즌 링크] : #o" + mobid[i] + "#\r\n";
            }
            cm.sendSimple(msg);
        } else if (selection == 1) {


            if (cm.haveItem(4001884)) {
                cm.sendYesNo("#b#v" + 4001884 + "##t" + 4001884 + "##k을 가지고 있구나, 지금 바로 #b3000마리#k를 충전해보겠어?");
                promotion = true;
                return;
            }
            var coinCount = parseInt(cm.getPlayer().getStackEventGauge(0));

            if (Math.floor(coinCount / quantity) < (limit - cm.GetCount("frozen_link"))) {
                limit = Math.floor(coinCount / quantity)
            } else {
                limit = (limit - cm.GetCount("frozen_link"));
            }
            cm.sendGetNumber("몇 회나 충전할 거니?\r\n\r\n#r#e※ 1회당 코인 " + quantity + "개를 소모하고 몬스터 500마리를 충전합니다.\r\n\r\n#b( 보유 중인 네오 스톤 : " + coinCount + "개)", 1, 1, limit);
        }
    } else if (status == 2) {
        if (sel == 0) {
            cm.getPlayer().setFrozenLinkMobID(mobid[selection]);
            cm.getPlayer().getMap().killAllMonstersFL(true, cm.getPlayer().getFrozenLinkSerialNumber());
            cm.getPlayer().cancelFrozenLinkTask();
            cm.getPlayer().changeMap(cm.getPlayer().getWarpMap(993014200));
            if (cm.getPlayer().getFrozenLinkMobCount() > 0) {
                cm.getPlayer().startFrozenLinkTask();
            }
            cm.sendOk("선택한 몬스터로 변경해 드렸습니다요~!");
            cm.dispose();
        } else if (sel == 1) {
            if (promotion) {
                cm.getPlayer().addFrozenLinkMobCount(3000);
                cm.gainItem(4001884, -1);
                cm.getPlayer().dropMessage(6, "3000마리 충전이 완료되었습니다.");
                if (cm.getPlayer().getFrozenLinkMobID() == 0) {
                    var a = Math.floor(cm.getPlayer().getLevel() / 10) - 1;
                    cm.getPlayer().setFrozenLinkMobID(mobid[selection]);
                }
                cm.getPlayer().startFrozenLinkTask();
                dispose();
                return;
            }

            var coinCount = parseInt(cm.getPlayer().getStackEventGauge(0));
            if (selection < 0) {
                return;
            }
            if (/*!cm.haveItem(ccoin, selection * quantity)*/coinCount < selection * quantity) {
                cm.sendNext("네오 스톤이 부족한 것 같은데요~?");
                cm.dispose();
                return;
            }
            cm.getPlayer().addFrozenLinkMobCount(500 * selection);
            //cm.gainItem(ccoin , -selection * quantity)
            cm.getPlayer().gainStackEventGauge(0, -(selection * quantity), true);
            //cm.getPlayer().gainKillPoint(-selection * quantity);
            cm.getPlayer().dropMessage(6, selection * 500 + "마리 충전이 완료되었습니다.");
            for (var i = 0; i < selection; ++i) {
                cm.getPlayer().CountAdd("frozen_link");
            }
            if (cm.getPlayer().getFrozenLinkMobID() == 0) {
                var a = Math.floor(cm.getPlayer().getLevel() / 10) - 1;
                cm.getPlayer().setFrozenLinkMobID(mobid[selection]);
            }
            cm.getPlayer().startFrozenLinkTask();
            cm.dispose();
        }
    }
}