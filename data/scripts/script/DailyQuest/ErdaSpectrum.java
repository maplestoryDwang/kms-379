package script.DailyQuest;

import database.DBConfig;
import network.models.CField;
import network.models.FontColorType;
import network.models.FontType;
import objects.fields.fieldset.FieldSet;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import scripting.ScriptMessageFlag;
import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;

import java.text.SimpleDateFormat;
import java.util.Date;

import static objects.fields.fieldset.instance.ErdaSpectrum.*;

public class ErdaSpectrum extends ScriptEngineNPC {

    /**
     * qexID : 34170
     * count -> 일일 몇번했는지 횟수
     * date -> 21/06/16 언제 시행했는지
     * ctype -> 메세지뜨는용인듯 0이면 Failed
     * clear -> 1이면 450001550맵에서 보상과 맵이펙트
     */


    public void arcane1MO_Enter() {
        initNPC(MapleLifeFactory.getNPC(3003145));
        FieldSet fieldSet = fieldSet("ErdaSpectrumEnter");
        if (fieldSet == null) {
            self.sayOk("지금은 에르다 스펙트럼을 진행할 수 없습니다.");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
        Date lastTime = null;
        Date now = null;
        try {
            lastTime = sdf.parse(getPlayer().getOneInfo(34170, "date"));
            now = sdf.parse(sdf.format(new Date()));
        } catch (Exception e) {
        }
        if (getPlayer().getMap().getId() == 450001550) { //보상맵
            if (lastTime != null && lastTime.equals(now) && getPlayer().getOneInfoQuestInteger(34170, "clear") > 0) {
                int v = self.askMenu("#b#e<에르다 스펙트럼>#n#k\r\n\r\n몸은 좀 어떠신가요? 많이 다치진 않으셨죠?\r\n조사 때마다 느끼는 거지만 정말 쉬운 날이 없네요.\r\n#b#L0# <에르다 스펙트럼> 보상을 받는다.#l\r\n#b#L2# 보상을 받지 않고 돌아간다.#l");
                switch (v) {
                    case 0: //에르다 스펙트럼 보상을 받는다
                        String v0 = "조사를 무사하게 끝까지 도와주셔서 정말 감사해요!\r\n" +
                        "생각보다 어려운 조사였지만...\r\n" +
                                "#r에르다의 색깔#k은 정말 아름답지 않았나요?\r\n" +
                                "\r\n" +
                                "#i1712001##b#e#t1712001:##n#k #e2개#n\r\n" +
                                "#b#e경험치:#n#k#e87026100#n";
                        if (DBConfig.isGanglim) {
                            v0 = "조사를 무사하게 끝까지 도와주셔서 정말 감사해요!\r\n" +
                                    "생각보다 어려운 조사였지만...\r\n" +
                                    "#r에르다의 색깔#k은 정말 아름답지 않았나요?\r\n" +
                                    "\r\n" +
                                    "#i1712001##b#e#t1712001:##n#k #e2개#n\r\n" +
                                    "#b#e경험치:#n#k#e87026100000#n\r\n" +
                                    "#b#e100 강림포인트#n#k";
                        }
                        self.say(v0, ScriptMessageFlag.NpcReplacedByNpc);
                        if (target.exchange(1712001, 10) > 0) {
                            target.registerTransferField(450001000);
                            if (DBConfig.isGanglim) {
                                getPlayer().gainExpLong(87026100000L);
                                getPlayer().gainHongboPoint(100);
                            } else {
                                getPlayer().gainExp(87026100, true, true, true);
                            }
                            getPlayer().updateOneInfo(34170, "count", String.valueOf(getPlayer().getOneInfoQuestInteger(34170, "count") + 1));
                            getPlayer().updateOneInfo(34170, "clear", "0");
                        } else {
                            self.sayOk("장비창이 부족합니다. 장비창을 비워주시고 다시 말을 걸어주세요.", ScriptMessageFlag.NpcReplacedByNpc);
                        }
                        break;
                    case 2: //보상을 받지 않고 떠난다
                        self.say("네? 돌아가시겠다고요?\r\n아직 #r보상을 받지 않으셨는데요#k...", ScriptMessageFlag.NpcReplacedByNpc);
                        if (1 == self.askYesNo("정말 조사를 마무리하고 마을로 돌아갈까요?\r\n#r(보상을 받지 않아도 도전 횟수는 1회 감소됩니다.)", ScriptMessageFlag.NpcReplacedByNpc)) {
                            getPlayer().updateOneInfo(34170, "count", String.valueOf(getPlayer().getOneInfoQuestInteger(34170, "count") + 1));
                            getPlayer().updateOneInfo(34170, "clear", "0");
                            target.registerTransferField(450001000);
                        } else {
                            self.sayOk("그래요! 조사 성과에 대한 보상은 받고 가시는게 제 마음이 편하답니다.", ScriptMessageFlag.NpcReplacedByNpc);
                        }
                        break;
                }
            } else {
                int v = self.askMenu("#b#e<에르다 스펙트럼>#n#k\r\n\r\n몸은 좀 어떠신가요? 많이 다치진 않으셨죠?\r\n조사 때마다 느끼는 거지만 정말 쉬운 날이 없네요.\r\n#L0# 마을로 돌아간다.#l");
                if (v == 0) {
                    self.say("이번 조사는 아쉽게도 마무리 짓지 못했지만 다음엔 좋은 결과가 있을 거예요... 그럼 안녕히 가세요!", ScriptMessageFlag.NpcReplacedByNpc);
                    target.registerTransferField(100000000);
                }
            }

        } else {
            int v = -1;
            if (lastTime != null && lastTime.equals(now) && getPlayer().getOneInfoQuestInteger(34170, "count") > 0) {
                v = self.askMenu("#b#e<에르다 스펙트럼>#n#k\r\n에르다에도 #r고유의 색#k이 있다는 걸 알고 계시나요?\r\n제가 개발한 #b에르다 응집기#k로 주변의 에르다를 추출하다가 발견한 사실이랍니다.\r\n그런데 제가 부상을 입는 바람에 도움이 필요합니다...\r\n#b#L0# <에르다 스펙트럼>에 입장한다.#l\r\n#L1# 니나에게서 설명을 듣는다.#l\r\n#L2# 오늘의 남은 완료 가능 횟수를 확인한다.#l#k\r\n\r\n\r\n#e*1회 클리어 후 즉시 완료가 가능합니다.\r\n*오늘의 최고 보상 기록:   \r\n#i1712001##b#e#t1712001:##n #e10개");
            } else {
                if (lastTime != null && !lastTime.equals(now)) {
                    getPlayer().updateOneInfo(34170, "count", "0");
                }
                v = self.askMenu("#b#e<에르다 스펙트럼>#n#k\r\n에르다에도 #r고유의 색#k이 있다는 걸 알고 계시나요?\r\n제가 개발한 #b에르다 응집기#k로 주변의 에르다를 추출하다가 발견한 사실이랍니다.\r\n그런데 제가 부상을 입는 바람에 도움이 필요합니다...\r\n#b#L0# <에르다 스펙트럼>에 입장한다.#l\r\n#L1# 니나에게서 설명을 듣는다.#l\r\n#L2# 오늘의 남은 완료 가능 횟수를 확인한다.#l#k\r\n\r\n\r\n#e*1회 클리어 후 즉시 완료가 가능합니다.\r\n*오늘의 최고 보상 기록:   \r\n#i1712001##b#e#t1712001:##n #e0개");
            }
            switch (v) {

                case 0: { //<에르다 스펙트럼>에 입장한다.
                    if (getPlayer().getParty() != null && getPlayer().getParty().getLeader().getId() == getPlayer().getId()) {
                        int dailyCount = getPlayer().getOneInfoQuestInteger(34170, "count");
                        int amount = 0;
                        if (getPlayer().getLevel() >= 210) {
                            amount++;
                        }
                        if (getPlayer().getLevel() >= 220) {
                            amount++;
                        }
                        if (dailyCount >= 1 && dailyCount < 3 && amount > 0) {
                            if (1 == self.askYesNo("#b#ho##k님은 #b#e<에르다 스펙트럼>#n#k을 즉시 완료하실 수 있어요. 즉시 완료 시 입장 횟수가 #r#e1회 차감#n#k되니까 잊지 마세요!\r\n#r(취소 시 입장이 진행됩니다.)#k#e\r\n\r\n◆오늘 남은 즉시 완료 횟수 : #b" + (amount - (dailyCount - 1)) + "회#k\r\n◆즉시 완료 보상 : #b#t1712001:# 10개")) {
                                if (target.exchange(1712001, 10) > 0) {
                                    getPlayer().updateOneInfo(34170, "count", String.valueOf((dailyCount + 1)));
                                    self.sayOk("즉시 완료 보상을 지급해 드렸어요. 인벤토리를 확인해보세요.", ScriptMessageFlag.NpcReplacedByNpc);
                                } else {
                                    self.sayOk("장비창이 부족합니다. 장비창을 비워주시고 다시 말을 걸어주세요.", ScriptMessageFlag.NpcReplacedByNpc);
                                }
                            } else {
                                int enter = fieldSet.enter(target.getId(), 0);
                                if (enter == -1) self.say("알 수 없는 이유로 입장할 수 없습니다. 잠시 후에 다시 시도해 주십시오.");
                                else if (enter == 1)
                                    self.say("파티 상태가 아니라면 입장하실 수 없으세요. #b1~3명#k의 파티를 만든 뒤 다시 찾아와 주세요.");
                                else if (enter == 2) self.say("파티장을 통해 진행해 주세요.");
                                else if (enter == 3)
                                    self.say("최소 " + fieldSet.minMember + "인 이상의 파티가 퀘스트를 시작할 수 있습니다.");
                                else if (enter == 4) self.say("파티원의 레벨은 최소 " + fieldSet.minLv + " 이상이어야 합니다.");
                                else if (enter == 5) self.say("파티원이 모두 모여 있어야 시작할 수 있습니다.");
                                else if (enter == 6) self.say("이미 다른 파티가 안으로 들어가 퀘스트 클리어에 도전하고 있는 중입니다.");
                                else if (enter == -2) self.say("일일 가능횟수를 가득채운 파티원이 존재합니다. 확인 후 다시 말을 걸어주세요.");
                            }
                        } else {
                            int enter = fieldSet.enter(target.getId(), 0);
                            if (enter == -1) self.say("알 수 없는 이유로 입장할 수 없습니다. 잠시 후에 다시 시도해 주십시오.");
                            else if (enter == 1) self.say("파티 상태가 아니라면 입장하실 수 없으세요. #b1~3명#k의 파티를 만든 뒤 다시 찾아와 주세요.");
                            else if (enter == 2) self.say("파티장을 통해 진행해 주세요.");
                            else if (enter == 3) self.say("최소 " + fieldSet.minMember + "인 이상의 파티가 퀘스트를 시작할 수 있습니다.");
                            else if (enter == 4) self.say("파티원의 레벨은 최소 " + fieldSet.minLv + " 이상이어야 합니다.");
                            else if (enter == 5) self.say("파티원이 모두 모여 있어야 시작할 수 있습니다.");
                            else if (enter == 6) self.say("이미 다른 파티가 안으로 들어가 퀘스트 클리어에 도전하고 있는 중입니다.");
                            else if (enter == -2) self.say("일일 가능횟수를 가득채운 파티원이 존재합니다. 확인 후 다시 말을 걸어주세요.");
                        }
                    } else {
                        int enter = fieldSet.enter(target.getId(), 0);
                        if (enter == -1) self.say("알 수 없는 이유로 입장할 수 없습니다. 잠시 후에 다시 시도해 주십시오.");
                        else if (enter == 1) self.say("파티 상태가 아니라면 입장하실 수 없으세요. #b1~3명#k의 파티를 만든 뒤 다시 찾아와 주세요.");
                        else if (enter == 2) self.say("파티장을 통해 진행해 주세요.");
                        else if (enter == 3) self.say("최소 " + fieldSet.minMember + "인 이상의 파티가 퀘스트를 시작할 수 있습니다.");
                        else if (enter == 4) self.say("파티원의 레벨은 최소 " + fieldSet.minLv + " 이상이어야 합니다.");
                        else if (enter == 5) self.say("파티원이 모두 모여 있어야 시작할 수 있습니다.");
                        else if (enter == 6) self.say("이미 다른 파티가 안으로 들어가 퀘스트 클리어에 도전하고 있는 중입니다.");
                        else if (enter == -2) self.say("일일 가능횟수를 가득채운 파티원이 존재합니다. 확인 후 다시 말을 걸어주세요.");
                    }
                    break;
                }
                case 1: { //니나에게서 설명을 듣는다.
                    int vv = self.askMenu("소멸의 여로 근방의 에르다를 관찰하고 기록하는 조사원 니나라고 합니다.\r\n지난 조사 중 돌발 상황이 발생해서 경미한 부상을 입게 되었어요. 그래서 조사를 도와주실 분을 찾고 있어요.\r\n\r\n#e<에르다 스펙트럼>#n\r\n\r\n#e1. 참가 인원:#n 1~3인\r\n#e2. 제한시간:#n 10분\r\n#e3. 1일 클리어 가능 횟수:#n 3회 (클리어 할 때만 누적)\r\n#e4. 보상:#n #i1712001##b#e#t1712001:##n#k + 경험치\r\n\r\n\r\n#L0#더 자세한 설명을 듣는다.#l");
                    if (vv == 0) {
                        int vvv = -2;
                        while (vvv != 4 && vvv != 5 && vvv != -1 && !getSc().isStop()) {
                            vvv = self.askMenu("무엇을 알려 드릴까요?#b\r\n#L0#<조사 도움의 필요>#l\r\n#L1#<조사 방법>#l\r\n#L2#<에르다 획득 및 사용 방법>#l\r\n#L3#<진행과 보상>#l\r\n#L5#<일일 퀘스트 간편하게 하기>#l\r\n#L4#더 이상 설명은 필요 없습니다.#l#k");
                            switch (vvv) {
                                case 0: //<조사 도움의 필요>
                                    self.say("#e<조사 도움의 필요>#n\r\n\r\n소멸의 여로 근방은 수많은 #b에르다#k로 가득 차 있어요.\r\n#b에르다#k가 주변 환경에 공명하여 다양한 형태를 가질 수 있다는 점은 이미 알고 계시죠?\r\n이처럼 #r단정할 수 없는 에너지#k라는 점이 에르다의 매력이 아닌가 생각해요.");
                                    self.say("#e<조사 도움의 필요>#n\r\n\r\n저는 에르다의 조사를 위해 #b에르다 응집기#k를 발명했어요.\r\n#b에르다 응집기#k를 통해 주변의 에르다를 좀더 쉽게 추출할 수 있고, 그것들을 #r하나로 모을 수#k도 있답니다.\r\n\r\n그런데 응집 과정 중 흥미로운 점을 발견했어요.");
                                    self.say("#e<조사 도움의 필요>#n\r\n\r\n바로 #b에르다의 색깔#k!\r\n\r\n저는 이것에 매료되어 버렸답니다.\r\n지적 호기심을 자극하는 영롱하고 아름다운 색이었어요!\r\n\r\n하지만 아직 색깔을 결정짓는 요소도, 색깔에 따른 차별점도 발견하지 못했어요...\r\n그래서 더 많은 표본을 위해 주변 용사님들의 도움을 받고 있답니다.");
                                    getSc().flushSay();
                                    break;
                                case 1: //<조사 방법>
                                    self.say("#e<조사 방법>#n\r\n\r\n중앙에 설치된 #b에르다 응집기#k를 이용해서 추출된 에르다를 전송하는 것이 조사의 목표입니다.\r\n\r\n- 에르다 응집기는 #r몬스터 처치#k 시 획득된 에르다로 활성화\r\n- 활성화된 에르다 응집기 #r타격#k 시 응집된 에르다 추출\r\n- 양측 전송 구역에 색상에 맞는 응집된 에르다를 #b채집키#k로 넉백시켜 전송");
                                    break;
                                case 2: //<에르다 획득 및 사용 방법>
                                    self.say("#e<에르다 획득 및 사용 방법>#n\r\n\r\n- 에르다 #b획득#k :\r\n에르다 응집기 주변에서 #r몬스터를 처치#k하거나 응집기의 영향으로 발생한 #r집중 지점에 위치#k\r\n\r\n- 에르다 #b사용#k :\r\n#r에르다 응집기를 활성화#k 시키거나 #r에르다 응집기의 특수 기능 사용#k 시의 매개\r\n(※ 에르다 응집기는 #bnpc/채집키로 설정한 키#k로 작동)");
                                    break;
                                case 3: //<진행과 보상>
                                    self.say("#e<진행과 보상>#n\r\n\r\n클리어 시 잔여 에르다 및 소요 시간에 상관없이\r\n#r#i1712001##t1712001# 2개\r\n\r\n#b+ 일정 경험치#k 지급");
                                    break;
                                case 5: //<일일 퀘스트 간편하게 하기> -> 이건 스크립트 좀 수정해야함.
                                    self.sayOk("새로운 아케인리버 지역의 일일 퀘스트를 수행할 수 있게 되면 #e<에르다 스펙트럼>#n 임무를 더욱 손쉽게 완수 할 수 있도록 매일 #b#e<에르다 스펙트럼> 즉시 완료#n#k 기회를 드려요. #e즉시 완료#n#k를 이용하면 오늘 내가 기록한 최고 기록을 기준으로 퀘스트를 완료할 수 있답니다. 단, 경험치 보상 및 업적등과 관련된 내용은 기록되지 않으니 이 점 잊지마세요!\r\n\r\n\r\n#e#b오늘 가능한 <에르다 스펙트럼> 즉시 완료 횟수 (0/2)#n#k\r\n ▶츄츄아일랜드 지역:  #r#e일일 퀘스트 수행 가능#n#k\r\n ▶레헬른 지역:  #r#e일일 퀘스트 수행 가능#n#k");
                                    break;
                                case 4: //더 이상 설명은 필요 없습니다.
                                    break;
                            }
                        }
                    }
                    break;
                }
                case 2: { //오늘의 남은 완료 가능 횟수를 확인한다.
                    int aa = 3;
                    if (lastTime != null && lastTime.equals(now) && getPlayer().getOneInfoQuestInteger(34170, "count") > 0) {
                        aa = 3 - getPlayer().getOneInfoQuestInteger(34170, "count");
                    }
                    self.say("#h0#님의 오늘 남은 완료 가능 횟수는 #r" + aa + "회#k 입니다.\r\n\r\n(※ 조사를 #r끝까지 마치고 나오는 경우#k에만 횟수가 차감됩니다.)");
                    break;
                }
            }
        }
    }

    @Script
    public void arcane1MO_1() {
        getPlayer().send(CField.startMapEffect("   주변 몬스터를 사냥하면 획득할 수 있는 에르다를 모아 에르다 응집기를 가동해주세요!   ", 5120025, true, 15));
        getPlayer().send(ErdaSpectrumErdaInfo(0, 0, "red"));
        getPlayer().send(ErdaSpectrumPhase(2));
        getPlayer().send(ErdaSpectrumTimer(600000, 10));
    }

    @Script
    public void arcane1MO_2_1() {
        objects.fields.fieldset.instance.ErdaSpectrum fieldSet = (objects.fields.fieldset.instance.ErdaSpectrum) getPlayer().getMap().getFieldSetInstance();
        if (fieldSet != null) {
            fieldSet.setVar("Phase", "1");
            getPlayer().send(CField.startMapEffect("  에르다 응집기가 과부하가 되기 전에 주변에 몬스터가 증가하는 걸 막아주세요!  ", 5120025, true, 15));
            getPlayer().send(CField.UIPacket.sendBigScriptProgressMessage("에르다에 의해 이끌려온 모양이다. 주변은 푸른 빛이 일렁이더니 이내 사라졌다.", FontType.NanumGothicBold, FontColorType.SkyBlue));
            fieldSet.startCrackMap();
        }
    }

    @Script
    public void arcane1MO_2_2() { //지렁이맵
        objects.fields.fieldset.instance.ErdaSpectrum fieldSet = (objects.fields.fieldset.instance.ErdaSpectrum) getPlayer().getMap().getFieldSetInstance();
        if (fieldSet != null) {
            fieldSet.setVar("Phase", "1");
            getPlayer().send(CField.startMapEffect("  에르다 응집기를 파괴하려는 아르마 주니어를 쫓아내주세요!  ", 5120025, true, 15));
            getPlayer().send(CField.UIPacket.sendBigScriptProgressMessage("에르다에 의해 이끌려온 모양이다 주변은 붉은 빛이 일렁이더니 이내 사라졌다.", FontType.NanumGothicBold, FontColorType.Pink));
            fieldSet.startWormMap();
        }
    }


    @Script
    public void enter_450001550() {
        //클리어 했으면 클리어했다고 이펙트 !
        if (getPlayer().getOneInfoQuestInteger(34170, "ctype") == 1) { //클리어한거임
            getPlayer().send(CField.MapEff("Map/Effect.img/killing/clear"));
            getPlayer().updateOneInfo(34170, "ctype", "-1");
        } else if (getPlayer().getOneInfoQuestInteger(34170, "ctype") == 0) {
            getPlayer().send(CField.MapEff("Map/Effect.img/killing/fail"));
            getPlayer().updateOneInfo(34170, "ctype", "-1");
        }
    }
}
