/*일일 퀘스트
 소멸의 여로     기쁨의 에르다스 100마리,분노의 에르다스 100마리 잡기	기쁨-8641000 분노-8641001				
 츄츄아일랜드    크릴라 100마리,버샤크 100마리 잡기			크릴라-8642012 버샤크-8642014				
 레헬른          성난 무도회주민100마리,광기의 무도회주민 100마리 잡기		성난-8643008 광기-8643009				
 아르카나        비탄의 정령200마리,절망의 정령 200마리 잡기			비탄-8644009 절망-8644008				
 모라스          푸른 그림자 200마리,붉은 그림자 200마리 잡기			푸른-8644405 붉은-8644406				
 에스페라        아라냐 300마리,아라네아 300마리 잡기			아라냐-8644504 아라네아-8644505	*/

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
    ["세르니움 1", "Cernium1"],
    ["세르니움 2", "Cernium2"],
    ["세르니움 3", "Cernium3"],
    ["불타는 세르니움 1", "Cernium4"],
    ["불타는 세르니움 2", "Cernium5"],
    ["불타는 세르니움 3", "Cernium6"],
    ["호텔 아르크스 1", "Arcs7"],
    ["호텔 아르크스 2", "Arcs8"],
    ["호텔 아르크스 3", "Arcs9"],
];

var check = [
    [[8645123, 300], [8645124, 300]],
    [[8645125, 300], [8645126, 300]],
    [[8645127, 300], [8645128, 300]],
    [[8645131, 300], [8645132, 300]],
    [[8645133, 300], [8645134, 300]],
    [[8645130, 300], [8645129, 300]],
    [[8645204, 300], [8645205, 300]],
    [[8645202, 300], [8645203, 300]],
    [[8645200, 300], [8645201, 300]],
];

var reward = [
    [2633336, 50]
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
        var say = "일일 퀘스트입니다. 수행하실 퀘스트를 선택해주세요.\r\n";
        //say += "#L0##e#b레벨범위 몬스터 #r(-20~+10레벨) #b10000마리#d (무제한)#l\r\n\r\n";
        for (var i = 0; i < quest.length; i++) {
            say += "#L" + i + "##e#b[" + quest[i][0] + "]#n#k\r\n#e#d";
            for (var a = 0; a < check[i].length; a++) {
                say += "   ㄴ#o" + check[i][a][0] + "# " + check[i][a][1] + "마리\r\n";
            }
            say += "\r\n";
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
            var say = "#e#b[" + quest[sel][1] + " 퀘스트]#n#k\r\n";
            for (var i = 0; i < check[sel].length; i++) {
                count = cm.getPlayer().getKeyValue("Quest_" + check[sel][i][0]);
                say += "#o" + check[sel][i][0] + "# (" + count + " / " + check[sel][i][1] + ")마리\r\n";
            }
            say += "#e#b몬스터를 다 잡은 후 퀘스트 완료가 가능합니다.\r\n";

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