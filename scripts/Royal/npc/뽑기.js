importPackage(Packages.handling.world);
importPackage(Packages.tools.packet);

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
핑크색 = "#fc0xFFFF3366#"
분홍색 = "#fc0xFFF781D8#"
엔터 = "\r\n"
엔터2 = "\r\n\r\n"
enter = "\r\n";
끝 = "#fc0xFF000000#"

var status = -1;
var enter = "\r\n";
var boxmsg = enter
var items0 = [
    [[2439988, 1], 0.1], // 0.1% 칠흑 선택
    [[2439961, 1], 0.1], // 0.1% 혼케인 선택
    [[2439960, 1], 0.1], // 0.1% 혼케인 랜덤
    [[2630782, 1], 4.6], // 4.8% 아케인 랜덤

    [[0, 0], 0], // 엔터

    [[2049380, 1], 0.2], // 0.2% 스타포스 25성
    [[2430045, 1], 0.3], // 0.3% 메강 18강
    [[2430044, 1], 1], // 1% 메강 17강
    [[2430043, 1], 1], // 1% 메강 15강
    [[2049377, 1], 1], // 1.0% 스타포스 22성
    [[2049376, 1], 1.5], // 1.5% 스타포스 20성
    [[2430042, 1], 7], // 7% 메강 13강
    [[2430041, 1], 11], // 11% 메강 10강

    [[0, 0], 0], // 엔터

    [[2439944, 1], 2], // 2% 자석펫 4기
    [[2439943, 1], 2], // 2% 자석펫 3기
    [[2439942, 1], 3], // 3% 자석펫 2기
    [[2630127, 1], 4], // 3% 자석펫 1기

    [[0, 0], 0], // 엔터

    [[4001715, 50], 11], // 11% 1억 50개
    [[4031227, 50], 11], // 11% 찬빛 50개
    [[4310308, 200], 11], // 11% 네오코어 200개
    [[4310266, 200], 12], // 12% 승급주화 200개
    [[4310237, 200], 16] // 16% 헌트코인 200개
]

var items = [
    //[[아이템코드, 갯수], 확률]
    [[2439958, 1], 1], //파멸 선택
    [[2439988, 1], 1], //칠흑 선택
    [[2430047, 1], 1], //메강 20강
    [[2430046, 1], 1], //메강 19강
    [[5068306, 1], 1], //레인보우베리
    [[2439960, 1], 5], //혼케인 랜덤
    [[2439961, 1], 5], // 혼케인 선택
    [[2439962, 1], 5], // 혼케인 무기
    [[1113305, 1], 5], // 글로나의 심장
    [[1122439, 1], 5], // 어둠의 유산
    [[1113070, 1], 5], // 스칼렛 링
    [[1012632, 1], 5], // 루컨마
    [[1022278, 1], 5], // 마깃안
    [[2430017, 20], 5], // 후포 200000
    [[5062005, 20], 5], // 어큐 20
    [[5062503, 20], 5], // 화에큐 20
    [[4031227, 1000], 5], // 찬빛 1000
    [[4310308, 2000], 5], // 네오코어 2000
    [[4310266, 2000], 5], // 승급주화 2000
    [[2049380, 1], 5], // 스타포스 25성
    [[2049377, 1], 5], // 스타포스 22성
    [[2430044, 1], 5], // 메강 17강 
    [[4001715, 100], 10] // 100억
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
        var chat = "#fs11#"
        chat += "#h0#님 안녕하세요? #b#e[강림월드]#k#n 의 돌림판입니다!" + enter
        chat += "돌림판 티켓 또는 스페셜 돌림판 티켓으로 뽑기에 도전해보세요!" + enter + enter
        chat += "#L0#" + 색 + "[일반 돌림판]" + 끝 + " 뽑기#l　"
        chat += "#L1#" + 색 + "[스페셜 돌림판]" + 끝 + " 뽑기#l"
        chat += "\r\n"
        chat += "#L98#" + 색 + "[일반 돌림판]" + 끝 + " 리스트#l"
        chat += "#L99#" + 색 + "[스페셜 돌림판]" + 끝 + " 리스트#l"
        cm.sendSimple(chat);
    } else if (status == 1) {
        switch (selection) {
            case 0:
                if (!cm.haveItem(4036660, 1)) {
                    cm.sendOkS("#i4036660# #z4036660# 아이템이 없는것같은데?..", 700);
                    cm.dispose();
                    return;
                }
                if (cm.getInvSlots(1) < 3 || cm.getInvSlots(2) < 3 || cm.getInvSlots(3) < 3 || cm.getInvSlots(4) < 3 || cm.getInvSlots(5) < 3) {
                    cm.sendOkS("#fs11##fc0xFF6600CC#인벤토리를 탭별로 3칸이상 비워주세요", 2);
                    cm.dispose();
                    return;
                }

                cm.gainItem(4036660, -1);
                //NormalUnboxing();
                // 일반 뽑기
                for (var x = 0; x < amount; x++) {
                    var percentage = 0;
                    var chance = Math.random() * 100;
                    for (var i = 0; i < items0.length; i++) {
                        percentage += items0[i][1];
                        if (percentage > chance) {
                            boxmsg = "#fs11#" + 색 + "#i" + items0[i][0][0] + "##z" + items0[i][0][0] + "# #r" + items0[i][0][1] + "개 #b가 당첨되셨습니다#k#n" + enter + enter;
                            cm.gainItem(items0[i][0][0], items0[i][0][1]);
                            break;
                        }
                    }
                }
                boxmsg += 핑크색 + 별 + "마음에 드셨길 바래요 한번더 뽑으시겠어요?";
                retry = "NORMAL";
                cm.sendYesNo(boxmsg);

                break;
            case 1:
                if (!cm.haveItem(4036661, 1)) {
                    cm.sendOkS("#i4036661# #z4036661# 아이템이 없는것같은데?..", 700);
                    cm.dispose();
                    return;
                }
                if (cm.getInvSlots(1) < 1 || cm.getInvSlots(2) < 1 || cm.getInvSlots(3) < 1 || cm.getInvSlots(4) < 1 || cm.getInvSlots(5) < 1) {
                    cm.sendOkS("#fs11##fc0xFF6600CC#인벤토리를 탭별로 1칸씩은 비워주세요", 2);
                    cm.dispose();
                    return;
                }

                cm.gainItem(4036661, -1);
                //AdvancedUnboxing();
                // M.V.P 뽑기
                for (var x = 0; x < amount; x++) {
                    var percentage = 0;
                    var chance = Math.random() * 100;
                    for (var i = 0; i < items.length; i++) {
                        percentage += items[i][1];
                        if (percentage > chance) {
                            boxmsg = 색 + "#fs11#" + 색 + "#i" + items[i][0][0] + "##z" + items[i][0][0] + "# #r" + items[i][0][1] + "개 #b가 당첨되셨습니다#k#n" + enter + enter;
                            cm.gainItem(items[i][0][0], items[i][0][1]);
                            break;
                        }
                    }
                }
                boxmsg += 핑크색 + 별 + "마음에 드셨길 바래요 한번더 뽑으시겠어요?";
                retry = "MVP";
                cm.sendYesNo(boxmsg);

                break;
            case 98:
                var msg = 별파 + "#fs11##fc0xFF000000# 뽑을 수 있는 품목은 아래와 같아! #fs11#" + 별파 + enter + enter;
                for (i = 0; i < items0.length; i++) {
                    if (items0[i][0][0] == 0) {
                        msg += enter + enter;
                    } else {
                        msg += 색 + "#i" + items0[i][0][0] + "##z" + items0[i][0][0] + "##r " + items0[i][0][1] + "개#k" + enter;
                    }
                }
                cm.sendOk(msg);
                cm.dispose();

            case 99:
                var msg = 별파 + "#fs11##fc0xFF000000# 뽑을 수 있는 품목은 아래와 같아! #fs11#" + 별파 + enter + enter;
                for (i = 0; i < items.length; i++) {
                    msg += 색 + "#i" + items[i][0][0] + "##z" + items[i][0][0] + "##r " + items[i][0][1] + "개 #b" + items[i][1] + "%#k" + enter;
                }
                cm.sendOk(msg);
                cm.dispose();
        }
    } else if (status == 2) {
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