package script.Quest;

import constants.GameConstants;
import network.SendPacketOpcode;
import network.encode.PacketEncoder;
import network.models.CField;
import network.models.CWvsContext;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.item.Equip;
import objects.item.Item;
import objects.item.MapleInventoryType;
import objects.quest.MapleQuest;
import objects.users.MapleStat;
import objects.utils.Randomizer;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class YetiXPinkBean extends ScriptEngineNPC {

    public void q100564s() {
        int v = self.askMenu("#b#e< 예티X핑크빈 월드 >#k#n가 열렸습니다!\r\n\r\n#L0##e예티#n를 만들고 싶어요.#l\r\n#L1##e핑크빈#n을 만들고 싶어요.#l\r\n#L2##e준비된 정령의 펜던트#n를 재발급 받고싶어요.#l\r\n#L3##e점검 보상#n을 받고 싶어요.#l\r\n#L4#칭호를 다시 칭호 교환권으로 바꾸고 싶어요(예아일체, 핑아일체)#l");
        if (v == 0) {
            self.say("현재 예티 캐릭터는 캐릭터 생성 메뉴를 통해 생성할 수 있습니다.");
        } else if (v == 1) {
            self.say("현재 핑크빈 캐릭터는 캐릭터 생성 메뉴를 통해 생성할 수 있습니다.");
        } else if (v == 2) {
            if (getPlayer().getQuestStatus(100568) == 2 && getPlayer().getOneInfoQuestInteger(100568, "fairy") == 0) {
                if (target.exchange(2633422, 2) > 0) {
                    getPlayer().updateOneInfo(100568, "fairy" , "1");
                    self.sayOk("준비된 정령의 펜던트 교환권 (14일) x 2 개가 재발급 완료되었습니다.");
                }
            } else {
                self.sayOk("이미 재발급을 받았거나 재발급 대상이 아닙니다.");
            }
        } else if (v == 3) {
            if (getPlayer().getOneInfoQuestInteger(1234699, "pReward") == 0) {
                if (target.exchange(2633201, 2) > 0) {
                    getPlayer().updateOneInfo(1234699, "pReward", "1");
                } else {
                    self.say("소비 인벤토리 2칸이상 비워주세요.");
                }
            } else {
                self.say("이미 발급을 받아 지급대상이 아닙니다.");
            }
        } else if (v == 4) {
            int vv = self.askMenu("#b#L0#핑아일체#k를 #r핑아일체 교환권#k으로 바꾸기#l\r\n#b#L1#예아일체#k를 #r예아일체 교환권#k으로 바꾸기#l");
            if (vv == 0) {
                if (target.exchange(3700287, -1, 2434030, 1) > 0) {
                    self.sayOk("완료되었습니다.");
                } else {
                    self.sayOk("핑아일체를 갖고 계시지 않거나 소비창에 여유공간이 없는지 확인 바랍니다.");
                }
            } else if (vv == 1) {
                if (target.exchange(3700682, -1, 2633232, 1) > 0) {
                    self.sayOk("완료되었습니다.");
                } else {
                    self.sayOk("예아일체를 갖고 계시지 않거나 소비창에 여유공간이 없는지 확인 바랍니다.");
                }
            }
        }
    }

    public void q100565s() {
        if (getPlayer().getJob() == 13100) { //핑크빈 스크립트
            target.say("처음 보는 곳이야! 흥분되는데?!");
            target.say("...진정하자. 예티 녀석과 다시 만나기 전까지 힘을 되찾아야해.");
            target.say("이럴 때를 대비해서 #e#b'스텝업'#k#n 미션 리스트를 준비했지!");
            target.say("새로운 세계에 왔다고 당황하지 말고 화면 왼쪽 이벤트 알림이에 있는 #e#b'예티x핑크빈 스텝업'#k#n 아이콘을 살펴보자! 일단 거기에 적힌 목표들을 하나씩 충실히 달성해볼까. #e#r그러면 힘을 다시 되찾을 수 있을 거야.");
            target.say("예티 녀석에게 질 수는 없지. 다행히 '파워 엘릭서'와 이것저것 가져왔으니 한 번 열심히 해보자! 헤헤헤.");
            target.say("좋아 필요한 물건도 챙겼어!\r\n#e#b첫 번째 목표는 레벨 30 달성이다!");
            getSc().flushSay();
            getQuest().forceComplete(getPlayer(), getNpc().getId());
            MapleQuest.getInstance(100566).forceStart(getPlayer(), getNpc().getId(), null);
            if (getPlayer().getLevel() >= 30) {
                getPlayer().updateInfoQuest(100565, "questNum=100565");
            }
            getPlayer().send(CField.UIPacket.openUI(1267));
        } else if (getPlayer().getJob() == 13500) { //예티스크립트
            target.say("예티, 다시 힘을 되찾아야 한다.\r\n핑크빈에게 질 수 없다...");
            target.say("이럴 때를 대비해서 #e#b'스텝업'#k#n 미션 리스트를 준비했다.");
            target.say("예티, 당황하지 않는다\r\n화면 왼쪽 이벤트 알림이에 있는 #e#b'예티x핑크빈 스텝업'#k#n에서 목표를 확인하고 하나씩 하나씩 해나간다.\r\n#e#r그리고 힘을 되찾는다.");
            target.say("예티 녀석에게 질 수는 없지. 다행히 '파워 엘릭서'와 이것저것 가져왔으니 한 번 열심히 해보자! 헤헤헤.");
            target.say("핑크빈에게 질 수 없다.\r\n예티, 집에서 가져온 물건들도 있다. 제대로 한다.");
            target.say("예티, 필요한 물건 챙겼다.\r\n#e#b첫 번째 목표 레벨 30 달성한다.");
            getSc().flushSay();
            getQuest().forceComplete(getPlayer(), getNpc().getId());
            MapleQuest.getInstance(100566).forceStart(getPlayer(), getNpc().getId(), null);
            if (getPlayer().getLevel() >= 30) {
                getPlayer().updateInfoQuest(100565, "questNum=100565");
            }
            getPlayer().send(CField.UIPacket.openUI(1267));
        }
    }

    public void q100588s() {
        if (GameConstants.isYetiPinkBean(getPlayer().getJob())) {
            if (getPlayer().getOneInfo(100565, "questNum") != null && getPlayer().getOneInfo(100565, "questNum").equals("finish")) {
                PacketEncoder p = new PacketEncoder();
                p.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
                p.write(14);
                p.writeInt(268);
                if (getPlayer().getJob() == 13100) {
                    p.writeMapleAsciiString("r4=1;");
                } else {
                    p.writeMapleAsciiString("r3=1;");
                }
                getPlayer().send(p.getPacket());
            }
            getPlayer().send(CField.UIPacket.openUI(1267));
        }
    }

    public void q100566e() {
        if (getPlayer().getLevel() >= 30) {
            if (target.exchange(2350000, 1) > 0) {
                int questId = 100566;
                clearStepUp(questId);
                sayStepUp(1, 2350000, "콤보킬 300 달성");
            } else {
                target.say("보상을 받을 공간이 부족한 것 같다.");
            }
        }
    }

    public void q100567e() {
        if (getPlayer().getOneInfo(100565, "questNum").equals("100566")) {
            if (target.exchange(4001832, 9000) > 0) {
                int questId = 100567;
                clearStepUp(questId);
                sayStepUp(2, 4001832, "레벨 50 달성");
                if (getPlayer().getLevel() >= 50) {
                    getPlayer().updateOneInfo(100565, "questNum", "100567");
                }
            } else {
                target.say("보상을 받을 공간이 부족한 것 같다.");
            }
        }
    }

    public void q100568e() {
        if (getPlayer().getLevel() >= 50) {
            if (target.exchange(2437095, 1, 2633422, 2) > 0) {
                getPlayer().updateOneInfo(100568, "fairy", "1");
                int questId = 100568;
                clearStepUp(questId);
                sayStepUp(3, 2437095, "레벨 범위 몬스터 999마리 처치");
            } else {
                target.say("보상을 받을 공간이 부족한 것 같다. 소비창 3칸의 여유가 필요하다.");
            }
        }
    }

    public void q100569e() {
        if (getPlayer().getQuest(100569).getQuest().canComplete(getPlayer(), null)) {
            if (target.exchange(4001211, 1) > 0) {
                int questId = 100569;
                clearStepUp(questId);
                if (getPlayer().getLevel() >= 70) {
                    getPlayer().updateOneInfo(100565, "questNum", "100569");
                }
                sayStepUp(4, 4001211, "레벨 70 달성");
            } else {
                target.say("보상을 받을 공간이 부족한 것 같다.");
            }
        }
    }

    public void q100570e() {
        if (getPlayer().getLevel() >= 70) {
            if (target.exchange(2048759, 200) > 0) {
                int questId = 100570;
                clearStepUp(questId);
                sayStepUp(5, 2048759, "룬 2회 사용");
            } else {
                target.say("보상을 받을 공간이 부족한 것 같다.");
            }
        }
    }

    public void q100571e() {
        if (getPlayer().getOneInfoQuestInteger(100571, "RunAct") >= 2) {
            if (target.exchange(2048758, 100) > 0) {
                int questId = 100571;
                clearStepUp(questId);
                if (getPlayer().getLevel() >= 100) {
                    getPlayer().updateOneInfo(100565, "questNum", "100571");
                }
                sayStepUp(6, 2048758, "레벨 100 달성");
            } else {
                target.say("보상을 받을 공간이 부족한 것 같다.");
            }
        }
    }

    public void q100572e() {
        if (getPlayer().getLevel() >= 100) {
            if (target.exchange(2633349, 20) > 0) {
                int questId = 100572;
                clearStepUp(questId);
                sayStepUp(7, 2633349, "몬스터 파크 2회 클리어");
            } else {
                target.say("보상을 받을 공간이 부족한 것 같다.");
            }
        }
    }

    public void q100573e() {
        if (getPlayer().getOneInfoQuestInteger(100573, "monsterPark") >= 2) {
            if (target.exchange(2631822, 1) > 0) {
                int questId = 100573;
                clearStepUp(questId);
                if (getPlayer().getLevel() >= 150) {
                    getPlayer().updateOneInfo(100565, "questNum", "100573");
                }
                sayStepUp(8, 2631822, "레벨 150 달성");
            } else {
                target.say("보상을 받을 공간이 부족한 것 같다.");
            }
        }
    }

    public void q100574e() {
        if (getPlayer().getLevel() >= 150) {
            if (target.exchange(2048766, 30) > 0) {
                int questId = 100574;
                clearStepUp(questId);
                sayStepUp(9, 2048766, "엘리트 몬스터/챔피언 5마리 처치");
            } else {
                target.say("보상을 받을 공간이 부족한 것 같다.");
            }
        }
    }

    public void q100575e() {
        if (getPlayer().getQuest(100575).getQuest().canComplete(getPlayer(), null)) {
            if (target.exchange(2631528, 1) > 0) {
                int questId = 100575;
                clearStepUp(questId);
                int chuk = 0;
                for (Item item : getPlayer().getInventory(MapleInventoryType.EQUIPPED).list()) {
                    Equip equip = (Equip) item;
                    if (equip != null) {
                        chuk += equip.getCHUC();
                    }
                }
                if (chuk >= 50) {
                    getPlayer().updateOneInfo(100565, "questNum", "100575");
                }
                sayStepUp(10, 2434007, "스타포스 50 이상 맞추기");
            } else {
                target.say("보상을 받을 공간이 부족한 것 같다.");
            }
        }
    }

    public void q100576e() {
        int chuk = 0;
        for (Item item : getPlayer().getInventory(MapleInventoryType.EQUIPPED).list()) {
            Equip equip = (Equip) item;
            if (equip != null) {
                chuk += equip.getCHUC();
            }
        }
        if (chuk >= 50) {
            if (target.exchange(5062010, 100) > 0) {
                int questId = 100576;
                clearStepUp(questId);
                sayStepUp(11, 5062010, "카오스 혼테일 처치");
            } else {
                target.say("보상을 받을 공간이 부족한 것 같다.");
            }
        }
    }

    public void q100577e() {
        if (getPlayer().getQuest(100577).getQuest().canComplete(getPlayer(), null) || (getPlayer().getOneInfo(3790, "mobid").equals("8810122") && getPlayer().getOneInfoQuestInteger(3790, "mobDead") > 0)) {
            if (target.exchange(2434011, 1) > 0) {
                int questId = 100577;
                clearStepUp(questId);
                if (getPlayer().getLevel() >= 200) {
                    getPlayer().updateOneInfo(100565, "questNum", "100577");
                }
                sayStepUp(12, 2434011, "레벨 200 달성");
            } else {
                target.say("보상을 받을 공간이 부족한 것 같다.");
            }
        }
    }

    public void q100578e() {
        if (getPlayer().getLevel() >= 200) {
            if (target.exchange(2633195, 1) > 0) {
                String start = "";
                String end = "";
                if (getPlayer().getJob() == 13100) {
                    start = "좋아! #e" + 13 + "단계 미션#n을 클리어했다!";
                    end = "모든 미션을 완료했군! 이제 어느 정도 힘도 돌아온 것 같다!\r\n예티 녀석. 혼쭐을 내줘야지! 헤헤!";
                } else if (getPlayer().getJob() == 13500) {
                    start = "예티! #e" + 13 + "단계 미션#n을 클리어했다!";
                    end = "예티, 모든 미션을 완료했다!\r\n예티, 귀여움과 강력함을 모두 가졌다.\r\n이제 핑크빈을 와장창 우지끈해줄 수 있다.";
                }
                target.say(start + "\r\n\r\n\r\n#e#b<" + 13 + "단계 스텝업 미션 완료 보상>\r\n보상");
                target.say(end);
                int questId = 100578;
                MapleQuest.getInstance(questId).forceComplete(getPlayer(), 9010000);
                getPlayer().updateOneInfo(100565, "stepNum", "finish");
                getPlayer().updateOneInfo(100565, "questNum", "100578");
            } else {
                target.say("보상을 받을 공간이 부족한 것 같다.");
            }
        }
    }

    public void yetiXpinkHeadTitle() {
        if (getPlayer().getOneInfo(100565, "questNum").equals("100578")) {
            int exItem = 2434030;
            if (getPlayer().getJob() == 13500) exItem = 2633232;
            if (target.exchange(exItem, 1) > 0) {
                PacketEncoder p = new PacketEncoder();
                p.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
                p.write(14);
                p.writeInt(268);
                if (getPlayer().getJob() == 13100) {
                    p.writeMapleAsciiString("r4=1;");
                } else {
                    p.writeMapleAsciiString("r3=1;");
                }
                getPlayer().send(p.getPacket());
                self.say("#b스텝업 미션을 전부 완료하셨군요!#k\r\n월드 내 이동 가능한 칭호 교환권을 보상으로 드리겠습니다.\r\n\r\n#e#b#i" + exItem + "# #z" + exItem + "#", ScriptMessageFlag.NpcReplacedByNpc);
                getPlayer().updateOneInfo(100565, "questNum", "finish");
            } else {
                target.say("보상을 받을 공간이 부족한 것 같다.");
            }
        }
    }

    private void sayStepUp(int d, int reward, String nextMission) {
        String start = "";
        String end = "";
        if (getPlayer().getJob() == 13100) {
            start = "좋아! #e" + d + "단계 미션#n을 클리어했다!";
            end = "이 기세를 몰아서 다음 단계로 넘어가 볼까?\r\n다음은 바로 이거군!\r\n\r\n";
        } else if (getPlayer().getJob() == 13500) {
            start = "예티! #e" + d + "단계 미션#n을 클리어했다!\r\n선물 자격 있다.";
            end = "예티, 힘을 되찾고 있다. 멈추지 않는다.\r\n다음 미션도 바로 해버린다.\r\n\r\n";
        }
        target.say(start + "\r\n\r\n\r\n#e#b<" + d + "단계 스텝업 미션 완료 보상>\r\n\r\n" + "#i" + reward + "# #z" + reward + "#");
        target.say(end + "#b#e<" + (d + 1) + "단계 스텝업 미션>\r\n#k- " + nextMission);
    }

    private void clearStepUp(int questId) {
        MapleQuest.getInstance(questId).forceComplete(getPlayer(), 9010000);
        MapleQuest.getInstance(questId + 1).forceStart(getPlayer(), 9010000, "");
        getPlayer().updateOneInfo(100565, "stepNum", String.valueOf(questId - 100565));
        getPlayer().updateOneInfo(100565, "questNum", " ");
    }

    //////////핑크빈 보상아이템 스크립트
    public void consume_2631822() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (target.exchange(1012478, 1, 1032241, 1, 1113149, 1, 1122150, 1, 1182087, 1, 2631822, -1) > 0) {

        } else {
            target.say("보상을 받을 공간이 부족한 것 같다.");
        }
    }

    public void consume_2631528() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() > 1) {
            int[] rewards00 = new int[]{1302361, 1312218, 1322270, 1402273, 1412193, 1422201, 1432231, 1442289};
            int[] rewards01 = new int[]{1482236, 1532162};
            String r = "무기를 선택해주세요.#b\r\n\r\n";
            if (getPlayer().getJob() == 13100) {
                for (int i = 0; i < rewards00.length; i++) {
                    r += "#L" + i + "# #i" + rewards00[i] + "# #z" + rewards00[i] + "# #l\r\n";
                }
                int a = self.askMenu(r);
                if (a >= 0) {
                    exchangePinkBeanSupportEquip(rewards00[a]);
                }
            } else if (getPlayer().getJob() == 13500) {
                for (int i = 0; i < rewards01.length; i++) {
                    r += "#L" + i + "# #i" + rewards01[i] + "# #z" + rewards01[i] + "#\r\n";
                }
                int a = self.askMenu(r);
                if (a >= 0) {
                    exchangePinkBeanSupportEquip(rewards01[a]);
                }
            }
            target.exchange(2631528, -1);
        } else {
            target.say("보상을 받을 공간이 부족한 것 같다.");
        }
    }

    public void consume_2434011() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() > 1) {
            if (target.exchange(2434011, -1) > 0) {
                exchangePinkBeanSupportEquip(1022144);
            }
        } else {
            target.say("보상을 받을 공간이 부족한 것 같다.");
        }
    }

    public void consume_2633195() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        int[][] reward = new int[][]{{2633222, 20}, {2633223, 10}};
        int v = self.askMenu("둘중 하나를 선택해주세요.\r\n\r\n#b#L0# #i" + reward[0][0] + "# #z" + reward[0][0] + "# " + reward[0][1] + "개\r\n#L1# #i" + reward[1][0] + "# #z" + reward[1][0] + "# " + reward[1][1] + "개");
        if (v >= 0 && target.exchange(reward[v][0], reward[v][1], 2633195, -1) > 0) {

        }
    }

    public void consume_2434030() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (target.exchange(2434030, -1, 3700287, 1) > 0) {

        }
    }

    public void consume_2633232() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (target.exchange(2633232, -1, 3700682, 1) > 0) {

        }
    }

    public void consume_2633349() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (GameConstants.isYetiPinkBean(getPlayer().getJob())) {
            self.say("핑크빈과 예티는 익스트림 성장비약을 사용할 수 없습니다.");
            return;
        }
        String percent = "";
        if (getPlayer().getLevel() >= 141 && getPlayer().getLevel() < 200) {
            List<Integer> levelUp = new ArrayList<>();
            for (int i = 1; i <= GameConstants.extreamPotion()[getPlayer().getLevel() - 141].length; i++) {
                for (int a = 0; a < GameConstants.extreamPotion()[getPlayer().getLevel() - 141][i - 1]; a++) {
                    levelUp.add(i);
                }
                percent += "- " + i + "레벨 상승 : " + GameConstants.extreamPotion()[getPlayer().getLevel() - 141][i - 1] + "%\r\n";
            }
            if (1 == self.askYesNo("#r#e익스트림 성장의 비약#k#n을 지금 사용하시겠습니까?\r\n\r\n※ 사용 시 #e#b랜덤한 확률#n#k에 따라 #b#e1 ~ 10레벨이 상승#k#n하며,　 캐릭터 레벨이 높을수록 그 확률이 낮아집니다.\r\n\r\n<#b" + getPlayer().getLevel() + "레벨#k에서 사용>\r\n" + percent)) {
                Collections.shuffle(levelUp);
                int plus = levelUp.get(Randomizer.nextInt(levelUp.size()));
                for (int i = 0; i < plus; i++) {
                    getPlayer().levelUp();
                }
                self.sayOk("익스트림 성장의 비약으로 #e#r" + plus + "레벨#k#n 성장했습니다!", ScriptMessageFlag.NoEsc);
                target.exchange(2633349, -1);
            }
        } else {
            getPlayer().gainExp(571115568.0d, true, false, false);
            self.sayOk("익스트림 성장의 비약으로 경험치를 571115568만큼 획득하였습니다.", ScriptMessageFlag.NoEsc);
            target.exchange(2633349, -1);
        }
    }

    public void consume_2633222() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (getPlayer().getLevel() >= 200 && getPlayer().getLevel() < 210) {
            if (1 == self.askYesNo("#r#e< 예티X핑크빈 성장의 비약 1 > #k#n을 지금 사용하시겠습니까?")) {
                if (target.exchange(2633222, -1) > 0) {
                    getPlayer().levelUp();
                    getPlayer().setExp(0);
                    HashMap<MapleStat, Long> eee = new HashMap<>();
                    eee.put(MapleStat.EXP, 0L);
                    getPlayer().send(CWvsContext.updatePlayerStats(eee, getPlayer()));
                }
            }
        } else if (getPlayer().getLevel() >= 210) {
            if (1 == self.askYesNo("#r#e< 예티X핑크빈 성장의 비약 1 > #k#n을 지금 사용하시겠습니까?")) {
                if (target.exchange(2633222, -1) > 0) {
                    getPlayer().gainExp(6120258214.0d, true, false, false);
                }
            }
        } else {
            self.say("레벨이 낮아 사용할 수 없습니다.");
        }
    }

    public void consume_2633223() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (getPlayer().getLevel() >= 210 && getPlayer().getLevel() < 220) {
            if (1 == self.askYesNo("#r#e< 예티X핑크빈 성장의 비약 2 > #k#n을 지금 사용하시겠습니까?")) {
                if (target.exchange(2633223, -1) > 0) {
                    getPlayer().levelUp();
                    getPlayer().setExp(0);
                    HashMap<MapleStat, Long> eee = new HashMap<>();
                    eee.put(MapleStat.EXP, 0L);
                    getPlayer().send(CWvsContext.updatePlayerStats(eee, getPlayer()));
                }
            }
        } else if (getPlayer().getLevel() >= 220) {
            if (1 == self.askYesNo("#r#e< 예티X핑크빈 성장의 비약 2 > #k#n을 지금 사용하시겠습니까?")) {
                if (target.exchange(2633223, -1) > 0) {
                    getPlayer().gainExp(27279159629.0d, true, false, false);
                }
            }
        } else {
            self.say("레벨이 낮아 사용할 수 없습니다.");
        }
    }

    public void q100785s() {
        self.say("안녕하세요! #b#e#h0##k#n님.\r\n\r\n#b #i3700287# #z3700287##k와 #b#i3700682# #z3700682##k 칭호 2개를\r\n#r예티X핑크빈 칭호 교환권#k과 교환해드리고 있습니다.\r\n\r\n#e [획득 가능한 칭호]\r\n#b#n #i2633243# #z2633243#", ScriptMessageFlag.NpcReplacedByNpc);
        if (target.exchange(3700287, -1, 3700682, -1, 2633243, 1) > 0) {
            self.sayOk("칭호 교환권을 지급해드렸습니다. 인벤토리를 확인해 주세요.\r\n\r\n#fUI/UIWindow.img/Quest/reward#\r\n\r\n#b#e #i2633243# #z2633243#", ScriptMessageFlag.NpcReplacedByNpc);
        } else {
            self.sayOk("칭호 2개를 보유하셔야 교환이 가능합니다.", ScriptMessageFlag.NpcReplacedByNpc);
        }
    }

    public void consume_2633243() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (target.exchange(2633243, -1, 3700683, 1) > 0) {
            self.sayOk("교환이 완료되었습니다.");
        }
    }

    public void consume_2633422() {
        initNPC(MapleLifeFactory.getNPC(9010000));
        if (getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() >= 1) {
            if (target.exchange(2633422, -1) > 0) {
                exchangeSupportEquipPeriod(1122334, 0, 0, 14);
                self.sayOk("교환이 완료되었습니다.");
            }
        }
    }
}
