importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);
importPackage(java.text);
importPackage(java.awt);

importPackage(Packages.objects.item);
importPackage(Packages.objects.users);
importPackage(Packages.objects.utils);
importPackage(Packages.constants);
importPackage(Packages.network.models);
importPackage(Packages.objects.wz.provider);
importPackage(Packages.objects.fields);
importPackage(Packages.objects.fields.gameobject);
importPackage(Packages.objects.fields.gameobject.lifes);
importPackage(Packages.database);
importPackage(Packages.scripting);

var startmap = [
    450009400, // 노말
    450009450, // 하드
    450009400, // 노말 연습
    450009450, // 하드 연습
];
var exitmap = 15;
var bossname = "더스크"
var limit = [
    3, // 노말
    3, // 카오스
    20, // 노말 연습
    20 // 카오스 연습
];
var time = 30;
var deathcount = 5;
var practice = false;

var diff = ["노말", "카오스", "노말연습", "카오스연습"];

function start() {
    practice = false;
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        text = "검은 마법사의 사념으로 이루어진 거대 괴수 더스크를 내버려 두어선 안된다.\r\n\r\n"
        text += "#L0##b공허의 눈(노멀 모드)으로 이동한다.#r(레벨 245이상)#k#l\r\n"
        text += "#L1##b공허의 눈(카오스 모드)으로 이동한다.#r(레벨 245이상)#k#l\r\n"
        text += "#L2##b공허의 눈(노멀 연습 모드)으로 이동한다.#r(레벨 245이상)#k#l\r\n"
        text += "#L3##b공허의 눈(카오스 연습 모드)으로 이동한다.#r(레벨 245이상)#k#l\r\n\r\n"
        text += "#L4#이동하지 않는다.#l";
        cm.askMenuSelfNew(text);
    } else if (status == 1) {
        if (selection == 4) {
            cm.dispose();
            return;
        }
        difficulty = selection;
        bn = diff[difficulty] + " " + bossname;
        if (selection == 2 || selection == 3) {
            practice = true;
        }
        if (practice) {
            cm.askYesNo("연습 모드에 입장을 선택하였습니다. 연습 모드에서는 #b#e경험치와 보상을 얻을 수 없으며#n#k 보스 몬스터의 종류와 상관없이 #b#e하루 5회#n#k만 이용할 수 있습니다. 입장하시겠습니까?", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
        } else {
            enterBoss();
        }
    } else if (status == 2) {
        enterBoss();
    }
}

function enterBoss() {
    var n = "dusk_clear";
    var p = cm.getPlayer();
    var q = cm.getPlayer().getOneInfoQuest(1234589, n);
    if (q != null && !q.isEmpty() && q == "1" && !practice) {
        cm.sayNpc("금주에 이미 격파하여 목요일 00시에 횟수 초기화 이후 다시 도전 가능합니다.", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
        cm.dispose();
        return;
    }

    var keyValue = bn + "c";
    if (practice) {
        keyValue = "boss_practice";
    }
    if (!cm.CountCheck(keyValue, limit[difficulty])) {
        cm.sayNpc("하루에 " + limit[difficulty] + "번만 시도 가능하며, 격파 이후 목요일부터 다시 도전 가능합니다.", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
        cm.dispose();
        return;
    }
    if (cm.getPlayer().getParty() == null) {
        cm.sayNpc("1인 이상의 파티에 속해야만 입장할 수 있습니다.", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
        cm.dispose();
        return;
    }
    if (!isPartyLeader()) {
        cm.sayNpc("파티장이 아니면 신청할 수 없습니다.", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
        cm.dispose();
        return;
    }
    if (!cm.allMembersHere()) {
        cm.sayNpc("파티원이 전원 이곳에 모여있어야 합니다.", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
        cm.dispose();
        return;
    }
    if (cm.getPlayerCount(startmap[difficulty]) > 0) {
        cm.sayNpc("이미 누군가가 도전중입니다.\r\n#b다른 채널을 이용해 주세요.#k", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
        cm.dispose();
        return;
    }
    if (!practice) {
        if (!cm.canEnterBoss("dusk_can_time")) {
            return;
        }
    }
    var it = cm.getClient().getChannelServer().getPartyMembers(cm.getParty()).iterator();
    var countPass = true;
    var timePass = true;
    while (it.hasNext()) {
        var chr = it.next();
        if (chr.getLevel() < 245) {
            countPass = false;
            break;
        }
        if (!CC(chr, keyValue, limit[difficulty])) {
            countPass = false;
            break;
        }
        if (!chr.canEnterBoss("dusk_can_time")) {
            timePass = false;
            break;
        }
    }
    if (!timePass && !practice) {
        cm.sayNpc("입장 제한시간이 남은 파티원이 있어 입장할 수 없습니다.", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
        cm.dispose();
        return;
    }
    if (!countPass) {
        cm.sayNpc("입장 제한횟수가 부족하거나 레벨 제한이 맞지 않는 파티원이 있어 입장할 수 없습니다.", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
        cm.dispose();
        return;
    } else {
        var it = cm.getClient().getChannelServer().getPartyMembers(cm.getParty()).iterator();
        var countPass = true;
        while (it.hasNext()) {
            var chr = it.next();
            var con = DBConnection.getConnection();
            var ps = con.prepareStatement("INSERT INTO boss_log(bossname, name) VALUES('" + bn + "', '" + chr.getName() + "')");
            ps.executeQuery();
            ps.close();
            con.close();
            AC(chr, keyValue);
            chr.setCurrentBossPhase(1);
        }
    }
    cm.resetMap(startmap[difficulty]);
    cm.dispose();
    var em = cm.getEventManager("Boss");
    var eim = em.readyInstance();
    eim.setProperty("StartMap", startmap[difficulty]);
    eim.setProperty("ExitMap", exitmap);
    eim.setProperty("BossName", bn);
    eim.setProperty("KillCount", 0);
    eim.setProperty("Leader", cm.getPlayer().getParty().getLeader().getName());
    eim.setProperty("DeathCount", deathcount);
    eim.setProperty("BossMode", practice ? 1 : 0);
    eim.startEventTimer(60000 * time); // 30분
    eim.registerParty(cm.getPlayer().getParty(), cm.getPlayer().getMap());
}

function isPartyLeader() {
    if (cm.getPlayer().getParty().getLeader().getId() == cm.getPlayer().getId())
        return true;
    else
        return false;
}

function AC(player, boss) {
    player.CountAdd(boss);
    if (!practice) {
        player.updateCanTime("dusk_can_time", 1);
    }
}

function CC(player, boss, limit) {
    var q = player.getOneInfoQuest(1234589, "dusk_clear");
    if (q != null && !q.isEmpty() && q == "1" && !practice) {
        return false;
    }
    return player.CountCheck(boss, limit);
}