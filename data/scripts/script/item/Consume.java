package script.item;

import constants.GameConstants;
import constants.JosaType;
import constants.Locales;
import constants.QuestExConstants;
import database.DBConfig;
import database.DBConnection;
import database.loader.CharacterSaveFlag;
import logging.LoggingManager;
import logging.entry.ConsumeLog;
import network.auction.AuctionServer;
import network.center.Center;
import network.game.GameServer;
import network.models.CWvsContext;
import network.shop.CashShopServer;
import objects.context.MonsterCollection;
import objects.fields.Field;
import objects.fields.gameobject.lifes.ChangeableStats;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.fields.gameobject.lifes.MapleMonster;
import objects.fields.gameobject.lifes.OverrideMonsterStats;
import objects.item.*;
import objects.quest.QuestEx;
import objects.users.MapleCharacter;
import objects.users.MapleTrait;
import objects.users.enchant.*;
import objects.utils.*;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

import java.nio.file.Paths;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class Consume extends ScriptEngineNPC {

    public void decreaseBossCount(List<Triple<Integer, String, String>> bossList, int itemID) {

        if (getPlayer().getMap().getFieldSetInstance() != null) {
            getPlayer().dropMessage(5, "보스 진행중엔 이용이 불가능합니다");
            return;
        }

        if (DBConfig.isGanglim) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Calendar CAL = new GregorianCalendar(Locale.KOREA);
            String fDate = sdf.format(CAL.getTime());

            String[] dates = fDate.split("-");
            int hours = Integer.parseInt(dates[3]);
            int minutes = Integer.parseInt(dates[4]);

            if (hours >= 23 && minutes >= 50) {
                self.sayOk("#fs11#11시 50분부터 12시 까지는 차감권을 이용할 수 없어", ScriptMessageFlag.NpcReplacedByUser);
                return;
            }

            StringBuilder bossComment = new StringBuilder("#fs11#어떤 보스의 #b입장 횟수#k를 1회 차감 할까?\r\n")
                    .append("차감권은 하루에 #r20번#k만 사용가능하니 신중히 골라야겠어.\r\n\r\n");

            int i = 0;
            for (var triple : bossList) {
                bossComment.append("#L").append(i).append("##b#fUI/UIWindow2.img/UserList/Main/Boss/BossList/")
                        .append(triple.left).append("/Icon/normal/0##k").append(triple.mid).append("\r\n");
                i++;
            }

            int select = self.askMenu(bossComment.toString(), ScriptMessageFlag.NpcReplacedByUser);
            var triple = bossList.get(select);

            int cancount = 20 - getPlayer().getOneInfoQuestInteger(1234569, "OffsetCount");

            int usecount = self.askNumber(
                    "#fs11##fUI/UIWindow2.img/UserList/Main/Boss/BossList/" + triple.left + "/Icon/normal/0#\r\n#r#e"
                            + triple.mid + " #k#n에 차감 티켓 몇개를 사용할까?\r\n사용 가능한 차감권 갯수 : " + cancount,
                    1, 1, cancount, ScriptMessageFlag.NpcReplacedByUser);
            if (usecount < 1)
                return;
            if (usecount > 20)
                return;

            if (1 == self.askYesNo(String.format("#r#e#fs11##fUI/UIWindow2.img/UserList/Main/Boss/BossList/" +
                    "%d" +
                    "/Icon/normal/0#\r\n" +
                    "%s" +
                    "#fs11##n#k 클리어 횟수 " + usecount + "회 차감을 진행할까?\r\n\r\n" +
                    "#fs11##r(※ 사용시 되돌릴 수 없습니다.)", triple.left, triple.mid), ScriptMessageFlag.NpcReplacedByUser)) {
                int count = getPlayer().getOneInfoQuestInteger(1234569, triple.right);
                int count2 = getPlayer().getOneInfoQuestInteger(1234570, triple.right);
                int count3 = getPlayer().getOneInfoQuestInteger(1234589, triple.right);
                int count4 = getPlayer().getOneInfoQuestInteger(1234590, triple.right);

                if (count < usecount && count2 < usecount && count3 < usecount && count4 < usecount) {
                    self.sayOk("#fs11#입장횟수가 사용할 차감 티켓의 갯수보다 적은 보스잖아?", ScriptMessageFlag.NpcReplacedByUser);
                    return;
                }

                if (target.exchange(itemID, -usecount) == 1) {
                    if (count > 0) {
                        getPlayer().updateOneInfo(1234569, triple.right, String.valueOf(count - usecount));
                    }
                    if (count2 > 0) {
                        getPlayer().updateOneInfo(1234570, triple.right, String.valueOf(count2 - usecount));
                    }
                    if (count3 > 0) {
                        getPlayer().updateOneInfo(1234589, triple.right, String.valueOf(count3 - usecount));
                    }
                    if (count4 > 0) {
                        getPlayer().updateOneInfo(1234590, triple.right, String.valueOf(count4 - usecount));
                    }

                    int offsetCount = getPlayer().getOneInfoQuestInteger(1234569, "OffsetCount");
                    int currentCount = offsetCount + usecount;
                    getPlayer().updateOneInfo(1234569, "OffsetCount", String.valueOf(currentCount));

                    String str = "#r#e#fs11##fUI/UIWindow2.img/UserList/Main/Boss/BossList/" + triple.left
                            + "/Icon/normal/0#\r\n" +
                            triple.mid +
                            "#fs11##n#k 클리어 횟수 " + usecount + "회 차감이 완료 되었습니다.\r\n" + "#fs11##n#k남은 사용 가능 횟수: "
                            + (20 - currentCount);

                    getPlayer().setSaveFlag(getPlayer().getSaveFlag() | CharacterSaveFlag.QUEST_INFO.getFlag()); // questinfo
                                                                                                                 // 저장
                    getPlayer().saveToDB(false, false);
                    self.sayOk(str, ScriptMessageFlag.NpcReplacedByUser);
                }

            }
        }
    }

    public void clearBossCount(List<Triple<Integer, String, String>> bossList, int itemID) {
        if (DBConfig.isGanglim) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Calendar CAL = new GregorianCalendar(Locale.KOREA);
            String fDate = sdf.format(CAL.getTime());

            String[] dates = fDate.split("-");
            int hours = Integer.parseInt(dates[3]);
            int minutes = Integer.parseInt(dates[4]);

            if (hours >= 23 && minutes >= 50) {
                self.sayOk("11시 50분부터 12시 까지는 초기화권을 이용하실 수 없습니다.", ScriptMessageFlag.NpcReplacedByUser);
                return;
            }

            StringBuilder bossComment = new StringBuilder("#fs11#어떤 보스의 #b입장 횟수#k를 초기화 할까?\r\n")
                    .append("초기화권은 하루에 #r3번#k만 사용가능합니다.\r\n\r\n");

            int i = 0;
            for (var triple : bossList) {
                bossComment.append("#L").append(i).append("##b#fUI/UIWindow2.img/UserList/Main/Boss/BossList/")
                        .append(triple.left).append("/Icon/normal/0##k").append(triple.mid).append("\r\n");
                i++;
            }

            int select = self.askMenu(bossComment.toString(), ScriptMessageFlag.NpcReplacedByUser);
            var triple = bossList.get(select);
            if (1 == self.askYesNo(String.format("#r#e#fs11##fUI/UIWindow2.img/UserList/Main/Boss/BossList/" +
                    "%d" +
                    "/Icon/normal/0#\r\n" +
                    "%s" +
                    "#fs11##n#k 클리어 횟수 초기화를 진행할까?\r\n\r\n" +
                    "#fs11##r(※ 사용시 되돌릴 수 없습니다.)", triple.left, triple.mid), ScriptMessageFlag.NpcReplacedByUser)) {
                String date = String.valueOf(GameConstants.getCurrentDate_NoTime());

        int used = getPlayer().getOneInfoQuestInteger(1234569, "ResetBoss");
        if (used >= 3) {
            self.sayOk("오늘은 이미 초기화권을 3번 사용했다. 내일 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            return;
        }

                if (target.exchange(itemID, -1) == 1) {
                    getPlayer().updateOneInfo(1234569, "ResetBoss", String.valueOf(used + 1));

                    getPlayer().updateOneInfo(1234569, triple.right, "0");
                    getPlayer().updateOneInfo(1234570, triple.right, "0");
                    getPlayer().updateOneInfo(1234589, triple.right, "0");
                    getPlayer().updateOneInfo(1234590, triple.right, "0");

                    String str = "#r#e#fs11##fUI/UIWindow2.img/UserList/Main/Boss/BossList/" + triple.left
                            + "/Icon/normal/0#\r\n" +
                            triple.mid +
                            "#fs11##n#k 클리어 횟수 초기화가 완료 되었습니다.";

                self.sayOk("초기화가 완료되었습니다. 남은 횟수: " + (3 - (used + 1)) + "회",
                            ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    public void compassUse_cash() {
        List<Triple<Integer, String, String>> bossList = List.of(
                new Triple<>(13, "스우", "swoo_clear"),
                new Triple<>(15, "데미안", "demian_clear"),
                new Triple<>(19, "루시드", "lucid_clear"),
                new Triple<>(23, "윌", "will_clear"));

        clearBossCount(bossList, 2430030);
    }

    public void snapShot() {
        List<Triple<Integer, String, String>> bossList = List.of(
                new Triple<>(27, "듄켈", "dunkel_clear"),
                new Triple<>(26, "더스크", "dusk_clear"),
                new Triple<>(24, "진 힐라", "jinhillah_clear"),
                new Triple<>(29, "가디언 엔젤 슬라임", "guardian_angel_slime_clear"));

        clearBossCount(bossList, 2430031);
    }

    public void blackBag() {
        List<Triple<Integer, String, String>> bossList = List.of(
                new Triple<>(13, "스우", "swoo_clear"),
                new Triple<>(15, "데미안", "demian_clear"),
                new Triple<>(19, "루시드", "lucid_clear"),
                new Triple<>(23, "윌", "will_clear"));

        decreaseBossCount(bossList, 2430032);
    }

    public void xmas_present00() {
        List<Triple<Integer, String, String>> bossList = List.of(
                new Triple<>(27, "듄켈", "dunkel_clear"),
                new Triple<>(26, "더스크", "dusk_clear"),
                new Triple<>(24, "진 힐라", "jinhillah_clear"),
                new Triple<>(29, "가디언 엔젤 슬라임", "guardian_angel_slime_clear"));

        decreaseBossCount(bossList, 2430033);
    }

    // 진:眞 초기지원 상자
    public void consume_2439600() {
        int[][] rewards = new int[][] {
                { 2439602, 1 }, // 진:眞 무기 지원 상자
                /*
                 * {1003243, 1}, // 메이플 래티넘 베레모
                 * {1102295, 1},// 메이플 래티넘 클록
                 * {1052358, 1}, // 메이플 래티넘 리센느
                 * {1072522, 1}, // 메이플 래티넘 슈즈
                 */
                { 1004492, 1 }, // 메이플 트레져 캡
                { 1102828, 1 }, // 메이플 트레져 망토
                { 1052929, 1 }, // 메이플 트레져 슈트
                { 1132287, 1 }, // 메이플 트레져 벨트
                { 1152187, 1 }, // 메이플 트레져 견장
                { 1073057, 1 }, // 메이플 트레져 슈즈
                { 1082647, 1 }, // 메이플 트레져 장갑
        };
        int[] weapons = new int[] {
                1212098, 1213009, 1214009, 1222092, 1232092, 1242099, 1272020, 1282020, 1302312, 1312182, 1322233,
                1332257, 1342097, 1362118, 1372204, 1382242, 1402233, 1412161, 1422168, 1432197, 1442251, 1452235,
                1462222, 1472244, 1482199, 1492209, 1522121, 1532127, 1592010, 1292009, 1262012, 1582012, 1404009
        };

        initNPC(MapleLifeFactory.getNPC(9062474));
        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            if (reward[1] != -1) {
                v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "##k " + reward[1] + "개\r\n";
            }
        }
        v0 += "\r\n#b자쿰의 포이즈닉 무기 중 1개 선택 획득\r\n";

        if (DBConfig.isGanglim) {
            v0 += "  - 스타포스 10성 적용, 에픽 잠재능력 옵션 부여, 올스탯 +30, 공/마 +15 적용\r\n";
        } else {
            v0 += "  - 스타포스 10성 적용, 유니크 잠재능력 옵션 부여, 올스탯 30 적용\r\n";
        }
        if (DBConfig.isGanglim) {
            v0 += "\r\n모든 메이플 트레져 장비에 올스탯 +30, 공/마 +15 적용\r\n";
        }
        v0 += "\r\n#k#e[스킬 획득]#n#b\r\n";
        v0 += "#s80001825# 일섬\r\n";
        v0 += "#s80001829# 비연\r\n";
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 올스탯 아이템 지금 때문에 아래와 같은 방식으로 해야한다.
            if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1 ||
                    getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 8) {

                self.say("#b장비 인벤토리#k와 #b소비 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                boolean isZero = false;
                int v3 = -1;
                if (GameConstants.isZero(getPlayer().getJob())) {
                    isZero = true;
                } else {
                    String v2 = "다음과 같은 무기 중 1개를 선택할 수 있다. 어떤것을 고를까?#b\r\n\r\n";
                    if (DBConfig.isGanglim) {
                        v2 = "다음 무기 중 1개를 선택할 수 있다. 어떤것을 고를까?#b\r\n\r\n";
                    }
                    for (int i = 0; i < weapons.length; ++i) {
                        int weapon = weapons[i];
                        v2 += "#L" + i + "##i" + weapon + "# #z" + weapon + "##l\r\n";
                    }
                    v3 = self.askMenu(v2, ScriptMessageFlag.NpcReplacedByUser);
                }
                if (v3 >= 0 || isZero) {
                    int itemID = 0;
                    String v4 = "다음과 같이 아이템을 획득할 수 있다. 이대로 진행할까?\r\n\r\n";
                    v4 += "#e[아이템 획득]#n\r\n";
                    if (!isZero) {
                        itemID = weapons[v3];
                        v4 += "#e#i" + itemID + "# #z" + itemID + "# (선택)#n\r\n";
                    }
                    for (int[] reward : rewards) {
                        if (reward[1] != -1) {
                            v4 += "#b#i" + reward[0] + "# #z" + reward[0] + "##k " + reward[1] + "개\r\n";
                        }
                    }
                    if (self.askYesNo(v4, ScriptMessageFlag.NpcReplacedByUser) == 1) {

                        if (target.exchange(2439600, -1, 2439602, 1) == 1) {
                            getPlayer().changeSkillLevel(80001825, 30, 30);
                            getPlayer().changeSkillLevel(80001829, 5, 5);

                            if (getPlayer().getOneInfoQuestInteger(1234569, "get_treasure_set") == 0) {
                                exchangeSupportEquip(1004492, 30, 15, 100);
                                exchangeSupportEquip(1102828, 30, 15, 100);
                                exchangeSupportEquip(1052929, 30, 15, 100);
                                exchangeSupportEquip(1132287, 30, 15, 100);
                                exchangeSupportEquip(1152187, 30, 15, 100);
                                exchangeSupportEquip(1073057, 30, 15, 100);
                                exchangeSupportEquip(1082647, 30, 15, 100);
                                exchangeSupportEquip(itemID, 30, 15, 100);
                                self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                                getPlayer().updateOneInfo(1234569, "get_treasure_set", "1");
                            } else {
                                self.say("메이플 트레져 세트는 이미 지급받아서 받지 못하였다. 나머지 아이템을 확인해보자.",
                                        ScriptMessageFlag.NpcReplacedByUser);
                            }

                        }
                    } else {
                        if (DBConfig.isGanglim) {
                            self.say("조금 더 생각해보자.", ScriptMessageFlag.NpcReplacedByUser);
                        } else {
                            self.say("아무래도 조금 더 고민을 해봐야할 것 같다.", ScriptMessageFlag.NpcReplacedByUser);
                        }
                    }

                }
            }
        }
    }

    // 진:眞 만나서 반가워요! 상자
    public void consume_2439601() {
        initNPC(MapleLifeFactory.getNPC(9062474));
        if (getPlayer().getOneInfoQuestInteger(1234567, "use_first_support") == 1) {
            if (DBConfig.isGanglim) {
                self.say("이미 보상을 받았었던 것 같다.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                self.say("이미 보상을 받았던 것 같은데?", ScriptMessageFlag.NpcReplacedByUser);
            }

            if (target.exchange(2439601, -1) == 1) {
            }
            return;
        }
        int[][] rewards = new int[][] {
                { 2439605, 1 }, // 진:眞 스페셜 코디 상자 (S)
                { 2439604, 5 }, // 진:眞 스페셜 코디 상자 (R)
                { 2436018, 1 }, // 진:眞 스페셜 헤어 쿠폰
                { 2439601, -1 }, // 상자 소비
        };
        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        if (DBConfig.isGanglim) {
            v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다고 한다. 지금 바로 열어볼까?\r\n\r\n";
        }
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            if (reward[1] != -1) {
                v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "##k " + reward[1] + "개\r\n";
            }
        }
        if (!DBConfig.isGanglim) {
            v0 += "\r\n#b#i4310306# #t4310306# #k100개";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            if (target.exchange(rewards) == 1) {
                if (!DBConfig.isGanglim) {
                    getPlayer().gainStackEventGauge(0, 100, true);
                }
                getPlayer().updateOneInfo(1234567, "use_first_support", "1");
                self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                self.say("#b소비 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            }
        }
    }

    // 진:眞 무기 지원 상자
    public void consume_2439602() {
        int[] rewards = new int[] {
                1212127,
                1213039,
                1214031,
                1222120,
                1232120,
                1242061,
                1262048,
                1272037,
                1282037,
                1292039,
                1302353,
                1312210,
                1322265,
                1332287,
                1362147,
                1372235,
                1382272,
                1402266,
                1412187,
                1422195,
                1432225,
                1442283,
                1452264,
                1462250,
                1472273,
                1482230,
                1492243,
                1522150,
                1532155,
                1582042,
                1592029,
                1404016
        };

        if (DBConfig.isGanglim) {
            initNPC(MapleLifeFactory.getNPC(2008));
        } else {
            initNPC(MapleLifeFactory.getNPC(9062474));
        }
        if (GameConstants.isZero(getPlayer().getJob())) {
            self.say("제로는 이용할 수 없습니다.");
            if (target.exchange(2439602, -1) == 1) {
            }
            return;
        }
        String v0 = "다음과 같은 #b파프니르 무기 중 1개#k를 선택하여 획득할 수 있어.\r\n선택한 무기는 #e10일간#n 사용 가능하고 #b올스탯 +200, 공/마 +200, 추가옵션#k이 적용되어 지급돼.\r\n\r\n원하는 무기를 골라봐.#b\r\n\r\n";
        if (DBConfig.isGanglim) {
            v0 = "다음과 같은 #b파프니르 무기 중 1개#k를 선택하여 획득할 수 있습니다.\r\n선택한 무기는 #e14일간#n 사용 가능하고 #b올스탯 +250, 공/마 +250, 추가옵션#k이 적용되어 지급됩니다.\r\n\r\n원하는 무기를 골라보세요.#b\r\n\r\n";
        }
        for (int i = 0; i < rewards.length; ++i) {
            int itemID = rewards[i];
            v0 += "#L" + i + "##i" + itemID + "# #z" + itemID + "##l\r\n";
        }

        int v1 = self.askMenu(v0, ScriptMessageFlag.NpcReplacedByNpc);
        if (v1 >= rewards.length) {
            return; // TODO: Hack
        }
        int itemID = rewards[v1];
        String v2 = "선택한 무기는 #b#i" + itemID + "# #z" + itemID
                + "##k(이)야.\r\n정말 이 무기로 선택할거니?\r\n\r\n#b(#e예#n를 누르면 아이템을 획득합니다.)";
        if (DBConfig.isGanglim) {
            v2 = "선택한 무기는 #b#i" + itemID + "# #z" + itemID
                    + "##k 입니다.\r\n이 무기로 선택하시겠어요?\r\n\r\n#b(#e확인#n을 누르면 아이템을 획득합니다.)";
        }
        if (self.askYesNo(v2, ScriptMessageFlag.NpcReplacedByNpc) > 0) {
            if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
                if (DBConfig.isGanglim) {
                    self.say("#b장비 인벤토리#k 공간을 확보하고 다시 시도해주세요.");
                } else {
                    self.say("#b장비 인벤토리#k 공간을 확보하고 다시 시도해주겠니?");
                }
            } else {
                if (target.exchange(2439602, -1) == 1) {
                    if (DBConfig.isGanglim) {
                        exchangeSupportEquipBonusStatPeriod(itemID, 250, 250, 14);
                        self.say("지급이 완료되었습니다. 인벤토리를 확인해보세요!");
                    } else {
                        exchangeSupportEquipBonusStatPeriod(itemID, 200, 200, 10);
                        self.say("지급이 완료되었어. 인벤토리를 확인해봐!");
                    }
                }
            }
        }
    }

    // 초보자 패키지 상자
    public void consume_2439603() {
        initNPC(MapleLifeFactory.getNPC(9062000));
        int[][] rewards = new int[][] {
                { 5068300, 4 }, // 위습의 원더베리 4개
                { 5069100, 1 }, // 루나 크리스탈 1개
                { 2049360, 3 }, // 놀라운 장비강화 주문서 3개
                { 2450153, 10 }, // 경험치 2배 쿠폰 10개
                { 2436605, 5 }, // 명장의 큐브 복주머니 5개
                { 2435719, 100 }, // 코어 젬스톤 100개
                { 2439292, 3 }, // 미궁의 아케인심볼 상자 3개
                { 5680410, 1 }, // 10만 캐시 교환권
                { 2439603, -1 }, // 상자 소비
        };

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            if (reward[1] != -1) {
                v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "##k " + reward[1] + "개\r\n";
            }
        }
        v0 += "\r\n#b1,000,000,000 메소 (10억 메소)";
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            if (target.exchange(rewards) == 1) {
                getPlayer().gainMeso(1000000000, true);
                self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                self.say("#b소비 인벤토리#k와 #b캐시 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            }
        }
    }

    // 진:眞 스페셜 코디 상자 (S)
    static int[] extremeList = new int[] { 1109000, 1009017, 1009018, 1009019, 1009020, 1009021, 1009022, 1009023,
            1009024, 1009025, 1009026, 1009027, 1009028, 1009029, 1009030, 1009031, 1009032, 1009033, 1009034, 1009035,
            1009036, 1009037, 1009038, 1009039, 1009040, 1009041, 1009042, 1009043, 1009044, 1009045, 1009046, 1009047,
            1009048, 1009049, 1009050, 1009051, 1009052, 1009053, 1009054, 1009055, 1009056, 1009057, 1009058, 1009059,
            1009060, 1009061, 1009062, 1009063, 1009064, 1009065, 1009066, 1009067, 1009068, 1009069, 1009070, 1009071,
            1009072, 1009073, 1009074, 1009078, 1009079, 1009080, 1009081, 1009082, 1709000, 1709001, 1709002, 1709003,
            1709004, 1709005, 1709006, 1709007, 1709008, 1709009, 1709010, 1709011, 1709012, 1709013, 1709014, 1709015,
            1709016, 1709017, 1709018, 1079000, 1079001, 1079002, 1079003, 1079004, 1079005, 1059000, 1059001, 1059002,
            1059003, 1059004, 1059005, 1059006, 1059007, 1059008, 1059009, 1059010, 1059011, 1059012, 1059013, 1059014,
            1059015, 1059016, 1059017, 1059018, 1059019, 1059020, 1059021, 1059022, 1059023, 1059024, 1059027, 1059028,
            1059029, 1059030, 1059031, 1059032, 1059033, 1059034, 1059035, 1059036, 1059037, 1059038, 1059039, 1059040,
            1059041, 1059042, 1059043, 1059044, 1059045, 1059046, 1059047, 1059048, 1059049, 1009000, 1009001, 1009002,
            1009003, 1009004, 1009005, 1009006, 1009007, 1009008, 1009009, 1009010, 1009011, 1009012, 1009013, 1009014,
            1009015, 1009016 };
    static int[][] jinList = new int[][] {
            // 모자
            { 1007000, 1007001, 1007002, 1007003, 1007004, 1007005, 1007006, 1007007, 1007008, 1007009, 1007010,
                    1007011, 1007012, 1007013, 1007014, 1007015, 1007016, 1007017, 1007018, 1007019, 1007020, 1007021,
                    1007022, 1007023, 1007024, 1007025, 1007026, 1007027, 1007028, 1007029, 1007030, 1007031, 1007032,
                    1007033, 1007034, 1007035, 1007036, 1007037, 1007038, 1007039, 1007040, 1007041, 1007042, 1007043,
                    1007044, 1007045, 1007046, 1007047, 1007048, 1007049, 1007050, 1007051, 1007052, 1007053, 1007054,
                    1007055, 1007056, 1007057, 1007058, 1007059, 1007060, 1007061, 1007062, 1007063, 1007064, 1007065,
                    1007066, 1007067, 1007068, 1007069, 1007070, 1007071, 1007072, 1007073, 1007074, 1007075, 1007076,
                    1007077, 1007078, 1007079, 1007080, 1007081, 1007082, 1007083, 1007084, 1007085, 1007086, 1007087,
                    1007088, 1007089, 1007090, 1007091, 1007092, 1007093, 1007094, 1007095, 1007096, 1007097, 1007098,
                    1007099, 1007100, 1007101, 1007102, 1007103, 1007104, 1007105, 1007106, 1007107, 1007108, 1007109,
                    1007110, 1007111, 1007112, 1007113, 1007114, 1007115, 1007116, 1007117, 1007118, 1007119, 1007120,
                    1007121, 1007122, 1007123, 1007124, 1007125, 1007126, 1007127, 1007128, 1007129, 1007130, 1007131,
                    1007132, 1007133, 1007134, 1007135, 1007136, 1007137, 1007138, 1007139, 1007140, 1007141, 1007142,
                    1007143, 1007144, 1007145, 1007146, 1007147, 1007148, 1007149, 1007150, 1007151, 1007152, 1007153,
                    1007154, 1007155, 1007156, 1007157, 1007158, 1007159, 1007160, 1007161, 1007162, 1007163, 1007164,
                    1007165, 1007166, 1007167, 1007168, 1007169, 1007170, 1007171, 1007172, 1007173, 1007174, 1007175,
                    1007176, 1007177, 1007178, 1007179, 1007180, 1007181, 1007182, 1007183, 1007184, 1007185, 1007186,
                    1007187, 1007188, 1007189, 1007190, 1007191, 1007192, 1007193, 1007194, 1007195, 1007196, 1007197,
                    1007198, 1007199, 1007200, 1007201, 1007202, 1007203, 1007204, 1007205, 1007206, 1007207, 1007208,
                    1007209, 1007210, 1007211, 1007212, 1007213, 1007214, 1007215, 1007216, 1007217, 1007218, 1007219,
                    1007220, 1007221, 1007222, 1007223, 1007224, 1007225, 1007226, 1007227, 1007228, 1007229 },
            // 한벌옷
            { 1056000, 1056001, 1056002, 1056003, 1056004, 1056005, 1056006, 1056007, 1056008, 1056009, 1056010,
                    1056011, 1056012, 1056013, 1056014, 1056015, 1056016, 1056017, 1056018, 1056019, 1056020, 1056021,
                    1056022, 1056023, 1056024, 1056025, 1056026, 1056027, 1056028, 1056029, 1056030, 1056031, 1056032,
                    1056033, 1056034, 1056035, 1056036, 1056037, 1056038, 1056039, 1056040, 1056041, 1056042, 1056043,
                    1056044, 1056045, 1056046, 1056047, 1056048, 1056049, 1056050, 1056051, 1056052, 1056053, 1056054,
                    1056055, 1056056, 1056057, 1056058, 1056059, 1056060, 1056061, 1056062, 1056063, 1056064, 1056065,
                    1056066, 1056067, 1056068, 1056069, 1056070, 1056071, 1056072, 1056073, 1056074, 1056075, 1056076,
                    1056077, 1056078, 1056079, 1056080, 1056081, 1056082, 1056083, 1056084,
                    1057000, 1057001, 1057002, 1057003, 1057004, 1057005, 1057006, 1057007, 1057008, 1057009, 1057010,
                    1057011, 1057012, 1057013, 1057014, 1057015, 1057016, 1057017, 1057018, 1057019,
                    1058000, 1058001, 1058002, 1058003, 1058004, 1058005, 1058006, 1058007, 1058008, 1058009, 1058010,
                    1058011, 1058012, 1058013, 1058014, 1058015, 1058016, 1058017, 1058018, 1058019, 1058020, 1058021,
                    1058022, 1058023, 1058024, 1058025, 1058026, 1058027, 1058028, 1058029, 1058030, 1058031, 1058032,
                    1058033, 1058034, 1058035, 1058036, 1058037, 1058038, 1058039, 1058040, 1058041, 1058042, 1058043,
                    1058044, 1058045, 1058046, 1058047, 1058048, 1058049, 1058050, 1058051, 1058052, 1058053, 1058054,
                    1058055, 1058056, 1058057, 1058058, 1058059, 1058060, 1058061, 1058062, 1058063, 1058064, 1058065,
                    1058066, 1058067, 1058068, 1058069, 1058070, 1058071, 1058072, 1058073, 1058074, 1058075, 1058076,
                    1058077, 1058078, 1058079, 1058080, 1058081, 1058082, 1058083, 1058084 },
            // 상의
            { 1045000, 1045001, 1045002, 1045003, 1045004 },
            // 무기
            { 1704000, 1704001, 1704002, 1704003, 1704004, 1704005, 1704006, 1704007, 1704008, 1704009, 1704010,
                    1704011, 1704012, 1704013, 1704014,
                    1705000, 1705001, 1705002, 1705003, 1705004, 1705005, 1705006, 1705007, 1705008, 1705009, 1705010,
                    1705011, 1705012, 1705013, 1705014, 1705015, 1705016, 1705017, 1705018, 1705019, 1705020, 1705021,
                    1705022, 1705023, 1705024, 1705025, 1705026, 1705027, 1705028, 1705029, 1705030, 1705031, 1705032,
                    1705033, 1705034, 1705035, 1705036, 1705037, 1705038, 1705039, 1705040, 1705041, 1705042, 1705043,
                    1705044, 1705045, 1705046, 1705047, 1705048, 1705049, 1705050, 1705051, 1705052, 1705053, 1705054,
                    1705055, 1705056, 1705057, 1705058, 1705059, 1705060, 1705061, 1705062, 1705063, 1705064, 1705065,
                    1705066, 1705067, 1705068, 1705069, 1705070, 1705071, 1705072, 1705073, 1705074, 1705075, 1705076,
                    1705077, 1705078, 1705079, 1705080, 1705081, 1705082, 1705083, 1705084, 1705085, 1705086, 1705087,
                    1705088, 1705089 },
            // 망토
            { 1104000, 1104001, 1104002, 1104003, 1104004, 1104005, 1104006, 1104007, 1104008, 1104009, 1104010,
                    1104011, 1104012, 1104013, 1104014, 1104015, 1104016, 1104017, 1104018, 1104019, 1104020, 1104021,
                    1104022, 1104023, 1104024, 1104035, 1104036, 1104037, 1104038, 1104039 },
            // 장갑
            { 1084000, 1084001, 1084002, 1084003, 1084004, 1084005, 1084006, 1084007, 1084008 },
            // 신발
            { 1075000, 1075001, 1075002, 1075003, 1075004, 1075005, 1075006, 1075007, 1075008, 1075009, 1075010,
                    1075011, 1075012, 1075013, 1075014 },
            // 얼굴장식
            { 1012850, 1012851, 1012852, 1012853, 1012854, 1012855, 1012856, 1012857, 1012858, 1012859, 1012860,
                    1012861, 1012862, 1012863, 1012864 }
    };
    static int[][] royalList = new int[][] {
            // 모자
            { 1007000, 1007001, 1007002, 1007003, 1007004, 1007005, 1007006, 1007007, 1007008, 1007009, 1007010,
                    1007011, 1007012, 1007013, 1007014, 1007015, 1007016, 1007017, 1007018, 1007019, 1007020, 1007021,
                    1007022, 1007023, 1007024, 1007025, 1007026, 1007027, 1007028, 1007029, 1007030, 1007031, 1007032,
                    1007033, 1007034, 1007035, 1007036, 1007037, 1007038, 1007039, 1007040, 1007041, 1007042, 1007043,
                    1007044, 1007045, 1007046, 1007047, 1007048, 1007049, 1007050, 1007051, 1007052, 1007053, 1007054,
                    1007055, 1007056, 1007057, 1007058, 1007059, 1007060, 1007061, 1007062, 1007063, 1007064, 1007065,
                    1007066, 1007067, 1007068, 1007069, 1007070, 1007071, 1007072, 1007073, 1007074, 1007075, 1007076,
                    1007077, 1007078, 1007079, 1007080, 1007081, 1007082, 1007083, 1007084, 1007085, 1007086, 1007087,
                    1007088, 1007089, 1007090, 1007091, 1007092, 1007093, 1007094, 1007095, 1007096, 1007097, 1007098,
                    1007099, 1007100, 1007101, 1007102, 1007103, 1007104, 1007105, 1007106, 1007107, 1007108, 1007109,
                    1007110, 1007111, 1007112, 1007113, 1007114, 1007115, 1007116, 1007117, 1007118, 1007119, 1007120,
                    1007121, 1007122, 1007123, 1007124, 1007125, 1007126, 1007127, 1007128, 1007129, 1007130, 1007131,
                    1007132, 1007133, 1007134, 1007135, 1007136, 1007137, 1007138, 1007139, 1007140, 1007141, 1007142,
                    1007143, 1007144, 1007145, 1007146, 1007147, 1007148, 1007149, 1007150, 1007151, 1007152, 1007153,
                    1007154, 1007155, 1007156, 1007157, 1007158, 1007159, 1007160, 1007161, 1007162, 1007163, 1007164,
                    1007165, 1007166, 1007167, 1007168, 1007169, 1007170, 1007171, 1007172, 1007173, 1007174, 1007175,
                    1007176, 1007177, 1007178, 1007179, 1007180, 1007181, 1007182, 1007183, 1007184, 1007185, 1007186,
                    1007187, 1007188, 1007189, 1007190, 1007191, 1007192, 1007193, 1007194, 1007195, 1007196, 1007197,
                    1007198, 1007199, 1007200, 1007201, 1007202, 1007203, 1007204, 1007205, 1007206, 1007207, 1007208,
                    1007209, 1007210, 1007211, 1007212, 1007213, 1007214, 1007215, 1007216 },
            // 한벌옷
            { 1056000, 1056001, 1056002, 1056003, 1056004, 1056005, 1056006, 1056007, 1056008, 1056009, 1056010,
                    1056011, 1056012, 1056013, 1056014, 1056015, 1056016, 1056017, 1056018, 1056019, 1056020, 1056021,
                    1056022, 1056023, 1056024, 1056025, 1056026, 1056027, 1056028, 1056029, 1056030, 1056031, 1056032,
                    1056033, 1056034, 1056035, 1056036, 1056037, 1056038, 1056039, 1056040, 1056041, 1056042, 1056043,
                    1056044, 1056045, 1056046, 1056047, 1056048, 1056049, 1056050, 1056051, 1056052, 1056053, 1056054,
                    1056055, 1056056, 1056057, 1056058, 1056059, 1056060, 1056061, 1056062, 1056063, 1056064, 1056065,
                    1056066, 1056067, 1056068, 1056069, 1056070, 1056071, 1056072, 1056073, 1056074, 1056075, 1056076,
                    1056077, 1056078, 1056079, 1056080, 1056081, 1056082, 1056083, 1056084, 1056085, 1056086, 1056087,
                    1056088, 1056089, 1056090, 1056091, 1056092, 1056093, 1056094, 1056095, 1056096, 1056097, 1057000,
                    1057001, 1057002, 1057003, 1057004, 1057005, 1057006, 1057007, 1057008, 1057009, 1057010, 1057011,
                    1057012, 1057013, 1057014, 1057015, 1057016, 1057017, 1057018, 1057019, 1057020, 1058000, 1058001,
                    1058002, 1058003, 1058004, 1058005, 1058006, 1058007, 1058008, 1058009, 1058010, 1058011, 1058012,
                    1058013, 1058014, 1058015, 1058016, 1058017, 1058018, 1058019, 1058020, 1058021, 1058022, 1058023,
                    1058024, 1058025, 1058026, 1058027, 1058028, 1058029, 1058030, 1058031, 1058032, 1058033, 1058034,
                    1058035, 1058036, 1058037, 1058038, 1058039, 1058040, 1058041, 1058042, 1058043, 1058044, 1058045,
                    1058046, 1058047, 1058048, 1058049, 1058050, 1058051, 1058052, 1058053, 1058054, 1058055, 1058056,
                    1058057, 1058058, 1058059, 1058060, 1058061, 1058062, 1058063, 1058064, 1058065, 1058066, 1058067,
                    1058068, 1058069, 1058070, 1058071, 1058072, 1058073, 1058074, 1058075, 1058076, 1058077, 1058078,
                    1058079, 1058080, 1058081, 1058082, 1058083, 1058084, 1058085, 1058086, 1058087, 1058088, 1058089,
                    1058090, 1058091, 1058092, 1058093, 1058094, 1058095, 1058096, 1058097 },
            // 상의
            {},
            // 무기
            { 1705000, 1705001, 1705002, 1705003, 1705004, 1705005, 1705006, 1705007, 1705008, 1705009, 1705010,
                    1705011, 1705012, 1705013, 1705014, 1705015, 1705016, 1705017, 1705018, 1705019, 1705020, 1705021,
                    1705022, 1705023, 1705024, 1705025, 1705026, 1705027, 1705028, 1705029, 1705030, 1705031, 1705032,
                    1705033, 1705034, 1705035, 1705036, 1705037, 1705038, 1705039, 1705040, 1705041, 1705042, 1705043,
                    1705044, 1705045, 1705046, 1705047, 1705048, 1705049, 1705050, 1705051, 1705052, 1705053, 1705054,
                    1705055, 1705056, 1705057, 1705058, 1705059, 1705060, 1705061, 1705062, 1705063, 1705064, 1705065,
                    1705066, 1705067, 1705068, 1705069, 1705070, 1705071, 1705072, 1705073, 1705074, 1705075, 1705076,
                    1705077, 1705078, 1705079, 1705080, 1705081, 1705082, 1705083, 1705084, 1705085, 1705086, 1705087,
                    1705088, 1705089, 1705090, 1705091, 1705092, 1705093, 1705094, 1705095, 1705096, 1705097, 1705098,
                    1705099, 1705100, 1705101, 1705102, 1705103, 1705104, 1705105, 1705106, 1705107, 1705108, 1705109,
                    1705110, 1705111, 1705112, 1705113, 1705114, 1705115, 1705116, 1705117, 1705118, 1705119, 1705120,
                    1705121, 1705122, 1705123, 1705124, 1705125, 1705126, 1705127, 1705128, 1705129, 1705130, 1705131,
                    1705132, 1705133, 1705134, 1705135, 1705136, 1705137, 1705138, 1705139, 1705140, 1705141, 1705142,
                    1705143, 1705144, 1705145, 1705146, 1705147, 1705148, 1705149, 1705150, 1705151, 1705152, 1705153 },
            // 망토
            { 1104000, 1104001, 1104002, 1104003, 1104004, 1104005, 1104006, 1104007, 1104008, 1104009, 1104010,
                    1104011, 1104012, 1104013, 1104014, 1104015, 1104016, 1104017, 1104018, 1104019, 1104020, 1104021,
                    1104022, 1104023, 1104024, 1104025, 1104026, 1104027, 1104028, 1104029, 1104030, 1104031, 1104032,
                    1104033, 1104034, 1104035, 1104036, 1104037, 1104038, 1104039, 1104040, 1104041 },
            // 장갑
            {},
            // 신발
            { 1075000, 1075001, 1075002, 1075003, 1075004, 1075005, 1075006, 1075007, 1075008, 1075009, 1075010,
                    1075011, 1075012, 1075013 },
            // 악세서리
            { 1022500, 1022501, 1022502, 1022503, 1022504, 1022505, 1022506, 1012900, 1012901, 1012902, 1012903,
                    1012904, 1012905, 1012906, 1012907, 1012908, 1012909, 1012910, 1012911, 1012912, 1012913, 1012914,
                    1012915, 1012916, 1012917, 1012918, 1012919, 1012920 }
    };
    static String[] label = new String[] {
            "모자", "한벌옷", "상의", "무기", "망토", "장갑", "신발", "얼굴장식"
    };

    public void consume_2439605() {
        if (DBConfig.isGanglim) {
            initNPC(MapleLifeFactory.getNPC(3003225));
        } else {
            initNPC(MapleLifeFactory.getNPC(9062475));
        }
        if (DBConfig.isGanglim) {
            int v0 = self.askMenu("#b#i2439605# #z2439605##k는 원하는 스페셜 코디 아이템을 선택하여 획득할 수 있습니다.#b\r\n\r\n" +
                    "#L0#지금 바로 선택하겠습니다.#l\r\n" +
                    "#L1#획득할 수 있는 스페셜 코디 리스트를 보여주세요.#k#l\r\n", ScriptMessageFlag.NpcReplacedByNpc);
            switch (v0) {
                case 0: { // 개봉
                    String v1 = "어떤 스페셜 코디를 고르시겠어요?#b\r\n\r\n";
                    v1 += "#L0#강림 메이플 스페셜 코디 1기#l\r\n";
                    int v2 = self.askMenu(v1, ScriptMessageFlag.NpcReplacedByNpc);

                    if (v2 == 0) { // 강림
                        String v3 = "스페셜 코디 부위를 고르세요.#b\r\n\r\n";
                        v3 += "#L0#모자#l\r\n#L1#한벌옷#l\r\n#L2#상의#l\r\n#L3#무기#l\r\n#L4#망토#l\r\n#L5#장갑#l\r\n#L6#신발#l\r\n#L7#얼굴장식#l";
                        int v4 = self.askMenu(v3, ScriptMessageFlag.NpcReplacedByNpc);
                        String v5 = "원하는 스페셜 코디를 골라보세요! #e(" + label[v4] + ")#n #b\r\n\r\n";
                        int index = 0;
                        for (int itemID : royalList[v4]) {
                            v5 += "#L" + index++ + "##i" + itemID + "# #z" + itemID + "##l\r\n";
                        }
                        int v6 = self.askMenu(v5, ScriptMessageFlag.NpcReplacedByNpc);
                        if (self.askYesNo("선택하신 스페셜 코디 #b#i" + royalList[v4][v6] + "# #z" + royalList[v4][v6]
                                + "##k(을)를 선택하시겠어요?\r\n\r\n#b#e예#n를 누르면 상자가 소비되며 교환됩니다.") > 0) {
                            if (target.exchange(2439605, -1, royalList[v4][v6], 1) == 1) {
                                self.say("스페셜 코디를 획득했습니다.\r\n#b치장 인벤토리#k를 확인해보세요~!");
                            } else {
                                self.say("#b치장 인벤토리#k 슬롯을 확보하고 다시 시도해주세요!");
                            }
                        } else {
                            self.say("좀 더 생각해보시고 다시 찾아주세요!", ScriptMessageFlag.NpcReplacedByNpc);
                        }
                    }
                }
                    break;
                case 1: {// 리스트
                    int v1 = self.askMenu("열람하고 싶은 스페셜 코디 리스트를 선택해주세요.#b\r\n\r\n#L0#강림 메이플 스페셜 코디 1기#l",
                            ScriptMessageFlag.NpcReplacedByNpc);
                    if (v1 == 0) { // 강림 스페셜 코디 리스트
                        String v2 = "#e<강림 메이플 스페셜 코디 1기>#n\r\n스페셜 코디 부위를 선택해주세요.\r\n\r\n#b";
                        v2 += "#L0#모자#l\r\n#L1#한벌옷#l\r\n#L2#상의#l\r\n#L3#무기#l\r\n#L4#망토#l\r\n#L5#장갑#l\r\n#L6#신발#l\r\n#L7#얼굴장식#l";
                        int v3 = self.askMenu(v2, ScriptMessageFlag.NpcReplacedByNpc);

                        String v4 = "#e<강림 메이플 스페셜 코디 1기 (" + label[v3] + ")>#n#b\r\n\r\n";
                        for (int itemID : royalList[v3]) {
                            v4 += "#i" + itemID + "# #z" + itemID + "#\r\n";
                        }
                        self.say(v4, ScriptMessageFlag.NpcReplacedByNpc);
                    }
                }
                    break;
            }
        } else {
            int v0 = self.askMenu("#b#i2439605# #z2439605##k는 원하는 스페셜 코디 아이템을 선택하여 획득할 수 있습니다.#b\r\n\r\n" +
                    "#L0#지금 바로 선택하겠습니다.#l\r\n" +
                    "#L1#획득할 수 있는 스페셜 코디 리스트를 보여주세요.#k#l\r\n", ScriptMessageFlag.NpcReplacedByNpc);
            switch (v0) {
                case 0: { // 개봉
                    String v1 = "어떤 스페셜 코디를 고르시겠어요?#b\r\n\r\n";
                    v1 += "#L0#익스트림[E] 스페셜 코디#l\r\n";
                    v1 += "#L1#진[J] 스페셜 코디#l";
                    int v2 = self.askMenu(v1, ScriptMessageFlag.NpcReplacedByNpc);
                    if (v2 == 0) { // 익스트림
                        String v3 = "원하는 스페셜 코디를 골라보세요!#b\r\n\r\n";
                        for (int i = 0; i < extremeList.length; ++i) {
                            int itemID = extremeList[i];
                            v3 += "#L" + i + "##i" + itemID + "# #z" + itemID + "##l\r\n";
                        }
                        int v4 = self.askMenu(v3, ScriptMessageFlag.NpcReplacedByNpc);
                        if (self.askYesNo("선택하신 스페셜 코디 #b#i" + extremeList[v4] + "# #z" + extremeList[v4]
                                + "##k(을)를 선택하시겠어요?\r\n\r\n#b#e예#n를 누르면 상자가 소비되며 교환됩니다.") > 0) {
                            if (target.exchange(2439605, -1, extremeList[v4], 1) == 1) {
                                self.say("스페셜 코디를 획득했습니다.\r\n#b치장 인벤토리#k를 확인해보세요~!");
                            } else {
                                self.say("#b치장 인벤토리#k 슬롯을 확보하고 다시 시도해주세요!");
                            }
                        } else {
                            self.say("좀 더 생각해보시고 다시 찾아주세요!", ScriptMessageFlag.NpcReplacedByNpc);
                        }
                    } else if (v2 == 1) { // 진
                        String v3 = "스페셜 코디 부위를 고르세요.#b\r\n\r\n";
                        v3 += "#L0#모자#l\r\n#L1#한벌옷#l\r\n#L2#상의#l\r\n#L3#무기#l\r\n#L4#망토#l\r\n#L5#장갑#l\r\n#L6#신발#l\r\n#L7#얼굴장식#l";
                        int v4 = self.askMenu(v3, ScriptMessageFlag.NpcReplacedByNpc);
                        String v5 = "원하는 스페셜 코디를 골라보세요! #e(" + label[v4] + ")#n #b\r\n\r\n";
                        int index = 0;
                        for (int itemID : jinList[v4]) {
                            v5 += "#L" + index++ + "##i" + itemID + "# #z" + itemID + "##l\r\n";
                        }
                        int v6 = self.askMenu(v5, ScriptMessageFlag.NpcReplacedByNpc);
                        if (self.askYesNo("선택하신 스페셜 코디 #b#i" + jinList[v4][v6] + "# #z" + jinList[v4][v6]
                                + "##k(을)를 선택하시겠어요?\r\n\r\n#b#e예#n를 누르면 상자가 소비되며 교환됩니다.") > 0) {
                            if (target.exchange(2439605, -1, jinList[v4][v6], 1) == 1) {
                                self.say("스페셜 코디를 획득했습니다.\r\n#b치장 인벤토리#k를 확인해보세요~!");
                            } else {
                                self.say("#b치장 인벤토리#k 슬롯을 확보하고 다시 시도해주세요!");
                            }
                        } else {
                            self.say("좀 더 생각해보시고 다시 찾아주세요!", ScriptMessageFlag.NpcReplacedByNpc);
                        }
                    }
                }
                    break;
                case 1: {// 리스트
                    int v1 = self.askMenu(
                            "보고 싶은 스페셜 코디 리스트를 선택해주세요.#b\r\n\r\n#L0#익스트림[E] 스페셜 코디#l\r\n#L1#진[J] 스페셜 코디#l",
                            ScriptMessageFlag.NpcReplacedByNpc);
                    if (v1 == 0) { // 익스트림 스페셜 코디 리스트
                        String v2 = "#e[익스트림[E] 스페셜 코디 리스트]#n\r\n\r\n#b";
                        for (int itemID : extremeList) {
                            v2 += "#i" + itemID + "# #z" + itemID + "#\r\n";
                        }
                        self.say(v2, ScriptMessageFlag.NpcReplacedByNpc);
                    } else if (v1 == 1) { // 진 스페셜 코디 리스트
                        String v2 = "#e[진[J] 스페셜 코디 리스트]#n\r\n스페셜 코디 부위를 선택해주세요.\r\n\r\n#b";
                        v2 += "#L0#모자#l\r\n#L1#한벌옷#l\r\n#L2#상의#l\r\n#L3#무기#l\r\n#L4#망토#l\r\n#L5#장갑#l\r\n#L6#신발#l\r\n#L7#얼굴장식#l";
                        int v3 = self.askMenu(v2, ScriptMessageFlag.NpcReplacedByNpc);

                        String v4 = "#e[진[J] 스페셜 코디 리스트 (" + label[v3] + ")]#n#b\r\n\r\n";
                        for (int itemID : jinList[v3]) {
                            v4 += "#i" + itemID + "# #z" + itemID + "#\r\n";
                        }
                        self.say(v4, ScriptMessageFlag.NpcReplacedByNpc);
                    }
                }
                    break;
            }
        }
    }

    // 진:眞 스페셜 코디 상자 (R)
    public void consume_2439604() {
        if (DBConfig.isGanglim) {
            initNPC(MapleLifeFactory.getNPC(3003225));
        } else {
            initNPC(MapleLifeFactory.getNPC(9062475));
        }

        if (DBConfig.isGanglim) {
            int v0 = self.askMenu("#b#i2439604# #z2439604##k는 스페셜 코디 아이템 중 1개를 랜덤으로 획득할 수 있습니다.#b\r\n\r\n" +
                    "#L0#지금 바로 개봉하겠습니다.#l\r\n" +
                    "#L1#획득할 수 있는 스페셜 코디 리스트를 보여주세요.#k#l\r\n", ScriptMessageFlag.NpcReplacedByNpc);
            switch (v0) {
                case 0: { // 개봉
                    if (self.askYesNo("지금 바로 개봉하시겠어요?\r\n\r\n#b#e예#n를 누르면 상자가 소비되며 교환됩니다.",
                            ScriptMessageFlag.NpcReplacedByNpc) == 1) {
                        List<Integer> list = new ArrayList<>();
                        for (int i = 0; i < jinList.length; ++i) {
                            Arrays.stream(royalList[i]).forEach(list::add);
                        }
                        Collections.shuffle(list);
                        Integer pick = list.stream().findAny().orElse(null);
                        if (pick == null) {
                            self.say("알 수 없는 오류가 발생했어요. 잠시 후 다시 시도해주세요.", ScriptMessageFlag.NpcReplacedByNpc);
                            return;
                        }
                        if (target.exchange(2439604, -1, pick, 1) == 1) {
                            self.say("#b#i2439604# #z2439604##k에서 다음과 같은 아이템이 나왔어요!\r\n\r\n#b#i" + pick + "# #z" + pick
                                    + "# #k1개 획득!", ScriptMessageFlag.NpcReplacedByNpc);
                        } else {

                        }
                    } else {
                        self.say("좀 더 생각해보시고 다시 찾아주세요!", ScriptMessageFlag.NpcReplacedByNpc);
                    }
                }
                    break;
                case 1: {// 리스트
                    int v1 = self.askMenu("보고 싶은 스페셜 코디 리스트를 선택해주세요.#b\r\n\r\n#L0#강림 메이플 스페셜 코디 1기#l",
                            ScriptMessageFlag.NpcReplacedByNpc);
                    if (v1 == 0) { // 강림 스페셜 코디 리스트
                        String v2 = "#e<강림 메이플 스페셜 코디 1기 리스트>#n\r\n스페셜 코디 부위를 선택해주세요.\r\n\r\n#b";
                        v2 += "#L0#모자#l\r\n#L1#한벌옷#l\r\n#L2#상의#l\r\n#L3#무기#l\r\n#L4#망토#l\r\n#L5#장갑#l\r\n#L6#신발#l\r\n#L7#얼굴장식#l";
                        int v3 = self.askMenu(v2, ScriptMessageFlag.NpcReplacedByNpc);

                        String v4 = "#e<강림 메이플 스페셜 코디 1기 리스트 (" + label[v3] + ")>#n#b\r\n\r\n";
                        for (int itemID : royalList[v3]) {
                            v4 += "#i" + itemID + "# #z" + itemID + "#\r\n";
                        }
                        self.say(v4, ScriptMessageFlag.NpcReplacedByNpc);
                    }
                }
                    break;
            }
        } else {
            int v0 = self.askMenu("#b#i2439604# #z2439604##k는 스페셜 코디 아이템 중 1개를 랜덤으로 획득할 수 있습니다.#b\r\n\r\n" +
                    "#L0#지금 바로 개봉하겠습니다.#l\r\n" +
                    "#L1#획득할 수 있는 스페셜 코디 리스트를 보여주세요.#k#l\r\n", ScriptMessageFlag.NpcReplacedByNpc);
            switch (v0) {
                case 0: { // 개봉
                    if (self.askYesNo("지금 바로 개봉하시겠어요?\r\n\r\n#b#e예#n를 누르면 상자가 소비되며 교환됩니다.",
                            ScriptMessageFlag.NpcReplacedByNpc) == 1) {
                        List<Integer> list = new ArrayList<>();
                        Arrays.stream(extremeList).forEach(list::add);
                        for (int i = 0; i < jinList.length; ++i) {
                            Arrays.stream(jinList[i]).forEach(list::add);
                        }
                        Collections.shuffle(list);
                        Integer pick = list.stream().findAny().orElse(null);
                        if (pick == null) {
                            self.say("알 수 없는 오류가 발생했어요. 잠시 후 다시 시도해주세요.", ScriptMessageFlag.NpcReplacedByNpc);
                            return;
                        }
                        if (target.exchange(2439604, -1, pick, 1) == 1) {
                            self.say("#b#i2439604# #z2439604##k에서 다음과 같은 아이템이 나왔어요!\r\n\r\n#b#i" + pick + "# #z" + pick
                                    + "# #k1개 획득!", ScriptMessageFlag.NpcReplacedByNpc);
                        } else {

                        }
                    } else {
                        self.say("좀 더 생각해보시고 다시 찾아주세요!", ScriptMessageFlag.NpcReplacedByNpc);
                    }
                }
                    break;
                case 1: {// 리스트
                    int v1 = self.askMenu(
                            "보고 싶은 스페셜 코디 리스트를 선택해주세요.#b\r\n\r\n#L0#익스트림[E] 스페셜 코디#l\r\n#L1#진[J] 스페셜 코디#l",
                            ScriptMessageFlag.NpcReplacedByNpc);
                    if (v1 == 0) { // 익스트림 스페셜 코디 리스트
                        String v2 = "#e[익스트림[E] 스페셜 코디 리스트]#n\r\n\r\n#b";
                        for (int itemID : extremeList) {
                            v2 += "#i" + itemID + "# #z" + itemID + "#\r\n";
                        }
                        self.say(v2, ScriptMessageFlag.NpcReplacedByNpc);
                    } else if (v1 == 1) { // 진 스페셜 코디 리스트
                        String v2 = "#e[진[J] 스페셜 코디 리스트]#n\r\n스페셜 코디 부위를 선택해주세요.\r\n\r\n#b";
                        v2 += "#L0#모자#l\r\n#L1#한벌옷#l\r\n#L2#상의#l\r\n#L3#무기#l\r\n#L4#망토#l\r\n#L5#장갑#l\r\n#L6#신발#l\r\n#L7#얼굴장식#l";
                        int v3 = self.askMenu(v2, ScriptMessageFlag.NpcReplacedByNpc);

                        String v4 = "#e[진[J] 스페셜 코디 리스트 (" + label[v3] + ")]#n#b\r\n\r\n";
                        for (int itemID : jinList[v3]) {
                            v4 += "#i" + itemID + "# #z" + itemID + "#\r\n";
                        }
                        self.say(v4, ScriptMessageFlag.NpcReplacedByNpc);
                    }
                }
                    break;
            }
        }
    }

    // 진:眞 성장 지원 상자 1
    public void consume_2439580() {
        initNPC(MapleLifeFactory.getNPC(9062475));

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        if (DBConfig.isGanglim) {
            v0 += "#e[아이템 획득]#n\r\n";
            v0 += "#b#i5002239# #z5002239# (기간제 30일)#k 1개\r\n";
            v0 += "#b#i2630437# #z2630437# #k 100개\r\n";
            // 2630437
        } else {
            v0 += "#e[아이템 획득]#n\r\n";
            v0 += "#b#i5000930# #z5000930# (기간제 5일)#k 1개\r\n";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 펫때문에 이렇게 해야한다.
            if (DBConfig.isGanglim ? (getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 1 ||
                    getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1)
                    : getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 1) {
                self.say("#b캐시 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                if (target.exchange(2439580, -1) == 1) {
                    if (DBConfig.isGanglim) {
                        exchangePetPeriod(5002239, 30);
                        target.exchange(2630437, 100);
                    } else {
                        exchangePetPeriod(5000930, 5);
                    }
                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 성장 지원 상자 2
    public void consume_2439581() {
        initNPC(MapleLifeFactory.getNPC(9062475));

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        if (DBConfig.isGanglim) {
            v0 += "#e[아이템 획득]#n\r\n";
            v0 += "#b#i1112401# #z1112401# (기간제 7일)#k 1개\r\n";
            v0 += "  - 올스탯 +70, 공/마 +40\r\n";
        } else {
            v0 += "#e[아이템 획득]#n\r\n";
            v0 += "#b#i1112405# #z1112405# (기간제 7일)#k 1개\r\n";
            v0 += "  - 올스탯 +50, 공/마 +25\r\n";
            v0 += "#b#i1112431# #z1112431# (기간제 7일)#k 1개\r\n";
            v0 += "  - 올스탯 +50, 공/마 +25\r\n";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 템 때문에 이렇게 해야한다.
            if (DBConfig.isGanglim ? getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1
                    : getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 2) {
                self.say("#b장비 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                if (target.exchange(2439581, -1) == 1) {
                    if (DBConfig.isGanglim) {
                        exchangeSupportEquipPeriod(1112401, 70, 40, 7, 50);
                    } else {
                        exchangeSupportEquipPeriod(1112405, 50, 25, 7);
                        exchangeSupportEquipPeriod(1112431, 50, 25, 7);
                    }
                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 성장 지원 상자 3
    public void consume_2439582() {
        initNPC(MapleLifeFactory.getNPC(9062475));

        int rewards[][] = new int[][] {
                // ItemID, Quantity, Period, AllStat, Attack
                { 2433509, 1, 0, 0, 0 }, // 진:眞 블랙 장비 상자
                { 1032259, 1, 7, 50, 25 }, // 할로윈 귀고리
                { 2432643, 5, 0, 0, 0 }, // 마스터리 북 20
                { 2434589, 6, 0, 0, 0 } // 검은 수호의 조각
        };
        if (DBConfig.isGanglim) {
            if (getPlayer().getGender() == 0) {
                rewards = new int[][] {
                        { 1122074, 1, 7, 100, 50 },
                        { 1005781, 1, 7, 30, 10 }, // 츄츄세트
                        { 1050583, 1, 7, 30, 10 }, // 츄츄세트
                        { 1103332, 1, 7, 30, 10 }, // 츄츄세트
                        { 1073534, 1, 7, 30, 10 }, // 츄츄세트
                        { 1703084, 1, 7, 30, 10 }, // 츄츄세트
                };
            } else {
                rewards = new int[][] {
                        { 1122074, 1, 7, 100, 50 },
                        { 1005781, 1, 7, 30, 10 }, // 츄츄세트
                        { 1051656, 1, 7, 30, 10 }, // 츄츄세트
                        { 1103332, 1, 7, 30, 10 }, // 츄츄세트
                        { 1073534, 1, 7, 30, 10 }, // 츄츄세트
                        { 1703084, 1, 7, 30, 10 }, // 츄츄세트
                };
            }
        }

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "#";
            if (reward[2] > 0) {
                v0 += " (기간제 " + reward[2] + "일) ";
            }
            v0 += "#k " + reward[1] + "개";
            if (reward[3] > 0 || reward[4] > 0) {
                v0 += "\r\n  - 올스탯 +" + reward[3] + ", 공/마 +" + reward[4] + "\r\n";
            }
            v0 += "\r\n";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 템 때문에 이렇게 해야한다.
            if (DBConfig.isGanglim ? (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 5 ||
                    getPlayer().getInventory(MapleInventoryType.CASH_EQUIP).getNumFreeSlot() < 5)
                    : (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 5 ||
                            getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1)) {
                self.say("#b장비 인벤토리#k와 #b소비 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                if (target.exchange(2439582, -1) == 1) {
                    for (int[] reward : rewards) {
                        if (reward[3] > 0 || reward[4] > 0) {
                            if (DBConfig.isGanglim) {
                                exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2], 70);
                            } else {
                                exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2]);
                            }
                        } else {
                            target.exchange(reward[0], reward[1]);
                        }
                    }
                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 성장 지원 상자 4
    public void consume_2439583() {
        initNPC(MapleLifeFactory.getNPC(9062475));

        int rewards[][] = new int[][] {
                // ItemID, Quantity, Period, AllStat, Attack
                { 1122330, 1, 7, 70, 30 }, // 할로윈 펜던트
                { 1022252, 1, 7, 70, 30 }, // 할로윈 귀고리
                { 2436605, 1, 0, 0, 0 }, // 명장의 큐브 복주머니
        };

        if (DBConfig.isGanglim) {
            rewards = new int[][] {
                    { 1022251, 1, 7, 100, 50 }
            };
        }

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "#";
            if (reward[2] > 0) {
                v0 += " (기간제 " + reward[2] + "일) ";
            }
            v0 += "#k " + reward[1] + "개";
            if (reward[3] > 0 || reward[4] > 0) {
                v0 += "\r\n  - 올스탯 +" + reward[3] + ", 공/마 +" + reward[4] + "\r\n";
            }
            v0 += "\r\n";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 템 때문에 이렇게 해야한다.
            if (DBConfig.isGanglim ? getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1
                    : (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1 ||
                            getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 2)) {
                self.say("#b장비 인벤토리#k와 #b소비 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                if (target.exchange(2439583, -1) == 1) {
                    for (int[] reward : rewards) {
                        if (reward[3] > 0 || reward[4] > 0) {
                            if (DBConfig.isGanglim) {
                                exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2], 70);
                            } else {
                                exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2]);
                            }
                        } else {
                            target.exchange(reward[0], reward[1]);
                        }
                    }
                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 성장 지원 상자 5
    public void consume_2439584() {
        initNPC(MapleLifeFactory.getNPC(9062475));

        int rewards[][] = new int[][] {
                // ItemID, Quantity, Period, AllStat, Attack
                { 1152052, 1, 7, 100, 50 }, // 월모 견장
                { 1712001, 10, 0, 0, 0 }, // 아케인심볼 : 소멸의 여로 10개
                { 2436078, 30, 0, 0, 0 }, // 코어 젬스톤 30개
        };

        if (DBConfig.isGanglim) {
            rewards = new int[][] {
                    // 리부트링
            };
        }

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        v0 += "#b#i1113227# #z1113227# (기간제 14일) #k 1개\r\n";
        v0 += "  - 메소 획득률 40%, 아이템 획득률 40%\r\n";
        for (int[] reward : rewards) {
            v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "#";
            if (reward[2] > 0) {
                v0 += " (기간제 " + reward[2] + "일) ";
            }
            v0 += "#k " + reward[1] + "개";
            if (reward[3] > 0 || reward[4] > 0) {
                v0 += "\r\n  - 올스탯 +" + reward[3] + ", 공/마 +" + reward[4] + "\r\n";
            }
            v0 += "\r\n";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 템 때문에 이렇게 해야한다.
            if (DBConfig.isGanglim ? getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1
                    : getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1 ||
                            getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 12) {
                self.say("#b장비 인벤토리#k와 #b소비 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                if (target.exchange(2439584, -1) == 1) {
                    for (int[] reward : rewards) {
                        if (reward[3] > 0 || reward[4] > 0) {
                            exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2]);
                        } else {
                            target.exchange(reward[0], reward[1]);
                        }
                    }
                    if (DBConfig.isGanglim) {
                        Equip item = (Equip) MapleItemInformationProvider.getInstance().getEquipById(1113227);
                        item.setState((byte) 20);
                        item.setPotential1(40650);
                        item.setPotential2(40650);
                        item.setPotential4(40656);
                        item.setPotential5(40656);
                        item.setWatk((short) 100);
                        item.setMatk((short) 100);
                        item.setDownLevel((byte) 70);
                        item.setExpiration((new Date()).getTime() + (1000 * 60 * 60 * 24 * 20));
                        MapleInventoryManipulator.addFromDrop(getClient(), item, false);
                    } else {
                        if (getPlayer().getOneInfoQuestInteger(1234566, "reboot_ring") <= 0) {
                            Equip item = (Equip) MapleItemInformationProvider.getInstance().getEquipById(1113227);
                            item.setState((byte) 20);
                            item.setPotential1(40650);
                            item.setPotential2(40650);
                            item.setPotential4(40656);
                            item.setPotential5(40656);
                            item.setExpiration((new Date()).getTime() + (1000 * 60 * 60 * 24 * 20));
                            MapleInventoryManipulator.addFromDrop(getClient(), item, false);
                            getPlayer().updateOneInfo(1234566, "reboot_ring", "1");
                        }
                    }

                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 성장 지원 상자 6
    public void consume_2439585() {
        initNPC(MapleLifeFactory.getNPC(9062475));

        int rewards[][] = new int[][] {
                // ItemID, Quantity, Period, AllStat, Attack
                { 2439292, 5, 0, 0, 0 }, // 미궁의 아케인심볼 상자
                { 2436605, 3, 0, 0, 0 }, // 명장의 큐브 복주머니
                { 1114305, 1, 0, 100, 50 }, // 카오스 링
        };

        if (DBConfig.isGanglim) {
            rewards = new int[][] {
                    { 1152118, 1, 7, 100, 50 } // 스텔라견장
            };
        }

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "#";
            if (reward[2] > 0) {
                v0 += " (기간제 " + reward[2] + "일) ";
            }
            v0 += "#k " + reward[1] + "개";
            if (reward[3] > 0 || reward[4] > 0) {
                v0 += "\r\n  - 올스탯 +" + reward[3] + ", 공/마 +" + reward[4] + "\r\n";
            }
            v0 += "\r\n";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 템 때문에 이렇게 해야한다.
            if (DBConfig.isGanglim ? getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1
                    : getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 7 ||
                            getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
                self.say("#b장비 인벤토리#k와 #b소비 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                if (target.exchange(2439585, -1) == 1) {
                    for (int[] reward : rewards) {
                        if (reward[3] > 0 || reward[4] > 0) {
                            exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2]);
                        } else {
                            target.exchange(reward[0], reward[1]);
                        }
                    }
                    if (!DBConfig.isGanglim) {
                        getPlayer().gainMeso(50000000, true);
                    }
                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 성장 지원 상자 7
    public void consume_2439586() {
        initNPC(MapleLifeFactory.getNPC(9062475));

        int rewards[][] = new int[][] {
                // ItemID, Quantity, Period, AllStat, Attack
                { 2436605, 1, 0, 0, 0 }, // 명장의 큐브 복주머니
                { 1162013, 1, 7, 50, 30 }, // ES스퀘어
        };

        if (DBConfig.isGanglim) {
            rewards = new int[][] {
                    { 1114305, 1, 7, 100, 50 } // 카오스링
            };
        }

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "#";
            if (reward[2] > 0) {
                v0 += " (기간제 " + reward[2] + "일) ";
            }
            v0 += "#k " + reward[1] + "개";
            if (reward[3] > 0 || reward[4] > 0) {
                v0 += "\r\n  - 올스탯 +" + reward[3] + ", 공/마 +" + reward[4] + "\r\n";
            }
            v0 += "\r\n";
        }
        v0 += "#b  - 포켓 개방";
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 템 때문에 이렇게 해야한다.
            if (DBConfig.isGanglim ? getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1
                    : getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1 ||
                            getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
                self.say("#b장비 인벤토리#k와 #b소비 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                if (target.exchange(2439586, -1) == 1) {
                    for (int[] reward : rewards) {
                        if (reward[3] > 0 || reward[4] > 0) {
                            exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2]);
                        } else {
                            target.exchange(reward[0], reward[1]);
                        }
                    }
                    if (!DBConfig.isGanglim) {
                        getPlayer().forceCompleteQuest(6500); // 포켓 개방
                        getPlayer().gainMeso(50000000, true);
                        getPlayer().gainExp(15000000000L, true, true, true);
                    }
                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 성장 지원 상자 8
    public void consume_2439587() {
        initNPC(MapleLifeFactory.getNPC(9062475));

        int rewards[][] = new int[][] {
                // ItemID, Quantity, Period, AllStat, Attack
                { 5062010, 10, 0, 0, 0 }, // 블랙 큐브
                { 4001832, 5000, 0, 0, 0 }, // 주문의 흔적
                { 1032227, 1, 0, 40, 25 }, // 이피아의 귀고리
        };

        if (DBConfig.isGanglim) {
            rewards = new int[][] {
                    { 1132296, 1, 0, 100, 30 } // 분노한 자쿰의 벨트
            };
        }

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "#";
            if (reward[2] > 0) {
                v0 += " (기간제 " + reward[2] + "일) ";
            }
            v0 += "#k " + reward[1] + "개";
            if (reward[3] > 0 || reward[4] > 0) {
                v0 += "\r\n  - 올스탯 +" + reward[3] + ", 공/마 +" + reward[4] + "\r\n";
            }
            v0 += "\r\n";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 템 때문에 이렇게 해야한다.
            if (DBConfig.isGanglim ? getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1
                    : (getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 1 ||
                            getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1 ||
                            getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 1)) {
                self.say("#b장비, 소비, 캐시 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                if (target.exchange(2439587, -1) == 1) {
                    for (int[] reward : rewards) {
                        if (reward[3] > 0 || reward[4] > 0) {
                            exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2]);
                        } else {
                            target.exchange(reward[0], reward[1]);
                        }
                    }
                    if (!DBConfig.isGanglim) {
                        getPlayer().gainMeso(50000000, true);
                        getPlayer().gainExp(20000000000L, true, true, true);
                    }
                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 성장 지원 상자 9
    public void consume_2439588() {
        initNPC(MapleLifeFactory.getNPC(9062475));
        int rewards[][] = new int[][] {
                // ItemID, Quantity, Period, AllStat, Attack
                { 1162025, 1, 0, 30, 20 }, // 핑크빛 성배
                { 2436605, 3, 0, 0, 0 }, // 명장의 큐브 복주머니
        };
        if (DBConfig.isGanglim) {
            rewards = new int[][] {
                    { 1113282, 1, 0, 100, 30 }, // 고귀한 이피아의 반지
                    { 1182200, 1, 0, 30, 20 }, // 칠요의 뱃지
                    { 1162025, 1, 0, 50, 30 }, // 핑크빛 성배
            };
        }

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "#";
            if (reward[2] > 0) {
                v0 += " (기간제 " + reward[2] + "일) ";
            }
            v0 += "#k " + reward[1] + "개";
            if (reward[3] > 0 || reward[4] > 0) {
                v0 += "\r\n  - 올스탯 +" + reward[3] + ", 공/마 +" + reward[4] + "\r\n";
            }
            v0 += "\r\n";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 템 때문에 이렇게 해야한다.
            if (DBConfig.isGanglim ? (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 3)
                    : (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1 ||
                            getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1)) {
                if (DBConfig.isGanglim) {
                    self.say("#b장비, 캐시장비 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
                } else {
                    self.say("#b장비, 소비 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
                }
            } else {
                if (target.exchange(2439588, -1) == 1) {
                    for (int[] reward : rewards) {
                        if (reward[3] > 0 || reward[4] > 0) {
                            exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2]);
                        } else {
                            target.exchange(reward[0], reward[1]);
                        }
                    }
                    if (DBConfig.isGanglim) {
                        getPlayer().forceCompleteQuest(6500); // 포켓 개방
                    } else {
                        getPlayer().gainMeso(50000000, true);
                        getPlayer().gainExp(50000000000L, true, true, true);
                    }
                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 성장 지원 상자 10
    public void consume_2439589() {
        initNPC(MapleLifeFactory.getNPC(9062475));

        int rewards[][] = new int[][] {
                // ItemID, Quantity, Period, AllStat, Attack
                { 2436078, 20, 0, 0, 0 }, // 코어 젬스톤 20개
                { 2450064, 3, 0, 0, 0 }, // 경험치 2배 쿠폰 3개 (교불)
                { 5062010, 20, 0, 0, 0 }, // 블랙 큐브 20개
                { 2439292, 5, 0, 0, 0 }, // 미궁의 아케인심볼 상자
        };

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "#";
            if (reward[2] > 0) {
                v0 += " (기간제 " + reward[2] + "일) ";
            }
            v0 += "#k " + reward[1] + "개";
            if (reward[3] > 0 || reward[4] > 0) {
                v0 += "\r\n  - 올스탯 +" + reward[3] + ", 공/마 +" + reward[4] + "\r\n";
            }
            v0 += "\r\n";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 템 때문에 이렇게 해야한다.
            if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 9 ||
                    getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 1) {
                self.say("#b소비, 캐시 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                if (target.exchange(2439589, -1) == 1) {
                    for (int[] reward : rewards) {
                        if (reward[3] > 0 || reward[4] > 0) {
                            exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2]);
                        } else {
                            target.exchange(reward[0], reward[1]);
                        }
                    }
                    getPlayer().gainMeso(50000000, true);
                    getPlayer().gainExp(100000000000L, true, true, true);
                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 성장 지원 상자 11
    public void consume_2439590() {
        initNPC(MapleLifeFactory.getNPC(9062475));

        int rewards[][] = new int[][] {
                // ItemID, Quantity, Period, AllStat, Attack
                { 1182200, 1, 0, 30, 20 }, // 칠요의 뱃지
                { 5062010, 20, 0, 0, 0 }, // 블랙 큐브 20개
                { 5680150, 1, 0, 0, 0 }, // 3만 메이플포인트 교환권
                { 2434290, 5, 0, 0, 0 }, // 무공이 보증한 명예의 훈장 5개
        };

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "#";
            if (reward[2] > 0) {
                v0 += " (기간제 " + reward[2] + "일) ";
            }
            v0 += "#k " + reward[1] + "개";
            if (reward[3] > 0 || reward[4] > 0) {
                v0 += "\r\n  - 올스탯 +" + reward[3] + ", 공/마 +" + reward[4] + "\r\n";
            }
            v0 += "\r\n";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 템 때문에 이렇게 해야한다.
            if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 2 ||
                    getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 3 ||
                    getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
                self.say("#b소비, 캐시, 장비 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                if (target.exchange(2439590, -1) == 1) {
                    for (int[] reward : rewards) {
                        if (reward[3] > 0 || reward[4] > 0) {
                            exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2]);
                        } else {
                            target.exchange(reward[0], reward[1]);
                        }
                    }
                    getPlayer().gainMeso(100000000, true);
                    getPlayer().gainExp(100000000000L, true, true, true);
                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 성장 지원 상자 12
    public void consume_2439591() {
        initNPC(MapleLifeFactory.getNPC(9062475));

        int rewards[][] = new int[][] {
                // ItemID, Quantity, Period, AllStat, Attack
                { 4001832, 5000, 0, 0, 0 }, // 주문의 흔적
                { 2436078, 20, 0, 0, 0 }, // 코어 젬스톤 20개
                { 2450064, 3, 0, 0, 0 }, // 경험치 2배 쿠폰
                { 3014005, 1, 0, 0, 0 }, // 명예의 상징
                { 2434891, 1, 0, 0, 0 }, // 데미지스킨 선택 박스
                { 2439239, 1, 0, 0, 0 }, // 매지컬 주문서 교환권
        };

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "#";
            if (reward[2] > 0) {
                v0 += " (기간제 " + reward[2] + "일) ";
            }
            v0 += "#k " + reward[1] + "개";
            if (reward[3] > 0 || reward[4] > 0) {
                v0 += "\r\n  - 올스탯 +" + reward[3] + ", 공/마 +" + reward[4] + "\r\n";
            }
            v0 += "\r\n";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 템 때문에 이렇게 해야한다.
            if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 6 ||
                    getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 1 ||
                    getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 2) {
                self.say("#b소비, 기타, 설치 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                if (target.exchange(2439591, -1) == 1) {
                    for (int[] reward : rewards) {
                        if (reward[3] > 0 || reward[4] > 0) {
                            exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2]);
                        } else {
                            target.exchange(reward[0], reward[1]);
                        }
                    }
                    getPlayer().gainMeso(100000000, true);
                    getPlayer().gainExp(300000000000L, true, true, true);
                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 성장 지원 상자 13
    public void consume_2439592() {
        initNPC(MapleLifeFactory.getNPC(9062475));

        int rewards[][] = new int[][] {
                // ItemID, Quantity, Period, AllStat, Attack
                { 2439239, 2, 0, 0, 0 }, // 매지컬 주문서 교환권
                { 5680150, 2, 0, 0, 0 }, // 3만 메이플 포인트 교환권
                { 2439292, 5, 0, 0, 0 }, // 미궁의 아케인심볼 상자 5개
                { 2439604, 1, 0, 0, 0 }, // 진:眞 스페셜 코디 상자 (R)
        };

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "#";
            if (reward[2] > 0) {
                v0 += " (기간제 " + reward[2] + "일) ";
            }
            v0 += "#k " + reward[1] + "개";
            if (reward[3] > 0 || reward[4] > 0) {
                v0 += "\r\n  - 올스탯 +" + reward[3] + ", 공/마 +" + reward[4] + "\r\n";
            }
            v0 += "\r\n";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 템 때문에 이렇게 해야한다.
            if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 8 ||
                    getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 2) {
                self.say("#b소비, 캐시 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                if (target.exchange(2439592, -1) == 1) {
                    for (int[] reward : rewards) {
                        if (reward[3] > 0 || reward[4] > 0) {
                            exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2]);
                        } else {
                            target.exchange(reward[0], reward[1]);
                        }
                    }
                    getPlayer().gainMeso(100000000, true);
                    getPlayer().gainExp(350000000000L, true, true, true);
                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 성장 지원 상자 14
    public void consume_2439593() {
        initNPC(MapleLifeFactory.getNPC(9062475));

        int rewards[][] = new int[][] {
                // ItemID, Quantity, Period, AllStat, Attack
                { 4001832, 5000, 0, 0, 0 }, // 주문의 흔적
                { 2436078, 20, 0, 0, 0 }, // 코어 젬스톤
                { 5062010, 50, 0, 0, 0 }, // 블랙 큐브
                { 2434891, 1, 0, 0, 0 }, // 데미지스킨 선택 상자
                { 2439604, 1, 0, 0, 0 }, // 진:眞 스페셜 코디 (R)
        };

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "#";
            if (reward[2] > 0) {
                v0 += " (기간제 " + reward[2] + "일) ";
            }
            v0 += "#k " + reward[1] + "개";
            if (reward[3] > 0 || reward[4] > 0) {
                v0 += "\r\n  - 올스탯 +" + reward[3] + ", 공/마 +" + reward[4] + "\r\n";
            }
            v0 += "\r\n";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 템 때문에 이렇게 해야한다.
            if (getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 2 ||
                    getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 4 ||
                    getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 2) {
                self.say("#b소비, 기타, 캐시 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                if (target.exchange(2439593, -1) == 1) {
                    for (int[] reward : rewards) {
                        if (reward[3] > 0 || reward[4] > 0) {
                            exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2]);
                        } else {
                            target.exchange(reward[0], reward[1]);
                        }
                    }
                    getPlayer().gainMeso(200000000, true);
                    getPlayer().gainExp(400000000000L, true, true, true);
                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 성장 지원 상자 15
    public void consume_2439594() {
        initNPC(MapleLifeFactory.getNPC(9062475));

        int rewards[][] = new int[][] {
                // ItemID, Quantity, Period, AllStat, Attack
                { 2439292, 5, 0, 0, 0 }, // 미궁의 아케인심볼 상자
                { 5062503, 10, 0, 0, 0 }, // 화이트 에디셔널 큐브
                { 2436604, 2, 0, 0, 0 }, // 영원한 환생의 불꽃 복주머니
                { 2450064, 2, 0, 0, 0 }, // 경험치 2배 쿠폰
        };

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "#";
            if (reward[2] > 0) {
                v0 += " (기간제 " + reward[2] + "일) ";
            }
            v0 += "#k " + reward[1] + "개";
            if (reward[3] > 0 || reward[4] > 0) {
                v0 += "\r\n  - 올스탯 +" + reward[3] + ", 공/마 +" + reward[4] + "\r\n";
            }
            v0 += "\r\n";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 템 때문에 이렇게 해야한다.
            if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 9 ||
                    getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 2) {
                self.say("#b소비, 캐시 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                if (target.exchange(2439594, -1) == 1) {
                    for (int[] reward : rewards) {
                        if (reward[3] > 0 || reward[4] > 0) {
                            exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2]);
                        } else {
                            target.exchange(reward[0], reward[1]);
                        }
                    }
                    getPlayer().gainMeso(500000000, true);
                    getPlayer().gainExp(450000000000L, true, true, true);
                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 성장 지원 상자 16
    public void consume_2439595() {
        initNPC(MapleLifeFactory.getNPC(9062475));

        int rewards[][] = new int[][] {
                // ItemID, Quantity, Period, AllStat, Attack
                { 2434891, 1, 0, 0, 0 }, // 데미지 스킨 선택 상자 1개
                { 2439605, 1, 0, 0, 0 }, // 진:眞 스페셜 코디 상자 (S)
                { 2450064, 3, 0, 0, 0 }, // 경험치 2배 쿠폰
                { 5062010, 50, 0, 0, 0 }, // 블랙 큐브 50개
        };

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "#";
            if (reward[2] > 0) {
                v0 += " (기간제 " + reward[2] + "일) ";
            }
            v0 += "#k " + reward[1] + "개";
            if (reward[3] > 0 || reward[4] > 0) {
                v0 += "\r\n  - 올스탯 +" + reward[3] + ", 공/마 +" + reward[4] + "\r\n";
            }
            v0 += "\r\n";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 템 때문에 이렇게 해야한다.
            if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 5 ||
                    getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 2) {
                self.say("#b소비, 캐시 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                if (target.exchange(2439595, -1) == 1) {
                    for (int[] reward : rewards) {
                        if (reward[3] > 0 || reward[4] > 0) {
                            exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2]);
                        } else {
                            target.exchange(reward[0], reward[1]);
                        }
                    }
                    getPlayer().gainMeso(1000000000, true);
                    getPlayer().gainExp(500000000000L, true, true, true);
                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 성장 지원 상자 17
    public void consume_2439596() {
        initNPC(MapleLifeFactory.getNPC(9062475));

        int rewards[][] = new int[][] {
                // ItemID, Quantity, Period, AllStat, Attack
                { 5680150, 5, 0, 0, 0 }, // 3만 메이플 포인트 교환권
                { 2439241, 3, 0, 0, 0 }, // 위습의 원더베리 3개
                { 2450064, 3, 0, 0, 0 }, // 경험치 2배 쿠폰
                { 2439239, 3, 0, 0, 0 }, // 매지컬 주문서 교환권 3개
                { 2435764, 3, 0, 0, 0 }, // 골드애플 교환권
        };

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "#";
            if (reward[2] > 0) {
                v0 += " (기간제 " + reward[2] + "일) ";
            }
            v0 += "#k " + reward[1] + "개";
            if (reward[3] > 0 || reward[4] > 0) {
                v0 += "\r\n  - 올스탯 +" + reward[3] + ", 공/마 +" + reward[4] + "\r\n";
            }
            v0 += "\r\n";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 템 때문에 이렇게 해야한다.
            if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 9 ||
                    getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 2) {
                self.say("#b소비, 캐시 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                if (target.exchange(2439596, -1) == 1) {
                    for (int[] reward : rewards) {
                        if (reward[3] > 0 || reward[4] > 0) {
                            exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2]);
                        } else {
                            target.exchange(reward[0], reward[1]);
                        }
                    }
                    getPlayer().gainMeso(1000000000, true);
                    getPlayer().gainExp(500000000000L, true, true, true);
                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 성장 지원 상자 18
    public void consume_2439597() {
        initNPC(MapleLifeFactory.getNPC(9062475));

        int rewards[][] = new int[][] {
                // ItemID, Quantity, Period, AllStat, Attack
                { 3014028, 1, 0, 0, 0 }, // 찬란한 명예의 상징
                { 2049360, 3, 0, 0, 0 }, // 놀라운 장비강화 주문서
                { 2439605, 1, 0, 0, 0 }, // 진:眞 스페셜 코디 상자 (S)
                { 2439241, 5, 0, 0, 0 }, // 위습의 원더베리 교환권
                { 2435764, 5, 0, 0, 0 }, // 골드애플 교환권
        };

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "#";
            if (reward[2] > 0) {
                v0 += " (기간제 " + reward[2] + "일) ";
            }
            v0 += "#k " + reward[1] + "개";
            if (reward[3] > 0 || reward[4] > 0) {
                v0 += "\r\n  - 올스탯 +" + reward[3] + ", 공/마 +" + reward[4] + "\r\n";
            }
            v0 += "\r\n";
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            // 본래 exchange로 하면 되나, 기간제 템 때문에 이렇게 해야한다.
            if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 13 ||
                    getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 1) {
                self.say("#b소비, 설치 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                if (target.exchange(2439597, -1) == 1) {
                    for (int[] reward : rewards) {
                        if (reward[3] > 0 || reward[4] > 0) {
                            exchangeSupportEquipPeriod(reward[0], reward[3], reward[4], reward[2]);
                        } else {
                            target.exchange(reward[0], reward[1]);
                        }
                    }
                    getPlayer().gainMeso(5000000000L, true);
                    getPlayer().gainExp(1000000000000L, true, true, true);
                    self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
                }
            }
        }
    }

    // 진:眞 블랙 장비 상자
    public void consume_2433509() {
        int[] rewards = new int[] {
                1212116, 1213023, 1214023, 1222110, 1232110, 1242117, 1262047, 1272031, 1282036, 1292023, 1302334,
                1312200, 1322251, 1332275, 1362136, 1372223, 1382260, 1402252, 1412178, 1422185, 1432215, 1442269,
                1452253, 1462240, 1472262, 1482217, 1492232, 1522139, 1532145, 1582041, 1592030
        };

        initNPC(MapleLifeFactory.getNPC(9062474));
        String v0 = "다음과 같은 #블랙 무기 중 1개#k를 선택하여 획득할 수 있어.\r\n선택한 무기는 #e14일간#n 사용 가능하고 #b올스탯 +200, 공/마 +200, 추가옵션#k이 적용되어 지급돼.\r\n\r\n원하는 무기를 골라봐.#b\r\n\r\n";
        for (int i = 0; i < rewards.length; ++i) {
            int itemID = rewards[i];
            v0 += "#L" + i + "##i" + itemID + "# #z" + itemID + "##l\r\n";
        }

        int v1 = self.askMenu(v0, ScriptMessageFlag.NpcReplacedByNpc);
        if (v1 >= rewards.length) {
            return; // TODO: Hack
        }
        int itemID = rewards[v1];
        if (self.askYesNo(
                "선택한 무기는 #b#i" + itemID + "# #z" + itemID
                        + "##k(이)야.\r\n정말 이 무기로 선택할거니?\r\n\r\n#b(#e예#n를 누르면 아이템을 획득합니다.)",
                ScriptMessageFlag.NpcReplacedByNpc) > 0) {
            if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
                self.say("#b장비 인벤토리#k 공간을 확보하고 다시 시도해주겠니?");
            } else {
                if (target.exchange(2433509, -1) == 1) {
                    exchangeSupportEquipBonusStatPeriod(itemID, 200, 200, 14);
                    self.say("지급이 완료되었어. 인벤토리를 확인해봐!");
                }
            }
        }
    }

    // 진:眞 레전드리 아케인셰이드 무기 상자
    public void consume_2439609() {
        int[] rewards = new int[] {
                1212131, 1213030, 1214030, 1222124, 1232124, 1242144, 1242145, 1262053, 1272043, 1282043, 1292030,
                1302359, 1312215, 1322266,
                1332291, 1362151, 1372239, 1382276, 1402271, 1412191, 1422199, 1432229, 1442287, 1452269, 1462254,
                1472277, 1482234, 1492247, 1522154, 1532159, 1582046, 1592037
        };

        initNPC(MapleLifeFactory.getNPC(9062474));
        String v0 = "다음과 같은 #아케인셰이드 무기 중 1개#k를 선택하여 획득할 수 있어.\r\n선택한 무기는 #b15성과 레전드리 잠재능력 옵션, 추가옵션#k이 적용되어 지급돼.\r\n\r\n원하는 무기를 골라봐.#b\r\n\r\n";
        for (int i = 0; i < rewards.length; ++i) {
            int itemID = rewards[i];
            v0 += "#L" + i + "##i" + itemID + "# #z" + itemID + "##l\r\n";
        }

        int v1 = self.askMenu(v0, ScriptMessageFlag.NpcReplacedByNpc);
        if (v1 >= rewards.length) {
            return; // TODO: Hack
        }
        int itemID = rewards[v1];
        if (self.askYesNo(
                "선택한 무기는 #b#i" + itemID + "# #z" + itemID
                        + "##k(이)야.\r\n정말 이 무기로 선택할거니?\r\n\r\n#b(#e예#n를 누르면 아이템을 획득합니다.)",
                ScriptMessageFlag.NpcReplacedByNpc) > 0) {
            if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
                self.say("#b장비 인벤토리#k 공간을 확보하고 다시 시도해주겠니?");
            } else {
                if (target.exchange(2439609, -1) == 1) {
                    exchangeEquipCHUCWithScroll(itemID, 15, 2);
                    self.say("지급이 완료되었어. 인벤토리를 확인해봐!");
                }
            }
        }
    }

    // 진:眞 유니크 앱솔랩스 무기 상자
    public void consume_2439610() {
        int[] rewards = new int[] {
                1212121, 1213028, 1214028, 1222114, 1232114, 1242123, 1242124, 1262040, 1272021, 1282022,
                1292028, 1302344, 1312204, 1322256, 1332280, 1342105, 1362141, 1372229, 1382266, 1402260, 1412182,
                1422190, 1432219,
                1442276, 1452258, 1462244, 1472266, 1482222, 1492236, 1522144, 1532148, 1582027, 1592028
        };

        initNPC(MapleLifeFactory.getNPC(9062474));
        String v0 = "다음과 같은 #아케인셰이드 무기 중 1개#k를 선택하여 획득할 수 있어.\r\n선택한 무기는 #b15성과 레전드리 잠재능력 옵션, 추가옵션#k이 적용되어 지급돼.\r\n\r\n원하는 무기를 골라봐.#b\r\n\r\n";
        for (int i = 0; i < rewards.length; ++i) {
            int itemID = rewards[i];
            v0 += "#L" + i + "##i" + itemID + "# #z" + itemID + "##l\r\n";
        }

        int v1 = self.askMenu(v0, ScriptMessageFlag.NpcReplacedByNpc);
        if (v1 >= rewards.length) {
            return; // TODO: Hack
        }
        int itemID = rewards[v1];
        if (self.askYesNo(
                "선택한 무기는 #b#i" + itemID + "# #z" + itemID
                        + "##k(이)야.\r\n정말 이 무기로 선택할거니?\r\n\r\n#b(#e예#n를 누르면 아이템을 획득합니다.)",
                ScriptMessageFlag.NpcReplacedByNpc) > 0) {
            if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
                self.say("#b장비 인벤토리#k 공간을 확보하고 다시 시도해주겠니?");
            } else {
                if (target.exchange(2439610, -1) == 1) {
                    exchangeEquipCHUCWithScroll(itemID, 15, 2);
                    self.say("지급이 완료되었어. 인벤토리를 확인해봐!");
                }
            }
        }
    }

    // Good Bye Extreme
    public void consume_2439611() {
        initNPC(MapleLifeFactory.getNPC(9062000));
        int[][] rewards = new int[][] {
                { 2630512, 100 }, // 선택 아케인심볼 교환권 100개
                { 2439605, 3 }, // 진:眞 스페셜 코디 상자 (S)
                { 2439660, 5 }, // 태풍 성장의 비약 5개
                { 5060048, 2 }, // 골드애플 2개
                { 5068300, 2 }, // 위습의 원더베리 2개
                { 5062010, 50 }, // 블랙 큐브 50개
                { 2436078, 100 }, // 코어 젬스톤 100개
                { 2439611, -1 }, // 상자 소비
        };

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            if (reward[1] != -1) {
                v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "##k " + reward[1] + "개\r\n";
            }
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            if (target.exchange(rewards) == 1) {
                getPlayer().gainMeso(1000000000, true);
                self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                self.say("#b소비 인벤토리#k와 #b캐시 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            }
        }
    }

    // 여제 클리어횟수 초기화 티켓
    public void consume_2431968() {
        bossResetCoupon(QuestExConstants.Cygnus.getQuestID(), "cygnus_clear", 2431968);
    }

    // 하드매그너스 클리어횟수 초기화 티켓
    public void consume_2431969() {
        bossResetCoupon(QuestExConstants.HardMagnus.getQuestID(), "hard_magnus_clear", 2431969);
    }

    // 카오스반반 클리어횟수 초기화 티켓
    public void consume_2431970() {
        bossResetCoupon(QuestExConstants.ChaosVonBon.getQuestID(), "chaos_banban_clear", 2431970);
    }

    // 카오스피에르 클리어횟수 초기화 티켓
    public void consume_2431971() {
        bossResetCoupon(QuestExConstants.ChaosPierre.getQuestID(), "chaos_pierre_clear", 2431971);
    }

    // 카오스블러디퀸 클리어횟수 초기화 티켓
    public void consume_2431972() {
        bossResetCoupon(QuestExConstants.ChaosCrimsonQueen.getQuestID(), "chaos_b_queen_clear", 2431972);
    }

    // 카오스벨룸 클리어횟수 초기화 티켓
    public void consume_2431973() {
        bossResetCoupon(QuestExConstants.ChaosVellum.getQuestID(), "chaos_velum_clear", 2431973);
    }

    public void bossResetCoupon(int questID, String key, int itemID) {
        initNPC(MapleLifeFactory.getNPC(2007));
        MapleCharacter user = getPlayer();
        if (user.getOneInfo(questID, "eNum") != null) {
            if (1 == self.askYesNo("#e초기화 티켓을 사용하시겠습니까?")) {
                if (DBConfig.isGanglim) {
                    user.dropMessage(5, user.getOneInfoQuestInteger(1234569, "use_" + itemID) + "");
                }
                int resetCan = 2;
                if (!DBConfig.isGanglim) {
                    resetCan = 8;
                }
                if (DBConfig.isGanglim) {
                    if (resetCan <= user.getOneInfoQuestInteger(1234569, "use_" + itemID)) {
                        self.sayOk("금주에 더 이상 해당 보스 입장 기록을 초기화할 수 없습니다.");
                        return;
                    }
                    if (user.getOneInfo(1234569, key) != null) {
                        if (user.getOneInfo(1234569, key).equals("1")) {
                            if (target.exchange(itemID, -1) == 1) {
                                user.updateOneInfo(1234569, key, "0");
                                user.updateOneInfo(1234569, "use_" + itemID,
                                        String.valueOf(user.getOneInfoQuestInteger(1234569, "use_" + itemID) + 1));
                                user.updateOneInfo(questID, "eNum", "0");
                                user.updateOneInfo(questID, "lastDate", "2000/01/01/01/01/01");
                                self.sayOk("성공적으로 초기화 되었습니다.");
                            } else {
                                self.sayOk("알 수 없는 오류로 사용에 실패하였습니다.");
                            }
                        } else {
                            self.sayOk("이번 주에 클리어하신 기록이 확인되지 않아 정상 처리되지 않았습니다.");
                        }
                    } else {
                        self.sayOk("이번 주에 클리어하신 기록이 확인되지 않아 정상 처리되지 않았습니다.");
                    }
                } else { // 진
                    int nSingleClear = user.getOneInfoQuestInteger(questID, "eNum_single");
                    int nMultiClear = user.getOneInfoQuestInteger(questID, "eNum_multi");
                    user.dropMessage(5, "싱글사용 : " + user.getOneInfoQuestInteger(1234569, "use_" + itemID + "_single")
                            + " / 멀티사용 : " + user.getOneInfoQuestInteger(1234569, "use_" + itemID + "_multi"));
                    if (resetCan <= user.getOneInfoQuestInteger(1234569, "use_" + itemID + "_single")
                            && resetCan <= user.getOneInfoQuestInteger(1234569, "use_" + itemID + "_multi")) {
                        self.sayOk("금주에 더 이상 해당 보스 입장 기록을 초기화할 수 없습니다.");
                        return;
                    }
                    if (nSingleClear == 0 && nMultiClear == 0) {
                        self.sayOk("해당 보스는 이번 주에 클리어하신 기록이 확인되지 않아 정상 처리되지 않았습니다.");
                        return;
                    }
                    if (nSingleClear < 0 || nMultiClear < 0) {
                        self.sayOk("초기화 기록에 오류가 발생해 정상 처리 되지 않았습니다.");
                        return;
                    }
                    String askClear = "입장횟수를 감소할 모드를 선택해주세요.";
                    if (nSingleClear > 0) {
                        askClear += "\r\n#L0#싱글모드(" + nSingleClear + "회 클리어)#l";
                    }
                    if (nMultiClear > 0) {
                        askClear += "\r\n#L1#멀티모드(" + nMultiClear + "회 클리어)#l";
                    }
                    int clearSel = self.askMenu(askClear);
                    if (clearSel != 0 && clearSel != 1)
                        return;
                    if (clearSel == 0 && (nSingleClear <= 0
                            || resetCan <= user.getOneInfoQuestInteger(1234569, "use_" + itemID + "_single"))) {
                        self.sayOk("금주에 더 이상 해당 보스 입장 기록을 초기화할 수 없습니다.");
                        return;
                    }
                    if (clearSel == 1 && (nMultiClear <= 0
                            || resetCan <= user.getOneInfoQuestInteger(1234569, "use_" + itemID + "_multi"))) {
                        self.sayOk("금주에 더 이상 해당 보스 입장 기록을 초기화할 수 없습니다.");
                        return;
                    }
                    if (target.exchange(itemID, -1) == 1) {
                        user.updateOneInfo(1234569, key, "0");
                        user.updateOneInfo(questID, "eNum", "0");
                        if (clearSel == 0) {
                            user.updateOneInfo(1234569, "use_" + itemID + "_single", String
                                    .valueOf(user.getOneInfoQuestInteger(1234569, "use_" + itemID + "_single") + 1));
                            user.updateOneInfo(questID, "eNum_single", String.valueOf(nSingleClear - 1));
                            user.updateOneInfo(questID, "lastDate", "2000/01/01/01/01/01");
                            self.sayOk("성공적으로 싱글 횟수가 1회 차감 되었습니다.");
                        } else { // clearSel == 1
                            user.updateOneInfo(1234569, "use_" + itemID + "_multi", String
                                    .valueOf(user.getOneInfoQuestInteger(1234569, "use_" + itemID + "_multi") + 1));
                            user.updateOneInfo(questID, "eNum_multi", String.valueOf(nMultiClear - 1));
                            user.updateOneInfo(questID, "lastDate", "2000/01/01/01/01/01");
                            self.sayOk("성공적으로 멀티 횟수가 1회 차감 되었습니다.");
                        }
                    } else {
                        self.sayOk("알 수 없는 오류로 사용에 실패하였습니다.");
                    }
                }
            }
        } else {
            self.sayOk("이번 주에 클리어하신 기록이 확인되지 않아 정상 처리되지 않았습니다.");
        }
    }

    // 진:眞 뉴비 지원 상자
    public void consume_2437121() {
        initNPC(MapleLifeFactory.getNPC(9062474));
        if (getPlayer().getOneInfoQuestInteger(1234567, "use_newbie_support") == 1) {
            self.say("이미 보상을 받았던 것 같은데?", ScriptMessageFlag.NpcReplacedByUser);

            if (target.exchange(2437121, -1) == 1) {
            }
            return;
        }
        int[][] rewards = new int[][] {
                { 2049361, 3 },
                { 5062009, 100 },
                { 5068300, 1 },
                { 5060048, 1 },
                { 5680150, 1 },
                { 2435890, 1 },
                { 2437121, -1 }, // 상자 소비
        };
        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        // v0 += "#b#i5000931# #z5000931# (기간제 30일)#k 1개\r\n";
        for (int[] reward : rewards) {
            if (reward[1] != -1) {
                v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "##k " + reward[1] + "개\r\n";
            }
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            if (getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 7) {
                self.say("#b캐시 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
                return;
            }
            if (target.exchange(rewards) == 1) {
                getPlayer().updateOneInfo(1234567, "use_newbie_support", "1");
                // exchangePetPeriod(5000931, 30);
                self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                self.say("#b캐시, 소비 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            }
        }
    }

    public void consume_2437122() {
        initNPC(MapleLifeFactory.getNPC(9062474));
        int[][] rewards = new int[][] {
                { 2049360, 1 },
                { 2435890, 1 },
                { 2436078, 50 },
                { 2439292, 20 },
                { 2437122, -1 }, // 상자 소비
        };
        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        v0 += "#b#i5060048# #z5060048# (20% 확률로 획득)#k 1개\r\n";
        v0 += "#b#i5068300# #z5068300# (20% 확률로 획득)#k 1개\r\n";
        for (int[] reward : rewards) {
            if (reward[1] != -1) {
                v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "##k " + reward[1] + "개\r\n";
            }
        }
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {
            if (getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 2) {
                self.say("#b캐시 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
                return;
            }
            if (target.exchange(rewards) == 1) {
                if (Randomizer.isSuccess(20)) {
                    getPlayer().dropMessage(5, "골드애플 1개를 획득했습니다.");
                    target.exchange(5060048, 1);
                }
                if (Randomizer.isSuccess(20)) {
                    getPlayer().dropMessage(5, "위습의 원더베리 1개를 획득했습니다.");
                    target.exchange(5068300, 1);
                }
                self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                self.say("#b캐시, 소비 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            }
        }
    }

    // 랜덤 성형 성장의 비약
    public void consume_2430909() {
        randomTraitSecretPotion(2430909);
    }

    // PC방 랜덤 성형 성장의 비약
    public void consume_2436786() {
        randomTraitSecretPotion(2436786);
    }

    // 월드 내 교가 성성비
    public void consume_2433604() {
        traitSecretPotion(2433604);
    }

    // 월드 내 교가 성성비
    public void consume_2633242() {
        traitSecretPotion(2633242);
    }

    // 교불 성성비
    public void consume_2434921() {
        traitSecretPotion(2434921);
    }

    // 교불 성성비
    public void consume_2439429() {
        traitSecretPotion(2439429);
    }

    // 교환 가능한 성성비
    public void consume_2436595() {
        traitSecretPotion(2436595);
    }

    // 성향 성장의 물약(성향 성장의 비약보다 경험치 적게줌)
    public void consume_2438644() {
        traitSecretPotion(2438644);
    }

    public void randomTraitSecretPotion(int itemId) {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (self.askYesNo("랜덤 성향의 비약을 사용 하시겠습니까?", ScriptMessageFlag.NpcReplacedByNpc) == 1) {
            MapleTrait.MapleTraitType traitType = null;
            MapleTrait.MapleTraitType[] t = { MapleTrait.MapleTraitType.charisma,
                    MapleTrait.MapleTraitType.sense,
                    MapleTrait.MapleTraitType.insight,
                    MapleTrait.MapleTraitType.will,
                    MapleTrait.MapleTraitType.craft,
                    MapleTrait.MapleTraitType.charm };
            boolean canUse = false;
            for (MapleTrait.MapleTraitType tT : t) {
                if (getPlayer().getTrait(tT).getLevel() < 100) {
                    canUse = true;
                    break;
                }
            }
            if (!canUse) {
                self.sayOk("이미 모든 성향의 레벨이 최대치입니다.");
                return;
            }
            while (traitType == null) {
                int r = Randomizer.nextInt(t.length);
                if (getPlayer().getTrait(t[r]).getLevel() < 100) {
                    traitType = t[r];
                }
            }
            if (target.exchange(itemId, -1) > 0) {
                getPlayer().getTrait(traitType).addTrueExp(11040, getPlayer());
                getPlayer().dropMessage(5, traitType.getName() + Locales.getKoreanJosa(traitType.getName(), JosaType.이가)
                        + " 눈에 띄게 성장 하였습니다.");
            }
        }
    }

    public void traitSecretPotion(int itemId) {
        initNPC(MapleLifeFactory.getNPC(9010000));
        StringBuilder s = new StringBuilder();
        s.append("#L0#카리스마   #k방어율 무시 증가 / 사망 패널티 지속시간 감소#l\r\n")
                .append("#b#L1#감성   #kMP 최대치 / 버프 지속시간 증가#l\r\n")
                .append("#b#L2#통찰력   #k속성 내성 무시 / 감정 능력 단계 증가#l\r\n")
                .append("#b#L3#의지   #kHP 최대치 / 방어력 / 상태 이상 내성 증가#l\r\n")
                .append("#b#L4#손재주   #k주문서 성공 확률 / 숙련도 2배 획득 확률 증가#l\r\n")
                .append("#b#L5#매력   #k포켓 슬롯 해방 가능 / 표정 추가 습득 가능#l\r\n");
        int v0 = self.askMenu("성장 시키고 싶은 성향을 선택해 주세요!#b\r\n" + s.toString(), ScriptMessageFlag.NpcReplacedByNpc);

        MapleTrait.MapleTraitType traitType = null;
        switch (v0) {
            case 0:
                traitType = MapleTrait.MapleTraitType.charisma;
                break;
            case 1:
                traitType = MapleTrait.MapleTraitType.sense;
                break;
            case 2:
                traitType = MapleTrait.MapleTraitType.insight;
                break;
            case 3:
                traitType = MapleTrait.MapleTraitType.will;
                break;
            case 4:
                traitType = MapleTrait.MapleTraitType.craft;
                break;
            case 5:
                traitType = MapleTrait.MapleTraitType.charm;
                break;
        }
        if (traitType != null) {
            int level = getPlayer().getTrait(traitType).getLevel();
            if (level < 100) {
                if (target.exchange(itemId, -1) > 0) {
                    int traitEXP = 11040;
                    if (itemId == 2438644) {
                        traitEXP = 3680;
                    }
                    getPlayer().getTrait(traitType).addTrueExp(traitEXP, getPlayer());
                    getPlayer().dropMessage(5, traitType.getName()
                            + Locales.getKoreanJosa(traitType.getName(), JosaType.이가) + " 눈에 띄게 성장 하였습니다.");
                }
            } else {
                self.sayOk("선택하신 성향은 이미 최대치 입니다.", ScriptMessageFlag.NpcReplacedByNpc);
            }
        }
    }

    public void consume_2633201() {
        int menu = self.askMenu(
                "받고 싶은 #r#e아이템 성별을 선택#k#n해주세요.\r\n성별이 다른 캐릭터는 입을 수 없습니다.\r\n#b#L0#츄츄 아일랜드 세트(남)#l\r\n#L1#츄츄 아일랜드 세트(여)#l");
        String cGender = "";
        int[] items = new int[5];
        switch (menu) {
            case 0: // 츄츄아일랜드세트(남)
                cGender = "츄츄 아일랜드 세트(남)";
                items = new int[] { 1005781, 1050583, 1103332, 1073534, 1703084 };
                break;
            case 1: // 츄츄아일랜드세트(여)
                cGender = "츄츄 아일랜드 세트(여)";
                items = new int[] { 1005781, 1051656, 1103332, 1073534, 1703084 };
                break;
        }
        String itemString = "";
        for (int i : items) {
            if (GameConstants.isCap(i)) {
                itemString += "[모자] #i" + i + "# #z" + i + "#\r\n";
            }
            if (GameConstants.isOverall(i)) {
                itemString += "[한벌옷] #i" + i + "# #z" + i + "#\r\n";
            }
            if (GameConstants.isCape(i)) {
                itemString += "[망토] #i" + i + "# #z" + i + "#\r\n";
            }
            if (GameConstants.isShoes(i)) {
                itemString += "[신발] #i" + i + "# #z" + i + "#\r\n";
            }
            if (GameConstants.isWeapon(i)) {
                itemString += "[무기] #i" + i + "# #z" + i + "#";
            }
        }
        if (1 == self.askYesNo(
                "선택하신 의상을 다시 한 번 확인해주세요.\r\n#b- 선택 성별: " + cGender + "\r\n- 수령 캐릭터: #h0##k\r\n\r\n" + itemString)) {
            if (getPlayer().getInventory(MapleInventoryType.CASH_EQUIP).getNumFreeSlot() >= 5) {
                if (target.exchange(2633201, -1) > 0) {
                    MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    for (int i : items) {
                        Item rewardItem = ii.getEquipById(i);
                        Equip rewardEquip = null;
                        if (rewardItem != null) {
                            rewardEquip = (Equip) rewardItem;
                        }
                        if (rewardEquip != null) {
                            rewardEquip.setUniqueId(MapleInventoryIdentifier.getInstance());
                            rewardEquip.setExpiration(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 180L);
                            if (GameConstants.isCap(i) || GameConstants.isCape(i)) {
                                rewardEquip.setStr((short) 30);
                                rewardEquip.setDex((short) 30);
                                rewardEquip.setInt((short) 30);
                                rewardEquip.setLuk((short) 30);
                            }
                            if (GameConstants.isOverall(i)) {
                                rewardEquip.setWdef((short) 300);
                            }
                            if (GameConstants.isShoes(i)) {
                                rewardEquip.setSpeed((short) 50);
                                rewardEquip.setJump((short) 50);
                            }
                            if (GameConstants.isWeapon(i)) {
                                rewardEquip.setWatk((short) 30);
                                rewardEquip.setMatk((short) 30);
                            }
                            rewardEquip.setFlag((short) 32);
                            short TI = MapleInventoryManipulator.addbyItem(getClient(), rewardEquip, false);
                        }
                    }
                }
            } else {
                self.say("치장 슬롯을 5칸 이상 비워주세요.");
            }
        }
    }

    public void consume_2632860() {
        if (getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() >= 1) {
            if (target.exchange(2632860, -1) > 0) {
                if (!DBConfig.isGanglim) {
                    exchangePetPeriod(5002081, 60);
                } else {
                    exchangePetPeriod(5002197, 90);
                }
                self.sayOk("교환이 완료되었습니다.");
            }
        } else {
            self.say("캐시 슬롯을 1칸 이상 비워주세요.");
        }
    }

    public void consume_2630782() {
        int[] rewards = new int[] {
                1212131, 1213030, 1214030, 1222124, 1232124, 1242144, 1242145, 1262053, 1272043, 1282043, 1292030,
                1302359, 1312215, 1322266,
                1332291, 1362151, 1372239, 1382276, 1402271, 1412191, 1422199, 1432229, 1442287, 1452269, 1462254,
                1472277, 1482234, 1492247, 1522154, 1532159, 1582046, 1592037
        };
        String v0 = "다음과 같은 #아케인셰이드 무기 중 1개#k를 선택하여 획득할 수 있어.\r\n선택한 무기는 #b15성과 레전드리 잠재능력 옵션, 추가옵션#k이 적용되어 지급돼.\r\n\r\n원하는 무기를 골라봐.#b\r\n\r\n";
        for (int i = 0; i < rewards.length; ++i) {
            int itemID = rewards[i];
            v0 += "#L" + i + "##i" + itemID + "# #z" + itemID + "##l\r\n";
        }

        int v1 = self.askMenu(v0, ScriptMessageFlag.NpcReplacedByNpc);
        if (v1 >= rewards.length) {
            return; // TODO: Hack
        }
        int itemID = rewards[v1];
        if (self.askYesNo(
                "선택한 무기는 #b#i" + itemID + "# #z" + itemID
                        + "##k(이)야.\r\n정말 이 무기로 선택할거니?\r\n\r\n#b(#e예#n를 누르면 아이템을 획득합니다.)",
                ScriptMessageFlag.NpcReplacedByNpc) > 0) {
            if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
                self.say("#b장비 인벤토리#k 공간을 확보하고 다시 시도해주겠니?");
            } else {
                if (target.exchange(2630782, -1) == 1) {
                    exchangeEquipCHUCWithScroll(itemID, 15, 2);
                    self.say("지급이 완료되었어. 인벤토리를 확인해봐!");
                }
            }
        }
    }

    public void consume_2632861() {
        int[] rewards = new int[] {
                1004808, 1004809, 1004810, 1004811, 1004812,
                1102940, 1102941, 1102942, 1102943, 1102944,
                1082695, 1082696, 1082697, 1082698, 1082699,
                1053063, 1053064, 1053065, 1053066, 1053067,
                1073158, 1073159, 1073160, 1073161, 1073162,
                1152196, 1152197, 1152198, 1152199, 1152200
        };
        String v0 = "다음과 같은 #b아케인셰이드 방어구 중 1개#k를 선택하여 획득할 수 있습니다.\r\n원하는 방어구를 골라보시기 바랍니다.#b\r\n\r\n";
        for (int i = 0; i < rewards.length; ++i) {
            int itemID = rewards[i];
            v0 += "#L" + i + "##i" + itemID + "# #z" + itemID + "##l\r\n";
        }

        int v1 = self.askMenu(v0, ScriptMessageFlag.NpcReplacedByNpc);
        if (v1 >= rewards.length) {
            return; // TODO: Hack
        }
        int itemID = rewards[v1];
        if (self.askYesNo(
                "선택한 방어구는 #b#i" + itemID + "# #z" + itemID
                        + "##k 입니다.\r\n정말 이 방어구로 선택하시겠습니까?\r\n\r\n#b(#e예#n를 누르면 아이템을 획득합니다.)",
                ScriptMessageFlag.NpcReplacedByNpc) > 0) {
            if (target.exchange(2632861, -1, itemID, 1) == 1) {
                self.say("지급이 완료되었습니다. 인벤토리를 확인해보시기 바랍니다!");
            } else {
                self.say("#b장비 인벤토리#k 공간을 확보하고 다시 시도해주시기 바랍니다.");
            }
        }
    }

    public void consume_2630133() {
        int[] rewards = new int[] {
                1212120, 1213018, 1214018, 1222113, 1232113, 1242121, 1242122, 1262039, 1272017, 1282017,
                1292018, 1302343, 1312203, 1322255, 1332279, 1342104, 1362140, 1372228, 1382265, 1402259,
                1412181, 1422189, 1432218, 1442274, 1452257, 1462243, 1472265, 1482221, 1492235, 1522143, 1532150,
                1582023, 4310217
        };
        String v0 = "다음과 같은 #b아케인셰이드 무기 중 1개#k를 랜덤으로 획득할 수 있습니다.\r\n지금 바로 상자를 열어보겠어요?#b\r\n\r\n";
        for (int i = 0; i < rewards.length; ++i) {
            int itemID = rewards[i];
            v0 += "#i" + itemID + "# #z" + itemID + "##l\r\n";
        }

        if (self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByNpc) > 0) {
            int v1 = Randomizer.rand(0, rewards.length - 1);
            if (v1 >= rewards.length) {
                return; // TODO: Hack
            }
            int itemID = rewards[v1];
            if (target.exchange(2630133, -1, itemID, 1) == 1) {
                self.say("상자에서 #b#i" + itemID + "# #z" + itemID + "##k을(를) 획득했습니다. 인벤토리를 확인해보시기 바랍니다.");
            } else {
                self.say("#b장비 인벤토리#k 공간을 확보하고 다시 시도해주시기 바랍니다.");
            }
        }
    }

    public void consume_2630291() {
        int[] rewards = new int[] {
                1212115, 1213017, 1214017, 1222109, 1232109, 1242116, 1242123, 1262017, 1272016, 1282016, 1292017,
                1302333, 1312199, 1322250, 1332274, 1342101, 1362135, 1372222, 1382259, 1402251, 1412177, 1422184,
                1432214, 1442268, 1452252, 1462239, 1472261, 1482216, 1492231, 1522138, 1532144, 1582017, 1592019,
                4310216
        };
        String v0 = "다음과 같은 #b앱솔랩스 무기 중 1개#k를 선택하여 획득할 수 있습니다.\r\n원하는 무기를 골라보시기 바랍니다.#b\r\n\r\n";
        for (int i = 0; i < rewards.length; ++i) {
            int itemID = rewards[i];
            v0 += "#L" + i + "##i" + itemID + "# #z" + itemID + "##l\r\n";
        }

        int v1 = self.askMenu(v0, ScriptMessageFlag.NpcReplacedByNpc);
        if (v1 >= rewards.length) {
            return; // TODO: Hack
        }
        int itemID = rewards[v1];
        if (self.askYesNo(
                "선택한 무기는 #b#i" + itemID + "# #z" + itemID
                        + "##k 입니다.\r\n정말 이 무기로 선택하시겠습니까?\r\n\r\n#b(#e예#n를 누르면 아이템을 획득합니다.)",
                ScriptMessageFlag.NpcReplacedByNpc) > 0) {
            if (target.exchange(2630291, -1, itemID, 1) == 1) {
                self.say("지급이 완료되었습니다. 인벤토리를 확인해보시기 바랍니다!");
            } else {
                self.say("#b장비 인벤토리#k 공간을 확보하고 다시 시도해주시기 바랍니다.");
            }
        }
    }

    public void consume_2630704() {
        int[] rewards = new int[] {
                1004422, 1004423, 1004424, 1004425, 1004426,
                1102775, 1102794, 1102795, 1102796, 1102797,
                1082636, 1082637, 1082638, 1082639, 1082640,
                1052882, 1052887, 1052888, 1052889, 1052890,
                1073030, 1073032, 1073033, 1073034, 1073035,
                1152174, 1152176, 1152177, 1152178, 1152179
        };
        String v0 = "다음과 같은 #b앱솔랩스 방어구 중 1개#k를 선택하여 획득할 수 있습니다.\r\n원하는 방어구를 골라보시기 바랍니다.#b\r\n\r\n";
        for (int i = 0; i < rewards.length; ++i) {
            int itemID = rewards[i];
            v0 += "#L" + i + "##i" + itemID + "# #z" + itemID + "##l\r\n";
        }

        int v1 = self.askMenu(v0, ScriptMessageFlag.NpcReplacedByNpc);
        if (v1 >= rewards.length) {
            return; // TODO: Hack
        }
        int itemID = rewards[v1];
        if (self.askYesNo(
                "선택한 방어구는 #b#i" + itemID + "# #z" + itemID
                        + "##k 입니다.\r\n정말 이 방어구로 선택하시겠습니까?\r\n\r\n#b(#e예#n를 누르면 아이템을 획득합니다.)",
                ScriptMessageFlag.NpcReplacedByNpc) > 0) {
            if (target.exchange(2630704, -1, itemID, 1) == 1) {
                self.say("지급이 완료되었습니다. 인벤토리를 확인해보시기 바랍니다!");
            } else {
                self.say("#b장비 인벤토리#k 공간을 확보하고 다시 시도해주시기 바랍니다.");
            }
        }
    }

    public void consume_2632789() { // 이터널 플레임 링 교환권
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
            self.sayOk("장비 아이템 슬롯이 부족합니다.");
            return;
        }
        if (target.exchange(2632789, -1) > 0) {
            exchangeUniqueItem(1114324);
            self.sayOk("유니크 이터널 플레임 링이 지급되었습니다.");
        }
    }

    public void consume_2435484() { // 쿠폰
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (target.exchange(2435484, -1) > 0) {
            int addPoint = 10000;
            int point = getPlayer().getEnchantPoint();
            getPlayer().setEnchantPoint(point + addPoint);
            self.sayOk(
                    "초월 강화 포인트 #e#r" + addPoint + "#n#k포인트가 지급되었습니다.\r\n포인트 : " + point + " → " + (point + addPoint));
        }
    }

    public void consume_2437090() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() >= 1) {
            if (target.exchange(2437090, -1) > 0) {
                exchangeSupportEquipPeriod(1122017, 0, 0, 14);
                self.sayOk("교환이 완료되었습니다.");
            }
        } else {
            self.sayOk("장비아이템 슬롯이 부족합니다.");
        }
    }

    public void consume_2633590() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (getPlayer().getLevel() < 200) {
            self.sayOk("200레벨 이상만 사용가능합니다.");
            return;
        }
        List<Item> symbols = new ArrayList<>();
        for (Item item : getPlayer().getInventory(MapleInventoryType.EQUIPPED).list()) {
            if (GameConstants.isArcaneSymbol(item.getItemId())) {
                symbols.add(item);
            }
        }
        if (symbols.isEmpty()) {
            self.sayOk("장착중인 아케인 심볼이 없습니다.");
            return;
        }
        Equip selected = null;
        String string = "안녕하세요.\r\n\r\n저는 아케인심볼 퀵패스 담당 엔피시입니다.\r\n최대 레벨로 올릴 아케인 심볼을 선택해주세요!\r\n\r\n #r※ 주의사항 ※ \r\n선택한 심볼의 장착중인 심볼의 레벨이 최대치로 올라갑니다.#k#b";

        for (int i = 0; i < symbols.size(); i++) {
            string += "\r\n#L" + i + "# #i" + symbols.get(i).getItemId() + ":# #z" + symbols.get(i).getItemId()
                    + ":# #l";
        }
        int v = self.askMenu(string);
        if (v >= 0) {
            selected = (Equip) symbols.get(v);
        }
        if (selected != null) {
            if (selected.getArcLevel() >= 20) {
                self.sayOk("선택한 심볼의 레벨이 이미 최대치입니다.");
                return;
            }
            // updateArcaneSymbol
            if (target.exchange(2633590, -1) > 0) {
                int level = selected.getArcLevel();
                int up = 20 - level;
                selected.setArcEXP(0);
                selected.setArcLevel(20);
                selected.setArc((short) (10 * (selected.getArcLevel() + 2)));
                if (getPlayer().getJob() >= 100 && getPlayer().getJob() < 200 || getPlayer().getJob() == 512
                        || getPlayer().getJob() == 1512 || getPlayer().getJob() == 2512
                        || getPlayer().getJob() >= 1100 && getPlayer().getJob() < 1200
                        || GameConstants.isAran(getPlayer().getJob()) || GameConstants.isBlaster(getPlayer().getJob())
                        || GameConstants.isDemonSlayer(getPlayer().getJob())
                        || GameConstants.isMichael(getPlayer().getJob()) || GameConstants.isKaiser(getPlayer().getJob())
                        || GameConstants.isZero(getPlayer().getJob()) || GameConstants.isArk(getPlayer().getJob())
                        || GameConstants.isAdele(getPlayer().getJob())) {
                    selected.setStr((short) (selected.getStr() + (100 * up)));
                } else if (getPlayer().getJob() >= 200 && getPlayer().getJob() < 300
                        || GameConstants.isFlameWizard(getPlayer().getJob())
                        || GameConstants.isEvan(getPlayer().getJob()) || GameConstants.isLuminous(getPlayer().getJob())
                        || getPlayer().getJob() >= 3200 && getPlayer().getJob() < 3300
                        || GameConstants.isKinesis(getPlayer().getJob()) || GameConstants.isIllium(getPlayer().getJob())
                        || GameConstants.isLara(getPlayer().getJob())) {
                    selected.setInt((short) (selected.getInt() + (100 * up)));
                } else if (GameConstants.isKain(getPlayer().getJob())
                        || getPlayer().getJob() >= 300 && getPlayer().getJob() < 400 || getPlayer().getJob() == 522
                        || getPlayer().getJob() == 532 || GameConstants.isMechanic(getPlayer().getJob())
                        || GameConstants.isAngelicBuster(getPlayer().getJob())
                        || getPlayer().getJob() >= 1300 && getPlayer().getJob() < 1400
                        || GameConstants.isMercedes(getPlayer().getJob())
                        || getPlayer().getJob() >= 3300 && getPlayer().getJob() < 3400) {
                    selected.setDex((short) (selected.getDex() + (100 * up)));
                } else if (getPlayer().getJob() >= 400 && getPlayer().getJob() < 500
                        || getPlayer().getJob() >= 1400 && getPlayer().getJob() < 1500
                        || GameConstants.isPhantom(getPlayer().getJob()) || GameConstants.isKadena(getPlayer().getJob())
                        || GameConstants.isHoyoung(getPlayer().getJob())) {
                    selected.setLuk((short) (selected.getLuk() + (100 * up)));
                } else if (GameConstants.isDemonAvenger(getPlayer().getJob())) {
                    selected.setHp((short) (selected.getHp() + (140 * up)));
                } else if (GameConstants.isXenon(getPlayer().getJob())) {
                    selected.setStr((short) (selected.getStr() + (39 * up)));
                    selected.setDex((short) (selected.getDex() + (39 * up)));
                    selected.setLuk((short) (selected.getLuk() + (39 * up)));
                }
                getPlayer().send(CWvsContext.InventoryPacket.updateArcaneSymbol(selected));
                self.sayOk("선택하신 아케인심볼이 성공적으로 최대레벨이 되었습니다.");
            } else {
                self.sayOk("알 수 없는 오류가 발생했습니다.");
            }
        }
    }

    public void consume_2430503() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (getPlayer().getLevel() < 200) {
            self.sayOk("200레벨 이상만 사용가능합니다.");
            return;
        }
        List<Item> symbols = new ArrayList<>();
        for (Item item : getPlayer().getInventory(MapleInventoryType.EQUIPPED).list()) {
            if (GameConstants.isArcaneSymbol(item.getItemId())) {
                symbols.add(item);
            }
        }
        self.say(
                "안녕하세요! 저는 여러분의 아케인 심볼 하나를 최대 레벨까지 올려주는 아케인 담당 입니다! 저와 동일한 효과의 아이템은 #e강림 크레딧, 포인트 상점#n에서 확인 가능하니 많은 이용 바랍니다.");
        if (symbols.isEmpty()) {
            self.sayOk("장착중인 아케인 심볼이 없습니다.");
            return;
        }
        Equip selected = null;
        String string = "최대 레벨로 올릴 아케인 심볼을 선택해주세요!\r\n\r\n #r※ 주의사항 ※ \r\n선택한 심볼의 장착중인 심볼의 레벨이 최대치로 올라갑니다.#k#b";

        for (int i = 0; i < symbols.size(); i++) {
            string += "\r\n#L" + i + "# #i" + symbols.get(i).getItemId() + ":# #z" + symbols.get(i).getItemId()
                    + ":# #l";
        }
        int v = self.askMenu(string);
        if (v >= 0) {
            selected = (Equip) symbols.get(v);
        }
        if (selected != null) {
            if (selected.getArcLevel() >= 20) {
                self.sayOk("선택한 심볼의 레벨이 이미 최대치입니다.");
                return;
            }
            // updateArcaneSymbol
            if (target.exchange(2430503, -1) > 0) {
                int level = selected.getArcLevel();
                int up = 20 - level;
                selected.setArcEXP(0);
                selected.setArcLevel(20);
                selected.setArc((short) (10 * (selected.getArcLevel() + 2)));
                if (getPlayer().getJob() >= 100 && getPlayer().getJob() < 200 || getPlayer().getJob() == 512
                        || getPlayer().getJob() == 1512 || getPlayer().getJob() == 2512
                        || getPlayer().getJob() >= 1100 && getPlayer().getJob() < 1200
                        || GameConstants.isAran(getPlayer().getJob()) || GameConstants.isBlaster(getPlayer().getJob())
                        || GameConstants.isDemonSlayer(getPlayer().getJob())
                        || GameConstants.isMichael(getPlayer().getJob()) || GameConstants.isKaiser(getPlayer().getJob())
                        || GameConstants.isZero(getPlayer().getJob()) || GameConstants.isArk(getPlayer().getJob())
                        || GameConstants.isAdele(getPlayer().getJob())) {
                    selected.setStr((short) (selected.getStr() + (100 * up)));
                } else if (getPlayer().getJob() >= 200 && getPlayer().getJob() < 300
                        || GameConstants.isFlameWizard(getPlayer().getJob())
                        || GameConstants.isEvan(getPlayer().getJob()) || GameConstants.isLuminous(getPlayer().getJob())
                        || getPlayer().getJob() >= 3200 && getPlayer().getJob() < 3300
                        || GameConstants.isKinesis(getPlayer().getJob()) || GameConstants.isIllium(getPlayer().getJob())
                        || GameConstants.isLara(getPlayer().getJob())) {
                    selected.setInt((short) (selected.getInt() + (100 * up)));
                } else if (GameConstants.isKain(getPlayer().getJob())
                        || getPlayer().getJob() >= 300 && getPlayer().getJob() < 400 || getPlayer().getJob() == 522
                        || getPlayer().getJob() == 532 || GameConstants.isMechanic(getPlayer().getJob())
                        || GameConstants.isAngelicBuster(getPlayer().getJob())
                        || getPlayer().getJob() >= 1300 && getPlayer().getJob() < 1400
                        || GameConstants.isMercedes(getPlayer().getJob())
                        || getPlayer().getJob() >= 3300 && getPlayer().getJob() < 3400) {
                    selected.setDex((short) (selected.getDex() + (100 * up)));
                } else if (getPlayer().getJob() >= 400 && getPlayer().getJob() < 500
                        || getPlayer().getJob() >= 1400 && getPlayer().getJob() < 1500
                        || GameConstants.isPhantom(getPlayer().getJob()) || GameConstants.isKadena(getPlayer().getJob())
                        || GameConstants.isHoyoung(getPlayer().getJob())) {
                    selected.setLuk((short) (selected.getLuk() + (100 * up)));
                } else if (GameConstants.isDemonAvenger(getPlayer().getJob())) {
                    selected.setHp((short) (selected.getHp() + (210 * up)));
                } else if (GameConstants.isXenon(getPlayer().getJob())) {
                    selected.setStr((short) (selected.getStr() + (48 * up)));
                    selected.setDex((short) (selected.getDex() + (48 * up)));
                    selected.setLuk((short) (selected.getLuk() + (48 * up)));
                }
                getPlayer().send(CWvsContext.InventoryPacket.updateArcaneSymbol(selected));
                self.sayOk("선택하신 아케인심볼이 성공적으로 최대레벨이 되었습니다.");
            } else {
                self.sayOk("알 수 없는 오류가 발생했습니다.");
            }
        }
    }

    public void consume_2430504() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (getPlayer().getLevel() < 225) {
            self.sayOk("225레벨 이상만 사용가능합니다.");
            return;
        }
        if (target.exchange(2430504, -1, 1712004, 1, 1712005, 1, 1712006, 1) > 0) {
            getPlayer().gainMeso(50000000, false);
            self.say("[아케인심볼 : 아르카나] [아케인심볼 : 모라스] [아케인심볼 : 에스페라] [50,000,000메소] 아이템이 지급되었습니다.");
        } else {
            self.say("장비 아이템 슬롯이 부족합니다. 필요 여유공간 3칸");
        }
    }

    public void consume_2439527() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        int[] armor = new int[] { 1004808, 1102940, 1082695, 1053063, 1073158, 1152196 };
        int[][] weapon = new int[][] {
                { 1213018, 1232113, 1302343, 1312203, 1322255, 1402259, 1412181, 1422189, 1432218, 1442274, 1582023 },
                { 1212120, 1262039, 1282017, 1372228, 1382265 },
                { 1214018, 1452257, 1462243, 1522143, 1592020 },
                { 1242122, 1272017, 1292018, 1332279, 1342104, 1362140, 1472265 },
                { 1222113, 1242121, 1482221, 1492235, 1532150 } };
        int v = self.askMenu(
                "안녕하세요~! 강림메이플 입니다.\r\n멋쟁이 용사님을 위해 더 강해질 수 있는 #r무기와 방어구를 #k준비 했습니다~!준비한 방어구 말고도 추가적인 뽀~너스 아이템들도 있으니 꼭 챙겨가세요!\r\n\r\n"
                        +
                        "#b#L0#전사 아이템 받기\r\n" +
                        "#L1#마법사 아이템 받기\r\n" +
                        "#L2#궁수 아이템 받기\r\n" +
                        "#L3#도적 아이템 받기\r\n" +
                        "#L4#해적 아이템 받기\r\n");
        if (v > 4) {
            self.sayOk("잘못된 요청입니다.");
            return;
        }
        String test = "지급될 방어구 입니다.\r\n#b";
        for (int a : armor) {
            int itemID = (a + v);
            test += "#i" + itemID + "# #z" + itemID + "#\r\n";
        }
        test += "#L0# #r#e무기 선택하기#l";
        int vv = self.askMenu(test);
        if (vv == 0) {
            String wTest = "무기 리스트#b\r\n";
            int index = 0;
            for (int a : weapon[v]) {
                wTest += "#L" + index + "#" + "#i" + (a) + "# #z" + (a) + "#\r\n";
                index++;
            }
            int vvv = self.askMenu(wTest);
            if (vvv >= 0 && weapon[v].length >= vvv) {
                String all = "선택하신 아이템을 확인해주세요!#b\r\n";
                for (int a : armor) {
                    all += "#i" + (a + v) + "# #z" + (a + v) + "#\r\n";
                }
                all += "#i" + weapon[v][vvv] + "# #z" + weapon[v][vvv] + "#\r\n";
                if (1 == self.askYesNo(all)) {
                    if (1 == self.askYesNo("마지막으로 한번 더!\r\n" + all)) {
                        if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 6
                                || getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 4
                                || getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 1) {
                            self.sayOk("장비아이템 슬롯이 부족합니다.");
                            return;
                        }
                        if (target.exchange(2439527, -1, 2630437, 300, 2434290, 100, 2048757, 100, 2000054, 1, 5044006,
                                1) > 0) {
                            if (weapon[v][vvv] == 1232113) {
                                for (int a : armor) {
                                    int itemID = (a + v);
                                    exchangeEquipCHUCADDSTATBONUSEDDITIONALArmor(itemID, 25, 2, 14, 1);
                                }
                            } else {
                                for (int a : armor) {
                                    int itemID = (a + v);
                                    exchangeEquipCHUCADDSTATBONUSEDDITIONALArmor(itemID, 25, 2, 14, 0);
                                }
                            }
                            if (v != 1) {
                                exchangeEquipCHUCADDSTATBONUSEDDITIONALArmor(weapon[v][vvv], 25, 2, 14, 3);
                            } else {
                                exchangeEquipCHUCADDSTATBONUSEDDITIONALArmor(weapon[v][vvv], 25, 2, 14, 4);
                            }
                            getPlayer().forceCompleteQuest(6500); // 포켓 개방
                        }
                        self.sayOk("포켓개방 및 다양한 아이템이 지급되었습니다.");
                    } else {
                        self.sayOk("신중하게 생각하신 뒤 선택해주세요~!");
                    }
                } else {
                    self.sayOk("신중하게 생각하신 뒤 선택해주세요~!");
                }
            } else {
                self.sayOk("잘못된 요청입니다.");
            }
        }
    }

    public void consume_2430497() { // 깜찍 뉴비 지원 상자
        initNPC(MapleLifeFactory.getNPC(9010000));
        int[] armor = new int[] { 1004229, 1102718, 1082608, 1052799, 1072967, 1152108 };
        int[][] weapon = new int[][] {
                { 1213014, 1232095, 1302315, 1312185, 1322236, 1402236, 1412164, 1422171, 1432200, 1442254, 1582011 },
                { 1212101, 1262011, 1282013, 1372207, 1382245 },
                { 1214014, 1452238, 1462225, 1522124, 1592016 },
                { 1242102, 1272013, 1292014, 1332260, 1342100, 1362121, 1472247 },
                { 1222095, 1242133, 1482202, 1492212, 1532130 } };
        int v = self.askMenu(
                "안녕하세요~! 강림메이플 입니다.\r\n멋쟁이 용사님을 위해 더 강해질 수 있는 #r무기와 방어구를 #k준비 했습니다~!준비한 방어구 말고도 추가적인 뽀~너스 아이템들도 있으니 꼭 챙겨가세요!\r\n\r\n"
                        +
                        "#b#L0#전사 아이템 받기\r\n" +
                        "#L1#마법사 아이템 받기\r\n" +
                        "#L2#궁수 아이템 받기\r\n" +
                        "#L3#도적 아이템 받기\r\n" +
                        "#L4#해적 아이템 받기\r\n");
        if (v > 4) {
            self.sayOk("잘못된 요청입니다.");
            return;
        }
        String test = "지급될 방어구 입니다.\r\n#b";
        for (int a : armor) {
            int itemID = (a + v);
            if (a + v > 1152108) {
                itemID += 1;
            }
            test += "#i" + itemID + "# #z" + itemID + "#\r\n";
        }
        test += "#L0# #r#e무기 선택하기#l";
        int vv = self.askMenu(test);
        if (vv == 0) {
            String wTest = "무기 리스트#b\r\n";
            int index = 0;
            for (int a : weapon[v]) {
                wTest += "#L" + index + "#" + "#i" + (a) + "# #z" + (a) + "#\r\n";
                index++;
            }
            int vvv = self.askMenu(wTest);
            if (vvv >= 0 && weapon[v].length >= vvv) {
                String all = "선택하신 아이템을 확인해주세요!#b\r\n";
                for (int a : armor) {
                    int itemID = (a + v);
                    if (a + v > 1152108) {
                        itemID += 1;
                    }
                    all += "#i" + itemID + "# #z" + itemID + "#\r\n";
                }
                all += "#i" + weapon[v][vvv] + "# #z" + weapon[v][vvv] + "#\r\n";
                if (1 == self.askYesNo(all)) {
                    if (1 == self.askYesNo("마지막으로 한번 더!\r\n" + all)) {
                        if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 11 ||
                                getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 13 ||
                                getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 1 ||
                                getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 2) {
                            self.sayOk("장비, 소비, 설치치,캐시 아이템 슬롯의 여유 공간이 부족합니다.");
                            return;
                        }
                        if (target.exchange(2430497, -1, 2630437, 100, 2048757, 50, 2000054, 1, 5044006, 1, 1712001, 1,
                                1712002, 1, 1712003, 1, 3014005, 1, 3014028, 1, 2439580, 1, 2439581, 1, 2439582, 1,
                                2439583, 1, 2439584, 1, 2435122, 3, 2430503, 1, 2430504, 1) > 0) {
                            if (weapon[v][vvv] == 1232095) { // 데스페라도
                                for (int a : armor) {
                                    int itemID = (a + v);
                                    if (a + v > 1152108) {
                                        itemID += 1;
                                    }
                                    exchangeEquipCHUCADDSTATBONUSEDDITIONALArmor(itemID, 17, 2, 14, 1);
                                }
                            } else {
                                for (int a : armor) {
                                    int itemID = (a + v);
                                    if (a + v > 1152108) {
                                        itemID += 1;
                                    }
                                    exchangeEquipCHUCADDSTATBONUSEDDITIONALArmor(itemID, 17, 2, 14, 0);
                                }
                            }
                            if (v != 1) {
                                exchangeEquipCHUCADDSTATBONUSEDDITIONALArmor(weapon[v][vvv], 17, 2, 14, 3);
                            } else {
                                exchangeEquipCHUCADDSTATBONUSEDDITIONALArmor(weapon[v][vvv], 17, 2, 14, 4);
                            }
                            getPlayer().gainMeso(5000000, true);
                            getPlayer().forceCompleteQuest(6500); // 포켓 개방
                            getPlayer().changeSkillLevel(80001825, 30, 30);
                            getPlayer().changeSkillLevel(80001829, 5, 5);
                        }
                        self.sayOk("[포켓개방] [일섬스킬지급] [비연스킬지급] [다양한 아이템] [5,000,000메소] 지급되었습니다.");
                    } else {
                        self.sayOk("신중하게 생각하신 뒤 선택해주세요~!");
                    }
                } else {
                    self.sayOk("신중하게 생각하신 뒤 선택해주세요~!");
                }
            } else {
                self.sayOk("잘못된 요청입니다.");
            }
        }
    }

    public void consume_2435122() {
        incDamageSkinSlot();
    }

    public void consume_2435513() {
        incDamageSkinSlot();
    }

    public void consume_2436784() {
        incDamageSkinSlot();
    }

    public void consume_2439631() {
        incDamageSkinSlot();
    }

    public void incDamageSkinSlot() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (increaseDamageSkinSlotCount(1)) {
            target.exchange(itemID, -1);
            getPlayer().dropMessage(5, "데미지 스킨 저장 슬롯이 확장되었습니다.");
        } else {
            getPlayer().dropMessage(5, "더 이상 데미지 스킨 저장 슬롯을 확장할 수 없습니다.");
        }
    }

    public boolean increaseDamageSkinSlotCount(int add) {
        if (getPlayer().getDamageSkinSaveInfo().getSlotCount() >= 48) {
            return false;
        }
        getPlayer().getDamageSkinSaveInfo().addSlotCount(add);
        getPlayer().setSaveFlag(getPlayer().getSaveFlag() | CharacterSaveFlag.DAMAGE_SKIN_SAVE.getFlag());

        getPlayer().updateOneInfo(13190, "slotCount",
                String.valueOf(getPlayer().getDamageSkinSaveInfo().getSlotCount()));
        return true;
    }

    public void consume_2633597() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (target.exchange(itemID, -1, 1662175, 1, 1672085, 1) > 0) {
            self.sayOk("교환이 완료되었습니다. 장비창을 확인해보세요.");
        } else {
            self.sayOk("인벤토리 슬롯이 부족합니다.");
        }
    }

    public void createRecoveryQex() {
        Table mainTable = new Table(getPlayer().getName() + "_qex");

        // 복원 시점에 sql파일을 가져와 데이터 파일을 생성한다.
        for (Map.Entry<Integer, QuestEx> entry : getPlayer().getQuestInfos().entrySet()) {
            Table table = new Table(String.valueOf(entry.getKey()));
            table.put("questID", String.valueOf(entry.getKey()));
            table.put("data", entry.getValue().getData());
            table.put("date", entry.getValue().getTime());

            mainTable.putChild(table);
        }
        try {
            mainTable.save(Paths.get(getPlayer().getName() + "_qex.data"));
        } catch (Exception e) {
        }
    }

    public void recoveryQex() {
        // 복원 데이터를 통해 복구한다.

        try {
            Table table = objects.utils.Properties.loadTable("./", "qex.data");
            for (Table child : table.list()) {
                for (Table c : child.list()) {
                    int questID = Integer.parseInt(c.getProperty("questID"));
                    String data = c.getProperty("data");
                    String time = c.getProperty("date");

                    getPlayer().getInfoQuest_Map().put(questID, new QuestEx(questID, data, time));
                    getPlayer().setSaveFlag(getPlayer().getSaveFlag() | CharacterSaveFlag.QUEST_INFO.getFlag());
                    getPlayer().send(CWvsContext.InfoPacket.updateInfoQuest(questID, data));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        getPlayer().dropMessage(5, "모든 퀘스트 데이터가 복구되었습니다. 재접속 해주시기 바랍니다.");
        getPlayer().dropMessage(1, "모든 퀘스트 데이터가 복구되었습니다. 재접속 해주시기 바랍니다.");
    }

    public void consume_2433482() {
        if (DBConfig.isGanglim) {
            if (getPlayer().getClient().isGm()) {
                /*
                 * try (Connection con = DBConnection.getConnection()) {
                 * List<Pair<Item, MapleInventoryType>> itemlist = new ArrayList<>();
                 * List<AuctionItemPackage> items = new ArrayList<>();
                 * for (int i = 0; i < 100000; ++i) {
                 * AuctionItemPackage aip = Center.Auction.getItem(i);
                 * if (aip != null) {
                 * items.add(aip);
                 * }
                 * }
                 * getPlayer().dropMessage(5, "저장 될 경매장 데이터 갯수 : " + items.size());
                 * for (AuctionItemPackage aitem : new ArrayList<>(items)) {
                 * itemlist.add(new Pair<>(aitem.getItem(),
                 * GameConstants.getInventoryType(aitem.getItem().getItemId())));
                 * }
                 * saveItems(itemlist, con, -1, items);
                 * getPlayer().dropMessage(5, "휴.. 저장 완료");
                 * } catch (SQLException e) {
                 * e.printStackTrace();
                 * }
                 */
            }
            /*
             * if (getPlayer().getClient().isGm()) {
             * for (GameServer cs : GameServer.getAllInstances()) {
             * for (Field map : cs.getMapFactory().getAllMaps()) {
             * for (MapleCharacter chr : new ArrayList<>(map.getCharacters())) {
             * if (chr != null) {
             * chr.getClient().removeKeyValue("HgradeWeek");
             * }
             * }
             * }
             * }
             * 
             * for (MapleCharacter chr :
             * AuctionServer.getPlayerStorage().getAllCharacters()) {
             * if (chr != null) {
             * chr.getClient().removeKeyValue("HgradeWeek");
             * }
             * }
             * 
             * for (GameServer cs : GameServer.getAllInstances()) {
             * for (Field map : cs.getMapFactory().getAllMaps()) {
             * for (MapleCharacter chr : new ArrayList<>(map.getCharacters())) {
             * if (chr != null) {
             * chr.getClient().removeKeyValue("HgradeWeek");
             * }
             * }
             * }
             * }
             * 
             * for (MapleCharacter chr :
             * CashShopServer.getPlayerStorage().getAllCharacters()) {
             * if (chr != null) {
             * chr.getClient().removeKeyValue("HgradeWeek");
             * }
             * }
             * }
             */
            return;
        } else {
            /*
             * if (getPlayer().getClient().isGm()) {
             * SimpleDateFormat lastDate = new SimpleDateFormat("yy/MM/dd");
             * for (GameServer cs : GameServer.getAllInstances()) {
             * for (Field map : cs.getMapFactory().getAllMaps()) {
             * for (MapleCharacter chr : new ArrayList<>(map.getCharacters())) {
             * if (chr != null) {
             * chr.updateOneInfo(1234699, "count", "0");
             * chr.updateOneInfo(1234699, "complete", "0");
             * chr.updateOneInfo(1234699, "day", "0");
             * chr.updateOneInfo(1234699, "dailyGiftCT", "0");
             * chr.updateOneInfo(1234699, "passCount", "0");
             * chr.updateOneInfo(1234699, "bMaxDay", "126");
             * chr.updateOneInfo(1234699, "lastDate", lastDate.format(new Date()));
             * chr.updateOneInfo(1234699, "cMaxDay", "126");
             * }
             * }
             * }
             * }
             * 
             * for (MapleCharacter chr :
             * AuctionServer.getPlayerStorage().getAllCharacters()) {
             * if (chr != null) {
             * chr.updateOneInfo(1234699, "count", "0");
             * chr.updateOneInfo(1234699, "complete", "0");
             * chr.updateOneInfo(1234699, "day", "0");
             * chr.updateOneInfo(1234699, "dailyGiftCT", "0");
             * chr.updateOneInfo(1234699, "passCount", "0");
             * chr.updateOneInfo(1234699, "bMaxDay", "126");
             * chr.updateOneInfo(1234699, "lastDate", lastDate.format(new Date()));
             * chr.updateOneInfo(1234699, "cMaxDay", "126");
             * }
             * }
             * 
             * for (MapleCharacter chr :
             * CashShopServer.getPlayerStorage().getAllCharacters()) {
             * if (chr != null) {
             * chr.updateOneInfo(1234699, "count", "0");
             * chr.updateOneInfo(1234699, "complete", "0");
             * chr.updateOneInfo(1234699, "day", "0");
             * chr.updateOneInfo(1234699, "dailyGiftCT", "0");
             * chr.updateOneInfo(1234699, "passCount", "0");
             * chr.updateOneInfo(1234699, "bMaxDay", "126");
             * chr.updateOneInfo(1234699, "lastDate", lastDate.format(new Date()));
             * chr.updateOneInfo(1234699, "cMaxDay", "126");
             * }
             * }
             * 
             * PreparedStatement ps = null;
             * ResultSet rs = null;
             * DBConnection db = new DBConnection();
             * try (Connection con = db.getConnection()) {
             * ps = con.
             * prepareStatement("SELECT `customData`, `account_id` FROM questinfo_account WHERE quest = ?"
             * );
             * ps.setInt(1, 1234699);
             * rs = ps.executeQuery();
             * boolean f = false;
             * while (rs.next()) {
             * f = true;
             * String customData = rs.getString("customData");
             * int accountID = rs.getInt("account_id");
             * 
             * String[] v = customData.split(";");
             * 
             * StringBuilder sb = new StringBuilder();
             * int i = 1;
             * int count = 0;
             * boolean a = false;
             * for (String v_ : v) {
             * String[] cd = v_.split("=");
             * if (cd[0].equals("get_sp_item") || cd[0].equals("get_sp_item2")) {
             * sb.append(cd[0]);
             * sb.append("=");
             * //System.out.println("시디1 : " + cd[1]);
             * sb.append(cd[1]);
             * if (count <= 0) {
             * sb.append(";");
             * }
             * count++;
             * }
             * }
             * PreparedStatement ps2 = con.
             * prepareStatement("UPDATE questinfo_account SET customData = ? WHERE account_id = ? and quest = ?"
             * );
             * ps2.setString(1, sb.toString());
             * ps2.setInt(2, accountID);
             * ps2.setInt(3, 1234699);
             * ps2.executeUpdate();
             * ps2.close();
             * }
             * } catch (SQLException e) {
             * e.printStackTrace();
             * } finally {
             * try {
             * if (ps != null) {
             * ps.close();
             * ps = null;
             * }
             * if (rs != null) {
             * rs.close();
             * rs = null;
             * }
             * } catch (SQLException e) {
             * e.printStackTrace();
             * }
             * }
             * getPlayer().dropMessage(5, "진 유저의 모든 황금 마차가 초기화되었습니다.");
             * return;
             * }
             */
        }
        initNPC(MapleLifeFactory.getNPC(9010000));

        if (getPlayer().getName().equals("이유")) {
            if (target.exchange(2433482, -1) > 0) {
                recoveryQex();
                return;
            }
        }
        if (target.exchange(2433482, -1) > 0) {
            doGenesisWeaponUpgrade();
        }

        /*
         * if (target.exchange(2433482, -1, 2439614, 1, 4036460, 1, 4036461, 1, 4036462,
         * 1, 4036463, 1, 4036464, 1) > 0) {
         * // 제네시스 무기 버릴 시 퀘스트 초기화
         * for (int i = 2000018; i <= 2000027; ++i) {
         * getPlayer().updateOneInfo(i, "clear", "0");
         * 
         * final MapleQuestStatus newStatus = new
         * MapleQuestStatus(MapleQuest.getInstance(i), (byte) 0, 2003);
         * getPlayer().updateQuest(newStatus);
         * }
         * 
         * for (int i = 2000018; i <= 2000025; ++i) {
         * getPlayer().updateOneInfo(i, "clear", "1");
         * }
         * 
         * getPlayer().fakeRelog();
         * } else {
         * self.say("#b소비#k 인벤토리와 #b기타#k 인벤토리 슬롯을 확보하고 다시 시도해주시기 바랍니다.");
         * }
         */
    }

    int[] bmWeapons = GameConstants.bmWeapons;

    // 봉인된 제네시스 무기 최종 해방
    public void doGenesisWeaponUpgrade() {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Equip equip = null;
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
        /*
         * int weaponID = equip.getItemId() + 1;
         * Equip genesis = (Equip) ii.getEquipById(weaponID);
         * 
         * if (genesis == null) {
         * sendNext("알 수 없는 오류가 발생했습니다.");
         * dispose();
         * return;
         * }
         */
        int weaponID = equip.getItemId();
        Equip genesis = (Equip) ii.getEquipById(weaponID);

        if (genesis == null) {
            self.say("알 수 없는 오류가 발생했습니다.", ScriptMessageFlag.Self);
            return;
        }

        int flag = EquipEnchantMan.filterForJobWeapon(weaponID);
        ItemUpgradeFlag[] flagArray = new ItemUpgradeFlag[] {
                ItemUpgradeFlag.INC_PAD,
                ItemUpgradeFlag.INC_MAD
        };
        ItemUpgradeFlag[] flagArray2 = new ItemUpgradeFlag[] {
                ItemUpgradeFlag.INC_STR,
                ItemUpgradeFlag.INC_DEX,
                ItemUpgradeFlag.INC_LUK,
                ItemUpgradeFlag.INC_MHP
        };
        ItemUpgradeFlag[] flagArray3 = new ItemUpgradeFlag[] {
                ItemUpgradeFlag.INC_INT
        };
        List<EquipEnchantScroll> source = new ArrayList<>();
        for (ItemUpgradeFlag f : flagArray) {
            for (ItemUpgradeFlag f2 : f == ItemUpgradeFlag.INC_PAD ? flagArray2 : flagArray3) {
                int index = 3; // 15%
                EquipEnchantOption option = new EquipEnchantOption();
                option.setOption(f.getValue(), EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
                if ((f2.check(flag))) {
                    option.setOption(f2.getValue(), EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3)
                            * (f2 == ItemUpgradeFlag.INC_MHP ? 50 : 1));
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
            option.setOption(ItemUpgradeFlag.INC_PAD.getValue(),
                    EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
            option.setOption(ItemUpgradeFlag.INC_LUK.getValue(),
                    EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3));

            source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
        }
        if (equip.getItemId() == 1232121) { // 데벤져
            source.clear();
            EquipEnchantOption option = new EquipEnchantOption();
            option.setOption(ItemUpgradeFlag.INC_PAD.getValue(),
                    EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
            option.setOption(ItemUpgradeFlag.INC_MHP.getValue(),
                    EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3) * 50);

            source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
        }
        if (equip.getItemId() == 1292021) { // 호영
            source.clear();
            EquipEnchantOption option = new EquipEnchantOption();
            option.setOption(ItemUpgradeFlag.INC_PAD.getValue(),
                    EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
            option.setOption(ItemUpgradeFlag.INC_LUK.getValue(),
                    EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3));

            source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
        }
        if (equip.getItemId() == 1362148) { // 팬텀
            source.clear();
            EquipEnchantOption option = new EquipEnchantOption();
            option.setOption(ItemUpgradeFlag.INC_PAD.getValue(),
                    EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
            option.setOption(ItemUpgradeFlag.INC_LUK.getValue(),
                    EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3));

            source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
        }
        if (equip.getItemId() == 1362148) { // 표도
            source.clear();
            EquipEnchantOption option = new EquipEnchantOption();
            option.setOption(ItemUpgradeFlag.INC_PAD.getValue(),
                    EquipEnchantMan.getIncATTWeapon(ii.getReqLevel(weaponID), 3));
            option.setOption(ItemUpgradeFlag.INC_LUK.getValue(),
                    EquipEnchantMan.getIncPrimaryStatWeapon(ii.getReqLevel(weaponID), 3));

            source.add(new EquipEnchantScroll(weaponID, 3, option, ScrollType.UPGRADE, 0, false));
        }
        if (source.size() <= 0) {
            self.say("알 수 없는 오류가 발생했습니다.", ScriptMessageFlag.Self);
            return;
        }
        EquipEnchantScroll scroll = source.get(0); // 첫번째가 직업에 맞는 주문서
        if (scroll == null) {
            self.say("알 수 없는 오류가 발생했습니다.", ScriptMessageFlag.Self);
            return;
        }
        // 8번 성공시킴

        Equip zeroEquip = null;
        if (GameConstants.isZero(getPlayer().getJob())) {
            zeroEquip = (Equip) getPlayer().getInventory(MapleInventoryType.EQUIPPED)
                    .getItem(equip.getPosition() == -11 ? (short) -10 : -11);
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
            int option = ItemOptionInfo.getItemOption(equip.getItemId(), optionGrade, genesis.getPotentials(false, i),
                    GradeRandomOption.Black);
            genesis.setPotentialOption(i, option);
        }

        // 에픽 에디셔널 잠재능력 3줄
        for (int i = 0; i < 3; ++i) {
            int optionGrade = 2; // 에픽
            int option = ItemOptionInfo.getItemOption(equip.getItemId(), optionGrade, genesis.getPotentials(true, i),
                    GradeRandomOption.Additional);
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

        MapleInventoryManipulator.removeFromSlot(getClient(), type, equip.getPosition(), equip.getQuantity(), false,
                false);
        MapleInventoryManipulator.addbyItem(getClient(), genesis);

        for (int i = 2000018; i <= 2000027; ++i) {
            getPlayer().forceCompleteQuest(i);
        }

        Center.Broadcast.broadcastMessage(CWvsContext.serverNotice(6,
                getPlayer().getName() + "님이 봉인된 힘을 해방하고 검은 마법사의 힘이 담긴 제네시스 무기의 주인이 되었습니다."));
    }

    public void consume_2633927() {
        initNPC(MapleLifeFactory.getNPC(9010000));

        String v0 = "받으실 아이템을 선택해 주세요.\r\n#b";
        int baseItem = 1190555;
        for (int i = 0; i < 5; ++i) {
            v0 += "#L" + i + "##i" + (baseItem + i) + "# #z" + (baseItem + i) + "#\r\n";
        }
        v0 += "\r\n#L6#사용 취소#l";
        int v1 = self.askMenu(v0);
        if (v1 >= 0 && v1 <= 4) {
            int itemID = baseItem + v1;
            if (target.exchange(2633927, -1, itemID, 1) > 0) {
                self.say("교환이 완료되었습니다.");
            } else {
                self.say("장비 인벤토리 공간을 확보하고 다시 시도해주시기 바랍니다.");
            }
        }
    }

    // 진:眞 11월 마음을 담은 상자
    public void consume_2439630() {
        initNPC(MapleLifeFactory.getNPC(9062000));
        int[][] rewards = new int[][] {
                { 2434557, 3 }, // 1만 강화 포인트 교환권 3개
                { 5680409, 1 }, // 5만 캐시 교환권 1개
                { 5060048, 5 }, // 골드애플 5개
                { 5068300, 5 }, // 위습의 원더베리 2개
                { 5680157, 1 }, // 진:眞 강림 스타일 1개
                { 2436018, 1 }, // 진:眞 스페셜 헤어 쿠폰 1개
                { 2439605, 1 }, // 진:眞 스페셜 코디 상자 (S) 1개
                { 2439630, -1 }, // 상자 소비
        };

        String v0 = "상자를 열면 아래와 같은 아이템을 얻을 수 있다. 열어볼까?\r\n\r\n";
        v0 += "#e[아이템 획득]#n\r\n";
        for (int[] reward : rewards) {
            if (reward[1] != -1) {
                v0 += "#b#i" + reward[0] + "# #z" + reward[0] + "##k " + reward[1] + "개\r\n";
            }
        }

        v0 += "#b#i5002239# #z5002239##k (기간제 30일)\r\n";
        int v1 = self.askYesNo(v0, ScriptMessageFlag.NpcReplacedByUser);
        if (v1 > 0) {

            // 본래 exchange로 하면 되나, 기간제 펫때문에 이렇게 해야한다.
            if (getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 1) {
                self.say("#b캐시 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
                return;
            }
            if (target.exchange(rewards) == 1) {
                exchangePetPeriod(5002239, 30);
                self.say("상자를 열어 아이템을 획득했다.", ScriptMessageFlag.NpcReplacedByUser);
            } else {
                self.say("#b소비 인벤토리#k와 #b캐시 인벤토리#k 슬롯 여유를 확보하고 다시 시도하자.", ScriptMessageFlag.NpcReplacedByUser);
            }
        }
    }

    // 아케인심볼 : 소멸의 여로 강화권
    public void consume_2431470() {
        initNPC(MapleLifeFactory.getNPC(9062000));

        // 아케인심볼 : 소멸의 여로를 장착중인지 체크
        Item item = getPlayer().getInventory(MapleInventoryType.EQUIPPED).findById(1712001);

        if (item == null) {
            self.say("#b#i1712001# #z1712001##k을(를) 장착하고 사용을 시도해주시기 바랍니다.");
            return;
        }
        String v0 = "#b#i1712001# #z1712001##k에 #b#z2431470##k을 사용하여 #e올스텟 +1,000, 공격력/마력 +300#n 효과를 부여하시겠습니까?\r\n\r\n해당 아이템은 1회만 적용 가능하며, #r이미 적용된 심볼엔 사용이 불가능#k합니다.";
        if (self.askYesNo(v0) == 1) {
            String owner = item.getOwner();
            if (!owner.isEmpty()) {
                self.say("강화권이 이미 적용된 심볼에는 사용이 불가능합니다.");
                return;
            }
            if (target.exchange(2431470, -1) > 0) {
                Equip equip = (Equip) item;
                equip.setOwner("초월한 심볼");
                equip.setWatk((short) (equip.getWatk() + 300));
                equip.setMatk((short) (equip.getMatk() + 300));
                equip.setStr((short) (equip.getStr() + 1000));
                equip.setDex((short) (equip.getDex() + 1000));
                equip.setInt((short) (equip.getInt() + 1000));
                equip.setLuk((short) (equip.getLuk() + 1000));

                getPlayer().send(CWvsContext.InventoryPacket.updateEquipSlot(equip));
                self.say("강화권이 적용되었습니다. 장비창을 확인해보시기 바랍니다.");
            }
        }
    }

    // 아케인심볼 : 츄츄 아일랜드 강화권
    public void consume_2431471() {
        initNPC(MapleLifeFactory.getNPC(9062000));

        // 아케인심볼 : 츄츄 아일랜드를 장착중인지 체크
        Item item = getPlayer().getInventory(MapleInventoryType.EQUIPPED).findById(1712002);

        if (item == null) {
            self.say("#b#i1712002# #z1712002##k을(를) 장착하고 사용을 시도해주시기 바랍니다.");
            return;
        }
        String v0 = "#b#i1712002# #z1712002##k에 #b#z2431471##k을 사용하여 #e올스텟 +750, 공격력/마력 +250#n 효과를 부여하시겠습니까?\r\n\r\n해당 아이템은 1회만 적용 가능하며, #r이미 적용된 심볼엔 사용이 불가능#k합니다.";
        if (self.askYesNo(v0) == 1) {
            String owner = item.getOwner();
            if (!owner.isEmpty()) {
                self.say("강화권이 이미 적용된 심볼에는 사용이 불가능합니다.");
                return;
            }
            if (target.exchange(2431471, -1) > 0) {
                Equip equip = (Equip) item;
                equip.setOwner("강화된 심볼");
                equip.setWatk((short) (equip.getWatk() + 750));
                equip.setMatk((short) (equip.getMatk() + 750));
                equip.setStr((short) (equip.getStr() + 1500));
                equip.setDex((short) (equip.getDex() + 1500));
                equip.setInt((short) (equip.getInt() + 1500));
                equip.setLuk((short) (equip.getLuk() + 1500));

                getPlayer().send(CWvsContext.InventoryPacket.updateEquipSlot(equip));
                self.say("강화권이 적용되었습니다. 장비창을 확인해보시기 바랍니다.");
            }
        }
    }

    // 아케인심볼 : 레헬른 강화권
    public void consume_2431472() {
        initNPC(MapleLifeFactory.getNPC(9062000));

        // 아케인심볼 : 레헬른를 장착중인지 체크
        Item item = getPlayer().getInventory(MapleInventoryType.EQUIPPED).findById(1712003);

        if (item == null) {
            self.say("#b#i1712003# #z1712003##k을(를) 장착하고 사용을 시도해주시기 바랍니다.");
            return;
        }
        String v0 = "#b#i1712003# #z1712003##k에 #b#z2431472##k을 사용하여 #e올스텟 +750, 공격력/마력 +250#n 효과를 부여하시겠습니까?\r\n\r\n해당 아이템은 1회만 적용 가능하며, #r이미 적용된 심볼엔 사용이 불가능#k합니다.";
        if (self.askYesNo(v0) == 1) {
            String owner = item.getOwner();
            if (!owner.isEmpty()) {
                self.say("강화권이 이미 적용된 심볼에는 사용이 불가능합니다.");
                return;
            }
            if (target.exchange(2431472, -1) > 0) {
                Equip equip = (Equip) item;
                equip.setOwner("강화된 심볼");
                equip.setWatk((short) (equip.getWatk() + 750));
                equip.setMatk((short) (equip.getMatk() + 750));
                equip.setStr((short) (equip.getStr() + 1500));
                equip.setDex((short) (equip.getDex() + 1500));
                equip.setInt((short) (equip.getInt() + 1500));
                equip.setLuk((short) (equip.getLuk() + 1500));

                getPlayer().send(CWvsContext.InventoryPacket.updateEquipSlot(equip));
                self.say("강화권이 적용되었습니다. 장비창을 확인해보시기 바랍니다.");
            }
        }
    }

    // 아케인심볼 : 아르카나 강화권
    public void consume_2431475() {
        initNPC(MapleLifeFactory.getNPC(9062000));

        // 아케인심볼 : 아르카나를 장착중인지 체크
        Item item = getPlayer().getInventory(MapleInventoryType.EQUIPPED).findById(1712004);

        if (item == null) {
            self.say("#b#i1712004# #z1712004##k을(를) 장착하고 사용을 시도해주시기 바랍니다.");
            return;
        }
        String v0 = "#b#i1712004# #z1712004##k에 #b#z2431475##k을 사용하여 #e올스텟 +750, 공격력/마력 +250#n 효과를 부여하시겠습니까?\r\n\r\n해당 아이템은 1회만 적용 가능하며, #r이미 적용된 심볼엔 사용이 불가능#k합니다.";
        if (self.askYesNo(v0) == 1) {
            String owner = item.getOwner();
            if (!owner.isEmpty()) {
                self.say("강화권이 이미 적용된 심볼에는 사용이 불가능합니다.");
                return;
            }
            if (target.exchange(2431475, -1) > 0) {
                Equip equip = (Equip) item;
                equip.setOwner("강화된 심볼");
                equip.setWatk((short) (equip.getWatk() + 750));
                equip.setMatk((short) (equip.getMatk() + 750));
                equip.setStr((short) (equip.getStr() + 1500));
                equip.setDex((short) (equip.getDex() + 1500));
                equip.setInt((short) (equip.getInt() + 1500));
                equip.setLuk((short) (equip.getLuk() + 1500));

                getPlayer().send(CWvsContext.InventoryPacket.updateEquipSlot(equip));
                self.say("강화권이 적용되었습니다. 장비창을 확인해보시기 바랍니다.");
            }
        }
    }

    // 아케인심볼 : 모라스 강화권
    public void consume_2431483() {
        initNPC(MapleLifeFactory.getNPC(9062000));

        // 아케인심볼 : 모라스를 장착중인지 체크
        Item item = getPlayer().getInventory(MapleInventoryType.EQUIPPED).findById(1712005);

        if (item == null) {
            self.say("#b#i1712005# #z1712005##k을(를) 장착하고 사용을 시도해주시기 바랍니다.");
            return;
        }
        String v0 = "#b#i1712005# #z1712005##k에 #b#z2431483##k을 사용하여 #e올스텟 +750, 공격력/마력 +250#n 효과를 부여하시겠습니까?\r\n\r\n해당 아이템은 1회만 적용 가능하며, #r이미 적용된 심볼엔 사용이 불가능#k합니다.";
        if (self.askYesNo(v0) == 1) {
            String owner = item.getOwner();
            if (!owner.isEmpty()) {
                self.say("강화권이 이미 적용된 심볼에는 사용이 불가능합니다.");
                return;
            }
            if (target.exchange(2431483, -1) > 0) {
                Equip equip = (Equip) item;
                equip.setOwner("강화된 심볼");
                equip.setWatk((short) (equip.getWatk() + 750));
                equip.setMatk((short) (equip.getMatk() + 750));
                equip.setStr((short) (equip.getStr() + 1500));
                equip.setDex((short) (equip.getDex() + 1500));
                equip.setInt((short) (equip.getInt() + 1500));
                equip.setLuk((short) (equip.getLuk() + 1500));

                getPlayer().send(CWvsContext.InventoryPacket.updateEquipSlot(equip));
                self.say("강화권이 적용되었습니다. 장비창을 확인해보시기 바랍니다.");
            }
        }
    }

    // 아케인심볼 : 에스페라 강화권
    public void consume_2431540() {
        initNPC(MapleLifeFactory.getNPC(9062000));

        // 아케인심볼 : 에스페라를 장착중인지 체크
        Item item = getPlayer().getInventory(MapleInventoryType.EQUIPPED).findById(1712006);

        if (item == null) {
            self.say("#b#i1712006# #z1712006##k을(를) 장착하고 사용을 시도해주시기 바랍니다.");
            return;
        }
        String v0 = "#b#i1712006# #z1712006##k에 #b#z2431540##k을 사용하여 #e올스텟 +750, 공격력/마력 +250#n 효과를 부여하시겠습니까?\r\n\r\n해당 아이템은 1회만 적용 가능하며, #r이미 적용된 심볼엔 사용이 불가능#k합니다.";
        if (self.askYesNo(v0) == 1) {
            String owner = item.getOwner();
            if (!owner.isEmpty()) {
                self.say("강화권이 이미 적용된 심볼에는 사용이 불가능합니다.");
                return;
            }
            if (target.exchange(2431540, -1) > 0) {
                Equip equip = (Equip) item;
                equip.setOwner("강화된 심볼");
                equip.setWatk((short) (equip.getWatk() + 750));
                equip.setMatk((short) (equip.getMatk() + 750));
                equip.setStr((short) (equip.getStr() + 1500));
                equip.setDex((short) (equip.getDex() + 1500));
                equip.setInt((short) (equip.getInt() + 1500));
                equip.setLuk((short) (equip.getLuk() + 1500));

                getPlayer().send(CWvsContext.InventoryPacket.updateEquipSlot(equip));
                self.say("강화권이 적용되었습니다. 장비창을 확인해보시기 바랍니다.");
            }
        }
    }

    public void consume_2434325() {
        initNPC(MapleLifeFactory.getNPC(9000159));
        int level = self.askNumber("소환할 허수아비의 레벨을 입력해 주세요.(100레벨 ~ 250레벨)", 200, 100, 250);
        if (level < 100)
            return;
        if (level > 250)
            return;
        MapleMonster mob = MapleLifeFactory.getMonster(9305650);
        if (!mob.getStats().isChangeable()) {
            mob.getStats().setChange(true);
        }
        ChangeableStats stat = new ChangeableStats(mob.getStats(),
                new OverrideMonsterStats(21000000000000L, mob.getStats().getMp(), 0));
        mob.changeCustomStat(new ChangeableStats(mob.getStats(), stat, level));
        getPlayer().getMap().spawnMonsterOnGroundBelow(mob, getPlayer().getPosition());
        getPlayer().removeItem(2434325, -1);
    }

    public void consume_2434330() {
        initNPC(MapleLifeFactory.getNPC(9000159));
        int level = self.askNumber("소환할 허수아비의 레벨을 입력해 주세요.(100레벨 ~ 250레벨)", 200, 100, 250);
        if (level < 100)
            return;
        if (level > 250)
            return;
        MapleMonster mob = MapleLifeFactory.getMonster(9305652);
        if (!mob.getStats().isChangeable()) {
            mob.getStats().setChange(true);
        }
        ChangeableStats stat = new ChangeableStats(mob.getStats(),
                new OverrideMonsterStats(21000000000000L, mob.getStats().getMp(), 0));
        mob.changeCustomStat(new ChangeableStats(mob.getStats(), stat, level));
        getPlayer().getMap().spawnMonsterOnGroundBelow(mob, getPlayer().getPosition());
        getPlayer().removeItem(2434330, -1);
    }

    public void consume_2432098() {
        if (DBConfig.isGanglim) { // 강림에서는 사용하지 않는 아이템
            return;
        }
        initNPC(MapleLifeFactory.getNPC(9062000));
        String v0 = "#b#i2432098# #z2432098##k를 사용하여 주간 보스 입장 횟수 및 클리어 횟수를 초기화 할 수 있습니다.\r\n\r\n#r헬 모드#k는 모든 보스를 통틀어 #r하루 5회#k까지만 클리어 횟수 초기화가 가능하며, 입장 횟수는 제한 없이 초기화가능합니다.\r\n\r\n";
        int count = getPlayer().getOneInfoQuestInteger(1234569, "hell_boss_count");
        v0 += "#e금일 헬 모드 클리어 초기화 횟수 : (" + count + "/5)#n\r\n\r\n";
        v0 += "초기화 할 보스를 선택해주시기 바랍니다.#b\r\n#L7#카오스 파풀라투스를 초기화하겠습니다.#l\r\n#L0#스우를 초기화 하겠습니다.#l\r\n#L1#데미안을 초기화하겠습니다.#l\r\n#L2#루시드를 초기화하겠습니다.#l\r\n#L3#윌을 초기화 하겠습니다.#l\r\n#L4#진 힐라를 초기화하겠습니다.#l\r\n#L5#더스크를 초기화하겠습니다.#l\r\n#L6#듄켈을 초기화 하겠습니다.#l\r\n#L9#가디언 엔젤 슬라임을 초기화하겠습니다.\r\n#L8#세렌을 초기화하겠습니다.#l\r\n#L10##r헬 스우#b를 초기화하겠습니다.#l\r\n#L11##r헬 데미안#b을 초기화하겠습니다.#l\r\n#L12##r헬 루시드#b를 초기화하겠습니다.#l\r\n#L13##r헬 윌#b을 초기화하겠습니다.#l\r\n";
        int v1 = self.askMenu(v0);
        String bossName = "";
        String clearKeyValue = "";
        String canTimeKeyValue = "";
        List<String> countList = new ArrayList<>();
        switch (v1) {
            case 0:
                bossName = "스우";
                clearKeyValue = "swoo_clear";
                canTimeKeyValue = "swoo_can_time";
                countList.add("노말 스우c");
                countList.add("하드 스우c");
                countList.add("헬 스우c");
                break;
            case 1:
                bossName = "데미안";
                clearKeyValue = "demian_clear";
                canTimeKeyValue = "demian_can_time";
                countList.add("노말 데미안c");
                countList.add("하드 데미안c");
                countList.add("헬 데미안c");
                break;
            case 2:
                bossName = "루시드";
                clearKeyValue = "lucid_clear";
                canTimeKeyValue = "lucid_can_time";
                countList.add("노말 루시드c");
                countList.add("하드 루시드c");
                countList.add("헬 루시드c");
                break;
            case 3:
                bossName = "윌";
                clearKeyValue = "will_clear";
                canTimeKeyValue = "will_can_time";
                countList.add("노말 윌c");
                countList.add("하드 윌c");
                countList.add("헬 윌c");
                break;
            case 4:
                bossName = "진 힐라";
                clearKeyValue = "jinhillah_clear";
                canTimeKeyValue = "jinhillah_can_time";
                countList.add("노말 진힐라c");
                countList.add("하드 진힐라c");
                countList.add("헬 진힐라c");
                break;
            case 5:
                bossName = "더스크";
                clearKeyValue = "dusk_clear";
                canTimeKeyValue = "dusk_can_time";
                countList.add("노말 더스크c");
                countList.add("카오스 더스크c");
                countList.add("헬 더스크c");
                break;
            case 6:
                bossName = "듄켈";
                clearKeyValue = "dunkel_clear";
                canTimeKeyValue = "dunkel_can_time";
                countList.add("노말 듄켈c");
                countList.add("하드 듄켈c");
                countList.add("헬 듄켈c");
                break;
            case 7:
                bossName = "파풀라투스";
                clearKeyValue = "chaos_papulatus_clear";
                canTimeKeyValue = "papulatus_can_time";
                countList.add("노말 파풀라투스c");
                countList.add("하드 파풀라투스c");
                break;
            case 8:
                bossName = "세렌";
                clearKeyValue = "seren_clear";
                canTimeKeyValue = "seren_can_time";
                countList.add("노말 세렌c");
                countList.add("하드 세렌c");
                countList.add("헬 세렌c");
                break;
            case 9:
                bossName = "가디언 엔젤 슬라임";
                clearKeyValue = "guardian_angel_slime_clear";
                canTimeKeyValue = "guardian_angel_slime_can_time";
                countList.add("노말 가디언 엔젤 슬라임c");
                countList.add("하드 가디언 엔젤 슬라임c");
                countList.add("헬 가디언 엔젤 슬라임c");
                break;
            case 10:
                bossName = "헬 스우";
                clearKeyValue = "swoo_clear";
                canTimeKeyValue = "swoo_can_time";
                countList.add("노말 스우c");
                countList.add("하드 스우c");
                countList.add("헬 스우c");
                break;
            case 11:
                bossName = "헬 데미안";
                clearKeyValue = "demian_clear";
                canTimeKeyValue = "demian_can_time";
                countList.add("노말 데미안c");
                countList.add("하드 데미안c");
                countList.add("헬 데미안c");
                break;
            case 12:
                bossName = "헬 루시드";
                clearKeyValue = "lucid_clear";
                canTimeKeyValue = "lucid_can_time";
                countList.add("노말 루시드c");
                countList.add("하드 루시드c");
                countList.add("헬 루시드c");
                break;
            case 13:
                bossName = "헬 윌";
                clearKeyValue = "will_clear";
                canTimeKeyValue = "will_can_time";
                countList.add("노말 윌c");
                countList.add("하드 윌c");
                countList.add("헬 윌c");
                break;
        }
        int qid = 1234569;
        if (v1 < 10) {
            /*
             * if (v1 == 5 || v1 == 6) {
             * qid = 1234589;
             * }
             * if (v1 == 8) {
             * qid = 39932;
             * boolean check = getPlayer().getOneInfoQuestInteger(qid, "clear") == 1;
             * if (!check) {
             * self.say(bossName + "의 처치기록이 없어서 사용할 수 없습니다.");
             * return;
             * }
             * } else {
             * boolean check = getPlayer().getOneInfoQuestInteger(qid, clearKeyValue) == 1;
             * if (!check) {
             * self.say(bossName + "의 처치기록이 없어서 사용할 수 없습니다.");
             * return;
             * }
             * }
             */
            if (self.askYesNo(bossName
                    + "의 보스 입장 횟수 및 클리어 횟수를 초기화하시겠습니까?\r\n\r\n#e#r헬 모드의 입장횟수는 초기화 되나, 클리어 횟수는 초기화되지 않습니다.") == 1) {
                if (target.exchange(2432098, -1) == 1) {
                    boolean downSingle = false;
                    boolean downMulti = false;
                    if (v1 == 8) {
                        getPlayer().updateOneInfo(39932, "clear", "");
                        if (!DBConfig.isGanglim) {
                            int trycounts = getPlayer().getOneInfoQuestInteger(39932, "clear_single");
                            if (trycounts > 0) {
                                downSingle = true;
                                getPlayer().updateOneInfo(39932, "clear_single", String.valueOf(trycounts - 1));
                            }
                            int trycountm = getPlayer().getOneInfoQuestInteger(39932, "clear_multi");
                            if (trycountm > 0) {
                                downMulti = true;
                                getPlayer().updateOneInfo(39932, "clear_multi", String.valueOf(trycountm - 1));
                            }
                        }
                        getPlayer().updateOneInfo(39932, "enter", "");
                        getPlayer().updateOneInfo(1234569, canTimeKeyValue, "");
                    }
                    if (v1 == 5 || v1 == 6) {
                        getPlayer().updateOneInfo(1234589, clearKeyValue, "");
                        if (!DBConfig.isGanglim) {
                            int trycounts = getPlayer().getOneInfoQuestInteger(1234589, clearKeyValue + "_single");
                            if (trycounts > 0) {
                                downSingle = true;
                                getPlayer().updateOneInfo(1234589, clearKeyValue + "_single",
                                        String.valueOf(trycounts - 1));
                            }
                            int trycountm = getPlayer().getOneInfoQuestInteger(1234589, clearKeyValue + "_multi");
                            if (trycountm > 0) {
                                downMulti = true;
                                getPlayer().updateOneInfo(1234589, clearKeyValue + "_multi",
                                        String.valueOf(trycountm - 1));
                            }
                        }
                        getPlayer().updateOneInfo(1234569, canTimeKeyValue, "");
                    } else {
                        if (DBConfig.isGanglim) {
                            getPlayer().updateOneInfo(qid, clearKeyValue, "");
                        } else {
                            getPlayer().updateOneInfo(qid, clearKeyValue, "");
                            int trycounts = getPlayer().getOneInfoQuestInteger(qid, clearKeyValue + "_single");
                            if (trycounts > 0) {
                                downSingle = true;
                                getPlayer().updateOneInfo(qid, clearKeyValue + "_single",
                                        String.valueOf(trycounts - 1));
                            }
                            int trycountm = getPlayer().getOneInfoQuestInteger(qid, clearKeyValue + "_multi");
                            if (trycountm > 0) {
                                downMulti = true;
                                getPlayer().updateOneInfo(qid, clearKeyValue + "_multi",
                                        String.valueOf(trycountm - 1));
                            }
                        }
                        if (!canTimeKeyValue.isEmpty()) {
                            getPlayer().updateOneInfo(qid, canTimeKeyValue, "");
                        }
                    }
                    for (String c : countList) {
                        getPlayer().CountClear(c);
                    }
                    String infoSingle = downSingle ? "싱글 1회 " : "";
                    String infoMulti = (downSingle ? ", " : "") + (downMulti ? "멀티 1회" : "");
                    self.say(bossName + "의 보스 입장 횟수 및 클리어 횟수가" + infoSingle + infoMulti + " 차감되었습니다.");

                    StringBuilder sb = new StringBuilder("보스 입장 초기화 (아이템 : ");
                    sb.append(2432098);
                    sb.append(", 초기화 보스 : ");
                    sb.append(bossName);
                    sb.append(")");
                    LoggingManager.putLog(new ConsumeLog(getPlayer(), 2432098, sb));
                }
            }
        } else {
            boolean check = getPlayer().getOneInfoQuestInteger(1234569, "hell_" + clearKeyValue) == 1;
            /*
             * if (!check) {
             * self.say(bossName + "의 처치기록이 없어서 사용할 수 없습니다.");
             * return;
             * }
             */

            int hbc = getPlayer().getOneInfoQuestInteger(1234569, "hell_boss_count");
            if (hbc >= 5 && !getPlayer().isGM()) {
                self.say("금일 헬 모드 클리어 횟수를 5번 초기화하여 더 이상 초기화할 수 없습니다. 헬 모드 초기화 횟수는 매일 자정에 초기화됩니다.");
                return;
            }
            if (self.askYesNo(bossName
                    + "의 보스 입장 횟수 및 클리어 횟수를 초기화하시겠습니까?\r\n\r\n#e#r헬 모드의 입장횟수는 초기화 되나, 클리어 횟수는 초기화되지 않습니다.") == 1) {
                if (target.exchange(2432098, -1) == 1) {
                    getPlayer().updateOneInfo(qid, clearKeyValue, "");
                    getPlayer().updateOneInfo(qid, "hell_" + clearKeyValue, "");
                    getPlayer().updateOneInfo(1234569, "hell_boss_count",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1234569, "hell_boss_count") + 1));
                    if (!canTimeKeyValue.isEmpty()) {
                        getPlayer().updateOneInfo(qid, canTimeKeyValue, "");
                    }
                    for (String c : countList) {
                        getPlayer().CountClear(c);
                    }
                    self.say(bossName + "의 보스 입장 횟수 및 클리어 횟수가 초기화되었습니다.");

                    StringBuilder sb = new StringBuilder("보스 입장 초기화 [헬 모드] (아이템 : ");
                    sb.append(2432098);
                    sb.append(", 초기화 보스 : ");
                    sb.append(bossName);
                    sb.append(")");
                    LoggingManager.putLog(new ConsumeLog(getPlayer(), 2432098, sb));
                }
            }
        }
    }

    public void cash_5680520() {
        initNPC(MapleLifeFactory.getNPC(9062000));
        if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1) {
            self.say("소비 아이템 슬롯을 1칸이상 비운뒤 다시 시도해주세요.");
            return;
        }
        if (target.exchange(5680520, -1, 2436577, 1) > 0) {
            self.sayOk("교환이 완료되었습니다.");
        }
    }

    public void consume_2436577() {
        initNPC(MapleLifeFactory.getNPC(9062000));
        if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1) {
            self.say("소비 아이템 슬롯을 1칸이상 비운뒤 다시 시도해주세요.");
            return;
        }
        Set keys = MonsterCollection.mobByName.keySet();
        String key = (String) Randomizer.next(Arrays.asList(keys.toArray()));
        int type = MonsterCollection.mobByName.get(key).getType();
        while (type == 2 || type == 6 || type >= 7) {
            key = (String) Randomizer.next(Arrays.asList(keys.toArray()));
            type = MonsterCollection.mobByName.get(key).getType();
        }
        MonsterCollection.CollectionMobData data = MonsterCollection.mobByName.get(key);
        if (target.exchange(itemID, -1) > 0) {
            if (!MonsterCollection.checkIfMobOnCollection(getPlayer(), data)) {
                MonsterCollection.setMobOnCollection(getPlayer(), data);
            } else {
                if (target.exchange(2048746, 1) > 0) {
                    self.sayOk(
                            "#h0#님께는 조금 더 특별한 보상을 지급해 드렸어요!\r\n소비창을 확인해 보세요!\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#e#b#i2048746# #t2048746#");
                }
            }
        }
    }

    public void consume_2434941() {
        initNPC(MapleLifeFactory.getNPC(9062000));
        if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1) {
            self.say("소비 아이템 슬롯을 1칸이상 비운뒤 다시 시도해주세요.");
            return;
        }
        Set keys = MonsterCollection.mobByName.keySet();
        String key = (String) Randomizer.next(Arrays.asList(keys.toArray()));
        int starRank = MonsterCollection.mobByName.get(key).getStarRank();
        while (starRank < 3) {
            key = (String) Randomizer.next(Arrays.asList(keys.toArray()));
            starRank = MonsterCollection.mobByName.get(key).getStarRank();
        }
        MonsterCollection.CollectionMobData data = MonsterCollection.mobByName.get(key);
        if (target.exchange(itemID, -1) > 0) {
            if (!MonsterCollection.checkIfMobOnCollection(getPlayer(), data)) {
                MonsterCollection.setMobOnCollection(getPlayer(), data);
            } else {
                if (target.exchange(2048746, 1) > 0) {
                    self.sayOk(
                            "#h0#님께는 조금 더 특별한 보상을 지급해 드렸어요!\r\n소비창을 확인해 보세요!\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#e#b#i2048746# #t2048746#");
                }
            }
        }
    }

    public void consume_2434942() {
        initNPC(MapleLifeFactory.getNPC(9062000));
        if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1) {
            self.say("소비 아이템 슬롯을 1칸이상 비운뒤 다시 시도해주세요.");
            return;
        }
        Set keys = MonsterCollection.mobByName.keySet();
        String key = (String) Randomizer.next(Arrays.asList(keys.toArray()));
        int type = MonsterCollection.mobByName.get(key).getType();
        while (type != 0) {
            key = (String) Randomizer.next(Arrays.asList(keys.toArray()));
            type = MonsterCollection.mobByName.get(key).getType();
        }
        MonsterCollection.CollectionMobData data = MonsterCollection.mobByName.get(key);
        if (target.exchange(itemID, -1) > 0) {
            if (!MonsterCollection.checkIfMobOnCollection(getPlayer(), data)) {
                MonsterCollection.setMobOnCollection(getPlayer(), data);
            } else {
                self.sayOk(String.format("%s몬스터가 나왔지만 이미 가지고 있는 몬스터라 등록되지 않았습니다.", key));
            }
        }
    }

    public void consume_2434943() {
        initNPC(MapleLifeFactory.getNPC(9062000));
        if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1) {
            self.say("소비 아이템 슬롯을 1칸이상 비운뒤 다시 시도해주세요.");
            return;
        }

        var csi = Randomizer.next(MonsterCollection.mobCollections.get(0).getSubIndexList());
        var randg = Randomizer.next(csi.getGroup());
        Integer randm = Randomizer.next(randg.getMobs());
        String key = MapleLifeFactory.getMonster(randm).getStats().getName();
        int type = MonsterCollection.mobByName.get(key).getType();
        while (type == 2 || type == 6 || type >= 7) {
            csi = Randomizer.next(MonsterCollection.mobCollections.get(0).getSubIndexList());
            randg = Randomizer.next(csi.getGroup());
            randm = Randomizer.next(randg.getMobs());
            key = MapleLifeFactory.getMonster(randm).getStats().getName();
            type = MonsterCollection.mobByName.get(key).getType();
        }
        MonsterCollection.CollectionMobData data = MonsterCollection.mobByName.get(key);
        if (target.exchange(itemID, -1) > 0) {
            if (!MonsterCollection.checkIfMobOnCollection(getPlayer(), data)) {
                MonsterCollection.setMobOnCollection(getPlayer(), data);
            } else {
                self.sayOk(String.format("%s몬스터가 나왔지만 이미 가지고 있는 몬스터라 등록되지 않았습니다.", key));
            }
        }
    }

    public void consume_2434958() {
        monsterMoMong("아이스 골렘");
    }

    public void consume_2434959() {
        monsterMoMong("총리대신");
    }

    public void consume_2434971() {
        monsterMoMong("포이즌 플라워");
    }

    public void consume_2435366() {
        monsterMoMong("혼테일");
    }

    public void consume_2435367() {
        monsterMoMong("카오스 혼테일");
    }

    public void consume_2435368() {
        List<String> names = Arrays.asList("단지", "삼단지", "도라지", "늙은 도라지", "거대 도라지");
        monsterMoMong(Randomizer.next(names));
    }

    public void consume_2437618() {
        monsterMoMong("싸구려 앰프");
    }

    public void consume_2437619() {
        monsterMoMong("고급 앰프");
    }

    private void monsterMoMong(String name) {
        initNPC(MapleLifeFactory.getNPC(9062000));
        if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1) {
            self.say("소비 아이템 슬롯을 1칸이상 비운뒤 다시 시도해주세요.");
            return;
        }
        var data = MonsterCollection.mobByName.getOrDefault(name, null);
        if (data == null) {
            self.sayOk("알 수 없는 오류로 실패했습니다.");
            return;
        }
        if (target.exchange(itemID, -1) > 0) {
            if (!MonsterCollection.checkIfMobOnCollection(getPlayer(), data)) {
                MonsterCollection.setMobOnCollection(getPlayer(), data);
            } else {
                if (target.exchange(2048745, 1) > 0) {
                    self.sayOk(
                            "#h0#님께는 조금 더 특별한 보상을 지급해 드렸어요!\r\n소비창을 확인해 보세요!\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#e#b#i2048745# #t2048745#");
                }
            }
        }
    }

    public void consume_2434929() {
        List<Pair<Integer, Integer>> rewards = Arrays.asList(new Pair<>(4001832, 3000));
        adventure_box_reward(Randomizer.next(rewards));
    }

    public void consume_2434930() {
        List<Pair<Integer, Integer>> rewards = Arrays.asList(new Pair<>(4001832, 6000));
        adventure_box_reward(Randomizer.next(rewards));
    }

    public void consume_2434931() {
        List<Pair<Integer, Integer>> rewards = Arrays.asList(new Pair<>(2048745, 3));
        adventure_box_reward(Randomizer.next(rewards));
    }

    public void consume_2434932() {
        List<Pair<Integer, Integer>> rewards = Arrays.asList(new Pair<>(2048746, 3));
        adventure_box_reward(Randomizer.next(rewards));
    }

    private void adventure_box_reward(Pair<Integer, Integer> reward) {
        initNPC(MapleLifeFactory.getNPC(9062000));
        if (getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 1
                || getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 3) {
            self.say("기타 슬롯 1칸 소비 슬롯 3칸 이상을 비운뒤 다시 시도해주세요.");
            return;
        }
        if (target.exchange(itemID, -1, reward.left, reward.right) > 0) {
            self.sayOk(String.format("보상을 지급해 드렸어요!\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#e#b#i%d# #t%d# %d개",
                    reward.left, reward.left, reward.right));
        }
    }

    public void consume_2632808() {
        initNPC(MapleLifeFactory.getNPC(9062000));
        int qty = getPlayer().getItemQuantity(2632808, false);
        qty = Math.min(30000, qty);
        if (target.exchange(2632808, -qty) > 0) {
            if (DBConfig.isGanglim) {
                int currentstone = getPlayer().getOneInfoQuestInteger(100711, "point");
                getPlayer().updateOneInfo(100711, "point", String.valueOf(currentstone + qty));
            } else {
                getPlayer().gainStackEventGauge(0, qty, false);
            }
            self.sayOk(String.format("보상을 지급해 드렸어요!\r\n\r\n#fUI/UIWindow2.img/QuestIcon/4/0#\r\n#e#b#i%d# #t%d# %d개",
                    2632905, 2632905, qty));
        }
    }

    public void ep1Reset() {
        initNPC(MapleLifeFactory.getNPC(9062000));
        final StringBuilder v0 = new StringBuilder(
                "어떤 훈장 아이템에 강화권을 사용하시겠어요?\r\n사용한 후에는 되돌릴 수 없으니 신중하게 선택해 주세요.\r\n\r\n#b※ 강화권 사용 시 #e올스탯 350, 공/마 250#n 옵션이 적용되며, 훈장마다 최대 10회까지 사용 가능합니다.\r\n\r\n");
        List<Item> itemList = new ArrayList<>();
        getPlayer().getInventory(MapleInventoryType.EQUIP).list().forEach(item -> {
            if (!DBConfig.isGanglim) {
                if (item.getItemId() / 10000 == 114) {
                    if (!GameConstants.isJinEndlessMedal(item.getItemId())) {
                        itemList.add(item);
                    }
                }
            } else {
                if (item.getItemId() / 10000 == 114) {
                    itemList.add(item);
                }
            }
        });
        if (itemList.isEmpty()) {
            v0.append("장비창에 보유중인 훈장이 없습니다.");
            self.say(v0.toString());
            return;
        } else {
            itemList.forEach(item -> {
                v0.append("#L" + item.getPosition() + "##i" + item.getItemId() + "# #z" + item.getItemId() + "##l\r\n");
            });
        }
        int v1 = self.askMenu(v0.toString());
        Item pick = getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short) v1);
        if (pick == null) {
            self.say("해당 아이템을 발견하지 못 했습니다.");
            return;
        }
        String owner = pick.getOwner();
        int level = 0;
        if (owner != null && !owner.isEmpty()) {
            level = Integer.parseInt(owner.split("성")[0]);
        }

        String v2 = "#b#i" + pick.getItemId() + "# #z" + pick.getItemId()
                + "##k\r\n\r\n위 아이템에 강화권을 사용하시겠어요? 사용 시 #e올스탯 350, 공/마 250#n 옵션이 적용됩니다.\r\n\r\n";
        v2 += "#e현재 적용된 강화 : +" + level;
        if (1 == self.askYesNo(v2)) {
            if (level >= 10) { // 10성까지 가능
                self.say("해당 훈장에는 더 이상 사용할 수 없습니다.");
                return;
            }
            if (exchange(2432096, -1) > 0) {
                Equip equip = (Equip) pick;
                equip.setOwner(++level + "성");
                equip.setStr((short) (equip.getStr() + 350));
                equip.setDex((short) (equip.getDex() + 350));
                equip.setInt((short) (equip.getInt() + 350));
                equip.setLuk((short) (equip.getLuk() + 350));
                equip.setMatk((short) (equip.getMatk() + 250));
                equip.setWatk((short) (equip.getWatk() + 250));

                getPlayer().send(CWvsContext.InventoryPacket.updateInventoryItem(MapleInventoryType.EQUIP, equip, false,
                        getPlayer()));

                objects.utils.FileoutputUtil.log("./TextLog/MedalEnchant.txt", "훈장 강화 사용 (아이템ID : " + equip.getItemId()
                        + ", 레벨 : " + level + ", 사용자 : " + getPlayer().getName() + ")\r\n");
                self.say("강화권 적용이 완료되었습니다.");
            }
        }
    }

    public void consume_2434560() {
        if (!DBConfig.isGanglim) {
            final int tradeitem = 2434560;
            if (!getPlayer().haveItem(tradeitem))
                return;
            if (getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 1) {
                self.sayOk("캐시 인벤토리에 빈 공간이 없습니다.");
                return;
            } else {
                int qty = getPlayer().getItemQuantity(tradeitem, false);
                int tradeQty = self.askNumber("몇개나 교환할까?", 1, 1, Math.min(100, qty),
                        ScriptMessageFlag.NpcReplacedByUser);
                if (tradeQty > qty || tradeQty <= 0)
                    return; // 패킷핵
                if (target.exchange(tradeitem, -tradeQty) > 0) {
                    target.exchange(5062010, tradeQty);
                    self.sayOk("교환이 완료되었습니다.");
                }
            }
        }
    }

    public void consume_2631879() {
        if (!DBConfig.isGanglim) {
            final int tradeitem = 2631879;
            if (!getPlayer().haveItem(tradeitem))
                return;
            if (getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 1) {
                self.sayOk("캐시 인벤토리에 빈 공간이 없습니다.");
                return;
            } else {
                int qty = getPlayer().getItemQuantity(tradeitem, false);
                int tradeQty = self.askNumber("몇개나 교환할까?", 1, 1, Math.min(100, qty),
                        ScriptMessageFlag.NpcReplacedByUser);
                if (tradeQty > qty || tradeQty <= 0)
                    return; // 패킷핵
                if (target.exchange(tradeitem, -tradeQty) > 0) {
                    target.exchange(5062500, tradeQty);
                    self.sayOk("교환이 완료되었습니다.");
                }
            }
        }
    }

    public void consume_2439259() {
        if (!DBConfig.isGanglim) {
            final int tradeitem = 2439259;
            if (!getPlayer().haveItem(tradeitem))
                return;
            if (getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 1) {
                self.sayOk("캐시 인벤토리에 빈 공간이 없습니다.");
                return;
            } else {
                int qty = getPlayer().getItemQuantity(tradeitem, false);
                int tradeQty = self.askNumber("몇개나 교환할까?", 1, 1, Math.min(100, qty),
                        ScriptMessageFlag.NpcReplacedByUser);
                if (tradeQty > qty || tradeQty <= 0)
                    return; // 패킷핵
                if (target.exchange(tradeitem, -tradeQty) > 0) {
                    target.exchange(5062503, tradeQty);
                    self.sayOk("교환이 완료되었습니다.");
                }
            }
        }
    }

    public void consume_2432122() {
        unboxingStat(2432122);
    }

    public void consume_2432123() {
        unboxingStat(2432123);
    }

    public void consume_2432124() {
        unboxingStat(2432124);
    }

    public void consume_2432125() {
        unboxingStat(2432125);
    }

    public void consume_2432160() {
        unboxingStat(2432160);
    }

    public void consume_2432161() {
        unboxingStat(2432161);
    }

    public void consume_2432162() {
        unboxingStat(2432162);
    }

    public void consume_2432163() {
        unboxingStat(2432163);
    }

    public void unboxingStat(int itemID) {
        initNPC(MapleLifeFactory.getNPC(2500000));

        if (itemID >= 2432160 && itemID <= 2432163) {
            self.say("등급에 맞는 해방 열쇠를 사용하면 봉인된 상자를 개방할 수 있을 것 같다.", ScriptMessageFlag.Self);
            return;
        }
        int needItem = 0;
        if (itemID == 2432122) { // Grade.1
            needItem = 2432160;
            if (0 >= getPlayer().getItemQuantity(2432160, false)) {
                self.say("#b#i2432160# #z2432160##k이(가) 없으면 아무짝에 쓸모가 없다.", ScriptMessageFlag.Self);
                return;
            }
        } else if (itemID == 2432123) { // Grade.2
            needItem = 2432161;
            if (0 >= getPlayer().getItemQuantity(2432161, false)) {
                self.say("#b#i2432161# #z2432161##k이(가) 없으면 아무짝에 쓸모가 없다.", ScriptMessageFlag.Self);
                return;
            }
        } else if (itemID == 2432124) { // Grade.3
            needItem = 2432162;
            if (0 >= getPlayer().getItemQuantity(2432162, false)) {
                self.say("#b#i2432162# #z2432162##k이(가) 없으면 아무짝에 쓸모가 없다.", ScriptMessageFlag.Self);
                return;
            }
        } else if (itemID == 2432125) { // Grade.4
            needItem = 2432163;
            if (0 >= getPlayer().getItemQuantity(2432163, false)) {
                self.say("#b#i2432163# #z2432163##k이(가) 없으면 아무짝에 쓸모가 없다.", ScriptMessageFlag.Self);
                return;
            }
        }
        if (needItem == 0) {
            return;
        }
        String v0 = "굳게 잠겨 있던 상자에 열쇠를 끼우자, 몸에서 이질적인 힘이 느껴지기 시작한다.\r\n지금까지 깨닫지 못한 잠재된 힘.\r\n\r\n이건……\r\n\r\n";
        if (itemID == 2432122) { // Grade.1
            v0 += "#b#L0##eSTR +2#n이다.#l\r\n";
            v0 += "#b#L1##eDEX +2#n이다.#l\r\n";
            v0 += "#b#L2##eINT +2#n이다.#l\r\n";
            v0 += "#b#L3##eLUK +2#n이다.#l\r\n";
            v0 += "\r\n\r\n#r※ 선택한 옵션이 증가됩니다.";
            int v1 = self.askMenu(v0, ScriptMessageFlag.Self);
            if (target.exchange(itemID, -1, needItem, -1) > 0) {
                if (v1 == 0) {
                    getPlayer().updateOneInfo(1237777, "str",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1237777, "str") + 2));
                } else if (v1 == 1) {
                    getPlayer().updateOneInfo(1237777, "dex",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1237777, "dex") + 2));
                } else if (v1 == 2) {
                    getPlayer().updateOneInfo(1237777, "int_",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1237777, "int_") + 2));
                } else if (v1 == 3) {
                    getPlayer().updateOneInfo(1237777, "luk",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1237777, "luk") + 2));
                }
                String v2 = "#e[현재까지 해방된 힘]#n\r\n\r\n#e#r";
                v2 += displayLiberationStats();
                self.say(v2, ScriptMessageFlag.Self);
                // objects.utils.FileoutputUtil.log("./TextLog/LiberationStat.txt", "봉인 해방
                // (Grade.1, rand : " + v1 + ", 사용자 : " + getPlayer().getName() + ")\r\n");

                StringBuilder sb = new StringBuilder(
                        "봉인 해방 (Grade.1, rand : " + v1 + ", 사용자 : " + getPlayer().getName() + ")");
                LoggingManager.putLog(new ConsumeLog(getPlayer(), itemID, sb));
            }
        } else if (itemID == 2432123) { // Grade.2
            v0 += "#b#L0##eSTR +20#n이다.#l\r\n";
            v0 += "#b#L1##eDEX +20#n이다.#l\r\n";
            v0 += "#b#L2##eINT +20#n이다.#l\r\n";
            v0 += "#b#L3##eLUK +20#n이다.#l\r\n";
            v0 += "\r\n\r\n#r※ 선택한 옵션이 증가됩니다.";
            int v1 = self.askMenu(v0, ScriptMessageFlag.Self);
            if (target.exchange(itemID, -1, needItem, -1) > 0) {
                if (v1 == 0) {
                    getPlayer().updateOneInfo(1237777, "str",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1237777, "str") + 20));
                } else if (v1 == 1) {
                    getPlayer().updateOneInfo(1237777, "dex",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1237777, "dex") + 20));
                } else if (v1 == 2) {
                    getPlayer().updateOneInfo(1237777, "int_",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1237777, "int_") + 20));
                } else if (v1 == 3) {
                    getPlayer().updateOneInfo(1237777, "luk",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1237777, "luk") + 20));
                }
                String v2 = "#e[현재까지 해방된 힘]#n\r\n\r\n#e#r";
                v2 += displayLiberationStats();
                self.say(v2, ScriptMessageFlag.Self);
                // objects.utils.FileoutputUtil.log("./TextLog/LiberationStat.txt", "봉인 해방
                // (Grade.2, rand : " + v1 + ", 사용자 : " + getPlayer().getName() + ")\r\n");

                StringBuilder sb = new StringBuilder(
                        "봉인 해방 (Grade.2, rand : " + v1 + ", 사용자 : " + getPlayer().getName() + ")");
                LoggingManager.putLog(new ConsumeLog(getPlayer(), itemID, sb));
            }
        } else if (itemID == 2432124) { // Grade.3
            if (target.exchange(itemID, -1, needItem, -1) > 0) {
                int rand = Randomizer.rand(0, 3);

                if (rand == 0) {
                    getPlayer().updateOneInfo(1237777, "str",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1237777, "str") + 50));
                } else if (rand == 1) {
                    getPlayer().updateOneInfo(1237777, "dex",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1237777, "dex") + 50));
                } else if (rand == 2) {
                    getPlayer().updateOneInfo(1237777, "int_",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1237777, "int_") + 50));
                } else if (rand == 3) {
                    getPlayer().updateOneInfo(1237777, "luk",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1237777, "luk") + 50));
                }
                String[] stats = new String[] {
                        "STR", "DEX", "INT", "LUK"
                };
                String v2 = String.format("그래. 이건 #b#e" + stats[rand] + "#n이다.\r\n%s만큼 증가한 게 느껴진다.", "+50");
                self.say(v2, ScriptMessageFlag.Self);
                // objects.utils.FileoutputUtil.log("./TextLog/LiberationStat.txt", "봉인 해방
                // (Grade.3, rand : " + rand + ", 사용자 : " + getPlayer().getName() + ")\r\n");

                StringBuilder sb = new StringBuilder(
                        "봉인 해방 (Grade.3, rand : " + rand + ", 사용자 : " + getPlayer().getName() + ")");
                LoggingManager.putLog(new ConsumeLog(getPlayer(), itemID, sb));
            }
        } else if (itemID == 2432125) { // Grade.4
            if (target.exchange(itemID, -1, needItem, -1) > 0) {
                int rand = Randomizer.rand(0, 4);

                if (rand == 0) {
                    getPlayer().updateOneInfo(1237777, "pad",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1237777, "pad") + 2));
                } else if (rand == 1) {
                    getPlayer().updateOneInfo(1237777, "mad",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1237777, "mad") + 2));
                } else if (rand == 2) {
                    getPlayer().updateOneInfo(1237777, "bdr",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1237777, "bdr") + 2));
                } else if (rand == 3) {
                    getPlayer().updateOneInfo(1237777, "imdr",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1237777, "imdr") + 2));
                } else if (rand == 4) {
                    getPlayer().updateOneInfo(1237777, "all_stat_r",
                            String.valueOf(getPlayer().getOneInfoQuestInteger(1237777, "all_stat_r") + 5));
                }
                String[] stats = new String[] {
                        "공격력", "마력", "보스 공격 시 데미지", "몬스터 방어율 무시", "올스탯"
                };
                String v2 = String.format("그래. 이건 #b#e" + stats[rand] + "#n이다.\r\n%s만큼 증가한 게 느껴진다.",
                        (rand == 4 ? "5%" : "2%"));
                self.say(v2, ScriptMessageFlag.Self);
                // objects.utils.FileoutputUtil.log("./TextLog/LiberationStat.txt", "봉인 해방
                // (Grade.4, rand : " + rand + ", 사용자 : " + getPlayer().getName() + ")\r\n");

                StringBuilder sb = new StringBuilder(
                        "봉인 해방 (Grade.4, rand : " + rand + ", 사용자 : " + getPlayer().getName() + ")");
                LoggingManager.putLog(new ConsumeLog(getPlayer(), itemID, sb));
            }
        }
        getPlayer().checkLiberationStats();
    }

    public String displayLiberationStats() {
        String ret = "";

        int str = getPlayer().getOneInfoQuestInteger(1237777, "str");
        int dex = getPlayer().getOneInfoQuestInteger(1237777, "dex");
        int int_ = getPlayer().getOneInfoQuestInteger(1237777, "int_");
        int luk = getPlayer().getOneInfoQuestInteger(1237777, "luk");
        int pad = getPlayer().getOneInfoQuestInteger(1237777, "pad");
        int mad = getPlayer().getOneInfoQuestInteger(1237777, "mad");
        int bdr = getPlayer().getOneInfoQuestInteger(1237777, "bdr");
        int imdr = getPlayer().getOneInfoQuestInteger(1237777, "imdr");
        int allStatR = getPlayer().getOneInfoQuestInteger(1237777, "all_stat_r");
        int totalTE = getPlayer().getTotalTranscendenceEnchant();
        double scale = 1.0;
        if (totalTE >= 72 && totalTE < 80) {
            scale = 1.2;
        } else if (totalTE >= 80) {
            scale = 1.5;
        }
        if (scale > 1.0) {
            str *= scale;
            dex *= scale;
            int_ *= scale;
            luk *= scale;
        }
        ret += String.format("STR +%d\r\n", str);
        ret += String.format("DEX +%d\r\n", dex);
        ret += String.format("INT +%d\r\n", int_);
        ret += String.format("LUK +%d\r\n", luk);
        ret += String.format("올스탯 +%s\r\n", allStatR + "%");
        ret += String.format("공격력 +%s\r\n", pad + "%");
        ret += String.format("마력 +%s\r\n", mad + "%");
        ret += String.format("보스 공격 시 데미지 +%s\r\n", bdr + "%");
        ret += String.format("몬스터 방어율 무시 +%s\r\n", imdr + "%");
        if (scale > 1.0) {
            ret += "\r\n#n위 옵션은 옵션의 * " + scale + "배가 적용된 옵션입니다. (초월 강화 보너스)";
        }

        return ret;
    }

    public void pickStatItem(boolean hongbo) {
        NumberFormat nf = NumberFormat.getInstance();
        if (hongbo) {
            if (getPlayer().getHongboPoint() < 6000) {
                self.say("홍보 포인트가 부족한 것 같군요.");
                return;
            }
        } else {
            if (getPlayer().getRealCash() < 2000) {
                self.say("진:眞 포인트가 부족한 것 같군요.");
                return;
            }
        }
        if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 3) {
            self.say("#b소비 인벤토리#k 슬롯이 부족합니다.");
            return;
        }

        if (1 == self.askYesNo(String.format(
                "정말 #b" + (hongbo ? "6,000" : "2,000")
                        + " %s 포인트#k를 사용하여 #b'봉인된 상자' 또는 '해방의 열쇠'를 뽑으시겠어요?\r\n\r\n#e#k보유중인 포인트 : %s",
                hongbo ? "홍보" : "진:眞",
                hongbo ? nf.format(getPlayer().getHongboPoint()) : nf.format(getPlayer().getRealCash())))) {
            int rand = Randomizer.rand(0, 100);
            int reward = 0;
            if (rand < 60) {
                if (Randomizer.isSuccess(50)) {
                    reward = 2432122;
                } else {
                    reward = 2432160;
                }
            } else if (rand >= 60 && rand < 90) {
                if (Randomizer.isSuccess(50)) {
                    reward = 2432123;
                } else {
                    reward = 2432161;
                }
            } else if (rand >= 90 && rand < 98) {
                if (Randomizer.isSuccess(50)) {
                    reward = 2432124;
                } else {
                    reward = 2432162;
                }
            } else if (rand >= 98) {
                if (Randomizer.isSuccess(50)) {
                    reward = 2432125;
                } else {
                    reward = 2432163;
                }
            }

            if (reward != 0) {
                if (target.exchange(reward, 1) > 0) {
                    if (hongbo) {
                        getPlayer().gainHongboPoint(-6000, true);
                    } else {
                        getPlayer().gainRealCash(-2000, true);
                    }

                    StringBuilder sb = new StringBuilder("봉인 스탯 뽑기 결과 (뽑은 아이템ID : " + reward + ", 사용재화 : "
                            + (hongbo ? "홍보 포인트" : "진:眞 포인트") + ", 사용자 : " + getPlayer().getName() + ")");
                    LoggingManager.putLog(new ConsumeLog(getPlayer(), 1, sb));

                    String v0 = "#b#i" + reward + "# #z" + reward + "# 1개를 획득했습니다.\r\n\r\n#k#e보유중인 포인트 : "
                            + (hongbo ? nf.format(getPlayer().getHongboPoint()) : nf.format(getPlayer().getRealCash()))
                            + "\r\n#n#b#L0#한 번 더 뽑을게요.#l\r\n#L1#대화를 종료한다.#l";
                    int v1 = self.askMenu(v0);
                    if (v1 == 0) {
                        pickStatItem(hongbo);
                    }
                }
            }
        }
    }

    public void rita_library() {
        if (DBConfig.isGanglim) {
            return;
        }
        initNPC(MapleLifeFactory.getNPC(2500000));
        String v0 = "안녕하세요 #h0#님!\r\n메이플 월드에서 이상한 기운이 느껴지는 상자가 발견되었다는 소식 들으셨나요?\r\n\r\n";
        v0 += "소문에 의하면 어떤 방법으로도 상자는 열리지 않는다고 해요. 구멍에 맞는 열쇠를 찾아야 할 것 같은데……\r\n\r\n#b";
        v0 += "#L0#현재까지 해방한 힘을 확인하고 싶어.#l\r\n";
        v0 += "#L2#포인트를 사용하여 상자 또는 열쇠를 뽑고 싶어.#l\r\n";
        v0 += "#L1#상자에 관해서 조금 더 자세히 듣고 싶어.#l";
        int v1 = self.askMenu(v0);
        if (v1 == 0) {
            String v2 = "#e[현재까지 해방한 힘]#n\r\n\r\n#e#r";
            v2 += displayLiberationStats();
            v2 += "#n\r\n#b#L0#메뉴로 돌아간다.#l\r\n#L1#대화를 종료한다.#l";
            int v3 = self.askMenu(v2);
            if (v3 == 0) {
                rita_library();
            }
        } else if (v1 == 1) {
            String v2 = "#bGrade 해방의 열쇠#k로 #bGrade 봉인된 상자#k를 개방하면 등급에 맞는 힘을 해방할 수 있습니다.\r\n";
            v2 += "Grade 등급은 #e1, 2, 3, 4 총 네 가지#n로 구분되며 숫자가 높을수록 강한 힘이 봉인되어 있습니다.\r\n\r\n";
            v2 += "#bGrade 봉인된 상자#k와 #bGrade 해방의 열쇠#k는 일정 난이도 이상의 보스를 처치하거나,\r\n";
            v2 += "#r진:眞 포인트, 홍보 포인트#k 등의 재화를 소모하여 #e1~4 등급을 랜덤#n으로 획득할 수 있습니다.\r\n\r\n#b";
            v2 += "#L0#드롭하는 보스와 확률을 자세히 알고 싶어.#l\r\n";
            v2 += "#L1#소모하는 재화와 해방할 수 있는 힘을 자세히 알고 싶어.#l\r\n";
            int v3 = self.askMenu(v2);
            if (v3 == 0) {
                String v4 = "#e- 하드 검은 마법사\r\n";
                v4 += "- 하드 세렌\r\n";
                v4 += "- 카오스 칼로스\r\n\r\n#n#k";
                v4 += "이상의 보스에서 모두 드롭하며, 자세한 확률 정보는 홈페이지 확률표를 확인하시기 바랍니다.";
                v4 += "#n\r\n#b#L0#메뉴로 돌아간다.#l\r\n#L1#대화를 종료한다.#l";
                int v5 = self.askMenu(v4);
                if (v5 == 0) {
                    rita_library();
                }
            } else if (v3 == 1) {
                String v4 = "- #r진:眞 포인트 2,000#k을 소모하여 #bGrade.1~Grade.4 등급의 '봉인된 상자' 또는 '해방의 열쇠'#k를 획득할 수 있습니다.\r\n";
                v4 += "- #r홍보 포인트 6,000#k을 소모하여 #bGrade.1~Grade.4 등급의 '봉인된 상자' 또는 '해방의 열쇠'#k를 획득할 수 있습니다.\r\n\r\n";
                v4 += "봉인된 상자와 해방의 열쇠는 등급에 따라 획득 확률 및 해방 시 상승하는 스탯이 다릅니다.";
                self.say(v4);
                String v5 = "#eGrade.1 (60%) - 상자 30%, 열쇠 30%#n\r\n";
                v5 += "#bSTR, DEX, INT, LUK 중에서 선택한 스탯이 2만큼 증가합니다.#k\r\n";
                v5 += "#eGrade.2 (30%) - 상자 15%, 열쇠 15%#n\r\n";
                v5 += "#bSTR, DEX, INT, LUK 중에서 선택한 스탯이 20만큼 증가합니다.#k\r\n";
                v5 += "#eGrade.3 (8%) - 상자 4%, 열쇠 4%#n\r\n";
                v5 += "#bSTR, DEX, INT, LUK 중에서 랜덤한 스탯이 50만큼 증가합니다.#k\r\n";
                v5 += "#eGrade.4 (2%) - 상자 1%, 열쇠 1%#n\r\n";
                v5 += "#b공격력, 마력, 보스 공격 시 데미지, 몬스터 방어율 무시, 올스탯% 중에서 랜덤한 스탯이 2%만큼 증가합니다. (올스탯은 5%)\r\n";
                v5 += "#n\r\n#b#L0#메뉴로 돌아간다.#l\r\n#L1#대화를 종료한다.#l";
                int v6 = self.askMenu(v5);
                if (v6 == 0) {
                    rita_library();
                }
            }
        } else if (v1 == 2) {
            String v2 = "어떤 포인트를 사용하여 #b'봉인된 상자' 또는 '해방의 열쇠'#k를 뽑아보시겠어요?\r\n\r\n#b";
            v2 += "#L0##e진:眞 포인트#n를 사용하여 뽑겠습니다. (2,000 포인트)#l\r\n";
            v2 += "#L1##e홍보 포인트#n를 사용하여 뽑겠습니다. (6,000 포인트)#l\r\n";
            v2 += "#L2#대화를 종료한다.#l\r\n";
            int v3 = self.askMenu(v2);
            if (v3 == 0) {
                pickStatItem(false);
            } else if (v3 == 1) {
                pickStatItem(true);
            }
        }
    }

    public void consume_2435873() {
        if (DBConfig.isGanglim) {
            return;
        }
        int[] itemList = new int[] {
                1012632, 1022278, 1132308, 1162080, 1162081, 1162082, 1162083
        };
        unboxingItem(2435873, itemList);
    }

    public void consume_2435874() {
        if (DBConfig.isGanglim) {
            return;
        }
        int[] itemList = new int[] {
                1113306, 1032316, 1122430
        };
        unboxingItem(2435874, itemList);
    }

    public void consume_2435875() {
        if (DBConfig.isGanglim) {
            return;
        }
        int[] itemList = new int[] {
                1182285, 1190555, 1190556, 1190557, 1190558, 1190559
        };
        unboxingItem(2435875, itemList);
    }

    public void consume_2435876() {
        if (DBConfig.isGanglim) {
            return;
        }
        int[] itemList = new int[] {
                1012632, 1022278, 1132308, 1162080, 1162081, 1162082, 1162083, 1113306, 1032316, 1122430, 1182285,
                1190555, 1190556, 1190557, 1190558, 1190559
        };
        String v0 = "아래의 아이템 중 하나를 선택하여 획득할 수 있습니다. 어떤 아이템을 선택하시겠습니까?\r\n\r\n#b";
        for (int i = 0; i < itemList.length; ++i) {
            v0 += "#L" + i + "##i" + itemList[i] + "# #z" + itemList[i] + "##l\r\n";
        }
        int v1 = self.askMenu(v0);
        if (target.exchange(2435876, -1, itemList[v1], 1) > 0) {
            self.sayOk("#b#i" + itemList[v1] + "# #z" + itemList[v1] + "##k 1개를 획득하였습니다.");
        } else {
            self.sayOk("장비 인벤토리 공간을 확보하고 다시 시도해주시기 바랍니다.");
        }
    }

    public void unboxingItem(int consumeID, int[] itemList) {
        initNPC(MapleLifeFactory.getNPC(9010000));
        String v0 = "상자를 개봉하면 아래 아이템 중 하나를 랜덤으로 획득할 수 있습니다. 지금 바로 개봉해보시겠습니까?\r\n\r\n#b";
        for (int itemID : itemList) {
            v0 += "#i" + itemID + "# #z" + itemID + "#\r\n";
        }
        if (1 == self.askYesNo(v0)) {
            if (target.exchange(consumeID, -1) > 0) {
                int rand = Randomizer.rand(0, itemList.length - 1);
                int pick = itemList[rand];

                if (target.exchange(pick, 1) > 0) {
                    self.sayOk("#b#i" + pick + "# #z" + pick + "##k 1개를 획득하였습니다.");
                } else {
                    self.sayOk("장비 인벤토리 공간을 확보하고 다시 시도해주시기 바랍니다.");
                }
            } else {
                self.sayOk("알 수 없는 오류가 발생하였습니다.");
            }
        }
    }

    // 정밀한 각인석
    public void consume_2432126() {
        equipStone(2432126);
    }

    // 평범한 각인석
    public void consume_2432127() {
        equipStone(2432127);
    }

    public void equipStone(int itemID) {
        initNPC(MapleLifeFactory.getNPC(9010000));
        String v0 = "";
        if (DBConfig.isGanglim) {
            v0 = "#fs11#장착을 원하는 홈을 선택해주세요. #r#e선택한 홈에 장착된 각인석은 해제되며 사라지게 되니#n#k 주의하여 주시기 바랍니다.\r\n\r\n";
        } else {
            v0 = "장착을 원하는 홈을 선택해주세요. #r#e선택한 홈에 장착된 각인석은 해제되며 사라지게 되니#n#k 주의하여 주시기 바랍니다.\r\n\r\n";
        }

        String empty = "#fc0xFF6600CC##fUI/UIWindow.img/IconBase/0#";
        String icon = "#fs11##fc0xFF6600CC##i";
        int item1 = getPlayer().getOneInfoQuestInteger(133333, "equip1");
        int item2 = getPlayer().getOneInfoQuestInteger(133333, "equip2");
        int item3 = getPlayer().getOneInfoQuestInteger(133333, "equip3");
        int item4 = getPlayer().getOneInfoQuestInteger(133333, "equip4");
        int item5 = getPlayer().getOneInfoQuestInteger(133333, "equip5");

        String lock1 = getPlayer().getOneInfoQuestInteger(133333, "lock1") > 0 ? "#r(잠김)" : "#b(열림)";
        String lock2 = getPlayer().getOneInfoQuestInteger(133333, "lock2") > 0 ? "#r(잠김)" : "#b(열림)";
        String lock3 = getPlayer().getOneInfoQuestInteger(133333, "lock3") > 0 ? "#r(잠김)" : "#b(열림)";
        String lock4 = getPlayer().getOneInfoQuestInteger(133333, "lock4") > 0 ? "#r(잠김)" : "#b(열림)";
        String lock5 = getPlayer().getOneInfoQuestInteger(133333, "lock5") > 0 ? "#r(잠김)" : "#b(열림)";

        v0 += "#e[석판에 장착된 각인석]#n\r\n";
        v0 += item1 > 0 ? (icon + item1 + "# ") : (empty + " ");
        v0 += item2 > 0 ? (icon + item2 + "# ") : (empty + " ");
        v0 += item3 > 0 ? (icon + item3 + "# ") : (empty + " ");
        v0 += item4 > 0 ? (icon + item4 + "# ") : (empty + " ");
        v0 += item5 > 0 ? (icon + item5 + "# ") : (empty + " ");

        v0 += "\r\n" + lock1 + " " + lock2 + " " + lock3 + " " + lock4 + " " + lock5;

        // int unlock1 = getPlayer().getOneInfoQuestInteger(133333, "unlock1");
        // int unlock2 = getPlayer().getOneInfoQuestInteger(133333, "unlock2");

        v0 += "\r\n\r\n#b#L0#1번째 홈에 장착하겠습니다.#l\r\n";
        v0 += "#b#L1#2번째 홈에 장착하겠습니다.#l\r\n";
        v0 += "#b#L2#3번째 홈에 장착하겠습니다.#l\r\n";
        v0 += "#b#L3#4번째 홈에 장착하겠습니다.#l\r\n";
        v0 += "#b#L4#5번째 홈에 장착하겠습니다.#l\r\n";

        int v1 = self.askMenu(v0);
        int itemID_ = 0;
        if (v1 == 0) {
            itemID_ = item1;
        } else if (v1 == 1) {
            itemID_ = item2;
        } else if (v1 == 2) {
            itemID_ = item3;
        } else if (v1 == 3) {
            itemID_ = item4;
        } else if (v1 == 4) {
            itemID_ = item5;
        }
        String v2 = "";
        if (DBConfig.isGanglim) {
            v2 = "#fs11#정말 #e" + (v1 + 1) + "번째 홈#n에 장착하시겠습니까?\r\n\r\n";
        } else {
            v2 = "정말 #e" + (v1 + 1) + "번째 홈#n에 장착하시겠습니까?\r\n\r\n";
        }

        if (itemID_ == 0) {
            v2 += "#b현재 해당 홈은 각인석이 장착되어있지 않습니다.";
        } else {
            v2 += "#b#i" + itemID_ + "# #z" + itemID_ + "##k이 장착되어있습니다.\r\n#e#r장착 시 해당 각인석은 해제되며 사라지게 됩니다.";
        }
        if (1 == self.askYesNo(v2)) {
            if (getPlayer().getOneInfoQuestInteger(133333, "lock" + (v1 + 1)) > 0) {
                if (DBConfig.isGanglim) {
                    self.say("#fs11##r해당 홈은 잠금이 설정되어 있어 장착이 불가능합니다.");
                } else {
                    self.say("#r해당 홈은 잠금이 설정되어 있어 장착이 불가능합니다.");
                }
                return;
            }
            if (target.exchange(itemID, -1) > 0) {
                int index = v1 + 1;
                getPlayer().updateOneInfo(133333, "equip" + index, String.valueOf(itemID));

                getPlayer().updateOneInfo(133333, "craftPlus" + index, "0");
                getPlayer().updateOneInfo(133333, "craftMinus" + index, "0");
                getPlayer().updateOneInfo(133333, "plusOption" + index, "0");
                getPlayer().updateOneInfo(133333, "minusOption" + index, "0");
                getPlayer().updateOneInfo(133333, "plusValue" + index, "0");
                getPlayer().updateOneInfo(133333, "minusValue" + index, "0");
                getPlayer().checkImprintedStone();
                if (DBConfig.isGanglim) {
                    self.say("#fs11#장착이 완료되었습니다.");
                } else {
                    self.say("장착이 완료되었습니다.");
                }

                StringBuilder sb = new StringBuilder(
                        "각인석 장착 (슬롯 : " + index + ", 아이템ID : " + itemID + ", 사용자 : " + getPlayer().getName() + ")");
                LoggingManager.putLog(new ConsumeLog(getPlayer(), itemID, sb));

            }
        }
    }

    public void HonorTransmission() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        String v0 = "#e[훈장 옵션 전승]#n\r\n";
        v0 += "전승을 진행할 훈장을 선택해주세요. 해당 훈장에 부여된 훈장 강화 옵션이 전승됩니다. 기존 옵션은 전승되지 않습니다.\r\n\r\n#b";
        boolean find = false;
        for (Item item : getPlayer().getInventory(MapleInventoryType.EQUIP).list()) {
            if (item.getItemId() / 10000 == 114) {
                if (!DBConfig.isGanglim) {
                    if (GameConstants.isJinEndlessMedal(item.getItemId()))
                        continue;
                }
                v0 += "#L" + item.getPosition() + "##i" + item.getItemId() + "# #z" + item.getItemId() + "##l\r\n";
                if (!find)
                    find = true;
            }
        }
        if (!find) {
            v0 += "소지하고 있는 훈장이 없습니다.";
            self.say(v0);
            return;
        }
        int v1 = self.askMenu(v0);
        Item item = getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short) v1);
        if (item == null || item.getItemId() / 10000 != 114) {
            self.say("잘못된 접근입니다.");
            return;
        }
        Equip baseEquip = (Equip) item;

        String v2 = "#e[훈장 옵션 전승]#n\r\n";
        v2 += "옵션을 전승할 훈장을 선택해주세요.\r\n\r\n";
        v2 += "#e전승에 사용될 훈장 : #i" + baseEquip.getItemId() + "# #z" + baseEquip.getItemId() + "##n#b\r\n\r\n";
        find = false;
        for (Item it : getPlayer().getInventory(MapleInventoryType.EQUIP).list()) {
            if (it.getItemId() / 10000 == 114 && it.getPosition() != v1) {
                if (!DBConfig.isGanglim) {
                    if (GameConstants.isJinEndlessMedal(it.getItemId()))
                        continue;
                }
                v2 += "#L" + it.getPosition() + "##i" + it.getItemId() + "# #z" + it.getItemId() + "##l\r\n";
                if (!find)
                    find = true;
            }
        }
        if (!find) {
            v2 += "소지하고 있는 훈장이 없습니다.";
            self.say(v2);
            return;
        }
        int v3 = self.askMenu(v2);
        Item item2 = getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short) v3);
        if (item2 == null || item2.getItemId() / 10000 != 114) {
            self.say("잘못된 접근입니다.");
            return;
        }
        Equip targetEquip = (Equip) item2;

        if (1 == self.askYesNo(
                "#e[훈장 옵션 전승]#n\r\n해당 훈장에 전승을 시도하시겠습니까? 이미 훈장 옵션 강화가 진행된 훈장에는 사용할 수 없습니다.\r\n\r\n#e전승에 사용될 훈장 : #i"
                        + baseEquip.getItemId() + "# #z" + baseEquip.getItemId() + "#\r\n전승할 훈장 : #i"
                        + targetEquip.getItemId() + "# #z" + targetEquip.getItemId() + "#")) {
            if (baseEquip.getOwner() == null || baseEquip.getOwner().isEmpty()
                    || targetEquip.getOwner() != null && !targetEquip.getOwner().isEmpty()) {
                self.say("전승에 사용될 훈장이 훈장 옵션 강화를 진행하지 않았거나, 전승될 훈장에 훈장 옵션 강화권이 적용되어 전승이 불가능합니다.");
                return;
            }
            int baseLevel = Integer.parseInt(baseEquip.getOwner().replaceAll("[^0-9]", ""));
            baseEquip.setStr((short) (baseEquip.getStr() - (350 * baseLevel)));
            baseEquip.setDex((short) (baseEquip.getDex() - (350 * baseLevel)));
            baseEquip.setInt((short) (baseEquip.getInt() - (350 * baseLevel)));
            baseEquip.setLuk((short) (baseEquip.getLuk() - (350 * baseLevel)));
            baseEquip.setWatk((short) (baseEquip.getWatk() - (250 * baseLevel)));
            baseEquip.setMatk((short) (baseEquip.getMatk() - (250 * baseLevel)));
            baseEquip.setOwner("");

            targetEquip.setStr((short) (targetEquip.getStr() + (350 * baseLevel)));
            targetEquip.setDex((short) (targetEquip.getDex() + (350 * baseLevel)));
            targetEquip.setInt((short) (targetEquip.getInt() + (350 * baseLevel)));
            targetEquip.setLuk((short) (targetEquip.getLuk() + (350 * baseLevel)));
            targetEquip.setWatk((short) (targetEquip.getWatk() + (250 * baseLevel)));
            targetEquip.setMatk((short) (targetEquip.getMatk() + (250 * baseLevel)));
            targetEquip.setOwner(baseLevel + "성");

            getPlayer().send(CWvsContext.InventoryPacket.updateInventoryItem(MapleInventoryType.EQUIP, baseEquip, false,
                    getPlayer()));
            getPlayer().send(CWvsContext.InventoryPacket.updateInventoryItem(MapleInventoryType.EQUIP, targetEquip,
                    false, getPlayer()));
            self.say("전승이 완료되었습니다.");
        }
    }

    public void consume_2434287() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (1 == self.askYesNo("명성치 10,000를 소모하여 #b#i2434290# #z2434290# 1개#k를 획득하시겠습니까?")) {
            if (getPlayer().getInnerExp() < 10000) {
                self.say("명성치가 부족하여 사용할 수 없습니다.");
                return;
            }
            if (target.exchange(2434287, -1, 2434290, 1) == 1) {
                getPlayer().addHonorExp(-10000);
                self.say("교환이 완료되었습니다.");
            }

        }
    }

    public void consume_2438116() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (1 == self.askYesNo(
                "#b#i2438116# #z2438116##k을 열어 아래 아이템을 획득하시겠습니까?\r\n\r\n#b#i5060048# #z5060048# 5개\r\n#i2434558# #z2434558# 1개\r\n#i5680157# #z5680157# 3개\r\n#i5068300# #z5068300# 5개\r\n#i2028273# #z2028273# 5개\r\n#i5680409# #z5680409# 1개")) {
            if (getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 3 ||
                    getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 4) {
                self.say("#b소비 인벤토리#k 3칸과 #b캐시 인벤토리#k 4칸을 확보하고 다시 시도해주시기 바랍니다.");
                return;
            }
            if (target.exchange(2438116, -1, 5060048, 5, 2434558, 1, 5680157, 3, 2028273, 5, 5680409, 1) == 1) {
                Item wonderBerry = new Item(5068300, (short) 1, (short) 5, (short) ItemFlag.KARMA_USE.getValue());
                MapleInventoryManipulator.addFromDrop(getClient(), wonderBerry, true);
                self.say("아이템을 획득했습니다.");
            }
        }
    }

    public void consume_2630009() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (1 == self.askYesNo("#b#i2630009# #z2630009##k을 열어 아래 아이템을 획득하시겠습니까?\r\n\r\n#b#i4034803# #z4034803# 1개")) {
            if (getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 1) {
                self.say("#b기타 인벤토리#k 1칸을 확보하고 다시 시도해주시기 바랍니다.");
                return;
            }
            if (target.exchange(2630009, -1, 4034803, 1) == 1) {
                self.say("아이템을 획득했습니다.");
            }
        }
    }

}
