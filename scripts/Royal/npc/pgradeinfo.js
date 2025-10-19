function start() {
    status = -1;
    action(1, 0, 0);
}

var serverName = "로얄"

function action(mode, type, selection) {
    dialogue = [
   [""],
   ["#fc0xFF54ff00#[강림월드 서포트 등급]#k"],
   ["#b서포트 등급에 따라 아래와 같은 버프가 지급됩니다.#k"],
   ["#fs18##rLv.1#k #fc0xFFFF3366# #k -[누적 100회] \r\nㄴ 데미지, 보스 공격시 데미지 5%, 크리티컬 데미지 1%\r\n#rLv.2#k #fc0xFFFF3366# #k - [누적 300회] \r\nㄴ 데미지, 보스 공격시 데미지 10%, 크리티컬 데미지 3%\r\n#rLv.3#k #fc0xFFFF3366# #k - [누적 600회] \r\nㄴ 데미지, 보스 공격시 데미지 20%, 크리티컬 데미지 5%\r\n#rLv.4#k #fc0xFFFF3366# #k - [누적 1000회] \r\nㄴ 데미지 40%, 보스 공격시 데미지 30%, 크리티컬 데미지 7%\r\n#rLv.5#k #fc0xFFFF3366# #k - [누적 2000회] \r\nㄴ 데미지 60%, 보스 공격시 데미지 40%, 크리티컬 데미지 10%\r\n#rLv.6#k #fc0xFFFF3366# #k - [누적 3000회] \r\nㄴ 데미지 80%, 보스 공격시 데미지 50%, 크리티컬 데미지 15%"],
   ];
    if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }
    if (status == 0) {
   cm.getClient().getSession().writeAndFlush(Packages.network.models.CField.blind(0x01, 200, 0, 0, 0, 800, 0));
   cm.getPlayer().setInGameDirectionMode(true, true, false, false);
   cm.getPlayer().InGameDirectionEvent("", 0x01, 1000);
    } else if (status > 0) {
       if (status == (dialogue.length - 1)) {
           cm.getPlayer().InGameDirectionEvent(dialogue[status], 0x0C, 1);
       } else if (status == dialogue.length) {
           cm.getPlayer().InGameDirectionEvent("", 0x01, 1000);
           cm.getPlayer().InGameDirectionEvent("", 0x16, 700);
       } else if (status == (dialogue.length + 1)) {
           cm.getPlayer().removeInGameDirectionMode();
           cm.getClient().getSession().writeAndFlush(Packages.network.models.CField.blind(0, 0, 0, 0, 0, 800, 0));
           cm.dispose();
       } else {
           cm.getPlayer().InGameDirectionEvent(dialogue[status], 0x0C, 0);
       }
    }
}