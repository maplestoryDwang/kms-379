// 편의시스템
importPackage(Packages.scripting.newscripting);
importPackage(java.text);
importPackage(java.lang);
importPackage(Packages.constants);

var enter = "\r\n";
var seld = -1;

파랑 = "#fc0xFF0054FF#";
연파 = "#fc0xFF6B66FF#";
연보 = "#fc0xFF8041D9#";
보라 = "#fc0xFF5F00FF#";
노랑 = "#fc0xFFEDD200#";
검정 = "#fc0xFF191919#";

펫 = "#fUI/CashShop.img/CashItem_label/5#";
펫1 = "#fUI/CashShop.img/CashItem_label/8#";
길드 = "#fUI/Basic.img/RoyalBtn/theblackcoin/14#";
젬스톤 = "#fUI/Basic.img/RoyalBtn/theblackcoin/19#";
이름변경 = "#fUI/Basic.img/RoyalBtn/theblackcoin/20#";
자유전직 = "#fUI/Basic.img/RoyalBtn/theblackcoin/21#";
창고 = "#fUI/Basic.img/RoyalBtn/theblackcoin/13#";
어빌리티 = "#fUI/Basic.img/RoyalBtn/theblackcoin/22#";
포인트 = "#fUI/Basic.img/RoyalBtn/theblackcoin/7#";
포켓 = "#fUI/Basic.img/RoyalBtn/theblackcoin/23#";
랭킹 = "#fUI/Basic.img/RoyalBtn/theblackcoin/24#";
템버리기 = "#fUI/Basic.img/RoyalBtn/theblackcoin/25#";
추천인 = "#fUI/Basic.img/RoyalBtn/theblackcoin/41#";
검은마법사 = "#fUI/Basic.img/RoyalBtn/theblackcoin/42#";
위장색 = "#fUI/Basic.img/RoyalBtn/theblackcoin/43#";
정령 = "#fUI/UIWindow4.img/pointShop/100712/iconShop#";
후원 = "#fUI/UIWindow4.img/pointShop/501053/iconShop#";
색 = "#fc0xFF6600CC#"
검은색 = "#fc0xFF000000#"
핑크색 = "#fc0xFFFF3366#"
분홍색 = "#fc0xFFF781D8#"
파란색 = "#fc0xFF479AEF#"

function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, sel) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        if (cm.inBoss()) {
            cm.getPlayer().dropMessage(5, "보스 진행중엔 이용이 불가능합니다.");
            cm.dispose();
            return;
        }

        var choose = "#fs11##fUI/Basic.img/Zenia/SC/5#\r\n";
        choose += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#\r\n";
        //choose += "#L20##fc0xFFFF3636#" + 템버리기 + " 아이템 버리기#l";

        choose += "　#L248##fc0xFFFF5E00#" + 랭킹 + " 각종랭킹확인#l　";
        choose += "#L6##fc0xFF6B66FF# 일반펫 분양#l　";
        choose += "#L21##fc0xFF626262#" + 길드 + " 길드본부이동#l　\r\n\r\n";

        choose += "　#L26##fc0xFF8041D9#" + 추천인 + " 추천인시스템#l　";
        //choose += "#L13##fc0xFF7112FF#" + 위장색 + " 메카닉 위장색#l\r\n";
        choose += "#L24##fc0xFF626262#" + 검은마법사 + " 창고 이용#l　　";
        choose += "#L9##fc0xFF626262#" + 검은마법사 + " 스탯 초기화#l\r\n\r\n";
        //choose += "#L27##fc0xFFFF3636#" + 검은마법사 + " 특별스킬 학습#l\r\n";

        choose += "　#L777##fc0xFF8041D9#" + 추천인 + " 뽑기 시스템#l　  ";
        choose += "#L779##fc0xFF626262#" + 검은마법사 + " 팬텀스틸스킬#l ";
        choose += "#L780##fc0xFF626262#" + 검은마법사 + " 은월오류해결#l\r\n\r\n";

        choose += "　　　#L245#" + 파란색 + 포켓 + " [장비] 수집 시스템#l　";
        choose += "#L246#" + 파란색 + 포켓 + " [종합] 원클릭 시스템#l　\r\n\r\n";

        choose += "#fc0xFFD5D5D5#───────────────────────────#k\r\n";
        //choose += "#L11##fc0xFF6B66FF#" + 젬스톤 + " V 코어 매트릭스#k를 이용하겠습니다.\r\n#l";
        //choose += "#L16##fc0xFFF29661#" + 자유전직 + " 직업변경 시스템#k을 이용하겠습니다.\r\n#l";
        //choose += "#L9##fc0xFFF29661#" + 포켓 + " 딜량체크 시스템#k을 이용하겠습니다.\r\n#l";
        //choose += "#L8##fc0xFFF29661#" + 어빌리티 + " 유니온 시스템#k을 이용하겠습니다.\r\n#l";
        //choose += "#L7##fc0xFFF29661#" + 이름변경 + " 캐릭터 이름#k을 변경하겠습니다.\r\n#l";

        //choose += "#L779#" + 색 + 추천인 + " [팬텀] 스틸 스킬#k을 이용하겠습니다.\r\n#l";
        //choose += "#L780#" + 색 + 정령 + " [은월] 상호작용 오류 해결#k을 이용하겠습니다.\r\n#l";
        //choose += "#L777#" + 색 + 어빌리티 + " 뽑기 시스템#k을 이용하겠습니다.\r\n#l";
        choose += "   #L778#" + 창고 + " #b택배 시스템#k을 이용하겠습니다.\r\n#l";
        //choose += "#L555#" + 색 + 후원 + " 후원 시스템#k을 이용하겠습니다.\r\n#l";
        //choose += "#L556#" + 색 + 후원 + " 홍보 시스템#k을 이용하겠습니다.\r\n#l";
        cm.sendSimple(choose);

    } else if (status == 1) {
        seld = sel;
        switch (sel) {
            case 9999:
                var msg = "#fs11#냥?";

                cm.sendOk(msg);
                cm.dispose();
                break;
            case 1:
                var msg = "\r\n#fs11##fUI/UIWindow8.img/EldaGauge/tooltip/47# #e초기 육성 TIP#n\r\n #b[1] 캐릭터 전직 시 지급되는 장비 지원 상자를 열고 장비 착용\r\n [2] 성장 컨텐츠 -> 퀘스트 -> 육성 다이어리 클리어\r\n [3] 제작할 수 있는 초기 악세서리 제작하기\r\n [4] 큐브를 통한 장비들의 잠재옵션 최대한 맞추기\r\n [5] 성장 컨텐츠 -> 장비강화 -> 메소강화 를 이용해 모든 장비 가성비 작 하기\r\n [6] 유니온 전용 캐릭터들을 키우기\r\n [7] 훨씬 강해지고 싶다면 서버를 홍보하고 홍보포인트 얻기\r\n [8] 쉴땐 마을에서 의자에 앉아있기 (1분마다 네오 젬을 수급하며 휴식상점에서 사용가능)\r\n\r\n";
                msg += "#fs11##fUI/UIWindow8.img/EldaGauge/tooltip/46# #e재화 TIP#n\r\n\r\n#r#i4033172##z4033172##k\r\n#fs 11#  획득처 : #b카오스 루타비스#k\r\n  사용처 : #b초월한 파프니르 제작 재료#k\r\n\r\n#r#i4031227##z4031227##k\r\n#fs 11#  획득처 : #b스우 이상 모든 보스, 광부 사냥터#k\r\n  사용처 : #b장비 강화#k\r\n\r\n";
                //msg += "#fs11##fUI/UIWindow8.img/EldaGauge/tooltip/45# #e핫타임#n\r\n\r\n- 오후 10시30분, 접속중이기만 하면 OK\r\n- 게임 플레이에 도움되는 여러 아이템 수령이 가능합니다."+enter;


                cm.sendSimple(msg);
                break;
            case 2:
                var msg = "#fs11##fUI/UIWindow8.img/EldaGauge/tooltip/46# #r만렙 : 500#k#b\r\n\r\n#fUI/UIWindow8.img/EldaGauge/tooltip/46# 경험치 배율\r\n0~200: 400배\r\n200~275: 150배\r\n275~300: 1500배\r\n300~320: 900배\r\n320~340: 800배\r\n340~370: 700배\r\n370~400: 600배\r\n400~410: 500배\r\n410~450: 400배\r\n450~490: 300배\r\n490~500: 100배#k";
                msg += "";

                cm.sendSimple(msg);
                break;
            case 3:
                var msg = "\r\n#fs11##fUI/UIWindow8.img/EldaGauge/tooltip/50# #e홍보 안내#n\r\n\r\n- 홍보 안내는 홈페이지를 참고하여 주세요.\r\n";
                msg += "" + enter;

                cm.sendSimple(msg);
                break;
            case 4:
                var msg = "\r\n#fs11##fUI/UIWindow8.img/EldaGauge/tooltip/52# #e유니온 & 링크 시스템#n\r\n\r\n- 유니온/링크 시스템은 공격력에 많은 영향을 미칩니다.\r\n- 유니온 시스템 관련 내용은 홈페이지에 추가 예정입니다.\r\n- 하단의 링크 효과는 120레벨을 기준으로 작성되었습니다.\r\n\r\n\r\n";
                msg += "\r\n#fs11##fUI/UIWindow8.img/EldaGauge/tooltip/43# #e직업 별 링크 효과#n\r\n\r\n- 메르세데스 : 경험치 획득량 15% 증가\r\n- 아란 : 콤보킬 구슬 경험치 획득량 650% 증가\r\n- 에반 : 룬 해방의 지속시간 50% 증가\r\n- 루미너스 : 몬스터 방어 무시 15% 증가\r\n- 팬텀 : 크리티컬 확률 15% 증가\r\n- 은월 : 사망에 이르는 피격 시 10% 확률로 생존\r\n" + enter;
                msg += "- 일리움 : 일정 시간 내 거리를 이동할 때마다 데미지 증가\r\n- 카데나 : 캐릭터보다 레벨이 낮은 몬스터에게 데미지 6% 증가\r\n- 아크 : 전투 지속 시 데미지 증가\r\n" + enter;
                msg += "- 엔젤릭버스터 : 사용시 10초간 데미지 90% 상승\r\n- 카이저 : 최대 HP 15% 증가\r\n\r\n- 캐논슈터 : 올스탯 25, 최대 HP10%, 최대 MP10% 상승\r\n- 제로 : 받는 데미지 15%감소, 공격 대상의 방어율 10% 무시\r\n- 키네시스 : 크리티컬 데미지 4%증가" + enter;
                msg += "- 시그너스 기사단 : 상태이상 내성 증가\r\n- 미하일 : 110초간 100% 확률로 스탠스\r\n\r\n- 레지스탕스 : 부활 시 8초 동안 피해 무시\r\n- 데몬슬레이어 : 보스 몬스터 공격시 15% 데미지 추가\r\n- 데몬슬레이어 : 보스 몬스터 공격시 15% 데미지 추가\r\n- 데몬어벤져 : 데미지 10% 증가\r\n- 제논 : 올스탯(힘/덱/럭/인트) 10% 상승\r\n" + enter;
                msg += "- 패스파인더 : 설명 준비중 입니다\r\n- 호영 : 설명 준비중 입니다" + enter;
                cm.sendSimple(msg);
                break;

            case 5:
                cm.resetStats(4, 4, 4, 4);
                cm.dispose();
                break;
            case 6:
                cm.dispose();
                cm.openNpc(1530330);
                break;
            case 7:
                Packages.scripting.newscripting.ScriptManager.runScript(cm.getPlayer().getClient(), "characterNameChange", null);
                cm.dispose();
                break;
            case 8:
                cm.dispose();
                cm.openNpc(9010106);
                break;
            case 13:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 1540432, "메카닉위장색");
                break;
            case 88:
                cm.dispose();
                cm.openNpc(2020001);
                break;
            case 89:
                cm.dispose();
                cm.openNpc(9201459);
                break;
            case 98:
                cm.dispose();
                cm.openNpc(9000302);
                break;
            case 99:
                cm.dispose();
                cm.openNpc(9001110);
                break;
            case 9: // 스탯 초기화
                //cm.resetStats(4, 4, 4, 4);
                cm.getPlayer().statReset();
                cm.sendOkS("#fs11##b#e스탯 초기화가 완료되었습니다.", 2);
                cm.dispose();
                break;
            case 10:
                cm.dispose();
                cm.sendOkS("준비중", 0x24);
                break;
            case 11:
                cm.dispose();
                cm.openNpc(1540945);
                break;
            case 12:
                cm.dispose();
                cm.openNpc(1052208);
                break;
            case 13:
                cm.dispose();
                cm.openNpc(1530110);
                break;
            case 14:
                cm.dispose();
                cm.openShop(9001212);
                break;
            case 15:
                cm.dispose();
                cm.openNpc(9001205);
                break;
            case 16:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 9000213, "change_job");
                break;
            case 17:
                cm.dispose();
                cm.openNpc(1540101);
                break;
            case 18:
                cm.dispose();
                cm.openNpc(3003362);
                break;
            case 19:
                cm.dispose();
                cm.openShop(17);
                break;
            case 20:
                cm.dispose();
                cm.openNpc(1012121);
                break;
            case 21:
                cm.dispose();
                cm.warp(200000301);
                break;
            case 22:
                cm.dispose();
                cm.openNpc(3003429);
                break;
            case 23:
                cm.dispose();
                cm.openNpc(3002000);
                break;
            case 24:
                cm.dispose();
                cm.openNpc(1002005);
                break;
            case 245:
                cm.dispose();
                cm.openNpc(9000224);
                break;
            case 246:
            case 247:
                Packages.scripting.newscripting.ScriptManager.runScript(cm.getPlayer().getClient(), "levelUP", null);
                cm.dispose();
                break;
            case 248:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 9000213, 9076004);
                break;
            case 25:
                cm.dispose();
                cm.openNpc(1052014);
                break;
            case 26:
                cm.dispose();
                ScriptManager.runScript(cm.getClient(), "recommend", null)
                break;
            case 27:
                msg = "#fs11#" + 검은색 + "배우고 싶은 특별스킬을 골라주세요\r\n";
                msg += 색 + "(특별스킬은 0차스킬에 저장됩니다)\r\n\r\n";

                msg += "#L1##fc0xFFFF3636##s80001829# 비연#l\r\n";
                msg += "#L2##fc0xFFFF3636##s80001825# 일섬#l";
                cm.sendSimple(msg);
                break;
            case 555: // 후원상점
                cm.dispose();
                cm.openNpc(1530050);
                break;
            case 556: // 홍보상점
                cm.dispose();
                cm.openNpc(1530051);
                break;
            case 777: // 뽑기
                cm.dispose();
                cm.openNpc(1530056);
                break;
            case 778: // 창고
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 9010009, "택배");
                break;
            case 779: // 팬텀
                if (!GameConstants.isPhantom(cm.getPlayer().getJob())) {
                    cm.sendOk("#fs11#팬텀만 이용할 수 있습니다");
                    cm.dispose();
                    return;
                }
                msg = "#fs11#" + 검은색 + "원하시는 시스템을 선택해 주세요\r\n";
                msg += "#L1##fc0xFFFF3636##s20031207# 스틸 스킬 시스템#l\r\n";
                msg += "#L2##fc0xFFFF3636##s20031208# 스틸 스킬 초기화 시스템#l";
                cm.sendSimple(msg);
                break;
            case 780: // 은월
                if (!GameConstants.isEunWol(cm.getPlayer().getJob())) {
                    cm.sendOk("#fs11#은월만 이용할 수 있습니다");
                    cm.dispose();
                    return;
                }
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 9000213, "은월오류수정");
                break;
        }
    } else if (status == 2) {
        switch (seld) {
            case 1:
                switch (sel) {
                    case 1:
                        cm.dispose();
                        cm.openNpc(9010106);
                        break;
                    case 2:
                        cm.dispose();
                        cm.openNpc(9010107);
                        break;
                    case 3:
                        cm.dispose();
                        cm.openNpc(3003162);
                        break;
                    case 4:
                        cm.dispose();
                        cm.openNpc(3003252);
                        break;
                    case 5:
                        cm.dispose();
                        cm.openNpc(3003480);
                        break;
                    case 6:
                        cm.dispose();
                        cm.openNpc(3003756);
                        break;
                }
                break;
            case 2:
                switch (sel) {
                    case 1:
                        cm.dispose();
                        cm.openNpc(2155000);
                        break;
                    case 2:
                        cm.dispose();
                        cm.openNpc(3003104);
                        break;
                    case 3:
                        cm.dispose();
                        cm.openNpc(3003162);
                        break;
                    case 4:
                        cm.dispose();
                        cm.openNpc(3003252);
                        break;
                    case 5:
                        cm.dispose();
                        cm.openNpc(3003480);
                        break;
                    case 6:
                        cm.dispose();
                        cm.openNpc(3003756);
                        break;
                    case 9:
                        cm.dispose();
                        cm.openNpc(3003151);
                        break;
                    case 8:
                        cm.dispose();
                        cm.openNpc(3003381);
                        break;
                    case 10:
                        cm.dispose();
                        cm.warp(450004000, 0);
                        break;
                }
                break;
            case 3:
                switch (sel) {
                    case 1:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                    case 2:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                    case 3:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                    case 4:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                    case 5:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                }
                break;
            case 4:
                switch (sel) {
                    case 1:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                    case 2:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                    case 3:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                    case 4:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                    case 5:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                }
                break;
            case 5:
                switch (sel) {
                    case 1:
                        cm.dispose();
                        cm.openNpc("guild_proc");
                        break;
                    case 2:
                        cm.dispose();
                        cm.openNpc(2010009);
                        break;
                    case 3:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                    case 4:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                    case 5:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                }
                break;
            case 6:
                switch (sel) {
                    case 1:
                        cm.dispose();
                        cm.warp(680000000, 1);
                        break;
                    case 2:
                        cm.dispose();
                        cm.openNpc(1031001);
                        break;
                    case 3:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                    case 4:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                    case 5:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                }
                break;
            case 7:
                switch (sel) {
                    case 1:
                        cm.dispose();
                        cm.warp(910130000, 0);
                        break;
                    case 2:
                        cm.dispose();
                        cm.warp(910530000, 0);
                        break;
                    case 3:
                        cm.dispose();
                        cm.warp(109040001, 0);
                        break;
                    case 4:
                        cm.dispose();
                        cm.warp(100000202, 0);
                        break;
                    case 5:
                        cm.dispose();
                        cm.warp(220000006, 0);
                        break;
                }
                break;
            case 8:
                switch (sel) {
                    case 1:
                        cm.dispose();
                        cm.warp(680000000, 1);
                        break;
                    case 2:
                        cm.dispose();
                        cm.openNpc(1031001);
                        break;
                    case 3:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                    case 4:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                    case 5:
                        cm.dispose();
                        cm.openNpc(2008);
                        break;
                }
                break;
            case 27:
                switch (sel) {
                    case 1:
                        cm.sendOkS("#fs11##b#e감사합니다~!", 2);
                        cm.dispose();
                        cm.teachSkill(80001829, 5, 5);
                        break;
                    case 2:
                        cm.sendOkS("#fs11##b#e감사합니다~!", 2);
                        cm.dispose();
                        cm.teachSkill(80001825, 5, 5);
                        break;
                }
                break;
            case 779:
                switch (sel) {
                    case 1:
                        cm.dispose();
                        cm.openNpcCustom(cm.getClient(), 9000213, "SkillSteal");
                        break;
                    case 2:
                        cm.getPlayer().getStolenSkills().clear();
                        cm.getPlayer().fakeRelog();
                        break;
                }
                break;
        }
    }
}
