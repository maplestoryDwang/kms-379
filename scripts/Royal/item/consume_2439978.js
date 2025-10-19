/*

즉시지급 패키지 간편화 By. 채원

*/


importPackage(java.lang);
importPackage(Packages.server);
importPackage(Packages.client.inventory);

var 패키지명 = "가챠패키지";
var 패키지코드 = "2439978";
var 패키지가격 = "200000";
var 후원포인트 = "200000";
var enter = "\r\n";

var itemlist = [
{'itemid' : 2439960, 'qty' : 1},
{'itemid' : 2630782, 'qty' : 10},
{'itemid' : 5060048, 'qty' : 30},
{'itemid' : 5068305, 'qty' : 15},
{'itemid' : 4310237, 'qty' : 5000},
{'itemid' : 4310266, 'qty' : 5000},
{'itemid' : 5062005, 'qty' : 20},
{'itemid' : 5062503, 'qty' : 20},
{'itemid' : 4001715, 'qty' : 500}
]

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
        if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.EQUIP).getNumFreeSlot() < 5 || cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.USE).getNumFreeSlot() < 5 || cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.ETC).getNumFreeSlot() < 5) {
           cm.sendOk("#fs11#장비,소비,기타 칸을 5칸 이상 비워주세요");
           cm.dispose();
           return;
        }
            // 시작
            
        var msg = "#fs11##b#e[" + 패키지명 + "] #k#n에서\r\n다음과같은 #b보상#k을 지급 받으시겠습니까?#d" + enter + enter;
            msg += "#b#e#i2430017# 강림 포인트 200,000 \r\n";
        for (i = 0; i < itemlist.length; i ++) {
            //cm.gainItem(itemlist[i]['itemid'],itemlist[i]['qty']);
            msg += "#b#e#i"+itemlist[i]['itemid']+"# #z"+itemlist[i]['itemid']+"# "+itemlist[i]['qty']+"개 \r\n";
        }
        msg += "#L1#지급받겠습니다.";
        msg += "#L2#다음에";

        cm.sendSimple(msg);
    } else if(status == 1) {
        if (sel == 1) {
            cm.dispose();
            for (i = 0; i < itemlist.length; i ++) {
            cm.gainItem(itemlist[i]['itemid'],itemlist[i]['qty']);
            }
            cm.gainItem(패키지코드, -1);
            cm.getPlayer().gainDonationPoint(후원포인트);
            cm.sendOkS("#fs11##b#e감사합니다~!", 2);

            //로그작성
            Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[상시패키지]/[NEW]" + 패키지명 + ".log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n사용한 아이템 : " + 패키지명 + " (" + 패키지코드 + ")" + "\r\n\r\n", true);

        } else if (sel == 2) {
            cm.dispose();
            cm.sendOk("#fs11#네");
        }
    }
    
}