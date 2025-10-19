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
끝 = "#k"

var a = "";
var b = "";

var status = -1;

var item = [
    [4033213, 1, 1000, "", ""], // 빛나는보석
    [5060048, 1, 3000, "", ""], // 레드애플
    [4036660, 1, 9000, "", ""], // 돌림판
    [5062005, 1, 3000, "", ""], // 어미큐
    [5062503, 1, 3000, "", ""], // 화에큐
    [2631527, 10, 5000, "10개", ""], //경코젬 
    [4009547, 500, 5000, "500개", ""], //솔에르다조각
    [2046076, 1, 5000, "", ""], // 한공
    [2046077, 1, 5000, "", ""], // 한마
    [2046150, 1, 5000, "", ""], // 두공
    [2046340, 1, 5000, "", ""], // 악공
    [2046341, 1, 5000, "", ""], // 악마
    [2048047, 1, 5000, "", ""], // 펫장공
    [2048048, 1, 5000, "", ""], // 펫장마
    [2046251, 1, 5000, "", ""], // 방줌
    [2630127, 1, 8000, "", ""], // 1기펫
    [2439942, 1, 8000, "", ""], // 2기펫
    [2439943, 1, 8000, "", ""], // 3기펫
    [2439944, 1, 8000, "", ""], // 4기펫
    [2439932, 1, 8000, "", ""], // 5기펫
    [2430218, 1, 10000, "", ""], // 슈성비
    [2538000, 1, 30000, "", ""], // 추옵전승
    //[2430007, 1, 25000, "", ""], // 스코티
    //[2437781, 1, 100000, "", ""], // 투명세트
    //[2431394, 1, 150000, "", ""], // 링상자
    [4034803, 1, 100000, "", ""], // 닉변
    //[2350002, 1, 1000000, "", ""], // 1글자
    [2633590, 1, 20000, "", ""], // 아케인만렙
    [2633591, 1, 40000, "", ""]//어센틱만렙
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
        var 보유포인트 = comma(cm.getPlayer().getDonationPoint());
        var 현재등급 = cm.getPlayer().getHgrades();

        chat = 별 + "#fs11# 현재 #h0#님의 포인트 : #r" + 보유포인트 + "P#k\r\n";
        chat += 별 + " 현재 #h0#님의 등급 : #fc0xFFFF3366#" + 현재등급 + "\r\n";
        chat += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
        for (i = 0; i < item.length; i++) {
            price = item[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
            chat += 색 + "#L" + i + "##i" + item[i][0] + "# #z" + item[i][0] + "# #r#e" + item[i][3] + "#l#n\r\n              #fc0xFF000000#가격 : #e#r" + price + "P#b#n " + item[i][4] + "#k#n\r\n";
        }
        cm.sendSimple(chat);


    } else if (status == 1) {
        a = selection;

        if (selection == -1) {
            cm.sendOk("비정상 적인 구매 시도");
            cm.dispose();
        } else {
            var suk1 = Math.floor((cm.getPlayer().getDonationPoint() / item[selection][2]));
            stigmacoin = Math.min(suk1);
            cm.sendGetNumber("#fs11#" + 검은색 + "#i " + item[selection][0] + "# #z" + item[selection][0] + "# 를 몇개 구매 하시겠습니까? \r\n#Cgray#(현재 구매 가능한 #z" + item[selection][0] + "# 갯수 : " + stigmacoin + "개)\r\n\r\n#r※ 입력시 즉시 구매 됩니다 [철회불가]", 1, 1, stigmacoin);
        }
    } else if (status == 2) {
        b = selection;
        cost = b;

        if (selection > 1000) {
            cm.sendOk("1000개 까지만 구매가 가능합니다.");
            cm.dispose();
            return;
        }

        if (!cm.canHold(item[a][0], item[a][1] * cost)) {
            cm.sendOk("#fs11#인벤토리 공간이 부족합니다.");
            cm.dispose();
            return;
        }

        if (cm.getPlayer().getDonationPoint() >= item[a][2] * cost) {
            cm.gainItem(item[a][0], item[a][1] * cost);
            cm.getPlayer().gainDonationPoint(-item[a][2] * cost);
            Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP포인트상점].log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n구매한 아이템 : " + cm.getItemName(item[a][0]) + "[" + item[a][0] + "] (" + cost + "개)\r\n사용 포인트 : " + -item[a][2] * cost + "\r\n보유 포인트 : " + cm.getPlayer().getDonationPoint() + "\r\n\r\n", true);
            cm.sendOkS(색 + "구매가 완료되었습니다." + 별, 2);
            cm.dispose();
        } else {
            cm.sendOk("#fs11#포인트가 부족합니다.");
            cm.dispose();
        }
    }
}

function comma(number) {
    return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}