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
        {'itemid' : 3994408, 'qty' : 1},
        {'itemid' : 3994409, 'qty' : 1},
        {'itemid' : 3994410, 'qty' : 1}
]

var tocoin = 4310185, toqty = 1;

var bosang = [
[2430030, 2],
[2430031, 2],
[2430032, 10],
[2430033, 10],
[2450134, 5],
[4310266, 1000]
]

var bosang1 = [1009902, 1109902, 1089902, 1079902, 1112048, 1012992, 1022992, 1032992];

function MakeItem(itemid) {
    var ii = Packages.objects.item.MapleItemInformationProvider.getInstance();
    var it = ii.getEquipById(itemid);
    it.setStr(444);
    it.setDex(444);
    it.setInt(444);
    it.setLuk(444);
    it.setWatk(333);
    it.setMatk(333);
    it.setCHUC(30);
    it.setAllStat(5);
    it.setOwner("로얄 메이플");
    it.setExpiration((new Date()).getTime() + (1000 * 60 * 60 * 24 * 30));
    Packages.objects.item.MapleInventoryManipulator.addFromDrop(cm.getPlayer().getClient(), it, false);
}

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
        cm.getClient().removeKeyValue("2023cntjreventFirst");

        // 첫 이벤트 클릭시 애니메이션재생
     /*   if (getFirst() == null) {
            cm.dispose();
            cm.getPlayer().dropMessage(6, "입장컷신발동!");
            cm.getClient().setKeyValue("2023cntjreventFirst", "1");
            cm.warp(307090001);
            cm.openNpcCustom(cm.getClient(), 9001102, "2023cntjreventeffect");
            return;
        }*/

        // Count 시작
        if (getCount() == null)
            cm.getClient().setKeyValue("2023cntjrevent", "0");

        if (cm.getPlayer().getMapId() == 120040000) {
             msg = "#fs11#여기는 이미 도장을 찍으셨거나 순서가 틀렸어요!\r\n\r\n" + 핑크색 + 별 + " 현재 찍은 도장 갯수 : " + getCount() + " / 7";
             if (getCount() == 0) {
                 cm.getClient().setKeyValue("2023cntjrevent", "1");
                 cm.showEffect(false, "Effect/CharacterEff.img/GradeUp");
                 cm.showEffect(false, "Effect/EventEffect.img/SalonDebut/screenEff/1366");
                 msg = "#fs11#도장을 찍어드렸어요 !\r\n\r\n" + 핑크색 + 별 + " 현재 찍은 도장 갯수 : " + getCount() + " / 7";
             }
             cm.sendOk(msg);
             cm.dispose();
             return;
         }

        if (cm.getPlayer().getMapId() == 100000000) {
             msg = "#fs11#여기는 이미 도장을 찍으셨거나 순서가 틀렸어요!\r\n\r\n" + 핑크색 + 별 + " 현재 찍은 도장 갯수 : " + getCount() + " / 7";
             if (getCount() == 1) {
                 cm.getClient().setKeyValue("2023cntjrevent", "2");
                 cm.showEffect(false, "Effect/CharacterEff.img/GradeUp");
                 cm.showEffect(false, "Effect/EventEffect.img/SalonDebut/screenEff/1366");
                 msg = "#fs11#도장을 찍어드렸어요 !\r\n\r\n" + 핑크색 + 별 + " 현재 찍은 도장 갯수 : " + getCount() + " / 7";
             }
             cm.sendOk(msg);
             cm.dispose();
             return;
         }
         
        if (cm.getPlayer().getMapId() == 101000000) {
             msg = "#fs11#여기는 이미 도장을 찍으셨거나 순서가 틀렸어요!\r\n\r\n" + 핑크색 + 별 + " 현재 찍은 도장 갯수 : " + getCount() + " / 7";
             if (getCount() == 2) {
                 cm.getClient().setKeyValue("2023cntjrevent", "3");
                 cm.showEffect(false, "Effect/CharacterEff.img/GradeUp");
                 cm.showEffect(false, "Effect/EventEffect.img/SalonDebut/screenEff/1366");
                 msg = "#fs11#도장을 찍어드렸어요 !\r\n\r\n" + 핑크색 + 별 + " 현재 찍은 도장 갯수 : " + getCount() + " / 7";
             }
             cm.sendOk(msg);
             cm.dispose();
             return;
         }
         
        if (cm.getPlayer().getMapId() == 102000000) {
             msg = "#fs11#여기는 이미 도장을 찍으셨거나 순서가 틀렸어요!\r\n\r\n" + 핑크색 + 별 + " 현재 찍은 도장 갯수 : " + getCount() + " / 7";
             if (getCount() == 3) {
                 cm.getClient().setKeyValue("2023cntjrevent", "4");
                 cm.showEffect(false, "Effect/CharacterEff.img/GradeUp");
                 cm.showEffect(false, "Effect/EventEffect.img/SalonDebut/screenEff/1366");
                 msg = "#fs11#도장을 찍어드렸어요 !\r\n\r\n" + 핑크색 + 별 + " 현재 찍은 도장 갯수 : " + getCount() + " / 7";
             }
             cm.sendOk(msg);
             cm.dispose();
             return;
         }
         
        if (cm.getPlayer().getMapId() == 120000000) {
             msg = "#fs11#여기는 이미 도장을 찍으셨거나 순서가 틀렸어요!\r\n\r\n" + 핑크색 + 별 + " 현재 찍은 도장 갯수 : " + getCount() + " / 7";
             if (getCount() == 4) {
                 cm.getClient().setKeyValue("2023cntjrevent", "5");
                 cm.showEffect(false, "Effect/CharacterEff.img/GradeUp");
                 cm.showEffect(false, "Effect/EventEffect.img/SalonDebut/screenEff/1366");
                 msg = "#fs11#도장을 찍어드렸어요 !\r\n\r\n" + 핑크색 + 별 + " 현재 찍은 도장 갯수 : " + getCount() + " / 7";
             }
             cm.sendOk(msg);
             cm.dispose();
             return;
         }
         
        if (cm.getPlayer().getMapId() == 200000000) {
             msg = "#fs11#여기는 이미 도장을 찍으셨거나 순서가 틀렸어요!\r\n\r\n" + 핑크색 + 별 + " 현재 찍은 도장 갯수 : " + getCount() + " / 7";
             if (getCount() == 5) {
                 cm.getClient().setKeyValue("2023cntjrevent", "6");
                 cm.showEffect(false, "Effect/CharacterEff.img/GradeUp");
                 cm.showEffect(false, "Effect/EventEffect.img/SalonDebut/screenEff/1366");
                 msg = "#fs11#도장을 찍어드렸어요 !\r\n\r\n" + 핑크색 + 별 + " 현재 찍은 도장 갯수 : " + getCount() + " / 7";
             }
             cm.sendOk(msg);
             cm.dispose();
             return;
         }
         
        if (cm.getPlayer().getMapId() == 101050000) {
             msg = "#fs11#여기는 이미 도장을 찍으셨거나 순서가 틀렸어요!\r\n\r\n" + 핑크색 + 별 + " 현재 찍은 도장 갯수 : " + getCount() + " / 7";
             if (getCount() == 6) {
                 cm.getClient().setKeyValue("2023cntjrevent", "7");
                 cm.showEffect(false, "Effect/CharacterEff.img/GradeUp");
                 cm.showEffect(false, "Effect/EventEffect.img/SalonDebut/screenEff/1366");
                 msg = "#fs11#도장을 찍어드렸어요 !\r\n\r\n" + 핑크색 + 별 + " 현재 찍은 도장 갯수 : " + getCount() + " / 7";
             }
             if (getCount() == 7) {
                 msg = "#fs11#모든 도장을 다 찍으셨어요! 얼른 1채널의 이벤트맵으로 가보세요\r\n\r\n" + 핑크색 + 별 + " 현재 찍은 도장 갯수 : " + getCount() + " / 7";
             }
             cm.sendOk(msg);
             cm.dispose();
             return;
         }
        
          
            var msg = "　　　#i4031187# #fs14##e[로얄메이플] - 오픈 이벤트#n#fs11# #i4031187#\r\n#fs11##Cblue#              항상 로얄 메이플을 이용해주셔서 감사합니다#k\r\n";
                    msg += 핑크색 + "                               #L100#이벤트안내#fc0xFF000000##l\r\n\r\n";
                    msg += "                      이벤트 기간 : #b23.12.15~24.01.31\r\n";
                    msg += "\r\n#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
           //         msg += "#L1##fc0xFFFF3366#이벤트 맵#fc0xFF000000# 이동#l\r\n";
                    msg += "#L2##fc0xFFFF3366#이벤트 아이템#fc0xFF000000# 교환#l\r\n";
                    msg += "#L3##fc0xFF6600CC#이벤트 아이템#fc0xFF000000# 상점#l\r\n";
                    msg += "#L4##fc0xFF6600CC#이벤트 아이템 수급처#fc0xFF000000# 확인#l\r\n";
                    msg += "#L5##fc0xFF6600CC#스페셜 아이템#fc0xFF000000# 지급받기#l\r\n";
                    msg += "\r\n#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
            if (cm.getPlayer().getMapId() == 307090001) {
            var msg = "　　　#i4031187# #fs14##e[로얄메이플] - 오픈 이벤트#n#fs11# #i4031187#\r\n#fs11##Cblue#              항상 로얄 메이플을 이용해주셔서 감사합니다#k\r\n";
                    msg += 핑크색 + "                               #L100#이벤트안내#fc0xFF000000##l\r\n\r\n";
                    msg += "                      이벤트 기간 : #b23.12.15~24.01.31\r\n";
                    msg += "\r\n#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
                    msg += "#L1001##fc0xFFFF3366#로얄 정원#fc0xFF000000# 이동#l\r\n";
                    msg += "#L1000##fc0xFFFF3366#찍은 도장#fc0xFF000000# 확인#l\r\n";
                    msg += "#L2##fc0xFF6600CC#이벤트 아이템#fc0xFF000000# 교환#l\r\n";
                    msg += "#L3##fc0xFF6600CC#이벤트 아이템#fc0xFF000000# 상점#l\r\n";
                    msg += "#L5##fc0xFF6600CC#스페셜 아이템#fc0xFF000000# 지급받기#l\r\n";
                    msg += "\r\n#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
            }
            cm.sendSimple(msg);
     } else if(status == 1) {
        seld = sel;
        switch(sel) {
            case 100:
            var msg = "#fn나눔고딕 Extrabold##d(사냥을하면 다양한 송편들이 드롭되던데..)#k\r\n\r\n#b얼른 사냥을하고 송편들을 모아야겠어!\r\n\r\n";
                    msg += "#L96##r그런데 이벤트 아이템어디에 사용하는거지..?#l\r\n";
            cm.sendNextS(msg, 2);
            break;
            
            case 1:
            cm.warp(307090001, 0);
            cm.dispose();

            break;
            
            case 2:
            var msg = "#fs11#" + 핑크색 + "송편을 모아오셨나요?! 추석코인으로 교환해드릴 수 있어요!" + 검은색 +enter+enter;

            for (i = 0; i < need.length; i++) {
                if (i != need.length - 1) msg += "#i"+need[i]['itemid']+"##z"+need[i]['itemid']+"# "+need[i]['qty']+"개와"+enter;
                else msg += "#fs11##i"+need[i]['itemid']+"##z"+need[i]['itemid']+"# "+need[i]['qty']+"개를 주신다면\r\n대신 제가 가진 #b#i"+tocoin+"##z"+tocoin+"#" + 검은색 + "을 드리죠!";
            }
                
            if (haveNeed(1))
                cm.sendNext(msg);
            else {
                msg += enter+enter+"허나.. 모험가님은 송편을 모두 가지고있지 않으시네요";
                cm.sendOk(msg);
                cm.dispose();
            }
            break;

            case 3:
            cm.dispose();
            cm.openShop(3333);
            break;

            case 4:
            var msg = "#fs11#";
                    msg += "#fs11##b#i3994408##z3994408#\r\n#i3994409##z3994409#\r\n#i3994410##z3994410#\r\n\r\n";
                    msg += "#r#e[낚시]#b 시 일정 확률로 위 세가지 아이템 획득가능\r\n";
                    msg += "#r#e[사냥]#b 시 일정 확률로 위 세가지 아이템 획득가능\r\n\r\n";
                    msg += 핑크색 + "                   위 세가지 송편을\r\n\r\n#b#i4310185# #z4310185#" + 핑크색 + " 으로교환할 수 있습니다";
            cm.sendOk(msg);
            cm.dispose();
            break;
            
            case 5:
            if (cm.getClient().getKeyValue("2023cntjreventitem") == null) {
                msg = "#fs11#스페셜 아이템을 지급받으시겠어요?\r\n\r\n#r※ 스페셜 아이템은 계정당 1회만 지급됩니다 (캐릭터 이동 불가)\r\n※ 스페셜 아이템은 30일 기간제이며 기간만료시 삭제됩니다";
                msg += "\r\n\r\n#b < 1개월 스페셜 아이템 리스트 >\r\n\r\n";
                for (i = 0; i < bosang1.length; i++) {
                    msg += 색 + "#i"+bosang1[i]+"# #z"+bosang1[i]+"#" +enter;
                }
                cm.sendYesNo(msg);
            } else {
                msg = "#fs11#" + 검은색 + "이미 #fc0xFFFF3366#스페셜 아이템" + 검은색 + " 을 지급받으셨습니다";
                cm.dispose();
                cm.sendOk(msg);
            }
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
                cm.sendOk("#fs11#" + 색 + "이벤트 상점에서 #i4310185# #z4310185#으로 다양한 아이템을 구매할 수 있어요!");
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

            case 5:
            if (cm.getInvSlots(6) < 8) {
                cm.sendOk("#fs11#인벤토리 공간이 부족합니다.\r\n치장 탭에 7칸이 필요합니다.");
                cm.dispose();
                return;
            }
            
            msg = "#fs11#" + 획득 + enter + enter;
            for (i = 0; i < bosang1.length; i++) {
                msg += 색 + "#i"+bosang1[i]+"# #z"+bosang1[i]+"#" +enter;
                MakeItem(bosang1[i]);
            }
            cm.getClient().setKeyValue("2023cntjreventitem", "1");
            msg += 핑크색 + "\r\n위와 같은 아이템이 지급되었습니다";
            cm.sendOk(msg);
            cm.dispose();
            break;

            case 1000:
            if (cm.getInvSlots(1) < 3 || cm.getInvSlots(2) < 3 || cm.getInvSlots(3) < 3 || cm.getInvSlots(4) < 3 || cm.getInvSlots(5) < 3) {
                cm.sendOkS("#fs11##fc0xFF6600CC#인벤토리를 탭별로 3칸이상 비워주세요", 2);
                cm.dispose();
                return;
            }
            
            msg = "#fs11#" + 획득 + enter;
            for (i = 0; i < bosang.length; i++) {
                msg += 색 + "#i"+bosang[i][0]+"# #z"+bosang[i][0]+"##r "+ bosang[i][1] +"개#k" +enter;
                cm.gainItem(bosang[i][0], bosang[i][1]);
            }
            msg += 핑크색 + "\r\n위와 같은 아이템이 지급되었습니다";
            cm.sendOk(msg);
            cm.getClient().setKeyValue("2023cntjrevent1", "1");
            cm.dispose();
            break;
        }
    } else if(status == 3) {
        switch(seld) {
            case 2:
            if (!haveNeed(sel)) {
                cm.sendOk("#fs11#모험가님의 송편이 부족해요");
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

function getCount() {
    return cm.getClient().getKeyValue("2023cntjrevent");
}

function getFirst() {
    return cm.getClient().getKeyValue("2023cntjreventFirst");
}

function getClear() {
    return cm.getClient().getKeyValue("2023cntjrevent1");
}