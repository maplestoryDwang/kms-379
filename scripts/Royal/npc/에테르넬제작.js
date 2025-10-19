importPackage(Packages.constants);

var enter = "\r\n";

var selectitem ;
var originitem;

function start()
{
	St = -1;
	action(1, 0, 0);
}

var itemlist2 = [
    1005980,
    1042433,
    1062285,
];

var itemlist = [
    1009500,
    1042392,
    1062258,
];
//모자 / 상의 / 하의

//소모재료 : 재료코드 , 수량
var Material =[
    [4001832, 1],
]

var suc = 100; //확률

function action(M, T, S)
{
	if(M != 1)
	{
		cm.dispose();
		return;
	}

	if(M == 1)
	St++;

	if(St == 0) {
        var 말 = "#fs11##b에테르넬 아이템으로 강화하실 아이템을 선택해 주세요.#k\r\n\r\n";
        for (i = 0; i <= cm.getInventory(1).getSlotLimit(); i++) {
            if ((cm.getInventory(1).getItem(i) != null) && !cm.isCash(cm.getInventory(1).getItem(i).getItemId())) {
                if(cm.getInventory(1).getItem(i).getItemId() >= itemlist[0] && cm.getInventory(1).getItem(i).getItemId() <= (itemlist[0]+5)){
                    말 += "#L" + i + "# #i" + cm.getInventory(1).getItem(i).getItemId() + "# #z" + cm.getInventory(1).getItem(i).getItemId() + "# #r["+i+"슬롯]#k\r\n"
                }
                if(cm.getInventory(1).getItem(i).getItemId() >= itemlist[1] && cm.getInventory(1).getItem(i).getItemId() <= (itemlist[1]+5)){
                    말 += "#L" + i + "# #i" + cm.getInventory(1).getItem(i).getItemId() + "# #z" + cm.getInventory(1).getItem(i).getItemId() + "# #r["+i+"슬롯]#k\r\n"
                }
                if(cm.getInventory(1).getItem(i).getItemId() >= itemlist[2] && cm.getInventory(1).getItem(i).getItemId() <= (itemlist[2]+5)){
                    말 += "#L" + i + "# #i" + cm.getInventory(1).getItem(i).getItemId() + "# #z" + cm.getInventory(1).getItem(i).getItemId() + "# #r["+i+"슬롯]#k\r\n"
                }
                
            }
        }
        cm.sendSimple(말);
	} else if(St == 1) {
        inz = cm.getInventory(1);
        selectitem = inz.getItem(S);
        originitem = Packages.objects.item.MapleItemInformationProvider.getInstance().getEquipById(selectitem.getItemId(), false);
        var state = selectitem.getState() == 20 ? "#fUI/UIWindow2.img/AdditionalOptionTooltip/legendary# #k#e#fs12##fc0xFFAAD34A#레전더리#fs11##n#k" : selectitem.getState() == 19 ? "#fUI/UIWindow2.img/AdditionalOptionTooltip/unique# #k#e#fs12##fc0xFFEDA900#유니크#fs11##n" : selectitem.getState() == 18 ? "#fUI/UIWindow2.img/AdditionalOptionTooltip/epic# #k#e#fs12##fc0xFF5F00FF#에픽#fs11##n" : selectitem.getState() == 17 ? "#fUI/UIWindow2.img/AdditionalOptionTooltip/rare# #k#e#fs12##fc0xFF1266FF#레어#fs11##n" : "없음";
        txt = "#fs11#선택하신 아이템이#b[ ";
        ii = selectitem.getItemId();
        it = selectitem.getMoru();
        if(Math.floor(ii/1000000) == 1){
        txt += it == 0 ? "#i"+ii+"#" +  "#b#z" + selectitem.getItemId() + "##k#l" : "#i"+(String(Math.floor(ii/10000))+String(it))+"#" +  "#b#z" + selectitem.getItemId() + "##k#l"
        } else {
        txt += "#i"+ii+"#" +  "#b#z" + selectitem.getItemId() + "##k#l";
        }
        txt += " ]#k가 맞나?\r\n"
        txt += "#fc0xFFD5D5D5#─────────────────────────────#k\r\n";
        txt += "#b#z" + selectitem.getItemId() + "#의 옵션은 아래와 같습니다.\r\n"
        txt += "#fc0xFFBDBDBD#업그레이드시, 옵션은 유지되며, 계정 내 이동가능으로 변경됩니다.\r\n\r\n#d"
        if(selectitem.getOwner() != null){
            txt += "" + selectitem.getOwner() + "\r\n"
        }
        if(-(originitem.getState() - selectitem.getState())>0){
            txt += "" + state + "\r\n"
        } // 잠재능력등급
        if(selectitem.getEnhance()>0){
        txt += "스타포스 수치 : \r\n";
        if (selectitem.getEquipmentType() == 5888) {
            txt += "#fc0xFF3DB7CC#";
        } else {
            txt += "#fc0xFFFFBB00#";
        }
            for(var i = 1; i<(-(originitem.getEnhance() - selectitem.getEnhance()))+1; i++){
                txt += "★";
                if (i % 5 == 0 && i != 0){
                    txt += " ";
                }
            }
        txt += "#k\r\n#d";
        } //스타포스 강화
        if(-(originitem.getStr() - selectitem.getStr())>0){
            txt += "STR : + " + -(originitem.getStr() - selectitem.getStr()) + "\r\n"
        } //힘
        if(-(originitem.getDex() - selectitem.getDex())>0){
            txt += "DEX : + " + -(originitem.getDex() - selectitem.getDex()) + "\r\n"
        } //덱스
        if(-(originitem.getInt() - selectitem.getInt())>0){
            txt += "INT : + " + -(originitem.getInt() - selectitem.getInt()) + "\r\n"
        } //인트
        if(-(originitem.getLuk() - selectitem.getLuk())>0){
            txt += "LUK : + " + -(originitem.getLuk() - selectitem.getLuk()) + "\r\n"
        } //럭
        if(-(originitem.getHp() - selectitem.getHp())>0){
            txt += "HP : + " + -(originitem.getHp() - selectitem.getHp()) + "\r\n"
        } //HP
        if(-(originitem.getMp() - selectitem.getMp())>0){
            txt += "Mp : + " + -(originitem.getMp() - selectitem.getMp()) + "\r\n"
        } //Mp
        if(-(originitem.getWatk() - selectitem.getWatk())>0){
            txt += "공격력 : + " + -(originitem.getWatk() - selectitem.getWatk()) + "\r\n"
        } //공격력
        if(-(originitem.getMatk() - selectitem.getMatk())>0){
            txt += "마력 : + " + -(originitem.getMatk() - selectitem.getMatk()) + "\r\n"
        } //마력
        if(-(originitem.getWdef() - selectitem.getWdef())>0){
            txt += "방어력 : + " + -(originitem.getWdef() - selectitem.getWdef()) + "\r\n"
        } //방어력
        if(-(originitem.getSpeed() - selectitem.getSpeed())>0){
            txt += "스피드 : + " + -(originitem.getSpeed() - selectitem.getSpeed()) + "\r\n"
        } //스피드
        if(-(originitem.getJump() - selectitem.getJump())>0){
            txt += "점프 : + " + -(originitem.getJump() - selectitem.getJump()) + "\r\n"
        } //점프
        if(-(originitem.getBossDamage() - selectitem.getBossDamage())>0){
            txt += "보스 몬스터 공격 시 데미지 : + " + -(originitem.getBossDamage() - selectitem.getBossDamage()) + "%\r\n"
        } //보스데미지
        if(-(originitem.getIgnorePDR() - selectitem.getIgnorePDR())>0){
            txt += "몬스터 방어율 무시 : + " + -(originitem.getIgnorePDR() - selectitem.getIgnorePDR()) + "%\r\n"
        } //몬스터방어력무시
        if(-(originitem.getTotalDamage() - selectitem.getTotalDamage())>0){
            txt += "총데미지 : + " + -(originitem.getTotalDamage() - selectitem.getTotalDamage()) + "%\r\n"
        } //총데미지
        if(-(originitem.getAllStat() - selectitem.getAllStat())>0){
            txt += "올스탯 : + " + -(originitem.getAllStat() - selectitem.getAllStat()) + "%\r\n"
        } //올스탯
        if(selectitem.getLevel()>0){
            txt += "주문서 성공 횟수 : "+selectitem.getLevel()+"\r\n"
        } //주문서 성공 횟수
        if(selectitem.getViciousHammer()>0){
            txt += "황금망치 제련완료\r\n"
        } //황금망치
        if(selectitem.getKarmaCount()>0){
            txt += "카르마 가위 횟수 : " + (selectitem.getKarmaCount()) + "\r\n"
        } //카르마 가위 횟수
        if(selectitem.getUpgradeSlots()>0){
            txt += "업글레이드 횟수 : " + (selectitem.getUpgradeSlots()) + "\r\n"
        } // 업그레이드 횟수
        txt += "승급을 진행 하시고 싶으시다면 입력창에\r\n#e#r승급#n#k라고 적어주세요.\r\n\r\n";
        txt += "#fs11##r※ 잠재 능력 옵션은 표기되진 않지만 그대로 전승 됩니다.\r\n "
        cm.sendGetText(txt, 1052206);
	} else if(St == 2) {
        text = cm.getText().replaceAll(" ", "");;
        if (!text.contains("승급")) {
            cm.getPlayer().dropMessage(5, "잘못 입력하셧습니다.");
            cm.dispose();
            return;
        } else {
            for(var i =0; i<Material.length; i++){
                if (!cm.haveItem(Material[i][0], Material[i][1])) {   //재료체크
                    cm.sendOkS("#fs11##r#z" + Material[i][0] + "##k 아이템이 부족하지 않는지 다시 확인해보게.", 0x04, 1052206);
                    cm.dispose();
                    return;
                }
            }
            
            //아이템값 구하기
            var upitem = 0;
            for(var i = 0; i<5; i++){
                if(itemlist[0]+i == selectitem.getItemId()){
                    upitem = itemlist2[0]+i;
                }
                if(itemlist[1]+i == selectitem.getItemId()){
                    upitem = itemlist2[1]+i;
                }
                if(itemlist[2]+i == selectitem.getItemId()){
                    upitem = itemlist2[2]+i;
                }
            }
            if (Math.floor(Math.random() * 100) <= suc) {// 성공했을때
                wonitem = Packages.objects.item.MapleItemInformationProvider.getInstance().getEquipById(selectitem.getItemId());
                giveitem = Packages.objects.item.MapleItemInformationProvider.getInstance().getEquipById(upitem);
                giveitem.addStr(- (originitem.getStr() - selectitem.getStr()));
                giveitem.addDex(- (originitem.getDex() - selectitem.getDex()));
                giveitem.addInt(- (originitem.getInt() - selectitem.getInt()));
                giveitem.addLuk(- (originitem.getLuk() - selectitem.getLuk()));
                giveitem.addHp(- (originitem.getHp() - selectitem.getHp()));
                giveitem.addMp(- (originitem.getMp() - selectitem.getMp()));
                giveitem.addWatk(- (originitem.getWatk() - selectitem.getWatk()));
                giveitem.addMatk(- (originitem.getMatk() - selectitem.getMatk()));
                giveitem.addWdef(- (originitem.getWdef() - selectitem.getWdef()));
                giveitem.addSpeed(- (originitem.getSpeed() - selectitem.getSpeed()));
                giveitem.addJump(- (originitem.getJump() - selectitem.getJump()));
                giveitem.setState((selectitem.getState()));
                giveitem.setPotential1((selectitem.getPotential1()));
                giveitem.setPotential2((selectitem.getPotential2()));
                giveitem.setPotential3((selectitem.getPotential3()));
                giveitem.setPotential4((selectitem.getPotential4()));
                giveitem.setPotential5((selectitem.getPotential5()));
                giveitem.setPotential6((selectitem.getPotential6()));
                giveitem.setEnhance(- (originitem.getEnhance() - selectitem.getEnhance()));
                giveitem.setEquipmentType((selectitem.getEquipmentType()));
                giveitem.addBossDamage(- (originitem.getBossDamage() - selectitem.getBossDamage()));
                giveitem.addIgnoreWdef(- (originitem.getIgnorePDR() - selectitem.getIgnorePDR()));
                giveitem.addTotalDamage(- (originitem.getTotalDamage() - selectitem.getTotalDamage()));
                giveitem.addAllStat(- (originitem.getAllStat() - selectitem.getAllStat()));
                giveitem.setViciousHammer((selectitem.getViciousHammer()));
                //giveitem.setKarmaCount((selectitem.getKarmaCount()));
                giveitem.setUpgradeSlots((selectitem.getUpgradeSlots() + 10));
                giveitem.setLevel((selectitem.getLevel()));
                //giveitem.setMoru((selectitem.getMoru()));
                if(selectitem.getOwner() != null){
                    giveitem.setOwner((selectitem.getOwner()));
                }
                //giveitem.setFire((selectitem.getFire()));
                Packages.objects.item.MapleInventoryManipulator.addbyItem(cm.getClient(), giveitem);
                Packages.objects.item.MapleInventoryManipulator.removeFromSlot(cm.getClient(), Packages.constants.GameConstants.getInventoryType(selectitem.getItemId()), selectitem.getPosition(), selectitem.getQuantity(), false);
                for(var i =0; i<Material.length; i++){
                    cm.gainItem(Material[i][0], -Material[i][1]);
                }
                말 = "#fs11#승급에 성공했네!\r\n\r\n"
                말 += "#fUI/UIWindow2.img/QuestIcon/4/0#\r\n\r\n"
                말 += "#i" + upitem + "##b#z" + upitem + "##k";
                cm.sendOkS(말, 0x04, 1052206);
                cm.dispose();
            } else { // 실패했을때
                for(var i =0; i<Material.length; i++){
                    cm.gainItem(Material[i][0], -Material[i][1]);
                }
                말 = "#fs11#안타깝게도 해방에 실패했다네..\r\n\r\n"
                cm.sendOkS(말, 0x04, 1052206);
                cm.dispose();
            }
        }
	}
}

