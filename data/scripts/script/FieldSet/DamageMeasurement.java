package script.FieldSet;

import java.util.Date;
import java.text.SimpleDateFormat;

import database.DBConfig;
import objects.fields.child.etc.DamageMeasurementRank;
import objects.fields.child.etc.Field_DamageMeasurement;
import objects.fields.fieldset.childs.MulungForestEnter;
import objects.fields.fieldset.childs.NormalDemianEnter;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.users.stats.SecondaryStatFlag;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DamageMeasurement extends ScriptEngineNPC {


    public void Npc_9062025() {
        if (getPlayer().getMapId() != 993026900) {
            self.sayOk("조금만 더 힘을 내세요!!");
            return;
        }
        int v = 0;
        /*if(!getPlayer().checkCharacterUse(1, 300, 7)) {
            return;
        }*/
        if (DBConfig.isGanglim) {
            v = self.askMenu("#fs11#전투력 측정을 담당하고 있는 #bMr.보체#k 입니다.\r\n#b#h ##k 님의 최고 전투력은 : #r" + DamageMeasurementRank.getUnit(DamageMeasurementRank.getDamage(getPlayer().getId())) + "#k 입니다.\r\n전투력은 원하시는 메뉴를 선택해보시겠어요?\r\n#L0#전투력 측정 설명을 듣는다.\r\n#L2#전투력 측정 보상 설명을 듣는다.\r\n\r\n#r#L1#전투력을 측정할게요.");
        } else {
            v = self.askMenu("전투력 측정을 담당하고 있는 #bMr.보체#k 입니다.\r\n#b#h ##k 님의 최고 전투력은 : #fs11##r" + DamageMeasurementRank.getUnit(DamageMeasurementRank.getDamage(getPlayer().getId())) + "#k#fs12# 입니다.\r\n전투력은 원하시는 메뉴를 선택해보시겠어요?\r\n#L0#전투력 측정 설명을 듣는다.\r\n#r#L1#전투력을 측정할게요.");
        }
        switch (v) {
            case 0: {
                if (DBConfig.isGanglim) {
                    self.sayOk("#fs11#전투력 측정은 2분간 진행되며 최고 데미지를 달성하면 자동으로 랭킹에 반영됩니다.\r\n전투력 측정을 도와줄 허수아비는 보스 몬스터로 취급되어 보스 추가 데미지 옵션도 적용됩니다.\r\n전투력 랭킹은 편의시스템 → 유저랭킹에서 확인해주세요.");
                } else {
                    self.sayOk("전투력 측정은 1분간 진행되며 최고 데미지를 달성하면 자동으로 랭킹에 반영됩니다.\r\n전투력 측정을 도와줄 허수아비는 보스 몬스터로 취급되어 보스 추가 데미지 옵션도 적용됩니다.\r\n전투력 랭킹은 편의시스템 → 유저랭킹에서 확인해주세요.");
                }
                break;
            }
            case 1: {
                Date date = new Date();
                if ((date.getDay() == 0 && date.getHours() == 23 && date.getMinutes() >= 50) || (date.getDay() == 1 && date.getHours() == 0 && date.getMinutes() <= 10)) {
                    self.sayOk("일요일 23시 50분 ~ 월요일 00시 10분까지는 랭킹 집계시간이므로 도전할 수 없습니다.");
                    return;
                }
                if (getClient().getChannelServer().getMapFactory().getMap(993026800).getCharacters().size() > 0) {
                    self.sayOk("이미 누군가가 도전중입니다.\r\n#b다른 채널을 이용해 주세요.#k");
                    return;
                }

                int vv = 0;
                if (DBConfig.isGanglim) {
                    vv = self.askMenu("#fs11#전투력을 측정할 허수아비 위치를 선택해주세요.\r\n\r\n#e#r※ 지난주 측정기록이 있을 시 이번주는 랭킹에 기록되지 않습니다. 추가로 계정 내 다른 캐릭터의 기록은 삭제됩니다.#b#n\r\n#b #L0#좌측#l\r\n #L1#중앙#l\r\n #L2#우측#l");
                } else {
                    vv = self.askMenu("전투력을 측정할 허수아비 위치를 선택해주세요.\r\n\r\n#e#r※ 지난주 측정기록이 있을 시 이번주는 랭킹에 기록되지 않습니다. 추가로 계정 내 다른 캐릭터의 기록은 삭제됩니다.#b#n\r\n#b #L0#좌측#l\r\n #L1#중앙#l\r\n #L2#우측#l");
                }
                if (getClient().getChannelServer().getMapFactory().getMap(993026800).getCharacters().size() > 0) {
                    self.sayOk("이미 누군가가 도전중입니다.\r\n#b다른 채널을 이용해 주세요.#k");
                    return;
                }
                if (1 == self.askYesNo("#fs11#전투력 측정을 공정하게 진행하기 위해\r\n\r\n#fs16#입장 시 #r#e모든 버프가 해제#n#k됩니다.#n#fs11#\r\n\r\n진행하시겠습니까?")) {
                    Field_DamageMeasurement map = (Field_DamageMeasurement) getClient().getChannelServer().getMapFactory().getMap(993026800);
                    map.spawnPoint = vv;
                    if (getClient().getChannelServer().getMapFactory().getMap(993026800).getCharacters().size() > 0) {
                        self.sayOk("이미 누군가가 도전중입니다.\r\n#b다른 채널을 이용해 주세요.#k");
                        return;
                    } else {
                        int duration = 0;
                        if(getPlayer().getCooldownLimit(80002282) != 0L){ // 봉인된 룬의 힘 해제 악용 방지
                            duration = (int) getPlayer().getRemainCooltime(80002282);
                        }
                        getPlayer().cancelAllBuffs();
                        target.registerTransferField(993026800);
                        if(duration != 0){
                            getPlayer().temporaryStatSet(80002282, duration, SecondaryStatFlag.RuneBlocked, 1);
                        }
                    }
                }
                break;
            }
            case 2: {
                if (DBConfig.isGanglim) {
                    self.sayOk("#fs11#매주 월요일 자정에 랭킹이 초기화되며 초기화이전의 랭킹 기준으로 보상이 지급됩니다\r\n\r\n#r랭킹 1~3위#k : 올스탯 1000, 공격력/마력 500\r\n#r랭킹 상위 30%#k : 올스탯 500, 공격력/마력 200\r\n#r랭킹 상위 70%#k : 올스탯 300, 공격력/마력 100\r\n#r그 외#k : 올스탯 150, 공격력/마력 50");
                }
                break;
            }
        }
    }

    public void DamageRanking() {
        initNPC(MapleLifeFactory.getNPC(9076004));
        String menu = "원하시는 메뉴를 선택해주세요.\r\n#b#L0#모든 랭킹보기#l\r\n";
        menu += "#L1#보상 랭킹보기#l";
        int v = self.askMenu(menu);
        switch (v) {
            case 0: //모든랭킹보기
                self.sayOk(DamageMeasurementRank.getRanks(50));
                break;
            case 1: //보상랭킹보기
                self.sayOk(DamageMeasurementRank.getRewardRanks(50));
                break;
        }
    }

    public void mulung_forest() {
        //100936 count, date
        initNPC(MapleLifeFactory.getNPC(2091011));
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
        Date lastTime = null;
        Date now = null;
        try {
            lastTime = sdf.parse(getPlayer().getOneInfo(100936, "date"));
        } catch (Exception e) {
            lastTime = null;
        }
        try {
            now = sdf.parse(sdf.format(new Date()));
        } catch (Exception e) {
            lastTime = null;
        }
        if ((lastTime != null && !lastTime.equals(now)) || lastTime == null) {
            getPlayer().updateOneInfo(100936, "count", "0");
        }
        int remainCount = 5 - getPlayer().getOneInfoQuestInteger(100936, "count");

        int menu = self.askMenu(String.format("무릉 깊숙한 곳엔 우리 선인들만의 비밀 수련장이 있지.\r\n\r\n (오늘 남은 입장 횟수 : #r#e%d회#k#n)\r\n#b#L0# 안개 숲 수련장에 입장하고 싶어.#l\r\n#L1# 안개 숲 수련장에 대해 알고 싶어.#l\r\n", remainCount));
        switch (menu) {
            case 0: { //입장
                MulungForestEnter fieldSet = (MulungForestEnter) fieldSet("MulungForestEnter");
                if (fieldSet == null) {
                    self.sayOk("지금은 안개숲 수련장을 이용할 수 없어!");
                    return;
                }
                int enter = fieldSet.enter(target.getId(), 0);
                if (enter == -1) self.say("일일 도전횟수가 부족한 파티원이 존재합니다.");
                else if (enter == 1) self.say("파티를 맺어야만 도전할 수 있습니다.");
                else if (enter == 2) self.say("파티장을 통해 진행해 주십시오.");
                else if (enter == 3) self.say( "최소 " + fieldSet.minMember + "인 이상의 파티가 퀘스트를 시작할 수 있습니다.");
                else if (enter == 4) self.say( "파티원의 레벨은 최소 " + fieldSet.minLv + " 이상이어야 합니다.");
                else if (enter == 5) self.say("파티원이 모두 모여 있어야 시작할 수 있습니다.");
                else if (enter == 6) self.say( "이미 다른 파티가 안으로 들어가 퀘스트 클리어에 도전하고 있는 중입니다.");

                break;
            }
            case 1: { //설명듣기
                self.say("\r\n#b#e<안개 숲 수련장>#n#k은 자신의 한계를 넘어서고 싶은 \r\n#b선인들만의 비밀 수련장#k이지.\r\n\r\n 우리 선인들만 쓰는 비밀 공간인데 #r#e특별히#k#n 공개할 테니 \r\n감사하라고.", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("\r\n#b#e<안개 숲 수련장>#n#k에서는 원하는 전투 상황을 만들 수 있어.\r\n 체력, 방어율 등의 #b능력치를 조절#k해서 #b선인 바위 몬스터#k를 \r\n소환할 수 있어.\r\n\r\n 필드의 #b포스 종류를 변경#k하고 #b포스 수치#k를 원하는 대로 \r\n바꿀 수도 있지.", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("\r\n단, 몬스터 설정과 필드 설정 권한은 #r#e파티장에게만#k#n 있어.", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("\r\n 모든 파티원들이 원할 때마다 #bHP와 MP를 모두 최대치로 \r\n1회 회복#k할 수도 있어.\r\n\r\n #b#e안개 숲 수련장이니까#n#k 가능한 거라고?", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("\r\n단, 특별한 공간인 만큼 아무나 들여보낼 수는 없지.\r\n#r#e200 레벨#k#n과 #r#e무릉 30층#k#n에 도달해야 입장할 수 있어.", ScriptMessageFlag.NpcReplacedByNpc);
                self.say("\r\n수련장에는 #r#e파티 상태로만#k#n 입장이 가능해.\r\n그리고 #r#e60분만#k#n 이용할 수 있으니까 이 점 유의하도록.", ScriptMessageFlag.NpcReplacedByNpc);
                break;
            }
        }
    }

    public void Training_exit() {
        initNPC(MapleLifeFactory.getNPC(2091011));
        if (self.askYesNo("수련을 그만두고 나갈꺼야?") == 1) {
            if (getPlayer().getEventInstance() != null) {
                getPlayer().getEventInstance().unregisterPlayer(getPlayer());
            }
            registerTransferField(925020001);
        }
    }
}
