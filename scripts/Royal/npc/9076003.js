var 별 = "#fUI/GuildMark.img/Mark/Pattern/00004001/14#";

var sel = 0

var ringList = [{
        'item': 1112030,
        'price': 1
    },
    {
        'item': 1112031,
        'price': 1
    },
    {
        'item': 1112032,
        'price': 1
    },
    {
        'item': 1112033,
        'price': 1
    },
    {
        'item': 1112034,
        'price': 1
    },
    {
        'item': 1112035,
        'price': 1
    },
    {
        'item': 1112036,
        'price': 1
    },
    {

        'item': 1112037,
        'price': 1
    },
    {
        'item': 1112038,
        'price': 1
    },
    {
        'item': 1112039,
        'price': 1
    },
    {
        'item': 1112040,
        'price': 1
    },
    {
        'item': 1112041,
        'price': 1
    },
    {
        'item': 1112042,
        'price': 1
    },
    {
        'item': 1112043,
        'price': 1
    },
    {
        'item': 1112044,
        'price': 1
    },
    {
        'item': 1112045,
        'price': 1
    },
    {
        'item': 1112850,
        'price': 1
    },
    {
        'item': 1112851,
        'price': 1
    },
    {
        'item': 1112853,
        'price': 1
    },
    {
        'item': 1112854,
        'price': 1
    },
    {
        'item': 1112855,
        'price': 1
    },
    {
        'item': 1112856,
        'price': 1
    },
    {
        'item': 1112857,
        'price': 1
    },
    {
        'item': 1112858,
        'price': 1
    },
    {
        'item': 1112859,
        'price': 1
    },
    {
        'item': 1112860,
        'price': 1
    },
    {
        'item': 1112861,
        'price': 1
    },
    {
        'item': 1112862,
        'price': 1
    }
]

var coin = 4001679; // 필요한 아이템 코드

var status = -1;

function start() {
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
        var text = "                       #fs11##fc0xFF000000##fs17#" + 별 + " 로얄메이플 우정링 상점 " + 별 + "\r\n#fs12##Cgray#                반지를 선물하여 우정을 확인해보세요!#k\r\n\r\n#fs12#";
        text += "#fs12#선물하실 반지를 선택해주세요.\r\n같이 장착하게 될 상대방과 #b파티#k를 맺어야 합니다\r\n#r(주의: 선물 후 받은분과 보낸분 모두 채널 변경을 해야 적용됩니다.)#k\r\n#fs11##fc0xFF000000#커플링을 선물 하기 위해서 #i4001679#가 필요합니다#b\r\n";
        for (var i = 0; i < ringList.length; ++i) {
            var itemID = ringList[i]['item'];
            var price = ringList[i]['price'];
            text += "#L" + i + "##i" + itemID + "# #z" + itemID + "\r\n\r\n";
        }
        cm.sendSimple(text);
    } else if (status == 1) {
        if (!cm.haveItem(coin, 1)) {
            cm.sendNext("#z" + coin + "#가 부족합니다.");
            cm.dispose();
            return;
        }
        sel = selection;
        cm.sendGetText("#fs11##fc0xFF000000# 같이 끼게될 상대의 캐릭터 이름을 입력해주세요.");
    } else if (status == 2) {
        if (cm.getPlayer().getParty() == null) {
            cm.sendOk("#fs11##fc0xFF000000# 같이 끼게될 상대방과 파티를 맺고 시도해주시기 바랍니다.");
            cm.dispose();
            return;
        }
        if (!cm.allMembersHere()) {
            cm.sendOk("#fs11##fc0xFF000000# 상대방과 같은 곳에서 시도해주시기 바랍니다.");
            cm.dispose();
            return;
        }
        var target = cm.getText();
        var it = cm.getClient().getChannelServer().getPartyMembers(cm.getParty()).iterator();
        var chr = null;

        while (it.hasNext()) {
            var chr = it.next();
            if (chr.getName().equals(target)) {
                find = chr;
                break;
            }
        }

        if (chr == null) {
            cm.sendOk("#fs11##fc0xFF000000#받을 대상이 접속중이지 않습니다.");
            cm.dispose();
            return;
        }

        cm.makeRing(ringList[sel]['item'], target);
        cm.gainItem(coin, -ringList[sel]['price']);
        cm.sendNext("#fs11##fc0xFF000000#구매가 완료되었습니다 재접속 후 커플링이 적용됩니다.");
        chr.chatMsg(10, cm.getPlayer().getName() + "님으로 부터 커플링을 선물 받았습니다. 지금 바로 인벤토리를 확인해보세요!");
        chr.chatMsg(10, "재접속 후 커플링이 적용됩니다.");
        cm.dispose();
    }
}