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
    [1143891, 1, 30000, 60000, 1],
    [1143892, 1, 50000, 100000, 2],
    [1143893, 1, 150000, 300000, 3],
    [1143894, 1, 300000, 600000, 4],
    [1143895, 1, 500000, 1000000, 5],
    [1143896, 1, 1000000, 2000000, 6],
    [1143897, 1, 2000000, 4000000, 7],
    [1143898, 1, 3000000, 6000000, 8],
    [1143899, 1, 4000000, 8000000, 9],
    [1143900, 1, 6000000, 12000000, 10],
    [1143901, 1, 8000000, 16000000, 11],
    [1143902, 1, 10000000, 20000000, 12],
    [1143903, 1, 13000000, 26000000, 13],
    [1143904, 1, 16000000, 32000000, 14],
    [1143905, 1, 20000000, 40000000, 15]
];

var item1 = [
    [1143892, 1, 20000, 40000, 2],
    [1143893, 1, 120000, 240000, 3],
    [1143894, 1, 270000, 540000, 4],
    [1143895, 1, 470000, 940000, 5],
    [1143896, 1, 970000, 1940000, 6],
    [1143897, 1, 1970000, 3940000, 7],
    [1143898, 1, 2970000, 5940000, 8],
    [1143899, 1, 3970000, 7940000, 9],
    [1143900, 1, 5970000, 11940000, 10],
    [1143901, 1, 7970000, 15940000, 11],
    [1143902, 1, 9970000, 19940000, 12],
    [1143903, 1, 12970000, 25940000, 13],
    [1143904, 1, 15970000, 31940000, 14],
    [1143905, 1, 19970000, 39940000, 15]
];

var item2 = [
    [1143893, 1, 100000, 200000, 3],
    [1143894, 1, 250000, 500000, 4],
    [1143895, 1, 450000, 900000, 5],
    [1143896, 1, 950000, 1900000, 6],
    [1143897, 1, 1950000, 3900000, 7],
    [1143898, 1, 2950000, 5900000, 8],
    [1143899, 1, 3950000, 7900000, 9],
    [1143900, 1, 5950000, 11900000, 10],
    [1143901, 1, 7950000, 15900000, 11],
    [1143902, 1, 9950000, 19900000, 12],
    [1143903, 1, 12950000, 25900000, 13],
    [1143904, 1, 15950000, 31900000, 14],
    [1143905, 1, 19950000, 39900000, 15]
];

var item3 = [
    [1143894, 1, 150000, 300000, 4],
    [1143895, 1, 350000, 700000, 5],
    [1143896, 1, 850000, 1700000, 6],
    [1143897, 1, 1850000, 3700000, 7],
    [1143898, 1, 2850000, 5700000, 8],
    [1143899, 1, 3850000, 7700000, 9],
    [1143900, 1, 5850000, 11700000, 10],
    [1143901, 1, 7850000, 15700000, 11],
    [1143902, 1, 9850000, 19700000, 12],
    [1143903, 1, 12850000, 25700000, 13],
    [1143904, 1, 15850000, 31700000, 14],
    [1143905, 1, 19850000, 39700000, 15]
];

var item4 = [
    [1143895, 1, 200000, 400000, 5],
    [1143896, 1, 700000, 1400000, 6],
    [1143897, 1, 1700000, 3400000, 7],
    [1143898, 1, 2700000, 5400000, 8],
    [1143899, 1, 3700000, 7400000, 9],
    [1143900, 1, 5700000, 11400000, 10],
    [1143901, 1, 7700000, 15400000, 11],
    [1143902, 1, 9700000, 19400000, 12],
    [1143903, 1, 12700000, 25400000, 13],
    [1143904, 1, 15700000, 31400000, 14],
    [1143905, 1, 19700000, 39400000, 15]
];

var item5 = [
    [1143896, 1, 500000, 1000000, 6],
    [1143897, 1, 1500000, 3000000, 7],
    [1143898, 1, 2500000, 5000000, 8],
    [1143899, 1, 3500000, 7000000, 9],
    [1143900, 1, 5500000, 11000000, 10],
    [1143901, 1, 7500000, 15000000, 11],
    [1143902, 1, 9500000, 19000000, 12],
    [1143903, 1, 12500000, 25000000, 13],
    [1143904, 1, 15500000, 31000000, 14],
    [1143905, 1, 19500000, 39000000, 15]
];

var item6 = [
    [1143897, 1, 1000000, 2000000, 7],
    [1143898, 1, 2000000, 4000000, 8],
    [1143899, 1, 3000000, 6000000, 9],
    [1143900, 1, 5000000, 10000000, 10],
    [1143901, 1, 7000000, 14000000, 11],
    [1143902, 1, 9000000, 18000000, 12],
    [1143903, 1, 12000000, 24000000, 13],
    [1143904, 1, 15000000, 30000000, 14],
    [1143905, 1, 19000000, 38000000, 15]
];

var item7 = [
    [1143898, 1, 1000000, 2000000, 8],
    [1143899, 1, 2000000, 4000000, 9],
    [1143900, 1, 4000000, 8000000, 10],
    [1143901, 1, 6000000, 12000000, 11],
    [1143902, 1, 8000000, 16000000, 12],
    [1143903, 1, 11000000, 22000000, 13],
    [1143904, 1, 14000000, 28000000, 14],
    [1143905, 1, 18000000, 36000000, 15]
];

var item8 = [
    [1143899, 1, 1000000, 2000000, 9],
    [1143900, 1, 3000000, 6000000, 10],
    [1143901, 1, 5000000, 10000000, 11],
    [1143902, 1, 7000000, 14000000, 12],
    [1143903, 1, 10000000, 20000000, 13],
    [1143904, 1, 13000000, 26000000, 14],
    [1143905, 1, 17000000, 34000000, 15]
];

var item9 = [
    [1143900, 1, 2000000, 4000000, 10],
    [1143901, 1, 4000000, 8000000, 11],
    [1143902, 1, 6000000, 12000000, 12],
    [1143903, 1, 9000000, 18000000, 13],
    [1143904, 1, 12000000, 24000000, 14],
    [1143905, 1, 16000000, 32000000, 15]
];

var item10 = [
    [1143901, 1, 2000000, 4000000, 11],
    [1143902, 1, 4000000, 8000000, 12],
    [1143903, 1, 7000000, 14000000, 13],
    [1143904, 1, 10000000, 20000000, 14],
    [1143905, 1, 14000000, 28000000, 15]
];

var item11 = [
    [1143902, 1, 2000000, 4000000, 12],
    [1143903, 1, 5000000, 10000000, 13],
    [1143904, 1, 8000000, 16000000, 14],
    [1143905, 1, 12000000, 24000000, 15]
];

var item12 = [
    [1143903, 1, 3000000, 6000000, 13],
    [1143904, 1, 6000000, 12000000, 14],
    [1143905, 1, 10000000, 20000000, 15]
];

var item13 = [
    [1143904, 1, 3000000, 6000000, 14],
    [1143905, 1, 7000000, 14000000, 15]
];

var item14 = [
    [1143905, 1, 4000000, 8000000, 15]
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

        var chat = "#fs11#";
        chat += 별 + " #fc0xFF000000#현재 #h0#님의 캐시 : #r" + 보유캐시 + "C#k\r\n";
        chat += 별 + " #fc0xFF000000#현재 #h0#님의 등급 : #fc0xFFFF3366#" + 현재등급 + "\r\n";
        chat += 별 + " #r원하는 등급 구매시 이전 등급까지 보상 수령 가능합니다.\r\n";
        chat += 별 + " #r등급 구매시 가격의 2배만큼 포인트를 지급받습니다.\r\n\r\n";
        chat += 색 + "#e<등급구매>#k#n" + 엔터;

        if (cm.getPlayer().getHgrade() >= 15) {
            cm.dispose();
            cm.sendOk("#fs11##fc0xFF6600CC#이미 최고등급 입니다.");
        } else if (cm.getPlayer().getHgrade() == 14) {
            for (i = 0; i < item14.length; i++) {
                price = item14[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
                chat += 검은색 + "#L" + i + "##i" + item14[i][0] + "# #z" + item14[i][0] + "# #l\r\n              #fc0xFF000000#가격 : #e#r" + price + "C#k#n\r\n";
            }
        } else if (cm.getPlayer().getHgrade() == 13) {
            for (i = 0; i < item13.length; i++) {
                price = item13[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
                chat += 검은색 + "#L" + i + "##i" + item13[i][0] + "# #z" + item13[i][0] + "# #l\r\n              #fc0xFF000000#가격 : #e#r" + price + "C#k#n\r\n";
            }
        } else if (cm.getPlayer().getHgrade() == 12) {
            for (i = 0; i < item12.length; i++) {
                price = item12[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
                chat += 검은색 + "#L" + i + "##i" + item12[i][0] + "# #z" + item12[i][0] + "# #l\r\n              #fc0xFF000000#가격 : #e#r" + price + "C#k#n\r\n";
            }
        } else if (cm.getPlayer().getHgrade() == 11) {
            for (i = 0; i < item11.length; i++) {
                price = item11[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
                chat += 검은색 + "#L" + i + "##i" + item11[i][0] + "# #z" + item11[i][0] + "# #l\r\n              #fc0xFF000000#가격 : #e#r" + price + "C#k#n\r\n";
            }
        } else if (cm.getPlayer().getHgrade() == 10) {
            for (i = 0; i < item10.length; i++) {
                price = item10[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
                chat += 검은색 + "#L" + i + "##i" + item10[i][0] + "# #z" + item10[i][0] + "# #l\r\n              #fc0xFF000000#가격 : #e#r" + price + "C#k#n\r\n";
            }
        } else if (cm.getPlayer().getHgrade() == 9) {
            for (i = 0; i < item9.length; i++) {
                price = item9[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
                chat += 검은색 + "#L" + i + "##i" + item9[i][0] + "# #z" + item9[i][0] + "# #l\r\n              #fc0xFF000000#가격 : #e#r" + price + "C#k#n\r\n";
            }
        } else if (cm.getPlayer().getHgrade() == 8) {
            for (i = 0; i < item8.length; i++) {
                price = item8[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
                chat += 검은색 + "#L" + i + "##i" + item8[i][0] + "# #z" + item8[i][0] + "# #l\r\n              #fc0xFF000000#가격 : #e#r" + price + "C#k#n\r\n";
            }
        } else if (cm.getPlayer().getHgrade() == 7) {
            for (i = 0; i < item7.length; i++) {
                price = item7[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
                chat += 검은색 + "#L" + i + "##i" + item7[i][0] + "# #z" + item7[i][0] + "# #l\r\n              #fc0xFF000000#가격 : #e#r" + price + "C#k#n\r\n";
            }
        } else if (cm.getPlayer().getHgrade() == 6) {
            for (i = 0; i < item6.length; i++) {
                price = item6[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
                chat += 검은색 + "#L" + i + "##i" + item6[i][0] + "# #z" + item6[i][0] + "# #l\r\n              #fc0xFF000000#가격 : #e#r" + price + "C#k#n\r\n";
            }
        } else if (cm.getPlayer().getHgrade() == 5) {
            for (i = 0; i < item5.length; i++) {
                price = item5[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
                chat += 검은색 + "#L" + i + "##i" + item5[i][0] + "# #z" + item5[i][0] + "# #l\r\n              #fc0xFF000000#가격 : #e#r" + price + "C#k#n\r\n";
            }
        } else if (cm.getPlayer().getHgrade() == 4) {
            for (i = 0; i < item4.length; i++) {
                price = item4[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
                chat += 검은색 + "#L" + i + "##i" + item4[i][0] + "# #z" + item4[i][0] + "# #l\r\n              #fc0xFF000000#가격 : #e#r" + price + "C#k#n\r\n";
            }
        } else if (cm.getPlayer().getHgrade() == 3) {
            for (i = 0; i < item3.length; i++) {
                price = item3[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
                chat += 검은색 + "#L" + i + "##i" + item3[i][0] + "# #z" + item3[i][0] + "# #l\r\n              #fc0xFF000000#가격 : #e#r" + price + "C#k#n\r\n";
            }
        } else if (cm.getPlayer().getHgrade() == 2) {
            for (i = 0; i < item2.length; i++) {
                price = item2[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
                chat += 검은색 + "#L" + i + "##i" + item2[i][0] + "# #z" + item2[i][0] + "# #l\r\n              #fc0xFF000000#가격 : #e#r" + price + "C#k#n\r\n";
            }
        } else if (cm.getPlayer().getHgrade() == 1) {
            for (i = 0; i < item1.length; i++) {
                price = item1[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
                chat += 검은색 + "#L" + i + "##i" + item1[i][0] + "# #z" + item1[i][0] + "# #l\r\n              #fc0xFF000000#가격 : #e#r" + price + "C#k#n\r\n";
            }
        } else {
            for (i = 0; i < item.length; i++) {
                price = item[i][2].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
                chat += 검은색 + "#L" + i + "##i" + item[i][0] + "# #z" + item[i][0] + "# #l\r\n              #fc0xFF000000#가격 : #e#r" + price + "C#k#n\r\n";
            }
        }
        cm.sendSimple(chat);

    } else if (status == 1) {
        a = selection;

        if (selection == -1) {
            cm.sendOk("비정상적인 구매 시도");
            cm.dispose();
        } else {
            if (cm.getPlayer().getHgrade() == 14) {
                cm.sendYesNo("#fs11#" + 검은색 + "정말 #i " + item14[selection][0] + "# #z" + item14[selection][0] + "# 등급을 구매 하시겠습니까? \r\n\r\n#r※ 구매 즉시 등급이 변경 됩니다");
            } else if (cm.getPlayer().getHgrade() == 13) {
                cm.sendYesNo("#fs11#" + 검은색 + "정말 #i " + item13[selection][0] + "# #z" + item13[selection][0] + "# 등급을 구매 하시겠습니까? \r\n\r\n#r※ 구매 즉시 등급이 변경 됩니다");
            } else if (cm.getPlayer().getHgrade() == 12) {
                cm.sendYesNo("#fs11#" + 검은색 + "정말 #i " + item12[selection][0] + "# #z" + item12[selection][0] + "# 등급을 구매 하시겠습니까? \r\n\r\n#r※ 구매 즉시 등급이 변경 됩니다");
            } else if (cm.getPlayer().getHgrade() == 11) {
                cm.sendYesNo("#fs11#" + 검은색 + "정말 #i " + item11[selection][0] + "# #z" + item11[selection][0] + "# 등급을 구매 하시겠습니까? \r\n\r\n#r※ 구매 즉시 등급이 변경 됩니다");
            } else if (cm.getPlayer().getHgrade() == 10) {
                cm.sendYesNo("#fs11#" + 검은색 + "정말 #i " + item10[selection][0] + "# #z" + item10[selection][0] + "# 등급을 구매 하시겠습니까? \r\n\r\n#r※ 구매 즉시 등급이 변경 됩니다");
            } else if (cm.getPlayer().getHgrade() == 9) {
                cm.sendYesNo("#fs11#" + 검은색 + "정말 #i " + item9[selection][0] + "# #z" + item9[selection][0] + "# 등급을 구매 하시겠습니까? \r\n\r\n#r※ 구매 즉시 등급이 변경 됩니다");
            } else if (cm.getPlayer().getHgrade() == 8) {
                cm.sendYesNo("#fs11#" + 검은색 + "정말 #i " + item8[selection][0] + "# #z" + item8[selection][0] + "# 등급을 구매 하시겠습니까? \r\n\r\n#r※ 구매 즉시 등급이 변경 됩니다");
            } else if (cm.getPlayer().getHgrade() == 7) {
                cm.sendYesNo("#fs11#" + 검은색 + "정말 #i " + item7[selection][0] + "# #z" + item7[selection][0] + "# 등급을 구매 하시겠습니까? \r\n\r\n#r※ 구매 즉시 등급이 변경 됩니다");
            } else if (cm.getPlayer().getHgrade() == 6) {
                cm.sendYesNo("#fs11#" + 검은색 + "정말 #i " + item6[selection][0] + "# #z" + item6[selection][0] + "# 등급을 구매 하시겠습니까? \r\n\r\n#r※ 구매 즉시 등급이 변경 됩니다");
            } else if (cm.getPlayer().getHgrade() == 5) {
                cm.sendYesNo("#fs11#" + 검은색 + "정말 #i " + item5[selection][0] + "# #z" + item5[selection][0] + "# 등급을 구매 하시겠습니까? \r\n\r\n#r※ 구매 즉시 등급이 변경 됩니다");
            } else if (cm.getPlayer().getHgrade() == 4) {
                cm.sendYesNo("#fs11#" + 검은색 + "정말 #i " + item4[selection][0] + "# #z" + item4[selection][0] + "# 등급을 구매 하시겠습니까? \r\n\r\n#r※ 구매 즉시 등급이 변경 됩니다");
            } else if (cm.getPlayer().getHgrade() == 3) {
                cm.sendYesNo("#fs11#" + 검은색 + "정말 #i " + item3[selection][0] + "# #z" + item3[selection][0] + "# 등급을 구매 하시겠습니까? \r\n\r\n#r※ 구매 즉시 등급이 변경 됩니다");
            } else if (cm.getPlayer().getHgrade() == 2) {
                cm.sendYesNo("#fs11#" + 검은색 + "정말 #i " + item2[selection][0] + "# #z" + item2[selection][0] + "# 등급을 구매 하시겠습니까? \r\n\r\n#r※ 구매 즉시 등급이 변경 됩니다");
            } else if (cm.getPlayer().getHgrade() == 1) {
                cm.sendYesNo("#fs11#" + 검은색 + "정말 #i " + item1[selection][0] + "# #z" + item1[selection][0] + "# 등급을 구매 하시겠습니까? \r\n\r\n#r※ 구매 즉시 등급이 변경 됩니다");
            } else {
                cm.sendYesNo("#fs11#" + 검은색 + "정말 #i " + item[selection][0] + "# #z" + item[selection][0] + "# 등급을 구매 하시겠습니까? \r\n\r\n#r※ 구매 즉시 등급이 변경 됩니다");
            }
        }
    } else if (status == 2) {
        b = selection;
        cost = 1; // 등급구매는 무조건 1개니깐 1 처리

        if (selection > 1) {
            cm.sendOk("1개 까지만 구매가 가능합니다.");
            cm.dispose();
        } else {
            if (cm.getPlayer().getHgrade() == 14) {
                if (cm.getPlayer().getCashPoint() >= item14[a][2] * cost) {
                    cm.getPlayer().gainCashPoint(-item14[a][2] * cost);
                    cm.getPlayer().gainDonationPoint(item14[a][3]);
                    Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP등급]/[MVP]등급구매.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n이전등급 : " + cm.getPlayer().getHgrades() + "\r\n달성등급 : " + cm.getItemName(item14[a][0]) + "\r\n사용 캐시 : " + -item14[a][2] + "\r\n보유 캐시 : " + cm.getPlayer().getCashPoint() + "\r\n\r\n", true);
                    cm.getPlayer().setHgrade(item14[a][4]);
                    cm.getPlayer().giveDonatorBuff();
                    cm.sendOk("#fs11#MVP 등급이 부여되었습니다\r\n달성,주간 보상은 후원NPC 를 이용해주세요");
                    cm.dispose();
                } else {
                    cm.sendOk("#fs11#캐시가 부족합니다.");
                    cm.dispose();
                }
            } else if (cm.getPlayer().getHgrade() == 13) {
                if (cm.getPlayer().getCashPoint() >= item13[a][2] * cost) {
                    cm.getPlayer().gainCashPoint(-item13[a][2] * cost);
                    cm.getPlayer().gainDonationPoint(item13[a][3]);
                    Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP등급]/[MVP]등급구매.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n이전등급 : " + cm.getPlayer().getHgrades() + "\r\n달성등급 : " + cm.getItemName(item13[a][0]) + "\r\n사용 캐시 : " + -item13[a][2] + "\r\n보유 캐시 : " + cm.getPlayer().getCashPoint() + "\r\n\r\n", true);
                    cm.getPlayer().setHgrade(item13[a][4]);
                    cm.getPlayer().giveDonatorBuff();
                    cm.sendOk("#fs11#MVP 등급이 부여되었습니다\r\n달성,주간 보상은 후원NPC 를 이용해주세요");
                    cm.dispose();
                } else {
                    cm.sendOk("#fs11#캐시가 부족합니다.");
                    cm.dispose();
                }
            } else if (cm.getPlayer().getHgrade() == 12) {
                if (cm.getPlayer().getCashPoint() >= item12[a][2] * cost) {
                    cm.getPlayer().gainCashPoint(-item12[a][2] * cost);
                    cm.getPlayer().gainDonationPoint(item12[a][3]);
                    Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP등급]/[MVP]등급구매.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n이전등급 : " + cm.getPlayer().getHgrades() + "\r\n달성등급 : " + cm.getItemName(item12[a][0]) + "\r\n사용 캐시 : " + -item12[a][2] + "\r\n보유 캐시 : " + cm.getPlayer().getCashPoint() + "\r\n\r\n", true);
                    cm.getPlayer().setHgrade(item12[a][4]);
                    cm.getPlayer().giveDonatorBuff();
                    cm.sendOk("#fs11#MVP 등급이 부여되었습니다\r\n달성,주간 보상은 후원NPC 를 이용해주세요");
                    cm.dispose();
                } else {
                    cm.sendOk("#fs11#캐시가 부족합니다.");
                    cm.dispose();
                }
            } else if (cm.getPlayer().getHgrade() == 11) {
                if (cm.getPlayer().getCashPoint() >= item11[a][2] * cost) {
                    cm.getPlayer().gainCashPoint(-item11[a][2] * cost);
                    cm.getPlayer().gainDonationPoint(item11[a][3]);
                    Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP등급]/[MVP]등급구매.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n이전등급 : " + cm.getPlayer().getHgrades() + "\r\n달성등급 : " + cm.getItemName(item11[a][0]) + "\r\n사용 캐시 : " + -item11[a][2] + "\r\n보유 캐시 : " + cm.getPlayer().getCashPoint() + "\r\n\r\n", true);
                    cm.getPlayer().setHgrade(item11[a][4]);
                    cm.getPlayer().giveDonatorBuff();
                    cm.sendOk("#fs11#MVP 등급이 부여되었습니다\r\n달성,주간 보상은 후원NPC 를 이용해주세요");
                    cm.dispose();
                } else {
                    cm.sendOk("#fs11#캐시가 부족합니다.");
                    cm.dispose();
                }
            } else if (cm.getPlayer().getHgrade() == 10) {
                if (cm.getPlayer().getCashPoint() >= item10[a][2] * cost) {
                    cm.getPlayer().gainCashPoint(-item10[a][2] * cost);
                    cm.getPlayer().gainDonationPoint(item10[a][3]);
                    Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP등급]/[MVP]등급구매.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n이전등급 : " + cm.getPlayer().getHgrades() + "\r\n달성등급 : " + cm.getItemName(item10[a][0]) + "\r\n사용 캐시 : " + -item10[a][2] + "\r\n보유 캐시 : " + cm.getPlayer().getCashPoint() + "\r\n\r\n", true);
                    cm.getPlayer().setHgrade(item10[a][4]);
                    cm.getPlayer().giveDonatorBuff();
                    cm.sendOk("#fs11#MVP 등급이 부여되었습니다\r\n달성,주간 보상은 후원NPC 를 이용해주세요");
                    cm.dispose();
                } else {
                    cm.sendOk("#fs11#캐시가 부족합니다.");
                    cm.dispose();
                }
            } else if (cm.getPlayer().getHgrade() == 9) {
                if (cm.getPlayer().getCashPoint() >= item9[a][2] * cost) {
                    cm.getPlayer().gainCashPoint(-item9[a][2] * cost);
                    cm.getPlayer().gainDonationPoint(item9[a][3]);
                    Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP등급]/[MVP]등급구매.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n이전등급 : " + cm.getPlayer().getHgrades() + "\r\n달성등급 : " + cm.getItemName(item9[a][0]) + "\r\n사용 캐시 : " + -item9[a][2] + "\r\n보유 캐시 : " + cm.getPlayer().getCashPoint() + "\r\n\r\n", true);
                    cm.getPlayer().setHgrade(item9[a][4]);
                    cm.getPlayer().giveDonatorBuff();
                    cm.sendOk("#fs11#MVP 등급이 부여되었습니다\r\n달성,주간 보상은 후원NPC 를 이용해주세요");
                    cm.dispose();
                } else {
                    cm.sendOk("#fs11#캐시가 부족합니다.");
                    cm.dispose();
                }
            } else if (cm.getPlayer().getHgrade() == 8) {
                if (cm.getPlayer().getCashPoint() >= item8[a][2] * cost) {
                    cm.getPlayer().gainCashPoint(-item8[a][2] * cost);
                    cm.getPlayer().gainDonationPoint(item8[a][3]);
                    Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP등급]/[MVP]등급구매.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n이전등급 : " + cm.getPlayer().getHgrades() + "\r\n달성등급 : " + cm.getItemName(item8[a][0]) + "\r\n사용 캐시 : " + -item8[a][2] + "\r\n보유 캐시 : " + cm.getPlayer().getCashPoint() + "\r\n\r\n", true);
                    cm.getPlayer().setHgrade(item8[a][4]);
                    cm.getPlayer().giveDonatorBuff();
                    cm.sendOk("#fs11#MVP 등급이 부여되었습니다\r\n달성,주간 보상은 후원NPC 를 이용해주세요");
                    cm.dispose();
                } else {
                    cm.sendOk("#fs11#캐시가 부족합니다.");
                    cm.dispose();
                }
            } else if (cm.getPlayer().getHgrade() == 7) {
                if (cm.getPlayer().getCashPoint() >= item7[a][2] * cost) {
                    cm.getPlayer().gainCashPoint(-item7[a][2] * cost);
                    cm.getPlayer().gainDonationPoint(item7[a][3]);
                    Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP등급]/[MVP]등급구매.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n이전등급 : " + cm.getPlayer().getHgrades() + "\r\n달성등급 : " + cm.getItemName(item7[a][0]) + "\r\n사용 캐시 : " + -item7[a][2] + "\r\n보유 캐시 : " + cm.getPlayer().getCashPoint() + "\r\n\r\n", true);
                    cm.getPlayer().setHgrade(item7[a][4]);
                    cm.getPlayer().giveDonatorBuff();
                    cm.sendOk("#fs11#MVP 등급이 부여되었습니다\r\n달성,주간 보상은 후원NPC 를 이용해주세요");
                    cm.dispose();
                } else {
                    cm.sendOk("#fs11#캐시가 부족합니다.");
                    cm.dispose();
                }
            } else if (cm.getPlayer().getHgrade() == 6) {
                if (cm.getPlayer().getCashPoint() >= item6[a][2] * cost) {
                    cm.getPlayer().gainCashPoint(-item6[a][2] * cost);
                    cm.getPlayer().gainDonationPoint(item6[a][3]);
                    Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP등급]/[MVP]등급구매.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n이전등급 : " + cm.getPlayer().getHgrades() + "\r\n달성등급 : " + cm.getItemName(item6[a][0]) + "\r\n사용 캐시 : " + -item6[a][2] + "\r\n보유 캐시 : " + cm.getPlayer().getCashPoint() + "\r\n\r\n", true);
                    cm.getPlayer().setHgrade(item6[a][4]);
                    cm.getPlayer().giveDonatorBuff();
                    cm.sendOk("#fs11#MVP 등급이 부여되었습니다\r\n달성,주간 보상은 후원NPC 를 이용해주세요");
                    cm.dispose();
                } else {
                    cm.sendOk("#fs11#캐시가 부족합니다.");
                    cm.dispose();
                }
            } else if (cm.getPlayer().getHgrade() == 5) {
                if (cm.getPlayer().getCashPoint() >= item5[a][2] * cost) {
                    cm.getPlayer().gainCashPoint(-item5[a][2] * cost);
                    cm.getPlayer().gainDonationPoint(item5[a][3]);
                    Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP등급]/[MVP]등급구매.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n이전등급 : " + cm.getPlayer().getHgrades() + "\r\n달성등급 : " + cm.getItemName(item5[a][0]) + "\r\n사용 캐시 : " + -item5[a][2] + "\r\n보유 캐시 : " + cm.getPlayer().getCashPoint() + "\r\n\r\n", true);
                    cm.getPlayer().setHgrade(item5[a][4]);
                    cm.getPlayer().giveDonatorBuff();
                    cm.sendOk("#fs11#MVP 등급이 부여되었습니다\r\n달성,주간 보상은 후원NPC 를 이용해주세요");
                    cm.dispose();
                } else {
                    cm.sendOk("#fs11#캐시가 부족합니다.");
                    cm.dispose();
                }
            } else if (cm.getPlayer().getHgrade() == 4) {
                if (cm.getPlayer().getCashPoint() >= item4[a][2] * cost) {
                    cm.getPlayer().gainCashPoint(-item4[a][2] * cost);
                    cm.getPlayer().gainDonationPoint(item4[a][3]);
                    Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP등급]/[MVP]등급구매.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n이전등급 : " + cm.getPlayer().getHgrades() + "\r\n달성등급 : " + cm.getItemName(item4[a][0]) + "\r\n사용 캐시 : " + -item4[a][2] + "\r\n보유 캐시 : " + cm.getPlayer().getCashPoint() + "\r\n\r\n", true);
                    cm.getPlayer().setHgrade(item4[a][4]);
                    cm.getPlayer().giveDonatorBuff();
                    cm.sendOk("#fs11#MVP 등급이 부여되었습니다\r\n달성,주간 보상은 후원NPC 를 이용해주세요");
                    cm.dispose();
                } else {
                    cm.sendOk("#fs11#캐시가 부족합니다.");
                    cm.dispose();
                }
            } else if (cm.getPlayer().getHgrade() == 3) {
                if (cm.getPlayer().getCashPoint() >= item3[a][2] * cost) {
                    cm.getPlayer().gainCashPoint(-item3[a][2] * cost);
                    cm.getPlayer().gainDonationPoint(item3[a][3]);
                    Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP등급]/[MVP]등급구매.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n이전등급 : " + cm.getPlayer().getHgrades() + "\r\n달성등급 : " + cm.getItemName(item3[a][0]) + "\r\n사용 캐시 : " + -item3[a][2] + "\r\n보유 캐시 : " + cm.getPlayer().getCashPoint() + "\r\n\r\n", true);
                    cm.getPlayer().setHgrade(item3[a][4]);
                    cm.getPlayer().giveDonatorBuff();
                    cm.sendOk("#fs11#MVP 등급이 부여되었습니다\r\n달성,주간 보상은 후원NPC 를 이용해주세요");
                    cm.dispose();
                } else {
                    cm.sendOk("#fs11#캐시가 부족합니다.");
                    cm.dispose();
                }
            } else if (cm.getPlayer().getHgrade() == 2) {
                if (cm.getPlayer().getCashPoint() >= item2[a][2] * cost) {
                    cm.getPlayer().gainCashPoint(-item2[a][2] * cost);
                    cm.getPlayer().gainDonationPoint(item2[a][3]);
                    Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP등급]/[MVP]등급구매.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n이전등급 : " + cm.getPlayer().getHgrades() + "\r\n달성등급 : " + cm.getItemName(item2[a][0]) + "\r\n사용 캐시 : " + -item2[a][2] + "\r\n보유 캐시 : " + cm.getPlayer().getCashPoint() + "\r\n\r\n", true);
                    cm.getPlayer().setHgrade(item2[a][4]);
                    cm.getPlayer().giveDonatorBuff();
                    cm.sendOk("#fs11#MVP 등급이 부여되었습니다\r\n달성,주간 보상은 후원NPC 를 이용해주세요");
                    cm.dispose();
                } else {
                    cm.sendOk("#fs11#캐시가 부족합니다.");
                    cm.dispose();
                }
            } else if (cm.getPlayer().getHgrade() == 1) {
                if (cm.getPlayer().getCashPoint() >= item1[a][2] * cost) {
                    cm.getPlayer().gainCashPoint(-item1[a][2] * cost);
                    cm.getPlayer().gainDonationPoint(item1[a][3]);
                    Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP등급]/[MVP]등급구매.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n이전등급 : " + cm.getPlayer().getHgrades() + "\r\n달성등급 : " + cm.getItemName(item1[a][0]) + "\r\n사용 캐시 : " + -item1[a][2] + "\r\n보유 캐시 : " + cm.getPlayer().getCashPoint() + "\r\n\r\n", true);
                    cm.getPlayer().setHgrade(item1[a][4]);
                    cm.getPlayer().giveDonatorBuff();
                    cm.sendOk("#fs11#MVP 등급이 부여되었습니다\r\n달성,주간 보상은 후원NPC 를 이용해주세요");
                    cm.dispose();
                } else {
                    cm.sendOk("#fs11#캐시가 부족합니다.");
                    cm.dispose();
                }
            } else {
                if (cm.getPlayer().getCashPoint() >= item[a][2] * cost) {
                    cm.getPlayer().gainCashPoint(-item[a][2] * cost);
                    cm.getPlayer().gainDonationPoint(item[a][3]);
                    Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP등급]/[MVP]등급구매.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n이전등급 : " + cm.getPlayer().getHgrades() + "\r\n달성등급 : " + cm.getItemName(item[a][0]) + "\r\n사용 캐시 : " + -item[a][2] + "\r\n보유 캐시 : " + cm.getPlayer().getCashPoint() + "\r\n\r\n", true);
                    cm.getPlayer().setHgrade(item[a][4]);
                    cm.getPlayer().giveDonatorBuff();
                    cm.sendOk("#fs11#MVP 등급이 부여되었습니다\r\n달성,주간 보상은 후원NPC 를 이용해주세요");
                    cm.dispose();
                } else {
                    cm.sendOk("#fs11#캐시가 부족합니다.");
                    cm.dispose();
                }
            }
        }
    }
}

function comma(number) {
    return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}