/*일일 퀘스트
 소멸의 여로     기쁨의 에르다스 100마리,분노의 에르다스 100마리 잡기	기쁨-8641000 분노-8641001				
 츄츄아일랜드    크릴라 100마리,버샤크 100마리 잡기			크릴라-8642012 버샤크-8642014				
 레헬른          성난 무도회주민100마리,광기의 무도회주민 100마리 잡기		성난-8643008 광기-8643009				
 아르카나        비탄의 정령200마리,절망의 정령 200마리 잡기			비탄-8644009 절망-8644008				
 모라스          푸른 그림자 200마리,붉은 그림자 200마리 잡기			푸른-8644405 붉은-8644406				
 에스페라        아라냐 300마리,아라네아 300마리 잡기			아라냐-8644504 아라네아-8644505	*/

importPackage(java.lang);

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

var Time = new Date();
var Year = Time.getFullYear() + "";
var Month = Time.getMonth() + 1 + "";
var Date = Time.getDate() + "";
if (Month < 10) {
    Month = "0" + Month;
}
if (Date < 10) {
    Date = "0" + Date;
}
var Today = parseInt(Year + Month + Date);

var quest = [
    ["", [0, 0], [0, 0], [0, 0], [0, 0]],
    [["접속보상", "HotTime"], [0, 0], [0, 0]],
];

var reward = [[2630442, 1]];
var reward2 = [[2633336, 1]];
var choice = 0;

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }
    if (status == 0) {
        var say = "#fs11#" + 별 + 색 + " 받으실 보상을 선택해 주세요 " + 별 + 엔터;
        // for (var i = 1; i < quest.length; i++) {
        say += "#L0##e#b[접속보상]#n#k";
        // }
        cm.sendSimple(say);

    } else if (status == 1) {
        if (cm.getClient().getKeyValue("hotTimeComplete") != Today) {
            var say = "#fs11#<보상 목록>\r\n";
            for (var i = 0; i < reward.length; i++) {
                say += "#i" + reward[i][0] + "##z" + reward[i][0] + "# " + reward[i][1] + "개\r\n";
            }
            cm.sendYesNo("#fs11#" + 별 + 색 + " 정말 보상을 받으시겠습니까? " + 별 + 엔터 + 엔터 + say);
        } else {
            cm.sendOkS("#fs11#" + 색 + "#e맞다 아까 받은거같은데..?", 2);
            cm.dispose();
            return;
        }
    } else if (status == 2) {
        cm.dispose();
        for (var i = 0; i < reward.length; i++) {
            if (!cm.canHold(reward[i][0], reward[i][1])) {
                cm.sendOk("#fs11#" + 색 + "인벤토리 슬롯이 부족하거나 꽉 찬 것은 아닌지 확인해 주세요.");
                cm.dispose();
                return;
            }
        }

        for (var i = 0; i < reward.length; i++) {
            cm.gainItem(reward[i][0], reward[i][1]);
        }

        cm.getClient().setKeyValue("hotTimeComplete", Today);
        cm.sendOk("#fs11#" + 색 + "일일접속 보상이 지급되었습니다. 인벤토리를 확인해 주세요.");
    }
}