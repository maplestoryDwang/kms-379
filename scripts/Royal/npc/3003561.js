var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
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
        if (cm.getPlayer().getMap().getNumMonsters() == 0 && (cm.getPlayer().getMapId() == 450008350)) {
            cm.askYesNo("윌을 물리쳤다. '거울에 비친 빛의 신좌'로 돌아갈까?", Packages.scripting.GameObjectType.User, Packages.scripting.ScriptMessageFlag.Self);
        } else {
            cm.askYesNo("이곳은 위험하다. 전투를 포기하고 밖으로 나갈까?", Packages.scripting.GameObjectType.User, Packages.scripting.ScriptMessageFlag.Self);
        }
    } else if (status == 1) {
        cm.getPlayer().setKeyValue("bossPractice", "0");
	cm.getPlayer().cancelEffectFromBuffStat(Packages.client.MapleBuffStat.DebuffIncHp);
        cm.warp(450007240, 1);
        cm.dispose();
    }
}