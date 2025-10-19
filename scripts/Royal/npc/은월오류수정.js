importPackage(Packages.client);
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
var seld = -1, seld2 = -1;
var deamon = 0;
var n = 0;
var t = 0;

var jobs = [
    {'jobid': 2512, 'jobname': "은월", 'job': "영웅", 'stat': 1, 'sk': [20051284, 20050285, 20050286, 20050074, 20050012, 20050073], 'uq': false},
]
var level = -1;
var coin = -1;
var hpmp = -1;

var final = [];
var finaljob;
var jrand = -1;
var ast = -1;

var price = 0;

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

    if (cm.inBoss()) {
        cm.getPlayer().dropMessage(5, "보스 진행중엔 이용이 불가능합니다.");
        cm.dispose();
        return;
    }

    if (!cm.getPlayer().isGM() && cm.getPlayer().getDonationPoint() < price && status > 1 && seld != 2) {
        cm.sendOk("#fs11##fc0xFFFF3300#" + price + "P#fc0xFF000000# 로얄 포인트가 필요합니다.");
        cm.dispose();
        return;
    }

    if (status == 0) {
        if (cm.getPlayer().getJob() != 2512) {
            cm.sendOk("#fs11#은월만 이용하실 수 있습니다.");
            cm.dispose();
            return;
        }
        if (!cm.getPlayer().vCoreEquipCheck()) {
            cm.sendOk("#fs11#V스킬 코어를 전부 장착해제 후 시도해주시기 바랍니다.");
            cm.dispose();
            return;
        }
        cm.dispose();
        prevjob = cm.getPlayer().getJob();
        tempjob = jobs[0];
        cm.getPlayer().gainDonationPoint(-price);
        changeJobscript(tempjob, 1);
        if (deamon == 1) {
            cm.sendOk("#fs11#직업 변경이 완료되었습니다.\r\n#r※ MP 게이지는 채널변경후 재접속시 생성됩니다.");
        } else {
            cm.effectText("#fn나눔고딕 ExtraBold##fs20#[오류해결] < " +  tempjob['jobname'] + " > 의 오류가 수정되었습니다", 50, 1000, 6, 0, 330, -550);
        }
        prevflag = cm.getPlayer().getSaveFlag();
        cm.getPlayer().setSaveFlag(1024); // VMatrix
        cm.getPlayer().saveToDB(false, false);
        cm.getPlayer().setSaveFlag(prevflag);
        cm.addCustomLog(20, "[직업변경] [은월오류수정]");
    }
}

function baseSkill() {
    switch (cm.getJob()) {

        case 2100:
            cm.teachSkill(20001295, 1, 1);
            break;

        case 6100:
            cm.teachSkill(60000219, 1, 1);
            cm.teachSkill(60001217, 1, 1);
            cm.teachSkill(60001216, 1, 1);
            cm.teachSkill(60001218, 1, 1);
            cm.teachSkill(60001219, 1, 1);
            cm.teachSkill(60001225, 1, 1);
            break;

        case 6500:
            break;

        case 2700:
            cm.teachSkill(27000106, 5, 5);
            cm.teachSkill(27000207, 5, 5);
            cm.teachSkill(27001201, 20, 20);
            cm.teachSkill(27001100, 20, 20);
            break;
        case 2500:
            cm.teachSkill(20051284, 30, 30);
            cm.teachSkill(20050285, 30, 30);
            cm.teachSkill(20050286, 30, 30);
            cm.teachSkill(25001000, 30, 30);
            cm.teachSkill(25001002, 30, 30);
            cm.teachSkill(25000003, 30, 30);
            break;

        case 1100:
        case 1200:
        case 1300:
        case 1400:
        case 1500:
            cm.teachSkill(10001251, 1, 1);
            cm.teachSkill(10001252, 1, 1);
            cm.teachSkill(10001253, 1, 1);
            cm.teachSkill(10001254, 1, 1);
            cm.teachSkill(10001255, 1, 1);
            break;
        case 14200:
            cm.teachSkill(140000291, 6, 6);
            break;
        case 15200:
            cm.teachSkill(150000079, 1, 1);
            cm.teachSkill(150011005, 1, 1);
            break;

        case 16400:
            cm.teachSkill(160000001, 1, 1);

            break;
        case 3500:
            cm.teachSkill(30001068, 1, 1);
            break;
    }
}

function arcsymbol(j) {
    var inv = cm.getInventory(-1);
    for (i = -1600; i >= -1605; i--) {
        item = cm.getInventory(-1).getItem(i);
        if (item == null) continue;
        if (Math.floor(item.getItemId() / 1000) != 1712) continue;
        ial = item.getArcLevel();
        var normal = 100 * (ial + 2);
        var zen = 48 * (ial + 2);
        var dev = 2100 * (ial + 2);
        // 1 = s, 2 = d, 3 = i, 4 = l, 5 = z, 6 = h
        var stat = (j >= 1 && j <= 4) ? normal : j == 5 ? zen : dev;
        item.setStr(0);
        item.setDex(0);
        item.setInt(0);
        item.setLuk(0);
        item.setHp(0);

        switch (j) {
            case 1:
                item.setStr(stat);
                break;
            case 2:
                item.setDex(stat);
                break;
            case 3:
                item.setInt(stat);
                break;
            case 4:
                item.setLuk(stat);
                break;
            case 5:
                item.setStr(stat);
                item.setDex(stat);
                item.setLuk(stat);
                break;
            case 6:
                item.setHp(stat);
                break;
        }
        cm.getPlayer().forceReAddItem(item, Packages.objects.item.MapleInventoryType.EQUIPPED);
    }
    var inv = cm.getInventory(1);
    for (i = 0; i <= inv.getSlotLimit(); i++) {
        item = cm.getInventory(1).getItem(i);
        if (item == null) continue;
        if (Math.floor(item.getItemId() / 1000) != 1712) continue;
        ial = item.getArcLevel();
        var normal = 100 * (ial + 2);
        var zen = 48 * (ial + 2);
        var dev = 2100 * (ial + 2);
        // 1 = s, 2 = d, 3 = i, 4 = l, 5 = z, 6 = h
        var stat = (j >= 1 && j <= 4) ? normal : j == 5 ? zen : dev;
        item.setStr(0);
        item.setDex(0);
        item.setInt(0);
        item.setLuk(0);
        item.setHp(0);

        switch (j) {
            case 1:
                item.setStr(stat);
                break;
            case 2:
                item.setDex(stat);
                break;
            case 3:
                item.setInt(stat);
                break;
            case 4:
                item.setLuk(stat);
                break;
            case 5:
                item.setStr(stat);
                item.setDex(stat);
                item.setLuk(stat);
                break;
            case 6:
                item.setHp(stat);
                break;
        }
        cm.getPlayer().forceReAddItem(item, Packages.objects.item.MapleInventoryType.EQUIPPED);
    }
}


function autsymbol(j) {
    var inv = cm.getInventory(-1);
    for (i = -1700; i >= -1703; i--) {
        item = cm.getInventory(-1).getItem(i);
        if (item == null) continue;
        if (Math.floor(item.getItemId() / 1000) != 1713) continue;
        ial = item.getArcLevel();
        var normal = 100 * ((ial * 2) + 3);
        var zen = 48 * ((ial * 2) + 3);
        var dev = 2100 * ((ial * 2) + 3);
        // 1 = s, 2 = d, 3 = i, 4 = l, 5 = z, 6 = h
        var stat = (j >= 1 && j <= 4) ? normal : j == 5 ? zen : dev;
        item.setStr(0);
        item.setDex(0);
        item.setInt(0);
        item.setLuk(0);
        item.setHp(0);

        switch (j) {
            case 1:
                item.setStr(stat);
                break;
            case 2:
                item.setDex(stat);
                break;
            case 3:
                item.setInt(stat);
                break;
            case 4:
                item.setLuk(stat);
                break;
            case 5:
                item.setStr(stat);
                item.setDex(stat);
                item.setLuk(stat);
                break;
            case 6:
                item.setHp(stat);
                break;
        }
        cm.getPlayer().forceReAddItem(item, Packages.objects.item.MapleInventoryType.EQUIPPED);
    }
    var inv = cm.getInventory(1);
    for (i = 0; i <= inv.getSlotLimit(); i++) {
        item = cm.getInventory(1).getItem(i);
        if (item == null) continue;
        if (Math.floor(item.getItemId() / 1000) != 1713) continue;
        ial = item.getArcLevel();
        var normal = 100 * ((ial * 2) + 3);
        var zen = 48 * ((ial * 2) + 3);
        var dev = 2100 * ((ial * 2) + 3);
        // 1 = s, 2 = d, 3 = i, 4 = l, 5 = z, 6 = h
        var stat = (j >= 1 && j <= 4) ? normal : j == 5 ? zen : dev;
        item.setStr(0);
        item.setDex(0);
        item.setInt(0);
        item.setLuk(0);
        item.setHp(0);

        switch (j) {
            case 1:
                item.setStr(stat);
                break;
            case 2:
                item.setDex(stat);
                break;
            case 3:
                item.setInt(stat);
                break;
            case 4:
                item.setLuk(stat);
                break;
            case 5:
                item.setStr(stat);
                item.setDex(stat);
                item.setLuk(stat);
                break;
            case 6:
                item.setHp(stat);
                break;
        }
        cm.getPlayer().forceReAddItem(item, Packages.objects.item.MapleInventoryType.EQUIPPED);
    }
}


function changeJobscript(tjob, jt) {
    jid = tjob['jobid'];
    
    cm.getPlayer().changeJob(jid, true);

    cm.getPlayer().getLinkSkill().updateLinkSkillByFreeJobChange(cm.getPlayer());

    cm.clearSkills();

    cm.getPlayer().getVCoreSkillsNoLock().clear();
    
    cm.getPlayer().getStolenSkills().clear();

    cm.getPlayer().setKeyValue(1477, "count", "0");

    baseSkill();

    cm.getPlayer().statReset();

    arcsymbol(tjob['stat']);

    autsymbol(tjob['stat']);

    for (i = 0; i < tjob['sk'].length; i++) {
        cm.teachSkill(tjob['sk'][i], 30, 30);
    }

    if (jid == 6500) {
        cm.getPlayer().setGender(1);
    }
    
    cm.getPlayer().updateOneInfo(122870, "AutoJob", jid + "");

    cm.autoSkillMaster();

    cm.getPlayer().fakeRelog();


/*
    if (!cm.getPlayer().isGM()) {
        cm.getClient().disconnect(true, false);
        cm.getClient().getSession().close();
    }
*/
}