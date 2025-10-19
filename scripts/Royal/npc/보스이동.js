importPackage(Packages.scripting.newscripting)
importPackage(Packages.constants)

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
엔터 = "\r\n"
엔터2 = "\r\n\r\n"

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }

    if (status == 0) {
        cm.dispose();
        cm.openUI(1338);
        return;


        if (cm.inBoss() && !cm.isLeader) {
            cm.getPlayer().dropMessage(5, "보스 진행중엔 이용이 불가능합니다.");
            cm.dispose();
            return;
        }

        var count = 1;
        // 최대 횟수
        count += cm.getPlayer().getBossTier();
        var msg = "#fs11#";
/*
        if (cm.getPlayer().isGM()) {
            msg += "#fc0xFF000000##e[#e#rEVENT#k#l]\r\n";
            msg += "#L30##fc0xFF6799FF#" + 별보 + " | 텐구#l　　";
            msg += "#L31##fc0xFF6799FF#" + 별보 + " | 아케치 미츠히데#l\r\n\r\n";
        }
*/
        msg += "#fc0xFF000000#※ 보스이동은 파티장만 가능합니다\r\n\r\n";
        msg += "#fc0xFF000000##e#r[EASY]#k#l\r\n";
        msg += "#L1##fc0xFF6799FF#[" + cm.getPlayer().getOneInfoQuestInteger(QuestExConstants.ChaosZakum.getQuestID(), "eNum") + "/" + count + "]#d " + 별노 + " | 자쿰#l　　 　　";
        // msg += "#L2##fc0xFF6799FF#[" + cm.getPlayer().getOneInfoQuestInteger(1234569, "zakum_clear") + "/" + count + "]#d " + 별노 + " | 카웅#l\r\n";
        msg += "#L3##fc0xFF6799FF#[" + cm.getPlayer().getOneInfoQuestInteger(QuestExConstants.Horntail.getQuestID(), "eNum") + "/" + count + "]#d " + 별노 + " | 혼테일#l\r\n";
        //msg += "#L4##fc0xFF6799FF#[" + cm.getPlayer().getOneInfoQuestInteger(QuestExConstants.Hillah.getQuestID(), "eNum") + "/" + count + "]#d " + 별노 + " | 힐라#l";
        msg += "#L5##fc0xFF6799FF#[" + cm.getPlayer().getOneInfoQuestInteger(1234571, "vonleon_clear") + "/" + count + "]#d " + 별노 + " | 반레온#l　　 　";
        msg += "#L6##fc0xFF6799FF#[" + cm.getPlayer().getOneInfoQuestInteger(1234570, "akairum_clear") + "/" + count + "]#d " + 별노 + " | 아카이럼#l\r\n";
        msg += "#L7##fc0xFF6799FF#[" + cm.getPlayer().getOneInfoQuestInteger(1234569, "pinkbean_clear") + "/" + count + "]#d " + 별노 + " | 핑크빈#l　　 　";
        msg += "#L8##fc0xFF6799FF#[" + cm.getPlayer().getOneInfoQuestInteger(QuestExConstants.Cygnus.getQuestID(), "eNum") + "/" + count + "]#d " + 별노 + " | 시그너스#l#k\r\n\r\n";
        msg += "\r\n#fs11##fc0xFF000000##e#r[NOMARL]#k#l#k\r\n";
        msg += "#L9##fc0xFF6799FF#["+cm.getPlayer().getOneInfoQuestInteger(QuestExConstants.Magnus.getQuestID(), "eNum") + "/" + count + "]["+cm.getPlayer().getOneInfoQuestInteger(QuestExConstants.HardMagnus.getQuestID(), "eNum") + "/" + count + "]#d " + 별파 + " | 매그너스#l";
        msg += "#L11##fc0xFF6799FF#["+cm.getPlayer().getOneInfoQuestInteger(1234569, "papulatus_clear")  + "/" + count + "]["+cm.getPlayer().getOneInfoQuestInteger(1234569, "chaos_papulatus_clear")  + "/" + count + "]#d " + 별파 + " | 파풀#k#l\r\n";
        msg += "#L10##fc0xFF6799FF#["+cm.getPlayer().getOneInfoQuestInteger(QuestExConstants.CrimsonQueen.getQuestID(), "eNum") + "/" + count + "]["+cm.getPlayer().getOneInfoQuestInteger(QuestExConstants.ChaosCrimsonQueen.getQuestID(), "eNum") + "/" + count + "]#d " + 별파 + " | 블러디퀸#l";
        msg += "#L10##fc0xFF6799FF#["+cm.getPlayer().getOneInfoQuestInteger(QuestExConstants.Pierre.getQuestID(), "eNum") + "/" + count + "]["+cm.getPlayer().getOneInfoQuestInteger(QuestExConstants.ChaosPierre.getQuestID(), "eNum") + "/" + count + "]#d " + 별파 + " | 피에르#l\r\n";
        msg += "#L10##fc0xFF6799FF#["+cm.getPlayer().getOneInfoQuestInteger(QuestExConstants.VonBon.getQuestID(), "eNum") + "/" + count + "]["+cm.getPlayer().getOneInfoQuestInteger(QuestExConstants.ChaosVonBon.getQuestID(), "eNum") + "/" + count + "]#d " + 별파 + " | 반반#l　　";
        msg += "#L10##fc0xFF6799FF#["+cm.getPlayer().getOneInfoQuestInteger(QuestExConstants.Vellum.getQuestID(), "eNum") + "/" + count + "]["+cm.getPlayer().getOneInfoQuestInteger(QuestExConstants.ChaosVellum.getQuestID(), "eNum") + "/" + count + "]#d " + 별파 + " | 벨룸#l\r\n\r\n";
        msg += "\r\n#fs11##fc0xFF000000##e#r[HARD]#k#l#k\r\n";
        msg += "#L12##fc0xFF6799FF#[" + cm.getPlayer().getOneInfoQuestInteger(1234570, "swoo_clear") + "/" + count + "][" + cm.getPlayer().getOneInfoQuestInteger(1234569, "swoo_clear") + "/" + count + "]#d " + 별빨 + " | 스우#l　　";
        msg += "#L13##fc0xFF6799FF#[" + cm.getPlayer().getOneInfoQuestInteger(1234570, "demian_clear") + "/" + count + "][" + cm.getPlayer().getOneInfoQuestInteger(1234569, "demian_clear") + "/" + count + "]#d " + 별빨 + " | 데미안#l\r\n";
        msg += "#L14##fc0xFF6799FF#[" + cm.getPlayer().getOneInfoQuestInteger(1234570, "lucid_clear") + "/" + count + "][" + cm.getPlayer().getOneInfoQuestInteger(1234569, "lucid_clear") + "/" + count + "]#d " + 별빨 + " | 루시드#l　";
        msg += "#L15##fc0xFF6799FF#[" + cm.getPlayer().getOneInfoQuestInteger(1234570, "will_clear") + "/" + count + "][" + cm.getPlayer().getOneInfoQuestInteger(1234569, "will_clear") + "/" + count + "]#d " + 별빨 + " | 윌#l\r\n";
        //msg += "#L16##fc0xFF6799FF#[" + cm.getPlayer().getKeyValue("Normal_FireWolf") +"/" + count +"]#d | "+별빨+" | 불꽃늑대#l\r\n";
        //msg += "#L17##fc0xFF6799FF#[" + cm.getPlayer().getKeyValue("URS") +"/" + count +"]#d | "+별빨+" | 우르스#l";
        msg += "#L19##fc0xFF6799FF#[0/0][" + cm.getPlayer().getOneInfoQuestInteger(1234589, "dunkel_clear") + "/" + count + "]#d " + 별빨 + " | 듄켈#l　　";
        msg += "#L20##fc0xFF6799FF#[" + cm.getPlayer().getOneInfoQuestInteger(1234590, "dusk_clear") + "/" + count + "][" + cm.getPlayer().getOneInfoQuestInteger(1234589, "dusk_clear") + "/" + count + "]#d " + 별빨 + " | 더스크#l\r\n";
        msg += "#L23##fc0xFF6799FF#[" + cm.getPlayer().getOneInfoQuestInteger(1234570, "guardian_angel_slime_clear") + "/" + count + "][" + cm.getPlayer().getOneInfoQuestInteger(1234569, "guardian_angel_slime_clear") + "/" + count + "]#d " + 별빨 + " | 가디언 엔젤 슬라임#l\r\n\r\n\r\n";

        msg += "　　　#L18##fc0xFF6799FF#[" + cm.getPlayer().getOneInfoQuestInteger(1234569, "jinhillah_clear") + "/" + count + "]#d " + 별검 + " | 고통의 미궁 : 진 힐라#l\r\n";
        msg += "　　　#L21##fc0xFF6799FF#[" + cm.getPlayer().getOneInfoQuestInteger(1234570, "blackmage_clear") + "/" + count + "]#d " + 별검 + " | 초월자 : 검은마법사#l\r\n";
        msg += "　　　#L22##fc0xFF6799FF#[" + cm.getPlayer().getOneInfoQuestInteger(QuestExConstants.SerniumSeren.getQuestID(), "clear") + "/" + count + "]#d " + 별검 + " | 선택받은 세렌#l\r\n ";
        cm.sendSimpleS(msg, 4);
    } else if (status == 1) {
        ans_01 = selection;

        if (!cm.isLeader()) {
            cm.getPlayer().dropMessage(5, "파티가 없거나 파티장이 아닙니다.");
            cm.dispose();
            return;
        }

        if (cm.inBoss() && !cm.isLeader) {
            cm.getPlayer().dropMessage(5, "보스 진행중엔 이용이 불가능합니다.");
            cm.dispose();
            return;
        }

        if (ans_01 <= 0) {
            cm.dispose();
            return;
        }

        switch (ans_01) {
        case 2:
            cm.dispose();
            cm.warpParty(221030900, 0);
            return;
        case 1:
            cm.dispose();
            cm.warpParty(211042300, 1);
            return;

        case 3:
            cm.dispose();
            cm.warpParty(240050000, 0);
            return;

        case 4:
            cm.dispose();
            cm.warpParty(262000000, 0);
            return;

        case 5:
            cm.dispose();
            cm.warpParty(211070000, 2);
            return;

        case 6:
            cm.dispose();
            cm.warpParty(272020110, 0);
            return;

        case 7:
            cm.dispose();
            cm.warpParty(270050000, 0);
            return;

        case 8:
            cm.dispose();
            //cm.warpParty(271041000, 0);
            cm.sendOk("#fs11#현재 시그너스는 처치가 불가능합니다");
            return;

        case 9:
            cm.dispose();
            cm.warpParty(401060000, 0);
            return;

        case 10:
            cm.dispose();
            cm.warpParty(105200000, 0);
            return;

        case 11:
            cm.dispose();
            cm.warpParty(220080000, 6);
            return;

        case 12:
            cm.dispose();
            cm.warpParty(350060300, 1);
            return;

        case 13:
            cm.dispose();
            cm.warpParty(105300303, 1);
            return;

        case 14:
            cm.dispose();
            cm.warpParty(450004000, 0);
            return;

        case 15:
            cm.dispose();
            cm.warpParty(450007240, 0);
            return;

        case 16:
            cm.dispose();
            cm.openNpc(9120012);
            return;

        case 17:
            cm.dispose();
            cm.warpParty(970072200, 0);
            return;

        case 18:
            cm.dispose();
            cm.warpParty(450011990, 0);
            return;

        case 19:
            cm.dispose();
            cm.warpParty(450012200, 0);
            return;

        case 20:
            cm.dispose();
            cm.warpParty(450009301, 1);
            return;

        case 21:
            cm.dispose();
            cm.warpParty(450012500, 0);
            return;

        case 22:
            cm.dispose();
            cm.warpParty(410000670, 2);
            return;
        case 23:
            cm.dispose();
            cm.warpParty(160000000, 0);
            return;
        case 30:
            cm.dispose();
            ScriptManager.runScript(cm.getClient(), "tengu_enter", null);
            return;
        case 31:
            cm.dispose();
            ScriptManager.runScript(cm.getClient(), "mitsuhide_enter", null);
            return;
        }
    }
}
