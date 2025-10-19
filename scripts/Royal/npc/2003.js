


/*

	* 단문엔피시 자동제작 스크립트를 통해 만들어진 스크립트 입니다.

	* (Guardian Project Development Source Script)

	화이트 에 의해 만들어 졌습니다.

	엔피시아이디 : 2004

	엔피시 이름 : 토드

	엔피시가 있는 맵 : The Black : Night Festival (100000000)

	엔피시 설명 : MISSINGNO


*/
importPackage(java.lang);
importPackage(Packages.constants);
importPackage(Packages.handling.channel.handler);
importPackage(Packages.tools.packet);
importPackage(Packages.handling.world);
importPackage(java.lang);
importPackage(Packages.constants);
importPackage(Packages.server.items);
importPackage(Packages.client.items);
importPackage(java.lang);
importPackage(Packages.launch.world);
importPackage(Packages.tools.packet);
importPackage(Packages.constants);
importPackage(Packages.client.inventory);
importPackage(Packages.server.enchant);
importPackage(java.sql);
importPackage(Packages.database);
importPackage(Packages.handling.world);
importPackage(Packages.constants);
importPackage(java.util);
importPackage(java.io);
importPackage(Packages.client.inventory);
importPackage(Packages.client);
importPackage(Packages.server);
importPackage(Packages.tools.packet);
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
        //cm.Entertuto(true);
cm.sendScreenText("#fs25##fc0xFFF2CB61#에테르넬 아이템#k에 대해 설명드리겠습니다.", false);
    } else if (status == 1) {
        cm.sendScreenText("#fs25##fc0xFFF2CB61#에테르넬 아이템#k에 대해 설명드리겠습니다.", false);
    } else if (status == 2) {
        cm.sendScreenText("#fs17##i1005980# #z1005980# (모자)\r\n#i1042433# #z1042433# (상의)\r\n#i1062285# #z1062285# (하의)", false);
    } else if (status == 3) {
        cm.sendScreenText("#fc0xFFF2CB61#에테르넬 아이템#k은 총 #fc0xFF6B66FF#3부위#k로 나뉘어져 있습니다.", false);
    } else if (status == 4) {
        cm.sendScreenText("\r\n#fc0xFFF2CB61#모자#k는 #fc0xFF6B66FF#[초월한 아케인셰이드 모자]#k로\r\n#fc0xFFF2CB61#상의#k는 #fc0xFF6B66FF#[초월한 파프니르 상의]#k로\r\n#fc0xFFF2CB61#하의#k는 #fc0xFF6B66FF#[초월한 파프니르 하의]#k로\r\n승급이 가능합니다.", true);
    } else if (status == 5) {
        cm.sendScreenText("#fs25##fc0xFFF2CB61#'승급 시스템' 이란?#k", false);
    } else if (status == 6) {
        cm.sendScreenText("#fs17##fc0xFFFFBB00#승급 시스템은#k 아이템 옵션 전승 이라는 고유 기능을 가지고 있습니다.", false);
    } else if (status == 7) {
        cm.sendScreenText("#fc0xFF6B66FF##fs20#1. [아이템 강화 전승]#k\r\n#fs17#주문서 작, 메소강화, 스타포스강화 가 #fc0xFFF29661#모두 전승 되는 시스템#k 입니다.", false);
    } else if (status == 8) {
        cm.sendScreenText("#fc0xFF6B66FF##fs20#2. [추옵 전승]#k\r\n#fs17#추옵은 추옵으로 전승되지않으며 일반 강화 옵션인\r\n파란색 + 스탯으로 적용되니 #fc0xFFF29661#추옵을 잘 띄운후 전승#k 을 추천드립니다.", false);
    } else if (status == 9) {
        cm.sendScreenText("\r\n#fs15#그에따라 기본추옵은 강화스탯으로 적용되며\r\n승급후 #fc0xFFF29661#추옵 설정을#k 또 할 수 있습니다", true);
    } else if (status == 10) {
        cm.sendScreenText("#fs24##fc0xFFFFBB00#승급 재료#k는 #r익스트림 보스#k 를 통해 획득이 가능합니다.", false);
    } else if (status == 11) {
        cm.sendScreenText("\r\n#fs22##fc0xFFF15F5F#첫번째#k, #fs20#익스트림 모드는 파티가 불가능하며 #r솔플#k만 가능합니다.\r\n#fs15##fc0xFFF29661#쩔, 엔드템 밸런스#k 등을 위함 입니다.", false);
    } else if (status == 12) {
        cm.sendScreenText("#fs22##fc0xFFF15F5F#두번째#k, #fs20#익스트림 모드에서는 데미지감소가 90% 적용됩니다.\r\n#fs15#보스의 체력은 #fc0xFFF29661#하드#k 모드와 동일합니다.", true);
    } else if (status == 13) {
        cm.sendScreenText("#fs25##fc0xFF6B66FF#승급#k 시스템 의 #fc0xFFA566FF#주의사항#k", false);
    } else if (status == 14) {
        cm.sendScreenText("#fs20#\r\n#fc0xFF6B66FF#승급된 아이템#k은 #fc0xFFF15F5F#귀속 아이템#k이며 교환이 불가능합니다.", false);
    } else if (status == 15) {
        cm.sendScreenText("#fs20##fc0xFF6B66FF#승급된 아이템#k은 #fc0xFFF15F5F#귀속 아이템#k이며 창고이용이 가능합니다", false);
    } else if (status == 16) {
        cm.sendScreenText("#fs20##fc0xFF6B66FF#승급된 아이템#k은 #fc0xFFF15F5F#귀속 아이템#k이며 승급취소가 불가능합니다", false);
    } else if (status == 17) {
        cm.sendScreenText("#fs16#\r\n\r\n항상 저희 #fc0xFFF2CB61#제니아 리턴즈#k를 이용해주셔서 감사합니다", true);
    } else if (status == 18) {
        //cm.Endtuto();
        cm.dispose();
    }
}
