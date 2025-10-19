importPackage(java.lang);
importPackage(Packages.tools.packet);
importPackage(Packages.handling.world);

req = [
    [0, [[4310266, 300], [4001715, 10]], 100000000, "[Bronze]"],
    [0, [[4310266, 700], [4001715, 20]], 2000000000, "[Silver]"],
    [0, [[4310266, 1300], [4001715, 50]], 5000000000, "[Gold]"],
    [0, [[4310266, 2300], [4001715, 150]], 15000000000, "[Platinum]"],
    [0, [[4310266, 4500], [4001715, 300]], 30000000000, "[Diamond]"],
    [0, [[4310266, 6000], [4001715, 500]], 50000000000, "[Master]"],
    [0, [[4310266, 9500], [4001715, 750]], 75000000000, "[Grand Master]"],
    [0, [[4310266, 15000], [4001715, 900]], 90000000000, "[Challenger]"],
    [0, [[4310266, 30000], [4001715, 900]], 90000000000, "[Noble]"],
    [0, [[4310266, 100000], [4001715, 2000]], 200000000000, "[Imperial]"],
    [0, [[4310266, 200000], [4001715, 4000]], 400000000000, "[Specialist]"]
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
/*
        if (gK() - 1 >= 10) {
            cm.sendOk("#fs11#최고단계까지 승급을 하여 더 이상 승급을 하실 수 없습니다.");
            cm.dispose();
            return;
        }
*/
        if (gK() >= 11) {
            말 = "#fs11#최고단계까지 승급을 하여 더 이상 승급을 하실 수 없습니다.\r\n\r\n";
        }
        if (gK() < 11) {
            말 = "#fs11##fc0xFF990033##e메인랭크 승급 시스템#n#fc0xFF000000#이라네\r\n#b메인랭크 승급#fc0xFF000000#을 통해 더욱 더 강해져보지 않겠나!?\r\n\r\n"
            말 += "#L0##b다음 랭크로 승급하고 싶습니다#l#k#n\r\n";
        }
        말 += "#L1##b랭크의 혜택이 궁금합니다#l#k#n";
        cm.sendSimple(말);

    } else if (status == 1) {
        if (selection == 0) {
            말 = "#fs11##b다음 랭크 : " + req[gK()][3] + "\r\n#k다음 랭크로 승급을 하기 위해선 아래와 같은 재료가 필요하다네\r\n\r\n"
            말 += "#k\r\n";
            for (i = 0; i < req[gK()][1].length; i++) {
                말 += "#i" + req[gK()][1][i][0] + "# #b#z" + req[gK()][1][i][0] + "##r " + req[gK()][1][i][1] + "개#k\r\n"; // 개수 : cm.itemQuantity(req[gK()][1][i][0])
            }
            //말 += "#i4031138# #b메소 #r" + req[gK()][2] + "#k\r\n\r\n"
            //말 += "#r#e주의 : 아이템을 지급받을 장비 칸과 소비칸을 5칸 이상 비워주게.#k#n\r\n"
            말 += " #r정말 승급을 하겠나?#k"
            cm.sendYesNo(말);
        } else {
            cm.dispose();
            cm.openNpcCustom(cm.getClient(), 9062294, "mainrankinfo");
        }
    } else if (status == 2) {
        for (i = 0; i < req[gK()][1].length; i++) {
            if (cm.itemQuantity(req[gK()][1][i][0]) < req[gK()][1][i][1]) {
                cm.sendOk("#fs11#승급에 필요한 #e재료#n가 부족한게 아닌가?");
                cm.dispose();
                return;
            }
        }
        before = gK();
        after = gK() + 1;
        if (Math.floor(Math.random() * 100) < 100) {

            cm.dispose();
            try {
                if (after >= 7 &&after <= 9) { // GrandMaster(7) ~ Overlord(9)
                    cm.worldGMMessage(22, "[메인랭크] " + cm.getPlayer().getName() + "님이 " + req[gK()][3] + "랭크로 승급하셨습니다.");
                } else if (after >= 10) { // Verga, Sirius
                    cm.worldGMMessage(5, "[메인랭크] " + cm.getPlayer().getName() + "님이 " + req[gK()][3] + "랭크로 승급하셨습니다.");
                }
             
                //Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/랭크승급/[메인랭크승급].log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n승급등급 : " + req[gK()][3] + " - " + Integer.parseInt(gK()+1) + "\r\n\r\n", true);
                cm.addCustomLog(1, "[메인랭크] 승급등급 : " + req[gK()][3] + " - " + Integer.parseInt(gK()+1) + "");
                cm.effectText("#fn나눔고딕 ExtraBold##fs20#[메인랭크] < " + req[gK()][3] + " > 랭크로 승급하였습니다", 50, 1000, 6, 0, 330, -550);

                cm.getPlayer().setKeyValue(190823, "grade", "" + (gK() + 1) + "");
                cm.getPlayer().setBonusCTSStat();

                prevflag = cm.getPlayer().getSaveFlag();
                cm.getPlayer().setSaveFlag(64); // QuestInfo
                cm.getPlayer().saveToDB(false, false);
                cm.getPlayer().setSaveFlag(prevflag)

                cm.showEffect(false, "Effect/EventEffect.img/SalonDebut/screenEff/1366");
                cm.showEffect(false, "Effect/CharacterEff.img/GradeUp");
            } catch(err) {
                cm.addCustomLog(50, "[ZodiacRank.js] 에러 발생 : " + err + "");
            }
        } else {
            cm.sendOk("#fs11#승급에 실패 하였습니다.")
        }

        if (after < 10) {
            cm.gainItem(req[before][1][0][0], -req[before][1][0][1]);
            cm.gainItem(req[before][1][1][0], -req[before][1][1][1]);
        } else if (after == 10) { // Vega
            cm.gainItem(4310266, -30000);
            cm.gainItem(4310266, -30000);
            cm.gainItem(4310266, -30000);
            cm.gainItem(4310266, -10000);
            cm.gainItem(req[before][1][1][0], -req[before][1][1][1]);
        } else if (after == 11) { // Sirius
            cm.gainItem(4310266, -30000);
            cm.gainItem(4310266, -30000);
            cm.gainItem(4310266, -30000);
            cm.gainItem(4310266, -30000);
            cm.gainItem(4310266, -30000);
            cm.gainItem(4310266, -30000);
            cm.gainItem(4310266, -20000);
            cm.gainItem(req[before][1][1][0], -req[before][1][1][1]);
        }

    }
}

function gK() {
    return cm.getPlayer().getKeyValue(190823, "grade");
}
