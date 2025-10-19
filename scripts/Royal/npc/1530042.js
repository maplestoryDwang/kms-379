// 로얄 성장시스템
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
enter = "\r\n"

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

        cm.getClient().setKeyValue("current", "1");
        // [임시]일일퀘 오류 해결용

        var msg = "#fs11#       #fUI/Basic.img/Zenia/SC/0#\r\n";
        msg += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n";
        msg +="        #L1##fUI/Basic.img/Zenia/SCBtn/400##l      ";//육성퀘스트
        msg +="#L2##fUI/Basic.img/Zenia/SCBtn/402##l\r\n";//일일퀘스트
        msg +="        #L1000##fUI/Basic.img/Zenia/SCBtn/405##l      ";//능력치랭크
        msg +="#L3##fUI/Basic.img/Zenia/SCBtn/403##l";//레벨업보상
        //msg +="#L999##fUI/Basic.img/Zenia/SCBtn/404##l";//헌트패스
        //msg +="#L777##fUI/Basic.img/Zenia/SCBtn/401##l";//메소패스
        cm.sendSimple(msg);
    } else if (status == 1) {
        seld = sel;
        switch (sel) {
            case 1:
                cm.dispose();
                cm.openNpc(9000368);
                break;
            case 2:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 1530042, "일퀘");
                break;
            case 3:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 1530042, "LevelReward");
                break;

            case 777: // 시즌패스
                var msg = "#fs11#";
                msg += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n#fc0xFFFF3300#";
                msg += 색 + "            #L1#" + 검은마법사 + " 시즌패스#l#L2#" + 포켓 + 핑크색 + " [프리미엄]" + 색 + " 시즌패스#l\r\n\r\n";
                msg += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n#fc0xFFFF3300#";
                cm.sendSimple(msg);
                break;
            case 999: // 엑어빌
                cm.dispose();
                Packages.scripting.newscripting.ScriptManager.runScript(cm.getPlayer().getClient(), "test", null);
                break;
            case 1000: // 랭크승급
                var msg = "#fs11#";

                msg +="    #L1##b메인랭크승급#l  ";
                msg +="#L2#보스랭크승급#l  ";
                msg +="#L3#능력랭크승급#l";

                cm.sendSimple(msg);
                break;
            case 1001: // 장비강화
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 1530050, "eqpupgrade");
                break;
        }
    } else if (status == 2) {
        switch (seld) {
            case 777:
                switch (sel) {
                    case 1:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 9000213, "시즌패스");
                        break;
                    case 2:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 9000213, "프리미엄패스");
                        break;
                }
                break;
            case 1000:
                switch (sel) {
                    case 1:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 9071000, "ZodiacRank");
                        break;
                    case 2:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 9071000, "BossRank");
                        break;
                    case 3:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 9071000, "StatRank");
                        break;
                }
                break;
        }
    }
}