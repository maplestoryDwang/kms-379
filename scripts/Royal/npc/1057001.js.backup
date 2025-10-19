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

        var text = "#fUI/Basic.img/RoyalBtn/StartImg/0#";

        cm.sendGetText(text);
    } else if (status == 1) {
        var text = cm.getText();

        if (text != "동의합니다" && !isenglish) {
            cm.dispose();
            cm.sendOk("#fs11#동의 하지않는다면 도와줄 방법이 없어요\r\n#b동의#k 한다면 다시 '#r#e동의합니다#k#n' 를 입력해주세요");
            return;
        }

        var v0 = "#b#h0##k님, 아래 직업 중 원하는 직업을 선택해주세요.#n\r\n\r\n";
        var job = cm.getPlayer().getJob();
        if (job == 0) { // 모험가(초보자)
            if (cm.getPlayer().getSubcategory() == 1) { // 듀얼블레이드
                v0 += "#b#L400#듀얼블레이드#k로 시작하겠습니다.#l\r\n";
            } else if (cm.getPlayer().getSubcategory() == 2) { // 캐논슈터
                v0 += "#b#L501#캐논슈터#k로 시작하겠습니다.#l\r\n";
            } else if (cm.getPlayer().getSubcategory() == 3) { // 패스파인더
                v0 += "#b#L301#패스파인더#k로 시작하겠습니다.#l\r\n";
            } else {
                v0 += "#b#L100#전사#k로 시작하겠습니다.#l\r\n";
                v0 += "#b#L200#마법사#k로 시작하겠습니다.#l\r\n";
                v0 += "#b#L300#궁수#k로 시작하겠습니다.#l\r\n";
                v0 += "#b#L400#도적#k으로 시작하겠습니다.#l\r\n";
                v0 += "#b#L500#해적#k으로 시작하겠습니다.#l\r\n";
            }
        } else if (job == 1000) { // 시그너스(노블레스)
            v0 += "#b#L1100#소울마스터#k로 시작하겠습니다.#l\r\n";
            v0 += "#b#L1200#플레임위자드#k로 시작하겠습니다.#l\r\n";
            v0 += "#b#L1300#윈드브레이커#k로 시작하겠습니다.#l\r\n";
            v0 += "#b#L1400#나이트워커#k로 시작하겠습니다.#l\r\n";
            v0 += "#b#L1500#스트라이커#k로 시작하겠습니다.#l\r\n";
        } else if (job == 5000) { // 미하일
            v0 += "#b#L5100#미하일#k로 시작하겠습니다.#l\r\n";
        } else if (job == 2000) { // 영웅(레전드)
            v0 += "#b#L2100#아란#k으로 시작하겠습니다.#l\r\n";
        } else if (job == 2001) { // 영웅(에반)
            v0 += "#b#L2200#에반#k으로 시작하겠습니다.#l\r\n";
        } else if (job == 2002) { // 영웅(메르세데스)
            v0 += "#b#L2300#메르세데스#k로 시작하겠습니다.#l\r\n";
        } else if (job == 2003) { // 영웅(팬텀)
            v0 += "#b#L2400#팬텀#k으로 시작하겠습니다.#l\r\n";
        } else if (job == 2004) { // 영웅(루미너스)
            v0 += "#b#L2700#루미너스#k로 시작하겠습니다.#l\r\n";
        } else if (job == 2005) { // 영웅(은월)
            v0 += "#b#L2500#은월#k로 시작하겠습니다.#l\r\n";
        } else if (job == 3000) { // 레지스탕스(시티즌)
            v0 += "#b#L3200#배틀메이지#k로 시작하겠습니다.#l\r\n";
            v0 += "#b#L3300#와일드헌터#k로 시작하겠습니다.#l\r\n";
            v0 += "#b#L3500#메카닉#k으로 시작하겠습니다.#l\r\n";
            v0 += "#b#L3700#블래스터#k로 시작하겠습니다.#l\r\n";
        } else if (job == 3001) { // 데몬
            v0 += "#b#L3100#데몬슬레이어#k로 시작하겠습니다.#l\r\n";
            v0 += "#b#L3101#데몬어벤져 [비추천]#k로 시작하겠습니다.#l\r\n";
        } else if (job == 3002) { // 제논
            v0 += "#b#L3600#제논#k으로 시작하겠습니다.#l\r\n";
        } else if (job == 6000) { // 카이저
            v0 += "#b#L6100#카이저#k로 시작하겠습니다.#l\r\n";
        } else if (job == 6001) { // 엔젤릭버스터
            v0 += "#b#L6500#엔젤릭버스터#k로 시작하겠습니다.#l\r\n";
        } else if (job == 6002) { // 카데나
            v0 += "#b#L6400#카데나#k로 시작하겠습니다.#l\r\n";
        } else if (job == 10112) { // 제로
            v0 += "#b#L10112#제로 [비추천]#k로 시작하겠습니다.#l\r\n"
        } else if (job == 14000) { // 키네시스
            v0 += "#b#L14200#키네시스#k로 시작하겠습니다.#l\r\n"
        } else if (job == 15000) { // 일리움
            v0 += "#b#L15200#일리움#k으로 시작하겠습니다.#l\r\n"
        } else if (job == 15001) { // 아크
            v0 += "#b#L15500#아크#k로 시작하겠습니다.#l\r\n"
        } else if (job == 16000) { // 호영
            v0 += "#b#L16400#호영#k으로 시작하겠습니다.#l\r\n"
        } else if (job == 15002) { // 아델
            v0 += "#b#L15100#아델#k으로 시작하겠습니다.#l\r\n"
        } else if (job == 6003) { // 카인
            v0 += "#b#L6300#카인#k으로 시작하겠습니다.#l\r\n";
        } else if (job == 16001) { // 라라
            v0 += "#b#L16200#라라#k로 시작하겠습니다.#l\r\n"
        } else if (job == 15003) { // 칼리
            v0 += "#b#L15400#칼리#k로 시작하겠습니다.#l\r\n"
        }
        cm.askMenu(v0, 1, GameObjectType.User, ScriptMessageFlag.NoEsc, ScriptMessageFlag.BigScenario);
    } else if (status == 2) {
        jobCode = selection;
        if (selection == 100 || selection == 200 || selection == 300 || selection == 400 && cm.getPlayer().getSubcategory() != 1 || selection == 500) {
            adventure = true;
            var v0 = "희망하는 2차 직업을 선택해주세요. 적정 레벨을 달성하면 자동으로 전직이 진행됩니다.#b\r\n\r\n";
            if (selection == 100) {
                v0 += "#L110#파이터 (4차직업 : 히어로)#l\r\n";
                v0 += "#L120#페이지 (4차직업 : 팔라딘)#l\r\n";
                v0 += "#L130#스피어맨 (4차직업 : 다크나이트)#l\r\n";
            } else if (selection == 200) {
                v0 += "#L210#위자드(불,독) (4차직업 : 아크메이지(불,독))#l\r\n";
                v0 += "#L220#위자드(썬,콜) (4차직업 : 아크메이지(썬,콜))#l\r\n";
                v0 += "#L230#클레릭 (4차직업 : 비숍)#l\r\n";
            } else if (selection == 300) {
                v0 += "#L310#헌터 (4차직업 : 보우마스터)#l\r\n";
                v0 += "#L320#사수 (4차직업 : 신궁)#l\r\n";
            } else if (selection == 400) {
                v0 += "#L410#어쌔신 (4차직업 : 나이트로드)#l\r\n";
                v0 += "#L420#시프 (4차직업 : 섀도어)#l\r\n";
            } else if (selection == 500) {
                v0 += "#L510#인파이터 (4차직업 : 바이퍼)#l\r\n";
                v0 += "#L520#건슬링거 (4차직업 : 캡틴)#l\r\n";
            }
            cm.sendSimple(v0);
        } else if (selection == 2700) {
            var v0 = "#e루미너스#n를 선택하셨습니다. 원하는 계열을 선택해주세요.\r\n\r\n#b#L0##e어둠 계열#n을 선택하겠습니다.#l\r\n#L1##e빛 계열#n을 선택하겠습니다.#l";
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
        selectJob = "듀얼블레이드";
    }
    if (selectJob == "" || s == 800 || s == 900 || s == 910) {
        cm.sendNext("?");
        cm.dispose();
        return;
    }
    var v0 = "선택하신 직업은 #e" + selectJob + "#n입니다. 해당 직업으로 전직하시겠습니까?\r\n 선택 시 해당 직업으로 전직되며 초기 자금이 지급됩니다.\r\n\r\n#L0#네 선택하겠습니다.#l\r\n#L1#다시 생각해보겠습니다.#l";
    cm.askMenu(v0, 1, GameObjectType.User, ScriptMessageFlag.NoEsc, ScriptMessageFlag.BigScenario);
    //	cm.sendYesNo("선택하신 직업은 #e" + selectJob + "#n입니다. #b예#k 버튼을 누르시면 해당 직업으로 전직되며 초기 자금이 지급됩니다.");
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
    //cm.getPlayer().send(CField.addPopupSay(1540208, 20000, "기본적인 컨텐츠는 #r캐시샵#k을 눌러서 확인 할 수 있으며 게임내 명령어는 채팅창에 #b@도움말#k을 입력하여 확인할 수 있습니다. #b편의 시스템에서 초보자 지원#k, 전구를 통한 #b진:眞 성장 퀘스트#k를 진행하시면 성장에 많은 도움이됩니다. 게임을 이용하면서 어렵거나 궁금한점은 #b홈페이지 초보자 가이드#k를 참고해주세요.", ""));
    //cm.getPlayer().dropMessage(5, "컨텐츠는 ~ 키를 눌러서 확인 할 수 있으며 게임내 명령어는 채팅창에 @도움말을 입력하여 확인할 수 있습니다. 편의 시스템에서 뉴비 지원, 전구를 통한 진:眞 성장 퀘스트를 진행하시면 성장에 많은 도움이됩니다. 게임을 이용하면서 어렵거나 궁금한점은 홈페이지 초보자 가이드를 참고해주세요.")
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
    cm.getPlayer().send(Packages.network.models.CField.addPopupSay(1052206, 20000, "기본적인 컨텐츠는\r\n체력바 우측 #r붉은 버튼#k 혹은 #r~키#k를 눌러서 확인 할 수 있으며 게임내 명령어는 채팅창에 #b@도움말#k을 입력하여 확인할 수 있습니다.\r\n게임을 이용하면서 어렵거나 궁금한점은 #b홈페이지,디스코드에 #r가이드#k 혹은 #b디스코드 내에 #r질문답변#k 게시판을 참고해주세요.", ""));
    cm.dispose();
}

function getJobName(job) {
    selectJob = "";
    if (job == 100) {
        selectJob = "전사";
    } else if (job == 200) {
        selectJob = "마법사";
    } else if (job == 300) {
        selectJob = "궁수";
    } else if (job == 301) {
        selectJob = "패스파인더";
    } else if (job == 400) {
        selectJob = "도적";
    } else if (job == 500) {
        selectJob = "해적";
    } else if (job == 501) {
        selectJob = "캐논슈터";
    } else if (job == 1100) {
        selectJob = "소울마스터";
    } else if (job == 1200) {
        selectJob = "플레임위자드";
    } else if (job == 1300) {
        selectJob = "윈드브레이커";
    } else if (job == 1400) {
        selectJob = "나이트워커";
    } else if (job == 1500) {
        selectJob = "스트라이커";
    } else if (job == 2100) {
        selectJob = "아란";
    } else if (job == 2200) {
        selectJob = "에반";
    } else if (job == 2300) {
        selectJob = "메르세데스";
    } else if (job == 2400) {
        selectJob = "팬텀";
    } else if (job == 2700) {
        selectJob = "루미너스";
    } else if (job == 2500) {
        selectJob = "은월";
    } else if (job == 3100) {
        selectJob = "데몬슬레이어";
    } else if (job == 3101) {
        selectJob = "데몬어벤져";
    } else if (job == 3200) {
        selectJob = "배틀메이지";
    } else if (job == 3300) {
        selectJob = "와일드헌터";
    } else if (job == 3500) {
        selectJob = "메카닉";
    } else if (job == 3600) {
        selectJob = "제논";
    } else if (job == 3700) {
        selectJob = "블래스터";
    } else if (job == 5100) {
        selectJob = "미하일";
    } else if (job == 6100) {
        selectJob = "카이저";
    } else if (job == 6500) {
        selectJob = "엔젤릭버스터";
    } else if (job == 6400) {
        selectJob = "카데나";
    } else if (job == 10112) {
        selectJob = "제로";
    } else if (job == 14200) {
        selectJob = "키네시스";
    } else if (job == 15200) {
        selectJob = "일리움";
    } else if (job == 15500) {
        selectJob = "아크";
    } else if (job == 16400) {
        selectJob = "호영";
    } else if (job == 15100) {
        selectJob = "아델";
    } else if (job == 6300) {
        selectJob = "카인";
    } else if (job == 16200) {
        selectJob = "라라";
    } else if (job == 15400) {
        selectJob = "칼리";
    }

    return selectJob;
}