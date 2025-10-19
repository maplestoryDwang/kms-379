package script.Server;

import constants.GameConstants;
import constants.ServerConstants;
import database.DBConfig;
import network.center.Center;
import network.discordbot.DiscordBotHandler;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.item.CustomItem;
import objects.item.Equip;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.quest.MapleQuest;
import objects.shop.MapleShopFactory;
import objects.users.MapleClient;
import objects.users.MapleStat;
import objects.users.looks.zero.ZeroInfoFlag;
import objects.users.skills.Skill;
import objects.users.skills.SkillEntry;
import objects.users.skills.VCore;
import objects.utils.Pair;
import objects.utils.Randomizer;
import scripting.NPCScriptManager;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;
import scripting.newscripting.ScriptManager;
import scripting.ScriptMessageType;
import scripting.GameObjectType;

import java.text.NumberFormat;
import java.util.*;

public class ZeniaCustomNPC extends ScriptEngineNPC {

    public void codySystem() {
        if (getPlayer().getMap().getFieldSetInstance() != null) {
            getPlayer().dropMessage(5, "보스 진행중엔 이용이 불가능합니다");
            return;
        }

        initNPC(MapleLifeFactory.getNPC(1530055));
        String msg = "#fs11##fUI/Basic.img/Zenia/SC/4#\r\n";
        msg += "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――#fc0xFF000000#\r\n";
        msg += "#L1##fUI/Basic.img/Zenia/SCBtn/100##l";
        msg += "#L2##fUI/Basic.img/Zenia/SCBtn/101##l";
        msg += "#L3##fUI/Basic.img/Zenia/SCBtn/102##l\r\n";
        msg += "#L5##fUI/Basic.img/Zenia/SCBtn/103##l";
        //msg += "#L8##fUI/Basic.img/Zenia/SCBtn/104##l";
        msg += "#L4##fUI/Basic.img/Zenia/SCBtn/105##l\r\n\r\n";
        String gColor = getPlayer().getGender() == 0 ? "#fs11##d여성" : "#fs11##b남성";
        String selStr = "";
        switch (self.askMenu(msg)) {
            case 1: {
                if (getPlayer().getMapId() != ServerConstants.TownMap) {
                    if (!getPlayer().isGM()) {
                        getPlayer().changeMap(ServerConstants.TownMap);
                        return;
                    }
                }
                int gender = getPlayer().getGender();
                if (GameConstants.isZero(getPlayer().getJob())) {
                    if (getPlayer().getZeroInfo().isBeta()) {
                        gender = 1;
                    }
                }

                selStr += "#e#r[캐릭터 헤어]#n#k#b\r\n";
                selStr += "#fs11##L998##e검색 헤어 이용#n#l#b\r\n";
                selStr += "#L1##fs11# 헤어 리스트 A#l\r\n#L2# 헤어 리스트 B#l\r\n";
                selStr += "#L3# 헤어 리스트 C#l\r\n#L4# 헤어 리스트 D#l \r\n#L5# 헤어 리스트 E#l#k\r\n";

                if (getPlayer().getAndroid() != null) {
                    selStr += "\r\n\r\n#e#r#fs12#[안드로이드 헤어]#k#b#n\r\n";
                    selStr += "#fs11##L995#" + 별흰 + 색 + " 검색 헤어#l#b\r\n";
                }

                int[] codyList = new int[0];
                switch (self.askMenu(selStr)) {
                    case 1: { //헤어 리스트 A
                        if (gender == 0)
                            codyList = new int[]{30000, 30020, 30030, 30040, 30050, 30060, 30100, 30110, 30120, 30130, 30140, 30150, 30160, 30170, 30180, 30190, 30200, 30210, 30220, 30230, 30240, 30250, 30260, 30270, 30280, 30290, 30300, 30310, 30320, 30330, 30340, 30350, 30360, 30370, 30400, 30410, 30420, 30440, 30450, 30460, 30470, 30480, 30490, 30510, 30520, 30530, 30540, 30560, 30570, 30590, 30610, 30620, 30630, 30640, 30650, 30660, 30670, 30680, 30700, 30710, 30730, 30760, 30770, 30790, 30800, 30810, 30820, 30830, 30840, 30850, 30860, 30870, 30880, 30910, 30930, 30940, 30950, 33030, 33060, 33070, 33080, 33090, 33110, 33120, 33130, 33150, 33170, 33180, 33190, 33210, 33220, 33250, 33260, 33270, 33280, 33310, 33330, 33350, 33360};
                        else
                            codyList = new int[]{31000, 31010, 31020, 31030, 31040, 31050, 31060, 31070, 31080, 31090, 31100, 31110, 31120, 31130, 31140, 31150, 31160, 31170, 31180, 31190, 31200, 31210, 31220, 31230, 31240, 31250, 31260, 31270, 31280, 31290, 31300, 31310, 31320, 31330, 31340, 31410, 31420, 31440, 31450, 31460, 31470, 31480, 31490, 31510, 31520, 31530, 31540, 31550, 31560, 31590, 31610, 31620, 31630, 31640, 31650, 31670, 31680, 31690, 31700, 31710, 31720, 31740, 31750, 31780, 31790, 31800, 31810, 31820, 31840, 31850, 31860, 31880, 31890};
                        break;
                    }
                    case 2: { //헤어 리스트 B
                        if (gender == 0)
                            codyList = new int[]{33380, 33390, 33400, 33410, 33430, 33440, 33450, 33460, 33480, 33500, 33510, 33520, 33530, 33550, 33580, 33590, 33600, 33610, 33620, 33630, 33640, 33660, 33670, 33680, 33690, 33700, 33710, 33720, 33730, 33740, 33750, 33760, 33770, 33780, 33790, 33800, 33810, 33820, 33830, 33930, 33940, 33950, 33960, 33990, 35000, 35010, 35020, 35030, 35040, 35050, 35060, 35070, 35080, 35090, 35100, 35150, 35180, 35190, 35200, 35210, 35250, 35260, 35280, 35290, 35300, 35310, 35330, 35350, 35360, 35420, 35430, 35440, 35460, 35470, 35480, 35490, 35500, 35510, 35520, 35530, 35540, 35550, 35560, 35570, 35600, 35620, 35630, 35640, 35650, 35660, 35680, 35690, 35710, 35720, 35780, 35790, 35950, 35960};
                        else
                            codyList = new int[]{31920, 31930, 31940, 31950, 31990, 34040, 34070, 34080, 34090, 34100, 34110, 34120, 34130, 34140, 34150, 34160, 34170, 34180, 34190, 34210, 34220, 34230, 34240, 34250, 34260, 34270, 34310, 34320, 34330, 34340, 34360, 34370, 34380, 34400, 34410, 34420, 34430, 34440, 34450, 34470, 34480, 34490, 34510, 34540, 34560, 34590, 34600, 34610, 34620, 34630, 34640, 34660, 34670, 34680, 34690, 34700, 34710, 34720, 34730, 34740, 34750, 34760, 34770, 34780, 34790, 34800, 34810, 34820, 34830, 34840, 34850, 34860, 34870, 34880, 34900, 34910, 34940, 34950, 34960, 34970};
                        break;
                    }
                    case 3: { //헤어리스트C
                        if (gender == 0)
                            codyList = new int[]{36010, 36020, 36030, 36040, 36050, 36070, 36080, 36090, 36100, 36130, 36140, 36150, 36160, 36170, 36180, 36190, 36200, 36210, 36220, 36230, 36240, 36250, 36300, 36310, 36330, 36340, 36350, 36380, 36390, 36400, 36410, 36420, 36430, 36440, 36450, 36460, 36470, 36480, 36510, 36520, 36530, 36560, 36570, 36580, 36590, 36620, 36630, 36640, 36650, 36670, 36680, 36690, 36700, 36710, 36720, 36730, 36740, 36750, 36760, 36770, 36780, 36790, 36800, 36810, 36820, 36830, 36840, 36850, 36860, 36900, 36910, 36920, 36940, 36950, 36980, 36990};
                        else
                            codyList = new int[]{37000, 37010, 37020, 37030, 37040, 37060, 37070, 37080, 37090, 37100, 37110, 37120, 37130, 37140, 37190, 37210, 37220, 37230, 37240, 37250, 37260, 37280, 37300, 37310, 37320, 37340, 37370, 37380, 37400, 37450, 37460, 37490, 37500, 37510, 37520, 37530, 37560, 37570, 37580, 37610, 37620, 37630, 37640, 37650, 37660, 37670, 37680, 37690, 37700, 37710, 37720, 37730, 37740, 37750, 37760, 37770, 37780, 37790, 37800, 37810, 37820, 37830, 37840, 37850, 37860, 37880, 37910, 37920, 37940, 37950, 37960, 37970, 37980, 37990, 38000, 38010, 38020, 38030, 38040, 38050, 38060, 38070, 38090, 38100, 38110, 38120, 38130, 38270, 38280, 38290, 38300, 38310, 38380, 38390, 38400, 38410, 38420, 38430, 38440, 38460, 38470, 38490, 38520, 38540, 38550, 38560, 38570, 38580, 38590, 38600, 38610, 38620, 38630, 38640, 38650};
                        break;
                    }
                    case 4: { //헤어리스트D
                        if (gender == 0)
                            codyList = new int[]{40000, 40010, 40020, 40050, 40060, 40090, 40100, 40120, 40250, 40260, 40270, 40280, 40290, 40300, 40310, 40320, 40330, 40390, 40400, 40410, 40420, 40440, 40450, 40470, 40480, 40490, 40500, 40510, 40570, 40580, 40600, 40610, 40640, 40660, 40670, 40690, 40720, 40740, 40810, 40820, 41060, 41070, 40930, 43020, 43140, 43150, 43180, 40780, 40770, 43290, 43300, 43310, 43320, 43350, 40650, 40710, 43580, 43570, 43330, 43667, 43610, 43620, 43700, 43750, 43750, 43760, 43770, 43780, 43810, 43910, 45000, 45010, 45060, 45040, 45050, 43820, 43900, 45090, 46030, 46060, 46070, 46080, 46090, 45220, 45230, 45150, 45100, 34910, 45110, 45150, 46060, 45160, 45110, 46330, 46320, 46310, 46340, 46350, 46420, 46430, 46440, 46450, 46460, 46490, 46500, 46530, 46570, 46590, 48850, 48840, 46810, 46790, 46770, 46830, 46840, 46870, 46780, 46890, 46860};
                        else
                            codyList = new int[]{38660, 38670, 38680, 38690, 38700, 38730, 38740, 38750, 38760, 38800, 38810, 38820, 38840, 38880, 38910, 38940, 39090, 41080, 41090, 41100, 41110, 41120, 41150, 41160, 41200, 41220, 41340, 41350, 41360, 41370, 41380, 41390, 41400, 41440, 41470, 41480, 41490, 41510, 41520, 41560, 41570, 41590, 41600, 41700, 44500, 44530, 41870, 44650, 44770, 44850, 44780, 44790, 44802, 44900, 41940, 44830, 44840, 44940, 44950, 47000, 47040, 47020, 47010, 47030, 47070, 47090, 47310, 47270, 47280, 47320, 47330, 47350, 47370, 47360, 47340, 48020, 48050, 48060, 48070, 48080, 47530, 47540, 47460, 47430, 47400, 47400, 47430, 47460, 41140, 46220, 48210, 48320, 48340, 48330, 48350, 48360, 48370, 48380, 48410, 48650, 48570, 48580, 48640, 48650, 48660, 48730, 48740, 48770, 48780, 48790, 48810};
                        break;
                    }
                    case 5: { //헤어 리스트E
                        if (gender == 0)
                            codyList = new int[]{46870, 46880, 46890, 46900, 46940, 46970, 46980, 47260, 60100, 60120, 60200, 60300, 60310};
                        else
                            codyList = new int[]{47560, 47570, 47580, 47590, 47600, 47610, 47620, 47630, 47640, 47670, 47690, 47740, 47760, 47770, 47780, 47790, 47800, 48020, 48050, 48060, 48070, 48080, 48130, 48140, 48210, 48220, 48320, 48330, 48340, 48350, 48360, 48370, 48380, 48410, 48430, 48450, 48480, 48490, 48500, 48510, 48520, 48530, 48540, 48550, 48560, 48570, 48580, 48640, 48650, 48660, 48840, 48850, 48900, 48960, 48970, 48990, 60000, 60060, 60070, 60080, 60090, 60110, 60130, 60140, 60150, 60160, 60170, 60180, 60190, 60210, 60220, 60230, 60240, 60250, 60260, 60270, 60280, 60290, 60320, 60330, 60340, 61050, 61060, 61070, 61080, 61090, 61100, 61110, 61120, 61130, 61140, 61150, 61160, 61170, 61180, 61190, 61200, 61210, 61220, 61230, 61240, 61250, 61260, 61270, 61280, 61290, 61300, 61310, 61320, 61330, 61340, 61350, 61360, 61370, 61380};
                        break;
                    }
                    case 998: { //검색헤어
                        openNpcCustom(getClient(), 3005131, "검색헤어");
                        return;
                    }
                    case 995: { //안드검색헤어
                        openNpcCustom(getClient(), 3005131, "검색안드헤어");
                        return;
                    }
                }
                if (codyList.length > 0) {
                    int v = self.askAvatar("#fs11##fn돋움##fc0xFFFFFFFF#지금의 헤어를 전혀 새로운 스타일로 바꿔 줄 수 있지. 지금 모습이 지겨워 졌다면 바꾸고 싶은 헤어를 천천히 고민해 봐", codyList);
                    if (v <= codyList.length && v >= 0) {
                        if (GameConstants.isAngelicBuster(getPlayer().getJob())) {
                            if (1 == self.askYesNo("#fs11#드레스업 모드로 적용 하시겠습니까?")) {
                                getPlayer().setSecondHair(codyList[v]);
                                getPlayer().fakeRelog();
                                getPlayer().equipChanged();
                                return;
                            }
                        }
                        if (GameConstants.isZero(getPlayer().getJob())) {
                            if (getPlayer().getZeroInfo().isBeta()) {
                                getPlayer().getZeroInfo().setSubHair(codyList[v]);
                                getPlayer().getZeroInfo().sendUpdateZeroInfo(getPlayer(), ZeroInfoFlag.SubHair);
                                getPlayer().equipChanged();
                                return;
                            }
                        }
                        getPlayer().setHair(codyList[v]);
                        getPlayer().updateSingleStat(MapleStat.HAIR, codyList[v]);
                        getPlayer().equipChanged();
                    }
                }
                break;
            }
            case 2: {
                if (getPlayer().getMapId() != ServerConstants.TownMap) {
                    if (!getPlayer().isGM()) {
                        getPlayer().changeMap(ServerConstants.TownMap);
                        return;
                    }
                }
                int gender = getPlayer().getGender();
                if (GameConstants.isZero(getPlayer().getJob())) {
                    if (getPlayer().getZeroInfo().isBeta()) {
                        gender = 1;
                    }
                }
                selStr += "#e#r[캐릭터 성형]#b#n\r\n";
                selStr += "#fs11##L999##e검색 성형 이용#n#l#b\r\n";
                selStr += "#e #n#L6##fs11#성형 리스트 A#l\r\n#L7# 성형 리스트 B#l\r\n#b#L8# 성형 리스트 C#l\r\n";

                if (getPlayer().getAndroid() != null) {
                    selStr += "\r\n\r\n#e#r#fs12#[안드로이드 성형]#k#b#n\r\n#fs11#";
                    selStr += "#fs11##L996#" + 별흰 + 색 + " 검색 성형#l#b\r\n";
                }

                int[] codyList = new int[0];
                switch (self.askMenu(selStr)) {
                    case 6: { //성형 리스트 A
                        if (gender == 0)
                            codyList = new int[]{20000, 20001, 20002, 20003, 20004, 20005, 20006, 20007, 20008, 20009, 20011, 20012, 20013, 20014, 20015, 20016, 20017, 20018, 20020, 20021, 20022, 20025, 20027, 20028, 20029, 20030, 20031, 20032, 20036, 20037, 20040, 20043, 20044, 20045, 20046, 20047, 20048, 20049, 20050, 20051, 20052, 20053, 20055, 20056, 20057, 20058, 20059, 20060, 20061, 20062, 20063, 20064, 20065, 20066, 20067, 20068, 20069, 20070, 20074, 20075, 20076, 20077, 20080, 20081, 20082, 20083, 20084, 20085, 20086, 20087, 20088, 20089, 20090, 20093, 20094, 20095, 20097, 20098, 20099, 20110, 23000, 23001, 23002, 23003, 23005, 23006, 23008, 23010, 23011, 23012};
                        else
                            codyList = new int[]{21002, 21003, 21004, 21005, 21006, 21007, 21008, 21009, 21010, 21011, 21012, 21013, 21014, 21015, 21016, 21017, 21020, 21021, 21023, 21024, 21026, 21027, 21028, 21029, 21030, 21031, 21033, 21036, 21038, 21041, 21042, 21043, 21044, 21045, 21048, 21050, 21052, 21053, 21056, 21058, 21059, 21061, 21062, 21063, 21065, 21073, 21074, 21075, 21077, 21078, 21079, 21080, 21081, 21082, 21083, 21084, 21085, 21089, 21090, 21091, 21092, 21093, 21094, 21095, 21096, 21097, 21098, 24002, 24003, 24004};
                        break;
                    }
                    case 7: { //성형 리스트 B
                        if (gender == 0)
                            codyList = new int[]{23015, 23016, 23017, 23018, 23019, 23020, 23023, 23024, 23025, 23026, 23027, 23028, 23029, 23031, 23032, 23033, 23035, 23038, 23039, 23040, 23041, 23042, 23043, 23044, 23053, 23054, 23056, 23057, 23060, 23061, 23062, 23063, 23067, 23068, 23069, 23072, 23073, 23074, 23075, 23079, 23080, 23081, 23082, 23083, 23084, 23085, 23086, 23087, 23088, 23089, 23090, 23091, 23092, 23095, 23096, 23097, 23099, 24061, 24098, 25006, 25007, 25011, 25014, 25016, 25017, 25021, 25022, 25023, 25024, 25025, 25027, 25033, 25058, 25057, 25049, 25053, 25029, 25020, 25043, 25044, 25063, 25062, 25050, 25080, 25079, 25083, 25055, 25085, 25088, 25089, 25091, 25073, 25075, 25084, 25099, 27010, 27011, 27038, 27039, 25093, 27008, 27006, 27022, 27006, 27008, 27037, 27052, 27064, 27065, 27066, 27069, 27070, 27073, 27076, 27077, 27078, 27079, 27080, 27081, 27082, 27083, 27084, 27085, 27086, 27087, 27092, 27098};
                        else
                            codyList = new int[]{24007, 24008, 24011, 24012, 24014, 24015, 24018, 24020, 24021, 24022, 24023, 24027, 24031, 24035, 24037, 24038, 24039, 24041, 24050, 24055, 24058, 24060, 24067, 24068, 24071, 24072, 24073, 24077, 24080, 24081, 24084, 24087, 24088, 24091, 24097, 24099, 25000, 25008, 25015, 26003, 26004, 26005, 26009, 26014, 26017, 26022, 26023, 26027, 26028, 26029, 26030, 26031, 26032, 26062, 26061, 26053, 26056, 26034, 26026, 26046, 26067, 26066, 26054, 26086, 26085, 25155, 26089, 26091, 26094, 26095, 26097, 28011, 26076, 26079, 28013, 26096, 28016, 28017, 28044, 26099, 28014, 28008, 28027, 28008, 28014, 28043, 28057, 28070, 28071, 28072, 28075, 28076, 28078, 28079, 28082, 28085, 28086, 28087, 28088, 28089, 28090, 28091, 28092, 28093, 28094, 28095, 28096, 28099, 28108, 28109, 28110, 28111, 28112, 28113, 28114, 28115, 28116, 28117, 28123};
                        break;
                    }
                    case 8: { //성형리스트C
                        if (gender == 0)
                            codyList = new int[]{27106, 27107, 27108, 27109, 27110, 27111, 27117, 27118, 27119, 27120, 27122, 27125, 27135, 27137, 27138, 27139, 27144, 27145, 27152, 27164, 27165, 27166, 27167, 27169, 27170, 27173, 27174, 27176, 27177, 27178, 27179, 27180, 27181, 27182, 27183, 27185, 27186, 27187, 27189, 27192, 27196, 27198};
                        else
                            codyList = new int[]{28124, 28130, 28141, 28143, 28144, 28149, 28150, 28156, 28157, 28170, 28171, 28172, 28173, 28175, 28176, 28178, 28179, 28182, 28183, 28185, 28186, 28187, 28188, 28189, 28190, 28191, 28192, 28193, 28194, 28195, 28196, 28197, 28199, 28208, 28209, 28210, 28211, 28212, 28213, 28214, 28215, 28216, 28217, 28223, 28224, 28225, 28227, 28230, 28241, 28243, 28244, 28249, 28250, 28256, 28257, 28270, 28271, 28272, 28273, 28275, 28276, 28278, 28279, 28282, 28283, 28285, 28286, 28287, 28288, 28289, 28290, 28291};
                        break;
                    }
                    case 999: { //검색성형
                        openNpcCustom(getClient(), 3005131, "검색성형");
                        return;
                    }
                    case 996: { //안드검색성형
                        openNpcCustom(getClient(), 3005131, "검색안드성형");
                        return;
                    }
                }
                if (codyList.length > 0) {
                    int v = self.askAvatar("#fs11##fn돋움##fc0xFFFFFFFF#지금의 얼굴을 전혀 새로운 스타일로 바꿔 줄 수 있지. 지금 모습이 지겨워 졌다면 바꾸고 싶은 얼굴을 천천히 고민해 봐", codyList);
                    if (v <= codyList.length && v >= 0) {
                        if (GameConstants.isAngelicBuster(getPlayer().getJob())) {
                            if (1 == self.askYesNo("#fs11#드레스업 모드로 적용 하시겠습니까?")) {
                                getPlayer().setSecondFace(codyList[v]);
                                getPlayer().fakeRelog();
                                getPlayer().equipChanged();
                                return;
                            }
                        }
                        if (GameConstants.isZero(getPlayer().getJob())) {
                            if (getPlayer().getZeroInfo().isBeta()) {
                                getPlayer().getZeroInfo().setSubFace(codyList[v]);
                                getPlayer().getZeroInfo().sendUpdateZeroInfo(getPlayer(), ZeroInfoFlag.SubFace);
                                getPlayer().equipChanged();
                                return;
                            }
                        }
                        getPlayer().setFace(codyList[v]);
                        getPlayer().updateSingleStat(MapleStat.FACE, codyList[v]);
                        getPlayer().equipChanged();
                    }
                }
                break;
            }
            case 3: {
                if (getPlayer().getMapId() != ServerConstants.TownMap) {
                    if (!getPlayer().isGM()) {
                        getPlayer().changeMap(ServerConstants.TownMap);
                        return;
                    }
                }
                openNpcCustom(getClient(), 9000172, "9000172");
                break;
            }
            case 4: {
                selStr += "#fc0xFF000000##L997##e치장 상점#n 이용#l\r\n\r\n";

                selStr += "#fc0xFF000000##L23##e해외캐시#n 이용#l\r\n\r\n";

                //원래 주석된거임
                //selStr += "#L29# 스페셜 헤어#n 이용하기#l\r\n";
                //selStr += "#L98##fMap/MapHelper.img/minimap/party# 알쏭 달쏭 믹렌#n 이용하기#l\r\n";
                selStr += "#fc0xFF000000##L988##e캐릭터 모든 RGB#n 변경#l\r\n\r\n";
                selStr += "#fc0xFF000000##L10##e" + gColor + "#n으로 성전환#l#b#n\r\n";
                switch (self.askMenu(selStr)) {
                    case 10: {
                        if (getPlayer().getJob() == 10112) {
                            self.sayOk("제로 직업군은 성전환을 할 수 없습니다.");
                            return;
                        }
                        if (1 == self.askYesNo("#e" + gColor + "#n#k으로 성전환을 하시겠습니까? 더 이상 해당 성별에 맞는 장비를 착용할 수 없습니다.")) {
                            if (getPlayer().getGender() == 0) {
                                getPlayer().setHair(31002);
                                getPlayer().setFace(21700);
                                getPlayer().setGender((byte) 1);
                            } else {
                                getPlayer().setHair(30000);
                                getPlayer().setFace(20100);
                                getPlayer().setGender((byte) 0);
                            }
                            getPlayer().fakeRelog();
                            getPlayer().getMap().broadcastMessage(CField.updateCharLook(getPlayer()));
                            return;
                        }
                        break;
                    }
                    case 23: {
                        openNpcCustom(getClient(), 3005131, "globalcash");
                        break;
                    }
                    case 997: {
                        MapleShopFactory.getInstance().getShop(11).sendShop(getClient());
                        break;
                    }
                    case 988: {
                        int number = self.askNumber("#fs11#변경을 원하는 #r색상값#k을 입력하면 돼\r\n기존 칼라는 #d'0'#k 이야 (0 ~ 1000)", 0, 0, 1000);
                        if (number >= 0) {
                            getPlayer().setKeyValue(100229, "hue", String.valueOf(number));
                            getPlayer().fakeRelog();
                            getPlayer().getMap().broadcastMessage(CField.updateCharLook(getPlayer()));
                            self.sayOk("변경을 완료했어! 확인해봐");
                        }
                        break;
                    }
                }
                break;
            }
            case 5: { //검색캐시
                openNpcCustom(getClient(), 3005131, "Itemsearch");
                break;
            }
            case 6: {
                openNpcCustom(getClient(), 3005131, "globalcash");
                break;
            }
            case 7: {
                MapleShopFactory.getInstance().getShop(11).sendShop(getClient());
                break;
            }
            case 8: {
                openNpcCustom(getClient(), 3005131, "색변선택");
                break;
            }
        }
    }

    public final void openNpcCustom(MapleClient client, final int id, final String name) {
        getClient().removeClickedNPC();
        NPCScriptManager.getInstance().start(client, id, name, true);
    }

    private String 보라 = "#fMap/MapHelper.img/weather/starPlanet/7#";
    private String 파랑 = "#fMap/MapHelper.img/weather/starPlanet/8#";
    private String 별파 = "#fUI/GuildMark.img/Mark/Pattern/00004001/11#";
    private String 별노 = "#fUI/GuildMark.img/Mark/Pattern/00004001/3#";
    private String 별흰 = "#fUI/GuildMark.img/Mark/Pattern/00004001/15#";
    private String 별갈 = "#fUI/GuildMark.img/Mark/Pattern/00004001/5#";
    private String 별빨 = "#fUI/GuildMark.img/Mark/Pattern/00004001/1#";
    private String 별검 = "#fUI/GuildMark.img/Mark/Pattern/00004001/16#";
    private String 별보 = "#fUI/GuildMark.img/Mark/Pattern/00004001/13#";
    private String 별 = "#fUI/FarmUI.img/objectStatus/star/whole#";
    private String S = "#fUI/CashShop.img/CSEffect/today/0#";
    private String 보상 = "#fUI/UIWindow2.img/Quest/quest_info/summary_icon/reward#";
    private String 획득 = "#fUI/UIWindow2.img/QuestIcon/4/0#";
    private String 색 = "#fc0xFF6600CC#";
    private String 엔터 = "\r\n";
    private String 엔터2 = "\r\n\r\n";
}






