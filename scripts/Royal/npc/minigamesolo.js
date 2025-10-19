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
        } else if (mode == 0 && type == 6) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        
        if (status == 0) {
            v0 = "#fs11#내가 준비한 미니게임에서 #b#e#i4310307:# #t4310307##n#k을 얻어봐~\r\n";
            v0 += "#L1# #r#e<알록달록 초대장>#n 참여하기#l#k\r\n";
            v0 += "#L2# #b#e<알록달록 초대장>#n 설명듣기#l#k\r\n\r\n";
            v0 += "#L3# #r#e<높이높이 로얄캐슬>#n 참여하기#l#k\r\n";
            v0 += "#L4# #b#e<높이높이 로얄캐슬>#n 설명듣기#l#k\r\n\r\n";
            v0 += "#L5# #r#e<레인보우 러쉬>#n 참여하기#l#k\r\n";
            v0 += "#L6# #b#e<레인보우 러쉬>#n 설명듣기#l#k\r\n\r\n";
            v0 += "#L100# 더 이상 궁금한 것이 없어.#l\r\n";

            cm.askMenu(v0, GameObjectType.Npc, ScriptMessageFlag.NpcReplacedByNpc);
        } else if (status == 1) {
            if (sel == -1) {
                sel = selection;
            }
            if (sel == 2) {
                var v0 = "#b#e알록달록 초대장#n#k은 로얄 캐슬로 사람들을 초대하기 위해\r\n초대장을 보내는 것이 목표인 게임이야~";
                cm.sayNpc(v0, GameObjectType.Npc, false, true, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 1) {
                var v0 = "지금 #e#b<알록달록 초대장>#n#k에 참여할래~?\r\n\r\n#r(게임 중에는 해상도가 1024x768로 변경됩니다.)#k";
                cm.askYesNo(v0, GameObjectType.Npc, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 3) {
                var v0 = "지금 #e#b<높이높이 로얄캐슬>#n#k에 참여할래~?\r\n\r\n#r(게임 중에는 해상도가 1024x768로 변경됩니다.)#k";
                cm.askYesNo(v0, GameObjectType.Npc, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 4) {
                var v0 = "#b#e높이높이 로얄캐슬#n#k은 우리만의 탑을 높게 쌓아\r\n올리는 게임이야~";
                cm.sayNpc(v0, GameObjectType.Npc, false, true, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 5) {
                var v0 = "지금 #e#b<레인보우 러쉬>#n#k에 참여할래~?\r\n\r\n#r(게임 중에는 해상도가 1366x768로 변경됩니다.)#k";
                cm.askYesNo(v0, GameObjectType.Npc, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 6) {
                var v0 = "#b#e레인보우 러쉬#n#k는 이쁜 무지갯빛 길을 달리는 게임이야~";
                cm.sayNpc(v0, GameObjectType.Npc, false, true, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 100) {
                cm.dispose();
            }
        } else if (status == 2) {
            if (sel == 2) {
                var v0 = "초대장을 보내기 위해선 대기하고 있는 초대장과\r\n#b#e동일한 색상의 버튼#n#k을 누르기만 하면 돼~";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 1) {
                cm.enterMission2Space();
                cm.dispose();
                return;
            } else if (sel == 3) {
                cm.enterBuzzingHouse();
                cm.dispose();
                return;
            } else if (sel == 5) {
                cm.enterExtremeRail();
                cm.dispose();
                return;
            } else if (sel == 4) {
                var v0 = "한 층 한 층이 옆에서 나타나면 #r#eSpace#k#n 키를 눌러서 멈출 수 있는데, 이때 집중해서 #e#b타이밍#k#n을 잘 맞추는 것이 중요해~";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 6) {
                var v0 = "신비한 #b#e눈꽃 순록#n#k을 타고 무지갯빛 길을 마음껏 달려봐~";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            }
        } else if (status == 3) {
            if (sel == 2) {
                var v0 = "초대장에는 #b#e3가지 종류#n#k가 있어~\r\n\r\n각각 다른 모양과 색깔을 지니고 있으니 구분은 쉬울 거야~\r\n\r\n#i03801591##i03801592##i03801593#";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 4) {
                var v0 = "범위 내에 멈추지 못한다고 무조건 실패하는 건 아니지만,\r\n쌓는 층의 폭이 줄어들 거야~\r\n\r\n물론 완전히 다른 위치에 멈추면 바로 #e<GAME OVER>#n~";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 6) {
                var v0 = "점점 더 빨라지는 눈꽃 순록을 타고 #e#b좌우 방향키#k#n를 이용해 장애물을 피해야 해~\r\n\r\n#b#e순발력#n#k이 필요하겠지~?";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            }
        } else if (status == 4) {
            if (sel == 2) {
                var v0 = "하지만 주의해야 할 #r#e특별한 초대장#n#l#k이 하나 있어~\r\n#i03801594#\r\n이 초대장은 #b#e동서남북 종이접기 모양#n#k을 띠고 있는데\r\n본 모습을 숨기고 있다가 전송 직전에 앞서 말한 세 초대장의 모습으로 변할 거야~";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 4) {
                var v0 = "폭이 줄어들수록 높이 쌓기 더 어렵다는 건 알겠지~?\r\n그래서 높은 #e#b집중력#k#n이 필요해~";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 6) {
                var v0 = "길을 가로막는 #r#e먹구름#n#l#k을 주의해~ \r\n\부딪히면 그대로 #e<GAME OVER>#n~";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            }
        } else if (status == 5) {
            if (sel == 2) {
                var v0 = "그리고 초대장을 보내다 보면 #r#e피버 게이지#n#l#k가 차오를 거야~\r\n\r\n#r#e피버 게이지#n#l#k가 가득 차면 #b#e스페이스 바#n#k를 빠르게 눌러\r\n더 빠르게 초대장을 보낼 수 있어~\r\n\r\n#i3801199#";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 4) {
                var v0 = "마지막으로 탑을 높이 쌓을수록 더 많은 #b#e보상#n#k을 줄게~\r\n이제 #b#e높이높이 로얄캐슬#n#k에 대해 이해했어~?\r\n\r\n어서 시작하자~";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 6) {
                var v0 = "마지막으로 더 멀리 달릴수록 더 많은 #b#e보상#n#k을 줄게~\r\n이제 #b#e레인보우 러쉬#n#k에 대해 이해했어~?\그럼 시작하자~";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            }
        } else if (status == 6) {
            if (sel == 2) {
                var v0 = "아름다운 로얄캐슬에 많은 사람이 올 수 있도록 초대장을\r\n많이 보내줘~!\r\n\r\n물론 #b#e보상#n#k도 빠짐없이 줄게~";
                cm.sayNpc(v0, GameObjectType.Npc, true, true, ScriptMessageFlag.NpcReplacedByNpc);
            } else if (sel == 4 || sel == 6) {
                cm.dispose();
            }
        } else if (status == 7) {
            if (sel == 2) {
                cm.dispose();
            }
        }
    }
}