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
        int menu = self.askMenu("#e<老闆：帕普拉圖斯>#n\r\n要防止惹禍精帕普拉圖斯繼續破壞維度。 能幫個忙嗎？ \r\n\r\n\r\n#L0#异地模式（115級以上）#l\r\n#L1#正常模式（155級以上）#l\r\n#L2#混沌模式（190級以上）#l\r\n#L3#混沌練習模式（190級以上）#l");
        if (!getPlayer().haveItem(4031179)) {
            if (target.exchange(4031179, 1) > 0) {
                self.say("#r#e隊員們都沒有#n#k#b#e次元裂痕的碎片#k#n。 為了見到帕普拉圖斯，非常需要。 我正好把我有的給你。");
                self.say("#b#e給了你次元裂痕的碎片#k#n，請一定要封锁帕普拉圖斯破壞次元！");
            } else {
                self.say("其他庫存空間不足。 請確保足够的其他庫存空間。");
                return;
            }
        }
        enter(menu);
    }
     */

    public void Populatus01() {
        if (self.askYesNo("嗶哩嗶哩~可以通過我去安全的地方。 嗶哩嗶哩~就這樣出去嗎？") == 1) {
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
            self.say("現在不能使用帕普拉圖斯RAID。");
            return;
        }
        if (target.getParty() == null) {
            self.say("必須屬於1人以上的隊伍才能入場。");
            return;
        }
        if (target.getParty().getLeader().getId() != target.getId()) {
            self.say("請通過派對現場進行。");
            return;
        }
        if (!target.getParty().isPartySameMap()) {
            self.say("全體隊員都應該聚集在這裡。");
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
                    self.say("今日已擊敗，00點初始化次數後可再次挑戰。");
                    return;
                }
                if (!getPlayer().CountCheck(key, 1)) {
                    self.say("一天只能嘗試一次。");
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
                    self.say("本周已擊敗，週四00點初始化次數後可再次挑戰。");
                    return;
                }
                status = "ChaosStatus";
                deathCount = 5;
                minLev = 190;
                break;
            case 3:
                initNPC(MapleLifeFactory.getNPC(9010000));
                if (0 == self.askYesNo("您已選擇進入練習模式。 在練習模式下無法獲得#b#e經驗和獎勵，無論#k#n BOSS怪物的種類如何，每天只能使用#b#e 20次#k#n。 \r\n\r\n在練習模式下，死亡後復活時使用Buff等化器也不會消耗。 但是，必須至少有一個#b#e Buff等化器#k#n才能使用。 \r\n\r\n是否要入場？", ScriptMessageFlag.NpcReplacedByNpc)) {
                    return;
                }
                key = "boss_practice";
                if (!getPlayer().CountCheck(key, 20)) {
                    self.say("每天只能嘗試20次。", ScriptMessageFlag.NpcReplacedByNpc);
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
            self.sayOk("入場限制次數不足或有等級限制不符的隊員無法入場。");
            return;
        }
        if (!timePass) {
            self.sayOk("因為還有限制入場時間的隊員，所以無法入場。");
            return;
        } else {
            String canTimeKey = null;
            if (diff == 2) {
                canTimeKey = "papulatus_can_time";
            }
            setBossEnter(target.getParty(), ("帕普拉圖斯難度：" + diff), key, canTimeKey, 3);
        }
        if (em.getProperty(status).equals("1")) {
            self.sayOk("當前所有實例都已滿，無法使用。 請使用其他頻道。");
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
