var invtype = 0;
var St = -1;
var rotation = 0;
var S1 = 0, S2 = 0, S3 = 0, S4 = 0;
var inz = null;
var txt = "";

function start() {
    St = -1;
    rotation = 0;
    S1 = 0; S2 = 0; S3 = 0; S4 = 0;
    action(1, 0, 0);
}

function send(i, str) {
    cm.getPlayer().dropMessage(6, str);
}

function Comma(i) {
    var reg = /(^[+-]?\d+)(\d{3})/;
    i += '';
    while (reg.test(i)) i = i.replace(reg, '$1' + ',' + '$2');
    return i;
}

function action(M, T, S) {
    if (M != 1) {
        cm.dispose();
        return;
    }

    if (M == 1) {
        St++;
    } else {
        St--;
    }

    if (St == 0) {
        if (!cm.getPlayer().isGM()) {
            cm.sendOk("안녕하세요? 메이플월드를 여행하는 일은 즐거우신가요?");
            cm.dispose();
            return;
        }
        cm.sendSimple("안녕하세요? 메이플월드를 여행하는 일은 즐거우신가요?\r\n"
            + "#L0##r대화를 끝낸다.#l\r\n"
            + "#L1##b아이템의 옵션을 변경한다.(장비)#l\r\n"
            + "#L3##b아이템의 옵션을 변경한다.(치장)#l\r\n"
            + "#L2##b잠재능력 코드를 확인한다.#l");
    }

    else if (St == 1) {
        S1 = S;
        switch (S1) {
            case 1:
                inz = cm.getInventory(1);
                invtype = 1;
                txt = "현재 #b#h ##k 님이 보유하고 있는 장비 아이템 목록입니다. 인벤토리에 정렬된 순서로 출력되었으니 #r옵션을 변경하고 싶은 아이템#k을 선택해주세요.\r\n#b#fs11#";
                for (w = 0; w <= inz.getSlotLimit(); w++) {
                    if (!inz.getItem(w)) continue;
                    txt += "#L" + w + "##i" + inz.getItem(w).getItemId() + ":# #t" + inz.getItem(w).getItemId() + "##l\r\n";
                }
                cm.sendSimple(txt);
                break;
            case 3:
                inz = cm.getInventory(6);
                invtype = 6;
                txt = "현재 #b#h ##k 님이 보유하고 있는 치장 아이템 목록입니다. 인벤토리에 정렬된 순서로 출력되었으니 #r옵션을 변경하고 싶은 아이템#k을 선택해주세요.\r\n#b#fs11#";
                for (w = 0; w <= inz.getSlotLimit(); w++) {
                    if (!inz.getItem(w)) continue;
                    txt += "#L" + w + "##i" + inz.getItem(w).getItemId() + ":# #t" + inz.getItem(w).getItemId() + "##l\r\n";
                }
                cm.sendSimple(txt);
                break;
            case 2:
                showPotentialCode();
                break;
            default:
                cm.dispose();
                break;
        }
    }

    else if (St > 1) {
        if (rotation != -1) {
            switch (St) {
                case 2: S2 = S; break;
                case 3: S3 = S; break;
                case 4: S4 = S; break;
            }
            if (St == 4 && rotation == 2) {
                inz = cm.getInventory(invtype).getItem(S2);
                inz.setArc(3000);
                switch (S3) {
                    case 0: inz.setStr(S4); break;
                    case 1: inz.setDex(S4); break;
                    case 2: inz.setInt(S4); break;
                    case 3: inz.setLuk(S4); break;
                    case 4: inz.setHp(S4); break;
                    case 5: inz.setMp(S4); break;
                    case 6: inz.setWatk(S4); break;
                    case 7: inz.setMatk(S4); break;
                    case 8: inz.setWdef(S4); break;
                    case 9: inz.setMdef(S4); break;
                    case 10: inz.setAcc(S4); break;
                    case 11: inz.setAvoid(S4); break;
                    case 12: inz.setSpeed(S4); break;
                    case 13: inz.setJump(S4); break;
                    case 14: inz.setFire(S4); break;
                    case 15: inz.setEnchantStr(S4); break;
                    case 16: inz.setEnchantDex(S4); break;
                    case 17: inz.setEnchantInt(S4); break;
                    case 18: inz.setEnchantLuk(S4); break;
                    case 19: inz.setEnchantHp(S4); break;
                    case 20: inz.setEnchantMp(S4); break;
                    case 21: inz.setEnchantWatk(S4); break;
                    case 22: inz.setEnchantMatk(S4); break;
                    case 23: inz.setEnchantWdef(S4); break;
                    case 24: inz.setEnchantMdef(S4); break;
                    case 25: inz.setEnchantAcc(S4); break;
                    case 26: inz.setEnchantAvoid(S4); break;
                    case 27: inz.setLevel(S4); break;
                    case 28: inz.setUpgradeSlots(S4); break;
                    case 29: inz.setEnhance(S4); break;
                    case 30: inz.setAmazingequipscroll(true); break;
                    case 31: inz.setBossDamage(S4); break;
                    case 32: inz.setIgnorePDR(S4); break;
                    case 33: inz.setTotalDamage(S4); break;
                    case 34: inz.setAllStat(S4); break;
                    case 35: inz.setReqLevel(S4); break;
                    case 36: inz.setState(S4); break;
                    case 37: inz.setPotential1(S4); break;
                    case 38: inz.setPotential2(S4); break;
                    case 39: inz.setPotential3(S4); break;
                    case 40: inz.setPotential4(S4); break;
                    case 41: inz.setPotential5(S4); break;
                    case 42: inz.setPotential6(S4); break;
                    case 43: inz.setArcLevel(S4); break;
                    case 44: inz.setArc(S4); break;
                    case 45: inz.setArcEXP(S4); break;
                }
                if (invtype == 1) {
                    cm.getPlayer().forceReAddItem(inz, Packages.client.inventory.MapleInventoryType.EQUIP);
                } else if (invtype == 6) {
                    cm.getPlayer().forceReAddItem(inz, Packages.client.inventory.MapleInventoryType.CASH);
                } else {
                    cm.sendOk("오류 발생");
                    cm.dispose();
                    return;
                }

                rotation = 0;
                St = 2;
            }
        } else {
            S2 = S2;
            rotation++;
        }
        addItemInfo();
    }
}

function showPotentialCode() {
    var list = [
        "　< 주요 스탯% 관련 잠재능력 코드 >",
        "　힘　: +3%(10041)　힘　: +6%(20041)　힘　: +9%(30041)　힘　: +12%(40041)",
        "　덱스: +3%(10042)　덱스: +6%(20042)　덱스: +9%(30042)　덱스: +12%(40042)",
        "　인트: +3%(10043)　인트: +6%(20043)　인트: +9%(30043)　인트: +12%(40043)",
        "　럭　: +3%(10044)　럭　: +6%(20044)　럭　: +9%(30044)　럭　: +12%(40044)",
        "　올스텟: +9%(40086)　　 올스텟: +12%(40081)　　 올스텟: +20%(60002)",
        "　< 기타 스탯% 관련 잠재능력 코드 >",
        "　최대체력: +3%(10045)　최대체력: +6%(20045)　최대체력: +9%(30045)　최대체력: +12%(40045)",
        "　최대마나: +3%(10046)　최대마나: +6%(20046)　최대마나: +9%(30046)　최대마나: +12%(40046)",
        "　회피치　: +3%(10048)　회피치　: +6%(20048)　회피치　: +9%(30048)　회피치　: +12%(40048)",
        "　< 무기 관련 잠재능력 코드 >",
        "　데미지: +6%(20070)　데미지: +9%(30070)　데미지: +12%(40070)",
        "　공격력: +6%(20051)　공격력: +9%(30051)　공격력: +12%(40051)",
        "　마력　: +6%(20052)　마력　: +9%(30052)　마력　: +12%(40052)",
        "　< 몬스터 방어율 무시 관련 잠재능력 코드 >",
        "　+15%(10291)　+20%(20291)　+30%(30291)　+35%(40291)　+40%(40292)",
        "　< 보스 몬스터 공격시 데미지 관련 잠재능력 코드 >",
        "　+20%(30601)　+25%(40601)　+30%(30602)　+35%(40602)　+40%(40603)",
        "　< 크리티컬 관련 잠재능력 코드 >",
        "　크리티컬 발동: +8%(20055)　+10%(30055)　+12%(40055)",
        "　크리티컬 최소 데미지: +15%(40056)　크리티컬 최대 데미지: +15%(40057)",
        "　< 장신구 · 방어구 관련 잠재능력 코드 >",
        "　메소 획득량: +20%(40650)　아이템 획득 확률: +20%(40656)",
        "　피격 후 무적시간: 1초(20366)　2초(30366)　3초(40366)",
        "　< 쓸만한 스킬 관련 잠재능력 코드 >",
        "　(유니크)　헤이스트(31001)　미스틱 도어(31002)　샤프 아이즈(31003)　하이퍼 바디(31004)",
        "　(레전드리)　컴뱃 오더스(41005)　어드밴스드 블레스(41006)　윈드 부스터(41007)"
    ];
    for (var i = 0; i < list.length; i++) {
        send(20, list[i]);
    }
    cm.getPlayer().dropMessage(1, "채팅창을 최대로 확대하면 모든 내용이 표시됩니다.");
    cm.dispose();
}

// 나머지 addItemInfo 함수는 기존과 동일하게 사용하시면 됩니다.
