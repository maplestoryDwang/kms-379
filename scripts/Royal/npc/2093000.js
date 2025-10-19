importPackage(java.sql);

importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);

importPackage(Packages.database);

색 = "#fc0xFF0FA400#"
검은색 = "#fc0xFF000000#"
핑크색 ="#fc0xFFFF3366#"

function start() {
    var i = 0;
    var a = 0;
    var b = 0;
    var print = new StringBuilder();
    var con = DBConnection.getConnection();
    var ps = con.prepareStatement("SELECT name, level, job, levelUpTime FROM characters WHERE gm = 0 ORDER BY level DESC, exp DESC, levelUpTime ASC LIMIT 100");
    var rs = ps.executeQuery();

    while (rs.next()) {
        i++;
        chrname = rs.getString("name");
        job = Packages.constants.GameConstants.getJobNameById(rs.getInt("job"));
        level = rs.getInt("level");
        date = rs.getDate("levelUpTime");
    
        if (i < 10) {
            aas = "#e00#fc0xFF09A17F#" + i + 검은색 + "#n위 | ";
        } else if (i < 100) {
            aas = "#e0#fc0xFF09A17F#" + i + 검은색 + "#n위 | ";
        } else {
            aas = "#e#fc0xFF09A17F#" + i + 검은색 + "#n위 | ";
        }

        
        if (rs.getInt("level") == 777) {
            print.append("#fc0xFFb2b2b2#"+aas).append("#b").append(chrname).append("#fc0xFF000000# | #r레벨 : ").append("#fc0xFFFF3366##e").append(level).append("#n");
            print.append("#fc0xFF000000# | #fc0xFF6600CC#").append(job).append("#fc0xFF000000# | ").append(date).append("\r\n");
        } else {
            print.append("#fc0xFFb2b2b2#"+aas).append("#b").append(chrname).append("#fc0xFF000000# | #r레벨 : ").append("#fc0xFF000000#").append(level);
            print.append("#fc0xFF000000# | #fc0xFF6600CC#").append(job).append("\r\n");
        }
        //print.append("#fc0xFF000000# | #fc0xFF6600CC#직업 : #fc0xFF000000#").append(job).append("\r\n");
    }

    //cm.sendOk("#fs11#만렙(300) 이상시 #r초월레벨#fc0xFF000000#이 증가하며\r\n레벨옆 #b파란색#fc0xFF000000# 수치가 #r초월레벨#fc0xFF000000#입니다\r\n※ 초월레벨은 #r초기화#fc0xFF000000# 될 수 있습니다\r\n\r\n"+print.toString());
    cm.sendOk("#fs11##fc0xFF000000#랭킹 순서는 아래와 같이 정렬됩니다\r\n#b레벨 -> 경험치 -> 해당레벨달성일자\r\n\r\n"+print.toString());
    rs.close();
    ps.close();
    con.close();
    cm.dispose();
}