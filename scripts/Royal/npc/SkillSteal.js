/*
Royal SkillSteal Ver.366
*/

importPackage(Packages.constants);
importPackage(Packages.client);
importPackage(Packages.client.skills);
importPackage(Packages.provider);
importPackage(Packages.tools);
importPackage(java.lang);
importPackage(java.io);
importPackage(Packages.objects.users.skills.Packages.objects.users.skills.SkillFactory);
importPackage(Packages.objects);

var GameConstants = Packages.constants.GameConstants;

var enter = '\r\n';
var reset = '#l#k';
var IS_DEBUGGING = false;

var status = -1;
var selectedStealSkillSlot;
var selectedJobIndex;
var selectedSkillIndex;
var selectedSkills;
var chat
var jobList = [];

var Job = function (name, id) {
    this.name = name
    this.id = id;
}

var stealSkills = [];
stealSkills[0] = [
    1001005, //검사
    2001008, 2001002, //매지션
    3001004, //아처
    3011004, //패파
    4001334, 4001344, 4001003, // 로그
    4301004, //세미듀어러
    5001002, 5001003, //해적
    5011000, 5011001 //캐슈    
];

stealSkills[1] = [
    1101011, 1101006, //파이터
    1201013, 1201015, //페이지
    1301012, 1301007, //스피어맨
    2301005, 2301002, //클레릭
    2101004, 2101005, 2101001, 2101010, //불독
    2201008, 2201005, 2201001, //썬콜
    3101005, //헌터
    3201011, //사수
    3301003, //패파
    4101010, 4101013, //어쌔신
    4201012, //시프
    4311002, 4311003, //듀어러
    5201001, 5201018, //건슬링거
    5101012, //인파이터
    5301000, 5301001, 5301003 //캐논슈터
];

stealSkills[2] = [
    1111010, 1111012, //크루세이더
    1211010, 1211012, 1211013, 1211014, 1211011, 1211018, //나이트
    1311011, 1311012, 1311015, //버서커
    2311004, 2311011, 2311012, 2311002, 2311001, 2311003, 2311009, //프리스트
    2111002, 2111003, //불독
    2211002, 2211014, //썬콜
    3111013, //레인저
    3211011, //저격수
    3311012, //패파
    4111010, 4111015, //허밋
    4211011, 4211002, //시프마스터
    4321006, 4321002, //듀얼마스터
    4331000, 4331011, 4331006, //슬래셔
    5211008, 5211010, 5211007, //발키리
    5111009, 5111007, 5111002, //버커니어? 누꼬
    5311000, 5311010, 5311004, 5311005 //캐논블래스터
];

stealSkills[3] = [
    1121016, //히어로
    1221009, 1221014, 1221011, 1221016, //팔라딘
    1321014, 1321012, //다크나이트
    2321007, 2321008, 2321006, 2321005, //비숍
    2121006, 2121007, 2121011, //불독
    2221006, 2221011, 2221007, 2221012, //썬콜
    3121020, 3121015, 3121002, //보우마스터
    3221007, 3221014, 3221002, //신궁
    3321022, //패파
    4121013, 4121017, 4121016, 4121015, //나로
    4221014, 4221010, 4221017, //섀도어
    4341002, 4341011, 4341004, 4341009, //듀블
    5221004, 5221016, 5221015, 5221017, 5221013, 5221018, //캡틴
    5121007, 5121013, 5121015, 5121010, 5121009, 5121016, //바퍼
    5321000, 5321012, 5321001 //캐논마스터
];

stealSkills[4] = [
    1121054, //히어로
    1221054, //팔라딘
    1321054, //다크나이트
    2121054, //불독
    2221054, //썬콜   
    3121054, //보마 
    3221054, //신궁
    3321034, //패파 
    4121054, //나로
    5221054, //캡틴
    5121054, //바퍼
]


var jobList = [];


function start() {
    //if(!cm.getPlayer().isGM()) return;
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    } else {
        cm.dispose();
        return;
    }

    chat = '#fs11#'
    if (status == 0) {
        chat += '안녕하세요! 저는 로얄 메이플에서\r\n팬텀의 스틸스킬을 관리해드리고있습니다!\r\n\r\n#r※ 일부 스틸이 안되거나 장착이안되는 스킬들은 제보해주세요'
        cm.sendSimple(chat)
    } else if (status == 1) {
        if (GameConstants.isPhantom(cm.getPlayer().getJob())) {
            for (var i = 0; i < 5; i++) {
                chat += '#L' + i + '#';
                chat += getStealSkillSlotName(i);
                chat += enter;
            }

            cm.sendSimple(chat)
        } else {
            chat += '팬텀만 이용하실 수 있는 기능입니다';
            cm.dispose()
            cm.sendOk(chat)
        }

    } else if (status == 2) { //직업출력        
        if (selectedStealSkillSlot == null) {
            selectedStealSkillSlot = selection;
        }

        chat += '스틸을 원하시는 직업군을 골라주세요' + enter

        //stealSkills에서 해당되는 차수의 스킬을 모두가져와서 
        //skillId에서 jobId을 잘라내서 직업목록을 파싱하는방식
        //직업별로 다 따로하는거보다는 이게 좀더 편해서그럼
        //가져온 목록은 0~해당차수의 스킬 길이만큼 iteration을 돌려서 
        //selection을 만들고 seletedStealSkillSlot은 글로벌 변수니까 
        //굳이 forkedList같은걸 만들지않고 필요할때마다 가져오기로함
        for (var i = 0; i < stealSkills[selectedStealSkillSlot].length; i++) {
            var skillId = stealSkills[selectedStealSkillSlot][i];
            var jobId = Math.floor(skillId / 10000);
            if (jobList[jobList.length - 1] != jobId) {
                chat += '#L' + jobList.length + '#'
                chat += getJobNameById(jobId);
                chat += enter;

                jobList.push(jobId);
            }
        }
        cm.sendSimple(chat)
    } else if (status == 3) { //스킬목록출력
        if (selectedJobIndex == null) {
            selectedJobIndex = selection;
        }

        chat += '스틸을 원하시는 스킬을 골라주세요' + enter

        //selectedJobIndex는 한번만 쓰이는데 굳이해야할까? 했는데 언젠가 한번은 쓰겠지뭐
        //걍 셀렉션 한번 래핑하는겸에 일케하기로함        
        var selectedJobId = jobList[selectedJobIndex];
        for (var i = 0; i < stealSkills[selectedStealSkillSlot].length; i++) {
            var skillId = stealSkills[selectedStealSkillSlot][i];
            var jobId = Math.floor(skillId / 10000);
            if (jobId == selectedJobId) {
                chat += '#L' + i + '#';
                chat += '#s' + skillId + '#'; //스킬 이미지
                chat += Packages.objects.users.skills.SkillFactory.getSkillName(skillId); //스킬이름
                chat += enter;
            }
        }
        cm.sendSimple(chat);
    } else if (status == 4) { //스킬넣을 슬롯선택
        if (selectedSkillIndex == null) {
            selectedSkillIndex = selection;
        }
        chat += '해당 스킬을 넣을 칸을 골라주세요' + enter;
        var stolenSkillArray = getStoleanSkillArray();
        //selectedStealSkillSlot값을 업데이트하는 셀렉션은 0부터하는데
        //해당 메서드와 인덱스차이가 1나므로 이렇게함 다른데쓰이는걸 고치는거보단 이게나은듯
        //어레이 인덱스로도 쓰이니까 이게 나은듯

        numstealslot = GameConstants.getNumSteal(selectedStealSkillSlot + 1);

        for (var i = 0; i < numstealslot; i++) {
            var stolenSkill = stolenSkillArray[selectedStealSkillSlot][i]; //.left, .right를 갖고있는 일종의 튜플
            chat += '#L' + i + '#';
            chat += (i + 1) + '번째 칸: '
            if (stolenSkill != null) {
                chat += '#s' + stolenSkill.left + '#'; //이미지출력
                chat += Packages.objects.users.skills.SkillFactory.getSkillName(stolenSkill.left);

                if (stolenSkill.right) {
                    chat += '(장착중인 스킬)';
                }

            } else {
                chat += '비어있음';
            }
            chat += enter;

        }

        cm.sendSimple(chat)

    } else if (status == 5) {
        var skill = Packages.objects.users.skills.SkillFactory.getSkill(stealSkills[selectedStealSkillSlot][selectedSkillIndex]);
        var tupleSlotSkill = getStoleanSkillArray()[selectedStealSkillSlot][selection]
        var skillLevel = skill.getMaxLevel()

        if (tupleSlotSkill == null || !tupleSlotSkill.right) {
            var isreplaceSkill = tupleSlotSkill == null ? false : true;

            chat += getStealSkillSlotName(selectedStealSkillSlot)
            chat += ' ' + (selection + 1) + '번째 슬롯에 ' + enter

            //대사처리
            if (isreplaceSkill) {
                chat += '#s' + tupleSlotSkill.left + '#';
                chat += Packages.objects.users.skills.SkillFactory.getSkillName(tupleSlotSkill.left) + '이(가) ' + enter
            }

            chat += '#s' + skill.getId() + '#';
            chat += Packages.objects.users.skills.SkillFactory.getSkillName(skill.getId());
            chat += (isreplaceSkill ? '으(로) 변경되었습니다.' : '이(가) 추가되었습니다.');

            //스킬추가부분
            if (isreplaceSkill) {
                //cm.getPlayer().removeStolenSkill(tupleSlotSkill.left);
                cm.getPlayer().invokeJobMethod("removeStolenSkill", tupleSlotSkill.left);
            }
            cm.getPlayer().addStolenSkill(skill.getId(), skillLevel);
        } else {
            chat += '착용중인 스킬은 변경하실 수 없습니다.'
            cm.sendOk(chat)
        }
        //cm.sendOk(chat)
        cm.dispose();
    }

}

function getStoleanSkillArray() {
    var stolenSkills = cm.getPlayer().getStolenSkills();
    var stolenSkillIterator = stolenSkills.iterator();
    var stolenSkillArray = [];

    for (var i = 0; i < 5; i++) {
        stolenSkillArray[i] = [];
    }

    while (stolenSkillIterator.hasNext()) {
        var stolenSkill = stolenSkillIterator.next();
        var skillJobLevel = GameConstants.getJobNumber(stolenSkill.left) - 1;

        stolenSkillArray[skillJobLevel].push(stolenSkill);
    }
    return stolenSkillArray;
}

function getStealSkillSlotName(index) {
    var name = ''
    if (index == 4) {
        name += '하이퍼 스킬';
    } else {
        name += (index + 1) + '차 스틸스킬';
    }
    return name;
}

////////////

function getJobNameById(job) {
    switch (job) {
        case 0:
            return "초보자";
        case 100:
            return "검사";
        case 110:
            return "파이터";
        case 111:
            return "크루세이더";
        case 112:
            return "히어로";
        case 120:
            return "페이지";
        case 121:
            return "나이트";
        case 122:
            return "팔라딘";
        case 130:
            return "스피어맨";
        case 131:
            return "버서커";
        case 132:
            return "다크나이트";
        case 200:
            return "마법사";
        case 210:
            return "위자드(불,독)";
        case 211:
            return "메이지(불,독)";
        case 212:
            return "아크메이지(불,독)";
        case 220:
            return "위자드(썬,콜)";
        case 221:
            return "메이지(썬,콜)";
        case 222:
            return "아크메이지(썬,콜)";
        case 230:
            return "클레릭";
        case 231:
            return "프리스트";
        case 232:
            return "비숍";
        case 300:
            return "아처";
        case 310:
            return "헌터";
        case 311:
            return "레인저";
        case 312:
            return "보우마스터";
        case 320:
            return "사수";
        case 321:
            return "저격수";
        case 322:
            return "신궁";
        case 400:
            return "로그";
        case 410:
            return "어쌔신";
        case 411:
            return "허밋";
        case 412:
            return "나이트로드";
        case 420:
            return "시프";
        case 421:
            return "시프마스터";
        case 422:
            return "섀도어";
        case 430:
            return "세미듀어러";
        case 431:
            return "듀어러";
        case 432:
            return "듀얼마스터";
        case 433:
            return "슬래셔";
        case 434:
            return "듀얼블레이더";
        case 500:
            return "해적";
        case 510:
            return "인파이터";
        case 511:
            return "버커니어";
        case 512:
            return "바이퍼";
        case 520:
            return "건슬링거";
        case 521:
            return "발키리";
        case 522:
            return "캡틴";
        case 800:
            return "매니저";
        case 900:
            return "운영자";
        case 1000:
            return "노블레스";
        case 1100:
        case 1110:
        case 1111:
        case 1112:
            return "소울마스터";
        case 1200:
        case 1210:
        case 1211:
        case 1212:
            return "플레임위자드";
        case 1300:
        case 1310:
        case 1311:
        case 1312:
            return "윈드브레이커";
        case 1400:
        case 1410:
        case 1411:
        case 1412:
            return "나이트워커";
        case 1500:
        case 1510:
        case 1511:
        case 1512:
            return "스트라이커";
        case 2000:
            return "레전드";
        case 2100:
        case 2110:
        case 2111:
        case 2112:
            return "아란";
        case 2001:
        case 2200:
        case 2210:
        case 2211:
        case 2212:
        case 2213:
        case 2214:
        case 2215:
        case 2216:
        case 2217:
        case 2218:
            return "에반";
        case 3000:
            return "시티즌";
        case 3200:
        case 3210:
        case 3211:
        case 3212:
            return "배틀메이지";
        case 3300:
        case 3310:
        case 3311:
        case 3312:
            return "와일드헌터";
        case 3500:
        case 3510:
        case 3511:
        case 3512:
            return "메카닉";
        case 501:
            return "해적(캐논슈터)";
        case 530:
            return "캐논슈터";
        case 531:
            return "캐논블래스터";
        case 532:
            return "캐논마스터";
        case 2002:
        case 2300:
        case 2310:
        case 2311:
        case 2312:
            return "메르세데스";
        case 3001:
        case 3100:
        case 3110:
        case 3111:
        case 3112:
            return "데몬슬레이어";
        case 2003:
        case 2400:
        case 2410:
        case 2411:
        case 2412:
            return "팬텀";
        case 2004:
        case 2700:
        case 2710:
        case 2711:
        case 2712:
            return "루미너스";
        case 5000:
        case 5100:
        case 5110:
        case 5111:
        case 5112:
            return "미하일";
        case 6000:
        case 6100:
        case 6110:
        case 6111:
        case 6112:
            return "카이저";
        case 6001:
        case 6500:
        case 6510:
        case 6511:
        case 6512:
            return "엔젤릭버스터";
        case 3101:
        case 3120:
        case 3121:
        case 3122:
            return "데몬어벤져";
        case 3002:
        case 3600:
        case 3610:
        case 3611:
        case 3612:
            return "제논";
        case 10000:
            return "제로JR";
        case 10100:
            return "제로10100";
        case 10110:
            return "제로10110";
        case 10111:
            return "제로10111";
        case 10112:
            return "제로";
        case 2005:
            return "???";
        case 2500:
        case 2510:
        case 2511:
        case 2512:
            return "은월";
        case 14000:
        case 14200:
        case 14210:
        case 14211:
        case 14212:
            return "키네시스";
        case 15000:
        case 15200:
        case 15210:
        case 15211:
        case 15212:
            return "일리움";
        case 15001:
        case 15500:
        case 15510:
        case 15511:
        case 15512:
            return "아크";

        case 301:
        case 330:
        case 331:
        case 332:
            return "패스 파인더";
        case 16000:
        case 16400:
        case 16410:
        case 16411:
        case 16412:
            return "호영";

        case 15002:
        case 15100:
        case 15110:
        case 15111:
        case 15112:
            return "아델";

        default:
            return "알수없음";
    }
}