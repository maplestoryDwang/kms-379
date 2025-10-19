function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        cm.sendSimple("뭐... 좋소. 당신들은 충분한 자격이 되어 보이는군. 무엇을 하시겠소?\r\n#b" +
            "#L0#폐광 동굴을 조사하러 떠난다.#l\r\n" +
            "#L1#자쿰 던전을 탐사한다.#l\r\n" +
            "#L2#자쿰에게 바칠 제물을 받는다.#l\r\n" +
            "#L3#엘나스로 이동한다.#l");
    } else if (status == 1) {
        sT = selection;
        if (selection == 0) {
            if (cm.getPlayerCount(280010000) >= 1) {
                cm.sendNext("이미 다른 파티가 해당 던전을 도전하고 있는 중이라네. 잠시 후에 다시 시도해 주길 바라네.");
            } else {
                cm.warp(280010000);
            }
            cm.dispose();
            return;
        } else if (selection == 1) {
            cm.sendNext("좋네! 이제부터 자네는 수 많은 장애물들이 있는 맵으로 이동될 것일세. 행운을 비네!");
        } else if (selection == 2) {
            cm.sendSimple("어느 자쿰에게 바칠 제물이 필요하오?\r\n#b"
                //+ "#L0#이지 자쿰#l\r\n"
                + "#L1#노말/카오스 자쿰#l\r\n");
        } else {
            cm.sendNext("그럼 엘나스로 보내주겠네");
        }
    } else if (status == 2) {
        sT2 = selection;
        if (sT == 1) {
            cm.warp(280020000);
            cm.dispose();
            return;
        } else if (sT == 2) {
            if (selection == 0) {
                if (cm.itemQuantity(4001796) >= 1) {
                    cm.sendOk("이미 이지 자쿰의 제물인 #b#t4001796##k을 가지고 있군.. 다 사용하면 다시 말하게.");
                    cm.dispose();
                    return;
                } else {
                    cm.sendNext("이지 자쿰에게 바칠 제물이 필요하군..");
                }
            } else {
                if (cm.itemQuantity(4001017) >= 1) {
                    cm.sendOk("이미 자쿰의 제물인 #b#t4001017##k을 가지고 있군.. 다 사용하면 다시 말하게.");
                    cm.dispose();
                    return;
                } else {
                    cm.sendNext("자쿰에게 바칠 제물이 필요하군..");
                }
            }
        } else {
            cm.warp(211000000);
            cm.dispose();
        }
    } else if (status == 3) {
        if (sT2 == 0) {
            cm.sendNextPrev("다만 자쿰을 부르는 데 제물로 필요한 #b#t4001796##k은 내게는 많이 있으니 그냥 주겠네.");
            if (!cm.haveItem(4001796)) {
                cm.gainItem(4001796, 1);
            }
        } else {
            cm.sendNextPrev("다만 자쿰을 부르는 데 제물로 필요한 #b#t4001017##k은 지금 내게는 많이 있으니 그냥 주겠네.");
            if (!cm.haveItem(4001017)) {
                cm.gainItem(4001017, 10);
            }
        }
    } else if (status == 4) {
        if (sT2 == 0) {
            cm.sendNextPrev("이것을 이지 자쿰의 제단에 떨어뜨리면 된다네.");
        } else {
            cm.sendNextPrev("이것을 자쿰의 제단에 떨어뜨리면 된다네.");
        }
        cm.dispose();
        return;
    }
}