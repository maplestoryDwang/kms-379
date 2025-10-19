// 후원시스템
importPackage(Packages.server);
importPackage(Packages.database);
importPackage(Packages.client);
importPackage(java.lang);
importPackage(Packages.network.game);

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

var seld = -1;
var seldreward = -1;
var seld2 = -1;
var seld3 = -1;

var reward = 0;
var seldgrade = 0;
var S1 = -1;

// 달성보상
var nreward = [
    { 'ngrade': 1, 'items': [[1143891, 1], [4310266, 2000], [4001715, 200]], 'select': false },
    { 'ngrade': 2, 'items': [[1143892, 1], [4310266, 3000], [4001715, 300]], 'select': false },
    { 'ngrade': 3, 'items': [[1143893, 1], [4310266, 4000], [4031227, 1000], [5060048, 10], [4001715, 500]], 'select': false },
    { 'ngrade': 4, 'items': [[1143894, 1], [4031227, 1500], [5060048, 30], [4001715, 1000]], 'select': false },
    { 'ngrade': 5, 'items': [[1143895, 1], [2439988, 1], [2439961, 3], [4031227, 1500], [4001715, 1500]], 'select': false },
    { 'ngrade': 6, 'items': [[1143896, 1], [5060048, 20], [2439961, 5], [4031227, 2500], [4001715, 3000]], 'select': false },
    { 'ngrade': 7, 'items': [[1143897, 1], [5060048, 30], [2439958, 2], [4031227, 3500], [4001715, 4000], [5068306, 2]], 'select': false },
    { 'ngrade': 8, 'items': [[1143898, 1], [5060048, 30], [2439958, 2], [4031227, 4000], [4001715, 5000], [5068306, 3]], 'select': false },
    { 'ngrade': 9, 'items': [[1143899, 1], [5060048, 30], [2439988, 2], [4031227, 5000], [4001715, 6000], [5068306, 5]], 'select': false },
    { 'ngrade': 10, 'items': [[1143900, 1], [1113316, 1], [1122443, 1], [1032330, 1], [1012757, 1], [4031227, 5000], [4310308, 5000], [5068306, 10]], 'select': false },
    { 'ngrade': 11, 'items': [[1143901, 1], [1113308, 1], [2439988, 1], [5068306, 10], [4031227, 5000], [4310308, 10000]], 'select': false },
    { 'ngrade': 12, 'items': [[1143902, 1], [1182294, 1], [2439988, 1], [5068306, 10], [4031227, 10000], [4310308, 15000]], 'select': false },
    { 'ngrade': 13, 'items': [[1143903, 1], [1162079, 1], [2439988, 1], [5068306, 15], [4031227, 15000], [4310308, 20000]], 'select': false },
    { 'ngrade': 14, 'items': [[1143904, 1], [1190565, 1], [2439988, 1], [5068306, 15], [4031227, 20000], [4310308, 25000]], 'select': false },
    { 'ngrade': 15, 'items': [[1143905, 1], [2439959, 1], [2439988, 1], [5068306, 20], [4031227, 25000], [4310308, 30000]], 'select': false }
] // ngrade는 밑에 grade

var grade = [
    [0, "일반"],
    [1, "VIP Bronze"],
    [2, "VIP Silver"],
    [3, "VIP Gold"],
    [4, "VIP Platinum"],
    [5, "VIP Diamond"],
    [6, "VVIP Classic"],
    [7, "VVIP Premium"],
    [8, "VVIP Luxury"],
    [9, "VVIP Noble"],
    [10, "VVIP Prestige"],
    [11, "MVP Elite"],
    [12, "MVP Prime"],
    [13, "MVP Signature"],
    [14, "MVP CROWN"],
    [15, "MVP Black"]
]

var daily;

// 주간보상
var daily_1 = [
    [4310237, 500],//헌트코인
    [4310266, 300],//승급주화
    [5060048, 1]//레드애플

] // Grade D

var daily_2 = [
    [4310237, 1000],//헌트코인
    [4310266, 500],//승급주화
    [5060048, 3]//레드애플
] // Grade C

var daily_3 = [
    [4310237, 1000],//헌트코인
    [4310266, 800],//승급주화
    [5060048, 5]//레드애플
] // Grade B

var daily_4 = [
    [4310237, 1500],//헌트코인
    [4310266, 1000],//승급주화
    [4031227, 50],//찬빛결
    [5060048, 10]//레드애플
] // Grade A

var daily_5 = [
    [4310237, 2000],//헌트코인
    [4310266, 1500],//승급주화
    [4031227, 200],//찬빛결
    [5060048, 10]//레드애플
] // Grade S

var daily_6 = [
    [4310237, 2500],//헌트코인
    [4310266, 2000],//승급주화
    [4031227, 500],//찬빛결
    [5060048, 10]//레드애플
] // Grade SS

var daily_7 = [
    [4310237, 3000],//헌트코인
    [4310266, 2500],//승급주화
    [4031227, 1000],//찬빛결
    [5060048, 10]//레드애플
] // Grade SSS

var daily_8 = [
    [4310237, 4000],//헌트코인
    [4310266, 3000],//승급주화
    [4031227, 1000],//찬빛결
    [5060048, 15]//레드애플
] // Grade SSS+

var daily_9 = [
    [4310237, 5000],//헌트코인
    [4310266, 4000],//승급주화
    [4031227, 1500],//찬빛결
    [5060048, 15]//레드애플
] // MVP PLATINUM

var daily_10 = [
    [4310237, 6000],//헌트코인
    [4310266, 5000],//승급주화
    [4031227, 2000],//찬빛결
    [5060048, 20]//레드애플
] // MVP LUXURY

var daily_11 = [
    [4310237, 7000],//헌트코인
    [4310266, 6000],//승급주화
    [4031227, 2500],//찬빛결
    [5060048, 20]//레드애플
] // MVP NOBLE

var daily_12 = [
    [4310237, 8000],//헌트코인
    [4310266, 7000],//승급주화
    [4031227, 3000],//찬빛결
    [5060048, 20]//레드애플
] // MVP CROWN

var daily_13 = [
    [4310237, 10000],//헌트코인
    [4310266, 8000],//승급주화
    [4031227, 4000],//찬빛결
    [5060048, 20]//레드애플
] // MVP PRESTIGE

var daily_14 = [
    [4310237, 10000],//헌트코인
    [4310266, 10000],//승급주화
    [4031227, 5000],//찬빛결
    [5060048, 50]//레드애플
] // MVP ROYAL

var daily_15 = [
    [4310237, 10000],//헌트코인
    [4310266, 10000],//승급주화
    [4031227, 6000],//찬빛결
    [5060048, 100]//레드애플
] // MVP ROYAL+

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
        getData();
        if (cm.getClient().getKeyValue("DPointAll") == null)
            cm.getClient().setKeyValue("DPointAll", "0");

        if (cm.inBoss()) {
            cm.getPlayer().dropMessage(5, "보스 진행중엔 이용이 불가능합니다.");
            cm.dispose();
            return;
        }

        var 보유캐시 = comma(cm.getPlayer().getCashPoint());
        var 보유포인트 = comma(cm.getPlayer().getDonationPoint());
        var 현재등급 = cm.getPlayer().getHgrades();
        var 누적캐시 = comma(cm.getClient().getKeyValue("DPointAll"));

        var msg = "#fs11#" + 검은색;
        msg += "         " + 별흰 + " 현재 #fc0xFFFF3366##h ##fc0xFF000000# 님의 등급 : #fc0xFFFF3366#" + 현재등급 + 검은색 + enter;
        msg += "         " + 별흰 + " 현재 #fc0xFFFF3366##h ##fc0xFF000000# 님의 누적 캐시 : #fc0xFFFF3366#" + 누적캐시 + "C#b" + enter + enter;
        msg += " #fc0xFF000000#　　- 보유 캐시 : #fc0xFFFF3366#" + 보유캐시 + "C#k" + 검은색;
        msg += " #fc0xFF000000#   - 보유 포인트 : #fc0xFFFF3366#" + 보유포인트 + "P#k" + 검은색 + enter;
        msg += "#l#Cgray#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
        msg += "　　　　#b#L22##e[ 캐시 상점 ]#n#l";
        msg += "      #b#L2##e[ 포인트 상점 ]#n#l" + enter + enter;
        msg += "　　　#b#L3#[ 초월 아케인심볼 ]#l";
        msg += "  #b#L4#[ 치장아이템 강화 ]#l" + enter;
        //msg += "      #b#L2#- 포인트 상점 -#l" +enter;

        msg += "#l\r\n#Cgray#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";

        msg += 색 + "　　　　#L5#- 등급 달성보상 수령 -#l" + enter;
        msg += 색 + "　　　　#L50#- 누적 캐시보상 수령 -#l #Cgray##L61#(보상보기)#l#k" + enter;
        msg += 색 + "　　　　#L6#- 등급 주간보상 수령 -#l #Cgray##L60#(보상보기)#l#k" + enter;
        cm.sendSimple(msg);
    } else if (status == 1) {
        seld = sel;
        switch (sel) {
            case 1:
                cm.sendSimple(getList());
                break;
            case 2:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 1530050, "포인트상점");
                break;
            case 3:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 9000178, "초월아케인심볼");
                break;
            case 4:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 9000331, "강림캐시강화");
                break;
            case 5:
                var msg = "#fs11#현재 #b#h ##k님의 등급은 #b" + cm.getPlayer().getHgrades() + "#k입니다." + enter;
                var a = "";
                a += "#L999##r달성 보상 미리보기#k" + enter + enter;
                for (i = 0; i < nreward.length; i++) {
                    if (cm.getClient().getKeyValue("nd_" + nreward[i]['ngrade']) == null && cm.getPlayer().getHgrade() >= nreward[i]['ngrade']) {
                        a += "#L" + i + "##b" + grade[nreward[i]['ngrade']][1] + " 누적 보상 (지급 가능)#k" + enter;
                    }
                }
                if (a.equals("")) {
                    msg += "받을 수 있는 보상이 없습니다.";
                    cm.sendOk(msg);
                    cm.dispose();
                    return;
                }
                cm.sendSimple(msg + a);
                break;

            case 6: // 주간보상
                if (cm.getPlayer().getHgrade() < 1) {
                    cm.sendOk("#fs11#MVP 브론즈 이상만 사용할 수 있는 기능입니다.");
                    cm.dispose();
                    return;
                }
                if (cm.getClient().getKeyValue("HgradeWeek") != null) {
                    cm.sendOk("#fs11#이번 주는 이미 수령하셨습니다.");
                    cm.dispose();
                    return;
                }

                time = new Date();
                year = time.getFullYear();
                month = time.getMonth() + 1;
                date2 = time.getDate() < 10 ? "0" + time.getDate() : time.getDate();
                date = year + "" + month + "" + date2;
                daily = cm.getPlayer().getHgrade() == 1 ? daily_1 : cm.getPlayer().getHgrade() == 2 ? daily_2 : cm.getPlayer().getHgrade() == 3 ? daily_3 : cm.getPlayer().getHgrade() == 4 ? daily_4 : cm.getPlayer().getHgrade() == 5 ? daily_5 : cm.getPlayer().getHgrade() == 6 ? daily_6 : cm.getPlayer().getHgrade() == 7 ? daily_7 : cm.getPlayer().getHgrade() == 8 ? daily_8 : cm.getPlayer().getHgrade() == 9 ? daily_9 : cm.getPlayer().getHgrade() == 10 ? daily_10 : cm.getPlayer().getHgrade() == 11 ? daily_11 : cm.getPlayer().getHgrade() == 12 ? daily_12 : cm.getPlayer().getHgrade() == 13 ? daily_13 : cm.getPlayer().getHgrade() == 14 ? daily_14 : cm.getPlayer().getHgrade() == 15 ? daily_15 : [];


                var rlist = "";
                for (a = 0; a < daily.length; a++) {
                    rlist += "#b#i" + daily[a][0] + "##z" + daily[a][0] + "# " + daily[a][1] + "개" + enter;
                }
                cm.sendYesNo("#fs11##b " + cm.getPlayer().getHgrades() + " 보상 수령 가능!#k#r (매주 월요일 초기화)#k\r\n\r\n" + rlist + "\r\n\r\n #r수령 하시겠습니까?");
                break;

            case 22:
                var 보유캐시 = comma(cm.getPlayer().getCashPoint());
                var 보유포인트 = comma(cm.getPlayer().getDonationPoint());
                var 현재등급 = cm.getPlayer().getHgrades();
                var 누적캐시 = comma(cm.getClient().getKeyValue("DPointAll"));

                var msg = "#fs11#" + 검은색;
                msg += 별 + " 현재 #fc0xFFFF3366##h ##fc0xFF000000# 님의 캐시 : #fc0xFFFF3366#" + 보유캐시 + "C#k" + 검은색 + enter;
                msg += "#l\r\n#Cgray#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#" + enter;
                msg += "#L1#- 상시 패키지 -#l" + enter;
                msg += "#L2#- 이벤트 패키지 - #r[Hot !]#k#l" + enter;
                msg += "#L3#- MVP 등급 - #r[Hot !]#l" + enter;

                msg += "#l\r\n#Cgray#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
                cm.sendSimple(msg);
                break;

            case 33: // 주간미션 초기화
                var con = DBConnection.getConnection();
                var ps = con.prepareStatement("DELETE FROM acckeyvalue WHERE `key` = 'HgradeWeek'");
                ps.executeUpdate();
                ps.close();
                con.close();

                for (var i = 0; i < GameServer.getAllInstances().size(); i++) {
                    var chrs = GameServer.getAllInstances().get(i)
                        .getPlayerStorage().getAllCharacters();
                    for (var a = 0; a < chrs.size(); a++) {
                        chrs.get(a).getClient().removeKeyValue("HgradeWeek");
                    }
                }

                cm.sendOkS("초기화완료", 2);
                cm.dispose();
                break;

            case 50: // 누적보상
                var msg = 별 + " #fs11##fc0xFF000000#현재 #fc0xFFFF3366##h ##fc0xFF000000# 님의 누적 캐시 : #fc0xFFFF3366#" + cm.getClient().getKeyValue("DPointAll") + "C#fc0xFF000000##b" + enter;
                msg += 색 + "받으실 누적보상을 선택해 주세요#b" + enter;
                msg += "#L1998#<전체채팅 칭호> 캐릭터" + enter;
                msg += "#L1999#<전체채팅 칭호> 계정공용" + enter + enter;
                msg += "#L10#10만 누적보상  " + checkDPointAll(10) + enter;
                msg += "#L20#20만 누적보상  " + checkDPointAll(20) + enter;
                msg += "#L30#30만 누적보상  " + checkDPointAll(30) + enter;
                msg += "#L50#50만 누적보상  " + checkDPointAll(50) + enter;
                msg += "#L75#75만 누적보상  " + checkDPointAll(75) + enter;
                msg += "#L100#100만 누적보상  " + checkDPointAll(100) + enter;
                msg += "#L125#125만 누적보상  " + checkDPointAll(125) + enter;
                msg += "#L150#150만 누적보상  " + checkDPointAll(150) + enter;
                msg += "#L200#200만 누적보상  " + checkDPointAll(200) + enter;
                msg += "#L250#250만 누적보상  " + checkDPointAll(250) + enter;
                msg += "#L300#300만 누적보상  " + checkDPointAll(300) + enter;
                msg += "#L350#350만 누적보상  " + checkDPointAll(350) + enter;
                msg += "#L400#400만 누적보상  " + checkDPointAll(400) + enter;
                msg += "#L450#450만 누적보상  " + checkDPointAll(450) + enter;
                msg += "#L500#500만 누적보상  " + checkDPointAll(500) + enter;
                msg += "#L550#550만 누적보상  " + checkDPointAll(550) + enter;
                msg += "#L600#600만 누적보상  " + checkDPointAll(600) + enter;
                msg += "#L650#650만 누적보상  " + checkDPointAll(650) + enter;
                msg += "#L700#700만 누적보상  " + checkDPointAll(700) + enter;
                msg += "#L750#750만 누적보상  " + checkDPointAll(750) + enter;
                msg += "#L800#800만 누적보상  " + checkDPointAll(800) + enter;
                msg += "#L850#850만 누적보상  " + checkDPointAll(850) + enter;
                msg += "#L900#900만 누적보상  " + checkDPointAll(900) + enter;
                msg += "#L950#950만 누적보상  " + checkDPointAll(950) + enter;
                msg += "#L1000#1000만 누적보상  " + checkDPointAll(1000) + enter;
                msg += "#L1050#1050만 누적보상  " + checkDPointAll(1050) + enter;
                msg += "#L1100#1100만 누적보상  " + checkDPointAll(1100) + enter;
                msg += "#L1150#1150만 누적보상  " + checkDPointAll(1150) + enter;
                msg += "#L1200#1200만 누적보상  " + checkDPointAll(1200) + enter;
                msg += "#L1250#1250만 누적보상  " + checkDPointAll(1250) + enter;
                msg += "#L1300#1300만 누적보상  " + checkDPointAll(1300) + enter;
                msg += "#L1350#1350만 누적보상  " + checkDPointAll(1350) + enter;
                msg += "#L1400#1400만 누적보상  " + checkDPointAll(1400) + enter;
                msg += "#L1450#1450만 누적보상  " + checkDPointAll(1450) + enter;
                msg += "#L1500#1500만 누적보상  " + checkDPointAll(1500) + enter;
                msg += "#L1550#1550만 누적보상  " + checkDPointAll(1550) + enter;
                msg += "#L1600#1600만 누적보상  " + checkDPointAll(1600) + enter;
                msg += "#L1650#1650만 누적보상  " + checkDPointAll(1650) + enter;
                msg += "#L1700#1700만 누적보상  " + checkDPointAll(1700) + enter;
                msg += "#L1750#1750만 누적보상  " + checkDPointAll(1750) + enter;
                msg += "#L1800#1800만 누적보상  " + checkDPointAll(1800) + enter;
                msg += "#L1850#1850만 누적보상  " + checkDPointAll(1850) + enter;
                msg += "#L1900#1900만 누적보상  " + checkDPointAll(1900) + enter;
                msg += "#L1950#1950만 누적보상  " + checkDPointAll(1950) + enter;
                msg += "#L2000#2000만 누적보상  " + checkDPointAll(2000) + enter;
                msg += "#L2100#2100만 누적보상  " + checkDPointAll(2100) + enter;
                msg += "#L2200#2200만 누적보상  " + checkDPointAll(2200) + enter;
                msg += "#L2300#2300만 누적보상  " + checkDPointAll(2300) + enter;
                msg += "#L2400#2400만 누적보상  " + checkDPointAll(2400) + enter;
                msg += "#L2500#2500만 누적보상  " + checkDPointAll(2500) + enter;
                msg += "#L2600#2600만 누적보상  " + checkDPointAll(2600) + enter;
                msg += "#L2700#2700만 누적보상  " + checkDPointAll(2700) + enter;
                msg += "#L2800#2800만 누적보상  " + checkDPointAll(2800) + enter;
                msg += "#L2900#2900만 누적보상  " + checkDPointAll(2900) + enter;
                msg += "#L3000#3000만 누적보상  " + checkDPointAll(3000) + enter;
                cm.sendSimple(msg);
                break;

            case 60:
                var allDaily = [
                    [],          // 인덱스 0 패스
                    daily_1,     // VIP Bronze
                    daily_2,     // VIP Silver
                    daily_3,     // VIP Gold
                    daily_4,     // VIP Platinum
                    daily_5,     // VIP Diamond
                    daily_6,     // VVIP Classic
                    daily_7,     // VVIP Premium
                    daily_8,     // VVIP Luxury
                    daily_9,     // VVIP Noble
                    daily_10,    // VVIP Prestige
                    daily_11,    // MVP Elite
                    daily_12,    // MVP Prime
                    daily_13,    // MVP Signature
                    daily_14,    // MVP CROWN
                    daily_15     // MVP Black
                ];

                var previewMsg = "#fs11##e주간 보상 전체 미리보기 (매주 월요일 초기화)#n\r\n\r\n";

                for (var i = 1; i < allDaily.length; i++) {
                    var list = allDaily[i];
                    previewMsg += "#fs11##e#r- " + grade[i][1] + " -#k#n#b#e\r\n";
                    for (var j = 0; j < list.length; j++) {
                        previewMsg += "#i" + list[j][0] + "# #z" + list[j][0] + "# " + list[j][1] + "개\r\n";
                    }
                    previewMsg += "\r\n";
                }

                cm.sendOk(previewMsg);
                cm.dispose();
                break;

            case 61:
                var msg = "#fs11##e누적 캐시보상 미리보기#n\r\n\r\n";

                var previewConfig = {
                    10: { spins: 2, berries: 0 },
                    20: { spins: 2, berries: 0 },
                    30: { spins: 3, berries: 0 },
                    50: { spins: 4, berries: 0 },
                    75: { spins: 4, berries: 0 },
                    100: { spins: 5, berries: 1 },
                    125: { spins: 5, berries: 1 },
                    150: { spins: 5, berries: 1 },
                    200: { spins: 7, berries: 2 },
                    250: { spins: 7, berries: 2 },
                    300: { spins: 10, berries: 3 },
                    350: { spins: 10, berries: 3 },
                    400: { spins: 10, berries: 3 },
                    450: { spins: 10, berries: 3 },
                    500: { spins: 10, berries: 3 },
                    550: { spins: 10, berries: 3 },
                    600: { spins: 15, berries: 3 },
                    650: { spins: 15, berries: 3 },
                    700: { spins: 15, berries: 3 },
                    750: { spins: 15, berries: 3 },
                    800: { spins: 15, berries: 3 },
                    850: { spins: 15, berries: 3 },
                    900: { spins: 15, berries: 3 },
                    950: { spins: 15, berries: 3 },
                    1000: { spins: 17, berries: 4 },
                    1050: { spins: 17, berries: 4 },
                    1100: { spins: 17, berries: 4 },
                    1150: { spins: 17, berries: 4 },
                    1200: { spins: 17, berries: 4 },
                    1250: { spins: 17, berries: 4 },
                    1300: { spins: 17, berries: 4 },
                    1350: { spins: 17, berries: 4 },
                    1400: { spins: 17, berries: 4 },
                    1450: { spins: 17, berries: 4 },
                    1500: { spins: 20, berries: 5 },
                    1550: { spins: 20, berries: 5 },
                    1600: { spins: 20, berries: 5 },
                    1650: { spins: 20, berries: 5 },
                    1700: { spins: 20, berries: 5 },
                    1750: { spins: 20, berries: 5 },
                    1800: { spins: 20, berries: 5 },
                    1850: { spins: 20, berries: 5 },
                    1900: { spins: 20, berries: 5 },
                    1950: { spins: 20, berries: 5 },
                    2000: { spins: 25, berries: 7 },
                    2100: { spins: 25, berries: 7 },
                    2200: { spins: 25, berries: 7 },
                    2300: { spins: 25, berries: 7 },
                    2400: { spins: 25, berries: 7 },
                    2500: { spins: 25, berries: 8 },
                    2600: { spins: 30, berries: 8 },
                    2700: { spins: 30, berries: 8 },
                    2800: { spins: 30, berries: 8 },
                    2900: { spins: 30, berries: 8 },
                    3000: { spins: 50, berries: 10 }
                };
                // 출력
                for (var tier in previewConfig) {
                    var cfg = previewConfig[tier];
                    msg += "#fs11##b- " + tier + "만 누적#k :  #i4036661# " + cfg.spins + "개";
                    if (cfg.berries > 0) msg += ",  #i5068306# " + cfg.berries + "개";
                    msg += "\r\n";
                }
                cm.sendOk(msg);
                cm.dispose();
                break;

            case 222:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 2460021, "2460021");
                break;
        }
    } else if (status == 2) {
        switch (seld) {
            case 1:
                getList2(getsex(sel));
                break;

            case 5:
                seld2 = sel;

                if (seld2 == 999) { // 달성보상 미리보기
                    var msg = "#fs11#" + 색 + "MVP 등급 달성시 받으실 수 있는 보상 입니다" + enter + enter;
                    for (i = 0; i < nreward.length; i++) {
                        msg += "#r#e- " + grade[1 + i][1] + " -" + enter;
                        for (ii = 0; ii < nreward[i]['items'].length; ii++) {
                            msg += "#b#e#i" + nreward[i]['items'][ii][0] + "# #z" + nreward[i]['items'][ii][0] + "# " + nreward[i]['items'][ii][1] + "개 \r\n";
                        }
                        msg += enter;
                    }
                    cm.sendOk(msg);
                    cm.dispose();
                    return;
                }

                if (cm.getInvSlots(1) < 3 || cm.getInvSlots(2) < 3 || cm.getInvSlots(3) < 3 || cm.getInvSlots(4) < 3 || cm.getInvSlots(5) < 3) {
                    cm.sendOkS("#fs11##fc0xFF6600CC#인벤토리를 탭별로 3칸이상 비워주세요", 2);
                    cm.dispose();
                    return;
                }

                if (cm.getClient().getKeyValue("nd_" + nreward[seld2]['ngrade']) != null || cm.getPlayer().getHgrade() < nreward[seld2]['ngrade']) {
                    cm.sendOk("r");
                    cm.dispose();
                    return;
                }

                if (!nreward[seld2]['select']) {
                    var msg = "#b#e[ 달성보상 ] #k#n에서\r\n다음과같은 #b보상#k을 지급 받으시겠습니까?\r\n" + enter + enter;
                    for (i = 0; i < nreward[seld2]['items'].length; i++) {
                        //cm.gainItem(nreward[seld2]['items'][i][0], nreward[seld2]['items'][i][1]);
                        msg += "#b#e#i" + nreward[seld2]['items'][i][0] + "# #z" + nreward[seld2]['items'][i][0] + "# " + nreward[seld2]['items'][i][1] + "개 \r\n";
                    }
                    //msg += "#L1#지급받겠습니다.";
                    //msg += "#L2#다음에";
                    cm.sendYesNo(msg);
                } else {
                    var msg = "해당 보상은 선택형 보상입니다. 받으실 보상을 선택해주세요.#b" + enter;
                    for (i = 0; i < nreward[seld2]['items'].length; i++)
                        msg += "#L" + i + "##i" + nreward[seld2]['items'][i][0] + "##z" + nreward[seld2]['items'][i][0] + "# " + nreward[seld2]['items'][i][1] + "개" + enter;
                    cm.sendSimple(msg);
                }
                break;

            case 6:
                if (cm.getClient().getKeyValue("HgradeWeek") == null) {
                    for (a = 0; a < daily.length; a++) {
                        if (!cm.canHold(daily[a][0], daily[a][1])) {
                            cm.sendOk("인벤토리에 공간이 부족합니다.");
                            cm.dispose();
                            return;
                        }
                    }
                    for (a = 0; a < daily.length; a++) {
                        cm.gainItem(daily[a][0], daily[a][1]);
                    }
                    cm.getClient().setKeyValue("HgradeWeek", "1");
                    //Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/[MVP주간보상].log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n등급 : " + cm.getPlayer().getHgrades() + "\r\n\r\n", true);
                    cm.addCustomLog(99, "[주간보상] 등급 : " + cm.getPlayer().getHgrades() + "");
                    cm.sendOk("#fs11#지급이 완료되었습니다.");
                    cm.dispose();
                }
                break;

            case 22:
                seld22 = sel;
                switch (seld22) {
                    case 1:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 1530050, "상시패키지상점");
                        break;
                    case 2:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 1530050, "이벤트패키지상점");
                        break;
                    case 3:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 1530050, "등급상점");
                        break;
                }
                break;

            case 50:  // 누적 캐시보상
                seld50 = sel;
                // 보상 설정: 기준(만 단위) -> {spins: 스핀 수, berries: 베리 수}
                var rewardConfig = {
                    10: { spins: 2, berries: 0 },
                    20: { spins: 2, berries: 0 },
                    30: { spins: 3, berries: 0 },
                    50: { spins: 4, berries: 0 },
                    75: { spins: 4, berries: 0 },
                    100: { spins: 5, berries: 1 },
                    125: { spins: 5, berries: 1 },
                    150: { spins: 5, berries: 1 },
                    200: { spins: 7, berries: 2 },
                    250: { spins: 7, berries: 2 },
                    300: { spins: 10, berries: 3 },
                    350: { spins: 10, berries: 3 },
                    400: { spins: 10, berries: 3 },
                    450: { spins: 10, berries: 3 },
                    500: { spins: 10, berries: 3 },
                    550: { spins: 10, berries: 3 },
                    600: { spins: 15, berries: 3 },
                    650: { spins: 15, berries: 3 },
                    700: { spins: 15, berries: 3 },
                    750: { spins: 15, berries: 3 },
                    800: { spins: 15, berries: 3 },
                    850: { spins: 15, berries: 3 },
                    900: { spins: 15, berries: 3 },
                    950: { spins: 15, berries: 3 },
                    1000: { spins: 17, berries: 4 },
                    1050: { spins: 17, berries: 4 },
                    1100: { spins: 17, berries: 4 },
                    1150: { spins: 17, berries: 4 },
                    1200: { spins: 17, berries: 4 },
                    1250: { spins: 17, berries: 4 },
                    1300: { spins: 17, berries: 4 },
                    1350: { spins: 17, berries: 4 },
                    1400: { spins: 17, berries: 4 },
                    1450: { spins: 17, berries: 4 },
                    1500: { spins: 20, berries: 5 },
                    1550: { spins: 20, berries: 5 },
                    1600: { spins: 20, berries: 5 },
                    1650: { spins: 20, berries: 5 },
                    1700: { spins: 20, berries: 5 },
                    1750: { spins: 20, berries: 5 },
                    1800: { spins: 20, berries: 5 },
                    1850: { spins: 20, berries: 5 },
                    1900: { spins: 20, berries: 5 },
                    1950: { spins: 20, berries: 5 },
                    2000: { spins: 25, berries: 7 },
                    2100: { spins: 25, berries: 7 },
                    2200: { spins: 25, berries: 7 },
                    2300: { spins: 25, berries: 7 },
                    2400: { spins: 25, berries: 7 },
                    2500: { spins: 25, berries: 8 },
                    2600: { spins: 30, berries: 8 },
                    2700: { spins: 30, berries: 8 },
                    2800: { spins: 30, berries: 8 },
                    2900: { spins: 30, berries: 8 },
                    3000: { spins: 50, berries: 10 }
                };

                // 1998/1999는 전체채팅 칭호 설정, 나머지는 누적 보상 로직으로
                switch (seld50) {
                    case 1998:
                    case 1999:
                        var chatConfig = {
                            1998: { required: 5000000, scope: '캐릭터', note: '※ 누적 500만 미만 시 1회만 설정 가능' },
                            1999: { required: 10000000, scope: '계정', note: '' }
                        };
                        var cc = chatConfig[seld50];
                        if (cm.getPlayer().getDPointAll() >= cc.required) {
                            var msg = "#fs11#";
                            msg += 색 + "< 전체채팅 칭호 우선순위 >\r\n";
                            msg += 검은색 + "캐릭터칭호 → 계정칭호 → 메인랭크 순서입니다.\r\n";
                            if (seld50 === 1998) msg += "#r" + cc.note + "#k\r\n\r\n";
                            msg += "적용할 " + cc.scope + " 칭호를 입력해 주세요.";
                            cm.sendGetText(msg);
                        } else {
                            cm.sendOk("#fs11#누적금액이 부족합니다.");
                            cm.dispose();
                        }
                        break;

                    default:
                        var threshold = seld50;
                        var requiredCash = threshold * 10000;
                        var key = "DPointAll_" + threshold;
                        var userTotal = cm.getPlayer().getDPointAll();
                        var cfg = rewardConfig[threshold];

                        if (!cfg) {
                            cm.sendOk("#fs11#잘못된 보상 항목입니다.");
                            cm.dispose();
                            break;
                        }

                        var already = Number(cm.getClient().getKeyValue(key) || "0") >= 1;
                        if (userTotal >= requiredCash && !already) {
                            cm.getClient().setKeyValue(key, "1");
                            cm.gainItem(4036661, cfg.spins);
                            if (cfg.berries > 0) cm.gainItem(5068306, cfg.berries);
                            cm.sendOk("#fs11#보상이 지급되었습니다.");
                        } else {
                            cm.sendOk("#fs11#이미 수령하셨거나 누적금액이 부족합니다.");
                        }
                        cm.dispose();
                        break;
                }
                break;
        }
        if (S1 == 223) {
            if (cm.getClient().getKeyValue("tradepoint") != null) {
                tradePoint = Number(cm.getClient().getKeyValue("tradepoint"));
            } else {
                tradePoint = 0
            }
            if (tradePoint >= tradePoint + sel) {
                cm.sendOk("계정당 포인트 충전은 최대 300만 까지 가능합니다.");
                cm.dispose();
                return;
            }
            if (cm.getPlayer().getCashPoint() < sel) {
                cm.sendOk("보유하신 캐시잔액보다 큰값을 입력하셨습니다.");
                cm.dispose();
                return;
            }
            if (0 > sel) {
                cm.sendOk("오류 발생 오류 발생");
                cm.dispose();
                return;
            }
            cm.getPlayer().gainCashPoint(-sel);
            cm.getPlayer().gainDonationPoint(sel);

            cm.getClient().setKeyValue("tradepoint", "" + (tradePoint + sel));
            cm.sendOk(comma(sel) + "환전이 완료되었습니다.");
            cm.dispose();
            return;
        }
    } else if (status == 3) {
        switch (seld) {
            case 1:
                seldreward = sel;
                reward = getQ(seldreward);
                cm.sendYesNo("정말 해당 보상을 지급받으시겠습니까?\r\n총 #b" + reward + " 후원포인트#k를 얻게됩니다.");
                break;

            case 4:
                seld2 = sel;
                switch (seld3) {
                    case 1:
                        getList2Admin(seld2);
                        break;
                }
                break;

            case 5:
                if (!nreward[seld2]['select']) {
                    for (i = 0; i < nreward[seld2]['items'].length; i++) {
                        cm.gainItem(nreward[seld2]['items'][i][0], nreward[seld2]['items'][i][1]);
                    }
                    cm.getClient().setKeyValue("nd_" + nreward[seld2]['ngrade'], "1");
                    cm.sendOkS("#fs11##b#e감사합니다~!", 2);
                    cm.dispose();
                    return;
                }
                break;

            case 50:
                switch (seld50) {
                    case 1998:
                        SetMedalString = cm.getText();
                        Get500MedalString = cm.getClient().getKeyValue("500MedalString");
                        if (cm.getClient().getKeyValue("DPointAll") >= 10000000) {
                            cm.getClient().getPlayer().setKeyValue("MedalString", SetMedalString);
                            cm.sendOkS("#fs11##b#e감사합니다~!", 2);
                            cm.dispose();
                            return;
                        } else if (SetMedalString != null && Get500MedalString == null) {
                            cm.getClient().setKeyValue("500MedalString", "1");
                            cm.getClient().getPlayer().setKeyValue("MedalString", SetMedalString);
                            cm.sendOkS("#fs11##b#e감사합니다~!", 2);
                            cm.dispose();
                            return;
                        } else {
                            cm.sendOkS("#fs11##b#e아 맞다 이미 설정했었지..", 2);
                            cm.dispose();
                            return;
                        }
                        break;

                    case 1999:
                        SetMedalString = cm.getText();
                        if (SetMedalString != null) {
                            cm.getClient().setKeyValue("MedalString", SetMedalString);
                            cm.sendOkS("#fs11##b#e감사합니다~!", 2);
                            cm.dispose();
                            return;
                        }
                        break;
                }
        }
    } else if (status == 4) {
        switch (seld) {
            case 1:
                getReward(seldreward);
                cm.getPlayer().gainDonationPoint(reward);
                cm.sendOk("수령이 완료되었습니다.");
                cm.dispose();
                break;

            case 4:
                switch (seld3) {
                    case 1:
                        DeleteReward(sel);
                        cm.sendOk("삭제가 완료되었습니다.");
                        cm.dispose();
                        break;
                }
                break;

            case 5:
                cm.gainItem(nreward[seld2]['items'][seld3][0], nreward[seld2]['items'][seld3][1]);

                cm.getClient().setKeyValue("nd_" + nreward[seld2]['ngrade'], "1");
                cm.sendOk("#fs11#지급이 완료되었습니다.");
                cm.dispose();
                break;
        }
    } else if (status == 5) {
        switch (seld) {
            case 4:
                switch (seld3) {
                    case 2:
                        if (modifychr != null) {
                            modifychr.setHgrade(seldgrade);
                        } else {
                            var con = DBConnection.getConnection();
                            asdid = getAccId(modify);
                            var ps = con.prepareStatement("UPDATE acckeyvalue SET `value` = ? WHERE `id` = ? and `key` = ?");
                            ps.setString(1, seldgrade + "");
                            ps.setInt(2, asdid);
                            ps.setString(3, "hGrade");
                            ps.executeUpdate();
                            ps.close();
                            con.close();
                        }
                        cm.sendOk("변경이 완료되었습니다.");
                        cm.dispose();
                        break;
                }
                break;
        }
    }
}


function getData() {
    time = new Date();
    year = time.getFullYear();
    month = time.getMonth() + 1;
    if (month < 10) {
        month = "0" + month;
    }
    date2 = time.getDate() < 10 ? "0" + time.getDate() : time.getDate();
    date = year + "." + month + "." + date2;
    day = time.getDay();
}

function getList() {
    var ret = "";
    var names = [];

    var con = DBConnection.getConnection();
    var ps = con.prepareStatement("SELECT * FROM donation WHERE `check` = 0");
    var rs = ps.executeQuery();

    while (rs.next()) {
        if (rs.getInt("cid") == cm.getPlayer().getId() || rs.getString("name").equals(cm.getPlayer().getName())) {
            ret += "#L" + rs.getInt("id") + "##b" + rs.getString("name") + " (지급대상)\r\n";
            break;
        }
    }
    rs.close();
    ps.close();
    con.close();

    if (ret.equals("")) return "#fs11#현재 수령 가능한 후원 보상이 없습니다.";
    return ret;
}
function getQ(id) {
    var ret = 0;
    var con = DBConnection.getConnection();
    var ps = con.prepareStatement("SELECT * FROM donation WHERE `check` = 0 AND `id` = ? AND (`name` = ? or `cid` = ?)");
    ps.setInt(1, id);
    ps.setString(2, cm.getPlayer().getName());
    ps.setInt(3, cm.getPlayer().getId());
    var rs = ps.executeQuery();
    while (rs.next()) {
        ret += rs.getInt("sum");
    }
    rs.close();
    ps.close();
    con.close();
    if (ret.equals("")) return "후원 보상이 없습니다.";
    return ret;
}

function getReward(id) {
    var con = DBConnection.getConnection();
    var ps = con.prepareStatement("UPDATE donation SET `check` = 1 WHERE `id` = ? AND (`name` = ? or `cid` = ?)");
    ps.setInt(1, id);
    ps.setString(2, cm.getPlayer().getName());
    ps.setInt(3, cm.getPlayer().getId());
    ps.executeUpdate();
    ps.close();
    con.close();
}

function comma(number) {
    return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

function checkDPointAll(cash) {
    price = cash * 10000;
    keyvalue = "DPointAll_" + cash + ""
    if (cm.getPlayer().getDPointAll() >= price) {
        if (cm.getClient().getKeyValue(keyvalue) < 1) {
            return "#b(수령가능)#b";
        }
        return "#fc0xFF9A9A9A#(수령완료)#b";
    } else {
        return "#r(금액부족)#b";
    }
}