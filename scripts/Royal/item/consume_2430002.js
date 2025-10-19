/*

채원


*/


검정 = "#fc0xFF191919#"
var status = -1;

function start() {
    status = -1;
    action (1, 0, 0);
}

function action(mode, type, selection) {

    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        status --;
    }
    if (mode == 1) {
        status++;
    }

    if (status == 0) {
        말 = "원하는 아이템을 선택해보세요.\r\n\r\n";
        말 += "#L0##i1009000# #b#z1009000##k#l\r\n";
        말 += "#L1##i1059000# #b#z1059000##k#l\r\n";
        말 += "#L2##i1109000# #b#z1109000##k#l\r\n";
        말 += "#L3##i1079000# #b#z1079000##k#l\r\n";
        말 += "#L4##i1703900# #b#z1703900##k#l\r\n\r\n";
        말 += "#r※ 주의 : 잘못 선택 시 환불이 불가능합니다.#k";
        cm.sendOk(말);
        return;
    } else if (status == 1) {
        if (selection == 0) {
            cm.gainItem(1009000, 1);
            cm.gainItem(2430002, -1);
            cm.sendOk("#b#i1009000##z1009000##k 아이템이 지급되었습니다.");
            cm.dispose();
        } else if (selection == 1) {
            cm.gainItem(1059000, 1);
            cm.gainItem(2430002, -1);
            cm.sendOk("#b#i1059000##z1059000##k 아이템이 지급되었습니다.");
            cm.dispose(); 
        } else if (selection == 2) {
            cm.gainItem(1109000, 1);
            cm.gainItem(2430002, -1);
            cm.sendOk("#b#i1109000##z1109000##k 아이템이 지급되었습니다.");
            cm.dispose();
        } else if (selection == 3) {
            cm.gainItem(1079000, 1);
            cm.gainItem(2430002, -1);
            cm.sendOk("#b#i1079000##z1079000##k 아이템이 지급되었습니다.");
            cm.dispose();
        } else if (selection == 4) {
            cm.gainItem(1703900, 1);
            cm.gainItem(2430002, -1);
            cm.sendOk("#b#i1703900##z1703900##k 아이템이 지급되었습니다.");
            cm.dispose();
        }
    }
}
