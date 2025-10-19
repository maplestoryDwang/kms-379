importPackage(Packages.database);
importPackage(java.lang);

var enter = "\r\n";
var status = -1;
var seld = -1;
var seldGM = -1;
var step = -1;
var targetName = "";
var amount = 0;

var DB_SCHEMA = "ganglim";

var grade = [
    [0, "Lv.0"],
    [1, "Lv.1"],
    [2, "Lv.2"],
    [3, "Lv.3"],
    [4, "Lv.4"],
    [5, "Lv.5"],
    [6, "Lv.6"]
];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, sel) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }

    if (status == 0) {
        if (cm.inBoss()) {
            cm.getPlayer().dropMessage(5, "보스 진행중엔 이용이 불가능합니다.");
            cm.dispose();
            return;
        }

        if (cm.getClient().getKeyValue("pGrade") == null) {
            cm.getClient().setKeyValue("pGrade", "0");
        }
        if (cm.getClient().getKeyValue("PCount") == null) {
            cm.getClient().setKeyValue("PCount", "0");
        }

        var loadpGrade = parseInt(cm.getClient().getKeyValue("pGrade"));
        var loadPCount = parseInt(cm.getClient().getKeyValue("PCount"));

        if (loadPCount >= 3000 && loadpGrade < 6) {
            setpGrade(6);
            return;
        } else if (loadPCount >= 2000 && loadpGrade < 5) {
            setpGrade(5);
            return;
        } else if (loadPCount >= 1000 && loadpGrade < 4) {
            setpGrade(4);
            return;
        } else if (loadPCount >= 600 && loadpGrade < 3) {
            setpGrade(3);
            return;
        } else if (loadPCount >= 300 && loadpGrade < 2) {
            setpGrade(2);
            return;
        } else if (loadPCount >= 100 && loadpGrade < 1) {
            setpGrade(1);
            return;
        }

        // 현재 보유 홍보 포인트, 등급, 누적 횟수 표시
        var 홍보포인트 = comma(cm.getPlayer().getHPoint());
        var 홍보등급 = cm.getPlayer().getPgrades();
        var 홍보횟수 = comma(cm.getClient().getKeyValue("PCount"));

        var msg = "#fs11#";
        msg += "#fc0xFFFF3366##h ##fc0xFF000000# 님의 홍보 포인트 : #fc0xFFFF3366#" + 홍보포인트 + "P#fc0xFF000000##n" + enter;
        msg += "#fc0xFFFF3366##h ##fc0xFF000000# 님의 홍보 등급 : #fc0xFFFF3366#" + 홍보등급 + "#fc0xFF000000##n#b" + enter;
        msg += "#fc0xFFFF3366##h ##fc0xFF000000# 님의 누적 홍보 횟수 : #fc0xFFFF3366#" + 홍보횟수 + " 번#k#b" + enter + enter;

        msg += "#L1##e[ 홍보 포인트 상점 ]#n#l    "+enter+enter;
        msg += "#L4#[ 초월 아케인심볼 ]#l" + enter+ enter;
        msg += "#L5#[ 치장아이템 강화 ]#l" + enter+ enter;
        msg += "#L2#홍보 등급의 혜택을 알고싶습니다.#l" + enter;
        msg += "#L3#홍보 포인트를 수령하겠습니다.#l" + enter; // ← 새 옵션 추가

        if (cm.getPlayer().isGM()) {
            msg += enter + enter + "#r#h GM#님, GM 전용 메뉴입니다!#fc0xFF000000#" + enter;
            msg += "#L99#GM: 홍보포인트/횟수 지급 메뉴 열기#l";
        }

        cm.sendSimple(msg);

    }
    else if (status == 1) {
        seld = sel;

        if (seld == 1) {
            cm.dispose();
            cm.openNpcCustom(cm.getClient(), 9000213, "홍보상점");
            return;
        }
        else if (seld == 2) {
            cm.dispose();
            cm.openNpcCustom(cm.getClient(), 9000213, "pgradeinfo");
            return;
        }
        else if (seld == 3) {
            giveHongboReward();
            return;
        }
        else if (seld == 4) {
            cm.dispose();
            cm.openNpcCustom(cm.getClient(), 9000178, "초월아케인심볼");
            return;
        }
        else if (seld == 5) {
            cm.dispose();
            cm.openNpcCustom(cm.getClient(), 9000331, "강림캐시강화");
            return;
        }
        // 99) GM 전용
        else if (seld == 99 && cm.getPlayer().isGM()) {
            var gmMsg = "#fs12#[GM 메뉴] 무엇을 하시겠습니까?#fc0xFF000000##fs11#" + enter;
            gmMsg += "#L100#1. 유저에게 홍보 포인트 지급#l" + enter;
            gmMsg += "#L101#2. 유저에게 누적 홍보 횟수 지급#l";

            cm.sendSimple(gmMsg);
            return;
        }

        cm.sendOk("잘못된 접근입니다.");
        cm.dispose();
    }
    else if (status == 2) {
        if (seld == 99 && cm.getPlayer().isGM()) {
            seldGM = sel;
            if (seldGM == 100) {
                cm.sendGetText("포인트를 지급할 유저의 닉네임을 정확히 입력하세요.");
                step = 1;
                return;
            } else if (seldGM == 101) {
                cm.sendGetText("누적 횟수를 지급할 유저의 닉네임을 정확히 입력하세요.");
                step = 1;
                return;
            } else {
                cm.sendOk("잘못된 접근입니다.");
                cm.dispose();
                return;
            }
        }

        cm.sendOk("잘못된 흐름입니다.");
        cm.dispose();
    }
    else if (status == 3) {
        if (step == 1) {
            targetName = cm.getText().trim();
            if (targetName.length < 1) {
                cm.sendOk("유효한 닉네임을 입력해주세요.");
                cm.dispose();
                return;
            }

            if (seldGM == 100) {
                cm.sendGetText("🔹 [" + targetName + "] 님에게 지급할 홍보 포인트(P)를 입력하세요.\r\n(숫자만 입력, 예: 500)");
            } else if (seldGM == 101) {
                cm.sendGetText("🔹 [" + targetName + "] 님에게 지급할 누적 홍보 횟수(번)를 입력하세요.\r\n(숫자만 입력, 예: 10)");
            }
            step = 2;
            return;

        } else {
            cm.sendOk("예상치 못한 오류가 발생했습니다.");
            cm.dispose();
            return;
        }
    }
    else if (status == 4) {
        if (step == 2) {
            var txt = cm.getText().trim();
            if (!/^\d+$/.test(txt)) {
                cm.sendOk("숫자만 입력해주세요.");
                cm.dispose();
                return;
            }
            amount = parseInt(txt);

            var target = cm.getClient().getChannelServer()
                            .getPlayerStorage()
                            .getCharacterByName(targetName);

            if (target == null) {
                cm.sendOk("해당 이름의 캐릭터가 현재 채널에 존재하지 않습니다.\r\n채널을 옮겼거나 접속이 끊겼을 수 있습니다.");
                cm.dispose();
                return;
            }

            if (!cm.getPlayer().isGM()) {
                cm.sendOk("권한이 없습니다.");
                cm.dispose();
                return;
            }

            if (seldGM == 100) {
                target.gainHPoint(amount);
                cm.sendOk("[" + targetName + "] 님에게 홍보 포인트 " + amount + "P 를 지급하였습니다.");
                cm.dispose();
                return;

            } else if (seldGM == 101) {
                var oldCount = parseInt(target.getClient().getKeyValue("PCount"));
                var newCount = oldCount + amount;
                target.getClient().setKeyValue("PCount", "" + newCount);
                cm.sendOk("[" + targetName + "] 님의 누적 홍보 횟수를 " + amount + " 회 늘려서, 총 " + newCount + " 회로 설정하였습니다.");
                cm.dispose();
                return;

            } else {
                cm.sendOk("잘못된 접근입니다.");
                cm.dispose();
                return;
            }

        } else {
            cm.sendOk("예상치 못한 흐름 오류가 발생했습니다.");
            cm.dispose();
            return;
        }
    }
    else {
        cm.dispose();
    }
}

function giveHongboReward() {
    var con = null;
    var ps = null, rs = null;
    try {
        con = DBConnection.getConnection();

        var dbgPS = con.prepareStatement("SELECT DATABASE()");
        var dbgRS = dbgPS.executeQuery();
        dbgRS.close();
        dbgPS.close();

        ps = con.prepareStatement(
            "SELECT id, blog, etc " +
            "FROM " + DB_SCHEMA + ".hongbo " +
            "WHERE `name` = ? AND `check` = 0"
        );
        ps.setString(1, cm.getPlayer().getName());
        rs = ps.executeQuery();

        var totalBlog = 0;  // 지급할 홍보 횟수 합계
        var totalEtc  = 0;  // 지급할 홍보 포인트 합계
        var idList = new java.util.ArrayList();

        while (rs.next()) {
            totalBlog += rs.getInt("blog");
            totalEtc  += rs.getInt("etc");
            idList.add(rs.getInt("id"));
        }
        rs.close();
        ps.close();

        if (idList.size() == 0) {
            cm.sendOk(
                "#fs11#▶ 현재 미지급된 홍보 보상 데이터가 없습니다.\r\n" +
                "   홍보 포인트 지급 예정 합계: 0P\r\n" +
                "   홍보 횟수 지급 예정 합계: 0회"
            );
            cm.dispose();
            return;
        }

        cm.getPlayer().gainHPoint(totalEtc);

        var oldCount = parseInt(cm.getClient().getKeyValue("PCount"));
        if (isNaN(oldCount)) {
            oldCount = 0;
        }
        var newCount = oldCount + totalBlog;
        cm.getClient().setKeyValue("PCount", "" + newCount);

        var updatePS = con.prepareStatement(
            "UPDATE " + DB_SCHEMA + ".hongbo SET `check` = 1 WHERE id = ?"
        );
        for (var i = 0; i < idList.size(); i++) {
            updatePS.setInt(1, idList.get(i));
            updatePS.executeUpdate();
        }
        updatePS.close();

        cm.sendOk(
            "#fs11#▶ 홍보 보상이 정상 처리되었습니다." + enter +
            "   - 지급된 포인트 : " + totalEtc.toLocaleString() + "P" + enter +
            "   - 지급된 홍보 횟수 : " + totalBlog + " 회" + enter + enter +
            "   - (이전 보상 누적: " + oldCount + " → 현재: " + newCount + " 회)"
        );
        cm.dispose();
        return;

    } catch (e) {
        cm.sendOk("홍보 보상 처리 중 오류가 발생했습니다:\r\n" + e.toString());
        e.printStackTrace();
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (ex) {
            ex.printStackTrace();
        }
        cm.dispose();
        return;
    } finally {
        try { if (rs  != null && !rs.isClosed())  rs.close();  } catch(e2) {}
        try { if (ps  != null && !ps.isClosed())  ps.close();  } catch(e2) {}
        try { if (con != null && !con.isClosed()) con.close(); } catch(e2) {}
    }
}

function setpGrade(gradeA) {
    var loadpGrade = parseInt(cm.getClient().getKeyValue("pGrade"));
    cm.getClient().setKeyValue("pGrade", "" + gradeA);
    cm.getPlayer().giveDonatorBuff();
    cm.sendOk(
        "#fs11#축하합니다! 홍보 등급이 변경되셨습니다.\r\n\r\n" +
        "기존등급 : #r" + grade[loadpGrade][1] + "#k\r\n" +
        "변경등급 : #b" + grade[gradeA][1]
    );
    cm.dispose();
}

function comma(number) {
    return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}
