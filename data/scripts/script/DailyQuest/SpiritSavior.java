package script.DailyQuest;

import network.models.CField;
import objects.fields.fieldset.childs.SpiritSaviorEnter;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SpiritSavior extends ScriptEngineNPC {

    /**
     * qexID : 16214
     * count -> 일일 몇번했는지 횟수
     * date -> 21/06/16 언제 시행했는지
     * coin -> 오늘 코인 몇개얻었는지
     * todayrecord -> 오늘 최고기록
     */

    /**
     * qexID : 16215
     * point -> 몇점인지
     * play -> 몇번시도했는지?
     * saved 몇마리 구하는중인가
     * life 100부터시작
     * chase 따라오는 괴물새끼 단계?
     * 8644301 : 따라오는 괴물새끼 1단계
     */


    public void spiritSavior_NPC() {
        initNPC(MapleLifeFactory.getNPC(3003381));
        SpiritSaviorEnter fieldSet = (SpiritSaviorEnter) fieldSet("SpiritSaviorEnter");
        if (fieldSet == null) {
            self.sayOk("지금은 스피릿 세이비어를 진행할 수 없습니다.");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
        Date lastTime = null;
        Date now = null;
        try {
            lastTime = sdf.parse(getPlayer().getOneInfo(16214, "date"));
            now = sdf.parse(sdf.format(new Date()));
        } catch (Exception e) {
            lastTime = null;
        }
        if (lastTime == null) {
            getPlayer().updateOneInfo(16214, "date", new SimpleDateFormat("yy/MM/dd").format(new Date()));
            getPlayer().updateOneInfo(16214, "count", "0");
        }
        if (lastTime != null && !lastTime.equals(now)) {
            getPlayer().updateOneInfo(16214, "count", "0");
        }
        // self.say("내 친구들 많이 구해줬남?\r\n구출 점수는 #b#e10500점#n#k 이구남!\r\n이 정도면 #r#e스피릿 코인 10개#n#k를 줄 수 있겠담!");
        // self.say("다음에 또 도와달람!");

        //만약에 코인받을만큼 구하지못했을경우
        //self.say("음... 내 친구들을 많이 구하지 못했구남!");
        //self.say("다음에는 친구들을 많이 구해달람!");

        //즉시완료대사
        //self.askYesNo("#b#ho!##k #b#e<스피릿 세이비어>#n#k를 즉시 완료할 수 있담. 즉시 완료 시 입장 횟수가 #r#e1회 차감#n#k되니까 잊지 마람!\r\n#r(취소 시 입장이 진행됩니다.)#k#e\r\n\r\n◆오늘 남은 즉시 완료 횟수 : #b1회#k\r\n◆즉시 완료 보상 : #b#t4310235:# 10개");
        //self.sayOk("즉시 완료 보상을 주었담!");

        //다햇을경우
        //self.say("오늘은 더 이상 #b#e<스피릿 세이비어>#n#k에 도전할 수 없담.\r\n내일 다시 찾아와 달람.\r\n\r\n#r#e(1일 3회 입장 가능)#n#k");
        if (getPlayer().getMap().getId() == 921172400) { // 보상맵
            if (lastTime != null && lastTime.equals(now)) {
                int point = getPlayer().getOneInfoQuestInteger(16215, "point");
                if (point >= 1000) {
                    int todayCoin = getPlayer().getOneInfoQuestInteger(16214, "coin");
                    self.say("내 친구들 많이 구해줬남?\r\n구출 점수는 #b#e" + point + "점#n#k 이구남!\r\n이 정도면 #r#e스피릿 코인 " + (point / 1000) + "개#n#k를 줄 수 있겠담!\r\n(하루 최대 코인 획득량 " + todayCoin + " / 30)", ScriptMessageFlag.NoEsc);
                    self.say("다음에 또 도와달람!", ScriptMessageFlag.NoEsc);
                    int giveCoin = (point / 1000);
                    if (todayCoin + giveCoin > 30) {
                        int what = giveCoin - ((todayCoin + giveCoin) - 30);
                        giveCoin = what;
                    }
                    if (getPlayer().getOneInfoQuestInteger(16215, "point") > 0) {
                        if (target.exchange(4310235, giveCoin) > 0) {
                            getPlayer().updateOneInfo(16214, "coin", String.valueOf(getPlayer().getOneInfoQuestInteger(16214, "coin") + giveCoin));
                            getPlayer().updateOneInfo(16215, "point", "0");
                            target.registerTransferField(450005000);
                        } else {
                            self.sayOk("기타창을 비워달람!", ScriptMessageFlag.NoEsc);
                        }
                    } else {
                        target.registerTransferField(450005000);
                    }
                } else {
                    self.say("음... 내 친구들을 많이 구하지 못했구남!");
                    self.say("다음에는 친구들을 많이 구해달람!");
                    target.registerTransferField(450005000);
                }
            } else {
                self.say("음... 내 친구들을 많이 구하지 못했구남!");
                self.say("다음에는 친구들을 많이 구해달람!");
                target.registerTransferField(450005000);
            }
        } else {
            int v = -1;
            int count = getPlayer().getOneInfoQuestInteger(16214, "count");
            int todayrecordCoin = getPlayer().getOneInfoQuestInteger(16214, "todayrecord") / 1000;
            int canD = 3;
            if (getPlayer().getLevel() >= 230) canD--;
            if (getPlayer().getLevel() >= 235) canD--;
            if (lastTime != null && lastTime.equals(now) && getPlayer().getOneInfoQuestInteger(16214, "count") > 0) {
                if (getPlayer().getLevel() >= 230) {
                    v = self.askMenu("#b#e<스피릿 세이비어>#n#k\r\n 내 친구들을 어서 구해줬음 좋겠담!\r\n\r\n#b#L0# <스피릿 세이비어>에 도전한다.#l\r\n#L1# 스피릿 코인을 교환한다.#l\r\n#L2# 설명을 듣는다.#l#k\r\n\r\n\r\n#e*" + canD + "회 클리어 후 즉시 완료가 가능합니다.\r\n*오늘의 최고 보상 기록:   \r\n#i4310235##b#e#t4310235:##n #e" + todayrecordCoin + "개");
                } else {
                    v = self.askMenu("#b#e<스피릿 세이비어>#n#k\r\n 내 친구들을 어서 구해줬음 좋겠담!\r\n\r\n#b#L0# <스피릿 세이비어>에 도전한다.#l\r\n#L1# 스피릿 코인을 교환한다.#l\r\n#L2# 설명을 듣는다.#l#k");
                }
            } else {
                v = self.askMenu("#b#e<스피릿 세이비어>#n#k\r\n 내 친구들을 어서 구해줬음 좋겠담!\r\n\r\n#b#L0# <스피릿 세이비어>에 도전한다.#l\r\n#L1# 스피릿 코인을 교환한다.#l\r\n#L2# 설명을 듣는다.#l#k\r\n\r\n\r\n#e*2회 클리어 후 즉시 완료가 가능합니다.\r\n*오늘의 최고 보상 기록:   \r\n#i4310235##b#e#t4310235:##n #e0개");
            }
            switch (v) {

                case 0: { //<스피릿 세이비어>에 도전한다.
                    if (count >= canD && count < 3) {
                        if (1 == self.askYesNo("#b#ho!##k #b#e<스피릿 세이비어>#n#k를 즉시 완료할 수 있담. 즉시 완료 시 입장 횟수가 #r#e1회 차감#n#k되니까 잊지 마람!\r\n#r(취소 시 입장이 진행됩니다.)#k#e\r\n\r\n◆오늘 남은 즉시 완료 횟수 : #b" + (3 - count) + "회#k\r\n◆즉시 완료 보상 : #b#t4310235:# " + todayrecordCoin + "개")) {
                            int todayCoin = getPlayer().getOneInfoQuestInteger(16214, "coin");
                            int giveCoin = todayrecordCoin;
                            boolean fuck = false;
                            if (todayCoin + giveCoin > 30) {
                                fuck = true;
                                int what = giveCoin - ((todayCoin + giveCoin) - 30);
                                giveCoin = what;
                            }
                            if (exchange(4310235, giveCoin) > 0) {
                                getPlayer().updateOneInfo(16214, "coin", String.valueOf(getPlayer().getOneInfoQuestInteger(16214, "coin") + giveCoin));
                                getPlayer().updateOneInfo(16214, "count", String.valueOf(count + 1));
                                if (fuck) {
                                    self.sayOk("즉시 완료 보상을 주었담!\r\n오늘은 #r#e30개 이상#n#k의 코인을 받아 가서 이 만큼 밖에 못 줄것 같담...\r\n");
                                } else {
                                    self.sayOk("즉시 완료 보상을 주었담!");
                                }
                            } else {
                                self.sayOk("기타창을 비워달람!", ScriptMessageFlag.NoEsc);
                            }
                        } else {
                            int enter = fieldSet.enter(target.getId(), 0);
                            if (enter == -1) self.say("알 수 없는 이유로 입장할 수 없습니다. 잠시 후에 다시 시도해 주십시오.");
                            else if (enter == 1) self.sayOk("<스피릿 세이비어>는 혼자서만 도전할 수 있담.\r\n파티를 해제하고 다시 찾아와 줬음 좋겠담.");
                            else if (enter == 2) self.say("레벨은 최소 " + fieldSet.minLv + " 이상이어야 내 친구들을 도와줄 수 있담.");
                            else if (enter == 3) self.say("현재 모든 인스턴스가 가득차 도전할 수 없담.");
                            else if (enter == -2)
                                self.sayOk("오늘은 더 이상 #b#e<스피릿 세이비어>#n#k에 도전할 수 없담.\r\n내일 다시 찾아와 달람.\r\n\r\n#r#e(1일 3회 입장 가능)#n#k");
                        }
                    } else {
                        if (1 == self.askYesNo("어서 내 친구들을 구해주면 좋겠담. 지금 도전 할 건감?\r\n\r\n#b오늘 도전 횟수 " + count + " / 3#k")) {
                            int enter = fieldSet.enter(target.getId(), 0);
                            if (enter == -1) self.say("알 수 없는 이유로 입장할 수 없습니다. 잠시 후에 다시 시도해 주십시오.");
                            else if (enter == 1) self.sayOk("<스피릿 세이비어>는 혼자서만 도전할 수 있담.\r\n파티를 해제하고 다시 찾아와 줬음 좋겠담.");
                            else if (enter == 2) self.say("레벨은 최소 " + fieldSet.minLv + " 이상이어야 내 친구들을 도와줄 수 있담.");
                            else if (enter == 3) self.say("현재 모든 인스턴스가 가득차 도전할 수 없담.");
                            else if (enter == -2)
                                self.sayOk("오늘은 더 이상 #b#e<스피릿 세이비어>#n#k에 도전할 수 없담.\r\n내일 다시 찾아와 달람.\r\n\r\n#r#e(1일 3회 입장 가능)#n#k");
                        } else {
                            self.sayOk("너무 늦으면 내 친구들을 영영 못 볼 수도 있담...");
                        }
                    }
                    break;
                }
                case 1: { //스피릿 코인을 교환한다. (수정해야해!!)
                    if (getPlayer().getItemQuantity(4310235, false) > 0) {
                        int number = self.askNumber("#b#i4310235:##t4310235##k을 #r#i1712004:##t1712004##k랑 바꿀램?\r\n(#b#t4310235# 1개#k = #r#t1712004# 1개#k)\r\n\r\n최대 #r#e" + getPlayer().getItemQuantity(4310235, false) + "개#n#k 교환 가능.", 1, 1, getPlayer().getItemQuantity(4310235, false));
                        if (exchange(4310235, -number, 1712004, number) > 0) {
                            self.sayOk("자! 여기 #b#i1712004:##t1712004#" + number + "개#k를 줄겜!\r\n다음에 또 도와달람!");
                        } else {
                            self.sayOk("장비창이 부족하거나 스피릿 코인이 부족하담! 확인해달람!");
                        }
                    } else {
                        self.sayOk("스피릿 코인이 없으면 아케인심볼을 교환할수 없담!");
                    }
                    break;
                }
                case 2: { //설명을 듣는다
                    int vvv = -2;
                    while (vvv != 2 && vvv != 100 && vvv != -1 && !getSc().isStop()) {
                        vvv = self.askMenu("무엇을 알고 싶은감?\r\n#L0# #e스피릿 세이비어 규칙#n#l\r\n#L1# #e스피릿 세이비어 보상#n#l\r\n#L2# #e일일 퀘스트 간편하게 하기#n#l"/*\r\n#L3# #e힘내라! 보너스 스피릿 코인이란?#n#l*/ + "\r\n#L100# #e설명을 듣지 않는다.#n#l");
                        switch (vvv) {
                            case 0: //스피릿 세이비어 규칙
                                self.say("#e<스피릿 세이비어 규칙>#n\r\n\r\n#e제한시간이 끝나기 전에 / 방어도가 모두 깎이기 전에#n  최대한 많은 #b#e속박된 돌의 정령#n#k을 구출해야 한담!\r\n#b#e속박된 돌의 정령#n#k을 구출하면 #r#e채집/NPC대화키를 눌러서#n#k 데리고 다닐 수 있담!");
                                self.say("#e<스피릿 세이비어 규칙>#n\r\n\r\n친구들은 #b#e최대 5명 까지#n#k 데리고 다닐 수 있담!\r\n친구들을 처음 시작했던 #b#e구출지점까지 무사히 데려오면#n#k\r\n#e'구출점수'#n를 얻을 수 있담!\r\n#b#e한 번에 많은 친구들을 구출할 수록#n#k 높은 점수를 얻는담!\r\n\r\n#e1명-200점\r\n2명-500점\r\n3명-1000점\r\n4명-1500점\r\n5명-2500점#n");
                                self.say("#e<스피릿 세이비어 규칙>#n\r\n\r\n하지만 #r#e나쁜 정령#n#k들이 친구들을 쉽게 데려가도록 내버려 두지 않을 거담.\r\n일정 시간이 지나면 맵 곳곳을 돌아다니는 #r#e정령의 파편#n#k이 생겨날거담. 그 녀석들에게 맞으면 #b#e방어도#n#k가 깎이게 된담.");
                                self.say("#e<스피릿 세이비어 규칙>#n\r\n\r\n또 우리 친구들을 구출하면 #r#e맹독의 정령#n#k이 너를 추격하기 시작할거담!\r\n#r#e맹독의 정령#n#k은 많은 친구들이 구출 될 수록 #e점점 커지고 빨라진담.#n 녀석에게 공격받으면 많은 방어도를 잃게되고 데리고 있던 친구들도 모두 사라지게 된담.. 녀석에게 부딪히지 않도록 조심하람!");
                                getSc().flushSay();
                                break;
                            case 1: //스피릿 세이비어 보상
                                self.say("#e<스피릿 세이비어 보상>#n\r\n\r\n친구들을 구출해서 #b#e구출 점수#n#k를 얻으면 #e1000 포인트#n당 #b#i4310235:##t4310235##k 1개를 얻을 수 있담.");
                                self.say("#e<스피릿 세이비어 보상>#n\r\n\r\n#b#i4310235:##t4310235# 3개#k를 나에게 가져오면 #r#i1712004##t1712004# 1개#k로 교환해 주겠담.");
                                self.say("#e<스피릿 세이비어 보상>#n\r\n\r\n도전은 #b#e하루에 3번#n#k할 수 있고#r#e 하루에 최대 30개의 코인#n#k을 얻을 수 있담. 그럼 내 친구들을 잘 부탁한담!");
                                getSc().flushSay();
                                break;
                            case 2: //일일 퀘스트 간편하게 하기
                                if (getPlayer().getLevel() < 230) {
                                    self.sayOk("새로운 아케인리버 지역의 일일 퀘스트를 수행할 수 있게 되면 #e<스피릿 세이비어>#n 임무를 더욱 손쉽게 완수 할 수 있도록 매일 #b#e<스피릿 세이비어> 즉시 완료#n#k 기회를 준담. #e즉시 완료#n#k를 이용하면 오늘 내가 기록한 최고 기록을 기준으로 즉시 임무를 완료할 수 있담. 단, 경험치 보상 및 업적과 관련된 내용은 기록되지 않으니 이 점 잊지마람!\r\n#r*즉시 완료 시 힘내라 보너스 코인은 획득 할 수 없습니다.#k");
                                } else if (getPlayer().getLevel() >= 230 && getPlayer().getLevel() < 235) {
                                    self.sayOk("새로운 아케인리버 지역의 일일 퀘스트를 수행할 수 있게 되면 #e<스피릿 세이비어>#n 임무를 더욱 손쉽게 완수 할 수 있도록 매일 #b#e<스피릿 세이비어> 즉시 완료#n#k 기회를 준담. #e즉시 완료#n#k를 이용하면 오늘 내가 기록한 최고 기록을 기준으로 즉시 임무를 완료할 수 있담. 단, 경험치 보상 및 업적과 관련된 내용은 기록되지 않으니 이 점 잊지마람!\r\n#r*즉시 완료 시 힘내라 보너스 코인은 획득 할 수 없습니다.#k\r\n\r\n\r\n#e#b오늘 가능한 <스피릿 세이비어> 즉시 완료 횟수 (0/1)#n#k\r\n ▶모라스 지역: #r#e일일 퀘스트 수행 가능#n#k\r\n ▶에스페라 지역: #e#k일일 퀘스트 수행 불가#n#k");
                                } else {
                                    self.sayOk("새로운 아케인리버 지역의 일일 퀘스트를 수행할 수 있게 되면 #e<스피릿 세이비어>#n 임무를 더욱 손쉽게 완수 할 수 있도록 매일 #b#e<스피릿 세이비어> 즉시 완료#n#k 기회를 준담. #e즉시 완료#n#k를 이용하면 오늘 내가 기록한 최고 기록을 기준으로 즉시 임무를 완료할 수 있담. 단, 경험치 보상 및 업적과 관련된 내용은 기록되지 않으니 이 점 잊지마람!\r\n#r*즉시 완료 시 힘내라 보너스 코인은 획득 할 수 없습니다.#k\r\n\r\n\r\n#e#b오늘 가능한 <스피릿 세이비어> 즉시 완료 횟수 (0/2)#n#k\r\n ▶모라스 지역: #r#e일일 퀘스트 수행 가능#n#k\r\n ▶에스페라 지역: #e#r일일 퀘스트 수행 가능#n#k");
                                }
                                break;
                            case 3: //힘내라! 보너스 스피릿 코인이란?
                                //self.say("#e<스피릿 세이비어 보상>#n\r\n\r\n하루에 3번을 모두 도전 하고 얻은 스피릿 코인의 총 개수가 1개 이상 9개 미만일 경우에는 응원의 차원에서 #b#e힘내라! 보너스 스피릿 코인#n#k이 총 획득 개수에 따라 차등으로 추가로 지급되니 포기하지 않길 바란담!");
                                break;
                            case 100: //설명을 듣지 않는다.
                                break;
                        }
                    }
                    break;
                }
            }
        }
    }

    public void enter_SavingSpirit() {
        if (getPlayer().getMap().getFieldSetInstance() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
            Date lastTime = null;
            Date now = null;
            try {
                lastTime = sdf.parse(getPlayer().getOneInfo(16214, "date"));
                now = sdf.parse(sdf.format(new Date()));
            } catch (Exception e) {
                lastTime = null;
            }
            if (lastTime != null && !lastTime.equals(now)) {
                getPlayer().updateOneInfo(16214, "count", "0");
                getPlayer().updateOneInfo(16214, "date", new SimpleDateFormat("yy/MM/dd").format(new Date()));
                getPlayer().updateOneInfo(16214, "coin", "0");
                getPlayer().updateOneInfo(16214, "todayrecord", "0");
            }
            getPlayer().updateOneInfo(16214, "date", new SimpleDateFormat("yy/MM/dd").format(new Date()));
            getPlayer().updateOneInfo(16214, "count", String.valueOf(getPlayer().getOneInfoQuestInteger(16214, "count") + 1)); //시작하자마자 카운트+1
            getPlayer().updateInfoQuest(16215, "point=0;play=1;saved=0;life=100;chase=0;");
            getPlayer().send(CField.environmentChange("event/start", 19, 0));
            getPlayer().send(CField.environmentChange("Dojang/clear", 5, 100));
            objects.fields.fieldset.instance.SpiritSavior spiritSavior = (objects.fields.fieldset.instance.SpiritSavior) getPlayer().getMap().getFieldSetInstance();
            spiritSavior.spawnRandomPrison();
            spiritSavior.spawnBomb();
        }
    }

    public void out_SavingSpirit() {
        if (getPlayer().getOneInfoQuestInteger(16215, "play") > 0) {
            int todayrecord = getPlayer().getOneInfoQuestInteger(16214, "todayrecord");
            int point = getPlayer().getOneInfoQuestInteger(16215, "point");
            if (todayrecord < point && point >= 1000) {
                getPlayer().updateOneInfo(16214, "todayrecord", String.valueOf(point));
            }
            getPlayer().send(CField.environmentChange("Map/Effect2.img/event/gameover", 16, 0));
            getPlayer().updateOneInfo(16215, "play", "0");
        }
    }
}
