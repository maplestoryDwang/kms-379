importPackage(java.lang);

var equipped = false;

var banitem = [1712001, 1712002, 1712003, 1712004, 1712005, 1712006, 1713000 ,1713001, 1713002, 1713003, 1713004, 1713005];

var canowner = ["", "1강", "2강", "3강", "4강", "5강", "6강", "7강", "8강", "9강", "10강", "11강", "12강", "13강", "14강", "15강", "16강", "17강", "18강", "19강", "20강"];

var cantowner = ["△", "★", "★△", "★★", "★★△", "★★★"];

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

var items = [ //강화수, 강화성공확률, 강화메소, 올스탯, 공마
    [0, 0, 0, 0, 0],
    [1, 100, 15000000, 5, 5],
    [2, 100, 15000000, 5, 5],
    [3, 90, 29500000, 5, 5],
    [4, 90, 37000000, 7, 5],
    [5, 80, 45000000, 7, 5],
    [6, 60, 1, 9, 5],
    [7, 50, 2, 11, 6],
    [8, 40, 3, 13, 9],
    [9, 30, 3, 15, 10],
    [10, 30, 5, 15, 15],
    [11, 25, 7, 20, 20],
    [12, 20, 9, 20, 20],
    [13, 20, 11, 30, 25],
    [14, 15, 15, 35, 30],
    [15, 9, 17, 35, 30],
    [16, 7, 19, 60, 30],
    [17, 5, 25, 100, 50],
    [18, 5, 30, 150, 100],
    [19, 3, 40, 250, 150],
    [20, 2, 50, 300, 250],
];

function getAddEnhance(item) {
    var owner = item.getOwner();
    return owner == "1강" ? 1 : 
        owner == "2강" ? 2 : 
        owner == "3강" ? 3 : 
        owner == "4강" ? 4 : 
        owner == "5강" ? 5 : 
        owner == "6강" ? 6 : 
        owner == "7강" ? 7 : 
        owner == "8강" ? 8 : 
        owner == "9강" ? 9 : 
        owner == "10강" ? 10 : 
        owner == "11강" ? 11 : 
        owner == "12강" ? 12 : 
        owner == "13강" ? 13 : 
        owner == "14강" ? 14 : 
        owner == "15강" ? 15 :
        owner == "16강" ? 16 :
        owner == "17강" ? 17 :
        owner == "18강" ? 18 :
        owner == "19강" ? 19 :
        owner == "20강" ? 20 :
            0; 
}

var keep = 300000000; //파괴 방지 메소
var keep2 = 3; //파괴 방지 메소주머니
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
        var say = "#fs11##b강화할 장비#k를 선택해 주세요\r\n\r\n#r스타포스와는 별개의 강화 시스템#k입니다.#n#k\r\n\r\n※ 일반 강화는 20강 까지 가능하며\r\n20강부터 초월강화를 시도할 수 있습니다\r\n\r\n#r#e※ 주의사항 ※\r\n장착된 아이템 - 장비 아이템부터 순으로 표기됩니다.#n#k#b\r\n\r\n";
        for (i = 0; i <= 50; i++) { // 장착중
            item = cm.getInventory(-1).getItem(i*-1)
            if (item != null) {
                // 강화 가능한 아이템만 출력
                if (canowner.indexOf(item.getOwner()) != -1) {
                    if (!cm.isCash(item.getItemId())) {
                        say += "#L" + (i+100000) + "##b#i" + item.getItemId() + "# #z" + item.getItemId() + "# #r#e[장착중]#k#n#l\r\n";
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
                        say += "#L" + i + "##b#i" + item.getItemId() + "# #z" + item.getItemId() + "# #r["+i+"슬롯]#k#l\r\n";
                        count++;
                    }
                }
            }
        }
        if (count <= 0) {
            cm.sendOk("#fs11#강화할 수 있는 장비가 없네요");
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
                item = cm.getInventory(-1).getItem(slot);
            } else {
                item = cm.getInventory(1).getItem(slot);
            }
        }

        if (item.getOwner().equals("20강")) {
            cm.sendOk("#fs11#이미 20강까지 강화가 완료된 아이템 입니다.");
            cm.dispose();
            return;
        }
        if (cantowner.indexOf(item.getOwner()) != -1) {
            cm.sendOk("#fs11#이미 초월 강화가 진행된 아이템 입니다.");
            cm.dispose();
            return;
        }
        if (item.getOwner().equals("[홍보]")) {
            cm.sendOk("#fs11#홍보 아이템은 강화할 수 없습니다.");
            cm.dispose();
            return;
        }

        itemid = item.getItemId();
        
        if (banitem.indexOf(itemid) != -1) {
            cm.sendOk("#fs11#심볼 아이템은 강화할 수 없습니다.");
            cm.dispose();
            return;
        }

        //강화수, 강화성공확률, 강화메소, 올스탯, 공마 
        var notice = "";
        say = "";
        say += "#fs 11#";
        say += "#r강화 : +" + getAddEnhance(item) + "강 -> +" + (getAddEnhance(item) + 1) + "강#k\r\n";
        say += "강화 성공 시 #b올스탯 +" + items[getAddEnhance(item) + 1][3] + ", 공마 +" + items[getAddEnhance(item) + 1][4] + "#k 증가\r\n";
        if (items[getAddEnhance(item) + 1][2] > 100) // 메소일경우
        say += "강화에 필요한 메소 :#b " + ConvertNumber(items[getAddEnhance(item) + 1][2]) + "메소#k\r\n";
        if (items[getAddEnhance(item) + 1][2] < 100)  // 메소주머니일경우
        say += "강화에 필요한 메소 :#b #z4001715# " + items[getAddEnhance(item) + 1][2] + "개#k\r\n";
        say += "#b성공확률 " + items[getAddEnhance(item) + 1][1] + "%#k, #r실패 시 아이템 등급 하락#k\r\n\r\n";
        say += "#r<아이템 정보>\r\n";
        say += "#fc0xFF9A9A9A##i" + itemid + "# #z" + itemid + "#\r\n";
        say += "STR : " + item.getStr() + "  |  DEX : " + item.getDex() + "  |  INT : " + item.getInt() + "  |  LUK " + item.getLuk() + "\r\n";
        say += "공격력 : " + item.getWatk() + "  |  마력 : " + item.getMatk() + "  | 스타포스 : " + item.getEnhance() + "성\r\n";
        cm.sendSimple(notice + say +
        "#L1##b" + ConvertNumber(keep) + "메소를 사용하여 등급 하락을 보호 하겠습니다.#l\r\n" +
        "#L2#등급 하락 보호를 사용하지 않고 강화하겠습니다.#k#l");
    } else if (status == 2) {
        if (re == 0) {
            choice = selection;
        }
        if (item.getOwner().equals("20강")) {
            cm.sendOk("#fs11#이미 20강까지 강화가 완료된 아이템 입니다.");
            cm.dispose();
            return;
        }
        //강화수, 강화성공확률, 강화메소, 올스탯, 공마 
        say = "";
        say += "#fs 11#";
        say += "강화 : #b+" + getAddEnhance(item) + "강 -> +" + (getAddEnhance(item) + 1) + "강#k\r\n";
        say += "강화 성공 시 #b올스탯 +" + items[getAddEnhance(item) + 1][3] + ", 공마 +" + items[getAddEnhance(item) + 1][4] + "#k 증가\r\n";
        if (items[getAddEnhance(item) + 1][2] > 100) // 메소일경우
        say += "강화에 필요한 메소 :#b " + ConvertNumber(items[getAddEnhance(item) + 1][2]) + "메소#k\r\n";
        if (items[getAddEnhance(item) + 1][2] < 100) // 메소주머니일경우
        say += "강화에 필요한 메소 :#b #z4001715# " + items[getAddEnhance(item) + 1][2] + "개#k\r\n";
        say += "#b성공확률 " + items[getAddEnhance(item) + 1][1] + "%#k";
        if (selection == 1 || choice == 1) {
            say += ", #b등급 하락 보호중#k\r\n\r\n";
        } else if (selection == 2 || choice == 2) {
            say += ", #r실패시 아이템 등급 하락#k\r\n\r\n";
        }   
        say += "#r<아이템 정보>\r\n";
        say += "#fc0xFF9A9A9A##i" + itemid + "# #z" + itemid + "#\r\n";
        say += "STR : " + item.getStr() + "  |  DEX : " + item.getDex() + "  |  INT : " + item.getInt() + "  |  LUK " + item.getLuk() + "\r\n";
        say += "공격력 : " + item.getWatk() + "  |  마력 : " + item.getMatk() + "  | 스타포스 : " + item.getEnhance() + "성\r\n";
        if (selection == 1 || choice == 1) {
            if (items[getAddEnhance(item) + 1][2] > 100) // 메소일경우
               cm.sendYesNo(say + "\r\n정말로 " + ConvertNumber(keep) + "메소를 사용하여 등급 하락을 보호 하시겠습니까?\r\n" + "성공/실패 여부와 상관없이 등급 하락 보호 메소는 소모됩니다.");
            if (items[getAddEnhance(item) + 1][2] < 100) // 메소주머니일경우
                cm.sendYesNo(say + "\r\n정말로 " + ConvertNumber(keep) + "메소를 사용하여 등급 하락을 보호 하시겠습니까?\r\n" + "성공/실패 여부와 상관없이 등급 하락 보호 메소는 소모됩니다.");
        } else if (selection == 2 || choice == 2) {
            cm.sendYesNo(say + "\r\n정말로 " + ConvertNumber(keep) + "메소를 사용하지 않고 강화를 시도하시겠습니까?\r\n" + "강화 실패시 강화 등급이 하락합니다.");
        }
    } else if (status == 3) {
        if (choice == 1) {
            if (items[getAddEnhance(item) + 1][2] > 100) { // 메소일경우
                if (cm.getPlayer().getMeso() >= (items[getAddEnhance(item) + 1][2] + keep)) {
                    cm.gainMeso(-(items[getAddEnhance(item) + 1][2] + keep));
                } else {
                    cm.sendOk("#fs11#메소가 부족하여 보호 강화가 불가능합니다.\r\n총 필요 강화 메소 : " + ConvertNumber(items[getAddEnhance(item) + 1][2] + keep) + "메소");
                    cm.dispose();
                    return;
                }
            } else { // 메소주머니일경우
                if (cm.haveItem(4001715, items[getAddEnhance(item) + 1][2] + keep2)) {
                    cm.gainItem(4001715, -(items[getAddEnhance(item) + 1][2] + keep2));
                } else {
                    cm.sendOk("#fs11#메소가 부족하여 보호 강화가 불가능합니다.\r\n총 필요 강화 메소 : #z4001715# " + (items[getAddEnhance(item) + 1][2] + keep2) + "개");
                    cm.dispose();
                    return;
                }
            }
        } else if (choice == 2) {
            if (items[getAddEnhance(item) + 1][2] > 100) { // 메소일경우
                if (cm.getPlayer().getMeso() >= items[getAddEnhance(item) + 1][2]) {
                    cm.gainMeso(-items[getAddEnhance(item) + 1][2]);
                } else {
                    cm.sendOk("#fs11#메소가 부족하여 강화가 불가능합니다.");
                    cm.dispose();
                    return;
                }
            } else { // 메소주머니일경우
                if (cm.haveItem(4001715, items[getAddEnhance(item) + 1][2])) {
                    cm.gainItem(4001715, -items[getAddEnhance(item) + 1][2]);
                } else {
                    cm.sendOk("#fs11#메소가 부족하여 강화가 불가능합니다.");
                    cm.dispose();
                    return;
                }
            }
        } else {
            cm.dispose();
            return;
        }
        if (item != null) {
            var rand = Math.ceil(Math.random() * 100);
            if (rand >= 0 && rand <= items[getAddEnhance(item) + 1][1]) {
                item.addStr(items[getAddEnhance(item) + 1][3]);
                item.addDex(items[getAddEnhance(item) + 1][3]);
                item.addInt(items[getAddEnhance(item) + 1][3]);
                item.addLuk(items[getAddEnhance(item) + 1][3]);
                item.addWatk(items[getAddEnhance(item) + 1][4]);
                item.addMatk(items[getAddEnhance(item) + 1][4]);
                item.setOwner(""+(getAddEnhance(item)+1)+"강");
                cm.getPlayer().send(Packages.network.models.CWvsContext.InventoryPacket.updateInventoryItem(Packages.objects.item.MapleInventoryType.EQUIP, item, false, cm.getPlayer()));

                say = "#fs11#";     
                say += "#r<아이템 정보>\r\n";
                say += "#fc0xFF9A9A9A##i" + itemid + "# #z" + itemid + "#\r\n";
                say += "STR : " + item.getStr() + "  |  DEX : " + item.getDex() + "  |  INT : " + item.getInt() + "  |  LUK " + item.getLuk() + "\r\n";
                say += "공격력 : " + item.getWatk() + "  |  마력 : " + item.getMatk() + "  | 스타포스 : " + item.getEnhance() + "성\r\n";
                cm.sendYesNo("#fs11##b강화에 성공#k하였습니다.\r\n계속 강화하시려면 '예'를 눌러 주세요.\r\n\r\n" + say);

                cm.addEnchantLog(1, item.getItemId(), item.getSerialNumberEquip(), 10, 0, "일반 메소 강화 " + (getAddEnhance(item) - 1) + "->" + getAddEnhance(item) + "강 성공 (계정 : " + cm.getClient().getAccountName() + ", 캐릭터 : " + cm.getPlayer().getName() + ", " + itemid + " [" + item.toString() + "])");

                re = 1;
                status = 1;
            } else {
                if (choice == 1 || getAddEnhance(item) == 0) {
                    say = "#fs11#";
                    say += "#r<아이템 정보>\r\n";
                    say += "#fc0xFF9A9A9A##i" + itemid + "# #z" + itemid + "#\r\n";
                    say += "STR : " + item.getStr() + "  |  DEX : " + item.getDex() + "  |  INT : " + item.getInt() + "  |  LUK " + item.getLuk() + "\r\n";
                    say += "공격력 : " + item.getWatk() + "  |  마력 : " + item.getMatk() + "  | 스타포스 : " + item.getEnhance() + "성\r\n";
                    cm.sendYesNo("#fs11##r강화에 실패#k하였지만 " + ConvertNumber(keep) + "메소를 사용하여\r\n#r강화 등급#k이 보호되었습니다.\r\n계속 강화하시려면 '예'를 눌러 주세요.\r\n\r\n" + say);

                    cm.addEnchantLog(1, item.getItemId(), item.getSerialNumberEquip(), 10, 1, "일반 메소 강화 " + getAddEnhance(item) + "->" + (getAddEnhance(item) + 1) + "강 실패 보호 (계정 : " + cm.getClient().getAccountName() + ", 캐릭터 : " + cm.getPlayer().getName() + ", " + itemid + " [" + item.toString() + "])");

                    re = 1;
                    status = 1;
                } else if (choice == 2) {
                    item.setStr(item.getStr() - items[getAddEnhance(item)][3]);
                    item.setDex(item.getDex() - items[getAddEnhance(item)][3]);
                    item.setInt(item.getInt() - items[getAddEnhance(item)][3]);
                    item.setLuk(item.getLuk() - items[getAddEnhance(item)][3]);
                    item.setWatk(item.getWatk() - items[getAddEnhance(item)][4]);
                    item.setMatk(item.getMatk() - items[getAddEnhance(item)][4]);

                    item.setOwner(""+(getAddEnhance(item) - 1)+"강");

                    cm.getPlayer().send(Packages.network.models.CWvsContext.InventoryPacket.updateInventoryItem(Packages.objects.item.MapleInventoryType.EQUIP, item, false, cm.getPlayer()));

                    say = "#fs11#";     
                    say += "#r<아이템 정보>\r\n";
                    say += "#fc0xFF9A9A9A##i" + itemid + "# #z" + itemid + "#\r\n";
                    say += "STR : " + item.getStr() + "  |  DEX : " + item.getDex() + "  |  INT : " + item.getInt() + "  |  LUK " + item.getLuk() + "\r\n";
                    say += "공격력 : " + item.getWatk() + "  |  마력 : " + item.getMatk() + "  | 스타포스 : " + item.getEnhance() + "성\r\n";
                    cm.sendYesNo("#fs11##r강화에 실패#k하여 #r강화 등급#k이 하락하였습니다.\r\n계속 강화하시려면 '예'를 눌러 주세요.\r\n\r\n" + say);

                    cm.addEnchantLog(1, item.getItemId(), item.getSerialNumberEquip(), 10, 1, "일반 메소 강화 " +(getAddEnhance(item) - 1) + "->" + getAddEnhance(item) + "강 실패 하락 (계정 : " + cm.getClient().getAccountName() + ", 캐릭터 : " + cm.getPlayer().getName() + ", " + itemid + " [" + item.toString() + "])");

                    re = 1;
                    status = 1;
                } else {
                    cm.dispose();
                    return;
                }
            }
        } else {
            cm.dispose();
            return;
        }
    } else {
        cm.dispose();
        return;
    }
}