importPackage(Packages.server);

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
핑크색 ="#fc0xFFFF3366#"
분홍색 = "#fc0xFFF781D8#"
엔터 = "\r\n"
엔터2 = "\r\n\r\n"

var enter = "\r\n";
var seld = -1;

var need = 4021031, qty = 100;
var 선택필요 = 4021031, 선택개수 = 300;

var range = 1; // 한줄에 몇개?

var dskin = [3700548, 3700540, 3700539, 3700530, 3700529, 3700528, 3700524, 3700514, 3700513, 3700512, 3700511, 3700510, 3700492, 3700490, 3700489, 3700486, 3700478, 3700468, 3700466, 3700465, 3700458, 3700454, 3700447, 3700445, 3700444, 3700442, 3700440, 3700435, 3700434, 3700433, 3700432, 3700430, 3700429, 3700425, 3700422, 3700421, 3700420, 3700419, 3700418, 3700417, 3700403, 37,004,023,700,390, 3700390, 3700389, 3700388, 3700380, 3700378, 3700377, 3700376, 3700356, 3700355, 3700354, 3700353, 3700352, 3700344, 3700339, 3700338, 3700337, 3700336, 3700335, 3700322, 3700321, 3700306, 3700000, 3700001, 3700002, 3700003, 3700004, 3700005, 3700006, 3700010, 3700011, 3700012, 3700013, 3700014, 3700015, 3700016, 3700017, 3700018, 3700019, 3700025, 3700026, 3700035, 3700040, 3700045, 3700046, 3700062, 3700063, 3700064, 3700065, 3700068, 3700091, 3700090, 3700093, 3700096, 3700108, 3700118, 3700119, 3700176, 3700177, 3700178, 3700180, 3700220, 3700270, 3700228, 3700278, 3700279, 3700280, 3700283, 3700285, 3700286, 3700007, 3700008, 3700009, 3700020, 3700039, 3700041, 3700042, 3700043, 3700044, 3700047, 3700048, 3700049, 3700057, 3700074, 3700075, 3700076, 3700077, 3700078, 3700079, 3700080, 3700081, 3700082, 3700084, 3700098, 3700099, 3700100, 3700101, 3700103, 3700106, 3700120, 3700135, 3700136, 3700141, 3700142, 3700143, 3700144, 3700147, 3700148, 3700149, 3700150, 3700156, 3700157, 3700158, 3700159, 3700164, 3700214, 3700215, 3700216, 3700217, 3700218, 3700219, 3700229, 3700230, 3700231, 3700242, 3700244, 3700245, 3700247, 3700248, 3700249, 3700250, 3700251, 3700252, 3700253, 3700254, 3700263, 3700268, 3700269, 3700271, 3700272, 3700281, 3700284, 3700288, 3700334, 3700350, 3700351, 3700385, 3700402, 3700085, 3700087];


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
        var msg = "#fs11#" + enter;
        msg += "#fc0xFF000000#각종 칭호들이 준비되어 있는 것 같다.#b" + enter;
        msg += "랜덤 - #i" + need + "##z" + need + "# " + qty + "개" + enter;
        //msg += "선택 - #i"+선택필요+"##z"+선택필요+"# "+선택개수+"개#b"+enter;
        msg += "#L1##d아이템 목록을 확인해본다." + enter;
        msg += "#L2#랜덤으로 뽑아볼래\r\n";
        //msg += "#L3#선택으로 뽑아볼래";

        cm.sendSimple(msg);
    } else if (status == 1) {
        switch (sel) {
            case 1:
                var msg = "#fs11#" +enter;
                for (i = 0; i < dskin.length; i++) {
                    if (i % range == 0) {
                        msg += "\r\n#b"
                    }
                    msg += "#i" + dskin[i] + "##z" + dskin[i] + "#";
                }
                cm.sendOk(msg);
                cm.dispose();
                break;
            case 2:
                if (!cm.haveItem(need, qty)) {
                    cm.sendOk("#fs11#뽑기에 필요한 재료가 부족합니다.");
                    cm.dispose();
                    return;
                }
                a = Packages.objects.utils.Randomizer.rand(0, (dskin.length - 1));

                if (!cm.canHold(dskin[a], 1)) {
                    cm.sendOk("#fs11#인벤토리 공간이 부족합니다.");
                    cm.dispose();
                    return;
                }

                cm.gainItem(need, -qty);
                cm.gainItem(dskin[a], 1);
                msg = "#fs11#다음과 같은 아이템이 나왔다." + enter;
                msg += "#fs11##b" + enter + "#i" + dskin[a] + "##z" + dskin[a] + "#";
                msg += 핑크색 + "\r\n\r\n한번더 뽑으시겠습니까?";
                cm.sendYesNo(msg);
                break;
            case 3:
                var msg = "#fs11#원하는 칭호를 선택해보자. #fs11##b"
                for (i = 0; i < dskin.length; i++) {
                    if (i % range == 0) {
                        msg += "\r\n"
                    }
                    msg += "#L" + i + "##i" + dskin[i] + "##z" + dskin[i] + "#";
                }
                cm.sendSimple(msg);
                break;
        }
    } else if (status == 2) {
        a = sel;

        if (a == -1) { // 한번더 뽑기[랜덤]
            status = 0;
            action(1, 0, 2);
            return;
        }

        if (!cm.haveItem(선택필요, 선택개수)) {
            cm.sendOk("#fs11#뽑기에 필요한 재료가 부족합니다.");
            cm.dispose();
            return;
        }

        if (!cm.canHold(dskin[a], 1)) {
            cm.sendOk("#fs11#인벤토리 공간이 부족합니다.");
            cm.dispose();
            return;
        }

        cm.gainItem(선택필요, -선택개수);
        cm.gainItem(dskin[a], 1);
        cm.sendOk("#fs11#다음과 같은 아이템이 나왔다.\r\n#b" + enter + "#i" + dskin[a] + "##z" + dskin[a] + "#");
        cm.dispose();
    }
}