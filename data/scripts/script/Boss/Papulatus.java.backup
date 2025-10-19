package script.Boss;

import objects.fields.MapleMapFactory;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.users.MapleCharacter;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

import java.util.Iterator;

public class Papulatus extends ScriptEngineNPC {

    /*
    public void Populatus00() {
        initNPC(MapleLifeFactory.getNPC(2041021));
        int menu = self.askMenu("#e<보스: 파풀라투스>#n\r\n사고뭉치 파풀라투스가 차원을 계속 부수는 것을 막아야 합니다. 도와주시겠어요?\r\n\r\n\r\n#L0# 이지 모드 ( 레벨 115 이상 )#l\r\n#L1# 노멀 모드 ( 레벨 155 이상 )#l\r\n#L2# 카오스 모드 ( 레벨 190 이상 )#l\r\n#L3# 카오스 연습 모드( 레벨 190 이상 )#l");
        if (!getPlayer().haveItem(4031179)) {
            if (target.exchange(4031179, 1) > 0) {
                self.say("#r#e파티원 모두#n#k #b#e차원 균열의 조각#k#n이 없으시군요. 파풀라투스를 만나기 위해서 꼭 필요합니다. 제가 마침 갖고 있는 것을 드리겠습니다.");
                self.say("#b#e차원 균열의 조각#k#n을 드렸으니, 파풀라투스가 차원을 부수는 것을 꼭 막아 주세요!");
            } else {
                self.say("기타 인벤토리 공간이 부족합니다. 기타 인벤토리 공간을 충분히 확보해주세요.");
                return;
            }
        }
        enter(menu);
    }
     */

    public void Populatus01() {
        if (self.askYesNo("삐리 삐리~ 저를 통해 안전한 곳으로 나가실 수 있습니다. 삐리 삐리~ 이대로 밖으로 나가시겠습니까?") == 1) {
            if (getPlayer().getEventInstance() != null) {
                getPlayer().getEventInstance().unregisterPlayer(getPlayer());
                getPlayer().setEventInstance(null);
                getPlayer().setRegisterTransferFieldTime(0);
                getPlayer().setRegisterTransferField(0);
                registerTransferField(220080000);
            }
        }
    }

    private void enter(int diff) {
        EventManager em = getEventManager("Papulatus");
        if (em == null) {
            self.say("지금은 파풀라투스 레이드를 이용하실 수 없습니다.");
            return;
        }
        if (target.getParty() == null) {
            self.say("1인 이상의 파티에 속해야만 입장할 수 있습니다.");
            return;
        }
        if (target.getParty().getLeader().getId() != target.getId()) {
            self.say("파티장을 통해 진행해 주십시오.");
            return;
        }
        if (!target.getParty().isPartySameMap()) {
            self.say("파티원이 전원 이곳에 모여있어야 합니다.");
            return;
        }
        int[] startMaps = new int[]{220080100, 220080200, 220080300, 220080300};
        String status = "EasyStatus";
        int deathCount = 50;
        int startMap = startMaps[diff];
        int minLev = 115;
        boolean countPass = true;
        boolean timePass = true;
        String key = "papulatus_c";
        Iterator it = getClient().getChannelServer().getPartyMembers(target.getParty()).iterator();
        switch (diff) {
            case 0:
            case 1:
                String q = getPlayer().getOneInfoQuest(1234569, "papulatus_clear");
                if (q != null && !q.isEmpty() && q.equals("1")) {
                    self.say("금일에 이미 격파하여 00시에 횟수 초기화 이후 다시 도전 가능합니다.");
                    return;
                }
                if (!getPlayer().CountCheck(key, 1)) {
                    self.say("하루에 1번만 시도 할 수 있습니다.");
                    return;
                }
                if (diff == 1) {
                    status = "NormalStatus";
                    deathCount = 5;
                    minLev = 155;
                }
                break;
            case 2:
                q = getPlayer().getOneInfoQuest(1234569, "chaos_papulatus_clear");
                if (q != null && !q.isEmpty() && q.equals("1")) {
                    self.say("금주에 이미 격파하여 목요일 00시에 횟수 초기화 이후 다시 도전 가능합니다.");
                    return;
                }
                status = "ChaosStatus";
                deathCount = 5;
                minLev = 190;
                break;
            case 3:
                initNPC(MapleLifeFactory.getNPC(9010000));
                if (0 == self.askYesNo("연습 모드에 입장을 선택하셨습니다. 연습 모드에서는 #b#e경험치와 보상을 얻을 수 없으며#k#n 보스 몬스터의 종류와 상관없이 #b#e하루 20회#k#n만 이용할 수 있습니다.\r\n\r\n연습 모드에서는 사망 후 부활할 때 버프 프리저를 사용해도 소모되지 않습니다. 단, #b#e버프 프리저가 1개 이상#k#n 있어야 사용할 수 있습니다.\r\n\r\n입장하시겠습니까?", ScriptMessageFlag.NpcReplacedByNpc)) {
                    return;
                }
                key = "boss_practice";
                if (!getPlayer().CountCheck(key, 20)) {
                    self.say("하루에 20번만 시도 가능합니다.", ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }
                status = "ChaosStatus";
                deathCount = 5;
                minLev = 190;
                break;
        }
        while (it.hasNext()) {
            MapleCharacter chr = (MapleCharacter) it.next();
            if (chr.getLevel() < minLev) {
                countPass = false;
                break;
            }
            if (diff != 2 && diff != 3) {
                String q = chr.getOneInfoQuest(1234569, "papulatus_clear");
                if (q != null && !q.isEmpty() && q.equals("1")) {
                    countPass = false;
                    break;
                }
                if (!chr.CountCheck("papulatus_c", 1)) {
                    countPass = false;
                    break;
                }
            }
            if (diff == 2) {
                if (!chr.canEnterBoss("papulatus_can_time")) {
                    timePass = false;
                    break;
                }
            }
        }
        if (!countPass) {
            self.sayOk("입장 제한횟수가 부족하거나 레벨 제한이 맞지 않는 파티원이 있어 입장할 수 없습니다.");
            return;
        }
        if (!timePass) {
            self.sayOk("입장 제한시간이 남은 파티원이 있어 입장할 수 없습니다.");
            return;
        } else {
            String canTimeKey = null;
            if (diff == 2) {
                canTimeKey = "papulatus_can_time";
            }
            setBossEnter(target.getParty(), ("파풀라투스 난이도 : " + diff), key, canTimeKey, 3);
        }
        if (em.getProperty(status).equals("1")) {
            self.sayOk("현재 모든 인스턴스가 가득차 이용하실 수 없습니다. 다른 채널을 이용해주세요.");
            return;
        }
        em.setProperty(status, "1");
        EventInstanceManager eim = em.readyInstance();
        eim.setProperty("mode", status.replace("Status", ""));
        eim.setProperty("map", startMap);
        eim.setProperty("deathCount", deathCount);
        eim.setProperty("practice", diff == 3 ? 1 : 0);
        MapleMapFactory mFactory = getClient().getChannelServer().getMapFactory();
        mFactory.getMap(startMap).resetFully(false);
        eim.registerParty(target.getParty(), getPlayer().getMap());
    }
}
