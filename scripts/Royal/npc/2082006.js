var enter = "\r\n";
var seld = -1;

var need = [
    {'itemid' : 4001326, 'qty' : 1},
    {'itemid' : 4001327, 'qty' : 1},
    {'itemid' : 4001328, 'qty' : 1},
    {'itemid' : 4001329, 'qty' : 1},
    {'itemid' : 4001330, 'qty' : 1},
    {'itemid' : 4001331, 'qty' : 1},
    {'itemid' : 4001332, 'qty' : 1}
];
var tocoin = 2433979, toqty = 5;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, sel) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }

    if (status == 0) {
        // 인트로 대사
        var msg = "#fs11##fc0xFF000000#안녕하세요~ 멋진 그림을 그리고 싶은데 크레파스가 부족해서요.. 도와주실래요?"+enter;

        // 필요한 아이템 수량 안내
        for (var i = 0; i < need.length; i++) {
            var item = need[i];
            if (i != need.length - 1) {
                msg += "#b#i" + item.itemid + "##z" + item.itemid + "# " + item.qty + "개 / #r#c " 
                    + item.itemid + "#개 보유" + "#b" + enter;
            } else {
                msg += "#i" + item.itemid + "##z" + item.itemid + "# " + item.qty + "개 / #r#c " 
                    + item.itemid + "#개 보유#fc0xFF000000#\r\n\r\n크레파스를 모아주세요!"+enter;
            }
        }

        // 보상 안내
        msg += "#b보상 : #i" + tocoin + "# #z" + tocoin + "# " + toqty + "개"+enter;

        if (haveNeed(1)) {
            cm.sendNext(msg);
        } else {
            msg += "\r\n#fc0xFF000000#크레파스가 부족해요! 빨리 구해주세요~";
            cm.sendOk(msg);
            cm.dispose();
        }

    } else if (status == 1) {
        // 최대 교환 가능 개수 계산
        var counts = [];
        for (var i = 0; i < need.length; i++) {
            counts.push(Math.floor(cm.itemQuantity(need[i].itemid) / need[i].qty));
        }
        counts.sort();
        var max = counts[0];

        cm.sendGetNumber(
            "#fs11##fc0xFF000000#최대 #b" + max + "번#k 교환 가능\r\n몇 번 교환하시겠어요?",
            1, 1, max
        );

    } else if (status == 2) {
        // 선택한 개수만큼 인벤토리에서 제거 및 보상 지급
        if (!haveNeed(sel)) {
            cm.sendOk("#fc0xFF000000#크레파스가 모자라요!");
            cm.dispose();
            return;
        }
        for (var i = 0; i < need.length; i++) {
            cm.gainItem(need[i].itemid, -need[i].qty * sel);
        }
        cm.gainItem(tocoin, toqty * sel);
        cm.sendOk("#fs11##fc0xFF000000#고마워요! 이제 그림을 그릴 수 있어요!");
        cm.dispose();
    }
}

function haveNeed(a) {
    for (var i = 0; i < need.length; i++) {
        if (!cm.haveItem(need[i].itemid, need[i].qty * a)) {
            return false;
        }
    }
    return true;
}
