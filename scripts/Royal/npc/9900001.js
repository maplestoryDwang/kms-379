importPackage(Packages.database);
importPackage(java.lang);

var enter = "\r\n";
var status = -1;
var step = -1;

var targetName = "";
var promoCount = 0;

// ※ 여러분 환경에 맞게 반드시 수정하세요. (HeidiSQL에서 보고 있는 실제 DB 스키마명)
var DB_SCHEMA = "ganglim";  // 예: "ganglim", "mydb" 등

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

    // ───────────────────────────────────────────────────────────────────────────
    // 0) 첫 대화: “유저 닉네임을 입력받기”
    // ───────────────────────────────────────────────────────────────────────────
    if (status == 0) {
        // GM 권한 체크
        if (!cm.getPlayer().isGM()) {
            cm.sendOk("권한이 없습니다.");
            cm.dispose();
            return;
        }
        cm.sendGetText(
            "#fs12#<GM 전용 홍보 기록 남기기>#fs11#" + enter +
            "1) 홍보 횟수를 기록할 유저의 닉네임을 정확히 입력하세요:\n\n" +
            "(예: 홍보받을유저닉네임)"
        );
        step = 1;
    }
    // ───────────────────────────────────────────────────────────────────────────
    // 1) 닉네임 입력 후 → “홍보 횟수 입력받기”
    // ───────────────────────────────────────────────────────────────────────────
    else if (status == 1) {
        if (step != 1) {
            cm.sendOk("예상치 못한 오류가 발생했습니다.");
            cm.dispose();
            return;
        }
        targetName = cm.getText().trim();
        if (targetName.length < 1) {
            cm.sendOk("유효한 닉네임을 입력해주세요.");
            cm.dispose();
            return;
        }
        cm.sendGetText(
            "🔹 [" + targetName + "] 님에게 기록할 홍보 횟수를 입력하세요:\n\n" +
            "(예: 3)"
        );
        step = 2;
    }
    // ───────────────────────────────────────────────────────────────────────────
    // 2) 홍보 횟수 입력 후 → 실제 INSERT 처리
    // ───────────────────────────────────────────────────────────────────────────
    else if (status == 2) {
        if (step != 2) {
            cm.sendOk("예상치 못한 흐름 오류가 발생했습니다.");
            cm.dispose();
            return;
        }
        var txt = cm.getText().trim();
        if (!/^\d+$/.test(txt)) {
            cm.sendOk("숫자만 입력해주세요.");
            cm.dispose();
            return;
        }
        promoCount = parseInt(txt);
        if (promoCount <= 0) {
            cm.sendOk("홍보 횟수는 1회 이상이어야 합니다.");
            cm.dispose();
            return;
        }

        // GM 권한 재확인
        if (!cm.getPlayer().isGM()) {
            cm.sendOk("권한이 없습니다.");
            cm.dispose();
            return;
        }

        // 이제 DB에 INSERT
        insertHongboRecord(targetName, promoCount);
        return;
    }
    // ───────────────────────────────────────────────────────────────────────────
    // 그 외의 status 값이면 종료
    // ───────────────────────────────────────────────────────────────────────────
    else {
        cm.dispose();
    }
}


// ───────────────────────────────────────────────────────────────────────────
// 함수: hongbo 테이블에 새로운 “홍보 기록”을 남기는 실제 DB 처리
// ───────────────────────────────────────────────────────────────────────────
function insertHongboRecord(nickname, count) {
    var con    = null;
    var ps     = null, rs    = null;
    var psIns  = null;
    try {
        con = DBConnection.getConnection();

        // 1) INSERT 시 사용할 캐릭터 ID(cId)를 찾기 위해, characters 테이블 조회
        ps = con.prepareStatement(
            "SELECT id FROM " + DB_SCHEMA + ".characters WHERE name = ?"
        );
        ps.setString(1, nickname);
        rs = ps.executeQuery();
        if (!rs.next()) {
            // 해당 닉네임의 캐릭터가 존재하지 않음
            rs.close();
            ps.close();
            cm.sendOk("DB에서 [" + nickname + "] 님을 찾을 수 없습니다.");
            cm.dispose();
            return;
        }
        var charId = rs.getInt("id");
        rs.close();
        ps.close();

        // 2) 홍보 포인트(etc) 계산 (예: 회당 50,000P)
        var pointValue = count * 50000;

        // 3) hongbo 테이블에 INSERT
        //    → date 컬럼에는 MySQL NOW() 함수를 사용해서 서버 시각을 자동으로 넣음
        psIns = con.prepareStatement(
            "INSERT INTO " + DB_SCHEMA + ".hongbo " +
            "(`name`, `check`, `youtube`, `blog`, `etc`, `comment`, `date`, `cid`) " +
            "VALUES (?, 0, 0, ?, ?, '', NOW(), ?)"
        );
        psIns.setString(1, nickname);        // name
        psIns.setInt(2, count);              // blog = 홍보 횟수
        psIns.setInt(3, pointValue);         // etc  = 홍보 포인트
        psIns.setInt(4, charId);             // cid  = characterId

        var inserted = psIns.executeUpdate();
        psIns.close();

        if (inserted > 0) {
            cm.sendOk(
                "#fs11#▶ GM 홍보 기록이 DB에 정상 등록되었습니다.\r\n\r\n" +
                "   닉네임 : " + nickname + enter +
                "   홍보 횟수 : " + count + " 회" + enter +
                "   지급 예정 홍보 포인트 : " + pointValue.toLocaleString() + "P" + enter +
                "   characterId (cid) : " + charId + enter +
                "   등록 시각 (DB서버 현재 시각) : NOW()"
            );
        } else {
            cm.sendOk("오류: 레코드가 한 건도 삽입되지 않았습니다.");
        }
        cm.dispose();
        return;

    } catch (e) {
        cm.sendOk("홍보 기록을 DB에 남기는 중 오류가 발생했습니다:\r\n" + e.toString());
        e.printStackTrace();
        try {
            if (con != null && !con.isClosed()) con.close();
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
