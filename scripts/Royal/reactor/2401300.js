function act() {
	if (rm.getMap().getId() == 240060300) {
    rm.changeMusic("Bgm14/HonTale");
    rm.spawnMonster(8810215,71,260);
    rm.mapMessage(6, "동굴 깊은 곳에서 혼테일이 나타났습니다!");
	}
}