별 = "#fUI/FarmUI.img/objectStatus/star/whole#";
별흰 = "#fUI/GuildMark.img/Mark/Pattern/00004001/10#"


function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
	if (cm.getPlayer().getLevel() < 10) {
	cm.sendOk("   #b10레벨 이상만 이용가능합니다.");
	cm.dispose();
	return;
	}
	var chat = "안녕하세요? #e진:眞#n의 #i5680157##z5680157#은 #b스페셜 라벨, 레드 라벨, 블랙 라벨, 마스터 라벨#k이 나오며 #i5069001##z5069001#을 이용해서 #r효과가 없는 스페셜 라벨끼리 또는 스페셜라벨과 레드 라벨을 조합#k하여 #e마스터 라벨을 한번 더 노릴 수 있는 아이템#n입니다.\r\n#r*블랙 라벨은 조합이 불가능 하기 때문에 꽝입니다.\r\n#r*검색캐시는 효과없음.#k\r\n"+별+" 현재 #b#e#h0##n#k님#n#k의 #b#i4430000##e홍보 포인트#n#k : #r"+cm.getHongboPoint()+" P#k\r\n"; 
	chat += "――――――――――――――――――――――――――――――――――――――――";
	chat += "#L0##e#d마스터라벨 세트 효과 및 각각 옵션을 알려주세요.#k#n#l\r\n\r\n";
	chat += "#L1##e#i5680157# #b#z5680157##k 1개 #d#e#r(2000 Point)#k#n#l\r\n";
	chat += "#L2##e#i5680157# #b#z5680157##k 5개 #d#e#r(10000 Point)#k#n#l\r\n";
	chat += "#L3##e#i5680157# #b#z5680157##k #r10+1#k개 #d#e#r(20000 Point)#k#n#l\r\n";
	chat += "#L4##e#i5069001# #b#z5069001##k 1개 #d#e#r(1500 Point)#k#n#l\r\n";
	chat += "#L5##e#i5069001# #b#z5069001##k 5개 #d#e#r(7500 Point)#k#n#l\r\n";
	chat += "#L6##e#i5069001# #b#z5069001##k #r10+1#k개 #d#e#r(15000 Point)#k#n#l\r\n";
	cm.sendSimple(chat);
	} else if (selection == 0) {
		cm.sendOk("#b* 스페셜 라벨 : 옵션 없음\r\n* 레드 라벨 : 랜덤스탯 + 10 / 공격력,마력 + 10\r\n* 블랙 라벨 : 랜덤스탯 + 20 / 공격력,마력 + 20\r\n* 마스터 라벨 : 랜덤스탯 + 40 / 공격력,마력 + 40#k\r\n#r#e\r\n<마스터라벨 세트효과>#n#k\r\n#d- 3개 : 올스탯 10%\r\n- 4개 : 올스탯 15%\r\n- 5개 : 올스탯 20%\r\n- 6개 : 올스탯 25% + 공격력,마력 10%#k\r\n");
		cm.dispose();
	} else if (selection == 1) {
         if (cm.getHongboPoint() >= 2000) {
		    if (cm.canHold(5680157)) {
			cm.gainHongboPoint(-2000);
			cm.gainItem(5680157, 1);
		       cm.sendOk("#b홍보 포인트#k 로 #i5680157# #r1 개#k 를 구입 하셨습니다.");
			cm.dispose();
		    } else {
		        cm.sendOk("#r캐시 칸에 빈 공간이 없습니다.#k");
		        cm.dispose();
		    }
		} else {
		    cm.sendOk("   #b홍보 포인트#k 가 부족합니다.");
		    cm.dispose();
			}
		} else if (selection == 2) {
         if (cm.getHongboPoint() >= 10000) {
		    if (cm.canHold(5680157)) {
			cm.gainHongboPoint(-10000);
			cm.gainItem(5680157, 5);
		       cm.sendOk("#b홍보 포인트#k 로 #i5680157# #r5 개#k 를 구입 하셨습니다.");
			cm.dispose();
		    } else {
		        cm.sendOk("#r캐시 칸에 빈 공간이 없습니다.#k");
		        cm.dispose();
		    }
		} else {
		    cm.sendOk("   #b홍보 포인트#k 가 부족합니다.");
		    cm.dispose();
			}
		} else if (selection == 3) {
         if (cm.getHongboPoint() >= 20000) {
		    if (cm.canHold(5680157)) {
			cm.gainHongboPoint(-20000);
			cm.gainItem(5680157, 11);
		       cm.sendOk("#b홍보 포인트#k 로 #i5680157# #r11 개#k 를 구입 하셨습니다.");
			cm.dispose();
		    } else {
		        cm.sendOk("#r캐시 칸에 빈 공간이 없습니다.#k");
		        cm.dispose();
		    }
		} else {
		    cm.sendOk("   #b홍보 포인트#k 가 부족합니다.");
		    cm.dispose();
			}
		} else if (selection == 4) {
         if (cm.getHongboPoint() >= 1500) {
		    if (cm.canHold(5069001)) {
			cm.gainHongboPoint(-1500);
			cm.gainItem(5069001, 1);
		       cm.sendOk("#b홍보 포인트#k 로 #i5069001# #r1 개#k 를 구입 하셨습니다.");
			cm.dispose();
		    } else {
		        cm.sendOk("#r캐시 칸에 빈 공간이 없습니다.#k");
		        cm.dispose();
		    }
		} else {
		    cm.sendOk("   #b홍보 포인트#k 가 부족합니다.");
		    cm.dispose();
			}
		} else if (selection == 5) {
         if (cm.getHongboPoint() >= 7500) {
		    if (cm.canHold(5069001)) {
			cm.gainHongboPoint(-7500);
			cm.gainItem(5069001, 5);
		       cm.sendOk("#b홍보 포인트#k 로 #i5069001# #r5 개#k 를 구입 하셨습니다.");
			cm.dispose();
		    } else {
		        cm.sendOk("#r캐시 칸에 빈 공간이 없습니다.#k");
		        cm.dispose();
		    }
		} else {
		    cm.sendOk("   #b홍보 포인트#k 가 부족합니다.");
		    cm.dispose();
			}
		} else if (selection == 6) {
         if (cm.getHongboPoint() >= 15000) {
		    if (cm.canHold(5069001)) {
			cm.gainHongboPoint(-15000);
			cm.gainItem(5069001, 11);
		       cm.sendOk("#b홍보 포인트#k 로 #i5069001# #r11 개#k 를 구입 하셨습니다.");
			cm.dispose();
		    } else {
		        cm.sendOk("#r캐시 칸에 빈 공간이 없습니다.#k");
		        cm.dispose();
		    }
		} else {
		    cm.sendOk("   #b홍보 포인트#k 가 부족합니다.");
		    cm.dispose();
			}
		} 
	}
}