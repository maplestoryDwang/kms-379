importPackage(Packages.scripting);

var status = -1;
var sel = -1;
var sel2 = -1;
var f = -1;

function start(flag) {
    status = -1;
    sel = -1;
    sel2 = -1;
    f = flag;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    var player = cm.getPlayer();
    if (status == 0) {
        if (f == 1) { // 후원 시스템
            cm.dispose();
            cm.openNpc(2003, "D후원시스템");
        } else if (f == 2) { // 홍보 시스템
            cm.dispose();
            cm.openNpc(2003, "D홍보시스템");
        } else if (f == 3) { // 닉네임 변경
            cm.dispose();
            cm.openNpc(2003, "D닉네임변경");
        } else if (f == 4) { // 직업 변경
            cm.dispose();
            cm.openNpc(2003, "D자유전직");
       } else if (f == 5) { // V 매트릭스
           cm.dispose();
           cm.openNpc(2003, "D매트릭스");
        } else if (f == 6) { // 유니온
            cm.dispose();
            cm.openNpc(2003, "D유니온시스템");
        } else if (f == 7) { // 전투력 측정
            cm.dispose();
            cm.openNpc(2003, "D전투력측정");
        } else if (f == 8) { // 성장 시스템
            cm.dispose();
            cm.openNpc(2003, "D성장시스템");
        } else if (f == 9) { // 결혼 시스템
            cm.dispose();
            cm.openNpc(2003, "D결혼시스템");
        } else if (f == 10) { // 랭킹 시스템
            cm.dispose();
            cm.openNpc(2003, "D랭킹시스템");
        } else if (f == 11) { // 강화 시스템
            cm.dispose();
            cm.openNpc(2003, "D강화시스템");
       }
    }
}