package script.Boss;

import constants.QuestExConstants;
import database.DBConfig;
import network.models.CWvsContext;
import objects.fields.Field;
import objects.fields.MapleMapFactory;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.newscripting.Script;
import scripting.newscripting.ScriptEngineNPC;

import java.awt.*;

public class Hillah extends ScriptEngineNPC {
	
    public void hillah_accept() {
        EventManager em = getEventManager("Hillah");
        if (em == null) {
            self.say("現在不能使用希拉·雷德。");
            return;
        }
        if (target.getParty() == null) {
            self.sayOk("1人以上組隊才能入場。");
            return;
        }
        if (target.getParty().getLeader().getId() != target.getId()) {
            self.sayOk("請通過派對現場進行。");
            return;
        }
        int v0 = self.askMenu("#e<老闆：希拉>#n\r\n準備好消滅希拉，實現阿斯旺的真正解放了嗎？ 如果有其他地區的隊員，請大家都聚過來。 \r\n#b\r\n#L0#<老闆：希拉>申請入場。 #l");
        if (v0 == 0) {
            String v1Menu = "\r\n#L1#硬模式（170級以上）#l\r\n#L2#硬模式（170級以上）#l"; //나중에 추가 될 하드모드와 연습모드
            int v1 = self.askMenu("#e<老闆：希拉>#n\r\n請選擇您想要的模式。 \r\n\r\n#L0#正常模式（等級120以上）#l");
            if (v1 != -1) {
                if (target.getParty().isPartySameMap()) {
                    if (v1 == 0) { //노말힐라
                        String overLap = checkEventNumber(getPlayer(), QuestExConstants.Hillah.getQuestID(), DBConfig.isGanglim);
                        if (overLap == null) {
                            boolean canEnter = false;
                            if (em.getProperty("status0").equals("0")) {
                                canEnter = true;
                            }
                            if (canEnter) {
                                em.setProperty("status0", "1");
                                EventInstanceManager eim = em.readyInstance();
                                eim.setProperty("map", 262030100); //복도1로
                                MapleMapFactory mFactory = getClient().getChannelServer().getMapFactory();
                                mFactory.getMap(262030100).setLastRespawnTime(0);
                                mFactory.getMap(262030100).resetFully(true);
                                mFactory.getMap(262030100).setLastRespawnTime(Long.MAX_VALUE);

                                mFactory.getMap(262030200).setLastRespawnTime(0);
                                mFactory.getMap(262030200).resetFully(true);
                                mFactory.getMap(262030200).setLastRespawnTime(Long.MAX_VALUE);

                                mFactory.getMap(262030300).resetFully(false);
                                mFactory.getMap(262030300).spawnMonster(MapleLifeFactory.getMonster(8870000), new Point(165, 196), 0);
                                updateEventNumber(getPlayer(), QuestExConstants.Hillah.getQuestID()); //힐라는
                                eim.registerParty(target.getParty(), getPlayer().getMap());
                            } else {
                                self.sayOk("當前所有地圖已滿，無法使用。 請使用其他頻道。");
                            }
                        } else {
                            self.sayOk("隊員中#b#e" + overLap + "#n#k今天入場了。 <老闆：希拉>普通模式每天只能挑戰一次。");
                        }
                    }
                } else {
                    self.sayOk(target.getParty().getPartyMemberList().size() + "名稱必須在同一映射中。");
                }
            }
        }
    }

    public void in_hillah() {
        initNPC(MapleLifeFactory.getNPC(2184001));
        EventManager em = getEventManager("Hillah");
        if (em == null) {
            self.say("現在不能使用希拉·雷德。");
            return;
        }
        if (target.getParty() == null) {
            self.sayOk("1人以上組隊才能入場。");
            return;
        }
        if (target.getParty().getLeader().getId() != target.getId()) {
            self.sayOk("請通過派對現場進行。");
            return;
        }
        int v0 = self.askMenu("#e<老闆：希拉>#n\r\n準備好消滅希拉，實現阿斯旺的真正解放了嗎？ 如果有其他地區的隊員，請大家都聚過來。 \r\n#b\r\n#L0#<老闆：希拉>申請入場。 #l");
        if (v0 == 0) {
            String v1Menu = "\r\n#L1#硬模式（170級以上）#l\r\n#L2#硬模式（170級以上）#l"; //나중에 추가 될 하드모드와 연습모드
            int v1 = self.askMenu("#e<老闆：希拉>#n\r\n請選擇您想要的模式。 \r\n\r\n#L0#正常模式（等級120以上）#l");
            if (v1 != -1) {
                if (target.getParty().isPartySameMap()) {
                    if (v1 == 0) { //노말힐라
                        String overLap = checkEventNumber(getPlayer(), QuestExConstants.Hillah.getQuestID());
                        if (overLap == null) {
                            boolean canEnter = false;
                            if (em.getProperty("status0").equals("0")) {
                                canEnter = true;
                            }
                            if (canEnter) {
                                em.setProperty("status0", "1");
                                EventInstanceManager eim = em.readyInstance();
                                eim.setProperty("map", 262030100); //복도1로
                                MapleMapFactory mFactory = getClient().getChannelServer().getMapFactory();
                                mFactory.getMap(262030100).setLastRespawnTime(0);
                                mFactory.getMap(262030100).resetFully(true);
                                mFactory.getMap(262030100).setLastRespawnTime(Long.MAX_VALUE);

                                mFactory.getMap(262030200).setLastRespawnTime(0);
                                mFactory.getMap(262030200).resetFully(true);
                                mFactory.getMap(262030200).setLastRespawnTime(Long.MAX_VALUE);

                                mFactory.getMap(262030300).resetFully(false);
                                mFactory.getMap(262030300).spawnMonster(MapleLifeFactory.getMonster(8870000), new Point(165, 196), 0);

                                updateEventNumber(getPlayer(), QuestExConstants.Hillah.getQuestID()); //힐라는
                                eim.registerParty(target.getParty(), getPlayer().getMap());
                            }
                        } else {
                            self.sayOk("隊員中#b#e" + overLap + "#n#k今天入場了。 <老闆：希拉>普通模式每天只能挑戰一次。");
                        }
                    }
                } else {
                    self.sayOk(target.getParty().getPartyMemberList().size() + "名稱必須在同一映射中。");
                }
            }
        }
    }

    @Script
    public void hillah_next() {
        EventInstanceManager eim = getEventInstance();
        if (eim == null) {
            getPlayer().dropMessage(5, "沒有事件實例。");
            return;
        }
        Field field = getPlayer().getMap();
        if (field.getAllMonster().size() == 0) {
            switch (field.getId()) {
                case 262030100:
                    if (eim.getProperty("stage1_bloodTooth") == null) {
                        eim.setProperty("stage1_bloodTooth", "1");
                        field.broadcastMessage(CWvsContext.getScriptProgressMessage("血圖斯察覺到了我們的入侵！！！ 請打敗血圖斯。"));
                        field.spawnMonster(MapleLifeFactory.getMonster(8870003), new Point(777, 196), 43);
                        field.spawnMonster(MapleLifeFactory.getMonster(8870003), new Point(777, 196), 43);
                        field.spawnMonster(MapleLifeFactory.getMonster(8870003), new Point(777, 196), 43);
                        field.spawnMonster(MapleLifeFactory.getMonster(8870003), new Point(777, 196), 43);
                    } else {
                        if (field.getAllMonster().size() == 0) {
                            registerTransferField(field.getId() + 100);
                        }
                    }
                    break;
                case 262030200:
                    if (eim.getProperty("stage2_bloodTooth") == null) {
                        eim.setProperty("stage2_bloodTooth", "1");
                        field.broadcastMessage(CWvsContext.getScriptProgressMessage("血圖斯察覺到了我們的入侵！！！ 請打敗血圖斯。"));
                        field.spawnMonster(MapleLifeFactory.getMonster(8870003), new Point(777, 196), 43);
                        field.spawnMonster(MapleLifeFactory.getMonster(8870003), new Point(777, 196), 43);
                        field.spawnMonster(MapleLifeFactory.getMonster(8870003), new Point(777, 196), 43);
                        field.spawnMonster(MapleLifeFactory.getMonster(8870003), new Point(777, 196), 43);
                    } else {
                        if (field.getAllMonster().size() == 0) {
                            registerTransferField(field.getId() + 100);
                        }
                    }
                    break;
            }
        } else {
            getPlayer().dropMessage(5, "現在還不能使用掠奪。");
        }
    }

    public void out_hillah() {
        initNPC(MapleLifeFactory.getNPC(2184001));
        if (1 == self.askYesNo("就這樣放弃嗎？")) {
            self.say("沒辦法。 謝謝你幫我到這裡。");
            getPlayer().setRegisterTransferFieldTime(0);
            getPlayer().setRegisterTransferField(0);
            target.registerTransferField(262030000);
            if (getPlayer().getEventInstance() != null) {
                getPlayer().getEventInstance().unregisterPlayer(getPlayer());
                getPlayer().setEventInstance(null);
            }
        }
    }

    @Script
    public void UIOpen() {
        registerTransferField(262030000);
    }
}
