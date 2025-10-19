importPackage(Packages.client.inventory);


function start() {
    status = -1;
    action(1, 0, 0);
}

var seld = -1;
var enter = "\r\n";

var item1 = [2437760, 1];
var ac = [
[1712001, "아케인 심볼 : 소멸의 여로", "5"],
[1712002, "아케인 심볼 : 츄츄 아일랜드", "5"],
[1712003, "아케인 심볼 : 레헬른", "5"],
[1712004, "아케인 심볼 : 아르카나", "5"],
[1712005, "아케인 심볼 : 모라스", "5"],
[1712006, "아케인 심볼 : 에스페라", "5"]
];
var 별 = "#fUI/FarmUI.img/objectStatus/star/whole#";

function action(mode, type, sel) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            var chat = "#fs11#";
            chat += "#fc0xFF7401DF#< 교환시스템 :: 심볼교환 >#k" + enter + enter;
            chat += "#r"+별+" 인벤토리 공간을 꼭 확보해주신후 사용하시기 바랍니다"+ enter + enter ;
            chat += "#fc0xFF000000#";
            for(var i = 0;i<ac.length;i++) {
                chat += "#b#L" + i + "##i" + ac[i][0] + "# " + ac[i][1] + " 교환" + enter;
            }
            cm.sendOkS(chat, 0x00);
        } else if (status == 1) {
             seld = sel;
            var suk1 = Math.floor((cm.itemQuantity(item1[0]) / 1));
            stigmacoin = Math.min(suk1);
            msg = "#fs11#" + enter;
            msg += "#i" + item1[0] + "# #z" + item1[0] + "# #fc0xFF7401DF#" + item1[1] + "#k 개를 주시면" + enter;
            msg += "#i" + ac[seld][0] + "# " + ac[seld][1] + " #fc0xFF7401DF#" + ac[seld][2] + "#k 개를 교환해드립니다." + enter;
            msg += "#Cgray#(현재 교환 가능한 #z" + item1[0] + "# 갯수 : " + stigmacoin + "개)";
            cm.sendGetNumber(msg, 1, 1, stigmacoin);
        } else if (status == 2) {
            var cost = sel;
            if((item1[1] * cost) < 0){
                cm.addCustomLog(4, "[아케인심볼] " + cm.getPlayer().getID() + " qty :" + qty +"개 음수 적용");
                cm.dispose();
                return;
            }
            if (cm.haveItem(item1[0], item1[1] * cost) && cm.canHold(1713000, cost*5)) {
                cm.gainItem(item1[0], -item1[1] * cost);
                cm.gainItem(ac[seld][0], cost*5);
                cm.sendOk("#fs11#교환이 완료되었습니다\r\n");
                cm.dispose();
                return;
            } else {
                cm.sendOk("#fs11##r인벤토리 자리가 부족합니다");
                cm.dispose();
                return;
            }
                
        }
    }
}