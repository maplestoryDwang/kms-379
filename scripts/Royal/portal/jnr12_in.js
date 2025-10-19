function enter(pi) {
    if ((pi.getMap().getCharactersSize() >= 2 && pi.getMap().getCharactersSize() <= 4) || pi.getMap(926110401).getCharactersSize() > 0) {
	pi.warpParty(926110401,0);
	pi.partyMessage(6, "유레테가 기계장치를 조작하자 거대한 괴물이 나타났다. 유레테는 기분나쁘게 웃으며 사라졌다.")
    } else {
	pi.playerMessage(5, "파티원 전원이 이곳에 모여있지 않습니다.");
    }
}