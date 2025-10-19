function start() {
    status = -1;
    action(1, 0, 0);
}

var serverName = "강림월드"

function action(mode, type, selection) {
    dialogue = [
   [""],
   ["#fc0xFF54ff00#[랭크 승급 버프]#k"],
   ["#fs16##b랭크 승급마다 아래 버프가 중첩 됩니다."],
   ["#r메소 획득량 + 10%\r\n아이템 드롭률 +10%\r\n크리티컬 데미지 + 5%\r\n보스공격력 +5%"],
   ["#fs20##fc0xFF54ff00#[랭크 종류]#k"],
   ["#fc0xFF330000##fEtc/ZodiacEvent.img/0/icon/1/0#[Bronze] #fc0xFF999999##fEtc/ZodiacEvent.img/0/icon/2/0#[Silver] #fc0xFFCCCC33##fEtc/ZodiacEvent.img/0/icon/3/0#[Gold] #fc0xFF339966##fEtc/ZodiacEvent.img/0/icon/4/0#[Platinum] #fc0xFF00CCCC##fEtc/ZodiacEvent.img/0/icon/5/0#[Diamond]\r\n#fc0xFF990066##fEtc/ZodiacEvent.img/0/icon/6/0#[Master] #fc0xFFCC0066##fEtc/ZodiacEvent.img/0/icon/7/0#[Grand Master] #fc0xFFFF6600##fEtc/ZodiacEvent.img/0/icon/8/0#[Challenger]\r\n\r\n#fc0xFFFFFFFF##fEtc/ZodiacEvent.img/0/icon/9/0#[Noble] #fEtc/ZodiacEvent.img/0/icon/10/0#[Imperial] #fEtc/ZodiacEvent.img/0/icon/11/0#[Specialist]\r\n\r\n오늘도 #fc0xFFFF3366#강림 월드#fc0xFFFFFFFF#를 이용해주셔서 감사합니다."],
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