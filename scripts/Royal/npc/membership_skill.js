var status = -1;
var f = -1;
var lv = -1;
var sp = -1;

var skillNames = [
	"공격력/마력 증가",
	"올스탯, 최대 HP/MP 증가",
	"방어율 무시 증가",
	"일반 몬스터 데미지 증가",
	"보스 몬스터 데미지 증가",
	"버프 지속 시간 증가",
	"크리티컬 확률 증가",
	"아케인 포스 증가",
	"획득 경험치 증가"
];
var needPoints = [ 1, 2, 3 ];

function start(flag) {
    status = -1;
    f = flag;
    lv = 0;
    sp = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) {
	if (f == 0) { 
		f = 0; // 공격력/마력 증가
	} else if (f == 0x100) {
		f = 1; // 올스탯, 최대 HP/MP 증가
	} else if (f == 0x200) {
		f = 2; // 방어율 무시 증가
	} else if (f == 0x300) {
		f = 3; // 일반 몬스터 데미지 증가
	} else if (f == 0x400) {
		f = 4; // 보스 몬스터 데미지 증가
	} else if (f == 0x500) {
	 	f = 5; // 버프 지속 시간 증가
	} else if (f == 0x600) {
		f = 6; // 크리티컬 확률 증가
	} else if (f == 0x700) {
		f = 7; // 아케인 포스 증가
	} else if (f == 0x800) {	
		f = 8; // 획득 경험치 증가
	}
	var v0 = "#e#b[" + skillNames[f] + "]#n#k 스킬을 올리시겠습니까?\r\n\r\n";
	lv = cm.getPlayer().getOneInfoQuestInteger(501046, f + "");
	if (lv >= 3) {
		cm.sendNext("해당 스킬은 더 이상 레벨을 올릴 수 없습니다.");
		cm.dispose();
		return;
	}	

	sp = cm.getPlayer().getOneInfoQuestInteger(501045, "sp");
	v0 += "- 필요한 스킬 포인트 : #e#r" + needPoints[lv] + "#k#n\r\n";
	v0 += "- 보유한 스킬 포인트 : #e#b" + sp + "#k#n";
	cm.sendYesNo(v0);
    } else if (status == 1) {
	if (sp < needPoints[lv]) {
		cm.sendNext("스킬 포인트가 부족해서 레벨을 올릴 수 없습니다.");
		cm.dispose();
		return;
	}
	cm.getPlayer().updateOneInfo(501045, "sp", (sp - needPoints[lv]) + "");
	lv += 1;
	cm.getPlayer().updateOneInfo(501046, f + "", lv + "");
	cm.sendNext("#e#b" + skillNames[f] + "#k#n 스킬이 #e#r" + lv + " 레벨#k#n이 되었습니다.");
	cm.bigScriptProgressMessage(skillNames[f] + " 스킬이 " + lv + " 레벨이 되었습니다!");
	cm.dispose();
    }
}