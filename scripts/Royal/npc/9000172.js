importPackage(Packages.scripting);
importPackage(Packages.constants);
importPackage(Packages.objects.users);


var status = -1;
var sel = -1;
var sel2 = -1;
var type = -1;
var gar = -1;
var page = -1;
var subPage = -1;

var maleHairCount = 0;
var femaleHairCount = 0;
var maleFaceCount = 0;
var femaleFaceCount = 0;

var specialHairE = [
    32700, 32710, 32720, 32730, 32740, 32750, 32760, 32770, 32780, 32790, 32800, 32810, 32820, 32830, 32860, 32870, 32880, 32890, 32890, 32900, 32910,
    32920, 32930, 32940, 32950, 32960, 32970, 32980, 32990, 39100, 39110, 39120, 39130, 39140, 39150, 39160, 39170, 45320, 45330, 45340, 45350, 45360,
    45370, 45380, 45390, 45400, 45410, 45420, 45430, 45440, 45450, 45460, 45470, 45480, 45490, 45500, 45510, 45520, 45530, 45540, 45550, 45560, 45570,
    45580, 45590, 45600, 45610, 45620, 45630, 45640, 45650, 45660, 45670, 45680, 45690, 45700, 45710, 45720, 45730, 45740, 45750, 45760, 45770, 45780,
    45790, 45800, 45810, 45820, 45830, 45840, 45850, 45860, 45870, 45880, 45890, 45900, 45910, 45920, 45930, 45940, 45950, 45960, 45970, 45980, 45990,
    39180, 39270, 39280, 39290, 39470, 39480, 39490, 39790, 39800, 39810, 39820, 39830, 39840, 39850, 39860, 39870, 39880, 39890, 39910, 47810, 47820
]; // 익스트림 색변 헤어 목록입니다.

var specialHairJ = [
    // 일반 헤어
    39000, 39010, 39020, 39030, 39450, 39460, 39300, 39310, 39320, 39330, 39340, 39350, 39360, 39370, 39380, 39390, 39400, 39410, 39420, 39430, 39440,
]; // 진:眞 색변 헤어 목록입니다.

var specialHairR = [
    // 리터칭 헤어
    39500, 39510, 39520, 39530, 39540, 39550, 39560, 39570, 39580, 39590, 39600, 39610, 39620, 39630, 39640, 39650, 39660, 39670, 39680, 39690, 39700,
    39710, 39720, 39730, 39740, 39750, 39760, 39770, 39780, 39900
]; // 진:眞 리터칭 헤어 목록입니다.

var royalspecialHair = [
    // 로얄 스페셜 헤어
    39000, 39001, 39002, 39003, 39004, 39005, 39006, 39007, 39010, 39011, 39012, 39013, 39014, 39015, 39016, 39017, 39020, 39021, 39022, 39023, 39024, 39025, 39026, 39027, 39030, 39031, 39032, 39033, 39034, 39035, 39036, 39037, 39040, 39041, 39042, 39043, 39044, 39045, 39046, 39047, 39050, 39051, 39052, 39053, 39054, 39055, 39056, 39057, 39060, 39061, 39062, 39063, 39064, 39065, 39066, 39067, 39070, 39071, 39072, 39073, 39074, 39075, 39076, 39077, 39080, 39081, 39082, 39083, 39084, 39085, 39086, 39087, 39100, 39101, 39102, 39103, 39104, 39105, 39106, 39107, 39110, 39111, 39112, 39113, 39114, 39115, 39116, 39117, 39120, 39121, 39122, 39123, 39124, 39125, 39126, 39127, 39130, 39131, 39132, 39133, 39134, 39135, 39136, 39137, 39140, 39141, 39142, 39143, 39144, 39145, 39146, 39147, 39150, 39151, 39152, 39153, 39154, 39155, 39156, 39157, 39160, 39161, 39162, 39163, 39164, 39165, 39166, 39167, 39170, 39171, 39172, 39173, 39174, 39175, 39176, 39177, 39180, 39181, 39182, 39183, 39184, 39185, 39186, 39187,
    39190, 39191, 39192, 39193, 39194, 39195, 39196, 39197, 39200, 39201, 39202, 39203, 39204, 39205, 39206, 39207, 39210, 39211, 39212, 39213, 39214, 39215, 39216, 39217, 39500, 39501, 39502, 39503, 39504, 39505, 39506, 39507, 39510, 39511, 39512, 39513, 39514, 39515, 39516, 39517, 39520, 39521, 39522, 39523, 39524, 39525, 39526, 39527, 39530, 39531, 39532, 39533, 39534, 39535, 39536, 39537, 39540, 39541, 39542, 39543, 39544, 39545, 39546, 39547, 39550, 39551, 39552, 39553, 39554, 39555, 39556, 39557, 39560, 39561, 39562, 39563, 39564, 39565, 39566, 39567, 39570, 39571, 39572, 39573, 39574, 39575, 39576, 39577, 39580, 39581, 39582, 39583, 39584, 39585, 39586, 39587, 39590, 39591, 39592, 39593, 39594, 39595, 39596, 39597
]; // 강림월드 스페셜 헤어 목록

var royalcolorHair = [
    // 로얄 색변 헤어
    65000, 65010, 65020, 65030, 65040, 65050, 65060, 65070, 65080, 65090, 65100, 65110, 65120, 65130, 65140, 65150, 65160, 65170, 65180, 65190, 65200, 65210, 65220, 65230, 65240, 65250, 65260, 65270, 65280, 65290, 65300, 65310, 65320, 65330, 65340, 65350, 65360, 65370, 65380, 65390, 65400, 65410, 65420, 65430, 65440, 65450, 65460, 65470, 65480, 65490, 65500, 65510, 65520, 65530, 65540, 65550, 65560, 65570, 65580, 65590, 65600, 65610, 65620, 65630, 65640, 65650, 65660, 65670, 65680, 65690, 65700, 65710, 65720, 65730, 65740, 65750, 65760, 65770, 65780, 65790, 65800, 65810, 65820, 65830, 65840, 65850, 65860, 65870, 65880, 65890, 65900, 65910, 65920, 65930, 65940, 65950, 65960, 65970, 65980, 65990, 66000, 66010, 66020, 66030, 66040, 66050, 66060, 66070, 66080, 66090, 66100, 66110, 66120, 66130, 66140, 66150, 66160, 66170, 66180, 66190, 66200, 66210, 66220, 66230, 66240, 66250, 66260, 66270, 66280, 66290, 66300, 66310, 66320, 66330, 66340, 66350, 66360, 66370, 66380, 66390, 66400, 66410, 66420, 66430,
    66440, 66450, 66460, 66470, 66480, 66490, 66500, 66510, 66520, 66530, 66540, 66550, 66560, 66570, 66580, 66590, 66600, 66610, 66620, 66630, 66640, 66650, 66660, 66670, 66680, 66690, 66700, 66710, 66720, 66730, 66740, 66750, 66760, 66770, 66780, 66790, 66800, 66810, 66820, 66830, 66840, 66850, 66860, 66870, 66880, 66890, 66900, 66910, 66920, 66930, 66940, 66950, 66960, 66970, 66980, 66990, 67000, 67010, 67020, 67030, 67040, 67050, 67060, 67070, 67080, 67090, 67100, 67110, 67120, 67130, 67140, 67150, 67160, 67170, 67180, 67190, 67200, 67210, 67220, 67230, 67240, 67250, 67260, 67270, 67280, 67290, 67300, 67310, 67320, 67330, 67340, 67350, 67360, 67370, 67380, 67390, 67400, 67410, 67420, 67430, 67440, 67450, 67460, 67470, 67480, 67490, 67500, 67510, 67520, 67530, 67540, 67550, 67560, 67570, 67580, 67590, 67600, 67610, 67620, 67630, 67640, 67650, 67660, 67670, 67680, 67690, 67700, 67710, 67720, 67730, 67740, 67750, 67760, 67770, 67780, 67790, 67800, 67810, 67820, 67830, 67840, 67850, 67860, 67870,
    67880, 67890, 67900, 67910, 67920, 67930, 67940, 67950, 67960, 67970, 67980, 67990, 68000, 68010, 68020, 68030, 68040, 68050, 68060, 68070, 68080, 68090, 68100, 68110, 68120, 68130, 68140, 68150, 68160, 68170, 68180, 68190, 68200, 68210, 68220, 68230, 68240, 68250, 68260, 68270, 68280, 68290, 68300, 68310, 68320, 68330, 68340, 68350, 68360, 68370, 68380, 68390, 68400, 68410, 68420, 68430, 68440, 68450, 68460, 68470, 68480, 68490, 68500, 68510, 68520, 68530, 68540, 68550, 68560, 68570, 68580, 68590, 68600, 68610, 68620, 68630, 68640, 68650, 68660, 68670, 68680, 68690, 68700, 68710, 68720, 68730, 68740, 68750, 68760, 68770, 68780, 68790, 68800, 68810, 68820, 68830, 68840, 68850, 68860, 68870, 68880, 68890, 68900, 68910, 68920, 68930, 68940, 68950, 68960, 68970, 68980, 68990, 69000, 69010, 69020, 69030, 69040, 69050, 69060, 69070, 69080, 69090, 69100, 69110, 69120, 69130, 69140, 69150, 69160, 69170, 69180, 69190, 69200, 69210, 69220, 69230, 69240, 69250, 69260, 69270, 69280, 69290, 69300, 69310,
    69320, 69330, 69340, 69350, 69360, 69370, 69380, 69390, 69400, 69410, 69420, 69430, 69440, 69450, 69460, 69470, 69480
]; // 강림월드 색변 헤어 목록

var banMiaxHair = [
32770, 32780, 32790, 32800, 32810, 32820, 32830, 32840, 32850, 32860, 32870, 32880, 32890, 32900, 32910, 32920, 32930, 32940, 32950, 32960, 32970, 32980, 32990, 39000, 39001, 39002, 39003, 39004, 39005, 39006, 39007, 39010, 39011, 39012, 39013, 39014, 39015, 39016, 39017, 39020, 39021, 39022, 39023, 39024, 39025, 39026, 39027, 39030, 39031, 39032, 39033, 39034, 39035, 39036, 39037, 39100, 39110, 39120, 39130, 39140, 39150, 39160, 39170, 39180, 39270, 39280, 39290, 39300, 39301, 39302, 39303, 39304, 39305, 39306, 39307, 39310, 39311, 39312, 39313, 39314, 39315, 39316, 39317, 39320, 39321, 39322, 39323, 39324, 39325, 39326, 39327, 39330, 39331, 39332, 39333, 39334, 39335, 39336, 39337, 39340, 39341, 39342, 39343, 39344, 39345, 39346, 39347, 39350, 39351, 39352, 39353, 39354, 39355, 39356, 39357, 39360, 39361, 39362, 39363, 39364, 39365, 39366, 39367, 39370, 39371, 39372, 39373, 39374, 39375, 39376, 39377, 39380, 39381, 39382, 39383, 39384, 39385, 39386, 39387, 39390, 39391, 39392, 39393, 39394, 39395, 39396, 39397, 39400, 39401, 39402, 39403, 39404, 39405, 39406, 39407, 39410, 39411, 39412, 39413, 39414, 39415, 39416, 39417, 39420, 39421, 39422, 39423, 39424, 39425, 39426, 39427, 39430, 39431, 39432, 39433, 39434, 39435, 39436, 39437, 39440, 39441, 39442, 39443, 39444, 39445, 39446, 39447, 39450, 39451, 39452, 39453, 39454, 39455, 39456, 39457, 39460, 39461, 39462, 39463, 39464, 39465, 39466, 39467, 39470, 39480, 39490, 39500, 39501, 39502, 39503, 39504, 39505, 39506, 39507, 39510, 39511, 39512, 39513, 39514, 39515, 39516, 39517, 39520, 39521, 39522, 39523, 39524, 39525, 39526, 39527, 39530, 39531, 39532, 39533, 39534, 39535, 39536, 39537, 39540, 39541, 39542, 39543, 39544, 39545, 39546, 39547, 39550, 39551, 39552, 39553, 39554, 39555, 39556, 39557, 39560, 39561, 39562, 39563, 39564, 39565, 39566, 39567, 39570, 39571, 39572, 39573, 39574, 39575, 39576, 39577, 39580, 39581, 39582, 39583, 39584, 39585, 39586, 39587, 39590, 39591, 39592, 39593, 39594, 39595, 39596, 39597, 39600, 39601, 39602, 39603, 39604, 39605, 39606, 39607, 39610, 39611, 39612, 39613, 39614, 39615, 39616, 39617, 39620, 39621, 39622, 39623, 39624, 39625, 39626, 39627, 39630, 39631, 39632, 39633, 39634, 39635, 39636, 39637, 39640, 39641, 39642, 39643, 39644, 39645, 39646, 39647, 39650, 39651, 39652, 39653, 39654, 39655, 39656, 39657, 39660, 39661, 39662, 39663, 39664, 39665, 39666, 39667, 39670, 39671, 39672, 39673, 39674, 39675, 39676, 39677, 39680, 39681, 39682, 39683, 39684, 39685, 39686, 39687, 39690, 39691, 39692, 39693, 39694, 39695, 39696, 39697, 39700, 39701, 39702, 39703, 39704, 39705, 39706, 39707, 39710, 39711, 39712, 39713, 39714, 39715, 39716, 39717, 39720, 39721, 39722, 39723, 39724, 39725, 39726, 39727, 39730, 39731, 39732, 39733, 39734, 39735, 39736, 39737, 39740, 39741, 39742, 39743, 39744, 39745, 39746, 39747, 39750, 39751, 39752, 39753, 39754, 39755, 39756, 39757, 39760, 39761, 39762, 39763, 39764, 39765, 39766, 39767, 39770, 39771, 39772, 39773, 39774, 39775, 39776, 39777, 39780, 39781, 39782, 39783, 39784, 39785, 39786, 39787, 39790, 39800, 39810, 39820, 39830, 39840, 39850, 39860, 39870, 39880, 39890, 39900, 39901, 39902, 39903, 39904, 39905, 39906, 39907, 39910, 41160, 44950, 44951, 45240, 45250, 45260, 45270, 45280, 45290, 45300, 45310, 45320, 45330, 45340, 45350, 45360, 45370, 45380, 45390, 45400, 45410, 45420, 45430, 45440, 45450, 45460, 45470, 45480, 45490, 45500, 45510, 45520, 45530, 45540, 45550, 45560, 45570, 45580, 45590, 45600, 45610, 45620, 45630, 45640, 45650, 45660, 45670, 45680, 45690, 45700, 45710, 45720, 45730, 45740, 45750, 45760, 45770, 45780, 45790, 45800, 45810, 45820, 45830, 45840, 45850, 45860, 45870, 45880, 45890, 45900, 45910, 45920, 45930, 45940, 45950, 45960, 45970, 45980, 45990, 47600, 47610, 47620, 47630, 47640, 47650, 47660, 47670, 47680, 47690, 47700, 47710, 47720, 47730, 47740, 47750, 47760, 47770, 47780, 47790, 47800, 47810, 47820, 48370, 30010, 30070, 30080, 30090, 38920
];

var zero = false;
var skinList = Array(0, 1, 2, 3, 4, 9, 10, 11, 12, 13, 15, 16, 18, 19, 24, 25, 26, 27);

var maleHair = [];
var femaleHair = [];
var maleFace = [];
var femaleFace = [];
var hairColor = [];
var lens = [];

var specialHair = [];

var hairList = []; // 최종 선택 헤어 리스트
var subHairList = []; // 최종 선택 헤어 리스트 (제로 베타)
var faceList = []; // 최종 선택 얼굴 리스트
var subFaceList = []; // 최종 선택 얼굴 리스트 (제로 베타)

var checkAngelicBuster = false;
var dressUp = 0;

function start() {
    status = -1;
    sel = -1;
    sel2 = -1;
    type = -1;
    gar = -1;
    page = -1;
    subPage = -1;
    hairList = [];
    faceList = [];
    maleHairCount = 0;
    femaleHairCount = 0;
    dressUp = 0;
    checkAngelicBuster = false;

    action(1, 0, 0);
}

function action(mode, t, selection) {
    action(mode, t, selection, 0);
}

function action(mode, t, selection, dressUp_) {
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

        if (status == 0) {
            var v0 = "#fs12##e#r< 캐릭터 컬러변경 >#n#fs11#\r\n#b#L0#헤어 염색#l\r\n#L1#피부, 렌즈 변경#l\r\n\r\n\r\n#fs12##e#r< 안드로이드 컬러변경 >#n#b#fs11#\r\n#L2#안드로이드 염색, 피부, 렌즈#l";
            cm.askMenu(v0, GameObjectType.User, ScriptMessageFlag.Self);
        } else if (status == 1) {
            if (GameConstants.isZero(cm.getPlayer().getJob())) {
                zero = true;
            }
            sel = selection;
            if (sel == 0) { // 헤어 관련
                var v0 = "#fs11#                             #e<헤어 관련>#n\r\n#b";
                if (zero) {
                    v0 += "- 헤어 스타일 변경을 제외한 나머지 기능은 현재 제로의 태그 상태에 따라갑니다. (현재 : #e#r" + (cm.getPlayer().getZeroInfo().isBeta() ? "베타(여자)" : "알파(남자)") + "#b#n)\r\n\r\n";
                }
                v0 += "#L1#헤어 염색#l\r\n";
                v0 += "#L2#믹스 염색#l\r\n";
                cm.askMenu(v0, GameObjectType.User, ScriptMessageFlag.Self);
            } else if (sel == 1) { // 성형 관련
                var v0 = "#fs11#                             #e<성형 관련>#n\r\n#b";
                if (zero) {
                    v0 += "- 아래 기능은 현재 제로의 태그 상태에 따라갑니다. (현재 : #e#r" + (cm.getPlayer().getZeroInfo().isBeta() ? "베타(여자)" : "알파(남자)") + "#b#n)\r\n\r\n";
                }
                v0 += "#L1#피부 변경#l\r\n";
                v0 += "#L2#렌즈 변경#l\r\n";
                //v0 += "#L3#믹스 렌즈를 사용해보자.#l\r\n";
                cm.askMenu(v0, GameObjectType.User, ScriptMessageFlag.Self);
            } else if (sel == 2) { // 안드로이드
                if (cm.getPlayer().getAndroid() == null) {
                    cm.sayNpc("보유중인 안드로이드가 없습니다.", GameObjectType.User, false, false, ScriptMessageFlag.Self);
                    cm.dispose();
                    return;
                }
                var v0 = "#fs11#                             #e<안드로이드>#n\r\n#b";
                //v0 += "#L0#헤어 스타일 변경을 해보자.#l\r\n";
                v0 += "#L1#헤어 염색#l\r\n";
                //v0 += "#L2#성형 변경을 해보자.#l\r\n";
                v0 += "#L3#피부 변경#l\r\n";
                v0 += "#L4#렌즈 변경#l\r\n";
                cm.askMenu(v0, GameObjectType.User, ScriptMessageFlag.Self);
            } else if (sel == 5) {
                cm.askAngelicBuster();
            }
        } else if (status == 2) {
            sel2 = selection;
            if (sel == 0) {
                if (sel2 == 0) { // 헤어 스타일 변경
                    generateHairList();
                    if (zero) {
                        var count = maleHair.length; // 제로 알파
                        var v0 = "                     #e<헤어 스타일 변경>#n\r\n\r\n어떤 페이지를 볼까요? #e#b(알파 헤어[남자 헤어])#k#n #e(총 " + count + " 페이지)#n\r\n#b";
                        for (var i = 0; i < count; ++i) {
                            v0 += "#L" + i + "#" + (i + 1) + "페이지#n 헤어 스타일#l\r\n";
                        }
                        cm.askMenu(v0, GameObjectType.User, ScriptMessageFlag.Self);
                    } else {
                        var count = cm.getPlayer().getGender() == 0 ? maleHair.length : femaleHair.length;
                        var v0 = "                     #e<헤어 스타일 변경>#n\r\n\r\n어떤 페이지를 볼까요? #e(총 " + count + " 페이지)#n\r\n#b";
                        for (var i = 0; i < count; ++i) {
                            v0 += "#L" + i + "#" + (i + 1) + "페이지#n 헤어 스타일#l\r\n";
                        }
                        cm.askMenu(v0, GameObjectType.User, ScriptMessageFlag.Self);
                    }
                } else if (sel2 == 1) { // 헤어 염색
                    // 스페셜 헤어는 해당 기능을 사용할 수 없음
                    if (isSpecialHair(cm.getPlayer().getHair())) {
                         cm.sayReplacedNpc("#fs11#스페셜 헤어는 해당 기능을 이용할 수 없습니다.\r\n일반 헤어로 변경 후 시도해주세요.", false, false, 1, 9062000);
                         cm.dispose();
                         return;
                     }
                    var hair = dressUp == 1 ? cm.getPlayer().getSecondHair() : cm.getPlayer().getHair();
                    if (zero) {
                        if (cm.getPlayer().getZeroInfo().isBeta()) {
                            hair = cm.getPlayer().getZeroInfo().getSubHair();
                        }
                    }
                    generateHairColor(hair);
                    cm.sendHairColorChange(dressUp, hairColor);
                    cm.dispose();
                    return;
                    //cm.sendStyle("#b#h0##k님, #e강림월드#n에서는 헤어 염색을 무료로 해드리고 있습니다.\r\n마음에 드시는 색상을 선택해보세요", dressUp, hairColor);
                } else if (sel2 == 2) { // 믹스 염색
                    // 스페셜 헤어는 해당 기능을 사용할 수 없음
                        cm.sayReplacedNpc("#fs11#치장상점에서 커스텀 믹스염색 쿠폰을 구매하여 이용해주세요", false, false, 1, 1052206);
                        cm.dispose();
                        return;

                    var hair = dressUp == 1 ? cm.getPlayer().getSecondHair() : cm.getPlayer().getHair();
                    if (zero) {
                        if (cm.getPlayer().getZeroInfo().isBeta()) {
                            hair = cm.getPlayer().getZeroInfo().getSubHair();
                        }
                    }
                    if (isSpecialHair(hair)) {
                        cm.sayReplacedNpc("#fs11#스페셜 헤어는 해당 기능을 이용할 수 없습니다. 일반 헤어로 변경 후 시도해주세요.", false, false, 1, 9062000);
                        cm.dispose();
                        return;
                    }
                    var zeroBeta = zero && cm.getPlayer().getZeroInfo().isBeta();
                    cm.askCustomMixHairAndProb("2가지 색깔을 믹스해 머리색깔을 변경할 수 있어요. 베이스 컬러와 믹스 컬러를 선택하고 스크롤을 움직여 자신만의 색을 만들어 보세요.", dressUp, zeroBeta);
                }
            } else if (sel == 1) { // 성형 관련
                if (sel2 == 0) { // 얼굴 성형
                    generateFaceList();

                    var count = cm.getPlayer().getGender() == 0 ? maleFace.length : femaleFace.length;
                    var zeroBeta = zero && cm.getPlayer().getZeroInfo().isBeta();

                    if (zero) {
                        if (zeroBeta) {
                            count = femaleFace.length;
                        }
                    }

                    var v0 = "                     #e<얼굴 성형>#n\r\n\r\n어떤 페이지를 볼까요? #e(총 " + count + " 페이지)#n\r\n#b";
                    for (var i = 0; i < count; ++i) {
                        v0 += "#L" + i + "#" + (i + 1) + "페이지#n 얼굴 성형 스타일#l\r\n";
                    }
                    cm.askMenu(v0, GameObjectType.User, ScriptMessageFlag.Self);
                } else if (sel2 == 1) { // 피부 변경
                    cm.sendSkinColorChange(dressUp, skinList);
                    cm.dispose();
                    return;
                    //cm.askAvatar("#b#h0##k님, #e강림월드#n에서는 피부색 변경을 무료로 진행해드리고 있습니다.\r\n마음에 드시는 스타일을 선택해보세요.", dressUp, skinList);
                } else if (sel2 == 2) { // 렌즈 변경
                    var face = dressUp == 1 ? cm.getPlayer().getSecondFace() : cm.getPlayer().getFace();
                    var zeroBeta = zero && cm.getPlayer().getZeroInfo().isBeta();
                    if (zeroBeta) {
                        face = cm.getPlayer().getZeroInfo().getSubFace();
                    }
                    generateLensList(face);
                    cm.sendLensColorChange(dressUp, lens);
                    cm.dispose();
                    return;
                    //cm.askAvatar("#b#h0##k님, #e강림월드#n에서는 렌즈 변경을 무료로 변경해드리고 있습니다.\r\n마음에 드시는 렌즈를 선택해보세요.", dressUp, lens);
                } else if (sel2 == 3) { // 믹스 렌즈
                    if (!cm.canHold(5152300, 1)) {
                        cm.sayNpc("#b캐시 인벤토리#k 공간을 확보하고 다시 시도하자.", GameObjectType.User, false, false, ScriptMessageFlag.Self);
                        cm.dispose();
                        return;
                    }
                    if (cm.haveItem(5152300, 1)) {
                        cm.sayNpc("이미 #b#z5152300##k을 가지고 있는 것 같다.", GameObjectType.User, false, false, ScriptMessageFlag.Self);
                        cm.dispose();
                        return;
                    } else {
                        cm.gainItem(5152300, 1);
                        cm.sayNpc("#i5152300# #b#z5152300##k을 지급받았다. 사용해보자.", GameObjectType.User, false, false, ScriptMessageFlag.Self);
                        cm.dispose();
                        return;
                    }
                }
            } else if (sel == 2) { // 안드로이드
                sel2 = selection;
                if (sel2 == 0) { // 헤어 스타일
                    generateHairList();

                    var count = cm.getAndroidGender() == 0 ? maleHair.length : femaleHair.length;
                    var v0 = "                     #e<안드로이드 헤어 스타일 변경>#n\r\n\r\n어떤 페이지를 볼까요? #e(총 " + count + " 페이지)#n\r\n#b";
                    for (var i = 0; i < count; ++i) {
                        v0 += "#L" + i + "#" + (i + 1) + "페이지#n 헤어 스타일#l\r\n";
                    }
                    cm.askMenu(v0, GameObjectType.User, ScriptMessageFlag.Self);
                } else if (sel2 == 1) { // 염색	
                    generateHairColor(cm.getPlayer().getAndroid().getHair());
                    cm.sendAndroidHairColorChange(hairColor);
                    cm.dispose();
                    return;
                    //cm.askAvatarAndroid("#b#h0##k님, #e강림월드#n에서는 헤어 염색을 무료로 해드리고 있습니다.\r\n마음에 드시는 색상을 선택해보세요", hairColor);
                } else if (sel2 == 2) { // 성형
                    generateFaceList();
                    var count = cm.getAndroidGender() == 0 ? maleFace.length : femaleFace.length;
                    var v0 = "                     #e<안드로이드 성형>#n\r\n\r\n어떤 페이지를 볼까요? #e(총 " + count + " 페이지)#n\r\n#b";
                    for (var i = 0; i < count; ++i) {
                        v0 += "#L" + i + "#" + (i + 1) + "페이지#n 얼굴 성형 스타일#l\r\n";
                    }
                    cm.askMenu(v0, GameObjectType.User, ScriptMessageFlag.Self);
                } else if (sel2 == 3) { // 피부 변경
                    cm.sendAndroidSkinColorChange(skinList);
                    cm.dispose();
                    return;
                    //cm.askAvatarAndroid("#b#h0##k님, #e강림월드#n에서는 피부색 변경을 무료로 진행해드리고 있습니다.\r\n마음에 드시는 스타일을 선택해보세요.", skinList);
                } else if (sel2 == 4) { // 렌즈 변경
                    generateLensList(cm.getPlayer().getAndroid().getFace());
                    cm.sendAndroidLensColorChange(lens);
                    cm.dispose();
                    return;
                    //cm.askAvatarAndroid("#b#h0##k님, #e강림월드#n에서는 렌즈 변경을 무료로 변경해드리고 있습니다.\r\n마음에 드시는 렌즈를 선택해보세요.", lens);
                } else if (sel2 == 5) { // 스페셜 코디
                    var v0 = "                     #e<안드로이드 스페셜 코디>#n\r\n#b";
                    v0 += "#L0#색변 헤어 (Extreme) 스타일 변경#l\r\n";
                    v0 += "#L1#색변 헤어 (Jin) 스타일 변경#l\r\n";
                    v0 += "#L2#리터칭 헤어 (Jin) 스타일 변경#l\r\n";
                    cm.askMenu(v0, GameObjectType.User, ScriptMessageFlag.Self);
                }
            } else if (sel == 3) { // 성별 전환
                if (cm.getPlayer().getGender() == 0) {
                    cm.setHair(37300);
                    cm.setFace(21078);
                    cm.setSkin(3);
                    cm.getPlayer().setGender(1);
                    cm.getPlayer().fakeRelog();
                    cm.getPlayer().dropMessage(5, "성별 전환이 완료되었습니다.");
                    cm.dispose();
                } else {
                    cm.setHair(35290);
                    cm.setFace(20047);
                    cm.setSkin(3);
                    cm.getPlayer().setGender(0);
                    cm.getPlayer().fakeRelog();
                    cm.getPlayer().dropMessage(5, "성별 전환이 완료되었습니다.");
                    cm.dispose();
                }
            } else if (sel == 4) { // 스페셜 코디
                var title = "색변 헤어 [Extreme]";
                if (sel2 == 0) {
                    generateSpecialHair(specialHairE);
                } else if (sel2 == 1) {
                    generateSpecialHair(specialHairJ);
                    title = "색변 헤어 [Jin]";
                } else if (sel2 == 2) {
                    generateSpecialHair(specialHairR);
                    title = "리터칭 헤어";
                }
                var count = specialHair.length;
                var v0 = "                     #e<" + title + ">#n\r\n\r\n어떤 페이지를 볼까요? #e(총 " + count + " 페이지)#n\r\n#b";
                for (var i = 0; i < count; ++i) {
                    v0 += "#L" + i + "#" + (i + 1) + "페이지 헤어 스타일#l\r\n";
                }
                cm.askMenu(v0, GameObjectType.User, ScriptMessageFlag.Self);
            }
        } else if (status == 3) {
            if (sel == 0) { // 헤어 관련
                if (sel2 == 0) { // 헤어 스타일 변경
                    page = selection;
                    if (zero) {
                        listingZeroHairs(cm.getPlayer(), false);
                        var count = femaleHair.length; // 제로 베타
                        var v0 = "                     #e<헤어 스타일 변경>#n\r\n\r\n어떤 페이지를 볼까요? #e#b(베타 헤어[여자 헤어])#k#n #e(총 " + count + " 페이지)#n\r\n#b";
                        for (var i = 0; i < count; ++i) {
                            v0 += "#L" + i + "#" + (i + 1) + "페이지#n 헤어 스타일#l\r\n";
                        }
                        cm.askMenu(v0, GameObjectType.User, ScriptMessageFlag.Self);
                    } else {
                        var hair = dressUp == 1 ? cm.getPlayer().getSecondHair() : cm.getPlayer().getHair();
                        listingHairs(hair, cm.getPlayer().getGender());
                        cm.askAvatar("#b#h0##k님, #e강림월드#n에서는 다음과 같은 헤어스타일은 무료로 변경해드리고 있습니다.\r\n마음에 드시는 스타일을 선택해보세요.", dressUp, hairList);
                    }
                } else if (sel2 == 1) { // 헤어 염색
                    if (zero) {
                        if (cm.getPlayer().getZeroInfo().isBeta()) {
                            cm.setZeroBetaAvatar(3994583, hairColor[selection]);
                        } else {
                            cm.setAvatar(3994583, hairColor[selection]);
                        }
                        cm.dispose();
                        return;
                    }
                    if (dressUp > 0) {
                        cm.getPlayer().setSecondHair(hairColor[selection]);
                        cm.getPlayer().fakeRelog();
                    } else {
                        cm.setAvatar(3994583, hairColor[selection]);
                    }
                    cm.dispose();
                }
            } else if (sel == 1) { // 성형 관련
                if (sel2 == 0) { // 얼굴 성형
                    page = selection;
                    var face = dressUp == 1 ? cm.getPlayer().getSecondFace() : cm.getPlayer().getFace();

                    var zeroBeta = zero && cm.getPlayer().getZeroInfo().isBeta();

                    if (zero) {
                        if (zeroBeta) {
                            face = cm.getPlayer().getZeroInfo().getSubFace();
                        }
                    }

                    listingFaces(face, zeroBeta ? 1 : cm.getPlayer().getGender());
                    cm.askAvatar("#b#h0##k님, #e강림월드#n에서는 다음과 같은 얼굴 성형은 무료로 진행해드리고 있습니다.\r\n마음에 드시는 스타일을 선택해보세요.", dressUp, faceList);
                } else if (sel2 == 1) { // 피부색 변경
                    var zeroBeta = zero && cm.getPlayer().getZeroInfo().isBeta();

                    if (zero) {
                        if (zeroBeta) {
                            cm.getPlayer().getZeroInfo().setSubSkin(skinList[selection]);
                            cm.getPlayer().fakeRelog();
                            cm.dispose();
                            return;
                        }
                    }
                    if (dressUp > 0) {
                        cm.getPlayer().setSecondSkinColor(skinList[selection]);
                        cm.getPlayer().fakeRelog();
                    } else {
                        cm.setAvatar(4055004, skinList[selection]);
                    }
                    cm.dispose();
                } else if (sel2 == 2) { // 렌즈 변경
                    var zeroBeta = zero && cm.getPlayer().getZeroInfo().isBeta();

                    if (zero) {
                        if (zeroBeta) {
                            cm.getPlayer().getZeroInfo().setSubFace(lens[selection]);
                            cm.getPlayer().fakeRelog();
                            cm.dispose();
                            return;
                        }
                    }

                    if (dressUp > 0) {
                        cm.getPlayer().setSecondFace(lens[selection]);
                        cm.getPlayer().fakeRelog();
                    } else {
                        cm.setAvatar(5152111, lens[selection]);
                    }
                    cm.dispose();
                }
            } else if (sel == 2) { // 안드로이드
                if (sel2 == 0) { // 헤어 스타일 변경
                    page = selection;
                    listingHairs(cm.getPlayer().getAndroid().getHair(), cm.getAndroidGender());

                    cm.askAvatarAndroid("#b#h0##k님, #e강림월드#n에서는 다음과 같은 헤어스타일은 무료로 변경해드리고 있습니다.\r\n마음에 드시는 스타일을 선택해보세요.", hairList);
                } else if (sel2 == 1) { // 헤어 염색
                    cm.setHairAndroid(hairColor[selection]);
                } else if (sel2 == 2) { // 얼굴 성형
                    page = selection;
                    listingFaces(cm.getPlayer().getAndroid().getFace(), cm.getAndroidGender());
                    cm.askAvatarAndroid("#b#h0##k님, #e강림월드#n에서는 다음과 같은 얼굴 성형은 무료로 변경해드리고 있습니다.\r\n마음에 드시는 스타일을 선택해보세요.", faceList);
                } else if (sel2 == 3) { // 피부색 변경
                    cm.setSkinAndroid(skinList[selection]);
                } else if (sel2 == 4) { // 렌즈 변경
                    cm.setFaceAndroid(lens[selection]);
                } else if (sel2 == 5) { // 스페셜 코디
                    var title = "색변 헤어 [Extreme]";
                    if (selection == 0) {
                        generateSpecialHair(specialHairE);
                    } else if (selection == 1) {
                        generateSpecialHair(specialHairJ);
                        title = "색변 헤어 [Jin]";
                    } else if (selection == 2) {
                        generateSpecialHair(specialHairR);
                        title = "리터칭 헤어";
                    }
                    var count = specialHair.length;
                    var v0 = "                     #e<" + title + ">#n\r\n\r\n어떤 페이지를 볼까요? #e(총 " + count + " 페이지)#n\r\n#b";
                    for (var i = 0; i < count; ++i) {
                        v0 += "#L" + i + "#" + (i + 1) + "페이지 헤어 스타일#l\r\n";
                    }
                    cm.askMenu(v0, GameObjectType.User, ScriptMessageFlag.Self);
                }
            } else if (sel == 4) { // 스페셜 코디
                page = selection;

                hairList = [];
                var idx = 0;
                for (var i = 0; i < specialHair[page].length; ++i) {
                    var hair = specialHair[page][i];
                    hairList[idx++] = hair;
                }
                if (dressUp > 0) {
                    if (cm.getPlayer().getSecondBaseColor() > -1) {
                        cm.getPlayer().setSecondBaseColor(-1);
                        cm.getPlayer().setSecondAddColor(0);
                        cm.getPlayer().setSecondBaseProb(0);
                        cm.getPlayer().dropMessage(5, "믹스 염색이 해제되었습니다. 다시 시도해주시기 바랍니다.");
                        cm.dispose();
                        return;
                    }
                } else {
                    if (zero) {
                        if (cm.getPlayer().getZeroInfo().getMixBaseHairColor() != -1) {
                            cm.getPlayer().getZeroInfo().setMixBaseHairColor(-1);
                            cm.getPlayer().getZeroInfo().setMixAddHairColor(0);
                            cm.getPlayer().getZeroInfo().setMixHairBaseProb(0);
                        }
                    }
                    if (cm.getPlayer().getBaseColor() > -1) {
                        cm.getPlayer().setBaseColor(-1);
                        cm.getPlayer().setAddColor(0);
                        cm.getPlayer().setBaseProb(0);
                        cm.getPlayer().dropMessage(5, "믹스 염색이 해제되었습니다. 다시 시도해주시기 바랍니다.");
                        cm.dispose();
                        return;
                    }
                }

                cm.askAvatar("#b#h0##k님, 마음에 드시는 스타일을 선택해보세요.\r\n스타일 변경에는 #b#i2436018# #z2436018# 1개#k가 필요합니다.", hairList);
            }
        } else if (status == 4) {
            selection = selection & 0xFF;
            if (sel == 0) { // 헤어 관련
                if (sel2 == 0) { // 헤어 스타일 변경
                    if (zero) {
                        subPage = selection;
                        listingZeroHairs(cm.getPlayer(), true);

                        cm.askAvatar("#b#h0##k님, #e강림월드#n에서는 다음과 같은 헤어스타일은 무료로 변경해드리고 있습니다.\r\n마음에 드시는 스타일을 선택해보세요.", hairList, subHairList);
                    } else {
                        if (dressUp > 0) {
                            cm.getPlayer().setSecondHair(hairList[selection]);
                            cm.getPlayer().fakeRelog();
                        } else {
                            cm.setAvatar(5150057, hairList[selection]);
                        }
                        cm.dispose();
                    }
                }
            } else if (sel == 1) { // 성형 관련
                if (sel2 == 0) { // 얼굴 성형
                    if (zero) {
                        if (cm.getPlayer().getZeroInfo().isBeta()) {
                            cm.getPlayer().getZeroInfo().setSubFace(faceList[selection]);
                            cm.getPlayer().getZeroInfo().sendUpdateZeroInfo(cm.getPlayer(), ZeroInfoFlag.SubFace);
                        } else {
                            cm.setAvatar(4055004, faceList[selection]);
                        }
                        cm.dispose();
                        return;
                    }
                    if (dressUp > 0) {
                        cm.getPlayer().setSecondFace(faceList[selection]);
                        cm.getPlayer().fakeRelog();
                    } else {
                        cm.setAvatar(4055004, faceList[selection]);
                    }
                    cm.dispose();
                }
            } else if (sel == 2) { // 안드로이드
                if (sel2 == 0) { // 헤어 스타일 변경
                    cm.setHairAndroid(hairList[selection]);
                } else if (sel2 == 2) { // 얼굴 성형
                    cm.setFaceAndroid(faceList[selection]);
                } else if (sel2 == 5) { // 스페셜 코디
                    page = selection;

                    hairList = [];
                    var idx = 0;
                    for (var i = 0; i < specialHair[page].length; ++i) {
                        var hair = specialHair[page][i];
                        hairList[idx++] = hair;
                    }
                    cm.askAvatarAndroid("#b#h0##k님, 마음에 드시는 스타일을 선택해보세요.\r\n스타일 변경에는 #b#i2436018# #z2436018# 1개#k가 필요합니다.", hairList);
                }
            } else if (sel == 4) { // 스페셜 코디
                if (!cm.haveItem(2436018, 1)) {
                    cm.sayNpc("#b#i2436018# #z2436018# 1개#k가 없는 것 같다.", GameObjectType.User, false, false, ScriptMessageFlag.Self);
                    return;
                }

                if (zero) {
                    if (cm.getPlayer().getZeroInfo().isBeta()) {
                        cm.setZeroBetaAvatar(2436018, hairList[selection]);
                        cm.getPlayer().fakeRelog();
                    } else {
                        cm.setAvatar(2436018, hairList[selection]);
                    }
                    cm.dispose();
                    return;
                }
                if (dressUp > 0) {
                    cm.getPlayer().setSecondHair(hairList[selection]);
                    cm.getPlayer().fakeRelog();
                } else {
                    cm.setAvatar(2436018, hairList[selection]);
                }
                cm.dispose();
            }
        } else if (status == 5) {
            if (sel == 2) { // 안드로이드
                if (sel2 == 5) { // 스페셜 코디

                    if (!cm.haveItem(2436018, 1)) {
                        cm.sayNpc("#b#i2436018# #z2436018# 1개#k가 없는 것 같다.", GameObjectType.User, false, false, ScriptMessageFlag.Self);
                        return;
                    }

                    cm.gainItem(2436018, -1);
                    cm.setHairAndroid(hairList[selection]);
                    cm.dispose();
                }
            } else if (sel == 0) {
                if (sel2 == 0) {
                    if (zero) {
                        cm.setZeroAvatar(5150057, hairList[selection], subHairList[dressUp_]);
                    }
                }
            }
        }
    }
}

function isSpecialHair(checkID) {
    var c = false;
    // 익스트림 색변 헤어 체크
    for (var i = 0; i < specialHairE.length; ++i) {
        if (checkID == specialHairE[i]) {
            c = true;
        }
    }

    // 진 색변 헤어 체크
    for (var i = 0; i < specialHairJ.length; ++i) {
        if (checkID == specialHairJ[i]) {
            c = true;
        }
    }

    // 리터칭 헤어 체크
    for (var i = 0; i < specialHairR.length; ++i) {
        if (checkID == specialHairR[i]) {
            c = true;
        }
    }

    // 로얄 스페셜 헤어 체크
    for (var i = 0; i < royalspecialHair.length; ++i) {
        if (checkID == royalspecialHair[i]) {
            c = true;
        }
    }

    // 로얄 색변 헤어 체크
    for (var i = 0; i < royalcolorHair.length; ++i) {
        if (checkID == royalcolorHair[i]) {
            c = true;
        }
    }

    // 믹염 하면 팅기는 애들
    for (var i = 0; i < banMiaxHair.length; ++i) {
        if (checkID == banMiaxHair[i]) {
            c = true;
        }
    }
    return c;
}

function generateHairList() {
    maleHairCount = 0;
    femaleHairCount = 0;
    for (var i = 3000; i < 6100; ++i) { // 최근 6만번대까지 생겨남
        if (i >= 5000 && i < 6000) { // 여긴 얼굴 영역대~
            continue;
        }
        var hairID = i * 10;
        if (cm.isExistFH(hairID)) { // 존재하는 머리인가?
            var check = true;

            // 1~7번까지 다 있는 헤어여야지 일반 헤어
            for (var s = 1; s <= 7; s++) {
                if (!cm.isExistFH(hairID + s)) {
                    check = false;
                }
            }
            check = !isSpecialHair(hairID);

            if (check) { // 일반 헤어 맞뉘?
                var hairIndex = parseInt(hairID / 1000);
                if (hairIndex == 30 || hairIndex == 33 || hairIndex == 35 || hairIndex == 36 || hairIndex == 40 || hairIndex == 43 || hairIndex == 45 || hairIndex == 46) {
                    // 남자 헤어
                    gar = Math.floor(maleHairCount / 127); // 127개 이상이면 팅깁니다~	
                    if (maleHairCount % 127 == 0) {
                        maleHair[gar] = [];
                    }
                    maleHairCount++;
                    maleHair[gar].push(hairID);
                } else {
                    // 여자 헤어
                    gar = Math.floor(femaleHairCount / 127); // 127개 이상이면 팅깁니다~
                    if (femaleHairCount % 127 == 0) {
                        femaleHair[gar] = [];
                    }
                    femaleHairCount++;
                    femaleHair[gar].push(hairID);
                }
            }
        }
    }
}

function generateSpecialHair(specialHairList) {
    specialHairCount = 0;
    for (var i = 0; i < specialHairList.length; ++i) {
        var hairID = specialHairList[i];
        if (cm.isExistFH(hairID)) {
            var hairIndex = parseInt(hairID / 1000);
            gar = Math.floor(specialHairCount / 127); // 127개 이상이면 팅깁니다~	
            if (specialHairCount % 127 == 0) {
                specialHair[gar] = [];
            }
            specialHairCount++;
            specialHair[gar].push(hairID);
        }
    }
}

function generateFaceList() {
    maleFaceCount = 0;
    femaleFaceCount = 0;
    // 20000번대
    for (var a = 0; a <= 8; ++a) {
        for (var i = 0; i < 99; ++i) {
            var faceID = 20000 + i + (a * 1000);
            var check = true;

            for (var s = 1; s <= 7; ++s) {
                // 서버에 없는 얼굴이네
                if (!cm.isExistFH(faceID + (s * 100))) {
                    check = false;
                }
            }

            if (check) {
                if (a == 0 || a == 3 || a == 5 || a == 7) {
                    // 남자 얼굴
                    gar = Math.floor(maleFaceCount / 127);
                    if (maleFaceCount % 127 == 0) {
                        maleFace[gar] = [];
                    }
                    maleFaceCount++;
                    maleFace[gar].push(faceID);
                } else {
                    gar = Math.floor(femaleFaceCount / 127);
                    if (femaleFaceCount % 127 == 0) {
                        femaleFace[gar] = [];
                    }
                    femaleFaceCount++;
                    femaleFace[gar].push(faceID);
                }
            }
        }
    }
    // 50000번대
    for (var a = 0; a <= 8; ++a) {
        for (var i = 0; i < 99; ++i) {
            var faceID = 50000 + i + (a * 1000);
            var check = true;

            for (var s = 1; s <= 7; ++s) {
                // 서버에 없는 얼굴이네
                if (!cm.isExistFH(faceID + (s * 100))) {
                    check = false;
                }
            }

            if (check) {
                if (a == 0 || a == 3 || a == 5 || a == 7) {
                    // 남자 얼굴
                    gar = Math.floor(maleFaceCount / 127);
                    if (maleFaceCount % 127 == 0) {
                        maleFace[gar] = [];
                    }
                    maleFaceCount++;
                    maleFace[gar].push(faceID);
                } else {
                    gar = Math.floor(femaleFaceCount / 127);
                    if (femaleFaceCount % 127 == 0) {
                        femaleFace[gar] = [];
                    }
                    femaleFaceCount++;
                    femaleFace[gar].push(faceID);
                }
            }
        }
    }
}

function generateLensList(currentFace) {
    var t = parseInt(currentFace / 10000);
    var current = currentFace % 100 + ((t * 10000) + ((currentFace % 10000) - (currentFace % 1000)));
    lens = Array(current, current + 100, current + 200, current + 300, current + 400, current + 500, current + 600, current + 700, current + 800);
    for (var i = 0; i < lens.length; i++) {
        if (!cm.isExistFH(lens[i])) {
            lens[i] = current;
        }
    }
}

function generateHairColor(hairID) {
    hairColor = [];
    var idx = 0;
    var current = parseInt(Math.floor(hairID / 10)) * 10;
    for (var i = 0; i < 8; i++) {
        if (cm.isExistFH(current + i)) {
            hairColor[idx++] = current + i;
        }
    }
}

function listingZeroHairs(player, isBeta) {
    if (isBeta) {
        subHairList = [];
    } else {
        hairList = [];
    }
    var currentHair = !isBeta ? player.getHair() : player.getZeroInfo().getSubHair();

    var idx = 0;
    if (!isBeta) {
        for (var i = 0; i < maleHair[page].length; ++i) {
            var hair = maleHair[page][i] + parseInt(currentHair % 10);
            if (cm.isExistFH(hair)) {
                hairList[idx++] = hair;
            }
        }
    } else {
        for (var i = 0; i < femaleHair[subPage].length; ++i) {
            var hair = femaleHair[subPage][i] + parseInt(currentHair % 10);
            if (cm.isExistFH(hair)) {
                subHairList[idx++] = hair;
            }
        }
    }
}

function listingHairs(currentHairID, gender) {
    hairList = [];
    var idx = 0;
    if (gender == 0) {
        for (var i = 0; i < maleHair[page].length; ++i) {
            var hair = maleHair[page][i] + parseInt(currentHairID % 10);
            if (cm.isExistFH(hair)) {
                hairList[idx++] = hair;
            }
        }
    } else {
        for (var i = 0; i < femaleHair[page].length; ++i) {
            var hair = femaleHair[page][i] + parseInt(currentHairID % 10);
            if (cm.isExistFH(hair)) {
                hairList[idx++] = hair;
            }
        }
    }
}

function listingFaces(currentFace, gender) {
    faceList = [];
    var idx = 0;
    if (gender == 0) {
        for (var i = 0; i < maleFace[page].length; ++i) {
            var face = maleFace[page][i] + currentFace % 1000 - (currentFace % 100);
            if (cm.isExistFH(face)) {
                faceList[idx++] = face;
            }
        }
    } else {
        for (var i = 0; i < femaleFace[page].length; ++i) {
            var face = femaleFace[page][i] + currentFace % 1000 - (currentFace % 100);
            if (cm.isExistFH(face)) {
                faceList[idx++] = face;
            }
        }
    }
}