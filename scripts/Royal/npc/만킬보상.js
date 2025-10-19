/*일일 퀘스트
10만 마리 잡기	*/

 importPackage(java.lang);

 var Time = new Date();
 var Year = Time.getFullYear() + "";
 var Month = Time.getMonth() + 1 + "";
 var Date = Time.getDate() + "";
 if (Month < 10) {
     Month = "0" + Month;
 }
 if (Date < 10) {
     Date = "0" + Date;
 }
 var Today = parseInt(Year + Month + Date);
 
 var quest = [
     ["", [0, 0], [0, 0], [0, 0], [0, 0]],
 ];
 
 var reward = [[0, 1],[2430658, 20],];
 var reward2 = [[0, 0],[2430658, 20],];
 var choice = 0;
 
 var status = -1;
 
 function start() {
	status = -1;
     action(1, 0, 0);
 }
 
 function action(mode, type, selection) {
     if (mode != 1) {
         cm.dispose();
         return;
     }
     if (mode == 1) {
         status++;
     }
     if (status == 0) {
         var say = "  #fs14##i4001779##e[메이플 로얄]10,000킬 보상 #i4001779#\r\n#fs11##Cblue#          메이플 로얄에서 항상 사냥해주셔서 감사합니다#k\r\n";
         say += "#fs12##L0##e#b레벨범위 몬스터 #r10,000마리#l#l\r\n\r\n#fs11##Cgray#메이플 로얄 유저들은 1만마리 금방 잡는다는데!!! 화이팅!!.#k";
         for (var i = 1; i < quest.length; i++) {
             say += "#L" + i + "##e#b[" + quest[i][0][0] + "]#n#k\r\n    #e#dㄴ#o" + quest[i][1][0] + "# " + quest[i][1][1] + "마리\r\n   ㄴ#o" + quest[i][2][0] + "# " + quest[i][2][1] + "마리#l\r\n\r\n";
         }
         cm.sendSimple(say);
 
     } else if (status == 1) {
         choice = selection;
         if (selection != 0) {
             if (cm.getPlayer().getKeyValue(Today, "Quest_" + quest[selection][1][0]) == -1) {
                 cm.getPlayer().setKeyValue(Today, "Quest_" + quest[selection][1][0], "0");
             }
             if (cm.getPlayer().getKeyValue(Today, "Quest_" + quest[selection][2][0]) == -1) {
                 cm.getPlayer().setKeyValue(Today, "Quest_" + quest[selection][2][0], "0");
             }
             if (cm.getPlayer().getKeyValue(Today, "Quest_" + quest[selection][0][1]) == -1) {
                 cm.getPlayer().setKeyValue(Today, "Quest_" + quest[selection][0][1], "0");
             }
             if (cm.getPlayer().getKeyValue(Today, "Quest_" + quest[selection][0][1]) == 1) {
                 cm.sendOk("이 퀘스트는 이미 완료된 퀘스트 입니다.");
                 cm.dispose();
                 return;
             } else if (cm.getPlayer().getKeyValue(Today, "Quest_" + quest[selection][1][0]) >= quest[selection][1][1] &&
                 cm.getPlayer().getKeyValue(Today, "Quest_" + quest[selection][2][0]) >= quest[selection][2][1]) {
                 if (selection >= 7 && selection <= 9) {
                     var say = "<보상 목록>\r\n";
                     say += reward2[0][0] + "메소\r\n";
                     for (var i = 1; i < reward2.length; i++) {
                         say += "#i" + reward2[i][0] + "##z" + reward2[i][0] + "# " + reward2[i][1] + "개\r\n";
                     }
                     cm.sendYesNo("해당 퀘스트를 완료하기 위한 조건이 충족되었습니다. 퀘스트를 완료하시겠습니까?\r\n\r\n" + say);
                 } else {
                     var say = "<보상 목록>\r\n";
                     for (var i = 1; i < reward.length; i++) {
                         say += "#i" + reward[i][0] + "##z" + reward[i][0] + "# " + reward[i][1] + "개\r\n";
                     }
                     cm.sendYesNo("해당 퀘스트를 완료하기 위한 조건이 충족되었습니다. 퀘스트를 완료하시겠습니까?\r\n\r\n" + say);
                 }
             } else {
                 var say = "#e#b[" + quest[selection][0][0] + " 퀘스트]#n#k\r\n" +
                     "#e#dㄴ#o" + quest[selection][1][0] + "# " + quest[selection][1][1] + "마리\r\n" +
                     "ㄴ#o" + quest[selection][2][0] + "# " + quest[selection][2][1] + "마리\r\n\r\n";
                 say += "#e#b[진행상황]#n#k\r\n" +
                     "#e#dㄴ#o" + quest[selection][1][0] + "# : #b" + cm.getPlayer().getKeyValue(Today, "Quest_" + quest[selection][1][0]) + "마리#d/#r" + quest[selection][1][1] + "마리\r\n" +
                     "#e#dㄴ#o" + quest[selection][2][0] + "# : #b" + cm.getPlayer().getKeyValue(Today, "Quest_" + quest[selection][2][0]) + "마리#d/#r" + quest[selection][2][1] + "마리\r\n\r\n";
                 say += "#e#b몬스터를 다 잡은 후 퀘스트 완료가 가능합니다.";
                 cm.sendOk(say);
                 cm.dispose();
                 return;
             }
         } else {
             if (cm.getPlayer().getKeyValue(2021, "mobcount") == -1) {
                 cm.getPlayer().setKeyValue(2021, "mobcount", "0");
             }
             if (cm.getPlayer().getKeyValue(2021, "mobcount") >= 10000) {
                 var say = "#e#b[레벨 범위 몬스터 10000마리 잡기]#n#k\r\n\r\n";
                 say += "#e#d[진행상황]#n#k\r\n" +
                     "#e#b" + cm.getPlayer().getKeyValue("2021", "mobcount") + "마리#d/#r10000마리\r\n\r\n";
                 say += "#n#k레벨 범위 몬스터 10000마리를 잡은 후 퀘스트 완료가 가능합니다.";
                 cm.sendYesNo("해당 퀘스트를 완료하기 위한 조건이 충족되었습니다. 퀘스트를 완료하시겠습니까?\r\n\r\n" + say);
             } else {
                 var say = "#e#b[레벨 범위 몬스터 10000마리 잡기]#n#k\r\n\r\n";
                 say += "#e#d[진행상황]#n#k\r\n" +
                     "#e#b" + cm.getPlayer().getKeyValue(2021, "mobcount") + "마리#d/#r10000마리\r\n\r\n";
                 say += "#n#k레벨 범위 몬스터 10000마리를 잡은 후 퀘스트 완료가 가능합니다.";
                 cm.sendOk(say);
                 cm.dispose();
                 return;
             }
         }
 
     } else if (status == 2) {
         if (choice != 0) {
 
             if (choice >= 7 && choice <= 9) {
                 if (cm.getInvSlots(1) >= 10) {
                     cm.getPlayer().setKeyValue(Today, "Quest_" + quest[choice][0][1], "1");
                     cm.gainItem(reward2[1][0], reward2[1][1]);
                     cm.gainItem(reward2[2][0], reward2[2][1]);
                     cm.gainItem(reward2[3][0], reward2[3][1]);
                     cm.gainItem(reward2[4][0], reward2[4][1]);
                     cm.gainItem(reward2[5][0], reward2[5][1]);
                     cm.gainItem(reward2[6][0], reward2[6][1]);
                     cm.gainItem(reward2[7][0], reward2[7][1]);
                     cm.gainItem(reward2[8][0], reward2[8][1]);
                     cm.gainItem(reward2[9][0], reward2[9][1]);
                     cm.gainItem(reward2[10][0], reward2[10][1]);
                     cm.sendOk("퀘스트가 완료되어 보상이 지급되었습니다. 인벤토리를 확인해 주세요.");
                     cm.dispose();
                     return;
                 } else {
                     cm.sendOk("인벤토리 슬롯이 부족하거나 꽉 찬 것은 아닌지 확인해 주세요.");
                     cm.dispose();
                     return;
                 }
             } else {
                 if (cm.getInvSlots(2) >= 3 && cm.getInvSlots(4) >= 1) {
                     cm.getPlayer().setKeyValue(Today, "Quest_" + quest[choice][0][1], "1");
                     cm.gainItem(reward[1][0], reward[1][1]);
                     cm.gainItem(reward[2][0], reward[2][1]);
                     cm.gainItem(reward[3][0], reward[3][1]);
                     cm.gainItem(reward[4][0], reward[4][1]);
                     cm.gainItem(reward[5][0], reward[5][1]);
                     cm.gainItem(reward[6][0], reward[6][1]);
                     cm.sendOk("퀘스트가 완료되어 보상이 지급되었습니다. 인벤토리를 확인해 주세요.");
                     cm.dispose();
                     return;
                 } else {
                     cm.sendOk("인벤토리 슬롯이 부족하거나 꽉 찬 것은 아닌지 확인해 주세요.");
                     cm.dispose();
                     return;
                 }
             }
         } else if (choice == 0) {
             if (cm.getInvSlots(2) >= 3) {
                 cm.getPlayer().setKeyValue(2021, "mobcount", "0");
                 cm.gainMeso(reward[0][0]);
                 cm.gainItem(reward[1][0], reward[1][1]);
                 cm.sendOk("퀘스트가 완료되어 보상이 지급되었습니다. 인벤토리를 확인해 주세요.");
                 cm.dispose();
                 return;
             } else {
                 cm.sendOk("인벤토리 슬롯이 부족하거나 꽉 찬 것은 아닌지 확인해 주세요.");
                 cm.dispose();
                 return;
             }
         } else {
             cm.dispose();
             return;
         }
     } else {
         cm.dispose();
         return;
     }
 }