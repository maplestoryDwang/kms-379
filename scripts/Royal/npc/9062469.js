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
            //cm.sayNpc("역시 장사는 쉽지 않군요...", GameObjectType.Npc, false, false, ScriptMessageFlag.NpcReplacedByNpc);
            cm.dispose();
            return;
        } else if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        
        if (status == 0) {
            var result = cm.getPlayer().getOneInfoQuestInteger(1234569, "miniGame1_result");
            var canGain = cm.getPlayer().canGainStackEventGauge(1); // 코어 젬 금일 획득 가능량
            
            if (result == 2) { // 항복
                var v0 = "도중에 포기하다니, 정말 실망이야!\r\n끈기가 없는 사람에겐 보상을 줄 수 없어.";
                cm.sayNpc(v0, GameObjectType.Npc, ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
            } else if (result == 1) { // 승리
                
                var v0 = "이번 게임의 승리자는 #b#h0##k! 제법인걸?\r\n\r\n";
                if (canGain <= 0) {
                    v0 += "오늘은 이미 #e#r일일 제한량#k#n을 달성해서 보상을 더 챙겨 줄 수 없어.";
                } else {
                    v0 += "#e#b네오 젬 60개#k#n를 챙겨줄게!\r\n\r\n#r(보상은 퇴장 시 지급됩니다)#k";
                }
                cm.sayNpc(v0, GameObjectType.Npc, ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
            } else if (result == 4) { // 패배
                var v0 = "아쉽지만 다음에 다시 승부를 내보자고!\r\n\r\n";
                if (canGain <= 0) {
                    v0 += "오늘은 이미 #e#r일일 제한량#k#n을 달성해서 보상을 더 챙겨 줄 수 없어.";
                } else {
                    v0 += "#e#b네오 젬 20개#k#n를 챙겨줄게!\r\n\r\n#r(보상은 퇴장 시 지급됩니다)#k";
                }
                cm.sayNpc(v0, GameObjectType.Npc, ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
            } else if (result == 3) { // 무승부
                var v0 = "무승부라니 아주 치열한 승부였어!\r\n\r\n";
                if (canGain <= 0) {
                    v0 += "오늘은 이미 #e#r일일 제한량#k#n을 달성해서 보상을 더 챙겨 줄 수 없어.";
                } else {
                    v0 += "#e#b네오 젬 30개#k#n를 챙겨줄게!\r\n\r\n#r(보상은 퇴장 시 지급됩니다)#k";
                }
                cm.sayNpc(v0, GameObjectType.Npc, ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
            } else {
                // 강제 종료
                var v0 = "도중에 나가다니, 정말 실망이야!\r\n끈기가 없는 사람에겐 보상을 줄 수 없어.";
                cm.sayNpc(v0, GameObjectType.Npc, ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
            }
        } else if (status == 1) {
            var result = cm.getPlayer().getOneInfoQuestInteger(1234569, "miniGame1_result");
            var canGain = cm.getPlayer().canGainStackEventGauge(1); // 코어 젬 금일 획득 가능량
            var prevneoGem = cm.getPlayer().getKeyValue(100712, "point");

            if (canGain > 0) {
                
                if (result == 1) {
                    cm.getPlayer().setKeyValue(100712, "point", prevneoGem + 60);
                    //cm.getPlayer().gainStackEventGauge(1, 60, false);
                } else if (result == 4) {
                    cm.getPlayer().setKeyValue(100712, "point", prevneoGem + 20);
                    //cm.getPlayer().gainStackEventGauge(1, 20, false);
                } else if (result == 3) {
                    cm.getPlayer().setKeyValue(100712, "point", prevneoGem + 30);
                    //cm.getPlayer().gainStackEventGauge(1, 30, false);
                }
            }
            cm.warp(ServerConstants.TownMap, 0);
            cm.dispose();
        }
    }
}