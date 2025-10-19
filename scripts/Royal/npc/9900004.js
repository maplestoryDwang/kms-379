var status = -1;
var selectedSlot = -1;
var itemList = [];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 1) {
            status++;
        } else {
            cm.dispose();
            return;
        }

        if (status == 0) {
            var inv = cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.EQUIP);
            var items = inv.list().toArray();

            // 슬롯 번호 기준 정렬
            java.util.Arrays.sort(items, function(a, b) {
                return a.getPosition() - b.getPosition();
            });

            var txt = "보낼 아이템을 선택하세요.\r\n";
            itemList = [];

            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                txt += "#L" + i + "##i" + item.getItemId() + ":# #t" + item.getItemId() + "# (슬롯: " + item.getPosition() + ")\r\n";
                itemList.push(item);
            }

            if (itemList.length == 0) {
                cm.sendOk("보유 중인 장비 아이템이 없습니다.");
                cm.dispose();
                return;
            }

            cm.sendSimple(txt);

        } else if (status == 1) {
            selectedSlot = selection;

            cm.sendGetText("아이템을 보낼 유저의 닉네임을 입력하세요.\r\n(같은 채널 내 유저만 가능)");

        } else if (status == 2) {
            var targetName = cm.getText();
            var target = cm.getClient().getChannelServer().getPlayerStorage().getCharacterByName(targetName);

            if (target == null) {
                cm.sendOk("해당 유저를 찾을 수 없습니다.\r\n(같은 채널에 접속 중이어야 합니다.)");
                cm.dispose();
                return;
            }

            var selectedItem = itemList[selectedSlot];

            if (selectedItem == null) {
                cm.sendOk("선택한 아이템을 찾을 수 없습니다.");
                cm.dispose();
                return;
            }

            var newItem = selectedItem.copy();

            target.getInventory(Packages.objects.item.MapleInventoryType.EQUIP).addItem(newItem);
            target.send(Packages.network.models.CWvsContext.InventoryPacket.addInventorySlot(Packages.objects.item.MapleInventoryType.EQUIP, newItem));

            cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.EQUIP).removeItem(selectedItem.getPosition());
            cm.getPlayer().send(Packages.network.models.CWvsContext.InventoryPacket.clearInventoryItem(Packages.objects.item.MapleInventoryType.EQUIP, selectedItem.getPosition(), false));

            cm.sendOk("아이템을 " + targetName + "님께 성공적으로 보냈습니다.");
            cm.dispose();
        }
    }
}
