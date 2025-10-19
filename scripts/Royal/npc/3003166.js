importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);
importPackage(java.text);

importPackage(Packages.objects.item);
importPackage(Packages.objects.users);
importPackage(Packages.objects.utils);
importPackage(Packages.constants);
importPackage(Packages.network.models);
importPackage(Packages.objects.wz.provider);

var limit = 3;
var easy_map = 921170050;
var hard_map = 921170100;
var sel2 = -1;
var difficulty_0 = "쉬운 난이도";
var difficulty_1 = "보통 난이도";
var difficulty_2 = "어려운 난이도";
var rank_0 = "S";
var rank_1 = "A";
var rank_2 = "B";

function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, sel) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (cm.getPlayer().getLevel() < 215) {
        cm.sendOk("배고픈 무토 컨텐츠는 215 레벨 이상만 가능합니다.");
        cm.dispose();
        return; 
    }
        if (cm.getPlayer().getMap().getId() == 450002024) {
                if (status == 0) {
                        var msg = "#e#b<로얄 메이플 - 배고픈 무토>#n\r\n";
                        msg += "#b무토#k를 도와주셔서 감사해요!\r\n";
                        msg += "#b#L0#<배고픈 무토> 보상을 받는다.#k#l";
                        cm.sendSimple(msg);
                } else if (status == 1) {
                        var time = cm.getPlayer().getMutoClearTime() / 1000;
                        if (time == -1) {
                            cm.sendNext("도대체 어떻게 여기를 온거지?");
                            cm.warp(450002023);
                            cm.dispose();
                            return;
                        }
        var count = calcRewardCount(cm.getPlayer().getMutoClearDifficulty(), cm.getPlayer().getMutoClearRank());
        if (count <= 0) {
            cm.warp(450002023);
                            cm.dispose();
                            return;
        }
                        var second = parseInt(time % 60);
                        time /= 60;
                        var minute = parseInt(time % 60);
                        time /= 60;
                        var msg = "도와 주셔서 감사해요!\r\n#e#r";
                        var diff = cm.getPlayer().getMutoClearDifficulty();
                        msg += (diff == 0 ? difficulty_0 : diff == 1 ? difficulty_1 : difficulty_2) + "#n#k를 #b#e" + minute + "분 " + second + "초#n#k 만에 클리어 하셨군요!";
                        cm.sendNext(msg);
                } else if (status == 2) {
                        var rank = cm.getPlayer().getMutoClearRank();
        var msg = "자! 여기 제가 준비한 선물을 받아주세요\r\n#e#b클리어 등급: #r" + (rank == 0 ? rank_0 : rank == 1 ? rank_1 : rank_2) + "#e#k급\r\n";
                        msg += "#i1712002##e#b#z1712002##k " + calcRewardCount(cm.getPlayer().getMutoClearDifficulty(), rank) + "개\r\n";
                        msg += "#b경험치:" + calcRewardExp(cm.getPlayer().getMutoClearDifficulty(), rank);
                        cm.sendSimple(msg);
                } else if (status == 3) {
                        var count = calcRewardCount(cm.getPlayer().getMutoClearDifficulty(), cm.getPlayer().getMutoClearRank());
                        if (!cm.canHold(1712002, count)) {
                                cm.sendNext("여행자님! 인벤토리 슬롯이 부족한거 같은데요?");
                                cm.dispose();
                                return;
                        }
                        cm.warp(450002023);
                        for (var i = 0; i < count; ++i) {
                                cm.gainItem(1712002, 1);
                        }
                        cm.gainExp(calcRewardExp(cm.getPlayer().getMutoClearDifficulty(), rank));
                        cm.getPlayer().setKeyValue2("has_muto_reward", 1);
                        var stack = cm.getPlayer().getStackTodayCount("muto_reward");
                        if (stack < count) {
                                cm.getPlayer().mutoTodayHighReward(count);
                        }
                        cm.dispose();
                }
        } else {
                if (status == 0) {
                        var msg = "#e#b<로얄 메이플 - 배고픈 무토>#n\r\n";
                        msg += " #b무토#k를 도와 #r굴라#k를 물리치는 걸 도와 주시겠어요?\r\n";
                        msg += "#r※10분간 플레이가 진행되며 중간에 이탈할 수 없습니다.\r\n\r\n";
                        msg += "#b#L0#<배고픈 무토>에 입장한다.#l\r\n";
                        msg += "#L1#시미아 에게서 설명을 듣는다.#l\r\n";
                        msg += "#L2#오늘의 남은 도전횟수를 확인한다.#l#k\r\n\r\n\r\n";
                        msg += "#e* 1회 클리어 후 즉시 완료가 가능합니다.\r\n";
                        msg += "* 오늘의 최고 보상 기록:\r\n";
                        msg += "#i1712002##e#b#z1712002# " + cm.getPlayer().getStackTodayCount("muto_reward") + "개";
                        cm.sendSimple(msg);
                } else if (status == 1) {
                        if (sel == 0) {
                                var msg = "#e#b<로얄 메이플 - 배고픈 무토>#n#k\r\n";
                                msg += "어떤 #b난이도#k로 도전 하시겠어요?\r\n\r\n";
                                msg += "#b#L0#쉬운 난이도#l\r\n";
                                msg += "#L1#일반 난이도#l\r\n";
                                msg += "#L2#어려운 난이도#l#k\r\n";
                                cm.sendSimple(msg);
                        } else if (sel == 1) {
                                var msg = "여행자님! #r굴라#k의 침공이 시작되었어요! #b무토#k에게 맛있는 음식을 만들어 줘서 #r굴라#k를 물리치도록 도와주세요!\r\n\r\n";
                                msg += "#e<로얄 메이플 - 배고픈 무토>\r\n\r\n";
                                msg += "1. 참가 인원: #n1~4인#e\r\n";
                                msg += "2. 제한 시간: #n10분#e\r\n";
                                msg += "3. 1일 클리어 가능 횟수: #n3회 (클리어 할 때만 누적)#e\r\n";
                                msg += "4. 보상: #i1712002##b#z1712002##k#n +경험치\r\n\r\n";
                                msg += "#L3#이전으로 돌아간다.#l";
                                cm.sendSimple(msg);
                        } else if (sel == 2) {
                                cm.sendNext("#h #님의 오늘 남은 도전 횟수는 #r" + (limit - cm.getPlayer().getStackTodayCount("hungry_muto")) + "회#k 입니다.");
                                sel2 = 4;
                        }
                } else if (status == 2) {
                        if (sel2 == 4 || sel == 3) {
                                start();
                                sel2 = -1;
                        } else {
                                var difficulty = sel;
                                if(!cm.CountCheck("hungry_muto", limit)) {
                                        cm.sendOk("하루에 "+limit+"번만 가능하답니다.");
                                        cm.dispose();
                                        return;
                                }
                                if (cm.getPlayer().getParty() == null) {
                                        cm.sendOk("파티를 꾸리고 도전하시길 바랍니다.");
                                        cm.dispose();
                                        return;
                                }
                                if (!isPartyLeader()) {
                                        cm.sendOk("파티장이 아니면 신청할 수 없습니다.");
                                        cm.dispose();
                                        return;
                                }
                                if (!cm.allMembersHere()) {
                                        cm.sendOk("파티원이 전원 이곳에 모여있어야 합니다.");
                                        cm.dispose();
                                        return;
                                }
                                if (cm.getPlayerCount(easy_map) > 0) {
                                        cm.sendOk("이미 누군가가 도전중입니다.\r\n#b다른 채널을 이용해 주세요.#k");
                                        cm.dispose();
                                        return;
                                }
                                if (cm.getPlayerCount(hard_map) > 0) {
                                        cm.sendOk("이미 누군가가 도전중입니다.\r\n#b다른 채널을 이용해 주세요.#k");
                                        cm.dispose();
                                        return;
                                }
                                
                                var it = cm.getClient().getChannelServer().getPartyMembers(cm.getParty()).iterator();
                                var countPass = true;
                                while (it.hasNext()) {
                                        var chr = it.next();
                                        if (!CC(chr,"hungry_muto", limit)) {
                                                countPass = false;
                                                break;
                                        }
                                }
                                if (!countPass) {
                                        cm.sendOk("파티원 중 입장 횟수가 남아있지 않은 파티원이 있습니다.");
                                        cm.dispose();
                                        return;
                                } else {
                                var it = cm.getClient().getChannelServer().getPartyMembers(cm.getParty()).iterator();
                                var countPass = true;
                                while (it.hasNext()) {
                                        var chr = it.next();
                                        //AC(chr, "muto_easy"); // 클리어시 차감으로 변경
                                }
                                }
                                cm.resetMap(difficulty == 1 ? easy_map : hard_map, false);
                                var em = cm.getEventManager("hungry_muto");
                                if (em == null) {
                                        cm.sendNext("배고픈 무토를 진행하는 도중에 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
                                        cm.dispose();
                                        return;
                                }
                                var eim = em.readyInstance();
                                eim.setProperty("StartMap", difficulty == 1 ? easy_map : hard_map);
                                eim.setProperty("Difficulty", difficulty);
                                eim.setProperty("Leader", cm.getPlayer().getParty().getLeader().getName());
                                //eim.startEventTimer(1000 * 60 * 10);
                                eim.registerParty(cm.getPlayer().getParty(), cm.getPlayer().getMap());
                                cm.dispose();
                        }
                }
        }
}

function AC(player, boss) {
    player.CountAdd(boss);
}

function CC(player, boss, limit) {
    return player.CountCheck(boss, limit);
}

function isPartyLeader() {
    if (cm.getPlayer().getParty().getLeader().getId() == cm.getPlayer().getId())
        return true;
    else
        return false;
}

function calcRewardCount(difficulty, rank) {
        if (difficulty == 0) {
                return 1;
        } else if (difficulty == 1) {
                if (rank == 0) {
                        return 5;
                } else if (rank == 1) {
                        return 3;
                } else {
                        return 2;
                }
        } else if (difficulty == 2) {
                if (rank == 0) {
                        return 10;
                } else if (rank == 1) {
                        return 8;
                } else {
                        return 6;
                }
        }
        return 0;
}

function calcRewardExp(difficulty, rank) {
        if (difficulty == 0) { // 경험치 직접 설정해주세요.
                if (rank == 0) {
                        return 2427144000;
                } else if (rank == 1) {
                        return 1000000000;
                } else if (rank == 2) {
                        return 500000000;
                }
        } else if (difficulty == 1) {
                if (rank == 0) {
                        return 3427144000;
                } else if (rank == 1) {
                        return 1500000000;
                } else if (rank == 2) {
                        return 1000000000;
                }
        } else if (difficulty == 2) {
                if (rank == 0) {
                        return 4230754390;
                } else if (rank == 1) {
                        return 2000000000;
                } else if (rank == 2) {
                        return 1500000000;
                }
        }
        return 0;
}