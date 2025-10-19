importPackage(java.lang);
importPackage(Packages.server);

var status = -1;
var maxUse = 0;

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
        // 가지고 있는 럭키백 최대 개수
        maxUse = cm.itemQuantity(2433979);
        if (maxUse <= 0) {
            cm.sendOk("럭키백이 하나도 없어요!");
            cm.dispose();
            return;
        }
        cm.sendGetNumber(
            "#b메소 5천만 ~ 3억원 랜덤 럭키백#k입니다.\r\n" +
            "몇 개를 사용하시겠습니까? (최대 #r" + maxUse + "#k개)",
            1, 1, maxUse
        );

    } else if (status == 1) {
        var count = selection;
        if (count < 1 || count > maxUse) {
            cm.sendOk("올바른 개수를 입력해주세요.");
            cm.dispose();
            return;
        }

        var totalMeso = 0;
        for (var i = 0; i < count; i++) {
            // 50,000,000 ~ 300,000,000 사이 랜덤 메소
            var rand = Packages.objects.utils.Randomizer.rand(50000000, 300000000);
            totalMeso += rand;
        }

        // 아이템 차감 및 메소 지급
        cm.gainItem(2433979, -count);
        cm.gainMeso(totalMeso);

        cm.sendOk("메소 럭키백 #b" + count + "개#k 사용 결과,\r\n총 #b" + totalMeso + " 메소#k를 획득하였습니다!");
        cm.dispose();
    }
}
