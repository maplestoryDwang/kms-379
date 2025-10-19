importPackage(java.lang);
importPackage(java.text);

importPackage(Packages.scripting);
importPackage(Packages.constants);

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
    if (status == 0) {
	cm.setIngameDirectionMode(false, false, false);
	cm.setBlind(1, 255, 0, 0, 0, 0, 0);
	cm.setStandAloneMode(true);
	cm.spawnLocalNpc(9001114, 708, 608, 0, 53, true);
	cm.spawnLocalNpc(9001115, 929, 608, 1, 53, true);
	cm.spawnLocalNpc(9001116, 1099, 608, 2, 53, true);
	cm.delay(1200);
    } else if (status == 1) {
	cm.setBlind(0, 0, 0, 0, 0, 1000, 0); //off
	cm.effectText("#fn나눔고딕 ExtraBold##fs20#<달맞이 언덕>", 100, 1000, 6, 0, -50, -100);
	cm.delay(3000);
    } else if (status == 2) {
	cm.effectPlay("Effect/OnUserEff.img/emotion/whatl", 0, 0, 0, 0, 0, false);
	cm.delay(3000);
    } else if (status == 3) {
	cm.sayNpc("여기는어디지..?", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 4) {
	cm.sayNpc("저 토끼들은 대체뭐야.....", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 5) {
	cm.effectPlay("Effect/OnUserEff.img/emotion/oh", 0, 0, 0, 0, 9001114, false);
	cm.delay(1000);
    } else if (status == 6) {
	cm.effectPlay("Effect/OnUserEff.img/emotion/oh", 0, 0, 0, 0, 9001115, false);
	cm.delay(1000);
    } else if (status == 7) {
	cm.effectPlay("Effect/OnUserEff.img/emotion/oh", 0, 0, 0, 0, 9001116, false);
	cm.delay(2000);
    } else if (status == 8) {
	cm.sayReplacedNpc("헤헤! 기다렸어요!..#fs30##r반가워요! 반갑죠? 환영해요!", false, true, 1, 9001116, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 9) {
	cm.sayReplacedNpc("얼마나 기다렸는지 알아요?!", false, true, 1, 9001116, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 10) {
	cm.sayReplacedNpc("진정해, 아가야...모처럼 찾아온 사람을 놀라게 하면 못써.", false, true, 1,  9001115, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 11) {
	cm.sayReplacedNpc("아앗... 죄송해요 오랜만에 사람을 만나서", false, true, 1, 9001116, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 12) {
	cm.sayReplacedNpc("후후. #e#b#h0##k. \r\n드디어 왔구나. 우리는 #b월묘#k라고 해.", false, true, 1, 9001114, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 13) {
	cm.sayNpc("월묘...?", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 14) {
	cm.sayReplacedNpc("이곳은 #r<달맞이 언덕>#k이야.\r\n달빛이 겹쳐진 곳에 비치는 덧없는 #b#e환상#n#k이지", false, true, 1, 9001114, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 15) {
	cm.sayNpc("#b달맞이 언덕#k..? #b환상#k..?\r\n그런데 저를 왜 기다리신건가요..?", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 16) {
	cm.sayReplacedNpc("저희는 항상 이곳에서 송편을 빚고있어요\r\n추석을 맞이하여 이곳에 놀러오는 #b모험가#k님들께 #b인사#k를 드리고있어요", false, true, 1,  9001115, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 17) {
	cm.sayNpc("어... 그 송편 나도 있으면 좋겠는데", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 18) {
	cm.sayReplacedNpc("#i3994408# #i3994409# #i3994410#\r\n낚시와 사냥시 일정확률로 이 세가지 송편을 얻을 수 있어요!", false, true, 1,  9001115, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 19) {
	cm.sayNpc("그 송편은 어디에 쓰는거야?", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 20) {
	cm.sayReplacedNpc("세가지 송편을 모두 가지고 #b아기 월묘#k에게 말을걸면\r\n#i4310185# #b#z4310185##k 으로 교환할 수 있어요!", false, true, 1,  9001116, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 21) {
	cm.sayNpc("그 추석코인은 어디에 쓰는건데?", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 22) {
	cm.sayReplacedNpc("#b이벤트 코인샵#k에서 다양한 아이템을 구매할 수 있지\r\n물론 코인샵도 #b아기 월묘#k에게 말을 걸어보렴", false, true, 1, 9001114, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 23) {
	cm.sayReplacedNpc("우리는 이만 송편을 배달하러 가야해\r\n언제나 #b< 강림월드 >#k 에서 재밌는 모험이 되길 바래", false, true, 1, 9001114, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 24) {
	cm.sayNpc("ㄱ...고마워\r\n#fs12#(별것도 아닌데 설명이 거창하네...)", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 25) {
	cm.removeLocalNpc(9001114);
	cm.delay(1000);
    } else if (status == 26) {
	cm.sayReplacedNpc("#fs25#얼른 안오고 뭐해!!", false, true, 1, 9001114, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 27) {
	cm.sayReplacedNpc("저희도 이만 가볼게요 좋은 모험되세요 !", false, true, 1, 9001115, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 28) {
	cm.sayReplacedNpc("#fs30##r안녕히가세요 모험가님!!!! 헤헤", false, true, 1, 9001116, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 29) {
	cm.delay(1000);
    } else if (status == 30) {
	cm.removeLocalNpc(9001115);
	cm.delay(1000);
    } else if (status == 31) {
	cm.removeLocalNpc(9001116);
	cm.delay(1000);
    } else if (status == 32) {
	cm.sayNpc("나도이제 이벤트를 즐기러 가볼까나?", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 33) {
	cm.showEffect(false, "rootabyss/firework");
	cm.delay(5000);
    } else if (status == 34) {
	cm.setStandAloneMode(false);
	cm.endIngameDirectionMode(1);
	cm.warp(ServerConstants.TownMap, 0);
	cm.dispose();
	cm.openNpcCustom(cm.getClient(), 9001102, "2023cntjrevent");
    }
}