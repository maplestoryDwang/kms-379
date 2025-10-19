importPackage(java.lang);

var equipped = false;

var banitem = [1712001, 1712002, 1712003, 1712004, 1712005, 1712006, 1713000 ,1713001, 1713002, 1713003, 1713004, 1713005];

var canowner = ["△", "★", "★△", "★★", "★★△", "★★★"];

function ConvertNumber(number) { //모 블로그 참조함, 이 부분에 대해서는 키네시스(kinesis8@nate.com), 라피스#2519 에게 저작권이 없음
    var inputNumber  = number < 0 ? false : number;
    var unitWords    = ['', '만 ', '억 ', '조 ', '경 '];
    var splitUnit    = 10000;
    var splitCount   = unitWords.length;
    var resultArray  = [];
    var resultString = '';
    if (inputNumber == false) {
        cm.sendOk("#fs11#오류가 발생하였습니다. 다시 시도해 주세요.\r\n(파싱오류)");
        cm.dispose();
        return;
    }
    for (var i = 0; i < splitCount; i++) {
        var unitResult = (inputNumber % Math.pow(splitUnit, i + 1)) / Math.pow(splitUnit, i);
        unitResult = Math.floor(unitResult);
        if (unitResult > 0){
            resultArray[i] = unitResult;
        }
    }
    for (var i = 0; i < resultArray.length; i++) {
        if(!resultArray[i]) continue;
        resultString = String(resultArray[i]) + unitWords[i] + resultString;
    }
    return resultString;
}

var status = -1;

var items = [ // 강화수, 강화성공확률, 강화메소, 올스탯, 공마, 붉구갯수, 푸구갯수, 올스탯%
    [0, 0, 0, 0, 0, 0, 0, 0],
    ["△", -1, -1, 1392, 975, -1, -1, 0],
    ["★", -1, -1, 1692, 1175, -1, -1, 1],
    ["★△", -1, -1, 2092, 1425, -1, -1, 2],
    ["★★", -1, -1, 2492, 1675, -1, -1, 4],
    ["★★△", -1, -1, 2992, 1975, -1, -1, 6],
    ["★★★", -1, -1-1, 3492, 2275, -1, -1, 10],
];

function getAddEnhance(item) {
    var owner = item.getOwner();
    return owner == "△" ? 1 : 
            owner == "★" ? 2 : 
            owner == "★△" ? 3 : 
            owner == "★★" ? 4 : 
            owner == "★★△" ? 5 : 
            owner == "★★★" ? 6 : 
            0; 
}

function getStar(star) {
    return star == 1 ? "△" : 
            star == 2 ? "★" : 
            star == 3 ? "★△" : 
            star == 4 ? "★★" : 
            star == 5 ? "★★△" : 
            star == 6 ? "★★★" : 
            "20강"; 
}

var keep = 10000000000; //파괴 방지 메소
var keep2 = 100; //파괴 방지 메소주머니
var item, itemid, slot, choice, say;
var re = 0;

function start () {
    status = -1;
    action (1, 0, 0);
}

function action (mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }
    if (status == 0) {
        var count = 0;
        var say = "#fs11#전승할 장비#k를 선택해 주세요\r\n\r\n#r초월 메소강화 아이템만 전승이 가능합니다#n#k\r\n\r\n#r#e※ 주의사항 ※\r\n장착된 아이템 - 장비 아이템부터 순으로 표기됩니다.#n#k#b\r\n\r\n";
        for (i = 0; i <= 50; i++) { // 장착중
            item = cm.getInventory(-1).getItem(i*-1)
            if (item != null) {
                // 강화 가능한 아이템만 출력
                if (canowner.indexOf(item.getOwner()) != -1) {
                    if (!cm.isCash(item.getItemId())) {
                        say += "#L" + (i + 100000) + "##e#b#i" + item.getItemId() + "# #z" + item.getItemId() + "# #r#e[장착중]#n#k#l\r\n";
                        count++;
                    }
                }
            }
        }

        for (i = 0; i <= cm.getInventory(1).getSlotLimit(); i++) {
            item = cm.getInventory(1).getItem(i)
            if (item != null) {
                // 강화 가능한 아이템만 출력
                if (canowner.indexOf(item.getOwner()) != -1) {
                    if (!cm.isCash(item.getItemId())) {
                        say += "#L" + i + "##e#b#i" + item.getItemId() + "# #z" + item.getItemId() + "# #r["+i+"슬롯]#k#l\r\n";
                        count++;
                    }
                }
            }
        }
        if (count <= 0) {
            cm.sendOk("#fs11#강화할 장비를 소지하고 있는지 확인해 주세요.\r\n초월 강화는 메소강화 20강이상 아이템만 가능합니다.");
            cm.dispose();
            return;
        }
        cm.sendSimple(say);
    } else if (status == 1) {
        if (re == 0) {
            slot = selection;
            if (slot > 10000) { // 장착중
                slot = -(slot - 100000)
                equipped = true;
            }
            if (equipped) { // 장착중
                item1 = cm.getInventory(-1).getItem(slot);
            } else {
                item1 = cm.getInventory(1).getItem(slot);
            }
        }

        if (canowner.indexOf(item1.getOwner()) == -1) {
            cm.sendOk("#fs11#초월강화된 아이템만 전승이 가능합니다");
            cm.dispose();
            return;
        }
        itemid = item1.getItemId();
        if (banitem.indexOf(itemid) != -1) {
            cm.sendOk("#fs11#심볼 아이템은 강화할 수 없습니다.");
            cm.dispose();
            return;
        }

        var say = "#fs11#전승받을 장비#k를 선택해 주세요\r\n\r\n#r메소강화가 진행되지않은 아이템만 전승이 가능합니다#n#k\r\n\r\n#r#e※ 주의사항 ※\r\n장착된 아이템 - 장비 아이템부터 순으로 표기됩니다.#n#k#b\r\n\r\n";
        for (i = 0; i <= 50; i++) { // 장착중
            item = cm.getInventory(-1).getItem(i*-1)
            if (item != null) {
                // 강화 가능한 아이템만 출력
                if (item.getOwner() == "") {
                    if (!cm.isCash(item.getItemId())) {
                        say += "#L" + (i + 100000) + "##e#b#i" + item.getItemId() + "# #z" + item.getItemId() + "# #r#e[장착중]#n#k#l\r\n";
                        count++;
                    }
                }
            }
        }

        for (i = 0; i <= cm.getInventory(1).getSlotLimit(); i++) {
            item = cm.getInventory(1).getItem(i)
            if (item != null) {
                // 강화 가능한 아이템만 출력
                 if (item.getOwner() == "") {
                    if (!cm.isCash(item.getItemId())) {
                        say += "#L" + i + "##e#b#i" + item.getItemId() + "# #z" + item.getItemId() + "# #r["+i+"슬롯]#k#l\r\n";
                        count++;
                    }
                }
            }
        }
        if (count <= 0) {
            cm.sendOk("#fs11#강화할 장비를 소지하고 있는지 확인해 주세요.\r\n초월 강화는 메소강화 20강이상 아이템만 가능합니다.");
            cm.dispose();
            return;
        }
        cm.sendSimple(say);
    } else if (status == 2) {
        if (re == 0) {
            equipped = false;
            slot = selection;
            if (slot > 10000) { // 장착중
                slot = -(slot - 100000)
                equipped = true;
            }
            if (equipped) { // 장착중
                item2 = cm.getInventory(-1).getItem(slot);
            } else {
                item2 = cm.getInventory(1).getItem(slot);
            }
        }
        
        if (item2.getOwner() != "") {
            cm.sendOk("#fs11#강화되지 않은 아이템만 전승을 받을 수 있습니다");
            cm.dispose();
            return;
        }
        
        itemid2 = item2.getItemId();
        if (banitem.indexOf(itemid2) != -1) {
            cm.sendOk("#fs11#심볼 아이템은 강화할 수 없습니다.");
            cm.dispose();
            return;
        }
        
        //강화수, 강화성공확률, 강화메소, 올스탯, 공마 
        say = "";
        say += "#fs 11#";
        say += "#r<전승 할 아이템 정보>\r\n";
        say += "#fc0xFF9A9A9A##i" + itemid + "# #z" + itemid + "#\r\n";
        say += "STR : " + item1.getStr() + "  |  DEX : " + item1.getDex() + "  |  INT : " + item1.getInt() + "  |  LUK " + item1.getLuk() + "\r\n";
        say += "공격력 : " + item1.getWatk() + "  |  마력 : " + item1.getMatk() + "  | 올스탯% : " + item1.getAllStat() + "% | 스타포스 : " + item1.getEnhance() + "성\r\n";
        say += "\r\n";
        say += "#r<전승 받을 아이템 정보>\r\n";
        say += "#fc0xFF9A9A9A##i" + itemid2 + "# #z" + itemid2 + "#\r\n";
        say += "STR : " + item2.getStr() + "  |  DEX : " + item2.getDex() + "  |  INT : " + item2.getInt() + "  |  LUK " + item2.getLuk() + "\r\n";
        say += "공격력 : " + item2.getWatk() + "  |  마력 : " + item2.getMatk() + "  | 올스탯% : " + item2.getAllStat() + "% | 스타포스 : " + item2.getEnhance() + "성\r\n";


        cm.sendYesNo(say + "#k\r\n#b정말 전승 하시겠습니까?");

    } else if (status == 3) {
        if (!cm.haveItem(2430048, 1)) {
            cm.sendOk("#fs11#전승권이 없네요");
            cm.dispose();
            return;
        }
        
        if (item1 != null && item2 != null) {
            cm.gainItem(2430048, -1);
            
            item2.setStr(item2.getStr() + items[getAddEnhance(item1)][3]);
            item2.setDex(item2.getDex() + items[getAddEnhance(item1)][3]);
            item2.setInt(item2.getInt() + items[getAddEnhance(item1)][3]);
            item2.setLuk(item2.getLuk() + items[getAddEnhance(item1)][3]);
            item2.setWatk(item2.getWatk() + items[getAddEnhance(item1)][4]);
            item2.setMatk(item2.getMatk() + items[getAddEnhance(item1)][4]);

            item2.setAllStat((item2.getAllStat() + items[getAddEnhance(item1)][7]));
            item2.setOwner(""+ getStar(getAddEnhance(item1)) +"");

            cm.getPlayer().send(Packages.network.models.CWvsContext.InventoryPacket.updateInventoryItem(Packages.objects.item.MapleInventoryType.EQUIP, item2, false, cm.getPlayer()));
            
            item1.setStr(item1.getStr() - items[getAddEnhance(item1)][3]);
            item1.setDex(item1.getDex() - items[getAddEnhance(item1)][3]);
            item1.setInt(item1.getInt() - items[getAddEnhance(item1)][3]);
            item1.setLuk(item1.getLuk() - items[getAddEnhance(item1)][3]);
            item1.setWatk(item1.getWatk() - items[getAddEnhance(item1)][4]);
            item1.setMatk(item1.getMatk() - items[getAddEnhance(item1)][4]);

            item1.setAllStat((item1.getAllStat() - items[getAddEnhance(item1)][7]));
            item1.setOwner("");

            cm.getPlayer().send(Packages.network.models.CWvsContext.InventoryPacket.updateInventoryItem(Packages.objects.item.MapleInventoryType.EQUIP, item1, false, cm.getPlayer()));
            
            say = "#fs11#";     
            say += "#r<전승이 완료된 아이템 정보>\r\n";
            say += "#fc0xFF9A9A9A##i" + itemid2 + "# #z" + itemid2 + "#\r\n";
            say += "STR : " + item2.getStr() + "  |  DEX : " + item2.getDex() + "  |  INT : " + item2.getInt() + "  |  LUK " + item2.getLuk() + "\r\n";
            say += "공격력 : " + item2.getWatk() + "  |  마력 : " + item2.getMatk() + "  | 올스탯% : " + item2.getAllStat() + "% | 스타포스 : " + item2.getEnhance() + "성\r\n";
        
            cm.sendOk("#fs11##b전승이 완료#k되었습니다.\r\n\r\n" + say);
            cm.dispose();
            return;
        } else {
            cm.dispose();
            return;
        }
    }
}