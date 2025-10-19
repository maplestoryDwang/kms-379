importPackage(Packages.database);

var 모험가 = [ //4차 이상만 뜨게끔 유도
    // 모험가
    [112, "히어로"], [122, "팔라딘"], [132, "다크나이트"],
    [212, "아크메이지(불,독)"], [222, "아크메이지(썬,콜)"], [232, "비숍"], 
    [312, "보우마스터"], [322, "신궁"], [332, "패스파인더"], 
    [412, "나이트로드"], [422, "섀도어"], [434, "듀얼블레이더"],
    [512, "바이퍼"], [522, "캡틴"], [532, "캐논슈터"],
    // 시그너스
    [1112, "소울마스터"], [1212, "플레임위자드"], [1312, "윈드브레이커"], [1412, "나이트워커"], [1512, "스트라이커"], [5112, "미하일"],
    // 영웅
    [2112, "아란"], [2217, "에반"], [2312, "메르세데스"], [2412, "팬텀"], [2512, "은월"], [2712, "루미너스"],
    // 레지스탕스, 데몬
    [3112, "데몬슬레이어"], [3122, "데몬어벤져"], [3212, "배틀메이지"], [3312, "와일드헌터"], [3512, "메카닉"], [3612, "제논"], [3712, "블래스터"],
    // 노바
    [6112, "카이저"], [6312, "카인"], [6412, "카데나"], [6512, "엔젤릭버스터"],
    // 레프
    [15112, "아델"], [15212, "일리움"], [15512, "아크"], [15412, "칼리"],
    // 아니마
    [16412, "호영"], [16212, "라라"],
    // 프렌즈 월드
    [14212, "키네시스"],
    // 초월자
    [10112, "제로"], 
];

색 = "#fc0xFF6600CC#"
검은색 = "#fc0xFF000000#"
핑크색 ="#fc0xFFFF3366#"

var choice = 0;

var status = -1;

function start() {
    status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode != 1) {
	    cm.dispose();
	    return;
    }
    if (mode == 1) {
        status++;
    }
	if (status == 0) {
        var say = "#fs11#직업별 레벨 랭킹입니다.\r\n보기 원하시는 직업의 랭킹을 선택해주세요.\r\n\r\n";
        for (var i = 0; i < 모험가.length; i++) {
            say += "#L" + i + "#" + 모험가[i][1] + "#l\r\n";
        }
		cm.sendSimple(say);
	} else if (status == 1) {
        var con = null;
        var ps = null;
        var rs = null;
        var count = 0;
        var say = "#fs11##fc0xFF000000#" + 모험가[selection][1] + " 직업의 레벨 랭킹입니다.\r\n\r\n랭킹 순서는 아래와 같이 정렬됩니다\r\n#b레벨 -> 경험치 -> 해당레벨달성일자\r\n\r\n";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement("SELECT name, level FROM `characters` WHERE `job` = " + 모험가[selection][0] + " ORDER BY `level` DESC, `exp` DESC, `levelUpTime` ASC LIMIT 100"); //AND `gm` = 0 
            rs = ps.executeQuery();
            while (rs.next()) {
                count++;
                if (count < 10) {
                    aas = "#e00#fc0xFF09A17F#" + count + 검은색 + "#n위 | ";
                } else if (count < 100) {
                    aas = "#e0#fc0xFF09A17F#" + count + 검은색 + "#n위 | ";
                } else {
                    aas = "#e#fc0xFF09A17F#" + count + 검은색 + "#n위 | ";
                }

                say += "#fc0xFFB2B2B2#" + aas + "#b" + rs.getString("name") + "#k | #r레벨 : #fc0xFF000000#" + rs.getInt("level") + "\r\n";
            }
            rs.close();
            ps.close();
            con.close();
            cm.sendOk(say);
            cm.dispose();
            return;
        } catch (e) {
            cm.sendOk("오류가 발생하였습니다.\r\n\r\n" + e);
            cm.dispose();
            return;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (con != null) {
                con.close();
            }
        }
    } else {
        cm.dispose();
        return;
    }
}
