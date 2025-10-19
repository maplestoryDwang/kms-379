function enter(pi) {
    pi.getClient().getSession().writeAndFlush(Packages.networkd.models.CField.playSE("Sound/MiniGame.img/prize"));
    pi.getClient().getSession().writeAndFlush(Packages.network.models.CField.environmentChange("monsterPark/clearF", 0x13));
    pi.getClient().getSession().writeAndFlush(Packages.network.models.CField.enforceMSG("스테이지 클리어다. 로비로 이동하지.", 212, 2000));
    Packages.server.Timer.MapTimer.getInstance().schedule(function () {
        pi.getClient().getSession().writeAndFlush(Packages.networkd.models.CField.playSE("Sound/MiniGame.img/Catch"));
    }, 1000)
    var stage = (pi.getPlayer().getMapId() - 993001000) / 10;
    pi.getPlayer().RegisterPlatformerRecord(stage);
    pi.getPlayer().warpdelay(993001000, 2);
    Packages.server.Timer.MapTimer.getInstance().schedule(function () {
        pi.getClient().getSession().writeAndFlush(Packages.network.models.CField.UIPacket.SetIngameDirectionMode(false, true, false, false));
    }, 2000)
}