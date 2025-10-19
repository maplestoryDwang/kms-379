package script.Profession;

import constants.GameConstants;
import network.models.CField;
import objects.item.MapleInventoryManipulator;
import objects.fields.gameobject.lifes.MapleLifeFactory;
import objects.users.skills.Skill;
import objects.users.skills.SkillEntry;
import objects.users.skills.SkillFactory;
import scripting.ScriptMessageFlag;
import scripting.newscripting.ScriptEngineNPC;

import java.util.*;

public class Profession extends ScriptEngineNPC {

    public void himmel() {
        self.say("전문기술에 대해 궁금하다.. 내가 간단하게 알려주도록 함세. .지금 이 마을에는 #b약초채집, 채광, 장비 제작, 장신구제작, 연금술#k 이렇게 총 5명의 장인이 있네.");
        self.say("채집은 약초 채집과 채광 2가지가 있으며 스타첼과 노붐을 통해 2가지 채집 기술을 모두 배울 수 있네.");
        self.say("제작은 장비 제작, 장신구 제작, 연금술 3가지가 있으며 에이센, 멜츠, 카리엔을 통해 3가지 중 하나만 선택해서 배울 수 있네.");
        self.sayOk("단, 장비 제작과 장신구 제작을 위해서는 채광 기술이 필요하고, 연금술을 배우기 위해서는 채집 기술이 있어야 하지.");
    }

    public void gatherTuto() {
        initNPC(MapleLifeFactory.getNPC(9031008));
        int[] mapList = new int[]{910001005, 910001006, 910001003, 910001004, 910001008, 910001007, 910001010, 910001009};
        StringBuilder text = new StringBuilder("다음 광산과 농장 중에서 어느 곳으로 이동하겠나?\r\n");

        for (int i = 0; i < mapList.length; i++) {
            text.append("#b#L" + i + "##m" + mapList[i] + "##k으로 이동하겠습니다.\r\n");
        }

        int v0 = self.askMenu(text.toString());

        if (v0 >= 0 && v0 < mapList.length) {
/*            int itemID;
            if (v0 > 3) {
                itemID = 4001570 + v0 - 5;
            } else {
                itemID = 4001480 + v0;
            }
            if (getPlayer().haveItem(itemID, 1, false, true)) {
                getPlayer().removeItem(itemID, -1);*/
            getPlayer().timeMoveMap(910001000, mapList[v0], 10 * 60);
//            } else {
            //              self.sayOk("#b#i" + itemID + "##z" + itemID + "##k 아이템이 필요하다네.");
            //        }
        }
    }

    public void herbalism() {
        int skillID = 92000000;
        int v0 = -1;
        if (getPlayer().getProfessionLevel(skillID) <= 0) {
            v0 = self.askMenu("안녕하세요. 무엇을 도와드릴까요?\r\n#L0##b#e약초 채집#n에 대한 설명을 듣는다.#l\r\n#L1##e약초 채집#n을 배운다.#k#l");
        } else {
            v0 = self.askMenu("안녕하세요. 무엇을 도와드릴까요?\r\n#L2##b#e약초 채집#n 레벨을 올린다.#l\r\n#L3##b#t4022023#를 교환한다.#k#l");
        }

        switch (v0) {
            case 0: {
                self.sayOk("약초 채집은 필드 곳곳에 있는 약초를 채집 하는 스킬입니다. 이렇게 채집한 약초를 #p9031007#가 판매하는 오일병에 담아 제련하게 되면 장비,장신구,연금술 등에 필요한 재료가 됩니다.");
                break;
            }
            case 1: {
                if (getPlayer().getQuestStatus(3194) != 2) {
                    self.sayOk("약초채집을 배우려면 #b힘멜#k님을 찾아가서 전문기술에 대한 강의를 들으셔야 해요. 마을 입구에서 힘멜님을 만날 수 있답니다.");
                } else if (getPlayer().getProfessionLevel(skillID) <= 0) {
                    if (1 == self.askYesNo("#b약초 채집#k을 배웁니다. 비용은 #b5000 메소#k입니다. 정말 배우시겠습니까?\r\n#b")) {
                        getPlayer().changeProfessionLevelExp(skillID, 1, 0, (byte) 10);
                        self.sayOk("좋아. 약초채집을 성공적으로 익혔습니다.  숙련도가 다 채워야지만 레벨을 올릴 수 있으니 다시 찾아오세요.");
                    } else {
                        self.sayOk("신중하신 분이군요. 좋아요. 충분히 더 생각해 보시고 다시와 주세요.");
                    }
                }
                break;
            }
            case 2: {
                if (getPlayer().addProfessionLevel(skillID)) {
                    self.sayOk("채집 레벨이 " + getPlayer().getProfessionLevel(skillID) + "이 되었어요. 감성도 추가로 " + ((int) Math.pow(2, getPlayer().getProfessionLevel(skillID))) + "만큼 올려드렸으니 확인해 보세요.");
                } else {
                    self.sayOk("아직 숙련도를 모두 채우진 못하셨네요. 숙련도를 모두 채우지 않으면 레벨업을 할 수 없답니다.");
                }
                break;
            }
            case 3: {
                int itemQty = (int) (getPlayer().getItemQuantity(4022023, false) / 100);
                int number = self.askNumber("#t2028066#를 몇 개를 원하시나요? \r\n#b#t4022023# 100개#k는 #i2028066:##b#t2028066# 1개#k로 교환해 드립니다.", itemQty, 1, itemQty);

                if (MapleInventoryManipulator.checkSpace(getPlayer().getClient(), 2028066, number, "")) {
                    getPlayer().removeItem(4022023, itemQty * -100);
                    getPlayer().gainItem(2028066, number);
                    self.sayOk("#t4022023#는 언제든지 교환해 드립니다.");
                } else {
                    self.sayOk("들고 있는 짐이 너무 많군요? 소비창을 1칸 이상 비운 후 시도해 주세요.");
                }
                break;
            }
        }
    }

    public void mining() {
        int skillID = 92010000;
        int v0 = -1;
        if (getPlayer().getProfessionLevel(skillID) <= 0) {
            v0 = self.askMenu("그래. 채광의 달인인 이 #b노붐#k님에게 원하는것이 무엇인가?\r\n#L0##b#e채광#n에 대한 설명을 듣는다.#l\r\n#L1##e채광#n을 배운다.#k#l");
        } else {
            v0 = self.askMenu("그래. 채광의 달인인 이 #b노붐#k님에게 원하는것이 무엇인가?\r\n#L2##b#e채광#n 레벨을 올린다.#l\r\n#L3##b#t4011010#을 교환한다.#k#l");
        }

        switch (v0) {
            case 0: {
                self.sayOk("채광은 필드 곳곳에 있는  광석을 채집 하는 스킬이야. 이렇게 채집한 원석을 #p9031006#가 판매하는 거푸집에 담아 제련하게 되면 장비, 장신구, 연금술 등에 필요한 재료가 되지.");
                break;
            }
            case 1: {
                if (getPlayer().getQuestStatus(3194) != 2) {
                    self.sayOk("채광을 배우려면 #b힘멜#k님을 찾아가서 인사를 드려야하지~ 마을 입구에서 힘멜님을 만날 수 있어.");
                } else if (getPlayer().getProfessionLevel(skillID) <= 0) {
                    if (1 == self.askYesNo("#b채광#k을 배우게 된다. 정말 이대로 배울거지? 약간의 비용이 드는데 #b5000 메소#k야. 그 정도 돈은 있는거지?.\r\n#b")) {
                        getPlayer().changeProfessionLevelExp(skillID, 1, 0, (byte) 10);
                        self.sayOk("좋아. 채광의 기초 지식을 너에게 전수했어. 숙련도를 채우면 다음 레벨이 되어 새로운 걸 배울 수 있으니 숙련도를 채우면 다시 찾아와 줘.");
                    } else {
                        self.sayOk("신중한 녀석이군. 좋아. 충분히 더 생각해 보시고 다시오도록 해.");
                    }
                }
                break;
            }
            case 2: {
                if (getPlayer().addProfessionLevel(skillID)) {
                    self.sayOk("채광 레벨이 " + getPlayer().getProfessionLevel(skillID) + "이 되었어. 선물로 의지를 " + ((int) Math.pow(2, getPlayer().getProfessionLevel(skillID))) + "만큼 올려주었으니 확인해 보는게 어때?");
                } else {
                    self.sayOk("아직 숙련도를 모두 채우진 못했군. 현재 레벨에서의 숙련도를 끝까지 올리고 다시 오도록 해.");
                }
                break;
            }
            case 3: {
                int itemQty = (int) (getPlayer().getItemQuantity(4011010, false) / 100);
                int number = self.askNumber("#t2028067#를 몇 개를 원하시나요? \r\n#b#t4011010# 100개#k는 #i2028067:##b#t2028067# 1개#k로 교환해 드립니다.", itemQty, 1, itemQty);

                if (MapleInventoryManipulator.checkSpace(getPlayer().getClient(), 2028067, number, "")) {
                    getPlayer().removeItem(4011010, itemQty * -100);
                    getPlayer().gainItem(2028067, number);
                    self.sayOk("#t4011010#은 언제든지 교환해 드립니다.");
                } else {
                    self.sayOk("들고 있는 짐이 너무 많군요? 소비창을 1칸 이상 비운 후 시도해 주세요.");
                }
                break;
            }
        }
    }

    public void equip_product() {
        int skillID = 92020000;
        int parentSkillID = 92010000;
        int v0 = -1;
        if (getPlayer().getProfessionLevel(skillID) <= 0) {
            v0 = self.askMenu("그래. 장비 제작의 달인, 이 #b에이센#k님에게 원하는게 뭐지?\r\n#L0##b#e장비 제작#n에 대한 설명을 듣는다.#l\r\n#L1##e장비 제작#n기술을 배운다.#k#l");
        } else {
            v0 = self.askMenu("그래. 장비 제작의 달인, 이 #b에이센#k님에게 원하는게 뭐지?\r\n#L2##b#e장비 제작#n 레벨을 올린다.#l\r\n#L3#장비 제작 기술을 초기화한다.#k#l");
        }

        switch (v0) {
            case 0: {
                self.sayOk("장비 제작은 채광으로 제련한 광물과 보석을 이 거대한 용광로에 녹여 너에게 필요한 유용한 방어구와 무기를 만드는 기술이야. 지금까지 볼 수 없었던 무기와 방어구도 이 에이센님께 배우면 만들 수 있지.");
                break;
            }
            case 1: {
                if (getPlayer().getQuestStatus(3194) != 2) {
                    self.sayOk("장비 제작에 관심이 있나보군. 배우고 싶다면 먼저 #b힘멜#k님을 찾아가서 인사를 드려야하지~ 마을 입구에서 힘멜님을 만날 수 있을걸세.");
                } else if (getPlayer().getProfessionLevel(parentSkillID) <= 0) {
                    self.sayOk("채광을 배우지 않는 사람에게 장비 제작을 가르쳐주지 않고 있어. 재료가 없다고 끈기 있게 하지 못할테니... 이 옆에 있는 채광의 마스터 #b노붐#k님께 먼저 채광을 배워오게.");
                } else if (getPlayer().getProfessionLevel(skillID) <= 0) {
                    if (1 == self.askYesNo("#b장비 제작#k을 배우고 싶다고? 배우고 싶다면 수강료를 내야지...#b 5000메소#k 인데... 정말 배울건가?\r\n#b")) {
                        getPlayer().changeProfessionLevelExp(skillID, 1, 0, (byte) 10);
                        self.sayOk("좋아. 장비 제작을 성공적으로 익혔어.  숙련도가 다 차게되면 장비 제작의 레벨을 올릴 수 있으니 다시 찾아와 레벨을 올리는 것 잊지말고.");
                    } else {
                        self.sayOk("신중한 녀석이군. 좋아. 충분히 더 생각해 보시고 다시오도록 해.");
                    }
                }
                break;
            }
            case 2: {
                if (getPlayer().addProfessionLevel(skillID)) {
                    if (getPlayer().getProfessionLevel(skillID) == 11) {
                        self.sayOk("좋아. 장비제작 레벨이 장인이 되었다네. 선물로 손재주를 " + ((int) Math.pow(2, getPlayer().getProfessionLevel(skillID))) + "만큼 올려주었으니 한번 확인해 보라고.");
                    } else if (getPlayer().getProfessionLevel(skillID) == 12) {
                        self.sayOk("좋아. 장비제작 레벨이 명장이 되었다네. 선물로 손재주를 " + ((int) Math.pow(2, getPlayer().getProfessionLevel(skillID))) + "만큼 올려주었으니 한번 확인해 보라고.");
                    } else {
                        self.sayOk("좋아. 장비제작 레벨이 " + getPlayer().getProfessionLevel(skillID) + "이 되었다네. 선물로 손재주를 " + ((int) Math.pow(2, getPlayer().getProfessionLevel(skillID))) + "만큼 올려주었으니 한번 확인해 보라고.");
                    }
                } else {
                    self.sayOk("아직 숙련도를 모두 채우진 못했군. 현재 레벨에서의 숙련도를 끝까지 올리고 다시 오도록 해.");
                }
                break;
            }
            case 3: {
                if (1 == self.askYesNo("장비 제작을 배우지 않은 상태로 초기화 한다네. 지금 까지 쌓은 레벨과 숙련도가 모두 사라 질텐데, 정말 초기화를 할건가?")) {
                    getPlayer().changeProfessionLevelExp(skillID, 0, 0, (byte) 10);
                    self.sayOk("장비 제작 기술을 모두 초기화 했다네. 다시 배우고 싶으면 언제든 다시 오라고.");
                } else {
                    self.sayOk("그래. 초기화는 신중하게 충분히 생각해 보고 결정하는 것이 좋지.");
                }
                break;
            }
        }
    }

    public void acc_product() {
        int skillID = 92030000;
        int parentSkillID = 92010000;
        int v0 = -1;
        if (getPlayer().getProfessionLevel(skillID) <= 0) {
            v0 = self.askMenu("우아한 #b멜츠#k님의 고상한 취미 생활은 보석 감상이지. 반짝반짝거리는 보석들을 보고 있노라면 시간 가는 줄 모르겠어. 으흠~ 너도 관심있는거야? 그래보이지 않는걸?\r\n#L0##b#e장신구제작#n에 대한 설명을 듣는다.#l\r\n#L1##e장신구제작#n기술을 배운다.#k#l");
        } else {
            v0 = self.askMenu("우아한 #b멜츠#k님의 고상한 취미 생활은 보석 감상이지. 반짝반짝거리는 보석들을 보고 있노라면 시간 가는 줄 모르겠어. 으흠~ 너도 관심있는거야? 그래보이지 않는걸?\r\n#L2##b#e장신구제작#n 레벨을 올린다.#l\r\n#L3#장신구제작 기술을 초기화한다.#k#l");
        }

        switch (v0) {
            case 0: {
                self.sayOk("장신구제작에 대해서 알려줄려면 우선 보석의 아름다움에 대한 근본적인 것에서부터 출발해야하지만 짧게 이야기하지. 밤새도록 이야기해도 모자르니... \r\n장신구제작은 간단해. 그냥 다듬어지지 않는 보석과 광물을 아름답게 다듬고 장신구로 만들어 원래의 빛을 발하게 해주는거야. 그 과정에서 숨겨진 힘이 발휘되어 나를 더 아름답고 강하게 만들 수 있지.");
                break;
            }
            case 1: {
                if (getPlayer().getQuestStatus(3194) != 2) {
                    self.sayOk("장신구제작을 배우고 싶다구?? 으흠~? 전문기술을 배우고 싶다면 먼저 #b힘멜#k님을 찾아가서 인사를 드려야하지~ 마을 입구쪽에 계시니까 찾아가보겠어?");
                } else if (getPlayer().getProfessionLevel(parentSkillID) <= 0) {
                    self.sayOk("아유~ 어쩌나? 장신구제작을 하고 싶다면 먼저 채광을 배워야 해. 장신구를 제작하기 위해선 각종 금속과 보석이 필요하니까~ 왼쪽으로 가면 오동통한 버섯같이 생긴 #b노붐#k이란 채광의 마스터가 있으니까 가보는게 어때?");
                } else if (getPlayer().getProfessionLevel(skillID) <= 0) {
                    if (1 == self.askYesNo("정말 #b장신구제작#k을 배울거지?..수강료는 #b5000메소#k야.\r\n#b")) {
                        getPlayer().changeProfessionLevelExp(skillID, 1, 0, (byte) 10);
                        self.sayOk("좋아!구우웃!그레이트!!자~ 너에게 나의 장신구제작 지식을 모두 전수했어. 숙련도가 다 차게되면 장신구제작의 레벨을 올릴 수 있으니 다시 찾아와 레벨을 올리는 것 잊지마.");
                    } else {
                        self.sayOk("아닛!!! 장신구제작은 정말 좋은 기술이란 말이야. 더 생각해보겠다니 너무 신중한 것 아냐?");
                    }
                }
                break;
            }
            case 2: {
                if (getPlayer().addProfessionLevel(skillID)) {
                    if (getPlayer().getProfessionLevel(skillID) == 11) {
                        self.sayOk("그레이트! 장신구제작 레벨이 장인이 되었다구! 추가로 손재주를 " + ((int) Math.pow(2, getPlayer().getProfessionLevel(skillID))) + "만큼 올려주었으니 확인해 보는게 어때?");
                    } else if (getPlayer().getProfessionLevel(skillID) == 12) {
                        self.sayOk("그레이트! 장신구제작 레벨이 명장이 되었다구! 추가로 손재주를 " + ((int) Math.pow(2, getPlayer().getProfessionLevel(skillID))) + "만큼 올려주었으니 확인해 보는게 어때?");
                    } else {
                        self.sayOk("그레이트! 장신구제작 레벨이 " + getPlayer().getProfessionLevel(skillID) + "이 되었다구! 추가로 손재주를 " + ((int) Math.pow(2, getPlayer().getProfessionLevel(skillID))) + "만큼 올려주었으니 확인해 보는게 어때?");
                    }
                } else {
                    self.sayOk("아직 숙련도를 모두 채우진 못했군. 현재 레벨에서의 숙련도를 끝까지 올리고 다시 오도록 해.");
                }
                break;
            }
            case 3: {
                if (1 == self.askYesNo("장신구제작을 지우고 싶은거야? 질린거야? 지금까지 네가 힘들게 쌓은 레벨과 숙련도.. 노력과 돈이.. 모두 한순간의 물거품이 될텐데... 정말 초기화 할거야?")) {
                    getPlayer().changeProfessionLevelExp(skillID, 0, 0, (byte) 10);
                    self.sayOk("모두 초기화했어... 냉정하기는. 다시 배우고 싶으면 언제든 찾아와.");
                } else {
                    self.sayOk("그래. 초기화는 신중하게 충분히 생각해 보고 결정해야지.");
                }
                break;
            }
        }
    }

    public void alchemy() {
        int skillID = 92040000;
        int parentSkillID = 92000000;
        int v0 = -1;
        if (getPlayer().getProfessionLevel(skillID) <= 0) {
            v0 = self.askMenu("안녕하세요. 혹시 연금술에 관심있으세요?\r\n#L0##b#e연금술#n에 대한 설명을 듣는다.#l\r\n#L1##e연금술#n을 배운다.#k#l");
        } else {
            v0 = self.askMenu("안녕하세요. 혹시 연금술에 관심있으세요?\r\n#L2##b#e연금술#n 레벨을 올린다.#l\r\n#L3#연금술을 초기화한다.#k#l");
        }

        switch (v0) {
            case 0: {
                self.sayOk("연금술은 허브의 오일을 이용하여 다양한 물약을 만드는 기술이랍니다. HP와 MP를 회복하는 물약부터 당신을 강하게 할 수 있는 다양한 물약도 만들 수 있어요. 지금까지 체험하지 못했던 신기한 물약도 당연히 만들 수 있구요.");
                break;
            }
            case 1: {
                if (getPlayer().getQuestStatus(3194) != 2) {
                    self.sayOk("죄송하지만, 우선 힘멜님께 전문기술에 대한 강의를 들으셔야 알려드릴 수 있답니다. 힘멜님께 가보시겠어요?");
                } else if (getPlayer().getProfessionLevel(parentSkillID) <= 0) {
                    self.sayOk("연금술은 약초채집을 배운 후에 가르쳐 드릴 수 있답니다. 약초채집은 오른쪽으로 가시면 솥에 열심히 약초를 넣고 있는 약초채집의 마스터 #b스타첼#k에게 배울 수 있어요.");
                } else if (getPlayer().getProfessionLevel(skillID) <= 0) {
                    if (1 == self.askYesNo("정말 #b연금술#k을 배우시겠어요? #b5000메소#k의 수강료가 필요하답니다. \r\n#b")) {
                        getPlayer().changeProfessionLevelExp(skillID, 1, 0, (byte) 10);
                        self.sayOk("자아~ 연금술에 대한 기본적인 지식을 알려드렸어요. 그리고 숙련도가 다 차게되면 연금술의 레벨을 올릴 수 있으니 꼭 저에게 다시 찾아와주세요. 새로운 지식을 알려드릴게요.");
                    } else {
                        self.sayOk("전문기술을 배울 때는 원래 신중해야하죠. 그만큼 많은 시간과 노력이 필요하니까요. 충분히 생각해보시고 마음이 결정되면 다시 찾아오세요.");
                    }
                }
                break;
            }
            case 2: {
                if (getPlayer().addProfessionLevel(skillID)) {
                    if (getPlayer().getProfessionLevel(skillID) == 11) {
                        self.sayOk("좋아요. 연금술 레벨이 장인이 되었습니다. 추가로 손재주를 " + ((int) Math.pow(2, getPlayer().getProfessionLevel(skillID))) + "만큼 올려드렸으니 한 번 확인해 보세요.");
                    } else if (getPlayer().getProfessionLevel(skillID) == 12) {
                        self.sayOk("좋아요. 연금술 레벨이 명장이 되었습니다. 추가로 손재주를 " + ((int) Math.pow(2, getPlayer().getProfessionLevel(skillID))) + "만큼 올려드렸으니 한 번 확인해 보세요.");
                    } else {
                        self.sayOk("좋아요. 연금술 레벨이 " + getPlayer().getProfessionLevel(skillID) + "이 되었습니다. 추가로 손재주를 " + ((int) Math.pow(2, getPlayer().getProfessionLevel(skillID))) + "만큼 올려드렸으니 한 번 확인해 보세요.");
                    }
                } else {
                    self.sayOk("아직 숙련도를 모두 채우지 못하신 것 같습니다. 현재 레벨에서의 숙련도를 끝까지 올리고 다시 와 주세요.");
                }
                break;
            }
            case 3: {
                if (1 == self.askYesNo("연금술을 배우지 않은 상태로 초기화 합니다. 초기화를 하게 되면 지금 까지 쌓은 레벨과 숙련도가 모두 사라지게 돼요. 정말 초기화를 하실건가요?")) {
                    getPlayer().changeProfessionLevelExp(skillID, 0, 0, (byte) 10);
                    self.sayOk("연금술 기술을 모두 초기화 했습니다. 다시 배우고 싶으면 언제든 다시 와 주세요.");
                } else {
                    self.sayOk("네. 초기화는 신중하게 충분히 생각해 보고 결정 해주세요.");
                }
                break;
            }
        }
    }

    public void open_herb() {
        int skillID = 92000000;
        open_meister_object(skillID, "약초채집을 배우지 않아 사용할 수 없습니다.");
    }

    public void open_mining() {
        int skillID = 92010000;
        open_meister_object(skillID, "채광을 배우지 않아 사용할 수 없습니다.");
    }

    public void open_equipP() {
        int skillID = 92020000;
        open_meister_object(skillID, "장비 제작을 배우지 않아 사용할 수 없습니다.");
    }

    public void open_accP() {
        int skillID = 92030000;
        open_meister_object(skillID, "장신구제작을 배우지 않아 사용할 수 없습니다.");
    }

    public void open_alchemy() {
        int skillID = 92040000;
        open_meister_object(skillID, "연금술을 배우지 않아 사용할 수 없습니다.");
    }

    private void open_meister_object(int skillID, String context) {
        if (getPlayer().getProfessionLevel(skillID) >= 0) {
            getPlayer().send(CField.UIPacket.openUIOption(0x2A, 0, 0, 0));
        } else {
            getPlayer().dropMessage(5, context);
        }
    }
}
