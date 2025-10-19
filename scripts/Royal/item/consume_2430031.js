var st = 0;
var status = -1;
importPackage(Packages.server.quest);
bossname = [
    //["스우", 13, "Normal_Lotus", "Hard_Lotus"],
    //["데미안", 15, "Normal_Demian", "Hard_Demian"],
    //["루시드", 19, "Easy_Lucid", "Normal_Lucid", "Hard_Lucid"],
    //["윌", 23, "Normal_Will", "Hard_Will"],
    ["듄켈", 27, "Normal_Dunkel", "Hard_Dunkel"],
    ["더스크", 26, "Normal_Dusk", "Chaos_Dusk"],
    ["고통의 미궁 : 진 힐라", 24, "Normal_JinHillah"],
    //["초월자 : 검은 마법사", 25, "Black_Mage"],
    //["선택받은 세렌", 28, "Hard_Seren"],
           ]
function start() {
    status = -1;
    action (1, 0, 0);
}

function action(mode, type, selection) {

    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        status --;
    }
    if (mode == 1) {
        status++;
    }

    if (status == 0) {
        말 = "#fs11##b[#h #]#fc0xFF000000# : #fc0xFF000000#어떤 #r보스#fc0xFF000000#의 #b입장 횟수#fc0xFF000000#를 초기화 할까?\r\n초기화권은 하루에 #r1번#fc0xFF000000#만 사용가능하니 신중히 골라야겠어\r\n\r\n";
        for (var a = 0; a < bossname.length; a++) {
            말 += "#L"+a+"##b#fUI/UIWindow2.img/UserList/Main/Boss/BossList/"+bossname[a][1]+"/Icon/normal/0# "+bossname[a][0]+"#fc0xFF000000#\r\n";
        }
        cm.sendSimpleS(말, 2);
    } else if (status == 1) {
        st = selection;
        cm.sendYesNoS("#r#e#fs11##fUI/UIWindow2.img/UserList/Main/Boss/BossList/"+bossname[st][1]+"/Icon/normal/0#\r\n"+bossname[st][0]+"#fs11##n#fc0xFF000000# 클리어 횟수 초기화를 진행 하시겠어요?\r\n\r\n#fs11##r※ 사용시 되돌릴 수 없습니다", 4, 9010061);
    } else if (status == 2) {
        if (cm.getPlayer().getV("ResetBoss") == null) {
	cm.getPlayer().addKV("ResetBoss", "0");
        }
        if (cm.getPlayer().getV("ResetBoss") != "0") {
	cm.sendOkS("#fs11#맞다 오늘 이미 초기화권을 사용했었지",2);
	cm.dispose();
	return;
        }

        cm.gainItem(2430031, -1);
        cm.getPlayer().removeV(bossname[st][2]);
        cm.getPlayer().addKV("ResetBoss", "1");
        // 다른 난이도 있을시 모두 초기화
        if (bossname[st][3] != null)
            cm.getPlayer().removeV(bossname[st][3]);
        if (bossname[st][4] != null)
            cm.getPlayer().removeV(bossname[st][4]);

        cm.addConsumeLog(2430032, "보스 초기화 사용 (아이템 : 2430031, 초기화 보스 : " + bossname[st][0] + ")");
        cm.sendOkS("#r#e#fs11##fUI/UIWindow2.img/UserList/Main/Boss/BossList/"+bossname[st][1]+"/Icon/normal/0#\r\n"+bossname[st][0]+"#fs11##n#fc0xFF000000# 클리어 횟수 초기화가 완료 되었습니다.", 4, 9010061);
        cm.dispose();
    }
}
