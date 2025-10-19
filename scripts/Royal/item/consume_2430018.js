importPackage(java.lang);
importPackage(Packages.server);
importPackage(Packages.client.inventory);


function start() {
    status = -1;
    action(1, 0, 0);
}

var enter = "\r\n";

var item1 = [2430018, "#z2430018#", "1"];
var ac1 = [0, "강림 캐시", 10000];
var ac2 = [1712002, "아케인 심볼 : 츄츄 아일랜드", "5"];
var 별 = "#fUI/FarmUI.img/objectStatus/star/whole#";

function action(mode, type, selection) {
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
            var chat = "#fs11##fn나눔고딕##fc0xFF7401DF#< 교환시스템 :: 캐시 상자 교환 >#k" + enter + "" + enter;
            chat += "#r#e"+별+"인벤토리 공간을 꼭 확보해주신후 사용해주시길 바랍니다"+ enter + enter + "" ;
            chat += "#L1#" +  별 + ac1[1] + " 교환" 
            //    + enter + "#L2##i" + ac2[0] + "# " + ac2[1] + " 교환"
            //+ enter + "#L7##i" + ac2[0] + "# " + ac2[1] + " 교환"
            //+ enter + "#L4##i2437529#강화석 교환"
            cm.sendOkS(chat, 0x00);
        } else if (status == 1) {
            select = selection;
            if (select == 1) {
                var suk1 = Math.floor((cm.itemQuantity(2430018) / 1));
                stigmacoin = Math.min(suk1);
                cm.sendGetNumber("\r\n#fn나눔고딕##i" + item1[0] + "# " + item1[1] + " #fc0xFF7401DF#" + item1[2] + "#k 개를 주시면"
                    + enter + "#i" + ac1[0] + "# " + ac1[1] + " #fc0xFF7401DF#" + ac1[2] + "#kC 로 교환해드립니다."
                    + enter + "#Cgray#(현재 교환 가능한 " + item1[1] + " 갯수 : " + stigmacoin + "개)", 1, 1, 3000);
            }
            
        } else if (status == 2) {
            var cost = selection;
            if (cost > 3000) {
                cm.dispose();
                return;
            }

            if (select == 1) {
                if (cm.haveItem(item1[0], item1[2] * cost)) {
                    if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                        cm.gainItem(item1[0], -item1[2] * cost);
                        cm.getPlayer().gainCashPointEvent(0, ac1[2]*cost);
                        cm.sendOk("#fn나눔고딕#교환완료\r\n");
                        cm.dispose();

                        //로그작성
                        Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/캐시.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n사용한 아이템 : [Z] 강림 캐시 교환권 (2430018)\r\n획득 캐시 : " + ac1[2]*cost + "\r\n\r\n", true);
                    } else {
                        cm.sendOk("#fn나눔고딕##r장비창을 확인해주세요");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("#fn나눔고딕##r#i" +  item1[0] + "#" + item1[1]  + "가 부족합니다. \r\n 또는 인벤토리가 꽉 차있는지 확인 해주시길 바랍니다");
                    cm.dispose();
                }
            }
            
        }
    }
}