importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);

importPackage(Packages.scripting);

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
            cm.sayNpc("#fs11#그러면 게임에 참여하고 싶을 때 다시 찾아와줘~", GameObjectType.Npc, false, false, ScriptMessageFlag.NpcReplacedByNpc);
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
            v0 = "#fs11#미니게임에서 실력을 보여주면 #r#e#i4310307:# #t4310307##n#k을 줄게!\r\n";
            v0 += "히힛! 나보다 게임을 잘하는 사람은 없을걸?!\r\n";
            v0 += "#L3# #r#e<초능력 윷놀이>#n 참여하기 [2인]#l#k\r\n";
            v0 += "#L30# #b#e<초능력 윷놀이>#n 설명 듣기#l#k\r\n\r\n";
            v0 += "#L6# #r#e<배틀 리버스>#n 참여하기 [2인]#l#k\r\n\r\n";
            v0 += "#L4# #r#e<메이플 원카드>#n 참여하기 [4인]#l#k\r\n\r\n";
            v0 += "#L5# #r#e<메이플 사커>#n 참여하기 [10인]#l#k\r\n";

            v0 += "\r\n#L100# 더 이상 궁금한 것이 없어.#l\r\n\r\n\r\n";
            //v0 += "#r※ #t4310307#은 캐릭터마다 일일 100개까지 획득 가능합니다.#k";

            cm.askMenu(v0, GameObjectType.Npc, ScriptMessageFlag.NpcReplacedByNpc);
        } else if (status == 1) {
            sel = selection;
            if (sel == 30) {
                v0 = "#fs11##b#e<초능력 윷놀이>#k#n는 윷을 던진 결과로 말을 움직여서\r\n자신의 말을 먼저 모두 골인시키면 승리하는 게임이야!";

                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 100) {
                cm.dispose();
            } else if (sel == 3) {
                cm.askYesNo("#fs11#지금 바로 #e#b<초능력 윷놀이>#k#n에 참여하겠어?\r\n\r\n#r(승낙 시 대기열에 등록됩니다.)#k", GameObjectType.Npc, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 4) {
                cm.askYesNo("#fs11#지금 바로 #e#b<메이플 원카드>#k#n에 참여하겠어?\r\n\r\n#r(승낙 시 대기열에 등록됩니다.)#k", GameObjectType.Npc, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 5) {
                cm.askYesNo("#fs11#지금 바로 #e#b<메이플 사커>#k#n에 참여하겠어?\r\n\r\n#r(승낙 시 대기열에 등록됩니다.)#k", GameObjectType.Npc, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 6) {
                cm.askYesNo("#fs11#지금 바로 #e#b<배틀 리버스>#k#n에 참여하겠어?\r\n\r\n#r(승낙 시 대기열에 등록됩니다.)#k", GameObjectType.Npc, ScriptMessageFlag.NpcReplacedByNpc);
            }
        } else if (status == 2) {
            if (sel == 30) {
                v0 = "#fs11#게임이 시작되면 #b#e2개의 일회성 초능력#k#n이 랜덤하게 선택되고, 자신의 차례에 윷을 던져 결과에 따라 말의 위치를 이동시킬 수 있지.";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);


            } else if (sel == 3 || sel == 4 || sel == 5 || sel == 6) {
                var canTime = cm.getPlayer().getOneInfoQuestLong(1234569, "miniGame1_can_time");
                var now = System.currentTimeMillis();
                var delta = parseInt((canTime - now));
                if (canTime != 0 && delta > 0) {
                    var minute = parseInt((delta / 1000 / 60));
                    cm.sayNpc("#fs11#" + minute + "분 후에 입장할 수 있습니다.", GameObjectType.Npc, false, false, ScriptMessageFlag.NpcReplacedByNpc);
                    cm.dispose();
                    return;
                }

                if (cm.getClient().getChannel() != 1) {
                    cm.sayNpc("#fs11#멀티 미니게임은 1채널에서만 참여가 가능합니다.", GameObjectType.Npc, false, false, ScriptMessageFlag.NpcReplacedByNpc);
                    cm.dispose();
                    return;
                }

                if (sel == 3) {
                    cm.registerWaitQueue(993189800); // 대기열 참가
                    //cm.worldGMMessage(6, "[미니게임] " + cm.getPlayer().getName() + "님이 [ 초능력 윷놀이 ] 대기열에 참가하였습니다.");
                    cm.worldGMMessage(6, "[미니게임] " + cm.getPlayer().getName() + "님이 [ 초능력 윷놀이 ] 대기열에 참가하였습니다. [" + cm.loadWaitQueue(993189800) + " / 2]");
                } else if (sel == 4) {
                    cm.registerWaitQueue(993189400); // 대기열 참가
                    //cm.worldGMMessage(6, "[미니게임] " + cm.getPlayer().getName() + "님이 [ 메이플 원카드 ] 대기열에 참가하였습니다.");
                    cm.worldGMMessage(6, "[미니게임] " + cm.getPlayer().getName() + "님이 [ 메이플 원카드 ] 대기열에 참가하였습니다. [" + cm.loadWaitQueue(993189400) + " / 4]");
                } else if (sel == 5) {
                    cm.registerWaitQueue(993195100); // 대기열 참가
                    //cm.worldGMMessage(6, "[미니게임] " + cm.getPlayer().getName() + "님이 [ 메이플 사커 ] 대기열에 참가하였습니다.");
                    cm.worldGMMessage(6, "[미니게임] " + cm.getPlayer().getName() + "님이 [ 메이플 사커 ] 대기열에 참가하였습니다. [" + cm.loadWaitQueue(993195100) + " / 10]");
                } else if (sel == 6) {
                    cm.registerWaitQueue(993189600); // 대기열 참가
                    //cm.worldGMMessage(6, "[미니게임] " + cm.getPlayer().getName() + "님이 [ 배틀 리버스 ] 대기열에 참가하였습니다.");
                    cm.worldGMMessage(6, "[미니게임] " + cm.getPlayer().getName() + "님이 [ 배틀 리버스 ] 대기열에 참가하였습니다. [" + cm.loadWaitQueue(993189600) + " / 2]");
                }

                cm.dispose();
            }
        } else if (status == 3) {
            if (sel == 30) {
                v0 = "#fs11#던진 윷의 결과가 윷 또는 모가 나오거나,\r\n상대방의 말을 잡으면 윷을 한 번 더 던질 수 있어!";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            }
        } else if (status == 4) {
            if (sel == 30) {
                v0 = "#fs11##b#e초능력#k#n을 쓰려면 내 차례에서 #b#e윷을 던지기 전에#k#n 사용할 초능력을 먼저 선택해야 해.";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            }
        } else if (status == 5) {
            if (sel == 30) {
                v0 = "#fs11#초능력을 사용하고 윷을 던지면\r\n윷 결과에 초능력을 연계할 수 있어!\r\n\r\n#r(주의! 초능력을 선택해서 발동시켰지만 사용하지 않고\r\n제한 시간이 넘어가면 초능력이 사라집니다.)#k";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            }
        } else if (status == 6) {
            if (sel == 30) {
                v0 = "#fs11#자신의 말 4개를 먼저 모두 골인시킨 사람이 승리하고\r\n승패 결과에 따라 더 많은 보상을 받아 갈 수 있어!\r\n\r\n#b#e<초능력 윷놀이 보상>#k#n\r\n - 승리 : #i4310307:# #b#t4310307:# 60개#k\r\n - 무승부 : #i4310307:# #b#t4310307:# 30개#k\r\n - 패배 : #i4310307:# #b#t4310307:# 20개#k\r\n\r\n.#k";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            }
        } else if (status == 7) {
            if (sel == 30) {
                v0 = "#fs11#참고로 자신의 차례에 제한 시간 동안 윷을 던지지 않거나 말을 움직이지 않으면 자동으로 진행될 거야.";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            }
        } else if (status == 8) {
            if (sel == 30) {
                v0 = "#fs11#5번이 반복되면 강제로 퇴장되고\r\n보상을 받아 갈 수 없으니 주의해줘!";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            }
        } else if (status == 9) {
            if (sel == 30) {
                cm.dispose();
            }
        }
    }
}