importPackage(java.lang);
importPackage(java.text);
importPackage(java.util);
importPackage(java.io);
importPackage(Packages.objects.wz.provider);
importPackage(Packages.objects.users);
importPackage(Packages.objects.item);
importPackage(Packages.objects.utils);
importPackage(Packages.network.models);
var status;
var select;

function start() {
    status = -1;
    action(1, 1, 0);
}

function action(mode, type, selection) {
    if (mode < 0) {
        cm.dispose();
        return;
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            var v0 = "#r#i2634291# #z2634291##k을 사용하시겠습니까?\r\n"
            cm.sendYesNo(v0)
        } else if (status == 1) {
            cm.sendGetText("정말 현재 캐릭터로 #r#i2634291# #z2634291##k을 사용하시려면\r\n아래의 입력창에 #r#e'캐릭터명/수령한다'#n#k를 입력해 주세요.\r\n\r\n#fs18##e#r(예시) #h #/수령한다");
        } else if (status == 2) {
            if (cm.haveItem(2634291, 1)) {
                if (cm.getText() == cm.getPlayer().getName() + "/수령한다") {
                    var slot = cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.CASH).getNumFreeSlot();
                    if (slot < 3) {
                        cm.sendOk("인벤토리 공간을 충분히 확보하고 다시 시도해 주세요.");
                        cm.dispose();
                        return;
                    }
                    var item = new Item(5002349, 1, 1, 0);
                    item.setExpiration((new Date()).getTime() + (1000 * 60 * 60 * 24 * 30));
                    var pet = MaplePet.createPet(5002349, MapleInventoryIdentifier.getInstance());
                    item.setPet(pet);
                    item.setUniqueId(pet.getUniqueId());
                    MapleInventoryManipulator.addFromDrop(cm.getClient(), item, false);
                    cm.sendOk("#r#i5002349# #z5002349##k(이)가 인벤토리로 지급되었습니다.");
                    cm.gainItem(2634291, -1);
                    cm.dispose();
                } else {
                    cm.sendOk("입력한 내용이 올바르지 않습니다. 다시 확인해 주세요.");
                    cm.dispose();
                }
            } else {
                cm.sendOk("#r#i2634291# #z2634291#을 보유 중이신 게 맞습니까?");
                cm.dispose();

            }
        }
    }
}