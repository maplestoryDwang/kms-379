importPackage(java.lang);

var equipped = false;

var cantowner = ["△", "★", "★△", "★★", "★★△", "★★★"];

var canowner = ["1강", "2강", "3강", "4강", "5강", "6강", "7강", "8강", "9강", "10강", "11강", "12강", "13강", "14강", "15강", "16강", "17강", "18강", "19강", "20강"];

var banitem = [1712001, 1712002, 1712003, 1712004, 1712005, 1712006, 1713000 ,1713001, 1713002, 1713003, 1713004, 1713005];

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
    [1, 100, 0, -5, -5],
    [2, 100, 0, -10, -10],
    [3, 100, 0, -15, -15],
    [4, 100, 0, -22, -20],
    [5, 100, 0, -29, -25],
    [6, 100, 0, -38, -30],
    [7, 100, 0, -49, -36],
    [8, 100, 0, -62, -45],
    [9, 100, 0, -77, -55],
    [10, 100, 0, -92, -70],
    [11, 100, 0, -112, -90],
    [12, 100, 0, -132, -110],
    [13, 100, 0, -162, -135],
    [14, 100, 0, -197, -165],
    [15, 100, 0, -232, -195],
    [16, 100, 0, -292, -225],
    [17, 100, 0, -392, -275],
    [18, 100, 0, -542, -375],
    [19, 100, 0, -792, -525],
    [20, 100, 0, -1092, -775],
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

var keep = 3000000000; //파괴 방지 메소
var item, itemid, slot, choice, say;
var re = 0;

function start () {
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
        var say = "#fs11##b초기화할 장비#k를 선택해 주세요\r\n\r\n#r메소강화 초기화 시스템입니다.#k입니다 \r\n#r#e초기화시 강화 주문서를 드리지않습니다.\r\n\r\n#r#e※ 주의사항 ※\r\n장착된 아이템 - 장비 아이템부터 순으로 표기됩니다.#n#k#b\r\n\r\n";

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
            cm.sendOk("#fs11#초기화할 수 있는 장비가 없네요");
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
        if (item.getOwner().equals("")) {
            cm.sendOk("#fs11#메소강화가 된 아이템이 아닙니다.");
            cm.dispose();
            return;
        }
        itemid = item.getItemId();
        //강화수, 강화성공확률, 강화메소, 올스탯, 공마 
        var notice = "";
        say = "";
        say += "#fs 11#";
        say += "#r강화 #b: +" + getAddEnhance(item) + "강 -> 0강#k\r\n";
        say += "초기화 시 #b올스탯 " + items[getAddEnhance(item)][3] + ", 공마 " + items[getAddEnhance(item)][4] + "#k\r\n";      
        say += "#r<아이템 정보>\r\n";
        say += "#fc0xFF9A9A9A#강화할 아이템 : #i" + itemid + "# #z" + itemid + "#\r\n";
        say += "STR : " + item.getStr() + "  |  DEX : " + item.getDex() + "  |  INT : " + item.getInt() + "  |  LUK " + item.getLuk() + "\r\n";
        say += "공격력 : " + item.getWatk() + "  |  마력 : " + item.getMatk() + "  | 스타포스 : " + item.getEnhance() + "성\r\n";
        say += "올 스탯 : " + item.getAllStat() + "%  |  총 데미지 : " + item.getTotalDamage() + "%  |  보스 공격력 : " + item.getBossDamage() + "%\r\n";
        //say += "아이템 강화 횟수 : " + getAddEnhance(item) + "번#k\r\n\r\n\r\n";
        cm.sendSimple(notice + say +
        //"#L1##b" + ConvertNumber(keep) + "메소를 사용하여 파괴, 미끄러짐을 방지 하겠습니다.#l\r\n" +
        "#L2#초기화 하겠습니다.#k#l");
    } else if (status == 2) {
        if (re == 0) {
            choice = selection;
        }
        if (item.getOwner().equals("")) {
            cm.sendOk("#fs11#메소강화가 된 아이템이 아닙니다.");
            cm.dispose();
            return;
        }
        //강화수, 강화성공확률, 강화메소, 올스탯, 공마 
        say = "";
        say += "#fs 11#";
        say += "#r강화 #b: +" + getAddEnhance(item) + "강 -> 0강#k\r\n";
        say += "초기화시 #b올스탯 " + items[getAddEnhance(item)][3] + ", 공마 " + items[getAddEnhance(item)][4] + "#k 증가\r\n";
        say += "초기화에 필요한 메소 : #b" + items[getAddEnhance(item)][2] + "메소#k\r\n";
        say += "#b성공확률 100%#k";
        if (selection == 1 || choice == 1) {
            say += "\r\n";
        } else if (selection == 2 || choice == 2) {
            say += "\r\n";
        }   
        say += "#r<아이템 정보>\r\n";
        say += "#fc0xFF9A9A9A#강화할 아이템 : #i" + itemid + "# #z" + itemid + "#\r\n";
        say += "STR : " + item.getStr() + "  |  DEX : " + item.getDex() + "  |  INT : " + item.getInt() + "  |  LUK " + item.getLuk() + "\r\n";
        say += "공격력 : " + item.getWatk() + "  |  마력 : " + item.getMatk() + "  | 스타포스 : " + item.getEnhance() + "성\r\n";
        say += "올 스탯 : " + item.getAllStat() + "%  |  총 데미지 : " + item.getTotalDamage() + "%  |  보스 공격력 : " + item.getBossDamage() + "%\r\n";
        //say += "아이템 강화 횟수 : " + getAddEnhance(item) + "번#k\r\n\r\n\r\n";
        if (selection == 1 || choice == 1) {
            cm.sendYesNo(say + "정말로 " + ConvertNumber(keep) + "메소를 사용하여 미끄러짐을 방지 하시겠습니까?\r\n" +
            "성공/실패 여부와 상관없이 미끄러짐 방지 메소는 소모됩니다.\r\n\r\n#b총 필요 메소 : " + ConvertNumber(items[getAddEnhance(item) + 1][2] + keep) + "메소#k#l");
        } else if (selection == 2 || choice == 2) {
            cm.sendYesNo(say + "정말로  초기화 하시겠습니까?");
        }
    } else if (status == 3) {
        if (choice == 1) {
            if (cm.getPlayer().getMeso() >= 0) {
                cm.gainMeso(-(items[getAddEnhance(item) + 1][2] + keep));
            } else {
                cm.sendOk("#fs11#메소가 부족하여 보호 강화가 불가능합니다.\r\n총 필요 강화 메소 : " + ConvertNumber(items[getAddEnhance(item) + 1][2] + keep) + "메소");
                cm.dispose();
                return;
            }
        } else if (choice == 2) {
            if (cm.getPlayer().getMeso() >= 0) {
                //cm.gainMeso(0);
            } else {
                cm.sendOk("#fs11#메소가 부족하여 강화가 불가능합니다.");
                cm.dispose();
                return;
            }
        } else {
            cm.dispose();
            return;
        }        
        if (item != null) {
            var rand = Math.ceil(Math.random() * 100);
            if (rand >= 0 && rand <= items[getAddEnhance(item)][1]) {
                item.setStr(item.getStr() + items[getAddEnhance(item)][3]);
                item.setDex(item.getDex() + items[getAddEnhance(item)][3]);
                item.setInt(item.getInt() + items[getAddEnhance(item)][3]);
                item.setLuk(item.getLuk() + items[getAddEnhance(item)][3]);
                item.setWatk(item.getWatk() + items[getAddEnhance(item)][4]);
                item.setMatk(item.getMatk() + items[getAddEnhance(item)][4]);
                item.setOwner("");
                cm.getPlayer().send(Packages.network.models.CWvsContext.InventoryPacket.updateInventoryItem(Packages.objects.item.MapleInventoryType.EQUIP, item, false, cm.getPlayer()));
                say = "";     
                say += "#fs11##r<아이템 정보>\r\n";
                say += "#fc0xFF9A9A9A#아이템 : #i" + itemid + "# #z" + itemid + "#\r\n";
                say += "STR : " + item.getStr() + "  |  DEX : " + item.getDex() + "  |  INT : " + item.getInt() + "  |  LUK " + item.getLuk() + "\r\n";
                say += "공격력 : " + item.getWatk() + "  |  마력 : " + item.getMatk() + "  | 스타포스 : " + item.getEnhance() + "성\r\n";
                say += "올 스탯 : " + item.getAllStat() + "%  |  총 데미지 : " + item.getTotalDamage() + "%  |  보스 공격력 : " + item.getBossDamage() + "%\r\n";
                say += "아이템 강화 횟수 : " + getAddEnhance(item) + "강#k\r\n\r\n\r\n";
                cm.sendOk("#fs11#초기화에 성공하셨습니다");
                cm.addEnchantLog(1, item.getItemId(), item.getSerialNumberEquip(), 11, 0, "메소 강화 초기화 성공 (계정 : " + cm.getClient().getAccountName() + ", 캐릭터 : " + cm.getPlayer().getName() + ", 장비 정보 [" + item.toString() + "])");
            } else {
                if (choice == 1 || getAddEnhance(item) == 0) {
                    say = "";
                    say += "#fs11##r<아이템 정보>\r\n";
                    say += "#fc0xFF9A9A9A#강화할 아이템 : #i" + itemid + "# #z" + itemid + "#\r\n";
                    say += "STR : " + item.getStr() + "  |  DEX : " + item.getDex() + "  |  INT : " + item.getInt() + "  |  LUK " + item.getLuk() + "\r\n";
                    say += "공격력 : " + item.getWatk() + "  |  마력 : " + item.getMatk() + "  | 스타포스 : " + item.getEnhance() + "성\r\n";
                    say += "올 스탯 : " + item.getAllStat() + "%  |  총 데미지 : " + item.getTotalDamage() + "%  |  보스 공격력 : " + item.getBossDamage() + "%\r\n";
                    say += "아이템 강화 횟수 : " + getAddEnhance(item) + "강#k\r\n\r\n\r\n";
                    cm.sendYesNo("#r강화에 실패#k하였지만" + ConvertNumber(keep) + " 메소를 사용하여 #r강화 등급#k이 보호되었습니다.\r\n계속 강화하시려면 '예'를 눌러 주세요.\r\n\r\n" + say);
                     cm.addEnchantLog(1, item.getItemId(), item.getSerialNumberEquip(), 11, 1, "메소 강화 초기화 실패 보호 (계정 : " + cm.getClient().getAccountName() + ", 캐릭터 : " + cm.getPlayer().getName() + ", 장비 정보 [" + item.toString() + "])");

                    re = 1;
                    status = 1;
                } else if (choice == 2) {
                    item.setStr(item.getStr() - items[getAddEnhance(item)][3]);
                    item.setDex(item.getDex() - items[getAddEnhance(item)][3]);
                    item.setInt(item.getInt() - items[getAddEnhance(item)][3]);
                    item.setLuk(item.getLuk() - items[getAddEnhance(item)][3]);
                    item.setWatk(item.getWatk() - items[getAddEnhance(item)][4]);
                    item.setMatk(item.getMatk() - items[getAddEnhance(item)][4]);
                    if (getAddEnhance(item) > 1 && getAddEnhance(item) < 15) {
                        item.setOwner(""+(getAddEnhance(item)-1)+"강");
                    } else if (getAddEnhance(item) == 1) {
                        item.setOwner("");
                    }                    
                    cm.getPlayer().send(Packages.network.models.CWvsContext.InventoryPacket.updateInventoryItem(Packages.objects.item.MapleInventoryType.EQUIP, item, false, cm.getPlayer()));
                    say = "";
                    say += "#r<아이템 정보>\r\n";
                    say += "#fc0xFF9A9A9A#강화할 아이템 : #i" + itemid + "# #z" + itemid + "#\r\n";
                    say += "STR : " + item.getStr() + "  |  DEX : " + item.getDex() + "  |  INT : " + item.getInt() + "  |  LUK " + item.getLuk() + "\r\n";
                    say += "공격력 : " + item.getWatk() + "  |  마력 : " + item.getMatk() + "  | 스타포스 : " + item.getEnhance() + "성\r\n";
                    say += "올 스탯 : " + item.getAllStat() + "%  |  총 데미지 : " + item.getTotalDamage() + "%  |  보스 공격력 : " + item.getBossDamage() + "%\r\n";
                    say += "아이템 강화 횟수 : " + getAddEnhance(item) + "강#k\r\n\r\n\r\n";
                    cm.sendYesNo("#r강화에 실패#k하여 #r강화 등급#k이 하락하였습니다.\r\n계속 강화하시려면 '예'를 눌러 주세요.\r\n\r\n" + say);
                     cm.addEnchantLog(1, item.getItemId(), item.getSerialNumberEquip(), 11, 1, "메소 강화 초기화 실패 (계정 : " + cm.getClient().getAccountName() + ", 캐릭터 : " + cm.getPlayer().getName() + ", 장비 정보 [" + item.toString() + "])");

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