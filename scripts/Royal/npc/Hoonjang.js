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

var need = 4021031, qty = 150;
var 선택필요 = 4021031, 선택개수 = 300;

var range = 1; // 한줄에 몇개?

var dskin = [1142661, 1142593, 1142006, 1142440, 1142013, 1142003, 1143114, 1142152, 1140002, 1141002, 1142000, 1142001, 1142002, 1142003, 1142004, 1142009, 1142010, 1142011, 1142012, 1142013, 1142014, 1142015, 1142016, 1142017, 1142018, 1142019, 1142020, 1142021, 1142022, 1142023, 1142024, 1142025, 1142026, 1142027, 1142028, 1142029, 1142030, 1142031, 1142032, 1142033, 1142034, 1142035, 1142036, 1142037, 1142038, 1142039, 1142040, 1142041, 1142042, 1142043, 1142044, 1142045, 1142046, 1142047, 1142048, 1142049, 1142050, 1142051, 1142052, 1142053, 1142054, 1142055, 1142056, 1142057, 1142058, 1142059, 1142060, 1142061, 1142062, 1142063, 1142064, 1142065, 1142066, 1142067, 1142068, 1142069, 1142070, 1142071, 1142072, 1142073, 1142074, 1142075, 1142076, 1142077, 1142078, 1142079, 1142080, 1142081, 1142082, 1142083, 1142084, 1142085, 1142086, 1142087, 1142088, 1142089, 1142090, 1142091, 1142092, 1142093, 1142094, 1142095, 1142096, 1142097, 1142098, 1142099, 1142100, 1142101, 1142107, 1142108, 1142109, 1142110, 1142111, 1142112, 1142113, 1142114, 1142115 ,1142116, 1142117, 1142118, 1142119, 1142120, 1142122, 1142123, 1142126, 1142134, 1142135, 1142136, 1142137, 1142138, 1142139, 1142141, 1142142, 1142143, 1142149, 1142150, 1142151, 1142166, 1142187, 1142190, 1142191, 1142217, 1142218, 1142295, 1142296, 1142297, 1142298, 1142299, 1142300, 1142301, 1142305, 1142306, 1142307, 1142329, 1142334, 1142335, 1142360, 1142373, 1142406, 1142408, 1142441, 1142442, 1142443, 1142457, 1142511, 1142512, 1142543, 1142569, 1142573, 1142627, 1143174, 1143033, 1143008];

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
        msg += "#fc0xFF000000#각종 훈장들이 준비되어 있는 것 같다.#b" + enter;
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
                    cm.sendOk("#fs11#인벤토리 공간이 부족하거나 고유아이템입니다");
                    cm.dispose();
                    return;
                }

                cm.gainItem(need, -qty);
                ItemInfo = Packages.objects.item.MapleItemInformationProvider.getInstance().getEquipById(dskin[a]);
                ItemInfo.setUpgradeSlots(0);
                ItemInfo.setStr(0);
                ItemInfo.setDex(0);
                ItemInfo.setInt(0);
                ItemInfo.setLuk(0);
                ItemInfo.setWatk(0);
                ItemInfo.setMatk(0);
                ItemInfo.setHp(0);
                ItemInfo.setMp(0);
                ItemInfo.setWdef(0);
                ItemInfo.setSpeed(0);
                ItemInfo.setJump(0);
                Packages.objects.item.MapleInventoryManipulator.addFromDrop(cm.getClient(),ItemInfo,true);
                msg = "#fs11#다음과 같은 아이템이 나왔다." + enter;
                msg += "#fs11##b" + enter + "#i" + dskin[a] + "##z" + dskin[a] + "#";
                msg += 핑크색 + "\r\n\r\n한번더 뽑으시겠습니까?";
                cm.sendYesNo(msg);
                break;
            case 3:
                var msg = "#fs11#원하는 훈장를 선택해보자. #fs11##b"
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
            cm.sendOk("#fs11#인벤토리 공간이 부족하거나 고유아이템입니다");
            cm.dispose();
            return;
        }

        cm.gainItem(선택필요, -선택개수);
        ItemInfo = Packages.objects.item.MapleItemInformationProvider.getInstance().getEquipById(dskin[a]);
        ItemInfo.setUpgradeSlots(0);
        ItemInfo.setStr(0);
        ItemInfo.setDex(0);
        ItemInfo.setInt(0);
        ItemInfo.setLuk(0);
        ItemInfo.setWatk(0);
        ItemInfo.setMatk(0);
        ItemInfo.setHp(0);
        ItemInfo.setMp(0);
        ItemInfo.setWdef(0);
        ItemInfo.setSpeed(0);
        ItemInfo.setJump(0);
        Packages.objects.item.MapleInventoryManipulator.addFromDrop(cm.getClient(),ItemInfo,true);
        cm.sendOk("#fs11#다음과 같은 아이템이 나왔다.\r\n#b" + enter + "#i" + dskin[a] + "##z" + dskin[a] + "#");
        cm.dispose();
    }
}