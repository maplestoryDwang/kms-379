package script.FieldSet;

import network.models.CField;
import objects.fields.fieldset.childs.CulvertEnter;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import scripting.ScriptMessageFlag;
import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;

import objects.users.stats.SecondaryStatFlag;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SharenianUndergroundCulvert extends ScriptEngineNPC {


    public void Culvert_NPC() {
        int v = self.askMenu("#fs11#샤레니안의 악마, 아르카누스에게 도전하시겠습니까?\r\n\r\n#L1# #b샤레니안의 지하 수로에 입장한다.#k#l\r\n#L2# #b샤레니안의 지하 수로는 어떤 공간인가요?#k#l", ScriptMessageFlag.NpcReplacedByNpc);
        switch (v) {
            case 1: //샤레니안의 지하 수로에 입장한다.

                registerTransferField(941000000);
                break;
            case 2: //샤레니안의 지하 수로는 어떤 공간인가요?
                self.say("#fs11##r#e샤레니안의 지하 수로#k#n에 대해 궁금하신가요?", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("#fs11#아주 먼 옛날... 샤레니안이라는 고대의 왕국이 있었습니다. 비록 끔찍한 사건으로 인해 왕국이 통째로 멸망하고 말았지만요.", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("#fs11#샤레니안의 유적지를 탐험하려는 시도는 여러 번 있었지만 쉽지 않았고, 결국 그곳으로 향하는 길은 잊히고 말았습니다.\r\n\r\n#r#e얼마 전까지만 해도 말이지요...", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("#fs11#전 이전부터 샤레니안의 유적지를 조사 중이었는데 사고로 인해 아무도 가지 않았던 곳에서 #b샤레니안의 지하 수로로 통하는 길을 발견했습니다.#k", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("#fs11#매우 많은 입구들이 미로처럼 얽혀있지만 어느 길로 가도 불길한 기운이 잠들어 있는 제단이 있는 곳으로 갈 수 있었지요.", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("#fs11#길드 본부는 지하 수로 조사를 결정했습니다. 지하 수로 입구에서 저에게 말을 걸어서 샤레니안의 지하 수로로 통하는 문을 열면, 불길한 기운의 제단으로 향하는 통로로 갈 수 있습니다.", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("#fs11#조사는 혼자서만 진행할 수 있고 중간에 길드를 탈퇴하거나 추방되면 던전 포인트를 획득할 수 없습니다. 또한, 길드에서 탈퇴하거나 추방당하면 해당 길드의 총 포인트에서 차감되니 주의하시길 바랍니다.", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("#fs11#조사는 제한 없이 진행할 수 있습니다. 단, 지하 수로의 불길한 기운으로 인해 입장 시 모든 버프 효과가 해제되므로 신중하게 결정하시길 바랍니다.", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("#fs11#제단으로 향하는 통로에서는 잠시 동안 버프 스킬과 소비 아이템을 사용할 수 있으니 이후 올 시련에 대비하실 수 있습니다.\r\n단, 일정 시간 내 제단으로 입장하지 못하면 지하 수로에서 퇴장하게 되니 주의하십시오.", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("#fs11#수로의 가장 깊은 곳에 자리한 불길한 기운의 제단에서 숨죽인 채 때를 기다리던 존재가 곧 깨어날 겁니다. 바로 \r\n\r\n                   #r샤레니안의 악마, 아르카누스#k\r\n\r\n입니다.", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("#fs11#그는 불멸의 존재... 공격을 받을수록 점점 더 강한 존재가 되어 태어나니 조심해야 합니다. 가장 강력한 상태로 재탄생되면 아무리 강한 공격을 받더라도 그는 결코 쓰러지는 모습을 보이지 않을 것이니 주의를 기울이는 것이 좋습니다.\r\n또한 아르카누스의 공격에 의해 사망한다면 곧바로 제단 밖으로 이동되니 주의하시길 바랍니다.", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("#fs11#지하 수로의 존재들에게 입힌 피해에 비례해 던전 포인트를 얻을 수 있고 월요일 0시마다 목표 달성에 따라 길드 본부에서 포상으로 노블레스 SP를 지급하니 힘내서 도전해보십시오. 다만 길드의 총 던전 포인트가 일정 점수 이상이 되어야 받을 수 있을 겁니다.", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("#fs11#또, 포인트 순위에 따라 추가로 노블레스 SP를 얻을 수도 있고 지하 수로에서 퇴장하고 일정 시간이 지나야 던전 포인트가 정산되니 참고하십시오.\r\n#r각 길드원의 던전 포인트 합산에 일정 시간이 소요되어 랭킹에 모두 반영 되기까지 어느정도 시간이 필요하오니 포인트가 적게 기록되어도 당황하지 말고 잠시 후 다시 확인해 보십시오.", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("#fs11##b#e1위 : 25개의 SP\r\n2위 : 23개의 SP\r\n3위 : 21개의 SP\r\n4위 ~ 10위 : 20개의 SP\r\n상위 10% : 19개의 SP\r\n상위 30% : 17개의 SP\r\n상위 60% : 15개의 SP\r\n상위 80% : 10개의 SP\r\n500포인트 이상 : 5개의 SP\r\n\r\n#r#e※ 단, 500포인트 이상 획득하지 못하면 순위 안에 들었더라도 보상을 받을 수 없습니다.#k#n", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("#fs11#제가 함께 수로 속으로 갈 테니 길을 잃을 염려는 하지 않으셔도 됩니다. 어서 도전하시지요.", ScriptMessageFlag.NpcReplacedByNpc);
                break;
        }
    }

    public void npc_2012041() {
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd/HH/mm");
        Date lastTime = null;
        try {
            lastTime = sdf.parse(getPlayer().getOneInfo(100811, "date"));
        } catch (Exception e) {
            //e.printStackTrace();
            lastTime = null;
        }
        if (lastTime != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(lastTime);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.DATE, cal.getTime().getDate() + 7);
            if (cal.getTime().getTime() <= new Date().getTime()) {
                getPlayer().updateOneInfo(100811, "point", "0");
                getPlayer().updateOneInfo(100811, "complete", "0");
                getPlayer().updateOneInfo(100811, "date", sdf.format(new Date()));
                getPlayer().updateOneInfo(100811, "guildPoint", "0");
                getPlayer().updateOneInfo(100811, "weeklyRecord", "0");
            }
        }

        switch (getPlayer().getMapId()) {
            case 941000100: {
                if (1 == self.askYesNo("#fs11#준비가 완료되셨다면 안내해드리겠습니다.\r\n\r\n#b#e지금 제단으로 이동하시겠습니까?#n#k", ScriptMessageFlag.NpcReplacedByNpc)) {
                    registerTransferField(941000200);
                } else {
                    self.sayOk("#fs11#그렇다면 정비를 마친 후 다시 시도해주세요.", ScriptMessageFlag.NpcReplacedByNpc);
                }
                break;
            }
            case 941000001: {
                if (getPlayer().getOneInfoQuestInteger(100811, "complete") > 0) {
                    int point = getPlayer().getOneInfoQuestInteger(100811, "point");
                    int weeklyRecord = getPlayer().getOneInfoQuestInteger(100811, "weeklyRecord");
                    int v = -1;
                    if (point > weeklyRecord) {
                        v = self.askMenu("#fs11#제의 불길한 기운이 조금은 줄어든 것 같습니다.\r\n다시 지하 수로 입구로 안내해드리겠습니다.\r\n\r\n#e- 획득 점수: #n#b#e" + point + "점#n#k#e#b (이번 주 신기록!)#n#k\r\n#e- 이번 주 최고 점수: #n#r#e" + point + "점#n#k\r\n#b#L1# 지하 수로 입구로 돌아간다.#k#l\r\n#b#L2# 대화를 종료한다.#k#l", ScriptMessageFlag.NpcReplacedByNpc);
                    } else {
                        v = self.askMenu("#fs11#제의 불길한 기운이 조금은 줄어든 것 같습니다.\r\n다시 지하 수로 입구로 안내해드리겠습니다.\r\n\r\n#e- 획득 점수: #n#b#e" + point + "점#n#k\r\n#e- 이번 주 최고 점수: #n#r#e" + weeklyRecord + "점#n#k\r\n#b#L1# 지하 수로 입구로 돌아간다.#k#l\r\n#b#L2# 대화를 종료한다.#k#l", ScriptMessageFlag.NpcReplacedByNpc);
                    }
                    switch (v) {
                        case 1:
                            if (point > weeklyRecord) {
                                getPlayer().updateOneInfo(100811, "weeklyRecord", String.valueOf(point));
                            }
                            registerTransferField(941000000);
                            break;
                    }
                } else {
                    int v = self.askMenu("#fs11#\r\n아르카누스의 힘이 전혀 줄어들지 않았군요. 다시 지하 수로 입구로 안내해드리겠습니다.\r\n\r\n#b#L1# 지하 수로 입구로 돌아간다.#k#l\r\n#b#L2# 대화를 종료한다.#k#l", ScriptMessageFlag.NpcReplacedByNpc);
                    switch (v) {
                        case 1:
                            registerTransferField(941000000);
                            break;
                    }
                }
                break;
            }
            default: {
                int weeklyRecord = getPlayer().getOneInfoQuestInteger(100811, "weeklyRecord");
                int v = self.askMenu("#fs11#\r\n유적지가 불길한 기운을 내뿜고 있습니다. 지금 지하 수로를 조사하시겠습니까?\r\n\r\n#e- 이번 주 최고 점수: #n#r#e" + weeklyRecord + "점#n#k\r\n#L1# #b샤레니안의 지하 수로에 입장한다.#k#l\r\n#L2# #b샤레니안의 지하 수로는 어떤 공간인가요?#k#l", ScriptMessageFlag.NpcReplacedByNpc);
                switch (v) {
                    case 1: {
                        if (1 == self.askAccept("지하 수로에 입장 시 지금 적용되어 있는\r\n#fs16##b#e모든 버프 효과가 해제#k#fs12##n됩니다.\r\n\r\n지금 도전하시겠습니까?")) {
                            CulvertEnter fieldSet = (CulvertEnter) fieldSet("CulvertEnter");
                            int enter = fieldSet.enter(target.getId(), 0);
                            if (enter == -1) self.say("#fs11#알 수 없는 이유로 입장할 수 없습니다. 잠시 후에 다시 시도해 주십시오.");
                            else if (enter == 1) self.sayOk("#fs11#<샤레니안의 지하수로>는 혼자서만 도전할 수 있습니다.\r\n파티를 해제하고 다시 찾아와 주길 바랍니다.");
                            else if (enter == 2) self.say("#fs11#레벨은 최소 " + fieldSet.minLv + " 이상이어야 합니다.");
                            else if (enter == 3) self.say("#fs11#현재 모든 인스턴스가 가득차 도전할 수 없습니다. 채널을 이동해주세요.");
                            else if (enter == -2) self.say("#fs11#일요일 오후 23시 ~ 월요일 오전 01시 사이에는 노블레스 포인트 정산시간으로 이용하실 수 없습니다.");
                        } else {
                            self.sayOk("#fs11#알겠습니다. 조사할 준비가 되시면 다시 시도해주세요.",   ScriptMessageFlag.NpcReplacedByNpc);
                        }
                        break;
                    }
                    case 2: { //샤레니안의 지하 수로는 어떤 공간인가요?
                        self.say("#fs11##r#e샤레니안의 지하 수로#k#n에 대해 궁금하신가요?");
                        self.say("#fs11#아주 먼 옛날... 샤레니안이라는 고대의 왕국이 있었습니다. 비록 끔찍한 사건으로 인해 왕국이 통째로 멸망하고 말았지만요.");
                        self.say("#fs11#샤레니안의 유적지를 탐험하려는 시도는 여러 번 있었지만 쉽지 않았고, 결국 그곳으로 향하는 길은 잊히고 말았습니다.\r\n\r\n#r#e얼마 전까지만 해도 말이지요...");
                        self.say("#fs11#전 이전부터 샤레니안의 유적지를 조사 중이었는데 사고로 인해 아무도 가지 않았던 곳에서 #b샤레니안의 지하 수로로 통하는 길을 발견했습니다.#k");
                        self.say("#fs11#매우 많은 입구들이 미로처럼 얽혀있지만 어느 길로 가도 불길한 기운이 잠들어 있는 제단이 있는 곳으로 갈 수 있었지요.");
                        self.say("#fs11#길드 본부는 지하 수로 조사를 결정했습니다. 지하 수로 입구에서 저에게 말을 걸어서 샤레니안의 지하 수로로 통하는 문을 열면, 불길한 기운의 제단으로 향하는 통로로 갈 수 있습니다.");
                        self.say("#fs11#조사는 혼자서만 진행할 수 있고 중간에 길드를 탈퇴하거나 추방되면 던전 포인트를 획득할 수 없습니다. 또한, 길드에서 탈퇴하거나 추방당하면 해당 길드의 총 포인트에서 차감되니 주의하시길 바랍니다.");
                        self.say("#fs11#조사는 제한 없이 진행할 수 있습니다. 단, 지하 수로의 불길한 기운으로 인해 입장 시 모든 버프 효과가 해제되므로 신중하게 결정하시길 바랍니다.");
                        self.say("#fs11#제단으로 향하는 통로에서는 잠시 동안 버프 스킬과 소비 아이템을 사용할 수 있으니 이후 올 시련에 대비하실 수 있습니다.\r\n단, 일정 시간 내 제단으로 입장하지 못하면 지하 수로에서 퇴장하게 되니 주의하십시오.");
                        self.say("#fs11#수로의 가장 깊은 곳에 자리한 불길한 기운의 제단에서 숨죽인 채 때를 기다리던 존재가 곧 깨어날 겁니다. 바로 \r\n\r\n                   #r샤레니안의 악마, 아르카누스#k\r\n\r\n입니다.");
                        self.say("#fs11#그는 불멸의 존재... 공격을 받을수록 점점 더 강한 존재가 되어 태어나니 조심해야 합니다. 가장 강력한 상태로 재탄생되면 아무리 강한 공격을 받더라도 그는 결코 쓰러지는 모습을 보이지 않을 것이니 주의를 기울이는 것이 좋습니다.\r\n또한 아르카누스의 공격에 의해 사망한다면 곧바로 제단 밖으로 이동되니 주의하시길 바랍니다.");
                        self.say("#fs11#지하 수로의 존재들에게 입힌 피해에 비례해 던전 포인트를 얻을 수 있고 월요일 0시마다 목표 달성에 따라 길드 본부에서 포상으로 노블레스 SP를 지급하니 힘내서 도전해보십시오. 다만 길드의 총 던전 포인트가 일정 점수 이상이 되어야 받을 수 있을 겁니다.");
                        self.say("#fs11#또, 포인트 순위에 따라 추가로 노블레스 SP를 얻을 수도 있고 지하 수로에서 퇴장하고 일정 시간이 지나야 던전 포인트가 정산되니 참고하십시오.\r\n#r각 길드원의 던전 포인트 합산에 일정 시간이 소요되어 랭킹에 모두 반영 되기까지 어느정도 시간이 필요하오니 포인트가 적게 기록되어도 당황하지 말고 잠시 후 다시 확인해 보십시오.");
                        self.say("#fs11##b#e1위 : 25개의 SP\r\n2위 : 23개의 SP\r\n3위 : 21개의 SP\r\n4위 ~ 10위 : 20개의 SP\r\n상위 10% : 19개의 SP\r\n상위 30% : 17개의 SP\r\n상위 60% : 15개의 SP\r\n상위 80% : 10개의 SP\r\n500포인트 이상 : 5개의 SP\r\n\r\n#r#e※ 단, 500포인트 이상 획득하지 못하면 순위 안에 들었더라도 보상을 받을 수 없습니다.#k#n");
                        self.say("#fs11#제가 함께 수로 속으로 갈 테니 길을 잃을 염려는 하지 않으셔도 됩니다. 어서 도전하시지요.");
                        break;
                    }
                }
                break;
            }
        }
    }

    @Script
    public void gb_reset() {
        int duration = 0;
        if(getPlayer().getCooldownLimit(80002282) != 0L){ // 봉인된 룬의 힘 해제 악용 방지
            duration = (int) getPlayer().getRemainCooltime(80002282);
        }
        getPlayer().cancelAllBuffs();
        getPlayer().send(CField.addPopupSay(2012041, 1300, "제단의 불길한 기운으로 인해\r\n#r모든 버프가 해제#k되었습니다.", ""));
        getPlayer().send(CField.addPopupSay(2012041, 1300, "#r샤레니안의 악마#k와의 결전을 위해 정비하세요!", ""));
        if(duration != 0){
            getPlayer().temporaryStatSet(80002282, duration, SecondaryStatFlag.RuneBlocked, 1);
        }
    }

    public void gb_out() {
        initNPC(MapleLifeFactory.getNPC(2012041));
        if (1 == self.askYesNo("#fs11#도전을 포기하고 지하 수로 입구로 돌아가시겠습니까?", ScriptMessageFlag.NpcReplacedByNpc)) {
            registerTransferField(941000001);
        }
    }


}
