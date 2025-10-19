importPackage(java.lang);
importPackage(Packages.database);

var status = -1;
var s = -1;

function start() {
    s = -1;
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, sel) {
    if (mode == 0 && sel == -1 && type == 6) {
        cm.dispose();
        return;
    }
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        status--;
    }
    if (mode == 1) {
        status++;
    }
    if (status == 0) {
        con = DBConnection.getConnection();
        ps = con.prepareStatement("SELECT phonenumber FROM accounts WHERE id = '" + cm.getPlayer().getAccountID() + "'");
        rs = ps.executeQuery();
        while (rs.next()) {
            phonenumber = rs.getString("phonenumber");
        }
        rs.close();
        ps.close();
        con.close();

        if (phonenumber == "010-7777-7777") {
            cm.dispose();
            cm.getPlayer().setKeyValue(19770, "elapsed", 179);
            cm.getPlayer().setKeyValue(19770, "count", 1);
            return;
        }
        var text = "#fs11##e#h0##n님의 #b진실의 방#k 누적 입장 횟수는 " + cm.getPlayer().getOneInfoQuestInteger(19770, "count") + "회입니다.\r\n\r\n";
        text += "#L0##b진실의 방#k에 대한 설명을 듣는다.#l\r\n#L1#제가 #b진실의 방#k으로 온 이유는 무엇인가요?#l";
        cm.sendSimple(text);
    } else if (status == 1) {
        if (s == -1) {
            s = sel;
        }
        if (s == 0) {
            cm.sendNext("#fs11##b진실의 방#k은 #r거짓말 탐지기에 오답을 입력#k하여 매크로 사용 의심 캐릭터로 적발되면 이동하는 공간입니다.");
        } else if (s == 1) {
            cm.sendSimple("#fs11#" + cm.getPlayer().getOneInfoQuest(19770, "reason") + "\r\n\r\n#L0##r거짓말 탐지기#k에 대해 자세한 설명을 듣는다.#l");
        }
    } else if (status == 2) {
        if (s == 0) {
            cm.sendNextPrev("#fs11##b진실의 방#k에 들어온 캐릭터는 화면 상단 타이머에 표시되는 #r남은 시간#k만큼 게임 접속을 유지해야 나갈 수 있습니다.");
        } else if (s == 1) {
            cm.sendNext("#fs11##r거짓말 탐지기#k에 매크로 사용 의심 캐릭터에게 다른 유저들이 아이템을 사용하여 발동시키거나 자동으로 발동되는 매크로 탐지 시스템입니다.");
        }
    } else if (status == 3) {
        if (s == 0) {
            cm.sendNextPrev("#fs11##b진실의 방#k 안에서는 아래 사항이 제한됩니다.\r\n\r\n#r  - 이동 및 스킬 사용\r\n  - 채팅\r\n  - 아이템 사용\r\n  - 캐시샵/메이플 옥션\r\n  - 게임 내 컨텐츠 및 이벤트 참여");
        } else if (s == 1) {
            cm.sendNextPrev("#fs11##r거짓말 탐지기#k가 발동되면 제한 시간 내에 화면에 보이는 문구를 정확하게 입력하셔야 합니다.");
        }
    } else if (status == 4) {
        if (s == 0) {
            cm.sendNextPrev("#fs11##b진실의 방#k은 누적 입장 횟수에 비례하여 체류 필요 시간이 늘어나며, 매주 #b월요일 자정#k 누적 입장 횟수가 #e초기화#n 됩니다.\r\n#r(진실의 방 입장 횟수는 계정 내 캐릭터들의 입장 횟수가 합산되어 적용됩니다.)");
        } else if (s == 1) {
            cm.sendNextPrev("#fs11#정답 입력 기회는 총 #e4회#n 주어지니 주어진 시간 안에 정답을 입력할 수 있도록 집중해서 보셔야겠죠?");
        }
    } else if (status == 5) {
        cm.dispose();
        if (s == 0) {
            cm.sendPrev("#fs11#앞으로 #b진실의 방#k에 오지 않도록 #r거짓말 탐지기#k에 주의해주세요.");
        } else if (s == 1) {
            cm.sendPrev("#fs11#위 내용을 숙지하신 후 앞으로는 #b진실의 방#k에 들어오지 않도록 주의해주세요.");
        }
    }
}