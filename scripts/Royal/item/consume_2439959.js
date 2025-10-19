var status;
var select = -1;
var book  = new Array(1212129, 1213022, 1214022, 1222122, 1232122, 1242139, 1242141, 1262051, 1272040, 1282040, 1292022, 1302355, 1312213, 1322264, 1332289, 1362149, 1372237, 1382274, 1402268, 1412189, 1422197, 1432227, 1442285, 1452266, 1462252, 1472275, 1482232, 1492245, 1522152, 1532157, 1582044, 1592022, 1404022);

function start() {    status = -1;
    action(1, 1, 0);
}
function MakeItem(itemid) {
    //장비 아이템 옵션 부여하기
    var ii = Packages.objects.item.MapleItemInformationProvider.getInstance();
    var it = ii.getEquipById(itemid);
    it.setStr(1500);
    it.setDex(1500);
    it.setInt(1500);
    it.setLuk(1500);
    it.setWatk(1500);
    it.setMatk(1500);
    it.setCHUC(25);
    it.setAllStat(0);
    it.setTotalDamage(100);
    it.setBossDamage(100);
    it.setIgnorePDR(100);
    it.setLines(3);
    it.setState(20);
    it.setPotential1(40603);
    it.setPotential2(40603);
    it.setPotential3(40603);
    it.setPotential4(40603);
    it.setPotential5(40603);
    it.setPotential6(40603);
    Packages.objects.item.MapleInventoryManipulator.addFromDrop(cm.getPlayer().getClient(), it, false);
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }
    if (status == 0) {
    var text = "#fs11#받고 싶은 제네시스 무기 아이템을 선택해줘#l\r\n\r\n#r#e[ 데미지 + 100% 보공 + 100% 방무 + 100% ]\r\n※ 주의! 잘못 수령시 복구가 불가능합니다\r\n\#n#b";
    for (var i = 0; i < book.length; i++) {
        text+="#L"+i+"##i"+book[i]+"# #z"+book[i]+"##l\r\n";
    }
    cm.sendSimple(text);
    } else if (status == 1) {
        select = selection;
        cm.sendYesNo("#fs11#받을 제네시스 아이템이 #b#z"+book[select]+"##k 맞아?");
    } else if (status == 2) {
        if (cm.haveItem(2439959, 1)) {
            if (cm.canHold(book[select])) {
                cm.sendOk("#fs11#인벤토리를 확인하세요");
                cm.gainItem(2439959, -1);
                MakeItem(book[select]);
                //cm.gainSponserItem3(book[select], "", 1000, 1100, 0,  100,  100, 100, 0)
                cm.dispose();
            } else {
                cm.sendOk("장비칸에 빈 공간이 없습니다.");
                cm.dispose();
            }
        } else {
            cm.sendOk("?");
            cm.dispose();
        }
    }
}