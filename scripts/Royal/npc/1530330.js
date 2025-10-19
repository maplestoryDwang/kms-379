importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);

importPackage(Packages.objects.item);
importPackage(Packages.objects.users);
importPackage(Packages.objects.utils);
importPackage(Packages.constants);
importPackage(Packages.network.models);
importPackage(Packages.objects.wz.provider);
var status = -1;
itemarray = [];

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    ccoin = 4310237;
    banneditem = [3015314, 1802598, 1802599, 5000930, 5000931, 5000932, 5000484, 5000485, 5000486, 5000490, 5000754, 5000727, 5000952];
    if (mode == -1 || mode == 0) {
        itemarray = null;
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }
    if (status == 0) {
        말 = "#fs11#안녕하세요? #r펫#k을 무료로 분양해드리고 있습니다.\r\n귀엽고 많은 펫들이 준비되어 있으니 천천히 골라주세요~\r\n\r\n"
        말 += "#L0##d펫을 골라보겠습니다.#k#l"
        cm.sendSimple(말);
    } else if (status == 1) {
        말 = "#fs11#원하시는 펫을 선택해 주세요.\r\n\r\n";
        var it =Packages.objects.item.MapleItemInformationProvider.getInstance().getAllItems().iterator();
        while (it.hasNext()) {
            var itemPair = it.next();
            banned = false;
            for (i = 0; i < banneditem.length; i++) {
                if (itemPair.getLeft() == banneditem[i]) {
                    banned = true;
                }
            }
            if (itemPair.getLeft() >= 5000000 && itemPair.getLeft() <= 5000999 && (itemPair.getRight() != null && itemPair.getRight() != "") && banned == false && itemPair.getRight() != " " && PetDataFactory.getPetData(itemPair.getLeft()).getWonderGrade() == -1) {
                itemarray.push(itemPair.getLeft());
            }
        }
        it = null;
        for (i = 0; i < itemarray.length; i++) {
            말 += "#L" + i + "# #i" + itemarray[i] + "#"
            if (i % 5 == 4) {
                말 += "\r\n"
            }
        }
        cm.sendSimple(말);
    } else if (status == 2) {
        if (!cm.isCash(itemarray[selection]) || itemarray[selection] >= 5001000) {
            a = new Date();
            temp = Packages.objects.utils.Randomizer.rand(0, 9999999);
            cn = cm.getPlayer().getName();
            fFile1 = new File("TextLog/scriptLog/" + temp + "_" + cn + ".log");
            if (!fFile1.exists()) {
                fFile1.createNewFile();
            }
            out1 = new FileOutputStream("TextLog/scriptLog/" + temp + "_" + cn + ".log", false);
            var msg = "'" + cm.getPlayer().getName() + "'이(가) 복사 시도 의심\r\n";
            msg += "'" + a.getFullYear() + "년 " + Number(a.getMonth() + 1) + "월 " + a.getDate() + "일'\r\n";
            msg += "복사 시도 템코드 : " + selection + "\r\n";
            msg += "캐릭터 아이디 : " + cm.getPlayer().getId() + "\r\n";
            msg += "어카운트 아이디 : " + cm.getPlayer().getAccountID() + "\r\n";
            out1.write(msg.getBytes());
            out1.close();
            cm.sendOk("정상적인 접근이 아닙니다.");
            cm.dispose();
        } else {
            //cm.BuyPET(itemarray[selection]);

            var item = new Item(itemarray[selection], 1, 1, 0);
            item.setExpiration((new Date()).getTime() + (1000 * 60 * 60 * 24 * 365));
            var pet = MaplePet.createPet(itemarray[selection], MapleInventoryIdentifier.getInstance());
            item.setPet(pet);
            item.setUniqueId(pet.getUniqueId());
            Packages.objects.item.MapleInventoryManipulator.addFromDrop(cm.getClient(), item, false);

            cm.sendOk("#fs11#지급을 정상적으로 받았습니다. 즐거운하루 보내세요!");
            cm.dispose();
            itemarray = null;
            return;
        }
    }
}