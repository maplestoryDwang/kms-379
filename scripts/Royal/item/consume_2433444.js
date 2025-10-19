importPackage(java.lang);
importPackage(java.io);
importPackage(Packages.packet.creators);
importPackage(Packages.objects.item);
importPackage(Packages.scripting);
importPackage(Packages.users.enchant);
importPackage(Packages.launch.world);
importPackage(Packages.client.inventory);
importPackage(Packages.database);
importPackage(java.lang);
importPackage(Packages.server);
importPackage(Packages.handling.world);
importPackage(Packages.tools.packet);
importPackage(Packages.constants);
importPackage(Packages.objects.item);
importPackage(Packages.objects.users.enchant)

보라 = "#fMap/MapHelper.img/weather/starPlanet/7#";
파랑 = "#fMap/MapHelper.img/weather/starPlanet/8#";
별파 = "#fUI/GuildMark.img/Mark/Pattern/00004001/11#"
별노 = "#fUI/GuildMark.img/Mark/Pattern/00004001/3#"
별흰 = "#fUI/GuildMark.img/Mark/Pattern/00004001/15#"
별갈 = "#fUI/GuildMark.img/Mark/Pattern/00004001/5#"
별빨 = "#fUI/GuildMark.img/Mark/Pattern/00004001/1#"
별검 = "#fUI/GuildMark.img/Mark/Pattern/00004001/16#"
별보 = "#fUI/GuildMark.img/Mark/Pattern/00004001/13#"
별 = "#fUI/FarmUI.img/objectStatus/star/whole#"
S = "#fUI/CashShop.img/CSEffect/today/0#"
보상 = "#fUI/UIWindow2.img/Quest/quest_info/summary_icon/reward#"
획득 = "#fUI/UIWindow2.img/QuestIcon/4/0#"
색 = "#fc0xFF6600CC#"
검은색 = "#fc0xFF000000#"
엔터 = "\r\n"
엔터2 = "\r\n\r\n"

var 보조무기 = "";
var 전사1 = "1004422"; // 모자
var 전사2 = "1052882"; // 옷
var 전사3 = "1152174"; // 숄더
var 전사4 = "1102775"; // 망토
var 전사5 = "1073030"; // 신발
var 전사6 = "1082636"; // 장갑

var 법사1 = "1004423";
var 법사2 = "1052887";
var 법사3 = "1152176";
var 법사4 = "1102794";
var 법사5 = "1073032";
var 법사6 = "1082637";

var 궁수1 = "1004424";
var 궁수2 = "1052888";
var 궁수3 = "1152177";
var 궁수4 = "1102795";
var 궁수5 = "1073033";
var 궁수6 = "1082638";

var 도적1 = "1004425";
var 도적2 = "1052889";
var 도적3 = "1152178";
var 도적4 = "1102796";
var 도적5 = "1073034";
var 도적6 = "1082639";

var 해적1 = "1004426";
var 해적2 = "1052890";
var 해적3 = "1152179";
var 해적4 = "1102797";
var 해적5 = "1073035";
var 해적6 = "1082640";


function start() {
    St = -1;
    action(1, 0, 0);
}

function action(M, T, S) {
    if (M != 1) {
        cm.dispose();
        return;
    }

    if (M == 1)
        St++;

    if (St == 0) {
        if (!cm.haveItem(2433444)) {
            cm.dispose();
            return;
        }
        if (cm.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() <= 9) {
            cm.sendOk("인벤토리에 충분한 공간을 비워 둔 뒤 사용해 주세요.");
            cm.dispose();
            return;
        }
        if (cm.getPlayer().getJob() == 10112) {

            cm.sayReplacedNpc("#fs11##fc0xFF000000##fc0xFFFF3300##e쓸만한 옵션#fc0xFF000000##n을 부여한 초기지원 아이템을 지급해드렸어요\r\n#fc0xFF990033#[강림월드]#fc0xFF000000#에서 즐거운 모험 되시기 바래요!\r\n\r\n\r\n#b" +
                "  #i" + 전사1 + ":# #t" + 전사1 + ":#\r\n" +
                "  #i" + 전사2 + ":# #t" + 전사2 + ":#\r\n" +
                "  #i" + 전사3 + ":# #t" + 전사3 + ":#\r\n" +
                "  #i" + 전사4 + ":# #t" + 전사4 + ":#\r\n" +
                "  #i" + 전사5 + ":# #t" + 전사5 + ":#\r\n" +
                "  #i" + 전사6 + ":# #t" + 전사6 + ":#\r\n" +
                "  #i1032148:# #t1032148:#\r\n" +
                "  #i1132161:# #t1132161:#\r\n", false, true, 1, 1052206, ScriptMessageFlag.NpcReplacedByNpc);


            addOption(전사1, false); // 모자
            addOption(전사2, false); // 옷
            addOption(전사3, false); // 숄더
            addOption(전사4, false); // 망토
            addOption(전사5, false); // 신발
            addOption(전사6, false); // 장갑
            addOption(1032148, false); // 귀걸이
            addOption(1132161, false); // 벨트
            cm.gainItem(2433444, -1);

            item = Packages.objects.item.MapleItemInformationProvider.getInstance().getEquipById(1142085);
            item.setStr(10);
            item.setDex(10);
            item.setInt(10);
            item.setLuk(10);
            item.setWatk(5);
            item.setMatk(5);
            item.setHp(item.getHp() + 3000);
            MapleInventoryManipulator.addFromDrop(cm.getClient(), item, false);
            cm.dispose();
            return;
        }
        selStr = "<지급: 초기 아이템 지급>\r\n\r\n"
        selStr += "초기 아이템을 지급 받을 경우, #b장비 인벤토리에 지급#k되며, 인벤토리에 충분한 공간이 존재하는지 확인 후에 사용하라고 되어 있다...\r\n\r\n"
        selStr += "#r※ 초기 아이템에는 특정 잠재능력 및 능력치가 적용됩니다. 초기 아이템을 지급받기 전, 장비 인벤토리에 충분한 빈 공간을 만들어 주세요."
        cm.askYesNo(selStr, GameObjectType.User, ScriptMessageFlag.BigScenario, ScriptMessageFlag.Self, ScriptMessageFlag.FlipImage, ScriptMessageFlag.NpcReplacedByUser);

    } else if (St == 1) {
        wList = [];
        getWeapon(cm.getPlayer().getJob());

        selStr = "#fs11#직업에 맞는 무기를 선택해주세요#b\r\n\r\n#r번복이 불가능하니 주의바랍니다\r\n#b";

        for (i = 0; i < wList.length; i++) {
            selStr += "#L" + wList[i] + "##i" + wList[i] + ":# #t" + wList[i] + ":##l\r\n";
        }


        cm.sendSimpleS(selStr, 4);
    } else if (St == 2) {
        if (Packages.constants.GameConstants.isWarrior(cm.getPlayer().getJob())) {

            cm.sayReplacedNpc("#fs11##fc0xFF000000##fc0xFFFF3300##e쓸만한 옵션#fc0xFF000000##n을 부여한 초기지원 아이템을 지급해드렸어요\r\n#fc0xFF990033#[강림월드]#fc0xFF000000#에서 즐거운 모험 되시기 바래요!\r\n\r\n\r\n#b" +
                "  #i" + 전사1 + ":# #t" + 전사1 + ":#\r\n" +
                "  #i" + 전사2 + ":# #t" + 전사2 + ":#\r\n" +
                "  #i" + 전사3 + ":# #t" + 전사3 + ":#\r\n" +
                "  #i" + 전사4 + ":# #t" + 전사4 + ":#\r\n" +
                "  #i" + 전사5 + ":# #t" + 전사5 + ":#\r\n" +
                "  #i" + 전사6 + ":# #t" + 전사6 + ":#\r\n" +
                "  #i1032148:# #t1032148:#\r\n" +
                "  #i1132161:# #t1132161:#\r\n", false, true, 1, 1052206, ScriptMessageFlag.NpcReplacedByNpc);

            if (wList.indexOf(S) == -1) {
                cm.sendOk("비정상적인 접근");
                cm.dispose();
                return;
            }

            addOption(전사1, false); // 모자
            addOption(전사2, false); // 옷
            addOption(전사3, false); // 숄더
            addOption(전사4, false); // 망토
            addOption(전사5, false); // 신발
            addOption(전사6, false); // 장갑
            addOption(1032148, false); // 귀걸이
            addOption(1132161, false); // 벨트
        } else if (Packages.constants.GameConstants.isMagician(cm.getPlayer().getJob())) {
            cm.sayReplacedNpc("#fs11##fc0xFF000000##fc0xFFFF3300##e쓸만한 옵션#fc0xFF000000##n을 부여한 초기지원 아이템을 지급해드렸어요\r\n#fc0xFF990033#[강림월드]#fc0xFF000000#에서 즐거운 모험 되시기 바래요!\r\n\r\n\r\n#b" +
                "  #i" + 법사1 + ":# #t" + 법사1 + ":#\r\n" +
                "  #i" + 법사2 + ":# #t" + 법사2 + ":#\r\n" +
                "  #i" + 법사3 + ":# #t" + 법사3 + ":#\r\n" +
                "  #i" + 법사4 + ":# #t" + 법사4 + ":#\r\n" +
                "  #i" + 법사5 + ":# #t" + 법사5 + ":#\r\n" +
                "  #i" + 법사6 + ":# #t" + 법사6 + ":#\r\n" +
                "  #i1032148:# #t1032148:#\r\n" +
                "  #i1132161:# #t1132161:#\r\n", false, true, 1, 1052206, ScriptMessageFlag.NpcReplacedByNpc);


            if (wList.indexOf(S) == -1) {
                cm.sendOk("비정상적인 접근");
                cm.dispose();
                return;
            }

            addOption(법사1, false); // 모자
            addOption(법사2, false); // 옷
            addOption(법사3, false); // 숄더
            addOption(법사4, false); // 망토
            addOption(법사5, false); // 신발
            addOption(법사6, false); // 장갑
            addOption(1032148, false); // 귀걸이
            addOption(1132161, false); // 벨트


        } else if (Packages.constants.GameConstants.isArcher(cm.getPlayer().getJob())) {
            cm.sayReplacedNpc("#fs11##fc0xFF000000##fc0xFFFF3300##e쓸만한 옵션#fc0xFF000000##n을 부여한 초기지원 아이템을 지급해드렸어요\r\n#fc0xFF990033#[강림월드]#fc0xFF000000#에서 즐거운 모험 되시기 바래요!\r\n\r\n\r\n#b" +
                "  #i" + 궁수1 + ":# #t" + 궁수1 + ":#\r\n" +
                "  #i" + 궁수2 + ":# #t" + 궁수2 + ":#\r\n" +
                "  #i" + 궁수3 + ":# #t" + 궁수3 + ":#\r\n" +
                "  #i" + 궁수4 + ":# #t" + 궁수4 + ":#\r\n" +
                "  #i" + 궁수5 + ":# #t" + 궁수5 + ":#\r\n" +
                "  #i" + 궁수6 + ":# #t" + 궁수6 + ":#\r\n" +
                "  #i1032148:# #t1032148:#\r\n" +
                "  #i1132161:# #t1132161:#\r\n", false, true, 1, 1052206, ScriptMessageFlag.NpcReplacedByNpc);


            if (wList.indexOf(S) == -1) {
                cm.sendOk("비정상적인 접근");
                cm.dispose();
                return;
            }

            addOption(궁수1, false); // 모자
            addOption(궁수2, false); // 옷
            addOption(궁수3, false); // 숄더
            addOption(궁수4, false); // 망토
            addOption(궁수5, false); // 신발
            addOption(궁수6, false); // 장갑
            addOption(1032148, false); // 귀걸이
            addOption(1132161, false); // 벨트
        } else if (Packages.constants.GameConstants.isThief(cm.getPlayer().getJob())) {
            cm.sayReplacedNpc("#fs11##fc0xFF000000##fc0xFFFF3300##e쓸만한 옵션#fc0xFF000000##n을 부여한 초기지원 아이템을 지급해드렸어요\r\n#fc0xFF990033#[강림월드]#fc0xFF000000#에서 즐거운 모험 되시기 바래요!\r\n\r\n\r\n#b" +
                "  #i" + 도적1 + ":# #t" + 도적1 + ":#\r\n" +
                "  #i" + 도적2 + ":# #t" + 도적2 + ":#\r\n" +
                "  #i" + 도적3 + ":# #t" + 도적3 + ":#\r\n" +
                "  #i" + 도적4 + ":# #t" + 도적4 + ":#\r\n" +
                "  #i" + 도적5 + ":# #t" + 도적5 + ":#\r\n" +
                "  #i" + 도적6 + ":# #t" + 도적6 + ":#\r\n" +
                "  #i1032148:# #t1032148:#\r\n" +
                "  #i1132161:# #t1132161:#\r\n", false, true, 1, 1052206, ScriptMessageFlag.NpcReplacedByNpc);


            if (wList.indexOf(S) == -1) {
                cm.sendOk("비정상적인 접근");
                cm.dispose();
                return;
            }

            addOption(도적1, false); // 모자
            addOption(도적2, false); // 옷
            addOption(도적3, false); // 숄더
            addOption(도적4, false); // 망토
            addOption(도적5, false); // 신발
            addOption(도적6, false); // 장갑
            addOption(1032148, false); // 귀걸이
            addOption(1132161, false); // 벨트
        } else if (Packages.constants.GameConstants.isPirate(cm.getPlayer().getJob())) {

            cm.sayReplacedNpc("#fs11##fc0xFF000000##fc0xFFFF3300##e쓸만한 옵션#fc0xFF000000##n을 부여한 초기지원 아이템을 지급해드렸어요\r\n#fc0xFF990033#[강림월드]#fc0xFF000000#에서 즐거운 모험 되시기 바래요!\r\n\r\n\r\n#b" +
                "  #i" + 해적1 + ":# #t" + 해적1 + ":#\r\n" +
                "  #i" + 해적2 + ":# #t" + 해적2 + ":#\r\n" +
                "  #i" + 해적3 + ":# #t" + 해적3 + ":#\r\n" +
                "  #i" + 해적4 + ":# #t" + 해적4 + ":#\r\n" +
                "  #i" + 해적5 + ":# #t" + 해적5 + ":#\r\n" +
                "  #i" + 해적6 + ":# #t" + 해적6 + ":#\r\n" +
                "  #i1032148:# #t1032148:#\r\n" +
                "  #i1132161:# #t1132161:#\r\n", false, true, 1, 1052206, ScriptMessageFlag.NpcReplacedByNpc);


            if (wList.indexOf(S) == -1) {
                cm.sendOk("비정상적인 접근");
                cm.dispose();
                return;
            }

            addOption(해적1, false); // 모자
            addOption(해적2, false); // 옷
            addOption(해적3, false); // 숄더
            addOption(해적4, false); // 망토
            addOption(해적5, false); // 신발
            addOption(해적6, false); // 장갑
            addOption(1032148, false); // 귀걸이
            addOption(1132161, false); // 벨트
        } else {
            if (S != 3604002) {
				cm.gainItem(2433444, -1);
                Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/복사/초기상자.log", "\r\n계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n닉네임 : " + cm.getPlayer().getName() + "\r\n복사한 아이템 : " + S + "\r\n\r\n", true);
                cm.getPlayer().ban("[초기상자] 복사한 아이템 : " + S + "으로 영구밴", true, true, true);
				cm.getPlayer().serialBan(false);
                cm.dispose();
                return;
            } else {
				cm.gainItem(2433444, -1);
                cm.dispose();
                return;
            }
        }
        addOption(S, true);
        
		cm.gainItem(2433444, -1);
        item = Packages.objects.item.MapleItemInformationProvider.getInstance().getEquipById(보조무기);
        if (보조무기 == "1342101") {
            item.setDownLevel(60);
        }
        MapleInventoryManipulator.addFromDrop(cm.getClient(), item, false);

        item = Packages.objects.item.MapleItemInformationProvider.getInstance().getEquipById(1142085);
        item.setStr(10);
        item.setDex(10);
        item.setInt(10);
        item.setLuk(10);
        item.setWatk(5);
        item.setMatk(5);
        item.setHp(item.getHp() + 3000);
        MapleInventoryManipulator.addFromDrop(cm.getClient(), item, false);
        cm.dispose();
        return;

    }
}

function isMagician(i) {
    switch (Math.floor(i / 100)) {
        case 2:
        case 12:
        case 22:
        case 27:
        case 32:
        case 142:
        case 152:
        case 162:
            return true;
            break;

        default:
            return false;
            break;
    }
}

function getWeapon(i) {
    switch (Math.floor(cm.getPlayer().getJob())) {
        /* 전사 */
        case 100:
            wList.push(1302333); // 한손검
            wList.push(1312199); // 한손도끼
            wList.push(1322250); // 한손둔기
            wList.push(1402251); // 두손검
            wList.push(1412177); // 두손도끼
            wList.push(1422184); // 두손둔기
            wList.push(1432214); // 창
            wList.push(1442268); // 폴암
            break;

        case 110: //히어로
        case 111:
        case 112:
            wList.push(1302333); // 한손검
            wList.push(1402251); // 두손검
            보조무기 = "1352203"; // 이볼빙 버츄스 메달
            break;

        case 120: //팔라딘
        case 121:
        case 122:
            wList.push(1302333); // 한손검
            wList.push(1312199); // 한손도끼
            wList.push(1322250); // 한손둔기
            wList.push(1402251); // 두손검

            wList.push(1412177); // 두손도끼
            wList.push(1422184); // 두손둔기
            보조무기 = "1352213"; // 이볼빙 세이크리드 로자리오
            break;

        case 130: //다크 나이트
        case 131:
        case 132:
            wList.push(1432214); // 창
            wList.push(1442268); // 폴암
            보조무기 = "1352223"; // 이볼빙 버서크 체인
            break;

        case 1100: //소울 마스터
        case 1110:
        case 1111:
        case 1112:
            wList.push(1402251); // 두손검
            보조무기 = "1352973"; // 이볼빙 에레브의 광휘
            break;

        case 2100:
        case 2110:
        case 2111:
        case 2112:
            wList.push(1442268); // 폴암
            보조무기 = "1352933"; // 이볼빙 천룡추
            break;

        case 3100:
        case 3110:
        case 3111:
        case 3112:
            wList.push(1312199); // 한손도끼
            wList.push(1322250); // 한손둔기
            보조무기 = "1099016"; // 이볼빙 극한의 포스실드
            break;

        case 3101:
        case 3120:
        case 3121:
        case 3122:
            wList.push(1232109); // 데스페라도
            보조무기 = "1099017"; // 이볼빙 극한의 포스실드
            break;

        case 3700: // 블래스터
        case 3710:
        case 3711:
        case 3712:
            wList.push(1582017); // 건틀렛 리볼버
            보조무기 = "1353407"; // 이볼빙 익스플로시브 필<3호>
            break;

        case 5100:
        case 5110: //미하일
        case 5111:
        case 5112:
            wList.push(1302333); // 한손검
            보조무기 = "1098009"; // 이볼빙 익스플로시브 필<3호>
            break;

        case 6100: // 카이저
        case 6110:
        case 6111:
        case 6112:
            wList.push(1402251); // 두손검
            보조무기 = "1352509"; // 이볼빙 진리의 노바의 정수
            break;

        case 15002: // 아델
        case 15100:
        case 15110:
        case 15111:
        case 15112:
            wList.push(1213017); // 튜너
            보조무기 = "1354006"; // 이볼빙 노블 브레이슬릿
            break;

            /* 마법사 */
        case 200: // 불독
        case 210:
        case 211:
        case 212:
            wList.push(1372222); // 완드
            wList.push(1382259); // 스태프
            보조무기 = "1352233"; // 이볼빙 적녹의 서 <종장>
            break;

        case 220: // 썬콜
        case 221:
        case 222:
            wList.push(1372222); // 완드
            wList.push(1382259); // 스태프
            보조무기 = "1352243"; // 이볼빙 청은의 서 <종장>
            break;

        case 230: // 비숍
        case 231:
        case 232:
            wList.push(1372222); // 완드
            wList.push(1382259); // 스태프
            보조무기 = "1352253"; // 이볼빙 백금의 서 <종장>
            break;

        case 1200: // 플레임 위자드
        case 1210:
        case 1211:
        case 1212:
            wList.push(1372222); // 완드
            wList.push(1382259); // 스태프
            보조무기 = "1352973"; // 이볼빙 에레브의 광휘
            break;

        case 2200: // 에반
        case 2210:
        case 2211:
        case 2212:
        case 2213:
        case 2214:
        case 2215:
        case 2216:
        case 2217:
        case 2218:
            wList.push(1372222); // 완드
            wList.push(1382259); // 스태프
            보조무기 = "1352943"; // 이볼빙 드래곤마스터의 유산
            break;

        case 2700:
        case 2710:
        case 2711:
        case 2712:
            wList.push(1212115); // 샤이닝로드
            보조무기 = "1352409"; // 이볼빙 카르마 오브
            break;

        case 3200:
        case 3210:
        case 3211:
        case 3212:
            wList.push(1382259); // 스태프
            보조무기 = "1352953"; // 이볼빙 맥시마이즈 볼
            break;

        case 14200:
        case 14210:
        case 14211:
        case 14212:
            wList.push(1262017); // ESP 리미터
            보조무기 = "1353208"; // 이볼빙 체스피스 디 퀸
            break;

        case 15200:
        case 15210:
        case 15211:
        case 15212:
            wList.push(1282016); // 매직 건틀렛
            보조무기 = "1353507"; // 이볼빙 글로리 매직윙
            break;

        case 16200: //라라
        case 16210:
        case 16211:
        case 16212:
            wList.push(1372222); // 완드
            보조무기 = "1354026"; // 이볼빙 사옥 노리개
            break;

            /* 궁1수 */
        case 300: // 보우마스터
        case 310:
        case 311:
        case 312:
            wList.push(1452252); // 활
            보조무기 = "1352263"; // 이볼빙 블라스트 페더
            break;

        case 1300: // 윈드브레이커
        case 1310:
        case 1311:
        case 1312:
            wList.push(1452252); // 활
            보조무기 = "1352973"; // 이볼빙 에레브의 광휘
            break;

        case 320: // 신궁
        case 321:
        case 322:
            wList.push(1462239); // 석궁
            보조무기 = "1352273"; // 이볼빙 전발적중
            break;

        case 3300: // 와일드헌터
        case 3310:
        case 3311:
        case 3312:
            wList.push(1462239); // 석궁
            보조무기 = "1352963"; // 이볼빙 와일드 팡
            break;

        case 301: // 패스파인더
        case 330:
        case 331:
        case 332:
            wList.push(1592019); // 에인션트 보우
            보조무기 = "1353705"; // 이볼빙 퍼펙트 렐릭
            break;

        case 2300: // 메르세데스
        case 2310:
        case 2311:
        case 2312:
            wList.push(1522138); // 듀얼 보우건
            보조무기 = "1352012"; // 이볼빙 무한의 마법 화살
            break;

        case 6300: // 카인
        case 6310:
        case 6311:
        case 6312:
            wList.push(1214017); // 브레스 슈터
            보조무기 = "1354016"; // 이볼빙 D100 웨폰 벨트
            break;

        case 400: // 나이트로드
        case 410:
        case 411:
        case 412:
            wList.push(1472261); // 아대
            보조무기 = "1352293"; // 이볼빙 파사부
            break;

            /* 도적 */
        case 420: // 섀도어
        case 421:
        case 422:
            wList.push(1332274); // 단검
            보조무기 = "1352283"; // 이볼빙 슬래싱 섀도우
            break;

        case 1400: // 나이트워커
        case 1410:
        case 1411:
        case 1412:
            wList.push(1472261); // 아대
            보조무기 = "1352973"; // 이볼빙 에레브의 광휘
            break;

        case 430: // 듀블
        case 431:
        case 432:
        case 433:
        case 434:
            wList.push(1332274); // 단검
            보조무기 = "1342101"; // 앱솔랩스 블레이드
            break;

        case 2400: // 팬텀
        case 2410:
        case 2411:
        case 2412:
            wList.push(1362135); // 케인
            보조무기 = "1352112"; // 이볼빙 데르니에 카르트
            break;

        case 3600: // 제논
        case 3610:
        case 3611:
        case 3612:
            wList.push(1242116); // 에너지소드
            보조무기 = "1353009"; // 이볼빙 옥타코어 컨트롤러
            break;

        case 6400: // 카데나
        case 6410:
        case 6411:
        case 6412:
            wList.push(1272016); // 체인
            보조무기 = "1353308"; // 이볼빙 트랜스미터 type:A
            break;

        case 16400: // 호영
        case 16410:
        case 16411:
        case 16412:
            wList.push(1292017); // 부채
            보조무기 = "1353806"; // 이볼빙 월장석 선추
            break;

        case 15400: // 칼리
        case 15410:
        case 15411:
        case 15412:
            wList.push(1404017); // 차크람
            보조무기 = "1354036"; // 이볼빙 인피니티 헥스시커
            break;

            /* 해적 */
        case 500: // 바이퍼
        case 510:
        case 511:
        case 512:
            wList.push(1482216); // 너클
            보조무기 = "1352903"; // 이볼빙 리스트 아머
            break;

        case 520: // 캡틴
        case 521:
        case 522:
            wList.push(1492231); // 건
            보조무기 = "1352913"; // 이볼빙 팔콘아이
            break;

        case 501: // 캐논슈터
        case 530:
        case 531:
        case 532:
            wList.push(1532144); // 핸드캐논
            보조무기 = "1352923"; // 이볼빙 봄버드 센터파이어
            break;

        case 1500: // 스트라이커
        case 1510:
        case 1511:
        case 1512:
            wList.push(1482216); // 너클
            보조무기 = "1352973"; // 이볼빙 에레브의 광휘
            break;

        case 2005: // 은월
        case 2500:
        case 2510:
        case 2511:
        case 2512:
            wList.push(1482216); // 너클
            보조무기 = "1353108"; // 이볼빙 황금빛 여우구슬
            break;

        case 15500: // 아크
        case 15510:
        case 15511:
        case 15512:
            wList.push(1482216); // 너클
            보조무기 = "1353607"; // 이볼빙 얼티밋 패스
            break;


        case 3500: // 메카닉
        case 3510:
        case 3511:
        case 3512:
            wList.push(1492231); // 건
            보조무기 = "1352710"; // 이볼빙 이터널 매그넘
            break;

        case 6500: // 엔젤릭버스터
        case 6510:
        case 6511:
        case 6512:
            wList.push(1222109); // 소울슈터
            보조무기 = "1352609"; // 이볼빙 그린 소울링
            break;


        case 10110:
            break;
        default:
            cm.getPlayer().dropMessage(5, "오류가 발생하였습니다. 코드를 운영진께 제보해 주세요. [Code : 0x" + cm.getPlayer().getJob() + "]");
            wList.push(3604002);
            break;
    }
}

function hpJobCheck(i) {
    if (Math.floor(i / 10) === 312)
        return true;
}

function addOption(i, isWeapon) {
    item = Packages.objects.item.MapleItemInformationProvider.getInstance().getEquipById(i);
    if (!isWeapon) {
        if (hpJobCheck(cm.getPlayer().getJob())) {
            item.setHp(item.getHp() + 2500);
            item.setState(20);
            item.setPotential1(40086);
            item.setPotential2(40086);
            item.setPotential3(40086);
            item.setWatk(item.getWatk() + 40);
            item.setCHUC(17);
            item.setItemState(item.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
        } else {
            item.setStr(item.getStr() + 50);
            item.setDex(item.getDex() + 50);
            item.setInt(item.getInt() + 50);
            item.setLuk(item.getLuk() + 50);

            if (isMagician(cm.getPlayer().getJob()))
                item.setMatk(item.getMatk() + 40);

            else
                item.setWatk(item.getWatk() + 40);

            item.setState(19);
            item.setPotential1(40086);
            item.setPotential2(40086);
            item.setPotential3(40086);
            item.setCHUC(17);
            item.setItemState(item.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
        }
    } else {

        if (hpJobCheck(cm.getPlayer().getJob())) {
            item.setHp(item.getHp() + 5000);
            item.setWatk(item.getWatk() + 40);
            item.setState(19);
            item.setPotential1(30051);
            item.setPotential2(30051);
            item.setPotential3(30051);
            item.setCHUC(17);
            item.setItemState(item.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());


        } else {
            item.setStr(item.getStr() + 30);
            item.setDex(item.getDex() + 30);
            item.setInt(item.getInt() + 30);
            item.setLuk(item.getLuk() + 30);
            item.setState(19);
            if (isMagician(cm.getPlayer().getJob())) {
                item.setMatk(item.getMatk() + 70);
                item.setPotential1(30052);
                item.setPotential2(30052);
                item.setPotential3(30052);
                item.setPotential4(30052);
                item.setPotential5(30052);
                item.setPotential6(30052);
                item.setCHUC(17);
                item.setItemState(item.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());


            } else {
                item.setWatk(item.getWatk() + 70);
                item.setPotential1(30051);
                item.setPotential2(30051);
                item.setPotential3(30051);
                item.setPotential4(30051);
                item.setPotential5(30051);
                item.setPotential6(30051);
                item.setCHUC(17);
                item.setItemState(item.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());
            }
        }
    }

    item.setDownLevel(60);
    item.setLevel(item.getUpgradeSlots());
    item.setExGradeOption(0);
    item.setUpgradeSlots(0);
    MapleInventoryManipulator.addFromDrop(cm.getClient(), item, false);
}