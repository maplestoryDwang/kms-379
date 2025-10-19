var enter = "\r\n";
var seld = -1;

var key = "levelreward";
var selr;
var reward = [
         {'lvl' : 200, 'ap' : 200, 'item' : [
                {'itemid' : 2630437, 'qty' : 6},
                {'itemid' : 5062500, 'qty' : 60},
                {'itemid' : 5062010, 'qty' : 150},
    ]},     
        {'lvl' : 220, 'ap' : 270, 'item' : [
                {'itemid' : 2435719, 'qty' : 100},
                {'itemid' : 5062500, 'qty' : 200},
                {'itemid' : 5062010, 'qty' : 400},
                {'itemid' : 4310237, 'qty' : 300},
        ]},
        {'lvl' : 230, 'ap' : 280, 'item' : [
                {'itemid' : 2450064, 'qty' : 2},
                {'itemid' : 2437760, 'qty' : 10},
                {'itemid' : 2435719, 'qty' : 100},
                {'itemid' : 2431940, 'qty' : 2},
        ]},
    {'lvl' : 240, 'ap' : 300, 'item' : [
                {'itemid' : 4001715, 'qty' : 10},
                {'itemid' : 2437760, 'qty' : 10},
                {'itemid' : 2049360, 'qty' : 3},
                {'itemid' : 4310266, 'qty' : 500},
                {'itemid' : 4310237, 'qty' : 500},
        ]},
            {'lvl' : 250, 'ap' : 320, 'item' : [
                {'itemid' : 4310266, 'qty' : 500},
                {'itemid' : 2048757, 'qty' : 300},
                {'itemid' : 2048753, 'qty' : 10},
                {'itemid' : 2450064, 'qty' : 5},
                {'itemid' : 2023072, 'qty' : 3},
                {'itemid' : 2430041, 'qty' : 2},
        ]},
            {'lvl' : 260, 'ap' : 330, 'item' : [
                {'itemid' : 2430042, 'qty' : 2},
                {'itemid' : 2048757, 'qty' : 300},
                {'itemid' : 2633336, 'qty' : 10},
                {'itemid' : 4001715, 'qty' : 30},
        ]},
            {'lvl' : 275, 'ap' : 350, 'item' : [
                {'itemid' : 5062005, 'qty' : 5},
                {'itemid' : 5062503, 'qty' : 5},
                {'itemid' : 4001715, 'qty' : 50},
                {'itemid' : 3014028, 'qty' : 1},
                {'itemid' : 2430043, 'qty' : 1},
                {'itemid' : 4310237, 'qty' : 500},
        ]},
            {'lvl' : 290, 'ap' : 351, 'item' : [
                {'itemid' : 5062005, 'qty' : 10},
                {'itemid' : 2438145, 'qty' : 1},
                {'itemid' : 4001715, 'qty' : 100},
        ]},
            {'lvl' : 300, 'ap' : 370, 'item' : [
                {'itemid' : 1143303, 'qty' : 1},
                {'itemid' : 5062005, 'qty' : 10},
                {'itemid' : 5062503, 'qty' : 10},
                {'itemid' : 5060048, 'qty' : 20},
                {'itemid' : 4001715, 'qty' : 200},
        ]},
            {'lvl' : 400, 'ap' : 0, 'item' : [
                {'itemid' : 5062005, 'qty' : 25},
                {'itemid' : 5062503, 'qty' : 25},
                {'itemid' : 5060048, 'qty' : 25},
                {'itemid' : 4001715, 'qty' : 250},
        ]},
            {'lvl' : 500, 'ap' : 0, 'item' : [
                {'itemid' : 1143304, 'qty' : 1},
                {'itemid' : 2430045, 'qty' : 1},
                {'itemid' : 5062005, 'qty' : 30},
                {'itemid' : 5062503, 'qty' : 30},
                {'itemid' : 5060048, 'qty' : 30},
                {'itemid' : 4001715, 'qty' : 300},
        ]},
            {'lvl' : 600, 'ap' : 0, 'item' : [
                {'itemid' : 2430045, 'qty' : 2},
                {'itemid' : 5062005, 'qty' : 35},
                {'itemid' : 5062503, 'qty' : 35},
                {'itemid' : 5060048, 'qty' : 35},
                {'itemid' : 4001715, 'qty' : 350},
        ]},
            {'lvl' : 700, 'ap' : 0, 'item' : [
                {'itemid' : 2430046, 'qty' : 1},
                {'itemid' : 5062005, 'qty' : 40},
                {'itemid' : 5062503, 'qty' : 40},
                {'itemid' : 5060048, 'qty' : 40},
                {'itemid' : 4001715, 'qty' : 400},
        ]},
            {'lvl' : 777, 'ap' : 0, 'item' : [
                {'itemid' : 1143305, 'qty' : 1},
                {'itemid' : 2430047, 'qty' : 1},
                {'itemid' : 5062005, 'qty' : 50},
                {'itemid' : 5062503, 'qty' : 50},
                {'itemid' : 5060048, 'qty' : 50},
                {'itemid' : 4001715, 'qty' : 500},
        ]},
         ]
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, sel) {
    if (mode == -1) {
        cm.dispose();
    }
    if (mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }
    if (status == 0) {
        var msg = "#fs11# #d수령 가능한 레벨 보상은 아래와 같습니다.#k"+enter;

        for (i = 0; i < reward.length; i++) {
            if (cm.getPlayer().getLevel() >= reward[i]['lvl']) {
                check = getk(reward[i]['lvl']) == 0 ? "#b(수령 가능)" : "#fc0xFF9A9A9A#(수령 완료)#b"
                msg += "#L"+i+"##b"+reward[i]['lvl']+"레벨 보상 #e"+check+"#n#k"+enter;
            } else {
                msg += "#L"+i+"##r"+reward[i]['lvl']+"레벨 보상 (수령 불가)#k"+enter;
            }
        }

        cm.sendSimple(msg);
    } else if (status == 1) {
        seld = sel;
        if (seld > 200) {
            if (cm.getInvSlots(1) < 3 || cm.getInvSlots(2) < 3 || cm.getInvSlots(3) < 3 || cm.getInvSlots(4) < 3 || cm.getInvSlots(5) < 3) {
                cm.sendOkS("#fs11##fc0xFF6600CC#인벤토리를 탭별로 3칸이상 비워주세요", 2);
                cm.dispose();
                return;
            }
            if (getk(seld) > 0) {
                cm.sendOk("#fs11##r이미 수령하신 보상입니다.");
                cm.dispose();
                return;
            }
            if (seld > cm.getPlayer().getLevel()) {
                cm.sendOk("#fs11##r레벨이 부족합니다.#k");
                cm.dispose();
                return;
            }
            if (seld == 250) {
                cm.forceCompleteQuest(6500);
                cm.forceCompleteQuest(12394);
                cm.forceCompleteQuest(12395);
                cm.forceCompleteQuest(12396);
                cm.setInnerStats(1);
                cm.setInnerStats(2);
                cm.setInnerStats(3);
            }
            setk(seld, "1");
            cm.sendOk("#fs11##r레벨업 달성보상이 지급되었습니다."+enter+enter+getRewardList());
            cm.dispose();
        } else {
            selr = reward[seld];
            if (getk(selr['lvl']) > 0) {
                cm.sendOk("#fs11##r이미 수령하신 보상입니다."+enter+enter+getRewardList());
                cm.dispose();
                return;
            }
            if (selr['lvl'] > cm.getPlayer().getLevel()) {
                cm.sendOk("#fs11##r이 보상을 받기엔 레벨이 모자랍니다."+enter+enter+getRewardList());
                cm.dispose();
                return;
            }
            //cm.getPlayer().gainAp(selr['ap']);
            gainReward(selr['lvl']);
            setk(selr['lvl'], "1");
            cm.sendOk("#fs11##r보상 지급이 완료되었습니다."+enter+enter+getRewardList());
            cm.dispose();
        }
        
    }
}

function gainReward(level) {
    for (p = 0; p < selr['item'].length; p++) {
        if (Math.floor(selr['item'][p]['itemid'] / 1000000) == 1) {
            gainItemall(selr['item'][p]['itemid'], selr['item'][p]['allstat'], selr['item'][p]['atk']);
        } else {
            cm.gainItem(selr['item'][p]['itemid'], selr['item'][p]['qty']);
        }
    }
}

function getRewardList() {
    var msg = "#b"+selr['lvl']+"#fs11#레벨#k 달성 보상리스트 입니다.#fs11#"+enter;
    msg += ""+enter;
    for (p = 0; p < selr['item'].length; p++) {
        if (Math.floor(selr['item'][p]['itemid'] / 1000000) == 1) {
            msg += "#i"+selr['item'][p]['itemid']+"##b#z"+selr['item'][p]['itemid']+"# "+selr['item'][p]['qty']+"개#k" +enter;
        } else {
            msg += "#i"+selr['item'][p]['itemid']+"##b#z"+selr['item'][p]['itemid']+"# "+selr['item'][p]['qty']+"개#k" + enter;
        }
    }
    return msg;
}

function gainItemall(itemid, allstat, atk) {
    item = Packages.objects.item.MapleItemInformationProvider.getInstance().getEquipById(itemid);
    /*item.setStr(allstat);
    item.setDex(allstat);
    item.setInt(allstat);
    item.setLuk(allstat);
    item.setWatk(atk);
    item.setMatk(atk);*/
    Packages.objects.item.MapleInventoryManipulator.addFromDrop(cm.getClient(), item, false);
}

function getk(level) {
    return cm.getPlayer().getKeyValue(201820, key+"_"+level);
}

function setk(level, value) {
    cm.getPlayer().setKeyValue(201820, key+"_"+level, value);
    prevflag = cm.getPlayer().getSaveFlag();
    cm.getPlayer().setSaveFlag(64); // QuestInfo
    cm.getPlayer().saveToDB(false, false);
    cm.getPlayer().setSaveFlag(prevflag)
}