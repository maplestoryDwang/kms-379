importPackage(Packages.database);

importPackage(java.text);
var enter = "\r\n";
var seld = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

var nf = NumberFormat.getInstance();

색 = "#fc0xFF6600CC#"
검은색 = "#fc0xFF000000#"
핑크색 ="#fc0xFFFF3366#"
회색 = "#fc0xFFB2B2B2#"

var grade = [
    [0, "일반"],
    [1, "Grade D"],
    [2, "Grade C"],
    [3, "Grade B"],
    [4, "Grade A"],
    [5, "Grade S"],
    [6, "Grade SS"],
    [7, "Grade SSS"],
    [8, "Grade SSS+"],
    [9, "MVP PLATINUM"],
    [10, "MVP LUXURY"],
    [11, "MVP NOBLE"],
    [12, "MVP CROWN"],
    [13, "MVP PRESTIGE"],
    [14, "MVP ROYAL"],
    [15, "MVP ROYAL+"]
]

var pgrade = [
    [0, "Lv.0 일반"],
    [1, "Lv.1 비기닝"],
    [2, "Lv.2 라이징"],
    [3, "Lv.3 플라잉"],
    [4, "Lv.4 샤이닝"],
    [5, "Lv.5 아이돌"],
    [6, "Lv.6 슈퍼스타"]
]

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

        var msg = "#fs11##fc0xFF990033##e[강림월드]#n#fc0xFF000000#의 각종 랭킹을 확인할 수 있는 게시판입니다.#b" + enter + enter;
        msg += "#L1#레벨 랭킹" + enter;
        msg += "#L8#직업 랭킹" + enter;
        //msg += "#L6# 보스헌터 랭킹"+enter;
        //msg += "#L2# 메소 랭킹"+enter;
        msg += "#L4#길드 랭킹" + enter;
        msg += "#L3#인기도 랭킹" + enter;
        msg += "#L5#무릉도장 랭킹"+enter;
        //msg += "#L7# PVP 대결 랭킹"+enter;
        msg += "#L9#전투력 측정 랭킹#l" + enter;
        msg += "#L10#누적 홍보횟수 랭킹#l" + enter;
        cm.sendSimple(msg);
    } else if (status == 1) {
        seld = sel;
        switch (seld) {
            case 1:
                cm.dispose();
                cm.openNpc(2093000);
                break;
            case 2:
                cm.dispose();
                cm.openNpc(2094000);
                break;
            case 3:
                cm.sendOk(getRank("SELECT id, name, fame FROM characters WHERE `fame` > 0 AND `gm` = 0 ORDER BY `fame` DESC LIMIT ", "fame", "인기도", 50));
                cm.dispose();
                break;
            case 4:
                cm.sendOk(getRankGuild("SELECT name, GP FROM guilds WHERE `GP` > 0 AND `GP` < 250000000 ORDER BY `GP` DESC LIMIT ", "GP", "보유 GP", 50));
                cm.dispose();
                break;
            case 5:
	     cm.showDojangRanking();
	     cm.getPlayer().dropMessage(5, "무릉도장 기록이 순위표에 반영되기까지 시간이 걸릴 수 있습니다. 정산 포인트는 매주 랭킹 정산 이후 전체랭킹 기준으로 지급됩니다.");
	     cm.dispose();
                break;
            case 6:
                cm.sendOk(getRank("SELECT * FROM characters WHERE `basebpoint` > 0 AND `gm` = 0 ORDER BY `basebpoint` DESC LIMIT ", "basebpoint", "누적포인트", 10));
                cm.dispose();
                break;
            case 7:
                cm.sendOk(getRank3("SELECT * FROM characters WHERE `willPVPCount` > 0 AND `gm` = 0 ORDER BY `willPVPCount` DESC LIMIT ", "willPVPCount", "WIN", 10, "faillPVPCount"));
                cm.dispose();
                break;
            case 8:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 9076004, "직업랭킹");
                break;
            case 9:
                  var msg = "#fs11##fc0xFF990033##e[강림월드]#n#fc0xFF000000#의 각종 랭킹을 확인할 수 있는 게시판입니다.#b" + enter + enter;
                msg += "#L1#전체 랭킹" + enter;
                msg += "#L2#직업 랭킹" + enter;
                cm.sendSimple(msg);
                //cm.sendOk(getRankDamage("SELECT `player_id`, `damage`, `name`, `job` FROM damage_measurement_rank ORDER BY damage DESC LIMIT ", "damage", "", 100));
                //cm.dispose();
                break;
            case 10:
                cm.sendOk(getRankPCount("SELECT `id`, `KEY`, `value` FROM acckeyvalue WHERE `value` > 0 AND `key` = 'PCount' ORDER BY CAST(`value` AS unsigned) DESC LIMIT ", "value", "누적 홍보횟수", 100));
                cm.dispose();
                break;
        }
    } else if (status == 2) {
        switch (seld) {
            case 9:
                if (sel == 1) {
                    cm.sendOk(getRankDamage("SELECT `player_id`, `damage`, `name`, `job` FROM damage_measurement_rank ORDER BY damage DESC LIMIT ", "damage", "", 100));
                    cm.dispose();
                    break;
                } else if (sel == 2) {
                    cm.dispose();
                    cm.openNpcCustom(cm.getClient(), 9000213, "직업데미지랭킹");
                    break;
                }
                break;
        }
    }
}

function getRank(v1, v2, v3, v4) {
    var ret = "#fs11#랭킹은 최대 #r" + v4 + "위#k 까지만 보여집니다.\r\n\r\n";
    var as = 0;
    var names = [];
    var name = "";

    var con = DBConnection.getConnection();
    var ps = con.prepareStatement(v1 + v4);
    var rs = ps.executeQuery();

    while (rs.next()) {
        as += 1;
        if (as < 10) {
            aas = "#e00#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        } else if (as < 100) {
            aas = "#e0#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        } else {
            aas = "#e#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        }
        var pssearchaccid = con.prepareStatement("SELECT banned FROM accounts Where id in (SELECT accountid FROM characters Where id = " + rs.getInt('id') + ")");
        var rssearchaccid = pssearchaccid.executeQuery();
        
        while (rssearchaccid.next()) {
            var banned = rssearchaccid.getInt("banned");
        }
        
        name = rs.getString('name');
        
        if (name == null) {
            name = "#r삭제된 캐릭터";
        }
        
        if (banned > 0) {
            name = "#r이용정지";
        }
        
        ret += 회색 + aas + "#b" + name + "#fc0xFF000000# | " + v3 + " : " + 핑크색 + rs.getInt(v2) + "#k\r\n";
    }
    rs.close();
    ps.close();
    con.close();

    if (ret.equals("")) return "랭킹이 없습니다.";
    return ret;
}

function getRankGuild(v1, v2, v3, v4) {
    var ret = "#fs11#랭킹은 최대 #r" + v4 + "위#k 까지만 보여집니다.\r\n\r\n";
    var as = 0;
    var names = [];
    var name = "";

    var con = DBConnection.getConnection();
    var ps = con.prepareStatement(v1 + v4);
    var rs = ps.executeQuery();

    while (rs.next()) {
        as += 1;
        if (as < 10) {
            aas = "#e00#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        } else if (as < 100) {
            aas = "#e0#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        } else {
            aas = "#e#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        }

        name = rs.getString('name');
        
        ret += 회색 + aas + "#b" + name + "#fc0xFF000000# | " + v3 + " : " + 핑크색 + rs.getInt(v2) + "#k\r\n";
    }
    rs.close();
    ps.close();
    con.close();

    if (ret.equals("")) return "랭킹이 없습니다.";
    return ret;
}

function getRankPCount(v1, v2, v3, v4) {
    var ret = "#fs11#랭킹은 최대 #r" + v4 + "위#k 까지만 보여집니다.\r\n";
    var as = 0;
    var names = [];

    var con = DBConnection.getConnection();
    var ps = con.prepareStatement(v1 + v4);
    var rs = ps.executeQuery();

    
    while (rs.next()) {
        as += 1;
        if (as < 10) {
            aas = "#e00#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        } else if (as < 100) {
            aas = "#e0#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        } else {
            aas = "#e#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        }

        var ps2 = con.prepareStatement("SELECT `name` FROM characters WHERE accountid = " + rs.getString("id") + " ORDER BY `mainchr` DESC, `level` DESC LIMIT 1");
        var rs2 = ps2.executeQuery();
        var ps3 = con.prepareStatement("SELECT `value` FROM acckeyvalue WHERE id = " + rs.getString("id") + " AND `key` = 'pGrade'");
        var rs3 = ps3.executeQuery();
        
        // 캐릭터 닉네임 불러오기
        var findchr = false;


        
        while (rs2.next()) {
            var pssearchaccid = con.prepareStatement("SELECT banned FROM accounts Where id = " + rs.getString("id"));
            var rssearchaccid = pssearchaccid.executeQuery();
            
            while (rssearchaccid.next()) {
                var banned = rssearchaccid.getInt("banned");
            }
            
            if (banned > 0) {
                ret += 회색 + aas + "#r이용정지";
            } else {
                ret += 회색 + aas + "#b" + rs2.getString("name");
            }
            findchr = true;
        }

        // 캐릭터가 없을경우
        if (!findchr)
            ret += 회색 + aas + "#r이용정지";


        // 홍보 등급 불러오기 & 작성
        while (rs3.next()) {
            ret += 검은색 + " | " + v3 + " : " + 색 + nf.format(rs.getInt(v2)) + 검은색 + " | " + 핑크색 + "[" + pgrade[rs3.getInt("value")][1] + "]#k\r\n";
        }
    }

    rs.close();
    ps.close();
    rs2.close();
    ps2.close();
    rs3.close();
    ps3.close();
    con.close();
    
    if (ret.equals("")) return "랭킹이 없습니다.";
    return ret;
}

function getRankDPoint(v1, v2, v3, v4) {
    var ret = "#fs11#랭킹은 최대 #r" + v4 + "위#k 까지만 보여집니다.\r\n";
    ret += 핑크색 + "※ 대표캐릭터 선정 방식\r\n";
    ret += 핑크색 + "ㄴ 디스코드 [봇 명령어] 채널에서 설정 가능\r\n";
    ret += 핑크색 + "ㄴ 설정하지않을시 계정내 레벨순서 (레벨이같다면 생성순서)\r\n\r\n";
    var as = 0;
    var names = [];

    var con = DBConnection.getConnection();
    var ps = con.prepareStatement(v1 + v4);
    var rs = ps.executeQuery();

    
    while (rs.next()) {
        as += 1;
        if (as < 10) {
           aas = "#e00#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        } else if (as < 100) {
           aas = "#e0#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        } else {
           aas = "#e#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        }
        
        var ps2 = con.prepareStatement("SELECT `name` FROM characters WHERE accountid = " + rs.getString("id") + " ORDER BY `mainchr` DESC, `level` DESC LIMIT 1");
        var rs2 = ps2.executeQuery();
        var ps3 = con.prepareStatement("SELECT `value` FROM acckeyvalue WHERE id = " + rs.getString("id") + " AND `key` = 'hGrade'");
        var rs3 = ps3.executeQuery();
        
        // 캐릭터 닉네임 불러오기
        var findchr = false;
        var findgrade = false;

        while (rs2.next()) {
            ret += 회색 + aas + "#b" + rs2.getString("name");
            findchr = true;
        }

        // 캐릭터가 없을경우
        if (!findchr)
            ret += 회색 + aas + "#r이용정지";


        // MVP 등급 불러오기 & 작성
        while (rs3.next()) {
            ret += 검은색 + " | " + v3 + " : " + 색 + nf.format(rs.getInt(v2)) + 검은색 + " | " + 핑크색 + "[" + grade[rs3.getInt("value")][1] + "]#k\r\n";
            findgrade = true;
        }

        // MVP 등급 없을경우
        if (!findgrade)
            ret += 검은색 + " | " + v3 + " : " + 색 + nf.format(rs.getInt(v2)) + "#k\r\n";

    }

    rs.close();
    ps.close();
    rs2.close();
    ps2.close();
    rs3.close();
    ps3.close();
    con.close();
    
    if (ret.equals("")) return "랭킹이 없습니다.";
    return ret;
}

function getRankCashEn(v1, v2, v3, v4) {
    var ret = "#fs11#랭킹은 최대 #r" + v4 + "위#k 까지만 보여집니다.\r\n\r\n";
    var as = 0;
    var names = [];

    var con = DBConnection.getConnection();
    var ps = con.prepareStatement(v1 + v4);
    var rs = ps.executeQuery();
    
    while (rs.next()) {
        as += 1;
        if (as < 10) {
           aas = "#e00#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        } else if (as < 100) {
           aas = "#e0#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        } else {
           aas = "#e#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        }
        
        var ps2 = con.prepareStatement("SELECT `name` FROM characters WHERE id = " + rs.getString("id") + " ORDER BY `mainchr` DESC, `level` DESC LIMIT 1");
        var rs2 = ps2.executeQuery();

        var ps3 = con.prepareStatement("SELECT `value` FROM keyvalue WHERE id = " + rs.getString("id") + " AND `key` = 'cashatk'");
        var rs3 = ps3.executeQuery();
        
        // 캐릭터 닉네임 불러오기
        var findchr = false;

        while (rs2.next()) {
            ret += 회색 + aas + "#b" + rs2.getString("name");
            findchr = true;
        }

        // 캐릭터가 없을경우
        if (!findchr)
            ret += 회색 + aas + "#r이용정지";
        while (rs3.next()) {
            ret += 검은색 + " | " + v3 + " : " + 색 + nf.format(rs.getInt(v2)) + 검은색 + " | Atk : " + 색 + nf.format(rs3.getInt(v2)) + "#k\r\n";
        }

    }

    rs.close();
    ps.close();
    rs2.close();
    ps2.close();
    con.close();
    
    if (ret.equals("")) return "랭킹이 없습니다.";
    return ret;
}

function getRankDamage(v1, v2, v3, v4) {
    var ret = "#fs11#랭킹은 최대 #r100위#k 까지만 보여집니다.\r\n";
    ret += 핑크색 + "※ 데미지는 억 단위부터 표기됩니다\r\n\r\n";
    var as = 0;
    var names = [];

    var con = DBConnection.getConnection();
    var ps = con.prepareStatement(v1 + v4);
    var rs = ps.executeQuery();
    
    var psmodify = con.prepareStatement("UPDATE damage_measurement_rank SET job = (SELECT job FROM characters Where id = player_id), name = (SELECT name FROM characters Where id = player_id)");
    var rsmodify = psmodify.executeQuery();
    
    while (rs.next()) {
        as += 1;
        if (as < 10) {
           aas = "#e00#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        } else if (as < 100) {
           aas = "#e0#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        } else {
           aas = "#e#fc0xFF09A17F#" + as + 검은색 + "#n위 | ";
        }
        
        name = rs.getString("name");
        job = Packages.constants.GameConstants.getJobNameById(rs.getInt("job"));
        damage = ConvertNumber(rs.getString(v2));
        playerid = rs.getInt("player_id");
        
        var pssearchaccid = con.prepareStatement("SELECT banned FROM accounts Where id in (SELECT accountid FROM characters Where id = " + playerid + ")");
        var rssearchaccid = pssearchaccid.executeQuery();
        
        while (rssearchaccid.next()) {
            var banned = rssearchaccid.getInt("banned");
        }
        
        if (name == null) {
            name = "#r삭제된 캐릭터";
        }

/* 정지캐릭 안뜨게하게
        if (banned > 0) { // 이용정지 캐릭터는 표기X
            name = "#r이용정지";
            as -= 1;
        } else {
            ret += 회색 + aas + "#b" + name;
            ret += 검은색 + " | 직업 : "  + 색 + job + " | #e" + 검은색 + damage + 검은색 + "#k#n\r\n";
        }

        if (as == 100) {
            break;
        }
*/

        if (banned > 0) { // 이용정지 캐릭터는 표기X
            name = "#r이용정지";
        }

        ret += 회색 + aas + "#b" + name;
        ret += 검은색 + " | 직업 : "  + 색 + job + " | #e" + 검은색 + damage + 검은색 + "#k#n\r\n";



    }

    rs.close();
    ps.close();
    rsmodify.close();
    psmodify.close();
    con.close();
    
    if (ret.equals("")) return "랭킹이 없습니다.";
    return ret;
}

function getRank3(v1, v2, v3, v4, v5) {
    var ret = "#fs11#랭킹은 최대 #r" + v4 + "명#k 까지만 보여집니다.\r\n\r\n";
    var as = 0;
    var names = [];

    var con = DBConnection.getConnection();
    var ps = con.prepareStatement(v1 + v4);
    var rs = ps.executeQuery();

    while (rs.next()) {
        as += 1;
        ret += as + ". #b" + rs.getString("name") + "#k " + v3 + " : #d" + rs.getInt(v2) + " / #kLOSE :#d " + rs.getInt(v5) + " #k\\r\n";
    }
    rs.close();
    ps.close();
    con.close();

    if (ret.equals("")) return "랭킹이 없습니다.";
    return ret;
}

function ConvertNumber(number) { //모 블로그 참조함, 이 부분에 대해서는 키네시스(kinesis8@nate.com), 라피스#2519 에게 저작권이 없음
    var inputNumber  = number < 0 ? false : number;
    var unitWords    = ['', '만 ', '억 ', '조 ', '경 '];
    var splitUnit    = 10000;
    var splitCount   = unitWords.length;
    var resultArray  = [];
    var resultString = '';
    if (inputNumber == false) {
        cm.sendOk("#fs11#오류가 발생하였습니다. 다시 시도해 주세요.\r\n(파싱오류)");
        cm.dispose();
        return;
    }
    for (var i = 0; i < splitCount; i++) {
        var unitResult = (inputNumber % Math.pow(splitUnit, i + 1)) / Math.pow(splitUnit, i);
        unitResult = Math.floor(unitResult);
        if (unitResult > 0){
            resultArray[i] = unitResult;
        }
    }
    for (var i = 2; i < resultArray.length; i++) {
        if(!resultArray[i]) continue;
        resultString = String(resultArray[i]) + unitWords[i] + resultString;
    }
    return resultString;
}