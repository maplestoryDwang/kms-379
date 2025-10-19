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
enter = "\r\n";

importPackage(Packages.objects.item);

var randitem = [
[1114001, "#fs11##i1114001# #fc0xFF000000#모험가님은 #fc0xFF000000#[검은색]#fc0xFF000000#이 잘 어울리시네요"],
[1114002, "#fs11##i1114002# #fc0xFF000000#모험가님은 #fc0xFFBEBEBE#[흰색]#fc0xFF000000#이 잘 어울리시네요"],
[1114003, "#fs11##i1114003# #fc0xFF000000#모험가님은 #fc0xFFFF0000#[빨간색]#fc0xFF000000#이 잘 어울리시네요"],
[1114004, "#fs11##i1114004# #fc0xFF000000#모험가님은 #fc0xFF0600FF#[파란색]#fc0xFF000000#이 잘 어울리시네요"],
[1114005, "#fs11##i1114005# #fc0xFF000000#모험가님은 #fc0xFF00FF30#[초록색]#fc0xFF000000#이 잘 어울리시네요"],
[1114006, "#fs11##i1114006# #fc0xFF000000#모험가님은 #fc0xFFCACC00#[노란색]#fc0xFF000000#이 잘 어울리시네요"],
[1114007, "#fs11##i1114007# #fc0xFF000000#모험가님은 #fc0xFF00FFEA#[하늘색]#fc0xFF000000#이 잘 어울리시네요"],
[1114008, "#fs11##i1114008# #fc0xFF000000#모험가님은 #fc0xFFFF7BF7#[분홍색]#fc0xFF000000#이 잘 어울리시네요"],
[1114009, "#fs11##i1114009# #fc0xFF000000#모험가님은 #fc0xFFFF9F16#[주황색]#fc0xFF000000#이 잘 어울리시네요"],
];

var randpet = [
[5002239, "#fs11##i5002239# #fc0xFF000000#모험가님은 #fc0xFF000000#[샤벳]#fc0xFF000000#이 잘 어울리시네요"],
[5002396, "#fs11##i5002396# #fc0xFF000000#모험가님은 #fc0xFFFF7BF7#[핑크빈]#fc0xFF000000#이 잘 어울리시네요"],
];

function start() {
     status = -1;    
     action (1, 0, 0);
}

function MakeItem(itemid) {
    var ii = Packages.objects.item.MapleItemInformationProvider.getInstance();
    var it = ii.getEquipById(itemid);
        it.setStr(400);
        it.setDex(400);
        it.setInt(400);
        it.setLuk(400);
        it.setWatk(200);
        it.setMatk(200);
        it.setCHUC(30);
        it.setAllStat(10);
        it.setOwner("로얄메이플");
        it.setExpiration((new Date()).getTime() + (1000 * 60 * 60 * 24 * 14));
        Packages.objects.item.MapleInventoryManipulator.addFromDrop(cm.getPlayer().getClient(), it, false);
}

function action(mode, type, sel) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }

    if (status == 0) {
            var msg = "　　　#i4317002# #fs14##e[로얄메이플] - 사탕 이벤트#n#fs11# #i4317002#\r\n#fs11##Cblue#                  항상 로얄 메이플을 이용해주셔서 감사합니다#k\r\n";
                    msg += 핑크색 + "                               #L100#이벤트안내#fc0xFF000000##l\r\n\r\n";
                    msg += "                      이벤트 기간 : #b08.15 ~ 08.31\r\n";
                    msg += "\r\n#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
                    msg += "#L1##fc0xFFFF3366#이벤트 반지#fc0xFF000000# 지급받기#l\r\n";
                    msg += "#L2##fc0xFFFF3366#이벤트 자석펫#fc0xFF000000# 지급받기#l\r\n";
                    msg += "#L3##fc0xFF6600CC#이벤트 아이템#fc0xFF000000# 상점#l\r\n";
                    msg += "#L4##fc0xFF6600CC#이벤트 아이템 수급처#fc0xFF000000# 확인#l\r\n";
                    msg += "\r\n#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
            cm.sendSimple(msg);
     } else if(status == 1) {
        seld = sel;
        switch(sel) {
            case 100:
            var msg = "#fn나눔고딕 Extrabold##d(사냥을하면 다양한 사탕들이 드롭되던데..)#k\r\n\r\n#b얼른 사냥을하고 사탕들을 모아야겠어!\r\n\r\n";
                    msg += "#L96##r그런데 이벤트 아이템어디에 사용하는거지..?#l\r\n";
            cm.sendNextS(msg, 2);
            break;

            case 1:
            if (cm.getClient().getKeyValue("candyeventitem") == null) {
                말 = "#fs11#" + 검은색;
                말 += "스탯이 부여된 다양한 이펙트 반지를 랜덤으로 얻을 수 있어요" + enter;
                말 += 핑크색 + "※계정당 1회, 15일 기간제로 지급됩니다" + enter + enter;
                말 += 핑크색 + "정말 받아보시겠어요?"
                cm.sendYesNo(말);
            } else {
                말 = "#fs11#" + 검은색 + "이미 #fc0xFFFF3366#[이벤트 반지]" + 검은색 + " 를 지급받으셨습니다";
                cm.dispose();
                cm.sendOk(말);
            }
            break;
            
            case 2:
            if (cm.getClient().getKeyValue("candyeventpet") == null) {
                말 = "#fs11#" + 검은색;
                말 += "귀염뽀짝한 자석펫을 랜덤으로 얻을 수 있어요" + enter;
                말 += 핑크색 + "※계정당 1회, 15일 기간제로 지급됩니다" + enter + enter;
                말 += 핑크색 + "정말 받아보시겠어요?"
                cm.sendYesNo(말);
            } else {
                말 = "#fs11#" + 검은색 + "이미 #fc0xFFFF3366#[이벤트 자석펫]" + 검은색 + " 을 지급받으셨습니다";
                cm.dispose();
                cm.sendOk(말);
            }
            break;

            case 3:
            cm.dispose();
            cm.openShop(7777);
            break;

            case 4:
            var msg = "#fs11#" + 색 + "이벤트 아이템 수급처 안내\r\n\r\n";
                    msg += "#fs11##b#i4317001##z4317001#\r\n#i4317002##z4317002#\r\n#i4317003##z4317003#\r\n\r\n";
                    msg += "#r#e[낚시]#b 시 일정 확률로 위 세가지 아이템 획득가능\r\n";
                    msg += "#r#e[사냥]#b 시 일정 확률로 위 세가지 아이템 획득가능";
            cm.sendOk(msg);
            cm.dispose();
            break;
        }
        
    } else if(status == 2) {
        switch(seld) {
            case 100:
                cm.sendOk("#fs11#" + 색 + "이벤트 상점에서 사탕들로 다양한 아이템을 구매할 수 있어요!");
                cm.dispose();
            break;
            
            case 1:
            a = Packages.objects.utils.Randomizer.rand(0, 8);
            
            if (!cm.canHold(randitem[a][0], 1)) {
                cm.sendOk("#fs11#인벤토리 공간이 부족합니다.");
                cm.dispose();
                return;
            }
            
            cm.getClient().setKeyValue("candyeventitem", "1");
            MakeItem(randitem[a][0]);
            cm.sendOk(randitem[a][1]);
            cm.dispose();
            break;
            
            case 2:
            b = Packages.objects.utils.Randomizer.rand(0, 1);
            
            if (!cm.canHold(randpet[b][0], 1)) {
                cm.sendOk("#fs11#인벤토리 공간이 부족합니다.");
                cm.dispose();
                return;
            }
            
            cm.getClient().setKeyValue("candyeventpet", "1");
            var item = new Item(randpet[b][0], 1, 1, 0);
            item.setExpiration((new Date()).getTime() + (1000 * 60 * 60 * 24 * 15));
            var pet = MaplePet.createPet(randpet[b][0], MapleInventoryIdentifier.getInstance());
            item.setPet(pet);
            item.setUniqueId(pet.getUniqueId());
            Packages.objects.item.MapleInventoryManipulator.addFromDrop(cm.getClient(), item, false);
            cm.sendOk(randpet[b][1]);
            cm.dispose();
            break;
        }
    }
}

