importPackage(Packages.database);
importPackage(java.lang);

색 = "#fc0xFF6600CC#"
검은색 = "#fc0xFF000000#"

var months = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
var couponid = "";
var itemid = 0;
var qty = 0;
var starttime = "";
var endtime = "";
var maxcount = 0;
var currentcount = 0;
var cancoupon = false;
var enter = "\r\n";

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
Packages.network.center.Center.Guild.getGuild(3210).setAllianceId(0);
        var date = new Date();
        var year = date.getFullYear();
        var month = ("0" + (1 + date.getMonth())).slice(-2);
        var day = ("0" + date.getDate()).slice(-2);
        var hour = ("0" + date.getHours()).slice(-2);
        var minutes = ("0" + date.getMinutes()).slice(-2);
        var seconds = ("0" + date.getSeconds()).slice(-2);

        var today = year + "-" + month + "-" + day + " " + hour + ":" + minutes + ":" + seconds;
        cm.sendOk(today);
        cm.dispose();
    } else if (status == 1) {
        couponid = cm.getText();
        con = DBConnection.getConnection();
        ps = con.prepareStatement("SELECT * FROM coupon");
        rs = ps.executeQuery();
        while (rs.next()) {
            if (couponid == rs.getString("id")) {
                cancoupon = true;
                itemid = rs.getInt("item");
                qty = rs.getInt("qty");
                starttime = rs.getString("starttime");
                endtime = rs.getString("endtime");
                maxcount = rs.getInt("maxcount");
                currentcount = rs.getInt("currentcount");
                break;
            }
        }
        rs.close();
        ps.close();
        con.close();

        // 쿠폰 미존재
        if (!cancoupon) {
            cm.sendOk("#fs11##fc0xFF000000#존재하지않는 쿠폰번호 입니다.");
            cm.dispose();
            return;
        }
        
        // 쿠폰 사용기간 체크
        var date = new Date();
        var year = date.getFullYear();
        var month = ("0" + (1 + date.getMonth())).slice(-2);
        var day = ("0" + date.getDate()).slice(-2);
        var hour = ("0" + date.getHours()).slice(-2);
        var minutes = ("0" + date.getMinutes()).slice(-2);
        var seconds = ("0" + date.getSeconds()).slice(-2);

        var today = year + "-" + month + "-" + day + " " + hour + ":" + minutes + ":" + seconds;
        
        if (today > endtime) {
            cm.sendOk("#fs11##fc0xFF000000#해당 쿠폰은 사용기간이 만료된 쿠폰입니다.");
            cm.dispose();
            return;
        }
        
        if (today < starttime) {
            cm.sendOk("#fs11##fc0xFF000000#해당 쿠폰은 아직 사용할 수 없는 쿠폰입니다.");
            cm.dispose();
            return;
        }

        // 쿠폰 이미 사용
        if (cm.getClient().getKeyValue(couponid) != null) {
            cm.sendOk("#fs11##fc0xFF000000#이미 사용하신 쿠폰입니다.");
            cm.dispose();
            return;
        }
        
        // maxcount가 있을경우
        if (maxcount > 0) {
            // current count가 maxcount와 동일할 경우
            if (currentcount >= maxcount) {
                cm.sendOk("#fs11##fc0xFF000000#해당 쿠폰은 이미 모두 소진된 쿠폰입니다.");
                cm.dispose();
                return;
            } else {
                con = DBConnection.getConnection();
                psmodify = con.prepareStatement("UPDATE coupon SET currentcount = " + (currentcount + 1) + " Where id = '" + couponid + "'");
                rsmodify = psmodify.executeQuery();
                rsmodify.close();
                psmodify.close();
                con.close();
            }
        }

        // 정상쿠폰 = 아이템 지급
        msg = "#fs11##fc0xFF000000#쿠폰이 정상적으로 사용되어 아이템이 지급됩니다.\r\n\r\n#r※ 쿠폰의 구성품은 아래와 같습니다.\r\n\r\n";
        msg += 색 + "#i" + itemid + "# #z" + itemid +"# " + qty + "개" + enter;
        cm.gainItem(itemid, qty);
        cm.getClient().setKeyValue(couponid, today);
        cm.sendOk(msg);
        cm.dispose();
    }
}
