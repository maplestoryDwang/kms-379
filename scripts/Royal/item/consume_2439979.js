/*

즉시지급 패키지 간편화 By. 채원

*/


importPackage(java.lang);
importPackage(Packages.server);
importPackage(Packages.client.inventory);

var 패키지명 = "주문서패키지";
var 패키지코드 = "2439979";
var 패키지가격 = "300000";
var 후원포인트 = "500000";
var 선택지급1 = "";
var 선택지급2 = "";
var enter = "\r\n";

var itemlist = [ 
{'itemid' : 2439960, 'qty' : 2},
{'itemid' : 2630133, 'qty' : 5},
{'itemid' : 4310308, 'qty' : 1000},
{'itemid' : 1012632, 'qty' : 1},
{'itemid' : 1022278, 'qty' : 1},
{'itemid' : 1132308, 'qty' : 1},
{'itemid' : 2438145, 'qty' : 2}
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
            msg += "#b#e#i2430017# 강림 포인트 500,000 \r\n";
        for (i = 0; i < itemlist.length; i ++) {
            //cm.gainItem(itemlist[i]['itemid'],itemlist[i]['qty']);
            msg += "#b#e#i"+itemlist[i]['itemid']+"# #z"+itemlist[i]['itemid']+"# "+itemlist[i]['qty']+"개 \r\n";
        }
        msg += "#L1#지급받겠습니다.";
        msg += "#L99#다음에";

        cm.sendSimple(msg);
    } else if(status == 1) {

        if (sel == 1) {
        var msg = "#fs11##b#e[" + 패키지명 + "] #k#n\r\n아래의#b목록#k중 하나를 골라주세요..#d" + enter + enter;
        msg += "#L1# #i2046340# #z2046340# 30개"+enter;
        msg += "#L2# #i2046341# #z2046341# 30개"+enter;
        cm.sendSimple(msg);
        } else if (sel == 2) {
            cm.dispose();
            cm.sendOk("#fs11#네");
        } else if (sel == 99) {
            cm.dispose();
            cm.sendOk("#fs11#네");
        }
    } else if(status ==2) {
        if (sel == 1) {
            선택지급1 = "2046340";
            var msg = "#fs11##b#e[" + 패키지명 + "] #k#n\r\n아래의#b목록#k중 하나를 골라주세요..#d" + enter + enter;
            msg += "#L1# #i2046076# #z2046076# 20개"+enter;
            msg += "#L2# #i2046150# #z2046150#20개"+enter;
            msg += "#L3# #i2046077# #z2046077# 20개"+enter;
            cm.sendSimple(msg);
        } else if (sel == 2) {
            선택지급1 = "2046341";
            var msg = "#fs11##b#e[" + 패키지명 + "] #k#n\r\n아래의#b목록#k중 하나를 골라주세요..#d" + enter + enter;
            msg += "#L1# #i2046076# #z2046076# 20개"+enter;
            msg += "#L2# #i2046150# #z2046150#20개"+enter;
            msg += "#L3# #i2046077# #z2046077# 20개"+enter;
            cm.sendSimple(msg);
        }
    } else if(status ==3) {
        if (sel == 1) {
            선택지급2 = "2046076";
            var msg = "#fs11##b#e[" + 패키지명 + "] #k#n에서\r\n선택하신 #b보상#k이 맞습니까?#d" + enter + enter;
            msg += "#b#e#i"+ 선택지급1 +"# #z"+ 선택지급1 +"# 30개 \r\n";
            msg += "#b#e#i"+ 선택지급2 +"# #z"+ 선택지급2 +"# 20개 \r\n";
            msg += "#L1#네 맞습니다";
            msg += "#L99#아니요";
            cm.sendSimple(msg);
        } else if (sel == 2) {
            선택지급2 = "2046150";
            var msg = "#fs11##b#e[" + 패키지명 + "] #k#n에서\r\n선택하신 #b보상#k이 맞습니까?#d" + enter + enter;
            msg += "#b#e#i"+ 선택지급1 +"# #z"+ 선택지급1 +"# 30개 \r\n";
            msg += "#b#e#i"+ 선택지급2 +"# #z"+ 선택지급2 +"# 20개 \r\n";
            msg += "#L1#네 맞습니다";
            msg += "#L99#아니요";
            cm.sendSimple(msg);
        } else if (sel == 3) {
            선택지급2 = "2046077";
            var msg = "#fs11##b#e[" + 패키지명 + "] #k#n에서\r\n선택하신 #b보상#k이 맞습니까?#d" + enter + enter;
            msg += "#b#e#i"+ 선택지급1 +"# #z"+ 선택지급1 +"# 30개 \r\n";
            msg += "#b#e#i"+ 선택지급2 +"# #z"+ 선택지급2 +"# 20개 \r\n";
            msg += "#L1#네 맞습니다";
            msg += "#L99#아니요";
            cm.sendSimple(msg);
        }
    } else if(status ==4) {
        if (sel == 1) {
            var msg = "#fs11##b#e감사합니다~!\r\n<#r나의 선택#b>" + enter + enter;
            msg += "#b#e#i"+ 선택지급1 +"# #z"+ 선택지급1 +"# 30개 \r\n";
            msg += "#b#e#i"+ 선택지급2 +"# #z"+ 선택지급2 +"# 20개 \r\n";
            for (i = 0; i < itemlist.length; i ++) {
            cm.gainItem(itemlist[i]['itemid'],itemlist[i]['qty']);
            }
            cm.gainItem(패키지코드, -1);
            cm.getPlayer().gainDonationPoint(후원포인트);
            cm.gainItem(선택지급1, 30);
            cm.gainItem(선택지급2, 20);
            cm.dispose();
            cm.sendOkS(msg, 2);
            
            
            // 로그작성
            Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[상시패키지]/[NEW]" + 패키지명 + ".log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n사용한 아이템 : " + 패키지명 + " (" + 패키지코드 + ")" + "\r\n\r\n", true);
        } else if (sel == 99) {
        cm.dispose();
        cm.sendOk("#fs11#네");
        }
    }
    
}