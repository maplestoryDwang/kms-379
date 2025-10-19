importPackage(java.lang);

var equipped = false;

var banitem = [1712001, 1712002, 1712003, 1712004, 1712005, 1712006, 1713000 ,1713001, 1713002, 1713003, 1713004, 1713005];

var cantowner = ["△", "★", "★△", "★★", "★★△", "★★★"];

var 아이템코드 = 2430046;
var 몇강 = 19;
var 올스탯 = 792;
var 공마 = 525;

function ConvertNumber(number) { //모 블로그 참조함, 이 부분에 대해서는 키네시스(kinesis8@nate.com), 라피스#2519 에게 저작권이 없음
    var inputNumber  = number < 0 ? false : number;
    var unitWords    = ['', '만 ', '억 ', '조 ', '경 '];
    var splitUnit    = 10000;
    var splitCount   = unitWords.length;
    var resultArray  = [];
    var resultString = '';
    if (inputNumber == false) {
        cm.sendOk("오류가 발생하였습니다. 다시 시도해 주세요.\r\n(파싱오류)");
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
    [1, 100, 0, 87, 65],
    [2, 95, 30000000, 5, 5],
    [3, 90, 45000000, 5, 5],
    [4, 90, 50000000, 7, 5],
    [5, 80, 150000000, 7, 5],
    [6, 70, 150000000, 9, 5],
    [7, 70, 250000000, 11, 6],
    [8, 60, 250000000, 13, 9],
    [9, 50, 450000000, 15, 10],
    [10, 40, 550000000,15, 15],
    [11, 30, 550000000,15, 20],
    [12, 30, 500000000,20, 20],
    [13, 20, 500000000,30, 25],
    [14, 15, 500000000,35, 30],
    [15, 12, 500000000,35, 30],
    [16, 10, 1000000000,30, 30],
    [17, 5, 1000000000,50, 50],
    [18, 5, 1000000000,100, 50],
    [19, 3, 1200000000,150, 100],
    [20, 2, 1500000000,200, 150],
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
        var say = "#fs11##b+" + 몇강 + "강화권#k을 사용할 장비를 선택 해주세요\r\n\r\n#r#e※ 주의사항 ※\r\n장착된 아이템 - 장비 아이템부터 순으로 표기됩니다.#n#k#b\r\n\r\n";

        for (i = 0; i <= 50; i++) { // 장착중
            item = cm.getInventory(-1).getItem(i*-1)
            if (item != null) {
                if (!cm.isCash(item.getItemId())) {
                    say += "#L" + (i+100000) + "##b#i" + item.getItemId() + "# #z" + item.getItemId() + "# #r#e[장착중]#k#n#l\r\n";
                    count++;
                }
            }
        }

        for (i = 0; i <= cm.getInventory(1).getSlotLimit(); i++) {
            item = cm.getInventory(1).getItem(i)
            if (item != null) {
                if (!cm.isCash(item.getItemId())) {
                    say += "#L" + i + "##b#i" + item.getItemId() + "# #z" + item.getItemId() + "# #r["+i+"슬롯]#k#l\r\n";
                    count++;
                }
            }
        }

        if (count <= 0) {
            cm.sendOk("강화할 장비를 소지하고 있는지 확인해 주세요.");
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
        if (getAddEnhance(item) >= 1) {
            cm.sendOk("#fs11#이미 한 번 이상 강화가 된 아이템에는 사용할 수 없습니다.");
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
        say += "#fs 11#+" + 몇강 + " 강화권을 사용하시겠습니까?\r\n\r\n\r\n ";
        cm.sendSimple(notice + say +
      //  "#L1##b" + ConvertNumber(keep) + "메소를 사용하여 파괴, 미끄러짐을 방지 하겠습니다.#l\r\n" +
        "#L2##b사용하겠습니다.#k#k#l");
    } else if (status == 2) {
        if (re == 0) {
            choice = selection;
        }

        //강화수, 강화성공확률, 강화메소, 올스탯, 공마 
        say = "";
        say += "#fs 11#";
        say += "강화 : #b+0강 -> +" + 몇강 + "강#k\r\n";
        say += "강화 성공 시 #b올스탯 + " + 올스탯 + " 공마 + " + 공마 + "#k 증가\r\n";
        if (selection == 1 || choice == 1) {
            say += "\r\n";
        } else if (selection == 2 || choice == 2) {
            say += "";
        }   
        say += "#r<아이템 정보>\r\n";
        say += "강화할 아이템 : #i" + itemid + "# #z" + itemid + "#\r\n";
        say += "STR : " + item.getStr() + "  |  DEX : " + item.getDex() + "  |  INT : " + item.getInt() + "  |  LUK " + item.getLuk() + "\r\n";
        say += "공격력 : " + item.getWatk() + "  |  마력 : " + item.getMatk() + "  | 스타포스 : " + item.getEnhance() + "성\r\n";
        say += "올 스탯 : " + item.getAllStat() + "%  |  총 데미지 : " + item.getTotalDamage() + "%  |  보스 공격력 : " + item.getBossDamage() + "%\r\n";
        say += "아이템 강화 횟수 : " + getAddEnhance(item) + "강#k\r\n\r\n\r\n";
        if (selection == 1 || choice == 1) {
            cm.sendYesNo(say + "정말로 " + ConvertNumber(keep) + "메소를 사용하여 미끄러짐을 방지 하시겠습니까?\r\n" +
            "성공/실패 여부와 상관없이 미끄러짐 방지 메소는 소모됩니다.\r\n\r\n#b총 필요 메소 : " + ConvertNumber(items[getAddEnhance(item) + 1][2] + keep) + "메소#k#l");
        } else if (selection == 2 || choice == 2) {
            cm.sendYesNo(say + "#b예#k를 누르면 강화권이 사용됩니다.");
        }
    } else if (status == 3) {
        if (choice == 1) {
            if (cm.getPlayer().getMeso() >= (items[getAddEnhance(item) + 1][2] + keep)) {
                cm.gainMeso(-(items[getAddEnhance(item) + 1][2] + keep));
            } else {
                cm.sendOk("수수료가 부족하여 강화권 사용이 불가능합니다.\r\n필요 수수료 : 1억메소");
                cm.dispose();
                return;
            }
        } else if (choice == 2) {
            if (cm.getPlayer().getMeso() >= items[getAddEnhance(item) + 1][2]) {
                //cm.gainMeso(-items[getAddEnhance(item) + 1][2]);
            } else {
                cm.sendOk("수수료가 부족하여 강화가 불가능합니다.");
                cm.dispose();
                return;
            }
        } else {
            cm.dispose();
            return;
        }        
        if (item != null) {
            var rand = Math.ceil(Math.random() * 100);
            if (rand >= 0 && rand <= items[getAddEnhance(item) + 1][1]) {
                item.addStr(올스탯);
                item.addDex(올스탯);
                item.addInt(올스탯);
                item.addLuk(올스탯);
                item.addWatk(공마);
                item.addMatk(공마);
                item.setOwner(몇강 +"강");
                cm.getPlayer().forceReAddItem(item, Packages.objects.item.MapleInventoryType.EQUIP);
                cm.gainItem(아이템코드, -1);
                say = "";     
                say += "#fs11##r<아이템 정보>\r\n";
                say += "강화할 아이템 : #i" + itemid + "# #z" + itemid + "#\r\n";
                say += "STR : " + item.getStr() + "  |  DEX : " + item.getDex() + "  |  INT : " + item.getInt() + "  |  LUK " + item.getLuk() + "\r\n";
                say += "공격력 : " + item.getWatk() + "  |  마력 : " + item.getMatk() + "  | 스타포스 : " + item.getEnhance() + "성\r\n";
                say += "올 스탯 : " + item.getAllStat() + "%  |  총 데미지 : " + item.getTotalDamage() + "%  |  보스 공격력 : " + item.getBossDamage() + "%\r\n";
                say += "아이템 강화 횟수 : " + getAddEnhance(item) + "강#k\r\n\r\n\r\n";

                //로그작성
                //Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/주문서/메소강화19강.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n사용한 아이템 : 메소강화 19강 주문서 (2430046)" + "\r\n\r\n", true);
                cm.addEnchantLog(1, item.getItemId(), item.getSerialNumberEquip(), 10, 0, "메소 강화 주문서 " + getAddEnhance(item) + "강 (계정 : " + cm.getClient().getAccountName() + ", 캐릭터 : " + cm.getPlayer().getName() + ", 장비 정보 [" + item.toString() + "])");

                cm.sendOk("#fs11#강화가 성공하였습니다.");
            cm.dispose();
            return;
            } else {
                if (choice == 1 || getAddEnhance(item) == 0) {
                    say = "";
                    say += "#fs11##r<아이템 정보>\r\n";
                    say += "강화할 아이템 : #i" + itemid + "# #z" + itemid + "#\r\n";
                    say += "STR : " + item.getStr() + "  |  DEX : " + item.getDex() + "  |  INT : " + item.getInt() + "  |  LUK " + item.getLuk() + "\r\n";
                    say += "공격력 : " + item.getWatk() + "  |  마력 : " + item.getMatk() + "  | 스타포스 : " + item.getEnhance() + "성\r\n";
                    say += "올 스탯 : " + item.getAllStat() + "%  |  총 데미지 : " + item.getTotalDamage() + "%  |  보스 공격력 : " + item.getBossDamage() + "%\r\n";
                    say += "아이템 강화 횟수 : " + getAddEnhance(item) + "강#k\r\n\r\n\r\n";
                    cm.sendYesNo("#r강화에 실패#k하였지만" + ConvertNumber(keep) + " 메소를 사용하여 #r강화 등급#k이 보호되었습니다.\r\n계속 강화하시려면 '예'를 눌러 주세요.\r\n\r\n" + say);
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
                    cm.getPlayer().forceReAddItem(item, Packages.objects.item.MapleInventoryType.EQUIP);
                    say = "";     
                    say += "#r<아이템 정보>\r\n";
                    say += "강화할 아이템 : #i" + itemid + "# #z" + itemid + "#\r\n";
                    say += "STR : " + item.getStr() + "  |  DEX : " + item.getDex() + "  |  INT : " + item.getInt() + "  |  LUK " + item.getLuk() + "\r\n";
                    say += "공격력 : " + item.getWatk() + "  |  마력 : " + item.getMatk() + "  | 스타포스 : " + item.getEnhance() + "성\r\n";
                    say += "올 스탯 : " + item.getAllStat() + "%  |  총 데미지 : " + item.getTotalDamage() + "%  |  보스 공격력 : " + item.getBossDamage() + "%\r\n";
                    say += "아이템 강화 횟수 : " + getAddEnhance(item) + "강#k\r\n\r\n\r\n";
                    cm.sendYesNo("#r강화에 실패#k하여 #r강화 등급#k이 하락하였습니다.\r\n계속 강화하시려면 '예'를 눌러 주세요.\r\n\r\n" + say);
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