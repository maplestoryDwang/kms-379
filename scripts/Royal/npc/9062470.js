importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);

importPackage(Packages.scripting);
importPackage(Packages.constants);

var status = -1;
var sel = -1;
var sel2 = -1;

function start() {
    status = -1;
    sel = -1;
    sel2 = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && type == 3 && selection == -1) {
            //cm.sayNpc("그러면 게임에 참여하고 싶을 때 다시 찾아와줘~", GameObjectType.Npc, false, false, ScriptMessageFlag.NpcReplacedByNpc);
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        
        if (status == 0) {
            var neoGem = cm.getPlayer().getOneInfoQuestInteger(1234569, "miniGame4_coin");
            var canGain = cm.getPlayer().canGainStackEventGauge(1); // 코어 젬 금일 획득 가능량
            var v0 = "재밌게 놀았어~?\r\n\r\n";
            if (canGain <= 0) {
                v0 += "이미 #e일일 제한량#n을 달성해서 보상을 더 줄 수 없어~\r\n\r\n";
            } else {
                v0 += "#e#b네오 젬 " + parseInt(Math.round(neoGem/100)) + "개#k#n를 챙겨줄게!\r\n\r\n";
            }
            v0 += "#r(보상은 퇴장 시 지급됩니다)#k";

            cm.sayNpc(v0, GameObjectType.Npc, ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
        } else if (status == 1) {
            var neoGem = cm.getPlayer().getOneInfoQuestInteger(1234569, "miniGame4_coin");
            var prevneoGem = cm.getPlayer().getKeyValue(100712, "point");
            var plusneoGem = parseInt(Math.round(neoGem/100));

            //cm.getPlayer().gainStackEventGauge(1, parseInt(Math.round(neoGem/100)), false);
            cm.getPlayer().setKeyValue(100712, "point", prevneoGem + plusneoGem);
            cm.getPlayer().updateOneInfo(1234569, "miniGame4_coin", "0");
            cm.warp(ServerConstants.TownMap, 0);
            cm.dispose();
        }
    }
}