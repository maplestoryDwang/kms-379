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
            cm.askYesNo("이곳은 위험하다. 전투를 포기하고 밖으로 나갈까?", Packages.scripting.GameObjectType.User, Packages.scripting.ScriptMessageFlag.Self);
    } else if (status == 1) {
        cm.warp(450004000, 0);
        cm.dispose();
    }
}