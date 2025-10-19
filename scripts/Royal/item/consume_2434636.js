 var status;
importPackage(Packages.server);
importPackage(Packages.client.inventory);

var item = [[4001715, 1, 2], [2049153, 1, 2], [4009005, 1, 10], [4021031, 1, 20], [2049360, 1, 1], [2048717, 1, 5], [2437760, 1, 2], [2633336, 1, 2], [4310266, 1, 20], [4310237, 1, 50], [2434290, 1, 2]];

function start() {
    status = -1;
    action(1, 1, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        status --;
    }
    if (mode == 1) {
        status++;
    }
    if (status == 0) {
                a = Packages.objects.utils.Randomizer.rand(0, item.length - 1);
                b = Packages.objects.utils.Randomizer.rand(item[a][1], item[a][2]);

        cm.dispose();
        if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.EQUIP).getNumFreeSlot() > 0) {
            if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.USE).getNumFreeSlot() > 0) {
                if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.ETC).getNumFreeSlot() > 0) {
                    cm.gainItem(2434636, -1);
                    cm.gainItem(item[a][0], b);
                    return;
                }
            }
        }
    }
}
