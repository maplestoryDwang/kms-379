importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);
importPackage(java.text);
importPackage(java.awt);


var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && type == 3) {
	cm.sendNext("뭐야 이랬다 저랬다하고! 하지만 얼마 안 있어 엉엉 울면서 돌아가고 싶어할걸?");
	cm.dispose();
    }
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;

        if (status == 0) {
	if (cm.getPlayer().getMap().checkDojangClear()) {
		cm.sendYesNo("뭐야? 상대방 쓰러뜨려놓고 어딜가? 이제 다음 단계로 넘어가기만 하면 되는데? 급한 볼일이라도 생각나셨나?");
	} else {
		cm.sendYesNo("여기서 포기하는거야? 역시 후회할줄 알았다고!");
	}
        } else if (status == 1) {
	cm.warp(925020002);
	cm.dispose();
        }
    }
}