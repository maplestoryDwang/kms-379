importPackage(Packages.scripting);

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
보라색 = "#fc0xFF6600CC#"
검은색 = "#fc0xFF000000#"
핑크색 ="#fc0xFFFF3366#"
분홍색 = "#fc0xFFF781D8#"
엔터 = "\r\n"
엔터2 = "\r\n\r\n"
enter = "\r\n";

var seld = -1;
var a = "";
var prevallstat = 0, prevatk = 0, nextallstat = 0, nextatk = 0;
var 사용재화 = 0, 재화가격 = 0, 사용재화S = "";
var logtype = 0, maxcount = 0;
var allstat = 2, atk = 2; // 1회당 올스탯/공마 증가치
var maxstat = 80000;

var currencyOptions = [
    { id: 4310308, price: 30, name: "네오코어",   maxCount: 1000, logType: 10 },
    { id: 4031227, price: 50, name: "찬빛",       maxCount:  600, logType: 11 },
    { id: 2437760, price: 300, name: "아케인심볼", maxCount:  100, logType: 12 }, // seld=3
    { id: 2633336, price: 300, name: "어센틱심볼", maxCount:  100, logType: 13 }, // seld=4
    { id: 4009547, price: 1000, name: "에르다조각", maxCount:  30, logType: 14 }  // seld=5
];

function buildCurrencyMenu() {
    var txt = "";
    for (var i = 0; i < currencyOptions.length; i++) {
        var opt = currencyOptions[i];
        var idx = i + 1;
        txt += "#fc0xFF6542D7##L" + idx + "##i" + opt.id + "# #z" + opt.id + "##l #Cgray#(1회 최대 : " + opt.maxCount + "회)#k\r\n";
    }
    return txt;
}

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    status++;

    if (status == 0) {
        if (cm.getPlayer().getKeyValue("cashallstat") == null)
            cm.getPlayer().setKeyValue("cashallstat", "0");
        if (cm.getPlayer().getKeyValue("cashatk")     == null)
            cm.getPlayer().setKeyValue("cashatk", "0");

        if (cm.getPlayer().getKeyValue("cashallstat") > maxstat - 2 ||
            cm.getPlayer().getKeyValue("cashatk")     > maxstat - 2) {
            cm.getPlayer().setKeyValue("cashallstat", maxstat);
            cm.getPlayer().setKeyValue("cashatk",     maxstat);
        }

        prevallstat = parseInt(cm.getPlayer().getKeyValue("cashallstat"));
        prevatk     = parseInt(cm.getPlayer().getKeyValue("cashatk"));

        var txt  = "#fs11#       #fUI/Basic.img/Zenia/SC/0#\r\n";
        txt += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n";
        txt += "#fc0xFF000000#      어떤 재화를 사용하여 캐릭터를 강화해보시겠어요?#l\r\n\r\n";
        txt += "#fc0xFF6542D7#";
        txt += buildCurrencyMenu();  // ← 배열 기반으로 자동 생성
        cm.sendSimple(txt);

    } else if (status == 1) {
        seld = selection;
        var opt = currencyOptions[seld - 1];
        if (!opt) {
            cm.sendOk("#fs11##fc0xFF000000#유효하지 않은 선택입니다.");
            cm.dispose();
            return;
        }
        사용재화   = opt.id;
        재화가격   = opt.price;
        사용재화S  = opt.name;
        maxcount   = opt.maxCount;
        logtype    = opt.logType;

        var txt  = "#fs11#       #fUI/Basic.img/Zenia/SC/0#\r\n";
        txt += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n";
        txt += "#fs11##fc0xFF000000#       #b#i" + 사용재화 + "##z" + 사용재화 + "# " + 재화가격 + "개#fc0xFF000000#로 캐릭터를 강화할 수 있습니다#b \r\n#r                      (1회당 올스탯 2 / 공 마 2 증가)#k\r\n\r\n";
        txt += "#fs13#"+ 보라색 + "                         < 현재 적용 중인 스탯 >\r\n";
        txt += "#fs11#"+ 검은색 + "                                 올스탯 #b+ " + prevallstat + "\r\n";
        txt += 검은색 + "                                 공　마 #b+ " + prevatk + "\r\n\r\n";
        txt += 핑크색 + "                     캐릭터 강화를 진행하시겠습니까?";
        cm.sendSimple(txt);

    } else if (status == 2) {
        var possible = Math.floor(cm.itemQuantity(사용재화) / 재화가격);
        possible = Math.min(possible, maxcount);
        cm.askNumber(
            "#fs11##fc0xFF000000#캐릭터를 몇 회 강화할까? \r\n#Cgray#(현재 강화 가능한 횟수 : " + possible + "번)",
            GameObjectType.User, 1, 1, possible, ScriptMessageFlag.NpcReplacedByUser
        );

    } else if (status == 3) {
        a = selection;
        nextallstat = prevallstat + allstat * a;
        nextatk     = prevatk     + atk     * a;

        if (nextallstat > maxstat || nextatk > maxstat) {
            cm.sendOk("#fs11#현재 캐릭터강화 수치는 80,000 을 초과할 수 없습니다");
            cm.dispose();
            return;
        }

        var txt  = "#fs11#       #fUI/Basic.img/Zenia/SC/0#\r\n";
        txt += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n";
        txt += "#fs13#" + 보라색 + "                         < 현재 적용 중인 스탯 >\r\n";
        txt += "#fs11#" + 검은색 + "                                 올스탯 #b+ " + prevallstat + "\r\n";
        txt += 검은색 + "                                 공　마 #b+ " + prevatk + "\r\n\r\n";
        txt += "#fs13#" + 보라색 + "                       < 강화 후 적용되는 스탯 >\r\n";
        txt += "#fs11#" + 검은색 + "                                 올스탯 #b+ " + nextallstat + "\r\n";
        txt += 검은색 + "                                 공　마 #b+ " + nextatk + "\r\n\r\n";
        txt += 검은색 + "#r                    사용할 #i" + 사용재화 + "##z" + 사용재화 + "# 갯수 : " + (재화가격 * a) + "개\r\n\r\n";
        txt += 검은색 + " 정말 강화를 진행하시겠다면 아래에 #r'동의합니다'" + 검은색 + " 를 입력해주세요";
        cm.sendGetText(txt);

    } else if (status == 4) {
        if (cm.getText() != "동의합니다") {
            cm.sendOk("#fs11#동의 하지않는다면 도와줄 방법이 없어요\r\n#b동의#k 한다면 다시 '#r#e동의합니다#k#n' 를 입력해주세요");
            cm.dispose();
            return;
        }
        if (!cm.haveItem(사용재화, 재화가격 * a)) {
            cm.sendOk("#fs11##fc0xFF000000#" + a + "회 강화하기 위해선 #b" + (재화가격 * a) + " #z" + 사용재화 + "##fc0xFF000000#가 필요합니다.");
            cm.dispose();
            return;
        }
        cm.dispose();
        try {
            cm.addCustomLog(logtype, "[캐릭터강화] | 누적스탯 : " + nextallstat + " | 누적공마 : " + nextatk + " |\r\nㄴ | 강화횟수 : " + a + " | 사용재화 :  " + 사용재화S + " " + (재화가격 * a) + "개 |");
            cm.effectText("#fn나눔고딕 ExtraBold##fs20#[캐릭터강화] 캐릭터가 < " + a + " > 회 강화되었습니다", 50, 1000, 6, 0, 330, -550);
            cm.getPlayer().setKeyValue("cashallstat", nextallstat);
            cm.getPlayer().setKeyValue("cashatk",     nextatk);
            cm.getPlayer().setBonusCTSStat();
            cm.gainItem(사용재화, -재화가격 * a);
        } catch(err) {
            cm.addCustomLog(50, "[CashEn.js] 에러 발생 : " + err);
        }
    }
}
