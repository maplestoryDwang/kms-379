importPackage(java.lang);

var Time = new Date();
var Year = Time.getFullYear() + "";
var Month = Time.getMonth() + 1 + "";
var Date = Time.getDate() + "";
if (Month < 10) {
    Month = "0" + Month;
}
if (Date < 10) {
    Date = "0" + Date;
}
var Today = parseInt(Year + Month + Date);

var quest = [
    ["이벤트 일일퀘스트.01", "3k"],
    ["이벤트 일일퀘스트.02", "5k"],
    ["이벤트 일일퀘스트.03", "7k"],
    ["이벤트 일일퀘스트.04", "10k"],
];

var check = [
    [[8643003, 3000], [8643004, 3000]],
    [[8644008, 5000], [8644009, 5000]],
    [[8644405, 7000], [8644406, 7000]],
    [[8644508, 10000], [8644509, 10000]]
];

var reward = [
    [4310266, 500],
];

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
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
        var say = "  #fs14#   #i4001779##fc0xFFF781D8##e [강림월드] 사냥 페스티벌! #i4001779#\r\n#fs11##Cblue#              매일 1회만 클리어 가능합니다.#k\r\n\r\n";
        //say += "#L0##e#b레벨범위 몬스터 #r(-20~+10레벨) #b10000마리#d (무제한)#l\r\n\r\n";
        for (var i = 0; i < quest.length; i++) {
            say += "#L" + i + "##e#b[" + quest[i][0] + "]#n#k\r\n#d";
            for (var a = 0; a < check[i].length; a++) {
                say += "   ㄴ#o" + check[i][a][0] + "# " + check[i][a][1] + "마리";
            }
            say += "\r\n\r\n";
        }
        cm.sendSimple(say);

    } else if (status == 1) {
        sel = selection;
        if (cm.getPlayer().getKeyValue("Quest_" + quest[sel][1]) == Today) {
            cm.sendOk("이 퀘스트는 이미 완료된 퀘스트 입니다.");
            cm.dispose();
            return;
        } else if (cm.getPlayer().getKeyValue("Quest_" + quest[sel][1]) != 0) {
            cm.getPlayer().setKeyValue("Quest_" + quest[sel][1], 0);
            for (var a = 0; a < check[sel].length; a++) {
                cm.getPlayer().setKeyValue("Quest_" + check[sel][a][0], "0");
                cm.getPlayer().setKeyValue("QuestMax_" + check[sel][a][0], check[sel][a][1]);
            }
        }

        // 이게 null이면 다시 설정해줘야댐.
        if (cm.getPlayer().getKeyValue("QuestMax_" + quest[sel][0][0]) == null) {
            for (var a = 0; a < check[sel].length; a++) {
                cm.getPlayer().setKeyValue("QuestMax_" + check[sel][a][0], check[sel][a][1]);
            }
        }

        bClear = true;

        for (var a = 0; a < check[sel].length; a++) {
            if (Number(cm.getPlayer().getKeyValue("Quest_" + check[sel][a][0])) < check[sel][a][1]) {
                bClear = false;
                break;
            }
        }

        if (bClear) {
            var say = "<보상 목록>\r\n";
            for (var i = 0; i < reward.length; i++) {
                say += "#i" + reward[i][0] + "##z" + reward[i][0] + "# " + reward[i][1] + "개\r\n";
            }
            cm.sendYesNo("해당 퀘스트를 완료하기 위한 조건이 충족되었습니다. 퀘스트를 완료하시겠습니까?\r\n\r\n" + say);
        } else {
            var say = "#e#b[" + quest[sel][1] + " 퀘스트]#n#k\r\n\r\n";
            for (var i = 0; i < check[sel].length; i++) {
                count = cm.getPlayer().getKeyValue("Quest_" + check[sel][i][0]);
                say += "#o" + check[sel][i][0] + "# (" + count + " / " + check[sel][i][1] + ")마리\r\n";
            }
            say += "\r\n#e#b완료 마릿 수 도달시 퀘스트 완료가 가능합니다.\r\n\r\n";

/*            say += "#L0##e#r사냥터로 이동하기#l#k\r\n\r\n";
            say += "[첫번째 맵에서 이동시 두번째맵으로 이동합니다]\r\n\r\n";*/
            say += "#fc0xFF6600CC#<보상 목록>\r\n";
            for (var i = 0; i < reward.length; i++) {
                say += "#i" + reward[i][0] + "##z" + reward[i][0] + "# " + reward[i][1] + "개\r\n";
            }
            cm.sendSimple(say);
        }

    } else if (status == 2) {
        cm.dispose();
        bClear = true;

        for (var a = 0; a < check[sel].length; a++) {
            if (Number(cm.getPlayer().getKeyValue("Quest_" + check[sel][a][0])) < check[sel][a][1]) {
                bClear = false;
                break;
            }
        }
        if (!bClear) {
            return;
        }
            for (var i = 0; i < reward.length; i++) {
                if (!cm.canHold(reward[i][0], reward[i][1])) {
                    cm.sendOk("인벤토리 슬롯이 부족하거나 꽉 찬 것은 아닌지 확인해 주세요.");
                    return;
                }
            }

            cm.getPlayer().setKeyValue("Quest_" + quest[sel][1], Today);

            for (var i = 0; i < reward.length; i++) {
                cm.gainItem(reward[i][0], reward[i][1]);
            }

            cm.sendOk("퀘스트가 완료되어 보상이 지급되었습니다. 인벤토리를 확인해 주세요.");
    }
}