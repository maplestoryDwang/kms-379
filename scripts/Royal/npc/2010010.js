importPackage(java.lang);
importPackage(java.util);
importPackage(java.awt);
importPackage(Packages.constants);


var status = -1;
function start() {
    status = -1;
    action (1, 0, 0);
}
function action(mode, type, selection) {

    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        status --;
    }
    if (mode == 1) {
        status++;
    }


    if (status == 0) {
        if (cm.getPlayer().getGuild() != null) {
            if (cm.getPlayer().getInventory(Packages.objects.item.MapleInventoryType.USE).getNumFreeSlot() < 2) {
                cm.sendOk("#fs11#인벤토리 소비탭 2칸을 비우고 다시 말을 걸어줘, 물품은 받아야 될 것 아니야?");
                cm.dispose();
                return;
            }
            cm.sendYesNo("#fs11##r매주 한 번#k 지급되는 #b길드 정기 지원 물품#k이 도착했어.\r\n지금 지원 물품을 받겠어?", 2010010);
        } else {
            cm.sendOk("#fs11#길드가 없어 받을 수 없습니다.")
            cm.dispose();
            return;
        }
    } else if (status == 1) {
        
        if (cm.getPlayer().getGuild().getSkillLevel(91000031) != 0 ||
        cm.getPlayer().getGuild().getSkillLevel(91000032) != 0 ||
        cm.getPlayer().getGuild().getSkillLevel(91000009) != 0) {
         
        var itemid = 2432290;
        var pootioncount = cm.getPlayer().getGuild().getSkillLevel(91000009) * 25;
        var itemcount = cm.getPlayer().getGuild().getSkillLevel(91000009) * 5;
        if (cm.getPlayer().getGuild().getSkillLevel(91000031) >= 1) {
            itemid = 2631501;
            pootioncount += 100;
            itemcount += 5;
        }
        if (cm.getPlayer().getGuild().getSkillLevel(91000032) >= 1) {
            itemid = 2631501;
            pootioncount += 100;
            itemcount += 5;
        }
        time = new Date();
        var day2 = time.getDay();
        var check = day2 == 1 ? 7 : day2 == 2 ? 6 : day2 == 3 ? 5 : day2 == 4 ? 4 : day2 == 5 ? 3 : day2 == 6 ? 2 : day2 == 0 ? 1 : 0;
        var inz = new Packages.objects.item.Item(itemid, 0, 1);

        beforedate = cm.getPlayer().getKeyValue("GuildBless");
        afterdate = getMonday(new Date());

        if(beforedate >= afterdate) {
            cm.sendOk("#fs11#이번주는 이미 물품을 받으셨습니다.");
            cm.dispose();
            return;
          }

        inz.setExpiration((new Date(time.getFullYear(), time.getMonth(), time.getDate(), 0, 0, 0, 0)).getTime() + (1000 * 60 * 60 * 24 * check));
        inz.setQuantity(itemcount);
        Packages.objects.item.MapleInventoryManipulator.addbyItem(cm.getClient(), inz);
        inz = new Packages.objects.item.Item(2002037, 0, 1);
        inz.setExpiration((new Date(time.getFullYear(), time.getMonth(), time.getDate(), 0, 0, 0, 0)).getTime() + (1000 * 60 * 60 * 24 * check));
        inz.setQuantity(pootioncount);
        Packages.objects.item.MapleInventoryManipulator.addbyItem(cm.getClient(), inz);
        cm.sendOk("#fs11#지급받은 물품은 이번 주 일요일까지 쓸 수 있어.\r\n열심히 활동해서 스킬 레벨이 올라가면 정기 지원 물품을 더 많이 받을 수 있으니까 노력하라구.\r\n그럼 이번 주도 수고하고, 다음 주에 지원 물품이 다시 도착 하니까 그때 보자구.", 2010010);

        cm.getPlayer().setKeyValue("GuildBless", TodayDate(new Date()));
        cm.dispose();
    } else {
        cm.sendOk("#fs11#길드 지원 스킬을 배우지 않았습니다.");
        cm.dispose();
    }
}
}

function getMonday(date ) {
    // 표기방식 = yyyyddmm

    var year = date.getFullYear();

    // 월요일로이동
    var day = date.getDay() || 7;  
    if( day !== 1 ) 
        date.setHours(-24 * (day - 1));
    // 월요일까지이동완료
 
    var month = date.getMonth() + 1;
    if (month < 10)  {
        month = '0' + month;
    }

    var date = date.getDate();
    if (date < 10) {
        date = '0' + date;
    }

    return year + '' + month + '' + date;
}

function TodayDate(date) {
    // 표기방식 = yyyyddmm

    var year = date.getFullYear();

    var month = date.getMonth() + 1;
    if (month < 10)  {
        month = '0' + month;
    }

    var date = date.getDate();
    if (date < 10) {
        date = '0' + date;
    }
    
    return year + '' + month + '' + date;
}