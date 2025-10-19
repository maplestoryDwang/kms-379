package script.FieldSet;

import constants.GameConstants;
import constants.ServerConstants;
import database.DBConfig;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.fields.fieldset.FieldSet;
import objects.users.MapleCharacter;
import objects.users.stats.SecondaryStatFlag;
import objects.utils.Timer;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class TangyoonKitchen extends ScriptEngineNPC {


    /**
     * qexID : 501555
     * start=1;date=21/07/04;today=0;dodge=1
     * time=210704175444105;start=1;date=21/07/04;today=0;dodge=1
     */

    public void npc_9062550() {
        /**
         * 도전가능한 횟수제한은 없으나 경험치는 하루에 2번까지 얻을 수 있음
         * 입장시 DispelItemOptionByField 버프가 걸림
         * */
        FieldSet fieldSet = fieldSet("TangyoonKitchenEnter");
        if (fieldSet == null) {
            self.sayOk("지금은 탕윤 식당을 이용하실 수 없습니다~! 다음에 찾아와주세요!", ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
        Date lastTime = null;
        Date lastTimePuzzleMaster = null;
        Date now = null;
        try {
            lastTime = sdf.parse(getPlayer().getOneInfo(501555, "date"));
        } catch (Exception e) {
            lastTime = null;
        }
        try {
            lastTimePuzzleMaster = sdf.parse(getPlayer().getOneInfo(501600, "date"));
        } catch (Exception e) {
            lastTimePuzzleMaster = null;
        }
        try {
            now = sdf.parse(sdf.format(new Date()));
        } catch (Exception e) {
            now = null;
        }
        if ((lastTime != null && !lastTime.equals(now)) || lastTime == null) {
            getPlayer().updateOneInfo(501555, "start", "0");
            getPlayer().updateOneInfo(501555, "date", sdf.format(new Date()));
            getPlayer().updateOneInfo(501555, "today", "0");
        }
        if ((lastTimePuzzleMaster != null && !lastTimePuzzleMaster.equals(now)) || lastTimePuzzleMaster == null) {
            getPlayer().updateOneInfo(501600, "start", "0");
            getPlayer().updateOneInfo(501600, "date", sdf.format(new Date()));
            getPlayer().updateOneInfo(501600, "today", "0");
        }
        getPlayer().updateOneInfo(501555, "complete", "0");
        getPlayer().updateOneInfo(501600, "complete", "0");
        int v = self.askMenu("#b#e메이플 버라이어티#n#k 제 1탄~! #r#e탕.윤.식.당!#l#k\r\n#L2# #b#e<탕윤 식당>#n 파티플레이(1~3인으로 도전)#l\r\n#L5# #b#e<퍼즐 마스터>#n 파티 플레이(1~3인으로 도전)#l#k\r\n\r\n#L3# #b#e<탕윤 식당>#n 설명 듣기#l\r\n#L6# #b#e<퍼즐 마스터>#n 설명 듣기#l#k\r\n#L4# 오늘 남은 경험치 보상 획득 가능 횟수 확인<탕윤식당>#l\r\n#L7# 오늘 남은 경험치 보상 획득 가능 횟수 확인<퍼즐>\r\n\r\n#L100# 더 이상 궁금한 것이 없어.#l", ScriptMessageFlag.NpcReplacedByNpc);
        switch (v) {
            case 2: { //파티로도전(탕윤식당)
                if (1 == self.askYesNo("\r\n지금 #b#e<탕윤 식당>#n#k에 참여하시겠습니까~?#k\r\n\r\n(게임 중에는 해상도가 1366x768로 변경됩니다.)", ScriptMessageFlag.NpcReplacedByNpc)) {
                    if (1 == self.askYesNo("\r\n#b#e<탕윤 식당>#n#k에 참여하시면 적용되어 있는\r\n#fs16##r#e모든 버프 효과가 해제#n#k#fs12#된답니다.\r\n\r\n정말 참여하시겠어요?", ScriptMessageFlag.NpcReplacedByNpc)) {
                        int enter = fieldSet.enter(target.getId(), 0);
                        if (enter == -1)
                            self.say("알 수 없는 이유로 입장할 수 없습니다. 잠시 후에 다시 시도해 주십시오.", ScriptMessageFlag.NpcReplacedByNpc);
                        else if (enter == 1)
                            self.say("1~3인 파티를 맺어야만 #b#e파티 플레이#n#k를 할 수 있답니다.", ScriptMessageFlag.NpcReplacedByNpc);
                        else if (enter == 2)
                            self.say("파티장을 통해서만 입장할 수 있답니다! 파티장을 통해 입장해주세요~!", ScriptMessageFlag.NpcReplacedByNpc);
                        else if (enter == 3)
                            self.say("최소 " + fieldSet.minMember + "인 이상의 파티가 퀘스트를 시작할 수 있답니다.", ScriptMessageFlag.NpcReplacedByNpc);
                        else if (enter == 4)
                            self.say("파티원의 레벨은 최소 " + fieldSet.minLv + " 이상이어야 합니다.", ScriptMessageFlag.NpcReplacedByNpc);
                        else if (enter == 5) self.say("파티원이 모두 모여 있어야 시작할 수 있습니다.", ScriptMessageFlag.NpcReplacedByNpc);
                        else if (enter == 6)
                            self.say("이미 다른 파티가 안으로 들어가 퀘스트 클리어에 도전하고 있는 중이랍니다.", ScriptMessageFlag.NpcReplacedByNpc);
                    } else {
                        self.say("\r\n그러면 방송에 참여하고 싶을 때 다시 찾아와주세요~", ScriptMessageFlag.NpcReplacedByNpc);
                    }
                } else {
                    self.say("\r\n그러면 방송에 참여하고 싶을 때 다시 찾아와주세요~", ScriptMessageFlag.NpcReplacedByNpc);
                }
                break;
            }
            case 5: { //파티로도전(퍼즐마스터)
                if (1 == self.askYesNo("\r\n지금 #b#e<퍼즐 마스터>#n#k에 참여하시겠습니까~?#k", ScriptMessageFlag.NpcReplacedByNpc)) {
                    if (1 == self.askYesNo("\r\n#b#e<퍼즐 마스터>#n#k에 참여하시면 적용되어 있는\r\n#fs16##r#e모든 버프 효과가 해제#n#k#fs12#된답니다.\r\n\r\n정말 참여하시겠어요?", ScriptMessageFlag.NpcReplacedByNpc)) {
                        FieldSet puzzleMaster = fieldSet("PuzzleMasterEnter");
                        if (puzzleMaster == null) {
                            self.sayOk("지금은 퍼즐 마스터를 이용하실 수 없습니다~! 다음에 찾아와주세요!", ScriptMessageFlag.NpcReplacedByNpc);
                            return;
                        }
                        int enter = puzzleMaster.enter(target.getId(), 0);
                        if (enter == -1)
                            self.say("알 수 없는 이유로 입장할 수 없습니다. 잠시 후에 다시 시도해 주십시오.", ScriptMessageFlag.NpcReplacedByNpc);
                        else if (enter == 1)
                            self.say("1~3인 파티를 맺어야만 #b#e파티 플레이#n#k를 할 수 있답니다.", ScriptMessageFlag.NpcReplacedByNpc);
                        else if (enter == 2)
                            self.say("파티장을 통해서만 입장할 수 있답니다! 파티장을 통해 입장해주세요~!", ScriptMessageFlag.NpcReplacedByNpc);
                        else if (enter == 3)
                            self.say("최소 " + fieldSet.minMember + "인 이상의 파티가 퀘스트를 시작할 수 있답니다.", ScriptMessageFlag.NpcReplacedByNpc);
                        else if (enter == 4)
                            self.say("파티원의 레벨은 최소 " + fieldSet.minLv + " 이상이어야 합니다.", ScriptMessageFlag.NpcReplacedByNpc);
                        else if (enter == 5) self.say("파티원이 모두 모여 있어야 시작할 수 있습니다.", ScriptMessageFlag.NpcReplacedByNpc);
                        else if (enter == 6)
                            self.say("이미 다른 파티가 안으로 들어가 퀘스트 클리어에 도전하고 있는 중이랍니다.", ScriptMessageFlag.NpcReplacedByNpc);
                    } else {
                        self.say("\r\n그러면 방송에 참여하고 싶을 때 다시 찾아와주세요~", ScriptMessageFlag.NpcReplacedByNpc);
                    }
                } else {
                    self.say("\r\n그러면 방송에 참여하고 싶을 때 다시 찾아와주세요~", ScriptMessageFlag.NpcReplacedByNpc);
                }
                break;
            }
            case 3: { //설명 듣기
                self.say("#e[이벤트 기간]\r\n\r\n2021년 07월 05일 (월) 점검 후 ~ \r\n미정", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("\r\n#b#e<탕윤 식당>#n#k은 최소 1명 ~ 최대 3명의 인원이 요리를 만들고 배달까지 완료해서 제한 시간 #e30분#n 동안 #b#e50,000#n#k 포인트를 획득하는 것이 목표랍니다!", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("\r\n좌측에 위치한 #b#e<주문판>#n#k에 들어오는 주문을 확인하고\r\n레시피대로 음식을 만들어서 배달해보세요~!", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("\r\n#e[재료 수집]#n\r\n\r\n#b#e주방 공간 좌측#n#k에 위치한 #e5종#n의 재료 앞에서 #r#eSpace키#k#n\r\n키다운으로 해당하는 재료를 획득할 수 있답니다.\r\n\r\n", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("\r\n#e[재료 놓기]#n\r\n\r\n#b#e주방 공간 중앙#n#k에 위치한 #e조리대#n 앞에서 #r#eSpace키#k#n\r\n키다운으로 현재 획득한 재료를 놓을 수 있답니다.\r\n\r\n#e각 주문 번호에 해당하는 조리대#n에서만 해당 음식을\r\n조리할 수 있다는 점을 잊지 마세요!", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("\r\n#e[가공 도구 들기]#n\r\n\r\n#b#e주방 공간 우측#n#k에 위치한 #e3종#n의 가공 도구 앞에서\r\n#r#eSpace키#k#n 키다운으로 도구를 획득할 수 있답니다.\r\n\r\n#b#e가공 도구#n#k를 획득한 상태에서만 재료 가공을 할 수 있다는 점을 잊지 마세요!", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("\r\n#e[재료 가공]#n\r\n\r\n#b#e주방 공간 중앙#n#k에 위치한 #e조리대#n 앞에서 #r#eSpace키#n#k\r\n키다운으로 해당 조리대에 놓인 재료를 가공할 수 있답니다.\r\n\r\n#e각 주문 번호에 해당하는 조리대#n에서만 해당 음식을\r\n조리할 수 있다는 점을 잊지 마세요!", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("\r\n#e[음식 배달]#n\r\n\r\n#b#e음식이 완성된 조리대#n#k는 음식을 들 수 있는 상태가 되며\r\n해당 #e조리대#n 앞에서 #r#eSpace키#k#n 키다운으로 배달을 진행할 수 있답니다.\r\n\r\n우측 배달 공간에서 #e몬스터#n들의 방해를 피해 주문한 손님\r\n앞에서 #r#eSpace키#k#n 키다운으로 배달 완료!", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("\r\n#b#e진정한 크리에이터#n#k라면 역시 실전이죠!\r\n\r\n어서 직접 체험해보시는 것은 어떠신가요?", ScriptMessageFlag.NpcReplacedByNpc);
                break;
            }
            case 6: { //퍼즐마스터 설명 듣기
                self.say("#e[이벤트 기간]\r\n\r\n2021년 07월 ~ \r\n미정", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("\r\n#e#b<퍼즐 마스터>#k#n는 최소 1명 ~ 최대 3명의 인원이 #e#b총 3가지의 퍼즐#n#k을 제한 시간 #b#e30분#n#k 내에 완성하는 것을 목표로 하는 컨텐츠랍니다!", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("\r\n#b#e화면 중앙에 배치된 액자 속에#n#k 맵 이곳저곳에서 등장하는 #b#e퍼즐 조각을 정확한 위치에 배치하면 됩니다!#n#k", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("\r\n앗! 너무 어려우실 것 같나요? 걱정하지 마세요!\r\n#b#e함께하는 크리에이터들과 힘을 합친다면 충분히 가능할 거예요!#n#k", ScriptMessageFlag.NpcReplacedByNpc);
                break;
            }
            case 4: { //오늘 남은 경험치 획득 횟수
                int today = getPlayer().getOneInfoQuestInteger(501555, "today");
                if (2 - today > 0) {
                    self.say("오늘 남은 경험치 보상 획득 가능 횟수는 #b#e" + (2 - today) + "회#n#k 입니다.", ScriptMessageFlag.NpcReplacedByNpc);
                } else {
                    self.say("오늘은 더 이상 경험치 보상을 획득하실 수 없답니다.", ScriptMessageFlag.NpcReplacedByNpc);
                }
                break;
            }
            case 7: { //오늘 남은 경험치 획득 횟수(퍼즐마스터)
                int today = getPlayer().getOneInfoQuestInteger(501600, "today");
                if (2 - today > 0) {
                    self.say("오늘 남은 경험치 보상 획득 가능 횟수는 #b#e" + (2 - today) + "회#n#k 입니다.", ScriptMessageFlag.NpcReplacedByNpc);
                } else {
                    self.say("오늘은 더 이상 경험치 보상을 획득하실 수 없답니다.", ScriptMessageFlag.NpcReplacedByNpc);
                }
                break;
            }
        }
    }

    public void TyoonKitchen_npc1() {
        if (getPlayer().getMap().getId() == 993194401) {
            if (getPlayer().getOneInfoQuestInteger(501555, "today") >= 2) {
                self.say("오늘 남은 경험치 획득 횟수가 없어서 경험치를 드릴 수가 없어요 원래 있던 곳으로 돌려 보내드릴게요!", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                getPlayer().temporaryStatReset(SecondaryStatFlag.DispelItemOptionByField);
                target.registerTransferField(ServerConstants.TownMap);
            } else if (getPlayer().getOneInfoQuestInteger(501555, "complete") >= 1) {
                Calendar CAL = new GregorianCalendar(Locale.KOREA);
                int day = CAL.getTime().getDay();
                boolean doubleEXP = false;
                if (day == 6 || day == 0) { //주말
                    doubleEXP = true;
                }
                long exp = GameConstants.tangyoonExp[getPlayer().getLevel() - 200];
                if (getPlayer().getLevel() < 280) {
                    exp *= 5;
                    if (DBConfig.isGanglim) {
                        exp *= 30;
                    }
                } else if (getPlayer().getLevel() < 285) {
                    exp *= 10;
                    if (DBConfig.isGanglim) {
                        exp *= 25;
                    }
                } else if (getPlayer().getLevel() < 290) {
                    exp *= 30;
                    if (DBConfig.isGanglim) {
                        exp *= 12;
                    }
                } else if (getPlayer().getLevel() < 300) {
                    exp *= 100;
                    if (DBConfig.isGanglim) {
                        exp *= 5;
                    }
                }

                String expString = String.format("%,d", exp);
                if (DBConfig.isGanglim) {
                    if (!doubleEXP) {
                        self.say("\r\n#e#b경험치#k #r" + expString + "#k#n와 #r100 강림포인트#k#n 를드릴게요!\r\n(보상은 퇴장 시 지급됩니다)", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                    } else {
                        self.say("\r\n#e#b경험치#k #r" + expString + " * 2 (주말 탕윤 식당 경험치 2배 이벤트!)#k#n와 #r100 강림포인트#k#n를 드릴게요!\r\n(보상은 퇴장 시 지급됩니다)", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                    }
                    getPlayer().gainHongboPoint(100);
                } else {
                    if (!doubleEXP) {
                        self.say("\r\n#e#b경험치#k #r" + expString + "#k#n 를 드릴게요!\r\n(보상은 퇴장 시 지급됩니다)", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                    } else {
                        self.say("\r\n#e#b경험치#k #r" + expString + " * 2 (주말 탕윤 식당 경험치 2배 이벤트!)#k#n 를 드릴게요!\r\n(보상은 퇴장 시 지급됩니다)", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                    }
                }
                getPlayer().temporaryStatReset(SecondaryStatFlag.DispelItemOptionByField);
                target.registerTransferField(ServerConstants.TownMap);
                getPlayer().updateOneInfo(501555, "start", "0");
                getPlayer().updateOneInfo(501555, "today", String.valueOf(getPlayer().getOneInfoQuestInteger(501555, "today") + 1));
                getPlayer().updateOneInfo(501555, "complete", "0");
                if (!doubleEXP) {
                    getPlayer().gainExpLong(exp);
                } else {
                    getPlayer().gainExpLong(exp * 2);
                }
            } else {
                self.say("\r\n\r\n#e#r50,000 포인트#n#k을 벌지 못하고 나오셨다면\r\n보상을 지급해드릴 수 없답니다.\r\n\r\n다음엔 더 노력해주세요!", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                getPlayer().temporaryStatReset(SecondaryStatFlag.DispelItemOptionByField);
                target.registerTransferField(ServerConstants.TownMap);
            }
        } else {
            int v = -2;
            boolean exit = false;
            while (v != -1 && v != 0 && !exit && !getSc().isStop()) {
                v = self.askMenu("궁금한 게 있으신가요?\r\n\r\n#L1# #b#e<탕윤 식당>#b#e에 대해 물어본다.#l\r\n#L2# #b#e<탕윤 식당 진행방법>#n#k에 대해 물어본다.#l\r\n#L3# #b#e<게임 보상>#n#k에 대해 물어본다.#l\r\n#L4# 원래 있던 장소로 돌아가고 싶다.#l\r\n\r\n#L0# 궁금한 것이 없다.#l", ScriptMessageFlag.NpcReplacedByNpc);
                switch (v) {
                    case 1: { //탕윤 식당에 대해 물어본다.
                        self.say("\r\n#b#e메이플 버라이어티#n#k 첫 번째 특집! #r#e탕.윤.식.당!#n#k", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("\r\n전설의 셰프 #b#e'탕윤'#n#k님과 식당 경영을 체험할 수 있는 기회!", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("\r\n#b#e진정한 크리에이터#n#k라면 이 기회를 놓칠 수 없겠죠?!", ScriptMessageFlag.NpcReplacedByNpc);
                        break;
                    }
                    case 2: { //탕윤 식당 진행방법에 대해 물어본다.
                        self.say("\r\n#b#e<탕윤 식당>#n#k은 최소 1명 ~ 최대 3명의 인원이 요리를 만들고 배달까지 완료해서 제한 시간 #e30분#n 동안 #b#e50,000#n#k 포인트를 획득하는 것이 목표랍니다!", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("\r\n좌측에 위치한 #b#e<주문판>#n#k에 들어오는 주문을 확인하고\r\n레시피대로 음식을 만들어서 배달해보세요~!", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("\r\n#e[재료 수집]#n\r\n\r\n#b#e주방 공간 좌측#n#k에 위치한 #e5종#n의 재료 앞에서 #r#eSpace키#k#n\r\n키다운으로 해당하는 재료를 획득할 수 있답니다.\r\n\r\n", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("\r\n#e[재료 놓기]#n\r\n\r\n#b#e주방 공간 중앙#n#k에 위치한 #e조리대#n 앞에서 #r#eSpace키#k#n\r\n키다운으로 현재 획득한 재료를 놓을 수 있답니다.\r\n\r\n#e각 주문 번호에 해당하는 조리대#n에서만 해당 음식을\r\n조리할 수 있다는 점을 잊지 마세요!", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("\r\n#e[가공 도구 들기]#n\r\n\r\n#b#e주방 공간 우측#n#k에 위치한 #e3종#n의 가공 도구 앞에서\r\n#r#eSpace키#k#n 키다운으로 도구를 획득할 수 있답니다.\r\n\r\n#b#e가공 도구#n#k를 획득한 상태에서만 재료 가공을 할 수 있다는 점을 잊지 마세요!", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("\r\n#e[재료 가공]#n\r\n\r\n#b#e주방 공간 중앙#n#k에 위치한 #e조리대#n 앞에서 #r#eSpace키#n#k\r\n키다운으로 해당 조리대에 놓인 재료를 가공할 수 있답니다.\r\n\r\n#e각 주문 번호에 해당하는 조리대#n에서만 해당 음식을\r\n조리할 수 있다는 점을 잊지 마세요!", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("\r\n#e[음식 배달]#n\r\n\r\n#b#e음식이 완성된 조리대#n#k는 음식을 들 수 있는 상태가 되며\r\n해당 #e조리대#n 앞에서 #r#eSpace키#k#n 키다운으로 배달을 진행할 수 있답니다.\r\n\r\n우측 배달 공간에서 #e몬스터#n들의 방해를 피해 주문한 손님\r\n앞에서 #r#eSpace키#k#n 키다운으로 배달 완료!", ScriptMessageFlag.NpcReplacedByNpc);
                        break;
                    }
                    case 3: { //게임 보상에 대해 물어본다.
                        self.say("\r\n#b#e50,000#n#k 포인트를 획득하시면 #e일정량의 경험치#n를\r\n보상으로 드린답니다!\r\n\r\n이런 #r#e엄청난 기회#n#k를 놓칠 수는 없겠죠?", ScriptMessageFlag.NpcReplacedByNpc);
                        break;
                    }
                    case 4: {
                        getPlayer().temporaryStatReset(SecondaryStatFlag.DispelItemOptionByField);
                        if (1 == self.askYesNo("\r\n#e#b<광장>#n#k으로 돌아가시겠습니까?", ScriptMessageFlag.NpcReplacedByNpc)) {
                            target.registerTransferField(ServerConstants.TownMap);
                            exit = true;
                        }
                        break;
                    }
                }
            }
        }
    }

    public void npc_9062573() {
        if (getPlayer().getMap().getId() == 993194801) { //퇴장맵
            if (getPlayer().getOneInfoQuestInteger(501600, "today") >= 2) {
                self.say("오늘 남은 경험치 획득 횟수가 없어서 경험치를 드릴 수가 없어요 원래 있던 곳으로 돌려 보내드릴게요!", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                if (DBConfig.isHosting) {
                    target.registerTransferField(ServerConstants.TownMap);
                } else {
                    target.registerTransferField(100000000);
                }
            } else if (getPlayer().getOneInfoQuestInteger(501600, "complete") >= 1) {
                Calendar CAL = new GregorianCalendar(Locale.KOREA);
                int day = CAL.getTime().getDay();
                boolean doubleEXP = false;
                if (day == 6 || day == 0) { //주말
                    doubleEXP = true;
                }
                long exp = GameConstants.tangyoonExp[getPlayer().getLevel() - 200];
                if (getPlayer().getLevel() < 280) {
                    exp *= 5;
                    if (DBConfig.isGanglim) {
                        exp *= 30;
                    }
                } else if (getPlayer().getLevel() < 285) {
                    exp *= 10;
                    if (DBConfig.isGanglim) {
                        exp *= 25;
                    }
                } else if (getPlayer().getLevel() < 290) {
                    exp *= 30;
                    if (DBConfig.isGanglim) {
                        exp *= 12;
                    }
                } else if (getPlayer().getLevel() < 300) {
                    exp *= 100;
                    if (DBConfig.isGanglim) {
                        exp *= 5;
                    }
                }
                String expString = String.format("%,d", exp);
                if (DBConfig.isGanglim) {
                    if (!doubleEXP) {
                        self.say("\r\n#e#b경험치#k #r" + expString + "#k#n와 #r100 강림포인트#k#n 를드릴게요!\r\n(보상은 퇴장 시 지급됩니다)", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                    } else {
                        self.say("\r\n#e#b경험치#k #r" + expString + " * 2 (주말 탕윤 식당 경험치 2배 이벤트!)#k#n와 #r100 강림포인트#k#n를 드릴게요!\r\n(보상은 퇴장 시 지급됩니다)", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                    }
                    getPlayer().gainHongboPoint(100);
                } else {
                    if (!doubleEXP) {
                        self.say("\r\n#e#b경험치#k #r" + expString + "#k#n 를 드릴게요!\r\n(보상은 퇴장 시 지급됩니다)", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                    } else {
                        self.say("\r\n#e#b경험치#k #r" + expString + " * 2 (주말 퍼즐 마스터 경험치 2배 이벤트!)#k#n 를 드릴게요!\r\n(보상은 퇴장 시 지급됩니다)", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                    }
                }
                getPlayer().temporaryStatReset(SecondaryStatFlag.DispelItemOptionByField);
                if (DBConfig.isHosting) {
                    target.registerTransferField(ServerConstants.TownMap);
                } else {
                    target.registerTransferField(100000000);
                }
                getPlayer().updateOneInfo(501600, "start", "0");
                getPlayer().updateOneInfo(501600, "today", String.valueOf(getPlayer().getOneInfoQuestInteger(501600, "today") + 1));
                getPlayer().updateOneInfo(501600, "complete", "0");
                if (!doubleEXP) {
                    getPlayer().gainExpLong(exp);
                } else {
                    getPlayer().gainExpLong(exp * 2);
                }
            } else {
                getPlayer().temporaryStatReset(SecondaryStatFlag.DispelItemOptionByField);
                self.say("\r\n#e#r3개의 퍼즐을 완성하지 못하고 나오셨다면\r\n보상을 지급해드릴 수 없답니다.#k\r\n\r\n다음엔 더 노력해 주세요!", ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
                if (DBConfig.isHosting) {
                    target.registerTransferField(ServerConstants.TownMap);
                } else {
                    target.registerTransferField(100000000);
                }
            }
        } else {
            int v = -2;
            boolean exit = false;
            while (v != -1 && v != 0 && !exit && !getSc().isStop()) {
                v = self.askMenu("궁금한 게 있으신가요?\r\n\r\n#L1# #b#e<퍼즐 마스터>#b#e에 대해 물어본다.#l\r\n#L2# #b#e<퍼즐 마스터 진행 방법>#n#k에 대해 물어본다.#l\r\n#L3# #b#e<게임 보상>#n#k에 대해 물어본다.#l\r\n#L4# 원래 있던 장소로 돌아가고 싶다.#l\r\n\r\n#L0# 궁금한 것이 없다.#l", ScriptMessageFlag.NpcReplacedByNpc);
                switch (v) {
                    case 1: { //퍼즐 마스터에 대해 물어본다.
                        self.say("\r\n#b#e메이플 버라이어티#n#k 두 번째 특집! #r#e퍼즐 마스터!#n#k", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("\r\n#b#e진정한 크리에이터#n#k라면 이 기회를 놓칠 수 없겠죠?!", ScriptMessageFlag.NpcReplacedByNpc);
                        break;
                    }
                    case 2: { //진행방법
                        self.say("\r\n#e#b<퍼즐 마스터>#k#n는 최소 1명 ~ 최대 3명의 인원이 #e#b총 3가지의 퍼즐#n#k을 제한 시간 #b#e30분#n#k 내에 완성하는 것을 목표로 하는 컨텐츠랍니다!", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("\r\n#b#e화면 중앙에 배치된 액자 속에#n#k 맵 이곳저곳에서 등장하는 #b#e퍼즐 조각을 정확한 위치에 배치하면 됩니다!#n#k", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("\r\n앗! 너무 어려우실 것 같나요? 걱정하지 마세요!\r\n#b#e함께하는 크리에이터들과 힘을 합친다면 충분히 가능할 거예요!#n#k", ScriptMessageFlag.NpcReplacedByNpc);
                        break;
                    }
                    case 3: { //게임 보상에 대해 물어본다.
                        self.say("\r\n#b#e총 3가지의 퍼즐#n#k을 완성하면 레벨에 따라 #e일정량의 경험치#n를\r\n보상으로 드리고 있어요!\r\n\r\n이런 #r#e엄청난 기회#n#k를 놓칠 수는 없겠죠?", ScriptMessageFlag.NpcReplacedByNpc);
                        break;
                    }
                    case 4: {
                        if (1 == self.askYesNo("\r\n#e#b<광장>#n#k으로 돌아가시겠습니까?", ScriptMessageFlag.NpcReplacedByNpc)) {
                            getPlayer().temporaryStatReset(SecondaryStatFlag.DispelItemOptionByField);
                            if (DBConfig.isHosting) {
                                target.registerTransferField(ServerConstants.TownMap);
                            } else {
                                target.registerTransferField(100000000);
                            }
                            exit = true;
                        }
                        break;
                    }
                }
            }
        }
    }

    public void TyoonKitchen_npc2() {
        if (getPlayer().getMap().getId() != 993194500)
            self.sayOk("\r\n요리는 단순한 일이 아니라 열정이네.", ScriptMessageFlag.NpcReplacedByNpc);
    }

    public void enter_993194400() {
        getPlayer().send(CField.addPopupSay(9062561, 5000, "탕윤 식당에 오신 것을\r\n환영합니다~!", ""));
        getPlayer().send(CField.addPopupSay(9062563, 4000, "전설의 셰프의 식당에서 펼쳐지는 숨막히는 삶의 체험!", ""));
        getPlayer().send(CField.addPopupSay(9062561, 5000, "시작~! 하겠습니다~!", ""));
    }

    public void enter_993194900() {

    }

    public void TyoonKitchen_setting() {
        Field field = getPlayer().getMap();
        if (field.getFieldSetInstance() != null) {
            objects.fields.fieldset.instance.TangyoonKitchen f = (objects.fields.fieldset.instance.TangyoonKitchen) field.getFieldSetInstance();
            getPlayer().send(objects.fields.fieldset.instance.TangyoonKitchen.TangyoonOrder(1));
            getPlayer().temporaryStatSet(993194500, Integer.MAX_VALUE, SecondaryStatFlag.DispelItemOptionByField, 1);
            getPlayer().send(CWvsContext.InfoPacket.brownMessage("현재 맵에서는 잠재 능력과 에디셔널 잠재능력이 적용되지 않습니다."));
            final MapleCharacter user = getPlayer();
            Timer.EventTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    if (user.getMap().getId() == 993194500) {
                        user.send(objects.fields.fieldset.instance.TangyoonKitchen.TangyoonOrder(2));
                        user.send(objects.fields.fieldset.instance.TangyoonKitchen.TangyoonObjectPacket_1());
                        user.send(CField.environmentChange("UI/TyoonKitchen_UI.img/TyoonKitchen_UI/effect/countdown", 16));
                    }
                }
            }, 3000);

            Timer.EventTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    if (user.getMap().getId() == 993194500) {
                        user.send(objects.fields.fieldset.instance.TangyoonKitchen.TangyoonOrder(3));
                        user.send(CField.tangyoonClock(1800000));
                    }
                }
            }, 6000);

            Timer.EventTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    if (user.getMap().getId() == 993194500) {
                        user.send(CField.environmentChange("UI/UIMiniGame.img/mapleOneCard/Effect/screeneff/start", 16));
                        user.send(CField.environmentChange("Sound/MiniGame.img/oneCard/start", 5, 100));
                    }
                }
            }, 6000);
        }
    }
}
