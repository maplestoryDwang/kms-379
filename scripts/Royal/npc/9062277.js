// 상점시스템

importPackage(Packages.scripting);

var enter = "\r\n";
var seld = -1, seld2 = -1;

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

        //        var msg = "#fs11#     #fUI/UIWindow5.img/Disguise/backgrnd3#\r\n";
        var msg = "#fs11##fUI/Basic.img/Zenia/SC/2#\r\n";
        //      msg += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#\r\n";
        msg += "#L1##fUI/Basic.img/Zenia/SCBtn/201##l";//소비
        msg += "#L91##fUI/Basic.img/Zenia/SCBtn/200##l";//장비
        msg += "#L13##fUI/Basic.img/Zenia/SCBtn/202##l\r\n";//제작
        msg += "#L90##fUI/Basic.img/Zenia/SCBtn/203##l";//코인
        msg += "#L92##fUI/Basic.img/Zenia/SCBtn/205##l";//기타
        msg += "#L3##fUI/Basic.img/Zenia/SCBtn/204##l\r\n\r\n";//결정
        /*
                msg +="#L1##fs11##fUI/Basic.img/Zenia/SCBtn/10#" + "#l#L90##fUI/Basic.img/Zenia/SCBtn/6##l\r\n";
                msg +="#L91##fUI/Basic.img/Zenia/SCBtn/8#" + "#l#L92##fUI/Basic.img/Zenia/SCBtn/7##l\r\n";
                msg +="#L2##fUI/Basic.img/Zenia/SCBtn/11#" + "#l#L3##fUI/Basic.img/Zenia/SCBtn/9##l\r\n\r\n";
                msg +="　　　　　　　　　　#fc0xFFFF3366##L13##fUI/UIWindow4.img/pointShop/100658/iconShop# 제작 시스템#l\r\n"
        */
        //msg += "　　　　#fc0xFFFF3366##L11##fUI/UIWindow4.img/pointShop/501053/iconShop# 도네이션 (후원)#l #L12##fUI/UIWindow4.img/pointShop/501053/iconShop##fc0xFFFF3366# 서포터즈 (홍보)#l\r\n\r\n";
        //msg +="#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#k";
        cm.sendSimple(msg);
    } else if (status == 1) {
        seld = sel;
        switch (sel) {
            case 1: // 소비상점
                cm.dispose();
                cm.openShop(1);
                break;
            case 3: // 결정석판매
                cm.dispose();
                cm.openShop(9001212);
                break;
            case 11: // 후원상점
                cm.dispose();
                cm.openNpc(1530050);
                break;
            case 12: // 홍보상점
                cm.dispose();
                cm.openNpc(1530051);
                break;
            case 13: // 제작시스템
                if (cm.getInvSlots(1) < 3 || cm.getInvSlots(2) < 3 || cm.getInvSlots(3) < 3 || cm.getInvSlots(4) < 3 || cm.getInvSlots(5) < 3) {
                    cm.sendOkS("#fs11##fc0xFF6600CC#인벤토리를 탭별로 3칸이상 비워주세요", 2);
                    cm.dispose();
                    return;
                }
                var msg = "#fs11#    #fUI/Basic.img/Zenia/SC/2#\r\n";
                msg += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#k";
                msg += "#fc0xFF000099#　　#L1# [제작] 기본악세#l　　　　#L124# [제작] 초월악세#l \r\n"
                msg += "#fc0xFF000099#　　#L2# [제작] 에테르넬#l　　　　#L3# [제작] 제네시스#l\r\n\r\n"

                msg += "#fc0xFF990033##L4# [승급] 혼돈의 루타비스#l #L50# [승급] 혼돈의 아케인셰이드#l\r\n"
                msg += "#fc0xFF990033##L60# [승급] 혼돈의 에테르넬#l #L70# [승급] 혼돈의 칠흑 장신구#l\r\n\r\n"

                msg += "#fc0xFFFF3300##L301# [승급] 파멸의 아케인셰이드#l\r\n"
                msg += "#fc0xFFFF3300##L302# #Cgray#[출시전] 파멸의 에테르넬#k#l #fc0xFFFF3300##L304# [승급] 파멸의 칠흑 장신구#k#l\r\n\r\n"
                msg += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#k";
                cm.sendSimple(msg);
                break;
            case 90: // 코인상점
                var msg = "#fs11#    #fUI/Basic.img/Zenia/SC/2#\r\n";
                msg += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#k";
                msg += "　　#fc0xFF000099##L10##fUI/UIWindow4.img/pointShop/4310237/iconShop# 헌트코인#l";
                msg += "　#fc0xFF000099##L7##fUI/UIWindow4.img/pointShop/500629/iconShop# 유니온샵#l";
                msg += "　#fc0xFF000099##L8##fUI/UIWindow4.img/pointShop/100508/iconShop# 출석코인#l\r\n\r\n";
                msg += "　　#fc0xFF6633FF##L5##fUI/UIWindow4.img/pointShop/100711/iconShop# 네오스톤#l";
                msg += "　#fc0xFF6633FF##L6##fUI/UIWindow4.img/pointShop/100712/iconShop# 휴식상점#l";
                msg += "　#fc0xFF6633FF##L22##fUI/UIWindow4.img/pointShop/501215/iconShop# 네오코어#l";
                //msg += "　　#fc0xFF990033##L23##fUI/UIWindow4.img/pointShop/16393/iconShop# 반마력석#l";
                //msg += "　#fc0xFF6633FF##L6##fUI/UIWindow4.img/pointShop/100712/iconShop# 123123#l";
                //msg += "　#fc0xFF6633FF##L22##fUI/UIWindow4.img/pointShop/501215/iconShop# 123123#l\r\n\r\n";
                msg += "\r\n\r\n#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#k";
                cm.sendSimple(msg);
                break;
            case 91: // 장비상점
                var msg = "#fs11#    #fUI/Basic.img/Zenia/SC/2#\r\n";
                msg += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#k";
                msg += "　　#fc0xFF000099##L1##fUI/UIWindow4.img/pointShop/100658/iconShop##fc0xFFFF3300# 기본악세상점#l";
                msg += "　　　#fc0xFF000099##L2##fUI/UIWindow4.img/pointShop/100658/iconShop##fc0xFFFF3300# 보조무기상점#l\r\n\r\n";
                msg += "　　#L50##fUI/UIWindow4.img/pointShop/4310065/iconShop##fc0xFF0066CC# 파프니르 상점#fc0xFF000000##l\r\n\r\n";
                //msg += "#L51##fUI/UIWindow4.img/pointShop/4310065/iconShop##fc0xFF0066CC# 초월 파프니르 상점#fc0xFF000000# 이용하기#l\r\n";
                msg += "　　#L52##fUI/UIWindow4.img/pointShop/4310218/iconShop##fc0xFF000099# 아케인셰이드 상점#fc0xFF000000##l";
                msg += "  #L53##fUI/UIWindow4.img/pointShop/4310249/iconShop##fc0xFF000099# 아케인셰이드 상점#fc0xFF000000##l\r\n";
                msg += "　　#L90##fUI/UIWindow4.img/pointShop/4310218/iconShop##fc0xFF000099# #z4310218##fc0xFF000000# 교환#l";
                msg += " #L91##fUI/UIWindow4.img/pointShop/4310249/iconShop##fc0xFF000099# #z4310249##fc0xFF000000# 교환#l\r\n";
                msg += "　　#L900##fUI/UIWindow4.img/pointShop/4310218/iconShop##fc0xFF000099# #z4310218##fc0xFF000000# 교환2#l";
                //msg += "#L54##fUI/UIWindow4.img/pointShop/4318000/iconShop##fc0xFF000099# 초월 아케인 상점 #fc0xFF000000#이용#l#L92##fUI/UIWindow4.img/pointShop/4318000/iconShop##fc0xFF000099# #z4318000##fc0xFF000000# 교환#l\r\n\r\n";

                cm.sendSimple(msg);
                break;
            case 92: // 기타상점
                var msg = "#fs11#    #fUI/Basic.img/Zenia/SC/2#\r\n";
                msg += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#k";
                msg += "　　#fc0xFF000099##L3##fUI/Basic.img/BtCoin/normal/0##fc0xFFFF3300# 화폐 환전하기#l";
                msg += "　　　#fc0xFF000099##L4##fUI/Basic.img/BtCoin/normal/0##fc0xFFFF3300# 메소 화폐상점#l\r\n\r\n";
                msg += "　　#fc0xFF000099##L1##fUI/Basic.img/BtCoin/normal/0##fc0xFF000099# 모루 관련상점#l";
                msg += "　　　#fc0xFF000099##L2##fUI/Basic.img/BtCoin/normal/0##fc0xFF000099# 캐시 코디상점#l\r\n\r\n";
                //msg += "　　　　　　　　　#fc0xFF000099##L5##fUI/Basic.img/BtCoin/normal/0##fc0xFFFF3300# 붉구 교환소#l\r\n";
                //msg += "　　　　　　　　　#fc0xFF000099##L6##fUI/Basic.img/BtCoin/normal/0##fc0xFFFF3300# 초월 아이템#l\r\n\r\n";
                msg += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#k";
                cm.sendSimple(msg);
                break;
        }
    } else if (status == 2) {
        seld2 = sel;
        switch (seld) {
            case 1:
                switch (sel) {
                    case 1:
                        cm.dispose();
                        cm.openShop(1);
                        break;
                }
                break;
            case 13: // 제작시스템
                switch (sel) {
                    case 1:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 3006009, "BaseAcc");
                        break;
                    case 2:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 3006009, "강림에테제작");
                        break;
                    case 3:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 3006009, "강림제네제작");
                        break;
                    case 4:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 3006009, "강림파프승급");
                        break;
                    case 50:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 3006009, "강림아케인승급");
                        break;
                    case 60:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 3006009, "강림에테승급");
                        break;
                    case 70:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 3006009, "강림칠흑승급");
                        break;
                    case 124:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 3006009, "강림초월악세");
                        break;
                    case 301:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 3006009, "강림아케인파멸");
                        break;
                    case 304:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 3006009, "강림칠흑파멸");
                        break;
                }
            case 90:
                switch (sel) {
                    case 5:
                        cm.dispose();
                        cm.openShop(9062459);
                        break;
                    case 6:
                        cm.dispose();
                        cm.openShop(9001213);
                        break;
                    case 7:
                        cm.openShop(9010107);
                        break;
                    case 8:
                        cm.dispose();
                        cm.openShop(18);
                        break;
                    case 10:
                        cm.dispose();
                        cm.openShop(1302011);
                        break;
                    case 22:
                        cm.dispose();
                        cm.openShop(20);
                        break;
                    case 23:
                        cm.dispose();
                        cm.openShop(21);
                        break;
                }
                break;
            case 91:
                switch (sel) {
                    case 1:
                        cm.dispose();
                        cm.openShop(9031015);
                        break;
                    case 2:
                        cm.dispose();
                        cm.openShop(2);
                        break;
                    case 50:
                        cm.dispose();
                        cm.openShop(1064003);
                        break;
                    case 51:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 9000213, "royalPaf");
                        break;
                    case 52:
                        cm.dispose();
                        cm.openShop(4);
                        break;
                    case 53:
                        cm.dispose();
                        cm.openShop(5);
                        break;
                    case 54: // 초월아케인
                        cm.dispose();
                        cm.openNpc(1052206, "제작초월아케인");
                        break;
                    // 재료교환
                    case 90:
                        cm.dispose();
                        cm.openNpc(3003105);
                        break;
                    case 900:
                        cm.dispose();
                        cm.openNpc(3006264);
                        break;
                    case 91:
                        cm.dispose();
                        cm.openNpc(3003536);
                        break;
                    case 92: // 초월아케인 재료교환
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 9000213, "초월아케인재료교환");
                        break;
                }
                break;
            case 92:
                switch (sel) {
                    case 1:
                        cm.dispose();
                        cm.openShop(9031003);
                        break;
                    case 2:
                        cm.dispose();
                        cm.openShop(6); //캐시상점
                        break;
                    case 3:
                        cm.dispose();
                        cm.openShop(17); // 메소환전
                    case 4:
                        cm.dispose();
                        cm.openShop(22); // 메소상점
                        break;
                    case 5:
                        cm.dispose();
                        cm.openShop(23); // 붉구상점
                    case 6:
                        cm.dispose();
                        cm.openShop(24); // 초월아이템
                }
                break;
        }
    } else if (status == 3) {
        if (seld == 4 && seld2 == 3) {
            switch (sel) {
                case 1:
                    cm.dispose();
                    cm.openShop(10);
                    break;
                case 2:
                    cm.dispose();
                    cm.openNpc(10);
                    break;
                case 3:
                    cm.dispose();
                    cm.openNpc(10);
                    break;
                case 4:
                    cm.dispose();
                    cm.openNpc(10);
                    break;
                case 5:
                    cm.dispose();
                    cm.openNpc(10);
                    break;
            }
        }
    }
}