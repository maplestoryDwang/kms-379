importPackage(Packages.client.inventory);
importPackage(Packages.packet.creators);
importPackage(Packages.constants);
importPackage(Packages.client);
importPackage(Packages.server);
importPackage(java.lang);
importPackage(Packages.network.models);
importPackage(Packages.objects.item);

var status = -1;
var SYMBOL_IDS = [1712001, 1712002, 1712003, 1712004, 1712005, 1712006];
var available = [];
var chosenId = -1;
var paySel = -1;

별파 = "#fUI/GuildMark.img/Mark/Pattern/00004001/11#";
별노 = "#fUI/GuildMark.img/Mark/Pattern/00004001/3#";
별흰 = "#fUI/GuildMark.img/Mark/Pattern/00004001/15#";
별갈 = "#fUI/GuildMark.img/Mark/Pattern/00004001/5#";
별빨 = "#fUI/GuildMark.img/Mark/Pattern/00004001/1#";
별검 = "#fUI/GuildMark.img/Mark/Pattern/00004001/16#";
별보 = "#fUI/GuildMark.img/Mark/Pattern/00004001/13#";

var DONATION_COST = 30000;
var HPOINT_COST = 3000000;

var BONUS_STAT = 300;
var BONUS_WATK = 80;
var BONUS_MATK = 80;
var MAX_STAR = 10;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0) {
        cm.dispose();
        return;
    }
    status++;
    var chr = cm.getPlayer();

    switch (status) {
        case 0:
            available = [];
            for (var i = 0; i < SYMBOL_IDS.length; i++) {
                var id = SYMBOL_IDS[i];
                var it = chr.getInventory(MapleInventoryType.EQUIPPED).findById(id);
                if (it != null) {
                    var own = it.getOwner();
                    if (own == "" || own.match(/초월\s*[1-9]성/)) {
                        available.push(id);
                    }
                }
            }
            if (available.length == 0) {
                cm.sendOk("강화 가능한 아케인 심볼이 없습니다.\r\n(이미 초월 10성인 심볼만 남았습니다.)");
                cm.dispose();
                return;
            }
            var msg = "#fs11#     #fUI/Basic.img/Zenia/SC/0#\r\n" +
                "#Cgray##fs11#――――――――――――――――――――――――――――――――――――――――\r\n" +
                "#fc0xFFFF3366##e                 < 아케인 심볼 초월 강화 >#n\r\n" +
                "#fc0xFFFF3366##e       < 초월 1성당 : 올스탯 + 300, 공/마 + 80 >#n\r\n\r\n" +
                "#e#r      초월할 아케인 심볼을 선택하세요. (최대 10성)#k\r\n" +
                "#Cgray##fs11#――――――――――――――――――――――――――――――――――――#n\r\n";
            for (var i = 0; i < available.length; i++) {
                var id = available[i];
                var it = chr.getInventory(MapleInventoryType.EQUIPPED).findById(id);
                var own = it.getOwner() || "";
                var m = own.match(/초월\s*([1-9])성/);
                var curStar = m ? parseInt(m[1], 10) : 0;
                var starText = curStar > 0 ? "초월 " + curStar + "성" : "미초월";
                msg += "#L" + i + "##i" + id + "# #b#z" + id + "##k  #e#fc0xFFF781D8#(" + starText + ")#n#k#l\r\n";
            }
            cm.sendSimple(msg);
            break;

        case 1:
            chosenId = available[selection];
            var payMsg = "#fc0xFF000000##fs11#초월에 필요한 결제 수단을 선택하세요.\r\n\r\n" +
                "#fc0xFF000000##e#L0#" + 별파 + " 후원 포인트 #r-30,000P#k#l\r\n\r\n" +
                "#fc0xFF000000##L1#" + 별보 + " 홍보 포인트 #r-3,000,000P#k#l";
            cm.sendSimple(payMsg);
            break;

        case 2:
            paySel = selection;
            if (paySel == 0) {
                if (chr.getDonationPoint() < DONATION_COST) {
                    cm.sendOk("후원포인트가 부족합니다.");
                    cm.dispose();
                    return;
                }
                chr.gainDonationPoint(-DONATION_COST);
            } else {
                if (chr.getHPoint() < HPOINT_COST) {
                    cm.sendOk("홍보포인트가 부족합니다.");
                    cm.dispose();
                    return;
                }
                chr.gainHPoint(-HPOINT_COST);
            }
            var equip = chr.getInventory(MapleInventoryType.EQUIPPED).findById(chosenId);
            var own = equip.getOwner();
            var curStar = 0;
            var m = own.match(/초월\s*([1-9])성/);
            if (m) curStar = parseInt(m[1], 10);
            // 5성 이상 차단
            if (curStar >= MAX_STAR) {
                cm.sendOk("이미 초월 5성 심볼은 더 이상 강화할 수 없습니다.");
                cm.dispose();
                return;
            }
            var nextStar = curStar + 1;
            equip.setStr(java.lang.Short.valueOf(equip.getStr() + BONUS_STAT + ""));
            equip.setDex(java.lang.Short.valueOf(equip.getDex() + BONUS_STAT + ""));
            equip.setInt(java.lang.Short.valueOf(equip.getInt() + BONUS_STAT + ""));
            equip.setLuk(java.lang.Short.valueOf(equip.getLuk() + BONUS_STAT + ""));
            equip.setWatk(java.lang.Short.valueOf(equip.getWatk() + BONUS_WATK + ""));
            equip.setMatk(java.lang.Short.valueOf(equip.getMatk() + BONUS_MATK + ""));
            equip.setOwner("초월 " + nextStar + "성");
            chr.send(CWvsContext.InventoryPacket.updateEquipSlot(equip));
            cm.sendOk("#fs11#" +
                "#b#e초월 " + nextStar + "성 완료!#k\r\n" +
                "(올스탯 +" + BONUS_STAT + " / 공격력·마력 +" + BONUS_WATK + ")");
            cm.dispose();
            break;
    }
}
