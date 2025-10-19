// 이벤트NPC 카스토르
별보 = "#fMap/MapHelper.img/weather/starPlanet/7#";
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
핑크색 = "#fc0xFFFF3366#"
분홍색 = "#fc0xFFF781D8#"
하늘색 = "#fc0xFF58D4D3#"
enter = "\r\n"
엔터 = "\r\n"
엔터2 = "\r\n\r\n"

var seld = -1;
var status = -1;

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

    // GM만 가능하게 임시로 막기
    /*
    if (cm.getPlayer().getGMLevel() < 5) {
    cm.dispose();
    cm.sendOkS("현재 이용하실수 없습니다", 0x24);
    }
    */
    if (status == 0) {
        var msg = "#fs11##fUI/Basic.img/Zenia/SC/1#\r\n";
        msg += "#Cgray#――――――――――――――――――――――――――――――――――――――――\r\n";

        msg += "#L110#" + 별파 + 하늘색 + " [EVENT]#b [" + 색 + "오픈기념 이벤트 주화 상점#b]" + 핑크색 + " 바로가기#l\r\n";
        msg += "#L10#" + 별파 + 하늘색 + " [EVENT]#b [" + 색 + "강림월드 사냥 페스티벌#b]" + 핑크색 + " 바로가기#l\r\n";
        //msg += "#L100#" + 별보 + 하늘색 + "[EVENT]#b [" + 색 + "미니게임#b] " + 검은색 + "진행하기#l\r\n";

        msg += "\r\n#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――";

        //msg += "#L16#" + 별보 + "#r[상시]#b [" + 색 + "강림쿠폰#b] " + 검은색 + "이벤트 코드 입력하기\r\n";
        
        msg += "#L14#" + 별보 + "#r [EVENT]#b [" + 색 + "매일접속보상#b] " + 검은색 + "지급받기#l\r\n";
        msg += "#L11#" + 별보 + "#r [EVENT]#b [" + 색 + "유니온 8000#b] " + 검은색 + "보상 받기 (" + checkunion() + ")#l\r\n";

        //msg += "#L12#" + 별보 + "#r[상시]#b [" + 색 + "수박화채#b] " + 검은색 + "만들기#l　";
        //msg += "#L13#" + 별보 + "#r[상시]#b [" + 색 + "돌림판#b] " + 검은색 + "이용하기#l\r\n";

        //msg += "#L15#" + 별보 + "#r[상시]#b [" + 색 + "우편함#b] " + 검은색 + "확인하기#l\r\n";
        cm.sendSimple(msg);

    } else if (status == 1) {
        seld = sel;
        switch (seld) {
            /* 
        case 122:
              cm.dispose();
             cm.getClient().setKeyValue("PCount", "1");
                  cm.getClient().removeKeyValue("unionevent");
        cm.getPlayer().dropMessage(6, "초기화 했다~");
            return;
*/
            case 10:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 9000213, "강림사냥페스티벌");
                return;

            case 11:
                var v = cm.getPlayer().getOneInfoQuest(18771, "rank");
                if (v == null | v.isEmpty()) {
                    cm.sendOk("#fs11#현재 유니온 정보가 없으십니다!\r\n\r\n먼저 유니온 시스템을 이용하여 유니온을 생성해주세요\r\n#b[컨텐츠시스템 - 유니온] #rOR #b[@유니온] #k명령어 사용");
                    return;
                }

                if (cm.getClient().getKeyValue("unionevent") == null) {
                    if (cm.getInvSlots(1) < 5 || cm.getInvSlots(2) < 5 || cm.getInvSlots(3) < 5 || cm.getInvSlots(4) < 5 || cm.getInvSlots(5) < 5) {
                        cm.sendOkS("#fs11##fc0xFF6600CC#인벤토리를 탭별로 5칸이상 비워주세요", 2);
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getUnionLevel() >= 8000) {
                        var msg = "#fs11#";
                        msg += "유니온 8천 달성 축하드립니다." + 엔터;
                        msg += "작지만 성장에 도움이 되는 아이템을 지급 하였습니다." + 엔터;
                        msg += "#i4310229##z4310229# 2000개" + 엔터;
                        msg += "#i4310266##z4310266# 1000개" + 엔터;
                        msg += "#i4310237##z4310237# 2000개" + 엔터;
                        msg += "#i4001715##z4001715# 200개" + 엔터;
                        msg += "#i5060048##z5060048# 5개" + 엔터;
                        msg += "#i2630009##z2630009# 3개" + 엔터;
                        cm.sendOk(msg);
                        cm.gainItem(4310229, 2000);
                        cm.gainItem(4310266, 1000);
                        cm.gainItem(4310237, 2000);
                        cm.gainItem(4001715, 100);
                        cm.gainItem(5060048, 5);
                        cm.gainItem(2630009, 1);
                        cm.getClient().setKeyValue("unionevent", "1");
                        cm.dispose();
                    } else {
                        cm.sendOk("#fs11#유니온 레벨이 모자라는거 같은데요?\r\n현재 유니온 레벨 :#r" + cm.getPlayer().getUnionLevel());
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("#fs11#이미 유니온 이벤트에 참여하셨습니다.");
                    cm.dispose();
                }
                return;

            case 12:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 9000213, "수박");
                return;

            case 13:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 9000213, "뽑기");
                return;

            case 14:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 9000213, "핫타임일퀘");
                return;

            case 15:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 9000213, "우편함");
                return;

            case 16:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 9000213, "쿠폰");
                return;



            // 미니게임
            case 100:
                var msg = "#fs11#    #fUI/Basic.img/Zenia/SC/1#\r\n";
                msg += "#Cgray#――――――――――――――――――――――――――――――――――――――――\r\n";
                msg += "                  #L10#" + 핑크색 + 파랑 + "[에르다 스펙트럼]" + 색 + " 진행" + 검은색 + " 하기#l" + enter;
                msg += "                    #L11#" + 핑크색 + 파랑 + "[배고픈 무토]" + 색 + " 진행" + 검은색 + " 하기#l" + enter;
                msg += "                   #L12#" + 핑크색 + 파랑 + "[드림 브레이커]" + 색 + " 진행" + 검은색 + " 하기#l" + enter;
                //msg += "                   #L13#" + 핑크색 + 파랑 + "[스피릿 세이버]" + 색 + " 진행" + 검은색 + " 하기#l" + enter;

                msg += enter;

                msg += "         #L1#" + 색 + 파랑 + "#r[솔로]" + 색 + " 게임" + 검은색 + " 하기#l";
                msg += "#L2#" + 색 + 파랑 + "#r[멀티]" + 색 + " 게임" + 검은색 + " 하기#l" + enter;

                msg += enter;

                msg += "                        #L3#" + 색 + 파랑 + "#b[고전]" + 색 + " 게임" + 검은색 + " 하기#l";
                msg += "\r\n\r\n#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――";
                cm.sendSimple(msg);
                return;

            case 123:
                if (cm.haveItem(3010432, 1)) {
                    cm.sendOk("#fs11##d이미 가지고 있는거 같은데?");
                    cm.dispose();
                    return;
                }
                cm.gainItem(3010432, 1);
                cm.sendOk("#fs11##i3010432##b#z3010432# #d 아이템이 지급되었습니다.");
                cm.dispose();
                return;

            case 110:
                cm.dispose();
                cm.openShop(3334);
                break;

        }
    } else if (status == 2) {
        switch (seld) {
            // 미니게임
            case 100:
                switch (sel) {
                    case 1:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 9000213, "minigamesolo");
                        return;
                    case 2:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 9000213, "minigamemulti");
                        return;
                    case 3:
                        cm.dispose();
                        cm.openShop(11111);
                        return;
                    case 10:
                        cm.dispose();
                        Packages.scripting.newscripting.ScriptManager.runScript(cm.getPlayer().getClient(), "arcane1MO_Enter", null);
                        return;
                    case 11:
                        cm.dispose();
                        cm.getPlayer().warp(450002023);
                        return;
                    case 12:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 9010100, "dreamBreaker_NPC");
                        return;
                    case 13:
                        cm.dispose();
                        Packages.scripting.newscripting.ScriptManager.runScript(cm.getPlayer().getClient(), "spiritSavior_NPC", null);
                        return;
                }
        }
    }
}

function checkunion() {
    if (cm.getClient().getKeyValue("unionevent") == null && cm.getPlayer().getUnionLevel() >= 8000) {
        return "#b수령가능";
    } else if (cm.getClient().getKeyValue("unionevent") == null && cm.getPlayer().getUnionLevel() < 8000) {
        return "#r레벨부족";
    } else if (cm.getClient().getKeyValue("unionevent") != null) {
        return "#r수령완료";
    }
}