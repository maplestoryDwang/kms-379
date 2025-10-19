function start() {
	cm.dispose();
	//cm.setMirrorDungeonInfo(false);
	//cm.openUI(152);
	cm.openUI(1271);
}

function trade_1100() {
	//cm.setMirrorDungeonInfo(true);
	cm.dispose();
	//cm.getPlayer().setSelectDungeon(true);
	cm.openNpc(9062611);
}

function trade_1101() {
	//cm.setMirrorDungeonInfo(true);
	cm.dispose();
	//cm.getPlayer().setSelectDungeon(true);
	cm.openNpc(9062277);
}

function trade_1102() {
	//cm.setMirrorDungeonInfo(true);
	cm.dispose();
	//cm.getPlayer().setSelectDungeon(true);
	cm.openNpc(9062294);
}

function trade_1103() {
	//cm.setMirrorDungeonInfo(true);
	Packages.scripting.newscripting.ScriptManager.runScript(cm.getPlayer().getClient(), "codySystem", null);
	cm.dispose();
	//cm.getPlayer().setSelectDungeon(true);
}

function trade_1104() {
	//cm.setMirrorDungeonInfo(true);
	cm.dispose();
	//cm.getPlayer().setSelectDungeon(true);
	cm.openNpc(1530058);
}

function trade_1105() {
	cm.dispose();
	cm.dimensionalMirror();
}

function action(a, b, c) {
	cm.dispose();
}