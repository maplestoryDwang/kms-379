var status = -1;

보라 = "#fMap/MapHelper.img/weather/starPlanet/7#";
파랑 = "#fMap/MapHelper.img/weather/starPlanet/8#";
별파 = "#fUI/GuildMark.img/Mark/Pattern/00004001/11#"
별노 = "#fUI/GuildMark.img/Mark/Pattern/00004001/3#"
별흰 = "#fUI/GuildMark.img/Mark/Pattern/00004001/15#"
별갈 = "#fUI/GuildMark.img/Mark/Pattern/00004001/5#"
별빨 = "#fUI/GuildMark.img/Mark/Pattern/00004001/1#"
별검 = "#fUI/GuildMark.img/Mark/Pattern/00004001/16#"
별보 = "#fUI/GuildMark.img/Mark/Pattern/00004001/13#"
별 = "#fUI/FarmUI.img/objectStatus/star/whole#"
S = "#fUI/CashShop.img/CSEffect/today/0#"
보상 = "#fUI/UIWindow2.img/Quest/quest_info/summary_icon/reward#"
획득 = "#fUI/UIWindow2.img/QuestIcon/4/0#"
색 = "#fc0xFF6600CC#"
검은색 = "#fc0xFF000000#"
엔터 = "\r\n"
엔터2 = "\r\n\r\n"
크기 = "#fs11#"

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        NullKeyValue();
        var chat = "#fs11#";
        chat += "#fc0xFF000000#로얄메이플 #e시즌 패스 시스템#n입니다.\r\n";
        chat += "#fc0xFF000000#현재 #fc0xFFFF3366##h ##fc0xFF000000# 님의 시즌패스 등급 : #fc0xFFFF3366#"+cm.getPlayer().getKeyValue(0, "Tear_Upgrade")+"#fc0xFF000000##b\r\n"
        if (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") == 10) {
            chat += "#r#L3#현재 티어가 최대 등급이므로 승급이 불가합니다.#k\r\n";
        } else {
            chat += "#L1#" + 색 + 보라 + " 다음 시즌패스 등급으로 승급\r\n";
	chat += "#L2#" + 색 + 파랑 + " 시즌패스 에 대해서\r\n";
        }
        cm.sendSimple(chat);

    } else if (status == 1) {
        if (selection == 1) {
            if (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") == 0) { // 1단계 시즌패스 
                var chat = "#fs11#" + 색 + (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") + 1) + "단계로 승급하기 위해서는 아래와 같은 재료가 필요합니다.\r\n\r\n";
                chat += "#i4001753# #b#z4001753##k #r10개#k\r\n\r\n";
                chat += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n#fc0xFFFF3300#";
                chat += "#b<1단계 시즌패스 보상>#l\r\n";
                chat +=  "#i4310237##z4310237# #r500개\r\n\r\n";
                chat += "#L0##r#e1 단계로 승급하시겠어요 ?#k#n";
                cm.sendSimple(chat);
            }

            if (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") == 1) { // 2단계 시즌패스
                var chat = "#fs11#" + 색 + (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") + 1) + "단계로 승급하기 위해서는 아래와 같은 재료가 필요합니다.\r\n\r\n";
                chat += "#i4001753# #b#z4001753##k #r30개#k\r\n";
                chat += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n#fc0xFFFF3300#";
                chat += "#b<2단계 시즌패스 보상>#l\r\n";
                chat +=  "#i4009005##z4009005# #r50개\r\n\r\n";
                chat += "#L1##r#e2 단계로 승급하시겠어요 ?#k#n";
                cm.sendSimple(chat);
            }

            if (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") == 2) { // 3단계 시즌패스
                var chat = "#fs11#" + 색 + (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") + 1) + "단계로 승급하기 위해서는 아래와 같은 재료가 필요합니다.\r\n\r\n";
                chat += "#i4001753# #b#z4001753##k #r50개#k\r\n";
                chat += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n#fc0xFFFF3300#";
                chat += "#b<3단계 시즌패스 보상>#l\r\n";
                chat +=  "#i5060048##z5060048# #r5개\r\n\r\n";
                chat += "#L2##r#e3 단계로 승급하시겠어요 ?#k#n";
                cm.sendSimple(chat);
            }

            if (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") == 3) { // 4단계 시즌패스
                var chat = "#fs11#" + 색 + (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") + 1) + "단계로 승급하기 위해서는 아래와 같은 재료가 필요합니다.\r\n\r\n";
                chat += "#i4001753# #b#z4001753##k #r70개#k\r\n";
                chat += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n#fc0xFFFF3300#";
                chat += "#b<4단계 시즌패스 보상>#l\r\n";
                chat +=  "#i5068305##z5068305# #r2개\r\n\r\n";
                chat += "#L3##r#e4 단계로 승급하시겠어요 ?#k#n";
                cm.sendSimple(chat);
            }

            if (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") == 4) { // 5단계
                var chat = "#fs11#" + 색 + (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") + 1) + "단계로 승급하기 위해서는 아래와 같은 재료가 필요합니다.\r\n\r\n";
                chat += "#i4001753# #b#z4001753##k #r90개#k\r\n";
                chat += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n#fc0xFFFF3300#";
                chat += "#b<5단계 시즌패스 보상>#l\r\n";
                chat +=  "#i2049376##z2049376# #r1개\r\n\r\n";
                chat += "#L4##r#e5 단계로 승급하시겠어요 ?#k#n";
                cm.sendSimple(chat);
            }

            if (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") == 5) { // 6단계
                var chat = "#fs11#" + 색 + (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") + 1) + "단계로 승급하기 위해서는 아래와 같은 재료가 필요합니다.\r\n\r\n";
                chat += "#i4001753# #b#z4001753##k #r110개#k\r\n";
                chat += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n#fc0xFFFF3300#";
                chat += "#b<6단계 시즌패스 보상>#l\r\n";
                chat +=  "#i4310308##z4310308# #r200개\r\n\r\n";
                chat += "#L5##r#e6 단계로 승급하시겠어요 ?#k#n";
                cm.sendSimple(chat);
            }

            if (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") == 6) { // 7단계
                var chat = "#fs11#" + 색 + (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") + 1) + "단계로 승급하기 위해서는 아래와 같은 재료가 필요합니다.\r\n\r\n";
                chat += "#i4001753# #b#z4001753##k #r130개#k\r\n";
                chat += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n#fc0xFFFF3300#";
                chat += "#b<7단계 시즌패스 보상>#l\r\n";
                chat +=  "#i4001715##z4001715# #r100개\r\n\r\n";
                chat += "#L6##r#e7 단계로 승급하시겠어요 ?#k#n";
                cm.sendSimple(chat);
            }

            if (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") == 7) { // 8단계
                var chat = "#fs11#" + 색 + (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") + 1) + "단계로 승급하기 위해서는 아래와 같은 재료가 필요합니다.\r\n\r\n";
                chat += "#i4001753# #b#z4001753##k #r150개#k\r\n";
                chat += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n#fc0xFFFF3300#";
                chat += "#b<8단계 시즌패스 보상>#l\r\n";
                chat +=  "#i2430043##z2430043# #r1개\r\n\r\n";
                chat += "#L7##r#e8 단계로 승급하시겠어요 ?#k#n";
                cm.sendSimple(chat);
            }

            if (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") == 8) { // 9단계
                var chat = "#fs11#" + 색 + (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") + 1) + "단계로 승급하기 위해서는 아래와 같은 재료가 필요합니다.\r\n\r\n";
                chat += "#i4001753# #b#z4001753##k #r170개#k\r\n";
                chat += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n#fc0xFFFF3300#";
                chat += "#b<9단계 시즌패스 보상>#l\r\n";
                chat +=  "#i4310266##z4310266# #r700개\r\n\r\n";
                chat += "#L8##r#e9 단계로 승급하시겠어요 ?#k#n";
                cm.sendSimple(chat);
            }
            if (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") == 9) { // 10단계
                var chat = "#fs11#" + 색 + (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") + 1) + "단계로 승급하기 위해서는 아래와 같은 재료가 필요합니다.\r\n\r\n";
                chat += "#i4001753# #b#z4001753##k #r200개#k\r\n";
                chat += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n#fc0xFFFF3300#";
                chat += "#b<10단계 시즌패스 보상>#l\r\n";
                chat +=  "#i4310266##z4310266# #r1000개\r\n\r\n";
                chat += "#L9##r#e10 단계로 승급하시겠어요 ?#k#n";
                cm.sendSimple(chat);
            }
	} else if (selection == 2) {
		var chat = "#fs11##e" + 색 + "< 시즌패스 안내 >\r\n\r\n";
                chat += "일일 퀘스트를 통해#k#n\r\n\r\n#i4001753# #b#z4001753##k" + 검은색 + " 아이템을 획득 할 수 있습니다#k\r\n\r\n";
		chat += 검은색 + "획득한 티켓으로 시즌패스 등급을 올릴수 있으며\r\n등급을 올릴때 티켓을 소모하지않습니다\r\n\r\n";
		chat += 검은색 + "승급 할때마다 보상을 획득 가능합니다\r\n프리미엄 패스는 더 좋은 보상을 획득 할 수 있습니다\r\n\r\n";

                chat += "#b<1단계 시즌패스 보상>#l\r\n";
                chat +=  "#i4310237##z4310237# #r500개\r\n\r\n";
                chat += "#b<2단계 시즌패스 보상>#l\r\n";
                chat +=  "#i4009005##z4009005# #r50개\r\n\r\n";
                chat += "#b<3단계 시즌패스 보상>#l\r\n";
                chat +=  "#i5060048##z5060048# #r5개\r\n\r\n";
                chat += "#b<4단계 시즌패스 보상>#l\r\n";
                chat +=  "#i5068305##z5068305# #r2개\r\n\r\n";
                chat += "#b<5단계 시즌패스 보상>#l\r\n";
                chat +=  "#i2049376##z2049376# #r1개\r\n\r\n";
                chat += "#b<6단계 시즌패스 보상>#l\r\n";
                chat +=  "#i4310308##z4310308# #r200개\r\n\r\n";
                chat += "#b<7단계 시즌패스 보상>#l\r\n";
                chat +=  "#i4001715##z4001715# #r100개\r\n\r\n";
                chat += "#b<8단계 시즌패스 보상>#l\r\n";
                chat +=  "#i2430043##z2430043# #r1개\r\n\r\n";
                chat += "#b<9단계 시즌패스 보상>#l\r\n";
                chat +=  "#i4310266##z4310266# #r700개\r\n\r\n";
                chat += "#b<10단계 시즌패스 보상>#l\r\n";
                chat +=  "#i4310266##z4310266# #r1000개\r\n\r\n";
                cm.sendOk(chat);
		cm.dispose();
		return;
        } else if (selection == 3) {
            cm.dispose();
            return;
        }

    } else if (status == 2) {
        if (selection == 0) {
            if (cm.haveItem(4001753, 10)) {
                cm.gainItem(4310237, 500);
                cm.getPlayer().setKeyValue(0, "Tear_Upgrade", "1");
                cm.sendOk("1 단계로 승급 하셨습니다 축하드립니다.");
                cm.dispose();
            } else {
                cm.sendOk("재료가 부족합니다.");
                cm.dispose();
            }
        }

        if (selection == 1) {
            if (cm.haveItem(4001753, 30)) {
                cm.gainItem(4009005, 50);
                cm.getPlayer().setKeyValue(0, "Tear_Upgrade", "2");
                 cm.sendOk("2 단계로 승급 하셨습니다 축하드립니다.");
                cm.dispose();
            } else {
                cm.sendOk("재료가 부족합니다");
                cm.dispose();
            }
        }

        if (selection == 2) {
            if (cm.haveItem(4001753, 50)) {
                cm.gainItem(5060048, 5);
                cm.getPlayer().setKeyValue(0, "Tear_Upgrade", "3");
                //Packages.handling.world.World.Broadcast.broadcastMessage(Packages.network.models.CField.getGameMessage(8, cm.getPlayer().getName() + "님이 골드 티어로 승급 하였습니다."));
                cm.sendOk("3 단계로 승급 하셨습니다 축하드립니다.");
                cm.dispose();
            } else {
                cm.sendOk("재료가 부족합니다");
                cm.dispose();
            }
        }

        if (selection == 3) {
            if (cm.haveItem(4001753, 70)) {
                cm.gainItem(5068305, 2);
                cm.getPlayer().setKeyValue(0, "Tear_Upgrade", "4");
                //Packages.handling.world.World.Broadcast.broadcastMessage(Packages.network.models.CField.getGameMessage(8, cm.getPlayer().getName() + "님이 플래티넘 티어로 승급 하였습니다."));
                cm.sendOk("4 단계로 승급 하셨습니다 축하드립니다.");
                cm.dispose();
            } else {
                cm.sendOk("재료가 부족합니다");
                cm.dispose();
            }
        }

        if (selection == 4) {
            if (cm.haveItem(4001753, 90)) {
                cm.gainItem(2049376, 1);
                cm.getPlayer().setKeyValue(0, "Tear_Upgrade", "5");
                //Packages.handling.world.World.Broadcast.broadcastMessage(Packages.network.models.CField.getGameMessage(8, cm.getPlayer().getName() + "님이 다이아몬드 티어로 승급 하였습니다."));
                cm.sendOk("5 단계로 승급 하셨습니다 축하드립니다.");
                cm.dispose();
            } else {
                cm.sendOk("재료가 부족합니다");
                cm.dispose();
            }
        }

        if (selection == 5) {
            if (cm.haveItem(4001753, 110)) {
                cm.gainItem(4310308, 200);
                cm.getPlayer().setKeyValue(0, "Tear_Upgrade", "6");
                //Packages.handling.world.World.Broadcast.broadcastMessage(Packages.network.models.CField.getGameMessage(8, cm.getPlayer().getName() + "님이 마스터 티어로 승급 하였습니다."));
                cm.sendOk("6 단계로 승급 하셨습니다 축하드립니다.");
                cm.dispose();
            } else {
                cm.sendOk("재료가 부족합니다");
                cm.dispose();
            }
        }

        if (selection == 6) {
            if (cm.haveItem(4001753, 130)) {
                cm.gainItem(4001715, 100);
                cm.getPlayer().setKeyValue(0, "Tear_Upgrade", "7");
                //Packages.handling.world.World.Broadcast.broadcastMessage(Packages.network.models.CField.getGameMessage(8, cm.getPlayer().getName() + "님이 그랜드마스터 티어로 승급 하였습니다."));
                cm.sendOk("7 단계로 승급 하셨습니다 축하드립니다.");
                cm.dispose();
            } else {
                cm.sendOk("재료가 부족합니다");
                cm.dispose();
            }
        }

        if (selection == 7) {
            if (cm.haveItem(4001753, 150)) {
                cm.gainItem(2430043, 1);
                cm.getPlayer().setKeyValue(0, "Tear_Upgrade", "8");
                //Packages.handling.world.World.Broadcast.broadcastMessage(Packages.network.models.CField.getGameMessage(8, cm.getPlayer().getName() + "님이 레전드 티어로 승급 하였습니다."));
                cm.sendOk("8 단계로 승급 하셨습니다 축하드립니다.");
                cm.dispose();
            } else {
                cm.sendOk("재료가 부족합니다");
                cm.dispose();
            }
        }
        if (selection == 8) {
            if (cm.haveItem(4001753, 170)) {
                cm.gainItem(4310266, 700);
                cm.getPlayer().setKeyValue(0, "Tear_Upgrade", "9");
                //Packages.handling.world.World.Broadcast.broadcastMessage(Packages.network.models.CField.getGameMessage(8, cm.getPlayer().getName() + "님이 레전드 티어로 승급 하였습니다."));
                cm.sendOk("9 단계로 승급 하셨습니다 축하드립니다.");
                cm.dispose();
            } else {
                cm.sendOk("재료가 부족합니다");
                cm.dispose();
            }
        }

        if (selection == 9) {
            if (cm.haveItem(4001753, 200)) {
                cm.gainItem(4310266, 1000);
                cm.getPlayer().setKeyValue(0, "Tear_Upgrade", "10");
                //Packages.handling.world.World.Broadcast.broadcastMessage(Packages.network.models.CField.getGameMessage(8, cm.getPlayer().getName() + "님이 레전드 티어로 승급 하였습니다."));
                cm.sendOk("10 단계로 승급 하셨습니다 축하드립니다.");
                cm.dispose();
            } else {
                cm.sendOk("재료가 부족합니다");
                cm.dispose();
            }
        }

        if (selection == 11) {
            if (cm.haveItem(4001753, 200)) {
                cm.gainItem(4310266, 1000);
                cm.getPlayer().setKeyValue(0, "Tear_Upgrade", "10");
                //Packages.handling.world.World.Broadcast.broadcastMessage(Packages.network.models.CField.getGameMessage(8, cm.getPlayer().getName() + "님이 챌린저 티어로 승급 하였습니다."));
                cm.sendOk("10 단계로 승급 하셨습니다 축하드립니다.");
                cm.dispose();
            } else {
                cm.sendOk("재료가 부족합니다");
                cm.dispose();
            }
        }
    }
}

function NullKeyValue() {
    if (cm.getPlayer().getKeyValue(0, "Tear_Upgrade") == -1) {
        cm.getPlayer().setKeyValue(0, "Tear_Upgrade", "0");
    }
}