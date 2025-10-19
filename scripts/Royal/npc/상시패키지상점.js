보라 = "#fMap/MapHelper.img/weather/starPlanet/7#";
파랑 = "#fMap/MapHelper.img/weather/starPlanet/8#";
별파 = "#fUI/GuildMark.img/Mark/Pattern/00004001/11#"
별노 = "#fUI/GuildMark.img/Mark/Pattern/00004001/3#"
별흰 = "#fUI/GuildMark.img/Mark/Pattern/00004001/15#"
별갈 = "#fUI/GuildMark.img/Mark/Pattern/00004001/5#"
별빨 = "#fUI/GuildMark.img/Mark/Pattern/00004001/1#"
별검 = "#fUI/GuildMark.img/Mark/Pattern/00004001/16#"
별보 = "#fUI/GuildMark.img/Mark/Pattern/00004001/13#"
별 = "#fUI/FarmUI.img/objectStatus/star/whole#"
S = "#fUI/CashShop.img/CSEffect/today/0#"
보상 = "#fUI/UIWindow2.img/Quest/quest_info/summary_icon/reward#"
획득 = "#fUI/UIWindow2.img/QuestIcon/4/0#"
색 = "#fc0xFF6600CC#"
검은색 = "#fc0xFF000000#"
엔터 = "\r\n"
엔터2 = "\r\n\r\n"

var a = "";
var b = "";

var status = -1;

var item = [
    [2430017, 1, 5000, "", "1만 포인트"],
    [2430017, 3, 13500, "", "3만 포인트"],
    [2430017, 6, 27000, "", "6만 포인트"],
    [2430017, 10, 45000, "", "10만 포인트"],
    [2430017, 20, 90000, "", "20만 포인트"],
    [2430017, 40, 180000, "", "40만 포인트"],
    [2430017, 60, 260000, "", "60만 포인트"],
    [2430017, 100, 430000, "", "100만 포인트"],
    //[2430017, 200, 850000, "#r[HOT]", "200만 포인트"]
];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
    }
    if (status == 0) {
        var 보유캐시 = comma(cm.getPlayer().getCashPoint());
        var 현재등급 = cm.getPlayer().getHgrades();

        chat = 별 + "#fs11# 현재 #h0#님의 캐시 : #r" + 보유캐시 + "C#k\r\n";
        chat += 별 + " 현재 #h0#님의 등급 : #fc0xFFFF3366#" + 현재등급 + "\r\n";
        chat += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
        chat += 색 + "#e<패키지>#k#n" + 엔터;
        for (i = 0; i < item.length; i++) {
            price = item[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
            chat += 검은색 + "#L" + i + "##i" + item[i][0] + "# #z" + item[i][0] + "# #r#e" + item[i][3] + "#l#n\r\n              #fc0xFF000000#가격 : #e#r" + price + "C#b#n " + item[i][4] + "#k#n\r\n";
        }
        cm.sendSimple(chat);

    } else if (status == 1) {
        a = selection;

        if (selection == -1) {
            cm.sendOk("비정상적인 구매 시도");
            cm.dispose();
        } else {
            var suk1 = Math.floor((cm.getPlayer().getCashPoint() / item[selection][2]));
            stigmacoin = Math.min(suk1);
            cm.sendGetNumber("#fs11#" + 검은색 + "#i " + item[selection][0] + "# #z" + item[selection][0] + "# 를 몇개 구매 하시겠습니까? \r\n#Cgray#(현재 구매 가능한 #z" + item[selection][0] + "# 갯수 : " + stigmacoin + "개)\r\n\r\n#r※ 입력시 즉시 구매 됩니다 [철회불가]", 1, 1, stigmacoin);
        }
    } else if (status == 2) {
        b = selection;
        cost = b;

        if (selection > 100) {
            cm.sendOk("100개 까지만 구매가 가능합니다.");
            cm.dispose();
            return;
        }

        if (!cm.canHold(item[a][0], item[a][1] * cost)) {
            cm.sendOk("#fs11#인벤토리 공간이 부족합니다.");
            cm.dispose();
            return;
        }

        if (cm.getPlayer().getCashPoint() >= item[a][2] * cost) {
            cm.gainItem(item[a][0], item[a][1] * cost);
            cm.getPlayer().gainCashPoint(-item[a][2] * cost);
            Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP캐시상점].log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n구매한 아이템 : " + cm.getItemName(item[a][0]) + "[" + item[a][0] + "] (" + cost + "개)\r\n사용 캐시 : " + -item[a][2] * cost + "\r\n보유 캐시 : " + cm.getPlayer().getCashPoint() + "\r\n\r\n", true);
            cm.sendOkS("#fs11#" + 색 + "구매가 완료되었습니다." + 별, 2);
            cm.dispose();
        } else {
            cm.sendOk("#fs11#캐시가 부족합니다.");
            cm.dispose();
        }
    }
}

function comma(number) {
    return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}