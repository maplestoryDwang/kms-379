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

var bosang = [
[2634770, 1],
[2633202, 1],
[2633202, 1]
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
            var msg = "　　#i1142275# #fs14##e[로얄메이플] - 가정의달 이벤트#n#fs11# #i1142275#\r\n#fs11##Cblue#              항상 로얄 메이플을 이용해주셔서 감사합니다#k\r\n";
                    msg += 핑크색 + "                               #L100#이벤트안내#fc0xFF000000##l\r\n\r\n";
                    msg += "                      이벤트 기간 : #b10.03 ~ 10.24\r\n";
                    msg += "\r\n#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
                    msg += "#L1##fc0xFF6600CC#이벤트 펫#fc0xFF000000# 지급받기#l\r\n";
                    msg += "\r\n#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#";
            cm.sendSimple(msg);
     } else if(status == 1) {
        seld = sel;
        switch(sel) {
            case 100:
                msg = "#fn나눔고딕 Extrabold##d홈페이지를 참고해주세요";
                cm.dispose();
                cm.sendOk(msg);
            break;
            
            case 1:
                msg = "#fs11#" + 핑크색 + enter + "이벤트 펫을 받아보시겠어요?" + 검은색 +enter+enter;
                msg += "#r#fs14#<주의사항 및 안내>#fs11#" +enter+enter;
                msg += 검은색 + "1. 이벤트 펫은 계정당 1회만 지급가능합니다" +enter;
                msg += 검은색 + "2. 해당 펫은 상자로 지급되며 2가지 펫중 랜덤으로 지급됩니다" +enter;
                msg += 검은색 + "3. 해당 상자는 창고이동 및 교환이 가능하니다" +enter;

                msg += 검은색 + enter + "위 내용을 충분히 읽으셨다면 아래에 #r'동의합니다'" + 검은색 + " 를 입력해주세요" +enter+" ";
                cm.sendGetText(msg);
            break;
            
        }
        
    } else if(status == 2) {
        switch(seld) {
            case 100:
                cm.sendOk("#fs11#" + 색 + "이벤트 상점에서 #i4310185# #z4310185#으로 다양한 아이템을 구매할 수 있어요!");
                cm.dispose();
            break;
            
            case 1:
            if (cm.getText() != "동의합니다") {
                cm.dispose();
                cm.sendOk("#fs11#동의 하지않는다면 지급이힘들어요\r\n#b동의#k 한다면 다시 '#r#e동의합니다#k#n' 를 입력해주세요");
                return;
            }
            if (getClear() != null) {
                cm.dispose();
                cm.sendOk("#fs11#이미 이벤트 펫을 수령하셨어요\r\n#b인벤토리를#k 확인해보세요");
                return;
            }
            if (cm.getInvSlots(1) < 3 || cm.getInvSlots(2) < 3 || cm.getInvSlots(3) < 3 || cm.getInvSlots(4) < 3 || cm.getInvSlots(5) < 3) {
                cm.sendOkS("#fs11##fc0xFF6600CC#인벤토리를 탭별로 3칸이상 비워주세요", 2);
                cm.dispose();
                return;
            }



            msg = "#fs11#" + 획득 + enter;

            i = Packages.objects.utils.Randomizer.rand(0, 2);
            msg += 색 + "#i"+bosang[i][0]+"# #z"+bosang[i][0]+"##r "+ bosang[i][1] +"개#k" +enter;
            cm.gainItem(bosang[i][0], bosang[i][1]);

            msg += 핑크색 + "\r\n위와 같은 아이템이 지급되었습니다";
            cm.sendOk(msg);
            cm.getClient().setKeyValue("10dnjfevent", "1");
            cm.dispose();
            break;            
        }

    }
}

function getClear() {
    return cm.getClient().getKeyValue("10dnjfevent");
}