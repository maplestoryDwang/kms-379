importPackage(Packages.tools.RandomStream);

var ii = Packages.objects.item.MapleItemInformationProvider.getInstance();
var MapleStat = Packages.client.MapleStat

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

var enter = '\r\n';
var reset = '#l#k';
var IS_DEBUGGING = false;
var MIN_SEARCHNAME_LENGTH = 2;
var status = -1;
var chat

var hairs = [];
var faces = [];
var searchedMolding = []

var firstSelection;

function start() {
    //if(!cm.getPlayer().isGM()) return;
    initializeMoldingData();

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

    chat = '#fs11#'

    if (status == 0) {
        selection = 0; // 0 헤어 1 성형
        firstSelection = 0; // 0 헤어 1 성형

        chat += '찾으실 ' + (selection == 0 ? '헤어' : '성형') + '이름을 입력해주세요.'
        cm.sendGetText(chat);
    } else if (status == 1) {
        var searchTarget = cm.getText();
        var isHairId = firstSelection == 0;

        if (searchTarget.length >= MIN_SEARCHNAME_LENGTH) {
            if (isHairId) { //헤어일 때
                chat += '#fs11##fn돋움##fc0xFFFFFFFF#지금의 헤어를 전혀 새로운 스타일로 바꿔 줄 수 있지. 지금 모습이 지겨워 졌다면 바꾸고 싶은 헤어를 천천히 고민해 봐'
                searchedMolding = searchHair(searchTarget)
            } else {
                chat += '#fs11##fn돋움##fc0xFFFFFFFF#지금의 얼굴을 전혀 새로운 스타일로 바꿔 줄 수 있지. 지금 모습이 지겨워 졌다면 바꾸고 싶은 얼굴을 천천히 고민해 봐'
                searchedMolding = searchFace(searchTarget)
            }

            if (searchedMolding.length > 0) {
                cm.askAvatarAndroid(chat, searchedMolding);
            } else {
                cm.sendOk('#fs11# 검색된 ' + (isHairId ? '헤어가' : '성형이') + ' 없습니다.');
                cm.dispose();
            }
        } else {
            chat += '글자수가 너무 짧습니다.' + enter;
            chat += '최소 ' + MIN_SEARCHNAME_LENGTH + '글자 이상 입력해주세요'
            cm.sendOk(chat)
        }

    } else if (status == 2) {
        if (selection >= 0) {
            setAvatar(searchedMolding[selection]);
        }
        resetUserInput();
        cm.dispose();
    }

}

function resetUserInput() {
    firstSelection = null;
    searchedMolding = [];
}

function searchHair(searchName) {
    var validHairIds = []
    for (var i = 0; i < hairs.length; i++) {
        var hair = hairs[i];
        var hairName = hair.right
        var hairId = hair.left
        if (hairId % 10 == 0) {
            if (hairName.indexOf(searchName) != -1) {
                validHairIds.push(hairId);
            } else if (hairName.indexOf(searchName[0]) != -1) {
                var index = 0;
                var isShortName = true;
                while (searchName[index] != null) {
                    var slicedSearchName = searchName[index];

                    if (hairName.indexOf(slicedSearchName) == -1) {
                        isShortName = false;
                        break;
                    }
                    index++;
                }
                if (isShortName) {
                    validHairIds.push(hairId);
                }
            }
        }

    }
    return validHairIds;
}

function searchFace(searchName) {
    var validFaceIds = []
    for (var i = 0; i < faces.length; i++) {
        var face = faces[i];
        var faceId = face.left;
        var faceName = face.right
        if (faceName.indexOf(searchName) != -1) {

            if (Math.floor(faceId / 100 % 10) == 0) {
                validFaceIds.push(faceId);
            }
        } else if (faceName.indexOf(searchName[0]) != -1) {
            var index = 0;
            var isShortName = true;
            while (searchName[index] != null) {
                var slicedSearchName = searchName[index];

                if (faceName.indexOf(slicedSearchName) == -1) {
                    isShortName = false;
                    break;
                }
                index++;
            }
            if (isShortName) {
                validFaceIds.push(faceId);
            }
        }
    }
    return validFaceIds;
}

function initializeMoldingData() {
    var it = ii.getAllEquips().iterator();
    while (it.hasNext()) {
        var avatar = it.next();
        var avatarId = avatar.getLeft()

        if (isHair(avatarId)) {
            hairs.push(avatar)
        } else if (isFace(avatarId)) {
            faces.push(avatar)
        }
    }
}

function setAvatar(args) {
    if (isHair(args)) {
        cm.setHairAndroid(args);
    } else if (isFace(args)) {
        cm.setFaceAndroid(args);
    }
    cm.getPlayer().equipChanged();
}

function isHair(itemId) {
    return Math.floor(itemId / 10000) == 3 || Math.floor(itemId / 10000) == 4 || Math.floor(itemId / 10000) == 6;
}

function isFace(itemId) {
    return Math.floor(itemId / 10000) == 2 || Math.floor(itemId / 10000) == 5;
}