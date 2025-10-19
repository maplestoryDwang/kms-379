function enter(pi) {
    if (pi.getPlayerCount(310030210) > 0) {
         pi.getPlayer().dropMessage(-1, "이미 누가 탑승중 이므로 잠시 기다려주세요.");
         pi.getPlayer().dropMessage(5, "이미 누가 탑승중 이므로 잠시 기다려주세요.");
    } else {
    pi.TimeMoveMap(310030210,304000000,20);
    return true;
    }
}