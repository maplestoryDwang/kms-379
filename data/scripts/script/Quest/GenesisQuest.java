package script.Quest;

import constants.GameConstants;
import network.SendPacketOpcode;
import network.center.Center;
import network.encode.PacketEncoder;
import network.models.CWvsContext;
import objects.context.GoldenChariot;
import objects.item.*;
import objects.quest.MapleQuest;
import objects.users.enchant.*;
import objects.utils.HexTool;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GenesisQuest extends ScriptEngineNPC {
    public void q2000019s() {
        if (self.askAccept("#e#r[진:眞]#k#n 제네시스 무기 해방 #bChapter.2#k\r\n\r\n#e<사자왕 반 레온의 흔적>#n\r\n다음의 조건으로 하드 반 레온 1인 격파\r\n#b  - 봉인된 제네시스 무기와 보조무기만 장착\r\n  - 착용중인 장비의 순수 능력치만 적용\r\n  - 최종 데미지 90% 감소\r\n\r\n#k도전할까?", ScriptMessageFlag.Self) == 1) {
            self.say("하드 반 레온을 1인 격파해보러 가볼까!", ScriptMessageFlag.Self);
            getQuest().forceStart(getPlayer(), getNpc().getId(), "");
        }
    }

    public void q2000019e() {
        self.say("반 레온을 1인 격파하였다. 하지만 아직 #b제네시스 무기#k를 해방하기엔 어두운 힘에 잠겨있는 것 같다.", ScriptMessageFlag.Self);
        getQuest().forceComplete(getPlayer(), getNpc().getId());
    }

    public void q2000020s() {
        if (self.askAccept("#e#r[진:眞]#k#n 제네시스 무기 해방 #bChapter.3#k\r\n\r\n#e<시간의 대신관 아카이럼의 흔적>#n\r\n다음의 조건으로 노멀 아카이럼 1인 격파\r\n#b  - 봉인된 제네시스 무기와 보조무기만 장착\r\n  - 착용중인 장비의 순수 능력치만 적용\r\n  - 최종 데미지 75% 감소\r\n\r\n#k도전할까?", ScriptMessageFlag.Self) == 1) {
            self.say("노멀 아카이럼을 1인 격파해보러 가볼까!", ScriptMessageFlag.Self);
            getQuest().forceStart(getPlayer(), getNpc().getId(), "");
        }
    }

    public void q2000020e() {
        self.say("아카이럼을 1인 격파하였다. 하지만 아직 #b제네시스 무기#k를 해방하기엔 어두운 힘에 잠겨있는 것 같다.", ScriptMessageFlag.Self);
        getQuest().forceComplete(getPlayer(), getNpc().getId());
    }

    public void q2000021s() {
        if (self.askAccept("#e#r[진:眞]#k#n 제네시스 무기 해방 #bChapter.4#k\r\n\r\n#e<폭군 매그너스의 흔적>#n\r\n다음의 조건으로 하드 매그너스 1인 격파\r\n#b  - 봉인된 제네시스 무기와 보조무기만 장착\r\n  - 착용중인 장비의 순수 능력치만 적용\r\n  - 최종 데미지 50% 감소\r\n\r\n#b#i4036460##z4036460# 1개#k 필요\r\n  #b- 해당 퀘스트를 수락 후 검은 마법사를 처치하여 얻을 수 있음#k\r\n\r\n#k도전할까?", ScriptMessageFlag.Self) == 1) {
            self.say("하드 매그너스 1인 격파해보러 가볼까!", ScriptMessageFlag.Self);
            getQuest().forceStart(getPlayer(), getNpc().getId(), "");
        }
    }

    public void q2000021e() {
        if (!getPlayer().haveItem(4036460, 1)) {
            self.say("#b#i4036460# #z4036460# 1개#k가 필요하다. 검은 마법사를 처치하여 획득할 수 있다.", ScriptMessageFlag.Self);
            return;
        }
        if (target.exchange(4036460, -1) > 0) {
            self.say("매그너스를 1인 격파하였다. 하지만 아직 #b제네시스 무기#k를 해방하기엔 어두운 힘에 잠겨있는 것 같다.", ScriptMessageFlag.Self);
            getQuest().forceComplete(getPlayer(), getNpc().getId());
        }
    }

    public void q2000022s() {
        String v0 = "#e#r[진:眞]#k#n 제네시스 무기 해방 #bChapter.5#k\r\n\r\n#e<윙 마스터 스우의 흔적>#n\r\n다음의 조건으로 스우 1인 격파\r\n#b  - 최종 데미지 20% 감소\r\n\r\n#b#i4036461##z4036461# 1개#k 필요\r\n  #b- 해당 퀘스트를 수락 후 검은 마법사를 처치하여 얻을 수 있음\r\n\r\n클리어 시 [파괴의 얄다바오트] 스킬 획득\r\n제네시스 무기 첫 번째 힘 개방\r\n\r\n#k도전할까?";
        if (GameConstants.isZero(getPlayer().getJob())) {
            v0 = "#e#r[진:眞]#k#n 제네시스 무기 해방 #bChapter.5#k\r\n\r\n#e<윙 마스터 스우의 흔적>#n\r\n다음의 조건으로 스우 1인 격파\r\n#b  - 최종 데미지 20% 감소\r\n\r\n#b#i4036461##z4036461# 1개#k 필요\r\n  #b- 해당 퀘스트를 수락 후 검은 마법사를 처치하여 얻을 수 있음\r\n\r\n#k도전할까?";
        }
        if (self.askAccept(v0, ScriptMessageFlag.Self) == 1) {
            self.say("하드 스우 1인 격파해보러 가볼까!", ScriptMessageFlag.Self);
            getQuest().forceStart(getPlayer(), getNpc().getId(), "");
        }
    }

    public void q2000022e() {
        if (!getPlayer().haveItem(4036461, 1)) {
            self.say("#b#i4036461# #z4036461# 1개#k가 필요하다. 검은 마법사를 처치하여 획득할 수 있다.", ScriptMessageFlag.Self);
            return;
        }
        if (target.exchange(4036461, -1) > 0) {
            if (!GameConstants.isZero(getPlayer().getJob())) {
                int result = doGenesisWeaponFirstUpgrade();
                if (result == -1) {
                    return;
                }
                self.say("#b제네시스 무기#k에 잠재된 첫 번째 힘이 깨어났다.\r\n\r\n#r- <파괴의 얄다바오트> 스킬 획득\r\n- 주문서/스타포스 강화 불가\r\n- 추가옵션/소울은 완전 해방 시 초기화\r\n\r\n#k#i" + result + "# #z" + result + "#", ScriptMessageFlag.Self);
            }
            getQuest().forceComplete(getPlayer(), getNpc().getId());
        }
    }

    public void q2000023s() {
        if (self.askAccept("#e#r[진:眞]#k#n 제네시스 무기 해방 #bChapter.6#k\r\n\r\n#e<파멸의 검 데미안의 흔적>#n\r\n다음의 조건으로 하드 데미안 1인 격파\r\n#b  - 데스카운트 5개로 감소\r\n\r\n#b#i4036462##z4036462# 1개#k 필요\r\n  #b- 해당 퀘스트를 수락 후 검은 마법사를 처치하여 얻을 수 있음#k\r\n\r\n#k도전해볼까?", ScriptMessageFlag.Self) == 1) {
            self.say("하드 데미안 1인 격파해보러 가볼까!", ScriptMessageFlag.Self);
            getQuest().forceStart(getPlayer(), getNpc().getId(), "");
        }
    }

    public void q2000023e() {
        if (!getPlayer().haveItem(4036462, 1)) {
            self.say("#b#i4036462# #z4036462# 1개#k가 필요하다. 검은 마법사를 처치하여 획득할 수 있다.", ScriptMessageFlag.Self);
            return;
        }
        if (target.exchange(4036462, -1) > 0) {
            self.say("데미안을 1인 격파하였다. 하지만 아직 #b제네시스 무기#k를 해방하기엔 어두운 힘에 잠겨있는 것 같다.", ScriptMessageFlag.Self);
            getQuest().forceComplete(getPlayer(), getNpc().getId());
        }
    }

    public void q2000024s() {
        if (self.askAccept("#e#r[진:眞]#k#n 제네시스 무기 해방 #bChapter.7#k\r\n\r\n#e<거미의 왕 윌의 흔적>#n\r\n다음의 조건으로 하드 윌 1인 격파\r\n#b  - 혼자서 격파\r\n\r\n#b#i4036463##z4036463# 1개#k 필요\r\n  #b- 해당 퀘스트를 수락 후 검은 마법사를 처치하여 얻을 수 있음#k\r\n\r\n#k도전해볼까?", ScriptMessageFlag.Self) == 1) {
            self.say("하드 윌 1인 격파해보러 가볼까!", ScriptMessageFlag.Self);
            getQuest().forceStart(getPlayer(), getNpc().getId(), "");
        }
    }

    public void q2000024e() {
        if (!getPlayer().haveItem(4036463, 1)) {
            self.say("#b#i4036463# #z4036463# 1개#k가 필요하다. 검은 마법사를 처치하여 획득할 수 있다.", ScriptMessageFlag.Self);
            return;
        }
        if (target.exchange(4036463, -1) > 0) {
            self.say("윌을 1인 격파하였다. 하지만 아직 #b제네시스 무기#k를 해방하기엔 어두운 힘에 잠겨있는 것 같다.", ScriptMessageFlag.Self);
            getQuest().forceComplete(getPlayer(), getNpc().getId());
        }
    }

    public void q2000025s() {
        if (self.askAccept("#e#r[진:眞]#k#n 제네시스 무기 해방 #bChapter.8#k\r\n\r\n#e<악몽의 주인 루시드의 흔적>#n\r\n다음의 조건으로 루시드 1인 격파\r\n#b  - #i2000047# #z2000047# 50개 지급\r\n  - #z2000047#를 제외한 #e모든 소비 아이템 사용 불가#n\r\n\r\n#b#i4036464##z4036464# 1개#k 필요\r\n  #b- 해당 퀘스트를 수락 후 검은 마법사를 처치하여 얻을 수 있음#k\r\n\r\n#k도전해볼까?", ScriptMessageFlag.Self) == 1) {
            self.say("하드 루시드 1인 격파해보러 가볼까!", ScriptMessageFlag.Self);
            getQuest().forceStart(getPlayer(), getNpc().getId(), "");
        }
    }

    public void q2000025e() {
        if (!getPlayer().haveItem(4036464, 1)) {
            self.say("#b#i4036464# #z4036464# 1개#k가 필요하다. 검은 마법사를 처치하여 획득할 수 있다.", ScriptMessageFlag.Self);
            return;
        }
        if (target.exchange(4036464, -1) > 0) {
            self.say("루시드를 1인 격파하였다. 곧 어두운 힘에서 해방할 수 있을 것 같다.", ScriptMessageFlag.Self);
            getQuest().forceComplete(getPlayer(), getNpc().getId());
        }
    }

    public void q2000026s() {
        if (self.askAccept("#e#r[진:眞]#k#n 제네시스 무기 해방 #bChapter.9#k\r\n\r\n#e<붉은 마녀 진 힐라의 흔적>#n\r\n다음의 조건으로 진 힐라 1인 격파\r\n#b  - 진 힐라의 HP 25% 감소#n\r\n\r\n#b#i4036465##z4036465# 1개#k 필요\r\n  #b- 검은 마법사를 처치하여 얻을 수 있음\r\n\r\n창조의 아이온 스킬 획득 가능#k\r\n\r\n#k도전해볼까?", ScriptMessageFlag.Self) == 1) {
            self.say("하드 진 힐라 1인 격파해보러 가볼까!", ScriptMessageFlag.Self);
            getQuest().forceStart(getPlayer(), getNpc().getId(), "");
        }
    }

    public void q2000026e() {
        if (!getPlayer().haveItem(4036465, 1)) {
            self.say("#b#i4036465# #z4036465# 1개#k가 필요하다. 검은 마법사를 처치하여 획득할 수 있다.", ScriptMessageFlag.Self);
            return;
        }
        if (target.exchange(4036465, -1) > 0) {
            self.say("진 힐라를 1인 격파하였다. 드디어 어두운 힘에서 완전히 해방할 수 있을 것 같다.\r\n\r\n#e<스킬 사용 가능>#n\r\n#b - 창조의 아이온 스킬 사용 가능 (제네시스 무기 장착중이라면 다시 착용해주시기 바랍니다.)", ScriptMessageFlag.Self);
            getQuest().forceComplete(getPlayer(), getNpc().getId());
        }
    }

    public void q2000027s() {
        final AtomicInteger weapon = new AtomicInteger(0);
        getPlayer().getInventory(MapleInventoryType.EQUIPPED).list().stream().forEach(item -> {
            for (int i : bmWeapons) {
                if (item.getItemId() == i + 1) {
                    weapon.set(i);
                    break;
                }
            }
        });
        if (weapon.get() == 0) {
            getPlayer().getInventory(MapleInventoryType.EQUIP).list().stream().forEach(item -> {
                for (int i : bmWeapons) {
                    if (item.getItemId() == i + 1) {
                        weapon.set(i);
                        break;
                    }
                }
            });
        }
        if (weapon.get() == 0) {
            if (GameConstants.isZero(getPlayer().getJob())) {
                if (target.exchange(4310260, 1) > 0) {
                    self.say("#b#i4310260# #z4310260##k을 획득했다. #e무기 성장#n을 통해 10형으로 성장할 수 있다.", ScriptMessageFlag.Self);
                    getQuest().forceComplete(getPlayer(), getNpc().getId());
                } else {
                    self.say("#b기타 인벤토리#k 슬롯을 1칸 확보하고 다시 시도하자.", ScriptMessageFlag.Self);
                }
                return;
            }
            self.say("제네시스 무기가 없으면 해당 퀘스트를 진행할 수 없다.", ScriptMessageFlag.Self);
            return;
        }
        if (self.askMenu("#e#r[진:眞]#k#n 제네시스 무기 해방 #bChapter.X#k\r\n\r\n#e<제네시스 무기>#n\r\n제네시스 무기가 강력한 힘으로 가득 찼다.\r\n제네시스 무기에 잠재된 힘을 완전히 깨울 수 있을 것 같은데, 해방을 시작해 볼까?\r\n\r\n#r- 15% 주문서로 모든 강화 완료\r\n- 스타포스 22성\r\n- 유니크 잠재능력 보유\r\n- 에픽 에디셔널 잠재능력 보유\r\n- 주문서/스타포스 강화 불가\r\n- 추가옵션/소울은 완전 해방 시 초기화\r\n#b#L0##i" + (weapon.get() + 1) + "# #z" + (weapon.get() + 1) +"##l", ScriptMessageFlag.Self) == 0) {
            doGenesisWeaponUpgrade();
            self.say("힘이 완전히 깨어나 제네시스 무기가 더 강력해졌다.\r\n무기를 착용하고 그 힘을 시험해보자.", ScriptMessageFlag.Self);

            getQuest().forceComplete(getPlayer(), getNpc().getId());
        }
    }


    int[] bmWeapons = new int[]{
            1212128, 1213021, 1222121, 1232121, 1242138, 1242140, 1262050, 1272039, 1282039, 1292021, 1302354, 1312212, 1322263, 1332288, 1362148, 1372236, 1382273, 1402267, 1412188, 1422196, 1432226, 1442284, 1452265, 1462251, 1472274, 1482231, 1492244, 1522151, 1532156, 1582043, 1592021, 1562010, 1214021
    };
    // 봉인된 제네시스 무기 1차 해방
    public int doGenesisWeaponFirstUpgrade() {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() < 0) {
            self.say("장비 창을 1칸 이상 비우자.", ScriptMessageFlag.Self);
            return -1;
        }

        Equip equip = null;
        for (Item item : new ArrayList<>(getPlayer().getInventory(MapleInventoryType.EQUIPPED).list())) {
            for (int i : bmWeapons) {
                if (item.getItemId() == i) {
                    equip = (Equip) item;
                    break;
                }
            }
        }
        if (equip == null) {
            for (Item item : new ArrayList<>(getPlayer().getInventory(MapleInventoryType.EQUIP).list())) {
                for (int i : bmWeapons) {
                    if (item.getItemId() == i) {
                        equip = (Equip) item;
                        break;
                    }
                }
            }
        }
        if (equip == null) {
            self.say("알 수 없는 오류가 발생했다.", ScriptMessageFlag.Self);
            return -1;
        }
        int weaponID = equip.getItemId() + 1;
        Equip genesis = (Equip) ii.getEquipById(weaponID);

        if (genesis == null) {
            self.say("알 수 없는 오류가 발생했다.", ScriptMessageFlag.Self);
            return -1;
        }

        // 추옵 부여
        if (BonusStat.resetBonusStat(genesis, BonusStatPlaceType.LevelledRebirthFlame)) {
        }

        MapleInventoryType type = MapleInventoryType.EQUIP;
        if (equip.getPosition() < 0) {
            type = MapleInventoryType.EQUIPPED;
        }
        getPlayer().send(CWvsContext.InventoryPacket.deleteItem(equip));

        MapleInventoryManipulator.removeFromSlot(getClient(), type, equip.getPosition(), equip.getQuantity(), false, false);
        MapleInventoryManipulator.addbyItem(getClient(), genesis);
        return genesis.getItemId();
    }

    // 봉인된 제네시스 무기 최종 해방
    public void doGenesisWeaponUpgrade() {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Equip equip = null;

        if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() < 0) {
            self.say("장비 창을 1칸 이상 비우자.", ScriptMessageFlag.Self);
            return;
        }

        for (Item item : new ArrayList<>(getPlayer().getInventory(MapleInventoryType.EQUIPPED).list())) {
            for (int i : bmWeapons) {
                if (item.getItemId() == i + 1) {
                    equip = (Equip) item;
                    break;
                }
            }
        }
        if (equip == null) {
            for (Item item : new ArrayList<>(getPlayer().getInventory(MapleInventoryType.EQUIP).list())) {
                for (int i : bmWeapons) {
                    if (item.getItemId() == i + 1) {
                        equip = (Equip) item;
                        break;
                    }
                }
            }
        }
        if (equip == null) {
            self.say("알 수 없는 오류가 발생했습니다.", ScriptMessageFlag.Self);
            return;
        }
        /*int weaponID = equip.getItemId() + 1;
        Equip genesis = (Equip) ii.getEquipById(weaponID);

        if (genesis == null) {
            sendNext("알 수 없는 오류가 발생했습니다.");
            dispose();
            return;
        }*/
        int weaponID = equip.getItemId();
        Equip genesis = (Equip) ii.getEquipById(weaponID);

        if (genesis == null) {self.say("알 수 없는 오류가 발생했습니다.", ScriptMessageFlag.Self);
            return;
        }

        int flag = EquipEnchantMan.filterForJobWeapon(weaponID);
        ItemUpgradeFlag[] flagArray = new ItemUpgradeFlag[]{
                ItemUpgradeFlag.INC_PAD,
                ItemUpgradeFlag.INC_MAD
        };
        ItemUpgradeFlag[] flagArray2 = new ItemUpgradeFlag[]{
                ItemUpgradeFlag.INC_STR,
                ItemUpgradeFlag.INC_DEX,
                ItemUpgradeFlag.INC_LUK,
                ItemUpgradeFlag.INC_MHP
        };
        ItemUpgradeFlag[] flagArray3 = new ItemUpgradeFlag[]{
                ItemUpgradeFlag.INC_INT
        };
        List<EquipEnchantScroll> source = new ArrayList<>();
        for (ItemUpgradeFlag f : flagArray) {
            for (ItemUpgradeFlag f2 : f == ItemUpgradeFlag.INC_PAD ? flagArray2 : flagArray3) {
                int index = 3; // 15%
                EquipEnchantOption option = new EquipEnchantOption();
                option.setOption(f.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
                if ((f2.check(flag))) {
                    option.setOption(f2.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3) * (f2 == ItemUpgradeFlag.INC_MHP ? 50 : 1));
                    if (option.flag > 0) {
                        source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
                    }
                }
            }
        }

        // 예외 처리
        if (equip.getItemId() == 1242140) { // 제논 DEX, LUK
            source.clear();
            EquipEnchantOption option = new EquipEnchantOption();
            option.setOption(ItemUpgradeFlag.INC_PAD.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
            option.setOption(ItemUpgradeFlag.INC_LUK.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3));

            source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
        }
        if (equip.getItemId() == 1232121) { // 데벤져
            source.clear();
            EquipEnchantOption option = new EquipEnchantOption();
            option.setOption(ItemUpgradeFlag.INC_PAD.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
            option.setOption(ItemUpgradeFlag.INC_MHP.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3) * 50);

            source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
        }
        if (equip.getItemId() == 1292021) { // 호영
            source.clear();
            EquipEnchantOption option = new EquipEnchantOption();
            option.setOption(ItemUpgradeFlag.INC_PAD.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
            option.setOption(ItemUpgradeFlag.INC_LUK.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3));

            source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
        }
        if (equip.getItemId() == 1362148) { // 팬텀
            source.clear();
            EquipEnchantOption option = new EquipEnchantOption();
            option.setOption(ItemUpgradeFlag.INC_PAD.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
            option.setOption(ItemUpgradeFlag.INC_LUK.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3));

            source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
        }
        if (equip.getItemId() == 1362148) { // 표도
            source.clear();
            EquipEnchantOption option = new EquipEnchantOption();
            option.setOption(ItemUpgradeFlag.INC_PAD.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
            option.setOption(ItemUpgradeFlag.INC_LUK.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3));

            source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
        }
        if (source.size() <= 0) {self.say("알 수 없는 오류가 발생했습니다.", ScriptMessageFlag.Self);
            return;
        }
        EquipEnchantScroll scroll = source.get(0); // 첫번째가 직업에 맞는 주문서
        if (scroll == null) {self.say("알 수 없는 오류가 발생했습니다.", ScriptMessageFlag.Self);
            return;
        }
        // 8번 성공시킴

        Equip zeroEquip = null;
        if (GameConstants.isZero(getPlayer().getJob())) {
            zeroEquip = (Equip) getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(equip.getPosition() == -11 ? (short) -10 : -11);
        }
        for (int i = 0; i < 8; ++i) {
            scroll.upgrade(genesis, 0, true, zeroEquip);
        }

        // 22성 부여
        genesis.setCHUC(22);
        genesis.setItemState(equip.getItemState() | ItemStateFlag.AMAZING_HYPER_UPGRADE_CHECKED.getValue());

        byte grade = genesis.getAdditionalGrade();
        if (grade == 0) {
            grade = 1;
        }

        // 유니크 잠재능력 3줄
        genesis.setLines((byte) 3); // 3줄
        genesis.setState((byte) 19); // 유니크
        for (int i = 0; i < 3; ++i) {
            int optionGrade = 3; // 유니크
            int option = ItemOptionInfo.getItemOption(equip.getItemId(), optionGrade, genesis.getPotentials(false, i), GradeRandomOption.Black);
            genesis.setPotentialOption(i, option);
        }

        // 에픽 에디셔널 잠재능력 3줄
        for (int i = 0; i < 3; ++i) {
            int optionGrade = 2; // 에픽
            int option = ItemOptionInfo.getItemOption(equip.getItemId(), optionGrade, genesis.getPotentials(true, i), GradeRandomOption.Additional);
            genesis.setPotentialOption(i + 3, option);
        }

        // 추옵 부여
        if (BonusStat.resetBonusStat(genesis, BonusStatPlaceType.LevelledRebirthFlame)) {
        }

        if (zeroEquip != null) {
            zeroEquip.setCHUC(genesis.getCHUC());
            zeroEquip.setItemState(genesis.getItemState());
            zeroEquip.setExGradeOption(genesis.getExGradeOption());
            zeroEquip.setLines(genesis.getLines());
            zeroEquip.setState(genesis.getState());
            zeroEquip.setPotential1(genesis.getPotential1());
            zeroEquip.setPotential2(genesis.getPotential2());
            zeroEquip.setPotential3(genesis.getPotential3());
            zeroEquip.setPotential4(genesis.getPotential4());
            zeroEquip.setPotential5(genesis.getPotential5());
            zeroEquip.setPotential6(genesis.getPotential6());
        }
        MapleInventoryType type = MapleInventoryType.EQUIP;
        if (equip.getPosition() < 0) {
            type = MapleInventoryType.EQUIPPED;
        }
        getPlayer().send(CWvsContext.InventoryPacket.deleteItem(equip));

        MapleInventoryManipulator.removeFromSlot(getClient(), type, equip.getPosition(), equip.getQuantity(), false, false);
        MapleInventoryManipulator.addbyItem(getClient(), genesis);


        Center.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, getPlayer().getName() + "님이 봉인된 힘을 해방하고 검은 마법사의 힘이 담긴 제네시스 무기의 주인이 되었습니다."));
    }
}
