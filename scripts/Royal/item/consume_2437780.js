weapon = [1212129, 1213022, 1214022, 1222122, 1232122, 1242139, 1242141, 1262051, 1272040, 1282040, 1292022, 1302355, 1312213, 1322264, 1332289, 1362149, 1372237, 1382274, 1402268, 1412189, 1422197, 1432227, 1442285, 1452266, 1462252, 1472275, 1482232, 1492245, 1522152, 1532157, 1582044, 1592022, 1404022];


var potential = [
    ["보스공격력 40%", 40603],
    ["공격력 12%", 40051], //공격력 12%
    ["마력 12%", 40052], //마력 12%
    ["공격력 12%", 40051], //공격력 9%
    ["마력 12%", 40052] //마력9%
];

function MakeItem(itemid) {
    //장비 아이템 옵션 부여하기
    var ii = Packages.objects.item.MapleItemInformationProvider.getInstance();
    var it = ii.getEquipById(itemid);
	it.setExpiration((new Date()).getTime() + (1000 * 60 * 60 * 24 * 30));
    it.setStr(1000);
    it.setDex(1000);
    it.setInt(1000);
    it.setLuk(1000);
    it.setWatk(1100);
    it.setMatk(1100);
    it.setCHUC(22);
    it.setBossDamage(50);
    it.setIgnorePDR(50);
    it.setLines(3);
    it.setState(20);
    it.setPotential1(potential[p1][1]);
    it.setPotential2(potential[p2][1]);
    it.setPotential3(potential[p3][1]);
    it.setPotential4(potential[p4][1]);
    it.setPotential5(potential[p5][1]);
    it.setPotential6(potential[p6][1]);
    Packages.objects.item.MapleInventoryManipulator.addFromDrop(cm.getPlayer().getClient(), it, false);
}

var p1 = 0, p2 = 0, p3 = 0, p4 = 0, p5 = 0, p6 = 0;
var item = 2437780;
var say = "";
var choice = 0;

var status = -1;

function start() {
    action (1, 0, 0);
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
        var text = "#fs11#30일 동안 사용할 수 있는 제네시스 무기를 선택할 수 있습니다."
		text += "\r\n\r\n#r#e※ 수령한 제네시스 무기는 어떠한 경우에도 다른 종류로 교환하거나 복구할 수 없으니, 반드시 30일 동안 사용할 직업군의 무기를 수령하시기 바랍니다.#k#n#b\r\n\r\n";
        for (var i = 0; i < weapon.length; i++) {
            text += "#L" + i + "##i" + weapon[i] + "##z" + weapon[i] + "##l\r\n";
        }
        cm.sendSimple(text);
    } else if (status == 1) {
        choice = selection;
        say += "   #fs11##b선택#k : #r#i" + weapon[choice] + "##z" + weapon[choice] + "##k\r\n\r\n";
        var text = "   첫 번째 잠재능력의 옵션을 선택하세요.\r\n\r\n#b";
        for (var i = 0; i < 3; i++) {
            text += "#L" + i + "#" + potential[i][0] + "#l\r\n";
        }
        cm.sendSimple(say + text);
    } else if (status == 2) {
        p1 = selection;
        say += "   #b#fs11#첫 번째 잠재능력 : " + potential[p1][0] + "#k\r\n";
        var text = "\r\n   두 번째 잠재능력의 옵션을 선택하세요.\r\n#b";
        for (var i = 0; i < 3; i++) {
            text += "#L" + i + "#" + potential[i][0] + "#l\r\n";
        }
        cm.sendSimple(say + text);
    } else if (status == 3) {
        p2 = selection;
        say += "   #b#fs11#두 번째 잠재능력 : " + potential[p2][0] + "#k\r\n";
        var text = "\r\n   세 번째 잠재능력의 옵션을 선택하세요.\r\n#b";
        for (var i = 3; i < 5; i++) {
            text += "#L" + i + "#" + potential[i][0] + "#l\r\n";
        }
        cm.sendSimple(say + text);
    } else if (status == 4) {
        p3 = selection;
        say += "   #b#fs11#세 번째 잠재능력 : " + potential[p3][0] + "#k\r\n";
        var text = "\r\n   첫 번째 에디셔널 잠재능력의 옵션을 선택하세요.\r\n#b";
        for (var i = 1; i < 3; i++) {
            text += "#L" + i + "#" + potential[i][0] + "#l\r\n";
        }
        cm.sendSimple(say + text);
    } else if (status == 5) {
        p4 = selection;
        say += "   #b#fs11#첫 번째 에디셔널 잠재능력 : " + potential[p4][0] + "#k\r\n";
        var text = "\r\n   두 번째 에디셔널 잠재능력의 옵션을 선택하세요.\r\n#b";
        for (var i = 1; i < 3; i++) {
            text += "#L" + i + "#" + potential[i][0] + "#l\r\n";
        }
        cm.sendSimple(say + text);
    } else if (status == 6) {
        p5 = selection;
        say += "   #b#fs11#두 번째 에디셔널 잠재능력 : " + potential[p5][0] + "#k\r\n";
        var text = "\r\n   세 번째 에디셔널 잠재능력의 옵션을 선택하세요.\r\n#b";
        for (var i = 3; i < 5; i++) {
            text += "#L" + i + "#" + potential[i][0] + "#l\r\n";
        }
        cm.sendSimple(say + text);
    } else if (status == 7) {
        p6 = selection;
        say += "   #b#fs11#세 번째 에디셔널 잠재능력 : " + potential[p6][0] + "\r\n";
        say += "   올스탯 + 1000, 공마 + 1100, 보스 공격력 50%\r\n   방어력 무시 50% 부여#k\r\n\r\n";
        cm.sendYesNo(say + "   정말로 위 조건의 제네시스 무기를 뽑으시겠습니까?\r\n\r\n   #b#i" + item + "##z" + item + "# 1개#k가 소모됩니다.");
    } else if (status == 8) {
        if (cm.haveItem(2437780, 1)) {
            cm.gainItem(2437780, -1);
            MakeItem(weapon[choice]);
            cm.sendOk("#fs11##i" + weapon[choice] + "##z" + weapon[choice] + "# 제작이 완료되었습니다.");
            //Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.packet.CWvsContext.serverNotice(2, "", "[제네시스] : " + cm.getPlayer().getName() + " 유저가 해방된 제네시스 무기를 제작하셨습니다."));
            cm.dispose();
            return;
        } else {
            cm.sendOk("#fs11##i" + item + "##z" + item + "#를 가지고 잇는지 확인해 주세요.\r\n혹시 장비 인벤토리 슬롯이 꽉 찬 것은 아닌지 확인해 주세요.");
            cm.dispose();
            return;
        }
    } else {
        cm.dispose();
        return;
    }
}