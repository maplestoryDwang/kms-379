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

var prevallstat = 0;
var prevatk = 0;
var nextallstat = 0;
var nextatk = 0;

var 사용재화 = 0;
var 재화가격 = 0;
var 사용재화S = "";
var maxcount = 0;
var price = 30;
var allstat = 2, atk = 2; // 1회당 올스텟, 공마 증가치

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        if (cm.getPlayer().getKeyValue("cashallstat") == null)
            cm.getPlayer().setKeyValue("cashallstat", "0")

        if (cm.getPlayer().getKeyValue("cashatk") == null)
            cm.getPlayer().setKeyValue("cashatk", "0")
        
        prevallstat = parseInt(cm.getPlayer().getKeyValue("cashallstat"));
        prevatk = parseInt(cm.getPlayer().getKeyValue("cashatk"));
        
        txt = "#fs11#       #fUI/Basic.img/Zenia/SC/0#\r\n";
        txt += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n";
        txt += "#fc0xFF000000#     어떤 재화를 사용하여 캐릭터를 강화해보시겠어요?#l\r\n\r\n";
        txt += "#fc0xFF6542D7##L1##i4310308# #z4310308##l";
        txt += "#fc0xFF6542D7##L2##i4031227# #z4031227##l";
        
        cm.sendSimple(txt);
    } else if (status == 1) {
        seld = selection;
        if (seld == 1) { // 네오코어
            사용재화 = 4310308;
            재화가격 = 30;
            사용재화S = "네오코어";
            maxcount = 1000;
        } else if (seld == 2) { // 붉은구슬
            사용재화 = 4031227;
            재화가격 = 100;
            사용재화S = "붉은구슬";
            maxcount = 300;
        } else {
            cm.sendOk("#fs11##fc0xFF000000#사용하실 재화를 선택해주세요");
            cm.dispose();
            return;
        }
        
        txt = "#fs11#       #fUI/Basic.img/Zenia/SC/0#\r\n";
        txt += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n";
        txt += "#fs11##fc0xFF000000#       #b#i" + 사용재화 + "##z" + 사용재화 + "# " + 재화가격 + "개#fc0xFF000000#로 캐릭터를 강화할 수 있습니다#b \r\n#r                      (1회당 올스탯 2 / 공 마 2 증가)#k" + enter + enter;
        txt += "#fs13#" + 보라색 + "                         < 현재 적용 중인 스탯 >" + enter;
        txt += "#fs11#" + 검은색 + "                                 올스탯 #b+ " + prevallstat + enter;
        txt += 검은색 + "                                 공　마 #b+ " + prevatk + enter + enter;
        txt += 핑크색 + "                     캐릭터 강화를 진행하시겠습니까?";

        cm.sendSimple(txt);
    } else if (status == 2) {
        var suk1 = Math.floor((cm.itemQuantity(사용재화) / 재화가격));
        stigmacoin = Math.min(suk1);
        stigmacoin = Math.min(1000, stigmacoin);

        if (stigmacoin > maxcount)
            //stigmacoin = maxcount

        //cm.sendGetNumber("#fs11##fc0xFF000000#캐릭터를 몇회 강화 하시겠습니까? \r\n#Cgray#(현재 강화 가능한 횟수 : " + stigmacoin + "번)", 1, 1, stigmacoin);
        cm.askNumber("#fs11##fc0xFF000000#캐릭터를 몇 회 강화할까? \r\n#Cgray#(현재 강화 가능한 횟수 : " + stigmacoin + "번)", GameObjectType.User, 1, 1, stigmacoin, ScriptMessageFlag.NpcReplacedByUser);
    } else if (status == 3) {
        a = selection;
        nextallstat = prevallstat + (allstat * a);
        nextatk = prevatk + (atk * a);
        
        if (a < 0 | 재화가격 * a > 30000) {
            cm.dispose();
            return;
        }

        txt = "#fs11#       #fUI/Basic.img/Zenia/SC/0#\r\n";
        txt += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n";
        txt += "#fs13#" + 보라색 + "                         < 현재 적용 중인 스탯 >" + enter;
        txt += "#fs11#" + 검은색 + "                                 올스탯 #b+ " + prevallstat + enter;
        txt += 검은색 + "                                 공　마 #b+ " + prevatk + enter + enter;
        txt += "#fs13#" + 보라색 + "                       < 강화 후 적용되는 스탯 >" + enter;
        txt += "#fs11#" + 검은색 + "                                 올스탯 #b+ " + nextallstat + enter;
        txt += 검은색 + "                                 공　마 #b+ " + nextatk + enter + enter;
        txt += 검은색 + "#r                    사용할 #i" + 사용재화 + "##z" + 사용재화 + "# 갯수 : " + (재화가격 * a) + "개" + enter + enter;
        txt += 검은색 + " 정말 강화를 진행하시겠다면 아래에 #r'동의합니다'" + 검은색 + " 를 입력해주세요";
        

        cm.sendGetText(txt);
    } else if (status == 4) {
        var text = cm.getText();
        if (text != "동의합니다") {
            cm.dispose();
            cm.sendOk("#fs11#동의 하지않는다면 도와줄 방법이 없어요\r\n#b동의#k 한다면 다시 '#r#e동의합니다#k#n' 를 입력해주세요");
            return;
        }

        if (!cm.haveItem(사용재화, 재화가격 * a)) {
            cm.sendOk("#fs11##fc0xFF000000#" + a + "회 강화하기 위해선 #b" + 재화가격 * a + " #z" + 사용재화 + "##fc0xFF000000#가 필요합니다.");
            cm.dispose();
            return;
        }

        cm.dispose();

        try {
            cm.addCustomLog(10, "[캐릭터강화] | 누적스탯 : " + nextallstat + " | 누적공마 : " + nextatk + " |\r\nㄴ | 강화횟수 : " + a + " | 사용재화 :  " + 사용재화S + " " + (재화가격 * a) + "개 |");
            cm.effectText("#fn나눔고딕 ExtraBold##fs20#[캐릭터강화] 캐릭터가 < " + a + " > 회 강화되었습니다", 50, 1000, 6, 0, 330, -550);
            cm.getPlayer().setKeyValue("cashallstat", nextallstat);
            cm.getPlayer().setKeyValue("cashatk", nextatk);
            cm.getPlayer().setBonusCTSStat();
            cm.gainItem(사용재화, -재화가격 * a);
            
            prevflag = cm.getPlayer().getSaveFlag();
            cm.getPlayer().setSaveFlag(4096); // KeyValue
            cm.getPlayer().saveToDB(false, false);
            cm.getPlayer().setSaveFlag(prevflag)
        } catch(err) {
            cm.addCustomLog(50, "[CashEn.js] 에러 발생 : " + err + "");
        }
    }
}