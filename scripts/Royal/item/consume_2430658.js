importPackage(Packages.client.inventory);


function start() {
	status = -1;
	action(1, 0, 0);
}

var enter = "\r\n";

var item1 = [2430658, "#z2430658#", "1"];
var ac1 = [4310198, "[E] 설날 코인", "5"];
var ac2 = [4310237, "[E] 사냥 코인", "1"];
var ac3 = [1712003, "아케인 심볼 : 레헬른", "5"];
var ac4 = [1712004, "아케인 심볼 : 아르카나", "5"];
var ac5 = [1712005, "아케인 심볼 : 모라스", "5"];
var ac6 = [1712006, "아케인 심볼 : 에스페라", "5"];
var ac7 = [1712001, "아케인 심볼 : 소멸의 여로", "1"];
var ac8 = [1712002, "아케인 심볼 : 츄츄 아일랜드", "1"];
var ac9 = [1712003, "아케인 심볼 : 레헬른", "1"];
var ac10 = [1712004, "아케인 심볼 : 아르카나", "1"];
var ac11 = [1712005, "아케인 심볼 : 모라스", "1"];
var ac12 = [1712006, "아케인 심볼 : 에스페라", "1"];
var ac13 = [1712001, "아케인 심볼 : 소멸의 여로", "1"];
var ac14 = [1712002, "아케인 심볼 : 츄츄 아일랜드", "1"];
var ac15 = [1712003, "아케인 심볼 : 레헬른", "1"];
var ac16 = [1712004, "아케인 심볼 : 아르카나", "1"];
var ac17 = [1712005, "아케인 심볼 : 모라스", "1"];
var ac18 = [1712006, "아케인 심볼 : 에스페라", "1"];
var 별 = "#fUI/FarmUI.img/objectStatus/star/whole#";

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
			var chat = "#fn나눔고딕##fc0xFF7401DF#< 교환시스템 :: 이벤트 교환 >#k" + enter + "" + enter;
			chat += "#r#e"+별+"인벤토리 공간을 꼭 확보해주신후 사용해주시길 바랍니다"+ enter + "" ;
			chat += ""+ enter + "#L1##i" + ac1[0] + "# " + ac1[1] + " 교환" 
				+ enter + "#L2##i" + ac2[0] + "# " + ac2[1] + " 교환"
				//+ enter + "#L3##i" + ac3[0] + "# " + ac3[1] + " 교환"
				//+ enter + "#L4##i" + ac4[0] + "# " + ac4[1] + " 교환"
				//+ enter + "#L5##i" + ac5[0] + "# " + ac5[1] + " 교환"
				//+ enter + "#L6##i" + ac6[0] + "# " + ac6[1] + " 교환"
				//+ enter + "#L7##i" + ac2[0] + "# " + ac2[1] + " 교환"
			//+ enter + "#L4##i2437529#강화석 교환"
			cm.sendOkS(chat, 0x00);
		} else if (status == 1) {
			select = selection;
			if (select == 1) {
				var suk1 = Math.floor((cm.itemQuantity(2430658) / 1));
				stigmacoin = Math.min(suk1);
				cm.sendGetNumber("\r\n#fn나눔고딕##i" + item1[0] + "# " + item1[1] + " #fc0xFF7401DF#" + item1[2] + "#k 개를 주시면"
					+ enter + "#i" + ac1[0] + "# " + ac1[1] + " #fc0xFF7401DF#" + ac1[2] + "#k 개를 교환해드립니다."
					+ enter + "#Cgray#(현재 교환 가능한 " + item1[1] + " 갯수 : " + stigmacoin + "개)", 1, 1, 1000);
			}
			if (select == 2) {
				var suk1 = Math.floor((cm.itemQuantity(2430658) / 1));
				var stigmacoin = 0;
				stigmacoin = Math.min(suk1);
				cm.sendGetNumber("\r\n#fn나눔고딕##i" + item1[0] + "# " + item1[1] + " #fc0xFF7401DF#" + item1[2] + "#k 개를  주시면"
					+ enter + "#i" + ac2[0] + "# " + ac2[1] + " #fc0xFF7401DF#" + ac2[2] + "#k 개를 교환해드립니다."
					+ enter + "#Cgray#(현재 교환 가능한 " + item1[1] + " 갯수 : " + stigmacoin + "개)", 1, 1, 1000);
			}
			if (select == 3) {
				var suk1 = Math.floor((cm.itemQuantity(2430658) / 1));
				var stigmacoin = 0;
				stigmacoin = Math.min(suk1);
				cm.sendGetNumber("\r\n#fn나눔고딕##i" + item1[0] + "# " + item1[1] + " #fc0xFF7401DF#" + item1[2] + "#k 개를  주시면"
					+ enter + "#i" + ac3[0] + "# " + ac3[1] + " #fc0xFF7401DF#" + ac3[2] + "#k 개를 교환해드립니다."
					+ enter + "#Cgray#(현재 교환 가능한 " + item1[1] + " 갯수 : " + stigmacoin + "개)", 1, 1, 1000);
			}
			if (select == 4) {
				var suk1 = Math.floor((cm.itemQuantity(2430658) / 1));
				var stigmacoin = 0;
				stigmacoin = Math.min(suk1);
				cm.sendGetNumber("\r\n#fn나눔고딕##i" + item1[0] + "# " + item1[1] + " #fc0xFF7401DF#" + item1[2] + "#k 개를  주시면"
					+ enter + "#i" + ac4[0] + "# " + ac4[1] + " #fc0xFF7401DF#" + ac4[2] + "#k 개를 교환해드립니다."
					+ enter + "#Cgray#(현재 교환 가능한 " + item1[1] + " 갯수 : " + stigmacoin + "개)", 1, 1, 1000);
			}
			if (select == 5) {
				var suk1 = Math.floor((cm.itemQuantity(2430658) / 1));
				var stigmacoin = 0;
				stigmacoin = Math.min(suk1);
				cm.sendGetNumber("\r\n#fn나눔고딕##i" + item1[0] + "# " + item1[1] + " #fc0xFF7401DF#" + item1[2] + "#k 개를  주시면"
					+ enter + "#i" + ac5[0] + "# " + ac5[1] + " #fc0xFF7401DF#" + ac5[2] + "#k 개를 교환해드립니다."
					+ enter + "#Cgray#(현재 교환 가능한 " + item1[1] + " 갯수 : " + stigmacoin + "개)", 1, 1, 1000);
			}
             if (select == 6) {
				var suk1 = Math.floor((cm.itemQuantity(2430658) / 1));
				var stigmacoin = 0;
				stigmacoin = Math.min(suk1);
				cm.sendGetNumber("\r\n#fn나눔고딕##i" + item1[0] + "# " + item1[1] + " #fc0xFF7401DF#" + item1[2] + "#k 개를  주시면"
					+ enter + "#i" + ac6[0] + "# " + ac6[1] + " #fc0xFF7401DF#" + ac6[2] + "#k 개를 교환해드립니다."
					+ enter + "#Cgray#(현재 교환 가능한 " + item1[1] + " 갯수 : " + stigmacoin + "개)", 1, 1, 1000);
			}
                if (select == 7) {
				var suk1 = Math.floor((cm.itemQuantity(2046857) / 15));
				var stigmacoin = 0;
				stigmacoin = Math.min(suk1);
				cm.sendGetNumber("\r\n#fn나눔고딕##i" + item8[0] + "# " + item8[1] + " #fc0xFF7401DF#" + item8[2] + "#k 개를  주시면"
					+ enter + "#i" + ac2[0] + "# " + ac2[1] + " #fc0xFF7401DF#" + ac2[2] + "#k 개를 교환해드립니다."
					+ enter + "#Cgray#(현재 교환 가능한 " + ac2[1] + " 갯수 : " + stigmacoin + "개)", 1, 1, 1000);
			}
		} else if (status == 2) {
			var cost = selection;
			if (cost > 1000) {
				cm.dispose();
				return;
			}
			/*if (select == 0) {
				if (cm.haveItem(item[0], item[2] * cost) && cm.haveItem(item1[0], item1[2] * cost) &&cm.canHold(1712001)) {
					if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.ETC).getNumFreeSlot() >= cost) {
						var stig = cm.itemQuantity(4310199) + cost;
						cm.gainItem(item[0], -item[2] * cost);
						cm.gainItem(item1[0], -item1[2] * cost);
						cm.gainItem(stigma[0], cost);
						cm.sendOk("#fn나눔고딕#교환완료\r\n#Cgray#(현재 가지고계신 " + stigma[1] + "의 갯수 : " + stig + "개");
						cm.dispose();
					} else {
						cm.sendOk("#fn나눔고딕##r장비창을 확인해주세요");
						cm.dispose();
					}
				} else {
					cm.sendOk("#fn나눔고딕##r영혼석이 부족합니다. \r\n 또는 인벤토리가 꽉 차있는지 확인 해주시길 바랍니다");
					cm.dispose();
				}
			}*/
			if (select == 1) {
				if (cm.haveItem(item1[0], item1[2] * cost) && cm.canHold(4310198, cost*5)) { // 설날 코인 *5
					if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.USE).getNumFreeSlot() >= 1) {
						cm.gainItem(item1[0], -item1[2] * cost);
						cm.gainItem(ac1[0], cost*5);
						cm.sendOk("#fn나눔고딕#교환완료\r\n");
						cm.dispose();
					} else {
						cm.sendOk("#fn나눔고딕##r장비창을 확인해주세요");
						cm.dispose();
					}
				} else {
					cm.sendOk("#fn나눔고딕##r#i2430658##z2430658#이 부족합니다. \r\n 또는 인벤토리가 꽉 차있는지 확인 해주시길 바랍니다");
					cm.dispose();
				}
			}
			if (select == 2) {
				if (cm.haveItem(item1[0], item1[2] * cost) && cm.canHold(4310237, cost)) {
					if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.USE).getNumFreeSlot() >= 1) {
						var pan = cm.itemQuantity(4310218) + cost;
						cm.gainItem(item1[0], -item1[2] * cost);
						cm.gainItem(ac2[0], cost*5);
						cm.sendOk("#fn나눔고딕#교환완료");
						cm.dispose();
					} else {
						cm.sendOk("#fn나눔고딕##r장비창을 확인해주세요");
						cm.dispose();
					}
				} else {
					cm.sendOk("#fn나눔고딕##r#i2430658##z2430658#이 부족합니다. \r\n 또는 인벤토리가 꽉 차있는지 확인 해주시길 바랍니다");
					cm.dispose();
				}
			}
			if (select == 3) {
				if (cm.haveItem(item1[0], item1[2] * cost) && cm.canHold(1712001, cost*5)) {
					if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.USE).getNumFreeSlot() >= 1) {
						var pan = cm.itemQuantity(4310218) + cost;
						cm.gainItem(item1[0], -item1[2] * cost);
						cm.gainItem(ac3[0], cost*5);
						cm.sendOk("#fn나눔고딕#교환완료");
						cm.dispose();
					} else {
						cm.sendOk("#fn나눔고딕##r장비창을 확인해주세요");
						cm.dispose();
					}
				} else {
					cm.sendOk("#fn나눔고딕##r#i2430658##z2430658#이 부족합니다. \r\n 또는 인벤토리가 꽉 차있는지 확인 해주시길 바랍니다");
					cm.dispose();
				}
			}
             if (select == 4) {
				if (cm.haveItem(item1[0], item1[2] * cost) && cm.canHold(1712001, cost*5)) {
					if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.USE).getNumFreeSlot() >= 1) {
						var pan = cm.itemQuantity(4310218) + cost;
						cm.gainItem(item1[0], -item1[2] * cost);
						cm.gainItem(ac4[0], cost*5);
						cm.sendOk("#fn나눔고딕#교환완료");
						cm.dispose();
					} else {
						cm.sendOk("#fn나눔고딕##r장비창을 확인해주세요");
						cm.dispose();
					}
				} else {
					cm.sendOk("#fn나눔고딕##r#i2430658##z2430658#이 부족합니다. \r\n 또는 인벤토리가 꽉 차있는지 확인 해주시길 바랍니다");
					cm.dispose();
				}
			}

                                     if (select == 5) {
				if (cm.haveItem(item1[0], item1[2] * cost) && cm.canHold(1712001, cost*5)) {
					if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.USE).getNumFreeSlot() >= 1) {
						var pan = cm.itemQuantity(4310218) + cost;
						cm.gainItem(item1[0], -item1[2] * cost);
						cm.gainItem(ac5[0], cost*5);
						cm.sendOk("#fn나눔고딕#교환완료");
						cm.dispose();
					} else {
						cm.sendOk("#fn나눔고딕##r장비창을 확인해주세요");
						cm.dispose();
					}
				} else {
					cm.sendOk("#fn나눔고딕##r#i2430658##z2430658#이 부족합니다. \r\n 또는 인벤토리가 꽉 차있는지 확인 해주시길 바랍니다");
					cm.dispose();
				}
			}
                                     if (select == 6) {
				if (cm.haveItem(item1[0], item1[2] * cost) && cm.canHold(1712001, cost*5)) {
					if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.USE).getNumFreeSlot() >= 1) {
						var pan = cm.itemQuantity(4310218) + cost;
						cm.gainItem(item1[0], -item1[2] * cost);
						cm.gainItem(ac6[0], cost*5);
						cm.sendOk("#fn나눔고딕#교환완료");
						cm.dispose();
					} else {
						cm.sendOk("#fn나눔고딕##r장비창을 확인해주세요");
						cm.dispose();
					}
				} else {
					cm.sendOk("#fn나눔고딕##r#i2430658##z2430658#이 부족합니다. \r\n 또는 인벤토리가 꽉 차있는지 확인 해주시길 바랍니다");
					cm.dispose();
				}
			}
                                     if (select == 7) {
				if (cm.haveItem(item8[0], item8[2] * cost)) {
					if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.USE).getNumFreeSlot() >= 1) {
						var pan = cm.itemQuantity(4310218) + cost;
						cm.gainItem(item8[0], -item8[2] * cost);
						cm.gainItem(ac2[0], cost);
						cm.sendOk("#fn나눔고딕#교환완료");
						cm.dispose();
					} else {
						cm.sendOk("#fn나눔고딕##r장비창을 확인해주세요");
						cm.dispose();
					}
				} else {
					cm.sendOk("#fn나눔고딕##r#i2430658##z2430658#이 부족합니다. \r\n 또는 인벤토리가 꽉 차있는지 확인 해주시길 바랍니다");
					cm.dispose();
				}
			}
			/*if (select == 9999) {
				if (cm.haveItem(item1[0], item1[2] * cost) && cm.haveItem(item8[0], item8[2] * cost)) {
					if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.ETC).getNumFreeSlot() >= cost) {
						var ara = cm.itemQuantity(4310249) + cost;
						cm.gainItem(item1[0], -item1[2] * cost);
						cm.gainItem(item8[0], -item8[2] * cost);
						cm.gainItem(ac3[0], cost);
						cm.sendOk("#fn나눔고딕#교환완료\r\n#Cgray#(현재 가지고계신 " + ac3[1] + "의 갯수 : " + ara + "개");
						cm.dispose();
					} else {
						cm.sendOk("#fn나눔고딕##r장비창을 확인해주세요");
						cm.dispose();
					}
				} else {
					cm.sendOk("#fn나눔고딕##r물방울석이 부족합니다. \r\n 또는 인벤토리가 꽉 차있는지 확인 해주시길 바랍니다");
					cm.dispose();
				}
			}*/
		}
	}
}