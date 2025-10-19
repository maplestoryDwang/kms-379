importPackage(Packages.constants);
importPackage(Packages.database);
importPackage(java.lang);

// 数字转换函数（参考某博客，此部分版权不属于hawkeye888@nate.com）
function ConvertNumber(number) {
    var inputNumber = number < 0 ? false : number;
    var unitWords = ['', '万 ', '亿 ', '兆 ', '京 '];
    var splitUnit = 10000;
    var splitCount = unitWords.length;
    var resultArray = [];
    var resultString = '';

    if (inputNumber == false) {
        cm.sendOk("发生错误。请重新尝试。\r\n(解析错误)");
        cm.dispose();
        return;
    }

    for (var i = 0; i < splitCount; i++) {
        var unitResult = (inputNumber % Math.pow(splitUnit, i + 1)) / Math.pow(splitUnit, i);
        unitResult = Math.floor(unitResult);
        if (unitResult > 0) {
            resultArray[i] = unitResult;
        }
    }

    for (var i = 0; i < resultArray.length; i++) {
        if (!resultArray[i]) continue;
        resultString = String(resultArray[i]) + unitWords[i] + resultString;
    }

    return resultString;
}

var Time = new Date();
var Year = Time.getFullYear() + "";
var Month = Time.getMonth() + 1 + "";
var Date = Time.getDate() + "";

if (Month < 10) {
    Month = "0" + Month;
}
if (Date < 10) {
    Date = "0" + Date;
}

var Today = parseInt(Year + Month + Date);
var admin = 0;
var rewarddate = 0;
var rank = 0;
var characterid = 0;
var name = "";
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
        /*cm.getPlayer().DamageMeterMap = 450002250;
        cm.getPlayer().DamageMeterMonster = 9300800;
        cm.getPlayer().DamageMeterTime = 30;
        cm.getPlayer().DamageMobX = 200;
        cm.getPlayer().DamageMobY = 100;
        cm.getPlayer().DamageMeterExitMap = 180000000;*/

        var say = "";
        // if (cm.getPlayer().getGMLevel() > 5) {
        say += "#fc0xFF6600CC#\r\n\r\n\r\n   <管理员菜单>\r\n#L4#重置排名#l\r\n\r\n"//#L5#发放排名奖励#l;
        // }

        cm.sendSimple("#fs11##fc0xFF000000#   是否开始伤害测量？\r\n" +
            "   上次记录伤害 : " + cm.getPlayer().DamageMeter + "\r\n   #r※ 排名记录的是最后一次记录，而非最高记录#k\r\n" +
            "#L1##r开始伤害测量 (2分钟)#l\r\n#L2##b查看伤害排名#l" + say + "" +
            "#L6##r退出 (2分钟)#l");

    } else if (status == 1) {
        if (selection == 1) {
            var em = cm.getEventManager("DamageMeter");
            if (em == null) {
                cm.sendOk("发生错误。请重新尝试。");
                cm.dispose();
                return;
            } else if (em.getProperty("entry").equals("false")) {
                cm.sendOk("其他用户正在进行伤害记录。请1分钟后重新尝试。");
                cm.dispose();
                return;
            } else {
                cm.getPlayer().DamageMeter = 0;
                em.startInstance(cm.getPlayer());
                cm.dispose();
            }
        } else if (selection == 2) {
            var con = null;
            var ps = null;
            var rs = null;
            try {
                con = DBConnection.getConnection();
                ps = con.prepareStatement("SELECT * FROM `DamageMeter` WHERE `damage` >= 100000000000000 ORDER BY `damage` DESC");
                rs = ps.executeQuery();
                var count = 0;
                var say = "#fs11##fc0xFF000000#伤害计量器排名。\r\n※ 仅显示100兆以上\r\n\r\n";

                while (rs.next()) {
                    count++;
                    say += count + "位 - #b" + rs.getString("name") + "   #r伤害#fc0xFF000000# : " + ConvertNumber(rs.getLong("damage")) + "\r\n";
                }

                rs.close();
                ps.close();
                con.close();
                cm.sendOk(say);
                cm.dispose();
                return;
            } catch (e) {
                cm.sendOk("尚无记录或发生错误。\r\n" + e);
                cm.dispose();
                return;
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (e) {}
                }
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (e) {}
                }
                if (con != null) {
                    try {
                        con.close();
                    } catch (e) {}
                }
            }
        } else if (selection == 3) {
            admin = 0;
            cm.sendGetNumber("您要查看哪一天的排名？\r\n请按以下格式输入日期。\r\n例) 20200101", 0, 20200101, 99999999);
        } else if (selection == 4 && cm.getPlayer().getGMLevel() > 5) {
            admin = 1;
            cm.sendYesNo("确定要重置伤害计量器排名吗？\r\n所有日期的记录都将被删除！");
        } else if (selection == 5 && cm.getPlayer().getGMLevel() > 5) {
            admin = 2;
            cm.sendGetNumber("您要发放哪一天的排名奖励？\r\n请按以下格式输入日期。\r\n例) 20200101", 0, 20200101, 99999999);
        } else if (selection == 6 ) {
            cm.getPlayer().changeMap(cm.getPlayer().getWarpMap(200000000));
        }
        else {
            cm.dispose();
            return;
        }
    } else if (status == 2) {
        if (admin == 0) {
            var con = null;
            var ps = null;
            var rs = null;
            try {
                con = DBConnection.getConnection();
                ps = con.prepareStatement("SELECT * FROM `DamageMeter` WHERE `date` = " + selection + " ORDER BY `damage` DESC");
                rs = ps.executeQuery();
                var count = 0;
                var say = selection.toString().substring(0,4) + "年 " + selection.toString().substring(4,6) + "月 " + selection.toString().substring(6,8) + "日的伤害计量器排名。\r\n\r\n";

                while (rs.next()) {
                    count++;
                    say += count + "位 - " + rs.getString("name") + "   伤害 : " + ConvertNumber(rs.getLong("damage")) + "\r\n";
                }

                rs.close();
                ps.close();
                con.close();
                cm.sendOk(say);
                cm.dispose();
                return;
            } catch (e) {
                cm.sendOk("尚无记录或发生错误。\r\n" + e);
                cm.dispose();
                return;
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (e) {}
                }
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (e) {}
                }
                if (con != null) {
                    try {
                        con.close();
                    } catch (e) {}
                }
            }
        } else if (admin == 1 && cm.getPlayer().getGMLevel() > 5) {
            var con = null;
            var ps = null;
            try {
                con = DBConnection.getConnection();
                ps = con.prepareStatement("DELETE FROM `DamageMeter`");
                ps.executeUpdate();
                ps.close();
                con.close();
                cm.sendOk("伤害计量器记录重置完成。");
                cm.dispose();
                return;
            } catch (e) {
                cm.sendOk("发生错误。\r\n" + e);
                cm.dispose();
                return;
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (e) {}
                }
                if (con != null) {
                    try {
                        con.close();
                    } catch (e) {}
                }
            }
        } else if (admin == 2 && cm.getPlayer().getGMLevel() > 5) {
            var con = null;
            var ps = null;
            var rs = null;
            try {
                con = DBConnection.getConnection();
                ps = con.prepareStatement("SELECT * FROM `DamageMeter` WHERE `date` = " + selection + " ORDER BY `damage` DESC");
                rs = ps.executeQuery();
                var count = 0;
                rewarddate = selection;
                var say = selection.toString().substring(0,4) + "年 " + selection.toString().substring(4,6) + "月 " + selection.toString().substring(6,8) + "日的伤害计量器排名。\r\n" +
                    "选择相应昵称即可发放排名奖励。\r\n";

                while (rs.next()) {
                    count++;
                    say += "#L" + rs.getInt("id") + "#" + count + "位 - " + rs.getString("name") + "   伤害 : " + ConvertNumber(rs.getLong("damage")) + "\r\n";
                }

                rs.close();
                ps.close();
                con.close();
                cm.sendSimple(say);
            } catch (e) {
                cm.sendOk("尚无记录或发生错误。\r\n" + e);
                cm.dispose();
                return;
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (e) {}
                }
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (e) {}
                }
                if (con != null) {
                    try {
                        con.close();
                    } catch (e) {}
                }
            }
        } else {
            cm.dispose();
            return;
        }
    } else if (status == 3 && cm.getPlayer().getGMLevel() > 5) {
        var con = null;
        var ps = null;
        var rs = null;
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM `DamageMeter` WHERE `date` = " + rewarddate + " ORDER BY `damage` DESC");
            rs = ps.executeQuery();
            var count = 0;
            var say = rewarddate.toString().substring(0,4) + "年 " + rewarddate.toString().substring(4,6) + "月 " + rewarddate.toString().substring(6,8) + "日的 ";

            while (rs.next()) {
                count++;
                if (rs.getInt("id") == selection) {
                    rank = count;
                    characterid = rs.getInt("characterid");
                    name = rs.getString("name");
                    say += rs.getString("name") + "用户的伤害计量器排名。\r\n\r\n";
                    say += count + "位 - 伤害 : " + ConvertNumber(rs.getLong("damage"));
                    say += "\r\n\r\n是否发放排名 " + count + "等的奖励？";
                }
            }

            rs.close();
            ps.close();
            con.close();
            cm.sendYesNo(say);
        } catch (e) {
            cm.sendOk("未选择用户或发生错误。\r\n" + e);
            cm.dispose();
            return;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (e) {}
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (e) {}
            }
            if (con != null) {
                try {
                    con.close();
                } catch (e) {}
            }
        }
    } else if (status == 4 && cm.getPlayer().getGMLevel() > 5) {
        // 第1名 2022424 20个 + 4001126 500个 + 4310024 5个
        // 第2名 2022424 10个 + 4001126 300个 + 4310024 3个
        // 第3名 2022424 5个 + 4001126 100个 + 4310024 1个

        var channel = Packages.handling.world.World.Find.findChannel(characterid);
        if (rank == 1) {
            Packages.handling.channel.handler.DueyHandler.addNewItemToDb(2022424, 20, characterid, "[伤害计量器]", "伤害计量器 " + rank + "等奖励", channel >= 0);
            Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4001126, 500, characterid, "[伤害计量器]", "伤害计量器 " + rank + "等奖励", channel >= 0);
            Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4310024, 5, characterid, "[伤害计量器]", "伤害计量器 " + rank + "等奖励", channel >= 0);
        } else if (rank == 2) {
            Packages.handling.channel.handler.DueyHandler.addNewItemToDb(2022424, 10, characterid, "[伤害计量器]", "伤害计量器 " + rank + "等奖励", channel >= 0);
            Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4001126, 300, characterid, "[伤害计量器]", "伤害计量器 " + rank + "等奖励", channel >= 0);
            Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4310024, 3, characterid, "[伤害计量器]", "伤害计量器 " + rank + "等奖励", channel >= 0);
        } else if (rank == 3) {
            Packages.handling.channel.handler.DueyHandler.addNewItemToDb(2022424, 5, characterid, "[伤害计量器]", "伤害计量器 " + rank + "等奖励", channel >= 0);
            Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4001126, 100, characterid, "[伤害计量器]", "伤害计量器 " + rank + "等奖励", channel >= 0);
            Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4310024, 1, characterid, "[伤害计量器]", "伤害计量器 " + rank + "等奖励", channel >= 0);
        } else {
            cm.sendOk("该用户排名在1~3名之外，没有奖励。");
            cm.dispose();
            return;
        }

        if (channel >= 0) {
            Packages.network.center.Center.Broadcast.sendPacket(characterid, Packages.tools.MaplePacketCreator.sendDuey(28, null, null));
            Packages.network.center.Center.Broadcast.sendPacket(characterid, Packages.tools.MaplePacketCreator.serverNotice(2, "[系统] : 伤害计量器 " + rank + "等奖励已通过快递发放。"));
        }

        cm.sendOk("已向" + name + "用户发放" + rank + "等奖励。");
        cm.dispose();
        return;
    } else {
        cm.dispose();
        return;
    }
}