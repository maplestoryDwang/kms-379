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
    220080100, // 이지
    220080200, // 노말
    220080300, // 카오스
    220080300, // 카오스 연습
];
var exitmap = 15;
var bossname = "파풀라투스"
var time = 30;
var deathcount = 5;
var practice = false;
var checkItem = false;
var diff = ["이지", "노말", "카오스", "카오스연습"];

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
        if (cm.getPlayer().getParty() == null) {
            cm.sayNpc("1인 이상의 파티에 속해야만 입장할 수 있습니다.", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
            cm.dispose();
            return;
        }
        text = "#e<보스: 파풀라투스>#n\r\n"
        text += "사고뭉치 파풀라투스가 차원을 계속 부수는 것을 막아야 합니다. 도와주시겠어요?\r\n\r\n\r\n"
        text += "#L1# 노말 모드 ( 레벨 155 이상 )"
        text += " #r[" + cm.getPlayer().getOneInfoQuestInteger(1234569, "papulatus_clear") + "/" + (cm.getPlayer().getBossTier() + 1) + "]#l#k\r\n";
        text += "#L2# 카오스 모드 ( 레벨 190 이상 )"
        text += " #r[" + cm.getPlayer().getOneInfoQuestInteger(1234569, "chaos_papulatus_clear") + "/" + (cm.getPlayer().getBossTier() + 1) + "]#l#k";
        for (i = 0; i < cm.getPlayer().getPartyMembers().size(); i++) {
            chr = cm.getPlayer().getPartyMembers().get(i);
            chr.dropMessage(5, "[노말 파풀라투스] 오늘 해당 보스를 " + chr.getOneInfoQuestInteger(1234569, "papulatus_clear") + "번 입장 하셨습니다. 총 " + (chr.getBossTier() + 1) + "번 입장 하실 수 있습니다.");
            chr.dropMessage(5, "[카오스 파풀라투스] 오늘 해당 보스를 " + chr.getOneInfoQuestInteger(1234569, "chaos_papulatus_clear") + "번 입장 하셨습니다. 총 " + (chr.getBossTier() + 1) + "번 입장 하실 수 있습니다.");
        }
        text += "\r\n#L3# 카오스 연습 모드 ( 레벨 190 이상 )#l";

        cm.askMenu(text, GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
    } else if (status == 1) {
        difficulty = selection;
        bn = diff[difficulty] + " " + bossname;
        if (selection == 3) {
            practice = true;
        }
        if (!cm.haveItem(4031179)) {
            if (!cm.canHold(4031179, 1)) {
                cm.sayNpc("기타 인벤토리 공간이 부족합니다. 기타 인벤토리 공간을 충분히 확보해주세요.", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
                cm.dispose();
                return;
            }
            cm.gainItem(4031179, 1);
            cm.sayNpc("차원 균열의 조각이 없으시군요. 파풀라투스를 만나기 위해서 꼭 필요합니다. 제가 갖고 있던 것을 드릴테니, 파풀라투스가 차원을 부수는 것을 꼭 막아 주세요!", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
            checkItem = true;
        } else {
            if (practice) {
                cm.askYesNo("연습 모드에 입장을 선택하였습니다. 연습 모드에서는 #b#e경험치와 보상을 얻을 수 없으며#n#k 보스 몬스터의 종류와 상관없이 #b#e하루 5회#n#k만 이용할 수 있습니다. 입장하시겠습니까?", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
            } else {
                enterBoss();
            }
        }
    } else if (status == 2) {
        if (checkItem) {
            if (practice) {
                cm.askYesNo("연습 모드에 입장을 선택하였습니다. 연습 모드에서는 #b#e경험치와 보상을 얻을 수 없으며#n#k 보스 몬스터의 종류와 상관없이 #b#e하루 5회#n#k만 이용할 수 있습니다. 입장하시겠습니까?", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
            } else {
                enterBoss();
            }
        } else {
            enterBoss();
        }
    } else if (status == 3) {
        enterBoss();
    }
}

function enterBoss() {
    var n = "papulatus_clear";
    if (difficulty == 2) {
        // 카오스는 1주에 1회, 노말/이지는 1일 1회
        n = "chaos_papulatus_clear";
    }
    if (cm.getPlayer().getParty() == null) {
        cm.sayNpc("1인 이상의 파티에 속해야만 입장할 수 있습니다.", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
        cm.dispose();
        return;
    }
    var p = cm.getPlayer();

    if (!practice) {
        // 격파 체크
        for (i = 0; i < cm.getPlayer().getPartyMembers().size(); i++) {
            chr = cm.getPlayer().getPartyMembers().get(i);
            var q = chr.getOneInfoQuestInteger(1234569, n);
            if (q >= chr.getBossTier() + 1) {
                if (DBConfig.isGanglim) {
                    cm.sayNpc("일일 입장횟수를 모두 사용한 파티원이 있어 00시 횟수 초기화 이후 다시 도전 가능합니다.", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
                } else {
                    cm.sayNpc("금일에 이미 격파한 파티원이 있어 00시에 횟수 초기화 이후 다시 도전 가능합니다.", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
                }
                cm.dispose();
                return;
            }
        }
    }
    var keyValue = "papulatus_c"; // 이지/노말 통합 (카오스 제외)
    if (practice) {
        keyValue = "boss_practice";
        if (!cm.CountCheck(keyValue, 20)) {
            cm.sayNpc("하루에 20번만 시도 가능합니다.", GameObjectType.User, ScriptMessageFlag.NpcReplacedByNpc);
            cm.dispose();
            return;
        }
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
    if (!practice && difficulty == 2 && !DBConfig.isGanglim) {
        if (!cm.canEnterBoss("papulatus_can_time")) {
            return;
        }
    }
    var it = cm.getClient().getChannelServer().getPartyMembers(cm.getParty()).iterator();
    var countPass = true;
    var timePass = true;
    var levelLimit = 115;
    if (difficulty == 1) {
        levelLimit = 155;
    } else if (difficulty == 2) {
        levelLimit = 190;
    }
    while (it.hasNext()) {
        var chr = it.next();
        if (chr.getLevel() < levelLimit) {
            countPass = false;
            break;
        }
        var q = chr.getOneInfoQuest(1234569, n);
        if (q != null && !q.isEmpty() && q >= cm.getPlayer().getBossTier() + 1) {
            countPass = false;
            break;
        }
        if (!DBConfig.isGanglim && !chr.canEnterBoss("papulatus_can_time") && difficulty == 2) {
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
        if (!DBConfig.isGanglim) {
            var canTimeKey = null;
            if (!practice && difficulty == 2) {
                canTimeKey = "papulatus_can_time";
            }
        }
        cm.setBossEnter(cm.getParty(), bn, keyValue, canTimeKey, 3);
    }

    it = cm.getClient().getChannelServer().getPartyMembers(cm.getParty()).iterator();
    while (it.hasNext()) {
        var chr = it.next();
        chr.setCurrentBossPhase(1);
        if (DBConfig.isGanglim) {
            if (!practice) {
                chr.updateOneInfo(1234569, n, (chr.getOneInfoQuestInteger(1234569, n) + 1) + "");
            }
        }
    }
    cm.resetMap(startmap[difficulty]);
    cm.dispose();

    if (difficulty == 0) { // 이지 모드는 데카 50개
        deathcount = 50;
    }
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