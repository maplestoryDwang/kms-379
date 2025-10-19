importPackage(java.util);
importPackage(java.lang);
importPackage(java.io);

importPackage(Packages.objects.utils);
importPackage(Packages.objects.item);
importPackage(Packages.constants);

importPackage(Packages.objects.fields.child.dreambreaker);

var status = -1;
var sss = false;
var select = -1;
var selectStage = 0;
var quantity = 0;
var clear = false;
var coin = 0;
var 코인 = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, sel) {
    if (mode == 0 && type == 6 && sel == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        status--;
    } else if (mode == 1) {
        status++;
    }
    if (status == 0) {
        if (cm.getPlayer().getMapId() == 921171100) { // 보상맵
            var stage = cm.getPlayer().getOneInfoQuestInteger(15901, "stage");
            var selectedStage = cm.getPlayer().getOneInfoQuestInteger(15901, "selectedStage");
            if (stage == selectedStage) {
                cm.sendNext("음! 도전에 #r실패#k했군요?\r\n힘들다면 조금 낮은 단계부터 도전해 보는게 어때요?");
            } else if (stage > selectedStage) {
                clear = true;
                var clearstage = stage - 1; // 성공한 스테이지

                talk = "와~ #b" + clearstage + "스테이지#k 까지 돌파하셨군요? 대단해요~\r\n\r\n";

                var b_best = cm.getPlayer().getOneInfoQuestInteger(15901, "best_b");
                var b_besttime = cm.getPlayer().getOneInfoQuestInteger(15901, "besttime_b");
                var best = cm.getPlayer().getOneInfoQuestInteger(15901, "best");
                var besttime = cm.getPlayer().getOneInfoQuestInteger(15901, "besttime");
                var b_rank = cm.getPlayer().getOneInfoQuestInteger(15901, "rank_b");
                var b_score = b_best * 1000 + (180 - b_besttime);
                var score = clearstage * 1000 + (180 - besttime);
                DreamBreakerRank.editRecord(cm.getPlayer().getName(), best, besttime);
                var rank = DreamBreakerRank.getRank(cm.getPlayer().getName());
                if ((rank > b_rank || score > b_score) && (rank > 0 && rank <= 100)) {
                    talk += "이번주 #r#e랭킹 신기록#k#n이군요! 랭킹에 등록해 드릴게요\r\n\r\n";
                }

                if (score > b_score) {
                    talk += "#r게다가 개인 신기록#k을 세우셨군요!";
                }
                cm.sendNext(talk);
            }
        } else {
            talk = "#b#e<드림브레이커>#k#n\r\n"
                talk += "아웅.... 언제쯤 편안하게 잠을 잘 수 있을까?\r\n\r\n"
                talk += "#L0# #b<드림브레이커>에 도전한다.#l\r\n"
                talk += "#L1# 나의 기록을 확인한다.#l\r\n"
                talk += "#L2# 주간 랭킹을 확인한다.#l\r\n"
                talk += "#L4# 드림 코인을 교환한다.#l\r\n";
            talk += "#L3# 설명을 듣는다.#l";
            cm.sendSimple(talk);
        }
    } else if (status == 1) {
        if (cm.getPlayer().getMapId() == 921171100) {
            if (clear) {
                quantity = cm.getPlayer().getOneInfoQuestInteger(15901, "stage") - 1;
                if (!cm.canHold(4036068, quantity)) {
                    cm.sendNext("인벤토리 공간을 확보하고 다시 말 걸어주세요!");
                    cm.dispose();
                    return;
                }
                cm.sendNext("자 여기 #i4036068##b#z4036068##k #r" + quantity + "개#k를 선물로 드릴게요!\r\n\r\n그럼 다음에 또 도와주세요!");
            } else {
                cm.warp(ServerConstants.TownMap, 0);
                cm.dispose();
                return;
            }
        } else {
            if (sel == 0) {
                var bestStage = cm.getPlayer().getOneInfoQuestInteger(15901, "best");
                talk = "당신의 최고 기록은 #r#e" + bestStage + "단계#k#n군요?\r\n"
                    talk += "현재 도전할 수 있는 스테이지는 다음과 같아요.\r\n\r\n"
                    floor = bestStage - (bestStage % 10);
                for (var i = floor; i >= 0; i -= 10) {
                    i = i == 0 ? 1 : i;
                    talk += "#L" + (i) + "##b" + (i) + "#k 단계\r\n";
                }
                cm.sendSimple(talk);
            } else if (sel == 1) {
                talk = "당신의 #e<드림브레이커 기록>#n을 알려드릴게요!\r\n\r\n"
                    talk += "#e개인 최고기록 : #b" + cm.getPlayer().getOneInfoQuestInteger(15901, "best") + "스테이지#k#n\r\n"
                    talk += "#e오늘 입장 횟수 : #b" + cm.getPlayer().GetCount("dream_breaker") + " 번#k#n\r\n"
                    var lastWeek = cm.getPlayer().getOneInfoQuestInteger(20200128, "last_week_dream_breaker");
                var lw = "기록 없음";
                if (lastWeek != 0) {
                    lw = lastWeek + "위";
                }
                talk += "#e지난주 랭킹 : #b" + lw + "#k#n\r\n"
                cm.sendNext(talk);
                cm.dispose();
            } else if (sel == 2) {
                cm.displayDreamBreakerRank();
                cm.dispose();
            } else if (sel == 3) {
                sss = true;
                talk = "무엇을 알려드릴까요?\r\n\r\n";
                talk += "#L0# #e드림브레이커 규칙#l\r\n"
                talk += "#L1# 드림포인트 획득과 스킬 사용#l\r\n"
                talk += "#L2# 드림브레이커 보상#l\r\n"
                talk += "#L3# 드림브레이커 랭킹#l\r\n";
                talk += "#L4# 설명을 듣지 않는다."
                cm.sendSimple(talk);
            } else if (sel == 4) {
                if (cm.haveItem(4036068, 300)) {
                    coin = 10;
                } else if (cm.haveItem(4036068, 270)) {
                    coin = 9;
                } else if (cm.haveItem(4036068, 240)) {
                    coin = 8;
                } else if (cm.haveItem(4036068, 210)) {
                    coin = 7;
                } else if (cm.haveItem(4036068, 180)) {
                    coin = 6;
                } else if (cm.haveItem(4036068, 150)) {
                    coin = 5;
                } else if (cm.haveItem(4036068, 120)) {
                    coin = 4;
                } else if (cm.haveItem(4036068, 90)) {
                    coin = 3;
                } else if (cm.haveItem(4036068, 60)) {
                    coin = 2;
                } else if (cm.haveItem(4036068, 30)) {
                    coin = 1;
                } else if (cm.haveItem(4310227, 300)) {
                    coin = 10;
                } else if (cm.haveItem(4310227, 270)) {
                    coin = 9;
                } else if (cm.haveItem(4310227, 240)) {
                    coin = 8;
                } else if (cm.haveItem(4310227, 210)) {
                    coin = 7;
                } else if (cm.haveItem(4310227, 180)) {
                    coin = 6;
                } else if (cm.haveItem(4310227, 150)) {
                    coin = 5;
                } else if (cm.haveItem(4310227, 120)) {
                    coin = 4;
                } else if (cm.haveItem(4310227, 90)) {
                    coin = 3;
                } else if (cm.haveItem(4310227, 60)) {
                    coin = 2;
                } else if (cm.haveItem(4310227, 30)) {
                    coin = 1;
                }
                cm.sendGetNumber("#i4036068##b#z4036068##k을 #i1712003##r#z1712003##k으로 교환 할래요?\r\n(#b드림 코인 30개#k = #r아케인심볼 : 레헬른 1개#k)\r\n 최대 #e#r" + coin + "#n#k개 교환 가능.)", coin, 1, coin);
                코인 = 1;
            }
        }
    } else if (status >= 2 && sss) {
        if (status == 2 && select == -1) {
            select = sel;
        }
        name = ["드림브레이커 규칙", "드림포인트", "드림브레이커 보상", "드림브레이커 랭킹"];
        dialogue = [
            ["루시드의 악몽을 멈추기 위해선 #b#e숙면의 오르골#k#n을 지키고 #r#e악몽의 오르골#k#n을 파괴해야 해요.",
                "총 #e5개의 방#n 중에서 #b#e숙면의 오르골#k#n이 더 많으면 #b#e노란색 게이지가 왼쪽으로#k#n 차오르고, #r#e악몽의 오르골#k#n이 더 많으면 #r#e보라색 게이지가 오른쪽으로#k#n 차오를 거예요.\r\n\r\n결과적으로 제한시간 #e3분 이내에 #b노란색 게이지#k#n를 다 채우면 스테이지를 클리어하게 되죠.",
                "드림브레이커는 #e하루 3회#n까지 입장 할 수 있어요.",
                "그럼 저를 도와 #r#e루시드의 악몽#k#n을 멈춰주세요!"
            ],
            ["#e드림포인트#n는 드림브레이커에서 #r#e스테이지를 클리어 할때마다#k#n 얻는 점수로, #b#e전략스킬#k#n을 발동핼 때 사용해요.\r\n\r\n획득되는 드림포인트는 #e매 10스테이지#n마다 #e10씩 증가#n하고 최대 #b#e3000점#k#n까지 누적할 수 있답니다.",
                "사용할 수 있는 #b#e전략스킬#n#k은 다음과 같아요.\r\n\r\n#e<게이지 홀드>#n\r\n드림포인트: 200 소모 / 5초간 게이지의 이동을 멈춤.\r\n\r\n#e<자각의 종>#n\r\n드림포인트: 300 소모 / 랜덤한 악몽의 오르골 1개를 제거.\r\n\r\n#e<헝겊인형 소환>#n\r\n드림포인트: 400 소모 / 주변의 몬스터를 도발하는 헝겊인형 소환. (15초간 유지)\r\n\r\n#e<폭파>#n\r\n드림포인트: 900 소모 / 모든 몬스터를 처치, 10초간 재소환을 막음.",
                "하나의 스테이지에서는 #r#e동일한 스킬을 두 번 사용할 수 없으니#k#n 스킬 사용은 신중하게 하세요!"
            ],
            ["스테이지를 클리어 하면 #e최종적으로 도달한 스테이지#n 만큼의 코인을 얻을 수 있어요.",
                "마지막으로 일일 #b도전 횟수를 모두 소모#k한 뒤 #b레헬른 중심가#k오른쪽에 위치한 #r주간 랭킹 1~5위 유저#k를 찾아가면 #b하루 1번 다양한 선물#k을 받을 수 있으니 꼭 찾아가 보세요!"
            ],
            ["스테이지를 클리어 하면 #b도달 스테이지 / 클리어 시간#k을 기준으로 #b주간 최고기록#k인 경우 #r자동으로 랭킹에 등록#k돼요.",
                "주간랭킹은 #b매 주 월요일 자정#k에 초기화 돼요.\r\n랭킹 정산을 위해 #r일요일 오후 11시 30분 부터 월요일 자정 12시 30분 까지는#k 입장이 제한 된답니다.",
                "그리고 주간 랭킹 #e1~5등#n에 기록된 캐릭터는 #b레헬른 중심가#k 한켠에 그 모습이 1주일간 기록되고 여러 용사님들에게 선물을 드리는 역할을 수행해요.",
                "#r최고의 드림브레이커#k가 되어 용사님들로부터 선망의 대상이 되어 보세요!"
            ]
        ];

        if (status - 2 == dialogue[select].length) {
            cm.dispose();
            return;
        }
        talk = "#e<" + name[select] + ">#n\r\n\r\n";
        talk += dialogue[select][parseInt(status - 2)];
        if (status == 2) {
            cm.sendNext(talk);
        } else {
            cm.sendNextPrev(talk);
        }
    } else if (status == 2) {
        if (코인 == 1) {
            var leftslot = cm.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot();
            if (leftslot < sel) {
                cm.sendOk("인벤토리 공간이 최소한 " + sel + "칸은 필요합니다. 장비 탭의 공간을 " + sel + "칸 이상 비워주세요.");
                cm.dispose();
                return;
            }
            if (cm.haveItem(4036068, sel * 30)) {
                cm.gainItem(4036068, -sel * 30);
                for (var i = 0; i < sel; ++i) {
                    cm.gainItem(1712003, 1);
                }
                cm.sendOk("자 여기 #i1712003# #b#z1712003##k " + sel + "개를 받아");
                cm.dispose();
                return;
            } else if (cm.haveItem(4310227, sel * 30)) {
                cm.gainItem(4310227, -sel * 30);
                for (var i = 0; i < sel; ++i) {
                    cm.gainItem(1712003, 1);
                }
                cm.sendOk("자 여기 #i1712003# #b#z1712003##k " + sel + "개를 받아");
                cm.dispose();
                return;
            } else {
                cm.sendOk("재료도 없으면서 선넘지마!");
                cm.dispose();
                return;
            }
        }
        if (cm.getPlayer().getMapId() == 921171100) {
            cm.gainItem(4036068, quantity);
            cm.warp(ServerConstants.TownMap);
            cm.dispose();
            return;
        } else {
            selectStage = sel;
            talk = "<드림브레이커> #b" + selectStage + "단계#k에 도전할 건가요?\r\n\r\n";
            talk += "#b오늘 도전 횟수 " + cm.getPlayer().GetCount("dream_breaker") + " / 3";
            cm.sendYesNo(talk);
        }
    } else if (status == 3) {
        if (cm.getPlayerCount(921171000) > 0) {
            cm.sendOk("이미 누군가 도전 중이에요.");
            cm.dispose();
            return;
        }

        if (cm.getPlayer().GetCount("dream_breaker") >= 3) {
            cm.sendOk("오늘은 더 이상 #b#e<드림 브레이커>#k#n에 도전할 수 없어요.\r\n\r\n#r#e(1일 3회 입장 가능)#k#n");
            cm.dispose();
        } else {
            cm.getPlayer().updateOneInfo(15901, "selectedStage", "" + selectStage);
            cm.getPlayer().CountAdd("dream_breaker");
            cm.resetMap(921171000);
            cm.warp(921171000);
            cm.dispose();
        }
    }
}
