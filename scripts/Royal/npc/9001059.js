importPackage(java.lang);
importPackage(Packages.objects.utils);

var status = -1;

var damage = 0;
var killWolf = 0;

function start() {
    damage = 0;
    killWolf = 0;
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, sel) {
    if (mode == 0 && sel == -1 && type == 6) {
        if (cm.getPlayer().getMapId() == 99300600) {
            cm.getPlayer().removeRandomPortal();
        }
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
        if (cm.getPlayer().getMapId() == 993000600) {
            var gameType = cm.getPlayer().getOneInfoQuestInteger(15142, "gameType");

            if (gameType == 8) {
                if (!cm.canHold(2434639, 3)) {
                    cm.sendNext("인벤토리 공간이 있는지 확인하고 다시 말 걸어주겠어?");
                    cm.dispose();
                    return;
                }
                killWolf = cm.getPlayer().getOneInfoQuestInteger(15142, "kill_wolf");
                if (killWolf == 1) {
                    cm.sendNextNoESC("흉악한 #e#r불꽃늑대#n#k를 퇴치하다니! 대단한 실력이군.");
                } else {
                    d = cm.getPlayer().getOneInfoQuest(15142, "wolf_damage");
                    if (d == null || d == "") {
                        d = "0";
                    }
                    damage = Long.parseLong(d);
                    var text = "";
                    if (damage < 1250000000) {
                        text = "적당한";
                    } else if (damage >= 1250000000 && damage < 12500000000) {
                        text = "상당한";
                    } else if (damage >= 12500000000 && damage < 75000000000) {
                        text = "막대한";
                    } else if (damage >= 75000000000) {
                        text = "치명적인";
                    }
                    cm.sendNextNoESC("너는 불꽃늑대에게 #e#b" + text + "#n#k 데미지를 주었군.");
                }
            } else {
                cm.sendSimple("너 또한 훌륭한 사냥꾼이군 그래...수고했다.\r\n\r\n#b#L0#원래 있던곳으로 보내줘.#l");
            }
        } else {
            var gameType = cm.getPlayer().getOneInfoQuestInteger(15142, "gameType");

            /*if (gameType == 9) {
                cm.sendYesNo("설날 이벤트 소굴이 열렸다. 입장 후 힘을 합쳐 거대 월묘를 물리치면 떡국 코인을 획득할 수 있다네. 입장할텐가? 거대 월묘가 처치되면 입장할 수 없다고.");
            } else {*/
                if (gameType == 8) {
                    if (!cm.getPlayer().CountCheck("spark_wolf", 5)) {
                        cm.sendNext("오늘은 더 이상 입장할 수 없다. 다음에 다시 보도록 하지.");
                        cm.dispose();
                        return;
                    }
                } else {
                    if (!cm.getPlayer().CountCheck("random_portal", 20)) {
                        cm.sendNext("오늘은 더 이상 입장할 수 없다. 다음에 다시 보도록 하지.");
                        cm.dispose();
                        return;
                    }
                }
                cm.sendNext("나는 메이플월드 최고의 현상금 사냥꾼 #e#r폴로#n#k,\r\n동생 #e#b프리토#n#k와 함께 마물들을 퇴치하고 있다.");
            //}
        }
    } else if (status == 1) {
        if (cm.getPlayer().getMapId() == 993000600) {
            var gameType = cm.getPlayer().getOneInfoQuestInteger(15142, "gameType");
            if (gameType == 8) {
                if (killWolf) {
                    cm.sendNextNoESC("#e#r불꽃늑대#n#k는 우리 형제가 아주 오랜시간 추적하던 적이었지...\r\n물론 녀석은 다시 나타나겠지만, 너 덕분에 한동안은 여행자들을 약탈할 수 없을 거다.");
                } else {
                    var reward = 0;
                    var quantity = 0;
                    if (damage < 1250000000) {
                        reward = 2434634;
                        quantity = 1;
                    } else if (damage >= 1250000000 && damage < 12500000000) {
                        reward = 2434634;
                        quantity = 2;
                    } else if (damage >= 12500000000 && damage < 75000000000) {
                        reward = 2434635;
                        quantity = 2;
                    } else if (damage >= 75000000000) {
                        reward = 2434636;
                        quantity = 1;
                    }
                    cm.sendNextNoESC("자 #b#i" + reward + "# #z" + reward + "#" + quantity + "개#k를 선물로 주마. 그럼 다음에 또 보도록 하지!");
                    cm.gainItem(reward, quantity);
                    cm.getPlayer().updateOneInfo(15142, "wolf_damage", "0");
                    cm.getPlayer().updateOneInfo(15142, "kill_wolf", "0");
                }
            } else {
                cm.sendNextNoESC("휴... 힘든 사냥이었다.\r\n너는 보기보다 훌륭한 실력을 가지고 있더군? 고맙다.");
            }
        } else {
            var portal = cm.getPlayer().getRandomPortal();
            if (portal != null) {
                var gameType = cm.getPlayer().getOneInfoQuestInteger(15142, "gameType");
                if (portal.getType().getType() == 3) {
                    cm.sendSimple("우리 형제가 오랜 시간 추격하던 최강의 몬스터 #e#r불꽃늑대#n#k의 소굴을 마침내 찾아냈다. 녀석은 메이플월드의 여행자들을 닥치는대로 약탈하는 아주 악랄한 놈이지... 어때, 나와 함께 녀석을 퇴치하러 가겠나?\r\n\r\n#b#L0#함께 한다.#l\r\n#L1#함께하지 않는다.#l");
                /*} else if (gameType == 9) {
	      var ret = cm.checkEnterRabbit();
                    if (ret == -1) {
                        cm.getPlayer().removeRandomPortal();
                        cm.sendNext("거대 눈사람이 처치되어 입장할 수 없다네. #e28일 20시까지 00시, 04시, 08시, 12시, 16시, 20시 4시간 간격#n으로 거대 눈사람이 출현하니 참고하라고.");
                        cm.dispose();
                        return;
                    }
                    cm.getPlayer().removeAllSummons();
                    cm.warpChangeChannel(ret, 910010000);
                    cm.getPlayer().setEnterRandomPortal(true);
                    cm.dispose();*/
                } else {
                    cm.sendSimple("이제 막 사냥을 떠나는 길이었는데, 자네도 나와 함께 #b마물#k들을 퇴치하러 가겠나?\r\n\r\n#b#L0#함께 한다.#l\r\n#L1#함께하지 않는다.#l");
                }
            }
        }
    } else if (status == 2) {
        if (cm.getPlayer().getMapId() == 993000600) {
            var gameType = cm.getPlayer().getOneInfoQuestInteger(15142, "gameType");
            if (gameType == 8) {
                var reward = 0;
                var quantity = 0;
                if (killWolf) {
                    reward = 2434636;
                    quantity = 2;
                } else {

                    var map = cm.getPlayer().getOneInfoQuestInteger(26022, "map");
                    if (map == 0) {
                        map = 15;
                    }
                    cm.warp(map);
                    cm.dispose();
                    return;
                }
                cm.sendNextNoESC("자, 여기 너를 위해 작은 선물을 준비했다.\r\n#b#i" + reward + "# #z" + reward + "#" + quantity + "개#k\r\n약소하지만 성의표시라고 생각하고 받도록.");
                cm.gainItem(reward, quantity);
                cm.getPlayer().updateOneInfo(15142, "wolf_damage", "0");
                cm.getPlayer().updateOneInfo(15142, "kill_wolf", "0");
            } else {
                cm.sendNextNoESC("앞으로도 열심히 사냥하면 우리 #e#r현상금 사냥꾼 형제#n#k를 만날 수 있을 거다.");
            }
        } else {
            if (sel == 0) {
                var portal = cm.getPlayer().getRandomPortal();
                if (portal != null) {
                    if (portal.getType().getType() == 3) {
                        cm.getPlayer().CountAdd("spark_wolf");
                        var mapID = portal.getMapID();
                        //cm.warp(mapID, "sp");
                        //cm.getPlayer().changeChannel(1);
                        cm.getPlayer().removeAllSummons();
                        cm.warpChangeChannel(1, mapID);
                        cm.getPlayer().setEnterRandomPortal(true);
                    } else {
                        cm.getPlayer().CountAdd("random_portal");
                        var mapID = portal.getMapID();
                        for (var i = 0; i < 20; ++i) {
                            if (cm.getPlayerCount(mapID + i) == 0) {
                                mapID = mapID + i;
                                break;
                            }
                        }
                        cm.getPlayer().removeAllSummons();
                        cm.warp(mapID, "sp");
                        cm.getPlayer().setEnterRandomPortal(true);
                    }
                }
                cm.dispose();
            } else if (sel == 1) {
                cm.getPlayer().removeRandomPortal();
            }
        }
    } else if (status == 3) {
        var map = cm.getPlayer().getOneInfoQuestInteger(26022, "map");
        if (map == 0) {
            map = 15;
        }
        cm.warp(map);
        cm.dispose();
    }
}
