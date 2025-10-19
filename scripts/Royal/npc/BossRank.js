importPackage(java.lang);
importPackage(Packages.server);

req = [
[0, [[4001715,30], [4310266,100], [4310308,50]], 0],
[0, [[4001715,50], [4310266,200], [4310308,100]], 0],
[0, [[4001715,70], [4310266,300], [4310308,350]],  0],
[0, [[4001715,90], [4310266,400], [4310308,700]],  0],
[0, [[4001715,150], [4310266,500], [4310308,1500]],  0],
[0, [[4001715,250], [4310266,600], [4310308,2700]],  0],
[0, [[4001715,350], [4310266,700], [4310308,3000]],  0],
[0, [[4001715,500], [4310266,800], [4310308,5000]],  0]
]

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {     
    if (mode <= 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }
    if (status == 0) {
        말 = "#fs11##fc0xFF990033##e보스랭크 승급 시스템#n#fc0xFF000000#이라네\r\n#b보스랭크 승급#fc0xFF000000#을 통해 더욱 더 강해져보지 않겠나!?\r\n\r\n"
        if (gK() >= 8) {
            말+= "#r이미 보스랭크가 최고단계 입니다#l\r\n";
        } else {
            말+= "#L0##b"+(gK() + 1)+"레벨로 승급하겠습니다.#l\r\n";
        }
        말+= "#L1##b보스랭크란 무엇인가요?#l"
        cm.sendSimple(말);
    } else if (status == 1) { 
        if (selection == 0) {
            말 = (gK() + 1) + "#fs11#레벨로 레벨업을 하기 위해선 아래와 같은 재료가 필요하다네.\r\n\r\n"

            for (i=0; i<req[gK()-0][1].length; i++) {
                말 += "#i"+req[gK()][1][i][0]+"# #b#z"+req[gK()-0][1][i][0]+"##r "+req[gK()][1][i][1]+"개#k\r\n"; // 개수 : cm.itemQuantity(req[gK()][1][i][0])
            }
            //말+= "#i4031138# #b메소 "+req[gK()][2]+"#k\r\n\r\n"
            말+= " \r\n#fs11##e#b정말 승급을 할텐가?#k#n"
            cm.sendYesNo(말);
        } else {
	cm.sendOk("#fs11##fc0xFF990033#[랭크별 승급버프]\r\n\r\n[1] 랭크 당\r\n#b보스 공격력 + 10%\r\n#b보스 입장가능횟수 + 1\r\n\r\n#fc0xFF990033#[보스 랭크 2레벨]\r\n#b하드 스우, 데미안, 루시드 입장 가능\r\n\r\n#fc0xFF990033#[보스 랭크 3레벨]\r\n#b노멀 윌, 노멀 가디언 엔젤 슬라임 입장 가능\r\n\r\n#fc0xFF990033#[보스 랭크 4레벨]\r\n#b노멀 듄켈, 노멀 더스크 입장 가능\r\n\r\n#fc0xFF990033#[보스 랭크 5레벨]\r\n#b하드 윌, 카오스 가디언 엔젤 슬라임 입장 가능\r\n\r\n#fc0xFF990033#[보스 랭크 6레벨]\r\n#b하드 듄켈, 카오스 더스크, 진 힐라 입장 가능\r\n\r\n#fc0xFF990033#[보스 랭크 7레벨]\r\n#b검은마법사, 세렌 입장 가능\r\n\r\n#fc0xFF990033#[보스 랭크 8레벨]\r\n#b헬 모드 보스 입장 가능");
            //cm.sendOk("#fs11##fc0xFF990033#[랭크별 승급버프]\r\n\r\n#b보스 공격력 + 10%\r\n\r\n#fc0xFF990033#[랭크별 혜택]\r\n\r\n#b[보스 랭크 1레벨 혜택]#k\r\n#fc0xFF6600CC#- 카루타 아래 모든 보스 입장횟수 +1 (시그너스 제외)\r\n- 노말 하드 스우, 데미안 입장횟수 +1#k\r\n\r\n #b[보스 랭크 2레벨 혜택]#k\r\n#fc0xFF6600CC#- 카루타 아래 모든 보스 입장횟수 +1 (시그너스 제외)\r\n- 노말 하드 루시드, 윌 입장횟수 +1#k\r\n\r\n #b[보스 랭크 3레벨 혜택]#k\r\n#fc0xFF6600CC#- 카루타 아래 모든 보스 입장횟수 +1 (시그너스 제외)\r\n- 크로스 보스 입장가능\r\n- 진힐라 입장 횟수 +1#k\r\n\r\n #b[보스 랭크 4레벨 혜택]#k\r\n#fc0xFF6600CC#- 카루타 아래 모든 보스 입장횟수 +1 (시그너스 제외)\r\n- 검은 마법사 입장횟수 +1\r\n- 더스크 입장 횟수 +1\r\n- 듄켈 입장횟수 +1\r\n\r\n #b[보스 랭크 5레벨 혜택]#k\r\n#fc0xFF6600CC#- 카루타 아래 모든 보스 입장횟수 +1 (시그너스 제외)\r\n- 노말 하드 스우, 데미안 입장횟수 +1#k\r\n\r\n #b[보스 랭크 6레벨 혜택]#k\r\n#fc0xFF6600CC#- 카루타 아래 모든 보스 입장횟수 +1 (시그너스 제외)\r\n- 노말 하드 루시드, 윌 입장횟수 +1#k\r\n\r\n #b[보스 랭크 7레벨 혜택]#k\r\n#fc0xFF6600CC#- 카루타 아래 모든 보스 입장횟수 +1 (시그너스 제외)\r\n- 크로스 입장횟수 +1\r\n- 진힐라 입장횟수 +1#k\r\n\r\n #b[보스 랭크 8레벨 혜택]#k\r\n#fc0xFF6600CC#- 카루타 이하 모든 보스 입장횟수 +1 (시그너스 제외)\r\n- 검은 마법사 입장횟수 +1\r\n- 크로스 입장횟수 +1#k\r\n\r\n");
            cm.dispose();
        }
    } else if (status == 2) {
        /*if (cm.getPlayer().getBossPoint() < req[gK()][0]) {
            cm.sendOk("보스포인트가 부족합니다.");
            cm.dispose();
            return;
        }*/
        for (i=0; i<req[gK()-0][1].length; i++) {
            if (cm.itemQuantity(req[gK()][1][i][0]) < req[gK()][1][i][1]) {
                cm.sendOk("#fs11#승급에 필요한 #e재료#n가 모자란 것 같군.");
                cm.dispose();
                return;
            }
        }

        ggK = gK();
        if (Math.floor(Math.random() * 100) < 100) {
            cm.dispose();
            try {
                if ((gK()+1) >= 6) // 6랭크 이상 승급 성공시 월드메세지 전송
                    cm.worldGMMessage(22, "[보스랭크] "+cm.getPlayer().getName()+"님이 " + (gK()+1) + "랭크로 승급하셨습니다.");
                //Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/랭크승급/[보스랭크승급].log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n승급등급 : " +Integer.parseInt(gK()+1) + "\r\n\r\n", true);
                cm.addCustomLog(2, "[보스랭크] 승급등급 : " + Integer.parseInt(gK()+1) + "");
                cm.effectText("#fn나눔고딕 ExtraBold##fs20#[보스랭크] < " + (gK()+1) + " > 랭크로 승급하였습니다", 50, 1000, 6, 0, 330, -550);

                cm.getPlayer().setBossTier(gK() + 1);
                cm.getPlayer().saveToDB(false, false);
                cm.getPlayer().setBonusCTSStat();
                cm.showEffect(false, "Effect/EventEffect.img/SalonDebut/screenEff/1366");
                cm.showEffect(false, "Effect/CharacterEff.img/GradeUp");
            } catch(err) {
                cm.addCustomLog(50, "[BossRank.js] 에러 발생 : " + err + "");
            }
        } else {
           cm.sendOk("#fs11#승급에 실패하였습니다.")
        }
        for (i=0; i<req[ggK][1].length; i++) {
            cm.gainItem(req[ggK][1][i][0], -req[ggK][1][i][1]);
        }

    }
}

function gK() {
    return cm.getPlayer().getBossTier();
}