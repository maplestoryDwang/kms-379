importPackage(java.lang);
importPackage(Packages.handling.world);
importPackage(Packages.tools.packet);
importPackage(Packages.objects.item);

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
끝 = "#fc0xFF000000#"

var status = -1;
var enter = "\r\n";
var boxmsg = enter;
var pluscount = 1;
var items = [
  //[[아이템코드, 갯수], 확률]
  [[1672078,1], 0.8], // Limited Heart I 0.8%
  [[1672079,1], 0.8], // Limited Heart II 0.8%

  [[0, 0], 0], // 옌터

  [[2439962,1], 1], // 초케인 무기
  [[2439961,1], 1], // 초케인 장비

  [[0, 0], 0], // 옌터

  [[2046076,1], 4], // 후한공
  [[2046150,1], 4], // 후두공
  [[2046077,1], 4], // 후한마
  [[2046251,1], 4], // 후방줌

  [[0, 0], 0], // 옌터

  [[2439932,1], 5], // 루나 쁘띠 5
  [[2439944,1], 5], // 루나 쁘띠 4
  [[2439943,1], 5], // 루나 쁘띠 3
  [[2439942,1], 5], // 루나 쁘띠 2

  [[0, 0], 0], // 옌터

  [[2430045,1], 1], // 메강 18
  [[2430044,1], 5], // 메강 17

  [[0, 0], 0], // 옌터

  [[2049377,1], 5], // 스타포스 22
  [[2049376,1], 8], // 스타포스 20
  [[2049372,1], 10], // 스타포스 15


  [[0, 0], 0], // 옌터

  [[2633336,1], 15.7], // 선택 어센틱심볼 5개
  [[2437760,1], 15.7], // 선택 아케인심볼 5개
]

var amount = 1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }
    if (status == 0) {
        if (cm.getClient().getKeyValue("juneticket") == null) {
            cm.getClient().setKeyValue("juneticket", 0);
        }

        reload();

        var chat = "#fs11#"
        chat += "#h0#님 안녕하세요? #b#e[강림월드]#k#n 의 돌림판 입니다!" + enter
        chat += "Limited Ticket June 으로 뽑기에 도전해보세요!" + enter + enter
        chat += 별 + 색 + " 꼭 좋은 아이템을 획득하세요 ! " + 별 + enter + enter + 끝
        chat += "#L1#" + 색 + "[Limited Ticket June 돌림판]" + 끝 + " 뽑기#l"
        chat += "\r\n"
        chat += "#L99#" + 색 + "[Limited Ticket June 돌림판]" + 끝 + " 리스트#l"
        cm.sendSimple(chat);
    } else if (status == 1) {
        switch (selection) {
            case 1:
                if (!cm.haveItem(2439991, 1)) {
                    cm.sendOkS("#i2439991# #z2439991# 아이템이 없는것같은데?..", 700);
                    cm.dispose();
                    return;
                }
                if (cm.getInvSlots(1) < 1 || cm.getInvSlots(2) < 1 || cm.getInvSlots(3) < 1 || cm.getInvSlots(4) < 1 || cm.getInvSlots(5) < 1) {
                    cm.sendOkS("#fs11##fc0xFF6600CC#인벤토리를 탭별로 1칸씩은 비워주세요", 2);
                    cm.dispose();
                    return;
                }
                
                cm.gainItem(2439991, -1);
                //AdvancedUnboxing();
                // M.V.P 뽑기
                for(var x = 0;x< amount;x++) {
                    var percentage = 0;
                    var chance = Math.random() * 100;
                    for(var i = 0;i<items.length;i++) {
                        percentage += items[i][1];
                        if(percentage > chance) {
                            boxmsg = 색 + "#fs11#" + 색 + "#i" + items[i][0][0]  + "##z" + items[i][0][0] + "# #r" + items[i][0][1] + "개 #b가 당첨되셨습니다#k#n" + enter + enter;
                            cm.gainItem(items[i][0][0], items[i][0][1]);
                            if (items[i][0][0] == 1672078 || items[i][0][0] == 1672079) {
                                var ii = Packages.objects.item.MapleItemInformationProvider.getInstance();
                                var it = ii.getEquipById(items[i][0][0]);
                                var Center = Packages.network.center.Center;
                                Center.Broadcast.broadcastGachaponMessage(cm.getPlayer().getName() + " 님이 Limited Ticket June 에서 {" + cm.getItemName(items[i][0][0]) + "} 아이템을 획득하셨습니다 !", 5068300, it);
                                cm.getClient().setKeyValue(items[i][0][0], "1");
                            }
                            currentcount = cm.getClient().getKeyValue("juneticket");
                            cm.getClient().setKeyValue("juneticket", Integer.parseInt(currentcount) + pluscount);
                            break;
                        }
                    }
                }
                boxmsg += 핑크색 + 별 + "마음에 드셨길 바래요 한번더 뽑으시겠어요?";
                retry = "MVP";
                cm.sendYesNo(boxmsg);

                break;
                    
            case 99:
                    var msg = 별파 + "#fs11##fc0xFF000000# 뽑을 수 있는 품목은 아래와 같아! #fs11#"+ 별파 + enter + enter;
                    for (i = 0; i < items.length; i++) {
                        if (items[i][1] == 0) {
                            msg += enter;
                        } else {
                            msg += 색 + "#i"+items[i][0][0]+"##z"+items[i][0][0]+"##r "+ items[i][0][1] +"개 #b"+ items[i][1]+ "%#k" +enter;
                        }
                    }
                    cm.sendOk(msg);
                    cm.dispose();
        }
    } else if (status == 2) {

        reload();

        if (retry == "NORMAL") {
            status = 0;
            action(1, 0, 0);
        }
        if (retry == "MVP") {
            status = 0;
            action(1, 0, 1);
        }
    }
}

function reload() {
    check1 = cm.getClient().getKeyValue("1672078");
    check2 = cm.getClient().getKeyValue("1672079");
    if (check1 != null) {
        items = [
          //[[아이템코드, 갯수], 확률]
          [[1672079,1], 1.6], // Limited Heart II
          
          [[0, 0], 0], // 옌터

          [[2439962,1], 1], // 초케인 무기
          [[2439961,1], 1], // 초케인 장비

          [[0, 0], 0], // 옌터

          [[2046076,1], 4], // 후한공
          [[2046150,1], 4], // 후두공
          [[2046077,1], 4], // 후한마
          [[2046251,1], 4], // 후방줌

          [[0, 0], 0], // 옌터

          [[2439932,1], 5], // 루나 쁘띠 5
          [[2439944,1], 5], // 루나 쁘띠 4
          [[2439943,1], 5], // 루나 쁘띠 3
          [[2439942,1], 5], // 루나 쁘띠 2

          [[0, 0], 0], // 옌터

          [[2430045,1], 1], // 메강 18
          [[2430044,1], 5], // 메강 17

          [[0, 0], 0], // 옌터

          [[2049377,1], 5], // 스타포스 22
          [[2049376,1], 8], // 스타포스 20
          [[2049372,1], 10], // 스타포스 15


          [[0, 0], 0], // 옌터

          [[2633336,1], 15.7], // 선택 어센틱심볼 5개
          [[2437760,1], 15.7], // 선택 아케인심볼 5개
        ]
    }
    if (check2 != null) {
        items = [
          //[[아이템코드, 갯수], 확률]
          [[1672078,1], 1.6], // Limited Heart I

          [[0, 0], 0], // 옌터

          [[2439962,1], 1], // 초케인 무기
          [[2439961,1], 1], // 초케인 장비

          [[0, 0], 0], // 옌터

          [[2046076,1], 4], // 후한공
          [[2046150,1], 4], // 후두공
          [[2046077,1], 4], // 후한마
          [[2046251,1], 4], // 후방줌

          [[0, 0], 0], // 옌터

          [[2439932,1], 5], // 루나 쁘띠 5
          [[2439944,1], 5], // 루나 쁘띠 4
          [[2439943,1], 5], // 루나 쁘띠 3
          [[2439942,1], 5], // 루나 쁘띠 2

          [[0, 0], 0], // 옌터

          [[2430045,1], 1], // 메강 18
          [[2430044,1], 5], // 메강 17

          [[0, 0], 0], // 옌터

          [[2049377,1], 5], // 스타포스 22
          [[2049376,1], 8], // 스타포스 20
          [[2049372,1], 10], // 스타포스 15


          [[0, 0], 0], // 옌터

          [[2633336,1], 15.7], // 선택 어센틱심볼 5개
          [[2437760,1], 15.7], // 선택 아케인심볼 5개
        ]
    }
    if (check1 != null && check2 != null) {
        cm.getClient().removeKeyValue("1672078");
        cm.getClient().removeKeyValue("1672079");
        items = [
          //[[아이템코드, 갯수], 확률]
          [[1672078,1], 0.8], // Limited Heart I 0.8%
          [[1672079,1], 0.8], // Limited Heart II 0.8%

          [[0, 0], 0], // 옌터

          [[2439962,1], 1], // 초케인 무기
          [[2439961,1], 1], // 초케인 장비

          [[0, 0], 0], // 옌터

          [[2046076,1], 4], // 후한공
          [[2046150,1], 4], // 후두공
          [[2046077,1], 4], // 후한마
          [[2046251,1], 4], // 후방줌

          [[0, 0], 0], // 옌터

          [[2439932,1], 5], // 루나 쁘띠 5
          [[2439944,1], 5], // 루나 쁘띠 4
          [[2439943,1], 5], // 루나 쁘띠 3
          [[2439942,1], 5], // 루나 쁘띠 2

          [[0, 0], 0], // 옌터

          [[2430045,1], 1], // 메강 18
          [[2430044,1], 5], // 메강 17

          [[0, 0], 0], // 옌터

          [[2049377,1], 5], // 스타포스 22
          [[2049376,1], 8], // 스타포스 20
          [[2049372,1], 10], // 스타포스 15


          [[0, 0], 0], // 옌터

          [[2633336,1], 15.7], // 선택 어센틱심볼 5개
          [[2437760,1], 15.7], // 선택 아케인심볼 5개
        ]
    }
}