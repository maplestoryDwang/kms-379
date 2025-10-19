var enter = "\r\n";
var seld = -1;

포켓 = "#fUI/Basic.img/RoyalBtn/theblackcoin/23#";
검은마법사 = "#fUI/Basic.img/RoyalBtn/theblackcoin/42#";

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

        var msg = "#fs11#       #fUI/Basic.img/Zenia/SC/0#\r\n";
        msg += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n";
        msg +="#L1##fUI/Basic.img/Zenia/SCBtn/600##l";
        msg +="#L2##fUI/Basic.img/Zenia/SCBtn/601##l";
        msg +="#L3##fUI/Basic.img/Zenia/SCBtn/602##l";

        cm.sendSimple(msg);

    } else if (status == 1) {
        switch (sel) {
            case 1:
            var msg = "#fs11#";
            msg += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n#fc0xFFFF3300#";
            msg += 색 + "              #L1##fUI/UIWindow4.img/pointShop/100658/iconShop# 장비 일반강화#l#L2##fUI/UIWindow4.img/pointShop/100658/iconShop# 장비 초월강화#l\r\n\r\n";
            msg += 색 + "                         #L3##fUI/UIWindow4.img/pointShop/501372/iconShop# #r메소강화 초기화#l\r\n\r\n";
            msg += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n#fc0xFFFF3300#";
            cm.sendSimple(msg);
            break;
        case 2:
            cm.dispose();
            cm.openNpcCustom(cm.getClient(), 3004434, "BallEn");
            break;
        case 3:
            cm.dispose();
            cm.openNpcCustom(cm.getClient(), 3004434, "CashEn");
            break;
        }
    } else if (status == 2) {
        switch (sel) {
            case 1:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 3004434, "MesoEn");
                break;
            case 2:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 3004434, "MesoEnHell");
                break;
            case 3:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 3004434, "MesoEnR");
                break;
        }
    }
}