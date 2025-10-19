var enter = "\r\n";
var seld = -1;
importPackage(Packages.server);
importPackage(Packages.scripting);
importPackage(Packages.objects.item);

하트 = "#fs11##fMap/MapHelper.img/minimap/match#"
파랑 = "#fc0xFF6B66FF#"
네모 = "#fMap/MapHelper.img/minimap/rune#"
검정 = "#fc0xFF191919#";
var sssss = 0;
var coin = 4033213;    // 재화
var price = 1;        // 가격
var allstat = 4, atk = 2;
var hoo = 50;          // 실패 확률 변수
var maxEnhance = 100;   // 최대 강화 레벨

function getAddEnhance(item) {
    var owner = item.getOwner();
    // 1~3자리 숫자까지 허용
    var m = owner.match(/^(\d{1,3})강$/);
    if (!m) return 0;
    var lvl = parseInt(m[1], 10);
    return (lvl >= 1 && lvl <= 100) ? lvl : 0;
}

function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == 1) {
        if (status == 1) {
            if (sssss == 1) {
                status++;
            }
        }
        status++;
        if (status == 4 && selection == 0 && sssss == 0) {
            status = 1;
        }
    } else {
        if (status == 2) {
            cm.sendOkS("그래, 알겠네 마음이 바뀌면 다시 찾아오게나.", 4, 9401232);
        }
        cm.dispose();
        return;
    }
    if (status == 0) {
        말 = "#fs11#" + 검정 + "안녕하세요 #b#h ##k님! \r\n";
        말 += "#fs11#" + 검정 + "혹시 캐시 아이템도 강화가 된다는 사실 알고 계신가요?  \r\n";
        말 += "#fs11#" + 검정 + "캐시 강화 시스템은 #i4033213##b#t4033213##d을 통해\r\n치장 아이템을";
        말 += "#fs11#" + 검정 + " 강화할 수 있는 시스템입니다. \r\n\r\n";
        말 += "#fs11#" + 검정 + "한번에 #r올스탯 4와 공격력,마력이 2#d씩 증가합니다. \r\n";
        말 += "#fs11#" + 검정 + "#b#z4033213##k 1개로 최대 100강까지 강화가 가능합니다.#r\r\n\r\n펫 장비를 비롯한 일부 아이템#d에는 사용할 수 없습니다.\r\n"
        말 += "#fc0xFFD5D5D5#─────────────────────────────#k\r\n";
        말 += "#L0##fc0xFF6B66FF#치장아이템#k" + 검정 + "을 강화하기 #r[강화확률 50%]#k\r\n"
        //말 += "#L1#설명을 듣고 싶어요."
        cm.sendSimple(말);
    } else if (status == 1) {
        sssss = selection;
        if (selection == 0) {
            var txt = "#fs11#" + 검정 + "어떤 아이템을 강화하실건가요?\r\n강화확률은 #b50%#k랍니다.\r\n";
            txt += "#fc0xFFD5D5D5#─────────────────────────────#k\r\n";
            for (i = 0; i < cm.getInventory(6).getSlotLimit(); i++) {
                if (cm.getInventory(6).getItem(i) != null) {
                    if (cm.isCash(cm.getInventory(6).getItem(i).getItemId())) {
                        txt += "#L" + i + "# #i" + cm.getInventory(6).getItem(i).getItemId() + "# #fc0xFF6B66FF##z" + cm.getInventory(6).getItem(i).getItemId() + "#\r\n";
                    }
                }
            }
            cm.sendSimple(txt);
        } else if (selection == 1) {
            cm.Entertuto(false, false);
        }
    } else if (status == 2) {
        items = selection;
        item = cm.getInventory(6).getItem(items);
        if (item == null) {
            cm.sendOk("오류가 발생했습니다. 재시도 부탁드립니다.");
            cm.dispose();
            return;
        }
        cm.sendNext("#fs11#정말 #r#i" + item.getItemId() + ":# #z" + item.getItemId() + ":##k을 강화 하시겠나요? 선택은 되돌릴 수 없으니 신중하게 선택하여 주세요.");
    } else if (status == 3) {
        if (sssss == 1) {
            cm.sendScreenText("#fs27##fn나눔고딕 Extrabold##fc0xFF6B66FF#치장아이템 강화#k에 대해 설명 드리겠습니다.", false);
        } else {
            if (!cm.haveItem(coin, price)) {
                cm.sendOk("#fs11#" + 검정 + "강화 재료가 부족해요! #i4033213##r#t4033213# 1개#d가 필요합니다.");
                cm.dispose();
                return;
            }

            if (item == null) {
                cm.dispose();
                return;
            }
            if (item.getItemId() >= 1112000 && item.getItemId() <= 1112099 || item.getItemId() >= 1112800 && item.getItemId() <= 1112899) {
                cm.sendOk("#fs11#" + 검정 + "커플링과 우정링은 강화가 불가능해요.");
                cm.dispose();
                return;
            }
            var cur = getAddEnhance(item);
            if (cur >= maxEnhance) {
                cm.sendOk("#fs11#더 이상 강화는 불가능해요! 한 아이템에 최대 #r"
                    + maxEnhance + "강#d 까지만 강화할 수 있습니다.");
                cm.dispose();
                return;
            }
            if (item.getItemId() >= 1802000 && item.getItemId() <= 1802999) {
                cm.sendOk("#fs11#" + 검정 + "펫 장비는 강화가 불가능해요.");
                cm.dispose();
                return;
            }
            if (!cm.isCash(item.getItemId())) {
                cm.sendOk("#fs11#" + 검정 + "비정상적인 아이템인것 같습니다.");
                cm.dispose();
                return;
            }

            var suc = (Math.random() * 100) < (100 - hoo);

            if (!suc) {
                item.setStr(item.getStr() - 0);
                item.setDex(item.getDex() - 0);
                item.setInt(item.getInt() - 0);
                item.setLuk(item.getLuk() - 0);
                item.setWatk(item.getWatk() - 0);
                item.setMatk(item.getMatk() - 0);
                item.setHp(item.getHp() - 0);
                cm.gainItem(coin, -price);
                cm.getPlayer().send(Packages.network.models.CWvsContext.InventoryPacket.updateInventoryItem(Packages.objects.item.MapleInventoryType.CASH_EQUIP, item, false, cm.getPlayer()));
                말 = "#fs11#" + 검정 + "강화에 실패하였습니다.\r\n"
                말 += "#fc0xFFD5D5D5#───────────────────────────#k#l\r\n";
                말 += "#i" + item.getItemId() + "# #fc0xFF4641D9##z" + item.getItemId() + "#\r\n";
                말 += "#fc0xFF6799FF#STR : + " + item.getStr() + "\r\n";
                말 += "DEX : + " + item.getDex() + "\r\n";
                말 += "INT : + " + item.getInt() + "\r\n";
                말 += "LUK : + " + item.getLuk() + "\r\n";
                말 += "MAXHP : + " + item.getHp() + "\r\n";
                말 += "공격력 : + " + item.getWatk() + "\r\n";
                말 += "마력   : + " + item.getMatk() + "\r\n";
                말 += "#L0##b한번 더 강화하겠습니다.\r\n"
                말 += "#L1##r더이상 강화를 하지 않겠습니다.\r\n"
                cm.sendSimple(말);
            } else {
                item.setStr(item.getStr() + allstat);
                item.setDex(item.getDex() + allstat);
                item.setInt(item.getInt() + allstat);
                item.setLuk(item.getLuk() + allstat);
                item.setWatk(item.getWatk() + atk);
                item.setMatk(item.getMatk() + atk);
                item.setHp(item.getHp() + 10);
                item.setOwner("" + (getAddEnhance(item) + 1) + "강");
                cm.gainItem(coin, -price);
                cm.getPlayer().send(Packages.network.models.CWvsContext.InventoryPacket.updateInventoryItem(Packages.objects.item.MapleInventoryType.CASH_EQUIP, item, false, cm.getPlayer()));
                말 = "#fs11#" + 검정 + "#b강화가 성공하였습니다!#k 강화하신 아이템의 옵션입니다.\r\n";
                말 += "#fc0xFFD5D5D5#───────────────────────────#k#l\r\n";
                말 += "#i" + item.getItemId() + "# #fc0xFF4641D9##z" + item.getItemId() + "#\r\n";
                말 += "#fc0xFF6799FF#STR : + " + item.getStr() + "\r\n";
                말 += "DEX : + " + item.getDex() + "\r\n";
                말 += "INT : + " + item.getInt() + "\r\n";
                말 += "LUK : + " + item.getLuk() + "\r\n";
                말 += "MAXHP : + " + item.getHp() + "\r\n";
                말 += "공격력 : + " + item.getWatk() + "\r\n";
                말 += "마력   : + " + item.getMatk() + "\r\n";
                말 += "#L0##b한번 더 강화하겠습니다.\r\n";
                말 += "#L1##r더이상 강화를 하지 않겠습니다.\r\n";
                cm.sendSimple(말);
            }
        }
    } else if (status == 4) { //여긴 다 설명인데 쓸거면 다 바꿔야함
        if (sssss == 1) {
            cm.sendScreenText("#fs18#코디 아이템이라면 어떤 아이템이든 가능합니다.　　　　　　　　　　　 　#fs15##r단, 커플링, 우정링, 펫 장비는 강화가 불가능합니다.#k", false);
        } else {
            if (selection == 0) {
            } else {
                cm.dispose();
            }
        }
    } else if (status == 5) {
        cm.sendScreenText("#fs18#강화마다 #i4310012# #fc0xFFFFBB00##z4310012##k #r30개#k가 필요하며,", false);
    } else if (status == 6) {
        cm.sendScreenText("최대 스탯은 #fc0xFFCCA63D#'50'#k 입니다.", false);
    } else if (status == 7) {
        cm.sendScreenText("#fs15##fc0xFF4641D9#강화 성공#k 시, #fc0xFF6799FF#올스탯 + 1 공/마 + 1 HP + 10#k이 부여됩니다.", false);
    } else if (status == 8) {
        cm.sendScreenText("#fs15##fc0xFFCC3D3D#강화 실패#k 시, #fc0xFFF15F5F#올스탯 - 1 공/마 - 1 HP - 10#k이 부여됩니다.", false);
    } else if (status == 9) {
        cm.sendScreenText("#fs18#강화 확률은 #r60%#k 입니다.", true);
    } else if (status == 10) {
        cm.Endtuto(false);
        cm.dispose();
    }
}