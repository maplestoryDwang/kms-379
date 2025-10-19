importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);
importPackage(java.text);
importPackage(java.awt);

importPackage(Packages.network.models);
importPackage(Packages.scripting);
importPackage(Packages.constants);
importPackage(Packages.database);

var status = -1;
var jobCode = 0;
var secondJob = 0;
var adventure = false;
var type_ = -1;
var isenglish = false;


importPackage(Packages.constants);

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        if (cm.getPlayer().getLevel() >= 100 && cm.getPlayer().getJob() != 10112) {
            cm.dispose();
            cm.warp(ServerConstants.TownMap, 0);
            return;
        }

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
            isenglish = true;
            status = 0;
            action(1, 0, 0);
            return;
        }

        // var text = "#fUI/Basic.img/RoyalBtn/StartImg/0#";
        var text = "input->[agree]";

        cm.sendGetText(text);
    } else if (status == 1) {
        var text = cm.getText();

        if (text != "agree" && !isenglish) {
            cm.dispose();
            cm.sendOk("#fs11#如果不同意，就没有办法帮你了\r\n#b如果同意#k，请再次输入“#r#e同意#k#n”");
            return;
        }

        var v0 = "#b#h0##k，请在以下职业中选择您想要的职业。#n\r\n\r\n";
        var job = cm.getPlayer().getJob();
        if (job == 0) { // 모험가(초보자)
            if (cm.getPlayer().getSubcategory() == 1) { // 듀얼블레이드
                v0 += "#b#L400#以双刀片#k开始。#l\r\n";
            } else if (cm.getPlayer().getSubcategory() == 2) { // 캐논슈터
                v0 += "#b#L501#以佳能射手#k开始。#l\r\n";
            } else if (cm.getPlayer().getSubcategory() == 3) { // 패스파인더
                v0 += "#b#L301#以探路者#k开始。#l\r\n";
            } else {
                v0 += "#b#L100#以战士#k开始。#l\r\n";
                v0 += "以#b#L200#向导#k开始。#l\r\n";
                v0 += "#b#L300#以弓手#k开始。#l\r\n";
                v0 += "#b#L400#以盗贼#k开始。#l\r\n";
                v0 += "#b#L500#以海盗#k开始。#l\r\n";
            }
        } else if (job == 1000) { // 시그너스(노블레스)
            v0 += "#b#L1100#以灵魂大师#k开始。#l\r\n";
            v0 += "#b#L1200#以花式魔法师#k开始。#l\r\n";
            v0 += "#b#L1300#Windbreaker#k开始。#l\r\n";
            v0 += "#b#L1400#以夜行侠#k开始。#l\r\n";
            v0 += "以#b#L1500#前锋#k开始。#l\r\n";
        } else if (job == 5000) { // 미하일
            v0 += "#b#L5100#以米哈伊尔#k开始。#l\r\n";
        } else if (job == 2000) { // 영웅(레전드)
            v0 += "#b#L2100#以阿兰#k开始。#l\r\n";
        } else if (job == 2001) { // 영웅(에반)
            v0 += "以#b#L2200#埃文#k开始。#l\r\n";
        } else if (job == 2002) { // 영웅(메르세데스)
            v0 += "#b#L2300#以梅赛德斯#k开始。#l\r\n";
        } else if (job == 2003) { // 영웅(팬텀)
            v0 += "#b#L2400#以幻影#k开始。#l\r\n";
        } else if (job == 2004) { // 영웅(루미너스)
            v0 += "#b#L2700#以Luminus#k开始。#l\r\n";
        } else if (job == 2005) { // 영웅(은월)
            v0 += "#b#L2500#以月#k开始。#l\r\n";
        } else if (job == 3000) { // 레지스탕스(시티즌)
            v0 += "#b#L3200#以对决明治#k开始。#l\r\n";
            v0 += "#b#L3300#以狂野猎人#k开始。#l\r\n";
            v0 += "#b#L3500#以机械#k开始。#l\r\n";
            v0 += "#b#L3700#以Blaster#k开始。#l\r\n";
        } else if (job == 3001) { // 데몬
            v0 += "#b#L3100#以守护者#k开始。#l\r\n";
            v0 += "#b#L3101#守护天使[不推荐]#k开始。#l\r\n";
        } else if (job == 3002) { // 제논
            v0 += "#b#L3600#以氙气#k开始。#l\r\n";
        } else if (job == 6000) { // 카이저
            v0 += "#b#L6100#以凯撒#k开始。#l\r\n";
        } else if (job == 6001) { // 엔젤릭버스터
            v0 += "#b#L6500#以Angelic Buster#k开始。#l\r\n";
        } else if (job == 6002) { // 카데나
            v0 += "#b#L6400#以学院#k开始。#l\r\n";
        } else if (job == 10112) { // 제로
            v0 += "#b#L10112#以Zero[非推荐]#k开始。#l\r\n"
        } else if (job == 14000) { // 키네시스
            v0 += "#b#L14200#以Kinesis#k开始。#l\r\n"
        } else if (job == 15000) { // 일리움
            v0 += "以#b#L15200#以太坊#k开始。#l\r\n"
        } else if (job == 15001) { // 아크
            v0 += "#b#L15500#以ARK#k开始。#l\r\n"
        } else if (job == 16000) { // 호영
            v0 += "#b#L16400#以浩英#k开始。#l\r\n"
        } else if (job == 15002) { // 아델
            v0 += "#b#L15100#以Adel#k开始。#l\r\n"
        } else if (job == 6003) { // 카인
            v0 += "#b#L6300#Kain#k开始。#l\r\n";
        } else if (job == 16001) { // 라라
            v0 += "#b#L16200#以Lara#k开始。#l\r\n"
        } else if (job == 15003) { // 칼리
            v0 += "#b#L15400#以卡利#k开始。#l\r\n"
        }
        cm.askMenu(v0, 1, GameObjectType.User, ScriptMessageFlag.NoEsc, ScriptMessageFlag.BigScenario);
    } else if (status == 2) {
        jobCode = selection;
        if (selection == 100 || selection == 200 || selection == 300 || selection == 400 && cm.getPlayer().getSubcategory() != 1 || selection == 500) {
            adventure = true;
            var v0 = "请选择您希望的第二职业。达到适当等级后，自动进行转职。#b\r\n\r\n";
            if (selection == 100) {
                v0 += "#L110#斗士（第四职业：英雄）#l\r\n";
                v0 += "#L120#页面（第四职业：帕拉丁）#l\r\n";
                v0 += "#L130#斯皮尔曼（第四职业：黑暗）#l\r\n";
            } else if (selection == 200) {
                v0 += "#L210#魔法师（火，毒）（第4职业：亚克梅吉（火，毒））#l\r\n";
                v0 += "#L220#魔法师（Sun，Call）（第4职业：Acmage（Sun，Call））#l\r\n";
                v0 += "#L230#克莱里克（第四职业：毕晓普）#l\r\n";
            } else if (selection == 300) {
                v0 += "#L310#猎人（第四职业：弓箭手）#l\r\n";
                v0 += "#L320#射手（第4职业：神宫）#l\r\n";
            } else if (selection == 400) {
                v0 += "#L410#刺客（第四职业：夜路）#l\r\n";
                v0 += "#L420#希夫（第四职业：影子）#l\r\n";
            } else if (selection == 500) {
                v0 += "#L510#斗士（第四职业：蝰蛇）#l\r\n";
                v0 += "#L520#甘斯林格（第四职业：队长）#l\r\n";
            }
            cm.sendSimple(v0);
        } else if (selection == 2700) {
            var v0 = "选择了#e Luminus#n。请选择您喜欢的系列。\r\n\r\n#b#L0##e选择黑暗系列#n。选择#l\r\n#L1##e光系列#n。#l";
            cm.askMenu(v0, 1, GameObjectType.User, ScriptMessageFlag.NoEsc, ScriptMessageFlag.BigScenario);
        } else {
            changeJob();
            //selectJob(selection);
        }
    } else if (status == 3) {
        if (adventure) {
            secondJob = selection;
            changeJob();
        } else if (jobCode == 2700) {
            type_ = selection;
            changeJob();
            return;
        } else {
            changeJob();
            return;
        }
    } else if (status == 4) {
        changeJob();
    }
}

function selectJob(s) {
    var selectJob = getJobName(s);
    if (cm.getPlayer().getSubcategory() == 1) {
        selectJob = "双刀片";
    }
    if (selectJob == "" || s == 800 || s == 900 || s == 910) {
        cm.sendNext("?");
        cm.dispose();
        return;
    }
    var v0 = "选择的职业是#e" + selectJob + "#n。您要转职到该职业吗？\r\n选择时将跳槽到该职业，并支付初始资金。\r\n\r\n#L0#是的，我选择。#l\r\n#L1#我会重新考虑的。#l";
    cm.askMenu(v0, 1, GameObjectType.User, ScriptMessageFlag.NoEsc, ScriptMessageFlag.BigScenario);
    //	cm.sendYesNo("选择的职业是#e" + selectJob + "#n。#b是#k按钮将调到该职业并支付初始资金。");
}

function changeJob() {
    cm.getPlayer().changeJob(jobCode);
    cm.getPlayer().maxskill(cm.getPlayer().getJob());

    //cm.gainItem(2431307, 1);
    //cm.gainItem(2432128, 1);
    cm.gainItem(2433444, 1);
    cm.gainItem(3010432, 1);
    cm.gainItem(2000005, 500);

    cm.gainMeso(10000000);
    cm.teachSkill(80001829, 5, 5);
    if (cm.getPlayer().getSkillLevel(80000545) > 0) {
        cm.gainItem(2633552, 1);
    }
    //cm.getPlayer().send(CField.addPopupSay(1540208, 20000, "基本内容可通过单击#r缓存shop#k来查看，游戏中的命令可通过在聊天窗口中输入#b@帮助#k来查看。#b在便利系统中进行新手支援#k，通过灯泡进行#b真：真成长任务#k，对成长有很大帮助。使用游戏时，有什么困难或疑问，请参考#b网站初学者指南#k。", ""));
    //cm.getPlayer().dropMessage(5, "可以按~键查看内容，也可以在聊天窗口中输入@帮助来查看游戏中的命令。在便利系统中支持Newby，通过灯泡进行真：真成长任务，对成长有很大帮助。在使用游戏时，如果有困难或疑问，请参考网站新手指南。")
    if (jobCode == 3600) {
        cm.teachSkill(30021236, 1, 1);
        cm.teachSkill(30021237, 1, 1);
    }
    if (jobCode == 2700) {
        if (type_ == 0) { // 어둠 계열
            cm.teachSkill(27001201, 20, 20);
            cm.teachSkill(27000207, 5, 5);
        } else if (type_ == 1) {
            cm.teachSkill(27001100, 20, 20);
            cm.teachSkill(27000106, 5, 5);
        }
    }
    if (jobCode == 2100) {
        cm.teachSkill(20001295, 1, 1);
    }
    if (jobCode == 16200) {
        cm.teachSkill(160011005, 1, 1);
        cm.gainItem(1354020, 1);
        cm.gainItem(1354021, 1);
        cm.gainItem(1354022, 1);
        cm.gainItem(1354023, 1);
    }
    if (jobCode == 15400) {
        cm.teachSkill(150031074, 1, 1);
        cm.teachSkill(150030079, 1, 1);
        cm.teachSkill(150031005, 1, 1);
    }

    var job_ = 0;
    if (jobCode == 10112) {
        job_ = 10112;
    } else if (jobCode == 501) {
        job_ = 530;
    } else if (jobCode == 301) {
        job_ = 330;
    } else if (jobCode == 3101) {
        job_ = 3120;
    } else if (cm.getPlayer().getSubcategory() == 1) {
        job_ = 430;
    } else {
        if (adventure) {
            job_ = secondJob;
        } else {
            job_ = jobCode + 10;
        }
    }
    cm.getPlayer().updateInfoQuest(122870, "AutoJob=" + job_);
    if (cm.getPlayer().getJob() != 10112) {
        for (var i = cm.getPlayer().getLevel(); i < 200; i++) {
            cm.gainExp(Packages.constants.GameConstants.getExpNeededForLevel(i));
        }
    }
    cm.warp(ServerConstants.TownMap, 0);
    //cm.resetStats(4, 4, 4, 4);
    cm.getPlayer().statReset();
    cm.autoSkillMaster();
    cm.getPlayer().send(Packages.network.models.CField.addPopupSay(1052206, 20000, "基本内容可通过按\r\n体力条右侧#r红色按钮#k或#r~键#k确认，游戏中的命令可通过在聊天窗口中输入#b@帮助#k确认。\r\n在玩游戏时，如果有困难或疑问，请在#b网站上，在代码中参考#r指南#k或#b代码内的#r问答#k公告栏。", ""));
    cm.dispose();
}

function getJobName(job) {
    selectJob = "";
    if (job == 100) {
        selectJob = "战士";
    } else if (job == 200) {
        selectJob = "魔法师";
    } else if (job == 300) {
        selectJob = "弓箭手";
    } else if (job == 301) {
        selectJob = "探路机";
    } else if (job == 400) {
        selectJob = "盗贼";
    } else if (job == 500) {
        selectJob = "海盗";
    } else if (job == 501) {
        selectJob = "佳能射手";
    } else if (job == 1100) {
        selectJob = "灵魂大师";
    } else if (job == 1200) {
        selectJob = "火焰魔法师";
    } else if (job == 1300) {
        selectJob = "风闸";
    } else if (job == 1400) {
        selectJob = "夜行者";
    } else if (job == 1500) {
        selectJob = "前锋";
    } else if (job == 2100) {
        selectJob = "阿兰";
    } else if (job == 2200) {
        selectJob = "埃文";
    } else if (job == 2300) {
        selectJob = "梅赛德斯";
    } else if (job == 2400) {
        selectJob = "幻影";
    } else if (job == 2700) {
        selectJob = "鲁米纳斯";
    } else if (job == 2500) {
        selectJob = "银月";
    } else if (job == 3100) {
        selectJob = "守护者";
    } else if (job == 3101) {
        selectJob = "守护天使";
    } else if (job == 3200) {
        selectJob = "战斗明治";
    } else if (job == 3300) {
        selectJob = "野蛮猎人";
    } else if (job == 3500) {
        selectJob = "机械师";
    } else if (job == 3600) {
        selectJob = "氙气";
    } else if (job == 3700) {
        selectJob = "猛击者";
    } else if (job == 5100) {
        selectJob = "米哈伊尔";
    } else if (job == 6100) {
        selectJob = "凯撒";
    } else if (job == 6500) {
        selectJob = "天使巴士";
    } else if (job == 6400) {
        selectJob = "卡德纳";
    } else if (job == 10112) {
        selectJob = "零";
    } else if (job == 14200) {
        selectJob = "基尼斯";
    } else if (job == 15200) {
        selectJob = "以利亚";
    } else if (job == 15500) {
        selectJob = "弧";
    } else if (job == 16400) {
        selectJob = "胡英";
    } else if (job == 15100) {
        selectJob = "阿黛尔";
    } else if (job == 6300) {
        selectJob = "该隐";
    } else if (job == 16200) {
        selectJob = "拉拉";
    } else if (job == 15400) {
        selectJob = "卡利";
    }

    return selectJob;
}