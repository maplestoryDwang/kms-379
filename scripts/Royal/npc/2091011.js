importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);
importPackage(java.text);
importPackage(java.awt);
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
    if (mode == 0 && type == 3 && sel == 0) {
    cm.sendNext("변덕이 죽 끓는듯 하군.\r\n이곳은 만만한 곳이 아니야. 잘 생각하고 입장하라고!");
    cm.dispose();
    return;
    }
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;

        if (status == 0) {
    if (cm.getPlayer().getMapId() == 925020002 || cm.getPlayer().getMapId() == 925020003) {
        cm.displayDojangResult();
        cm.dispose();
        return;
    }
    if (!cm.canEnterDojang()) {
        cm.sendNext("#fs11##e#b토요일 23시 55분#k#n부터 #e#b일요일 00시 04분#k#n까지 #b랭킹 집계#k를 위해 도전을 제한하고 있어. #b#e00시 05#n#k분 이후에 다시 도전해줘.");
        cm.dispose();
        return;
    }
    var v0 = "#fs11#우리 사부님은 무릉에서 최고로 강한 분이지. 그런 분에게 네가 도전하겠다고? 나중에 후회하지마.\r\n#b";
    v0 += "#L0#무릉도장에 도전해볼게.#l\r\n";
    v0 += "#L6#무릉도장(챌린지)에 도전해볼게.#l\r\n";
    v0 += "#L1#무릉도장이 뭐지?#l\r\n";
    v0 += "#L3#오늘 남은 도전 횟수를 확인하고 싶어.#l\r\n\r\n";
    //v0 += "#L4#무릉도장 정산 포인트를 받고 싶어.#l\r\n";
    v0 += "#L5#무릉도장 랭커 보상을 받고 싶어.#l\r\n";
    v0 += "#L2#무릉도장 랭커 보상을 확인하고 싶어.#l\r\n\r\n";
    v0 += "#L7##r안개 숲 수련장을 이용하고 싶어.#l"

    cm.sendSimple(v0);
        } else if (status == 1) {
    sel = selection;
    if (sel == 0) {

        cm.sendYesNo("#fs11#무릉도장에 입장 시 지금 적용되어 있는\r\n#fs16##b#e모든 버프 효과가 해제#k#fs11##n될거야.\r\n\r\n#fs16#그리고 #b챌린지#k 기록이 있다면 사라지게 돼.#fs11#\r\n\r\n그래도 진짜로 도전하겠어?");
    } else if (sel == 1) {
        cm.sendNext("#fs11#우리 사부님은 무릉에서 가장 강한 분이야.\r\n그런 사부님께서 만드신 곳이 바로 이 #b무릉도장#k이지.");
    } else if (sel == 2) {
        //cm.sendSimple("#fs11#무릉도장에서 얻을 수 있는 보상은 두 가지야.\r\n각 분야의 #r상위 랭커#k가 되어 보상을 얻거나\r\n꾸준히 무릉도장에 참여하여 얻는 #r포인트#k를 통한 물물교환.\r\n#b\r\n#L0#랭커 보상에 대해 묻는다.#l\r\n#L1#참여 보상(포인트)에 대해 묻는다.#l");
        cm.sendSimple("#fs11#각 분야의 #r상위 랭커#k가 되어 보상을 얻을수 있어\r\n#b\r\n#L0#랭커 보상에 대해 묻는다.#l");
    } else if (sel == 3) {
        cm.sendNext("#fs11#오늘 무릉도장에는 " + (3 - cm.GetCount("dojang_count")) + "번 참여할 수 있어. 그런 건 알아서 세어보라고.");
        cm.dispose();
    } else if (sel == 4) {
        cm.tryGetDojangPoint();
    } else if (sel == 5) {
        cm.tryGetDojangRankerReward();
    } else if (sel == 6) {
        // 입장 자격이 있는지 체크
        if (cm.getPlayer().getOneInfoQuestInteger(1234590, "open_challenge") <= 0) {
            cm.sendNext("#fs11#너는 챌린지모드를 도전할 자격이 없어보이는데?\r\n#b통달#k모드에서 #b70층#k 이상을 기록해야 자격이 주어진다구.\r\n\r\n이미 70층이 넘는 사람은 다시 도전해야 자격이 주어진다는걸 참고하길 바래.");
            cm.dispose();
            return;
        }
        cm.sendYesNo("#fs11#무릉도장(챌린지)에 입장 시 지금 적용되어 있는\r\n#fs16##b#e모든 버프 효과가 해제#k#fs11##n될거야.\r\n\r\n#fs16#그리고 #b통달#k 기록이 사라지게 돼.#fs11#\r\n\r\n그래도 진짜로 도전하겠어?");
    } else if (sel == 7) {
        Packages.scripting.newscripting.ScriptManager.runScript(cm.getPlayer().getClient(), "mulung_forest", null);
        cm.dispose();
        return;
    }
        } else if (status == 2) {
    if (sel == 0) {
        if (cm.getPlayer().getParty() != null) {
            cm.sendNext("#fs11#파티로는 입장할 수 없어! 혼자서 도전하라구! 겁쟁이냐?");
            cm.dispose();
            return;
        }

        var check = true;
        for (var i = 0; i <= 80; ++i) {
            var id = 925070000 + (i * 100);
            if (cm.getPlayerCount(id) > 0) {
                check = false;
            }
        }
        if (!check) {
            cm.sendNext("#fs11#이미 다른 모험가가 도전중이야. 다른 채널을 이용해줄래?");
            cm.dispose();
            return;
        }
        for (var i = 0; i <= 80; ++i) {
            var id = 925070000 + (i * 100);
            cm.resetMap(id);
        }
        if (cm.GetCount("dojang_count") >= 3) {
            cm.sendNext("#fs11#넌 오늘 무릉도장 도전 횟수를 3회 모두 채웠잖아? 오늘은 더 이상 도전할 수 없어. 그만 돌아가.");
            cm.dispose();
            return;
        }
        duration = 0;
        if(cm.getPlayer().getCooldownLimit(80002282) != 0){ // 봉인된 룬의 힘 해제 악용 방지
            duration = cm.getPlayer().getRemainCooltime(80002282);
        }
        cm.getPlayer().cancelAllBuffs();
        if(duration != 0){
            cm.temporaryStatSet(80002282, duration, "RuneBlocked", 1);
        }
        cm.getPlayer().removeAllSummons();
        cm.CountAdd("dojang_count");
        cm.getPlayer().setDojangChallengeMode(0);
        cm.warp(925070000);

        cm.dispose();
    } else if (sel == 6) {
        if (cm.getPlayer().getParty() != null) {
            cm.sendNext("#fs11#파티로는 입장할 수 없어! 혼자서 도전하라구! 겁쟁이냐?");
            cm.dispose();
            return;
        }

        var check = true;
        for (var i = 0; i <= 80; ++i) {
            var id = 925070000 + (i * 100);
            if (cm.getPlayerCount(id) > 0) {
                check = false;
            }
        }
        if (!check) {
            cm.sendNext("#fs11#이미 다른 모험가가 도전중이야. 다른 채널을 이용해줄래?");
            cm.dispose();
            return;
        }
        for (var i = 0; i <= 80; ++i) {
            var id = 925070000 + (i * 100);
            cm.resetMap(id);
        }
        if (cm.GetCount("dojang_count_c") >= 3) {
            cm.sendNext("#fs11#넌 오늘 무릉도장(챌린지) 도전 횟수를 3회 모두 채웠잖아? 오늘은 더 이상 도전할 수 없어. 그만 돌아가.");
            cm.dispose();
            return;
        }
        duration = 0;
        if(cm.getPlayer().getCooldownLimit(80002282) != 0){ // 봉인된 룬의 힘 해제 악용 방지
            duration = cm.getPlayer().getRemainCooltime(80002282);
        }
        cm.getPlayer().cancelAllBuffs();
        if(duration != 0){
            cm.temporaryStatSet(80002282, duration, "RuneBlocked", 1);
        }
        cm.getPlayer().removeAllSummons();
        cm.CountAdd("dojang_count_c");
        cm.getPlayer().setDojangChallengeMode(1);
        cm.warp(925070000);
        cm.dispose();
    } else if (sel == 1) {
        cm.sendNext("#fs11#무릉도장은 79층에 사부님의 별층까지 #e#b총 80층#k#n의 건물이야.\r\n강한 자일 수록 더 높이 올라갈 수 있지.\r\n물론 너의 실력으로는 끝까지 가기 힘들겠지만.");
    } else if (sel == 2) {
        sel2 = selection;
        if (sel2 == 0) {
            cm.sendNext("#fs11#사부님께서는 매주 #b상위 랭커#k에게 보상품을 하사하시지.\r\n강함이야말로 우리 무릉도장의 최고 가치니 그에 대한 보상은 당연한거 아니겠어?");
        } else if (sel2 == 1) {
            cm.sendNext("#fs11#너의 무릉도장 참여도에 따라 포인트가 지급될 거야.\r\n아래의 두 가지 기준으로 포인트를 지급해주고 있지.\r\n\r\n- 도전할 때마다 #b돌파하는 층수에 비례#k한 포인트 지급\r\n- 자신이 속한 랭킹 구간의 #b지난주 전체 랭킹 백분율#k에 따른 포인트 지급");
        }
    }
        } else if (status == 3) {
    if (sel == 2) {
        if (sel2 == 0) {
            cm.sendNext("#fs11#좀 더 공정한 경쟁을 위해 레벨에 따라 랭킹 구간이 달라.\r\n네가 어느 구간에 속해있는지 잘 보라고.\r\n\r\n#e- 입문#k : 105~200 레벨\r\n- #b통달#k : 201 레벨 이상\r\n- #r챌린지#k : 통달 70층 이상");
        } else if (sel2 == 1) {
            cm.sendNext("#fs11#층수에 비례한 포인트는 1층당 10 포인트가 기본적으로 지급되고, 10층당 100 포인트가 추가 지급되는 방식이야.");
        }
    } else if (sel == 1) {
        cm.sendNext("#fs11#사부님이 계신 80층을 제외한 각 층엔 #r메이플 월드의 몬스터 들이 그 곳을 지키고 있지. 자세한 사정은 나도 몰라.\r\n사부님만이 아실 뿐.");
    }
        } else if (status == 4) {
    if (sel == 2) {
        if (sel2 == 0) {
            cm.sendNext("#fs11#당연한 얘기지만 랭킹 구간에 따라 보상도 달라.\r\n#b모든 보상은 지금 속해있는 랭킹 구간으로 지급#k해 줄거야.\r\n설마 네가 지나온 랭킹 구간에서 상위 랭커였다고 보상을 달라고 떼쓰진 않겠지?");
        } else if (sel2 == 1) {
            cm.sendNext("#fs11#랭킹 백분율에 따른 포인트는 더 강한 자들이 속한 랭킹 구간 일수록, 그리고 더 좋은 결과를 낼수록 많이 지급해 줄거야.");
        }
    } else if (sel == 1) {
        cm.sendNext("#fs11#입장하면 초입층에서 네가 가지고 있던 #r모든 버프 효과를 해제#k할거야. 자기 힘만 가지고 경쟁해야 공정하지 않겠어?");
    }
        } else if (status == 5) {
    if (sel == 2) {
        if (sel2 == 0) {
            cm.sendNext("#fs11##b< 통달 모드 랭커 보상 >#k\r\n통달 순위 #r 1위#k   : #b#i3700526##z3700526##k\r\n통달 순위 #r2~5위#k : #b#i3700307##z3700307##k\r\n\r\n\r\n#b< 챌린지 모드 랭커 보상 >#k\r\n챌린지 순위 #r 1위#k   : #b#i3700525##z3700525##k\r\n챌린지 순위 #r2~5위#k : #b#i3700308##z3700308##k");
            cm.dispose();
        } else if (sel2 == 1) {
            cm.sendNext("#fs11#랭킹 백분율에 따른 포인트는 각 랭킹 구간에 따라\r\n#b일정 백분율 안#k에 들어야 포인트를 지급하니\r\n포인트를 얻고 싶다면 남보다 강해지라고. 흐흐흐..\r\n\r\n#e- #b입문#k : 상위 50%\r\n- #r 통달#k : 상위 70%");
        }
    } else if (sel == 1) {
        cm.sendNext("#fs11#초입층에 머무르는 건 네 자유지만,\r\n#r타이머는 단 30초만 정지#k하니 더 좋은 기록을 세우고 싶다면\r\n빠르게 준비하고 1층으로 넘어가는 게 좋을거야.");
    }
        } else if (status == 6) {
    if (sel == 2) {
        if (sel2 == 1) {
            cm.sendNext("#fs11#아, 그리고 포인트는 최대 #b50만 포인트#k를 넘게 가지고 있을 순 없어. 재깍재깍 쓰는 습관을 가지도록 해.");
            cm.dispose();
        }
    } else if (sel == 1) {
        cm.sendNext("#fs11##e1 ~ 9층#n, #e11 ~ 19층#n엔 #b하나의 보스#k가 등장해.\r\n다음으로 넘어가려면 딱 하나만 처치하면 된다고.");
    }
        } else if (status == 7) {
    if (sel == 1) {
        cm.sendNext("#fs11##e21 ~ 29층#n엔 #b보스 하나#k와 #b부하 다섯#k이 등장해.\r\n보스와 부하를 모두 처치해야 다음 층으로 넘어갈 수 있어.");
    }
        } else if (status == 8) {
    if (sel == 1) {
        cm.sendNext("#fs11##e31 ~ 39층#n엔 #b둘 이상의 보스#k를 상대해야 해.\r\n설마 벌써 지레 겁먹은 건 아니겠지? 흐흐흐...");
    }
        } else if (status == 9) {
    if (sel == 1) {
        cm.sendNext("#fs11##e41층#n부터는 다시 #b보스 하나#k만 등장하니까 너무 걱정하지 마.\r\n과연 그게 더 쉬울지는 모르겠지만 말이야. 흐흐흐흐...");
    }
        } else if (status == 10) {
    if (sel == 1) {
        cm.sendNext("#fs11#사부님이 계신 80층을 제외한 70층까지는\r\n#e10층 단위#n로 #b메이플 월드의 네임드 보스#k들이 등장해.\r\n여기선 #r15초#k마다 포션을 사용할 수 있어.");
    }
        } else if (status == 11) {
    if (sel == 1) {
        cm.sendNext("#fs11##e41층 이후#n부터도 #r15초#k마다 포션을 사용할 수 있어. 왜냐고?\r\n그야 네가 들어가보면 알게 되겠지. 흐흐흐...");
    }
        } else if (status == 12) {
    if (sel == 1) {
        cm.sendNext("#fs11#각 층에 누가 있냐고? 그런 것은 직접 올라가면서 알아봐.\r\n네가 강한만큼 많이 알게되지 않겠어? 흐흐흐...");
    }
        } else if (status == 13) {
    if (sel == 1) {
        cm.sendNext("#fs11#뭐, 하나만 알려주자면....\r\n#e74층 ~ 79층#n엔 #b사부님의 제자#k들이 지키고 있어.\r\n어쭙잖은 실력으로 만나면 고생 좀 할 거야.");
    }
        } else if (status == 14) {
    if (sel == 1) {
        cm.sendNext("#fs11#참, 무릉도장 내부는 사부님의 결계로 인해서\r\n메이플 월드에서 발휘하던 힘의 #b10분의 1#k밖에 못쓸거야.\r\n들어가서 당황하지 마.");
    }
        } else if (status == 15) {
    if (sel == 1) {
        cm.sendNext("#fs11#랭킹 \초기화는 #e#r일요일 자정#k#n이고\r\n#e#b토요일 23시 55분#k#n부터 #e#b일요일 00시 04분#k#n까지\r\n#b랭킹 집계#k를 위해 도전을 제한하고 있어\r\n\r\n알아들었으면 어서 들어가 봐.\r\n몸이 근질근질하지 않아?");
        cm.dispose();
    }
        }
    }
}