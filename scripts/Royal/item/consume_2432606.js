var seld, seld2;
var enter = "\r\n"
var ch = 0;

var Potential = ["크리티컬 데미지 + 8 %", "보스 공격 시 데미지 + 40 % ", "메소 획득량 + 20% ", "아이템 획득 확률 + 20 % ", "올스텟 + 20 % ", "공격력 + 12% ", "마력 + 12% ", "올스텟 + 12%", "크리티컬 확률 + 12% ",
"쿨타임 감소 + 10%","방어율 무시 + 30%","힘 + 12%","덱스 + 12%","인트 + 12%","럭 + 12%","체력 + 12%",];
var Potentiallist = [];
var itemid;

function start() {
	St = -1;
	action(1, 0, 0);
}

function action(M, T, S) {
	if(M != 1) {
		cm.dispose();
		return;
	}

	if(M == 1)
		if(St == 2 && ch < 2){
			ch++
			PotentialSel(S);
		} else {
			St++;
		}
	    

	if(St == 0) {
		var msg ="잠재능력 부여 시스템"+enter;
		msg+="#L1#  윗잠재 설정"+enter;
		//msg+="#L2#  아랫잠재 설정"+enter;
		cm.sendSimple(msg);
	} else if(St == 1) {
		seld = S;
		if(!cm.haveItem(2431307, seld*1)){
			cm.sendOk("코인이 부족합니다.");
			cm.dispose();
			return;
		}
		var msg ="잠재능력을 부여할 아이템을 선택해주세요."+enter+enter;
		for (j = 0; j < cm.getInventory(1).getSlotLimit(); j++) {
			if (cm.getInventory(1).getItem(j) != null) {
				itemid = cm.getInventory(1).getItem(j).getItemId();
				msg += "#L"+j+"##i"+itemid+"##z"+itemid+"#"+enter;
			}
		} 
		cm.sendSimple(msg);
	} else if(St == 2) {
		if(seld2 == null){
			seld2 = S;
			itemid = cm.getInventory(1).getItem(S);
		}
		if(itemid.getPotential1() == 0 && seld == 2){
			cm.sendOk("윗잠재능력이 없으면 불가능합니다.");
			cm.dispose();
			return;
		}
		var msg ="잠재능력을 선택해주세요."+enter+enter;
		for(var i = 0; i < Potential.length; i++){
			msg+="#L"+i+"# #b"+Potential[i]+"#k"+enter;
		}
		cm.sendSimple(msg);
	} else if(St == 3) {
		PotentialSel(S);
		item = cm.getInventory(1).getItem(seld2);
		if(seld == 1){
			item.setState(20);
			item.setPotential1(Potentiallist[0]);
			item.setPotential2(Potentiallist[1]);
			item.setPotential3(Potentiallist[2]);
                                    cm.gainItem(2432606, -1);
		} else if(seld == 2){
			item.setState(20);
			item.setPotential4(Potentiallist[0]);
			item.setPotential5(Potentiallist[1]);
			item.setPotential6(Potentiallist[2]);
		}
		cm.getPlayer().forceReAddItem(item, Packages.objects.item.MapleInventoryType.getByType(1));
		cm.sendOk("잠재능력이 적용되셧습니다.");
		cm.dispose();
	}
}

function PotentialSel(num){
		switch(num){
			case 0:
				Potentiallist.push(40057);
			break;
			case 1:
				Potentiallist.push(40603);
			break;
			case 2:
				Potentiallist.push(40650);
			break;
			case 3:
				Potentiallist.push(40656);
			break;
			case 4:
				Potentiallist.push(60002);
			break;
			case 5:
				Potentiallist.push(40051);
			break;
			case 6:
				Potentiallist.push(40052);
			break;
			case 7:
				Potentiallist.push(40081);
			break;
			case 8:
				Potentiallist.push(40055);
			break;
			case 9:
				Potentiallist.push(40557);
			break;
			case 10:
				Potentiallist.push(30291);
			break;
			case 11:
				Potentiallist.push(40041);
			break;
			case 12:
				Potentiallist.push(40042);
			break;
			case 13:
				Potentiallist.push(40043);
			break;
			case 14:
				Potentiallist.push(40044);
			break;
                                    case 15:
				Potentiallist.push(40045);
			break;

		}
}