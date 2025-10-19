function enter(pi) {
    pi.warp(304000100,0);
    pi.getPlayer().dropMessage(-1, "레이저에 닿지 않게 조심히 이동하세요...");
    pi.getPlayer().dropMessage(5, "레이저에 닿지 않게 조심히 이동하세요...");
    return true;
}