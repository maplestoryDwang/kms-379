importPackage(java.sql);
importPackage(java.lang);
importPackage(Packages.database);
importPackage(Packages.launch.world);
importPackage(Packages.packet.creators);

// 세팅
var status = -1;
var own = 3202
var need = 2635012
var name = '비숑과 산책 시간 라이딩'

function start() {
    status = -1;
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }
    if (status == 0) {
        cm.teachSkill(80000000+own, 1, 0)
        cm.gainItem(need, -1);
        cm.showInfo('[스킬] '+name+' 라이딩 획득!!');
        cm.updateChar();
        cm.dispose();
    }
}
