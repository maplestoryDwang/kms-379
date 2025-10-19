importPackage(Packages.objects.wz.provider);
importPackage(Packages.objects.users);
importPackage(Packages.objects.item);
importPackage(Packages.objects.utils);
importPackage(Packages.network.models);

var status = -1;
var itemcode = 2439995;
var chance = 10;

var qty1 = 0;
var qty2 = 0;

function start() {
    status = -1;
    action (1, 0, 0);
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
        if(!cm.haveItem(itemcode, 1)) {
            cm.dispose();
            return;
        }
        
        var suk1 = Math.floor((cm.itemQuantity(itemcode) / 1));
        stigmacoin = Math.min(suk1);
        cm.sendGetNumber("\r\n#fn나눔고딕##i" + itemcode + "# #z" + itemcode + "# 를 몇 개 개봉하시겠습니까?\r\n#Cgray#(현재 교환 가능한 #z" + itemcode + "# 갯수 : " + stigmacoin + "개)", 1, 1, stigmacoin);

        var leftslot = cm.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot();
        
        if (leftslot < 3) {
            cm.sendOk("인벤토리 공간이 최소한 2칸은 필요합니다. 소비 탭의 공간을 1칸이상 비워주신 후 다시 열어주세요.");
            cm.dispose();
            return;
        }

    } else if (status == 1) {
        var cost = selection;
        
        for (i = 0; i < cost; i++) {
            var rndvalue = Randomizer.rand(1, 100);
            
            if (rndvalue < chance) {
                qty1++;
            } else {
                qty2++;
            }
        }
        
        if (cm.haveItem(itemcode, cost)) {
            if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.USE).getNumFreeSlot() >= 2) {
                cm.gainItem(itemcode, -cost);
                if (qty1 > 0) cm.gainItem(2432126, qty1);
                if (qty2 > 0) cm.gainItem(2432127, qty2);
                cm.sendOk("#fn나눔고딕##i" + itemcode + "# #z" + itemcode + "#\r\n\r\n#r개봉 결과는 아래와 같습니다\r\n\r\n#b#z2432126# " + qty1 + "개\r\n#z2432127# " + qty2 + "개");
                cm.dispose();
            } else {
                cm.sendOk("#fn나눔고딕##r인벤토리 자리를 확인해주세요");
                cm.dispose();
            }
        } else {
            cm.sendOk("?");
            cm.dispose();
        }
    }
}