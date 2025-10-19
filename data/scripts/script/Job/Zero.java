package script.Job;

import network.models.CWvsContext;
import objects.item.*;
import objects.utils.Randomizer;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

public class Zero extends ScriptEngineNPC {

    public void zero_inheritance() {
        int level = getPlayer().getLevel();
        int curLv = 0;
        int itemID = getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10).getItemId();

        if (itemID == 1562007) {
            curLv = 7;
        } else if (itemID == 1562008) {
            curLv = 8;
        } else if (itemID == 1562009) {
            curLv = 9;
        } else if (itemID == 1562010) {
            curLv = 10;
        }

        if (curLv == 7 && level < 180) {
            return;
        }
        if (curLv == 8 && level < 200) {
            return;
        }

        if (curLv != 10) {
            self.sayReplacedNpc("이제부터 우리를 더 성장 시키기 위해서는 특별한 재료가 필요해.", 2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("#face1#비싼 몸이구나…… 우리……", 2400010, 0, ScriptMessageFlag.Scenario);
        }

        int v0 = -1;
        if (getPlayer().getOneInfoQuestInteger(40981, "save_genesis") == 1) {
            if (curLv != 10) {
                v0 = self.askMenuReplacedNpc("자, #h0#. 어떻게 하겠어?\r\n\r\n#b#L3# 제네시스 라피스와 라즐리를 얻는다.#l\r\n#L999# 그만둔다.#l", 2400009, 0,  ScriptMessageFlag.NpcReplacedByNpc);
            } else {
                v0 = self.askMenuReplacedNpc("자, #h0#. 어떻게 하겠어?\r\n\r\n#b#L3# 이전 단계의 라피스와 라즐리를 장착한다.#l\r\n#L999# 그만둔다.#l", 2400009, 0,  ScriptMessageFlag.NpcReplacedByNpc);
            }
        } else if (curLv == 7) {
            if (level >= 200) {
                v0 = self.askMenuReplacedNpc("자, #h0#. 어떻게 하겠어?\r\n\r\n#b#L0# 라피스와 라즐리를 8형으로 성장시킨다.#l\r\n#L1# 라피스와 라즐리를 9형으로 성장시킨다.#l\r\n#L999# 그만둔다.#l", 2400009, 0,  ScriptMessageFlag.Scenario);
            } else {
                v0 = self.askMenuReplacedNpc("자, #h0#. 어떻게 하겠어?\r\n\r\n#b#L0# 라피스와 라즐리를 8형으로 성장시킨다.#l\r\n#L999# 그만둔다.#l", 2400009, 0,  ScriptMessageFlag.Scenario);
            }
        } else if (curLv == 8) {
            v0 = self.askMenuReplacedNpc("자, #h0#. 어떻게 하겠어?\r\n\r\n#b#L1# 라피스와 라즐리를 9형으로 성장시킨다.#l\r\n#L999# 그만둔다.#l", 2400009, 0,  ScriptMessageFlag.Scenario);
        } else if (curLv == 9) {
            v0 = self.askMenuReplacedNpc("자, #h0#. 어떻게 하겠어?\r\n\r\n#b#L2# 라피스와 라즐리를 10형으로 성장시킨다.#l\r\n#L999# 그만둔다.#l", 2400009, 0,  ScriptMessageFlag.Scenario);
        } else if (curLv == 10) {
            v0 = self.askMenuReplacedNpc("자, #h0#. 어떻게 하겠어?\r\n\r\n#b#L3# 이전 단계의 라피스와 라즐리를 장착한다.#l\r\n#L999# 그만둔다.#l", 2400009, 0,  ScriptMessageFlag.NpcReplacedByNpc);
        }

        if (v0 == 0) {
            self.sayReplacedNpc("성장하기 위해서는 #t4310216#가 1개 필요해.", 2400009, 0, ScriptMessageFlag.Scenario);
            getPlayer().openInheritanceUpgrade(8, 4310216, 1);
        } else if (v0 == 1) {
            self.sayReplacedNpc("성장하기 위해서는 #t4310217#가 1개 필요해.", 2400009, 0, ScriptMessageFlag.Scenario);
            getPlayer().openInheritanceUpgrade(9, 4310217, 1);
        } else if (v0 == 2) {
            self.sayReplacedNpc("제네시스 무기를 얻기 위해서는 #t4310260#가 1개 필요해.", 2400009, 0, ScriptMessageFlag.Scenario);
            if (!getPlayer().haveItem(4310260)) {
                self.sayReplacedNpc("검은 마법사가 남긴 흔적을 따라가면 #b#t4310260##k를 습득할 수 있을거야.", 2400009, 0, ScriptMessageFlag.Scenario);
                self.sayReplacedNpc("참고로 #t4310260#를 사용한 성장은 지금까지와는 다른 점이 많아!", 2400009, 0, ScriptMessageFlag.Scenario);
                self.sayReplacedNpc("#b제네시스 무기#k는 이전 단계의 무기에서 잠재능력, 에디셔널 잠재능력, 추가옵션을 제외하고는 전수되지 않고 특별한 옵션을 가지고 있어.\r\n#r- 주문의 흔적 15% 공격력(힘) 주문서로 업그레이드 가능 횟수 모두 소모\r\n- 스타포스 22성\r\n- 유니크 잠재능력 및 에픽 에디셔널 잠재능력 보유(전수하지 않을 경우)\r\n-장착 시 <파괴의 얄다바오트>, <창조의 아이온> 스킬 획득\r\n- 주문서 및 스타포스 강화 불가", 2400009, 0, ScriptMessageFlag.Scenario);
                self.sayReplacedNpc("또 제네시스 무기를 완성한 후에는 무기 성장 버튼으로 이전 단계와 제네시스 무기를 오갈 수 있으니 제네시스 무기로 강화하는 것을 고민하지 않아도 될거야.\r\n단, 교체하는데 힘이 많이 필요해서 한번 교체한 후 10분 동안은 교체할 수 없어.", 2400009, 0, ScriptMessageFlag.Scenario);
                self.sayReplacedNpc("열심히 노력해서 #b#t4310260##k를 얻기를 바랄게.", 2400009, 0, ScriptMessageFlag.Scenario);
                //getPlayer().openInheritanceUpgrade(10, 4310260, 1);
            } else {
                self.sayReplacedNpc("검은 마법사의 힘이 담긴 #b#t4310260##k를 구하다니!\r\n대단하구나 너!", 2400009, 0, ScriptMessageFlag.Scenario);
                self.sayReplacedNpc("#b#t4310260##k를 이용하면 우리를 #b제네시스 무기#k로 강화할 수 있어!\r\n하지만 지금까지의 강화와는 다른 점이 많아!", 2400009, 0, ScriptMessageFlag.Scenario);
                self.sayReplacedNpc("#b제네시스 무기#k는 이전 단계의 무기에서 잠재능력, 에디셔널 잠재능력, 추가옵션을 제외하고는 전수되지 않고 특별한 옵션을 가지고 있어.\r\n#r- 주문의 흔적 15% 공격력(힘) 주문서로 업그레이드 가능 횟수 모두 소모\r\n- 스타포스 22성\r\n- 유니크 잠재능력 및 에픽 에디셔널 잠재능력 보유(전수하지 않을 경우)\r\n-장착 시 <파괴의 얄다바오트>, <창조의 아이온> 스킬 획득\r\n- 주문서 및 스타포스 강화 불가", 2400009, 0, ScriptMessageFlag.Scenario);
                self.sayReplacedNpc("또 제네시스 무기를 완성한 후에는 무기 성장 버튼으로 이전 단계와 제네시스 무기를 오갈 수 있으니 제네시스 무기로 강화하는 것을 고민하지 않아도 될거야.\r\n단, 교체하는데 힘이 많이 필요해서 한번 교체한 후 10분 동안은 교체할 수 없어.", 2400009, 0, ScriptMessageFlag.Scenario);
                int v1 = self.askMenuReplacedNpc("#r잠재능력#k의 경우 네가 원한다면 랜덤한 #b유니크 잠재능력#k으로 변화할거야. #b유니크 잠재능력#k으로 변화하겠어?\r\n\r\n#b#L0#유니크 잠재능력으로 변경한다.#l\r\n#L1#기존 잠재능력을 그대로 유지한다.#l", 2400009, 0, ScriptMessageFlag.NpcReplacedByNpc);
                int v2 = self.askMenuReplacedNpc("#r에디셔널 잠재능력#k의 경우 네가 원한다면 랜덤한 #b에픽 에디셔널 잠재 옵션#k으로 변화할거야. #b에픽 에디셔널 잠재능력#k으로 변화하겠어?\r\n\r\n#b#L0#에픽 에디셔널 잠재능력으로 변경한다.#l\r\n#L1#기존 에디셔널 잠재능력을 그대로 유지한다.#l", 2400009, 0, ScriptMessageFlag.NpcReplacedByNpc);
                self.sayReplacedNpc("#b추가옵션#k은 그대로 전수되니 걱정하지마!", 2400009, 0, ScriptMessageFlag.Scenario);
                String v4 = "자! 이제 정말 마지막이야!\r\n#t4310260#를 소비해 #b제네시스 라피스와 라즐리#k를 얻겠어?\r\n";
                v4 += "\r\n#r잠재능력 옵션 유니크로 재설정 여부 : #e" + (v1 == 0 ? "재설정" : "재설정하지 않음") + "#n\r\n";
                v4 += "#r에디셔널 잠재능력 옵션 에픽으로 재설정 여부 : #e" + (v2 == 0 ? "재설정" : "재설정하지 않음") + "#n\r\n";
                v4 += "\r\n#b#L0#제네시스 라피스와 라즐리를 얻는다.#l\r\n#L1#그만둔다.#l";
                int v3 = self.askMenuReplacedNpc(v4, 2400009, 0, ScriptMessageFlag.NpcReplacedByNpc);
                if (v3 == 0) {
                    if (v1 == 0) {
                        getPlayer().updateOneInfo(40981, "reset_potential", "1");
                    }
                    if (v2 == 0) {
                        getPlayer().updateOneInfo(40981, "reset_e_potential", "1");
                    }
                    getPlayer().openInheritanceUpgrade(10, 4310260, 1);
                }
            }
        } else if (v0 == 3) {
            boolean check = false;
            long time = getPlayer().getOneInfoQuestLong(40981, "last_change_time");
            if (time == 0 || System.currentTimeMillis() - time >= (1000 * 60 * 10)) {
                // 10분
                check = true;
            }
            if (check) {
                Item alphaBefore = getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -20000);
                Item betaBefore = getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -20001);
                if (alphaBefore == null || betaBefore == null) {
                    self.sayReplacedNpc("알 수 없는 오류가 발생했어. 고객센터에 문의해주겠어?", 2400009, 0, ScriptMessageFlag.NpcReplacedByNpc);
                    return;
                }

                // 현재 무기 저장
                Equip alpha_copy = (Equip) getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11).copy();
                Equip beta_copy = (Equip) getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10).copy();


                getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeItem((short ) -11);
                getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeItem((short ) -10);

                alphaBefore.setPosition((short) -11);
                betaBefore.setPosition((short) -10);

                getPlayer().getInventory(MapleInventoryType.EQUIPPED).addFromDB(alphaBefore);
                getPlayer().getInventory(MapleInventoryType.EQUIPPED).addFromDB(betaBefore);

                getPlayer().send(CWvsContext.InventoryPacket.updateEquipSlot(alphaBefore));
                getPlayer().send(CWvsContext.InventoryPacket.updateEquipSlot(betaBefore));


                alpha_copy.setPosition((short) -20000);
                beta_copy.setPosition((short) -20001);

                getPlayer().getInventory(MapleInventoryType.EQUIPPED).addFromDB(alpha_copy);
                getPlayer().getInventory(MapleInventoryType.EQUIPPED).addFromDB(beta_copy);

                if (curLv == 10) {
                    getPlayer().changeSkillLevel(80002632, -1, -1);
                    getPlayer().changeSkillLevel(80002633, -1, -1);
                } else {
                    getPlayer().changeSkillLevel(80002632, 1, 1);
                    getPlayer().changeSkillLevel(80002633, 1, 1);
                }
                getPlayer().updateOneInfo(40981, "save_genesis", "1");
                getPlayer().updateOneInfo(40981, "last_change_time", String.valueOf(System.currentTimeMillis()));
            } else {
                self.sayReplacedNpc("이전 무기로 돌아가는데 많은 힘이 필요해. 아직 시간이 더 필요하니 나중에 다시 시도해줄래?", 2400009, 0, ScriptMessageFlag.NpcReplacedByNpc);
            }
        }
    }

    public void zero_reinvoke_weapon() {
        int curLv = getPlayer().getOneInfoQuestInteger(40981, "lv");

        self.sayReplacedNpc("#face2#우왓, 이거 완전히 가루가 되어버렸잖아? 너, 대체 나한테 무슨 짓을 한 거야? 이제 어떡하냐고? 어떡하긴 뭘 어떡해? 무기가 깨졌으니 모든 게 끝, GAME OVER지! 그동안 즐거웠어. 그럼 안녕……", 2400009, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
        self.sayReplacedNpc("…라고 하면 서운하겠지? 하하, 놀랐어? 하지만 나도 이번엔 많이 놀랐다고. 나, 알고 보면 예민한 남자야. 소중하게 다뤄줘~", 2400009, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
        self.sayReplacedNpc("어차피 무기는 우리를 담고 있는 그릇일 뿐, 우리가 그대로 남아있다면 무기는 언제든지 되살릴 수 있어. 자자, 그럼 시간의 힘을 모아서 이렇게……", 2400009, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);

        getSc().flushSay();

        int alphaID = 1572000 + Math.min(7, curLv);
        int betaID = 1562000 + Math.min(7, curLv);

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Equip alpha_new = (Equip) ii.getEquipById(alphaID);
        Equip beta_new = (Equip) ii.getEquipById(betaID);

        alpha_new.setPosition((short) -11);
        beta_new.setPosition((short) -10);

        getPlayer().getInventory(MapleInventoryType.EQUIPPED).addFromDB(alpha_new);
        getPlayer().getInventory(MapleInventoryType.EQUIPPED).addFromDB(beta_new);

        getPlayer().send(CWvsContext.InventoryPacket.updateEquipSlot(alpha_new));
        getPlayer().send(CWvsContext.InventoryPacket.updateEquipSlot(beta_new));

        self.sayReplacedNpc("쨘! 어때. 감쪽같지? 응? 언제든지 재생이 가능하니까 마음놓고 강화를 해도 되겠다고? 그건…", 2400009, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
        self.sayReplacedNpc("#face2#…어떻게 그런 생각을 할 수 있어?! 무기가 깨질 때 우리도 만만찮게 충격을 받는단 말야! 무기가 깨지는 바람에 얼마나 놀랐는데……", 2400010, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
        self.sayReplacedNpc("그건 라즐리 말이 맞아. 게다가 너무 강력한 아이템은 시간의 힘으로도 되살리는데에 한계가 있어. 앞으로는 조심해!", 2400009, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.NoEsc);
    }

    public void zero_egoequiptalk() {
        int r = Randomizer.rand(1, 34);
        if (r == 1) {
            self.sayReplacedNpc("내가 제일 좋아하는 거…? 으응… 나는 딸기 케이크가 제일 좋아. 나랑 같이 먹고 싶다면... 응, 너라면 같이 먹어도 좋아.",  2400010, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("나도, 나도! 딸기 케이크 나도 좋아해!",  2400009, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("#face0#싫어! 나는 알파랑 둘이… 아, 아니 그럼 난 안 갈래.",  2400010, 0, ScriptMessageFlag.Scenario);
        } else if (r == 2) {
            self.sayReplacedNpc("라피스에 대해서 어떻게 생각하냐고? 음… 덩치만 커다란 바보?",  2400010, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("#face2#너무해, 라즐리. 나 상처받았다고.",  2400009, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("앗, 저기에 엄청 맛있어보이는 고기가!",  2400010, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("고기?! 어디? 어디?",  2400009, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("흥, 역시 바보라니까.",  2400010, 0, ScriptMessageFlag.Scenario);
        } else if (r == 3) {
            self.sayReplacedNpc("#face1#그, 그렇게 빤히 쳐다보면 어쩐지 창피하단 말야.",  2400010, 0, ScriptMessageFlag.Scenario);
        } else if (r == 4) {
            self.sayReplacedNpc("알파를 선택한 이유? 그건… 벼, 별다른 이유 없어. 그냥 아무나 골랐을 뿐이라구. 혹시 네가 마음에 들어서 그렇다는 착각은 하지 말아줬으면 좋겠어!",  2400010, 0, ScriptMessageFlag.Scenario);
        } else if (r == 5) {
            self.sayReplacedNpc("#face1#나는 밤이 싫어. 혼자 있는 것만 같은 기분이 든단 말야. 그래도 이젠 혼자가 아니니까 괜찮아.",  2400010, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("그래그래, 내가 있으니까 걱정하지 말라구.",  2400009, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("#face0#너한테 한 말 아니거든!",  2400010, 0, ScriptMessageFlag.Scenario);
        } else if (r == 6) {
            self.sayReplacedNpc("#face0#딱히 네가 좋아서 같이 있어주는 건 아니야. 그냥 나는 내 의무를 다하고 있는 것 뿐이니까 신경쓰지 말아줬음 좋겠어.",  2400010, 0, ScriptMessageFlag.Scenario);
        } else if (r == 7) {
            self.sayReplacedNpc("알파에 대해서 어떻게 생각하냐고? 어, 어떻게 생각하긴 뭘 어떻게 생각해? 아, 알파 같은 건 조금도 좋아하지 않는다구!",  2400010, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("흐음?",  2400005, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("#face1#그, 그렇다고 싫어한다는 뜻은 아니니까……",  2400010, 0, ScriptMessageFlag.Scenario);
        } else if (r == 8) {
            self.sayReplacedNpc("#face0#내가 화난 것 같다고? 그냥 날씨가 별로라서 그래. 굳이 네가 나에게 오랫동안 말을 걸어주지 않아서 그런 게 아니라구!",  2400010, 0, ScriptMessageFlag.Scenario);
        } else if (r == 9) {
            self.sayReplacedNpc("내가 제일 좋아하는 건… 당연히 고기지! 몸에 좋고 맛도 좋은 고기!",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("나도 고기 좋아해. 맛있어.",  2400006, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("그럼 나랑 고기 먹으러 가자! 내가 완전 맛있게 구워줄게!",  2400009, 0, ScriptMessageFlag.Scenario);
        } else if (r == 10) {
            self.sayReplacedNpc("라즐리에 대해서 어떻게 생각하냐고? 라즐리는… 정말 무서워. 화내면 마녀같다고. 봐, 지금도… 히익!",  2400009, 0, ScriptMessageFlag.Scenario);
        } else if (r == 11) {
            self.sayReplacedNpc("베타를 선택한 이유? 그야 당연히… 예쁘니까! 이왕 주인으로 모실 거면 예쁜 여자가 좋잖아? 하하.",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("내가 예뻐?",  2400006, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("당연하지! 라즐리도 알파가 잘생겨서 선택한 거잖아.",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("#face1#그, 그런 거 아니거든!",  2400010, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
        } else if (r == 12) {
            self.sayReplacedNpc("라즐리 녀석, 사실 너를 꽤나 좋아하는 거 같아. 어떻게 아느냐고? 원래 우리는 하나였으니까 말하지 않아도 다 알지.",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("#face1#쓸데없는 소리 하지마. 그런 거 아니야!",  2400006, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
        } else if (r == 13) {
            self.sayReplacedNpc("나 제법 쓸만한 것 같지 않아? …아니라고 생각하면 어쩔 수 없고.",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("라피스, 힘내…",  2400006, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("#face2#어, 어쩐지 그 말이 더 기운빠지게 만드는 것 같은데.",  2400009, 0, ScriptMessageFlag.Scenario);
        } else if (r == 14) {
            self.sayReplacedNpc("우울한 땐 먹는 게 최고지. 와구와구!",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("그럼 기분이 좋을 땐?",  2400006, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("기분 좋을 때도 먹는 게 최고지. 와구와구!",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("그럼 슬픈 땐?",  2400006, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("슬플 때도 먹는 게……",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("결국 계속 먹는다는 거잖아!",  2400010, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
        } else if (r == 15) {
            self.sayReplacedNpc("무슨 생각을 하고 있냐고? 사실 나는 지금 아주 심각하게… …아무 생각도 없는데?",  2400009, 0, ScriptMessageFlag.Scenario);
        } else if (r == 16) {
            self.sayReplacedNpc("다들 조용하니까 나라도 분위기를 살려야지. 나는 분위기 메이커로써 나름의 사명감을 가지고 있다고! 단순히 심심해서 장난치는 게 아니야. …정말로!",  2400009, 0, ScriptMessageFlag.Scenario);
        } else if (r == 17) {
            self.sayReplacedNpc("보스 몬스터를 잡으면 더 많은 wp를 획득할 수 있어. 물론 잡아야 획득할 수 있지, 잡아야…",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("#face0#1절만 하라고, 1절만! ",  2400005, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
        } else if (r == 18) {
            self.sayReplacedNpc("네가 성장해야 우리도 진화할 수 있어. 그러니까 몸에 좋고 맛도 좋은 고기를 많이 먹어야… 아, 이게 아닌가?",  2400009, 0, ScriptMessageFlag.Scenario);
        } else if (r == 19) {
            self.sayReplacedNpc("주문서나 큐브를 사용하면 대검과 태도에 동시에 적용된다고. 이게 바로 1+1!",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("#face5#오, 그거 괜찮은데?",  2400005, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("대신 파괴도 1+1이지!",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("#face6#……!!!",  2400005, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
        } else if (r == 20) {
            self.sayReplacedNpc("책 읽는 거 좋아해? 나는 책만 펼치면 잠이 오던데… 혹시 신전 책장에 만화책은 없을까?",  2400009, 0, ScriptMessageFlag.Scenario);
        } else if (r == 21) {
            self.sayReplacedNpc("110, 120, 130, 140, 150, 170, 180레벨이 되면 무기 등급을 상승시킬 수 있어. 물론 네가 원하지 않는다면 나중에 선택해도 좋아.",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("뭔가 예전과 미묘하게 다른 것 같은데?",  2400010, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("너야말로 안내를 유도하는 말투인 것 같은데?",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("...",  2400006, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
        } else if (r == 22) {
            self.sayReplacedNpc("잠재능력변경 버튼을 눌러봤어? 어떤 잠재능력이 적용될 지 내 심장이 두근거린다고!",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("그러다가 안 좋은 잠재능력이 나오면 어떡해?",  2400010, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("괜찮아, 나는 매사에 긍정적인 남자니까! 좋은 게 나올 때까지 돌리면 되지!",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("#face7#결국 될대로 되라는 거잖아…",  2400005, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
        } else if (r == 23) {
            self.sayReplacedNpc("신관 중에 델로라는 녀석, 어린데 제법 똑똑하던걸? 전문기술에 관해서 꽤나 해박한 지식을 갖고 있더라고.",  2400009, 0, ScriptMessageFlag.Scenario);
        } else if (r == 24) {
            self.sayReplacedNpc("WEAPON UI에서는 무기만 강화할 수 있어. 여긴 우리만의 공간이니까. 나머지 방어구나 액세서리는 원래처럼 장비창에서 강화하라구. ",  2400010, 0, ScriptMessageFlag.Scenario);
        } else if (r == 25) {
            self.sayReplacedNpc("태그를 이용하면 좀 더 효율적인 전투가 가능해. 구, 굳이 나도 너희와 함께 싸우고 싶어서 그러는 건 아니니까 오해하진 말아줘.",  2400010, 0, ScriptMessageFlag.Scenario);
        } else if (r == 26) {
            self.sayReplacedNpc("럭키아이템 주문서를 사용하면 세트아이템 효과를 낼 수 있다던데. 사실 난 어디에 소속되는 걸 그리 좋아하진 않지만 말야.",  2400010, 0, ScriptMessageFlag.Scenario);
        } else if (r == 27) {
            self.sayReplacedNpc("코인을 모아서 신관들에게 가져가면 좋아할 거야. 가난뱅이들에게는 꽤 가치가 있을 테니까.",  2400010, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("#face11#너 말이 너무 심하다?",  2400005, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("#face2#나, 난 그런 게 아니라…… ",  2400010, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("#face10#하긴, 네 말도 틀린 건 아니지. 내가 가난뱅이인 건 사실이니까.",  2400005, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("#face2#아냐, 그런 게 아니라…… 몰라, 바보!",  2400010, 0, ScriptMessageFlag.Scenario);
        } else if (r == 28) {
            self.sayReplacedNpc("골드라벨 나이트 방어구 세트라고 알아? 거울세계 보스들이 레시피를 가지고 있다던데 구해서 만들어 보는 게 어때? 그럴 리는 없지만 의외로 너한테 어울릴지도 모르니까, 흥.",  2400010, 0, ScriptMessageFlag.Scenario);
        } else if (r == 29) {
            self.sayReplacedNpc("MP포션은 맛없어. MP포션을 먹지 않아도 되는 건 정말 다행이야. ",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("세상에 네가 싫어하는 음식도 있었어?",  2400010, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("#face2#당연하지! 날 대체 뭘로 생각하고 있는 거야?",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("먹을 거 좋아하는 바보.",  2400010, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("#face2#너, 너무 솔직하잖아!",  2400009, 0, ScriptMessageFlag.Scenario);
        } else if (r == 30) {
            self.sayReplacedNpc("우리는 당연히 두손무기지. 두손으로 공손히 모시라는 뜻이야.",  2400010, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("#face7#두손무기가 그런 의미였어?!",  2400005, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
        } else if (r == 31) {
            self.sayReplacedNpc("여신의 눈물을 모으면 새로운 스킬을 배울 수 있어. …하지만 난 걔네랑 별로 같이 다니고 싶진 않단 말야.",  2400010, 0, ScriptMessageFlag.Scenario);
        } else if (r == 32) {
            self.sayReplacedNpc("너희, 닮긴 했지만 그래도 많이 다른걸. 헤어나 성형은 같이 쓸 수도 있지만 한 명씩도 사용 가능하니까 좀 더 개성을 추구하는 것도 나쁘지 않을지도?",  2400010, 0, ScriptMessageFlag.Scenario);
        } else if (r == 33) {
            self.sayReplacedNpc("#face1#엥? 라즐리랑 사이가 나쁘냐고? 아니, 우리 제법 친한 사이라고 생각하는데. 안 그래?",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("난 바보랑은 안 놀아.",  2400010, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("#face0#봐, 나랑 친하다잖아.",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("#face7#전혀 그런 말이 아니잖아.",  2400005, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
        } else if (r == 34) {
            self.sayReplacedNpc("#face1#근데, wp는 거울세계에서만 얻을 수 있다고 내가 말했었나?",  2400009, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("#face7#뭐? 거울세계에서 자유로워져도 얻을 수 없는거야?",  2400005, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("wp는 거울세계에 흩어진 시간의 힘. 거울세계에서만 얻을 수 있는게 당연한 거 아니야? 메이플 월드에서는 군단장급의 보스들만 wp를 가지고 있어.",  2400010, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("그리고 아쉽게도 고기에는 wp가 함유(?)되어 있지 않아.",  2400009, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
            self.sayReplacedNpc("아쉽다...",  2400006, 0, ScriptMessageFlag.Scenario);
            self.sayReplacedNpc("#face7#뭐가 아쉽다는 거야?",  2400005, 0, ScriptMessageFlag.Scenario, ScriptMessageFlag.FlipImage);
        }
    }
}
