/*
이동NPC
*/

h = "#fUI/UIMiniGame.img/starPlanetRPS/heart#";
g = "#fMap/MapHelper.img/minimap/anothertrader#";
a = "#i3801317#"
b = "#i3801313#"
c = "#i3801314#"
d = "#i3801315#"
p = "#fc0xFFF781D8#"

randnumber = Packages.objects.utils.Randomizer.rand(100, 200);

포켓 = "#fUI/Basic.img/RoyalBtn/theblackcoin/23#";
검은마법사 = "#fUI/Basic.img/RoyalBtn/theblackcoin/42#";

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
핑크색 = "#fc0xFFFF3366#"
분홍색 = "#fc0xFFF781D8#"
엔터 = "\r\n"
엔터2 = "\r\n\r\n"

mTown = ["SixPath", "Henesys", "Ellinia", "Perion", "KerningCity",
    "Rith", "Dungeon", "Nautilus", "Ereb", "Rien",
    "Orbis", "ElNath", "Ludibrium", "Folkvillige", "AquaRoad",
    "Leafre", "Murueng", "WhiteHerb", "Ariant", "Magatia",
    "Edelstein", "Eurel", "critias", "Haven", "Road of Vanishing", "ChewChew", "Lacheln", "Arcana", "Morass", "esfera", "aliance", "moonBridge", "TheLabyrinthOfSuffering", "Limen"
];

cTown = [104020000, 120040000, 101000000, 102000000, 103000000,
    104000000, 105000000, 120000000, 130000000, 140000000,
    200000000, 211000000, 220000000, 224000000, 230000000,
    240000000, 250000000, 251000000, 260000000, 261000000,
    310000000, 101050000, 241000000, 310070000, 450001000, 450002000, 450003000, 450005000, 450006130, 450007040, 450009050, 450009100, 450011500, 450012300
];

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
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

        var msg = "#fs11##fUI/Basic.img/Zenia/SC/3#\r\n";
        msg += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#\r\n";

        msg += "       #L1##fUI/Basic.img/Zenia/SCBtn/300##l       ";//사냥이동
        //msg += "#L999##fUI/Basic.img/Zenia/SCBtn/301##l";//자동사냥
        msg += "#L2##fUI/Basic.img/Zenia/SCBtn/302##l\r\n";//보스이동
        msg += "       #L3##fUI/Basic.img/Zenia/SCBtn/303##l       ";//마을이동
        //msg += "#L4##fUI/Basic.img/Zenia/SCBtn/304##l";//이벤트맵
        msg += "#L5##fUI/Basic.img/Zenia/SCBtn/305##l\r\n\r\n";//기타이동

        cm.sendSimple(msg);
    } else if (status == 1) {
        ans_01 = selection;
        selStr = "";
        if (ans_01 == 999 && cm.getPlayer().getLevel() < 300) {
            cm.sendSimple("레벨 300 이상부터 이용가능합니다.");
            cm.dispose();
            return;
        }


        switch (ans_01) {
            case 1:
                selStr += "#L1# #fMap/MapHelper.img/minimap/party##r 레벨 10 ~ 레벨 200#k #fc0xFF000000#사냥터로 이동#l\r\n\r\n";
                selStr += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#k";
                selStr += "#L3# #fUI/UIWindow.img/ToolTip/WorldMap/ArcaneForce##fc0xFF0066CC# 아케인리버#fc0xFF000000# 사냥터로 이동#l\r\n";
                selStr += "#L5# #fUI/UIWindow.img/ToolTip/WorldMap/ArcaneForce##fc0xFF0066CC# 셀라스#fc0xFF000000# 사냥터로 이동#l\r\n";
                selStr += "#L6# #fUI/UIWindow.img/ToolTip/WorldMap/AuthenticForce##fc0xFF3366FF# 세르니움#fc0xFF000000# 사냥터로 이동#l\r\n";
                selStr += "#L7# #fUI/UIWindow.img/ToolTip/WorldMap/AuthenticForce##fc0xFF3366FF# 호텔 아르크스#fc0xFF000000# 사냥터로 이동#l\r\n";
                selStr += "#L8# #fUI/UIWindow.img/ToolTip/WorldMap/AuthenticForce##fc0xFF3366FF# 오디움#fc0xFF000000# 사냥터로 이동#l\r\n";
                selStr += "#L9# #fUI/UIWindow.img/ToolTip/WorldMap/AuthenticForce##fc0xFF3366FF# 도원경#fc0xFF000000# 사냥터로 이동#l\r\n";
                selStr += "#L10# #fUI/UIWindow.img/ToolTip/WorldMap/AuthenticForce##fc0xFF3366FF# 아르테리아#fc0xFF000000# 사냥터로 이동#l\r\n\r\n";
                selStr += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#k";
                selStr += "#L2# #fUI/Basic.img/BtCoin/normal/0##fc0xFF339933# 피로도 사냥터로 이동#l#fc0xFF000000#\r\n";
                cm.sendSimpleS(selStr, 4);
                break;

            case 2:
                cm.dispose();
                cm.openNpcCustom(cm.getClient(), 9000213, "보스이동");
                break;

            case 3:
                cm.dispose();
                cm.openNpc(3000012);
                break;

            case 4:
                var selStr = "#fs11#";
                selStr += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n#fc0xFFFF3300#";
                selStr += 색 + "            #L1#" + 검은마법사 + " 준비중#l#L2#" + 포켓 + 핑크색 + " [코디맵]" + 색 + " 이동하기#l\r\n\r\n";
                selStr += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n#fc0xFFFF3300#";
                cm.sendSimple(selStr);
                break;

            case 5:
                selStr += "#fs11#";
                //selStr += "#L5# #fs11# 어드벤처 드릴 #b(강함의 척도로 재화를!)#k\r\n";
                selStr += "#L2#  무릉도장 이동하기\r\n\r\n";
                selStr += "#L3#  보상점프맵\r\n";
                selStr += "#L6#  일반점프맵\r\n\r\n";
                //selStr += "#L4# 낚시터 #b(낚시를 해서 대어를 낚자!)#k\r\n";
                cm.sendSimpleS(selStr, 4);
                break;

            case 999:
                starImg = "#fUI/UIWindow.img/ToolTip/WorldMap/StarForce#";
                selStr += "#fs11##fc0xFF000000#피로도 사냥터는 #e#fc0xFFFF9436피로회복제를 통한 회복 후 #피로도#k#n를 소모하며 #b#e일반 피로도 사냥터#fc0xFF000000##n와는 달리 #b#e많은 아이템 #r[#z4031227#, #z4310266#, #z4310237#]#b 과\r\n많은 경험치를 획득#fc0xFF000000##n할 수 있네\r\n\r\n#fc0xFF000000#또한 피로도를 모두 소진하면 이용할 수 없네.\r\n#r주의 : 피로도는 자정이 지나면 0으로 초기화됩니다.\r\n\r\n";
                    selStr += "#b#L" + 150 + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.300 #fc0xFFFF9436#피로도 사냥터 #b(A - 1)#l\r\n";
                    selStr += "#b#L" + 151 + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.300 #fc0xFFFF9436#피로도 사냥터 #b(A - 2)#l\r\n";
                    selStr += "#b#L" + 152 + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.300 #fc0xFFFF9436#피로도 사냥터 #b(A - 3)#l\r\n\r\n";
                    selStr += "#b#L" + 153 + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.300 #fc0xFFFF9436#피로도 사냥터 #b(B - 1)#l\r\n";
                    selStr += "#b#L" + 154 + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.300 #fc0xFFFF9436#피로도 사냥터 #b(B - 2)#l\r\n";
                    selStr += "#b#L" + 155 + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.300 #fc0xFFFF9436#피로도 사냥터 #b(B - 3)#l\r\n\r\n";
                    selStr += "#b#L" + 156 + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.300 #fc0xFFFF9436#피로도 사냥터 #b(C - 1)#l\r\n";
                    selStr += "#b#L" + 157 + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.300 #fc0xFFFF9436#피로도 사냥터 #b(C - 2)#l\r\n";
                    selStr += "#b#L" + 158 + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.300 #fc0xFFFF9436#피로도 사냥터 #b(C - 3)#l\r\n";
                cm.sendSimpleS(selStr, 4);
                break;

        }

    } else if (status == 2) {
        ans_02 = selection;
        if (ans_01 == 5) {
            switch (ans_02) {
                case 6:
                    cm.dispose();
                    cm.openNpcCustom(cm.getClient(), 9062294, "점프맵이동");
                    return;
                    break;
                case 2:
                    cm.dispose();
                    cm.getPlayer().send(Packages.network.models.CField.UIPacket.closeUI(62));
                    cm.warp(925020000, 0);
                    return;
                    break;
                case 3:
                    cm.dispose();
                    cm.openNpc(9000293);
                    return;
                    break;
                case 5:
                    cm.dispose();
                    cm.openNpc(9000219);
                    return;
                    break;
                case 4:
                    cm.dispose();
                    cm.warp(993174800, 0);
                    return;
                    break;
            }
        } else if (ans_01 == 1) {
            selStr = "";
            switch (ans_02) {
                case 1:
                    selStr += "#fs11#사냥터에 등장하는 #b몬스터의 평균 레벨#n#k을 확인하고 이동하게나.#fs11#\r\n";
                    selStr += "#b#L" + (931000500 + randnumber) + "##b#fMap/MapHelper.img/minimap/anothertrader# 재규어 서식지 (와일드 헌터 전용)#l\r\n\r\n";
                    selStr += "#b#L" + (100010000 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.10  #d헤네시스 - 헤네시스 북쪽언덕#l\r\n";
                    selStr += "#b#L" + (103050340 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.30  #d빅토리아로드 - 수련장2#l\r\n";
                    selStr += "#b#L" + (101030500 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.30  #d북쪽 숲 - 숲이 끝나는 곳#l\r\n";
                    selStr += "#b#L" + (103020320 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.50  #d커닝시티 지하철- 1호선 3구간#l\r\n";
                    selStr += "#b#L" + (102030000 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.55  #d불타버린 땅 - 와일드보어의 땅#l\r\n";
                    selStr += "#b#L" + (102040600 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.60  #d유적 발굴지 - 미접근 지역#l\r\n";
                    selStr += "#b#L" + (105010000 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.70  #d슬리피우드 - 조용한 습지#l\r\n";
                    selStr += "#b#L" + (230040000 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.100  #d아쿠아로드 - 깊은 바다 협곡1#l\r\n";
                    selStr += "#b#L" + (220020600 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.120  #d루디브리엄성 - 장난감공장<기계실>#l\r\n";
                    selStr += "#b#L" + (250020200 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.130  #d무릉사원 - 중급수련장#l\r\n";
                    selStr += "#b#L" + (251010402 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.150  #d무릉도원 - 빨간코 해적단 소굴2#l\r\n";
                    selStr += "#b#L" + (221030800 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.180  #dUFO 내부 - 조종간1#l\r\n";
                    selStr += "#b#L" + (273040300 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.195  #d황혼의 페리온 - 버려진 발굴지역4#l\r\n";
                    selStr += "#b#L" + (241000216 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.200  #d킹덤로드 - 타락한 마력의 숲1#l\r\n";
                    selStr += "#b#L" + (241000206 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.200  #d킹덤로드 - 타락한 마력의 숲2#l\r\n";
                    selStr += "#b#L" + (310070140 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.205  #d기계무덤 - 기계무덤 언덕4#l\r\n";
                    selStr += "#b#L" + (241000226 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.210  #d킹덤로드 - 타락한 마력의 숲3#l\r\n";
                    selStr += "#b#L" + (310070210 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.220  #d스카이라인 - 스카이라인1#l\r\n";
                    break;

                case 2:
                    starImg = "#fUI/UIWindow.img/ToolTip/WorldMap/StarForce#";
                    selStr += "#fs11##fc0xFF000000#피로도 사냥터는 #e#fc0xFFFF9436피로회복제를 통한 회복 후 #피로도#k#n를 소모하며 #b#e일반적인 사냥터#fc0xFF000000##n와는 달리 #b#e귀한 아이템 #r[#z4031227#, #z4310266#, #z4001715#]#b 을 획득할 수 있다네,\r\n경험치 또한 일반 사냥터보다 높지!\r\n\r\n#fc0xFF000000#또한 피로도를 모두 소진하면 이용할 수 없네.\r\n#r주의 : 피로도는 자정이 지나면 0으로 초기화됩니다.\r\n\r\n";
                    selStr += "#b#L" + (261020700 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.220 #fc0xFFFF9436#피로도 사냥터 1 #b#l\r\n";
                    selStr += "#b#L" + (261010103 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.260 #fc0xFFFF9436#피로도 사냥터 2 #b#l\r\n";
                    //selStr += "#b#L" + (261010104 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.260 #fc0xFFFF9436#피로도 사냥터 #b(중급 - 2)#l\r\n";
                    //selStr += "#b#L" + (261010105 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.260 #fc0xFFFF9436#피로도 사냥터 #b(중급 - 3)#l\r\n";
                    break;

                case 3:
                    starImg = "#fUI/UIWindow.img/ToolTip/WorldMap/ArcaneForce#";
                    if (cm.getPlayer().getLevel() < 200) {
                        selStr += "#fs11##fc0xFF000000#해당 사냥터는 #b200레벨#fc0xFF000000#이상부터 입장 가능하다네";
                        cm.sendNext(selStr);
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getLevel() >= 200) {
                        selStr += "#fs11#사냥터의 #e#fc0xFF6799FF#아케인포스#k#n와 #b몬스터의 평균 레벨#k#n 확인하고\r\n이동하게나.\r\n";
                        selStr += "\r\n#e#r[소멸의 여로]#d#n\r\n";
                        selStr += "#b#L" + (450001010 + randnumber) + "##fc0xFF6799FF#Lv.202#d | " + starImg + " 30 | 풍화된 기쁨의 땅#l\r\n";
                        selStr += "#b#L" + (450001012 + randnumber) + "##fc0xFF6799FF#Lv.202#d | " + starImg + " 30 | 풍화된 분노의 땅#l\r\n";
                        selStr += "#b#L" + (450001014 + randnumber) + "##fc0xFF6799FF#Lv.202#d | " + starImg + " 30 | 풍화된 슬픔의 땅#l\r\n";
                        selStr += "#b#L" + (450001214 + randnumber) + "##fc0xFF6799FF#Lv.207#d | " + starImg + " 60 | 동굴의 동쪽길2#l\r\n";
                        selStr += "#b#L" + (450001216 + randnumber) + "##fc0xFF6799FF#Lv.207#d | " + starImg + " 60 | 동굴 아래쪽#l\r\n";
                        selStr += "#b#L" + (450001230 + randnumber) + "##fc0xFF6799FF#Lv.207#d | " + starImg + " 60 | 아르마의 은신처#l\r\n";
                        selStr += "#b#L" + (450001260 + randnumber) + "##fc0xFF6799FF#Lv.209#d | " + starImg + " 80 | 숨겨진 호숫가#l\r\n";
                        selStr += "#b#L" + (450001262 + randnumber) + "##fc0xFF6799FF#Lv.209#d | " + starImg + " 80 | 숨겨진 동굴#l\r\n";
                        selStr += "\r\n\r\n#e#r[리버스 시티]#d#n\r\n";
                        selStr += "#b#L" + (450014140 + randnumber) + "##fc0xFF6799FF#Lv.207#d | " + starImg + " 60 | 지하열차1#l\r\n";
                        selStr += "#b#L" + (450014310 + randnumber) + "##fc0xFF6799FF#Lv.209#d | " + starImg + " 100 | 숨겨진 지하열차#l\r\n";
                        selStr += "#b#L" + (450014320 + randnumber) + "##fc0xFF6799FF#Lv.209#d | " + starImg + " 100 | 숨겨진 M타워#l\r\n";
                        selStr += "\r\n\r\n#e#r[츄츄&얌얌 아일랜드]#d#n\r\n";
                        selStr += "#b#L" + (450002001 + randnumber) + "##fc0xFF6799FF#Lv.210#d | " + starImg + " 100 | 오색동산#l\r\n";
                        selStr += "#b#L" + (450002006 + randnumber) + "##fc0xFF6799FF#Lv.212#d | " + starImg + " 100 | 츄릅 포레스트#l\r\n";
                        selStr += "#b#L" + (450002010 + randnumber) + "##fc0xFF6799FF#Lv.212#d | " + starImg + " 100 | 츄릅 포레스트 깊은 곳 #l\r\n";
                        selStr += "#b#L" + (450002012 + randnumber) + "##fc0xFF6799FF#Lv.214#d | " + starImg + " 130 | 격류지대#l\r\n";
                        selStr += "#b#L" + (450002016 + randnumber) + "##fc0xFF6799FF#Lv.217#d | " + starImg + " 160 | 하늘 고래산#l\r\n";
                        selStr += "#b#L" + (450015290 + randnumber) + "##fc0xFF6799FF#Lv.225#d | " + starImg + " 190 | 얌얌 아일랜드 - 숨겨진 일리야드 들판#l\r\n";
                        selStr += "\r\n\r\n#e#r[꿈의 도시 레헬른]#d#n\r\n";
                        selStr += "#b#L" + (450003200 + randnumber) + "##fc0xFF6799FF#Lv.220#d | " + starImg + " 190 | 레헬른 뒷골목 무법자의거리#l\r\n";
                        selStr += "#b#L" + (450003300 + randnumber) + "##fc0xFF6799FF#Lv.221#d | " + starImg + " 210 | 레헬른 야시장#l\r\n";
                        selStr += "#b#L" + (450003400 + randnumber) + "##fc0xFF6799FF#Lv.223#d | " + starImg + " 210 | 레헬른 무도회장#l\r\n";
                        selStr += "#b#L" + (450003440 + randnumber) + "##fc0xFF6799FF#Lv.225#d | " + starImg + " 210 | 레헬른 춤추는 구두점령지#l\r\n";
                        selStr += "\r\n\r\n#e#r[신비의 숲 아르카나]#d#n\r\n";
                        selStr += "#b#L" + (450005131 + randnumber) + "##fc0xFF6799FF#Lv.230#d | " + starImg + " 280 | 햇살과 흙의 숲#l\r\n";
                        selStr += "#b#L" + (450005221 + randnumber) + "##fc0xFF6799FF#Lv.233#d | " + starImg + " 320 | 서리와 번개의 숲 1#l\r\n";
                        selStr += "#b#L" + (450005230 + randnumber) + "##fc0xFF6799FF#Lv.235#d | " + starImg + " 320 | 맹독의 숲#l\r\n";
                        selStr += "#b#L" + (450005500 + randnumber) + "##fc0xFF6799FF#Lv.237#d | " + starImg + " 360 | 다섯 갈래 동굴#l\r\n ";
                        selStr += "\r\n\r\n#e#r[모라스]#d#n\r\n";
                        selStr += "#b#L" + (450006010 + randnumber) + "##fc0xFF6799FF#Lv.236#d | " + starImg + " 400 | 산호숲으로 가는길#l\r\n";
                        selStr += "#b#L" + (450006140 + randnumber) + "##fc0xFF6799FF#Lv.238#d | " + starImg + " 440 | 형님들 구역#l\r\n";
                        selStr += "#b#L" + (450006210 + randnumber) + "##fc0xFF6799FF#Lv.239#d | " + starImg + " 480 | 그림자가 춤추는 곳#l\r\n";
                        selStr += "#b#L" + (450006300 + randnumber) + "##fc0xFF6799FF#Lv.241#d | " + starImg + " 480 | 폐쇄구역#l\r\n ";
                        selStr += "#b#L" + (450006410 + randnumber) + "##fc0xFF6799FF#Lv.245#d | " + starImg + " 480 | 그날의 트뤼에페#l\r\n ";
                        selStr += "\r\n\r\n#e#r[에스페라]#d#n\r\n";
                        selStr += "#b#L" + (450007010 + randnumber) + "##fc0xFF6799FF#Lv.240#d | " + starImg + " 560 | 생명이 시작되는 곳 2#l\r\n";
                        selStr += "#b#L" + (450007050 + randnumber) + "##fc0xFF6799FF#Lv.242#d | " + starImg + " 560 | 생명이 시작되는 곳 5#l\r\n";
                        selStr += "#b#L" + (450007110 + randnumber) + "##fc0xFF6799FF#Lv.244#d | " + starImg + " 600 | 거울빛에 물든 바다 2#l\r\n";
                        selStr += "#b#L" + (450007120 + randnumber) + "##fc0xFF6799FF#Lv.244#d | " + starImg + " 600 | 거울빛에 물든 바다 3#l\r\n";
                        selStr += "#b#L" + (450007210 + randnumber) + "##fc0xFF6799FF#Lv.248#d | " + starImg + " 640 | 거울에 비친 빛의 신전#l\r\n";
                        selStr += "\r\n\r\n#e#r[문브릿지]#d#n\r\n";
                        selStr += "#b#L" + (450009110 + randnumber) + "##fc0xFF6799FF#Lv.250#d | " + starImg + " 670 | 사상의 경계#l\r\n";
                        selStr += "#b#L" + (450009210 + randnumber) + "##fc0xFF6799FF#Lv.252#d | " + starImg + " 700 | 미지의 안개#l\r\n";
                        selStr += "#b#L" + (450009310 + randnumber) + "##fc0xFF6799FF#Lv.254#d | " + starImg + " 730 | 공허의 파도#l\r\n";
                        selStr += "\r\n\r\n#e#r[테네브리스]#d#n\r\n";
                        selStr += "#b#L" + (450011420 + randnumber) + "##fc0xFF6799FF#Lv.255#d | " + starImg + " 760 | 고통의 미궁 내부1#l\r\n";
                        selStr += "#b#L" + (450011510 + randnumber) + "##fc0xFF6799FF#Lv.257#d | " + starImg + " 790 | 고통의 미궁 중심부#l\r\n";
                        selStr += "#b#L" + (450011600 + randnumber) + "##fc0xFF6799FF#Lv.259#d | " + starImg + " 820 | 고통의 미궁 최심부#l\r\n";
                        selStr += "\r\n\r\n#e#r[리멘]#d#n\r\n";
                        selStr += "#b#L" + (450012030 + randnumber) + "##fc0xFF6799FF#Lv.260#d | " + starImg + " 850 | 세계의 눈물 하단#l\r\n";
                        selStr += "#b#L" + (450012100 + randnumber) + "##fc0xFF6799FF#Lv.261#d | " + starImg + " 850 | 세계의 눈물 중단#l\r\n";
                        selStr += "#b#L" + (450012310 + randnumber) + "##fc0xFF6799FF#Lv.262#d | " + starImg + " 880 | 세계가 끝나는 곳 1-2#l\r\n";
                        //selStr += "#b#L" + (450012410 + randnumber) + "##fc0xFF6799FF#Lv.262#d "+starImg+" 880 세계가 끝나는 곳 2-2#l\r\n";
                        //selStr += "#b#L" + (993072000 + randnumber) + "##fc0xFF6799FF#Lv.263#d "+starImg+" 880 레지스탕스 함선 갑판#l\r\n";
                    }

                    break;

                case 4:
                    starImg = "#e#fUI/UIWindow.img/ToolTip/WorldMap/ArcaneForce#";
                    selStr += "#fs11##fc0xFF6799FF#레벨#k#n에 맞는 #b사냥터#k#n 목록만 보인다네\r\n어디로 이동 하겠나?\r\n";
                    selStr += "#fs11##b아래 사냥터에선 승급코인이 드롭된다네#k\r\n\r\n";
                    if (cm.getPlayer().getLevel() >= 300) {
                        selStr += "#b#L" + (993163100 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.300  #d히든 스트리트 - 비극의 성벽 3#l\r\n";
                    }
                    if (cm.getPlayer().getLevel() >= 340) {
                        selStr += "#b#L" + (993162600 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.340  #d히든 스트리트 - 성벽 근처 민가 2#l\r\n";
                    }
                    if (cm.getPlayer().getLevel() >= 400) {
                        selStr += "#b#L" + (940204490 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.400  #d모라스 - 해방된 폐쇄구역1#l\r\n";
                    }
                    if (cm.getPlayer().getLevel() >= 440) {
                        selStr += "#b#L" + (940204510 + randnumber) + "##fMap/MapHelper.img/minimap/anothertrader# #fc0xFF6799FF#Lv.440  #d모라스 - 해방된 폐쇄구역2#l\r\n";
                    }
                    break;

                case 5:
                    starImg = "#fUI/UIWindow.img/ToolTip/WorldMap/ArcaneForce#";
                    if (cm.getPlayer().getLevel() < 230) {
                        selStr += "#fs11##fc0xFF000000#해당 사냥터는 #b230레벨#fc0xFF000000#이상부터 입장 가능하다네";
                        cm.sendNext(selStr);
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getLevel() >= 230) {
                        selStr += "#fs11#사냥터의 #e#fc0xFF6799FF#아케인포스#k#n와 #b몬스터의 평균 레벨#k#n 확인하고\r\n이동하시기 바랍니다.\r\n";
                        selStr += "\r\n#e#r[셀라스 아케인포스 600]#d#n\r\n";
                        selStr += "#b#L" + (450016010 + randnumber) + "##fc0xFF6799FF#Lv.245#d | " + starImg + " 600  | 빛이 마지막으로 닿는 곳 1#l\r\n";
                        selStr += "#b#L" + (450016060 + randnumber) + "##fc0xFF6799FF#Lv.245#d | " + starImg + " 600  | 빛이 마지막으로 닿는 곳 6#l\r\n";
                        selStr += "\r\n#e#r[셀라스 아케인포스 640]#d#n\r\n";
                        selStr += "#b#L" + (450016130 + randnumber) + "##fc0xFF6799FF#Lv.250#d | " + starImg + " 640  | 끝없이 추락하는 심해3#l\r\n";
                        selStr += "#b#L" + (450016140 + randnumber) + "##fc0xFF6799FF#Lv.250#d | " + starImg + " 640  | 끝없이 추락하는 심해4#l\r\n";
                        selStr += "#b#L" + (450016150 + randnumber) + "##fc0xFF6799FF#Lv.250#d | " + starImg + " 640  | 끝없이 추락하는 심해5#l\r\n";
                        selStr += "#b#L" + (450016160 + randnumber) + "##fc0xFF6799FF#Lv.250#d | " + starImg + " 640  | 끝없이 추락하는 심해6#l\r\n";
                        selStr += "\r\n\r\n#e#r[셀라스 아케인포스 670]#d#n\r\n";
                        selStr += "#b#L" + (450016210 + randnumber) + "##fc0xFF6799FF#Lv.250#d | " + starImg + " 670  | 별이 삼켜진 심해1#l\r\n";
                        selStr += "#b#L" + (450016220 + randnumber) + "##fc0xFF6799FF#Lv.250#d | " + starImg + " 670  | 별이 삼켜진 심해2#l\r\n";
                        selStr += "#b#L" + (450016230 + randnumber) + "##fc0xFF6799FF#Lv.250#d | " + starImg + " 670  | 별이 삼켜진 심해3#l\r\n";
                        selStr += "#b#L" + (450016240 + randnumber) + "##fc0xFF6799FF#Lv.250#d | " + starImg + " 670  | 별이 삼켜진 심해4#l\r\n";
                        selStr += "#b#L" + (450016250 + randnumber) + "##fc0xFF6799FF#Lv.250#d | " + starImg + " 670  | 별이 삼켜진 심해5#l\r\n";
                        selStr += "#b#L" + (450016260 + randnumber) + "##fc0xFF6799FF#Lv.250#d | " + starImg + " 670  | 별이 삼켜진 심해6#l\r\n";
                    }
                    break;

                case 6:
                    starImg = "#fUI/UIWindow.img/ToolTip/WorldMap/AuthenticForce#";
                    if (cm.getPlayer().getLevel() < 260) {
                        selStr += "#fs11##fc0xFF000000#해당 사냥터는 #b260레벨#fc0xFF000000#이상부터 입장 가능하다네";
                        cm.sendNext(selStr);
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getLevel() >= 260) {
                        selStr += "#fs11#사냥터의 #e#fc0xFF6799FF#어센틱포스#k#n와 #b몬스터의 평균 레벨#k#n 확인하고\r\n이동하게나.\r\n";
                        selStr += "\r\n#e#r[세르니움 어센틱포스 50]#d#n\r\n";
                        selStr += "#b#L" + (410000520 + randnumber) + "##fc0xFF6799FF#Lv.260#d | " + starImg + " 50  | 해변 암석 지대 1#l\r\n";
                        selStr += "#b#L" + (410000530 + randnumber) + "##fc0xFF6799FF#Lv.260#d | " + starImg + " 50  | 해변 암석 지대 2#l\r\n";
                        selStr += "#b#L" + (410000540 + randnumber) + "##fc0xFF6799FF#Lv.260#d | " + starImg + " 50  | 해변 암석 지대 3#l\r\n";
                        selStr += "#b#L" + (410000550 + randnumber) + "##fc0xFF6799FF#Lv.260#d | " + starImg + " 50  | 해변 암석 지대 4#l\r\n";
                        selStr += "\r\n\r\n#e#r[세르니움 어센틱포스 50]#d#n\r\n";
                        selStr += "#b#L" + (410000590 + randnumber) + "##fc0xFF6799FF#Lv.261#d | " + starImg + " 50  | 서쪽성벽1#l\r\n";
                        selStr += "#b#L" + (410000600 + randnumber) + "##fc0xFF6799FF#Lv.261#d | " + starImg + " 50  | 서쪽성벽2#l\r\n";
                        selStr += "#b#L" + (410000610 + randnumber) + "##fc0xFF6799FF#Lv.261#d | " + starImg + " 50  | 서쪽성벽3#l\r\n";
                        selStr += "\r\n\r\n#e#r[세르니움 어센틱포스 50]#d#n\r\n";
                        selStr += "#b#L" + (410000640 + randnumber) + "##fc0xFF6799FF#Lv.262#d | " + starImg + " 50  | 동쪽성벽1#l\r\n";
                        selStr += "#b#L" + (410000650 + randnumber) + "##fc0xFF6799FF#Lv.262#d | " + starImg + " 50  | 동쪽성벽2#l\r\n";
                        selStr += "#b#L" + (410000660 + randnumber) + "##fc0xFF6799FF#Lv.262#d | " + starImg + " 50  | 동쪽성벽3#l\r\n";
                        selStr += "\r\n\r\n#e#r[세르니움 어센틱포스 50]#d#n\r\n";
                        selStr += "#b#L" + (410000700 + randnumber) + "##fc0xFF6799FF#Lv.263#d | " + starImg + " 50  | 왕립 도서관 제1구역#l\r\n";
                        selStr += "#b#L" + (410000710 + randnumber) + "##fc0xFF6799FF#Lv.263#d | " + starImg + " 50  | 왕립 도서관 제2구역#l\r\n";
                        selStr += "#b#L" + (410000720 + randnumber) + "##fc0xFF6799FF#Lv.263#d | " + starImg + " 50  | 왕립 도서관 제3구역#l\r\n";
                        selStr += "#b#L" + (410000730 + randnumber) + "##fc0xFF6799FF#Lv.263#d | " + starImg + " 50  | 왕립 도서관 제4구역#l\r\n";
                        selStr += "#b#L" + (410000740 + randnumber) + "##fc0xFF6799FF#Lv.263#d | " + starImg + " 50  | 왕립 도서관 제5구역#l\r\n";
                        selStr += "#b#L" + (410000750 + randnumber) + "##fc0xFF6799FF#Lv.263#d | " + starImg + " 50  | 왕립 도서관 제6구역#l\r\n";
                        selStr += "\r\n\r\n#e#r[불타는세르니움 어센틱포스 70]#d#n\r\n";
                        selStr += "#b#L" + (410000920 + randnumber) + "##fc0xFF6799FF#Lv.265#d | " + starImg + " 70  | 격전의 서쪽 성벽1#l\r\n";
                        selStr += "#b#L" + (410000930 + randnumber) + "##fc0xFF6799FF#Lv.265#d | " + starImg + " 70  | 격전의 서쪽 성벽2#l\r\n";
                        selStr += "#b#L" + (410000940 + randnumber) + "##fc0xFF6799FF#Lv.265#d | " + starImg + " 70  | 격전의 서쪽 성벽3#l\r\n";
                        selStr += "#b#L" + (410000950 + randnumber) + "##fc0xFF6799FF#Lv.265#d | " + starImg + " 70  | 격전의 서쪽 성벽4#l\r\n";
                        selStr += "\r\n\r\n#e#r[불타는세르니움 어센틱포스 100]#d#n\r\n";
                        selStr += "#b#L" + (410000980 + randnumber) + "##fc0xFF6799FF#Lv.266#d | " + starImg + " 100  | 격전의 동쪽 성벽1#l\r\n";
                        selStr += "#b#L" + (410000990 + randnumber) + "##fc0xFF6799FF#Lv.266#d | " + starImg + " 100  | 격전의 동쪽 성벽2#l\r\n";
                        selStr += "#b#L" + (410001000 + randnumber) + "##fc0xFF6799FF#Lv.266#d | " + starImg + " 100  | 격전의 동쪽 성벽3#l\r\n";
                        selStr += "#b#L" + (410001010 + randnumber) + "##fc0xFF6799FF#Lv.266#d | " + starImg + " 100  | 격전의 동쪽 성벽4#l\r\n";
                        selStr += "#b#L" + (410001020 + randnumber) + "##fc0xFF6799FF#Lv.266#d | " + starImg + " 100  | 격전의 동쪽 성벽5#l\r\n";
                        selStr += "#b#L" + (410001030 + randnumber) + "##fc0xFF6799FF#Lv.266#d | " + starImg + " 100  | 격전의 동쪽 성벽6#l\r\n";
                        selStr += "\r\n\r\n#e#r[불타는세르니움 어센틱포스 100]#d#n\r\n";
                        selStr += "#b#L" + (410000840 + randnumber) + "##fc0xFF6799FF#Lv.268#d | " + starImg + " 100  | 불타는 왕립 도서관 제1구역#l\r\n";
                        selStr += "#b#L" + (410000850 + randnumber) + "##fc0xFF6799FF#Lv.268#d | " + starImg + " 100  | 불타는 왕립 도서관 제2구역#l\r\n";
                        selStr += "#b#L" + (410000860 + randnumber) + "##fc0xFF6799FF#Lv.268#d | " + starImg + " 100  | 불타는 왕립 도서관 제3구역#l\r\n";
                        selStr += "#b#L" + (410000870 + randnumber) + "##fc0xFF6799FF#Lv.268#d | " + starImg + " 100  | 불타는 왕립 도서관 제4구역#l\r\n";
                        selStr += "#b#L" + (410000880 + randnumber) + "##fc0xFF6799FF#Lv.268#d | " + starImg + " 100  | 불타는 왕립 도서관 제5구역#l\r\n";
                        selStr += "#b#L" + (410000890 + randnumber) + "##fc0xFF6799FF#Lv.268#d | " + starImg + " 100  | 불타는 왕립 도서관 제6구역#l\r\n";
                    }
                    break;

                case 7:
                    starImg = "#fUI/UIWindow.img/ToolTip/WorldMap/AuthenticForce#";
                    if (cm.getPlayer().getLevel() < 260) {
                        selStr += "#fs11##fc0xFF000000#해당 사냥터는 #b260레벨#fc0xFF000000#이상부터 입장 가능하다네";
                        cm.sendNext(selStr);
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getLevel() >= 260) {
                        selStr += "#fs11#사냥터의 #e#fc0xFF6799FF#어센틱포스#k#n와 #e#b몬스터의 평균 레벨#k#n 확인하고\r\n이동하게나.\r\n";
                        selStr += "\r\n#e#r[아르크스 어센틱포스 130]#d#n\r\n";
                        selStr += "#L" + (410003040 + randnumber) + "##fc0xFF6799FF#Lv.270#d | " + starImg + " 130  | 무법자들이 지배하는 황야 1#l\r\n";
                        selStr += "#L" + (410003050 + randnumber) + "##fc0xFF6799FF#Lv.270#d | " + starImg + " 130  | 무법자들이 지배하는 황야 2#l\r\n";
                        selStr += "#L" + (410003060 + randnumber) + "##fc0xFF6799FF#Lv.270#d | " + starImg + " 130  | 무법자들이 지배하는 황야 3#l\r\n";
                        selStr += "#L" + (410003070 + randnumber) + "##fc0xFF6799FF#Lv.270#d | " + starImg + " 130  | 무법자들이 지배하는 황야 4#l\r\n";
                        selStr += "\r\n#e#r[아르크스 어센틱포스 160]#d#n\r\n";
                        selStr += "#L" + (410003090 + randnumber) + "##fc0xFF6799FF#Lv.271#d | " + starImg + " 160  | 낭만이 저무는 자동차 극장 1#l\r\n";
                        selStr += "#L" + (410003100 + randnumber) + "##fc0xFF6799FF#Lv.271#d | " + starImg + " 160  | 낭만이 저무는 자동차 극장 2#l\r\n";
                        selStr += "#L" + (410003110 + randnumber) + "##fc0xFF6799FF#Lv.271#d | " + starImg + " 160  | 낭만이 저무는 자동차 극장 3#l\r\n";
                        selStr += "#L" + (410003120 + randnumber) + "##fc0xFF6799FF#Lv.271#d | " + starImg + " 160  | 낭만이 저무는 자동차 극장 4#l\r\n";
                        selStr += "#L" + (410003130 + randnumber) + "##fc0xFF6799FF#Lv.271#d | " + starImg + " 160  | 낭만이 저무는 자동차 극장 5#l\r\n";
                        selStr += "#L" + (410003140 + randnumber) + "##fc0xFF6799FF#Lv.271#d | " + starImg + " 160  | 낭만이 저무는 자동차 극장 6#l\r\n";
                        selStr += "\r\n#e#r[아르크스 어센틱포스 200]#d#n\r\n";
                        selStr += "#L" + (410003150 + randnumber) + "##fc0xFF6799FF#Lv.273#d | " + starImg + " 200  | 종착지 없는 횡단 열차1#l\r\n";
                        selStr += "#L" + (410003160 + randnumber) + "##fc0xFF6799FF#Lv.273#d | " + starImg + " 200  | 종착지 없는 횡단 열차2#l\r\n";
                        selStr += "#L" + (410003170 + randnumber) + "##fc0xFF6799FF#Lv.273#d | " + starImg + " 200  | 종착지 없는 횡단 열차3#l\r\n";
                        selStr += "#L" + (410003180 + randnumber) + "##fc0xFF6799FF#Lv.273#d | " + starImg + " 200  | 종착지 없는 횡단 열차4#l\r\n";
                        selStr += "#L" + (410003190 + randnumber) + "##fc0xFF6799FF#Lv.273#d | " + starImg + " 200  | 종착지 없는 횡단 열차5#l\r\n";
                        selStr += "#L" + (410003200 + randnumber) + "##fc0xFF6799FF#Lv.273#d | " + starImg + " 200  | 종착지 없는 횡단 열차6#l\r\n";
                    }
                    break;

                case 8:
                    starImg = "#fUI/UIWindow.img/ToolTip/WorldMap/AuthenticForce#";
                    if (cm.getPlayer().getLevel() < 270) {
                        selStr += "#fs11##fc0xFF000000#해당 사냥터는 #b270레벨#fc0xFF000000#이상부터 입장 가능하다네";
                        cm.sendNext(selStr);
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getLevel() >= 270) {
                        selStr += "#fs11#사냥터의 #e#fc0xFF6799FF#어센틱포스#k#n와 #e#b몬스터의 평균 레벨#k#n 확인하고\r\n이동하게나.\r\n";
                        selStr += "\r\n#e#r[오디움 어센틱포스 230]#d#n\r\n";
                        selStr += "#L" + (410007002 + randnumber) + "##fc0xFF6799FF#Lv.275#d | " + starImg + " 230  | 성문으로 가는 길 1#l\r\n";
                        selStr += "#L" + (410007003 + randnumber) + "##fc0xFF6799FF#Lv.275#d | " + starImg + " 230  | 성문으로 가는 길 2#l\r\n";
                        selStr += "#L" + (410007004 + randnumber) + "##fc0xFF6799FF#Lv.275#d | " + starImg + " 230  | 성문으로 가는 길 3#l\r\n";
                        selStr += "#L" + (410007005 + randnumber) + "##fc0xFF6799FF#Lv.275#d | " + starImg + " 230  | 성문으로 가는 길 4#l\r\n";
                        selStr += "\r\n#e#r[오디움 어센틱포스 260]#d#n\r\n";
                        selStr += "#L" + (410007006 + randnumber) + "##fc0xFF6799FF#Lv.276#d | " + starImg + " 260  | 점령당한 골목 1#l\r\n";
                        selStr += "#L" + (410007007 + randnumber) + "##fc0xFF6799FF#Lv.276#d | " + starImg + " 260  | 점령당한 골목 2#l\r\n";
                        selStr += "#L" + (410007008 + randnumber) + "##fc0xFF6799FF#Lv.276#d | " + starImg + " 260  | 점령당한 골목 3#l\r\n";
                        selStr += "#L" + (410007009 + randnumber) + "##fc0xFF6799FF#Lv.276#d | " + starImg + " 260  | 점령당한 골목 4#l\r\n";
                        selStr += "\r\n#e#r[오디움 어센틱포스 280]#d#n\r\n";
                        selStr += "#L" + (410007012 + randnumber) + "##fc0xFF6799FF#Lv.277#d | " + starImg + " 280  | 볕 드는 실험실 1#l\r\n";
                        selStr += "#L" + (410007013 + randnumber) + "##fc0xFF6799FF#Lv.277#d | " + starImg + " 280  | 볕 드는 실험실 2#l\r\n";
                        selStr += "#L" + (410007014 + randnumber) + "##fc0xFF6799FF#Lv.277#d | " + starImg + " 280  | 볕 드는 실험실 3#l\r\n";
                        selStr += "\r\n#e#r[오디움 어센틱포스 300]#d#n\r\n";
                        selStr += "#L" + (410007015 + randnumber) + "##fc0xFF6799FF#Lv.278#d | " + starImg + " 300  | 잠긴 문 뒤 실험실 1#l\r\n";
                        selStr += "#L" + (410007016 + randnumber) + "##fc0xFF6799FF#Lv.278#d | " + starImg + " 300  | 잠긴 문 뒤 실험실 2#l\r\n";
                        selStr += "#L" + (410007017 + randnumber) + "##fc0xFF6799FF#Lv.278#d | " + starImg + " 300  | 잠긴 문 뒤 실험실 3#l\r\n";
                    }
                    break;

                case 9:
                    starImg = "#fUI/UIWindow.img/ToolTip/WorldMap/AuthenticForce#";
                    if (cm.getPlayer().getLevel() < 275) {
                        selStr += "#fs11##fc0xFF000000#해당 사냥터는 #b275레벨#fc0xFF000000#이상부터 입장 가능하다네";
                        cm.sendNext(selStr);
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getLevel() >= 275) {
                        selStr += "#fs11#사냥터의 #e#fc0xFF6799FF#어센틱포스#k#n와 #e#b몬스터의 평균 레벨#k#n 확인하고\r\n이동하게나.\r\n";
                        selStr += "\r\n#e#r[도원경 어센틱포스 230]#d#n\r\n";
                        selStr += "#L" + (410007028 + randnumber) + "##fc0xFF6799FF#Lv.275#d | " + starImg + " 230  | 생기가 돌아오는 봄 5#l\r\n";
                        selStr += "#L" + (410007039 + randnumber) + "##fc0xFF6799FF#Lv.275#d | " + starImg + " 230  | 생기가 돌아오는 봄 4#l\r\n";
                        selStr += "#L" + (410007038 + randnumber) + "##fc0xFF6799FF#Lv.275#d | " + starImg + " 230  | 생기가 돌아오는 봄 3#l\r\n";
                        selStr += "#L" + (410007027 + randnumber) + "##fc0xFF6799FF#Lv.275#d | " + starImg + " 230  | 생기가 돌아오는 봄 2#l\r\n";
                        selStr += "#L" + (410007026 + randnumber) + "##fc0xFF6799FF#Lv.275#d | " + starImg + " 230  | 생기가 돌아오는 봄 1#l\r\n";
                        selStr += "\r\n#e#r[도원경 어센틱포스 260]#d#n\r\n";
                        selStr += "#L" + (410007029 + randnumber) + "##fc0xFF6799FF#Lv.276#d | " + starImg + " 260  | 빛이 약한 여름 1#l\r\n";
                        selStr += "#L" + (410007030 + randnumber) + "##fc0xFF6799FF#Lv.276#d | " + starImg + " 260  | 빛이 약한 여름 2#l\r\n";
                        selStr += "#L" + (410007040 + randnumber) + "##fc0xFF6799FF#Lv.276#d | " + starImg + " 260  | 빛이 약한 여름 3#l\r\n";
                        selStr += "#L" + (410007041 + randnumber) + "##fc0xFF6799FF#Lv.276#d | " + starImg + " 260  | 빛이 약한 여름 4#l\r\n";
                        selStr += "#L" + (410007031 + randnumber) + "##fc0xFF6799FF#Lv.276#d | " + starImg + " 260  | 빛이 약한 여름 4#l\r\n";
                        selStr += "\r\n#e#r[도원경 어센틱포스 280]#d#n\r\n";
                        selStr += "#L" + (410007032 + randnumber) + "##fc0xFF6799FF#Lv.277#d | " + starImg + " 280  | 색깔이 옅은 가을 1#l\r\n";
                        selStr += "#L" + (410007033 + randnumber) + "##fc0xFF6799FF#Lv.277#d | " + starImg + " 280  | 색깔이 옅은 가을 2#l\r\n";
                        selStr += "#L" + (410007042 + randnumber) + "##fc0xFF6799FF#Lv.277#d | " + starImg + " 280  | 색깔이 옅은 가을 3#l\r\n";
                        selStr += "#L" + (410007043 + randnumber) + "##fc0xFF6799FF#Lv.277#d | " + starImg + " 280  | 색깔이 옅은 가을 4#l\r\n";
                        selStr += "#L" + (410007034 + randnumber) + "##fc0xFF6799FF#Lv.277#d | " + starImg + " 280  | 색깔이 옅은 가을 5#l\r\n";
                        selStr += "\r\n#e#r[도원경 어센틱포스 300]#d#n\r\n";
                        selStr += "#L" + (410007035 + randnumber) + "##fc0xFF6799FF#Lv.278#d | " + starImg + " 300  | 참혹한 흔적의 겨울 1#l\r\n";
                        selStr += "#L" + (410007036 + randnumber) + "##fc0xFF6799FF#Lv.278#d | " + starImg + " 300  | 참혹한 흔적의 겨울 2#l\r\n";
                        selStr += "#L" + (410007044 + randnumber) + "##fc0xFF6799FF#Lv.278#d | " + starImg + " 300  | 참혹한 흔적의 겨울 3#l\r\n";
                        selStr += "#L" + (410007045 + randnumber) + "##fc0xFF6799FF#Lv.278#d | " + starImg + " 300  | 참혹한 흔적의 겨울 4#l\r\n";
                        selStr += "#L" + (410007037 + randnumber) + "##fc0xFF6799FF#Lv.278#d | " + starImg + " 300  | 참혹한 흔적의 겨울 5#l\r\n";
                    }
                    break;


                case 10:
                    starImg = "#fUI/UIWindow.img/ToolTip/WorldMap/AuthenticForce#";
                    if (cm.getPlayer().getLevel() < 280) {
                        selStr += "#fs11##fc0xFF000000#해당 사냥터는 #b280레벨#fc0xFF000000#이상부터 입장 가능하다네";
                        cm.sendNext(selStr);
                        cm.dispose();
                        return;
                    }
                    if (cm.getPlayer().getLevel() >= 280) {
                        selStr += "#fs11#사냥터의 #e#fc0xFF6799FF#어센틱포스#k#n와 #e#b몬스터의 평균 레벨#k#n 확인하고\r\n이동하게나.\r\n";
                        selStr += "\r\n#e#r[아르테리아 어센틱포스 360]#d#n\r\n";
                        selStr += "#L" + (410007527 + randnumber) + "##fc0xFF6799FF#Lv.275#d | " + starImg + " 360  | 최하층 통로 1#l\r\n";
                        selStr += "#L" + (410007528 + randnumber) + "##fc0xFF6799FF#Lv.275#d | " + starImg + " 360  | 최하층 통로 2#l\r\n";
                        selStr += "#L" + (410007529 + randnumber) + "##fc0xFF6799FF#Lv.275#d | " + starImg + " 360  | 최하층 통로 3#l\r\n";
                        selStr += "#L" + (410007548 + randnumber) + "##fc0xFF6799FF#Lv.275#d | " + starImg + " 360  | 최하층 통로 4#l\r\n";
                        selStr += "#L" + (410007549 + randnumber) + "##fc0xFF6799FF#Lv.275#d | " + starImg + " 360  | 최하층 통로 5#l\r\n";
                        selStr += "#L" + (410007530 + randnumber) + "##fc0xFF6799FF#Lv.275#d | " + starImg + " 360  | 최하층 통로 6#l\r\n";
                        selStr += "\r\n#e#r[아르테리아 어센틱포스 400]#d#n\r\n";
                        selStr += "#L" + (410007537 + randnumber) + "##fc0xFF6799FF#Lv.276#d | " + starImg + " 400  | 최상층 통로 1#l\r\n";
                        selStr += "#L" + (410007538 + randnumber) + "##fc0xFF6799FF#Lv.276#d | " + starImg + " 400  | 최상층 통로 2#l\r\n";
                        selStr += "#L" + (410007539 + randnumber) + "##fc0xFF6799FF#Lv.276#d | " + starImg + " 400  | 최상층 통로 3#l\r\n";
                        selStr += "#L" + (410007550 + randnumber) + "##fc0xFF6799FF#Lv.276#d | " + starImg + " 400  | 최상층 통로 4#l\r\n";
                        selStr += "#L" + (410007551 + randnumber) + "##fc0xFF6799FF#Lv.276#d | " + starImg + " 400  | 최상층 통로 5#l\r\n";
                        selStr += "#L" + (410007552 + randnumber) + "##fc0xFF6799FF#Lv.276#d | " + starImg + " 400  | 최상층 통로 6#l\r\n";
                        selStr += "#L" + (410007553 + randnumber) + "##fc0xFF6799FF#Lv.276#d | " + starImg + " 400  | 최상층 통로 7#l\r\n";
                        selStr += "#L" + (410007540 + randnumber) + "##fc0xFF6799FF#Lv.276#d | " + starImg + " 400  | 최상층 통로 8#l\r\n";
                    }
                    break;


                case 11:
                    starImg = "#fUI/UIWindow.img/ToolTip/WorldMap/AuthenticForce#";
                    selStr += "#fs11#파티던전 #e#fc0xFF6799FF#핫타임던전#k#n와 #e#b파티던전#k#n 확인하고\r\n이동하게나.\r\n";
                    selStr += "\r\n#e#r[파티던전]#d#n\r\n";
                    selStr += "#L" + (993072000 + randnumber) + "##fc0xFF6799FF#Lv.200#d | " + starImg + " 0  | 파티초급던전 1#l\r\n";
                    selStr += "#L" + (993072100 + randnumber) + "##fc0xFF6799FF#Lv.200#d | " + starImg + " 0  | 파티중급 #l\r\n";
                    selStr += "#L" + (993072200 + randnumber) + "##fc0xFF6799FF#Lv.200#d | " + starImg + " 0  | 파티상급 #l\r\n";
                    //selStr += "#L" + (940204350 + randnumber) + "##fc0xFF6799FF#Lv.200#d | "+starImg+" 0  | 핫타임던전 4 #l\r\n";
                    //selStr += "#L" + (940204530 + randnumber) + "##fc0xFF6799FF#Lv.200#d | "+starImg+" 0  | 핫타임던전 5 #l\r\n";
                    //selStr += "#L" + (993134200 + randnumber) + "##fc0xFF6799FF#Lv.200#d | "+starImg+" 0  | 핫타임던전 6 #l\r\n";

                    break;
            }
            cm.sendSimpleS(selStr, 4);
        } else if (ans_01 == 4) { // 이벤트맵
            switch (ans_02) {
                case 1:
                    cm.dispose();
                    cm.sendOkS("#fs11#준비중 인거 같네..", 2);
                    break;

                case 2:
                    cm.warp(102, 0);
                    cm.dispose();
                    break;

            }
        } else if (ans_01 == 999) {
            cm.getPlayer().dropMessage(5, "해당 피로도 사냥터는 파티 플레이 시 경험치 공유가 되지 않습니다.");
            // 시, 분 불러오기
            today = new Date();
            hours = today.getHours();
            minutes = today.getMinutes();

            // 점검안내
            if (hours <= 0 && minutes <= 5) {
                cm.sendOk("#fs11##r#e[입장 불가안내]#k#n\r\n매일 00시00분 ~ 00시05분\r\n해당 시간에는 피로도 사냥터 입장이 불가능합니다");
                cm.dispose();
                return;
            }

            if (cm.getParty() != null) {
                if (cm.getPartyMembers().size() < 3) {
                    cm.warp(ans_02, "sp");
                    cm.dispose();
                } else {
                    cm.dispose();
                    cm.sendOk("#fs11#3인 이상 파티는 들어갈 수 없습니다.");
                }
            } else {
                cm.warp(ans_02, "sp");
                cm.dispose();
            }
            return;
        }
    } else if (status == 3) {
        mapcode = selection - randnumber;
        switch (ans_02) {
            case 1:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                cm.warp(mapcode, "sp");
                cm.dispose();
                break;

                // 피로도
            case 2:
                // 시, 분 불러오기
                today = new Date();
                hours = today.getHours();
                minutes = today.getMinutes();

                // 점검안내
                if (hours <= 0 && minutes <= 5) {
                    cm.sendOk("#fs11##r#e[입장 불가안내]#k#n\r\n매일 00시00분 ~ 00시05분\r\n해당 시간에는 피로도 사냥터 입장이 불가능합니다");
                    cm.dispose();
                    return;
                }

                if (cm.getParty() != null) {
                    if (cm.getPartyMembers().size() < 3) {
                        cm.warp(mapcode, "sp");
                        cm.dispose();
                    } else {
                        cm.dispose();
                        cm.sendOk("#fs11#3인 이상 파티는 들어갈 수 없습니다.");
                    }
                } else {
                    cm.warp(mapcode, "sp");
                    cm.dispose();
                }
                break;
        }
    }
}