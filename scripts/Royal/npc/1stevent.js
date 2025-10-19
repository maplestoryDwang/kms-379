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

var tocoin = 4310184, toqty = 1;

var bosang = [1009900, 1109900, 1089900, 1079900, 1112046, 1012990, 1022990, 1032990];

var bosang1 = [3700599];

var dayitem = [];

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

function MakeItem1(itemid) {
    //칭호 기간 부여하기
    var item = new Item(itemid, 1, 1, 0);
    item.setExpiration((new Date()).getTime() + (1000 * 60 * 60 * 24 * 60));
    Packages.objects.item.MapleInventoryManipulator.addFromDrop(cm.getPlayer().getClient(), item, false);
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
        // 첫 이벤트 클릭시 애니메이션재생

        if (cm.getClient().getKeyValue("1stday") == null) {
            cm.getClient().setKeyValue("1stday", 0);
        }

        count = cm.getClient().getKeyValue("1stday");
        if (count == 0) {
            dayitem = [
            [4310282, 30]
            ]
        } else if (count == 1) {
            dayitem = [
            [4310237, 1000]
            ]
        } else if (count == 2) {
            dayitem = [
            [4310266, 1000]
            ]
        } else if (count == 3) {
            dayitem = [
            [4310229, 1000]
            ]
        } else if (count == 4) {
            dayitem = [
            [4001715, 100]
            ]
        } else if (count == 5) {
            dayitem = [
            [2630127, 1]
            ]
        } else if (count == 6) {
            dayitem = [
            [2439990, 2]
            ]
        } else if (count == 7) {
            dayitem = [
            [5060048, 5]
            ]
        } else if (count == 8) {
            dayitem = [
            [4036660, 10]
            ]
        } else if (count == 9) {
            dayitem = [
            [5068305, 5]
            ]
        } else if (count == 10) {
            dayitem = [
            [4036661, 2]
            ]
        } else if (count == 11) {
            dayitem = [
            [2439932, 1]
            ]
        } else if (count == 12) {
            dayitem = [
            [2439932, 2]
            ]
        } else if (count == 13) {
            dayitem = [
            [2439990, 2]
            ]
        }

            var msg = "　#i4310175# #fs11##e[로얄메이플] - 1St Anniversary 이벤트#n#fs11# #i4310175#\r\n#fs11##Cblue#              항상 로얄 메이플을 이용해주셔서 감사합니다#k\r\n";
                    msg += 핑크색 + "                               #L100#이벤트안내#fc0xFF000000##l\r\n\r\n";
                    msg += "                      이벤트 기간 : #b04.17 ~ 05.31\r\n";
                    msg += "\r\n#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
                    //msg += "#L1##fc0xFFFF3366#이벤트 맵#fc0xFF000000# 이동#l\r\n";
                    //msg += "#L2##fc0xFFFF3366#이벤트 아이템#fc0xFF000000# 교환#l\r\n";
                    msg += "#L3##fc0xFF6600CC#이벤트 코인샵#fc0xFF000000# 이용#l\r\n";
                    msg += "#L4##fc0xFF6600CC#이벤트 아이템 수급처#fc0xFF000000# 확인#l\r\n";
                    msg += "#L5##fc0xFF6600CC#스페셜 아이템#fc0xFF000000# 지급받기#l\r\n\r\n";
                    msg += "#L6##fc0xFF6600CC#출석체크#fc0xFF000000#[ " + count + "일/14일 ]#l\r\n";
                    msg += "\r\n#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";

            cm.sendSimple(msg);
     } else if(status == 1) {
        seld = sel;
        switch(sel) {
            case 100:
            var msg = "#fn나눔고딕 Extrabold##d(사냥과 잠수를하면 코인이 드롭되던데..)#k\r\n\r\n#b얼른 코인을 모아야겠어!\r\n\r\n";
                    msg += "#r그런데 이벤트 코인은 어디에 사용하는거지..?\r\n";
            cm.sendNextS(msg, 2);
            break;
            
            case 1:
            cm.warp(993031000, 0);
            cm.dispose();

            break;
            
            case 2:
            var msg = "#fs11#" + 핑크색 + "사탕을 모아오셨나요?! 1St Anniversary 코인으로 교환해드릴 수 있어요!" + 검은색 +enter+enter;

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
            cm.openShop(5555);
            break;

            case 4:
            var msg = "#fs11#";
                    msg += "#fs11##b#i4310175##z4310175#\r\n\r\n";
                    msg += "#r#e[낚시]#b 시 일정 확률로 위 코인 획득가능\r\n";
                    msg += "#r#e[사냥]#b 시 일정 확률로 위 코인 획득가능";
            cm.sendOk(msg);
            cm.dispose();
            break;
            
            
            case 5:
            if (cm.getClient().getKeyValue("1steventitem") == null) {
                msg = "#fs11#스페셜 아이템을 지급받으시겠어요?\r\n\r\n#r※ 스페셜 아이템은 계정당 1회만 지급됩니다\r\n※ 스페셜 아이템은 30일 기간제이며 기간만료시 삭제됩니다";
                msg += "\r\n\r\n#b < 2개월 스페셜 아이템 리스트 >\r\n\r\n";
                    msg += 색 + "#i"+bosang1[0]+"# #z"+bosang1[0]+"#" +enter;
                msg += "\r\n\r\n#b < 1개월 스페셜 아이템 리스트 >\r\n\r\n";
                for (i = 0; i < bosang.length; i++) {
                    msg += 색 + "#i"+bosang[i]+"# #z"+bosang[i]+"#" +enter;
                }
                cm.sendYesNo(msg);
            } else {
                msg = "#fs11#" + 검은색 + "이미 #fc0xFFFF3366#스페셜 아이템" + 검은색 + " 을 지급받으셨습니다";
                cm.dispose();
                cm.sendOk(msg);
            }
            break;

            case 6:
            // 출석 체크
            // 날짜 불러오기
            var months = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
            today = new Date();
            month = months[today.getMonth()];
            day = today.getDate();

            today = month + day;

            count = cm.getClient().getKeyValue("1stday");

            if (count >= 14) {
                cm.sendOk("#fs11#" + 검은색 + "이미 모든 #fc0xFFFF3366#출석 체크" + 검은색 + " 를 하셨습니다\r\n\r\n현재 출석 일수 : " + count + "/14");
                cm.dispose();
            }

            if (cm.getClient().getKeyValue("1stdaylast") == today) {
                cm.sendOk("#fs11#" + 검은색 + "오늘은 이미 #fc0xFFFF3366#출석 체크" + 검은색 + " 를 하셨습니다\r\n\r\n현재 출석 일수 : " + count + "/14");
                cm.dispose();
            }

            msg = "#fs11#출석 체크를 하시겠어요?\r\n\r\n#r※ 오늘 출석체크의 보상은 아래와 같습니다.\r\n\r\n";
            for (i = 0; i < dayitem.length; i++) {
                msg += 색 + "#i"+dayitem[i][0]+"# #z"+ dayitem[i][0]+"# " + dayitem[i][1] + "개" +enter;
            }
            cm.sendYesNo(msg);

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
                cm.sendOk("#fs11#" + 색 + "이벤트 코인샵 에서 #i4310175# #z4310175#으로 다양한 아이템을 구매할 수 있어요!");
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
            if (cm.getInvSlots(3) < 1 || cm.getInvSlots(6) < 7) {
                cm.sendOk("#fs11#인벤토리 공간이 부족합니다.\r\n설치 탭에 1칸, 치장 탭에 7칸이 필요합니다.");
                cm.dispose();
                return;
            }
            
            msg = "#fs11#" + 획득 + enter + enter;
            msg += 색 + "#i"+bosang1[0]+"# #z"+bosang1[0]+"#" +enter;
            for (i = 0; i < bosang.length; i++) {
                msg += 색 + "#i"+bosang[i]+"# #z"+bosang[i]+"#" +enter;
                MakeItem(bosang[i]);
            }
            MakeItem1(bosang1[0], 1);
            cm.getClient().setKeyValue("1steventitem", "1");
            msg += 핑크색 + "\r\n위와 같은 아이템이 지급되었습니다";
            cm.sendOk(msg);
            cm.dispose();
            break;

            case 6:
            // 출석 체크
            // 날짜 불러오기
            var months = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
            today = new Date();
            month = months[today.getMonth()];
            day = today.getDate();

            today = month + day

            count = cm.getClient().getKeyValue("1stday");
            plustcount = parseInt(count) + 1;
            
            if (cm.getInvSlots(1) < 1 || cm.getInvSlots(2) < 1 || cm.getInvSlots(3) < 1 || cm.getInvSlots(4) < 1 || cm.getInvSlots(5) < 1) {
                cm.sendOkS("#fs11##fc0xFF6600CC#인벤토리를 탭별로 1칸이상 비워주세요", 2);
                cm.dispose();
                return;
            }

            cm.getClient().setKeyValue("1stday", plustcount);
            cm.getClient().setKeyValue("1stdaylast", today);
            msg = "#fs11#출석 체크를 완료했습니다\r\n\r\n#r※ 오늘 출석체크의 보상은 아래와 같습니다.\r\n\r\n";
            for (i = 0; i < dayitem.length; i++) {
                msg += 색 + "#i"+dayitem[i][0]+"# #z"+ dayitem[i][0]+"# " + dayitem[i][1] + "개" +enter;
                cm.gainItem(dayitem[i][0], dayitem[i][1]);
            }
            cm.sendOk(msg);
            cm.dispose();
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
    return cm.getClient().getKeyValue("1steventFirst");
}