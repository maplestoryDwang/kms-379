var enter = "\r\n";
var seld = -1;

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
핑크색 ="#fc0xFFFF3366#"
분홍색 = "#fc0xFFF781D8#"
엔터 = "\r\n"
엔터2 = "\r\n\r\n"

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
        if (cm.inBoss()) {
            cm.getPlayer().dropMessage(5, "보스 진행중엔 이용이 불가능합니다.");
            cm.dispose();
            return;
        }

        var msg = "";
        msg += "#fs12##b#e   [일일 퀘스트]#k#n" + enter + enter;
        msg += "#L1##fs11##fUI/UIWindow.img/Quest/icon0#  (Lv. 190)#d 버려진 야영지#l#n#k" + enter;
        msg += "#L2##fs11##fUI/UIWindow.img/Quest/icon0#  (Lv. 190)#d 헤이븐#l#n#k" + enter;
        msg += "#L3##fs11##fUI/UIWindow.img/Quest/icon0#  (Lv. 200)#d 아케인포스#l#n#k" + enter;
        msg += "#L4##fs11##fUI/UIWindow.img/Quest/icon0#  (Lv. 265)#d 어센틱포스#l#n" + enter;
        msg += "#L5##fs11##fUI/UIWindow.img/Quest/icon0#  (Lv. 265)#d 강림월드 탐사#l#n#k" + enter;

        msg += enter + enter;

        msg += "#fs12##b#e   [반복 퀘스트]#k#n" + enter + enter;
        msg += "#L9##fs11##fUI/UIWindow.img/Quest/icon0#  잃어버린 크레파스#l" + enter;
        cm.sendSimple(msg);
    } else if (status == 1) {
        seld = sel;
        switch (sel) {
            case 1:
                cm.dispose();

                if (cm.getPlayer().getLevel() < 190) {
                	cm.sendOk("#fs11#" + 색 + "레벨이 부족합니다");
                	return;
                }

                cm.openNpcCustom(cm.getClient(), 3004434, "버려진 야영지");
                break;
            case 2:
                cm.dispose();

                if (cm.getPlayer().getLevel() < 190) {
                	cm.sendOk("#fs11#" + 색 + "레벨이 부족합니다");
                	return;
                }

                cm.openNpcCustom(cm.getClient(), 3004434, "헤이븐");
                break;
            case 3:
                cm.dispose();

                if (cm.getPlayer().getLevel() < 200) {
                	cm.sendOk("#fs11#" + 색 + "레벨이 부족합니다");
                	return;
                }

                cm.openNpcCustom(cm.getClient(), 3004434, "아케인일퀘");
                break;
            case 4:
                cm.dispose();

                if (cm.getPlayer().getLevel() < 265) {
                	cm.sendOk("#fs11#" + 색 + "레벨이 부족합니다");
                	return;
                }
                
                cm.openNpcCustom(cm.getClient(), 3004434, "어센틱일퀘");
                break;
            case 5:
                cm.dispose();

                if (cm.getPlayer().getLevel() < 265) {
                	cm.sendOk("#fs11#" + 색 + "레벨이 부족합니다");
                	return;
                }

                cm.openNpc(1052230, "일퀘무지개빛");
                break;
            case 9:
                cm.dispose();
                cm.openNpc(2082006);
                break;
        }
		
    }
}