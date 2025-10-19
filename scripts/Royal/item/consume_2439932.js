importPackage(Packages.objects.item);

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

var itemcode = 2439932;
var pets = [5002561, 5002562, 5002563];

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
        var msg = "#fs11##b#i" + itemcode +"##z" + itemcode + "##k를 사용하시겠습니까?  \r\n#b" + enter;
        msg += "#L1##b사용하기" + enter;

        cm.sendSimple(msg);
    } else if (status == 1) {
        var msg = "#fs11#받고싶은 #b펫#k을 선택 해주세요" + enter;
        for (var i = 0; i < pets.length; i++)
            msg += "#L" + i + "##i" + pets[i] + "##b#z" + pets[i] + "##k" + enter;
        cm.sendSimple(msg);
    } else if (status == 2) {
        seld = sel;
        cm.sendYesNo("#fs11#정말로 #i" + pets[seld] + "##b#z" + pets[seld] + "##k을(를) 받으시겠습니까?");
    } else if (status == 3) {
        cm.sendGetNumber("#fs11#" + 검은색 + "#i " + pets[seld] + "# #b#z" + pets[seld] + "#" + 검은색 + " 을(를) 몇번 구매 하시겠습니까?\r\n" + 핑크색 + "1번당 15일 입니다 1 = 15일, 2 = 30일... 최대150일", 1, 1, 10);
    } else if (status == 4) {
        count = sel;
        cm.sendYesNo("#fs11#지급받으실 펫을 확인하세요\r\n #i" + pets[seld] + "##b#z" + pets[seld] + "# - " + count * 15 + "일#k\r\n\r\n" + 핑크색 + "해당 펫을 지급받으시겠습니까?");
    } else if (status == 5) {
        if (!cm.haveItem(itemcode, count)) {
            cm.sendOk("#fs11#상자가 부족합니다");
            cm.dispose();
            return;
        }

        var item = new Item(pets[seld], 1, 1, 0);
        item.setExpiration((new Date()).getTime() + (1000 * 60 * 60 * 24 * 15 * count));
        var pet = MaplePet.createPet(pets[seld], MapleInventoryIdentifier.getInstance());
        item.setPet(pet);
        item.setUniqueId(pet.getUniqueId());
        Packages.objects.item.MapleInventoryManipulator.addFromDrop(cm.getClient(), item, false);
        cm.sendOk("#fs11#교환이 완료되었습니다.");
        cm.gainItem(itemcode, -count);
        cm.dispose();
    }
}