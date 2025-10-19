var enter = "\r\n";
var seld = -1;

var a = "";

var price = 30;
var allstat = 2, atk = 2; // 1회당 올스텟, 공마 증가치

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }

    if (status == 0) {
        var txt = "#fs11##fc0xFF000000#캐시아이템을 #b#i4310308##z4310308# 30개#fc0xFF000000#로 강화할 수 있다는 사실을 알고 계신가요?#b 강화하실 캐시아이템#fc0xFF000000#을 골라주세요.\r\n#r(1회당 올스탯 2 / 공 마 2 증가 중첩가능)#k\r\n";
        for (i = 0; i <= cm.getInventory(6).getSlotLimit(); i++) {
            if (cm.getInventory(6).getItem(i) != null) {
                if (cm.isCash(cm.getInventory(6).getItem(i).getItemId())) {
                    txt += "#b#L" + i + "# #e#i" + cm.getInventory(6).getItem(i).getItemId() + "# #b#z" + cm.getInventory(6).getItem(i).getItemId() + "# #r["+i+"슬롯]#k\r\n";
                }
            }
        }
        cm.sendSimple(txt);
    } else if (status == 1) {
        tem = selection;
        var suk1 = Math.floor((cm.itemQuantity(4310308) / price));
        stigmacoin = Math.min(suk1);

		item = cm.getInventory(6).getItem(tem);

        for (var i = 0; i < stigmacoin; i++) {
			if (item.getStr() + (allstat * i) >= 32000 || item.getDex() + (allstat * i) >= 32000 || item.getInt() + (allstat * i) >= 32000
                || item.getLuk() + (allstat * i) >= 32000 || item.getWatk() + (atk * i) >= 32000 || item.getMatk() + (atk * i) >= 32000) {
				stigmacoin = i;
				break;
			}
		}

        stigmacoin = Math.min(1000, stigmacoin);

        cm.sendGetNumber("\r\n#fn나눔고딕#정말 몇회 강화 하시겠습니까? \r\n#Cgray#(현재 강화 가능한 횟수 : " + stigmacoin + "번)", 1, 1, stigmacoin);
    } else if (status == 2) {
        a = selection;

        if (!cm.haveItem(4310308, price * a)) {
            cm.sendOk("#fs11##fc0xFF000000#" + a + "회 강화하기 위해선 #b" + price * a + " #z4310308##fc0xFF000000#가 필요합니다.");
            cm.dispose();
            return;
        }

        if (item == null) {
            return;
        }
        if (!cm.isCash(item.getItemId())) {
            cm.sendOk("핵 사용 적발");
            cm.dispose();
            return;
        }

        item.addStr(allstat * a);
        item.addDex(allstat * a);
        item.addInt(allstat * a);
        item.addLuk(allstat * a);
        item.addWatk(atk * a);
        item.addMatk(atk * a);
        cm.gainItem(4310308, -price * a);
        cm.sendOk("#fs11#아이템에 스텟을 부여했습니다. 이용해 주셔서 감사합니다.");
        cm.dispose();

        cm.addEnchantLog(1, item.getItemId(), item.getSerialNumberEquip(), 13, 0, "캐시 옵션 부여 " + a + "번 적용 (계정 : " + cm.getClient().getAccountName() + ", 캐릭터 : " + cm.getPlayer().getName() + ", 장비 정보 [" + item.toString() + "])");

        cm.getPlayer().send(Packages.network.models.CWvsContext.InventoryPacket.updateInventoryItem(Packages.objects.item.MapleInventoryType.CASH_EQUIP, item, false, cm.getPlayer()));
    }
}