var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) {
	var text = "#fUI/Basic.img/RoyalBtn/StartImg/0#";
		
	text += "\r\n#fn나눔고딕 Extrabold##d							로얄메이플 에 오신걸 환영합니다#k\r\n#r								디스코드 공지사항 필수#k\r\n#b									즐거운 시간 되세요";
	cm.sendOk(text);
	cm.dispose();
    }
}