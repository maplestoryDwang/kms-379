/*
제니아 우편함
*/
importPackage(Packages.objects.item);
importPackage(Packages.objects.users);

importPackage(Packages.database);
    
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

var status = -1;

function start()
{
     status = -1;    
     action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
    status++;
    } else {
    if (status == 0) {
        cm.dispose();
    }
    status--;
    }
    if (status == 0) {
    say = "#fs11#" + 별 + 색 + "우편함 아이템을 지급 받으시겠습니까?" + 엔터2 + 별빨 +"#r 인벤토리의 공간을 충분히 비워주시길 바랍니다.\r\n\r\n#e< 우편함 목록 >#n\r\n"
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM `offline` WHERE `chrid` = " + cm.getPlayer().getId() + " AND status = 0");
            rs = ps.executeQuery();
            while (rs.next()) {
                say += 검은색 + "#i" + rs.getString("item") + "#" + 색 + " #z" + rs.getString("item") + "# " + rs.getInt("qua") + "개\r\n";
            }
            rs.close();
            ps.close();
            con.close();
            cm.sendYesNo(say);
            return;
        } catch (e) {
            cm.sendOk("오류가 발생하였습니다.\r\n\r\n" + e);
            cm.dispose();
            return;
        }
    } else if (status == 1) {
        if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.EQUIP).getNumFreeSlot() < 10 || cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.USE).getNumFreeSlot() < 10 || cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.ETC).getNumFreeSlot() < 10) {
           cm.sendOk("#fs11#장비,소비,기타 칸을 10칸 이상 비워주세요");
           cm.dispose();
           return;
        }

        if (MailBox.pickUpItemOff(cm.getPlayer())) {
            MailBox.removeItemOff(cm.getPlayer().getId());
            cm.sendOk("#fs11#우편함에 있는 아이템을 지급 받으셨습니다.\r\n인벤토리를 확인하세요");
        } else {
            cm.sendOk("#fs11#우편함에 아이템이 없습니다.");
        }
        cm.dispose();
    }
}