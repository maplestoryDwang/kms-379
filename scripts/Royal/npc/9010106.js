importPackage(java.lang);

var status = -1;
var s = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        status--;
        cm.dispose();
    }
    if (mode == 1) {
        status++;
    }

    if (status == 0) {
        if (cm.inBoss()) {
            cm.getPlayer().dropMessage(5, "보스 진행중엔 이용이 불가능합니다.");
            cm.dispose();
            return;
        }

        var v = cm.getPlayer().getOneInfoQuest(18771, "rank");
        if (v != null && !v.isEmpty()) {
            s = 0;
            var v0 = "#fs11#이것 참 용잡으러 가기 좋은 날이군요!\r\n어떤 #b메이플 유니온#k 업무를 도와드릴까요?\r\n\r\n#L0##b<나의 메이플 유니온 정보를 확인한다.>#l\r\n#L1##b<메이플 유니온 등급을 올린다.>#l\r\n#L2##b<메이플 유니온에 대해 설명을 듣는다.>#k#l";
            cm.sendSimple(v0);
        } else {
            if (cm.getPlayer().canCreateMapleUnion()) {
                s = 1;
                cm.sendNext("#fs11#메이플 유니온을 만들 수 있게 되었군요. 지금 바로 만들어 보죠.");
            } else {
                cm.sendNext("#fs11#메이플 유니온은 #b#e유니온 레벨 500 이상#n#k, #r#e보유 캐릭터 3개 이상#n#k이 되면 만드실 수 있습니다. 아직 유니온을 만드실 수 없습니다.");
                cm.dispose();
            }
         }
    } else if (status == 1) {
    if (s == 1) {
        if (cm.getPlayer().firstLoadMapleUnion(true)) {
            cm.getPlayer().updateOneInfo(500629, "point", "0");
            cm.getPlayer().updateOneInfo(18771, "rank", "101");
            cm.sendNext("#fs11#메이플 유니온을 만드는데 성공했어요. 메이플 유니온은 #e#r<메뉴>#k#n에서 #e#b<메이플 유니온>#k#n을 선택하여 공격대원을 편집할 수 있습니다.");
            cm.dispose();
        } else {
            cm.sendNext("#fs11#알 수 없는 오류가 발생하여 유니온 생성에 실패했어요. 나중에 다시 시도해보세요.");
            cm.dispose();
        }
    } else if (s == 0) {
        if (selection == 0) {
            cm.sendNext("#fs11#용사님의 #e메이플 유니온#n 정보를 알려드릴까요?\r\n\r\n#e메이플 유니온 등급: #n#b#e<" + cm.getPlayer().getUnionLevelName() + ">#n#k\r\n#e유니온 레벨: #n#b#e<" + cm.getPlayer().getUnionLevel() + ">#n#k\r\n#e보유 유니온 캐릭터: #n#b#e<" + cm.getPlayer().getUnionCharacterCount() + ">#n#k\r\n#e공격대원:#n#b#e<" + cm.getPlayer().getUnionActive() + " / " + cm.getPlayer().getUnionActiveMax() + " 명>#n#k");
            cm.dispose();
        } else if (selection == 1) {
            if (cm.getPlayer().getMapleUnion().rank == 505) {
                cm.sendNext("#fs11#이미 유니온 등급이 최고 등급이십니다");
                cm.dispose();
                return;
            }
            var nextName = cm.getPlayer().getNextUnionLevelName();
            var nextMax = cm.getPlayer().getNextUnionActiveMax();
            if (nextMax != -1) {
                cm.sendYesNo("#fs11##e메이플 유니온 승급#n을 하고 싶으신가요?\r\n\r\n#e현재등급: #n#b#e<" + cm.getPlayer().getUnionLevelName() + ">#n#k\r\n#e다음등급: #n#b#e<" + nextName + ">#n#k\r\n#e승급 시 투입 가능 공격대원 증가:#n #b#e<" + cm.getPlayer().getUnionActiveMax() + "→" + nextMax + " 명>#n#k\r\n\r\n승급을 위해선 아래 조건을 충족하셔야 해요.\r\n\r\n#e<유니온 레벨> #r#e" + cm.getPlayer().getNextUnionNeedLevel() + " 이상#n#k #n\r\n#e<지불 코인> #b#e#t4310229# " + cm.getPlayer().getNextUnionNeedCoin() + "개#n#k\r\n\r\n 지금 메이플 유니온을 #e승급#n 시켜 드릴까요?");
            } else {
                cm.sendNext("#fs11#더 이상 유니온 등급을 올리실 수 없으세요.");
                cm.dispose();
            }
        } else if (selection == 2) {
            cm.dispose();
            cm.openNpc(9010106, "mapleunion_help");
        }
    }
    } else if (status == 2) {
    if (s == 0) {
        var ret = cm.getPlayer().levelUpUnion();
        if (ret == -1) {
            cm.sendNext("#fs11#알 수 없는 오류로 인하여 등급 업 처리에 실패했습니다. 나중에 다시 시도해주세요.");
            cm.dispose();
        } else if (ret == 0) {

            cm.addCustomLog(4, "[유니온] 승급등급 : " + cm.getPlayer().getUnionLevelName() + "(" + cm.getPlayer().getMapleUnion().rank + ") - " + cm.getPlayer().getUnionLevel() + "");
            cm.getPlayer().setSaveFlag(cm.getPlayer().getSaveFlag() | 1048576 | 2097152 | 4194304); // Maple_union_data, Maple_Union_Group, Maple_Union_Raiders
            cm.getPlayer().saveToDB(false, false);
            cm.sendNext("#fs11#짝짝짝!\r\n#e메이플 유니온 등급#n이 올랐어요! 이제 더 많은 공격대원과 함께 더욱 빠르게 성장하실 수 있어요!\r\n\r\n#e신규등급: #n#b#e<" + cm.getPlayer().getUnionLevelName() + ">#n#k\r\n#e투입 가능 공격대원:#n #b#e" + cm.getPlayer().getUnionActiveMax() + "#n#k\r\n\r\n그럼 다음 등급까지 쭉쭉~ 성장하세요!");
            cm.dispose();
        } else if (ret == 1) {
            cm.sendNext("#fs11#더 이상 유니온 등급을 올리실 수 없으세요.");
            cm.dispose();
        } else if (ret == 2) {
            cm.sendNext("#fs11#등급을 올리는데에 필요한 코인이 부족합니다. 소지중인 코인을 다시 확인해주세요.");
            cm.dispose();
        } else if (ret == 3) {
            cm.sendNext("#fs11#다음 등급에 필요한 유니온 레벨이 부족합니다. 유니온 레벨을 더 올려주세요.");
            cm.dispose();
        }
    }
    }
}