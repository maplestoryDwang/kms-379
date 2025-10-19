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

var status = -1;
var sel = -1;
var seld = -1;

var rachikaonlyring = [1115500, 1115501, 1115502, 1115503, 1115504, 1115505, 1115506, 1115507, 1115508, 1115509, 1115510, 1115511, 1115512, 1115513, 1115514, 1115515, 1115516, 1115517, 1115518, 1115519, 1115800, 1115801, 1115802, 1115803, 1115804, 1115805, 1115806, 1115807, 1115808, 1115809, 1115810, 1115811, 1115812, 1115813, 1115814, 1115815, 1115816, 1115817, 1115818, 1115819, 1114001, 1114002, 1114003, 1114004, 1114005, 1114006, 1114007, 1114008, 1114009, 1114011, 1114012, 1114013, 1114014, 1114015, 1114016, 1114010, 1114011, 1114012, 1114013, 1114014, 1114015, 1114016];
var onlyone = [1114001, 1114002, 1114003, 1114004, 1114005, 1114006, 1114007, 1114008, 1114009, 1114011, 1114012, 1114013, 1114014, 1114015, 1114016, 1114010, 1114011, 1114012, 1114013, 1114014, 1114015, 1114016];

function action(mode, type, selection) {
    if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }
    
    
    if (status == 0) {
        // 치장 슬롯 1칸
        if (cm.getInvSlots(6) <= 0) {
            cm.sendOk("#fs11#인벤토리 공간이 부족합니다. [치장] 탭의 1칸을 비워주세요");
            cm.dispose();
            return;
        }

        v0 = "#fs11#\r\n";
        v0 += 핑크색 + "[강림월드]" + 검은색 + " 의 한정 캐시 목록이다 원하는 방식을 선택해보자!\r\n\r\n";
        v0 += "#L1000#[" + 핑크색 + "검색 캐시" + 검은색 + "] 로 지급받을래#l\r\n";
        v0 += "#L1001#[" + 핑크색 + "해외 캐시" + 검은색 + "] 로 지급받을래#l\r\n";
        v0 += "#L1002#[" + 핑크색 + "제니아 스페셜 캐시" + 검은색 + "] 로 지급받을래#l\r\n";
        v0 += "#L1003#[" + 핑크색 + "로얄 스페셜 캐시" + 검은색 + "] 로 지급받을래#l\r\n";
        v0 += "\r\n\r\n";
        v0 += 핑크색 + "< 한정캐시 목록 >\r\n";
        for (var i = 0; i < rachikaonlyring.length; i++) {
                v0 += "#L" + i + "##i" + rachikaonlyring[i] + "# #b#z" + rachikaonlyring[i] + "##l\r\n";
        }

        cm.askMenu(v0, GameObjectType.User, ScriptMessageFlag.NpcReplacedByUser);
    } else if (status == 1) {
        seld = selection;
        sel = rachikaonlyring[selection];
        if (seld == 1000) {
            cm.dispose();
            cm.openNpcCustom(cm.getClient(), 9000213, "ItemsearchSS");
            return;
        } else if (seld == 1001) {
            cm.dispose();
            cm.openNpcCustom(cm.getClient(), 9000213, "globalcashSS");
            return;
        } else if (seld == 1002) {
            cm.dispose();
            cm.openNpcCustom(cm.getClient(), 9000213, "색변캐시SS");
            return;
        } else if (seld == 1003) {
            cm.dispose();
            cm.openNpcCustom(cm.getClient(), 9000213, "색변캐시2SS");
            return;
        }

        if (onlyone.indexOf(sel) != -1) {
            cm.askYesNo("#fs11##i" + sel + "##b #z" + sel + "##k (을)를 받을까?\r\n\r\n#r※ 이 종류의 반지는 중복착용이 불가능하고\r\n#i1114000##z1114000# 아이템과 같이 착용할 수 없어", GameObjectType.User, ScriptMessageFlag.NpcReplacedByUser);
        } else {
            cm.askYesNo("#fs11##i" + sel + "##b #z" + sel + "##k (을)를 받을까?\r\n\r\n#r※ 선택은 되돌릴수 없어!!", GameObjectType.User, ScriptMessageFlag.NpcReplacedByUser);
        }
    } else if (status == 2) {
        var ii =Packages.objects.item.MapleItemInformationProvider.getInstance();

        if (!Packages.objects.item.MapleItemInformationProvider.getInstance().isCash(sel)) {
            cm.sendOk("#fs11#오류가 발생 했어요.");
            cm.dispose();
            return;
        }

        item = Packages.objects.item.MapleItemInformationProvider.getInstance().getEquipById(sel);
        item.setStr(300);
        item.setDex(300);
        item.setInt(300);
        item.setLuk(300);
        item.setWatk(200);
        item.setMatk(200);
        Packages.objects.item.MapleInventoryManipulator.addbyItem(cm.getClient(), item, false);
        cm.gainItem(2431394, -1);
        cm.dispose();
        cm.sayNpc("#fs11#선택한 아이템이 지급되었다!", GameObjectType.User, false, false, ScriptMessageFlag.NpcReplacedByUser);
    }
}