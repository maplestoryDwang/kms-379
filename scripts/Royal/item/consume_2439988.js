
var status;
var select = -1;
var book  = new Array(1012632, 1672082, 1022278, 1132308, 1122430, 1182285, 1032316, 1113306, 1162080, 1162081, 1162082, 1162083,1190555,1190556,1190557,1190558,1190559);

function start() {    status = -1;
    action(1, 1, 0);
}

function action(mode, type, selection) {
    if (mode <= 0) {
        cm.dispose();
    	return;
    } else {
        if (mode == 1)
            status++;
        if (status == 0) {
	    var text = "받고 싶은 칠흑의 보스 세트 아이템을 선택해줘#l\r\n\r\n#b";
		for (var i = 0; i < book.length; i++) {
		    text+="#L"+i+"##i"+book[i]+"# #z"+book[i]+"##l\r\n";
		}
		cm.sendSimple(text);
	} else if (status == 1) {
		select = selection;
		cm.sendYesNo("받을 아이템이 #b#z"+book[select]+"##k 맞아?");
	} else if (status == 2) {
	    if (cm.haveItem(2439988, 1)) {
		if (cm.canHold(2439988)) {
		    cm.sendOk("인벤토리를 확인하세요");
		    cm.gainItem(2439988, -1);
		    cm.gainItem(book[select], 1);
		    cm.dispose();
		} else {
		    cm.sendOk("장비칸에 빈 공간이 없습니다.");
		    cm.dispose();
		}
            } else {
		cm.sendOk("부족합니다.");
		cm.dispose();

}
	}
    }
}