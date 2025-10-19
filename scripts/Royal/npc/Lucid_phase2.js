importPackage(Packages.tools.packet);
var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }

    if (status == 0) {
		cm.getPlayer().getClient().getSession().writeAndFlush(Packages.network.models.CField.UIPacket.setInGameDirectionMode(false, false, false));
		cm.getPlayer().getClient().getSession().writeAndFlush(CField.blind(1, 0xff, 0xf0, 0xf0, 0xf0, 0, 0));
		cm.sendNextS("#face6#어머, 이를 어째? 꿈이 무너지나봐요~!", 37, 0, 3003250);
		statusplus(3000);
	} else if (status == 1) {
		//cm.dispose();
        cm.getPlayer().getClient().getSession().writeAndFlush(CField.blind(0, 0, 0, 0, 0, 2500, 0));
		cm.getPlayer().getClient().getSession().writeAndFlush(CField.playSE("Sound/SoundEff.img/ArcaneRiver/phase2"));
		cm.getPlayer().getClient().getSession().writeAndFlush(Packages.network.models.CField.UIPacket.InGameDirectionEvent("Map/Effect3.img/BossLucid/Lucid5", 0x02, 0, 0x59, 0x24, 0x1, 0x1, 0, 0x1, 0));
        cm.getPlayer().getClient().getSession().writeAndFlush(Packages.network.models.CField.UIPacket.InGameDirectionEvent("Map/Effect3.img/BossLucid/Lucid2", 0x02, 0, 0x59, 0x24, 0xA, 0x1, 0, 0x1, 0));
        cm.getPlayer().getClient().getSession().writeAndFlush(Packages.network.models.CField.UIPacket.InGameDirectionEvent("Map/Effect3.img/BossLucid/Lucid3", 0x02, 0, -140, 0x24, 0xB, 0x1, 0, 0x1, 0));
		cm.getPlayer().getClient().getSession().writeAndFlush(Packages.network.models.CField.UIPacket.InGameDirectionEvent("Map/Effect3.img/BossLucid/Lucid4", 0x02, 0, 0x59, 0x24, 0x1, 0x1, 0, 0x1, 0));
        statusplus(1000);
		cm.getPlayer().getClient().getSession().writeAndFlush(Packages.network.models.CField.UIPacket.setInGameDirectionMode(true, false, false));
	} else if (status == 2) {
		cm.dispose();
		cm.getPlayer().getClient().getSession().writeAndFlush(CField.blind(1, 0xff, 0xf0, 0xf0, 0xf0, 0, 0));
		return;
	} else if (status == 3) {
		cm.dispose();
		cm.getPlayer().getClient().getSession().writeAndFlush(CField.blind(1, 0xff, 0xf0, 0xf0, 0xf0, 0, 0));
    }

}

function statusplus(time) {
    cm.getPlayer().getClient().getSession().writeAndFlush(Packages.network.models.CField.UIPacket.InGameDirectionEvent("", 1, time));
}