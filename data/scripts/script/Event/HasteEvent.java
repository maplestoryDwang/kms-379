package script.Event;

import constants.QuestExConstants;
import network.models.CField;
import objects.quest.MapleQuest;
import scripting.newscripting.ScriptEngineNPC;

public class HasteEvent extends ScriptEngineNPC {

    public static String[] customData = {"", "", "", "", "count=0", "count=0", "RunAct=0", "suddenMK=0"};

    public void q16401s() {
        if (getPlayer().getQuestStatus(QuestExConstants.HasteEventInit.getQuestID()) < 1) {
            self.say("안녕! #b#h0##k!\\r\\n#e사냥 가속 #b<헤이스트>#k 이벤트#n가 돌아왔어!");
            self.say("#b#e<헤이스트>#n#k는 #b2021년 11월 4일 오전 0시#k부터 #b12월 5일 오후 11시 59분#k까지 #b여러 가지 변화#k가 생겨나 말 그대로 #b사냥을 가속#k시킬 수 있는 이벤트야!");
            self.say("어떤 변화가 생기냐면~!");
            self.say("#b- #e엘리트 몬스터#n가 #e더#n 자주 나와!\\r\\n- #e룬#n이 #e더#n 자주 나오고 #e더#n 자주 쓸 수 있어!\\r\\n- #e룬#n 경험치 효과가 두 배로 #e더#n 강력해져!\\r\\n- #e돌발미션#n을 하루에 두 배로 #e더#n 클리어할 수 있어!\\r\\n- #e폴로 & 프리토#n가 #e더#n 자주 나와!\\r\\n- #e불꽃늑대#n 경험치가 1.5배로 #e더#n 강력해져!");
            self.say("어때? 듣기만 해도 사냥이 팍팍 가속될 것 같지 않아?\\r\\n\\r\\n이뿐만이 아니야!\\r\\n이벤트 기간 동안 #b#e6가지 일일 미션#n#k을 수행할 수 있고,\\r\\n미션을 달성할 때 마다 #b#e<헤이스트 상자>#n#k를 받을 수 있어!");
            self.say("6개의 #b<헤이스트 상자>#k에서는 푸짐한 #b보상#k과 #b헤이스트 부스터#k를 얻을 수 있으니까 매일매일 놓치지 말라구~!\\r\\n#b헤이스트 부스터#k를 사용하면 100초동안 #b몬스터#k가 #b추가#k로 #b소환#k되니 사냥도 성장도 더 빠르게 가속시킬 수 있지!");
            self.say("하나 더!\\r\\n\\r\\n하루에 #e#b6가지 <헤이스트 일일 미션>#n#k을 전부 달성하면...\\r\\n흐흐... 그건 네가 직접 확인해 봐!");


            for (int i = QuestExConstants.HasteEventInit.getQuestID(); i <= QuestExConstants.HasteEventSuddenMK.getQuestID(); i++) {
                if (i != QuestExConstants.HasteEventEliteBoss.getQuestID()) {
                    MapleQuest.getInstance(i).forceStart(getPlayer(), 9010010, "");
                    getPlayer().updateInfoQuest(i, customData[i - QuestExConstants.HasteEventInit.getQuestID()]);
                }
            }


            getPlayer().updateInfoQuest(QuestExConstants.HasteEvent.getQuestID(),
                    "M1=0;M2=0;M3=0;M4=0;M5=0;M6=0;date=21/11/03;booster=0;openBox=0;unlockBox=0;str=1단계 상자 도전 중! 일일 미션 1개를 완료하세요!");
        } else {
            getPlayer().send(CField.UIPacket.openUI(1251));
        }
    }

    public void weekHQuest() {
        self.say("#b#e자, 드디어! <헤이스트 히든 미션>#n#k이 열렸어!\r\n\r\n#b2021년 12월 5일 오후 11시 59분#k까지\r\n#b#e레벨 범위 몬스터 44,444마리#n#k...가 아니라..#b#e88,888마리#n#k를 사냥하면 된다구!");
        self.say("#b#e<헤이스트 히든 미션 상자>#n#k에서는..\r\n#b#e#i2631097:# #t2631097:#,\r\n#i1114317:# #t1114317:##n#k을 받을 수 있으니 힘을 내!");
    }

    public void useHasteBooster() {
        if (1 == self.askYesNo("#r#e헤이스트 부스터#n#k를 사용할꺼야?\r\n#b#e100초동안 몬스터가 추가로 소환된다구!#n#k\r\n진정한 사냥 가속을 할 수 있지!\r\n\r\n#e<사용 할 수 없는 경우>#n\r\n 1. 레벨 범위 몬스터가 없는 필드 또는 마을.\r\n 2. 엘리트 보스가 소환 된 경우.\r\n 3. 현재 자신이 헤이스트 부스터를 사용 중인 경우.\r\n 4. 현재 다른 플레이어가 헤이스트 부스터를 사용 중인 경우.")) {
            getPlayer().getMap().startHasteBooster(getPlayer());
        }
    }

}
