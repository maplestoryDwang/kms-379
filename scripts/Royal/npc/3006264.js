// NPC 이름: 교환하는 NPC 예시 (ex: “교환의 정령”)
// 아이템 목록 배열
var exchangeItems = [
    1213018, 1214018, 1212120, 1222113, 1232113, 1242121, 1262039, 1302343,
    1322255, 1312203, 1332279, 1342104, 1362140, 1372228, 1382265, 1402259, 1412181, 1422189,
    1432218, 1442274, 1452257, 1462243, 1472265, 1482221, 1492235, 1522143, 1532150, 1582023,
    1272017, 1282017, 1292018, 1592020, 1404018, 1102940, 1102941, 1102942, 1102943, 1102944,
    1082695, 1082696, 1082697, 1082698, 1082699, 1073158, 1073159, 1073160, 1073161, 1073162,
    1152196, 1152197, 1152198, 1152199, 1152200, 1053063, 1053064, 1053065, 1053066, 1053067, 1004808, 1004809, 1004810, 1004811, 1004812
];

// 교환 대상 아이템 ID
var targetItem = 4310218;
var availableItems = [];
var selectionIndex = -1; // 사용자가 선택한 인덱스 저장

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0) {
        cm.dispose();
        return;
    }
    status++;

    // === status == 0: “소지한 교환 가능 아이템만 골라서 메뉴를 보여주는 단계” ===
    if (status == 0) {
        availableItems = [];
        // 플레이어 인벤토리에 있는 아이템만 필터링
        for (var i = 0; i < exchangeItems.length; i++) {
            var id = exchangeItems[i];
            if (cm.haveItem(id, 1)) {
                availableItems.push(id);
            }
        }
        if (availableItems.length == 0) {
            cm.sendOk("죄송하지만, 현재 인벤토리에 교환 가능한 아이템이 없어요.");
            cm.dispose();
            return;
        }

        // 대화 시작 및 메뉴 생성
        var msg = "안녕하세요! 저에게 #e#b아케인셰이드 장비 1개#k#n를 주신다면\r\n"
                + "#e#b#z4310218##k#n 1개로 교환해 드리고 있어요.\r\n\r\n"
                + "#b다음 보유 목록 중 교환하실 아이템을 선택하세요.#k\r\n\r\n";
        for (var i = 0; i < availableItems.length; i++) {
            var itemId = availableItems[i];
            msg += "#L" + i + "# #i" + itemId + "# [#z" + itemId + "#]#l\r\n";
        }
        cm.sendSimple(msg);

    // === status == 1: 사용자가 메뉴에서 아이템을 선택 ===
    } else if (status == 1) {
        selectionIndex = selection;
        var itemId = availableItems[selectionIndex];
        // 혹시 클릭한 순간 이미 인벤토리에서 사라졌다면 에러 처리
        if (!cm.haveItem(itemId, 1)) {
            cm.sendOk("아쉽게도 선택하신 아이템이 더 이상 인벤토리에 없어요.");
            cm.dispose();
            return;
        }

        // 교환 진행: 아이템 제거 & targetItem 지급
        cm.gainItem(itemId, -1);
        cm.gainItem(targetItem, 1);

        // 성공 메시지 출력. 이 때 dispose 하지 않고, 확인 버튼 누르면 다시 메뉴 열리도록 상태 초기화
        cm.sendOk("성공적으로 아이템 #i" + itemId + "# 1개를 #i" + targetItem + "# 1개로 교환했습니다!");
    
    // === status == 2: “확인”을 누른 뒤 다시 메뉴를 재실행하기 위한 로직 ===
    } else if (status == 2) {
        // status 값을 초기화하고 다시 start() 혹은 action(1,0,0) 호출
        status = -1;
        action(1, 0, 0);
    }
}
