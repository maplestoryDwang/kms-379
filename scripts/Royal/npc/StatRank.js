importPackage(java.lang);
importPackage(Packages.server);

var status = -1;
var sel = 0;
var HuntCoin1 = 4310266;
var HuntCoin10000 = 4310269;
var needcoin = 0;
var needcoincount = 0;
var uptear = 0;
var tearname = "";
var tearnamestring = "";
var suc = 0;
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
        txt = "#fs11##b각종 스탯의 랭크#fc0xFF000000#를 올릴 수 있는 시스템이라네 원하는 항목을 골라보게나#n#k\r\n\r\n#b";
        txt += "#L0#데미지 랭크\r\n";
        txt += "#L4#보스 공격력 랭크\r\n";
		/*
if (cm.getPlayer().isGM()) {
        txt += "#L1#경험치 랭크\r\n";
        txt += "#L2#드롭률 랭크\r\n";
        txt += "#L3#크리티컬 데미지 랭크\r\n";

        //txt += "#L5#메소 랭크\r\n";
        //txt += "#L6#올스탯 랭크\r\n";
}
*/
        cm.sendSimple(txt);
    } else if (status == 1) {
        sel = selection;
        if (sel == 0) {
            //데미지 티어
            maxtear = 15;
            tearname = "DamageTear";
            tearnamestring = "데미지 랭크";
            effect = "데미지";
            
            if (cm.getPlayer().getKeyValue(999, tearname) < 0) {
                cm.getPlayer().setKeyValue(999, tearname, "0");
            }

            mytear = cm.getPlayer().getKeyValue(999, tearname);
            uptear = mytear + 1;
            damageup = 0;
            
            if (mytear >= maxtear) {
                cm.sendOk("#fs11#이미 최고등급입니다.\r\n\r\n#fc0xFF1A9714#최대 " + tearnamestring + " : #fc0xFF000000##e[" + maxtear + "랭크]#n\r\n#b현재 " + tearnamestring + " : #fc0xFF000000##e[" + mytear + "랭크]#n");
                cm.dispose();
                return;
            }

            switch (uptear) {
                case 1://1티어
                    needcoin = HuntCoin1;
                    needcoincount = 200;
                    damageup = 10;
                    suc = 100;
                    break;
                case 2:
                    needcoin = HuntCoin1;
                    needcoincount = 600;
                    damageup = 15;
                    suc = 100;
                    break;
                case 3:
                    needcoin = HuntCoin1;
                    needcoincount = 800;
                    damageup = 20;
                    suc = 100;
                    break;
                case 4:
                    needcoin = HuntCoin1;
                    needcoincount = 1400;
                    damageup = 25;
                    suc= 100;
                    break;
                case 5:
                    needcoin = HuntCoin1;
                    needcoincount = 2000;
                    damageup = 30;
                    suc = 100;
                    break;
                case 6:
                    needcoin = HuntCoin1;
                    needcoincount = 2600;
                    damageup = 40;
                    suc = 100;
                    break;
                case 7:
                    needcoin = HuntCoin1;
                    needcoincount = 3200;
                    damageup = 60;
                    suc = 100;
                    break;
                case 8:
                    needcoin = HuntCoin1;
                    needcoincount = 5000;
                    damageup = 80;
                    suc = 100;
                    break;
                case 9:
                    needcoin = HuntCoin1;
                    needcoincount = 7000;
                    damageup = 90;
                    suc = 100;
                    break;
                case 10:
                    needcoin = HuntCoin1;
                    needcoincount = 10000;
                    damageup = 100;
                    suc = 100;
                    break;
                case 11:
                    needcoin = HuntCoin1;
                    needcoincount = 14000;
                    damageup = 120;
                    suc = 100;
                    break;
                case 12:
                    needcoin = HuntCoin1;
                    needcoincount = 18000;
                    damageup = 140;
                    suc = 100;
                    break;
                case 13:
                    needcoin = HuntCoin1;
                    needcoincount = 22000;
                    damageup = 160;
                    suc = 100;
                    break;
                case 14:
                    needcoin = HuntCoin1;
                    needcoincount = 25000;
                    damageup = 180;
                    suc = 100;
                    break;
                case 15:
                    needcoin = HuntCoin1;
                    needcoincount = 30000;
                    damageup = 200;
                    suc = 100;
                    break;
                case 16:
                    needcoin = HuntCoin1;
                    needcoincount = 1;
                    damageup = 220;
                    suc = 100;
                    break;
                case 17:
                    needcoin = HuntCoin1;
                    needcoincount = 1;
                    damageup = 240;
                    suc = 100;
                    break;
                case 18:
                    needcoin = HuntCoin1;
                    needcoincount = 1;
                    damageup = 260;
                    suc = 100;
                    break;
                case 19:
                    needcoin = HuntCoin1;
                    needcoincount = 1;
                    damageup = 280;
                    suc = 100;
                    break;
                case 20:
                    needcoin = HuntCoin1;
                    needcoincount = 1;
                    damageup = 300;
                    suc = 100;
                    break;
                    
            }
            txt = "#fs11#";
            
            txt += "#fc0xFF1A9714#최대 " + tearnamestring + " : #fc0xFF000000##e[" + maxtear + "랭크]#n\r\n\r\n";
            
            txt += "#b현재 " + tearnamestring + " : #fc0xFF000000##e[" + mytear + "랭크]#n\r\n";
            txt += "#b승급 " + tearnamestring + " : #fc0xFF000000##e[" + uptear + "랭크]#n\r\n\r\n";
            
            txt += "승급확률, 승급한 랭크의 효과와\r\n승급에 필요한 #r아이템#fc0xFF000000#은 다음과 같다네\r\n\r\n";
            txt += "#b#e승급 성공 확률 : " + suc + "%#k\r\n";
            txt += "#r#e승급 성공 시 총 " + effect + " + #r#e" + damageup + "%#n#k\r\n승급 재료 : #i" + needcoin + "# #b#z" + needcoin + "# " + needcoincount + "개#k\r\n\r\n";
            
            txt += "#fs11##fc0xFF000000#정말 #e승급#n을 진행 하겠나?";
            cm.sendYesNo(txt);
        } else if (sel == 1) {
            //경험치 티어
            maxtear = 8;
            tearname = "ExpTear";
            tearnamestring = "경험치 랭크";
            effect = "경험치";
            
            if (cm.getPlayer().getKeyValue(999, tearname) < 0) {
                cm.getPlayer().setKeyValue(999, tearname, "0");
            }
            
            mytear = cm.getPlayer().getKeyValue(999, tearname);
            uptear = mytear + 1;
            up = 0;
            
            if (mytear >= maxtear) {
                cm.sendOk("#fs11#이미 최고등급입니다.\r\n\r\n#fc0xFF1A9714#최대 " + tearnamestring + " : #fc0xFF000000##e[" + maxtear + "랭크]#n\r\n#b현재 " + tearnamestring + " : #fc0xFF000000##e[" + mytear + "랭크]#n");
                cm.dispose();
                return;
            }
            
            switch (uptear) {
                case 1://1티어
                    needcoin = HuntCoin1;
                    needcoincount = 5000;
                    up = 3;
                    suc = 100;
                    break;
                case 2:
                    needcoin = HuntCoin1;
                    needcoincount = 6000;
                    up = 7;
                    suc = 80;
                    break;
                case 3:
                    needcoin = HuntCoin1;
                    needcoincount = 7000;
                    up = 13;
                    suc = 60;
                    break;
                case 4:
                    needcoin = HuntCoin10000;
                    needcoincount = 2;
                    up = 18;
                    suc= 50;
                    break;
                case 5:
                    needcoin = HuntCoin10000;
                    needcoincount = 5;
                    up = 23;
                    suc = 45;
                    break;
                case 6:
                    needcoin = HuntCoin10000;
                    needcoincount = 10;
                    up = 30;
                    suc = 30;
                    break;
                case 7:
                    needcoin = HuntCoin10000;
                    needcoincount = 30;
                    up = 40;
                    suc = 20;
                    break;
                case 8:
                    needcoin = HuntCoin10000;
                    needcoincount = 50;
                    up = 50;
                    suc = 10;
                    break;
            }

            txt = "#fs11#";
            
            txt += "#fc0xFF1A9714#최대 " + tearnamestring + " : #fc0xFF000000##e[" + maxtear + "랭크]#n\r\n\r\n";
            
            txt += "#b현재 " + tearnamestring + " : #fc0xFF000000##e[" + mytear + "랭크]#n\r\n";
            txt += "#b승급 " + tearnamestring + " : #fc0xFF000000##e[" + uptear + "랭크]#n\r\n\r\n";
            
            txt += "승급확률, 승급한 랭크의 효과와\r\n승급에 필요한 #r아이템#fc0xFF000000#은 다음과 같다네\r\n\r\n";
            txt += "#b#e승급 성공 확률 : " + suc + "%#k\r\n";
            txt += "#r#e승급 성공 시 총 " + effect + " + #r#e" + up + "%#n#k\r\n승급 재료 : #i" + needcoin + "# #b#z" + needcoin + "# " + needcoincount + "개#k\r\n\r\n";
            
            txt += "#fs11##fc0xFF000000#정말 #e승급#n을 진행 하겠나?";
            cm.sendYesNo(txt);
        } else if (sel == 2) {
            //드롭 티어
            maxtear = 8;
            tearname = "DropTear";
            tearnamestring = "드롭률 랭크";
            effect = "드롭";
            
            if (cm.getPlayer().getKeyValue(999, tearname) < 0) {
                cm.getPlayer().setKeyValue(999, tearname, "0");
            }
            
            mytear = cm.getPlayer().getKeyValue(999, tearname);
            uptear = mytear + 1;
            up = 0;
            
            if (mytear >= maxtear) {
                cm.sendOk("#fs11#이미 최고등급입니다.\r\n\r\n#fc0xFF1A9714#최대 " + tearnamestring + " : #fc0xFF000000##e[" + maxtear + "랭크]#n\r\n#b현재 " + tearnamestring + " : #fc0xFF000000##e[" + mytear + "랭크]#n");
                cm.dispose();
                return;
            }
            
            switch (uptear) {
                case 1://1티어
                    needcoin = HuntCoin1;
                    needcoincount = 5000;
                    up = 10;
                    suc = 100;
                    break;
                case 2:
                    needcoin = HuntCoin1;
                    needcoincount = 6000;
                    up = 20;
                    suc = 80;
                    break;
                case 3:
                    needcoin = HuntCoin1;
                    needcoincount = 7000;
                    up = 40;
                    suc = 60;
                    break;
                case 4:
                    needcoin = HuntCoin10000;
                    needcoincount = 2;
                    up = 60;
                    suc= 50;
                    break;
                case 5:
                    needcoin = HuntCoin10000;
                    needcoincount = 5;
                    up = 80;
                    suc = 45;
                    break;
                case 6:
                    needcoin = HuntCoin10000;//만개짜리코인
                    needcoincount = 10;//2개필요
                    up = 120;//데미지
                    suc = 30;
                    break;
                case 7:
                    needcoin = HuntCoin10000;//만개짜리코인
                    needcoincount = 30;//2개필요
                    up = 180;//데미지
                    suc = 20;
                    break;
                case 8:
                    needcoin = HuntCoin10000;//만개짜리코인
                    needcoincount = 50;//2개필요
                    up = 300;//데미지
                    suc = 10;
                    break;
            }

            txt = "#fs11#";
            
            txt += "#fc0xFF1A9714#최대 " + tearnamestring + " : #fc0xFF000000##e[" + maxtear + "랭크]#n\r\n\r\n";
            
            txt += "#b현재 " + tearnamestring + " : #fc0xFF000000##e[" + mytear + "랭크]#n\r\n";
            txt += "#b승급 " + tearnamestring + " : #fc0xFF000000##e[" + uptear + "랭크]#n\r\n\r\n";
            
            txt += "승급확률, 승급한 랭크의 효과와\r\n승급에 필요한 #r아이템#fc0xFF000000#은 다음과 같다네\r\n\r\n";
            txt += "#b#e승급 성공 확률 : " + suc + "%#k\r\n";
            txt += "#r#e승급 성공 시 총 " + effect + " + #r#e" + up + "%#n#k\r\n승급 재료 : #i" + needcoin + "# #b#z" + needcoin + "# " + needcoincount + "개#k\r\n\r\n";
            
            txt += "#fs11##fc0xFF000000#정말 #e승급#n을 진행 하겠나?";
            cm.sendYesNo(txt);
        } else if (sel == 3) {
            //크뎀 티어
            maxtear = 8;
            tearname = "CridamTear";
            tearnamestring = "크리티컬 데미지 랭크";
            effect = "크리티컬 데미지";
            
            if (cm.getPlayer().getKeyValue(999, tearname) < 0) {
                cm.getPlayer().setKeyValue(999, tearname, "0");
            }
            
            mytear = cm.getPlayer().getKeyValue(999, tearname);
            uptear = mytear + 1;
            up = 0;
            
            if (mytear >= maxtear) {
                cm.sendOk("#fs11#이미 최고등급입니다.\r\n\r\n#fc0xFF1A9714#최대 " + tearnamestring + " : #fc0xFF000000##e[" + maxtear + "랭크]#n\r\n#b현재 " + tearnamestring + " : #fc0xFF000000##e[" + mytear + "랭크]#n");
                cm.dispose();
                return;
            }
            
            switch (uptear) {
                case 1://1티어
                    needcoin = HuntCoin1;
                    needcoincount = 400;
                    up = 10;
                    suc = 100;
                    break;
                case 2:
                    needcoin = HuntCoin1;
                    needcoincount = 800;
                    up = 15;
                    suc = 100;
                    break;
                case 3:
                    needcoin = HuntCoin1;
                    needcoincount = 1500;
                    up = 20;
                    suc = 100;
                    break;
                case 4:
                    needcoin = HuntCoin1;
                    needcoincount = 2500;
                    up = 25;
                    suc= 100;
                    break;
                case 5:
                    needcoin = HuntCoin1;
                    needcoincount = 3500;
                    up = 30;
                    suc = 100;
                    break;
                case 6:
                    needcoin = HuntCoin1;//만개짜리코인
                    needcoincount = 4800;
                    up = 35;//데미지
                    suc = 100;
                    break;
                case 7:
                    needcoin = HuntCoin1;
                    needcoincount = 6000;
                    up = 40;//데미지
                    suc = 100;
                    break;
                case 8:
                    needcoin = HuntCoin1;
                    needcoincount = 9000;
                    up = 45;//데미지
                    suc = 100;
                    break;
            }

            txt = "#fs11#";
            
            txt += "#fc0xFF1A9714#최대 " + tearnamestring + " : #fc0xFF000000##e[" + maxtear + "랭크]#n\r\n\r\n";
            
            txt += "#b현재 " + tearnamestring + " : #fc0xFF000000##e[" + mytear + "랭크]#n\r\n";
            txt += "#b승급 " + tearnamestring + " : #fc0xFF000000##e[" + uptear + "랭크]#n\r\n\r\n";
            
            txt += "승급확률, 승급한 랭크의 효과와\r\n승급에 필요한 #r아이템#fc0xFF000000#은 다음과 같다네\r\n\r\n";
            txt += "#b#e승급 성공 확률 : " + suc + "%#k\r\n";
            txt += "#r#e승급 성공 시 총 " + effect + " + #r#e" + up + "%#n#k\r\n승급 재료 : #i" + needcoin + "# #b#z" + needcoin + "# " + needcoincount + "개#k\r\n\r\n";
            
            txt += "#fs11##fc0xFF000000#정말 #e승급#n을 진행 하겠나?";
            cm.sendYesNo(txt);
        } else if (sel == 4) {
            //보공 티어
            maxtear = 10;
            tearname = "BossdamTear";
            tearnamestring = "보스공격력 랭크";
            effect = "보스 공격력";
            
            if (cm.getPlayer().getKeyValue(999, tearname) < 0) {
                cm.getPlayer().setKeyValue(999, tearname, "0");
            }
            
            mytear = cm.getPlayer().getKeyValue(999, tearname);
            uptear = mytear + 1;
            up = 0;
            
            if (mytear >= maxtear) {
                cm.sendOk("#fs11#이미 최고등급입니다.\r\n\r\n#fc0xFF1A9714#최대 " + tearnamestring + " : #fc0xFF000000##e[" + maxtear + "랭크]#n\r\n#b현재 " + tearnamestring + " : #fc0xFF000000##e[" + mytear + "랭크]#n");
                cm.dispose();
                return;
            }
            
            switch (uptear) {
                case 1://1티어
                    needcoin = HuntCoin1;
                    needcoincount = 500;
                    up = 10;
                    suc = 100;
                    break;
                case 2:
                    needcoin = HuntCoin1;
                    needcoincount = 1000;
                    up = 20;
                    suc = 100;
                    break;
                case 3:
                    needcoin = HuntCoin1;
                    needcoincount = 2000;
                    up = 30;
                    suc = 85;
                    break;
                case 4:
                    needcoin = HuntCoin1;
                    needcoincount = 3000;
                    up = 50;
                    suc= 85;
                    break;
                case 5:
                    needcoin = HuntCoin1;
                    needcoincount = 5000;
                    up = 70;
                    suc = 80;
                    break;
                case 6:
                    needcoin = HuntCoin1;
                    needcoincount = 7000;
                    up = 100;//데미지
                    suc = 70;
                    break;
                case 7:
                    needcoin = HuntCoin1;
                    needcoincount = 10000;
                    up = 120;//데미지
                    suc = 60;
                    break;
                case 8:
                    needcoin = HuntCoin1;
                    needcoincount = 12000;
                    up = 150;
                    suc = 50;
                    break;
                case 9:
                    needcoin = HuntCoin1;
                    needcoincount = 15000;
                    up = 170;
                    suc = 50;
                    break;
                case 10:
                    needcoin = HuntCoin1;
                    needcoincount = 20000;
                    up = 200;
                    suc = 50;
                    break;
            }

            txt = "#fs11#";
            
            txt += "#fc0xFF1A9714#최대 " + tearnamestring + " : #fc0xFF000000##e[" + maxtear + "랭크]#n\r\n\r\n";
            
            txt += "#b현재 " + tearnamestring + " : #fc0xFF000000##e[" + mytear + "랭크]#n\r\n";
            txt += "#b승급 " + tearnamestring + " : #fc0xFF000000##e[" + uptear + "랭크]#n\r\n\r\n";
            
            txt += "승급확률, 승급한 랭크의 효과와\r\n승급에 필요한 #r아이템#fc0xFF000000#은 다음과 같다네\r\n\r\n";
            txt += "#b#e승급 성공 확률 : " + suc + "%#k\r\n";
            txt += "#r#e승급 성공 시 총 " + effect + " + #r#e" + up + "%#n#k\r\n승급 재료 : #i" + needcoin + "# #b#z" + needcoin + "# " + needcoincount + "개#k\r\n\r\n";
            
            txt += "#fs11##fc0xFF000000#정말 #e승급#n을 진행 하겠나?";
            cm.sendYesNo(txt);
        } else if (sel == 5) {
            //메소 티어
            maxtear = 8;
            tearname = "MesoTear";
            tearnamestring = "메소 랭크";
            effect = "메소";
            
            if (cm.getPlayer().getKeyValue(999, tearname) < 0) {
                cm.getPlayer().setKeyValue(999, tearname, "0");
            }
            
            mytear = cm.getPlayer().getKeyValue(999, tearname);
            uptear = mytear + 1;
            up = 0;
            
            if (mytear >= maxtear) {
                cm.sendOk("#fs11#이미 최고등급입니다.\r\n\r\n#fc0xFF1A9714#최대 " + tearnamestring + " : #fc0xFF000000##e[" + maxtear + "랭크]#n\r\n#b현재 " + tearnamestring + " : #fc0xFF000000##e[" + mytear + "랭크]#n");
                cm.dispose();
                return;
            }
            
            switch (uptear) {
                case 1://1티어
                    needcoin = HuntCoin1;
                    needcoincount = 5000;
                    up = 10;
                    suc = 100;
                    break;
                case 2:
                    needcoin = HuntCoin1;
                    needcoincount = 6000;
                    up = 20;
                    suc = 80;
                    break;
                case 3:
                    needcoin = HuntCoin1;
                    needcoincount = 7000;
                    up = 40;
                    suc = 60;
                    break;
                case 4:
                    needcoin = HuntCoin10000;
                    needcoincount = 2;
                    up = 60;
                    suc= 50;
                    break;
                case 5:
                    needcoin = HuntCoin10000;
                    needcoincount = 5;
                    up = 80;
                    suc = 45;
                    break;
                case 6:
                    needcoin = HuntCoin10000;//만개짜리코인
                    needcoincount = 10;//2개필요
                    up = 90;//데미지
                    suc = 30;
                    break;
                case 7:
                    needcoin = HuntCoin10000;//만개짜리코인
                    needcoincount = 30;//2개필요
                    up = 100;//데미지
                    suc = 20;
                    break;
                case 8:
                    needcoin = HuntCoin10000;//만개짜리코인
                    needcoincount = 50;//2개필요
                    up = 120;//데미지
                    suc = 10;
                    break;
            }

            txt = "#fs11#";
            
            txt += "#fc0xFF1A9714#최대 " + tearnamestring + " : #fc0xFF000000##e[" + maxtear + "랭크]#n\r\n\r\n";
            
            txt += "#b현재 " + tearnamestring + " : #fc0xFF000000##e[" + mytear + "랭크]#n\r\n";
            txt += "#b승급 " + tearnamestring + " : #fc0xFF000000##e[" + uptear + "랭크]#n\r\n\r\n";
            
            txt += "승급확률, 승급한 랭크의 효과와\r\n승급에 필요한 #r아이템#fc0xFF000000#은 다음과 같다네\r\n\r\n";
            txt += "#b#e승급 성공 확률 : " + suc + "%#k\r\n";
            txt += "#r#e승급 성공 시 총 " + effect + " + #r#e" + up + "%#n#k\r\n승급 재료 : #i" + needcoin + "# #b#z" + needcoin + "# " + needcoincount + "개#k\r\n\r\n";
            
            txt += "#fs11##fc0xFF000000#정말 #e승급#n을 진행 하겠나?";
            cm.sendYesNo(txt);
        }
    } else if (status == 2) {
        if (!cm.haveItem(needcoin, needcoincount)) {
            cm.sendOk("#fs11#승급에 필요한 코인이 부족합니다.");
            cm.dispose();
            return;
        }

        cm.gainItem(needcoin, -needcoincount);
        if (Packages.objects.utils.Randomizer.isSuccess(suc)) {
            try {
                if (uptear >= 10) // 10랭크 이상만 월드 메세지 전송
                    cm.worldGMMessage(22, "[스탯랭크] " + cm.getPlayer().getName() + "님이 [" + tearnamestring + "] 를 " + uptear + "랭크로 승급하셨습니다.");
                //Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/랭크승급/[스탯랭크승급].log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n승급랭크 : " + tearnamestring + "\r\n승급등급 : " + uptear + "\r\n\r\n", true);
                cm.addCustomLog(3, "[스탯랭크] " + tearnamestring + " 승급등급 : " + uptear + "");
                cm.effectText("#fn나눔고딕 ExtraBold##fs20#[스탯랭크] < " + tearnamestring + " > 를 " + uptear + "랭크로 승급하였습니다", 50, 1000, 6, 0, 330, -550);

                cm.getPlayer().setKeyValue(999, tearname, "" + uptear + "");
                cm.getPlayer().setBonusCTSStat();

                prevflag = cm.getPlayer().getSaveFlag();
                cm.getPlayer().setSaveFlag(64); // QuestInfo
                cm.getPlayer().saveToDB(false, false);
                cm.getPlayer().setSaveFlag(prevflag)

                cm.showEffect(false, "Effect/EventEffect.img/SalonDebut/screenEff/1366");
                cm.showEffect(false, "Effect/CharacterEff.img/GradeUp");
                
                cm.sendYesNo("#fs11##fc0xFF000000#승급에 성공하셨습니다!\r\n\r\n#b" + tearnamestring + " : [" + uptear + "랭크]\r\n\r\n#fc0xFF000000#또 승급을 시도하시겠어요?");
            } catch(err) {
                cm.addCustomLog(50, "[StatRank.js] 에러 발생 : " + err + "");
            }
        } else {
            cm.sendYesNo("#fs11##fc0xFF000000#승급에 실패하셨습니다..\r\n\r\n#b" + tearnamestring + " : [" + (uptear - 1) + "랭크]\r\n\r\n#fc0xFF000000#다시 승급을 시도하시겠어요?");
        }
    } else if (status == 3) {
        status = 0;
        action(1, 0, sel);
        return;
    }
}
