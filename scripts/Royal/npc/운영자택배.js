importPackage(Packages.objects.item);
importPackage(Packages.constants);
importPackage(Packages.client);
importPackage(Packages.network);
importPackage(Packages.network.models);

importPackage(Packages.objects.item);
importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);
importPackage(java.awt);

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
var Today = (Year + "-" + Month + "-" + Date);

var status = 0;
var operation = -1;
var select = -1;
var type;
var ty;
var dd = true;
var yes = 1;
var invs = Array(1, 5);
var invv;
var selected;
var slot_1 = Array();
var slot_2 = Array();
var statsSel;
var sel;
var name;
var isban = false;

var banitem = [2437659, 3994410, 2434583, 2430027, 2040727, 2439302, 2437121, 2430218, 2023072, 2430488, 2433977, 2432305, 3994351, 2437122, 2023287, 4033114, 4310261, 2630442, ];

var canitemCube = [
    5062009,
    5062010,
    5062500,
    5062005,
    5062503,
];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            var ask = "#fs11#";
            ask += 검은색 + "어느 타입의 아이템을 #r택배" + 검은색 + "로 보내시겠습니까?\r\n\r\n";
            ask += 색 + "▶ 선물하실 아이템 종류를 선택해주세요.#k\r\n";
            ask +="#L1##b[장비]" + 검은색 + "아이템을 보내겠습니다.\r\n";
            ask +="#L2##b[소비]" + 검은색 + "아이템을 보내겠습니다.\r\n";
            ask +="#L3##b[설치]" + 검은색 + "아이템을 보내겠습니다.\r\n";
            ask +="#L4##b[기타]" + 검은색 + "아이템을 보내겠습니다.\r\n";
            ask +="#L5##b[캐시]" + 검은색 + "아이템을 보내겠습니다.\r\n";
            //ask += "#L55##b[큐브]" + 검은색 + "아이템을 보내겠습니다.\r\n";
            ask +="#L6##b[치장]" + 검은색 + "아이템을 보내겠습니다.\r\n";
            //ask += "\r\n#L999##b[교환가능아이템]" + 검은색 + "리스트를 보겠습니다.\r\n";
            cm.sendSimple(ask);

        } else if (cm.getPlayer().getReborns() < 0) {
            cm.sendOk("\r\n#b총 10번 이상의 환생 유저만 사용가능.\r\n");
            cm.dispose();

        } else if (status == 1) {
            operation = selection;
            if (operation == 1) {
                type = Packages.objects.item.MapleInventoryType.EQUIP;
                yes = 1;
            } else if (operation == 2) {
                type = Packages.objects.item.MapleInventoryType.USE;
                yes = 2;
            } else if (operation == 4) {
                type = Packages.objects.item.MapleInventoryType.SETUP;
                yes = 4;
            } else if (operation == 3) {
                type = Packages.objects.item.MapleInventoryType.ETC;
                yes = 3;
            } else if (operation == 5) {
                type = Packages.objects.item.MapleInventoryType.CASH;
                yes = 5;
            } else if (operation == 6) {
                type = Packages.objects.item.MapleInventoryType.EQUIP;
                yes = 6;
            } else if (operation == 55) {
                type = Packages.objects.item.MapleInventoryType.CASH;
                yes = 5;
            } else if (operation == 999) {
                var msg = 별파 + "#fs11##fc0xFF000000# 보낼 수 있는 아이템 리스트입니다! #fs11#" + 별파 + 엔터 + 엔터;
                msg += 색 + "#e<큐브>#n\r\n";
                for (i = 0; i < canitemCube.length; i++) {
                    msg += 검은색 + "#i" + canitemCube[i] + "##z" + canitemCube[i] + "##k" + 엔터;
                }
                cm.sendOk(msg);
                cm.dispose();
            }
            if (selection >= 1 && selection <= 6 || selection == 55) {
                cm.sendGetText("#fs11#" + 검은색 + "택배받으실 분의 닉네임을 입력해주세요.#r\r\n\r\n받으시는분은 같은 채널에 접속중이여야 합니다. 고유 아이템 택배시 이미 아이템을 보유 중 일시 증발되며 잘못된 사용으로 인한 문제는 운영진 측에서 책임지지 않습니다,");
            } else if (selection == 7) {
                cm.sendOk("#fs11#택배시스템은 아이템의 종류나 옵션에 관계없이 누구에게나 택배할수 있는 시스템입니다. 택배을 하기 위해선 5억 메소가 필요하며, 같은 채널에 접속중이여야 합니다. 잘못된 사용으로 인한 문제는 운영진 측에서 책임지지 않습니다.");
                cm.dispose();
            }
        } else if (status == 2) {
            if (operation == 1) {
                type = Packages.objects.item.MapleInventoryType.EQUIP;
            } else if (operation == 2) {
                type = Packages.objects.item.MapleInventoryType.USE;
            } else if (operation == 3) {
                type = Packages.objects.item.MapleInventoryType.SETUP;
            } else if (operation == 4) {
                type = Packages.objects.item.MapleInventoryType.ETC;
            } else if (operation == 5) {
                type = Packages.objects.item.MapleInventoryType.CASH;
            } else if (operation == 6) {
                type = Packages.objects.item.MapleInventoryType.EQUIP;
            } else if (operation == 55) {
                type = Packages.objects.item.MapleInventoryType.CASH;
            }
            var item = cm.getChar().getInventory(type);
            var text = cm.getText();
            var conn = Packages.network.game.GameServer.getInstance(cm.getClient().getChannel()).getPlayerStorage().getCharacterByName(text);
            if (conn == null) {
                cm.sendOk("#fs11#현재 접속중이 아니거나 채널이 다릅니다. 혹은 존재하지 않는 닉네임일 수도 있습니다.");
                cm.dispose();
	} else if (!cm.getPlayer().isGM() && !conn.isGM()) {
                cm.sendOk("#fs11#" + 색 + "운영자 에게만 택배를 보낼 수 있습니다");
                cm.dispose();
	} else {
                var ok = false;
                var selStr = "#fs11##b" + conn.getName() + 검은색 + "님에게 어떤 아이템을 택배하시겠습니까?\r\n";
                for (var x = 1; x < 2; x++) {
                    var inv = cm.getInventory(yes);
                    for (var i = 0; i <= cm.getInventory(yes).getSlotLimit(); i++) {
                        if (x == 0) {
                            slot_1.push(i);
                        } else {
                            slot_2.push(i);
                        }
                        var it = inv.getItem(i);
                        if (it == null) {
                            continue;
                        }
                        var itemid = it.getItemId();
                        ok = true;
                        if ((itemid >= 1140000 && itemid <= 1143999) && !cm.getPlayer().isGM()) {
                            continue;
                        }
                        //여기가 택배물건 정하는곳
                        if (operation == 55) {
                            for (ii = 0; ii < canitemCube.length; ii++) {
                                if (cm.getInventory(yes).getItem(i).getItemId() == canitemCube[ii]) {
                                    selStr += 검은색 + "#L" + (yes * 1000 + i) + "##i" + itemid + "##z" + itemid + "# #r[" + i + "슬롯]\#l\r\n";
                                }
                            }
                        } else {
                            selStr += 검은색 + "#L" + (yes * 1000 + i) + "##i" + itemid + "##z" + itemid + "# #r[" + i + "슬롯]#l\r\n";
                        }
                    }
                }
                if (!ok) {
                    cm.sendOk("#fs11#택배를 보낼 아이템이 없는것 같은데요?");
                    cm.dispose();
                    return;
                }
                cm.sendSimple(selStr + "#k");
            }
        } else if (status == 3) {
            sel = selection;
            if (operation == 1) {
                type = Packages.objects.item.MapleInventoryType.EQUIP;
            } else if (operation == 2) {
                type = Packages.objects.item.MapleInventoryType.USE;
            } else if (operation == 3) {
                type = Packages.objects.item.MapleInventoryType.SETUP;
            } else if (operation == 4) {
                type = Packages.objects.item.MapleInventoryType.ETC;
            } else if (operation == 5) {
                type = Packages.objects.item.MapleInventoryType.CASH;
            } else if (operation == 6) {
                type = Packages.objects.item.MapleInventoryType.CASH_EQUIP;
            } else if (operation == 55) {
                type = Packages.objects.item.MapleInventoryType.CASH;
            }
            var item = cm.getChar().getInventory(type).getItem(selection % 1000).copy();
            var text = cm.getText();
            invv = selection / 1000;
            var inzz = cm.getInventory(invv);
            selected = selection % 1000;
            if (invv == invs[0]) {
                statsSel = inzz.getItem(slot_1[selected]);
            } else {
                statsSel = inzz.getItem(slot_2[selected]);
            }
            if (statsSel == null) {
                cm.sendOk("#fs11#오류입니다. 운영자에게 보고해주세요.");
                cm.dispose();
                return;
            }
            var text = cm.getText();
            var con = Packages.network.game.GameServer.getInstance(cm.getClient().getChannel()).isMyChannelConnected(text);
            var conn = Packages.network.game.GameServer.getInstance(cm.getClient().getChannel()).getPlayerStorage().getCharacterByName(text);

            for (a = 0; a < banitem.length; a++) {
                if (banitem[a] == item.getItemId()) {
                    isban = true;
                    continue;
                }
            }

            if (conn == null) { // 이미 상대방 닉네임을 입력했는데 그상태에서 상대방이 로그아웃,채널변경등시 아이템 증발하는 오류 방지
                cm.sendOk("#fs11#상대방이 로그아웃, 채널변경 등을 하셨습니다\r\nr\n#r택배발송을 취소합니다.");
                cm.dispose();
                return;
            }

            if (item.getQuantity() == 1) {
                if (cm.haveItem(5330000, 0)) {
                    if (Packages.constants.GameConstants.isPet(item.getItemId()) == false) {
                        if (cm.getPlayer().getName() != text) {
                            //if (!conn.canHold(item.getItemId())) {
                            //	cm.sendOk("#fs11#택배을 받을 상대방의 인벤토리에 빈 공간이 없습니다.");
                            //	cm.dispose();
                            //	return;
                            //}
                            if (isban) {
                                cm.sendOk("금지된 아이템입니다.");
                                cm.dispose();
                                return;
                            }
                            Packages.objects.item.MapleInventoryManipulator.removeFromSlot(cm.getC(), type, selection % 1000, item.getQuantity(), true);
                            Packages.objects.item.MapleInventoryManipulator.addFromDrop(conn.getClient(), item, true);
                            //cm.gainItem(5330000, -1);
                            WriteLog(cm.getPlayer().getName(), text, item.getItemId(), item.getQuantity());
                            cm.sendOk("#fs11#택배를 " + text + " 님에게 정상적으로 보냈습니다");
                            cm.dispose();
                        } else {
                            cm.sendOk("#fs11#자기 자신에게는 택배할수 없습니다.");
                            cm.dispose();
                        }
                    } else {
                        cm.sendOk("#fs11#펫은 택배로 보낼 수 없습니다. 학대를 멈춰주세요");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("이용할 권한이 없습니다.");
                    cm.dispose();
                }
            } else {
                if (!isban) {
                    cm.sendGetNumber("#fs11#몇개를 택배보내시겠습니까?\r\n현재 소지중인 #i" + item.getItemId() + "# #b(#t" + item.getItemId() + "#)#k 갯수 : #b" + item.getQuantity() + "#k", 1, 1, item.getQuantity());
                } else {
                    cm.sendOk("금지된 아이템입니다.");
                    cm.dispose();
                }
            }
            name = text;
        } else if (status == 4) {

            if (isban) {
                cm.sendOk("오류입니다.");
                cm.dispose();
                return;
            }
            var sele = selection % 1000;
            var quan = cm.getText();
            if (operation == 1) {
                type = Packages.objects.item.MapleInventoryType.EQUIP;
            } else if (operation == 2) {
                type = Packages.objects.item.MapleInventoryType.USE;
            } else if (operation == 3) {
                type = Packages.objects.item.MapleInventoryType.SETUP;
            } else if (operation == 4) {
                type = Packages.objects.item.MapleInventoryType.ETC;
            } else if (operation == 5) {
                type = Packages.objects.item.MapleInventoryType.CASH;
            } else if (operation == 6) {
                type = Packages.objects.item.MapleInventoryType.CASH_EQUIP;
            } else if (operation == 55) {
                type = Packages.objects.item.MapleInventoryType.CASH;
            }
            var item = cm.getChar().getInventory(type).getItem(sel % 1000).copy();
            var text = cm.getText();
            invv = sel / 1000;
            var inzz = cm.getInventory(invv);
            selected = sel % 1000;
            if (invv == invs[0]) {
                statsSel = inzz.getItem(slot_1[selected]);
            } else {
                statsSel = inzz.getItem(slot_2[selected]);
            }
            if (statsSel == null) {
                cm.sendOk("오류입니다. 운영자에게 보고해주세요.");
                cm.dispose();
                return;
            }

            var text = selection;
            var con = Packages.network.game.GameServer.getInstance(cm.getClient().getChannel()).isMyChannelConnected(name);
            var conn = Packages.network.game.GameServer.getInstance(cm.getClient().getChannel()).getPlayerStorage().getCharacterByName(name);

            if (conn == null) { // 이미 상대방 닉네임을 입력했는데 그상태에서 상대방이 로그아웃,채널변경등시 아이템 증발하는 오류 방지
                cm.sendOk("#fs11#상대방이 로그아웃, 채널변경 등을 하셨습니다\r\nr\n#r택배발송을 취소합니다.");
                cm.dispose();
                return;
            }

            if (item.getQuantity() >= text) {
                if (cm.haveItem(5330000, 0)) {
                    if (cm.getPlayer().getName() != name) {
                        item.setQuantity(text);
                        Packages.objects.item.MapleInventoryManipulator.removeFromSlot(cm.getC(), type, sel % 1000, item.getQuantity(), true);
                        Packages.objects.item.MapleInventoryManipulator.addFromDrop(conn.getClient(), item, true);
                        //cm.gainItem(5330000,-1);
                        WriteLog(cm.getPlayer().getName(), name, item.getItemId(), item.getQuantity());
                        cm.sendOk("#fs11#택배를 " + name + " 님에게 정상적으로 보냈습니다");
                        cm.dispose();
                    } else {
                        cm.sendOk("자기 자신에게는 택배할수 없습니다.");
                        cm.dispose();
                    }
                } else {
                    cm.sendOk("아이템을 택배보내기 위해선 퀵배송 이용권이 필요합니다.");
                    cm.dispose();
                }
            } else {
                cm.sendOk("가지고 있는 수보다 더 큰 수를 입력했습니다.");
                cm.dispose();
            }
        }
    }
}

function WriteLog(cname, vname, itemid, qty) {
    Packages.scripting.NPCConversationManager.writeLog("TextLog/zenia/운영자택배/" + Today + ".log", "\r\n보낸계정 : " + cm.getClient().getAccountName() + " (" + cm.getClient().getAccID() + ")\r\n보낸닉네임 : " + cm.getPlayer().getName() + "\r\n받은닉네임 : " + vname + "\r\n선물한 아이템 : " + cm.getItemName(itemid) + "[" + itemid + "] (" + qty + "개)" + "\r\n\r\n", true);
    /*
    a = new Date();
    temp = Packages.objects.utils.Randomizer.rand(0,9999999);
    fFile1 = new File("Log/택배/"+cname+"가 "+vname+"에게 템선물 "+itemid+".log");
    if (!fFile1.exists()) fFile1.createNewFile();
    out1 = new FileOutputStream("Log/택배/"+cname+"가 "+vname+"에게 템선물 "+itemid+".log",false);
    var msg = "'"+cname+"'이 '"+vname+"'에게 선물을 보냈습니다.\r\n";
    msg += "보낸이 : "+cname+"\r\n";
    msg += "받은이 : "+vname+"\r\n";
    msg += "보낸 시각 : "+a.getFullYear()+"년 "+Number(a.getMonth() + 1)+"월 "+a.getDate()+"일 "+a.getHours()+"시 "+a.getMinutes()+"분 "+a.getSeconds()+"초\r\n";
    msg += "보낸 아이템 코드 : "+itemid+"\r\n";
    msg += "보낸 아이템 개수 : "+qty+"\r\n";
    out1.write(msg.getBytes());
    out1.close();
     */
}
