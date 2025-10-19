importPackage(Packages.handling.channel);
importPackage(java.text);
importPackage(Packages.handling.cashshop);
importPackage(Packages.handling.channel.handler);
importPackage(Packages.handling.cashshop.handler);
importPackage(java.text);
importPackage(java.lang);
importPackage(Packages.tools.packet);

var GameConstants = Packages.constants.GameConstants;
var checkAngelicBuster = false;
var dressUp = 0;

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
핑크색 ="#fc0xFFFF3366#"
분홍색 = "#fc0xFFF781D8#"
엔터 = "\r\n"
엔터2 = "\r\n\r\n"
enter = "\r\n";


function start()
{
    status = -1;
    action(1, 0, 0);
} 

function action(mode, type, sel) {
    action(mode, type, sel, 0);
}

function action(mode, type, sel, dressUp_) {
//function action(mode, type, sel) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (cm.getPlayer().getJob() >= 6500 && cm.getPlayer().getJob() <= 6512) {
            if (!checkAngelicBuster) {
                cm.askAngelicBuster();
                checkAngelicBuster = true;
                return;
            }
            if (dressUp_ > 0) {
                dressUp = 1;
            }
        }
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
    }


    if (status == 0) {
        if (cm.getPlayer().getJob() == 10112) {
            cm.sendOk("#fs11#제로 직업군은 현재 스폐셜헤어 서비스를 이용할 수 없어요");
            cm.dispose();
            return;
        }

        var msg = "#fs11##fUI/Basic.img/Zenia/SC/4#\r\n";
        msg += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#\r\n";
        msg += "                          #L1##fUI/Basic.img/Zenia/SCBtn/100##l";
        cm.sendSimple(msg);

    } else if (status ==1) {
        if (cm.getPlayer().getBaseColor() > -1) {
            cm.getPlayer().setBaseColor(-1);
            cm.getPlayer().setAddColor(0);
            cm.getPlayer().setBaseProb(0);
            cm.getPlayer().dropMessage(1, "믹스 염색이 해제되었습니다. 다시 시도해주시기 바랍니다.");
            cm.dispose();
            return;
        }
        if (cm.getPlayer().getHair() % 10 != 0) {
            cm.getPlayer().dropMessage(1, "스페셜 헤어는 검은색 머리만 가능합니다.\r\n염색 후 다시 시도해주세요");
            cm.dispose();
            return;
        }
    codyList = [];
    seld = sel;
    selStr = "";
      switch (seld) {
        case 1:
            selStr += "#fs11#" + 핑크색 + "※ 스페셜헤어는 염색이 불가능합니다#n\r\n";
            selStr += 검은색;
            //selStr += "#L1111##fs11# 스페셜 헤어 여#l\r\n";
            //selStr += "#L2222##fs11# 스페셜 헤어 남#l\r\n";
            selStr += "#L1##fs11# 스페셜 헤어 A (50개)#l\r\n";
            selStr += "#L2##fs11# 스페셜 헤어 B (50개)#l\r\n";
            selStr += "#L3##fs11# 스페셜 헤어 C (50개)#l\r\n";
            selStr += "#L4##fs11# 스페셜 헤어 D (50개)#l\r\n";
            selStr += "#L5##fs11# 스페셜 헤어 E (50개)#l\r\n";
            selStr += "#L6##fs11# 스페셜 헤어 F (50개)#l\r\n";
            selStr += "#L7##fs11# 스페셜 헤어 G (50개)#l\r\n";
            selStr += "#L8##fs11# 스페셜 헤어 H (50개)#l\r\n";
            selStr += "#L9##fs11# 스페셜 헤어 I (49개)#l\r\n";
            break;
      }
      cm.sendSimpleS(selStr, 4);
      
   } else if(status == 2) {
       selStr = "#fs11##fn돋움##fc0xFFFFFFFF#지금의 헤어를 전혀 새로운 스타일로 바꿔 줄 수 있지. 지금 모습이 지겨워 졌다면 바꾸고 싶은 헤어를 천천히 고민해 봐";
       switch (sel) {
            // 헤어 리스트 시작
            case 1111: // 스폐셜헤어 여
                codyList = [39000, 39010, 39020, 39030, 39040, 39050, 39060, 39070, 39080, 39100, 39110, 39120, 39130, 39140, 39150, 39160, 39170, 39180, 39190, 39200, 39210, 39500, 39510, 39520, 39530];
            break;
            case 2222: // 스폐셜헤어 남
                codyList = [39540, 39550, 39560, 39570, 39580, 39590];
            break;
            case 1: // 스폐셜헤어 A
                codyList = [65000, 65010, 65020, 65030, 65040, 65050, 65060, 65070, 65080, 65090, 65100, 65110, 65120, 65130, 65140, 65150, 65160, 65170, 65180, 65190, 65200, 65210, 65220, 65230, 65240, 65250, 65260, 65270, 65280, 65290, 65300, 65310, 65320, 65330, 65340, 65350, 65360, 65370, 65380, 65390, 65400, 65410, 65420, 65430, 65440, 65450, 65460, 65470, 65480, 65490];
            break;
            case 2: // 스폐셜헤어 B
                codyList = [65500, 65510, 65520, 65530, 65540, 65550, 65560, 65570, 65580, 65590, 65600, 65610, 65620, 65630, 65640, 65650, 65660, 65670, 65680, 65690, 65700, 65710, 65720, 65730, 65740, 65750, 65760, 65770, 65780, 65790, 65800, 65810, 65820, 65830, 65840, 65850, 65860, 65870, 65880, 65890, 65900, 65910, 65920, 65930, 65940, 65950, 65960, 65970, 65980, 65990];
            break;
            case 3: // 스폐셜헤어 C
                codyList = [66000, 66010, 66020, 66030, 66040, 66050, 66060, 66070, 66080, 66090, 66100, 66110, 66120, 66130, 66140, 66150, 66160, 66170, 66180, 66190, 66200, 66210, 66220, 66230, 66240, 66250, 66260, 66270, 66280, 66290, 66300, 66310, 66320, 66330, 66340, 66350, 66360, 66370, 66380, 66390, 66400, 66410, 66420, 66430, 66440, 66450, 66460, 66470, 66480, 66490];
            break;
            case 4: // 스폐셜헤어 D
                codyList = [66500, 66510, 66520, 66530, 66540, 66550, 66560, 66570, 66580, 66590, 66600, 66610, 66620, 66630, 66640, 66650, 66660, 66670, 66680, 66690, 66700, 66710, 66720, 66730, 66740, 66750, 66760, 66770, 66780, 66790, 66800, 66810, 66820, 66830, 66840, 66850, 66860, 66870, 66880, 66890, 66900, 66910, 66920, 66930, 66940, 66950, 66960, 66970, 66980, 66990];
            break;
            case 5: // 스폐셜헤어 E
                codyList = [67000, 67010, 67020, 67030, 67040, 67050, 67060, 67070, 67080, 67090, 67100, 67110, 67120, 67130, 67140, 67150, 67160, 67170, 67180, 67190, 67200, 67210, 67220, 67230, 67240, 67250, 67260, 67270, 67280, 67290, 67300, 67310, 67320, 67330, 67340, 67350, 67360, 67370, 67380, 67390, 67400, 67410, 67420, 67430, 67440, 67450, 67460, 67470, 67480, 67490];
            break;
            case 6: // 스폐셜헤어 F
                codyList = [67500, 67510, 67520, 67530, 67540, 67550, 67560, 67570, 67580, 67590, 67600, 67610, 67620, 67630, 67640, 67650, 67660, 67670, 67680, 67690, 67700, 67710, 67720, 67730, 67740, 67750, 67760, 67770, 67780, 67790, 67800, 67810, 67820, 67830, 67840, 67850, 67860, 67870, 67880, 67890, 67900, 67910, 67920, 67930, 67940, 67950, 67960, 67970, 67980, 67990];
            break;
            case 7: // 스폐셜헤어 G
                codyList = [68000, 68010, 68020, 68030, 68040, 68050, 68060, 68070, 68080, 68090, 68100, 68110, 68120, 68130, 68140, 68150, 68160, 68170, 68180, 68190, 68200, 68210, 68220, 68230, 68240, 68250, 68260, 68270, 68280, 68290, 68300, 68310, 68320, 68330, 68340, 68350, 68360, 68370, 68380, 68390, 68400, 68410, 68420, 68430, 68440, 68450, 68460, 68470, 68480, 68490];
            break;
            case 8: // 스폐셜헤어 H
                codyList = [68500, 68510, 68520, 68530, 68540, 68550, 68560, 68570, 68580, 68590, 68600, 68610, 68620, 68630, 68640, 68650, 68660, 68670, 68680, 68690, 68700, 68710, 68720, 68730, 68740, 68750, 68760, 68770, 68780, 68790, 68800, 68810, 68820, 68830, 68840, 68850, 68860, 68870, 68880, 68890, 68900, 68910, 68920, 68930, 68940, 68950, 68960, 68970, 68980, 68990];
            break;
            case 9: // 스폐셜헤어 I
                codyList = [69000, 69010, 69020, 69030, 69040, 69050, 69060, 69070, 69080, 69090, 69100, 69110, 69120, 69130, 69140, 69150, 69160, 69170, 69180, 69190, 69200, 69210, 69220, 69230, 69240, 69250, 69260, 69270, 69280, 69290, 69300, 69310, 69320, 69330, 69340, 69350, 69360, 69370, 69380, 69390, 69400, 69410, 69420, 69430, 69440, 69450, 69460, 69470, 69480];
            break;
       }
        cm.sendStyle(selStr, dressUp, codyList);
        
    } else if(status ==3) {
        newItem = sel & 0xFF;
        if (dressUp == 0) { // 기본
            cm.gainItem(2630441, -1);
            setAvatar(codyList[newItem]);
            cm.dispose();
        } else { // 엔버드레스업
            cm.gainItem(2630441, -1);
            setAvatar2(codyList[newItem]);
            cm.dispose();
        }
    }
}

function setAvatar(args) {
    if (isHair(args)) {
        cm.setHair(args);
    } else if (isFace(args)) {
        cm.setFace(args);
    }
    cm.getPlayer().updateSingleStat(MapleStat.HAIR, codyList[newItem]);
    cm.getPlayer().equipChanged();
}

function setAvatar2(args) {
    if (isHair(args)) {
        cm.getPlayer().setSecondHair(args);
    } else if (isFace(args)) {
        cm.getPlayer().setSecondFace(args);
    }
    cm.getPlayer().fakeRelog();
    cm.getPlayer().equipChanged();
}

function isHair(itemId) {
    return Math.floor(itemId / 10000) == 3 || Math.floor(itemId / 10000) == 4 || Math.floor(itemId / 10000) == 6;
}

function isFace(itemId) {
    return Math.floor(itemId / 10000) == 2 || Math.floor(itemId / 10000) == 5;
}