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
핑크색 = "#fc0xFFFF3366#"
분홍색 = "#fc0xFFF781D8#"
엔터 = "\r\n"
엔터2 = "\r\n\r\n"
enter = "\r\n";

importPackage(java.lang);
importPackage(Packages.server);

var a = "";
var b = "";

var status = -1;

var item = [
    ["#g[EVENT]#fc0xFF000000# 찬빛결 3k 패키지", 4031227, 3000, 50000, "#r[HOT]", "판매기간 : ~2025.07.13"],
    ["#g[EVENT]#fc0xFF000000# 네오코어 4k 패키지", 4310308, 4000, 50000, "#r[HOT]", "판매기간 : ~2025.07.13"],
    ["#g[EVENT]#fc0xFF000000# 기억의조각 4k 패키지", 4033172, 4000, 50000, "#b[NEW]", "판매기간 : ~2025.07.13"],
    ["#g[EVENT]#fc0xFF000000# 반마력석 7k 패키지", 4009005, 7000, 50000, "#b[NEW]", "판매기간 : ~2025.07.13"],
    ["#g[EVENT]#fc0xFF000000# 승급주화 8k 패키지", 4310266, 8000, 50000, "#b[NEW]", "판매기간 : ~2025.07.13"],
    ["#g[EVENT]#fc0xFF000000# 헌트코인 15k 패키지", 4310237, 15000, 50000, "#b[NEW]", "판매기간 : ~2025.07.13"],
    //["#g[EVENT]#fc0xFF000000# 레드애플 120개 패키지", 5060048, 120, 100000, "#r[HOT]", "판매기간 : ~2025.06.08"],
    //["#g[EVENT]#fc0xFF000000# 돌림판 40개 패키지", 4036660, 40, 100000, "#b[NEW]", "판매기간 : ~2025.06.08"],
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

        chat = 별 + "#fs11# 현재 #h0#님의 캐시 : #fc0xFFFF3366#" + 보유캐시 + "C#k\r\n";
        chat += 별 + " 현재 #h0#님의 등급 : #fc0xFFFF3366#" + 현재등급 + "\r\n";
        chat += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
        chat += 색 + "#e<이벤트패키지>#k#n" + 엔터;
        for (i = 0; i < item.length; i++) {
            // 이벤트패키지는 아래 양식 다름 조심
            price = item[i][3].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
            chat += 검은색 + "#L" + i + "##i" + item[i][1] + "# " + item[i][0] + " #r#e" + item[i][4] + "#l#n\r\n              #fc0xFF000000#가격 : #e#r" + price + "C#b#n " + item[i][5] + "#k#n\r\n";
        }
        cm.sendSimple(chat);
    } else if (status == 1) {
        a = selection;

        if (selection == -1) {
            cm.sendOk("#fs11#구매 가능한 아이템이 없습니다");
            cm.dispose();
        } else {
            var suk1 = Math.floor((cm.getPlayer().getCashPoint() / item[selection][3]));
            stigmacoin = Math.min(suk1);
            // 이벤트패키지는 아래 양식 다름 조심
            cm.sendGetNumber("#fs11#" + 검은색 + "#i " + item[selection][1] + "# " + item[selection][0] + " 를 몇개 구매 하시겠습니까? \r\n#Cgray#(현재 구매 가능한 " + item[selection][0] + " 갯수 : " + stigmacoin + "개)\r\n\r\n#r※ 입력시 즉시 구매 됩니다 [철회불가]", 1, 1, 100);
        }
    } else if (status == 2) {
        b = selection;
        cost = b;

        if (selection <= 0) {
            cm.dispose();
            return;
        }

        if (selection > 100) {
            cm.sendOk("#fs11#100개 까지만 구매가 가능합니다.");
            cm.dispose();
            return;
        }

        if (cm.getPlayer().getCashPoint() >= item[a][3] * cost) {
            var totalCount = item[a][2] * cost; // 총 지급 개수
            var maxStack = 32000; // 분할 기준 (안전하게 32000으로 설정)
            var givenCount = 0;

            while (totalCount > 0) {
                var give = Math.min(totalCount, maxStack);
                cm.gainItem(item[a][1], give);
                totalCount -= give;
                givenCount += give;
            }

            cm.getPlayer().gainCashPoint(-item[a][3] * cost);

            Packages.scripting.NPCConversationManager.writeLog(
                "TextLog/zenia/[MVP캐시상점].log",
                "\r\n계정 : " + cm.getClient().getAccountName() +
                " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() +
                "\r\n구매한 아이템 : " + cm.getItemName(item[a][1]) + " [" + item[a][1] + "] (" + cost + "개)\r\n" +
                "사용 캐시 : " + -item[a][3] * cost + "\r\n보유 캐시 : " + cm.getPlayer().getCashPoint() + "\r\n\r\n",
                true
            );

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