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
enter = "\r\n";

importPackage(Packages.objects.item);
importPackage(Packages.constants);


var need = [
        {'itemid' : 4317001, 'qty' : 1},
        {'itemid' : 4317002, 'qty' : 1},
        {'itemid' : 4317003, 'qty' : 1}
]

var tocoin = 4310320, toqty = 1;

var bosang = [
[2430030, 2],
[2430031, 2],
[2430032, 10],
[2430033, 10],
[2450134, 5],
[4310266, 1000]
]

function start() {
     status = -1;    
     action (1, 0, 0);
}

function action(mode, type, sel) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }

    if (status == 0) {
        // 첫 이벤트 클릭시 애니메이션재생
        if (getFirst() == null) {
            cm.dispose();
            cm.getClient().setKeyValue("12dnjfeventFirst", "1");
            cm.warp(209000013, 0);
            cm.openNpcCustom(cm.getClient(), 2001004, "12dnjfeventeffect");
            return;
        }

            var msg = "　#i4001395# #fs11##e[로얄메이플] - 겨울 & 크리스마스 이벤트#n#fs11# #i4001395#\r\n#fs11##Cblue#              항상 로얄 메이플을 이용해주셔서 감사합니다#k\r\n";
                    msg += 핑크색 + "                               #L100#이벤트안내#fc0xFF000000##l\r\n\r\n";
                    msg += "                      이벤트 기간 : #b11.28 ~ 12.26\r\n";
                    msg += "\r\n#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
                    msg += "#L1##fc0xFFFF3366#이벤트 맵#fc0xFF000000# 이동#l\r\n";
                    msg += "#L2##fc0xFFFF3366#이벤트 아이템#fc0xFF000000# 교환#l\r\n";
                    msg += "#L3##fc0xFF6600CC#이벤트 아이템#fc0xFF000000# 상점#l\r\n";
                    msg += "#L4##fc0xFF6600CC#이벤트 아이템 수급처#fc0xFF000000# 확인#l\r\n";
                    msg += "\r\n#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
            if (cm.getPlayer().getMapId() == 209000013) {
            var msg = "　#i4001395# #fs11##e[로얄메이플] - 겨울 & 크리스마스 이벤트#n#fs11# #i4001395#\r\n#fs11##Cblue#              항상 로얄 메이플을 이용해주셔서 감사합니다#k\r\n";
                    msg += 핑크색 + "                               #L100#이벤트안내#fc0xFF000000##l\r\n\r\n";
                    msg += "                      이벤트 기간 : #b11.28 ~ 12.26\r\n";
                    msg += "\r\n#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
                    msg += "#L1001##fc0xFFFF3366#로얄 캐슬#fc0xFF000000# 이동#l\r\n";
                    msg += "#L2##fc0xFF6600CC#이벤트 아이템#fc0xFF000000# 교환#l\r\n";
                    msg += "#L3##fc0xFF6600CC#이벤트 아이템#fc0xFF000000# 상점#l\r\n";
                    msg += "#L4##fc0xFF6600CC#이벤트 아이템 수급처#fc0xFF000000# 확인#l\r\n";
                    msg += "\r\n#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
            }
            cm.sendSimple(msg);
     } else if(status == 1) {
        seld = sel;
        switch(sel) {
            case 100:
            var msg = "#fn나눔고딕 Extrabold##d(사냥을하면 다양한 사탕들이 드롭되던데..)#k\r\n\r\n#b얼른 사냥을하고 사탕들을 모아야겠어!\r\n\r\n";
                    msg += "#L96##r그런데 이벤트 아이템어디에 사용하는거지..?#l\r\n";
            cm.sendNextS(msg, 2);
            break;
            
            case 1:
            cm.warp(209000013, 0);
            cm.dispose();

            break;
            
            case 2:
            var msg = "#fs11#" + 핑크색 + "사탕을 모아오셨나요?! 크리스마스 코인으로 교환해드릴 수 있어요!" + 검은색 +enter+enter;

            for (i = 0; i < need.length; i++) {
                if (i != need.length - 1) msg += "#i"+need[i]['itemid']+"##z"+need[i]['itemid']+"# "+need[i]['qty']+"개와"+enter;
                else msg += "#fs11##i"+need[i]['itemid']+"##z"+need[i]['itemid']+"# "+need[i]['qty']+"개를 주신다면\r\n대신 제가 가진 #b#i"+tocoin+"##z"+tocoin+"#" + 검은색 + "을 드리죠!";
            }
                
            if (haveNeed(1))
                cm.sendNext(msg);
            else {
                msg += enter+enter+"허나.. 모험가님은 사탕을 모두 가지고있지 않으시네요";
                cm.sendOk(msg);
                cm.dispose();
            }
            break;

            case 3:
            cm.dispose();
            cm.openShop(9999);
            break;

            case 4:
            var msg = "#fs11#";
                    msg += "#fs11##b#i4317001##z4317001#\r\n#i4317002##z4317002#\r\n#i4317003##z4317003#\r\n\r\n";
                    msg += "#r#e[낚시]#b 시 일정 확률로 위 세가지 아이템 획득가능\r\n";
                    msg += "#r#e[사냥]#b 시 일정 확률로 위 세가지 아이템 획득가능\r\n\r\n";
                    msg += 핑크색 + "                   위 세가지 사탕을\r\n\r\n#b#i4310320# #z4310320#" + 핑크색 + " 으로교환할 수 있습니다";
            cm.sendOk(msg);
            cm.dispose();
            break;
            
            
            case 1000:
            if (getCount() == 7) {
                if (getClear() == null) {
                    cm.sendYesNo("#fs11#도장을 다 받아오셨군요! 특별한 선물을 지급받으시겠어요?\r\n\r\n#r※ 특별한 아이템은 계정당 1회만 지급됩니다");
                    break;
                } else {
                    msg = "#fs11#이미 특별한 선물을 지급받으셨어요!";
                }
            } else {
                msg = "#fs11#도장을다 받아오지 못하셨어요..\r\n\r\n" + 핑크색 + 별 + " 현재 찍은 도장 갯수 : " + getCount() + " / 7";
            }
            cm.sendOk(msg);
            cm.dispose();
            break;

            case 1001:
            cm.warp(ServerConstants.TownMap, 0);
            cm.dispose();
            break;
            
        }
        
    } else if(status == 2) {
        switch(seld) {
            case 100:
                cm.sendOk("#fs11#" + 색 + "이벤트 상점에서 #i4310320# #z4310320#으로 다양한 아이템을 구매할 수 있어요!");
                cm.dispose();
            break;
            
            case 2:
            temp = [];
            for (i = 0; i < need.length; i++) {
                temp.push(Math.floor(cm.itemQuantity(need[i]['itemid']) / need[i]['qty']));
            }
            temp.sort();
            max = temp[0];
            cm.sendGetNumber("#fs11#모험가님은 최대 #b #i" + tocoin + "##z"+ tocoin + "# " + max+"개를#k 교환할 수 있어요..\r\n\r\n몇 개를 교환하시겠어요?", 1, 1, max);
            break;

        }
    } else if(status == 3) {
        switch(seld) {
            case 2:
            if (!haveNeed(sel)) {
                cm.sendOk("#fs11#모험가님의 사탕이 부족해요");
                cm.dispose();
                return;
            }
            for (i = 0; i < need.length; i++) {
                cm.gainItem(need[i]['itemid'], -(need[i]['qty'] * sel));
            }
            cm.gainItem(tocoin, (toqty * sel));
            cm.sendOk("#fs11#교환이 완료되었습니다!");
            cm.dispose();
            break;

        }
    }
}

function haveNeed(a) {
        var ret = true;
        for (i = 0; i < need.length; i++) {
                if (!cm.haveItem(need[i]['itemid'], (need[i]['qty'] * a)))
                        ret = false;
        }
        return ret;
}


function getFirst() {
    return cm.getClient().getKeyValue("12dnjfeventFirst");
}