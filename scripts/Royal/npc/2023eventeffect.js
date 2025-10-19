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
	cm.delay(1200);
    } else if (status == 1) {
	cm.setBlind(0, 0, 0, 0, 0, 1000, 0); //off
	cm.effectText("#fn나눔고딕 ExtraBold##fs20#<해맞이 고개>", 100, 1000, 6, 0, -50, -100);
	cm.delay(3000);
    } else if (status == 2) {
	cm.effectPlay("Effect/OnUserEff.img/emotion/whatl", 0, 0, 0, 0, 0, false);
	cm.delay(3000);
    } else if (status == 3) {
	cm.sayNpc("여기는어디지..?", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 4) {
	cm.sayNpc("왜 갑자기 여기로 끌려온걸까.....", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 5) {
	cm.delay(1000);
    } else if (status == 6) {
	cm.delay(1000);
    } else if (status == 7) {
	cm.delay(1000);
    } else if (status == 8) {
	cm.sayReplacedNpc("헤헤! 기다렸어요!..#fs30##r반가워요! 반갑죠? 환영해요!", false, true, 1, 2074118, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 9) {
	cm.sayReplacedNpc("얼마나 기다렸는지 알아요?!", false, true, 1, 2074118, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 10) {
	cm.sayReplacedNpc("진정해, 심청아...모처럼 찾아온 사람을 놀라게 하면 못써.", false, true, 1,  2071005, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 11) {
	cm.sayReplacedNpc("아앗... 죄송해요 오랜만에 외부인을 만나서", false, true, 1, 2074118, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 12) {
	cm.sayReplacedNpc("후후. #e#b#h0##k#n \r\n드디어 왔구나. 강림월드의 #e#b모험가#k 맞지?", false, true, 1, 2071002, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 13) {
	cm.sayNpc("네 맞아요", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 14) {
	cm.sayReplacedNpc("이곳은 #r<해맞이 고개>#k이야.\r\n저기 방패연을 날리는 #b#e월묘#n#k 너무 귀엽지않아?", false, true, 1, 2071002, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 15) {
	cm.sayNpc("#b월묘#k..? 귀엽긴하네요..?\r\n그런데 저를 왜 기다리신건가요..?", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 16) {
	cm.sayReplacedNpc("저희는 항상 이곳에서 설날을 맞이하여\r\n이곳에 놀러오는 #b모험가#k님들께 #b인사#k를 드리고있어요", false, true, 1,  2071005, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 17) {
	cm.sayNpc("어... 그 사탕 나도 있으면 좋겠는데", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 18) {
	cm.sayReplacedNpc("#i4317001# #i4317002# #i4317003#\r\n낚시와 사냥시 일정확률로 이 세가지 사탕을 얻을 수 있어요!", false, true, 1,  2071005, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 19) {
	cm.sayNpc("그 사탕은 어디에 쓰는거야?", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 20) {
	cm.sayReplacedNpc("세가지 사탕을 모두 가지고 #b놀부#k님에게 말을걸면\r\n#i4310198# #b#z4310198##k 으로 교환할 수 있어요!", false, true, 1,  2074118, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 21) {
	cm.sayNpc("그 설날 코인은 어디에 쓰는건데?", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 22) {
	cm.sayReplacedNpc("#b이벤트 코인샵#k에서 다양한 아이템을 구매할 수 있지\r\n물론 코인샵도 #b나#k에게 말을 걸어보렴", false, true, 1, 2071002, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 23) {
	cm.sayReplacedNpc("우리는 이만 사탕을 배달하러 가야해\r\n언제나 #b< 강림월드 >#k 에서 재밌는 모험이 되길 바래", false, true, 1, 2071002, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 24) {
	cm.sayNpc("ㄱ...고마워\r\n#fs12#(별것도 아닌데 설명이 거창하네...)", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 25) {
	cm.delay(1000);
    } else if (status == 26) {
	cm.sayReplacedNpc("#fs25#얼른 안오고 뭐해!!", false, true, 1, 2071002, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 27) {
	cm.sayReplacedNpc("저희도 이만 가볼게요 좋은 모험되세요 !", false, true, 1, 2071005, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 28) {
	cm.sayReplacedNpc("#fs30##r안녕히가세요 모험가님!!!! 헤헤", false, true, 1, 2074118, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    } else if (status == 29) {
	cm.delay(1000);
    } else if (status == 30) {
	cm.delay(1000);
    } else if (status == 31) {
	cm.sayNpc("나도이제 이벤트를 즐기러 가볼까나?", GameObjectType.User, false, true, 1, ScriptMessageFlag.Self, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NoEsc);
    } else if (status == 32) {
	cm.showEffect(false, "rootabyss/firework");
	cm.delay(5000);
    } else if (status == 33) {
	cm.setStandAloneMode(false);
	cm.endIngameDirectionMode(1);
	cm.warp(ServerConstants.TownMap, 0);
	cm.dispose();
	cm.openNpcCustom(cm.getClient(), 2071002, "2023event");
    }
}