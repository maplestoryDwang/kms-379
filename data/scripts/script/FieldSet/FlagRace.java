package script.FieldSet;

import network.models.CField;
import network.models.CWvsContext;
import objects.context.guild.GuildContentsType;
import objects.fields.Field;
import objects.fields.fieldset.FieldSet;
import objects.fields.fieldset.childs.FlagRaceN1Enter;
import objects.fields.fieldset.childs.FlagRaceN2Enter;
import objects.fields.fieldset.childs.FlagRaceN3Enter;
import objects.fields.fieldset.instance.FlagRaceN1;
import objects.fields.fieldset.instance.FlagRaceN2;
import objects.fields.fieldset.instance.FlagRaceN3;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.item.Item;
import scripting.ScriptMessageFlag;
import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FlagRace extends ScriptEngineNPC {


    public void flag_NPC() {
        String course = "한낮의 설원";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date1 = sdf.parse("20210802");
            Date date2 = new Date();
            long time = date2.getTime() - date1.getTime();
            long day = TimeUnit.MILLISECONDS.toDays(time);
            if ((day / 7) % 3 == 0) {
                course = "한낮의 설원";
            } else if ((day / 7) % 3 == 1) {
                course = "석양의 설원";
            } else if ((day / 7) % 3 == 2) {
                course = "한밤의 설원";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int v = self.askMenu("#fs11#\r\n먼 세계에서 게임을 만들 땐 이렇게 인기가 좋지 않았는데... 나의 아름다운 월드에서 플래그 레이스를 즐기고 싶은거지?\r\n\r\n이번 주의 코스 : #r#e" + course + "#n#k\r\n\r\n#b#L0#플래그 레이스에 도전하고 싶어요.#l#k\r\n#b#L1#플래그 레이스 연습 대기실로 이동시켜 주세요.#l#k\r\n#b#L2#플래그 레이스에 대해서 알려주세요.#l#k", ScriptMessageFlag.NpcReplacedByNpc);
        switch (v) {
            case 0: //플래그 레이스에 도전하고 싶어요.
                if (getPlayer().getGuild() == null) {
                    self.say("#fs11#\r\n플래그 레이스는 #r주간 길드 미션 포인트를 1 이상 획득한 자#k만이 참가할 수 있어. 조건을 갖춘 뒤에 다시 시도해 줘.", ScriptMessageFlag.NpcReplacedByNpc);
                } else {
                    if (getPlayer().getGuild().getPointLogByType(GuildContentsType.WEEK_MISSIONS, getPlayer()) < 0) {
                        self.say("#fs11#\r\n플래그 레이스는 #r주간 길드 미션 포인트를 1 이상 획득한 자#k만이 참가할 수 있어. 조건을 갖춘 뒤에 다시 시도해 줘.", ScriptMessageFlag.NpcReplacedByNpc);
                    } else {
                        FieldSet fieldSet = fieldSet("FlagRaceEnter");
                        if (fieldSet == null) {
                            self.sayOk("#fs11#지금은 플래그 레이스을 이용할 수 없어.");
                            return;
                        }
                        int enter = fieldSet.enter(target.getId(), 0);
                        if (enter == -1) self.say("#fs11#알 수 없는 이유로 입장할 수 없습니다. 잠시 후에 다시 시도해 주십시오.");
                        else if (enter == 1) self.sayOk("#fs11#<플래그레이스>는 혼자서만 도전할 수 있어.\r\n파티를 해제하고 다시 찾아오도록 해");
                        else if (enter == 2) self.say("#fs11#레벨은 최소 " + fieldSet.minLv + " 이상이어야 플래그레이스를 할 수 있어.");
                        else if (enter == -2)
                            self.sayOk("#fs11#플래그레이스는 매시 30분에 개최 돼 시간을 확인하고 다시 시도해 줘");
                        else if (enter == -3)
                            self.sayOk("#fs11#일요일 23시 ~ 월요일 01시는 노블레스 포인트 정산시간이라 연습모드만 가능해~! 시간을 확인하고 다시 시도해 줘");
                    }
                }
                break;
            case 1: //플래그 레이스 연습 대기실로 이동시켜 주세요.
                if (1 == self.askYesNo("#fs11##b플래그 레이스 연습 대기실#k로 지금 이동할래?", ScriptMessageFlag.NpcReplacedByNpc)) {
                    registerTransferField(942003050); //플래그 레이스 : 연습 대기실
                }
                break;
            case 2: //플래그 레이스에 대해서 알려주세요.
                int vv = self.askMenu("#fs11#쿠쿠쿠, 플래그 레이스에 대해서 뭐가 그렇게 알고 싶은데?\r\n\r\n#b#L0#플래그 레이스가 뭔가요?#l\r\n#b#L1#플래그 레이스를 하면 뭐가 좋죠?#l\r\n#L4#플래그 레이스의 랭킹 포인트 기준을 알고 싶어요.#l\r\n#L3#아무 것도 아니에요.#l", ScriptMessageFlag.NpcReplacedByNpc);
                switch (vv) {
                    case 0: //플래그 레이스가 뭔가요?
                        self.say("#fs11#\r\n내 위대한 창작물인 플래그 레이스에 대해 알고 싶은 거야?", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11#\r\n룰은 간단해. #r매시 30분마다#k 개최되고, #r주간 길드 미션 포인트가 1 이상인 길드원#k만이 참가할 수 있어.", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11#\r\n빠르게 골인 지점까지 달리면 된다고. 물론 장애물 같은게 조----------------------------금은 있을 수도 있어.", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11#\r\n플래그 레이스 내에서는 오로지 이동과 점프만으로 정정당당하게 승부... 하는 건 아니고, 코스 내에서만 사용할 수 있는 플래그 스킬들이 있어.", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11#\r\n플래그 스킬들과 맵에 배치된 장치들을 잘 조합하면 통상의 상황에서는 갈 수 없는 길을 가거나 더 빠르게 코스를 주파할 수 있을 거야.", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11#\r\n아, 그리고 레이스 입장 시 모든 변신 상태가 해제되고 레이스 중 변신 포션을 사용하더라도 변신이 되지 않을 거야. 소중한 물약을 날려버릴 수도 있으니 조심하라고.", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11#\r\n101레벨 이상만 참여할 수 있으니 확인해 두라고. 이미 101레벨이 넘은지 한참 됐다면야 뭐...", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11#\r\n그럼, 힘내라고.", ScriptMessageFlag.NpcReplacedByNpc);
                        break;
                    case 1: //플래그 레이스를 하면 뭐가 좋죠?
                        self.say("#fs11#\r\n뭐가 좋으냐고? 길이 있으니까 달리고, 장애물이 있으니까 넘는 거지 꼭 좋을 게 있어야 하나?", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11#\r\n...농담이고, 길드 스킬 중 노블레스 스킬에 직접 투자할 수 있는 SP를 일주일간의 플래그 레이스 길드 랭킹에 따라 받을 수 있지.", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11##b#e1위 : 25개의 SP\r\n2위 : 22개의 SP\r\n3위 : 20개의 SP\r\n4위 ~ 10위 : 18개의 SP\r\n상위 10% : 16개의 SP\r\n상위 20% : 14개의 SP\r\n상위 30% : 12개의 SP\r\n상위 40% : 10개의 SP\r\n상위 50% : 9개의 SP\r\n상위 60% : 8개의 SP\r\n상위 70% : 7개의 SP\r\n상위 80% : 6개의 SP\r\n1,000포인트 이상 : 5개의 SP#n#k\r\n\r\n#r#e※ 단, 1,000포인트 이상 획득하지 못하면 순위 안에 들었더라도 보상을 받을 수 없습니다.#k#n", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11#\r\n플래그 레이스 길드 랭킹은 각 길드원의 플래그 레이스 점수를 합산한 점수로 매겨지지. 달리기 실력이 빼어난 동료가 많은 길드가 더 높은 순위를 가져갈 수 있어.", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11#\r\n너의 플래그 레이스 점수는 주간 최고 기록으로 결정돼. 지금의 기록이 마음에 들지 않으면 언제든 다시 도전하라고. 물론 열려 있을 때 말이야.", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11#\r\n아, 길드에서 탈퇴하거나 추방당하면 너의 주간 최고 기록이 초기화되고 길드의 합산 점수도 그만큼 차감되니까 반드시 조심하라고.", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11#\r\n그리고 랭킹은 영원하지 않아. 매주 월요일 0시에 랭킹과 노블레스 스킬은 모두 초기화되고 그때까지 합산된 랭킹을 반영해서 노블레스 스킬SP를 주니까. 이번 주에 실패했다고 해서 너무 실망하지 말라구.", ScriptMessageFlag.NpcReplacedByNpc);
                        break;
                    case 4: //플래그 레이스의 랭킹 포인트 기준을 알고 싶어요.
                        self.say("#fs11#\r\n이제 플래그 레이스에서 얻을 수 있는 랭킹 포인트는 세 바퀴를 모두 완주하는데 걸린 시간에 따라 결정돼. 남들과 상관 없이 너만 잘 달리면 높은 점수를 얻을 수 있다는 뜻이지.", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11#\r\n랭킹 포인트 기준은 일주일 간 너의 최고 기록을 기준으로 결정돼. 더 높은 기록을 얻어 기록을 갱신할 수 있으니까 기록이 아쉬우면 다시 도전하라고.", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11#\r\n마지막으로, 플래그 레이스의 코스는 매주 월요일 0시마다 바뀌게 돼. 코스마다 점수 기준이 다르니까 꼭 참고해서 똑똑하게 달리라고.", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11#\r\n#e[한낮의 설원 랭킹 포인트 기준]#n\r\n\r\n2분 10초 미만 : #b#e1,000 포인트#n#k\r\n2분 10초 ~ 2분 15초 미만 : #b#e800 포인트#n#k\r\n2분 15초 ~ 2분 20초 미만 : #b#e650 포인트#n#k\r\n2분 20초 ~ 2분 25초 미만 : #b#e550 포인트#n#k\r\n2분 25초 ~ 2분 30초 미만 : #b#e450 포인트#n#k\r\n2분 30초 ~ 2분 35초 미만 : #b#e400 포인트#n#k\r\n2분 35초 ~ 2분 40초 미만 : #b#e350 포인트#n#k\r\n2분 40초 ~ 2분 50초 미만 : #b#e300 포인트#n#k\r\n2분 50초 ~ 3분 00초 미만 : #b#e250 포인트#n#k\r\n3분 00초 ~ 3분 10초 미만 : #b#e200 포인트#n#k\r\n3분 10초 이상 : #b#e100 포인트#n#k", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11#\r\n#e[석양의 설원 랭킹 포인트 기준]#n\r\n\r\n1분 50초 미만 : #b#e1,000 포인트#n#k\r\n1분 50초 ~ 1분 55초 미만 : #b#e800 포인트#n#k\r\n1분 55초 ~ 2분 00초 미만 : #b#e650 포인트#n#k\r\n2분 00초 ~ 2분 05초 미만 : #b#e550 포인트#n#k\r\n2분 05초 ~ 2분 10초 미만 : #b#e450 포인트#n#k\r\n2분 10초 ~ 2분 15초 미만 : #b#e400 포인트#n#k\r\n2분 15초 ~ 2분 20초 미만 : #b#e350 포인트#n#k\r\n2분 20초 ~ 2분 30초 미만 : #b#e300 포인트#n#k\r\n2분 30초 ~ 2분 40초 미만 : #b#e250 포인트#n#k\r\n2분 40초 ~ 2분 50초 미만 : #b#e200 포인트#n#k\r\n2분 50초 이상 : #b#e100 포인트#n#k", ScriptMessageFlag.NpcReplacedByNpc);
                        self.say("#fs11#\r\n#e[한밤의 설원 랭킹 포인트 기준]#n\r\n\r\n2분 30초 미만 : #b#e1,000 포인트#n#k\r\n2분 30초 ~ 2분 35초 미만 : #b#e800 포인트#n#k\r\n2분 35초 ~ 2분 40초 미만 : #b#e650 포인트#n#k\r\n2분 40초 ~ 2분 45초 미만 : #b#e550 포인트#n#k\r\n2분 45초 ~ 2분 50초 미만 : #b#e450 포인트#n#k\r\n2분 50초 ~ 2분 55초 미만 : #b#e400 포인트#n#k\r\n2분 55초 ~ 3분 00초 미만 : #b#e350 포인트#n#k\r\n3분 00초 ~ 3분 10초 미만 : #b#e300 포인트#n#k\r\n3분 10초 ~ 3분 20초 미만 : #b#e250 포인트#n#k\r\n3분 20초 ~ 3분 30초 미만 : #b#e200 포인트#n#k\r\n3분 30초 이상 : #b#e100 포인트#n#k", ScriptMessageFlag.NpcReplacedByNpc);
                        break;
                    case 3: //아무것도 아니에요
                        self.sayOk("#fs11#\r\n그래. 무엇을 하건 하지 않건 너의 자유지.", ScriptMessageFlag.NpcReplacedByNpc);
                        break;
                }
                break;
        }
    }

    public void mad_Designer() {
        switch (getPlayer().getMapId()) {
            case 942003000:
            case 942003001:
            case 942003002: {
                if (getPlayer().getOneInfoQuestInteger(32581, "mode") > 1) {
                    if (1 == self.askAccept("#fs11#쿠쿠쿠, 연습 코스는 어땠어? 다시 연습 대기실로 돌려보내 줄까?")) {
                        registerTransferField(942003050); //플래그 레이스 : 연습 대기실
                    }
                } else {
                    int v = self.askMenu("#fs11#플래그 레이스는 재미있게 즐겼어? 무슨 일이야?\r\n#b#L0#이 곳에서 나가고 싶어요.#l\r\n#b#L1#플래그 레이스에 대해 알려주세요.#l");
                    switch (v) {
                        case 1: { //플래그 레이스에 대해서 알려주세요.
                            int vv = self.askMenu("#fs11#쿠쿠쿠, 플래그 레이스에 대해서 뭐가 그렇게 알고 싶은데?\r\n\r\n#b#L0#플래그 레이스가 뭔가요?#l\r\n#b#L1#플래그 레이스를 하면 뭐가 좋죠?#l\r\n#L4#플래그 레이스의 랭킹 포인트 기준을 알고 싶어요.#l\r\n#L3#아무 것도 아니에요.#l");
                            switch (vv) {
                                case 0: //플래그 레이스가 뭔가요?
                                    self.say("#fs11#\r\n내 위대한 창작물인 플래그 레이스에 대해 알고 싶은 거야?");
                                    self.say("#fs11#\r\n룰은 간단해. #r매시 30분마다#k 개최되고, #r주간 길드 미션 포인트가 1 이상인 길드원#k만이 참가할 수 있어.");
                                    self.say("#fs11#\r\n빠르게 골인 지점까지 달리면 된다고. 물론 장애물 같은게 조----------------------------금은 있을 수도 있어.");
                                    self.say("#fs11#\r\n플래그 레이스 내에서는 오로지 이동과 점프만으로 정정당당하게 승부... 하는 건 아니고, 코스 내에서만 사용할 수 있는 플래그 스킬들이 있어.");
                                    self.say("#fs11#\r\n플래그 스킬들과 맵에 배치된 장치들을 잘 조합하면 통상의 상황에서는 갈 수 없는 길을 가거나 더 빠르게 코스를 주파할 수 있을 거야.");
                                    self.say("#fs11#\r\n아, 그리고 레이스 입장 시 모든 변신 상태가 해제되고 레이스 중 변신 포션을 사용하더라도 변신이 되지 않을 거야. 소중한 물약을 날려버릴 수도 있으니 조심하라고.");
                                    self.say("#fs11#\r\n101레벨 이상만 참여할 수 있으니 확인해 두라고. 이미 101레벨이 넘은지 한참 됐다면야 뭐...");
                                    self.say("#fs11#\r\n그럼, 힘내라고.");
                                    break;
                                case 1: //플래그 레이스를 하면 뭐가 좋죠?
                                    self.say("#fs11#\r\n뭐가 좋으냐고? 길이 있으니까 달리고, 장애물이 있으니까 넘는 거지 꼭 좋을 게 있어야 하나?");
                                    self.say("#fs11#\r\n...농담이고, 길드 스킬 중 노블레스 스킬에 직접 투자할 수 있는 SP를 일주일간의 플래그 레이스 길드 랭킹에 따라 받을 수 있지.");
                                    self.say("#fs11##b#e1위 : 25개의 SP\r\n2위 : 22개의 SP\r\n3위 : 20개의 SP\r\n4위 ~ 10위 : 18개의 SP\r\n상위 10% : 16개의 SP\r\n상위 20% : 14개의 SP\r\n상위 30% : 12개의 SP\r\n상위 40% : 10개의 SP\r\n상위 50% : 9개의 SP\r\n상위 60% : 8개의 SP\r\n상위 70% : 7개의 SP\r\n상위 80% : 6개의 SP\r\n1,000포인트 이상 : 5개의 SP#n#k\r\n\r\n#r#e※ 단, 1,000포인트 이상 획득하지 못하면 순위 안에 들었더라도 보상을 받을 수 없습니다.#k#n");
                                    self.say("#fs11#\r\n플래그 레이스 길드 랭킹은 각 길드원의 플래그 레이스 점수를 합산한 점수로 매겨지지. 달리기 실력이 빼어난 동료가 많은 길드가 더 높은 순위를 가져갈 수 있어.");
                                    self.say("#fs11#\r\n너의 플래그 레이스 점수는 주간 최고 기록으로 결정돼. 지금의 기록이 마음에 들지 않으면 언제든 다시 도전하라고. 물론 열려 있을 때 말이야.");
                                    self.say("#fs11#\r\n아, 길드에서 탈퇴하거나 추방당하면 너의 주간 최고 기록이 초기화되고 길드의 합산 점수도 그만큼 차감되니까 반드시 조심하라고.");
                                    self.say("#fs11#\r\n그리고 랭킹은 영원하지 않아. 매주 월요일 0시에 랭킹과 노블레스 스킬은 모두 초기화되고 그때까지 합산된 랭킹을 반영해서 노블레스 스킬SP를 주니까. 이번 주에 실패했다고 해서 너무 실망하지 말라구.");
                                    break;
                                case 4: //플래그 레이스의 랭킹 포인트 기준을 알고 싶어요.
                                    self.say("#fs11#\r\n이제 플래그 레이스에서 얻을 수 있는 랭킹 포인트는 세 바퀴를 모두 완주하는데 걸린 시간에 따라 결정돼. 남들과 상관 없이 너만 잘 달리면 높은 점수를 얻을 수 있다는 뜻이지.");
                                    self.say("#fs11#\r\n랭킹 포인트 기준은 일주일 간 너의 최고 기록을 기준으로 결정돼. 더 높은 기록을 얻어 기록을 갱신할 수 있으니까 기록이 아쉬우면 다시 도전하라고.");
                                    self.say("#fs11#\r\n마지막으로, 플래그 레이스의 코스는 매주 월요일 0시마다 바뀌게 돼. 코스마다 점수 기준이 다르니까 꼭 참고해서 똑똑하게 달리라고.");
                                    self.say("#fs11#\r\n#e[한낮의 설원 랭킹 포인트 기준]#n\r\n\r\n2분 10초 미만 : #b#e1,000 포인트#n#k\r\n2분 10초 ~ 2분 15초 미만 : #b#e800 포인트#n#k\r\n2분 15초 ~ 2분 20초 미만 : #b#e650 포인트#n#k\r\n2분 20초 ~ 2분 25초 미만 : #b#e550 포인트#n#k\r\n2분 25초 ~ 2분 30초 미만 : #b#e450 포인트#n#k\r\n2분 30초 ~ 2분 35초 미만 : #b#e400 포인트#n#k\r\n2분 35초 ~ 2분 40초 미만 : #b#e350 포인트#n#k\r\n2분 40초 ~ 2분 50초 미만 : #b#e300 포인트#n#k\r\n2분 50초 ~ 3분 00초 미만 : #b#e250 포인트#n#k\r\n3분 00초 ~ 3분 10초 미만 : #b#e200 포인트#n#k\r\n3분 10초 이상 : #b#e100 포인트#n#k");
                                    self.say("#fs11#\r\n#e[석양의 설원 랭킹 포인트 기준]#n\r\n\r\n1분 50초 미만 : #b#e1,000 포인트#n#k\r\n1분 50초 ~ 1분 55초 미만 : #b#e800 포인트#n#k\r\n1분 55초 ~ 2분 00초 미만 : #b#e650 포인트#n#k\r\n2분 00초 ~ 2분 05초 미만 : #b#e550 포인트#n#k\r\n2분 05초 ~ 2분 10초 미만 : #b#e450 포인트#n#k\r\n2분 10초 ~ 2분 15초 미만 : #b#e400 포인트#n#k\r\n2분 15초 ~ 2분 20초 미만 : #b#e350 포인트#n#k\r\n2분 20초 ~ 2분 30초 미만 : #b#e300 포인트#n#k\r\n2분 30초 ~ 2분 40초 미만 : #b#e250 포인트#n#k\r\n2분 40초 ~ 2분 50초 미만 : #b#e200 포인트#n#k\r\n2분 50초 이상 : #b#e100 포인트#n#k");
                                    self.say("#fs11#\r\n#e[한밤의 설원 랭킹 포인트 기준]#n\r\n\r\n2분 30초 미만 : #b#e1,000 포인트#n#k\r\n2분 30초 ~ 2분 35초 미만 : #b#e800 포인트#n#k\r\n2분 35초 ~ 2분 40초 미만 : #b#e650 포인트#n#k\r\n2분 40초 ~ 2분 45초 미만 : #b#e550 포인트#n#k\r\n2분 45초 ~ 2분 50초 미만 : #b#e450 포인트#n#k\r\n2분 50초 ~ 2분 55초 미만 : #b#e400 포인트#n#k\r\n2분 55초 ~ 3분 00초 미만 : #b#e350 포인트#n#k\r\n3분 00초 ~ 3분 10초 미만 : #b#e300 포인트#n#k\r\n3분 10초 ~ 3분 20초 미만 : #b#e250 포인트#n#k\r\n3분 20초 ~ 3분 30초 미만 : #b#e200 포인트#n#k\r\n3분 30초 이상 : #b#e100 포인트#n#k");
                                    break;
                                case 3: //아무것도 아니에요
                                    self.sayOk("#fs11#\r\n그래. 무엇을 하건 하지 않건 너의 자유지.");
                                    break;
                            }
                            break;
                        }
                        case 0:
                            self.say("#fs11#이 곳에서 나가고 싶은 거야? 나가기 전에 옆에 있는 훌륭한 게임 머신에서 랭킹을 확인할 수 있어. 한 번 보는 것도 괜찮을 거야.");
                            if (1 == self.askAccept("#fs11#이 곳에 오기 전의 맵으로 돌려보내 줄까?")) {
                                registerTransferField(100000000);
                            }
                            break;
                    }
                }
                break;
            }
            case 942000000:
            case 942001000:
            case 942002000: {
                int v = self.askMenu("#fs11#\r\n여긴 플래그 레이스 게임을 하기 위해 대기자들이 기다리는 곳이야. 너무 보채지 말라고.\r\n#b#L0#플래그 레이스에 대해서 알려주세요.#l\r\n#b#L1#이 곳에서 나가고 싶어요.#l");
                switch (v) {
                    case 0: { //플래그 레이스에 대해서 알려주세요.
                        int vv = self.askMenu("#fs11#쿠쿠쿠, 플래그 레이스에 대해서 뭐가 그렇게 알고 싶은데?\r\n\r\n#b#L0#플래그 레이스가 뭔가요?#l\r\n#b#L1#플래그 레이스를 하면 뭐가 좋죠?#l\r\n#L4#플래그 레이스의 랭킹 포인트 기준을 알고 싶어요.#l\r\n#L3#아무 것도 아니에요.#l");
                        switch (vv) {
                            case 0: //플래그 레이스가 뭔가요?
                                self.say("#fs11#\r\n내 위대한 창작물인 플래그 레이스에 대해 알고 싶은 거야?");
                                self.say("#fs11#\r\n룰은 간단해. #r매시 30분마다#k 개최되고, #r주간 길드 미션 포인트가 1 이상인 길드원#k만이 참가할 수 있어.");
                                self.say("#fs11#\r\n빠르게 골인 지점까지 달리면 된다고. 물론 장애물 같은게 조----------------------------금은 있을 수도 있어.");
                                self.say("#fs11#\r\n플래그 레이스 내에서는 오로지 이동과 점프만으로 정정당당하게 승부... 하는 건 아니고, 코스 내에서만 사용할 수 있는 플래그 스킬들이 있어.");
                                self.say("#fs11#\r\n플래그 스킬들과 맵에 배치된 장치들을 잘 조합하면 통상의 상황에서는 갈 수 없는 길을 가거나 더 빠르게 코스를 주파할 수 있을 거야.");
                                self.say("#fs11#\r\n아, 그리고 레이스 입장 시 모든 변신 상태가 해제되고 레이스 중 변신 포션을 사용하더라도 변신이 되지 않을 거야. 소중한 물약을 날려버릴 수도 있으니 조심하라고.");
                                self.say("#fs11#\r\n101레벨 이상만 참여할 수 있으니 확인해 두라고. 이미 101레벨이 넘은지 한참 됐다면야 뭐...");
                                self.say("#fs11#\r\n그럼, 힘내라고.");
                                break;
                            case 1: //플래그 레이스를 하면 뭐가 좋죠?
                                self.say("#fs11#\r\n뭐가 좋으냐고? 길이 있으니까 달리고, 장애물이 있으니까 넘는 거지 꼭 좋을 게 있어야 하나?");
                                self.say("#fs11#\r\n...농담이고, 길드 스킬 중 노블레스 스킬에 직접 투자할 수 있는 SP를 일주일간의 플래그 레이스 길드 랭킹에 따라 받을 수 있지.");
                                self.say("#fs11##b#e1위 : 25개의 SP\r\n2위 : 22개의 SP\r\n3위 : 20개의 SP\r\n4위 ~ 10위 : 18개의 SP\r\n상위 10% : 16개의 SP\r\n상위 20% : 14개의 SP\r\n상위 30% : 12개의 SP\r\n상위 40% : 10개의 SP\r\n상위 50% : 9개의 SP\r\n상위 60% : 8개의 SP\r\n상위 70% : 7개의 SP\r\n상위 80% : 6개의 SP\r\n1,000포인트 이상 : 5개의 SP#n#k\r\n\r\n#r#e※ 단, 1,000포인트 이상 획득하지 못하면 순위 안에 들었더라도 보상을 받을 수 없습니다.#k#n");
                                self.say("#fs11#\r\n플래그 레이스 길드 랭킹은 각 길드원의 플래그 레이스 점수를 합산한 점수로 매겨지지. 달리기 실력이 빼어난 동료가 많은 길드가 더 높은 순위를 가져갈 수 있어.");
                                self.say("#fs11#\r\n너의 플래그 레이스 점수는 주간 최고 기록으로 결정돼. 지금의 기록이 마음에 들지 않으면 언제든 다시 도전하라고. 물론 열려 있을 때 말이야.");
                                self.say("#fs11#\r\n아, 길드에서 탈퇴하거나 추방당하면 너의 주간 최고 기록이 초기화되고 길드의 합산 점수도 그만큼 차감되니까 반드시 조심하라고.");
                                self.say("#fs11#\r\n그리고 랭킹은 영원하지 않아. 매주 월요일 0시에 랭킹과 노블레스 스킬은 모두 초기화되고 그때까지 합산된 랭킹을 반영해서 노블레스 스킬SP를 주니까. 이번 주에 실패했다고 해서 너무 실망하지 말라구.");
                                break;
                            case 4: //플래그 레이스의 랭킹 포인트 기준을 알고 싶어요.
                                self.say("#fs11#\r\n이제 플래그 레이스에서 얻을 수 있는 랭킹 포인트는 세 바퀴를 모두 완주하는데 걸린 시간에 따라 결정돼. 남들과 상관 없이 너만 잘 달리면 높은 점수를 얻을 수 있다는 뜻이지.");
                                self.say("#fs11#\r\n랭킹 포인트 기준은 일주일 간 너의 최고 기록을 기준으로 결정돼. 더 높은 기록을 얻어 기록을 갱신할 수 있으니까 기록이 아쉬우면 다시 도전하라고.");
                                self.say("#fs11#\r\n마지막으로, 플래그 레이스의 코스는 매주 월요일 0시마다 바뀌게 돼. 코스마다 점수 기준이 다르니까 꼭 참고해서 똑똑하게 달리라고.");
                                self.say("#fs11#\r\n#e[한낮의 설원 랭킹 포인트 기준]#n\r\n\r\n2분 10초 미만 : #b#e1,000 포인트#n#k\r\n2분 10초 ~ 2분 15초 미만 : #b#e800 포인트#n#k\r\n2분 15초 ~ 2분 20초 미만 : #b#e650 포인트#n#k\r\n2분 20초 ~ 2분 25초 미만 : #b#e550 포인트#n#k\r\n2분 25초 ~ 2분 30초 미만 : #b#e450 포인트#n#k\r\n2분 30초 ~ 2분 35초 미만 : #b#e400 포인트#n#k\r\n2분 35초 ~ 2분 40초 미만 : #b#e350 포인트#n#k\r\n2분 40초 ~ 2분 50초 미만 : #b#e300 포인트#n#k\r\n2분 50초 ~ 3분 00초 미만 : #b#e250 포인트#n#k\r\n3분 00초 ~ 3분 10초 미만 : #b#e200 포인트#n#k\r\n3분 10초 이상 : #b#e100 포인트#n#k");
                                self.say("#fs11#\r\n#e[석양의 설원 랭킹 포인트 기준]#n\r\n\r\n1분 50초 미만 : #b#e1,000 포인트#n#k\r\n1분 50초 ~ 1분 55초 미만 : #b#e800 포인트#n#k\r\n1분 55초 ~ 2분 00초 미만 : #b#e650 포인트#n#k\r\n2분 00초 ~ 2분 05초 미만 : #b#e550 포인트#n#k\r\n2분 05초 ~ 2분 10초 미만 : #b#e450 포인트#n#k\r\n2분 10초 ~ 2분 15초 미만 : #b#e400 포인트#n#k\r\n2분 15초 ~ 2분 20초 미만 : #b#e350 포인트#n#k\r\n2분 20초 ~ 2분 30초 미만 : #b#e300 포인트#n#k\r\n2분 30초 ~ 2분 40초 미만 : #b#e250 포인트#n#k\r\n2분 40초 ~ 2분 50초 미만 : #b#e200 포인트#n#k\r\n2분 50초 이상 : #b#e100 포인트#n#k");
                                self.say("#fs11#\r\n#e[한밤의 설원 랭킹 포인트 기준]#n\r\n\r\n2분 30초 미만 : #b#e1,000 포인트#n#k\r\n2분 30초 ~ 2분 35초 미만 : #b#e800 포인트#n#k\r\n2분 35초 ~ 2분 40초 미만 : #b#e650 포인트#n#k\r\n2분 40초 ~ 2분 45초 미만 : #b#e550 포인트#n#k\r\n2분 45초 ~ 2분 50초 미만 : #b#e450 포인트#n#k\r\n2분 50초 ~ 2분 55초 미만 : #b#e400 포인트#n#k\r\n2분 55초 ~ 3분 00초 미만 : #b#e350 포인트#n#k\r\n3분 00초 ~ 3분 10초 미만 : #b#e300 포인트#n#k\r\n3분 10초 ~ 3분 20초 미만 : #b#e250 포인트#n#k\r\n3분 20초 ~ 3분 30초 미만 : #b#e200 포인트#n#k\r\n3분 30초 이상 : #b#e100 포인트#n#k");
                                break;
                            case 3: //아무것도 아니에요
                                self.sayOk("#fs11#\r\n그래. 무엇을 하건 하지 않건 너의 자유지.");
                                break;
                        }
                        break;
                    }
                    case 1:
                        if (1 == self.askYesNo("#fs11#정말 이 곳에서 나가고 싶은거야?")) {
                            registerTransferField(getPlayer().getMapId() + 1);
                        }
                        break;
                }
                break;
            }
            default: {
                int v = self.askMenu("#fs11#\r\n여기는 플래그 레이스 연습 대기실이야~ 궁금한 거 있으면 물어봐도 돼.\r\n#b#L1#플래그 레이스 연습 코스로 가고 싶어요.#l\r\n#b#L0#플래그 레이스에 대해서 알려주세요.#l");
                switch (v) {
                    case 1: { //플래그 레이스 연습 코스로 가고 싶어요.
                        int vv = self.askMenu("#fs11#\r\n어느 코스를 연습하고 싶은데? 전부 쉽지는 않지만 말이야.\r\n#b#L1#한낮의 설원 연습 코스#l\r\n#b#L2#석양의 설원 연습 코스#l\r\n#b#L3#한밤의 설원 연습 코스#l");
                        switch (vv) {
                            case 1: //한낮의 설원 연습 코스
                                if (1 == self.askAccept("#fs11##b한낮의 설원 연습 코스#k로 이동시켜 줄까?\r\n\r\n#r※ 파티장만 시작할 수 있으며 모든 파티원이 연습 대기실에 있어야만 입장 가능합니다.")) {
                                    FieldSet fieldSet = fieldSet("FlagRaceN1Enter");
                                    if (fieldSet == null) {
                                        self.sayOk("#fs11#지금은 플래그 레이스(한낮의 설원)을 이용할 수 없어.");
                                        return;
                                    }
                                    int enter = ((FlagRaceN1Enter) fieldSet).enter(target.getId(), true, 0);
                                    if (enter == -1)
                                        self.say("#fs11#알 수 없는 이유로 입장할 수 없습니다. 잠시 후에 다시 시도해 주십시오.");
                                    else if (enter == 1)
                                        self.say("#fs11#1~6인 파티를 맺어야만 시작할 수 있어.");
                                    else if (enter == 2)
                                        self.say("#fs11#...너는 파티장이 아닌걸? 파티장만이 입장을 신청할 수 있다고.");
                                    else if (enter == 3)
                                        self.say("#fs11#최소 " + fieldSet.minMember + "인 이상의 파티가 퀘스트를 시작할 수 있답니다.");
                                    else if (enter == 4)
                                        self.say("#fs11#파티원의 레벨은 최소 " + fieldSet.minLv + " 이상이어야 합니다.");
                                    else if (enter == 5) self.say("#fs11#파티원이 모두 모여 있어야 시작할 수 있습니다.");
                                    else if (enter == 6)
                                        self.say("#fs11#이미 다른 파티가 안으로 들어가 퀘스트 클리어에 도전하고 있는 중이랍니다.");
                                } else {
                                    self.sayOk("#fs11#연습이 필요해지면 언제든 말해...");
                                }
                                break;
                            case 2: //석양의 설원 연습 코스
                                if (1 == self.askAccept("#fs11##b석양의 설원 연습 코스#k로 이동시켜 줄까?\r\n\r\n#r※ 파티장만 시작할 수 있으며 모든 파티원이 연습 대기실에 있어야만 입장 가능합니다.")) {
                                    //942003200
                                    FieldSet fieldSet = fieldSet("FlagRaceN2Enter");
                                    if (fieldSet == null) {
                                        self.sayOk("#fs11#지금은 플래그 레이스(석양의 설원)을 이용할 수 없어.");
                                        return;
                                    }
                                    int enter = ((FlagRaceN2Enter) fieldSet).enter(target.getId(), true, 0);
                                    if (enter == -1)
                                        self.say("#fs11#알 수 없는 이유로 입장할 수 없습니다. 잠시 후에 다시 시도해 주십시오.");
                                    else if (enter == 1)
                                        self.say("#fs11#1~6인 파티를 맺어야만 시작할 수 있어.");
                                    else if (enter == 2)
                                        self.say("#fs11#...너는 파티장이 아닌걸? 파티장만이 입장을 신청할 수 있다고.");
                                    else if (enter == 3)
                                        self.say("#fs11#최소 " + fieldSet.minMember + "인 이상의 파티가 퀘스트를 시작할 수 있답니다.");
                                    else if (enter == 4)
                                        self.say("#fs11#파티원의 레벨은 최소 " + fieldSet.minLv + " 이상이어야 합니다.");
                                    else if (enter == 5) self.say("#fs11#파티원이 모두 모여 있어야 시작할 수 있습니다.");
                                    else if (enter == 6)
                                        self.say("#fs11#이미 다른 파티가 안으로 들어가 퀘스트 클리어에 도전하고 있는 중이랍니다.");
                                } else {
                                    self.sayOk("#fs11#연습이 필요해지면 언제든 말해...");
                                }
                                break;
                            case 3: //한밤의 설원 연습 코스
                                if (1 == self.askAccept("#fs11##b한밤의 설원 연습 코스#k로 이동시켜 줄까?\r\n\r\n#r※ 파티장만 시작할 수 있으며 모든 파티원이 연습 대기실에 있어야만 입장 가능합니다.")) {
                                    //942003300
                                    FieldSet fieldSet = fieldSet("FlagRaceN3Enter");
                                    if (fieldSet == null) {
                                        self.sayOk("#fs11#지금은 플래그 레이스(석양의 설원)을 이용할 수 없어.");
                                        return;
                                    }
                                    int enter = ((FlagRaceN3Enter) fieldSet).enter(target.getId(), true, 0);
                                    if (enter == -1)
                                        self.say("#fs11#알 수 없는 이유로 입장할 수 없습니다. 잠시 후에 다시 시도해 주십시오.");
                                    else if (enter == 1)
                                        self.say("#fs11#1~6인 파티를 맺어야만 시작할 수 있어.");
                                    else if (enter == 2)
                                        self.say("#fs11#...너는 파티장이 아닌걸? 파티장만이 입장을 신청할 수 있다고.");
                                    else if (enter == 3)
                                        self.say("#fs11#최소 " + fieldSet.minMember + "인 이상의 파티가 퀘스트를 시작할 수 있답니다.");
                                    else if (enter == 4)
                                        self.say("#fs11#파티원의 레벨은 최소 " + fieldSet.minLv + " 이상이어야 합니다.");
                                    else if (enter == 5) self.say("#fs11#파티원이 모두 모여 있어야 시작할 수 있습니다.");
                                    else if (enter == 6)
                                        self.say("#fs11#이미 다른 파티가 안으로 들어가 퀘스트 클리어에 도전하고 있는 중이랍니다.");

                                } else {
                                    self.sayOk("#fs11#연습이 필요해지면 언제든 말해...");
                                }
                                break;
                        }
                        break;
                    }
                }
                break;
            }
        }
    }

    public void flag_Exit_NPC() {
        if (1 == self.askYesNo("#fs11#\r\n벌써 포기하고 싶은 거야? 밖으로 내보내줄까?", ScriptMessageFlag.NpcReplacedByNpc)) {
            registerTransferField(942003000); //한낮 퇴장맵
            //942003001 석양 퇴장맵
            //942003002 한밤 퇴장맵
        }
    }

    public void flag_Exit_Map_NPC() {
        if (1 == self.askAccept("#fs11#쿠쿠쿠, 연습 코스는 어땠어? 다시 연습 대기실로 돌려보내 줄까?")) {
            registerTransferField(942003050);
        } else {
            self.sayOk("#fs11#그래, 돌아가고 싶거든 내게 말해줘.");
        }
    }

    @Script
    public void flag_pexit() {
        registerTransferField(100000000);
    }

    @Script
    public void flag_Start() {
        switch (getPlayer().getMapId()) {
            case 942000500: { //실전맵
                objects.fields.fieldset.instance.FlagRace fieldSet = (objects.fields.fieldset.instance.FlagRace) getPlayer().getMap().getFieldSetInstance();
                if (fieldSet == null) {
                    getPlayer().dropMessage(5, "지금은 포탈을 이용하실 수 없습니다.");
                    return;
                }
                if (fieldSet.isStartGame()) {
                    Field field = getPlayer().getMap();
                    field.removeDropsIndividual(getPlayer());
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(94, 2297), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(1892, 1225), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(812, 1167), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(420, 1008), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-2023, 1244), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-555, 1273), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-600, 1043), false);
                    userLocalTeleport(16);
                }
                break;
            }
            case 942003100: {
                FlagRaceN1 fieldSet = (FlagRaceN1) getPlayer().getMap().getFieldSetInstance();
                if (fieldSet == null) {
                    getPlayer().dropMessage(5, "지금은 포탈을 이용하실 수 없습니다.");
                    return;
                }
                if (fieldSet.isStartGame()) {
                    Field field = getPlayer().getMap();
                    field.removeDropsIndividual(getPlayer());
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(94, 2297), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(1892, 1225), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(812, 1167), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(420, 1008), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-2023, 1244), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-555, 1273), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-600, 1043), false);
                    userLocalTeleport(16);
                }
                break;
            }

            case 942001500: {
                objects.fields.fieldset.instance.FlagRace fieldSet = (objects.fields.fieldset.instance.FlagRace) getPlayer().getMap().getFieldSetInstance();
                if (fieldSet == null) {
                    getPlayer().dropMessage(5, "지금은 포탈을 이용하실 수 없습니다.");
                    return;
                }
                if (fieldSet.isStartGame()) {
                    Field field = getPlayer().getMap();
                    field.removeDropsIndividual(getPlayer());
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-803, 1480), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(270, 1525), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(632, 1525), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(2007, 1103), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(1357, 1103), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(725, 975), false);
                    userLocalTeleport(1);
                }
                break;
            }
            case 942003200: {
                FlagRaceN2 fieldSet = (FlagRaceN2) getPlayer().getMap().getFieldSetInstance();
                if (fieldSet == null) {
                    getPlayer().dropMessage(5, "지금은 포탈을 이용하실 수 없습니다.");
                    return;
                }
                if (fieldSet.isStartGame()) {
                    Field field = getPlayer().getMap();
                    field.removeDropsIndividual(getPlayer());
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-803, 1480), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(270, 1525), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(632, 1525), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(2007, 1103), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(1357, 1103), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(725, 975), false);
                    userLocalTeleport(1);
                }
                break;
            }
            //932200300
            case 942002500: {
                objects.fields.fieldset.instance.FlagRace fieldSet = (objects.fields.fieldset.instance.FlagRace) getPlayer().getMap().getFieldSetInstance();
                if (fieldSet == null) {
                    getPlayer().dropMessage(5, "지금은 포탈을 이용하실 수 없습니다.");
                    return;
                }
                if (fieldSet.isStartGame()) {
                    Field field = getPlayer().getMap();
                    field.removeDropsIndividual(getPlayer());
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(689, 2328), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-1075, 2322), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-110, 2660), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(2073, 1644), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(2073, 1284), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(808, 1339), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-2050, 1081), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-2065, 1345), false);
                    userLocalTeleport(18);
                }
                break;
            }
            case 942003300: {
                FlagRaceN3 fieldSet = (FlagRaceN3) getPlayer().getMap().getFieldSetInstance();
                if (fieldSet == null) {
                    getPlayer().dropMessage(5, "지금은 포탈을 이용하실 수 없습니다.");
                    return;
                }
                if (fieldSet.isStartGame()) {
                    Field field = getPlayer().getMap();
                    field.removeDropsIndividual(getPlayer());
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(689, 2328), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-1075, 2322), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-110, 2660), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(2073, 1644), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(2073, 1284), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(808, 1339), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-2050, 1081), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-2065, 1345), false);
                    userLocalTeleport(18);
                }
                break;
            }
        }
    }

    @Script
    public void flag_goal() {
        switch (getPlayer().getMapId()) {
            case 942000500: { //실전맵
                objects.fields.fieldset.instance.FlagRace fieldSet = (objects.fields.fieldset.instance.FlagRace) getPlayer().getMap().getFieldSetInstance();
                if (fieldSet == null) {
                    getPlayer().dropMessage(5, "지금은 포탈을 이용하실 수 없습니다.");
                    return;
                }
                int flagGoal = fieldSet.addFlagGoalNumber(getPlayer().getName());
                getPlayer().updateOneInfo(32581, "lap", String.valueOf(flagGoal));
                if (flagGoal == 3) {
                    SimpleDateFormat sdf = new SimpleDateFormat("YYMMddHHmmssSS");
                    getPlayer().updateOneInfo(32581, "finish", sdf.format(new Date()));
                    long record = 0;
                    try {
                        long start = sdf.parse(getPlayer().getOneInfo(32581, "start")).getTime();
                        long finish = sdf.parse(getPlayer().getOneInfo(32581, "finish")).getTime();
                        record = finish- start;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getPlayer().updateOneInfo(32581, "complete", "1");
                    getPlayer().updateOneInfo(32581, "record", String.valueOf(record));
                    getPlayer().getMap().broadcastMessage(CWvsContext.getScriptProgressMessage(getPlayer().getName() + " 님께서 완주에 성공하였습니다."));
                    getPlayer().send(CField.environmentChange("quest/party/clear", 19));
                    userLocalTeleport(18);
                } else {
                    getPlayer().getMap().broadcastMessage(CWvsContext.getScriptProgressMessage(getPlayer().getName() + "님이 골인 지점을 통과하였습니다. 앞으로 " + (3 - flagGoal) + "바퀴 남았습니다."));
                    userLocalTeleport(17);
                }
                break;
            }
            case 942003100: {
                FlagRaceN1 fieldSet = (FlagRaceN1) getPlayer().getMap().getFieldSetInstance();
                if (fieldSet == null) {
                    getPlayer().dropMessage(5, "지금은 포탈을 이용하실 수 없습니다.");
                    return;
                }
                int flagGoal = fieldSet.addFlagGoalNumber(getPlayer().getName());
                getPlayer().updateOneInfo(32581, "lap", String.valueOf(flagGoal));
                if (flagGoal == 3) {
                    SimpleDateFormat sdf = new SimpleDateFormat("YYMMddHHmmssSS");
                    getPlayer().updateOneInfo(32581, "finish", sdf.format(new Date()));
                    long record = 0;
                    try {
                        long start = sdf.parse(getPlayer().getOneInfo(32581, "start")).getTime();
                        long finish = sdf.parse(getPlayer().getOneInfo(32581, "finish")).getTime();
                        record = finish- start;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getPlayer().updateOneInfo(32581, "complete", "1");
                    getPlayer().updateOneInfo(32581, "record", String.valueOf(record));
                    getPlayer().getMap().broadcastMessage(CWvsContext.getScriptProgressMessage(getPlayer().getName() + " 님께서 완주에 성공하였습니다."));
                    getPlayer().send(CField.environmentChange("quest/party/clear", 19));
                    userLocalTeleport(18);
                } else {
                    getPlayer().getMap().broadcastMessage(CWvsContext.getScriptProgressMessage(getPlayer().getName() + "님이 골인 지점을 통과하였습니다. 앞으로 " + (3 - flagGoal) + "바퀴 남았습니다."));
                    userLocalTeleport(17);
                }
                break;
            }
            case 942001500: {
                objects.fields.fieldset.instance.FlagRace fieldSet = (objects.fields.fieldset.instance.FlagRace) getPlayer().getMap().getFieldSetInstance();
                if (fieldSet == null) {
                    getPlayer().dropMessage(5, "지금은 포탈을 이용하실 수 없습니다.");
                    return;
                }
                int flagGoal = fieldSet.addFlagGoalNumber(getPlayer().getName());
                getPlayer().updateOneInfo(32581, "lap", String.valueOf(flagGoal));
                if (flagGoal == 3) {
                    SimpleDateFormat sdf = new SimpleDateFormat("YYMMddHHmmssSS");
                    getPlayer().updateOneInfo(32581, "finish", sdf.format(new Date()));
                    long record = 0;
                    try {
                        long start = sdf.parse(getPlayer().getOneInfo(32581, "start")).getTime();
                        long finish = sdf.parse(getPlayer().getOneInfo(32581, "finish")).getTime();
                        record = finish- start;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getPlayer().updateOneInfo(32581, "complete", "1");
                    getPlayer().updateOneInfo(32581, "record", String.valueOf(record));
                    getPlayer().getMap().broadcastMessage(CWvsContext.getScriptProgressMessage(getPlayer().getName() + " 님께서 완주에 성공하였습니다."));
                    getPlayer().send(CField.environmentChange("quest/party/clear", 19));
                    userLocalTeleport(5);
                } else {
                    Field field = getPlayer().getMap();
                    field.removeDropsIndividual(getPlayer());
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-803, 1480), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(270, 1525), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(632, 1525), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(2007, 1103), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(1357, 1103), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(725, 975), false);
                    getPlayer().getMap().broadcastMessage(CWvsContext.getScriptProgressMessage(getPlayer().getName() + "님이 골인 지점을 통과하였습니다. 앞으로 " + (3 - flagGoal) + "바퀴 남았습니다."));
                    userLocalTeleport(1);
                }
                break;
            }

            case 942003200: {
                FlagRaceN2 fieldSet = (FlagRaceN2) getPlayer().getMap().getFieldSetInstance();
                if (fieldSet == null) {
                    getPlayer().dropMessage(5, "지금은 포탈을 이용하실 수 없습니다.");
                    return;
                }
                int flagGoal = fieldSet.addFlagGoalNumber(getPlayer().getName());
                getPlayer().updateOneInfo(32581, "lap", String.valueOf(flagGoal));
                if (flagGoal == 3) {
                    SimpleDateFormat sdf = new SimpleDateFormat("YYMMddHHmmssSS");
                    getPlayer().updateOneInfo(32581, "finish", sdf.format(new Date()));
                    long record = 0;
                    try {
                        long start = sdf.parse(getPlayer().getOneInfo(32581, "start")).getTime();
                        long finish = sdf.parse(getPlayer().getOneInfo(32581, "finish")).getTime();
                        record = finish- start;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getPlayer().updateOneInfo(32581, "complete", "1");
                    getPlayer().updateOneInfo(32581, "record", String.valueOf(record));
                    getPlayer().getMap().broadcastMessage(CWvsContext.getScriptProgressMessage(getPlayer().getName() + " 님께서 완주에 성공하였습니다."));
                    getPlayer().send(CField.environmentChange("quest/party/clear", 19));
                    userLocalTeleport(5);
                } else {
                    Field field = getPlayer().getMap();
                    field.removeDropsIndividual(getPlayer());
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(-803, 1480), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(270, 1525), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(632, 1525), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(2007, 1103), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(1357, 1103), false);
                    field.spawnIndividualItemDropPos(getPlayer(), getPlayer(), new Item(2633626, (byte) 0, (short) 1, (byte) 0), new Point(725, 975), false);
                    getPlayer().getMap().broadcastMessage(CWvsContext.getScriptProgressMessage(getPlayer().getName() + "님이 골인 지점을 통과하였습니다. 앞으로 " + (3 - flagGoal) + "바퀴 남았습니다."));
                    userLocalTeleport(1);
                }
                break;
            }
            case 942002500: {
                objects.fields.fieldset.instance.FlagRace fieldSet = (objects.fields.fieldset.instance.FlagRace) getPlayer().getMap().getFieldSetInstance();
                if (fieldSet == null) {
                    getPlayer().dropMessage(5, "지금은 포탈을 이용하실 수 없습니다.");
                    return;
                }
                int flagGoal = fieldSet.addFlagGoalNumber(getPlayer().getName());
                getPlayer().updateOneInfo(32581, "lap", String.valueOf(flagGoal));
                if (flagGoal == 3) {
                    SimpleDateFormat sdf = new SimpleDateFormat("YYMMddHHmmssSS");
                    getPlayer().updateOneInfo(32581, "finish", sdf.format(new Date()));
                    long record = 0;
                    try {
                        long start = sdf.parse(getPlayer().getOneInfo(32581, "start")).getTime();
                        long finish = sdf.parse(getPlayer().getOneInfo(32581, "finish")).getTime();
                        record = finish- start;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getPlayer().updateOneInfo(32581, "complete", "1");
                    getPlayer().updateOneInfo(32581, "record", String.valueOf(record));
                    getPlayer().getMap().broadcastMessage(CWvsContext.getScriptProgressMessage(getPlayer().getName() + " 님께서 완주에 성공하였습니다."));
                    getPlayer().send(CField.environmentChange("quest/party/clear", 19));
                    userLocalTeleport(3);
                } else {
                    getPlayer().getMap().broadcastMessage(CWvsContext.getScriptProgressMessage(getPlayer().getName() + "님이 골인 지점을 통과하였습니다. 앞으로 " + (3 - flagGoal) + "바퀴 남았습니다."));
                    userLocalTeleport(5);
                }
                break;
            }
            case 942003300: {
                FlagRaceN3 fieldSet = (FlagRaceN3) getPlayer().getMap().getFieldSetInstance();
                if (fieldSet == null) {
                    getPlayer().dropMessage(5, "지금은 포탈을 이용하실 수 없습니다.");
                    return;
                }
                int flagGoal = fieldSet.addFlagGoalNumber(getPlayer().getName());
                getPlayer().updateOneInfo(32581, "lap", String.valueOf(flagGoal));
                if (flagGoal == 3) {
                    SimpleDateFormat sdf = new SimpleDateFormat("YYMMddHHmmssSS");
                    getPlayer().updateOneInfo(32581, "finish", sdf.format(new Date()));
                    long record = 0;
                    try {
                        long start = sdf.parse(getPlayer().getOneInfo(32581, "start")).getTime();
                        long finish = sdf.parse(getPlayer().getOneInfo(32581, "finish")).getTime();
                        record = finish- start;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getPlayer().updateOneInfo(32581, "complete", "1");
                    getPlayer().updateOneInfo(32581, "record", String.valueOf(record));
                    getPlayer().getMap().broadcastMessage(CWvsContext.getScriptProgressMessage(getPlayer().getName() + " 님께서 완주에 성공하였습니다."));
                    getPlayer().send(CField.environmentChange("quest/party/clear", 19));
                    userLocalTeleport(3);
                } else {
                    getPlayer().getMap().broadcastMessage(CWvsContext.getScriptProgressMessage(getPlayer().getName() + "님이 골인 지점을 통과하였습니다. 앞으로 " + (3 - flagGoal) + "바퀴 남았습니다."));
                    userLocalTeleport(5);
                }
                break;
            }
        }
    }

    public void flag_exit() {
        initNPC(MapleLifeFactory.getNPC(9000233));
        switch (getPlayer().getMapId()) {
            case 942000500:
            case 942001500:
            case 942002500: {
                objects.fields.fieldset.instance.FlagRace fieldSet = (objects.fields.fieldset.instance.FlagRace) getPlayer().getMap().getFieldSetInstance();
                if (fieldSet == null) {
                    getPlayer().dropMessage(5, "포탈을 이용하실 수 없습니다.");
                    return;
                }
                int flagGoal = fieldSet.getFlagGoalNumber(getPlayer().getName());
                if (flagGoal == 3) {
                    String mapName = "한낮의 설원";
                    if (getPlayer().getMapId() == 932200200) {
                        mapName = "석양의 설원";
                    } else if (getPlayer().getMapId() == 932200300) {
                        mapName = "한밤의 설원";
                    }
                    self.say("#b" + mapName + "#k 완주에 성공했구나! 생각보단 꽤 하는걸?", ScriptMessageFlag.NpcReplacedByNpc);
                    if (1 == self.askYesNo("내가 직접 코스 밖으로 안내해줄게. 물론 원한다면 아직도 달리고 있는 동료들을 구경해도 되고. 쿠쿠쿠.", ScriptMessageFlag.NpcReplacedByNpc)) {
                        int returnMap = 942003000 + ((getPlayer().getMapId() - 942000500) / 1000);
                        registerTransferField(returnMap);
                    }
                }
                break;
            }
            case 942003100: {
                FlagRaceN1 fieldSet = (FlagRaceN1) getPlayer().getMap().getFieldSetInstance();
                if (fieldSet == null) {
                    getPlayer().dropMessage(5, "포탈을 이용하실 수 없습니다.");
                    return;
                }
                int flagGoal = fieldSet.getFlagGoalNumber(getPlayer().getName());
                if (flagGoal == 3) {
                    self.say("#b한낮의 설원#k 완주에 성공했구나! 생각보단 꽤 하는걸?", ScriptMessageFlag.NpcReplacedByNpc);
                    if (1 == self.askYesNo("내가 직접 코스 밖으로 안내해줄게. 물론 원한다면 아직도 달리고 있는 동료들을 구경해도 되고. 쿠쿠쿠.", ScriptMessageFlag.NpcReplacedByNpc)) {
                        registerTransferField(942003000); //한낮 퇴장맵
                    }
                }
                break;
            }
            case 942003200: {
                FlagRaceN2 fieldSet = (FlagRaceN2) getPlayer().getMap().getFieldSetInstance();
                if (fieldSet == null) {
                    getPlayer().dropMessage(5, "포탈을 이용하실 수 없습니다.");
                    return;
                }
                int flagGoal = fieldSet.getFlagGoalNumber(getPlayer().getName());
                if (flagGoal == 3) {
                    self.say("#b석양의 설원#k 완주에 성공했구나! 생각보단 꽤 하는걸?", ScriptMessageFlag.NpcReplacedByNpc);
                    if (1 == self.askYesNo("내가 직접 코스 밖으로 안내해줄게. 물론 원한다면 아직도 달리고 있는 동료들을 구경해도 되고. 쿠쿠쿠.", ScriptMessageFlag.NpcReplacedByNpc)) {
                        registerTransferField(942003001); //석양 퇴장맵
                    }
                }
                break;
            }
            case 942003300: {
                FlagRaceN3 fieldSet = (FlagRaceN3) getPlayer().getMap().getFieldSetInstance();
                if (fieldSet == null) {
                    getPlayer().dropMessage(5, "포탈을 이용하실 수 없습니다.");
                    return;
                }
                int flagGoal = fieldSet.getFlagGoalNumber(getPlayer().getName());
                if (flagGoal == 3) {
                    self.say("#b한밤의 설원#k 완주에 성공했구나! 생각보단 꽤 하는걸?", ScriptMessageFlag.NpcReplacedByNpc);
                    if (1 == self.askYesNo("내가 직접 코스 밖으로 안내해줄게. 물론 원한다면 아직도 달리고 있는 동료들을 구경해도 되고. 쿠쿠쿠.", ScriptMessageFlag.NpcReplacedByNpc)) {
                        registerTransferField(942003002); //석양 퇴장맵
                    }
                }
                break;
            }
        }
    }

    public void flag_result() {
        if (getPlayer().getOneInfoQuestInteger(32581, "complete") > 0) {
            getPlayer().updateOneInfo(32581, "complete", "0");
            initNPC(MapleLifeFactory.getNPC(9000232));
            switch (getPlayer().getMapId()) {
                case 942003000: {
                    long record = getPlayer().getOneInfoQuestInteger(32581, "record");
                    long weeklyRecord = getPlayer().getOneInfoQuestInteger(32581, "weeklyRecord");
                    long minute = TimeUnit.MILLISECONDS.toMinutes(record);
                    long second = TimeUnit.MILLISECONDS.toSeconds(record % 60000);
                    String recordToString = minute + "분 " + second + "초";
                    int score = 0;
                    if (record < 130000) { // 2분 10초
                        score = 1000;
                    } else if (record >= 130000 && record < 135000) {
                        score = 800;
                    } else if (record >= 135000 && record < 140000) {
                        score = 650;
                    } else if (record >= 140000 && record < 145000) {
                        score = 550;
                    } else if (record >= 145000 && record < 150000) {
                        score = 450;
                    } else if (record >= 150000 && record < 155000) {
                        score = 400;
                    } else if (record >= 155000 && record < 160000) {
                        score = 350;
                    } else if (record >= 160000 && record < 170000) {
                        score = 300;
                    } else if (record >= 170000 && record < 180000) {
                        score = 250;
                    } else if (record >= 180000 && record < 190000) {
                        score = 200;
                    } else if (record >= 190000) {
                        score = 100;
                    }
                    if (getPlayer().getOneInfoQuestInteger(32581, "mode") > 1) {
                        self.say("\r\n#e[플래그 레이스 연습 코스 결과]\r\n\r\n#n- 기록 : #b" + recordToString + "#k\r\n- 예상 획득 랭킹 포인트 : #b" + score + "점", ScriptMessageFlag.NpcReplacedByNpc);
                    } else {
                        if (getPlayer().getGuild() != null) {
                            getPlayer().getGuild().setPointLog(GuildContentsType.FLAG_RACE, getPlayer(), score);
                            int guildScore = getPlayer().getGuild().getPointLogByType(GuildContentsType.FLAG_RACE, getPlayer());
                            if (weeklyRecord > 0) {
                                if (record < weeklyRecord) {
                                    self.say("\r\n#e[플래그 레이스 결과]\r\n\r\n#n- 기록 : #b" + recordToString + "(신기록 갱신!)#k\r\n- 이번 주 최고 기록 : #r" + recordToString + " #k\r\n- 이번 주 획득한 길드 랭킹 포인트 : " + guildScore + "점", ScriptMessageFlag.NpcReplacedByNpc);
                                    getPlayer().updateOneInfo(32581, "weeklyRecord", String.valueOf(record));
                                } else {
                                    self.say("\r\n#e[플래그 레이스 결과]\r\n\r\n#n- 기록 : #b" + recordToString + "#k\r\n- 이번 주 획득한 길드 랭킹 포인트 : #b" + guildScore + "점", ScriptMessageFlag.NpcReplacedByNpc);
                                }
                            } else if (weeklyRecord == 0) {
                                self.say("\r\n#e[플래그 레이스 결과]\r\n\r\n#n- 기록 : #b" + recordToString + "#k\r\n- 이번 주 획득한 길드 랭킹 포인트 : #b" + guildScore + "점", ScriptMessageFlag.NpcReplacedByNpc);
                                getPlayer().updateOneInfo(32581, "weeklyRecord", String.valueOf(record));
                            }
                        }
                    }
                    break;
                }
                case 942003001: {
                    long record = getPlayer().getOneInfoQuestInteger(32581, "record");
                    long weeklyRecord = getPlayer().getOneInfoQuestInteger(32581, "weeklyRecord");
                    long minute = TimeUnit.MILLISECONDS.toMinutes(record);
                    long second = TimeUnit.MILLISECONDS.toSeconds(record % 60000);
                    String recordToString = minute + "분 " + second + "초";
                    int score = 0;
                    if (record < 110000) {
                        score = 1000;
                    } else if (record >= 110000 && record < 115000) {
                        score = 800;
                    } else if (record >= 115000 && record < 120000) {
                        score = 650;
                    } else if (record >= 120000 && record < 125000) {
                        score = 550;
                    } else if (record >= 125000 && record < 130000) {
                        score = 450;
                    } else if (record >= 130000 && record < 135000) {
                        score = 400;
                    } else if (record >= 135000 && record < 140000) {
                        score = 350;
                    } else if (record >= 140000 && record < 150000) {
                        score = 300;
                    } else if (record >= 150000 && record < 160000) {
                        score = 250;
                    } else if (record >= 160000 && record < 170000) {
                        score = 200;
                    } else if (record >= 170000) {
                        score = 100;
                    }
                    if (getPlayer().getOneInfoQuestInteger(32581, "mode") > 1) {
                        self.say("\r\n#e[플래그 레이스 연습 코스 결과]\r\n\r\n#n- 기록 : #b" + recordToString + "#k\r\n- 예상 획득 랭킹 포인트 : #b" + score + "점", ScriptMessageFlag.NpcReplacedByNpc);
                    } else {
                        if (getPlayer().getGuild() != null) {
                            getPlayer().getGuild().setPointLog(GuildContentsType.FLAG_RACE, getPlayer(), score);
                            int guildScore = getPlayer().getGuild().getPointLogByType(GuildContentsType.FLAG_RACE, getPlayer());
                            if (weeklyRecord > 0) {
                                if (record < weeklyRecord) {
                                    self.say("\r\n#e[플래그 레이스 결과]\r\n\r\n#n- 기록 : #b" + recordToString + "(신기록 갱신!)#k\r\n- 이번 주 최고 기록 : #r" + recordToString + " #k\r\n- 이번 주 획득한 길드 랭킹 포인트 : " + guildScore + "점", ScriptMessageFlag.NpcReplacedByNpc);
                                    getPlayer().updateOneInfo(32581, "weeklyRecord", String.valueOf(record));
                                } else {
                                    self.say("\r\n#e[플래그 레이스 결과]\r\n\r\n#n- 기록 : #b" + recordToString + "#k\r\n- 이번 주 획득한 길드 랭킹 포인트 : #b" + guildScore + "점", ScriptMessageFlag.NpcReplacedByNpc);
                                }
                            } else if (weeklyRecord == 0) {
                                self.say("\r\n#e[플래그 레이스 결과]\r\n\r\n#n- 기록 : #b" + recordToString + "#k\r\n- 이번 주 획득한 길드 랭킹 포인트 : #b" + guildScore + "점", ScriptMessageFlag.NpcReplacedByNpc);
                                getPlayer().updateOneInfo(32581, "weeklyRecord", String.valueOf(record));
                            }
                        }
                    }
                    break;
                }
                case 942003002: {
                    long record = getPlayer().getOneInfoQuestInteger(32581, "record");
                    long weeklyRecord = getPlayer().getOneInfoQuestInteger(32581, "weeklyRecord");
                    long minute = TimeUnit.MILLISECONDS.toMinutes(record);
                    long second = TimeUnit.MILLISECONDS.toSeconds(record % 60000);
                    String recordToString = minute + "분 " + second + "초";
                    int score = 0;
                    if (record < 150000) {
                        score = 1000;
                    } else if (record >= 150000 && record < 155000) {
                        score = 800;
                    } else if (record >= 155000 && record < 160000) {
                        score = 650;
                    } else if (record >= 160000 && record < 165000) {
                        score = 550;
                    } else if (record >= 165000 && record < 170000) {
                        score = 450;
                    } else if (record >= 170000 && record < 175000) {
                        score = 400;
                    } else if (record >= 175000 && record < 180000) {
                        score = 350;
                    } else if (record >= 180000 && record < 190000) {
                        score = 300;
                    } else if (record >= 190000 && record < 200000) {
                        score = 250;
                    } else if (record >= 200000 && record < 210000) {
                        score = 200;
                    } else if (record >= 210000) {
                        score = 100;
                    }
                    if (getPlayer().getOneInfoQuestInteger(32581, "mode") > 1) {
                        self.say("\r\n#e[플래그 레이스 연습 코스 결과]\r\n\r\n#n- 기록 : #b" + recordToString + "#k\r\n- 예상 획득 랭킹 포인트 : #b" + score + "점", ScriptMessageFlag.NpcReplacedByNpc);
                    } else {
                        if (getPlayer().getGuild() != null) {
                            getPlayer().getGuild().setPointLog(GuildContentsType.FLAG_RACE, getPlayer(), score);
                            int guildScore = getPlayer().getGuild().getPointLogByType(GuildContentsType.FLAG_RACE, getPlayer());
                            if (weeklyRecord > 0) {
                                if (record < weeklyRecord) {
                                    self.say("\r\n#e[플래그 레이스 결과]\r\n\r\n#n- 기록 : #b" + recordToString + "(신기록 갱신!)#k\r\n- 이번 주 최고 기록 : #r" + recordToString + " #k\r\n- 이번 주 획득한 길드 랭킹 포인트 : " + guildScore + "점", ScriptMessageFlag.NpcReplacedByNpc);
                                    getPlayer().updateOneInfo(32581, "weeklyRecord", String.valueOf(record));
                                } else {
                                    self.say("\r\n#e[플래그 레이스 결과]\r\n\r\n#n- 기록 : #b" + recordToString + "#k\r\n- 이번 주 획득한 길드 랭킹 포인트 : #b" + guildScore + "점", ScriptMessageFlag.NpcReplacedByNpc);
                                }
                            } else if (weeklyRecord == 0) {
                                self.say("\r\n#e[플래그 레이스 결과]\r\n\r\n#n- 기록 : #b" + recordToString + "#k\r\n- 이번 주 획득한 길드 랭킹 포인트 : #b" + guildScore + "점", ScriptMessageFlag.NpcReplacedByNpc);
                                getPlayer().updateOneInfo(32581, "weeklyRecord", String.valueOf(record));
                            }
                        }
                    }
                    break;
                }
            }
        }
    }


}
