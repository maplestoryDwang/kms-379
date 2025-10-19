별파 = "#fUI/GuildMark.img/Mark/Pattern/00004001/11#"
별노 = "#fUI/GuildMark.img/Mark/Pattern/00004001/3#"
별흰 = "#fUI/GuildMark.img/Mark/Pattern/00004001/15#"
별갈 = "#fUI/GuildMark.img/Mark/Pattern/00004001/5#"
별빨 = "#fUI/GuildMark.img/Mark/Pattern/00004001/1#"
별검 = "#fUI/GuildMark.img/Mark/Pattern/00004001/16#"
별보 = "#fUI/GuildMark.img/Mark/Pattern/00004001/13#"
별 = "#fUI/FarmUI.img/objectStatus/star/whole#";
S = "#fUI/CashShop.img/CSEffect/today/0#"
색 = "#fc0xFF6600CC#"
끝 = "#k"

var a = "";
var b = "";

var status = -1;

var item = [
    [4033213, 1, 100000, "", ""], // 빛나는보석
    [5060048, 1, 300000, "", ""], // 레드애플
    [4036660, 1, 900000, "", ""], // 돌림판
    //[5068306, 1, 1500000, "", ""], // 무지개베리
    [5062005, 1, 300000, "", ""], // 어미큐
    [5062503, 1, 300000, "", ""], // 화에큐
    [2631527, 10, 500000, "10개", ""], // 경코젬
    [4009547, 500, 500000, "500개", ""], //솔에르다조각
    [2046076, 1, 500000, "", ""], // 한공
    [2046077, 1, 500000, "", ""], // 한마
    [2046150, 1, 500000, "", ""], // 두공
    [2046340, 1, 500000, "", ""], // 악공
    [2046341, 1, 500000, "", ""], // 악마
    [2048047, 1, 500000, "", ""], // 펫장공
    [2048048, 1, 500000, "", ""], // 펫장마
    [2046251, 1, 500000, "", ""], // 방줌
    [2630127, 1, 800000, "", ""], // 1기펫
    [2439942, 1, 800000, "", ""], // 2기펫
    [2439943, 1, 800000, "", ""], // 3기펫
    [2439944, 1, 800000, "", ""], // 4기펫
    [2439932, 1, 800000, "", ""], // 5기펫
    [2430218, 1, 1000000, "", ""], // 슈성비
    [2538000, 1, 3000000, "", ""], // 추옵전승
    [4034803, 1, 10000000, "", ""], // 닉변
    [2633590, 1, 500000, "", ""], // 아케인만렙
    [2633591, 1, 1000000, "", ""]//어센틱만렙

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
        var 홍보포인트 = comma(cm.getPlayer().getHPoint());
                
        chat = 별 + "#fs11# 현재 #h0#님의 홍보포인트 : #r" + 홍보포인트 + "P#k\r\n";
        for (i = 0; i < item.length; i++) {
            price = item[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
            chat += 색 + "#L" + i + "##i" + item[i][0] + "# #z" + item[i][0] + "# #r#e" + item[i][3] + "#l#n\r\n              #fc0xFF000000#가격 : #e#r" + price + "P#b#n " + item[i][4] + "#k#n\r\n";
        }
        cm.sendSimple(chat);

    } else if (status == 1) {
        a = selection;
        
        if (selection == 1000) {
            cm.dispose();
        } else {
                var suk1 = Math.floor((cm.getPlayer().getHPoint() / item[selection][2]));
                stigmacoin = Math.min(suk1);
                cm.sendGetNumber("\r\n#fn나눔고딕#정말 #i " + item[selection][0] + "##z" + item[selection][0] + " ##fc0xFF7401DF#" +  item[selection][1] + "#k 개로 구매하시겠습니까? \r\n#Cgray#(현재 구매 가능한 #z" +  item[selection][0] + "# 갯수 : " + stigmacoin + "개)", 1, 1, stigmacoin);
        }
    } else if (status == 2) {
        b = selection;
        cost = b;
        
        if (selection == 1000) {
            cm.dispose();
        }

        if (!cm.canHold(item[a][0], item[a][1] * cost)) {
            cm.sendOk("#fs11#인벤토리 공간이 부족합니다.");
            cm.dispose();
            return;
        }

        if (cm.getPlayer().getHPoint() >= item[a][2] * cost) {
            cm.gainItem(item[a][0], item[a][1] * cost);
            cm.getPlayer().gainHPoint(-item[a][2] * cost);
            cm.sendOkS(색 + "감사합니다~" + 별,2);
            cm.dispose();
        } else {
            cm.sendOk("홍보 포인트가 부족합니다.");
            cm.dispose();
        }
    }
}

function comma(number) {
    return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}