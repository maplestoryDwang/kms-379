importPackage(java.lang);

var status = -1;
var s = -1;
var gameType = -1;

var rewards = [
    "독수리 사냥", "보물 받기", "드래곤의 알 훔치기", "구애의 춤"
];

function start() {
    s = -1;
    gameType = -1;
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, sel) {
    if (mode == 0 && sel == -1 && type == 6) {
        cm.getPlayer().removeRandomPortal();
        cm.dispose();
        return;
    }
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        status--;
    }
    if (mode == 1) {
        status++;
    }
    if (status == 0) {
        if (cm.getPlayer().getMapId() == 993000300) {
            var stage = cm.getPlayer().getOneInfoQuestInteger(15142, "Stage");
            if (stage == 0) {
                cm.sendNextNoESC("이런이런... 첫 번째 단계도 넘지 못하다니. 운이 없군 그래?");
            } else if (stage == 1) {
                cm.sendNextNoESC("이런이런... 두 번째 단계에서 실패하다니. 다음에는 잘 해봐!");
            } else if (stage == 2) {
                cm.sendNextNoESC("호오... 세 번째 단계까지 오다니. 다음에는 꼭 성공할거야!");
            } else if (stage == 3) {
                cm.sendNextNoESC("와~ 네 번재 단계까지 오다니. 아까웠어!");
            } else if (stage == 4) {
                cm.sendNextNoESC("정말 아쉽다... 이번 단계만 넘겼으면 성공했을텐데. 다음에는 꼭 성공할거야!");
            } else {
                cm.sendNextNoESC("혹시 비정상적인 방법으로 접근한건 아니니?");
            }
        } else if (cm.getPlayer().getMapId() == 993000601) {
            gameType = cm.getPlayer().getOneInfoQuestInteger(26022, "gameType");
            var v0 = "수고했어! 생각보다 대단한 실력을 가졌는걸?\r\n\r\n#b";
            if (gameType != -1) {
                v0 += "#L0#" + rewards[gameType] + " 보상 받기#l\r\n";
            }
            v0 += "#L1#원래 있던곳으로 보내줘.#l";
            cm.sendSimple(v0);
        } else {
	if (!cm.getPlayer().CountCheck("random_portal", 20)) {
		cm.sendNext("오늘은 더 이상 입장할 수 없다. 다음에 다시 보도록 하지.");
		cm.dispose();
		return;
	}
            cm.sendNext("안녕? 나는 현상금 사냥꾼 #e#b프리토#k#n라고해.\r\n형 #e#r폴로#n#k와 함께 엄청난 명성을 떨치고 있지! 하핫!");
        }
    } else if (status == 1) {
        if (cm.getPlayer().getMapId() == 993000300) {
            cm.warp(993000601);
            cm.dispose();
            return;
        } else if (cm.getPlayer().getMapId() == 993000601) {
            if (sel == 0) {
                if (!cm.canHold(2434639, 2)) {
                    cm.sendNext("인벤토리 공간이 있는지 확인하고 다시 말 걸어주겠어?");
                    cm.dispose();
                    return;
                }
                if (gameType == 1) { // 보물 받기
                    cm.sendNextNoESC("나의 완벽한 #b#e보물 서리#n#k는 잘 보았지?\r\n불꽃늑대과 돌아와서 #e#r텅 빈 보물창고#k#n를 보면 화가 잔뜩 나겠지? 히힛!");
                    var score = cm.getPlayer().getOneInfoQuestInteger(26022, "score");
                    var itemID = 0;
                    var quantity = 0;
                    var exp = 0;

                    var avgExp = cm.getPlayer().getOneInfoQuestInteger(26022, "exp");
                    if (score >= 2000) {
                        itemID = 2434635;
                        quantity = 2;
                        exp = parseInt(avgExp) * 6000;
                    } else if (score >= 1000 && score < 2000) {
                        itemID = 2434635;
                        quantity = 1;
                        exp = parseInt(avgExp) * 4000;
                    } else {
                        itemID = 2434634;
                        quantity = 1;
                        exp = parseInt(avgExp) * 2000;
                    }
                    cm.getPlayer().updateOneInfo(26022, "exp", "");
                    cm.getPlayer().updateOneInfo(26022, "score", "");
                    cm.getPlayer().updateOneInfo(26022, "gameType", "-1");
                    cm.gainItem(itemID, quantity);
                    cm.gainExp(exp);
                } else if (gameType == 0) { // 독수리 사냥
                    cm.sendNextNoESC("하하! #b독수리 사냥#k은 어땠어?\r\n멀리서 총으로 쏘아 잡으니 생각보다 쉽지?");
                } else if (gameType == 2) { // 드래곤 알 차지
                    cm.sendNextNoESC("휴~ 큰일 날 뻔 했군! 하마터면 #b드래곤#k에게 걸릴 뻔 했어!\r\n살금살금 올라가는 너의 모습을 보니 너도 나처럼 훌륭한 현상금 사냥꾼이 될 것 같더군 하핫!");
                } else if (gameType == 3) { // 닭에게 구애의 춤
                    cm.sendNextNoESC("나의 #e#b완벽한 위장 능력#n#k은 잘 보았겠지?\r\n하핫! 암탉들은 나의 #e#b구애의 춤#n#k에 홀딱 넘어왔어!");
                }
            } else if (sel == 1) {
                cm.getPlayer().updateOneInfo(26022, "exp", "");
                cm.getPlayer().updateOneInfo(26022, "score", "");
                cm.getPlayer().updateOneInfo(26022, "gameType", "-1");
                var map = cm.getPlayer().getOneInfoQuestInteger(26022, "map");
                if (map == 0) {
                    map = 15;
                }
                cm.warp(map);
                cm.dispose();
            }
        } else {
            cm.sendSimple("남들은 나를 어수룩하다고 생각하지만 사실 나는 엄청난 실력의 소유자야. 어때, 나와 함께 모험을 하지 않겠어?\r\n\r\n#b#L0#함께 한다.#l\r\n#L1#함께하지 않는다.#l");
        }
    } else if (status == 2) {
        if (cm.getPlayer().getMapId() == 993000601) {
            if (gameType == 0) {
                cm.sendNextNoESC("형처럼 정면에서 싸우는 것 만이 능사는 아니지! 나처럼 머리를 쓸 줄 아는 사냥꾼이야 말로 진정한 사냥꾼이라 할 수 있다고!");
            } else if (gameType == 2) {
                var stage = cm.getPlayer().getOneInfoQuestInteger(15142, "Stage");

                var itemID = 0;
                var quantity = 0;
                var exp = 0;
                var avgExp = cm.getPlayer().getOneInfoQuestInteger(26022, "exp");

                if (stage == 5) {
                    itemID = 2434636;
                    quantity = 2;
                    exp = parseInt(avgExp) * 12000;
                } else if (stage >= 3 && stage < 5) {
                    itemID = 2434635;
                    quantity = 1;
                    exp = parseInt(avgExp) * 10000;
                } else if (stage == 2) {
                    itemID = 2434634;
                    quantity = 1;
                    exp = parseInt(avgExp) * 4000;
                } else if (stage <= 1) {
                    itemID = 2434633;
                    quantity = 1;
                    exp = parseInt(avgExp) * 2000;
                }

                if (stage < 5 && stage >= 1) {
                    cm.sendNextNoESC("이번에는 #e#b" + stage + "#n#k단계 까지 올라갔군?\r\n자 여기 너의 몫으로 #b#i" + itemID + "##z" + itemID + "##k와 #b경험치#k를 줄게! 다음에 또 보자고!");
                } else if (stage == 0) {
                    cm.sendNextNoESC("아쉽게도 한층도 올라가지 못했군?\r\n자 여기 너의 몫으로 #b#i" + itemID + "##z" + itemID + "##k와 #b경험치#k를 줄게! 다음에 또 보자고!");
                } else {
                    cm.sendNextNoESC("아닛! 너 #b드래곤의 알#k을 훔치는데 성공 했구나? 대단해 대단해! 자 여기 너의 몫으로 #b#i" + itemID + "##z" + itemID + "##k와 #b경험치#k를 줄게! 다음에 또 보자고!");
                }

                cm.getPlayer().updateOneInfo(15142, "Stage", "");
                cm.getPlayer().updateOneInfo(26022, "gameType", "-1");
                cm.getPlayer().updateOneInfo(26022, "exp", "");
                cm.gainItem(itemID, quantity);
                cm.gainExp(exp);
            } else if (gameType == 3) {
                cm.sendNextNoESC("나만큼은 아니지만 너도 꽤나 훌륭한 #e#b구애의 춤#n#k을 추더군?\r\n역시 너도 나와 #e#b같은 부류#n#k의 사냥꾼이 틀림없어.");
            } else {
                var map = cm.getPlayer().getOneInfoQuestInteger(26022, "map");
                if (map == 0) {
                    map = 15;
                }
                cm.warp(map);
                cm.dispose();
            }
        } else {
            if (sel == 0) {
                var portal = cm.getPlayer().getRandomPortal();
                if (portal != null) {
	       var mapID = portal.getMapID();
	       for (var i = 0; i < 20; ++i) {
		if (cm.getPlayerCount(mapID + i) == 0) {
		    mapID = mapID + i;
		    break;
		}
	       }
                    cm.warp(mapID, "sp");
                    cm.getPlayer().removeAllSummons();
                    cm.getPlayer().setEnterRandomPortal(true);
	       cm.getPlayer().CountAdd("random_portal");
                }
                cm.dispose();
            } else if (sel == 1) {
                cm.getPlayer().removeRandomPortal();
            }
        }
    } else if (status == 3) {
        if (gameType == 0) {
            var point = cm.getPlayer().getOneInfoQuestInteger(15141, "point");
            var itemID = 0;
            var quantity = 0;
            var exp = 0;
            var avgExp = cm.getPlayer().getOneInfoQuestInteger(26022, "exp");

            if (point >= 1000) {
                itemID = 2434635;
                quantity = 2;
                exp = parseInt(avgExp) * 6000;
            } else if (point >= 750 && point < 1000) {
                itemID = 2434635;
                quantity = 1;
                exp = parseInt(avgExp) * 4000;
            } else {
                itemID = 2434634;
                quantity = 1;
                exp = parseInt(avgExp) * 2000;
            }
            cm.getPlayer().updateOneInfo(15141, "point", "");
            cm.getPlayer().updateOneInfo(26022, "gameType", "-1");
            cm.getPlayer().updateOneInfo(26022, "exp", "");
            cm.sendNextNoESC("이번 사냥에서 #e#b" + point + "#k#n점을 획득 했군?\r\n자 여기 너의 몫으로 #b#i" + itemID + "##z" + itemID + "##k와 #b경험치#k를 줄게! 다음에 또 보자고!");
            cm.gainItem(itemID, quantity);
            cm.gainExp(exp);
        } else if (gameType == 2) {
            var map = cm.getPlayer().getOneInfoQuestInteger(26022, "map");
            if (map == 0) {
                map = 15;
            }
            cm.warp(map);
            cm.dispose();
        } else if (gameType == 3) {
            var point = cm.getPlayer().getOneInfoQuestInteger(15143, "success");
            var itemID = 0;
            var quantity = 0;
            var exp = 0;
            var avgExp = cm.getPlayer().getOneInfoQuestInteger(26022, "exp");

            if (point >= 10) {
                itemID = 2434636;
                quantity = 2;
                exp = parseInt(avgExp) * 10000;
            } else if (point >= 8 && point < 10) {
                itemID = 2434636;
                quantity = 1;
                exp = parseInt(avgExp) * 6000;
            } else if (point >= 4 && point < 8) {
                itemID = 2434635;
                quantity = 2;
                exp = parseInt(avgExp) * 4000;
            } else {
                itemID = 2434635;
                quantity = 1;
                exp = parseInt(avgExp) * 3000;
            }
            cm.getPlayer().updateOneInfo(15141, "point", "");
            cm.getPlayer().updateOneInfo(26022, "gameType", "-1");
            cm.getPlayer().updateOneInfo(26022, "exp", "");

            cm.sendNextNoESC("이번 #b구애의 춤#k에서 #e#b" + point + "번#k#n의 춤동작을 훌륭하게 소화했어!\r\n자 여기 너의 몫으로 #b#i" + itemID + "##z" + itemID + "##k와 #b경험치#k를 줄게! 다음에 또 보자고!");
            cm.gainItem(itemID, quantity);
            cm.gainExp(exp);
        }
    } else if (status == 4) {
        var map = cm.getPlayer().getOneInfoQuestInteger(26022, "map");
        if (map == 0) {
            map = 15;
        }
        cm.warp(map);
        cm.dispose();
    }
}