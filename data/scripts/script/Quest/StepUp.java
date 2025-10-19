package script.Quest;

import network.models.CField;
import network.models.CWvsContext;
import objects.effect.EffectHeader;
import objects.effect.NormalEffect;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryType;
import objects.item.MapleItemInformationProvider;
import objects.quest.MapleQuest;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StepUp extends ScriptEngineNPC {

    public void q100865s() { //스텝업 시작퀘
        self.say("\r\n안녕!\r\n넌 #b강림 월드#k에 도착한 지 얼마 안 된 #b새로운 용사#k구나?", ScriptMessageFlag.NpcReplacedByNpc);
        self.say("\r\n반가워! 내 이름은 #b카산드라#k야!\r\n\r\n메이플 월드 최고의 #b점성술사#k이자 너와 같이 메이플 월드를 새롭게 찾은 용사들을 위한 #b안내자#k야!", ScriptMessageFlag.NpcReplacedByNpc);
        self.say("\r\n아직 온 지 얼마 안 돼서 이것저것 모르겠는게 많다고?\r\n\r\n오늘은 또 어떤 몬스터를 사냥해야 할지\r\n육성은 또 어떻게 해야 좋을지?", ScriptMessageFlag.NpcReplacedByNpc);
        self.say("\r\n#b고민고민하지 마~!#k\r\n\r\n나 #b카산드라#k님이 새내기 용사들의 성장을 도와줄\r\n#b#e<강림 월드 스텝업>#n#k 이벤트를 준비했으니까 말이야~!", ScriptMessageFlag.NpcReplacedByNpc);
        self.say("\r\n특정 레벨마다 주어지는 미션으로 #b스텝업~!#k\r\n미션 완료마다 주어지는 보상으로 #b스펙업~!#k", ScriptMessageFlag.NpcReplacedByNpc);
        if (1 == self.askYesNo("어때? 메이플 월드의 핵심 요소를 체계적으로 알려줄\r\n#b#e<강림 월드 스텝업>#n#k 이벤트, 너도 참여해 보지 않을래?", ScriptMessageFlag.NpcReplacedByNpc)) {
            for (int i = 501525; i <= 50155; i++) {
                getPlayer().updateInfoQuest(i, "");
            }
            getPlayer().updateInfoQuest(100865, "");
            SimpleDateFormat sdf = new SimpleDateFormat("YY/MM/dd/HH/mm;");
            String timeToString = sdf.format(new Date());
            getPlayer().updateInfoQuest(501524, "reward=00000000000000000000000000000;step=0;sTime=" + timeToString + "state=3");
            getPlayer().updateInfoQuest(501527, "value=0");
            getPlayer().updateInfoQuest(501540, "value=0");
            getPlayer().updateInfoQuest(501541, "value=0");
            getPlayer().updateInfoQuest(501524, "reward=00000000000000000000000000000;start=1;step=0;sTime=" + timeToString + "state=3");
            getQuest().forceComplete(getPlayer(), getNpc().getId());
            self.say("\r\n잘 생각했어~!\r\n후회 없는 선택이 될 거야~!", ScriptMessageFlag.NpcReplacedByNpc);
            self.say("\r\n이제 특정 레벨마다 #b스텝업 미션#k을 진행할 수 있어!\r\n\r\n미션과 진행 상태를 확인하고 싶을 때는 왼쪽 이벤트 알림이에서 #b#e스텝업 아이콘#n#k을 클릭하면 돼!", ScriptMessageFlag.NpcReplacedByNpc);
            //self.say("\r\n#b#e<버닝 월드 스텝업>#n#k은\r\n#b#e2021년 9월 9일(목) 점검 전#n#k까지 진행할 수 있어.\r\n\r\n그 이후에는 미션을 달성하거나 보상을 받을 수 없으니\r\n주의하도록 해! 알았지~?", ScriptMessageFlag.NpcReplacedByNpc);
            self.say("\r\n설명은 여기까지야, 간단하지?\r\n\r\n그럼 #b#e<강림 월드 스텝업>#n#k으로 신나는 모험되길 바랄게~!\r\n앞으로 이루어질 너의 멋진 성장에 #b치얼스#k~#r♥#k", ScriptMessageFlag.NpcReplacedByNpc);
            getSc().flushSay();
            getPlayer().send(CField.UIPacket.openUI(1160));

            NormalEffect e = new NormalEffect(getPlayer().getId(), EffectHeader.QuestClear);
            getPlayer().send(e.encodeForLocal());
        } else {
            self.say("\r\n스스로 개척하길 좋아하는 #b진취적인 스타일#k이구나!\r\n\r\n하지만 때로는 다른 사람의 #b노하우#k와 #b도움#k을 통해\r\n#b더 큰 성장#k을 이루어 낼 때도 있어!", ScriptMessageFlag.NpcReplacedByNpc);
            //self.say("\r\n#b<버닝 월드 스텝업>#k은 #b2021년 9월 9일(목) 점검 전#k까지\r\n참여할 수 있으니까 혹시 마음이 바뀌면 언제든지 말을 걸어줘~!\r\n\r\n난 언제나 이 자리에서 널 기다리고 있을게!", ScriptMessageFlag.NpcReplacedByNpc);
        }
    }

    public void StepUp_1() {
        /*
            self.say("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.", ScriptMessageFlag.NpcReplacedByNpc);
            self.say("\r\n저는 #b#h0##k님의 #b스텝업#k 진행을 돕게 될\r\n#b<스텝업 골>#k이라고 합니다.\r\n\r\n이름에서 알 수 있듯이 스텝업에서 가장 중요한\r\n#b<GOAL>칸#k을 담당하고 있습니다.", ScriptMessageFlag.NpcReplacedByNpc);
            self.say("\r\n#b#h0##k님을 #b한 스텝 한 스텝#k 발전하실 수 있도록 돕는 것이 #b저의 임무#k입니다.\r\n\r\n#b스텝업#k 기간 동안 잘 부탁드리겠습니다.", ScriptMessageFlag.NpcReplacedByNpc);
            self.say("\r\n그럼 첫 번째 스텝을 시작하겠습니다.\r\n\r\n첫 번째 스텝에서 알아볼 내용은\r\n#b#e<레벨 범위 몬스터>#n#k입니다.", ScriptMessageFlag.NpcReplacedByNpc);
            self.say("\r\n성장의 기본은 역시 #b몬스터 사냥#k이지요.\r\n\r\n그런데 경험치가 높다고 무조건 #r높은 레벨의 몬스터#k를\r\n사냥하는 건 좋은 방법이 아닙니다.", ScriptMessageFlag.NpcReplacedByNpc);
            self.say("\r\n레벨 차이가 #b10레벨 미만#k인 몬스터를 사냥하면\r\n레벨차에 따라 #b경험치 보너스#k를 얻으실 수 있습니다.\r\n\r\n반대로 #r10레벨 초과#k인 몬스터를 사냥하면\r\n레벨차에 따라 조금씩 #r경험치 패널티#k를 받게 됩니다.", ScriptMessageFlag.NpcReplacedByNpc);
            self.say("\r\n만약 레벨 차이가 #r20레벨 이상#k이면 사냥하기 몹시 힘들 뿐만 아니라 #r큰 폭의 경험치 패널티#k까지 받게 됩니다.\r\n\r\n오히려 #b사냥 효율#k이 떨어질 수 있지요.", ScriptMessageFlag.NpcReplacedByNpc);
            self.say("\r\n따라서 사냥하기 적당한 몬스터 레벨은\r\n자신과 레벨 차이 #b20레벨 이하#k 몬스터라고 볼 수 있습니다.\r\n\r\n그리고 이런 몬스터들을\r\n바로 #b<레벨 범위 몬스터>#k라고 부릅니다.", ScriptMessageFlag.NpcReplacedByNpc);
            self.say("\r\n#b[첫 번째 스텝업 미션]#k은\r\n#b#e<레벨 범위 몬스터 300마리 사냥>#n#k입니다.\r\n\r\n레벨 차이 20레벨 이하의 몬스터가 있는\r\n적당한 사냥터를 찾아 300마리 사냥해 보시기 바랍니다.", ScriptMessageFlag.NpcReplacedByNpc);
            self.say("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!", ScriptMessageFlag.NpcReplacedByNpc);
         */
        //public void check_step_up(int sLevel, int questID, List<String> questSay, int reward, int rewardqty, String rewardString, String mission) {
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n저는 #b#h0##k님의 #b스텝업#k 진행을 돕게 될\r\n#b<스텝업 골>#k이라고 합니다.\r\n\r\n이름에서 알 수 있듯이 스텝업에서 가장 중요한\r\n#b<GOAL>칸#k을 담당하고 있습니다.");
        questSay.add("\r\n#b#h0##k님을 #b한 스텝 한 스텝#k 발전하실 수 있도록 돕는 것이 #b저의 임무#k입니다.\r\n\r\n#b스텝업#k 기간 동안 잘 부탁드리겠습니다.");
        questSay.add("\r\n그럼 첫 번째 스텝을 시작하겠습니다.\r\n\r\n첫 번째 스텝에서 알아볼 내용은\r\n#b#e<광장 명령어 사용>#n#k입니다.");
        questSay.add("\r\nMMORPG의 기본은 유저들간의 만남이겠죠. 다른 유저들과 쉽게 소통할 수 있는 공간(광장)으로 쉽게 이동할 수 있는 명령어");
        questSay.add("\r\n#r@광장#k의 사용 방법을 아시나요? 채팅을통해 #r@광장#k 명령어를 사용하게 되면 서버내 특별한 공간!으로 쉽게 이동할 수 있게 됩니다!\r\n광장 명령어를 사용해 보시기 바랍니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(33, 501525, questSay, 2439580, 1, "10000000000000000000000000000", "<광장 명령어 사용>");
    }

    public void StepUp_2() {
        int level = 35;
        int questID = 501526;
        int reward = 2450042;
        int rewardQTY = 2;
        String rewardString = "11000000000000000000000000000";
        String mission = "<40 레벨 달성>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n두 번째 스텝업은 40Lv 달성하기 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_3() {
        int level = 40;
        int questID = 501527;
        int reward = 2000019;
        int rewardQTY = 500;
        String rewardString = "11100000000000000000000000000";
        String mission = "<45 레벨 달성>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n세 번째 스텝업은 45Lv 달성하기 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_4() {
        int level = 45;
        int questID = 501528;
        int reward = 2439581;
        int rewardQTY = 1;
        String rewardString = "11110000000000000000000000000";
        String mission = "<룬 사용>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n네 번째 스텝업은 룬 사용하기입니다.");
        questSay.add("\r\n사냥필드를 돌아다니다 보면 #r#e룬#n#k을 발견하실 수 있으실겁니다. 룬을 사용하게 되면 룬의 각종 효과 및 경험치 2배 버프가 걸리게 되며");
        questSay.add("\r\n필드에 소환된 룬을 사용하지 않고 일정시간이 지나게 되면 사냥터에 안좋은 패널티가 부여되게 됩니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_5() {
        int level = 50;
        int questID = 501529;
        int reward = 5076100;
        int rewardQTY = 5;
        String rewardString = "11111000000000000000000000000";
        String mission = "<55Lv 달성>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n다섯 번째 스텝업은 55Lv 달성하기 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_6() {
        //보상 주문의흔적으로
        int level = 55;
        int questID = 501530;
        int reward = 4001832;
        int rewardQTY = 9000;
        String rewardString = "11111100000000000000000000000";
        String mission = "<60Lv 달성>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n여섯 번째 스텝업은 60Lv 달성하기 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_7() {
        int level = 60;
        int questID = 501531;
        int reward = 2450042;
        int rewardQTY = 2;
        String rewardString = "11111110000000000000000000000";
        String mission = "<업그레이드 강화>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n그럼 일곱 번째 스텝을 시작하겠습니다.\r\n\r\n일곱 번째 스텝에서 알아볼 내용은\r\n#b#e<업그레이드 강화>#n#k입니다.");
        questSay.add("\r\n인벤토리(I) 버튼을 누르게 되면 #r빨간색 망치버튼#k을 발견하실 수 있으실 겁니다.");
        questSay.add("\r\n빨간색 버튼을 누르게 되면 강화UI가 등장하게 되는데 이 UI를 통해 강화를 진행할 수 있습니다.");
        questSay.add("\r\n강화에 필요한 재료는 #b주문의 흔적#k으로 아이템에 레벨제한이 높을수록 필요한 주문의 흔적이 늘어나게 되며 #b주문의 흔적#k은 여러 경로(대표적으로 사냥)를 통해 획득 하실 수 있으실 겁니다!");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_8() {
        //보상 일반 메소 럭키백 2개
        int level = 65;
        int questID = 501532;
        int reward = 2433019;
        int rewardQTY = 2;
        String rewardString = "11111111000000000000000000000";
        String mission = "<70Lv 달성>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n여덟 번째 스텝업은 70Lv 달성하기 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_9() {
        int level = 70;
        int questID = 501533;
        int reward = 2450042;
        int rewardQTY = 2;
        String rewardString = "11111111100000000000000000000";
        String mission = "<스타포스 강화>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n그럼 아홉 번째 스텝을 시작하겠습니다.\r\n\r\n아홉 번째 스텝에서 알아볼 내용은\r\n#b#e<스타포스 강화>#n#k입니다.");
        questSay.add("\r\n일곱 번째 스텝업에서 배운 아이템 강화하기를 기억 하시나요? 스타포스 강화 역시 빨간망치버튼(강화하기버튼)을 통해 진행할 수 있습니다.");
        questSay.add("\r\n주문의 흔적으로 업그레이드 강화가 전부다된 아이템을 강화 UI에 올리게 되면 스타포스 강화를 시도할 수 있게되는데 이를 진행하기 위해서는 일정량의 메소가 필요합니다.");
        questSay.add("\r\n이 역시 업그레이드 강화와 마찬가지로 레벨제한이 높은 아이템일수록 더 많은 메소를 필요로 요구하게 되며 강화 상태에따라 파괴확률도 존재하게 되니 주의하시기 바랍니다!");
        questSay.add("\r\n아홉 번째 스텝업은 스타포스 강화를 1회 시도하시면 됩니다!");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_10() {
        int level = 75;
        int questID = 501534;
        int reward = 2439582;
        int rewardQTY = 1;
        String rewardString = "11111111110000000000000000000";
        String mission = "<엘리트 몬스터, 엘리트 챔피언>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n그럼 열 번째 스텝을 시작하겠습니다.\r\n\r\n열 번째 스텝에서 알아볼 내용은\r\n#b#e<엘리트 몬스터, 엘리트 챔피언>#n#k입니다.");
        questSay.add("\r\n레벨에 맞는 사냥 필드에서 오랜시간 사냥하다보면 일반 몬스터보다 크기가 더 큰 #r엘리트 몬스터#k가 등장하게 됩니다.");
        questSay.add("\r\n#r엘리트 몬스터#k는 일반 몬스터보다 체력이 많은대신 다양한 보상아이템들이 드랍되게 됩니다.");
        questSay.add("\r\n열 번째 스텝업은 엘리트 몬스터를 1회 사냥하시면 됩니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_11() {
        int level = 80;
        int questID = 501535;
        int reward = 5044008;
        int rewardQTY = 1;
        String rewardString = "11111111111000000000000000000";
        String mission = "<90Lv 달성>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n열한 번째 스텝업은 90Lv 달성하기 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_12() {
        int level = 90;
        int questID = 501536;
        int reward = 2000019;
        int rewardQTY = 500;
        String rewardString = "11111111111100000000000000000";
        String mission = "<100Lv 달성>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n열두 번째 스텝업은 100Lv 달성하기 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_13() {
        int level = 100;
        int questID = 501537;
        int reward = 2439609;
        int rewardQTY = 1;
        String rewardString = "11111111111110000000000000000";
        String mission = "<101Lv 달성>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n열세 번째 스텝업은 101Lv 달성하기 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_14() {
        int level = 101;
        int questID = 501538;
        int reward = 2023836;
        int rewardQTY = 10;
        String rewardString = "11111111111111000000000000000";
        String mission = "<이벤트로 스텝업>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n그럼 열네 번째 스텝을 시작하겠습니다.\r\n\r\n열네 번째 스텝에서 알아볼 내용은\r\n#b#e<이벤트 UI확인하기>#n#k입니다.");
        questSay.add("\r\n강림 서버는 이벤트(V)키를 통해 진행중인 이벤트 리스트를 확인할 수 있던거 알고 계셨나요?");
        questSay.add("\r\n다양한 이벤트 일정 및 이벤트로 얻을 수 있는 아이템들을 확인할 수 있습니다.");
        questSay.add("\r\n열네 번째 스텝업은 이벤트(V)키를 이용해 이벤트 UI를 확인해 보기입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_15() {
        int level = 105;
        int questID = 501539;
        int reward = 2644017;
        int rewardQTY = 1;
        String rewardString = "11111111111111100000000000000";
        String mission = "<107Lv 달성>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n열다섯 번째 스텝업은 107Lv 달성하기 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_16() {
        int level = 107;
        int questID = 501540;
        int reward = 2437090;
        int rewardQTY = 1;
        String rewardString = "11111111111111110000000000000";
        String mission = "<110Lv 달성>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n열여섯 번째 스텝업은 110Lv 달성하기 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_17() {
        int level = 110;
        int questID = 501541;
        int reward = 2433019;
        int rewardQTY = 2;
        String rewardString = "11111111111111111000000000000";
        String mission = "<몬스터 파크>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n그럼 열일곱 번째 스텝을 시작하겠습니다.\r\n\r\n열일곱 번째 스텝에서 알아볼 내용은\r\n#b#e<몬스터 파크>#n#k입니다.");
        questSay.add("\r\n몬스터 파크는 막대한 경험치 및 요일별 다양한 보상을 얻을 수 있는 컨텐츠 입니다.");
        questSay.add("\r\n몬스터 파크는 컨텐츠 시스템을 통해 이용하실 수 있으며 권장레벨은 200레벨 이상입니다.");
        questSay.add("\r\n몬스터 파크를 많은 이용 바라며 이번 스텝업은 클리어 조건없이 미션 완료버튼을 누르시면 됩니다.");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_18() {
        int level = 119;
        int questID = 501542;
        int reward = 2000019;
        int rewardQTY = 500;
        String rewardString = "11111111111111111100000000000";
        String mission = "<130Lv 달성>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n열여덟 번째 스텝업은 130Lv 달성하기 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_19() {
        int level = 130;
        int questID = 501543;
        int reward = 2450042;
        int rewardQTY = 2;
        String rewardString = "11111111111111111110000000000";
        String mission = "<스타포스 45성>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n그럼 열아홉 번째 스텝을 시작하겠습니다.\r\n\r\n열아홉 번째 스텝에서 알아볼 내용은\r\n#b#e<스타포스 45성>#n#k입니다.");
        questSay.add("\r\n아홉 번째 스텝업에서 배웠던 스타포스 강화를 기억하시나요? 다양한 사냥터를 이용하기 위해, 더 강해지기 위해서는 더 높은 스타포스를 요구 됩니다.");
        questSay.add("\r\n열아홉 번째 스텝업은 장착하고 있는 장비의 스타포스 총 수치가 45를 넘어보자 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        int chuk = 0;
        for (Item item : getPlayer().getInventory(MapleInventoryType.EQUIPPED).list()) {
            Equip equip = (Equip) item;
            if (equip != null) {
                chuk += equip.getCHUC();
            }
        }
        if (chuk >= 45) {
            if (getPlayer().isQuestStarted(501543)) {
                if (getPlayer().getOneInfoQuestInteger(501543, "value") < 1) {
                    getPlayer().updateOneInfo(501543, "value", "1");
                }
                if (getPlayer().getOneInfoQuestInteger(501524, "state") < 2) {
                    getPlayer().updateOneInfo(501524, "state", "2");
                }
            }
        }
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_20() {
        int level = 140;
        int questID = 501544;
        int reward = 2631822;
        int rewardQTY = 1;
        String rewardString = "11111111111111111111000000000";
        String mission = "<노멀 보스>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n드디어 스무 번째 스텝업 입니다! 축하드립니다.");
        questSay.add("\r\n그럼 스무 번째 스텝을 시작하겠습니다.\r\n\r\n스무 번째 스텝에서 알아볼 내용은\r\n#b#e<노멀 보스>#n#k입니다.");
        questSay.add("\r\n메이플스토리에는 다양한 보스가 존재 합니다. 그 중에서도 대표되는 보스 #b자쿰#k을 아시나요?");
        questSay.add("\r\n자쿰은 노말 모드, 카오스 모드가 존재하며 이번 스무 번째 스텝업에서 잡아볼 보스 몬스터는 노말 자쿰입니다!");
        questSay.add("\r\n보스UI(T) 단축키를 이용해 보스를 입장할 수 있으며 보스 클리어 여부또한 확인 가능합니다.");
        questSay.add("\r\n스무 번째 스텝업은 노말 자쿰 처치하기 입니다!");
        questSay.add("\r\n아참 혹시라도 카오스 자쿰을 격파해보고 싶다면 카오스 자쿰을 잡아도 퀘스트 완료가 가능하니 카오스 자쿰에 도전하실 분은 도전하셔도 됩니다!");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_21() {
        //보상 영환불로
        int level = 150;
        int questID = 501545;
        int reward = 2048723;
        int rewardQTY = 10;
        String rewardString = "11111111111111111111100000000";
        String mission = "<스타포스 80성>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n그럼 스물한 번째 스텝을 시작하겠습니다.\r\n\r\n스물한 번째 스텝에서 알아볼 내용은\r\n#b#e<스타포스 80성>#n#k입니다.");
        questSay.add("\r\n스물한 번째 스텝업은 장착하고 있는 장비의 스타포스 총 수치가 80을 넘어보자 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        int chuk = 0;
        for (Item item : getPlayer().getInventory(MapleInventoryType.EQUIPPED).list()) {
            Equip equip = (Equip) item;
            if (equip != null) {
                chuk += equip.getCHUC();
            }
        }
        if (chuk >= 80) {
            if (getPlayer().isQuestStarted(501545)) {
                if (getPlayer().getOneInfoQuestInteger(501545, "value") < 1) {
                    getPlayer().updateOneInfo(501545, "value", "1");
                }
                if (getPlayer().getOneInfoQuestInteger(501524, "state") < 2) {
                    getPlayer().updateOneInfo(501524, "state", "2");
                }
            }
        }
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_22() {
        //어빌리티 서큘레이터 보상으로
        int level = 160;
        int questID = 501546;
        int reward = 5062800;
        int rewardQTY = 10;
        String rewardString = "11111111111111111111110000000";
        String mission = "<추가옵션>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n그럼 스물두 번째 스텝을 시작하겠습니다.\r\n\r\n스물두 번째 스텝에서 알아볼 내용은\r\n#b#e<추가옵션>#n#k입니다.");
        questSay.add("\r\nMMORPG에서 강해지는 방법 중 하나 바로 더 강한 아이템 장착이지요!");
        questSay.add("\r\n더 강한 아이템을 장착하는 방법중 하나 자신에게 더 좋은 추가옵션이 있는 아이템 장착하기!");
        questSay.add("\r\n추가옵션은 강력한 환생의 불꽃, 영원한 환생의 불꽃, 검은 환생의 불꽃 아이템을 통해 재설정 가능합니다.");
        questSay.add("\r\n스물두 번째 스텝업은 환생의 불꽃 아이템을 통해 추가옵션 재설정하기 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_23() {
        int level = 170;
        int questID = 501547;
        int reward = 2439583;
        int rewardQTY = 1;
        String rewardString = "11111111111111111111111000000";
        String mission = "<어빌리티>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n그럼 스물세 번째 스텝을 시작하겠습니다.\r\n\r\n스물세 번째 스텝에서 알아볼 내용은\r\n#b#e<어빌리티>#n#k입니다.");
        questSay.add("\r\n캐릭터를 강하게 만들기 위해서는 캐릭터 내면에 숨겨진 힘 어빌리티를 좋게 만드는 방법도 있습니다.");
        questSay.add("\r\n최초에 어빌리티는 왼쪽 전구 어빌리티 퀘스트를 통해 부여받을 수 있게되며");
        questSay.add("\r\n어빌리티의 재설정은 등급별 필요 명성치를 이용해 스탯창(S) 및 서큘레이터 아이템을 통해 재설정 할 수 있습니다.");
        questSay.add("\r\n스물세 번째 스텝업은 어빌리티 재설정하기 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_24() {
        int level = 175;
        int questID = 501548;
        int reward = 5680148;
        int rewardQTY = 1;
        String rewardString = "11111111111111111111111100000";
        String mission = "<무릉도장>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n그럼 스물네 번째 스텝을 시작하겠습니다.\r\n\r\n스물네 번째 스텝에서 알아볼 내용은\r\n#b#e<무릉도장>#n#k입니다.");
        questSay.add("\r\n무릉도장은 각 층에 배치되어있는 보스들을 한층한층 격파해나가며 더 위로 올라가야하는 컨텐츠입니다!");
        questSay.add("\r\n물론 위로 올라갈수록 더 강한 보스들이 존재하며 더 높은층을 올라갈수록 주마다 진행되는 무릉도장 정산을 통해 다양한 아이템을 획득할 수 있습니다.");
        questSay.add("\r\n무릉도장을 많은 이용 바라며 이번 스텝업은 클리어 조건없이 미션 완료버튼을 누르시면 됩니다.");
        questSay.add("\r\n아! 참고로 무릉도장 역시 몬스터파크와 같이 컨텐츠 시스템을 이용해 이용하실 수 있습니다. 감사합니다.");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_25() {
        int level = 179;
        int questID = 501549;
        int reward = 2439584;
        int rewardQTY = 1;
        String rewardString = "11111111111111111111111110000";
        String mission = "<190Lv 달성>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n스물다섯 번째 스텝업은 190Lv 달성하기 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_26() {
        //보상으로 선택형 아케인심볼 100개
        int level = 190;
        int questID = 501550;
        int reward = 2439585;
        int rewardQTY = 1;
        String rewardString = "11111111111111111111111111000";
        String mission = "<200Lv 달성>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n스물여섯 번째 스텝업은 200Lv 달성하기 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_27() {
        int level = 200;
        int questID = 501551;
        int reward = 2439586;
        int rewardQTY = 1;
        String rewardString = "11111111111111111111111111100";
        String mission = "<소멸의 여로>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n그럼 스물일곱 번째 스텝을 시작하겠습니다.\r\n\r\n스물일곱 번째 스텝에서 알아볼 내용은\r\n#b#e<소멸의 여로>#n#k입니다.");
        questSay.add("\r\n소멸의 여로 이후 사냥터부터는 아케인 포스를 요구하게 됩니다. 물론 이 아케인 포스는 몬스터가 강해지면 강해질수록 더 높은 아케인 포스를 요구하게 되며");
        questSay.add("\r\n아케인 포스를 높이는 방법으로는 일일 퀘스트, 각종 이벤트, 일일 마을별 컨텐츠, 슈피겔만의 퀵패스를 이용해 아케인 심볼을 획득해 아케인 포스를 늘릴 수 있습니다.");
        questSay.add("\r\n스물일곱 번째 스텝업은 슈피겔만(아케인리버 퀵패스) 엔피시에게 말을 걸어보자 입니다.");
        questSay.add("\r\n슈피겔만(아케인리버 퀵패스)엔피시는 각종 마을(이름 없는 마을, 츄츄 아일랜드 등)에 위치하고 있습니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_28() {
        int level = 210;
        int questID = 501552;
        int reward = 2439587;
        int rewardQTY = 1;
        String rewardString = "11111111111111111111111111110";
        String mission = "<스타포스 120성>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n그럼 스물여덟 번째 스텝을 시작하겠습니다.\r\n\r\n스물여덟 번째 스텝에서 알아볼 내용은\r\n#b#e<스타포스 120성>#n#k입니다.");
        questSay.add("\r\n스물여덟 번째 스텝업은 장착하고 있는 장비의 스타포스 총 수치가 120을 넘어보자 입니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        int chuk = 0;
        for (Item item : getPlayer().getInventory(MapleInventoryType.EQUIPPED).list()) {
            Equip equip = (Equip) item;
            if (equip != null) {
                chuk += equip.getCHUC();
            }
        }
        if (chuk >= 120) {
            if (getPlayer().isQuestStarted(501552)) {
                if (getPlayer().getOneInfoQuestInteger(501552, "value") < 1) {
                    getPlayer().updateOneInfo(501552, "value", "1");
                }
                if (getPlayer().getOneInfoQuestInteger(501524, "state") < 2) {
                    getPlayer().updateOneInfo(501524, "state", "2");
                }
            }
        }
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void StepUp_29() {
        int level = 220;
        int questID = 501553;
        int reward = 2439588;
        int rewardQTY = 1;
        String rewardString = "11111111111111111111111111111";
        String mission = "<아케인포스>";
        List<String> questSay = new ArrayList<>();
        questSay.add("\r\n안녕하십니까, #b#h0##k님.\r\n#b<스텝업>#k에 오신 걸 환영합니다.");
        questSay.add("\r\n드.디.어 마지막 스텝업입니다. 고생 많으셨습니다.");
        questSay.add("\r\n스텝업 기간동안 엘리트 몬스터도 사냥하랴 룬도 사용하랴 어빌리티도 재설정하랴 고생 많으셨습니다.");
        questSay.add("\r\n그럼 마지막 스텝업을 시작하겠습니다.");
        questSay.add("\r\n마지막 스텝업은 <아케인포스>로 스텝업! 입니다.");
        questSay.add("\r\n220레벨을 달성한 용사님 정도면 일일 퀘스트 및 각종 이벤트로 아케인포스를 가득 채웠겠지요!??");
        questSay.add("\r\n마지막 스텝업의 내용은 아케인포스 3000채우기 입니다.");
        questSay.add("\r\n220인데 3000이 너무 많은거 아.니.냐.구.요?");
        questSay.add("\r\n저는 모르겠습니다.");
        questSay.add("\r\n\r\n그럼 미션 완료 후 다시 뵙도록 하겠습니다.\r\n건투를 빕니다!");
        questSay.add("\r\n라고 할뻔! 지금까진 장난이였구 스텝업 퀘스트를 하느라 고생 많으셨습니다. 엔피시 대화가 종료된뒤에 미션 완료를 누르시면 됩니다. 그럼 수고많으셨습니다. 감사합니다.");
        check_step_up(level, questID, questSay, reward, rewardQTY, rewardString, mission);
    }

    public void check_step_up(int sLevel, int questID, List<String> questSay, int reward, int rewardqty, String rewardString, String mission) {
        if (getPlayer().getLevel() < sLevel) {
            self.sayOk("레벨" + sLevel + "이상만 미션을 수행할 수 있습니다.", ScriptMessageFlag.NpcReplacedByNpc);
            return;
        }
        if (getPlayer().getQuestStatus(questID) < 1) { //퀘스트 안받아져있는 상태
            for (String q : questSay) {
                self.say(q, ScriptMessageFlag.NpcReplacedByNpc, ScriptMessageFlag.NoEsc);
            }
            getSc().flushSay();
            MapleQuest.getInstance(questID).forceStart(getPlayer(), getNpc().getId(), null);
            getPlayer().updateOneInfo(501524, "state", "1");
            switch (questID) {
                case 501526: //두번째 스텝업
                    if (getPlayer().getLevel() >= 40) {
                        getPlayer().updateOneInfo(501524, "state", "2");
                        getPlayer().updateOneInfo(501526, "value", "1");
                    }
                    break;
                case 501527: //세번째 스텝업
                    if (getPlayer().getLevel() >= 45) {
                        getPlayer().updateOneInfo(501524, "state", "2");
                        getPlayer().updateOneInfo(501527, "value", "1");
                    }
                    break;
                case 501529:
                    if (getPlayer().getLevel() >= 55) {
                        getPlayer().updateOneInfo(501524, "state", "2");
                        getPlayer().updateOneInfo(501529, "value", "1");
                    }
                    break;
                case 501530:
                    if (getPlayer().getLevel() >= 60) {
                        getPlayer().updateOneInfo(501524, "state", "2");
                        getPlayer().updateOneInfo(501530, "value", "1");
                    }
                    break;
                case 501532:
                    if (getPlayer().getLevel() >= 70) {
                        getPlayer().updateOneInfo(501524, "state", "2");
                        getPlayer().updateOneInfo(501532, "value", "1");
                    }
                    break;
                case 501535:
                    if (getPlayer().getLevel() >= 90) {
                        getPlayer().updateOneInfo(501524, "state", "2");
                        getPlayer().updateOneInfo(501535, "value", "1");
                    }
                    break;
                case 501536:
                    if (getPlayer().getLevel() >= 100) {
                        getPlayer().updateOneInfo(501524, "state", "2");
                        getPlayer().updateOneInfo(501536, "value", "1");
                    }
                    break;
                case 501537:
                    if (getPlayer().getLevel() >= 101) {
                        getPlayer().updateOneInfo(501524, "state", "2");
                        getPlayer().updateOneInfo(501537, "value", "1");
                    }
                    break;
                case 501539:
                    if (getPlayer().getLevel() >= 107) {
                        getPlayer().updateOneInfo(501524, "state", "2");
                        getPlayer().updateOneInfo(501539, "value", "1");
                    }
                    break;
                case 501540:
                    if (getPlayer().getLevel() >= 110) {
                        getPlayer().updateOneInfo(501524, "state", "2");
                        getPlayer().updateOneInfo(501540, "value", "1");
                    }
                    break;
                case 501541:
                    getPlayer().updateOneInfo(501524, "state", "2");
                    getPlayer().updateOneInfo(501541, "value", "1");
                    break;
                case 501548:
                    getPlayer().updateOneInfo(501524, "state", "2");
                    getPlayer().updateOneInfo(501548, "value", "1");
                    break;
                case 501553:
                    getPlayer().updateOneInfo(501524, "state", "2");
                    getPlayer().updateOneInfo(501553, "value", "1");
                    break;
                case 501542:
                    if (getPlayer().getLevel() >= 130) {
                        getPlayer().updateOneInfo(501524, "state", "2");
                        getPlayer().updateOneInfo(501542, "value", "1");
                    }
                    break;
                case 501543:
                    int chuk = 0;
                    for (Item item : getPlayer().getInventory(MapleInventoryType.EQUIPPED).list()) {
                        Equip equip = (Equip) item;
                        if (equip != null) {
                            chuk += equip.getCHUC();
                        }
                    }
                    if (chuk >= 45) {
                        if (getPlayer().isQuestStarted(501543)) {
                            if (getPlayer().getOneInfoQuestInteger(501543, "value") < 1) {
                                getPlayer().updateOneInfo(501543, "value", "1");
                            }
                            if (getPlayer().getOneInfoQuestInteger(501524, "state") < 2) {
                                getPlayer().updateOneInfo(501524, "state", "2");
                            }
                        }
                    }
                    break;
                case 501545:
                    chuk = 0;
                    for (Item item : getPlayer().getInventory(MapleInventoryType.EQUIPPED).list()) {
                        Equip equip = (Equip) item;
                        if (equip != null) {
                            chuk += equip.getCHUC();
                        }
                    }
                    if (chuk >= 80) {
                        if (getPlayer().isQuestStarted(501545)) {
                            if (getPlayer().getOneInfoQuestInteger(501545, "value") < 1) {
                                getPlayer().updateOneInfo(501545, "value", "1");
                            }
                            if (getPlayer().getOneInfoQuestInteger(501524, "state") < 2) {
                                getPlayer().updateOneInfo(501524, "state", "2");
                            }
                        }
                    }
                    break;
                case 501552:
                    chuk = 0;
                    for (Item item : getPlayer().getInventory(MapleInventoryType.EQUIPPED).list()) {
                        Equip equip = (Equip) item;
                        if (equip != null) {
                            chuk += equip.getCHUC();
                        }
                    }
                    if (chuk >= 120) {
                        if (getPlayer().isQuestStarted(501552)) {
                            if (getPlayer().getOneInfoQuestInteger(501552, "value") < 1) {
                                getPlayer().updateOneInfo(501552, "value", "1");
                            }
                            if (getPlayer().getOneInfoQuestInteger(501524, "state") < 2) {
                                getPlayer().updateOneInfo(501524, "state", "2");
                            }
                        }
                    }
                    break;
                case 501549:
                    if (getPlayer().getLevel() >= 190) {
                        getPlayer().updateOneInfo(501524, "state", "2");
                        getPlayer().updateOneInfo(501549, "value", "1");
                    }
                    break;
                case 501550:
                    if (getPlayer().getLevel() >= 200) {
                        getPlayer().updateOneInfo(501524, "state", "2");
                        getPlayer().updateOneInfo(501550, "value", "1");
                    }
                    break;
            }
        } else {
            //if (!getPlayer().getQuest(questID).getQuest().canComplete(getPlayer(), null)) {
            if (getPlayer().getOneInfoQuestInteger(questID, "value") == 1 && (getPlayer().isQuestStarted(questID)/* || ((questID - 501525 + 1) == getPlayer().getOneInfoQuestInteger(501524, "step"))*/)) {
                if (1 == self.askYesNo("\r\n#b[" + sLevel + "레벨 스텝업 미션]#k 달성을 축하드립니다!\r\n지금 바로 #b미션 보상#k을 받으시겠습니까?\r\n\r\n\r\n#fUI/UIWindow2.img/Quest/quest_info/summary_icon/reward#\r\n#b#i" + reward + ":# #t" + reward + ":# " + rewardqty + "개#k\r\n", ScriptMessageFlag.NpcReplacedByNpc)) {
                    MapleItemInformationProvider mif = MapleItemInformationProvider.getInstance();
                    if (mif.getItemInformation(reward) != null) {
                        String itemName = mif.getName(reward);
                        if (target.exchange(reward, rewardqty) > 0) {
                            getPlayer().completeQuest(questID);
                            getPlayer().updateOneInfo(501524, "reward", rewardString);
                            getPlayer().updateOneInfo(501524, "state", "3");
                            getPlayer().send(CWvsContext.getScriptProgressMessage(reward, " " + itemName + " " + rewardqty + "개를 획득하였습니다!"));
                        } else {
                            self.sayOk("인벤토리의 공간이 부족합니다. 공간을 넉넉히 비운뒤 다시 시도해주세요!", ScriptMessageFlag.NpcReplacedByNpc);
                        }
                    } else {
                        self.sayOk("오류가 발생했습니다.");
                    }
                }
            } else {
                if (getPlayer().getQuestStatus(questID) == 2) {
                    self.say("이미 깬 스텝업 미션이거나 필요한 레벨을 달성하지 못했습니다.");
                } else {
                    int v = self.askMenu("#b#e[" + sLevel + "레벨 스텝업 미션]#k 진행 중입니다!\r\n\r\n\r\n<미션 완료 조건>#n\r\n" + mission + "\r\n\r\n#b#L0#미션 설명 다시 듣기#l#k", ScriptMessageFlag.NpcReplacedByNpc);
                    if (v == 0) {
                        for (String q : questSay) {
                            self.say(q, ScriptMessageFlag.NpcReplacedByNpc);
                        }
                    }
                }
            }
        }
    }


}
