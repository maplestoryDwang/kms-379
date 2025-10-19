importPackage(Packages.objects.users);

function enter(pi) {
    pi.warp(141030000,0);
    pi.getPlayer().cancelEffectFromBuffStat(SecondaryStatFlag.RIDING_VEHICLE);
    pi.getPlayer().dropMessage(-1, "[항해 성공] 오른쪽의 따보를 찾아가 따보의 부탁을 들어주세요.");
    pi.getPlayer().dropMessage(5, "[항해 성공] 오른쪽의 따보를 찾아가 따보의 부탁을 들어주세요.");
    return true;
}
